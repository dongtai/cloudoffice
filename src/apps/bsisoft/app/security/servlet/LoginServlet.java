package apps.bsisoft.app.security.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bsisoft.corabaPool.CorbaConFactory;
import com.bsisoft.security.ca.LoginManager;
import com.bsisoft.security.ca.LoginManagerImpl2;


/**
 * 
 * <b>登陆测试类</b><br/>
 * 说明<br/>
 * <p>
 * 此代码演示如何使用PKI技术进行登陆验证
 * </p>
 * 
 * 
 * @author chenyongshun E-mail:chenyongshun@bisoft.cn
 * 
 */
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3532847687989926839L;

	public static final String OP_LOGIN_1 = "login1";

	public static final String OP_LOGIN_2 = "login2";


	// 安全平台客户端 连接

	private CorbaConFactory corbaConFactory;
	private LoginManager loginManager;
 

	private String successUrl;

	private String failureUrl;

	private String dnAttributeName;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String op = request.getParameter("op");

		if (OP_LOGIN_1.equalsIgnoreCase(op)) {
			 
			login1(request, response);

		} else if (OP_LOGIN_2.equalsIgnoreCase(op)) {

			login2(request, response);

		}

	}

	

	private void login1(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		debug("login1");
 
		String clientLogin1RequestInfo = request
				.getParameter("clientLogin1RequestInfo");
		String uuid = request.getParameter("uuid");
 
		String login1 = loginManager.login1(clientLogin1RequestInfo, uuid);
		
		response.getWriter().write(login1);
		response.getWriter().flush();
		response.getWriter().close();

	}
	
	
	
	private void login2(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// login 2
		debug("login2");

		String instring = request.getParameter("clientLogin2RequestInfo");
		String uuid = request.getSession(false) == null ? null : request
				.getSession(false).getId();
		String dn = loginManager.login2(instring, uuid);
		if(dn == null || dn.trim().equals("")){
			request.getRequestDispatcher(failureUrl).include(request, response);
			return;
		}
	 
		request.setAttribute(dnAttributeName, dn);
		request.getSession().setAttribute(dnAttributeName, dn);
		request.getRequestDispatcher(successUrl).forward(request, response);
		 
	}
	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {

		Properties ps =  Constants.getProperties();
		successUrl = ps.getProperty(Constants.SUCCESS_DISPATCH_URL);
		failureUrl = ps.getProperty(Constants.FAILURE_DISPATCH_URL);
		dnAttributeName = ps.getProperty(Constants.DN_ATTRIBUTE_NAME);
		String refFile = ps.getProperty(Constants.REF_FILE);
		String maxconn = ps.getProperty(Constants.MAX_CONN);
		String outtime = ps.getProperty(Constants.OUT_TIME);
		corbaConFactory = new CorbaConFactory();
		if(refFile == null || refFile.trim().equals("")){
			throw new RuntimeException("Corba 配置文件不能为空。");
		}
		corbaConFactory.setRefFile(refFile);
		corbaConFactory.setMaxconn(maxconn);
		corbaConFactory.setOuttime(outtime);
		LoginManagerImpl2  lm = new LoginManagerImpl2();
		lm.setCorbaConFactory(corbaConFactory);
		loginManager = lm;

		System.out
				.println("========================= cssp 初始化成功===================: ");

	}

	private void debug(String str) {
		if (Constants.isDebug) {
			System.out.println(str);
		}

	}

}