package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sun.awt.DisplayChangedListener;

/**
 * ����ʹ��Comparator�ӿ�ʵ�ּ��ϡ������������
 * Comparable��Comparator�ӿڵ�����
 * 1��Comparable���ڼ����ڲ�����ķ���ʵ�ֵ�����Comparator���ڼ����ⲿʵ�ֵ�����
 * 2��һ����ʵ����Camparable�ӿ�����������Ķ���֮���ǿ����໥�Ƚϵģ�
 * ����������ɵļ��ϾͿ���ֱ��ʹ��sort��������һ������д��bean��Ҫʵ����һ�ӿڣ���Ҳ�Ǳ�׼javabean�Ĺ淶��
 * 3��Comparator���Կ���һ���㷨��ʵ�֣����㷨�����ݷ��룬ComparatorҲ�������������ֻ�����ʹ�ã�
 * 	3.1��������ʦû�п��ǵ��Ƚ������û��ʵ��Comparable������ͨ��Comparator��ʵ����������ظı������
 * 	3.2������ʹ�ö��������׼���������򡢽���ȡ�
 * TestComparator
 * Author��jllin
 * 2013-6-2  ����10:08:40
 */
public class TestComparator {

	/**
	 * TestComparator.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-6-1 ����10:40:32
	 */
	public static void main(String[] args) {
		List<Employee> employees=new ArrayList<Employee>();
		employees.add(new Employee("Andy",21,2000));
		employees.add(new Employee("Felix",21,3000));
		employees.add(new Employee("Bill",35,20000));
		employees.add(new Employee("Helen",21,10000));
		employees.add(new Employee("Cindy",28,8000));
		employees.add(new Employee("Douglas",25,5000));
		
		//����������
		System.out.println("����������:");
		Collections.sort(employees, new NameComparator());
		display(employees);
		
		//����������
		System.out.println("����������:");
		Collections.sort(employees,new AgeComparator());
		display(employees);
		
		//��нˮ����
		System.out.println("��нˮ����:");
		Collections.sort(employees, new SalaryComparator());
		display(employees);
	}
	
	public static void display(List<Employee> employees){
		for(Employee emp : employees) {
			System.out.println("��Ա��:"+emp.getName()+" ����:"+emp.getAge()+" нˮ:"+emp.getSalary());
		}
		System.out.println();
	}
	
	//���徲̬�ڲ���
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
	
	//Comparator�ľ���ʵ����
	//����������
	static class AgeComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getAge() - eOp2.getAge();
		}
	}
	
	//����������
	static class NameComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getName().compareTo(eOp2.getName());
		}
	}
	
	//��нˮ����
	static class SalaryComparator implements Comparator {
		public int compare(Object op1,Object op2) {
			Employee eOp1 = (Employee)op1;
			Employee eOp2 = (Employee)op2;
			
			return eOp1.getSalary() - eOp2.getSalary();
		}
	}
}
