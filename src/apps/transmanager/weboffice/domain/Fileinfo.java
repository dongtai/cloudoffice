package apps.transmanager.weboffice.domain;

import java.util.Date;

import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.util.both.FlagUtility;



/**
 * Fileinfo entity. @author MyEclipse Persistence Tools
 */

public class Fileinfo  implements SerializableAdapter
{

    // Fields    

    private Long fileId;
    private String fileName;    
    private String keywords;
    private Integer status;
    private Long fileSize;
    private Date createTime;
    private Date lastedTime;
    private Date deletedTime;
    private String linkAddress;
    private String pathInfo;
    private String author;
    private String title;
    private boolean isFold;
    //private boolean isCheckout;
    //private boolean isVersion;
    private String imageTitle;
    private String imageUrl;
    private int permit = 0x00000011;
    // Constructors
    private String userLock;
    private boolean isShared;
    private String primalPath;
    private long otherShareID;
    private boolean isChild;
    private String showPath;
    private long important;
    private int isNew;//0 表明该文件经更新后还未阅读过
//    private String test;
    //共享者的真实姓名
    private String shareRealName;
    //共享描述信息
    private String shareCommet;
    //共享者的id
    private long sharerId;
    private Date shareTime;
    private boolean islog = false;
    public long getSharerId() {
		return sharerId;
	}

	public void setSharerId(long sharerId) {
		this.sharerId = sharerId;
	}

	private Long filePermit; // 文档权限
    private Long spacePermisson; // 空间权限
    private String tag = null; //标签
    
	/**
     * 文件状态用于定稿，迁入迁出
     */
    private String fileStatus;
    
    private String ischeck;
    
    private boolean hasVersion;
    
    private String isEntrypt;//是否加密
    
    private String isSign;//是否签名
    // 文件审批状态
    private int approvalStatus = -1;
    private int approvalCount;
    //check时间
    private Date checkTime;
    private int shareCount;
    private int signCount;
    private long versionCount;
    private String versionName;
    private boolean groupFile;                // 是否是项目组中的文件。
    private String shareToUser;
    private String approvestate;
    private String approve;    
    private String approveComment;//审阅备注
    
    public String getApprove()
	{
		return approve;
	}

	public void setApprove(String approve)
	{
		this.approve = approve;
	}

	public String getApproveComment()
	{
		return approveComment;
	}

	public void setApproveComment(String approveComment)
	{
		this.approveComment = approveComment;
	}

	/** default constructor */
    public Fileinfo()
    {
    }
    
    public String getShareToUser() {
        return shareToUser;
    }

    public void setShareToUser(String shareToUser) {
        this.shareToUser = shareToUser;
    }
    
    public String getApprovestate() {
        return approvestate;
    }

    public void setApprovestate(String approvestate) {
        this.approvestate = approvestate;
    }
    
    public boolean isGroupFile()
	{
		return groupFile;
	}

	public void setGroupFile(boolean isGroup)
	{
		this.groupFile = isGroup;
	}


