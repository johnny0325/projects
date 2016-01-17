package com.test.thread;

/*
 * 测试m1()方法被锁定,而m2()方法没有锁定,别的线程是否能访问m2(),事实证明,虽然m1()方法锁定了当前对象,但是别的线程是可以正常访问运行没有被
 * 锁定的方法,这曾经是一个企业出的面试题目,现在我们用程序给出了答案
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
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
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
