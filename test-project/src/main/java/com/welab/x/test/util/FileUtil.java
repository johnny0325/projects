package com.welab.x.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil
{
	public static ArrayList<String> readFileByLines(String fileName)
	{

		ArrayList<String> dataList = new ArrayList<String>();

		File file = new File(fileName);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null)
			{
				if (!StringUtil.isEmpty(tempString))
				{
					dataList.add(tempString);
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			dataList.clear();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e1)
				{
				}
			}
		}

		return dataList;
	}

	public static void fileOutput(final String fileName, final String content)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write(content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			try
			{
				bw.flush();
				bw.close();
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
			}
			return;
		}
		finally
		{
			try
			{
				bw.flush();
				bw.close();
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
			}
		}
	}
}
