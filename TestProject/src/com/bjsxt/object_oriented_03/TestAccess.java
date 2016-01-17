package com.bjsxt.object_oriented_03;

/**
 * 要深刻理解访问控制符private、default、protected和public的含义和使用
 * TestAccess
 * Author：jllin
 * 2013-9-29  下午10:00:43
 */
public class TestAccess {

	int b = 0;
	/**
	 * TestAccess.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-9-29 下午09:47:09
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class Person {
	private int i_private = 0;
			int j_default = 0;
	protected int k_protected = 0;
	public int m_public = 0;
	
	private void test() {
		System.out.println("Person");
	}
	
}
 
class Student extends Person {
	public void method() {
		/**
		 * 虽然Student继承了父类Person,拥有了Person的所有成员变量和方法，
		 * 但是对于private变量，或者方法，子类Student只有拥有权，没有使用权，
		 * 所以子类student对变量i_private和test()是看不见的，不能使用的
		 */
//		i_private = 1;
//		Person p = new Person();
//		p.test();
		j_default = 2;
		k_protected = 3;
		m_public = 4;
	}
}
