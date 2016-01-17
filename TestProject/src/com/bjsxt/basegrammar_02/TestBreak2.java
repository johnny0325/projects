package com.bjsxt.basegrammar_02;

public class TestBreak2 {

	/**
	 * 循环语句举例
	 * 输出101~200内的质数
	 * 质数：是只能被1和它本身整除的数。
	 * 也就是说，对于某个整数，只要从2到它之间有一个数可以整除它，那么它就不是质数。
	 * 以上是这个程序所采用算法的基本原理
	 * TestBreak2.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-8-4 下午02:20:36
	 */
	public static void main(String[] args) {
		for(int i=101; i<200; i+=2) {//这里为什么是i+=2?因为偶数肯定不是质数
			boolean f = true;
			for(int j=2; j<i; j++) {
				if(i % j == 0){
					f = false;
					break;//跳出内层的for循环
				}
			}
			if(!f) {
				continue;
			}
			System.out.println(" "+i);//如果是质数，把i的值打印出来
		}
	}

}
