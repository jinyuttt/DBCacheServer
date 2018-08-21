/**
 * 
 */
package cd.db.jason.DBAcess;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jinyu
 * 管理连接
 */
public class PoolManager {
    private static class Single
    {
        private final static   PoolManager dBService=new PoolManager();
    }
    public static PoolManager getInstance(){
        return Single.dBService;
    }
    private  ConcurrentHashMap<String,HikariDataSource> map=new ConcurrentHashMap<String,HikariDataSource>();
    public String applocaltion="";
    // 调用SQL      
    PreparedStatement pst = null;
    //连接
    private ConcurrentHashMap<String, Connection> mapCon=new ConcurrentHashMap<String, Connection>();
   
    private ReentrantLock lock_obj=new ReentrantLock();
    private PoolManager()
    {
        URL url = PoolManager.class.getProtectionDomain().getCodeSource().getLocation();
        if(url==null)
        {
            url=ClassLoader.getSystemClassLoader().getResource("");
        }
        String filePath = null;  
        try {  
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"  
            // 截取路径中的jar包名  
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);  
        }  
          
        File file = new File(filePath);  
        applocaltion = file.getAbsolutePath();//得到windows下的正确路径 
    }
    
    /**
      * 初始化配置连接
     * @param name
     */
 private  HikariDataSource initConfig(String name)
 {
     System.out.println("读取DB配置路径："+applocaltion);
     StringBuffer buf=new StringBuffer();
     buf.append(applocaltion);
     buf.append("/dbconfig/");
     buf.append(name);
     buf.append("_dbpool.properties");
     File conf=new File(buf.toString());
     if(!conf.exists())
     {
         //继续处理路径，防止插件路径
         //构造函数已经采用加载器路径
         System.out.println("没有文件："+conf.getAbsolutePath());
         return null;
     }
     else
     {
         System.out.println("读取DB配置："+buf.toString());
     }
     //多次线程发送替换也没有关系;连接池最后都会销毁
     lock_obj.lock();//同步锁阻塞方法内
     //判断一次
     HikariDataSource dataSource=null;
     dataSource=map.getOrDefault(name, null);//再次取出，没有才加入
     if(dataSource==null)
     {
       HikariConfig config = new HikariConfig(buf.toString());
       dataSource= new HikariDataSource(config);
       map.put(name, dataSource);
     }
     lock_obj.unlock();
     return dataSource;
 }

 
 /**
  * 获取线程ID
  * @return
  */
 private String getThreadId()
 {
     return String.valueOf(Thread.currentThread().getId());
 }
 /**
  * 获取连接
  * @param dbName db
  * @return
  *
  */
public Connection getConnection(String dbName)  {
    try {
        Connection con=null;
        HikariDataSource   dataSource=map.getOrDefault(dbName, null);
        if(dataSource==null)
        {
            //同步方法
            dataSource=initConfig(dbName);
        }
        if(dataSource!=null)
              con= dataSource.getConnection();
        return con;
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }    
   }

/**
 * 关闭数据库
 */
public void closeCurDB()
{
    Connection con = mapCon.getOrDefault(this.getThreadId(), null);
    if(con!=null)
    {
        try {
            con.close();
        } catch (SQLException e) {
         
            e.printStackTrace();
        }
    }
}
/**
 *  停止
 *  关闭所有连接
 */
public void stop()  {
    lock_obj.lock();
    for(HikariDataSource v:map.values())
    {
        v.close();
    }
    map.clear();
    lock_obj.unlock();
}


}
