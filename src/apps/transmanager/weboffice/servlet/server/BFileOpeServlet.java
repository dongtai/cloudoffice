package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.BFilesOpeHandler;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.JSONTools;

/**
 * 企业空间
 * 
 * @author zy
 * 
 */
public class BFileOpeServlet extends AbstractServlet {

	public static final String ADD_OR_UPDATEGROUP_ACTION = "addOrUpdateGroup"; // 创建更新协作项目

	public static final String GET_GROUP_SPACES_BY_USERID_ACTION = "getGroupSpacesByUserId";// 获取协作项目

	public static final String DELETE_GROUP_SPACES_ACTION = "deleteGroupSpaces";// 删除;//企业空间和群组合用

	public static final String GET_USERS_BY_GROUPID_ACTION = "getUsersByGroupId";// 获取项目成员

	public static final String ADD_OR_UPDATE_GROUP_MEMBERS_ACTION = "addOrUpdateGroupMembers";// 成员添加//成员删除

	public static final String GET_ROLES = "getRoles";// 空间内获得角色//企业空间和群组合用

	public static final String ADD_ROLE = "addRole";// 空间内增加角色//企业空间和群组合用

	public static final String DELETE_ROLE = "deleteRole";// 空间内删除角色//企业空间和群组合用

	public static final String MODIFY_ROLE = "modifyRole";// 空间内修改角色//企业空间和群组合用

	public static final String GET_ROLE_ACTION = "getRoleAction";// 空间内角色权限获取//企业空间和群组合用

	public static final String ADD_OR_UPDATE_DEFINEDROLE_ACTION = "addOrUpdateDefinedRoleAction";// 空间内更新角色//企业空间和群组合用

	public static final String GET_ALLFILE_SYSTEM_PERMISSION_ACTION = "getAllFileSystemPermission";// 获取某个资源的权限情况//企业空间和群组合用

	public static final String ADD_OR_UPDATE_FILESYSTEM_PERMISSION = "addOrUpdateFileSystemPermission";// 更新某个资源的权限//企业空间和群组合用

	public static final String CAN_CREATE_SPACE = "canCreateSpace";// 判断能否创建企业空间
																	// //菜单亮
	public static final String CAN_MODIFY_GROUP_SPACE_PERMISSION = "canModifyGroupSpacePermission";// 空间菜单里面权限菜单亮？//企业空间和群组合用

	/**
	 * 用户系统操作权限 。新建空间 等
	 * FlagUtility.isLongFlag(permission,ManagementCons.CREATE_SPACE)
	 */
	public static final String GET_MANAGEMENT_ACTION_PERMISSION = "getManagementActionPermission";
	/**
	 * 空间权限 "某空间分配权限", "某空间增删成员", "某空间增删角色", "某空间发布公告", "某空间回收站??", "某空间删除空间"
	 * FlagUtility.isValue(spacepermission, SpaceConstants.PUBLIC_BULLETIN_FLAG)
	 */
	public static final String GET_GROUP_SPACEPERMISSION = "getGroupSpacePermission";// 获得用户对某个空间的权限//企业文库的相对应的菜单灰亮

	/**
	 * 文件操作权限 "浏览文件列表", "读", "写", "另存", "新建","下载", "打印", "重命名", "删除", "上传"
	 * "复制粘贴", "离线编辑", "版本", "锁定/解锁", "发送", "审批","移动"
	 * FlagUtility.isValue(permit, FileSystemCons.BROWSE_FLAG);
	 */
	public static final String GET_FILESYSTEM_ACTION = "getFileSystemAction";// 用户每步操作时用到
	
	
	public static final String SEARCH_FIlE = "searchFile";//

