package com.test.xstream;

import java.sql.Date;

import com.thoughtworks.xstream.XStream;

public class TestXStream {
	public static void  main(String[] args) {
		XStream xStream = new XStream();
		Student student = new Student();
		student.setId(1);
		student.setName("jack");
		student.setEmail("ljun0325@sina.com");
		student.setAddress("广州市天河区天河北路时代广场");
		student.setBirthday(new Date(123));
		
		//为输出的类重命名，如果不重命名，那么根结点连包路径也显示出来
		xStream.alias("student", Student.class);
		System.out.println(xStream.toXML(student));
	}
}

class Student {
	private int id;
	private String name;
	private String email;
	private String address;
	private Date birthday;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String toString() {
		return this.name + "#" + this.id + "#" + this.address + "#" + this.birthday + "#" + this.email;
	}
}
