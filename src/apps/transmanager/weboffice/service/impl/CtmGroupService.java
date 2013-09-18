package apps.transmanager.weboffice.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IAddressListDAO;
import apps.transmanager.weboffice.dao.ICtmGroupMemberDAO;
import apps.transmanager.weboffice.dao.ICustomGroupDAO;
import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.OnlinePo;
import apps.transmanager.weboffice.service.ICtmGroupService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.server.WebTools;

@Component
public class CtmGroupService implements ICtmGroupService {

	public static final String NAME = "ctmGroupService";
	@Autowired
	private IAddressListDAO addresslistDAO;
	@Autowired
	private ICtmGroupMemberDAO ctmGroupMemberDAO;
	public ICtmGroupMemberDAO getCtmGroupMemberDAO() {
		return ctmGroupMemberDAO;
	}

	@Autowired
	private ICustomGroupDAO customGroupDAO;
	@Autowired
	private IOnlineDAO onlineDAO;
	@Autowired
	private IUserDAO userDAO;

	@Override
	public int addCtmG(CustomGroupPo ctmG, List<Users> userList) {
		boolean isExist = isCtmGExist(ctmG);
		if (isExist)
			return PageConstant.VALIDATOR_NAME_DUP;
		customGroupDAO.saveOrUpdate(ctmG);
		return addCtmGMList(ctmG, userList);
	}

	@Override
	public int addCtmG(Long ownerId, String ctmGName, List<Users> userList) {
		CustomGroupPo ctmG = new CustomGroupPo();
		ctmG.setUserId(ownerId);
		ctmG.setName(ctmGName);
		ctmG.setCreateTime(new Date());
		boolean isExist = isCtmGExist(ctmG);
		if (isExist)
			return PageConstant.VALIDATOR_NAME_DUP;
		customGroupDAO.saveOrUpdate(ctmG);
		if (userList != null)
			addCtmGMList(ctmG, userList);
		return PageConstant.VALIDATOR_NAME_SUC;
	}
	
	@Override
	public int addCtmGM(Long ownerId, Long rosterId, Long gId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", rosterId);
		paramMap.put("ownerId", ownerId);
		CtmGroupMemberPo ctmGM = ctmGroupMemberDAO.findByPropertyUnique(
				CtmGroupMemberPo.class.getName(), paramMap);
		Users roster = userDAO.findById(Users.class.getName(), rosterId);
		if (ctmGM != null) { // 若不为空，则说明联系人已存在，改变分组ID即可
			ctmGM.setGroupId(gId);
			ctmGM.setMail(roster.getRealEmail());
		} else {
			String avatar = roster.getImage1();
			if (avatar != null && avatar.indexOf("/") != -1) {
				avatar = avatar.substring(avatar.lastIndexOf("/") + 1,
						avatar.length());
			}
			ctmGM = new CtmGroupMemberPo();
			ctmGM.setGroupId(gId);
			ctmGM.setOwnerId(ownerId);
			ctmGM.setUserId(roster.getId());
			ctmGM.setImage(avatar);
			ctmGM.setMail(roster.getRealEmail());
			ctmGM.setUserName(roster.getRealName());
			ctmGM.setCreateTime(new Date());
		}
		ctmGroupMemberDAO.saveOrUpdate(ctmGM);
		return 1;
	}

	@Override
	public int addCtmGMList(CustomGroupPo ctmG, List<Users> userList) {
		if (userList != null && !userList.isEmpty()) {
			for (Users roster : userList) {
				addCtmGM(ctmG.getUserId(), roster.getId(), ctmG.getId());
			}
		}
		return 1;
	}

