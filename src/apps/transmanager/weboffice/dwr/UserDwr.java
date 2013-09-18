package apps.transmanager.weboffice.dwr;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.bugs.BugWork;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.OnlinePo;
import apps.transmanager.weboffice.service.IUserService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.DwrScriptSessionManagerUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.server.WebTools;

public class UserDwr {
	
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	public String getValidateCode(HttpServletRequest req,HttpServletResponse resp)
	{//获取验证码
		String code=(String)req.getSession().getAttribute("validateCode");
		System.out.println(req.getSession().getId()+"==="+code);
		Cookie[] cookies=req.getCookies();
		if (cookies!=null)
		{
			for (int i=0;i<cookies.length;i++)
			{
				System.out.println(cookies[i].getName()+"==="+cookies[i].getValue());
			}
		}
		return code;
	}
	public String getServerIp(HttpServletRequest req)
	{
		String ip=req.getLocalAddr();
		int index=ip.lastIndexOf(".");
		if (index>0)
		{
			return ip.substring(index+1);
		}
		return "";
	}
	public Users getUserinfo(HttpServletRequest req){
		Users userinfo = (Users) req.getSession().getAttribute("userKey");
		try
		{
			userinfo = userService.getUserById(userinfo.getId());
			userinfo.setImage(WebConfig.userPortrait + userinfo.getImage1());
			String token=(String)req.getSession().getAttribute("token");
			userinfo.setToken(token);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return userinfo;
	}
	/**
	 * 获取用户信息
	 * @param id 用户ID
	 * @param req
	 * @return
	 */
	public Users getNewUserinfo(Long id,HttpServletRequest req){
		Users userinfo = (Users) req.getSession().getAttribute("userKey");
		//暂不做用户权限判断
		try
		{
			Users edituserinfo = userService.getUserById(userinfo.getId());
			edituserinfo.setImage(WebConfig.userPortrait + edituserinfo.getImage1());
			//存放角色和部门
			userinfo.setRoles(null);//如果需要自行添加
			userinfo.setOrgs(null);//如果需要自行添加
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return userinfo;
	}
	
	public int getOpenFileType(HttpServletRequest req)
	{
		Users userinfo = (Users) req.getSession().getAttribute("userKey");
		try
		{
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(
	                UserService.NAME);
	        return userService.getOpenFileType(userinfo.getId());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 删除在线用户
	 * @param req 请求信息
	 */
	public void delOnline(HttpServletRequest req)
	{
		Users userInfo = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);//当前登录的用户编号
		if (userInfo != null)
		{
			userService.delOnline(userInfo.getId());
		}
		String ssId = (String) req.getSession().getAttribute("DWR_ScriptSession_Id");
		DwrScriptSessionManagerUtil.invalidate(ssId);
	}
	
	/**
	 * 初始化登录用户，获得当前用户信息，同时将当前用户加入在线列表
	 * @return 用户信息
	 */
	@Deprecated
	public Users initOnline(HttpServletRequest req)
	{
		Users userInfo = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);//当前登录的用户编号
		//需要将信息插入登录信息表里
		OnlinePo onlinePo = new OnlinePo();
		onlinePo.setUserId(userInfo.getId());
		onlinePo.setName(userInfo.getUserName());
		String ip=WebTools.getRealIpAddr(req);
		onlinePo.setIp(ip);
		onlinePo.setLoginTime(new Date());
		userService.addOnline(onlinePo);
		req.getSession().setAttribute(PageConstant.LG_SESSION_ONLINE, onlinePo);
		return userInfo;
	}
	
	/**
	 * 更新用户信息
	 * @param userInfo 用户信息
	 * @param req 请去信息
	 * @return 更新结果
	 */
	public boolean updateUser(Users userInfo,HttpServletRequest req){
		try{
			Users olduserinfo = userService.getUserById(userInfo.getId());
			userInfo.setPassW(olduserinfo.getPassW());
			userInfo.setUruid(olduserinfo.getUruid());
			String image = userInfo.getImage1();
			int index = image.lastIndexOf("/");
			if (index >= 0)
			{
				image = image.substring(index + 1);
			}
			userInfo.setImage(image);
			
			olduserinfo.setImage(image);
			olduserinfo.setRealEmail(userInfo.getRealEmail());
			olduserinfo.setRealName(userInfo.getRealName());
			olduserinfo.setMan(userInfo.getMan());
			olduserinfo.setBirthday(userInfo.getBirthday());
			olduserinfo.setMobile(userInfo.getMobile());
			olduserinfo.setPhone(userInfo.getPhone());
			olduserinfo.setCompanyAddress(userInfo.getCompanyAddress());
			olduserinfo.setAddress(userInfo.getAddress());
			olduserinfo.setDescription(userInfo.getDescription());
			olduserinfo.setLoginCA(userInfo.getLoginCA());
			olduserinfo.setInfodef(userInfo.getInfodef());
			olduserinfo.setPageviews(userInfo.getPageviews());
			userService.updateUser(olduserinfo);
			//更新session中的信息
			Users sessionUser = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			sessionUser.setRealName(userInfo.getRealName());
			sessionUser.setImage(userInfo.getImage1());
			req.getSession().setAttribute(PageConstant.LG_SESSION_USER, sessionUser);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * 更新用户密码
	 * @param id 用户ID
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @param req 请求信息
	 * @return 更新结果
	 */
	public int updatePwd(Long id,String oldPwd,String newPwd,HttpServletRequest req){
		int result = PageConstant.VALIDATOR_NAME_SUC;
		try{
			result = userService.updatePwd(id,oldPwd,newPwd);
		}catch(Exception e){
			return PageConstant.VALIDATOR_NAME_DUP;
		}
		return result;
	}
	
	/***
	 * 
	 * @param userId
	 * @param req
	 * @return
	 */
	public String getOrgs(Long userId,HttpServletRequest req){
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		List<Organizations> list = ((UserService)userService).getOrganizationByUsers(userId);
		int length = list.size();
    	String str = "";
        for (int i = 0; i < length; i++)
        {
        	Organizations org = list.get(i);
        	str += org.getName();
        }
        return str;
	}
	/**
	 * 密码强度验证
	 * @param newPass
	 * @return
	 */
	public String regexPassWord(String newPass){
		Properties properties = new Properties();
    	try {
			properties.load(UserDwr.class.getClassLoader().getResourceAsStream("/conf/passworPolicy.properties"));
			String regular = properties.getProperty("password.regular");
			String alertMessage = properties.getProperty("password.alertMessage");
			System.out.println(regular);
			Pattern p = Pattern.compile(regular); 
			Matcher m = p.matcher(newPass); 
			if (m.matches()) { 
				return "true";
			}else{
				return alertMessage;
			} 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "exception";
		}
	}
	
	public String[] changeacntion(Long actionid,HttpServletRequest req)
	{
		return BugWork.getActionStates(actionid);
	}
}
