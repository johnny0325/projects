package com.example;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;

/**
 * ʵ��һ��������������������
 * Guess
 * Author��jlLin
 * Aug 27, 2011  4:06:02 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
class  Guess extends JFrame implements ActionListener
{
	JTextField tf = new JTextField(20);
	JButton start = new JButton("Start");
	JButton stop = new JButton("Stop");
	public Guess()
	{
		start.addActionListener(this);
		stop.addActionListener(this);
	//	Container c = getContentPane();
		JPanel panel = new JPanel();
	//	panel.setLayout(new FlowLayout());
		panel.add(start); 
		panel.add(stop);
		add(tf,"North");
		add(panel);
		setSize(300,100);
		setVisible(true);		
	}
	boolean flag = true;
	int a = 1;
	//Print p = new Print();
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		if(action.equals("Start"))
		{	
			flag = true;
			Print p = new Print();
			p.setName("hello"+a);
			p.start();//
			a++;
		}
		else
		{
			flag = false;
		}
	}
	
	//����һ����Ա�ڲ���
	class Print extends Thread
	{
		public void run()  //�߳���
		{
			//System.out.println(Thread.activeCount());	
			//	synchronized(tf)
			//	{
				while(flag)
				{
					synchronized(tf)
				{
					System.out.println(Thread.currentThread().getName());	
					//int location = (int)(Math.random()*43)+1;
					int location=(new Random()).nextInt(44);  
					tf.setText(""+location);
					try
					{
						Thread.sleep(100);
					}catch(Exception a)
					{
						a.printStackTrace();
					}
				}
			}	
		}
	}
	public static void main(String[] args) 
	{
		new Guess();
	}
}

