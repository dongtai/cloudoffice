package apps.transmanager.weboffice.databaseobject.transmanage;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 事务过程附件表（时间关系暂没有对字段进行整理），该表只记录处理过的文件，没有保存或处理的文件不记录
 * <p>
 * <p>
 * EIO版本:        最新版存放多附件
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-10-24
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:       CLOUD
 * <p>
 * <p>
 */
@Entity
@Table(name="transtaskfiles")
public class TransTaskFiles implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transtaskfiles_gen")
    @GenericGenerator(name = "seq_transtaskfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSTASKFILES_ID") })
    private Long id;//主键
    
    @Column(name = "transid")
    private Long transid;//事务编号
    
    @Column(name = "sameid")
    private Long sameid;//事务处理编号

	@Column(name = "userID")
    private Long userID;//上传文件用户ID
	
    
    @Column(name = "adddate")
    private Date adddate=new Date();// 上传文件时间

    @Column(name = "documentpath",length = 60000)
    private String documentpath;//文件路径
    @Column(length = 1000)
    private String fileName;//文件名称
    @Column(name = "oldpath",length = 60000)
    private String oldpath;//原始版本号，暂时冗余
    
    private Integer filetype=0;//文件类型，0为原来文件，1为上传
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    private Long taskid;//签批号，在提交的时候才更新，保存文档时是空的。
    
    
    
	public Long getSameid() {
		return sameid;
	}
	public void setSameid(Long sameid) {
		this.sameid = sameid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTransid() {
		return transid;
	}
	public void setTransid(Long transid) {
		this.transid = transid;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
	}
	public String getDocumentpath() {
		return documentpath;
	}
	public void setDocumentpath(String documentpath) {
		this.documentpath = documentpath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOldpath() {
		return oldpath;
	}
	public void setOldpath(String oldpath) {
		this.oldpath = oldpath;
	}
	public Integer getFiletype() {
		return filetype;
	}
	public void setFiletype(Integer filetype) {
		this.filetype = filetype;
	}
	public Long getIsnew() {
		return isnew;
	}
	public void setIsnew(Long isnew) {
		this.isnew = isnew;
	}
	public Long getTaskid() {
		return taskid;
	}
	public void setTaskid(Long taskid) {
		this.taskid = taskid;
	}
}