	@Override
	public int addCtmGMListWithBean(Long gId, List<AddressBean> beanList) {
		int exitCount = 0;
		CustomGroupPo ctmGroup = customGroupDAO.findById(CustomGroupPo.class.getName(), gId);
		if (beanList != null && !beanList.isEmpty()) {
			for (AddressBean addBean : beanList) {
				if (ctmGroup.getUserId().equals(Long.parseLong(addBean.getId().split("-")[1]))) {
					continue;
				}
				CtmGroupMemberPo ctmGM = this.getCtmGM(ctmGroup.getUserId(), Long.parseLong(addBean.getId().split("-")[1]));
				if (ctmGM == null) {
					Long userId=Long.parseLong(addBean.getId().split("-")[1]);
					Users user=this.userDAO.findById(Users.class.getCanonicalName(), userId);
					ctmGM = new CtmGroupMemberPo();
					ctmGM.setGroupId(ctmGroup.getId());
					ctmGM.setOwnerId(ctmGroup.getUserId());
					ctmGM.setUserId(Long.parseLong(addBean.getId().split("-")[1])); // 内部联系人格式inner-1
					ctmGM.setUserName(addBean.getUserName());
					ctmGM.setNickName(addBean.getRealName());
					ctmGM.setRealName(addBean.getRealName());
					ctmGM.setCreateTime(new Date());
					ctmGM.setMan(true);
					
					//添加用户是否设置隐私，没有的话，添加详细内容
					if(user.getInfodef()!=null && user.getInfodef()){
						ctmGM.setMail(addBean.getRealEmail());
						ctmGM.setRealName(addBean.getRealName());
						ctmGM.setDuty(addBean.getDuty());
						ctmGM.setMobile(addBean.getMobile());
						ctmGM.setCompanyName(addBean.getCompanyName());
						ctmGM.setFax(addBean.getFax());
						ctmGM.setPhone(addBean.getPhone());
						ctmGM.setMan(addBean.getMan());
						ctmGM.setPostcode(addBean.getPostcode());
						ctmGM.setAddress(addBean.getAddress());
						ctmGM.setCompanyAddress(addBean.getCompanyAddress());
						ctmGM.setBirthday(addBean.getBirthday());
						ctmGM.setDepartment(addBean.getDepartment());
						ctmGM.setComment(addBean.getComment());
					}
					
					String userImg = user.getImage();//addBean.getImage();//联系人头像
					if (userImg != null && userImg.indexOf("/") != -1) {
						userImg = userImg.substring(
								userImg.lastIndexOf("/") + 1, userImg.length());
					}
					ctmGM.setImage(userImg);
//					int i = addCtmGM(ctmGM.getOwnerId(), ctmGM.getUserId(), ctmGM.getGroupId());
//					if (i == Constant.VALIDATOR_NAME_DUP)
//						exitCount++;
					this.ctmGroupMemberDAO.saveOrUpdate(ctmGM);
					
				} else {
					ctmGM.setGroupId(ctmGroup.getId());
					this.ctmGroupMemberDAO.saveOrUpdate(ctmGM);
					exitCount++;
				}
			}
		}
		return exitCount;
	}
	

	@Override
	public int addCtmGMOuter(AddressBean addressBean) {
		if (addressBean != null) {
		    int isExist = isEmailExist(addressBean);
			if (isExist == -1) {
				return PageConstant.VALIDATOR_NAME_DUP;
			} else if (isExist == 0) {
				AddressListPo addressListPo = new AddressListPo(
						addressBean.getGroupId(), addressBean.getOwnerId(),
						addressBean.getUserinfo(), addressBean.getUserName(),
						addressBean.getRealEmail(), addressBean.getRealName(),
						addressBean.getDuty(), addressBean.getImage(),
						addressBean.getMobile(), addressBean.getCompanyName(),
						addressBean.getFax(), addressBean.getPhone(),
						addressBean.getPostcode(), addressBean.getAddress(),
						addressBean.getCompanyAddress(), addressBean.getMan(),
						addressBean.getBirthday(), addressBean.getDepartment(),
						addressBean.getComment());
				addresslistDAO.saveOrUpdate(addressListPo);
				return PageConstant.VALIDATOR_NAME_SUC;
			} else {
				return -2;
			}
		}
		return PageConstant.VALIDATOR_NAME_FAIL;
	}

	@Override
	public void addSystemCtmG(Long ownerId, String ctmGName, boolean isSystem, boolean isDefault) {
		CustomGroupPo ctmG = new CustomGroupPo();
		ctmG.setUserId(ownerId);
		ctmG.setName(ctmGName);
		ctmG.setCreateTime(new Date());

		CustomGroupPo cg = customGroupDAO.findByOwnerAndGName(ownerId, ctmGName);
		if (null != cg) {
			ctmG = cg;
		}
		ctmG.setSystem(isSystem);
		ctmG.setDefaultOrg(isDefault);
		customGroupDAO.saveOrUpdateWithTransactional(ctmG);
	}

	@Override
	public void addSystemCtmGs(Long ownerId) {
	    addSystemCtmG(ownerId, "我的联系人", true, true);
		addSystemCtmG(ownerId, "我的好友", true, false);
		addSystemCtmG(ownerId, "我的同事", true, false);
	}
	
