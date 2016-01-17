package com.test.thread;

public class StopThread {

	/**
	 * 如何让一个线程停止？最好的办法是设置一个变量，让它等于false
	 * run()方法一结束，线程就结束
	 * StopThread.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-4-22 下午03:07:53
	 */
	public static void main(String[] args) {
		TestRunnable r = new TestRunnable();
		Thread t1 = new Thread(r);
		t1.start();
		
		for(int i=0;i<=10000;i++){
			if(i%1000 == 0 & i>0){
				System.out.println("in thread main i="+i);
			}
		}
		//调用shutdown,flag=false;
		r.shutdown();
	}

}

class TestRunnable implements Runnable {
	boolean flag = true;
	
	public void run(){
		int i=0;
		while(flag == true){
			System.out.println(" "+ i++);
		}
	}
	
	public void shutdown(){
		flag = false;
	}
}