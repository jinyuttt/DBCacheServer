/**    
 * 文件名：RedLoadConfig.java    
 *    
 * 版本信息：    
 * 日期：2018年8月19日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.List;

import cd.db.jason.DBAcess.PoolManager;
import cd.strommq.log.LogFactory;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：ReloadConfig    
 * 类描述：如果启用了集群部署配置文件    
 * 创建人：SYSTEM    
 * 创建时间：2018年8月19日 上午9:10:38    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月19日 上午9:10:38    
 * 修改备注：    
 * @version     
 *     
 */
public class ReloadConfig {
    private static class Sington
    {
        private static ReloadConfig instance=new ReloadConfig();
    }
    public static ReloadConfig getInstance()
    {
       return  Sington.instance;
    }
    /**
     * 
    * @Title: start
    * @Description: 开启文件重置
    * @return void    返回类型
     */
public void start()
{
    Thread reload=new Thread(new Runnable() {

        @Override
        public void run() {
           
            DBServerConfig conf=new DBServerConfig();
            List<String> lst= ClusterServer.getInstance().getUpFiles();
            if(!lst.isEmpty())
            {
                int size=lst.size();
                for(int i=0;i<size;i++)
                {
                   String file= lst.get(i);
                   //这里只传输了目录；
                   if(file.startsWith("SQLConfig"))
                   {
                       //SQL文件配置
                      SQLConfig.getInstance().readSQLFile(file);
                   }
                   else if(file.startsWith("DBConfig")||conf.dbTypeConfig.equals(file))
                   {
                       //重新更新；数据库配置
                       PoolManager.getInstance().stop();
                   }
                   else if(file.equals(conf.logConfig))
                   {
                       //日志配置
                       LogFactory.getInstance().initLog();
                   }
                   else if(file.endsWith("dbbase.xml"))
                   {
                       //基础配置SQL修改
                       DBServer.getInstance().init();
                   }
                }
            }
           
        }
        
    });
    reload.setDaemon(true);
    reload.setName("加载配置文件");
    reload.start();
}
}
