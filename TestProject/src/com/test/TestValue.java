package com.test;

/**
 * 测试递增++、递减--运算符以及do...while()结构的使用
 * TestValue
 * Author：jlLin
 * Sep 1, 2011  5:58:17 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestValue {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Sep 1, 2011 5:48:47 PM
	 */
	public static void main(String[] args) {
		//要注意观察两个方法体里面不同的地方
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
				//当i=5的时候，不符合while<5的条件，循环结束，j=6
				continue;
			}
		}while(i<5);
		System.out.println("i==>"+i+" j==>"+j);
	}
}
