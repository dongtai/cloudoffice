package apps.transmanager.weboffice.service.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarEvent;



public class CalendarEventDAO extends BaseDAO{
	
	private static final Log log = LogFactory.getLog(CalendarEvent.class);
	
	/**
	 * 根据用户Id 活的日程事件
	 * @param userId
	 * @return
	 */
	public  List<CalendarEvent> getCalendarEvents(long userId)
	{
		log.debug("finding CalendarEvent instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarEvent as model where model.userinfo.id  = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public CalendarEvent save(CalendarEvent transientInstance)
	{
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
			return transientInstance;
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}
	
	public void deleteEvent(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete CalendarEvent where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public CalendarEvent findById(java.lang.Long id)
	{
		log.debug("getting CalendarEvent instance with id: " + id);
		try
		{
			CalendarEvent instance = (CalendarEvent) find("com.evermore.weboffice.databaseobject.CalendarEvent", id);
			return instance;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<CalendarEvent> getRemindCalender()
	{
		try
		{
			//select CEIL((unix_timestamp(now())-unix_timestamp(model.startDate))/60),model.id,model.reminder from CalendarEvent as model where model.id = 175 and model.reminder = CEIL((unix_timestamp(now())-unix_timestamp(model.startDate))/60);
			String queryString = "select model from CalendarEvent as model where CEIL((unix_timestamp(model.startDate)-unix_timestamp(now()))/60)>= 0 and CEIL((unix_timestamp(model.startDate)-unix_timestamp(now()))/60) <= model.reminder and isRead=0";
			return findAllBySql(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 移动端日程提醒
	 * @param userId
	 * @return
	 */
	public List<CalendarEvent> getRemindCalenderForMobile(long userId)
	{
		try
		{
			//select CEIL((unix_timestamp(now())-unix_timestamp(model.startDate))/60),model.id,model.reminder from CalendarEvent as model where model.id = 175 and model.reminder = CEIL((unix_timestamp(now())-unix_timestamp(model.startDate))/60);
			String queryString = "select model from CalendarEvent as model where model.userinfo.id = ? and CEIL((unix_timestamp(model.startDate)-unix_timestamp(now()))/60)>= 0 and CEIL((unix_timestamp(model.startDate)-unix_timestamp(now()))/60) <= model.reminder";
			return findAllBySql(queryString,userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	/**
	 * 获取今天的日程
	 * @param userId
	 * @return
	 */
	public List<CalendarEvent> getTodaysCalendar(long userId)
	{
		log.debug("finding CalendarEvent instance with userId:" + userId);
		try
		{
			String queryString = "select model from CalendarEvent as model where model.userinfo.id = ? and DATE_FORMAT(model.startDate,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d')";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	/**
	 * 获取某天的日程
	 * @param userId
	 * @return
	 */
	public List<CalendarEvent> getCalendarByDate(long userId,String date)
	{
		log.debug("finding CalendarEvent instance with userId:" + userId);
		try
		{
		 //   DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		     String queryString = "select model from CalendarEvent as model where model.userinfo.id = ? and DATE_FORMAT(model.startDate,'%Y-%m-%d')= '"+date+"'";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	/**
	 * 获取某端日期的日程
	 * @param userId
	 * @return
	 */
	public List<CalendarEvent> getCalendarByDurationDate(long userId,String stardate,String enddate)
	{
		log.debug("finding CalendarEvent instance with userId:" + userId);
		try
		{
			Date startdate1 = new Date(),endDate2 = new Date();
			
		    DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		    try {
				startdate1=format.parse(stardate);
				endDate2=format.parse(enddate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		     String queryString = "select model from CalendarEvent as model where model.userinfo.id = ? and model.startDate between ? and ? order by model.startDate asc";
			return findAllBySql(queryString, userId,startdate1,endDate2);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	/**
	 * 根据用户Id 活的日程事件 (分页)
	 * @param userId
	 * @return
	 */
	public  List<CalendarEvent> getCalendarEvents(long userId,int start, int length)
	{
		log.debug("finding CalendarEvent instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarEvent as model where model.userinfo.id  = ? order by model.startDate desc";
			return findAllBySql(start, length,queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public void updateRead(long eventId) {
		log.debug("update by id");
		try
		{
			String queryString = "update CalendarEvent set isRead = 1 where id = " + eventId;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
		
	}

	public List<CalendarEvent> checkCreateUser(long eventId, Long userId) {
		log.debug("finding CalendarEvent instance with userId:" + userId + " and eventId:" +eventId);
		try
		{
			String queryString = "from CalendarEvent as model where model.userinfo.id  = ? and id = ?";
			return findAllBySql(queryString,userId,eventId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CalendarEvent> getCalendarEvents(Long currentUID, Long systemUID) {
		log.debug("finding CalendarEvent instance with userId:" + currentUID);
		try
		{
			//String queryString = "select a from CalendarEvent as a, CalendarPublicer as b where a.userinfo.id  = ? and ( (a.userinfo.id  = ?) or (a.isPrivate = 0) or ((a.isPrivate = 1) and (a.id in ( select b.calendarEvent.id from b where b.userId = ?))))";
			String queryString = "from CalendarEvent as a where a.userinfo.id  = ? and ( (a.userinfo.id  = ?) or (a.isPrivate = 0) or (a.id in ( select b.calendarEvent.id from CalendarPublicer as b where b.userId = ?))))";
			//return findAllBySql(queryString, currentUID,systemUID,systemUID);
			return findAllBySql(queryString,currentUID,systemUID,systemUID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	
	
}
