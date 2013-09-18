package apps.transmanager.weboffice.service.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class Context {

	// 阅读邮件
	public static MimeMessage openmail(byte[] contents) throws Exception {
		InputStream inputstream = new ByteArrayInputStream(contents);
//		Properties p = System.getProperties();
		Properties pclone = new Properties();
		pclone.put("mail.mime.address.strict", false);
		Session session = Session.getInstance(pclone);
		MimeMessage message = new MimeMessage(session, inputstream);
		return message;
	}

	public static MimeMessage openmail(InputStream inputstream) throws Exception {
//		Properties p = System.getProperties();
		Properties pclone = new Properties();
		pclone.put("mail.mime.address.strict", false);
		Session session = Session.getInstance(pclone);
		MimeMessage message = new MimeMessage(session, inputstream);
		return message;
	}
	public static void createMessage(MimeMessage msg, String from, String[] to,
			String[] cc, String[] bcc, String subject, String content,
			Vector<String> file,Vector<BodyPart> cid) throws MessagingException,
			UnsupportedEncodingException 
	{
		createMessage(msg, from, to, cc, bcc, subject, content, file, cid,false,0);
	}
	// zy<zy@yozosoft.com>//cc zy<zy@yozosoft.com>,zy<zy@yozosoft.com>中间是斗号
	public static void createMessage(MimeMessage msg, String from, String[] to,
			String[] cc, String[] bcc, String subject, String content,
			Vector<String> file,Vector<BodyPart> cid,boolean isNotification,int important) throws MessagingException,
			UnsupportedEncodingException {

		if (from != null) {
			String[] str = splitStr(from);
			Address from_address = new InternetAddress(str[0], str[1]);
			msg.setFrom(from_address);

		}
		InternetAddress[] address = null;
		if (to != null) {
			address = new InternetAddress[to.length];
			for (int i = 0; i < address.length; i++) {
				String[] str = splitStr(to[i]);
				address[i] = new InternetAddress(str[0], str[1]);
			}
			msg.setRecipients(Message.RecipientType.TO, address);
		}
		if (cc != null) {
			address = new InternetAddress[cc.length];
			for (int i = 0; i < address.length; i++) {
				String[] str = splitStr(cc[i]);
				address[i] = new InternetAddress(str[0], str[1]);
			}
			msg.setRecipients(Message.RecipientType.CC, address);
		}

		if (bcc != null) {
			address = new InternetAddress[bcc.length];
			for (int i = 0; i < address.length; i++) {
				String[] str = splitStr(bcc[i]);
				address[i] = new InternetAddress(str[0], str[1]);
			}
			msg.setRecipients(Message.RecipientType.BCC, address);
		}

		msg.setSubject(subject, "utf-8");
		if(isNotification)
		msg.setHeader("Disposition-Notification-To",from);
		Multipart mp = new MimeMultipart();
		MimeBodyPart mbp = new MimeBodyPart();
		if(content != null)
		mbp.setContent(content.toString(), "text/html;charset=UTF-8");
		mp.addBodyPart(mbp);
		if (file!=null && !file.isEmpty()) {// 有附件
			Enumeration<String> efile = file.elements();
			while (efile.hasMoreElements()) {
				mbp = new MimeBodyPart();
				String filename = efile.nextElement(); // 选择出每一个附件名
				FileDataSource fds = new FileDataSource(filename); // 得到数据源
				mbp.setDataHandler(new DataHandler(fds)); // 得到附件本身并至入BodyPart
				mbp.setFileName(MimeUtility.encodeText(fds.getName())); // 得到文件名同样至入BodyPart
				mp.addBodyPart(mbp);
			}
			file.removeAllElements();
		}
		if(cid != null && cid.size() != 0)
		{
			for (BodyPart bodyPart : cid) {
				mp.addBodyPart(bodyPart);
			}
		}
		msg.setContent(mp); // Multipart加入到信件
		msg.setSentDate(new Date()); // 设置信件头的发送日期
		// 发送信件
		msg.saveChanges();
	}
	
	// zy<zy@yozosoft.com>
	public static String[] splitStr(String str) {
		// /String str = "zy<zy@yozosoft.com>";
		String[] strs = str.split("<.+?>");
		if(strs.length == 0)
		{
			return new String[]{str.substring(1, str.length() - 1),null};
		}
		String person = str.split("<.+?>")[0];// zy
		if(person.length() == str.length())
		{
			return new String[]{str,null};
		}
		String mail = str.substring(person.length() + 1, str.length() - 1);
		return new String[] { mail, person };
	}

}
