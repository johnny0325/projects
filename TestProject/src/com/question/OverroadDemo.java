package com.question;

import java.util.ArrayList;
import java.util.List;

/**
 * ���ʣ������жϷ����Ƿ����ص���������������������Ӧ�ò������زŶԣ�Ϊʲô����û�б������أ�
 * �������أ�������һ���������б�ͬ���������Ϳ�����ͬ��Ҳ���Բ�ͬ��
 * 2013.4.18������⣬���ݷ������صĶ��壬������⣬�����������Ĳ��������ǲ�һ���ģ���Ȼ����List���������ǵ����������ǲ�һ���ġ�
 * TestOverroad
 * Author��jlLin
 * Aug 28, 2011  4:03:10 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class OverroadDemo {
	
	public void test(List<String> list){
		System.out.println("List<String>");
	}
	
	public ArrayList test(List<Object> list){
		System.out.println("List<Object>");
		return null;
	}
	
	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 28, 2011 4:02:44 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
