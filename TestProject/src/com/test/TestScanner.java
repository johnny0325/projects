package com.test;

import java.util.Scanner;

/**
 * �Ӽ��̶�ȡ���ݣ�����ʹ��Scanner��,����java.util����,������ṩ�˶�ȡ���������������ݵķ���
 * TestScanner
 * Author��jlLin
 * Aug 27, 2011  1:59:36 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestScanner {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 27, 2011 1:59:22 PM
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("������������");
		String name = input.next();
		System.out.println("���������䣺");
		int age = input.nextInt();
	}

}
