/**    
 * 文件名：RegisterServer.java    
 *    
 * 版本信息：    
 * 日期：2018年8月18日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.cluster_discovery;

import cd.jason.BilSrv.RegisterClusterServer;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：RegisterServer    
 * 类描述：    
 * 创建人：SYSTEM    
 * 创建时间：2018年8月18日 下午9:38:34    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月18日 下午9:38:34    
 * 修改备注：    
 * @version     
 *     
 */
public class RegisterServer {
    private String SYS_NAME="DBCacheSrv";
    RegisterClusterServer registerServer=new RegisterClusterServer();

    
    /**
     * 
    * @Title: setConfig
    * @Description: 需要上传的配置文件或者目录
    * @param conf    参数
    * @return void    返回类型
     */
    public void setConfig(String conf)
    {
        registerServer.setConfigUp(conf);
    }
    
    /**
     * 
    * @Title: setSYS_Name
    * @Description: 设置服务系统名称
    * @param name    参数
    * @return void    返回类型
     */
    public void setSYS_Name(String name)
    {
        registerServer.setServerName(name);
    }
    /**
     * 
    * @Title: registerNode
    * @Description: 注册服务
    * @param  noedeName
    * @param  localSrvAddr
    * @param  registerAddress
    * @param  ttl    参数
    * @return void    返回类型
     */
public void registerNode(String noedeName,String localSrvAddr,String registerAddress,int ttl)
{
    registerServer.setServerName(SYS_NAME);
    registerServer.registerNode(noedeName, localSrvAddr, registerAddress, ttl);
   
}


}
