package apps.transmanager.weboffice.service.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.PropertyFilter;
import applets.common.Constants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.MailAccount;
import apps.transmanager.weboffice.databaseobject.MailAccountSign;
import apps.transmanager.weboffice.databaseobject.MailFolders;
import apps.transmanager.weboffice.databaseobject.MailMessages;
import apps.transmanager.weboffice.databaseobject.MailSources;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.DefaultMailServerConfig;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.mail.Context;
import apps.transmanager.weboffice.service.mail.MailBean;
import apps.transmanager.weboffice.service.mail.StringUtil;
import apps.transmanager.weboffice.service.mail.commailconfig.CommonMailConfig;
import apps.transmanager.weboffice.service.mail.receive.ParseMimeMessage;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.MailService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;
import apps.transmanager.weboffice.util.server.convertforread.FileUtil;
import apps.transmanager.weboffice.util.server.convertforread.bean.ConvertForRead;
import apps.transmanager.weboffice.util.server.convertforread.bean.FileConvertStatus;

import com.sun.mail.pop3.POP3Message;
import com.sun.mail.util.ASCIIUtility;

public class MailHandler {

	// 收邮件时，是否及时将邮件整体收取，如果false，则不及时收取，true，及时收取
	public final static int maxsize = 120971520;// 邮件大小限制
	public final static Integer REPEAT_MAILACCOUNT_ERROR = 600001;// 已经创建过,就不需要再创建了.
	public final static Integer MAILACCOUNT_NO_COMPLETE_ERROR = 600002;// 账户不完整.
	public final static Integer MAILACCOUNT_OVERTIME = 600006;// 连接超时.
	public final static Integer MAILACCOUNT_Connect_ERROR = 600003;// 账户连接失败.
	public final static Integer MAIL_Receive_in_background = 600010;// 邮件收取放在后台收取
	public final static Integer OVERACCOUNT_ERROR = 600004;// 邮箱账户超过5封
	public final static Integer REREPEAT = 600005;// 重复请求接收

	public final static Integer MAILACCOUNT_OK = 0;
	public final static Integer MAILACCOUNT_NOPSW = 1;
	public final static Integer MAILACCOUNT_NOPOP3 = 2;
	public final static Integer MAILACCOUNT_NULL = 3;

	public final static String mailreceiveAction = "receiveAction";
	
	public final static HashMap<String, HashMap<String, Object>> hmReceive = new HashMap<String, HashMap<String, Object>>();
	public final static HashMap<Long, MailBean> hmmb = new HashMap<Long, MailBean>();

