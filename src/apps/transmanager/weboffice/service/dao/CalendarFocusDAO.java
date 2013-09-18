package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarFocused;

public class CalendarFocusDAO extends BaseDAO{

	private static final Log log = LogFactory.getLog(CalendarFocused.class);
	
	public long save(CalendarFocused transientInstance)
	{
		log.debug("saving CalendarFocused instance");
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
	public  List<CalendarFocused> getCalendarFocus(long userId)
	{
		log.debug("finding CalendarFocus instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarFocused as model where model.userinfo.id  = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public void deleteFocus(long id) {
		log.debug("delete by id");
		try
		{
			String queryString = "delete CalendarFocused where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
		
	}

}
