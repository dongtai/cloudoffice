package apps.transmanager.weboffice.service.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import templates.objectdb.NewsType;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.JobTitles;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.SystemApps;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.UsersRoles;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveSecurity;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.service.IFileTemplateService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.PermissionDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.handler.AppsHandler;
import apps.transmanager.weboffice.service.impl.FileTemplateService;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.license.LicenseService;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.RSACoder;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Component(value=InitDataService.NAME)
public class InitDataService
{	
	public final static String NAME = "initDataService";
	@Autowired
    private StructureDAO baseDAO;
	@Autowired
	private PermissionDAO permissionDAO;
	
	private static Properties properties;
	
	public static String importfilepath;
	
	private boolean isInited()
	{
		List list = baseDAO.findAll("Users");
		if (list == null || list.size() <= 0)
		{
			return false;
		}
		return true;
	}
	
	private void init()
	{
		if (properties != null)
		{
			return;
		}
		properties = new Properties();
		try
		{
			//System.out.println("==="+LDAPUtil.class.getClassLoader().getResource("/config/loginConfig.properties"));
			properties.load(InitDataService.class.getClassLoader().getResourceAsStream("/conf/initData.properties"));			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			LogsUtility.error(e);
		}
	}
	
	// 处理原系统升级的兼容问题。后续删除
	@Deprecated
	private void initCompat()
	{
		String[] values;
		int size;
		String value;
		try
		{
			//初始化密级,这是后来添加的，为了兼容这样做的
			value = properties.getProperty("archivesecurity");
			List list=baseDAO.findAll(ArchiveSecurity.class);
			if (value != null && list.size()==0)
			{
				//value = converString(value);
				values = value.split(";");
				size = values.length;
				for (int i = 0; i < size; i++)
				{
					ArchiveSecurity jt = new ArchiveSecurity(values[i].trim());	
					baseDAO.save(jt);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			// 兼容已经存在是数据库
			List list = baseDAO.findAll(SystemApps.class);
			if (list == null || list.size() <= 0)    // 初始化应用配置
			{
				String fileName = WebConfig.webContextPath + "/WEB-INF/classes/META-INF/applications";
				AppsHandler.importApps(fileName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void initLicense()
	{
		LicenseService licenseService = (LicenseService)ApplicationContext.getInstance().getBean(LicenseService.NAME);
		String licenseString = WebConfig.webContextPath + File.separatorChar + "WEB-INF" + File.separatorChar + "license";
        File file = new File(licenseString);
        boolean flag = file.exists(); 
        if (flag)
        {
        	int result = licenseService.initLicense(file);
        	if (result != Constant.LEGAL_LICENSE)
        	{
        		flag = false;
        	}
        }
        if (!flag)
        {        
        	licenseService.checkInit();
        }
	}
	
	public void initDatabase()
	{
		//初始化密级,这是后来添加的，为了兼容这样做的
		String[] values;
		int size;
		String value;
		init();
		
		WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
		try {
			initTemplate();
		} catch (Exception e1) {
			e1.printStackTrace();
		}//导入文档模板
		if (webConfig.replacetag)//如果为true,就替换相应文件的内容
		{
			replaceLog();
		}
		if (isInited())
		{
			initCompat();
			try {
				if(webConfig.isImportFiles()){
					importFiles();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}
		//importFiles();
		initLicense();
		
		try
		{
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			Spaces space;
			//初始化密级
			value = properties.getProperty("archivesecurity");
			if (value != null)
			{
				//value = converString(value);
				values = value.split(";");
				size = values.length;
				for (int i = 0; i < size; i++)
				{
					ArchiveSecurity jt = new ArchiveSecurity(values[i].trim());	
					baseDAO.save(jt);
				}
			}			
			// 初始化自定义角色 
			value = properties.getProperty("roleinfo");				
			Hashtable<String, Roles> roles = new Hashtable<String, Roles>();
			if (value != null)
			{
				//value = converString(value);
				values = value.split(";");				
				size = values.length;
				for (int i = 0; i < size; i++)
				{
					String[] para = values[i].split(",");					
					Roles ui = new Roles(para[1].trim(), para[2], Integer.valueOf(para[3]));	
					baseDAO.save(ui);
					roles.put(para[0].trim(), ui);
					String temp = para[4] != null ? para[4].trim() : "";
					String[] tempR = temp.split("\\|");
					int tempS = tempR.length;
					for (int k = 0; k < tempS; k++)
					{
						String r = tempR[k].substring(1);
						long rv = r != null && r.length() > 0 ? Long.valueOf(r) : 0;
						if (tempR[k].startsWith("m"))
						{ 
							permissionDAO.addRoleSystemMangeActions(ui, ui.getRoleName(), rv);						
						}
						else if (tempR[k].startsWith("s"))
						{
							permissionDAO.addRoleSpaceActions(ui, ui.getRoleName(), rv);	
						}
						else if (tempR[k].startsWith("f"))
						{
							permissionDAO.addRoleFileSystemActions(ui, ui.getRoleName(), rv);
						}						
					}
				}
			}
			//?
			String attachtype = properties.getProperty("attachtype");
			if (attachtype != null && attachtype.length()>0)
			{
				//attachtype=converString(attachtype);
				String[] types = attachtype.split(";");
				if (types!=null && types.length>0)
				{
					for (int i=0;i<types.length;i++)
					{
						String[] typevalues=types[i].split(",");
						if (typevalues!=null && typevalues.length>1)
						{
							NewsType newtype=new NewsType();
//							newtype.setTid(Long.valueOf(typevalues[0]));
							newtype.setTypeNames(typevalues[1]);
							baseDAO.save(newtype);
						}
					}
				}
				
			}
			
			// 初始用户
			value = properties.getProperty("userinfo");
			if (value != null)
			{
				//value = converString(value);
				values = value.split(";");
				size = values.length;
				String tp;
				for (int i = 0; i < size; i++)
				{
					String[] para = values[i].split(",");
					MD5 md5 = new MD5();
					tp = para[1].trim();
					para[1] = md5.getMD5ofStr(para[1].trim());
					Users ui = new Users(para[0].trim(), para[1], para[2].trim(),  Integer.valueOf(para[3].trim()), para[4].trim());
		            String spaceUID = jcrService.createSpace(FileConstants.USER_ROOT + ui.getUserName());
		            ui.setSpaceUID(spaceUID);   
		            ui.setRealPass(tp);
		            if (WebConfig.mailEnable)
		            {
		            	ui.setRealEmail(ui.getUserName() + "@" + WebConfig.mailDomain);
		            }		            
		            // 加入用户的公钥与私钥
		            try 
		            {		            	
		            	Map<String,String> keyMap = RSACoder.generateKey();
		            	ui.setPrivateKey(keyMap.get("RSAPrivateKey"));
		            	ui.setPublicKey(keyMap.get("RSAPublicKey"));
					}
		            catch (Exception e) 
		            {
		            	LogsUtility.error(e);
					}
					baseDAO.save(ui);
					
					space = new Spaces(spaceUID, ui.getUserName(), "", spaceUID);
					baseDAO.save(space);
					Roles r = roles.get(para[3].trim());
					if (r != null)
					{
						UsersRoles ur = new UsersRoles(ui, r);
						baseDAO.save(ur);
					}					
					
				}
			}
			
			// 初始化自定义职务
			value = properties.getProperty("jobtitles");
			if (value != null)
			{
				//value = converString(value);
				values = value.split(";");
				size = values.length;
				for (int i = 0; i < size; i++)
				{
					JobTitles jt = new JobTitles(values[i].trim());	
					baseDAO.save(jt);
				}
			}
			
			// 初始化系统公司
			value = properties.getProperty("company");
			Company company = null;
			if (value != null)
			{
				values = value.split(";");
				long t = Integer.valueOf(values[3]);
				t = t > 1000 ? 1000 : t;
				t = t * 365L * 24L * 60L * 60L * 1000L;
				company = new Company(values[0], values[1], Integer.valueOf(values[2]), System.currentTimeMillis() + t);
				company.setCode("");
				String spaceUID = jcrService.createSpace(FileConstants.COMPANY_ROOT + values[0]);
				space = new Spaces(spaceUID, values[0], "", spaceUID);	
				baseDAO.save(space);
				company.setSpaceUID(space.getSpaceUID());
	            
	            baseDAO.save(company);			
	            permissionDAO.copySystemRoleActionToCompany(company.getId());
			}
			
			// 初始化系统默认组织
			value = properties.getProperty("org");
			Organizations org = null;
			if (company != null && value != null)
			{
				values = value.split(";");
				org = new Organizations();
				org.setName(values[0]);
				org.setDescription(values[1].trim());
				org.setCompany(company);			
				String spaceUID = jcrService.createSpace(FileConstants.ORG_ROOT + values[0]);
				space = new Spaces(spaceUID, values[0], "", spaceUID);	
				baseDAO.save(space);
	            org.setSpaceUID(space.getSpaceUID());            
	            baseDAO.save(org);		
				permissionDAO.copySystemRoleActionToOrg(org.getId(), company.getId());
			}
			
			// 初始化公司管理员
			value = properties.getProperty("companyadmin");
			if (company != null && org != null && value != null)
			{
				values = value.split(";");
				MD5 md5 = new MD5();
				String passW = md5.getMD5ofStr(values[1]);
				Users user = new Users(values[0], passW, values[2], Constant.COMPANY_ADMIN, values[3]);
				if (WebConfig.mailEnable)
	            {
	            	user.setRealEmail(user.getUserName() + "@" + WebConfig.mailDomain);
	            }
				String spaceUID = jcrService.createSpace(FileConstants.USER_ROOT + values[0]);
	            user.setSpaceUID(spaceUID);   
	            user.setCompany(company);
	            user.setStorageSize(WebConfig.defaultsize);
	            // 加入用户的公钥与私钥
	            try 
	            {            	
	            	Map<String,String> keyMap = RSACoder.generateKey();
	            	user.setPrivateKey(keyMap.get("RSAPrivateKey"));
	            	user.setPublicKey(keyMap.get("RSAPublicKey"));
				}
	            catch (Exception e) 
	            {
	            	LogsUtility.error(e);
				}
				baseDAO.save(user);			
				space = new Spaces(spaceUID, values[0], "", spaceUID);
				baseDAO.save(space);			
								
//				UsersOrganizations uog = new UsersOrganizations(user, org);
//				baseDAO.save(uog);	//管理员不能加入到某一个部门，否则自己就能编辑自己了————孙爱华注掉的	
				
				String roleName = values[4].trim();
				Roles role = roles.get(roleName);
				if (role == null)
				{
					role = baseDAO.findSystemRoleByName(roleName, company.getId());
				}
				if (role != null)
				{
					UsersRoles ur = new UsersRoles(user, role);
					baseDAO.save(ur);
				}	
			}
			
		    // 初始化应用配置
			String fileName = WebConfig.webContextPath + "/WEB-INF/classes/META-INF/applications";
			AppsHandler.importApps(fileName);
						
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			LogsUtility.error(e);
			//throw e;
		}
	}
	
	/*private String converString(String s)
	{
		try
		{
			byte[] tmpbyte = s.getBytes("ISO-8859-1");
			return new String(tmpbyte, "utf-8");
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
			return s;
		}
	}*/
	
	/**
	 * 需要导入的组织结构及用户文件名及路径
	 * @param fileName
	 */
	public String importUsers(String fileName, Long companyId)
	{
		//EF BB BF
		String content = null;
		FileInputStream reader = null;
		try
		{
			reader = new FileInputStream(fileName);
			int size = reader.available();
			byte[] retByte = new byte[size];
			reader.read(retByte);
			if ((retByte[0] & 0x00FF) == 0xEF && (retByte[1] & 0x00FF) == 0xBB && (retByte[2]  & 0x00FF) == 0xBF)
			{
				content = new String(retByte, 3, size - 3, "utf-8");
			}
			else if ((retByte[0] & 0x00FF) == 0xFF && (retByte[1] & 0x00FF) == 0xFE)
			{
				content = new String(retByte, "unicode");
			}
			else
			{				
				content = new String(retByte, "GBK");
			}
			
			String[] lines = content.split("[\n\r]");
			StringBuffer sb = new StringBuffer();
			String[] ou;
			//Hashtable<String, Company> companys = new Hashtable<String, Company>();
			Hashtable<String, Organizations> orgs = new Hashtable<String, Organizations>();
			Hashtable<String, Roles> roles = new Hashtable<String, Roles>();
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			AppsService appsService = (AppsService)ApplicationContext.getInstance().getBean(AppsService.NAME);
			
			Company company = baseDAO.findCompanyById(companyId);
			if (company == null)      // 不允许导入的用户无公司存在
			{
				return "";
			}
			//companys.put(com.getName(), com);
			for (String line : lines)
			{
				line = line.trim();
				/*if (line.startsWith("company"))     // 导入公司
				{
					ou = line.split(",");
					importCompany(companys, ou, jcrService);
				}
				else*/ 
				if (line.startsWith("org"))     // 导入组织
				{
					ou = line.split(",");
					importOrganization(company, orgs, ou, jcrService);
				}
				else if (line.startsWith("user"))  // 导入数据
				{
					ou = line.split(",");
					String ret = importUser(company, orgs, roles, ou, jcrService, appsService);
					if (ret != null)
					{
						sb.append(ret);
						sb.append(";");
					}
				}
				else      // 其他数据
				{
					continue;
				}
			}
			return sb.toString();
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
			return "";
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception ee)
				{
					LogsUtility.error(ee);
				}
			}
		}		
	}
	
	/*private void importCompany(Hashtable<String, Company> companys, String[] comString, JCRService jcrService)
	{
		try
		{
			//  company,公司名字,公司描述,最大使用用户数,公司地址,联系人名,联系人email,联系人手机，多个手机用";"分开,联系人传真，多个传真用";"分开,联系人座机，多个座机用";"分开。			
			String temp = comString[3].trim();
			Integer max;
			try
			{
				max = Integer.valueOf(temp);
			}
			catch(Exception e)
			{
				LogsUtility.error(e);
				max = 1;
			}
			String name = comString[1].trim();
			Company comp = companys.get(name);
			if (comp != null)
			{
				return;
			}
			comp = baseDAO.findCompanyByName(name);
			if (comp != null)    // 已经存在公司，则直接返回
			{
				companys.put(name, comp);
				return;
			}
			comp = new Company(name, comString[2].trim(), comString[4].trim(), max);
			comp.setUserInfo(comString[5].trim(), comString[6].trim(), comString[7].trim(), comString[8].trim(), comString[9].trim());
			String spaceUID = jcrService.createSpace(FileConstants.COMPANY_ROOT + name);
			Spaces space = new Spaces(spaceUID, name, "", spaceUID);	
			baseDAO.save(space);
            comp.setSpaceUID(space.getSpaceUID());
            
            baseDAO.save(comp);			
            permissionDAO.copySystemRoleActionToCompany(comp.getId());
            companys.put(name, comp);			
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
	}*/
	
	private void importOrganization(Company company, Hashtable<String, Organizations> orgs, String[] orgString, JCRService jcrService)
	{
		try
		{
			//  org,研发,研发中心 			
			String temp = orgString[1].trim();
			Organizations org = orgs.get(temp);
			if (org != null)
			{
				return;
			}
			String name = temp;
			String parent = null;
			Organizations parentT = null;
			int index = temp.lastIndexOf(".");
			if (index > 0)
			{
				name = temp.substring(index + 1);
				parent = temp.substring(0, index);
			}			
			if (parent != null)
            {
				parentT = orgs.get(parent);
				if (parentT == null)
				{
					parentT = baseDAO.findOrganizationsByName(parent, company.getId());
					orgs.put(parent, parentT);
				}
            }
			org = baseDAO.findOrganizationsByName(name, parentT, company.getId());
			if (org != null)   // 已经存在，直接返回
			{
				orgs.put(temp, org);
				return;
			}
			org = new Organizations();
        	org.setParent(parentT);
			org.setName(name);
			org.setDescription(orgString[2].trim());
			org.setCompany(company);			
			String spaceUID = jcrService.createSpace(FileConstants.ORG_ROOT + name);
			Spaces space = new Spaces(spaceUID, name, "", spaceUID);	
			baseDAO.save(space);
            org.setSpaceUID(space.getSpaceUID());            
            baseDAO.save(org);			
            orgs.put(temp, org);
			permissionDAO.copySystemRoleActionToOrg(org.getId(), company.getId());
			
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
	}
	
	private String importUser(Company company, Hashtable<String, Organizations> orgs, Hashtable<String, Roles> roles, String[] userString,
			JCRService jcrService, AppsService appsService)
	{
		try
		{
			// user,test1,123456,测试1,test1@test.com,职务,研发.开发,空间创建员
			String name = userString[1].trim();
			if (WebConfig.cloudPro)
			{
				name = company.getCode() + "_" + name;
			}
			Users user = baseDAO.isExistUser(name); 
			Long companyId = company.getId();
			if (user != null)
			{
				if (companyId.longValue() == user.getCompany().getId().longValue())     // 只能更新本公司的人员
				{
					updateUser(orgs, roles,  userString, user);
				}
				return name;
			}
			
			if (baseDAO.isCompanyUserFull(companyId))      // 允许的最大人数已经满了，不能在添加
			{
				return null;
			}
			
			MD5 md5 = new MD5();
			String passW = md5.getMD5ofStr(userString[2]);
			user = new Users(name, passW, userString[4].trim(), 0, userString[3].trim());
			user.setRealPass(userString[2]);
			if (WebConfig.mailEnable)
            {
            	user.setRealEmail(user.getUserName() + "@" + WebConfig.mailDomain);
            }
			user.setDuty(userString[5].trim());
			String spaceUID = jcrService.createSpace(FileConstants.USER_ROOT + name);
            user.setSpaceUID(spaceUID);   
            user.setCompany(company);
            user.setStorageSize(WebConfig.defaultsize);
            // 加入用户的公钥与私钥
            try 
            {            	
            	Map<String,String> keyMap = RSACoder.generateKey();
            	user.setPrivateKey(keyMap.get("RSAPrivateKey"));
            	user.setPublicKey(keyMap.get("RSAPublicKey"));
			}
            catch (Exception e) 
            {
            	LogsUtility.error(e);
			}
			baseDAO.save(user);			
			Spaces space = new Spaces(spaceUID, name, "", spaceUID);
			baseDAO.save(space);			
			
			
			String[] orgNames = userString[6].trim().split("\\|");
			for (String orgName : orgNames)
			{
				orgName = orgName.trim();
				Organizations org = orgs.get(orgName);
				if (org == null)
				{
					org = baseDAO.findOrganizationsByName(orgName, companyId);
					//System.out.println("=======orgName=====" + orgName+"====");
					if (org!=null)
					{
						orgs.put(orgName, org);
					}
					else
					{
						System.out.println(orgName+"==============================="+org);
					}
				}
				if (org != null)
				{
					UsersOrganizations uog = new UsersOrganizations(user, org);
					baseDAO.save(uog);
				}
			}
			
			String roleName = userString[7].trim();
			Roles role = roles.get(roleName);
			if (role == null)
			{
				role = baseDAO.findSystemRoleByName(roleName, companyId);
				roles.put(roleName, role);
			}
			if (role != null)
			{
				UsersRoles ur = new UsersRoles(user, role);
				baseDAO.save(ur);
			}
			appsService.copyCompanyAppToUser(companyId, user);
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}	
	
	private void updateUser(Hashtable<String, Organizations> orgs,	Hashtable<String, Roles> roles, String[] userString, Users user)
	{
		try
		{
			// user,test1,123456,测试1,test1@test.com,职务,研发.开发,空间创建员,公司名
			user.setRealName(userString[3].trim());
			if (WebConfig.mailEnable)
            {
            	user.setRealEmail(user.getUserName() + "@" + WebConfig.mailDomain);
            }
			else
			{
				user.setRealEmail(userString[4].trim());
			}
			user.setDuty(userString[5].trim());			
			baseDAO.update(user);
			
			List<UsersOrganizations> olds = baseDAO.findUsersOrganizationsByUserId(user.getId());
			Hashtable<Long, UsersOrganizations> oldHash = new Hashtable<Long, UsersOrganizations>();
			for (UsersOrganizations t : olds)
			{
				oldHash.put(t.getOrganization().getId(), t);
			}			
			String[] orgNames = userString[6].trim().split("\\|");
			for (String orgName : orgNames)
			{
				orgName = orgName.trim();
				Organizations org = orgs.get(orgName);
				if (org == null)
				{
					org = baseDAO.findOrganizationsByName(orgName, user.getCompany().getId());
					//System.out.println("=======orgName=====" + orgName+"====");
					orgs.put(orgName, org);
				}
				if (org != null)
				{
					if (oldHash.get(org.getId()) == null)     // 新增或修改组织结构
					{
						UsersOrganizations uog = new UsersOrganizations(user, org);
						baseDAO.save(uog);
					}
					else     // 同原有组织一致
					{
						oldHash.remove(org.getId());
					}
				}
			}
			int size = oldHash.size();
			if (size > 0)
			{
				baseDAO.delete(oldHash.values());
			}
			
			
			UsersRoles ur = baseDAO.findUserRoleInSystem(user.getId(), user.getCompany().getId());
			String roleName = userString[7].trim();
			Roles role = roles.get(roleName);
			if (role == null)
			{
				role = baseDAO.findSystemRoleByName(roleName, user.getCompany().getId());
				roles.put(roleName, role);
			}
			if (role != null)   // 更新角色
			{
				if (ur == null)  // 原来没有角色
				{
					ur = new UsersRoles(user, role);
					baseDAO.save(ur);
				}
				else if (ur.getRole() == null || role.getId().longValue() != ur.getRole().getId().longValue())     // 原有角色同现在不一致
				{
					ur.setRole(role);
					baseDAO.update(ur);
				}
			}
			else    // 删除角色
			{
				if (ur != null)
				{
					baseDAO.delete(ur);
				}
			}
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
	}
	
	/**
	 * 获取资源的版本信息
	 * @param name 获取资源的名字，同版本配置文件中的定义一致（配置文件中最后一个_前的内容）。
	 * @return 返回资源的相关信息。[0]为版本号,[1]为资源在服务器中名字,[2]为资源在服务器中的位置
	 */
	public String[] getVersionContent(String name)
	{
		if (WebConfig.cloudPro)//公云
		{
			name="iyo"+name;
		}
		String content = null;
		InputStream reader = null;
		try
		{ 
			//reader = getClass().getClassLoader().getResourceAsStream("/config/versionConfig.properties");
			reader = new FileInputStream(WebConfig.webContextPath + "/WEB-INF/classes/conf/versionConfig.properties");
			int size = reader.available();
			byte[] retByte = new byte[size];
			reader.read(retByte);
			if ((retByte[0] & 0x00FF) == 0xEF && (retByte[1] & 0x00FF) == 0xBB && (retByte[2]  & 0x00FF) == 0xBF)
			{
				content = new String(retByte, 3, size - 3, "utf-8");
			}
			else if ((retByte[0] & 0x00FF) == 0xFF && (retByte[1] & 0x00FF) == 0xFE)
			{
				content = new String(retByte, "unicode");
			}
			else
			{				
				content = new String(retByte, "GBK");
			}
			//System.out.println("======\n" + content);
			String[] lines = content.split("[\n\r]");
			name += "=";
			for (String line : lines)
			{
				line = line.trim();
				if (line.startsWith(name))     // 
				{
					int index = line.indexOf("=");
					if (index >= 0)
					{
						line = line.substring(index + 1);
					}
					return line.split(";");
				}
				else      // 其他数据
				{
					continue;
				}
			}
			return null;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
			return null;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception ee)
				{
					LogsUtility.error(ee);
				}
			}
		}
	}
	
	private void importFiles() throws Exception
	{
		    File configFile=new File(WebConfig.webContextPath+File.separatorChar +"WEB-INF"+File.separatorChar+"classes"+File.separatorChar+"conf"+File.separatorChar+"importfilespath.txt");
		    
		    if(!configFile.exists()){
				System.out.println("加载用户数据的文件配置文件不存在");
				return;
			}
		    /**
		     * 解析配置文件，获取要加载文件的位置
		     */
		    File loadFile=this.getLoadFile(configFile);
		    
		    if(loadFile.exists()){
		    	 /**
			     * 解析并加载文件
			     */
			    JcrFileDataHelper.getInstance().loadJcrFiles(loadFile);
		    }
		    
		    
		    //删除配置文件以免下次重启服务器再次加载
		   configFile.delete();

	}
	
	/**
	 * 根据配置文件获取需要加载文件的路径文件
	 *               <b>这块需要重构</b>
	 * @param confileFile
	 *            配置文件
	 * @return
	 * 
	 * @throws Exception
	 */
	private File getLoadFile(File confileFile) throws Exception{
		FileInputStream input = new FileInputStream(confileFile);
		int size = input.available();
		byte[] retByte = new byte[size];
		input.read(retByte);
		String content="";
		if (retByte.length>0)
		{
			if ((retByte[0] & 0x00FF) == 0xEF && (retByte[1] & 0x00FF) == 0xBB && (retByte[2]  & 0x00FF) == 0xBF)
			{
				content = new String(retByte, 3, size - 3, "utf-8");
			}
			else if ((retByte[0] & 0x00FF) == 0xFF && (retByte[1] & 0x00FF) == 0xFE)
			{
				content = new String(retByte, "unicode");
			}
			else
			{				
				content = new String(retByte, "GBK");
			}
			content=content.trim();
		}
		input.close();
		importfilepath=content;
		return new File(content);
	}
   private void initTemplate() throws Exception{
	   if(WebConfig.importtemplate){
		   IFileTemplateService   fileTemplateService=(IFileTemplateService) ApplicationContext.getInstance().getBean(FileTemplateService.NAME);
		   System.out.println("系统内置模板初始化");
		   String templateLocation=properties.getProperty("templateLocation");
		   String template=properties.getProperty("template");
		   if(templateLocation !=null && !templateLocation.trim().isEmpty()){
			   String[] values=template.split(";");//模板名称
			   if (values!=null)
			   {
				   File testfile=new File(WebConfig.webContextPath+File.separatorChar +templateLocation);
				   for(File f:testfile.listFiles()){
					   System.out.println("f=========================="+f.getPath());
				   }
				   
				   for(String templateName:values){
					   File file=new File(WebConfig.webContextPath+File.separatorChar +templateLocation+File.separatorChar+templateName);
					   System.out.println("=========================="+WebConfig.webContextPath+File.separatorChar +templateLocation+File.separatorChar+templateName);
					   if(file.exists() && file.isDirectory()){
						   System.out.println("==========================file.exists");
						   long templateId=fileTemplateService.addSystemTemplate(templateName);
						   for(File f:file.listFiles()){
							   System.out.println("f=========================="+f.getPath());
							   FileTransfer fileTransfer=new FileTransfer(f.getName(),"",f.getTotalSpace(),new FileInputStream(f));
							   File image=new File(WebConfig.webContextPath+File.separatorChar+"cloud/images/view/111.jpg");
							   FileTransfer imageTransfer=new FileTransfer(image.getName(),"",image.getTotalSpace(),new FileInputStream(image));
							   fileTemplateService.addSystemTemplateItem(null, templateId, WebConfig.webContextPath,f.getName(),f.getName(),image.getName(),fileTransfer,imageTransfer);
						   }
					   }
					   else
					   {
						   System.out.println("not==========================file.exists");
					   }
				   }
			   }
		   }
	   }
	   
   }
   private void replaceLog()
   {
	   try
	   {
		   WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
		   String logostr = WebConfig.webContextPath + File.separatorChar + "WEB-INF" + File.separatorChar 
				   + "classes"+ File.separatorChar +"conf"+ File.separatorChar +"logo.properties";
		   File logofile=new File(logostr);
		   if (logofile.exists())
		   {
			    FileInputStream input = new FileInputStream(logostr);
				int size = input.available();
				byte[] retByte = new byte[size];
				input.read(retByte);
				String content="";
				if (retByte.length>0)
				{
					if ((retByte[0] & 0x00FF) == 0xEF && (retByte[1] & 0x00FF) == 0xBB && (retByte[2]  & 0x00FF) == 0xBF)
					{
						content = new String(retByte, 3, size - 3, "utf-8");
					}
					else if ((retByte[0] & 0x00FF) == 0xFF && (retByte[1] & 0x00FF) == 0xFE)
					{
						content = new String(retByte, "unicode");
					}
					else
					{				
						content = new String(retByte, "GBK");
					}
					content=content.trim();
				}
				input.close();
				String[] lines = content.split("[\n\r]");//获取所有内容
				if (lines!=null && lines.length>0)
				{
					for (int i=0;i<lines.length;i++)
					{
						if (lines[i]!=null && lines[i].trim().length()>0 && lines[i].indexOf("=")>0)
						{
							String oldstr=lines[i].trim();
							String tempstr=null;
							if (webConfig.cloudPro )//公云
							{
								if (oldstr.startsWith("public."))
								{
									tempstr=oldstr;
								}
							}
							else if (oldstr.startsWith("private."))
							{
								tempstr=oldstr;
							}
							if (tempstr!=null)
							{
								int index=tempstr.indexOf(".");
								tempstr=tempstr.substring(index+1);
								index=tempstr.indexOf("=");
								String repname=tempstr.substring(0,index);//需要替换的文件
								String totalstr=tempstr.substring(index+1);//替换的内容
								index=totalstr.indexOf("##");
								String encode=totalstr.substring(0,index);
								String repstr=totalstr.substring(index+2);
								List<String[]> list=new ArrayList<String[]>();
								String[] replaces=repstr.split(";");
								for (int n=0;n<replaces.length;n++)
								{
									String[] values=replaces[n].split(",");
									list.add(values);
								}
								FileInputStream replaceinput = new FileInputStream(WebConfig.webContextPath + File.separatorChar +repname);
								int repsize = replaceinput.available();
								byte[] repByte = new byte[repsize];
								replaceinput.read(repByte);
								String totalrep="";
								totalrep = new String(repByte, encode);
								replaceinput.close();
								for (String[] values:list)
								{
									totalrep=totalrep.replace(values[0], values[1]);
								}
//								System.out.println(totalrep);
//								System.out.println("encode===="+encode);
//								FileOutputStream out=new FileOutputStream(WebConfig.webContextPath + File.separatorChar +repname);
//								out.write(totalrep.getBytes());
								Writer out = new OutputStreamWriter(new FileOutputStream(WebConfig.webContextPath + File.separatorChar +repname), "utf-8");
								out.write(totalrep);

//								BufferedWriter out=new BufferedWriter(new FileWriter(WebConfig.webContextPath + File.separatorChar +repname));
//								out.write(totalrep);
								out.close();
							}
						}
					}
				}
		   }
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
   }
}
