package com.bjsxt.io_08;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;

public class TestObjectIO {

	/**
	 * 知识点：学习使用对象流、序列化，以及关键字transient使用方法
	 * TestObjectIO.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-28 下午11:39:48
	 */
	public static void main(String[] args) {
		T obj = new T();
		obj.k = 8;
		try {
			FileOutputStream fos = new FileOutputStream("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\testobjectio.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			fos.close();
			
			FileInputStream fis = new FileInputStream("E:\\项目源程序\\TestProject\\src\\com\\test\\io\\testobjectio.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			T readObj = (T)ois.readObject();
			System.out.println("T's i = "+readObj.i+", j = "+readObj.j+", d = "+readObj.d+", k = "+readObj.k);
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

//如果你确确实实想把这样的一个类对象，写到硬盘上或者写到网络上，就是你想把它序列化成一个字节流，必须得实现这个接口
//Serializable是一个标志性的接口，它自身没有方法，只是打了一个标志，编译器遇到这个标志后，就知道它是实现序列化的
class T implements java.io.Serializable {
	int i = 10;
	int j = 9;
	double d = 2.3;
	//transient,透明的意思，被transient修饰的成员变量，在序列化的时候，不予考虑
	//也就是说，往硬盘上写的时候，只写上面的三个变量，k变量不写，那么读对象出来的时候就是默认的值0
	transient int k = 15;
}