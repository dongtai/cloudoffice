package apps.transmanager.weboffice.service.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.Timer;

import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.ArrayUtils;

import apps.moreoffice.annotation.HandlerMethod;
import apps.moreoffice.annotation.ServerHandler;
import apps.moreoffice.ext.share.QueryDb;
import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.both.RoleCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.FileSystemActions;
import apps.transmanager.weboffice.databaseobject.MessageInfo;
import apps.transmanager.weboffice.databaseobject.MobileBackInfo;
import apps.transmanager.weboffice.databaseobject.MobileSendInfo;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.ReceptionDefaultUsers;
import apps.transmanager.weboffice.databaseobject.ReceptionUsers;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.SpacesActions;
import apps.transmanager.weboffice.databaseobject.SystemManageActions;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersConfig;
import apps.transmanager.weboffice.databaseobject.UsersDevice;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.UsersRoles;
import apps.transmanager.weboffice.databaseobject.UsersSynch;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetSameInfo;
import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.ITalkService;
import apps.transmanager.weboffice.service.approval.MeetUtil;
import apps.transmanager.weboffice.service.cache.IMemCache;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.impl.AddressListService;
import apps.transmanager.weboffice.service.impl.TalkService;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.license.LicenseService;
import apps.transmanager.weboffice.service.mail.send.MailSender;
import apps.transmanager.weboffice.service.objects.LoginUserInfo;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.service.server.AppsService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.LogServices;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.servlet.filter.UserFilter;
import apps.transmanager.weboffice.util.VerifyCodeUtils;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.BackgroundSend;
import apps.transmanager.weboffice.util.server.ClearTempfile;
import apps.transmanager.weboffice.util.server.DES;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;

/**
 * 处理用户登录，用户在线等与用户在线相关的内容。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */


@ServerHandler
public class UserOnlineHandler
{
	public final static String USER_LIST_KEY = "userListKey";
	private final static String DEFAULT_DOMAN = "com.yozo.do";
	//private static IMemCache memCache;
	private static Hashtable<String, HttpSession> sessiontable;
	private static Timer autoClearPubFiletimer;   //定期清除公共文件夹内文件(邮件发送的文件URL过期)
	public static UserOnlineHandler uoh = new UserOnlineHandler();
	
	private static ITalkService talk = null;
	private static UserService userService = null;
	private static String shareComment = "";
	private UserOnlineHandler()
	{
		init();
	}
	
	// 从原UploadServiceImpl中移植过来，后续在修改。 	
	private void init()
	{
		sessiontable = new Hashtable<String, HttpSession>();
		//memCache = (IMemCache)ApplicationContext.getInstance().getBean("memCacheBean");
        //memCache.cacheStart();
        //memCache.addCacheListener(this);
        
        autoClearPubFiletimer = new Timer(24 * 60 * 60 * 1000,new AutoClearFileAction());
        autoClearPubFiletimer.start();  
	}
	    
	private static IMemCache getMemCache()
	{
		return (IMemCache)ApplicationContext.getInstance().getBean("memCacheBean");
	}

    /**
     * 系统登录处理。
     * @param loginStr 登录用户名
     * @param password 登录密码
     * @param req
     * @return
     * @deprecated 原有方法，将删除
     */    
	public static String login(String loginStr, String password,  HttpServletRequest req, HttpServletResponse resp)
    {
    	return login1(loginStr, password, null, req, resp);
    }
    
	/**
     * @return
     * @deprecated 原有方法，将删除
     */ 
	public static void login(HttpServletRequest req, HttpServletResponse resp)  throws ServletException,  IOException
	{
		String loginStr = WebTools.converStr(req.getParameter("T2"));
        String password = WebTools.converStr(req.getParameter("T1"), "gb2312");
        String nopassword = String.valueOf(req.getParameter("nopassword"));
        String result = login1(loginStr, password, nopassword, req, resp);
        try
        {
        	resp.getWriter().print(result);
        }
        catch(Exception e)
        {
        	LogsUtility.error(e);
        }
	}
	
	/**
     * @return
     * @deprecated 原有方法，将删除
     */
	public static void autoLogin(HttpServletRequest req, HttpServletResponse resp)
	{
		String loginStr = WebTools.converStr(req.getParameter("T2"));
        String password = WebTools.converStr(req.getParameter("T1"), "gb2312");
        String nopassword = String.valueOf(req.getParameter("nopassword"));
        String result = login1(loginStr, password, nopassword, req, resp);
        try
        {
        	if (result.indexOf("pass") != -1)
        	{
        		resp.sendRedirect(req.getContextPath() + "/static/main.html");
        	}
        }
        catch(Exception e)
        {
        	LogsUtility.error(e);
        }
	}
	
    /**
     * 
     * @param loginStr
     * @param password
     * @param req
     * @deprecated 原有方法，将删除
     * @return
     */
    private static String login1(String loginStr, String password, String nopassword, HttpServletRequest req, HttpServletResponse resp)
    {
        try
        {
        	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
            DataHolder data = userService.loginCheck(loginStr.trim(), password, "true".equalsIgnoreCase(nopassword) ? true: false );
            int result = data.getIntData();
            if (result == Constant.PERMIT_USER)
            {
            	result = handleLogin1(loginStr, password, userService, data, req, resp);
                if (result == Constant.LONG_SUCESS)
                {
                    String s1 = WebofficeUtility.passwordEncrypt(loginStr);
                    String s2 = WebofficeUtility.passwordEncrypt(password);
                    String s = "pass" + "^" + s1 + "^" + s2 + "^" + data.getUserinfo().getUserName()
                        + "^" +data.getUserinfo().getRole();                    
                    return s;
                }
                else if (result == Constant.LOGINCA)
                {
                	 String s1 = WebofficeUtility.passwordEncrypt(loginStr);
                     String s2 = WebofficeUtility.passwordEncrypt(password);
                     String s = "pass" + "^" + s1 + "^" + s2 + "^" + data.getUserinfo().getUserName()
                         + "^" +data.getUserinfo().getRole();                    
                     return s;
                }
            }
            String s = Constant.AUTOLOGIN_ERROR_0;//"未知错误!";
            if (result == Constant.ILLEGAL_LICENSE)
            {
                s = Constant.AUTOLOGIN_ERROR_1;//"非法license!";
            }
            else if (result == Constant.ONLINE_USER_ILLEGAL)
            {
                s = Constant.AUTOLOGIN_ERROR_2;//"非法用户在线数!";
            }
            else if (result == Constant.ONLINE_MAX_USER)
            {
                s = Constant.AUTOLOGIN_ERROR_3;//"已经达到最大用户在线数!";
            }
            else if (result == Constant.LICENSE_END)
            {
                s = Constant.AUTOLOGIN_ERROR_4;//"license到期!";
            }
            else if (result == Constant.LICENSE_ILLEGAL_TIME)
            {
                s = Constant.AUTOLOGIN_ERROR_5;//"非法license时间!";
            }
            else if (result == Constant.NO_USER)
            {
                s = (Constant.AUTOLOGIN_ERROR_7);//"登录名对应的帐户不存在!");
            }
            else if (result == Constant.PASSWORD_ERROR)
            {
                s = (Constant.AUTOLOGIN_ERROR_8);//"登录名与密码不匹配!");
            }
            else if (result == Constant.COMPANY_ERROR)
            {
                s = (Constant.AUTOLOGIN_ERROR_9);//"公司ID错误!");
            }
            else if (result == Constant.NO_PERMIT_USER)
            {
                s = (Constant.AUTOLOGIN_ERROR_10);//"帐户已被禁用,如有疑问请与管理员联系!");
            }
            else if (result == Constant.HAS_EXISTED)
            {
                s = (Constant.AUTOLOGIN_ERROR_11);//"当前用户已经登陆!");
            }
            else if (result == Constant.TEMP_ERROR)
            {
                s = (Constant.AUTOLOGIN_ERROR_6);//"文件系统异常!");
            }
            else if (result == Constant.LOGINCA)
            {
                s = "LOGINCA_SET_MSG";//您的当前登录设置为证书登录，请使用证书登录方式登录
            }
            return s;
        }
        catch (Exception e) 
        {
            LogsUtility.error(e);
            return "远程调用异常，登录失败，请联系管理员。";
        }
    }
    
    /**
     * 单点登录处理
     * @param userMapping 单点登录用户数据
     * @param req
     * @return
     */
    public static int SSOLogin(Hashtable<String, Object> userMapping, HttpServletRequest req, HttpServletResponse resp)
    {
    	UserService userService = getUserService();
    	DataHolder data = userService.SSOLogin(userMapping);
    	return handleLogin1((String)userMapping.get(PropsConsts.USER_NAME), "123456", userService, data,	req, resp);
    }
    
    /**
     * 
     * @param loginStr
     * @param password
     * @param userService
     * @param data
     * @param req
     * @param resp
     * @deprecated 原有方法，将删除
     * @return
     */
    private static int handleLogin1(String loginStr, String password, UserService userService, DataHolder data,
    		HttpServletRequest req, HttpServletResponse resp)
    {
    	HttpSession session = req.getSession();
    	handleSameSession(session, DEFAULT_DOMAN);    // 处理同一个浏览器中多个账户多次登录.
    	
        String sessionID = session.getId();
        Users uinfo = data.getUserinfo();
        uinfo.setToken(sessionID);             

        
        String userName = uinfo.getUserName();  
        userName = userName.toLowerCase();
        LicenseService licenseService = (LicenseService)ApplicationContext.getInstance().getBean("licenseService");
        //boolean flag = handleRepUser(userService, userName, getRealIpAddr(req), session.getId());
        /*if (!flag)
        {
	        LoginUserInfo logUserInfo = memCache.getLoginUserInfo(userName);
	        //System.out.println("the set rep login user =================   "+logUserInfo);
	        if (logUserInfo != null)
	        {	
	        	memCache.setRepUser(userName, getRealIpAddr(req));
	        }
        }*/
        
        int licenseResult;
        IMemCache  memCache = getMemCache();
        Integer ltc = memCache.getLoginUserCount(); 
        ltc = ltc == null ? 1 : ltc + (memCache.getLoginUserInfo(DEFAULT_DOMAN + "+" + userName) != null ? 0 : 1);
        //System.out.println("The login user count ============+++++++++" + ltc);
        
        // 管理员暂时不检查。
        int role = uinfo.getRole();
        String tempName = DEFAULT_DOMAN + "+" + userName;
        if (role == Constant.ADIMI || role == Constant.USER_ADMIN || role == Constant.AUDIT_ADMIN || role == Constant.SECURITY_ADMIN
            || (licenseResult = licenseService.checkOnlinUser(ltc)) == Constant.ONLINE_USER_PER)
        {
        	handleRepUser(userService, tempName, getRealIpAddr(req), session.getId());
            session.setAttribute("userKey", uinfo);
            session.setAttribute("IPAdr", getRealIpAddr(req));
            session.setAttribute("isLoginContinue", new Boolean(true)); 
            int repResult = userService.loginRepository1(data.getUserinfo(), password);
            if (repResult == Constant.PERMIT_USER)
            {
				//add by user685
            	//Loginfo loginfo = new Loginfo(uinfo.getUserName(), getRealIpAddr(req), LogConstant.OPERATE_TYPE_LOGIN);
            	session.setAttribute("operateType", LogConstant.OPERATE_TYPE_LOGIN);
            	session.setAttribute("domain", DEFAULT_DOMAN);     // 先如此兼容

                //sessionList.add(session);
            	//String keMail = uinfo.getEmail();
            	String token = sessionID + System.currentTimeMillis(); 
            	memCache.setLoginUserInfo(tempName, new LoginUserInfo(DEFAULT_DOMAN, userName,
            			token, getRealIpAddr(req)));
            	//memCache.setLoginUserCount(ltc);
            	
            	session.setAttribute(USER_LIST_KEY, userName);
            	sessiontable.put(tempName, session); 
            	
            	// user290 2011-10-06
            	session.setAttribute("key_userid", uinfo.getId().toString());
            	session.setAttribute("key_email", uinfo.getSpaceUID());
            	
                try
                {
                    String s1 = WebofficeUtility.passwordEncrypt(loginStr);
	                Cookie history = new Cookie("history", URLEncoder.encode(s1, "UTF-8"));
	                long expires = System.currentTimeMillis() + 360 * 24 * 60 * 60 * 1000;
	                history.setMaxAge((int)expires);
	                resp.addCookie(history);
	                //LogsUtility.logToFile("", WebofficeUtility.getFormateDate2(new Date(),"-") + ".log", true, loginfo);
	                LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME);
	                logServices.setLogin(uinfo, getRealIpAddr(req), token);

                }
                catch(Exception e)
                {
                	e.printStackTrace();
                }
                return Constant.LONG_SUCESS;
            }
            else
            {
                return repResult;
            }
        }
        else
        {
            return licenseResult;
        }
    }
    
    
    /**
     * 处理同一个浏览器中多次登录用户
     * @param session
     */
    private static void handleSameSession(HttpSession session, String domain)
    {
    	Users userinfo = (Users)session.getAttribute("userKey");
    	if (userinfo != null)
    	{
    		String userID = userinfo.getSpaceUID();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
    		try
    		{
    			jcrService.removeUserAllOpenedFile(userinfo.getUserName(), userinfo.getSpaceUID());
    			//退出时，清空打开列表
    			//jcrService.clearFileList(userID, 0);
    			//退出时，遍历锁定列表，如果有本人锁定的文件，则取消锁定
    			//jcrService.clearFileList(userID, 1);
    			//退出时，清空关闭列表
    			//jcrService.clearFileList(userID, 2);
    		}
    		catch(RepositoryException e)
    		{
    			e.printStackTrace();
            }
    		IMemCache  memCache = getMemCache();
    		memCache.removeLoginUserInfo(domain + "+" + userinfo.getUserName().toLowerCase());
    		//Integer ltc = memCache.getLoginUserCount();
    		//ltc = ltc == null ? 0 : ltc - 1;
    		//memCache.setLoginUserCount(ltc);
    		try
    		{
    			sessiontable.remove(domain + "+" + session.getAttribute(USER_LIST_KEY));
    		}
    		catch(Exception e)
    		{
    			
    		}
    	}
    }
    
    private static boolean handleRepUser(UserService userService, 
    		String name, String ip, String sessionId)
    {
    	HttpSession otherSameUser = getOtherSameSession(name, sessionId);        
        if (otherSameUser != null)
        {
            try
            {
            	Users info = (Users)otherSameUser.getAttribute("userKey");
                String userID = info.getSpaceUID();
                JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                		JCRService.NAME);
                try
                {
                	jcrService.removeUserAllOpenedFile(info.getUserName(), info.getSpaceUID());
                    //退出时，清空打开列表
                    //jcrService.clearFileList(userID, 0);
                    //退出时，遍历锁定列表，如果有本人锁定的文件，则取消锁定
                    //jcrService.clearFileList(userID, 1);
                    //退出时，清空关闭列表
                    //jcrService.clearFileList(userID, 2);
                }
                catch(RepositoryException e)
                {
                    e.printStackTrace();
                }
                otherSameUser.removeAttribute("userKey");
                otherSameUser.removeAttribute("t1");
                otherSameUser.setAttribute("IPAdr", ip);
                sessiontable.remove(name);//otherSameUser.getAttribute(USER_LIST_KEY));
                
                MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
                ArrayList userIds = new ArrayList();
                userIds.add(info.getId());
                messageService.sendMessage("forcequit", "userId",
                		Constant.UPLOADSERVICE_ERROR_14_0 + ip + Constant.UPLOADSERVICE_ERROR_14_1, MessageCons.FORCE_QUIT, userIds);
                
                //sessionList.remove(otherSameUser);
                //licenseService.checkLogoutUser();
                //Integer ltc = memCache.getLoginUserCount(); 
                //ltc = ltc == null ? 0 : ltc - 1;
                //System.out.println("rep user =====================  logout   "+ltc);
                //memCache.setLoginUserCount(ltc);
            }
            catch(Exception e)
            {
            	e.printStackTrace();
                sessiontable.remove(name);
            }
        }
        return false;
    }
    
    private static HttpSession getOtherSameSession(String name, String sessionId)  //HttpSession session1)
    {        
    	//String mail = ((Userinfo)(session1.getAttribute("userKey"))).getEmail();
        HttpSession session2 = sessiontable.get(name);
        if (sessionId != null && session2 != null && session2.getId() == sessionId)  //session1.getId())
        {
            return null;
        }
        else
        {
            return session2;
        }        
    }
    
    public static boolean quit(HttpSession session)
    {
    	return quit(session, true, null);
    }
    
    public static boolean quit(HttpSession session, boolean isInvalidate, String domain)
    {
        if (session == null)
        {
            return false;
        }
        Users info = (Users)session.getAttribute("userKey");
        if(info==null)
        {
            return false;
        }
        // 管理员暂时不检查。



        if (domain == null)
        {
        	domain = (String)session.getAttribute("domain");
        }
        String mailKey = (String)session.getAttribute(USER_LIST_KEY);
        if (mailKey != null)
        {
            sessiontable.remove(domain + "+" + mailKey);
        }
        
        /*if (info.getRole() != 1)
        {
	        LicenseService licenseService = (LicenseService)ApplicationContext.getInstance().getBean("licenseService");
	        licenseService.checkLogoutUser();
        }*/
        
        //String userID = info.getEmail().replace('@', '_');
        //Integer ltc = memCache.getLoginUserCount(); 
        //ltc = ltc == null ? 0 : ltc - 1;
        //memCache.setLoginUserCount(ltc);
        
        String mail = info.getSpaceUID();
        //System.out.println("quit==================================remove user "+mail);
        
        IMemCache  memCache = getMemCache();
        LoginUserInfo lui = memCache.getLoginUserInfo(domain + "+" + info.getUserName().toLowerCase());
        String token = lui != null ? lui.getToken() : null;
        	
        memCache.removeLoginUserInfo(domain + "+" + info.getUserName().toLowerCase());
        String userID = mail;//.replace('@', '_'); 
        
        session.setAttribute("operateType", LogConstant.OPERATE_TYPE_QUIT);

        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
            jcrService.removeUserAllOpenedFile(info.getUserName(), info.getSpaceUID());
            //jcrService.clearUserOpenFile(info.getSpaceUID());
            //退出时，清空打开列表
            //jcrService.clearFileList(userID, 0);
            //退出时，遍历锁定列表，如果有本人锁定的文件，则取消锁定
            //jcrService.clearFileList(userID, 1);
            //退出时，清空关闭列表
            //jcrService.clearFileList(userID, 2);
            jcrService.logOut(userID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //add by user685
        //String record = Constant.USER_NAME + info.getUserName()+"; " + Constant.USER_REALNAME + info.getRealName() + ";  " + Constant.OPERATE_TIME + new Date().toLocaleString() + ";  " 
        //	+ Constant.IP_ADDRESS + session.getAttribute("IPAdr") + ";  " + Constant.OPERATE_TYPE + session.getAttribute("operateType") + "\n\r";
        //LogsUtility.writeToFile(webServerPath + File.separatorChar + "log/", WebofficeUtility.getFormateDate2(new Date(),"-") + ".log", record, true);
        //Loginfo loginfo = new Loginfo(info.getUserName(), (String)session.getAttribute("IPAdr"), (String)session.getAttribute("operateType"));
        //LogsUtility.logToFile("", WebofficeUtility.getFormateDate2(new Date(),"-") + ".log", true, loginfo);
        LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME);
        logServices.setLogout(info, (String)session.getAttribute("IPAdr"), token);
        /**
         * 此处理论上没必要按个删除属性，最后invalidate方法会统一清除，
         * 但实际情况并非如此，故增强容错能力
         */
        session.removeAttribute("userKey");
        session.removeAttribute("IPAdr");
        session.removeAttribute("isLoginContinue");
        session.removeAttribute("logID");
        if (isInvalidate)
        {
        	session.invalidate();
        }
        
        return true;
    }
    
    public static void applicationQuit()
    {
    	int size;
    	if ((size = sessiontable.size()) > 0)
    	{
    		//Integer ltc = memCache.getLoginUserCount(); 
            //ltc = ltc == null ? 0 : ltc - size;
            //System.out.println("container quit =====================  logout   "+ltc);
            //memCache.setLoginUserCount(ltc);
            Enumeration<String> enu = sessiontable.keys();
            String key;
            while (enu.hasMoreElements())
            {
            	key = enu.nextElement();
            	quit(sessiontable.get(key));
	            //System.out.println("quit==================================remove user "+key);
            	//String domain = (String)sessiontable.get(key).getAttribute("domain");
                //memCache.removeLoginUserInfo(domain + "+" + info.getUserName());
	            //memCache.removeLoginUserInfo(key);
            }
            
    		//System.out.println("+++++++++++++++++++++++te count is   +"+size);
	    	//LicenseService licenseService = (LicenseService)ApplicationContext.getInstance().getBean("licenseService");
	        //licenseService.checkLogoutUser(size);
    	}
    	IMemCache  memCache = getMemCache();
    	memCache.cacheStop();
    }
    
    public static void clearAllSession()
    {
    	clearAllSession(null);
    }
    public static void clearAllSession(HttpSession mysession)
    {
    	if (sessiontable!=null)
    	{
    		Enumeration<String> all = sessiontable.keys();
    		while (all.hasMoreElements())
    		{
    			String key=all.nextElement();
    			HttpSession session=sessiontable.get(key);
    			if (session!=null)
    			{
    				if (mysession!=null)
    				{
    					if (mysession.getId().equals(session.getId()))
    					{
    						
    					}
    					else
    					{
    						session.invalidate();
    					}
    				}
    				else
    				{
    					session.invalidate();
    				}
    			}
    			sessiontable.remove(key);
    		}
    	}
    }
    
    /**
     * 检查某个EMail对应的用户是否在线
     * @param se
     * @param email
     * @return
     */  
    public static boolean checkUserLoginStatus(HttpSession se, String email)
    {
//        HttpSession session = sessiontable.get(email);
//        if (session == null)
//        {
//            return false;
//        }
//        Userinfo user1 = (Userinfo)(session.getAttribute("userKey"));
//        if (user1 == null)
//        {
//            return false;
//        }
//        String mail1 = user1.getEmail();
//        if (mail1.equals(email))
//        {
//            return true;
//        }
//        return false;

    	return true;
        /*int size = sessionList.size();
        //        Userinfo user2 = (Userinfo)(se.getAttribute("userKey"));
        //        if (user2 != null)
        //        {
        //            return true;
        //        }
        for (int i = 0; i < size; i++)
        {
            try
            {
                HttpSession session = sessionList.get(i);
                Userinfo user1 = (Userinfo)(session.getAttribute("userKey"));
                if (user1 == null)
                {
                    continue;
                }
                String mail1 = user1.getEmail();
                //                String mail2 = email;
                if (mail1.equals(email))
                {
                    return true;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();                
            }
        }
        return false;*/
    }
    
    private static String getRealIpAddr(HttpServletRequest request)
    {
        return WebTools.getRealIpAddr(request);
    }
    
    class MyTimerAction implements ActionListener
    {
        //HttpSession session;
        public MyTimerAction()
        {
            //session = s;
        }

        public void actionPerformed(ActionEvent event)
        {
            long t2 = System.currentTimeMillis();
            if(sessiontable == null)
            {
            	return;
            }
            int size = sessiontable.size();
            /*if (frame != null)
            {
                frame.setTotalSessionCount(size);
            }*/
            int activeCount = 0;   
            try
            {
	            for (HttpSession session : sessiontable.values())
	            {
	                String mailKey = "";
	                try
	                {
	                    mailKey = (String)session.getAttribute(USER_LIST_KEY);
	                    Long T1 = ((Long)(session.getAttribute("t1")));
	                    Boolean isLoginContinue = ((Boolean)(session.getAttribute("isLoginContinue")));
	                    if (T1 == null || isLoginContinue != null)
	                    {
	                        continue;
	                    }
	                    long t1 = T1.longValue();
	                    if ((t2 - t1) / 1000 > 30)
	                    {
	                        //心跳停止超过30秒,表明客户非正常退出(浏览器直接关闭,断电,死机....等一切意外退出),清除session
	                    	Users info = (Users)session.getAttribute("userKey");
	                        if (info == null)
	                        {
	                            continue;
	                        }
	                        quit(session);
	                    }
	                }
	                catch(Exception e)
	                {
	                    if (!mailKey.equals(""))
	                    {
	                        sessiontable.remove(mailKey);
	                    }
	                    continue;
	                }
	            }
            }
            catch (Exception ee)
            {
            	ee.printStackTrace();
            }
        }
    }
    
    /**
     * 定期清除公共文件夹内文件(邮件发送的文件URL过期),以文件的最后修改日期为基准,超过10天的文件必需删除
     */    
    class AutoClearFileAction implements ActionListener
    {
        //HttpSession session;
        public AutoClearFileAction()
        {
            //session = s;
        }

        public void actionPerformed(ActionEvent event)
        {
            String tempFolder = WebConfig.sendMailPath; //getServletContext().getRealPath("sendmailfile");
            File f = new File(tempFolder);
            File[] fs = f.listFiles();
            if (fs == null)
            {
                return;
            }
            for (int i = 0; i < fs.length; i++)
            {
                long lastTime = fs[i].lastModified();
                long currTime = System.currentTimeMillis();
                long dis = (20 * 24 * 60 * 60 * 1000);
                //                long a = 1245307341234L/(24*60*60*1000*365);
                //                System.out.println("a: "+(currTime-lastTime)/(24*60*60*1000));
                if ((currTime - lastTime) > dis)
                {
                    boolean delSucc = fs[i].delete();
                }
                //                System.out.println("lastTime: "+lastTime);
            }
            /*UserService userService = (UserService)ApplicationContext.getInstance().getBean(
            		UserService.NAME);
            
            userService.delberfore20days();*/
        }
    }
    
    //新建立的方法，该方法可以取得在线用户列表
    public static List<String> getActivedUser()
    {
    	long t2 = System.currentTimeMillis();
    	ArrayList<String> list = new ArrayList<String>();
    	IMemCache  memCache = getMemCache();
    	Map<String, LoginUserInfo>  userMap= memCache.getAllLoginUser();
    	//注意，不要导错包
    	java.util.Iterator<Entry<String, LoginUserInfo>> it =  userMap.entrySet().iterator();
    	Collection<LoginUserInfo> c = userMap.values();
    	try
        {
    		while(it.hasNext()){
     			Map.Entry  entry = it.next();
     			LoginUserInfo info = (LoginUserInfo)entry.getValue();
    			 list.add(info.getName() + ";" + info.getIp());
    		}
        }
        catch (Exception ee)
        {
        	ee.printStackTrace();
        }
        return list;
    }