	@Override
	public void delCtmG(Long gId) {
		ctmGroupMemberDAO.deleteByProperty(CtmGroupMemberPo.class.getName(), "groupId", gId);
		addresslistDAO.deleteByProperty(AddressListPo.class.getName(), "groupId", gId);
		customGroupDAO.deleteById(CustomGroupPo.class.getName(), gId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int delCtmG(Long ownerId, Long gId) {
		CustomGroupPo customGroupPo = this.customGroupDAO.findById(
				"com.yozo.weboffice.domain.CustomGroupPo", gId);
		if (customGroupPo.isSystem()) {
			return 1;
		}
		CustomGroupPo ctmG = customGroupDAO.findSystemDefaultCtmG(ownerId);
		if (null != ctmG) {
			List<CtmGroupMemberPo> ctmGMList = (List<CtmGroupMemberPo>) getCtmGMByGId(
					gId).get("ctmGMList");
			for (CtmGroupMemberPo cg : ctmGMList) {
				cg.setGroupId(ctmG.getId());
			}
			// 将分组中用户移至“我的联系人”分组
			ctmGroupMemberDAO.saveOrUpdateAll(ctmGMList);
			List<AddressListPo> addressList = addresslistDAO.findByProperty(
					AddressListPo.class.getName(), "groupId", gId);
			for (AddressListPo ap : addressList) {
				ap.setGroupId(ctmG.getId());
			}
			addresslistDAO.saveOrUpdateAll(addressList);
			customGroupDAO.deleteById(CustomGroupPo.class.getName(), gId);
		}
		return 0;
	}
	
	@Override
	public void delCtmGM(Long ownerId, Long userId, Long gId) {
		ctmGroupMemberDAO.delete(getCtmGM(ownerId, userId));
	}
	
	@Override
	public void delCtmGM(Long ownerId, Long userId, Long gId, String idFix) {
		if (idFix.equalsIgnoreCase("ctmgm")) {
			this.delCtmGM(ownerId, userId, gId);
		} else if (idFix.equalsIgnoreCase("outer")) {
			addresslistDAO.deleteById(AddressListPo.class.getName(), userId);
		}
	}
	
	@Override
	public void delCtmGM(Long ownerId, String idString) {
		// idString 树节点ID保存的信息值：ctmgm-1-2,outer-1-2, 1标识在哪个组, 2标识用户id,
		// 或外部联系人真实ID。
		String idFix = idString.split("-")[0];
		if (idFix.equalsIgnoreCase("ctmgm")) {
			this.delCtmGM(ownerId, Long.parseLong(idString.split("-")[2]),
					Long.parseLong(idString.split("-")[1]));
		} else if (idFix.equalsIgnoreCase("outer")) {
			addresslistDAO.deleteById(AddressListPo.class.getName(),
					Long.parseLong(idString.split("-")[2]));
		}
	}

	@Override
	public void delCtmGMInAndOut(Long ownerId, List<String> userIdList) {
		// 联系人分两类：内部和外部，分两类删除
		// userIdList格式: inner-1-2, outer-1-2, 标识-id-分组id
		// id值inner-0的0代表联系人id，非真正的自定义联系人id,而outer-0,中的0代表外部联系人的真实id值
		for (String idStr : userIdList) {
			String idFix = idStr.split("-")[0];
			Long userId = Long.parseLong(idStr.split("-")[1]);
			if (idFix.equalsIgnoreCase("inner")) {
				Long gId = Long.parseLong(idStr.split("-")[2]);
				this.delCtmGM(ownerId, userId, gId);
			} else if (idFix.equalsIgnoreCase("outer")) {
				addresslistDAO.deleteById(AddressListPo.class.getName(), userId);
			}
		}
	}

	@Override
	public void delCtmGMs(Long ownerId, List<String> uidList) {
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < uidList.size(); i++) {
			Long realId = this.getCtmGM(ownerId, Long.parseLong(uidList.get(i))).getId();
			idList.add(realId);
		}
		if ((idList != null) && (idList.size() != 0)) {
			ctmGroupMemberDAO.deleteByIdList(CtmGroupMemberPo.class.getName(), idList);
		}
	}

	@Override
	public int editCtmG(CustomGroupPo ctmG, List<Users> userList) {
		boolean isExist = isCtmGExistExceptSelf(ctmG);
		if (isExist)
			return PageConstant.VALIDATOR_NAME_DUP;
		customGroupDAO.updateGName(ctmG.getId(), ctmG.getName());
		addCtmGMList(ctmG, userList);
		return PageConstant.VALIDATOR_NAME_SUC;
	}
	
	@Override
	public int editCtmG(Long ownerId, Long gId, String ctmGName,
			List<Users> userList) {
		CustomGroupPo ctmG = new CustomGroupPo();
		ctmG.setUserId(ownerId);
		ctmG.setId(gId);
		ctmG.setName(ctmGName);
		boolean isExist = isCtmGExistExceptSelf(ctmG);
		if (isExist)
			return PageConstant.VALIDATOR_NAME_DUP;
		customGroupDAO.updateGName(gId, ctmGName);
		if (userList != null) {
			ctmGroupMemberDAO.deleteByProperty(CtmGroupMemberPo.class.getName(), "groupId", gId);
			addCtmGMList(ctmG, userList);
		}
		return PageConstant.VALIDATOR_NAME_SUC;
	}
	
	@Override
	public Map<String, List<CtmGroupMemberPo>> getAllCtmGM(Long ownerId) {
		Map<String, List<CtmGroupMemberPo>> map = new HashMap<String, List<CtmGroupMemberPo>>();
		List<CtmGroupMemberPo> ctmGMList = ctmGroupMemberDAO.findByProperty(
				CtmGroupMemberPo.class.getName(), "ownerId", ownerId);
		List<CtmGroupMemberPo> onlineMember = new ArrayList<CtmGroupMemberPo>();
		List<CtmGroupMemberPo> offlineMember = new ArrayList<CtmGroupMemberPo>();
		for (CtmGroupMemberPo cmp : ctmGMList) {
			OnlinePo po = this.onlineDAO.findByPropertyUnique(OnlinePo.class.getCanonicalName(), "userId", cmp.getUserId());
			if (null != po) {
				onlineMember.add(cmp);
			} else {
				offlineMember.add(cmp);
			}
		}
		map.put("onlineMbL", onlineMember);
		map.put("offlineMbL", offlineMember);
		return map;
	}
	
	/**
	 * 获取当前用户的所有联系人
	 * @param ownerId
	 *        当前用户的Id
	 * @return
	 */
	public List<CtmGroupMemberPo> getAllCtmGMByOwnerId(Long ownerId) {
		return ctmGroupMemberDAO.findByProperty(CtmGroupMemberPo.class.getName(), "ownerId", ownerId);
	}
	
	@Override
	public List<AddressBean> getAllCtmGMBean(Long ownerId) {
		List<CtmGroupMemberPo> ctmGMList = (List<CtmGroupMemberPo>) ctmGroupMemberDAO.findByProperty(
				CtmGroupMemberPo.class.getName(), "ownerId", ownerId);
		List<AddressListPo> addressList = addresslistDAO.findByProperty(
				AddressListPo.class.getName(), "ownerId", ownerId);
		List<AddressBean> abList = new ArrayList<AddressBean>();
		for(CtmGroupMemberPo cmp : ctmGMList) {
			if(null != cmp.getMail() && null != cmp.getUserName()) {
				AddressBean ab = new AddressBean();
				ab.setRealEmail(cmp.getMail());
				ab.setRealName(cmp.getUserName());
				abList.add(ab);
			}
		}
		for(AddressListPo ap : addressList) {
			if(null != ap.getRealEmail() && null != ap.getRealName()) {
				AddressBean ab = new AddressBean();
				ab.setRealEmail(ap.getRealEmail());
				ab.setRealName(ap.getRealName());
				abList.add(ab);
			}
		}
		return abList;
	}
	
	@Override
	public List<Map<String, Object>> getAllCtmGMList(Long ownerId) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		Map<String, List<CtmGroupMemberPo>> map = getAllCtmGM(ownerId);
		List<CtmGroupMemberPo> onlineMbL = map.get("onlineMbL");
		List<CtmGroupMemberPo> offlineMbL = map.get("offlineMbL");
		for (CtmGroupMemberPo po : onlineMbL) {
			mapList.add(rosterData(po.getUserId(), po.getImage(),
					po.getUserName(), true, null));
		}
		for (CtmGroupMemberPo po : offlineMbL) {
			mapList.add(rosterData(po.getUserId(), po.getImage(),
					po.getUserName(), false, null));
		}
		return mapList;
	}

