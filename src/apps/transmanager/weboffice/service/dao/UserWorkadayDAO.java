package apps.transmanager.weboffice.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;

import apps.transmanager.weboffice.databaseobject.UserWorkaday;

/**
 */
public class UserWorkadayDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(UserWorkaday.class);


	public void save(UserWorkaday transientInstance)
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
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}


	public UserWorkaday findById(java.lang.Long id)
	{
		log.debug("getting UserWorkaday instance with id: " + id);
		try
		{
			UserWorkaday instance = (UserWorkaday) find("com.evermore.weboffice.databaseobject.UserWorkaday", id);
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
		log.debug("finding UserWorkaday instance with property: " + propertyName
				+ ", value: " + value);
		try
		{
			return findByProperty("UserWorkaday", propertyName, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List<UserWorkaday> findByUserId(long userId, int from, int count)
	{
		log.debug("finding UserWorkaday instance with userId:" + userId);
		try
		{
			String queryString = "from UserWorkaday as model where model.userinfo.id  = ?";
			return findAllBySql(from, count, queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public Long getUserWorkadayCount(long userId)
	{
		log.debug("finding UserWorkaday instance with userId:" + userId);
		try
		{
			String queryString = "select count(*) from UserWorkaday as model where model.userinfo.id  = ?";
			
            List<Long> list = findAllBySql(queryString, userId);
            if (list != null && list.size() > 0)
            {
            	return list.get(0);
            }
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		return 0L;
	}
	

	public List findAll()
	{
		log.debug("finding all UserWorkaday instances");
		try
		{
			return findAll("UserWorkaday");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public void deleteByID(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete UserWorkaday where id = " + id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	/**
	 *  根据登录用户和日期获得当前工作日志
	 * @param userId
	 * @param date
	 * @return
	 */
	public UserWorkaday  getByUserAndDate(long userId,Date date)
	{
		log.debug("finding UserWorkaday instance with userId:" + userId+ "and date ="+date);
		try
		{
			String queryString = "from UserWorkaday uw where uw.userinfo.id  = ? and uw.date = ? ";
			List list = findAllBySql(queryString, userId, date);
			if(list != null && list.size()>0)
			{
				return (UserWorkaday)list.get(0);
			}else{
				return null;
			}
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	/**
	 * 根据关键字查询相关日志
	 * @param keyWord 查询关键字
	 * @return
	 */
	public List<UserWorkaday> getByKeyWord(String keyWord,long userId)
	{
		log.debug("finding UserWorkadays with keyWord:" + keyWord);
		try
		{
			String queryString = "from UserWorkaday uw where uw.userinfo.id  = ? and (uw.contentAm like '%"
				+ keyWord + "%' or uw.contentPm like '%" + keyWord + "%') ";
			List list = findAllBySql(queryString, userId);
			if(list != null && list.size()>0)
			{
				return (List<UserWorkaday>)list;
			}else{
				return null;
			}
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	/**
	 * 根据关键字和起止日期查询
	 * @param keyWord 关键字
	 * @param fromDate 开始日期
	 * @param toDate 结束日期
	 * @param userId 当前登录用户
	 * @return
	 */
	public List<UserWorkaday> getByKeyAndDate(final String keyWord, final Date fromDate, final Date toDate, final long userId)
	{
		log.debug("finding UserWorkadays with keyWord:" + keyWord+" and fromDate :"+fromDate+" and toDate ："+toDate);
		try
		{	List<UserWorkaday> ret = getJpaTemplate().executeFind(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					StringBuffer queryString = new StringBuffer( "from UserWorkaday uw where 1=1");
					if(0!=userId)
					{
						queryString.append(" and uw.userinfo.id  = ? and (uw.contentAm!='' or uw.contentPm!='')");
					}
					if(keyWord!=null)
					{
						queryString = queryString.append(" and (uw.contentAm like '%"+keyWord+"%' or uw.contentPm like '%"+keyWord+"%')");
					}
					if(fromDate!=null)
					{
						queryString = queryString.append(" and uw.date >=: ? ");
					}
					if(toDate!=null)
					{
						queryString = queryString.append(" and uw.date <=: ? ");
					}
					queryString = queryString.append(" order by uw.date desc");
								
					Query queryObject = em.createQuery(queryString.toString());
					if(0!=userId)
					{
						queryObject.setParameter(0, userId);
					}
					if(fromDate!=null)
					{
						queryObject.setParameter("fromDate", fromDate);
					}
					if(toDate!=null)
					{
						queryObject.setParameter("toDate", toDate);
					}
					List list = queryObject.getResultList();
					if(list != null && list.size()>0)
					{
						return (List<UserWorkaday>)list;
					}
					else
					{
						return null;
					}
				}
			});
			return ret;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
}