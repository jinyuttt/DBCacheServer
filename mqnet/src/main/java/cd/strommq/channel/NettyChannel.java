/**    
 * 文件名：NettyChannel.java    
 *    
 * 版本信息：    
 * 日期：2018年7月29日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.strommq.channel;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.channel.Channel;

/**    
 *     
 * 项目名称：mqnet    
 * 类名称：NettyChannel    
 * 类描述：    服务端数据接收
 * 创建人：jinyu    
 * 创建时间：2018年7月29日 上午4:08:18    
 * 修改人：jinyu    
 * 修改时间：2018年7月29日 上午4:08:18    
 * 修改备注：    
 * @version     
 *     
 */
public class NettyChannel {
private ConcurrentHashMap<String,Channel> group=null;
private LinkedBlockingQueue<NettyRspData> queue=null;
public NettyChannel()
{
    group=new ConcurrentHashMap<String,Channel>();
    queue=new LinkedBlockingQueue<NettyRspData>();
}

/**
 * 
 * @Title: addChannel   
 * @Description: 保持TCP连接的CHANEL  
 * @param ch      
 * void      
 * @throws
 */
public void addChannel(Channel ch)
{
    group.put(ch.id().asLongText(),ch);
    
}

/**
 * 
 * @Title: removeChenel   
 * @Description: 移除TCP连接的CHANEL   
 * @param ch      
 * void      
 * @throws
 */
public void removeChenel(Channel ch)
{
    group.remove(ch.id().asLongText());
}

/**
 * 
 * @Title: getData   
 * @Description: 获取数据及CHANEL
 * @return
 * @throws InterruptedException      
 * NettyData      
 * @throws
 */
public NettyRspData getData() throws InterruptedException
{
    return queue.take();
}

/**
 * 
 * @Title: getData   
 * @Description: 超时获取
 * @param timeout
 * @return
 * @throws InterruptedException    
 * NettyRspData
 */
public NettyRspData getData(long timeout) throws InterruptedException
{
    return queue.poll(timeout, TimeUnit.MILLISECONDS);
}
/**
 * 
 * @Title: add   
 * @Description: 添加数据及CHANEL，UDP时有远端地址 
 * @param data
 * @param ch
 * @param client      
 * void      
 * @throws
 */
 public void add(Object data,Channel ch,InetSocketAddress client)
{
    NettyRspData e=new NettyRspData();
    e.chanel=ch;
    e.client=client;
    e.data=data;
    try {
        queue.put(e);
    } catch (InterruptedException e1) {
     
        e1.printStackTrace();
    }
}
}
