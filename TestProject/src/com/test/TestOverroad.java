package com.test;

import java.util.ArrayList;
import java.util.List;

/**
 * ���췽������
 * �жϷ����Ƿ����ص������ж�����
 * 1 �������Ͳ�ͬ 2 ����������ͬ
 * �������ķ������Ͳ�����Ϊ�жϷ����Ƿ����ص�������
 * TestOverroad
 * Author��jlLin
 * Aug 28, 2011  3:59:29 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
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
		System.out.println("�������Ͳ�ͬ");
	}

	public void printNum(int a,int b){
		System.out.println("");
	}
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 3:24:18 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