	/**
	 * 获取账户
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getUserAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams , Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailAccount ma = null;
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		String idString = (String) jsonParams.get("id");
		Long id = (long) 0;
		if(idString!=null && idString.length() > 0)
		{
			 id = Long.parseLong(idString);
			
		}
		if(id!=0 && !checkId(user, id, mailservice))
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		ma = mailservice.findMailAccountById(id);
		if (ma == null) {
			List<MailAccount> lists = mailservice.findALLbyUser(user);
			if (lists.size() > 0)// 容错老数据
			{
				ma = lists.get(0);
			}
		}
		if (ma != null) {
			//JsonConfig cfg = createMailAccountJsonConfig();
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("id", ma.getId());
			hm.put("personName", ma.getPersonName());
			hm.put("email", ma.getEmail());
			hm.put("inpassword", "123456");
			hm.put("incomingServer", ma.getIncomingServer());
			hm.put("incomingport", ma.getIncomingport());
			hm.put("outgoingServer", ma.getOutgoingServer());
			hm.put("outgoingport", ma.getOutgoingport());
			hm.put("smtpAuth", ma.getSmtpAuth());
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR,hm);
		} else {
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}
		return error;
	}

	/**
	 * 登录内置账户
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String login(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		String domain = (String) jsonParams.get("domain");// "yozosoft.com";
		String username = (String) jsonParams.get("inuser");
		String email = (String) jsonParams.get("email");
		if (email != null) {
			username = email.substring(0, email.indexOf('@'));
			domain = email.substring(email.indexOf('@') + 1);
		} else {
			email = username + '@' + domain;
		}
		String psw = (String) jsonParams.get("psw");
		MailAccount ma = null;
		List<MailAccount> lists = mailservice.findALLbyUser(user);
		for (MailAccount mailAccount : lists) {
			if (mailAccount.getEmail().equals(email)
					&& encodemm(mailAccount.getInpassword()).equals(psw)) {
				ma = mailAccount;
				break;
			}
		}
		if (ma == null) {
			ma = new MailAccount();
		}
		ma.setUser(user);
		ma.setEmail(email);
		ma.setInpassword(psw);
		ma.setPersonName(username);
		ma.setReplyToemail(null);
		ma.setIncomingServer(mailconfig.getIncomingServer());
		ma.setIncomingport(mailconfig.getIncomingport());
		ma.setInSSL(mailconfig.getInSSL());
		ma.setIncomingServerType(MailAccount.convertServerType(mailconfig
				.getIncomingServerType()));
		ma.setInuser(username);
		ma.setOutgoingServer(mailconfig.getOutgoingServer());
		ma.setOutgoingport(mailconfig.getOutgoingport());
		ma.setOutSSL(mailconfig.getOutSSL());
		ma.setOutgoingServerType(MailAccount.ServerType.SMTP);
		ma.setSmtpAuth(mailconfig.getSmtpAuth());
		ma.setSmtpAuthSameasin(mailconfig.getSmtpAuthSameasin());
		ma.setOutsmtpUser(null);
		ma.setOutsmtpPassword(null);
		ma.setDeleteafterdays(-1);
		ma.setIsdefault(Boolean.TRUE);
		ma.setIsinnerAccount(Boolean.TRUE);
		mailservice.createMailAccount(ma);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	// 获取账户，并判断账户的类型，1、缺少密码的，2、缺少pop3地址的，3、没有账户的，4、完整的ok-----未使用
	public static String getAccountTypeforlogin(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
			return "";
	}

	/**
	 * 获取账户列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String listUserAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		List<Object[]> lists = mailservice.findALLByUser(user);
		if (lists.size() == 0) {
			error = JSONTools.convertToJson(MAILACCOUNT_NULL, null);
		} else {
			List<HashMap<Object, Object>> list = new ArrayList<HashMap<Object, Object>>();
			for(int i = 0;i!=lists.size();i++)
			{
				HashMap<Object, Object> hm = new HashMap<Object, Object>();
				hm.put("id", lists.get(i)[0]);
				hm.put("email", lists.get(i)[1]);
				hm.put("isdefault", lists.get(i)[2]);
				list.add(hm);
			}
			//JsonConfig cfg = createMailAccountJsonConfig();
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, list);
		}
		return error;
	}
	
	
	/**
	 * 创建邮件账户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String createUserAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		List<MailAccount> accLists = mailservice.findALLbyUser(user);
		if (accLists.size() < 5)// 账户数不超过5个，才可以创建
		{
			MailAccount ma = new MailAccount();
			//
			ma.setUser(user);
			handelComonMail(ma, jsonParams);
			List<MailAccount> lists = mailservice.findALLbyUser(user,
					ma.getEmail());
			if (lists != null && lists.size() > 0) {// 已经创建过,就不需要再创建了.
				error = JSONTools.convertToJson(REPEAT_MAILACCOUNT_ERROR, null);
			} else {
				if (ma.getIncomingServer() != null) {

					// cacheMailAccount(req, ma);
					//JsonConfig cfg = createMailAccountJsonConfig();
					try {//验证
						long time=System.currentTimeMillis();
						Properties props = new Properties();
						props.put("mail.smtp.host", ma.getOutgoingServer());
						props.put("mail.smtp.auth", "true");// 同时通过验证，设置需要验证才能发送
						props.put("mail.smtp.connectiontimeout","20000");//连接邮件服务器的时间，单位毫秒
						props.put("mail.smtp.timeout", "20000");//连接邮件服务器的时间，单位毫秒
						Session s = Session.getInstance(props);// 根据属性新建一个邮件会话
						String passw = ma.getInpassword();
						if(ma.getIscode())
							passw = encodemm(ma.getInpassword());
						//s.setDebug(true);
						Transport transport = s.getTransport("smtp"); // 以smtp方式登录邮箱
						transport.connect((String) props.get("mail.smtp.host"),
								ma.getEmail(), passw);// 检查能否通过Smpt验证
						if (transport.isConnected())
						{
							System.out.println("mail connect true test ==================="+(System.currentTimeMillis()-time));
							transport.close();
							mailservice.createMailAccount(ma);
							List<MailAccount> listalls = mailservice
									.findALLbyUser(user);
							if (listalls.size() == 1) {// 若新建的是第一个，设为默认邮箱
								mailservice.setDefaultMailAccount(listalls.get(0)
										.getId(), user);
							}
							mailservice.setDefaultMailAccount(ma.getId(), user);
							HashMap<Object, Object> hm = new HashMap<Object, Object>();
							hm.put("id", ma.getId());
							hm.put("email", ma.getEmail());
							error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
						}
						else
						{
							System.out.println("mail connect false test ==================="+(System.currentTimeMillis()-time));
							transport.close();
							error = JSONTools.convertToJson(
									MAILACCOUNT_OVERTIME, null);
						}

					} catch (Exception e) {
						e.printStackTrace();
						error = JSONTools.convertToJson(
								MAILACCOUNT_Connect_ERROR, null);
					}
				} else {
					error = JSONTools.convertToJson(
							MAILACCOUNT_NO_COMPLETE_ERROR, null);
				}
			}
		} else {
			error = JSONTools.convertToJson(OVERACCOUNT_ERROR, null);
		}
		// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 * 编辑邮箱账户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String editUserAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		String acctype = (String) jsonParams.get("acctype");
		if (String.valueOf(MAILACCOUNT_NOPSW).equals(acctype)) {
			String username = (String) jsonParams.get("inuser");
			String psw = (String) jsonParams.get("psw");
			ma.setInpassword(psw);
			ma.setInuser(username);
		} else {
			handelParam(ma, jsonParams);
		}
		if (ma.getIncomingServer() != null) {

			// cacheMailAccount(req, ma);
			//JsonConfig cfg = createMailAccountJsonConfig();
			try {//验证
				long time=System.currentTimeMillis();
				Properties props = new Properties();
				props.put("mail.smtp.host", ma.getOutgoingServer());
				props.put("mail.smtp.auth", "true");// 同时通过验证，设置需要验证才能发送
				props.put("mail.smtp.connectiontimeout","20000");//连接邮件服务器的时间，单位毫秒
				props.put("mail.smtp.timeout", "20000");//连接邮件服务器的时间，单位毫秒
				Session s = Session.getInstance(props);// 根据属性新建一个邮件会话
				String passw = ma.getInpassword();
				if(ma.getIscode())
					passw = encodemm(ma.getInpassword());
				//s.setDebug(true);
				Transport transport = s.getTransport("smtp"); // 以smtp方式登录邮箱
				transport.connect((String) props.get("mail.smtp.host"),
						ma.getEmail(), passw);// 检查能否通过Smpt验证
				if (transport.isConnected())
				{
					System.out.println("mail connect true test ==================="+(System.currentTimeMillis()-time));
					transport.close();
					mailservice.updateMailAccount(ma);
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
				}
				else
				{
					System.out.println("mail connect false test ==================="+(System.currentTimeMillis()-time));
					transport.close();
					error = JSONTools.convertToJson(
							MAILACCOUNT_OVERTIME, null);
				}

			} catch (Exception e) {
				e.printStackTrace();
				error = JSONTools.convertToJson(
						MAILACCOUNT_Connect_ERROR, null);
			}
		}else {
			error = JSONTools.convertToJson(
					MAILACCOUNT_NO_COMPLETE_ERROR, null);
		} 
		
		return error;
	}

	/**
	 * 删除邮箱账户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String deleteUserAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		//
		List<MailAccount> lists = mailservice.findALLbyUser(user);
		if (lists.size() != 0)
		{
			mailservice.deleteMailAccount(id);
			if (ma.getIsdefault())// 如果是默认的，并且还有账户，重新设置一个默认，并修改users
			{
				lists = mailservice.findALLbyUser(user);
				if (lists.size() > 0) {
					ma = lists.get(0);
					mailservice.setDefaultMailAccount(ma.getId(), user);
				}
			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		}
		return error;
	}

	/**
	 * 设置默认账户
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String setdefaultacc(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		mailservice.setDefaultMailAccount(id, user);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}
    
	/**
	 * 设置活动账户,已不用
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String setactiveacc(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		List<MailAccount> lists = mailservice.findALLbyUser(user);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		boolean check = false;
		for (int i = 0; i != lists.size(); i++) {

			if (lists.get(i).getId().equals(id)) {
				check = true;
				break;
			}
		}
		if (check) {
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		}else{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
			System.out.println("非法入侵！！");
		}
		return error;
	}

	// 初始化账户，邮件导入//等同于收邮件，可以不处理
	@Deprecated
	public static void initAccount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
	}

	/**
	 *  得到某个文件夹信息，总共多少封邮件，有几封未读。
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String getFolderInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = "";
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		List<MailAccount> accoutlists = mailservice.findALLbyUser(user);
		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		boolean check = false;
		for (int i = 0; i != accoutlists.size(); i++) {

			if (accoutlists.get(i).getId().equals(ma.getId())) {
				check = true;
				break;
			}
		}
		if (check) {
			Long folderid = null;
			if (jsonParams.get("folderid") != null
					&& jsonParams.get("folderid") != "undefined"
					&& ((String) jsonParams.get("folderid")).length() != 0) {
				folderid = Long.parseLong((String) jsonParams.get("folderid"));
			}
			ArrayList<Object> lists = new ArrayList<Object>();
			if (folderid == null)// all所有信息
			{
				for (long i = MailFolders.INBOX_ID; i <= MailFolders.OUTBOX_ID; i++) {
					folderid = i;
					MailFolders mf = mailservice.findMailFolderById(folderid);
					long unseencount = mailservice.countMailUnSeenMessages(ma,
							mf);
					long count = mailservice.countMailMessages(ma, mf);
					HashMap<Object, Object> hm = new HashMap<Object, Object>();
					hm.put("MailFolderID", mf.getId());
					hm.put("MailFolderName", mf.getName());
					hm.put("count", count);
					hm.put("unseencount", unseencount);
					lists.add(hm);
				}
			} else {
				MailFolders mf = mailservice.findMailFolderById(folderid);
				long count = mailservice.countMailMessages(ma, mf);
				long unseencount = mailservice.countMailUnSeenMessages(ma, mf);
				HashMap<Object, Object> hm = new HashMap<Object, Object>();
				hm.put("MailFolderID", mf.getId());
				hm.put("MailFolderName", mf.getName());
				hm.put("count", count);
				hm.put("unseencount", unseencount);
				lists.add(hm);
			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, lists);
		}

		return error;
	}

	/**
	 *  获取邮件列表
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String listmail(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);

		// 邮箱的文件夹
		String folderidStr = (String) jsonParams.get("folderid");
		folderidStr = folderidStr == null || folderidStr.length() == 0 ? null
				: folderidStr;
		Long folderid = folderidStr != null ? Long
				.parseLong((String) jsonParams.get("folderid"))
				: MailFolders.INBOX_ID;

		MailFolders mf = mailservice.findMailFolderById(folderid);

		int start = Integer.parseInt((String) jsonParams.get("start"));
		int length = Integer.parseInt((String) jsonParams.get("length"));
		String sort = (String) jsonParams.get("sort");
		List<Object[]> msslist = mailservice.findMailMessage(ma, mf, sort,
				start, length);
		List<HashMap<Object, Object>> list = new ArrayList<HashMap<Object, Object>>();
		for(int i = 0;i!=msslist.size();i++)
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("id", msslist.get(i)[1]);
			if(msslist.get(i)[6]!=null)
				hm.put("sentDate", msslist.get(i)[6].toString());
			else 
				hm.put("sentDate", "");
			hm.put("hasatt", msslist.get(i)[2]);
			hm.put("isseen", msslist.get(i)[0]);
			hm.put("mto", msslist.get(i)[3]);
			hm.put("mfrom", msslist.get(i)[4]);
			hm.put("subject", msslist.get(i)[5]);
			hm.put("msgSize", msslist.get(i)[7]);
			hm.put("cc", msslist.get(i)[8]);
			hm.put("bcc", msslist.get(i)[9]);
			list.add(hm);
		}
		//JsonConfig cfg = createMailMessageJsonConfig();
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, list);
		return error;
	}

	/**
	 * 获取默认账户的邮件列表 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
		public static String deflistmail(HttpServletRequest req,
				HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
				throws Exception, IOException {
			String error;
			if (user == null) {
				return null;
			}
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			MailAccount ma = new MailAccount();

			// 邮箱的文件夹
			String folderidStr = (String) jsonParams.get("folderid");
			folderidStr = folderidStr == null || folderidStr.length() == 0 ? null
					: folderidStr;
			Long folderid = folderidStr != null ? Long
					.parseLong((String) jsonParams.get("folderid"))
					: MailFolders.INBOX_ID;
			List<MailAccount> accoutlists = mailservice.findALLbyUser(user);
			List<Object[]> msslist;
			if (accoutlists!=null && accoutlists.size()>0)
			{
				MailFolders mf = mailservice.findMailFolderById(folderid);
				for (int i = 0; i<accoutlists.size(); i++) {
					if(accoutlists.get(i).getIsdefault())
					{
						ma=accoutlists.get(i);
						break;
					}
				}
				int length = Integer.parseInt((String) jsonParams.get("length"));
				if(length == 0)
					length=15;
				String sort = (String) jsonParams.get("sort");
				msslist = mailservice.findMailmessages(ma, mf, sort,
						0, length);
			}
			else
			{
				msslist=new ArrayList<Object[]>();
			}
			List<HashMap<Object, Object>> list = new ArrayList<HashMap<Object, Object>>();
			for(int i = 0;i!=msslist.size();i++)
			{
				HashMap<Object, Object> hm = new HashMap<Object, Object>();
				hm.put("id", msslist.get(i)[0]);
				hm.put("mfrom", msslist.get(i)[1]);
				hm.put("subject", msslist.get(i)[2]);
				if(msslist.get(i)[3]!=null)
					hm.put("sentDate", msslist.get(i)[3].toString());
				else 
					hm.put("sentDate", null);
				list.add(hm);
			}
			//JsonConfig cfg = createMailMessageJsonConfig();
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, list);
			
			return error;
		}
		
		
	/**
	 *  收邮件，先然后判断该邮件在数据库有无，如果无，则收取
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String receive(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		String statuskey = null;
		MailBean mb = null;
		try {
			if (user == null) {
				return null;
			}
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
					.getInstance().getBean(DefaultMailServerConfig.NAME);
			Long id = Long.parseLong((String) jsonParams.get("accmail"));
			if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return error;
			}
			MailAccount ma = mailservice.findMailAccountById(id);
			if (ma == null || ma.getId()==null) {
				return null;
			}
			statuskey = ma.getId() + mailreceiveAction;
			if (hmReceive.get(statuskey)!=null) {
				// 收的过程中不在收，避免收取两封相同的。
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return error;
			}
			HashMap<String, Object> rsta = new HashMap<String, Object>();
			hmReceive.put(statuskey, rsta);
			MailFolders mf = mailservice
					.findMailFolderById(MailFolders.INBOX_ID);
			// 建立连接
			mb = new MailBean(ma);
			mb.connectReceiveStore();
			String mid = null;
			List<MailMessages> msslist = mailservice.findMailMessages(ma, mf,
					"", 0, 1);
			if (msslist.size() != 0) {
				mid = msslist.get(0).getUID();
			}
			ArrayList<String> uidlists = mb.getMessagesId(ma,mailservice,mid);
			rsta.put("newcount",uidlists.size());
			for (int i = 0 , size = uidlists.size(); i < size; i++) {// 从最新的开始收
				try {
					
					rsta.put("progress",(uidlists.size() - i));
					hmReceive.put(statuskey, rsta);
					String uid = uidlists.get(i);
					MailMessages entities = null;
					MimeMessage mimms = (MimeMessage) mb.getMessage(uid);
					if (mailconfig.getStorageenabled()) {
						if (mailconfig.getStoragetype() == DefaultMailServerConfig.FOLDER) {
							String path = mailconfig.getStoragelocation()
									+ File.separatorChar + user.getId();
							File f = new File(path);
							f.mkdirs();
							String name = System.currentTimeMillis() + ".eml";
							mb.connectReceiveStore();
							String emailpath = ParseMimeMessage
									.saveMessageAsFile(mimms, path, name);
							String mailpath = path + File.separatorChar + name;
							ParseMimeMessage pm = new ParseMimeMessage(
									(MimeMessage) Context
											.openmail(new FileInputStream(
													mailpath)));
							entities = convertMessages(pm, ma, mf, false);
							entities.setSourcepath(emailpath);
							entities.setUID(uid);
						} else {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(
									512);
							mb.connectReceiveStore();
							mimms.writeTo(baos);
							MailSources ms = new MailSources();
							byte[] content = baos.toByteArray();
							ms.setContent(content);
							if (content.length < maxsize) {
								try {
									mailservice.saveMailSources(ms);
								} catch (Exception e) {// 可能太大，存不下去
									e.printStackTrace();
								}
							}
							ParseMimeMessage pm = new ParseMimeMessage(
									(MimeMessage) Context.openmail(content));
							entities = convertMessages(pm, ma, mf, false);
							baos.close();
							entities.setSource(ms);
							entities.setUID(uid);
						}
					} else {
						ParseMimeMessage pm = new ParseMimeMessage(mimms);
						entities = convertMessages(pm, ma, mf, false);
						entities.setUID(uid);
					}
					mailservice.saveMailMessage(entities);
					((POP3Message) mimms).invalidate(true);

				} catch (Exception e) {
					e.printStackTrace();
					resetMailBean(mb);
					mb=null;
					if (e instanceof
							org.springframework.dao.DataIntegrityViolationException)
						break;
				}
			}
			hmReceive.remove(statuskey);
			resetMailBean(mb);
			mb=null;
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		} catch (Exception ee) {
			hmReceive.remove(statuskey);
			String message = null;
			if (ee instanceof javax.mail.AuthenticationFailedException) {
				//hmReceive.remove(statuskey);
				message = ((javax.mail.AuthenticationFailedException) ee)
						.getMessage();
				message = new String(ASCIIUtility.getBytes(message), "GBK");
			}
			resetMailBean(mb);
			mb=null;
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
					message);
		} finally {

		}
		return error;

	}
    
	/**
	 * 后台接收邮件
	 * @author temp
	 *
	 */
	@SuppressWarnings("unused")
	private static class Backgroundreceive implements Runnable {
		MailAccount ma;
		MailBean mb;
		ArrayList<String> uidlists;
		DefaultMailServerConfig mailconfig;
		MailService mailservice;
		long userId;

