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
    private ConcurrentHashMap<String, Connection> mapCon=new ConcurrentHashMap<String, Connection>();
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
 private synchronized  HikariDataSource initConfig(String name)
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
         System.out.println("没有文件："+conf.getAbsolutePath());
         //继续处理路径，防止插件路径
         URL url =null;
         url=ClassLoader.getSystemClassLoader().getResource("");
         String path = null;  
         try {  
             path = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
         if(applocaltion==path)
         {
             return null;
         }
         else
         {
             applocaltion=path;
         }
          return initConfig(name);
     }
     else
     {
         System.out.println("读取DB配置："+buf.toString());
     }
     //替换了也没有关系;连接池最后都会销毁
     HikariConfig config = new HikariConfig(buf.toString());
     HikariDataSource dataSource= new HikariDataSource(config);
     map.put(name, dataSource);
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
   
    for(HikariDataSource v:map.values())
    {
        v.close();
    }
    map.clear();
}


}
