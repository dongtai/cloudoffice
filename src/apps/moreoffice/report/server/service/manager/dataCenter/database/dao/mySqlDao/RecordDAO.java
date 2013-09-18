package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.RecordIndex;

/**
 * 记录操作DAO
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
public class RecordDAO extends BaseHibernateDAO
{
    /**
     * 根据模板ID得到所有记录
     * 
     * @param templateID 模板ID
     * @return List Record列表
     */
    @ SuppressWarnings("unchecked")
    public List<Record> getRecord(long templateID)
    {
        try
        {
            String sql = "select R from Template T join T.records R where T.id = ?";
            return (List<Record>)find(sql, templateID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到记录对象
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Record 记录对象
     */
    @ SuppressWarnings("unchecked")
    public Record getRecord(long templateID, long recordID)
    {
        try
        {
            String sql = "select R from Template T join T.records R where T.id = ? and R.id = ?";
            List<Record> list = (List<Record>)find(sql, templateID, recordID);
            if (list != null && list.size() == 1)
            {
                return list.get(0);
            }
            return null;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到制定记录下的记录索引
     * 
     * @param recordID 记录ID
     * @return List 记录索引集合
     */
    @ SuppressWarnings("unchecked")
    public List<RecordIndex> getReportIndex(long recordID)
    {
        try
        {
            String sql = "select RI from Record R join R.recordIndexs RI where R.id = ?";
            return (List<RecordIndex>)find(sql, recordID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据表名和数据id得到对应的记录索引对象
     * 
     * @param rtableID 表ID
     * @param id 数据id
     * @return RecordIndex 记录索引
     */
    @ SuppressWarnings("rawtypes")
    public RecordIndex getRecordIndex(long rtableID, long id)
    {
        try
        {
            String sql = "select RI from RecordIndex RI join RI.rtable R where R.id = ? and RI.dataID = ?";
            List list = find(sql, rtableID, id);
            if (list != null && !list.isEmpty() && list.get(0) instanceof RecordIndex)
            {
                return (RecordIndex)list.get(0);
            }
            return null;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
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
    public List findByExample(final long templateID, final int firstResult, final int maxResults)
    {
        try
        {
            List resultList = getHibernateTemplate().executeFind(new HibernateCallback()
            {
                public Object doInHibernate(Session arg0) throws HibernateException, SQLException
                {
                    String hql = "select R from Template T join T.records R where T.id = "
                        + templateID;
                    Query query = arg0.createQuery(hql);
                    query.setFirstResult(firstResult);
                    query.setMaxResults(maxResults);

                    return query.list();
                }

            });
            return resultList;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }
}