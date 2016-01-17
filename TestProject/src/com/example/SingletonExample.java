package com.example;

/**
 * singleton模式的两种实现方式
 * SingletonExample
 * Author：jlLin
 * Aug 23, 2011  7:59:28 PM
 * Copyright 华仝九方科技有限公司
 */
public class SingletonExample {
	
	//singleton模式的第一种实现形式:饿汉式
	/*private SingletonExample(){}
	private static SingletonExample instance = new SingletonExample();
	public static SingletonExample getInstance(){
		return instance;
	}*/
	
	//singleton模式的第二种实现形式:懒汉式
	private static SingletonExample instance;
	private SingletonExample(){}
	public static synchronized SingletonExample getInstance(){
		if(instance == null){
			instance = new SingletonExample();
		}
		return instance;
	}
}
