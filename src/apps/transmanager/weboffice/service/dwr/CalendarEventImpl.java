package apps.transmanager.weboffice.service.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.CalendarEmpower;
import apps.transmanager.weboffice.databaseobject.CalendarEvent;
import apps.transmanager.weboffice.databaseobject.CalendarFocused;
import apps.transmanager.weboffice.databaseobject.CalendarInviteer;
import apps.transmanager.weboffice.databaseobject.CalendarPublicer;
import apps.transmanager.weboffice.databaseobject.CalendarRelation;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.dao.CalendarEmpowerDAO;
import apps.transmanager.weboffice.service.dao.CalendarEventDAO;
import apps.transmanager.weboffice.service.dao.CalendarFocusDAO;
import apps.transmanager.weboffice.service.dao.CalendarInviteerDAO;
import apps.transmanager.weboffice.service.dao.CalendarPublicerDAO;
import apps.transmanager.weboffice.service.dao.CalendarRelationDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.beans.PageConstant;

@Component(value=CalendarEventImpl.NAME)
public class CalendarEventImpl implements ICalendarService
{
	public final static String NAME = "calendarService";
	@Autowired
	private CalendarEventDAO calendarEventDAO;
	@Autowired
	private StructureDAO  structureDAO;
	@Autowired
	private CalendarRelationDAO  calendarRelationDAO;
	@Autowired
	private CalendarFocusDAO  calendarfocusedDAO;
	@Autowired
	private CalendarEmpowerDAO  calendarempowerDAO;
	public CalendarEmpowerDAO getCalendarempowerDAO() {
		return calendarempowerDAO;
	}

	public void setCalendarempowerDAO(CalendarEmpowerDAO calendarempowerDAO) {
		this.calendarempowerDAO = calendarempowerDAO;
	}

	@Autowired
	private CalendarInviteerDAO calendarInviteerDAO;
	@Autowired
	private CalendarPublicerDAO calendarpublicerDAO;
	public CalendarPublicerDAO getCalendarpublicerDAO() {
		return calendarpublicerDAO;
	}

	public void setCalendarpublicerDAO(CalendarPublicerDAO calendarpublicerDAO) {
		this.calendarpublicerDAO = calendarpublicerDAO;
	}

	@Autowired
	private MessagesService messagesService;
	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private IAddressListService addressListService;

	public IAddressListService getAddressListService() {
		return addressListService;
	}

	public void setAddressListService(IAddressListService addressListService) {
		this.addressListService = addressListService;
	}

	public IUserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public MessagesService getMessagesService() {
		return messagesService;
	}

	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	public CalendarInviteerDAO getCalendarInviteerDAO() {
		return calendarInviteerDAO;
	}

	public void setCalendarInviteerDAO(CalendarInviteerDAO calendarInviteerDAO) {
		this.calendarInviteerDAO = calendarInviteerDAO;
	}

	public CalendarRelationDAO getCalendarRelationDAO() {
		return calendarRelationDAO;
	}

	public void setCalendarRelationDAO(CalendarRelationDAO calendarRelationDAO) {
		this.calendarRelationDAO = calendarRelationDAO;
	}
	
	public CalendarFocusDAO getCalendarfocusedDAO() {
		return calendarfocusedDAO;
	}

	public void setCalendarfocusedDAO(CalendarFocusDAO calendarfocusedDAO) {
		this.calendarfocusedDAO = calendarfocusedDAO;
	}
	
	public StructureDAO getStructureDAO() {
		return structureDAO;
	}

	public void setStructureDAO(StructureDAO structureDAO) {
		this.structureDAO = structureDAO;
	}

	public CalendarEventDAO getCalendarEventDAO() {
		return calendarEventDAO;
	}

	public void setCalendarEventDAO(CalendarEventDAO calendarEventDAO) {
		this.calendarEventDAO = calendarEventDAO;
	}

