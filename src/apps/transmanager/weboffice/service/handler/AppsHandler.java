package apps.transmanager.weboffice.service.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.annotation.HandlerMethod;
import apps.moreoffice.annotation.ServerHandler;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CompanyApps;
import apps.transmanager.weboffice.databaseobject.PersonApps;
import apps.transmanager.weboffice.databaseobject.SystemApps;
import apps.transmanager.weboffice.databaseobject.UserApps;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.AppsService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 应用的处理
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@ServerHandler
public class AppsHandler
{

	public static Integer importApps(String fileName)
	{
		//EF BB BF
		String content = null;
		FileInputStream reader = null;
		try
		{
			reader = new FileInputStream(fileName);
			int size = reader.available();
			byte[] retByte = new byte[size];
			reader.read(retByte);
			if ((retByte[0] & 0x00FF) == 0xEF && (retByte[1] & 0x00FF) == 0xBB && (retByte[2]  & 0x00FF) == 0xBF)
			{
				content = new String(retByte, 3, size - 3, "utf-8");
			}
			else if ((retByte[0] & 0x00FF) == 0xFF && (retByte[1] & 0x00FF) == 0xFE)
			{
				content = new String(retByte, "unicode");
			}
			else
			{				
				content = new String(retByte, "GBK");
			}
			
			String[] lines = content.split("[\n\r]");
			String[] ou;
			int index;
			String name;
			AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME);
			SystemApps sa;
			long n;
			Date time;
			boolean success = false;
			for (String line : lines)
			{
				line = line.trim();
				if (!line.startsWith("#"))     // 
				{
					index = line.indexOf("=");
					if (index <=0)
					{
						continue;
					}
					name = line.substring(0, index);
					line = line.substring(index + 1);
					ou = line.split(",");
					sa = appsService.getSystemApps(name);
					n = Long.valueOf(ou[5]);
					if (n < 0)    // 删除应用
					{
						if (sa != null)
						{
							appsService.deleteSystemApps(sa.getId());
							success = true;
						}
						continue;
					}
					if (n == 0 || n > 1000 * 365)    // 长期使用
					{
						n = 1000 * 365;
					}
					time = new Date(System.currentTimeMillis() + n * 24L * 60L * 60L * 1000L);
					if (sa != null)
					{
						sa.update(ou[2], Integer.valueOf(ou[0]), Integer.valueOf(ou[1]), ou[3], ou[4], time, ou[7], Boolean.valueOf(ou[6]));
						appsService.updateSystemApps(sa);
						success = true;
					}
					else
					{						
						sa = new SystemApps(name, ou[2], Integer.valueOf(ou[0]), Integer.valueOf(ou[1]), ou[3], ou[4], time, ou[7], Boolean.valueOf(ou[6]));
						appsService.addSystemApps(sa);
						success = true;
					}
				}
				else      // 其他数据
				{
					continue;
				}
			}
			return success ? ErrorCons.NO_ERROR : ErrorCons.FILE_INVALIDATE_ERROR;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);	
			return ErrorCons.FILE_FORMAT_ERROR;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception ee)
				{
					LogsUtility.error(ee);
				}
			}
		}		
	}
	
	
	/**
	 * 获取用户可以使用的应用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_APPS)
	public static String getUserApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String tempF = (String)param.get("flag");
    	Integer flag = tempF != null ? Integer.valueOf(tempF) : 0; // 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
    	tempF = (String)param.get("type");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Integer type = tempF != null ? Integer.valueOf(tempF) : -1; 
    	    		
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	List<UserApps> ret = appsService.getUserApps(account, flag, type); 
    	List<HashMap<String, Object>> retsult = new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> apps;
    	CompanyApps capp;
    	SystemApps sapp;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	for (UserApps temp : ret)
    	{
    		apps = new HashMap<String, Object>();
    		capp = temp.getCompanyApps();
    		if (capp != null)
    		{
    			sapp = capp.getApps();
    		}
    		else
    		{
    			sapp = temp.getSysApps();
    		}
    		apps.put("id", sapp.getId());
    		apps.put("name", sapp.getName());   
    		if (temp.getDisplayName() == null)    // 用户没有修改过名字
    		{
    			if (capp != null)
    			{
    				apps.put("displayName", capp.getDisplayName());
    			}
    			else
    			{
    				apps.put("displayName", sapp.getDisplayName());
    			}
    		}
    		else
    		{
    			apps.put("displayName", temp.getDisplayName());
    		}
    		apps.put("sortCode", temp.getSortCode());
    		apps.put("path", sapp.getPath());
    		apps.put("picPath", sapp.getPicPath());
    		apps.put("type", sapp.getType());    	
    		apps.put("time", sdf.format(temp.getEndTime()));
    		retsult.add(apps);    		
    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取系统中的应用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SYSTEM_APPS)
	public static String getSystemApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	String tempF = (String)param.get("flag");
    	Integer flag = tempF != null ? Integer.valueOf(tempF) : 0; // 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
    	tempF = (String)param.get("type");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Integer type = tempF != null ? Integer.valueOf(tempF) : -1; 
    	    		
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	List<SystemApps> ret = appsService.getSystemApps(flag, type); 
    	List<HashMap<String, Object>> retsult = new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> apps;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	for (SystemApps temp : ret)
    	{
    		apps = new HashMap<String, Object>();
    		apps.put("id", temp.getId());
    		apps.put("name", temp.getName());    		
    		apps.put("displayName", temp.getDisplayName());
    		apps.put("sortCode", temp.getSortCode());
    		//apps.put("path", temp.getPath());
    		//apps.put("picPath", temp.getPicPath());
    		apps.put("type", temp.getType());    	
    		apps.put("time", sdf.format(temp.getEndTime()));
    		apps.put("desc", temp.getDesc());
    		apps.put("flag", temp.getFlag());
    		retsult.add(apps);    		
    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 获取公司的应用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_COMPANY_APPS)
	public static String getCompanyApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	Users user=jqlService.getUsers(account);
    	String tempF = (String)param.get("flag");
    	Integer flag = tempF != null ? Integer.valueOf(tempF) : 0; // 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
    	tempF = (String)param.get("type");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Integer type = tempF != null ? Integer.valueOf(tempF) : -1;
    	tempF = (String)param.get("companyId");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Long companyId = Long.valueOf(tempF); 
    	String managetype=(String)param.get("managetype");//back为后台管理，不要过滤
    	PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	List<CompanyApps> ret = appsService.getCompanyApps(companyId, flag, type); 
    	List<PersonApps> plist=(List<PersonApps>)jqlService.findAllBySql("select a from PersonApps as a where a.user.id=?", user.getId());//个人用户设置的模块显示
    	List<HashMap<String, Object>> retsult = new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> apps;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	SystemApps sapp;
    	for (CompanyApps temp : ret)
    	{
    		apps = new HashMap<String, Object>();
    		sapp = temp.getApps();
    		String name=sapp.getName();
    		boolean ismy=true;
    		boolean notfilt=false;
    		for (int j=0;j<ManagementCons.NOTFILT.length;j++)
    		{
    			if (ManagementCons.NOTFILT[j].equals(name))
    			{
    				notfilt=true;
    				break;
    			}
    		}
    		if (notfilt)//固有模块，直接放过
    		{
    			ismy=true;
    		}
    		else //只有非固定模块才进行判断
    		{
    			if (!"back".equals(managetype))//后台管理员编辑单位模块不进行过滤
    			{
		    		for (int j=0;j<ManagementCons.RATIONAL_MODELNAME.length;j++)
		    		{
		    			if (name.equals(ManagementCons.RATIONAL_MODELNAME[j]))
		    			{
		    				//获取当前用户的角色，然后判断该角色是否有此权限
		    				ismy=false;//模块在角色权限中，需要判断此人有没有权限
		    				long systemA = permissionService.getSystemPermission(user.getId());
		    	    		if (FlagUtility.isLongFlag(systemA, ManagementCons.RATIONAL_MODELID[j]))//相等说明有权限
		    	    		{
		    	    			ismy=true;
		    	    		}
		    			}
		    		}
		    		if (ismy && plist!=null && plist.size()>0)
		    		{
		    			//这里还要处理用户自己设置的模块选项
		    			ismy=false;
		    			for (int j=0;j<plist.size();j++)
		    			{
		    				PersonApps personApps=plist.get(j);
		    				if (temp.getId().longValue()==personApps.getCompanyApps().getId().longValue())
		    				{
		    					ismy=true;
		    					break;
		    				}
		    			}
		    		}
		    		if (plist==null || plist.size()==0)//如果用户没有设置就显示固有模块
    	    		{
    					ismy=false;
			    		for (int j=0;j<ManagementCons.NOTFILT.length;j++)
			    		{
			    			if (ManagementCons.NOTFILT[j].equals(name))
			    			{
			    				ismy=true;
			    				break;
			    			}
			    		}
    	    		}
    			}
    		}
    		if (ismy)
    		{
    			if ("caibian".equals(name))
    			{
    				long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
                	if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_ANY_FLAG))//判断是否有采编分析权限
            		{
                		apps.put("id", sapp.getId());
        	    		apps.put("name", name);    		
        	    		apps.put("displayName", temp.getDisplayName());
        	    		apps.put("sysDisplayName", sapp.getDisplayName());
        	    		apps.put("sortCode", temp.getSortCode());
        	    		//apps.put("path", temp.getPath());
        	    		//apps.put("picPath", temp.getPicPath());
        	    		apps.put("type", sapp.getType());    	
        	    		apps.put("time", sdf.format(temp.getEndTime()));
        	    		apps.put("desc", sapp.getDesc());
        	    		apps.put("flag", temp.getFlag());
        	    		retsult.add(apps);
            		}
    			}
    			else
    			{
    				apps.put("id", sapp.getId());
    	    		apps.put("name", name);    		
    	    		apps.put("displayName", temp.getDisplayName());
    	    		apps.put("sysDisplayName", sapp.getDisplayName());
    	    		apps.put("sortCode", temp.getSortCode());
    	    		//apps.put("path", temp.getPath());
    	    		//apps.put("picPath", temp.getPicPath());
    	    		apps.put("type", sapp.getType());    	
    	    		apps.put("time", sdf.format(temp.getEndTime()));
    	    		apps.put("desc", sapp.getDesc());
    	    		apps.put("flag", temp.getFlag());
    	    		retsult.add(apps);
    			}
	    		
    		}
    		if ("caibian".equals(name))
    		{
    			HashMap<String, Object> permisions = new HashMap<String, Object>();//权限判断，主要用于按钮的显示
    			long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
    			if (FlagUtility.isValue(per, ManagementCons.COLLECT_BAOSONG_FLAG))//判断是否有报送权限
    			{
    				permisions.put("baosong", true);//是否显示报送按钮
    			}
    			else
    			{
    				permisions.put("baosong", false);//是否显示报送按钮
    			}
    			//有权限的人才可以采编
            	
            	if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))//判断是否有采编权限
        		{
            		permisions.put("caibian", true);
        		}
            	else
            	{
            		permisions.put("caibian", false);
            	}
    			permisions.put("name", "permisions");
    			permisions.put("displayName", "permisions");
    			retsult.add(0,permisions);//必须要将权限放到第一个
    		}
    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 增加或修改公司的应用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_UPDATE_COMPANY_APPS)
	public static String addOrUpdateCompanyApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	
    	UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Company company = userService.getCompany(companyId);     	
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	
    	List<HashMap<String, String>> apps = (List<HashMap<String, String>>)param.get("apps");
    	appsService.addOrUpdateCompanyApps(apps, company);  	   
    	    	
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 增加或修改公司给用户的应用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_UPDATE_USER_APPS)
	public static String addOrUpdateUserApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	String temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	
    	UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Users user = userService.getUser(userId);     	
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List<HashMap<String, String>> apps = (List<HashMap<String, String>>)param.get("apps");
    	Long appId;
    	UserApps uapps;
    	CompanyApps capps;
    	Integer type;     // 0 增加，1修改，2删除
    	List<Long> deleteList = new ArrayList<Long>();    	
    	List<UserApps> addList = new ArrayList<UserApps>();
    	List<UserApps> modifyList = new ArrayList<UserApps>();
    	Date time;
    	for (HashMap<String, String> tempApp : apps)
    	{
    		temp = tempApp.get("id");    		
    		appId = Long.valueOf(temp);
    		temp = tempApp.get("type");
    		type = Integer.valueOf(temp);
    		if (type == 2)
    		{
    			deleteList.add(appId);
    			continue;
    		}
    		uapps = appsService.getUserAppsByCompany(appId, userId);
    		if (uapps == null)
    		{
    			capps = appsService.getCompanyAppsByUser(appId, userId);
    			uapps = new UserApps(capps, user);
    			addList.add(uapps);
    		}
    		else
    		{
    			modifyList.add(uapps);
    		}
    		temp = tempApp.get("displayName");
    		if (temp != null)
    		{
    			uapps.setDisplayName(temp);
    		}
    		temp = tempApp.get("sortCode");  
    		if (temp != null)
    		{
    			uapps.setSortCode(Integer.valueOf(temp));
    		}
    		temp = tempApp.get("endTime");
    		if (temp != null)
    		{
    			try
    			{
    				time = sdf.parse(temp);
    				if (time.getTime() < uapps.getCompanyApps().getEndTime().getTime())     // 不可设置大于本身公司允许的时间
    				{
    					uapps.setEndTime(time);
    				}
    			}
    			catch(Exception e)
    			{
    				LogsUtility.error(e);    				
    			}
    		}
    	}    	
    	appsService.addOrUpdateUserAppsByCompany(addList, modifyList, deleteList, userId); 
    	    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 增加或修改用户的应用（用户自己购买的应用）
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_UPDATE_USER_APPS_BY_USER)
	public static String addOrUpdateUserAppsByUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	String temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);
    	
    	UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME); 
    	Users user = userService.getUser(userId);     	
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List<HashMap<String, String>> apps = (List<HashMap<String, String>>)param.get("apps");
    	Long appId;
    	UserApps uapps;
    	SystemApps sapps;
    	Integer type;     // 0 增加，1修改，2删除
    	List<Long> deleteList = new ArrayList<Long>();    	
    	List<UserApps> addList = new ArrayList<UserApps>();
    	List<UserApps> modifyList = new ArrayList<UserApps>();
    	Date time;
    	for (HashMap<String, String> tempApp : apps)
    	{
    		temp = tempApp.get("id");    		
    		appId = Long.valueOf(temp);
    		temp = tempApp.get("type");
    		type = Integer.valueOf(temp);
    		if (type == 2)
    		{
    			deleteList.add(appId);
    			continue;
    		}
    		uapps = appsService.getUserAppsBySystem(appId, userId);
    		if (uapps == null)
    		{
    			sapps = appsService.getSystemApps(appId);
    			uapps = new UserApps(sapps, user);
    			addList.add(uapps);
    		}
    		else
    		{
    			modifyList.add(uapps);
    		}
    		temp = tempApp.get("displayName");
    		if (temp != null)
    		{
    			uapps.setDisplayName(temp);
    		}
    		temp = tempApp.get("sortCode");  
    		if (temp != null)
    		{
    			uapps.setSortCode(Integer.valueOf(temp));
    		}
    		temp = tempApp.get("endTime");
    		if (temp != null)
    		{
    			try
    			{
    				time = sdf.parse(temp);
    				if (time.getTime() < uapps.getSysApps().getEndTime().getTime())     // 不可设置大于本身系统允许的时间
    				{
    					uapps.setEndTime(time);
    				}
    			}
    			catch(Exception e)
    			{
    				LogsUtility.error(e);    				
    			}
    		}
    	}    	
    	appsService.addOrUpdateUserAppsByUser(addList, modifyList, deleteList, userId); 
    	    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);    
    }
	
	
	/**
	 * 获取个人用户的应用模块，默认是获取单位所有的应用模块，每个模块有个人用户的使用状态
	 * 此方法要与获取公司的应用模块配套进行,可以提取公用方法
	 */
	@HandlerMethod(methodName = ServletConst.GET_PERSONAL_APPS)
	public static String getPersonalApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	Users user=jqlService.getUsers(account);
    	String tempF = (String)param.get("flag");
    	Integer flag = tempF != null ? Integer.valueOf(tempF) : 0; // 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
    	//tempF = (String)param.get("type");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Integer type = -1;//tempF != null ? Integer.valueOf(tempF) : -1;
    	//tempF = (String)param.get("companyId");  // 0在左边，1在上边，2为在右边，3为在下边,-1为所有为所有应用
    	Long companyId = user.getCompany().getId();//Long.valueOf(tempF); 
    	  
    	PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	AppsService appsService = (AppsService) ApplicationContext.getInstance().getBean(AppsService.NAME); 
    	List<CompanyApps> ret = appsService.getCompanyApps(companyId, flag, type); 
    	List<HashMap<String, Object>> retsult = new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> apps;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	SystemApps sapp;
    	
    	//获取个人用户的模块，默认是空，如果设置了才进行模块筛选
    	List<PersonApps> personlist=(List<PersonApps>)jqlService.findAllBySql("select DISTINCT a from PersonApps as a where a.user.id=?", user.getId());
    	boolean isfilt=false;//是否筛选
    	if (personlist!=null && personlist.size()>0)
    	{
    		isfilt=true;
    	}
    	for (CompanyApps temp : ret)
    	{
    		apps = new HashMap<String, Object>();
    		sapp = temp.getApps();
    		String name=sapp.getName();
    		boolean ismy=true;
    		boolean notfilt=false;
    		if ("caibian".equals(name))
			{
	    		long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
	        	if (!FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_ANY_FLAG))//判断是否有采编分析权限
	        	{
	        		continue;//如果没有采编分析权限也不出现按钮
	        	}
			}
    		for (int j=0;j<ManagementCons.NOTFILT.length;j++)
    		{
    			if (ManagementCons.NOTFILT[j].equals(name))
    			{
    				notfilt=true;
    				break;
    			}
    		}
    		if (notfilt)//固有模块，不显示
    		{
    			ismy=false;
    		}
    		else //只有非固定模块才进行判断
    		{
	    		for (int j=0;j<ManagementCons.RATIONAL_MODELNAME.length;j++)
	    		{
	    			if (name.equals(ManagementCons.RATIONAL_MODELNAME[j]))
	    			{
	    				//获取当前用户的角色，然后判断该角色是否有此权限
	    				ismy=false;//模块在角色权限中，需要判断此人有没有权限
	    				long systemA = permissionService.getSystemPermission(user.getId());
	    	    		if (FlagUtility.isLongFlag(systemA, ManagementCons.RATIONAL_MODELID[j]))//相等说明有权限
	    	    		{
	    	    			ismy=true;
	    	    		}
	    			}
	    		}
    		}
    		
    		if (ismy)
    		{
    			
	    		apps.put("id", sapp.getId());
	    		apps.put("name", name);    		
	    		apps.put("displayName", temp.getDisplayName());
	    		apps.put("sysDisplayName", sapp.getDisplayName());
	    		apps.put("sortCode", temp.getSortCode());
	    		//apps.put("path", temp.getPath());
	    		//apps.put("picPath", temp.getPicPath());
	    		apps.put("type", sapp.getType());    	
	    		apps.put("time", sdf.format(temp.getEndTime()));
	    		apps.put("desc", sapp.getDesc());
	    		apps.put("flag", temp.getFlag());
	    		
	    		if (isfilt )
    			{
	    			boolean isvisable=false;
    				for (int n=0;n<personlist.size();n++)
    				{
    					PersonApps personApps=personlist.get(n);
    					if (personApps.getCompanyApps().getApps().getId().longValue()==sapp.getId().longValue())//个人定义的模块
    					{
    						isvisable=true;
    						break;
    					}
    				}
    				apps.put("usestate", isvisable);//当前模块使用状况，true为勾选false为不选
    			}
	    		else
	    		{
	    			apps.put("usestate", false);//当前模块使用状况，true为勾选false为不选
	    		}
	    		retsult.add(apps);
    		}
    	}
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	/**
	 * 设置个人使用的模块
	 */
	@HandlerMethod(methodName = ServletConst.SAVE_PERSONAL_APPS)
	public static String savePersonalApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 验证用
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	Users user=jqlService.getUsers(account);
    	List<List<Integer>> modellist = (List<List<Integer>>)param.get("modellist");//用户定义的模块列表，存储格式为数组，数组0为模块ID，数组1为模块顺序，顺序暂未用上，可全部传0或空
    	List<CompanyApps> comlist=(List<CompanyApps>)jqlService.findAllBySql("select a from CompanyApps as a where a.company.id=?", user.getCompany().getId());//获取单位的所有应用模块
    	//先删除个人用户的模块再添加
    	jqlService.excute("delete from PersonApps as a where a.user.id=?", user.getId());
    	if (modellist!=null && modellist.size()>0)
    	{
    		for (int i=0;i<modellist.size();i++)
    		{
    			List<Integer> values=modellist.get(i);//数组0为模块ID，数组1为模块顺序
    			for (int j=0;j<comlist.size();j++)
    			{
    				CompanyApps companyApps=comlist.get(j);
    				if (companyApps.getApps().getId().longValue()==values.get(0).longValue())
    				{
    					PersonApps personApps=new PersonApps();
    					personApps.setCompanyApps(companyApps);//对应公司模块
    					personApps.setUser(user);//添加用户
    					personApps.setCompanyId(user.getCompany().getId());
    					personApps.setModelid(companyApps.getApps().getId());
    					personApps.setModelname(companyApps.getApps().getName());
    					jqlService.save(personApps);
    				}
    			}
    		}
    	}
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, "设置成功");        
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
}
