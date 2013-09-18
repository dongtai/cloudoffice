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
 * 文档审批附件签批表
 * <p>
 * <p>
 * EIO版本:        FGW
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-4-20
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:       CLOUD
 * <p>
 * <p>
 */
@Entity
@Table(name="approvalfileshistory")
public class ApprovalFilesHistory implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalfileshistory_gen")
    @GenericGenerator(name = "seq_approvalfileshistory_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALFILESHISTORY_ID") })
    private Long id;//主键
    
    @Column(name = "approvalid")
    private Long approvalid;//流程编号
    
    @Column(name = "approvaltaskid")
    private Long approvaltaskid;//签批编号，已包含当前状态
    
    @Column(name = "nodeid")
    private Long nodeid;//节点编号（状态），冗余，为了查询方便
    
    @Column(name = "fileid")
    private Long fileid;//文件编号
    
    @Column(name = "signid")
    private Long signid;//签批用户ID
    
    @Column(name = "adddate")
    private Date adddate=new Date();// 签批时间(插入时间)

    @Column(name = "status")
    private int status=0;// 文件状态，0 = 空闲，1 = 打开中，暂不用
    
    @Column(name = "openuser")
    private Long openuser;//当前打开用户ID，暂不用
    
    @Column(name = "comment",length = 1000)
    private String comment;// 签批附件说明，暂不用
    
    @Column(name = "documentpath",length = 3000)
    private String documentpath;//附件路径（版本号）,先将附件保存再保存版本
    
    @Column(length = 1000)
    private String fileName;//文件名称,冗余，暂没用

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

	public Long getApprovaltaskid()
	{
		return approvaltaskid;
	}

	public void setApprovaltaskid(Long approvaltaskid)
	{
		this.approvaltaskid = approvaltaskid;
	}

	public Long getNodeid()
	{
		return nodeid;
	}

	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}

	public Long getFileid()
	{
		return fileid;
	}

	public void setFileid(Long fileid)
	{
		this.fileid = fileid;
	}

	public Long getSignid()
	{
		return signid;
	}

	public void setSignid(Long signid)
	{
		this.signid = signid;
	}

	public Date getAdddate()
	{
		return adddate;
	}

	public void setAdddate(Date adddate)
	{
		this.adddate = adddate;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public Long getOpenuser()
	{
		return openuser;
	}

	public void setOpenuser(Long openuser)
	{
		this.openuser = openuser;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
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
