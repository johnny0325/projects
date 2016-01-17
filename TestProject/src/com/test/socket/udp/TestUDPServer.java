package com.test.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TestUDPServer {

	/**
	 * TestUDPServer.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-17 下午09:54:28
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[1024];//定义数据包的大小容量
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		DatagramSocket ds = new DatagramSocket(5678);//指定5678作为UDP服务端的监听端口，注意TCP和UDP的端口是不一样的，各自拥有65536个端口可以使用
		
		while(true) {
			ds.receive(dp);//接收客户端发过来的数据包，它也是一个阻塞的方法
			System.out.println(new String(buf,0,dp.getLength()));//把接收到的数据包打印出来
		}
 	}

}