	@Override
	public HashMap getEvents(long userId,String userName) {
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getCalendarEvents(userId);
		List<CalendarEvent> eventList2 = new ArrayList<CalendarEvent>();
		if(userName!=null){
		eventList2 = calendarInviteerDAO.getSharedEvents(userName);
		}
		List a  = new ArrayList();
		if(eventList!=null&&eventList.size()>0)
		{
			for(CalendarEvent ce:eventList)
			{
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("loc",ce.getLocation());
				m.put("rem", ce.getReminder());
				m.put("url",ce.getUrl());
				m.put("ts","测试");
				m.put("cn",ce.getUserinfo().getUserName());
				if(userName==null)
				{
				m.put("rd", true);
				}else{
				m.put("rd", false);
				}
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				if(userName!=null){
					a.add(m);
				}else{
						if(ce.getIsInvite()==null||ce.getIsInvite()==true)
						{
							a.add(m);
						}
					
					}
			}
		}
		if(eventList2!=null&&eventList2.size()>0)
		{
			for(CalendarEvent ce:eventList2)
			{
				if(ce.getIsInvite()!=null){
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("rem", ce.getReminder());
				m.put("loc",ce.getLocation());
				m.put("url",ce.getUrl());
				m.put("cn",ce.getUserinfo().getUserName());
				m.put("ts","测试");
				m.put("rd", true);
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				a.add(m);
				}
			}
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", a);
		return returnMap;
	}
	
	@Override
	public CalendarEvent saveCalendarEvent(CalendarEvent event){
		
		try
		{
			return calendarEventDAO.save(event);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	@Override
	public boolean deleteEvent(long id)
	{
		try
		{
			calendarEventDAO.deleteEvent(id);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	@Override
	public int addFocusedUser(Users uinfo,String userName){
		List<Users> list1 = structureDAO.findUserByProperty("userName",userName);
		
		if(list1.size()<1)
		{
			return 1;//用户不存在
		}
		if(list1.get(0).getCalendarPublic()==false){
			return 2;//用户日程未公开
		}else{
			CalendarRelation cr = new CalendarRelation();
			cr.setUserinfo(uinfo);
			cr.setRelativedUserId(list1.get(0).getId());
			cr.setRelativedUserName(list1.get(0).getUserName());
			cr.setRelativedRealName(list1.get(0).getRealName());
			calendarRelationDAO.save(cr);
			return 0;
		}
	}
	
	@Override
	public List<Map> getFocusedUsers(long userId) {
		List<Map>  returnList  =  new  ArrayList<Map>();
		List<CalendarRelation> relationList = calendarRelationDAO.getCalendarRelations(userId);
		if(relationList!=null&&relationList.size()>0)
		{
			for(CalendarRelation cr:relationList)
			{
				Users u= structureDAO.findUserById(cr.getRelativedUserId());
				//if(u.getCalendarPublic()){
					Map m = new HashMap();
					m.put("id", cr.getId());
					m.put("name",cr.getRelativedUserName());
					m.put("userId", cr.getRelativedUserId());
					m.put("realName",cr.getRelativedRealName());
					returnList.add(m);
				//}
			}
		}
		return returnList;
	}
	
	@Override
	public boolean deleteRelation(long id)
	{
		try
		{
			calendarRelationDAO.deleteRelation(id);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	@Override
	public List<Map> getInviteersByEvent(long  eventId)
	{
		List<Map>  returnList  =  new  ArrayList<Map>();
		List<CalendarInviteer> relationList = calendarInviteerDAO.getCalendarInviteers(eventId);
		if(relationList!=null&&relationList.size()>0)
		{
			for(CalendarInviteer ci:relationList)
			{
				Map m = new HashMap();
				m.put("id", ci.getId());
				m.put("name",ci.getUserName());
				m.put("eventId", eventId);
				m.put("realName", ci.getRealName());
				returnList.add(m);
			}
		}
		return returnList;
	}
	
	@Override
	public void dealEventInviteerCon(CalendarEvent ce,String userNameList)
	{
		if(userNameList!=null)
		{
			String[] inviteerNameList = userNameList.split(";");
			List oldlist=calendarInviteerDAO.findAllBySql("select a.userName from CalendarInviteer as a where a.calendarEvent.id=? ", ce.getId());
			for(int i = 0; i<inviteerNameList.length;i++)
			{
				String currentInviteerName = inviteerNameList[i];
				if(currentInviteerName!=null&&!currentInviteerName.equals("")){
					List<Users> list1 = structureDAO.findUserByProperty("userName",currentInviteerName);
					Long count=calendarInviteerDAO.getCountBySql("select count(a.id) from CalendarInviteer as a where a.calendarEvent.id=? and a.userId=? ",ce.getId(),list1.get(0).getId());
					if (count==null || count.longValue()==0)
					{
						CalendarInviteer  ci = new CalendarInviteer();
						ci.setUserName(inviteerNameList[i]);
						ci.setCalendarEvent(ce);
						ci.setRealName(list1.get(0).getRealName());
						ci.setUserId(list1.get(0).getId());
						calendarInviteerDAO.save(ci);
					}
				}
			}
			if (oldlist!=null && oldlist.size()>0)
			{//以下是处理删除的邀请人
				for (int i=0;i<oldlist.size();i++)
				{
					String username=(String)oldlist.get(i);
					boolean isdel=true;
					for (int j=0;j<inviteerNameList.length;j++)
					{
						if (username.equals(inviteerNameList[j]))
						{
							isdel=false;
							break;
						}
					}
					if (isdel)
					{
						calendarInviteerDAO.excute("delete from CalendarInviteer as a where a.calendarEvent.id=? and a.userName=?",ce.getId(),username);
					}
				}
			}
		}
	}
	public void dealEventInviteer(CalendarEvent ce,String userNameList)
	{
		if(userNameList!=null)
		{
			String[] inviteerNameList = userNameList.split(";");
			for(int i = 0; i<inviteerNameList.length;i++)
			{
				String currentInviteerName = inviteerNameList[i];
				if(currentInviteerName!=null&&!currentInviteerName.equals("")){
					List<Users> list1 = structureDAO.findUserByProperty("userName",currentInviteerName);
					Long count=calendarInviteerDAO.getCountBySql("select count(a.id) from CalendarInviteer as a where a.calendarEvent.id=? and a.userId=? ",ce.getId(),list1.get(0).getId());
					if (count==null || count.longValue()==0)
					{
						CalendarInviteer  ci = new CalendarInviteer();
						ci.setUserName(inviteerNameList[i]);
						ci.setCalendarEvent(ce);
						ci.setRealName(list1.get(0).getRealName());
						ci.setUserId(list1.get(0).getId());
						calendarInviteerDAO.save(ci);
					}
				}
			}
		}
	}
	@Override
	public boolean addEventInviteer(long eventId,String inviteerName)
	{
		List<Users> list1 = structureDAO.findUserByProperty("userName",inviteerName);
		if(list1.size()<1)
		{
			return false;
		}else{
			CalendarInviteer  ci = new CalendarInviteer();
			ci.setUserName(inviteerName);
			ci.setCalendarEvent(calendarEventDAO.findById(eventId));
			ci.setRealName(list1.get(0).getRealName());
			ci.setUserId(list1.get(0).getId());
			calendarInviteerDAO.save(ci);
			return true;
		}
	}
	
	@Override
	public boolean removeEventInviteer(long id)
	{
		try
		{
			calendarInviteerDAO.deleteRelation(id);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	@Override
	public boolean validateInviteer(String inviteerName)
	{
		List<Users> list1 = structureDAO.findUserByProperty("userName",inviteerName);
		if(list1.size()<1)
		{
			return false;
		}else{
			return true;
		}
	}
	public List<String[]> searchUser(String str,Long userId)
	{
		Long orgId = addressListService.getRootGroupByUserId(userId);
		List<String[]> rs = new ArrayList<String[]>();
		List<Users> l = structureDAO.searchUser(str,userId,orgId);
		for(int i = 0;i<l.size();i++)
		{
			String[] temp=new String[2];
			temp[0]=l.get(i).getUserName()+"("+l.get(i).getRealName()+")";
			temp[1]=String.valueOf(l.get(i).getId());
			rs.add(temp);
		}
		return rs;
	}
	@Override
	public List<String> getOtherUserNameList(long userId)
	{
		List<String> rs = new ArrayList<String>();
		List<Users> l = structureDAO.getAllUsersButSelf(userId);
		for(int i = 0;i<l.size();i++)
		{
			rs.add(l.get(i).getUserName()+"("+l.get(i).getRealName()+")");
		}
		return rs;
	}
	
	@Override
	public void sendCalendarMessage(){
		
		List<Long> userIds = new ArrayList<Long>();
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getRemindCalender();
		//System.out.println(eventList.size());
		for(CalendarEvent e:eventList)
		{
			userIds.add(e.getUserinfo().getId());
			List<CalendarInviteer>  calendarInviteerList =	calendarInviteerDAO.getUnReadCalendarInviteers(e.getId());
			for(CalendarInviteer c : calendarInviteerList)
			{
				userIds.add(c.getUserId());
			}
			String returnContent ="$"+e.getId()+"$活动-【"+e.getTitle()+"】将在"+e.getStartDate().toLocaleString()+"开始";
			messagesService.sendMessage(returnContent, 3, userIds);
		}
		
	}
	/**
	 * mobile
	 */
	@Override
	public HashMap getAlertCalendarsEvent(long userId){
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getRemindCalenderForMobile(userId);
		List<Map>  returnList  =  new  ArrayList<Map>();
		for(CalendarEvent ce: eventList)
		{
			HashMap m = new HashMap();
			m.put("id", ce.getId());
			m.put("cid",ce.getCalendarId());
			m.put("title",ce.getTitle());
			m.put("start",ce.getStartDate());
			m.put("end",ce.getEndDate());
			m.put("ad",ce.getIsAllDay());
			m.put("notes",ce.getNotes());
			m.put("rem", ce.getReminder());
			m.put("loc",ce.getLocation());
			m.put("url",ce.getUrl());
			m.put("cn",ce.getUserinfo().getUserName());
			m.put("ts","测试");
			m.put("isRead", ce.getIsRead());
			m.put("rd", true);
			m.put("ip",ce.getIsInvite());
			m.put("rp",ce.getIsPrivate());
			returnList.add(m);			
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", returnList);
		return returnMap;
	}
	@Override
	public List<Map> getTodayCalendars(long userId){
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getTodaysCalendar(userId);
		List<Map>  returnList  =  new  ArrayList<Map>();
		for(CalendarEvent e: eventList)
		{
			Map m = new HashMap();
			m.put("title",e.getTitle());
			m.put("start",e.getStartDate());
			m.put("end",e.getEndDate());
			m.put("notes",e.getNotes());
			m.put("loc", e.getLocation());
			returnList.add(m);			
		}
		return returnList;
	}
	
	@Override
	public HashMap getTodayCalendarsEvent(long userId){
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getTodaysCalendar(userId);
//		for(CalendarEvent ca: eventList){
//			
//		}
		List<Map>  returnList  =  new  ArrayList<Map>();
		for(CalendarEvent ce: eventList)
		{
			HashMap m = new HashMap();
			m.put("id", ce.getId());
			m.put("cid",ce.getCalendarId());
			m.put("title",ce.getTitle());
			m.put("start",ce.getStartDate());
			m.put("end",ce.getEndDate());
			m.put("ad",ce.getIsAllDay());
			m.put("notes",ce.getNotes());
			m.put("rem", ce.getReminder());
			m.put("loc",ce.getLocation());
			m.put("url",ce.getUrl());
			m.put("cn",ce.getUserinfo().getUserName());
			m.put("ts","测试");
			m.put("rd", true);
			m.put("ip",ce.getIsInvite());
			m.put("rp",ce.getIsPrivate());
			returnList.add(m);			
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", returnList);
		return returnMap;
	}
	
	@Override
	public void updateCalendarSetting(Users user,boolean calendarSetting)
	{
		userDAO.updateCalendarSetting(user, calendarSetting);
		//user.setCalendarPublic(calendarSetting);
		//userDAO.saveOrUpadate(user);
	}
	@Override
	public List<Map> getSharedUsers(long userId){
		List<Map>  returnList  =  new  ArrayList<Map>();
		List<CalendarRelation> relationList = calendarRelationDAO.getCalendarRelations2(userId);
		if(relationList!=null&&relationList.size()>0)
		{
			for(CalendarRelation cr:relationList)
			{
				Users u= structureDAO.findUserById(cr.getRelativedUserId());
					Map m = new HashMap();
					m.put("id", cr.getId());
					m.put("name",cr.getUserinfo().getUserName());
					m.put("userId", cr.getRelativedUserId());
					m.put("realName",cr.getUserinfo().getRealName());
					returnList.add(m);
			}
		}
		return returnList;
	}
	  @Override
	public int 	addSharedUser(Users uinfo,String userName){
		  List<Users> list1 = structureDAO.findUserByProperty("userName",userName);
			
			if(list1.size()<1)
			{
				return 1;//用户不存在
			}else{
				CalendarRelation cr = new CalendarRelation();
				cr.setUserinfo(list1.get(0));
				cr.setRelativedUserId(uinfo.getId());
				cr.setRelativedUserName(uinfo.getUserName());
				cr.setRelativedRealName(uinfo.getRealName());
				calendarRelationDAO.save(cr);
				return 0;
			}
	  }

	  @Override
		public HashMap getCalendarsByDate(long userId,String userName,String date){
			List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
			eventList = calendarEventDAO.getCalendarByDate(userId, date);
			List<CalendarEvent> eventList2 = new ArrayList<CalendarEvent>();
			if(userName!=null){
			eventList2 = calendarInviteerDAO.getSharedEvents(userName,date);
			}
			List a  = new ArrayList();
			if(eventList!=null&&eventList.size()>0)
			{
				for(CalendarEvent ce:eventList)
				{
					HashMap m = new HashMap();
					m.put("id", ce.getId());
					m.put("cid",ce.getCalendarId());
					m.put("title",ce.getTitle());
					m.put("start",ce.getStartDate());
					m.put("end",ce.getEndDate());
					m.put("ad",ce.getIsAllDay());
					m.put("notes",ce.getNotes());
					m.put("loc",ce.getLocation());
					m.put("rem", ce.getReminder());
					m.put("url",ce.getUrl());
					m.put("ts","测试");
					m.put("cn",ce.getUserinfo().getUserName());
					if(userName==null)
					{
					m.put("rd", true);
					}else{
					m.put("rd", false);
					}
					m.put("ip",ce.getIsInvite());
					m.put("rp",ce.getIsPrivate());
					if(userName!=null){
						a.add(m);
					}else{
							if(ce.getIsInvite()==null||ce.getIsInvite()==true)
							{
								a.add(m);
							}
						
						}
				}
			}
			if(eventList2!=null&&eventList2.size()>0)
			{
				for(CalendarEvent ce:eventList2)
				{
					if(ce.getIsInvite()!=null){
					HashMap m = new HashMap();
					m.put("id", ce.getId());
					m.put("cid",ce.getCalendarId());
					m.put("title",ce.getTitle());
					m.put("start",ce.getStartDate());
					m.put("end",ce.getEndDate());
					m.put("ad",ce.getIsAllDay());
					m.put("notes",ce.getNotes());
					m.put("rem", ce.getReminder());
					m.put("loc",ce.getLocation());
					m.put("url",ce.getUrl());
					m.put("cn",ce.getUserinfo().getUserName());
					m.put("ts","测试");
					m.put("rd", true);
					m.put("ip",ce.getIsInvite());
					m.put("rp",ce.getIsPrivate());
					a.add(m);
					}
				}
			}
			HashMap returnMap = new HashMap();
			returnMap.put("evts", a);
			return returnMap;
		}

	@Override
	public HashMap getEvents(long userId, String userName, int start, int length) {
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getCalendarEvents(userId,start,length);
		List<CalendarEvent> eventList2 = new ArrayList<CalendarEvent>();
		if(userName!=null){
		eventList2 = calendarInviteerDAO.getSharedEvents(userName);
		}
		List a  = new ArrayList();
		if(eventList!=null&&eventList.size()>0)
		{
			for(CalendarEvent ce:eventList)
			{
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("loc",ce.getLocation());
				m.put("rem", ce.getReminder());
				m.put("url",ce.getUrl());
				m.put("ts","测试");
				m.put("cn",ce.getUserinfo().getUserName());
				if(userName==null)
				{
				m.put("rd", true);
				}else{
				m.put("rd", false);
				}
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				if(userName!=null){
					a.add(m);
				}else{
						if(ce.getIsInvite()==null||ce.getIsInvite()==true)
						{
							a.add(m);
						}
					
					}
			}
		}
		if(eventList2!=null&&eventList2.size()>0)
		{
			for(CalendarEvent ce:eventList2)
			{
				if(ce.getIsInvite()!=null){
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("rem", ce.getReminder());
				m.put("loc",ce.getLocation());
				m.put("url",ce.getUrl());
				m.put("cn",ce.getUserinfo().getUserName());
				m.put("ts","测试");
				m.put("rd", true);
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				a.add(m);
				}
			}
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", a);
		return returnMap;
	}

	@Override
	public HashMap getCalendarsDurationDate(long userId, String date1,
			String date2) {
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getCalendarByDurationDate(userId, date1, date2);
		List<Map>  returnList  =  new  ArrayList<Map>();
		for(CalendarEvent ce: eventList)
		{
			HashMap m = new HashMap();
			m.put("id", ce.getId());
			m.put("title",ce.getTitle());
			m.put("start",ce.getStartDate());
//			m.put("cid",ce.getCalendarId());
//			m.put("end",ce.getEndDate());
//			m.put("ad",ce.getIsAllDay());
//			m.put("notes",ce.getNotes());
//			m.put("rem", ce.getReminder());
//			m.put("loc",ce.getLocation());
//			m.put("url",ce.getUrl());
//			m.put("cn",ce.getUserinfo().getUserName());
//			m.put("ts","测试");
//			m.put("rd", true);
//			m.put("ip",ce.getIsInvite());
			returnList.add(m);			
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", returnList);
		return returnMap;
	}

	@Override
	public HashMap getCalendarsOfDates(long userId,String userName,String date1, String date2) {
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
		eventList = calendarEventDAO.getCalendarByDurationDate(userId, date1, date2);
		List<CalendarEvent> eventList2 = new ArrayList<CalendarEvent>();
		if(userName!=null){
		eventList2 = calendarInviteerDAO.getSharedEvents(userName,date1,date2);
		}
		List returnList  = new ArrayList();
		if(eventList!=null&&eventList.size()>0)
		{
			for(CalendarEvent ce:eventList)
			{
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("loc",ce.getLocation());
				m.put("rem", ce.getReminder());
				m.put("url",ce.getUrl());
				m.put("ts","测试");
				m.put("cn",ce.getUserinfo().getUserName());
				if(userName==null)
				{
				m.put("rd", true);
				}else{
				m.put("rd", false);
				}
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				if(userName!=null){
					returnList.add(m);
				}else{
						if(ce.getIsInvite()==null||ce.getIsInvite()==true)
						{
							returnList.add(m);
						}
					
					}
			}
		}
		if(eventList2!=null&&eventList2.size()>0)
		{
			for(CalendarEvent ce:eventList2)
			{
				if(ce.getIsInvite()!=null){
				HashMap m = new HashMap();
				m.put("id", ce.getId());
				m.put("cid",ce.getCalendarId());
				m.put("title",ce.getTitle());
				m.put("start",ce.getStartDate());
				m.put("end",ce.getEndDate());
				m.put("ad",ce.getIsAllDay());
				m.put("notes",ce.getNotes());
				m.put("rem", ce.getReminder());
				m.put("loc",ce.getLocation());
				m.put("url",ce.getUrl());
				m.put("cn",ce.getUserinfo().getUserName());
				m.put("ts","测试");
				m.put("rd", true);
				m.put("ip",ce.getIsInvite());
				m.put("rp",ce.getIsPrivate());
				returnList.add(m);
				}
			}
		}
		HashMap returnMap = new HashMap();
		returnMap.put("evts", returnList);
		return returnMap;
	}
	
	public int getCalendarCount(long userId){
		List<CalendarEvent> calendarEventsList = new ArrayList<CalendarEvent>();
		try {
			Users userinfo = userDAO.findById(Users.class.getName(),userId);
			calendarEventsList = calendarEventDAO.findByProperty(CalendarEvent.class.getName(),"userinfo",userinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendarEventsList.size();
	}

	@Override
	public List<Users> getCalendarPublic(long userId) {
		List<Users> usersList = null;
		usersList = structureDAO.getAllUsersPublic(userId);
		return usersList;
	}
/*
 * for moblie
 */
	@Override
	public List<CalendarInviteer> getInviteersByEvents(long eventId) {
			return calendarInviteerDAO.getCalendarInviteers(eventId);
	}

	@Override
	public List<Map> getNewFiveUsers(long userId) {
		List<Map>  returnList  =  new  ArrayList<Map>();
		List<CalendarInviteer> calendarinviteerList = calendarInviteerDAO.getNewFiveInviter(userId);
		List<Users> userlist = new ArrayList<Users>();
		if(calendarinviteerList!=null&&calendarinviteerList.size()>0)
		{
			int count=0;
			for(CalendarInviteer cr:calendarinviteerList)
			{
				if((!userlist.contains(cr.getCalendarEvent().getUserinfo()))&&(count<=4)){
					userlist.add(cr.getCalendarEvent().getUserinfo());
					Map m = new HashMap();
					m.put("id", cr.getId());
					m.put("name",cr.getCalendarEvent().getUserinfo().getUserName());
					m.put("userId", cr.getCalendarEvent().getUserinfo().getId());
					m.put("realName",cr.getCalendarEvent().getUserinfo().getRealName());
					System.out.println("*************"+cr.getCalendarEvent().getUserinfo().getRealName()+"********************");
					returnList.add(m);
					count++;
				}
			}
		}
		else System.out.println("*************null********************");
		return returnList;
	}
///////////////////////////////////////修改///////////////////////////////////////////////////////
	
@Override
public int addSharedFocusedUser(Users uinfo, String userName){
List<Users> list1 = structureDAO.findUserByProperty("userName",userName);
	
	if(list1.size()<1)
	{
		return 1;//用户不存在
	}else{
		CalendarFocused cr = new CalendarFocused();
		cr.setUserinfo(uinfo);
		cr.setFocusedUserId(list1.get(0).getId());
		cr.setFocusedUserName(list1.get(0).getUserName());
		cr.setFocusedRealName(list1.get(0).getRealName());
		calendarfocusedDAO.save(cr);
		return 0;
	}
	
}
@Override
public boolean deleteSharedFocusedUser(long id){
	try
	{
		calendarfocusedDAO.deleteFocus(id);
		return true;
	}
	catch (Exception e)
	{
		return false;
	}
	
}


@Override
public List<Map> getSharedFocusedUsers(Long userId) {
	List<Map>  returnList  =  new  ArrayList<Map>();
	List<CalendarFocused> focusList = calendarfocusedDAO.getCalendarFocus(userId);
	if(focusList!=null&&focusList.size()>0)
	{
		for(CalendarFocused cr:focusList)
		{
			//Users u= structureDAO.findUserById(cr.getFocusedUserId());
			//if(u.getCalendarPublic()){
				Map m = new HashMap();
				m.put("id", cr.getId());
				m.put("name",cr.getFocusedUserName());
				m.put("userId", cr.getFocusedUserId());
				m.put("realName",cr.getFocusedRealName());
				returnList.add(m);
			//}
		}
	}
	return returnList;
}

@Override
public boolean checkReadable(Long readUserId, Long CurrentUserId) {
	boolean flag = false;
	Users user = structureDAO.findUserById(readUserId);
	if(user.getCalendarPublic())flag = true;
	else{
	try
	{
		
		List<CalendarRelation> relationlist = calendarRelationDAO.checkRelation(readUserId,CurrentUserId);
		//System.out.println("*****************"+relationlist.size()+"*******************");
		if(relationlist!=null&&relationlist.size()>0){
			flag = true;
		}
		else flag = false;
	}
	catch (Exception e)
	{
		return false;
	 }
	}
	return flag;
}

@Override
public List<Map> getEmpowerPerson(Long id) {
	List<Map>  returnList  =  new  ArrayList<Map>();
	List<CalendarEmpower> empowerList = calendarempowerDAO.getCalendarEmpower(id);
	if(empowerList!=null&&empowerList.size()>0)
	{
		for(CalendarEmpower cr:empowerList)
		{
			Users u= structureDAO.findUserById(cr.getEmpoweredUserId());
			//if(u.getCalendarPublic()){
				Map m = new HashMap();
				m.put("id", cr.getId());
				m.put("name",cr.getEmpoweredUserName());
				m.put("userId", cr.getEmpoweredUserId());
				m.put("realName",cr.getEmpoweredRealName());
				returnList.add(m);
			//}
		}
	}
	return returnList;
}

@Override
public int addEmpowerPerson(Users uinfo, String userName) {
List<Users> list1 = structureDAO.findUserByProperty("userName",userName);
	
	if(list1.size()<1)
	{
		return 1;//用户不存在
	}else{
		CalendarEmpower cr = new CalendarEmpower();
		cr.setUserinfo(uinfo);
		cr.setEmpoweredUserId(list1.get(0).getId());
		cr.setEmpoweredUserName(list1.get(0).getUserName());
		cr.setEmpoweredRealName(list1.get(0).getRealName());
		calendarempowerDAO.save(cr);
		return 0;
	}
}

@Override
public boolean deleteEmpower(long id) {
	try
	{
		calendarempowerDAO.deleteEmpower(id);
		return true;
	}
	catch (Exception e)
	{
		return false;
	}
}

@Override
public List<Map> getEmpoweredPerson(Long id) {
	List<Map>  returnList  =  new  ArrayList<Map>();
	List<CalendarEmpower> empoweredPersonList = calendarempowerDAO.getCalendarEmpoweredPerson(id);
	if(empoweredPersonList!=null&&empoweredPersonList.size()>0)
	{
		for(CalendarEmpower cr:empoweredPersonList)
		{
			//Users u= structureDAO.findUserById(cr.getUserinfo().getId());
			//if(u.getCalendarPublic()){
				Map m = new HashMap();
				m.put("id", cr.getId());
				m.put("name",cr.getUserinfo().getUserName());
				m.put("userId", cr.getUserinfo().getId());
				m.put("realName",cr.getUserinfo().getRealName());
				returnList.add(m);
			//}
		}
	}
	return returnList;
}

@Override
public Users getCalendarUser(Long userid) {
	Users u= structureDAO.findUserById(userid);
	return u;
}

@Override
public HashMap getUserEvents(Long SystemUID, Long currentUID, String currentName) {
	List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
	//eventList = calendarEventDAO.getCalendarEvents(currentUID);
	eventList = calendarEventDAO.getCalendarEvents(currentUID,SystemUID);
	List<CalendarEvent> eventList2 = new ArrayList<CalendarEvent>();
	if(currentName!=null){
	eventList2 = calendarInviteerDAO.getSharedEvents(currentName);
	}
	List a  = new ArrayList();
	if(eventList!=null&&eventList.size()>0)
	{
		for(CalendarEvent ce:eventList)
		{
			HashMap m = new HashMap();
			m.put("id", ce.getId());
			m.put("cid",ce.getCalendarId());
			m.put("title",ce.getTitle());
			m.put("start",ce.getStartDate());
			m.put("end",ce.getEndDate());
			m.put("ad",ce.getIsAllDay());
			m.put("notes",ce.getNotes());
			m.put("loc",ce.getLocation());
			m.put("rem", ce.getReminder());
			m.put("url",ce.getUrl());
			m.put("ts","测试");
			m.put("cn",ce.getUserinfo().getUserName());
			if (ce.getPoweruserinfo()!=null)
			{
				m.put("emper",ce.getPoweruserinfo().getUserName());
			}
			else
			{
				m.put("emper",ce.getUserinfo().getUserName());//授权人帮填，默认为空，显示自己
			}
			List<CalendarEmpower> empoweredPersonList  = calendarempowerDAO.getRecord(SystemUID,currentUID);
			boolean flag = false;
			if(empoweredPersonList.size()>0)flag=true;
			if((currentName==null)&&(flag==false))
			{
			m.put("rd", true);
			}else{
			m.put("rd", false);
			}
			m.put("ip",ce.getIsInvite());
			m.put("rp",ce.getIsPrivate());
				a.add(m);
		}
	}
	if(eventList2!=null&&eventList2.size()>0)
	{
		for(CalendarEvent ce:eventList2)
		{
			if(ce.getIsInvite()!=null){
			HashMap m = new HashMap();
			m.put("id", ce.getId());
			m.put("cid",ce.getCalendarId());
			m.put("title",ce.getTitle());
			m.put("start",ce.getStartDate());
			m.put("end",ce.getEndDate());
			m.put("ad",ce.getIsAllDay());
			m.put("notes",ce.getNotes());
			m.put("rem", ce.getReminder());
			m.put("loc",ce.getLocation());
			m.put("url",ce.getUrl());
			m.put("cn",ce.getUserinfo().getUserName());
			if (ce.getPoweruserinfo()!=null)
			{
				m.put("emper",ce.getPoweruserinfo().getUserName());
			}
			else
			{
				m.put("emper",ce.getUserinfo().getUserName());
			}

			m.put("ts","测试");
			m.put("rd", true);
			m.put("ip",ce.getIsInvite());
			m.put("rp",ce.getIsPrivate());
			a.add(m);
			}
		}
	}
	HashMap returnMap = new HashMap();
	returnMap.put("evts", a);
	return returnMap;
}

@Override
public boolean updateReadStatus(long eventId, Long userId) {
	List<CalendarEvent> eList = calendarEventDAO.checkCreateUser(eventId,userId);
	boolean creatorFlag = false;
	if(eList.size()>0) creatorFlag = true;
	if(creatorFlag){
	try
	{
		calendarEventDAO.updateRead(eventId);
		return true;
	}
	catch (Exception e)
	{
		return false;
	}
	}
	else{
		try
		{
			calendarInviteerDAO.updateinviteeRead(eventId,userId);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
public Users getUserById(long userid){
	return structureDAO.findUserById(userid);
}

@Override
public Map<String, Object> getPublicUser(Long id) {
	Map<String, Object> searchResultMap = new HashMap<String, Object>();
	List<Users> returnList = new ArrayList<Users>();
	List<CalendarRelation>  relationList  = calendarRelationDAO.getPubicerByUserId(id);
	if(relationList!=null&&relationList.size()>0)
	{
		for(CalendarRelation cr:relationList)
		{
			Users u= cr.getUserinfo();
			returnList.add(u);
			
		}
	}
	searchResultMap.put("searchResultList", returnList);
	searchResultMap.put(PageConstant.LG_ONLINE_ID_LIST, returnList);
	return searchResultMap;
}

@Override
public Map<String, Object> getSearchPublicUser(Long id, String keyword) {
	Map<String, Object> searchResultMap = new HashMap<String, Object>();
	List<Users> returnList = new ArrayList<Users>();
	List<CalendarRelation>  relationList  = calendarRelationDAO.getSearchPublicUser(id,keyword);
	if(relationList!=null&&relationList.size()>0)
	{
		for(CalendarRelation cr:relationList)
		{
			Users u= cr.getUserinfo();
			returnList.add(u);
			
		}
	}
	searchResultMap.put("searchResultList", returnList);
	searchResultMap.put(PageConstant.LG_ONLINE_ID_LIST, returnList);
	return searchResultMap;
}

@Override
public void dealEventPublicer(CalendarEvent ce, String userNameList) {
	
	if(userNameList!=null)
	{
		String[] publicNameList = userNameList.split(";");
		for(int i = 0; i<publicNameList.length;i++)
		{
			String currentpublicerName = publicNameList[i];
			if(currentpublicerName!=null&&!currentpublicerName.equals("")){
				List<Users> list1 = structureDAO.findUserByProperty("userName",currentpublicerName);
				Long count=calendarpublicerDAO.getCountBySql("select count(a.id) from CalendarPublicer as a where a.calendarEvent.id=? and a.userId=? ",ce.getId(),list1.get(0).getId());
				if (count==null || count.longValue()==0)
				{
					CalendarPublicer  ci = new CalendarPublicer();
					ci.setUserName(publicNameList[i]);
					ci.setCalendarEvent(ce);
					ci.setRealName(list1.get(0).getRealName());
					ci.setUserId(list1.get(0).getId());
					calendarpublicerDAO.save(ci);
				}
			}
		}
	}
	
}

@Override
public boolean addEventPublicer(long eventId,String publicerName)
{
	List<Users> list1 = structureDAO.findUserByProperty("userName",publicerName);
	if(list1.size()<1)
	{
		return false;
	}else{
		CalendarPublicer  ci = new CalendarPublicer();
		ci.setUserName(publicerName);
		ci.setCalendarEvent(calendarEventDAO.findById(eventId));
		ci.setRealName(list1.get(0).getRealName());
		ci.setUserId(list1.get(0).getId());
		calendarpublicerDAO.save(ci);
		return true;
	}
}
@Override
public List<Map> getPublicersByEvent(long  eventId)
{
	List<Map> returnList  =  new  ArrayList<Map>();
	List<CalendarPublicer> relationList = calendarpublicerDAO.getCalendarPublicers(eventId);
	if(relationList!=null&&relationList.size()>0)
	{
		for(CalendarPublicer ci:relationList)
		{
			Map m = new HashMap();
			m.put("id", ci.getId());
			m.put("name",ci.getUserName());
			m.put("eventId", eventId);
			m.put("realName", ci.getRealName());
			returnList.add(m);
		}
	}
	return returnList;
}


@Override
public boolean removeEventPublicer(long id) {
	try
	{
		calendarpublicerDAO.deletePublicer(id);
		return true;
	}
	catch (Exception e)
	{
		return false;
	}
}
///////////////////////////////////////修改结束//////////////////////////////////////////////////////
}
