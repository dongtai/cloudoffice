package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 归属于某个组织（部门）的文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
public class OrganizationFileSysResources extends FileSystemResources
{
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations org;      // 该文件资源归属的组织（部门）

	public OrganizationFileSysResources()
	{		
	}
	
	public OrganizationFileSysResources(Organizations org, String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		super(resourceName, displayName, path, isFolder, des);
		this.org = org;
	}
	
	public OrganizationFileSysResources(Organizations org, FileSystemResources re)
	{
		super(re);
		this.org = org;
	}
	
	public Organizations getOrganization()
	{
		return org;
	}

	public void setOrganization(Organizations org)
	{
		this.org = org;
	}
	
}
