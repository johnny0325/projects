package com.reflex;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public class TestReflex implements Serializable
{

	public static void main(String[] args) throws Exception
	{
		//1.通过一个对象获得完整的包名和类名
		TestReflex testReflex = new TestReflex();
		System.out.println(testReflex.getClass().getName());//结果 com.reflex.TestReflex

		//2.实例化Class对象
		Class<?> class1 = null;
		Class<?> class2 = null;
		Class<?> class3 = null;
		//一般采用这种形式
		class1 = Class.forName("com.reflex.TestReflex");
		class2 = new TestReflex().getClass();
		class3 = TestReflex.class;
		System.out.println("类名称："+class1.getName());
		System.out.println("类名称："+class2.getName());
		System.out.println("类名称："+class3.getName());
		
		//3.获取一个对象的父类与实现的接口
		Class<?> clazz = Class.forName("com.reflex.TestReflex");
		//取得父类
		Class<?> parentClass = clazz.getSuperclass();
		System.out.println("clazz的父类为："+parentClass.getName());
		// clazz的父类为： java.lang.Object
		
		//获取所有的接口
		Class<?> intes[] = clazz.getInterfaces();
		System.out.println("clazz实现的接口有：");
		for (int i=0; i<intes.length; i++)
		{
			System.out.println((i+1) + ": "+intes[i].getName());
		}
		// clazz实现的接口有：
        // 1：java.io.Serializable
		
		//4.通过反射机制实例化一个类的对象
		Class<?> class4 = Class.forName("com.reflex.User");
		//第一种方法，实例化默认构造方法，调用set赋值
		User user = (User)class4.newInstance();
		user.setAge(10);
		user.setName("Jason");
		System.out.println(user);
		//第二种方法，取得全部的构造函数，使用构造函数赋值
		Constructor<?> cons[] = class4.getConstructors();
		//查看每个构造方法需要的参数
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
