package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
 
/**
 * @author zouhailin
 * 2013-7-5
 */
public class XmlTest {
 
//    private static Map<String, String> xmlmap = new HashMap<String, String>();
    //存储xml元素信息的容器
    private static List<String> elemList = new ArrayList<String>();
 
    //要测试的xml对象
    private static String srcXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
            "<doc>\n" +
            "    <person>\n" +
            "        <name>某人</name>\n" +
            "        <adds>            \n" +
            "            <add ID=\"10001\">\n" +
            "                <BS>10001</BS>\n" +
            "                <note>西安市太白路</note>\n" +
            "            </add>\n" +
            "            <add ID=\"\">\n" +
            "                <BS>10002</BS>\n" +
            "                <note>空ID节点啊</note>\n" +
            "            </add>\n" +
            "            <add>\n" +
            "                <BS>10003</BS>\n" +
            "                <note>空ID节点啊</note>\n" +
            "            </add>\n" +
            "            <add ID=\"10004\">\n" +
            "                <BS>10004</BS>\n" +
            "                <note>西安市太白路2</note>\n" +
            "            </add>\n" +
            "       </adds>\n" +
            "    </person>\n" +
            "    <other>\n" +
            "        <name ID=\"HEHE\">ASDF</name>\n" +
            "    </other>\n" +
            "</doc>";
 
    /**
     * 获取根元素
     *
     * @return
     * @throws DocumentException
     */
    public Element getRootElement() throws DocumentException {
        Document srcdoc = DocumentHelper.parseText(srcXml);
        Element elem = srcdoc.getRootElement();
        return elem;
    }
 
    /**
     * 递归遍历方法
     *
     * @param element
     */
    public void getElementList(Element element) {
        List elements = element.elements();
        if (elements.size() == 0) {
            //没有子元素
            String xpath = element.getPath();
            String value = element.getTextTrim();
            elemList.add(xpath+" "+value);
        } else {
            //有子元素
            for (Iterator it = elements.iterator(); it.hasNext();) {
                Element elem = (Element) it.next();
                //递归遍历
                getElementList(elem);
            }
        }
    }
 
    @SuppressWarnings("unchecked")
    public String getListString(List<String> elemList) {
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> it = elemList.iterator(); it.hasNext();) {
            String str = it.next();
//            sb.append(str+"\n");
        }
        Map eleMap = eleMap = new HashMap();
        List<Map> list = new ArrayList<Map>();
        for (int i=1; i<=elemList.size(); i++) {
//        	System.out.println(elemList.get(i));
        	if (i % 2 == 0){
        		eleMap.put(elemList.get(i-1), "map");
        		list.add(eleMap);
        		eleMap = new HashMap();
        	} else {
        		eleMap.put(elemList.get(i-1), "map");
        	}
        }
        
        System.out.println(list.size());
        return sb.toString();
    }
    
    public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
    
    public static void main(String args[]) throws DocumentException {
//    	Document document;
//    	document = DocumentHelper.parseText(srcXml);
//        
//        XmlTest test = new XmlTest();
//        Node node = document.selectSingleNode("/doc/person/adds/add");
//        Element root = node.getParent();
//        test.getElementList(root);
//        String x = test.getListString(elemList);
// 
//        System.out.println("-----------原xml内容------------");
//        System.out.println(srcXml);
//        System.out.println("-----------解析结果------------");
//        System.out.println(x);
    	
//    	String str = "/CMBC/xDataBody/dtlList/dtlInfo/opAreaCode";
//    	System.out.println(str.substring(str.lastIndexOf("/")+1));
    	
//    	Long value = 13876L;
//    	String formatStr = "%012d";
//    	System.out.println(String.format(formatStr, value));
    	
    	String str = "100.00";
    	double amount = Double.valueOf(str)/100;
    	System.out.println("amount ==> " + amount);
    	System.out.println(isNum(str));
    	
    }
} 