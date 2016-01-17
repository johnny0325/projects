package com.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ���飺Comparator��Comparable�������е�Ӧ��
 * Comparator��ʵ��������һ����:TestComparator.java
 * ����Ҫ����ļ��ϻ����鲻�ǵ�����������ʱ������ʹ��Comparator��Comparable,�Լ򵥵ķ�ʽʵ�ֶ���������Զ�������
 * TestComparable
 * Author��jllin
 * 2013-5-27  ����10:01:07
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
	 * Author��jllin
	 * 2013-5-26 ����10:59:45
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		String[] strArr;
		List<TestComparable> list = new ArrayList<TestComparable>();
		
		try {
			System.out.println("�밴���¸�ʽ������5��ѧ�������������ġ���ѧ��Ӣ��ĳɼ���");
			System.out.println("studentname,���ĳɼ�,��ѧ�ɼ�,Ӣ��ɼ�");
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
		//��������
		Collections.sort(list);
		
		for(int i = 0;i<list.size();i++) {
			TestComparable stu = (TestComparable)list.get(i);
			System.out.println(stu);
		}
	}
	
}
