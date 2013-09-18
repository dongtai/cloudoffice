package apps.transmanager.weboffice.service.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarEvent;
import apps.transmanager.weboffice.databaseobject.CalendarInviteer;

public class CalendarInviteerDAO extends BaseDAO{
	
	private static final Log log = LogFactory.getLog(CalendarInviteer.class);
	public  List<CalendarInviteer> getCalendarInviteers(long eventId)
	{
		log.debug("finding CalendarInviteer instance with eventId:" + eventId);
		try
		{
			String queryString = "from CalendarInviteer as model where model.calendarEvent.id  = ?";
			return findAllBySql(queryString, eventId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public  List<CalendarInviteer> getUnReadCalendarInviteers(long eventId)
	{
		log.debug("finding CalendarInviteer instance with eventId:" + eventId);
		try
		{
			String queryString = "from CalendarInviteer as model where model.calendarEvent.id  = ? and isRead = 0";
			return findAllBySql(queryString, eventId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public long save(CalendarInviteer transientInstance)
	{
		log.debug("saving CalendarInviteer instance");
		try
		{
			if (transientInstance.getId() == null)
			{
				super.save(transientInstance);
			}
			else
			{
				update(transientInstance);
			}
			return transientInstance.getId();
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}
	
	public void deleteRelation(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete CalendarInviteer where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public List<CalendarEvent> getSharedEvents(String name)
	{
		
		log.debug("finding CalendarEvent instance with eventId:" + name);
		try
		{
			String queryString = "select model.calendarEvent from CalendarInviteer as model where model.userName  = ?";
			return findAllBySql(queryString, name);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<CalendarEvent> getSharedEvents(String name,String stardate,String enddate)
	{
		
		log.debug("finding CalendarEvent instance with eventId:" + name);
		Date startdate1 = new Date(),endDate2 = new Date();
		
	    DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
	    try {
			startdate1=format.parse(stardate);
			endDate2=format.parse(enddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try
		{
			String queryString = "select model.calendarEvent from CalendarInviteer as model where model.userName  = ?and DATE_FORMAT(model.calendarEvent.startDate,'%Y-%m-%d') between ? and ? order by model.calendarEvent.startDate asc";
			return findAllBySql(queryString, name,stardate,enddate);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<CalendarEvent> getSharedEvents(String name,String nowdate)
	{
		log.debug("finding CalendarEvent instance with eventId:" + name);
		Date startdate1 = new Date(),endDate2 = new Date();
		
	    DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
	    try {
			startdate1=format.parse(nowdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try
		{
			String queryString = "select model.calendarEvent from CalendarInviteer as model where model.userName  = ?and DATE_FORMAT(model.calendarEvent.startDate,'%Y-%m-%d') = ? order by model.calendarEvent.startDate asc";
			return findAllBySql(queryString, name,nowdate);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CalendarInviteer> getNewFiveInviter(long userId) {
		log.debug("finding CalendarInviteer instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarInviteer as model where model.userId  = ? order by model.id desc";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public void updateinviteeRead(long eventId, Long userId) {
		log.debug("update by id");
		try
		{
			String queryString = "update CalendarInviteer set isRead = 1 where userId = " + userId + "  and calendarEvent.id = " +eventId;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("update failed", re);
			throw re;
		}
		
	}
}
