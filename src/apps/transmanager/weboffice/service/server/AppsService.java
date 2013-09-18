package apps.transmanager.weboffice.service.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CompanyApps;
import apps.transmanager.weboffice.databaseobject.SystemApps;
import apps.transmanager.weboffice.databaseobject.UserApps;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.dao.AppsDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 处理应用增减
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Component(value=AppsService.NAME)
public class AppsService
{
	public static final String NAME = "appsService";
	
	@Autowired
	private AppsDAO appsDao;
	@Autowired
	private StructureDAO structureDao;
	
	
	/**
	 * 增加系统功能模块，默认给现有的所有公司使用
	 * @param sa
	 */
	public void addSystemApps(SystemApps sa)
	{
		appsDao.save(sa);
		if (sa.getFlag())
		{
			List<Company> com = structureDao.findAll(Company.class);
			CompanyApps capps;
			for (Company temp : com)
			{
				capps = new CompanyApps(temp, sa, sa.getDisplayName(), sa.getSortCode(), true);
				addCompanyApps(capps);
			}
		}
	}
	
	/**
	 * 获取系统中的应用
	 * @param name
	 * @return
	 */
	public SystemApps getSystemApps(String name)
	{
		return appsDao.getSystemApps(name);
	}
	
	/**
	 * 获取系统中的应用
	 * @param name
	 * @return
	 */
	public SystemApps getSystemApps(Long Id)
	{
		return appsDao.getSystemApps(Id);
	}
	
	/**
	 * 更新系统应用
	 * @param sa
	 */
	public void updateSystemApps(SystemApps sa)
	{
		appsDao.update(sa);
	}
	
	/**
	 * 获取系统中的所有应用
	 * @return
	 */
	public List<SystemApps> getSystemApps()
	{
		return appsDao.getSystemApps();
	}
	
	/**
	 * 获取系统中的应用
	 * @param name
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能 
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有 
	 * @return
	 */
	public List<SystemApps> getSystemApps(int flag, int type)
	{
		return appsDao.getSystemApps(flag, type);
	}
	
	/**
	 * 删除系统应用
	 * @param systemId
	 */
	public void deleteSystemApps(Long systemId)
	{
		appsDao.deleteSystemApps(systemId);
	}
	
	
	/**
	 * 获取公司的应用
	 * @param companyId
	 * @return
	 */
	public List<CompanyApps> getCompanyApps(Long companyId)
	{
		return appsDao.getCompanyApps(companyId);
	}
	
	/**
	 * 获取系统中的应用
	 * @param name
	 * @flag 0所有应用都获取，包括过期的不能使用的应用，1为只获取可用的功能，2为只获取过期不能使用的功能 
	 * @type 0在左边，1在上边，2为在右边，3为在下边, -1为所有 
	 * @return
	 */
	public List<CompanyApps> getCompanyApps(Long companyId, int flag, int type)
	{
		return appsDao.getCompanyApps(companyId, flag, type);
	}
	
	/**
	 * 删除公司使用的应用
	 * @param caId
	 * @return
	 */
	public void deleteCompanyApps(Long caId)
	{
		appsDao.deleteCompanyApps(caId);
	}
	
	/**
	 * 为公司新增应用。
	 * @param capps
	 */
	public void addCompanyApps(CompanyApps capps)
	{
		appsDao.save(capps);
		if (capps.getFlag())
		{
			List<Users> users = structureDao.getUsers(capps.getCompany().getId(), -1, -1, null, null);
			UserApps uapp;
			for (Users u : users)
			{
				uapp = new UserApps(capps, u);
				appsDao.save(uapp);
			}
		}
	}
	
	/**
	 * 为公司新增应用。
	 * @param capps
	 */
	public void addCompanyApps(List<CompanyApps> capps)
	{
		long time=System.currentTimeMillis();
		System.out.println("capps size===="+capps.size());
		appsDao.saveAll(capps);		
		List<Users> users = null;
		UserApps uapp;
		for (CompanyApps tc : capps)
		{
			if (tc.getFlag())
			{
				String sql="insert into ";
				if (users == null)
				{
					users = structureDao.getUsers(tc.getCompany().getId(), -1, -1, null, null);
				}
//				System.out.println(System.currentTimeMillis()-time);
				for (Users u : users)
				{
					uapp = new UserApps(tc, u);
					appsDao.save(uapp);
				}
			}
		}
		System.out.println(System.currentTimeMillis()-time);
	}
	
