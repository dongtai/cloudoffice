package apps.transmanager.weboffice.databaseobject.archive;


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

import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 归档记录历史表
 * 文件注释
 * <p>
 * <p>
 * @author  Administrator
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="archiveformlog")
public class ArchiveFormLog implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_archiveformlog_gen")
	@GenericGenerator(name = "seq_archiveformlog_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ARCHIVEFORMLOG_ID") })
	private Long id;
	private ArchiveForm archiveForm;
	@Column(length = 50)
	private String security;//密级
	@Column(length = 1000)
	private String archivescript;//归档说明
	private Date createdate;//归档日期
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users archiver;//归档人员
	
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ApprovalInfo approvalinfo;//对应的签批记录


	public ArchiveForm getArchiveForm()
	{
		return archiveForm;
	}


	public void setArchiveForm(ArchiveForm archiveForm)
	{
		this.archiveForm = archiveForm;
	}


	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}


	public String getSecurity()
	{
		return security;
	}


	public void setSecurity(String security)
	{
		this.security = security;
	}


	public String getArchivescript()
	{
		return archivescript;
	}


	public void setArchivescript(String archivescript)
	{
		this.archivescript = archivescript;
	}


	public Date getCreatedate()
	{
		return createdate;
	}


	public void setCreatedate(Date createdate)
	{
		this.createdate = createdate;
	}


	public Users getArchiver()
	{
		return archiver;
	}


	public void setArchiver(Users archiver)
	{
		this.archiver = archiver;
	}


	public ApprovalInfo getApprovalinfo()
	{
		return approvalinfo;
	}


	public void setApprovalinfo(ApprovalInfo approvalinfo)
	{
		this.approvalinfo = approvalinfo;
	}
	
	
	
	
}
