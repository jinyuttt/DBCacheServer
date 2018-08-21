package cd.db.jason.DBServer;

import java.io.IOException;

import cd.db.jason.DBAcess.PoolManager;
import cd.db.jason.cache.DBCache;
import cd.db.jason.cache.RedisClient;
import cd.strommq.log.LogFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
     
        DBServerConfig conf=new DBServerConfig();
        conf.loadConfig();
        //日志配置
        LogFactory.appPath=System.getProperty("user.dir");
        LogFactory.confPath=conf.logConfig;
        //缓存配置
        DBCache.getInstance().maxCacheSize=conf.cacheSize;
        DBCache.getInstance().maxCacheTime=conf.cacheTime;
        DBCache.getInstance().init();
        RedisClient.isUse=conf.isRedis;
        if(conf.isRedis)
        {
            //启用设置，采用的集群方式
            RedisClient.setRedidAddress(conf.redisSrv);
        }
        //
        if(conf.iscluster)
        {
            String config=conf.sqlConfig+";"+conf.dbTypeConfig+";"+conf.logConfig+";"+"dbconfig";
            //采用集群部署方案
            ClusterServer.getInstance().setConfigUp(config);
            ClusterServer.getInstance().setServerName(DBServerConfig.ServerName);
            String localSrvAddr=conf.srvNetType+"://"+conf.localIP+":"+conf.port;
            ClusterServer.getInstance().RegisterNode(conf.localNodeID, localSrvAddr, conf.clusterAddress, conf.ttl);
            ReloadConfig.getInstance().start();
        }
        //数据库设置路径
        PoolManager.getInstance().applocaltion=LogFactory.appPath;
        //
        BusDictionary.getInstance().setDBType(conf.db);
        BusDictionary.getInstance().defaultDB=conf.dbType;
        //
        XMLRead.dir=conf.sqlConfig;
        SQLConfig.getInstance().read();//读取SQL
        //
       // DBServer server=new DBServer();
        DBServer.getInstance().start(conf.port);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
        
            e1.printStackTrace();
        }
        try {
            System.in.read();
        } catch (IOException e) {
        
            e.printStackTrace();
        }
    }
}
