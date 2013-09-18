package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

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
 * 系统中的某个公司拥有的应用记录，该表同systemapps表关联。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="companyapps")
public class CompanyApps implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_company_apps_gen")
	@GenericGenerator(name = "seq_company_apps_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_COMPANY_APPS_ID") })
	private Long id;	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;            // 
	private Organizations organizations;//对应的一级部门，暂没用到，以后会用到的

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private SystemApps apps;            // 
	@Column(length = 100)
	private String displayName;        // 应用显示名
	private Integer sortCode;          // 排序号,公司自定义排序号	
	private Date endTime;              // 功能结束使用时间,如果为null，则表示该功能无使用期限限制
	private Integer validate = 0;          // 是否禁用，0为正常使用，1为禁用 
	private Boolean flag;              // 是否是公司的用户默认都有的功能，true表示为公司用户默认都有的功能，false表示非默认功能

	public CompanyApps()
	{		
	}

	public CompanyApps(Company company, SystemApps apps, String display, Integer sortCode, Boolean flag)
	{
		this.company = company;
		this.apps = apps;
		this.displayName = display;
		this.sortCode = sortCode;
		this.flag = flag;
		this.endTime = apps.getEndTime();
	}
	
	public CompanyApps(Company company, SystemApps apps)
	{
		this.company = company;
		this.apps = apps;
		this.displayName = apps.getDisplayName();
		this.sortCode = apps.getSortCode();
		this.flag = apps.getFlag();
		this.endTime = apps.getEndTime();
	}
	
	public Organizations getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Organizations organizations) {
		this.organizations = organizations;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}


	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}


	public Integer getSortCode()
	{
		return sortCode;
	}

	public void setSortCode(Integer sortCode)
	{
		this.sortCode = sortCode;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}

	public SystemApps getApps()
	{
		return apps;
	}

	public void setApps(SystemApps apps)
	{
		this.apps = apps;
	}

	public Integer getValidate()
	{
		return validate;
	}

	public void setValidate(Integer validate)
	{
		this.validate = validate;
	}
	
	
	public Boolean getFlag()
	{
		return flag;
	}

	public void setFlag(Boolean flag)
	{
		this.flag = flag;
	}

	public void update(CompanyApps ca)
	{
		displayName = ca.displayName;
		sortCode = ca.sortCode;	
		endTime = ca.endTime;
		validate = ca.validate;
		flag = ca.flag;
	}
	
}
