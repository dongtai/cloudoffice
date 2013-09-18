package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarPublicer;

public class CalendarPublicerDAO extends BaseDAO{
	private static final Log log = LogFactory.getLog(CalendarPublicer.class);
	public List<CalendarPublicer> getCalendarPublicers(long eventId){
		
		log.debug("finding CalendarPublicer instance with eventId:" + eventId);
		try
		{
			String queryString = "from CalendarPublicer as model where model.calendarEvent.id  = ?";
			return findAllBySql(queryString, eventId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public void deletePublicer(long id) {
		log.debug("delete by id");
		try
		{
			String queryString = "delete CalendarPublicer where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
		
	}

}
