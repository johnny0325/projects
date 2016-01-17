package com.test;

import java.util.ArrayList;
import java.util.List;

/**
 * 考察方法重载
 * 判断方法是否重载的条件有二个：
 * 1 参数类型不同 2 参数个数不同
 * 而方法的返回类型不能作为判断方法是否重载的条件。
 * TestOverroad
 * Author：jlLin
 * Aug 28, 2011  3:59:29 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestOverroad {

	public void printNum(){
		System.out.println("");
	}
	
	public void printNum(int i){
		System.out.println("int's i==>"+i);
	}
	
	public void printNum(float f) throws Exception{
		System.out.println("float'f==>"+f);
	}
	
	public void printNum(int i,double d){
		System.out.println("i==>"+i+" d==>"+d);
	}
	
	public void printNum(double d,int i){
		System.out.println("参数类型不同");
	}

	public void printNum(int a,int b){
		System.out.println("");
	}
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 3:24:18 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
