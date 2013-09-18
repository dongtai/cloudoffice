package apps.transmanager.weboffice.servlet.auth.login;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.auth.login.AutoLogin;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.servlet.auth.BasicFilter;


public class AutoLoginFilter extends BasicFilter
{
	private String sucessURL;
	private String errorURL;
	private static AutoLogin[] autoLogins;
	
	public void init(FilterConfig filterConfig)
	{
		if (filterConfig != null)
		{
			sucessURL = filterConfig.getInitParameter("sucessURL");
			errorURL = filterConfig.getInitParameter("errorURL");
		}
		int size = 0;
		String auto = PropsValue.get(PropsConsts.AUTO_LOGIN_CLASS);
		String[] autoClass = null;
		if (auto != null && auto.length() > 0)
		{
			autoClass = auto.split(";");
		}
		if (autoClass != null && (size = autoClass.length) > 0)
		{
			int realSize = 0;
			autoLogins = new AutoLogin[size];
			String cl;
			for (int i = 0; i < size; i++)
			{
				cl = autoClass[i].trim();
				if (cl.length() > 0)
				{
					try
					{
						AutoLogin a = (AutoLogin)Class.forName(cl).newInstance();
						autoLogins[realSize++] = a;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			if (realSize != size)
			{
				AutoLogin[] temp = autoLogins;
				autoLogins = new AutoLogin[realSize];
				System.arraycopy(temp, 0, autoLogins, 0, realSize);
			}
		}		
	}

	protected void processFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException
	{
		Hashtable<String, Object> ret;
		for(AutoLogin autoLo : autoLogins)
		{
			ret = autoLo.login(request, response);
			if (ret != null && ret.size() > 0)
			{
				int result = UserOnlineHandler.SSOLogin(ret, request, response);
				if (result == Constant.LONG_SUCESS)
				{
					request.setCharacterEncoding("UTF-8");
		            response.setContentType("text/html;charset=UTF-8");
		            
					request.getRequestDispatcher(sucessURL).forward(request, response);
				}
				else
				{
					String s = Constant.AUTOLOGIN_ERROR_0; //"未知错误!";
		            if (result == Constant.ILLEGAL_LICENSE)
		            {
		                s = Constant.AUTOLOGIN_ERROR_1; //"非法license!";
		            }
		            else if (result == Constant.ONLINE_USER_ILLEGAL)
		            {
		                s = Constant.AUTOLOGIN_ERROR_2; //"非法用户在线数!";
		            }
		            else if (result == Constant.ONLINE_MAX_USER)
		            {
		                s = Constant.AUTOLOGIN_ERROR_3; //"已经达到最大用户在线数!";
		            }
		            else if (result == Constant.LICENSE_END)
		            {
		                s = Constant.AUTOLOGIN_ERROR_4; //"license到期!";
		            }
		            else if (result == Constant.LICENSE_ILLEGAL_TIME)
		            {
		                s = Constant.AUTOLOGIN_ERROR_5; //"非法license时间!";
		            }
		            else if (result == Constant.TEMP_ERROR)
		            {
		                s = Constant.AUTOLOGIN_ERROR_6; //"文件系统异常!";
		            }
		            request.setCharacterEncoding("UTF-8");
		            response.setContentType("text/html;charset=UTF-8");
		            request.setAttribute("SSOLoginError", s);
					request.getRequestDispatcher(errorURL).forward(request, response);
				}
				return;
			}
		}
		processFilter(this.getClass(), request, response, filterChain);
	}

	public static void registerAutoLogin(AutoLogin autoLogin)
	{
		if (autoLogins == null || autoLogin == null)
		{
			return;
		}
		
		int realSize = autoLogins.length + 1;
		AutoLogin[] temp = autoLogins;
		autoLogins = new AutoLogin[realSize];
		System.arraycopy(temp, 0, autoLogins, 0, realSize - 1);

	}

	public static void unregisterAutoLogin(AutoLogin autoLogin)
	{
		if (autoLogins == null || autoLogin == null)
		{
			return;
		}
		int realSize = autoLogins.length - 1;
		AutoLogin[] temp = autoLogins;
		autoLogins = new AutoLogin[realSize];
		System.arraycopy(temp, 0, autoLogins, 0, realSize);
	}

}

