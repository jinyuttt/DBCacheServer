/**    
 * 文件名：ClientClusterProxy.java    
 *    
 * 版本信息：    
 * 日期：2018年8月19日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.jason.etcd.proxy;


import cd.jason.BilSrv.ClientCluster;
import cd.jason.BilSrv.SrvAddress;

/**    
 *     
 * 项目名称：proxy    
 * 类名称：ClientClusterProxy    
 * 类描述：    
 * 创建人：SYSTEM    
 * 创建时间：2018年8月19日 上午9:10:22    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月19日 上午9:10:22    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientClusterProxy {
    private static class Sington
    {
        private  static  ClientClusterProxy instance=new ClientClusterProxy();
    }
    
    /**
     * 
    * @Title: getInstance
    * @Description: 单例
    * @return    参数
    * @return ClientCluster    返回类型
     */
public static ClientClusterProxy getInstance()
{
    return Sington.instance;
}

/**
 * 
* @Title: init
* @Description: 初始化信息
* @return void    返回类型
 */
public void init()
{
    ClientCluster.getInstance().setCheckCluster(ClusterProxyConfig.ischeckCluster);
    ClientCluster.getInstance().setEtcdCheck(ClusterProxyConfig.checkTimeLen, ClusterProxyConfig.forceUpdateday);
    ClientCluster.getInstance().setEtcdNode(ClusterProxyConfig.Client_Cluster_Addr);
    ClientCluster.getInstance().setServerName(ClusterProxyConfig.srvName);
    ClientCluster.getInstance().setUpdateFile(true);
}


/**
 * 
* @Title: getProxyData
* @Description: 返回服务地址
* @return    参数
* @return SrvAddress    返回类型
 */
public SrvAddress getProxyData()
{
    return ClientCluster.getInstance().getSrvAddress();
}

}
