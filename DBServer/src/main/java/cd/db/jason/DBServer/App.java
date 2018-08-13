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
        //
        LogFactory.appPath=System.getProperty("user.dir");
        LogFactory.confPath=conf.logConfig;
        //
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
        PoolManager.getInstance().applocaltion=LogFactory.appPath;
        //
        BusDictionary.getInstance().setDBType(conf.db);
        BusDictionary.getInstance().defaultDB=conf.dbType;
        XMLRead.dir=conf.sqlConfig;
        SQLConfig.getInstance().read();//读取SQL
        //
        DBServer server=new DBServer();
        server.start(conf.port);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
        
            e1.printStackTrace();
        }
//        ClientModel model=new ClientModel();
//        model.strSQL="select * from student where id=?";
//        model.addParam("id", 2);
//        model.userName="1";
//        server.startThread(model);
        try {
            System.in.read();
        } catch (IOException e) {
        
            e.printStackTrace();
        }
    }
}
