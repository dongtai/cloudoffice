package apps.transmanager.weboffice.dwr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;



public class FlashTotalDwr 
{
	/** 保存更新文件的列表 */
	public static List<Map> filelist = new ArrayList<Map>();
	
	/**
	 * 将用户id和页面脚本session绑定
	 * @param userid
	 */
	public void setScriptSessionFlag(Map updatefilemap) {
		WebContextFactory.get().getScriptSession().setAttribute("flashfilelist", updatefilemap);
	}

	/**
	 * 根据用户id获得指定用户的页面脚本session
	 * @param userid
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ScriptSession getScriptSession(String filepath, HttpServletRequest request) {
		ScriptSession scriptSessions = null;
		Collection<ScriptSession> sessions = new HashSet<ScriptSession>();
		sessions.addAll(ServerContextFactory.get(request.getSession().getServletContext())
				.getScriptSessionsByPage("/static/open.jsp"));
		for (ScriptSession session : sessions) {
			Map tempmap = (Map) session.getAttribute("flashfilelist");
			if (tempmap.get("filepath") != null ) {
				scriptSessions = session;
			}
		}
		return scriptSessions;
	}
	
	/**
	 * 更新汇总表或自己文件
	 * @param updatefile 部门文件的路径
	 * @param request
	 */
	public Map updateFileList(String path,  HttpServletRequest request) {
		long currenttime=System.currentTimeMillis();
		for (int i=0;i<filelist.size();i++)
		{
			Map tempmap=(Map)filelist.get(i);
			if (tempmap!=null)
			{
				if ((Long)tempmap.get("updatetime")<currenttime)
				{
					filelist.remove(i);
				}
			}
		}
		Map updatefilemap=new HashMap();
		updatefilemap.put("filepath", path);
		updatefilemap.put("updatetime", currenttime);
		filelist.add(updatefilemap);
		
		this.setScriptSessionFlag(updatefilemap);

		//获得DWR上下文
		ServletContext sc = request.getSession().getServletContext();
		ServerContext sctx = ServerContextFactory.get(sc);
		//获得当前浏览 index.jsp 页面的所有脚本session
		Collection sessions = sctx.getScriptSessionsByPage("/static/open.jsp");
		Util util = new Util(sessions);
		//处理这些页面中的一些元素
//		util.removeAllOptions("users");
//		util.addOptions("users", users, "username");
//		util.removeAllOptions("receiver");
//		util.addOptions("receiver", users,"userid","username");
//		if(!flag){
//			return null;
//		}
//		刷新窗体
		
		util.addFunctionCall("flashwindow",path);//把当前改变的文件推到汇总文件页面，如果是汇总文件才刷新，否则不做处理
		return updatefilemap;
	}
}
