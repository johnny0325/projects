package com.example;

/**
 * singletonģʽ������ʵ�ַ�ʽ
 * SingletonExample
 * Author��jlLin
 * Aug 23, 2011  7:59:28 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class SingletonExample {
	
	//singletonģʽ�ĵ�һ��ʵ����ʽ:����ʽ
	/*private SingletonExample(){}
	private static SingletonExample instance = new SingletonExample();
	public static SingletonExample getInstance(){
		return instance;
	}*/
	
	//singletonģʽ�ĵڶ���ʵ����ʽ:����ʽ
	private static SingletonExample instance;
	private SingletonExample(){}
	public static synchronized SingletonExample getInstance(){
		if(instance == null){
			instance = new SingletonExample();
		}
		return instance;
	}
}
