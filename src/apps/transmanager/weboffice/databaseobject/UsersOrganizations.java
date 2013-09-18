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
 * 用户组织关联表。
 * 即是组织中的用户成员，或者说是用户加入的组织。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="usersorganizations")
public class UsersOrganizations implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_organizations_gen")
	@GenericGenerator(name = "seq_users_organizations_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_ORGANIZATIONS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations organization;
	
	public UsersOrganizations()
	{
		
	}
	
	public UsersOrganizations(Users u, Organizations o)
	{
		user = u;
		organization = o;
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
	public Organizations getOrganization()
	{
		return organization;
	}
	public void setOrganization(Organizations organization)
	{
		this.organization = organization;
	}	

}
