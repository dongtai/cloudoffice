package apps.transmanager.weboffice.databaseobject.flow;

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

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;
/**
 * 此表是为流程表单人员列表，以后与samesigninfo合并
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 发改委
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="flowformowners")
public class FlowFormOwners implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowformowners_gen")
	@GenericGenerator(name = "seq_flowformowners_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWFORMOWNERS_ID") })
	private Long id;//主键
	@Column(length = 1000)
	private String fileName;//文件名称   冗余
	@Column(length = 60000)
	private String filePath;//文件路径  冗余
	private Long mainformid;// 主表单编号，flowform的主键***
	private Long flowinforid;//流程编号,冗余
	private Long stateid;//当前状态,冗余
	private Long nodeid;//某节点的拥有者,冗余
	private Integer ismodified=0;//是否已处理,0为未处理，1为查看过，2为处理***
	private Integer deleted=0;//删除标记,1表示删除，2表示正在处理或查看

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users signer;//签批者，或叫拥有者***
	@Column(name = "userID")
    private Long userID;//送审者 冗余
	private Date createDate;//送审时间
	private Date approvalDate;//签批时间   冗余
	@Column(name="state") 
	private Integer state;//状态,目前只要记录待签就可以了
	@Column(name="signnum") 
	private Integer signnum;//签批节点数，为流程图用的
	@Column(name = "approvalID")
    private Long approvalID; //流程编号
	 // 送审说明
    @Column(name = "comment", length = 1000)
    private String comment;//送审备注
    private Long isnew=1L;//是否最新的会签,0为旧的，依次往上加
    private Long subnodeid;//子流程节点
	
	public Integer getDeleted()
	{
		return deleted;
	}
	public void setDeleted(Integer deleted)
	{
		this.deleted = deleted;
	}
	public Integer getIsmodified()
	{
		return ismodified;
	}
	public void setIsmodified(Integer ismodified)
	{
		this.ismodified = ismodified;
	}
	public Long getMainformid()
	{
		return mainformid;
	}
	public void setMainformid(Long mainformid)
	{
		this.mainformid = mainformid;
	}
	public Long getFlowinforid()
	{
		return flowinforid;
	}
	public void setFlowinforid(Long flowinforid)
	{
		this.flowinforid = flowinforid;
	}
	public Long getStateid()
	{
		return stateid;
	}
	public void setStateid(Long stateid)
	{
		this.stateid = stateid;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Long getSubnodeid()
	{
		return subnodeid;
	}
	public void setSubnodeid(Long subnodeid)
	{
		this.subnodeid = subnodeid;
	}
	public Long getIsnew()
	{
		return isnew;
	}
	public void setIsnew(Long isnew)
	{
		this.isnew = isnew;
	}
	public Integer getSignnum()
	{
		return signnum;
	}
	public void setSignnum(Integer signnum)
	{
		this.signnum = signnum;
	}
    public Date getApprovalDate()
	{
		return approvalDate;
	}
	public void setApprovalDate(Date approvalDate)
	{
		this.approvalDate = approvalDate;
	}
	public Long getUserID()
	{
		return userID;
	}
	public void setUserID(Long userID)
	{
		this.userID = userID;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getFilePath()
	{
		return filePath;
	}
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	public Users getSigner()
	{
		return signer;
	}
	public void setSigner(Users signer)
	{
		this.signer = signer;
	}
	public Date getCreateDate()
	{
		return createDate;
	}
	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}
	public Integer getState()
	{
		return state;
	}
	public void setState(Integer state)
	{
		this.state = state;
	}
	public Long getApprovalID()
	{
		return approvalID;
	}
	public void setApprovalID(Long approvalID)
	{
		this.approvalID = approvalID;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
}
