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

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;
/**
 * 签批和送阅放一起
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 最新版本
 * @see     
 * @since   南方销售负责
 */
@Entity
@Table(name="approvalsignread")
public class ApprovalSignRead implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalsignread_gen")
	@GenericGenerator(name = "seq_approvalsignread_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALSIGNREAD_ID") })
	private Long id;//主键
	//<th>序号</th><th>类别</th><th>传阅人</th><th>送阅人</th><th>送阅时间</th><th>状态</th><th>处理时间</th><th>处理意见</th><th>附件</th>
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sender;//送文者
	private Date sendtime;//送文时间
	// 送审说明
    @Column(name = "sendcomment",length = 1000)
    private String sendcomment;//送审备注
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users signer;//签批者，不会变的
	private Date signtime;//签批（处理）时间
	@Column(name = "signcomment",length = 1000)
    private String signcomment;//签批（处理）备注
	@Column(name = "modifytype")
    private Integer modifytype=1;//处理方式，0表示文档协作，1表示签批,2表示送阅
	@Column(name="state") 
	private Integer state=ApproveConstants.NEW_STATUS_WAIT;//状态,记录当前人的状态，未变化，NEW_STATUS_READ
	@Column(name="actionid") 
	private Long actionid;//动作
	@Column(name = "approvalid")
    private Long approvalid; //流程编号
	private String filename;//文件名称
	@Column(length = 500)
	private String fileversion;//版本路径
	@Column(length = 6)
	private String signtag;//签收标记,已签为Y
	private Date signtagdate;//签收时间
	private Long sameid;//签批信息编号
	private Long readid;//批阅信息编号


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getSender() {
		return sender;
	}

	public void setSender(Users sender) {
		this.sender = sender;
	}

	public Date getSendtime() {
		return sendtime;
	}

	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}

	public String getSendcomment() {
		return sendcomment;
	}

	public void setSendcomment(String sendcomment) {
		this.sendcomment = sendcomment;
	}

	public Users getSigner() {
		return signer;
	}

	public void setSigner(Users signer) {
		this.signer = signer;
	}

	public Date getSigntime() {
		return signtime;
	}

	public void setSigntime(Date signtime) {
		this.signtime = signtime;
	}

	public String getSigncomment() {
		return signcomment;
	}

	public void setSigncomment(String signcomment) {
		this.signcomment = signcomment;
	}

	public Integer getModifytype() {
		return modifytype;
	}

	public void setModifytype(Integer modifytype) {
		this.modifytype = modifytype;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getActionid() {
		return actionid;
	}

	public void setActionid(Long actionid) {
		this.actionid = actionid;
	}

	public Long getApprovalid() {
		return approvalid;
	}

	public void setApprovalid(Long approvalid) {
		this.approvalid = approvalid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileversion() {
		return fileversion;
	}

	public void setFileversion(String fileversion) {
		this.fileversion = fileversion;
	}

	public String getSigntag() {
		return signtag;
	}

	public void setSigntag(String signtag) {
		this.signtag = signtag;
	}

	public Date getSigntagdate() {
		return signtagdate;
	}

	public void setSigntagdate(Date signtagdate) {
		this.signtagdate = signtagdate;
	}

	public Long getSameid() {
		return sameid;
	}

	public void setSameid(Long sameid) {
		this.sameid = sameid;
	}

	public Long getReadid() {
		return readid;
	}

	public void setReadid(Long readid) {
		this.readid = readid;
	}
}
