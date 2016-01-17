package com.test.thread;

//演示yield()方法的使用
public class TestYield {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Dec 4, 2011 9:29:45 PM
	 */
	public static void main(String[] args) {
		MyThread3 t1 = new MyThread3("t1");
		MyThread3 t2 = new MyThread3("t2");
		t1.start();
		t2.start();
	}

}

class MyThread3 extends Thread{
	public MyThread3(String s){
		super(s);//调用父类的构造方法，为线程起一个名字
	}
	
	public void run(){
		for(int i=1;i<100;i++){
			System.out.println(getName()+"==>"+i);
			if(i%10 == 0){
				yield();
			}
		}
	}
}
