package apps.transmanager.weboffice.domain;

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

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="usersorganizations")
public class DepMemberPo implements SerializableAdapter
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
