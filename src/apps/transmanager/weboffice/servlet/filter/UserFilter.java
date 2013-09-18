package apps.transmanager.weboffice.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.service.config.WebConfig;


/**
 */

public class UserFilter implements Filter
{

	private FilterConfig filterConfig = null;
	public static String redirectURL;
	private String backURL;
	public static int backinga=0;
	public static String[] notfilter;
	public UserFilter()
	{

	}

	/**
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
	{

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		//String result = "failed";
//		String path = request.getContextPath();
//		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
		//String url = request.getRequestURL().toString();
		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String query = request.getQueryString();
		
		if ((uri.indexOf("chPass.html") >0  || uri.indexOf("mailRePassError.html") > 0)
		 || (uri.indexOf("/CAcheck.jsp") != -1))
		{
			chain.doFilter(request, response);
			return;
		}
		
		if(uri.indexOf("mailRePass.html") > 0){
			String account=request.getParameter("emailAccount");
			if(null !=account){
				Map<String,Object> userData=(Map<String, Object>) request.getSession().getServletContext().getAttribute(account);
				if(null !=userData && null!=userData.get("emailValidate")){
					chain.doFilter(request, response);
					return;
				}
			}
		}
		//System.out.println("================="+url+"===== query:"+uri+"===context==:"+context+":=====");
//		如果调试状态无法使用，把下面的if全部注释了就可以了
		if (//(url.startsWith("http://localhost") || url.startsWith("http://127.0.0.1")) && 
				uri.indexOf("/backdata.jsp")>0 )
		{
			String state=request.getParameter("state");
//			if ("wuxiyozobackdata".equals(state))//先用明码，以后看情况再加密-暂去除，孙爱华
//			{
//				//开始备份
//				backing=1;
//			}
//			else if ("wuxiyozooverdata".equals(state))
//			{
//				//解除备份
//				backing=0;
//			}
		}
		
		if (backinga==0)
		{
			
			if (uri.indexOf("/login.html") != -1 && query!=null && query.indexOf("autologin") != -1)
			{
				//chain.doFilter(req, res);
				response.sendRedirect(context + "/static/UploadService?" + query);
			}
			else 
			{
				if (request.getSession().getAttribute("userKey") != null || (uri.indexOf("/linkCheck2.jsp") != -1) 
				|| uri.equals(context))
				{
					chain.doFilter(req, res);
				}
				else
				{
					boolean isfilter=false;
					if (notfilter!=null)
					{
						for (int i=0;i<notfilter.length;i++)
						{
							if (uri.indexOf("/".concat(notfilter[i].trim()))!=-1)
							{
								isfilter=true;
								break;
							}
						}
					}
					if (isfilter)
					{
						chain.doFilter(req, res);
					}
					else
					{
						if ("/".equals(uri))
						{
//							System.out.println("2222222222222=========");
							PrintWriter out=response.getWriter();
							out.write("<!DOCTYPE html>");
							out.write("<html>");
							out.write("<head>");
							out.write("<title>Yozo WebOffice</title>");
							out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
							if (WebConfig.cloudPro)
							{
								out.write("<meta http-equiv=\"refresh\" content=\"0; url=/cloud/iyologin.html\" />");	
							}
							else //信电局版本，私云
							{
								if ("https".equals(WebConfig.accesstype))
								{
									out.write("<meta http-equiv=\"refresh\" content=\"0; url=/cloud/loginCA.jsp\" />");
								}
								else
								{
									out.write("<meta http-equiv=\"refresh\" content=\"0; url=/cloud/login.html\" />");
								}
							}
							out.write("</head>");  
							out.write("</html>");
						}
						else
						{
							//request.getRequestDispatcher(redirectURL).forward(req, res);
							//String r = request.getLocalAddr() + redirectURL;
							response.sendRedirect(context + redirectURL);
							//response.sendRedirect(basePath + redirectURL);
						}
					}
				}
			}
		}
		else
		{
			//正在备份
			request.getRequestDispatcher(backURL).forward(req, res);
		}
	}

	/**
	 */
	public FilterConfig getFilterConfig()
	{

		return this.filterConfig;
	}

	/**
	 */
	public void setFilterConfig(FilterConfig filterConfig)
	{

		this.filterConfig = filterConfig;
	}

	/**
	 */
	public void destroy()
	{

	}

	/**
	 */
	public void init(FilterConfig filterConfig)
	{

		this.filterConfig = filterConfig;
		if (filterConfig != null)
		{
			if (debug)
			{
				log("UserFilter:Initializing filter");
			}
		}
		String notf=filterConfig.getInitParameter("notfilter");
		if (notf!=null)
		{
			notfilter=notf.split(";");
		}
		redirectURL = filterConfig.getInitParameter("redirectURL");
		backURL = filterConfig.getInitParameter("backURL");
	}

	/**
	 */
	public String toString()
	{

		if (filterConfig == null)
		{
			return ("UserFilter()");
		}
		StringBuffer sb = new StringBuffer("UserFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());

	}

	public void log(String msg)
	{
		filterConfig.getServletContext().log(msg);
	}

	private static final boolean debug = false;
}
