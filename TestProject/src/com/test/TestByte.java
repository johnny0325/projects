package com.test;

public class TestByte {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 27, 2011 12:36:52 PM
	 */
	public static void main(String[] args) {
		byte a = 1;
		byte b = 2;
		byte c;
		//c = a+b; //这样是计算不出c,是错误的
		//c = a+1; //这样也是不能计算c的
		c = 64+1; //为什么这样就能计算c,在Java中这是什么原理啊？因为这两个数相加是65，属于byte的数值范围-128~127，而两个byte类型的变量相加首先要转为int型
		
		char d = 'a';
		int i = d+1;
		System.out.println("i === >"+i);
		
		//数组的声明方式，三种
		int[] nums = {1,2,3};
		int[] nums2 = new int[5];
		int[] nums3 = new int[]{1,2,3};
	}

}
