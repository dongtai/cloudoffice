package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Groupmembershareinfo;

/**
 * 
 */
public class GroupmembershareinfoDAO extends BaseDAO
{
	private static final Log log = LogFactory
			.getLog(GroupmembershareinfoDAO.class);

	public void deleteByID(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete Groupmembershareinfo where groupmembershareId = "
					+ id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByOwnerAndFile(long userID, String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupmembershareinfo where shareowner"
					+ "= ?  and shareFile = ?";
			excute(queryString, userID, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByFile(String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupmembershareinfo where shareFile=?";
			excute(queryString, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByLikeFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupmembershareinfo where shareFile like ? and companyID =?";
			excute(queryString, path + "%", companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all Groupmembershareinfo instances");
		try
		{
			return findAll("Groupmembershareinfo");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<Groupmembershareinfo> findByMemberAndPath(String path,long memberID)
	{
		try
		{
	        String queryString = "from Groupmembershareinfo as model where model.shareFile = ?  and model.userinfo.id = ?";
	        return findAllBySql(queryString, path, memberID);
		}
		catch (RuntimeException re) 
		{
			log.error("find  failed", re);
			throw re;
		}
	}

	public List<Groupmembershareinfo> findByOwner(String path, long ownerID)
	{
		try
		{
			String queryString = "from Groupmembershareinfo as model where model.shareFile = ? and model.shareowner = ?";
			return findAllBySql(queryString, path, ownerID);
		}
		catch (RuntimeException re)
		{
			log.error("find  failed", re);
			throw re;
		}
	}

	public List<Groupmembershareinfo> findByOwner(long ownerID)
	{
		try
		{
			String queryString = "from Groupmembershareinfo as model where model.shareowner = ?";
			return findAllBySql(queryString, ownerID);
		}
		catch (RuntimeException re)
		{
			log.error("find  failed", re);
			throw re;
		}
	}

	public List<Groupmembershareinfo> findByOwnerAndGroupAndPath(long groupID,
			long ownerID, String path)
	{
		try
		{
			String queryString = "from Groupmembershareinfo as model where model.groupinfo.id"
					+ " = ? and model.shareowner = ? and model.shareFile =?";
			return findAllBySql(queryString, groupID, ownerID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find  failed", re);
			throw re;
		}
	}

	public void delByOwnerAndGroupAndPath(long groupID, long ownerID,
			String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupmembershareinfo where groupinfo.id"
					+ " = ? and shareowner = ? and shareFile =?";
			excute(queryString,  groupID, ownerID, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List<String> findByGroupAndNew(Long groupId, int i)
	{
		try
		{
			String queryString = "select model.shareFile from Groupmembershareinfo as model where model.groupinfo.id"
					+ "= ? and model.isNew = ?";
			return findAllBySql(queryString,  groupId, 0);
		}
		catch (RuntimeException re)
		{
			log.error("find  failed", re);
			throw re;
		}
	}

}