	/**
	 * 更新公司应用
	 * @param ca
	 */
	public void updateCompanyApps(Long caId, CompanyApps ca)
	{
		CompanyApps update = (CompanyApps)appsDao.find(CompanyApps.class, caId);
		update.update(ca);
		appsDao.update(update);
	}
	
	/**
	 * 获取公司的应用
	 * @param companyId
	 * @return
	 */
	public CompanyApps getCompanyApps(Long appId, Long companyId)
	{
		return appsDao.getCompanyApps(appId, companyId);
	}
	
	/**
	 * 更新公司使用的应用的有效期
	 * @param caId
	 * @param time
	 */
	public void updateCompanyAppsTime(Long caId, Date time)
	{
		appsDao.updateCompanyAppsTime(caId, time);
	}
	
	/**
	 * 为公司新增应用。
	 * @param capps
	 */
	public void updateCompanyApps(List<CompanyApps> capps)
	{
		appsDao.updateAll(capps);		
		List<Users> users = null;
		UserApps uapp;
		for (CompanyApps tc : capps)
		{
			if (tc.getFlag())
			{
				if (users == null)
				{
					users = structureDao.getUsers(tc.getApps().getId(), -1, -1, null, null);
				}
				for (Users u : users)
				{
					if (!appsDao.isExistUserApps(tc.getId(), u.getId()))
					{
						uapp = new UserApps(tc, u);
						appsDao.save(uapp);
					}
				}
			}
		}
	}
	
	/**
	 * 增加或修改公司的应用
	 * @param addList
	 * @param modifyList
	 * @param deleteList
	 */
	public void addOrUpdateCompanyApps(List<CompanyApps> addList, List<CompanyApps> modifyList, List<Long> deleteList, Long companyId)
	{
		if(addList != null && addList.size() > 0)
		{
			addCompanyApps(addList);
		}
		if(modifyList != null && modifyList.size() > 0)
		{
			updateCompanyApps(modifyList);
		}
		if(deleteList != null && deleteList.size() > 0)
		{
			appsDao.deleteCompanyApps(deleteList, companyId);
		}
	}
	
	/**
	 * 更新公司使用的应用的有效性
	 * @param caId
	 * @param validate，true有效，false无效
	 */
	public void updateCompanyAppsValidate(Long caId, boolean validate)
	{
		appsDao.updateCompanyAppsValidate(caId, validate);
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
		return appsDao.getUserApps(userId, flag, type);
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
		return appsDao.getUserApps(name, flag, type);
	}
	
	/**
	 * 为用户增加应用。
	 * @param ua
	 */
	public void addUserApps(UserApps ua)
	{
		appsDao.save(ua);
	}
	
	/**
	 * 判断用户是否可以使用某个应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public CompanyApps getCompanyAppsByUser(Long appsId, Long userId)
	{
		return appsDao.getCompanyAppsByUser(appsId, userId);
	}
	
	/**
	 * 判断用户是否可以使用某个应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public UserApps getUserAppsByCompany(Long appsId, Long userId)
	{
		return appsDao.getUserAppsByCompany(appsId, userId);
	}
	
	/**
	 * 判断用户是否可以通过系统使用某个应用，用户自己购买的应用
	 * @param appsId 应用id
	 * @param userId
	 * @return
	 */
	public UserApps getUserAppsBySystem(Long appsId, Long userId)
	{
		return appsDao.getUserAppsBySystem(appsId, userId);
	}
	
	/**
	 * 为用户增加应用。
	 * @param ua
	 */
	public void addUserApps(List<UserApps> ua)
	{
		appsDao.saveAll(ua);
	}
	
	/**
	 * 更新用户使用的应用的有效期
	 * @param caId
	 * @param time
	 */
	public void updateUserAppsTime(Long uId, Date time)
	{
		appsDao.updateUserAppsTime(uId, time);
	}
	
	/**
	 * 删除公司使用的应用
	 * @param caId
	 * @return
	 */
	public void deleteUserApps(Long uId)
	{
		appsDao.deleteUserApps(uId);
	}
	
