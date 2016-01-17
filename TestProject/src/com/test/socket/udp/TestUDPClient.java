package com.test.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TestUDPClient {

	/**
	 * TestUDPClient.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-17 下午10:07:50
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = (new String("hello,udpserver")).getBytes();
		//用IP和端口指定这个数据包发到什么地方，
		DatagramPacket dp = new DatagramPacket(buf,buf.length,new InetSocketAddress("127.0.0.1",5678));
		
		DatagramSocket ds = new DatagramSocket(999);//使用999连接5678
		ds.send(dp);
		
		ds.close();
	}

}
