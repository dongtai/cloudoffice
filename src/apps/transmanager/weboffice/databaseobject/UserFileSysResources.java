package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 归属于某个用户的文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
public class UserFileSysResources  extends FileSystemResources
{
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;      // 该文件资源归属的用户

	public UserFileSysResources()
	{		
	}
	
	public UserFileSysResources(Users user, String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		super(resourceName, displayName, path, isFolder, des);
		this.user = user;		
	}
	
	public UserFileSysResources(Users user, FileSystemResources re)
	{
		super(re);
		this.user = user;		
	}
	
	public Users getUser()
	{
		return user;
	}

	public void setUser(Users user)
	{
		this.user = user;
	}
	
}
