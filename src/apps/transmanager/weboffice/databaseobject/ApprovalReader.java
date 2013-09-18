package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 
 * @author  徐文平，孙爱华改进
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="approvalreader")
public class ApprovalReader implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalReader_gen")
	@GenericGenerator(name = "seq_approvalReader_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALREADER_ID") })
	private Long id;
	private Long approvalInfoId;         // 送审id
	private Long taskid;//签批历史编号
	private Long readUser;               // 阅读者

	private Long userId;                 // 送阅者
	private String path;                 // 文档路径     冗余
	@Column(name = "versionName", length = 1000)
	private String version;               // 文件版本   冗余
	@Column(length = 100)
	private String title;                // 标题        冗余
	@Column(length = 1000)
	private String fileName;             // 文件名       冗余
    @Column(length = 10000)
    private String webcontent;//网页内容
	private boolean isRead;              // 文档是否已经阅读过
	private Date senddate;//送阅时间
	private Integer isview=0;//是否显示，相当于删除，0显示，1不显示

	@Column(length = 10)
	private String signtag;//签收标记
	private Date signtagdate;//签收时间
	private Integer isnew=0;//是否最新，0为最新，已过的就是依次往上加
	private Long islast=0L;//如果该人被多次送阅，只算最后一次，过去的依次加1，最新的为0
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数
	
	private int state=0;//待阅为0，已阅为3
	@Column(name = "date_")
	private Date date; //阅读时间                  //
	@Column(name = "comment_",length = 1000)
	private String comment;             // 批注
	@Transient
	private String userName;            // 阅读者真实名字，为历史记录中显示用
	
	@Column(length = 50)
	private String stepName;     // 当前步骤任务的名称       冗余
	
	

	public ApprovalReader()
	{		
	}
	
	public Integer getIsview()
	{
		return isview;
	}
	public void setIsview(Integer isview)
	{
		this.isview = isview;
	}
	public String getWebcontent()
	{
		return webcontent;
	}
	public void setWebcontent(String webcontent)
	{
		this.webcontent = webcontent;
	}


	public Long getIslast()
	{
		return islast;
	}

	public void setIslast(Long islast)
	{
		this.islast = islast;
	}
	public Long getTaskid()
	{
		return taskid;
	}
	public void setTaskid(Long taskid)
	{
		this.taskid = taskid;
	}
	public Date getWarndate() {
		return warndate;
	}
	public void setWarndate(Date warndate) {
		this.warndate = warndate;
	}
	public Integer getWarnnum() {
		return warnnum;
	}
	public void setWarnnum(Integer warnnum) {
		this.warnnum = warnnum;
	}
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}

	public Integer getIsnew() {
		return isnew;
	}

	public void setIsnew(Integer isnew) {
		this.isnew = isnew;
	}


	public String getSigntag() {
		return signtag;
	}

	public void setSigntag(String signtag) {
		this.signtag = signtag;
	}

	public Date getSigntagdate() {
		return signtagdate;
	}

	public void setSigntagdate(Date signtagdate) {
		this.signtagdate = signtagdate;
	}

	public String getStepName()
	{
		return stepName;
	}
	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}

	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}


	public Long getApprovalInfoId()
	{
		return approvalInfoId;
	}


	public void setApprovalInfoId(Long approvalInfoId)
	{
		this.approvalInfoId = approvalInfoId;
	}


	public Long getUserId()
	{
		return userId;
	}


	public void setUserId(Long userId)
	{
		this.userId = userId;
	}


	public String getPath()
	{
		return path;
	}


	public void setPath(String path)
	{
		this.path = path;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getFileName()
	{
		return fileName;
	}


	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}


	public boolean isRead()
	{
		return isRead;
	}


	public void setRead(boolean isRead)
	{
		this.isRead = isRead;
	}
	public Long getReadUser()
	{
		return readUser;
	}
	public void setReadUser(Long readUser)
	{
		this.readUser = readUser;
	}


	public Date getDate()
	{
		return date;
	}


	public void setDate(Date date)
	{
		this.date = date;
	}


	public String getComment()
	{
		return comment;
	}


	public void setComment(String comment)
	{
		this.comment = comment;
	}


	public String getUserName()
	{
		return userName;
	}


	public void setUserName(String userName)
	{
		this.userName = userName;
	}


	public String getVersion()
	{
		return version;
	}


	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public int getState()
	{
		return state;
	}
	public void setState(int state)
	{
		this.state = state;
	}



}
