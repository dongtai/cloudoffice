package apps.moreoffice.report.commons.domain.info;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.interfaces.IReportService;

/**
 * 组织架构
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-10
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class Organization extends TreeInfo
{
    // 序列化ID
    private static final long serialVersionUID = -2930351953980149118L;
    // 是否从服务器获取过组织用户对象
    private transient boolean hasSend;
    // 组织用户对象
    private transient ArrayList<OrgUser> orgUsers;

    /**
     * 得到该组织下的组织用户对象
     * 
     * @param service 报表服务
     * @return ArrayList<OrgUser> 组织用户列表
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<OrgUser> getOrgUser(IReportService service)
    {
        if (orgUsers == null && service != null && !hasSend)
        {
            hasSend = true;
            Result result = service.getOrgUserByOrg(getId());
            if (!result.hasError())
            {
                orgUsers = (ArrayList<OrgUser>)result.getData();
                return orgUsers;
            }
        }
        return orgUsers;
    }

    /**
     * 得到以separator分隔的全路径
     * 
     * @param separator 分隔符
     */
    public String getPath(String separator)
    {
        String path = name;
        TreeInfo parent = getParent();
        while (parent != null)
        {
            path = parent.getName().concat(separator).concat(path);
            parent = parent.getParent();
        }
        return name;
    }

    /**
     * 对话盒用
     */
    public String toString()
    {
        return name;
    }
}