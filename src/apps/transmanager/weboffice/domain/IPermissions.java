package apps.transmanager.weboffice.domain;

import apps.transmanager.weboffice.databaseobject.Permissions;

public interface IPermissions extends SerializableAdapter
{
	/**
	 * 具体的权限
	 * @return
	 */
	Permissions getPermission();
	/**
	 * 权限的拥有者
	 * @return
	 */
	Object getOwner();
	
	/**
	 * 权限的拥有者类别具体参见com.evermore.weboffice.constants.both.PermissionConst中的定义
	 * @return
	 */
	int getType();
}
