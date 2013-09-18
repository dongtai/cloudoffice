package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Taginfo;

/**
 * 
 */
public class TaginfoDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(TaginfoDAO.class);

	public Taginfo findById(java.lang.Long id)
	{
		log.debug("getting Taginfo instance with id: " + id);
		try
		{
			Taginfo instance = (Taginfo) find("com.evermore.weboffice.databaseobject.Taginfo", id);
			// Hibernate.initialize(instance.getFiletaginfos());
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
		log.debug("finding Taginfo instance with property: " + propertyName
				+ ", value: " + value);
		try
		{
			return findByProperty("Taginfo",  propertyName, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all Taginfo instances");
		try
		{
			return findAll("Taginfo");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}
	public void deleteByID(long tagID)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete Taginfo where tagId = " + tagID;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List findByTagAndID(String tagName, long creatorID)
	{
		log.debug("finding Taginfo with property: tag: " + tagName
				+ ", creatorID: " + creatorID);
		try
		{
			String queryString = "from Taginfo as model where model.tag" + "=?"
					+ " and model.userinfo.id =?";
			return findAllBySql(queryString,  tagName, creatorID);
		}
		catch (RuntimeException re)
		{
			log.error("find by tag and creatorID failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public void modifyByID(long tagID, String newName)
	{
		log.debug("modify by id");
		try
		{
			String queryString = "update Taginfo set tag " + "=?"
					+ "where tagID =?";
			excute(queryString,  newName, tagID);
		}
		catch (RuntimeException re)
		{
			log.error("modify failed", re);
			throw re;
		}
	}
}