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

import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.IPermissions;
import apps.transmanager.weboffice.domain.Resources;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 角色权限管理表。
 * 即是角色拥有的权限。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="rolespermissions")
public class RolesPermissions implements SerializableAdapter, IPermissions
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_roles_permissions_gen")
	@GenericGenerator(name = "seq_roles_permissions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ROLES_PERMISSIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Roles role;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Permissions permission;
	
	public RolesPermissions()
	{		
	}
	
	public RolesPermissions(Roles role, Permissions p, Actions a, Resources r)
	{
		this.role = role;
		permission = p;
		this.permission.setPermission(a, r);
	}
	
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
	public Permissions getPermission()
	{
		return permission;
	}
	public void setPermission(Permissions permission)
	{
		this.permission = permission;
	}
	/**
	 * 权限的拥有者
	 * @return
	 */
	public Object getOwner()
	{
		return role;
	}
	
	/**
	 * 权限的拥有者类别具体参见com.evermore.weboffice.constants.both.PermissionConst中的定义
	 * @return
	 */
	public int getType()
	{
		return PermissionConst.ROLE_PERMISSION;
	}

}
