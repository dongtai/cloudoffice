package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.Receptionpower;
import apps.transmanager.weboffice.databaseobject.Users;


public class ReceptionDAO  extends BaseDAO<Reception>{

	public void delete(Reception persistentInstance) {
		log.debug("deleting Reception instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public void deleteByID(Long receptionid)
	{
        log.debug("delete by id");
        try
        {
        	String queryString = "delete from Reception where receptionid = "+ receptionid;
            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
            queryObject.executeUpdate();
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
    
	}
	public void deleteUsersByID(Long receptionid)
	{
        log.debug("delete by id");
        try
        {
        	String queryString = "delete from ReceptionUsers where reception.id = "+ receptionid;
            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
            queryObject.executeUpdate();
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
	}
	public Reception findById(Long id) {
		log.debug("getting Reception instance with id: " + id);
		try {
			Reception instance = (Reception) getHibernateTemplate().get(
					"com.evermore.weboffice.databaseobject.Reception", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<Reception> findByExample(Reception instance) {
		log.debug("finding Reception instance by example");
		try {
			List<Reception> results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<Reception> findByProperty(String propertyName, Object value) {
		log.debug("finding Reception instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Reception as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<Reception> findByReception(Reception reception,Users user,List<Long> canlist, int start,int count,String sort)
	{
		return findByReception(reception,user,canlist, start,count,sort,false);
	}
	public List<Reception> findByReception(Reception reception,Users user,List<Long> canlist, int start,int count,String sort,boolean isSize)
	{
		log.debug("finding Reception instance with findByReception: ");
		try {
			Criteria criteria = getCriteria();
			if (reception.getAddtime()!=null)
			{
				criteria.add(Restrictions.eq("addtime",reception.getAddtime()));
			}
			if (reception.getProvince()!=null && reception.getProvince().length()>0)
			{
				criteria.add(Restrictions.like("province", reception.getProvince(), MatchMode.ANYWHERE));
			}
			if(reception.getCity() != null && reception.getCity().length()>0){
				criteria.add(Restrictions.like("city", reception.getCity(), MatchMode.ANYWHERE));
			}
			
			if(reception.getComedate() != null)
			{
				criteria.add(Restrictions.ge("comedate", reception.getComedate()));
			}
			if (reception.getLeavedate()!=null)
			{
				criteria.add(Restrictions.le("comedate", reception.getLeavedate()));
			}
			
			if (reception.getComereason()!=null && reception.getComereason().length()>0)
			{
				criteria.add(Restrictions.like("comereason", reception.getComereason(), MatchMode.ANYWHERE));
			}
			if(reception.getDaycontext() != null && reception.getDaycontext().length()>0){
				
				criteria.add(Restrictions.like("daycontext", reception.getDaycontext(), MatchMode.ANYWHERE));
			}
			if(reception.getDeleted() != null){
				 
				criteria.add(Restrictions.eq("deleted", reception.getDeleted()));
			}
			if(reception.getIsdisplay() != null){
				 
				criteria.add(Restrictions.eq("isdisplay", reception.getIsdisplay()));
			}
			if (canlist!=null && canlist.size()>0 && user!=null)
			{
				criteria.add(Restrictions.or(Restrictions.eq("userid", user.getId()),Restrictions.in("receptionid", canlist)));
			}
			else if (user!=null)
			{
				criteria.add(Restrictions.eq("userid", user.getId()));
			}
			
			if (reception.getEmail()!=null && reception.getEmail().length()>0)
			{
				criteria.add(Restrictions.like("email", reception.getEmail(), MatchMode.ANYWHERE));
			}
			if (reception.getUnits()!=null && reception.getUnits().length()>0)
			{
				criteria.add(Restrictions.like("units", reception.getUnits(), MatchMode.ANYWHERE));
			}
			if (reception.getJobtype()!=null && reception.getJobtype().length()>0)
			{
				criteria.add(Restrictions.like("jobtype", reception.getJobtype(), MatchMode.ANYWHERE));
			}
			if (reception.getLeader()!=null && reception.getLeader().length()>0)
			{
				criteria.add(Restrictions.like("leader", reception.getLeader(), MatchMode.ANYWHERE));
			}

			if (reception.getLunchaddress()!=null && reception.getLunchaddress().length()>0)
			{
				criteria.add(Restrictions.like("lunchaddress", reception.getLunchaddress(), MatchMode.ANYWHERE));
			}
			
			if (reception.getMobilenum()!=null && reception.getMobilenum().length()>0)
			{
				criteria.add(Restrictions.like("mobilenum", reception.getMobilenum(), MatchMode.ANYWHERE));
			}
			
			if (reception.getPhone()!=null && reception.getPhone().length()>0)
			{
				criteria.add(Restrictions.like("phone", reception.getPhone(), MatchMode.ANYWHERE));
			}
			if (reception.getStayhotel()!=null && reception.getStayhotel().length()>0)
			{
				criteria.add(Restrictions.like("stayhotel", reception.getStayhotel(), MatchMode.ANYWHERE));
			}
			
			if (reception.getVisitspot()!=null && reception.getVisitspot().length()>0)
			{
				criteria.add(Restrictions.like("visitspot", reception.getVisitspot(), MatchMode.ANYWHERE));
			}
			if (reception.getUserid()!=null)
			{
				criteria.add(Restrictions.eq("userid", reception.getUserid()));
			}
			if (reception.getRootgroupid()!=null)
			{
				criteria.add(Restrictions.eq("rootgroupid", reception.getRootgroupid()));
			}
			if (reception.getGroupid()!=null)
			{
				criteria.add(Restrictions.eq("groupid", reception.getGroupid()));
			}
//			criteria.addOrder(Order.asc("cityid")).addOrder(Order.desc("rdate"))
//	        .addOrder(Order.desc("rbuyNum"));
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
				criteria.addOrder(Order.desc("receptionid"));
				criteria.setMaxResults(count);
				criteria.setFirstResult(start);
			}
			return criteria.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public int findByReceptionsize(Reception reception,Users user,List<Long> canlist)
	{
		return findByReception(reception,user,canlist, 0,0,"",true).size();
	}
	public int[] totalReception(Reception reception,Users user,List<Long> canlist)
	{
		int[] result=new int[1];
		List<Reception> list=findByReception(reception,user,canlist, 0,0,"",true);
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				Reception temp=list.get(i);
				if (temp!=null && temp.getMans()!=null)
				{
					result[0]+=temp.getMans();
				}
			}
		}
		return result;
	}
	public void deletePowerBytype(Integer powertype) throws Exception
	{
		log.debug("deletePowerBytype: " + powertype);
		try {
			String queryString = "from Receptionpower as model where model.powernum"
				+ "= ? and model.typeid=0 ";
			List<Receptionpower> list = getHibernateTemplate().find(queryString, powertype);
			if (list!=null)
			{
				for (int i=0;i<list.size();i++)
				{
					getHibernateTemplate().delete(list.get(i));
				}
			}
			
		} catch (RuntimeException re) {
			log.error("deletePowerBytype name failed", re);
			throw re;
		}
	}
	public void savePower(Receptionpower receptionpower) throws Exception
	{
		getHibernateTemplate().save(receptionpower);
	}
	
	public List<Receptionpower> getPowerByuserid(Long userid) throws Exception
	{
		log.debug("finding getPowerByuserid instance with getPowerByuserid: ");
		try {
			Receptionpower temp=new Receptionpower();
			temp.setRpuserid(userid);
			return getHibernateTemplate().findByExample(temp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
