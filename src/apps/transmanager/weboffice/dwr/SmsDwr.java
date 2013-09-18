package apps.transmanager.weboffice.dwr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.LocaleConstant;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.IUserService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;
import apps.transmanager.weboffice.util.beans.SQLUtil;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.WebTools;

public class SmsDwr {
	private IAddressListService addressListService;
	private PermissionService permissionService;

	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setAddressListService(IAddressListService addressListService) {
		this.addressListService = addressListService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	private Users getCurrentUser(HttpServletRequest req) {
		return (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
	}
//	/**
//	 * 获得当前用户信息，同时将当前用户加入在线列表
//	 * 
//	 * @return 用户信息
//	 */
//	public Users getUserinfo(HttpServletRequest req) {
//		Users userInfo = getCurrentUser(req);// 当前登录的用户编号
//		// 需要将信息插入登录信息表里
//		OnlinePo onlinePo = new OnlinePo();
//		onlinePo.setUserId(userInfo.getId());
//		onlinePo.setName(userInfo.getUserName());
//		String ip=WebTools.getRealIpAddr(req);
//		onlinePo.setIp(ip);
//		onlinePo.setLoginTime(new Date());
//		//addressListService.addOnline(onlinePo);
//		req.getSession().setAttribute(Constant.LG_SESSION_ONLINE, onlinePo);
//		return userInfo;
//	}

	/**
	 * 删除在线用户
	 * @deprecated
	 * @param req
	 *            请求信息
	 */
//	public void delOnline(HttpServletRequest req) {
//		Users userInfo = getCurrentUser(req);// 当前登录的用户编号
//		addressListService.delOnline(userInfo.getId());
//		String ssId = (String) req.getSession().getAttribute(
//				"DWR_ScriptSession_Id");
//		DwrScriptSessionManagerUtil.invalidate(ssId);
//	}

	/**
	 * 获取当前用户的所有自定义组或者成员,转化为树的节点,与即时通讯保持一致
	 * 
	 * @param req
	 *            请求信息
	 * @return 自定义
	 */
	public List<Map<String, Object>> getCtmGOrMNode(String pId,
			HttpServletRequest req) {
		Users userInfo = getCurrentUser(req);
		return this.addressListService.getCtmGOrMNode(userInfo.getId(), pId,true,false);
	}

	/**
	 * 获取项目组成员
	 * 
	 * @deprecated
	 * @param req
	 *            请求信息，用于确定用户
	 * @return 项目组成员树
	 */
//	@SuppressWarnings("unchecked")
//	public List<Map<String, Object>> getTeamProGOrMember(String pId,
//			HttpServletRequest req) {
//		List<Map<String, Object>> teamGOrMNodeMap = new ArrayList<Map<String, Object>>();
//		Users userInfo = (Users) req.getSession().getAttribute(
//				Constant.LG_SESSION_USER);
//		if ("0".equals(pId.split("-")[1])) {
//			List<Groups> teamGList = addressListService
//					.getTeamGByUserId(userInfo.getId());
//			if (teamGList != null && !teamGList.isEmpty())
//				teamGOrMNodeMap = TreeUtil.convertTeamGList(teamGList, "teamg");
//		} else {
//			Long gId = Long.parseLong(pId.split("-")[1]);
//			Map<String, Object> teamGMMap = addressListService
//					.getTeamGMByGroupId(gId);
//			List<Users> teamGMList = (List<Users>) teamGMMap.get("teamGMList");
//			teamGMList = WebTools.sortUsers(teamGMList, "realName", true);
//			List<Long> onlineIdList = (List<Long>) teamGMMap
//					.get(Constant.LG_ONLINE_ID_LIST);
//			teamGOrMNodeMap = TreeUtil.convertTeamGMList(teamGMList,
//					onlineIdList, "teamgm", gId);
//		}
//		return teamGOrMNodeMap;
//	}

	/**
	 * 获得组里面的成员集合，并转换成树节点
	 * 
	 * @param gId
	 *            组ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSelRelationOfCtmG(String gIdS) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		Long gId = Long.parseLong(gIdS.split("-")[1]);
		Map<String, Object> ctmGMMap = addressListService
				.getCtmGMByGroupId(gId);
		if (ctmGMMap != null) {
			List<CtmGroupMemberPo> ctmGMList = (List<CtmGroupMemberPo>) ctmGMMap
					.get("ctmGMList");
			List<Long> onlineIdList = (List<Long>) ctmGMMap
					.get(PageConstant.LG_ONLINE_ID_LIST);
			nodeList = TreeUtil.convertCtmGMList(ctmGMList, onlineIdList, "gm");
		}
		return nodeList;
	}

	/**
	 * 添加用户自定义组
	 * 
	 * @param ctmGName
	 *            组名
	 * @param idList
	 *            组员ID集合
	 * @param req
	 *            请求信息
	 * @return 添加信息
	 */
	public int addCtmG(String ctmGName, List<Users> userList,
			HttpServletRequest req) {
		CustomGroupPo ctmG = new CustomGroupPo();
		Users userInfo = getCurrentUser(req);
		ctmG.setUserId(userInfo.getId());
		ctmG.setName(ctmGName);
		ctmG.setCreateTime(new Date());
		int result = addressListService.getCtmGroupService().addCtmG(ctmG, userList);
		return result;
	}

	/**
	 * 编辑用户自定义组的信息
	 * 
	 * @param gIds
	 *            用户自定义组的ID信息(需要对ID进行处理)
	 * @param ctmGName
	 *            用户自定义组名
	 * @param userList
	 *            用户自定义组成员集合
	 * @return 编辑信息
	 */
	public int editCtmG(String gIds, String ctmGName, List<Users> userList,
			HttpServletRequest req) {
		Long gId = Long.parseLong(gIds.split("-")[1]);
		CustomGroupPo ctmGroup = new CustomGroupPo();
		Users userInfo = getCurrentUser(req);
		ctmGroup.setId(gId);
		ctmGroup.setName(ctmGName);
		ctmGroup.setUserId(userInfo.getId());
		int result = addressListService.getCtmGroupService().editCtmG(ctmGroup, userList);
		return result;
	}

	/**
	 * 添加人员列表到指定组
	 * 
	 * @param gIds
	 *            组Id
	 * @param ctmGName
	 *            组名字
	 * @param userList
	 *            用户列表
	 * @param req
	 *            请求信息
	 * @return 添加结果信息
	 */

	public int addAddrsToCtmG(String gIds, String ctmGName,
			List<AddressBean> beanList, HttpServletRequest req) {
		int exitCount = addressListService.getCtmGroupService().addCtmGMListWithBean(Long.parseLong(gIds), beanList);
		return exitCount;
	}

	/**
	 * 添加人员列表到指定组
	 * 
	 * @param gIds
	 *            组Id
	 * @param ctmGName
	 *            组名字
	 * @param userIds
	 *            用户ID
	 * @param req
	 *            请求信息
	 * @return 添加结果信息
	 */

	public int addSingleAddrToCtmG(String gIds, String ctmGName,
			String userIds, HttpServletRequest req) {
		Long gId = Long.parseLong(gIds);
		String idFix = userIds.split("-")[0];
		Long userId = 0L;
		if (idFix.equalsIgnoreCase("search")) {
			userId = Long.parseLong(userIds.split("-")[1]);
		} else if (idFix.equalsIgnoreCase("gm")
				|| idFix.equalsIgnoreCase("teamgm")) {
			userId = Long.parseLong(userIds.split("-")[2]);
		} else {
			return -1;
		}
		List<AddressBean> beanList = new ArrayList<AddressBean>();
		Users user = userService.getUserById(userId);
		AddressBean temp = new AddressBean();
		temp.setId("inner-" + userId);
		temp.setImage(WebConfig.userPortrait + user.getImage1());
		temp.setRealName(user.getRealName());
		beanList.add(0, temp);
		int exitCount = addressListService.getCtmGroupService().addCtmGMListWithBean(gId, beanList);
		return exitCount;
	}

	/**
	 * 删除一个用户的自定义组
	 * 
	 * @param id
	 *            用户自定义组的ID
	 * @return 删除信息
	 */
	public boolean delCtmG(Long gId, HttpServletRequest req) {
		Users userInfo = getCurrentUser(req);
		try {
			addressListService.getCtmGroupService().delCtmG(userInfo.getId(), gId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除一个用户，从树节点删除，与列表删除时ID标识不同
	 * 
	 * @param idString
	 *            树节点ID保存的信息值：ctmgm-1-2,outer-1-2, 1标识在哪个组, 2标识用户id, 或外部联系人真实ID。
	 * @return 删除信息
	 */
	public boolean delCtmGM(String idString, HttpServletRequest req) {
		String idFix = idString.split("-")[0];
		if (idFix.equalsIgnoreCase("ctmgm") || idFix.equalsIgnoreCase("outer")) {
			try {
				Users user = getCurrentUser(req);
				addressListService.getCtmGroupService().delCtmGM(user.getId(), idString);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 删除选择到的用户，从列表选择删除，与树节点删除有区别
	 * 
	 * @param userIdList
	 *            用户自定义组下联系人ID列表, 外部+内部
	 *            userIdList格式: inner-1-2, outer-1-2, 标识-id-分组id
	 * @return 删除信息
	 */
	public boolean delCtmGMS(List<String> userIdList, HttpServletRequest req) {
		try {
			Users user = getCurrentUser(req);
			addressListService.getCtmGroupService().delCtmGMInAndOut(user.getId(), userIdList);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获得搜索联系人的结果，结果为Map集合，group为组ID链，user为用户ID
	 * 
	 * @param key
	 *            搜索关键字
	 * @param req
	 *            请求信息
	 * @return 搜索联系人结果
	 */
	public List<String[]> searchRel(String key, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		List<String[]> userSearchList = addressListService.searchRel(user, SQLUtil.stringEscape(key));
		return userSearchList;
	}

	/**
	 * 获得联系人分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getOrgsAndMember(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Users user = getCurrentUser(req);
		int usertype = 0;
		if (user.getCompany().getId().longValue() == 1l) {
			usertype = 1;
		}
		parentID = parentID.split("-")[1];
		Map<String, Object> groupAndMemberMap = addressListService
				.getGroupAndMemberList(Long.parseLong(parentID), user
						.getCompany().getId(), "groupcode", null, usertype);
		List<Organizations> groupList = (List<Organizations>) groupAndMemberMap
				.get("groupList");
		List<DepMemberPo> groupMemberList = (List<DepMemberPo>) groupAndMemberMap
				.get("groupMemberList");
		List<Long> onlineIdList = (List<Long>) groupAndMemberMap
				.get(PageConstant.LG_ONLINE_ID_LIST);
		if (groupList != null && !groupList.isEmpty()) {
			List<Map<String, Object>> nodeMapTemp = TreeUtil.convertOrgList(
					groupList, "g");
			nodeMap.addAll(nodeMapTemp);
		}
		if (groupMemberList != null && !groupMemberList.isEmpty()) {
			groupMemberList = WebTools.sortUsers(groupMemberList, "User",
					"realName", true);
			List<Map<String, Object>> nodeMapTemp = TreeUtil
					.convertOrgMemberList(groupMemberList, onlineIdList, "gm",
							exceptSelf, user.getId());
			nodeMap.addAll(nodeMapTemp);
		}
		return nodeMap;
	}

	/**
	 * 获得联系人列表
	 * 
	 * @param id
	 *            组ID
	 * @param req
	 *            请求信息
	 * @return 联系人列表
	 */
	public Map<String, Object> getAddrListPage(int start, int limit, Long id,
			String type, String sort, String order, HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		Page page = new Page();
		page.setCurrentRecord(start);
		page.setPageSize(limit);
		page.setCurrentPage(start, limit);
		List<Long> userIdList = new ArrayList<Long>();
		List<AddressListPo> addressList = new ArrayList<AddressListPo>();
		Map<String, Object> listMap = new HashMap<String, Object>();
		Users myUser = getCurrentUser(req);
		int usertype = 0;
		if (myUser.getCompany().getId().longValue() == 1) {
			usertype = 1;
		}
		if (id != 0) {
			listMap = addressListService.getAddrListPage(id, myUser
					.getCompany().getId(), type, sort, order, page, usertype);
			userIdList = (List<Long>) listMap.get("userIdList");
			addressList = (List<AddressListPo>) listMap.get("addressList");
		} else {
			listMap = addressListService.getAddrListPage(myUser.getId(), myUser
					.getCompany().getId(), type, sort, order, page, usertype);
			userIdList = (List<Long>) listMap.get("userIdList");
			addressList = (List<AddressListPo>) listMap.get("addressList");
		}

		List<AddressBean> addBeanList = new ArrayList<AddressBean>();
		if (type.equalsIgnoreCase("ctmg") || type.equals("ctmgr")) {
			// 只有从自定义组获取人时才获取外部联系人列表
			for (AddressListPo addressPo : addressList) {
				AddressBean addressBean = new AddressBean("outer-"
						+ addressPo.getId(), addressPo.getOwnerId(), // ownerId,
						addressPo.getUserinfo(), // userinfo,
						addressPo.getGroupId(), // groupId,
						addressPo.getUserName(), // userName,
						addressPo.getRealEmail(), // realEmail,
						addressPo.getRealName(), // realName,
						addressPo.getDuty(), // duty,
						WebConfig.userPortrait + addressPo.getImage(), // image,
						addressPo.getMobile(), // mobile,
						addressPo.getCompanyName(), // companyName,
						addressPo.getFax(), // fax,
						addressPo.getPhone(), // phone,
						addressPo.getPostcode(), // postcode,
						addressPo.getAddress(), // address,
						addressPo.getCompanyAddress(), // companyAddress,
						addressPo.getMan(), // man,
						addressPo.getBirthday(), // birthday,
						addressPo.getDepartment(), // department,
						addressPo.getComment() // comment
				);
				addBeanList.add(addressBean);
			}
		}
		{ // 无论从哪个入口获取联系人，都要获取系统内部联系人
			for (Long userId : userIdList) {
				Users user = userService.getUserById(userId);
				if (user == null) {
					continue;
				}
				AddressBean addressBean = new AddressBean("inner-" + userId,
						0L, // ownerId,
						user, // userinfo,
						id, // groupId, 当前取出的分组的ID
						user.getUserName(), // userName,
						user.getRealEmail(), // realEmail,
						user.getRealName(), // realName,
						user.getDuty(), // duty,
						WebConfig.userPortrait + user.getImage1(), // image,
						user.getMobile(), // mobile,
						user.getCompanyName(), // companyName,
						user.getFax(), // fax,
						user.getPhone(), // phone,
						user.getPostcode(), // postcode,
						user.getAddress(), // address,
						user.getCompanyAddress(), // companyAddress,
						user.getMan(), // man,
						user.getBirthday(), // birthday,
						"", // department,
						user.getDescription() // comment
				);
				addBeanList.add(addressBean);
			}
		}
		addBeanList = WebTools.sortUsers(addBeanList, "realName", true);
		if (type.equalsIgnoreCase("ctmg") || type.equals("ctmgr")) {
			map.put("totalRecords", addBeanList.size());
			List<AddressBean> returnList = new ArrayList<AddressBean>();
			int end = page.getCurrentRecord(); // 分页结束索引值
			if (page.getCurrentRecord() + page.getPageSize() > addBeanList
					.size()) {// 如果请求的数据操作了长度
				end = start + (addBeanList.size() - page.getCurrentRecord()); // 开始索引
																				// +
																				// 当前记录与总记录的差值
			} else {
				end = end + limit;
			}
			for (int i = start; i < end; i++) { // 从第start条到第start+limit条数据。塞进去限制的条目数，此处用程序来分页，不推荐
				returnList.add(addBeanList.get(i));
			}
			map.put("data", returnList);
		} else {
			map.put("totalRecords", page.getTotalRecord());
			map.put("data", addBeanList);
		}
		return map;
	}

	/**
	 * 获取当前联系人的所有自定义分组
	 * 
	 * @param req
	 *            请求信息
	 */
	public Map<String, Object> getAllCtmGroup(HttpServletRequest req) {
		return addressListService.getCustomGroupMap(getCurrentUser(req).getId());
	}

	public Map<String, Object> getUserInfo(int start, int limit, Long idLong,
			String key, String sort, String order, HttpServletRequest req) {
		List<AddressBean> addrBeanList = new ArrayList<AddressBean>();
		AddressBean addressBean = null;
		String idFix = key.split("-")[0];
		if (idFix.equals("outer") || idFix.equals("csearch")) { // 外部联系人：outer-id，分割成两部分
			Long id = Long.parseLong(key.split("-")[2]);
			AddressListPo addPo = addressListService.getAddressById(id);
			String imageString = addPo.getImage();
			if (imageString == null || imageString.equals("")
					|| imageString.equals("null")) {
				imageString = "image.jpg";
			}
			imageString = WebConfig.userPortrait + imageString;
			addressBean = new AddressBean(key, // id,
					addPo.getOwnerId(), // ownerId,
					addPo.getUserinfo(), // userinfo,
					addPo.getGroupId(), // groupId,
					addPo.getUserName() + "("
							+ LocaleConstant.instance.getValue("outContacts")
							+ ")", // userName,
					addPo.getRealEmail(), // realEmail,
					addPo.getRealName(), // realName,
					addPo.getDuty(), // duty,
					imageString, // image,
					addPo.getMobile(), // mobile,
					addPo.getCompanyName(), // companyName,
					addPo.getFax(), // fax,
					addPo.getPhone(), // phone,
					addPo.getPostcode(), // postcode,
					addPo.getAddress(), // address,
					addPo.getCompanyAddress(), // companyAddress,
					addPo.getMan(), // man,
					addPo.getBirthday(), // birthday,
					addPo.getDepartment(), // department,
					addPo.getComment() // comment
			);
		} else if (idFix.equals("search") || idFix.equals("ctmgm")
				|| idFix.equals("gm") || idFix.equals("teamgm")) { // 搜索结果或者自定义组联系人：search-id，分割成两部分
			Long id = 0L;
			if (idFix.equals("search")) {
				id = Long.parseLong(key.split("-")[1]);
			} else if (idFix.equals("gm") || idFix.equals("teamgm")
					|| idFix.equals("ctmgm")) {
				id = Long.parseLong(key.split("-")[2]);
			}
			Users user = userService.getUserById(id);
			String imageString = user.getImage1();
			if (imageString == null || imageString.equals("")
					|| imageString.equals("null")) {
				imageString = "image.jpg";
			}
			imageString = WebConfig.userPortrait + imageString;
			addressBean = new AddressBean(key, // id,
					0L, // ownerId,
					user, // userinfo,
					0L, // groupId,
					user.getUserName(), // userName,
					user.getRealEmail(), // realEmail,
					user.getRealName(), // realName,
					user.getDuty(), // duty,
					imageString, // user.getImage1(), //image, 要判断一下
					user.getMobile(), // mobile,
					user.getCompanyName(), // companyName,
					user.getFax(), // fax,
					user.getPhone(), // phone,
					user.getPostcode(), // postcode,
					user.getAddress(), // address,
					user.getCompanyAddress(), // companyAddress,
					user.getMan(), // man,
					user.getBirthday(), // birthday,
					"", // department,
					user.getDescription() // comment
			);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		addrBeanList.add(addressBean);
		map.put("data", addrBeanList);
		return map;
	}

	/**
	 * 获得有审批权限的联系人分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRoleOrgsAndMember(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Users user = getCurrentUser(req);
		parentID = parentID.split("-")[1];
		Map<String, Object> groupAndMemberMap = addressListService
				.getGroupAndMemberList(Long.parseLong(parentID), user
						.getCompany().getId(), "groupcode", null);
		List<Organizations> groupList = (List<Organizations>) groupAndMemberMap
				.get("groupList");
		List<DepMemberPo> groupMemberList = (List<DepMemberPo>) groupAndMemberMap
				.get("groupMemberList");
		if (null != groupMemberList && !groupMemberList.isEmpty()) {
			groupMemberList = filterLeader(groupMemberList);
		}
		List<Long> onlineIdList = (List<Long>) groupAndMemberMap
				.get(PageConstant.LG_ONLINE_ID_LIST);
		if (groupList != null && !groupList.isEmpty()) {
			List<Map<String, Object>> nodeMapTemp = TreeUtil.convertOrgList(
					groupList, "g");
			nodeMap.addAll(nodeMapTemp);
		}
		if (groupMemberList != null && !groupMemberList.isEmpty()) {
			groupMemberList = WebTools.sortUsers(groupMemberList, "User",
					"realName", true);
			List<Map<String, Object>> nodeMapTemp = TreeUtil
					.convertOrgMemberList(groupMemberList, onlineIdList, "gm",
							exceptSelf, user.getId());
			nodeMap.addAll(nodeMapTemp);
		}

		return nodeMap;
	}

	/**
	 * 获得有审批权限的联系人,不获取部门分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRoleMembers(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);
			Map<String, Object> allMembersMap = addressListService
					.getGroupAndMemberList(-1L, user.getCompany().getId(),
							"groupcode", null);
			List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap
					.get("groupMemberList");
			if (null != groupMemberList && !groupMemberList.isEmpty()) {
				groupMemberList = filterLeader(groupMemberList);
			}
			List<Long> onlineIdList = (List<Long>) allMembersMap
					.get(PageConstant.LG_ONLINE_ID_LIST);
			if (groupMemberList != null && !groupMemberList.isEmpty()) {
				groupMemberList = WebTools.sortUsers(groupMemberList, "User",
						"realName", true);
				List<Map<String, Object>> nodeMapTemp = TreeUtil
						.convertOrgMemberListWithOrg(groupMemberList,
								onlineIdList, "gm", exceptSelf, user.getId());
				nodeMap.addAll(nodeMapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeMap;
	}

	public List<Map<String, Object>> getPowerMembersA(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 1, false, req);
	}

	public List<Map<String, Object>> getPowerMembersB(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 2, false, req);
	}

	public List<Map<String, Object>> getPowerMembersC(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 3, false, req);
	}

	public List<Map<String, Object>> getPowerReadersA(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 1, true, req);
	}

	public List<Map<String, Object>> getPowerReadersB(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 2, true, req);
	}

	public List<Map<String, Object>> getPowerReadersC(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerMembers(parentID, exceptSelf, 3, true, req);
	}

	/**
	 * 获得有审批权限的联系人,不获取部门分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPowerMembers(String parentID,
			boolean exceptSelf, int typeid, boolean isread,
			HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		// 屏蔽外部注册用户的功能
		try {		
			Users user = getCurrentUser(req);
			String pFix="g";
			String pID="0";
			if (parentID.length() > 2) {
				pFix = parentID.split("-")[0];
				pID = parentID.split("-")[1];
			}
			if ((user.getCompany().getId()!=1L)&&(!pFix.equalsIgnoreCase("ctmg"))) {
				Long orgid = null;
				try {
					orgid = Long.valueOf(pID);
				} catch (Exception e) {
					// 容错的不处理
					System.out.println("orgid=======" + orgid);
					orgid = Long.valueOf(0);
				}
				if (orgid == 0) {
					// 根节点，需要根据当前用户获取parentid
					if (typeid == 1)// 处室时显示当前用户所在处室的签批领导
					{
						orgid = addressListService.getGroupByUserId(user
								.getId());
					} else if (typeid == 2)// 单位时显示当前单位的所有处室
					{
						orgid = addressListService.getRootDepByUserId(user
								.getId());
					}
				}
				Map<String, Object> allMembersMap = addressListService
						.getGroupAndMemberList(orgid,
								user.getCompany().getId(), "groupcode",
								"sortNum");
	
				List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap
						.get("groupMemberList");
	
				List<Organizations> groupList = (List<Organizations>) allMembersMap
						.get("groupList");
				if (groupList != null && !groupList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgList(groupList, "g");
					nodeMap.addAll(nodeMapTemp);
				}
	
				if (null != groupMemberList && !groupMemberList.isEmpty()
						&& !isread) {
					groupMemberList = filterLeader(groupMemberList);
				}
				List<Long> onlineIdList = (List<Long>) allMembersMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				if (groupMemberList != null && !groupMemberList.isEmpty()) {
					// groupMemberList =
					// WebTools.sortUsers(groupMemberList,"User","realName",true);//不能用这个排序，必须用sortNum
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgMemberListWithOrg(groupMemberList,
									onlineIdList, "gm", exceptSelf,
									user.getId());
					nodeMap.addAll(nodeMapTemp);
				}
			}
			
			if(!pFix.equalsIgnoreCase("g")){
				// 无论是否是外部注册的人，都增加我的联系人分组人员
				List<Map<String, Object>> list=addressListService.getCtmGOrMNode(user.getId(),parentID,isread,true);
				if(list!=null && list.size()>0){
					nodeMap.addAll(list);
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeMap;
	}

	private List<DepMemberPo> filterLeader(List<DepMemberPo> memberList) {
		List<DepMemberPo> result = new ArrayList<DepMemberPo>();
		int size = memberList.size();
		for (int i = 0; i < size; i++) {
			DepMemberPo po = memberList.get(i);
			if (null == po.getOrganization()) {
				continue;
			}
			// int role =
			// ApprovalUtil.instance().getUserRole(po.getUser().getId());
			// 判断这个角色有没有权限
			long myrole = permissionService.getSystemPermission(po.getUser()
					.getId());
			// boolean toAduit = FlagUtility.isValue(role,
			// ManagementCons.AUDIT_SEND_FLAG);
			boolean aduit = FlagUtility.isValue(myrole,
					ManagementCons.AUDIT_AUDIT_FLAG);
			// boolean managerment = FlagUtility.isValue(role,
			// ManagementCons.AUDIT_MANGE_FLAG);
			if (aduit) {
				result.add(po);
			}
		}
		return result;
	}

	/**
	 * 外部功能接口 签批功能中选择联系人的时候所用搜索联系人方法
	 * 
	 * @param keyword
	 *            关键字
	 * @param isLeader
	 *            是否过滤领导
	 * @return nodeMap 搜索后的联系人树
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchPersons(String parentID,
			boolean exceptSelf, String keyword, boolean isLeader,
			HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);
			// 过滤外部注册用户的搜索
			if (user.getCompany().getId() != 1L) {
				Map<String, Object> searchResultMap = addressListService
						.getSearchResultMap(user.getCompany().getId(), keyword);
				List<DepMemberPo> searchResultList = (List<DepMemberPo>) searchResultMap
						.get("searchResultList");
				List<Long> onlineIdList = (List<Long>) searchResultMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				if (null != searchResultList && !searchResultList.isEmpty()
						&& isLeader) {
					searchResultList = filterLeader(searchResultList);
				}
				if (null != searchResultList && !searchResultList.isEmpty()) {
					searchResultList = WebTools.sortUsers(searchResultList,
							"User", "realName", true);
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgMemberListWithOrg(searchResultList,
									onlineIdList, "gm", exceptSelf,
									user.getId());
					nodeMap.addAll(nodeMapTemp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return nodeMap;
	}

	/**
	 * 得到用户自定义组的所有成员邮件地址
	 */
	public String findByAllGroupUser(Long ownerId, HttpServletRequest req) {
		if (ownerId == null || ownerId == 0) {
			Users user = getCurrentUser(req);
			ownerId = user.getId();
		}
		List<AddressBean> us = addressListService.getCtmGroupService().getAllCtmGMBean(ownerId);
		return getUserMail(us);
	}

	/**
	 * 得到用户定义的某个自定义中的成员邮件地址
	 */
	public String findByGroupUser(Long groupId, HttpServletRequest req) {
		List<Users> us = addressListService.findByGroupUser(groupId);
		List<AddressListPo> adds = addressListService
				.findByGroupIdOuter(groupId);
		StringBuffer sBuffer = new StringBuffer(getUserMailByUser(us));
		for (AddressListPo ad : adds) {
			sBuffer.append("'");
			sBuffer.append(ad.getRealName());
			sBuffer.append("'<");
			sBuffer.append(ad.getRealEmail());
			sBuffer.append(">,");
		}
		return sBuffer.toString();
	}

	/**
	 * 得到用户的邮件地址
	 * 
	 * @param userId
	 * @return
	 */
	public String getUser(Long userId, HttpServletRequest req) {
		Users u = addressListService.getUser(userId);
		if (u != null) {
			return "'" + u.getRealName() + "'<" + u.getRealEmail() + ">";
		}
		return "";
	}

	/**
	 * 得到系统中所有的用户邮件地址
	 * 
	 * @return
	 */
	public String getAllUserMail(HttpServletRequest req) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		List<Users> us = userService.getUsers(-1, -1, null, null);
		return getUserMailByUser(us);
	}

	/**
	 * 得到某个组织的邮件地址
	 * 
	 * @param orgId
	 * @return
	 */
	public String getOrganizationUser(Long orgId, boolean treeFlag,
			HttpServletRequest req) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		ArrayList<Long> orgIds = new ArrayList<Long>();
		orgIds.add(orgId);
		List<Users> us = userService.getUsersByOrgId(orgIds, treeFlag);
		return getUserMailByUser(us);
	}

	/**
	 * 得到用户所在所有组的所有成员的邮件地址。
	 * 
	 * @param ownerId
	 * @return
	 */
	public String getAllGroupUser(Long ownerId, HttpServletRequest req) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		if (ownerId == null || ownerId == 0) {
			Users user = getCurrentUser(req);
			ownerId = user.getId();
		}
		List<Groups> groups = userService.getGroupsByUsers(ownerId);
		ArrayList<Long> groupIds = new ArrayList<Long>();
		for (Groups t : groups) {
			groupIds.add(t.getId());
		}
		List<Users> us = userService.getUsersByGroupId(groupIds, true);
		return getUserMailByUser(us);
	}

	/**
	 * 得到组中所有成员的邮件地址。
	 * 
	 * @param groupId
	 * @return
	 */
	public String getGroupUser(Long groupId, boolean treeFlag,
			HttpServletRequest req) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		ArrayList<Long> groupIds = new ArrayList<Long>();
		groupIds.add(groupId);
		List<Users> us = userService.getUsersByGroupId(groupIds, treeFlag);
		return getUserMailByUser(us);
	}

	private String getUserMailByUser(List<Users> us) {
		if (us != null) {
			StringBuffer sb = new StringBuffer();
			for (Users t : us) {
				sb.append("'");
				sb.append(t.getRealName());
				sb.append("'<");
				sb.append(t.getRealEmail());
				sb.append(">,");
			}
			return sb.toString();
		}
		return "";
	}
	
	private String getUserMail(List<AddressBean> us) {
		if (us != null) {
			StringBuffer sb = new StringBuffer();
			for (AddressBean t : us) {
				sb.append("'");
				sb.append(t.getRealName());
				sb.append("'<");
				sb.append(t.getRealEmail());
				sb.append(">,");
			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * 添加外部联系人
	 * 
	 * @param groupId
	 *            分组ID
	 * @param name
	 *            姓名
	 * @param email
	 *            邮箱
	 * @param comment
	 *            备注
	 * @param isman
	 *            性别
	 * @param req
	 *            请求
	 * @return 1:成功 -1:邮箱已经存在 -2:邮箱存在于公司部门 0:失败
	 */
	public int addOuterAddress(Long groupId, String name, String email,
			String phone, String comment, boolean isman, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		AddressBean addressBean = new AddressBean(null,
				user.getId(), // ownerId,
				user, // userinfo,
				groupId, // groupId,
				name, // userName,
				email, // realEmail,
				name, // realName,
				"", // duty,
				"", // image,
				"", // mobile,
				"", // companyName,
				"", // fax,
				phone, // phone,
				"", // postcode,
				"", // address,
				"", // companyAddress,
				isman, // man,
				null, // birthday,
				"", // department,
				comment // comment
		);
		return addressListService.getCtmGroupService().addCtmGMOuter(addressBean);
	}

	/**
	 * 导入第一步，上传导入文档
	 * 
	 * @param file
	 *            文档信息
	 * @param req
	 *            请求
	 * @return 第一步信息
	 */
	public Map<String, Object> upFile(InputStream inputStream,
			HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		BufferedReader br = null;
		String upPath = req.getSession().getServletContext().getRealPath("/")
				+ "data/uploadfile/" + new Date().getTime() + ".txt";
		File upFile = new File(upPath);
		File upFileDir = upFile.getParentFile();
		if (!upFileDir.exists()) {
			upFileDir.mkdirs();
		}
		FileOutputStream write = null;
		try {
			write = new FileOutputStream(upFile);
			br = new BufferedReader(
					new InputStreamReader(inputStream, "gb2312"));
			String readLine = null;
			StringBuffer contextBr = new StringBuffer();
			int i = 0;
			String colums = "";
			while ((readLine = br.readLine()) != null) {
				if (i == 0) {
					colums = readLine;
				}
				contextBr.append(readLine).append("\n");
				i++;
			}
			write.write(contextBr.toString().getBytes());
			write.flush();
			write.close();
			inputStream.close();
			String[] fields = colums.split(",");
			map.put("isSuc", PageConstant.VALIDATOR_NAME_SUC);
			map.put("upPath", upPath);
			map.put("fields", fields);

		} catch (IOException e) {
			e.printStackTrace();
			map.put("isSuc", PageConstant.VALIDATOR_NAME_FAIL);
		}
		return map;
	}

	/**
	 * 删除上传的文档
	 * 
	 * @param path
	 *            上传文档的路径
	 * @param req
	 *            请求信息
	 * @return 删除是否成功
	 */
	public boolean delUpFile(String path, HttpServletRequest req) {
		File file = new File(path);
		try {
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 导入通讯录
	 * 
	 * @param fieldList
	 *            字段列表
	 * @param filePath
	 *            文件路径
	 * @param req
	 *            请求信息
	 * @return 导入结果
	 */
	public boolean impCplAddr(List<String> fieldList, String filePath,
			HttpServletRequest req) {
		try {
			Users user = getCurrentUser(req);
			addressListService.importAddr(fieldList, filePath, user);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// /////////////////////////修改//////////////////////////////////////
	public List<Map<String, Object>> getPowerSharedReadersA(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerSharedMembers(parentID, exceptSelf, 1, true, req);
	}

	public List<Map<String, Object>> getPowerSharedReadersB(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerSharedMembers(parentID, exceptSelf, 2, true, req);
	}

	public List<Map<String, Object>> getPowerSharedReadersC(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getPowerSharedMembers(parentID, exceptSelf, 3, true, req);
	}

	/**
	 * 选择对用户公开日志的联系人,不获取部门分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPowerSharedMembers(String parentID,
			boolean exceptSelf, int typeid, boolean isread,
			HttpServletRequest req) {

		if (parentID.length() > 2) {
			parentID = parentID.split("-")[1];
		}
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);
			Long orgid = null;
			try {
				orgid = Long.valueOf(parentID);
			} catch (Exception e) {
				// 容错的不处理
				orgid = Long.valueOf(0);
			}
			if (orgid == 0) {
				// 根节点，需要根据当前用户获取parentid
				if (typeid == 1) {// 处室时显示当前用户所在处室的签批领导
					orgid = addressListService.getGroupByUserId(user.getId());
				} else if (typeid == 2) {// 单位时显示当前单位的所有处室
					orgid = addressListService.getRootDepByUserId(user.getId());
				}
			}
			Map<String, Object> allMembersMap = addressListService
					.getSharedGroupAndMemberList(user.getId(), user
							.getCompany().getId(), orgid, "groupcode",
							"sortNum");
			List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap
					.get("groupMemberList");

			List<Organizations> groupList = (List<Organizations>) allMembersMap
					.get("groupList");
			if (groupList != null && !groupList.isEmpty()) {
				List<Map<String, Object>> nodeMapTemp = TreeUtil
						.convertOrgList(groupList, "g");
				nodeMap.addAll(nodeMapTemp);
			}

			if (null != groupMemberList && !groupMemberList.isEmpty()
					&& !isread) {
				groupMemberList = filterLeader(groupMemberList);
			}
			List<Long> onlineIdList = (List<Long>) allMembersMap
					.get(PageConstant.LG_ONLINE_ID_LIST);
			if (groupMemberList != null && !groupMemberList.isEmpty()) {
				// groupMemberList =
				// WebTools.sortUsers(groupMemberList,"User","realName",true);//不能用这个排序，必须用sortNum
				List<Map<String, Object>> nodeMapTemp = TreeUtil
						.convertOrgMemberListWithOrg(groupMemberList,
								onlineIdList, "gm", exceptSelf, user.getId());
				nodeMap.addAll(nodeMapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeMap;
	}

	/**
	 * 选择对用户公开日志联系人的时候所用搜索联系人
	 * 
	 * @param keyword
	 *            关键字
	 * @param isLeader
	 *            是否过滤领导
	 * @return nodeMap 搜索后的联系人树
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchSharedPersons(String parentID,
			boolean exceptSelf, String keyword, boolean isLeader,
			HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);
			Map<String, Object> searchResultMap =

			addressListService.getSearchSharedResultMap(user.getId(), user
					.getCompany().getId(), keyword);
			List<DepMemberPo> searchResultList = (List<DepMemberPo>)

			searchResultMap.get("searchResultList");
			List<Long> onlineIdList = (List<Long>) searchResultMap.get

			(PageConstant.LG_ONLINE_ID_LIST);
			if (null != searchResultList && !searchResultList.isEmpty()
					&& isLeader) {
				searchResultList = filterLeader(searchResultList);
			}
			if (null != searchResultList && !searchResultList.isEmpty()) {
				searchResultList = WebTools.sortUsers

				(searchResultList, "User", "realName", true);
				List<Map<String, Object>> nodeMapTemp =

				TreeUtil.convertOrgMemberListWithOrg(searchResultList,
						onlineIdList, "gm", exceptSelf, user.getId());
				nodeMap.addAll(nodeMapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return nodeMap;
	}

	/**
	 * 根据前台当前的用户ID选择显示的联系人列表
	 * 
	 * @param parentID
	 * @param exceptSelf
	 * @param userId
	 *            用户id
	 * @param req
	 * @return
	 */
	public List<Map<String, Object>> getInviteA(String parentID,
			boolean exceptSelf, String userId, HttpServletRequest req) {
		return getPowerMembers(parentID, userId, exceptSelf, 1, true, req);
	}

	public List<Map<String, Object>> getInviteB(String parentID,
			boolean exceptSelf, String userId, HttpServletRequest req) {
		return getPowerMembers(parentID, userId, exceptSelf, 2, true, req);
	}

	public List<Map<String, Object>> getInviteC(String parentID,
			boolean exceptSelf, String userId, HttpServletRequest req) {
		return getPowerMembers(parentID, userId, exceptSelf, 3, true, req);
	}

	/**
	 * 根据用户id获得相关的部门和用户map
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @param userId
	 *            用户id
	 * @return 分组信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPowerMembers(String parentID,
			String userid, boolean exceptSelf, int typeid, boolean isread,
			HttpServletRequest req) {
		if (parentID.length() > 2) {
			parentID = parentID.split("-")[1];
		}
		Long UID = Long.valueOf(userid);
		Users user = userService.getUserById(UID);
		if (user != null) {
			List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
			try {
				Long orgid = null;
				try {
					orgid = Long.valueOf(parentID);
				} catch (Exception e) {
					// 容错的不处理
					orgid = Long.valueOf(0);
				}
				if (orgid == 0) {
					// 根节点，需要根据当前用户获取parentid
					if (typeid == 1)// 处室时显示当前用户所在处室的签批领导
					{
						orgid = addressListService.getGroupByUserId(user
								.getId());
					} else if (typeid == 2)// 单位时显示当前单位的所有处室
					{
						orgid = addressListService.getRootDepByUserId(user
								.getId());
					}
				}
				Map<String, Object> allMembersMap = addressListService
						.getGroupAndMemberList(orgid,
								user.getCompany().getId(), "groupcode",
								"sortNum");

				List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap
						.get("groupMemberList");
				List<Organizations> groupList = (List<Organizations>) allMembersMap
						.get("groupList");
				if (groupList != null && !groupList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgList(groupList, "g");
					nodeMap.addAll(nodeMapTemp);
				}

				if (null != groupMemberList && !groupMemberList.isEmpty()
						&& !isread) {
					groupMemberList = filterLeader(groupMemberList);
				}
				List<Long> onlineIdList = (List<Long>) allMembersMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				if (groupMemberList != null && !groupMemberList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgMemberListWithOrg(groupMemberList,
									onlineIdList, "gm", exceptSelf,
									user.getId());
					nodeMap.addAll(nodeMapTemp);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return nodeMap;
		} else
			return null;
	}

	/**
	 * 根据用户id和关键字搜索联系人方法
	 * 
	 * @param keyword
	 *            关键字
	 * @param isLeader
	 *            是否过滤领导
	 * @param userId
	 *            用户id
	 * @return nodeMap 搜索后的联系人树
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchInvitePersons(String parentID,
			boolean exceptSelf, String userid, String keyword,
			boolean isLeader, HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Long UID = Long.valueOf(userid);
		Users user = userService.getUserById(UID);
		if (user != null) {
			try {
				Map<String, Object> searchResultMap = addressListService
						.getSearchResultMap(user.getCompany().getId(), keyword);
				List<DepMemberPo> searchResultList = (List<DepMemberPo>) searchResultMap
						.get("searchResultList");
				List<Long> onlineIdList = (List<Long>) searchResultMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				if (null != searchResultList && !searchResultList.isEmpty()
						&& isLeader) {
					searchResultList = filterLeader(searchResultList);
				}
				if (null != searchResultList && !searchResultList.isEmpty()) {
					searchResultList = WebTools.sortUsers(searchResultList,
							"User", "realName", true);
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgMemberListWithOrg(searchResultList,
									onlineIdList, "gm", exceptSelf,
									user.getId());
					nodeMap.addAll(nodeMapTemp);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return nodeMap;
		} else
			return null;
	}

	// ///////////////////////////////获得所有公司联系人,后台日志模块///////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllUsers(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		String orgName = null;
		String CompanyId = null;
		Boolean isOnline = true;
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);
			if (parentID.equals("all-0")) {
				short start = 1;
				short end = 4;
				List<Users> superAdminList = userService.getUserByRole(start,end);
				List<Long> onlinelist = new ArrayList<Long>();
				if (null != superAdminList && !superAdminList.isEmpty()) {
					for(int i = 0;i<superAdminList.size();i++)onlinelist.add(superAdminList.get(i).getId());
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertSearchUsers(superAdminList, onlinelist,
									"gm", exceptSelf, user.getId());
					nodeMap.addAll(nodeMapTemp);
				}
				List<Company> CompanyList = userService.getCompanyList();
				if (CompanyList != null && !CompanyList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertCompanyList(CompanyList, "cop");
					nodeMap.addAll(nodeMapTemp);
				}
				
			} else {
				Long orgid = null;
				Long cmpid = null;
				orgName = parentID.split("-")[0];
				if (orgName.equals("g")) {
					CompanyId = parentID.split("-")[1];
					parentID = parentID.split("-")[2];
				} else if (orgName.equals("cop")) {
					CompanyId = parentID.split("-")[1];
					parentID = "0";
				}
				try {
					cmpid = Long.valueOf(CompanyId);
					orgid = Long.valueOf(parentID);
				} catch (Exception e) {
					// 容错的不处理
					orgid = Long.valueOf(0);
				}
				Map<String, Object> allMembersMap = addressListService
						.getGroupAndMemberList(orgid, cmpid, "groupcode",
								"sortNum");
				List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap
						.get("groupMemberList");
				List<Organizations> groupList = (List<Organizations>) allMembersMap
						.get("groupList");
				Users companyAdmin = (Users) allMembersMap.get("companyAdmin");
				List<Long> onlineIdList = (List<Long>) allMembersMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				if (orgName.equals("cop")) {
					if (companyAdmin != null) {
						isOnline = onlineIdList.contains(companyAdmin.getId());
						Map<String, Object> adminMap = TreeUtil.convertSearchU(
								companyAdmin, isOnline, "gm");
						nodeMap.add(adminMap);
					}
				}
				if (groupList != null && !groupList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertOrgList(groupList, "g-" + CompanyId);
					nodeMap.addAll(nodeMapTemp);
				}

				if (groupMemberList != null && !groupMemberList.isEmpty()) {
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertAllOrgMemberListWithOrg(groupMemberList,
									onlineIdList, "gm", exceptSelf,
									user.getId());
					nodeMap.addAll(nodeMapTemp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeMap;
	}

	/**
	 * 
	 * @param keyword
	 *            关键字
	 * @return nodeMap 搜索后的联系人树
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchAllPersons(String parentID,
			boolean exceptSelf, String keyword, boolean isLeader,
			HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = (Users) req.getSession().getAttribute(
					PageConstant.LG_SESSION_USER);
			Map<String, Object> searchResultMap = addressListService
					.getSearchAllUserMap(keyword);
			List<Users> searchResultList = (List<Users>) searchResultMap
					.get("searchResultList");
			List<Long> onlineIdList = (List<Long>) searchResultMap
					.get(PageConstant.LG_ONLINE_ID_LIST);

			if (null != searchResultList && !searchResultList.isEmpty()) {
				List<Map<String, Object>> nodeMapTemp = TreeUtil
						.convertSearchUsers(searchResultList, onlineIdList,
								"gm", exceptSelf, user.getId());
				nodeMap.addAll(nodeMapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return nodeMap;
	}

	// /////////////////////////////////修改结束/////////////////////////////////////
	/*
	 * 他人共享中筛选对话框获取的人员列表
	 */
	public List<Map<String, Object>> getSharer(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 0, req);
	}

	/*
	 * 待办中筛选对话框获取的人员列表
	 */
	public List<Map<String, Object>> getSenderOfTodo(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 1, req);
	}

	/*
	 * 已办中筛选对话框获取的人员列表
	 */
	public List<Map<String, Object>> getSenderOfDone(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 2, req);
	}

	/*
	 * 送阅/收阅中筛选对话框获取的人员列表
	 */
	public List<Map<String, Object>> getSenderOfToread(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 3, req);
	}

	/*
	 * 草稿中筛选对话框的人员列表
	 */
	public List<Map<String, Object>> getAccepterOfDraft(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 4, req);
	}

	/*
	 * 我的送文中筛选对话框的人员列表
	 */
	public List<Map<String, Object>> getAccepterOfMyquest(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 5, req);
	}

	/*
	 * 事务交办中待办的交办人
	 */
	public List<Map<String, Object>> getAssignederOfTodo(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 6, req);
	}

	/*
	 * 事务交办中办结的交办人
	 */
	public List<Map<String, Object>> getAssignederOfFiled(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 7, req);
	}

	/*
	 * 事务交办中我的交办的办理人
	 */
	public List<Map<String, Object>> getTransactorOfMyquest(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 8, req);
	}

	/*
	 * 会议通知中的待办的通知者
	 */
	public List<Map<String, Object>> getInformerOfTodo(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 9, req);
	}

	/*
	 * 会议通知中的待办的召开人
	 */
	public List<Map<String, Object>> getConvenerOfTodo(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 10, req);
	}

	/*
	 * 会议通知中的已办的通知者
	 */
	public List<Map<String, Object>> getInformerOfFiled(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 11, req);
	}

	/*
	 * 会议通知中的已办的召开人
	 */
	public List<Map<String, Object>> getConvenerOfFiled(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 12, req);
	}

	/*
	 * 会议通知中的我的通知的召开人
	 */
	public List<Map<String, Object>> getConvenerOfMyquest(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return getMemberOfFilter(parentID, exceptSelf, 14, req);
	}

	/*
	 * 筛选对话框中获取所对应的模块的人员列表
	 */
	public List<Map<String, Object>> getMemberOfFilter(String parentID,
			boolean exceptSelf, int typeid, HttpServletRequest req) {
		if (parentID.length() > 2) {
			parentID = parentID.split("-")[1];
		}

		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = getCurrentUser(req);

			Long orgid = null;
			try {
				orgid = Long.valueOf(parentID);
			} catch (Exception e) {
				// 容错的不处理
				System.out.println("orgid=======" + orgid);
				orgid = Long.valueOf(0);
			}
			if (orgid == 0) {
				// 根节点，需要根据当前用户获取parentid
				if (typeid == 0)// 他人共享的共享者
				{
					orgid = 1l;
				} else if (typeid == 1)// 待办的原始送文人
				{
					orgid = 2l;
				} else if (typeid == 2)// 已办的原始送文人
				{
					orgid = 3l;
				} else if (typeid == 3)// 送阅/已阅的原始发文人
				{
					orgid = 4l;
				} else if (typeid == 4)// 草稿的收文人
				{
					orgid = 5l;
				} else if (typeid == 5)// 我的送文的收文人
				{
					orgid = 6l;
				} else if (typeid == 6)// 事务交办中的待办的交办人
				{
					orgid = 7l;
				} else if (typeid == 7)// 事务交办中的办结的交办人
				{
					orgid = 8l;
				} else if (typeid == 8)// 事务交办中的我的交办的交办人
				{
					orgid = 9l;
				} else if (typeid == 9)// 会议通知中的待办的通知者
				{
					orgid = 10l;
				} else if (typeid == 10)// 会议通知中的待办的召开人
				{
					orgid = 11l;
				} else if (typeid == 11)// 会议通知中的已办的通知者
				{
					orgid = 12l;
				} else if (typeid == 12)// 会议通知中的已办的召开人
				{
					orgid = 13l;
				} else if (typeid == 14)// 会议通知中的我的通知的召开人
				{
					orgid = 15l;
				}
			}
			if (orgid != 11l && orgid != 13l && orgid != 15l) {
				Map<String, Object> allMembersMap = addressListService
						.getFilterMemberList(orgid, user.getId(), "groupcode",
								"sortNum");
				List<Users> memberList = (List<Users>) allMembersMap.get("memberList");
				// List<Organizations> groupList = (List<Organizations>)
				// allMembersMap
				// .get("groupList");
				// if (groupList != null && !groupList.isEmpty()) {
				// List<Map<String, Object>> nodeMapTemp = TreeUtil
				// .convertOrgList(groupList, "g");
				// nodeMap.addAll(nodeMapTemp);
				// }
				// if (null != groupMemberList && !groupMemberList.isEmpty()) {
				// groupMemberList = filterLeader(groupMemberList);
				// }
				List<Long> onlineIdList = (List<Long>) allMembersMap.get(PageConstant.LG_ONLINE_ID_LIST);
				if (memberList != null && !memberList.isEmpty()) {
					// groupMemberList =
					// WebTools.sortUsers(groupMemberList,"User","realName",true);//不能用这个排序，必须用sortNum
					List<Map<String, Object>> nodeMapTemp = TreeUtil
							.convertFilterMemberList(memberList, onlineIdList, "", orgid, exceptSelf);
					nodeMap.addAll(nodeMapTemp);
				}
			} else {
				Map<String, Object> allMembersMap = addressListService
						.getFilterMemberList(orgid, user.getId(), "groupcode",
								"sortNum");
				List<String> memberList = (List<String>) allMembersMap
						.get("memberList");
				List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < memberList.size(); i++) {
					Map<String, Object> nodeMapTemp = new HashMap<String, Object>();
					nodeMapTemp.put("id", i);
					nodeMapTemp.put("text", memberList.get(i));
					nodeMapTemp.put("leaf", true);
					nodeMapTemp.put("icon", WebConfig.userPortrait
							+ "image.jpg");
					nodeMapTemp.put("realName", memberList.get(i));
					nodeList.add(nodeMapTemp);
				}
				nodeMap.addAll(nodeList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeMap;
	}

	/**
	 * 添加联系人分组(不包含联系人) ydm
	 * 
	 * @param ctmGName
	 *            分组名
	 * @param req
	 *            请求信息
	 * @return 添加信息
	 */
	public int addCtmGByGName(String ctmGName, HttpServletRequest req) {
		Users userInfo = getCurrentUser(req);
		int result = addressListService.getCtmGroupService().addCtmG(userInfo.getId(), ctmGName,
				null);
		return result;
	}

	/**
	 * 编辑联系人分组
	 * 
	 * @param gId
	 *            用户分组的ID
	 * @param ctmGName
	 *            用户分组名
	 * @param req
	 * @return
	 */
	public int editCtmGName(Long gId, String ctmGName, HttpServletRequest req) {
		Users userInfo = getCurrentUser(req);
		return addressListService.getCtmGroupService().editCtmG(userInfo.getId(), gId, ctmGName,
				null);
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getAllUserMailStrArray(HttpServletRequest req) {
		Users currentuser = getCurrentUser(req);
		if (null == currentuser) {
			return new String[]{};
		}
		List<AddressBean> abList = this.addressListService.getCtmGroupService().getAllCtmGMBean(currentuser.getId());//userService.getUsers(-1, -1, null, null);
		String[] ret = null;
		if (abList != null) {
			ret = new String[abList.size()];
			StringBuffer sb = new StringBuffer();
			int i = 0;
			for (AddressBean t : abList) {
				sb.append("'");
				sb.append(t.getRealName());
				sb.append("'<");
				sb.append(t.getRealEmail());
				sb.append(">");
				ret[i] = sb.toString();
				i++;
				sb.delete(0, sb.length());
			}
		}
		return ret;
	}
	
	public String getUserMailStrById(Long userId) {
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users u = userService.getUser(userId);
		if (u != null) {
			return "'" + u.getRealName() + "'<" + u.getRealEmail() + ">";
		}
		return "";
	}
	
	/**
	 * 发送短信
	 * 
	 * @param data
	 * @param req
	 * @param resp
	 * @return
	 *          -1 用户sessin过期
	 *          0 :短信发送失败
	 *          1: 短信发送成功
	 *          2: 短信发送条数超过设置
	 * @throws Exception
	 * 
	 */
	public int sendSms(Map<String,String> data,HttpServletRequest req, HttpServletResponse resp) throws Exception{
		    Users currentuser = getCurrentUser(req);
		    //当前用户是否已经session 失效。
			if(null !=currentuser){
				//获取发送短信的条数
				int size=this.getSmsTelNos(data).split(";").length;
				//验证用户所在单位的短信设置
				if(this.validateUserSMS(size,currentuser)){
					String value=this.sendSms(this.getSmsTelNos(data), this.getSMSContent(data),data.get("backmobile"), currentuser.getUserName(), req, resp);
					return value.contains("\"errorCode\":\"0\"")? 1:0;
				}
				return 2;
			}
		return -1;
		
	}
	
	/**
	 * 验证短信发送的设置。
	 *     <p>通常用户所在的组织是选择短信发送的类型
	 *         <ul>
	 *            类型包括 
	 *              <li>每年短息发送量不限制 </li>
	 *              <li>每年短息发送量最大多少条</li>
	 *              <li>每个月短息发送量最大多少条</li>
	 *              <li>每天短息发送量最大多少条</li>
	 *         </ul>
	 *         <b>主要是根据部门所设置的类型，计算用户发送的短信的数量大小加上当前用户的发送的短信的数量 是否大于部门设置的数量。<b>
	 * @param size
	 *          发送顿先的总数量
	 * @param currentuser
	 *            当前用户的实例
	 * @return
	 *      验证是否成功
	 *      
	 * @throws Exception
	 */
	private boolean validateUserSMS(int size,Users currentuser) throws Exception{
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		Organizations organizations=userService.getOrganizationByUsers(currentuser.getId()).get(0);
		/**
		 * 组织短信设置策略 
		 *  0不限制
		 *  1：每年  max条
		 *  2:每月 max条
		 *  3：每天 max条
		 */
		int type=organizations.getSmsLimit();
		
		//部门的短信条数设置
		int maxNums=organizations.getSmsMaxNums();
		
		FileSystemService filesystemservice = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		if(type == 0){ //不限制
			return true;
		}else if(type == 1){ //每年
			Calendar start=new GregorianCalendar();
			start.set(Calendar.MONTH, 0);
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE,0);
			start.set(Calendar.SECOND, 0);
			
			Calendar end=new GregorianCalendar();
			end.set(Calendar.YEAR, end.get(Calendar.YEAR)+1);
			end.set(Calendar.MONTH, 0);
			end.set(Calendar.DAY_OF_MONTH, 1);
			end.set(Calendar.HOUR_OF_DAY, 0);
			end.set(Calendar.MINUTE,0);
			end.set(Calendar.SECOND, 0);
			Long total=filesystemservice.getUserMessageInfoCount(currentuser.getId(), start.getTime(), end.getTime());
			return (total.intValue()+size) <= maxNums;
		}else if( type == 2){ // 每月
			Calendar start=new GregorianCalendar();
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE,0);
			start.set(Calendar.SECOND, 0);
			Calendar end=new GregorianCalendar();
			end.set(Calendar.MONTH, end.get(Calendar.MONTH)+1);
			end.set(Calendar.DAY_OF_MONTH, 1);
			end.set(Calendar.HOUR_OF_DAY, 0);
			end.set(Calendar.MINUTE,0);
			end.set(Calendar.SECOND, 0);
			Long total=filesystemservice.getUserMessageInfoCount(currentuser.getId(), start.getTime(), end.getTime());
			return (total.intValue()+size) <= maxNums;
		}else if( type == 3){ //每天
			
			Calendar start=new GregorianCalendar();
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE,0);
			start.set(Calendar.SECOND, 0);
			Calendar end=new GregorianCalendar();
			end.set(Calendar.DAY_OF_MONTH, end.get(Calendar.DAY_OF_MONTH)+1);
			end.set(Calendar.HOUR_OF_DAY, 0);
			end.set(Calendar.MINUTE,0);
			end.set(Calendar.SECOND, 0);
			Long total=filesystemservice.getUserMessageInfoCount(currentuser.getId(), start.getTime(), end.getTime());
			return (total.intValue()+size) <= maxNums;
		}
		
		return false;
	}
	
