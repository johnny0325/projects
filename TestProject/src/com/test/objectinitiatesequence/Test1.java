package com.test.objectinitiatesequence;

/**
 * ��������  
 * TestDefaultValue main() start... 
 * one-1 
 * one-2 
 * one-3 
 * two
 * ��main()������ʵ������һ��Two��Ķ��󡣵������ڳ�ʼ��Two��Ķ���ʱ��
 * �����ȵ���Two��Ĺ��췽���������ȳ�ʼ��Two��ĳ�Ա����������Two����3����Ա������
 * ���Ƕ���One��Ķ�������Ҫ�ȵ���3��One�����Ӧ�Ĺ��췽��������ٳ�ʼ��Two��Ķ���
 * ��������Ĺ��̣�1������ռ䣻2����ʼ����Ա������3�����ñ���Ĺ��췽����
 * Author��jlLin
 * Aug 28, 2011  11:20:02 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class One {
	One(String str){
		System.out.println(str);
	}
}

class Two {
	One one_1 = new One("one-1");
	One one_2 = new One("one-2");
	One one_3 = new One("one-3");
	
	Two(String str){
		System.out.println(str);
	}
}

public class Test1 {
	
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 11:15:07 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Two two = new Two("two");
	}

}
