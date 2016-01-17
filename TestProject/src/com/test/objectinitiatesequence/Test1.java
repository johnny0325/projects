package com.test.objectinitiatesequence;

/**
 * 输出结果：  
 * TestDefaultValue main() start... 
 * one-1 
 * one-2 
 * one-3 
 * two
 * 在main()方法中实例化了一个Two类的对象。但程序在初始化Two类的对象时，
 * 并非先调用Two类的构造方法，而是先初始化Two类的成员变量。这里Two类有3个成员变量，
 * 它们都是One类的对象，所以要先调用3次One类的相应的构造方法。最后再初始化Two类的对象。
 * 创建对象的过程：1、分配空间；2、初始化成员变量；3、调用本类的构造方法。
 * Author：jlLin
 * Aug 28, 2011  11:20:02 AM
 * Copyright 华仝九方科技有限公司
 */
class One {
	One(String str){
		System.out.println(str);
	}
}

class Two {
	One one_1 = new One("one-1");
	One one_2 = new One("one-2");
	One one_3 = new One("one-3");
	
	Two(String str){
		System.out.println(str);
	}
}

public class Test1 {
	
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 11:15:07 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Two two = new Two("two");
	}

}
