package apps.moreoffice.ext.biokee.bioplat.yozo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.ext.biokee.bioplat.demo.IUruid;
import apps.moreoffice.ext.biokee.bioplat.demo.UruidImplService;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.WebTools;

public class BioplatService extends HttpServlet
{

	// String srvcode = "100010";
	// String pwd = "77345d745ef0163697f82d3fdd8a78d0";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		// resp.setCharacterEncoding("UTF-8");
		String method = req.getParameter("method");
		String srvcode = properties.get("srvcode").toString();
		String pwd = properties.get("pwd").toString();
		if ("verify".equalsIgnoreCase(method))
		{
			// String srvcode = properties.get("srvcode").toString();
			// String pwd = properties.get("pwd").toString();
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			// String name = URLDecoder.decode(req.getParameter("T2"),"UTF-8");
			String name = WebTools.converStr(req.getParameter("T2"));
			Users userinfo = userService.getUser(name);
			if (userinfo == null)
			{
				userinfo = userService.getUserBySpaceUID(name);
			}
			if (userinfo == null)
			{
				resp.getWriter().print("nopass");
				return;
			}

			if ("1".equals(userinfo.getLoginCA()))
			{
				resp.getWriter().print("LOGINCA_SET_MSG");
				return;
			}

			String uruid = userinfo.getUruid();
			// 通过注册取得的uruid，前台仅仅是传递fpdata
			// String uruid = "1000000034";//req.getParameter("uruid");

			String fpdata = java.net.URLDecoder.decode(req
					.getParameter("fpdata"), "UTF-8");
			// String fpdata = req.getParameter("fpdata");
			String ret = "";
			UruidImplService service = new UruidImplService();
			IUruid iUruid = service.getUruidImplPort();
			ret = iUruid.verify(srvcode, pwd, uruid, fpdata);
			if ("100".equalsIgnoreCase(ret))
			{
				resp.getWriter().print("pass");
				req.setAttribute("nopassword", "true");
				this.getServletContext().getRequestDispatcher(
						"/static/UploadService").forward(req, resp);
			}
			else
			{
				resp.getWriter().print("nopass");
				// resp.getWriter().print(String.valueOf(ret));
			}
		}
		if ("register".equalsIgnoreCase(method))
		{
			// String srvcode = properties.get("srvcode").toString();
			// String pwd = properties.get("pwd").toString();
			UruidImplService service = new UruidImplService();
			IUruid iUruid = service.getUruidImplPort();
			String fpdata = java.net.URLDecoder.decode(req
					.getParameter("fpdata"), "UTF-8");
			// String fpdata = req.getParameter("fpdata");;
			String temp = iUruid.register(srvcode, pwd, null, fpdata);
			if (temp != null && temp.length() == 10)
			{
				try
				{
					UserService userService = (UserService) ApplicationContext
							.getInstance().getBean(UserService.NAME);
					String email = req.getParameter("email");
					Users userinfo = userService.getUserBySpaceUID(email);
					userinfo.setUruid(temp);
					userService.updataUserinfo(userinfo);
					resp.getWriter().print("pass");
				}
				catch (Exception e)
				{
					resp.getWriter().print("nopass");
				}
			}
			else
			{
				resp.getWriter().print("nopass");
			}
		}
		else if ("getToken".equalsIgnoreCase(method))
		{
			// String srvcode = properties.get("srvcode").toString();
			// String pwd = properties.get("pwd").toString();

			String ret = "";
			UruidImplService service = new UruidImplService();
			IUruid iUruid = service.getUruidImplPort();
			ret = iUruid.getToken(srvcode, pwd);
			// resp.getWriter().print(String.valueOf(ret));
			ret = URLEncoder.encode(ret, "UTF-8");
			resp.getWriter().print(ret);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		this.doGet(req, resp);
	}

	private static Properties properties = new Properties();

	static
	{
		try
		{
			properties.load(PropsValue.class.getClassLoader()
					.getResourceAsStream("/conf/zhiwang.properties"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
