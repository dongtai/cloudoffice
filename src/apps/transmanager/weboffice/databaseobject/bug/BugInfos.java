package apps.transmanager.weboffice.databaseobject.bug;

import java.util.Date;
import java.util.List;

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
 * BugInfos 问题收集表
 * @author 孙爱华
 * 2013.6.14
 */
@Entity
@Table(name="buginfos")
public class BugInfos implements SerializableAdapter {
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_buginfos_gen")
	@GenericGenerator(name = "seq_buginfos_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGINFOS_ID") })
	private Long id;//BUG编号
	private Long userid;//报BUG人员
	private String username;//报BUG人员名称，冗余
	private Date adddate=new Date();//报BUG时间
	private Long numid;//报BUG人的自己BUG编号
	private Long softid=1l;//软件号，默认1为信电局版本
	private Integer errortype=1;//问题类别，1为云办公，2为OFFICE，3为移动，4为IOS
	private Integer bugtype=1;//BUG类型，1为BUG，2为建议
	private Integer seriousid;//严重性，1、一般问题，2、无法使用，3、死机 4数据错误 5 数据丢失
	private Integer environmentid;//环境
	private String otheren;//其他环境
	private String os;//操作系统
	
	@Column(length = 255)
	private String summer;	//问题概述
	@Column(length = 2000)
	private String opstep;	//操作步骤
	private Boolean isedit=true;//是否可编辑，当被受理后自动变为false
	private Date edittime;//编辑时间
	
	private Integer modifyresult=1;//状态 1、未确认，2、BUG，3、特殊环境，4、重复BUG，5、产品定义
	private Long bugstate=1L;//状态 1、未确认，2、BUG，3、特殊环境，4、重复BUG，5、产品定义
	private Long bugaction=1L;//处理情况
	private Long rebugid;//重复的BUG号
	private Integer isdelete=0;//是否删除标记，1为删除
	private Long rootgroupid;//所属单位，冗余

	@Transient
	private List<BugFiles> bugFiles;//BUG附件内容
	@Transient
	private BugModifyInfos bugModifyInfos;//BUG处理信息
	
	@Transient
	private Date startdate;//查询用的，开始时间
	@Transient
	private Date enddate;//查询用的，结束时间
	@Transient
    private String reason;//查询用的，模糊查询
	

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
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
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
	
	public List<BugFiles> getBugFiles() {
		return bugFiles;
	}
	public void setBugFiles(List<BugFiles> bugFiles) {
		this.bugFiles = bugFiles;
	}
	public BugModifyInfos getBugModifyInfos() {
		return bugModifyInfos;
	}
	public void setBugModifyInfos(BugModifyInfos bugModifyInfos) {
		this.bugModifyInfos = bugModifyInfos;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getRootgroupid() {
		return rootgroupid;
	}
	public void setRootgroupid(Long rootgroupid) {
		this.rootgroupid = rootgroupid;
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
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getBugstate() {
		return bugstate;
	}
	public void setBugstate(Long bugstate) {
		this.bugstate = bugstate;
	}
	public Long getBugaction() {
		return bugaction;
	}
	public void setBugaction(Long bugaction) {
		this.bugaction = bugaction;
	}
}