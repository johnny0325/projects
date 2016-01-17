package com.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 考查：Comparator和Comparable在排序中的应用
 * Comparator的实现在另外一个类:TestComparator.java
 * 当需要排序的集合或数组不是单纯的数字型时，可以使用Comparator或Comparable,以简单的方式实现对象排序或自定义排序。
 * TestComparable
 * Author：jllin
 * 2013-5-27  下午10:01:07
 */
public class TestComparable implements Comparable{
	String name;
	int yuwen,shuxue,english;
	int sum;
	
	public TestComparable(String name,int yuwen,int shuxue,int english,int sum) {
		this.name = name;
		this.yuwen = yuwen;
		this.shuxue = shuxue;
		this.english = english;
		this.sum = sum;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getYuwen() {
		return yuwen;
	}
	public void setYuwen(int yuwen) {
		this.yuwen = yuwen;
	}
	public int getShuxue() {
		return shuxue;
	}
	public void setShuxue(int shuxue) {
		this.shuxue = shuxue;
	}
	public int getEnglish() {
		return english;
	}
	public void setEnglish(int english) {
		this.english = english;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int compareTo(Object o){
		TestComparable student = (TestComparable)o;
		return this.sum - student.getSum();
	}
	
	public String toString(){
		return this.name+":"+this.sum;
	}
	/**
	 * TestComparable.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-5-26 下午10:59:45
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		String[] strArr;
		List<TestComparable> list = new ArrayList<TestComparable>();
		
		try {
			System.out.println("请按以下格式，输入5个学生的姓名，语文、数学、英语的成绩：");
			System.out.println("studentname,语文成绩,数学成绩,英语成绩");
			while((line = br.readLine()) != null) {
				
				if(line.equals("over")){
					break;
				}else{
					strArr = line.split(",");
					System.out.println(Arrays.toString(strArr));
					System.out.println();
					TestComparable student = new TestComparable(
							strArr[0], Integer.parseInt(strArr[1]), Integer.parseInt(strArr[2]), 
							Integer.parseInt(strArr[3]), 
							Integer.parseInt(strArr[1])+Integer.parseInt(strArr[2])+Integer.parseInt(strArr[3])
					);
					list.add(student);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//进行排序
		Collections.sort(list);
		
		for(int i = 0;i<list.size();i++) {
			TestComparable stu = (TestComparable)list.get(i);
			System.out.println(stu);
		}
	}
	
}
