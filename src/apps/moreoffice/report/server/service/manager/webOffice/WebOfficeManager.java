package apps.moreoffice.report.server.service.manager.webOffice;

import java.util.ArrayList;
import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.commons.domain.info.Organization;
import apps.moreoffice.report.commons.domain.info.TreeInfo;
import apps.moreoffice.report.server.service.manager.BaseManager;
import apps.moreoffice.report.server.service.manager.IUserManager;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.service.dao.StructureDAO;


/**
 * 与webOffice的用户、角色、组织架构整合
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
public class WebOfficeManager extends BaseManager implements IUserManager
{
    // 云办公的StructureDAO
    private StructureDAO structureDAO;

    /**
     * 是否是报表设计者
     * 
     * @param userID 用户ID
     * @return boolean 是否是报表设计者
     */
    public boolean isReportDesigner(long userID)
    {
        Users user = structureDAO.findUserById(userID);
        return isReportDesigner(user);
    }

    /*
     * 是否是报表设计者
     */
    private boolean isReportDesigner(Users user)
    {
        // TODO
        if (user.getRole() == (short)Constant.PART_ADMIN)
        {
            return true;
        }
        return false;
    }

    /**
     * 得到组织架构
     * 
     * @param userID 用户ID
     * @return Organization 根节点
     */
    public Organization getOrganization(long userID)
    {
        Users user = structureDAO.findUserById(userID);
        if (user == null || user.getCompany() == null)
        {
            return null;
        }
        // 得到所有组织结构
        List<Organizations> allOrgs = structureDAO.findAllOrganizations(user.getCompany().getId());
        // 初始化根节点
        Organization root = new Organization();
        // 根节点的id暂时设置为-1;
        root.setId(-1);
        root.setName(user.getCompany().getName());
        if (allOrgs != null && !allOrgs.isEmpty())
        {
            Organization child;
            ArrayList<TreeInfo> childList = new ArrayList<TreeInfo>();
            for (Organizations org : allOrgs)
            {
                if (org.getParent() == null)
                {
                    // 初始化第一层节点
                    child = new Organization();
                    child.setId(org.getId());
                    child.setName(org.getName());
                    childList.add(child);
                    // 初始化第一层节点的孩子节点
                    createChilds(child, allOrgs);
                }
            }

            if (childList.size() > 0)
            {
                Organization[] childs = new Organization[childList.size()];
                childList.toArray(childs);
                root.setChilds(childs);
            }
        }

        return root;
    }

    /*
     * 遍历得到整个组织架构树
     */
    private void createChilds(Organization root, List<Organizations> allOrgs)
    {
        // 遍历得到所有孩子节点
        ArrayList<TreeInfo> childList = new ArrayList<TreeInfo>();
        for (Organizations org : allOrgs)
        {
            if (org.getParent() != null && org.getParent().getName().equals(root.getName()))
            {
                Organization child = new Organization();
                child.setId(org.getId());
                child.setName(org.getName());
                childList.add(child);
                createChilds(child, allOrgs);
            }
        }

        // 设置孩子节点到父节点上
        if (childList.size() > 0)
        {
            Organization[] childs = new Organization[childList.size()];
            childList.toArray(childs);
            root.setChilds(childs);
        }
    }

    /**
     * 通过组织ID得到组织用户对象
     * 
     * @param orgID 组织ID
     * @return ArrayList<OrgUser> 组织用户对象
     */
    public ArrayList<OrgUser> getOrgUserByOrg(long orgID)
    {
        // 调用云办公方法，得到当前组织下所有的用户
        List<UsersOrganizations> userOrgs = structureDAO.findUsersOrganizationsByOrgId(orgID);
        if (userOrgs == null || userOrgs.size() < 1)
        {
            return null;
        }

        // 把云办公的对象转换为报表对象
        ArrayList<OrgUser> orgUserList = new ArrayList<OrgUser>();
        Organization organization = new Organization();
        organization.setId(orgID);
        for (UsersOrganizations userOrg : userOrgs)
        {
            OrgUser orgUser = new OrgUser();
            orgUser.setId(userOrg.getId());

            // 组织对象
            organization.setName(userOrg.getOrganization().getName());
            orgUser.setOrganization(organization);

            // 用户对象
            User user = new User();
            user.setId(userOrg.getUser().getId());
            user.setUserName(userOrg.getUser().getRealName());
            orgUser.setUser(user);

            orgUserList.add(orgUser);
        }

        return orgUserList;
    }

    /**
     * 通过用户ID得到组织用户对象
     * 
     * @param userID 用户ID
     * @return ArrayList<OrgUser> 组织用户对象
     */
    public ArrayList<OrgUser> getOrgUserByUser(long userID)
    {
        // 调用云办公方法，得到当前组织下所有的用户
        List<UsersOrganizations> userOrgs = structureDAO.findUsersOrganizationsByUserId(userID);
        if (userOrgs == null || userOrgs.size() < 1)
        {
            return null;
        }

        // 把云办公的对象转换为报表对象
        User user = new User();
        user.setId(userID);
        Organization organization;
        ArrayList<OrgUser> orgUserList = new ArrayList<OrgUser>();
        for (UsersOrganizations userOrg : userOrgs)
        {
            OrgUser orgUser = new OrgUser();
            orgUser.setId(userOrg.getId());

            // 组织对象
            organization = new Organization();
            organization.setId(userOrg.getOrganization().getId());
            organization.setName(userOrg.getOrganization().getName());
            orgUser.setOrganization(organization);

            // 用户对象
            user.setUserName(userOrg.getUser().getRealName());
            orgUser.setUser(user);

            orgUserList.add(orgUser);
        }

        return orgUserList;
    }

    /**
     * 得到设计者列表
     * 
     * @param userID 用户ID
     * @return Result 设计者列表
     */
    public ArrayList<User> getDesignerList(long userID)
    {
        Users currentUser = structureDAO.findUserById(userID);
        if (currentUser != null && currentUser.getCompany() != null)
        {
            ArrayList<User> userList = new ArrayList<User>();
            List<Users> users = structureDAO.getUsers(currentUser.getCompany().getId(), 0,
                Integer.MAX_VALUE, null, null);
            for (Users user : users)
            {
                if (isReportDesigner(user))
                {
                    userList.add(convertUsersToUser(user));
                }
            }

            if (!userList.isEmpty())
            {
                return userList;
            }
        }
        return null;
    }

    /**
     * 得到用户
     * 
     * @param userID 用户ID
     * @return User 用户
     */
    public User getUser(long userID)
    {
        return convertUsersToUser(structureDAO.findUserById(userID));
    }

    /**
     * 得到用户
     * 
     * @param loginName 登录名
     * @return User 用户
     */
    public User getUser(String loginName)
    {
        return convertUsersToUser(structureDAO.getUserByName(loginName));
    }

    /*
     * 转换云办公用户为报表用户
     */
    private User convertUsersToUser(Users user)
    {
        User u = new User();
        u.setId(user.getId());
        u.setUserName(user.getRealName());
        u.setLoginName(user.getUserName());
        return u;
    }

    /**
     * @param structureDAO 设置 structureDAO
     */
    public void setStructureDAO(StructureDAO structureDAO)
    {
        this.structureDAO = structureDAO;
    }
}