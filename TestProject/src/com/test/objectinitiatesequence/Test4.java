package com.test.objectinitiatesequence;

/**
 * 输出结果：
 * TestDefaultValue main() start...
   one4-2
   one4-3
   Two4's i==>0
	不仅第1次创建对象时，类中所有的静态变量要初始化，第1次访问类中的静态变量（没有创建对象）时，
	该类中所有的静态变量也要按照它们在类中排列的顺序初始化。 
 * One4
 * Author：jlLin
 * Aug 28, 2011  12:35:38 PM
 * Copyright 华仝九方科技有限公司
 */
class One4 {
	One4(String str){
		System.out.println(str);
	}
}

class Two4 {
	static int i = 0;
	One4 one4_1 = new One4("one4-1");
	static One4 one4_2 = new One4("one4-2");
	static One4 one4_3 = new One4("one4-3");
	
	Two4(String str){
		System.out.println(str);
	}
}
public class Test4 {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 12:24:07 PM
	 */
	public static void main(String[] args) {
		System.out.println("TestDefaultValue main() start..."); 
		System.out.println("Two4's i==>"+Two4.i); 
	}

}
