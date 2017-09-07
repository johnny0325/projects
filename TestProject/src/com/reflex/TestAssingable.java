package com.reflex;

/**
 * 
 * @ClassName: AssingableTest
 * @Description: 可以用来校验一个类是否实现指定的父类
 * @author johnny.lin
 * @date 2017年6月13日 下午6:33:16
 * @version V1.0
 */
public class TestAssingable
{

	public static void main(String[] args)
	{
		//Class.isAssignableFrom()是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的父类或接口
		Class<?> parent = java.io.InputStream.class;    
        Class<?> child = java.io.FileInputStream.class;    
        System.out.println(parent.isAssignableFrom(child)); 
        System.out.println("String是Object的父类:"+String.class.isAssignableFrom(Object.class));  
        System.out.println("Object是String的父类:"+Object.class.isAssignableFrom(String.class));  
	}

}
