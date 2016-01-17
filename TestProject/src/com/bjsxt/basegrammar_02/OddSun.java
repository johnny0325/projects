package com.bjsxt.basegrammar_02;

public class OddSun {

	/**
	 * 练习：用一个for循环计算1+3+5+7+......+99的值，并输出结果。
	 * 其实就是求质数的和
	 * 小技巧：如果不小心按多了tab，你想往回缩，应该按shift+tab就可以
	 * OddSun.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-8-2 上午11:30:28
	 */
	public static void main(String[] args) {
		long result = 0;
		for(int i=1; i<=99; i+=2) {//注意：第三个表达式是i+=2,每次都递增2
			result += i;
		}
		System.out.println("result ==>"+result);
	}

}
