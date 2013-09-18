package apps.transmanager.weboffice.databaseobject.meetmanage;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 会议处理记录表（时间关系暂没有对字段进行整理）
 * <p>
 * <p>
 * CLOUD版本:      CLOUDOFFICE v1.0
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
@Table(name="meettask")
public class MeetTask  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_meettask_gen")
    @GenericGenerator(name = "seq_meettask_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MEETTASK_ID") })
    private Long id;
    
    private Long sameid;//会议人号
    private Long meetid;//会议号
    
    @Column(name="state") 
	private Long state=ApproveConstants.MEET_STATUS_START;//状态,记录当前人的状态
	
	@Column(name="actionid") 
	private Long actionid;//动作,0未处理暂时用常量代替1//参加2//替会，需要填写替会人员3//不参加
	
	@Column(name="meetname", length = 100) 
	private String meetname;//会议名称,冗余

	@Column(name = "meetmannames",length = 1000)
	private String meetmannames;//与会人员名称
	@Column(name = "othermannames",length = 1000)
	private String othermannames;//其他与会人员

	@Column(length = 100)
	private String replaceman;//替换人员
	@Column(length = 255)
	private String replaceunit;//替换人员单位
	@Column(length = 20)
	private String replacemobile;//替换人员手机
	
	private Integer mantype=0;//人员类型，0为系统内的，1为其他人员
	
    @Column(name = "comment",length = 1000)
    private String comment;//回执说明
    
    private Long submiter;//提交者
    private Date submitdate;//提交时间
    
	
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public String getMeetmannames() {
		return meetmannames;
	}
	public void setMeetmannames(String meetmannames) {
		this.meetmannames = meetmannames;
	}
	public String getOthermannames() {
		return othermannames;
	}
	public void setOthermannames(String othermannames) {
		this.othermannames = othermannames;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSameid() {
		return sameid;
	}
	public void setSameid(Long sameid) {
		this.sameid = sameid;
	}
	public Long getMeetid() {
		return meetid;
	}
	public void setMeetid(Long meetid) {
		this.meetid = meetid;
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
	public String getReplaceman() {
		return replaceman;
	}
	public void setReplaceman(String replaceman) {
		this.replaceman = replaceman;
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
	public Integer getMantype() {
		return mantype;
	}
	public void setMantype(Integer mantype) {
		this.mantype = mantype;
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
}
