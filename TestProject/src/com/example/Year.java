package com.example;

import java.util.Scanner;

/**
 * �����Ƿ�������
 * �й�������˽⣺
 * ���������ǣ�һ���ܱ�4����,�����ܱ�100����; �����ܱ�400����; ����һ���������ꡣ��2000�����꣬��1900�������� 
 * ��ϸ������1900�������������������� 
 * ͨ�׵�˵����������һ�򣬰��겻���İ�������
 * Year
 * Author��jlLin
 * Aug 27, 2011  2:31:46 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class Year {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 27, 2011 2:31:21 PM
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("��������ݣ�");
		int year = input.nextInt();
		
		if((year%4==0 && year%100!=0)||year%400 ==0){
			System.out.println("������!");
		}else{
			System.out.println("��ƽ��!");
		}
	}

}
