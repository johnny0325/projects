package com.test;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import com.sun.corba.se.spi.protocol.ForwardException;

/**
 * ���������Ķ��帳ֵ���Լ�8���ƣ�16���Ƶ�������ʾ
 * TestInt
 * Author��jlLin
 * Aug 27, 2011  1:01:16 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestInt {
	public static void main(String[] args)
	{
	System.out.println("��������-----��ʼ!");
	byte a = -8;
	short b = 1000;
	int c = 50000;
	long d = 99999999999999L;
	int e = 0x234; //16����
	int f = 012;   //8����,ǰ���������"0"����������ĸo
	int g = 0x22;  //16����
	System.out.println("a="+a);
	System.out.println("b="+b);
	System.out.println("c="+c);
	System.out.println("d="+d);
	System.out.println("e="+e);
	System.out.println("f="+f);
	System.out.println("g="+g);
	System.out.println("��������-----��ʼ!");
	
	
	
	}
}
