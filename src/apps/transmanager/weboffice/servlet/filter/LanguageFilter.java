package apps.transmanager.weboffice.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 统一处理编码及国际化的过滤器。
 * 在该类中，统一处理编码以及
 * 仅仅根据用户浏览器要求的语言在请求中增加语言的标志。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class LanguageFilter implements Filter
{
	/**
	 * 
	 */
	public void init(FilterConfig filterConfig)
	{		
	}
	/**
	 */
	public void destroy()
	{
	}
	
	/**
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		String lan = request.getHeader("Accept-Language");
		// 后续根据具体情况在处理国际化的内容。
		chain.doFilter(req, res);
	}
}
