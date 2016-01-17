package com.bjsxt.object_oriented_03;

public class TestInherit {

	/**
	 * 父类与子类的方法可以重写，但是拥有相同的变量，变量是不会重写的，父类和子类都有这个变量
	 * TestInherit.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2014-5-7 下午08:53:26
	 */
	public static void main(String[] args) {
		ChildClass cc = new ChildClass();
		cc.f();
	}

}

class FatherClass {
	public int value;
	public void f() {
		value = 100;
		System.out.println("FatherClass.value="+value);
		System.out.println(this.value);
		//getClass()不受this和super影响，而是由当前的运行类决定的
		System.out.println(this.getClass());
	}
}

class ChildClass extends FatherClass {
	public int value;
	public void f() {
		value = 200;
		super.f();
		
		System.out.println("ChildClass.value="+value);
		System.out.println(super.value);
	}
}
