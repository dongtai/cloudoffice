package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.server.service.manager.dataCenter.IPermissionDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.PermissionDAO;


/**
 * 数据库操作实现类：权限
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
public class PermissionDB extends DataBase implements IPermissionDB
{
    // dao对象
    private PermissionDAO dao;

    /**
     * 得到指定模板或数据表上的所有权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @return List<Permission> 权限集
     */
    public List<Permission> getPermission(int type, long typeID)
    {
        return dao.getPermission(type, typeID);
    }
    
    /**
     * 得到指定模板或数据表上的指定类型的权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param pType 基本权限还是设计权限
     * @return List<Permission> 权限集
     */
    public List<Permission> getPermission(int type, long typeID, int pType)
    {
        return dao.getPermission(type, typeID, pType);
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
    public List<Permission> getPermission(int type, long typeID, short objectType, long objectID)
    {
        return dao.getPermission(type, typeID, objectType, objectID);
    }
    
    /**
     * 优化
     */
    public void optimization()
    {
        dao.optimization();
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(PermissionDAO dao)
    {
        this.dao = dao;
        setBaseDao(dao);
    }
}