package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name = "mailmessages")
// 邮件
public class MailMessages  implements SerializableAdapter
{
	
	/**
	 * Low Importance
	 */
	public final static String IMP_LOW = "Low";
	
	/**
	 * Normal Importance
	 */
	public final static String IMP_NORMAL = "Normal";
	
	/**
	 * High Importance
	 */
	public final static String IMP_HIGH = "High";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mail_messages_gen")
	@GenericGenerator(name = "seq_mail_messages_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MAIL_MESSAGES_ID") })
	private Long id;

	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MailFolders folder;// 所处文件夹

	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MailSources source;// 邮件的内容

	@Column(length = 60000)
	private String sourcepath;//邮件的内容保留字段，今后可能会存储在文件库内
	
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Index(name = "IDX_ACC")
	private MailAccount account;// 某账户下的邮件

	@Column(length = 100)
	private String mfrom;// 发信人

	@Column(length = 1024)
	private String mto;// 收信人

	@Column(length = 1024)
	private String cc;// 抄送人

	@Column(length = 1024)
	private String bcc;// 密送人

	@Column(length = 255)
	private String subject;// 主题

	private Date sentDate;// 发送时间

	private Date receivedDate;// 接收时间

	private Boolean isimportant = Boolean.FALSE;// 是否紧急发送

	private Boolean isreceivenotify = Boolean.FALSE;// 是否发送已读回执

	private Integer msgSize = 0;// 邮件大小，单位byte

	private Boolean isseen=Boolean.FALSE;// 是否已读//默认都是未读

	public enum IMPFlag {
		IMP_NORMAL, IMP_LOW, IMP_HIGH
	};

	private IMPFlag impflag = IMPFlag.IMP_NORMAL;// 邮件重要性标记

	private Boolean hasatt = Boolean.FALSE;// 是否有附件

	public enum MAILFlag {// 一般邮件，重要邮件，公司邮件，业务邮件，资讯邮件，亲友邮件，同学邮件，休闲邮件，趣味邮件
		NORMAL_MAIL, IMP_MAIL, COMPANY_MAIL, BUSINESS_MAIL, INFORMATION_MAIL, RELATIVES_EMAIL, CLASSMATES_MAIL, LEISURE_MAIL, INTERESTING_MAIL
	};

	private MAILFlag mailflag = MAILFlag.NORMAL_MAIL;// 邮件类别分类//暂时不去实现

	@Column(length = 255)
	private String UID = "";//比如UID1801-1290559602,唯一的id,和邮件服务器对应起来
	
	private Boolean isremoved = Boolean.FALSE;
	
	private Boolean isNotification = Boolean.FALSE;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MailFolders getFolder() {
		return folder;
	}

	public void setFolder(MailFolders folder) {
		this.folder = folder;
	}

	public MailSources getSource() {
		return source;
	}

	public void setSource(MailSources source) {
		if(source.getId() != null)
		this.source = source;
	}

	public String getSourcepath() {
		return sourcepath;
	}

	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	public MailAccount getAccount() {
		return account;
	}

	public void setAccount(MailAccount account) {
		this.account = account;
	}

	public String getMfrom() {
		return mfrom;
	}

	public void setMfrom(String from) {
		this.mfrom = from;
	}

	public String getMto() {
		return mto;
	}

	public void setMto(String to) {
		this.mto = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Boolean getIsimportant() {
		return isimportant;
	}

	public void setIsimportant(Boolean isImportant) {
		this.isimportant = isImportant;
	}

	public Boolean getIsreceivenotify() {
		return isreceivenotify;
	}

	public void setIsreceivenotify(Boolean isreceivenotify) {
		this.isreceivenotify = isreceivenotify;
	}

	public int getMsgSize() {
		return msgSize;
	}

	public void setMsgSize(Integer msgSize) {
		this.msgSize = msgSize;
	}

	public Boolean getIsseen() {
		return isseen;
	}

	public void setIsseen(Boolean isSeen) {
		this.isseen = isSeen;
	}

	public IMPFlag getImpflag() {
		return impflag;
	}

	public void setImpflag(IMPFlag impflag) {
		this.impflag = impflag;
	}

	public Boolean getHasatt() {
		return hasatt;
	}

	public void setHasatt(Boolean hasAtt) {
		this.hasatt = hasAtt;
	}

	public MAILFlag getMailflag() {
		return mailflag;
	}

	public void setMailflag(MAILFlag mailflag) {
		this.mailflag = mailflag;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public Boolean getIsremoved() {
		if(isremoved == null)
		{
			return Boolean.FALSE;
		}
		return isremoved;
	}

	public void setIsremoved(Boolean isremoved) {
		this.isremoved = isremoved;
	}

	public Boolean getIsNotification() {
		return isNotification;
	}

	public void setIsNotification(Boolean isNotification) {
		this.isNotification = isNotification;
	}

	
}
