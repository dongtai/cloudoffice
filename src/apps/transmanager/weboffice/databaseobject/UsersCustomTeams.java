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
 * 用户自定义的分组中的成员关联表,
 * 即是属于用户的自定义组。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="userscustomteams")
public class UsersCustomTeams implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_custom_teams_gen")
	@GenericGenerator(name = "seq_users_custom_teams_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_CUSTOM_TEAMS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;         // 在用户自定义组中的成员用户
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams customTeam;   // 用户自定义的组
	
	public UsersCustomTeams()
	{		
	}
	public UsersCustomTeams(Users u, CustomTeams team)
	{
		user = u;
		customTeam = team;
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
	public CustomTeams getCustomTeam()
	{
		return customTeam;
	}
	public void setCustomTeam(CustomTeams customTeam)
	{
		this.customTeam = customTeam;
	}	
	
}
