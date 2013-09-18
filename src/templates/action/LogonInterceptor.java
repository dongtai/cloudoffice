package templates.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class LogonInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		Map session =ai.getInvocationContext().getSession();
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if(request.getAttribute("userListKey") !=null ){
			return ai.invoke();
		}else{
			System.out.println("Login");
			return "login";
		}
	}

}
