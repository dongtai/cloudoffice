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
 * 组织角色关联表
 * 即是该组织拥有的角色。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="organizationsroles")
public class OrganizationsRoles implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_organizations_roles_gen")
	@GenericGenerator(name = "seq_organizations_roles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ORGANIZATIONS_ROLES_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations organization;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Roles role;
	
	public OrganizationsRoles()
	{		
	}
	
	public OrganizationsRoles(Organizations o, Roles r)
	{
		this.organization = o;
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
	public Organizations getOrganization()
	{
		return organization;
	}
	public void setOrganization(Organizations organization)
	{
		this.organization = organization;
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
