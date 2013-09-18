package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.databaseobject.Receptionmanlist;


public class ReceptionmanlistDAO extends BaseDAO<Receptionmanlist>{

	public void delete(Receptionmanlist persistentInstance) {
		log.debug("deleting Reception instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public void deleteByID(Integer receptionmanlistid)
	{
        log.debug("delete by id");
        try
        {
        	String queryString = "delete from Receptionmanlist where manid = "+ receptionmanlistid;
            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
            queryObject.executeUpdate();
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
    
	}

	public Receptionmanlist findById(Integer id) {
		log.debug("getting Receptionmanlist instance with id: " + id);
		try {
			Receptionmanlist instance = (Receptionmanlist) getHibernateTemplate().get(
					"com.evermore.weboffice.databaseobject.Receptionmanlist", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<Receptionmanlist> findByExample(Receptionmanlist instance) {
		log.debug("finding Receptionmanlist instance by example");
		try {
			List<Receptionmanlist> results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<Receptionmanlist> findByProperty(String propertyName, Object value) {
		log.debug("finding Receptionmanlist instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Receptionmanlist as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<Receptionmanlist> findByReceptionmanlist(Receptionmanlist receptionmanlist, int start,int count)
	{
		log.debug("finding Receptionmanlist instance with findByReception: ");
		try {
			Criteria criteria = getCriteria();
			if (receptionmanlist.getReception()!=null)
			{
				criteria.add(Restrictions.eq("reception",receptionmanlist.getReception()));
			}
//			criteria.addOrder(Order.asc("cityid")).addOrder(Order.desc("rdate"))
//	        .addOrder(Order.desc("rbuyNum"));

			criteria.setMaxResults(count);
			criteria.setFirstResult(start);
			return criteria.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
}
