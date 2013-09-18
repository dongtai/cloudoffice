package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 归属于某个用户自定义组的文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
public class CustomTeamFileSysResources extends FileSystemResources
{
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams customTeam;      // 该文件资源归属的组

	public CustomTeamFileSysResources()
	{		
	}
	
	public CustomTeamFileSysResources(CustomTeams c, String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		super(resourceName, displayName, path, isFolder, des);
		customTeam = c;
	}
	
	public CustomTeamFileSysResources(CustomTeams c, FileSystemResources re)
	{
		super(re);
		customTeam = c;
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
