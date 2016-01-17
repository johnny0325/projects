package com.test;

/**
 * 测试对assert的使用
 * 断言在默认情况下是禁用的。要在编译时启用断言，需要使用 source 1.4 标记
 * 要在运行时启用断言，可使用 -enableassertions 或者 -ea 标记
 * TestAssert
 * Author：jlLin
 * Aug 20, 2011  2:40:48 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestAssert {
	public static void main(String args[]){
		int x = 10;
		System.out.println("Testing assertion that x ==100!");
		assert x==100:"Out assertion failed!"; 
		System.out.println("TestDefaultValue Passed!");
	}
}
