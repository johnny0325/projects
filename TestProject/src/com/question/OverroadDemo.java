package com.question;

import java.util.ArrayList;
import java.util.List;

/**
 * 疑问：根据判断方法是否重载的两个条件，这两个方法应该不是重载才对，为什么这里没有报错误呢？
 * 方法重载：方法名一样，参数列表不同，返回类型可以相同，也可以不同。
 * 2013.4.18重新理解，根据方法重载的定义，不能理解，这两个方法的参数类型是不一样的，虽然都是List，但是它们的数据类型是不一样的。
 * TestOverroad
 * Author：jlLin
 * Aug 28, 2011  4:03:10 PM
 * Copyright 华仝九方科技有限公司
 */
public class OverroadDemo {
	
	public void test(List<String> list){
		System.out.println("List<String>");
	}
	
	public ArrayList test(List<Object> list){
		System.out.println("List<Object>");
		return null;
	}
	
	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 28, 2011 4:02:44 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
