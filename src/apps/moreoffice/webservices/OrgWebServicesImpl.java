package apps.moreoffice.webservices;

import java.io.StringReader;

import javax.jws.WebService;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.MD5;


/**
 * 网络office为外界提供同步用户及组织结构的webservice接口。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
@WebService(endpointInterface = "com.yozo.webservices.OrgWebServices")
public class OrgWebServicesImpl implements OrgWebServices
{

	//@WebMethod
	public int CA_UpdateForSame (String xmlString, String strTrustId)
	{
		SAXReader saxReader = new SAXReader();    
        Document document = null;    
        try   
        {    
            document = saxReader.read(new StringReader(xmlString));    
        } catch (DocumentException e)    
        {    
            e.printStackTrace();    
            return 0;
        }    
        
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(
        		UserService.NAME);
        
        Element root = document.getRootElement();        
        Element userEle = root.element("user");
        Element orgEle = root.element("org");
        if (userEle != null)
        {
        	Element operEle = userEle.element("oper");
        	if (operEle != null)
        	{        		
        		String action = operEle.getText();
        		if ("add".equals(action))//增加用户
        		{
        			Element nameEle = userEle.element("name");
        			String realName = nameEle.getText();
        			Element idnameEle = userEle.element("id");
        			String name = idnameEle.getText();
        			Users info = userService.getUser(name);
        			if (info != null)
        			{
        				info.setValidate((short)1);
        				return 1;
        			}
        			Users user = new Users();
        	        user.setUserName(name);
        	        user.setRealName(realName);
        	        Element mailEle = userEle.element("mail");
        	        String mail = name+"@eio.cn";
        	        if (mailEle != null && !"".equals(mailEle.getText()))
        	        {
        	        	mail = mailEle.getText();	
        	        }
        	        
        	        user.setRealEmail(mail);
        	        //user.setEmail(mail);
        	        Element pswEle = userEle.element("password");
        	        String psw = "";
        	        if (pswEle != null)
        	        {
        	        	psw = pswEle.getText();	
        	        }
        	        user.setRole((short)4);
        	        user.setStorageSize(1024.0f);
        	        user.setCompanyId(Constant.PUBLICID);
        	        Element idEle = userEle.element("did");
        			String description = idEle.getText();
        			MD5 md5 = new MD5();
        	        String passW = md5.getMD5ofStr(psw);
        	        user.setPassW(passW);
        	        userService.saveUser(user);
        	        userService.saveMember(user,description);
        	        return 1;
        			
        		}
        		else if ("mod".equals(action))//修改用户
        		{
        			Element rN = userEle.element("name");
        			String realName = rN.getText();
        			
        			Element nameEle = userEle.element("id");
        			String name = nameEle.getText();
        			Users user = userService.getUser(name);
        			if (user != null)
        			{
        				user.setUserName(name);
            	        user.setRealName(realName);
            	        Element mailEle = userEle.element("mail");
            	        String mail = name+"@eio.cn";
            	        if (mailEle != null && !"".equals(mailEle.getText()))
            	        {
            	        	mail = mailEle.getText();	
            	        }
//            	        user.setRealEmail(mail);
//            	        user.setEmail(mail);
            	        Element pswEle = userEle.element("password");
            	        String psw = "";
            	        if (pswEle != null)
            	        {
            	        	psw = pswEle.getText();	
            	        }
            	        user.setRole((short)4);
            	        user.setStorageSize(1024.0f);
            	        user.setCompanyId(Constant.PUBLICID);
            	        Element idEle = userEle.element("did");
            			String description = idEle.getText();
            			MD5 md5 = new MD5();
            	        String passW = md5.getMD5ofStr(psw);
            	        user.setPassW(passW);
            	        userService.saveUser(user);
            	        userService.saveMember(user,description);
        				return 1;
        			}
        		}
        		else if ("deluser".equals(action) || "del".equals(action))
        		{
        			Element idnameEle = userEle.element("id");
        			String name = idnameEle.getText();
        			Users info = userService.getUser(name);
        			if (info != null)
        			{
        				info.setValidate((short)0);
        				return 1;
        			}
        		}
        	}
        }        
        else if (orgEle != null)
        {
        	Element operEle = orgEle.element("oper");
        	if (operEle != null)
        	{
        		String action = operEle.getText();
        		if ("add".equals(action))//增加组
        		{
        			Element pidEle = orgEle.element("pid");
        			long parentId = 0;
        			if (pidEle != null)
        			{
	        			String tparentId = pidEle.getText();
	        			Organizations groupinfo = userService.getGroupinfo(tparentId);
	        			if (groupinfo != null)
	        			{
	        				parentId = groupinfo.getId();
	        			}
        			}
        			
        			Element nameEle = orgEle.element("name");
        			String groupName = nameEle.getText();
        			
        			Element idEle = orgEle.element("id");
        			String description = idEle.getText();
        			
        			
        			Users info = userService.getUser("admin");
        			userService.createGroup(info.getId(), parentId, groupName, description, null);
        			return 1;
        		}
        		else if ("mod".equals(action))//修改组
        		{
        			Element pidEle = orgEle.element("pid");
        			long parentId = 0;
        			if (pidEle != null)
        			{
	        			String tparentId = pidEle.getText();
	        			Organizations groupinfo = userService.getGroupinfo(tparentId);
	        			if (groupinfo != null)
	        			{
	        				parentId = groupinfo.getId();
	        			}
        			}
        			Element nameEle = orgEle.element("name");
        			String groupName = nameEle.getText();
        			
        			Element idEle = orgEle.element("id");
        			String description = idEle.getText();
        			
        			userService.updateGroup(Long.valueOf(parentId), groupName, description);
        			return 1;
        		}
        	}
        }
        
		return 1;
	}
}

