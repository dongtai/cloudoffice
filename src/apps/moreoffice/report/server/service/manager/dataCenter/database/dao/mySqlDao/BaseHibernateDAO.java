package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;

/**
 * 基本DAO对象
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class BaseHibernateDAO extends HibernateDaoSupport
{
    /**
     * 保存或更新实体对象
     * 
     * @param entity 实体对象
     * @param merge 是否合并
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject saveOrUpdate(DataBaseObject entity, boolean merge)
    {
        try
        {
            if (merge)
            {
                /**
                 * 只所以没有直接调用saveOrUpdate方法，原因是当修改属性时
                 * 客户端传过来的对象跟hibernate缓存中的对象不是一个对象，但
                 * 拥有相同的id，hibernate就会报错，因此需要合并 
                 */
                entity = (DataBaseObject)getHibernateTemplate().merge(entity);
            }
            getHibernateTemplate().saveOrUpdate(entity);
            return entity;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     */
    public void delete(final DataBaseObject entity)
    {
        try
        {
            getHibernateTemplate().delete(entity);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 执行hql
     * 
     * @param hql hibernate的sql语句
     */
    public void update(String hql)
    {
        try
        {
            getSession().createSQLQuery(hql).executeUpdate();
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据id得到实体对象(取缓存中数据)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    public DataBaseObject getEntityByID(final String entityName, long id)
    {
        try
        {
            return (DataBaseObject)getHibernateTemplate().get(entityName, id);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据id得到实体对象(从数据库中取)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    public DataBaseObject loadEntityByID(final String entityName, long id)
    {
        try
        {
            return (DataBaseObject)getHibernateTemplate().load(entityName, id);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 单条件查询
     * 
     * @param sql 查询语句
     * @param paramName 字段名
     * @param value 查询值
     * @return List 对象集
     */
    @ SuppressWarnings("rawtypes")
    public List findByNamedParam(final String sql, final String paramName, final Object value)
    {
        try
        {
            return getHibernateTemplate().findByNamedParam(sql, paramName, value);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 多条件查询
     * 
     * @param sql 查询语句
     * @param values 条件值
     * @return List 对象集
     */
    @ SuppressWarnings("rawtypes")
    public List find(final String sql, final Object...values)
    {
        try
        {
            return getHibernateTemplate().find(sql, values);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 分段查询
     * 
     * @param exampleEntity 示例实体对象
     * @param firstResult 开始位置
     * @param maxResults 最大值
     * @return List 结果集
     */
    @ SuppressWarnings("rawtypes")
    public List findByExample(DataBaseObject exampleEntity, int firstResult, int maxResults)
    {
        try
        {
            return getHibernateTemplate().findByExample(exampleEntity, firstResult, maxResults);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 判断是否是空的数据表
     * 
     * @param entityName 实体名称
     * @return boolean 是否是空表 
     */
    public boolean isNullTable(String entityName)
    {
        try
        {
            Long count = (Long)getSession().createQuery("select count(*) from " + entityName)
                .uniqueResult();
            return count == 0 ? true : false;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    // ----------实体名常量----------
    protected final String entity = "com.yozo.report.commons.domain.databaseObject.";
    protected final String entity_template = entity + "Template";
    protected final String entity_RTable = entity + "RTable";
    protected final String entity_dataTable = entity + "DataTable";
    protected final String entity_dataType = entity + "DataType";
    protected final String entity_dataRule = entity + "DataRule";
    protected final String entity_TableRule = entity + "TableRule";
}