/**    
 * 文件名：Cluster.java    
 *    
 * 版本信息：    
 * 日期：2018年8月19日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBClient;

import cd.jason.BilSrv.SrvAddress;
import cd.jason.db.Client.ClientModel;
import cd.jason.db.Client.DataFormat;
import cd.jason.db.Client.MsgSerialize;
import cd.jason.db.Client.RequestResult;
import cd.strommq.channel.NettyClient;
import cd.strommq.nettyFactory.FactorySocket;

/**    
 *     
 * 项目名称：DBClient    
 * 类名称：Cluster    
 * 类描述：    通过集群组方式访问
 * 创建人：SYSTEM    
 * 创建时间：2018年8月19日 上午10:51:07    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月19日 上午10:51:07    
 * 修改备注：    
 * @version     
 *     
 */
public class ClusterRequestParam {
    public RequestResult  requestResult(ClientModel client)
    {
        RequestResult result=null;
        SrvAddress srvAddr = ClientBilCluster.getInstance().getSrvAddress();
        NettyClient netClient = FactorySocket.createClient(srvAddr.netType);
        boolean r=netClient.connect(srvAddr.srvIP, srvAddr.srvPort);
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
