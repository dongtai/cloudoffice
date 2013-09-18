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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="files")
public class Files implements SerializableAdapter
{
	// Fields
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_files_gen")
	@GenericGenerator(name = "seq_files_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FILES_ID") })
	private Long fileId;
	@Column(name = "fileName",length = 1000)
	private String fileName;
	@Column(name = "description",length = 1000)
	private String description;
	@Column(name = "filetype", length = 100)
	private String filetype;
	@Column(name = "keywords", length = 255)
	private String keywords;
	@Column(name = "status")
	private Integer status;
	@Column(name = "fileSize")
	private Long fileSize;
	@Column(name = "createTime")
	private Date createTime;
	@Column(name = "lastedTime")
	private Date lastedTime;
	@Column(name = "deletedTime")
	private Date deletedTime;
	@Column(name = "linkAddress",length = 1000)
	private String linkAddress;
	@Column(name = "pathInfo",length = 1000)
	private String pathInfo;
	@Column(name = "uid")
	private long uid;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	@Column(name = "title",length = 100)
	private String title;
	@Column(name = "isFold")
	private boolean isFold;// 0表示文件，1表示文件夹
	@Column(name = "imageTitle",length = 100)
	private String imageTitle;
	@Column(name = "imageUrl",length = 1000)
	private String imageUrl;
	@Column(name = "permit")
	private int permit = 0x00000011;
	@Column(name = "userLock",length = 100)
	private String userLock;
	@Column(name = "isShared")
	private boolean isShared;// 0表示不共享，1表示共享
	@Column(name = "primalPath",length = 1000)
	private String primalPath;
	@Column(name = "isChild")
	private boolean isChild;// 0表示非孩子，1表示孩子
	@Column(name = "showPath", length = 1000)
	private String showPath;
	@Column(name = "important")
	private long important;
	@Column(name = "isNew")
	private int isNew;// 0 表明该文件经更新后还未阅读过
	@Column(length = 1000)
	private String sharecomment;
	@Column(length = 100)
	private String author;
	@Column(length = 1000)
	private String copyurl;//复制地址

	public String getCopyurl()
	{
		return copyurl;
	}

	public void setCopyurl(String copyurl)
	{
		this.copyurl = copyurl;
	}

	public Long getFileId()
	{
		return fileId;
	}

	public void setFileId(Long fileId)
	{
		this.fileId = fileId;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getFiletype()
	{
		return filetype;
	}

	public void setFiletype(String filetype)
	{
		this.filetype = filetype;
	}

	public String getKeywords()
	{
		return keywords;
	}

	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(Long fileSize)
	{
		this.fileSize = fileSize;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date getLastedTime()
	{
		return lastedTime;
	}

	public void setLastedTime(Date lastedTime)
	{
		this.lastedTime = lastedTime;
	}

	public Date getDeletedTime()
	{
		return deletedTime;
	}

	public void setDeletedTime(Date deletedTime)
	{
		this.deletedTime = deletedTime;
	}

	public String getLinkAddress()
	{
		return linkAddress;
	}

	public void setLinkAddress(String linkAddress)
	{
		this.linkAddress = linkAddress;
	}

	public String getPathInfo()
	{
		return pathInfo;
	}

	public void setPathInfo(String pathInfo)
	{
		this.pathInfo = pathInfo;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getImageTitle()
	{
		return imageTitle;
	}

	public void setImageTitle(String imageTitle)
	{
		this.imageTitle = imageTitle;
	}

	public boolean getIsFold()
	{
		return isFold;
	}

	public void setIsFold(boolean isFold)
	{
		this.isFold = isFold;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public int getPermit()
	{
		return permit;
	}

	public void setPermit(int permit)
	{
		this.permit = permit;
	}

	public String getUserLock()
	{
		return userLock;
	}

	public void setUserLock(String userLock)
	{
		this.userLock = userLock;
	}

	public boolean getIsShared()
	{
		return isShared;
	}

	public void setIsShared(boolean isShared)
	{
		this.isShared = isShared;
	}

	public String getPrimalPath()
	{
		return primalPath;
	}

	public void setPrimalPath(String primalPath)
	{
		this.primalPath = primalPath;
	}

	public boolean getIsChild()
	{
		return isChild;
	}

	public void setIsChild(boolean isChild)
	{
		this.isChild = isChild;
	}

	public String getShowPath()
	{
		return showPath;
	}

	public void setShowPath(String showPath)
	{
		this.showPath = showPath;
	}

	public long getImportant()
	{
		return important;
	}

	public void setImportant(long important)
	{
		this.important = important;
	}

	public int getIsNew()
	{
		return isNew;
	}

	public void setIsNew(int isNew)
	{
		this.isNew = isNew;
	}

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public Users getUserinfo()
	{
		return userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public String getSharecomment()
	{
		return sharecomment;
	}

	public void setSharecomment(String sharecomment)
	{
		this.sharecomment = sharecomment;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

}
