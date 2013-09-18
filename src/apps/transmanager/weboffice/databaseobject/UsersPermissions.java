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
 * 用户权限关联表。
 * 即是用户拥有的权限。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="userspermissions")
public class UsersPermissions implements SerializableAdapter, IPermissions
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_permissions_gen")
	@GenericGenerator(name = "seq_users_permissions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_PERMISSIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Permissions permission;
	
	public UsersPermissions()
	{		
	}
		
	public UsersPermissions(Users u, Long t)
	{
		this.user  = u;
		id = t;
	}
	
	public UsersPermissions(Users u, Permissions p, Actions a, Resources r)
	{
		this.user = u;
		this.permission = p;
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
	public Users getUser()
	{
		return user;
	}
	public void setUser(Users user)
	{
		this.user = user;
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
		return user;
	}
	
	/**
	 * 权限的拥有者类别具体参见com.evermore.weboffice.constants.both.PermissionConst中的定义
	 * @return
	 */
	public int getType()
	{
		return PermissionConst.USER_PERMISSION;
	}
	
	
}
