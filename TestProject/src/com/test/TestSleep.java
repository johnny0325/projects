package com.test;

public class TestSleep {
	public static void main(String args[]){
		System.out.println("I'am going to bed!");
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("I wake up!");
	}
}
