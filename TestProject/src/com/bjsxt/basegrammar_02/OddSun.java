package com.bjsxt.basegrammar_02;

public class OddSun {

	/**
	 * ��ϰ����һ��forѭ������1+3+5+7+......+99��ֵ������������
	 * ��ʵ�����������ĺ�
	 * С���ɣ������С�İ�����tab��������������Ӧ�ð�shift+tab�Ϳ���
	 * OddSun.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-8-2 ����11:30:28
	 */
	public static void main(String[] args) {
		long result = 0;
		for(int i=1; i<=99; i+=2) {//ע�⣺���������ʽ��i+=2,ÿ�ζ�����2
			result += i;
		}
		System.out.println("result ==>"+result);
	}

}
