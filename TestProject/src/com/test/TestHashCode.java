package com.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @ClassName: TestHashCode
 * @Description:  [��ϣ�㷨��String���е�Ӧ��]
 * 				  String����д��Object���equals()������hashcode()������
 * 				  String���hashcodeֵ����ϣֵ������μ���õ��ģ�����ʵ�֣�Ϊ�˷����Ķ������������зֲ�˵��
 * 				  ͨ������Ĳ��Է��������ǿ��Ժ������Ŀ���hashcode()��������ļ������
 * @author johnny.lin
 * @date 2018��2��25�� ����10:48:59
 * @version V1.0
 */
public class TestHashCode
{
	public static void main(String[] args)
	{
		String str = "yangcq";  
	      
	    // ��һ�� = (int)'y'  
	    // �ڶ��� = (31 * (int)'y') + (int)'a'  
	    // ������ = 31 * ((31 * (int)'y') + (int)'a') + (int)'n'  
	    // ���Ĳ� = 31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g'  
	    // ���岽 = 31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c'  
	    // ������ = 31 * (31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c') + (int)'q'  
	      
	    // ����Ĺ��̣�Ҳ����������ķ�ʽ��ʾ  
	      
	    // ��һ�� = (int)'y'  
	    // �ڶ��� = 31 * (��һ���ļ�����) + (int)'a'  
	    // ������ = 31 * (�ڶ����ļ�����) + (int)'n'  
	    // ���Ĳ� = 31 * (�������ļ�����) + (int)'g'  
	    // ���岽 = 31 * (���Ĳ��ļ�����) + (int)'c'  
	    // ������ = 31 * (���岽�ļ�����) + (int)'q'  
	      
	    int hashcode = 31 * (31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c') + (int)'q';  
	    System.out.println("yangcq��hashcode = " + hashcode);        // yangcq��hashcode = -737879313  
	    System.out.println("yangcq��hashcode = " + str.hashCode());  // yangcq��hashcode = -737879313  
	    System.out.println(31 * 1147447700);
	    System.out.println(BigDecimal.valueOf(31).multiply(BigDecimal.valueOf(1147447700)));
	    
	    Set<FilterData> set = new HashSet<>();
	    set.add(new FilterData());
	    set.add(new FilterData());
	    System.out.println(set.size()); 
	    System.out.println(new FilterData().hashCode());
	    System.out.println(new FilterData().hashCode());
	    System.out.println((int)'��');
	    System.out.println("��".hashCode());
	    
	    Map<String, String> map = new HashMap<>();
	    map.put("a", null);
	    map.put("b", "b");
	    map.put("a", "ac");
	    System.out.println(map);
	    
	    System.out.println(new Date(1477900730370L));
	}

}
