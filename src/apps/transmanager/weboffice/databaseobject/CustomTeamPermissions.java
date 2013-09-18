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
import apps.transmanager.weboffice.domain.IPermissions;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户自定义组所拥有的权限
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="customteampermissions")
public class CustomTeamPermissions implements SerializableAdapter, IPermissions
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_cutom_team_permissions_gen")
	@GenericGenerator(name = "seq_cutom_team_permissions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CUTOM_TEAM_PERMISSIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams team;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Permissions permission;
	
	public CustomTeamPermissions()
	{
		
	}
	public CustomTeamPermissions(CustomTeams t, Permissions p)
	{
		team = t;
		permission = p;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public CustomTeams getTeam()
	{
		return team;
	}
	public void setTeam(CustomTeams team)
	{
		this.team = team;
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
		return team;
	}
	
	/**
	 * 权限的拥有者类别具体参见com.evermore.weboffice.constants.both.PermissionConst中的定义
	 * @return
	 */
	public int getType()
	{
		return PermissionConst.TEAM_PERMISSION;
	}
	
}
