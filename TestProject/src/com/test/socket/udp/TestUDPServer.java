package com.test.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TestUDPServer {

	/**
	 * TestUDPServer.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-17 ����09:54:28
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[1024];//�������ݰ��Ĵ�С����
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		DatagramSocket ds = new DatagramSocket(5678);//ָ��5678��ΪUDP����˵ļ����˿ڣ�ע��TCP��UDP�Ķ˿��ǲ�һ���ģ�����ӵ��65536���˿ڿ���ʹ��
		
		while(true) {
			ds.receive(dp);//���տͻ��˷����������ݰ�����Ҳ��һ�������ķ���
			System.out.println(new String(buf,0,dp.getLength()));//�ѽ��յ������ݰ���ӡ����
		}
 	}

}
