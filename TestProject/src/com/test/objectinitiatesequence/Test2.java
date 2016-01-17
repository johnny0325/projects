package com.test.objectinitiatesequence;

/**
 * 输出结果： 
   TestDefaultValue main() start... 
   one-3 
   one-1 
   one-2 
   two-1 
   ------------ 
   one-1 
   one-2 
   two-2 
	如果一个类中有静态对象，那么它会在非静态对象前初始化，但只初始化一次。非静态对象每次调用时都要初始化。
 * One2
 * Author：jlLin
 * Aug 28, 2011  11:51:35 AM
 * Copyright 华仝九方科技有限公司
 */
class One2 {
	One2(String str){
		System.out.println(str);
	}
}

class Two2 {
	//成员变量
	One2 one_1 = new One2("one2-1");
	One2 one_2 = new One2("one2-2");
	static One2 one_3 = new One2("one2-3");
	
	Two2(String str){
		System.out.println(str);
	}
}

public class Test2 {
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 11:39:30 AM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		Two2 two_1 = new Two2("two2-1");
		System.out.println("----------");
		Two2 two_2 = new Two2("two2-2");
	}

}
