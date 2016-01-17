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
	 * Author��jllin
	 * 2013-6-4 ����11:45:23
	 */
	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket(6666);//ʹ��6666�˿ڼ����ͻ��˷�����������
		while(true) {
			//�������˽��տͻ��˵����ӣ�ͬʱ����һ��socket���ͻ��˵�socket����ͨ��
			Socket s = ss.accept();//accept()��һ�������ķ��������û�пͻ��������ӣ�����ɵɵ�صȴ���ȥ
			//System.out.println("a server.");
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF("Hello,"+s.getInetAddress()+" port:"+s.getPort());
			dos.flush();
			
			String str = dis.readUTF();
			System.out.println("���տͻ��˷���������Ϣ:"+str);
			
			dos.close();
			dis.close();
			s.close();
		}
	}

}
