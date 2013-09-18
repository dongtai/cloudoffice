package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.service.config.WebConfig;

@Entity
@Table(name="ctmgroupmember_tb")
public class CtmGroupMemberPo implements SerializableAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = -662658573210346859L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_ctmGroupMember_gen")
	@GenericGenerator(name = "seq_ctmGroupMember_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CTMGROUPMEMBER_ID") })
	private Long id;
	private Long groupId;
	private Long userId;
	@Column(length = 100)
	private String userName;
	
	/**
	 * 组员的昵称
	 */
	@Column(length = 100)
	private String nickName;
	private Date createTime;
	private Long ownerId;
	@Column(length = 100)
	private String image;
	
	/**
	 * 邮箱地址
	 */
	private String mail;
	
	@Column(length = 100)
	private String realName;	// 用户显示名
	@Column(length = 100)
	private String duty;              // 输入的职务
	
	@Column(length = 1000)
	private String mobile;    // 手机，多个手机用空格分开
	@Column(length = 100)
	private String companyName; // 公司
	@Column(length = 100)
	private String fax; // 传真，多个传真用空格分开
	@Column(length = 100)
	private String phone; // 座机，多个座机用空格分开
	@Column(length = 20)
	private String postcode; // 邮编
	@Column(length = 255)
	private String address; // 通讯地址
	@Column(length = 255)
	private String companyAddress;  // 公司地址
	private Boolean man=true;     // 性别，true为男，false为女。
	private Date birthday;   // 生日.
	@Column(length = 100)
	private String department;    // 部门
	@Column(length = 1000)
	private String comment;    // 备注、介绍说明
	
	public String getRealName() {
		if (realName==null){realName=userName;}
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompanyAddress() {
		return companyAddress;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	public Boolean getMan() {
		if(man == null ||man.toString().trim().equals("null") ||man.toString().trim().equals("true")){
			return true;
		}
		return man;
	}
	public void setMan(Boolean man) {
		this.man = man;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getImage() {
		return image == null ? WebConfig.userPortrait + "image.jpg" : image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
