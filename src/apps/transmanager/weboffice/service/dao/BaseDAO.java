package apps.transmanager.weboffice.service.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import apps.transmanager.weboffice.util.server.LogsUtility;


/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class BaseDAO<T> extends JpaDaoSupport 
{

	/**
	 * 保存新对象
	 * @param entity
	 */
	public void save(T entity)
	{
		try
		{
			getJpaTemplate().persist(entity);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("save failed", re);
			throw re;
		}
	}
	/**
	 * 保存集合中的所有对象
	 * @param entities
	 */
	public void saveAll(final Collection<T> entities)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	for (T entity : entities)
	            	{
	            		em.persist(entity);
	            	}
	            	return null;
	            }
	        });  
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 更新对象
	 * @param entity
	 * @return
	 */
	public void update(T entity) 
	{  
		LogsUtility.debug("updating " + entity + "instance");
		try
		{
			getJpaTemplate().merge(entity);
			LogsUtility.debug("update successful");
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("update failed", re);
			throw re;
		}
    }
	
	/**
	 * 更新对象
	 * @param entity
	 * @return
	 */
	public void updateAll(final Collection<T> entities)
	{  
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	for (T entity : entities)
	            	{
	            		em.merge(entity);
	            	}
	            	return null;
	            }
	        });  
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
    } 

	/**
	 * 删除对象	
	 * @param entity
	 */
	public void delete(T entity)
	{
		try
		{
			getJpaTemplate().remove(entity); 
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除entityclass实例，idName为该实例的主键名
	 * @param entityClass 实例类
	 * @param idName 实例类的主键名
	 * @param id 具体的主键值
	 */
	public void deleteEntityByID(Class entityClass, String idName, Long id)
	{
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete " + entityClass.getSimpleName() + " where " + idName + " = ? ";
			excute(queryString, id);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除entityclass实例，idName为该实例的主键名
	 * @param entityClass 实例类
	 * @param idName 实例类的主键名
	 * @param id 具体的主键值
	 */
	public void deleteEntityByID(Class entityClass, String idName, List<Long> id)
	{
		LogsUtility.debug("delete by id");
		try
		{
			String queryString = "delete " + entityClass.getSimpleName() + " where " + idName + " in (?1) ";
			excute(queryString, id);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
		
	/**
	 * 删除集合中的所有对象
	 * @param entities
	 */
	public void delete(final Collection<T> entities)
	{
		try
		{
			getJpaTemplate().execute(new JpaCallback()
			{  
	            public Object doInJpa(EntityManager em) throws PersistenceException
	            {
	            	for (T entity : entities)
	            	{
	            		em.remove(entity);
	            	}
	            	return null;
	            }  
	        });  
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}

	/**
	 * 执行JPQL语句，该语句为更新或删除语句。
	 * @param query
	 * @return
	 */
	public Object excute(final String query)
	{
		return getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createQuery(query);
				return queryObject.executeUpdate();
			}
		});
	}
	
	/**
	 * 执行JPQL语句，该语句为更新或删除语句。
	 * @param query jpql语法的语句
	 * @param param 该语句中的参数
	 * @return
	 */
	public Object excute(final String query, final Object... param)
	{
		return getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createQuery(query);
				if (param != null) 
				{
					for (int i = 0; i < param.length; i++)
					{
						queryObject.setParameter(i + 1, param[i]);
					}
				}
				return queryObject.executeUpdate();
			}
		});
	}
	
	/**
	 * 执行JPQL语句，该语句为更新或删除语句。
	 * @param query jpql语法的语句
	 * @param param 该语句中的参数
	 * @return
	 */
	public Object excuteByNamedParams(final String query, final Map<String, ?> params)
	{
		return getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createQuery(query);
				if (params != null) 
				{
					for (Map.Entry<String, ?> entry : params.entrySet()) 
					{
						queryObject.setParameter(entry.getKey(), entry.getValue());
					}
				}
				return queryObject.executeUpdate();
			}
		});
	}
	
	/**
	 * 执行标准SQL语句，该语句为插入、更新、删除语句。
	 * @param query
	 * @return
	 */
	public Object excuteNativeSQL(final String query)
	{
		return getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createNativeQuery(query);
				return queryObject.executeUpdate();
			}
		});
	}
	/**
	 * 执行标准SQL语句，该语句为查询信息列表。
	 * @param query 标准sql语句
	 * @param start  开始位置，如果为-1，表示从0开始
	 * @param length 长度，如果为-1，表是从start位置开始的记录条数
	 * @return
	 */
	public List excuteNativeSQL(final String query, final int start, final int length)
	{
		return (List)getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createNativeQuery(query);
				if (start >= 0)
				{
					queryObject.setFirstResult(start);
				}
				if (length >= 0)
				{
					queryObject.setMaxResults(length);
				}
				return queryObject.getResultList();
			}
		});
	}
	
	/**
	 * 执行标准SQL语句，该语句为查询信息列表。
	 * @param query 标准sql语句
	 * @param params 为命名参数
	 * @param start  开始位置，如果为-1，表示从0开始
	 * @param length 长度，如果为-1，表是从start位置开始的记录条数
	 * @return
	 */
	public List excuteNativeSQLByName(final String query, final Map<String, ?> params, final int start, final int length)
	{
		return (List)getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createNativeQuery(query);
				if (params != null) 
				{
					for (Map.Entry<String, ?> entry : params.entrySet()) 
					{
						queryObject.setParameter(entry.getKey(), entry.getValue());
					}
				}
				if (start >= 0)
				{
					queryObject.setFirstResult(start);
				}				
				if (length >= 0)
				{
					queryObject.setMaxResults(length);
				}
				return queryObject.getResultList();
			}
		});
	}
	
	/**
	 * 执行标准SQL语句，该语句为查询信息列表。
	 * @param query 标准sql语句
	 * @param start  开始位置，如果为-1，表示从0开始
	 * @param length 长度，如果为-1，表是从start位置开始的记录条数
	 * @return
	 */
	public List excuteNativeSQL(final int start, final int length, final String queryString, final Object... values)
	{
		return (List)getJpaTemplate().execute(new JpaCallback()
		{
			public Object doInJpa(EntityManager em) throws PersistenceException
			{
				Query queryObject = em.createNativeQuery(queryString);
				if (values != null) 
				{
					for (int i = 0; i < values.length; i++)
					{
						queryObject.setParameter(i + 1, values[i]);
					}
				}
				if (start >= 0)
				{
					queryObject.setFirstResult(start);
				}				
				if (length >= 0)
				{
					queryObject.setMaxResults(length);
				}
				return queryObject.getResultList();
			}
		});		
	}
	
	/**
	 * 获得某个实例的记录总数。
	 * @param cla
	 * @return
	 */
	public long getCount(final Class cla)
	{
		return getCount(cla.getSimpleName());
	}
	
	/**
	 * 	获得某个实例的记录总数。
	 * @param className 该名字为类名，不包含类的包名。
	 * @return
	 */
	public long getCount(final String className)
	{	
		try
		{
			final String queryString = "select count(*) from " + className;
			Object ret = getJpaTemplate().execute(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(queryString);
					
					return queryObject.getSingleResult();
				}
			});
			return (Long)ret;			
		}
		catch(RuntimeException e)
		{
			LogsUtility.error(e);
		}
		return 0;
		
	}
	
	/**
	 * 通过JPQL语句，获得记录的总数。
	 * @param query JPQL语句
	 * @param params 参数
	 * @return
	 */
	public Long getCountBySql(final String query, final Object ... params)
	{	
		try
		{
			Object ret = getJpaTemplate().execute(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(query);
					if (params != null) 
					{
						for (int i = 0; i < params.length; i++)
						{
							queryObject.setParameter(i + 1, params[i]);
						}
					}
					return queryObject.getSingleResult();
				}
			});
			return (Long)ret;
		}
		catch(RuntimeException e)
		{
			LogsUtility.error(e);
		}
		return Long.valueOf(0);
		
	}
	
	/**
	 * 通过Name标记的JPQL语句，获得记录的总数。
	 * @param query JPQL语句
	 * @param params 参数
	 * @return
	 */
	public Long getCountByNamedParams(final String query, final Map<String, ?> params)
	{	
		try
		{
			Object ret = getJpaTemplate().execute(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(query);
					if (params != null) 
					{
						for (Map.Entry<String, ?> entry : params.entrySet()) 
						{
							queryObject.setParameter(entry.getKey(), entry.getValue());
						}
					}
					return queryObject.getSingleResult();
				}
			});
			return (Long)ret;
		}
		catch(RuntimeException e)
		{
			LogsUtility.error(e);
		}
		return Long.valueOf(0);
		
	}
	
	/**
	 * 通过某个对象实例得到所有的对象
	 * @param instance
	 * @return
	 */
	public List<T> findByExample(T instance)
	{
		//LogsUtility.debug("finding " + instance + " instance by example");
		try
		{
			String query = "FROM " + instance.getClass().getSimpleName();
			List<T> results = getJpaTemplate().find(query);
			//LogsUtility.debug("find by example successful, result size: "	+ results.size());
			return results;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by example failed", re);
			throw re;
		}
	}
	
	/**
	 * 通过某个变量值查找该类的所有对象
	 * @param entityClass 数据库对象类名（不包含包名）
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public List<T> findByProperty(String entityClass, String propertyName, Object value)
	{
		//LogsUtility.debug("finding  " + entityClass + "instance with property: " + propertyName
		//		+ ", value: " + value);
		try
		{
			String queryString = "from " + entityClass + " as model where model." + propertyName + " = ?";
			return getJpaTemplate().find(queryString, value);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}

	/**
	 * 通过某个变量值查找该类的所有对象
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public List<T> findByProperty(Class<T> entityClass, String propertyName, Object value)
	{
		return findByProperty(entityClass.getSimpleName(), propertyName, value);
	}	

	/**
	 * 通过某个变量值查找该类的所有对象
	 * @param entityClass 数据库对象类名（不包含包名）
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public List<T> findByProperty(String entityClass, String propertyName, final Object value, final int start, final int length)
	{
		//LogsUtility.debug("finding  " + entityClass + "instance with property: " + propertyName
		//		+ ", value: " + value);
		try
		{
			final String queryString = "from " + entityClass + " as model where model." + propertyName + " = ?";
			List ret = getJpaTemplate().executeFind(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(queryString);
					queryObject.setParameter(1, value);
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (length >= 0)
					{
						queryObject.setMaxResults(length);
					}
					return queryObject.getResultList();
				}
			});
			return ret;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find by property name failed", re);
			throw re;
		}
	}

	/**
	 * 通过某个变量值查找该类的所有对象
	 * @param entityClass 数据库对象类名（不包含包名）
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public List<T> findByProperty(Class<T> entityClass, String propertyName, Object value, int start, int length)
	{
		return findByProperty(entityClass.getSimpleName(), propertyName, value, start, length);
	}
	
	/**
	 * 查找所有对象
	 * @param entityClass 数据库对象类
	 * @return
	 */
	public List<T> findAll(Class<T> entityClass)
	{
		return findAll(entityClass.getSimpleName());
	}
	
	/**
	 * 查找对象
	 * @param entityClass 数据库对象类
	 * @return
	 */
	public T find(final Class<T> entityClass, final Object id)
	{
		return getJpaTemplate().find(entityClass, id);
	}
	
	/**
	 * 查找对象
	 * @param entityClass 数据库对象类名（包含包名）
	 * @return
	 */
	public T find(final String entityClass, final Object id)
	{
		//LogsUtility.debug("finding instances");
		try
		{
			Class<T> cl = (Class<T>)Class.forName(entityClass);
			return getJpaTemplate().find(cl, id);
		}
		catch(Exception e)
		{
			LogsUtility.error("find  failed", e);
			return null;
		}
	}
	
	/**
	 * 查找所有对象
	 * @param entityClass 数据库对象类名（不包含包名）
	 * @return
	 */
	public List<T> findAll(String entityClass)
	{
		//LogsUtility.debug("finding all instances");
		try
		{
			String queryString = "FROM " + entityClass;
			return getJpaTemplate().find(queryString);
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("find all failed", re);
			throw re;
		}
	}
		
	/**
	 * 查找所有对象
	 * @param entityClass
	 * @return
	 */
	public List<T> findAll(Class<T> entityClass, int first, int length)
	{
		return findAll(entityClass.getSimpleName(), first, length);
	}
	
	/**
	 * 查找所有对象
	 * @param entityClass
	 * @return
	 */
	public List<T> findAll(String entityClass, final int start, final int length)
	{
		//LogsUtility.debug(" get all instance ");
		try
		{
			final String queryString = "FROM " + entityClass;
			List<T> ret = getJpaTemplate().executeFind(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(queryString);
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (length >= 0)
					{
						queryObject.setMaxResults(length);
					}
					return queryObject.getResultList();
				}
			});
			return ret;			
		}
		catch(RuntimeException e)
		{
			LogsUtility.error("find all failed", e);
			throw e;
		}
	}

	/**
	 * 得到对象
	 * @param queryString JPQL语句
	 * @param values
	 * @return
	 */
	public List<T> findAllBySql(final String queryString, final Object... values)
	{
		//LogsUtility.debug(" find instance ");
		try
		{
			return getJpaTemplate().find(queryString, values);
		}
		catch(RuntimeException e)
		{
			LogsUtility.error("find failed", e);
			throw e;
		}
	}
	
	/**
	 * 得到对象
	 * @param queryString JPQL语句
	 * @param values
	 * @return
	 */
	public List<T> findAllBySql(final int start, final int length, final String queryString, final Object... values)
	{
		//LogsUtility.debug(" find instance ");
		try
		{
			return getJpaTemplate().executeFind(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException 
				{
					Query queryObject = em.createQuery(queryString);
					if (values != null) 
					{
						for (int i = 0; i < values.length; i++)
						{
							queryObject.setParameter(i + 1, values[i]);
						}
					}
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (length >= 0)
					{
						queryObject.setMaxResults(length);
					}
					return queryObject.getResultList();
				}
			});
		}
		catch(RuntimeException e)
		{
			LogsUtility.error("find failed", e);
			throw e;
		}
	}
	
	/**
	 * 合并实例 
	 * @param detachedInstance
	 * @return
	 */
	public T merge(T detachedInstance)
	{
		//LogsUtility.debug("merging " + detachedInstance + " instance");
		try
		{
			T result = (T) getJpaTemplate().merge(detachedInstance);
			LogsUtility.debug("merge successful");
			return result;
		}
		catch (RuntimeException re)
		{
			LogsUtility.error("merge failed", re);
			throw re;
		}
	}
	
	/**
	 * 
	 * @param queryString  JPQL语句
	 * @param params
	 * @return
	 */
	public List<T> findByNamedParams(final String queryString, final Map<String, ?> params)
	{
		try
		{
			return getJpaTemplate().findByNamedParams(queryString, params);
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedParams failed", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param queryString  JPQL语句
	 * @param params
	 * @param start
	 * @param count
	 * @return
	 */
	public List<T> findByNamedParams(final String queryString, final Map<String, ?> params, final int start, final int count)
	{
		try
		{
			return getJpaTemplate().executeFind(new JpaCallback() 
			{
				public Object doInJpa(EntityManager em) throws PersistenceException 
				{
					Query queryObject = em.createQuery(queryString);
					if (params != null) 
					{
						for (Map.Entry<String, ?> entry : params.entrySet()) 
						{
							queryObject.setParameter(entry.getKey(), entry.getValue());
						}
					}
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (count >= 0)
					{
						queryObject.setMaxResults(count);
					}
					return queryObject.getResultList();
				}
			});
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedParams failed", e);
			return null;
		}
	}

	/**
	 * 
	 * @param queryName  
	 * @return
	 * @throws DataAccessException
	 */
	public List<T> findByNamedQuery(String queryName)
	{
		return findByNamedQuery(queryName, (Object[]) null);
	}

	public List<T> findByNamedQuery(final String queryName, final Object... values)
	{
		try
		{
			return getJpaTemplate().findByNamedQuery(queryName, values);
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedQuery failed", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param queryName
	 * @return
	 * @throws DataAccessException
	 */
	public List<T> findByNamedQuery(String queryName, final int start, final int count)
	{
		return findByNamedQuery(start, count, queryName, (Object[]) null);
	}

	/**
	 * 
	 * @param start
	 * @param count
	 * @param queryName
	 * @param values
	 * @return
	 */
	public List<T> findByNamedQuery(final int start, final int count, final String queryName, final Object... values)
	{
		try
		{
			return getJpaTemplate().executeFind(new JpaCallback() 
			{
				public Object doInJpa(EntityManager em) throws PersistenceException 
				{
					Query queryObject = em.createNamedQuery(queryName);
					if (values != null) 
					{
						for (int i = 0; i < values.length; i++)
						{
							queryObject.setParameter(i + 1, values[i]);
						}
					}
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (count >= 0)
					{
						queryObject.setMaxResults(count);
					}
					return queryObject.getResultList();
				}
			});
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedQuery failed", e);
			return null;
		}
	}

	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 * @throws DataAccessException
	 */
	public List findByNamedQueryAndNamedParams(final String queryName, final Map<String, ?> params)
	{
		try
		{
			return getJpaTemplate().findByNamedQueryAndNamedParams(queryName, params);
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedQueryAndNamedParams failed", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	public List findByNamedQueryAndNamedParams(final String queryName, final Map<String, ?> params, final int start, final int count)
	{
		try
		{			
			return getJpaTemplate().executeFind(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException 
				{
					Query queryObject = em.createNamedQuery(queryName);
					if (params != null) 
					{
						for (Map.Entry<String, ?> entry : params.entrySet()) 
						{
							queryObject.setParameter(entry.getKey(), entry.getValue());
						}
					}
					if (start >= 0)
					{
						queryObject.setFirstResult(start);
					}
					if (count >= 0)
					{
						queryObject.setMaxResults(count);
					}
					return queryObject.getResultList();
				}
			});
		}
		catch(Exception e)
		{
			LogsUtility.error("findByNamedQueryAndNamedParams failed", e);
			return null;
		}
	}
	
	/**
	 * 通过sql获取一个对象实例。如果结果集中有多个对象，则只返回第一个对象。
	 * @param sql
	 * @param params
	 * @return
	 */
	public Object findOneObjectBySql(final String sql, final Object ... params)
	{
		try
		{
			Object ret = getJpaTemplate().execute(new JpaCallback()
			{
				public Object doInJpa(EntityManager em) throws PersistenceException
				{
					Query queryObject = em.createQuery(sql);
					if (params != null) 
					{
						for (int i = 0; i < params.length; i++)
						{
							queryObject.setParameter(i + 1, params[i]);
						}
					}
					queryObject.setMaxResults(1);
					return queryObject.getSingleResult();
				}
			});
			return ret;			
		}
		catch(NoResultException nr)    // 没有实例对象
		{
			return null;
		}
		catch(EmptyResultDataAccessException yy)    // 没有实例对象
		{
			return null;
		}
		catch(RuntimeException e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
	
}
