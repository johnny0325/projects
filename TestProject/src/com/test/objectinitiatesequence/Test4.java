package com.test.objectinitiatesequence;

/**
 * ��������
 * TestDefaultValue main() start...
   one4-2
   one4-3
   Two4's i==>0
	������1�δ�������ʱ���������еľ�̬����Ҫ��ʼ������1�η������еľ�̬������û�д�������ʱ��
	���������еľ�̬����ҲҪ�����������������е�˳���ʼ���� 
 * One4
 * Author��jlLin
 * Aug 28, 2011  12:35:38 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class One4 {
	One4(String str){
		System.out.println(str);
	}
}

class Two4 {
	static int i = 0;
	One4 one4_1 = new One4("one4-1");
	static One4 one4_2 = new One4("one4-2");
	static One4 one4_3 = new One4("one4-3");
	
	Two4(String str){
		System.out.println(str);
	}
}
public class Test4 {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 12:24:07 PM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		System.out.println("Two4's i==>"+Two4.i); 
	}

}
