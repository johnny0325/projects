package com.bjsxt.basegrammar_02;

public class TestBreak1 {

	/**
	 * ѭ��������
	 * ����1~100��ǰ5�����Ա�3��������
	 * TestBreak1.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-8-4 ����02:12:10
	 */
	public static void main(String[] args) {
		int num = 0,i = 1;
		while(i <= 100) {
			if(i % 3 == 0) {
				System.out.println(i + " ");
				num++;
			}
			if(num == 5) {
				break;//����ѭ��
			}
			i++;
		}
	}

}
