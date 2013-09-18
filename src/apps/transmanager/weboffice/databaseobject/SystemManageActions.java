package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.domain.Actions;

/**
 * 针对系统管理的action动作定义
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="systemmanageactions")
public class SystemManageActions implements Actions
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_files_manage_actions_gen")
	@GenericGenerator(name = "seq_files_manage_actions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSTEM_MANAGE_ACTIONS_ID") })
	private Long id;
	private Long action;    // 文件及文件夹的权限先以long的各位来表示，如果后续位数不够，在扩展。
	@Column(length = 100)
	private String name;
	
	public SystemManageActions()
	{		
	}
	
	public SystemManageActions(Long action, String name)
	{
		this.action = action;
		this.name = name;
	}
	
	public SystemManageActions getClone()
	{
		return new SystemManageActions(action, name);
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getAction()
	{
		return action;
	}
	public void setAction(Long action)
	{
		this.action = action;
	}
	
	public String getActionName()
	{
		return name;
	}

	public void setActionName(String name)
	{
		this.name = name;
	}

	public Integer getActionType()
	{
		return PermissionConst.MANAGEMENT_ACTION;
	}
	
	public void update(Actions n)
	{
		SystemManageActions ne = (SystemManageActions)n;
		this.action = ne.action;
		this.name = ne.name;
	}

}
