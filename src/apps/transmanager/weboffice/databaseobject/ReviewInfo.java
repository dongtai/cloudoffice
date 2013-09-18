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
 * @author 李孟生
 */
@Entity
@Table(name="reviewinfo")
public class ReviewInfo implements SerializableAdapter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "seq_reviewinfo_gen")
	@GenericGenerator(name = "seq_reviewinfo_gen",strategy = "native",parameters = {@Parameter(name = "sequence", value = "SEQ_REVIEWINFO_ID")})
	private Long id;
	//审阅文档路径
	@Column(length = 5000)
	private String documentPath;
	//审阅文档原路径
	@Column(length = 5000)
	private String oldPath;
	public String getOldPath() {
		return oldPath;
	}
	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
	//审阅文档名称
	@Column(length = 3000)
	private String fileName;
	//送审用户
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sender;	
	//送审时间
	private Date sendDate;
	//备注
	@Column(length = 1000)
	private String comment;
	//审结时间
	private Date reviewDate;
	//未审的文档数目
	private Integer count;
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public Users getSender() {
		return sender;
	}
	public void setSender(Users sender) {
		this.sender = sender;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
