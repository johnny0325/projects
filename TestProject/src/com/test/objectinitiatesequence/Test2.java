package com.test.objectinitiatesequence;

/**
 * �������� 
   TestDefaultValue main() start... 
   one-3 
   one-1 
   one-2 
   two-1 
   ------------ 
   one-1 
   one-2 
   two-2 
	���һ�������о�̬������ô�����ڷǾ�̬����ǰ��ʼ������ֻ��ʼ��һ�Ρ��Ǿ�̬����ÿ�ε���ʱ��Ҫ��ʼ����
 * One2
 * Author��jlLin
 * Aug 28, 2011  11:51:35 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class One2 {
	One2(String str){
		System.out.println(str);
	}
}

class Two2 {
	//��Ա����
	One2 one_1 = new One2("one2-1");
	One2 one_2 = new One2("one2-2");
	static One2 one_3 = new One2("one2-3");
	
	Two2(String str){
		System.out.println(str);
	}
}

public class Test2 {
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 11:39:30 AM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		Two2 two_1 = new Two2("two2-1");
		System.out.println("----------");
		Two2 two_2 = new Two2("two2-2");
	}

}
