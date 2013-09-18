package apps.transmanager.weboffice.databaseobject.meetmanage;

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
 * 会议草稿表（时间关系暂没有对字段进行整理）
 * 2012-10-24
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   云办公
 */
@Entity
@Table(name="meetsave")
public class MeetSave implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_meetsave_gen")
    @GenericGenerator(name = "seq_meetsave_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MEETSAVE_ID") })
    private Long id;

    @Column(length = 100)
    private String meetname;// 会议名称
    @Column(length = 100)
    private String meetdate;//会议日期
    @Column(length = 100)
    private String meettime;//会议时间
    private Date meetdetailtime;//日期和时间合起来的
    @Column(length = 255)
    private String meetaddress;//会议地址
    @Column(length = 100)
    private String mastername;//会议召集人，主持人

	@Column(length = 10000)
	private String meetcontent;//会议议程
    @Column(name = "comment",length = 1000)
    private String comment;// 会议备注
    
    @Column(length = 60000)
    private String filepaths;//附件路径，多个用,间隔
    @Column(length = 60000)
    private String filepathnames;//附件名称，多个用,间隔
    
    @Column(length = 10000)
    private String meetpersonids;//与会人会编号，多个用,间隔
    @Column(length = 10000)
    private String meetpersonnames;//与会人名称，多个用,间隔
    @Column(length = 10000)
    private String meetmanname;//与会人员名称，多个用,间隔
    @Column(length = 10000)
	private String meetmanunit;//与会人员单位，多个用,间隔
    @Column(length = 10000)
	private String mobilenum;//与会人员手机号，多个用,间隔
    @Column(length = 10000)
	private String otherinfo;//其他信息
    private Date submitdate;//提交时间
	private Long submiter;//提交人
	
	@Transient
	private String meetosnames="";//与会人名称，系统中人的人，用,间隔

	@Transient
	private String[][] meetmannames;//与会人名称，系统中人的人，用,间隔
	@Transient
	private String[][] othermannames;//与会人名称，系统中人的人，用,间隔
	@Transient
	private String[][] filePaths;//与会人名称，系统中人的人，用,间隔
	
	
	
	public String[][] getMeetmannames() {
		return meetmannames;
	}

	public void setMeetmannames(String[][] meetmannames) {
		this.meetmannames = meetmannames;
	}

	public String[][] getOthermannames() {
		return othermannames;
	}

	public void setOthermannames(String[][] othermannames) {
		this.othermannames = othermannames;
	}

	public String[][] getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String[][] filePaths) {
		this.filePaths = filePaths;
	}

	public Date getSubmitdate() {
		return submitdate;
	}

	public void setSubmitdate(Date submitdate) {
		this.submitdate = submitdate;
	}

	public Long getSubmiter() {
		return submiter;
	}

	public void setSubmiter(Long submiter) {
		this.submiter = submiter;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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


	public String getMastername() {
		return mastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	public String getMeetcontent() {
		return meetcontent;
	}

	public void setMeetcontent(String meetcontent) {
		this.meetcontent = meetcontent;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFilepaths() {
		return filepaths;
	}

	public void setFilepaths(String filepaths) {
		this.filepaths = filepaths;
	}

	public String getFilepathnames() {
		return filepathnames;
	}

	public void setFilepathnames(String filepathnames) {
		this.filepathnames = filepathnames;
	}

	public String getMeetpersonids() {
		return meetpersonids;
	}

	public void setMeetpersonids(String meetpersonids) {
		this.meetpersonids = meetpersonids;
	}

	public String getMeetpersonnames() {
		return meetpersonnames;
	}

	public void setMeetpersonnames(String meetpersonnames) {
		this.meetpersonnames = meetpersonnames;
	}

	public String getOtherinfo() {
		return otherinfo;
	}

	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
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

	public String getMeetosnames() {
		return meetosnames;
	}

	public void setMeetosnames(String meetosnames) {
		this.meetosnames = meetosnames;
	}
	
	
	
 
}
