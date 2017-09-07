package com.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddress
{

	public static void main(String[] args)
	{
		try
		{
			//通过域名建立InetAddress对象
			InetAddress addr = InetAddress.getByName("www.baidu.com");
			String domainName = addr.getHostName();
			String IPName = addr.getHostAddress();
			System.out.println(domainName);
			System.out.println(IPName);
			
			InetAddress[] addresses=InetAddress.getAllByName("www.baidu.com");
			for(InetAddress addr2:addresses)

			{
				System.out.println(addr2);
			}
			
			InetAddress address=InetAddress.getLocalHost();
			System.out.println(address);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

}
