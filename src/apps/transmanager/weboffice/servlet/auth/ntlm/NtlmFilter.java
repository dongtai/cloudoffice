package apps.transmanager.weboffice.servlet.auth.ntlm;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jcifs.Config;
import jcifs.UniAddress;
import jcifs.http.NtlmHttpFilter;
import jcifs.http.NtlmSsp;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbSession;
import jcifs.util.Base64;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.servlet.config.DynamicParaFilterConfig;

/**
 */
public class NtlmFilter extends NtlmHttpFilter
{

	private DynamicParaFilterConfig _filterConfig;
	
	public void init(FilterConfig filterConfig) throws ServletException
	{
		super.init(filterConfig);
		_filterConfig = new DynamicParaFilterConfig(filterConfig);
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		try
		{
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;
			  
			if ("true".equalsIgnoreCase(PropsValue.get("ntlm.auth.enabled")))
			{
				_filterConfig.addInitParameter("jcifs.http.domainController", PropsValue.get("ntlm.auth.domain.controller"));
				_filterConfig.addInitParameter("jcifs.smb.client.domain",PropsValue.get("ntlm.auth.domain"));

				super.init(_filterConfig);

				String msg = request.getHeader("Authorization");
				if (msg != null && msg.startsWith("NTLM"))
				{
					byte[] src = Base64.decode(msg.substring(5));
					if (src[8] == 1)
					{
						UniAddress dc = UniAddress.getByName(Config.getProperty("jcifs.http.domainController"),
								true);
						byte[] challenge = SmbSession.getChallenge(dc);
						Type1Message type1 = new Type1Message(src);
						Type2Message type2 = new Type2Message(type1, challenge,	null);
						msg = Base64.encode(type2.toByteArray());
						
						response.setHeader("WWW-Authenticate", "NTLM " + msg);
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentLength(0);
						response.flushBuffer();

						return;
					}
				}

				String path = request.getPathInfo();
				if (path != null && path.endsWith("/login"))
				{
					NtlmPasswordAuthentication ntlm = negotiate(request,
							response, false);

					if (ntlm == null)
					{
						return;
					}
					String remoteUser = ntlm.getName();

					int pos = remoteUser.indexOf("\\");

					if (pos != -1)
					{
						remoteUser = remoteUser.substring(pos + 1);
					}

					servletRequest.setAttribute("NTLM_REMOTE_USER", remoteUser);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public NtlmPasswordAuthentication negotiate(HttpServletRequest request,
			HttpServletResponse response, boolean skipAuthentication)
			throws IOException, ServletException
	{
		NtlmPasswordAuthentication ntlm = null;
		HttpSession session = request.getSession(false);
		String authorizationHeader = request.getHeader("Authorization");

		if ((authorizationHeader != null)
				&& ((authorizationHeader.startsWith("NTLM "))))
		{

			String domainController = Config.getProperty("jcifs.http.domainController");

			UniAddress uniAddress = UniAddress.getByName(domainController, true);
			byte[] challenge = SmbSession.getChallenge(uniAddress);
			ntlm = NtlmSsp.authenticate(request, response, challenge);
			session.setAttribute("NtlmHttpAuth", ntlm);
		}
		else
		{
			if (session != null)
			{
				ntlm = (NtlmPasswordAuthentication)session.getAttribute("NtlmHttpAuth");
			}
			if (ntlm == null)
			{
				response.setHeader("WWW-Authenticate", "NTLM");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentLength(0);
				response.flushBuffer();
				return null;
			}
		}

		return ntlm;
	}
	

}
