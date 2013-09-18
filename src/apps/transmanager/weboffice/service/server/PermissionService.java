package apps.transmanager.weboffice.service.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CompanyFileSysResources;
import apps.transmanager.weboffice.databaseobject.CustomTeamFileSysResources;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.FileSystemActions;
import apps.transmanager.weboffice.databaseobject.FileSystemResources;
import apps.transmanager.weboffice.databaseobject.GroupFileSysResources;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.OrganizationFileSysResources;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.SpacesActions;
import apps.transmanager.weboffice.databaseobject.SystemManageActions;
import apps.transmanager.weboffice.databaseobject.UserFileSysResources;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.IPermissions;
import apps.transmanager.weboffice.service.dao.PermissionDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.util.both.FlagUtility;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Component(value=PermissionService.NAME)
public class PermissionService
{
	public final static String NAME = "permissionService";
	@Autowired
	private PermissionDAO permissionDAO;
	@Autowired
	private StructureDAO structureDAO;
			
	/**
	 * 返回角色设置的action
	 * @param roleId
	 * @return
	 */
	public List<Actions> getRoleAction(long roleId)
	{
		return permissionDAO.getDefinedRoleAction(roleId);
	}
	
	/**
	 * 增加或修改定义角色的actions。
	 * @param roleId
	 * @param actions
	 */
	public void addOrUpdateDefinedRoleAction(long roleId, List<Actions> actions)
	{
		permissionDAO.addOrUpdateDefinedRoleAction(roleId, actions);
	}
	
	/**
	 * 增加或定义角色的预定义action。	type为0表示系统权限，1为空间权限，2为文件权限
	 * @param roleId
	 * @param actions
	 */
	public void updateDefinedRoleAction(long roleId, long permission, int type)
	{
		permissionDAO.updateDefinedRoleAction(roleId, permission, type);
	}
	
