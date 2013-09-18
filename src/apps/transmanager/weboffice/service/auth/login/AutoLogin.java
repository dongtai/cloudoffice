package apps.transmanager.weboffice.service.auth.login;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface AutoLogin
{
	
	/**
	 * 在各种自动登录中使用该接口。
	 * 主要是为SSO登录后，从认证系统中取得相应的认证通过的用户信息。
	 * @param request
	 * @param response
	 * @return
	 */
	Hashtable<String, Object> login(HttpServletRequest request, HttpServletResponse response);

}
