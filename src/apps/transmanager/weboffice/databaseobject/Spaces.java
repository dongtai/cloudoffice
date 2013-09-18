package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 空间表。
 * 所有的组织、组、用户自定义组、用户都拥有一个唯一的独立空间。
 * 后续根据需要再修改为一个空间，不属于任何组织、组、用户自定义组、用户，
 * 而是上述任何组织、组、用户自定义组、用户都可以加入到某个空间中。即是
 * 空间是一个独立存在的单位，其中的成员可以是任何组织、组、用户自定义组、用户等等。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="spaces")
public class Spaces implements SerializableAdapter
{
	@Id
	@Column(length = 255)
	private String spaceUID;            // 空间的UID值，根据用户或组织结构建立的时候生成该值。
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	@Column(length = 1000)
	private String spacePath;           // 空间的路径名
	@Column(length = 100)
	private String spaceStatus;         // 目前的状态
	private Date date;
	@Transient
	private Users user;
	@Transient
	private Organizations organization;
	@Transient	
	private Groups group;
	@Transient
	private Company company;
	@Transient
	private CustomTeams team;
	@Transient
	private Long filePermit; // 文档权限
	@Transient
	private Long spacePermisson; // 空间权限
	
	public Spaces()
	{		
	}
	
	public Spaces(String name, String des)
	{
		this.name = name; 
		this.description = des; 
		date = new Date();
	}
	
	public Spaces(String spaceUID, String name, String des, String path, String status)
	{
		this.spaceUID = spaceUID;
		this.name = name; 
		this.description = des; 
		this.spacePath = path;
		this.spaceStatus = status;
		date = new Date();
	}
	
	public Spaces(String spaceUID, String name, String des, String path)
	{
		this.spaceUID = spaceUID;
		this.name = name; 
		this.description = des; 
		this.spacePath = path;
		date = new Date();
	}
	public Spaces(Spaces s, Users u)
	{
		spaceUID = s.spaceUID;
		name = s.name;
		description = s.description;
		spacePath = s.spacePath;
		spaceStatus = s.spaceStatus;
		date = s.date;
		user = u;
	}
	
	public Spaces(Spaces s, Company c)
	{
		spaceUID = s.spaceUID;
		name = s.name;
		description = s.description;
		spacePath = s.spacePath;
		spaceStatus = s.spaceStatus;
		date = s.date;
		company = c;
	}
	
	public Spaces(Spaces s, Organizations o)
	{
		spaceUID = s.spaceUID;
		name = s.name;
		description = s.description;
		spacePath = s.spacePath;
		spaceStatus = s.spaceStatus;
		date = s.date;
		organization = o;
	}
	
	public Spaces(Spaces s, Groups g)
	{
		spaceUID = s.spaceUID;
		name = s.name;
		description = s.description;
		spacePath = s.spacePath;
		spaceStatus = s.spaceStatus;
		date = s.date;
		group = g;
	}
	
	public Spaces(Spaces s, CustomTeams c)
	{
		spaceUID = s.spaceUID;
		name = s.name;
		description = s.description;
		spacePath = s.spacePath;
		spaceStatus = s.spaceStatus;
		date = s.date;
		team = c;
	}
	
	public String getSpaceUID()
	{
		return spaceUID;
	}
	public void setSpaceUID(String spaceUID)
	{
		this.spaceUID = spaceUID;
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
	public String getSpacePath()
	{
		return spacePath;
	}
	public void setSpacePath(String spacePath)
	{
		this.spacePath = spacePath;
	}	
			
	public String getSpaceStatus()
	{
		return spaceStatus;
	}

	public void setSpaceStatus(String spaceStatus)
	{
		this.spaceStatus = spaceStatus;
	}

	public void update(Spaces n)
	{
		name = n.name;
		description = n.description;	
		spaceStatus = n.spaceStatus;
	}
	
	public Users getUser()
	{
		return user;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}

	public void setUser(Users user)
	{
		this.user = user;
	}

	public Organizations getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organizations organization)
	{
		this.organization = organization;
	}

	public Groups getGroup()
	{
		return group;
	}

	public void setGroup(Groups group)
	{
		this.group = group;
	}

	public CustomTeams getTeam()
	{
		return team;
	}

	public void setTeam(CustomTeams team)
	{
		this.team = team;
	}

	public Long getFilePermit() {
		return filePermit;
	}

	public void setFilePermit(Long filePermit) {
		this.filePermit = filePermit;
	}

	public Long getSpacePermisson() {
		return spacePermisson;
	}

	public void setSpacePermisson(Long spacePermisson) {
		this.spacePermisson = spacePermisson;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
	

}
