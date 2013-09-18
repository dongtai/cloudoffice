package apps.transmanager.weboffice.databaseobject.transmanage;

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
 * 事务阅读者,查看文件的记录（时间关系暂没有对字段进行整理）【暂时因业务不需要】
 * @author  孙爱华
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="transreaderfiles")
public class TransReaderFiles implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transreaderfiles_gen")
	@GenericGenerator(name = "seq_transreaderfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRRANSREADERFILES_ID") })
	private Long id;
	private Long transreaderid;   // 阅读编号，为了关联用的
	private Long readUser;        // 阅读者
	private Long transfilesid;    // 送阅者
	@Column(name = "readdate")
	private Date readdate; //阅读时间
	@Column(name = "comment",length = 1000)
	private String comment;             // 批注
	
	@Transient
	private String userName;            // 阅读者真实名字，为历史记录中显示用

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTransreaderid() {
		return transreaderid;
	}

	public void setTransreaderid(Long transreaderid) {
		this.transreaderid = transreaderid;
	}

	public Long getReadUser() {
		return readUser;
	}

	public void setReadUser(Long readUser) {
		this.readUser = readUser;
	}

	public Long getTransfilesid() {
		return transfilesid;
	}

	public void setTransfilesid(Long transfilesid) {
		this.transfilesid = transfilesid;
	}

	public Date getReaddate() {
		return readdate;
	}

	public void setReaddate(Date readdate) {
		this.readdate = readdate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
}