		public Backgroundreceive(MailAccount ma, MailBean mb,
				ArrayList<String> uidlists, long userId) {
			mailservice = (MailService) ApplicationContext.getInstance()
					.getBean(MailService.NAME);
			mailconfig = (DefaultMailServerConfig) ApplicationContext
					.getInstance().getBean(DefaultMailServerConfig.NAME);
			this.ma = ma;
			mb.setRunning(true);
			this.mb = mb;
			// mb.connectReceiveStore();
			this.uidlists = uidlists;
			this.userId = userId;
		}

		@Override
		public void run() {
			try {
				MailFolders mf = mailservice
						.findMailFolderById(MailFolders.INBOX_ID);
				// MailBean mb = this.mb;
				mb.connectReceiveStore();
				for (int i = 0; i < uidlists.size(); i++) {
					// mb.setRunning(true);
					String uid = uidlists.get(i);
					MailMessages entities = null;
					MimeMessage mimms = (MimeMessage) mb.getMessage(uid);
					if (mailconfig.getStorageenabled()) {
						if (mailconfig.getStoragetype() == DefaultMailServerConfig.FOLDER) {
							String path = mailconfig.getStoragelocation()
									+ File.separatorChar + userId;
							File f = new File(path);
							f.mkdirs();
							String name = System.currentTimeMillis() + ".eml";
							mb.connectReceiveStore();
							String emailpath = ParseMimeMessage
									.saveMessageAsFile(mimms, path, name);
							String mailpath = path + File.separatorChar + name;
							ParseMimeMessage pm = new ParseMimeMessage(
									(MimeMessage) Context
											.openmail(new FileInputStream(
													mailpath)));
							entities = convertMessages(pm, ma, mf, false);
							entities.setSourcepath(emailpath);
							entities.setUID(uid);
						} else {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(
									512);
							mb.connectReceiveStore();
							mimms.writeTo(baos);
							MailSources ms = new MailSources();
							byte[] content = baos.toByteArray();
							ms.setContent(content);
							if (content.length < maxsize) {
								try {
									mailservice.saveMailSources(ms);
								} catch (Exception e) {// 可能太大，存不下去
									e.printStackTrace();
								}
							}
							ParseMimeMessage pm = new ParseMimeMessage(
									(MimeMessage) Context.openmail(content));
							entities = convertMessages(pm, ma, mf, false);
							baos.close();
							entities.setSource(ms);
							entities.setUID(uid);
						}
					} else {
						ParseMimeMessage pm = new ParseMimeMessage(mimms);
						entities = convertMessages(pm, ma, mf, false);
						entities.setUID(uid);
					}
					mailservice.saveMailMessage(entities);
					((POP3Message) mimms).invalidate(true);
				}
				mb.closeconnect();
			} catch (Exception e) {
				e.printStackTrace();
				resetMailBean(mb);
				mb=null;
			}
		}
	}
	
	
/**
 * 返回接收邮件的状态
 * @param req
 * @param resp
 * @param jsonParams
 * @return
 * @throws Exception
 * @throws IOException
 */
	
	public static String receivestatus(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		if (ma == null) {
			return null;
		}
		String statuskey = ma.getId() + mailreceiveAction;
		HashMap<String, Object> rst = hmReceive.get(statuskey); 
		if (rst == null) {
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		} else {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			
			//hm.put("isbg", isbg);
			hm.put("newcount", rst.get("newcount"));
			hm.put("progress", rst.get("progress"));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
		}
		return error;
	}
    
/**
 * 邮件接收状态类
 * @author temp
 *
 */
	@SuppressWarnings("unused")
	private static class Receivestatus {
		int progress = -1;//还需要接收的数
		int newcount = -1;//邮件总数
		boolean isbg;
		boolean invalidate;// 是否失效.
	}

