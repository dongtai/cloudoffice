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
 * @author  孙爱华
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="approvalreaderdelete")
public class ApprovalReaderDelete implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalReader_gen")
	@GenericGenerator(name = "seq_approvalReader_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALREADER_ID") })
	private Long id;
	private Long approvalInfoId;         // 送审id
	private Long userId;                 // 阅读者
	private String path;                 // 文档路径
	@Column(name = "versionName",length = 1000)
	private String version;               // 文件版本
	@Column(length = 100)
	private String title;                // 标题
	@Column(length = 1000)
	private String fileName;             // 文件名
	private boolean isRead;              // 文档是否已经阅读过
	private Long sendUser;               // 发送者	
	@Column(name = "date_")
	private Date date;                   //
	@Column(name = "comment_",length = 1000)
	private String comment;             // 批注
	@Transient
	private String userName;            // 阅读者真实名字，为历史记录中显示用
	@Column(length = 50)
	private String stepName;     // 当前步骤任务的名称
    
	public ApprovalReaderDelete()
	{		
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


	public Long getSendUser()
	{
		return sendUser;
	}


	public void setSendUser(Long sendUser)
	{
		this.sendUser = sendUser;
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
	
	

}
