package apps.transmanager.weboffice.service.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import apps.moreoffice.annotation.HandlerMethod;
import apps.moreoffice.annotation.ServerHandler;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.databaseobject.NewsInfo;
import apps.transmanager.weboffice.databaseobject.SystemLogs;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.WebInfo;
import apps.transmanager.weboffice.domain.SysMonitorInfoBean;
import apps.transmanager.weboffice.domain.SysReportBean;
import apps.transmanager.weboffice.service.IWebInfoService;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.impl.WebInfoService;
import apps.transmanager.weboffice.service.server.LogServices;
import apps.transmanager.weboffice.service.sysreport.SysMonitor;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;

@ServerHandler
public class LogHandler
{
		
	/**删除某段时间之前的日志
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_LOGS)
	public static String deleteLogs(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;
    	temp = (String)param.get("type");
    	Integer type = temp != null ? Integer.valueOf(temp) : null;         // 0或无该参数为所有类型，1为访问类型，2为文件操作类型
    	if (type != null)
    	{
    		type = type == 0 ? null : -type;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	
    	String ip = (String)param.get("ip");
    	List<String> ids = (List<String>)param.get("userIds");
    	//String ids = (String)param.get("userIds");
    	List<Long> userIds = null;
    	if (ids != null && ids.size() > 0)
    	{
    		userIds = new ArrayList<Long>();
    		for (String t : ids)
    		{
    			userIds.add(Long.valueOf(t));
    		}
    	} 
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	logServices.deleteLogs(companyId, type, startD, endD, ip, userIds); 			
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
//    	return error;
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	
	/** 获取某时间段的访问数量
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ACCESS_LOGS_COUNT)
	public static String getAccessCount(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;
    	    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    		
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	Long count = logServices.getAccessCount(companyId, startD, endD);
    			
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, count); 
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	
	/** 获取日志访问数量基本值
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_ACCESS_INFO)
	public static String getAccessInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");       // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;    	    	
    	    	
    	LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME);     	
    	Date startDate = new Date();
    	startDate.setHours(0);
    	startDate.setMinutes(0);
    	startDate.setSeconds(0);
    	Date endDate = new Date();
    	endDate.setHours(23);
    	endDate.setMinutes(59);
    	endDate.setSeconds(59);
    	Long count = logServices.getAccessCount(companyId, startDate, endDate);    			
    	ret.put("今日访问量", String.valueOf(count));
    	startDate.setDate(1);
    	endDate.setDate(31);     //   简单处理，暂时不处理大小月的问题
    	count = logServices.getAccessCount(companyId, startDate, endDate);    			
    	ret.put("本月访问量", String.valueOf(count));
    	startDate.setMonth(0);
    	endDate.setMonth(11);
    	count = logServices.getAccessCount(companyId, startDate, endDate);    			
    	ret.put("今年访问量", String.valueOf(count));
    	count = logServices.getAccessCount(companyId, null, null);    			
    	ret.put("总访问量", String.valueOf(count));
    	count = logServices.getAllDay(companyId);
    	ret.put("统计天数", String.valueOf(count));
    	count = logServices.getAccessCount(companyId, null, null)/logServices.getAllDay(companyId);
    	ret.put("平均日访问量", String.valueOf(count));
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret); 
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	/**最近访问日志
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_LASTEST_LOG)
	public static String getLastestLog(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;
    	
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : 10;
    		
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<SystemLogs> logs = logServices.getLastestLog(companyId, LogConstant.TYPE_ONLINE, count);
    	List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    	HashMap<String, String> ret;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	for (SystemLogs t : logs)
    	{
    		ret = new HashMap<String, String>();
    		ret.put("user", t.getUser().getUserName() + "(" + t.getUser().getRealName() + ")");
    		ret.put("company", t.getCompany().getName());
			ret.put("type", LogConstant.get(t.getType()));
			ret.put("operType", LogConstant.get(t.getOperType()));
			ret.put("startDate", sdf.format(t.getStartDate()));
			ret.put("ip", t.getIp());
			ret.put("content", t.getContent());
			result.add(ret);
    	}    	
    	
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	/**查询系统日志
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SEARCH_LOG)
	public static String getSearchLogs(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;
    	temp = (String)param.get("type");
    	Integer type = temp != null ? Integer.valueOf(temp) : null;         // 0或无该参数为所有类型，1为访问类型，2为文件操作类型
    	if (type != null)
    	{
    		type = type == 0 ? null : -type;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	
    	String ip = (String)param.get("ip");
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	//String sort = (String)param.get("sort");  
    	//if (sort==null || "undefined".equals(sort))//为了兼容EXT的分页显示用的,但是加上会产生点击无用的情况，所以去掉
    	//{
    	String	sort=req.getParameter("sort");
    	//}
    	//String dir = (String)param.get("dir");    // 排序的方式（asc或desc）
    	//if (dir==null || "undefined".equals(dir))//为了兼容EXT的分页显示用的,但是加上会产生点击无用的情况，所以去掉
    	//{
    	String dir=req.getParameter("dir");
    	//}
    	List<String> ids = (List<String>)param.get("userIds");
    	
    	//String ids = (String)param.get("userIds");
    	List<Long> userIds = null;
    	if (ids != null && ids.size() > 0)
    	{
    		userIds = new ArrayList<Long>();
    		for (String t : ids)
    		{
    			userIds.add(Long.valueOf(t));
    		}
    	}  
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<SystemLogs> logs = logServices.getSearchLogs(companyId, type, startD, endD, ip, userIds, start, count, sort, dir);
    	List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    	HashMap<String, String> ret;
    	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	long size = logServices.getSearchLogsCount(companyId, type, startD, endD, ip, userIds);
    	for (SystemLogs t : logs)
    	{
    		ret = new HashMap<String, String>();
    		ret.put("userName", t.getUser().getUserName() );
    		ret.put("realName", t.getUser().getRealName());
    		if (t.getCompany() != null)
			{
	    		ret.put("company", t.getCompany().getName());        // 先这样处理
			}
			else
			{
				ret.put("company","");
			}
			ret.put("type", LogConstant.get(t.getType()));
			ret.put("operType", LogConstant.get(t.getOperType()));
			ret.put("startDate", sdf.format(t.getStartDate()));
			if (t.getEndDate() != null)
			{
				ret.put("endDate", sdf.format(t.getEndDate()));
			}
			else
			{
				ret.put("endDate", "");
			}
			ret.put("ip", t.getIp());
			
			String filename = "";
			String filepath = "";
			if(t.getContent().indexOf('/')>=0){
				filename = t.getContent().substring(t.getContent().lastIndexOf('/')+1);
				filepath = t.getContent();
			}
			ret.put("filename", filename); 
			ret.put("filepath", filepath); 
			result.add(ret);
			
    	}
    	HashMap<String, Object> back = new HashMap<String, Object>();
    	back.put("list", result);
    	back.put("total", size);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	/**
	 * 导出日志，以文本文件方式
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.EXPORT_SEARCH_LOGS)
	public static String exportSearchLogs(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");     // 后续验证用		
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;
    	temp = (String)param.get("type");
    	Integer type = temp != null ? Integer.valueOf(temp) : null;         // 0或无该参数为所有类型，1为访问类型，2为文件操作类型
    	if (type != null)
    	{
    		type = type == 0 ? null : -type;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	String ip = (String)param.get("ip");
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	String order = (String)param.get("order");     // 值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
    	String dir = (String)param.get("dir");
    	List<String> ids = (List<String>)param.get("userIds");    	
    	List<Long> userIds = null;
    	if (ids != null && ids.size() > 0)
    	{
    		userIds = new ArrayList<Long>();
    		for (String t : ids)
    		{
    			userIds.add(Long.valueOf(t));
    		}
    	}    	
    		
    	resp.setCharacterEncoding("utf-8");
		resp.setContentType("application/octet-stream");
    	//resp.setContentType("text/plain;charset=UTF-8");
		resp.setHeader("Content-Disposition", "attachment;filename=\"logs.txt\"");
		
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<SystemLogs> logs = logServices.getSearchLogs(companyId, type, startD, endD, ip, userIds, start, count, order, dir);
    	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	OutputStream oos = resp.getOutputStream();
    	StringBuffer sb;
    	if(type ==-1){
		for (SystemLogs t : logs)
		{
			try
			{
				sb = new StringBuffer();
				sb.append("访问者:");
				sb.append(t.getUser().getUserName());
				sb.append(";");
				sb.append("姓名:");
				sb.append(t.getUser().getRealName());
				sb.append(";");
				
				sb.append("公司:");
				if(t.getCompany()!=null){
				if (t.getCompany() != null)
				{
					sb.append(t.getCompany().getName());
				}
				else
				{
					sb.append("");
				}
				}else{
					sb.append("");	
				}
				sb.append(";");
				sb.append("登录时间:");
				sb.append(sdf.format(t.getStartDate()));
				sb.append(";");
				sb.append("退出时间:");
				if(t.getEndDate()!=null){
				sb.append(sdf.format(t.getEndDate()));
				}
				else sb.append(""); 
				sb.append(";");
				sb.append(LogConstant.get(LogConstant.IP_TIP));
				sb.append(t.getIp());
				sb.append("\r\n");
				oos.write(sb.toString().getBytes());
				
			}
			catch(Exception e)
			{
				LogsUtility.error(e);
			}
		}
    	}
    	else if(type==-2){
    		for (SystemLogs t : logs)
    		{
    			try
    			{
    				sb = new StringBuffer();
    				sb.append("访问者:");
    				sb.append(t.getUser().getUserName());
    				sb.append(";");
    				sb.append("姓名:");
    				sb.append(t.getUser().getRealName());
    				sb.append(";");
    				
    				sb.append("公司:");
    				if(t.getCompany()!=null){
    				sb.append(t.getCompany().getName());
    				}else{
    					sb.append("");	
    				}
    				sb.append(";");
    				sb.append("文件名:");
    				String filename = "";
    				String filepath = "";
    				if(t.getContent().indexOf('/')>=0){
    					filename = t.getContent().substring(t.getContent().lastIndexOf('/')+1);
    					filepath = t.getContent();
    				}
    				sb.append(filename);
    				sb.append(";");
    				sb.append("文件路径:");
    				sb.append(filepath);
    				sb.append(";");
    				sb.append("时间:");
    				sb.append(sdf.format(t.getStartDate())); 
    				sb.append(";");
    				sb.append(LogConstant.get(LogConstant.OPE_TYPE_TIP));
    				sb.append(LogConstant.get(t.getOperType()));
    				sb.append("\r\n");
    				oos.write(sb.toString().getBytes());
    			}
    			catch(Exception e)
    			{
    				LogsUtility.error(e);
    			}
    		}
    	}
		oos.close();
		return null;
	}
	
	/**查询用户登录退出日志
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_LOGIN)
	public static String getUserLoginLogs(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	String ip = (String)param.get("ip");
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	String order = (String)param.get("order");     // 值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
    	String dir = (String)param.get("dir");
    	List<String> ids = (List<String>)param.get("userIds");    	
    	List<Long> userIds = null;
    	if (ids != null && ids.size() > 0)
    	{
    		userIds = new ArrayList<Long>();
    		for (String t : ids)
    		{
    			userIds.add(Long.valueOf(t));
    		}
    	}    	
    		
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<SystemLogs> logs = logServices.getUserLoginLogs(companyId, startD, endD, ip, userIds, start, count, order, dir);
    	List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    	HashMap<String, String> ret;
    	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//Object[] ob;
    	for (SystemLogs t : logs)
    	{
    		//u.userName, u.realName, s.startDate, a.startDate, s.ip, s.company_id		
    		//ob = (Object[])t;
    		ret = new HashMap<String, String>();
    		ret.put("user", t.getUser().getUserName());
             ret.put("realname", t.getUser().getRealName());
			ret.put("type", LogConstant.get(t.getType()));
			ret.put("startDate", sdf.format(t.getStartDate()));
			if (t.getEndDate() != null)
			{
				ret.put("endDate", sdf.format(t.getEndDate()));
			}
			else
			{
				ret.put("endDate", "");
			}
			ret.put("ip", t.getIp());  
			ret.put("content", LogConstant.get(LogConstant.OPER_TYPE_LOGIN) + LogConstant.get(LogConstant.OPER_TYPE_LOGOUT));
			if (t.getCompany() != null)
			{
	    		ret.put("company", t.getCompany().getName());        // 先这样处理
			}
			else
			{
				ret.put("company","");
			}
			result.add(ret);
    	}    	
    			
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
//       
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	
	/**查询用户登录退出总数量
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_USER_LOGIN_COUNT)
	public static String getUserLoginLogsCount(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception
	{
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	Long companyId = temp != null ? Long.valueOf(temp) : null;    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	String ip = (String)param.get("ip");
    	List<String> ids = (List<String>)param.get("userIds");    	
    	List<Long> userIds = null;
    	if (ids != null && ids.size() > 0)
    	{
    		userIds = new ArrayList<Long>();
    		for (String t : ids)
    		{
    			userIds.add(Long.valueOf(t));
    		}
    	}    	
    		
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	Long count = logServices.getUserLoginLogsCount(companyId, startD, endD, ip, userIds);
    			
    	return  JSONTools.convertToJson(ErrorCons.NO_ERROR, count); 
//       
//	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    resp.getWriter().write(error);
	}
	

	@HandlerMethod(methodName = ServletConst.GET_DEP_LOGS)
	public static String getDepLogs(HttpServletRequest request,HttpServletResponse response, HashMap<String, Object> jsonParams) throws  ServletException, Exception {
//		String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	String companyId = temp;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	temp = (String)param.get("start");
    	int start = temp != null ? Integer.valueOf(temp) : -1;
    	temp = (String)param.get("count");
    	int count = temp != null ? Integer.valueOf(temp) : -1;
    	//String sort = (String)param.get("sort");  // depName
    	//if (sort==null || "undefined".equals(sort))//为了兼容EXT的分页显示用的
    	//{
    	 String	sort=request.getParameter("sort");
    	//}
    	//String dir = (String)param.get("dir");    // 排序的方式（asc或desc）
    	//if (dir==null || "undefined".equals(dir))//为了兼容EXT的分页显示用的,但是加上会产生点击无用的情况，所以去掉
    	//{
    	String dir=request.getParameter("dir");
    	//}
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<HashMap<String, String>> Deplogs = logServices.getDepLogs(companyId, startD, endD,start,count,sort,dir);
    	int TotalSize = logServices.getDepLogsCount(companyId, startD, endD);
    	HashMap<String, Object> back = new HashMap<String, Object>();
    	back.put("list", Deplogs);
    	back.put("total", TotalSize);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, back); 
//    	response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//    	response.getWriter().write(error);
		
	}


	@HandlerMethod(methodName = ServletConst.EXPORT_DEP_LOGS)
	public static String exportDepLogs(HttpServletRequest request,HttpServletResponse response, HashMap<String, Object> jsonParams) throws  ServletException, Exception {
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 
    	String temp = (String)param.get("companyId");
    	String companyId = temp;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	temp = (String)param.get("startDate");
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
    	;
    	response.setCharacterEncoding("utf-8");
    	response.setContentType("application/octet-stream");
    	response.setHeader("Content-Disposition", "attachment;filename=\"logs.txt\"");
    	LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME); 
    	List<HashMap<String, String>> Deplogs = logServices.getDepLogs(companyId, startD, endD,-1,-1,null,null);
    	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	OutputStream oos = response.getOutputStream();
    	StringBuffer sb;
    	for (HashMap<String, String> t : Deplogs)
		{
			try
			{
				sb = new StringBuffer();
				sb.append("单位:");
				sb.append(t.get("depName"));
				sb.append(";");
				sb.append("起始日期:");
				sb.append(t.get("startDate"));
				sb.append(";");
				
				sb.append("结束日期:");
				sb.append(t.get("endDate"));	
				sb.append(";");
				sb.append("总人数:");
				sb.append(t.get("totalUser"));
				sb.append(";");
				sb.append("访问次数:");
				sb.append(t.get("totalNumber"));
				sb.append(";");
				sb.append("访问人数:");
				sb.append(t.get("visitUser"));
				sb.append(";");
				sb.append("访问比例:");
				sb.append(t.get("visitPercent"));
				sb.append("\r\n");
				oos.write(sb.toString().getBytes());
				
			}
			catch(Exception e)
			{
				LogsUtility.error(e);
			}
		}
    	oos.close();
    	return null;
	}
	
	////////////////////////////以下为系统监控部分
	//获得系统监控信息
		//获得用户信息和文档信息
	@HandlerMethod(methodName = ServletConst.GET_SYS_MONITOR)
	public static String getSysMonitorInfo(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams)
					throws ServletException, IOException {
//			String error;  
		    HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		    String account = (String)param.get("account");   // 

		    SysMonitorInfoBean infoBean = LogServices.getSysMonitorInfo(request);  //系统监控信息存入了SysMonitorInfoBean

		    LinkedHashMap<String, String> retsult = new LinkedHashMap<String, String>();
		    
		    retsult.put("公司名称",infoBean.getCompanyName());
		    retsult.put("部门数量", infoBean.getDepartmentCount());
		    retsult.put("账号数量", infoBean.getAccountCount());
		    retsult.put("在线账号", infoBean.getOnlineAccount());
		    retsult.put("空间数量", infoBean.getSpaceCount());
		    retsult.put("部门空间数量", infoBean.getDepartmentSpaceCount());
		    retsult.put("项目空间数量", infoBean.getProjectSpaceCount());
		    retsult.put("文档数量", infoBean.getDocumentCount());
		    retsult.put("文档库容量", infoBean.getContentSize());
		    
	    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);
//	    	response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//	    	response.getWriter().write(error);    	
		    
		 }

		 //获得服务器状态
	@HandlerMethod(methodName = ServletConst.GET_SYS_STATUS)
	public static String getSysStatusInfo(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams)
					throws ServletException, IOException{

			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			String account = (String)param.get("account");   // 
			LinkedHashMap<String, String> retsult = new LinkedHashMap<String, String>();
			
			SysMonitorInfoBean infoBean = LogServices.getSysMonitorInfo(request);  //系统监控信息存入了SysMonitorInfoBean
			 
			//系统信息
			retsult.put("IP", infoBean.getServerIP());
			retsult.put("域名", infoBean.getServerDomain());
			retsult.put("名称", infoBean.getServerName());
			
			int ratioCpu = (int)(infoBean.getCpuRatio() * 100);  //cpu使用率
			String strCpu = "使用" + ratioCpu+ "%";
			retsult.put("cpu使用率", strCpu);
			
			
			float a = Float.parseFloat(infoBean.getUsedCpuHZ().split("GHz")[0]);
			float b = Float.parseFloat(infoBean.getCpuHZ().split("GHz")[0]);
			int ratioCpuHZ =(int)((a/b)*100);  //cpu频率
			String strCpuHZ = "使用"+ratioCpuHZ+"%" + infoBean.getUsedCpuHZ() + "/" +infoBean.getCpuHZ();
			retsult.put("cpu频率", strCpuHZ);
			
	        int rationMemory = (int)(infoBean.getMemoryRatio() * 100);   //物理内存
	        String strMemory = "使用" + rationMemory + "%;" 
	                + infoBean.getUsedMemory() + "/" + infoBean.getTotalMemorySize();
	        retsult.put("物理内存", strMemory);
	                                                     
	        int ratioStorage = (int)(infoBean.getDiskRatio() * 100);   //存储
	        String strStorage = "使用" + ratioStorage + "%；" + infoBean.getDiskUsedSize() + "/" + infoBean.getDiskSize();
	        retsult.put("存储", strStorage);
	        
	        //用户信息和文档信息
//		    retsult.put("公司名称",infoBean.getCompanyName());
//		    retsult.put("部门数量", infoBean.getDepartmentCount());
//		    retsult.put("账号数量", infoBean.getAccountCount());
//		    retsult.put("在线账号", infoBean.getOnlineAccount());
//		    retsult.put("空间数量", infoBean.getSpaceCount());
//		    retsult.put("部门空间数量", infoBean.getDepartmentSpaceCount());
//		    retsult.put("项目空间数量", infoBean.getProjectSpaceCount());
//		    retsult.put("文档数量", infoBean.getDocumentCount());
//		    retsult.put("文档库容量", infoBean.getContentSize());
		    
		    return JSONTools.convertToJson(ErrorCons.NO_ERROR, retsult);

		 }
	
	@HandlerMethod(methodName = ServletConst.GET_SYS_ONLINE_ACCOUNT)
	public static String getSysOnlineAccount(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams)
					throws ServletException, IOException{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			String account = (String)param.get("account");   // 
			String starttemp =  (String)param.get("start");
			String counttemp =  (String)param.get("count");

			int start = starttemp != null ? Integer.valueOf(starttemp) : -1;
			int count = counttemp != null ? Integer.valueOf(counttemp) : -1;

			List<HashMap<String, String>> result1 = new ArrayList<HashMap<String, String>>();
			List<String> usersOnlineList = UserOnlineHandler.getActivedUser();  //获得在线用户列表
			
			//为了分页
			ArrayList<String> listPage = new ArrayList<String>(); 
			int j=start;
			//判断 ：j < usersOnlineList.size()，是为了防止填充不满页面的情况
			for(int i=0; i < count && j < usersOnlineList.size(); i++){
				String str = usersOnlineList.get(j++);
				listPage.add(str);
			}
			
			int TotalSize = usersOnlineList.size();  //总数目
			
			for(int i=0;i<listPage.size();i++)
			{
				String name = listPage.get(i).split(";")[0];   //获得访问者名字		
				String ip = listPage.get(i).split(";")[1];  //获得访问者ip
				HashMap<String, String> ret = new  HashMap<String, String>();
				if(name!=null && ip!=null){
					ret.put("name", name);
					ret.put("ip", ip);
				}
				result1.add(ret);
			}
			
			HashMap<String, Object> back = new HashMap<String, Object>();
			back.put("list", result1);
			back.put("total", TotalSize);
			
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, back);	
		 }
			 
	@HandlerMethod(methodName = ServletConst.GET_SYS_REPORT)
	public static String getSysReportInfo(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams)
					throws ServletException, Exception{
		    HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		    String account = (String)param.get("account");   
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	String temp = (String)param.get("startDate");

			String type = (String)param.get("periodType");  //类型
			int cycleType=0;
//			if(type.equals("天")) {cycleType=0;}
//			else if(type.equals("周")) {cycleType = 1;}
//			else if(type.equals("月")) {cycleType = 2;}
			
			if(type.equals("day")) {cycleType=0;}
			else if(type.equals("week")) {cycleType = 1;}
			else if(type.equals("month")) {cycleType = 2;}
			
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
	    	temp = (String)param.get("start");
	    	int start = temp != null ? Integer.valueOf(temp) : -1;
	    	temp = (String)param.get("count");
	    	int count = temp != null ? Integer.valueOf(temp) : -1;

	    	String uid="1";//userID 没有用到，设置为空 或者某个值？？

	    	List<SysReportBean> sysReport1 = SysMonitor.instance().getSysReportBean(uid, startD, endD, cycleType,start,count);   
	    	
	    	int totalSize = SysMonitor.instance().getSysReportCount(startD,endD,cycleType);
	    	
			List<HashMap<String, String>> result1 = new ArrayList<HashMap<String, String>>();
			
			for(int i=0;i<sysReport1.size();i++){
				SysReportBean srb = sysReport1.get(i);
				String date = srb.getReportdate();   
				String usersnum =Long.toString(srb.getAccountCount());
				String spacenum = Long.toString(srb.getSpaceCount());
				String docnum = Long.toString(srb.getDocumentCount());
				String doccontentsize = SysMonitor.instance().formatSpace(srb.getContentSize());
				HashMap<String, String> ret = new  HashMap<String, String>();
				if(date!=null && usersnum!=null && spacenum!=null && docnum!=null && doccontentsize!=null){
					ret.put("date", date);
					ret.put("accountcount", usersnum);
					ret.put("spacecount", spacenum);
					ret.put("documentcount", docnum);
					ret.put("contentsize", doccontentsize);
				}
				result1.add(ret);
			}

	    	HashMap<String, Object> back = new HashMap<String, Object>();
	    	back.put("list", result1);
	    	back.put("total", totalSize);
	    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, back); 
			 
		 }
 
		 
			/***
			 * 方法参考自，SysMonitor.java中的exportSysReport()方法，并将formatSpace()方法改为public
			 * @param request
			 * @param response
			 * @param jsonParams
			 * @return
			 * @throws ServletException
			 * @throws Exception
			 */
	@HandlerMethod(methodName = ServletConst.SYS_REPORT_EXPORT_ACTION)
	public static String systeReportExport(HttpServletRequest request,HttpServletResponse response, HashMap<String, Object> jsonParams) 
			throws  ServletException, Exception {
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
		    	
				long startDate = startD.getTime(); 
				long endDate = endD.getTime();      //转换为long型
				
		    	String type = (String)param.get("periodType");  //类型
				int cycleType=0;
//				if(type.equals("天")) {cycleType=0;}
//				else if(type.equals("周")) {cycleType = 1;}
//				else if(type.equals("月")) {cycleType = 2;}
				
				if(type.equals("day")) {cycleType=0;}
				else if(type.equals("week")) {cycleType = 1;}
				else if(type.equals("month")) {cycleType = 2;}
				
		    	response.setCharacterEncoding("utf-8");
		    	response.setContentType("application/octet-stream");
		    	response.setHeader("Content-Disposition", "attachment;filename=\"sysreport.xls\"");
		    	
		    	String userID = "1";   //userID,设置为空？ 或者为某特定值？ 程序中对它并无太大要求？
		    	
		        List<SysReportBean> sysReportBean = SysMonitor.instance().getSysReportBean(userID, startD, endD, cycleType);
		        if (sysReportBean == null || sysReportBean.isEmpty())
		        {
		            return null;
		        }
		        OutputStream oos = response.getOutputStream();
		        try{
		        	HSSFWorkbook workbook = new HSSFWorkbook();
	                HSSFSheet sheet = workbook.createSheet();
	                int rowIndex = 1;
	                short cellIndex = 1;
	                HSSFRow row = sheet.createRow(rowIndex++);
	                HSSFCell cell = row.createCell(cellIndex++);
	                // 日期
	                cell.setCellValue(new HSSFRichTextString("报表日期"));
	                // 用户数
	                cell = row.createCell(cellIndex++);
	                cell.setCellValue(new HSSFRichTextString("用户数"));
	                // 空间数
	                cell = row.createCell(cellIndex++);
	                cell.setCellValue(new HSSFRichTextString("空间数"));
	                // 文档数
	                cell = row.createCell(cellIndex++);
	                cell.setCellValue(new HSSFRichTextString("文档数"));
	                // 文档库容量
	                cell = row.createCell(cellIndex++);
	                cell.setCellValue(new HSSFRichTextString("文档库容量"));
	                
	                int size = sysReportBean.size();
	                for (int i = 0; i < size; i++)
	                {
	                    SysReportBean srb = sysReportBean.get(i);
	                    cellIndex = 1;
	                    row = sheet.createRow(rowIndex++);
	                    // 日期
	                    cell = row.createCell(cellIndex++);
	                    cell.setCellValue(new HSSFRichTextString(srb.getReportdate()));
	                    // 用户数
	                    cell = row.createCell(cellIndex++);
	                    cell.setCellValue(srb.getAccountCount());
	                    // 空间数
	                    cell = row.createCell(cellIndex++);
	                    cell.setCellValue(srb.getSpaceCount());
	                    // 文档数
	                    cell = row.createCell(cellIndex++);
	                    cell.setCellValue(srb.getDocumentCount());
	                    // 文档库容量
	                    cell = row.createCell(cellIndex++);
	                    cell.setCellValue(new HSSFRichTextString(SysMonitor.instance().formatSpace(srb.getContentSize())));
	                }
	                workbook.write(oos);
		        }catch(Exception e){
		        	e.printStackTrace();
		        }finally{
		        	 oos.close();
		        }
		    	return null;
			}
			
