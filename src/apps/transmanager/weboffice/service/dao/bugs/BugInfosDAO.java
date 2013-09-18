package apps.transmanager.weboffice.service.dao.bugs;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.bug.BugInfos;


public class BugInfosDAO  extends BaseDAO<BugInfos>{

	public void delete(BugInfos persistentInstance) {
		log.debug("deleting BugInfos instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public void deleteByID(Long bugid)
	{
        log.debug("delete by id");
        try
        {
        	String queryString = "delete from BugInfos where id = "+ bugid;
            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
            queryObject.executeUpdate();
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
    
	}
	
	public BugInfos findById(Long id) {
		log.debug("getting BugInfos instance with id: " + id);
		try {
			BugInfos instance = (BugInfos) getHibernateTemplate().get("com.evermore.weboffice.databaseobject.bug.BugInfos", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<BugInfos> findByExample(BugInfos instance) {
		log.debug("finding BugInfos instance by example");
		try {
			List<BugInfos> results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<BugInfos> findByProperty(String propertyName, Object value) {
		log.debug("finding BugInfos instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from BugInfos as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<BugInfos> findByBugs(BugInfos bugInfos,Users user, int start,int count,String sort)
	{
		return findByBugs(bugInfos,user,start,count,sort,false);
	}
	public List<BugInfos> findByBugs(BugInfos bugInfos,Users user, int start,int count,String sort,boolean isSize)
	{
		log.debug("finding BugInfos instance with findByBugs: ");
		try {
			Criteria criteria = getSession().createCriteria(BugInfos.class);//getCriteria();
			if (bugInfos.getStartdate()!=null)
			{
				criteria.add(Restrictions.ge("adddate", bugInfos.getStartdate()));
			}
			if (bugInfos.getEnddate()!=null)
			{
				criteria.add(Restrictions.le("adddate", bugInfos.getEnddate()));
			}
			if (bugInfos.getUserid()!=null && bugInfos.getUserid().longValue()>0l)
			{
				criteria.add(Restrictions.eq("userid", bugInfos.getUserid()));
			}
			if (bugInfos.getModifyresult()!=null && bugInfos.getModifyresult().intValue()>0)
			{
				criteria.add(Restrictions.eq("modifyresult", bugInfos.getModifyresult()));
			}
			if (bugInfos.getBugtype()!=null && bugInfos.getBugtype().intValue()>0)
			{
//				criteria.add(Restrictions.eq("bugtype", bugInfos.getBugtype()));
			}
			if (bugInfos.getErrortype()!=null && bugInfos.getErrortype().intValue()>0)
			{
//				criteria.add(Restrictions.eq("errortype", bugInfos.getErrortype()));
			}
			if (bugInfos.getSeriousid()!=null && bugInfos.getSeriousid().intValue()>0)
			{
				criteria.add(Restrictions.eq("seriousid", bugInfos.getSeriousid()));
			}
			if (bugInfos.getEnvironmentid()!=null && bugInfos.getEnvironmentid().intValue()>0)
			{
				criteria.add(Restrictions.eq("environmentid", bugInfos.getEnvironmentid()));
			}
	
			if (bugInfos.getReason()!=null && bugInfos.getReason().length()>0)
			{
				criteria.add(Restrictions.or(Restrictions.like("summer", bugInfos.getReason(), MatchMode.ANYWHERE), Restrictions.like("opstep", bugInfos.getReason(), MatchMode.ANYWHERE)));
			}
			criteria.add(Restrictions.eq("isdelete", 0));

			if (!isSize)
			{
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
				criteria.addOrder(Order.desc("id"));
				criteria.setMaxResults(count);
				criteria.setFirstResult(start);
			}
			List<BugInfos> list=criteria.list();
			
			
			return list;
		} catch (RuntimeException re) {
			log.error("findByBugs failed", re);
			throw re;
		}
	}
	public int findByBugsize(BugInfos bugInfos,Users user)
	{
		return findByBugs(bugInfos,user, 0,0,"",true).size();
	}
	public int[] totalBugs(BugInfos bugInfos,Users user)
	{
		int[] result=new int[1];
		List<BugInfos> list=findByBugs(bugInfos,user,0,0,"",true);
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				BugInfos temp=list.get(i);
				
			}
		}
		return result;
	}
	
}
