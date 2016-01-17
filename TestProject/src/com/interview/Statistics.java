package com.interview;

import java.util.Scanner;


/**
 * ��Ŀ���Ӽ�������һ���ַ����ֱ�ͳ�Ƴ����е�Ӣ����ĸ���ո����ֺ������ַ��ĸ�����
 * ʱ�䣺2013.5.21�����Թ�˾��ʵ�Ŵ�Ƽ����޹�˾
 * Statistics
 * Author��jllin
 * 2013-5-21  ����11:00:33
 */
public class Statistics {
	
	/**
	 * Statistics.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-5-21 ����11:00:25
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("�������ַ�:");
		String str = sc.nextLine();
		
		int englishCount = 0;	//Ӣ����ĸͳ��
		int spaceCount = 0;		//�ո�ͳ��
		int numberCount = 0;	//����ͳ��
		int otherCount = 0;		//�����ַ�ͳ��
		
		char[] c = str.toCharArray();
		for(int i = 0;i<c.length;i++){
			if(c[i]>= 'a' && c[i]<='z' || c[i]>= 'A' && c[i]<='Z') {
				englishCount += 1;
			}else if(c[i] >= '0' && c[i]<= '9') {
				numberCount += 1;
			}else if (c[i] == ' ') {
				spaceCount += 1;
			}else {
				otherCount += 1;
			}
		}
		
		System.out.println("Ӣ����ĸͳ��:"+englishCount);
		System.out.println("����ͳ��:"+numberCount);
		System.out.println("�ո�ͳ��:"+spaceCount);
		System.out.println("�����ַ�ͳ��:"+otherCount);
	}

}
