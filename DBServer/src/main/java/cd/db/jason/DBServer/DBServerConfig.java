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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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
public static String ServerName="DBCacheSrv";
public int port=6000;//端口
public String logConfig="config/log4j2.xml";
public String sqlConfig="SQLConfig";
public String dbTypeConfig="config/dbType.properties";
public String dbType="ora";//默认主数据库配置文件
private String confFile="config/config.properties";
public  long cacheSize=10000;//内存缓存大小
public int cacheTime=60;//内存缓存时间
public HashMap<String,String> db=new HashMap<String,String>();
public boolean isRedis=false;//redis扩展
public String redisSrv="";//reids地址
public boolean iscluster=false;//是否集群部署
public String clusterAddress="";//集群地址
public String localIP="";//本节点IP
public int ttl=10;//心跳时间
public String localNodeID="";//本节点唯一ID
public String srvNetType="tcp";//通信类型
public String getConfigFile()
{
    return confFile;
}
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
     String db=properties.getProperty("dbConfig", "config/dbType.properties");
     String redisUse=properties.getProperty("redisUse", "false");
     String redisAddr=properties.getProperty("redisSrv", "");
     String isCluster=properties.getProperty("cluster_discovery", "false");
     String ClusterAddr=properties.getProperty("cluster_discovery_address", "");
     String ip=properties.getProperty("ip", "127.0.0.1");
     String updatettl=properties.getProperty("ttl", "10");
     String localID=properties.getProperty("localnodeid", "");
     this.sqlConfig=srvSqls;
     this.logConfig=logConf;
     this.dbType=srvDBType;
     this.port=Integer.valueOf(srvPort);
     this.cacheTime=Integer.valueOf(cacheMaxTime);
     this.cacheSize=Long.valueOf(cacheMaxSize);
     this.dbTypeConfig=db;
     this.redisSrv=redisAddr;
     this.isRedis=Boolean.valueOf(redisUse);
     this.iscluster=Boolean.valueOf(isCluster);
     this.clusterAddress=ClusterAddr;
     this.localIP=ip;
     this.ttl=Integer.valueOf(updatettl);
     this.localNodeID=localID;
     if(localID==null||localID.trim().isEmpty())
     {
         this.localNodeID=ip+":"+port;
     }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    loaddbconfig();
}
private void loaddbconfig()
{
    Properties properties = new Properties();
    try
    {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(dbTypeConfig));
    properties.load(bufferedReader);
   Iterator<Entry<Object, Object>> iter = properties.entrySet().iterator();
    while(iter.hasNext())
    {
        Entry<Object, Object> item = iter.next();
        db.put(item.getKey().toString(), item.getValue().toString());
    }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
}
}
