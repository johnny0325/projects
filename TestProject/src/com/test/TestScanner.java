package com.test;

import java.util.Scanner;

/**
 * 从键盘读取数据，可以使用Scanner类,它在java.util包中,这个类提供了读取基本数据类型数据的方法
 * TestScanner
 * Author：jlLin
 * Aug 27, 2011  1:59:36 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestScanner {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 27, 2011 1:59:22 PM
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("请输入姓名：");
		String name = input.next();
		System.out.println("请输入年龄：");
		int age = input.nextInt();
	}

}
