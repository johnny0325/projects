package com.test;

/**
 * ����break�����ã�������ǰѭ����Ҳ������ѭ������ִ��ѭ����ʣ�����
 * TestBreak
 * Author��jllin
 * 2013-4-27  ����09:30:42
 */
public class TestBreak {

	/**
	 * TestDefaultValue.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-4-27 ����09:27:19
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println("i==>"+i);
			while (true) {
				System.out.println("while==>"+i);break;
				/*if(i == 0) {
					System.out.println("break");
					break;
				}*/
			}
	}

	}

}
