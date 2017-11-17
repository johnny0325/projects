package com.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TestMap {

	/**
	 * 功能描述：如何遍历Map集合的数据
	 * Map是java中的接口，Map.Entry是Map的一个内部接口。
	 * Map提供了一些常用方法，如keySet()、entrySet()等方法，keySet()方法返回值是Map中key值的集合；
	 * entrySet()的返回值也是返回一个Set集合，此集合的类型为Map.Entry。
	 * Map.Entry是Map声明的一个内部接口，此接口为泛型，定义为Entry<K,V>。它
	 * 表示Map中的一个实体（一个key-value对）,接口中有getKey(),getValue方法。
	 * @param args
	 * @return void
	 * Author：jlLin
	 * Aug 29, 2011 11:16:13 AM
	 */
	public static void main(String[] args) {
		Map map = new HashMap();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		map.put("d", 4);
		
		//方法一
		Set keySet = map.keySet();
		if(keySet != null) {
			Iterator iterator = keySet.iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object value = map.get(key);
				System.out.println("方法一：key==>"+key+" value==>"+value);
			}
		}
		
		//方法二
		Iterator<Entry<String,Object>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			
			System.out.println("方法二：key==>"+key+" value==>"+value);
		}
		
	}

}
