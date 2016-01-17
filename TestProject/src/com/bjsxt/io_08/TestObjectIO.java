package com.bjsxt.io_08;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;

public class TestObjectIO {

	/**
	 * ֪ʶ�㣺ѧϰʹ�ö����������л����Լ��ؼ���transientʹ�÷���
	 * TestObjectIO.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-28 ����11:39:48
	 */
	public static void main(String[] args) {
		T obj = new T();
		obj.k = 8;
		try {
			FileOutputStream fos = new FileOutputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\bjsxt\\io_08\\testobjectio.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			fos.close();
			
			FileInputStream fis = new FileInputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\test\\io\\testobjectio.dat");
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

//�����ȷȷʵʵ���������һ�������д��Ӳ���ϻ���д�������ϣ���������������л���һ���ֽ����������ʵ������ӿ�
//Serializable��һ����־�ԵĽӿڣ�������û�з�����ֻ�Ǵ���һ����־�����������������־�󣬾�֪������ʵ�����л���
class T implements java.io.Serializable {
	int i = 10;
	int j = 9;
	double d = 2.3;
	//transient,͸������˼����transient���εĳ�Ա�����������л���ʱ�򣬲��迼��
	//Ҳ����˵����Ӳ����д��ʱ��ֻд���������������k������д����ô�����������ʱ�����Ĭ�ϵ�ֵ0
	transient int k = 15;
}