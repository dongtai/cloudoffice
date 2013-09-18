package apps.transmanager.weboffice.servlet.auth.cas;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.servlet.auth.BasicFilter;
import apps.transmanager.weboffice.servlet.config.DynamicParaFilterConfig;
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 * 
 */
public class SSOCAS2Filter extends BasicFilter
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

			String serverName = PropsValue.get("cas.server.name");
			String serviceUrl = PropsValue.get("cas.service.url");
			config.addInitParameter(CASFilter.LOGIN_INIT_PARAM, PropsValue.get("cas.login.url"));
			if (serviceUrl != null && !serviceUrl.equals(""))
			{
				config.addInitParameter(CASFilter.SERVICE_INIT_PARAM, serviceUrl);
			}
			else
			{
				config.addInitParameter(CASFilter.SERVERNAME_INIT_PARAM, serverName);
			}
			config.addInitParameter(CASFilter.VALIDATE_INIT_PARAM, PropsValue.get("cas.validate.url"));
			config.addInitParameter(CASFilter.WRAP_REQUESTS_INIT_PARAM, PropsValue.get("cas.wrap.request"));
			
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
			Object companyO = request.getParameter("companyID");
			long companyId = companyO == null ? 1L : Long.valueOf((String)companyO);  
			if ("true".equalsIgnoreCase(PropsValue.get("cas.auth.enabled")))
			{
				String pathInfo = request.getPathInfo();
				if (pathInfo != null && pathInfo.indexOf("/logout") != -1)
				{
					HttpSession session = request.getSession();
					session.invalidate();
					String logoutUrl = PropsValue.get("cas.logout.url");
					response.sendRedirect(logoutUrl);
				}
				else
				{
					Filter casFilter = getCASFilter(companyId);
					casFilter.doFilter(request, response, filterChain);
					System.out.println("==============");					
				}
			}
			else
			{
				processFilter(SSOCAS2Filter.class, request, response, filterChain);
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
