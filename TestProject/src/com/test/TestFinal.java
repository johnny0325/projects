package com.test;
/**
 * 验证：被声明为final的变量必须在声明时给定初值(包括成员变量，即成员变量被final修饰时也是要给定初值的)，而在以后的引用中只能读取，不可修改。
 * TestFinal
 * Author：jlLin
 * Aug 20, 2011  9:47:10 AM
 * Copyright 华仝九方科技有限公司
 */
public class TestFinal {
	final int value = 5;
	String str;
	
	public static void main(String args[]){
		TestFinal tf = new TestFinal();
		System.out.println(tf.value);
		System.out.println(tf.str);
	}
}
