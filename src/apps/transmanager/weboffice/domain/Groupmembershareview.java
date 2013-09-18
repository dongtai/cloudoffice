package apps.transmanager.weboffice.domain;

/**
 * Groupmembershareview entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class Groupmembershareview implements SerializableAdapter
{

	// Fields

	private String shareFile;
	private Long shareowner;
	private Integer permit;
	private Long memberId;
	private Integer isNew;

	// Constructors

	/** default constructor */
	public Groupmembershareview()
	{
	}

	/** minimal constructor */
	public Groupmembershareview(String shareFile, Long shareowner,
			Integer permit)
	{
		this.shareFile = shareFile;
		this.shareowner = shareowner;
		this.permit = permit;
	}

	/** full constructor */
	public Groupmembershareview(String shareFile, Long shareowner,
			Integer permit, Long memberId, Integer isNew)
	{
		this.shareFile = shareFile;
		this.shareowner = shareowner;
		this.permit = permit;
		this.memberId = memberId;
		this.isNew = isNew;
	}

	// Property accessors

	public String getShareFile()
	{
		return this.shareFile;
	}

	public void setShareFile(String shareFile)
	{
		this.shareFile = shareFile;
	}

	public Long getShareowner()
	{
		return this.shareowner;
	}

	public void setShareowner(Long shareowner)
	{
		this.shareowner = shareowner;
	}

	public Integer getPermit()
	{
		return this.permit;
	}

	public void setPermit(Integer permit)
	{
		this.permit = permit;
	}

	public Long getMemberId()
	{
		return this.memberId;
	}

	public void setMemberId(Long memberId)
	{
		this.memberId = memberId;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Groupmembershareview))
			return false;
		Groupmembershareview castOther = (Groupmembershareview) other;

		return ((this.getShareFile() == castOther.getShareFile()) || (this
				.getShareFile() != null
				&& castOther.getShareFile() != null && this.getShareFile()
				.equals(castOther.getShareFile())))
				&& ((this.getShareowner() == castOther.getShareowner()) || (this
						.getShareowner() != null
						&& castOther.getShareowner() != null && this
						.getShareowner().equals(castOther.getShareowner())))
				&& ((this.getPermit() == castOther.getPermit()) || (this
						.getPermit() != null
						&& castOther.getPermit() != null && this.getPermit()
						.equals(castOther.getPermit())))
				&& ((this.getMemberId() == castOther.getMemberId()) || (this
						.getMemberId() != null
						&& castOther.getMemberId() != null && this
						.getMemberId().equals(castOther.getMemberId())))
				&& ((this.getIsNew() == castOther.getIsNew()) || (this
						.getIsNew() != null
						&& castOther.getIsNew() != null && this.getIsNew()
						.equals(castOther.getIsNew())));
	}

	public int hashCode()
	{
		int result = 17;

		result = 37 * result
				+ (getShareFile() == null ? 0 : this.getShareFile().hashCode());
		result = 37
				* result
				+ (getShareowner() == null ? 0 : this.getShareowner()
						.hashCode());
		result = 37 * result
				+ (getPermit() == null ? 0 : this.getPermit().hashCode());
		result = 37 * result
				+ (getMemberId() == null ? 0 : this.getMemberId().hashCode());

		result = 37 * result
				+ (getIsNew() == null ? 0 : this.getIsNew().hashCode());
		return result;
	}

	public Integer getIsNew()
	{
		return isNew;
	}

	public void setIsNew(Integer isNew)
	{
		this.isNew = isNew;
	}

}