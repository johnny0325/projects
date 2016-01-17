package com.test.objectinitiatesequence;

/**
 * 输出结果:
 *  one3-3
	one3-1
	one3-2
	two3-3
	TestDefaultValue main() start...
	one3-1
	one3-2
	two3-1
	----------
	one3-1
	one3-2
	two3-2
	----------
	one3-1
	one3-2
	two3_4
	程序中主类的静态变量会在main()方法执行前初始化。而其它非静态变量,要在调用主类的构造方法前,被初始化。
	结果中只输出了一次one-3，这也说明：如果一个类中有静态对象，那么它会在非静态对象前初始化，但只初始化一次。
	非静态对象每次调用时都要初始化。 
 * One3
 * Author：jlLin
 * Aug 28, 2011  12:17:42 PM
 * Copyright 华仝九方科技有限公司
 */
class One3 {
	One3(String str){
		System.out.println(str);
	}
}

class Two3 {
	One3 one3_1 = new One3("one3-1");
	One3 one3_2 = new One3("one3-2");
	static One3 one3_3 = new One3("one3-3");
	
	Two3(String str){
		System.out.println(str);
	}
	
	public void printStr(){
		System.out.println("printStr()...");
	}
}

public class Test3 {
	//静态成员变量
	static Two3 two3_3 = new Two3("two3-3");
	Two3 two3_4 = new Two3("two3_4");
	
	public Test3(String str){
		System.out.println("Test3的构造方法.......");
	}
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 11:39:30 AM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		Two3 two3_1 = new Two3("two3-1");
		System.out.println("----------");
		Two3 two3_2 = new Two3("two3-2");
		System.out.println("----------");
		//实例化主类
		Test3 test3 = new Test3("");
	}

}