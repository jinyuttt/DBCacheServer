/**
 * 
 */
package cd.strommq.udp;

import cd.strommq.channel.NettyChannel;
import cd.strommq.channel.NettyServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * @author jinyu
 *
 */
public class NettyUdpSeverHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    private int localPort=0;
    public NettyUdpSeverHandler(int port)
    {
        this.localPort=port;
    }
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        System.out.println("已经5秒未收到客户端的消息了！");
//        if (evt instanceof IdleStateEvent){
//            IdleStateEvent event = (IdleStateEvent)evt;
//            if (event.state()== IdleState.READER_IDLE){
//                lossConnectCount++;
//                if (lossConnectCount>2){
//                    System.out.println("关闭这个不活跃通道！");
//                    ctx.channel().close();
//                }
//            }
//        }else {
//            super.userEventTriggered(ctx,evt);
//        }
//    }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		// 读取收到的数据
        System.out.println("我是UDP服务端接收");
		ByteBuf buf = (ByteBuf) packet.copy().content();
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
//		String body = new String(req, CharsetUtil.UTF_8);
//
//		System.out.println("【NOTE】>>>>>> 收到客户端的数据："+body);
//
//		// 回复一条信息给客户端
//		ctx.writeAndFlush(new DatagramPacket(
//		Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+System.currentTimeMillis()
//
//		, CharsetUtil.UTF_8)
//
//		, packet.sender())).sync();
		 NettyChannel channel=NettyServerData.getChannel(localPort);
         if(channel==null)
         {
             channel=new NettyChannel();
             //channel.addChannel(ctx.channel());
             NettyServerData.addChannel(localPort, channel);
         }
         channel.add(req, ctx.channel(), packet.sender());
		
	}
	


}


