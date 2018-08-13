/**    
 * 文件名：DBServerConfig.java    
 *    
 * 版本信息：    
 * 日期：2018年8月8日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DBServerConfig    
 * 类描述：   配置读取
 * 创建人：jinyu    
 * 创建时间：2018年8月8日 上午2:01:11    
 * 修改人：jinyu    
 * 修改时间：2018年8月8日 上午2:01:11    
 * 修改备注：    
 * @version     
 *     
 */
public class DBServerConfig {
public int port=6000;
public String logConfig="config/log4j2.xml";
public String sqlConfig="SQLConfig";
public String dbType="ora";
private String confFile="config/config.properties";
public  long cacheSize=10000;
public int cacheTime=60;
public void loadConfig()
{
   File conf=new File(confFile);
   if(!conf.exists())
   {
       return;
   }
    Properties properties = new Properties();
     // 使用InPutStream流读取properties文件
    try
    {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(confFile));
    properties.load(bufferedReader);
    // 获取key对应的value值
     String srvPort=properties.getProperty("port", "5000");
     String logConf=properties.getProperty("logconf", "config/log4j2.xml");
     String srvSqls=properties.getProperty("sqlconfig", "SQLConfig");
     String srvDBType=properties.getProperty("dbType", "psql");
     String cacheMaxSize=properties.getProperty("cacheSize", "10000");
     String cacheMaxTime=properties.getProperty("cacheTime", "60");
     this.sqlConfig=srvSqls;
     this.logConfig=logConf;
     this.dbType=srvDBType;
     this.port=Integer.valueOf(srvPort);
     this.cacheTime=Integer.valueOf(cacheMaxTime);
     this.cacheSize=Long.valueOf(cacheMaxSize);
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
}
}
