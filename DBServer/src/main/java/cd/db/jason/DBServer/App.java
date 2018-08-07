package cd.db.jason.DBServer;

import java.io.IOException;

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
        LogFactory.appPath=System.getProperty("user.dir");
        LogFactory.confPath=conf.logConfig;
        XMLRead.dir=conf.sqlConfig;
        SQLConfig.getInstance().read();//读取SQL
        DBServer server=new DBServer();
        server.dbType=conf.dbType;
        server.start(conf.port);
        try {
            System.in.read();
        } catch (IOException e) {
        
            e.printStackTrace();
        }
    }
}
