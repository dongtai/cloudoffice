package apps.moreoffice.ext.share;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;


public class PopupAuthenticator extends Authenticator
{
    String username=null;
    String password=null;
    public PopupAuthenticator(String name,String psw)
    {
        username=name;
        password=psw;
    }
    
    public PasswordAuthentication performCheck(String user,String pass)
    {
        username = user;
        password = pass;
        return getPasswordAuthentication();
    }
    protected PasswordAuthentication getPasswordAuthentication() 
    {
        return new PasswordAuthentication(username, password); 
    } 

}

