package com.test;

public class ChildClass extends FatherClass {
	public ChildClass() 
	{ 
	System.out.println("ChildClass Create"); 
	} 
	public static void main(String[] args) 
	{ 
	
	ChildClass cc = new ChildClass(); 
	FatherClass fc = new ChildClass(); 
	} 
}
