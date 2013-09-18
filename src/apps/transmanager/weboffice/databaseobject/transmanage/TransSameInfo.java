package apps.transmanager.weboffice.databaseobject.transmanage;

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

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;
/**
 * 此表是为事务处理人表（时间关系暂没有对字段进行整理）
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 最新版本
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="transsameinfo")
public class TransSameInfo implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transsameinfo_gen")
	@GenericGenerator(name = "seq_transsameinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSSAMEINFO_ID") })
	private Long id;//主键
	private Integer nodetype=0;//是串(0)还是并(1)
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users signer;//处理者，不会变的
	@Column(name = "senduser")
    private Long senduser;//交办者
	private Date senddate;//交办时间
	
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数

	@Column(name="state") 
	private Long state=ApproveConstants.TRANS_STATUS_START;//状态,记录当前人的状态
	
	@Column(name="actionid") 
	private Long actionid;//动作

	@Column(name="signnum") 
	private Integer signnum;//签批节点数，为流程图用的

	@Column(name = "transid")
    private Long transid; //事务编号
	@Column(name = "taskid")
	private Long taskid;//事务对应历史编号，为流程图用的
	
	private Integer isview=0;//是否显示，相当于删除，0显示，1不显示
    @Column(name = "comment",length = 1000)
    private String comment;//处理备注
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    private Long islast=0L;//如果该人被多次送审，只算最后一次，过去的依次加1，最新的为0
    @Column(length = 6)
	private String signtag;//签收标记,已签为Y
	private Date signtagdate;//签收时间
	private Long submiter;//提交者,如果有代理人就是代理人号
    private Date submitdate;//提交时间
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getNodetype() {
		return nodetype;
	}
	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}
	public Users getSigner() {
		return signer;
	}
	public void setSigner(Users signer) {
		this.signer = signer;
	}
	public Long getSenduser() {
		return senduser;
	}
	public void setSenduser(Long senduser) {
		this.senduser = senduser;
	}
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
	public Date getWarndate() {
		return warndate;
	}
	public void setWarndate(Date warndate) {
		this.warndate = warndate;
	}
	public Integer getWarnnum() {
		return warnnum;
	}
	public void setWarnnum(Integer warnnum) {
		this.warnnum = warnnum;
	}
	public Long getState() {
		return state;
	}
	public void setState(Long state) {
		this.state = state;
	}
	public Long getActionid() {
		return actionid;
	}
	public void setActionid(Long actionid) {
		this.actionid = actionid;
	}
	public Integer getSignnum() {
		return signnum;
	}
	public void setSignnum(Integer signnum) {
		this.signnum = signnum;
	}
	public Long getTransid() {
		return transid;
	}
	public void setTransid(Long transid) {
		this.transid = transid;
	}
	public Long getTaskid() {
		return taskid;
	}
	public void setTaskid(Long taskid) {
		this.taskid = taskid;
	}
	public Integer getIsview() {
		return isview;
	}
	public void setIsview(Integer isview) {
		this.isview = isview;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getIsnew() {
		return isnew;
	}
	public void setIsnew(Long isnew) {
		this.isnew = isnew;
	}
	public Long getIslast() {
		return islast;
	}
	public void setIslast(Long islast) {
		this.islast = islast;
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
	public Long getSubmiter() {
		return submiter;
	}
	public void setSubmiter(Long submiter) {
		this.submiter = submiter;
	}
	public Date getSubmitdate() {
		return submitdate;
	}
	public void setSubmitdate(Date submitdate) {
		this.submitdate = submitdate;
	}
	
	

}
