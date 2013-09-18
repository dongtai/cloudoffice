package apps.transmanager.weboffice.service.dao.reception;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BaseDAO<E> extends HibernateDaoSupport implements IBaseDAO<E> {
	protected Class<E> entityClass;

	/**
	 * 
	 */
	protected Logger log = Logger.getLogger(this.getClass());

	/**
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BaseDAO() {
		super();
		entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	/**
	 * @param entityClassName
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void setEntityClass(String entityClassName) throws ClassNotFoundException {
		this.entityClass = (Class<E>) Class.forName(entityClassName);
	}

	/**
	 * @param entityClass
	 */
	@SuppressWarnings("hiding")
	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * 返回Criteria
	 * 
	 * @return Criteria
	 */
	protected Criteria getCriteria() {
		Criteria criteria = getSession().createCriteria(entityClass);
		criteria.setCacheable(true);
		criteria.setCacheRegion("cn.com.travel.base.BaseEntity");
		return criteria;
	}

	/**
	 * 返回Criteria
	 * 
	 * @param criteria
	 * @param sort
	 * @return Criteria
	 */
	protected Criteria setCriteriaOrder(Criteria criteria, Map<String, String> sort) {

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

	/**
	 * 返回查询的数目
	 * 
	 * @param criteria
	 * @return Criteria
	 */
	protected int pageByCount(Criteria criteria) {
		CriteriaImpl impl = (CriteriaImpl) criteria;
		// 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
		Projection projection = impl.getProjection();
		int totalCount = ((Integer) criteria.setProjection(Projections.rowCount()).uniqueResult())
				.intValue();
		// 将之前的Projection和OrderBy条件重新设回去
		criteria.setProjection(projection);
		if (projection == null) {
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		return totalCount;
	}

//	/**
//	 * 返回Criteria
//	 * 
//	 * @param criteria
//	 * @param page
//	 * @return Criteria
//	 */
//	protected Criteria setCriteriaPage(Criteria criteria, PageBean<E> page) {
//		criteria.setFirstResult((page.getPage() - 1) * page.getPageSize());
//		criteria.setMaxResults(page.getPageSize());
//		return criteria;
//	}

	/**
	 * @param entity
	 */
	public void delete(E entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * @param name
	 * @param value
	 * @return List<E>
	 */
	public List<E> findBy(String name, Object value) {
		return findBy(new String[] { name }, new Object[] { value });
	}

	/**
	 * @param names
	 * @param values
	 * @return List<E>
	 */
	@SuppressWarnings("unchecked")
	public List<E> findBy(String[] names, Object[] values) {
		if (names == null || values == null || names.length != values.length) {
			throw new HibernateException("Illegal name and values" + names + ":" + values);
		}
		Criteria criteria = getCriteria();
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		for (int i = 0; i < names.length; i++) {
			criteriaMap.put(names[i], values[i]);
		}
		criteria.add(Restrictions.allEq(criteriaMap));
		criteria.setCacheable(true);
		criteria.setCacheRegion("cn.com.travel.base.BaseEntity");
		return criteria.list();
	}

	/**
	 * 
	 */
	public void flush() {
		getHibernateTemplate().flush();
	}

	/**
	 * @param id
	 * @return E
	 */
	@SuppressWarnings("unchecked")
	public E get(Integer id) {
		if(id==null)
			return null;
		return (E) getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * @return List<E>
	 */
	@SuppressWarnings("unchecked")
	public List<E> listAll() {
		return getHibernateTemplate().loadAll(entityClass);
	}

	/**
	 * @param id
	 * @return E
	 */
	@SuppressWarnings("unchecked")
	public E load(Integer id) {
		return (E) getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * @param name
	 * @param value
	 * @return E
	 */
	public E loadBy(String name, Object value) {
		return loadBy(new String[] { name }, new Object[] { value });
	}

	/**
	 * @param names
	 * @param values
	 * @return E
	 */
	public E loadBy(String[] names, Object[] values) {
		E result = null;
		List<E> resultList = findBy(names, values);
		if (resultList.size() > 0) {
			result = resultList.get(0);
		}
		return result;
	}

	/**
	 * @param entity
	 */
	public void refresh(E entity) {
		getHibernateTemplate().refresh(entity);
	}

	/**
	 * @param entity
	 * @return Integer
	 */
	public Long save(E entity) {
		return (Long) getHibernateTemplate().save(entity);
	}

	/**
	 * @param entity
	 */
	public void saveOrUpdate(E entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	/**
	 * @param entity
	 */
	public void update(E entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * @param entity
	 */
	public void merge(E entity) {
		getHibernateTemplate().merge(entity);
	}


	public void saveAll(List<E> entityList) {
		getHibernateTemplate().saveOrUpdateAll(entityList);
		
	}
}
