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
 * 系统中的某个用户拥有的应用记录，该表同companyapps表关联，
 * 公司增加或删除应用，则该表也需要更新。copmanyapps标准有的记录
 * 该表没有，则表示该用户不能使用某功能。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="userapps")
public class UserApps implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_apps_gen")
	@GenericGenerator(name = "seq_users_apps_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_APPS_ID") })
	private Long id;	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CompanyApps companyApps;            // 
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;            // 
	private Integer sortCode;          // 排序号,公司自定义排序号	
	private Date endTime;              // 功能结束使用时间,如果为null，则表示该功能无使用期限限制
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private SystemApps sysApps;            // 用户直接购买的应用
	@Column(length = 100)
	private String displayName;        // 应用显示名
		
	public UserApps()
	{
	}
	
	public UserApps(CompanyApps ca, Users u)
	{
		this.companyApps = ca;
		this.user = u;
		this.displayName = ca.getDisplayName();
		this.endTime = ca.getEndTime();
		this.sortCode = ca.getSortCode();
	}
	
	public UserApps(SystemApps sa, Users u)
	{
		this.sysApps = sa;
		this.user = u;
		this.displayName = sa.getDisplayName();
		this.endTime = sa.getEndTime();
		this.sortCode = sa.getSortCode();
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}


	public Integer getSortCode()
	{
		return sortCode;
	}

	public void setSortCode(Integer sortCode)
	{
		this.sortCode = sortCode;
	}

	public CompanyApps getCompanyApps()
	{
		return companyApps;
	}

	public void setCompanyApps(CompanyApps companyApps)
	{
		this.companyApps = companyApps;
	}

	public Users getUser()
	{
		return user;
	}

	public void setUser(Users user)
	{
		this.user = user;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public SystemApps getSysApps()
	{
		return sysApps;
	}

	public void setSysApps(SystemApps apps)
	{
		this.sysApps = apps;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	
	
}
