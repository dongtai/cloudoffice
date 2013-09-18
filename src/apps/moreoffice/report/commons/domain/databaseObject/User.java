package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;

/**
 * 用户
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
public class User extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -3806048335719843938L;
    // id
    private Long id;
    // 用户名
    private String userName;
    // 登录名
    private String loginName;
    // 登录密码
    private String passWord;
    // token
    private String token;

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
     * @return 返回 userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName 设置 userName
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return 返回 loginName
     */
    public String getLoginName()
    {
        return loginName;
    }

    /**
     * @param loginName 设置 loginName
     */
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    /**
     * @return 返回 passWord
     */
    public String getPassWord()
    {
        return passWord;
    }

    /**
     * @param passWord 设置 passWord
     */
    public void setPassWord(String passWord)
    {
        this.passWord = passWord;
    }

    /**
     * @return 返回 token
     */
    public String getToken()
    {
        return token;
    }

    /**
     * @param token 设置 token
     */
    public void setToken(String token)
    {
        this.token = token;
    }

    /**
     * @return 返回 permission
     */
    public Permission getPermission()
    {
        if (permission == null)
        {
            permission = new Permission();
            permission.setType(PermissionCons.DESIGN);
            permission.setObjectID(getId());
            permission.setObjectType(PermissionCons.USER);
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
        return userName;
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
        // 用户名
        params.put(ParamCons.USERNAME, userName);
        // 登录名
        params.put(ParamCons.LOGINNAME, loginName);
        // 登录密码
        params.put(ParamCons.PASSWORD, passWord);
        // token
        params.put(ParamCons.TOKEN, token);

        return params;
    }
}