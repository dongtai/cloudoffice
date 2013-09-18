package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarEmpower;
import apps.transmanager.weboffice.databaseobject.CalendarFocused;

public class CalendarEmpowerDAO extends BaseDAO{
private static final Log log = LogFactory.getLog(CalendarFocused.class);
	
	public long save(CalendarEmpower transientInstance)
	{
		log.debug("saving CalendarEmpower instance");
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
	public  List<CalendarEmpower> getCalendarEmpower(Long userId)
	{
		log.debug("finding CalendarEmpower instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarEmpower as model where model.userinfo.id  = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public void deleteEmpower(long id) {
		log.debug("delete by id");
		try
		{
			String queryString = "delete CalendarEmpower where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
		
	}
	public List<CalendarEmpower> getCalendarEmpoweredPerson(Long id) {
		log.debug("finding CalendarEmpower instance with userId:" + id);
		try
		{
			String queryString = "from CalendarEmpower as model where model.empoweredUserId  = ?";
			return findAllBySql(queryString, id);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List getRecord(Long systemUID, Long currentUID) {
		log.debug("finding CalendarRelation instance with userId:" + systemUID);
		try
		{
			String queryString = "from CalendarEmpower as model where model.empoweredUserId = ? and model.userinfo.id = ?";
			return findAllBySql(queryString, systemUID,currentUID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
}
