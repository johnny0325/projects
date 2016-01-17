package com.test.thread;

/**
 * 该类主要是测试使用synchronized关键字进行线程同步操作
 * 线程在执行同步方法时是具有排它性的。当任意一个线程进入到一个对象的任意一个同步方法时，这个对象的所有同步方法都被锁定了，在此期间，
 * 其它任何线程都不能访问这个对象的任意一个同步方法，直到这个线程执行完它所调用的同步方法并从中退出，从而导致它释放了该对象的同步锁
 * 之后。在一个对象被某个线程锁定之后，其它线程是可以访问这个对象的所有非同步方法的。
 * TestSync
 * Author：jllin
 * 2013-5-14  上午11:10:11
 */
public class TestSync implements Runnable {
	Timer timer = new Timer();
	public static void main(String[] args) {
		TestSync test = new TestSync();
		Thread t1 = new Thread(test);
		Thread t2 = new Thread(test);
		t1.setName("t1");
		t2.setName("t2");
		t1.start();
		t2.start();
	}
	
	public void run(){
		timer.add(Thread.currentThread().getName());
	}
}

class Timer{
	private static int num = 0 ;
	public void add(String name){
		//锁定当前对象，当一个线程执行的过程中，不会有另外一个线程执行访问。直到该线程运行完 ，释放锁了，另外一个线程才可以执行synchronied里面的代码
		synchronized(this){
			num++;
			try{
				Thread.sleep(1);
			}catch(InterruptedException e){
				
			}
			System.out.println(name + ",你是第"+num+"个使用Timer的线程");
		}
	}
}
