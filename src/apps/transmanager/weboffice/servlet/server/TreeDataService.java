package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.util.server.JSONTools;

/**
 * 孙爱华临时增加的从office直接上传文件到服务器
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class TreeDataService extends HttpServlet
{

	public TreeDataService()
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Enumeration e = (Enumeration)request.getParameterNames();   
	    while(e.hasMoreElements())     {   
    		String parName=(String)e.nextElement();   
    		System.out.println(parName+"==="+request.getParameter(parName));   
	    }   
	    String id=request.getParameter("id");
	    String name=request.getParameter("name");
	    String action=request.getParameter("action");//===flowselectman
	    List<Map> list=new ArrayList<Map>();
	    
	    JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	    List<Organizations> orglist = jqlService.findAllBySql("select a from Organizations as a where a.parent.id=? ",Long.valueOf(id));
	    if (orglist!=null && orglist.size()>0)
	    {
	    	for (int i=0;i<orglist.size();i++)
	    	{
	    		Organizations org=orglist.get(i);
	    		Map map=new HashMap();
	    		map.put("id", org.getId());
	    		map.put("name", org.getName());
	    		map.put("halfCheck", true);
	    		map.put("isParent", true);
	    		list.add(map);
	    	}
	    }
	    List<Users> userlist = jqlService.findAllBySql("select a from Users as a,UsersOrganizations as b where a.id=b.user.id and b.organization.id=?",Long.valueOf(id));
	    String result="";
	    if (userlist!=null)
	    {
	    	
	    	for (int i=0;i<userlist.size();i++)
	    	{
	    		Users users=userlist.get(i);
	    		Map map=new HashMap();
	    		map.put("id", users.getId());
	    		//map.put("name", URLEncoder.encode(users.getRealName()));
	    		map.put("name", users.getRealName());
	    		map.put("isParent", false);
	    		list.add(map);
	    	}
	    	result=JSONTools.convertToJson(list);
	    	if (result!=null && result.length()>10)
	    	{
	    		int index=result.indexOf(":");
	    		if (index<0)
	    		{
	    			result="";
	    		}
	    		else
	    		{
	    			result=result.substring(index+1,result.length()-1);
	    		}
	    	}
	    	System.out.println(result);
	    }
	    response.setContentType("text/xml;charset=utf-8");
        response.getWriter().write(result);
        		//"[{ id:'05', name:'n1', isParent:true},{ id:'06', name:'n2', isParent:false},{ id:'07', name:'n3', isParent:true},{ id:'08', name:'n4', isParent:false}]"
        return;
	}
}
