package com.test;

/**
 * try、catch、finally 的执行顺序问题
 * 测试try后的finally {}里的code会不会被执行，什么时候被执行，在return前还是后?
 * 答案:会执行，在return前执行
 * 
 * 我们可以看出，其实 finally 语句块是在 try 或者 catch 中的 return 语句之前执行的。
 * 更加一般的说法是，finally 语句块应该是在控制转移语句之前执行，控制转移语句除了 return 外，还有 break 和 continue。
 * 另外，throw 语句也属于控制转移语句。虽然 return、throw、break 和 continue 都是控制转移语句，但是它们之间是有区别的。
 * 其中 return 和 throw 把程序控制权转交给它们的调用者（invoker），而 break 和 continue 的控制权是在当前方法内转移。
 * 
 * TestFinally
 * Author：jlLin
 * Aug 23, 2011  6:44:48 PM
 * Copyright 华仝九方科技有限公司
 */
public class TestFinally {
	public static void main(String args[]){
		TestFinally tf = new TestFinally();
		System.out.println(tf.test1());
		System.out.println();
		System.out.println(tf.test2());;
	}
	
	/**
	 * 说明 finally 语句块在 try 语句块中的 return 语句之前执行。
	 * TestFinally.test1()
	 * @return
	 * @return String
	 * Author：jllin
	 * 2013-4-20 下午05:42:06
	 */
	public String test1(){
		try{
			System.out.println("test1 try block");
			return "test1 return...";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.out.println("test1 finally block");
		}
		return "";
	}
	
	/**
	 * 说明了 finally 语句块在 catch 语句块中的 return 语句之前执行
	 * TestFinally.test2()
	 * @return
	 * @return String
	 * Author：jllin
	 * 2013-4-20 下午05:42:15
	 */
	public String test2(){
		int i=1;
		try{
			System.out.println("test2 try block");
			i=1/0;
			return "1";
		}catch(Exception e){
			System.out.println("test2 exception block");
			return "test2 2";
		}finally{
			System.out.println("test2 finally block");
		}
	}
}
