package apps.transmanager.weboffice.service.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.springframework.orm.jpa.JpaCallback;

import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.constants.both.RoleCons;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CustomTeamFileSysResources;
import apps.transmanager.weboffice.databaseobject.CustomTeamPermissions;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.FileSystemActions;
import apps.transmanager.weboffice.databaseobject.FileSystemResources;
import apps.transmanager.weboffice.databaseobject.GroupFileSysResources;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.GroupsPermissions;
import apps.transmanager.weboffice.databaseobject.OrganizationFileSysResources;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.OrganizationsPermissions;
import apps.transmanager.weboffice.databaseobject.Permissions;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.RolesActions;
import apps.transmanager.weboffice.databaseobject.RolesPermissions;
import apps.transmanager.weboffice.databaseobject.SpacesActions;
import apps.transmanager.weboffice.databaseobject.SystemManageActions;
import apps.transmanager.weboffice.databaseobject.UserFileSysResources;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersPermissions;
import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.IPermissions;
import apps.transmanager.weboffice.domain.Resources;
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

public class PermissionDAO extends BaseDAO
{
	
	//private final static String FILEACTION =  " CONCAT('" + PermissionConst.FILE_ACTION + "',";
	//private final static String FILERESOURCE =  " CONCAT('" + PermissionConst.FILE_RESOURCE + "',";
	//private final static String MANAGEACTION =  " CONCAT('" + PermissionConst.MANAGEMENT_ACTION + "',";
	//private final static String MANAGERESOURCE =  " CONCAT('" + PermissionConst.MANAGEMENT_RESOURCE + "',";
	//private final static String SPACEACTION =  " CONCAT('" + PermissionConst.SPACE_ACTION + "',";
	//private final static String SPACERESOURCE =  " CONCAT('" + PermissionConst.SPACE_RESOURCE + "',";
	
	
	public void deletePermission(long permissionId)
	{
		String sql = "delete Permissions where id = ? ";
		excute(sql, permissionId);
	}
		