	/**
	 * 增加或修改公司给用户的应用
	 * @param addList
	 * @param modifyList
	 * @param deleteList
	 */
	public void addOrUpdateUserAppsByCompany(List<UserApps> addList, List<UserApps> modifyList, List<Long> deleteList, Long userId)
	{
		if(addList != null && addList.size() > 0)
		{
			appsDao.saveAll(addList);
		}
		if(modifyList != null && modifyList.size() > 0)
		{
			appsDao.update(modifyList);
		}
		if(deleteList != null && deleteList.size() > 0)
		{
			appsDao.deleteUserAppsByCompany(deleteList, userId);
		}
	}
	
	/**
	 * 增加或修改用户的应用（用户自己购买）
	 * @param addList
	 * @param modifyList
	 * @param deleteList
	 */
	public void addOrUpdateUserAppsByUser(List<UserApps> addList, List<UserApps> modifyList, List<Long> deleteList, long userId)
	{
		if(addList != null && addList.size() > 0)
		{
			appsDao.saveAll(addList);
		}
		if(modifyList != null && modifyList.size() > 0)
		{
			appsDao.update(modifyList);
		}
		if(deleteList != null && deleteList.size() > 0)
		{
			appsDao.deleteUserAppsByUser(deleteList, userId);
		}
	}
	
	/**
	 * 新建公司的时候，拷贝系统中为公司用户默认都有的功能为该公司。
	 * @param companyId
	 */
	public void copySystemAppToCompany(Company company)
	{
		List<SystemApps> list = appsDao.getSystemApps(true);
		if (list != null && company != null)
		{		
			CompanyApps capps;
			for(SystemApps temp : list)
			{
				capps = new CompanyApps(company, temp);
				addCompanyApps(capps);
			}
		}
	}
	
	/**
	 * 新建用户时候，拷贝公司的默认给用户使用的功能。
	 * @param companyId
	 * @param user
	 */
	public void copyCompanyAppToUser(Long companyId, Users user)
	{
		List<CompanyApps> list = appsDao.getCompanyApps(companyId, true);		
		if (list != null && user != null)
		{		
			UserApps uapp;
			for(CompanyApps temp : list)
			{
				uapp = new UserApps(temp, user);
				appsDao.save(uapp);
			}
		}
	}
	
	/**
	 * 为公司增加应用
	 * @param apps
	 * @param company
	 */
	public void addOrUpdateCompanyApps(List<HashMap<String, String>> apps, Company company)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Long appId;
    	Long companyId = company.getId();
    	CompanyApps capps;
    	SystemApps sapps;
    	Integer type;     // 0 增加，1修改，2删除
    	List<Long> deleteList = new ArrayList<Long>();    	
    	List<CompanyApps> addList = new ArrayList<CompanyApps>();
    	List<CompanyApps> modifyList = new ArrayList<CompanyApps>();
    	Date time;
    	String temp;
    	for (HashMap<String, String> tempApp : apps)
    	{
    		temp = tempApp.get("id");    		
    		appId = Long.valueOf(temp);
    		temp = tempApp.get("type");
    		type = Integer.valueOf(temp);
    		if (type == 2)
    		{
    			deleteList.add(appId);
    			continue;
    		}
    		capps = getCompanyApps(appId, companyId);
    		if (capps == null)
    		{
    			sapps = getSystemApps(appId);
    			capps = new CompanyApps(company, sapps);
    			addList.add(capps);
    		}
    		else
    		{
    			modifyList.add(capps);
    		}
    		
    		temp = tempApp.get("displayName");
    		if (temp != null)
    		{
    			capps.setDisplayName(temp);
    		}
    		temp = tempApp.get("sortCode");  
    		if (temp != null)
    		{
    			capps.setSortCode(Integer.valueOf(temp));
    		}
    		temp = tempApp.get("validate");
    		if (temp != null)
    		{
    			capps.setValidate(Integer.valueOf(temp));
    		}
    		temp = tempApp.get("endTime");
    		if (temp != null)
    		{
    			try
    			{
    				time = sdf.parse(temp);
    				if (time.getTime() < capps.getApps().getEndTime().getTime())     // 不可设置大于本身系统允许的时间
    				{
    					capps.setEndTime(time);
    				}
    			}
    			catch(Exception e)
    			{
    				LogsUtility.error(e);    				
    			}
    		}
    		temp = tempApp.get("flag");
    		if (temp != null)
    		{
    			capps.setFlag(Boolean.valueOf(temp));
    		} 
    	}    	
    	addOrUpdateCompanyApps(addList, modifyList, deleteList, companyId); 
	}
	
}
