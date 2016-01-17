package com.example;

import java.util.Scanner;

/**
 * 测试是否是闰年
 * 有关闰年的了解：
 * 闰年条件是：一、能被4整除,而不能被100整除; 二、能被400整除; 符合一个就是闰年。如2000是闰年，而1900不是闰年 
 * 仔细看看，1900以上两个条件都不符合 
 * 通俗的说，就是四年一闰，百年不闰，四百年再闰。
 * Year
 * Author：jlLin
 * Aug 27, 2011  2:31:46 PM
 * Copyright 华仝九方科技有限公司
 */
public class Year {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 27, 2011 2:31:21 PM
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("请输入年份：");
		int year = input.nextInt();
		
		if((year%4==0 && year%100!=0)||year%400 ==0){
			System.out.println("是闰年!");
		}else{
			System.out.println("是平年!");
		}
	}

}
