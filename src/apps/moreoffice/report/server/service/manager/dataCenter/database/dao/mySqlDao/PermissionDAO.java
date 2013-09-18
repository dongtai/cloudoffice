package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;

/**
 * 权限操作DAO
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class PermissionDAO extends BaseHibernateDAO
{

    /**
     * 得到指定模板或数据表上的指定类型的权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @return Set<Permission> 权限集
     */
    @ SuppressWarnings("unchecked")
    public List<Permission> getPermission(int type, long typeID)
    {
        try
        {
            String sql = "select P from Template T join T.permissions P where T.id = ?";
            return find(sql, typeID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到指定模板或数据表上的指定类型的权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param pType 基本权限还是设计权限
     * @return Set<Permission> 权限集
     */
    @ SuppressWarnings("unchecked")
    public List<Permission> getPermission(int type, long typeID, int pType)
    {
        try
        {
            String sql = "select P from Template T join T.permissions P where T.id = ? and P.type = ?";
            return find(sql, typeID, pType);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 得到指定用户在指定模板或数据表上的的权限集
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param ObjectType 权限对应的作用对象类型
     * @param ObjectID 权限对应的作用对象ID
     * @return List<Permission> 权限集
     */
    @ SuppressWarnings("unchecked")
    public List<Permission> getPermission(int type, long typeID, short objectType, long objectID)
    {
        try
        {
            String sql = "select P from " + getMainTableName(type)
                + " T join T.permissions P where T.id = ? and P.objectType = ? and P.objectID = ?";
            return find(sql, typeID, objectType, objectID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 优化
     */
    @ SuppressWarnings("rawtypes")
    public void optimization()
    {
        try
        {
            List list = find("select P from Permission P where P.dtable is null and P.template is null");
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

    /*
     * 得到主表名
     */
    private String getMainTableName(int type)
    {
        switch (type)
        {
            case PermissionCons.TEMPLATE:
                return "Template";
            case PermissionCons.TABLE:
                return "DataTable";
            default:
                return "";
        }
    }
}