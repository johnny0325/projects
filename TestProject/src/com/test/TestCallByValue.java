package com.test;

import com.sun.swing.internal.plaf.basic.resources.basic;

/**
 * 测试值传递，请参考com.test下的文件：java参数传递(经典).doc
 * TestCallByValue
 * Author：jlLin
 * Aug 27, 2011  12:31:00 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestCallByValue {
	
	public static void main(String args[]){
		//范例一
		String str = "12345678";
		change(str);
		System.out.println(str);
		System.out.println(str.substring(2,4));
		
		//范例二
		 StringBuffer sb = new StringBuffer("Hello ");         
		 System.out.println("Before change, sb = " + sb);         
		 changeData(sb);         
		 System.out.println("After changeData(n), sb = " + sb);
		 
		//范例三
		 StringBuffer a = new StringBuffer("A");      
		 StringBuffer b = new StringBuffer("B");
		 System.out.println("Before change, a = " + a);         
		 changeData(a,b );         
		 //a变了，但是b还是没有变的，仍然是B
		 System.out.println(a +","+b);
	}

	public static void change(String str){
		str = "welcome";
	}
	
	public static void changeData(StringBuffer strBuf) {
		strBuf = new StringBuffer("Hi ");            
		strBuf.append("World!");     
	}
	
	public static void changeData(StringBuffer x,StringBuffer y){
		x.append(y);
		y = x;
	}
}
