package apps.transmanager.weboffice.databaseobject.transmanage;

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
 * 事务数据处理（列表、处理、显示等）（时间关系暂没有对字段进行整理）
 * 为了省事，暂不做大的改动
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="transinfo")
public class TransInfo implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transinfo_gen")
    @GenericGenerator(name = "seq_transinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSINFO_ID") })
    private Long id;
    // 交办用户ID
    @Column(name = "userID")
    private Long userID;
    @Column(name = "issender")
    private Integer issender=0;//当前事务是否在送文人名下

	@Column(name = "modifytype")
    private Integer modifytype=1;//处理方式，暂不用
    @Column(length = 10000)
	private String webcontent;//事务内容
 
    @Column (name = "transStep")
    private int transStep=0;//交办步骤（次数）
    
    @Column(name = "status")
    private Long status;//0表示交办，1表示已办， 4 = 终止 5//销毁
    
    @Column(name = "nodetype")
    private Integer nodetype=1;//当前节点类型,0是原来默认的方式，1为并行会签  2串行签批 默认是并行（并发处理）

	
    @Column(name = "senddate")
    private Date senddate;// 交办时间
    
    @Column(name = "comment",length = 1000)
    private String comment;// 交办说明
    private Date warndate;//提醒时间，针对当前的签批者
	@Column(name = "lastsignid")
    private Long lastsignid;//最后一个处理者,如果为会签就是null
    
    @Column(name = "isRead", length = 6)
    private String isRead="0";// 是否读过，查看过就不能反悔和撤销
	private Long reader;//最后一个阅读人,为提示用的。

	@Column(length = 100)
    private String title;// 办件名称

	@Lob
	private ArrayList<Long> userlist;//参照人员列表，(或者叫预备人员)

	@Column(length = 100)
	private String stepName;  // 当前步骤任务的名称,用不上

    private Long submiter;//提交者
    private Date submitdate;//提交时间
    private Integer isview=0;//软删除标记

	@Transient
	private ArrayList<String> userlistname;//用来显示预存的用户名
	@Transient
	private ArrayList filelist;//用来存放附件
	@Transient
	private String sendname;//交办人名
	@Transient
	private ArrayList taskfiles;//处理过程中附件
	@Transient
	private String detail;//上次保存的办理详情

    
	public Integer getIsview() {
		return isview;
	}

	public void setIsview(Integer isview) {
		this.isview = isview;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public ArrayList getTaskfiles() {
		return taskfiles;
	}

	public void setTaskfiles(ArrayList taskfiles) {
		this.taskfiles = taskfiles;
	}

	public String getSendname() {
		return sendname;
	}

	public void setSendname(String sendname) {
		this.sendname = sendname;
	}

	public ArrayList getFilelist() {
		return filelist;
	}

	public void setFilelist(ArrayList filelist) {
		this.filelist = filelist;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public Integer getIssender() {
		return issender;
	}

	public void setIssender(Integer issender) {
		this.issender = issender;
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

	public int getTransStep() {
		return transStep;
	}

	public void setTransStep(int transStep) {
		this.transStep = transStep;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Integer getNodetype() {
		return nodetype;
	}

	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}

	public Date getSenddate() {
		return senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getWarndate() {
		return warndate;
	}

	public void setWarndate(Date warndate) {
		this.warndate = warndate;
	}

	public Long getLastsignid() {
		return lastsignid;
	}

	public void setLastsignid(Long lastsignid) {
		this.lastsignid = lastsignid;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public Long getReader() {
		return reader;
	}

	public void setReader(Long reader) {
		this.reader = reader;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Long> getUserlist() {
		return userlist;
	}

	public void setUserlist(ArrayList<Long> userlist) {
		this.userlist = userlist;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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

	public ArrayList<String> getUserlistname() {
		return userlistname;
	}

	public void setUserlistname(ArrayList<String> userlistname) {
		this.userlistname = userlistname;
	}
    

}
