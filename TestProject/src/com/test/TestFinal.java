package com.test;
/**
 * ��֤��������Ϊfinal�ı�������������ʱ������ֵ(������Ա����������Ա������final����ʱҲ��Ҫ������ֵ��)�������Ժ��������ֻ�ܶ�ȡ�������޸ġ�
 * TestFinal
 * Author��jlLin
 * Aug 20, 2011  9:47:10 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
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
