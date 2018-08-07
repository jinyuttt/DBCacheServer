/**
 * 
 */
package cd.strommq.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cd.strommq.channel.NettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author jinyu
 *
 */
public class NettyUdpClient implements NettyClient{
public String host="255.255.255.255";
public int port=9999;
private  Channel channel=null;
private EventLoopGroup group=null;
private LinkedBlockingQueue<byte[]> queue=null;
private volatile int recTimeOut=-1;
private  AtomicLong size=new AtomicLong(0);
private void init()
{
	 group = new NioEventLoopGroup();
	 queue=new LinkedBlockingQueue<byte[]>();
    try {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ClientUdpHandler(this));
               // .handler(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        channel= b.bind(0).sync().channel();
    }catch (Exception e){
        e.printStackTrace();
    }finally {
        //group.shutdownGracefully();
    }
}
public int sendData(byte[] data)
{
    if(data==null)
    {
        return 0;
    }
	if(channel==null)
	{
		init();
	}
	ByteBuf buf=Unpooled.wrappedBuffer(data);
	channel.writeAndFlush(new DatagramPacket(buf,
             new InetSocketAddress(host, port)));
	System.out.println("udpClient send "+host+" "+port);
	return data.length;
}
public int send(String ip,int port,byte[] data)
{
    if(data==null)
    {
        return 0;
    }
	if(channel==null)
	{
		init();
	}
	ByteBuf buf=Unpooled.wrappedBuffer(data);
	channel.writeAndFlush(new DatagramPacket(buf,
             new InetSocketAddress(ip, port)));
	return data.length;
}
public void addData(byte[]data)
{
    queue.add(data);
}
public byte[] recvice()
{
    byte[] result=null;
    try {
        if(this.recTimeOut==-1)
        {
            result=queue.take();
            size.decrementAndGet();
        }
        else
        {
            result= queue.poll(recTimeOut, TimeUnit.MILLISECONDS);
            if(result!=null)
            {
                size.decrementAndGet();
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    if(size.get()<0)
    {
        size.set(0);
    }
    return result;
}
public void close()
{
	channel.close();
	if(queue!=null)
	{
	    queue.clear();
	}
	try {
		channel.closeFuture().await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	group.shutdownGracefully();
}
@Override
public boolean connect(String host, int port) {
    this.host=host;
    this.port=port;
    return true;
}
@Override
public int send(byte[] data) {
  return this.sendData(data);
}
@Override
public boolean isClose() {
    // boolean r= channel.isActive();
    return group.isShutdown();
}
@Override
public void resetConnect() {
 
    
}
@Override
public int sendUDP(String host, int port, byte[] data) {
   return this.send(host, port, data);
}
@Override
public void setRecviceTimeOut(int time) {
  this.recTimeOut=time;
  if(this.recTimeOut<-1)
  {
      this.recTimeOut=-1;
  }
}
@Override
public long getSize() {
    if(size.get()<0)
    {
        size.set(0);
    }
    return size.get();
}
@Override
public boolean isEmpty() {
   
    return queue.isEmpty();
}
}
