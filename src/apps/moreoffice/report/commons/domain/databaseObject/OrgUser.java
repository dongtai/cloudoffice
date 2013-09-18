package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.info.Organization;

/**
 * 组织用户
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
public class OrgUser extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 537377295597624619L;
    // id
    private Long id;
    // 组织对象
    private Organization organization;
    // 用户对象
    private User user;

    // 权限
    private transient Permission permission;

    /**
     * @return 返回 id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id 设置 id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return 返回 organization
     */
    public Organization getOrganization()
    {
        return organization;
    }

    /**
     * @param organization 设置 organization
     */
    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    /**
     * @return 返回 user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user 设置 user
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * @return 返回 permission
     */
    public Permission getPermission()
    {
        if (permission == null)
        {
            permission = new Permission();
            permission.setType(PermissionCons.BASE);
            permission.setObjectID(getId());
            permission.setObjectType(PermissionCons.ORG_USER);
        }
        return permission;
    }

    /**
     * @param permission 设置 permission
     */
    public void setPermission(Permission permission)
    {
        this.permission = permission;
    }

    /**
     * 对话盒用
     */
    public String toString()
    {
        String separator = "-";
        return organization.getPath(separator) + separator + user.getUserName();
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // id
        params.put(ParamCons.ID, id);
        // 组织对象
        if (organization != null)
        {
            params.put(ParamCons.ORGANIZATION, organization.getJsonObj());
        }
        // 用户对象
        if (user != null)
        {
            params.put(ParamCons.USER, user);
        }

        return params;
    }
}