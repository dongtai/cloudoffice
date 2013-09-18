package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.databaseobject.Receptionhistory;


public class ReceptionhistoryDAO  extends BaseDAO<Receptionhistory> {

	public List<Receptionhistory> gethistory(Long receptionid, int start,int count,String sort) throws Exception
	{
		log.debug("finding gethistory instance with gethistory: ");
		try {
			Criteria criteria = getCriteria();
			criteria.add(Restrictions.eq("reception.receptionid",receptionid));
			
			if (sort!=null && sort.length()>10)
			{
				sort=sort.replaceAll("order by ", "").trim();
				if (sort.endsWith("asc"))
				{
					sort=sort.substring(0,sort.length()-4).trim();
					criteria.addOrder(Order.asc(sort));
				}
				else if (sort.endsWith("desc"))
				{
					sort=sort.substring(0,sort.length()-5).trim();
					criteria.addOrder(Order.desc(sort));
				}
			}
			else
			{
				criteria.addOrder(Order.desc("historyid"));
			}
			criteria.setMaxResults(count);
			criteria.setFirstResult(start);
			return criteria.list();
		} catch (RuntimeException re) {
			log.error("find by gethistory name failed", re);
			throw re;
		}
	}
	public void deletehistoryByReception(Long receptionid) throws Exception
	{
		log.debug("delete by receptionid");
        try
        {
        	String queryString = "delete from Receptionhistory where reception.receptionid = "+ receptionid;
            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
            queryObject.executeUpdate();
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
	}
}
