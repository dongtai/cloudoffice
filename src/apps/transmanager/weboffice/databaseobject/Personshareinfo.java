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

/**
 * Personshareinfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="personshareinfo")
public class Personshareinfo implements SerializableAdapter
{

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_personShareinfo_gen")
	@GenericGenerator(name = "seq_personShareinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PERSON_SHAREINFO_ID") })
	private Long personShareId;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfoBySharerUserId; // 被共享的个人
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfoByShareowner; // 共享的个人
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations groupinfoOwner; // 共享的部门
	private Long firstshareid;//原始共享人，取消共享时会将所有共享信息删除

	@Column(length = 60000)
	private String shareFile;
	private Integer permit;
	@Column(length = 100)
	private String companyId;
	private Integer isNew;
	// 共享时间
	private Date date;
	// 是否是文件夹：0是文件，1是文件夹
	private Integer isFolder;
	// 共享描述信息
	@Column(length = 1000)
	private String shareComment;
	
	// Constructors
	@Column(length = 100)
    private String approve;    
    //审阅结果
	@Column(length = 100)
    private String approveResult;    
    //审阅备注
	@Column(length = 1000)
    private String approveComment;
	@Column(length = 5000)
	private String comment;//备注
	private Date addDate;//添加备注的时间
	public Long getFirstshareid()
	{
		return firstshareid;
	}

	public void setFirstshareid(Long firstshareid)
	{
		this.firstshareid = firstshareid;
	}

    public String getApprove()
    {
        return approve;
    }

    public void setApprove(String approve)
    {
        this.approve = approve;
    }

    public String getApproveResult()
    {
        return approveResult;
    }

    public void setApproveResult(String approveResult)
    {
        this.approveResult = approveResult;
    }

    public String getApproveComment()
    {
        return approveComment;
    }

    public void setApproveComment(String approveComment)
    {
        this.approveComment = approveComment;
    }

	// Constructors

	/** default constructor */
	public Personshareinfo()
	{
	}

	/** full constructor */
	public Personshareinfo(Users userinfoBySharerUserId,
			Users userinfoByShareowner, String shareFile, Integer permit,
			String companyId, Integer isNew)
	{
		this.userinfoBySharerUserId = userinfoBySharerUserId;
		this.userinfoByShareowner = userinfoByShareowner;
		this.shareFile = shareFile;
		this.permit = permit;
		this.companyId = companyId;
		this.isNew = isNew;
	}

	// Property accessors

	public Long getPersonShareId()
	{
		return this.personShareId;
	}

	public void setPersonShareId(Long personShareId)
	{
		this.personShareId = personShareId;
	}

	public Users getUserinfoBySharerUserId()
	{
		return this.userinfoBySharerUserId;
	}

	public void setUserinfoBySharerUserId(Users userinfoBySharerUserId)
	{
		this.userinfoBySharerUserId = userinfoBySharerUserId;
	}

	public Users getUserinfoByShareowner()
	{
		return this.userinfoByShareowner;
	}

	public void setUserinfoByShareowner(Users userinfoByShareowner)
	{
		this.userinfoByShareowner = userinfoByShareowner;
	}

	public String getShareFile()
	{
		return this.shareFile;
	}

	public void setShareFile(String shareFile)
	{
		this.shareFile = shareFile;
	}

	public Integer getPermit()
	{
		return this.permit;
	}

	public void setPermit(Integer permit)
	{
		this.permit = permit;
	}

	public String getCompanyId()
	{
		return this.companyId;
	}

	public void setCompanyId(String companyId)
	{
		this.companyId = companyId;
	}

	public Integer getIsNew()
	{
		return isNew;
	}

	public void setIsNew(Integer isNew)
	{
		this.isNew = isNew;
	}

	public Organizations getGroupinfoOwner()
	{
		return groupinfoOwner;
	}

	public void setGroupinfoOwner(Organizations groupinfoOwner)
	{
		this.groupinfoOwner = groupinfoOwner;
	}

	public Integer getIsFolder()
	{
		return isFolder;
	}

	public void setIsFolder(Integer isFolder)
	{
		this.isFolder = isFolder;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getShareComment()
	{
		return shareComment;
	}

	public void setShareComment(String shareComment)
	{
		this.shareComment = shareComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
}