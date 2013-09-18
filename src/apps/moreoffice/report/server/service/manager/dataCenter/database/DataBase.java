package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataBase;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.BaseHibernateDAO;

/**
 * 数据库操作实现类：公共
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataBase implements IDataBase
{
    // 基本操作DAO
    private BaseHibernateDAO baseDao;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        return save(entity, true);
    }
    
    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @param merge 是否合并
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity, boolean merge)
    {
        return baseDao.saveOrUpdate(entity, merge);
    }

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     */
    public void delete(DataBaseObject entity)
    {
        baseDao.delete(entity);
    }

    /**
     * 删除实体对象
     * 
     * @param id 实体对象ID
     */
    public void delete(long id)
    {

    }

    /**
     * 根据id得到实体对象(取缓存中数据)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    public DataBaseObject getEntityByID(String entityName, long id)
    {
        return baseDao.getEntityByID(entityName, id);
    }

    /**
     * 根据id得到实体对象(从数据库中取)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    public DataBaseObject loadEntityByID(String entityName, long id)
    {
        return baseDao.loadEntityByID(entityName, id);
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
        return baseDao.findByExample(exampleEntity, firstResult, maxResults);
    }

    /**
     * 判断是否是空的数据表
     * 
     * @param entityName 实体名称
     * @return boolean 是否是空的数据表
     */
    public boolean isNullTable(String entityName)
    {
        return baseDao.isNullTable(entityName);
    }

    /**
     * @param baseDao 设置 baseDao
     */
    public void setBaseDao(BaseHibernateDAO baseDao)
    {
        this.baseDao = baseDao;
    }
}