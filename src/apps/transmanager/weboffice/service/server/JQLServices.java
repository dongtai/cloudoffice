package apps.transmanager.weboffice.service.server;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.dao.NormalDAO;

/**
 * 单独出来jpa的jql语句或者原生sql语句的service。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Component(value=JQLServices.NAME)
public class JQLServices
{
    public static final String NAME = "JQLService";
    
	@Autowired
	private NormalDAO normalDAO;
	
	/**
	 * 执行JPQL语句
	 * @param query
	 * @return
	 */
	public Object excute(final String query)
	{
		return normalDAO.excute(query);
	}
	
	/**
	 * 执行JPQL语句
	 * @param query
	 * @param param 执行语句的参数
	 * @return
	 */
	public Object excute(final String query, final Object... param)
	{
		return normalDAO.excute(query, param);
	}
	
	/**
	 * 执行标准SQL语句
	 * @param query
	 * @return
	 */
	public Object excuteNativeSQL(final String query)
	{
		return normalDAO.excuteNativeSQL(query);
	}
	
	/**
	 * 得到某个类型对象的个数
	 * @param cla
	 * @return
	 */
	public long getEntityCount(final Class cla)
	{
		return getEntityCount(cla.getSimpleName());
	}
	
	/**
	 * 得到某个类型对象的个数 
	 * @param className
	 * @return
	 */
	public long getEntityCount(final String className)
	{
		return normalDAO.getCount(className);
	}
	
	/**
	 * 
	 * @param entity
	 */
	public void save(Object entity)
	{
		normalDAO.save(entity);
	}
	
	/**
	 * 集合中的所有对象
	 * @param entities
	 */
	public void saveAll(final Collection entities)
	{
		normalDAO.saveAll(entities);
	}
	
	/**
	 * 更新对象
	 * @param entity
	 * @return
	 */
	public void update(Object entity) 
	{
		normalDAO.update(entity);
	}
	
	/**
	 * 删除对象	
	 * @param entity
	 */
	public void delete(Object entity)
	{
		normalDAO.delete(entity);
	}
	
	/**
     * 查找所有对象
     * @param entityClass 数据库对象类
     * @return
     */
    public List findAll(Class entityClass)
    {
    	return normalDAO.findAll(entityClass);
    }
    
    /**
     * 批量删除某个对象数据。
     * @param entityClass 需要删除的实例对象
     * @param idName 需要删除对象的id字段名（该字段名字为对象中定义的属性名）
     * @param id
     */
    public void deleteEntityByID(Class entityClass, String idName, List<Long> id)
    {
    	normalDAO.deleteEntityByID(entityClass, idName, id);
    }
    
    /**
     * 删除某个对象数据。
     * @param entityClass 需要删除的实例对象
     * @param idName 需要删除对象的id字段名（该字段名字为对象中定义的属性名）
     * @param id
     */
    public void deleteEntityByID(Class entityClass, String idName, Long id)
    {
    	normalDAO.deleteEntityByID(entityClass, idName, id);
    }
	
    /**
	 * 得到对象
	 * @param queryString JPQL语句
	 * @param values查询参数。
	 * @return
	 */
	public List findAllBySql(final String queryString, final Object... values)
	{
		return normalDAO.findAllBySql(queryString, values);
	}
	
	/**
	 * 分页获取对象。
	 * @param start 开始位置，如果传入值小于0，从0位置开始
	 * @param length 查询长度，如果传入值小于0，则全部获取。
	 * @param queryString 查询的JPQL语句
	 * @param values 参数值。
	 * @return
	 */	
	public List findAllBySql(final int start, final int length, final String queryString, final Object... values)
	{
		return normalDAO.findAllBySql(start, length, queryString, values);
	}
	
	
	public Object getCount(String querySting, final Object... values)
	{
		return normalDAO.getCountBySql(querySting, values);
	}
	
	public Object getObjectByNativeSQL(String sql, int start, int length)
	{
		return normalDAO.excuteNativeSQL(sql, start, length);
	}
	public Object findOneObjectBySql(final String sql, final Object ... params)
	{
		return normalDAO.findOneObjectBySql(sql,params);
	}
	public List<ApprovalInfo> findInstance(final String query,final Object ... params)
	{
		return (List<ApprovalInfo>)normalDAO.findAllBySql(query, params);
	}
	
	public Object getEntity(Class entityClass,Object id)
	{
		return normalDAO.find(entityClass, id);
	}
	public Users getUsers(Long userid)
	{
		return (Users)normalDAO.find(Users.class, userid);
	}
	public Users getUsers(String username)
	{
		List<Users> list = (List<Users>)normalDAO.findAllBySql("select a from Users as a where a.userName=?", username);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
		
	}
}
