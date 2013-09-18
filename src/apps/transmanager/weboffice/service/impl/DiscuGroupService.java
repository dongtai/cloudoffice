package apps.transmanager.weboffice.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IDepartmentDAO;
import apps.transmanager.weboffice.dao.IDiscuGroupDAO;
import apps.transmanager.weboffice.dao.IDiscuGroupMemberDAO;
import apps.transmanager.weboffice.dao.IGroupSessionMegDAO;
import apps.transmanager.weboffice.dao.IGroupSessionMegReadDAO;
import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPo;
import apps.transmanager.weboffice.domain.DiscuGroupPo;
import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;
import apps.transmanager.weboffice.service.IDiscuGroupService;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.SQLUtil;
import apps.transmanager.weboffice.util.server.WebTools;

@Component
public class DiscuGroupService implements IDiscuGroupService {

	public static final String NAME = "discuGroupService";
	@Autowired
	private IDepartmentDAO departmentDAO;
	@Autowired
	private IDiscuGroupMemberDAO discuGMDAO;
	@Autowired
	private IDiscuGroupDAO discuGroupDAO;
	@Autowired
	private IGroupSessionMegDAO groupSessionMegDAO;
	@Autowired
	private IGroupSessionMegReadDAO groupSessionMegReadDAO;
	@Autowired
	private IOnlineDAO onlineDAO;
	@Autowired
	private StructureDAO structureDAO;
	@Autowired
	private IUserDAO userDAO;
	@Override
	public void addDiscuGMList(DiscuGroupPo discuG, List<Users> userList) {
		if (userList == null || userList.isEmpty()) {
			return;
		}
		for (Users user : userList) {
			DiscuGroupMemberPo discGM = new DiscuGroupMemberPo();
			discGM.setOwnerId(discuG.getOwnerId());
			discGM.setGroupId(discuG.getId());
			discGM.setMemberId(user.getId());
			discGM.setMemberName(user.getRealName());
			discGM.setCreateDate(new Date());
			String userImg = user.getImage1();
			if (userImg != null && userImg.indexOf("/") != -1) {
				userImg = userImg.substring(userImg.lastIndexOf("/") + 1,
						userImg.length());
			}
			discGM.setImage(userImg);
			discuGMDAO.saveOrUpdate(discGM);
		}
	}
	
	@Override
	public Long addDiscuGroup(String gName, Users owner, List<Users> userList) {
		if (userList == null) {
			userList = new ArrayList<Users>();
		}
		userList.add(0, owner);
		DiscuGroupPo discuG = discuGroupDAO.findByNameAndOwner(gName, owner.getId());
		if (discuG != null) {
			return (long) PageConstant.VALIDATOR_NAME_DUP;
		}
		discuG = new DiscuGroupPo();
		discuG.setName(gName);
		discuG.setOwnerId(owner.getId());
		discuG.setCreateDate(new Date());
		discuGroupDAO.saveOrUpdate(discuG);
		addDiscuGMList(discuG, userList);
		// return Constant.VALIDATOR_NAME_SUC;
		return discuG.getId();
	}
	public void delDiscuGM(Long groupId, Long userId) {
		discuGMDAO.delete(groupId, userId);
	}

	public int delDiscuGroup(Long id) {
		discuGMDAO.deleteByProperty(DiscuGroupMemberPo.class.getName(),
				"groupId", id);
		discuGroupDAO.deleteById(DiscuGroupPo.class.getName(), id);
		groupSessionMegDAO.deleteByProperty(GroupSessionMegPo.class.getName(), "groupId", id);
		groupSessionMegReadDAO.deleteByProperty(GroupSessionMegReadPo.class.getName(), "groupId", id);
		return 1;
	}

	public int editDiscuGroup(Long gId, String gName, Users owner, List<Users> userList) {
		if (userList == null)
			userList = new ArrayList<Users>();
		userList.add(0, owner);
		DiscuGroupPo discuGroup = discuGroupDAO.findByNameAndOwner(gName, owner.getId());
		// 除去其本身
		if (null != discuGroup && discuGroup.getId().longValue() != gId.longValue()) {
			return PageConstant.VALIDATOR_NAME_DUP;
		}
		DiscuGroupPo discuG = discuGroupDAO.findById(DiscuGroupPo.class.getName(), gId);
		discuG.setName(gName);
		discuGroupDAO.saveOrUpdate(discuG);
		// 先删除原组员，然后添加
		discuGMDAO.deleteByProperty(DiscuGroupMemberPo.class.getName(), "groupId", discuG.getId());
		addDiscuGMList(discuG, userList);
		return PageConstant.VALIDATOR_NAME_SUC;
	}

