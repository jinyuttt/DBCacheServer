package cd.strommq.mqnet;

import cd.strommq.channel.NettyRspData;
import cd.strommq.tcp.NettyTcpClient;
import cd.strommq.tcp.NettyTcpServer;
import cd.strommq.udp.NettyUdpClient;
import cd.strommq.udp.NettyUdpServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       // NettyTcpServer server=new NettyTcpServer();
       // server.start();
//        NettyUdpServer server=new NettyUdpServer();
//       
//        Thread dd=new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                server.start(9999);
//                
//            }
//            
//        });
//        dd.setDaemon(true);
//        dd.start();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//         
//            e.printStackTrace();
//        }
//        while(true)
//        {
//            NettyRspData rsp = server.recvice();
//            System.out.println(new String((byte[])rsp.data));;
//            rsp.setRsp("收到");
//        }
//       
        
        NettyUdpClient client=new NettyUdpClient();
    	client.connect("192.168.17.1", 9999);
       // client.connect("127.0.0.1", 9999);
    	while(true)
    	{
    	  client.send("dfgg".getBytes());
    	  byte[] rec=client.recvice();
    	  String rsp=new String(rec);
    	  System.out.println(rsp);
    	  try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
    	}
    }
}
