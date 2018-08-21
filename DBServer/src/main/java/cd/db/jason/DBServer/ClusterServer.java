/**    
 * 文件名：RegisterClusterServer.java    
 *    
 * 版本信息：    
 * 日期：2018年8月19日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.List;

import cd.jason.BilSrv.ClientCluster;
import cd.jason.BilSrv.RegisterClusterServer;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：RegisterClusterServer    
 * 类描述：   服务集群部署注册
 * 创建人：SYSTEM    
 * 创建时间：2018年8月19日 上午8:17:27    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月19日 上午8:17:27    
 * 修改备注：    
 * @version     
 *     
 */
public class ClusterServer {
    RegisterClusterServer server=new RegisterClusterServer();
    private long waitTime=3600000;//1小时监测更新一次集群节点
    private int checkDay=7;//7天强制更新一次集群节点
    private static class Sington
    {
        private  static  ClusterServer instance=new ClusterServer();
    }
    /**
     * 
    * @Title: getInstance
    * @Description: 单例
    * @return    参数
    * @return ClientCluster    返回类型
     */
public static ClusterServer getInstance()
{
    return Sington.instance;
}

/**
 * 
* @Title: setConfigUp
* @Description: 设置配置目录或文件
* @param config    参数
* @return void    返回类型
 */
public void setConfigUp(String config)
{
    server.setConfigUp(config);
    ClientCluster.getInstance().setUpdateFile(true);
   
}

/**
 * 
* @Title: setServerName
* @Description: 设置服务名称
* @param name    参数
* @return void    返回类型
 */
public void setServerName(String name)
{
    server.setServerName(name);
    ClientCluster.getInstance().setServerName(name);
}

/**
 * 
* @Title: init
* @Description: 初始化客户端应用，使用其中的集群更新功能及文件功能
* @return void    返回类型
 */
private void init()
{
    ClientCluster.getInstance().setCheckCluster(true);
    ClientCluster.getInstance().setEtcdCheck(waitTime, checkDay);
   // ClientCluster.getInstance().setEtcdNode(nodeAddr);
}

/**
 * 
* @Title: RegisterNode
* @Description: 注册
* @param noedeName
* @param localSrvAddr
* @param registerAddress
* @param ttl    参数
* @return void    返回类型
 */
public void RegisterNode(String noedeName,String localSrvAddr,String registerAddress,int ttl)
{
    init();
    server.registerNode(noedeName, localSrvAddr, registerAddress, ttl);
}

/**
 * 
* @Title: getUpFiles
* @Description: 获取最新更新文件
* @return    参数
* @return List<String>    返回类型
 */
public List<String> getUpFiles()
{
    return ClientCluster.getInstance().lastUpdateFiles();
}
}
