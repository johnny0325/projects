package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class TestFilePath {

	/**
	 * TestFilePath.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2014-6-20 下午03:48:02
	 */
	public static void main(String[] args) {
		TestFilePath tfPath = new TestFilePath();
		System.out.println("----读取相对路径的文件");
		File file = new File("src/com/question/相对路径.txt");
		String fileContent = tfPath.file2String(file, "GBK");
		System.out.println("fileContent ==> "+fileContent);
		
		System.out.println("");
		System.out.println("通过CLASSPATH读取包内文件，注意以\"/\"开头");
		InputStream is = TestFilePath.class.getResourceAsStream("/com/question/相对路径.txt");
		fileContent = tfPath.stream2StringTwo(is, "GBK");
		System.out.println("fileContent ==> "+fileContent);
	}
	
	public String file2String(File file,String charset) {
		String result = null;
		try {
//			result = stream2StringOne(new FileInputStream(file),charset);
//			result = stream2StringTwo(new FileInputStream(file),charset);
//			result = stream2StringThree(new FileInputStream(file),charset);
			result = stream2StringFour(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 该方法中使用一个无限循环，从字节流中读取字节，存放到byte数组中，每次读取1024个字节(一般都是这个设置)，
	 * 由于每次读取的字节数量不一定够1024个(比如最后一次的读取就可能不够)，所以我们要记录每次实际读到的字节数，
	 * 然后将实际读取到的字节按指定的编码方式转换成字符串。
	 * TestFilePath.stream2StringOne()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author：jllin
	 * 2014-6-25 上午11:43:44
	 */
	public String stream2StringOne(InputStream is,String charset) {
		try {
			byte[] b = new byte[1024];
			String result = "";
			if( is == null ){
				return "";
			}
			
			int bytesRead = 0;
			while( true ) {
				//将输入流中最多 len 个数据字节读入 byte数组，尝试读取 len 个字节，但读取的字节也可能小于该值。以整数形式返回实际读取的字节数。
				//0表示从b的第0个位置开始写入
				bytesRead = is.read(b, 0, b.length);
				if( bytesRead == -1 ) {
					//关闭流
					is.close();
					return result;
				}
				
				//通过使用指定的 charset解码指定的 byte子数组，构造一个新的 String
				result += new String(b, 0,bytesRead,charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 思路：
	 * 1.字节输入流转换为字符流
	 * 2.字符流读取字符数组，并把实际读取到的数据append()到StringBuffer中
	 * 3.最后把字符串toString()出来。
	 * TestFilePath.stream2StringTwo()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author：jllin
	 * 2014-6-25 上午11:53:43
	 */
	public String stream2StringTwo(InputStream is,String charset) {
		StringBuffer sb = new StringBuffer();
		try {
			//字节流转换成字符流
			Reader reader = new InputStreamReader(is, charset);
			int length = 0;
			for(char[] c = new char[1024]; (length = reader.read(c))!= -1;) {
				sb.append(c, 0, length);
			}
			
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * BufferedReader:
	 * 从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。
	 * 可以指定缓冲区的大小，或者可使用默认的大小。大多数情况下，默认值足够大
	 * TestFilePath.stream2StringThree()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author：jllin
	 * 2014-6-25 下午02:33:50
	 */
	public String stream2StringThree(InputStream is,String charset) {
		String strLine = "";
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			//创建一个使用默认大小输入缓冲区的缓冲字符输入流
			br = new BufferedReader(new InputStreamReader(is,charset));
			if( br != null ){
				;
				while( (strLine = br.readLine()) != null ){
					sb.append(strLine);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * java.util.Scanner是Java5的新特征，主要功能是简化文本扫描。这个类最实用的地方表现在获取控制台输入
	 * TestFilePath.stream2StringFour()
	 * @param is
	 * @return
	 * @return String
	 * Author：jllin
	 * 2014-6-25 下午03:01:06
	 */
	public String stream2StringFour(InputStream is) {
		String result = "";
		Scanner scanner = new Scanner(is);
		while( scanner.hasNextLine() ) {
			result += scanner.nextLine();
		}
		return result;
	}
}
