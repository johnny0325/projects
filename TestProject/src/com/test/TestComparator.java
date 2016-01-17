package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sun.awt.DisplayChangedListener;

/**
 * 测试使用Comparator接口实现集合、数组的排序功能
 * Comparable和Comparator接口的区别：
 * 1：Comparable是在集合内部定义的方法实现的排序，Comparator是在集合外部实现的排序
 * 2：一个类实现了Camparable接口则表明这个类的对象之间是可以相互比较的，
 * 这个类对象组成的集合就可以直接使用sort方法排序。一般我们写的bean都要实现这一接口，这也是标准javabean的规范。
 * 3：Comparator可以看成一种算法的实现，将算法和数据分离，Comparator也可以在下面两种环境下使用：
 * 	3.1：类的设计师没有考虑到比较问题而没有实现Comparable，可以通过Comparator来实现排序而不必改变对象本身
 * 	3.2：可以使用多种排序标准，比如升序、降序等。
 * TestComparator
 * Author：jllin
 * 2013-6-2  上午10:08:40
 */
public class TestComparator {

	/**
	 * TestComparator.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-6-1 下午10:40:32
	 */
	public static void main(String[] args) {
		List<Employee> employees=new ArrayList<Employee>();
		employees.add(new Employee("Andy",21,2000));
		employees.add(new Employee("Felix",21,3000));
		employees.add(new Employee("Bill",35,20000));
		employees.add(new Employee("Helen",21,10000));
		employees.add(new Employee("Cindy",28,8000));
		employees.add(new Employee("Douglas",25,5000));
		
		//按姓名排序
		System.out.println("按姓名排序:");
		Collections.sort(employees, new NameComparator());
		display(employees);
		
		//按年龄排序
		System.out.println("按年龄排序:");
		Collections.sort(employees,new AgeComparator());
		display(employees);
		
		//按薪水排序
		System.out.println("按薪水排序:");
		Collections.sort(employees, new SalaryComparator());
		display(employees);
	}
	
	public static void display(List<Employee> employees){
		for(Employee emp : employees) {
			System.out.println("雇员名:"+emp.getName()+" 年龄:"+emp.getAge()+" 薪水:"+emp.getSalary());
		}
		System.out.println();
	}
	
	//定义静态内部类
	static class Employee {
		public String name;
		public int age;
		public int salary;
		
		public Employee(String name,int age,int salary) {
			this.name = name;
			this.age = age;
			this.salary = salary;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public int getSalary() {
			return salary;
		}
		public void setSalary(int salary) {
			this.salary = salary;
		}
	}
	
	//Comparator的具体实现类
	//按年龄排序
	static class AgeComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getAge() - eOp2.getAge();
		}
	}
	
	//按姓名排序
	static class NameComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getName().compareTo(eOp2.getName());
		}
	}
	
	//按薪水排序
	static class SalaryComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getSalary() - eOp2.getSalary();
		}
	}
}
