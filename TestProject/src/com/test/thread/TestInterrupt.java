package com.test.thread;

import java.util.Date;

//该类主要是测试了线程的sleep()和interrupt()方法的使用
public class TestInterrupt {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Dec 2, 2011 12:03:41 AM
	 */
	public static void main(String[] args) {
		MyThread thread = new MyThread();
		thread.start(); 
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){
			
		}
		
		thread.interrupt(); //子线程调用中断方法interrupt(),子线程中断退出线程
	}

}

class MyThread extends Thread{
	public void run(){
		while(true){
			System.out.println("==="+ new Date() +"===");
			try{
				sleep(1000);
			}catch(InterruptedException e){
				return ; //退出线程
			}
		}
	}
}
