package com.bjsxt.basegrammar_02;

public class TestRecuision {

	/**
	 * 常用递归调用，在笔试题中经常考到的，要熟练掌握
	 * TestRecuision.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-8-8 下午03:44:25
	 */
	public static void main(String[] args) {
		System.out.println(fibonacci2(5));
	}
	
	/**
	 * 求某个数的阶乘
	 * 形象地说明递归的调用过程，要画图理解
	 * TestRecuision.method1()
	 * @param n
	 * @return
	 * @return int
	 * Author：jllin
	 * 2013-8-8 下午03:46:59
	 */
	public static int method1(int n) {
		if(n == 1) {
			return 1;
		}else {
			return n*method1(n);
		}
	}
	
	/**
	 * 求fibonacci(斐波那契)数列：1,1,2,3,5,8,...第40个数的值。数列满足递推公式：
	 * F(1)=1,F(2)=1,F(n)=F(n-1)+F(n-2)(n>2)
	 * 使用递归方法实现如下，具体执行流程请参考：fibonacci执行流程图.jpg
	 * TestRecuision.fibonacci()
	 * @param n
	 * @return
	 * @return int
	 * Author：jllin
	 * 2013-8-8 下午04:00:15
	 */
	public static int fibonacci(int n) {
		if(n ==1 || n ==2) {
			return 1;
		}else {
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}
	
	/**
	 * 使用非递归的方法，求fibonacci数列
	 * TestRecuision.fibonacci2()
	 * @param index
	 * @return
	 * @return long
	 * Author：jllin
	 * 2013-8-8 下午05:11:39
	 */
	public static long fibonacci2(int index) {
		if(index ==1 || index ==2) {
			return 1;
		}
		
		long f1 = 1L;
		long f2 = 1L;
		long result = 0;
		for(int n=0; n<index-2; n++) {
			result = f1+f2;
			f1 = f2;
			f2 = result;
		}
		return result;
	}
}
