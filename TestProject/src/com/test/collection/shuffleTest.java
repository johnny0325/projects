package com.test.collection;

import java.util.ArrayList;
import java.util.Collections;

public class shuffleTest
{
	public static void main(String[] args)
	{
		// 为了避免获取相同的字符串，打乱集体元素的顺序
		String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		char[] ca = s.toCharArray();
		ArrayList<String> charList = new ArrayList<>();
		for (char c : ca)
		{
			charList.add(c + "");
		}
		// 打乱集体元素的顺序的方法,要求参数为一个集合
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
