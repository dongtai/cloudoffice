package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.RecordIndex;

/**
 * 数据库操作接口：记录
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IRecordDB extends IDataBase
{
    /**
     * 根据模板ID得到所有记录
     * 
     * @param templateID 模板ID
     * @return List Record列表
     */
    List<Record> getRecord(long templateID);

    /**
     * 得到记录对象
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Record 记录对象
     */
    Record getRecord(long templateID, long recordID);

    /**
     * 得到指定记录下的记录索引
     * 
     * @param recordID 记录ID
     * @return List 记录索引集合
     */
    List<RecordIndex> getRecordIndex(long recordID);
    
    /**
     * 根据表id和数据id得到对应的记录索引对象
     * 
     * @param rtableID 表ID
     * @param id 数据id
     * @return RecordIndex 记录索引
     */
    RecordIndex getRecordIndex(long rtableID, long id);

    /**
     * 分段查询
     * 
     * @param templateID 模板ID
     * @param firstResult 开始位置
     * @param maxResults 最大值
     * @return List 结果集
     */
    @ SuppressWarnings("rawtypes")
    List findByExample(long templateID, int firstResult, int maxResults);
}