package apps.moreoffice.ext.share;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import apps.moreoffice.LocaleConstant;

class SimpleThread extends Thread
{
	private boolean runningFlag;
	private String argument;
    private String email_name="webmaster@chn-marketing.com";
    private String email_psw="61b6b812";
    private String from="webmaster@chn-marketing.com";
    private String fromname=LocaleConstant.instance.getValue("ext_linkNetworkCompany");
    private String host="61.129.65.224"; 
    PopupAuthenticator popAuthenticator=new PopupAuthenticator(email_name,email_psw);
    PasswordAuthentication pop = popAuthenticator.performCheck(email_name,email_psw);
	String emailto=null;
	String content=null;
	String subject=null;
	
	String emailto2=null;
	String content2=null;
	String subject2=null;
	String filename2=null;
	
	public SimpleThread(String email_name,String email_psw,String emailto,String content,String subject)
	{
		this.emailto=emailto;
		this.content=content;
		this.subject=subject;
		this.email_name=email_name;
        this.email_psw=email_psw;
		runningFlag = false;
	}
	public SimpleThread(String email_name,String email_psw,String emailto2,String content2,String subject2,String filename2)
	{
		this.emailto2=emailto2;
		this.content2=content2;
		this.subject2=subject2;
		this.filename2=filename2;
		this.email_name=email_name;
        this.email_psw=email_psw;
		runningFlag = false;
	}
	
    public String sendMessage() 
    { 
    	try
    	{
	        String to=emailto;
	        System.out.println(to);
	        if (subject==null)
	        {
	            subject = LocaleConstant.instance.getValue("ext_haveNewEmail");
	        }
	        String messageText=content;
	        boolean sessionDebug = false; 
	        Properties props = System.getProperties(); 
	        props.put("mail.smtp.host",host);
	        props.put("mail.smtp.auth","true");
	        props.put("mail.smtp.user",email_name);
	        props.put("mail.smtp.password",email_psw);
	        Session session = Session.getDefaultInstance(props, popAuthenticator); 
	        session.setDebug(sessionDebug); 
	 
	        
	        try { 
	        	sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder(); 
	            Message msg = new MimeMessage(session); 
	            msg.setFrom(new InternetAddress(from)); 
	            InternetAddress[] address = {new InternetAddress(to)};
	            msg.setRecipients(Message.RecipientType.TO, address); 
	            if (emailto2!=null)
	            {
	                InternetAddress[] ccaddress = {new InternetAddress(emailto2)};
	                msg.setRecipients(Message.RecipientType.CC, ccaddress);
	            }
	            
	        	 
	        	String strenc=enc.encode(subject.getBytes("GBK"));
	            msg.setSubject("=?GB2312?B?"+strenc+"?=");
      	        msg.setSentDate(new java.util.Date()); 
	            MimeMultipart mp = new MimeMultipart();
	            BodyPart bp = new MimeBodyPart();
	            
			    bp.setContent(messageText,"text/html;charset=GBK");
			    mp.addBodyPart(bp);
			    msg.setContent(mp);

	            msg.setFlag(Flags.Flag.DELETED, true);
		        msg.saveChanges();
	        	            
	            Transport.send(msg); 
	            System.out.println("mail finished!!!!!===="+strenc);
	            return strenc;
	        } 
	        catch (Exception mex) 
	        { 
	            System.out.println("not success "+mex.getMessage());
	            return "failure mail: ";
	        } 
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	System.out.println("mail finished!!!!!");
    	return "123";
    }
    public String getStr(String str)
    {
    	String temp=str;
    	if (temp!=null)
    	{
	    	try
	    	{
	    		temp=new String(temp.getBytes(), "utf8");
	    	}
	    	catch (Exception e)
	    	{
	    		System.out.println("getStr===="+e.getMessage());
	    	}
    	}
    	return temp;
    }
    public String sendMessageFile() 
    { 
        String host = "smtp.sohu.com"; 
        String to=emailto2;
        if (subject2==null)
        {
            subject2 = LocaleConstant.instance.getValue("ext_haveNewSuggest");
        }
        String messageText="";
        messageText = content2;
        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.smtp.host",host);
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.user",email_name);
        props.put("mail.smtp.password",email_psw);
        Session session = Session.getDefaultInstance(props, popAuthenticator); 
        session.setDebug(sessionDebug); 
        try
        {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from,LocaleConstant.instance.getValue("ext_linkNetworkCompany")));
            
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO,address);
            sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();  
            msg.setSubject("=?GB2312?B?"+enc.encode(subject2.getBytes("GBK"))+"?=");
            msg.setSentDate(new java.util.Date());
            msg.setText(messageText);
            msg.setFileName(filename2);
            Transport.send(msg);
            return LocaleConstant.instance.getValue("ext_sendedMailTip");
        }
        catch (Exception mex)
        { 
            System.out.println("not success "+mex.getMessage());
            mex.printStackTrace();
            return "failure mail: ";
        } 
    }

	
    public SimpleThread(String email_name,String email_psw) 
    {
        this.email_name=email_name;
        this.email_psw=email_psw;
        popAuthenticator=new PopupAuthenticator(email_name,email_psw);
        pop = popAuthenticator.performCheck(email_name,email_psw);
		runningFlag = false;
    }
	public void setEmailto(String emailto)
	{
		this.emailto=emailto;
	}
	public void setContent(String content)
	{
		this.content=content;
	}
	public void setSubject(String subject)
	{
		this.subject=subject;
	}

	public void setEmailto2(String emailto2)
	{
		this.emailto2=emailto2;
	}
	public void setContent2(String content2)
	{
		this.content2=content2;
	}
	public void setSubject2(String subject2)
	{
		this.subject2=subject2;
	}
	public void setFilename2(String filename2)
	{
		this.filename2=filename2;
	}

	
	
	
	
	
	public boolean isRunning()
	{
		return runningFlag;
	}
	public synchronized void setRunning(boolean flag)
	{
		runningFlag = flag;
		if(flag)
			this.notify();
	}
	public String getArgument()
	{
		return this.argument;
	}
	public void setArgument(String string)
	{
		argument = string;
	}
	public SimpleThread(int threadNumber)
	{
		runningFlag = false;
	}
	public synchronized void run()
	{
		try{
			while(true)
			{
				if(!runningFlag)
				{
					//System.out.println("thread is wait!!!");
					this.wait();
					//System.out.println("thread is wait end !!!");
				}
				else
				{
					//System.out.println("processing " + getArgument() + "... done.");
					//sleep(5000);
					//System.out.println("Thread is sleeping...");
					if (filename2!=null)
					{
						sendMessageFile();
					}
					else if (subject!=null)
					{
						sendMessage();
					}
					setRunning(false);
				}
			}
		} catch(InterruptedException e){
			System.out.println("Interrupt!!!!!!!!!!!!!!!!!!!!!");
		}
	}//end of run()
	
	
}//end of class SimpleThread 
