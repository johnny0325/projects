package com.test.socket.udp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TestLongServer {

	/**
	 * TestLongServer.main()
	 * @param args
	 * @return void
	 * Author£ºjllin
	 * 2013-7-21 ÏÂÎç02:36:32
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		DatagramSocket ds = new DatagramSocket(5678);
		while (true) {
			ds.receive(dp);
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			DataInputStream dis = new DataInputStream(bais);
			long n = dis.readLong();
			System.out.println("n==>"+n);
		}
	}

}