	/////////////
	@HandlerMethod(methodName = ServletConst.IMP_FLOWINFO)
	public static String impFlowinfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
	throws ServletException, Exception
	{//解析流程数据
//		String error;  
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");   // 
		//先拿文件再解析

		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
		
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	
	///////////////////////////////与新闻订阅相关的处理
	/**
	 * 后台管理使用，添加或者更新一个订阅栏目
	 * 
	 * 传入的参数为webname(订阅栏目名称)、url(订阅栏目的url)、category(订阅栏目的分类，如：时政新闻)
	 * 传入的参数为isrss(是否是rss，是的话从前端传来yes；否则，传来no)、newslist(订阅栏目的新闻列表div,形如div==id==newslist...)
	 * 传入的参数为picpath(订阅栏目的Logo图像路径)、mainarea(订阅栏目的主要区域div,形如div==id==newslist)
	 * 传入的参数为timearea(订阅栏目的标题区域div)、titlearea(订阅栏目的标题区域div)、
	 * 传入的参数为contentarea(订阅栏目的内容区域div)
	 */
	@HandlerMethod(methodName = ServletConst.ADD_WEBINFO)
	public static String addWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		
		String webname =  (String)param.get("webname");   
		String url =  (String)param.get("url");   
		String category =  (String)param.get("category");   
		
		String isrss = (String)param.get("isrss");  //是否是rss，是的话从前端传来yes；否则，传来no
		boolean isrssOrnot;
		if(isrss.equals("yes")) isrssOrnot=true;
		else  isrssOrnot=false;
		
		String newslist = (String)param.get("newslist");    
		String picpath = (String)param.get("picpath");      
		String mainarea =  (String)param.get("mainarea");   
		String timearea =  (String)param.get("timearea");   
		String titlearea =  (String)param.get("titlearea");    
		String contentarea =  (String)param.get("contentarea");   
		Long num = Long.valueOf(0);   //初始化为0
		
		WebInfo webinfo  = new WebInfo();
		webinfo.setWebname(webname);
		webinfo.setUrl(url);
		webinfo.setCategory(category);
		webinfo.setIsrss(isrssOrnot);
		webinfo.setNewslist(newslist);
		webinfo.setPicpath(picpath);
		webinfo.setMainarea(mainarea);
		webinfo.setTimearea(timearea);
		webinfo.setTitlearea(titlearea);
		webinfo.setContentarea(contentarea);
		webinfo.setNum(num);
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);

