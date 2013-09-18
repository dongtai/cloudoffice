package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;
/**
 * 此表是为同时会签（串签）使用的，此表只是增加了字段
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 最新版本
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="samesigninfo")
public class SameSignInfo implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_samesigninfo_gen")
	@GenericGenerator(name = "seq_samesigninfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SAMESIGNINFO_ID") })
	private Long sid;//主键
	private Integer nodetype=0;//是串(0)还是并(1)
	private Boolean isreturn;//是否返回送审人
	@Column(length = 3000)
	private String fileName;//文件名称   冗余
	@Column(length = 3000)
	private String filePath;//文件路径  冗余
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users signer;//签批者，不会变的
	@Column(name = "userID")
    private Long userID;//送审者 冗余

	private Date createDate;//送审时间
	
	private Date approvalDate;//签批时间   冗余
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数

	@Column(name="state") 
	private Integer state=ApproveConstants.NEW_STATUS_WAIT;//状态,记录当前人的状态
	
	@Column(name="actionid") 
	private Long actionid;//动作

	@Column(name="signnum") 
	private Integer signnum;//签批节点数，为流程图用的

	@Column(name = "approvalID")
    private Long approvalID; //流程编号
	@Column(name = "taskid")
	private Long taskid;//签批历史编号
	
	private Integer isview=0;//是否显示，相当于删除，0显示，1不显示

	// 送审说明
    @Column(name = "comment",length = 1000)
    private String comment;//送审备注
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    private Long islast=0L;//如果该人被多次送审，只算最后一次，过去的依次加1，最新的为0
	private Long subnodeid;//子流程节点
	private Long mainformid;// 主表单编号，flowform的主键***
	private Long flowinforid;//流程编号,冗余
	private Long stateid;//当前状态,冗余
	private Long nodeid;//某节点的拥有者,冗余
	private Integer ismodified=0;//是否已处理,0为未处理，1为查看过，2为处理***
	private Integer deleted=0;//删除标记,1表示删除，2表示正在处理或查看
	@Column(length = 6)
	private String signtag;//签收标记,已签为Y
	private Date signtagdate;//签收时间
	@Transient
	private Integer currentsign=0;//当前是否为签批
	
	public SameSignInfo()
	{
		
	}

	public Integer getIsview()
	{
		return isview;
	}
	public void setIsview(Integer isview)
	{
		this.isview = isview;
	}
	public Long getActionid()
	{
		return actionid;
	}
	public void setActionid(Long actionid)
	{
		this.actionid = actionid;
	}
    public Long getIslast()
	{
		return islast;
	}

	public void setIslast(Long islast)
	{
		this.islast = islast;
	}
	public Long getTaskid()
	{
		return taskid;
	}
	public void setTaskid(Long taskid)
	{
		this.taskid = taskid;
	}
	public Integer getWarnnum() {
		return warnnum;
	}
	public void setWarnnum(Integer warnnum) {
		this.warnnum = warnnum;
	}
	public Integer getNodetype() {
		return nodetype;
	}
	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}
	public String getSigntag() {
		return signtag;
	}
	public void setSigntag(String signtag) {
		this.signtag = signtag;
	}
	public Date getSigntagdate() {
		return signtagdate;
	}
	public void setSigntagdate(Date signtagdate) {
		this.signtagdate = signtagdate;
	}
	public Date getWarndate() {
		return warndate;
	}
	public void setWarndate(Date warndate) {
		this.warndate = warndate;
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
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Integer getIsmodified()
	{
		return ismodified;
	}
	public void setIsmodified(Integer ismodified)
	{
		this.ismodified = ismodified;
	}
	public Integer getDeleted()
	{
		return deleted;
	}
	public void setDeleted(Integer deleted)
	{
		this.deleted = deleted;
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



	public Long getSid()
	{
		return sid;
	}



	public void setSid(Long sid)
	{
		this.sid = sid;
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

	public Boolean getIsreturn() {
		return isreturn;
	}
	public void setIsreturn(Boolean isreturn) {
		this.isreturn = isreturn;
	}

	public Integer getCurrentsign() {
		return currentsign;
	}

	public void setCurrentsign(Integer currentsign) {
		this.currentsign = currentsign;
	}

}