	@Override
	protected String handleService(HttpServletRequest request,HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws Exception, IOException 
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String method = (String) jsonParams.get(ServletConst.METHOD_KEY); //
		String account = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		Users user = (Users) request.getSession().getAttribute("userKey");
		Users userFromMobile = userService.getUser(account); // 非web端发来的请求。错误做法，将删除
		if (user == null)
		{
			user = userFromMobile;
		}
		if (user != null) // 还需要进行登录认证
		{
			if (method == null || method.length() <= 0) // json请求方法参数不可以为null
			{
				return JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			}
			if (method.equals(ADD_OR_UPDATEGROUP_ACTION)) // 创建更新协作项目
			{
				return BFilesOpeHandler.addOrUpdateGroup(request, response,	jsonParams, user);
			}
			if (method.equals(GET_GROUP_SPACES_BY_USERID_ACTION)) // 获取协作项目
			{
				return BFilesOpeHandler.getGroupSpacesByUserId(request, response, jsonParams, user);
			}
			if (method.equals(DELETE_GROUP_SPACES_ACTION)) // 删除
			{
				return BFilesOpeHandler.deleteGroupSpaces(request, response, jsonParams, user);
			}
			if (method.equals(GET_USERS_BY_GROUPID_ACTION)) // 获取项目成员
			{
				return BFilesOpeHandler.getUsersByGroupId(request, response, jsonParams, user);
			}
			if (method.equals(ADD_OR_UPDATE_GROUP_MEMBERS_ACTION)) // 成员添加//成员删除
			{
				return BFilesOpeHandler.addOrUpdateGroupMembers(request, response,	jsonParams, user);
			}

			if (method.equals(GET_ROLES)) {
				return BFilesOpeHandler.getRoles(request, response, jsonParams, user);
			}
			if (method.equals(ADD_ROLE)) {
				return BFilesOpeHandler.addRole(request, response, jsonParams, user);
			}
			if (method.equals(DELETE_ROLE)) {
				return 	BFilesOpeHandler.deleteRole(request, response, jsonParams, user);
			}
			if (method.equals(MODIFY_ROLE)) {
				return BFilesOpeHandler.modifyRole(request, response, jsonParams, user);
			}
			if (method.equals(GET_ROLE_ACTION)) {
				return BFilesOpeHandler.getRoleAction(request, response, jsonParams, user);
			}
			if (method.equals(ADD_OR_UPDATE_DEFINEDROLE_ACTION)) {
				return BFilesOpeHandler.addOrUpdateDefinedRoleAction(request,response, jsonParams, user);
			}
			if (method.equals(CAN_CREATE_SPACE)) {
				return BFilesOpeHandler.canCreateSpace(request, response, jsonParams, user);
			}
			if (method.equals(GET_MANAGEMENT_ACTION_PERMISSION)) {
				return BFilesOpeHandler.getManagementActionPermission(request,	response, jsonParams, user);
			}
			if (method.equals(GET_GROUP_SPACEPERMISSION)) {
				return BFilesOpeHandler.getGroupSpacePermission(request, response,	jsonParams, user);
			}
			if (method.equals(GET_FILESYSTEM_ACTION)) // 获取某个资源的权限情况
			{
				return BFilesOpeHandler.getFileSystemAction(request, response,	jsonParams, user);
			}
			if (method.equals(GET_ALLFILE_SYSTEM_PERMISSION_ACTION)) // 获取某个资源的权限情况
			{
				return BFilesOpeHandler.getAllFileSystemPermission(request, response,jsonParams, user);
			}
			if (method.equals(ADD_OR_UPDATE_FILESYSTEM_PERMISSION)) // 获取某个资源的权限情况
			{
				return BFilesOpeHandler.addOrUpdateFileSystemPermission(request, response, jsonParams, user);
			}
			if (method.equals(CAN_MODIFY_GROUP_SPACE_PERMISSION)) // 获取某个资源的权限情况
			{
				return BFilesOpeHandler.canModifyGroupSpacePermission(request,	response, jsonParams, user);
			}
			if (method.equals(SEARCH_FIlE)) // 获取某个资源的权限情况
			{
				return BFilesOpeHandler.searchFile(request,	response, jsonParams, user);
			}

		}
			// 无效请求方法
			return  JSONTools.convertToJson(ErrorCons.SYSTEM_REQUEST_ERROR, method);

	}

}
