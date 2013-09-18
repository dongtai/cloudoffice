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
 * 公司集团信息，该公司集团同company是松耦合关系。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="corporations")
public class Corporations implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_corporations_gen")
	@GenericGenerator(name = "seq_corporations_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CORPORATION_ID") })
	private Long id;	
	@Column(unique=true, length = 100)
	private String name;               // 公司名
	@Column(length = 1000)
	private String description;        // 描述
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.NO_ACTION)
	private Company company;           // 集团下的所有公司

	
	public Corporations()
	{		
	}
	
	public Corporations(String name, String description)
	{
		this.name = name;
		this.description = description;
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

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	
}
