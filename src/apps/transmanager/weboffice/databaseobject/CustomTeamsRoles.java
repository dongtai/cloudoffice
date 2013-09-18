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
 * 用户自定义组同角色的关联表。
 * 即是用户自定义组又有的角色。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="customteamsroles")
public class CustomTeamsRoles implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_custom_teams_roles_gen")
	@GenericGenerator(name = "seq_custom_teams_roles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CUSTOM_TEAMS_ROLES_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams team;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Roles role;
	
	public CustomTeamsRoles()
	{		
	}
	
	public CustomTeamsRoles(CustomTeams t, Roles r)
	{
		this.team = t;
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
	
	public CustomTeams getTeam()
	{
		return team;
	}

	public void setTeam(CustomTeams team)
	{
		this.team = team;
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