	private List<DiscuGroupPo> getDepDiscuGList(Long userId) {
		List<DiscuGroupPo> depGList = new ArrayList<DiscuGroupPo>();
		List<Organizations> organizations = departmentDAO.findOrgByUserId(userId);
		List<Long> idList = new ArrayList<Long>();
		for (Organizations o : organizations) {
			idList.add(o.getId());
			DiscuGroupPo p = new DiscuGroupPo();
			p.setId(o.getId() * (-1)); // 避免和讨论组ID冲突
			p.setName(o.getName());
			p.setOwnerId(0L); // 避免和用户ID冲突
			depGList.add(p);
			String parentKey = o.getParentKey(); // 添加父级部门
			if (parentKey != null) {
				String[] pIds = parentKey.split("-");
				for (int i = 0; i < pIds.length; i++) {
					if (pIds[i] != null) {
						if (idList.contains(Long.valueOf(pIds[i]))) {
							continue;
						}
						DepartmentPo d = (DepartmentPo) departmentDAO
								.findById(DepartmentPo.class.getName(),
										Long.valueOf(pIds[i]));
						DiscuGroupPo pd = new DiscuGroupPo();
						pd.setId(d.getId() * (-1)); // 避免和讨论组ID冲突
						pd.setName(d.getName());
						pd.setOwnerId(d.getCompany().getId() * (-1)); // 避免和用户ID冲突
						depGList.add(pd);
					}
				}
			}
		}
		return depGList;
	}

