package apps.transmanager.weboffice.databaseobject.archive;

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
 * 归档密级表
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="archivesecurity")
public class ArchiveSecurity implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_archivesecurity_gen")
	@GenericGenerator(name = "seq_archivesecurity_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ARCHIVESECURITY_ID") })
	private Long id;
	
	@Column(length = 100)
	private String name;
	
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
	public ArchiveSecurity()
	{
		
	}
	public ArchiveSecurity(String name)
	{
		this.name=name;
	}

}
