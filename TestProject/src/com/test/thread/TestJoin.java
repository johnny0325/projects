package com.test.thread;

public class TestJoin {

	/**
	 * TestJoin.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-4-21 ����09:18:56
	 */
	public static void main(String[] args) {
		MyThread2 t1 = new MyThread2("t1");
		t1.start();
		try {
			t1.join(); //�̺߳ϲ�,t1ִ�����ˣ����̲߳Ž���ִ��
		} catch (InterruptedException e) {
			
		}
		
		for(int i=0;i<=10;i++){
			System.out.println("I am main thread.");
		}
	}

}

class MyThread2 extends Thread {
	MyThread2(String s) {
		super(s);
	}
	
	public void run(){
		for(int i=0;i<=10;i++){
			System.out.println("I am "+getName());
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
