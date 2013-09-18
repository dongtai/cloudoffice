package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Groupshareinfo;

/**
 * 
 */
public class GroupshareinfoDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(GroupshareinfoDAO.class);
	public static String SHAREFILE = "shareFile";

	public void save(Groupshareinfo transientInstance)
	{
		try
		{
			if (transientInstance.getFileShareId() == null)
			{
				super.save(transientInstance);
			}
			else
			{
				update(transientInstance);
			}
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}

	public void deleteByID(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete Groupshareinfo where fileShareId = "
					+ id;
			excute(queryString);
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
			String queryString = "delete Groupshareinfo where shareFile =?";
			excute(queryString, path);
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
			String queryString = "delete Groupshareinfo where userinfo.id"
					+ "= ? and shareFile = ?";
			excute(queryString,  userID, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByGroupOwnerAndFile(long groupid, String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupshareinfo where groupinfoOwner.id"
					+ "= ?  and shareFile = ?";
			excute(queryString,  groupid, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Groupshareinfo where shareFile =?"
					+ " and companyID =?";
			excute(queryString,  path, companyID);
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
			String queryString = "delete Groupshareinfo where shareFile like ? and companyID =?";
			excute(queryString,  path + "%", companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public Groupshareinfo findById(java.lang.Long id)
	{
		try
		{
			Groupshareinfo instance = (Groupshareinfo) find("com.evermore.weboffice.databaseobject.Groupshareinfo", id);
			return instance;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * 通过被共享的组的ID找到共享者
	 */
	public List<Groupshareinfo> findByGroupId(java.lang.Long id)
	{
		try
		{
			String queryString = "from Groupshareinfo as model where model.groupinfo.id = ? ";
			return findAllBySql(queryString, id);
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * 通过共享者组的ID找到被共享的组
	 * 
	 * @param id
	 * @return
	 */
	public List<Groupshareinfo> findByGroupOwnerId(java.lang.Long id)
	{
		try
		{
			String queryString = "from Groupshareinfo as model where model.groupinfoOwner.id = ? ";
			return findAllBySql(queryString, id);
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value)
	{
		log.debug("finding Groupshareinfo instance with property: "
				+ propertyName + ", value: " + value);
		try
		{
			return findByProperty("Groupshareinfo",  propertyName,  value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all Groupshareinfo instances");
		try
		{
			return findAll("Groupshareinfo");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public Groupshareinfo findByShareAndPath(long shareID, String path)
	{

		log.debug("finding Groupshareinfo instance with property: groupinfo: "
				+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Groupshareinfo as model where groupinfo.id"
					+ " = ?  and model.shareFile = ?";
			List<Groupshareinfo> l = findAllBySql(queryString,  shareID, path);
			if (l.size() > 0)
			{
				return l.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public Groupshareinfo findByShareOwnerAndPath(long shareID, String path)
	{

		log.debug("finding Groupshareinfo instance with property: groupinfo: "
				+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Groupshareinfo as model where userinfo.id"
					+ "= ?  and model.shareFile = ?";
			List<Groupshareinfo> l = findAllBySql(queryString,  shareID, path);
			if (l.size() > 0)
			{
				return l.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public List likeSerch(String property, String value)
	{
		String queryString = "from Groupshareinfo as model where model."
				+ property + " like ?";
		return findAllBySql(queryString, value + "%");
	}


	public List<String> findPathByProperty(String propertyName, Object value)
	{
		try
		{
			String queryString = "select distinct new java.lang.String(model.shareFile) from Groupshareinfo as model where model."
					+ propertyName + "= ?";
			return findAllBySql(queryString, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public boolean findByShareFile(String path)
	{
		try
		{
			String queryString = "select count(*) from Groupshareinfo as model where model.shareFile"
					+ "= ?";
			List l = findAllBySql(queryString, path);
			if (l != null && l.size() > 0)
			{
				return ((Long)l.get(0)).intValue() > 0;
			}
			else
			{
				return false;
			}

		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public List<Groupshareinfo> findByOwnerAndPath(Long shareID, String path)
	{
		log.debug("finding Groupshareinfo instance with property: groupinfo: "
				+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Groupshareinfo as model where userinfo.id"
					+ "= ?  and model.shareFile = ?";
			return findAllBySql(queryString, shareID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Groupshareinfo> findByGroup(long sharerGroupID)
	{
		try
		{
			String queryString = "from Groupshareinfo as model where model.groupinfo.id = ?";
			return findAllBySql(queryString,  sharerGroupID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	/**
	 * 通过共享组ID及被共享组ID找到共享的文件
	 */
	public List<Groupshareinfo> findShareFileByGroupId(Long groupId,
			Long ownerGroupId, int start, int count)
	{
		try
		{
			String queryString = "from Groupshareinfo as model where model.groupinfo.id = ? and  model.groupinfoOwner.id = ? ";
			return findAllBySql(start, count, queryString,  groupId, ownerGroupId);

		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	/**
	 * 通过共享用户ID及被共享组ID找到共享的文件
	 */
	public List<Groupshareinfo> findShareFileByUserId(Long groupId,
			Long ownerUserId, int start, int count)
	{
		try
		{
			String queryString = "from Groupshareinfo as model where model.groupinfo.id = ? and  model.userinfo.id = ? ";
			return findAllBySql(start, count, queryString,  groupId, ownerUserId);

		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public long findShareFileByGroupIdCount(Long groupId, Long ownerGroupId)
	{
		try
		{
			String queryString = "select count(*) from Groupshareinfo as model where model.groupinfo.id = ? and  model.groupinfoOwner.id = ? ";
			List<Long> ret = findAllBySql(queryString,  groupId, ownerGroupId);
			if (ret != null && ret.size() > 0)
			{
				return ret.get(0);
			}
			else
			{
				return 0L;
			}
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			return 0L;
		}
	}

	public long findShareFileByUserIdCount(Long groupId, Long ownerUserId)
	{
		try
		{
			String queryString = "select count(*) from Groupshareinfo as model where model.groupinfo.id = ? and  model.userinfo.id = ? ";
			List<Long> ret = findAllBySql(queryString,  groupId, ownerUserId);
			if (ret != null && ret.size() > 0)
			{
				return ret.get(0);
			}
			else
			{
				return 0L;
			}
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			return 0L;
		}
	}
	
	public void delByLikeSignFile(String[] path)
	{
		log.debug("delete ");
		try
		{
			for(int i=0;i<path.length;i++)
			{
				String queryString = "delete SignInfo where filePath = ? ";
				excute(queryString,  path[i]);
			}
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

}