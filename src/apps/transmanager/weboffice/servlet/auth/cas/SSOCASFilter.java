package apps.transmanager.weboffice.servlet.auth.cas;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.servlet.auth.BasicFilter;
import apps.transmanager.weboffice.servlet.config.DynamicParaFilterConfig;
import apps.transmanager.weboffice.util.server.CheckUtility;
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 * 
 */
public class SSOCASFilter extends BasicFilter
{
	private static Map<Long, CASFilter> _casFilters = new ConcurrentHashMap<Long, CASFilter>();
	private String _filterName;
	
	public void init(FilterConfig filterConfig)
	{
		super.init(filterConfig);
		_filterName = "SSOCASFilter";
	}
	
	public static void reload(long companyId)
	{
		_casFilters.remove(companyId);
	}

	protected Filter getCASFilter(long companyId) throws Exception
	{
		CASFilter casFilter = _casFilters.get(companyId);
		if (casFilter == null)
		{
			casFilter = new CASFilter();
			DynamicParaFilterConfig config = new DynamicParaFilterConfig(_filterName,
					getFilterConfig().getServletContext());

			String serverName = PropsValue.get(PropsConsts.CAS_SERVER_NAME);
			String serviceUrl = PropsValue.get(PropsConsts.CAS_SERVICE_URL);
			config.addInitParameter(CASFilter.LOGIN_INIT_PARAM, PropsValue.get(PropsConsts.CAS_LOGIN_URL));
			if (!CheckUtility.isNull(serviceUrl))
			{
				config.addInitParameter(CASFilter.SERVICE_INIT_PARAM, serviceUrl);
			}
			else
			{
				config.addInitParameter(CASFilter.SERVERNAME_INIT_PARAM, serverName);
			}
			config.addInitParameter(CASFilter.VALIDATE_INIT_PARAM, PropsValue.get(PropsConsts.CAS_VALIDATE_URL));
			config.addInitParameter(CASFilter.WRAP_REQUESTS_INIT_PARAM, PropsValue.get(PropsConsts.CAS_WRAP_REQUEST));
			
			casFilter.init(config);
			_casFilters.put(companyId, casFilter);
		}

		return casFilter;
	}

	protected void processFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
	{
		try
		{		
			Object companyO = request.getParameter("companyMapId");
			long companyId = 1L;
			if (companyO != null)
			{	
				try
				{
					companyId = Long.valueOf((String)companyO);
				}
				catch(Exception e)
				{
					
				}
			}
			if ("true".equalsIgnoreCase(PropsValue.get(PropsConsts.CAS_AUTH_ENABLED)))
			{
				//String pathInfo = request.getPathInfo();
				String pathInfo = request.getRequestURI();
				//System.out.println("the path is  =================  "+pathInfo+"===="+request.getPathInfo());
				if (pathInfo != null && pathInfo.indexOf("/logout") != -1)
				{
					HttpSession session = request.getSession();
					//System.out.println("-----------old"+session);
					session.invalidate();
					String logoutUrl = PropsValue.get(PropsConsts.CAS_LOGOUT_URL);
					response.sendRedirect(logoutUrl);
				}
				else
				{
					Filter casFilter = getCASFilter(companyId);
					casFilter.doFilter(request, response, filterChain);
					//System.out.println("==============" + request.getSession());					
				}
			}
			else
			{
				processFilter(SSOCASFilter.class, request, response, filterChain);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void destroy()
	{
		super.destroy();
		_casFilters.clear();
	}

}
