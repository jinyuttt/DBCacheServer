/**    
 * 文件名：FactoryConnection.java    
 *    
 * 版本信息：    
 * 日期：2018年8月4日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBAcess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**    
 *     
 * 项目名称：DBAcess    
 * 类名称：FactoryConnection    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月4日 下午7:58:32    
 * 修改人：jinyu    
 * 修改时间：2018年8月4日 下午7:58:32    
 * 修改备注：    
 * @version     
 *     
 */
public class FactoryConnection {
    private static class Single
    {
        private final static   FactoryConnection instance=new FactoryConnection();
    }
    public  FactoryConnection getInstance()
    {
        return Single.instance;
    }
    private  ReentrantReadWriteLock obj_lock=new ReentrantReadWriteLock();
    HashMap<String,HikariDataSource> map=new HashMap<String,HikariDataSource>();
    
    /**
     * 
     * @Title: initlocal   
     * @Description: 加载配置  
     * @param name    
     * void
     */
    private HikariDataSource initlocal(String name)
    {
        obj_lock.writeLock().lock();
        String file="DBConfig/"+name+"_pool.properties";
        HikariConfig config = new HikariConfig(file);
        HikariDataSource ds = new HikariDataSource(config);
        map.put(name, ds);
        obj_lock.writeLock().unlock();
        return ds;
    }
    
    /**
     * 
     * @Title: getConnection   
     * @Description: 获取连接
     * @param name
     * @return    
     * Connection
     */
public Connection getConnection(String name)
{
    Connection conn=null;
    HikariDataSource ds=map.getOrDefault(name, null);
    if(ds==null)
    {
        obj_lock.readLock().lock();
       ds= initlocal(name);
       obj_lock.readLock().unlock();
    }
    if(ds!=null)
    {
        try {
            conn=ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   return conn;
}

}
