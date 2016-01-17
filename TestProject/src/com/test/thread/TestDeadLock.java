package com.test.thread;

//演示一个死锁的例子
//解决死锁的办法是:把锁的粒度加粗,尽量只使用一把锁
//不写很复杂的东西(比如实现一个数据库产品),你是很难遇到死锁,但是你要知道有这么一回事存在
public class TestDeadLock implements Runnable{

	public int flag = 1;
	static Object o1 = new Object(), o2 = new Object();
	public void run(){
		System.out.println("flag==>"+flag);
		if(flag == 1){
			synchronized(o1){
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				synchronized(o2){
					System.out.println("1");
				}
			}
			
		}
		
		if(flag == 0){
			synchronized(o2){
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				synchronized(o1){
					System.out.println("0");
				}
			}

		}
	}
	public static void main(String[] args) {
		TestDeadLock td1 = new TestDeadLock();
		TestDeadLock td2 = new TestDeadLock();
		td1.flag = 1;
		td2.flag = 0;
		Thread t1 = new Thread(td1);
		Thread t2 = new Thread(td2);
		t1.start();
		t2.start();

	}

}
