package apps.transmanager.weboffice.domain;

/**
 * GroupmemberinfoView entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GroupmemberinfoView implements SerializableAdapter
{
	// Fields

	private Long groupmemberId;
	private Long groupId;
	private Long memberId;
	private String userName;
	private String email;
	private String realName;

	// Constructors

	/** default constructor */
	public GroupmemberinfoView()
	{
	}

	public GroupmemberinfoView(Long groupmemberId, Long groupId, Long memberId,
			String userName, String email, String realName)
	{
		this.groupmemberId = groupmemberId;
		this.groupId = groupId;
		this.memberId = memberId;
		this.userName = userName;
		this.email = email;
		this.realName = realName;
	}

	/** minimal constructor */
	public GroupmemberinfoView(Long groupmemberId, Long groupId, Long memberId,
			String email)
	{
		this.groupmemberId = groupmemberId;
		this.groupId = groupId;
		this.memberId = memberId;
		this.email = email;
	}

	/** full constructor */
	public GroupmemberinfoView(Long groupmemberId, Long groupId, Long memberId,
			String userName, String email)
	{
		this.groupmemberId = groupmemberId;
		this.groupId = groupId;
		this.memberId = memberId;
		this.userName = userName;
		this.email = email;
	}

	public String getRealName()
	{
		return realName;
	}

	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	// Property accessors

	public Long getGroupmemberId()
	{
		return this.groupmemberId;
	}

	public void setGroupmemberId(Long groupmemberId)
	{
		this.groupmemberId = groupmemberId;
	}

	public Long getGroupId()
	{
		return this.groupId;
	}

	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}

	public Long getMemberId()
	{
		return this.memberId;
	}

	public void setMemberId(Long memberId)
	{
		this.memberId = memberId;
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

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GroupmemberinfoView))
			return false;
		GroupmemberinfoView castOther = (GroupmemberinfoView) other;

		return ((this.getGroupmemberId() == castOther.getGroupmemberId()) || (this
				.getGroupmemberId() != null
				&& castOther.getGroupmemberId() != null && this
				.getGroupmemberId().equals(castOther.getGroupmemberId())))
				&& ((this.getGroupId() == castOther.getGroupId()) || (this
						.getGroupId() != null
						&& castOther.getGroupId() != null && this.getGroupId()
						.equals(castOther.getGroupId())))
				&& ((this.getMemberId() == castOther.getMemberId()) || (this
						.getMemberId() != null
						&& castOther.getMemberId() != null && this
						.getMemberId().equals(castOther.getMemberId())))
				&& ((this.getUserName() == castOther.getUserName()) || (this
						.getUserName() != null
						&& castOther.getUserName() != null && this
						.getUserName().equals(castOther.getUserName())))
				&& ((this.getEmail() == castOther.getEmail()) || (this
						.getEmail() != null
						&& castOther.getEmail() != null && this.getEmail()
						.equals(castOther.getEmail())));
	}

	public int hashCode()
	{
		int result = 17;

		result = 37
				* result
				+ (getGroupmemberId() == null ? 0 : this.getGroupmemberId()
						.hashCode());
		result = 37 * result
				+ (getGroupId() == null ? 0 : this.getGroupId().hashCode());
		result = 37 * result
				+ (getMemberId() == null ? 0 : this.getMemberId().hashCode());
		result = 37 * result
				+ (getUserName() == null ? 0 : this.getUserName().hashCode());
		result = 37 * result
				+ (getEmail() == null ? 0 : this.getEmail().hashCode());
		return result;
	}

}