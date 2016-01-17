package com.interview;

import java.util.Scanner;


/**
 * 题目：从键盘输入一行字符，分别统计出其中的英文字母、空格、数字和其他字符的个数。
 * 时间：2013.5.21，笔试公司：实信达科技有限公司
 * Statistics
 * Author：jllin
 * 2013-5-21  下午11:00:33
 */
public class Statistics {
	
	/**
	 * Statistics.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-5-21 下午11:00:25
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入字符:");
		String str = sc.nextLine();
		
		int englishCount = 0;	//英文字母统计
		int spaceCount = 0;		//空格统计
		int numberCount = 0;	//数字统计
		int otherCount = 0;		//其他字符统计
		
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
		
		System.out.println("英文字母统计:"+englishCount);
		System.out.println("数字统计:"+numberCount);
		System.out.println("空格统计:"+spaceCount);
		System.out.println("其他字符统计:"+otherCount);
	}

}
