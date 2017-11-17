package com.welab.x.test.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * @ClassName: TestHttpClient
 * @Description: 使用HttpClient发起http请求，主要用到的API：DefaultHttpClient, HttpPost, HttpGet
 * @author johnny.lin
 * @date 2017年10月10日 下午12:03:52
 * @version V1.0
 */
public class TestHttpClient
{
	public static void main(String[] args)
	{
		System.out.println();
	}
	
	/**
	 * 
	 * @Title: method
	 * @Description: 使用HttpClient发送post请求
	 * @param  param    设定参数类型
	 * @return String    返回类型
	 */
	public static String post(String url, Map<String, Object> params, String charset) {
		StringBuffer resultBuffer = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator<Entry<String,Object>> iterator = params.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String,Object> elem = iterator.next();
			list.add(new BasicNameValuePair(elem.getKey(),elem.getValue().toString()));
		}
		
		BufferedReader br = null;
		try {
			if (list.size() > 0)
			{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			
			HttpResponse response = client.execute(httpPost);
			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String temp;
			while ((temp = br.readLine()) != null)
			{
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally
		{
			if (br != null)
			{
				try {
					br.close();
				} catch (Exception e)
				{
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		
		return resultBuffer.toString();
	}
	
	/**
	 * 
	 * @Title: method
	 * @Description: 使用HttpClient发送get请求
	 * @param  param    设定参数类型
	 * @return String    返回类型
	 */
	public static String get(String urlParam, Map<String, Object> params, String charset)
	{
		StringBuffer resultBuffer = null;
		HttpClient client = new DefaultHttpClient();
		BufferedReader br = null;
		//构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0)
		{
			for (Entry<String, Object> entry : params.entrySet())
			{
				sbParams.append(entry.getKey());
				sbParams.append("=");
				try
				{
					sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
				}
				catch (UnsupportedEncodingException e)
				{
					throw new RuntimeException(e);
				}
				sbParams.append("&");
			}
		}
		
		if (sbParams != null && sbParams.length() > 0)
		{
			urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
		}
		
		HttpGet httpGet = new HttpGet(urlParam);
		try
		{
			HttpResponse response = client.execute(httpGet);
			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String temp;
			while ((temp = br.readLine()) != null)
			{
				resultBuffer.append(temp);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		} finally
		{
			if (br != null)
			{
				try {
					br.close();
				} catch (Exception e)
				{
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		
		return resultBuffer.toString();
	}
}