	/**
	 * 设置文件资源的actions值。
	 * @param actionName
	 * @param action
	 * @return
	 */
	public Actions addFileSysActions(final String actionName, final long action)
	{
		try
		{
			return (Actions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	FileSystemActions fsa = new FileSystemActions();
	        		fsa.setAction(action);
	        		fsa.setActionName(actionName);
	            	em.persist(fsa);
	            	return fsa;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 设置某个用户拥有的文件资源
	 * @param resourceName
	 * @param ownerUserId
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Resources addUserFileSysResources(final String resourceName, final long ownerUserId, 
			final String path, final String displayName, final boolean isFold, final String desc)
	{
		try
		{
			return (Resources)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	Users owner = em.find(Users.class, ownerUserId);	            	            	
	            	UserFileSysResources ufsr = new UserFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(ufsr);
	            	return ufsr;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 设置某个组织拥有的文件资源
	 * @param resourceName
	 * @param ownerOrgId
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Resources addOrgFileSysResources(final String resourceName, final long ownerOrgId, 
			final String path, final String displayName, final boolean isFold, final String desc)
	{
		try
		{
			return (Resources)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	Organizations owner = em.find(Organizations.class, ownerOrgId);	            	            	
	            	OrganizationFileSysResources ofsr = new OrganizationFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(ofsr);
	            	return ofsr;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 设置某个组拥有的文件资源
	 * @param resourceName
	 * @param ownerGroupId
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Resources addGroupFileSysResources(final String resourceName, final long ownerGroupId, 
			final String path, final String displayName, final boolean isFold, final String desc)
	{
		try
		{
			return (Resources)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	Groups owner = em.find(Groups.class, ownerGroupId);	            	            	
	            	GroupFileSysResources gfsr = new GroupFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(gfsr);
	            	return gfsr;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 设置某个用户自定义组拥有的文件资源
	 * @param resourceName
	 * @param ownerTeamId
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Resources addCustomTeamFileSysResources(final String resourceName, final long ownerTeamId, 
			final String path, final String displayName, final boolean isFold, final String desc)
	{
		try
		{
			return (Resources)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	CustomTeams owner = em.find(CustomTeams.class, ownerTeamId);	            	            	
	            	CustomTeamFileSysResources tfsr = new CustomTeamFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(tfsr);
	            	return tfsr;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
		
	/**
	 * 对某个role设置相应的action动作
	 * @param actionName
	 * @param actions
	 * @return
	 */
	public RolesActions addRoleSystemMangeActions(final Roles role, final String actionName, final long actions)
	{
		try
		{
			return (RolesActions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	SystemManageActions sm = new SystemManageActions();
					sm.setActionName(actionName);
					sm.setAction(actions);
	            	em.persist(sm);
	            		            		            	
	            	RolesActions rp = new RolesActions();
	            	rp.setAction(sm);
	            	rp.setRole(role);
	            	em.persist(rp);	            	
	            	
	            	return rp;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 对某个role设置相应的action动作
	 * @param actionName
	 * @param actions
	 * @return
	 */
	public RolesActions addRoleSpaceActions(final Roles role, final String actionName, final long actions)
	{
		try
		{
			return (RolesActions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	SpacesActions sm = new SpacesActions();
					sm.setActionName(actionName);
					sm.setAction(actions);
	            	em.persist(sm);
	            		            		            	
	            	RolesActions rp = new RolesActions();
	            	rp.setAction(sm);
	            	rp.setRole(role);
	            	em.persist(rp);	            	
	            	
	            	return rp;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 对某个role设置相应的action动作
	 * @param actionName
	 * @param actions
	 * @param resourceName
	 * @return
	 */
	public RolesActions addRoleFileSystemActions(final Roles role, final String actionName, final long actions)
	{
		try
		{
			return (RolesActions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	FileSystemActions sm = new FileSystemActions();
					sm.setActionName(actionName);
					sm.setAction(actions);
	            	em.persist(sm);
	            		            		            	
	            	RolesActions rp = new RolesActions();
	            	rp.setAction(sm);
	            	rp.setRole(role);
	            	em.persist(rp);	            	
	            	
	            	return rp;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 对某个文件资源设置相应的action动作
	 * @param ownerUserId
	 * @param actionName
	 * @param actions
	 * @param resourceName
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Permissions addUserFileSystemPermission(final long ownerUserId, final String actionName, final long actions,
			final String resourceName, final String path, final String displayName, final boolean isFold, final String desc)
	{
		try
		{
			return (Permissions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	Users owner = em.find(Users.class, ownerUserId);
	            	FileSystemActions fsa = new FileSystemActions();
	            	fsa.setAction(actions);
	            	fsa.setActionName(actionName);
	            	em.persist(fsa);
	            		            	
	            	UserFileSysResources ufsr = new UserFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(ufsr);
	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(fsa);
	            	p.setResourceContent(ufsr);
	            	em.persist(p);
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
		
	/**
	 * 对某个文件资源设置相应的action动作,并把该权限赋予用户。
	 * @param userId 需要赋予权限的用户人员
	 * @param ownerUserId
	 * @param actionName
	 * @param actions
	 * @param resourceName
	 * @param path
	 * @param displayName
	 * @param isFold
	 * @param desc
	 * @return
	 */
	public Permissions addUserFileSystemPermission(final long ownerUserId, final String actionName, final long actions,
			final String resourceName, final String path, final String displayName,
			final boolean isFold, final String desc, final Long... userId)
	{
		try
		{
			return (Permissions)getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	Users owner = em.find(Users.class, ownerUserId);
	            	FileSystemActions fsa = new FileSystemActions();
	            	fsa.setAction(actions);
	            	fsa.setActionName(actionName);
	            	em.persist(fsa);
	            		            	
	            	UserFileSysResources ufsr = new UserFileSysResources(owner, resourceName, displayName, path, isFold, desc);
	            	em.persist(ufsr);
	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(fsa);
	            	p.setResourceContent(ufsr);
	            	em.persist(p);
	            	
	            	UsersPermissions up;
	            	for (long temp : userId)
	            	{
	            		owner = em.find(Users.class, temp);
	            		up = new UsersPermissions();
	            		up.setPermission(p);
	            		up.setUser(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加用户人员对资源及action操作的权限。
	 * @param action
	 * @param resource
	 * @param userId
	 */
	public void addUserPermissions(final Actions action, final Resources resource, final Long... userId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(action);
	            	p.setResourceContent(resource);
	            	em.persist(p);
	            	
	            	UsersPermissions up;
	            	Users owner;
	            	for (long temp : userId)
	            	{
	            		owner = em.find(Users.class, temp);
	            		up = new UsersPermissions();
	            		up.setPermission(p);
	            		up.setUser(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加用户的权限
	 * @param p
	 * @param userId
	 */
	public void addUserPermissions(final Permissions p, final Long... userId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {  	
	            	UsersPermissions up;
	            	Users owner;
	            	for (long temp : userId)
	            	{
	            		owner = em.find(Users.class, temp);
	            		up = new UsersPermissions();
	            		up.setPermission(p);
	            		up.setUser(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加组对资源及action操作的权限。
	 * @param action
	 * @param resource
	 * @param groupId
	 */
	public void addGroupPermissions(final Actions action, final Resources resource, final Long... groupId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(action);
	            	p.setResourceContent(resource);
	            	em.persist(p);
	            	
	            	GroupsPermissions up;
	            	Groups owner;
	            	for (long temp : groupId)
	            	{
	            		owner = em.find(Groups.class, temp);
	            		up = new GroupsPermissions();
	            		up.setPermission(p);
	            		up.setGroup(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加组的权限
	 * @param p
	 * @param groupId
	 */
	public void addGroupPermissions(final Permissions p, final Long... groupId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {  	
	            	GroupsPermissions up;
	            	Groups owner;
	            	for (long temp : groupId)
	            	{
	            		owner = em.find(Groups.class, temp);
	            		up = new GroupsPermissions();
	            		up.setPermission(p);
	            		up.setGroup(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加角色对资源及action操作的权限。
	 * @param action
	 * @param resource
	 * @param roleId
	 */
	public void addRolePermissions(final Actions action, final Resources resource, final Long... roleId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(action);
	            	p.setResourceContent(resource);
	            	em.persist(p);
	            	
	            	RolesPermissions up;
	            	Roles owner;
	            	for (long temp : roleId)
	            	{
	            		owner = em.find(Roles.class, temp);
	            		up = new RolesPermissions();
	            		up.setPermission(p);
	            		up.setRole(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加角色的权限
	 * @param p
	 * @param roleId
	 */
	public void addRolePermissions(final Permissions p, final Long... roleId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {  	
	            	RolesPermissions up;
	            	Roles owner;
	            	for (long temp : roleId)
	            	{
	            		owner = em.find(Roles.class, temp);
	            		up = new RolesPermissions();
	            		up.setPermission(p);
	            		up.setRole(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加组织对资源及action操作的权限。
	 * @param action
	 * @param resource
	 * @param orgId
	 */
	public void addOrgPermissions(final Actions action, final Resources resource, final Long... orgId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(action);
	            	p.setResourceContent(resource);
	            	em.persist(p);
	            	
	            	OrganizationsPermissions up;
	            	Organizations owner;
	            	for (long temp : orgId)
	            	{
	            		owner = em.find(Organizations.class, temp);
	            		up = new OrganizationsPermissions();
	            		up.setPermission(p);
	            		up.setOrganization(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加组织的权限
	 * @param p
	 * @param orgId
	 */
	public void addOrgPermissions(final Permissions p, final Long... orgId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {  	
	            	OrganizationsPermissions up;
	            	Organizations owner;
	            	for (long temp : orgId)
	            	{
	            		owner = em.find(Organizations.class, temp);
	            		up = new OrganizationsPermissions();
	            		up.setPermission(p);
	            		up.setOrganization(owner);
	            		em.persist(up);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加用户自定义组对资源及action操作的权限。
	 * @param action
	 * @param resource
	 * @param teamId
	 */
	public void addCustomTeamPermissions(final Actions action, final Resources resource, final Long... teamId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	            		            	
	            	Permissions p = new Permissions();
	            	p.setActionContent(action);
	            	p.setResourceContent(resource);
	            	em.persist(p);
	            	
	            	CustomTeamPermissions ct;
	            	CustomTeams owner;
	            	for (long temp : teamId)
	            	{
	            		owner = em.find(CustomTeams.class, temp);
	            		ct = new CustomTeamPermissions();
	            		ct.setPermission(p);
	            		ct.setTeam(owner);
	            		em.persist(ct);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 增加用户自定义组的权限
	 * @param p
	 * @param teamId
	 */
	public void addCustomTeamPermissions(final Permissions p, final Long... teamId)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {  	
	            	CustomTeamPermissions ct;
	            	CustomTeams owner;
	            	for (long temp : teamId)
	            	{
	            		owner = em.find(CustomTeams.class, temp);
	            		ct = new CustomTeamPermissions();
	            		ct.setPermission(p);
	            		ct.setTeam(owner);
	            		em.persist(ct);
	            	}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
		
	/**
	 * 得到用户对文件资源所拥有的action。
	 * @param userId 用户id
	 * @param resourceId 资源Id
	 * @return 
	 */
	public List<FileSystemActions> getUserFileSysActions(long userId, long resourceId)
	{
		//String sql = "select distinct a from UsersPermissions u, FileSystemActions a"
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + FILERESOURCE + "?) and u.permission.actionContent = "
		//		+ FILEACTION + "a.id)";	
		String sql = "select distinct a from UsersPermissions u, FileSystemActions a "
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = ? "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";		
		return (List<FileSystemActions>)findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, resourceId, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到用户对文件资源所拥有的action。
	 * @param userId 用户id
	 * @param filePath 文件path
	 * @return
	 */
	public List<FileSystemActions> getFileSysActionsByUser(long userId, String filePath)
	{
		List<FileSystemActions> ret = new ArrayList<FileSystemActions>();
		// 先如此分步处理，后续用union来处理。
		//String sql = "select distinct a from UsersPermissions u, FileSystemActions a, FileSystemResources b"
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)"
		//		+ " and b.abstractPath = ? ";
		String sql = "select distinct a from UsersPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id  and b.abstractPath = ? ";	
		List temp = findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
		ret.addAll(temp);
		
		//sql = "select distinct a from RolesActions gra, FileSystemActions a, UsersRoles u"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + FILEACTION + "a.id)"
		//	+ " and (gra.role.organization.id in (select o.id from Organizations o where o.spaceUID = ?) " 
		//			+ " or gra.role.company.id in (select c.id from Company c where c.spaceUID = ? )"
		//			+ " or gra.role.group.id in (select g.id from Groups g where g.spaceUID = ? )"
		//			+ " or gra.role.team.id in (select t.id from CustomTeams t where t.spaceUID = ? ))";
		sql = "select distinct a from RolesActions gra, FileSystemActions a, UsersRoles u"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id "
			+ " and (gra.role.organization.id in (select o.id from Organizations o where o.spaceUID = ?) " 
					+ " or gra.role.company.id in (select c.id from Company c where c.spaceUID = ? )"
					+ " or gra.role.group.id in (select g.id from Groups g where g.spaceUID = ? )"
					+ " or gra.role.team.id in (select t.id from CustomTeams t where t.spaceUID = ? ))";
		temp = findAllBySql(sql, userId, PermissionConst.FILE_ACTION, filePath, filePath, filePath, filePath);
		ret.addAll(temp);				
		
		/*sql = "select distinct a from RolesActions gra, FileSystemActions a, UsersRoles u"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + FILEACTION + "a.id)"
			+ " and gra.role.group.spaceUID = ? ";
		temp = findAllBySql(sql, userId, filePath);
		ret.addAll(temp);*/
		
		// role
		//sql = "select distinct a from UsersRoles u, RolesPermissions r, FileSystemActions a, FileSystemResources b"
		//	+ " where u.user.id = ? and u.role.id = r.role.id and r.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and r.permission.actionContent = "	+ FILEACTION + "a.id)"
		//	+ " and b.abstractPath = ? ";
		sql = "select distinct a from UsersRoles u, RolesPermissions r, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.role.id = r.role.id and r.permission.resourceType = ? and r.permission.resourceId = b.id"
			+ " and r.permission.actionType = ? and r.permission.actionId = a.id and b.abstractPath = ? ";	
		temp = findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
		ret.addAll(temp);
		
		// group
		//sql = "select distinct a from UsersGroups u, GroupsPermissions g, FileSystemActions a, FileSystemResources b"
		//	+ " where u.user.id = ? and u.group.id = g.group.id and g.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and g.permission.actionContent = "	+ FILEACTION + "a.id)"
		//	+ " and b.abstractPath = ? ";		
		sql = "select distinct a from UsersGroups u, GroupsPermissions g, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.group.id = g.group.id and g.permission.resourceType = ? and g.permission.resourceId = b.id "
			+ " and g.permission.actionType = ? and g.permission.actionId = a.id and b.abstractPath = ? ";
		temp = findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
		ret.addAll(temp);
		
		// organization
		//sql = "select distinct a from UsersOrganizations u, OrganizationsPermissions o, FileSystemActions a, FileSystemResources b"
		//	+ " where u.user.id = ? and u.organization.id = o.organization.id and o.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and o.permission.actionContent = "	+ FILEACTION + "a.id)"
		//	+ " and b.abstractPath = ? ";
		sql = "select distinct a from UsersOrganizations u, OrganizationsPermissions o, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.organization.id = o.organization.id and o.permission.resourceType = ? and o.permission.resourceId = b.id "
			+ " and o.permission.actionType = ? and o.permission.actionId = a.id  and b.abstractPath = ? ";	
		temp = findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
		ret.addAll(temp);
		
		// team
		//sql = "select distinct a from UsersCustomTeams u, CustomTeamPermissions o, FileSystemActions a, FileSystemResources b"
		//	+ " where u.user.id = ? and u.customTeam.id = o.team.id and o.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and o.permission.actionContent = "	+ FILEACTION + "a.id)"
		//	+ " and b.abstractPath = ? ";
		sql = "select distinct a from UsersCustomTeams u, CustomTeamPermissions o, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.customTeam.id = o.team.id and o.permission.resourceType = ? and o.permission.resourceId = b.id "
			+ " and o.permission.actionType = ? and o.permission.actionId = a.id and b.abstractPath = ? ";
		temp = findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
		ret.addAll(temp);
		
		return ret;
	}
	
	/**
	 * 删除文件资源所拥有的所有action。删除该path及path子目录后的所有相应权限。
	 * 注意：该方法供系统中删除文件及文件夹的时候，相应的删除对应的所有权限。
	 * 如果是对某个文件夹及文件删除权限，而文件及文件夹不从系统中删除，不能使用该方法。
	 * 
	 * @param filePath 文件path
	 * @return
	 */
	public void deletFileSysActionsByUser(String filePath)
	{
		// 先如此分步处理，后续用union来处理。
		String delPath = filePath + "%";
				
		// 删除关联的permission
		/*String sql = " delete Permissions where id in ("
				+ "select per.id from Permissions per, FileSystemResources b"
				+ " where per.resourceContent = " + FILERESOURCE + "b.id)"
				+ " and b.abstractPath like ?) ";
		excute(sql, delPath);*/
		// 由于mysql不支持该操作，只有先查出，再删了。
		//String sql = "select distinct per from Permissions per, FileSystemResources b"
		//	+ " where per.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and b.abstractPath like ? ";
		String sql = "select distinct per from Permissions per, FileSystemResources b"
			+ " where per.resourceType = ? and per.resourceId = b.id and b.abstractPath like ? ";
		List ret = findAllBySql(sql, PermissionConst.FILE_RESOURCE, delPath);
		delete(ret);
		
		// 删除filesystemresource		
		sql = "delete from FileSystemResources fs where fs.abstractPath like ? ";
		excute(sql, delPath);
		
		//  删除关联的action
		/*sql = " delete FileSystemActions  where id not in ("
			+ "select a.id from Permissions per, FileSystemActions a"
			+ " where per.actionContent = "	+ FILEACTION + "a.id) "
			+ " union select aa.id from RolesActions gra, FileSystemActions aa "
			+ " where gra.actionContent = " + FILEACTION + "aa.id))";
		excute(sql);*/
		//sql =  "select distinct a.id from Permissions per, FileSystemActions a"
		//		+ " where per.actionContent = "	+ FILEACTION + "a.id)";
		sql =  "select distinct a.id from Permissions per, FileSystemActions a"
			+ " where per.actionType = ? and per.actionId = a.id";
		ret = findAllBySql(sql, PermissionConst.FILE_ACTION);
	
		//sql = " select distinct a.id from FileSystemActions a, RolesActions gra"
		//		+ " where gra.actionContent = " + FILEACTION + "a.id)";
		sql = " select distinct a.id from FileSystemActions a, RolesActions gra"
			+ " where gra.actionType = ?  and gra.actionId = a.id";
		ret.addAll(findAllBySql(sql, PermissionConst.FILE_ACTION));
	
		if (ret.size() > 0)
		{
			sql = " delete from FileSystemActions ac where ac.id not in (?1)";
			excute(sql, ret);
		}	
		// roleaction中先不处理，现在需求还没有这样。				
	}
	
	/**
	 * 由于文件及文件夹移动，需要更新权限相应的resouce。
	 * @param oldPaths
	 * @param newPath
	 */
	public void updateFileSystemActionForMove(String newPath, String ... oldPaths)
	{
		int index;
		String old;
		String  queryString;
		for(String temp : oldPaths)
		{
			index = temp.lastIndexOf("/");
			old = index >= 0 ? temp.substring(0, index) : temp;
			queryString = "update FileSystemResources as fs set fs.abstractPath = replace(fs.abstractPath, ?, ?) where fs.abstractPath like ? ";
			excute(queryString, old, newPath, temp + "%");		
		}
	}
	
	/**
	 * 由于文件更名，需要更新权限相应的resource
	 * @param oldPath
	 * @param newPath
	 * @param fileName
	 */
	public void updateFileSystemActionForRename(String oldPath, String newPath)
	{
		String  queryString = "update FileSystemResources as fs set fs.abstractPath = replace(fs.abstractPath, ?, ?) where fs.abstractPath like ? ";
		excute(queryString, oldPath, newPath, oldPath + "%");
	}
	
	/**
	 * 得到用户对文件资源所拥有的action。
	 * @param userId 用户id
	 * @param filePath 文件path
	 * @return
	 */
	public List<FileSystemActions> getUserFileSysActions(long userId, String filePath)
	{
		//String sql = "select distinct a from UsersPermissions u, FileSystemActions a, FileSystemResources b"			
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)"
		//		+ " and b.abstractPath = ? ";		
		String sql = "select distinct a from UsersPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.abstractPath = ? ";
		return (List<FileSystemActions>)findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
	}
	
	/**
	 * 得到角色对文件资源所拥有的action。
	 * @param roleId 用户id
	 * @param filePath 文件path
	 * @return
	 */
	public List<FileSystemActions> getRoleFileSysActions(long roleId, String filePath)
	{
		//String sql = "select distinct a from RolesPermissions u, FileSystemActions a, FileSystemResources b"
		//		+ " where u.role.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = " + FILEACTION + "a.id)"
		//		+ " and b.abstractPath = ? ";
		String sql = "select distinct a from RolesPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.role.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.abstractPath = ? ";	
		return (List<FileSystemActions>)findAllBySql(sql, roleId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
	}
	
	/**
	 * 得到组对文件资源所拥有的action。
	 * @param groupId 用户id
	 * @param filePath 文件path
	 * @return
	 */
	public List<FileSystemActions> getGroupFileSysActions(long groupId, String filePath)
	{
		//String sql = "select distinct a from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
		//		+ " where u.group.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = " + FILEACTION + "a.id)"
		//		+ " and b.abstractPath = ? ";		
		String sql = "select distinct a from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.group.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.abstractPath = ? ";		
		return (List<FileSystemActions>)findAllBySql(sql, groupId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
	}
	
	/**
	 * 得到组织对文件资源所拥有的action。
	 * @param orgId 用户id
	 * @param filePath 文件path
	 * @return
	 */
	public List<FileSystemActions> getOrgFileSysActions(long orgId, String filePath)
	{
		//String sql = "select distinct a from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
		//		+ " where u.organization.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)"
		//		+ " and b.abstractPath = ? ";	
		String sql = "select distinct a from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.organization.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.abstractPath = ? ";	
		return (List<FileSystemActions>)findAllBySql(sql, orgId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION, filePath);
	}
	
	/**
	 * 得到用户拥有权限的文件资源
	 * @param userId
	 * @return
	 */
	public List<FileSystemResources> getUserFileSystemResource(long userId)
	{
		//String sql = "select distinct b from UsersPermissions u, FileSystemResources b"
		//	+ " where u.user.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)";	
		String sql = "select distinct b from UsersPermissions u, FileSystemResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id";	
		return (List<FileSystemResources>)findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE);
	}
	
	/**
	 * 得到角色拥有权限的文件资源
	 * @param roleId
	 * @return
	 */
	public List<FileSystemResources> getRoleFileSystemResource(long roleId)
	{
		//String sql = "select distinct b from RolesPermissions u, FileSystemResources b"			
		//	+ " where u.role.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)";
		String sql = "select distinct b from RolesPermissions u, FileSystemResources b"
			+ " where u.role.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id";
		return (List<FileSystemResources>)findAllBySql(sql, roleId, PermissionConst.FILE_RESOURCE);
	}
	
	/**
	 * 得到组拥有权限的文件资源
	 * @param groupId
	 * @return
	 */
	public List<FileSystemResources> getGroupFileSystemResource(long groupId)
	{
		//String sql = "select distinct b from GroupsPermissions u, FileSystemResources b"			
		//	+ " where u.group.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)";
		String sql = "select distinct b from GroupsPermissions u, FileSystemResources b"
			+ " where u.group.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id";
		return (List<FileSystemResources>)findAllBySql(sql, groupId, PermissionConst.FILE_RESOURCE);
	}
	
	/**
	 * 得到组及父组所拥有权限的资源。
	 * 在得父组的时候，会递归得到所有级别的父组，直到根为止。	
	 * @param groupId
	 * @return
	 */
	public List<FileSystemResources> getGroupTreeFileSystemResource(long groupId)
	{
		String sql = "select g.parentKey from Groups g where g.id = ? ";
		List<String> temp = findAllBySql(sql, groupId);
		List<Long> para = new ArrayList<Long>(); 
		if (temp != null && temp.size() > 0)
		{
			String[] sa;
			for (String s : temp)
			{
				sa = s.split("-");
				for (String  a : sa)
				{
					try
					{
						Long l = new Long(a.trim());
						para.add(l);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		para.add(groupId);
		
		//sql = "select distinct b from GroupsPermissions u, FileSystemResources b"
		//	+ " where u.group.id in (?1) and u.permission.resourceContent = " + FILERESOURCE + "b.id)";
		sql = "select distinct b from GroupsPermissions u, FileSystemResources b"
			+ " where u.group.id in (?1) and u.permission.resourceType = ? and u.permission.resourceId = b.id";	
		return (List<FileSystemResources>)findAllBySql(sql, para, PermissionConst.FILE_RESOURCE);
	}
	
	/**
	 * 得到组织拥有权限的资源
	 * @param orgId
	 * @return
	 */
	public List<FileSystemResources> getOrganizationFileSystemResource(long orgId)
	{
		//String sql = "select distinct b from OrganizationsPermissions u, FileSystemResources b"
		//	+ " where u.organization.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)";	
		String sql = "select distinct b from OrganizationsPermissions u, FileSystemResources b"
			+ " where u.organization.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id";	
		return (List<FileSystemResources>)findAllBySql(sql, orgId, PermissionConst.FILE_RESOURCE);
	}
	
	/**
	 * 得到组织及父组织所拥有权限的资源。
	 * 在得父组织的时候，会递归得到所有级别的父组织，直到根为止。	
	 * @param groupId
	 * @return
	 */
	public List<FileSystemResources> getOrganizationTreeFileSystemResource(long orgId)
	{
		String sql = "select g.parentKey from Organizations g where g.id = ? ";
		List<String> temp = findAllBySql(sql, orgId);
		List<Long> para = new ArrayList<Long>(); 
		if (temp != null && temp.size() > 0)
		{
			String[] sa;
			for (String s : temp)
			{
				sa = s.split("-");
				for (String  a : sa)
				{
					try
					{
						Long l = new Long(a.trim());
						para.add(l);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		para.add(orgId);
		
		//sql = "select distinct b from OrganizationsPermissions u, FileSystemResources b"
		//	+ " where u.organization.id in (?1) and u.permission.resourceContent = " + FILERESOURCE + "b.id)";	
		sql = "select distinct b from OrganizationsPermissions u, FileSystemResources b"
			+ " where u.organization.id in (?1) and u.permission.resourceType = ? and u.permission.resourceId = b.id";
		return (List<FileSystemResources>)findAllBySql(sql, para, PermissionConst.FILE_RESOURCE);
	}
		
	/**
	 * 得到用户所拥有的权限。
	 * @param path 资源路径
	 * @return
	 */
	public List<UsersPermissions> getUserFileSysPermissions(String path)
	{
		//String sql = "select new UsersPermissions(u.user, u.permission, a, b) from UsersPermissions u, " 
		//		+ " FileSystemActions a, FileSystemResources b"
		//		+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new UsersPermissions(u.user, u.permission, a, b) from UsersPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<UsersPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到用户所拥有的权限。
	 * @param userId
	 * @return
	 */
	public List<Permissions> getUserFileSysPermissions(long userId)
	{
		//String sql = "select new Permissions(u.permission, a, b) from UsersPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where u.user.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new Permissions(u.permission, a, b) from UsersPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<Permissions>)findAllBySql(sql, userId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到角色所拥有的权限。
	 * @param path
	 * @return
	 */
	public List<RolesPermissions> getRoleFileSysPermissions(String path)
	{
		//String sql = "select new RolesPermissions(u.role, u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new RolesPermissions(u.role, u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<RolesPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到角色所拥有的权限。
	 * @param roleId
	 * @return
	 */
	public List<Permissions> getRoleFileSysPermissions(long roleId)
	{
		//String sql = "select new Permissions(u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where u.role.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new Permissions(u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.role.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId a.id";
		return (List<Permissions>)findAllBySql(sql, roleId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到组所拥有的权限。
	 * @param path
	 * @return
	 */
	public List<GroupsPermissions> getGroupFileSysPermissions(String path)
	{
		//String sql = "select new GroupsPermissions(u.group, u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new GroupsPermissions(u.group, u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<GroupsPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到组所拥有的权限。
	 * @param groupId
	 * @return
	 */
	public List<Permissions> getGroupFileSysPermissions(long groupId)
	{
		//String sql = "select new Permissions(u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where u.group.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new Permissions(u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.group.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<Permissions>)findAllBySql(sql, groupId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到组及父组所拥有的权限。
	 * 在得父组的时候，会递归得到所有级别的父组，直到根为止。	
	 * @param groupId
	 * @return
	 */
	public List<Permissions> getGroupTreeFileSysPermissions(long groupId)
	{
		String sql = "select g.parentKey from Groups g where g.id = ? ";
		List<String> temp = findAllBySql(sql, groupId);
		List<Long> para = new ArrayList<Long>(); 
		if (temp != null && temp.size() > 0)
		{
			String[] sa;
			for (String s : temp)
			{
				sa = s.split("-");
				for (String  a : sa)
				{
					try
					{
						Long l = new Long(a.trim());
						para.add(l);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		para.add(groupId);
		
		//sql = "select new Permissions(u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where u.group.id in (?1) and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		sql = "select new Permissions(u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.group.id in (?1) and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<Permissions>)findAllBySql(sql, para, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到组织所拥有的权限。
	 * @param groupId
	 * @return
	 */
	public List<Permissions> getOrganizationFileSysPermissions(long orgId)
	{
		//String sql = "select new Permissions(u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"			
		//	+ " where u.organization.id = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new Permissions(u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.organization.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<Permissions>)findAllBySql(sql, orgId, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	
	/**
	 * 得到组织所拥有的权限。
	 * @param path
	 * @return
	 */
	public List<OrganizationsPermissions> getOrganizationFileSysPermissions(String path)
	{
		//String sql = "select new OrganizationsPermissions(u.organization, u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new OrganizationsPermissions(u.organization, u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<OrganizationsPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}
	
	/**
	 * 得到path文件资源所赋予给所有拥有则的权限，包括赋予给用户，赋予给组织，赋予给组，赋予给用户自定义组，赋予给角色等
	 * 的权限。
	 * @param path
	 * @return
	 */
	public List<IPermissions> getAllFileSystemPermission(String path)
	{
		List<IPermissions> ret = new ArrayList<IPermissions>();
		// 先分别得，后续根据性能情况改进。
		//String sql = "select new UsersPermissions(u.user, u.permission, a, b) from UsersPermissions u, " 
		//	+ " FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		String sql = "select new UsersPermissions(u.user, u.permission, a, b) from UsersPermissions u,  FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		ret.addAll((List<UsersPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION));
		
		//sql = "select new RolesPermissions(u.role, u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		sql = "select new RolesPermissions(u.role, u.permission, a, b) from RolesPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id "
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		ret.addAll((List<RolesPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION));
		
		//sql = "select new GroupsPermissions(u.group, u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		sql = "select new GroupsPermissions(u.group, u.permission, a, b) from GroupsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		ret.addAll((List<GroupsPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION));
		
		//sql = "select new OrganizationsPermissions(u.organization, u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where b.abstractPath = ? and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ FILEACTION + "a.id)";
		sql = "select new OrganizationsPermissions(u.organization, u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where b.abstractPath = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		ret.addAll((List<OrganizationsPermissions>)findAllBySql(sql, path, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION));
		
		return ret;
	}
	
	/**
	 * 得到组织及父组织所拥有的权限。
	 * 在得父组织的时候，会递归得到所有级别的父组织，直到根为止。	
	 * @param groupId
	 * @return
	 */
	public List<Permissions> getOrganizationTreeFileSysPermissions(long orgId)
	{
		String sql = "select g.parentKey from Organizations g where g.id = ? ";
		List<String> temp = findAllBySql(sql, orgId);
		List<Long> para = new ArrayList<Long>(); 
		if (temp != null && temp.size() > 0)
		{
			String[] sa;
			for (String s : temp)
			{
				sa = s.split("-");
				for (String  a : sa)
				{
					try
					{
						Long l = new Long(a.trim());
						para.add(l);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		para.add(orgId);
		
		//sql = "select new Permissions(u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
		//	+ " where u.organization.id in (?1) and u.permission.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + FILEACTION + "a.id)";
		sql = "select new Permissions(u.permission, a, b) from OrganizationsPermissions u, FileSystemActions a, FileSystemResources b"
			+ " where u.organization.id in (?1) and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		return (List<Permissions>)findAllBySql(sql, para, PermissionConst.FILE_RESOURCE, PermissionConst.FILE_ACTION);
	}

	/**
	 * 得到预定义的角色对文件系统的action及系统管理的action。
	 * @param roleId
	 * @return
	 */
	public List<Actions> getDefinedRoleAction(long roleId)
	{
		List<Actions> ret = new ArrayList<Actions>();
		//String sql = "select a from RolesActions  ra, FileSystemActions a"
		//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
		String sql = "select a from RolesActions  ra, FileSystemActions a"
			+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
		List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);		
		ret.addAll(temp);
		
		//sql = "select a from RolesActions  ra, SystemManageActions a"
		//	+ " where ra.role.id = ? and ra.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select a from RolesActions  ra, SystemManageActions a"
			+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
		temp = findAllBySql(sql, roleId, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		//sql = "select a from RolesActions  ra, SpacesActions a"
		//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
		sql = "select a from RolesActions  ra, SpacesActions a"
			+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
		temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
		ret.addAll(temp);
		
		return ret;
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	@Deprecated
	public void copySystemRoleActionToGroup(long groupId)
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null and model.type = " + RoleCons.SPACE;
		List<Roles> roles =  findAllBySql(queryString.toString());
		long roleId;
		Groups group = (Groups)find(Groups.class, groupId);
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id ";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setGroup(group);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	@Deprecated
	public void copySystemRoleActionToOrg(long orgId)
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null  and model.type = " + RoleCons.SPACE;
		List<Roles> roles =  findAllBySql(queryString.toString());
		long roleId;
		Organizations org = (Organizations)find(Organizations.class, orgId);
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setOrganization(org);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	@Deprecated
	public void copySystemRoleActionToTeam(long teamId)
	{
		//此方法设计上以及逻辑上都存在问题 FileSystemActions和SpacesActions只应该放默认权限，
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null  and model.type = " + RoleCons.SPACE;
		List<Roles> roles =  findAllBySql(queryString.toString());
		long roleId;
		CustomTeams team = (CustomTeams)find(CustomTeams.class, teamId);
		int i=0;
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			if (temp==null || temp.size()==0)
			{
				//插入FileSystemActions和RolesActions
				
			}
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setTeam(team);
			if (i==0)//临时
			{
				role.setAction(0L);//插入默认值
			}
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	public void copySystemRoleActionToCompany(long companyId)
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null";
		List<Roles> roles =  findAllBySql(queryString.toString());
		long roleId;
		Company company = (Company)find(Company.class, companyId);
		for (Roles role : roles)
		{
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, SystemManageActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + MANAGEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, SystemManageActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.MANAGEMENT_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);			
						
			role = role.clone();			
			role.setCompany(company);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	public void copySystemRoleActionToGroup(long groupId, long companyId)
	{
		String queryString = "from Roles as model where model.company.id = ? and model.organization is null" 
				 + " and  model.group is null and model.team is null and model.type = ? ";
		List<Roles> roles =  findAllBySql(queryString.toString(), companyId, RoleCons.SPACE);
		long roleId;
		Groups group = (Groups)find(Groups.class, groupId);
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setGroup(group);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	public void copySystemRoleActionToOrg(long orgId, long companyId)
	{
		String queryString = "from Roles as model where model.company.id = ? and model.organization is null "
				+ " and  model.group is null and model.team is null  and model.type = ? ";
		List<Roles> roles =  findAllBySql(queryString.toString(), companyId, RoleCons.SPACE);
		long roleId;
		Organizations org = (Organizations)find(Organizations.class, orgId);
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setOrganization(org);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	// 根据目前的需求，全局的角色定义仅仅是字典定义。
	public void copySystemRoleActionToTeam(long teamId, long companyId)
	{
		String queryString = "from Roles as model where model.company.id = ? and model.organization is null "
				 + " and  model.group is null and model.team is null  and model.type = ? ";
		List<Roles> roles =  findAllBySql(queryString.toString(), companyId, RoleCons.SPACE);
		long roleId;
		CustomTeams team = (CustomTeams)find(CustomTeams.class, teamId);
		for (Roles role : roles)
		{ 
			List<Actions> ret = new ArrayList<Actions>();
			roleId = role.getRoleId();	
			//String sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			String sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			List<Actions> temp = findAllBySql(sql, roleId, PermissionConst.FILE_ACTION);
			ret.addAll(temp);
			
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = findAllBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			ret.addAll(temp);
			
			role = role.clone();			
			role.setTeam(team);
			save(role);
			for (Actions ac : ret)
			{
				ac = ac.getClone();
				save(ac);
				RolesActions ra = new RolesActions();
				ra.setRole(role);
				ra.setAction(ac);
				save(ra);
			}	
			
		}
	}
	
	/**
	 * 增加或定义角色的预定义action。	
	 * @param roleId
	 * @param actions
	 */
	public void addOrUpdateDefinedRoleAction(long roleId, List<Actions> actions)
	{
		Actions a;
		Roles role = (Roles)find(Roles.class, roleId);
		for (Actions temp : actions)
		{
			if (temp.getId() != null)
			{
				a = (Actions)find(temp.getClass(), temp.getId());
				a.update(temp);
				update(temp);
			}
			else
			{
				/*String sql = " from RolesActions ra where ra.role.id = ? and ra.actionContent like " + temp.getActionType() + "%";
				List<RolesActions> ret = findAllBySql(sql, roleId);
				if (ret != null && ret.size() > 0)
				{
					
				}
				else*/
				{
					save(temp);
					RolesActions ra = new RolesActions();
					ra.setRole(role);
					ra.setAction(temp);
					save(ra);
				}
			}
		}
	}
	
	/**
	 * 增加或定义角色的预定义action。	type为0表示系统权限，1为空间权限，2为文件权限
	 * @param roleId
	 * @param actions
	 */
	public void updateDefinedRoleAction(long roleId, long permission, int type)
	{
		Roles role = (Roles)find(Roles.class, roleId);
		Actions temp = null;
		String sql;
		boolean update = false;
		if (type == 0)
		{
			//sql = "select a from RolesActions  ra, SystemManageActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + MANAGEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SystemManageActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = (Actions)findOneObjectBySql(sql, roleId, PermissionConst.MANAGEMENT_ACTION);
			if (temp != null)
			{
				((SystemManageActions)temp).setAction(permission);
				update = true;
			}
			else
			{
				temp = new SystemManageActions(permission, role.getRoleName());
			}
		}
		else if (type == 2)
		{
			//sql = "select a from RolesActions  ra, FileSystemActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + FILEACTION + "a.id)";
			sql = "select a from RolesActions  ra, FileSystemActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = (FileSystemActions)findOneObjectBySql(sql, roleId, PermissionConst.FILE_ACTION);
			if (temp != null)
			{
				((FileSystemActions)temp).setAction(permission);
				update = true;
			}
			else
			{
				temp = new FileSystemActions(permission, role.getRoleName());
			}
		}
		else if (type == 1)
		{
			//sql = "select a from RolesActions  ra, SpacesActions a"
			//	+ " where ra.role.id = ? and ra.actionContent = " + SPACEACTION + "a.id)";
			sql = "select a from RolesActions  ra, SpacesActions a"
				+ " where ra.role.id = ? and ra.actionType = ? and ra.actionId = a.id";
			temp = (SpacesActions)findOneObjectBySql(sql, roleId, PermissionConst.SPACE_ACTION);
			if (temp != null)
			{
				((SpacesActions)temp).setAction(permission);
				update = true;
			}
			else
			{
				temp = new SpacesActions(permission, role.getRoleName());
			}
		}
		else
		{
			return;
		}
		if (update)
		{
			update(temp);
		}
		else
		{
			save(temp);
			RolesActions ra = new RolesActions();
			ra.setRole(role);
			ra.setAction(temp);
			save(ra);
		}
	}
	
	/**
	 * 得到用户对系统资源空间所拥有的action。
	 * @param userId 用户id
	 * @return
	 */
	public List<SystemManageActions> getUserSystemManageActions(long userId)
	{
		//String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)";		
		String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";		
		return (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
	}
	
	/**
	 * 得到用户对系统资源空间所拥有的action。该方法会得到用户所在的组织，组，以及用户拥有的角色拥有的action
	 * @param userId 用户id
	 * @return
	 */
	public List<SystemManageActions> getSystemManageActionsByUserId(long userId)
	{
		List<SystemManageActions> ret = new ArrayList<SystemManageActions>();
		// 先分别获取，后续再看采用union获取是否有性能优化
		//String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)";
		String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";	
		List temp = findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		//sql = "select distinct a from RolesActions  ra, SystemManageActions a, UsersRoles u"
		//	+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct a from RolesActions  ra, SystemManageActions a, UsersRoles u"
			+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionType = ? and ra.actionId = a.id";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// role
		//sql = "select distinct a from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ur.user.id = ? and ur.role.id = u.role.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct a from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ur.user.id = ? and ur.role.id = u.role.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// group
		//sql = "select distinct a from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ug.user.id = ? and ug.group.id = u.group.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct a from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ug.user.id = ? and ug.group.id = u.group.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// organization
		//sql = "select distinct a from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where uo.user.id = ? and uo.organization.id = u.organization.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)";
		sql = "select distinct a from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where uo.user.id = ? and uo.organization.id = u.organization.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		return ret;		
	}
	
	/**
	 * 得到所有有审批权限的用户
	 * @return
	 */
	public List<Users> getAllAuditUser(Long companyId)
	{
		long permission = ManagementCons.AUDIT_AUDIT_FLAG;
		List<Users> retAll = new ArrayList<Users>();
		List<UsersPermissions> ret = new ArrayList<UsersPermissions>();
		// 先分别获取，后续再看采用union获取是否有性能优化
		//String sql = "select distinct new UsersPermissions(u.user, a.action) from UsersPermissions u, SystemManageActions a, SystemManageResources b"
		//		+ " where u.user.company.id = ? and a.action >= ? and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)";
		String sql = "select distinct new UsersPermissions(u.user, a.action) from UsersPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where u.user.company.id = ? and a.action >= ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		List temp = findAllBySql(sql, companyId, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		//sql = "select distinct new UsersPermissions(u.user, a.action) from RolesActions  ra, SystemManageActions a, UsersRoles u"
		//	+ " where u.user.company.id = ? and a.action >= ? and ra.role.id = u.role.id and ra.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct new UsersPermissions(u.user, a.action) from RolesActions  ra, SystemManageActions a, UsersRoles u"
			+ " where u.user.company.id = ? and a.action >= ? and ra.role.id = u.role.id and ra.actionType = ? and ra.actionId = a.id";
		temp = (List<Users>)findAllBySql(sql, companyId, permission, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// role
		//sql = "select distinct new UsersPermissions(ur.user, a.action) from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ur.user.company.id = ? and a.action >= ? and ur.role.id = u.role.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct new UsersPermissions(ur.user, a.action) from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ur.user.company.id = ? and a.action >= ? and ur.role.id = u.role.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<Users>)findAllBySql(sql, companyId, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// group
		//sql = "select distinct new UsersPermissions(ug.user, a.action) from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ug.user.company.id = ? and a.action >= ? and ug.group.id = u.group.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct new UsersPermissions(ug.user, a.action) from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ug.user.company.id = ? and a.action >= ? and ug.group.id = u.group.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<Users>)findAllBySql(sql, companyId, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		// organization
		//sql = "select distinct new UsersPermissions(uo.user, a.action) from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where uo.user.company.id = ? and a.action >= ? and uo.organization.id = u.organization.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)";
		sql = "select distinct new UsersPermissions(uo.user, a.action) from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where uo.user.company.id = ? and a.action >= ? and uo.organization.id = u.organization.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id";
		temp = (List<Users>)findAllBySql(sql, companyId, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
		
		HashMap tempM = new HashMap();
		for (UsersPermissions up : ret)
		{
			if (FlagUtility.isValue(up.getId(), ManagementCons.AUDIT_AUDIT_FLAG))
			{
				tempM.put(up.getUser().getId(), up.getUser());
			}
		}
		retAll.addAll(tempM.values());
		
		return retAll;		
	}
	
	/**
	 * 得到同部门下所有有审批权限的用户
	 * @return
	 */
	public List<Users> getAuditUserByOrgId(Long orgId)
	{
		long permission = ManagementCons.AUDIT_AUDIT_FLAG;
		List<Users> retAll = new ArrayList<Users>();
		List<UsersPermissions> ret = new ArrayList<UsersPermissions>();
		// 先分别获取，后续再看采用union获取是否有性能优化
		//String sql = "select distinct new UsersPermissions(u.user, a.action) from UsersPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
		//		+ " where a.action >= ? and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id) and u.user.id = uo.user.id and uo.organization.id = ? ";		
		String sql = "select distinct new UsersPermissions(u.user, a.action) from UsersPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
			+ " where a.action >= ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and u.user.id = uo.user.id and uo.organization.id = ? ";	
		List temp = findAllBySql(sql, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, orgId);
		ret.addAll(temp);
		
		//sql = "select distinct new UsersPermissions(u.user, a.action) from RolesActions  ra, SystemManageActions a, UsersRoles u, UsersOrganizations uo"
		//	+ " where a.action >= ? and ra.role.id = u.role.id and ra.actionContent = " + MANAGEACTION + "a.id)  and u.user.id = uo.user.id and uo.organization.id = ? ";
		sql = "select distinct new UsersPermissions(u.user, a.action) from RolesActions  ra, SystemManageActions a, UsersRoles u, UsersOrganizations uo"
			+ " where a.action >= ? and ra.role.id = u.role.id and ra.actionType = ? and ra.actionId = a.id  and u.user.id = uo.user.id and uo.organization.id = ? ";
		temp = (List<Users>)findAllBySql(sql, permission, PermissionConst.MANAGEMENT_ACTION, orgId);
		ret.addAll(temp);
		
		// role
		//sql = "select distinct new UsersPermissions(ur.user, a.action) from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
		//	+ " where a.action >= ? and ur.role.id = u.role.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id) and ur.user.id = uo.user.id and uo.organization.id = ? ";
		sql = "select distinct new UsersPermissions(ur.user, a.action) from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
			+ " where a.action >= ? and ur.role.id = u.role.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and ur.user.id = uo.user.id and uo.organization.id = ? ";
		temp = (List<Users>)findAllBySql(sql, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, orgId);
		ret.addAll(temp);
		
		// group
		//sql = "select distinct new UsersPermissions(ug.user, a.action) from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
		//	+ " where a.action >= ? and ug.group.id = u.group.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id) and ug.user.id = uo.user.id and uo.organization.id = ? ";
		sql = "select distinct new UsersPermissions(ug.user, a.action) from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b, UsersOrganizations uo"
			+ " where a.action >= ? and ug.group.id = u.group.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and ug.user.id = uo.user.id and uo.organization.id = ? ";
		temp = (List<Users>)findAllBySql(sql, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, orgId);
		ret.addAll(temp);
		
		// organization
		//sql = "select distinct new UsersPermissions(uo.user, a.action) from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where a.action >= ? and uo.organization.id = u.organization.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id) and uo.organization.id = ? ";
		sql = "select distinct new UsersPermissions(uo.user, a.action) from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where a.action >= ? and uo.organization.id = u.organization.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and uo.organization.id = ? ";
		temp = (List<Users>)findAllBySql(sql, permission, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, orgId);
		ret.addAll(temp);
		
		HashMap tempM = new HashMap();
		for (UsersPermissions up : ret)
		{
			if (FlagUtility.isValue(up.getId(), ManagementCons.AUDIT_AUDIT_FLAG))
			{
				tempM.put(up.getUser().getId(), up.getUser());
			}
		}
		retAll.addAll(tempM.values());
		
		return retAll;		
	}
	
	/**
	 * 得到用户所在角色的文件系统权限。
	 * @param userId
	 * @return
	 */
	public List<FileSystemActions> getRoleFileSystemActionByUserId(Long userId)
	{
		//String sql = "select distinct a from RolesActions  ra, FileSystemActions a, UsersRoles u"
		//	+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionContent = " + FILEACTION + "a.id)";
		String sql = "select distinct a from RolesActions  ra, FileSystemActions a, UsersRoles u"
			+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionType = ? and ra.actionId = a.id";
		return findAllBySql(sql, userId, PermissionConst.FILE_ACTION);
		
	}
	
	/**
	 * 得到用户所在角色的系统管理权限。
	 * @param userId
	 * @return
	 */
	public List<SystemManageActions> getRoleSystemManageActionByUserId(Long userId)
	{
		//String sql = "select distinct a from RolesActions  ra, SystemManageActions a, UsersRoles u"
		//	+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionContent = " + MANAGEACTION + "a.id)";
		String sql = "select distinct a from RolesActions  ra, SystemManageActions a, UsersRoles u"
			+ " where u.user.id = ? and ra.role.id = u.role.id and ra.actionType = ? and ra.actionId = a.id";
		return findAllBySql(sql, userId, PermissionConst.MANAGEMENT_ACTION);
	}
	
	/**
	 * 得到用户对公司空间所拥有的action。
	 * @param userId 用户id
	 * @return
	 */
	public List<SpacesActions> getCompanySpacesActionsByUserId(long userId)
	{
		//String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + SPACEACTION + "a.id)" 
		//	+ " and gra.role.company.id = u.user.company.id";
		String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id" 
			+ " and gra.role.company.id = u.user.company.id";
		return (List<SpacesActions>)findAllBySql(sql, userId, PermissionConst.SPACE_ACTION);
	}
	
	/**
	 * 得到用户对组织空间所拥有的action。
	 * @param userId 用户id
	 * @return
	 */
	public List<SpacesActions> getOrgSpacesActionsByUserId(long userId, Long orgId)
	{
		//String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + SPACEACTION + "a.id)" 
		//	+ " and gra.role.organization.id = ?";
		String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id" 
			+ " and gra.role.organization.id = ?";
		return (List<SpacesActions>)findAllBySql(sql, userId, PermissionConst.SPACE_ACTION, orgId);
	}
	
	/**
	 * 得到用户对空间所拥有的action。
	 * @param userId 用户id
	 * @return
	 */
	public List<SpacesActions> getGroupSpacesActionsByUserId(long userId, Long groupId)
	{
		//String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + SPACEACTION + "a.id)" 
		//	+ " and gra.role.group.id = ?";
		String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id" 
			+ " and gra.role.group.id = ?";
		return (List<SpacesActions>)findAllBySql(sql, userId, PermissionConst.SPACE_ACTION, groupId);
	}
	
	/**
	 * 得到用户对用户自定义组所拥有的action。
	 * @param userId 用户id
	 * @return
	 */
	public List<SpacesActions> getTeamSpacesActionsByUserId(long userId, Long teamId)
	{
		//String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + SPACEACTION + "a.id)" 
		//	+ " and gra.role.team.id = ?";
		String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id" 
			+ " and gra.role.team.id = ?";
		return (List<SpacesActions>)findAllBySql(sql, userId, PermissionConst.SPACE_ACTION, teamId);
	}
	
	/**
	 * 得到用户对空间所拥有的action。该方法会得到用户所在的组织，组，以及用户拥有的角色拥有的action
	 * @param userId 用户id
	 * @return
	 */
	public List<SpacesActions> getSpacesActionsBySpaceUID(long userId, String spaceUID)
	{
		//String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + SPACEACTION + "a.id)" 
		//	+ " and (gra.role.organization.id in (select o.id from Organizations o where o.spaceUID = ?) "
		//	+ " or gra.role.group.id in (select g.id from Groups g where g.spaceUID = ? ))";
		String sql = "select distinct a from SpacesActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id" 
			+ " and (gra.role.organization.id in (select o.id from Organizations o where o.spaceUID = ?) "
			+ " or gra.role.group.id in (select g.id from Groups g where g.spaceUID = ? ))";
		return (List<SpacesActions>)findAllBySql(sql, userId, PermissionConst.SPACE_ACTION, spaceUID, spaceUID);
	}
	
	/**
	 * 得到用户对系统资源空间所拥有的action。该方法会得到用户所在的组织，组，以及用户拥有的角色拥有的action
	 * @param userId 用户id
	 * @return
	 */
	public List<SystemManageActions> getSystemManageActionsByUserId(long userId, String content)
	{
		List<SystemManageActions> ret = new ArrayList<SystemManageActions>();
		// 先分别获取，后续再看采用union获取是否有性能优化
		//String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
		//		+ " where u.user.id = ? and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//		+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)"
		//		+ " and b.content = ? ";
		String sql = "select distinct a from UsersPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where u.user.id = ? and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id"
			+ " and b.content = ? ";
		List temp = findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, content);
		ret.addAll(temp);
		
		//sql = "select distinct a from SystemManageActions a, UsersRoles u, RolesActions gra"
		//	+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionContent = " + MANAGEACTION + "a.id)";
		sql = "select distinct a from SystemManageActions a, UsersRoles u, RolesActions gra"
			+ " where u.user.id = ? and gra.role.id = u.role.id and gra.actionType = ? and gra.actionId = a.id";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_ACTION);
		ret.addAll(temp);
				
		// role
		//sql = "select distinct a from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ur.user.id = ? and ur.role.id = u.role.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)"
		//	+ " and b.content = ? ";
		sql = "select distinct a from UsersRoles ur, RolesPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ur.user.id = ? and ur.role.id = u.role.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.content = ? ";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, content);
		ret.addAll(temp);
		
		// group
		//sql = "select distinct a from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where ug.user.id = ? and ug.group.id = u.group.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = " + MANAGEACTION + "a.id)"
		//	+ " and b.content = ? ";
		sql = "select distinct a from UsersGroups ug, GroupsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where ug.user.id = ? and ug.group.id = u.group.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.content = ? ";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, content);
		ret.addAll(temp);
		
		// organization
		//sql = "select distinct a from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
		//	+ " where uo.user.id = ? and uo.organization.id = u.organization.id and u.permission.resourceContent = " + MANAGERESOURCE + "b.id)"
		//	+ " and u.permission.actionContent = "	+ MANAGEACTION + "a.id)"
		//	+ " and b.content = ? ";
		sql = "select distinct a from UsersOrganizations uo, OrganizationsPermissions u, SystemManageActions a, SystemManageResources b"
			+ " where uo.user.id = ? and uo.organization.id = u.organization.id and u.permission.resourceType = ? and u.permission.resourceId = b.id"
			+ " and u.permission.actionType = ? and u.permission.actionId = a.id and b.content = ? ";
		temp = (List<SystemManageActions>)findAllBySql(sql, userId, PermissionConst.MANAGEMENT_RESOURCE, PermissionConst.MANAGEMENT_ACTION, content);
		ret.addAll(temp);
		
		return ret;		
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
	public void addOrUpdateFileSystemPermission(final FileSystemResources resource, final FileSystemActions action,
			final List<Long> addUserIds, final List<Long> delUserIds, final List<Long> addGroupIds, 
			final List<Long> delGroupIds, final List<Long> addRoleIds, final List<Long> delRoleIds,
			final List<Long> addOrgIds, final List<Long> delOrgIds, final List<Long> addTeamIds, 
			final List<Long> delTeamIds)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {	        
	            	//String sql = "select p from Permissions p where p.resourceContent = " + FILERESOURCE + " ? )"
	        		//	+ " and p.actionContent = "	+ FILEACTION + " ? )";
	            	String sql = "select p from Permissions p where p.resourceType = ? and p.resourceId =  ? "
        			+ " and p.actionType = ? and p.actionId =  ? ";
	        		List<Permissions> tempP = (List<Permissions>)findAllBySql(sql, PermissionConst.FILE_RESOURCE, resource.getId(), PermissionConst.FILE_ACTION, action.getId());
	        		Permissions p;
	        		if (tempP != null && tempP.size() > 0)
	        		{
	        			p = tempP.get(0);
	        		}
	        		else
	        		{
		            	p = new Permissions();	            	
		            	p.setActionContent(action);
		            	p.setResourceContent(resource);
		            	em.persist(p);
	        		}
	            	
	        		if (addUserIds != null && addUserIds.size() > 0)
	        		{
		            	UsersPermissions up;
		            	Users user;
		            	for (long temp : addUserIds)
		            	{
		            		user = em.find(Users.class, temp);
		            		up = new UsersPermissions();
		            		up.setPermission(p);
		            		up.setUser(user);
		            		em.persist(up);
		            	}
	        		}
	        		if (delUserIds != null && delUserIds.size() > 0)
	        		{
	        			sql = "delete UsersPermissions up where up.user.id in (?1) ";
	        			excute(sql, delUserIds);
	        		}
	        		
	        		if (addRoleIds != null)
	        		{
		            	RolesPermissions up;
		            	Roles role;
		            	for (long temp : addRoleIds)
		            	{
		            		role = em.find(Roles.class, temp);
		            		up = new RolesPermissions();
		            		up.setPermission(p);
		            		up.setRole(role);
		            		em.persist(up);
		            	}
	        		}
	        		
	        		if (delRoleIds != null && delRoleIds.size() > 0)
	        		{
	        			sql = "delete RolesPermissions up where up.role.id in (?1) ";
	        			excute(sql, delRoleIds);
	        		}
	        		
	        		if (addTeamIds != null)
	        		{
		            	CustomTeamPermissions up;
		            	CustomTeams team;
		            	for (long temp : addTeamIds)
		            	{
		            		team = em.find(CustomTeams.class, temp);
		            		up = new CustomTeamPermissions();
		            		up.setPermission(p);
		            		up.setTeam(team);
		            		em.persist(up);
		            	}
	        		}
	        		
	        		if (delTeamIds != null && delTeamIds.size() > 0)
	        		{
	        			sql = "delete CustomTeamPermissions up where up.team.id in (?1) ";
	        			excute(sql, delTeamIds);
	        		}
	        		
	        		if (addGroupIds != null)
	        		{
		            	GroupsPermissions up;
		            	Groups group;
		            	for (long temp : addGroupIds)
		            	{
		            		group = em.find(Groups.class, temp);
		            		up = new GroupsPermissions();
		            		up.setPermission(p);
		            		up.setGroup(group);
		            		em.persist(up);
		            	}
	        		}
	        		
	        		if (delGroupIds != null && delGroupIds.size() > 0)
	        		{
	        			sql = "delete GroupsPermissions up where up.group.id in (?1) ";
	        			excute(sql, delGroupIds);
	        		}
	        		
	        		if (addOrgIds != null)
	        		{
		            	OrganizationsPermissions up;
		            	Organizations org;
		            	for (long temp : addOrgIds)
		            	{
		            		org = em.find(Organizations.class, temp);
		            		up = new OrganizationsPermissions();
		            		up.setPermission(p);
		            		up.setOrganization(org);
		            		em.persist(up);
		            	}
	        		}
	        		
	        		if (delOrgIds != null && delOrgIds.size() > 0)
	        		{
	        			sql = "delete OrganizationsPermissions up where up.organization.id in (?1) ";
	        			excute(sql, delOrgIds);
	        		}
	            	
	            	return p;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 删除用户对文件资源所拥有的所有action。 
	 * @param filePath 文件path
	 * @return
	 */  // 还需要处理同角色，组织，组的关系。
	public void deletFileSysActionsByUser(List<Long> delUserIds, String filePath)
	{
		// 先如此分步处理，后续用union来处理。
		
		// 删除关联的permission
		/*String sql = " delete Permissions where id in ("
				+ "select per.id from Permissions per, FileSystemResources b"
				+ " where per.resourceContent = " + FILERESOURCE + "b.id)"
				+ " and b.abstractPath = ?) ";
		excute(sql, delPath);*/
		// 由于mysql不支持该操作，只有先查出，再删了。
		//String sql = "select distinct per from Permissions per, FileSystemResources b"
		//	+ " where per.resourceContent = " + FILERESOURCE + "b.id)"
		//	+ " and b.abstractPath = ? ";
		String sql = "select distinct per from Permissions per, FileSystemResources b"
			+ " where per.resourceType = ? and per.resourceId = b.id"
			+ " and b.abstractPath = ? ";
		List<Permissions> ret = findAllBySql(sql, PermissionConst.FILE_RESOURCE, filePath);
		//boolean delFlag;		
		long size;
		for (Permissions p : ret)
		{			
			//delFlag = false;
			sql = " select up from UsersPermissions up where up.user.id in (?1) and up.permission.id = ?2 ";
			List temp = findAllBySql(sql, delUserIds, p.getId());
			delete(temp);     // 删除用户对资源的权限关系。
			
			sql = " select count(*) from UsersPermissions up where up.permission.id = ? ";
			size = getCountBySql(sql, p.getId());       //  还需要判断，角色，组织，组等的引用
			if (size <= 0)
			{
				//delFlag = true;
				delete(p);
			}
			//if (delFlag)
			{				
				// 删除filesystemresource				
				/*sql = " select fr from FileSystemResources fr where fr.id not in (" 
						 + " select b.id from FileSystemResources b, Permissions per"
						 + " where per.resourceContent = " + FILERESOURCE + "b.id)"
						 + " and b.abstractPath = ? ) and fr.abstractPath = ? ";
				temp = findAllBySql(sql, filePath, filePath);
				delete(temp);*/
			}
		}
		
		//  删除关联的action
		/*sql = " delete FileSystemActions  where id not in ("
			+ "select a.id from Permissions per, FileSystemActions a"
			+ " where per.actionContent = "	+ FILEACTION + "a.id) "
			+ " union select aa.id from RolesActions gra, FileSystemActions aa "
			+ " where gra.actionContent = " + FILEACTION + "aa.id))";
		excute(sql);*/
		/*sql = " select ac from FileSystemActions ac where ac.id not in ("
			+ "select a.id from Permissions per, FileSystemActions a, RolesActions gra"
			+ " where per.actionContent = "	+ FILEACTION + "a.id)"
			+ " or gra.actionContent = " + FILEACTION + "a.id))";
		ret = findAllBySql(sql);
		delete(ret);*/

		// roleaction中先不处理，现在需求还没有这样。				
	}
	
	/**
	 * 删除孤立的文件系统资源与文件权限记录。
	 * @param path
	 */
	private void deletFileSysActions(String path)
	{
		//String sql = " select fr from FileSystemResources fr where fr.id not in (" 
		//	 + " select distinct b.id from FileSystemResources b, Permissions per"
		//	 + " where per.resourceContent = " + FILERESOURCE + "b.id)"
		//	 + " and b.abstractPath = ? ) and fr.abstractPath = ? ";
		String sql = " select fr from FileSystemResources fr where fr.id not in (" 
			 + " select distinct b.id from FileSystemResources b, Permissions per"
			 + " where per.resourceType = ? and per.resourceId = b.id and b.abstractPath = ? ) and fr.abstractPath = ? ";
		List temp = findAllBySql(sql, PermissionConst.FILE_RESOURCE, path, path);
		delete(temp);
				
		//sql =  "select distinct a.id from Permissions per, FileSystemActions a"
		//		+ " where per.actionContent = "	+ FILEACTION + "a.id)";
		sql =  "select distinct a.id from Permissions per, FileSystemActions a"
			+ " where per.actionType = ? and per.actionId = a.id";
		temp = findAllBySql(sql, PermissionConst.FILE_ACTION);
		
		//sql = " select distinct a.id from FileSystemActions a, RolesActions gra"
		//		+ " where gra.actionContent = " + FILEACTION + "a.id)";
		sql = " select distinct a.id from FileSystemActions a, RolesActions gra"
			+ " where gra.actionType = ? and gra.actionId = a.id";
		temp.addAll(findAllBySql(sql, PermissionConst.FILE_ACTION));
		
		if (temp.size() > 0)
		{
			sql = " delete from FileSystemActions ac where ac.id not in (?1)";
			excute(sql, temp);
		}
	}
	
	/**
	 * 对文件资源增加或删除相应的用户、组织、用户组、角色等的权限
	 * @param resource 文件系统资源。如果该资源是新设置权限，则从resource中获得的getId()为空，否则是对已经设置过
	 * 的资源在进行一定的权限，及用户修改。
	 * @param ownId, 文件系统的拥有者Id。
	 * @param ownType， 文件系统拥有者类型。具体见com.evermore.weboffice.constants.both.FileSystemCons中的定义类型。
	 * @param actions 对文件系统资源设置的actions，如果是新的actions，则从中获得的getId（）为null，否则是对已经设置过
	 * 的action进行一定的修改。
	 * @param addUserIds 需要增加权限的用户id集合，无则为null。actions数组和addUserIds数组需要一一对应，即是一个actions
	 * 对应一个addUserIds值。没有这位null 
	 * @param delUserIds 需要删除权限的用户id集合，无则为null。
	 */
	public void addOrUpdateFileSystemPermission(final FileSystemResources resource, final FileSystemActions[] actions,
			final Long[] addUserIds, final List<Long> delUserIds)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	//改为先删除后添加，已达到更新某权限的效果。
	            	if (delUserIds != null && delUserIds.size() > 0)
	        		{
	            		deletFileSysActionsByUser(delUserIds, resource.getAbstractPath());
	        			//String sql = "delete UsersPermissions up where up.user.id in (?1) ";
	        			//excute(sql, delUserIds);
	        		}
	            	int size = actions.length;
	            	FileSystemActions action;
	            	FileSystemActions ac;
	            	for (int i = 0; i < size; i++)
	            	{
	            		action = actions[i];
	            		if (action.getId() == null)
		    			{
		    				save(action);
		    				ac = action;
		    			}
		    			else
		    			{
		    				ac = (FileSystemActions)find(FileSystemActions.class, action.getId());
		    				ac.update(action);
		    				update(ac);
		    			}	            		
	            		
		            	//String sql = "select p from Permissions p where p.resourceContent = " + FILERESOURCE + " ? )"
		        		//	+ " and p.actionContent = "	+ FILEACTION + " ? )";
	            		String sql = "select p from Permissions p where p.resourceType = ? and p.resourceId =  ? "
	        			+ " and p.actionType = ? and p.actionId = ? ";
		        		List<Permissions> tempP = (List<Permissions>)findAllBySql(sql, PermissionConst.FILE_RESOURCE, resource.getId(), PermissionConst.FILE_ACTION, action.getId());
		        		Permissions p;
		        		if (tempP != null && tempP.size() > 0)
		        		{
		        			p = tempP.get(0);
		        		}
		        		else
		        		{
			            	p = new Permissions();	            	
			            	p.setActionContent(action);
			            	p.setResourceContent(resource);
			            	em.persist(p);
		        		}
		            	
		        		if (addUserIds != null && addUserIds[i] != null)
		        		{
			            	UsersPermissions up;
			            	Users user = em.find(Users.class, addUserIds[i]);
			            	up = new UsersPermissions();
			            	up.setPermission(p);
			            	up.setUser(user);
			            	em.persist(up);			            	
		        		}       		
	            	}
	            	deletFileSysActions(resource.getAbstractPath());
	            	
	            	return null;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
}
