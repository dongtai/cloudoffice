package apps.transmanager.weboffice.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.ICtmGroupMemberDAO;
import apps.transmanager.weboffice.dao.IDepMemberDAO;
import apps.transmanager.weboffice.dao.IDepartmentDAO;
import apps.transmanager.weboffice.dao.IDiscuGroupDAO;
import apps.transmanager.weboffice.dao.IDiscuGroupMemberDAO;
import apps.transmanager.weboffice.dao.IDiscuGroupMemberPoRelationDao;
import apps.transmanager.weboffice.dao.IGroupSessionMegDAO;
import apps.transmanager.weboffice.dao.IGroupSessionMegReadDAO;
import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.dao.ISessionMegDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.dao.IValidateSessionMegDAO;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPo;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPoRelationPo;
import apps.transmanager.weboffice.domain.DiscuGroupPo;
import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;
import apps.transmanager.weboffice.domain.OnlinePo;
import apps.transmanager.weboffice.domain.SessionMegPo;
import apps.transmanager.weboffice.domain.ValidateSessionMegPo;
import apps.transmanager.weboffice.service.ICtmGroupService;
import apps.transmanager.weboffice.service.IDiscuGroupService;
import apps.transmanager.weboffice.service.ITalkService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.DateUtils;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;
import apps.transmanager.weboffice.util.beans.SQLUtil;
import apps.transmanager.weboffice.util.server.WebTools;

@Component
public class TalkService implements ITalkService {

	public static final String NAME = "talkService";

	@Autowired
	private ICtmGroupMemberDAO ctmGroupMemberDAO;
	@Autowired
	private ICtmGroupService ctmGroupService;
	@Autowired
	private IDepartmentDAO departmentDAO;
	@Autowired
	private IDepMemberDAO depMemberDAO;
	@Autowired
	private IDiscuGroupMemberDAO discuGMDAO;
	@Autowired
	private IDiscuGroupDAO discuGroupDAO;
	@Autowired
	private IDiscuGroupService discuGroupService;
	@Autowired
	private IGroupSessionMegDAO groupSessionMegDAO;
	@Autowired
	private IGroupSessionMegReadDAO groupSessionMegReadDAO;
	@Autowired
	private MessagesService messagesService;
	@Autowired
	private IOnlineDAO onlineDAO;
	@Autowired
	private ISessionMegDAO sessionMegDAO;
	@Autowired
	private IUserDAO userDAO;
	
	@Autowired
	private IDiscuGroupMemberPoRelationDao discuGroupMemberPoRelationDao;
	/**
	 * 即时通讯用户验证信息
	 */
	@Autowired
	private IValidateSessionMegDAO validateSessionMegDAO;
	
	/**
	 * 移动端表情字符串数组
	 */
	private static String[] mobile = { "[/微笑]", "[/瘪嘴]", "[/好色]", "[/瞪眼]",
			"[/得意]", "[/流泪]", "[/害羞]", "[/闭嘴]", "[/睡觉]", "[/大哭]", "[/尴尬]",
			"[/愤怒]", "[/调皮]", "[/呲牙]", "[/惊讶]", "[/难过]", "[/装酷]", "[/冷汗]",
			"[/抓狂]", "[/呕吐]", "[/偷笑]", "[/可爱]", "[/白眼]", "[/傲慢]", "[/饥饿]",
			"[/困]", "[/恐惧]", "[/流汗]", "[/憨笑]", "[/大兵]", "[/奋斗]", "[/咒骂]",
			"[/疑问]", "[/嘘嘘]", "[/晕]", "[/折磨]", "[/衰]", "[/骷髅]", "[/敲打]",
			"[/再见]" };
	/**
	 * 网页端表情前缀
	 */
	private static String prefix = "<img src='/static/js/im/style/editor/images/default/emoticons/";
	private static String end = ".gif'>";
	private static Map<String, String> mobileToWebEmotions = null;
	private static Map<String, String> webToMobileEmotions = null;
	