	/**
	 *  阅读邮件
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String open(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user) throws Exception, IOException {
		String error;
		MailBean mb = null;
		try {
			if (user == null) {
				return null;
			}
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
					.getInstance().getBean(DefaultMailServerConfig.NAME);
			Long id = Long.parseLong((String) jsonParams.get("id"));
			Long aid = Long.parseLong((String) jsonParams.get("accmail"));
			if(!checkId(user, aid, mailservice))//验证该邮箱id是否属于该用户
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return error;
			}
			MailAccount ma = mailservice.findMailAccountById(aid);
			if(!checkmailId(ma, id, mailservice))//验证该邮件id是否属于该邮箱账户
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return error;
			}
			boolean isandroid = jsonParams.get("isandroid") == null ? false
					: (Boolean) jsonParams.get("isandroid");// 来自android的请求
			boolean mailbodyFlag = jsonParams.get("mailbody") == null ? false
					: (Boolean) jsonParams.get("mailbody");// 来自android的请求

			MailMessages mms = mailservice.findMailMessageById(id);
			if (mms == null) {
				return null;
			}
			HashMap<String, Object> hm = new HashMap<String, Object>();
			MimeMessage mimms = null;
			if (mms.getSourcepath() != null) {
				String path = mailconfig.getStoragelocation() + File.separator
						+ user.getId();
				String emailpath = path + File.separator + mms.getSourcepath();
				File file = new File(emailpath);
				if (file.exists()) {
					mimms = Context.openmail(new FileInputStream(emailpath));
				}
			} else if (mms.getSource() != null) {
				mimms = Context.openmail(mms.getSource().getContent());
			}
			if (mimms == null) {
				if (mms.getUID() != null && mms.getUID().length() != 0) {
					mb = new MailBean(mms.getAccount());
					mb.connectReceiveStore();
					mimms = (MimeMessage) mb.getMessage(mms.getUID());// 如果没有保存，则从邮件服务器取。
					if (mimms != null) {
						if (mailconfig.getStorageenabled()) {
							if (mailconfig.getStoragetype() == DefaultMailServerConfig.FOLDER) {
								String path = mailconfig.getStoragelocation()
										+ File.separatorChar + user.getId();
								String emailpath = ParseMimeMessage
										.saveMessageAsFile(mimms, path);
								mms.setSourcepath(emailpath);
							} else {
								ByteArrayOutputStream baos = new ByteArrayOutputStream(
										512);
								mimms.writeTo(baos);
								MailSources ms = new MailSources();
								ms.setContent(baos.toByteArray());
								if (mimms.getSize() < maxsize) {
									try {
										mailservice.saveMailSources(ms);
									} catch (Exception e) {// 可能太大，存不下去
										e.printStackTrace();
									}
								}
								baos.close();
								mms.setSource(ms);
							}
							mailservice.updateMailMessage(mms);
						}
						resetMailBean(mb);
						mb=null;
						if (mms.getSourcepath() != null) {
							mimms = Context.openmail(new FileInputStream(
									mailconfig.getStoragelocation()
											+ File.separator + user.getId()
											+ File.separator
											+ mms.getSourcepath()));
						} else if (mms.getSource() != null) {
							mimms = Context.openmail(mms.getSource()
									.getContent());
						}
					}
				}
			}
			String mailbody = null;
			if (mimms == null) {
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						"邮件不存在");
			} else {
				ParseMimeMessage pmm = new ParseMimeMessage(mimms);
				mailbody = pmm.getBodyText(true);
				mailbody = pmm.handleCid(mailbody, cidURL(id, req));
				if (isandroid) {
					/*
					 * if (mailbodyFlag) {
					 * 
					 * }
					 */
				} else {
					// mailbody = pmm.getBodyText(true);
					// mailbody = pmm.handleCid(mailbody, cidURL(id,req));
					hm.put("mailbody", mailbody);
				}
				String subject = mms.getSubject();
				if (subject == null || subject.length() == 0) {
					subject = "(无主题)";
				}
				hm.put("subject", subject);
				hm.put("from", StringUtil.htmltextencoder(mms.getMfrom()));
				hm.put("to", StringUtil.htmltextencoder(mms.getMto()));
				hm.put("cc", StringUtil.htmltextencoder(mms.getCc()));
				hm.put("bcc", StringUtil.htmltextencoder(mms.getBcc()));
				SimpleDateFormat bartDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				if (mms.getSentDate() != null)
					hm.put("sentdate", bartDateFormat.format(mms.getSentDate()));
				ArrayList<HashMap<Object, Object>> ojv = pmm.getContainAttach();
				ArrayList<HashMap<Object, Object>> kk = new ArrayList<HashMap<Object, Object>>();
				if (ojv != null) {
					for (HashMap<Object, Object> hashMap : ojv) {
						String cid = (String) hashMap.get("cid");
						if (cid != null) {
							if (!pmm.findCid(mailbody,
									cid.substring(1, cid.length() - 1))) {
								kk.add(hashMap);
							}
						} else {
							kk.add(hashMap);
						}
					}
				}
				if (kk.size() > 0)
					hm.put("attachments", kk);
				if (kk.size() == 0) {
					mms.setHasatt(false);
					hm.put("attachments",null);
				}
				hm.put("accountmail", ma.getEmail());

				hm.put("isNotification", mms.getIsNotification());
				if (isandroid && mailbodyFlag) {
					error = mailbody;
				} else {
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
				}
			}
			mms.setIsseen(true);
			mailservice.updateMailMessage(mms);// 标记为已读

		} catch (Exception ee) {
			resetMailBean(mb);
			mb=null;
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
	}

	/**
	 *  选中邮件，点击删除
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String delete(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		MailBean mb = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		String obj = (String) jsonParams.get("id");
		Long[] lists = null;
		if (obj.indexOf(',') != -1) {
			String[] strs = obj.split(",");
			lists = new Long[strs.length];
			for (int i = 0; i < strs.length; i++) {
				Long mailid = Long.parseLong((String) strs[i]);
				lists[i] = mailid;
			}
		} else {
			lists = new Long[] { Long.parseLong((String) jsonParams.get("id")) };
		}
		//
		MailFolders mf = mailservice.findMailFolderById(MailFolders.TRASH_ID);
		for (int i = 0; i < lists.length; i++) {
			MailMessages mms = mailservice.findMailMessageById(lists[i]);
			mms.setIsNotification(false);
			if (!mms.getFolder().getId().equals(mf.getId()))// 删除邮件去垃圾箱
			{
				mms.setFolder(mf);
				mailservice.updateMailMessage(mms);
			} else// 从垃圾箱里删除
			{
				mailservice.delMailMessage(mms);// 有时删除不掉啊
				/*
				 * mms.setFolder(null); mms.setIsremoved(true);
				 * mailservice.updateMailMessage(mms);
				 */
				if (mms.getUID() != null) {
					if (mb == null) {
						mb = new MailBean(mms.getAccount());
						mb.connect(Folder.READ_WRITE);
					}
					mb.delMessage(mms.getUID());
				}
			}
		}
		if (mb != null) {
			resetMailBean(mb);
			mb=null;
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 *  在回收站里删除邮件
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String clearTrash(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		MailBean mb = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		MailFolders mf = mailservice.findMailFolderById(MailFolders.TRASH_ID);
		List<MailMessages> lists = mailservice.findAllMailMessages(ma, mf);
		mailservice.delMailMessages(lists);
		mb = new MailBean(ma);
		mb.connect(Folder.READ_WRITE);
		for (MailMessages mailMessages : lists) {
			mb.delMessage(mailMessages.getUID());
		}
		resetMailBean(mb);
		mb=null;
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 *  移动邮件，包括移动到回收站
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws Exception
	 * @throws IOException
	 */
	public static void move(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user) throws Exception, IOException {
		String error;
		try {
			if (user == null) {
				return;
			}
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			Long id = Long.parseLong((String) jsonParams.get("id"));
			MailMessages mms = mailservice.findMailMessageById(id);
			//
			Long folderid = Long.parseLong((String) jsonParams.get("folderid"));
			MailFolders mf = mailservice.findMailFolderById(folderid);
			mms.setFolder(mf);

			mailservice.updateMailMessage(mms);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		} catch (Exception ee) {
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);
	}

	public static String save(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user) throws Exception, IOException {
		return send(req, resp, jsonParams, true,  user);
	}

	public static String send(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user) throws Exception, IOException {
		return send(req, resp, jsonParams, false,  user);
	}
	
	/**
	 * 发送回执
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String sendnotification(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		MailFolders sentmf = mailservice
				.findMailFolderById(MailFolders.SENT_ID);

		MailFolders outboxmf = mailservice
				.findMailFolderById(MailFolders.OUTBOX_ID);

		MailFolders draftmf = mailservice
				.findMailFolderById(MailFolders.DRAFT_ID);
		// mailid
		MailMessages mm = null;
		String mailid = (String) jsonParams.get("mailid");
		if (mailid != null && mailid.length() > 0) {
			long id = Long.parseLong(mailid);
			mm = mailservice.findMailMessageById(id);
		}
		MailAccount ma = mm.getAccount();
		HashMap<String, Object> newjsonParams = new HashMap<String, Object>();
		String mto = mm.getMfrom();
		String subject = "已读:" + mm.getSubject();
		SimpleDateFormat bartDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		String content = "";
		try {
			content = "这是邮件收条，收件人:" + "<br>" + StringUtil.htmltextencoder(mto)
					+ "<br>" + "原邮件主题:" + mm.getSubject() + "<br>" + "<br>"
					+ "此收条表明收件人的电脑上曾显示过此邮件，显示时间:"
					+ bartDateFormat.format(new Date());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MailMessages mms = new MailMessages();
		// 邮件生成后，保存至数据库，
		mms.setAccount(ma);
		String from = ma.getEmail();
		if (ma.getPersonName() != null) {
			from = ma.getPersonName() + "<" + from + ">";
		}
		mms.setMfrom(from);
		mms.setMto(mto);
		// mms.setCc(cc);
		// mms.setBcc(bcc);
		mms.setSentDate(new Date());
		mms.setSubject(subject);
		mms.setFolder(outboxmf);
		mms.setHasatt(false);

		newjsonParams.put("mto", mto);
		newjsonParams.put("subject", subject);
		newjsonParams.put("content", content);
		boolean issuccess = false;
		javax.mail.Message mess = null;
		MailBean mb = null;
		try {
			mb = new MailBean(ma);
			mess = mb.createMessage();
			Context.createMessage((MimeMessage) mess, from,
					new String[] { mto }, null, null, subject, content, null,
					null, false, 0);
			mb.connectSendTrans();
			mb.send(mess);
			resetMailBean(mb);
			mb=null;
			issuccess = true;
			mms.setFolder(sentmf);
		} catch (Exception e) {
			resetMailBean(mb);
			mb=null;
			e.printStackTrace();
		}
		if (mailconfig.getStorageenabled()) {
			if (mailconfig.getStoragetype() == DefaultMailServerConfig.FOLDER) {
				String path = mailconfig.getStoragelocation()
						+ File.separatorChar + user.getId();
				String emailpath = ParseMimeMessage.saveMessageAsFile(mess,
						path);
				mms.setSourcepath(emailpath);
				File f = new File(path + File.separatorChar + emailpath);
				if (f.exists()) {
					FileInputStream fis = new FileInputStream(f);
					mms.setMsgSize(fis.available());
					fis.close();
				}

			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
				try {
					mess.writeTo(baos);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MailSources source = new MailSources();
				source.setContent(baos.toByteArray());
				mms.setMsgSize(source.getContent().length);
				mailservice.saveMailSources(source);
				mms.setSource(source);
				baos.close();
			}
		}
		mms.setIsseen(true);

		if (!issuccess) {
			mms.setFolder(draftmf);// 发送失败存入草稿箱...
		}
		mailservice.saveMailMessage(mms);
		// 修改原始邮件
		mm.setIsNotification(false);
		mailservice.updateMailMessage(mm);
		return null;
	}

	/**
	 *  发送邮件，先进入，发送成功，之后，在移动至已发送
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param issaveDraft
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String send(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, boolean issaveDraft, Users user)
			throws Exception, IOException {
		String error = null;
		try {
			if (user == null) {
				return null;

			}
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
					.getInstance().getBean(DefaultMailServerConfig.NAME);
			MailMessages mms = null;
			boolean isnew = false;
			Long aid = 1l;
			try
			{
				aid=Long.parseLong((String) jsonParams.get("accmail"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if(!checkId(user, aid, mailservice))//验证该邮箱id是否属于该用户
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return error;
			}
			MailAccount ma = mailservice.findMailAccountById(aid);
			if(ma!=null)
			{
				String mailid = (String) jsonParams.get("mailid");
				if (mailid != null && mailid.length() > 0) {
					long id = Long.parseLong(mailid);
					if(!checkmailId(ma, id, mailservice))//验证该邮件id是否属于该邮箱账户
					{
						error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
						return error;
					}
					mms = mailservice.findMailMessageById(id);
				}
				// 邮件如果来自非草稿箱的,属于非法传入
				if (mms == null
						|| mms.getFolder().getId().longValue() != MailFolders.DRAFT_ID) {
					isnew = true;
					mms = new MailMessages();
				}
				MailFolders sentmf = mailservice
						.findMailFolderById(MailFolders.SENT_ID);
	
				MailFolders outboxmf = mailservice
						.findMailFolderById(MailFolders.OUTBOX_ID);
	
				MailFolders draftmf = mailservice
						.findMailFolderById(MailFolders.DRAFT_ID);
	
				String from = ma.getEmail();
				if (ma.getPersonName() != null) {
					from = ma.getPersonName() + "<" + from + ">";
				}
				// zhr<zhr@yozosoft.com>
				String to = (String) jsonParams.get("mto");
				String[] tos = to != null ? to.split(",") : null;
				String bcc = (String) jsonParams.get("bcc");
				bcc = bcc == null || bcc.length() == 0 ? null : bcc;
				String[] bccs = bcc != null ? bcc.split(",") : null;
				String cc = (String) jsonParams.get("cc");
				cc = cc == null || cc.length() == 0 ? null : cc;
				String[] ccs = cc != null ? cc.split(",") : null;
				String subject;
				String content;
				int important = 0;
				boolean isNotification = false;
				Object obj = jsonParams.get("notification");
				if (obj != null) {
					isNotification = (Boolean) obj;
				}
				boolean isandroid = jsonParams.get("isandroid") == null ? false
						: (Boolean) jsonParams.get("isandroid");// 来自android的请求
				if (!isandroid) {
					// tomcat
					subject = req.getParameter("subject");
					subject = WebTools.converStr(subject);
					subject = URLDecoder.decode(subject, "UTF-8");
	
					content = req.getParameter("content");
					content = WebTools.converStr(content);
					content = URLDecoder.decode(content, "UTF-8");// /这里还需要？？？
				} else {
					InputStream in = req.getInputStream();
					byte[] len = new byte[4];
					byte[] sub = null;
					int size = 0;
					while ((size = in.read(len)) >= 0) {
						if (size == 4) {
							sub = new byte[byteToInt(len)];
							break;
						}
					}
					size = 0;
					int off = 0;
					int length = sub.length;
					while ((size = in.read(sub, off, length)) >= 0) {
						off += size;
						length -= size;
						if (off == sub.length) {
							break;
						}
					}
					subject = new String(sub, "UTF-8");
	
					byte[] con = null;
					size = 0;
					while ((size = in.read(len)) >= 0) {
						if (size == 4) {
							con = new byte[byteToInt(len)];
							break;
						}
					}
					content = null;
					off = 0;
					length = con.length;
					while ((size = in.read(con, off, length)) >= 0) {
						off += size;
						length -= size;
						if (off == con.length) {
							break;
						}
					}
					content = new String(con, "UTF-8");
				}
				ArrayList<String> removefile = new ArrayList<String>();
				ArrayList<String> strcids = findcidfrommailbody(content);
				if (strcids.size() != 0) {
					content = replaceallcid(content);
				}
				Vector<BodyPart> lll = new Vector<BodyPart>();
				if (strcids.size() != 0) {
					String mid = strcids.get(0);
					MailMessages tmms = mailservice.findMailMessageById(Long
							.parseLong(mid));
					MimeMessage mimms = null;
					if (tmms != null && tmms.getSourcepath() != null) {
						mimms = Context.openmail(new FileInputStream(mailconfig
								.getStoragelocation()
								+ File.separator
								+ user.getId()
								+ File.separator
								+ tmms.getSourcepath()));
					} else if (tmms != null && tmms.getSource() != null) {
						mimms = Context.openmail(tmms.getSource().getContent());
					}
					ParseMimeMessage pm = new ParseMimeMessage(mimms);
					for (int i = 1; i < strcids.size(); i++) {
						Object[] ojb = pm.getCidAttachMent(mimms, strcids.get(i));
						lll.add((BodyPart) ojb[2]);
					}
				}
				strcids = findimgfrommailbody(content);
				if (strcids.size() != 0) {
					content = replaceallimgcid(content);
				}
				for (int i = 0; i < strcids.size(); i++) {
				BodyPart gifPart = new MimeBodyPart();
				DataSource gifDs = new FileDataSource(WebConfig.webContextPath+strcids.get(i));
	        	DataHandler gifDh = new DataHandler(gifDs);
	        	gifPart.setDataHandler(gifDh);
	        	String cidString  = System.currentTimeMillis()+generateMixString(6)+strcids.get(i).substring(strcids.get(i).indexOf("."),strcids.get(i).indexOf(".")+4);
	        	gifPart.setFileName(cidString);
	        	gifPart.setHeader("Content-ID", cidString);
	        	content=content.replaceAll(strcids.get(i), cidString);
	        	lll.add(gifPart);
				}
				
				Date sentDate = new Date();
	
				String atts = (String) jsonParams.get("atts");// fileid[time,time2]//见上传附件
				String[] att = atts != null && atts.length() != 0 ? atts.split(",")
						: null;
	
				Vector<String> file = new Vector<String>();
				if (att != null) {
					for (int i = 0; i < att.length; i++) {
						String object = att[i];
						File par = new File(WebConfig.tempFilePath
								+ File.separatorChar + object);
						removefile.add(WebConfig.tempFilePath + File.separatorChar
								+ object);
						File[] childFs = par.listFiles();
						if (childFs != null && childFs.length == 1) {
							file.add(childFs[0].getPath());
						}
					}
				}
	
				// TODO 附件？？还有转发？如何处理？回复？
				// -----如果是转发。带有附件的。
	
				String forwardmailid = (String) jsonParams.get("forwardmailid");
				if (forwardmailid != null && forwardmailid.length() != 0) {
					String forwardatts = (String) jsonParams.get("forwardatts");// 此处的数值,可参考下载附件的partno
					String[] forwardatt = forwardatts != null
							&& forwardatts.length() != 0 ? forwardatts.split(",")
							: null;
					if (forwardatt != null) {
						MailMessages tmms = mailservice.findMailMessageById(Long
								.parseLong(forwardmailid));
						MimeMessage mimms = null;
						if (tmms.getSourcepath() != null) {
							mimms = Context.openmail(new FileInputStream(mailconfig
									.getStoragelocation()
									+ File.separator
									+ user.getId()
									+ File.separator
									+ tmms.getSourcepath()));
						} else if (tmms.getSource() != null) {
							mimms = Context.openmail(tmms.getSource().getContent());
						}
						ParseMimeMessage pm = new ParseMimeMessage(mimms);
						for (int i = 0; i < forwardatt.length; i++) {
							long time = System.currentTimeMillis();
							String tempPath = WebConfig.tempFilePath
									+ File.separatorChar + time;// 每次上传都会保存在一个新建文件夹内
							File tempfile = new File(tempPath);
							tempfile.mkdirs();
							removefile.add(tempPath);
							String fil = pm
									.saveAttachMent(mimms,
											Integer.parseInt(forwardatt[i]), null,
											tempPath);
							file.add(fil);
						}
					}
				}
				String webdocatts = (String) jsonParams.get("webdocatts");// 从文档中心选文件path<>filename,path<>filename
				String[] webdocatt = webdocatts != null && webdocatts.length() != 0 ? webdocatts
						.split(",") : null;
				if (webdocatt != null) {
					for (int i = 0; i < webdocatt.length; i++) {
						long time = System.currentTimeMillis();
						String tempPath = WebConfig.tempFilePath
								+ File.separatorChar + time;// 每次上传都会保存在一个新建文件夹内
						File tempfile = new File(tempPath);
						tempfile.mkdirs();
						removefile.add(tempPath);
						String[] strs = webdocatt[i].split("<>");
						String filePath = strs[0];
						String name = strs[1];
						String filePathname = tempPath + File.separatorChar + name;
						File tfile = new File(filePathname);
						if (!tfile.exists()) {
							JCRService jcrService = (JCRService) ApplicationContext
									.getInstance().getBean(JCRService.NAME);
							InputStream in;
	
							int idx = filePath
									.indexOf("jcr:system/jcr:versionStorage");
							int idx2 = filePath.indexOf("system_audit_root");
							if (idx > -1) {
								in = jcrService.getVersionContent(filePath, name);
							} else if (idx2 > -1) {
								in = jcrService.getContent(filePath);
							} else {
								in = jcrService.getContent(null, filePath, false); // 从文件库中获取文件流
							}
							tfile.createNewFile();
							FileOutputStream out = new FileOutputStream(tfile);
							byte[] b = new byte[8 * 1024];
							int len = 0;
							while ((len = in.read(b)) > 0) {
								out.write(b, 0, len);
							}
							out.close();
						}
						file.add(filePathname);
					}
				}
				// 邮件生成后，保存至数据库，
				mms.setAccount(ma);
				mms.setMfrom(from);
				mms.setMto(to);
				mms.setCc(cc);
				mms.setBcc(bcc);
				mms.setSentDate(sentDate);
				mms.setSubject(subject);
				mms.setFolder(outboxmf);
				mms.setHasatt(file.size() != 0);
				mms.setIsNotification(false);
				boolean issuccess = false;
				javax.mail.Message mess = null;
				MailBean mb = null;
				try {
					mb = new MailBean(ma);
					if (!issaveDraft) {
						mb.connectSendSession();
					}
					mess = mb.createMessage();
					if (!isandroid && !issaveDraft
							&& mailconfig.getMailtag() != null) {
						content += mailconfig.getMailtag();
					}
					Context.createMessage((MimeMessage) mess, from, tos, ccs, bccs,
							subject, content, file, lll, isNotification, important);
					if (!issaveDraft) {
						mb.connectSendTrans();
						mb.send(mess);
						resetMailBean(mb);
						mb=null;
					}
					issuccess = true;
					mms.setFolder(sentmf);
	
				} catch (Exception e) {
					String message = null;
					if (e instanceof MessagingException) {
						MessagingException eee = (javax.mail.MessagingException) e;
						message = eee.getMessage();
						if (eee.getNextException() != null)
							message += eee.getNextException().getMessage();
						message = StringUtil.htmltextencoder(message);
					}
					resetMailBean(mb);
					mb=null;
					e.printStackTrace();
					error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
							message);
				}
				if (mailconfig.getStorageenabled()) {
					if (mailconfig.getStoragetype() == DefaultMailServerConfig.FOLDER) {
						String path = mailconfig.getStoragelocation()
								+ File.separatorChar + user.getId();
						String emailpath = ParseMimeMessage.saveMessageAsFile(mess,
								path);
						mms.setSourcepath(emailpath);
						File f = new File(path + File.separatorChar + emailpath);
						if (f.exists()) {
							FileInputStream fis = new FileInputStream(f);
							mms.setMsgSize(fis.available());
							fis.close();
						}
	
					} else {
						ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
						mess.writeTo(baos);
						MailSources source = new MailSources();
						source.setContent(baos.toByteArray());
						mms.setMsgSize(source.getContent().length);
						mailservice.saveMailSources(source);
						mms.setSource(source);
						baos.close();
					}
				}
				mms.setIsseen(true);
				if (issaveDraft) {
					mms.setIsseen(false);
					mms.setFolder(draftmf);
				}
				if (!issuccess) {
					mms.setFolder(draftmf);// 发送失败存入草稿箱...
				}
				if (isnew) {
					mailservice.saveMailMessage(mms);
				} else {
					mailservice.updateMailMessage(mms);
				}
				if (issuccess) {
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
				}
				if (removefile != null) {
					for (Iterator<String> iterator = removefile.iterator(); iterator
							.hasNext();) {
						String string = (String) iterator.next();
						File tfile = new File(string);
						FileUtil.deleteFile(tfile);
					}
				}
			}else {
				error = JSONTools.convertToJson(MAILACCOUNT_NULL, null);
			}
		}catch (Exception ee) {
				String message = null;
				if (ee instanceof javax.mail.AuthenticationFailedException) {
					message = ((javax.mail.AuthenticationFailedException) ee)
							.getMessage();
				}
				ee.printStackTrace();
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						message);
			}
		
		if (error == null)
			error = "";
		return error;
	}

	//转化为mailMessage
	
	public static MailMessages convertMessages(ParseMimeMessage msg,
			MailAccount acnt, MailFolders folder, boolean isbg)
			throws Exception {
		MailMessages message = new MailMessages();
		message.setAccount(acnt);
		message.setBcc(msg.getMailAddress("bcc"));
		message.setMto(msg.getMailAddress("to"));
		message.setCc(msg.getMailAddress("cc"));
		message.setFolder(folder);// ???
		message.setMfrom(msg.getFrom());
		if (!isbg) {
			ArrayList<HashMap<Object, Object>> ojv = msg.getContainAttach();
			ArrayList<HashMap<Object, Object>> kk = new ArrayList<HashMap<Object, Object>>();
			if (ojv != null) {
				for (HashMap<Object, Object> hashMap : ojv) {
					kk.add(hashMap);
				}
			}
			message.setHasatt(kk.size() != 0);// 耗时
		}
		String importance = msg.getimportance();
		if (importance == MailMessages.IMP_LOW) {
			message.setImpflag(MailMessages.IMPFlag.IMP_LOW);
		} else if (importance == MailMessages.IMP_HIGH) {
			message.setImpflag(MailMessages.IMPFlag.IMP_HIGH);
		}
		message.setMsgSize(msg.getSize());
		message.setIsseen(msg.isNew());// 第一次都是未读
		message.setReceivedDate(msg.getMsgReceivedDate());
		message.setSentDate(msg.getMsgSentDate());
		message.setSubject(msg.getSubject());
		message.setIsNotification(msg.getReplySign());// 是否需要回复？
		return message;
	}

	/**
	 * 上传附件
	 * @param req
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String uploadAttach(HttpServletRequest req,
			HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws Exception, IOException {
		String error = null;
		// try {
		String fileName = WebTools.converStr(req
				.getParameter(Constants.FileName));
		fileName = FilesHandler.normalName(fileName);
		long time = System.currentTimeMillis();
		String tempPath = WebConfig.tempFilePath + File.separatorChar + time;// 每次上传都会保存在一个新建文件夹内
		File file = new File(tempPath);
		file.mkdirs();
		long maxfilesize = 10 * 1024 * 1024 * 2;
		List<String> xx = FilesHandler.fileUploadByHttpForm(req, tempPath,
				maxfilesize);
		if (xx == null) {
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		} else {
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, time);// 返回一个字符串给前端,前端会根据字符串,发送邮件
		}
		return error;
	}
	
	
		
	/**
	 * 删除附件
	 * @param req
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String delAttach(HttpServletRequest req,
			HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws Exception, IOException {
		String error = null;
		// try {
		String name = (String) jsonParams.get("fileid");
		String tempPath = WebConfig.tempFilePath + File.separatorChar + name;
		File file = new File(tempPath);
		// file.delete();
		FileUtil.deleteFile(file);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 * 下载附件
	 * @param request
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String downloadAttach(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		PrintWriter out = null;
		if (user == null) {
			return null;
		}

		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		response.reset();
		out = response.getWriter();

		String ctnttype = (String) jsonParams.get("ctnttype");
		String charset = (String) jsonParams.get("charset");
		if (ctnttype != null)
			response.setHeader("Content-Type", ctnttype
					+ ((charset != null) ? "; charset=" + charset : ""));

		Long id = Long.parseLong((String) jsonParams.get("id"));
		Integer partno = Integer.parseInt((String) jsonParams.get("partno"));

		MailMessages mms = mailservice.findMailMessageById(id);

		MimeMessage mimms = null;
		if (mms.getSourcepath() != null) {
			mimms = Context.openmail(new FileInputStream(mailconfig
					.getStoragelocation()
					+ File.separator
					+ user.getId()
					+ File.separator + mms.getSourcepath()));
		} else if (mms.getSource() != null) {
			mimms = Context.openmail(mms.getSource().getContent());
		}
		ParseMimeMessage pm = new ParseMimeMessage(mimms);

		pm.saveAttachMent(response, request, mimms, out, partno, null);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 * 附件预览
	 * @param request
	 * @param response
	 * @param jsonParams
	 * @param servlet
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String previewAttach(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams,
			HttpServlet servlet, Users user) throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		Integer partno = Integer.parseInt((String) jsonParams.get("partno"));
		MailMessages mms = mailservice.findMailMessageById(id);
		MimeMessage mimms = null;
		if (mms.getSourcepath() != null) {
			mimms = Context.openmail(new FileInputStream(mailconfig
					.getStoragelocation()
					+ File.separator
					+ user.getId()
					+ File.separator + mms.getSourcepath()));
		} else if (mms.getSource() != null) {
			mimms = Context.openmail(mms.getSource().getContent());
		}

		long time = System.currentTimeMillis();
		String tempPath = WebConfig.tempFilePath + File.separatorChar + time;// 每次上传都会保存在一个新建文件夹内
		File tempfile = new File(tempPath);
		tempfile.mkdirs();
		ParseMimeMessage pm = new ParseMimeMessage(mimms);
		String srcfile = pm.saveAttachMent(mimms, partno, null, tempPath);

		String s = UUID.randomUUID().toString();
		String fid = s.substring(0, 8) + s.substring(9, 13)
				+ s.substring(14, 18) + s.substring(19, 23) + s.substring(24);

		String filename = srcfile.substring(tempPath.length() + 1);
		String fileNamesuddix = filename.substring(filename.lastIndexOf('.'));
		String srcFolder = ConvertForRead.getSrcFold() == null ? servlet
				.getServletContext().getRealPath("srcpreviewfiles")
				: ConvertForRead.getSrcFold();
		boolean istxt = fileNamesuddix.regionMatches(true, 0, ".txt", 0, 4);
		boolean iscsv = fileNamesuddix.regionMatches(true, 0, ".csv", 0, 4);

		File file = new File(srcfile);

		if (istxt || iscsv) {
			String encode = FileUtil.getFileEncode(srcfile);
			String defaultendcode = System.getProperty("file.encoding");// Charset.defaultCharset();
			LogsUtility.error("encode:   " + encode + " defaultendcode::"
					+ defaultendcode);
			if (encode.equals("UTF-8+BOM")) {
				String outFilename = srcFolder + File.separatorChar + "temp1"
						+ fid + fileNamesuddix;
				FileUtil.UTF8BOMTOUTF8(new File(srcfile), new File(outFilename));
				srcfile = outFilename;
				file.delete();
				file = new File(outFilename);
				encode = "UTF-8";
			}
			if (encode.equals(defaultendcode)) {
				srcfile = srcFolder + File.separatorChar + fid + fileNamesuddix;
				boolean flag = file.renameTo(new File(srcfile));
				if (!flag) {
					srcfile = file.getPath();
				}
			} else {
				String outFilename = srcFolder + File.separatorChar + fid
						+ fileNamesuddix;
				FileUtil.translateCharset(srcfile, outFilename, encode,
						defaultendcode);
				srcfile = outFilename;
				file.delete();
			}
		}

		FileConvertStatus fs = null;
		String tarFolder = ConvertForRead.getTargetFold() == null ? servlet
				.getServletContext().getRealPath("tarpreviewfiles")
				: ConvertForRead.getTargetFold();

		String tarfile = tarFolder + File.separatorChar + fid;
		if (fs == null) {
			fs = ConvertForRead.createFileConvertStatus(fid, srcfile, tarfile,
					null);
			fs.setSessionid(request.getSession().getId());
		}
		if (true) {
			String url = "converttohtml.html?fid=" + fid + "&filename="
					+ URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
			;
			response.sendRedirect(url);
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
		
	}
	
	/**
	 * 是否同名文件
	 * @param request
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String isFilesExist(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}
		String path = (String) jsonParams.get("path");
		String name = (String) jsonParams.get("name");
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		name = FilesHandler.normalName(name);
		Boolean ret = jcrService.isFileExist(path + "/" + name);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		return error;
	}

	/**
	 * 保存附件到文档中心
	 * @param request
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String saveAttachTowebDoc(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}

		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);

		Long id = Long.parseLong((String) jsonParams.get("id"));
		Integer partno = Integer.parseInt((String) jsonParams.get("partno"));
		String path = (String) jsonParams.get("path");

		Long permit = service.getFileSystemAction(user.getId(), path, true);
		boolean canFlag = permit == null || permit == 0 ? false : FlagUtility
				.isValue(permit, FileSystemCons.UPLOAD_FLAG);
		if (!canFlag) {
			//
			error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
					"你没有上传的权限");
		} else {
			MailMessages mms = mailservice.findMailMessageById(id);

			MimeMessage mimms = null;
			if (mms.getSourcepath() != null) {
				mimms = Context.openmail(new FileInputStream(mailconfig
						.getStoragelocation()
						+ File.separator
						+ user.getId()
						+ File.separator + mms.getSourcepath()));
			} else if (mms.getSource() != null) {
				mimms = Context.openmail(mms.getSource().getContent());
			}
			ParseMimeMessage pm = new ParseMimeMessage(mimms);
			long time = System.currentTimeMillis();
			String tempPath = WebConfig.tempFilePath + File.separatorChar
					+ time;// 每次上传都会保存在一个新建文件夹内
			File tempfile = new File(tempPath);
			tempfile.mkdirs();
			String[] objs = pm.saveAttachMentToWebDoc(mimms, partno, null,
					tempPath);

			File file = new File(objs[0]);
			InputStream fin = new FileInputStream(file);
			InputStream ois = new FileInputStream(file);

			Fileinfo info = fileSystemService.createFile(user.getId(),
					user.getRealName(), path, objs[1], fin, ois, false, null,
					true);
			fin.close();
			file.delete();
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		}
		return error;
	}

	// 打开附件？已删除！
	public static String openAttach(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error = null;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
				.getInstance().getBean(DefaultMailServerConfig.NAME);
		Long id = Long.parseLong((String) jsonParams.get("id"));
		Integer partno = Integer.parseInt((String) jsonParams.get("partno"));
		String path = (String) jsonParams.get("path");
		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		MailMessages mms = mailservice.findMailMessageById(id);
		MimeMessage mimms = null;
		if (mms.getSourcepath() != null) {
			mimms = Context.openmail(new FileInputStream(mailconfig
					.getStoragelocation()
					+ File.separator
					+ user.getId()
					+ File.separator + mms.getSourcepath()));
		} else if (mms.getSource() != null) {
			mimms = Context.openmail(mms.getSource().getContent());
		}

		long time = System.currentTimeMillis();
		String tempPath = WebConfig.tempFilePath + File.separatorChar + time;// 每次上传都会保存在一个新建文件夹内
		File tempfile = new File(tempPath);
		tempfile.mkdirs();
		ParseMimeMessage pm = new ParseMimeMessage(mimms);
		// String srcfile = pm.saveAttachMent(mimms, partno, null, tempPath);
		// tempfile.mkdirs();
		String[] objs = pm
				.saveAttachMentToWebDoc(mimms, partno, null, tempPath);
		File file = new File(objs[0]);
		InputStream fin = new FileInputStream(file);
		InputStream ois = new FileInputStream(file);

		Fileinfo info = fileSystemService.createFile(user.getId(),
				user.getRealName(), path, objs[1], fin, ois, false, null, true);
		fin.close();
		file.delete();
		return error;
	}
	
	/**
	 * 打开邮件内容的图片
	 * @param request
	 * @param response
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String showCid(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws Exception, IOException {
		// ../mailService?jsonParams={method:"showcid"}&id=11956&contentid=_Foxmail.1@AFC0BC1A-D490-4BB0-B815-DB7DDB535F1E
		try {
			MailService mailservice = (MailService) ApplicationContext
					.getInstance().getBean(MailService.NAME);
			DefaultMailServerConfig mailconfig = (DefaultMailServerConfig) ApplicationContext
					.getInstance().getBean(DefaultMailServerConfig.NAME);
			Long id = Long.parseLong(request.getParameter("id"));
			MailMessages mms = mailservice.findMailMessageById(id);
			if (mms == null) {
				return null;
			}
			Users user = mms.getAccount().getUser();
			MimeMessage mimms = null;
			if (mms.getSourcepath() != null) {
				mimms = Context.openmail(new FileInputStream(mailconfig
						.getStoragelocation()
						+ File.separator
						+ user.getId()
						+ File.separator + mms.getSourcepath()));
			} else if (mms.getSource() != null) {
				mimms = Context.openmail(mms.getSource().getContent());
			}

			String contentid = (String) request.getParameter("contentid");
			ParseMimeMessage pm = new ParseMimeMessage(mimms);
			Object[] objs = pm.getCidAttachMent(mimms, contentid);
			if (objs == null) {
				return null;
			}
			response.setContentType("image/" + objs[1]);
			BufferedInputStream buffInput = new BufferedInputStream(
					(InputStream) objs[0]);
			BufferedOutputStream buffout = new BufferedOutputStream(
					response.getOutputStream());
			int length = -1;
			byte[] buff = new byte[1024];
			while ((length = buffInput.read(buff)) != -1)
				buffout.write(buff, 0, length);
			buffout.flush();
			((InputStream) objs[0]).close();
			buffInput.close();
			buffout.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return null;
	}

	/**
	 * 搜索邮件
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	
	public static String search(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);

		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);
		
		// 邮箱的文件夹
		String str = (String) jsonParams.get("folderid");
		MailFolders mf = null;
		if (str != null && str.length() != 0) {
			mf = mailservice.findMailFolderById(Long.parseLong(str));
		}
		int start = -1;
		str = (String) jsonParams.get("start");
		if (str != null && str.length() != 0) {
			start = Integer.parseInt(str);
		}
		int length = -1;
		str = (String) jsonParams.get("length");
		if (str != null && str.length() != 0) {
			length = Integer.parseInt(str);
		}

		String sort = null;
		str = (String) jsonParams.get("sort");
		if (str != null && str.length() != 0) {
			sort = str;
		}
		//
		String subject = null;
		str = (String) jsonParams.get("subject");
		if (str != null && str.length() != 0) {
			subject = str;
		}
		String mfrom = null;
		str = (String) jsonParams.get("mfrom");
		if (str != null && str.length() != 0) {
			mfrom = str;
		}

		String mto = null;
		str = (String) jsonParams.get("mto");
		if (str != null && str.length() != 0) {
			mto = str;
		}
		Boolean hasatt = (Boolean) jsonParams.get("hasatt");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startdate = null;
		str = (String) jsonParams.get("startdate");
		if (str != null && str.length() != 0) {
			startdate = dateFormat.parse(str);
		}
		Date enddate = null;
		str = (String) jsonParams.get("enddate");
		if (str != null && str.length() != 0) {
			enddate = dateFormat.parse(str);
		}
		Long count = mailservice.searchcountMailMessages(ma, mf, subject,
				mfrom, mto, hasatt, startdate, enddate);
//
//		List<MailMessages> msslist = mailservice.searchMailMessages(ma, sort,
//				start, length, mf, subject, mfrom, mto, hasatt, startdate,
//				enddate);
		List<Object[]> msslist = mailservice.searchMailMessage(ma, sort,
				start, length, mf, subject, mfrom, mto, hasatt, startdate,
				enddate);
		List<HashMap<Object, Object>> list = new ArrayList<HashMap<Object, Object>>();
		for(int i = 0;i!=msslist.size();i++)
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("id", msslist.get(i)[1]);
			if(msslist.get(i)[6]!=null)
				hm.put("sentDate", msslist.get(i)[6].toString());
			else 
				hm.put("sentDate", "");
			hm.put("hasatt", msslist.get(i)[2]);
			hm.put("isseen", msslist.get(i)[0]);
			hm.put("mto", msslist.get(i)[3]);
			hm.put("mfrom", msslist.get(i)[4]);
			hm.put("subject", msslist.get(i)[5]);
			hm.put("msgSize", msslist.get(i)[7]);
			hm.put("cc", msslist.get(i)[8]);
			hm.put("bcc", msslist.get(i)[9]);
			list.add(hm);
		}
		HashMap<Object, Object> hm = new HashMap<Object, Object>();
		hm.put("count", count);
		hm.put("list", list);
		//JsonConfig cfg = createMailMessageJsonConfig();
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
		return error;
	}
	
	/**
	 * 获取邮件签名
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String getSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long id = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, id, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(id);

		List<MailAccountSign> lists = mailservice.findAllsign(ma);
		JsonConfig jc = createMailAccountSignJsonConfig();
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, lists, jc);
		return error;
	}
	
	/**
	 * 保存邮箱签名
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String saveSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long aid = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, aid, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(aid);
		String signid = (String) jsonParams.get("id");
		MailAccountSign ms = null;
		if (signid != null && signid.length() > 0) {
			ms = mailservice.findsign(Long.parseLong(signid));
		} else {
			ms = new MailAccountSign();
			ms.setAccount(ma);
		}
		String sign = req.getParameter("sign");
		sign = WebTools.converStr(sign);
		sign = URLDecoder.decode(sign, "UTF-8");

		ms.setSign(sign);
		if (ms.getId() == null) {
			mailservice.createMailAccountSign(ms);
		} else {
			mailservice.updateMailAccountSign(ms);
		}

		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	/**
	 * 设置所有邮件已读
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String setallmailseen(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException {
		String error;
		if (user == null) {
			return null;
		}
		MailService mailservice = (MailService) ApplicationContext
				.getInstance().getBean(MailService.NAME);
		Long aid = Long.parseLong((String) jsonParams.get("accmail"));
		if(!checkId(user, aid, mailservice))//验证该邮箱id是否属于该用户
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
		}
		MailAccount ma = mailservice.findMailAccountById(aid);
		MailFolders mf = mailservice.findMailFolderById(MailFolders.INBOX_ID);

		mailservice.setAllMailMessagesSeen(ma, mf);

		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		return error;
	}

	public static String getAllMailDomain(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException {
		String error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
				CommonMailConfig.getAllDomain());
		return error;
	}



	private static void resetMailBean(MailBean mb) {
		if (mb != null)
			mb.reset(null);
	}

	public static void clearMailBean(HttpSession session) {
		if (session == null) {
			return;
		}
		try {
			Users user = (Users) session.getAttribute(PageConstant.LG_SESSION_USER);
			if (user != null) {
				String key = "keyMailBean" + user.getId();
				@SuppressWarnings("unchecked")
				HashMap<Long, MailBean> hm = (HashMap<Long, MailBean>) session
						.getAttribute(key);
				if (hm != null) {
					Collection<MailBean> c = hm.values();
					for (MailBean mailBean : c) {
						mailBean.reset(null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//一般邮箱的设置
	private static void handelComonMail(MailAccount ma,
			HashMap<String, Object> jsonParams) {
		String domain = null;
		String email = (String) jsonParams.get("email");
		if (domain == null && email != null) {
			domain = email.substring(email.indexOf('@') + 1);
		}
		String personName = (String) jsonParams.get("personName");
		CommonMailConfig cmc = CommonMailConfig.createCommonMailConfig(domain);
		if (cmc != null) {

			String inuser = (String) jsonParams.get("inuser");
			if (inuser == null) {
				inuser = email.substring(0, email.indexOf('@'));
			}
			String inpassword = (String) jsonParams.get("inpassword");
			if (inpassword != null && inpassword.length() != 0){
				{
					ma.setInpassword(genaratemm(inpassword));
					ma.setIscode(true);
				}
			}
			else {
				ma.setInpassword(null);
			}
			ma.setEmail(email);
			//ma.setInpassword(inpassword);
			if (personName != null && personName.length() != 0)
				ma.setPersonName(personName);
			else
				ma.setPersonName(inuser);
			ma.setReplyToemail(null);

			ma.setIncomingServer(cmc.getIncomingServer());
			ma.setIncomingport(cmc.getIncomingport());
			ma.setInSSL(cmc.getInSSL());
			ma.setIncomingServerType(MailAccount.convertServerType(cmc
					.getIncomingServerType()));
			if (cmc.inuserisFullEmailAddress()) {
				ma.setInuser(email);
			} else {
				ma.setInuser(inuser);
			}

			ma.setOutgoingServer(cmc.getOutgoingServer());
			ma.setOutgoingport(cmc.getOutgoingport());
			ma.setOutSSL(cmc.getOutSSL());
			ma.setOutgoingServerType(MailAccount.ServerType.SMTP);

			ma.setSmtpAuth(cmc.getSmtpAuth());
			ma.setSmtpAuthSameasin(cmc.getSmtpAuthSameasin());
			ma.setOutsmtpUser(null);
			ma.setOutsmtpPassword(null);
			ma.setDeleteafterdays(-1);
		} else {
			handelParam(ma, jsonParams);
		}
	}

	//设置账户信息
	private static void handelParam(MailAccount ma,
			HashMap<String, Object> jsonParams) {
		String personName = (String) jsonParams.get("personName");
		if (personName != null && personName.length() != 0)
			ma.setPersonName(personName);
		else {
			ma.setPersonName(null);
		}
		String email = (String) jsonParams.get("email");
		if (email != null && email.length() != 0)
			ma.setEmail(email);
		else {
			ma.setEmail(null);
		}

		String replyToemail = (String) jsonParams.get("replyToemail");
		if (replyToemail != null && replyToemail.length() != 0)
			ma.setReplyToemail(replyToemail);
		else {
			ma.setReplyToemail(null);
		}
		String incomingServer = (String) jsonParams.get("incomingServer");
		if (incomingServer != null && incomingServer.length() != 0)
			ma.setIncomingServer(incomingServer);
		else {
			ma.setIncomingServer(null);
		}

		String incomingportobj = (String) jsonParams.get("incomingport");
		if (incomingportobj != null) {
			int incomingport = Integer.parseInt(incomingportobj);
			ma.setIncomingport(incomingport);
		} else {
			ma.setIncomingport(-1);
		}

		String inSSLobj = (String) jsonParams.get("inSSL");
		if (inSSLobj != null && inSSLobj.length() != 0) {
			ma.setInSSL(Boolean.parseBoolean(inSSLobj));
		} else {
			ma.setInSSL(Boolean.FALSE);
		}

		String incomingServerTypeobj = (String) jsonParams
				.get("incomingServerType");
		if (incomingServerTypeobj != null
				&& incomingServerTypeobj.length() != 0) {
			int type = Integer.parseInt(incomingServerTypeobj);
			if (type == 0) {
				ma.setIncomingServerType(MailAccount.ServerType.POP3);
			}
			if (type == 1) {
				ma.setIncomingServerType(MailAccount.ServerType.SMTP);
			}
			if (type == 2) {
				ma.setIncomingServerType(MailAccount.ServerType.IMAP);
			}
		} else {
			ma.setIncomingServerType(MailAccount.ServerType.POP3);
		}

		String inuser = (String) jsonParams.get("inuser");
		if (inuser != null && inuser.length() != 0)
			ma.setInuser(inuser);
		else {
			ma.setInuser(null);
		}

		String inpassword = (String) jsonParams.get("inpassword");
		if (inpassword != null && inpassword.length() != 0){
			if(!"123456".equals(inpassword))
			{
				ma.setInpassword(genaratemm(inpassword));
				ma.setIscode(true);
			}
		}
		else {
			ma.setInpassword(null);
		}
		
		String outgoingServer = (String) jsonParams.get("outgoingServer");
		if (outgoingServer != null && outgoingServer.length() != 0)
			ma.setOutgoingServer(outgoingServer);
		else {
			ma.setOutgoingServer(null);
		}

		String outgoingportobj = (String) jsonParams.get("outgoingport");
		if (outgoingportobj != null) {
			int outgoingport = Integer.parseInt(outgoingportobj);
			ma.setOutgoingport(outgoingport);
		} else {
			ma.setOutgoingport(-1);
		}
		String outSSLobj = (String) jsonParams.get("outSSL");
		if (outSSLobj != null && outSSLobj.length() != 0) {
			ma.setOutSSL(Boolean.parseBoolean(outSSLobj));
		} else {
			ma.setOutSSL(Boolean.FALSE);
		}

		String outgoingServerTypeobj = (String) jsonParams
				.get("outgoingServerType");
		if (outgoingServerTypeobj != null
				&& outgoingServerTypeobj.length() != 0) {
			int type = Integer.parseInt(outgoingServerTypeobj);
			if (type == 0) {
				ma.setOutgoingServerType(MailAccount.ServerType.POP3);
			}
			if (type == 1) {
				ma.setOutgoingServerType(MailAccount.ServerType.SMTP);
			}
			if (type == 2) {
				ma.setOutgoingServerType(MailAccount.ServerType.IMAP);
			}
		} else {
			ma.setOutgoingServerType(MailAccount.ServerType.SMTP);
		}

		String smtpAuthobj = (String) jsonParams.get("smtpAuth");
		if (smtpAuthobj != null && smtpAuthobj.length() != 0)
			ma.setSmtpAuth(Boolean.parseBoolean(smtpAuthobj));
		else {
			ma.setSmtpAuth(Boolean.TRUE);
		}

		String smtpAuthSameasinobj = (String) jsonParams
				.get("smtpAuthSameasin");
		if (smtpAuthSameasinobj != null && smtpAuthSameasinobj.length() != 0)
			ma.setSmtpAuthSameasin(Boolean.parseBoolean(smtpAuthSameasinobj));
		else {
			ma.setSmtpAuthSameasin(Boolean.TRUE);
		}

		String outsmtpUser = (String) jsonParams.get("outsmtpUser");
		if (outsmtpUser != null && outsmtpUser.length() != 0)
			ma.setOutsmtpUser(outsmtpUser);
		else {
			ma.setOutsmtpUser(null);
		}

		String outsmtpPassword = (String) jsonParams.get("outsmtpPassword");
		if (outsmtpPassword != null && outsmtpPassword.length() != 0)
			ma.setOutsmtpPassword(outsmtpPassword);
		else {
			ma.setOutsmtpPassword(null);
		}

		String deleteafterdays = (String) jsonParams.get("deleteafterdays");
		if (deleteafterdays != null && deleteafterdays.length() != 0)
			ma.setDeleteafterdays(Integer.parseInt(deleteafterdays));
		else {
			ma.setDeleteafterdays(-1);
		}
	}

	@SuppressWarnings("unused")
	private static JsonConfig createMailMessageJsonConfig() {
		JsonConfig cfg = new JsonConfig();
		cfg.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (name.equals("folder") || name.equals("account")
						|| name.equals("source") || name.equals("sourcepath")
						|| name.equals("isremoved")) {
					return true;
				}
				return false;
			}
		});
		cfg.registerJsonValueProcessor("msgSize", new JsonValueProcessor() {

			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1,
					JsonConfig arg2) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					return "0KB";
				}
				Integer value = (Integer) arg1;
				java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
				if (value / (1024.0f * 1024) > 1) {
					return df.format(value / (1024.0f * 1024)) + "MB";
				}
				return df.format(value / (1024.0f)) + "KB";
			}
		});
		cfg.registerJsonValueProcessor("sentDate", new JsonValueProcessor() {

			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1,
					JsonConfig arg2) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					return "";
				}
				Date value = (Date) arg1;
				SimpleDateFormat bartDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				return bartDateFormat.format(value);
			}
		});
		cfg.registerJsonValueProcessor("mfrom", new JsonValueProcessor() {

			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1,
					JsonConfig arg2) {
				if (arg1 == null) {
					return null;
				}
				String from = (String) arg1;

				String[] strs = Context.splitStr(from);
				if (strs[1] == null) {
					return strs[0];
				} else {
					return strs[1];
				}
			}
		});
		cfg.registerJsonValueProcessor("mto", new JsonValueProcessor() {

			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1,
					JsonConfig arg2) {
				if (arg1 == null) {
					return null;
				}
				String to = (String) arg1;
				String[] tos = to.split(",");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < tos.length; i++) {
					String[] strs = Context.splitStr(tos[i]);
					String str = null;
					if (strs[1] == null) {
						str = strs[0];
					} else {
						str = strs[1];
					}
					if (i == 0) {
						sb.append(str);
					} else {
						sb.append(",");
						sb.append(str);
					}
				}
				return sb.toString();
			}
		});
		cfg.registerJsonValueProcessor("subject", new JsonValueProcessor() {

			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1,
					JsonConfig arg2) {
				String subject = (String) arg1;
				if (subject == null || subject.length() == 0) {
					return "(无主题)";
				}
				return subject;
			}
		});
		return cfg;
	}

	@SuppressWarnings("unused")
	private static JsonConfig createMailAccountJsonConfig() {
		JsonConfig cfg = new JsonConfig();
		cfg.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (name.equals("user")) {
					return true;
				}
				return false;
			}
		});
		return cfg;
	}

	private static JsonConfig createMailAccountSignJsonConfig() {
		JsonConfig cfg = new JsonConfig();
		cfg.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (name.equals("account")) {
					return true;
				}
				return false;
			}
		});
		/*
		 * cfg.registerJsonValueProcessor("sign", new JsonValueProcessor() {
		 * 
		 * @Override public Object processArrayValue(Object arg0, JsonConfig
		 * arg1) { // TODO Auto-generated method stub return null; }
		 * 
		 * @Override public Object processObjectValue(String arg0, Object arg1,
		 * JsonConfig arg2) { String sign = (String) arg1; try { sign =
		 * StringUtil.htmltextencoder(sign); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } return sign; } });
		 */
		return cfg;
	}

	private static int byteToInt(byte[] b) {
		int s = 0;
		s = (b[3] & 0xFF) | ((b[2] << 8) & 0xFF00) | ((b[1] << 16) & 0xFF0000)
				| ((b[0] << 24) & 0xFF000000);
		return s;
	}

	//获取邮件大小
	@SuppressWarnings("unused")
	private static long getFileSizes(String filepath) throws Exception {// 取得文件大小
		File f = new File(filepath);
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			// f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	private static String cidURL(long id, HttpServletRequest req) {
		// req.getRequestURL();
		/*
		 * String scheme = req.getScheme(); // http String serverName =
		 * req.getServerName(); // hostname.com int serverPort =
		 * req.getServerPort();
		 */// 80
		String contextPath = req.getContextPath(); // /mywebapp
		if (contextPath != null && contextPath.length() != 0) {
			contextPath += '/';
		}
		// String servletPath = req.getServletPath(); // /servlet/MyServlet
		return contextPath
				+ "../mailService?jsonParams={method:\'showcid\'}&id=" + id
				+ "&contentid=";
	}

	//获取已打开邮件内容中的图片信息
	private static ArrayList<String> findcidfrommailbody(String mailbody) {
		ArrayList<String> lists = new ArrayList<String>();
		String str = "showcid";
		String regex = "<\\s*img[^>]+src\\s*=\\s*['\"]([^\"'>]+)['\"][^>]*>";
		// ../mailService?jsonParams=%7Bmethod:%27showcid%27%7D&amp;id=11956&amp;contentid=_Foxmail.1@AFC0BC1A-D490-4BB0-B815-DB7DDB535F1E"
		Pattern pattern = Pattern.compile(regex);
		// 通过match（）创建Matcher实例
		Matcher matcher = pattern.matcher(mailbody);
		String id = null;
		while (matcher.find())// 查找符合pattern的字符串
		{
			String ss = matcher.group();
			int pos = 0;
			if ((pos = ss.indexOf(str)) == -1) {
				continue;
			}
			int end = pos + str.length();
			// %27%7D&amp;id=11956&amp;contentid=_Foxmail.0@7FBC826C-EB98-41C0-B3C7-ACA242E574E9
			int endd = ss.indexOf('\"', end);
			if (endd != -1) {
				String s = ss.substring(end, endd);
				int st = s.indexOf("id=") + "id=".length();
				id = s.substring(st, s.indexOf("&amp;", st));
				String cid = s.split("contentid=")[1];
				lists.add(cid);
			}
		}
		if (id != null) {
			lists.add(0, id);
		}
		return lists;
	}

	//获取邮件内容中新添加的图片的信息
	private static ArrayList<String> findimgfrommailbody(String mailbody) {
		ArrayList<String> lists = new ArrayList<String>();
		String str = "img";
		String regex = "<\\s*img[^>]+src\\s*=\\s*['\"]([^\"'>]+)['\"][^>]*>";
		// ../mailService?jsonParams=%7Bmethod:%27showcid%27%7D&amp;id=11956&amp;contentid=_Foxmail.1@AFC0BC1A-D490-4BB0-B815-DB7DDB535F1E"
		Pattern pattern = Pattern.compile(regex);
		// 通过match（）创建Matcher实例
		Matcher matcher = pattern.matcher(mailbody);
		while (matcher.find())// 查找符合pattern的字符串
		{
			String ss = matcher.group();
			int pos = 0;
			if ((pos = ss.indexOf(str)) == -1) {
				continue;
			}
			int end = pos + str.length();
			int endd = ss.indexOf("src=\"", end);
			if (endd != -1) {
				String s = ss.substring(endd+5, endd+8);
				if(!s.equalsIgnoreCase("cid")&&!s.equalsIgnoreCase("htt"))
				{
					int enddd = ss.indexOf('\"', endd+5);
					String cid = ss.substring(endd+5, enddd);
					lists.add(cid);
				}
			}
		}
		return lists;
	}
	
	//处理已打开邮件内容中的图片信息
	private static String replaceallcid(String mailbody) {

		StringBuffer sb = new StringBuffer();
		String str = "showcid";
		String regex = "<\\s*img[^>]+src\\s*=\\s*['\"]([^\"'>]+)['\"][^>]*>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mailbody);
		int loc = 0;
		while (matcher.find()) {
			String ss = matcher.group();
			int pos = 0;
			if ((pos = ss.indexOf(str)) == -1) {
				continue;
			}
			sb.append(mailbody.substring(loc, matcher.start()));
			loc = matcher.end();
			pos = ss.toLowerCase().indexOf("src=\"");
			sb.append(ss.substring(0, pos));
			int ind = ss.indexOf("contentid=");
			if (ind != -1) {
				int indend = ss.indexOf('\"', ind);
				if (indend != -1) {
					String cid = ss.substring(ind + "contentid=".length(),
							indend);
					sb.append("src=\"cid:" + cid + "\">");
					if (indend + 1 > ss.length())
						sb.append(ss.substring(indend + 1));
				}
			}
		}
		if (sb.length() != 0)
			mailbody = sb.toString();
		return mailbody;
	}

	//处理邮件内容中新添加的图片的信息
	private static String replaceallimgcid(String mailbody) {

		StringBuffer sb = new StringBuffer();
		String str = "img";
		String regex = "<\\s*img[^>]+src\\s*=\\s*['\"]([^\"'>]+)['\"][^>]*>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mailbody);
		int loc = 0;
		while (matcher.find()) {
			String ss = matcher.group();
			int pos = 0;
			if ((pos = ss.indexOf(str)) == -1) {
				continue;
			}
			sb.append(mailbody.substring(loc, matcher.start()));
			loc = matcher.end();
			pos = ss.toLowerCase().indexOf("src=\"");
			sb.append(ss.substring(0, pos+5));
			String string = ss.substring(pos+5,pos+8);
			if(!string.equalsIgnoreCase("cid")&&!string.equalsIgnoreCase("htt"))
				sb.append("cid:");
			sb.append(ss.substring(pos+5));
		}
		sb.append(mailbody.substring(loc));
		if (sb.length() != 0)
			mailbody = sb.toString();
		return mailbody;
	}
	
	
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String numberChar = "0123456789";
 
	//产生混合型随机字符串
	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(letterChar.length())));
		}
		return sb.toString();
	}
	
	//密码加密
	private static  String  genaratemm(String string)
	{
		StringBuffer sb = new StringBuffer();
		char [] s = string.toCharArray();
		for(int i = 0;i != s.length;i++)
		{
			if(i%2==0)
				sb.append(s[i]+generateMixString(1));
			else
				sb.append(s[i]+generateMixString(2));
		}
		return sb.toString();
	}
	
	//密码解密
	public static String encodemm(String string)
	{
		StringBuffer sb = new StringBuffer();
		char [] s = string.toCharArray();
		int j = 0;
		for(int i = 0;i < s.length;)
		{
			if(j%2==0){
				sb.append(s[i]);
				i+=2;
			}else{
				sb.append(s[i]);
				i+=3;
			}
			j++;
		}
		return sb.toString();
	}
	
	//验证账户id
	private static Boolean checkId(Users user, long id, MailService mailservice)
	{
		MailAccount ma = mailservice.findmailbyUser(user , id);
		boolean check = false;
		if(ma != null)
			check = true;
		
		if (!check) {
			System.out.println("非法入侵！！");
		}
		return check;
	}
	
	//验证邮件id
	private static Boolean checkmailId(MailAccount ma, long id, MailService mailservice)
	{
		MailMessages mm = mailservice.findMailMessage(ma , id);
		boolean check = false;
		if(mm != null)
			check = true;
		if (!check) {
			System.out.println("非法入侵！！");
		}
		return check;
	}
}
