package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.commons.domain.info.Organization;

/**
 * 用户、组织、角色管理接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IUserManager
{
    /**
     * 是否是报表设计者
     * 
     * @param userID 用户ID
     * @return boolean 是否是报表设计者
     */
    boolean isReportDesigner(long userID);

    /**
     * 得到组织架构
     * 
     * @param userID 用户ID
     * @return Organization 根节点
     */
    Organization getOrganization(long userID);

    /**
     * 通过组织ID得到组织用户对象
     * 
     * @param orgID 组织ID
     * @return ArrayList<OrgUser> 组织用户对象
     */
    ArrayList<OrgUser> getOrgUserByOrg(long orgID);

    /**
     * 通过用户ID得到组织用户对象
     * 
     * @param userID 用户ID
     * @return ArrayList<OrgUser> 组织用户对象
     */
    ArrayList<OrgUser> getOrgUserByUser(long userID);

    /**
     * 得到设计者列表
     * 
     * @param userID 用户ID
     * @return Result 设计者列表
     */
    ArrayList<User> getDesignerList(long userID);

    /**
     * 得到用户
     * 
     * @param userID 用户ID
     * @return User 用户
     */
    User getUser(long userID);

    /**
     * 得到用户
     * 
     * @param loginName 登录名
     * @return User 用户
     */
    User getUser(String loginName);
}