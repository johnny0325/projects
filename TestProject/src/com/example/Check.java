package com.example;

import java.util.Arrays;

public class Check {

	/**
	 * �����������ж������������������Ƿ����
	 * @param args
	 * @return void
	 * Author��ljLin
	 * Mar 17, 2011 12:29:24 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long[] a = {1,2,3,4,5};
        long[] b = {5,4,3,2,1};
        int counts = 0;
        Arrays.sort(a);
        Arrays.sort(b);//�����������Ƚ�����������
        int length1 = a.length;
        int length2 = b.length;
        if(length1 < length2) {
            for(int i = 0;i<length1;i++) {
                if(a[i]==b[i])
                    continue;
                else {
                    System.out.println("�������鲻���1��");
                }
            }
            System.out.println("��������Ԫ�����2��");
        }
        else {
            for(int i = 0;i<length2;i++) {
                if(a[i]==b[i]){
                	++counts;
                	continue;
                }else {
                    System.out.println("�������鲻���3��");
                   
                }
            }
            System.out.println("��������Ԫ�����4��==>"+counts);
        }

	}

}