//    public static List<String> getActivedUser()
//    {
//    	long t2 = System.currentTimeMillis();
//    	ArrayList<String> list = new ArrayList<String>();
//    	
//    	Map<String, LoginUserInfo>  userMap= memCache.getAllLoginUser();
//    	
//    	try
//        {
//            for (HttpSession session : sessiontable.values())
//            {
//                try
//                {
//                    Long T1 = ((Long)(session.getAttribute("t1")));
//                    Boolean isLoginContinue = ((Boolean)(session.getAttribute("isLoginContinue")));
//                    if (T1 == null || isLoginContinue != null)
//                    {
//                        continue;
//                    }
//                    long t1 = T1.longValue();
//                    if ((t2 - t1) / 1000 > 30)
//                    {
//                        //心跳停止超过30秒,表明客户非正常退出(浏览器直接关闭,断电,死机....等一切意外退出),清除session
//                        /*Userinfo info = (Userinfo)session.getAttribute("userKey");
//                        if (info == null)
//                        {
//                            continue;
//                        }*/
//                        
//                    }
//                    else
//                    {
//                        try
//                        {
//                        	Users info = (Users)session.getAttribute("userKey");
////                        	list.add(info.getEmail() + ";" + session.getAttribute("IPAdr"));
//                        	list.add(info.getRealName() + ";" + session.getAttribute("IPAdr"));
//                        }
//                        catch(Exception e)
//                        {
//                        }
//                    }
//                }
//                catch(Exception e)
//                {
//                    
//                }
//            }
//        }
//        catch (Exception ee)
//        {
//        	ee.printStackTrace();
//        }
//        return list;
//    }
    
    /**
     * 心跳检测
     * @param req
     * @param resp
     */
    public static void heartbeat(HttpServletRequest req, HttpServletResponse resp)
    {
    	resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
    	resp.setHeader("Connection", "close");
        long t1 = System.currentTimeMillis();
        HttpSession session = req.getSession();
        Object ip = session.getAttribute("IPAdr");
        try
        {
	        if (session.getAttribute("userKey") == null && ip != null)
	        {
	            session.removeAttribute("IPAdr");
	            //resp.getWriter().print("您的账号在异地(" + ip + ")被人登录,被迫退出,请重新登录!");
	            resp.getWriter().print(Constant.UPLOADSERVICE_ERROR_14_0 + ip + Constant.UPLOADSERVICE_ERROR_14_1);
	        }
	        else
	        {
	            session.setAttribute("t1", t1);
	            resp.getWriter().print("");
	        }
        }
        catch(Exception e)
        {
        	LogsUtility.error(e);
        }
    }
    /**
     * 
     * @param req
     * @param resp
     * @deprecated
     */
    public static void onlineCheck(HttpServletRequest req, HttpServletResponse resp)
    {
    	resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
    	try
    	{
    		resp.getWriter().print("");
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    	
    }
    
    /**
     * 
     * @param req
     * @param resp
     * @deprecated
     */
    @Deprecated
    public static void furbish(HttpServletRequest req, HttpServletResponse resp)
    {
    	try
    	{
	    	String history = WebTools.converStr(req.getParameter("history"));
	        if (!history.trim().equals(""))
	        {
	            resp.getWriter().print(WebofficeUtility.furbishPassword(history));
	            return;
	        }
	        String userName = WebTools.converStr(req.getParameter("userName"));
	        String password = WebTools.converStr(req.getParameter("password"));
	        if (!userName.trim().equals(""))
	        {
	            userName = WebofficeUtility.furbishPassword(userName);
	            password = WebofficeUtility.furbishPassword(password);            
	            resp.getWriter().print(userName + "^" + password);
	            return;
	        }
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    }
    
    public static void quit(HttpServletRequest req, HttpServletResponse resp)
    {
    	String context = req.getContextPath();
//    	String url = context + "/static/login.html";
    	String url = context + UserFilter.redirectURL;
    	System.out.println("url======"+url);
    	try
    	{
        	boolean flag = PropsValue.isSSOLogin(); 
            quit(req.getSession(), !flag, null);
//        String aa = req.getParameter("gwtquit");
//        if ("true".equals(aa))
//        {
        	if (flag)
            {
            	resp.sendRedirect(PropsValue.get(PropsConsts.SSO_LOGOUT_URL));
            	/*try
            	{
            		req.getRequestDispatcher(PropsValue.get(PropsConsts.SSO_LOGOUT_URL)).forward(req, resp);
            	}
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}*/
            }
            else
            {
            	if ("https".equals(WebConfig.accesstype))
            	{
//            		System.out.println(context + "/cloud/logoutCA.jsp");
            		resp.sendRedirect("https://"+WebConfig.serverurlname + "/cloud/logoutCA.jsp");
            	}
            	else
            	{
            		resp.sendRedirect(url);
            	}
            }            	
        }
        catch(Exception e)
        {
        	System.out.println("exception ----- flag");
        	try
        	{
        		resp.sendRedirect(url);
        	}
        	catch(Exception ee)
        	{
        		
        	}
        }
    }
    
    public static void loadName(HttpServletRequest req, HttpServletResponse resp)
    {
    	HttpSession session = req.getSession();
    	Users userInfo = (Users)session.getAttribute("userKey");
    	if (userInfo != null)
    	{
        	String username = userInfo.getRealName();
        	if (username == null)
        	{
        		username = userInfo.getUserName();
        	}
        	try
        	{
        		resp.getWriter().print(username);
        	}
        	catch(Exception e)
        	{
        		LogsUtility.error(e);
        	}
    	}
    }
    
    public static void checkLogin(HttpServletRequest req, HttpServletResponse resp)
    {
    	Users userInfo = (Users) req.getSession().getAttribute("userKey");
    	try
    	{
	    	if(null != userInfo)
	    	{
	    		resp.getWriter().print("true#"+userInfo.getRole());
	    	}
	    	else
	    	{
	    		resp.getWriter().print("false#");
	    	}
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
	}
    /**
     * 贵州那边需要的
     * @param req
     * @param resp
     */
    public static void CASessionLogin(HttpServletRequest req, HttpServletResponse resp)
    {
    	String caId = (String)req.getAttribute("calogin");
    	if (caId==null)
    	{
    		caId=(String)req.getSession().getAttribute("calogin");
    	}
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	try
    	{
    		if (caId==null)
        	{
        		resp.sendRedirect("/index.jsp");
        		return;
        	}
    		Users userInfo = userService.getUserByCaId(caId);
			if (userInfo!=null)//nanjing && "1".equals(userInfo.getLoginCA()))
			{
				String uname=userInfo.getUserName();
	    		if (uname==null || uname.length()==0 || uname.indexOf(" ")>=0 || uname.indexOf("\n")>=0)
	    		{
	    			uname="gz"+System.currentTimeMillis();
	    			userInfo.setUserName(uname);
	    			userService.updataUserinfo(userInfo);
	    		}
			    DataHolder data = userService.loginCheckCA(userInfo);
			    int result = data.getIntData();
			    if (result == Constant.PERMIT_USER)
			    {
			    	HttpSession session = req.getSession();
			    	result = handleLogin1(userInfo.getUserName(),  "", userService, data, req, resp);
//			        if (result == Constant.LONG_SUCESS) //nanjing
			        {
			        	IMemCache  memCache = getMemCache();
			        	System.out.println("send Redirect to calogin.html");
			        	Users uinfo = data.getUserinfo();			        	
			        	LoginUserInfo lui = memCache.getLoginUserInfo("com.yozo.do" + "+" + uinfo.getUserName().toLowerCase());
			            String token = lui.getToken();
			            System.out.println("============="+token);
			        	resp.sendRedirect("/static/default.jsp?token=" + token + "&username=" + URLEncoder.encode(uinfo.getUserName(), "UTF-8"));//nanjing
			        }
			    }
			} 
			else 
			{
				resp.sendRedirect("/index.jsp");
			}
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    }
    public static void CALogin(HttpServletRequest req, HttpServletResponse resp)
    {
    	String caId = req.getParameter("caId");        	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	
    	Users userInfo = userService.getUserByCaId(caId);
System.out.println("caid====="+caId);
    	try
    	{
			if (userInfo!=null)//nanjing && "1".equals(userInfo.getLoginCA()))
			{
				String uname=userInfo.getUserName();
	    		if (uname==null || uname.length()==0 || uname.indexOf(" ")>=0 || uname.indexOf("\n")>=0)
	    		{
	    			uname="gz"+System.currentTimeMillis();
	    			userInfo.setUserName(uname);
	    			userService.updataUserinfo(userInfo);
	    		}
			    DataHolder data = userService.loginCheckCA(userInfo);
			    int result = data.getIntData();
			    if (result == Constant.PERMIT_USER)
			    {
			    	HttpSession session = req.getSession();
			    	result = handleLogin1(userInfo.getUserName(),  "", userService, data, req, resp);
//			        if (result == Constant.LONG_SUCESS) //nanjing
			        {
			        	IMemCache  memCache = getMemCache();
			        	System.out.println("send Redirect to calogin.html");
			        	Users uinfo = data.getUserinfo();
			        	LoginUserInfo lui = memCache.getLoginUserInfo("com.yozo.do" + "+" + uinfo.getUserName().toLowerCase());
			            String token = lui.getToken();
			            System.out.println("============="+token);
			        	resp.sendRedirect("/cloud/index.html?token=" + token + "&username=" + URLEncoder.encode(uinfo.getUserName(), "UTF-8"));//nanjing
			        }
			    }
			} 
			else 
			{
				resp.sendRedirect("login.html?loginCA=notSet");
			}
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    }
    
    @Deprecated
    public static void uploadUserPortrait(HttpServletRequest req, HttpServletResponse resp) throws ServletException,  IOException
    {

    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	String uid = WebTools.converStr(req.getParameter("userId"));
    	Long userId = null;
    	if (uid != null && !"".equals(uid))
    	{
    		userId = Long.valueOf(uid);
    	}
    	String ret = userService.addOrUpdateUserPortrait(userId, WebConfig.userPortraitPath, req);        	
    	if (ret != null)
    	{
    	    File file = new File(WebConfig.userPortraitPath + File.separatorChar + ret);
            long fileSize = file.length();
            if (fileSize > 80 * 1024)//不允许上传大于200k的头像
            {
                file.delete();
                resp.getWriter().print("{success:false,message:'invalid'}");
            }
    	    else
    	    {
    	        resp.getWriter().print("{success:true,message:'上传成功',base:'" + WebConfig.userPortrait + "',url:'" + ret + "'}");
    	    }
    	}
    }
    
    public static void uploadUserPortraitbyCamera(HttpServletRequest req, HttpServletResponse resp) throws ServletException,  IOException
    {

    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	String uid = WebTools.converStr(req.getParameter("userId"));
    	Long userId = null;
    	if (uid != null && !"".equals(uid))
    	{
    		userId = Long.valueOf(uid);
    	}
    	String ret = userService.addOrUpdateUserPortraitbyCamera(userId, WebConfig.userPortraitPath, req);        	
    	if (ret != null)
    	{
//    		resp.getWriter().print(ret);
    		resp.getWriter().print("{success:true,message:'上传成功',base:'" + WebConfig.userPortrait + "'url:'"  + ret + "'}");
    	}
    }
    
    public static void uploadGroupPortrait(HttpServletRequest req, HttpServletResponse resp) throws ServletException,  IOException
    {
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	String uid = WebTools.converStr(req.getParameter("groupId"));
    	Long groupId = null;
    	if (uid != null && !"".equals(uid))
    	{
    		groupId = Long.valueOf(uid);
    	}
    	String ret = userService.addOrUpdateGroupPortrait(groupId, WebConfig.groupPortraitPath, req);        	
    	if (ret != null)
    	{
    	    File file = new File(WebConfig.groupPortraitPath + File.separatorChar + ret);
            long fileSize = file.length();
            if (fileSize > 80 * 1024)//不允许上传大于200k的头像
            {
                file.delete();
                resp.getWriter().print("invalid");
            }
            else
            {
                resp.getWriter().print("{success:true,message:'上传成功',base:'" + WebConfig.groupPortrait + "',url:'"+ret+"'}");
            }
    	}
    }
    
    public static void uploadOrgPortrait(HttpServletRequest req, HttpServletResponse resp) throws ServletException,  IOException
    {
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	String uid = WebTools.converStr(req.getParameter("orgId"));
    	Long orgId = null;
    	if (uid != null && !"".equals(uid))
    	{
    		orgId = Long.valueOf(uid);
    	}
    	String ret = userService.addOrUpdateOrgPortrait(orgId, WebConfig.orgPortraitPath, req);        	
    	if (ret != null)
    	{
//    		resp.getWriter().print(ret);
    		resp.getWriter().print("{success:true,message:'上传成功',base:'" + WebConfig.orgPortrait + "',url:'"+ret+"'}");
    	}
    }
    
    /**
     * 重新设置用户密码，并发邮件。
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public static void resetUserPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException,  IOException
    {
    	String userName = WebTools.converStr(req.getParameter("userName"));
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	Users user = userService.getUser(userName);
    	if (user != null)
    	{
    		String pass = System.currentTimeMillis() + "";
    		MD5 md5 = new MD5();
			String p = md5.getMD5ofStr(pass);
			user.setPassW(p);
			userService.updataUserinfo(user);
			String mailContent = "您的密码已经重置为：\"" + pass + "\"，请登录后修改！";
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMail(user.getRealEmail(), mailContent, "系统密码重置通知");
    	}
    }
    
    /**
     * 
     * @param req
     * @param resp
     * @param jsonParams
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(required = false, methodName = ServletConst.GETMOBILEBACK_ACTION)
    public static String getMobileBack(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//获取用户返回的手机短信
    	String args=req.getParameter("args");//返回的内容
    	System.out.println("args===="+args);
    	try
    	{
	    	if (args!=null && args.length()>0)
	    	{
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				if (args.startsWith("-")) {
					//接收失败的情况，输出失败信息
					System.out.print(args+"  序列号或密码不对。-6为未加密，-2为加密不对");
				}else if ("1".equals(args)) {
					System.out.print("无可接收信息");
				}else {
					//多条信息的情况，以回车换行分割
					String[] result = args.split("\r\n");
					for(int i=0;i<result.length;i++)
					{
						//内容做了url编码，在此解码，编码方式gb2312
						String infos=URLDecoder.decode(result[i], "gb2312");
						//95153359,157589222222,15251664207,好的,2012-11-29 11:05:31------------------数据格式
						MobileBackInfo mobileBackInfo=new MobileBackInfo();
						mobileBackInfo.setTotalback(infos);//不管是否解析成功都必须将内容保存，确保信息不丢失
						mobileBackInfo.setAdddate(new Date());
						try
						{
							int index=infos.indexOf(",");//主要考虑短信中有,号的问题
							//第一个不保存
							infos=infos.substring(index+1);
							index=infos.indexOf(",");
							int ext=Integer.parseInt(infos.substring(5,index));
							mobileBackInfo.setExt(ext);//扩展码
							infos=infos.substring(index+1);
							
							index=infos.indexOf(",");
							mobileBackInfo.setBackcontent(infos.substring(0,index));//手机号
							infos=infos.substring(index+1);
							
							index=infos.lastIndexOf(",");
							mobileBackInfo.setBackcontent(infos.substring(0,index));
							
							mobileBackInfo.setBackdate(sdf.parse(infos.substring(index+1)));
						}
						catch (Exception ee)
						{
							ee.printStackTrace();
						}
						jqlService.save(mobileBackInfo);
						
						updateTransInfo(mobileBackInfo,jqlService);
					}
				}			
				
	    	}
	    	resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	        resp.getWriter().write(0);
	        return null;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
            resp.getWriter().write(2);
            return null;
    	}
	}
    
    public static void updateTransInfo(MobileBackInfo mobileBackInfo,JQLServices jqlService)
    {//更新业务相关信息
    	try
    	{
    		//根据ext和手机号更新发送表的信息
    		if (mobileBackInfo.getExt()!=null && mobileBackInfo.getMobile()!=null)
    		{
    			List<MobileSendInfo> list=(List<MobileSendInfo>)jqlService.findAllBySql("select a from MobileSendInfo a where a.ext=? and a.mobile=? ",mobileBackInfo.getExt(),mobileBackInfo.getMobile() );
    			if (list!=null && list.size()>0)//发送的时候要确保手机号唯一
    			{
    				MobileSendInfo mobileSendInfo=list.get(0);
    				mobileSendInfo.setIsvalidate(1);
    				mobileSendInfo.setBackcontent(mobileBackInfo.getBackcontent());
    				mobileSendInfo.setBackdate(mobileBackInfo.getBackdate());
    				jqlService.update(mobileSendInfo);//更新发送信息
    				Integer type=mobileSendInfo.getType();
    				if (type!=null && type.intValue()>0)//更新业务
    				{
    					if (type.intValue()==Constant.MEETING)//更新会议模块
    					{
    						if (mobileSendInfo.getOutid()!=null && mobileSendInfo.getOutid().longValue()>0l)
    						{//处理手机回复的信息
    							MeetSameInfo meetSameInfo = (MeetSameInfo)jqlService.getEntity(MeetSameInfo.class, mobileSendInfo.getOutid());
    							String backcontent=mobileBackInfo.getBackcontent();
    							Long actionid=1L;
    							if (backcontent==null)
    							{
    								backcontent="";
    							}
    							if (backcontent.toUpperCase().indexOf("Y")>=0)//参加，1
    							{
    								actionid=1L;
    								MeetUtil.instance().meetbackModify(mobileSendInfo.getOutid(), actionid
        									, "手机短信回复"+backcontent, null, null, null, null, meetSameInfo.getMeeter(),false);
    							}
    							else if (backcontent.toUpperCase().indexOf("N")>=0)//不参加,3
    							{
    								actionid=3L;
    								MeetUtil.instance().meetbackModify(mobileSendInfo.getOutid(), actionid
        									, "手机短信回复"+backcontent, null, null, null, null, meetSameInfo.getMeeter(),false);
    							}
    							else//替会2
    							{
    								actionid=2L;
    								MeetUtil.instance().meetbackModify(mobileSendInfo.getOutid(), actionid
        									, "手机短信回复"+backcontent, null, backcontent, null, null, meetSameInfo.getMeeter(),false);
    							}
     						}
    						
    					}
    					else if (type.intValue()==Constant.SHAREINFO) //更新共享模块
    					{
    						
    					}
    					else if (type.intValue()==Constant.COMPANYFILE) //更新企业文库
    					{
    						
    					}
    					else if (type.intValue()==Constant.GROUPTEAM) //更新群组协作模块
    					{
    						
    					}
    					else if (type.intValue()==Constant.MOBILESIGN) //更新移动签批模块
    					{
    						
    					}
    					else if (type.intValue()==Constant.TRANSSPLIT) //更新事务分发模块
    					{
    						
    					}
    					else if (type.intValue()==Constant.SHAREDIALOG) //更新共享日程模块
    					{
    						
    					}
    				}
    			}
    			
    		}
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 
     * @param req
     * @param resp
     * @param jsonParams
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(required = false, methodName = ServletConst.MOBILECODE_ACTION)
    public static String getMobileCode(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//发送手机验证码
    	String error=JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);
    	Long userid=0L;
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
	    String mobile = (String)params.get("mobile");//手机号
		String mobilenum = (String)params.get("mobilenum");
		Integer totalnums=(Integer)req.getSession().getAttribute("totalnums");//防止人为破坏，增加总数限制
		if (totalnums==null)
		{
			totalnums=0;
		}
		String mobileregist=(String)WebConfig.mobileregistmap.get(mobile);//用手机号做key验证码,次数,时间//每天夜里0时清空
		int getnums=0;//获取次数
		Long getcodedate=null;//获取时间
		String[] registvalues;
		if (mobileregist!=null)
		{
			registvalues=mobileregist.split(",");
			getnums=Integer.parseInt(registvalues[1]);//点击次数
			getcodedate=Long.valueOf(registvalues[2]);//获取时间
		}
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		Long hadnum=(Long)jqlService.getCount("select count(a) from Users as a where a.userName=?", mobile) ;
		
        if (hadnum!=null && hadnum.longValue()>0)
        {
        	error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        else if (getnums>=3 || totalnums>=12)
        {
        	//超过次数了
        	error = JSONTools.convertToJson(ErrorCons.PASS_ERROR, null);
        }
        else
        {
        	totalnums++;
        	getnums++;
        	long times=System.currentTimeMillis();
        	if (getcodedate==null)
        	{
        		getcodedate=times;
        	}

        	String code=WebTools.getCode();
        	mobileregist=code+","+getnums+","+times;
        	WebConfig.mobileregistmap.put(mobile, mobileregist);
        	req.getSession().setAttribute("totalnums",totalnums);//存放总数
        	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        	
        	System.out.println("======"+code);
        	//发送手机短信到手机上
			//多线程发送
	    	String content="欢迎使用"+WebConfig.smsname+"系统，您的验证码为："+code+"  当日有效";
	    	Thread receiveT = new Thread(new BackgroundSend(new String[]{mobile},content,1L,"系统软件",Constant.REGEST,new Long[]{0L},false,null));//这里的1先临时写死
			receiveT.start();
	    	//发送手机短信到手机上

        }
        return error;
	}
    @HandlerMethod(required = false, methodName = ServletConst.MOBILELOGINCODE_ACTION)
    public static String getMobileLoginCode(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//移动端登录界面获取验证码
    	String error=JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
	    String account = (String)params.get("account");//用户账号
		String mobilenum = (String)params.get("sendnums");//获取验证码的次数，用户关闭登录界面再打开清0
		int sendnums=0;
		if (mobilenum!=null)
		{
			sendnums=Integer.parseInt(mobilenum);
		}
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	    Users user=userService.getUser(account);
	    sendnums++;
        if (sendnums>3)
        {
        	//超过次数了
        	error = JSONTools.convertToJson(ErrorCons.PASS_ERROR, null);
        }
        else
        {
        	try
        	{
	        	if (user.getMobile()!=null && user.getMobile().length()==11 && Long.valueOf(user.getMobile())>100000l)
	        	{
		        	long times=System.currentTimeMillis();
		        	HashMap<String, Object> back=new HashMap<String, Object>();
		        	String code=WebTools.getCode();
		        	WebConfig.mobileregistmap.put(account, code+","+sendnums+","+times);
		        	back.put("code",code);//验证码
		        	back.put("sendnums", sendnums);//获取次数
		        	back.put("validate", times);//发送时间
		        	System.out.println("======"+code);
		        	//发送手机短信到手机上
					//多线程发送
			    	String content="欢迎使用"+WebConfig.smsname+"系统，您的验证码为："+code+"  有效时间2分钟";
			    	Thread receiveT = new Thread(new BackgroundSend(new String[]{user.getMobile()},content,2L,WebConfig.smsname+"系统",Constant.GETMOBILELOGIN,new Long[]{0L},false,null));//这里的2先临时写死
					receiveT.start();
			    	//发送手机短信到手机上
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
	        	}
	        	else
	        	{
	        		error = JSONTools.convertToJson(ErrorCons.ERRORMOBILE_ERROR, "手机号不正确");
	        	}
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        		error = JSONTools.convertToJson(ErrorCons.ERRORMOBILE_ERROR, "手机号不正确");
        	}
        }
        return error;
	}
    @HandlerMethod(required = false, methodName = ServletConst.MOBILEFORGETCODE_ACTION)
    public static String getMobileForgetCode(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//发送手机验证码
    	String error=JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);
    	Long userid=0L;
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
	    String mobile = (String)params.get("mobile");//手机号
		String mobilenum = (String)params.get("mobilenum");
		Integer totalnums=(Integer)req.getSession().getAttribute("totalforgetnums");//防止人为破坏，增加总数限制
		if (totalnums==null)
		{
			totalnums=0;
		}
		String mobileregist=(String)WebConfig.mobileregistmap.get("forget"+mobile);//用手机号做key验证码,次数,时间//每天夜里0时清空
		int getnums=0;//获取次数
		Long getcodedate=null;//获取时间
		String[] registvalues;
		if (mobileregist!=null)
		{
			registvalues=mobileregist.split(",");
			getnums=Integer.parseInt(registvalues[1]);//点击次数
			getcodedate=Long.valueOf(registvalues[2]);//获取时间
		}
//		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
//		Long hadnum=(Long)jqlService.getCount("select count(a) from Users as a where a.userName=?", mobile) ;
//		
//        if (hadnum!=null && hadnum.longValue()>0)
//        {
//        	error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
//        }
//        else 
        if (getnums>=3 || totalnums>=12)
        {
        	//超过次数了
        	error = JSONTools.convertToJson(ErrorCons.PASS_ERROR, null);
        }
        else
        {
        	totalnums++;
        	getnums++;
        	long times=System.currentTimeMillis();
        	if (getcodedate==null)
        	{
        		getcodedate=times;
        	}

        	String code=WebTools.getCode();
        	mobileregist=code+","+getnums+","+times;
        	WebConfig.mobileregistmap.put("forget"+mobile, mobileregist);
        	req.getSession().setAttribute("totalforgetnums",totalnums);//存放总数
        	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        	
        	System.out.println("======"+code);
        	//发送手机短信到手机上
			//多线程发送
	    	String content="欢迎使用"+WebConfig.smsname+"系统，您的验证码为："+code+"  当日有效";
	    	Thread receiveT = new Thread(new BackgroundSend(new String[]{mobile},content,1L,"系统软件",Constant.REGEST,new Long[]{0L},false,null));//这里的1先临时写死
			receiveT.start();
	    	//发送手机短信到手机上

        }
        return error;
	}
    @HandlerMethod(required = false, methodName = ServletConst.ACTIVE_ACTION)
    public static String active(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//用户激活
    	String error = JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);
    	Long userid=0L;
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
		String loginStr = (String)params.get("account");
	    String password = (String)params.get("password");
	    String spaceid = (String)params.get("spaceid");
	    
	        
	    UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	    Users user=userService.getUser(loginStr);
        if (user!=null)
        {
        	if (user.getSpaceUID().equals(spaceid) && user.getPassW().equals(password))
        	{
        		user.setValidate((short)1);
        		userService.updataUserinfo(user);
        		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        	}
        }
        resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        resp.getWriter().write("<script language='javascript'>"
        		+ " alert('激活成功,请登录！');"
        		+" window.location.href='/index.html';"
        		+"</script>");
        return null;
	}
    /**
     * 登录系统。
     * <p>
     * <pre>
     * request
     * {
	 * 		method:"login",
	 * 		params : {account:"xxxxx",password:"xxxxx"},
	 * 		token: "xxxxxxxx"
	 * }
     * </pre>
     * <pre>
     * Response:
	 * {
	 * 		errorCode:"0",
	 *		errorMessage:null,
	 * 		result:
	 * 		{
	 * 			token:"xxxxx”
	 *		}
	 * }
     * </pre>
     * @param req
     * @param resp
     * @param jsonParams
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(required = false, methodName = ServletConst.LOGIN_ACTION)
    public static String login(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
    	String error;
    	Long userid=0L;
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
		String loginStr = (String)params.get("account");
	    String password = (String)params.get("password");
	    String forbid = (String)params.get("forbid");
	    String code=(String)params.get("code");
	    System.out.println("login accout=="+loginStr);
	    if (code!=null && code.length()>1)
	    {
	    	String codevalues=(String)WebConfig.mobileregistmap.get(loginStr);//, code+","+sendnums+","+times
	    	String[] values=codevalues.split(",");
	    	if (code.toLowerCase().equals(values[0].toLowerCase()))
	    	{
	    		
	    	}
	    	else
	    	{
	    		//暂时不判断时间的有效性
	    		return error=JSONTools.convertToJson(ErrorCons.VALIDATE_ERROR, "验证码已失效");//验证码已失效
	    	}
	    }
	    if (forbid != null && forbid.length() > 0)    // 检查设备是否有效。
	    {
	    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	        if (userService.isDisabledDevice(forbid, loginStr))
            {
                return JSONTools.convertToJson(ErrorCons.USER_DEVICE_FORBID_ERROR, null);
                //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
                //resp.getWriter().write(error);
                //return;
            }	
	    }
	        
	    HttpSession session = req.getSession();
	    String token = session.getId() + System.currentTimeMillis();   // 暂时以sessionId值作为认证的token值。
	    session.setAttribute("token", token);
	    Object ency = params.get("ency");//req.getContextPath()
	    if (ency != null && (Boolean)ency)     // 通过cookie自动登录时候，用户名和密码是加密的。后续统一处理
	    {
		    loginStr = WebofficeUtility.furbishPassword(loginStr);
	        password = WebofficeUtility.furbishPassword(password);
	    }
	        
	    UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        Object data = userService.login(loginStr, password, true);
        Integer result;
        if (data instanceof Users)
        {
         	Users user = (Users)data;
           	result = handleLogin(domain, token, loginStr, password,null, userService, user, req, resp);
           	userid=user.getId();
        }
        else
        {
           	result = (Integer)data;
        }
            
	    if (result ==  ErrorCons.NO_ERROR)    // 登录成功
	    {
	    	String autoDirect = (String)params.get("autoDirect");
	       	if (autoDirect != null && autoDirect.length() > 0)
	       	{
	       		req.getRequestDispatcher(autoDirect).forward(req, resp);
	       		//resp.sendRedirect(autoDirect);
	      		return null;
	       	}	        	
	        	
	       	HashMap map = new HashMap();
 	       	map.put("token", token);       // 暂时以sessionId值作为认证的token值。
 	       	Users user=(Users)data;
 	        map.put("id", String.valueOf(user.getId()));
            map.put("realname", user.getRealName());
            map.put("userSpaceUID", user.getSpaceUID());
			Company company = ((Users)data).getCompany();
			if (company != null)
			{
				map.put("companyId", String.valueOf(company.getId()));
				map.put("spaceUID",company.getSpaceUID());
			}
 	        	
 	        	// user242, zipEnable
 	       	map.put("isSupportZip", true);
	       	Object cookie = params.get("cookie"); 
	       	if (cookie != null && (Boolean)cookie)   // 先兼容原有的做法
	       	{
	       		map.put("name", WebofficeUtility.passwordEncrypt(loginStr));
	       		map.put("pass", WebofficeUtility.passwordEncrypt(password));
	       		map.put("role", ((Users)data).getRole());	       		
	       	}
	       	
	       	//通知当前用户的联系人，用户已经上线
	       	ITalkService talkService=(ITalkService)ApplicationContext.getInstance().getBean(TalkService.NAME);
	       	try
	       	{
	       		talkService.sendOnlineNoticeMessage(req.getRemoteHost(),userid);
	       	}
	       	catch(Exception e)
	       	{
	       		e.printStackTrace();
	       	}
	       	
	       	error =  JSONTools.convertToJson(result, map);
	    }
	    else
	    {
	      	error = JSONTools.convertToJson(result, null);
	    }
	    
        //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        //resp.getWriter().write(error);
        
      //通过线程来删除文件
        if (userid!=null && userid>0)
        {
	        String path=WebConfig.tempFilePath;
	        String startstr=userid.longValue()+"_";
	        new ClearTempfile(path,startstr,null,null).start();
        }
        return error;
	}
        
    /**
     * 登录处理license，用户重复登录等
     * @param loginStr
     * @param password
     * @param userService
     * @param data
     * @param req
     * @param resp
     * @return
     */
    private static int handleLogin(String domain, String token, String loginStr, String password,String caid, UserService userService, Users data,
    		HttpServletRequest req, HttpServletResponse resp)
    {
    	HttpSession session = req.getSession();
    	handleSameSession(session, domain);    // 处理同一个浏览器中多个账户多次登录.    	
        String sessionID = session.getId();
        Users uinfo = data;
        uinfo.setToken(sessionID);             
        
        String userName = uinfo.getUserName();  
        userName = userName.toLowerCase();
        //boolean flag = handleRepUser(userService, userName, getRealIpAddr(req), session.getId());
        /*if (!flag)
        {
	        LoginUserInfo logUserInfo = memCache.getLoginUserInfo(userName);
	        if (logUserInfo != null)
	        {	
	        	memCache.setRepUser(userName, getRealIpAddr(req));
	        }
        }*/
        IMemCache  memCache = getMemCache();
        Integer licenseResult;
        Integer ltc = memCache.getLoginUserCount(); //
        ltc = ltc == null ? 1 : ltc + (memCache.getLoginUserInfo(domain + "+" + userName) != null ? 0 : 1);
        
        // 管理员暂时不检查。
        int role = uinfo.getRole();
        String tempName = domain + "+" + userName;
        if (role == Constant.ADIMI || role == Constant.USER_ADMIN || role == Constant.AUDIT_ADMIN || role == Constant.SECURITY_ADMIN
            || (licenseResult = userService.checkOnlinUser(ltc, uinfo.getCompany().getId())) == ErrorCons.USER_ONLINE_USER_PER_ERROR)
        {
        	handleRepUser(userService, tempName, getRealIpAddr(req), session.getId());
            session.setAttribute("userKey", uinfo);
            session.setAttribute("IPAdr", getRealIpAddr(req));
            session.setAttribute("isLoginContinue", new Boolean(true)); 
            Integer repResult = userService.loginRepository(data, password);
            if (repResult == ErrorCons.NO_ERROR)
            {
				//add by user685
            	//Loginfo loginfo = new Loginfo(uinfo.getUserName(), getRealIpAddr(req), LogConstant.OPERATE_TYPE_LOGIN);
            	session.setAttribute("operateType", LogConstant.OPERATE_TYPE_LOGIN);
            	session.setAttribute("domain", domain);

            	memCache.setLoginUserInfo(domain + "+" + userName, new LoginUserInfo(domain, userName,
            			token, getRealIpAddr(req)));
            	//memCache.setLoginUserCount(ltc);
            	
            	session.setAttribute(USER_LIST_KEY, userName);
            	sessiontable.put(tempName, session); 
            	
            	// user290 2011-10-06
            	session.setAttribute("key_userid", uinfo.getId().toString());
            	session.setAttribute("key_email", uinfo.getSpaceUID());
            	
                try
                {
                    String s1 = WebofficeUtility.passwordEncrypt(loginStr);
	                Cookie history = new Cookie("history", URLEncoder.encode(s1, "UTF-8"));
	                long expires = System.currentTimeMillis() + 360 * 24 * 60 * 60 * 1000;
	                history.setMaxAge((int)expires);
	                resp.addCookie(history);
	                LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME);
	                logServices.setLogin(uinfo, getRealIpAddr(req), token);
	                
	                //LogsUtility.logToFile("", WebofficeUtility.getFormateDate2(new Date(),"-") + ".log", true, loginfo);

                }
                catch(Exception e)
                {
                	e.printStackTrace();
                }
            }
            return repResult;
        }
        else
        {
            return licenseResult;
        }
    }
    
    /**
     * 判断用户登录信息是否是有效。
     * 主要判断用户的域名，登录用户名，登录认证token。
     * 目前暂时不判断时间有效性。
     * @param jsonParams
     * @return
     */
    public static boolean isValidate(HashMap<String, Object> jsonParams)
    {
    	HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String domain = (String)params.get("domain");    // 暂时不判断
		String acount = (String)params.get("account");
        String token = (String)jsonParams.get("token");
        if(token == null)
        	token = (String)params.get("token");
        if (acount == null)
        {
        	return false;
        }
        IMemCache  memCache = getMemCache();
        LoginUserInfo lui = memCache.getLoginUserInfo(domain + "+" + acount.toLowerCase());
        if (token != null && lui != null && token.equals(lui.getToken()))
        {
        	return true;
        }
        return false;
    }
    
    /**
     * 退出登录
     * @param req
     * @param resp
     * @param jsonParams
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(methodName = ServletConst.LOGOUT_ACTION)
    public static String logout(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		HashMap<String, Object> logout = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String domain = (String) logout.get("domain"); // 暂时不判断
		String loginStr = (String) logout.get("account");
		String token = (String) logout.get("token");
		boolean flag = PropsValue.isSSOLogin();
		quit(req.getSession(), !flag, domain);
		if (flag)
		{
			resp.sendRedirect(PropsValue.get(PropsConsts.SSO_LOGOUT_URL));
			return null;
		}
		Object direct = logout.get("direct");
		if (direct != null)
		{
			req.getRequestDispatcher((String) direct).forward(req, resp);
			// resp.sendRedirect((String)direct);
			return null;
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, "success");
		//resp.getWriter().write(error);
        
    }
    
    /**
     * 
     * @param req
     * @param resp
     * @param jsonParams
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(methodName = ServletConst.ONLINE_ACTION)
    public static String online(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   
    	HashMap<String, Object> online = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String domain = (String)online.get("domain");    // 暂时不判断
		String account = (String)online.get("account");
		IMemCache  memCache = getMemCache();
    	boolean ret = memCache.getLoginUserInfo(domain + "+" + account.toLowerCase()) != null;
    		
    		//HttpSession session = req.getSession();     // 先以老的方式判断。后续修改为通过jsonParam参数中accout和token判断。
    		//Users info = (Users)session.getAttribute("userKey");
    		//String ret = info == null ? "0" : "1";
        return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret ? 1 : 0);
        //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
    
    /**
     * 判断用户是否在线。
     * @param domain
     * @param userName
     * @return
     */
    public static boolean isUserOnline(String domain, String userName)
    {
    	IMemCache  memCache = getMemCache();
    	return memCache.getLoginUserInfo(domain + "+" + userName.toLowerCase()) != null;
    }
    
    /**
     * 
     * @param req
     * @param resp
     * @param jsonParams
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(methodName = ServletConst.GET_ALL_USER_ACTION)
    public static String getAllUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account");
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		Users us = userService.getUser(account);
    		Long companyId = us.getCompany().getId();
    		List<Users> user = userService.getCompanyUsers(companyId, -1, -1, "userName", "asc");
    		HashMap<String, Object> temp;
    		List result = new ArrayList();
    		for (Users u : user)
    		{
    			temp = new HashMap<String, Object>();
    			temp.put("id", u.getId());
    			temp.put("companyId", companyId);
    			temp.put("account", u.getUserName());
    			temp.put("name", u.getRealName());
    			if (u.getImage() != null)
    			{
    				temp.put("portrait", WebConfig.userPortrait + u.getImage());
    			}
    			result.add(temp);
    		}
        	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
//    /**
//     * 验证密码
//     */
//    public static void validatePassword(HttpServletRequest req, HttpServletResponse resp,HashMap<String, Object> jsonParams){
//    	String error;
//    	Properties properties = new Properties();
//    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
//    	String newPass = param.get("newPass").toString();
//    	try {
//			properties.load(UserOnlineHandler.class.getClassLoader().getResourceAsStream("/config/passworPolicy.properties"));
//			String regular = properties.getProperty("password.regular");
//			String alertMessage = properties.getProperty("password.alertMessage");
//			Pattern p = Pattern.compile(regular); 
//			Matcher m = p.matcher(newPass); 
//			HashMap map = new HashMap();
//			if (m.matches()) { 
//				
//				error =  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
//			}else{
//				map.put("alertMessage", alertMessage);
//				error =  JSONTools.convertToJson(ErrorCons.USER_PASSWORD_FORCE_ERROR, map);
//			} 
//			resp.getWriter().write(error);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			LogsUtility.error(e);
//		}
//    }
    
    /**
     * 修改密码接口
     */
    @HandlerMethod(methodName = ServletConst.VALIDATEPASSWORD)
    public static String validatePassword(HttpServletRequest req, HttpServletResponse resp,HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;
    	Properties properties = new Properties();
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String account = (String)param.get("account");
		String newPass = param.get("newPass").toString();
		MD5 md5 = new MD5();
		Users user = userService.getUser(account);
    	
			properties.load(UserOnlineHandler.class.getClassLoader().getResourceAsStream("/conf/passworPolicy.properties"));
			String regular = properties.getProperty("password.regular");
			String alertMessage = properties.getProperty("password.alertMessage");
			Pattern p = Pattern.compile(regular); 
			Matcher m = p.matcher(newPass); 
			HashMap map = new HashMap();
			if (m.matches()) { 
				user.setPassW( md5.getMD5ofStr(newPass));
				userService.updataUserinfo(user);
				error =  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}else{
				map.put("alertMessage", alertMessage);
				error =  JSONTools.convertToJson(ErrorCons.USER_PASSWORD_FORCE_ERROR, map);
			} 
			return error;
			//resp.getWriter().write(error);
		
    }
    
    
    /**
     * 
     * @param req
     * @param resp
     * @param jsonParams
     * @throws ServletException
     * @throws IOException
     */
    @HandlerMethod(methodName = ServletConst.GET_USERINFO_ACTION)
    public static String getUserinfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account");
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		System.out.println("account==="+account);
    		Users user = userService.getUser(account);
    		
    		HashMap<String, String> temp = new HashMap<String, String>();
    		if (user != null)
    		{	
    			temp.put("account", user.getUserName());
    			temp.put("id", String.valueOf(user.getId()));
    			Company company = user.getCompany();
    			if (company != null)
    			{
    				temp.put("companyId", String.valueOf(company.getId()));
    				temp.put("companyName", company.getName());
    				temp.put("companycode", company.getCode());
    			}
    	    	List<Organizations> organizations = userService.getOrganizationByUsers(user.getId());
    	    	String parentOrgId =null;
    	    	for(Organizations org : organizations){
    	    		Organizations parentOrg = userService.getGroup(org.getId());
    	    		if(parentOrg!=null){
    	    			parentOrgId = String.valueOf(parentOrg.getId());
    	    		}
    	    		else{
    	    			parentOrgId = String.valueOf(org.getId());
    	    		}
    	    	}
    			temp.put("role", String.valueOf(user.getRole()));
    			temp.put("name", user.getRealName());
    			temp.put("displayName", user.getRealName());
    			if (user.getImage() != null)
    			{
    				temp.put("portrait", WebConfig.userPortrait + user.getImage());
    			}
    			temp.put("mail", user.getRealEmail());
    			temp.put("role", String.valueOf(user.getRole()));
    			temp.put("orgid", parentOrgId);//用户所属部门的最顶层单位
    			temp.put("address", user.getAddress());
    			temp.put("mobile", user.getMobile());
    			temp.put("phone", user.getPhone());
    			temp.put("duty", user.getDuty());  
    			temp.put("sortNum", String.valueOf(user.getSortNum()));
    			temp.put("spaceid",user.getSpaceUID());
    			temp.put("cloudpublic", String.valueOf(WebConfig.cloudPro));//私有云还是公有云
    		}
        	return JSONTools.convertToJson(ErrorCons.NO_ERROR, temp);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }    
    
    /**
	 * 为移动增加的获取具有审批权限的用户列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
    @HandlerMethod(methodName = ServletConst.GET_AUDIT_USER_ACTION)
	public static String getAllAuditUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   	
    	
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account");    		    		
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		Users user = userService.getUser(account);
    		Long companyId = user.getCompany().getId();
    		List<AdminUserinfoView> users = userService.getAllAuditUser(companyId);
    		ArrayList result = new ArrayList();
    		HashMap<String, Object> resultMap;
    		if (users != null && users.size() > 0)
    		{
    			for (AdminUserinfoView temp : users)
    			{
    				resultMap = new HashMap<String, Object>();
    				resultMap.put("id", temp.getId());
    				resultMap.put("name", temp.getUserName());
    				resultMap.put("realName", temp.getRealName());
    				Organizations org = temp.getGroupinfo();
    				if (org != null)
    				{    					
	    				resultMap.put("orgName", org.getName());
	    				resultMap.put("orgId", org.getId());
    				}
    				else 
    				{
    					resultMap.put("orgName", "");
	    				resultMap.put("orgId", 0);
    				}
    				
    				result.add(0, resultMap);
    			}
    		}
   			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
    
    /*public static void online(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   	
    	try
    	{
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String path = (String)param.get("path");
    		String account = (String)param.get("account");
        	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        }
	    catch(ClassCastException e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }*/
	/**
	 * 获取用户的个性化信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.GET_USER_CONFIG_ACTION)
	public static String getUsersConfig(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   	
    	Users user = (Users)req.getSession().getAttribute("userKey");
    	if(user == null)
    	{
    		return null;
    	}
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	UsersConfig uc = userService.getUsersConfig(user.getId());
//    		uc.getModules();
//    		uc.getApps();
    	JsonConfig cfg = new JsonConfig();
    	cfg.setJsonPropertyFilter(new PropertyFilter() {
    	public boolean apply(Object source, String name, Object value) {
    				/*if (source instanceof UsersYzModule) {
    					if (name.equals("usersConfig") || name.equals("id")) {
    						return true;
    					} else {
    						return false;
    					}
    				}
    				if (source instanceof YzModule) {
    					if (name.equals("mid")) {
    						return false;
    					}
    					if (name.equals("mpath")) {
    						return false;
    					}
    					if (name.equals("mname")) {
    						return false;
    					}
    					return true;
    				}
    				if (source instanceof YzAPP) {
    					if (name.equals("tag")) {
    						return true;
    					}
    					return false;
    				}*/
    		if (source instanceof UsersConfig) {
    			if (name.equals("user") || name.equals("id")
    					|| name.equals("yzModuleSet")
    						|| name.equals("yzAPPSet") /*|| name.equals("opentype")*/) {
    					return true;
    			} else {
    				return false;
    			}
    		}
    		return false;
    		}
    		});
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, uc,cfg);    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 设置用户的个性化信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SET_USER_CONFIG_ACTION)
	public static String setUsersConfig(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;
		Users user = (Users) req.getSession().getAttribute("userKey");
		if (user == null)
		{
			return null;
		}
		// HashMap<String, Object> param = (HashMap<String,
		// Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		//
		//
		UsersConfig uc = userService.getUsersConfig(user.getId());

		// yzstyle:
		// layout:
		// apps:
		// modules:
		String skin = (String) jsonParams.get("skin");
		if (skin != null)
		{
			uc.setSkin(skin);
		}
		String layout = (String) jsonParams.get("layout");
		if (layout != null)
		{
			uc.setLayout(Integer.parseInt(layout));
		}
		String apps = (String) jsonParams.get("apps");
		if (apps != null && apps.length() != 0)
		{
			uc.setApps(apps);
		}
		String modules = (String) jsonParams.get("modules");
		if (modules != null && modules.length() != 0)
		{
			uc.setModules(modules);
		}
		if (uc.getUser() == null)
		{
			// 添加一个
			uc.setUser(user);
		}
		userService.updateUsersConfig(uc);
		/*
		 * ArrayList<Object> lists = (ArrayList<Object>)param.get("apps");
		 * if(lists != null) { String[] apps = new String[lists.size()]; for
		 * (int i = 0; i < lists.size(); i++) { apps[i] =
		 * lists.get(i).toString(); } String method =
		 * (String)param.get("method"); if("addapp".equals(method)) {
		 * userService.addapps(apps, uc); } else if("removeapp".equals(method))
		 * { userService.removeapps(apps, uc); } else {
		 * userService.setapps(apps, uc); } } ArrayList<ArrayList<Object>>
		 * modules = (ArrayList<ArrayList<Object>>)param.get("modules");
		 * if(modules != null) { String[][] mids = new String[modules.size()][];
		 * for (int i = 0; i < modules.size(); i++) { ArrayList<Object> tmp =
		 * (ArrayList<Object>)modules.get(i); if(tmp == null) { mids[i] = null;
		 * continue; } String[] tmpstring = new String[tmp.size()]; mids[i] =
		 * tmpstring; for (int j = 0; j < tmp.size(); j++) { tmpstring[j] =
		 * tmp.get(j).toString(); } } userService.setmodules(mids,
		 * user.getId()); }
		 */
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
    }	
	
	/**
	 * 获取联系人
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_CONTACTS)
	public static String getContacts(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams) throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); //
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		Users us = userService.getUser(account);
		Integer id = (Integer) param.get("id");
		Integer type = (Integer) param.get("type");

		IAddressListService addressService = (IAddressListService) ApplicationContext.getInstance().getBean(
				AddressListService.NAME);
		List<AddressListPo> addressList = new ArrayList<AddressListPo>();
		List<Map> result = new ArrayList<Map>();
		Map<String, Object> temp;
		List<Users> users;
		if (type == 1)
		{
			List<Long> usersId = addressService.getCtmGroupService().getCtmGMIdByGId(new Long(id));
			// users = addressService.getMemberByGroupId(new Long(id));
			users = new ArrayList<Users>();
			for (Long userId : usersId)
			{
				users.add(userService.getUser(userId));
			}

			addressList = addressService.getOutContacts(new Long(id), new Long(us.getId()));
		} // else if(type == 2)
		// {
		// users = addressService.getTeamMembersByGroupId(new Long(id));
		// }
		else
		{
			users = addressService.getCtmGroupService().getAllCtmGMUsersList(us.getId());

			addressList = addressService.getOutContacts(new Long(id), new Long(us.getId()));
		}
		for (Users u : users)
		{
			temp = new HashMap<String, Object>();
			temp.put("account", u.getUserName());
			temp.put("name", u.getRealName());
			if (u.getImage1() != null)
			{
				temp.put("portrait", WebConfig.userPortrait + u.getImage());
			}
			temp.put("mail", u.getRealEmail());
			temp.put("address", u.getAddress());
			temp.put("mobile", u.getMobile());
			temp.put("phone", u.getPhone());
			temp.put("duty", u.getDuty());
			result.add(temp);
		}
		if (addressList != null)
		{
			for (AddressListPo alp : addressList)
			{
				temp = new HashMap<String, Object>();
				temp.put("account", alp.getUserName());
				temp.put("name", alp.getRealName());
				if (alp.getImage() != null)
				{
					temp.put("portrait", WebConfig.userPortrait + alp.getImage());
				}
				temp.put("mail", alp.getRealEmail());
				temp.put("address", alp.getAddress());
				temp.put("mobile", alp.getMobile());
				temp.put("phone", alp.getPhone());
				temp.put("duty", alp.getDuty());
				result.add(temp);
			}
		}

		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);
	}

	@HandlerMethod(methodName = ServletConst.GET_USER_CONTACT_GROUPS)
	public static String getUserContactGroups(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException {
		//String error;
		
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
			.getInstance().getBean(UserService.NAME);
			IAddressListService iAddressService = (IAddressListService) ApplicationContext
			.getInstance().getBean(AddressListService.NAME);
			Users user = userService.getUser(userName);
			List<CustomGroupPo> groups= iAddressService.getCtmGroupService().getCtmGList(user.getId());
//			List<Groups> teams= iAddressService.getTeamGByUserId(user.getId());
			HashMap<String, Object> temp;
			List result = new ArrayList();
			
			for (CustomGroupPo cg : groups) {
				temp = new HashMap<String, Object>();
				temp.put("id", cg.getId());
				temp.put("name", cg.getName());
				temp.put("isCustomGroup", true);
				result.add(temp);
			}
			
//			for(Groups g : teams){
//				temp = new HashMap<String, Object>();
//				temp.put("id", g.getId());
//				temp.put("name", g.getName());
//				temp.put("isCustomGroup", false);
//				result.add(temp);
//			}
			
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/**
	 * 为移动增加的获取具有审批权限的用户列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_AUDIT_USERS_ACTION)
	public static String getAuditUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;

		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");
		String type = (String) param.get("type"); // 0表示本组织（不包含上下级组织），1表示本单位（根），2表示系统中所有单位（所有的根）
		
		String isAll = (String) param.get("isAll"); // 0 表示签批权限者，1为全部用户
		if (isAll == null) 
		{
			isAll = "0";
		}
		
		Object oid = param.get("orgId"); //
		
		Long orgId = null;
		if (oid instanceof Integer)
		{
			orgId = ((Integer) oid).longValue();
		}
		if (oid instanceof Long)
		{
			orgId = (Long) oid;
		}
		
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(account);
		Long companyId = user.getCompany().getId();

		List<Organizations> orgs = null;
		List<Users> users = null;
		if (orgId != null)
		{
			orgs = userService.getChildOrganizations(companyId, orgId);
			if ("0".equals(isAll))
			{
				users = userService.getAuditUserByOrgId(orgId);
			}
			else
			{
				users = userService.getOrgUsers(companyId, orgId, false, 0, -1, null, "asc", null);
			}
		} else
		{
			if ("0".equals(type))
			{
				if ("0".equals(isAll))
				{
					users = userService.getAuditUserByUser(account);
				}
				else
				{
					List<Organizations> tempO = userService
							.findOrganizationsByUserName(account);
					if (tempO != null && tempO.size() > 0)
					{
						for (Organizations to : tempO)
						{
							List<Users> tempusers = userService.getOrgUsers(companyId, 
									to.getId(), false, 0, -1, null, "asc", null);
							if (users == null)
							{
								users = tempusers;
							}
							else
							{
								users.addAll(tempusers);
							}
						}

					}
				}
			} else if ("1".equals(type))
			{
				List<Organizations> tempO = userService
						.findOrganizationsByUserName(account);
				if (tempO != null && tempO.size() > 0)
				{
					Organizations to = tempO.get(0);
					if (to.getParentKey() != null)
					{
						String[] ts = to.getParentKey().split("-");
						Long tI = Long.valueOf(ts[0]);						
						orgs = userService.getChildOrganizations(companyId, tI);
						
						if ("0".equals(isAll)) 
						{
							users = userService.getAuditUserByOrgId(tI);
						} else 
						{
							users = userService.getOrgUsers(companyId, tI, false, 0, -1,
									null, "asc", null);
						}
					} else
					{
						orgs = userService.getChildOrganizations(companyId,
								null);
					}
				}
			} else
			{
				orgs = userService.getChildOrganizations(companyId, orgId);
			}
		}

		ArrayList result = new ArrayList();
		HashMap<String, Object> resultMap;
		if (orgs != null && orgs.size() > 0)
		{
			for (Organizations o : orgs)
			{
				resultMap = new HashMap<String, Object>();
				resultMap.put("type", "org");
				resultMap.put("id", o.getId());
				resultMap.put("name", o.getName());
				result.add(resultMap);
			}
		}
		if (users != null && users.size() > 0)
		{
			List<Long> idList = new ArrayList<Long>();
			for (Users temp : users)
			{
				if (!idList.contains(temp.getId())) {
					resultMap = new HashMap<String, Object>();
					resultMap.put("type", "user");
					resultMap.put("id", temp.getId());
					resultMap.put("name", temp.getUserName());
					resultMap.put("realName", temp.getRealName());
					result.add(resultMap);
				}
				
			}
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
    }
	/**
	 * 移动端增加外部联系人接口
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_OUTERADDRESS)
	public static String addOuterAddress(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");
		Users user = getCurrentUser(account);
		
		String gid = (String) param.get("groupId");//所属组别
		Long groupId=null;
		if (gid!=null)
		{
			groupId=Long.valueOf(gid);
		}
		String name = (String) param.get("name");//用户名称，系统内的用户为登录名
		String email = (String) param.get("email");//用户邮箱
		String phone = (String) param.get("phone");//用户电话（固定电话）
		String userMoible=(String) param.get("mobile");//手机号码
		String comment = (String) param.get("comment");//备注
		Boolean man=(Boolean) param.get("isman");//男女
		boolean isman=true;
		if (man!=null)
		{
			isman=man.booleanValue();
		}
		String duty=(String) param.get("duty");//职务
		String userBirthday=(String) param.get("userBirthday");//生日
		String userWorkAddress=(String) param.get("userWorkAddress");//地址
		String userHomeAddress=(String) param.get("userHomeAddress");//公司地址

		AddressBean addressBean = new AddressBean(null,
				user.getId(), // ownerId,
				user, // userinfo,
				groupId, // groupId,
				name, // userName,
				email, // realEmail,
				name, // realName,
				duty, // duty,
				"", // image,
				userMoible, // mobile,
				"", // companyName,
				"", // fax,
				phone, // phone,
				"", // postcode,
				userWorkAddress, // address,
				userHomeAddress, // companyAddress,
				isman, // man,
				userBirthday == null? null:new Date(Long.parseLong(userBirthday)), // birthday,
				"", // department,
				comment // comment
		);
		IAddressListService addressListService = (IAddressListService) ApplicationContext.getInstance().getBean(
				AddressListService.NAME);
		addressListService.getCtmGroupService().addCtmGMOuter(addressBean);

		String error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
		return error;
    }
	/**
	 * 为移动增加的获取用户的签名
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SIGN_VERSION)
	public static String getUserSingVersion(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   	
    	
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account"); 
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		UsersConfig us = userService.getSign(account);
    		if (us != null)
    		{
    			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, us.getSignVersion() != null ? us.getSignVersion() : 0);
    		}
    		else
    		{
    			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
    		}
        return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	
	/**
	 * 为移动增加的获取用户的签名
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SIGIN)
	public static String getUserSing(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   	
    	try
    	{
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account"); 
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		UsersConfig us = userService.getSign(account);
    		if (us != null)
    		{
    			byte[] cont = us.getSign();
    			if (cont != null)
    			{
    				resp.setHeader("Error-Code", "0");
	    			resp.setHeader("Content-Length", String.valueOf(cont.length));
	    			resp.getOutputStream().write(cont);
	    			return null;
    			}
    		}
    		resp.setHeader("Error-Code", "0");
    		resp.setHeader("Content-Length", String.valueOf(0));
    		return null;
        }
	    catch(ClassCastException e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    catch(Exception ee)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
	    }
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.setHeader("Error-Code", "-1");
	    //resp.getWriter().write(error);
	    return error;
    }
	
	/**
	 * 为移动增加的更新签名内容
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_SIGIN)
	public static String updateSign(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   	
    	
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account"); 
    		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    		
    		InputStream in = req.getInputStream();
    		byte[] cont = new byte[1024 * 1024];
    		byte[] tempC = new byte[1024];
    		int size = 0;
    		int tempS;
    		while ((tempS = in.read(tempC)) >= 0)
    		{
    			if (size + tempS > cont.length)
    			{
    				byte[] tt = cont;
    				cont = new byte[cont.length + 1024];
    				System.arraycopy(tt, 0, cont, 0, size);
    			}
    			System.arraycopy(tempC, 0, cont, size, tempS);
    			size += tempS;
    		}
    		if (size > 0)
    		{
    			byte[] tt = cont;
				cont = new byte[size];
				System.arraycopy(tt, 0, cont, 0, size);
				
    			int a = userService.updateSign(account, cont);
    			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, a);
    		}
    		else
    		{
    			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, -1);
    		}
        return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获得公司管理的角色 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_MANAGE_ROLES)
	public static String getManageRoles(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   	    	
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");    	
    	long companyId = Long.valueOf(temp);
    	temp = (String)param.get("type");    	 // 0表示获取系统角色，1表示获取空间角色，无此参数或其他值为表示既获取系统角色，也获取空间角色
    	int type = temp != null ? Integer.valueOf(temp) : -1;
    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	List<Roles> roles = userService.getCompanyRoles(companyId, type);
    	HashMap<String, Object> retsult = new HashMap<String, Object>();
    	ArrayList systems = new ArrayList();
    	ArrayList spaces = new ArrayList();
    	if (roles != null)
    	{
    		for (Roles tr : roles)
    		{
    			HashMap<String, String> retR = new HashMap<String, String>();
				retR.put("id", String.valueOf(tr.getId()));
				retR.put("name", tr.getRoleName());
				retR.put("description", tr.getDescription());
    			if (tr.getType() ==  RoleCons.SPACE)
    			{
    				spaces.add(retR);
    			}
    			else
    			{
    				systems.add(retR);
    			}
    		}
//    		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
//    		Company company=(Company)jqlService.getEntity(Company.class, companyId);
//    		retsult.put("code", company.getCode());
    		retsult.put("systems", systems);
    		retsult.put("spaces", spaces);
    	}
    		
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 新建公司管理的角色 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_MANAGE_ROLES)
	public static String addManageRoles(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	long companyId = Long.valueOf(temp);
    	String name = (String)param.get("name");
    	String desc = (String)param.get("description");
    	temp = (String)param.get("type");
    	int type = Integer.valueOf(temp);      // 0系统角色，1空间角色
    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);    		
    	Roles role = new Roles(name, desc, type);
    	String ret = userService.addRole(role, companyId, -1, -1, -1);
    	if (ret != null)
    	{
    		error = JSONTools.convertToJson(ErrorCons.MANAGE_EXIST_ERROR, ret);
    	}
    	else
    	{
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
    	}
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 修改公司管理的角色 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_MANAGE_ROLES)
	public static String updateManageRoles(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	long companyId = Long.valueOf(temp);
    	String name = (String)param.get("name");
    	String desc = (String)param.get("description");
    	temp = (String)param.get("type");
    	int type = Integer.valueOf(temp);      // 0系统角色，1空间角色
    	temp = (String)param.get("roleId");
    	long roleId = Long.valueOf(temp);
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);    		
    	Roles role = new Roles(name, desc, type);
    	String ret = userService.updateRole(role, roleId, companyId, -1, -1, -1);
    	if (ret != null)
    	{
    		error = JSONTools.convertToJson(ErrorCons.MANAGE_EXIST_ERROR, ret);
    	}
    	else
    	{
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
    	}
        return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 删除公司管理的角色 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_MANAGE_ROLES)
	public static String deleteManageRoles(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	long companyId = Long.valueOf(temp);
    	ArrayList<String> ids = (ArrayList<String>)param.get("roleIds");
    	ArrayList<Long> roleIds = new ArrayList<Long>();
    	for (String tr : ids)
    	{
    		roleIds.add(Long.valueOf(tr));
    	}
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);    
    	userService.deleteRole(companyId, roleIds);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 得权限列表 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ROLE_PERMISSIONS)
	public static String getRolePermissions(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account"); 
    		String temp = (String)param.get("companyId");    		
    		long companyId = Long.valueOf(temp);    	    // 后续需要使用	
    		temp = (String)param.get("roleId");
    		long roleId = Long.valueOf(temp);
    		temp = (String)param.get("type");
    		int type = Integer.valueOf(temp);               // 0系统角色，1空间角色
    		
    		HashMap<String, Object> retsult = new HashMap<String, Object>();
    		PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME); 
    		List<Actions> actions = permissionService.getRoleAction(roleId);
    		long systemA = 0;
    		long fileA = 0;
    		long spaceA = 0;
    		for (Actions tr : actions)
    		{
    			if (tr instanceof FileSystemActions)
    			{
    				fileA |= ((FileSystemActions)tr).getAction();
    			}
    			else if (tr instanceof SystemManageActions)
    			{
    				systemA |= ((SystemManageActions)tr).getAction();
    			}
    			else if (tr instanceof SpacesActions)
    			{
    				spaceA |= ((SpacesActions)tr).getAction();
    			}
    		}
    		if (type == RoleCons.SYSTEM)
    		{
    			String[] systemN = ManagementCons.MANAGEMENT_ACTION_NAME;
    			ArrayList<Object> systemP = new ArrayList<Object>();
    			int size = systemN.length;
    			for (int i = 0; i < size; i++)
    			{
    				HashMap<String, String> per = new HashMap<String, String>();
    				per.put("index", String.valueOf(i));
    				per.put("name", systemN[i]);
    				per.put("select", String.valueOf(FlagUtility.isLongFlag(systemA, i)));
    				systemP.add(per);
    			}
    			retsult.put("systems", systemP);
    		}
    		else
    		{
	    		String[] fileN = FileSystemCons.FILE_SYSTEM_ACTION_NAME;
	    		String[] spaceN = SpaceConstants.SAPCE_ACTION_NAME;
	    		ArrayList<Object> fileP = new ArrayList<Object>();
	    		int size = fileN.length;
    			for (int i = 0; i < size; i++)
    			{
    				HashMap<String, String> per = new HashMap<String, String>();
    				per.put("index", String.valueOf(i));
    				per.put("name", fileN[i]);
    				per.put("select", String.valueOf(FlagUtility.isLongFlag(fileA, i)));
    				fileP.add(per);
    			}
	    		ArrayList<Object> spaceP = new ArrayList<Object>();
	    		size = spaceN.length;
    			for (int i = 0; i < size; i++)
    			{
    				HashMap<String, String> per = new HashMap<String, String>();
    				per.put("index", String.valueOf(i));
    				per.put("name", spaceN[i]);
    				per.put("select", String.valueOf(FlagUtility.isLongFlag(spaceA, i)));
    				spaceP.add(per);
    			}
	    		retsult.put("files", fileP);
	    		retsult.put("spaces", spaceP);
    		}
    		
    		return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 修改权限列表 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_ROLE_PERMISSIONS)
	public static String updateRolePermissions(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   	
    	
    		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    		String account = (String)param.get("account"); 
    		String temp = (String)param.get("companyId");    		
    		long companyId = Long.valueOf(temp);    	    // 后续需要使用	
    		temp = (String)param.get("roleId");
    		long roleId = Long.valueOf(temp);
    		
    		PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    		HashMap<String, Object> permissions = (HashMap<String, Object>)param.get("permissions");
    		temp = (String)permissions.get("system");
    		if (temp != null)
    		{
    			long permission = Long.valueOf(temp);
    			permissionService.updateDefinedRoleAction(roleId, permission, 0);
    		}
    		temp = (String)permissions.get("file");
    		if (temp != null)
    		{
    			long permission = Long.valueOf(temp);
    			permissionService.updateDefinedRoleAction(roleId, permission, 2);
    		}
    		temp = (String)permissions.get("space");
    		if (temp != null)
    		{
    			long permission = Long.valueOf(temp);
    			permissionService.updateDefinedRoleAction(roleId, permission, 1);
    		}  		
    		
    		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取公司是否已经存在
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IS_EXIST_COMPANY)
	public static String isExistCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String name = (String)param.get("companyName"); 
    		 
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	boolean ret = userService.isExistCompany(name);    		    		
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取用户是否已经存在
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IS_EXIST_USER)
	public static String isExistUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error; 
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String name = (String)param.get("userName"); 
    		 
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	boolean ret = userService.isExistUser(name);    		    		
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	
	/**
	 * 创建公司及管理员
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_COMPANY)
	public static String addCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String companyName = (String)param.get("companyName");
    	String contactName = (String)param.get("contactName");
    	String contactPhone = (String)param.get("contactPhone");
    	String contactMail = (String)param.get("contactMail");
    	String desc = (String)param.get("description");
    	String code = (String)param.get("code");
    	if (code==null || code.length()==0)
    	{
    		code=""+System.currentTimeMillis();//如果已经存在
    	}
    	String adminName = (String)param.get("adminName");
    	String adminPass = (String)param.get("adminPass");
    	String temp = (String)param.get("count");
    	int count = Integer.valueOf(temp) + 1;    // 管理员不计算在使用帐号中
    	temp = (String)param.get("date");
    	long date = Long.valueOf(temp);
    	long cuDate = System.currentTimeMillis();
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);     		
    	Company company = new Company(companyName, desc, count, date);
    	company.setCode(code);
    	company.setBeginTime(cuDate, cuDate);
    	company.setUserInfo(contactName, contactMail, "", "", contactPhone);
    	Users admin = new Users(adminName, adminPass, contactMail, (short)Constant.COMPANY_ADMIN, companyName + "管理员");
    	
    	List<HashMap<String, String>> apps = (List<HashMap<String, String>>)param.get("apps");
    	
    	boolean appsFlag = apps != null && apps.size() > 0;
    	
    	int ret = userService.addCompany(company, admin, !appsFlag);  
    	
    	if (ret == ErrorCons.NO_ERROR && appsFlag)
    	{
    		AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME);    	
    		appsService.addOrUpdateCompanyApps(apps, company);
    	}
    	
    	return  JSONTools.convertToJson(ret, null);
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 公司注册
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.REGISTER_COMPANY_ACCOUNT)
	public static String registerCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String companyName = (String)param.get("companyName");
    	String code = (String)param.get("code");
    	String contactPhone = (String)param.get("phone");
    	String contactMail = (String)param.get("mail");    		
    	String adminName = (String)param.get("adminName");
    	String url = (String)param.get("url");
    	if (adminName == null)
    	{
    		adminName = code + "_admin";
    	}
    	int p = (int)(Math.random() * 1000000);    // 管理员初始密码，简单处理
    	String adminPass = String.valueOf(p);    	
    	p = (int)(Math.random() * 1000000);        // 验证码，简单处理
    	String verifyC = String.valueOf(p);
    	long cuDate = System.currentTimeMillis(); 
    	
    	int count = 51;    // 管理员不计算在使用帐号中默认50人
    	long date = cuDate + 30L * 24L * 60L * 60L * 1000L;    // 默认30天验证内有效 
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);     		
    	Company company = new Company(companyName, "", count, date);
    	company.setCode(code);
    	company.setFd(cuDate);
    	company.setVerifyCode(verifyC);
    	company.setUserInfo(adminName, contactMail, "", "", contactPhone);
    	Users admin = new Users(adminName, adminPass, contactMail, (short)Constant.COMPANY_ADMIN, companyName + "管理员");
    	admin.setResetPass(adminPass);
    	admin.setValidate((short)0);
    		
    	int ret = userService.registerCompany(company, admin, url);
    	
    	if ((ret==ErrorCons.NO_ERROR) && (contactPhone != null) && (contactPhone.length() == 11))
    	{
    		try
    		{
		    	String content = "欢迎注册使用"+WebConfig.smsname+"系统，您的企业验证码为：" + verifyC + "  30天内有效";
		    	Thread receiveT = new Thread(new BackgroundSend(new String[]{contactPhone}, content, 1L, "系统软件", Constant.REGEST, new Long[]{0L}, false, null));//这里的1先临时写死
				receiveT.start();
    		}
    		catch(Exception e)
    		{
    			LogsUtility.error(e);
    		}
    	}
    	
    	
    	return  JSONTools.convertToJson(ret, null);
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 注册公司激活
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.ACTIVE_COMPANY_ACCOUNT)
	public static String activeCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
	    HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	    String domain = (String)params.get("domain");
		String acount = (String)params.get("account");
	    String code = (String)params.get("company");
	    String verify = (String)params.get("verify");
	    
	        
	    UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	    Users user = userService.getUser(acount);
        if (user != null)
        {
        	Company company = userService.getCompanyByCode(code);
        	if (company != null)
        	{
        		long time = System.currentTimeMillis();
        		if (user.getCompany().getId().longValue() == company.getId().longValue()
        				&& company.getUd() >= time && verify.equals(company.getVerifyCode()))
        		{
        			long cuDate = System.currentTimeMillis();
        			time = cuDate + 60L * 24L * 60L * 60L * 1000L;    // 默认60天
        			company.setVerifyCode(null);
        			company.setUd(time);
        			company.setBd(cuDate);
        			userService.update(company);
        			
        			user.setValidate((short)1);
        			userService.update(user);
        			/*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        	        resp.getWriter().write("<script language='javascript'>"
        	        		+ " alert('激活成功,请登录！');"
        	        		+" window.location.href='/static/login.html';"
        	        		+"</script>");
        			return  null;*///
        			return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        		}
        	}
        }
        /*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        resp.getWriter().write("<script language='javascript'>"
        		+ " alert('激活成功,请登录！');"
        		+" window.location.href='/static/login.html';"
        		+"</script>");
        return null;*///
        return JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);
	}
	
	
	/**
	 * 获取公司列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_COMPANY_LIST)
	public static String getCompanyList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");

    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	List<Company> company = userService.getCompanyList();
    	ArrayList<Object> retsult = new ArrayList<Object>();
    	for (Company temp : company)
    	{
    		HashMap<String, String> data = new HashMap<String, String>();
    		data.put("id", String.valueOf(temp.getId()));
            data.put("name", temp.getName());
    		data.put("count", String.valueOf(temp.getMaxUsers()));
    		data.put("date", String.valueOf(temp.getUd()));
    		if (temp.getBd() != null)
    		{
    			data.put("beginDate", String.valueOf(temp.getBd()));
    		}
    		if (temp.getFd() != null)
    		{
    			data.put("regDate", String.valueOf(temp.getFd()));
    		}
    		data.put("code", temp.getCode());
    		retsult.add(data);
    	}
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 搜索公司
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SEARCH_COMPANY)
	public static String searchCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String key = (String)param.get("key");
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	List<Company> company = userService.searchCompanyByName(key);
    	ArrayList<Object> retsult = new ArrayList<Object>();
    	for (Company temp : company)
    	{
    		HashMap<String, String> data = new HashMap<String, String>();
    		data.put("id", String.valueOf(temp.getId()));
    		data.put("name", temp.getName());
    		data.put("count", String.valueOf(temp.getMaxUsers()));
    		data.put("date", String.valueOf(temp.getUd()));
    		if (temp.getBd() != null)
    		{
    			data.put("beginDate", String.valueOf(temp.getBd()));
    		}
    		if (temp.getFd() != null)
    		{
    			data.put("regDate", String.valueOf(temp.getFd()));
    		}
    		data.put("code", temp.getCode());
    		retsult.add(data);
    	}
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 删除公司
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_COMPANY)
	public static String deleteCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error; 
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	ArrayList<String> temp = (ArrayList<String>)param.get("ids");
    	ArrayList<Long> ids = new ArrayList<Long>();
    	for (String t: temp)
    	{
    		ids.add(Long.valueOf(t));
    	}
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	userService.deleteCompany(ids);
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取公司详细信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_COMPANY_INFO)
	public static String getCompanyInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("id");
    	long id = Long.valueOf(temp);
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Company company = userService.getCompany(id);
    	Users admin = userService.getCompanyAdminUser(company.getId());
    	Users sysU = userService.getUser(account);
    	boolean sys = sysU.getRole() == Constant.ADIMI;
    	HashMap<String, String> retsult = new HashMap<String, String>();
    		
    	retsult.put("companyName", company.getName());
    	retsult.put("contactName", company.getUserName());
    	retsult.put("contactPhone", company.getUserPhone());
    	retsult.put("contactMail", company.getUserMail());
    	retsult.put("description", company.getDescription());
    	retsult.put("code", company.getCode());
    		
    	retsult.put("adminName", admin.getUserName());
    	retsult.put("adminId", String.valueOf(admin.getId()));
    	if (sys)     // 系统管理员才有权
    	{
	    	retsult.put("count", String.valueOf(company.getMaxUsers()));
	    	retsult.put("date", String.valueOf(company.getUd()));
	    	if (company.getBd() != null)
    		{
    			retsult.put("beginDate", String.valueOf(company.getBd()));
    		}
    		if (company.getFd() != null)
    		{
    			retsult.put("regDate", String.valueOf(company.getFd()));
    		}
    	}
    		
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 系统重置管理员密码，不同于管理员自己修改密码，此时不需要原密码
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.RESET_COMPANY_ADMIN_PASS)
	public static String resetCompanyAdminPass(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("userId");
    	long userId = Long.valueOf(temp);
    	temp = (String)param.get("companyId");     // 为后续验证用
    	long companyId = Long.valueOf(temp);
    	String pass = (String)param.get("pass");
    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Users admin = userService.getUser(userId);
    	if (companyId != admin.getCompany().getId().longValue())
    	{
    		error = JSONTools.convertToJson(ErrorCons.USER_INFOR_ERROR, null);
    	}
    	else
    	{
	    	MD5 md5 = new MD5();
	    	pass = md5.getMD5ofStr(pass);
	    	admin.setPassW(pass);
	    	userService.updataUserinfo(admin);
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	}
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 改变管理员密码，此时需要原密码，如果是公司管理员，则需要公司的id
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_USER_PASS)
	public static String updateUserPass(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("userId");
    	long userId = Long.valueOf(temp);
    	temp = (String)param.get("companyId");     // 为后续验证用
    	long companyId = (temp != null && temp.length()>0) ? Long.valueOf(temp) : -1;    		
    	String pass = (String)param.get("pass");
    	String oldPass = (String)param.get("oldPass");
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Users admin = userService.getUser(userId);
    	if (account.equals(admin.getUserName()) && ((companyId == -1 && admin.getCompany() == null)
    				|| (companyId == admin.getCompany().getId().longValue())))     // 管理员自己登录才能修改自己的密码
    	{
	    	MD5 md5 = new MD5();
	    	oldPass = md5.getMD5ofStr(oldPass);
	    	if (!oldPass.equals(admin.getPassW()))
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.USER_PASSWORD_ERROR, null);
	    	}
	    	else
	    	{
		   		pass = md5.getMD5ofStr(pass);
		   		admin.setPassW(pass);
		   		userService.updataUserinfo(admin);
		   		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	    	}
    	}
    	else
    	{
    		error = JSONTools.convertToJson(ErrorCons.USER_INFOR_ERROR, null);
    	}
    	return  error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 更新公司
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_COMPANY)
	public static String updateCompany(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	long companyId = Long.valueOf(temp);
    	String companyName = (String)param.get("companyName");
    	String contactName = (String)param.get("contactName");
    	String contactPhone = (String)param.get("contactPhone");
    	String contactMail = (String)param.get("contactMail");
    	String desc = (String)param.get("description");
    		
    	temp = (String)param.get("count");
    	int count = Integer.valueOf(temp);
    	temp = (String)param.get("date");
    	long date = Long.valueOf(temp);
    		
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	Users sysU = userService.getUser(account);
    	boolean sys = sysU.getRole() == Constant.ADIMI;
    	
    	Company company = userService.getCompany(companyId);
    	Users admin = userService.getCompanyAdminUser(companyId);
    	if (!companyName.equals(company.getName()) && userService.isExistCompany(companyName))
    	{
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_EXIST, null);
    	}
    	else
    	{
    		company.setName(companyName);
    		company.setUserName(contactName);
    		company.setUserPhone(contactPhone);
    		company.setUserMail(contactMail);
    		company.setDescription(desc);
    		if (sys)   // 只有系统管理员才可以修改这两项
    		{
	    		company.setMaxUsers(count);
	    		company.setUd(date);
    		}
    		admin.setRealName(companyName+"管理员");
    		userService.update(company);
    		userService.updataUserinfo(admin);
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    		List<HashMap<String, String>> apps = (List<HashMap<String, String>>)param.get("apps");
    		if (apps != null && apps.size() > 0)
    		{
    			AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME);
    			appsService.addOrUpdateCompanyApps(apps, company);
    		}
    	}
    	
    	return  error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 新建公司部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_ORG)
	public static String addOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("parentId");
    	Long parentId = temp != null ? Long.valueOf(temp) : null;
    	temp = (String)param.get("leaderId");
    	Long leaderId = temp != null ? Long.valueOf(temp) : null;
    	String name = (String)param.get("name");     	
    	String desc = (String)param.get("desc");    	
    	temp = (String)param.get("sortNum");
    	Integer sortNum = temp != null ? Integer.valueOf(temp) : null;
    	
    	ArrayList<String> members = (ArrayList<String>)param.get("memberIds");
    	    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Company company = userService.getCompany(companyId);
    	if (company == null)
    	{
    		error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_EXIST_ERROR, null);
    	}
    	else
    	{
    		Organizations org = new Organizations(name, desc);
    		if (sortNum != null)
    		{
    			org.setSortNum(sortNum);
    		}
    		Long[] addUserIds;
    		if (members != null && members.size() > 0)
    		{
    			int size = members.size();
    			addUserIds = new Long[size];
    			for(int i = 0; i < size; i++)
    			{
    				addUserIds[i] = Long.valueOf(members.get(i));
    			}
    		}
    		else 
    		{
    			addUserIds = null;
    		}
    		
    		Users leader = leaderId != null ? userService.getUser(leaderId) : null;
    		
    		userService.addOrganization(company, parentId, org, addUserIds, leader);
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	}
    	return  error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 新建公司部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ORG_LIST)
	public static String getOrgList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");  
    	//Long roleId = Long.valueOf((String)param.get("roleId"));
//    	Long orgId = Long.valueOf((String)param.get("roleId"));
    	String role = (String)param.get("roleId");
    	String org = (String)param.get("orgId");
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("parentId");
    	Long parentId = temp != null ? Long.valueOf(temp) : null;
    	if (parentId <=0 )    // -1参数表示获得根
    	{
    		parentId = null;
    	}    	
    	temp = (String)param.get("treeFlag");
    	boolean treeFlag = temp != null ? Boolean.valueOf(temp): false;
    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	ArrayList<Object> retsult = new ArrayList<Object>();  
    	if(role!=null){
    		Long roleId = Long.valueOf(role);
    		if(roleId==9){
    			Long orgId = Long.valueOf(org);
    			Organizations company = userService.getGroup(orgId);
        		HashMap<String, String> com = new HashMap<String, String>();    		
        		com.put("id", String.valueOf(company.getId()));
        		com.put("name", company.getName());   
        		com.put("deptDesc",company.getDescription());
        		com.put("parentKey", company.getParentKey());
        		com.put("sortNum", String.valueOf(company.getSortNum()));
        		retsult.add(com);
    		}
    		else if(roleId==8){
    			List<Organizations> company = userService.getOrganizations(companyId, parentId, treeFlag);
//       		 	company = WebTools.sortUsers(company, "sortNum", true);
       	    	//ArrayList<Object> retsult = new ArrayList<Object>();    
       	    	for (Organizations tempC : company)
       	    	{
       	    		HashMap<String, String> com = new HashMap<String, String>();    		
       	    		com.put("id", String.valueOf(tempC.getId()));
       	    		com.put("name", tempC.getName());  
       	    		com.put("deptDesc",tempC.getDescription());
       	    		com.put("parentKey", tempC.getParentKey());
       	    		com.put("sortNum", String.valueOf(tempC.getSortNum()));
       	    		retsult.add(com);
       	    	}
    		}
    		
    	}
    	else{
    		if(org!=null){
    			parentId = Long.valueOf(org);
    		}
    		List<Organizations> company = userService.getOrganizations(companyId, parentId, treeFlag);
//    		 company = WebTools.sortUsers(company, "sortNum", true);
    	    	//ArrayList<Object> retsult = new ArrayList<Object>();    
    	    	for (Organizations tempC : company)
    	    	{
    	    		HashMap<String, String> com = new HashMap<String, String>();    		
    	    		com.put("id", String.valueOf(tempC.getId()));
    	    		com.put("name", tempC.getName());   
    	    		com.put("deptDesc",tempC.getDescription());
    	    		com.put("parentKey", tempC.getParentKey());
    	    		com.put("sortNum", String.valueOf(tempC.getSortNum()));
    	    		retsult.add(com);
    	    	}
    	    	//return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	/*else if(roleId==9){
    		company = userService.getGroup(orgId);
    	}*/
    	
    	//company = userService.getOrganizationByUsers(userId)
    	
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 得到公司的所有用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_COMPANY_USE_LIST)
	public static String getCompanyUserList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	String sort = (String)param.get("sort");  // id, userName,realName,realEmail
    	String dir = (String)param.get("dir");    // 排序的方式（asc或desc）
    	    	
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	List<Users> users = userService.getCompanyUsers(companyId, start, count, sort, dir);
    	ArrayList<Object> retsult = new ArrayList<Object>();  
    	Long size = userService.getUsersCount(companyId);
    	HashMap<String, Object> com2 = new HashMap<String, Object>();    		
		com2.put("total", String.valueOf(size));
		retsult.add(com2);
		
		ArrayList<Object> usersRet = new ArrayList<Object>(); 
    	for (Users tempC : users)
    	{
    		HashMap<String, String> com = new HashMap<String, String>();    		
    		com.put("id", String.valueOf(tempC.getId()));
    		com.put("name", tempC.getUserName());
    		com.put("display", tempC.getRealName());
    		com.put("mail", tempC.getRealEmail());
    		com.put("duty", tempC.getDuty());
    		com.put("size", String.valueOf(tempC.getStorageSize()));
    		com.put("validate", String.valueOf(tempC.getValidate() == 1  ? true : false));
    		usersRet.add(com);
    	}
    	com2 = new HashMap<String, Object>();    		
		com2.put("list", usersRet);
    	retsult.add(com2);
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 得到公司的所有用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_COMPANY_USE_COUNT)
	public static String getCompanyUserCount(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	    	
    	UserService userService = getUserService(); 
    	Long count = userService.getUsersCount(companyId);
    	HashMap<String, String> com = new HashMap<String, String>();    		
		com.put("count", String.valueOf(count));
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, com);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 得到公司部门的所有用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ORG_USE_COUNT)
	public static String getOrgUserCount(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String searchcond = (String)param.get("searchcond");
    	String temp = (String)param.get("orgId");
    	Long orgId = Long.valueOf(temp);
    	temp = (String)param.get("tree");
    	boolean treeFlag = temp != null ? Boolean.valueOf(temp): false;
    	
    	    	
    	UserService userService = getUserService(); 
    	Long count = userService.findUsersCountByOrgId(null,orgId, treeFlag,searchcond);
    	HashMap<String, String> com = new HashMap<String, String>();    		
		com.put("count", String.valueOf(count));
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, com);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	public static String toUTF8(String isoString) {
        String utf8String = null;
        if (null != isoString && !isoString.equals("")) {
            try {
                byte[] stringBytesISO = isoString.getBytes("ISO-8859-1");
                utf8String = new String(stringBytesISO, "UTF-8");
            } catch (Exception e) {
                // As we can't translate just send back the best guess.
                System.out.println("UnsupportedEncodingException is: "
                        + e.getMessage());
                utf8String = isoString;
            }
        } else {
            utf8String = isoString;
        }
        return utf8String;
    }

	/**
	 * 获取公司部门的成员信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ORG_USE_LIST)
	public static String getOrgUserList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {    	
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	Object obj=req.getParameterNames();
    	String account = (String)param.get("account");  
    	UserService userService = getUserService();
    	Users user = userService.getUser(account);
    	String searchcond = (String)param.get("searchcond");
    	//System.out.println("searchcond==========================="+searchcond);
    	String temp = (String)param.get("orgId");
    	Long orgId = Long.valueOf(temp);
    	temp = (String)param.get("roleId");
    	Long roleId = Long.valueOf(temp);//管理员类型，8为云办公管理员，9为单位管理员
    	temp = (String)param.get("start");
    	if (temp == null || "undefined".equals(temp))//为了兼容EXT的分页显示用的
    	{
    		temp=req.getParameter("start");
    	}
//    	System.out.println(req.getParameter("limit")+"==="+req.getParameter("start")+"==="+req.getParameter("sort")+"==="+req.getParameter("dir"));
    	int start = (temp != null && !"undefined".equals(temp)) ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	if (temp == null || "undefined".equals(temp))//为了兼容EXT的分页显示用的
    	{
    		temp=req.getParameter("limit");
    	}
    	int count = (temp != null && !"undefined".equals(temp)) ? Integer.valueOf(temp) : -1;
    	String sort = (String)param.get("sort");  // id, userName,realName,realEmail
    	if (sort==null || "undefined".equals(sort))//为了兼容EXT的分页显示用的
    	{
    		sort=req.getParameter("sort");
    	}
    	String dir = (String)param.get("dir");    // 排序的方式（asc或desc）
    	if (dir==null || "undefined".equals(dir))//为了兼容EXT的分页显示用的
    	{
    		dir=req.getParameter("dir");
    	}
    	if (sort==null || sort.length()==0)
    	{
    		sort="sortNum";
    		dir="ASC";
    	}
    	temp = (String)param.get("tree");
    	boolean treeFlag = temp != null ? Boolean.valueOf(temp): false; 
    	
    	if(roleId == 9 && orgId==0){
    		orgId = Long.valueOf((String)param.get("orgid"));
    	}
    	List<Users> users = userService.getOrgUsers(user.getCompany().getId(),orgId, treeFlag, start, count, sort, dir,searchcond);
    	ArrayList<Object> retsult = new ArrayList<Object>();    	
    	
    	Long size = userService.findUsersCountByOrgId(user.getCompany().getId(),orgId, treeFlag,searchcond);

    	
    	for (Users tempC : users)
    	{
    		HashMap<String, String> com = new HashMap<String, String>();    		
    		com.put("id", String.valueOf(tempC.getId()));
    		com.put("userName", tempC.getUserName());
    		com.put("realName", tempC.getRealName());
    		com.put("realEmail", tempC.getRealEmail());
    		com.put("duty", tempC.getDuty());
    		com.put("storageSize", String.valueOf(tempC.getStorageSize()));
    		com.put("sortNum", String.valueOf(tempC.getSortNum()));
    		com.put("validate", String.valueOf(tempC.getValidate() == 1  ? "true" : "false"));
            com.put("sortNum", String.valueOf(tempC.getSortNum()));
            com.put("fawenuser", String.valueOf(tempC.getFawen()));
    		if (orgId!=null && orgId.longValue()>0)
    		{
    			com.put("userorgId", String.valueOf(orgId));
    		}
    		retsult.add(com);
    	}
    	HashMap<String, Object> back = new HashMap<String, Object>();
    	back.put("list", retsult);
    	back.put("total", size);
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
    	
    	
//    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);    	
	   // resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 删除公司部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_ORG)
	public static String deleteOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");      // 需要权限验证    	
    	ArrayList<String> temp = (ArrayList<String>)param.get("orgIds");
    	ArrayList<Long> orgIds = new ArrayList<Long>();
    	for (String t : temp)
    	{
    		orgIds.add(Long.valueOf(t));
    	}
    	    	
    	UserService userService = getUserService(); 
    	userService.deleteOrganizations(orgIds);
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 更新公司部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_ORG_INFO)
	public static String updateOrgInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");      // 需要权限验证    	
    	String temp = (String)param.get("orgId");
    	Long orgId = Long.valueOf(temp);
    	temp = (String)param.get("parentId");
    	Long parentId = temp != null ? Long.valueOf(temp) : null;
    	temp = (String)param.get("leaderId");
    	Long leaderId = temp != null ? Long.valueOf(temp) : null;
    	String name = (String)param.get("name");     	
    	String desc = (String)param.get("desc"); 
    	temp = (String)param.get("sortNum");
    	Integer sortNum = temp != null ? Integer.valueOf(temp) : null;
    	
    	UserService userService = getUserService(); 
    	userService.updateOrganization(parentId, orgId, leaderId, name, desc, sortNum);
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 修改公司部门的成员信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_ORG_USE_LIST)
	public static String updateOrgUserList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("orgId");
    	Long orgId = Long.valueOf(temp);    	
    	ArrayList<String> adds = (ArrayList<String>)param.get("addIds");
    	ArrayList<String> dels = (ArrayList<String>)param.get("delIds");
    	    	
    	List<Long> delUserIds = new ArrayList<Long>();
    	List<Long> addUserIds = new ArrayList<Long>();
    	if (dels != null)
    	{
	    	for (String t : dels)
	    	{
	    		delUserIds.add(Long.valueOf(t));
	    	}
    	}
    	if (adds != null)
    	{	    	
	    	for (String t : adds)
	    	{
	    		addUserIds.add(Long.valueOf(t));
	    	}
    	}
    	
    	UserService userService = getUserService(); 
    	userService.updateOrgUserList(orgId, delUserIds, addUserIds);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 查询获取公司部门的信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SEARCH_ORG)
	public static String searchOrgList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	String key = (String)param.get("key"); 
    	
    	UserService userService = getUserService(); 
    	List<Organizations> orgs = userService.findOrganizationsByKey(key, companyId);
    	ArrayList<Object> retsult = new ArrayList<Object>();    	
    	for (Organizations tempC : orgs)
    	{
    		HashMap<String, String> com = new HashMap<String, String>();    		
    		com.put("id", String.valueOf(tempC.getId()));
    		com.put("name", tempC.getName());    		
    		retsult.add(com);
    	}
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 查询获取公司的成员信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SEARCH_COMPANY_USER_LIST)
	public static String searchCompanyUserList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	String key = (String)param.get("key");
    	temp = (String)param.get("option");     // 搜索的方式，0表示搜索用户登录名，1表示搜索邮件地址，2表示搜索用户显示名，其他值表示即搜索用户名，也搜索邮件地址。
    	int option = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	String sort = (String)param.get("sort");  // id, userName,realName,realEmail
    	String dir = (String)param.get("dir");    // 排序的方式（asc或desc）
    	
    	UserService userService = getUserService(); 
    	List<Users> users = userService.searchUser(companyId, option, key, start, count, sort, dir);
    	
    	ArrayList<Object> retsult = new ArrayList<Object>();    	
    	for (Users tempC : users)
    	{
    		HashMap<String, String> com = new HashMap<String, String>();    		
    		com.put("id", String.valueOf(tempC.getId()));
    		com.put("name", tempC.getUserName());
    		com.put("display", tempC.getRealName());
    		com.put("mail", tempC.getRealEmail());
    		com.put("duty", tempC.getDuty());
    		com.put("size", String.valueOf(tempC.getStorageSize()));
    		com.put("validate", String.valueOf(tempC.getValidate() == 1  ? "true" : "false"));
    		retsult.add(com);
    	}
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 禁止公司的成员使用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.FORBID_USER)
	public static String forbidUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");  
    	List<String> temp = (List<String>)param.get("userIds");
    	List<Long> userIds = new ArrayList<Long>();
    	for (String t : temp)
    	{
    		userIds.add(Long.valueOf(t));
    	}
    	
    	String temp2 = (String)param.get("value");     // 1为有效，0或无此参数为无效
    	Short value = temp2 != null ? Short.valueOf(temp2) : 0;
    	
    	UserService userService = getUserService(); 
    	userService.forbidUser(userIds, value);
    	
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 *删除公司的成员
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_USER)
	public static String deleteUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	List<String> temp = (List<String>)param.get("userIds");
    	List<Long> userIds = new ArrayList<Long>();
    	for (String t : temp)
    	{
    		userIds.add(Long.valueOf(t));
    	}
    	
    	UserService userService = getUserService(); 
    	userService.deleteUsers(userIds);
    	
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 创建公司用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_USER)
	public static String addUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	UserService userService = getUserService();
    	String account = (String)param.get("account"); 
    	Users adminuser=userService.getUser(account);
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("orgId");
        String fawenuser=(String)param.get("fawenuser");
    	Long orgId = Long.valueOf(temp);
    	   	
    	String name = (String)param.get("name");
    	if (WebConfig.cloudPro)//公云账号
    	{
    		if (name!=null && name.indexOf("_")<0)//不包含下划线
    		{
    			String code=adminuser.getCompany().getCode();
    			if (code!=null && code.length()>1)//只有单位编号超过1位才强制加单位简称
    			{
    				name=code+"_"+name;
    			}
    		}
    	}
    	String realName = (String)param.get("displayName");
    	String pass = (String)param.get("pass");
    	String mail = (String)param.get("mail");    		
    	String portrait = (String)param.get("portrait");
    	String duty = (String)param.get("duty");
    	temp = (String)param.get("roleId");
    	Long roleId = Long.valueOf(temp);
    	String ca = (String)param.get("caName");
    	temp = (String)param.get("size");
    	float size = temp != null ? Integer.valueOf(temp) : WebConfig.defaultsize;
    	temp = (String)param.get("man");
    	boolean man = temp != null ? Integer.valueOf(temp) == 0 : true;
    	temp = (String)param.get("birthday");
    	Date birthday = temp != null ? new Date(Long.valueOf(temp)): null;
    	String mobile = (String)param.get("mobile");
    	String phone = (String)param.get("phone");
    	String address = (String)param.get("address");
    	String description = (String)param.get("description");
    	String partadmin=(String)param.get("partadmin");
    	String calogin=(String)param.get("calogin");
    	
    	temp = (String)param.get("sortNum");
    	Integer sortNum = temp != null ? Integer.valueOf(temp) : 1000000;
    	
    	Company company = userService.getCompany(companyId);
    	String cc = company.getCode();
    	/*if (cc != null && cc.length() > 0) {
    		if (WebConfig.cloudPro) {
    			name = cc + "_" + name;
    		}
    	}*/
    	if (userService.isCompanyUserFull(companyId)) {  // 允许的最大人数已经满了，不能在添加
			error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, null);
		} else if (userService.getUser(name) != null) {  // 有同名存在
    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
    	} else {
	    	Users user = new Users(name, pass, mail, 0, realName);
	    	if (sortNum != null) {
	    		user.setSortNum(sortNum);
	    	}
	    	user.setResetPass(pass);
	    	user.setImage(portrait);
	    	user.setDuty(duty);
	    	user.setCaId(ca);
	    	user.setStorageSize(size);
	    	user.setMan(man);
	    	user.setBirthday(birthday);
	    	user.setMobile(mobile);
	    	user.setPhone(phone);
	    	user.setAddress(address);
	    	user.setDescription(description);
    		if ("true".equals(partadmin))//部门管理和普通人员之间切换
	    	{
    			user.setRole((short)Constant.PART_ADMIN);
	    	}
            if("true".equals(fawenuser)){
                user.setFawen(true);
            }else{
                user.setFawen(false);
            }
    		if ("true".equals(calogin))//部门管理和普通人员之间切换
	    	{
    			user.setLoginCA("1");
	    	}
    		else
    		{
    			user.setLoginCA("0");
    		}
	    	List<Long> addOrgIds = new ArrayList<Long>();
	    	addOrgIds.add(orgId);
	    	userService.addOrUpdateUser(companyId, user, addOrgIds, null, roleId, null); 
	    	if (!WebConfig.cloudPro)//私云
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "新建用户成功！"); 
	    	}
	    	else
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "新建用户成功！该用户账号为："+user.getUserName());
	    	}
    	}
       
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 修改公司用户信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_USER)
	public static String updateUserInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	   	
    	String name = (String)param.get("name");
    	String realName = (String)param.get("displayName");
    	String pass = (String)param.get("pass");
    	String mail = (String)param.get("mail");    		
    	String portrait = (String)param.get("portrait");
    	String duty = (String)param.get("duty");
    	temp = (String)param.get("roleId");
    	Long roleId = Long.valueOf(temp);
    	temp = (String)param.get("oldRoleId");
    	Long oldRoleId = temp != null ? Long.valueOf(temp) : null;
    	String ca = (String)param.get("caName");
    	
    	temp = (String)param.get("size");
    	float size = temp != null ? Float.valueOf(temp) : WebConfig.defaultsize;
    	temp = (String)param.get("sortNum");
    	Integer sortNum = temp != null ? Integer.valueOf(temp) : 1000000;
    	temp = (String)param.get("man");
    	boolean man = temp != null ? Integer.valueOf(temp) == 0 : true;
    	temp = (String)param.get("birthday");
    	Date birthday = temp != null ? new Date(Long.valueOf(temp)): null;
    	String mobile = (String)param.get("mobile");
    	String phone = (String)param.get("phone");
    	String address = (String)param.get("address");
    	String description = (String)param.get("description");
        String partadmin=(String)param.get("partadmin");
        String fawenuser=(String)param.get("fawenuser");
    	String calogin=(String)param.get("calogin");//是否CA登录
    	UserService userService = getUserService(); 
    	Users otherUser = userService.getUser(name); 
    	if (otherUser != null && otherUser.getId().longValue() != userId.longValue())   // 有同名存在
    	{
    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
    	}
    	else
    	{
	    	Users user = userService.getUser(userId);
	    	user.setUserName(name);
	    	user.setRealEmail(mail);
	    	user.setRealName(realName);
	    	user.setResetPass(pass);
	    	user.setImage(portrait);
	    	user.setDuty(duty);
	    	user.setCaId(ca);
	    	user.setStorageSize(size);
	    	user.setSortNum(sortNum);
	    	user.setMan(man);
	    	user.setBirthday(birthday);
	    	user.setMobile(mobile);
	    	user.setPhone(phone);
	    	user.setAddress(address);
	    	user.setDescription(description);
	    	
	    	if (user.getRole()==null || user.getRole()==0 || user.getRole()==4 || user.getRole()>=Constant.PART_ADMIN)//部门管理和普通人员之间切换
	    	{
	    		if ("true".equals(partadmin))
		    	{
	    			user.setRole((short)Constant.PART_ADMIN);
		    	}
	    		else
	    		{
	    			user.setRole((short)0);
	    		}
	    	}
            if("true".equals(fawenuser)){
                user.setFawen(true);
            }else{
                user.setFawen(false);
            }
	    	if ("true".equals(calogin) && ca!=null && ca.length()>0)
	    	{
	    		user.setLoginCA("1");
	    	}
	    	else
	    	{
	    		user.setLoginCA("0");
	    	}
	    	userService.addOrUpdateUser(companyId, user, null, null, roleId, oldRoleId);    		    		
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
    	}
       
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 上传用户头像
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPLOAD_USER_PORTRAIT)
	public static String uploadUserPortrait(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("userId");
    	Long userId = temp != null ? Long.valueOf(temp) : null;
    	
    	UserService userService = getUserService();
    	
    	String ret = userService.addOrUpdateUserPortrait(userId, WebConfig.userPortraitPath, req);        	
    	if (ret != null)
    	{
    	    File file = new File(WebConfig.userPortraitPath + File.separatorChar + ret);
            long fileSize = file.length();
            if (fileSize > 100 * 1024)//不允许上传大于100k的头像
            {
            	error = JSONTools.convertToJson(ErrorCons.USER_IMAGE_SIZE_ERROR, null);
                file.delete();
            }
    	    else
    	    {
    	    	HashMap<String, String> re = new HashMap<String, String>();
    	    	re.put("url", WebConfig.userPortrait + ret);
    	    	re.put("name", ret);
    	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, re);
    	    }
    	}
    	else
    	{
    		error = JSONTools.convertToJson(ErrorCons.FILESIZE_UPLOAD_ERROR, null);
    	}
        
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取用户所在的部门列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_ORG_LIST)
	public static String getUserOrgList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     	
    	String temp = (String)param.get("companyId");   // 后续验证用
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	
    	UserService userService = getUserService(); 
    	List<Organizations> company = userService.getOrganizationByUsers(userId);
    	ArrayList<Object> retsult = new ArrayList<Object>();    	
    	for (Organizations tempC : company)
    	{
    		HashMap<String, String> com = new HashMap<String, String>();    		
    		com.put("id", String.valueOf(tempC.getId()));
    		com.put("name", tempC.getName());
    		com.put("deptDesc",tempC.getDescription());
    		com.put("parentKey", tempC.getParentKey()); 
    		
    		retsult.add(com);
    	}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
    	
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    ///resp.getWriter().write(error);
    }
	
	/**
	 * 修改用户所在的部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPLOAD_USER_ORG)
	public static String updateUserOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	List<String> nids = (List<String>)param.get("addOrgIds");
    	List<String> oids = (List<String>)param.get("delOrgIds");
    		
    	List<Long> addIds = new ArrayList<Long>();
    	if (nids != null)
    	{
	    	for (String t : nids)
	    	{
	    		addIds.add(Long.valueOf(t));
	    	}
    	}
    	List<Long> delOrgIds = new ArrayList<Long>();
    	if (oids != null)
    	{
	    	for (String t : oids)
	    	{
	    		delOrgIds.add(Long.valueOf(t));
	    	}
    	}
    	
    	UserService userService = getUserService(); 
    	userService.addOrUpdateUser(companyId, userId, addIds, delOrgIds);  
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取用户使用的设备信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_DEV_LIST)
	public static String getUserDevOrgList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	
    	UserService userService = getUserService(); 
    	List<UsersDevice> dev = userService.getUserDeviceList(userId);
    	ArrayList<Object> retsult = new ArrayList<Object>();
    	for (UsersDevice ud : dev)
    	{
    		HashMap<String, String> add = new HashMap<String, String>();
    		add.put("id", String.valueOf(ud.getId()));
    		add.put("name", ud.getMobDevName());
    		add.put("devID", ud.getMobDevID());
    		add.put("os", ud.getMobDevOS());
    		add.put("date", String.valueOf(ud.getMobDevTime().getTime()));
    		add.put("status", String.valueOf(ud.getMobDevStatus()));
    		retsult.add(add);
    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);  
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    ///resp.getWriter().write(error);
    }
	
	/**
	 * 修改用户使用的设备信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPLOAD_USER_DEV)
	public static String updateUserDev(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	temp = (String)param.get("value");     // 1表示该设备需要禁用, 0表示该设备正常
    	Short value = temp != null ? Short.valueOf(temp) : 0;
    	
    	List<String> nids = (List<String>)param.get("devIds");    		
    	List<Long> mobDevIDS = new ArrayList<Long>();
    	if (nids != null)
    	{
	    	for (String t : nids)
	    	{
	    		mobDevIDS.add(Long.valueOf(t));
	    	}
    	}
    	
    	UserService userService = getUserService(); 
    	userService.forbidDevice(mobDevIDS, value);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取用户所有信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_ALL_INFO)
	public static String getUserAllInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	
    	UserService userService = getUserService(); 
    	Users user = userService.getUser(userId);
    	HashMap<String, String> ret = new HashMap<String, String>();
    	ret.put("id", String.valueOf(user.getId()));
    	if (user.getCompany() != null) {
    		ret.put("companyId", String.valueOf(user.getCompany().getId()));
    	}
    	ret.put("name", user.getUserName());
    	ret.put("displayName", user.getRealName());
    	ret.put("mail", user.getRealEmail());  
    	if (user.getImage1() != null)
    	{
    		ret.put("portrait", WebConfig.userPortrait + user.getImage1());
    		ret.put("image", WebConfig.userPortrait + user.getImage1());
    	}
    	else
    	{
    		ret.put("portrait", WebConfig.userPortrait + "image.jpg");
    		ret.put("image", WebConfig.userPortrait + "image.jpg");
    	}
    	ret.put("duty", user.getDuty());
    	UsersRoles uroles = userService.findUserRoleInSystem(userId, companyId);
    	if (uroles != null && uroles.getRole() != null) {
    		ret.put("roleId", String.valueOf(uroles.getRole().getId()));
    	}
    	ret.put("caName", user.getCaId());
    	ret.put("loginCA",user.getLoginCA());
    	ret.put("size", String.valueOf(user.getStorageSize()));
    	ret.put("man", String.valueOf(user.getMan()));
    	if (user.getBirthday() != null) {
    		ret.put("birthday", String.valueOf(user.getBirthday().getTime()));
    	}
    	if (user.getRole()==(short)Constant.PART_ADMIN) {
    		ret.put("partadmin", "true");//部门管理员
    	} else {
    		ret.put("partadmin", "false");
    	}
        if(user.getFawen()==true){
            ret.put("fawenuser", "true");//是否有发文权限
        }
    	ret.put("mobile", user.getMobile());
    	ret.put("phone", user.getPhone());
    	ret.put("address", user.getAddress());
    	ret.put("description", user.getDescription());
    	ret.put("validate", String.valueOf(user.getValidate()));
    	ret.put("sortNum", String.valueOf(user.getSortNum()));
    	
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);  
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 获取用户的部分信息，为用户自己修改信息用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_PART_INFO)
	public static String getUserPartInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account"); 
    	String temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	
    	UserService userService = getUserService(); 
    	Users user = userService.getUser(userId);
    	HashMap<String, String> ret = new HashMap<String, String>();
    	ret.put("id", String.valueOf(user.getId()));
    	if (user.getCompany() != null)
    	{
    		ret.put("companyId", String.valueOf(user.getCompany().getId()));
    	}
    	ret.put("name", user.getUserName());
    	ret.put("displayName", user.getRealName());
    	ret.put("mail", user.getRealEmail());    	
    	ret.put("portrait", user.getImage1());
    	ret.put("man", String.valueOf(user.getMan()));
    	if (user.getBirthday() != null)
    	{
    		ret.put("birthday", String.valueOf(user.getBirthday().getTime()));
    	}
    	ret.put("mobile", user.getMobile());
    	ret.put("phone", user.getPhone());
    	ret.put("address", user.getAddress());
    	ret.put("description", user.getDescription());
    	
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);  
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    ///resp.getWriter().write(error);
    }
	
	/**
	 * 修改用户的部分信息，为用户自己修改信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATE_USER_PART_INFO)
	public static String updateUserPartInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	    	
    	String realName = (String)param.get("displayName");
    	String mail = (String)param.get("mail");    		
    	String portrait = (String)param.get("portrait");
    	temp = (String)param.get("man");
    	boolean man = temp != null ? Integer.valueOf(temp) == 0 : true;
    	temp = (String)param.get("birthday");
    	Date birthday = temp != null ? new Date(Long.valueOf(temp)): null;
    	String mobile = (String)param.get("mobile");
    	String phone = (String)param.get("phone");
    	String address = (String)param.get("address");
    	String description = (String)param.get("description");
    		
    	UserService userService = getUserService(); 
    	Users user = userService.getUser(userId);
    	user.setRealEmail(mail);
    	user.setRealName(realName);
    	user.setImage(portrait);
    	user.setMan(man);
    	user.setBirthday(birthday);
    	user.setMobile(mobile);
    	user.setPhone(phone);
    	user.setAddress(address);
    	user.setDescription(description);
    	userService.update(user); 
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	@HandlerMethod(required = false, methodName = ServletConst.ADD_EXTRA_USER)
	public static String addExtraUser(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		String error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = "sys_admin";
    	Long companyId = Long.valueOf(1);//暂时这样写死，以后示情况更改
    	Long orgId = Long.valueOf(1);
    	String name = (String)param.get("name");
    	String realName = (String)param.get("displayName");
    	String mail = (String)param.get("mail");
    	String pass = (String)param.get("pass");
    	String mobile = (String)param.get("mobile");//手机号
    	String vcode = (String)param.get("vcode");//验证码
    	Long roleId = Long.valueOf(11);//这里不能这样写，要出错的
    	float size = WebConfig.defaultsize;
    	boolean man = true;
    	Date birthday = null;

    	UserService userService = getUserService(); 
    	boolean isgoon=true;
    	if (mobile!=null && mobile.length()>0 && vcode!=null && vcode.length()>0) {
    		//手机验证码对不对
    		vcode=vcode.toUpperCase();
        	System.out.println("vcode====="+vcode);
        	String mobileregist=(String)WebConfig.mobileregistmap.get(mobile);//获取手机注册号
    		if (mobileregist==null) {
    			error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);//验证码不正确
    			isgoon=false;
    		} else {
    			String[] registvalues=mobileregist.split(",");
	    		if (!vcode.equals(registvalues[0]))
	    		{
	    			isgoon=false;
	    			error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);//验证码不正确
	    		}
    		}
    	}
    	if (isgoon) {
	    	if (userService.isCompanyUserFull(companyId)) {  // 允许的最大人数已经满了，不能在添加
				error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, null);
			}
	    	else if (userService.getUser(name) != null) {  // 有同名存在
	    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
	    	} else {
		    	Users user = new Users(name, pass, mail, 0, realName);
		    	user.setResetPass(pass);
		    	user.setStorageSize(size);
		    	user.setMan(man);
		    	user.setBirthday(birthday);
		    	user.setMobile(mobile);
		    	if (mobile!=null && mobile.length()>0 && vcode!=null && vcode.length()>0) {
		    		user.setValidate((short) 1);
		    	} else {
		    		user.setValidate((short) 0);
		    	}
		    	
		    	List<Long> addOrgIds = new ArrayList<Long>();
		    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		    	List<Organizations> list=(List<Organizations>)jqlService.findAllBySql("select a from Organizations as a where a.company.id=1");
		    	if (list!=null && list.size()>0) {
		    		orgId=list.get(0).getId();//默认将注册用户加入第一个部门
		    	}
		    	addOrgIds.add(orgId);
		    	userService.addOrUpdateUser(companyId, user, addOrgIds, null, roleId, null);
		    	user = userService.getUser(name);
		    	String key = user.getSpaceUID();
		    	String port="";
		    	if (req.getServerPort()!=80) {
		    		port=":"+ req.getServerPort();
		    	}
		    	if (mobile!=null && mobile.length()>0 && vcode!=null && vcode.length()>0) {
		    		//这里不需要发邮件了
		    	} else {
			    	String httpUrl = req.getScheme() + "://" + req.getServerName() + port + req.getContextPath() ;
			    	System.out.println("如果有反向代理需要这里替换一下======================================="+httpUrl);
			    	httpUrl=QueryDb.getIpName(httpUrl);
			    	MailSender.sendRegisterMail(name, mail,pass, user.getPassW(), key,httpUrl);
		    	}
	    	}
    	}
    	return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
	}
	
	@HandlerMethod(required = false, methodName = ServletConst.VALIDATECODE)
	public static String validateCode(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		String error;
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	Object vcode=param.get("vcode");
    	String codevalue="";
    	if (vcode instanceof Integer)
    	{
    		codevalue=String.valueOf((Integer)vcode);
    	}
    	else
    	{
    		codevalue=(String)vcode;
    	}
    	codevalue=codevalue.toUpperCase();
    	System.out.println("codevalue====="+codevalue);
		HttpSession session =req.getSession();
		String code=(String)session.getAttribute("validateCode");
//		System.out.println("Userservlet session================="+session.getId()+"===="+code);
//		Cookie[] cookies=req.getCookies();
//		if (cookies!=null)
//		{
//			for (int i=0;i<cookies.length;i++)
//			{
//				System.out.println(cookies[i].getName()+"==="+cookies[i].getValue());
//			}
//		}
		if (codevalue.equals(code))
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}
		else
		{
			error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);
		}
		return error;
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
	}
	
	@HandlerMethod(required = false, methodName = ServletConst.VALIDATE_EXTRA_USER)
	public static String validateExtraUser(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		UserService userService = getUserService(); 
		Users user = userService.getUser((String)param.get("name"));
		if (user.getSpaceUID().equals(param.get("key"))) {
			user.setValidate((short) 1);
			userService.addOrUpdateUser(Long.valueOf(1), user, null, null, Long.valueOf(11), null);
			// System.out.println("验证邮箱成功");
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		} else {
			error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null);
		}
		return error;
	}
	
	/**
	 * 增加或修改用户的公司角色
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_UPDATE_USER_COMPANY_ROLE)
	public static String addOrUpdateCompanyRoles(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	
    	HashMap<String, String>  temp2 = (HashMap<String, String>)param.get("datas");
    	int size = temp2.size();
    	Long[] addUserIds = new Long[size];
    	Long[] roleIds = new Long[size];
    	int index = 0;
    	String v;
    	for (String key : temp2.keySet())
    	{
    		v = temp2.get(key);	
    		addUserIds[index] = Long.valueOf(key);
    		roleIds[index] = v != null ? Long.valueOf(v) : null;
    		index++;
	    }
    		
    	UserService userService = getUserService(); 
    	userService.addOrUpdateCompanyRoles(companyId, addUserIds, roleIds);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	//与国泰同步用户
	/**
	 * 添加用户
	 *     默认部门管理员
	 *     mail：name+@qq.com
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_USER_ADD)
	public static String addSynchUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		//some code here
		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");
    	String orgName = (String)param.get("orgNames");
    	   	
    	String name = (String)param.get("name");
    	String pass = (String)param.get("pass");
    	
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	Organizations org=null;
    	if(null == company){//判断是否存在公司
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else if (userService.isCompanyUserFull(company.getId())){   // 允许的最大人数已经满了，不能在添加
			error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, null);
		}else if (null==(org=validateOrganizationsExist(company,orgName.split(";")))){   // 判断是否存部门
    		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    	}else if (userService.getUser(name) != null){  // 有同名存在
    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
    	}
    	else
    	{
	    	Users user = new Users(name, pass,name+"@qq.com", 0, name);
	    	user.setResetPass(pass);
    		if ("true".equals("true"))//部门管理和普通人员之间切换
	    	{
    			user.setRole((short)Constant.PART_ADMIN);
	    	}
	    	List<Long> addOrgIds = new ArrayList<Long>();
	    	addOrgIds.add(org.getId());
	    	userService.addOrUpdateUser(company.getId(), user, addOrgIds, null, null, null);
	    	UsersSynch synch= new UsersSynch();
	    	synch.setUserKey(pass);
	    	synch.setUsers(user);
	    	user.setSysnch(synch);
	    	userService.update(user);
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
    	}
    	return error;
	}
	
	
	/**
	 * 更新用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_USER_UPDATE)
	public static String updateSynchUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");
    	String orgName = (String)param.get("orgNames");
    	   	
    	String name = (String)param.get("name");
    	String pass = (String)param.get("newpass");
    	
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	Organizations org=null;
    	Users users=null;
    	if(null == company){//判断是否存在公司
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else if (userService.isCompanyUserFull(company.getId())){   // 允许的最大人数已经满了，不能在添加
			error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, null);
		}else if (null==(org=validateOrganizationsExist(company,orgName.split(";")))){   // 判断是否存部门
    		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    	}else if (null==(users=userService.getUser(name))){  // 用户不存在
    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
    	}
    	else
    	{
    		users.setResetPass(pass);
    		UsersSynch synch=users.getSysnch();
	    	synch.setUserKey(pass);
	    	userService.update(users);
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
    	}
    	return error;
	}
	
	/**
	 * 删除用户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_USER_DEL)
	public static String delSynchUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");
    	String orgName = (String)param.get("orgNames");
    	   	
    	String name = (String)param.get("name");
    	
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	Organizations org=null;
    	Users users=null;
    	if(null == company){//判断是否存在公司
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else if (userService.isCompanyUserFull(company.getId())){   // 允许的最大人数已经满了，不能在添加
			error = JSONTools.convertToJson(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, null);
		}else if (null==(org=validateOrganizationsExist(company,orgName.split(";")))){   // 判断是否存部门
    		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    	}else if (null==(users=userService.getUser(name))){  // 用户不存在
    		error = JSONTools.convertToJson(ErrorCons.USER_EXITSEUSERNAME_ERROR, null);
    	}
    	else
    	{
    		List<Long> userIds=new ArrayList<Long>();
    		userIds.add(users.getId());
    		userService.deleteUsers(userIds);
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);  
    	}
    	return error;
	}
	
	/**
	 * 添加部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_ORG_ADD)
	public static String addSynchOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		//公司名称
    	String comName = (String)param.get("comName");
    	//公司组织名称用.分割
    	String orgNames= (String)param.get("orgNames");
    	
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    	   	error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else{
    		String orgs[]=orgNames.split(";");
    		Organizations parent=null;
    		//部门组织分割数大于2
    		if(orgs.length>=2){
    			//父部门中含有一个不存在
    			parent=validateOrganizationsExist(company,(String[])ArrayUtils.subarray(orgs, 0, orgs.length-1));
    		  	if(null == parent){
    		  		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    		  		return error;
    		  	}
    		}
    		 if(null !=getOrganizations(company,parent,orgs[orgs.length-1])){
    			 error=JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_EXIST, null);
    		 }else{
    			 Organizations org=new Organizations();
     			org.setName(orgs[orgs.length-1]);
     			org.setParent(parent);
     			userService.addOrganization(company,(parent==null)?null:parent.getId(), org, null, null);
     			error=JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    		 }
    	}
    	return error;
	}
	
	/**
	 * 判断部门列表是否全部存在
	 *     如果有一个不存在，返回一个null，
	 *     如果都存在，返回最后近的父节点
	 * @param company
	 *          公司对象
	 * @param orgs
	 *          部门名称列表
	 *              树状父子结构
	 * @return
	 *     true/false
	 */
	private static Organizations validateOrganizationsExist(Company company,String orgs[]){
		Organizations current=null;
		for(int i=0;i<orgs.length;i++){
			current=getOrganizations(company, current, orgs[i]);
			if(null == current){
				return null;
			}
		}
		return current;
	}
	
	/**
	 *    通过公司、父部门和部门名称获取对应的部门
	 * @param company 
	 *           公司
	 * @param parent
	 *           父部门
	 * @param orgName
	 *           部门名称
	 * @return
	 *     null/部门
	 */
	private static Organizations getOrganizations(Company company,Organizations parent,String orgName){
		return ((UserService)ApplicationContext.getInstance().getBean(UserService.NAME)).getOrganizations(company, parent, orgName);
	}
	
	
	/**
	 * 更新部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_ORG_UPDATE)
	public static String updateSynchOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		//公司名称
    	String comName = (String)param.get("comName");
    	//公司组织名称用.分割
    	String orgNames= (String)param.get("orgNames");
    	String orgNewName=(String)param.get("orgNewName");
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    	   	error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else{
    		String[] orgs=orgNames.split(";");
    		Organizations parent=null;
    		//部门组织分割数大于2
    		if(orgs.length>=2){
    			//父部门中含有一个不存在
    			parent=validateOrganizationsExist(company,(String[])ArrayUtils.subarray(orgs, 0, orgs.length-1));
    		  	if(null == parent){
    		  		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    		  		return error;
    		  	}
    		}
    			Organizations org=getOrganizations(company,parent,orgs[orgs.length-1]);
    			if(null !=org){
    				org.setName(orgNewName);
        			userService.update(org);
        			error=JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    			}else{
    				error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    			}
    		
    	}
    	return error;
	}
	
	/**
	 * 删除部门
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_ORG_DEL)
	public static String delSynchOrg(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		//公司名称
    	String comName = (String)param.get("comName");
    	//公司组织名称用.分割
    	String orgNames= (String)param.get("orgNames");
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    	   	error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else{
    		String[] orgs=orgNames.split(";");
    		Organizations parent=null;
    		//部门组织分割数大于2
    		if(orgs.length>=2){
    			//父部门中含有一个不存在
    			parent=validateOrganizationsExist(company,(String[])ArrayUtils.subarray(orgs, 0, orgs.length-1));
    		  	if(null == parent){
    		  		error = JSONTools.convertToJson(ErrorCons.USER_ORG_NAME_NOT_EXIST, null);
    		  		return error;
    		  	}
    		}
			Organizations org=getOrganizations(company,parent,orgs[orgs.length-1]);
			if(null !=org){
				List<Long> orgIds=new ArrayList<Long>();
				orgIds.add(org.getId());
				userService.deleteOrganizations(orgIds);
    			error=JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		    }else{
		    	error=JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		    }
    	}
    	return error;
	}
	
	
	/**
	 * 添加公司
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_COM_ADD)
	public static String addSynchCom(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		//some code here
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");//公司名称
    	String adminName= (String)param.get("adminName");//管理员账号
    	String adminPass= (String)param.get("adminPass");//管理员密码
    	String code=(String)param.get("code");//公司code
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    		company = new Company();
    		company.setName(comName);
    	   	company.setCode(code);
    	   	company.setMaxUsers(100);
    	   	//十年之内有效
    	   	company.setUd(new Date().getTime()+60*1000*60*24*365*10);
    	   	Users admin = new Users(adminName, adminPass, "", (short)Constant.COMPANY_ADMIN, comName + "管理员");
    	   	UsersSynch synch=new UsersSynch();
    	   	synch.setUserKey(adminPass);
    	   	synch.setUsers(admin);
    	   	admin.setSysnch(synch);
    	   	int ret = userService.addCompany(company, admin);    		    		
    	   	error = JSONTools.convertToJson(ret, null);
    	}else{
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_EXIST, null);
    	}
    	return error;
	}
	
	/**
	 * 更新公司名称
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_COM_UPDATE)
	public static String updateSynchCom(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		//some code here
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");//公司名称
    	String newComName=(String)param.get("newComName");//公司新名称
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else{
    		if(null ==userService.getCompanyByName(newComName)){
    			company.setName(newComName);
    			userService.update(company);
        		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    		}else{
    			error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_EXIST, null);
    		}
    		
    	}
    	return error;
	}
	
	/**
	 * 删除公司
	 *     根据公司名称删除公司
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.SYNCH_COM_DEL)
	public static String delSynchCom(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException{
		//some code here
		String error = "";
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String comName = (String)param.get("comName");//公司名称
    	UserService userService = getUserService();
    	Company company = userService.getCompanyByName(comName);
    	if(null == company){//判断是否存在公司
    		error = JSONTools.convertToJson(ErrorCons.USER_COMPANY_NAME_NOT_EXIST, null);
    	}else{
    		//设置公司过期
    		company.setUd(new Date().getTime());
    		userService.update(company);
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
    	}
    	return error;
	}
	
	/**
	 * 向客户端返回数据
	 *  
	 * @param resp
	 * @param data
	 *        返回数据
	 * @throws IOException 
	 */
	/*private static void responseData(HttpServletResponse resp,String data) throws IOException{
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
 	    resp.getWriter().write(data);
	}*/

	//以下是移动端消息处理方法
	@HandlerMethod(methodName = ServletConst.SEND_MESSAGE)
	public static String sendMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {//移动端发送消息
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String type = (String)param.get("type"); // 类型："group"或"person"
    	String msg = (String)param.get("msgCt"); // 消息内容
    	String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long sendId = us.getId();
    	Long acceptId = Long.parseLong(String.valueOf(param.get("acceptId"))); // 接受者ID
    	String date = (String)param.get("date"); // 发送日期
    	ITalkService talk = getTalkService();
    	if(type.equals("group")) {
    	    Long groupId = (Long)param.get("groupId"); // 讨论组ID
    		talk.sendGroupSessionMeg(msg, us, groupId, 0, date);
    	} else if(type.equals("person")) {
    		talk.sendSessionMeg(msg, us, acceptId, 0, date);
    	}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	@HandlerMethod(methodName = ServletConst.RECEIVE_MESSAGE)
	public static String receiveMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {//移动端接受消息
    	//String error;  	
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long userId = us.getId();
    	Map<String, Object> result = getTalkService().getAllNewMegTip(userId, true);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
       
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	// 移动端获取组织和人员
	@HandlerMethod(methodName = ServletConst.GET_G_AND_M)
	public static String getTalkGroupsAndMember(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long userId = us.getId();
    	// 根parentId为"g-0",部门ID为"g-部门ID",联系人parentId为"gm-用户ID"
    	String parentId = (String)param.get("parentId");
    	List<Map<String, Object>> nodeMap = getTalkService().getCompanyTree(us, parentId, true, "gm");  	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, nodeMap); 
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	// 移动端获取组织和人员
	@HandlerMethod(methodName = ServletConst.GET_CTM_G_AND_M)
	public static String getCtmGAndM(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long userId = us.getId();
    	// 根parentId为"c-0",分组节点parentId为"ctmg-1",1为分组ID
    	String parentId = (String)param.get("parentId");
    	List<Map<String, Object>> nodeMap = getTalkService().getCtmGroupService().getCtmGOrMNode(us.getId(), parentId, "ctmgm");  	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, nodeMap); 
    }

	@HandlerMethod(methodName = ServletConst.GET_DISCU_LIST)
	public static String getDiscuList(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException { 
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long userId = us.getId();
    	List<Map<String, Object>> nodeMap = getTalkService().getDiscuGroupService().getDiscuGroupNodeList(us);  	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, nodeMap); 
	}
	
	@HandlerMethod(methodName = ServletConst.GET_DISCU_Member_LIST)
	public static String getDiscuMemberList(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException { 
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	Long groupId = (Long)param.get("groupId");
    	List<Map<String, Object>> nodeMap = getTalkService().getDiscuGroupService().getDiscuGMNodeList(groupId);  	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, nodeMap);
	}
	
	@HandlerMethod(methodName = ServletConst.VALIDATEUSEREXISTS,required=false)
	public static String validateUserExists(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account=(String) param.get("account");
    	String validateCode =(String) param.get("validateCode");
    	String codetime =(String) param.get("codetime");
    	
    	//无效的验证码
    	if(validateCode== null || validateCode.trim().equals("")){
    		return JSONTools.convertToJson(ErrorCons.USER_VALIDATECODE_NOT_MATCH, null); 
    	}
    	
//    	String code=(String) req.getSession().getAttribute("validateCode");
    	String backcode=(String)WebConfig.mobileregistmap.get(validateCode+","+codetime);
    	
    	if (backcode==null)
    	{
    		backcode=(String) req.getSession().getAttribute("validateCode");
    	}
    	else
    	{
    		validateCode+=","+codetime;
    	}
    	//验证码错误
    	if(backcode == null || backcode.equals("")|| !backcode.trim().toLowerCase().equals(validateCode.trim().toLowerCase())){
    		return JSONTools.convertToJson(ErrorCons.USER_VALIDATECODE_NOT_MATCH, null); 
    	}
    	
    	Users us = getCurrentUser(account);

    	//账号不存在
    	if(null == us){
    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
    	}
    	
    	String mobileNos[]=org.apache.commons.lang.StringUtils.split(us.getMobile()," ");
    	
    	String es[]=org.apache.commons.lang.StringUtils.split(us.getRealEmail()," ");
    	
    	List<String> data=new ArrayList<String>();
    	if(null !=mobileNos &&mobileNos.length>0){
    		data.add(mobileNos[0]);
    	}else{
    		data.add("");
    	}
    	if(null !=es &&es.length>0){
    		data.add(es[0]);
    	}else{
    		data.add("");
    	}
    	
//    	req.getSession().setAttribute("accountValidate", true);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, data); 
	}
	/**
	 * 忘记密码通过手机找回
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.FORGETMOBILEPASSWORD,required=false)
	public static String forgetMobilePassword(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account=(String) param.get("account");
		String mobile=(String) param.get("mobile");
		String mobileCode=(String) param.get("mobileCode");
//		String code=(String) req.getSession().getAttribute("mobilecode");
		String mobileregist=(String)WebConfig.mobileregistmap.get("forget"+mobile);//用手机号做key验证码,次数,时间//每天夜里0时清空
		int getnums=0;//获取次数
		Long getcodedate=null;//获取时间
		String[] registvalues;
		String code=null;
		if (mobileregist!=null)
		{
			registvalues=mobileregist.split(",");
			code=registvalues[0];//验证码
			getnums=Integer.parseInt(registvalues[1]);//点击次数
			getcodedate=Long.valueOf(registvalues[2]);//获取时间
			int changpswnums=0;
			if (registvalues.length>=4)//第四位放更改密码次数
			{
				changpswnums=Integer.parseInt(registvalues[3]);
			}
			changpswnums++;
			WebConfig.mobileregistmap.put("change"+mobile,System.currentTimeMillis()+","+changpswnums);
		}
		//判断
		if( null ==code ||mobileCode ==null || mobileCode.toString().equals("")|| !code.toLowerCase().equals(mobileCode.toLowerCase())){
			return JSONTools.convertToJson(ErrorCons.USER_MOBILECODE_ERROR, null); 
		}
		
		Users us = getCurrentUser(account);
    	
    	//账号不存在
    	if(null == us){
    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
    	}
    	
    	String userMobile=us.getMobile();
    	//账号 手机不一致
    	if(userMobile == null || userMobile.toString().equals("")){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	String mobileNos[]=org.apache.commons.lang.StringUtils.split(userMobile," ");
    	boolean match=false;
    	for(String no:mobileNos){
    		if(no.trim().equals(mobile)){
    			match=true;
    		}
    	}
    	//手机 账号 不一致
    	if(!match){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	
    	//以下欠考虑，如果手机端操作就有问题了，不能用session处理——孙爱华
    	//主要检测用户是否通过第一步的验证
//    	Boolean accountValidate =(Boolean) req.getSession().getAttribute("accountValidate");
//    	if(accountValidate == null|| !accountValidate){
//    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
//    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	/**
	 * 手机注册，通过邮箱找回
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception 
	 */
	@HandlerMethod(methodName = ServletConst.FORGETEMAILPASSWORD,required=false)
	public static String forgetEmailPassword(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws Exception {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account=(String) param.get("account");
		String email=(String) param.get("email");
		int pc=1;
		Users us = getCurrentUser(account);
    	
    	//账号不存在
    	if(null == us){
    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
    	}
    	
    	String emails=us.getRealEmail();
    	//账号 邮箱不一致
    	if(emails == null || emails.toString().equals("")){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	String es[]=org.apache.commons.lang.StringUtils.split(emails," ");
    	boolean match=false;
    	for(String e:es){
    		if(e.trim().equals(email)){
    			match=true;
    		}
    	}
    	//账号邮箱 不一致
    	if(!match){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	
    	String httpUrl = req.getScheme() + "://" + req.getServerName() +":"+req.getServerPort()+"" + req.getContextPath() ;
    	System.out.println("如果有反向代理需要这里替换一下======================================="+httpUrl);
    	httpUrl=QueryDb.getIpName(httpUrl);
    	String code =new DES().encrypt(""+System.currentTimeMillis());
    	
    	MailSender.sendRepasswordMail(account,us.getRealName(),email, httpUrl,code);
    	
    	Map<String,Object> userData=new HashMap<String,Object>();
    	userData.put("emailValidate", true);
    	userData.put("code", code);
    	userData.put("mail", email);
    	req.getSession().getServletContext().setAttribute(account, userData);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception 
	 */
	@HandlerMethod(methodName = ServletConst.MAILREPASSWORDCHECK,required=false)
	public static String mailRepasswordCheck(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws Exception {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		DES des=new DES();
		String account=des.decrypt((String) param.get("account"));
		String code=(String) param.get("code");
		code=des.decrypt(code);
		Map<String,Object> userData=(Map<String, Object>) req.getSession().getServletContext().getAttribute(account);
		if(userData == null){
			resp.sendRedirect("/static/user/mailRePassError.html");
		}
		
		if(!(Boolean)userData.get("emailValidate")){
			resp.sendRedirect("/static/user/mailRePassError.html");
		}

    	Users us = getCurrentUser(account);
    	//账号不存在
    	if(null == us){
    		resp.sendRedirect("/static/user/mailRePassError.html");
    	}
    	
    	if(!code.equals(des.decrypt((String)userData.get("code")))){
    		resp.sendRedirect("/static/user/mailRePassError.html");
		}
    	
    	String emails=us.getRealEmail();
    	//账号 邮箱不一致
    	if(emails == null || emails.toString().equals("")){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	
    	String es[]=org.apache.commons.lang.StringUtils.split(emails," ");
    	boolean match=false;
    	
    	for(String e:es){
    		if(e.trim().equals(userData.get("mail"))){
    			match=true;
    		}
    	}
    	
    	//账号邮箱 不一致
    	if(!match){
    		return JSONTools.convertToJson(ErrorCons.USER_MOBILE_NOT_MATCH, null); 
    	}
    	
    	//判断code是否有效
    	if(code == null || code.trim().equals("")){
    		resp.sendRedirect("/static/user/mailRePassError.html");
    	}
		    		    	
		userData.put("emailValidate", false);
		userData.put("emailCheck", true);
		//判断code是否过期
    	if(new Date().after(new Date(Long.parseLong(code)+1000*60*60*24*5))){
    		resp.sendRedirect("/static/user/mailRePassError.html");
    	}else{
    		resp.sendRedirect("/static/user/mailRePass.html?emailAccount="+account);
    	}
		return null;
	}
	
	/**
	 *   邮箱更新账号
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.MAILREPASSWORD,required=false)
	public static String mailRepassword(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account=(String) param.get("account");
		String newpass=(String) param.get("newpass");
		
		Map<String,Object> userData=(Map<String, Object>) req.getSession().getServletContext().getAttribute(account);
		if(userData == null){
			return JSONTools.convertToJson(ErrorCons.USER_MAIL_VALIDATECODE_NOT_ERROR, null); 
		}
		
		if(!(Boolean)userData.get("emailCheck")){
			return JSONTools.convertToJson(ErrorCons.USER_MAIL_VALIDATECODE_NOT_ERROR, null); 
		}

		Users us = getCurrentUser(account);
	    	
    	//账号不存在
    	if(null == us){
    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
    	}
         
    	us.setPassW(new MD5().getMD5ofStr(newpass));
    	us.setPwdupdatetime(new Date());
    	userService.update(us);
    	
    	req.getSession().getServletContext().removeAttribute(account);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	
	/**
	 *  手机更新账号
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.MOBILECHANGEPASS,required=false)
	public static String mobileChangePass(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account=(String) param.get("account");
		String newpass=(String) param.get("newpass");
    	Users us = getCurrentUser(account);
    	if (us!=null) {
			String forgetstr=(String)WebConfig.mobileregistmap.get("change"+us.getMobile());//这里很关键，没有这个会跳墙过来更改密码，不能用session
			
			if (forgetstr!=null && forgetstr.length()>0) {
				String[] strs=forgetstr.split(",");
				if (strs[1]!=null && Integer.parseInt(strs[1])>0 && Integer.parseInt(strs[1])<4)//每天只能更新3次——孙爱华
				{
			    	//账号不存在
			    	if(null == us) {
			    		return JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR, null); 
			    	}
			    	us.setPassW(new MD5().getMD5ofStr(newpass));
			    	us.setPwdupdatetime(new Date());
			    	userService.update(us);
			    	WebConfig.mobileregistmap.remove("change"+account);
			    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
				}else{
					return JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null); 
				}
			}
    	}
    	return JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null); 
	}
	
	/**
	 *  获取验证码
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.MOBILECALIDATECODE,required=false)
	public static String mobileValidateCode(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String code =VerifyCodeUtils.getValidateCode(4);
		System.out.println(code);
		String backcode=code+","+System.currentTimeMillis();
		WebConfig.mobileregistmap.put(backcode,backcode);
		req.getSession().setAttribute("validateCode",backcode);
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, backcode); 
	}
	
	/**
	 *  更新用户个人信息置为已读
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATESESSIONMESSAGEREAD)
	public static String updateSessionMessgaeRead(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String messges =(String) param.get("messageIds");
		if(messges == null ||messges.equals("")){
			return JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null); 
		}
		
		List<Long> mids=new ArrayList<Long>();
		String msgId[]=messges.split(";");
		for(String id:msgId){
			if(org.apache.commons.lang.StringUtils.isNumeric(id)){
				mids.add(Long.parseLong(id));
			}
		}
		getTalkService().updateSessionMessageRead(mids);
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	
	
	/**
	 *  更新用户组信息置为已读
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPDATEGROUPSESSIONMESSAGEREAD)
	public static String updateGroupSessionMessgaeRead(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String messges =(String) param.get("messageIds");
		Integer groupId =(Integer) param.get("groupId");
		Integer userId  =(Integer) param.get("userId");
		
		if(messges == null ||messges.equals("") ||groupId == null ||groupId.equals("") ||userId == null ||userId.equals("") ){
			return JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null); 
		}
		
		List<Long> mids=new ArrayList<Long>();
		String msgId[]=messges.split(";");
		for(String id:msgId){
			if(org.apache.commons.lang.StringUtils.isNumeric(id)){
				mids.add(Long.parseLong(id));
			}
		}
		getTalkService().updateGroupSessionMessageRead(groupId.longValue(), userId.longValue(), mids);
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}

	@HandlerMethod(methodName = ServletConst.GETPERSONMESSAGE)
	public static String getPersonMeg(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		Long senderId = Long.parseLong(String.valueOf(param.get("senderId")));
		String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	Long userId = us.getId();
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, getTalkService().getPersonNewMeg(senderId, userId, true)); 
	}
	
	@HandlerMethod(methodName = ServletConst.GETGROUPMESSAGE)
	public static String getGroupMeg(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		Long groupId = Long.parseLong(String.valueOf(param.get("groupId")));
		String account = (String)param.get("account");   // 
		Users us = getCurrentUser(account);
    	Long userId = us.getId();
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, getTalkService().getGroupAcceptMeg(groupId, userId, true)); 
	}
	
	@HandlerMethod(methodName = ServletConst.SEARCH_ROSTERS)
	public static String searchRostersByKey(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	String keyword = (String)param.get("keyword");
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, getTalkService().searchRostersByKey(keyword, us.getId())); 
	}
	
	@HandlerMethod(methodName = ServletConst.SEND_VALIDATE)
	public static String sendValidateSessionMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	// 1加组  2加人
    	int category = (Integer) param.get("category");
		// 要加入的联系人分组的ID
		Long groupId = Long.parseLong(String.valueOf(param.get("groupId")));
		// 请求加为好友的用户的ID
		Long acceptId = Long.parseLong(String.valueOf(param.get("acceptId")));
		// 验证消息
		String validateSessionMeg = (String)param.get("validateSessionMeg");
		// return -1: 当前session过期 0： 发送成功 1： 联系人已经存在
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, getTalkService().sendValidateSessionMessage(category, us,
				acceptId, groupId, validateSessionMeg)); 
	}

	@HandlerMethod(methodName = ServletConst.PRO_VALIDATE)
	public static String proValidateSessionMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");   // 
    	Users us = getCurrentUser(account);
    	// 1加组  2加人
    	int category = (Integer) param.get("category");
		// 同意后的添加的小组ID
		Long groupId = Long.parseLong(String.valueOf(param.get("groupId")));
		// 要验证的消息ID,即用户接受到的信息ID
		Long vMsgId = Long.parseLong(String.valueOf(param.get("vMsgId")));
		// type 1:同意 2：拒绝
		int type = (Integer) param.get("type");
		// 拒绝原因
		String validateSessionMeg = (String)param.get("validateSessionMeg");
		// return -1: 当前session过期 0： 发送成功 1： 联系人已经存在
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, getTalkService().proValidateSessionMessage(us.getId(), vMsgId,
				type, validateSessionMeg, groupId));
	}
	
	@HandlerMethod(methodName = ServletConst.END_VALIDATE)
	public static String endValidateMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");   // 
		// 要验证的消息ID,即用户接受到的信息ID
		Long vMsgId = Long.parseLong(String.valueOf(param.get("vMsgId")));
		getTalkService().endValidateMessage(vMsgId);
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	
	private static ITalkService getTalkService(){
		if(null == talk){
			talk = (ITalkService) ApplicationContext.getInstance().getBean(TalkService.NAME);
		}
		return talk;
	}
	
	private static UserService getUserService() {
		if(null == userService){
			userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		}
		return userService;
	}
	
	private static Users getCurrentUser(String account) {
		return getUserService().getUser(account);
	}
	
	@HandlerMethod(required = false, methodName = ServletConst.SEND_NOTE)
	public static String sendNote(HttpServletRequest req, HttpServletResponse resp, HashMap<String,Object> jsonParams) throws IOException{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String mobileStr = (String)param.get("address");
		String[] mobiles = mobileStr.split(";");
		Long[] outid = new Long[mobiles.length];
		String account = (String)param.get("account");
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		Users sender = userService.getUser(account);
		FileSystemService filesystemservice = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		String content = (String)param.get("content");
		String backmobile=(String)param.get("backmobile");//返回的手机号
		for(String mobile : mobiles){
			MessageInfo messageinfo = new MessageInfo();
			messageinfo.setContent(content);
			messageinfo.setSendUsers(sender);
			messageinfo.setReceiver(mobile);
			messageinfo.setDate(new Date());
			filesystemservice.saveMessageInfo(messageinfo);
			//messageinfo.set
		}
		//业务编号暂为0
		for(int i = 0; i < mobiles.length; i++){
			outid[i] = 0L;
		}
		Integer type = (Integer)param.get("type");
		if (type==null)
		{
			type=Constant.SENDMSG;
		}
		Thread receiveT = new Thread(new BackgroundSend(mobiles,content,sender.getCompany().getId(),sender.getCompany().getName(),type,outid,false,sender));
		receiveT.start();
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	/**
	 * 获取接待管理默认用户----孙爱华
	 */
	@HandlerMethod(required = false, methodName = "getDefaultInfo")
	public static String getDefaultInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws ServletException, IOException
	{
		String error=null;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			
			Number temp = (Number) param.get("receptionid"); // 选择的接待信息
			long id=0;
			if (temp!=null)
			{
				id=temp.longValue();
			}
			String account = (String) param.get("account"); // 登录的账户

			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			Users user=jqlService.getUsers(account);
			HashMap<String, Object> backshare = new HashMap<String, Object>();
			List backlist = new ArrayList<Object[]>();//获取已设置的用户及权限数组0-ID字符，1-接受的用户编号 字符，
			//2-接受的用户名称字符，3-接受用户的部门名称 字符，4-具体权限数据  整型
			if (id==0)
			{
				List<ReceptionDefaultUsers> defaultlist=(List<ReceptionDefaultUsers>)jqlService.findAllBySql("select a from ReceptionDefaultUsers as a where a.owner.id=?", user.getId());
				if (defaultlist!=null && defaultlist.size()>0)
				{
					for (int i=0;i<defaultlist.size();i++)
					{
						ReceptionDefaultUsers defaultusers=defaultlist.get(i);
						HashMap<String, Object> values = new HashMap<String, Object>();//前台只要4个
						values.put("id", defaultusers.getId());
						values.put("userid", defaultusers.getDefaultuser().getId());
						values.put("username", defaultusers.getDefaultuser().getRealName());
						values.put("permission", defaultusers.getDefaultpowerid());
						backlist.add(values);
					}
				}
			}
			else
			{
				List<ReceptionUsers> userlist=(List<ReceptionUsers>)jqlService.findAllBySql("select a from ReceptionUsers as a where a.reception.id=? ", id);
				if (userlist!=null && userlist.size()>0)
				{
					for (int i=0;i<userlist.size();i++)
					{
						ReceptionUsers receptionUsers=userlist.get(i);
						HashMap<String, Object> values = new HashMap<String, Object>();//前台只要4个
						values.put("id", receptionUsers.getId());
						values.put("userid", receptionUsers.getUser().getId());//有权限的用户ID
						values.put("username", receptionUsers.getUser().getRealName());//有权限的用户名称
						values.put("permission", receptionUsers.getPowerid());//对应的权限
						backlist.add(values);
					}
				}
			}
			backshare.put("defaultpermision", backlist);// 已经设置的用户权限
			
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, backshare);
			

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return error;
	}
	
	/**
	 * 提交接待管理的默认设置
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = "receptionDefault")
	public static String receptionDefault(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws ServletException, IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String account = (String) param.get("account"); // 登录的账户
		Users user=jqlService.getUsers(account);
		Number temp = (Number) param.get("receptionid"); // 选择的具体接待信息
		long id=0;
		if (temp!=null)
		{
			id=temp.longValue();
		}
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		
		Boolean isreset = Boolean.valueOf((String)param.get("isreset"));//是否重置用户权限
		Boolean isMobile = Boolean.valueOf((String)param.get("isMobile"));//是否手机短信提示
		List<Long> defaultuserslist=new ArrayList<Long>();//默认的用户列表

		ArrayList<String> delIdList = (ArrayList<String>) param.get("info_del");//删除的权限用户
		List addIdList = (List) param.get("info_add");//增加的权限用户
		List modifyList = (List) param.get("info_modify");//修改的权限用户
		List<ReceptionDefaultUsers> totaluser=new ArrayList<ReceptionDefaultUsers>();//所有默认的用户
		FileSystemService fileService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		if (modifyList != null)
		{
			try
			{
				if (id>0)
				{
					for (int i = 0; i < modifyList.size(); i++)
					{
						String shareinfo = (String) modifyList.get(i);
						String[] infos = shareinfo.split("-");
						Long userid = Long.parseLong(infos[1]);
						int permit = Integer.parseInt(infos[2]);
						Long defaultid = Long.parseLong(infos[0]);
						
						ReceptionUsers receptionUsers=(ReceptionUsers)jqlService.getEntity(ReceptionUsers.class, defaultid);
						receptionUsers.setUser(jqlService.getUsers(userid));
						receptionUsers.setPowerid(permit);
						jqlService.update(receptionUsers);
					}
				}
				else
				{
					for (int i = 0; i < modifyList.size(); i++)
					{
						String shareinfo = (String) modifyList.get(i);
						String[] infos = shareinfo.split("-");
						Long userid = Long.parseLong(infos[1]);
						int permit = Integer.parseInt(infos[2]);
						Long defaultid = Long.parseLong(infos[0]);
						
						ReceptionDefaultUsers defaultuser=(ReceptionDefaultUsers)jqlService.getEntity(ReceptionDefaultUsers.class, defaultid);
						defaultuser.setDefaultuser(jqlService.getUsers(userid));
						defaultuser.setDefaultpowerid(permit);
						defaultuser.setIsmobileinfo(isMobile);
						jqlService.update(defaultuser);
						totaluser.add(defaultuser);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (addIdList != null)
		{
			if (id>0)
			{
				Reception reception=(Reception)jqlService.getEntity(Reception.class, id);
				for (int i = 0; i < addIdList.size(); i++)
				{
					String shareinfo = (String) addIdList.get(i);
					String[] infos = shareinfo.split("-");
					Long userid = Long.valueOf(infos[0]);
					int permit = Integer.parseInt(infos[1]);
					ReceptionUsers receptionUsers=new ReceptionUsers();
					receptionUsers.setUser(jqlService.getUsers(userid));
					receptionUsers.setReception(reception);
					receptionUsers.setPowerid(permit);
					jqlService.save(receptionUsers);
				}
			}
			else
			{
				for (int i = 0; i < addIdList.size(); i++)
				{
					String shareinfo = (String) addIdList.get(i);
					String[] infos = shareinfo.split("-");
					Long userid = Long.valueOf(infos[0]);
					int permit = Integer.parseInt(infos[1]);
					ReceptionDefaultUsers defaultuser=new ReceptionDefaultUsers();
					defaultuser.setDefaultuser(jqlService.getUsers(userid));
					defaultuser.setOwner(user);
					defaultuser.setDefaultpowerid(permit);
					defaultuser.setIsmobileinfo(isMobile);
					jqlService.save(defaultuser);
					totaluser.add(defaultuser);
				}
			}
		}
		if (delIdList!=null)
		{
			if (id>0)
			{
				for (int i=0;i<delIdList.size();i++)
				{
					ReceptionUsers receptionUsers=(ReceptionUsers)jqlService.getEntity(ReceptionUsers.class, Long.valueOf(delIdList.get(i)));
					jqlService.deleteEntityByID(ReceptionUsers.class, "id", Long.valueOf(delIdList.get(i)));
				}
			}
			else
			{
				for (int i=0;i<delIdList.size();i++)
				{
					ReceptionDefaultUsers defaultuser=(ReceptionDefaultUsers)jqlService.getEntity(ReceptionDefaultUsers.class, Long.valueOf(delIdList.get(i)));
					if (isreset!=null && isreset.booleanValue()==true)
					{
						jqlService.excute("delete from ReceptionUsers as a where a.rpuserid=? and a.ownerid=?",defaultuser.getDefaultuser().getId(),user.getId());
					}
					jqlService.deleteEntityByID(ReceptionDefaultUsers.class, "id", Long.valueOf(delIdList.get(i)));
				}
			}
		}
		if (isreset!=null && isreset.booleanValue()==true)
		{
			if (id>0)
			{
				//不处理
			}
			else
			{
				List<Reception> list=(List<Reception>)jqlService.findAllBySql("select a from Reception as a where a.userid=? and a.deleted=? and isdisplay=?", user.getId(),false,false);
				if (list!=null)
				{
					for (int i=0;i<totaluser.size();i++)//重置所有的接待信息权限
					{
						ReceptionDefaultUsers defaultuser=totaluser.get(i);
						for (int j=0;j<list.size();j++)
						{
							List<ReceptionUsers> powerlist=(List<ReceptionUsers>)jqlService.findAllBySql("select a from ReceptionUsers as a where a.reception.id=? and a.user.id=?", list.get(j).getReceptionid(),defaultuser.getDefaultuser().getId());
							if (powerlist!=null && powerlist.size()>0)//已存在，直接更新权限
							{
								ReceptionUsers power=powerlist.get(0);
								power.setPowerid(defaultuser.getDefaultpowerid());
								jqlService.update(power);
							}
							else
							{
								ReceptionUsers power=new ReceptionUsers();
								power.setUser(defaultuser.getDefaultuser());
								power.setReception(list.get(j));
								power.setPowerid(defaultuser.getDefaultpowerid());
								jqlService.save(power);
							}
						}
					}
				}
			}
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "设置成功");
		return error;
	}
	
	@HandlerMethod(required = false, methodName = ServletConst.ADDORUPDATEUSER)
	public static void addOrupdateUser(HttpServletRequest req, HttpServletResponse resp,
           HashMap<String, Object> jsonParams) throws ServletException, IOException
   {//增加或修改用户
	   String error;
	   HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	   String account = (String)jsonParams.get("account");
	   if (WebConfig.outsameusertag.equals(account))//只有账号一致才让同步
	   {
		   String outcode = (String)param.get("outcode");//外部用户编号
		   String outname = (String)param.get("outname");//用户登录名
		   String outrealname = (String)param.get("outrealname");//外部用户名称
		   String outpassw = (String)param.get("outpassw");//外部同步过来的密码
		   String outemail = (String)param.get("outemail");//外部邮箱
		   String outrole = (String)param.get("outrole");//是否部门管理员
		   String outorgcode = (String)param.get("outorgcode");//所属组织编号;多个组织用;间隔
		   String outca = (String)param.get("outca");//ca号
		   String outsize = (String)param.get("outsize");//空间大小
		   String outsex = (String)param.get("outsex");//性别
		   String updatetime = (String)param.get("updatetime");//上次同步更新的时间
		   String optype = (String)param.get("optype");//删除用户标记
		   
		   try
		   {
			   UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			   if ("del".equals(optype))
			   {
				   Users user=userService.getUserByname(outname);
				   List<Long> userIds=new ArrayList<Long>();
				   userIds.add(user.getId());
				   userService.deleteUsers(userIds);
				   error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"删除成功");
			   }
			   else
			   {
			   		Long roleid=3L;
			   		Users user=userService.getUserByname(outname);
			   		if (user==null)
			   		{
			   			user=new Users();
			   			user.setCompanyId("public");
	                    user.setUserName(outname);
	                    user.setRealEmail(outemail); 
	                    user.setPassW(outpassw);
	                    user.setResetPass(outpassw);
	                    user.setRealName(outrealname);
	                    user.setRole((short)0);
	                    user.setStorageSize(Float.valueOf(outsize));
	                    if ("1".equals(outsex))
	                    {
	                    	user.setMan(true);
	                    }
	                    else
	                    {
	                    	user.setMan(false);
	                    }
	                    if (outrole!=null && "1".equals(outrole))
	                    {
	                    	roleid=2L;
	                        user.setRole(Short.valueOf("4"));
	                        user.setPartadmin(0);//设置部门管理员
	                    }
	                    if (outca!=null && outca.trim().length()>0)
	                    {
	                    	user.setCaId(outca);
	//                        	user.setLoginCA("1");
	                    }
	                    user.setOutcode(outcode);//外部用户编号
	                    user.setOutname(outname);//用户登录名
	                    user.setOutrealname(outrealname);//外部用户名称
	                    user.setOutpassW(outpassw);//外部同步过来的密码
	                    user.setOutemail(outemail);//外部邮箱
	                    user.setOutrole(outrole);//是否部门管理员
	                    user.setOutorgcode(outorgcode);//所属组织编号;多个组织用;间隔
	                    user.setOutca(outca);//ca号
	                    user.setOutsize(outsize);//空间大小
	                    user.setOutsex(outsex);//性别
	                    user.setUpdatetime(updatetime);//上次同步更新的时间
	                    List<Long> addOrglist=null;
	                    if (outorgcode!=null && outorgcode.trim().length()>0)
	                    {
	                    	String[] orgs=outorgcode.split(";");
	                    	if (orgs.length>0)
	                    	{
	                    		//删除用户所属的组织
	                    		addOrglist=new ArrayList<Long>();
	                    		for (int i=0;i<orgs.length;i++)
	                    		{
	                    			Organizations organizations=userService.getOrganizationsBydepid(orgs[i]);
	                    			if (organizations!=null)
	                    			{
	                    				addOrglist.add(organizations.getId());
	                    			}
	                    		}
	                    	}
	                    }
	                    
	                    user=userService.addOrUpdateUser(user, addOrglist, null, roleid, roleid);
			   			error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"同步成功");
			   		}
			   		else//编辑用户信息
			   		{
			   			//暂时没有合并，以后有需要再合并这些重复代码
			   			user.setCompanyId("public");
	                    user.setUserName(outname);
	                    user.setRealEmail(outemail); 
	                    user.setPassW(outpassw);
	                    user.setResetPass(outpassw);
	                    user.setRealName(outrealname);
	                    user.setRole((short)0);
	                    user.setStorageSize(Float.valueOf(outsize));
	                    if ("1".equals(outsex))
	                    {
	                    	user.setMan(true);
	                    }
	                    else
	                    {
	                    	user.setMan(false);
	                    }
	                    if (outrole!=null && "1".equals(outrole))
	                    {
	                        user.setRole(Short.valueOf("4"));
	                        user.setPartadmin(0);//设置部门管理员
	                        roleid=2L;
	                    }
	                    if (outca!=null && outca.trim().length()>0)
	                    {
	                    	user.setCaId(outca);
	//                    	user.setLoginCA("1");
	                    }
	                    user.setOutcode(outcode);//外部用户编号
	                    user.setOutname(outname);//用户登录名
	                    user.setOutrealname(outrealname);//外部用户名称
	                    user.setOutpassW(outpassw);//外部同步过来的密码
	                    user.setOutemail(outemail);//外部邮箱
	                    user.setOutrole(outrole);//是否部门管理员
	                    user.setOutorgcode(outorgcode);//所属组织编号;多个组织用;间隔
	                    user.setOutca(outca);//ca号
	                    user.setOutsize(outsize);//空间大小
	                    user.setOutsex(outsex);//性别
	                    user.setUpdatetime(updatetime);//上次同步更新的时间
	                    List<Long> addOrglist=null;
	//                    List<Long> delOrglist=null;
	                    if (outorgcode!=null && outorgcode.trim().length()>0)
	                    {
	                    	String[] orgs=outorgcode.split(";");
	                    	if (orgs.length>0)
	                    	{
	                    		userService.delUsersOrganizations(user.getId());//删除用户所属的组织
	                    		addOrglist=new ArrayList<Long>();
	                    		for (int i=0;i<orgs.length;i++)
	                    		{
	                    			Organizations organizations=userService.getOrganizationsBydepid(orgs[i]);
	                    			if (organizations!=null)
	                    			{
	                    				addOrglist.add(organizations.getId());
	                    			}
	                    		}
	                    	}
	                    }
	                    Long oldroleid=3L;
	                    
	                    Roles roles=userService.getRoleByUser(user.getId());
	                    if (roles!=null)
	                    {
	                    	oldroleid=roles.getId();//获取旧角色
	                    }
	                    user=userService.addOrUpdateUser(user, addOrglist, null, roleid, oldroleid);
			   			error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"同步成功");
			   		}
			   }
		   }
		   catch (Exception e)
		   {
			   e.printStackTrace();
			   error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR,"同步失败");
		   }
	   }
	   else
	   {
		   error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, "账号不对");
	   }
	   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
       resp.getWriter().write(error);
   }
	@HandlerMethod(required = false, methodName = ServletConst.ADDORUPDATEORGS)
   public static void addOrupdateOrgs(HttpServletRequest req, HttpServletResponse resp,
           HashMap<String, Object> jsonParams) throws ServletException, IOException
   {//增加或修改部门信息
	   UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	   HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
	   String account = (String)jsonParams.get("account");
	   String error;
	   try
	   {
		   if (WebConfig.outsameusertag.equals(account))//只有账号一致才让同步
		   {
			   
			   String depid = (String)param.get("depid");//外部系统的部门编号
			   String parentid = (String)param.get("parentid");//外部系统的父部门编号
			   String depname = (String)param.get("depname");//外部系统的部门名称
			   String updatetime = (String)param.get("updatetime");//同步时间（long型的字符串）
			   String description = (String)param.get("description");//备注
			   String memberlist=(String)param.get("memberlist");//部门组员，以;间隔
			   String optype = (String)param.get("optype");//删除用户标记
			   Organizations org=null;
			   if (depid!=null && depid.length()>0)
			   {
				   org=userService.getOrgBydepid(depid);
			   }
			   //根据部门编号获取部门信息
			   if ("del".equals(optype))
			   {
				   if (org!=null)
				   {
					   userService.deleteGroup(org.getId());
				   }
				   error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"删除成功");
			   }
			   else
			   {
				   
				   Organizations parent=null;
				   if (parentid!=null && parentid.length()>0)
				   {
					   parent=userService.getOrgBydepid(parentid);
				   }
				   Long parentId=null;
				   if (parent!=null)
				   {
					   parentId=parent.getId();
				   }
				   Spaces space = new Spaces();
		           space.setName("weboffice");
		           space.setDescription("weboffice depart");
		           Long leaderId=null;//部门负责人
		           Long[] addUserIds=null;//增加的用户
		           List<Long> delUserIds=null;//删除了的用户
				   if (org==null)//新增组织
				   {
					   org=new Organizations();
					   org.setName(depname);
					   org.setDescription(description);
					   org.setParent(parent);
					   
					   org.setDepid(depid);
					   org.setParentid(parentid);
					   org.setDepname(depname);
					   org.setUpdatetime(updatetime);
					   org.setMemberlist(memberlist);
				   }
				   else
				   {
					   if (memberlist!=null && memberlist.length()>0)
					   {
						   List<UsersOrganizations> oldlist=userService.getUserInGroup(org.getId(), null);//获取原来的部门用户
						   String[] values=memberlist.split(";");
						   List<Long> templist=new ArrayList<Long>();
						   for (int i=0;i<values.length;i++)
						   {
							   boolean isadd=true;
							   if (oldlist!=null)
							   {
								   for (int j=0;j<oldlist.size();j++)
								   {
									   UsersOrganizations userorg=oldlist.get(j);
									   if (userorg.getUser().getUserName().equals(values[i]))
									   {
										   isadd=false;
										   break;
									   }
								   }
							   }
							   if (isadd)
							   {
								   Users user=userService.getUserByname(values[i]);
								   templist.add(user.getId());//增加的用户
							   }
						   }
						   if (oldlist!=null)
						   {
							   List dellist=new ArrayList<Long>();
							   for (int j=0;j<oldlist.size();j++)
							   {
								   UsersOrganizations userorg=oldlist.get(j);
								   boolean isdel=true;
								   for (int i=0;i<values.length;i++)
								   {
									   if (userorg.getUser().getUserName().equals(values[i]))
									   {
										   isdel=false;
										   break;
									   }
								   }
								   if (isdel)
								   {
									   dellist.add(userorg.getUser().getId());
								   }
							   }
							   if (dellist.size()>0)
							   {
								   delUserIds=dellist;
							   }
						   }
						   addUserIds=new Long[templist.size()];
						   templist.toArray(addUserIds);
					   }
					   else if ("0".equals(memberlist))//删除原来的用户
					   {
						   userService.delUsersOrganizationsByOrg(org.getId());
					   }
					   
					   org.setName(depname);
					   org.setDescription(description);
					   org.setParent(parent);
					   
					   org.setDepid(depid);
					   org.setParentid(parentid);
					   org.setDepname(depname);
					   org.setUpdatetime(updatetime);
					   org.setMemberlist(memberlist);
				   }
		           userService.addOrUpdateOrganization(parentId, org, addUserIds, null, delUserIds,
		   	            leaderId, space);
		           error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"同步成功");
			   }
		   }
		   else
		   {
			   error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, "同步失败");
		   }
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
		   error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, "同步失败");
	   }
	   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	   resp.getWriter().write(error);
   }
	@HandlerMethod(required = false, methodName = ServletConst.GETALLUSERSLIST)
   public static void getAllUsersList(HttpServletRequest req, HttpServletResponse resp,
           HashMap<String, Object> jsonParams) throws ServletException, IOException
   {//获取所有用户信息
	   UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	   
	   try
	   {
		   HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		   String account = (String)jsonParams.get("account");
		   if (WebConfig.outsameusertag.equals(account))//只有账号一致才让同步
		   {
			   List<Users> list=userService.getAllNormalUser();//获取所有的用户
			   List<HashMap<String, Object>> backlist=new ArrayList<HashMap<String, Object>>();
			   for (int i=0;i<list.size();i++)
			   {
				   Users user=list.get(i);
				   HashMap<String, Object> hashmap=new HashMap<String, Object>();
				   
				   hashmap.put("outcode", user.getOutcode());//外部用户编号
				   hashmap.put("outname", user.getOutname());//用户登录名
				   hashmap.put("outrealname", user.getOutrealname());//外部用户名称
				   hashmap.put("outpassw", user.getOutpassW());//外部同步过来的密码
				   hashmap.put("outemail", user.getOutemail());//外部邮箱
				   hashmap.put("outrole", user.getOutrole());//是否部门管理员
				   hashmap.put("outorgcode", user.getOutorgcode());//所属组织编号;多个组织用;间隔
				   hashmap.put("outca", user.getOutca());//ca号
				   hashmap.put("outsize", user.getOutsize());//空间大小
				   hashmap.put("outsex", user.getOutsex());//性别
				   hashmap.put("updatetime", user.getUpdatetime());//上次同步更新的时间
				   backlist.add(hashmap);
			   }
			   String error = JSONTools.convertToJson(ErrorCons.NO_ERROR,backlist);
			   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
			   resp.getWriter().write(error);
		   }
	   }catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   String error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, "获取失败");
	   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	   resp.getWriter().write(error);
   }
	@HandlerMethod(required = false, methodName = ServletConst.GETALLORGSLIST)
   public static void getAllOrgsList(HttpServletRequest req, HttpServletResponse resp,
           HashMap<String, Object> jsonParams) throws ServletException, IOException
   {//获取所有组织信息
	   try
	   {
		   UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		   HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		   String account = (String)jsonParams.get("account");
		   if (WebConfig.outsameusertag.equals(account))//只有账号一致才让同步
		   {
			   List<Organizations> orglist=userService.getAllNormalOrganizations();
			   List<HashMap<String, Object>> backlist=new ArrayList<HashMap<String, Object>>();
			   if (orglist!=null && orglist.size()>0)
			   {
				   for (int i=0;i<orglist.size();i++)
				   {
					   Organizations org=orglist.get(i);
					   HashMap<String, Object> hashmap=new HashMap<String, Object>();
					   hashmap.put("depid", org.getDepid());//外部系统的部门编号
					   hashmap.put("parentid", org.getParentid());//外部系统的父部门编号
					   hashmap.put("depname", org.getDepname());//外部系统的部门名称
					   hashmap.put("updatetime", org.getUpdatetime());//同步时间（long型的字符串）
					   hashmap.put("description", org.getDescription());//备注
					   hashmap.put("memberlist", org.getMemberlist());//部门组员，以;间隔
					   backlist.add(hashmap);
				   }
			   }
			   String error = JSONTools.convertToJson(ErrorCons.NO_ERROR,backlist);
			   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
			   resp.getWriter().write(error);
		   }
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   String error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, "获取失败");
	   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	   resp.getWriter().write(error);
   }
	@HandlerMethod(required = false, methodName = ServletConst.GETCALOGINTOKEN)
   public static void getCaLoginToken(HttpServletRequest req, HttpServletResponse resp,
           HashMap<String, Object> jsonParams) throws ServletException, IOException
   {//获取CAID的token
		try
	   {
			String caId = (String)jsonParams.get("caId");
			String domain = (String)jsonParams.get("domain");
			String[] back=loginCaInfo(caId,domain,req, resp);
		   String error = JSONTools.convertToJson(ErrorCons.NO_ERROR,back);
		   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		   resp.getWriter().write(error);
           return;
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   String error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);
	   resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	   resp.getWriter().write(error);
   }
	public static String[] loginCaInfo(String caId,String domain,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String[] back=new String[4];
	   try
	   {
		   Long userid=0L;
		   UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	       Users user = userService.calogin(caId);
	       if (user!=null)
	       {
	    	    HttpSession session = req.getSession();
			    String token = session.getId() + System.currentTimeMillis();   // 暂时以sessionId值作为认证的token值。
			    session.setAttribute("token", token);
			    
			    Integer result;
	           	result = handleLogin(domain, token, user.getUserName(), null,caId, userService, user, req, resp);
	           	userid=user.getId();
	           	
			    back[0]=user.getUserName();
			    back[1]=token;
			    back[2]=user.getCompany().getId().toString();
			    back[3]=user.getRole().toString();
	       }
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   return back;
	}
	public static void loginCaInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{//CA用户登录获取信息
		String[] back=null;
	   try
	   {
		   String caid=req.getParameter("caId");
		   String token=req.getParameter("token");
		   System.out.println("caid==="+caid);
		   String domain="com.yozo.do";
	    	Long userid=0L;
	    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	        Users user = userService.calogin(caid);
	        IMemCache  memCache = getMemCache();
	        LoginUserInfo lui = memCache.getLoginUserInfo(domain + "+" + user.getUserName().toLowerCase());
	        
	        if (token != null && lui != null && token.equals(lui.getToken()))
	        {
	        	//token验证成功了
	        }
	        else
	        {
	        	user=null;
	        }
		    if (user==null)
		    {
//		    	return null;
		    	resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		    	resp.getWriter().write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
		    			+"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />"
		    			+"<title>"+WebConfig.smsname+"系统</title></head><body>");
		    }
		        
//		    HttpSession session = req.getSession();
//		    String token = session.getId() + System.currentTimeMillis();   // 暂时以sessionId值作为认证的token值。
//		    session.setAttribute("token", token);
//		    System.out.println(caid+"================================="+token);
		        
	        Integer result;
           	result = handleLogin(domain, token, user.getUserName(), null,caid, userService, user, req, resp);
           	userid=user.getId();

		    if (result ==  ErrorCons.NO_ERROR)    // 登录成功
		    {
		    	back=new String[4];
		       	back[0]=user.getUserName();
		       	back[1]=user.getSpaceUID();
		       	back[2]=token;
		       	back[3]=""+user.getRole().shortValue();
		       	System.out.println(back[0]+"================================="+back[1]);
		       	resp.getWriter().write("<div id=\"flagForCA\" style=\"display: none;\">allReadyLogin</div>");
			    resp.getWriter().write("<div id=\"token\" style=\"display: none;\">"+token+"</div>");
			    resp.getWriter().write("<div id=\"userName\" style=\"display: none;\">"+user.getUserName()+"</div>");
			    resp.getWriter().write("<div id=\"spaceUID\" style=\"display: none;\">"+user.getSpaceUID()+"</div>");
			    resp.getWriter().write("<div id=\"role\" style=\"display: none;\">"+user.getRole().shortValue()+"</div>");
			    resp.getWriter().write("<div id=\"realEmail\" style=\"display: none;\">"+user.getRealEmail()+"</div>");
			    resp.getWriter().write("<div id=\"realName\" style=\"display: none;\">"+user.getRealName()+"</div>");
		       	//通知当前用户的联系人，用户已经上线
		       	ITalkService talkService=(ITalkService)ApplicationContext.getInstance().getBean(TalkService.NAME);
		       	try
		       	{
		       		talkService.sendOnlineNoticeMessage(req.getRemoteHost(),userid);
		       	}
		       	catch(Exception e)
		       	{
		       		e.printStackTrace();
		       	}
		    }
		    
	      //通过线程来删除文件
	        if (userid!=null && userid>0)
	        {
		        String path=WebConfig.tempFilePath;
		        String startstr=userid.longValue()+"_";
		        new ClearTempfile(path,startstr,null,null).start();
	        }
	        
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   resp.getWriter().write("</body></html>");
	}
	/**
	 * 登录系统。主要是常州机要局CA认证登录
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.LOGINCA_ACTION)
	public static String loginCA(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException {
		String error;
		Long userid = 0L;
		HashMap<String, Object> params = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String usercn = (String) params.get("usercn"); // 用户标示
		String username = (String) params.get("username");//用户账号
		String forbid = (String) params.get("forbid");
		System.out.println("usercn===="+usercn);
		System.out.println("username===="+username);
		System.out.println("utf8username===="+WebTools.converStr(username));
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users u = userService.getUser(username);
		
		if(u== null){
			u=userService.getUserByUserName1(username);
			if(u== null){
				u=userService.getUserByUserName2(username);
			}
			
			if(u == null){
				error = JSONTools.convertToJson(ErrorCons.USER_CA_LOGIN_ERROR, null);
				return error;
			}
			
		}
		
		userid = u.getId();
		
		if (forbid != null && forbid.length() > 0) // 检查设备是否有效。
		{
//			UserService userService = (UserService) ApplicationContext
//					.getInstance().getBean(UserService.NAME);
			if (userService.isDisabledDevice(forbid, username)) {
				return JSONTools.convertToJson(
						ErrorCons.USER_DEVICE_FORBID_ERROR, null);
				// resp.setHeader("Cache-Control",
				// "no-store,no-cache,must-revalidate");
				// resp.getWriter().write(error);
				// return;
			}
		}

		HttpSession session = req.getSession();
		String token = session.getId() + System.currentTimeMillis(); // 暂时以sessionId值作为认证的token值。
		session.setAttribute("token", token);

		Object data = userService.login(u.getUserName(), u.getPassW(), false);
		Integer result;
		if (data instanceof Users) {
			Users user = (Users) data;
			result = handleLogin("com.yozo.do", token, u.getUserName(),
					u.getPassW(),null, userService, user, req, resp);
			userid = user.getId();
		} else {
			result = (Integer) data;
		}

		if (result == ErrorCons.NO_ERROR) // 登录成功
		{
			String autoDirect = (String) params.get("autoDirect");
			if (autoDirect != null && autoDirect.length() > 0) {
				req.getRequestDispatcher(autoDirect).forward(req, resp);
				// resp.sendRedirect(autoDirect);
				return null;
			}

			HashMap map = new HashMap();
			map.put("token", token); // 暂时以sessionId值作为认证的token值。
			Users user = (Users) data;
			map.put("id", String.valueOf(user.getId()));
			map.put("realname", user.getRealName());
			map.put("role", u.getRole());
			Company company = ((Users) data).getCompany();
			if (company != null) {
				map.put("companyId", String.valueOf(company.getId()));
				map.put("spaceUID", company.getSpaceUID());
			}

			// user242, zipEnable
			map.put("isSupportZip", true);
			Object cookie = params.get("cookie");
			if (cookie != null && (Boolean) cookie) // 先兼容原有的做法
			{
				map.put("name",
						WebofficeUtility.passwordEncrypt(u.getUserName()));
				map.put("pass", WebofficeUtility.passwordEncrypt(u.getPassW()));

			}

			// 通知当前用户的联系人，用户已经上线
			ITalkService talkService = (ITalkService) ApplicationContext
					.getInstance().getBean(TalkService.NAME);
			try {
				talkService
						.sendOnlineNoticeMessage(req.getRemoteHost(), userid);
			} catch (Exception e) {
				e.printStackTrace();
			}

			error = JSONTools.convertToJson(result, map);
		} else {
			error = JSONTools.convertToJson(result, null);
		}

		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);

		// 通过线程来删除文件
		if (userid != null && userid > 0) {
			String path = WebConfig.tempFilePath;
			String startstr = userid.longValue() + "_";
			new ClearTempfile(path, startstr, null, null).start();
		}
		return error;
	}

	/**
	 * 登录系统。主要是常州机要局CA认证登录
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.LOGINCAMOBILE_ACTION)
	public static String loginCAMobile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException {
		String error;
		// 用户名称
		String username = null;
		// 用户标示
		String usercn = "yozo";

		Cookie[] cookies = req.getCookies();

		if (cookies == null) {
			error = JSONTools.convertToJson(ErrorCons.USER_CA_LOGIN_ERROR,
					"请插入CA证书");
			return error;
		}

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];

				if ("SSL_VERIFY_CERT".equals(cookie.getName())) {
					String value = new String(cookie.getValue().getBytes(
							"ISO-8859-1"), "GBK");
					value = URLDecoder.decode(value, "utf-8");
					if (value == null
							| !value.trim().toLowerCase().equals("yes")) {
						error = JSONTools.convertToJson(
								ErrorCons.USER_CA_LOGIN_ERROR, "CA证书验证失败");
						return error;
					}
				} else if ("KOAL_CERT_T".equals(cookie.getName())) {
					usercn = new String(cookie.getValue()
							.getBytes("ISO-8859-1"), "GBK");
					usercn = URLDecoder.decode(usercn, "utf-8");
				} else if ("KOAL_CERT_CN".equals(cookie.getName())) {
					username = new String(cookie.getValue().getBytes(
							"ISO-8859-1"), "GBK");
					username = URLDecoder.decode(username, "utf-8");
				}

			}
		}

		if (usercn == null || usercn.trim().equals("") || username == null
				|| username.trim().equals("")) {
			error = JSONTools.convertToJson(ErrorCons.USER_CA_LOGIN_ERROR,
					"CA证书验证失败");
			return error;
		} else {
			// String u=username+"&"+usercn;
			// u=new DES().encrypt(u);
			// request.getRequestDispatcher("dispachCA.jsp?u="+u).forward(request,response);
			// return;
			HashMap<String, Object> params = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String forbid = (String) params.get("forbid");
			
			jsonParams = new HashMap<String, Object>();
			params = new HashMap<String, Object>();
			params.put("usercn", usercn);
			params.put("username", username);
			params.put("forbid", forbid);
			
			jsonParams.put("params", params);
			jsonParams.put("method", "loginCA");
			
			return loginCA(req, resp, jsonParams);
		}
	}
	
	@HandlerMethod(methodName = ServletConst.TOTAL_SMS)
	public static String getTotalSMS(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{//汇总，今日发信量，本月，今年，总数，登录总人数，平均日发量
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	String account = (String)param.get("account");
    	Users users=jqlService.getUsers(account);//获取用户信息
    	LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date startDate = new Date();
    	startDate.setHours(0);
    	startDate.setMinutes(0);
    	startDate.setSeconds(0);
    	Date endDate = new Date();
    	endDate.setHours(23);
    	endDate.setMinutes(59);
    	endDate.setSeconds(59);
    	String sql="select count(a) from MobileSendInfo as a where a.senddate>=?";
    	Long count = (Long)jqlService.getCount(sql, startDate);    			
    	ret.put("今日短信量", String.valueOf(count));
    	
    	startDate.setDate(1);
    	endDate.setDate(31);     //   简单处理，暂时不处理大小月的问题
    	sql="select count(a) from MobileSendInfo as a where a.senddate>=? and a.senddate<=?";
    	count = (Long)jqlService.getCount(sql, startDate, endDate);
    	ret.put("本月短信量", String.valueOf(count));
    	
    	startDate.setMonth(0);
    	endDate.setMonth(11);
    	sql="select count(a) from MobileSendInfo as a where a.senddate>=? and a.senddate<=?";
    	count = (Long)jqlService.getCount(sql,startDate, endDate);
    	ret.put("今年短信量", String.valueOf(count));
    	
    	sql="select count(a) from MobileSendInfo as a ";
    	count = (Long)jqlService.getCount(sql);
    	Long totalsms=count;
    	ret.put("总短信量", String.valueOf(count));
    	
    	sql="select count(a) from MobileSendInfo as a where a.sender is null ";
    	count = (Long)jqlService.getCount(sql);
    	ret.put("验证码短信", String.valueOf(count));
    	
    	sql="select count(distinct a.user) from SystemLogs as a,Users as b where a.user.id=b.id and (b.role=0 or b.role=9)";
    	count =  (Long)jqlService.getCount(sql);
    	Long mans=count;
    	ret.put("总使用人数", String.valueOf(count));//不算管理员
    	
    	sql="select max(a.senddate) from MobileSendInfo as a ";//最大发送时间
    	List maxdate = (List)jqlService.findAllBySql(sql);
    	if (maxdate!=null && maxdate.size()>0 && totalsms!=null && mans!=null && mans.longValue()>0)
    	{
	    	Calendar calendar=Calendar.getInstance();
	    	calendar.setTime((Date)maxdate.get(0));
	    	long maxtime=calendar.getTimeInMillis();
	    	sql="select min(a.senddate) from MobileSendInfo as a ";//最小发送时间
	    	List mindate = (List)jqlService.findAllBySql(sql);
	    	calendar.setTime((Date)mindate.get(0));
	    	long mintime=calendar.getTimeInMillis();
	    	long days=(maxtime-mintime)/(24*60*60*1000);//折算成天
	    	if (days>0)
	    	{
	    		DecimalFormat df=new DecimalFormat("#.##");
	    		ret.put("平均每人每日短信数", df.format(1.0*totalsms/(days*mans)));
	    	}
    	}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret); 
	}
	@HandlerMethod(methodName = ServletConst.TOTAL_MESSAGE)
	public static String totalMessage(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{//获取每个单位的短信总数
		//先获取所有的单位，然后在遍历所有的部门短息
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String temp = (String)param.get("startDate");
    	Date startD = temp != null ? sdf.parse(temp) : null;
    	if (startD != null)
    	{
    		startD.setHours(0);
    		startD.setMinutes(0);
    		startD.setSeconds(0);
    	}
    	temp = (String)param.get("endDate");
    	Date endD = temp != null ? sdf.parse(temp) : null;
    	if (endD != null)
    	{
    		endD.setHours(23);
    		endD.setMinutes(59);
    		endD.setSeconds(59);
    	}
    	String sql="select a.id,a.parentKey,count(a) from Organizations a,UsersOrganizations b,MobileSendInfo c "
    			   +" where a.id=b.organization.id and b.user.id=c.sender.id group by a.id,a.parentKey";
//    	String countsql="select count(a) from Organizations a,UsersOrganizations b,MobileSendInfo c "
// 			   +" where a.id=b.organization.id and b.user.id=c.sender.id and a.parent=null ";
    	String orgsql="select a from Organizations a where a.parent is null ";
    	List<Organizations> orglist=(List<Organizations>)jqlService.findAllBySql(orgsql);//先获取所有单位,目前是信电局版本这样做，如果公网还要加单位编号限制
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	String order = (String)param.get("order");     // 值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
    	String dir = (String)param.get("dir");
		if (order != null && dir != null)
		{
			if (order.equals("orgname"))
			{
				sql+=" order by a.name "+dir;
			}
		}
		else
		{
			sql+=" order by a.name ";
		}
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		List<Object[]> objlist=(List<Object[]>)jqlService.findAllBySql(sql);
		if (orglist!=null && orglist.size()>0)
		{
			for (int j=0;j<orglist.size();j++)
			{
				Organizations org=orglist.get(j);
				HashMap<String, String> map=new HashMap<String, String>();
				String id=String.valueOf(org.getId())+"-";
				
				map.put("orgname",String.valueOf(org.getName()));
				long nums=0;
				if (objlist!=null && objlist.size()>0)
				{
					for (int i=0;i<objlist.size();i++)
					{
						Object[] obj=objlist.get(i);
						if (obj[1]==null)
						{
							if (org.getId().longValue()==((Long)obj[0]).longValue())
							{
								nums+=((Long)obj[2]).longValue();
							}
						}
						else if ((String.valueOf(obj[1])).startsWith(id))//判断是否属于这个部门
						{
							nums+=((Long)obj[2]).longValue();
						}
					}
				}
				map.put("mobilenums",String.valueOf(nums));
				if (nums==0)
				{
					map.put("orgid",String.valueOf(0));
				}
				else
				{
					map.put("orgid",String.valueOf(org.getId()));
				}
				result.add(map);
			}
		}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	@HandlerMethod(methodName = ServletConst.TOTAL_COMPANYSMS)
	public static String getCompanysms(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{//获取单位人员的短信总数
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   //
    	String orgid = (String)param.get("orgid");//单位编号
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String sql="select d.id,d.realName,count(a) from Organizations a,UsersOrganizations b,MobileSendInfo c,Users d "
    			   +" where a.id=b.organization.id and b.user.id=c.sender.id "
    			   +" and b.user.id=d.id "
    			   +" and (a.id="+orgid+" or a.parentKey like '"+orgid+"-%')"
    			   +" group by d.id,d.realName ";
//    	String sql="select d.id,d.realName,1 from Organizations a,UsersOrganizations b,MobileSendInfo c,Users d "
// 			   +" where a.id=b.organization.id and b.user.id=c.sender.id "
// 			   +" and b.user.id=d.id "
// 			   ;
    	String order = (String)param.get("order");     // 值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
    	String dir = (String)param.get("dir");
		if (order != null && dir != null)
		{
			if (order.equals("username"))
			{
				sql+=" order by d.realName "+dir;
			}
		}
		else
		{
			sql+=" order by a.realName ";
		}
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		List<Object[]> objlist=(List<Object[]>)jqlService.findAllBySql(sql);
		
		if (objlist!=null && objlist.size()>0)
		{
			for (int i=0;i<objlist.size();i++)
			{
				Object[] obj=objlist.get(i);
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("username",String.valueOf(obj[1]));
				map.put("mobilenums",String.valueOf(obj[2]));
				map.put("userid",String.valueOf(obj[0]));
				result.add(map);
			}
		}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	@HandlerMethod(methodName = ServletConst.GETCONFIG,required=false)
	public static String getConfig(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams) throws IOException {
    	//获取配置文件，是否要注册和是否要验证码  
    	HashMap<String, Object> back = new HashMap<String, Object>();
    	back.put("identifycode", WebConfig.identifycode);//是否需要验证码
    	back.put("register", WebConfig.register);//是否需要注册
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, back); 
	}
}