		webinfoService.addWebInfo(webinfo);  //添加一个订阅栏目
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
		
	}

	
	//更新一个订阅表
	@HandlerMethod(methodName = ServletConst.UPDATE_WEBINFO)
	public static String updateWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		
		Long id = Long.parseLong((String)param.get("id"));
		
		String isrss = (String)param.get("isrss");  //是否是rss，是的话从前端传来yes；否则，传来no
		boolean isrssOrnot;
		if(isrss.equals("yes")) isrssOrnot=true;
		else  isrssOrnot=false;
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		WebInfo webinfo = webinfoService.getWebInfoById(id);
		webinfo.setWebname((String)param.get("webname"));
		webinfo.setUrl((String)param.get("url"));
		webinfo.setCategory((String)param.get("category"));
		webinfo.setIsrss(isrssOrnot);
		webinfo.setNewslist((String)param.get("newslist"));
		webinfo.setPicpath((String)param.get("picpath"));
		webinfo.setMainarea((String)param.get("mainarea"));
		webinfo.setTimearea((String)param.get("timearea"));
		webinfo.setTitlearea((String)param.get("titlearea"));
		webinfo.setContentarea((String)param.get("contentarea"));
		webinfoService.addWebInfo(webinfo);  //添加一个订阅栏目
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
		
	}
	
	/**
	 * 后台管理使用，删除一个订阅栏目
	 * 传入的参数为栏目的id
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_WEBINFO)
	public static String delWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		Long id = Long.parseLong((String)param.get("id")); //传过来的栏目的id
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.delWebInfo(id);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}

	/**
	 * 后台管理使用，删除一个订阅栏目
	 * 传入的参数为栏目的id
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_WEBINFO_LIST)
	public static String delWebInfoList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		List<String> ids = (List<String>) param.get("ids"); //传过来的栏目的id
		List<Long> idList =new ArrayList<Long>();
		for(int i=0;i<ids.size();i++){
			idList.add(Long.parseLong(ids.get(i)));
		}
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.delWebInfoList(idList);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	
	/**
	 * 后台管理使用，删除一个新闻表
	 * 
	 * 传入的参数为，新闻表的id
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_NEWSINFO)
	public static String delNewsInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		Long id = Long.parseLong((String)param.get("id")); //传过来的新闻表的id
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.delNewsInfo(id);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null); 
	}
	
	/**
	 * 待整理，可以和下一个方法合并
	 * 
	 * 查看所有的订阅栏目，后台管理。
	 */
	@HandlerMethod(methodName = ServletConst.ALLWEBINFO)
	public static String getAllWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		List<WebInfo> webinfoList = webinfoService.getAllWebInfoList();
		int totalSize = webinfoList.size();
		
		ArrayList<Object> result = new ArrayList<Object>();
		//注意为null的情况
		for(WebInfo webinfo:webinfoList){
			HashMap<String,String> data = new HashMap<String,String>();
			data.put("id", String.valueOf(webinfo.getGid()));
			data.put("name", webinfo.getWebname());
			data.put("url", webinfo.getUrl());
			data.put("type", webinfo.getCategory());
			data.put("isRss",  (webinfo.isIsrss()==true)?"是":"非");
			data.put("picPath", webinfo.getPicpath());
			data.put("newslistDiv", webinfo.getNewslist());
			data.put("mainContentArea", webinfo.getMainarea());
			data.put("titleArea", webinfo.getTitlearea());
			data.put("timeArea", webinfo.getTimearea());
			data.put("contentArea", webinfo.getContentarea());
			data.put("num", webinfo.getNum().toString());
			result.add(data);
		}
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	
	
	/**
	 * 待整理
	 * 
	 * 查看一个订阅栏目的所有信息，后台管理使用。
	 */
	@HandlerMethod(methodName = ServletConst.GET_WEBINFO)
	public static String getWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String id = (String)param.get("id");  //传过来的id
		
		HashMap<String, String> result = new HashMap<String, String>();
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		WebInfo webinfo = webinfoService.getWebInfoById(Long.parseLong(id));
		
		//注意为null的情况
		result.put("gid", String.valueOf(webinfo.getGid()));
		result.put("name", webinfo.getWebname());
		result.put("url", webinfo.getUrl());
		result.put("type", webinfo.getCategory());
		result.put("isRss", (webinfo.isIsrss()==true)?"是RSS":"非RSS");
		result.put("picPath", webinfo.getPicpath());
		result.put("newslistDiv", webinfo.getNewslist());
		result.put("mainContentArea", webinfo.getMainarea());
		result.put("titleArea", webinfo.getTitlearea());
		result.put("timeArea", webinfo.getTimearea());
		result.put("contentArea", webinfo.getContentarea());
		result.put("num", webinfo.getNum().toString());
			
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	
	
	
	/**
	 * 查看所有的订阅栏目
	 */
	@HandlerMethod(methodName = ServletConst.ALLWEBINFOLIST)
	public static String getAllWebInfoList(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		HashMap<String, String> result = new HashMap<String, String>();
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		List<WebInfo> webinfoList = webinfoService.getAllWebInfoList();
		
		for(int i=0;i<webinfoList.size();i++){
			WebInfo webinfo = webinfoList.get(i);
			String picpath = webinfo.getPicpath();
			String webname = webinfo.getWebname();
			String num = Long.toString(webinfo.getNum());
			if(picpath!=null && webname!=null && num!=null ){
				result.put("picpath", picpath);   //Logo图像路径
				result.put("webname",webname);    //栏目名称
				result.put("num", num);           //栏目的关注人数
			}
		}
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	
	/**
	 * 查看我的订阅
	 */
	@HandlerMethod(methodName = ServletConst.MYWEBINFOLIST)
	public static String MYWEBINFOLIST(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		HashMap<String, String> result = new HashMap<String, String>();
		
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		
		List<WebInfo> webinfoList = webinfoService.getUserWebInfoList(user.getId());
		
		for(int i=0;i<webinfoList.size();i++){
			WebInfo webinfo = webinfoList.get(i);
			String picpath = webinfo.getPicpath();
			String webname = webinfo.getWebname();
			String num = Long.toString(webinfo.getNum());
			if(picpath!=null && webname!=null && num!=null ){
				result.put("picpath", picpath);   //Logo图像路径
				result.put("webname",webname);    //栏目名称
				result.put("num", num);           //栏目的关注人数
			}
		}
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	
	/**
	 * 根据分类名称，查找一系列的订阅栏目
	 * 
	 * 传入的参数为，分类的名称，如“国际时政”
	 * 返回的是订阅栏目的Logo图像路径、名称以及关注人数
	 */
	@HandlerMethod(methodName = ServletConst.GET_WEBINFOLISTBY_CATEGORY)
	public static String getWebInfoListByCategory(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String category = (String)param.get("catagory");
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		List<WebInfo> webinfoList = webinfoService.getWebInfoListByCategory(category);
		
		HashMap<String, String> result = new HashMap<String, String>();
		for(int i=0;i<webinfoList.size();i++){
			WebInfo webinfo = webinfoList.get(i);
			String picpath = webinfo.getPicpath();
			String webname = webinfo.getWebname();
			String num = Long.toString(webinfo.getNum());
			if(picpath!=null && webname!=null && num!=null ){
				result.put("picpath", picpath);   //Logo图像路径
				result.put("webname",webname);    //栏目名称
				result.put("num", num);           //栏目的关注人数
			}
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result); 
	}
	
	/**
	 * 根据分类名称，列出该分类下的所有新闻，按新闻发布时间降序排列
	 * 
	 * 传入的参数：分类名称category
	 * 返回的是：新闻标题、新闻摘要、摘要图片(如果有)、发布时间、来源
	 */
	@HandlerMethod(methodName = ServletConst.GET_NEWSINFOLISTBY_CATEGORY)
	public static String getNewsInfoListByCategory(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String category = (String)param.get("catagory");
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		List<NewsInfo> newsinfoList = webinfoService.getNewsByCategory(category);
		
		HashMap<String, String> result = new HashMap<String, String>();
		for(int i = 0; i<newsinfoList.size(); i++){
			NewsInfo newsinfo = newsinfoList.get(i);
			String title = newsinfo.getTitle();
			String abstractcontent = newsinfo.getAbstractContent();
			String picpath = newsinfo.getPicPath();
		    Date date = newsinfo.getDate();
		    String url = newsinfo.getNewsUrl();
		    String source = newsinfo.getSource();
		    if(title!=null && abstractcontent!=null && date!=null && url!=null && source !=null){
		    	result.put("title",title );
		    	result.put("abstractcontent", abstractcontent);
		    	result.put("picpath", picpath);
		    	result.put("date", date.toGMTString());
		    	result.put("url", url);
		    	result.put("source", source);
		    }
		}
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	}
	
	/**
	 * 关注一个订阅栏目
	 * 传过来的参数是：栏目的id
	 */
	@HandlerMethod(methodName = ServletConst.ATTEN_WEBINFO)
	public static String addAttenWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String id = (String)param.get("id");
		
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.addAttenWebInfo(user.getId(), Long.parseLong(id));
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	
	/**
	 * 取消订阅一个栏目
	 * 
	 * 传过来的参数是栏目的id
	 */
	@HandlerMethod(methodName = ServletConst.DEL_WEBINFO)
	public static String delAttenWebInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String id = (String)param.get("id");
		
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.delAttenWebInfo(user.getId(), Long.parseLong(id));
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	
	/**
	 * 订阅一个栏目
	 * 
	 * 传过来的参数是栏目的名称
	 */
	@HandlerMethod(methodName = ServletConst.ATTEN_WEBINFOBY_WEBNAME)
	public static String addAttenWebInfoByWebName(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String webname = (String)param.get("webname");
		
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.addAttenWebInfo(user.getId(), webname);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	
	
	/**
	 * 取消订阅一个栏目
	 * 
	 * 传过来的参数是栏目的名称
	 */
	@HandlerMethod(methodName = ServletConst.DEL_WEBINFOBY_WEBNAME)
	public static String delAttenWebInfoByWebName(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String webname = (String)param.get("webname");
		
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		webinfoService.delAttenWebInfo(user.getId(), webname);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
	}
	
	/**
	 * 列出一个订阅栏目下的所有新闻
	 * 传过来的是订阅栏目的名称
	 */
	@HandlerMethod(methodName = ServletConst.LIST_NEWSINFO_IN_WEBINFO)
	public static String listNewsInfo(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String webname = (String)param.get("webname");
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		List<NewsInfo> newsinfoList = webinfoService.listNewsInWebInfo(webname);
		
		HashMap<String, String> result = new HashMap<String, String>();
		for(int i = 0; i<newsinfoList.size(); i++){
			NewsInfo newsinfo = newsinfoList.get(i);
			String title = newsinfo.getTitle();
			String abstractcontent = newsinfo.getAbstractContent();
			String picpath = newsinfo.getPicPath();
		    Date date = newsinfo.getDate();
		    String url = newsinfo.getNewsUrl();
		    String id = newsinfo.getNewsId().toString();
		    String source = newsinfo.getSource();
		    if(title!=null && abstractcontent!=null && date!=null && url!=null && source !=null){
		    	result.put("title",title );
		    	result.put("abstractcontent", abstractcontent);
		    	result.put("picpath", picpath);
		    	result.put("date", date.toGMTString());
		    	result.put("url", url);
		    	result.put("id", id);
		    	result.put("source", source);
		    }
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	}
	
	/**
	 * 打开一条新闻
	 * 
	 * 依靠的参数是新闻的url(也可以是id)
	 * 暂定是id
	 */
	@HandlerMethod(methodName = ServletConst.NEWSCONTENT)
	public static String newsContent(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, Exception{
		HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String)param.get("account");
		String id = (String)param.get("id");
		
		IWebInfoService webinfoService = (IWebInfoService)ApplicationContext.getInstance().getBean(WebInfoService.NAME);
		NewsInfo newsinfo = webinfoService.getNewsInfoById(Long.parseLong(id));
		
		HashMap<String, String> result = new HashMap<String, String>();
			
		String title = newsinfo.getTitle();
		String content = newsinfo.getContent();
		Date date = newsinfo.getDate();
		String url = newsinfo.getNewsUrl();
		String source = newsinfo.getSource();
		if(title!=null && content!=null && date!=null && url!=null && source !=null){
			result.put("title",title );
		    result.put("abstractcontent", content);
		    result.put("date", date.toGMTString());
		    result.put("url", url);
		    result.put("source", source);
		 }
		    
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	}
	
}
