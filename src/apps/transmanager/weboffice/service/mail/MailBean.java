package apps.transmanager.weboffice.service.mail;

import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

import apps.transmanager.weboffice.databaseobject.MailAccount;
import apps.transmanager.weboffice.databaseobject.MailMessages;
import apps.transmanager.weboffice.domain.SerializableAdapter;
import apps.transmanager.weboffice.service.handler.MailHandler;
import apps.transmanager.weboffice.service.mail.send.SmtpAuth;
import apps.transmanager.weboffice.service.server.MailService;

import com.sun.mail.pop3.POP3Folder;

/**
 * 今后需要缓存
 * 
 * @author zhouyun
 * 
 */
public class MailBean implements SerializableAdapter{
	private boolean iscache = true;//手机端失效..不在session中缓存的.
	
	private boolean isRunning;

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private Session receivesession;

	private Session sendsession;

	private Store store;

	private Folder folder;

	private Transport trans;

	private MailAccount acnt;

	public MailBean(MailAccount acnt) {
		this.acnt = acnt;
	}

	public synchronized void connectReceiveStore() throws Exception {
		connect(Folder.READ_WRITE);
	}

	public synchronized void connect(int type) throws Exception {
		testconnect();
	}

	private synchronized void testconnect() throws Exception {
		if (store == null || !store.isConnected()) {
			String host = acnt.getIncomingServer(); // 【pop.mail.yahoo.com.cn】
			String username = acnt.getInuser(); // 【wwp_1124】
			String password = acnt.getInpassword(); // 【........】
			if(acnt.getIscode())
				password = MailHandler.encodemm(password);

			Properties props = new Properties();// System.getProperties();
			if (acnt.getInSSL()) {
				props.setProperty("mail.pop3.ssl.enable", "true");
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.pop3.socketFactory.fallback", "false");
				props.setProperty("mail.mime.address.strict","false");
			}

			receivesession = Session.getInstance(props, null);
			// session.setDebug(true);
			store = receivesession.getStore("pop3");
			store.connect(host, acnt.getIncomingport(), username, password);
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
		} else {
			
			if(folder != null && !folder.isOpen())
			{
				folder = null;
			}
			if (folder == null) {
				folder = store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);
			}
		}
	}

	public synchronized void closed(boolean flag) throws Exception {
		/*if (iscache)
			return;*/
		if (folder.isOpen())
			folder.close(flag);
		folder = null;
		if (iscache)
			return;
		if (store.isConnected())
			store.close();
	}

	public synchronized void closeconnect() throws Exception {
		/*if (iscache)
			return;*/
		if (folder != null && folder.isOpen())
			folder.close(false);
		folder = null;
		if (iscache)
			return;
		if (store.isConnected())
			store.close();
	}

	public synchronized ArrayList<String> getMessagesId(MailAccount ma , MailService mails, String uid) throws Exception {
		testconnect();
		ArrayList<String> uidlist = new ArrayList<String>();
		ArrayList<String> temlist = new ArrayList<String>();
		Folder apiFolder = folder;
		FetchProfile fp = new FetchProfile();
		fp.add(UIDFolder.FetchProfileItem.UID);
		Message[] mss = apiFolder.getMessages();
		if(uid!=null)
		{
			for (int i = mss.length - 1; i >= 0; i--) {//从最新的开始检测
				javax.mail.Message curMessage = mss[i];
				MimeMessage pm = (MimeMessage)curMessage;
				String id = getUID(curMessage);
				if (id == null) {
					id = pm.getMessageID();
				}
				MailMessages mmg = mails.findMailMessageByUId(ma, id);
				if (mmg == null) {
					uidlist.add(id);
				} else {
					break;
				}
			}
			for (int i = 0 , length = mss.length; i < length; i++) {//从最早的开始检测
				javax.mail.Message curMessage = mss[i];
				MimeMessage pm = (MimeMessage)curMessage;
				String id = getUID(curMessage);
				if (id == null) {
					id = pm.getMessageID();
				}
				MailMessages mmg = mails.findMailMessageByUId(ma, id);
				if (mmg == null) {
					temlist.add(id);
				} else {
					break;
				}
			}
			for (int i = temlist.size()-1; i>=0; i--)
			{
				uidlist.add(temlist.get(i));
			}
		}else {//如果数据库中没有邮件，说明是新建的账户，全部接收，不检测
			for (int i = mss.length - 1; i >= 0; i--) {
				javax.mail.Message curMessage = mss[i];
				MimeMessage pm = (MimeMessage)curMessage;
				String id = getUID(curMessage);
				if (id == null) {
					id = pm.getMessageID();
				}
				uidlist.add(id);
			}
		}
		return uidlist;
	}

	public synchronized Message getMessage(final String uid) throws Exception {
		if (uid == null || uid.length() == 0) {
			return null;
		}
		testconnect();
		final Folder apiFolder = folder;
		// FetchProfile fp = new FetchProfile();
		// fp.add(UIDFolder.FetchProfileItem.UID);
		// FetchProfile profile = new FetchProfile();
		// ************获取邮件的UID**************************
		// profile.add(UIDFolder.FetchProfileItem.UID);
		// 通过邮件的UID搜索邮件
		Message message = folder.search(new SearchTerm() {
			@Override
			public boolean match(Message arg0) {
				try {
					String mid = ((POP3Folder) apiFolder).getUID(arg0);
					if (mid == null) {
						mid = ((MimeMessage) arg0).getMessageID();
					}
					if (mid!=null && mid.equals(uid))
						return true;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return false;
			}
		})[0];
		return message;
	}

	public synchronized void delMessage(final String uid) throws Exception {
		if (uid == null || uid.length() == 0) {
			return;
		}
		testconnect();
		final Folder apiFolder = folder;
		/*
		 * FetchProfile fp = new FetchProfile();
		 * fp.add(UIDFolder.FetchProfileItem.UID); FetchProfile profile = new
		 * FetchProfile(); //************获取邮件的UID**************************
		 * profile.add(UIDFolder.FetchProfileItem.UID);
		 */
		// 通过邮件的UID搜索邮件
		Message[] messages = folder.search(new SearchTerm() {
			@Override
			public boolean match(Message arg0) {
				try {
					String mid = ((POP3Folder) apiFolder).getUID(arg0);
					if (mid == null) {
						mid = ((MimeMessage) arg0).getMessageID();
					}
					if (mid.equals(uid))
						return true;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return false;
			}
		});
		if (messages != null && messages.length > 0 && messages[0] != null)
			messages[0].setFlag(javax.mail.Flags.Flag.DELETED, true);
	}

	public synchronized String getUID(Message arg0) throws Exception {
		testconnect();
		String mid = ((POP3Folder) folder).getUID(arg0);
		return mid;
	}

	public synchronized void connectSendSession() throws Exception {
		testConnectsendSession();
	}

	private synchronized void testConnectsendSession() {
		if (sendsession != null) {
			//System.out.println("sendsession not null===============");
			return;
		}
		//System.out.println("sendsession is null===============");
		Properties props = new Properties();
		String smtpServer = acnt.getOutgoingServer();
		String username = null;
		String password = null;
		if (acnt.getSmtpAuthSameasin()) {
			username = acnt.getInuser();
			password = acnt.getInpassword();
		} else {
			username = acnt.getOutsmtpUser();
			password = acnt.getOutsmtpPassword();
		}
		props.put("mail.smtp.host", smtpServer);
		if (acnt.getOutSSL()) {
			props.setProperty("mail.smtp.ssl.enable", "true");
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.mime.address.strict","false");
		}
		if (acnt.getSmtpAuth()) { // 服务器需要身份认证
			props.put("mail.smtp.auth", "true");
			SmtpAuth smtpAuth = new SmtpAuth(username, password);
			sendsession = Session.getInstance(props, smtpAuth);
		} else {
			props.put("mail.smtp.auth", "false");
			sendsession = Session.getInstance(props, null);
		}
	}

	public synchronized void connectSendTrans() throws Exception {
		testconnectSendTrans();
	}

	private synchronized void testconnectSendTrans() throws Exception// 服务器连接失败
	{
		testConnectsendSession();
		if (trans == null || !trans.isConnected()) {
			String smtpServer = acnt.getOutgoingServer();
			String username = null;
			String password = null;
			if (acnt.getSmtpAuthSameasin()) {
				username = acnt.getInuser();
				password = MailHandler.encodemm(acnt.getInpassword());
			} else {
				username = acnt.getOutsmtpUser();
				password = acnt.getOutsmtpPassword();
				if(acnt.getIscode())
					password = MailHandler.encodemm(password);
			}

			trans = sendsession.getTransport("smtp");
			trans.connect(smtpServer, acnt.getOutgoingport(), username,
					password);
		}
	}

	public synchronized Message createMessage() throws Exception {
		testconnectSendTrans();
		Message msg = new MimeMessage(sendsession);
		return msg;
	}

	public synchronized void send(Message msg) throws Exception// 发送不成功
	{
		testconnectSendTrans();
		trans.sendMessage(msg, msg.getAllRecipients());
	}

	public synchronized void closesend() throws Exception {
		if (iscache)
			return;
		trans.close();
	}

	public synchronized void reset(MailAccount acnt) {
		try {
			if (folder != null && folder.isOpen()) {
				folder.close(true);
			}
			if (store != null && store.isConnected()) {
				store.close();
			}
			if (trans != null && trans.isConnected()) {
				trans.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		receivesession = null;
		sendsession = null;
		store = null;
		folder = null;
		trans = null;
		if (acnt != null)
			this.acnt = acnt;
	}
	
	public void setIscache(boolean iscache) {
		this.iscache = iscache;
	}
}
