package com.test.collection;

import java.util.ArrayList;
import java.util.Collections;

public class shuffleTest
{
	public static void main(String[] args)
	{
		// Ϊ�˱����ȡ��ͬ���ַ��������Ҽ���Ԫ�ص�˳��
		String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		char[] ca = s.toCharArray();
		ArrayList<String> charList = new ArrayList<>();
		for (char c : ca)
		{
			charList.add(c + "");
		}
		// ���Ҽ���Ԫ�ص�˳��ķ���,Ҫ�����Ϊһ������
		Collections.shuffle(charList);
		StringBuffer buffer = new StringBuffer();
		for (String str : charList)
		{
			buffer.append(str);
		}

		String element = buffer.toString();
		System.out.println(element);
	}
}
