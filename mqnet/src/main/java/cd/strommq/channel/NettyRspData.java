/**    
 * 文件名：NettyServer.java    
 *    
 * 版本信息：    
 * 日期：2018年7月29日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.strommq.channel;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**    
 *     
 * 项目名称：mqnet    
 * 类名称：NettyServer    
 * 类描述：    数据接收及回执
 * 创建人：jinyu    
 * 创建时间：2018年7月29日 上午10:22:31    
 * 修改人：jinyu    
 * 修改时间：2018年7月29日 上午10:22:31    
 * 修改备注：    
 * @version     
 *     
 */
public class NettyRspData {

/**
 * ID号，固有
 */
public long id;

/**
 * 接收的数据，一般是byte[]，除非有自己格式
 */
public Object data;

/**
 * UDP通信回执地址，如果是TCP则为null
 */
public InetSocketAddress client;

/**
 * 回执发送的通道
 */
public Channel chanel;

/**
 * 
 * @Title: setRsp   
 * @Description: 回执数据
 * @param data      
 * void      
 * @throws
 */
public void setRsp(byte[]data)
{
    ByteBuf buf=Unpooled.copiedBuffer(data);
    if(client!=null)
    {
        System.out.println("udp回执："+client.getHostName()+","+client.getPort());
       chanel.writeAndFlush(new DatagramPacket(buf,client));
    }
    else
    {
        chanel.writeAndFlush(buf);
    }
   // ReferenceCountUtil.release(buf);
}

/**
 * 
 * @Title: setRsp   
 * @Description: 回执数据  
 * @param msg      
 * void      
 * @throws
 */
public void setRsp(String msg)
{
   byte[] data=msg.getBytes(CharsetUtil.UTF_8);
   this.setRsp(data);

}

/**
 * 
 * @Title: setRsp   
 * @Description: 回执数据  
 * @param msg      
 * void      
 * @throws
 */
public void setRsp(byte[]flage,String msg)
{
   byte[] data=msg.getBytes(CharsetUtil.UTF_8);
   if(flage==null||flage.length==0)
   {
       this.setRsp(data);
   }
   else
   {
       byte[]tmp=new byte[flage.length+data.length];
       System.arraycopy(flage, 0, tmp, 0, flage.length);
       System.arraycopy(data, 0, tmp, flage.length, data.length);
       this.setRsp(tmp); 
   }

}


/**
 * 
 * @Title: setRsp   
 * @Description: 回执数据   
 * @param datas      
 * void      
 * @throws
 */
public void setRsp(List<byte[]> datas)
{
    if(datas==null||datas.isEmpty())
    {
        return;
    }
    int size=datas.size();
    for(int i=0;i<size;i++)
    {
        this.setRsp(datas.get(i));
    }
}

/**
 * 
 * @Title: setRsp   
 * @Description: 数据回执  
 * @param flage 添加标识
 * @param data  真实数据
 * void      
 * @throws
 */
public void setRsp(byte[]flage,List<byte[]> data)
{
    
    if(data==null||data.isEmpty())
    {
        return;
    }
    int size=data.size();
    for(int i=0;i<size;i++)
    {
        if(flage==null||flage.length==0)
        {
           this.setRsp(data.get(i));
        }
        else
        {
            byte[] cur=data.get(i);
            if(cur==null)
            {
                continue;
            }
            byte[]tmp=new byte[flage.length+cur.length];
            System.arraycopy(flage, 0, tmp, 0, flage.length);
            System.arraycopy(cur, 0, tmp, flage.length, cur.length);
            this.setRsp(tmp); 
        }
        
    }
}
}