	/**
	 * 获取短信电话号码
	 * 
	 * @param data
	 * @return
	 */
	private String getSmsTelNos(Map<String,String> data){
		String mobile=data.get("personIds");
		if(mobile.endsWith(";")){
			mobile=mobile.substring(0,mobile.length()-1);
		}
		String addtionalNo=data.get("addtionalNo");
		if(addtionalNo.endsWith(";")){
			addtionalNo=addtionalNo.substring(0,addtionalNo.length()-1);
		}
		
		if(!addtionalNo.isEmpty()){
			if(mobile.trim().isEmpty()){
				mobile=addtionalNo;
			}else{
				mobile+=";"+addtionalNo;
			}
			
		}
		return mobile;
	}
	
	/**
	 * 获取短信内容
	 * @param data
	 * @return
	 */
	private String getSMSContent(Map<String,String> data){
		String comment=(String)data.get("comment");
		if (comment!=null && comment.length()>0)
		{
			return data.get("content")+" --"+ data.get("comment");
		}
		else
		{
			return data.get("content");
		}
	}
	
	/**
	 * 发送短信
	 * 
	 * @param data
	 * @param currentuser
	 * @param backmobile 短信返回的手机号
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	private String sendSms(String address,String content,String acount,String backmobile, HttpServletRequest req, HttpServletResponse resp) throws IOException{
		HashMap<String,Object> smslData=new HashMap<String, Object>();
		smslData.put("address",address);
		smslData.put("content", content);
		smslData.put("account", acount);
		smslData.put("backmobile", backmobile);
		smslData.put("type", apps.transmanager.weboffice.constants.server.Constant.SENDMSG);
		HashMap<String,Object> jsonParams=new HashMap<String, Object>();
		jsonParams.put(ServletConst.PARAMS_KEY, smslData);
		return UserOnlineHandler.sendNote(req, resp, jsonParams);
	}
	public boolean deleteMessage(Long id, HttpServletRequest req, HttpServletResponse resp){
		try{
			FileSystemService filesystemservice = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			filesystemservice.deleteMessage(id);
			return true;
			//String result = 
		}
		catch(Exception e){
			
		}
		return false;
	}
}