	/**
	 * 判断用户是否有权限对空间进行一定的操作, 该权限位由ManagementCons中定义。
	 * @param userId
	 * @return
	 */
	public long getSystemPermission(Long userId)
	{
		List<SystemManageActions> ret = permissionDAO.getSystemManageActionsByUserId(userId);
		if (ret == null || ret.size() < 1)
		{
			return 0;
		}
		else
		{
			Long action;
			long retAction = 0;
			for (SystemManageActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
					/*if (FlagUtility.isLongFlag(action, ManagementCons.CREATE_GROUP))
					{
						return true;
					}*/
				}
			}
			return retAction;
		}
	}
	
	/**
	 * 获得用户对某个公司空间的权限, 该权限位由SpaceConstants中定义
	 * @param userId
	 * @param orgId
	 * @return
	 */
	public long getCompanySpacePermission(Long userId)
	{
		long retAction = 0;
		// 系统级的角色拥有的权限。
		if (structureDAO.isCompanyAdmin(userId))   // 公司管理员默认一些权限
		{
			retAction = SpaceConstants.SPACE_MANAGER;
		}
		List<SpacesActions> ret = permissionDAO.getCompanySpacesActionsByUserId(userId);
		if (ret != null && ret.size() > 0)
		{
			Long action;
			for (SpacesActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		return retAction;
	}
	
	/**
	 * 获得用户对某个空间的权限, 该权限位由SpaceConstants中定义
	 * @param userId
	 * @param orgId
	 * @return
	 */
	public long getOrgSpacePermission(Long userId, Long orgId)
	{
		long retAction = 0;
		if (structureDAO.isOrganizatonManager(userId, orgId))      // 空间管理员默认一些权限
		{
			retAction = SpaceConstants.SPACE_MANAGER;
		}
		// 系统级的角色拥有的权限。
		List<SpacesActions> ret = permissionDAO.getOrgSpacesActionsByUserId(userId, orgId);
		if (ret != null && ret.size() > 0)
		{
			Long action;
			for (SpacesActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		return retAction;
	}
		
	/**
	 * 获得用户对某个空间的权限, 该权限位由SpaceConstants中定义
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public long getGroupSpacePermission(Long userId, Long groupId)
	{
		long retAction = 0;
		if (structureDAO.isGroupManager(userId, groupId)||structureDAO.isCompanyAdmin(userId))   // 空间管理员默认一些权限
		{
			retAction = SpaceConstants.SPACE_MANAGER;
		}
		/*if (FlagUtility.isLongFlag(getSystemPermission(userId),
				ManagementCons.CREATE_SPACE))     //  企业文库做法改变，临时这样处理吧。
		{
			Spaces space = structureDAO.findSpaceByUID(JCRService.COMPANY_ROOT);
			if (space==null)
			{
				//孙爱华增加的，如果不存在会报错的，如果做了初始化，这段代码就不需要了
				space=new Spaces();
				space.setSpaceUID(JCRService.COMPANY_ROOT);
				space.setName(JCRService.COMPANY_ROOT);
				space.setSpacePath(JCRService.COMPANY_ROOT);
				space.setDate(new Date());
				structureDAO.save(space);
			}
			
			if (space.getSpaceUID().equals(JCRService.COMPANY_ROOT)) 
			{
				retAction = SpaceConstants.SPACE_MANAGER;
			}
		}*/
		// 系统级的角色拥有的权限。
		List<SpacesActions> ret = permissionDAO.getGroupSpacesActionsByUserId(userId, groupId);
		if (ret != null && ret.size() > 0)
		{
			Long action;
			for (SpacesActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		return retAction;
	}
	
	/**
	 * 获得用户对某个用户自定义空间的权限, 该权限位由SpaceConstants中定义
	 * @param userId
	 * @param teamId
	 * @return
	 */
	public long getTeamSpacePermission(Long userId, Long teamId)
	{
		long retAction = 0;
		if (structureDAO.isTeamOwner(userId, teamId))   // 空间管理员默认一些权限
		{
			retAction = SpaceConstants.SPACE_MANAGER;
		}
		// 系统级的角色拥有的权限。
		List<SpacesActions> ret = permissionDAO.getTeamSpacesActionsByUserId(userId, teamId);
		if (ret != null && ret.size() > 0)
		{
			Long action;
			for (SpacesActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		return retAction;
	}
	
	/**
	 * 获得用户对某个资源拥有的action值, 如果该资源没有设置过权限，则返回null值。
	 * @param userName
	 * @param path
	 * @return
	 */
	public Long getFileSystemAction(String userName, String path)
	{
		Users user = structureDAO.isExistUser(userName);
		if (user != null)
		{
			return getFileSystemAction(user.getId(), path);
		}
		return null;
	}
	
	/**
	 * 获得用户对某个资源拥有的action值, 如果该资源没有设置过权限，则返回null值。
	 * @param userId
	 * @param path
	 * @return
	 */
	public Long getFileSystemAction(Long userId, String path)
	{
		if (structureDAO.isSpaceOwner(userId, path))
		{
			return FileSystemCons.SPACE_MANAGER;			
		}
		if (structureDAO.isTeamOwner(userId, path))       // 用户自定义组的拥有者
		{
			return FileSystemCons.SPACE_MANAGER;
		}	
		// 系统级的角色拥有的权限。
		if (structureDAO.isGroupManager(userId, path))      // 空间管理员默认一些权限
		{
			return FileSystemCons.SPACE_MANAGER;
		}
		if (structureDAO.isOrganizatonManager(userId, path))      // 空间管理员默认一些权限
		{
			return FileSystemCons.SPACE_MANAGER;
		}
		if (path.startsWith(FileConstants.COMPANY_ROOT) && structureDAO.isCompanyAdmin(userId))                 // 公司管理员
		{
			return FileSystemCons.SPACE_MANAGER;
		}
		return getFileSysteAction(userId, path);
	}
	
	/**
	 * 获得用户对某个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限，
	 * 直到获取到空间根为止。
	 * @param userName 用户名
	 * @param path 具体的文件资源路径
	 * @param treeFlag 是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	public Long getFileSystemAction(String userName, String path, boolean treeFlag)
	{
		Users user = structureDAO.isExistUser(userName);
		if (user != null)
		{
			return getFileSystemAction(user.getId(), path, treeFlag);
		}
		return null;
	}
	
	/**
	 * 获得用户对某个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限，
	 * 直到获取到空间根为止。
	 * @param userId 用户Id
	 * @param path 具体的文件资源路径
	 * @param treeFlag 是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	public Long getFileSystemAction(Long userId, String path, boolean treeFlag)
	{
		long retAction = 0;
		// 系统级的角色拥有的权限。
		String rootPath = path;
		int index;
		if (treeFlag)
		{
			index = path.indexOf("/");
			if (index > 0)
			{
				rootPath = path.substring(0, index);
			}
		}
		//判断此空间是否是个人文库
		if (structureDAO.isSpaceOwner(userId, rootPath))
		{
			retAction = FileSystemCons.SPACE_MANAGER;			
			return retAction;
		}
		if (rootPath.startsWith(FileConstants.COMPANY_ROOT) && structureDAO.isCompanyAdmin(userId))                 // 公司管理员
		{
				return FileSystemCons.SPACE_MANAGER;
		}
		if (rootPath.startsWith(FileConstants.COMPANY_ROOT))
		{
			if (FlagUtility.isLongFlag(getSystemPermission(userId),
				ManagementCons.CREATE_SPACE))
			{
				retAction = FileSystemCons.SPACE_MANAGER;			
				return retAction;
			}
			else
			{
				retAction =  FileSystemCons.BROWSER;
			}
		}
		if (structureDAO.isTeamOwner(userId, rootPath))       // 用户自定义组的拥有者
		{
			retAction = FileSystemCons.SPACE_MANAGER;			
			return retAction;
		}	
		if (structureDAO.isGroupManager(userId, rootPath))      // 空间管理员默认一些权限
		{
			retAction = FileSystemCons.SPACE_MANAGER;
			//nullFlag = false;
			return retAction;
		}
		if (structureDAO.isOrganizatonManager(userId, rootPath))      // 空间管理员默认一些权限
		{
			retAction = FileSystemCons.SPACE_MANAGER;
			//nullFlag = false;
			return retAction;
		}
				
		Long ret;
		do              // 有性能问题，后续再说吧。
		{			
			ret = getFileSysteAction(userId, path);
			if (ret != null)
			{
				return ret;
			}
			index = path.lastIndexOf("/");
			if (index < 0)
			{
				break;
			}
			path = path.substring(0, index);			
		}
		while (treeFlag);
		
		return retAction;
	}
	
	public boolean isShareFile(Long userId, String path)
	{
		if (path.startsWith(FileConstants.USER_ROOT))
		{
			Users user = structureDAO.findUserById(userId);
			if (user != null)
			{
				return path.indexOf(user.getSpaceUID()) == -1;
			}
		}
		return false;
	}
	
	/**
	 * 获得用户对多个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限，
	 * 直到获取到空间根为止。该方法的paths参数，除了最后一级路径不同外，以后各级的路径
	 * 必须相同，即是该方法中的paths参数，是同一级父目录下的不同文件及文件夹的权限判断。
	 * @param userId 用户Id
	 * @param paths 具体的文件资源路径
	 * @param treeFlag 是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	public List<Long> getFileSystemAction(Long userId, String[] paths, boolean treeFlag)
	{
		List<Long> permits = new ArrayList<Long>();
		int size = paths.length;
		long retAction = 0;
		// 系统级的角色拥有的权限。
		String rootPath = paths[0];
		int index;
		if (treeFlag)
		{
			index = paths[0].indexOf("/");
			if (index > 0)
			{
				rootPath = paths[0].substring(0, index);
			}
		}		
		if (rootPath.startsWith(FileConstants.COMPANY_ROOT) && structureDAO.isCompanyAdmin(userId))                 // 公司管理员
		{
			retAction = FileSystemCons.SPACE_MANAGER;			
			for (int i = 0; i < size; i++)
			{
				permits.add(retAction);
			}
			return permits;
		}
		if (structureDAO.isTeamOwner(userId, rootPath))       // 用户自定义组的拥有者
		{
			retAction = FileSystemCons.SPACE_MANAGER;			
			for (int i = 0; i < size; i++)
			{
				permits.add(retAction);
			}
			return permits;
		}	
		if (structureDAO.isSpaceOwner(userId, rootPath))
		{
			retAction = FileSystemCons.SPACE_MANAGER;
			for (int i = 0; i < size; i++)
			{
				permits.add(retAction);
			}
			return permits;
		}		
		if (structureDAO.isGroupManager(userId, rootPath))      // 空间管理员默认一些权限
		{
			retAction = FileSystemCons.SPACE_MANAGER;
			for (int i = 0; i < size; i++)
			{
				permits.add(retAction);
			}
			return permits;
		}
		if (structureDAO.isOrganizatonManager(userId, rootPath))      // 空间管理员默认一些权限
		{
			retAction = FileSystemCons.SPACE_MANAGER;
			for (int i = 0; i < size; i++)
			{
				permits.add(retAction);
			}
			return permits;
		}
				
		Long ret;
		Long parentPermits = null;
		for (String path : paths)
		{
			ret = getFileSysteAction(userId, path);
			if (ret != null)
			{
				permits.add(ret);
			}
			else if (!treeFlag)
			{
				permits.add(0L);
			}
			else
			{
				if (parentPermits == null)    // 
				{
					while(true)              // 有性能问题，后续再说吧。
					{
						index = path.lastIndexOf("/");
						if (index < 0)
						{
							parentPermits = 0L;
							break;
						}
						path = path.substring(0, index);
						ret = getFileSysteAction(userId, path);
						if (ret != null)
						{
							parentPermits = ret;
							break;
						}
					}
				}
				permits.add(parentPermits);
			}
		}
		
		return permits;
	}
	
	public Long getFileSysteAction(Long userId, String path)
	{
		long retAction = 0;		
		List<FileSystemActions> ret =  permissionDAO.getFileSysActionsByUser(userId, path);
		boolean nullFlag = true;
		if (ret != null && ret.size() > 0)
		{
			Long action;
			for (FileSystemActions temp : ret)
			{
				action = temp.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
					nullFlag = false;
				}
			}
		}
		return nullFlag ?  null : retAction;
	}
	
	/**
	 * 当文件资源删除的时候，需要删除该资源对外赋予的所有权限。
	 * @param path
	 */
	public void deleteFileSystemAction(String path)
	{
		permissionDAO.deletFileSysActionsByUser(path);
	}
	
	/**
	 * 当文件资源删除的时候，需要删除该资源对外赋予的所有权限。
	 * @param path
	 */
	public void deleteFileSystemAction(String[] path)
	{
		for (String temp : path)
		{
			permissionDAO.deletFileSysActionsByUser(temp);
		}
	}
	
	/**
	 * 对文件资源增加或删除相应的用户、组织、用户组、角色等的权限
	 * @param resource 文件系统资源。如果该资源是新设置权限，则从resource中获得的getId()为空，否则是对已经设置过
	 * 的资源在进行一定的权限，及用户修改。
	 * @param ownId, 文件系统的拥有者Id。
	 * @param ownType， 文件系统拥有者类型。具体见com.evermore.weboffice.constants.both.FileSystemCons中的定义类型。
	 * @param action 对文件系统资源设置的action，如果是新的action，则从中获得的getId（）为null，否则是对已经设置过
	 * 的action进行一定的修改。
	 * @param addUserIds 需要增加权限的用户id集合，无则为null。
	 * @param delUserIds 需要删除权限的用户id集合，无则为null。
	 * @param addGroupIds 需要增加权限的组id集合，无则为null。
	 * @param delGroupIds 需要删除权限的组id集合，无则为null。
	 * @param addRoleIds 需要增加权限的角色id集合，无则为null。
	 * @param delRoleIds 需要删除权限的角色id集合，无则为null。
	 * @param addOrgIds 需要增加权限的组织id集合，无则为null。
	 * @param delOrgIds 需要删除权限的组织id集合，无则为null。
	 * @param addTeamIds 需要增加权限的用户自定义组id集合，无则为null。
	 * @param delTeamIds 需要删除权限的用户自定义组id集合，无则为null。
	 */
	public void addOrUpdateFileSystemPermission(FileSystemResources resource, Long ownId, int ownType, FileSystemActions action,
			 List<Long> addUserIds, List<Long> delUserIds, List<Long> addGroupIds, List<Long> delGroupIds, List<Long> addRoleIds, List<Long> delRoleIds,
			List<Long> addOrgIds, List<Long> delOrgIds, List<Long> addTeamIds, List<Long> delTeamIds)
	{
		FileSystemResources res = null;
		FileSystemActions ac;
		if (action.getId() == null)
		{
			permissionDAO.save(action);
			ac = action;
		}
		else
		{
			ac = (FileSystemActions)permissionDAO.find(FileSystemActions.class, action.getId());
			ac.update(action);
			permissionDAO.update(ac);
		}
		if (resource.getId() == null)
		{
			if (ownType == FileSystemCons.USER_OWN)
			{
				Users u = (Users)permissionDAO.find(Users.class, ownId);
				res = new UserFileSysResources(u, resource);
			}
			else if (ownType == FileSystemCons.GROUP_OWN)
			{
				Groups g = (Groups)permissionDAO.find(Groups.class, ownId);
				res = new GroupFileSysResources(g, resource);
			}
			else if (ownType == FileSystemCons.ORG_OWN)
			{
				Organizations o = (Organizations)permissionDAO.find(Organizations.class, ownId);
				res = new OrganizationFileSysResources(o, resource);
			}
			else if (ownType == FileSystemCons.TEAM_OWN)
			{
				CustomTeams g = (CustomTeams)permissionDAO.find(CustomTeams.class, ownId);
				res = new CustomTeamFileSysResources(g, resource);	
			}
			if (res != null)
			{
				permissionDAO.save(res);
			}
		}
		else
		{
			res = (FileSystemResources)permissionDAO.find(FileSystemResources.class, resource.getId());
			res.update(resource);
			permissionDAO.update(res);
		}
		permissionDAO.addOrUpdateFileSystemPermission(res, ac,
				 addUserIds,  delUserIds,  addGroupIds,  delGroupIds,  addRoleIds,  delRoleIds,
				 addOrgIds,  delOrgIds,  addTeamIds,  delTeamIds);
		
	}
	
	/**
	 * 对文件资源增加或删除相应的用户、组织、用户组、角色等的权限
	 * @param resource 文件系统资源。如果该资源是新设置权限，则从resource中获得的getId()为空，否则是对已经设置过
	 * 的资源在进行一定的权限，及用户修改。
	 * @param ownId, 文件系统的拥有者Id。
	 * @param ownType， 文件系统拥有者类型。具体见com.evermore.weboffice.constants.both.FileSystemCons中的定义类型。
	 * @param action 对文件系统资源设置的action，如果是新的action，则从中获得的getId（）为null，否则是对已经设置过
	 * 的action进行一定的修改。
	 * @param addUserIds 需要增加权限的用户id集合，无则为null。actions数组和addUserIds数组需要一一对应，即是一个actions
	 * 对应一个addUserIds值。没有这位null 
	 * @param delUserIds 需要删除权限的用户id集合，无则为null。
	 */
	public void addOrUpdateFileSystemPermission(FileSystemResources resource, Long ownId, int ownType, FileSystemActions[] actions,
			 Long[] addUserIds, List<Long> delUserIds)
	{
		FileSystemResources res = null;
		if (resource.getId() == null)
		{
			if (ownId!=null)
			{
				if (ownType == FileSystemCons.USER_OWN)
				{
					Users u = (Users)permissionDAO.find(Users.class, ownId);
					res = new UserFileSysResources(u, resource);
				}
				else if (ownType == FileSystemCons.GROUP_OWN)
				{
					Groups g = (Groups)permissionDAO.find(Groups.class, ownId);
					res = new GroupFileSysResources(g, resource);
				}
				else if (ownType == FileSystemCons.ORG_OWN)
				{
					Organizations o = (Organizations)permissionDAO.find(Organizations.class, ownId);
					res = new OrganizationFileSysResources(o, resource);
				}
				else if (ownType == FileSystemCons.TEAM_OWN)
				{
					CustomTeams g = (CustomTeams)permissionDAO.find(CustomTeams.class, ownId);
					res = new CustomTeamFileSysResources(g, resource);	
				}
				else if (ownType == FileSystemCons.COMPANY_OWN)
				{
					Company g = (Company)permissionDAO.find(Company.class, ownId);
					res = new CompanyFileSysResources(g, resource);	
				}
				if (res != null)
				{
					permissionDAO.save(res);
				}
			}
		}
		else
		{
			res = (FileSystemResources)permissionDAO.find(FileSystemResources.class, resource.getId());
			res.update(resource);
			permissionDAO.update(res);
		}
		if (res!=null)
		{
			permissionDAO.addOrUpdateFileSystemPermission(res, actions,	 addUserIds,  delUserIds);
		}
	}	
	
	/**
	 * 获得某个文件资源拥有的权限，包括赋予给用户的，赋予给组织的，赋予给组的，
	 * 赋予给角色的，赋予给用户自定义组的。
	 * @param path 文件系统资源路径
	 * @return
	 */
	public List<IPermissions> getAllFileSystemPermission(String path)
	{
		return  permissionDAO.getAllFileSystemPermission(path);
	}
	
	/**
	 * 由于文件及文件夹移动，需要更新权限相应的resouce。
	 * @param oldPaths
	 * @param newPath
	 */
	public void updateFileSystemActionForMove(String newPath, String ... oldPaths)
	{
		permissionDAO.updateFileSystemActionForMove(newPath, oldPaths);
	}
	
	/**
	 * 由于文件更名，需要更新权限相应的resource
	 * @param oldPath
	 * @param newPath
	 * @param fileName
	 */
	public void updateFileSystemActionForRename(String oldPath, String newPath)
	{
		permissionDAO.updateFileSystemActionForRename(oldPath, newPath);
	}
	
	public boolean isCompanyAdmin(long userId)
	{
		return structureDAO.isCompanyAdmin(userId);
	}

}
