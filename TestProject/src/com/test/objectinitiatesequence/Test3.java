package com.test.objectinitiatesequence;

/**
 * ������:
 *  one3-3
	one3-1
	one3-2
	two3-3
	TestDefaultValue main() start...
	one3-1
	one3-2
	two3-1
	----------
	one3-1
	one3-2
	two3-2
	----------
	one3-1
	one3-2
	two3_4
	����������ľ�̬��������main()����ִ��ǰ��ʼ�����������Ǿ�̬����,Ҫ�ڵ�������Ĺ��췽��ǰ,����ʼ����
	�����ֻ�����һ��one-3����Ҳ˵�������һ�������о�̬������ô�����ڷǾ�̬����ǰ��ʼ������ֻ��ʼ��һ�Ρ�
	�Ǿ�̬����ÿ�ε���ʱ��Ҫ��ʼ���� 
 * One3
 * Author��jlLin
 * Aug 28, 2011  12:17:42 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class One3 {
	One3(String str){
		System.out.println(str);
	}
}

class Two3 {
	One3 one3_1 = new One3("one3-1");
	One3 one3_2 = new One3("one3-2");
	static One3 one3_3 = new One3("one3-3");
	
	Two3(String str){
		System.out.println(str);
	}
	
	public void printStr(){
		System.out.println("printStr()...");
	}
}

public class Test3 {
	//��̬��Ա����
	static Two3 two3_3 = new Two3("two3-3");
	Two3 two3_4 = new Two3("two3_4");
	
	public Test3(String str){
		System.out.println("Test3�Ĺ��췽��.......");
	}
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 11:39:30 AM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		Two3 two3_1 = new Two3("two3-1");
		System.out.println("----------");
		Two3 two3_2 = new Two3("two3-2");
		System.out.println("----------");
		//ʵ��������
		Test3 test3 = new Test3("");
	}

}