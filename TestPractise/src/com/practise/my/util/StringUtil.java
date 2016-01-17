package com.practise.my.util;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XPP3Reader;

/**
 * 
 * 增加检查空字符串方法;<br>
 * 增加日期格式检查方法;
 * @author qfg
 * @version 1.0
 */
public class StringUtil {
	
	private static String packageName = "com.huawei.cbs.cbsinterface";//cbs route
	private static long currseq = 1000000000;//只截取后面9位
	private static Calendar now = Calendar.getInstance();
	private static long currfileseq = 10000;//只截取后面9位
	private static Calendar filetime = Calendar.getInstance();	
	private static DateFormat month_day = new SimpleDateFormat("MMdd");
	private static DateFormat dateTemplate8 = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat dateTemplate14 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static DateFormat dateTemplate19 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat dateTemplate22 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	private static SimpleDateFormat dateTemplateArea = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.US);

	
	/**
	 * 根据surround和split将字符数组拼接成字符串
	 * @Date 2013-6-24上午11:57:01
	 * @return String
	 */
	public static String concatStrArr(String[] strArr,String surround, String split){
		if(null == strArr || strArr.length == 0) return "";
		if(surround == null) surround ="";
		if(split == null) split ="";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<strArr.length ; i++){
			sb.append(surround).append(strArr[i]).append(surround);
			if(i<strArr.length-1){
				sb.append(split);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 检查是否空字符串<br>
	 * 是合法字串返回true;空字串返回false;
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNullAndBlank(String str) {
		return (null != str && !"".equals(str));
	}

	/**
	 * 检查对象中的属性值是否为空.
	 * 
	 * @param objFields 要判断是否为空的对象属性数组
	 * @param obj 
	 * @return
	 * @throws Exception
	 *             反射出错则会抛出异常
	 */
	public static String checkObjProperty(String[] objFields, Object obj) {
		if( null == obj || null == objFields || objFields.length <= 0 ){
			return "";
		}
		
		List<String> fieldsList = new ArrayList<String>();
		List<String> upperFieldsList = new ArrayList<String>();
		for(int i=0; i< objFields.length; i++){
			fieldsList.add(objFields[i]);
			upperFieldsList.add(objFields[i].toUpperCase());
		}
		StringBuffer errMsg = new StringBuffer();
		try {
			Class<?> objClass = obj.getClass();
			Field[] fields = objClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if(upperFieldsList.contains(fields[i].getName().toUpperCase())){
				    if(!checkNullAndBlank(BeanUtils.getProperty(obj, fields[i].getName()))){
				    	errMsg.append(obj.getClass().getSimpleName()).append(".").
				    	       append(fieldsList.get(upperFieldsList.indexOf(fields[i].getName().toUpperCase()))).
				    	       append(" 不能为空,");
					}
				}
			}
		} catch (Exception e) {
			return "不能遍历obj";
		}
		return errMsg.toString();
	}
	
	/**
	 * 字符串数组转成用 split 连接的字符串
	 * 
	 * @param strArr
	 * @param split
	 * @return
	 */
	public static String array2String(String[] strArr,String split){
		if(null == strArr || strArr.length <= 0){
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0; i< strArr.length; i++){
			if(i < strArr.length-1){
				stringBuffer.append(strArr[i]).append(split);
			}else{
				stringBuffer.append(strArr[i]);
			}
		}
		return stringBuffer.toString();
	}
	
	/**
	 * List<String>转成用 split 连接的字符串
	 * 
	 * @param strArr
	 * @param split
	 * @return
	 */
	public static String list2String(List<String> list,String split){
		if(null == list || list.size() <= 0){
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0; i< list.size(); i++){
			if(i < list.size()-1){
				stringBuffer.append(list.get(i)).append(split);
			}else{
				stringBuffer.append(list.get(i));
			}
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 将字符串数组转换成List
	 * 
	 * @param strArr
	 * @return
	 */
	public static List<String> array2List(String[] strArr){
		if(null == strArr || strArr.length <= 0){
			return null;
		}
		
		List<String> strList = new ArrayList<String>();
		for(int i=0; i< strArr.length; i++){
			strList.add(strArr[i]);
		}
		return strList;
	}
	
	/**
	 * 检查日期字符串格式是否YYYY-MM-DD HH:MM:SS
	 * 
	 * @param datetime
	 * @return
	 */
	public static boolean checkDateTime(String datetime) {
		if (!checkNullAndBlank(datetime)) {
			return false;
		}

		// 正则匹配式
		String matchCase = "[1-9][0-9]{3}\\-(0[0-9]|1[0-2])\\-([0-2][0-9]|3[0-1])\\s(2[0-3]|[0-1][0-9])\\:([0-5][0-9])\\:([0-5][0-9])";
		return datetime.matches(matchCase);
	}
	
	/**
	 * 检查日期字符串格式是否YYYYMMDD
	 * 
	 * @param datetime
	 * @return
	 */
	public static boolean checkDateTime8(String datetime) {
		if (!checkNullAndBlank(datetime)) {
			return false;
		}

		// 正则匹配式
		String matchCase = "[1-9][0-9]{3}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])";
		return datetime.matches(matchCase);
	}
	
	/**
	 * 检查日期字符串格式是否YYYYMMDDHHMMSS
	 * 
	 * @param datetime
	 * @return
	 */
	public static boolean checkDateTime14(String datetime) {
		if (!checkNullAndBlank(datetime)) {
			return false;
		}

		// 正则匹配式
		String matchCase = "[1-9][0-9]{3}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])(2[0-3]|[0-1][0-9])([0-5][0-9])([0-5][0-9])";
		return datetime.matches(matchCase);
	}
	
	/**
	 * 将“yyyyMMdd"格式的日期字符串转换为java.utils.Date类型
	 * 
	 * @throws Exception
	 */
	public static java.util.Date changeStrToDate8(String item,String datetime)
			throws Exception {
		if(datetime == null || "".equals(datetime)){
			return null;
		}
		
		if(checkDateTime8(datetime)){
			return changeStrToDate(item, datetime, "yyyyMMdd");
		}else{
			throw new Exception(datetime + ":日期格式不符合YYYYMMDD");
		}
	}
	
	public static void main(String[] arg) throws Exception{
		System.out.println(changeStrToDate8("生日","20111101"));
	}
	/**
	 * 将“yyyyMMddHHmmss"格式的日期字符串转换为java.utils.Date类型
	 * 
	 * @throws Exception
	 */
	public static java.util.Date changeStrToDate14(String item,String datetime)
			throws Exception {
		if(datetime == null || "".equals(datetime)){
			return null;
		}
		
		if(checkDateTime14(datetime)){
			return changeStrToDate(item, datetime, "yyyyMMddHHmmss");
		}else{
			throw new Exception(item + " (" + datetime + ") :日期格式不符合YYYYMMDDHHMMSS");
		}
	}

	/**
	 * 将"yyyy-MM-dd HH:mm:ss"格式的日期字符串转换为java.utils.Date类型
	 * 
	 * @throws Exception
	 */
	public static java.util.Date changeStrToDate(String item,String datetime)
			throws Exception {
		return changeStrToDate(item, datetime, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将pattern格式的日期字符串转换为java.utils.Date类型
	 * 
	 * @throws Exception
	 */
	public static java.util.Date changeStrToDate(String item, String datetime, String pattern)
			throws Exception {
		java.util.Date result;
		try {
			if ((datetime != null) && (datetime.length() > 0)) {
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				result = format.parse(datetime);
			} else {
				result = null;
			}
		} catch (Exception exp) {
			// 指定的日期字符串格式不对
			throw new Exception(item + " (" + datetime + ") , 日期格式不合法.");
		}
		return result;
	}

	/**
	 * 将YYYY-MM-DD HH:MM:SS 转换成YYYYMMDD
	 * 
	 * @param str
	 * @return
	 * @throws 格式不合法时候抛出
	 */
	public static String formatDate8(String str) throws Exception {
		if (checkDateTime(str)) {
			str = str.replaceAll("-", "").replaceAll(":", "").replaceAll(" ",
					"");
			return str.substring(0, 8);
		} else {
			throw new Exception("日期格式不符合YYYY-MM-DD HH:MM:SS");
		}
	}

	/**
	 * 将YYYY-MM-DD HH:MM:SS 转换成YYYYMMDDHHMMSS
	 * 
	 * @param str
	 * @return
	 * @throws 格式不合法时候抛出
	 */
	public static String formatDate14(String str) throws Exception {
		if (checkDateTime(str)) {
			str = str.replaceAll("-", "").replaceAll(":", "").replaceAll(" ",
					"");
			return str;
		} else {
			throw new Exception(str + ":日期格式不符合YYYY-MM-DD HH:MM:SS");
		}
	}
	/**
	 * 将YYYYMMDDHHMMSS 转换成 YYYY-MM-DD HH:MM:SS
	 * 
	 * @param str
	 * @return
	 * @throws 格式不合法时候抛出
	 */
	public static String formatTimeType14(String str) throws Exception{
		StringBuffer datestr = new StringBuffer("");
		if(str.length()!=14){
			throw new Exception(str + "的格式不符合YYYYMMDDHHMMSS,");
		}else{
			datestr.append(str.substring(0, 4))
			.append("-")
			.append(str.substring(4, 6))
			.append("-")
			.append(str.substring(6, 8))
			.append(" ")
			.append(str.substring(8, 10))
			.append(":")
			.append(str.substring(10, 12))
			.append(":")
			.append(str.substring(12, 14));
		}
		return datestr.toString();
		
	}
	
//	/**
//	 * 将DATE格式转换成YYYYMMDDHHMMSS
//	 * 
//	 * @param date
//	 * @return
//	 * @throws Exception
//	 */
//	public static String formatDate14(Date date) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		return dateFormat.format(date);
//	}

	/**
	 * 检查字符是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNumber(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 将字符转换成数字
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Long formatLong(String item,String str) throws Exception {
		//add on 2011-08-12:联调发现CRM会传"null"这样字符过来导致转换失败。修改兼容CRM的这种错误
		if(null == str || "".equals(str)||"null".equalsIgnoreCase(str)) return null;
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			throw new Exception(item + " 转换失败。" + str + " 不是合法数字。");
		}
	}

	/**
	 * 将字符转换成数字
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static int formatInt(String str) throws Exception {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			throw new Exception("转换失败。" + str + "不是合法数字。");
		}
	}
	
	/**
	 * 将DATE转换成YYYY-MM-DD HH:MI:SS
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeDateToStr19(Date date){
		return dateTemplate19.format(date);
	}
	
	/**
	 * 将DATE转换成YYYY-MM-DD HH:MI:SS
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeDateToStr22(Date date){
		return dateTemplate22.format(date);
	}
	
	/**
	 * 将DATE转换成  YYYYMMDDHHMISS
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeDateToStr14(Date date) {
		return dateTemplate14.format(date);
	}
	
	/**
	 * 将DATE转换成  YYYYMMDD
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeDateToStr8(Date date) {
		return dateTemplate8.format(date);
	}
	
	/**
	 * 将DATE转换成  MMDD
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeDateToStr4(Date date) {
		return month_day.format(date);
	}
	
	/**
	 * 将EEE MMM dd HH:mm:ss z yyyy 转换成 YYYYMMDDHH24MISS
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeAreaDateToStr14(String date) throws Exception{
		return dateTemplate14.format(dateTemplateArea.parse(date));
	}
	
	/**
	 * 将EEE MMM dd HH:mm:ss z yyyy 转换成 YYYY-MM-DD HH:MI:SS
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String changeAreaDateToStr19(String date) throws Exception{
		return dateTemplate19.format(dateTemplateArea.parse(date));
	}
	
	/**
	 * 生成SequenceId
	 * 
	 * @return
	 */
	public static synchronized String getSequenceId() {
		StringBuffer buff = new StringBuffer();
		buff.append(dateTemplate14.format(new Date()));// 14
		buff.append(getNextSeq());// 9
		return buff.toString();
	}
	
	/**
	 * 生成当日发起方填写的包的流水号，每天从000000001开始
	 * @return
	 */
	public static String getNextSeq(){
		Calendar requesttime = Calendar.getInstance();
		String seq = null;
		if(requesttime.get(Calendar.YEAR) == now.get(Calendar.YEAR)
		   && requesttime.get(Calendar.MONTH) == now.get(Calendar.MONTH)
		   && requesttime.get(Calendar.DATE) == now.get(Calendar.DATE)){
			currseq = currseq+1;
			seq = String.valueOf(currseq);
		}else{
			now = requesttime;
			currseq = 1000000001;
			seq = String.valueOf(currseq);
		}
		requesttime = null;//释放
		return seq;
	}
	
	public static synchronized String getFileSequenceId() {
		StringBuffer buff = new StringBuffer();
		buff.append(dateTemplate14.format(new Date())).append("_");// 协议要求有"_"分开
		buff.append(getNextFileSeq());// 5
		return buff.toString();
	}
	
	public static String getNextFileSeq(){
		Calendar requesttime = Calendar.getInstance();
		String seq = null;
		if(requesttime.get(Calendar.YEAR) == filetime.get(Calendar.YEAR)
		   && requesttime.get(Calendar.MONTH) == filetime.get(Calendar.MONTH)
		   && requesttime.get(Calendar.DATE) == filetime.get(Calendar.DATE)
		   && requesttime.get(Calendar.HOUR) == filetime.get(Calendar.HOUR)
		   && requesttime.get(Calendar.MINUTE) == filetime.get(Calendar.MINUTE)
		   && requesttime.get(Calendar.SECOND) == filetime.get(Calendar.SECOND)){
			currfileseq = currfileseq+1;
			seq = String.valueOf(currfileseq);
		}else{
			filetime = requesttime;
			currfileseq = 10001;
			seq = String.valueOf(currfileseq);
		}
		requesttime = null;//释放
		return seq;
	}
	
	/**
	 * 将接口请求的对象转换为字符串，给HscServiceImpl使用
	 * 
	 * @param vo
	 * @return
	 * @throws Exception
	 *             反射出错则会抛出异常
	 */

//	@SuppressWarnings("unchecked")
//	public static String complexObject2Str(Object vo) {
//		StringBuffer buff = new StringBuffer();
//		try {
//			Class<?> voClass = vo.getClass();
//			if(voClass.getName().endsWith("RequestHeader")){
//				buff.append("<MessageHeader>");
//			}else{
//		        buff.append("<").append(voClass.getSimpleName()).append(">");//<RequestMessage>
//			}
//			Field[] fields = voClass.getDeclaredFields();
//			for (int i = 0; i < fields.length; i++) {
//				if(LoggerUtil.isSimpleType(fields[i].getType())){
//					String fieldName = fields[i].getName();
//					
//					//对于属性名称有下划线的,要进行把下划线去掉(函数名不能有下划线)
////					if(fieldName.contains("_")){//1.5协议中字段中没有有下划线的
////						fieldName = fieldName.replace("_", "");
////					}
//					try {
//						String value = BeanUtils.getProperty(vo, fieldName);
//						if(StringUtil.checkNullAndBlank(value)){
//							if(fields[i].getType().getName().endsWith("Date")){//日期要进行转换
//								value = StringUtil.changeAreaDateToStr19(value);
//							}
//							//<element>string</element>
//							buff.append("<").append(fields[i].getName()).append(">");
//							buff.append(value);
//							buff.append("</").append(fields[i].getName()).append(">");
//						}else{
//							//<element/>
//							buff.append("<").append(fields[i].getName()).append("/>");
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						//属性没有对应的get方法(可能是属性名有问题)
//						System.out.println(voClass.getSimpleName()+ "对象,读取属性("+fields[i].getName()+")出错.");
//					}
//				}else{
//					//递归解释复杂属性对象
//					Class<?>[] classes = null;
//					Method method = voClass.getDeclaredMethod("get"+fields[i].getName().
//							substring(0,1).toUpperCase()+fields[i].getName().substring(1), classes);
//					Object[] objs = null;
//					Object obj = method.invoke(vo, objs);
//					if(obj == null){
//						//null判断,防止出现异常
//						//对象为null ,不处理
//					}else{
////						System.out.println(obj.getClass().getName());
//						if(obj.getClass().getName().contains("ArrayList")){//遍历ArrayList对象
//							List tmp = (ArrayList)obj;
//							for(int j=0; j<tmp.size();j++){
//								buff.append(complexObject2Str(tmp.get(j)));
//							}
//						}else{
//					        buff.append(complexObject2Str(obj));
//						}
//					}
//				}
//			}
//			//最后将END-TAG补上.
//			if(voClass.getName().endsWith("RequestHeader")){
//				buff.append("</MessageHeader>");
//			}else{
//		        buff.append("</").append(voClass.getSimpleName()).append(">");//<RequestMessage>
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "不能遍历对象:"+vo.getClass().getName();
//		}
//		return buff.toString();
//	}
	
	/**
	 * 
	 * 
	 * @param xml
	 * @param clazz
	 * @return
	 */
//	public static Object str2ComplexObject(String xml, Class clazz){
//		Document doc;
//		try {
//			doc = new XPP3Reader().read(new StringReader(xml));
//			Element eResultMessage = doc.getRootElement();//<MessageHeader><MessageBody>
//			return paraseElement(eResultMessage,clazz.getName());
//		}catch(Exception e){
//			System.out.println("xml报文转换出错.");
//		}
//		return null;
//	}
	
	/**
	 * 将Element转换为Object,此方法由str2ComplexObject使用
	 * 
	 * @param element
	 * @param className
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object paraseElement(Element element , String  className) throws Exception{
		Object object = null;
		List list = null;
		String listName = null;
		String objectName = className.substring(className.lastIndexOf(".")+1);
		try {
			//list对象属性要全部取完后,再进行设置
			if(objectName.endsWith("List")){// AcctList , ExtendObjects
			    list = new ArrayList();
			}
			object = Class.forName(className).newInstance();
			for(Iterator<Element> it = element.elementIterator();it.hasNext();){
				Element e  = it.next();
				String elementName = e.getName();
				String attrName = elementName;
				String tmpIndexStr = elementName.substring(0,2);
				if(!tmpIndexStr.toUpperCase().equals(tmpIndexStr)){
					attrName = elementName.substring(0,1).toLowerCase()+elementName.substring(1);
				}else{
					//前两个字母为大写的属性名,不进行首字母变小写处理,直接用原属性名.IMSI
				}
				if(e.isTextOnly()){//简单对象:字符串
					BeanUtils.setProperty(object, attrName, e.getText());
					System.out.println(e.getName()+"|"+e.getText());
				}else{             //复杂对象
					String tmpClassName = null;
					if(elementName.equals("ParaList")){//跳过ParaList类取值
//							tmpClassName = paraListPackageName+elementName;
					}else{
						tmpClassName = packageName+elementName;
						Object obj = paraseElement(e, tmpClassName);
						if(objectName.endsWith("List")){// AcctList , ExtendObjects
							//List对象设置属性值要特殊处理
							listName = attrName;
							list.add(obj);
						}else{
							BeanUtils.setProperty(object, attrName, obj);
						}
					}
				}
			}
			if(objectName.endsWith("List")){// AcctList , ExtendObjects
			    BeanUtils.setProperty(object, listName, list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("构造对象("+className+")出错."+e.getMessage());
		}
		return object;
	}
}
