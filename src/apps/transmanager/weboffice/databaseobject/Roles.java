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
 * 根据目前的需求定义，角色必须属于某个组织、组、用户自定义组等等。
 * 如果角色不属于任何组织等，则该角色仅仅是一个系统定义的角色字典。
 * 后续如果根据需要，要有全局的角色，该类也基本上不用修改，仅仅
 * 根据需求的定义，修改相应的DAO类，进行角色的组合判断即可。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="roles")
public class Roles implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_roles_gen")
	@GenericGenerator(name = "seq_roles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ROLES_ID") })
	private Long id; // 角色ID
	private Long action;    //角色的权限——孙爱华加的
	@Column(length = 100)
	private String roleName; // 角色名字
	@Column(length = 1000)
	private String description; // 角色描述
	private int type; // 角色类型 。具体定义见com.evermore.weboffice.constants.both.RoleCons中定义
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations organization;    // 该roles属于某个组织私有的roles
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;					// 该roles属于某个组私有的roles
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams team;				// 该roles属于某个用户自定义组私有的roles
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;				// 该roles属于某个公司私有的roles
	
	public Roles()
	{

	}
	
	public Roles clone()
	{
		return new Roles(roleName, description, type);
	}

	public Roles(String name, String des, int type)
	{
		roleName = name;
		description = des;
		this.type = type;
	}
	
	public Roles(String name, String des)
	{
		roleName = name;
		description = des;
	}

	
	public Long getAction()
	{
		return action;
	}

	public void setAction(Long action)
	{
		this.action = action;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId()
	{
		return id;
	}

	public void setRoleId(Long roleId)
	{
		this.id = roleId;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
		
	public void update(Roles n)
	{
		roleName = n.getRoleName();
		description = n.getDescription();
		type = n.getType();
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

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	

}
