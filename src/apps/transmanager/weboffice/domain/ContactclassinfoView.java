package apps.transmanager.weboffice.domain;


/**
 * ContactclassinfoView entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class ContactclassinfoView implements SerializableAdapter
{


	// Fields

	private Long creatorId;
	private Long contactClassId;
	private Long classId;
	private Long contactId;
	private String userName;
	private String email;
	private String department;
	private String className;

	// Constructors

	/** default constructor */
	public ContactclassinfoView() 
	{
	}

	/** minimal constructor */
	public ContactclassinfoView(Long creatorId, Long contactClassId,
			Long classId, Long contactId, String email) {
		this.creatorId = creatorId;
		this.contactClassId = contactClassId;
		this.classId = classId;
		this.contactId = contactId;
		this.email = email;
	}

	/** full constructor */
	public ContactclassinfoView(Long creatorId, Long contactClassId,
			Long classId, Long contactId, String userName, String email,
			String department, String className) 
	{
		this.creatorId = creatorId;
		this.contactClassId = contactClassId;
		this.classId = classId;
		this.contactId = contactId;
		this.userName = userName;
		this.email = email;
		this.department = department;
		this.className = className;
	}

	// Property accessors

	public Long getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getContactClassId() {
		return this.contactClassId;
	}

	public void setContactClassId(Long contactClassId) {
		this.contactClassId = contactClassId;
	}

	public Long getClassId() {
		return this.classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Long getContactId() {
		return this.contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ContactclassinfoView))
			return false;
		ContactclassinfoView castOther = (ContactclassinfoView) other;

		return ((this.getCreatorId() == castOther.getCreatorId()) || (this
				.getCreatorId() != null
				&& castOther.getCreatorId() != null && this.getCreatorId()
				.equals(castOther.getCreatorId())))
				&& ((this.getContactClassId() == castOther.getContactClassId()) || (this
						.getContactClassId() != null
						&& castOther.getContactClassId() != null && this
						.getContactClassId().equals(
								castOther.getContactClassId())))
				&& ((this.getClassId() == castOther.getClassId()) || (this
						.getClassId() != null
						&& castOther.getClassId() != null && this.getClassId()
						.equals(castOther.getClassId())))
				&& ((this.getContactId() == castOther.getContactId()) || (this
						.getContactId() != null
						&& castOther.getContactId() != null && this
						.getContactId().equals(castOther.getContactId())))
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
						.getDepartment().equals(castOther.getDepartment())))
				&& ((this.getClassName() == castOther.getClassName()) || (this
						.getClassName() != null
						&& castOther.getClassName() != null && this
						.getClassName().equals(castOther.getClassName())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCreatorId() == null ? 0 : this.getCreatorId().hashCode());
		result = 37
				* result
				+ (getContactClassId() == null ? 0 : this.getContactClassId()
						.hashCode());
		result = 37 * result
				+ (getClassId() == null ? 0 : this.getClassId().hashCode());
		result = 37 * result
				+ (getContactId() == null ? 0 : this.getContactId().hashCode());
		result = 37 * result
				+ (getUserName() == null ? 0 : this.getUserName().hashCode());
		result = 37 * result
				+ (getEmail() == null ? 0 : this.getEmail().hashCode());
		result = 37
				* result
				+ (getDepartment() == null ? 0 : this.getDepartment()
						.hashCode());
		result = 37 * result
				+ (getClassName() == null ? 0 : this.getClassName().hashCode());
		return result;
	}


}