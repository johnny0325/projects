package com.example;

import java.util.Arrays;

public class Check {

	/**
	 * 功能描述：判断两个整形数组数据是否相等
	 * @param args
	 * @return void
	 * Author：ljLin
	 * Mar 17, 2011 12:29:24 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long[] a = {1,2,3,4,5};
        long[] b = {5,4,3,2,1};
        int counts = 0;
        Arrays.sort(a);
        Arrays.sort(b);//将两个数组先进行升序排序
        int length1 = a.length;
        int length2 = b.length;
        if(length1 < length2) {
            for(int i = 0;i<length1;i++) {
                if(a[i]==b[i])
                    continue;
                else {
                    System.out.println("两个数组不相等1！");
                }
            }
            System.out.println("两个数组元素相等2！");
        }
        else {
            for(int i = 0;i<length2;i++) {
                if(a[i]==b[i]){
                	++counts;
                	continue;
                }else {
                    System.out.println("两个数组不相等3！");
                   
                }
            }
            System.out.println("两个数组元素相等4！==>"+counts);
        }

	}

}
