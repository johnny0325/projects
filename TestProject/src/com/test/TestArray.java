package com.test;

/**
 * һά���ݵ�������ʽ�����֣��Լ����鸳ֵ
 * TestArray
 * Author��jlLin
 * Aug 23, 2011  11:38:24 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestArray {
	public static void main(String args[]){
		//��һ��
		int[] nums = {1,2,3};
		//�ڶ���
		int[] nums2 = new int[3];
		//������
		int[] nums3 = new int[]{1,2,3};
		
		boolean booleanArray[] = new boolean[5]; 
		float floatArray[] = new float[5]; 
		System.out.println(nums2[2]);
		System.out.println(booleanArray[2]);
		System.out.println(floatArray[2]);
		
		//�ܻ�����һ�����⣬������ʮ�����˼�Ȼû��һ�����ԣ� 
		String st1[]={"aa","bb","cc","dd","ee","ff"}; 
		String st2[]=st1; 
		st2[0]="00"; 
		System.out.println(st1[0]); 
		System.out.println(st2[0]); 
 	}
}
