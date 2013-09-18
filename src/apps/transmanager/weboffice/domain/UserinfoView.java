package apps.transmanager.weboffice.domain;

/**
 * UserinfoView entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class UserinfoView implements SerializableAdapter
{

	// Fields

	private Long userId;
	private String userName;
	private String email;
	private String spaceUID;
	private String department = "";
	private String companyName;

	private String realName;
	private String mobile;
	private String userRealEmail;
	// Constructors

	
	/** default constructor */
	public UserinfoView()
	{
	}
	
	public UserinfoView(UserinfoView view)
	{
		this.userId = view.userId;
		this.userName = view.userName;
		this.email = view.email;
		this.department = view.department;
		this.realName = view.realName;
		this.companyName = view.companyName;
		this.mobile = view.mobile;
	}

	/** full constructor */
	public UserinfoView(Long userId, String userName, String email,
			 String realName, String companyName, String spaceUID)
	{
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		//this.department = department;
		this.realName = realName;
		this.companyName = companyName;
		this.mobile = mobile;
		this.spaceUID = spaceUID;
	}

	// Property accessors

	public Long getUserId()
	{
		return this.userId;
	}

	public String getSpaceUID()
	{
		return spaceUID;
	}

	public void setSpaceUID(String spaceUID)
	{
		this.spaceUID = spaceUID;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return this.userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getDepartment()
	{
		return this.department;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getRealName()
	{
		return realName;
	}

	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UserinfoView))
			return false;
		UserinfoView castOther = (UserinfoView) other;

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
				&& ((this.getDepartment() == castOther.getDepartment()) || (this
						.getDepartment() != null
						&& castOther.getDepartment() != null && this
						.getDepartment().equals(castOther.getDepartment()))
						&& ((this.getRealName() == castOther.getRealName()) || (this
								.getRealName() != null
								&& castOther.getRealName() != null && this
								.getRealName().equals(castOther.getRealName())))
						&& ((this.getCompanyName() == castOther
								.getCompanyName()) || (this.getCompanyName() != null
								&& castOther.getCompanyName() != null && this
								.getCompanyName().equals(
										castOther.getCompanyName()))));
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
				+ (getDepartment() == null ? 0 : this.getDepartment()
						.hashCode());
		result = 37 * result
				+ (getRealName() == null ? 0 : this.getRealName().hashCode());
		result = 37
				* result
				+ (getCompanyName() == null ? 0 : this.getCompanyName()
						.hashCode());
		return result;
	}
	public String getUserRealEmail() {
		return userRealEmail;
	}

	public void setUserRealEmail(String userRealEmail) {
		this.userRealEmail = userRealEmail;
	}

}