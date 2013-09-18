package apps.transmanager.weboffice.databaseobject;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 事务审批、批阅、协作
 * <p>
 * <p>
 * EIO版本:        WebOffice v3.0
 * <p>
 * 作者:           徐文平  孙爱华改进
 * <p>
 * 日期:           2012-9-14
 * <p>
 * 负责人:         徐文平
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
@Entity
@Table(name="approvaltask")
public class ApprovalTask  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvaltask_gen")
    @GenericGenerator(name = "seq_approvaltask_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALTASK_ID") })
    private Long id;
    // 审批ID
    @Column(name = "approvalID")
    private long approvalID;
    // 文档所在空间ID
    @Column(name = "spaceID")
    private long spaceID;
    // 审批者ID
    @Column(name = "approvalUserID")
    private long approvalUserID;
    // 审批动作  0 = 通过， 1 = 拒绝
    @Column(name = "action")
    private Long action;//1=送签 2=送协作3=签批 4=批阅 5=协作  10=终止
    
    @Column(name = "resendtag")
    private Integer resendtag=0;//签批时再次送下一级审批标记

	@Column(name = "nodetype")
    private Integer nodetype=0;//当前节点类型,0是原来默认的方式，1为并行会签,2为串签      孙爱华增加

    @Column(name = "changepdftag")
    private Integer changepdftag=0;//转换PDF标记，主要是文档查看找对路径用的，临时这样,0是原来默认的方式，1为已经转换      孙爱华增加

    private Boolean isnodetag=false;//是否签批节点，如果是送签的就标记，这样流程图好生成，有送的，然后根据taskid到samesigninfo表中获取相关内容

	// 审批日期
    @Column(name = "date")
    private Date date;
    //文件名
    @Column(length = 3000)
    private String fileName;
    @Column(length = 10000)
    private String webcontent;//网页内容

	//标题
    @Column(length = 255)
    private String title;
 // 审批说明
    @Column(name = "comment",length = 1000)
    private String comment;
    
 // 审批说明
    @Column(name = "versionName", length = 1000)
    private String versionName;
  //下一个接收者
    private Long nextAcceptorID;
    // 抄送阅读者
    @Lob
    private ArrayList<Long> readers;
    
    @Column(length = 100)
    private String stepName;     // 该步骤任务的名称
    @Column (name = "approvalStep")
    private int approvalStep=0;//审批第几步，相当于流程节点

	private Long sameid;//会签编号，用来显示流程图用的
    
    @Column(name = "flowinfoid")
	private Long flowinfoid;// 签批执行的哪个流程，自定义流程
	private Long mainformid;// 主表单编号，flowform的主键

	private Long nodeid;// 流程执行的当前节点
	private Long modifier;// 处理者
	private Date modifytime;// 当前处理时间
	private Long actionid;//这是新签批用的
	private Long stateid;// 当前状态
	@Column(length = 100)
	private String statename;
	@Column(length = 10000)
    private String coopers;//文档协作者，多人之间用,间隔
	@Column(length = 10000)
    private String signers;//签批者的人ID,多人用,间隔
	@Column(length = 10000)
	private String sendreaders;//送阅人，多人用,间隔
	
	private Integer signtype=0;//签批类别，1为串签,2为并行会签

	private Long owner;// 当前拥有者或者叫办理人员
	@Column(length = 100)
	private String actionname;// 当前操作
	private Date cometime;// 接受时间,暂时没用到
	private Date signtagdate;//签收时间，用户流程图时间戳显示

	@Column(length = 1000)
	private String modifyscript;// 办理意见（备注）
	private Long submiter;// 提交者
	private Date submitdate;// 提交时间

	// 以上是共有表单字段
	// 还有其他表单
	private Long num1;// 申请类型
	private Long num2;
	private Long num3;
	private Long num4;
	private Long num5;
	private Long num6;
	private Long num7;
	private Long num8;
	private Long num9;
	private Long num10;
	private Long num11;
	private Long num12;

	@Column(length = 100)
	private String numname1;// 办文单
	@Column(length = 100)
	private String numname2;// 申请人
	@Column(length = 100)
	private String numname3;// 申请人代码
	@Column(length = 100)
	private String numname4;// 办件名称
	@Column(length = 100)
	private String numname5;// 发文号
	@Column(length = 20)
	private String numname6;// 联系电话
	@Column(length = 50)
	private String numname7;// 联系人
	@Column(length = 100)
	private String numname8;
	@Column(length = 100)
	private String numname9;
	@Column(length = 1000)
	private String numname10;
	@Column(length = 1000)
	private String numname11;
	@Column(length = 1000)
	private String numname12;
	 @Transient
	private String modifyname="";//办理人
	 @Column(length = 100) 
    private String submitname;//提交人
    @Column(name = "opscript",length = 2000)
    private String opscript;//操作描述,暂不用
    

	public Date getSigntagdate() {
		return signtagdate;
	}
	public void setSigntagdate(Date signtagdate) {
		this.signtagdate = signtagdate;
	}
	public Integer getSigntype() {
		return signtype;
	}
	public void setSigntype(Integer signtype) {
		this.signtype = signtype;
	}
    public String getOpscript() {
		return opscript;
	}
	public void setOpscript(String opscript) {
		this.opscript = opscript;
	}
	public int getApprovalStep()
	{
		return approvalStep;
	}
	public void setApprovalStep(int approvalStep)
	{
		this.approvalStep = approvalStep;
	}
	public Boolean getIsnodetag()
	{
		return isnodetag;
	}
	public void setIsnodetag(Boolean isnodetag)
	{
		this.isnodetag = isnodetag;
	}
	public String getCoopers() {
		return coopers;
	}
	public void setCoopers(String coopers) {
		this.coopers = coopers;
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
    public Long getMainformid()
	{
		return mainformid;
	}
	public void setMainformid(Long mainformid)
	{
		this.mainformid = mainformid;
	}
	public Long getModifier()
	{
		return modifier;
	}
	public void setModifier(Long modifier)
	{
		this.modifier = modifier;
	}
	public Date getModifytime()
	{
		return modifytime;
	}
	public void setModifytime(Date modifytime)
	{
		this.modifytime = modifytime;
	}
	public Long getStateid()
	{
		return stateid;
	}
	public void setStateid(Long stateid)
	{
		this.stateid = stateid;
	}
	public String getStatename()
	{
		return statename;
	}
	public void setStatename(String statename)
	{
		this.statename = statename;
	}
	public Long getOwner()
	{
		return owner;
	}
	public void setOwner(Long owner)
	{
		this.owner = owner;
	}
	public String getActionname()
	{
		return actionname;
	}
	public void setActionname(String actionname)
	{
		this.actionname = actionname;
	}
	public Date getCometime()
	{
		return cometime;
	}
	public void setCometime(Date cometime)
	{
		this.cometime = cometime;
	}
	public String getModifyscript()
	{
		return modifyscript;
	}
	public void setModifyscript(String modifyscript)
	{
		this.modifyscript = modifyscript;
	}
	public Long getSubmiter()
	{
		return submiter;
	}
	public void setSubmiter(Long submiter)
	{
		this.submiter = submiter;
	}
	public Date getSubmitdate()
	{
		return submitdate;
	}
	public void setSubmitdate(Date submitdate)
	{
		this.submitdate = submitdate;
	}
	public Long getNum1()
	{
		return num1;
	}
	public void setNum1(Long num1)
	{
		this.num1 = num1;
	}
	public Long getNum2()
	{
		return num2;
	}
	public void setNum2(Long num2)
	{
		this.num2 = num2;
	}
	public Long getNum3()
	{
		return num3;
	}
	public void setNum3(Long num3)
	{
		this.num3 = num3;
	}
	public Long getNum4()
	{
		return num4;
	}
	public void setNum4(Long num4)
	{
		this.num4 = num4;
	}
	public Long getNum5()
	{
		return num5;
	}
	public void setNum5(Long num5)
	{
		this.num5 = num5;
	}
	public Long getNum6()
	{
		return num6;
	}
	public void setNum6(Long num6)
	{
		this.num6 = num6;
	}
	public Long getNum7()
	{
		return num7;
	}
	public void setNum7(Long num7)
	{
		this.num7 = num7;
	}
	public Long getNum8()
	{
		return num8;
	}
	public void setNum8(Long num8)
	{
		this.num8 = num8;
	}
	public Long getNum9()
	{
		return num9;
	}
	public void setNum9(Long num9)
	{
		this.num9 = num9;
	}
	public Long getNum10()
	{
		return num10;
	}
	public void setNum10(Long num10)
	{
		this.num10 = num10;
	}
	public Long getNum11()
	{
		return num11;
	}
	public void setNum11(Long num11)
	{
		this.num11 = num11;
	}
	public Long getNum12()
	{
		return num12;
	}
	public void setNum12(Long num12)
	{
		this.num12 = num12;
	}
	public String getNumname1()
	{
		return numname1;
	}
	public void setNumname1(String numname1)
	{
		this.numname1 = numname1;
	}
	public String getNumname2()
	{
		return numname2;
	}
	public void setNumname2(String numname2)
	{
		this.numname2 = numname2;
	}
	public String getNumname3()
	{
		return numname3;
	}
	public void setNumname3(String numname3)
	{
		this.numname3 = numname3;
	}
	public String getNumname4()
	{
		return numname4;
	}
	public void setNumname4(String numname4)
	{
		this.numname4 = numname4;
	}
	public String getNumname5()
	{
		return numname5;
	}
	public void setNumname5(String numname5)
	{
		this.numname5 = numname5;
	}
	public String getNumname6()
	{
		return numname6;
	}
	public void setNumname6(String numname6)
	{
		this.numname6 = numname6;
	}
	public String getNumname7()
	{
		return numname7;
	}
	public void setNumname7(String numname7)
	{
		this.numname7 = numname7;
	}
	public String getNumname8()
	{
		return numname8;
	}
	public void setNumname8(String numname8)
	{
		this.numname8 = numname8;
	}
	public String getNumname9()
	{
		return numname9;
	}
	public void setNumname9(String numname9)
	{
		this.numname9 = numname9;
	}
	public String getNumname10()
	{
		return numname10;
	}
	public void setNumname10(String numname10)
	{
		this.numname10 = numname10;
	}
	public String getNumname11()
	{
		return numname11;
	}
	public void setNumname11(String numname11)
	{
		this.numname11 = numname11;
	}
	public String getNumname12()
	{
		return numname12;
	}
	public void setNumname12(String numname12)
	{
		this.numname12 = numname12;
	}
	public String getModifyname()
	{
		return modifyname;
	}
	public void setModifyname(String modifyname)
	{
		this.modifyname = modifyname;
	}
	public String getSubmitname()
	{
		return submitname;
	}
	public void setSubmitname(String submitname)
	{
		this.submitname = submitname;
	}
	public Long getFlowinfoid()
	{
		return flowinfoid;
	}
	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Integer getResendtag()
	{
		return resendtag;
	}
	public void setResendtag(Integer resendtag)
	{
		this.resendtag = resendtag;
	}
	public Long getSameid()
	{
		return sameid;
	}
	public void setSameid(Long sameid)
	{
		this.sameid = sameid;
	}
	public Integer getChangepdftag()
	{
		return changepdftag;
	}
	public void setChangepdftag(Integer changepdftag)
	{
		this.changepdftag = changepdftag;
	}
    public Integer getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
	public String getStepName()
	{
		return stepName;
	}
	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}
	
	public String getFileName() {
		return fileName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Long getNextAcceptorID() {
		return nextAcceptorID;
	}
	public void setNextAcceptorID(Long nextAcceptorID) {
		this.nextAcceptorID = nextAcceptorID;
	}
	
    
    
    public void setId(Long id)
    {
        this.id = id;
    }
    public Long getId()
    {
        return id;
    }
    public void setApprovalID(long approvalID)
    {
        this.approvalID = approvalID;
    }
    public long getApprovalID()
    {
        return approvalID;
    }
    public void setSpaceID(long spaceID)
    {
        this.spaceID = spaceID;
    }
    public long getSpaceID()
    {
        return spaceID;
    }
    public void setApprovalUserID(long approvalUserID)
    {
        this.approvalUserID = approvalUserID;
    }
    public long getApprovalUserID()
    {
        return approvalUserID;
    }
    public void setAction(Long action)
    {
        this.action = action;
    }
    public Long getAction()
    {
        return action;
    }
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    public String getComment()
    {
        return comment;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
    public Date getDate()
    {
        return date;
    }
    public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public ArrayList<Long> getReaders() {
		return readers;
	}
	public void setReaders(ArrayList<Long> readers) {
		this.readers = readers;
	}
	public Long getActionid() {
		return actionid;
	}
	public void setActionid(Long actionid) {
		this.actionid = actionid;
	}
    public String getWebcontent()
	{
		return webcontent;
	}
	public void setWebcontent(String webcontent)
	{
		this.webcontent = webcontent;
	}
}
