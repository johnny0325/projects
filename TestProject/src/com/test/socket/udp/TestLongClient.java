package com.test.socket.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * ���Դӿͻ��˴�һ��long�͵����ݵ��������ˣ�Ȼ�������Ҫ��ȷ�ض��������ؼ�����ô�����long����תΪ�ֽ�����
 * TestLongClient
 * Author��jllin
 * 2013-7-21  ����03:22:04
 */
public class TestLongClient {

	/**
	 * TestLongClient.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-21 ����02:04:36
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
 