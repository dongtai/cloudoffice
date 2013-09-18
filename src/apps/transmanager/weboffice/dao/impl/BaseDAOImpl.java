package apps.transmanager.weboffice.dao.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;

import apps.transmanager.weboffice.dao.IBaseDAO;
import apps.transmanager.weboffice.util.beans.Page;

public abstract class BaseDAOImpl<T> implements IBaseDAO<T> {

	private HibernateTemplate hibTemplate;

	public void setHibTemplate(HibernateTemplate hibTemplate) {
		this.hibTemplate = hibTemplate;
	}

	public Session getSession() {
		return hibTemplate.getSessionFactory().getCurrentSession();
	}

	public void saveOrUpdate(T obj) {
		try {
			hibTemplate.saveOrUpdate(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveOrUpdateAll(List<T> obj)
	{
		hibTemplate.saveOrUpdateAll(obj);
	}

	public void delete(T obj) {
		hibTemplate.delete(obj);
	}
	
	public boolean deleteById(String className,Object id){
		StringBuffer queryString = new StringBuffer();
		queryString.append("delete ").append(className).append(" where id=:id");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("id", id);
		try{
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteByIdList(String className,List<?> id){
		StringBuffer queryString = new StringBuffer();
		queryString.append("delete ").append(className).append(" where id in(:id)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameterList("id",id);
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}
	}
	
	public boolean deleteByProperty(String className,String property,Object value)
	{
		StringBuffer queryString = new StringBuffer();
		queryString.append("delete ").append(className).append(" where ").append(property).append("=?");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter(0, value);
		try{
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T findById(String className,Object id) {
		return findById(className, id,0);
	}
	@SuppressWarnings("unchecked")
	public T findById(String className,Object id,int usertype) {
		return (T) hibTemplate.get(className, (Serializable) id);
	}
	public List<T> findAll(String className, Page page) {
		if (null != page) {
			int totalRecord = (int) findCount(className);
			page.retSetTotalRecord(totalRecord);
			return findAll(className, page.getCurrentRecord(),
					page.getPageSize());
		} else {
			return findAll(className);
		}
	}
	
	@Override
	public boolean update(String className,List<String> columNames,List<String> conditions,Map<String,Object> propertyMap)
	{
		StringBuffer queryString = new StringBuffer();
		String setQuery = createUpdateQuery(className,columNames);
		String conditionQuery = createConditions(conditions);
		queryString.append(setQuery).append(conditionQuery);
		Query query = getQuery(queryString.toString(), propertyMap);
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}
	}
	
	public boolean update(String className,List<String> columNames,List<String> conditions,T object)
	{
		try
		{
		StringBuffer queryString = new StringBuffer();
		String setQuery = createUpdateQuery(className,columNames);
		String conditionQuery = createConditions(conditions);
		queryString.append(setQuery).append(conditionQuery);
		Query query = getSession().createQuery(queryString.toString());
		query.setProperties(object);
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean update(String className,String columName,String condition,T object)
	{
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update ").append(className).append(" t set ");
			queryString.append(" t.").append(columName).append("=").append(":").append(columName);
			queryString.append(" where 1=1 and t.").append(condition).append("=:").append(condition);
			Query query = getSession().createQuery(queryString.toString());
			query.setProperties(object);
			int flag = query.executeUpdate();
			if(flag==0)
			{
				return false;
			}else{
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className, String name, Object value,
			Page page, String sortColumn, String sort) {
		int totalRecord = (int)findCount(className, name, value);
		page.retSetTotalRecord(totalRecord);
		String queryString = createQueryString(className, name,value,sortColumn,sort);
		Query query = getSession().createQuery(queryString.toString());
		if (null != value) {
			query.setParameter(0, value);
		}
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return query.list();
	}


	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className, String name, Object value,
			Page page) {
		int totalRecord = (int)findCount(className, name, value);
		page.retSetTotalRecord(totalRecord);
		String queryString = createQueryString(className, name, value);
		Query query = getSession().createQuery(queryString);
		if (null != value) {
			query.setParameter(0, value);
		}
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className, String name, Object value,
			String sortColumn, String sort) {
		String queryString = createQueryString(className, name,value,sortColumn,sort);
		Query query = getSession().createQuery(queryString.toString());
		if (null != value) {
			query.setParameter(0, value);
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className, String name, Object value) {
		try{
		String queryString = createQueryString(className, name, value);
		Query query = getSession().createQuery(queryString);// 设置查询条件
		if (null != value) {
			query.setParameter(0, value);
		}
		return query.list();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByProperty(List<String> columNames, T obj) {
		String tableName = obj.getClass().getName();
		String queryString = creatQueryString(tableName, columNames);
		Query query = getSession().createQuery(queryString);
		query.setProperties(obj);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public T findByPropertyUnique(List<String> columNames, T obj) {
		String tableName = obj.getClass().getName();
		String queryString = creatQueryString(tableName, columNames);
		Query query = getSession().createQuery(queryString);
		query.setProperties(obj);
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public T findByPropertyUnique(String className, String name, Object value) {
		T result = null;
		try {
			String queryString = createQueryString(className, name, value);
			Query query = getSession().createQuery(queryString.toString());
			if (null != value) {
				query.setParameter(0, value);
			}
			result = (T) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className,
			Map<String, Object> propertyMap) {
		String queryString = createQueryString(className, propertyMap);
		Query query = getQuery(queryString, propertyMap);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className,
			Map<String, Object> propertyMap,Page page) {
		if(page==null)
		{
			return null;
		}
		int totalRecord = (int) findCount(className,propertyMap);
		page.retSetTotalRecord(totalRecord);
		String queryString = createQueryString(className, propertyMap);
		Query query = getQuery(queryString, propertyMap);
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String className,
			Map<String, Object> propertyMap,Page page,String sortName,String order) {
		if(page==null || sortName==null || order==null)
		{
			return null;
		}
		int totalRecord = (int) findCount(className,propertyMap);
		page.retSetTotalRecord(totalRecord);
		String queryString = createQueryString(className, propertyMap,sortName,order);
		Query query = getQuery(queryString, propertyMap);
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return query.list();
	}
	
	/**
	 * 根据单多条件查询记录(同时要求分页和排序条件)
	 * @param className 类名
	 * @param propertyMap 条件集合
	 * @param page  分页条件
	 * @param sortColume 排序字段
	 * @param sort 排序方式
	 * @return 记录集
	 */
	public List<T> findByProperty(String className,
			Map<String, Object> propertyMap, String sortName,
			String order){
		String queryString = createQueryString(className, propertyMap,sortName,order);
		Query query = getQuery(queryString, propertyMap);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public T findByPropertyUnique(String className,
			Map<String, Object> propertyMap) {
		String queryString = createQueryString(className, propertyMap);
		Query query = getQuery(queryString,propertyMap);
		return (T) query.uniqueResult();
	}

	/*------------------------------------------------------------私有方法----------------------------------------------------------------*/
	
	/**
	 * 创建HQL语句，参数为单一条件(列名，对应的对象值，对象里对应的属性必须有值)
	 */
	private String createQueryString(String className, String name, Object value) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className).append(" t");
		queryString.append(createConditions(name, value));
		return queryString.toString();
	}
	
	/**
	 * 创建HQL语句，参数为单一条件,排序(列名，对应的对象值，对象里对应的属性必须有值)
	 */
	private String createQueryString(String className, String name, Object value,String sortName,String order) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className).append(" t");
		queryString.append(createConditions(name, value));
		queryString.append(createSort(sortName, order));
		return queryString.toString();
	}
	
	
	/**
	 * 创建HQL语句，参数为类名和列名-值的MAP
	 * 
	 * @param className
	 *            类名
	 * @param propertyMap
	 *            列名-值
	 * @return HQL语句
	 */
	private String createQueryString(String className,
			Map<String, Object> propertyMap) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className).append(" t");
		String condition = createConditions(propertyMap);
		queryString.append(condition);
		return queryString.toString();
	}
	
	/**
	 * 创建HQL语句，参数为类名和列名-值的MAP
	 * 
	 * @param className
	 *            类名
	 * @param propertyMap
	 *            列名-值
	 * @return HQL语句
	 */
	private String createQueryString(String className,
			Map<String, Object> propertyMap,String sortName,String order) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className).append(" t");
		queryString.append(createConditions(propertyMap)).append(createSort(sortName, order));
		return queryString.toString();
	}

	/**
	 * 创建HQL语句，参数为类名和列名列表
	 * 
	 * @param tableName
	 *            类名
	 * @param columNames
	 *            列名列表
	 * @return HQL语句
	 */
	private String creatQueryString(String tableName, List<String> columNames) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(tableName).append(" t");
		String condition = createConditions(columNames);
		queryString.append(condition);
		return queryString.toString();
	}

	/**
	 * 查找所有记录（不分页）
	 * 
	 * @param className
	 *            类名
	 * @return 记录列表
	 */
	@SuppressWarnings("unchecked")
	private List<T> findAll(String className) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className);
		Query query = getSession().createQuery(queryString.toString());
		return query.list();

	}

	/**
	 * 查找所有记录(分页)
	 * 
	 * @param className
	 *            类名
	 * @param firstResult
	 *            当前记录
	 * @param maxResult
	 *            每页记录
	 * @return 记录列表
	 */
	@SuppressWarnings("unchecked")
	private List<T> findAll(String className, int firstResult, int maxResult) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ").append(className);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.list();

	}

	/**
	 * 查找记录的数目
	 * 
	 * @param className
	 *            类名
	 * @return 记录数
	 */
	private long findCount(String className) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from ").append(className);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	
	/**
	 * 查找单一条件记录的数目
	 * 
	 * @param className
	 *            类名
	 * @param name 列名
	 * @param value 对象值(对应的属性必须有值)
	 * @return 记录数
	 */
	private long findCount(String className,String name,Object value) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from ").append(className).append(" t");
		queryString.append(createConditions(name, value));
		Query query = getSession().createQuery(queryString.toString());
		if (null != value) {
			query.setParameter(0, value);
		}
		return (Long) query.uniqueResult();
	}
	
	/**
	 * 查找多条件下的数目
	 * @param className 类名
	 * @param propertyMap 列-值MAP
	 * @return 数目
	 */
	private long findCount(String className,Map<String,Object> propertyMap){
		StringBuffer queryString = new StringBuffer();
		String condition = createConditions(propertyMap);
		queryString.append("select count(id) from ").append(className).append(" t").append(condition);
		Query query = getQuery(queryString.toString(),propertyMap);
		return (Long) query.uniqueResult();
	}

	/**
	 * 组装条件
	 * @param conditionMap 过滤条件
	 * @return 条件语句
	 */
	private String createConditions(Map<String,Object> propertyMap)
	{
		StringBuffer queryString = new StringBuffer(" where 1=1");
		Iterator<String> iter = propertyMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (null == propertyMap.get(key)) {
				queryString.append(" and t.").append(key).append(" is null");
			} else {
				queryString.append(" and t.").append(key).append("=:")
						.append(key);
			}
		}
		return queryString.toString();
	}
	
	/**
	 * 组装条件
	 * @param conditions 过滤条件
	 * @return 条件语句
	 */
	private String createConditions(List<String> conditions)
	{
		StringBuffer queryString = new StringBuffer(" where 1=1");
		for(String condition:conditions)
		{
			if (null == condition) {
				queryString.append(" and t.").append(condition).append(" is null");
			} else {
				queryString.append(" and t.").append(condition).append("=:")
						.append(condition);
			}
		}
		return queryString.toString();
	}
	
	
	/**
	 * 组装查询条件(重载方法，请根据参数进行选择)
	 * 
	 * @param name
	 *            字段名
	 * @param value
	 *            字段值
	 * @return 条件语句
	 */
	private String createConditions(String name, Object value) {
		StringBuffer conditions = new StringBuffer(" where 1=1 ");
		if (name!=null && null == value) {
			conditions.append("and t.").append(name).append(" is null ");
		} else if(name!=null && null!=value){
			conditions.append("and t.").append(name).append("=? ");
		}
		return conditions.toString();
	}

	/**
	 * 组装排序条件
	 * 
	 * @param sortName
	 *            排序字段
	 * @param sort
	 *            排序方式(1:ASC;2:DESC)
	 * @return 排序条件
	 */
	private String createSort(String sortName, String sort) {
		String queryString = " order by " + sortName +" "+ sort;
		return queryString;
	}
	
	/**
	 * 创建更新设置语句
	 * @param className 类名
	 * @param columNames 字段名
	 * @return 更新设置语句
	 */
	private String createUpdateQuery(String className,List<String> columNames) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("update ").append(className).append(" t set ");
		for(int i=0;i<columNames.size();i++)
		{
			String columName = columNames.get(i);
			if(i==columNames.size()-1)
				queryString.append(" t.").append(columName).append("=").append(":").append(columName);
			else
				queryString.append(" t.").append(columName).append("=").append(":").append(columName).append(",");
		}
		return queryString.toString();
	}
	
	/**
	 * 返回Query
	 * @param queryString query语句
	 * @param propertyMap 参数为列-值MAP
	 * @return
	 */
	private Query getQuery(String queryString, Map<String, Object> propertyMap) {
		Query query  = getSession().createQuery(queryString);
		Iterator<String> iter = propertyMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (null != propertyMap.get(key)) {
				query.setParameter(key, propertyMap.get(key));
			}
		}
		return query;
	}

}
