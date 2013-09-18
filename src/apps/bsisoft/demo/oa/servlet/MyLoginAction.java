package apps.bsisoft.demo.oa.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.bsisoft.app.security.servlet.Constants;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.UserService;

public class MyLoginAction extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8702873591034710399L;
	public static String usernametag="CN";
	public static String emailtag="E";
	public static String groupnametag="O";
	public static String subgroupnametag="OU";
	public static String dnstrtag="dn";
	public static String logincreate="N";
	public void init() throws ServletException {

		Properties ps =  Constants.getProperties();
		String usernameT = ps.getProperty("username");
		if (usernameT!=null)
		{
			usernametag=usernameT.trim();
		}
		String emailT = ps.getProperty("email");
		if (emailT!=null)
		{
			emailtag=emailT.trim();
		}
		String groupnameT = ps.getProperty("groupname");
		if (groupnameT!=null)
		{
			groupnametag=groupnameT.trim();
		}
		String subgroupnameT = ps.getProperty("subgroupname");
		if (subgroupnameT!=null)
		{
			subgroupnametag=subgroupnameT.trim();
		}
		String dnstrT = ps.getProperty("dn_attribute_name");
		if (dnstrT!=null)
		{
			dnstrtag=dnstrT.trim();
		}
		
		String logincreateT = ps.getProperty("logincreate");
		if (logincreateT!=null)
		{
			logincreate=logincreateT.trim();
		}
	}
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//取得用户DN 用于登录
		String dn = (String) request.getAttribute(dnstrtag);
		if (dn==null || dn.length()==0)
		{
			dn=(String)request.getSession().getAttribute(dnstrtag);
		}
		if (dn==null)
		{
			dn=request.getParameter(dnstrtag);//测试用的
		}
		if (dn==null || dn.length()==0)
		{
			response.sendRedirect("/index.jsp");
			return;
		}
		String context = request.getContextPath();
		dn=handleDN(dn);
		System.out.println("========= dn: "+dn);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		
		Users olduser=userService.getUserByCaId(dn);
		if (olduser==null)
		{
			olduser=userService.getUser(dn);
		}
		if (olduser==null && "Y".equals(logincreate))
		{
			String loginN = dn;
			String[] dnstrs=dn.split(",");
			
	        String emailAddress = System.currentTimeMillis()+"@yozosoft.com";
	    	String password = "123456";
	    	String rePassword = "123456";
	//        String companyID = BackApplicationParmeters.instance.user.getCompanyId();
	    	String name = dn;
	    	String orgName = "";                        
	    	String storageSize = "1024";
	    	String comboV = "";
	    	String orgCBV = "";
	    	String groupname="";
	    	String subgroupname="";
	    	if (dnstrs!=null)
			{
				for (int i=0;i<dnstrs.length;i++)
				{
					int index=dnstrs[i].indexOf("=");
					if (dnstrs[i].startsWith(usernametag+"="))//用户名
					{
						name = dnstrs[i].substring(index+1).trim();
					}
					if (dnstrs[i].startsWith(emailtag+"="))//邮件标识
					{
						String temp = dnstrs[i].substring(index+1).trim();
						if (temp.length()>0)
						{
							emailAddress=temp;
							loginN=temp;
						}
					}
					if (dnstrs[i].startsWith(groupnametag+"="))//组织名称
					{
						groupname = dnstrs[i].substring(index+1).trim();
					}
					if (dnstrs[i].startsWith(subgroupnametag+"="))//子组织名称
					{
						subgroupname=dnstrs[i].substring(index+1).trim();
					}
				}	
			}
	    	groupname+="/"+subgroupname;
	        float size = 0;
	        int sizeTemp = 0;
	        try
	        {
	            size = Float.parseFloat(storageSize);
	        }
	        catch(Exception e1)
	        {
	            
	        }
	        // 对数据进行四舍五入。
	        sizeTemp = (int)(size + 0.5);
	        
			Users user = new Users();
	        user.setCompanyId("public");
	        user.setUserName(loginN);//用邮件地址作为
	        //user.setEmail(emailAddress);       
	        user.setRealEmail(emailAddress); 
	        System.out.println("loginN===="+loginN);
	        System.out.println("emailAddress===="+emailAddress);
	        System.out.println("groupname==="+groupname);
	        
	        user.setPassW("123456");
	        user.setResetPass("123456");
	        user.setRealName(name);
	        user.setRole((short)0);
	        user.setStorageSize(new Float(sizeTemp));
	        user.setDuty(comboV);
	    	user.setRole(Short.valueOf("4"));
	    	user.setPartadmin(0);
	    	user.setCaId(dn);
	    	user.setLoginCA("1");
	
	        List list = new ArrayList();
	        if (groupname.length()>0)
	        {
		        groupname=groupname.replaceAll("//", "/");//有可能是双斜杠间隔
		        if (groupname.startsWith("/"))//去除开始斜杠
		        {
		        	groupname=groupname.substring(1);
		        }
		        if (groupname.endsWith("/"))//去除终止斜杠
		        {
		        	groupname=groupname.substring(0,groupname.length()-1);
		        }
	        }
	        Organizations org=userService.createGroups(groupname);//创建组织，其中组织是以/间隔的,已经存在就不创建
	        if (org==null)
	        {
		        org=userService.getFirstGroup();
	        }
	        if (org!=null)
	        {
		        Long orgID = org.getId();
		        list.add(orgID);
	        }
	        
	        userService.addOrUpdateUser(user,list,null,null,null);
	        request.getSession().setAttribute("calogin", dn);
	        response.sendRedirect("/static/UploadService?action=sessionloginCA");
		}
		else
		{
			request.getSession().setAttribute("calogin", dn);
			response.sendRedirect("/static/UploadService?action=sessionloginCA");
		}
