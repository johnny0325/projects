package com.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TestMap {

	/**
	 * ������������α���Map���ϵ�����
	 * Map��java�еĽӿڣ�Map.Entry��Map��һ���ڲ��ӿڡ�
	 * Map�ṩ��һЩ���÷�������keySet()��entrySet()�ȷ�����keySet()��������ֵ��Map��keyֵ�ļ��ϣ�
	 * entrySet()�ķ���ֵҲ�Ƿ���һ��Set���ϣ��˼��ϵ�����ΪMap.Entry��
	 * Map.Entry��Map������һ���ڲ��ӿڣ��˽ӿ�Ϊ���ͣ�����ΪEntry<K,V>����
	 * ��ʾMap�е�һ��ʵ�壨һ��key-value�ԣ�,�ӿ�����getKey(),getValue������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Aug 29, 2011 11:16:13 AM
	 */
	public static void main(String[] args) {
		Map map = new HashMap();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		map.put("d", 4);
		
		//����һ
		Set keySet = map.keySet();
		if(keySet != null) {
			Iterator iterator = keySet.iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object value = map.get(key);
				System.out.println("����һ��key==>"+key+" value==>"+value);
			}
		}
		
		//������
		Iterator<Entry<String,Object>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			
			System.out.println("��������key==>"+key+" value==>"+value);
		}
		
	}

}
