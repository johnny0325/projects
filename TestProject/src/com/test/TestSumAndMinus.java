package com.test;

/**
 * 测试递增++、递减--运算符的使用
 * ++i,是先自增，再操作（或者说：先加1再使用）
 * i++,是先操作，再自增（或者说：先使用再加1）
 * TestSumAndMinus
 * Author：jlLin
 * Aug 27, 2011  2:58:11 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestSumAndMinus {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 27, 2011 2:52:45 PM
	 */
	public static void main(String[] args) {
		int a = 1;
		int b  =5;
		
		System.out.println("++a -->"+(++a));
		System.out.println("b++ -->"+(b++));
		int c = 1 + ++a;
		int d = 1 + a++;
		
		int e = 1 + --b;
		int f = 1 + b--;
		
		System.out.println(c);
		System.out.println(d);
		System.out.println(e);
		System.out.println(f);
		
		test1();
		test2();
	}
	
	public static void test1() {
		int i = 0;
		int j = i++;
		int k = --i;
		
		System.out.println("i==>"+i);
		System.out.println("j==>"+j);
		System.out.println("k==>"+k);
	}

	public static void test2() {
		float f = 0.1F;
		f++;
		double d = 0.1D;
		d++;
		char c = 'a';
		c++;
		
		System.out.println("f==>"+f);
		System.out.println("d==>"+d);
		System.out.println("c==>"+c);
	}

}
