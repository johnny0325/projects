package com.reflex;

/**
 * 
 * @ClassName: AssingableTest
 * @Description: ��������У��һ�����Ƿ�ʵ��ָ���ĸ���
 * @author johnny.lin
 * @date 2017��6��13�� ����6:33:16
 * @version V1.0
 */
public class TestAssingable
{

	public static void main(String[] args)
	{
		//Class.isAssignableFrom()�������ж�һ����Class1����һ����Class2�Ƿ���ͬ������һ����ĸ����ӿ�
		Class<?> parent = java.io.InputStream.class;    
        Class<?> child = java.io.FileInputStream.class;    
        System.out.println(parent.isAssignableFrom(child)); 
        System.out.println("String��Object�ĸ���:"+String.class.isAssignableFrom(Object.class));  
        System.out.println("Object��String�ĸ���:"+Object.class.isAssignableFrom(String.class));  
	}

}
