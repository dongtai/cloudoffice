package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.server.service.manager.dataCenter.IPermissionDB;

/**
 * 权限管理器
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
public class PermissionManager extends BaseManager
{
    // 数据库操作接口
    private IPermissionDB db;
    // 用户管理器
    private IUserManager userM;

    /**
     * 对Permission进行存盘前处理
     * 
     * @param template 模板对象
     */
    protected void beforeSave(Template template)
    {
        Set<Permission> permissions = template.getPermissions();
        if (permissions == null || permissions.isEmpty())
        {
            return;
        }
        
        for (Permission permission : permissions)
        {
            permission.setTemplate(template);
        }
    }

    /**
     * 得到指定模板或表上的所有权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @return Set<Permission> 权限集
     */
    public Set<Permission> getPermission(int type, long typeID)
    {
        ArrayList<Permission> permissions = (ArrayList<Permission>)db.getPermission(type, typeID);
        if (permissions == null)
        {
            return null;
        }

        HashSet<Permission> set = new HashSet<Permission>();
        set.addAll(permissions);
        return set;
    }

    /**
     * 得到指定模板或表上的指定类型的权限
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param pType 基本权限还是设计权限
     * @return Set<Permission> 权限集
     */
    public Set<Permission> getPermission(int type, long typeID, int pType)
    {
        ArrayList<Permission> permissions = (ArrayList<Permission>)db.getPermission(type, typeID,
            pType);
        if (permissions == null)
        {
            return null;
        }

        HashSet<Permission> set = new HashSet<Permission>();
        set.addAll(permissions);
        return set;
    }

    /**
     * 得到指定用户在指定模板或表上的权限，用于权限判断
     * 
     * @param type 类型
     * @param typeID 类型ID
     * @param userID 用户ID
     * @return ArrayList<Permission> 权限集
     */
    public ArrayList<Permission> getPermission(int type, long typeID, long userID)
    {
        ArrayList<Permission> permissions = new ArrayList<Permission>();

        // 用户
        List<Permission> pList = db.getPermission(type, typeID, PermissionCons.USER, userID);
        permissions.addAll(pList);

        // 组织用户
        ArrayList<OrgUser> orgUserList = userM.getOrgUserByUser(userID);
        if (orgUserList != null && orgUserList.size() > 0)
        {
            for (OrgUser orgUser : orgUserList)
            {
                pList = db.getPermission(type, typeID, PermissionCons.ORG_USER, orgUser.getId());
                permissions.addAll(pList);
            }
        }

        return permissions;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IPermissionDB db)
    {
        this.db = db;
        setBasedb(db);
    }

    /**
     * @param userM 设置 userM
     */
    public void setUserM(IUserManager userM)
    {
        this.userM = userM;
    }
}