package apps.transmanager.weboffice.service.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.FileSystemActions;
import apps.transmanager.weboffice.databaseobject.FileSystemResources;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.SpacesActions;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersPermissions;
import apps.transmanager.weboffice.domain.Actions;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.IPermissions;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.DateUtils;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.JSONTools;

public class BFilesOpeHandler {

	// 参考FilesOpeHandler ，addAndUpdateTeamSpace
	public static String addOrUpdateGroup(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String name = (String) param.get("name");// 群组名称
			String groupId = (String) param.get("groupId");// 群组Id(如果是修改组是用到)
			String description = (String) param.get("description"); // 群组描述
			String status = (String) param.get("status"); // 群组状态
			List<String> addUserIdsS = (List<String>) param.get("addUserIds");
			List<String> delUserIdsS = (List<String>) param.get("delUserIds");
			Number leaderIdS = (Number) param.get("leaderId");
			// /////////////////////////////////////////////////////////
			Long parentId = null;

			Groups group = new Groups();
			// 修改空间公告等
			if (groupId != null && !groupId.equals("")) {

				Long spacepermission = getGroupSpacePermission(user.getId(),
						Long.parseLong(groupId));
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.PUBLIC_BULLETIN_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
				group.setId(Long.parseLong(groupId));
			} else// 新建
			{
				// 权限判断：
				long permission = getSpacePermission(user.getId());
				boolean flag = FlagUtility.isLongFlag(permission,
						ManagementCons.CREATE_SPACE);
				if (!flag) {
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,  "no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}
			group.setName(name);
			group.setDescription(description);

			Long[] addUserIds = addUserIdsS == null ? null : FilesOpeHandler
					.stringToLong(addUserIdsS).toArray(new Long[0]);
			Long[] roleIds = null;
			List<Long> delUserIds = FilesOpeHandler.stringToLong(delUserIdsS);
			Long leaderId = leaderIdS == null ? user.getId() : leaderIdS
					.longValue();// 创建者//需要从前台设置进入

			Spaces space = new Spaces();
			space.setName(name);
			space.setDescription(description);
			space.setSpaceStatus(status);

			addOrUpdateGroup(parentId, group, addUserIds, roleIds, delUserIds,
					leaderId, space);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*} catch (Exception ee) {
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/

	}

	/*
	 * public static void addOrUpdateGroup(HttpServletRequest req,
	 * HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user)
	 * throws ServletException, IOException { String error; try {
	 * HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
	 * .get(ServletConst.PARAMS_KEY); Long parentId = ((Number)
	 * param.get("parentId")).longValue();
	 * 
	 * JSONObject jo2 = JSONObject.fromObject(param.get("group")); Groups group
	 * = (Groups) JSONObject.toBean(jo2, Class
	 * .forName("com.evermore.weboffice.databaseobject.Groups"));
	 * 
	 * Long[] addUserIds = (Long[]) JSONTools.convert2LongArray(param
	 * .get("addUserIds"));
	 * 
	 * Long[] roleIds = (Long[])
	 * JSONTools.convert2LongArray(param.get("roleIds"));
	 * 
	 * List<Long> delUserIds = new ArrayList<Long>();
	 * JSONTools.convert2List(delUserIds, param.get("delUserIds"), Long.class);
	 * 
	 * Long leaderId = ((Number) param.get("leaderId")).longValue();
	 * 
	 * jo2 = JSONObject.fromObject(param.get("group")); Spaces space = (Spaces)
	 * JSONObject.toBean(jo2, Class
	 * .forName("com.evermore.weboffice.databaseobject.Spaces"));
	 * 
	 * Spaces spaces = addOrUpdateGroup(parentId, group, addUserIds, roleIds,
	 * delUserIds, leaderId, space);
	 * 
	 * error = JSONTools.convertToJson(ErrorCons.NO_ERROR, spaces);
	 * 
	 * } catch (Exception e) { error =
	 * JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null); }
	 * resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	 * resp.getWriter().write(error); }
	 */

	/**
	 * 新建或修改组
	 * 
	 * @param parentId
	 *            新的父组，没有为null
	 * @param group
	 *            新增或修改的组
	 * @param addUserIds
	 *            在组中的新增成员
	 * @param roleIds
	 *            组成员对应的role的角色id值，如果没有角色，则赋值为null，该数组的下标需要同
	 *            addUserIds的下标对应，即是addUserIds[index]和roleIds[index]是同一个用户的角色。
	 * @param delUserIds
	 *            删除组中的成员
	 * @param leaderId
	 *            组的负责人
	 * @param space
	 *            组的空间，如果在修改组的时候，不需要修改空间的属性，则该值传null。
	 */
	private static Spaces addOrUpdateGroup(Long parentId, Groups group,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds,
			Long leaderId, Spaces space) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		return userService.addOrUpdateGroup(parentId, group, addUserIds,
				roleIds, delUserIds, leaderId, space);
	}

	// 参考FilesOpeHandler.getFileList companyFile
	public static String getGroupSpacesByUserId(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			Long userId = ((Number) param.get("userId")).longValue();

			List<Spaces> list = getGroupSpacesByUserId(userId);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, list);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 获得用户所在组的空间
	 * 
	 * @param userId
	 * @return
	 */
	private static List<Spaces> getGroupSpacesByUserId(Long userId) {
		FileSystemService fss = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		List<Spaces> ret = new ArrayList<Spaces>();
		List<Spaces> temp = fss.getGroupSpacesByUserId(userId);
		ret.addAll(temp);
		// temp = fss.findUserManageGroupSpaceByUserId(userId);
		// ret.addAll(temp);

		return ret;
	}

	public static String deleteGroupSpaces(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> spaceUIDs = new ArrayList<String>();
			JSONTools.convert2List(spaceUIDs, param.get("spaceUIDs"),
					String.class);

			// 权限判断：
			ArrayList<String> canntdel = new ArrayList<String>();
			ArrayList<String> candel = new ArrayList<String>();
			boolean isGroup = isGroup(spaceUIDs);
			FileSystemService fss = (FileSystemService) ApplicationContext
			.getInstance().getBean(FileSystemService.NAME);
			for (int i = 0; i < spaceUIDs.size(); i++) {
				boolean flag = isGroup ? canDelGroupSpacesByUserId(spaceUIDs.get(i),
						user.getId()) : fss.canDelTeamSpacesByUserId(spaceUIDs.get(i),
						user.getId());
				if (flag) {
					candel.add(spaceUIDs.get(i));
				} else {
					canntdel.add(spaceUIDs.get(i));
				}
			}
			deleteGroupSpaces(candel);
			return  canntdel.size() == 0 ? JSONTools.convertToJson(
					ErrorCons.NO_ERROR, "ok") : JSONTools.convertToJson(
					ErrorCons.PERMISSION_ERROR, canntdel);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 删除组
	 * 
	 * @param groupIds
	 */
	private static void deleteGroupSpaces(List<String> spaceUIDs) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		userService.deleteGroupSpaces(spaceUIDs);
	}

	// 参考 FilesOpeHandler.getUsersByTeamId
	public static String getUsersByGroupId(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			List<Long> groupId = new ArrayList<Long>();
			groupId.add(((Number) param.get("groupId")).longValue());

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			List<AdminUserinfoView> list = userService.getUserViewByGroupId(
					groupId, false);

			Groups group = userService.findGroupById(((Number) param
					.get("groupId")).longValue());
			ArrayList json = new ArrayList();
			for (AdminUserinfoView A : list) {
				HashMap<String, Object> userInfo = new HashMap<String, Object>();
				userInfo.put("id", A.getId());
				userInfo.put("realName", A.getRealName());
				userInfo.put("userName", A.getUserName());
				List<Roles> roleList = A.getRoles();
				if (roleList == null || roleList.size() < 1) {
					if (A.getUserId().longValue() == group.getManager().getId()
							.longValue()) {
						userInfo.put("roleName", "创建者");
						userInfo.put("roleId", "");
					} else {
						userInfo.put("roleName", "无");
						userInfo.put("roleId", "");
					}
				} else {
					Roles role = roleList.get(0);
					if (role != null) {
						userInfo.put("roleName", role.getRoleName());
						userInfo.put("roleId", role.getRoleId().toString());
					} else {
						userInfo.put("roleName", "无");
						userInfo.put("roleId", "");
					}
				}
				userInfo.put("role", A.getRole());
				userInfo.put("email", A.getEmail());
				json.add(userInfo);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
		/*} catch (Exception ee) {
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	// public static void getUsersByGroupId(HttpServletRequest req,
	// HttpServletResponse resp, HashMap<String, Object> jsonParams,
	// Users user) throws ServletException, IOException {
	// String error;
	// try {
	// HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
	// .get(ServletConst.PARAMS_KEY);
	// List<Long> groupId = new ArrayList<Long>();
	// JSONTools.convert2List(groupId, param.get("groupId"), Long.class);
	//
	// Boolean treeFlag = (Boolean) param.get("treeFlag");
	// List<Users> lists = getUsersByGroupId(groupId, treeFlag);
	//
	// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, lists);
	//
	// } catch (Exception e) {
	// error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
	// }
	// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	// resp.getWriter().write(error);
	// }

	/**
	 * 根据组Id得到组中的所有用户，如果treeFlag为true则递归查询组中所有子组中 的用户，为false，则值查询该级组中的用户。
	 * 
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	// private static List<Users> getUsersByGroupId(List<Long> groupId,
	// boolean treeFlag) {
	// UserService userService = (UserService) ApplicationContext
	// .getInstance().getBean(UserService.NAME);
	// List<Users> result = userService.getUsersByGroupId(groupId, treeFlag);
	// result = WebTools.sortUsers(result, "realName", true);//
	// 宋学永要求所有人员信息显示都默认按姓名排序
	// return result;
	//
	// }

	// 参考 FilesOpeHandler modifyUsersByTeamId
	public static String addOrUpdateGroupMembers(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			/*
			 * Long groupId = ((Number) param.get("groupId")).longValue();
			 * 
			 * Long[] addUserIds = (Long[]) JSONTools.convert2LongArray(param
			 * .get("addUserIds"));
			 * 
			 * Long[] roleIds = (Long[]) JSONTools.convert2LongArray(param
			 * .get("roleIds"));
			 * 
			 * List<Long> delUserIds = new ArrayList<Long>();
			 * JSONTools.convert2List(delUserIds, param.get("delUserIds"),
			 * Long.class);
			 * 
			 * addOrUpdateGroupMembers(groupId, addUserIds, roleIds,
			 * delUserIds);
			 */
			Long groupId = ((Number) param.get("groupId")).longValue();// 群组Id(如果是修改组是用到)

			// Number groupId = (Number) param.get("groupId");//目前没有传入
			if (groupId != null && !"".equals(groupId) && !"-1".equals(groupId)) {
				Long spacepermission = getGroupSpacePermission(user.getId(),
						groupId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.ADD_MEMBER_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}

			Long[] addUserIds = JSONTools.convert2LongArray(param.get("addUserIds"));
			List<String> roleIdsList = (List<String>) param.get("roleIds");
			Long[] roleIds = roleIdsList == null ? null : FilesOpeHandler
					.stringToLong(roleIdsList).toArray(new Long[0]);
			List<String> delUserIdsList = (List<String>) param.get("delUserIds");
			List<Long> delUserIds = FilesOpeHandler
					.stringToLong(delUserIdsList);
			addOrUpdateGroupMembers(groupId, addUserIds, roleIds, delUserIds);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 给组中增加成员或删除成员或修改组成员
	 * 
	 * @param groupId
	 *            组Id
	 * @param addUserIds
	 *            增加或修改的成员id
	 * @param roleIds
	 *            成员的角色id
	 * @param delUserIds
	 *            删除成员id
	 */
	private static void addOrUpdateGroupMembers(long groupId,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		userService.addOrUpdateGroupMembers(groupId, addUserIds, roleIds,
				delUserIds);
	}

	public static String getAllFileSystemPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			List<IPermissions> lists;
			if(path.startsWith(FileConstants.COMPANY_ROOT))
			{
				Long companyId = user.getCompany().getId();
				 lists = getAllFileSystemPermission(path,companyId);
			}
			
			else{
				 lists = getAllFileSystemPermission(path);
			}
			

			List<HashMap<String, Object>> pblists = new ArrayList<HashMap<String, Object>>();
			for (IPermissions permissions : lists) {
				HashMap<String, Object> pe = new HashMap<String, Object>();
				UsersPermissions userP = (UsersPermissions) permissions;
				FileSystemActions action = (FileSystemActions) permissions
						.getPermission().getAction();
				pe.put("name", userP.getUser().getRealName());
				pe.put("userid", userP.getUser().getId());
				pe.put("permit", action.getAction());
				pblists.add(pe);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, pblists);

/*		} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 获得某个文件资源拥有的权限，包括赋予给用户的，赋予给组织的，赋予给组的， 赋予给角色的，赋予给用户自定义组的。
	 * 
	 * @param path
	 *            文件系统资源路径
	 * @return
	 */
	private static List<IPermissions> getAllFileSystemPermission(String path) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		return service.getAllFileSystemPermission(path);
	}
	
	/**
	 * 获取企业文库资源拥有的权限
	 * @param path
	 * 			  文件系统资源路径,若当前目录无权限值，则向其上级路径查找
	 * @param companyId
	 * 				   公司id
	 * @return
	 */
	private static List<IPermissions> getAllFileSystemPermission(String path, Long companyId ) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		FileSystemResources resource = new FileSystemResources();
		resource.setAbstractPath(path);
		List<IPermissions> permission = service.getAllFileSystemPermission(path);
		if(permission.size() == 0)
		{
			while(!path.endsWith(FileConstants.DOC))
			{
				path = path.substring(0, path.lastIndexOf("/"));
				permission = service.getAllFileSystemPermission(path);
				if(permission.size()!=0)
				{
					//将继承自父目录的权限写保存为自己的目录
					break;
				}
			}
//			将父文件夹权限设置为当前目录权限
			/*List<Long> addUserLists = new ArrayList<Long>();
			List<FileSystemActions> actionslist = new ArrayList<FileSystemActions>();
			
			for (IPermissions permissions : permission) {
				UsersPermissions userP = (UsersPermissions) permissions;
				FileSystemActions action = (FileSystemActions) permissions
						.getPermission().getAction();
				actionslist.add(action);
				addUserLists.add(userP.getUser().getId());
			}
			FileSystemActions[] actions = actionslist
					.toArray(new FileSystemActions[0]);
			Long[] addUserIds = addUserLists.toArray(new Long[0]);
			addOrUpdateFileSystemPermission(resource, companyId, 1, actions,
					addUserIds, null);*/
			
		}
		return permission;
	}
	
	

	// 参数 参考PowerWindow.doSendInvite
	public static String addOrUpdateFileSystemPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			// Number groupId = (Number) param.get("groupId");// 目前没有传入
			String spaceUID = (String) param.get("spaceUID");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			PermissionService service = (PermissionService) ApplicationContext
					.getInstance().getBean("permissionService");
			Long ownId = null;
			Integer ownType = (Integer) param.get("ownType");
			if (isGroup(spaceUID)) {
//				Long groupId = userService.getGroupIdBySpaceUID(spaceUID);// sp.getGroup().getId();
//				if (groupId==null || groupId.longValue()==0)
//				{
//					//孙爱华增加的，如果不存在会报错的，如果做了初始化，这段代码就不需要了
//					Groups groups=new Groups();
//					groups.setSpaceUID(spaceUID);
//					groups.setName(spaceUID);
//					userService.save(groups);
//					groupId=groups.getId();
//				}
//				ownId = groupId;
//				if (groupId != null && groupId.intValue() != -1) {
					/*Long spacepermission = getGroupSpacePermission(
							user.getId(), groupId.longValue());*/
					ownId = user.getCompany().getId();//userService.getGroupIdBySpaceUID(spaceUID);
					ownType = FileSystemCons.COMPANY_OWN;
					Long spacepermission = getGroupSpacePermission(user.getId(),null);
					spacepermission = spaceUID.equals(user.getCompany().getSpaceUID())&&service.isCompanyAdmin(user.getId()) ? FileSystemCons.SPACE_MANAGER:spacepermission;
					boolean spacepermissionflag = FlagUtility.isValue(
							spacepermission, SpaceConstants.PERMISSION_FLAG);
					if (!spacepermissionflag)// 没有权限
					{
						return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						//resp.setHeader("Cache-Control",								"no-store,no-cache,must-revalidate");
						//resp.getWriter().write(error);
						//return;
					}					
//				}
			} else {
				Long teamId = userService.getTeamIdBySpaceUID(spaceUID);
				ownId = teamId;
				// 判断team的权限

				if (teamId != null && teamId.intValue() != -1) {
					Long spacepermission = service.getTeamSpacePermission(
							user.getId(), teamId);
					boolean spacepermissionflag = FlagUtility.isValue(
							spacepermission, SpaceConstants.PERMISSION_FLAG);
					if (!spacepermissionflag)// 没有权限
					{
						return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						//resp.setHeader("Cache-Control",								"no-store,no-cache,must-revalidate");
						//resp.getWriter().write(error);
						//return;
					}
				}
			}
			JSONObject jo2 = JSONObject.fromObject(param.get("resource"));
			FileSystemResources resource = (FileSystemResources) JSONObject
					.toBean(jo2,	Class.forName("com.evermore.weboffice.databaseobject.FileSystemResources"));

			// Long ownId = groupId;// ((Number)
			// param.get("ownId")).longValue();//从对话框得出是同一个

			

			List<FileSystemActions> actionslist = new ArrayList<FileSystemActions>();
			ArrayList<Integer> actionsparam = (ArrayList<Integer>) param.get("actions");
			for (int i = 0; actionsparam != null && i < actionsparam.size(); i++) {
				FileSystemActions fsa = new FileSystemActions();
				fsa.setAction((actionsparam.get(i)).longValue());
				actionslist.add(fsa);
			}
			/*
			 * JSONTools.convert2List(actionslist, param.get("actions"),
			 * FileSystemActions.class);
			 */
			FileSystemActions[] actions = actionslist.toArray(new FileSystemActions[0]);

			Long[] addUserIds = JSONTools.convert2LongArray(param.get("addUserIds"));

			List<Long> delUserIds = new ArrayList<Long>();
			JSONTools.convert2List(delUserIds, param.get("delUserIds"),	Long.class);

			addOrUpdateFileSystemPermission(resource, ownId, ownType, actions,
					addUserIds, delUserIds);

			return JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 对文件资源增加或删除相应的用户、组织、用户组、角色等的权限
	 * 
	 * @param resource
	 *            文件系统资源。如果该资源是新设置权限，则从resource中获得的getId()为空，否则是对已经设置过
	 *            的资源在进行一定的权限，及用户修改。
	 * @param ownId
	 *            , 文件系统的拥有者Id。
	 * @param ownType
	 *            ， 文件系统拥有者类型。具体见com.evermore.weboffice.constants.both.
	 *            FileSystemCons中的定义类型。
	 * @param action
	 *            对文件系统资源设置的action，如果是新的action，则从中获得的getId（）为null，否则是对已经设置过
	 *            的action进行一定的修改。
	 * @param addUserIds
	 *            需要增加权限的用户id集合，无则为null。actions数组和addUserIds数组需要一一对应，即是一个actions
	 *            对应一个addUserIds值。没有这位null
	 * @param delUserIds
	 *            需要删除权限的用户id集合，无则为null。
	 */
	private static void addOrUpdateFileSystemPermission(
			FileSystemResources resource, Long ownId, int ownType,
			FileSystemActions[] actions, Long[] addUserIds,
			List<Long> delUserIds) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		service.addOrUpdateFileSystemPermission(resource, ownId, ownType,
				actions, addUserIds, delUserIds);
	}

	public static String getFileSystemAction(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			Long userId = ((Number) param.get("userId")).longValue();
			String path = null;
			String[] paths = null;
			Object obj = param.get("path");
			if (obj instanceof String) {
				path = (String) obj;
			} else if (obj instanceof ArrayList) {
				paths = ((ArrayList<String>) obj).toArray(new String[0]);
			}
			Boolean treeFlag = (Boolean) param.get("treeFlag");

			if (path != null) {
				Long action = getFileSystemAction(userId, path, treeFlag);

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, action);
			} else {
				List<Long> action = getFileSystemAction(userId, paths, treeFlag);

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, action);
			}
			return error;
		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 每步操作都需要判断 获得用户对某个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限， 直到获取到空间根为止。
	 * 
	 * @param userId
	 *            用户Id
	 * @param path
	 *            具体的文件资源路径
	 * @param treeFlag
	 *            是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	private static Long getFileSystemAction(Long userId, String path,
			boolean treeFlag) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		return service.getFileSystemAction(userId, path, treeFlag);
	}

	private static List<Long> getFileSystemAction(Long userId, String[] paths,
			boolean treeFlag) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		return service.getFileSystemAction(userId, paths, treeFlag);
	}

	// 参考FilesOpeHandler.hasPermission
	public static boolean canGroupOperationFileSystemAction(Long userId,
			String path, long action) {
		if (isGroup(path)) {
			Long permit = getFileSystemAction(userId, path, true);
			return FlagUtility.isValue(permit, action);// FileSystemCons.BROWSE_FLAG
		}
		return true;
	}
	
	public static boolean canCompanyOperationFileSystemAction(Long userId,
			String path, long action) {
			Long permit = getFileSystemAction(userId, path, true);
			return FlagUtility.isValue(permit, action);
	}

	public static List<Boolean> canGroupOperationFileSystemAction(Long userId,
			String[] paths, int action) {
		List<Boolean> lists = new ArrayList<Boolean>();
		if (paths == null) {
			lists.add(Boolean.TRUE);
			return lists;
		}
		if (paths != null) {
			if (!isGroup(paths[0])) {
				lists.add(Boolean.TRUE);
				return lists;
			}
		}
		List<Long> pers = getFileSystemAction(userId, paths, true);
		for (Long permit : pers) {
			lists.add(FlagUtility.isValue(permit, action));
		}
		return lists;
	}

	// 权限菜单的灰亮控制---企业空间和群组合用
	public static String canModifyGroupSpacePermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		String error = null;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String spaceUID = (String) param.get("spaceUID");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			// sp.getGroup().getId();
			if (isGroup(spaceUID)) {
				Long groupId = userService.getGroupIdBySpaceUID(spaceUID);
				Long spacepermission = getGroupSpacePermission(user.getId(),
						groupId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.PERMISSION_FLAG);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
						spacepermissionflag);
			} else // isTeam
			{
				Long teamId = userService.getTeamIdBySpaceUID(spaceUID);
				PermissionService service = (PermissionService) ApplicationContext
						.getInstance().getBean("permissionService");
				Long spacepermission = service.getTeamSpacePermission(
						user.getId(), teamId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.PERMISSION_FLAG);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
						spacepermissionflag);
			}
			return error;
		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static String getRoles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			Long groupId = ((Number) param.get("groupId")).longValue();
			Long orgId = ((Number) param.get("orgId")).longValue();
			Long teamId = ((Number) param.get("teamId")).longValue();

			List<Roles> lists = getRoles(groupId, orgId, teamId);

			return JSONTools.convertToJson(ErrorCons.NO_ERROR, lists);

		//}
			/* catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 获得的角色定义 如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * 
	 * @param groupId
	 *            ， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId
	 *            ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId
	 *            ， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * @return
	 */
	private static List<Roles> getRoles(long groupId, long orgId, long teamId) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		return userService.getRoles(groupId, orgId, teamId);
	}

	public static String addRole(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException {
		String error;
		//try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			JSONObject jo2 = JSONObject.fromObject(param.get("role"));
			Roles role = (Roles) JSONObject.toBean(jo2, Class.forName("com.evermore.weboffice.databaseobject.Roles"));

			Long groupId = ((Number) param.get("groupId")).longValue();

			if (groupId != -1) {
				Long spacepermission = getGroupSpacePermission(user.getId(),
						groupId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					//resp.setHeader("Cache-Control",						"no-store,no-cache,must-revalidate");
					//resp.getWriter().write(error);
					//return;
				}
			}

			Long orgId = ((Number) param.get("orgId")).longValue();
			Long teamId = ((Number) param.get("teamId")).longValue();

			// teamid的权限判断
			if (teamId != -1) {
				PermissionService service = (PermissionService) ApplicationContext
						.getInstance().getBean("permissionService");
				Long spacepermission = service.getTeamSpacePermission(
						user.getId(), teamId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,		"no permission");
					//resp.setHeader("Cache-Control",							"no-store,no-cache,must-revalidate");
					//resp.getWriter().write(error);
					//return;
				}
			}

			String ret = addRole(role, groupId, orgId, teamId);
			if ("角色重名了！".equalsIgnoreCase(ret)) {
				error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, ret);
			}else {
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
			}
			return error;
			/*
		} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 增加新的角色，如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * 
	 * @param role
	 *            角色内容
	 * @param groupId
	 *            ， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId
	 *            ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId
	 *            ， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * 
	 */
	private static String addRole(Roles role, long groupId, long orgId,
			long teamId) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		return userService.addRole(role, groupId, orgId, teamId);
	}

	public static String deleteRole(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			List<Long> roleId = new ArrayList<Long>();
			JSONTools.convert2List(roleId, param.get("roleId"), Long.class);
			if (roleId.size() != 0) {
				Roles role = userService.findRoleById(roleId.get(0));
				Long groupId = role.getGroup() == null ? null : role.getGroup()
						.getId();
				if (groupId != null && groupId.intValue() != -1) {
					Long spacepermission = getGroupSpacePermission(
							user.getId(), groupId.longValue());
					boolean spacepermissionflag = FlagUtility.isValue(
							spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
					if (!spacepermissionflag)// 没有权限
					{
						return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						/*resp.setHeader("Cache-Control",
								"no-store,no-cache,must-revalidate");
						resp.getWriter().write(error);
						return;*/
					}
				}
				// 判断team的权限
				Long teamId = role.getTeam() == null ? null : role.getTeam()
						.getId();
				if (teamId != null && teamId.intValue() != -1) {
					PermissionService service = (PermissionService) ApplicationContext
							.getInstance().getBean("permissionService");
					Long spacepermission = service.getTeamSpacePermission(
							user.getId(), teamId);
					boolean spacepermissionflag = FlagUtility.isValue(
							spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
					if (!spacepermissionflag)// 没有权限
					{
						return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						/*resp.setHeader("Cache-Control",
								"no-store,no-cache,must-revalidate");
						resp.getWriter().write(error);
						return;*/
					}
				}
				deleteRole(roleId);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");
		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 删除角色
	 * 
	 * @param roleId
	 */
	private static void deleteRole(List<Long> roleId) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		userService.deleteRole(roleId);
	}

	public static String modifyRole(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			Long id = ((Number)param.get("id")).longValue();
			Long roleId = ((Number)param.get("roleId")).longValue();
			String roleName = (String)param.get("roleName");
			String description = (String)param.get("description");
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Roles role = userService.findRoleById(roleId);
			role.setRoleName(roleName);
			role.setDescription(description);
			Long groupId = role.getGroup() == null ? null : role.getGroup()
					.getId();
			if (groupId != null && groupId.intValue() != -1) {
				Long spacepermission = getGroupSpacePermission(user.getId(),
						groupId.longValue());
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}

			// 判断team的权限
			Long teamId = role.getTeam() == null ? null : role.getTeam()
					.getId();
			if (teamId != null && teamId.intValue() != -1) {
				PermissionService service = (PermissionService) ApplicationContext
						.getInstance().getBean("permissionService");
				Long spacepermission = service.getTeamSpacePermission(
						user.getId(), teamId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}
			String ret = modifyRole(roleId, role);

			if(ret != null)
			{
				if("角色重名了！".equalsIgnoreCase(ret)){
					error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR,ret);
				}else {
					error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR,ret, ret);
				}
				
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
			}
			return error;

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 修改角色的属性
	 * 
	 * @param role
	 */
	private static String modifyRole(long roleId, Roles role) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		return userService.updateRole(roleId, role);
	}

	public static void findRoleById(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		String error;
		try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);

			Long id = ((Number) param.get("id")).longValue();

			Roles roles = findRoleById(id);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, roles);

		} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);
	}

	/**
	 * 根据角色id获得角色对象。
	 * 
	 * @param id
	 * @return
	 */
	private static Roles findRoleById(Long id) {
		UserService fss = (UserService) ApplicationContext.getInstance()
				.getBean(UserService.NAME);
		return fss.findRoleById(id);
	}

	public static String getRoleAction(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);

			Long roleId = ((Number) param.get("roleId")).longValue();

			List<Actions> list = getRoleAction(roleId);

			JsonConfig cfg = createActionsJsonConfig();

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, list, cfg);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	private static JsonConfig createActionsJsonConfig() {
		JsonConfig cfg = new JsonConfig();
		cfg.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (name.equals("id")) {
					return false;
				} else if (name.equals("action")) {
					return false;
				} else if (name.equals("actionName")) {
					return false;
				} else if (name.equals("actionType")) {
					return false;
				}
				return true;
			}
		});
		return cfg;
	}

	/**
	 * 返回角色设置的action
	 * 
	 * @param roleId
	 * @return
	 */
	private static List<Actions> getRoleAction(long roleId) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		return service.getRoleAction(roleId);
	}

	public static String addOrUpdateDefinedRoleAction(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);

			Long roleId = ((Number) param.get("roleId")).longValue();
			Roles role = findRoleById(roleId);
			Long groupId = role.getGroup() == null ? null : role.getGroup()
					.getId();
			if (groupId != null && groupId.intValue() != -1) {
				Long spacepermission = getGroupSpacePermission(user.getId(),
						groupId.longValue());
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}

			// 判断team的权限
			Long teamId = role.getTeam() == null ? null : role.getTeam()
					.getId();
			if (teamId != null && teamId.intValue() != -1) {
				PermissionService service = (PermissionService) ApplicationContext
						.getInstance().getBean("permissionService");
				Long spacepermission = service.getTeamSpacePermission(
						user.getId(), teamId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.CREATE_ROLE_FLAG);
				if (!spacepermissionflag)// 没有权限
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,			"no permission");
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}

			List<Actions> actions = convert2Actions(param.get("actions"));

			addOrUpdateDefinedRoleAction(roleId, actions);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, "ok");

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 更新或设置某个角色的预定义拥有的actions
	 * 
	 * @param roleId
	 * @param actions
	 */
	private static void addOrUpdateDefinedRoleAction(long roleId,
			List<Actions> actions) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		service.addOrUpdateDefinedRoleAction(roleId, actions);
	}

	public static String canCreateSpace(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		/*String error;
		try {*/
			/*
			 * HashMap<String, Object> param = (HashMap<String, Object>)
			 * jsonParams .get(ServletConst.PARAMS_KEY);
			 */
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String spaceUID = (String) param.get("spaceUID");
			PermissionService service = (PermissionService) ApplicationContext
					.getInstance().getBean("permissionService");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			
			boolean flag=false;
//			if (spaceUID.startsWith("team_"))//快速共享的权限显示
//			{
				long permission = getSpacePermission(user.getId());
				flag = FlagUtility.isLongFlag(permission,
						ManagementCons.CREATE_SPACE);
//			}
			flag = user.getCompany().getSpaceUID().equals(spaceUID)&&service.isCompanyAdmin(user.getId()) ? true : flag;
			if(spaceUID.startsWith("team_") && !flag){ //当是协作共享且无系统权限时再判断对此空间的权限
				Long teamId = userService.getTeamIdBySpaceUID(spaceUID);

				if (teamId != null && teamId.intValue() != -1) {
					Long spacepermission = service.getTeamSpacePermission(
							user.getId(), teamId);
					flag = FlagUtility.isValue(
							spacepermission, SpaceConstants.PERMISSION_FLAG);
				}
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, flag);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static String getManagementActionPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		//String error;
		//try {
			long permission = getSpacePermission(user.getId());
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, permission);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 判断某个用户是否有权限建立新空间。
	 * 
	 * @param userId
	 *            // boolean flag =
	 *            FlagUtility.isLongFlag(permission,ManagementCons
	 *            .CREATE_SPACE);
	 * @return
	 */
	private static long getSpacePermission(Long userId) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		return service.getSystemPermission(userId);
	}

	/**
	 * 判断能删除的空间
	 * 
	 * @param userId
	 * @return
	 */
	private static boolean canDelGroupSpacesByUserId(String spaceuid,
			Long userId) {
		FileSystemService fss = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		return fss.canDelGroupSpacesByUserId(spaceuid, userId);
	}

	public static String getGroupSpacePermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String groupId = (String) param.get("groupId");// 群组Id(如果是修改组是用到)
			long permission = getGroupSpacePermission(user.getId(),
					Long.parseLong(groupId));
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, permission);

