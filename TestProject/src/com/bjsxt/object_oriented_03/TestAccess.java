package com.bjsxt.object_oriented_03;

/**
 * Ҫ��������ʿ��Ʒ�private��default��protected��public�ĺ����ʹ��
 * TestAccess
 * Author��jllin
 * 2013-9-29  ����10:00:43
 */
public class TestAccess {

	int b = 0;
	/**
	 * TestAccess.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-9-29 ����09:47:09
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
		 * ��ȻStudent�̳��˸���Person,ӵ����Person�����г�Ա�����ͷ�����
		 * ���Ƕ���private���������߷���������Studentֻ��ӵ��Ȩ��û��ʹ��Ȩ��
		 * ��������student�Ա���i_private��test()�ǿ������ģ�����ʹ�õ�
		 */
//		i_private = 1;
//		Person p = new Person();
//		p.test();
		j_default = 2;
		k_protected = 3;
		m_public = 4;
	}
}
