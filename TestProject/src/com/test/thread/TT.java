package com.test.thread;

/*
 * ����m1()����������,��m2()����û������,����߳��Ƿ��ܷ���m2(),��ʵ֤��,��Ȼm1()���������˵�ǰ����,���Ǳ���߳��ǿ���������������û�б�
 * �����ķ���,��������һ����ҵ����������Ŀ,���������ó�������˴�
*/
public class TT implements Runnable{
	public int b = 100;
	public synchronized void m1()throws InterruptedException {
		b = 1000;
		Thread.sleep(5000);
		System.out.println("b==>"+b);
	}
	
	public void m2(){
		System.out.println(b);
	}
	
	public void run(){
		try{
			m1();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Dec 12, 2011 10:52:33 PM
	 */
	public static void main(String[] args) {
		TT tt = new TT();
		Thread t = new Thread(tt);
		t.start();
		
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		tt.m2();
	}

}
