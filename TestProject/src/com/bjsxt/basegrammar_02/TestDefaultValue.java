package com.bjsxt.basegrammar_02;

/**
 * 测试下面这几种基本类型的数据作为成员变量时的默认值是多少
 * TestDefaultValue
 * Author：jllin
 * 2013-8-11  下午09:29:25
 */
public class TestDefaultValue {
	private char c = 3;
	private boolean flag;
	private float f;
	private double d;
	/**
	 * TestDefaultValue.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-8-11 下午09:06:47
	 */
	public static void main(String[] args) {
		TestDefaultValue t = new TestDefaultValue();
		System.out.println(t.c);
		System.out.println(t.flag);
		System.out.println(t.f);
		System.out.println(t.d);
	}

}
