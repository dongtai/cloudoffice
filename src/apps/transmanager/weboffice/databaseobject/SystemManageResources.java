package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.domain.Resources;

/**
 * 针对系统管理的资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="systemmanageresources")
public class SystemManageResources  implements Resources
{	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_system_manage_resources_gen")
	@GenericGenerator(name = "seq_system_manage_resources_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSTEM_MANAGE_RESOURCES_ID") })
	private Long id;	
	@Column(length = 1000)
	private String name;
	@Column(length = 1000)
	private String content;    // 资源的具体内容
	@Column(length = 1000)
	private String description;
	
	public SystemManageResources()
	{
		
	}
	
	public SystemManageResources(String name)
	{
		this.name = name;
	}
	
	public Long getId()
	{
		return id;
	}

	public String getResourceName()
	{
		return name;
	}

	public Integer getResourceType()
	{
		return PermissionConst.MANAGEMENT_RESOURCE;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
	public void setResurceName(String name)
	{
		this.name = name;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	

}
