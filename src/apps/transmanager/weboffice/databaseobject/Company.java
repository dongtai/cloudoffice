package apps.transmanager.weboffice.databaseobject;

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
 * 公司信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="company")
public class Company implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_company_gen")
	@GenericGenerator(name = "seq_company_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_COMPANY_ID") })
	private Long id;	
	@Column(unique=true, length = 100)
	private String name;               // 公司名
	@Column(name="code_", unique=true, length = 15)
	private String code;                 // 公司在系统中的编码
	@Column(length = 1000)
	private String description;        // 描述
	@Column(length = 255)
	private String spaceUID;           // 公司空间的根目录名：建立公司账户时候的名字+系统时间，即是name_systemtime，以后用户自定义组修改name后，该名字始终不变。
	@Column(length = 255)
	private String address;
	private Integer maxUsers;          // 最大允许用户数，为空表示用户数不受限制
	private Long ud;                   // 允许使用的最后结束时间，在用户自己注册的时候，表示注册验证最后时间，验证通过后表示允许的最后使用时间
	private Long bd;                   // 公司账户的激活时间，在系统管理员分配的时候，该时间即是为帐号开通时候的时间
	private Long fd;                   // 公司账户的注册时间，在系统管理员分配的时候，该时间即是为帐号开通时候的时间
	@Column(length = 100)
	private String userName;           // 公司联系人名，可同用户表的人不同
	@Column(length = 100)
	private String userMail;           // 公司联系人email
	@Column(length = 100)
	private String userMobile;         // 公司联系人手机，多个手机用";"分开
	@Column(length = 100)
	private String userFax;            // 公司联系人传真，多个传真用";"分开
	@Column(length = 100)
	private String userPhone;          // 公司联系人座机，多个座机用";"分开
	@Column(length = 10)
	private String verifyCode;         // 用户自己注册时候的验证吗。在验证通过后，该值为null。
	
	public Company()
	{		
	}
	
	public Company(String name, String description)
	{
		this.name = name;
		this.description = description;
	}
	
	public Company(String name, String description, String add, int maxCount)
	{
		this.name = name;
		this.description = description;
		maxUsers = maxCount;
		this.address = add;
	}
	
	public Company(String name, String description, int maxCount, long date)
	{
		this.name = name;
		this.description = description;
		maxUsers = maxCount;
		ud = date;
	}
	
	// 设置公司联系人相关信息
	public void setUserInfo(String name, String mail, String mobile, String fax, String phone)
	{
		this.userName = name;
		this.userMail = mail;
		this.userMobile = mobile;
		this.userFax = fax;
		this.userPhone = phone;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getSpaceUID()
	{
		return spaceUID;
	}	
	public void setSpaceUID(String sp)
	{
		spaceUID = sp;
	}
	
	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public Integer getMaxUsers()
	{
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers)
	{
		this.maxUsers = maxUsers;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserMail()
	{
		return userMail;
	}

	public void setUserMail(String userMail)
	{
		this.userMail = userMail;
	}

	public String getUserMobile()
	{
		return userMobile;
	}

	public void setUserMobile(String userMobile)
	{
		this.userMobile = userMobile;
	}

	public String getUserFax()
	{
		return userFax;
	}

	public void setUserFax(String userFax)
	{
		this.userFax = userFax;
	}

	public String getUserPhone()
	{
		return userPhone;
	}

	public void setUserPhone(String userPhone)
	{
		this.userPhone = userPhone;
	}

	public Long getUd()
	{
		return ud;
	}

	public void setUd(Long ud)
	{
		this.ud = ud;
	}
	

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}
	

	public String getVerifyCode()
	{
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode)
	{
		this.verifyCode = verifyCode;
	}
	
	public Long getBd()
	{
		return bd;
	}

	public void setBd(Long bd)
	{
		this.bd = bd;
	}

	public Long getFd()
	{
		return fd;
	}

	public void setFd(Long fd)
	{
		this.fd = fd;
	}
	
	public void setBeginTime(long fd, long bd)
	{
		this.fd = fd;
		this.bd = bd;
	}

	public void update(Company t)
	{
		name = t.name;
		description = t.description;
		ud = t.ud;
		address = t.address;
		maxUsers = t.maxUsers;
		userName = t.userName;
		userMail = t.userMail;
		userMobile = t.userMobile;
		userFax = t.userFax;
		userPhone = t.userPhone;
	}
	
}
