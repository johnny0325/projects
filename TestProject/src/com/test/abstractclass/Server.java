package com.test.abstractclass;

public class Server extends TestAbstract {
	
	public Server(){
//		super();
		System.out.println("server �Ĺ��췽��");
	}

	/**
	 * Server.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2015-8-2 ����04:27:03
	 */
	public static void main(String[] args) {
		Server server = new Server();
	}

}
