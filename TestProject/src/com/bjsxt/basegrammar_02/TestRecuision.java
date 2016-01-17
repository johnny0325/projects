package com.bjsxt.basegrammar_02;

public class TestRecuision {

	/**
	 * ���õݹ���ã��ڱ������о��������ģ�Ҫ��������
	 * TestRecuision.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-8-8 ����03:44:25
	 */
	public static void main(String[] args) {
		System.out.println(fibonacci2(5));
	}
	
	/**
	 * ��ĳ�����Ľ׳�
	 * �����˵���ݹ�ĵ��ù��̣�Ҫ��ͼ���
	 * TestRecuision.method1()
	 * @param n
	 * @return
	 * @return int
	 * Author��jllin
	 * 2013-8-8 ����03:46:59
	 */
	public static int method1(int n) {
		if(n == 1) {
			return 1;
		}else {
			return n*method1(n);
		}
	}
	
	/**
	 * ��fibonacci(쳲�����)���У�1,1,2,3,5,8,...��40������ֵ������������ƹ�ʽ��
	 * F(1)=1,F(2)=1,F(n)=F(n-1)+F(n-2)(n>2)
	 * ʹ�õݹ鷽��ʵ�����£�����ִ��������ο���fibonacciִ������ͼ.jpg
	 * TestRecuision.fibonacci()
	 * @param n
	 * @return
	 * @return int
	 * Author��jllin
	 * 2013-8-8 ����04:00:15
	 */
	public static int fibonacci(int n) {
		if(n ==1 || n ==2) {
			return 1;
		}else {
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}
	
	/**
	 * ʹ�÷ǵݹ�ķ�������fibonacci����
	 * TestRecuision.fibonacci2()
	 * @param index
	 * @return
	 * @return long
	 * Author��jllin
	 * 2013-8-8 ����05:11:39
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
