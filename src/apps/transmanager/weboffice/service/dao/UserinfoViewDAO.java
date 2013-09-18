package apps.transmanager.weboffice.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.UserinfoView;

/**
 */
public class UserinfoViewDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(UserinfoViewDAO.class);
	private static final String VIEW = " select new com.evermore.weboffice.domain.UserinfoView("
			+ "userinfo.id, userinfo.userName, userinfo.realEmail,  "
			+ "userinfo.realName, userinfo.companyName, userinfo.spaceUID) "
			+ " from Users userinfo";
	private static final String VIEW_COUNT = " select count(*) from Users userinfo";

	
	public UserinfoView findById(long id)
	{
		log.debug("getting UserinfoView instance with id: " + id);
		try
		{
			List<UserinfoView> list = findAllBySql(VIEW + " where userinfo.id = ? ", id);
			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	public DataHolder getlimited(final int index, final int persize)
	{
		log.debug("getting Userinfo");
		try
		{
			final DataHolder holer = new DataHolder();
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
					Query queryObject; 
					if (index == 0)
					{
						queryObject = em.createQuery(VIEW_COUNT);
						Object o = queryObject.getSingleResult();						
						holer.setIntData(((Long)o).intValue());
					}
					queryObject = em.createQuery(VIEW);
					queryObject.setFirstResult((index) * persize);
					queryObject.setMaxResults(persize);
					holer.setUserinfoviewData(new ArrayList<UserinfoView>(
							queryObject.getResultList()));
					return null;
				}
			});
			return holer;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	// 为搜索而使用。user663
	public DataHolder getSearchUserinfoView(int option, String keyWord,
			int index, int length, String sort, String dir)
	{
		log.debug("attaching dirty UserinfoView instance");
		try
		{
			String queryString;
			List list;
			if (option == 0)
			{
				
				if (sort != null && dir != null)
				{
					queryString = VIEW + " where userinfo.userName like ?)"
							+ " order by " + sort + " " + dir;
				}
				else
				{
					queryString = VIEW + " where  userinfo.userName like ?)";
				}
				list = findAllBySql(queryString, "%" + keyWord + "%");

			}
			else if (option == 1)
			{
				
				if (sort != null && dir != null)
				{
					queryString = VIEW + " where userinfo.realEmail like ?)"
							+ " order by " + sort + " " + dir;
				}
				else
				{
					queryString = VIEW + " where userinfo.realEmail like ?)";
				}
				list = findAllBySql(queryString,  "%" + keyWord + "%");
			}
			else
			{
				if (sort != null && dir != null)
				{
					queryString = VIEW + " where (userinfo.userName like ? or userinfo.realEmail like ?)"
							+ " order by " + sort + " " + dir;
				}
				else
				{
					queryString = VIEW + "  where (userinfo.userName like ? or userinfo.realEmail like ?)";
				}
				list = findAllBySql(queryString,  "%" + keyWord + "%", "%" + keyWord + "%");
			}

			// q.setFirstResult((index - 1) * length);
			// q.setMaxResults(length);

			if (dh == null)
			{
				dh = new DataHolder();
			}

			dh.setUserinfoviewData(new ArrayList<UserinfoView>(list));
			return dh;
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;

		}
	}
	
	DataHolder dh;
	
	public Users findUsersById(long id)
	{
		try
		{
			List<Users> list = findAllBySql("from Users where id = ? ", id);
			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}
}