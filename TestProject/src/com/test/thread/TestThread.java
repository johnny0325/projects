package com.test.thread;

public class TestThread implements Runnable {
	String myString = "yes";
	public void run() {
		System.out.println("---------开始");
		this.myString = "no";
	}

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Sep 1, 2011 5:38:57 PM
	 */
	public static void main(String[] args) {
		TestThread t = new TestThread();
		Thread thread = new Thread(t);
		thread.start();
		for(int i=0;i<6;i++){
			System.out.println("-----------");
			System.out.println(t.myString);
		}
	}

}
