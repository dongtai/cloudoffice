package apps.transmanager.weboffice.service.dao;

import java.util.ArrayList;
import java.util.List;

import apps.moreoffice.workflow.server.WorkFlowServer;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.both.RoleCons;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersConfig;
import apps.transmanager.weboffice.databaseobject.UsersCustomTeams;
import apps.transmanager.weboffice.databaseobject.UsersGroups;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.UsersRoles;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.IParentKey;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.util.server.LogsUtility;


/**
 * 处理组织结构相关对象
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class StructureDAO extends BaseDAO 
{
	/**
	 * 保存。如果该对象实现了IParentKey接口，则会做父键保存处理。
	 */
	public void save(Object object)
	{
		if (object instanceof IParentKey)
		{
			saveWithParentKey((IParentKey)object);
		}
		else
		{
			super.save(object);
		}
				
		// 用户同步修改，先在这个统一入口处处理。
		WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
		if (webConfig.getWorkflowServiceEnable())
		{
			if (object instanceof Organizations)
			{
				WorkFlowServer pts = (WorkFlowServer)ApplicationContext.getInstance().getBean("workFlowServer");
				pts.addGroup(((Organizations)object).getName());
			}
			else if (object instanceof Users)
			{
				WorkFlowServer pts = (WorkFlowServer)ApplicationContext.getInstance().getBean("workFlowServer");	
				pts.addUser(((Users)object).getUserName());
			}
		}
		if (object instanceof Users)
		{
			//WebConfig.syncAddMailUser((Users)object);
		}
	}
	
	/**
	 * 更新。如果该对象实现了IParentKey接口，则会做父键更新处理。
	 */	
	public void update(Object object)
	{
		if (object instanceof IParentKey)
		{
			updateWithParentKey((IParentKey)object);
		}
		else
		{
			super.update(object);
		}
		
		// 用户同步修改，先在这个统一入口处处理。
		WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
		if (webConfig.getWorkflowServiceEnable())
		{
			if (object instanceof Organizations)
			{
				WorkFlowServer pts = (WorkFlowServer)ApplicationContext.getInstance().getBean("ProcessTaskServices");
				pts.addUser(((Organizations)object).getName());
			}
			else if (object instanceof Users)
			{
				WorkFlowServer pts = (WorkFlowServer)ApplicationContext.getInstance().getBean("ProcessTaskServices");	
				pts.addUser(((Users)object).getUserName());
			}
		}
		if (object instanceof Users)
		{
			//WebConfig.syncChangePass((Users)object);
		}
	}
	
	/**
	 * 删除对象	
	 * @param entity
	 */
	public void delete(Object object)
	{
		super.delete(object);
		if (object instanceof Users)
		{
			WebConfig.syncRemoveMailUser((Users)object);
		}
	}
	
	/**
	 * 保存带有父关系的对象
	 * @param object
	 */
	protected void saveWithParentKey(IParentKey object)
	{
		IParentKey p = object.getParent();
		if (p != null)
		{
			String pk = p.getParentKey();
			if (pk == null)
			{
				pk = "";
			}
			object.setParentKey(pk + p.getId() + "-");
		}
		super.save(object);
	}
	
	/**
	 * 更新带有父关系的对象
	 * @param object
	 */
	protected void updateWithParentKey(IParentKey object)
	{
		IParentKey p = object.getParent();
		
		String oldKey = object.getParentKey();
		if (oldKey == null)
		{
			oldKey = "";
		}
		oldKey += object.getId() + "-";		
		String newKey;
		if (p != null)
		{
			String pk = p.getParentKey();
			if (pk == null)
			{
				pk = "";
			}
			newKey = pk + p.getId() + "-";
			object.setParentKey(newKey);
		}
		else
		{
			object.setParentKey(null);
			newKey = "";
		}
		super.update(object);		
		
		newKey += object.getId() + "-";		
		String sql = "update " + object.getClass().getSimpleName() + " set parentKey = replace(parentKey, ?, ?) where parentKey like ? ";
		excute(sql, oldKey, newKey, oldKey + "%");		
	}
	
	/**
	 * 通过用户id查询用户对象
	 * @param id
	 * @return
	 */
	public Users findUserById(Long id)
	{
		try
		{
			Users instance = (Users) find("com.evermore.weboffice.databaseobject.Users", id);			
			return instance;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error(re);
			throw re;
		}
	}
	
	/**
	 * 通过角色id查找角色对象
	 * @param id
	 * @return
	 */
	public Roles findRoleById(Long id)
	{
		LogsUtility.debug("getting Roles instance with id: " + id);
		try
		{
			Roles instance = (Roles) find(Roles.class, id);			
			return instance;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error(re);
			throw re;
		}
	}
	
	/**
	 * 获取系统的角色
	 * @param name
	 * @return
	 */
	public Roles findSystemRoleByName(String name, long companyId)
	{
		LogsUtility.debug("getting Roles instance with name: " + name);
		try
		{
			String sql = " select r from Roles r where r.roleName = ? and r.type = ? and r.company.id = ? ";	
			return (Roles) findOneObjectBySql(sql, name, RoleCons.SYSTEM, companyId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error(re);
			throw re;
		}
	}
	
	/**
	 * 删除组织角色中的用户。该角色是属于组织的。
	 * @param userId 用户id
	 * @param orgId 组织id
	 */
	public void delUserOrgRole(Long userId, Long orgId)
	{
		String query = "delete from UsersRoles ur where ur.user.id = ? and ur.role.id in (select r.id from Roles r where r.organization.id = ?)";
		excute(query, userId, orgId);		
		/*String queryString = "select ur from UsersRoles ur, Roles role where ur.user.id = ? and role.organization.id = ? "
				+ "and role.id = ur.role.id ";
		List<UsersRoles> ret = findAllBySql(queryString, userId, orgId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/
	}
	
	/**
	 * 删除组织角色中的用户。该角色是属于组织的。
	 * @param userIds 用户id
	 * @param orgId 组织id
	 */
	public void delUserOrgRole(List<Long> userIds, Long orgId)
	{
		String query = "delete from UsersRoles ur where ur.user.id in ( ?1) and ur.role.id in (select r.id from Roles r where r.organization.id = ?2 )";
		excute(query, userIds, orgId);	
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id in( ?1 ) and gra.organization.id = ?2 and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userIds, orgId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/
	}
	
	/**
	 * 删除组角色中的用户。该角色是属于组的。
	 * @param userIds 用户id
	 * @param groupId 组id
	 */
	public void delUserGroupRole(List<Long> userIds, Long groupId)
	{
		String query = "delete from UsersRoles ur where ur.user.id in ( ?1) and ur.role.id in (select r.id from Roles r where r.group.id = ?2 )";
		excute(query, userIds, groupId);
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id in( ?1 ) and gra.group.id = ?2 and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userIds, groupId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/		
	}
	
	/**
	 * 删除组角色中的用户。该角色是属于组的。
	 * @param userIds 用户id
	 * @param groupId 组id
	 */
	public void delUserGroupRole(Long userId, Long groupId)
	{
		String query = "delete from UsersRoles ur where ur.user.id = ? and ur.role.id in (select r.id from Roles r where r.group.id = ?)";
		excute(query, userId, groupId);
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id = ? and gra.group.id = ? and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userId, groupId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/		
	}
	
	/**
	 * 删除用户自定义组角色中的用户。该角色是属于组的。
	 * @param userIds 用户id
	 * @param groupId 组id
	 */
	public void delUserTeamRole(List<Long> userIds, Long teamId)
	{
		String query = "delete from UsersRoles ur where ur.user.id in ( ?1) and ur.role.id in (select r.id from Roles r where r.team.id = ?2 )";
		excute(query, userIds, teamId);
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id in( ?1 ) and gra.group.id = ?2 and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userIds, groupId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/		
	}
	
	/**
	 * 删除用户自定义组角色中的用户。该角色是属于组的。
	 * @param userIds 用户id
	 * @param groupId 组id
	 */
	public void delUserTeamRole(Long userId, Long teamId)
	{
		String query = "delete from UsersRoles ur where ur.user.id = ? and ur.role.id in (select r.id from Roles r where r.team.id = ?)";
		excute(query, userId, teamId);
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id = ? and gra.group.id = ? and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userId, groupId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/		
	}
	
	/**
	 * 删除用户在公司中的角色。该角色是属于公司 的。
	 * @param userIds 用户id
	 * @param groupId 组id
	 */
	public void delUserCompanyRole(Long userId, Long companyId)
	{
		String query = "delete from UsersRoles ur where ur.user.id = ? and ur.role.id in (select r.id from Roles r where r.company.id = ?)";
		excute(query, userId, companyId);
		/*String queryString = "select ur from UsersRoles ur, Roles gra where ur.user.id = ? and gra.group.id = ? and ur.role.id = gra.id";
		List<UsersRoles> ret = findAllBySql(queryString, userId, groupId);
		if (ret != null && ret.size() > 0)
		{
			delete(ret);
		}*/		
	}

	
	/**
	 * 获取用户在组中的角色。
	 * @param userId
	 * @param groupId
	 * @param flag
	 * @return
	 */
	public List<Roles> getUserGroupRole(Long userId, Long groupId, boolean flag)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ? and ur.role.group.id = ? ";
		return findAllBySql(queryString, userId, groupId);
		/*List<Roles> ret = new ArrayList<Roles>();
		String queryString = "select gra from UsersRoles ur, Roles gra where ur.user.id = ? and gra.group.id = ? and gra.id = ur.role.id";
		ret.addAll(findAllBySql(queryString, userId, groupId));
		return ret;*/
	}
	
	/**
	 * 获取用户在组中的角色。
	 * @param userId
	 * @param groupId
	 * @param flag
	 * @return
	 */
	public List<Roles> getUserTeamRole(Long userId, Long teamId, boolean flag)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ? and ur.role.team.id = ? ";
		return findAllBySql(queryString, userId, teamId);
		/*List<Roles> ret = new ArrayList<Roles>();
		String queryString = "select gra from UsersRoles ur, Roles gra where ur.user.id = ? and gra.group.id = ? and gra.id = ur.role.id";
		ret.addAll(findAllBySql(queryString, userId, groupId));
		return ret;*/
	}
	
	/**
	 * 获取用户在组织中的角色	
	 * @param userId
	 * @param orgId
	 * @param flag
	 * @return
	 */
	public List<Roles> getUserOrgRole(Long userId, Long orgId, boolean flag)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ? and ur.role.organization.id = ? ";
		return findAllBySql(queryString, userId, orgId);
		/*List<Roles> ret = new ArrayList<Roles>();
		String queryString = "select gra from UsersRoles ur, Roles gra where ur.user.id = ? and gra.organization.id = ? and gra.id = ur.role.id";
		ret.addAll(findAllBySql(queryString, userId, orgId));
		return ret;*/
	}
	
	/**
	 * 通过用户对象的属性查找用户对象
	 * @param propertyName
	 * @param value
	 * @return 
	 * 该方法即将删除
	 */
	@Deprecated
	public List<Users> findUserByProperty(String propertyName, Object value)
	{
		try
		{
			return findByProperty("Users", propertyName, value);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 获取某个公司中的用户
	 * @param propertyName
	 * @param value
	 * @param companyName
	 * @return
	 */
	public List<Users> findUserByProperty(String propertyName, Object value, String companyName)
	{
		try
		{
			try
			{
				String queryString = "from Users as model where model." + propertyName + " = ? and model.company = ? ";
				return getJpaTemplate().find(queryString, value, companyName);
			}
			catch (RuntimeException re)
			{
				LogsUtility.error("find by property name failed", re);
				throw re;
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
		
	/**
	 * 通过用户spaceuid获得用户信息
	 * @param name
	 * @return
	 */
	public Users searchUserBySpaceUID(String spaceUID)
	{
		try
		{
			String queryString = "from Users as model where model.spaceUID = ? ";
			return (Users)findOneObjectBySql(queryString, spaceUID);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	@Deprecated
	public List<Users> searchUserByContain(String propertyName, String name)
	{
		try
		{
			String queryString = "from Users as model where model." + propertyName + " like ? ";
			return findAllBySql(queryString, "%" + name + "%");
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List<Users> searchUserByEmail(String email){
		try{
			String queryString = "from Users as model where model.realEmail = ?";
			return findAllBySql(queryString, email);
		}
		catch(RuntimeException re){
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过手机号查询用户
	 * @param mobile 手机号码
	 * @return
	 */
	public List<Users> searchUserByMobile(String mobile)
	{
		try{
			String queryString = "from Users as model where model.mobile = ?";
			return findAllBySql(queryString, mobile);
		}
		catch(RuntimeException re){
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除用户
	 * @param userid
	 */
	public void deleteUserByID(long userid)
	{
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete from Spaces where spaceUID in (select u.spaceUID from Users u where u.id = ?) ";
			excute(queryString, userid);
			
			queryString = "update Groups set manager = null where manager.id = ? ";
			excute(queryString, userid);
			
			queryString = "update Organizations set manager = null where manager.id = ? ";
			excute(queryString, userid);
			
			queryString = "update Messages set user = null where user.id = ? ";
			excute(queryString, userid);
			
			if (WebConfig.mailEnable)       // 先临时这样处理
			{
				Users u = findUserById(userid);
				WebConfig.syncRemoveMailUser(u);
			}
			
			queryString = "delete Users where id = ? ";
			excute(queryString, userid);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除多个用户
	 * @param userIds
	 */
	public void deleteUserByID(List<Long> userIds)
	{
		if (userIds == null || userIds.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete from Spaces where spaceUID in (select u.spaceUID from Users u where u.id in ( ?1 )) ";
			excute(queryString, userIds);
			
			queryString = "delete from UsersConfig where user.id in ( ?1 ) ";
			excute(queryString, userIds);//用户模块
			queryString = "delete from UsersCustomTeams where user.id in ( ?1 ) ";
			excute(queryString, userIds);//用户自定义组
			queryString = "delete from UsersDevice where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from UsersGroups where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from UsersOrganizations where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from UsersPermissions where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from UsersRoles where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from UserWorkaday where userinfo.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "select tagId from Taginfo where userinfo.id in ( ?1 ) ";
			List<Long> tagids=findAllBySql(queryString, userIds);
			if (tagids!=null && tagids.size()>0)
			{
				queryString = "delete from Filetaginfo where taginfo.tagId in ( ?1 ) ";
				excute(queryString, tagids);//
			}
			
			queryString = "delete from Taginfo where userinfo.id in ( ?1 ) ";
			excute(queryString, userIds);//
			
			
			queryString = "delete from MailAccount where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "delete from Groupmembershareinfo where userinfo.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "select id from CustomTeams where user.id in ( ?1 ) ";
			List<Long> teamids=findAllBySql(queryString, userIds);
			deleteTeamsById(teamids);
			
			queryString = "delete from SystemLogs where user.id in ( ?1 ) ";
			excute(queryString, userIds);//
			//获取单位管理员
			
			
			
			queryString = "select id from Users where role="+Constant.COMPANY_ADMIN
			+" and company.id in (select b.company.id from Users as b where b.id=?)";
			List<Long> adminids = findAllBySql(queryString, userIds.get(0));
			long admin=1;
			if (adminids!=null && adminids.size()>0)
			{
				admin=adminids.get(0);
			}
			queryString = "update ApprovalInfo set userID="+admin+" where userID in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update ApprovalInfo set newuserID="+admin+" where newuserID in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update ApprovalInfo set approvalUsersID='"+admin+"' where approvalUsersID in ( ?1 ) ";
			List<String> strids=new ArrayList<String>();
			for (int i=0;i<userIds.size();i++)
			{
				strids.add(""+userIds.get(i));
			}
			excute(queryString, strids);
			
			queryString = "update ApprovalInfo set lastsignid="+admin+" where lastsignid in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update ApprovalInfo set operateid="+admin+" where operateid in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update ApprovalInfo set modifier="+admin+" where modifier in ( ?1 ) ";
			excute(queryString, userIds);
		    
		    queryString = "update ApprovalInfo set submiter="+admin+" where submiter in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update ApprovalTask set approvalUserID="+admin+" where approvalUserID in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update ApprovalTask set nextAcceptorID="+admin+" where nextAcceptorID in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update ApprovalTask set submiter="+admin+" where submiter in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update SignInfo set signer.id="+admin+" where signer.id in ( ?1 ) ";
			excute(queryString, userIds);//
			queryString = "update SameSignInfo set signer.id="+admin+" where signer.id in ( ?1 ) ";
			excute(queryString, userIds);//是更新还是删除再定
			queryString = "update Organizations set manager = null where manager.id in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update MobileSendInfo set sender = null where sender.id in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update Groupshareinfo set userinfo = null where userinfo.id in ( ?1 ) ";
			excute(queryString, userIds);
			queryString = "update Groups set manager = null where manager.id in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "delete from Personshareinfo where userinfoBySharerUserId.id in ( ?1 ) ";
			excute(queryString, userIds);//清除共享
			
			queryString = "delete from Personshareinfo where userinfoByShareowner.id in ( ?1 ) ";
			excute(queryString, userIds);//清除共享
			
			queryString = "delete from DiscuGroupMemberPo where memberId in ( ?1 )";
			excute(queryString, userIds);//清除讨论组成员
			
			queryString = "delete from CtmGroupMemberPo where userId in ( ?1 )";
			excute(queryString, userIds);//清除联系人
			
			queryString = "delete from CtmGroupMemberPo where ownerId in ( ?1 )";
			excute(queryString, userIds);//清除联系人
			
			queryString = "delete from CustomGroupPo where userId in ( ?1 )";
			excute(queryString, userIds);//清除联系人
			
			queryString = "delete from SessionMegPo where sendId in ( ?1 )";
			excute(queryString, userIds);//清除联系人消息记录
			
			queryString = "delete from SessionMegPo where acceptId in ( ?1 )";
			excute(queryString, userIds);//清除联系人消息记录
			
			queryString = "delete from UsersMessages where user.id in ( ?1 )";
			excute(queryString, userIds);//清除用户消息
			
//			queryString = "delete from Messages where (user.id in ( ?1 ) or msguser .id in ( ?2 ))";
//			excute(queryString, userIds, userIds);//清除消息
			
			//还要继续加
			
			queryString = "delete from CalendarEmpower where userinfo.id in ( ?1 )";
			excute(queryString, userIds);//
			queryString = "delete from CalendarEvent where userinfo.id in ( ?1 )";
			excute(queryString, userIds);//日程用户
			queryString = "delete from CalendarEvent where poweruserinfo.id in ( ?1 )";
			excute(queryString, userIds);//为您安排日程的用户
			queryString = "delete from Files where userinfo.id in ( ?1 )";
			excute(queryString, userIds);//文件
			queryString = "delete from MobileSendInfo where sender.id in ( ?1 )";
			excute(queryString, userIds); // 发送者,有可能为空
			queryString = "delete from NewPersonshareinfo where (userinfoBySharerUserId.id in ( ?1 ) or userinfoByShareowner.id in ( ?2 ))";
			excute(queryString, userIds, userIds); //
		
			queryString = "update Groups set manager = null where manager.id in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update Organizations set manager = null where manager.id in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "update Messages set user = null where user.id in ( ?1 ) ";
			excute(queryString, userIds);
			
			queryString = "delete UsersSynch where users.id in (?1) ";
			excute(queryString, userIds);
			
			queryString = "delete Users where id in (?1) ";
			excute(queryString, userIds);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除多个组织结构
	 * @param ids
	 */
	public void deleteOrganizationsByID(List<Long> ids)
	{
		if (ids == null || ids.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			String  queryString = "update UsersMessages as um set um.isDelete = true, um.isNew = false " 
				+ "where um.messageId in (select m.id from Messages m, Organizations o where m.type in (?1, ?2)" 
					+ " and m.attach like CONCAT(o.spaceUID, '%') and o.id in (?3)) ";
			excute(queryString, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, ids);
		
			queryString = "delete from Spaces where spaceUID in (select o.spaceUID from Organizations o where o.id in ( ?1 )) ";
			excute(queryString, ids);				
			
			queryString = "delete Organizations where id in (?1) ";
			excute(queryString, ids);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除多个组	
	 * @param ids
	 */
	public void deleteGroupsByID(List<Long> ids)
	{
		if (ids == null || ids.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{	
			String  queryString = "update UsersMessages as um set um.isDelete = true, um.isNew = false " 
				+ "where um.messageId in (select m.id from Messages m, Groups g where m.type in (?1, ?2)" 
					+ " and m.attach like CONCAT(g.spaceUID, '%') and g.id in (?3)) ";
			excute(queryString, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, ids);
			
			queryString = "delete from Spaces where spaceUID in (select g.spaceUID from Groups g where g.id in ( ?1 )) ";
			excute(queryString, ids);
						
			queryString = "delete Groups where id in (?1) ";
			excute(queryString, ids);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过spaceUID删除多个组定义
	 * @param ids
	 */
	public void deleteGroupSpacesBySpaceUID(List<String> ids)
	{
		if (ids == null || ids.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			// jpa 在这里有bug，只能通过如此方式更新了。
			String  queryString = "update UsersMessages as um set um.isDelete = true, um.isNew = false " 
					+ "where um.messageId in (select m.id from Messages m, Spaces s where m.type in (?1, ?2)" 
						+ " and m.attach like CONCAT(s.spaceUID, '%') and s.spaceUID in (?3)) ";
			excute(queryString, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, ids);
			
			queryString = "delete Spaces where spaceUID in (?1) ";
			excute(queryString, ids);
						
			queryString = "delete Groups where spaceUID in (?1) ";
			excute(queryString, ids);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除用户自定义组
	 * @param ids
	 */
	public void deleteTeamsById(List<Long> ids)
	{
		if (ids == null || ids.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete CustomTeams where id in (?1) ";
			excute(queryString, ids);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除组织结构
	 * @param id
	 */
	public void deleteOrganizationsByID(long id)
	{
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete Organizations where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，查询用户所在的组织结构
	 * @param userId
	 * @return
	 */
	public List<UsersOrganizations> findUsersOrganizationsByUserId(java.lang.Long userId)
	{
		try
		{
			String queryString = "from UsersOrganizations as model where model.user.id = ?";
			return findAllBySql(queryString,  userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组织结构id，查询组织结构中的人员
	 * @param orgId
	 * @return
	 */
	public List<UsersOrganizations> findUsersOrganizationsByOrgId(java.lang.Long orgId)
	{
		try
		{
			String queryString = "from UsersOrganizations as model where model.organization.id = ? order by model.organization.sortNum ";
			return findAllBySql(queryString,  orgId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组织结构id和用户，查询用户是否在组织结构中。
	 * @param orgID
	 * @param userID
	 * @return
	 */
	@Deprecated
	public List<UsersOrganizations> findUsersOrganizationsByOrgIdAndUserId(long orgID, long userID)
	{
		LogsUtility.debug("attaching clean UsersOrganizations instance");
		try
		{
			String queryString = "from UsersOrganizations as model where model.user.id = ? and model.organization.id = ? order by model.organization.sortNum";
			return findAllBySql(queryString, userID, orgID);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("attach failed", re);
			throw re;
		}
	}
	
	@Deprecated
	public List<UsersOrganizations> findUsersOrganizationsByGroupNames(String[] groupNames)
	{
		LogsUtility.debug("getting UsersOrganizations instance with names: " + groupNames);
		try
		{
			StringBuffer sb = new StringBuffer();
			boolean flag = false;
			for (int i = 0; i < groupNames.length; i++)
			{
				if (flag)
				{
					sb.append(",");
				}
				sb.append(groupNames[i]);
				flag = true;
			}
			String queryString = "from UsersOrganizations as model where model.organization.name in( ? )";
			return findAllBySql(queryString, sb.toString());

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	// ?
	@Deprecated
	public List<String> getUsersMailByOrganizationsId(Long[] groupIds)
	{
		LogsUtility.debug("get distict members in group");
		try
		{
			StringBuffer sb = new StringBuffer("(");
			for (int i = 0; i < groupIds.length; i++)
			{
				if (i == (groupIds.length - 1))
				{
					sb.append(groupIds[i]).append(")");
					break;
				}
				sb.append(groupIds[i]).append(",");
			}
			String ids = sb.toString();
			String queryString = "select distinct model.user.email from UsersOrganizations as model where model.organization.id in "
					+ ids;
			return findAllBySql(queryString);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("attach failed", re);
			throw re;
		}

	}
	
	/**
	 * 通过用户id，查询用户所在的组织结构
	 * @param userId
	 * @return
	 */
	public List<Organizations> findOrganizationsByUserId(Long userId)
	{
		try
		{
			String queryString = "select model.organization from UsersOrganizations as model where model.user.id = ?";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，查询用户所在的根组织结构
	 * @param userId
	 * @return
	 */
	public List<Organizations> findRootOrganizationsByUserId(Long userId)
	{
		try
		{
			String queryString = "select model.organization from UsersOrganizations as model where model.user.id = ?";
			List<Organizations> ret = findAllBySql(queryString,  userId);
			List<Long> params = new ArrayList<Long>();
			String p;
			for (Organizations temp : ret)
			{
				p = temp.getParentKey();
				if (p != null)
				{
					params.add(Long.valueOf(p.substring(0, p.indexOf("-"))));
				}
				else
				{
					params.add(temp.getId());
				}
			}
			queryString = "select model from Organizations as model where model.parent is null and model.id in (?1) ";
			return findAllBySql(queryString,  params);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，查询用户所在公司的根一级组织结构
	 * @param userId
	 * @return
	 */
	public List<Organizations> findCompanyOrganizationsByUserId(Long userId)
	{
		try
		{
			String queryString = "select model from Organizations as model, Users as user where user.id = ? " +
					" and model.parent is null and user.company.id = model.company.id";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户名，查询用户所在的组织结构
	 * @param userId
	 * @return
	 */
	public List<Organizations> findOrganizationsByUserName(String userName)
	{
		try
		{
			String queryString = "select model.organization from UsersOrganizations as model where model.user.userName = ?";
			return findAllBySql(queryString,  userName);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，查询用户所在的组
	 */
	public List<Groups> findGroupsByUserId(Long userId)
	{
		try
		{
			String queryString = "select model.group from UsersGroups as model where model.user.id = ?";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 查询用户自定义组
	 * @param teamId
	 * @return
	 */
	public CustomTeams findCustomTeamsById(Long teamId)
	{
		LogsUtility.debug("getting CustomTeams instance with id: " + teamId);
		try
		{
			return (CustomTeams)find(CustomTeams.class, teamId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，查询用户所在的用户自定义组，不包括用户自己创建的组
	 * @param userId
	 * @return
	 */
	public List<CustomTeams> findCustomTeamsByUserId(Long userId)
	{
		LogsUtility.debug("getting CustomTeams instance with id: " + userId);
		try
		{
			String queryString = "select model.customTeam from UsersCustomTeams as model where model.user.id = ?";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户id，获取用户自己创建的用户自定义组
	 * @param userId
	 * @return
	 */
	public List<CustomTeams> findUserOwnCustomTeamsByUserId(Long userId)
	{
		LogsUtility.debug("getting CustomTeams instance with id: " + userId);
		try
		{
			String queryString = "select model from CustomTeams model where model.user.id = ?";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组织结构id，查询组织结构
	 * @param Id
	 * @return
	 */
	public Organizations findOrganizationsById(Long orgId)
	{
		try
		{
			return (Organizations)find(Organizations.class, orgId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/** 通过组织名字找某个组织，该组织名字中以.分割父组织和子组织。
	 *  及如name为“a.b.c”，则是查找组织名为c，其父组织名为b，祖父组织名为a的组织对象
	 * @param name
	 * @param companyId 组织结构所在的公司id.
	 * @return
	 */
	public Organizations findOrganizationsByName(String name, long companyId)
	{
		try
		{
			String[] tempN = name.split("\\.");
			int size = tempN.length;
			String[] names = new String[size];
			for (int i = 0; i < size; i++)
			{
				names[i] = tempN[size - 1 - i].trim();
				//System.out.println("=====names===="+names[i]+"--------");
			}
			StringBuffer queryString = new StringBuffer("from Organizations as model where model.name = ? and model.company.id = " + companyId);
			if (size > 1)
			{
				String temp = "parent";
				for (int i = size - 2; i >= 0; i--)
				{
					queryString.append(" and model." + temp + ".name = ? ");
					temp += ".parent";
				}
			}
			else
			{
				queryString.append(" and model.parent = null ");
			}
			return (Organizations)findOneObjectBySql(queryString.toString(), names);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	 
	/**
	 * 通过name，查找父组织下的子组织
	 */
	@Deprecated
	public Organizations findOrganizationsByName(String name, Organizations parent)
	{
		try
		{
			String queryString = "";
			if (parent != null)
			{
				queryString = "from Organizations as model where model.name = ? and model.parent.id = ? ";
				return (Organizations)findOneObjectBySql(queryString, name, parent.getId());
			}
			else
			{
				queryString = "from Organizations as model where model.name = ? and model.parent = null ";
				return (Organizations)findOneObjectBySql(queryString, name);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find all failed findOrganizationsByNameId", re);
			throw re;
		}
	}
	
	/**
	 * 通过name，查找父组织下的子组织
	 */
	public Organizations findOrganizationsByName(String name, Organizations parent, long companyId)
	{
		try
		{
			String queryString = "";
			if (parent != null)
			{
				queryString = "from Organizations as model where model.name = ? and model.parent.id = ? and model.company.id = ? ";
				return (Organizations)findOneObjectBySql(queryString, name, parent.getId(), companyId);
			}
			else
			{
				queryString = "from Organizations as model where model.name = ? and model.parent = null and model.company.id = ?";
				return (Organizations)findOneObjectBySql(queryString, name, companyId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find all failed findOrganizationsByNameId", re);
			throw re;
		}
	}
	
	/**
	 * 查询公司
	 * @param Id
	 * @return
	 */
	public Company findCompanyById(Long Id)
	{
		try
		{
			return (Company)find(Company.class, Id);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过公司名字查询公司
	 * @param name
	 * @return
	 */
	public Company findCompanyByName(String name)
	{
		try
		{
			String queryString = "from Company as model where model.name = ? ";
			return (Company) findOneObjectBySql(queryString, name);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
		
	/**
	 * 删除用户注册的，没有经过验证，且已经过了验证有效期的所有公司
	 */
	public void deleteInvalidateRegister()
	{
		String sql = "delete from Company as model where model.verifyCode is not null and model.ud < ? ";
		excute(sql, System.currentTimeMillis());
	}
	
	/**
	 * 通过公司编码查询公司
	 * @param name
	 * @return
	 */
	public Company findCompanyByCode(String code)
	{
		try
		{
			String queryString = "from Company as model where model.code = ? ";
			return (Company) findOneObjectBySql(queryString, code);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 查询公司名字中有name的所有公司
	 * @param name
	 * @return
	 */
	public List<Company> searchCompanyByName(String name)
	{
		try
		{
			String queryString = "from Company as model where model.name like ? ";
			return findAllBySql(queryString, "%" + name + "%");
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 获取公司的管理员用户帐号，目前系统中只支持一个公司只有一个管理员
	 * @param companyId
	 * @return
	 */
	public Users getCompanyAdminUser(long companyId)
	{
		String sql = "from Users as u where u.company.id = ? and u.role = ? ";
		return (Users)findOneObjectBySql(sql, companyId, (short)Constant.COMPANY_ADMIN);
	}
	
	/**
	 * 查询组
	 * @param Id
	 * @return
	 */
	public Groups findGroupById(Long Id)
	{
		try
		{
			return (Groups)find(Groups.class, Id);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据父Id得到子组织的 
	 * @param parentId 如果为null则返回第一级的所有组织。
	 * @return
	 */
	@Deprecated
	public List<Organizations> getChildOrganizations(Long parentId)
	{
		
		try
		{
			if (parentId == null)
			{
				String queryString = "from Organizations as model where model.parent is null order by model.sortNum ";
				return findAllBySql(queryString);
			}
			else
			{
				String queryString = "from Organizations as model where model.parent.id = ? order by model.sortNum ";
				return findAllBySql(queryString, parentId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据父Id得到子组织的 
	 * @param parentId 如果为null则返回第一级的所有组织。如果treeFlag为true，则返回公司的所有部门
	 * @return
	 */
	public List<Organizations> getChildOrganizations(Long parentId, Long companyId, boolean treeFlag)
	{		
		try
		{
			/*if (parentId == null && companyId == null)
			{
				String queryString = "from Organizations as model where model.parent is null order by model.sortNum ";
				return findAllBySql(queryString, parentId);
			}*/						
			if (parentId == null)
			{
				String queryString = treeFlag ? "from Organizations as model where model.company.id = ? order by model.organizecode,model.sortNum "
						: "from Organizations as model where model.parent is null and model.company.id = ? order by model.sortNum,model.organizecode ";
				return findAllBySql(queryString, companyId);
			}
			else
			{
				if (!treeFlag)
				{
					String queryString = "from Organizations as model where model.parent.id = ? and model.company.id = ? order by model.sortNum,model.organizecode ";
					return findAllBySql(queryString, parentId, companyId);
				}
				else
				{
					Organizations parent = this.findOrganizationsById(parentId);
					String queryString = "from Organizations as model where model.parentKey like ? and model.company.id = ? order by model.organizecode,model.sortNum ";
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parentId + "-%";	
					return findAllBySql(queryString, key, companyId);
				}
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	// ??
	@Deprecated
	public List<Organizations> getChildOrganizations(Long parentId, Users user)
	{
		
		try
		{
			String cond="";
			if (user!=null && !"admin".equals(user.getUserName()))
			{
				List<UsersOrganizations> list=findUsersOrganizationsByUserId(user.getId());
	    		if (list!=null && list.size()>0)
	    		{
	    			String orgcode=list.get(0).getOrganization().getOrganizecode();
	    			if (orgcode!=null)
	    			{
	    				int index = orgcode.indexOf("-");
	    				if (index>0)
	    				{
	    					orgcode=orgcode.substring(0,index);
	    					cond=" and model.organizecode like '"+orgcode+"%'";
	    				}
	    				else
	    				{
	    					cond=" and model.organizecode like '"+orgcode+"%'";
	    				}
	    			}
	    		}
			}
			if (parentId == null)
			{
				String queryString = "from Organizations as model where model.parent is null "+cond;
				return findAllBySql(queryString);
			}
			else
			{
				String queryString = "from Organizations as model where model.parent.id = ? ";
				return findAllBySql(queryString, parentId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	/**
	 * 根据父Id得到子组的 
	 * @param parentId 如果parentId为null则返回第一级的所有组。
	 * @return
	 */
	public List<Groups> getChildGroups(Long parentId)
	{
		LogsUtility.debug("finding group instance with parentId: " + parentId);
		try
		{
			if (parentId == null)
			{
				String queryString = "from Groups as model where model.parent is null ";
				return findAllBySql(queryString);
			}
			else
			{
				String queryString = "from Groups as model where model.parent.id = ? ";
				return findAllBySql(queryString, parentId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据父组织得到所有的子组织
	 * @param parent 父组织
	 * @param treeFlag，为true表示递归取得所有的子组织，false表示只取第一级子组织
	 * @return
	 */
	public List<Organizations> getChildTreeOrganizations(Organizations parent, boolean treeFlag)
	{
		
		try
		{
			if (!treeFlag)
			{
				String queryString = "from Organizations as model where model.parent.id = ? ";
				return findAllBySql(queryString, parent.getId());
			}
			else
			{
				String queryString = "from Organizations as model where model.parentKey like ? ";
				String key = parent.getParentKey();
				if (key == null)
				{
					key = "";
				}
				key += parent.getId() + "-%";	
				return findAllBySql(queryString, key);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	// ???
	@Deprecated
	public List<Organizations> getAllOrganizations(String searchkey,int start, int count, String sort, String dir,Users users)
	{
		String queryString = " select u from Organizations u ";
        String queryCount = " select count(*) from Organizations u ";
        if (users!=null)
        {
        	List<UsersOrganizations> list=findUsersOrganizationsByUserId(users.getId());
    		if (list!=null && list.size()>0)
    		{
    			String orgcode=list.get(0).getOrganization().getOrganizecode();
    			if (orgcode!=null)
    			{
    				int index = orgcode.indexOf("-");
    				String cond="";
    				if (searchkey!=null && searchkey.length()>1)
    				{
    					cond=" and (u.name like '%"+searchkey+"%' or u.depid like '%"+searchkey+"%')";
    				}
    				if (index>0)
    				{
    					orgcode=orgcode.substring(0,index);
    					queryString+=" where u.organizecode like '"+orgcode+"%'"+cond;
    					queryCount+=" where u.organizecode like '"+orgcode+"%'"+cond;
    				}
    				else
    				{
    					queryString+=" where u.organizecode like '"+orgcode+"%'"+cond;
    					queryCount+=" where u.organizecode like '"+orgcode+"%'"+cond;
    				}
    			}
    		}
        }
        else if ("-1-1".equals(searchkey))//只查询父组织
        {
        	queryString+=" where u.parent=null ";
        }
        else if (searchkey!=null && searchkey.length()>1)
        {
        	queryString+=" where (u.name like '%"+searchkey+"%' or u.depid like '%"+searchkey+"%')";
        }
        if (sort != null && dir != null)
        {
            queryString += " order by u." + sort + " " + dir;
        }
        else
        {
        	queryString += " order by u.organizecode,u.sortNum ";
        }
        List<Organizations> ret = findAllBySql(start, count, queryString); 
        for(Organizations temp : ret)
        {
            temp.setMemberSize(getUserCountByOrganizations(temp.getId(), false).intValue());//这里要改进，影响性能----孙爱华
        }
        Organizations o = new Organizations();
        try
        {
        	Long len=getCountBySql(queryCount);
        	if (len!=null)
        	{
        		o.setMemberSize(len.intValue());
        	}
        }
        catch (Exception e)
        {
        	o.setMemberSize(0);
        }
        ret.add(o);
        return ret;
	}
	
	/**
	 * 得到组织的成员数量
	 * @param orgId
	 * @param treeFlag，为true表示递归取得所有的子组织，false表示只取第一级子组织
	 * @return
	 */
	public Long getUserCountByOrganizations(Long orgId, boolean treeFlag)
	{

		try
		{
			if (!treeFlag)
			{
				String queryString = "select count(*) from UsersOrganizations as model where model.organization.id = ? ";
				return getCountBySql(queryString, orgId);
			}
			else
			{
				Organizations parent = this.findOrganizationsById(orgId); 
				String queryString = "select count(*) from UsersOrganizations as model where model.organization.parentKey like ? or model.organization.id = ? ";
				String key = parent.getParentKey();
				if (key == null)
				{
					key = "";
				}
				key += parent.getId() + "-%";	
				return getCountBySql(queryString, key, orgId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);			
		}
		return 0L;
	}
	
	@Deprecated	
	public List<Organizations> findOrganizationsByOrgProperty(String propertyName, Object value)
	{
		LogsUtility.debug("finding Organizations instance with property: " + propertyName + ", value: " + value);
		try
		{
			return findByProperty("Organizations", propertyName, value);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组织结构属性，获取组织结构
	 * @param propertyName
	 * @param value
	 * @param companyId
	 * @return
	 */
	public List<Organizations> findOrganizationsByOrgProperty(String propertyName, Object value, Long companyId)
	{
		LogsUtility.debug("finding Organizations instance with property: " + propertyName + ", value: " + value);
		try
		{

			String queryString = "from Organizations as model where model." + propertyName +" = ? and model.company.id = ? ";
			return findAllBySql(queryString, value, companyId);			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	@Deprecated
	public List<Organizations> findAllOrganizations()
	{
		return findAllOrganizations(false,null);
	}
	@Deprecated
	public List<Organizations> findAllOrganizations(boolean spaceFlag)
	{
		return findAllOrganizations(spaceFlag,null);
	}
	@Deprecated
	public List<Organizations> findAllOrganizations(boolean spaceFlag,String orgcode)
	{
		LogsUtility.debug("finding all Organizations instances");
		try
		{
			if (true)
			{
				String queryString = "select new Organizations(model, space) from Organizations model, Spaces space where model.spaceUID = space.spaceUID ";
				if (orgcode!=null && orgcode.length()>0)
				{
					queryString+=" and model.organizecode like '"+orgcode+"%' ";
				}
				queryString+=" order by model.sortNum ";
				return findAllBySql(queryString);
			}
			else
			{
				return super.findAll(Organizations.class);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 获取公司下的所有组织结构
	 * @param companyId
	 * @return
	 */
	public List<Organizations> findAllOrganizations(long companyId)
	{
		return findAllOrganizations(false, null, companyId);
	}
	
	/**
	 * 获取公司下的所有组织结构，获得的组织对象中是否包含space对象
	 * @param companyId
	 * @return
	 */
	public List<Organizations> findAllOrganizations(boolean spaceFlag, long companyId)
	{
		return findAllOrganizations(spaceFlag, null, companyId);
	}
	
	/**
	 * 获取公司下的所有组织结构，获得的组织对象中是否包含space对象
	 * @param companyId
	 * @return
	 */
	public List<Organizations> findAllOrganizations(boolean spaceFlag, String orgcode, long companyId)
	{
		LogsUtility.debug("finding all Organizations instances");
		try
		{
			if (spaceFlag)
			{
				String queryString = "select new Organizations(model, space) from Organizations model, Spaces space where model.spaceUID = space.spaceUID ";
				if (orgcode!=null && orgcode.length()>0)
				{
					queryString+=" and model.organizecode like '"+orgcode+"%' ";
				}
				queryString+=" and model.company.id = ?  order by model.sortNum ";
				return findAllBySql(queryString, companyId);
			}
			else
			{
				String queryString = "from Organizations as model where model.company.id = ? order by model.sortNum ";
				return findAllBySql(queryString, companyId);	
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find all failed", re);
			throw re;
		}
	}
		
	/**
	 * 删除在组织结构中的用户
	 * @param id
	 */
	public void deleteUsersOrganizationsByID(long uoId)
	{
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete UsersOrganizations where id = ? ";
			excute(queryString, uoId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除在多个组织结构中的多个用户
	 * @param orgId
	 * @param userId
	 */
	public void deleteUsersOrganizationsByOrgId(List<Long> orgId, List<Long> userId)
	{
		if (orgId == null || orgId.size() <= 0 || userId == null || userId.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete UsersOrganizations where user.id in ( ?1 ) and organization.id in ( ?2 )";
			excute(queryString, userId, orgId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除在多个组中的多个用户
	 * @param groupId
	 * @param userId
	 */
	public void deleteUsersGroupsByGroupId(List<Long> groupId, List<Long> userId)
	{
		if (groupId == null || groupId.size() <= 0 || userId == null || userId.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete UsersGroups where user.id in ( ?1 ) and group.id in ( ?2 )";
			excute(queryString, userId, groupId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
			
	/**
	 * 获得组拥有的角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getRolesByGroupId(long groupId)
	{
		LogsUtility.debug("get role by groupId ");
		try
		{
			String queryString = "select model.role from GroupsRoles as model where model.group.id = ? ";
			return findAllBySql(queryString, groupId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	/**
	 * 获得组织拥有的角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getRolesByOrgId(long orgId)
	{
		LogsUtility.debug("get role by orgId ");
		try
		{
			String queryString = "select model.role from OrganizationsRoles as model where model.organization.id = ? ";
			return findAllBySql(queryString, orgId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 获得用户自定义组拥有的角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getRolesByTeamId(long teamId)
	{
		LogsUtility.debug("get role by groupId ");
		try
		{
			String queryString = "select model.role from CustomTeamsRoles as model where model.team.id = ?  ";
			return findAllBySql(queryString, teamId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 是否是角色模板
	 * @param isTemp
	 * @return
	 */
	public List<Roles> getGlobalRoles(int type)
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null and model.type = ? ";
		return findAllBySql(queryString.toString(), type);
	}
	
	/**
	 * 获取用户当前的系统角色
	 * @param isTemp
	 * @return
	 */
	@Deprecated
	public List<Roles> getGlobalRolesByUserId(Long userId)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ? and ur.role.organization is null and  ur.role.group is null and ur.role.team is null and ur.role.type = ? ";
		return findAllBySql(queryString, userId, RoleCons.SYSTEM);
	}
	@Deprecated
	public List<Roles> getGlobalRolesByUserId(Long userId, Long companyId)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ? and ur.role.company.id = ? " 
				+ " and ur.role.organization is null and  ur.role.group is null and ur.role.team is null and ur.role.type = ? ";
		return findAllBySql(queryString, userId, companyId, RoleCons.SYSTEM);
	}
	
	/**
	 * 获得公司定义的角色，同一个公司，一种模版角色不能同名。
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	public Roles getCompanyTemplateRole(Long companyId, String roleName, int type)
	{
		String queryString = "from Roles as model where model.company.id = ? and model.organization is null "
				+ " and  model.group is null and model.team is null and model.roleName = ? and model.type = ? ";
		return (Roles)findOneObjectBySql(queryString, companyId, roleName, type);
	}
	
	/**
	 * 获得组的有某个名字的角色
	 * @param groupId
	 * @param roleName
	 * @return
	 */
	public Roles getGroupRole(Long groupId, String roleName)
	{
		String queryString = "from Roles as model where model.group.id = ?  and model.roleName = ?";
		return (Roles)findOneObjectBySql(queryString, groupId, roleName);
	}
	
	/**
	 * 获得组织的有某个名字的角色
	 * @param orgId
	 * @param roleName
	 * @return
	 */
	public Roles getOrgRole(Long orgId, String roleName)
	{
		String queryString = "from Roles as model where model.organization.id = ?  and model.roleName = ?";
		return (Roles)findOneObjectBySql(queryString, orgId, roleName);
	}
	
	/**
	 * 获得自定义组的有某个名字的角色
	 * @param teamId
	 * @param roleName
	 * @return
	 */
	public Roles getTeamRole(Long teamId, String roleName)
	{
		String queryString = "from Roles as model where model.team.id = ?  and model.roleName = ?";
		return (Roles)findOneObjectBySql(queryString, teamId, roleName);
	}
	
	/**
	 * 角色模板
	 * @param type
	 * @return
	 */
	public Roles getGlobalRoles(String roleName, int type)
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null "
			+ " and  model.group is null and model.team is null and model.roleName = ? and model.type = ? ";
		return (Roles)findOneObjectBySql(queryString, roleName, type);
	}
	
	/**
	 * 是否是角色模板
	 * @param isTemp
	 * @return
	 */
	public List<Roles> getGlobalRoles()
	{
		String queryString = "from Roles as model where model.company is null and model.organization is null and  model.group is null and model.team is null";
		return findAllBySql(queryString.toString());
	}
	
	/**
	 * 获得公司拥有的私有角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getCompanyRoles(long companyId)
	{
		LogsUtility.debug("get role defined ");
		try
		{
			StringBuffer queryString = new StringBuffer();			
			if (companyId != -1)
			{
				queryString.append("from Roles as model where model.company.id = ? ");
				return findAllBySql(queryString.toString(), companyId);
			}
			return null;
			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 获得公司拥有的私有角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getCompanyRoles(long companyId, int type)
	{
		LogsUtility.debug("get role defined ");
		try
		{
			String sql = "from Roles as model where model.company.id = ? ";
			if (type >= 0)
			{
				sql += " and model.type = ?";
				return findAllBySql(sql, companyId, type);	
			}
			else
			{
				return findAllBySql(sql, companyId);
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 获得组拥有的私有角色
	 * @param groupId
	 * @return
	 */
	public List<Roles> getGroupRoles(long groupId)
	{
		LogsUtility.debug("get role defined ");
		try
		{
			StringBuffer queryString = new StringBuffer();			
			if (groupId != -1)
			{
				queryString.append("from Roles as model where model.group.id = ? ");
				return findAllBySql(queryString.toString(), groupId);
			}
			return null;
			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 获取组织结构所拥有的私有角色
	 * @param orgId
	 * @return
	 */
	public List<Roles> getOrgRoles(long orgId)
	{
		LogsUtility.debug("get role defined ");
		try
		{
			StringBuffer queryString = new StringBuffer();	
			if (orgId != -1)
			{
				queryString.append("from Roles as model where model.organization.id = ? ");
				return findAllBySql(queryString.toString(), orgId);
			}
			return null;
			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 获取用户自定义组所拥有的私有角色
	 * @param teamId
	 * @return
	 */
	public List<Roles> getTeamRoles(long teamId)
	{
		LogsUtility.debug("get role defined ");
		try
		{
			StringBuffer queryString = new StringBuffer();
			if (teamId != -1)
			{
				queryString.append("from Roles as model where model.team.id = ? ");
				return findAllBySql(queryString.toString(), teamId);
			}
			return null;
			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除公司的角色
	 * @param entityClass
	 * @param idName
	 * @param id
	 */
	public void deleteTemplateRoleById(long companyId, List<Long> id)
	{
		String queryString = "delete Roles where company.id = ?1 and id in (?2)";
		excute(queryString, companyId, id);
	}
	
	/**
	 * 获取用户所拥有的角色
	 * @param roleId
	 * @return
	 */
	public List<Users> findUsersByRoleId(Long roleId)
	{
		try
		{
			String queryString = "select model.user from UsersRoles as model where model.role.id = ?";
			return findAllBySql(queryString,  roleId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	public List<Users> findUsersByOrgId(Long orgId, boolean treeFlag)
	{
		return findUsersByOrgId(null,orgId, treeFlag, -1, -1, null, null,null);
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<Users> findUsersByOrgId(Long companyId,Long orgId, boolean treeFlag, int start, int count, String sort, String dir,String searchcond)
	{
		//记住了此方法的sql下面获取总数方法也必须相应更改
		try
		{
			String searchsql="";
			if (searchcond!=null && searchcond.length()>0 && !searchcond.equals("null") && !searchcond.equals("undefined"))//孙爱华增加查询条件
			{
				searchsql=" and (model.user.userName like '%"+searchcond+"%' "
						+" or model.user.realEmail like '%"+searchcond+"%' "
						+" or model.user.realName like '%"+searchcond+"%' "
						+" or model.user.mobile like '%"+searchcond+"%' "
						+" or model.user.caId like '%"+searchcond+"%' ) ";
			}

			if (!treeFlag)
			{
				String queryString = "select model.user from UsersOrganizations as model where model.organization.id = ? "+searchsql;
				if (sort != null && dir != null)
				{
					queryString += " order by model.user." + sort + " " + dir;
				}
				return findAllBySql(start, count, queryString, orgId);
			}
			else
			{
				Organizations parent = (Organizations) find(Organizations.class, orgId);
				if (parent == null && searchsql.length()>0)//查整个单位的用户
				{
					String queryString = "select model.user from UsersOrganizations as model where model.user.company.id = ? "+searchsql;
					if (sort != null && dir != null)
					{
						queryString += " order by model.user." + sort + " " + dir;
					}
					return findAllBySql(start, count, queryString,companyId);
				}
				else if (parent == null)
				{
					String queryString = "select model from Users as model where model.company.id = ? and model.role != ? ";
					if (sort != null && dir != null)
					{
						queryString += " order by model." + sort + " " + dir;
					}
					return findAllBySql(start, count, queryString, companyId, (short)Constant.COMPANY_ADMIN);
				}
				else if (parent != null)
				{
					String queryString = "select model.user from UsersOrganizations as model where (model.organization.id = ? or model.organization.parentKey like ? ) "+searchsql;
					if (sort != null && dir != null)
					{
						queryString += " order by model.user." + sort + " " + dir;
					}
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parent.getId() + "-%";
					return findAllBySql(start, count, queryString, orgId, key);
				}
				return new ArrayList();
			}
			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户总数，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgId
	 * @param searchcond 查询用户的条件
	 * @return
	 */
	public Long findUsersCountByOrgId(Long companyid,Long orgId, boolean treeFlag,String searchcond)
	{	
		try
		{
			String searchsql="";
			if (searchcond!=null && searchcond.length()>0 && !searchcond.equals("null") && !searchcond.equals("undefined"))//孙爱华增加查询条件
			{
				searchsql=" and (model.user.userName like '%"+searchcond+"%' "
						+" or model.user.realEmail like '%"+searchcond+"%' "
						+" or model.user.realName like '%"+searchcond+"%' "
						+" or model.user.mobile like '%"+searchcond+"%' "
						+" or model.user.caId like '%"+searchcond+"%' ) ";
			}
			if (!treeFlag)
			{
				String queryString = "select count(model.user) from UsersOrganizations as model where model.organization.id = ? "+searchsql;
				return getCountBySql(queryString, orgId);
			}
			else
			{

				Organizations parent = (Organizations) find(Organizations.class, orgId);
				if (parent == null && searchsql.length()>0)
				{
					String queryString = "select count(model.user) from UsersOrganizations as model where model.user.company.id=? "+searchsql;
					return getCountBySql(queryString, companyid);
				}
				else if (parent == null)
				{
					String queryString = "select count(model) from Users as model where model.company.id = ? and model.role != ? ";
					return getCountBySql(queryString, companyid, (short)Constant.COMPANY_ADMIN);
				}
				else if (parent != null)
				{
					String queryString = "select count(model.user) from UsersOrganizations as model where (model.organization.id = ? or model.organization.parentKey like ?) "+searchsql;
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parent.getId() + "-%";	
					return getCountBySql(queryString, orgId, key);
				}

			}
			return 0L;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据组Id得到组中的所有用户，如果treeFlag为true则递归查询组中所有子组中
	 * 的用户，为false，则值查询该级组中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	public List<Users> findUsersByGroupId(Long groupId, boolean treeFlag)
	{
		
		try
		{
			if (!treeFlag)
			{
				String queryString = "select model.user from UsersGroups as model where model.group.id = ? ";
				return findAllBySql(queryString, groupId);
			}
			else
			{
				Groups parent = (Groups) find(Groups.class, groupId);
				if (parent != null)
				{
					String queryString = "select model.user from UsersGroups as model where model.group.id = ? or model.group.parentKey like ? ";
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parent.getId() + "-%";	
					return findAllBySql(queryString, groupId, key);
				}
				return new ArrayList();
			}
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 根据用户自定义组Id得到用户自定义组中的所有用户。
	 * @param orgId
	 * @return
	 */
	public List<Users> findUsersByTeamId(Long teamId)
	{
		
		try
		{
			String queryString = "select model.user from UsersCustomTeams as model where model.customTeam.id = ? ";
			return findAllBySql(queryString, teamId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 删除分配给用户的角色
	 * @param roleId
	 * @param userIds
	 */
	public void deleteRolesUsers(long roleId, List<Long> userIds)
	{
		if (userIds == null || userIds.size() <= 0)
		{
			return;
		}
		LogsUtility.debug("delete Users role " + roleId);
		try
		{
			String queryString = "delete UsersRoles as model where model.role.id = ?1 and model.user.id in ( ?2 )";
			excute(queryString, roleId, userIds);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据spaceUID获取组
	 * @param spaceUID
	 * @return
	 */
	public Groups findGroupsBySpaceUID(String spaceUID)
	{
		String query = " select model from Groups model where model.spaceUID = ?";
		return (Groups)findOneObjectBySql(query, spaceUID);
		
	}
	
	/**
	 * 根据spaceUID获取用户自定义组
	 * @param spaceUID
	 * @return
	 */
	public CustomTeams findTeamBySpaceUID(String spaceUID)
	{
		String query = " select model from CustomTeams model where model.spaceUID = ?";
		return (CustomTeams)findOneObjectBySql(query, spaceUID);		
	}
	
	/**
	 * 根据组的spaceUID获取组中的所有用户
	 * @param spaceUID
	 * @return
	 */
	public List<Users> findGroupUsersBySpaceUID(String spaceUID)
	{
		String query = " select model.user from UsersGroups model where model.group.spaceUID = ? ";
		return findAllBySql(query, spaceUID);
		
	}
	
	/**
	 * 根据spaceUID获取space
	 * @param uid
	 * @return
	 */
	public Spaces findSpaceByUID(String uid)
	{
		return (Spaces)this.find(Spaces.class, uid);
	}
	
	/**
	 * 根据用户id获取用户的space对象
	 * @param userId
	 * @return
	 */
	public Spaces findUserSpaceByUserId(Long userId)
	{
		LogsUtility.debug("get user spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, user) from Spaces model, Users user " +
					"where model.spaceUID = user.spaceUID and user.id = ? ";
			return (Spaces)this.findOneObjectBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过用户自定义组id获取用户自定义组的space对象
	 * @param teamId
	 * @return
	 */
	public Spaces findTeamSpaceByTeamId(Long teamId)
	{
		LogsUtility.debug("get custom team spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, team) from Spaces model, CustomTeams team " +
					"where model.spaceUID = team.spaceUID and team.id = ? ";
			return (Spaces)this.findOneObjectBySql(queryString, teamId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组织结构id获取组织结构的space对象
	 * @param orgId
	 * @return
	 */
	public Spaces findOrganizationSpaceByOrgId(Long orgId)
	{
		LogsUtility.debug("get organization spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, org) from Spaces model, Organizations org " +
					"where model.spaceUID = org.spaceUID and org.id = ? ";
			return (Spaces)this.findOneObjectBySql(queryString, orgId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 获得所有子组织结构的space对象
	 * @param parentId
	 * @return
	 */
	public List<Spaces> findChildOrganizationSpaceByOrgId(Long parentId)
	{
		LogsUtility.debug("get organization spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, org) from Spaces model, Organizations org " +
					"where model.spaceUID = org.spaceUID and org.parent.id = ? ";
			return findAllBySql(queryString, parentId);			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过组id获取组的space对象
	 * @param groupId
	 * @return
	 */
	public Spaces findGroupSpaceByGroupId(Long groupId)
	{
		LogsUtility.debug("get group spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, groups) from Spaces model, Groups groups " +
					"where model.spaceUID = groups.spaceUID and groups.id = ? ";
			return (Spaces)this.findOneObjectBySql(queryString, groupId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 获得所有子组的space对象
	 * @param parentId
	 * @return
	 */
	public List<Spaces> findChildGroupSpaceByGroupId(Long parentId)
	{
		LogsUtility.debug("find group spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, groups) from Spaces model, Groups groups " +
					"where model.spaceUID = groups.spaceUID and groups.parent.id = ? ";
			return findAllBySql(queryString, parentId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户参与的所有用户自定义在的空间
	 */
	public List<Spaces> findTeamSpaceByUserId(Long userId)
	{
		LogsUtility.debug("delete custom team spaces " );
		try
		{
			String queryString = "select distinct new Spaces(model, team.customTeam) from Spaces model, UsersCustomTeams team " +
					"where model.spaceUID = team.customTeam.spaceUID and team.user.id = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户参与的所有组织结构的空间
	 */
	public List<Spaces> findOrganizationSpaceByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct new Spaces(model, org.organization) from Spaces model, UsersOrganizations org " +
					"where model.spaceUID = org.organization.spaceUID and org.user.id = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户管理的所有的组织结构空间
	 */
	public List<Spaces> findUserManageOrganizationSpaceByUserId(Long userId)
	{
		try
		{
			String queryString = "select  distinct new Spaces(model, org) from Spaces model, Organizations org " +
					"where model.spaceUID = org.spaceUID and org.manager.id = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户参与的所有group组的空间
	 */
	public List<Spaces> findGroupSpaceByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct new Spaces(model, groups.group) from Spaces model, UsersGroups groups " +
					"where model.spaceUID = groups.group.spaceUID and groups.user.id = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户所在公司的空间
	 */
	public Spaces findCompanySpaceByUserId(Long userId)
	{
		try
		{
			String queryString = "select new Spaces(model, com) from Spaces model, Users user, Company com " +
					"where model.spaceUID = com.spaceUID and user.id = ? and user.company.id = com.id ";
			return (Spaces)findOneObjectBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	public String getCompanySpaceIdByUserId(Long userId)
	{
		try
		{
			String queryString = "select com.spaceUID from Users user, Company com " +
					"where user.id = ? and user.company.id = com.id ";
			return (String)findOneObjectBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 得到用户管理的所有group组的空间
	 */
	public List<Spaces> findUserManageGroupSpaceByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct new Spaces(model, groups) from Spaces model, Groups groups " +
					"where model.spaceUID = groups.spaceUID and groups.manager.id = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	
	/**
	 * 判断用户是否是组织空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isOrganizatonManager(Long userId, Long orgId)
	{
		try
		{
			String queryString = "select count(*) from Organizations org  where org.manager.id = ?  and org.id = ?";
			long count = getCountBySql(queryString, userId, orgId);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}
	}
	
	/**
	 * 判断用户是否是组织空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isOrganizatonManager(Long userId, String spaceUID)
	{
		try
		{
			String queryString = "select count(*) from Organizations org  where org.manager.id = ?  and org.spaceUID = ?";
			long count = getCountBySql(queryString, userId, spaceUID);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}
	}
	
	/**
	 * 判断用户是否是组空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isGroupManager(Long userId, String spaceUID)
	{
		try
		{
			String queryString = "select count(*) from Groups group  where group.manager.id = ?  and group.spaceUID = ?";
			long count = getCountBySql(queryString, userId, spaceUID);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}		
	}
	
	/**
	 *  公司管理员
	 * @param userId
	 * @return
	 */
	public boolean isCompanyAdmin(Long userId)
	{
		String queryString = "select count(*) from Users user  where user.id = ? and user.role = ?";
		long count = getCountBySql(queryString, userId, (short)Constant.COMPANY_ADMIN);
		return  count > 0;
	}
	
	/**
	 * 判断用户是否是空间的拥有者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isSpaceOwner(Long userId, String path)
	{
		try
		{			
			String queryString = "select count(*) from Users user where user.id = ? and LOCATE(user.spaceUID, ?) = 1 ";
			long count = getCountBySql(queryString, userId, path);
			return  count > 0;			
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}		
	}
	
	/**
	 * 判断用户是否是组空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isGroupManager(Long userId, Long groupId)
	{
		try
		{
			if (groupId==null) return false;
			String queryString = "select count(*) from Groups group  where group.manager.id = ?  and group.id = ?";
			long count = getCountBySql(queryString, userId, groupId);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}		
	}
	
	/**
	 * 判断用户是否是用户组的创建者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isTeamOwner(Long userId, Long tempId)
	{
		try
		{
			String queryString = "select count(*) from CustomTeams team where team.user.id = ?  and team.id = ?";
			long count = getCountBySql(queryString, userId, tempId);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}		
	}
	
	/**
	 * 判断用户是否是用户组的创建者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isTeamOwner(Long userId, String spaceUID)
	{
		try
		{
			String queryString = "select count(*) from CustomTeams team where team.user.id = ?  and team.spaceUID = ?";
			long count = getCountBySql(queryString, userId, spaceUID);
			return  count > 0;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			return false;
		}		
	}
	
	/**
	 * 删除在用户自定义组中的用户
	 * @param teamId
	 * @param userId
	 */
	public void deleteTeamsByTeamId(List<Long> teamId, List<Long> userId)
	{
		if (teamId == null || teamId.size() <= 0 || userId == null || userId.size() <= 0)
		{
			return;
		}		
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete UsersCustomTeams where customTeam.id in ( ?1 ) and user.id in ( ?2 )";
			excute(queryString, teamId, userId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 
	 * @param users
	 * @param start
	 * @param length
	 * @param sort
	 * @param dir
	 * @return
	 */
	@Deprecated
	public DataHolder getUserView(Users users,int start, int length, String sort, String dir)
	{
		try
		{
			int size = 0;
			String queryString;
			String cond="";
			if (users!=null && !"admin".equals(users.getUserName()))
	    	{
	    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(users.getId());
	    		if (list!=null && list.size()>0)
	    		{
	    			String orgcode=list.get(0).getOrganization().getOrganizecode();
	    			if (orgcode!=null)
	    			{
	    				int index = orgcode.indexOf("-");
	    				if (index>0)
	    				{
	    					orgcode=orgcode.substring(0,index);
	    					cond+=" and userinfo.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
	    				}
	    				else
	    				{
	    					cond+=" and userinfo.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
	    				}
	    			}
	    		}
	    	}
			String VIEW = "select new com.evermore.weboffice.domain.AdminUserinfoView("
				+ "userinfo.id, userinfo.userName, userinfo.realName, userinfo.realEmail, "
				+ "userinfo.storageSize, userinfo.duty, userinfo.role, userinfo.validate, "
				+ "userinfo.sortNum, userinfo.caId) "
				+ "from Users userinfo where userinfo.role != 1";  
			String COUNT = " select count(*) from Users userinfo where userinfo.role != 1";
			if (sort != null && dir != null)
			{
				String q = " order by  " + sort + " " + dir; 
				queryString = COUNT + q+cond;				
				List lo = findAllBySql(queryString);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
				queryString = VIEW + q+cond;
			}
			else
			{
				queryString = COUNT+cond;
				
				List lo = findAllBySql(queryString);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
				queryString = VIEW+cond;
			}
			List<AdminUserinfoView> l = findAllBySql(start, length, queryString, null); //(index - 1) * length, length, queryString, null);				
			DataHolder dh = new DataHolder();
			dh.setIntData(size);
			dh.setAdminData(l);
			return dh;
		}
		catch (RuntimeException re)
		{
			throw re;
		}
	}
	
	/**
	 * 根据组Id得到组中的所有用户，如果treeFlag为true则递归查询组中所有子组中
	 * 的用户，为false，则值查询该级组中的用户。
	 * @param groupId
	 * @param treeFlag
	 * @return
	 */
	public List<AdminUserinfoView> findUserViewByGroupId(Long groupId, boolean treeFlag)
	{		
		try
		{
			List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
			List<AdminUserinfoView> temp = null;
			if (!treeFlag)
			{
				String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersGroups as model where model.group.id = ? ";
				temp = findAllBySql(queryString, groupId);
			}
			else
			{
				Groups parent = (Groups) find(Groups.class, groupId);
				if (parent != null)
				{
					String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersGroups as model where model.group.id = ? or model.group.parentKey like ? ";
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parent.getId() + "-%";	
					temp = findAllBySql(queryString, groupId, key);
				}
			}
			if (temp != null && temp.size() > 0)
			{
				for (AdminUserinfoView auv : temp)
				{
					auv.setRoles(getUserGroupRole(auv.getUserId(), groupId, true));
					auv.setOrganization(findOrganizationsByUserId(auv.getUserId()));					
					ret.add(auv);
				}
			}
			return ret;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 根据用户自定义组Id得到用户自定义组中的所有用户
	 * @param teamId
	 * @return
	 */
	public List<AdminUserinfoView> findUserViewByTeamId(Long teamId)
	{		
		try
		{
			List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
			List<AdminUserinfoView> temp = null;
			String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersCustomTeams as model where model.customTeam.id = ? ";
			temp = findAllBySql(queryString, teamId);
			if (temp != null && temp.size() > 0)
			{
				for (AdminUserinfoView auv : temp)
				{
					auv.setRoles(getUserTeamRole(auv.getUserId(), teamId, true));
					auv.setOrganization(findOrganizationsByUserId(auv.getUserId()));	
					ret.add(auv);
				}
			}
			return ret;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	public List<AdminUserinfoView> findUserViewByOrgId(Long orgId, boolean treeFlag)
	{		
		try
		{
			List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
			List<AdminUserinfoView> temp = null;
			if (!treeFlag)
			{
				String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersOrganizations as model where model.organization.id = ? ";
				temp = findAllBySql(queryString, orgId);
			}
			else
			{
				Organizations parent = (Organizations) find(Organizations.class, orgId);
				if (parent != null)
				{
					String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersOrganizations as model where model.organization.id = ? or model.organization.parentKey like ? ";
					String key = parent.getParentKey();
					if (key == null)
					{
						key = "";
					}
					key += parent.getId() + "-%";	
					temp = findAllBySql(queryString, orgId, key);
				}
			}
			if (temp != null && temp.size() > 0)
			{
				for (AdminUserinfoView auv : temp)
				{
					auv.setRoles(getUserOrgRole(auv.getUserId(), orgId, true));
					ret.add(auv);
				}
			}
			return ret;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}		
	}
	
	/**
	 * 根据用户id，组id获得用户参与的组
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public UsersGroups findUserGroup(long userId, long groupId)
	{
		try
		{
			String queryString = "select ug from UsersGroups ug where ug.user.id = ? and ug.group.id = ?"; 
			return (UsersGroups)findOneObjectBySql(queryString, userId, groupId);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 根据用户id和用户自定组id获取用户参与的用户自定义组
	 * @param userId
	 * @param teamId
	 * @return
	 */
	public UsersCustomTeams findUserTeam(long userId, long teamId)
	{
		try
		{
			String queryString = "select ug from UsersCustomTeams ug where ug.user.id = ? and ug.customTeam.id = ?"; 
			return (UsersCustomTeams)findOneObjectBySql(queryString, userId, teamId);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 根据用户id和组织结构id获取用户所在的组织结构
	 * @param userId
	 * @param orgId
	 * @return
	 */
	public UsersOrganizations findUserOrganization(long userId, long orgId)
	{
		try
		{
			String queryString = "select ug from UsersOrganizations ug where ug.user.id = ? and ug.organization.id = ?"; 
			return (UsersOrganizations)findOneObjectBySql(queryString, userId, orgId);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 查询用户是否具有group组私有角色,目前前端只支持一个用户在一个组中只有一个角色
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public UsersRoles findUserRoleInGroup(long userId, long groupId)
	{
		try
		{
			String queryString = "select ug from UsersRoles ug where ug.user.id = ? and ug.role.group.id = ?"; 
			return (UsersRoles)findOneObjectBySql(queryString, userId, groupId); // 目前前端只支持一个用户在一个组中只有一个角色
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 查询用户是否具有组织结构私有角色,目前前端只支持一个用户在一个组织中只有一个角色
	 * @param userId
	 * @param orgId
	 * @return
	 */
	public UsersRoles findUserRoleInOrg(long userId, long orgId)
	{
		try
		{
			String queryString = "select ug from UsersRoles ug where ug.user.id = ? and ug.role.organization.id = ?"; 
			return (UsersRoles)findOneObjectBySql(queryString, userId, orgId); // 目前前端只支持一个用户在一个组织中只有一个角色
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 查询用户是否具有自定义组中私有角色,目前前端只支持一个用户在一个用户自定义组中只有一个角色
	 * @param userId
	 * @param teamId
	 * @return
	 */
	public UsersRoles findUserRoleInTeam(long userId, long teamId)
	{
		try
		{
			String queryString = "select ug from UsersRoles ug where ug.user.id = ? and ug.role.team.id = ?"; 
			return (UsersRoles)findOneObjectBySql(queryString, userId, teamId); // 目前前端只支持一个用户在一个用户自定义组中只有一个角色
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 通过用户id和角色id获取用户是否有该角色
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public UsersRoles findUserRole(long userId, long roleId)
	{
		try
		{
			String queryString = "select ug from UsersRoles ug where ug.user.id = ? and ug.role.id = ?"; 
			return (UsersRoles)findOneObjectBySql(queryString, userId, roleId);  // 目前前端只支持一个角色
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 获取用户当前的系统角色,目前前端只支持一个系统角色
	 * @param isTemp
	 * @return
	 */
	public UsersRoles findUserRoleInSystem(Long userId, Long companyId)
	{
		String queryString = "select ur from UsersRoles ur where ur.user.id = ? and ur.role.company.id = ? "
			+ " and ur.role.organization is null and  ur.role.group is null and ur.role.team is null and ur.role.type = ? ";
		return (UsersRoles)findOneObjectBySql(queryString, userId, companyId, RoleCons.SYSTEM);  // 目前前端只支持一个系统角色
		/*List<UsersRoles> ret = findAllBySql(queryString, userId, RoleCons.APPLY);
		if (ret != null && ret.size() > 0)   // 目前前端只支持一个系统角色
		{
			return ret.get(0);
		}
		return null;*/
	}
	
	/**
	 * 获取用户当前的公司角色,目前前端只支持一个公司 角色
	 * @param isTemp
	 * @return
	 */
	public UsersRoles findUserRoleInCompany(Long userId, Long companyId)
	{
		String queryString = "select ur from UsersRoles ur where ur.user.id = ? and ur.role.company.id = ? "
			+ " and ur.role.organization is null and  ur.role.group is null and ur.role.team is null and ur.role.type = ? ";
		return (UsersRoles)findOneObjectBySql(queryString, userId, companyId, RoleCons.SPACE);  // 目前前端只支持一个系统角色
	}
	
	
	/**
	 * 得用户的头像名
	 * @param userId
	 * @return
	 */
	public String getUserPortraitName(Long userId)
	{
		Users user = this.findUserById(userId);
		return user != null ? user.getImage1() : null;
	}
	/**
	 * 判断用户是否已经存在
	 * @param username1
	 * @return
	 */
	public Users isExistUsername1(String name1)
	{
		String queryString = "select u from Users u where u.username1 = ?";
		Users us = (Users) findOneObjectBySql(queryString, name1);
		return us;
	}
	
	/**
	 * 判断用户是否已经存在
	 * @param username2
	 * @return
	 */
	public Users isExistUsername2(String name2)
	{
		String queryString = "select u from Users u where u.username2 = ?";
		Users us = (Users) findOneObjectBySql(queryString, name2);
		return us;
	}
	/**
	 * 得到组的头像名
	 * @param groupId
	 * @return
	 */
	public String getGroupPortraitName(Long groupId)
	{
		Groups group = this.findGroupById(groupId);
		return group != null ? group.getImage1() : null;
	}
	
	/**
	 * 得到组织结构的头像名
	 * @param orgId
	 * @return
	 */
	public String getOrgPortraitName(Long orgId)
	{
		Organizations org = this.findOrganizationsById(orgId);
		return org != null ? org.getImage1() : null;
	}
	
	
	/**
	 * 判断用户是否已经存在
	 * @param name
	 * @return
	 */
	public Users isExistUser(String name)
	{
		String queryString = "select u from Users u where u.userName = ?";
		Users us = (Users) findOneObjectBySql(queryString, name);
		return us;
	}
		
	/**
	 * 判断用户是否已经存在
	 * @param name
	 * @return
	 */
	public Users isExistUser(String name, String companyName)
	{
		String queryString = "select u from Users u where u.userName = ? and u.company.name = ? ";
		Users us = (Users) findOneObjectBySql(queryString, name, companyName);
		return us;
	}
	
	/**
	 * 判断单位是否存在，存在直接返回ID
	 * @param orgname
	 * @return
	 */
	@Deprecated
	public Long isExistOrg(String orgname)
	{
		String queryString = "select u from Organizations u where u.name = ?";
		List<Organizations> os = findAllBySql(queryString, orgname);
		if (os != null && os.size() > 0)
		{
			return os.get(0).getId();
		}
		return null;
	}
	
	/**
	 * 判断组织结构是否存在，存在直接返回ID,该方法有问题，因为在同一单位中，可能存在组织名相同的情况
	 * @param orgname
	 * @return
	 */
	@Deprecated
	public Long isExistOrg(String orgname, Long companyId)
	{
		String queryString = "select u from Organizations u where u.name = ? and u.company.id = ?";
		Organizations os = (Organizations)findOneObjectBySql(queryString, orgname, companyId);
		if (os != null)
		{
			return os.getId();
		}
		return null;
	}
	
	@Deprecated
	public List<Users> getAllUsersButSelf(long userid)
	{
	//	String queryString = "select u from Users u where u.id != ? and u.calendarPublic = true";
		String queryString = "select u from Users u where u.id != ?";
		List<Users> us = findAllBySql(queryString, userid);
		return us;
	}
	
	/*
	 * get user which  calendar is public 
	 */
	@Deprecated
	public List<Users> getAllUsersPublic(long userid)
	{
		String queryString = "select u from Users u where u.id != ? and u.calendarPublic = true";
		List<Users> us = findAllBySql(queryString, userid);
		return us;
	}
	
	@Deprecated
	public List<Users> searchUser(String str,Long userid,Long orgId)
	{
		//String queryString = "select u from Users u where (u.userName like '%"+str+"%' or u.realName like '%"+str+"%')"
		//+" and u.id != ?";
		//List<Users> us = findAllBySql(queryString,userid);
		//return us;
		
		Organizations parent = (Organizations) find(Organizations.class, orgId);
		if (parent != null)
		{
			String queryString = "select u.user from UsersOrganizations as u where (u.user.userName like '%"+str+"%' or u.user.realName like '%"+str+"%')"
		+" and u.user.id != ? and  (u.organization.id = ? or u.organization.parentKey like ?) ";
			String key = parent.getParentKey();
			if (key == null)
			{
				key = "";
			}
			key += parent.getId() + "-%";	
			return findAllBySql(queryString,userid, orgId, key);
		}
		return new ArrayList();
	}
	
	/**
     * 获取系统中用户信息。如果start和count不小于0，则表示分页获取。
     * @param start 开始的位置，如果小于0，在表示从第一条记录开始获取。
     * @param count 一次获取的数量，如果小于0，则表示从start开始后的所有用户数据。
     * @param sort 排序字段名
     * @param dir 排序的方式（asc或desc）
     * @return
     */
	@Deprecated
    public List<Users> getUsers(int start, int count, String sort, String dir)
    {
    	String queryString = " select u from Users u ";
		if (sort != null && dir != null)
		{
			queryString += " order by u." + sort + " " + dir;
		}
		return (List<Users>) findAllBySql(start, count, queryString);	
    }
    
    /**
     * 获取系统中用户信息。如果start和count不小于0，则表示分页获取。
     * @param start 开始的位置，如果小于0，在表示从第一条记录开始获取。
     * @param count 一次获取的数量，如果小于0，则表示从start开始后的所有用户数据。
     * @param sort 排序字段名
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> getUsers(long companyId, int start, int count, String sort, String dir)
    {
    	String queryString = " select u from Users u where u.company.id = ? ";
		if (sort != null && dir != null)
		{
			queryString += " order by u." + sort + " " + dir;
		}
		return (List<Users>) findAllBySql(start, count, queryString, companyId);	
    }
    /**
     * 判断用户是否已经存在
     * @param name
     * @return
     */
    public Users getUserByName(String name)
    {
        String queryString = "select u from Users u where u.userName = ?";
        List<Users> us = findAllBySql(queryString, name);
        if (us != null && us.size() > 0)
        {
            return us.get(0);
        }
        return null;
    }
    
    /**
     *获取公司的总用户数
     * @return
     */
    public Long getUsersCount(Long companyId)
    {
    	String queryString = " select count(u) from Users u where u.company.id = ? ";
		return getCountBySql(queryString, companyId);	
    }
    
    // ???
    public List<Users> getUsers(int start, int count, String sort, String dir, Users user)
    {
    	String queryString = " select u from Users u ";
    	if (user!=null)
    	{
    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(user.getId());
    		if (list!=null && list.size()>0)
    		{
    			String orgcode=list.get(0).getOrganization().getOrganizecode();
    			if (orgcode!=null)
    			{
    				int index = orgcode.indexOf("-");
    				if (index>0)
    				{
    					orgcode=orgcode.substring(0,index);
    					queryString+=" where u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
    				}
    				else
    				{
    					queryString+=" where u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
    				}
    			}
    		}
    	}
		if (sort != null && dir != null)
		{
			queryString += " order by u." + sort + " " + dir;
		}
		return (List<Users>) findAllBySql(start, count, queryString);	
    }
    /**
     * 搜索符合keyword条件的用户。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @return
     */
    @Deprecated
    public long getUser(int option, String keyWord)
    {
    	String query = "select count(*) from Users u ";
    	String par = "%" + keyWord + "%";
    	if (option == 0)
		{
    		query += " where u.userName like ? ";
    		return getCountBySql(query, par);
		}
		else if (option == 1)
		{
			query += " where u.realEmail like ? ";
			return getCountBySql(query, par);
		}
    	query += " where u.userName like ? or u.realEmail like ? ";
    	return getCountBySql(query, par, par);
			
    }
    
    /**
     * 搜索符合keyword条件的用户。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @return
     */
    public long getUser(int option, String keyWord, long companyId)
    {
    	String query = "select count(*) from Users u ";
    	String par = "%" + keyWord + "%";
    	if (option == 0)
		{
    		query += " where u.userName like ? and u.company.id = ?";
    		return getCountBySql(query, par, companyId);
		}
		else if (option == 1)
		{
			query += " where u.realEmail like ?  and u.company.id = ?";
			return getCountBySql(query, par, companyId);
		}
    	query += " where u.userName like ? or u.realEmail like ?  and u.company.id = ?";
    	return getCountBySql(query, par, par, companyId);
			
    }
    
    /**
     * 搜索符合keyword条件的用户，并根据需要分页显示。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @param start 搜索的开始位置，小于0，表示从第一个记录开始搜索。
     * @param count 搜索的数量，小于0，表示从start位置开始后的所有记录。
     * @param sort 排序字段
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    @Deprecated
    public List<Users> getUser(int option, String keyWord, int start, int count, String sort, String dir)
    {
    	String query = " select u from Users u ";
    	String par = "%" + keyWord + "%";
		if (option == 0)
		{
			if (sort != null && dir != null)
			{
				query  += " where u.userName like ? order by u." + sort + " " + dir;
			}
			else
			{
				query += " where u.userName like ? ";
			}
			return (List<Users>) findAllBySql(start, count, query, par);
		}
		else if (option == 1)
		{
			if (sort != null && dir != null)
			{
				query += " where u.realEmail like ? order by u." + sort + " " + dir;
			}
			else
			{
				query = " where u.realEmail like ? ";
			}
			return (List<Users>) findAllBySql(start, count, query, par);
		}
		if (sort != null && dir != null)
		{
			query += " where (u.userName like ? or u.realName like ? or u.realEmail ?)  order by u." + sort + " " + dir;
		}
		else
		{
			query += " where (u.userName like ? or u.realName like ? or u.realEmail like ?) ";
		}
		List<Users> userlist=(List<Users>) findAllBySql(start, count, query, par, par, par);
		return userlist;
    }
    
    /**
     * 搜索符合keyword条件的用户，并根据需要分页显示。 
     * @param option 搜索的方式，0表示搜索用户登录名，1表示搜索邮件地址，2表示搜索用户显示名，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @param start 搜索的开始位置，小于0，表示从第一个记录开始搜索。
     * @param count 搜索的数量，小于0，表示从start位置开始后的所有记录。
     * @param sort 排序字段
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> searchUser(long companyId, int option, String keyWord, int start, int count, String sort, String dir)
    {
    	if(companyId==0||companyId==0l){
    	    String   query = " select u from Users u where ";
    	    String par = "%" + keyWord + "%";
    	    query += " u.userName like ? or u.realName like ? or u.realEmail like ? ";
    	    List<Users> userlist=(List<Users>) findAllBySql(start, count, query, par, par, par);
    	    return userlist;
    	}
    	else{
    	   String	query = " select u from Users u where u.company.id = ?  ";
    	   String par = "%" + keyWord + "%";
		if (option == 0)
		{
			if (sort != null && dir != null)
			{
				query  += " and  u.userName like ?  order by u." + sort + " " + dir;
			}
			else
			{
				query += " and  u.userName like ? ";
			}
			return (List<Users>) findAllBySql(start, count, query, companyId, par);
		}
		else if (option == 1)
		{
			if (sort != null && dir != null)
			{
				query += " and  u.realEmail like ? order by u." + sort + " " + dir;
			}
			else
			{
				query = " and  u.realEmail like ? ";
			}
			return (List<Users>) findAllBySql(start, count, query, companyId, par);
		}
		else if (option == 2)
		{
			if (sort != null && dir != null)
			{
				query += " and  u.realName like ? order by u." + sort + " " + dir;
			}
			else
			{
				query = " and  u.realName like ? ";
			}
			return (List<Users>) findAllBySql(start, count, query, companyId, par);
		}
		if (sort != null && dir != null)
		{
			query += "  and (u.userName like ? or u.realName like ? or u.realEmail like ?)  order by u." + sort + " " + dir;
		}
		else
		{
			query += " and  (u.userName like ? or u.realName like ? or u.realEmail like ?) ";
		}
		List<Users> userlist=(List<Users>) findAllBySql(start, count, query, companyId, par, par, par);
		return userlist;
    	}
    }
    
    
    // ??
    @Deprecated
    public List<Users> getUser(int option, String keyWord, int start, int count, String sort, String dir,Users user)
    {
    	String query = " select u from Users u ";
    	String par = "%" + keyWord + "%";
    	String cond="";
    	if (user!=null)
    	{
    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(user.getId());
    		if (list!=null && list.size()>0)
    		{
    			String orgcode=list.get(0).getOrganization().getOrganizecode();
    			if (orgcode!=null)
    			{
    				int index = orgcode.indexOf("-");
    				if (index>0)
    				{
    					orgcode=orgcode.substring(0,index);
    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
    				}
    				else
    				{
    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
    				}
    			}
    		}
    	}
		if (option == 0)
		{
			if (sort != null && dir != null)
			{
				query  += " where u.userName like '"+par+"' "+cond+" order by u." + sort + " " + dir;
			}
			else
			{
				query += " where u.userName like '"+par+"'  "+cond;
			}
			return (List<Users>) findAllBySql(start, count, query, par);
		}
		else if (option == 1)
		{
			if (sort != null && dir != null)
			{
				query += " where u.realEmail like '"+par+"'  "+cond+" order by u." + sort + " " + dir;
			}
			else
			{
				query = " where u.realEmail like '"+par+"'  "+cond;
			}
			return (List<Users>) findAllBySql(start, count, query, par);
		}
		if (sort != null && dir != null)
		{
			query += " where (u.userName like '"+par+"' or u.realName like '"+par+"' or u.realEmail like '"+par+"' ) "+cond+" order by u." + sort + " " + dir;
		}
		else
		{
			query += " where (u.userName like '"+par+"' or u.realName like '"+par+"' or u.realEmail like '"+par+"' ) "+cond;
		}
		
		
		return (List<Users>) findAllBySql(start, count, query, par, par);	
    }
    
    
    // ???
    @Deprecated
    public long getUserCount(int option, String keyWord,Users user)
    {
    	try
    	{
	    	String query = " select count(*) from Users u ";
	    	if (keyWord!=null && keyWord.length()>0)
	    	{
		    	String par = "%" + keyWord + "%";
		    	String cond="";
		    	if (user!=null)
		    	{
		    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(user.getId());
		    		if (list!=null && list.size()>0)
		    		{
		    			String orgcode=list.get(0).getOrganization().getOrganizecode();
		    			if (orgcode!=null)
		    			{
		    				int index = orgcode.indexOf("-");
		    				if (index>0)
		    				{
		    					orgcode=orgcode.substring(0,index);
		    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
		    				}
		    				else
		    				{
		    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
		    				}
		    			}
		    		}
		    	}
				query += " where (u.userName like '"+par+"' or u.realName like '"+par+"' or u.realEmail like '"+par+"' ) "+cond;
				long back=getCountBySql(query);
				return back;
	    	}
	    	else
	    	{
	    		String cond="";
	    		if (user!=null)
		    	{
		    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(user.getId());
		    		if (list!=null && list.size()>0)
		    		{
		    			String orgcode=list.get(0).getOrganization().getOrganizecode();
		    			if (orgcode!=null)
		    			{
		    				int index = orgcode.indexOf("-");
		    				if (index>0)
		    				{
		    					orgcode=orgcode.substring(0,index);
		    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
		    				}
		    				else
		    				{
		    					cond=" and u.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
		    				}
		    			}
		    		}
		    	}
	    		query += " where 1=1 "+cond;
				long back=getCountBySql(query);
				return back;
	    	}
    	}
    	catch (Exception e)
    	{
    		return 0;
    	}
    }
    
    /**
     * 获取用户的配置信息
     * @param userId
     * @return
     */
    public UsersConfig getUsersConfig(Long userId)
    {
    	String query = " select model from UsersConfig model where model.user.id = ? ";
    	return (UsersConfig)findOneObjectBySql(query, userId);
    }
    
    
    /**
     * 通过spaceUID获取所属组的id值
     * @param spaceUID
     * @return
     */
    public Long getGroupIdBySpaceUID(String spaceUID)
    {
    	String query = " select id from Groups where spaceUID = ?";
    	return (Long)findOneObjectBySql(query, spaceUID);
    }
    //获取公司编号
    public Long getCompanyIdBySpaceUID(String spaceUID)
    {
    	String query = " select id from Company where spaceUID = ?";
    	return (Long)findOneObjectBySql(query, spaceUID);
    }
    
    /**
     * 通过spaceUID获取所属用户自定义组的id值
     * @param spaceUID
     * @return
     */
    public Long getTeamIdBySpaceUID(String spaceUID)
    {
    	String query = " select id from CustomTeams where spaceUID = ?";
    	return (Long)findOneObjectBySql(query, spaceUID);
    }
    
    /**
     * 同步更新头像路径(维护4张表)
     * @param id 用户ID
     * @param fileName 头像名
     */
    @Deprecated
	public void sycnUpdateImg(Long id, String fileName) 
	{
		StringBuffer query = new StringBuffer("update SessionMegPo");
		query.append(" set sendImg='").append(fileName).append("' where sendId=").append(id);
		excute(query.toString());
		query = new StringBuffer("update GroupSessionMegPo");
		query.append(" set sendImg='").append(fileName).append("' where sendId=").append(id);
		excute(query.toString());
		query = new StringBuffer("update DiscuGroupMemberPo");
		query.append(" set image='").append(fileName).append("' where memberId=").append(id);
		excute(query.toString());
		query = new StringBuffer("update CtmGroupMemberPo");
		query.append(" set image='").append(fileName).append("' where userId=").append(id);
		excute(query.toString());
	}
	
    // ???
	@Deprecated
	 public List getAllUser(Long userId, String searchTxt)
	 {
		 	String par = "%" + searchTxt + "%";
		 	String query = "";
		 	List userslist = null;
		 	if(null == searchTxt || "".equals(searchTxt))
		 	{
		 		query = "select u from Users u where u.userName !='admin' and id != "+userId;
		 		userslist=findAllBySql(query);		 		
		 	}
		 	else
		 	{
		 		query = "select u from Users u where u.userName !='admin' and (u.realName like '%"+searchTxt+"%' or u.userName like '%"+searchTxt+"%') and id!=? order by u.realName ";
//		 		query = "select u from Users u where u.userName !='admin' and u.realName like '%"+searchTxt+"%' and id!=? order by u.realName ";
		 		userslist=findAllBySql(query,userId);	
		 	}
		 	int lenuser=100;
	    	if (userslist!=null && userslist.size()>lenuser)
	    	{
	    		List backlist=new ArrayList();
	    		query="select a.organizecode from Organizations as a,UsersOrganizations as b where a.id=b.organization.id "
	    			+" and b.user.id=? ";
	    		List codelist=findAllBySql(query,userId);
	    		String cond="";
	    		if (codelist!=null && codelist.size()>0)
	    		{
	    			for (int i=0;i<codelist.size();i++)
	    			{
	    				String code=(String)codelist.get(i);
	    				int index=code.indexOf("-");
	    				if (index>0)
	    				{
	    					code=code.substring(0,index);
	    				}
	    				if (cond.length()<1)
	    				{
	    					cond+=" (a.organizecode like '"+code+"%' ";
	    				}
	    				else
	    				{
	    					cond+=" or a.organizecode like '"+code+"%' ";
	    				}
	    			}
	    			if (cond.length()>1)
	    			{
	    				cond=" and "+cond+")";
	    			}
	    			
	    			query="select u from Users as u,Organizations as a,UsersOrganizations as b where a.id=b.organization.id and u.id=b.user.id "
	    				+" and (u.realName like '%"+searchTxt+"%' or u.userName like '%"+searchTxt+"%') "
		    			+cond;
	    			List mylist=findAllBySql(query);
	    			if (mylist!=null && mylist.size()>0)
	    			{
	    				for (int i=0;i<mylist.size();i++)
	    				{
	    					Users myuser=(Users)mylist.get(i);
	    					for (int j=0;j<userslist.size();j++)
	    					{
	    						Users alluser=(Users)userslist.get(j);
	    						if (myuser.getId().longValue()==alluser.getId().longValue())
	    						{
	    							backlist.add(alluser);
	    							userslist.remove(j);
	    							break;
	    						}
	    					}
	    				}
	    				int len=lenuser-backlist.size();
	    				for (int i=0;i<len;i++)
	    				{
	    					backlist.add(userslist.get(i));
	    				}
	    				return backlist;
	    			}
	    		}
	    	}
			return userslist;	
	  }
	 
	// ???
	@Deprecated
	 public List<AdminUserinfoView> findUserViewByGroupId(Long groupId, boolean treeFlag,String searchTxt)
		{
			
			try
			{
				searchTxt =  "%" + searchTxt + "%";
				List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
				List<AdminUserinfoView> temp = null;
				if (!treeFlag)
				{
					String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersGroups as model where model.group.id = ? and (model.user.userName like ? or model.user.realEmail like ?)";
					temp = findAllBySql(queryString, groupId,searchTxt,searchTxt);
				}
				else
				{
					Groups parent = (Groups) find(Groups.class, groupId);
					if (parent != null)
					{
						String queryString = "select new com.evermore.weboffice.domain.AdminUserinfoView(model.user) from UsersGroups as model where (model.user.realName like ? or model.user.userName like ?) and (model.group.id = ? or model.group.parentKey like ?) ";
						String key = parent.getParentKey();
						if (key == null)
						{
							key = "";
						}
						key += parent.getId() + "-%";	
						temp = findAllBySql(queryString, searchTxt,searchTxt,groupId, key);
					}
				}
				if (temp != null && temp.size() > 0)
				{
					for (AdminUserinfoView auv : temp)
					{
						auv.setRoles(getUserGroupRole(auv.getUserId(), groupId, true));
						auv.setOrganization(findOrganizationsByUserId(auv.getUserId()));					
						ret.add(auv);
					}
				}
				return ret;
			}
			catch (RuntimeException re)
			{
				LogsUtility.error("find by property name failed", re);
				throw re;
			}		
		}
	 
	 @Deprecated
	 public String findUserDept(Long id)
	 {
		 try
			{
				String queryString = "select ug from UsersOrganizations ug where ug.user.id = ?"; 
				List<UsersOrganizations> ret = findAllBySql(queryString, id);
				if (ret != null && ret.size() > 0)
				{
					UsersOrganizations org = ret.get(0);
					return org.getOrganization().getName();
				}
				return null;
			}
			catch(Exception e)
			{
				return null;
			}
	 }
	 
	 /**
	  * 搜索公司下的部门组织名字
	  * @param key
	  * @param companyId
	  * @return
	  */
	public List<Organizations> findOrganizationsByKey(String key, Long companyId)
	{
		try
		{
			String queryString = "from Organizations or where or.name = ? and or.company.id = ? "; 
			return findAllBySql(queryString.toString(), "%" + key + "%", companyId);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	/**
     * 判断公司的允许的最大用户数是否已经满了
     * @param companyId
     * @return 运行的最大人数已经满了，则返回true，否则返回false。
     */
    public boolean isCompanyUserFull(Long companyId)
    {
    	String sql = "select count(*) from Company as c where c.id = ?  and c.maxUsers > (select count(*) from Users as u where u.company.id = ?) ";
    	Long ret = getCountBySql(sql, companyId, companyId);
    	return ret <= 0;
    }
    
    /**
	 * 设置用户是否有效多个用户
	 * role 1为有效，其他值为无效
	 * @param userIds
	 */
	public void forbidUserById(List<Long> userIds, short value)
	{
		if (userIds == null || userIds.size() <= 0)
		{
			return;
		}
		String queryString = "update Users set validate = ?1 where id in ( ?2 ) ";
		excute(queryString, value, userIds);
	}
	
	/**
	 * 设置用户设备是否有效
	 * value 1表示该设备需要禁用 0表示该设备正常
	 * @param userIds
	 */
	public void forbidUserDevice(List<Long> devIds, short value)
	{
		if (devIds == null || devIds.size() <= 0)
		{
			return;
		}
		String queryString = "update UsersDevice set mobDevStatus = ?1 where id in ( ?2 ) ";
		excute(queryString, value, devIds);
	}
   /**
    * 根据公司的id，得到公司的管理员
    * @param companyId
    * @return
    */
	public Users getAdminByCmpId(Long companyId) {
		String queryString = "select u from Users u where u.role = 8 and u.company.id = ?";
        List<Users> us = findAllBySql(queryString, companyId);
        if (us != null && us.size() > 0)
        {
            return us.get(0);
        }
		return null;
	}

	public List<Users> findUserByRole(short i, short j) {
		String queryString = "select u from Users u where u.role >=? and u.role <=?";
	    List<Users> uslist = findAllBySql(queryString, i,j);
	    if (uslist != null && uslist.size() > 0)
	    {
	        return uslist;
	    }
		return null;
	}
	 
	public Users findUserByname(String outname)
	{
		try
		{
			String sql = " select r from Users r where r.outname = ?";
			List<Users> list=findAllBySql(sql, outname);
			if (list!=null && list.size()>0)
			{
				return list.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error(re);
			throw re;
		}
	}
	public List<Organizations> findOrganizationsBydepid(String depid)
	{
		try
		{
			String queryString = "select model from Organizations as model where model.depid = ?";
			return findAllBySql(queryString,  depid);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get findOrganizationsBydepid", re);
			throw re;
		}
	}
	public void delUsersOrganizations(Long userid)
	{
		LogsUtility.debug("delUsersOrganizations ===");
		try
		{
			String queryString = "delete from UsersOrganizations as model where model.user.id = ? ";
			excute(queryString, userid);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delUsersOrganizations", re);
			throw re;
		}
	}
	public List<Roles> getRoleByUser(Long userId)
	{
		String queryString = "select ur.role from UsersRoles ur where ur.user.id = ?";
		return findAllBySql(queryString, userId);
	}
	public void delUsersOrganizationsByOrg(Long orgid)
	{
		LogsUtility.debug("delUsersOrganizationsByOrg ===");
		try
		{
			String queryString = "from UsersOrganizations as model where model.organization.id = ? ";
			excute(queryString, orgid);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delUsersOrganizationsByOrg", re);
			throw re;
		}
	}
	public List<Users> getAllNormalUser()
	 {//获取所有普通用户信息
		String query = "select u from Users u where u.userName !='admin' ";
		List<Users> userslist=findAllBySql(query);		 		
		 	
		return userslist;	
	 }
	public List<Organizations> getAllNormalOrganizations()
	 {//获取所有普通用户信息
		String query = "select u from Organizations u ";
		List<Organizations> orglist=findAllBySql(query);
		return orglist;	
	 }
	public Organizations getRootOrg(Long userId) {
		List<UsersOrganizations> list = findByProperty(UsersOrganizations.class.getName(), "user.id", userId);
		if (list != null && list.size() > 0) {
			String parentkey = list.get(0).getOrganization().getParentKey();
			if (parentkey != null) {
				int index = parentkey.indexOf("-");
				if (index != -1) {
					parentkey = parentkey.substring(0, index);
				}
				Organizations org=(Organizations)this.find(Organizations.class, Long.valueOf(parentkey));
				return org;
			}
			else
			{
				return list.get(0).getOrganization();
			}
		}
		return null;
	}
}
