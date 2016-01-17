package com.test.thread;

import java.util.Date;

//������Ҫ�ǲ������̵߳�sleep()��interrupt()������ʹ��
public class TestInterrupt {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Dec 2, 2011 12:03:41 AM
	 */
	public static void main(String[] args) {
		MyThread thread = new MyThread();
		thread.start(); 
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){
			
		}
		
		thread.interrupt(); //���̵߳����жϷ���interrupt(),���߳��ж��˳��߳�
	}

}

class MyThread extends Thread{
	public void run(){
		while(true){
			System.out.println("==="+ new Date() +"===");
			try{
				sleep(1000);
			}catch(InterruptedException e){
				return ; //�˳��߳�
			}
		}
	}
}
