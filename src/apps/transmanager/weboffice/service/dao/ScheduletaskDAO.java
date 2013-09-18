package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Scheduletask;

/**
 */

public class ScheduletaskDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(ScheduletaskDAO.class);
	// property constants
	public static final String TASK_NAME = "taskName";
	public static final String DAYTYPE = "daytype";
	public static final String WEEKVALUE = "weekvalue";
	public static final String MONTHVALUE = "monthvalue";
	public static final String SCHEDULECONTENT = "schedulecontent";
	public static final String USERID = "id";
	public static final String STATE = "state";
	public static final String BACKPATH = "backpath";
	public static final String BACKTYPE = "backtype";
	public static final String BACKCOND = "backcond";
	public static final String RESERVE_A = "reserveA";
	public static final String RESERVE_B = "reserveB";

	public Integer save(Scheduletask transientInstance)
	{
		try 
		{
			super.save(transientInstance);
			return transientInstance.getTaskId();
		} 
		catch (RuntimeException re) 
		{			
			log.error("save failed", re);
			throw re;
		}
	}

	public Scheduletask findById(java.lang.Integer id)
	{
		log.debug("getting Scheduletask instance with id: " + id);
		try
		{
			Scheduletask instance = (Scheduletask) find("com.eio.Scheduletask", id);
			return instance;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value)
	{
		log.debug("finding Scheduletask instance with property: "
				+ propertyName + ", value: " + value);
		try
		{
			return findByProperty("Scheduletask", propertyName, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByBacktype(Object backtype)
	{
		return findByProperty(BACKTYPE, backtype);
	}

	public List findAll()
	{
		log.debug("finding all Scheduletask instances");
		try
		{
			return findAll("Scheduletask");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}


}
