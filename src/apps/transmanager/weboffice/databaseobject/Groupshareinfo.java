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
 * Groupshareinfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="groupshareinfo")
public class Groupshareinfo implements SerializableAdapter
{

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groupshareinfo_gen")
	@GenericGenerator(name = "seq_groupshareinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUP_SAHERINFO_ID") })
	private Long fileShareId;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo; // 共享的个人，如果为null表示是共享给部门
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations groupinfo; // 被共享的部门
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations groupinfoOwner; // 共享的部门，如果为null表示是共享给个人
	@Column(length = 3000)
	private String shareFile;
	private Integer permit;
	@Column(length = 100)
	private String companyId;

	// Constructors

	/** default constructor */
	public Groupshareinfo()
	{
	}

	/** full constructor */
	public Groupshareinfo(Users userinfo, Organizations groupinfo,
			String shareFile, Integer permit, String companyId)
	{
		this.userinfo = userinfo;
		this.groupinfo = groupinfo;
		this.shareFile = shareFile;
		this.permit = permit;
		this.companyId = companyId;
	}

	// Property accessors

	public Long getFileShareId()
	{
		return this.fileShareId;
	}

	public void setFileShareId(Long fileShareId)
	{
		this.fileShareId = fileShareId;
	}

	public Users getUserinfo()
	{
		return this.userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public Organizations getGroupinfo()
	{
		return this.groupinfo;
	}

	public void setGroupinfo(Organizations groupinfo)
	{
		this.groupinfo = groupinfo;
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

	public Organizations getGroupinfoOwner()
	{
		return groupinfoOwner;
	}

	public void setGroupinfoOwner(Organizations groupinfoOwner)
	{
		this.groupinfoOwner = groupinfoOwner;
	}

}