	@Override
	public List<Users> getAllCtmGMUsersList(Long ownerId) {
		return this.ctmGroupMemberDAO.findRostersByOwner(ownerId);
	}

	/**
	 * 获得用户头像
	 * 
	 * @param avatar
	 * @return
	 */
	private String getAvatarSrc(String avatar) {
		if (null == avatar || avatar.equals("")) {
			avatar = WebConfig.userPortrait + "image.jpg";
		} else if (!avatar.contains("/")) {
			avatar = WebConfig.userPortrait + avatar;
		}
		return avatar;
	}
	
	@Override
	public CustomGroupPo getCtmG(Long ownerId, String gName) {
		return customGroupDAO.findByOwnerAndGName(ownerId, gName);
	}

	@Override
	public List<CustomGroupPo> getCtmGList(Long ownerId) {
		try
		{
			if (getSystemDefaultCtmG(ownerId) == null) {
			    addSystemCtmGs(ownerId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return customGroupDAO.findByOwner(ownerId);
	}

	@Override
	public CtmGroupMemberPo getCtmGM(Long ownerId, Long rosterId) {
		return ctmGroupMemberDAO.findRosterByOwner(ownerId, rosterId);
	}
	
	@Override
	@Deprecated
	public CtmGroupMemberPo getCtmGM(Long ownerId, Long rosterId, Long gId) {
		return ctmGroupMemberDAO.findRosterByOwner(ownerId, rosterId,gId);
	}

	@Override
	public Map<String, Object> getCtmGMByGId(Long gId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CtmGroupMemberPo> ctmGMList = ctmGroupMemberDAO.findByProperty(CtmGroupMemberPo.class.getName(), "groupId", gId);
		List<AddressListPo> ctmGMListOuter = addresslistDAO.findByProperty(AddressListPo.class.getName(), "groupId", gId);
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		map.put("ctmGMList", ctmGMList);
		map.put("ctmGMListOuter",ctmGMListOuter);
		map.put("onlineIdList", onlineIdList);
		return map;
	}

	@Override
	public List<Long> getCtmGMIdByGId(Long gId) {
		return ctmGroupMemberDAO.findByGroup(gId);
	}

	@Override
	public List<Users> getCtmGMInByGId(Long gId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtmGroupMemberPo> getCtmGMOnlineList(Long ownerId) {
		return this.getAllCtmGM(ownerId).get("onlineMbL");
	}

	@Override
	public List<AddressListPo> getCtmGMOutByGId(Long gId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获得在线联系人
	 * 
	 * @param ownerId
	 * @return
	 */
	private Map<String, List<CtmGroupMemberPo>> getCtmGMUsersOnline(Long ownerId) {
		Map<String, List<CtmGroupMemberPo>> map = new HashMap<String, List<CtmGroupMemberPo>>();
		List<CtmGroupMemberPo> onlineMember = getCtmGMOnlineList(ownerId);
		map.put("onlineMember", onlineMember);
		return map;
	}

	@Override
	public List<Map<String, Object>> getCtmGNodeList(Long ownerId, String uId) {
		List<Map<String, Object>> ctmGNodeMap = new ArrayList<Map<String, Object>>();
		List<CustomGroupPo> ctmGList = getCtmGList(ownerId);
		Long gId = getUserCtmGId(Long.valueOf(uId), ownerId);
		if (ctmGList != null && !ctmGList.isEmpty())
			ctmGNodeMap = TreeUtil.convertCtmGList(ctmGList, gId, "ctmg");
		return ctmGNodeMap;
	}

	@Override
	public List<Map<String, Object>> getCtmGOrMNode(Long userId, String pId, String idFix) {
		List<Map<String, Object>> ctmGOrMNodeMap = new ArrayList<Map<String, Object>>();
		if ("0".equals(pId.split("-")[1])) {
			List<CustomGroupPo> ctmGList = getCtmGList(userId);
			if (ctmGList != null && !ctmGList.isEmpty()) {
				ctmGOrMNodeMap = TreeUtil.convertCtmGList(ctmGList, "ctmg");
			}
		} else if ("online".equals(pId.split("-")[1])) {
			List<CtmGroupMemberPo> onlineGMList = (List<CtmGroupMemberPo>) getCtmGMUsersOnline(userId).get("onlineMember");
			onlineGMList = WebTools.sortUsers(onlineGMList, "userName", true);
			ctmGOrMNodeMap = TreeUtil.convertCtmGMList(onlineGMList, null, idFix);
		} else {
			Long gId = Long.parseLong(pId.split("-")[1]);
			Map<String, Object> ctmGMMap = getCtmGMByGId(gId);
			List<CtmGroupMemberPo> ctmGMList = (List<CtmGroupMemberPo>) ctmGMMap.get("ctmGMList");
			ctmGMList = WebTools.sortUsers(ctmGMList, "userName", true);
			List<Long> onlineIdList = (List<Long>) ctmGMMap.get(PageConstant.LG_ONLINE_ID_LIST);
			ctmGOrMNodeMap = TreeUtil.convertCtmGMList(ctmGMList, onlineIdList, idFix); //ctmgm
		}
		return ctmGOrMNodeMap;
	}

	@Override
	public List<CtmGroupMemberPo> getOnlineCtmGMPoList(Long userId) {
		return (List<CtmGroupMemberPo>) this.getCtmGMUsersOnline(userId).get(
				"onlineMember");
	}

	@Override
	public CustomGroupPo getSystemDefaultCtmG(Long ownerId) {
		return customGroupDAO.findSystemDefaultCtmG(ownerId);
	}

	@Override
	public Long getUserCtmGId(Long userId, Long ownerId) {
		CtmGroupMemberPo ctmGM = getCtmGM(ownerId, userId);
		return ctmGM == null ? null : ctmGM.getGroupId();
	}

	@Override
	public boolean isCtmGExist(CustomGroupPo ctmG) {
		CustomGroupPo ctmGroup = getCtmG(ctmG.getUserId(), ctmG.getName());
		return ctmGroup != null ? true : false;
	}

	@Override
	public boolean isCtmGExistExceptSelf(CustomGroupPo ctmG) {
		Long gId = ctmG.getId();
		CustomGroupPo ctmGroup = getCtmG(ctmG.getUserId(), ctmG.getName());
		if (ctmGroup != null && ctmGroup.getId().longValue() != gId.longValue())
			return true;
		return false;
	}

	/**
	 * 联系人中是否已存在相同邮箱帐户
	 * @param addressBean
	 * @return 0:不存在 -1:联系人中已存在 -2:公司部门中存在
	 */
	private int isEmailExist(AddressBean addressBean) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("realEmail", addressBean.getRealEmail());
		paramMap.put("ownerId", addressBean.getOwnerId());
		// paramMap.put("groupId", addressBean.getGroupId());
//		List<AddressListPo> addressPos = addresslistDAO.findByProperty(
//				AddressListPo.class.getName(), paramMap);
//		if (addressPos != null && addressPos.size() != 0) {
//			return -1;
//		}
		//不需要判断邮件是否重复——孙爱华
//		if (!addressBean.getUserinfo().getCompany().getId().equals(1)) {
//			Map<String, Object> paramMap2 = new HashMap<String, Object>();
//			paramMap2.put("realEmail", addressBean.getRealEmail());
//			paramMap2.put("company", addressBean.getUserinfo().getCompany());
//			List<Users> userList = userDAO.findByProperty(Users.class.getName(), paramMap2);
//			if (userList != null && userList.size() != 0) {
//				return -2;
//			}
//		}
		return 0;
	}

	@Override
	public boolean isRosterExist(Long ownerId, Long memberId) {
		CtmGroupMemberPo roster = this.ctmGroupMemberDAO.findRosterByOwner(ownerId, memberId);
		return null != roster;
	}
	
	private Map<String, Object> rosterData(Long id, String avatar, String name,
			Boolean isOnline, String msg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rosterId", id);
		data.put("avatar", this.getAvatarSrc(avatar));
		data.put("rosterName", name);
		data.put("isOnline", isOnline);
		data.put("msg", msg);
		return data;
	}
	
	@Override
	public List<String[]> searchCtmGMList(Long ownerId, String key) {
		List<CtmGroupMemberPo> ctmGMList = searchCtmGMList(key, ownerId);
		if (ctmGMList == null || ctmGMList.isEmpty()) {
			return null;
		}
		List<String[]> resultList = new ArrayList<String[]>();
		for (CtmGroupMemberPo ctmM : ctmGMList) {
			String[] ctmMStr = new String[2];
			String value = "ctmg-" + ctmM.getGroupId() + "-" + ctmM.getUserId();
			String name = ctmM.getUserName();
			ctmMStr[0] = name;
			ctmMStr[1] = value;
			resultList.add(ctmMStr);
		}
		return resultList;
	}
	
	@Override
	public List<CtmGroupMemberPo> searchCtmGMList(String key, Long ownerId) {
		return ctmGroupMemberDAO.searchByKey(key, ownerId);
	}

	@Override
	public CtmGroupMemberPo getCtmGMById(Long id) {
		return this.ctmGroupMemberDAO.findById(CtmGroupMemberPo.class.getCanonicalName(), id);
	}
	
	
}
