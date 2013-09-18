package apps.bsisoft.demo.oa.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import apps.bsisoft.demo.oa.Constants;





public class SafeAndChartSetFilter implements Filter{

	private String encoding;

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		
		 request.setCharacterEncoding(encoding);
		 response.setCharacterEncoding(encoding);
		 
		 HttpServletRequest req = (HttpServletRequest) request;
		 
		 if(req.getRequestURI().startsWith(req.getContextPath()+"/login") 
		  ||req.getRequestURI().startsWith(req.getContextPath()+"/securityLogin" )){
			 
			 chain.doFilter(request, response);
			 return;
		 }
		 
		 if(req.getSession(false) == null || req.getSession(false).getAttribute(Constants.SESSION_USER_KEY) == null){
			 
			 req.getRequestDispatcher("/index.jsp")
			 .include(request, response);
	 
		 }else{
			 
			 chain.doFilter(request, response);
		 }
		
		// chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		
		encoding = config.getInitParameter("encoding");
		
	}

}
