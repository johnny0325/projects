package com.test.thread;

/**
 * 题目：有三个线程ID分别是A、B、C,请用多线编程实现，在屏幕上循环打印10次ABCABC…
 * 分析：由于线程执行的不确定性，要保证这样有序的输出，必须控制好多线程的同步。
 * 线程同步有两种基本方法：(1)使用synchronized关键字，利用同步锁技术实现同步 (2)使用线程的常用方法wait,notify,notifyAll
 * XunleiInterReviewMultithread
 * Author：jlLin
 * Dec 4, 2011  2:42:41 PM
 * Copyright 华仝九方科技有限公司
 */
public class XunleiInterReviewMultithread {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Dec 4, 2011 2:07:49 PM
	 */
	public static void main(String[] args) {
		XunleiLock lock = new XunleiLock();
		new Thread(new XunleiPrinter("A",lock)).start();
		new Thread(new XunleiPrinter("B",lock)).start();
		new Thread(new XunleiPrinter("C",lock)).start();
		
	}

}

//解法一
class XunleiPrinter implements Runnable{
	private String name = "";
	private XunleiLock lock;
	private int count = 10;
	
	public XunleiPrinter(String name,XunleiLock lock){
		this.name = name;
		this.lock = lock;
	}

	public void run(){
		while(count > 0){System.out.println("count/lock'name/this'name==>"+count+" "+lock.name+" "+this.name);
			synchronized(lock){
				if(lock.name.equalsIgnoreCase(this.name)){//控制线程的有序性
					System.out.println(name);
					count--;
					
					if(this.name.equals("A")){
						lock.setName("B");
					}else if(this.name.equals("B")){
						lock.setName("C");
					}else if(this.name.equals("C")){
						lock.setName("A");
					}
				}
			}
		}
	}
	
}

class XunleiLock {
	public String name = "A";
	
	public void setName(String name){
		this.name = name;System.out.println("lock setName==>"+name);
	}
	
	public String getName(){
		return name;
	}
}
