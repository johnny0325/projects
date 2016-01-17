package com.test;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class ChengFaBiao {
	
	/**
	 * 功能描述：打印九九乘法表
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 27, 2011 10:41:46 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=1;i<10;i++){
			for(int j=1;j<=i;j++){
				System.out.print(" "+j+"*"+i+"="+j*i);
			}
			System.out.println(" ");
		}
	}
}
