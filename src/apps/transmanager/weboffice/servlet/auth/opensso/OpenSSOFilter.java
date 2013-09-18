package apps.transmanager.weboffice.servlet.auth.opensso;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.auth.opensso.OpenSSOUtil;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.servlet.auth.BasicFilter;

/**
 */
public class OpenSSOFilter extends BasicFilter
{

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
			
			String loginUrl;
			String logoutUrl;
			String serviceUrl;
			if (!"true".equalsIgnoreCase(PropsValue.get(PropsConsts.OPENSSO_ENABLED)))
			{
				processFilter(OpenSSOFilter.class, request, response, filterChain);
				return;
			}
			loginUrl = PropsValue.get(PropsConsts.OPENSSO_LOGIN_URL);
			logoutUrl = PropsValue.get(PropsConsts.OPENSSO_LOGOUT_URL);
			serviceUrl = PropsValue.get(PropsConsts.OPENSSO_SERVICE);
			
			if (loginUrl == null || loginUrl.equals("")
					|| logoutUrl == null || logoutUrl.equals("")
					|| serviceUrl == null || serviceUrl.equals(""))
			{
				processFilter(OpenSSOFilter.class, request, response, filterChain);
				return;
			}

			String requestURI = request.getRequestURI();

			if (requestURI != null && requestURI.indexOf("/logout") != -1)
			{
				HttpSession httpSes = request.getSession();
				httpSes.invalidate();
				response.sendRedirect(logoutUrl);
			}
			else
			{
				boolean authenticated = false;
				try
				{
					authenticated = OpenSSOUtil.isAuthenticated(request, serviceUrl);
				}
				catch (Exception e)
				{

					processFilter(OpenSSOFilter.class, request, response, filterChain);
					e.printStackTrace();
					return;
				}
				if (authenticated)
				{
					/*String newSubjectId = OpenSSOUtil.getSubjectId(request,	serviceUrl);

					HttpSession httpSes = request.getSession();
					String oldSubjectId = (String) httpSes.getAttribute(PropsConsts.OPENSSO_SUBJECT_ID_KEY);

					if (oldSubjectId == null)
					{
						httpSes.setAttribute(PropsConsts.OPENSSO_SUBJECT_ID_KEY, newSubjectId);
					}
					else if (!newSubjectId.equals(oldSubjectId))
					{
						httpSes.invalidate();
						httpSes = request.getSession();
						httpSes.setAttribute(PropsConsts.OPENSSO_SUBJECT_ID_KEY, newSubjectId);
					}*/
					processFilter(OpenSSOFilter.class, request, response, filterChain);
				}
				else
				{
					response.sendRedirect(loginUrl);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


}
