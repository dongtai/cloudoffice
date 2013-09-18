package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="mailaccount")
public class MailAccount implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mail_account_gen")
	@GenericGenerator(name = "seq_mail_account_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MAIL_ACCOUNT_ID") })
	private Long id;
	
	public enum ServerType {
		POP3, SMTP, IMAP
	};
	
	@Column(length = 100)
	private String personName;
	
	@Column(length = 255)
	private String email;
	
	@Column(length = 255)
	private String replyToemail;//回复邮件的地址
	
	@Column(length = 255)
	private String incomingServer;//接收服务器
	
	private int incomingport = -1;//服务器接收端口
	
	private Boolean inSSL = Boolean.FALSE;//是否使用安全链接SSL 默认为false

	private ServerType incomingServerType = ServerType.POP3;//协议
	
	@Column(length = 100)
	private String inuser;//收件箱邮箱用户名
	
	@Column(length = 100)
	private String inpassword;//收件箱邮箱密码
	
	@Column(length = 255)
	private String outgoingServer;//发送服务器
	
	private int outgoingport = -1;//发送端口
	
	private Boolean outSSL = Boolean.FALSE;//是否使用安全链接SSL 默认为false
	
	private Boolean iscode = Boolean.FALSE;//密码是否加密过
	
	private ServerType outgoingServerType = ServerType.SMTP;//协议
	
	private Boolean smtpAuth = Boolean.TRUE;//发送服务器smtp需要验证//默认是需要验证且用与pop（IMAP）服务器相同的信息
	
	private Boolean smtpAuthSameasin = Boolean.TRUE;//使用与发送服务器相同的信息,如果不同,则下面的两个字段派上用处.
	
	@Column(length = 100)
	private String outsmtpUser;//发件箱验证用的用户名
	@Column(length = 100)
	private String outsmtpPassword;//发件箱验证用的密码
	
	
//	private Boolean isLeaveMsgOnServer = Boolean.TRUE;//是否保留在服务器上，默认是true
	
	private Integer deleteafterdays = new Integer(-1);//如果不保留,几天后删除。//可能上面的不需要，如果 -1，不删除，其他删除
	
	private Boolean isdefault = Boolean.FALSE;//设置登录是默认账户
	
	private Boolean isinnerAccount = Boolean.FALSE;//是否是内置账户，不能删除
	
	private Boolean isLogin = Boolean.TRUE;//是否是可登陆的
	
	private String latestMsUid;//该用户最后接收到的邮件的uid

	
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Users user; // 该配置所属的用户
	
	public Boolean getIsLogin()
	{
		return isLogin;
	}

	public void setIsLogin(Boolean islogin)
	{
		this.isLogin=islogin;
	}
	
	public String getlatestMsUid()
	{
		return latestMsUid;
	}

	public void setlatestMsUid(String latestmsUid)
	{
		this.latestMsUid = latestmsUid;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getReplyToemail() {
		return replyToemail;
	}

	public void setReplyToemail(String replyToemail) {
		this.replyToemail = replyToemail;
	}

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

	public ServerType getIncomingServerType() {
		return incomingServerType;
	}

	public void setIncomingServerType(ServerType incomingServerType) {
		this.incomingServerType = incomingServerType;
	}

	public String getInuser() {
		return inuser;
	}

	public void setInuser(String inuser) {
		this.inuser = inuser;
	}

	public String getInpassword() {
		return inpassword;
	}

	public void setInpassword(String inpassword) {
		this.inpassword = inpassword;
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

	public ServerType getOutgoingServerType() {
		return outgoingServerType;
	}

	public void setOutgoingServerType(ServerType outgoingServerType) {
		this.outgoingServerType = outgoingServerType;
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

	public String getOutsmtpUser() {
		return outsmtpUser;
	}

	public void setOutsmtpUser(String outsmtpUser) {
		this.outsmtpUser = outsmtpUser;
	}

	public String getOutsmtpPassword() {
		return outsmtpPassword;
	}

	public void setOutsmtpPassword(String outsmtpPassword) {
		this.outsmtpPassword = outsmtpPassword;
	}

	/*public Boolean getIsLeaveMsgOnServer() {
		return isLeaveMsgOnServer;
	}

	public void setIsLeaveMsgOnServer(Boolean isLeaveMsgOnServer) {
		this.isLeaveMsgOnServer = isLeaveMsgOnServer;
	}*/

	public Integer getDeleteafterdays() {
		return deleteafterdays;
	}

	public void setDeleteafterdays(Integer deleteafterdays) {
		this.deleteafterdays = deleteafterdays;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	
	public Boolean getIsdefault() {
		return isdefault == null ? Boolean.FALSE : isdefault;
	}

	public void setIsdefault(Boolean isdefault) {
		this.isdefault = isdefault;
	}

	public Boolean getIsinnerAccount() {
		return isinnerAccount == null ? Boolean.FALSE : isdefault;
	}

	public void setIsinnerAccount(Boolean isinnerAccount) {
		this.isinnerAccount = isinnerAccount;
	}
	
	public Boolean getIscode() {
		return iscode == null ? Boolean.FALSE : iscode;
	}

	public void setIscode(Boolean iscode) {
		this.iscode = iscode;
	}

	public static ServerType convertServerType(String str)
	{
		if(str.equalsIgnoreCase("POP3"))
		{
			return ServerType.POP3;
		}
		if(str.equalsIgnoreCase("IMAP"))
		{
			return ServerType.IMAP;
		}
		if(str.equalsIgnoreCase("SMTP"))
		{
			return ServerType.SMTP;
		}
		return ServerType.POP3;
	}
	
	public static String convertServerType(ServerType incomingServerType) {
		if(incomingServerType == ServerType.POP3)
		{
			return "POP3";
		}
		if(incomingServerType == ServerType.IMAP)
		{
			return "IMAP";
		}
		if(incomingServerType == ServerType.SMTP)
		{
			return "SMTP";
		}
		return "POP3";
	}
}
