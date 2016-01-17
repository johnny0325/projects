package com.test;

/**
 * String s = new String("xyz");创建了几个String Object?
 * 正确答案是:2个。
 * 解答："xyz "作为一个常量字符串首先被创建，它被保存在“字符串池”中，而且字符串池中只可能只有一个“xyz”，但是堆中可能有多个，
 * 因为你这里用了new String来强制在堆中再创建了一个对象，所以是两个哈；如果是String s= "xyz"这样的，s这个引用就会直
 * 接指向常量池的"xyz",而不会是指向你在堆中创建的“xyz”。另外，s只是一个引用，它指向了在堆中创建的“xyz”。
 * 参考文章：http://www.iteye.com/topic/774673
 * TestString3
 * Author：jlLin
 * Aug 20, 2011  3:12:27 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestString3 {
	String s = new String("xyz");
}
