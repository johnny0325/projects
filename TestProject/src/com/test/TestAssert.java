package com.test;

/**
 * ���Զ�assert��ʹ��
 * ������Ĭ��������ǽ��õġ�Ҫ�ڱ���ʱ���ö��ԣ���Ҫʹ�� source 1.4 ���
 * Ҫ������ʱ���ö��ԣ���ʹ�� -enableassertions ���� -ea ���
 * TestAssert
 * Author��jlLin
 * Aug 20, 2011  2:40:48 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestAssert {
	public static void main(String args[]){
		int x = 10;
		System.out.println("Testing assertion that x ==100!");
		assert x==100:"Out assertion failed!"; 
		System.out.println("TestDefaultValue Passed!");
	}
}
