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
 * 事务管理附件表（时间关系暂没有对字段进行整理）
 * <p>
 * <p>
 * EIO版本:        CLOUDOFFICE 1.0
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
@Table(name="transfiles")
public class TransFiles implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transfiles_gen")
    @GenericGenerator(name = "seq_transfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSFILES_ID") })
    private Long id;//主键
    
    @Column(name = "transid")
    private Long transid;//交办编号
    
	@Column(name = "userID")
    private Long userID;//上传用户ID
    
    @Column(name = "adddate")
    private Date adddate=new Date();// 上传时间

    @Column(name = "status")
    private int status=0;// 文件状态，0 = 空闲，1 = 打开中
    
    @Column(name = "openuser")
    private Long openuser;//当前打开用户ID
    
    @Column(name = "comment", length = 1000)
    private String comment;// 附件说明
    
    @Column(name = "documentpath", length = 6000)
    private String documentpath;//附件路径
    @Column(length = 1000)
    private String fileName;//文件名称
    @Column(name = "oldpath",length = 6000)
    private String oldpath;//原始版本号
    private Long isnew=0L;//是否最新的签批,0为最新的（为了查询方便），过期的依次往上加
    
    
    
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getOpenuser() {
		return openuser;
	}
	public void setOpenuser(Long openuser) {
		this.openuser = openuser;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public Long getIsnew() {
		return isnew;
	}
	public void setIsnew(Long isnew) {
		this.isnew = isnew;
	}
    


}
