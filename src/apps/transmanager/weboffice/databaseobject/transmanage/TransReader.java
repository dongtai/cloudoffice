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
 * 事务阅读者,相当于抄送（时间关系暂没有对字段进行整理）【暂时因业务不需要】
 * @author  孙爱华
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="transreader")
public class TransReader implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transreader_gen")
	@GenericGenerator(name = "seq_transreader_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRRANSREADER_ID") })
	private Long id;
	private Long transid;         // 交办id
	private Long readUser;        // 阅读者
	private Long senduser;                 // 送阅者
	@Column(length = 255)	
	private String title;                // 标题        冗余
    @Column(length = 10000)
    private String webcontent;//网页内容
	private boolean isRead;              // 文档是否已经阅读过
	private Date senddate;//送阅时间
	private Integer isview=0;//是否显示，相当于删除，0显示，1不显示
	@Column(length = 100)
	private String signtag;//签收标记
	private Date signtagdate;//签收时间
	private Integer isnew=0;//是否最新，0为最新，已过的就是依次往上加
	private Long islast=0L;//如果该人被多次送阅，只算最后一次，过去的依次加1，最新的为0
	private Date warndate;//最新提醒时间
	private Integer warnnum=0;//提醒次数
	
	private int state=0;//待阅为0，已阅为3
	@Column(name = "readdate")
	private Date readdate; //最近阅读阅读时间
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

	public Long getTransid() {
		return transid;
	}

	public void setTransid(Long transid) {
		this.transid = transid;
	}

	public Long getReadUser() {
		return readUser;
	}

	public void setReadUser(Long readUser) {
		this.readUser = readUser;
	}

	public Long getSenduser() {
		return senduser;
	}

	public void setSenduser(Long senduser) {
		this.senduser = senduser;
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

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public Date getSenddate() {
		return senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}

	public Integer getIsview() {
		return isview;
	}

	public void setIsview(Integer isview) {
		this.isview = isview;
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

	public Integer getIsnew() {
		return isnew;
	}

	public void setIsnew(Integer isnew) {
		this.isnew = isnew;
	}

	public Long getIslast() {
		return islast;
	}

	public void setIslast(Long islast) {
		this.islast = islast;
	}

	public Date getWarndate() {
		return warndate;
	}

	public void setWarndate(Date warndate) {
		this.warndate = warndate;
	}

	public Integer getWarnnum() {
		return warnnum;
	}

	public void setWarnnum(Integer warnnum) {
		this.warnnum = warnnum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
