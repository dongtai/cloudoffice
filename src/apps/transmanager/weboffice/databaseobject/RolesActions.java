package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 角色和action的预定义字典表，
 * 即是系统的全局角色定义。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="rolesactions")
public class RolesActions implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_roles_actions_gen")
	@GenericGenerator(name = "seq_roles_actions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ROLES_ACTIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Roles role;
	@Transient
	private Actions action;
	private Integer actionType;    // 动作类型
	private Long actionId;         // 相应动作表id值
	
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Roles getRole()
	{
		return role;
	}
	public void setRole(Roles role)
	{
		this.role = role;
	}
	public Actions getAction()
	{
		return action;
	}
	public void setAction(Actions action)
	{
		this.action = action;
		if (action != null)
		{
			//actionContent = action.getActionType() + action.getId();
			actionType = action.getActionType();
			actionId = action.getId();
		}
	}
	
	public Integer getActionType()
	{
		return actionType;
	}
	public void setActionType(Integer actionType)
	{
		this.actionType = actionType;
	}
	public Long getActionId()
	{
		return actionId;
	}
	public void setActionId(Long actionId)
	{
		this.actionId = actionId;
	}
	
	
}
