package apps.transmanager.weboffice.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.AppUtil;
import apps.transmanager.weboffice.util.server.WebTools;

public class RegisterServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String veryCode = "";
			Cookie[] cookies = req.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("code")) {
					System.out.println("cook.code=" + cookies[i].getValue());
					veryCode = cookies[i].getValue();
				}
			}
			String code = req.getParameter("code");
			String action = req.getParameter("action");
			String name = req.getParameter("name");
			if ("code".equals(action) && code != null && !"".equals(code)) {
				System.out.println(req.getSession().getId());
				String sessionCode = (String) req.getSession().getAttribute(
						"validateCode");
				if (sessionCode == null) {
					if (veryCode.toLowerCase().equals(code.toLowerCase())) {
						resp.getWriter().print(true);
						return;
					}

				} else if (sessionCode.toLowerCase().equals(code.toLowerCase())) {
					resp.getWriter().print(true);
					return;
				} else {
					// 账号输入不正确
				}
			} else if ("name".equals(action) && name != null
					&& !"".equals(name)) {
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				boolean duplicate = userService.isExistUser(name);
				resp.getWriter().print(!duplicate);

				return;
			} else if ("exist".equals(action) && name != null
					&& !"".equals(name)) {
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				boolean duplicate = userService.isExistUser(name);
				resp.getWriter().print(duplicate);
				return;
			} else if ("register".equals(action)) {
				Users user = new Users();
				user.setUserName(WebTools.converStr(req
						.getParameter("userName")));
				user.setResetPass(req.getParameter("resetPass"));
				user.setRealEmail(req.getParameter("realEmail"));
				String groupname = WebTools.converStr(req
						.getParameter("groupname"));
				// System.out.println(groupname+"======="+WebTools.converStr(groupname));

				String register = register(user, groupname);
				resp.getWriter().print(register);
				return;
			} else if ("findpwd".equals(action)) {
				boolean result = findPwd(name);
				resp.getWriter().print(result);
				return;
			}
		} catch (Exception e) {
			System.out.println("验证码有错！");
			resp.getWriter().print(false);
		}
		resp.getWriter().print(false);
	}

	public boolean findPwd(String userName) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		MessagesService messagesService = (MessagesService) ApplicationContext
				.getInstance().getBean(MessagesService.NAME);
		try {
			Users user = userService.getUser(userName);
			if (user != null) {
				String newPwd = AppUtil.getRandomPwd(6);
				user.setResetPass(newPwd);
				userService.modifyPassword(user.getId(), newPwd);
				String content = "<div style='font-size:13px;'>"
						+ "<p>尊敬的"
						+ user.getRealName()
						+ "，您好：</p>"
						+ "<div style='padding-left:24px'>"
						+ "<p>您的密码已经重置，您的帐号信息如下：</p><p>用户名： "
						+ user.getUserName()
						+ "</p>"
						+ "<p>新密码： "
						+ newPwd
						+ "</p>"
						+ "<p>登录页面：<a href='http://c.yozosoft.com' target='_blank'>http://c.yozosoft.com</a></p>"
						+ "</div><p>&nbsp;</p>"
						+ "<p>若有任何问题或建议，欢迎您及时联系我们;非常感谢您的配合和支持，祝您工作愉快!<br />Copyright (C) 2011 Yozosoft Co., Ltd. All Rights Reserved</p></div>";
				messagesService.sendMail(user.getRealEmail(), content,
						"密码找回和重置");
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private String register(Users user, String groupname) {
		if (user == null || user.getUserName() == null
				|| "".equals(user.getUserName().trim())
				|| user.getRealEmail() == null
				|| "".equals(user.getRealEmail())) {
			return "null";
		}
		try {
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			boolean duplicate = userService.isExistUser(user.getUserName());

			if (duplicate) {
				return "duplicate";
			}
			if (user != null) {
				// Organizations dep =
				// userService.getChildOrganizations(null).get(0);
				Spaces space = new Spaces();
				space.setName(groupname);
				space.setDescription("外部用户注册时创建");
				Long orgid = userService.isExistOrg(groupname);
				boolean isnew = false;
				if (orgid == null) {
					isnew = true;
					Organizations org = new Organizations();
					org.setName(groupname);
					org.setDescription("外部用户注册");
					Spaces retSpace = userService.addOrUpdateOrganization(null,
							org, null, null, null, null, space);
					if (retSpace != null && retSpace.getOrganization() != null) {
						orgid = retSpace.getOrganization().getId();
					}
				}
				List<Long> orgs = new ArrayList<Long>();
				orgs.add(orgid);
				user.setRealName(user.getUserName());
				user.setStorageSize(1000f);
				if (isnew) {
					user.setDuty("部门管理员");
					user.setRole((short) 1);
					user.setPartadmin(2);
					userService.addOrUpdateUser(user, orgs, null, 1L, null);
				} else {
					user.setDuty("外部注册");
					user.setRole((short) 2);
					userService.addOrUpdateUser(user, orgs, null, 2L, null);
				}

				userService.createGroups(groupname, user, space);

//				ITalkService talkService = (ITalkService) ApplicationContext
//						.getInstance().getBean("talkService");
//				talkService.getCtmGroupService().addSystemCtmG(user.getId());
			}
		} catch (Exception e) {
			return "false";
		}
		try {
			MessagesService messagesService = (MessagesService) ApplicationContext
					.getInstance().getBean(MessagesService.NAME);
			String content = "<div style='font-size:13px;'>"
					+ "<p>尊敬的"
					+ user.getRealName()
					+ "，您好：</p>"
					+ "<div style='padding-left:24px'>"
					+ "<p>感谢您注册无锡市政务移动协同办公系统，您的无锡市政务移动协同办公系统帐号信息如下：</p><p>用户名： "
					+ user.getUserName()
					+ "</p>"
					+ "<p>密码： "
					+ user.getResetPass()
					+ "</p>"
					+ "<p>登录页面：<a href='http://office.yozosoft.com' target='_blank'>http://office.yozosoft.com</a></p>"
					+ "</div><p>&nbsp;</p>"
					+ "<p>若有任何问题或建议，欢迎您及时联系我们;非常感谢您的配合和支持，祝您工作愉快!<br />Copyright (C) 2012 Yozosoft Co., Ltd. All Rights Reserved</p></div>";

			messagesService.sendMail(user.getRealEmail(), content,
					"欢迎使用无锡市政务移动协同办公系统服务");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

}
