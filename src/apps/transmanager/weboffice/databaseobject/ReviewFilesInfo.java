package apps.transmanager.weboffice.databaseobject;

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

import apps.transmanager.weboffice.domain.SerializableAdapter;

/*
 * 审阅存放的具体审阅人信息，与ReviewInfo关联
 * @author 李孟生
 */
@Entity
@Table(name="reviewfilesinfo")
public class ReviewFilesInfo implements SerializableAdapter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "seq_reviewfilesinfo_gen")
	@GenericGenerator(name = "seq_reviewfilesinfo_gen",strategy = "native",parameters = {@Parameter(name = "sequence", value = "SEQ_REVIEWFILESINFO_ID")})
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ReviewInfo reviewinfo;//审阅信息
	//待审的文档
	@Column(length = 5000)
	private String documentPath;
	//审阅人
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users reviewer;
	//版本号
	private String versionsId;
	//送审时间
	private Date sendDate;
	//审结时间
	private Date reviewDate;
	//审阅权限
	private Integer permit;
	
	private Integer state=0;//状态,0为未审，1为已审
	//审阅结果
	private Integer result;
	//审阅备注
	@Column(length = 5000)
	private String reviewCommet;
	//版本
	private String version;
	//送审备注  冗余
	private String comment;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ReviewInfo getReviewinfo() {
		return reviewinfo;
	}
	public void setReviewinfo(ReviewInfo reviewinfo) {
		this.reviewinfo = reviewinfo;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public Users getReviewer() {
		return reviewer;
	}
	public void setReviewer(Users reviewer) {
		this.reviewer = reviewer;
	}
	public String getVersionsId() {
		return versionsId;
	}
	public void setVersionsId(String versionsId) {
		this.versionsId = versionsId;
	}
	public Date getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	public Integer getPermit() {
		return permit;
	}
	public void setPermit(Integer permit) {
		this.permit = permit;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public String getReviewCommet() {
		return reviewCommet;
	}
	public void setReviewCommet(String reviewCommet) {
		this.reviewCommet = reviewCommet;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	//文件名
	private String fileName;
}
