package com.welab.x.test.util;

public class StringUtil
{
	/**
     * 判断字符串是否为空
     * @param str 字符串
     * @return 判断结果
     */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() <= 0) {
			return true;
		}
		return false;
	}
}	
