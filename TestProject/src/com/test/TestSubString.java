package com.test;

public class TestSubString {

	/**
	 * 
	 * TestSubString.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2014-5-5 ����12:10:39
	 */
	public static void main(String[] args) {
		
		/**
		 * public String substring(int beginIndex,int endindex)
		 * ����һ�����ַ��������Ǵ��ַ�����һ�����ַ����������ַ�����ָ���� beginIndex����ʼ�� endindex:��ʾ��β��������
		 * ������
		 * beginIndex - ��ʼ����������������
		 * endindex ��β����������������
		 */
		String workDate = "20140505";
		System.out.println(workDate.substring(4,6));
	}

}
