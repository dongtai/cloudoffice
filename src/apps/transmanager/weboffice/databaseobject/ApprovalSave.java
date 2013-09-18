package apps.transmanager.weboffice.databaseobject;

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
 * 保存送签数据
 * 2012-9-7
 * <p>
 * <p>
 * @author  孙爱华
 * @version 3.0
 * @see     
 * @since   云办公
 */
@Entity
@Table(name="approvalsave")
public class ApprovalSave implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalsafe_gen")
    @GenericGenerator(name = "seq_approvalsafe_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALSAVE_ID") })
    private Long id;
    @Column(length = 50)
    private String title;//标题
    @Column(length = 10000)
    private String filepaths;//附件路径，多个用,间隔
    @Column(length = 10000)
    private String filepathnames;//附件名称，多个用,间隔

	@Column(length = 10000)
	private String webcontent;//网页内容
    @Column(name = "modifytype")
    private Integer modifytype;//处理方式，0表示文档协作，1表示签阅
    @Column(name = "coopers",length = 2000)
    private String coopers;//文档协作者，多人之间用,间隔
    @Column(name = "sendsigntag")
    private Boolean sendsigntag;//送签是否选中，0不选中，1表示选中
    @Column(name = "sendreadtag")
    private Boolean sendreadtag;//送阅是否选中，0不选中，1表示选中

	@Column(name = "signers",length = 2000)
    private String signers;//签批者的人ID,多人用,间隔
    
    @Column(name = "issame")
    private Boolean issame;//会签是否选中，0不选中，1表示选中
    
    @Column(name = "isreturn")
    private Boolean isreturn;//返还送文人是否选中，0不选中，1表示选中
    
    @Column(name = "sendreaders",length = 2000)
	private String sendreaders;//送阅人，多人用,间隔
	
    private String filetype;//送审的文档类别   或者是收文方式 政务网 扫描
    private Long fileflowid;//文件流水号，自动在最大号上加1，用户可以输入
    private Date filesuccdate;//成文日期
    private String fromunit;//来文单位
	private String filecode;//文号
    private String filescript;//文件备注
    
	@Column(name = "backsigners",length = 2000)
    private String backsigners;//会签后的处理人，多人用,间隔，节点间用;间隔

	@Column(name = "comment",length = 1000)
    private String comment;// 送审说明（备注）
    
    @Column(name = "userID")
    private Long userID;//保存者ID
    @Column(name = "date")
    private Date date;// 保存时间
	//以上是共有表单字段
    //还有其他表单
    private Long pre1;//
	private Long pre2;
	private Long pre3;
	private Long pre4;

	@Column(length = 10000)
	private String prename1;//
	@Column(length = 1000)
	private String prename2;//
	@Column(length = 100)
	private String prename3;//
	@Column(length = 255)
	private String prename4;// 
	
	@Transient
	private Boolean iswebcontent;//是否有网页内容，主要是为了节省网络流量
	@Transient
	private String sendreadnames="";//送阅人名称，用,间隔
	@Transient
	private String signernames="";//签批人名称，用,间隔
	@Transient
	private String coopernames="";//协作者名称，用,间隔
	@Transient
	private String backsignnames="";//会签后的处理人名，多人用,间隔，节点间用;间隔
	
	
	
	public String getBacksignnames() {
		return backsignnames;
	}
	public void setBacksignnames(String backsignnames) {
		this.backsignnames = backsignnames;
	}
    public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
    public String getFilepathnames()
	{
		return filepathnames;
	}
	public void setFilepathnames(String filepathnames)
	{
		this.filepathnames = filepathnames;
	}
	public Boolean getIswebcontent() {
		return iswebcontent;
	}
	public void setIswebcontent(Boolean iswebcontent) {
		this.iswebcontent = iswebcontent;
	}
	public String getFilepaths() {
		return filepaths;
	}
	public void setFilepaths(String filepaths) {
		this.filepaths = filepaths;
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
	public String getWebcontent() {
		return webcontent;
	}
	public void setWebcontent(String webcontent) {
		this.webcontent = webcontent;
	}

	public String getCoopers() {
		return coopers;
	}
	public void setCoopers(String coopers) {
		this.coopers = coopers;
	}

	public String getSigners() {
		return signers;
	}
	public void setSigners(String signers) {
		this.signers = signers;
	}
	public String getSendreaders() {
		return sendreaders;
	}
	public void setSendreaders(String sendreaders) {
		this.sendreaders = sendreaders;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getPre1() {
		return pre1;
	}
	public void setPre1(Long pre1) {
		this.pre1 = pre1;
	}
	public Long getPre2() {
		return pre2;
	}
	public void setPre2(Long pre2) {
		this.pre2 = pre2;
	}
	public Long getPre3() {
		return pre3;
	}
	public void setPre3(Long pre3) {
		this.pre3 = pre3;
	}
	public Long getPre4() {
		return pre4;
	}
	public void setPre4(Long pre4) {
		this.pre4 = pre4;
	}
	public String getPrename1() {
		return prename1;
	}
	public void setPrename1(String prename1) {
		this.prename1 = prename1;
	}
	public String getPrename2() {
		return prename2;
	}
	public void setPrename2(String prename2) {
		this.prename2 = prename2;
	}
	public String getPrename3() {
		return prename3;
	}
	public void setPrename3(String prename3) {
		this.prename3 = prename3;
	}
	public String getPrename4() {
		return prename4;
	}
	public void setPrename4(String prename4) {
		this.prename4 = prename4;
	}

    public Integer getModifytype() {
		return modifytype;
	}
	public void setModifytype(Integer modifytype) {
		this.modifytype = modifytype;
	}
	public Boolean getSendsigntag() {
		return sendsigntag;
	}
	public void setSendsigntag(Boolean sendsigntag) {
		this.sendsigntag = sendsigntag;
	}
	public Boolean getSendreadtag() {
		return sendreadtag;
	}
	public void setSendreadtag(Boolean sendreadtag) {
		this.sendreadtag = sendreadtag;
	}
	public Boolean getIssame() {
		return issame;
	}
	public void setIssame(Boolean issame) {
		this.issame = issame;
	}
	public Boolean getIsreturn() {
		return isreturn;
	}
	public void setIsreturn(Boolean isreturn) {
		this.isreturn = isreturn;
	}
	
    public String getSendreadnames() {
		return sendreadnames;
	}
	public void setSendreadnames(String sendreadnames) {
		this.sendreadnames = sendreadnames;
	}
	public String getSignernames() {
		return signernames;
	}
	public void setSignernames(String signernames) {
		this.signernames = signernames;
	}
	public String getCoopernames() {
		return coopernames;
	}
	public void setCoopernames(String coopernames) {
		this.coopernames = coopernames;
	}
    public String getBacksigners() {
		return backsigners;
	}
	public void setBacksigners(String backsigners) {
		this.backsigners = backsigners;
	}

	public Long getFileflowid() {
		return fileflowid;
	}
	public void setFileflowid(Long fileflowid) {
		this.fileflowid = fileflowid;
	}
	public Date getFilesuccdate() {
		return filesuccdate;
	}
	public void setFilesuccdate(Date filesuccdate) {
		this.filesuccdate = filesuccdate;
	}
	public String getFromunit() {
		return fromunit;
	}
	public void setFromunit(String fromunit) {
		this.fromunit = fromunit;
	}
	public String getFilecode() {
		return filecode;
	}
	public void setFilecode(String filecode) {
		this.filecode = filecode;
	}
	public String getFilescript() {
		return filescript;
	}
	public void setFilescript(String filescript) {
		this.filescript = filescript;
	}
}
