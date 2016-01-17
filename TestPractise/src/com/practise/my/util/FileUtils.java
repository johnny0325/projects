package com.practise.my.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 主要用于生成需要上传的省HSC数据文件
 * 也可提供写文件操作
 * 
 * @time 2011-7-20 下午12:18:29
 *
 */
public class FileUtils {
	
//	private static final Logger log = Logger.getLogger(FileUtil.class);
	public static SimpleDateFormat dateTemplate4 = new SimpleDateFormat("MMdd");
	public static SimpleDateFormat dateTemplate14 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat dateTemplate19 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String ret = System.getProperty("line.separator");
	
	/**
	 * 将字符串写成默认格式的文件(中文系统默认编码为:GBK)
	 * 
	 * @param path
	 * @param content
	 * @param append
	 */
	public static void writeFile(String path, String content, boolean append){
		try {
			FileWriter fileWriter = new FileWriter(path,append);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(path+" 写文件出错."+e.getMessage());
		}
	}
	
	/**
	 * 将字符串写成UTF-8格式的文件
	 * 
	 * @param path
	 * @param content
	 * @param append
	 */
	public static void writeFileUTF8(String path, String content, boolean append){
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path,append));
			bos.write(content.getBytes("UTF-8"));
			bos.flush();
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(path+" 写文件出错."+e.getMessage());
		}
	}
	
	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		try {
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				if(!file.delete()){
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println(path + " 删除文件出错。" + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 获取发送到省HSC的文件名
	 * 
	 * @param table
	 * @param areaNo
	 * @return
	 */
	public static String getProvFileName(String table, String areaNo){
		return table+"_"+areaNo+"_"+StringUtil.changeDateToStr14(new Date());
	}
	
	/**
	 * 三户接口使用
	 * 获取发送到省HSC的IMSI数据的文件名(Cssmmdd 如CGZ0910)
	 * 
	 * @param table
	 * @param areaNo
	 * @return
	 */
//	public static String getIMSIFileName(String table, String areaNo){
//		return table+
//				FileUtil.getRegionLetterMap().get(areaNo)+
//		       StringUtil.changeDateToStr4(new Date());
//	}
	
	/**
	 * 将一个对象转换为字符串,用于写文件.
	 * 对象为复杂对象,对象的所有属性应为简单类型.
	 * tmpfields用于保证获取对象属性值的顺序,属性要严格对应VO中的属性名(大小写)
	 * 
	 * @param opType
	 * @param vo
	 * @param tmpfields
	 * @return
	 */
	public static String object2Str(String opType,Object vo,String[] tmpfields) {
		StringBuffer buff = new StringBuffer();
		if(null != opType){//有些库表有,文件不需要(VPMNID)
		    buff.append(opType).append("|");
		}
		try {
			Class<?> voClass = vo.getClass();
			Field tmpField = null;
			for (int i = 0; i < tmpfields.length; i++) {
				tmpField = voClass.getDeclaredField(tmpfields[i]);
//			    //对于属性名称有下划线的,要进行把下划线去掉(函数名不能有下划线)
				if(tmpfields[i].contains("_")){
					tmpfields[i] = tmpfields[i].replace("_", "");
				}
				try {
					String value = BeanUtils.getProperty(vo, tmpfields[i]);
					if(StringUtil.checkNullAndBlank(value) && tmpField.getType().getName().endsWith("Date")){
						value = StringUtil.changeAreaDateToStr19(value);
					}
					if(null != value){
					    buff.append(value);
					}
					buff.append("|");
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println(voClass.getSimpleName()+ "对象,读取属性("+tmpfields[i]+")出错.");
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println( "不能遍历VO:"+vo.getClass().getName()+", "+e.getMessage());
		}
		buff.append(ret);//没有出错就返回
		return buff.toString();
	}
	
	/**
	 * 读文件,系统默认为GBK编码,若文件为其它格式的(如:UTF-8),则要指定读取文件的编码格式.
	 * "UTF-8"
	 * "GBK"
	 * @param file
	 * @return
	 */
	public static String readFile(File file,String encoding){
		if(null == file ) return null;
		StringBuffer stringBuffer = new StringBuffer();
	    try {
//	    	BufferedReader fileReader = new BufferedReader(new FileReader(file));                            //默认字符编码:GBK
//			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//默认字符编码:GBK
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding)); 
			String line = fileReader.readLine();
			while(null != line){
				stringBuffer.append(line);
				line = fileReader.readLine();
			}
			fileReader.close();//读完要及时关闭连接
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(file.getName()+"读取文件出错."+e.getMessage());
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 读文件,系统默认为GBK编码,若文件为其它格式的(如:UTF-8),则要指定读取文件的编码格式.
	 * "UTF-8"
	 * "GBK"
	 * @param file
	 * @return
	 */
	public static String readFileWithRet(File file,String encoding){
		if(null == file ) return null;
		StringBuffer stringBuffer = new StringBuffer();
	    try {
//	    	BufferedReader fileReader = new BufferedReader(new FileReader(file));                            //默认字符编码:GBK
//			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//默认字符编码:GBK
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding)); 
			String line = fileReader.readLine();
			int i=1;
			while(null != line){
				stringBuffer.append(line);
				line = fileReader.readLine();
				if(null != line){
					stringBuffer.append(ret);
				}
				System.out.println(i++ +" "+line);
			}
			fileReader.close();//读完要及时关闭连接
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(file.getName()+"读取文件出错."+e.getMessage());
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 若知道字符编码,最好直接在读文件时,指定字符编码,而不要用此方法(转换3个中文时,最后一个中文出现问号乱码).
	 * 读文件时,指定字符编码 : BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
	 * 
	 * 字符串编码转换的实现方法
	 * @param str   待转换编码的字符串
	 * @param newCharset 目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
	   if (str != null) {
	    //用默认字符编码解码字符串。
	    byte[] bs = str.getBytes();
	    //用新的字符编码生成字符串
	    return new String(bs, newCharset);
	   }
	   return null;
	}
	
	/**
	* 字符串编码转换的实现方法
	* @param str   待转换编码的字符串
	* @param oldCharset 原编码
	* @param newCharset 目标编码
	* @return
	* @throws UnsupportedEncodingException
	*/
	public static String changeCharset(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException {
	   if (str != null) {
	    //用旧的字符编码解码字符串。解码可能会出现异常。
	    byte[] bs = str.getBytes(oldCharset);
	    //用新的字符编码生成字符串
	    return new String(bs, newCharset);
	   }
	   return null;
	}
	
	/**
	 * dom4j格式化XML文本
	 * 
	 * @param text
	 * @return
	 */
	public static String fromatXML(String text) {
		try {
//			OutputFormat format = OutputFormat.createPrettyPrint();//标准格式化
//			OutputFormat format = OutputFormat.createCompactFormat();//缩减格式:单行
			StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, OutputFormat.createPrettyPrint());
            writer.write(DocumentHelper.parseText(text));
            writer.close();
            sw.flush();
			return sw.toString();
		} catch (Exception e) {
			return text;
		}
	}
	
	/**
	 * 读取配置文件 XXX.properties
	 * 
	 * @param propertiesName
	 * @return
	 */
	public static Properties getProjectProperties(String filePath){
		Properties properties = null;
		try {
			properties = new Properties();
			System.out.println("开始读取文件 :" +filePath);
			properties.load(new FileReader(filePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("读取ds.properties文件出错."+e.getMessage());
		}
		return properties;
	}
	
	/**
	 * 读取配置文件 XXX.properties
	 * 
	 * @param propertiesName
	 * @return
	 */
	public static Properties getProperties(String propertiesName){
		Properties properties = null;
		String path = FileUtils.class.getResource("").getPath();
		int pos = path.indexOf("WEB-INF");

		if (pos >= 0) {
			try {
				path = path.substring(0, pos + "WEB-INF".length() + 1);// 获取文件名
				path = URLDecoder.decode(path,"UTF-8");//获取路径中的空格和中文
				properties = new Properties();
				System.out.println("开始读取文件 :" + path + propertiesName);
				properties.load(new FileReader(path + propertiesName));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("读取ds.properties文件出错."+e.getMessage());
			}
		} else {
			System.out.println("路径为:" + path);
			System.out.println("找不到 WEB-INF 目录,不会加载 "+propertiesName+" 文件.");
		}
		
		return properties;
	}

	/**
	 * XML文件过滤器
	 * 
	 */
	public static FileFilter xmlFileFilter = new FileFilter(){
    	public boolean accept(File file){
    		if(file.getName().toLowerCase().endsWith("xml")){
    			return true;
    		}
    		return false;
    	}
    };
    
    /**
     * XML文件名过滤器
     * 
     */
    public static FilenameFilter xmlFileNameFilter = new FilenameFilter(){

		@Override
		public boolean accept(File dir, String name) {
//			if(dir.isDirectory()){
//			}
			if(name.toLowerCase().endsWith("xml")){
    			return true;
    		}
			return false;
		}

    };
}
