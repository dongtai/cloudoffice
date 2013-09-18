package apps.transmanager.weboffice.databaseobject;

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
 * 用于存放回复的短信，防止有些短信ext不能匹配
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="mobilebackinfo")
public class MobileBackInfo implements SerializableAdapter
{
	//95153359,157589222222,15251664207,好的,2012-11-29 11:05:31
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mobilebackinfo_gen")
	@GenericGenerator(name = "seq_mobilebackinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MOBILEBACKINFO_ID") })
	private Long id; // 主键ID
	@Column(length = 50)
	private String mobile; // 手机号
	private Long sendid;//对应发送编号，有可能为空
	
	private Integer ext;//有可能为空，暂不发号
	
	@Column(length = 500)
	private String totalback;//用户回复的所有内容，没有解析,主要是为了验证有没有错误
	private Date adddate;//接收的时间

	private String backcontent;//手机回复的短息
	private Date backdate;//回复的时间
	
	
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
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
	public Long getSendid() {
		return sendid;
	}
	public void setSendid(Long sendid) {
		this.sendid = sendid;
	}
	public Integer getExt() {
		return ext;
	}
	public void setExt(Integer ext) {
		this.ext = ext;
	}
	public String getTotalback() {
		return totalback;
	}
	public void setTotalback(String totalback) {
		this.totalback = totalback;
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
