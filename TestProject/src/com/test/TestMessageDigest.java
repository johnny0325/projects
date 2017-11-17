package com.test;

public class TestMessageDigest
{

	public static void main(String[] args)
	{
		TestMessageDigest testMessageDigest = new TestMessageDigest();
		testMessageDigest.testDigest();
	}

	public void testDigest()
	{
		try
		{
			String myinfo = "�ҵĲ�����Ϣ";
			// java.security.MessageDigest
			// alg=java.security.MessageDigest.getInstance("MD5");
			java.security.MessageDigest alga = java.security.MessageDigest.getInstance("SHA-1");
			alga.update(myinfo.getBytes());
			byte[] digesta = alga.digest();
			System.out.println("����ϢժҪ��:" + byte2hex(digesta));
			// ͨ��ĳ�з�ʽ���������������Ϣ(myinfo)��ժҪ(digesta) �Է������ж��Ƿ���Ļ�������
			java.security.MessageDigest algb = java.security.MessageDigest.getInstance("SHA-1");
			algb.update(myinfo.getBytes());
			if (algb.isEqual(digesta, algb.digest()))
			{
				System.out.println("��Ϣ�������");
			}
			else
			{
				System.out.println("ժҪ����ͬ");
			}
		}
		catch (java.security.NoSuchAlgorithmException ex)
		{
			System.out.println("�Ƿ�ժҪ�㷨");
		}
	}

	public String byte2hex(byte[] b) // ������ת�ַ���
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++)
		{
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}
}
