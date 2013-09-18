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
 * 公司信息相关配置信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="companyconfig")
public class CompanyConfig implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_company_config_gen")
	@GenericGenerator(name = "seq_company_config_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_COMPANY_CONFIG_ID") })
	private Long id;	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company; 	
	@Column(name="domain_", length = 1000)
	private String domain;        // 描述
	@Column(name="log_", length = 100)
	private String log;           // 公司log的名字
	
	public CompanyConfig()
	{		
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getLog()
	{
		return log;
	}

	public void setLog(String log)
	{
		this.log = log;
	}
	
	
}
