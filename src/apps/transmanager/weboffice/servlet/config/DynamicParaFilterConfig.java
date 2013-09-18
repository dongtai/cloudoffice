package apps.transmanager.weboffice.servlet.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * 
 */
public class DynamicParaFilterConfig implements FilterConfig
{
	private String _filterName;
	private ServletContext _servletContext;
	private Map<String, String> _parameters = new LinkedHashMap<String, String>();
	
	public DynamicParaFilterConfig(FilterConfig filterC)
	{
		Enumeration<String> enu = filterC.getInitParameterNames();
		while (enu.hasMoreElements())
		{
			String name = enu.nextElement();
			addInitParameter(name, filterC.getInitParameter(name));
		}
		_filterName = filterC.getFilterName();
		_servletContext = filterC.getServletContext();
	}

	public DynamicParaFilterConfig(String filterName, ServletContext servletContext)
	{
		_filterName = filterName;
		_servletContext = servletContext;
	}

	public String getFilterName()
	{
		return _filterName;
	}

	public ServletContext getServletContext()
	{
		return _servletContext;
	}

	public void addInitParameter(String name, String value)
	{
		_parameters.put(name, value);
	}

	public String getInitParameter(String name)
	{
		return _parameters.get(name);
	}

	public Enumeration<String> getInitParameterNames()
	{
		return Collections.enumeration(_parameters.keySet());
	}	

}
