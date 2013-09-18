package apps.transmanager.weboffice.service.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.CompanyApps;
import apps.transmanager.weboffice.databaseobject.SystemApps;
import apps.transmanager.weboffice.databaseobject.UserApps;


/**
 * 处理系统中的应用增加
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class AppsDAO  extends BaseDAO 
{
	
	/**
	 * 获取系统的应用
	 * @param name
	 * @return
	 */
	public SystemApps getSystemApps(String name)
	{
		String sql = "select m from SystemApps as m where m.name = ?  ";
		return (SystemApps)findOneObjectBySql(sql, name);
	}
	
	/**
	 * 获取系统的应用
	 * @param name
	 * @return
	 */
	public SystemApps getSystemApps(Long Id)
	{
		String sql = "select m from SystemApps as m where m.id = ?  ";
		return (SystemApps)findOneObjectBySql(sql, Id);
	}
	
	/**
	 * 获取是否是公司的用户默认都有的功能
	 * @param flag
	 * @return
	 */
	public List<SystemApps> getSystemApps(Boolean flag)
	{
		String sql = "from SystemApps as model where model.flag = ? ";
		return findAllBySql(sql, flag);
	}
	
	/**
	 * 删除系统应用
	 * @param systemId
	 */
	public void deleteSystemApps(Long systemId)
	{
		deleteEntityByID(SystemApps.class, "id", systemId);
	}
	
	/**
	 * 获取系统中的所有应用
	 * @return
	 */
	public List<SystemApps> getSystemApps()
	{
		String sql = "select m from SystemApps as m order by m.sortCode";
		return findAllBySql(sql);
	}
	
	/**
	 * 获取系统的应用
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有
	 * @return
	 */
	public List<SystemApps> getSystemApps(int flag, int type)
	{
		HashMap<String, Object> parms = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select a from SystemApps as a where 1 = 1 ");
		if (flag == 1)
		{
			sb.append(" and a.endTime >= :date");
			parms.put("date", new Date());
		}
		else if (flag == 2)
		{
			sb.append(" and a.endTime < :date");
			parms.put("date", new Date());
		}		
		if (type >= 0)
		{
			sb.append(" and a.type = :type");
			parms.put("type", type);
		}
		sb.append(" order by a.sortCode");		
		return findByNamedParams(sb.toString(), parms);
	}
	
	/**
	 * 获取是否是公司的用户默认都有的功能的应用
	 * @param companyId
	 * @return
	 */
	public List<CompanyApps> getCompanyApps(Long companyId, Boolean flag)
	{
		String sql = "select m from CompanyApps as m where m.company.id = ? and m.flag = ? ";
		return findAllBySql(sql, companyId, flag);
	}
	
	/**
	 * 获取公司的应用
	 * @param companyId
	 * @return
	 */
	public List<CompanyApps> getCompanyApps(Long companyId)
	{
		String sql = "select m from CompanyApps as m where m.company.id = ?  order by m.sortCode";
		return findAllBySql(sql, companyId);
	}
	
	/**
	 * 获取公司的应用
	 * @param companyId
	 * @return
	 */
	public CompanyApps getCompanyApps(Long appId, Long companyId)
	{
		String sql = "select m from CompanyApps as m where m.apps.id = ? and m.company.id = ? ";
		return (CompanyApps)findOneObjectBySql(sql, appId, companyId);
	}
	
	/**
	 * 删除公司的应用
	 * @param systemAppIds，为系统的应用id
	 */
	public void deleteCompanyApps(List<Long> systemAppIds, Long companyId)
	{
		String sql = "delete from CompanyApps as m where m.apps.id in (?1) and m.company.id = ?2 ";
		excute(sql, systemAppIds, companyId);		
	}
	
	/**
	 * 删除公司给用户的应用
	 * @param systemAppIds，为系统的应用id
	 */
	public void deleteUserAppsByCompany(List<Long> systemAppIds, Long userId)
	{
		String sql = "delete from UserApps as m where m.companyApps.apps.id in (?1) and m.user.id = ?2";
		excute(sql, systemAppIds, userId);		
	}
	
	/**
	 * 删除用户的应用（用户自己购买的应用）
	 * @param systemAppIds，为系统的应用id
	 */
	public void deleteUserAppsByUser(List<Long> systemAppIds, Long userId)
	{
		String sql = "delete from UserApps as m where m.sysApps.id in (?1) and m.user.id = ?2";
		excute(sql, systemAppIds, userId);		
	}
	
	/**
	 * 判断用户是否可以使用某个应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public boolean isExistUserApps(Long appsId, Long userId)
	{
		String sql = "select count(*) from UserApps as m where m.companyApps.apps.id = ? and m.user.id = ? ";
		return getCountBySql(sql, appsId, userId) > 0;
	}
	
	/**
	 * 判断用户是否可以使用某个应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public CompanyApps getCompanyAppsByUser(Long appsId, Long userId)
	{
		String sql = "select m.companyApps from UserApps as m where m.companyApps.apps.id = ? and m.user.id = ? ";
		return (CompanyApps)findOneObjectBySql(sql, appsId, userId);
	}
	
	/**
	 * 判断用户是否可以通过公司使用某个应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public UserApps getUserAppsByCompany(Long appsId, Long userId)
	{
		String sql = "select m from UserApps as m where m.companyApps.apps.id = ? and m.user.id = ? ";
		return (UserApps)findOneObjectBySql(sql, appsId, userId);
	}
	
	/**
	 * 判断用户是否可以通过系统使用某个应用，用户自己购买的应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public UserApps getUserAppsBySystem(Long appsId, Long userId)
	{
		String sql = "select m from UserApps as m where m.sysApps.id = ? and m.user.id = ? ";
		return (UserApps)findOneObjectBySql(sql, appsId, userId);
	}
		
	/**
	 * 获取公司的应用
	 * @param companyId
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有
	 * @return
	 */
	public List<CompanyApps> getCompanyApps(Long companyId, int flag, int type)
	{
		HashMap<String, Object> parms = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select m from CompanyApps as m where m.company.id = :companyId ");
		parms.put("companyId", companyId);
		if (flag == 1)
		{
			sb.append(" and m.endTime >= :date");
			parms.put("date", new Date());
		}
		else if (flag == 2)
		{
			sb.append(" and m.endTime < :date");
			parms.put("date", new Date());
		}
		if (type >= 0)
		{
			sb.append(" and m.apps.type = :type");
			parms.put("type", type);
		}
		sb.append(" order by m.sortCode");
		return findByNamedParams(sb.toString(), parms);
	}
	
	/**
	 * 更新公司使用的应用的有效期
	 * @param caId
	 * @param time
	 */
	public void updateCompanyAppsTime(Long caId, Date time)
	{
		String sql = "update CompanyApps set endTime = ? where id = ? ";
		excute(sql, time, caId);
	}
	
	/**
	 * 更新公司使用的应用的有效性
	 * @param caId
	 * @param validate，true有效，false无效
	 */
	public void updateCompanyAppsValidate(Long caId, boolean validate)
	{
		String sql = "update CompanyApps set validate = ? where id = ? ";
		excute(sql, validate ? 0 : 1, caId);
	}
	
	/**
	 * 删除公司使用的应用
	 * @param caId
	 * @return
	 */
	public void deleteCompanyApps(Long caId)
	{
		deleteEntityByID(CompanyApps.class, "id", caId);
	}
	
	/**
	 * 获取用户可以使用的应用
	 * @param userId
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有
	 * @return
	 */
	public List<UserApps> getUserApps(Long userId, int flag, int type)
	{
		HashMap<String, Object> parms = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select m from UserApps as m where m.user.id = :userId ");
		parms.put("userId", userId);
		if (flag == 1)
		{
			sb.append(" and m.endTime >= :date");
			parms.put("date", new Date());
		}
		else if (flag == 2)
		{
			sb.append(" and m.endTime < :date");
			parms.put("date", new Date());
		}
		if (type >= 0)
		{
			sb.append(" and (m.companyApps.apps.type = :type or m.sysApps.type = : type)");
			parms.put("type", type);
		}
		sb.append(" order by m.sortCode");
		return findByNamedParams(sb.toString(), parms);
	}
	
	/**
	 * 获取用户可以使用的应用
	 * @param name
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有
	 * @return
	 */
	public List<UserApps> getUserApps(String name, int flag, int type)
	{
		HashMap<String, Object> parms = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select a from UserApps as a, Users as u where a.user.id = u.id and u.userName = :name ");
		parms.put("name", name);
		if (flag == 1)
		{
			sb.append(" and a.endTime >= :date");
			parms.put("date", new Date());
		}
		else if (flag == 2)
		{
			sb.append(" and a.endTime < :date");
			parms.put("date", new Date());
		}		
		if (type >= 0)
		{
			sb.append(" and (a.companyApps.apps.type = :type or a.sysApps.type = : type)");
			parms.put("type", type);
		}
		sb.append(" order by a.sortCode");		
		return findByNamedParams(sb.toString(), parms);
	}
		
	/**
	 * 更新用户使用的应用的有效期
	 * @param caId
	 * @param time
	 */
	public void updateUserAppsTime(Long uId, Date time)
	{
		String sql = "update UserApps set endTime = ? where id = ? ";
		excute(sql, time, uId);
	}
	
	/**
	 * 删除公司使用的应用
	 * @param caId
	 * @return
	 */
	public void deleteUserApps(Long uId)
	{
		deleteEntityByID(UserApps.class, "id", uId);
	}
	
}
