package com.test;

public class TestByte {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 27, 2011 12:36:52 PM
	 */
	public static void main(String[] args) {
		byte a = 1;
		byte b = 2;
		byte c;
		//c = a+b; //�����Ǽ��㲻��c,�Ǵ����
		//c = a+1; //����Ҳ�ǲ��ܼ���c��
		c = 64+1; //Ϊʲô�������ܼ���c,��Java������ʲôԭ������Ϊ�������������65������byte����ֵ��Χ-128~127��������byte���͵ı����������ҪתΪint��
		
		char d = 'a';
		int i = d+1;
		System.out.println("i === >"+i);
		
		//�����������ʽ������
		int[] nums = {1,2,3};
		int[] nums2 = new int[5];
		int[] nums3 = new int[]{1,2,3};
	}

}
