package com.test.thread;

/**
 * 题目：有三个线程ID分别是A、B、C,请用多线编程实现，在屏幕上循环打印10次ABCABC…
 * 解法二
 * XunLeiExam2
 * Author：jllin
 * 2013-4-27  下午11:22:15
 */
public class XunLeiExam2 {
	public static int i;

	public static void main(String[] args) {
		Thread th1 = new XLThread(1);
		th1.start();
		Thread th2 = new XLThread(2);
		th2.start();
		Thread th3 = new XLThread(3);
		th3.start();
		//初始化i为1，使其打印出A
		i = 1;
	}

	public static void addI() {
		if (i == 3)
			i = 1;
		else
			i = i + 1;
	}
}

class XLThread extends Thread {
	public int v;

	public XLThread(int v) {
		this.v = v;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			while (true) {
				
				if (XunLeiExam2.i == this.v) {
					System.out.println("XunLeiExam2.i==>"+XunLeiExam2.i + " v==>"+v);
					System.out.println((char) ('A' + v - 1));
					XunLeiExam2.addI();
					//跳出当前循环
					break;
				} else {
					System.out.println("ELSE XunLeiExam2.i==>"+XunLeiExam2.i + " v==>"+v);
					try {
						Thread.sleep(v);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
