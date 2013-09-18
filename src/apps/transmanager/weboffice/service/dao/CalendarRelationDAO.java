package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.CalendarRelation;

public class CalendarRelationDAO extends BaseDAO {
	
	private static final Log log = LogFactory.getLog(CalendarRelation.class);
	
	public  List<CalendarRelation> getCalendarRelations(long userId)
	{
		log.debug("finding CalendarRelation instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarRelation as model where model.userinfo.id  = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public long save(CalendarRelation transientInstance)
	{
		log.debug("saving CalendarRelation instance");
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
			String queryString = "delete CalendarRelation where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public  List<CalendarRelation> getCalendarRelations2(long userId)
	{
		log.debug("finding CalendarRelation instance with userId:" + userId);
		try
		{
			String queryString = "from CalendarRelation as model where model.relativedUserId = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List checkRelation(Long readUserId, Long currentUserId) {
		log.debug("finding CalendarRelation instance with userId:" + currentUserId);
		try
		{
			String queryString = "from CalendarRelation as model where model.relativedUserId = ? and model.userinfo.id = ?";
			return findAllBySql(queryString, readUserId,currentUserId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CalendarRelation> getPubicerByUserId(Long id) {
		log.debug("finding CalendarRelation instance with userId:" + id);
		try
		{
			String queryString = "from CalendarRelation as model where model.relativedUserId = ?";
			return findAllBySql(queryString, id);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CalendarRelation> getSearchPublicUser(Long id, String keyword) {
		log.debug("finding CalendarRelation instance with userId:" + id);
		try
		{
			String queryString = "from CalendarRelation as model where model.relativedUserId = ?  and (model.userinfo.userName like ? or model.userinfo.realName like ? or model.userinfo.realEmail like ? )";
			String searchkey = '%'+keyword+'%';
			return findAllBySql(queryString, id ,searchkey ,searchkey ,searchkey);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}


}
