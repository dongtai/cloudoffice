package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.Permission;

/**
 * 数据库操作接口：权限
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
public interface IPermissionDB extends IDataBase
{
    /**
     * 得到指定模板或数据表上的所有权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @return List<Permission> 权限集
     */
    List<Permission> getPermission(int type, long typeID);

    /**
     * 得到指定模板或数据表上的指定类型的权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param pType 基本权限还是设计权限
     * @return List<Permission> 权限集
     */
    List<Permission> getPermission(int type, long typeID, int pType);

    /**
     * 得到指定用户在指定模板或数据表上的的权限集
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param ObjectType 权限对应的作用对象类型
     * @param ObjectID 权限对应的作用对象ID
     * @return List<Permission> 权限集
     */
    List<Permission> getPermission(int type, long typeID, short objectType, long objectID);
    
    /**
     * 优化
     */
    void optimization();
}