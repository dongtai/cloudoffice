package apps.transmanager.weboffice.service.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.NewPersonshareinfo;

/**
 *
 */
public class NewPersonshareinfoDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(NewPersonshareinfoDAO.class);

	public void saveAll(Collection entities)
	{
		try
		{
			super.saveAll(entities);
		}
		catch (Exception re)
		{
			log.error("save failed", re);
			re.printStackTrace();
			// throw re;
		}
	}

	public List<NewPersonshareinfo> findNew(Long userId, int isNew,
			Integer firstIndex, Integer pageCount)
	{
		try
		{
			StringBuffer queryString = new StringBuffer("from NewPersonshareinfo as model where 1=1");

			if (null != userId)
			{
				queryString.append(" and model.userinfoBySharerUserId.id = ").append(userId);
			}
			//	            
			// if(-1!=isNew)
			// {
			// queryString.append("  and model.isNew = ").append(isNew);
			// }

			queryString.append(" order by model.date desc");
			return findAllBySql(firstIndex != null ? firstIndex : -1, 
					pageCount != null ? pageCount: -1, queryString.toString());
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	public NewPersonshareinfo findByOtherShareAndPath(long shareID, String path)
	{
		log.debug("finding NewPersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			//String queryString = "from NewPersonshareinfo as model where model.userinfoBySharerUserId.id = ? and model.shareFile = ?";
			String queryString = "from NewPersonshareinfo as model where model.userinfoBySharerUserId.id = ? and LOCATE(model.shareFile, ?) > 0";
			List<NewPersonshareinfo> l = findAllBySql(queryString, shareID, path);
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
		// return null;

	}

	public NewPersonshareinfo findByShareOwnerAndPath(long shareID, String path)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from NewPersonshareinfo as model where model.userinfoByShareowner.id"
					+ "= ? and model.shareFile = ?";
			List<NewPersonshareinfo> l = findAllBySql(queryString,  shareID, path);
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

	// 最近共享用户
	public List findLatelyUser(Long userID)
	{
		// log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
		// + shareID + ", isNew: " + isNew);
		try
		{
			String queryString = "select distinct model.userinfoByShareowner from NewPersonshareinfo as model where model.userinfoBySharerUserId.id "
					+ "= ? limit 0,10";
			return findAllBySql(queryString,  userID);
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	public void delByLikeFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete NewPersonshareinfo where shareFile like ? and companyID"
					+ "=?";
			excute(queryString,  path + "%", companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List<NewPersonshareinfo> getShareinfoBySharer(long shareuserID)
	{
		try
		{
			String queryString = "from NewPersonshareinfo as model where model.userinfoBySharerUserId.id"
					+ "=?";
			return findAllBySql(queryString,  shareuserID);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List likeSerch(String property, String value)
	{
		try
		{
			String queryString = "from NewPersonshareinfo as model where model."
					+ property + " like ?";
			return findAllBySql(queryString,  value + "%");
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			throw re;
		}
	}

	/*
	 * 
	 */
	public void bulkUpdateDelByFileAndSharer(String[] values, Long uid)
	{
		log.debug("bulkUpdateDelByFileAndSharer ");
		try
		{
			if (values != null && values.length > 0)
			{
				// String queryString =
				// "delete NewPersonshareinfo as model where shareFile"+
				// " in(?) " + "and model.userinfoBySharerUserId.userId=" + uid;
				String queryString = "delete NewPersonshareinfo as model where shareFile"
						+ " in(";// ?) " + "and
									// model.userinfoBySharerUserId.userId=" +
									// uid;
				for (int i = 0; i < values.length; i++)
				{
					queryString += "\'" + (String) values[i] + "\'";
					if (i + 1 < values.length)
					{
						queryString += ",";
					}
				}
				queryString += ") and model.userinfoBySharerUserId.id="	+ uid;

				excute(queryString);
			}
			// bulkUpdate(queryString,values);
		}
		catch (RuntimeException re)
		{
			log.error("bulkUpdateDelByFileAndSharer failed", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.evermore.weboffice.server.dao.INewPersonshareinfoDAO#delByFileAndSharer
	 * (java.lang.String, java.lang.Long)
	 */

	public void delByFileAndSharer(String path, Long sharerID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete NewPersonshareinfo as model where shareFile"
					+ "=? and model.userinfoBySharerUserId.id =?";
			excute(queryString,  path, sharerID);
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
			String queryString = "delete NewPersonshareinfo where shareFile"
					+ "=?  and companyID =?";
			excute(queryString,  path, companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List<NewPersonshareinfo> findByOwnerAndPath(long shareID, String path)
	{
		log.debug("finding NewPersonshareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from NewPersonshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?  and model.shareFile = ?";
			return findAllBySql(queryString,  shareID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<NewPersonshareinfo> findByOwnerlikePath(long shareID,
			String path)
	{
		log.debug("findByOwnerlikePath NewPersonshareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from NewPersonshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?  and model.shareFile like ?";
			return findAllBySql(queryString,  shareID, path);
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
			String queryString = "from NewPersonshareinfo as model where model.shareFile"
					+ "= ?";
			List l = findAllBySql(queryString,  path);
			if (l != null && l.size() > 0)
			{
				return true;
			}
			else
			{
				return false;
			}

		}
		catch (RuntimeException re)
		{
			log.error("find by  NewPersonshareinfo property name failed", re);
			throw re;
		}

	}

	public List<NewPersonshareinfo> findByPath(String path)
	{
		log.debug("finding NewPersonshareinfo instance with property: path: "
				+ path);
		try
		{
			String queryString = "from NewPersonshareinfo as model where model.shareFile like '"
					+ path + "%'";
			List<NewPersonshareinfo> l = findAllBySql(queryString);
			if (l != null && l.size() > 0)
			{
				return l;
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property path", re);
			throw re;
		}

	}
}