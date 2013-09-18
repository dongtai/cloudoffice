package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.DataRule;

/**
 * 数据规范DAO
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
public class DataRuleDAO extends BaseHibernateDAO
{
    /**
     * 得到所有数据规范
     * 
     * @return Result 结果
     */
    @ SuppressWarnings("rawtypes")
    public List getAllDataRule()
    {
        try
        {
            String sql = "from " + entity_dataRule;
            return find(sql);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到指定类型的数据规范列表
     * 
     * @param type 类型
     * @return Result 结果
     */
    @ SuppressWarnings("rawtypes")
    public List getDataRuleByType(short type)
    {
        try
        {
            String sql = "select D from DataRule D where D.type = ?";
            return find(sql, type);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param dataRuleID 数据规范ID
     * @return Result 数据规范
     */
    public DataRule getDataRuleByID(long dataRuleID)
    {
        try
        {
            return (DataRule)getEntityByID(entity_dataRule, dataRuleID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 优化ListSelectDataItem
     */
    @ SuppressWarnings("rawtypes")
    public void optimizationL()
    {
        try
        {
            List list = find("select L from ListSelectDataItem L where L.listSelectRule is null");
            if (list != null && list.size() > 0)
            {
                for (int i = 0, size = list.size(); i < size; i++)
                {
                    getHibernateTemplate().delete(list.get(i));
                }
            }
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }
    
    /**
     * 优化LevelField
     */
    @ SuppressWarnings("rawtypes")
    public void optimizationT()
    {
        try
        {
            List list = find("select L from LevelField L where L.treeSelectRule is null");
            if (list != null && list.size() > 0)
            {
                for (int i = 0, size = list.size(); i < size; i++)
                {
                    getHibernateTemplate().delete(list.get(i));
                }
            }
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }
}