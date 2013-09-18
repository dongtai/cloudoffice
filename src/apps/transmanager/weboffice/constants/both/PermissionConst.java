package apps.transmanager.weboffice.constants.both;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

public final class PermissionConst
{

	/**
	 * 禁止实例化类
	 */
	private PermissionConst()
	{		
	}	
	
	/**
	 * 文件action的类别
	 */
	public final static Integer FILE_ACTION = 1;  //"_fileaction_";
	/**
	 * 文件资源的类别
	 */
	public final static Integer FILE_RESOURCE = 1;//"_fileresource_";

	/**
	 * 管理action的类别，即是对系统中权限的管理的action
	 */
	public final static Integer MANAGEMENT_ACTION = 2; //"_managementaction_";
	/**
	 * 管理资源的类别，即是对系统中权限的管理的资源类别，如对用户的管理，对组织的管理等
	 */
	public final static Integer MANAGEMENT_RESOURCE = 2;//"_managementresource_";
	
	/**
	 * 空间管理action的类别，即是对空间权限的管理的action
	 */
	public final static Integer SPACE_ACTION = 3;   //"_spaceaction_";
	/**
	 * 空间资源的类别，即是对空间中权限的管理的资源类别，如对增减成员。
	 */
	public final static Integer SPACE_RESOURCE = 3;    //"_spaceresource_";
	
	
	/**
	 * 权限的拥有者类别定义。
	 */
	public final static int USER_PERMISSION = 0;                           // 0 UsersPermissions类别
	public final static int GROUP_PERMISSION = USER_PERMISSION + 1;        // 1 GroupsPermissions类别
	public final static int ORG_PERMISSION = GROUP_PERMISSION + 1;         // 2 OrganizationsPermissions类别 
	public final static int TEAM_PERMISSION = ORG_PERMISSION + 1;          // 3 CustomTeamsPermissions类别
	public final static int ROLE_PERMISSION = TEAM_PERMISSION + 1;         // 4 RolesPermissions类别  
	
}
