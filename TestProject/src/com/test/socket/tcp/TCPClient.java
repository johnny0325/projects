package com.test.socket.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPClient {

	/**
	 * TCPClient.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-6-4 下午11:45:41
	 */
	public static void main(String[] args) throws Exception{
		Socket s = new Socket("127.0.0.1",6666);//向本机的6666端口发出连接请求
		
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		
//		Thread.sleep(30000);
		dos.writeUTF("我要连接服务器");
		
		System.out.println(dis.readUTF());
	}

}
