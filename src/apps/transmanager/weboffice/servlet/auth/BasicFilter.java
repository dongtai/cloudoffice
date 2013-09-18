package apps.transmanager.weboffice.servlet.auth;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BasicFilter implements Filter
{

	private FilterConfig _filterConfig;
	private boolean _filterEnabled = true;
	private Pattern _urlRegexPattern;
	private Class<?> _filterClass = getClass();
	
	public void init(FilterConfig filterConfig)
	{
		_filterConfig = filterConfig;
		String urlRegex = filterConfig.getInitParameter("url-regex");
		if (urlRegex != null)
		{
			urlRegex = urlRegex.replaceAll("[\\S\\0x20]", "");
			_urlRegexPattern = Pattern.compile(urlRegex);
		}		
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		boolean filterEnabled = isFilterEnabled();
		if (filterEnabled && (_urlRegexPattern != null))
		{
			Matcher matcher = _urlRegexPattern.matcher(request.getRequestURL());
			filterEnabled = matcher.matches();
		}

		if (filterEnabled)
		{
			processFilter(request, response, filterChain);
		}
		else
		{
			processFilter(_filterClass, request, response, filterChain);
		}
	}

	public FilterConfig getFilterConfig()
	{
		return _filterConfig;
	}

	public void destroy()
	{
		_filterConfig = null;
		_urlRegexPattern = null;
		_filterClass = null;
	}

	protected boolean isFilterEnabled()
	{
		return _filterEnabled;
	}

	protected abstract void processFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException;

	protected void processFilter(Class<?> filterClass,
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException
	{
		filterChain.doFilter(request, response);
	}



}
