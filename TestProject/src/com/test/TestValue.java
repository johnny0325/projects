package com.test;

/**
 * ���Ե���++���ݼ�--������Լ�do...while()�ṹ��ʹ��
 * TestValue
 * Author��jlLin
 * Sep 1, 2011  5:58:17 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestValue {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Sep 1, 2011 5:48:47 PM
	 */
	public static void main(String[] args) {
		//Ҫע��۲��������������治ͬ�ĵط�
		test1();
		test2();
	}

	public static void test1(){
		int i = 0;
		int j = 10;
		do{
			if(i++<--j){
				//0 1 9
				//1 2 8
				//2 3 7
				//3 4 6
				//4 5 5
				continue;
			}
		}while(i<5);
		System.out.println("i==>"+i+" j==>"+j);
	}
	
	public static void test2(){
		int i = 1;
		int j = 10;
		do{
			if(i++>--j){
				//1 2 9
				//2 3 8
				//3 4 7
				//4 5 6
				//��i=5��ʱ�򣬲�����while<5��������ѭ��������j=6
				continue;
			}
		}while(i<5);
		System.out.println("i==>"+i+" j==>"+j);
	}
}
