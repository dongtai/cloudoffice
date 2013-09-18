package apps.transmanager.weboffice.databaseobject.meetmanage;

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
 * 此表是为会议处理人表（时间关系暂没有对字段进行整理）
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 最新版本
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="meetsameinfo")
public class MeetSameInfo implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_meetsameinfo_gen")
	@GenericGenerator(name = "seq_meetsameinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MEETSAMEINFO_ID") })
	private Long id;//主键
	@Column(name = "meetid")
    private Long meetid; //会议编号
	private String meetname; //会议名称，冗余

	private Long taskid;//历史编号

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users meeter;//与会者，不会变的，系统内的才会有
	private Long senduser;//发起人
	private Date senddate;//发起日期

	@Column(name="state") 
	private Long state=ApproveConstants.MEET_STATUS_START;//状态,记录当前人的状态
	
	@Column(name="actionid") 
	private Long actionid;//动作,0为未处理，暂时用常量代替 1//参加 2//替会 需要填写替会人员3//不参加

	private Long replaceid;//替换人员id
	@Column(length = 100)
	private String replaceman;//替换人员
	@Column(length = 100)
	private String replaceunit;//替换人员单位
	@Column(length = 20)
	private String replacemobile;//替换人员手机
	
	@Column(length = 100)
	private String meetmanname;//与会人员名称
	@Column(length = 255)
	private String meetmanunit;//与会人员单位
	@Column(length = 20)
	private String mobilenum;//与会人员手机号
	@Column(length = 255)
	private String otherinfo;//与会人员其他信息
	private Integer mantype=0;//人员类型，0为系统内的，1为其他人员
	
    @Column(name = "comment",length = 1000)
    private String comment;//回执说明
    
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    private Long isview=0L;//是否显示
    
    private Long submiter;//提交者
    private Date submitdate;//提交时间
    @Column(length = 6)
	private String signtag;//签收标记,已签为Y
	private Date signtagdate;//签收时间
	private Integer warntype;//提醒方式，0为系统内提醒，1为手机短信
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数
	
	
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public Long getReplaceid() {
		return replaceid;
	}
	public void setReplaceid(Long replaceid) {
		this.replaceid = replaceid;
	}
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
	public Long getSenduser() {
		return senduser;
	}
	public void setSenduser(Long senduser) {
		this.senduser = senduser;
	}
	public Long getTaskid() {
		return taskid;
	}
	public void setTaskid(Long taskid) {
		this.taskid = taskid;
	}
	public Integer getWarntype() {
		return warntype;
	}
	public void setWarntype(Integer warntype) {
		this.warntype = warntype;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getReplaceunit() {
		return replaceunit;
	}
	public void setReplaceunit(String replaceunit) {
		this.replaceunit = replaceunit;
	}
	public String getReplacemobile() {
		return replacemobile;
	}
	public void setReplacemobile(String replacemobile) {
		this.replacemobile = replacemobile;
	}
	public String getReplaceman() {
		return replaceman;
	}
	public void setReplaceman(String replaceman) {
		this.replaceman = replaceman;
	}
	public String getMeetmanname() {
		return meetmanname;
	}
	public void setMeetmanname(String meetmanname) {
		this.meetmanname = meetmanname;
	}
	public String getMeetmanunit() {
		return meetmanunit;
	}
	public void setMeetmanunit(String meetmanunit) {
		this.meetmanunit = meetmanunit;
	}
	public String getMobilenum() {
		return mobilenum;
	}
	public void setMobilenum(String mobilenum) {
		this.mobilenum = mobilenum;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getMantype() {
		return mantype;
	}
	public void setMantype(Integer mantype) {
		this.mantype = mantype;
	}

	public Users getMeeter() {
		return meeter;
	}
	public void setMeeter(Users meeter) {
		this.meeter = meeter;
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
	public Long getMeetid() {
		return meetid;
	}
	public void setMeetid(Long meetid) {
		this.meetid = meetid;
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
	public Long getIsview() {
		return isview;
	}
	public void setIsview(Long isview) {
		this.isview = isview;
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
	public Integer getWarnnum() {
		return warnnum;
	}
	public void setWarnnum(Integer warnnum) {
		this.warnnum = warnnum;
	}
	
}
