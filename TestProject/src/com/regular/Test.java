package com.regular;

public class Test
{

	public static void main(String[] args)
	{
		String tradeTimeStr = "12.32222";
		String newStr = tradeTimeStr.replaceAll("\\.[0-9]{1,}", "");
		System.out.println(newStr);
	}

}
