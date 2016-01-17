package com.test;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import com.sun.corba.se.spi.protocol.ForwardException;

/**
 * 测试整数的定义赋值，以及8进制，16进制的整数表示
 * TestInt
 * Author：jlLin
 * Aug 27, 2011  1:01:16 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestInt {
	public static void main(String[] args)
	{
	System.out.println("测试整数-----开始!");
	byte a = -8;
	short b = 1000;
	int c = 50000;
	long d = 99999999999999L;
	int e = 0x234; //16进制
	int f = 012;   //8进制,前面的是数字"0"，而不是字母o
	int g = 0x22;  //16进制
	System.out.println("a="+a);
	System.out.println("b="+b);
	System.out.println("c="+c);
	System.out.println("d="+d);
	System.out.println("e="+e);
	System.out.println("f="+f);
	System.out.println("g="+g);
	System.out.println("测试整数-----开始!");
	
	
	
	}
}
