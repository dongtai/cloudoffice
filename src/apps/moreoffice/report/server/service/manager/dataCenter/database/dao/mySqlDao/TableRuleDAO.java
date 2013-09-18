package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.TableRule;

/**
 * 表间规则DAO
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
public class TableRuleDAO extends BaseHibernateDAO
{
    /**
     * 根据表间规则ID得到表间规则
     * 
     * @param id 表间规则ID
     * @return TableRule 表间规则
     */
    public TableRule getTableRuleByID(long id)
    {
        try
        {
            return (TableRule)getEntityByID(entity_TableRule, id);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 优化
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public void optimization()
    {
        try
        {
            // TableRule
            List list = find("select TR from TableRule TR where TR.currentTemplate is null");
            // FillMode
            list.addAll(find("select FM from FillMode FM where FM.readRule is null"));
            // JoinCond
            list.addAll(find("select J from JoinCond J where J.readRule is null"));
            // WriteMode
            list.addAll(find("select WM from WriteMode WM where WM.newFormRule is null"));
            // WriteModeItem
            list.addAll(find("select WMI from WriteModeItem WMI where WMI.modifyRule is null and WMI.addDetailRule is null and WMI.writeMode is null"));
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