package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class GroupmemberinfoViewDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(GroupmemberinfoViewDAO.class);
	private static final String VIEW = "select new com.evermore.weboffice.domain.GroupmemberinfoView("
			+ "groupmemberinfo.id, groupmemberinfo.organization.id, "
			+ " groupmemberinfo.user.id, userinfo.userName, "
			+ " userinfo.realEmail, userinfo.realName) "
			+ " from	Users userinfo, UsersOrganizations groupmemberinfo "
			+ " where groupmemberinfo.user.id = userinfo.id";

	public List findByGroupId(Object value)
	{
		log.debug("finding GroupmemberinfoView instance with property: groupId, value: " + value);
		try
		{
			String queryString = VIEW + " and groupmemberinfo.organization.id = ?";
			return findAllBySql(queryString, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all GroupmemberinfoView instances");
		try
		{
			return findAllBySql(VIEW);
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List<String> getDistictGMCA(Long[] groupIds)
	{
        log.debug("get distict members in group");
        try
        {
            StringBuffer sb = new StringBuffer("(");
            for (int i = 0; i < groupIds.length; i++)
            {
                if (i == (groupIds.length - 1))
                {
                    sb.append(groupIds[i]).append(")");
                    break;
                }
                sb.append(groupIds[i]).append(",");
            }
            String ids = sb.toString();
            String queryString = "select distinct model.user.caId from GroupsManagers as model where model.group.id in"
                + ids;
            return findAllBySql(queryString);

        }
        catch(RuntimeException re)
        {
            log.error("attach failed", re);
            throw re;
        }

    }

}