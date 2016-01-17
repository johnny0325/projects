package com.test.interfaceclass;

/**
 * 编译器能够自动将类B的实例对象b直接赋值给A类的引用变量，也就是子类能够自动转换成父类类型。
 * 另外，程序可以直接创建一个类B的实例对象，传递给需要类A的实例对象作参数的callA()方法，
 * 在参数传递的过程中发生了隐式自动类型转换。子类能够自动转换成父类的道理非常容易理解。
 * A
 * Author：jlLin
 * Aug 29, 2011  12:20:24 AM
 * Copyright 华仝九方科技有限公司
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
