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
 * 系统中的日志信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="systemlogs")
public class SystemLogs implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_system_logs_gen")
	@GenericGenerator(name = "seq_system_logs_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSTEM_LOGS_ID") })
	private Long id;	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;        // 日志所属公司，如果日志不是公司用户相关的日志，则该值为null，此时中日志属于系统本身的日志。
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;            // 操作者。
	@Column(name="type_")
	private Integer type;               // 日志类型
	private Integer operType;               // 日志具体操作类型
	private Date startDate;         // 日志时间
	@Column(length = 100)
	private String ip;              // ip 地址
	@Column(length = 60000)
	private String content;         // 操作内容	
	private Date endDate;         //  为登录退出匹配用
	
	public SystemLogs()
	{		
	}

	public SystemLogs(Company company, Users user, Integer type, Integer operType, String ip, String content)
	{
		this.company = company;
		this.user = user;
		this.type = type;
		this.operType = operType;
		this.ip = ip;
		this.content = content;
		startDate = new Date();
	}
	
	/**
	 * 为登录退出
	 * @param log
	 * @param end
	 */
	public SystemLogs(SystemLogs log, SystemLogs end)
	{
		this.company = log.company;
		this.user = log.user;
		this.type = log.type;
		this.operType = log.operType;
		this.ip = log.ip;
		this.content = log.content;
		this.startDate = log.startDate;
		this.endDate = end.startDate;
	}

	public Long getId()
	{
		return id;
	}


	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
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


	public Users getUser()
	{
		return user;
	}


	public void setUser(Users user)
	{
		this.user = user;
	}


	public Integer getType()
	{
		return type;
	}


	public void setType(Integer type)
	{
		this.type = type;
	}


	public Date getStartDate()
	{
		return startDate;
	}


	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public String getIp()
	{
		return ip;
	}


	public void setIp(String ip)
	{
		this.ip = ip;
	}


	public String getContent()
	{
		return content;
	}


	public void setContent(String content)
	{
		this.content = content;
	}


	public Integer getOperType()
	{
		return operType;
	}


	public void setOperType(Integer operType)
	{
		this.operType = operType;
	}
		
	
}
