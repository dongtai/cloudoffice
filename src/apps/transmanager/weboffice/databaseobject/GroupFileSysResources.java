package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 归属于某个组的文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
public class GroupFileSysResources extends FileSystemResources
{
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups group;      // 该文件资源归属的组

	public GroupFileSysResources()
	{		
	}
	
	public GroupFileSysResources(Groups g, String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		super(resourceName, displayName, path, isFolder, des);
		group = g;
	}
	
	public GroupFileSysResources(Groups g, FileSystemResources re)
	{
		super(re);
		group = g;
	}
	public Groups getGroups()
	{
		return group;
	}

	public void setGroups(Groups group)
	{
		this.group = group;
	}
	
}
