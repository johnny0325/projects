package com.test.inheritance;

/**
 * ������:
 * SupperClass constructor
 * SubClass constructor
 * ��������ֻʵ������һ��������󡣴��������Ͽ������򲢲���һ��ʼ�������Լ��Ĺ��췽����
 * �����������丸���Ĭ�Ϲ��췽����ע�⣺�����Զ������丸���Ĭ�Ϲ��췽���� 
 * SupperClass
 * Author��jlLin
 * Aug 28, 2011  10:52:15 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
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
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 10:30:40 AM
	 */
	public static void main(String[] args) {
		SubClass sub = new SubClass();
	}

}

