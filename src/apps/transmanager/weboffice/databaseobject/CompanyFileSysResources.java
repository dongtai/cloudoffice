package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 归属于公司的文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
public class CompanyFileSysResources extends FileSystemResources
{
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;      // 该文件资源归属的组

	public CompanyFileSysResources()
	{		
	}
	
	public CompanyFileSysResources(Company c, String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		super(resourceName, displayName, path, isFolder, des);
		this.company = c;
	}
	
	public CompanyFileSysResources(Company c, FileSystemResources re)
	{
		super(re);
		this.company = c;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	
	
}
