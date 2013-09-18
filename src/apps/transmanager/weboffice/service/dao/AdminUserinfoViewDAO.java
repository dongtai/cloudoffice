package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 */
public class AdminUserinfoViewDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(AdminUserinfoViewDAO.class);
	private static final String VIEW = "select distinct new com.evermore.weboffice.domain.AdminUserinfoView("
			+ "userinfo.id, userinfo.userName, userinfo.realName, userinfo.realEmail, "
			+ "userinfo.storageSize, userinfo.duty, userinfo.role, userinfo.validate, "
			+ "userinfo.sortNum, userinfo.caId) "
			+ "from Users userinfo";	
	private static final String COUNT = " select count(*) from Users userinfo";

	public List findByExample(AdminUserinfoView instance)
	{
		log.debug("finding UserinfoView instance by example");
		try
		{
			return findAllBySql(VIEW);
		}
		catch (RuntimeException re)
		{
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByRole(String role, Object value)
	{
		log.debug("finding AdminUserinfoView instance with role: "	+ role + ", value: " + value);
		try
		{
			String queryString = VIEW + " where userinfo.role = ? ";
			return findAllBySql(queryString, value);
			
			/*String queryString = "from AdminUserinfoView as model where model."
					+ propertyName + "= ?";			
			Query queryObject = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();*/
		}                                    
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all AdminUserinfoView instances");
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

	// 为分页而使用。user663
	public DataHolder getlimitedUser(int index, int length, String sort,
			String dir)
	{
		log.debug("finding all AdminUserinfoView instances");
		try
		{
			int size = 0;
			String queryString;
			if (sort != null && dir != null)
			{
				String q = " where userinfo.role != 1 order by  "
					+ sort + " " + dir; 
				queryString = COUNT + q;				
				List lo = findAllBySql(queryString);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
				queryString = VIEW + q;
			}
			else
			{
				String q = " where userinfo.role != 1  ";
				queryString = COUNT + q;
				
				List lo = findAllBySql(queryString);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
				queryString = VIEW + q;
			}
			List l = findAllBySql((index - 1) * length, length, queryString, null);			
			// if(dh == null)
			DataHolder dh = new DataHolder();
			dh.setIntData(size);
			dh.setAdminData(l);
			return dh;
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<UsersOrganizations> findUsersOrganizationsByUserId(java.lang.Long userId)
	{
		try
		{
			String queryString = "from UsersOrganizations as model where model.user.id = ?";
			return findAllBySql(queryString,  userId);

		}
		catch (RuntimeException re)
		{
			LogsUtility.error("get failed", re);
			throw re;
		}
	}
	// 为搜索而使用。user663
	public DataHolder getSearchUser(int option, String keyWord,Users users, int index,
			int length, String sort, String dir)
	{
		log.debug("attaching dirty AdminUserinfoView instance");
		try
		{
			String queryString;
			int size = 0;
			String q;
			List l;
			String par = "%" + keyWord + "%";
			String cond="";
			if (users!=null && !"admin".equals(users.getUserName()))
			{
	    		List<UsersOrganizations> list=findUsersOrganizationsByUserId(users.getId());
	    		if (list!=null && list.size()>0)
	    		{
	    			String orgcode=list.get(0).getOrganization().getOrganizecode();
	    			if (orgcode!=null)
	    			{
	    				int indexnum = orgcode.indexOf("-");
	    				if (indexnum>0)
	    				{
	    					orgcode=orgcode.substring(0,indexnum);
	    					cond+=" and userinfo.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
	    				}
	    				else
	    				{
	    					cond+=" and userinfo.id in (select w.user.id from UsersOrganizations as w where w.organization.organizecode like '"+orgcode+"%')";
	    				}
	    			}
	    		}
			}
			if (option == 0)
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.userName like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.userName like ?)";
				}
				queryString = COUNT + q+cond;				
				List lo = findAllBySql(queryString, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q+cond;
				l = findAllBySql((index - 1) * length, length, queryString, par);				
			}
			else if (option == 1)
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.realEmail like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.realEmail like ?)";
				}
				queryString = COUNT + q+cond;				
				List lo = findAllBySql(queryString, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q+cond;
				l = findAllBySql((index - 1) * length, length, queryString, par);	
			}
			else
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.userName like ? or userinfo.realEmail like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.userName like ? or userinfo.realEmail like ?)";
				}
				queryString = COUNT + q+cond;				
				List lo = findAllBySql(queryString, par, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q+cond;
				l = findAllBySql((index - 1) * length, length, queryString, par, par);
			}

			DataHolder dh = new DataHolder();
			dh.setIntData(size);
			dh.setAdminData(l);
			return dh;
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;

		}
	}

	/**
	 * 文件发送功能中，可以按姓名查找，上一个方法是以用户名查找
	 * 
	 * @param option
	 * @param keyWord
	 * @param index
	 * @param length
	 * @param sort
	 * @param dir
	 * @return
	 */
	public DataHolder getSearchUser2(int option, String keyWord, int index,
			int length, String sort, String dir)
	{
		log.debug("attaching dirty AdminUserinfoView instance");
		try
		{
			String queryString;
			int size = 0;
			String q;
			List l;
			String par = "%" + keyWord + "%";
			if (option == 0)
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.realName like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.realName like ?)";
				}
				queryString = COUNT + q;				
				List lo = findAllBySql(queryString, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q;
				l = findAllBySql((index - 1) * length, length, queryString, par);				
			}
			else if (option == 1)
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.realEmail like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.realEmail like ?)";
				}
				queryString = COUNT + q;				
				List lo = findAllBySql(queryString, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q;
				l = findAllBySql((index - 1) * length, length, queryString, par);	
			}
			else
			{
				if (sort != null && dir != null)
				{
					q = " where (userinfo.role!=1) and (userinfo.realName like ? or userinfo.realEmail like ?)"
						+ " order by " + sort + " " + dir;
				}
				else
				{
					q = " where (userinfo.role!=1) and (userinfo.realName like ? or userinfo.realEmail like ?)";
				}
				queryString = COUNT + q;				
				List lo = findAllBySql(queryString, par, par);
				if (lo != null && lo.size() > 0)
				{
					size = ((Long)lo.get(0)).intValue();
				}
								
				queryString = VIEW + q;
				l = findAllBySql((index - 1) * length, length, queryString, par, par);
			}			

			DataHolder dh = new DataHolder();
			dh.setIntData(size);
			dh.setAdminData(l);
			return dh;
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;

		}
	}

	// DataHolder dh;
}