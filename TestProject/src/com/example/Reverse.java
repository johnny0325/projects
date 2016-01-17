package com.example;

/**
 * 题目：写一个方法，要求参数int类型，如：传入一个12345，返回结果54321。 
 *  面试的时候遇到这个题，还有个要求，是方法体内的代码不能超过8行，而且还要用递归。 
 * Reverse
 * Author：jlLin
 * Aug 24, 2011  11:01:47 PM
 * Copyright 华仝九方科技有限公司
 */
public class Reverse {
	public static void main(String[] args) {  
        int i = 1234567890;  
        new Reverse().reverse(i);  
    }  
  
    private void reverse(int integer) {  
        String str = String.valueOf(integer);  
        System.out.print(str.substring(str.length() - 1));  
        if (str.length() > 1) {  
            String s = str.substring(0, str.length() - 1);  
            this.reverse(Integer.parseInt(s));  
        }  
    }  
}
