package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.RecordIndex;
import apps.moreoffice.report.server.service.manager.dataCenter.IRecordDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.RecordDAO;

/**
 * 数据库操作实现类：记录
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
public class RecordDB extends DataBase implements IRecordDB
{
    // dao对象
    private RecordDAO dao;

    /**
     * 根据模板ID得到所有记录
     * 
     * @param templateID 模板ID
     * @return List<Record> Record列表
     */
    public List<Record> getRecord(long templateID)
    {
        return dao.getRecord(templateID);
    }

    /**
     * 得到记录对象
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Record 记录对象
     */
    public Record getRecord(long templateID, long recordID)
    {
        return dao.getRecord(templateID, recordID);
    }

    /**
     * 得到指定记录下的记录索引
     * 
     * @param recordID 记录ID
     * @return List 记录索引集合
     */
    public List<RecordIndex> getRecordIndex(long recordID)
    {
        return dao.getReportIndex(recordID);
    }
    
    /**
     * 分段查询
     * 
     * @param templateID 模板ID
     * @param firstResult 开始位置
     * @param maxResults 最大值
     * @return List 结果集
     */
    @ SuppressWarnings("rawtypes")
    public List findByExample(long templateID, int firstResult, int maxResults)
    {
        return dao.findByExample(templateID, firstResult, maxResults);
    }
    
    /**
     * 根据表id和数据id得到对应的记录索引对象
     * 
     * @param rtableID 表ID
     * @param id 数据id
     * @return RecordIndex 记录索引
     */
    public RecordIndex getRecordIndex(long rtableID, long id)
    {
        return dao.getRecordIndex(rtableID, id);
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(RecordDAO dao)
    {
        this.dao = dao;
        setBaseDao(dao);
    }
}