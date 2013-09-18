package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 协作者上传的文档表
 * <p>
 * <p>
 * EIO版本:        最新版存放多附件
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-9-20
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:       CLOUD
 * <p>
 * <p>
 */
@Entity
@Table(name="approvalcooperfiles")
public class ApprovalCooperFiles implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalcooperfiles_gen")
    @GenericGenerator(name = "seq_approvalcooperfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALCOOPERFILES_ID") })
    private Long id;//主键
    
    @Column(name = "approvalid")
    private Long approvalid;//流程编号
    
	@Column(name = "userID")
    private Long userID;//更新用户ID
    
    @Column(name = "adddate")
    private Date adddate=new Date();// 更新时间

    @Column(name = "documentpath", length = 3000)
    private String documentpath;//文件路径，版本号
    
    private String fileName;//文件名称
    @Column(name = "oldpath",length = 3000)
    private String oldpath;//原始版本号
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    
    private Long coopid;//签批号，在提交的时候才更新，保存文档时是空的。
    
    public Long getCoopid()
	{
		return coopid;
	}

	public void setCoopid(Long coopid)
	{
		this.coopid = coopid;
	}
	public Long getIsnew() {
		return isnew;
	}

	public void setIsnew(Long isnew) {
		this.isnew = isnew;
	}

	public String getOldpath()
	{
		return oldpath;
	}

	public void setOldpath(String oldpath)
	{
		this.oldpath = oldpath;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getApprovalid()
	{
		return approvalid;
	}

	public void setApprovalid(Long approvalid)
	{
		this.approvalid = approvalid;
	}

	public Long getUserID()
	{
		return userID;
	}

	public void setUserID(Long userID)
	{
		this.userID = userID;
	}

	public Date getAdddate()
	{
		return adddate;
	}

	public void setAdddate(Date adddate)
	{
		this.adddate = adddate;
	}

	public String getDocumentpath()
	{
		return documentpath;
	}

	public void setDocumentpath(String documentpath)
	{
		this.documentpath = documentpath;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

}
