package com.test.interfaceclass;

/**
 * �������ܹ��Զ�����B��ʵ������bֱ�Ӹ�ֵ��A������ñ�����Ҳ���������ܹ��Զ�ת���ɸ������͡�
 * ���⣬�������ֱ�Ӵ���һ����B��ʵ�����󣬴��ݸ���Ҫ��A��ʵ��������������callA()������
 * �ڲ������ݵĹ����з�������ʽ�Զ�����ת���������ܹ��Զ�ת���ɸ���ĵ���ǳ�������⡣
 * A
 * Author��jlLin
 * Aug 29, 2011  12:20:24 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class A {
    public void func1() {
         System.out.println("A func1 is calling.");
    }
    public void func2() {
         //func1();
    	 System.out.println("A func2 is calling.");
    }
}

class B extends A {
    public void func1() {
         System.out.println("B func1 is calling.");         
    }
    public void func3() {
         System.out.println("B func3 is calling.");
    }
}

public class C {
	public static void main(String[] args) {
        B b = new B();
        A a = b;
        callA(a);
        callA(new B());
   }
   public static void callA(A a) {
        a.func1();
        a.func2();
   }
}
