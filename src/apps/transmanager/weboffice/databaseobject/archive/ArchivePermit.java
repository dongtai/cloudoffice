package apps.transmanager.weboffice.databaseobject.archive;

import java.util.Date;

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
 * 归档权限分配表
 * 文件注释
 * <p>
 * <p>
 * @author  Administrator
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="archivepermit")
public class ArchivePermit implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_archiveform_gen")
	@GenericGenerator(name = "seq_archiveform_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ARCHIVEFORM_ID") })
	private Long id;
	private Integer isReadOnly = 1;		//是否具有查阅权限，默认有
	private Integer isDownload = 0;		//是否具有下载权限，默认没有
	private Integer isReplace = 0;		//是否具有替换权限，默认没有
	private Integer isDelete = 0;		//是否具有删除权限，默认没有
	private Date startDate=new Date();	//开始日期
	private Date endDate;				//截止日期
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;					//分配给谁
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ApprovalInfo approvalinfo;	//对应的签批记录
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getIsReadOnly() {
		return isReadOnly;
	}
	public void setIsReadOnly(Integer isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	public Integer getIsDownload() {
		return isDownload;
	}
	public void setIsDownload(Integer isDownload) {
		this.isDownload = isDownload;
	}
	public Integer getIsReplace() {
		return isReplace;
	}
	public void setIsReplace(Integer isReplace) {
		this.isReplace = isReplace;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public ApprovalInfo getApprovalinfo() {
		return approvalinfo;
	}
	public void setApprovalinfo(ApprovalInfo approvalinfo) {
		this.approvalinfo = approvalinfo;
	}
}
