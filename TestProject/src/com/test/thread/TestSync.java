package com.test.thread;

/**
 * ������Ҫ�ǲ���ʹ��synchronized�ؼ��ֽ����߳�ͬ������
 * �߳���ִ��ͬ������ʱ�Ǿ��������Եġ�������һ���߳̽��뵽һ�����������һ��ͬ������ʱ��������������ͬ���������������ˣ��ڴ��ڼ䣬
 * �����κ��̶߳����ܷ���������������һ��ͬ��������ֱ������߳�ִ�����������õ�ͬ�������������˳����Ӷ��������ͷ��˸ö����ͬ����
 * ֮����һ������ĳ���߳�����֮�������߳��ǿ��Է��������������з�ͬ�������ġ�
 * TestSync
 * Author��jllin
 * 2013-5-14  ����11:10:11
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
		//������ǰ���󣬵�һ���߳�ִ�еĹ����У�����������һ���߳�ִ�з��ʡ�ֱ�����߳������� ���ͷ����ˣ�����һ���̲߳ſ���ִ��synchronied����Ĵ���
		synchronized(this){
			num++;
			try{
				Thread.sleep(1);
			}catch(InterruptedException e){
				
			}
			System.out.println(name + ",���ǵ�"+num+"��ʹ��Timer���߳�");
		}
	}
}