	@Override
	public String getDiscuGMailStr(Long gId) {
		List<Users> users = discuGMDAO.findGroupUser(gId);
		if (users != null) {
			StringBuffer sb = new StringBuffer();
			for (Users t : users) {
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

	public Map<String, Object> getDiscuGMList(Long groupId, Boolean self) {
		Map<String, Object> discuGMMap = new HashMap<String, Object>();
		List<DiscuGroupMemberPo> discuGMList = new ArrayList<DiscuGroupMemberPo>();
		if (groupId >= 0) {
			if (self != null && self) {
				discuGMList = discuGMDAO.findByProperty(
						DiscuGroupMemberPo.class.getName(), "groupId", groupId);
			} else {
				discuGMList = discuGMDAO.findNoOwner(groupId);
			}
		} else {
			List<Users> users = structureDAO.findUsersByOrgId(groupId * (-1), true);
			for (Users user : users) {
				DiscuGroupMemberPo dgmp = new DiscuGroupMemberPo();
				dgmp.setId(user.getId());
				dgmp.setGroupId(groupId);
				dgmp.setImage(user.getImage1());
				dgmp.setMemberId(user.getId());
				dgmp.setMemberName(user.getRealName());
				dgmp.setOwnerId(groupId);
				discuGMList.add(dgmp);
			}
		}
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		discuGMMap.put("discuGMList", discuGMList);
		discuGMMap.put(PageConstant.LG_ONLINE_ID_LIST, onlineIdList);
		return discuGMMap;
	}

	@Override
	public List<Map<String, Object>> getDiscuGMNodeList(Long groupId) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> discuGMMap = getDiscuGMList(groupId, true);
			if (discuGMMap != null) {
				List<DiscuGroupMemberPo> discuGMList = (List<DiscuGroupMemberPo>) discuGMMap
						.get("discuGMList");
				if (discuGMList != null && !discuGMList.isEmpty())
					discuGMList = WebTools.sortUsers(discuGMList, "MemberName", true);
				List<Long> onlineIdList = (List<Long>) discuGMMap
						.get(PageConstant.LG_ONLINE_ID_LIST);
				nodeList = TreeUtil.convertDiscuGMList(discuGMList, onlineIdList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	@Override
	public DiscuGroupPo getDiscuGroupById(Long id) {
		DiscuGroupPo discu = null;
		if (id >= 0) {
			discu = discuGroupDAO.findById(DiscuGroupPo.class.getName(), id);
		} else {
			DepartmentPo d = departmentDAO.findById(DepartmentPo.class.getName(), id * (-1));
			discu = new DiscuGroupPo();
			discu.setId(id);
			discu.setName(d.getName());
			discu.setOwnerId(d.getCompany().getId() * (-1)); // 避免和用户ID冲突
		}
		return discu;
	}

	/**
	 * 获得用户的讨论组的集合，先获得用户创建的讨论组，再获得用户参与的讨论组，如果是企业用户，则添加部门讨论组
	 * 
	 * @param id
	 *            用户ID
	 * @param isStandaloneUser
	 *            是否是散户
	 * @return 讨论组集合
	 */
	@Override
	public List<DiscuGroupPo> getDiscuGroupList(Long id, boolean isStandaloneUser) {
		List<DiscuGroupPo> discuGroupList = new ArrayList<DiscuGroupPo>();
		List<DiscuGroupPo> ownerDiscuGList = discuGroupDAO.findByOwner(id);
		List<DiscuGroupPo> joinedDiscuGList = discuGroupDAO.findByMbId(id);
		discuGroupList.addAll(ownerDiscuGList);
		discuGroupList.addAll(joinedDiscuGList);
		// 如果是企业用户，则添加部门讨论组，如不需要部门讨论组直接注释掉即可
		if (!isStandaloneUser) {
			discuGroupList.addAll(getDepDiscuGList(id));
		}
		return discuGroupList;
	}
	
	/**
	 * 根据讨论组ID获得讨论组节点,用于获取单个讨论组信息
	 */
	@Override
	public Map<String, Object> getDiscuGroupNode(Long gId) {
		DiscuGroupPo discu = getDiscuGroupById(gId);
		if (null == discu) {
			return null;
		}
		return TreeUtil.convertDiscuG(discu);
	}
	
	@Override
	public List<Map<String, Object>> getDiscuGroupNodeList(Users user) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		try {
			boolean isStandaloneUser = user.getCompany().getId() == 1 ? true : false;
			List<DiscuGroupPo> discuGroupList = getDiscuGroupList(user.getId(), isStandaloneUser);
			if (discuGroupList != null && !discuGroupList.isEmpty())
				nodeList = TreeUtil.convertDiscuGList(discuGroupList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	/**
	 * 获得用户信息
	 * 
	 * @param userId
	 * @return
	 */
	private Users getUsersById(Long userId) {
		return userDAO.findById(Users.class.getName(), userId);
	}

	@Override
	public List<String[]> searchDiscuGList(Long userId, String key, boolean b) {
		List<DiscuGroupPo> discuGList = getDiscuGroupList(userId, b);
		if (discuGList == null || discuGList.isEmpty()) {
			return null;
		}
		List<String[]> resultList = new ArrayList<String[]>();
		for (DiscuGroupPo dg : discuGList) {
			if (dg.getName().indexOf(key) != -1) {
				String[] dgStr = new String[2];
				String value = "" + dg.getId();
				String name = dg.getName();
				dgStr[0] = name;
				dgStr[1] = value;
				resultList.add(dgStr);
			}
		}
		return resultList;
	}
	
	@Override
	public List<Map<String, String>> searchGroupsByKey(String keyWord, Long userId) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		List<DiscuGroupPo> discuGroupPos = this.discuGroupDAO
				.searchDiscuGroupPoByKey(SQLUtil.stringEscape(keyWord), new String[] { "name" }, userId);
		if (discuGroupPos == null || discuGroupPos.isEmpty()) {
			return null;
		}
		for (DiscuGroupPo po : discuGroupPos) {
			Users user = this.getUsersById(po.getOwnerId());
			if (user == null) {
				continue;
			}
			Map<String, String> m = new HashMap<String, String>();
			m.put("groupName", po.getName());
			m.put("createTime", new SimpleDateFormat("yyyy-MM-dd").format(po
					.getCreateDate()));
			m.put("ownerName", user.getRealName());
			m.put("groupId", po.getId().toString());
			m.put("ownerId", user.getId().toString());
			data.add(m);
		}
		return data;
	}
}
