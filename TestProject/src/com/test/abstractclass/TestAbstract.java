package com.test.abstractclass;

/**
 * 抽象类可以有构造方法，构造方法不可继承，但是可以供子类用super（）或者super（参数，参数。。。。）调用
 * TestAbstract
 * Author：jllin
 * 2015-5-15  上午11:46:28
 */
public abstract class TestAbstract{
	//抽象类的构造方法
	public TestAbstract(){
		System.out.println("TestAbstract 的构造方法...");
	}
	
}
