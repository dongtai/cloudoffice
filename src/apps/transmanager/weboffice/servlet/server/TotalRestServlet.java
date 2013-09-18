package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.service.config.HandlerConfig;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.WebTools;

/**
 * 系统主控servlet类，处理系统中的主要以RSET方式进行的所有servlet请求。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class TotalRestServlet extends HttpServlet
{
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,  IOException
	{
		request.setCharacterEncoding("UTF-8");
		String error = null;
		try
		{
			String context = request.getContextPath();
			String uri = request.getRequestURI();
			uri = WebTools.decode(uri, "utf-8");
			if (context != null && context.length() > 0)   // 去除上下文的路径
			{
				uri = uri.substring(context.length());
			}
			Method preMethod;
			Method suffMethod;
			String key;
			String path;
			int index = 1;
			boolean flag = true;
			boolean success = false;
			while(flag)
			{
				index = uri.indexOf("/", index);
				if (index > 0)
				{
					key = uri.substring(0, index);
					index++;
					path = uri.substring(index);
				}
				else
				{
					key = uri;
					path = null;
					flag = false;
				}
				preMethod = HandlerConfig.getDoServiceMethod(key, false);
				if (preMethod != null)
				{
					error = (String) preMethod.invoke(null, request, response, path);
					success = true;
					break;
				}
				// 验证.....
				//String validate = request.getParameter("v_token");    // 需要验证之token值

				suffMethod = HandlerConfig.getDoServiceMethod(key, true);
				if (suffMethod != null)
				{
					error = (String) suffMethod.invoke(null, request, response, path);
					success = true;
					break;
				}
			}
			if (!success && error == null)
			{
				error = JSONTools.convertToJson(ErrorCons.SYSTEM_REQUEST_ERROR, uri);
			}
		}
		catch(Throwable e)
		{
			error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);
		}
		if (error != null)
		{
			response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
			response.getWriter().write(error);
		}
		
	}

}
