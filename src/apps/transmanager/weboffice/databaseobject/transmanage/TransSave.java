package apps.transmanager.weboffice.databaseobject.transmanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * 事务草稿表 （时间关系暂没有对字段进行整理）
 * 2012-10-24
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   云办公
 */
@Entity
@Table(name="transsave")
public class TransSave implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transsave_gen")
    @GenericGenerator(name = "seq_transsave_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSSAVE_ID") })
    private Long id;
    @Column(length = 100)
    private String title;//标题
    @Column(length = 60000)
    private String filepaths;//附件路径，多个用,间隔
    @Column(length = 60000)
    private String filepathnames;//附件名称，多个用,间隔

	@Column(length = 10000)
	private String webcontent;//网页内容
    
	@Column(name = "signers",length = 2000)
    private String signers;//办理者的人ID,多人用,间隔
    
    @Column(name = "issame")
    private Boolean issame=true;//是否并列办理，true为并列，false为串
    
    @Column(name = "comment",length = 1000)
    private String comment;// 送审说明（备注）
    
    @Column(name = "userID")
    private Long userID;//保存者ID
    @Column(name = "adddate")
    private Date adddate;// 保存时间
	
	@Transient
	private Boolean iswebcontent;//是否有网页内容，主要是为了节省网络流量
	@Transient
	private String signernames="";//签批人名称，用,间隔
	@Transient
	private List filedata=new ArrayList();//存放文件名用的
	@Transient
	private List modifierlist=new ArrayList();//处理人列表
	
	
	public List getModifierlist() {
		return modifierlist;
	}
	public void setModifierlist(List modifierlist) {
		this.modifierlist = modifierlist;
	}
	public List getFiledata() {
		return filedata;
	}
	public void setFiledata(List filedata) {
		this.filedata = filedata;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getWebcontent() {
		return webcontent;
	}
	public void setWebcontent(String webcontent) {
		this.webcontent = webcontent;
	}
	public String getSigners() {
		return signers;
	}
	public void setSigners(String signers) {
		this.signers = signers;
	}
	public Boolean getIssame() {
		return issame;
	}
	public void setIssame(Boolean issame) {
		this.issame = issame;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public Boolean getIswebcontent() {
		return iswebcontent;
	}
	public void setIswebcontent(Boolean iswebcontent) {
		this.iswebcontent = iswebcontent;
	}
	public String getSignernames() {
		return signernames;
	}
	public void setSignernames(String signernames) {
		this.signernames = signernames;
	}
}
