package templates.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sun.xml.ws.rm.v200702.SequenceAcknowledgementElement.Final;

public abstract class GenericDaoHibernateImpl<T, PK extends Serializable> extends HibernateDaoSupport implements IGenericDao<T,PK> {    
	   
    private Class<T> persistentClass;    
        
    @SuppressWarnings("unchecked")   
    public GenericDaoHibernateImpl(Class<T> t) {   
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()      
                .getGenericSuperclass()).getActualTypeArguments()[0];     
    }   
        
//    public GenericDaoHibernateImpl(Class<T> t){    
//        this.persistentClass = t;
//    }    
        
    public Class<T> getPersistentClass() {       
        return persistentClass;       
    }    
   
    public void delete(T persistentObject) {    
            
        this.getHibernateTemplate().delete(persistentObject);    
    }    
   

	@SuppressWarnings("unchecked")    
    public T find(PK id) {    
            
        return (T) this.getHibernateTemplate().get(getPersistentClass(), id);    
    }    
   
    @SuppressWarnings("unchecked")    
    public PK save(T newInstance) {    
        return (PK)this.getHibernateTemplate().save(newInstance);    
    }    
   
    public void update(T transientObject) {    
    
        this.getHibernateTemplate().update(transientObject);    
    }    
    
    public List<T> find(String hqlString){
    	
    	return this.getHibernateTemplate().find(hqlString);
    	
    }
    
    public List<T> find(final String hqlString,final int limit){
    	return this.getHibernateTemplate().executeFind(new HibernateCallback(){
	            public Object doInHibernate(Session session) throws HibernateException,SQLException{
	                Query query = session.createQuery(hqlString);
	                query.setFirstResult(0);
	                query.setMaxResults(limit);
	                List<T> list = query.list();
	                return list;
	            }
	    });
    	
    }
    

	public void merge(T transientObject){
		
		getHibernateTemplate().merge(transientObject);
	}
	 public List getUserByPage(final String hql,final int beginIndex,final int everyPage){
	        List list = getHibernateTemplate().executeFind(new HibernateCallback(){
	            public Object doInHibernate(Session session) throws HibernateException,SQLException{
	                Query query = session.createQuery(hql);
	                query.setFirstResult(beginIndex);
	                query.setMaxResults(everyPage);
	                List list = query.list();
	                return list;
	            }
	        });
	        return list;
	    }
	 
	public Boolean insert(String sql) throws DataAccessResourceFailureException, HibernateException, IllegalStateException, SQLException{
		Boolean result= getSession().connection().prepareStatement(sql.toString()).execute();
		return result;
		
	}
	 /**
	 * 返回Criteria
	 * 
	 * @return Criteria
	 */
	public Criteria getCriteria() {
		Criteria criteria = getSession().createCriteria(persistentClass);
		criteria.setCacheable(true);
		criteria.setCacheRegion("yozosoft.template.pojo");
		return criteria;
	}

	/**
	 * 返回Criteria
	 * 
	 * @param criteria
	 * @param sort
	 * @return Criteria
	 */
	public Criteria setCriteriaOrder(Criteria criteria, Map<String, String> sort) {

		String order = "id";
		String taxis = "desc";
		if (sort.get("order") != null) {
			order = sort.get("order");
		}
		if (sort.get("taxis") != null) {
			taxis = sort.get("taxis");
		}
		if (taxis.equals("asc")) {
			criteria.addOrder(Order.asc(order));
		}
		else if (taxis.equals("desc")) {
			criteria.addOrder(Order.desc(order));
		}
		return criteria;
	}
	
	public void flush() {
		getHibernateTemplate().flush();
	}
	
	public void clear(){
		getHibernateTemplate().clear();
	}
}    
