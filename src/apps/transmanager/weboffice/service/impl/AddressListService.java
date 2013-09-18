package apps.transmanager.weboffice.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.dao.IAddressListDAO;
import apps.transmanager.weboffice.dao.ICtmGroupMemberDAO;
import apps.transmanager.weboffice.dao.IDepMemberDAO;
import apps.transmanager.weboffice.dao.IDepartmentDAO;
import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.ICtmGroupService;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.ApprovalDAO;
import apps.transmanager.weboffice.service.dao.PersonshareinfoDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.WebTools;

@Component(value = AddressListService.NAME)
public class AddressListService implements IAddressListService {
	public static final String NAME = "addressListService";
	@Autowired
	private IDepartmentDAO departmentDAO;
	@Autowired
	private IDepMemberDAO depMemberDAO;
	@Autowired
	private IOnlineDAO onlineDAO;
	@Autowired
	private ICtmGroupMemberDAO ctmGroupMemberDAO;
	@Autowired
	private StructureDAO structureDAO;
	@Autowired
	private IAddressListDAO addresslistDAO;

	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private PersonshareinfoDAO personshareinfoDAO;
	@Autowired
	private ApprovalDAO approvalDAO;
	@Autowired
	private ICtmGroupService ctmGroupService;
	@Override
	public ICtmGroupService getCtmGroupService() {
		return ctmGroupService;
	}

	public void setCtmGroupService(ICtmGroupService ctmGroupService) {
		this.ctmGroupService = ctmGroupService;
	}

