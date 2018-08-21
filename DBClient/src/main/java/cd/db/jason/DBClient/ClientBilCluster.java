/**    
 * 文件名：ClientCluster.java    
 *    
 * 版本信息：    
 * 日期：2018年8月19日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBClient;

import java.util.List;

import cd.jason.BilSrv.ClientCluster;
import cd.jason.BilSrv.SrvAddress;

/**    
 *     
 * 项目名称：DBClient    
 * 类名称：ClientCluster    
 * 类描述：    获取集群服务地址
 * 创建人：SYSTEM    
 * 创建时间：2018年8月19日 上午10:25:12    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月19日 上午10:25:12    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientBilCluster {
    private static class Sington
    {
        private  static  ClientBilCluster instance=new ClientBilCluster();
    }
    
    /**
     * 
    * @Title: getInstance
    * @Description: 单例
    * @return    参数
    * @return ClientCluster    返回类型
     */
public static ClientBilCluster getInstance()
{
    return Sington.instance;
}
    private int forceDay;
    private String SYS_NAME="DBCacheSrv";
    private static String updateCmd="updateconf";
    private  volatile boolean isInit=true;
/**
 * 
* @Title: setEtcdNode
* @Description: 设置etcd注册节点地址
* @param nodeAddr    参数
* @return void    返回类型
 */
public void setEtcdNode(String nodeAddr)
{
    ClientCluster.getInstance().setEtcdNode(nodeAddr);
}

/**
 * 
* @Title: setServerName
* @Description:设置系统服务名称
* @param name    参数
* @return void    返回类型
 */
public void setServerName(String name)
{
    SYS_NAME=name;
    ClientCluster.getInstance().setServerName(name);
    
}

/**
 * 
* @Title: setCheckCluster
* @Description: 设置是否需要动态监测etcd节点，默认是
* @param isAllow    参数
* @return void    返回类型
 */
public  void setCheckCluster(boolean isAllow)
{
    ClientCluster.getInstance().setCheckCluster(isAllow);
}
/**
 * 
* @Title: setEtcdCheck
* @Description: 设置检查机集群客户端的频率(s)及强制更新天数(day)
* @param waitTime
* @param checkDay    参数
* @return void    返回类型
 */
public void setEtcdCheck(long waitTime,int checkDay)
{
    this.forceDay=checkDay;
    if(forceDay<1)
    {
        //视为不监测客户端
        forceDay=1;
    }
    ClientCluster.getInstance().setEtcdCheck(waitTime, checkDay);
}
/**
 * 
* @Title: isHaveUpdateFile
* @Description: 是否有上传的文件更新过
* @return    参数
* @return boolean    返回类型
 */
public boolean isHaveUpdateFile()
{
    return ClientCluster.getInstance().isHaveUpdateFile();
}

/**
 * 
* @Title: lastUpdateFiles
* @Description: 最近一次更新的文件名称，包括路径
* @return    参数
* @return List<String>    返回类型
 */
public List<String> lastUpdateFiles()
{
    return ClientCluster.getInstance().lastUpdateFiles();
}

/**
 * 
* @Title: initClient
* @Description: 初始化服务集群
* @return void    返回类型
 */
public void initClient()
{
   if(isInit)
   {
    ClientCluster.getInstance().setServerName(SYS_NAME);
    ClientCluster.getInstance().initClient();
    ClientCluster.updateCmd=ClientBilCluster.updateCmd;
     isInit=false;
   }
}


/**
 * 
* @Title: getSrvAddress
* @Description: 获取访问的服务地址
* @return    参数
* @return SrvAddress    返回类型
 */
public  SrvAddress getSrvAddress()
{
    if(isInit)
    {
        this.setEtcdNode(ClientConfig.Client_Cluster_Addr);
        this.setCheckCluster(ClientConfig.ischeckCluster);
        this.setEtcdCheck(ClientConfig.checkTimeLen, 7);
        this.initClient();
    }
    return ClientCluster.getInstance().getSrvAddress();
}
}
