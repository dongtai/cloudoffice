package apps.transmanager.weboffice.databaseobject;

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
 * Taginfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="taginfo")
public class Taginfo implements SerializableAdapter
{

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_taginfo_gen")
	@GenericGenerator(name = "seq_taginfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TAGINFO_ID") })
	private Long tagId;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	@Column(length = 100)
	private String tag;

	// private Set filetaginfos = new HashSet(0);

	// Constructors

	/** default constructor */
	public Taginfo()
	{
	}

	/** minimal constructor */
	public Taginfo(Users userinfo, String tag)
	{
		this.userinfo = userinfo;
		this.tag = tag;
	}

	/** full constructor */
	/*
	 * public Taginfo(Userinfo userinfo, String tag, Set filetaginfos) {
	 * this.userinfo = userinfo; this.tag = tag; this.filetaginfos =
	 * filetaginfos; }
	 */

	// Property accessors

	public Long getTagId()
	{
		return this.tagId;
	}

	public void setTagId(Long tagId)
	{
		this.tagId = tagId;
	}

	public Users getUserinfo()
	{
		return this.userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public String getTag()
	{
		return this.tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	/*
	 * public Set getFiletaginfos() { return this.filetaginfos; }
	 * 
	 * public void setFiletaginfos(Set filetaginfos) { this.filetaginfos =
	 * filetaginfos; }
	 */

}