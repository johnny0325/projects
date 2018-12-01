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
 * @Description:  [哈希算法在String类中的应用]
 * 				  String类重写了Object类的equals()方法和hashcode()方法，
 * 				  String类的hashcode值（哈希值）是如何计算得到的？具体实现？为了方便阅读，我们来进行分步说明
 * 				  通过下面的测试方法，我们可以很清晰的看到hashcode()方法具体的计算过程
 * @author johnny.lin
 * @date 2018年2月25日 上午10:48:59
 * @version V1.0
 */
public class TestHashCode
{
	public static void main(String[] args)
	{
		String str = "yangcq";  
	      
	    // 第一步 = (int)'y'  
	    // 第二步 = (31 * (int)'y') + (int)'a'  
	    // 第三步 = 31 * ((31 * (int)'y') + (int)'a') + (int)'n'  
	    // 第四步 = 31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g'  
	    // 第五步 = 31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c'  
	    // 第六步 = 31 * (31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c') + (int)'q'  
	      
	    // 上面的过程，也可以用下面的方式表示  
	      
	    // 第一步 = (int)'y'  
	    // 第二步 = 31 * (第一步的计算结果) + (int)'a'  
	    // 第三步 = 31 * (第二步的计算结果) + (int)'n'  
	    // 第四步 = 31 * (第三步的计算结果) + (int)'g'  
	    // 第五步 = 31 * (第四步的计算结果) + (int)'c'  
	    // 第六步 = 31 * (第五步的计算结果) + (int)'q'  
	      
	    int hashcode = 31 * (31 * (31 * (31 * ((31 * (int)'y') + (int)'a') + (int)'n') + (int)'g') + (int)'c') + (int)'q';  
	    System.out.println("yangcq的hashcode = " + hashcode);        // yangcq的hashcode = -737879313  
	    System.out.println("yangcq的hashcode = " + str.hashCode());  // yangcq的hashcode = -737879313  
	    System.out.println(31 * 1147447700);
	    System.out.println(BigDecimal.valueOf(31).multiply(BigDecimal.valueOf(1147447700)));
	    
	    Set<FilterData> set = new HashSet<>();
	    set.add(new FilterData());
	    set.add(new FilterData());
	    System.out.println(set.size()); 
	    System.out.println(new FilterData().hashCode());
	    System.out.println(new FilterData().hashCode());
	    System.out.println((int)'二');
	    System.out.println("二".hashCode());
	    
	    Map<String, String> map = new HashMap<>();
	    map.put("a", null);
	    map.put("b", "b");
	    map.put("a", "ac");
	    System.out.println(map);
	    
	    System.out.println(new Date(1477900730370L));
	}

}
