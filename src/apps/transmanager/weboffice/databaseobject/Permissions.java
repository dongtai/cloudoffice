package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.Resources;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 权限统一管理表。
 * 根据actionContent和resourceContent内容，确定一个资源的具体权限操作。
 * actionContent的保存规则为具体的action类型+该类型action表的id值为记录方式。
 * recourceContent的保存规则为具体的resource类型+该类型资源表的id值为记录方式。
 * 例如对于文件资源的权限，文件的action类型为PermissionConst.FILE_ACTION("_fileaction_"),
 * 该action对应的action表为FileSystemActions表；文件的资源类型为PermissionConst.FILE_RESOURCE（"_fileresource_"），
 * 该resource对应的resource表为FileSystemResources表。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="permissions")
public class Permissions implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_permissions_gen")
	@GenericGenerator(name = "seq_permissions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PERMISSIONS_ID") })
	private Long id;	
	@Transient
	private Actions action;     // 当前权限所允许的action操作。
	@Transient
	private Resources resource;  // 当前权限所允许的资源。
	private Integer actionType;    // 动作类型
	private Long actionId;         // 相应动作表id值
	private Integer resourceType;   // 资源类型
	private Long resourceId;       // 资源表id值
		
	public Permissions()
	{		
	}
	
	public Permissions(Permissions p, Actions a, Resources r)
	{
		this.id = p.id;
		this.actionType = p.actionType;
		this.actionId = p.actionId;
		this.resourceType = p.resourceType;
		this.resourceId = p.resourceId;
		this.action = a;
		this.resource = r;
	}
	
	public void setPermission(Actions a, Resources r)
	{
		this.action = a;
		this.resource = r;
	}	
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Actions getAction()
	{
		return action;
	}
	
	public void setActionContent(Actions action)
	{
		this.action = action;
		if (action != null)
		{
			//actionContent = action.getActionType() + action.getId();
			actionType = action.getActionType();
			actionId = action.getId();
		}
	}
	public Resources getResource()
	{
		return resource;
	}
	public void setResourceContent(Resources resource)
	{
		this.resource = resource;
		if (resource != null)
		{
			//resourceContent = resource.getResourceType() + resource.getId();
			resourceType = resource.getResourceType();
			resourceId = resource.getId();
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

	public Integer getResourceType()
	{
		return resourceType;
	}

	public void setResourceType(Integer resourceType)
	{
		this.resourceType = resourceType;
	}

	public Long getResourceId()
	{
		return resourceId;
	}

	public void setResourceId(Long resourceId)
	{
		this.resourceId = resourceId;
	}
	
	
}
