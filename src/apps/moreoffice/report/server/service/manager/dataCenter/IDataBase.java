package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;

/**
 * 数据库操作接口：公共
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
public interface IDataBase
{
    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @param DataBaseObject 保存后的对象
     */
    DataBaseObject save(DataBaseObject entity);
    
    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @param merge 是否合并
     * @param DataBaseObject 保存后的对象
     */
    DataBaseObject save(DataBaseObject entity, boolean merge);

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     */
    void delete(DataBaseObject entity);

    /**
     * 删除实体对象
     * 
     * @param id 实体对象ID
     */
    void delete(long id);

    /**
     * 根据id得到实体对象(取缓存中数据)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    DataBaseObject getEntityByID(String entityName, long id);

    /**
     * 根据id得到实体对象(从数据库中取)
     * 
     * @param entityName 实体对象名称
     * @param id id
     * @return DataBaseObject 实体对象
     */
    DataBaseObject loadEntityByID(String entityName, long id);

    /**
     * 分段查询
     * 
     * @param exampleEntity 示例实体对象
     * @param firstResult 开始位置
     * @param maxResults 最大值
     * @return List 结果集
     */
    @ SuppressWarnings("rawtypes")
    List findByExample(DataBaseObject exampleEntity, int firstResult, int maxResults);

    /**
     * 判断是否是空的数据表
     * 
     * @param entityName 实体名称
     * @return boolean 是否是空的数据表
     */
    boolean isNullTable(String entityName);
}