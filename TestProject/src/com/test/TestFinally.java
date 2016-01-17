package com.test;

/**
 * try��catch��finally ��ִ��˳������
 * ����try���finally {}���code�᲻�ᱻִ�У�ʲôʱ��ִ�У���returnǰ���Ǻ�?
 * ��:��ִ�У���returnǰִ��
 * 
 * ���ǿ��Կ�������ʵ finally �������� try ���� catch �е� return ���֮ǰִ�еġ�
 * ����һ���˵���ǣ�finally ����Ӧ�����ڿ���ת�����֮ǰִ�У�����ת�������� return �⣬���� break �� continue��
 * ���⣬throw ���Ҳ���ڿ���ת����䡣��Ȼ return��throw��break �� continue ���ǿ���ת����䣬��������֮����������ġ�
 * ���� return �� throw �ѳ������Ȩת�������ǵĵ����ߣ�invoker������ break �� continue �Ŀ���Ȩ���ڵ�ǰ������ת�ơ�
 * 
 * TestFinally
 * Author��jlLin
 * Aug 23, 2011  6:44:48 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class TestFinally {
	public static void main(String args[]){
		TestFinally tf = new TestFinally();
		System.out.println(tf.test1());
		System.out.println();
		System.out.println(tf.test2());;
	}
	
	/**
	 * ˵�� finally ������ try �����е� return ���֮ǰִ�С�
	 * TestFinally.test1()
	 * @return
	 * @return String
	 * Author��jllin
	 * 2013-4-20 ����05:42:06
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
	 * ˵���� finally ������ catch �����е� return ���֮ǰִ��
	 * TestFinally.test2()
	 * @return
	 * @return String
	 * Author��jllin
	 * 2013-4-20 ����05:42:15
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
