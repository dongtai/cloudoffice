package apps.transmanager.weboffice.service.mail.send;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.util.server.DES;

public class MailSender {
	public static boolean sendMail(MailInfo mailInfo) {
		SmtpAuth authenticator = null;
		if (mailInfo.isValidate()) {
			authenticator = new SmtpAuth(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		Properties pro = mailInfo.getProperties();
		Session session = Session.getInstance(pro, authenticator);
		try {
			
			MimeMessage message = new MimeMessage(session);
			Address from = new InternetAddress(mailInfo.getFromAddress());
			Address to = new InternetAddress(mailInfo.getToAddress());
			message.setFrom(from);
			message.setRecipient(Message.RecipientType.TO, to);
			message.setSubject(mailInfo.getSubject(), "utf-8");
			message.setSentDate(new Date());
			BodyPart html = new MimeBodyPart();
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			Multipart mainPart = new MimeMultipart();
			mainPart.addBodyPart(html);
			message.setContent(mainPart);
			Transport.send(message);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public static void sendRegisterMail(String registerName, String registerMail,String pass, String password, String key,String url) 
	{
		try
		{
			// System.out.println(registerName + " " + registerMail);
			url = url+"/static/userservice?jsonParams=" ;
			String param="{method:\"active\",params:{domain:\"com.yozo.do\",account:\""+registerName
					+"\",password:\""+password+ "\",spaceid:\""+key+"\"}}";
			param=getencrypt(param);
			url+=param;
			String content = "您好<br/><br/>感谢您注册系统优云!<br/><br/>"
					+"您的账号："+registerName+"<br/>"
					+"您的密码："+pass+"<br/><br/>"
					+ "<b>验证您的邮箱地址注册</b><br/><br/>请点击下面的链接来确认您的注册，并在收到此邮件的<b>7天内</b>输入您的云办公密码。<br/><br/>"
					+ "<a href='" + url + "'>确认!请点击这里验证此邮件</a><br/><br/>"
					+ "如果您不能点击上述标签为“确认！”的链接，您还可以通过复制（或输入）下面的URL到地址栏中来验证您的邮件地址。" 
					+ "<a href='" + url + "'>" + url + "</a><br/><br/>"
					+ "如果您认为这是垃圾邮件，请忽略此邮件。<br/><br/><br/>"
					+ "<b>感谢使用我们的服务</b><br/>系统官网<br/><br/><a href='http://www.yozosoft.com'>http://www.yozosoft.com</a><br/><br/><br/>"
					+ "<b>您的订阅信息：</b>感谢您注册系统优云！此邮件用于确认您的信息。<br/><br/><br/>"
					+ "系统软件有限公司<br>版权所有 2013 无锡新区震泽路18号国家软件园三期飞鱼座D栋系统大厦<br> 邮编：214135<br>电话：800-828-7652";
			
			MailInfo mailInfo = new MailInfo();
			mailInfo.setMailSmtpHost(WebConfig.publicserver);//"mail.yozosoft.com"
			mailInfo.setFromAddress(WebConfig.publicdisplay);//"cloudadmin@yozosoft.com"
			mailInfo.setToAddress(registerMail);
			mailInfo.setUserName(WebConfig.publicname);//cloudadmin@yozosoft.com
			mailInfo.setPassword(WebConfig.publicpass);//"yozo2012"
			mailInfo.setValidate(true);
			mailInfo.setSubject("感谢您注册系统优云账号");
			
			mailInfo.setContent(content);
			
			sendMail(mailInfo);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送重置密码邮件
	 * @param registerName
	 *               注册账号昵称
	 * @param registerMail
	 *               注册账号的密码
	 * @param  type
	 *              访问类型  1:pC 2:手机
	 * @param url
	 *              发送后的地址
	 */
	public static void sendRepasswordMail(String account,String registerName,String registerMail,String url,String code) 
	{
		try
		{
			// System.out.println(registerName + " " + registerMail);
			url = url+"/static/userservice?jsonParams=" ;
					String param="{method:\"mailRepasswordCheck\",params:{domain:\"com.yozo.do\",account:\""+getencrypt(account)
					+"\",code:\""+code+ "\"}}";
			url+=getencrypt(param);
			String content = "尊敬的:"+registerName+"<br/><br/>您好,"
					+"   您在系统优云（www.iyocloud.com）点击了“忘记密码”按钮，故系统自动为您发送了这封邮件。您可以点击以下链接<b>修改</b>您的密码：<br/><br/>"
					+ "<br/>请点击下面的链接来修改密码，并在收到此邮件的<b>1天内</b>输入您的云办公密码。<br/><br/>"
					+ "<a href='" + url + "'>确认!请点击这里验证此邮件</a><br/><br/>"
					+ "如果您不能点击上述标签为“确认！”的链接，您还可以通过复制（或输入）下面的URL到地址栏中来验证您的邮件地址。" 
					+ "<a href='" + url + "'>" + url + "</a><br/><br/>"
					+ "如果您认为这是垃圾邮件，请忽略此邮件。<br/><br/><br/>"
					+ "<b>感谢使用我们的服务</b><br/>系统官网<br/><br/><a href='http://www.yozosoft.com'>http://www.yozosoft.com</a><br/><br/><br/>"
					+ "<b>您的订阅信息：</b>感谢您注册系统优云！此邮件用于确认您的信息。<br/><br/><br/>"
					+ "系统软件有限公司<br>版权所有 2013 无锡新区震泽路18号国家软件园三期飞鱼座D栋系统大厦<br> 邮编：214135<br>电话：800-828-7652";
			
			MailInfo mailInfo = new MailInfo();
			mailInfo.setMailSmtpHost(WebConfig.publicserver);//"mail.yozosoft.com"
			mailInfo.setFromAddress(WebConfig.publicdisplay);//"cloudadmin@yozosoft.com"
			mailInfo.setToAddress(registerMail);
			mailInfo.setUserName(WebConfig.publicname);//cloudadmin@yozosoft.com
			mailInfo.setPassword(WebConfig.publicpass);//"yozo2012"
			mailInfo.setValidate(true);
			mailInfo.setSubject("忘记密码提示(iyocloud.com)");
			
			mailInfo.setContent(content);
			
			sendMail(mailInfo);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean sendRegisterCompanyMail(Company company, Users user, String url, boolean reg) 
	{
		// System.out.println(registerName + " " + registerMail);		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(company.getUd()));
		StringBuffer sb = new StringBuffer();
		sb.append(user.getRealName());
		sb.append("，您好！<br/><br/> 感谢您使用系统优云系统，我们会以最优质的产品和服务来回报您的关注。<br/><br/> 以下是您的账号相关信息，请查收： <br/>");
		if (reg)
		{
			sb.append("您的企业验证码为：");
			sb.append(company.getVerifyCode());
			sb.append("<br/><br/>");
		}
		sb.append("您的公司名称是：");
		sb.append(company.getName());
		sb.append("<br/>您的公司编码是：");
		sb.append(company.getCode());
		sb.append("<br/>您的公司管理员账号：");
		sb.append(user.getUserName());
		sb.append("<br/>您的公司管理员密码：");
		sb.append(user.getResetPass());
		sb.append("(请尽快修改您的密码）<br/><br/>");
		if (reg)
		{
			// ra参数和w仅仅是一个混淆的参数，没有实际意义。
			url = url + "/cloud/activeAccount.htm?";
			String param="{method:\"activeCompany\",params:{domain:\"com.yozo.do\",ra:" + (long)(Math.random() * 10000000L)
					+ ",account:\"" + user.getUserName()
					+ "\",company:\"" + company.getCode() + "\",verify:\"" + company.getVerifyCode() + "\"},w:" + System.currentTimeMillis() + "}" ;
			
			param=getencrypt(param);
			url+=param;
			sb.append("<br>请在验证页面输入企业验证码进行验证！</b><br/><br/>或在30天有效期内点击下面的链接来进行验证。<br/><br/>");
			sb.append("<a href='");			
			sb.append(url);
			sb.append("'>确认，请点击这里验证此邮件</a><br/><br/>如果您不能点击上述标签研究，您还可以通过复制（或输入）下面的URL到浏览器的地址栏中来验证您的邮件地址。"); 
			sb.append("<a href='");
			sb.append(url);
			sb.append("'>");
			sb.append(url);
			sb.append("</a><br/><br/>");
		}
		else
		{
			sb.append("<b>系统优云系统的试用周期为60天，您的账号到期日");
			sb.append(date);
			sb.append("。");
		}
		sb.append("如果您在使用过程中遇到任何问题，可以邮件iyocloud@yozosoft.com 或者拨打系统服务热线 800-828-7652 与我们联系，我们会尽快为您解决。<br/><br/>");
		sb.append("（本邮件由系统自动发出，请勿回复。）<br/><br/>");
		sb.append("</b><br/>系统官网<br/><br/><a href='http://www.yozosoft.com'>http://www.yozosoft.com</a><br/><br/><br/>");
		sb.append("系统软件有限公司<br>版权所有 2013 无锡新区震泽路18号国家软件园三期飞鱼座D栋系统大厦<br> 邮编：214135<br>电话：800-828-7652");
			
		MailInfo mailInfo = new MailInfo();
		mailInfo.setMailSmtpHost(WebConfig.publicserver);//"mail.yozosoft.com"
		mailInfo.setFromAddress(WebConfig.publicdisplay);//"cloudadmin@yozosoft.com"
		mailInfo.setToAddress(user.getRealEmail());
		mailInfo.setUserName(WebConfig.publicname);//cloudadmin@yozosoft.com
		mailInfo.setPassword(WebConfig.publicpass);//"yozo2012"
		mailInfo.setValidate(true);
		mailInfo.setSubject("系统优云系统试用");
			
		mailInfo.setContent(sb.toString());
		
		return sendMail(mailInfo);
		
	}
	
	public static String getencrypt(String value){
		try {
			return new DES().encrypt(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}