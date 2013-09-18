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

/**
 * 用于存放手机短信
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="mobilesendinfo")
public class MobileSendInfo implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mobilesendinfo_gen")
	@GenericGenerator(name = "seq_mobilesendinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MOBILESENDINFO_ID") })
	private Long id; // 主键ID
	@Column(length = 50)
	private String mobile; // 手机号
	@Column(length = 250)
	private String content; // 手机短信内容
	private Integer ext; //扩展码，可能会重复，要实现发号，暂不发号
	private int isvalidate=0;//ext是否失效，根据senddate来自动处理，2个月前的记录直接收回
	private Date senddate;//发送时间
	private String backCode;//发送返回的编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sender;    // 发送者,有可能为空
	private Long companyid;	//公司ID，为了计费用的（发送者的公司）
	private String companyname;//公司名称，冗余，主要是防止单位删了，这里显示不出公司名字
	
	private Integer type;//事务类型，常量定义表中，详见Constant.MEETING
	private Long outid;//事务编号，与外部的业务有关系,有可能为空
	private String backcontent;//手机回复的短息,最后一个短信的内容
	private Date backdate;//回复的时间，最后一个回复的
	
	private Integer issuccess=0;//0为成功，1为失败
	//95153359,157589222222,15251664207,好的,2012-11-29 11:05:31
	
	public Integer getIssuccess() {
		if (issuccess==null)
		{
			issuccess=0;
		}
		return issuccess;
	}
	public void setIssuccess(Integer issuccess) {
		this.issuccess = issuccess;
	}
	public int getIsvalidate() {
		return isvalidate;
	}
	public void setIsvalidate(int isvalidate) {
		this.isvalidate = isvalidate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}



	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getExt() {
		return ext;
	}
	public void setExt(int ext) {
		this.ext = ext;
	}
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
	public String getBackCode() {
		return backCode;
	}
	public void setBackCode(String backCode) {
		this.backCode = backCode;
	}
	public Users getSender() {
		return sender;
	}
	public void setSender(Users sender) {
		this.sender = sender;
	}
	public Long getCompanyid() {
		return companyid;
	}
	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getOutid() {
		return outid;
	}
	public void setOutid(Long outid) {
		this.outid = outid;
	}
	public String getBackcontent() {
		return backcontent;
	}
	public void setBackcontent(String backcontent) {
		this.backcontent = backcontent;
	}
	public Date getBackdate() {
		return backdate;
	}
	public void setBackdate(Date backdate) {
		this.backdate = backdate;
	}

}
