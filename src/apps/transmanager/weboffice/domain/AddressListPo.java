package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="addresslist")
public class AddressListPo implements SerializableAdapter{

	private static final long serialVersionUID = -3404728189850704603L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_addresslist_gen")
	@GenericGenerator(name = "seq_addresslist_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ADDRESSLIST_ID") })
	private Long id;	//真实ID
	private Long groupId;	//所在组id
	private Long ownerId;	//拥有者id
	@ManyToOne
	@JoinColumn(name="userinfo_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;    //用户
	@Column(length = 100)
	private String userName;	//系统内部的id，外部联系人为空
	@Column(length = 100)
	private String realEmail;	//邮箱地址
	@Column(length = 100)
	private String realName;	// 用户显示名
	@Column(length = 100)
	private String duty;              // 输入的职务
	@Column(length = 100)
	private String image;              // 用户头像的保存缓存名
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
	private boolean man;     // 性别，true为男，false为女。
	private Date birthday;   // 生日.
	@Column(length = 100)
	private String department;    // 部门
	@Column(length = 1000)
	private String comment;    // 备注、介绍说明
	public AddressListPo() {
		super();
	}
	public AddressListPo(Long groupId, Long ownerId, Users userinfo,
			String userName, String realEmail, String realName, String duty,
			String image, String mobile, String companyName, String fax,
			String phone, String postcode, String address,
			String companyAddress, boolean man, Date birthday,
			String department, String comment) {
		super();
		this.groupId = groupId;
		this.ownerId = ownerId;
		this.userinfo = userinfo;
		this.userName = userName;
		this.realEmail = realEmail;
		this.realName = realName;
		this.duty = duty;
		this.image = image;
		this.mobile = mobile;
		this.companyName = companyName;
		this.fax = fax;
		this.phone = phone;
		this.postcode = postcode;
		this.address = address;
		this.companyAddress = companyAddress;
		this.man = man;
		this.birthday = birthday;
		this.department = department;
		this.comment = comment;
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
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Users getUserinfo() {
		return userinfo;
	}
	public void setUserinfo(Users userinfo) {
		this.userinfo = userinfo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealEmail() {
		return realEmail;
	}
	public void setRealEmail(String realEmail) {
		this.realEmail = realEmail;
	}
	public String getRealName() {
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public boolean getMan() {
		return man;
	}
	public void setMan(boolean man) {
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
}