	public IAddressListDAO getAddresslistDAO() {
		return addresslistDAO;
	}
	/**
	 * 获取当前用户的组织
	 */
	@Override
	public Long getGroupByUserId(Long userId) {
		List<DepMemberPo> list = depMemberDAO.findByProperty(
				DepMemberPo.class.getName(), "user.id", userId);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {// 先临时这样处理，以后要加级联删除的
				if (list.get(i).getOrganization() != null) {
					return list.get(i).getOrganization().getId();
				}
			}

		}
		return null;
	}

	/**
	 * 获取当前用户的根组织
	 */
	@Override
	public Long getRootGroupByUserId(Long userId) {
		List<DepMemberPo> list = depMemberDAO.findByProperty(
				DepMemberPo.class.getName(), "user.id", userId);
		if (list != null && list.size() > 0) {
			String code = list.get(0).getOrganization().getOrganizecode();
			if (code != null) {
				int index = code.indexOf("-");
				if (index != -1) {
					code = code.substring(0, index);
				}
				DepartmentPo org = departmentDAO.findById(
						DepartmentPo.class.getName(), Long.valueOf(code));
				if (org != null) {
					return org.getId();
				}
			}
		}
		return null;
	}

	/**
	 * 获取当前用户的根组织
	 */
	@Override
	public Long getRootDepByUserId(Long userId) {
		List<DepMemberPo> list = depMemberDAO.findByProperty(
				DepMemberPo.class.getName(), "user.id", userId);
		if (list != null && list.size() > 0) {
			String code = list.get(0).getOrganization().getParentKey();
			if (code != null) {
				int index = code.indexOf("-");
				if (index != -1) {
					code = code.substring(0, index);
				}
				DepartmentPo org = departmentDAO.findById(
						DepartmentPo.class.getName(), Long.valueOf(code));
				if (org != null) {
					return org.getId();
				}
			} else {
				return list.get(0).getOrganization().getId();
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getGroupAndMemberList(Long parentID,
			Long companyId, String sortName, String order) {
		return getGroupAndMemberList(parentID, companyId, sortName, order, 0);
	}

	/**
	 * 根据父组织获取子组织及该组织下的人员 parentID 父组织编号 typeid 类型，1为本处室
	 * 2为本单位3为全部单位，主要是parentID=0时有区别
	 */
	@Override
	public Map<String, Object> getGroupAndMemberList(Long parentID,
			Long companyId, String sortName, String order, int usertype) {
		Map<String, Object> groupAndMemberMap = new HashMap<String, Object>();

		List<DepMemberPo> groupMemberList = null;
		List<Long> onlineIdList = onlineDAO.findAllUserId();// 获取在线用户

		// 以下获取鼠标点击组织的所有成员
		if (parentID == null) {
			parentID = Long.valueOf(0);
		}
		if (parentID == 0) {
			// 不需要查人员
		} else {
			if (usertype == 0) {
				groupMemberList = filterSameUser(depMemberDAO
						.findOrgUsers(parentID));
			} else {
				groupMemberList = new ArrayList<DepMemberPo>();
			}
			// 要根据sortNum排序的签批领导
		}

		// 以下获取子组织单位列表
		List<Organizations> groupList = null;// 获取组织结构
		if (parentID == 0) {
			groupList = structureDAO.getChildOrganizations(null, companyId,
					false);
		} else {
			groupList = structureDAO.getChildOrganizations(parentID, companyId,
					false);
		}
		// 得到公司的管理员(用于日志中人员选择 added by hxy)
		Users cmpAdmin = structureDAO.getAdminByCmpId(companyId);
		// groupList=departmentDAO.findByProperty(DepartmentPo.class.getName(),"parent.id"
		// , parentID);//获取父节点下的所有单位
		// 以上获取子组织单位列表

		groupAndMemberMap.put("groupList", groupList);
		groupAndMemberMap.put("groupMemberList", groupMemberList);
		groupAndMemberMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		groupAndMemberMap.put("companyAdmin", cmpAdmin);

		return groupAndMemberMap;
	}

	@Override
	public Map<String, Object> getCustomGroupMap(Long ownerId) {
		List<CustomGroupPo> ctmGList = this.ctmGroupService.getCtmGList(ownerId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", ownerId);
		map.put("totalRecords", ctmGList.size());
		map.put("data", ctmGList);
		return map;
	}
	@Override
	public Map<String, Object> getCtmGMByGroupId(Long groupId) {
		Map<String, Object> map = ctmGroupService.getCtmGMByGId(groupId);
		List<AddressListPo> ctmGMListOuter = addresslistDAO.findByProperty(
				AddressListPo.class.getName(), "groupId", groupId);
		map.put("ctmGMListOuter", ctmGMListOuter);
		return map;
	}

//	/**
//	 * 获得用户的项目组
//	 * @deprecated
//	 * @param userId
//	 *            用户的ID
//	 * @return 用户所有的项目组
//	 */
//	public List<Groups> getTeamGByUserId(Long userId) {
//		List<Groups> teamGList = new ArrayList<Groups>();
//		try {
//			teamGList = structureDAO.findGroupsByUserId(userId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return teamGList;
//	}

//	/**
//	 * 获得用户所在项目组的成员（成员需要区分在线和非在线状态，同时获取在线用户ID集合）
//	 * 
//	 * @param teamGroupId
//	 *            项目组的ID
//	 * @return 项目组内成员和在线用户ID集合
//	 */
//	public Map<String, Object> getTeamGMByGroupId(Long teamGroupId) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		List<Users> teamGMList = structureDAO.findUsersByGroupId(teamGroupId,
//				false);
//		List<Long> onlineIdList = onlineDAO.findAllUserId();
//		map.put("teamGMList", teamGMList);
//		map.put("onlineIdList", onlineIdList);
//		return map;
//	}

	private List<DepMemberPo> searchDepMemberList(Long companyId, String key) {
		List<DepMemberPo> depMemberList = depMemberDAO.searchUsersByKey(
				companyId, key);
		return filterSameUser(depMemberList);
	}

	@Override
	public Map<String, Object> getAddrListPage(Long id, Long companyId,
			String type, String sort, String order, Page page) {
		return getAddrListPage(id, companyId, type, sort, order, page, 0);
	}
	@Override
	public Map<String, Object> getAddrListPage(Long id, Long companyId,
			String type, String sort, String order, Page page, int usertype) {
		List<Long> userIdList = new ArrayList<Long>();
		List<AddressListPo> addressList = new ArrayList<AddressListPo>();
		if (type.equalsIgnoreCase("ctmg")) {
			// userIdList = ctmGroupMemberDAO.findByGroup(id,page,sort,order);
			List<CtmGroupMemberPo> ctmGroupMemberPos = ctmGroupMemberDAO
					.findByProperty(CtmGroupMemberPo.class.getName(),
							"groupId", id);
			for (CtmGroupMemberPo ctmGMPo : ctmGroupMemberPos) {
				userIdList.add(ctmGMPo.getUserId());
			}
			addressList = addresslistDAO.findByProperty(
					AddressListPo.class.getName(), "groupId", id);
		} else if (type.equalsIgnoreCase("ctmgr")) {
			userIdList = ctmGroupMemberDAO.findByAll(id, page, sort, order);
			addressList = addresslistDAO.findByProperty(
					AddressListPo.class.getName(), "ownerId", id);
		} else if (type.equalsIgnoreCase("g")) {
			DepartmentPo departmentPo = departmentDAO.findById(
					DepartmentPo.class.getName(), id);
			userIdList = depMemberDAO.findByDepartment(departmentPo, page,
					sort, order, usertype);
		} else if (type.equalsIgnoreCase("gr")) {
			userIdList = depMemberDAO.findByDepAll(companyId, page, sort,
					order, usertype);
		} else {
			// 根据用户的id查找他的所有联系人ID
			userIdList = ctmGroupMemberDAO.findByAll(id, page, sort, order);
		}
		Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.put("userIdList", userIdList);
		listMap.put("addressList", addressList);
		return listMap;
	}
	/**
	 * 获取外部联系人
	 * @param groupid 联系人组
	 * @param userid 当前用户
	 * @return
	 */
	public List<AddressListPo> getOutContacts(Long groupid, Long userid) {
		List<AddressListPo> addressList = new ArrayList<AddressListPo>();
		if (groupid!=null && groupid.longValue()>0)
		{
			addressList = addresslistDAO.findByProperty(
					AddressListPo.class.getName(), "groupId", groupid);
		}
		else //当前用户的全部联系人
		{
			addressList = addresslistDAO.findByProperty(
					AddressListPo.class.getName(), "ownerId", userid);
		}
		return addressList;
	}
	@Override
	public Map<String, Object> getSearchResultMap(Long companyId, String keyword) {
		Map<String, Object> searchResultMap = new HashMap<String, Object>();
		List<DepMemberPo> searchResultList = depMemberDAO.searchUsersByKey(
				companyId, keyword);
		searchResultList = filterSameUser(searchResultList);
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		searchResultMap.put("searchResultList", searchResultList);
		searchResultMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		return searchResultMap;
	}

	private List<DepMemberPo> filterSameUser(List<DepMemberPo> searchReList) {
		List<DepMemberPo> noSameList = new ArrayList<DepMemberPo>();
		List<Long> idList = new ArrayList<Long>();
		for (DepMemberPo depMemberPo : searchReList) {
			if (!idList.contains(depMemberPo.getUser().getId())
					&& depMemberPo.getOrganization() != null) {// 不知道为什么有组织为空的记录去除——孙爱华
				idList.add(depMemberPo.getUser().getId());
				noSameList.add(depMemberPo);
			}
		}
		return noSameList;
	}
	/**
	 */
	@Override
	public List<Users> findByGroupUser(Long groupId) {
		return ctmGroupMemberDAO.findByGroupUser(groupId);
	}
	@Override
	public List<AddressListPo> findByGroupIdOuter(Long groupId) {
		return addresslistDAO.findByProperty(AddressListPo.class.getName(),
				"groupId", groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public Users getUser(Long userId) {
		return structureDAO.findUserById(userId);
	}
	@Override
	public AddressListPo getAddressById(Long id) {
		return addresslistDAO.findById(AddressListPo.class.getName(), id);
	};
	@Override
	public void importAddr(List<String> fieldList, String filePath, Users user)
			throws Exception {
		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String line = null;
		int i = 1;
		while ((line = br.readLine()) != null) {
			if (!line.equals("") && i != 1) {
				String[] values = line.split(",");
				AddressListPo address = new AddressListPo();
				for (int n = 0; n < fieldList.size(); n++) {
					if (n < values.length && null != values[n]
							&& !"".equals(fieldList.get(n))) {
						String value = values[n];
						AddressListPo.class.getMethod("set" + fieldList.get(n),
								String.class).invoke(address, value);
					}
				}
				address.setUserinfo(user);
				addresslistDAO.saveOrUpdate(address);
			}
			i++;
		}
		file.delete();

	}

	// ////////////////////////////////修改///////////////////////////////
	/**
	 * 根据父组织获取子组织及该组织下的人员 parentID 父组织编号 typeid 类型，1为本处室
	 * 2为本单位3为全部单位，主要是parentID=0时有区别
	 */
	@Override
	public Map<String, Object> getSharedGroupAndMemberList(Long userId,
			Long companyId, Long parentID, String sortName, String order) {
		Map<String, Object> groupAndMemberMap = new HashMap<String, Object>();

		List<DepMemberPo> groupMemberList = null;
		List<Long> onlineIdList = onlineDAO.findAllUserId();// 获取在线用户
		// List<Long> SharedIdList =
		// UserDAO.findAllSharedUserId(userId);//获取在线用户
		// 以下获取鼠标点击组织的所有成员
		if (parentID == null) {
			parentID = Long.valueOf(0);
		}
		if (parentID == 0) {
			// 不需要查人员
		} else {
			// groupMemberList =
			// filterSameUser(depMemberDAO.findOrgUsers(parentID));
			groupMemberList = filterSameUser(depMemberDAO.findOrgSharedUsers(
					userId, parentID));
			// 要根据sortNum排序的签批领导
		}

		// 以下获取子组织单位列表
		// List<DepartmentPo> groupList = null;//获取组织结构
		// groupList=departmentDAO.findOrgByParentId(parentID);//获取父节点下的所有单位,根据sortNum排序
		List<Organizations> groupList = null;// 获取组织结构
		if (parentID == 0) {
			groupList = structureDAO.getChildOrganizations(null, companyId, false);
		} else {
			groupList = structureDAO.getChildOrganizations(parentID, companyId, false);
		}
		// groupList=departmentDAO.findByProperty(DepartmentPo.class.getName(),"parent.id"
		// , parentID);//获取父节点下的所有单位
		// 以上获取子组织单位列表

		groupAndMemberMap.put("groupList", groupList);
		groupAndMemberMap.put("groupMemberList", groupMemberList);
		groupAndMemberMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		return groupAndMemberMap;
	}
	@Override
	public Map<String, Object> getSearchSharedResultMap(Long userid,
			Long companyId, String keyword) {
		Map<String, Object> searchResultMap = new HashMap<String, Object>();
		List<DepMemberPo> searchResultList = depMemberDAO
				.searchSharedUsersByKey(userid, companyId, keyword);
		searchResultList = filterSameUser(searchResultList);
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		searchResultMap.put("searchResultList", searchResultList);
		searchResultMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		return searchResultMap;
	}

	/**
	 * 根据关键字搜索用户（无公司之分，用于日志）
	 */
	@Override
	public Map<String, Object> getSearchAllUserMap(String keyword) {
		Map<String, Object> searchResultMap = new HashMap<String, Object>();
		List<Users> searchResultList = structureDAO.searchUser(0, 3, keyword,
				-1, -1, "realName", "asc");
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		searchResultMap.put("searchResultList", searchResultList);
		searchResultMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		return searchResultMap;
	}

	/////////////////////////////////修改结束//////////////////////////////
	/*
	 * 获取筛选的人员列表
	 * 
	 */
	@Override
	public Map<String, Object> getFilterMemberList(Long parentID,Long userId,String sortName,String order)
	{
		Map<String,Object> memberMap = new HashMap<String, Object>();
		if(parentID != 11l && parentID != 13l && parentID != 15l)
		{
			List<Users> memberList = null;
			List<Long> onlineIdList = onlineDAO.findAllUserId();//获取在线用户
			
			//以下获取鼠标点击组织的所有成员
			if (parentID == null) {
				parentID=Long.valueOf(0);
			}
			if (parentID == 0) {
				//不需要查人员
			} else if(parentID == 1) {
				memberList = personshareinfoDAO.findByShareUsers(userId,-1l);//获取他人共享中的共享人
			} else if(parentID == 2) {
				memberList = approvalDAO.findTodoSenderByUserId(userId);//获取待办中的原始发文人
			} else if(parentID == 3) {
				memberList = approvalDAO.findDoneSenderByUserId(userId);//获取已办中的原始发文人
			} else if(parentID == 4) {
				memberList = approvalDAO.findToreadSenderByUserId(userId);//获取送阅/已阅中的原始发文人
			} else if(parentID == 5) {
				memberList = approvalDAO.findDraftAccepterByUserId(userId);//获取草稿的收文人
			} else if(parentID == 6){
				memberList = approvalDAO.findMyquestAccepterByUserId(userId);//获取我的送文中的送签的收文人
			} else if (parentID == 7) {
				memberList = approvalDAO.findAssignederOfTodoByUserId(userId);//获取事务交办中的待办的交办人
			} else if (parentID == 8) {
				memberList = approvalDAO.findAssignederOfFiledByUserId(userId);//获取事务交办中的办结的交办人
			} else if (parentID == 9) {
				memberList = approvalDAO.findTransactorOfMyquestByUserId(userId);//获取事务交办中的我的交办的办理人
			} else if (parentID == 10){
				memberList = approvalDAO.findInformerOfTodoByUserId(userId);//获取会议通知中的待办的通知者
			} else if (parentID == 12) {
				memberList = approvalDAO.findInformerOfDoneByUserId(userId);//获取会议通知中的已办的通知者
			}
			memberMap.put("memberList", memberList);
			memberMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
			return memberMap;
		}else{
			List<String> memberList = null;
			if (parentID == 11) {
				memberList = approvalDAO.findConvenerOfTodoByUserId(userId);//获取会议通知中的待办的召开人
			} else if (parentID == 13) {
				memberList = approvalDAO.findConvenerOfDoneByUserId(userId);//获取会议通知中的已办的召开人
			} else if (parentID == 15) {
				memberList = approvalDAO.findConvenerOfMyquestByUserId(userId);//获取会议通知中的我的通知的召开人
			}
			memberMap.put("memberList", memberList);
			return memberMap;
		}
		
//			groupMemberList = filterSameUser(depMemberDAO.findOrgUsers(parentID));
			//要根据sortNum排序的签批领导
//		}

		//以下获取子组织单位列表
//		List<Organizations> groupList = null;//获取组织结构
//		if(parentID==0){
//			groupList=structureDAO.getChildOrganizations(null, companyId, false);	
//		}else{
//			groupList=structureDAO.getChildOrganizations(parentID, companyId, false);	
//		}
		

//		groupList=departmentDAO.findByProperty(DepartmentPo.class.getName(),"parent.id" , parentID);//获取父节点下的所有单位
		//以上获取子组织单位列表
		
//		groupAndMemberMap.put("groupList", groupList);
		
	}

	/**
	 * 
	 * @param userId
	 * @param pId
	 * @param isread 是否是读者，不是读者是领导时传fasle
	 * @param isouter 是否是签批，签批的时候不用获取外部联系人，传true，如果不需要获取外部联系人传true
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getCtmGOrMNode(Long userId, String pId,boolean isread,boolean isSign) {
		List<Map<String, Object>> ctmGOrMNodeMap = new ArrayList<Map<String, Object>>();
		if ("0".equals(pId.split("-")[1])) {
			List<CustomGroupPo> ctmGList = this.ctmGroupService.getCtmGList(userId);
			if (ctmGList != null && !ctmGList.isEmpty()) {
				ctmGOrMNodeMap = TreeUtil.convertCtmGListA(ctmGList, "ctmg");
			}
		} else {
			Long gId = Long.parseLong(pId.split("-")[1]);
			Map<String, Object> ctmGMMap = this.ctmGroupService.getCtmGMByGId(gId);
			List<CtmGroupMemberPo> ctmGMList=new ArrayList<CtmGroupMemberPo>();
			List<AddressListPo> ctmGMListOuter=new ArrayList<AddressListPo>();
			//是否是签批，是否获取外部联系人，签批|不获取=true，非签批|获取=false
			if (!isSign) {
				ctmGMListOuter = (List<AddressListPo>) ctmGMMap.get("ctmGMListOuter");
			}
			//是否是阅读者，是否是领导，阅读者|非领导=true，非阅读者|领导=false
			ctmGMList= (List<CtmGroupMemberPo>) ctmGMMap.get("ctmGMList");
			if (!isread) {
				ctmGMList=filterLeader(ctmGMList);
			}
			
			List<AddressBean> addBeanList = new ArrayList<AddressBean>();
			/**
			 * 合并内部联系人与外部联系人，组合左边的树不需要全部的信息，这里只传递部分信息
			 */
			if(null != ctmGMListOuter) {
				//不过滤领导
				for (AddressListPo addPo : ctmGMListOuter) {
					AddressBean addressBean = new AddressBean();
					addressBean.setRealEmail(addPo.getRealEmail());
					addressBean.setGroupId(addPo.getGroupId());
					addressBean.setId("outer-" + gId + "-" + addPo.getId()); // 外部联系人ID组成：outer-1-2,outer外部标识，1所在组的ID，2外部联系人ID
					String imageString = addPo.getImage();
					if (imageString == null || imageString.trim().equals("")
							|| imageString.equalsIgnoreCase("null")) {
						imageString = "image.jpg";
					}
					addressBean.setImage(imageString);
					addressBean.setUserName(addPo.getUserName());
					addressBean.setRealName(addPo.getRealName());
					addressBean.setOwnerId(addPo.getOwnerId());
					addBeanList.add(addressBean);
				}
			}
			if (null != ctmGMList) {
				for (CtmGroupMemberPo ctmPo : ctmGMList) {
//					Long cuserId = ctmPo.getUserId();
//					Users users = this.getUser(cuserId);
//					if (users == null) {
//						continue;
//					}
					AddressBean addressBean = new AddressBean();
					addressBean.setRealEmail(ctmPo.getMail());
					addressBean.setGroupId(gId);
					addressBean.setId("ctmgm-" + gId + "-" + ctmPo.getUserId()); // 内部联系人ID组成：ctmgm-1-2,outer外部标识，1所在组的ID，2用户ID
					String imageString = ctmPo.getImage();
					if ((imageString != null) && (imageString.indexOf("/") != -1)) {
						imageString = imageString.substring(
								imageString.lastIndexOf("/") + 1,
								imageString.length());
					}
					addressBean.setImage(imageString);
					addressBean.setUserName(ctmPo.getUserName());
					addressBean.setRealName(ctmPo.getRealName());
					addressBean.setOwnerId(userId);
					addBeanList.add(addressBean);
				}
			}
			addBeanList = WebTools.sortUsers(addBeanList, "realName", true);

			List<Long> onlineIdList = (List<Long>) ctmGMMap.get(PageConstant.LG_ONLINE_ID_LIST);
			ctmGOrMNodeMap = TreeUtil.convertCtmGMOutList(addBeanList, onlineIdList, "");
		}

		return ctmGOrMNodeMap;
	}

	@Override
	public List<String[]> searchRel(Users cuser, String key) {
		List<String[]> userSearchList = new ArrayList<String[]>();
		List<CtmGroupMemberPo> ctmGM = ctmGroupMemberDAO.searchByKey(key, cuser.getId());
		List<AddressListPo> ctmGMOut = addresslistDAO.searchByKey(key, cuser.getId());
		List<DepMemberPo> depMemberList = null;
		for (CtmGroupMemberPo cm : ctmGM) {
			String[] cmStr = new String[3];
			// value值的形式为 组ID-用户ID（包括层级关系）
			String email = cm.getMail();
			if (null == email || email.equals("")) {
				email = userDAO.findById(Users.class.getName(), cm.getUserId()).getRealEmail();
				if (null == email || email.equals("")) {
					email = "邮箱未知";
				} else {
				    cm.setMail(email);
			        ctmGroupMemberDAO.saveOrUpdate(cm);
				}
			}
			String name = cm.getUserName() + ";" + email;
			cmStr[0] = name;
			cmStr[1] = "true";
			cmStr[2] = "search-" + cm.getUserId();
			userSearchList.add(cmStr);
		}
		for (AddressListPo cm : ctmGMOut) {
			String[] cmStr = new String[3];
			// value值的形式为 组ID-用户ID（包括层级关系）
			String name = cm.getRealName() + ";" + cm.getRealEmail();
			cmStr[0] = name;
			cmStr[1] = "true";
			cmStr[2] = "osearch-" + cm.getId();
			userSearchList.add(cmStr);
		}
		if (cuser.getCompany().getId() != 1) {
		    depMemberList = searchDepMemberList(cuser.getCompany().getId(), key);
			if (depMemberList != null && !depMemberList.isEmpty()) {
				depMemberList = WebTools.sortUsers(depMemberList, "User",
						"realName", true);
				for (DepMemberPo depMember : depMemberList) {
					if(depMember.getUser().getId().intValue() == cuser.getId().intValue()){
						continue;
					}
					String[] depMemberS = new String[3];
					// value值的形式为 组ID-用户ID（包括层级关系）
					if (depMember.getOrganization() == null)
						continue;
					Organizations org = depMember.getOrganization();
					Users user = depMember.getUser();
					String name = user.getRealName() + ";" + user.getRealEmail()
							+ ";" + org.getName();
					depMemberS[0] = name;
					depMemberS[1] = "true";
					depMemberS[2] = "search-" + user.getId();
					userSearchList.add(depMemberS);
				}
			}
		}
		return userSearchList;
	}
	
	private List<CtmGroupMemberPo> filterLeader(List<CtmGroupMemberPo> memberList) {
		List<CtmGroupMemberPo> result = new ArrayList<CtmGroupMemberPo>();
		int size = memberList.size();
		PermissionService permissionService=(PermissionService)ApplicationContext.getInstance().getBean("permissionService");
		for (int i = 0; i < size; i++) {
			CtmGroupMemberPo po = memberList.get(i);
			if (null == po.getUserId()) {
				continue;
			}
			// 判断这个角色有没有权限
			long myrole = permissionService.getSystemPermission(po.getUserId());
			boolean aduit = FlagUtility.isValue(myrole,ManagementCons.AUDIT_AUDIT_FLAG);
			if (aduit) {
				result.add(po);
			}
		}
		return result;
	}

	@Override
	public void update(AddressListPo po) {
		this.addresslistDAO.saveOrUpdate(po);
	}	
}
