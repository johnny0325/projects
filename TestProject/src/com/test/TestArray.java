package com.test;

/**
 * 一维数据的声明方式有三种，以及数组赋值
 * TestArray
 * Author：jlLin
 * Aug 23, 2011  11:38:24 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestArray {
	public static void main(String args[]){
		//第一种
		int[] nums = {1,2,3};
		//第二种
		int[] nums2 = new int[3];
		//第三种
		int[] nums3 = new int[]{1,2,3};
		
		boolean booleanArray[] = new boolean[5]; 
		float floatArray[] = new float[5]; 
		System.out.println(nums2[2]);
		System.out.println(booleanArray[2]);
		System.out.println(floatArray[2]);
		
		//很基础的一个问题，面试了十几个人既然没有一个做对！ 
		String st1[]={"aa","bb","cc","dd","ee","ff"}; 
		String st2[]=st1; 
		st2[0]="00"; 
		System.out.println(st1[0]); 
		System.out.println(st2[0]); 
 	}
}
