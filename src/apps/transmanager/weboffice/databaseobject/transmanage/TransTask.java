package apps.transmanager.weboffice.databaseobject.transmanage;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 事务的处理记录表（时间关系暂没有对字段进行整理）
 * <p>
 * <p>
 * Cloud版本:      CLOUDOFFICE v1.0
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-10-24
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
@Entity
@Table(name="transtask")
public class TransTask  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transtask_gen")
    @GenericGenerator(name = "seq_transtask_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSTASK_ID") })
    private Long id;
    // 审批ID
    @Column(name = "transid")
    private Long transid;

    @Column(name = "resendtag")
    private Integer resendtag=0;//签批时再次送下一级审批标记

	@Column(name = "nodetype")
    private Integer nodetype=1;//当前节点类型,0是原来默认的方式，1为并行会签,2为串签,与resendtag配合使用

    private Boolean isnodetag=false;//历史类型，

    @Column(length = 100)
    private String title;//标题 冗余
 
    @Column(length = 100)
    private String stepName;     // 该步骤任务的名称,冗余
    @Column (name = "transstep")
    private int transstep=0;//处理第几步，相当于流程节点

	private Long sameid;//会签编号，用来显示流程图用的
    
	private Long actionid;//这是新签批用的
	private Long stateid;// 当前状态
	
	@Column(length = 10000)
    private String signers;//办理者的人ID,多人用,间隔
	@Column(length = 10000)
	private String sendreaders;//抄阅者，多人用,间隔
	
    @Column(name = "comment",length = 1000)
    private String comment;// 处理说明
	private Long submiter;// 提交者
	private Date submitdate;// 提交时间

	@Transient
	private String modifyname="";//办理人
	@Column(length = 100)
    private String submitname="";//提交人
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTransid() {
		return transid;
	}
	public void setTransid(Long transid) {
		this.transid = transid;
	}
	public Integer getResendtag() {
		return resendtag;
	}
	public void setResendtag(Integer resendtag) {
		this.resendtag = resendtag;
	}
	public Integer getNodetype() {
		return nodetype;
	}
	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}
	public Boolean getIsnodetag() {
		return isnodetag;
	}
	public void setIsnodetag(Boolean isnodetag) {
		this.isnodetag = isnodetag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public int getTransstep() {
		return transstep;
	}
	public void setTransstep(int transstep) {
		this.transstep = transstep;
	}
	public Long getSameid() {
		return sameid;
	}
	public void setSameid(Long sameid) {
		this.sameid = sameid;
	}
	public Long getActionid() {
		return actionid;
	}
	public void setActionid(Long actionid) {
		this.actionid = actionid;
	}
	public Long getStateid() {
		return stateid;
	}
	public void setStateid(Long stateid) {
		this.stateid = stateid;
	}
	public String getSigners() {
		return signers;
	}
	public void setSigners(String signers) {
		this.signers = signers;
	}
	public String getSendreaders() {
		return sendreaders;
	}
	public void setSendreaders(String sendreaders) {
		this.sendreaders = sendreaders;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public String getModifyname() {
		return modifyname;
	}
	public void setModifyname(String modifyname) {
		this.modifyname = modifyname;
	}
	public String getSubmitname() {
		return submitname;
	}
	public void setSubmitname(String submitname) {
		this.submitname = submitname;
	}
    

}
