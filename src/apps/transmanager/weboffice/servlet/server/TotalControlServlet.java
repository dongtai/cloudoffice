package apps.transmanager.weboffice.servlet.server;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.service.config.HandlerConfig;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.util.server.JSONTools;


/**
 * 系统主控servlet类，处理系统中的主要以一个json参数方式进行的所有servlet请求。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class TotalControlServlet extends AbstractServlet
{	
	@Override
	protected String handleService(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams) throws ServletException,  Exception
	{
		String method = (String)jsonParams.get(ServletConst.METHOD_KEY);             // 
//		System.out.println("method=============="+method);
		// 不需要验证的方法，如登录、注册、下载免费资源等
		Method m = HandlerConfig.getJsonMethod(method, false);
		if (m != null)
		{
			return (String)m.invoke(null, request, response, jsonParams);
		}		
		// 验证用户的登录token是否有效。
		if (!UserOnlineHandler.isValidate(jsonParams))
		{
			return  JSONTools.convertToJson(ErrorCons.SYSTEM_TOKEN_ERROR, null);
		}
		// 登录验证后的方法
		m = HandlerConfig.getJsonMethod(method, true);
		if (m != null)
		{
			return (String)m.invoke(null, request, response, jsonParams);
		}
		
		// 无效请求方法
		return JSONTools.convertToJson(ErrorCons.SYSTEM_REQUEST_ERROR, method);
		
	}
	
}
