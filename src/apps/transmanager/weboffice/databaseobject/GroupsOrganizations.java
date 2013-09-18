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
 * 组织组管理表。
 * 即是在组的成员中，可以把一个组织结构作为一个整体成员加入到组中。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="groupsorganizations")
public class GroupsOrganizations implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_organizations_gen")
	@GenericGenerator(name = "seq_groups_organizations_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ORGANIZATIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations organization;
	
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
	public Organizations getOrganization()
	{
		return organization;
	}
	public void setOrganization(Organizations organization)
	{
		this.organization = organization;
	}
	
}
