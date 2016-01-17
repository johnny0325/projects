package com.test.thread;

public class TT2 implements Runnable{
	int b = 100;
	
	public synchronized void m1() throws Exception{
		System.out.println("m1() is called...");
		b = 1000;
		Thread.sleep(5000);
		System.out.println("b==>"+b);
	}
	
	public synchronized void m2() throws Exception{
		Thread.sleep(2500);
		b = 2000;
		System.out.println("m2() is called...");
	}
	
	
	public void run(){
		try{
			m1();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception{
		TT2 tt = new TT2();
		Thread t = new Thread(tt);
		t.start();
		
		//因为m2()方法加了synchronied方法同步，所以m2()执行完，释放锁，才轮到m1()执行
		tt.m2();
		System.out.println("dddd...");
		System.out.println(tt.b);
	}

}
