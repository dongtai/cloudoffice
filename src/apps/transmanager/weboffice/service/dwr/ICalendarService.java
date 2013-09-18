package apps.transmanager.weboffice.service.dwr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.CalendarEvent;
import apps.transmanager.weboffice.databaseobject.CalendarInviteer;
import apps.transmanager.weboffice.databaseobject.Users;


public interface ICalendarService {
	
	/**
	 * 根据当前登录用户 ，加载日程事件
	 * @param userinfo
	 * @return
	 */
	public HashMap getEvents(long userId,String userName);
	/*
	 * moblie getEvent 
	 */
	public HashMap getEvents(long userId,String userName,int start,int length);

	public CalendarEvent saveCalendarEvent(CalendarEvent event);
	
	public boolean deleteEvent(long id);
	
	public int addFocusedUser(Users uinfo,String userName);
	public List<Map> getFocusedUsers(long userId);
	public List<Map> getNewFiveUsers(long userId);
	public boolean deleteRelation(long id);
	
	public List<Map> getInviteersByEvent(long  eventId);
	public void dealEventInviteer(CalendarEvent ce,String userNameList);
	public void dealEventInviteerCon(CalendarEvent ce,String userNameList);
	public boolean addEventInviteer(long eventId,String inviteerName);
	public boolean removeEventInviteer(long id);
	public boolean validateInviteer(String inviteerName);
	public List<String[]> searchUser(String str,Long userId);
	public List<String> getOtherUserNameList(long userId);
	
	/*
	 * for moblie
	 */
	public List<CalendarInviteer> getInviteersByEvents(long  eventId);

	/**
	 * 发送日程消息提醒
	 */
	public void sendCalendarMessage();
	/**
	 * moblie get alertEvent
	 */
	public HashMap getAlertCalendarsEvent(long userId);
	/**
	 * moblie get calendarEvent between and startdate1,startdate2
	 */
	public HashMap getCalendarsDurationDate(long userId,String date1,String date2);
	public HashMap getCalendarsOfDates(long userId,String userName,String date1,String date2);

	/**
	 * 获取今天的日程
	 * @param userId
	 * @return
	 */
	public List<Map> getTodayCalendars(long userId);
	/*
	 * getevent by date
	 */
	public HashMap getCalendarsByDate(long userId,String username,String date);

	/**
	 * 获取今天的日程,mobile
	 * @param userId
	 * @return
	 */
	public HashMap getTodayCalendarsEvent(long userId);
	
	/**
	 * 更新日程设置
	 * @param calendarSetting
	 */
	public void updateCalendarSetting(Users user,boolean calendarSetting);
	/**
	 * 获取特定的共享人
	 * @param userId
	 * @return
	 */
	public List<Map> getSharedUsers(long userId);
	/**
	 * 添加指定共享的人
	 * @param uinfo 当前用户
	 * @param userName 被共享的人
	 * @return
	 */
    public int 	addSharedUser(Users uinfo,String userName);
	/*
	 * get calendarEventCount for moblie
	 * 
	 */
    public int getCalendarCount(long userId);
    public List<Users> getCalendarPublic(long userId);
///////////////////////////////////////修改///////////////////////////////////////////////////////

public int addSharedFocusedUser(Users uinfo, String userName);
public boolean deleteSharedFocusedUser(long id);
public List<Map> getSharedFocusedUsers(Long id);
public boolean checkReadable(Long readUserId, Long CurrentUserId);
public List<Map> getEmpowerPerson(Long id);
public int addEmpowerPerson(Users uinfo, String userName);
public boolean deleteEmpower(long id);
public List<Map> getEmpoweredPerson(Long id);
public Users getCalendarUser(Long userid);
public HashMap getUserEvents(Long SystemUID, Long currentUID, String currentName);
public boolean updateReadStatus(long eventId, Long userId);
public Users getUserById(long id);
public Map<String, Object> getPublicUser(Long id);
public Map<String, Object> getSearchPublicUser(Long uID, String keyword);
public void dealEventPublicer(CalendarEvent ce, String userNameList);
public boolean addEventPublicer(long eventId, String publicerName);
public List<Map> getPublicersByEvent(long eventId);
public boolean removeEventPublicer(long id);

///////////////////////////////////////修改结束//////////////////////////////////////////////////////
}
