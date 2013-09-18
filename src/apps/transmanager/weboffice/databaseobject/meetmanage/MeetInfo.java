package apps.transmanager.weboffice.databaseobject.meetmanage;

import java.util.ArrayList;
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
 * 会议数据（列表、处理、显示等）
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="meetinfo")
public class MeetInfo implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_meetinfo_gen")
    @GenericGenerator(name = "seq_meetinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MEETINFO_ID") })
    private Long id;
    
    @Column(name = "status")
    private Long status=0l;//会议的整体状态，可能没什么用处，0为开放,4为终止，5为销毁

    @Column(name = "senduser")
    private Long senduser;// 会议发起人ID
    
    

	@Column(name = "adddate")
    private Date adddate;// 创建时间
    
    @Column(name = "isRead")
    private int isRead=0;// 是否读过,为了控制反悔和撤销
    
    @Column(length = 100)
    private String meetname;// 会议名称
    @Column(length = 100)
    private String meetdate;//会议日期
    @Column(length = 100)
    private String meettime;//会议时间
    private Date meetdetailtime;//日期和时间合起来的
    @Column(length = 255)
    private String meetaddress;//会议地址
    @Column(name = "mastername",length = 100)
    private String mastername;// 会议召开人
    

	@Column(length = 10000)
	private String meetcontent;//会议议程
    @Column(name = "comment",length = 1000)
    private String comment;// 会议备注
    private Integer isview=0;//软删除标记

	private Long submiter;//提交者
    private Date submitdate;//提交时间
    @Transient
	private String meetingtime;//会议时间
    @Transient
	private String allpersons;//与会所有人名

	@Transient
	private ArrayList filelist;//用来存放附件
    @Transient
	private ArrayList meetmannames;//用来存放与会人员
    @Transient
	private ArrayList othermannames;//用来存放其他人员

    
    public Integer getIsview() {
		return isview;
	}
	public void setIsview(Integer isview) {
		this.isview = isview;
	}
	public String getAllpersons() {
		return allpersons;
	}
	public void setAllpersons(String allpersons) {
		this.allpersons = allpersons;
	}
    public String getMeetingtime() {
		return meetingtime;
	}
	public void setMeetingtime(String meetingtime) {
		this.meetingtime = meetingtime;
	}
    public ArrayList getMeetmannames() {
		return meetmannames;
	}
	public void setMeetmannames(ArrayList meetmannames) {
		this.meetmannames = meetmannames;
	}
	public ArrayList getOthermannames() {
		return othermannames;
	}
	public void setOthermannames(ArrayList othermannames) {
		this.othermannames = othermannames;
	}
	public ArrayList getFilelist() {
		return filelist;
	}
	public void setFilelist(ArrayList filelist) {
		this.filelist = filelist;
	}
	public String getMastername() {
		return mastername;
	}
	public void setMastername(String mastername) {
		this.mastername = mastername;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSenduser() {
		return senduser;
	}
	public void setSenduser(Long senduser) {
		this.senduser = senduser;
	}
	public String getMeetcontent() {
		return meetcontent;
	}
	public void setMeetcontent(String meetcontent) {
		this.meetcontent = meetcontent;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public String getMeetdate() {
		return meetdate;
	}
	public void setMeetdate(String meetdate) {
		this.meetdate = meetdate;
	}
	public String getMeettime() {
		return meettime;
	}
	public void setMeettime(String meettime) {
		this.meettime = meettime;
	}
	public Date getMeetdetailtime() {
		return meetdetailtime;
	}
	public void setMeetdetailtime(Date meetdetailtime) {
		this.meetdetailtime = meetdetailtime;
	}
	public String getMeetaddress() {
		return meetaddress;
	}
	public void setMeetaddress(String meetaddress) {
		this.meetaddress = meetaddress;
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
