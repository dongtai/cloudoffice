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

/**
 * Groupmembershareinfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="groupmembershareinfo")
public class Groupmembershareinfo implements SerializableAdapter
{

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groupmembershareinfo_gen")
	@GenericGenerator(name = "seq_groupmembershareinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUP_MEMBER_SAHERINFO_ID") })
	private Long groupmembershareId;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations groupinfo;
	@Column(length = 3000)
	private String shareFile;
	private long shareowner;
	private Integer isNew;
	@Column(length = 100)
	private String companyID;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private UsersOrganizations groupmemberinfo;

	// Constructors

	/** default constructor */
	public Groupmembershareinfo()
	{
	}

	/** full constructor */
	public Groupmembershareinfo(Long groupmembershareId, Organizations groupinfo,
			String shareFile, long shareowner, Users userinfo,
			Integer isNew, String companyID, UsersOrganizations groupmemberinfo)
	{
		this.groupmembershareId = groupmembershareId;
		this.groupinfo = groupinfo;
		this.shareFile = shareFile;
		this.shareowner = shareowner;
		this.userinfo = userinfo;
		this.isNew = isNew;
		this.companyID = companyID;
		this.groupmemberinfo = groupmemberinfo;
	}

	public Long getGroupmembershareId()
	{
		return groupmembershareId;
	}

	public void setGroupmembershareId(Long groupmembershareId)
	{
		this.groupmembershareId = groupmembershareId;
	}

	public String getShareFile()
	{
		return shareFile;
	}

	public void setShareFile(String shareFile)
	{
		this.shareFile = shareFile;
	}

	public long getShareowner()
	{
		return shareowner;
	}

	public void setShareowner(long shareowner)
	{
		this.shareowner = shareowner;
	}

	public Users getUserinfo()
	{
		return userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public Integer getIsNew()
	{
		return isNew;
	}

	public void setIsNew(Integer isNew)
	{
		this.isNew = isNew;
	}

	public Organizations getGroupinfo()
	{
		return groupinfo;
	}

	public void setGroupinfo(Organizations groupinfo)
	{
		this.groupinfo = groupinfo;
	}

	public String getCompanyID()
	{
		return companyID;
	}

	public void setCompanyID(String companyID)
	{
		this.companyID = companyID;
	}

	public UsersOrganizations getGroupmemberinfo()
	{
		return groupmemberinfo;
	}

	public void setGroupmemberinfo(UsersOrganizations groupmemberinfo)
	{
		this.groupmemberinfo = groupmemberinfo;
	}

}