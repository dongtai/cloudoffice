package apps.transmanager.weboffice.domain;

import java.util.ArrayList;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Users;

/**
 * UserinfoView entity.
 * 
 * @author MyEclipse Persistence Tools
 */

//@Entity
//@Table(name="users")
public class AdminUserinfoView implements SerializableAdapter
{
	// Fields
	//@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_gen")
	//@GenericGenerator(name = "seq_users_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_ID") })
	private Long id;
	private String userName;
	private String realName;
	private String realEmail;
	private Float storageSize;
	private String duty;
	private Short role;
	private Short validate = 1;
	private Integer sortNum = 10000000;
	private String caId;
	//@OneToMany(fetch = FetchType.LAZY)
	//@JoinTable(name="usersorganizations", joinColumns = @JoinColumn(name="id"), inverseJoinColumns = @JoinColumn(name="organization_id"))
	//@Transient
	private List<Organizations> groupinfo = new ArrayList<Organizations>();
	//@OneToMany(fetch = FetchType.LAZY)
	//@JoinTable(name="usersroles", joinColumns = @JoinColumn(name="id"), inverseJoinColumns = @JoinColumn(name="role_id"))
	private List<Roles> roles = new ArrayList<Roles>();      // 用户可以在多个角色中。

	
	private String image;
	public AdminUserinfoView()
	{
	}

	public AdminUserinfoView(Users u)
	{
		this.id = u.getId();
		this.image = u.getImage();
		this.userName = u.getUserName();
		this.realName = u.getRealName();
		this.realEmail = u.getRealEmail();
		this.storageSize = u.getStorageSize();
		this.duty = u.getDuty();
		this.role = u.getRole();
		this.validate = u.getValidate();
		this.sortNum = u.getSortNum();
		this.caId = u.getCaId();
	}
	
	public AdminUserinfoView(Long userId, String userName, String realName,
			String email, Float storageSize, String duty, Short role,
			Short validate,	Integer sortNum, String caId)
	{
		this.id = userId;
		this.userName = userName;
		this.realName = realName;
		this.realEmail = email;
		this.storageSize = storageSize;
		this.duty = duty;
		this.role = role;
		this.validate = validate;
		this.sortNum = sortNum;
		this.caId = caId;
		this.roles = new ArrayList<Roles>();
	}
	
	public List<Organizations> getOrganization()
	{
		return groupinfo;
	}
	public void setOrganization(List<Organizations> org)
	{
		groupinfo = org;
	}
	public List<Roles> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Roles> roles)
	{
		this.roles = roles;
	}

	public String getCaId()
	{
		return caId;
	}

	public void setCaId(String caId)
	{
		this.caId = caId;
	}

	public Integer getSortNum()
	{
		return sortNum;
	}

	public void setSortNum(Integer sortNum)
	{
		this.sortNum = sortNum;
	}

	// Constructors

	public String getRealName()
	{
		return realName;
	}

	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	public Organizations getGroupinfo()
	{
		return groupinfo.size() <= 0 ? null : groupinfo.get(0);
	}

	public void setGroupinfo(Organizations groupinfo)
	{
		this.groupinfo.add(groupinfo);
	}

	/** full constructor */
	public AdminUserinfoView(Long userId, String userName, String email,
			Float storageSize, String duty, Short role)
	{
		this.id = userId;
		this.userName = userName;
		this.realEmail = email;
		this.storageSize = storageSize;
		this.duty = duty;
		this.role = role;
	}

	// Property accessors

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AdminUserinfoView))
			return false;
		AdminUserinfoView castOther = (AdminUserinfoView) other;

		return ((this.getUserId() == castOther.getUserId()) || (this
				.getUserId() != null
				&& castOther.getUserId() != null && this.getUserId().equals(
				castOther.getUserId())))
				&& ((this.getUserName() == castOther.getUserName()) || (this
						.getUserName() != null
						&& castOther.getUserName() != null && this
						.getUserName().equals(castOther.getUserName())))
				&& ((this.getEmail() == castOther.getEmail()) || (this
						.getEmail() != null
						&& castOther.getEmail() != null && this.getEmail()
						.equals(castOther.getEmail())))
				&& ((this.getDuty() == castOther.getDuty()) || (this.getDuty() != null
						&& castOther.getDuty() != null && this.getDuty()
						.equals(castOther.getDuty())))
				&& ((this.getRole() == castOther.getRole()) || (this.getRole() != null
						&& castOther.getRole() != null && this.getRole()
						.equals(castOther.getRole())))
				&& ((this.getStorageSize() == castOther.getStorageSize()) || (this
						.getStorageSize() != null
						&& castOther.getStorageSize() != null && this
						.getStorageSize().equals(castOther.getStorageSize())));
	}

	public int hashCode()
	{
		int result = 17;

		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result
				+ (getUserName() == null ? 0 : this.getUserName().hashCode());
		result = 37 * result
				+ (getEmail() == null ? 0 : this.getEmail().hashCode());
		result = 37
				* result
				+ (getStorageSize() == null ? 0 : this.getStorageSize()
						.hashCode());
		result = 37 * result
				+ (getRole() == null ? 0 : this.getRole().hashCode());
		result = 37 * result
				+ (getDuty() == null ? 0 : this.getDuty().hashCode());
		return result;
	}

	public Long getUserId()
	{
		return id;
	}

	public void setUserId(Long userId)
	{
		this.id = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public Short getRole()
	{
		return this.role;
	}

	public void setRole(Short role)
	{
		this.role = role;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getEmail()
	{
		return realEmail;
	}

	public void setEmail(String email)
	{
		this.realEmail = email;
	}

	public Float getStorageSize()
	{
		return storageSize;
	}

	public void setStorageSize(Float storageSize)
	{
		this.storageSize = storageSize;
	}

	public String getDuty()
	{
		return duty;
	}

	public void setDuty(String duty)
	{
		this.duty = duty;
	}

	public Short getValidate()
	{
		return validate;
	}

	public void setValidate(Short validate)
	{
		this.validate = validate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}