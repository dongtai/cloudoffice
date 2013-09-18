package templates.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;

import com.sun.xml.ws.rm.v200702.SequenceAcknowledgementElement.Final;


public interface IGenericDao<T, PK extends Serializable> {   
	
	public SessionFactory getSessionFactory();
	
	PK save(T newInstance);   
	
	T find(PK id);   
	
	void update(T transientObject);   
	
	void merge(T transientObject);   
	
	void delete(T persistentObject);   
	public List<T> find(String hqlString);
	public List<T> find(final String hqlString,final int limit);
	public List getUserByPage(final String hql,final int beginIndex,final int everyPage); 
	
	public Boolean insert(String sql) throws DataAccessResourceFailureException, HibernateException, IllegalStateException, SQLException;
	public Criteria getCriteria() ;
	public Criteria setCriteriaOrder(Criteria criteria, Map<String, String> sort);
	public void flush();
	public void clear();
	
} 
