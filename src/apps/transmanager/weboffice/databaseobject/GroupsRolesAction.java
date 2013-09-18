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
 * 文件rolesaction所属的groups范围。
 * 即是组拥有的全局角色。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="groupsrolesaction")
public class GroupsRolesAction implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_group_roles_actions_gen")
	@GenericGenerator(name = "seq_group_roles_actions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUP_ROLES_ACTIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private RolesActions roleAction;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public RolesActions getRoleAction()
	{
		return roleAction;
	}
	public void setRoleAction(RolesActions roleAction)
	{
		this.roleAction = roleAction;
	}
	public Groups getGroup()
	{
		return group;
	}
	public void setGroup(Groups group)
	{
		this.group = group;
	}
	
	
}
