package apps.moreoffice.ext.share;

public class SendMail 
{
	public static ThreadPoolManager tm=new ThreadPoolManager(2);
	public static void sendmailto(String mailto,String content,String subject)
	{
		try
		{
			tm.process(mailto,content,subject);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		System.out.println("successfully!!!!!!!!!!!!!");
	}
}
