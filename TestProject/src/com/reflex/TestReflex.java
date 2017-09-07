package com.reflex;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public class TestReflex implements Serializable
{

	public static void main(String[] args) throws Exception
	{
		//1.ͨ��һ�������������İ���������
		TestReflex testReflex = new TestReflex();
		System.out.println(testReflex.getClass().getName());//��� com.reflex.TestReflex

		//2.ʵ����Class����
		Class<?> class1 = null;
		Class<?> class2 = null;
		Class<?> class3 = null;
		//һ�����������ʽ
		class1 = Class.forName("com.reflex.TestReflex");
		class2 = new TestReflex().getClass();
		class3 = TestReflex.class;
		System.out.println("�����ƣ�"+class1.getName());
		System.out.println("�����ƣ�"+class2.getName());
		System.out.println("�����ƣ�"+class3.getName());
		
		//3.��ȡһ������ĸ�����ʵ�ֵĽӿ�
		Class<?> clazz = Class.forName("com.reflex.TestReflex");
		//ȡ�ø���
		Class<?> parentClass = clazz.getSuperclass();
		System.out.println("clazz�ĸ���Ϊ��"+parentClass.getName());
		// clazz�ĸ���Ϊ�� java.lang.Object
		
		//��ȡ���еĽӿ�
		Class<?> intes[] = clazz.getInterfaces();
		System.out.println("clazzʵ�ֵĽӿ��У�");
		for (int i=0; i<intes.length; i++)
		{
			System.out.println((i+1) + ": "+intes[i].getName());
		}
		// clazzʵ�ֵĽӿ��У�
        // 1��java.io.Serializable
		
		//4.ͨ���������ʵ����һ����Ķ���
		Class<?> class4 = Class.forName("com.reflex.User");
		//��һ�ַ�����ʵ����Ĭ�Ϲ��췽��������set��ֵ
		User user = (User)class4.newInstance();
		user.setAge(10);
		user.setName("Jason");
		System.out.println(user);
		//�ڶ��ַ�����ȡ��ȫ���Ĺ��캯����ʹ�ù��캯����ֵ
		Constructor<?> cons[] = class4.getConstructors();
		//�鿴ÿ�����췽����Ҫ�Ĳ���
		for (int i=0; i<cons.length; i++)
		{
			
		}
		
	}
	
}

class User {
	private int age;
	private String name;
	public User(){
		super();
	}
	
	public User(String name) {
		super();
		this.name = name;
	}
	
	public User(int age, String name) {
		super();
		this.age = age;
		this.name = name;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "User [age="+age+", name="+name+"]";
	}
}