	public String getVersionName()
	{
		return versionName;
	}


	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}


	public int getIsNew() 
    {
        return isNew;
    }

    public void setIsNew(int isNew) 
    {
        this.isNew = isNew;
    }
    
    public String getIsEntrypt() {
		return isEntrypt;
	}

	public void setIsEntrypt(String isEntrypt) {
		this.isEntrypt = isEntrypt;
	}

	public String getIsSign() {
		return isSign;
	}

	public void setIsSign(String isSign) {
		this.isSign = isSign;
	}

	public void setPrimalPath(String primalPath)
    {
        this.primalPath = primalPath;
    }

    public String getPrimalPath()
    {
        return this.primalPath;
    }

    public void setShared(boolean isShared)
    {
        this.isShared = isShared;
    }

    public boolean isShared()
    {
        return this.isShared;
    }

    public void setUserLock(String userLock)
    {
        this.userLock = userLock;
    }

    public String getUserLock()
    {
        return this.userLock;
    }
    
    public void setShowPath(String showPath)
    {
        this.showPath = showPath;
    }
    
    public String getShowPath()
    {
        return showPath;
    }

    public String getImageTitle()
    {
        return imageTitle;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageTitle(String imageTitle)
    {
        this.imageTitle = imageTitle;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    
    public void setChild(boolean isChild)
    {
        this.isChild = isChild;
    }
    
    public boolean isChild()
    {
        return isChild;
    }

    //    /** minimal constructor */
    //    public Fileinfo(Long fileId)
    //    {
    //        this.fileId = fileId;
    //    }
    //    
    //
    //    /** full constructor */
    //    public Fileinfo(Long fileId, String fileName, String keywords, Integer status,
    //        Long fileSize, Date createTime, Date lastedTime, String linkAddress, String pathInfo,
    //        String author, String title)
    //    {
    //        this.fileId = fileId;
    //        this.fileName = fileName;
    //        this.keywords = keywords;
    //        this.status = status;
    //        this.fileSize = fileSize;
    //        this.createTime = createTime;
    //        this.lastedTime = lastedTime;
    //        this.linkAddress = linkAddress;
    //        this.pathInfo = pathInfo;
    //        this.author = author;
    //        this.title = title;
    //    }

    /*public void setCheckout(boolean isCheckout)
    {
        this.isCheckout = isCheckout;
    }

    public boolean isCheckout()
    {
        return isCheckout;
    }*/

    /*public void setVersion(boolean isVersion)
    {
        this.isVersion = isVersion;
    }

    public boolean isVersion()
    {
        return isVersion;
    }*/

    public void setFold(boolean isFold)
    {
        this.isFold = isFold;
    }

    public boolean isFold()
    {
        return isFold;
    }

    // Property accessors

    public Long getFileId()
    {
        return this.fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getKeyWords()
    {
        return this.keywords;
    }

    public void setKeyWords(String keywords)
    {
        this.keywords = keywords;
    }

    public Integer getStatus()
    {
        return this.status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Long getFileSize()
    {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Date getCreateTime()
    {
        return this.createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getLastedTime()
    {
        return this.lastedTime;
    }

    public void setLastedTime(Date lastedTime)
    {
        this.lastedTime = lastedTime;
    }

    public void setDeletedTime(Date deletedTime)
    {
        this.deletedTime = deletedTime;
    }

    public Date getDeletedTime()
    {
        return this.deletedTime;
    }

    public String getLinkAddress()
    {
        return this.linkAddress;
    }

    public void setLinkAddress(String linkAddress)
    {
        this.linkAddress = linkAddress;
    }

    public String getPathInfo()
    {
        return this.pathInfo;
    }

    public void setPathInfo(String pathInfo)
    {
        this.pathInfo = pathInfo;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPermitName()
    {
    	long tempPermit = permit;
        if (FlagUtility.isValue(tempPermit, FileSystemCons.DOWNLOAD_FLAG))
        {
        	if (FlagUtility.isValue(tempPermit, FileSystemCons.WRITE_FLAG))
        	{
        		return "读写，下载";
        	}
        	return "只读，下载";
        }
        /*if (FlagUtility.isValue(tempPermit, FileSystemCons.AUDIT_FLAG))
        {
        	return "审批";
        }*/
        if (FlagUtility.isValue(tempPermit, FileSystemCons.WRITE_FLAG))
        {
        	return "读写";
        }
        if (FlagUtility.isValue(tempPermit, FileSystemCons.READ_FLAG))
        {
        	return "只读";
        }
        if (FlagUtility.isValue(tempPermit, FileSystemCons.BROWSE_FLAG))
        {
        	return "浏览";
        }
        return "";
    }
        
    public int getPermit()
    {
        return permit;
    }

    public void setPermit(int permit)
    {
        this.permit = permit;
    }

    public long getOtherShareID()
    {
        return otherShareID;
    }

    public void setOtherShareID(long otherShareID)
    {
        this.otherShareID = otherShareID;
    }
    
    
    
    public long getImportant()
    {
        return important;
    }

    public void setImportant(long important)
    {
        this.important = important;
    }

	public String getShareRealName() {
		return shareRealName;
	}

	public void setShareRealName(String shareRealName) {
		this.shareRealName = shareRealName;
	}

	public String getShareCommet() {
		return shareCommet;
	}

	public void setShareCommet(String shareCommet) {
		this.shareCommet = shareCommet;
	}
	
	public Date getShareTime(){
		return shareTime;
	}
	
	public void setShareTime(Date shareTime){
		this.shareTime = shareTime;
	}

	public boolean isIslog() {
		return islog;
	}

	public void setIslog(boolean islog) {
		this.islog = islog;
	}
	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getIscheck() {
		return ischeck;
	}

	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}

	public boolean isHasVersion() {
		return hasVersion;
	}

	public void setHasVersion(boolean hasVersion) {
		this.hasVersion = hasVersion;
	}

	public Long getFilePermit() {
		return filePermit;
	}

	public void setFilePermit(Long filePermit) {
		this.filePermit = filePermit;
	}

	public Long getSpacePermisson() {
		return spacePermisson;
	}

	public void setSpacePermisson(Long spacePermisson) {
		this.spacePermisson = spacePermisson;
	}

    public void setApprovalStatus(int approvalStatus)
    {
        this.approvalStatus = approvalStatus;
    }

    public int getApprovalStatus()
    {
        return approvalStatus;
    }
    public void setApprovalCount(int count){
        this.approvalCount = count;
    }
    public int getApprovalCount(){
        return this.approvalCount;
    }

    public void setVersionCount(long count) {
        versionCount = count;
    }
    public long getVersionCount(){
        return this.versionCount;
    }

    public void setShareCount(int count){
        this.shareCount = count;
    }
    public int getShareCount(){
        return this.shareCount;
    }
    
    public void setSignCount(int count){
        this.signCount = count;
    }
    public int getSignCount(){
        return this.signCount;
    }
    public Date getCheckTime() {
        return checkTime;
    }
    public void setCheckTime(Date date) {
        this.checkTime = date;
    }

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	} 

    
//    public void setTest(String test)
//    {
//        this.test = test;
//    }
}