//		response.getWriter().write("<html><body><ceneter><b>"+dn+"</b></ceneter></body></html>");
	}
	
	public static  String handleDN(String dn) {
		
		if(dn == null){
			return null;
		}
		
		String str = "";
		
		if(dn.startsWith("C=")){
			//倒序，
			String[] array = dn.split(",");
			for(int i=array.length-1;i>=0;i--){
				str += array[i];
				if(i != 0){
					str += ",";
				}
			}
		}else{
			
			str = dn;
		}
		
		str = str.replaceAll("^.*?CN=", "CN=");	
		
		return str;
	}

	private String getName(String strname)
	{
		if (strname!=null && strname.length()>0)
	    {
			strname=strname.replaceAll("//", "/");//有可能是双斜杠间隔
	        if (strname.startsWith("/"))//去除开始斜杠
	        {
	        	strname=strname.substring(1);
	        }
	        if (strname.endsWith("/"))//去除终止斜杠
	        {
	        	strname=strname.substring(0,strname.length()-1);
	        }
	    }
		return strname;
	}
//	private void samedata(HttpServletRequest req, HttpServletResponse response)
//	{
//		String action=WebTools.converStr(req.getParameter("action"));
//		String serverid=WebTools.converStr(req.getParameter("serverid"));//服务器编号
//		if (servercode.equals(serverid))//只有允许的服务器编号才允许操作
//		{
//			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
//			if ("sameuser".equals(action))//同步用户
//			{
//				String caid=WebTools.converStr(req.getParameter("caid"));//卡号
//				String realname=WebTools.converStr(req.getParameter("realname"));//用户中文名
//				String realEmail=WebTools.converStr(req.getParameter("realEmail"));//邮件地址
//				String groupname=WebTools.converStr(req.getParameter("groupname"));//组织名称
//				String groupcode=WebTools.converStr(req.getParameter("groupcode"));//组织唯一编号
//				groupname=getName(groupname);//去除不必要的斜杠
//				Organizations org=userService.modifyGroups(groupname,groupcode);//创建组织，其中组织是以/间隔的,已经存在就不创建
//				
//				
//				Users olduser=userService.getUserByCaId(caid);
//				if (olduser!=null)
//				{
//					olduser.setRealName(realname);
//					olduser.setRealEmail(realEmail);
//					List list = new ArrayList();
//			        if (org!=null)
//			        {
//			        	list.add(org.getId());
//			        }
//					userService.addOrUpdateUser(olduser,list,null,null,null);//更新用户和用户组织
//				}
//				else
//				{
//					Users user = new Users();
//			        user.setCompanyId("public");
//			        user.setUserName(caid);//用邮件地址作为
//			        //user.setEmail(emailAddress);       
//			        user.setRealEmail(realEmail); 
//			        user.setPassW("123456");
//			        user.setResetPass("123456");
//			        user.setRealName(realname);
//			        user.setRole((short)0);
//			        user.setStorageSize(new Float(1024));
//			        user.setDuty("");
//			    	user.setRole(Short.valueOf("4"));
//			    	user.setPartadmin(0);
//			    	user.setCaId(caid);
//			    	user.setLoginCA("1");
//			        List list = new ArrayList();
//			        if (org!=null)
//			        {
//			        	list.add(org.getId());
//			        }
//			        
//			        userService.addOrUpdateUser(user,list,null,null,null);
//				}
//		        
//				
//				
////暂不考虑角色同步
////				String rolename=WebTools.converStr(req.getParameter("rolename"));//角色名称
////				String rolecode=WebTools.converStr(req.getParameter("rolecode"));//角色唯一编号
//				
//			}
//			else if ("deluser".equals(action))//删除用户
//			{
//				String caid=WebTools.converStr(req.getParameter("caid"));//卡号
//				userService.modifyObject("delete from Users as a where a.caId=? ",caid);//组织关联会自动删除
//			}
//			else if ("delgroup".equals(action))//删除组织
//			{
//				String groupcode=WebTools.converStr(req.getParameter("groupcode"));//组织唯一编号
//				userService.modifyObject("delete from Organizations as a where a.depid=? ",groupcode);//组织关联会自动删除
//			}
//			else if ("delrole".equals(action))//删除角色
//			{
//				
//				String rolecode=WebTools.converStr(req.getParameter("rolecode"));//角色唯一编号
//			}
//		}
//	}

}
