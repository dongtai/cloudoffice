package apps.moreoffice.report.server.service.manager;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataBase;

/**
 * 管理器基础类
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
public class BaseManager
{
    // 数据库操作接口类
    protected IDataBase basedb;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        return basedb.save(entity);
    }

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     */
    public void delete(DataBaseObject entity)
    {
        basedb.delete(entity);
    }

    /**
     * @param basedb 设置 basedb
     */
    public void setBasedb(IDataBase basedb)
    {
        this.basedb = basedb;
    }
}