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
 * 用户自定义的分组中的组成员关联表，即是在用户自定义组中
 * 可以把一个组作为一个整体成员加入。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="customteamsgroups")
public class CustomTeamsGroups implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_custom_team_groups_gen")
	@GenericGenerator(name = "seq_custom_team_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CUSTOM_TEAMS_GROUPS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;         // 在用户自定义组中的组成员
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams customTeam;   // 用户自定义的组
	
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
	public CustomTeams getCustomTeam()
	{
		return customTeam;
	}
	public void setCustomTeam(CustomTeams customTeam)
	{
		this.customTeam = customTeam;
	}	
	
}
