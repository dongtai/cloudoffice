package test.weboffice.share;

import apps.moreoffice.ext.share.ThreadPoolManager;

public class TestEmail 
{
	public static void main(String[] args)
	{
		try
		{
			ThreadPoolManager tm=new ThreadPoolManager(2);
			tm.process("sun___@sohu.com", "test", "test");
			//SimpleThread st=new SimpleThread("sun___","767374","dongtai74@hotmail.com", "测试", "邮件");
			//st.sendMessage();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		System.out.println("successfully!!!!!!!!!!!!!");
		System.exit(0);
	}
}