/*		} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 获得用户对某个空间的权限
	 * 
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public static long getGroupSpacePermission(Long userId, Long groupId) {
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		return service.getGroupSpacePermission(userId, groupId);
	}

	private static List<Actions> convert2Actions(Object json) {
		List<Actions> list = new ArrayList<Actions>();
		if (json instanceof ArrayList) {
			ArrayList list0 = (ArrayList) json;
			for (int i = 0; i < list0.size(); i++) {

				JSONObject jsonObject = new JSONObject();
				HashMap<Object, Object> map = (HashMap<Object, Object>) list0
						.get(i);
				jsonObject.putAll(map);
				// JSONObject jsonObject = (JSONObject) list0.get(i);
				Integer temp = ((Integer)jsonObject.get("actionType"));
				if (temp != null)
				{
					if (PermissionConst.FILE_ACTION.intValue() == temp.intValue()) 
					{
						list.add((Actions) JSONObject.toBean(jsonObject,FileSystemActions.class));
					}
					else if (PermissionConst.SPACE_ACTION.intValue() == temp.intValue()) 
					{
						list.add((Actions) JSONObject.toBean(jsonObject,	SpacesActions.class));
					}
					/*else if (PermissionConst.FILE_ACTION.equals(jsonObject.get("actionType")))
					{
						list.add((Actions) JSONObject.toBean(jsonObject,FileSystemActions.class));
					}
					else if (PermissionConst.FILE_ACTION.equals(jsonObject.get("actionType"))) 
					{
						list.add((Actions) JSONObject.toBean(jsonObject,FileSystemActions.class));
					}*/
				}
			}
		}
		return list;
	}

	/**
	 * 判断是否处理的group，主要是区别customTeam
	 * 
	 * @param spaceUID
	 * @return
	 */
	private static boolean isGroup(String spaceUID) {
		if(spaceUID.startsWith("group"))
		{
			return true;
		}
		return spaceUID == null ? false : spaceUID.startsWith("company");
	}
	
	private static boolean isTeam(String spaceUID) {
		return spaceUID == null ? false : spaceUID.startsWith("team");
	}

	private static boolean isGroup(List<String> spaceUIDlist) {
		return spaceUIDlist == null && spaceUIDlist.size() > 0 ? false
				: spaceUIDlist.get(0).startsWith("group");
	}

	/**
	 * 少一个排序的问题????
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String searchFile(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException {
		//String error;
		//try {
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			String path = (String) param.get("path");
			String spaceUID = (String) param.get("spaceUID");
			int sindex = path.indexOf("/");
			if (spaceUID == null || spaceUID.length() == 0 && sindex > 0)
			{
//				path = path.substring(sindex+1);
				spaceUID = path.substring(0,path.indexOf("/"));
				
//				spaceUID = path.substring(0, sindex);
			}
			
//			path = path.substring(sindex+1);
			
			path = spaceUID + "/" + FileConstants.DOC;
			
			String contents = (String) param.get("keyword");
			Integer index = 0;//(Integer) param.get("index");
			Integer start = (Integer) param.get("start");
			Integer limit = (Integer) param.get("count");
			String sort = (String) param.get("sort");
			String order = (String) param.get("order");
			
			DataHolder dh = jcrService.searchFile(path, index, spaceUID, contents, start, limit,user.getId(),sort,order);
			List list = dh.getFilesData();

			ArrayList json = new ArrayList();
			HashMap<String, Object> files = new HashMap<String, Object>();
			HashMap<String, Object> retJson = new HashMap<String, Object>();
			int fileListSize = dh.getIntData();
			if (list != null && list.size() > 0) {
				Fileinfo file;
				for (Object file1 : list) {
					file = (Fileinfo) file1;
					files = new HashMap<String, Object>();
					files.put("name", file.getFileName());
					files.put("folder", file.isFold());
					files.put("path", file.getPathInfo());
					files.put("isShared", file.isShared());
					files.put("displayPath", file.getShowPath());
					files.put(
							"size",
							file.isFold() ? ""
									: FilesOpeHandler.fileSizeOperation(file
											.getFileSize() == null ? 0 : file
											.getFileSize()));
					try {
						files.put("modifyTime", DateUtils.ftmDateToString(
								"yyyy-MM-dd HH:mm:ss", (file.getLastedTime()==null?file.getCreateTime():file.getLastedTime())));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					json.add(files);

				}
			
			}
			retJson.put("fileListSize", fileListSize);
			retJson.put("currentFolderPath", path);
			retJson.put("fileList", json);
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, retJson);

		/*} catch (Exception e) {
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public final static void main(String[] args) {
		Groups gr = new Groups();
		gr.setId(Long.valueOf(3000000000000000000l));
		gr.setName("sss");

		Groups gr2 = new Groups();
		gr2.setId(Long.valueOf(4));
		gr2.setName("sssss22222");
		gr2.setParent(gr);

		JSONObject jo = JSONObject.fromObject(gr2);
		String str = jo.toString();

		// {"description":"","id":4,"image":"image.jpg","image1":"","manager":null,"name":"sssss22222","parent":{"description":"","id":3,"image":"image.jpg","image1":"","manager":null,"name":"sss","parent":null,"parentKey":"","spaceUID":""},"parentKey":"","spaceUID":""}
		JSONObject jo2 = JSONObject.fromObject(str);

		Object obj11 = JSONTools.convertParams(
				"{\"Groups.class\":" + str + "}", true);
		Object obj = null;
		try {
			obj = JSONObject.toBean(jo2, Class
					.forName("com.evermore.weboffice.databaseobject.Groups"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(str);

		//
		Long[] boolArray = new Long[] { 1L, 2L, 3L };
		JSONArray jsonArray = JSONArray.fromObject(boolArray);
		// jo = JSONObject.fromObject(boolArray);
		str = jsonArray.toString();
		System.out.println(str);
		// [1,2,3]
		jsonArray = JSONArray.fromObject(str);
		// obj = JSONObject.toBean(jo2,Long[].class);
		// obj = JSONArray.toArray(jsonArray,Long[].class);

		// obj = jsonArray.toArray(new Long[0]);
		// obj = JSONArray.toArray(jsonArray,Long.class);
		List<Long> list = new ArrayList<Long>();
//		JSONTools.convert2List(list, str, Long.class);
		System.out.println(list);

	}
}
