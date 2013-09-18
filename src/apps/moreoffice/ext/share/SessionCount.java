package apps.moreoffice.ext.share;

import javax.servlet.*; 
import javax.servlet.http.*; 
import java.util.Vector;


public class SessionCount implements HttpSessionListener 
{ 
	private static int count=0; 
	private static Vector vector=new Vector();
	private static int number=0;
	
	public void sessionCreated(HttpSessionEvent se) 
	{ 
		
		count++; 
		try
		{
			for (int i=0;i<vector.size();i++)
			{
				HttpSessionEvent tempse=(HttpSessionEvent)vector.elementAt(i);
				HttpSession tempsession=tempse.getSession();
				long time=tempsession.getLastAccessedTime();
				long currenttime=System.currentTimeMillis();
				long difftime=currenttime-time;
				long totaltime=30*60*1000;
				//System.out.println(difftime+"===time===="+time);
				if (difftime>totaltime)
				{
					if (tempsession!=null)
					{
						count++;
						String session_id=tempsession.getId();
						//将当前会话注销
						if (session_id!=null)
						{
							deletesession(session_id);
						}
						tempsession.invalidate();
						tempsession.getSessionContext();
					}
					tempsession=null;
					tempse=null;
					vector.remove(i);
					count--;
				}
			}
			vector.addElement(se);
			//System.out.println("session创建："+new java.util.Date()+"size==="+vector.size()); 
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	} 
	
	public void sessionDestroyed(HttpSessionEvent se) 
	{ 
		count--;
		if (se!=null)
		{
			HttpSession tempsession=se.getSession();
			if (tempsession!=null)
			{
				String session_id=tempsession.getId();
				if (session_id!=null)
				{
					deletesession(session_id);
				}
			}
		}
		/**
		String error="";
		try
		{
			int size=vector.size();
			if (se!=null)
			{
				//error="1111111111111";
				HttpSession sess=se.getSession();
				//error="22222222222222";
				if (sess!=null)
				{
					String sessionid=sess.getId();
					//error="3333333333333333";
					//System.out.println("session id====="+sessionid+(number++));
					
					for (int i=0;i<size;i++)
					{
						HttpSessionEvent tempse=(HttpSessionEvent)vector.elementAt(i);
						if (tempse!=null)
						{
							//error="555555555555555555555";
							HttpSession tempsession=tempse.getSession();
							if (tempsession!=null)
							{
								//System.out.println(tempsession.getId()+"===========!!!!!!!!!"+i);
								String tempsessionid=tempsession.getId();
								if (sessionid!=null && sessionid.equals(tempsessionid))
								{
									//tempsession.invalidate();
									tempsession=null;
									tempse=null;
									vector.remove(i);
									System.out.println(tempsessionid+"====session removed is null!!!!!!!!!!!!"+i);
									//break;
								}
							}
						}
					}
					System.out.println(sessionid+"session销毁:"+new java.util.Date()+"size==="+vector.size());
				}
			}
			
		}
		catch(Exception e)
		{
			System.out.println(error+"========"+e.getMessage());
		}
		*/
	} 
	public void deletesession(String session_id)
	{
		try
		{
			String SQL="update totalinfo set effect='N' where effect='Y' and session_id='"+session_id+"'";
			QueryDb querydb=new QueryDb();
			querydb.modifydata(SQL);
		}
		catch (Exception e)
		{
			System.out.println("deletesession====="+e.getMessage());
		}
	}
	public static int getCount() 
	{ 
		int size=vector.size();
		if (size==0)
		{
			size=1;
		}
		return size;
		//return(count); 
	} 
	public static Vector getVector() 
	{ 
		return(vector); 
	}
}

