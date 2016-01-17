package com.bjsxt.object_oriented_03;

public class TestInherit {

	/**
	 * ����������ķ���������д������ӵ����ͬ�ı����������ǲ�����д�ģ���������඼���������
	 * TestInherit.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2014-5-7 ����08:53:26
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
		//getClass()����this��superӰ�죬�����ɵ�ǰ�������������
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
