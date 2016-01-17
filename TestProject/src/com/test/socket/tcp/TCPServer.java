package com.test.socket.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	/**
	 * TCPServer.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-6-4 下午11:45:23
	 */
	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket(6666);//使用6666端口监听客户端发过来的连接
		while(true) {
			//服务器端接收客户端的连接，同时创建一个socket跟客户端的socket进行通信
			Socket s = ss.accept();//accept()是一个阻塞的方法，如果没有客户端来连接，它会傻傻地等待下去
			//System.out.println("a server.");
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF("Hello,"+s.getInetAddress()+" port:"+s.getPort());
			dos.flush();
			
			String str = dis.readUTF();
			System.out.println("接收客户端发过来的信息:"+str);
			
			dos.close();
			dis.close();
			s.close();
		}
	}

}
