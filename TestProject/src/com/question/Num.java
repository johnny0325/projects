package com.question;

public class Num {
	static void go(Integer x){  
        System.out.println("Integer");  
    }  
      
    static void go(long l){  
        System.out.println("l");  
    }  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
         Integer i1 = 1000;  
         Integer i2 = 1000;  
         Integer i3 = 10;  
         Integer i4 = 10;  
         System.out.println(String.valueOf(i1 == i2)+" "+ String.valueOf(i1.equals(i2)));  
         System.out.println(String.valueOf(i3 == i4)+" "+ String.valueOf(i3.equals(i4)));  
         int i = 5;  
         go(i);  
           
           
    }  
}
