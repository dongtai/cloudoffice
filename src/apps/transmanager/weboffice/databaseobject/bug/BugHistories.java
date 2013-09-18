package apps.transmanager.weboffice.databaseobject.bug;


import java.util.Date;
import java.util.List;

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

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * BugInfos bug历史记录表，BUG信息表+BUG处理表的记录
 * @author 孙爱华
 * 2013.6.14
 */
@Entity
@Table(name="bughistories")
public class BugHistories implements SerializableAdapter {
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bughistories_gen")
	@GenericGenerator(name = "seq_bughistories_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGHISTORIES_ID") })
	private Long id;//主键
	private Long bugid;//bug编号
	private Long userid;//报BUG人员
	private String username;//报BUG人员名称，冗余
	private Date adddate;//报BUG时间
	private Long numid;//报BUG人的自己BUG编号
	private Long softid=1l;//软件号，默认1为信电局版本
	private Integer errortype=1;//问题类别，1为云办公，2为OFFICE，3为移动，4为IOS
	private Integer bugtype=1;//BUG类型，1为BUG，2为建议
	private Integer seriousid;//严重性，1、一般问题，2、无法使用，3、死机
	private Integer environmentid;//环境
	private String otheren;//其他环境
	private String os;//操作系统

	@Column(length = 255)
	private String summer;	//问题概述
	@Column(length = 2000)
	private String opstep;	//操作步骤
	private Boolean isedit=false;//是否可编辑，编辑完后自动变为false
	private Date edittime;//编辑时间
	private Integer modifyresult=0;//处理结果 0、未确认，1、BUG，2、重复BUG，3、产品定义
	private Long rebugid;//重复的BUG号
	private Integer isdelete=0;//是否删除标记，1为删除
	private String ophistype;//操作类型，新增BUG、修改BUG，处理BUG
	private Long modifier;//处理人,不要从modifyinfo中取，自己的时间和人
	private Date modifydate;//处理时间
	private Integer buglevel=1;//BUG优先级，1，立解，2、必解，3、要解，4，缓解，5，不解
	private Long solveuser;//解决人
	private Date solvedate;//解决时间
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugActions bugActions;//处理动作
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugStates bugStates;//处理状态
	private Long ownerid;//当前负责人
	
	private String rebugs;//重复BUG号
	private String testuser;//测试用户
	private Date testdate;//测试时间
	private Integer testresult=0;//测试结果0未测试，1、测试通过，2、测试不通过
	@Column(length = 2000)
	private String modifyscript;	//处理备注
	
	@Transient
	private List<BugFiles> bugFiles;//BUG附件内容，单条历史记录显示时要将附件显示出来
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getNumid() {
		return numid;
	}
	public void setNumid(Long numid) {
		this.numid = numid;
	}
	public Long getSoftid() {
		return softid;
	}
	public void setSoftid(Long softid) {
		this.softid = softid;
	}
	public Integer getErrortype() {
		return errortype;
	}
	public void setErrortype(Integer errortype) {
		this.errortype = errortype;
	}
	public Integer getBugtype() {
		return bugtype;
	}
	public void setBugtype(Integer bugtype) {
		this.bugtype = bugtype;
	}
	public Integer getSeriousid() {
		return seriousid;
	}
	public void setSeriousid(Integer seriousid) {
		this.seriousid = seriousid;
	}
	public Integer getEnvironmentid() {
		return environmentid;
	}
	public void setEnvironmentid(Integer environmentid) {
		this.environmentid = environmentid;
	}
	public String getSummer() {
		return summer;
	}
	public void setSummer(String summer) {
		this.summer = summer;
	}
	public String getOpstep() {
		return opstep;
	}
	public void setOpstep(String opstep) {
		this.opstep = opstep;
	}
	public Boolean getIsedit() {
		return isedit;
	}
	public void setIsedit(Boolean isedit) {
		this.isedit = isedit;
	}
	public Date getEdittime() {
		return edittime;
	}
	public void setEdittime(Date edittime) {
		this.edittime = edittime;
	}
	
	public Integer getModifyresult() {
		return modifyresult;
	}
	public void setModifyresult(Integer modifyresult) {
		this.modifyresult = modifyresult;
	}
	public Long getRebugid() {
		return rebugid;
	}
	public void setRebugid(Long rebugid) {
		this.rebugid = rebugid;
	}
	public Integer getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}
	public Long getModifier() {
		return modifier;
	}
	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}
	public Date getModifydate() {
		return modifydate;
	}
	public void setModifydate(Date modifydate) {
		this.modifydate = modifydate;
	}
	public Integer getBuglevel() {
		return buglevel;
	}
	public void setBuglevel(Integer buglevel) {
		this.buglevel = buglevel;
	}
	public Long getSolveuser() {
		return solveuser;
	}
	public void setSolveuser(Long solveuser) {
		this.solveuser = solveuser;
	}
	public Date getSolvedate() {
		return solvedate;
	}
	public void setSolvedate(Date solvedate) {
		this.solvedate = solvedate;
	}
	public BugActions getBugActions() {
		return bugActions;
	}
	public void setBugActions(BugActions bugActions) {
		this.bugActions = bugActions;
	}
	public BugStates getBugStates() {
		return bugStates;
	}
	public void setBugStates(BugStates bugStates) {
		this.bugStates = bugStates;
	}
	public String getTestuser() {
		return testuser;
	}
	public void setTestuser(String testuser) {
		this.testuser = testuser;
	}
	public Date getTestdate() {
		return testdate;
	}
	public void setTestdate(Date testdate) {
		this.testdate = testdate;
	}
	public Integer getTestresult() {
		return testresult;
	}
	public void setTestresult(Integer testresult) {
		this.testresult = testresult;
	}
	public String getModifyscript() {
		return modifyscript;
	}
	public void setModifyscript(String modifyscript) {
		this.modifyscript = modifyscript;
	}
	
	public Long getBugid() {
		return bugid;
	}
	public void setBugid(Long bugid) {
		this.bugid = bugid;
	}
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
	}
	public String getOphistype() {
		return ophistype;
	}
	public void setOphistype(String ophistype) {
		this.ophistype = ophistype;
	}
	public List<BugFiles> getBugFiles() {
		return bugFiles;
	}
	public void setBugFiles(List<BugFiles> bugFiles) {
		this.bugFiles = bugFiles;
	}
	public String getOtheren() {
		return otheren;
	}
	public void setOtheren(String otheren) {
		this.otheren = otheren;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getRebugs() {
		return rebugs;
	}
	public void setRebugs(String rebugs) {
		this.rebugs = rebugs;
	}
	public Long getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(Long ownerid) {
		this.ownerid = ownerid;
	}
}