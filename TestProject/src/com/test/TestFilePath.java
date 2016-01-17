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
	 * Author��jllin
	 * 2014-6-20 ����03:48:02
	 */
	public static void main(String[] args) {
		TestFilePath tfPath = new TestFilePath();
		System.out.println("----��ȡ���·�����ļ�");
		File file = new File("src/com/question/���·��.txt");
		String fileContent = tfPath.file2String(file, "GBK");
		System.out.println("fileContent ==> "+fileContent);
		
		System.out.println("");
		System.out.println("ͨ��CLASSPATH��ȡ�����ļ���ע����\"/\"��ͷ");
		InputStream is = TestFilePath.class.getResourceAsStream("/com/question/���·��.txt");
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
	 * �÷�����ʹ��һ������ѭ�������ֽ����ж�ȡ�ֽڣ���ŵ�byte�����У�ÿ�ζ�ȡ1024���ֽ�(һ�㶼���������)��
	 * ����ÿ�ζ�ȡ���ֽ�������һ����1024��(�������һ�εĶ�ȡ�Ϳ��ܲ���)����������Ҫ��¼ÿ��ʵ�ʶ������ֽ�����
	 * Ȼ��ʵ�ʶ�ȡ�����ֽڰ�ָ���ı��뷽ʽת�����ַ�����
	 * TestFilePath.stream2StringOne()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author��jllin
	 * 2014-6-25 ����11:43:44
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
				//������������� len �������ֽڶ��� byte���飬���Զ�ȡ len ���ֽڣ�����ȡ���ֽ�Ҳ����С�ڸ�ֵ����������ʽ����ʵ�ʶ�ȡ���ֽ�����
				//0��ʾ��b�ĵ�0��λ�ÿ�ʼд��
				bytesRead = is.read(b, 0, b.length);
				if( bytesRead == -1 ) {
					//�ر���
					is.close();
					return result;
				}
				
				//ͨ��ʹ��ָ���� charset����ָ���� byte�����飬����һ���µ� String
				result += new String(b, 0,bytesRead,charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * ˼·��
	 * 1.�ֽ�������ת��Ϊ�ַ���
	 * 2.�ַ�����ȡ�ַ����飬����ʵ�ʶ�ȡ��������append()��StringBuffer��
	 * 3.�����ַ���toString()������
	 * TestFilePath.stream2StringTwo()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author��jllin
	 * 2014-6-25 ����11:53:43
	 */
	public String stream2StringTwo(InputStream is,String charset) {
		StringBuffer sb = new StringBuffer();
		try {
			//�ֽ���ת�����ַ���
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
	 * ���ַ��������ж�ȡ�ı�����������ַ����Ӷ�ʵ���ַ���������еĸ�Ч��ȡ��
	 * ����ָ���������Ĵ�С�����߿�ʹ��Ĭ�ϵĴ�С�����������£�Ĭ��ֵ�㹻��
	 * TestFilePath.stream2StringThree()
	 * @param is
	 * @param charset
	 * @return
	 * @return String
	 * Author��jllin
	 * 2014-6-25 ����02:33:50
	 */
	public String stream2StringThree(InputStream is,String charset) {
		String strLine = "";
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			//����һ��ʹ��Ĭ�ϴ�С���뻺�����Ļ����ַ�������
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
	 * java.util.Scanner��Java5������������Ҫ�����Ǽ��ı�ɨ�衣�������ʵ�õĵط������ڻ�ȡ����̨����
	 * TestFilePath.stream2StringFour()
	 * @param is
	 * @return
	 * @return String
	 * Author��jllin
	 * 2014-6-25 ����03:01:06
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
