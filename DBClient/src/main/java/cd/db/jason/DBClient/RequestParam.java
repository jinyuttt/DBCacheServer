/**    
 * 文件名：RequestParam.java    
 *    
 * 版本信息：    
 * 日期：2018年8月14日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBClient;

import cd.jason.db.Client.ClientModel;
import cd.jason.db.Client.DataFormat;
import cd.jason.db.Client.MsgSerialize;
import cd.jason.db.Client.RequestResult;
import cd.strommq.channel.NettyClient;
import cd.strommq.nettyFactory.FactorySocket;

/**    
 *     
 * 项目名称：DBClient    
 * 类名称：RequestParam    
 * 类描述：    客户端请求数据
 * 创建人：jinyu    
 * 创建时间：2018年8月14日 上午12:06:50    
 * 修改人：jinyu    
 * 修改时间：2018年8月14日 上午12:06:50    
 * 修改备注：    
 * @version     
 *     
 */
public class RequestParam {
   
    /**
     * 
    * @Title: requestResult
    * @Description: SQL传输后产生的数据
    * @param @param client
    * @param @return    参数
    * @return RequestResult    返回类型
     */
public RequestResult  requestResult(ClientModel client)
{
    RequestResult result=null;
    NettyClient netClient = FactorySocket.createClient(ClientConfig.netType);
    boolean r=netClient.connect(ClientConfig.srvIP, ClientConfig.srvPort);
    if(r)
    {
        byte[] data=null;
        data=MsgSerialize.Serialize(client);
        netClient.send(data);
        data=netClient.recvice();
        if(client.resultFormat==DataFormat.datamodel)
        {
          result=MsgSerialize.DeSerialize(data, RequestResult.class);
        }
        else
        {
            result=MsgSerialize.JSONByteDeSerialize(data, RequestResult.class);
        }
    }
    return result;
}
}
