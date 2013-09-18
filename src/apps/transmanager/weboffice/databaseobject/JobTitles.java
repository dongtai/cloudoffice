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
 * 组织中人员的职位定义字典表
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="jobtitles")
public class JobTitles  implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_jobtitles_gen")
	@GenericGenerator(name = "seq_jobtitles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_JOB_TITLES_ID") })
	private Long id;
	@Column(unique=true, length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	
	public JobTitles()
	{
	}
	
	public JobTitles(String name)
	{
		this.name = name;
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
	
	
}
