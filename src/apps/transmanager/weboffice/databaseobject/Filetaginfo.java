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
 * Filetaginfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="filetaginfo")
public class Filetaginfo implements SerializableAdapter
{

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_fileTaginfo_gen")
	@GenericGenerator(name = "seq_fileTaginfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FILE_TAGINFO_ID") })
	private Long fileTagId;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Taginfo taginfo;
	@Column(length = 1000)
	private String fileName;
	@Column(length = 100)
	private String companyId;

	// Constructors

	/** default constructor */
	public Filetaginfo()
	{
	}

	/** full constructor */
	public Filetaginfo(Taginfo taginfo, String fileName, String companyId)
	{
		this.taginfo = taginfo;
		this.fileName = fileName;
		this.companyId = companyId;
	}

	// Property accessors

	public Long getFileTagId()
	{
		return this.fileTagId;
	}

	public void setFileTagId(Long fileTagId)
	{
		this.fileTagId = fileTagId;
	}

	public Taginfo getTaginfo()
	{
		return this.taginfo;
	}

	public void setTaginfo(Taginfo taginfo)
	{
		this.taginfo = taginfo;
	}

	public String getFileName()
	{
		return this.fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getCompanyId()
	{
		return this.companyId;
	}

	public void setCompanyId(String companyId)
	{
		this.companyId = companyId;
	}

}