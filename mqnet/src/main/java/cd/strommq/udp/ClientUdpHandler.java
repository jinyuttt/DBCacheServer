/**
 * 
 */
package cd.strommq.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @author jinyu
 *
 */
public class ClientUdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    NettyUdpClient client=null;
  //  private int curTime;
  //  private int beatTime;
    private String heart="heartserver";
    private int heartLen=0;
   public ClientUdpHandler(NettyUdpClient client)
   {
       this.client=client;
       this.heartLen=heart.getBytes(CharsetUtil.UTF_8).length;
   }
   
//   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//       System.out.println("客户端循环心跳监测发送: "+new Date());
//       if (evt instanceof IdleStateEvent){
//           IdleStateEvent event = (IdleStateEvent)evt;
//           if (event.state()== IdleState.WRITER_IDLE){
//               if (curTime<beatTime){
//                   curTime++;
//                   ctx.writeAndFlush("heartclient");
//               }
//           }
//       }
//   }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		//String  body =  packet.content().toString(CharsetUtil.UTF_8);
		//System.out.println(body);
	    System.out.println("我是UDP客户端接收");
	    ByteBuf in=(ByteBuf) packet.content();
        try {
        //  while(in.isReadable()){
            //  System.out.println((char)in.readByte());
            //  System.out.flush();
            //}
            int num=in.readableBytes();
            byte[]data=new byte[num];
            in.readBytes(data);
            if(num==heartLen)
            {
                String heartStr=new String(data);
                if(this.heart.compareToIgnoreCase(heartStr)==0)
                {
                    //ReferenceCountUtil.release(packet);
                    //不要该数据
                    return;
                }
            }
            this.client.addData(data);
        } finally {
           // ReferenceCountUtil.release(packet);
        }
	}


}
