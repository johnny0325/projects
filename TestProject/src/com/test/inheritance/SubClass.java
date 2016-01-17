package com.test.inheritance;

/**
 * 输出结果:
 * SupperClass constructor
 * SubClass constructor
 * 在子类中只实例化了一个子类对象。从输出结果上看，程序并不是一开始就运行自己的构造方法，
 * 而是先运行其父类的默认构造方法。注意：程序自动调用其父类的默认构造方法。 
 * SupperClass
 * Author：jlLin
 * Aug 28, 2011  10:52:15 AM
 * Copyright 华仝九方科技有限公司
 */
class SupperClass {
	SupperClass(){
		System.out.println("SupperClass constructor");
	}
}

public class SubClass extends SupperClass {
	SubClass(){
		System.out.println("SubClass constructor");
	}
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 10:30:40 AM
	 */
	public static void main(String[] args) {
		SubClass sub = new SubClass();
	}

}

