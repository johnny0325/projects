package com.bjsxt.basegrammar_02;

public class TestBreak1 {

	/**
	 * 循环语句举例
	 * 输了1~100内前5个可以被3整除的数
	 * TestBreak1.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-8-4 下午02:12:10
	 */
	public static void main(String[] args) {
		int num = 0,i = 1;
		while(i <= 100) {
			if(i % 3 == 0) {
				System.out.println(i + " ");
				num++;
			}
			if(num == 5) {
				break;//跳出循环
			}
			i++;
		}
	}

}
