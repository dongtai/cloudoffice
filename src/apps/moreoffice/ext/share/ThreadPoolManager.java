package apps.moreoffice.ext.share;

import java.util.Vector;

public class ThreadPoolManager
{
	private int maxThread;
	public Vector vector;
	public void setMaxThread(int threadCount)
	{
		maxThread = threadCount;
	}
	public ThreadPoolManager(int threadCount)
	{
		setMaxThread(threadCount);
		vector = new Vector();
		for(int i = 1; i <= threadCount; i++)
		{
			SimpleThread thread = new SimpleThread(i);
			vector.addElement(thread);
			thread.start();
		}
	}
	public void process(String emailto,String content,String subject)
	{
		int i;
		for(i = 0; i < vector.size(); i++)
		{
			SimpleThread currentThread = (SimpleThread)vector.elementAt(i);
			if(!currentThread.isRunning())
			{
				//System.out.println("Thread "+ (i+1) +" is processing:" );
				//currentThread.setArgument(argument);
				currentThread.setEmailto(emailto);
//				if ("sah@emo3.com".equals(emailto))
//				{
//					currentThread.setEmailto2(emailto2);
//				}
//				else
//				{
					currentThread.setEmailto2(null);
//				}
				currentThread.setContent(content);
				currentThread.setSubject(subject);
				currentThread.setRunning(true);
				return;
			}
		}
		if(i == vector.size())
		{
			System.out.println("pool is full, try in another time.");
		}
	}
	public void process(String emailto2,String content2,String subject2,String filename2)
	{
		int i;
		for(i = 0; i < vector.size(); i++)
		{
			SimpleThread currentThread = (SimpleThread)vector.elementAt(i);
			if(!currentThread.isRunning())
			{
				//System.out.println("Thread "+ (i+1) +" is processing:" );
				//currentThread.setArgument(argument);
				currentThread.setEmailto2(emailto2);
				currentThread.setContent2(content2);
				currentThread.setSubject2(subject2);
				currentThread.setFilename2(filename2);
				currentThread.setRunning(true);
				return;
			}
		}
		if(i == vector.size())
		{
			System.out.println("pool is full, try in another time.");
		}
	}
}//end of class ThreadPoolManager 
