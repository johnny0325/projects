package com.test.socket.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 测试从客户端传一个long型的数据到服务器端，然后服务器要正确地读出来，关键是怎么把这个long数据转为字节数组
 * TestLongClient
 * Author：jllin
 * 2013-7-21  下午03:22:04
 */
public class TestLongClient {

	/**
	 * TestLongClient.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-21 下午02:04:36
	 */
	public static void main(String[] args) {
		try {
			long n = 10000L;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeLong(n);
			
			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf, buf.length,new InetSocketAddress("127.0.0.1", 5678));
			DatagramSocket ds = new DatagramSocket(9999);
			ds.send(dp);
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
 