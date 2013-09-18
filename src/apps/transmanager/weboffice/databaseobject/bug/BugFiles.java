package apps.transmanager.weboffice.databaseobject.bug;

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

/**
 * bug附件表
 * @author 孙爱华
 * 2013.6.14
 *
 */
@Entity
@Table(name="bugfiles")
public class BugFiles implements SerializableAdapter{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bugfiles_gen")
	@GenericGenerator(name = "seq_bugfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGFILES_ID") })
	private Long id;//编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugInfos bugInfos;//对应的BUG信息
	@Column(length = 255)
	private String fileurl;//附件地址一般存在data/bugs目录下，或ftp目录
	@Column(length = 255)
	private String filename;
	private Date adddate=new Date();//创建时间
	private Long userid;//上传者
	private Integer isdelete=0;//是否删除标记，1为删除
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BugInfos getBugInfos() {
		return bugInfos;
	}
	public void setBugInfos(BugInfos bugInfos) {
		this.bugInfos = bugInfos;
	}
	public String getFileurl() {
		return fileurl;
	}
	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Integer getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}
}
