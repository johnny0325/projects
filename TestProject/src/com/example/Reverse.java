package com.example;

/**
 * ��Ŀ��дһ��������Ҫ�����int���ͣ��磺����һ��12345�����ؽ��54321�� 
 *  ���Ե�ʱ����������⣬���и�Ҫ���Ƿ������ڵĴ��벻�ܳ���8�У����һ�Ҫ�õݹ顣 
 * Reverse
 * Author��jlLin
 * Aug 24, 2011  11:01:47 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
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
