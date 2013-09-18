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
 * 此表是为用于文件协作
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 新版本
 * @see     
 * @since   web3.0
 */
@Entity
@Table(name="approvalcooper")
public class ApprovalCooper implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalcooper_gen")
	@GenericGenerator(name = "seq_approvalcooper_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALCOOPER_ID") })
	private Long id;//主键
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users cooper;//协作者，目前都是并行方式
	private Integer nodetype=1;//是串(0)还是并(1)
	private Long taskid;//签批历史编号

	@Column(name = "userID")
    private Long userID;//送协作者 冗余
	private Date createDate;//送时间
	
	private Date approvalDate;//协作者协作时间   冗余
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数
	@Column(length = 1000)
	private String fileName;             // 文件名       冗余
    @Column(length = 10000)
    private String webcontent;//网页内容

	@Column(name="state") 
	private Integer state=0;//状态,记录当前待协作人的状态，待协作0，已协作3
	
	@Column(name="actionid") 
	private Integer actionid;//动作1送审   这些应该放到数据库中2送协作3签批4批阅5协作10终止

	@Column(name="coopnum") 
	private Integer coopnum;//协作节点数，分支数，冗余

	@Column(name = "approvalID")
    private Long approvalID; //流程编号，对应ApprovalInfo表
	private Integer isview=0;//是否显示，相当于删除，0显示，1不显示

	// 协作说明
    @Column(name = "comment",length = 1000)
    private String comment;//协作备注
    private Long isnew=0L;//是否最新的协作,0为最新的（为了查询方便），过期的依次往上加
    private Long islast=0L;//是否最后一次
    @Column(length = 3)
	private String signtag;//签收标记,已签为Y
	private Date signtagdate;//签收时间
	
	public Integer getIsview()
	{
		return isview;
	}
	public void setIsview(Integer isview)
	{
		this.isview = isview;
	}
	public Integer getActionid()
	{
		return actionid;
	}
	public void setActionid(Integer actionid)
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
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getWebcontent()
	{
		return webcontent;
	}
	public void setWebcontent(String webcontent)
	{
		this.webcontent = webcontent;
	}
	public Integer getNodetype() {
		return nodetype;
	}
	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getCooper() {
		return cooper;
	}
	public void setCooper(Users cooper) {
		this.cooper = cooper;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
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
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getCoopnum() {
		return coopnum;
	}
	public void setCoopnum(Integer coopnum) {
		this.coopnum = coopnum;
	}
	public Long getApprovalID() {
		return approvalID;
	}
	public void setApprovalID(Long approvalID) {
		this.approvalID = approvalID;
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

}
