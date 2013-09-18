package apps.transmanager.weboffice.databaseobject;

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
 * 组角色管理表。
 * 即是组拥有的角色。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="groupsroles")
public class GroupsRoles implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_roles_gen")
	@GenericGenerator(name = "seq_groups_roles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ROLES_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Roles role;
	
	public GroupsRoles()
	{		
	}
	
	public GroupsRoles(Groups g, Roles r)
	{
		this.group = g;
		this.role = r;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Groups getGroup()
	{
		return group;
	}
	public void setGroup(Groups group)
	{
		this.group = group;
	}
	public Roles getRole()
	{
		return role;
	}
	public void setRole(Roles role)
	{
		this.role = role;
	}
	
	
}
