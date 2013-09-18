package apps.transmanager.weboffice.service.config;

import java.io.File;

/**
 * 内置账户的配置//defaultMailServer.properties
 * @author zhouyun
 *
 */
public class DefaultMailServerConfig {

	public static final String NAME = "defaultMailServerConfig";
	
	private String incomingServer;//接收服务器
	
	private int incomingport = -1;//服务器接收端口
	
	private Boolean inSSL = Boolean.FALSE;//是否使用安全链接SSL 默认为false

	private String incomingServerType = "POP3";//协议
	
	private String outgoingServer;//发送服务器
	
	private int outgoingport = -1;//发送端口
	
	private Boolean outSSL = Boolean.FALSE;//是否使用安全链接SSL 默认为false
	
	private Boolean smtpAuth = Boolean.TRUE;//发送服务器smtp需要验证//默认是需要验证且用与pop（IMAP）服务器相同的信息
	
	private Boolean smtpAuthSameasin = Boolean.TRUE;//使用与发送服务器相同的信息,如果不同,则下面的两个字段派上用处.
	
	private int deleteafterdays=-1;//如果不保留,几天后删除。//可能上面的不需要，如果 -1，不删除，其他删除

	private Boolean storageenabled = false;
	
	private int storagetype = 0;//0存文件夹，1存mysql
	
	private int msgsizelimit;
	
	private int onereceivemaxnumber;
	
	private String storagelocation;
	
	private boolean isbackgroudreceive = true;
	
	private String mailtag;//邮件内容加一个标记
	
	public static final int FOLDER = 0;
	public static final int SQL = 1;
	
	public String getIncomingServer() {
		return incomingServer;
	}

	public void setIncomingServer(String incomingServer) {
		this.incomingServer = incomingServer;
	}

	public int getIncomingport() {
		return incomingport;
	}

	public void setIncomingport(int incomingport) {
		this.incomingport = incomingport;
	}

	public Boolean getInSSL() {
		return inSSL;
	}

	public void setInSSL(Boolean inSSL) {
		this.inSSL = inSSL;
	}

	public String getIncomingServerType() {
		return incomingServerType;
	}

	public void setIncomingServerType(String incomingServerType) {
		this.incomingServerType = incomingServerType;
	}

	public String getOutgoingServer() {
		return outgoingServer;
	}

	public void setOutgoingServer(String outgoingServer) {
		this.outgoingServer = outgoingServer;
	}

	public int getOutgoingport() {
		return outgoingport;
	}

	public void setOutgoingport(int outgoingport) {
		this.outgoingport = outgoingport;
	}

	public Boolean getOutSSL() {
		return outSSL;
	}

	public void setOutSSL(Boolean outSSL) {
		this.outSSL = outSSL;
	}

	public Boolean getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(Boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public Boolean getSmtpAuthSameasin() {
		return smtpAuthSameasin;
	}

	public void setSmtpAuthSameasin(Boolean smtpAuthSameasin) {
		this.smtpAuthSameasin = smtpAuthSameasin;
	}

	public int getDeleteafterdays() {
		return deleteafterdays;
	}

	public void setDeleteafterdays(int deleteafterdays) {
		this.deleteafterdays = deleteafterdays;
	}

	public Boolean getStorageenabled() {
		return storageenabled;
	}

	public void setStorageenabled(Boolean storageenabled) {
		this.storageenabled = storageenabled;
	}

	public int getStoragetype() {
		return storagetype;
	}

	public void setStoragetype(int storagetype) {
		this.storagetype = storagetype;
	}

	public int getMsgsizelimit() {
		return msgsizelimit;
	}

	public void setMsgsizelimit(int msgsizelimit) {
		this.msgsizelimit = msgsizelimit;
	}

	public String getStoragelocation() {
		return WebConfig.webContextPath+ File.separatorChar + storagelocation;
	}

	public void setStoragelocation(String storagelocation) {
		this.storagelocation = storagelocation;
	}

	public boolean isIsbackgroudreceive() {
		return isbackgroudreceive;
	}

	public void setIsbackgroudreceive(boolean isbackgroudreceive) {
		this.isbackgroudreceive = isbackgroudreceive;
	}

	public String getMailtag() {
		return mailtag;
	}

	public void setMailtag(String mailtag) {
		this.mailtag = mailtag;
	}

	public int getOnereceivemaxnumber() {
		return onereceivemaxnumber;
	}

	public void setOnereceivemaxnumber(int onereceivemaxnumber) {
		this.onereceivemaxnumber = onereceivemaxnumber;
	}
	
	
}