	/**
	 * 表情转换
	 * 
	 * @param message
	 * @param isToMobile
	 *            true 为转成手机端表情 false为转为网页端表情
	 * @return
	 */
	private String formatEmotion(String message, Boolean isToMobile) {
		if (mobileToWebEmotions == null) {
			mobileToWebEmotions = new HashMap<String, String>();
			webToMobileEmotions = new HashMap<String, String>();
			for (int i = 0; i < mobile.length; i++) {
				mobileToWebEmotions.put(mobile[i], prefix + i + end);
				webToMobileEmotions.put(prefix + i + end, mobile[i]);
			}
		}
		String regex = null;
		Pattern pattern = null;
		Matcher matcher = null;
		StringBuffer buffer = new StringBuffer();
		if (!isToMobile) {
			regex = "\\[\\/[\u4E00-\u9FA5]*\\]";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(message);
			while (matcher.find()) {
				matcher.appendReplacement(buffer,
						mobileToWebEmotions.get(matcher.group()));
			}
		} else {
			regex = prefix + "[0-9]+" + end;
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(message);
			while (matcher.find()) {
				matcher.appendReplacement(buffer,
						webToMobileEmotions.get(matcher.group()));
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	@Override
	public void deleteAllGroupSessionMessage(Long ownerId, Long groupId) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("acceptId", ownerId);
		propertyMap.put("groupId", groupId);
		List<GroupSessionMegReadPo> groupSessionMegReadPos = this.groupSessionMegReadDAO
				.findByProperty(GroupSessionMegReadPo.class.getCanonicalName(),
						propertyMap);
		if (groupSessionMegReadPos != null && !groupSessionMegReadPos.isEmpty()) {
			for(GroupSessionMegReadPo po : groupSessionMegReadPos) {
				po.setDeleted(true);
			}
			this.groupSessionMegReadDAO.saveOrUpdateAll(groupSessionMegReadPos);
		}
	}

	@Override
	public void deleteAllPersonalSessionMessage(Long ownerId, Long userId) {
		List<SessionMegPo> sessionMegPoList = this.sessionMegDAO.findHisRecord(ownerId, userId);
		if (sessionMegPoList != null) {
			for(SessionMegPo sessionMegPo : sessionMegPoList) {
				if (sessionMegPo.getAcceptId().longValue() == ownerId.longValue()) {
					sessionMegPo.setAccepterDelete(true);
				} else if (sessionMegPo.getSendId().longValue() == ownerId.longValue()) {
					sessionMegPo.setSenderDelele(true);
				}
			}
			this.sessionMegDAO.saveOrUpdateAll(sessionMegPoList);
		}
	}
	
	@Override
	public void deleteGroupSessionMessage(Long msgId, Long userId) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("acceptId", userId);
		propertyMap.put("groupSessionMegId", msgId);

		List<GroupSessionMegReadPo> groupSessionMegReadPos = this.groupSessionMegReadDAO
				.findByProperty(GroupSessionMegReadPo.class.getCanonicalName(),
						propertyMap);
		if (groupSessionMegReadPos != null && !groupSessionMegReadPos.isEmpty()) {
			GroupSessionMegReadPo po = groupSessionMegReadPos.get(0);
			po.setDeleted(true);
			this.groupSessionMegReadDAO.saveOrUpdate(po);
		}
	}
	
	@Override
	public void deleteHisSessionMessage(int type, Long ownerId, List<Long> msgIds) {
		if (type == 1) {
			for(Long msgId : msgIds) {
				deletePersonalSessionMessage(msgId, ownerId);
			}
		} else if (type == 2) {
			for(Long msgId : msgIds) {
				deleteGroupSessionMessage(msgId, ownerId);
			}
		}
	}

	public void deletePersionHisSessionMessageByDate(Long userId, Date startDate, Date endDate, Long ownerId) {
		List<SessionMegPo> sessionMegPoList = this.sessionMegDAO.findHisRecordByDate(ownerId, userId, startDate, endDate);
		if (sessionMegPoList != null) {
			for(SessionMegPo sessionMegPo : sessionMegPoList) {
				if (sessionMegPo.getAcceptId().longValue() == ownerId.longValue()) {
					sessionMegPo.setAccepterDelete(true);
				} else if (sessionMegPo.getSendId().longValue() == ownerId.longValue()) {
					sessionMegPo.setSenderDelele(true);
				}
			}
			this.sessionMegDAO.saveOrUpdateAll(sessionMegPoList);
		}
	}
	
	public void deleteGroupHisSessionMessageByDate(Long groupId, Date startDate, Date endDate, Long ownerId) {
		List<GroupSessionMegReadPo> groupSessionMegReadPos = this.groupSessionMegReadDAO.findHisRecordByDate(groupId, ownerId, startDate, endDate);
		if (groupSessionMegReadPos != null && !groupSessionMegReadPos.isEmpty()) {
			for(GroupSessionMegReadPo po : groupSessionMegReadPos) {
				po.setDeleted(true);
			}
			this.groupSessionMegReadDAO.saveOrUpdateAll(groupSessionMegReadPos);
		}
	}
	
	@Override
	public void deleteHisSessionMessageByDate(int type, Long otherId,
			int delType, String startDate, String endDate, Long ownerId) {
		Date start = null, end = null;
		try {
			start = DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", startDate + " 0:0:0");
			switch(delType) {
			case 1:
				end = new Date(new Date().getTime() - 7*24*60*60*1000);
				break;
			case 2:
				end = new Date(new Date().getTime() - 3*30*24*60*60*1000);
				break;
			case 3:
				end = new Date(new Date().getTime() - 365*24*60*60*1000);
				break;
			case 4:
				end = DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", endDate + " 23:59:59");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (type == 1) {
			deletePersionHisSessionMessageByDate(otherId, start, end, ownerId);
		} else if (type == 2) {
			deleteGroupHisSessionMessageByDate(otherId, start, end, ownerId);
		}
	}
	
	@Override
	public void deletePersonalSessionMessage(Long msgId, Long userId) {
		SessionMegPo sessionMegPo = this.sessionMegDAO.findById(
				SessionMegPo.class.getCanonicalName(), msgId);
		if (sessionMegPo != null) {
			if (sessionMegPo.getAcceptId().longValue() == userId.longValue()) {
				sessionMegPo.setAccepterDelete(true);
			} else if (sessionMegPo.getSendId().longValue() == userId
					.longValue()) {
				sessionMegPo.setSenderDelele(true);
			}
			this.sessionMegDAO.saveOrUpdate(sessionMegPo);
		}
	}

	@Override
	public void endValidateMessage(Long vMsgId) {
		if (null != vMsgId && vMsgId > 0L) {
			ValidateSessionMegPo po = this.validateSessionMegDAO.findById(
					"com.yozo.weboffice.domain.ValidateSessionMegPo", vMsgId);
			if (null != po) {
				po.setHandle(true);
				po.setHandleDate(new Date());
				this.validateSessionMegDAO.saveOrUpdate(po);
			}
		}
	}

	/**
	 * 过滤<font></font>
	 * 
	 * @param msg
	 * @return
	 */
	private String filterMsgForMobile(String msg) {
		if (null != msg && msg.contains(">") && msg.contains(">")) {
			msg = msg.substring(msg.indexOf(">") + 1, msg.lastIndexOf("<"));
		}
		return msg;
	}

	/**
	 * 获取用户在线的个人信息和讨论组信息
	 * 
	 * @param userID
	 *            当前用户的ID
	 */
	private Map<String, Object> getAllNewMegTip(Long userId) {
		Map<String, Object> newMegTipMap = new HashMap<String, Object>();
		newMegTipMap.put("pNewMegTip", getAllOfflinePersonMessage(userId));
		newMegTipMap.put("gNewMegTip", getAllOfflineGroupMessage(userId));
		newMegTipMap.put("dNewMegTip", getUnhandleValidatSessionMegTip(userId));
		return newMegTipMap;
	}

	@Override
	public Map<String, Object> getAllNewMegTip(Long userId, boolean isMobile) {
		return isMobile ? this.getAllNewMegTipForMobile(userId) : this
				.getAllNewMegTip(userId);
	}

	/**
	 * 获取用户在线的个人信息和讨论组信息（手机端使用）
	 * 
	 * @param userId
	 *            当前用户ID
	 * @return
	 */
	private Map<String, Object> getAllNewMegTipForMobile(Long userId) {
		Map<String, Object> newMegTipMap = new HashMap<String, Object>();
		newMegTipMap
				.put("pNewMegTip", getMobileAllOfflinePersonMessage(userId));
		newMegTipMap.put("gNewMegTip", getMobileAllOfflineGroupMessage(userId));
		newMegTipMap.put("dNewMegTip", getUnhandleValidatSessionMegTip(userId));
		return newMegTipMap;
	}

	/**
	 * 获取当前用户的离线组信息
	 * 
	 * @param userId
	 *            当前用户ID
	 * @return
	 */
	private List<Map<String, Object>> getAllOfflineGroupMessage(Long userId) {
		List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
		List<Map<Long, Long>> data = (List<Map<Long, Long>>) groupSessionMegDAO
				.findAllGNewMegTip(userId);
		if (data != null && !data.isEmpty()) {
			for (Map<Long, Long> d : data) {
				Map<String, Object> da = new HashMap<String, Object>();
				Long groupId = d.get("groupId");
				da.put("count", d.get("count"));
				da.put("meg", this.groupSessionMegDAO
						.getLastUnreadGroupMessage(groupId));
				messages.add(da);
			}
		}
		return messages;
	}

	/**
	 * 获取当前用户的离线个人信息
	 * 
	 * @param userId
	 *            当前的用户ID
	 * @return
	 */
	private List<Map<String, Object>> getAllOfflinePersonMessage(Long userId) {
		List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
		List<Map<Long, Long>> data = (List<Map<Long, Long>>) sessionMegDAO
				.findAllPNewMegTip(userId);

		if (data != null && !data.isEmpty()) {
			for (Map<Long, Long> d : data) {
				Map<String, Object> da = new HashMap<String, Object>();
				Long sendId = d.get("sendId");
				da.put("count", d.get("count"));
				da.put("meg", this.sessionMegDAO.getLastestUnreadMessage(
						userId, sendId));
				Boolean isOnline = onlineDAO.findByPropertyUnique(
						OnlinePo.class.getCanonicalName(), "userId", sendId) != null;
				da.put("online", isOnline);
				messages.add(da);
			}
		}
		return messages;
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
	public List<Map<String, Object>> getCompanyTree(Users user,
			String parentID, boolean exceptSelf, String idFix) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Company company = user.getCompany();
		parentID = parentID.split("-")[1];
		Map<String, Object> groupAndMemberMap = getGroupAndMemberList(
				Long.parseLong(parentID), company, "sortNum", "asc");
		List<DepartmentPo> groupList = (List<DepartmentPo>) groupAndMemberMap
				.get("groupList");
		List<DepMemberPo> groupMemberList = (List<DepMemberPo>) groupAndMemberMap
				.get("groupMemberList");
		List<Long> onlineIdList = (List<Long>) groupAndMemberMap
				.get(PageConstant.LG_ONLINE_ID_LIST);
		if (groupList != null && !groupList.isEmpty()) {
			List<Map<String, Object>> nodeMapTemp = TreeUtil.convertDepList(
					groupList, "g");
			nodeMap.addAll(nodeMapTemp);
		}
		if (groupMemberList != null && !groupMemberList.isEmpty()) {
			groupMemberList = WebTools.sortUsers(groupMemberList, "User",
					"RealName", true);
			List<Map<String, Object>> nodeMapTemp = TreeUtil
					.convertDepMemberList(groupMemberList, onlineIdList, idFix,
							exceptSelf, user.getId());
			nodeMap.addAll(nodeMapTemp);
		}
		return nodeMap;
	}

	@Override
	public ICtmGroupService getCtmGroupService() {
		return ctmGroupService;
	}

	/**
	 * 获取当前用户在线好友 目前是获取用户的联系人
	 * 
	 * @param currentUserId
	 *            当前用户的ID
	 * @return
	 */
	private List<Long> getCurrentUserOnlineFriendsIds(Long currentUserId) {
		return this.onlineDAO.findAllOnlineUserIdsByOwnerId(currentUserId);
	}

	@Override
	public IDiscuGroupService getDiscuGroupService() {
		return discuGroupService;
	}

	public List<GroupSessionMegPo> getGroupAcceptMeg(Long groupId,
			Long acceptId, Boolean isMobile) {
		List<GroupSessionMegPo> groupSessionMeg = groupSessionMegDAO
				.findReadSessionMeg(acceptId, groupId);
		if (isMobile) {
			for (GroupSessionMegPo gmp : groupSessionMeg) {
				String message = gmp.getSessionMeg();
				if (null != message) {
					gmp.setSessionMeg(this.filterMsgForMobile(message));
				}
			}
		}
		groupSessionMegReadDAO.updateReade(groupId, acceptId);
		return groupSessionMeg;
	}

	/**
	 * 根据组的父ID获取子组List和成员List
	 * 
	 * @param parentID
	 *            父ID
	 * @param sortName
	 *            排序字段
	 * @param order
	 *            排序方式
	 * @return 子组List和成员List
	 */
	private Map<String, Object> getGroupAndMemberList(Long parentID,
			Company company, String sortName, String order) {
		Map<String, Object> groupAndMemberMap = new HashMap<String, Object>();
		List<DepartmentPo> groupList;
		if (parentID == 0L) {
			parentID = null;
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			propertyMap.put("parent.id", parentID);
			propertyMap.put("company", company);
			groupList = departmentDAO.findByProperty(
					DepartmentPo.class.getName(), propertyMap, sortName, order);
		} else {
			groupList = departmentDAO.findByProperty(
					DepartmentPo.class.getName(), "parent.id", parentID,
					sortName, order);
		}
		// findGroupsByPid(parentID,sortName,order);
		List<DepMemberPo> groupMemberList = null;
		List<Long> onlineIdList = onlineDAO.findAllUserId();
		if (parentID != null) {
			groupMemberList = depMemberDAO.findByProperty(
					DepMemberPo.class.getName(), "organization.id", parentID,
					"user." + sortName, order);
		}
		groupAndMemberMap.put("groupList", groupList);
		groupAndMemberMap.put("groupMemberList", groupMemberList);
		groupAndMemberMap.put("onlineIdList", onlineIdList);
		return groupAndMemberMap;
	}

	@Override
	public List<GroupSessionMegPo> getHisGroupSessionMeg(Page page,
			Long groupId, Long selfId) {
		List<GroupSessionMegPo> groupSessionMegList = this.groupSessionMegDAO
				.findUndeleteSessionMeg(selfId, groupId, page, "addDate",
						"desc");
		if (groupSessionMegList == null || groupSessionMegList.isEmpty()) {
			return null;
		}
		for (GroupSessionMegPo meg : groupSessionMegList) {
			// 判断，进行处理，针对自己说的话和别人说的话
			try {
				if (meg.getSendId().longValue() == selfId.longValue())
					meg.setSessionMeg("<b style='color:#42B475;font-size:12px;'>"
							+ meg.getSendName()
							+ " "
							+ DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
									meg.getAddDate())
							+ "</b><br/> "
							+ meg.getSessionMeg() + "<br/>");
				else
					meg.setSessionMeg("<b style='color:#006EFE;font-size:12px;'>"
							+ meg.getSendName()
							+ " "
							+ DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
									meg.getAddDate())
							+ "</b><br/> "
							+ meg.getSessionMeg() + "<br/>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		java.util.Collections.reverse(groupSessionMegList);
		return groupSessionMegList;
	}

	public int getHisGroupSessionMegCount(Long userId, Long groupId) {
		long count = groupSessionMegDAO.findUndeleteSessionMegCount(userId,
				groupId);
		return (int) count;
	}

	@Override
	public List<SessionMegPo> getHisSessionMeg(Page page, Long ownerId,
			Long otherId) {
		List<SessionMegPo> sessionMegList = sessionMegDAO.findHisRecord(
				ownerId, otherId, page);

		if (sessionMegList == null || sessionMegList.isEmpty()) {
			return null;
		}
		for (SessionMegPo meg : sessionMegList) {
			// 判断，进行处理，针对自己说的话和别人说的话
			try {
				if (meg.getSendId().longValue() == ownerId.longValue())
					meg.setSessionMeg("<b style='color:#42B475;font-size:12px;'>"
							+ meg.getSendName()
							+ " "
							+ DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
									meg.getAddDate())
							+ "</b><br/><div style='white-space: pre-wrap;'>"
							+ meg.getSessionMeg() + "</div><br/>");
				else
					meg.setSessionMeg("<b style='color:#006EFE;font-size:12px;'>"
							+ meg.getSendName()
							+ " "
							+ DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
									meg.getAddDate())
							+ "</b><br/><div style='white-space: pre-wrap;'>"
							+ meg.getSessionMeg() + "</div><br/>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		java.util.Collections.reverse(sessionMegList);
		return sessionMegList;

	}

	@Override
	public int getHisSessionMegCount(Long ownerId, Long otherId) {
		long total = sessionMegDAO.findHisRecordCount(ownerId, otherId);
		return (int) total;
	}

	/**
	 * 获取当前用户的离线组信息(手机端)
	 * 
	 * @param userId
	 *            当前用户ID
	 * @return
	 */
	private List<Map<String, Object>> getMobileAllOfflineGroupMessage(
			Long userId) {
		List<Map<String, Object>> gNewMegTip = this
				.getAllOfflineGroupMessage(userId);
		// 去除组信息中<font ***> </font>
		for (Map<String, Object> gNewMeg : gNewMegTip) {
			for (String count : gNewMeg.keySet()) {
				if (gNewMeg.get(count) instanceof GroupSessionMegPo) {
					GroupSessionMegPo groupSessionMegPo = (GroupSessionMegPo) (gNewMeg
							.get(count));
					String message = groupSessionMegPo.getSessionMeg();
					if (null != message) {
						groupSessionMegPo.setSessionMeg(this
								.filterMsgForMobile(message));
					}

				}
			}
		}
		return gNewMegTip;
	}

	/**
	 * 获取当前用户的离线个人信息(手机端)
	 * 
	 * @param userId
	 *            当前的用户ID
	 * @return
	 */
	private List<Map<String, Object>> getMobileAllOfflinePersonMessage(
			Long userId) {
		List<Map<String, Object>> pNewMegTip = this
				.getAllOfflinePersonMessage(userId);
		// 去除个人信息中<font ***> </font>
		for (Map<String, Object> pNewMeg : pNewMegTip) {
			for (String count : pNewMeg.keySet()) {
				if (pNewMeg.get(count) instanceof SessionMegPo) {
					SessionMegPo sessionMegPo = (SessionMegPo) (pNewMeg
							.get(count));
					String message = sessionMegPo.getSessionMeg();
					if (null != message) {
						sessionMegPo.setSessionMeg(this
								.filterMsgForMobile(message));
					}
				}
			}
		}
		return pNewMegTip;
	}

	private Map<String, Object> getNoticeValidateMessage(Long VMsgId,
			ValidateSessionMegPo validateSessionMegPo, Users user) {
		// 通知客户
		String groupName = "";

		if (validateSessionMegPo.getCategory() == 2) {
			DiscuGroupPo discuGroupPo = this.discuGroupDAO.findById(
					DiscuGroupPo.class.getCanonicalName(),
					validateSessionMegPo.getGroupId());
			groupName = discuGroupPo.getName();
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(
				"wId",
				validateSessionMegPo.getAcceptId() + "-"
						+ validateSessionMegPo.getSendId() + "-"
						+ validateSessionMegPo.getGroupId());
		data.put("sessionMeg", validateSessionMegPo.getSessionMeg());
		data.put("icon", "");
		data.put("vMsgId", VMsgId);
		data.put("category", validateSessionMegPo.getCategory());
		data.put("senderName", validateSessionMegPo.getSendName());
		data.put("groupName", groupName);
		data.put("image", this.getAvatarSrc(user.getImage1()));
		if (validateSessionMegPo.getCategory() == 1) {
			if (this.ctmGroupService.isRosterExist(
					validateSessionMegPo.getAcceptId(),
					validateSessionMegPo.getSendId())) {
				data.put("isUserExist", true);
			} else {
				data.put("isUserExist", false);
			}
		}
		return data;
	}

	@Override
	public List<SessionMegPo> getPersonNewMeg(Long sendId, Long acceptId,
			Boolean isMobile) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("sendId", sendId);
		propertyMap.put("acceptId", acceptId);
		propertyMap.put("readed", false);
		List<SessionMegPo> megList = sessionMegDAO.findByProperty(
				SessionMegPo.class.getName(), propertyMap);
		if (isMobile) {
			for (SessionMegPo smp : megList) {
				String message = smp.getSessionMeg();
				if (null != message) {
					smp.setSessionMeg(this.filterMsgForMobile(message));
				}
			}
		}
		List<String> columNames = new ArrayList<String>();
		columNames.add("readed");
		List<String> conditions = new ArrayList<String>();
		conditions.add("sendId");
		conditions.add("acceptId");
		propertyMap.put("readed", true);
		sessionMegDAO.update(SessionMegPo.class.getName(), columNames,
				conditions, propertyMap);
		return megList;
	}

	@Override
	public List<Map<String, Object>> getRecentCtmGM(Long ownerId, int size) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		List<Map<Long, Long>> map = this.sessionMegDAO
				.findRecentTalkRoster(ownerId);
		List<Long> rosterIds = new ArrayList<Long>();
		for (Map<Long, Long> m : map) {
			if (rosterIds.size() > size)
				return mapList;
			Long rosterId = m.get("sendId").equals(ownerId) ? m.get("acceptId")
					: m.get("sendId");
			if (!rosterId.equals(ownerId) && !rosterIds.contains(rosterId)) {
				rosterIds.add(rosterId);
				CtmGroupMemberPo po = this.ctmGroupService.getCtmGM(ownerId,
						rosterId);
				if (null == po) {
					this.ctmGroupService.getCtmGM(rosterId, ownerId);
				}
				if (null != po) {
					Boolean isOnline = this.onlineDAO.findByPropertyUnique(
							OnlinePo.class.getCanonicalName(), "userId",
							po.getUserId()) != null;
					mapList.add(rosterData(po.getUserId(), po.getImage(),
							po.getUserName(), isOnline, null));
				}
			}
		}
		return mapList;
	}

	@Override
	public List<Map<String, Object>> getRelations(Users user, String parentID,
			boolean exceptSelf) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Company company = user.getCompany();
		String idFix = parentID.split("-")[0];
		if (parentID.split("-")[1].equals("0")) {
			nodeMap.addAll(this.ctmGroupService.getCtmGOrMNode(user.getId(), parentID, "rel"));
			if (company.getId() != 1) {
				nodeMap.addAll(getCompanyTree(user, parentID, exceptSelf, "rel"));
			}
		} else if (idFix.equals("g") || idFix.equals("gm")) {
			if (company.getId() != 1) {
				nodeMap.addAll(getCompanyTree(user, parentID, exceptSelf, "rel"));
			}
		} else {
			nodeMap.addAll(this.ctmGroupService.getCtmGOrMNode(user.getId(), parentID, "rel"));
		}
		return nodeMap;
	}

	/**
	 * 获取没有处理的用户验证信息
	 * 
	 * @param userId
	 *            当前用户ID
	 * @return
	 * 
	 */
	private List<Map<String, Object>> getUnhandleValidatSessionMegTip(
			Long userId) {
		List<Map<String, Object>> dNewMegTip = new ArrayList<Map<String, Object>>();
		List<ValidateSessionMegPo> validateSessionMegPos = (List<ValidateSessionMegPo>) this.validateSessionMegDAO
				.findUnhandleValidateSessionMeg(userId);
		if (null != validateSessionMegPos && !validateSessionMegPos.isEmpty()) {
			for (ValidateSessionMegPo validateSessionMegPo : validateSessionMegPos) {
				Users user = getUsersById(validateSessionMegPo.getSendId());
				if (null != user) {
					dNewMegTip.add(getNoticeValidateMessage(
							validateSessionMegPo.getId(), validateSessionMegPo,
							user));
				}
			}
		}
		return dNewMegTip;
	}

	/**
	 *  获取用户用户的资料信息,首先默认用户是联系人，如果通过ID查找到，则显示该用户的资料，
	 *    
	 * 
	 * @param  currentUserId 
	 *        当前用户的ID
	 * 
	 * @param userId
	 *         要看用户资料的ID
	 */
	public Map<String, Object> getUserInfo(Long currentUserId,Long userId) {
		AddressBean addressBean =null;
		CtmGroupMemberPo roster=this.ctmGroupMemberDAO.findRosterByOwner(currentUserId, userId);
		if (null != roster){
			Users tempuser = this.userDAO.findById(Users.class.getCanonicalName(), roster.getUserId());
			addressBean = new AddressBean("user-info-" + userId, // id,
					0L, // ownerId,
					null, // userinfo,
					0L, // groupId,
					roster.getUserName(), // userName,
					roster.getMail(), // realEmail,
					roster.getRealName(), // realName,
					roster.getDuty(), // duty,
					getAvatarSrc(tempuser.getImage1()), // user.getImage1(), //image, 要判断一下
					roster.getMobile(), // mobile,
					roster.getCompanyName(), // companyName,
					roster.getFax(), // fax,
					roster.getPhone(), // phone,
					roster.getPostcode(), // postcode,
					roster.getAddress(), // address,
					roster.getCompanyAddress(), // companyAddress,
					roster.getMan(), // man,
					roster.getBirthday(), // birthday,
					"", // department,
					roster.getComment() // comment
			);
		}else{
			Users user = this.userDAO.findById(Users.class.getCanonicalName(), userId);
			String imageString = user.getImage1();
			if (imageString == null || imageString.equals("")
					|| imageString.equals("null")) {
				imageString = "image.jpg";
			}
			imageString = WebConfig.userPortrait + imageString;
			String email=user.getRealEmail();//邮件地址
			String mobile=user.getMobile();//手机号码
			String companyname=user.getCompanyName();
			String phone=user.getPhone();//工作电话
			String address=user.getAddress();//家庭地址
			Date birthDay=user.getBirthday();
			String comment=user.getDescription();
			String fax=user.getFax();
			String duty=user.getDuty();
			String postCode=user.getPostcode();
			if (user.getInfodef()!=null && !user.getInfodef().booleanValue())
			{
				email="";//邮件地址
				mobile="";//手机号码
				companyname="";
				phone="";//工作电话
				address="";//家庭地址
				birthDay=null;
				comment="";
				fax="";
				duty="";
				postCode="";
			}
				addressBean = new AddressBean("inner-"+user.getId()+"", // id,
						0L, // ownerId,
						user, // userinfo,
						0L, // groupId,
						user.getUserName(), // userName,
						email, // realEmail,
						user.getRealName(), // realName,
						duty, // duty,
						imageString, // user.getImage1(), //image, 要判断一下
						mobile, // mobile,
						companyname, // companyName,
						fax, // fax,
						phone, // phone,
						postCode, // postcode,
						address, // address,
						user.getCompanyAddress(), // companyAddress,
						user.getMan(), // man,
						birthDay, // birthday,
						"", // department,
						comment // comment
			);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", addressBean);
		return map;
	}

	@Override
	public Map<String, Object> getUserNodeByUId(Long id) {
		Users user = userDAO.findById(Users.class.getName(), id);
		if (null != user) {
			OnlinePo po = this.onlineDAO.findByPropertyUnique(
					OnlinePo.class.getCanonicalName(), "userId", user.getId());
			Map<String, Object> map = TreeUtil.convertUser(user, null != po,
					"gm");
			return map;
		} else {
			return null;
		}
	}

	/**
	 * 获取用户下线通知的消息模型
	 * 
	 * @param users
	 *            当前用户
	 * @return
	 */
	private Map<String, Object> getUserOfflineNoticeMessage(Users user) {
		return rosterData(user.getId(), user.getImage1(), user.getRealName(),
				false, user.getRealName() + " 下线了！");
	}

	/**
	 * 获取用户上线通知的消息模型
	 * 
	 * @param users
	 *            当前用户
	 * @return
	 */
	private Map<String, Object> getUserOnlineNoticeMessage(Users user) {
		return rosterData(user.getId(), user.getImage1(), user.getRealName(),
				true, user.getRealName() + " 上线了！");
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

	/**
	 * 
	 * @param category
	 *            是否是组验证信息还是找人的验证信息 1：找人 2：找组
	 * @param type
	 *            0：普通验证信息 1：同意验证信息 2：拒绝验证信息 3：通知验证信息
	 * @param acceptId
	 *            接受者的ID
	 * @param senderId
	 *            发送者的ID
	 * @param sendName
	 *            发送者名称
	 * @param validateSessionMeg
	 *            发送者的信息
	 * @param groupId
	 *            发送者请求组ID
	 * @return
	 */
	private ValidateSessionMegPo getValidateSessionMegPoWrapper(int category,
			int type, Long acceptId, Long senderId, String sendName,
			String validateSessionMeg, Long groupId) {
		ValidateSessionMegPo validateSessionMegPo = new ValidateSessionMegPo();
		validateSessionMegPo.setAcceptId(acceptId);
		validateSessionMegPo.setAddDate(new Date());
		validateSessionMegPo.setCategory(category);
		validateSessionMegPo.setGroupId(groupId);
		validateSessionMegPo.setSessionMeg(validateSessionMeg);
		validateSessionMegPo.setType(type);
		validateSessionMegPo.setSendId(senderId);
		validateSessionMegPo.setSendName(sendName);
		return validateSessionMegPo;
	}

	/**
	 * 判断用户是否存在讨论组中
	 * 
	 * @param groupId
	 *            讨论组的ID
	 * @param memberId
	 *            用户的ID
	 * @return true 不存在 false 存在
	 */
	private boolean isUserInDiscuGroup(Long groupId, Long memberId) {
		List<DiscuGroupMemberPo> discuGroupMemberPos = this.discuGMDAO
				.findGroupUser(groupId, memberId);
		return discuGroupMemberPos == null || discuGroupMemberPos.isEmpty();
	}

	/**
	 * 通知用户的联系人，用户下线
	 * 
	 * @param users
	 */
	private void noticeCurrentUserOffline(Users users) {
		messagesService.sendMessage("IM.messageHandler.updateNoticeMeg",
				PageConstant.LG_USER_ID, this.getUserOfflineNoticeMessage(users),
				this.getCurrentUserOnlineFriendsIds(users.getId()));
	}

	/**
	 * 通知用户的联系人，用户上线
	 * 
	 * @param users
	 */
	private void noticeCurrentUserOnline(Users users) {
		messagesService.sendMessage("IM.messageHandler.updateNoticeMeg",
				PageConstant.LG_USER_ID, this.getUserOnlineNoticeMessage(users),
				this.getCurrentUserOnlineFriendsIds(users.getId()));
	}

	/**
	 * 处理请求者的请求
	 * 
	 * @param senderId
	 *            请求者ID
	 * @param gourpId
	 *            请求者所选的组ID
	 */
	private void proSenderValidateRequest(ValidateSessionMegPo po) {
		CtmGroupMemberPo ctmGroupMemberPo = this.ctmGroupService.getCtmGM(
				po.getSendId(), po.getAcceptId());
		if (ctmGroupMemberPo == null) {
			Users user = this.userDAO.findById(Users.class.getCanonicalName(),
					po.getAcceptId());
			ctmGroupMemberPo = new CtmGroupMemberPo();
			ctmGroupMemberPo.setCreateTime(new Date());
			ctmGroupMemberPo.setGroupId(po.getGroupId());
			ctmGroupMemberPo.setOwnerId(po.getSendId());
			ctmGroupMemberPo.setUserId(user.getId());
			ctmGroupMemberPo.setUserName(user.getRealName());
			ctmGroupMemberPo.setImage(user.getImage1());
			ctmGroupMemberPo.setMail(user.getRealEmail());
			this.ctmGroupMemberDAO.saveOrUpdate(ctmGroupMemberPo);
		} else {
			ctmGroupMemberPo.setGroupId(po.getGroupId());
			this.ctmGroupMemberDAO.saveOrUpdate(ctmGroupMemberPo);
		}
	}

	/**
	 * 处理请求者请求组的请求
	 * 
	 * @param senderId
	 *            请求者ID
	 * @param gourpId
	 *            请求者所选的组ID
	 */
	private void proSenderValidateRequestGroup(ValidateSessionMegPo po) {
		Users user = this.userDAO.findById(Users.class.getCanonicalName(),
				po.getAcceptId());
		DiscuGroupMemberPo discuGroupMemberPo = new DiscuGroupMemberPo();
		discuGroupMemberPo.setCreateDate(new Date());
		discuGroupMemberPo.setGroupId(po.getGroupId());
		discuGroupMemberPo.setMemberId(po.getSendId());
		discuGroupMemberPo.setMemberName(po.getSendName());
		discuGroupMemberPo.setOwnerId(po.getGroupId());
		discuGroupMemberPo.setOwnerName(user.getRealName());
		this.discuGMDAO.saveOrUpdate(discuGroupMemberPo);
	}

	/**
	 * 处理用户信息
	 * 
	 * @param vMsgID
	 *            接受信息ID
	 * @param validateSessionMegPo
	 *            发送信息
	 */
	public boolean proValidateSessionMessage(Long userId, Long vMsgId,
			int type, String validateSessionMeg, Long groupId) {
		Users user = this.getUsersById(userId);
		ValidateSessionMegPo po = this.validateSessionMegDAO.findById(
				"com.yozo.weboffice.domain.ValidateSessionMegPo", vMsgId);
		if (null != po) {
			if (type == 1) {// 同意
				if (po.getCategory() == 1) {
					// 处理请求者的请求
					this.proSenderValidateRequest(po);
					// 返回请求者处理信息
					this.sendSenderValidateMessage(po.getAcceptId(),
							po.getSendId(), user.getRealName()
									+ "接受了您的请求并将您添加为好友");
					// 处理接受者请求
					ValidateSessionMegPo validateSessionMegPo = this
							.getValidateSessionMegPoWrapper(po.getCategory(),
									type, po.getSendId(), userId,
									user.getRealName(), validateSessionMeg,
									groupId);
					this.proSenderValidateRequest(validateSessionMegPo);
				} else if (po.getCategory() == 2) {
					// 处理请求者的请求
					this.proSenderValidateRequestGroup(po);
					DiscuGroupPo discuGroupPo = this.discuGroupDAO.findById(
							"com.yozo.weboffice.domain.DiscuGroupPo", groupId);
					// 返回请求者处理信息
					this.sendSenderValidateMessage(
							po.getAcceptId(),
							po.getSendId(),
							"讨论组" + discuGroupPo.getName()
									+ "管理员通过您的申请<br/>您已加入讨论组"
									+ discuGroupPo.getName() + "");
				}
			} else if (type == 2) {
				if (po.getCategory() == 1) {
					this.sendSenderValidateMessage(po.getAcceptId(),
							po.getSendId(), user.getRealName()
									+ "拒绝了您的添加请求<br/>拒绝理由："
									+ validateSessionMeg);
				} else if (po.getCategory() == 2) {
					DiscuGroupPo discuGroupPo = this.discuGroupDAO.findById(
							"com.yozo.weboffice.domain.DiscuGroupPo", groupId);
					this.sendSenderValidateMessage(po.getAcceptId(),
							po.getSendId(), "讨论组" + discuGroupPo.getName()
									+ "管理员拒绝了您加入讨论组" + discuGroupPo.getName()
									+ "<br/>拒绝理由：" + validateSessionMeg);
				}
			}
			po.setHandle(true);
			po.setHandleDate(new Date());
			this.validateSessionMegDAO.saveOrUpdate(po);
			return true;
		}
		return false;
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
	public GroupSessionMegPo saveGroupMeg(GroupSessionMegPo sessionMegGroup) {
		groupSessionMegDAO.saveOrUpdate(sessionMegGroup);
		return sessionMegGroup;
	}

	@Override
	public void saveGroupMegRead(GroupSessionMegReadPo sessionMegGroupRead) {
		groupSessionMegReadDAO.saveOrUpdate(sessionMegGroupRead);

	}

	@Override
	public void savePersonMeg(SessionMegPo sessionMeg) {
		sessionMegDAO.saveOrUpdate(sessionMeg);
	}
	
	/**
	 * 将用户的上线状态保存到数据库中
	 * 
	 * @param users
	 *            当前用户状态
	 * @param ip
	 *            用户的Ip地址
	 */
	private void saveUserOnlineStatus(Users users, String ip) {
		OnlinePo po = this.onlineDAO.findByPropertyUnique(
				OnlinePo.class.getCanonicalName(), "userId", users.getId());
		if (null == po) {
			po = new OnlinePo();
			po.setLoginTime(new Date());
			po.setName(users.getRealName());
			po.setUserId(users.getId());
			po.setIp(ip);
			this.onlineDAO.saveOrUpdate(po);
		}
	}

	@Override
	public List<String[]> searchDepMemberList(Long userId, String key,
			Company company) {
		List<DepMemberPo> depMemberList = depMemberDAO.searchByKeyAndCompany(
				key, company);
		if (depMemberList == null || depMemberList.isEmpty()) {
			return null;
		}
		List<String[]> resultList = new ArrayList<String[]>();
		for (DepMemberPo depMember : depMemberList) {
			Users duser = depMember.getUser();
			Long duserId = duser.getId();
			if (!userId.equals(duserId)) { // 排除当前用户
				String[] depMemberS = new String[2];
				// value值的形式为 组ID-用户ID（包括层级关系）
				Organizations org = depMember.getOrganization();
				if (org != null) {
					String value = org.getId() + "-" + duserId;
					if (org.getParentKey() != null) {
						value = org.getParentKey() + value;
					}
					String name = duser.getRealName() + "(" + org.getName()
							+ ")";
					depMemberS[0] = name;
					depMemberS[1] = value;
					resultList.add(depMemberS);
				}
			}
		}
		return resultList;
	}

	@Override
	public List<String[]> searchRel(Users user, String key, String index) {
		Long userId = user.getId();
		Company company = user.getCompany();
		if (index.equals("company")) {
			return searchDepMemberList(userId, key, company);
		} else if (index.equals("friend")) {
			return getCtmGroupService().searchCtmGMList(userId, key);
		} else if (index.equals("discu")) {
			return getDiscuGroupService().searchDiscuGList(userId, key, company.equals(1));
		}
		return null;
	}

	@Override
	public List<Map<String, String>> searchRostersByKey(String keyWord, Long userId) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		List<Users> usersList = this.userDAO.searchByKey(SQLUtil.stringEscape(keyWord), userId,
				new String[] { "realEmail", "userName", "mobile", "realName" });
		if (usersList == null || usersList.isEmpty()) {
			return null;
		}
		for (Users user : usersList) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("userId", user.getId().toString());
			m.put("avatar", getAvatarSrc(user.getImage1()));
			m.put("userName", user.getRealName());
			m.put("gender", user.getMan() ? "男" : "女");
			Company company = user.getCompany();
			m.put("companyName", company == null ? "" : company.getName());
			data.add(m);
		}
		return data;
	}

	@Override
	public void sendGroupSessionMeg(String groupSessionMeg, Users user,
			Long groupId, int type, String date) {
		// 先获取组成员
		Map<String, Object> discuGMMap = this.discuGroupService.getDiscuGMList(
				groupId, true);
		DiscuGroupPo dg = (DiscuGroupPo) this.discuGroupService
				.getDiscuGroupById(groupId);
		GroupSessionMegPo groupSessionMegPo = new GroupSessionMegPo();
		try {
			groupSessionMegPo.setSendId(user.getId());
			groupSessionMegPo.setSendImg(user.getImage1());
			groupSessionMegPo.setSendName(user.getRealName());
			groupSessionMegPo.setGroupId(groupId);
			groupSessionMegPo.setGroupName(dg.getName());
			groupSessionMegPo.setOwnerId(dg.getOwnerId());
			groupSessionMegPo.setSessionMeg(groupSessionMeg);
			groupSessionMegPo.setAddDate(DateUtils.ftmStringToDate(
					"yyyy-MM-dd HH:mm:ss", date));
			groupSessionMegPo.setType(type);
			// groupSessionMegPo.setUrl(url);
			groupSessionMegPo = saveGroupMeg(groupSessionMegPo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<DiscuGroupMemberPo> discuGMList = (List<DiscuGroupMemberPo>) discuGMMap
				.get("discuGMList");
		if (discuGMList == null || discuGMList.isEmpty()) {
			return;
		}
		final List<Long> memberIdList = new ArrayList<Long>();
		for (DiscuGroupMemberPo discuGM : discuGMList) {
			if (discuGM.getMemberId().longValue() != user.getId().longValue()) {
				memberIdList.add(discuGM.getMemberId());
			}
		}
		if (!memberIdList.isEmpty()) {
			GroupSessionMegReadPo groupSessionMegRead = new GroupSessionMegReadPo();
			groupSessionMegRead.setGroupId(groupId);
			groupSessionMegRead.setReaded(false);
			groupSessionMegRead.setGroupSessionMegId(groupSessionMegPo.getId());

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wId", groupId);
			data.put("groupSessionMeg", groupSessionMegPo);
			data.put("groupSessionMegRead", groupSessionMegRead);
			data.put("memberIdList", memberIdList);
			messagesService.sendMessage("IM.messageHandler.updateGMsg",
					PageConstant.LG_USER_ID, data, memberIdList);
			try {
				for (Long acceptId : memberIdList) {
					groupSessionMegRead = new GroupSessionMegReadPo();
					groupSessionMegRead.setGroupId(groupId);
					groupSessionMegRead.setReaded(false);
					groupSessionMegRead.setGroupSessionMegId(groupSessionMegPo.getId());
					groupSessionMegRead.setReaded(false);
					groupSessionMegRead.setAcceptId(acceptId);
					saveGroupMegRead(groupSessionMegRead);
				}
				groupSessionMegRead = new GroupSessionMegReadPo();
				groupSessionMegRead.setGroupId(groupId);
				groupSessionMegRead.setGroupSessionMegId(groupSessionMegPo.getId());
				groupSessionMegRead.setReaded(true);
				groupSessionMegRead.setAcceptId(user.getId());
				saveGroupMegRead(groupSessionMegRead);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 找组信息
	 * 
	 * @param validateSessionMegPo
	 * @return
	 */
	private int sendGroupValidateSessionMessage(
			ValidateSessionMegPo validateSessionMegPo) {
		if (this.isUserInDiscuGroup(validateSessionMegPo.getGroupId(),
				validateSessionMegPo.getSendId())) {
			Long id = null;
			// 验证信息是否已经发送过。
			ValidateSessionMegPo po = this.validateSessionMegDAO
					.getUnValidateSessionMegPo(
							validateSessionMegPo.getSendId(),
							validateSessionMegPo.getAcceptId(),
							validateSessionMegPo.getCategory());
			if (po != null) {
				po.setGroupId(validateSessionMegPo.getGroupId());
				po.setSessionMeg(validateSessionMegPo.getSessionMeg());
				id = po.getId();
				this.validateSessionMegDAO.saveOrUpdate(po);
			} else {
				this.validateSessionMegDAO.saveOrUpdate(validateSessionMegPo);
				id = validateSessionMegPo.getId();
			}
			this.sendValidateMessage(id, validateSessionMegPo,
					getUsersById(validateSessionMegPo.getSendId()));
			return 0;

		}
		return 1;
	}

	@Override
	public void sendOfflineNoticeMessage(Long ownerId) {
		Users users = this.userDAO.findById(Users.class.getCanonicalName(),
				ownerId);
		if (null != users) {
			// 通知用户的好友，用户上线
			this.noticeCurrentUserOffline(users);
		}
	}

	@Override
	public void sendOnlineNoticeMessage(String ip, Long ownerId) {
		Users users = this.userDAO.findById(Users.class.getCanonicalName(),
				ownerId);
		if (null != users) {
			// 将用户的状态保存到数据库中
			this.saveUserOnlineStatus(users, ip);
			// 通知用户的好友，用户上线
			this.noticeCurrentUserOnline(users);
		}
	}

	/**
	 * 发送找人验证信息
	 * 
	 * @param validateSessionMegPo
	 * @return
	 */
	private int sendPersonValidateSessionMessage(
			ValidateSessionMegPo validateSessionMegPo) {
		// 查找处理人是否已经在请求者的联系人列表中
		if (!this.ctmGroupService.isRosterExist(
				validateSessionMegPo.getSendId(),
				validateSessionMegPo.getAcceptId())) {
			Long id = null;
			// 验证信息是否已经发送过。
			ValidateSessionMegPo po = this.validateSessionMegDAO
					.getUnValidateSessionMegPo(
							validateSessionMegPo.getSendId(),
							validateSessionMegPo.getAcceptId(),
							validateSessionMegPo.getCategory());
			if (po != null) {
				po.setGroupId(validateSessionMegPo.getGroupId());
				po.setSessionMeg(validateSessionMegPo.getSessionMeg());
				id = po.getId();
				this.validateSessionMegDAO.saveOrUpdate(po);
			} else {
				this.validateSessionMegDAO.saveOrUpdate(validateSessionMegPo);
				id = validateSessionMegPo.getId();
			}
			// 发送信息
			this.sendValidateMessage(id, validateSessionMegPo,
					getUsersById(validateSessionMegPo.getSendId()));
			return 0;
		}
		return 1;
	}

	/**
	 * 返回请求者信息
	 * 
	 * @param senderId
	 * @param acceptId
	 * @param message
	 */
	private void sendSenderValidateMessage(Long senderId, Long acceptId,
			String message) {
		ValidateSessionMegPo po = new ValidateSessionMegPo();
		po.setAcceptId(acceptId);
		po.setAddDate(new Date());
		po.setSendId(senderId);
		po.setSessionMeg(message);
		po.setType(3);
		this.validateSessionMegDAO.saveOrUpdate(po);

		List<Long> userIds = new ArrayList<Long>();
		userIds.add(po.getAcceptId());
		messagesService.sendMessage("IM.messageHandler.updateValidateMeg",
				PageConstant.LG_USER_ID, this.getNoticeValidateMessage(po.getId(),
						po, getUsersById(senderId)), userIds);
	}

	@Override
	public void sendSessionMeg(String meg, Users user, Long acceptId, int type,
			String date) {
		final SessionMegPo sessionMeg = new SessionMegPo();
		try {
			sessionMeg.setSendId(user.getId());
			sessionMeg.setSendImg(user.getImage1());
			sessionMeg.setSendName(user.getRealName());
			sessionMeg.setAcceptId(acceptId);
			sessionMeg.setSessionMeg(meg);
			sessionMeg.setAddDate(DateUtils.ftmStringToDate(
					"yyyy-MM-dd HH:mm:ss", date));
			sessionMeg.setType(type);
			sessionMeg.setReaded(false);
			savePersonMeg(sessionMeg);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wId", acceptId + "-" + user.getId());
			data.put("icon", user.getImage1());
			data.put("sessionMeg", sessionMeg);
			List<Long> userIds = new ArrayList<Long>();
			userIds.add(acceptId);
			messagesService.sendMessage("IM.messageHandler.updatePMsg",
					PageConstant.LG_USER_ID, data, userIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送验证信息到用户
	 * 
	 * @param replyMsgId
	 *            回复验证信息的ID
	 * @param validateSessionMegPo
	 *            回复信息
	 * @param user
	 *            用户实体
	 */
	private void sendValidateMessage(Long replyMsgId,
			ValidateSessionMegPo validateSessionMegPo, Users user) {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(validateSessionMegPo.getAcceptId());
		messagesService.sendMessage("IM.messageHandler.updateValidateMeg",
				PageConstant.LG_USER_ID, this.getNoticeValidateMessage(replyMsgId,
						validateSessionMegPo, user), userIds);
	}

	@Override
	public int sendValidateSessionMessage(int category, Users user,
			Long acceptId, Long groupId, String validateSessionMeg) {
		ValidateSessionMegPo validateSessionMegPo = new ValidateSessionMegPo();
		validateSessionMegPo.setAcceptId(acceptId);
		validateSessionMegPo.setAddDate(new Date());
		validateSessionMegPo.setCategory(category);
		validateSessionMegPo.setGroupId(groupId);
		validateSessionMegPo.setSessionMeg(validateSessionMeg);
		validateSessionMegPo.setType(0);
		validateSessionMegPo.setSendId(user.getId());
		validateSessionMegPo.setSendName(user.getRealName());
		return (validateSessionMegPo.getCategory() == 1) ? sendPersonValidateSessionMessage(validateSessionMegPo)
				: sendGroupValidateSessionMessage(validateSessionMegPo);
	}

	@Override
	public void updateGroupSessionMessageRead(Long groupId, Long userId,
			List<Long> mesasgeIds) {
		this.groupSessionMegDAO.updateGroupSessionMessageRead(groupId, userId,
				mesasgeIds);
	}

	@Override
	public void updateSessionMessageRead(List<Long> mesasgeIds) {
		this.sessionMegDAO.updateSessionMessageRead(mesasgeIds);
	}
	
	/**
	 * 个人文件文件传输
	 * fileTransType//10 notice 11:接收 12:拒绝 13：接收完毕 14:系统拒绝
	 * @throws Exception 
	 */
	public boolean sendPersonFile(long acceptId,String basePath,FileTransfer file,Users user) throws Exception{
		int fileTransType=10;
		String uploadUrl = "data/uploadfile/talk/resource";
		String name = new String(file.getFilename().getBytes(),"utf-8");
		name=name.replace(" ", "");

		String type=getFileType(name);
		String size=getFileSizeText(file.getSize());
		
		String fileName=new String(this.UploadFile(file, basePath, uploadUrl, type).getBytes(),"utf-8");
		fileName=fileName.replace(" ", "");
		
		uploadUrl=uploadUrl+"/"+fileName;
		String msg=this.getTransactionFileString(0,user.getId(),user.getRealName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), name, fileName, size,type,basePath);
		
		SessionMegPo sessionMeg=this.savePersonFileTransSessionMegPo(msg, user, acceptId, fileTransType, new Date());
		msg=sessionMeg.getSessionMeg().replace("{msgId}", sessionMeg.getId()+"");
		sessionMeg.setSessionMeg(msg);
		this.sessionMegDAO.saveOrUpdate(sessionMeg);
		
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("url", uploadUrl);
		data.put("size", size);
		data.put("type", fileTransType);//10 notice 11:接收 12:拒绝 13：接收完毕 14:系统拒绝
		data.put("senderName", user.getRealName());
		data.put("receiverName", user.getRealName());
		data.put("sendId", user.getId());
		data.put("acceptId", acceptId);
		data.put("addDate", new Date());
		data.put("wId", acceptId + "-" + user.getId());
		data.put("icon", user.getImage1());
		data.put("sessionMeg", sessionMeg);
		
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(acceptId);
		
		messagesService.sendMessage("IM.messageHandler.updatePMsg",
				PageConstant.LG_USER_ID, data, userIds);
		return true;
	}
	
	/**
	 * 个人文件文件传输
	 * fileTransType//10 notice 11:接收 12:拒绝 13：接收完毕 14:系统拒绝
	 */
	public boolean sendGroupFile(long groupId,String basePath,FileTransfer file,Users user) throws Exception{
		String uploadUrl = "data/uploadfile/talk/resource";
		String name = new String(file.getFilename().getBytes(),"utf-8");
		name=name.replace(" ", "");
		
		String type=getFileType(name);
		String size=getFileSizeText(file.getSize());
		
		String fileName=new String(this.UploadFile(file, basePath, uploadUrl, type).getBytes(),"utf-8");
		fileName=fileName.replace(" ", "");
		uploadUrl=uploadUrl+"/"+fileName;
		String msg=this.getTransactionFileString(1,groupId,user.getRealName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), name, fileName, size,type,basePath);
			
		        Map<String, Object> discuGMMap = this.discuGroupService.getDiscuGMList(groupId, true);
			    DiscuGroupPo dg = (DiscuGroupPo) this.discuGroupService.getDiscuGroupById(groupId);
			    GroupSessionMegPo groupSessionMegPo = new GroupSessionMegPo();
				groupSessionMegPo.setSendId(user.getId());
				groupSessionMegPo.setSendImg(user.getImage1());
				groupSessionMegPo.setSendName(user.getRealName());
				groupSessionMegPo.setGroupId(groupId);
				groupSessionMegPo.setGroupName(dg.getName());
				groupSessionMegPo.setOwnerId(dg.getOwnerId());
				groupSessionMegPo.setSessionMeg(formatEmotion(msg,false));
				groupSessionMegPo.setAddDate( new Date());
				groupSessionMegPo.setType(0);
				// groupSessionMegPo.setUrl(url);
				groupSessionMegPo = saveGroupMeg(groupSessionMegPo);
				
				msg=groupSessionMegPo.getSessionMeg().replace("{msgId}", groupSessionMegPo.getId()+"");
			    groupSessionMegPo.setSessionMeg(msg);
				this.groupSessionMegDAO.saveOrUpdate(groupSessionMegPo);
				
				List<DiscuGroupMemberPo> discuGMList = (List<DiscuGroupMemberPo>) discuGMMap.get("discuGMList");
				if (discuGMList == null || discuGMList.isEmpty()) {
					return false;
				}
			    final List<Long> memberIdList = new ArrayList<Long>();
				for (DiscuGroupMemberPo discuGM : discuGMList) {
					if (discuGM.getMemberId().longValue() != user.getId().longValue()) {
						memberIdList.add(discuGM.getMemberId());
					}
				}
				if (!memberIdList.isEmpty()) {
					final GroupSessionMegReadPo groupSessionMegRead = new GroupSessionMegReadPo();
					groupSessionMegRead.setGroupId(groupId);
					groupSessionMegRead.setReaded(false);
					groupSessionMegRead.setGroupSessionMegId(groupSessionMegPo.getId());
	
					final Map<String, Object> data = new HashMap<String, Object>();
					data.put("wId", groupId);
					data.put("groupSessionMeg", groupSessionMegPo);
					data.put("groupSessionMegRead", groupSessionMegRead);
					data.put("memberIdList", memberIdList);
					data.put("name", name);
					data.put("url", uploadUrl);
					data.put("size", size);
					messagesService.sendMessage("IM.messageHandler.updateGMsg",
							PageConstant.LG_USER_ID, data, memberIdList);
					try {
						for (Long acceptId : memberIdList) {
							groupSessionMegRead.setReaded(false);
							groupSessionMegRead.setAcceptId(acceptId);
							saveGroupMegRead(groupSessionMegRead);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		return true;
	}
	
	
	/**
	 * 
	 * @param type
	 *          0表示个人  1表示讨论组
	 * @param acceptId
	 *          接收ID
	 * @param senderName
	 *           发送人名称
	 * @param date
	 *            时间
	 * @param trueName
	 *            文件的真实名称
	 * @param fileName
	 *            文件的保存名称
	 * @param size
	 *            文件的大小文本
	 * @param fileType
	 *           文件类型
	 * @param basePath
	 *           基本路径
	 * @return
	 */
	private String getTransactionFileString(int type,long acceptId,String senderName,String date,String trueName,String fileName,String size,String fileType,String basePath){
		String msgContent = "";
	    msgContent +="<div stype='margin-top:3px;'><span style='margin:3px 10px;font-size:12px;display:block;'>接收文件请求:</span>"
	    	+"<img style='float:left;margin:0px 10px;width:30px;height:35px;' src='../"+this.imamgePath(basePath, fileType)+"'/>"
			+"<div style=''>"
		       + "文件名称:&nbsp;&nbsp;"+ trueName+" "+"  ("+size+")"
		  +"<div>&nbsp;&nbsp;<a id='{msgId}_yes' onclick='javascript:IM.messageHandler.downFileTrans(this,{msgId},"+type+",\""+trueName+"\",\""+fileName+"\","+acceptId+")' href='javascript:void(0);'>接收</a>" +
		  "&nbsp;&nbsp;&nbsp;&nbsp;<a id='{msgId}_no' onclick='javascript:IM.messageHandler.rejectFileTrans(this,{msgId},"+type+",\""+acceptId+"\",\""+trueName+"\");'  href='javascript:void(0);'>拒绝</a>" +
		  "</div></div></div><br/>";
	    
	    return msgContent;
	}
	
	private String imamgePath(String basePath,String fileType){
		if(fileType.contains(".")){
			fileType=fileType.substring(fileType.indexOf(".")+1);
		}
		String path="static/images/fileicon48/";
		File file=new File(basePath+File.separator+path+fileType+".png");
		if(file.exists()){
			return path+fileType+".png";
		}
		return path+"other.png";
	}
	

	private SessionMegPo savePersonFileTransSessionMegPo(String meg, Users user, Long acceptId, int type,
			Date date) {
		final SessionMegPo sessionMeg = new SessionMegPo();
		try {
			sessionMeg.setSendId(user.getId());
			sessionMeg.setSendImg(user.getImage1());
			sessionMeg.setSendName(user.getRealName());
			sessionMeg.setAcceptId(acceptId);
			sessionMeg.setSessionMeg(formatEmotion(meg, false));
			sessionMeg.setAddDate( date);
			sessionMeg.setType(type);
			sessionMeg.setReaded(false);
			savePersonMeg(sessionMeg);
			return sessionMeg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
   private String  UploadFile(FileTransfer file,String basePath,String uploadUrl,String type){
		
		BufferedInputStream brIn = null;
		BufferedOutputStream brOut = null;
		File imgFile = null;
		try {
			InputStream in = file.getInputStream();
			brIn = new BufferedInputStream(in);
			byte[] by = new byte[1024];
			File dir = new File(basePath+ uploadUrl);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			imgFile = new File(dir, new Date().getTime() + type);
			brOut = new BufferedOutputStream(new FileOutputStream(imgFile));
			int read = 0;
			while ((read = brIn.read(by)) != -1) {
				brOut.write(by, 0, read);
			}
			brOut.flush();
			brOut.close();
			brIn.close();
			uploadUrl += "/" + imgFile.getName();
			// uploadUrl = imgFile.getAbsolutePath();
		} catch (IOException e) {
			System.out.println("文件上传出错了");
			e.printStackTrace();
		} finally {
			try {
				if (brOut != null)
					brOut.close();
				if (brIn != null)
					brIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imgFile.getName();
	}
	
	private String getFileType(String name){
		String type = "";
		if(name.contains(".")){
			type=name.substring(name.lastIndexOf("."), name.length());
		}
		return type;
	}
	
	private String getFileSizeText(long bytes){
		DecimalFormat format=new DecimalFormat("#.##");
		double size=0;
		String sizeText="";
		if(bytes >1024){
			if(bytes > 1024*1024){
			   size=(double)bytes/(1024*1024);
			   sizeText=format.format(size)+"MB";
			}else{
				size=(double)bytes/(1024);
				sizeText=format.format(size)+"KB";
			}
		}else{
			sizeText=bytes+"B";
		}
		return sizeText;
	}
	
	/**
	 * 拒绝接受文件
	 * @param acceptId
	 * @param fileName
	 * @param user
	 */
	public void rejectFileTrans(String type,long msgId,long acceptId,String fileName,Users user){
		String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if(type.equals("0")){
		  this.personFileTransBefore(msgId);
		  this.sendSessionMeg("\t [<span style='color:red;'>系统提示</span>]"+"拒绝接收您的传输文件("+fileName+")<br/>", user, acceptId, 0, date);
		}else{
			this.groupFileTransBefore(msgId, acceptId);
			this.sendGroupSessionMeg("\t [<span style='color:red;'>系统提示</span>]"+"拒绝接收您的传输文件("+fileName+")<br/>", user, acceptId, 0,date);
		}
	}
	
	public void personFileTransBefore(long msgId){
		SessionMegPo po=this.sessionMegDAO.findById(SessionMegPo.class.getCanonicalName(), msgId);
		po.setAccepterDelete(true);
		this.sessionMegDAO.saveOrUpdate(po);
	}
	
	public void groupFileTransBefore(long msgId,long acceptId){
		Map<String,Object> propertyMap=new HashMap<String, Object>();
		propertyMap.put("groupSessionMegId", msgId);
		propertyMap.put("acceptId", acceptId);
		GroupSessionMegReadPo po=this.groupSessionMegReadDAO.findByPropertyUnique(GroupSessionMegReadPo.class.getCanonicalName(), propertyMap);
		if(null !=po){
			po.setDeleted(true);
			this.groupSessionMegReadDAO.saveOrUpdate(po);
		}
	}
	
   public void renamePersonNickName(Users user,long userId,String newNickname){
	   this.renameCtmPerson(user, userId, newNickname);
	   this.renameDiscuPerson(user, userId, newNickname);
	}
   
   private void renameCtmPerson(Users user,long userId,String newNickname){
	   Map<String,Object> propertyMap=new HashMap<String, Object>();
		propertyMap.put("ownerId", user.getId());
		propertyMap.put("userId", userId);
		List<CtmGroupMemberPo> ctmGroupMemberPos=this.ctmGroupMemberDAO.findByProperty(CtmGroupMemberPo.class.getCanonicalName(), propertyMap);
		for(CtmGroupMemberPo ctmGroupMemberPo:ctmGroupMemberPos){
			ctmGroupMemberPo.setNickName(newNickname);
			this.ctmGroupMemberDAO.saveOrUpdate(ctmGroupMemberPo);
		}
   }
   
  
   
   private void renameDiscuPerson(Users user,long memberId,String newNickname){
		DiscuGroupMemberPoRelationPo discuGroupMemberPoRelationPo=this.discuGroupMemberPoRelationDao.getDiscuGroupMemberPoRelationPo(user.getId(), memberId);
		if(null == discuGroupMemberPoRelationPo){
			discuGroupMemberPoRelationPo=new DiscuGroupMemberPoRelationPo();
			discuGroupMemberPoRelationPo.setMemberId(memberId);
			discuGroupMemberPoRelationPo.setOwnerId(user.getId());
			discuGroupMemberPoRelationPo.setNickName(newNickname);
			this.discuGroupMemberPoRelationDao.saveOrUpdate(discuGroupMemberPoRelationPo);
		}else{
			discuGroupMemberPoRelationPo.setNickName(newNickname);
			this.discuGroupMemberPoRelationDao.saveOrUpdate(discuGroupMemberPoRelationPo);
		}
		
   }
   
   /**
	 * 判断是否相同
	 * @param newPass
	 * @return
	 */
	public boolean isUserSameCompany(Users userInfo,Long userId){
		if(userInfo !=null){
			Users user=this.userDAO.findById(Users.class.getCanonicalName(), userId);
			if(null != user){
				return user.getCompany().getId().intValue() == userInfo.getCompany().getId().intValue();
			}
		}
		return false;
	}
}