package com.test;

import com.sun.swing.internal.plaf.basic.resources.basic;

/**
 * ����ֵ���ݣ���ο�com.test�µ��ļ���java��������(����).doc
 * TestCallByValue
 * Author��jlLin
 * Aug 27, 2011  12:31:00 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestCallByValue {
	
	public static void main(String args[]){
		//����һ
		String str = "12345678";
		change(str);
		System.out.println(str);
		System.out.println(str.substring(2,4));
		
		//������
		 StringBuffer sb = new StringBuffer("Hello ");         
		 System.out.println("Before change, sb = " + sb);         
		 changeData(sb);         
		 System.out.println("After changeData(n), sb = " + sb);
		 
		//������
		 StringBuffer a = new StringBuffer("A");      
		 StringBuffer b = new StringBuffer("B");
		 System.out.println("Before change, a = " + a);         
		 changeData(a,b );         
		 //a���ˣ�����b����û�б�ģ���Ȼ��B
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
