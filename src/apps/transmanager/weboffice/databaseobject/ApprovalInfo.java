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
 * 流程数据处理（列表、处理、显示等）
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="approvalinfo")
public class ApprovalInfo implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalinfo_gen")
    @GenericGenerator(name = "seq_approvalinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALINFO_ID") })
    private Long id;
    // 送审用户ID
    @Column(name = "userID")
    private Long userID;
    @Column(name = "issender")
    private Integer issender=0;//当前流程是否在送文人名下

	@Column(name = "newuserID")
    private Long newuserID;//最新送审人，多次送审后

	@Column(name = "modifytype")
    private Integer modifytype=1;//处理方式，0表示文档协作，1表示签阅
    @Column(length = 10000)
	private String webcontent;//网页内容
 
	private Boolean isreturn;//是否返回送文人
    
    // 审批第几步。
    @Column (name = "approvalStep")
    private int approvalStep=0;
    // 文档权限
    @Column(name = "permit")
    private int permit; 
    // 文档所在空间ID
    @Column(name = "spaceID")
    private long spaceID;
    
    @Column(name = "status")
    private int status;//0表示待阅，1表示待审，2表示已审  3 = 已完成， 4 = 已终止 5;//已成文
    
    @Column(name = "nodetype")
    private Integer nodetype=0;//当前节点类型,0是原来默认的方式，1为并行会签  2串行签批    孙爱华增加

    @Column(name = "changepdftag")
    private Integer changepdftag=0;//转换PDF标记，主要是文档查看找对路径用的，临时这样,0是原来默认的方式，1为已经转换      孙爱华增加

	// 送审时间
    @Column(name = "date")
    private Date date;
    // 送审说明
    @Column(name = "comment",length = 1000)
    private String comment;//目前认为是领导备注
    @Column(name = "sendcomment",length = 1000)
    private String sendcomment;//送文备注

	// 送审文档路径
    @Column(name = "documentPath",length = 3000)
    private String documentPath;
    // 要审批用户ID组，ID之用"|"间隔，此数据是顺序的，排在第一个是第一步审批者。
    @Column(name = "approvalUsersID",length = 1000)
    private String approvalUsersID;//下一步要审批的人
    private Date warndate;//提醒时间，针对当前的签批者

	@Column(name = "lastsignid")
    private Long lastsignid;//最后一个签批者,如果为会签就是null
    
    @Column(name = "operateid")
    private Long operateid;//当前操作者

	// 是否读过
    @Column(name = "isRead",length = 6)
    private String isRead="0";
    //标题
    @Column(length = 255)
    private String title;// 办件名称

    //文件名称
    @Column(length = 1000)
    private String fileName;
	private Boolean predefined;                // 是否是预定义多步审批人员的审批。当预定义的人员执行完成后，该值就变为false

	@Lob
    private ArrayList<Long> preUserIds;        // 预定义的多步具体审批人员id，顺序由list中顺序决定。每执行完成一个预定义人员，该值就从列表中清空。
	@Lob
	private ArrayList<Long> userlist;//参照人员列表，只有串签才有用，中间某个人不按照要求来就直接清空，或存放新的自定义人员【只有nodetype=2时才有效】
	@Lob
    private ArrayList<String> signerslist;//预定义的人员列表，人员之间用,间隔,主要是为了会签返回下一个节点用的
	@Column(length = 255)
	private String filetype;//文件类别   或者是收文方式 政务网 扫描

    private Long fileflowid;//文件流水号，自动在最大号上加1，用户可以输入
    private Date filesuccdate;//成文日期
    private String fromunit;//来文单位
	private String filecode;//文号
    private String filescript;//文件备注
    
	@Column(length = 255)
	private String stepName;     // 当前步骤任务的名称
	private Long signreader;//阅读后记录当前阅读人的userid，签批阅读，如果送审的文档被查看过，或者查看过签批信息，在待审状态都不能终止。
	@Lob
    private ArrayList<Long> sendhistory;//送审历史记录，主要用来返回送审人用的
	
	//approvalinfo相当于一个流程执行对象

    @Column(name = "flowinfoid")
    private Long flowinfoid;//签批执行的哪个流程，自定义流程
    private Long nodeid;//流程执行的当前节点
    private Long modifier;//处理者
    private Date modifytime;//当前处理时间
    private Long stateid;//当前状态
    @Column(length = 50)
    private String statename;//当前状态,用于传递数据

	private Long owner;//当前拥有者或者叫办理人员
	@Column(length = 50)
    private String actionname;//当前操作
    private Date cometime;//接受时间
    @Column(length = 1000)
    private String modifyscript;//办理意见（备注）
    private Long submiter;//提交者
    private Date submitdate;//提交时间
    

	//以上是共有表单字段
    //还有其他表单
    private Long num1;//申请类型
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
	@Column(length = 10)
	private String signtag;//签收标记
	private Date signtagdate;//签收时间
    

	@Transient
	private String canread="";//当前节点是否可传阅
	@Transient
	private ArrayList<String> userlistname;//用来显示预存的用户名
    
    public String getSendcomment() {
		return sendcomment;
	}
	public void setSendcomment(String sendcomment) {
		this.sendcomment = sendcomment;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public ArrayList<String> getSignerslist() {
		return signerslist;
	}
	public void setSignerslist(ArrayList<String> signerslist) {
		this.signerslist = signerslist;
	}
    public Integer getIssender() {
		return issender;
	}
	public void setIssender(Integer issender) {
		this.issender = issender;
	}
	public ArrayList<String> getUserlistname()
	{
		return userlistname;
	}
	public void setUserlistname(ArrayList<String> userlistname)
	{
		this.userlistname = userlistname;
	}
	public ArrayList<Long> getUserlist()
	{
		return userlist;
	}
	public void setUserlist(ArrayList<Long> userlist)
	{
		this.userlist = userlist;
	}
    public Long getNewuserID()
	{
		return newuserID;
	}
	public void setNewuserID(Long newuserID)
	{
		this.newuserID = newuserID;
	}
    public Date getWarndate() {
		return warndate;
	}
	public void setWarndate(Date warndate) {
		this.warndate = warndate;
	}
	public String getCanread()
	{
		return canread;
	}
	public void setCanread(String canread)
	{
		this.canread = canread;
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
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Long getFlowinfoid()
	{
		return flowinfoid;
	}
	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}
	public Long getOperateid()
	{
		return operateid;
	}
	public void setOperateid(Long operateid)
	{
		this.operateid = operateid;
	}
    public ArrayList<Long> getSendhistory()
	{
		return sendhistory;
	}
	public void setSendhistory(ArrayList<Long> sendhistory)
	{
		this.sendhistory = sendhistory;
	}
	public Long getSignreader()
	{
		return signreader;
	}
	public void setSignreader(Long signreader)
	{
		this.signreader = signreader;
	}
	public Long getLastsignid()
	{
		return lastsignid;
	}
	public void setLastsignid(Long lastsignid)
	{
		this.lastsignid = lastsignid;
	}
	public Integer getChangepdftag()
	{
		return changepdftag;
	}
	public void setChangepdftag(Integer changepdftag)
	{
		this.changepdftag = changepdftag;
	}
	public String getStepName()
	{
		return stepName;
	}
	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}

    public Integer getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
	public Boolean getPredefined()
	{
		return predefined;
	}
    public String getFileName() 
    {
		return fileName;
	}
	public void setFileName(String fileName) 
	{
		this.fileName = fileName;
	}
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setId(Long id)
    {
        this.id = id;
    }
    public Long getId()
    {
        return id;
    }
    public void setApprovalStep(int approvalStep)
    {
        this.approvalStep = approvalStep;
    }
    public int getApprovalStep()
    {
        return approvalStep;
    }
    public void setUserID(Long userID)
    {
        this.userID = userID;
    }
    public Long getUserID()
    {
        return userID;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public int getStatus()
    {
        return status;
    }
    public void setPermit(int permit)
    {
        this.permit = permit;
    }
    public int getPermit()
    {
        return permit;
    }
    public void setSpaceID(long spaceID)
    {
        this.spaceID = spaceID;
    }
    public long getSpaceID()
    {
        return spaceID;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
    public Date getDate()
    {
        return date;
    }
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    public String getComment()
    {
        return comment;
    }
    public void setDocumentPath(String documentPath)
    {
        this.documentPath = documentPath;
    }
    public String getDocumentPath()
    {
        return documentPath;
    }
    public void setApprovalUsersID(String approvalUsersID)
    {
        this.approvalUsersID = approvalUsersID;
    }
    public String getApprovalUsersID()
    {
        return approvalUsersID;
    }
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public Boolean isPredefined()
	{
		return predefined;
	}
	public void setPredefined(Boolean predefined)
	{
		this.predefined = predefined;
	}
	public ArrayList<Long> getPreUserIds()
	{
		return preUserIds;
	}
	public void setPreUserIds(ArrayList<Long> preUserIds)
	{
		this.preUserIds = preUserIds;
	}
    
	public String getSigntag()
	{
		return signtag;
	}
	public void setSigntag(String signtag)
	{
		this.signtag = signtag;
	}
	public Date getSigntagdate()
	{
		return signtagdate;
	}
	public void setSigntagdate(Date signtagdate)
	{
		this.signtagdate = signtagdate;
	}
	   
	public Integer getModifytype() {
		return modifytype;
	}
	public void setModifytype(Integer modifytype) {
		this.modifytype = modifytype;
	}
	public String getWebcontent() {
		return webcontent;
	}
	public void setWebcontent(String webcontent) {
		this.webcontent = webcontent;
	}
	public Boolean getIsreturn() {
		return isreturn;
	}
	public void setIsreturn(Boolean isreturn) {
		this.isreturn = isreturn;
	}
    

	public Long getFileflowid() {
		return fileflowid;
	}
	public void setFileflowid(Long fileflowid) {
		this.fileflowid = fileflowid;
	}
	public Date getFilesuccdate() {
		return filesuccdate;
	}
	public void setFilesuccdate(Date filesuccdate) {
		this.filesuccdate = filesuccdate;
	}
	public String getFilecode() {
		return filecode;
	}
	public void setFilecode(String filecode) {
		this.filecode = filecode;
	}
	public String getFilescript() {
		return filescript;
	}
	public void setFilescript(String filescript) {
		this.filescript = filescript;
	}
    public String getFromunit() {
		return fromunit;
	}
	public void setFromunit(String fromunit) {
		this.fromunit = fromunit;
	}
}
