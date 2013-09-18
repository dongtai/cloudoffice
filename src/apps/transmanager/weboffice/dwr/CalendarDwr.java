package apps.transmanager.weboffice.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.databaseobject.CalendarEvent;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dwr.ICalendarService;
import apps.transmanager.weboffice.util.TreeUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;

//import com.yozo.calendar.model.Event;

public class CalendarDwr {

	private ICalendarService calendarService = (ICalendarService) ApplicationContext
			.getInstance().getBean("calendarService");

	public HashMap getEvents(HttpServletRequest req) {
		HashMap returnMap = new HashMap();
		try {
			Users uinfo = (Users) req.getSession().getAttribute("userKey");
			if (uinfo != null) {
				// returnMap =
				// calendarService.getEvents(uinfo.getId(),uinfo.getUserName());
				returnMap = calendarService.getUserEvents(uinfo.getId(),
						uinfo.getId(), uinfo.getUserName());
				returnMap.put("currentUId", uinfo.getId());
			} else {
				returnMap.put("evts", new ArrayList<HashMap>());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	public HashMap getEmpowerdEvents(long id, HttpServletRequest req) {
		HashMap returnMap = new HashMap();
		try {
			Users uinfo = (Users) req.getSession().getAttribute("userKey");
			Users currentUser = (Users) calendarService.getUserById(id);
			if (uinfo != null) {
				returnMap = calendarService.getUserEvents(uinfo.getId(), id,
						currentUser.getUserName());
				returnMap.put("currentUId", uinfo.getId());
			} else {
				returnMap.put("evts", new ArrayList<HashMap>());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	public boolean deleteEvent(long eventId) {
		return calendarService.deleteEvent(eventId);
	}

	public long addEvent(Long userid, CalendarEvent event, HttpServletRequest req) {
		long returnValue = 0;
		Users poweruinfo = (Users) req.getSession().getAttribute("userKey");// 系统本身用户
		Users cuserinfo = calendarService.getCalendarUser(userid);
		if ((poweruinfo != null) && (cuserinfo != null)) {
			event.setPoweruserinfo(poweruinfo);// 为您安排日程的用户
			event.setUserinfo(cuserinfo);// 日出日程归属用户
			CalendarEvent ce = calendarService.saveCalendarEvent(event);
			returnValue = ce.getId();
			calendarService.dealEventInviteer(ce, event.getTestString());
			calendarService.dealEventPublicer(ce, event.getTestString1());
		}
		return returnValue;
	}

	public boolean updateEvent(Long userid, CalendarEvent event,
			HttpServletRequest req) {
		Users poweruinfo = (Users) req.getSession().getAttribute("userKey");
		Users cuserinfo = calendarService.getCalendarUser(userid);
		if ((poweruinfo != null) && (cuserinfo != null)) {
			event.setPoweruserinfo(poweruinfo);// 为您安排日程的用户
			event.setUserinfo(cuserinfo);// 日出日程归属用户
			calendarService.saveCalendarEvent(event);
		}
		return true;
	}

	/**
	 * 获得关注的其他人的日程
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getFocusedUsers(HttpServletRequest req) {
		List<Map> returnList = new ArrayList<Map>();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			returnList = calendarService.getFocusedUsers(uinfo.getId());
		}
		return returnList;
	}

	/**
	 * 获得最新分享的5个人的日程
	 */
	public List<Map> getNewFiveUsers(HttpServletRequest req) {
		List<Map> returnList = new ArrayList<Map>();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			returnList = calendarService.getNewFiveUsers(uinfo.getId());
		}
		return returnList;
	}

	/**
	 * 添加关注的用户
	 */
	public int addFocusedUsers(String userName, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.addFocusedUser(uinfo, userName);
	}

	/**
	 * 移除关注人
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeFocusedUsers(long id) {
		// Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.deleteRelation(id);
	}

	// public boolean testDate(Event dd){
	// System.out.println("test");
	// return true;
	// }
	/**
	 * 获得关注的人事件
	 */
	public HashMap getFocusedUserEvents(long id, HttpServletRequest req) {
		HashMap returnMap = new HashMap();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			// returnMap = calendarService.getEvents(id,null);
			returnMap = calendarService.getUserEvents(uinfo.getId(), id, null);
		}
		return returnMap;
	}

	/**
	 * 获得当前事件的邀请人
	 */
	public List<Map> getIniteersByEvent(long eventId) {
		List<Map> returnList = new ArrayList<Map>();
		// Users uinfo = (Users) req.getSession().getAttribute("userKey");
		returnList = calendarService.getInviteersByEvent(eventId);
		return returnList;
	}

	/**
	 * 添加事件邀请人
	 * 
	 * @return
	 */
	public boolean addEventInviteer(long eventId, String inviteerName) {
		return calendarService.addEventInviteer(eventId, inviteerName);
	}

	/**
	 * 移除事件邀请人
	 * 
	 * @param eventId
	 * @param inviteerName
	 * @return
	 */
	public boolean removeEventInviteer(long id) {
		return calendarService.removeEventInviteer(id);
	}

	public boolean validateInviteerName(String inviteerName) {
		return calendarService.validateInviteer(inviteerName);
	}

	/**
	 * 获得除自己以外的其他用户的用户名
	 */
	public List<String> getOtherUserName(HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.getOtherUserNameList(uinfo.getId());
	}

	/**
	 * 模糊查询用户信息
	 * 
	 * @param str
	 * @param req
	 * @return
	 */
	public List<String[]> searchUser(String str, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.searchUser(str, uinfo.getId());
	}

	/**
	 * 获取今日的日程
	 * 
	 * @param currentDate
	 * @return
	 */
	public List<Map> getTodayCalendar(HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		List<Map> returnList = new ArrayList<Map>();
		returnList = calendarService.getTodayCalendars(uinfo.getId());
		return returnList;
	}

	/**
	 * 更新用户日程设置
	 * 
	 * @param calendarSetting
	 * @return
	 */
	public boolean updateUserCalendarSet(boolean calendarSetting,
			HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		calendarService.updateCalendarSetting(uinfo, calendarSetting);
		return true;
	}

	/**
	 * 获取特定的共享人
	 * 
	 * @return
	 */
	public List<Map> getSharedPerson(HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.getSharedUsers(uinfo.getId());
	}

	/**
	 * 添加指定的共享人
	 * 
	 * @param req
	 * @return
	 */
	public int addSharedPerson(String userName, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.addSharedUser(uinfo, userName);
	}

	// /////////////////////////////////////修改///////////////////////////////////////////////////////
	/**
	 * 获得关注的其他人的日程
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getSharedFocusedUsers(HttpServletRequest req) {
		List<Map> returnList = new ArrayList<Map>();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			returnList = calendarService.getSharedFocusedUsers(uinfo.getId());
		}
		return returnList;
	}

	/**
	 * 添加关注的用户
	 */
	public int addSharedFocusedUsers(String userName, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.addSharedFocusedUser(uinfo, userName);
	}

	/**
	 * 移除关注人
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeSharedFocusedUsers(long id) {
		// Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.deleteSharedFocusedUser(id);
	}

	public boolean checkReadable(Long id, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.checkReadable(id, uinfo.getId());
	}

	/**
	 * 获得授权人的日程
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getEmpowerPerson(HttpServletRequest req) {
		List<Map> returnList = new ArrayList<Map>();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			returnList = calendarService.getEmpowerPerson(uinfo.getId());
		}
		return returnList;
	}

	/**
	 * 添加授权的用户
	 */
	public int addEmpowerPerson(String userName, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.addEmpowerPerson(uinfo, userName);

	}

	/**
	 * 移除授权的用户
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeEmpowerUser(long id) {
		// Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.deleteEmpower(id);
	}

	/**
	 * 获得授权给你修改日程的用户
	 * 
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getEmpoweredPerson(HttpServletRequest req) {
		List<Map> returnList = new ArrayList<Map>();
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			returnList = calendarService.getEmpoweredPerson(uinfo.getId());
		}
		return returnList;
	}

	/**
	 * 标记日程为已读
	 * 
	 * @param id
	 * @param req
	 * @return
	 */
	public boolean updateReadStatus(long id, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		return calendarService.updateReadStatus(id, uinfo.getId());
	}

	public int getInviterCompany(long userid) {
		Users user = calendarService.getUserById(userid);
		Company company = user.getCompany();
		return company.getId().intValue();
	}
	public boolean getUserPublic(long userid) {
		Users user = calendarService.getUserById(userid);
		boolean isPublic = user.getCalendarPublic();
		return isPublic;
	}
	public List<Map<String, Object>> getPublicUser(boolean exceptSelf,String userid,
			HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		try {
			Users user = (Users) req.getSession().getAttribute(
					PageConstant.LG_SESSION_USER);
			Map<String, Object> searchResultMap = calendarService.getPublicUser(user.getId());
			List<Users> searchResultList = (List<Users>) searchResultMap.get("searchResultList");
			List<Long> onlineIdList = (List<Long>) searchResultMap.get(PageConstant.LG_ONLINE_ID_LIST);

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
	public List<Map<String, Object>> searchPublicUsers(String parentID,
			boolean exceptSelf, String userid, String keyword,
			boolean isLeader, HttpServletRequest req) {
		List<Map<String, Object>> nodeMap = new ArrayList<Map<String, Object>>();
		Long UID = Long.valueOf(userid);
		Users user = calendarService.getUserById(UID);
		if (user != null) {
			try {
				Map<String, Object> searchResultMap = calendarService.getSearchPublicUser(UID,keyword);
				List<Users> searchResultList = (List<Users>) searchResultMap.get("searchResultList");
				List<Long> onlineIdList = (List<Long>) searchResultMap.get(PageConstant.LG_ONLINE_ID_LIST);
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
		} else
			return null;
	}
	public boolean addEventPublicer(long eventId, String publicerName) {
		return calendarService.addEventPublicer(eventId, publicerName);
	}
	public List<Map> getPublicersByEvent(long eventId) {
		List<Map> returnList = new ArrayList<Map>();
		returnList = calendarService.getPublicersByEvent(eventId);
		return returnList;
	}
	public boolean removeEventPublicer(long id) {
		return calendarService.removeEventPublicer(id);
	}
	
	// /////////////////////////////////////修改结束//////////////////////////////////////////////////////
}