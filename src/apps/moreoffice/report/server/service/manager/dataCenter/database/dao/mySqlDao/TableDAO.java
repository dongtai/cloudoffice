package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;

/**
 * 表和字段DAO
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
public class TableDAO extends BaseHibernateDAO
{
    /**
     * 得到所有的已创建的用户数据表
     * 
     * @return List 已创建的用户数据表
     */
    @ SuppressWarnings("rawtypes")
    public List getDataTables()
    {
        try
        {
            String sql = "select D from " + entity_dataTable + " D where D.createState = ?";
            return find(sql, TableCons.HASCREATED);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到外部数据源对应的RTable列表
     * 
     * @return List 外部数据源对应的RTable列表
     */
    @ SuppressWarnings("rawtypes")
    public List getOtherRTables()
    {
        try
        {
            String sql = "select R from " + entity_RTable
                + " R join R.dtable D where R.template is null and D.dataSource is not null";
            return find(sql);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 通过RTableID得到对应的RField
     * 
     * @param rtableID RTable的ID
     * @return List RField集合
     */
    @ SuppressWarnings("rawtypes")
    public List getRFieldByRTableID(long rtableID)
    {
        try
        {
            String sql = "select RF from RTable RT join RT.rfields RF where RT.id = ?";
            return find(sql, rtableID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 检查表名是否合法
     * 
     * @param tableName 表名
     * @return boolean true:合法；false:不合法
     */
    @ SuppressWarnings("rawtypes")
    public boolean checkTableName(String tableName)
    {
        try
        {
            String sql = "select D from DataTable D where D.realName = ? or D.name = ?";
            List list = find(sql, tableName, tableName);
            return !(list != null && list.size() > 0);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据RTableID得到RTable对象
     * 
     * @param rtableID rtableID
     * @return RTable RTable对象
     */
    public RTable getRTableByID(long rtableID)
    {
        try
        {
            return (RTable)getEntityByID(entity_RTable, rtableID);
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
            List list = find("select R from RTable R join R.dtable D where R.template is null and D.dataSource is null");
            list.addAll(find("select H from HyperLinkInfo H where H.hyperLink is null"));
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