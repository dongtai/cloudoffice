package apps.transmanager.weboffice.service.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.RoleCons;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.Filetaginfo;
import apps.transmanager.weboffice.databaseobject.Groupmembershareinfo;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Groupshareinfo;
import apps.transmanager.weboffice.databaseobject.License;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.PublishAddress;
import apps.transmanager.weboffice.databaseobject.Receptionpower;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.Taginfo;
import apps.transmanager.weboffice.databaseobject.UserDesks;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersConfig;
import apps.transmanager.weboffice.databaseobject.UsersCustomTeams;
import apps.transmanager.weboffice.databaseobject.UsersDevice;
import apps.transmanager.weboffice.databaseobject.UsersGroups;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.UsersRoles;
import apps.transmanager.weboffice.domain.AdminConfig;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.GroupmemberinfoView;
import apps.transmanager.weboffice.service.auth.ldap.LDAPUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.AdminUserinfoViewDAO;
import apps.transmanager.weboffice.service.dao.FiletaginfoDAO;
import apps.transmanager.weboffice.service.dao.GroupmemberinfoViewDAO;
import apps.transmanager.weboffice.service.dao.GroupmembershareinfoDAO;
import apps.transmanager.weboffice.service.dao.GroupshareinfoDAO;
import apps.transmanager.weboffice.service.dao.NormalDAO;
import apps.transmanager.weboffice.service.dao.PermissionDAO;
import apps.transmanager.weboffice.service.dao.PersonshareinfoDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.dao.SysReportDAO;
import apps.transmanager.weboffice.service.dao.TaginfoDAO;
import apps.transmanager.weboffice.service.dao.UserWorkadayDAO;
import apps.transmanager.weboffice.service.dao.UserinfoViewDAO;
import apps.transmanager.weboffice.service.dao.reception.ReceptionDAO;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.license.LicenseEn;
import apps.transmanager.weboffice.service.mail.send.MailSender;
import apps.transmanager.weboffice.service.objects.FileArrayComparator;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.RSACoder;

/**
 * 系统用户服务类，主要提供与用户，组织结构等相关的处理。
 * <p>
 * <p>
 * <p>
 */

@Component(value=UserService.NAME)
public class UserService 
{
	public static final String NAME = "userService";
	@Autowired
    private FiletaginfoDAO filetaginfoDAO;
	@Autowired
    private StructureDAO structureDAO;
	@Autowired
	private PermissionDAO permissionDAO;
	@Autowired
    private GroupshareinfoDAO groupshareinfoDAO;
	@Autowired
    private PersonshareinfoDAO personshareinfoDAO;
	@Autowired
    private TaginfoDAO taginfoDAO;
	@Autowired
    private UserinfoViewDAO userinfoViewDAO;
    @Autowired
    private GroupmemberinfoViewDAO groupmemberinfoViewDAO;
    @Autowired
    private AdminUserinfoViewDAO adminUserinfoViewDAO;
    @Autowired
    private GroupmembershareinfoDAO groupmembershareinfoDAO;   
    
    @Autowired
    private UserWorkadayDAO userWorkadayDao;
    //@Autowired
    //private ACListDAO acListDAO;
    @Autowired
    private NormalDAO normalDAO;
    @Autowired
    private SysReportDAO sysReportDAO;
    @Autowired
	private ReceptionDAO receptionDao;
    
    public ReceptionDAO getReceptionDao() {
		return receptionDao;
	}

	public void setReceptionDao(ReceptionDAO receptionDao) {
		this.receptionDao = receptionDao;
	}
    private boolean ldapRequrie;
    
    private boolean adminSplit;  //是否是三权分离方式
	private boolean superAdminEnable;   //在三权分离的情况下是否允许超级管理员存在
	
	// 得到该用户的所有的权限
	/*public List<Permission> getPermissionByUserID(long userId, int from, int count)
	{
		return acListDAO.getPermissionByUserID(userId, from, count);
	}*/
	
	//得到所有共享显示名
//	public List getAllShowName(String[] paths)
//	{
//		return acListDAO.getPermissions(paths);
//	}
//	
//	public List getShowP(List list)
//	{
//		int len = list.size();
//		String[] ss = new String[len];
//		for (int i=0;i<len;i++)
//		{
//			String[] s = (String[])list.get(i);
//			ss[i] = s[1];
//			
//		}		
//		List pL = acListDAO.getShowNamePermissions(ss);		
//		return pL; 
//	}
	
//	public List saveShowName(List list)
//	{
//		List existL = getShowP(list);
//		if (existL != null && existL.size() > 0)
//		{
//			return existL;
//		}
//		else
//		{
//			int len = list.size();
//			for (int i=0;i<len;i++)
//			{
//				String[] s = (String[])list.get(i);
//				
//				Permission p = new Permission();
//				p.setAbstractPath(s[0]);
//				p.setDisplayName(s[1]);
//				if (s[2].equals("1"))
//				{
//					p.setIsFolder(true);
//				}
//				acListDAO.save(p);
//			}
//			return null;
//		}
//	}
	
	/**
	 * 根据绝对路径取得所有的权限
	 */
//	public List getPowerinfo(DataHolder pathHolder)
//    {
//        String[] path = pathHolder.getStringData();
////        ArrayList l = new ArrayList();
////        for (int i = 0; i < path.length; i++)
////        {        
////        	ACList aclist = new ACList();
////        	
////        	Permission permission = new Permission();
////        	permission.setAbstractPath(path[i]);        	
////        	List backL = acListDAO.findByPermission(permission);        	
////        	if (backL == null || backL.size() == 0)
////        	{
////        	}
////        	else
////        	{
////        		permission = (Permission)backL.get(0);
////        		aclist.setPermission(permission);
////        		List list = acListDAO.findByExample(aclist);
////                l.addAll(list);
////        	}
////        }
//		return acListDAO.getACLists(path);
////        return l;
//        //    	return getSharedUser(path[0]);
//    }
	
	/**
	 * 删除所有权限
	 */
//	public void deleteAllPower(String[] filePath)
//	{
//		acListDAO.deleteByPaths(filePath);
//	}
	
	/**
	 * 是否共享
	 * @param paths
	 * @return
	 */
//	public boolean checkShare(String[] paths) {
//		List list = acListDAO.getPermissions(paths);
//		if (list != null && list.size() > 0)
//		{
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
	
	/**
	 * 设置权限
	 * @param newShareFiles：新增的权限
	 * @param delIDs：删除的权限
	 * @param modifyArrayList：更新的权限
	 */
//	public void setPowerinfo(ArrayList newShareFiles, ArrayList<String> delIDs,
//	        ArrayList<String> modifyArrayList)
//	    {
//	        if (delIDs != null)
//	        {
//	            int size = delIDs.size();
//	            for (int i = 0; i < size; i++)
//	            {
//	                String temp = delIDs.get(i);
////	                int type = Integer.parseInt(temp.substring(0, 1));
//	                long id = Long.parseLong(temp.substring(1, temp.length()));
//	                acListDAO.deleteByID(id);	                
//	                /*if (i % 20 == 0)
//	                {
//	                    sessionFactory.getCurrentSession().flush();
//	                    sessionFactory.getCurrentSession().clear();
//	                }*/
//
//	            }
//	        }
//	        if (newShareFiles != null)
//	        {
//	            int size = newShareFiles.size();
//	            for (int i = 0; i < size; i++)
//	            {
//	                Object temp = newShareFiles.get(i);
//	                if (temp instanceof Personshareinfo)
//	                {
////	                    personshareinfoDAO.save((Personshareinfo)temp);
//	                	Personshareinfo info = (Personshareinfo)temp;
//	                	ACList aclist = new ACList();
//	                	String shareFile = info.getShareFile();
//	                	
////	                	Permission permission = new Permission();
////	                	permission.setAbstractPath(shareFile);
////	                	permission.setDisplayName("dhl");
////	                	List backL = acListDAO.findByPermission(permission);
////	                	
////	                	if (backL == null || backL.size() == 0)
////	                	{
////	                		acListDAO.save(permission);
////	                	}
////	                	else
////	                	{
////	                		permission = (Permission)backL.get(0);
////	                	}
//	                		
//	                	List pL = acListDAO.getPermissions(new String[]{shareFile});
//	                	aclist.setPermission((Permission)pL.get(0));
//	                	
//	                	
//	                	
//	                	aclist.setUserinfo(info.getUserinfoBySharerUserId());
//	                	
//	                	List backL2 = acListDAO.findByACList(aclist);
//	                	
//	                	aclist.setPermissionValue(info.getPermit().longValue());
////	                	acListDAO.save(aclist);
//	                	
//	                	if (backL2 == null || backL2.size() == 0)
//	                	{
//	                		acListDAO.save(aclist);
//	                	}
//	                	else
//	                	{
//	                		aclist = (ACList)backL2.get(0);
//	                		aclist.setPermissionValue(info.getPermit().longValue());
//	                		acListDAO.save(aclist);
////	                		permission = (Permission)backL.get(0);
//	                	}
//	                	
//	                }
//	                else if (temp instanceof Groupshareinfo)
//	                {
//	                	Groupshareinfo info = (Groupshareinfo) temp;
//	                	ACList aclist = new ACList();
//	                	String shareFile = info.getShareFile();
//	                	
////	                	Permission permission = new Permission();
////	                	permission.setAbstractPath(shareFile);
////	                	permission.setDisplayName("dhl");
////	                	List backL = acListDAO.findByPermission(permission);
////	                	
////	                	if (backL == null || backL.size() == 0)
////	                	{
////	                		acListDAO.save(permission);
////	                	}
////	                	else
////	                	{
////	                		permission = (Permission)backL.get(0);
////	                	}
//	                		
//	                	List pL = acListDAO.getPermissions(new String[]{shareFile});
//	                	aclist.setPermission((Permission)pL.get(0));
//	                	
//	                	
//	                	aclist.setGroupInfo(info.getGroupinfo());
//	                	
//	                	List backL2 = acListDAO.findByACList(aclist);
//	                	
//	                	aclist.setPermissionValue(info.getPermit().longValue());
////	                	acListDAO.save(aclist);
//	                	
//	                	if (backL2 == null || backL2.size() == 0)
//	                	{
//	                		acListDAO.save(aclist);
//	                	}
//	                	else
//	                	{
//	                		aclist = (ACList)backL2.get(0);
//	                		aclist.setPermissionValue(info.getPermit().longValue());
//	                		acListDAO.save(aclist);
////	                		permission = (Permission)backL.get(0);
//	                	}
//	                	
////	                	Groupshareinfo gsinfo = (Groupshareinfo) temp;
////	                	Groupmembershareinfo gminfo = new Groupmembershareinfo();
////	                	List<UsersOrganizations> l = groupmemberinfoDAO.findByGroupId(gsinfo.getGroupinfo().getGroupId());
////	                	Iterator<UsersOrganizations> iter = l.iterator();
////	        			while(iter.hasNext())
////	        			{
////	        				UsersOrganizations gmi = iter.next();
////	        				Userinfo ui = gmi.getUserinfo();
////	        				gminfo.setUserinfo(ui);
////	        				gminfo.setShareFile(gsinfo.getShareFile());
////	        				gminfo.setShareowner(gsinfo.getUserinfo().getUserId());
////	        				gminfo.setGroupinfo(gsinfo.getGroupinfo());
////	        				gminfo.setGroupmemberinfo(gmi);
////	        				if(gminfo.getIsNew() == null)
////	        				{
////	        					gminfo.setIsNew(0);
////	        				}
////							groupmembershareinfoDAO.save(gminfo);
////
////	        			}
////	                    groupshareinfoDAO.save((Groupshareinfo)temp);
//	                }
//	            }
//	        }
//
//	        if (modifyArrayList != null)
//	        {
//	            int size = modifyArrayList.size();
//	            for (int i = 0; i < size; i++)
//	            {
//	                String temp = modifyArrayList.get(i);
//	                int pos = temp.indexOf(",");
////	                int type = Integer.parseInt(temp.substring(0, 1));
//	                long id = Long.parseLong(temp.substring(1, pos));
//	                int permit = Integer.parseInt(temp.substring(pos + 1));
//	                ACList aclist = acListDAO.findById(id);
//	                aclist.setPermissionValue(new Integer(permit).longValue());
//	                acListDAO.save(aclist);
//	            }
//	        }
//	    }
	
	public void setSuperAdminEnable(boolean superAdminEnable)
	{
		this.superAdminEnable = superAdminEnable;
	}
    
//    public ACListDAO getAcListDAO()
//	{
//		return acListDAO;
//	}
//
//	public void setAcListDAO(ACListDAO acListDAO)
//	{
//		this.acListDAO = acListDAO;
//	}

	public void setAdminSplit(boolean adminSplit)
	{
		this.adminSplit = adminSplit;
	}

    public void setSysReportDAO(SysReportDAO sysReportDAO)
    {
        this.sysReportDAO = sysReportDAO;
    }

    public SysReportDAO getSysReportDAO()
    {
        return sysReportDAO;
    }

    public GroupmemberinfoViewDAO getGroupmemberinfoViewDAO()
    {
        return groupmemberinfoViewDAO;
    }

    public void setGroupmemberinfoViewDAO(GroupmemberinfoViewDAO groupmemberinfoViewDAO)
    {
        this.groupmemberinfoViewDAO = groupmemberinfoViewDAO;
    }

    public UserinfoViewDAO getUserinfoViewDAO()
    {
        return userinfoViewDAO;
    }

    public void setUserinfoViewDAO(UserinfoViewDAO userinfoViewDAO)
    {
        this.userinfoViewDAO = userinfoViewDAO;
    }


    public FiletaginfoDAO getFiletaginfoDAO()
    {
        return filetaginfoDAO;
    }

    public void setFiletaginfoDAO(FiletaginfoDAO filetaginfoDAO)
    {
        this.filetaginfoDAO = filetaginfoDAO;
    }

    public StructureDAO getStructureDAO()
    {
        return structureDAO;
    }

    public void setStructureDAO(StructureDAO structureDAO)
    {
        this.structureDAO = structureDAO;
    }

    public GroupshareinfoDAO getGroupshareinfoDAO()
    {
        return groupshareinfoDAO;
    }

    public void setGroupshareinfoDAO(GroupshareinfoDAO groupshareinfoDAO)
    {
        this.groupshareinfoDAO = groupshareinfoDAO;
    }

    public PersonshareinfoDAO getPersonshareinfoDAO()
    {
        return personshareinfoDAO;
    }

    public void setPersonshareinfoDAO(PersonshareinfoDAO personshareinfoDAO)
    {
        this.personshareinfoDAO = personshareinfoDAO;
    }

    public TaginfoDAO getTaginfoDAO()
    {
        return taginfoDAO;
    }

    public void setTaginfoDAO(TaginfoDAO taginfoDAO)
    {
        this.taginfoDAO = taginfoDAO;
    }

    
    public AdminUserinfoViewDAO getAdminUserinfoViewDAO()
    {
        return adminUserinfoViewDAO;
    }

    public void setAdminUserinfoViewDAO(AdminUserinfoViewDAO adminUserinfoViewDAO)
    {
        this.adminUserinfoViewDAO = adminUserinfoViewDAO;
    }

    
	public GroupmembershareinfoDAO getGroupmembershareinfoDAO() {
		return groupmembershareinfoDAO;
	}

	public void setGroupmembershareinfoDAO(
			GroupmembershareinfoDAO groupmembershareinfoDAO) {
		this.groupmembershareinfoDAO = groupmembershareinfoDAO;
	}

    public Users saveUser(Users info)
    {
        try
        {
            if(info.getRole() == null)
            {
                info.setRole((short)0);
            }
            String spaceUID = createSpace(FileConstants.USER_ROOT, info.getUserName(), info.getUserName(), "", "").getSpaceUID();            
            info.setSpaceUID(spaceUID);            
            structureDAO.save(info); 
            return info;
        }
        catch (Exception e) {
            return null;
        }

    }
    
    public void saveMember(Users user,String description)
    {
    	List list = structureDAO.findOrganizationsByOrgProperty("description", description);
    	if (list == null || list.size() < 1)
    	{
    		return;
    	}
    	
    	Organizations gi = (Organizations)list.get(0);    
    	List<UsersOrganizations> gm = structureDAO.findUsersOrganizationsByUserId(user.getId());
    	if (gm != null && gm.size() > 0)
    	{
    		UsersOrganizations temp = gm.get(0);
    		temp.setOrganization(gi);
    		structureDAO.update(temp);
    	}
    	else
    	{
    		UsersOrganizations temp = new UsersOrganizations();
    		temp.setUser(user);
    		temp.setOrganization(gi);
    		structureDAO.save(temp);
    	}
    	
    }
    
    public void updateGroup(long parentId,String groupName,String description)
    {
    	List list = structureDAO.findOrganizationsByOrgProperty("description", description);
    	if (list == null || list.size() < 1)
    	{
    		return;
    	}
    	Organizations parent = structureDAO.findOrganizationsById(parentId);
    	Organizations gi = (Organizations)list.get(0);
    	gi.setName(groupName);
    	gi.setParent(parent);
    	structureDAO.update(gi);
    }
    
    public Organizations getGroupinfo(String description)
    {
    	List list = structureDAO.findOrganizationsByOrgProperty("description", description);
    	if (list == null || list.size() < 1)
    	{
    		return null;
    	}
    	return (Organizations)list.get(0);
    }
    
    @SuppressWarnings("unchecked")
	public DataHolder enrolUser(Users user, Long orgId)
    {

        DataHolder dataUser = new DataHolder();
        Short role = 0;
        if(user.getRole() == null)
        {
            user.setRole(role);
        }
        else
        {
            role = user.getRole();
        }
        //邮箱地址和用户名都不能重复注册(user308 for 变更)
        //List<Users> exitsMail = structureDAO.findUserByProperty("email", user.getEmail());
        List<Users> exitsUserName = structureDAO.findUserByProperty("userName", user.getUserName());
        List<Users> exitsCaId = structureDAO.findUserByProperty("caId", user.getCaId());
        //boolean exist1 = (exitsMail != null && exitsMail.size() > 0);
        boolean exist2 = (exitsUserName != null && exitsUserName.size() > 0);
        boolean exist3 = (exitsCaId != null && exitsCaId.size() > 0);
        if(exist2)
        {
            dataUser.setIntData(Constant.ENROL_EXITSEUSERNAME);
            return dataUser;
        }
        if(exist3)
        {
            dataUser.setIntData(Constant.ENROL_EXITSEUSERNAMECA);
            return dataUser;
        }

        MD5 md5 = new MD5();
        String oPassW = user.getPassW();
        String passW = md5.getMD5ofStr(oPassW);
        user.setPassW(passW);
        saveUser(user);
        Organizations gi = structureDAO.findOrganizationsById(orgId);
        UsersOrganizations gm = new UsersOrganizations();
        gm.setOrganization(gi);
        gm.setUser(user);
        structureDAO.save(gm);
        
        user.setCompanyId(Constant.PUBLICID);
        String companyID = user.getCompanyId();
        
        Users needUser = new Users();
        needUser.setId(user.getId());
        needUser.setCompanyId(user.getCompanyId());
        //needUser.setEmail(user.getEmail());
        needUser.setRealEmail(user.getRealEmail());
        needUser.setPassW(oPassW);
        /*String dep = user.getDepartment();
        if (dep != null)
        {
            needUser.setDepartment(dep);
        }*/
        String duty = user.getDuty();
        if (duty != null)
        {
            needUser.setDuty(duty);
        }
        String image = user.getImage1();
        if (image != null)
        {
            needUser.setImage(image);
        }
        Integer option = user.getMyoption();
        needUser.setMyoption(option);

        needUser.setRole(role);
        Float size = user.getStorageSize();
        needUser.setStorageSize(size);
        String name = user.getUserName();
        if (name != null)
        {
            needUser.setUserName(name);
        }
        dataUser.setIntData(Constant.PERMIT_USER);
        dataUser.setUserinfo(needUser);
        return dataUser;
    
    }

    
    /**
     * 其他登录方式登录系统后，把用户的信息映射到本系统的表中。
     * @return
     */
    private Users mapUserInfo(Hashtable<String, Object> ret, String password, boolean saveFlag) 
    {
    	Users needUser = new Users();
        
        MD5 md5 = new MD5();
        String passW = md5.getMD5ofStr(password);
        needUser.setPassW(passW);
        needUser.setUserName((String)ret.get(PropsConsts.USER_NAME));
        String tempS = (String)ret.get(PropsConsts.USER_REAL_NAME);
        if (tempS == null || tempS == "")
        {
        	tempS = needUser.getUserName();
        }
        needUser.setRealName(tempS);
        
        tempS = (String)ret.get(PropsConsts.USER_MAIL);
        if (tempS == null || tempS == "")
        {
        	tempS = needUser.getUserName() + "@com";
        }
        //needUser.setEmail(tempS);
        if (needUser.getRealEmail() == null)
        {
        	needUser.setRealEmail(tempS);
        }
        
        needUser.setCompanyId((String)ret.get(PropsConsts.USER_COMPANY));
        if (needUser.getCompanyId() == null)
        {
        	needUser.setCompanyId(PropsConsts.DEFAULT_COMPANY);
        }
        //needUser.setDepartment((String)ret.get(PropsConsts.USER_DEP));
        needUser.setDuty((String)ret.get(PropsConsts.USER_DUTY)); 

        needUser.setLoginType((Integer)ret.get(PropsConsts.LOGIN_TYPE));
        needUser.setRole((short)Constant.USER);
        needUser.setStorageSize(WebConfig.defaultsize);
        
        if (saveFlag)
        {
        	//userinfoDAO.save(needUser);
        	saveUser(needUser);
        }
        mapGroup((String)ret.get(PropsConsts.LDAP_USER_GROUP_NAME), needUser);
        return needUser;
    }
    
    /**
     * 其他登录方式登录系统后，把用户的组信息映射到本系统的表中。
     * @param members
     */
    private void mapGroup(String groupName,	Users member)
    {
        List<Organizations> groups = structureDAO.findOrganizationsByOrgProperty("name", groupName);
        Organizations group;
        if (groups != null && groups.size() > 0)
        {
        	group = groups.get(0);
        }
        else
        {
        	group = createOrganization(groupName, "LDAP create group", null, null);
        }        
        
        UsersOrganizations groupmemberinfo = new UsersOrganizations();
        groupmemberinfo.setOrganization(group);
        groupmemberinfo.setUser(member);
        structureDAO.save(groupmemberinfo);
        
    }

    private Users ldapLogin(String email, String password)
    {
    	try
    	{
	    	Hashtable<String, Object> ret = LDAPUtil.authenticateUser(email, password);
	    	if (ret != null)
	    	{
	    		//List list = structureDAO.findUserByProperty("email", email);
	    		List list = structureDAO.findUserByProperty("userName", email);
	    		/*if (list.size() < 1)
	    		{
	    			list = list2;
	    		}*/
	    		Users needUser;
	    		if (list.size() < 1)
	            {
			        needUser = mapUserInfo(ret, password, true);			       
	            }
	            else
	            {
	            	needUser = (Users)list.get(0);
	            }
		        return needUser;
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public DataHolder SSOLogin(Hashtable<String, Object> ret) 
    {
    	String email = (String)ret.get(PropsConsts.USER_NAME);
    	//List list = structureDAO.findUserByProperty("email", email);
		List list = structureDAO.findUserByProperty("userName", email);
		/*if (list.size() < 1)
		{
			list = list2;
		}*/
		Users needUser;
		if (list.size() < 1)
        {
	        needUser = mapUserInfo(ret, "123456", true);			       
        }
        else
        {
        	needUser = (Users)list.get(0);
        	/*if (CompilerConsts.JINDIE_AUDTING)
        	{
        		synUser4Jindei(needUser, ret);
        	}*/
        }
        return loginCheck(email, "123456", needUser, false);
    }
        
    //-------------------------------------------------------------------------
    //
    // Public interface
    //
    //-------------------------------------------------------------------------
    /**
     * Authenticates the user from its login and password
     */
    public DataHolder loginCheck(String loginStr, String password)
    {
    	Users user = null;
    	boolean passFlag = true;
    	if (ldapRequrie)
    	{
    		user = ldapLogin(loginStr, password);
    		if (user != null)
    		{
    			passFlag = false;
    		}
    	}
    	return loginCheck(loginStr, password, user, passFlag);
    }
    
    public DataHolder loginCheckCA(Users userInfo)
    {
    	return loginCheck(userInfo);
    }
    
    public DataHolder loginCheck(String loginStr, String password,boolean nopassword)
    {
    	if(nopassword)
    	{
    		return loginCheck(loginStr, password, null, !nopassword);
    	}
    	return loginCheck(loginStr, password);
    }
    
    private DataHolder loginCheck(String loginStr, String password, Users user, boolean passFlag)
    {	
    	DataHolder dataUser = new DataHolder();
    	if (user == null)
    	{
	        List list2 = structureDAO.findUserByProperty("userName", loginStr);
	        if (list2.size() < 1)
	        {
	            dataUser.setIntData(Constant.NO_USER);
	            return dataUser;
	        }
	        if(list2.size()>=1)
	        {
	            user = (Users)list2.get(0);
	        }
    	}
    	    	
    	 if ("1".equals(user.getLoginCA()))//
         {
             dataUser.setIntData(Constant.LOGINCA);
             return dataUser;
         }
    	
        String userPassword = user.getPassW();
        
        if (user.getRole() != null)
        {
        	short role;
            role = user.getRole();
            if (adminSplit && !superAdminEnable && role == Constant.ADIMI)    // 三权分离、不允许超级管理员存在的情况下，不允许超级管理员登陆
            {
            	dataUser.setIntData(Constant.NO_USER);
	            return dataUser;
            }
            else if (!adminSplit && (role == Constant.AUDIT_ADMIN 
            		|| role == Constant.USER_ADMIN 
            		|| role == Constant.SECURITY_ADMIN))   // 在非三权分离的情况下，不允许用户管理员、审核员以及安全员登陆
            {
            	dataUser.setIntData(Constant.NO_USER);
	            return dataUser;
            }
        }
//        else
//        {
//            role = 0;
//            user.setRole(role);
//        }
        if (user.getValidate().intValue() == 0)//
        {
            dataUser.setIntData(Constant.NO_PERMIT_USER);
            return dataUser;
        }
        if (passFlag)
        {
	        if (userPassword == null || userPassword.equals(""))
	        {
	            if (!password.equals(""))
	            {
	                dataUser.setIntData(Constant.PASSWORD_ERROR);
	                return dataUser;
	            }
	        }
	        else
	        {
	            MD5 md5 = new MD5();
	            String opassword = md5.getMD5ofStr(password);
	            if (!(userPassword.equals(opassword)))
	            {
	                dataUser.setIntData(Constant.PASSWORD_ERROR);
	                return dataUser;
	            }
	        }
        }
        String companyID = user.getCompanyId();
        if (companyID == null)
        {
            dataUser.setIntData(Constant.COMPANY_ERROR);
        }
        Users needUser = new Users();
        needUser.setId(user.getId());
        needUser.setCompanyId(user.getCompanyId());
        //needUser.setEmail(user.getEmail());
        needUser.setRealEmail(user.getRealEmail());
        needUser.setPassW(password);

        /*String dep = user.getDepartment();
        if (dep != null)
        {
            needUser.setDepartment(dep);
        }*/
        String duty = user.getDuty();
        if (duty != null)
        {
            needUser.setDuty(duty);
        }
        String image = user.getImage1();
        if (image != null)
        {
            needUser.setImage(image);
        }
        Integer option = user.getMyoption();
        needUser.setMyoption(option);

        needUser.setRole(user.getRole());
        Float size = user.getStorageSize();
        needUser.setStorageSize(size);
        String name = user.getUserName();
        if (name != null)
        {
            needUser.setUserName(name);
        }
        needUser.setSpaceUID(user.getSpaceUID());
        needUser.setRealName(user.getRealName());
        needUser.setImage(WebConfig.userPortrait + user.getImage());   //==null?com.yozo.weboffice.util.beans.Constant.LG_DEFAULT_ICON : user.getImage1());
        needUser.setAddress(user.getAddress());
        needUser.setCompanyName(user.getCompanyName());
        needUser.setFax(user.getFax());
        needUser.setMobile(user.getMobile());
        needUser.setPhone(user.getPhone());
        needUser.setPostcode(user.getPostcode());        
        needUser.setCaId(user.getCaId());
        dataUser.setIntData(Constant.PERMIT_USER);
        dataUser.setUserinfo(needUser);
        return dataUser;
    }
    
    private DataHolder loginCheck(Users user) {	
    	DataHolder dataUser = new DataHolder();

        if (user.getValidate().intValue() == 0) {
            dataUser.setIntData(Constant.NO_PERMIT_USER);
            return dataUser;
        }
        
        String companyID = user.getCompanyId();
        if (companyID == null) {
            dataUser.setIntData(Constant.COMPANY_ERROR);
        }

        Users needUser = new Users();
        needUser.setId(user.getId());
        needUser.setCompanyId(user.getCompanyId());
        //needUser.setEmail(user.getEmail());
        needUser.setRealEmail(user.getRealEmail());
        //needUser.setPassW(password);	//???

        /*String dep = user.getDepartment();
        if (dep != null)
        {
            needUser.setDepartment(dep);
        }*/
        String duty = user.getDuty();
        if (duty != null)
        {
            needUser.setDuty(duty);
        }
        String image = user.getImage1();
        if (image != null)
        {
            needUser.setImage(image);
        }
        Integer option = user.getMyoption();
        needUser.setMyoption(option);
        needUser.setSpaceUID(user.getSpaceUID());

        needUser.setRole(user.getRole());
        Float size = user.getStorageSize();
        needUser.setStorageSize(size);
        String name = user.getUserName();
        if (name != null)
        {
            needUser.setUserName(name);
        }
        needUser.setRealName(user.getRealName());
        needUser.setAddress(user.getAddress());
        needUser.setCompanyName(user.getCompanyName());
        needUser.setFax(user.getFax());
        needUser.setMobile(user.getMobile());
        needUser.setPhone(user.getPhone());
        needUser.setPostcode(user.getPostcode());        
        needUser.setCaId(user.getCaId());
        dataUser.setIntData(Constant.PERMIT_USER);
        dataUser.setUserinfo(needUser);
        return dataUser;
    }
    
    public int loginRepository1(Users user, String password)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		try
		{
			jcrService.login(user.getSpaceUID(), password);
			jcrService.removeUserAllOpenedFile(user.getUserName(), user.getSpaceUID());
			//jcrService.clearUserOpenFile(user.getSpaceUID());
			return Constant.PERMIT_USER;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Constant.TEMP_ERROR;
		}
	}
    
    /**
	 * 联系人添加进组
	 * 
	 * @param linkerID
	 * @param groupID
	 * @return
	 */
    public UsersOrganizations addTogroup(long contactID, long groupID)
    {
        List<UsersOrganizations> l = structureDAO.findUsersOrganizationsByOrgIdAndUserId(groupID, contactID);
        List<Groupshareinfo> list = groupshareinfoDAO.findByGroup(groupID);
        if (l != null && l.size() != 0)
        {
            return l.get(0);
        }
        else
        {
            Organizations groupinfo = structureDAO.findOrganizationsById(groupID);
            Users contact = structureDAO.findUserById(contactID);
            UsersOrganizations groupmemeber = new UsersOrganizations();
            groupmemeber.setUser(contact);
            groupmemeber.setOrganization(groupinfo);
            structureDAO.save(groupmemeber);
            
            Iterator<Groupshareinfo> gsinfo = list.iterator();
            while(gsinfo.hasNext())
            {
            	UsersOrganizations info = structureDAO.findUsersOrganizationsByOrgIdAndUserId(groupID, contactID).get(0);
            	Groupshareinfo gshareinfo = gsinfo.next();
                Groupmembershareinfo gmsinfo = new Groupmembershareinfo();
                gmsinfo.setGroupinfo(gshareinfo.getGroupinfo());
                gmsinfo.setGroupmemberinfo(info);
                gmsinfo.setShareFile(gshareinfo.getShareFile());
                gmsinfo.setShareowner(gshareinfo.getUserinfo().getId());
                gmsinfo.setUserinfo(info.getUser());
				if(gmsinfo.getIsNew() == null)
				{
					gmsinfo.setIsNew(0);
				}
				groupmembershareinfoDAO.save(gmsinfo);
            }
            return groupmemeber;
        }
    }


    /**
     * 用户删除一个组
     * @param gi
     * @return
     */
    public boolean deleteGroup(long groupID)
    {
        structureDAO.deleteOrganizationsByID(groupID);
        return true;
    }

    /**
     * 检查是否含有子组织
     * @param dataholder
     * @return
     */
    public boolean isExitSubGroup(DataHolder dataholder)
    {
        long[] ids = dataholder.getLongData();
        String cond=" where a.parentKey!=null and (a.parentKey like 'aaa' ";
        for (int i = 0; i < ids.length; i++)
        {
        	cond+=" or a.parentKey like '"+ids[i]+"-'";
        }
        cond+=")";
        List list=structureDAO.findAllBySql("select a from Organizations as a "+cond);
        if (list!=null && list.size()>0)
        {
        	return true;//存在子组织
        }
        return false;
    }
    public boolean deleteGroupList(DataHolder dataholder)
    {
        long[] ids = dataholder.getLongData();
        for (int i = 0; i < ids.length; i++)
        {
            deleteGroup(ids[i]);
        }
        return true;
    }

    
    /**
     * 根据当前用户ID得到他所有的组信息

     * @param creator
     * @return
     */
    public List<Organizations> getUserGruopId(long userID)
    {
    	
    	return structureDAO.findOrganizationsByUserId(userID);        
    }
    
    /**
     * 根据父Id得到子组织的
     * @param parentId
     * @return
     */
    public List<Organizations> getChildGroup(long parentId)
    {
    	if (parentId <= 0)
    	{
    		return structureDAO.getChildOrganizations(null);
    	}
    	return structureDAO.getChildOrganizations(parentId);
    }
    public List<Organizations> getChildGroup(long parentId,Users user)
    {
    	if (parentId <= 0)
    	{
    		return structureDAO.getChildOrganizations(null,user);
    	}
    	return structureDAO.getChildOrganizations(parentId);
    }

    /**
     * 根据Id得到组织,得到最顶层的group
     */
    public Organizations getGroup(long groupID)
    {
    	Organizations info = structureDAO.findOrganizationsById(groupID);
    	if (info == null)
    	{
    		return null;
    	}
    	if (info != null && info.getParentID().intValue() == 0)
    	{
    		return info;
    	}
    	else
    	{
    		return getGroup(info.getParentID());
    	}
    }
    
    
    public List<Organizations> getAllGroup()
    {
    	return structureDAO.findAllOrganizations();
    }
    public Organizations createGroups(String name)
    {
    	Organizations parent=null;
    	if (name!=null && name.length()>0)
    	{
	    	String[] values=name.split("/");
	    	String parentCode="";
	    	for (int i=0;i<values.length;i++)
	    	{
	    		Organizations oldlist=structureDAO.findOrganizationsByName(values[i], parent);//查询是否存在
	    		if (oldlist!=null)
	    		{
	    			//已经存在
	    			parent=oldlist;
	    		}
	    		else
	    		{
	    			Organizations grouptemp = new Organizations();
		    		grouptemp.setName(values[i]);
		    		grouptemp.setParent(parent);
		    		grouptemp.setDescription("From out");
		    		String spaceUID = createSpace(FileConstants.ORG_ROOT, values[i], values[i], "", "").getSpaceUID();//组织空间
		    		grouptemp.setSpaceUID(spaceUID);
		    		
		    		if (parent!=null)
	            	{
		            	parentCode=parent.getOrganizecode();
		            	if (parentCode==null || parentCode.length()==0)
		            	{
		            		parentCode=""+parent.getId();
		            		parent.setOrganizecode(parentCode);
		            	}
	            	}
		    		
					
					
		    		structureDAO.save(grouptemp);
		    		String mycode="";
					if (parent == null)
					{
						mycode=""+grouptemp.getId();
					}
					else
					{
						mycode=parentCode+"-"+grouptemp.getId();
					}
					grouptemp.setOrganizecode(mycode);
					structureDAO.update(grouptemp);
		    		parent=grouptemp;
	    		}
	    	}
    	}
        return parent;
    }
    public Organizations getFirstGroup()
    {
    	List<Organizations> list = structureDAO.findAllOrganizations();
    	if (list!=null && list.size()>0)
    	{
    		return list.get(0);
    	}
    	return null;
    }
    /**
     * 修改密码
     * @param email
     * @param oldPassword
     * @param newPassWord
     * @return
     */
    public Users modifyPassword(long userid, String newPassWord)
    {
    	Users user = structureDAO.findUserById(userid);
        MD5 md5 = new MD5();
        newPassWord = md5.getMD5ofStr(newPassWord);
        user.setPassW(newPassWord);
        return user;
    }
    public String[] modifyPassword(long userid,String oldpassw, String newPassWord)
    {
    	Users user = structureDAO.findUserById(userid);
    	MD5 md5 = new MD5();
    	if (oldpassw==null)
    	{
    		oldpassw="";
    	}
    	String mdoldpassw=md5.getMD5ofStr(oldpassw);
    	String userpass = user.getPassW();
    	if (userpass==null)
    	{
    		userpass="";
    	}
    	if ((userpass.equals(mdoldpassw)))
    	{
    		newPassWord = md5.getMD5ofStr(newPassWord);
            user.setPassW(newPassWord);
            structureDAO.update(user);
            return new String[]{"1","成功更新密码"};
    	}
    	else
    	{
    		return new String[]{"0","当前密码输入不正确"};
    	}
    }
    /**
     * 修改名字
     * @param email
     * @param newname
     * @return
     */
    public Users modifyUsernfo(long userid, String newname)
    {
    	Users user = structureDAO.findUserById(userid);
        user.setUserName(newname);
        return user;
    }

    /**
     * 根据当前用户ID得到他所有标签
     * 
     */
    public List<Taginfo> getALLTags(long creatorID)
    {
    	Users userinfo = structureDAO.findUserById(creatorID);
        List<Taginfo> temp = taginfoDAO.findByProperty("userinfo", userinfo);        
        return temp;
    }

    /**
     * 删除组成员

     * @param creatorID
     * @param linkerID
     * @param groupID
     * @return
     */
    public boolean removeMerberInGroup(DataHolder groupmemberIDs)
    {
        if (groupmemberIDs == null)
        {
            return true;
        }
        long[] ids = groupmemberIDs.getLongData();
        if (ids == null)
        {
            return true;
        }
        for (int i = 0; i < ids.length; i++)
        {
            structureDAO.deleteUsersOrganizationsByID(ids[i]);
        }
        return true;
    }

    public boolean modifyMemberInGroup(DataHolder delMemIDS, DataHolder addMemIDS, long groupID)
    {
        removeMerberInGroup(delMemIDS);
        addToGroup(addMemIDS, groupID);
        return true;
    }


    /**
     * 根据groupID 得到当前组内的所有成员
     * @param groupID
     * @return
     */
    public List<UsersOrganizations> getUserInGroup(String[] groupIDs)
    {
    	return structureDAO.findUsersOrganizationsByGroupNames(groupIDs);
    }
    
    /**
     * 根据groupID 得到当前组内的所有成员
     * @param groupID
     * @return
     */
    public List<UsersOrganizations> getUserInGroup(long groupID,Long userId)
    {
    	if (userId != null)
    	{
    		return structureDAO.findUsersOrganizationsByOrgIdAndUserId(groupID, userId);
    	}
    	else
    	{
    		return structureDAO.findUsersOrganizationsByOrgId(groupID);
    	}
    }

    public List<GroupmemberinfoView> getMemberIngGroup(long groupID)
    {
        return groupmemberinfoViewDAO.findByGroupId(groupID);
    }

    /**
     * 批量添加组员
     * @param data
     * @param tag
     * @return
     */
    public List<UsersOrganizations> addToGroup(DataHolder data, long groupid)
    {
        if (data == null)
        {
            return null;
        }
        long[] ids = data.getLongData();
        if (ids == null)
        {
            return null;
        }
        ArrayList<UsersOrganizations> ga = new ArrayList<UsersOrganizations>();
        long[] memberID = data.getLongData();
        if (memberID != null)
        {
            for (int i = 0; i < memberID.length; i++)
            {
                ga.add(addTogroup(memberID[i], groupid));
            }
        }
        return ga;
    }

    private Organizations createOrganization(String name, String des, Organizations parent, Integer sort)
    {
    	Organizations group = new Organizations();
        group.setName(name);
        group.setDescription(des);
        group.setParent(parent);
        group.setSortNum(sort);
        
        String spaceUID = createSpace(FileConstants.ORG_ROOT, name, name, "", "").getSpaceUID();	
        
        group.setSpaceUID(spaceUID);
        structureDAO.save(group);
        return group;
    }
    
    /**
     * 创建组

     * @param creatorID
     * @param groupName
     * @param description
     * @param members
     */
    public Organizations createGroup(long creatorID, long parentId, String groupName, String description,
        List<Long> members)
    {    	
        Organizations group = createOrganization(groupName, description, structureDAO.findOrganizationsById(parentId), new Integer(10000));        
        if (members != null)
        {
            Iterator<Long> it = members.iterator();
            Users tempMember = null;
            while (it.hasNext())
            {
                tempMember = structureDAO.findUserById(it.next());
                UsersOrganizations groupmemberinfo = new UsersOrganizations();
                groupmemberinfo.setOrganization(group);
                groupmemberinfo.setUser(tempMember);
                structureDAO.save(groupmemberinfo);
            }
        }

        return group;
    }

    /**
     * 删除联系人,由于没有设置关联，1.删除user 2.从组中删除 3.从分类中删除
     * @return
     */
    public boolean removeusers(DataHolder userids)
    {
        long[] users = userids.getLongData();
        for (int i = 0; i < users.length; i++)
        {
            structureDAO.deleteUserByID(users[i]);
        }
        return true;
    }
    
    /**
     * 从标签列表中删除一个标签
     */
    public void deleteTag(long tagID)
    {
        taginfoDAO.deleteByID(tagID);
    }
    
    public void deleteTags(DataHolder tagID)
    {
    	long[] longDate = tagID.getLongData();
    	if(longDate != null)
    	{
    		for(int i = 0; i <longDate.length; i++)
    		{
    			deleteTag(longDate[i]);
    		}
    	}
    }

    /**
     * 删除某个文件上的标签
     */
    public void deleteTagfromFile(long tagID, String filePath, String companyID)
    {
        filetaginfoDAO.deleteFiletaginfoByTagIDAndFile(tagID, filePath, companyID);
    }
    
    /**
     * 创建标签
     * @param tagName
     * @param userID
     */
    public void createTags(String[] tagNames, long userID)
    {
    	for(String tagname : tagNames)
    	{	
			Taginfo taginfo = new Taginfo();
            taginfo.setTag(tagname);
            Users userinfo = structureDAO.findUserById(userID);
            taginfo.setUserinfo(userinfo);
            taginfoDAO.save(taginfo);
    	}  
        return;
    }
    /**
     * 判断是否重名
     */
    public String isNameEx(List<String> names, long userId)
    {
    	String isN = null;
    	for(String name : names)
    	{
    		List<Taginfo> taginfoList = taginfoDAO.findByTagAndID(name, userId);
    		if(0 != taginfoList.size())
        	{
        		isN = name;
        		break;
        	}
    	}  	
    	return isN;
    }
    public boolean isNameExOne(String name, long userId)
    {
    	boolean isN = false;
		List<Taginfo> taginfoList = taginfoDAO.findByTagAndID(name, userId);
		if(0 != taginfoList.size())
    	{
    		isN = true;
    	}
    	return isN;
    }
    /**
     * 文件增加标签 在该文件的标签类表增加标签
     * @param name
     * @param userID
     * @param paths
     */
    public boolean addTagFile(List<String> names, long userID, String userName, List<String> paths)
    {
    	boolean isN = false;
    	for(String name : names)
    	{
    		List<Taginfo> taginfoList = taginfoDAO.findByTagAndID(name, userID);
        	//filetaginfoDAO.deleteTags(paths, userID);
        	Taginfo taginfo = taginfoList.get(0);
        	for(int i = 0; i < paths.size(); i++){
        		if(filetaginfoDAO.isFileTags(paths.get(i), name, userID))
        		{
        			isN = true;
        			continue;
        		}
        		Filetaginfo filetaginfo = new Filetaginfo();
                filetaginfo.setFileName(paths.get(i));
                filetaginfo.setTaginfo(taginfo);
                filetaginfo.setCompanyId(userName);
                filetaginfoDAO.save(filetaginfo);
        	}
    	}
		return isN;  		
    }
    
    public void delFileTag(List<String> fileName, Long userId)
    {
		filetaginfoDAO.deleteTags(fileName, userId);	
    }
    
    public void delLitFileTag(String fileName, List<String> tags, String username)
    {
		filetaginfoDAO.deleteLitTags(fileName, tags, username);	
    }
    
    public void delTTag(List tagName, Long userId)
    {
		filetaginfoDAO.deleteTTags(tagName, userId);	
    }
    
    public void remTag(String tagName, String newName, Long userId)
    {
		filetaginfoDAO.updateTags(tagName, newName, userId);	
    }
    /**
     * 在标签列表中插入标签
     */
    public List<Taginfo> createTags(List<String> tagName, long userID)
    {
        List<Taginfo> list = new ArrayList<Taginfo>();
        for (int i = 0; i < tagName.size(); i++)
        {
            Taginfo taginfo = new Taginfo();
            taginfo.setTag(tagName.get(i));
            Users userinfo = structureDAO.findUserById(userID);
            taginfo.setUserinfo(userinfo);
            taginfoDAO.save(taginfo);
            list.add(taginfo);
        }
        return list;
    }

    private Taginfo createTag(String tagName, long userID)
    {
        Taginfo taginfo = new Taginfo();
        taginfo.setTag(tagName);
        Users userinfo = structureDAO.findUserById(userID);
        taginfo.setUserinfo(userinfo);
        taginfoDAO.save(taginfo);
        return taginfo;
    }

    /**
     * 重命名一个标签
     */
    public Taginfo renameTag(long tagID, String newName)
    {
        try
        {
            Taginfo taginfo = taginfoDAO.findById(tagID);
            taginfoDAO.modifyByID(tagID, newName);
            return taginfo;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在标签编辑框中给某个文件设置批量标签
     */
    public List<Taginfo> changeTagFile(DataHolder data, long userID, List<String> filePath,
        String companyID)
    {

        // TODO Auto-generated method stub
        long[] tagID = data.getLongData();
        String[] newName = data.getStringData();
        int oldSize = tagID.length;
        int newSize = newName.length;
        int fileSize = filePath.size();
        List<Taginfo> list = new ArrayList<Taginfo>();
        for (int i = 0; i < oldSize; i++)
        {
            for(int j = 0; j < fileSize; j++)
            {
                filetaginfoDAO.deleteFiletaginfoByTagIDAndFile(tagID[i], filePath.get(j), companyID);
            }
        }
        for (int i = 0; i < newSize; i++)
        {
            List<Taginfo> taginfoList = taginfoDAO.findByTagAndID(newName[i], userID);
            Taginfo taginfo;
            if (taginfoList.size() == 0)
            {
                taginfo = createTag(newName[i], userID);
                list.add(taginfo);
                for(int j = 0; j < fileSize; j++)
                {
                    Filetaginfo filetaginfo = new Filetaginfo();
                    filetaginfo.setFileName(filePath.get(j));
                    filetaginfo.setTaginfo(taginfo);
                    filetaginfo.setCompanyId(companyID);
                    filetaginfoDAO.save(filetaginfo);
                }
            }
            else
            {
                for(int j = 0; j < fileSize; j++)
                {
                    taginfo = taginfoList.get(0);
                    List<Filetaginfo> filetag = filetaginfoDAO.findByTagAndFile(taginfo.getTagId(),
                        filePath.get(j));
                    if (filetag.size() > 0)
                    {
                        continue;
                    }
                    else
                    {
                        Filetaginfo filetaginfo = new Filetaginfo();
                        filetaginfo.setFileName(filePath.get(j));
                        filetaginfo.setTaginfo(taginfo);
                        filetaginfo.setCompanyId(companyID);
                        filetaginfoDAO.save(filetaginfo);
                    }
                }
            }
        }
        return list;

    }
    
    public List<String> getAllTags(long userid)
    {
		return filetaginfoDAO.getAllTags(userid);
    }
    
    public List<String> getFileTags(String filename, String username)
    {
		return filetaginfoDAO.getFileTags(filename, username);
    }

    /**
     * 通过文件得到标签列表
     */
    public List<Filetaginfo> getTagsByFile(List<String> filePath, long userid)
    {
        ArrayList<Filetaginfo> infos = new ArrayList<Filetaginfo>();
        int size = filePath.size();
        for(int i = 0 ; i < size; i++)
        {
        List<Filetaginfo> fileTags = filetaginfoDAO.getTagsByFile(filePath.get(i), userid);
        Hibernate.initialize(fileTags);
        infos.addAll(fileTags);
        }
        return infos;
    }

    public List<Object> getTagFiles(String loginMail, long creatorID, String companyID, int start,
        int limit, String sort, String dir)
    {
        List list = filetaginfoDAO.getTagFiles(loginMail, creatorID, companyID, start, limit, sort,
            dir);
        if (list != null)
        {
            int length = (Integer)list.get(0);
            list.remove(0);
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= length ? length : start + limit);
            }
            list.add(0, length);
        }
        return list;
    }

    public List<Object> getFilesByTag(String loginMail, long tagID, String companyID, int start,
        int limit, String sort, String dir)
    {
        List list = filetaginfoDAO.getFilesByTagID(loginMail, tagID, companyID, start, limit, sort,
            dir);
        if (list != null)
        {
            int length = (Integer)list.get(0);
            list.remove(0);
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= length ? length : start + limit);
            }
            list.add(0, length);
        }
        return list;
    }


    public Users changeOption(long creatorID, int option)
    {
    	Users user = structureDAO.findUserById(creatorID);
        user.setMyoption(option);
        return user;
    }


    

    public void forbidUser(DataHolder userid, short role)
    {
        long[] ids = userid.getLongData();
        if (ids != null && ids.length > 0)
        {
            for (int i = 0; i < ids.length; i++)
            {
            	Users user = structureDAO.findUserById(ids[i]);
                user.setValidate(role);
            }
        }
    }

    public String setConfig(AdminConfig config, long adminID) throws IOException
    {
    	List<License> list = normalDAO.findAll(License.class);
        if (list != null && list.size() > 0)
        {
        	License temp = list.get(0);        
	        String companyID = config.getCompanyID();
	        String email = config.getEmail();
	        String mailSerAddress = config.getMailSerAddress();
	        String password = config.getPassword();
	        if (email != null)
	        {
	        	temp.setMailAddress(email);
	        }
	        else
	        {
	        	temp.setMailAddress("");
	        }
	        if (companyID != null)
	        {
	        	temp.setCompany(companyID);
	        }
	        else
	        {
	        	temp.setCompany("");
	        }
	        if (mailSerAddress != null)
	        {
	        	temp.setMailHost(mailSerAddress);
	        }
	        else
	        {
	        	temp.setMailHost("");
	        }
	        if (password != null)
	        {
	        	temp.setMailPwd(password);
	        }
	        else
	        {
	        	temp.setMailPwd("");
	        }
	        normalDAO.update(temp);
        }
        return "";
    }
    
    public AdminConfig getConfig()
    {
        AdminConfig config = new AdminConfig();
        List<License> list = normalDAO.findAll(License.class);
        if (list != null && list.size() > 0)
        {
        	License temp = list.get(0);
        	config.setCompanyID(temp.getCompany());
        	config.setMailSerAddress(temp.getMailHost());
        	config.setEmail(temp.getMailAddress());
        	config.setPassword(temp.getMailPwd());
        }
        return config;
    }

    
    /**
     * 得到组内的用户
     */
    public List<String> getDistinctGM(List<Long> groupIds)
    {
        Long[] ids = groupIds.toArray(new Long[0]);
        return structureDAO.getUsersMailByOrganizationsId(ids);
    }
    
    @SuppressWarnings("unchecked")
	public DataHolder getAllUserinfoView(long userid, int index, int persize) 
	{
        DataHolder list = null;
        //if(version == 0)
        {
            list = userinfoViewDAO.getlimited(index, persize);
        }
       
        return list;
	}
    

	public DataHolder getLimitedAdminUser(int index, int length,String sort,String dir) 
	{
		DataHolder listHolder = adminUserinfoViewDAO.getlimitedUser(index, length,sort,dir);
 //       Collections.sort(list/*, new ContacterArrayComparator("name", 1)*/);
        return listHolder;
        
	}

	public DataHolder getSearchUser(int option, String keyWord,Users users, int index,
			int length, String sort, String dir) 
	{
		DataHolder listHolder = adminUserinfoViewDAO.getSearchUser(option, keyWord,users, index, length, sort, dir);
		return listHolder;
	}
	/**
	 * 文件发送功能中，可以按姓名查找，上一个方法是以用户名查找
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
        DataHolder listHolder = adminUserinfoViewDAO.getSearchUser2(option, keyWord, index, length, sort, dir);
        return listHolder;
    }
		
	public boolean isLdapRequrie()
	{	
		return ldapRequrie;
	}

	
	public void setLdapRequrie(boolean ldapRequrie)
	{	
		this.ldapRequrie = ldapRequrie;
	}
	
	//user663
	public DataHolder getSearchUserinfoView(long userid,int option,String keyWord, int index,  int length, String sort, String dir)
	{
		DataHolder dh;
//		version = 0;
		//if(version == 0)
		{
			dh = userinfoViewDAO.getSearchUserinfoView(option, keyWord, index, length, sort, dir);
		}
		
		return dh;
	}	
    
    public boolean updataUserinfo(Users userinfo)
    {
    	try
    	{
    		structureDAO.update(userinfo);
    		return true;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    
    
    /**
     * 从数据库中得某个用户信息
     * @param userId
     * @return
     */
    public Users getUser(long userId)
    {
    	return structureDAO.findUserById(userId);
    }

    public Users getUserByEmail(String email)
    {    	
    	List<Users> list = structureDAO.searchUserByEmail(email);
    	if (list == null || list.size() < 1)
    	{
    		return null;
    	}
    	return list.get(0);
    }
    
    public Users getUserByMobile(String mobile){
    	List<Users> list = structureDAO.searchUserByMobile(mobile);
    	if(list == null || list.size() <1 ){
    		return null;
    	}
    	return list.get(0);
    }
    
    /**
     * 根据角色id获得角色对象。
     * @param id
     * @return
     */
    public Roles findRoleById(Long id)
    {
    	return structureDAO.findRoleById(id);
    }
    
    /**
     * 从数据库中得某个用户信息
     * @param userId
     * @return
     */
    public Users getUser(String userName)
    {
    	return structureDAO.isExistUser(userName);
    	/*List<Users> list = structureDAO.searchUserByContain("userName", userName);
    	if (list == null || list.size() < 1)
    	{
    		return null;
    	}
    	Users info = list.get(0);
    	return info;*/
    }
    public Users getUserByUserName1(String userName){
    	return structureDAO.isExistUsername1(userName);
    }
    
   public Users getUserByUserName2(String userName){
	   return structureDAO.isExistUsername2(userName);
    }
	public UserWorkadayDAO getUserWorkadayDao()
	{
		return userWorkadayDao;
	}

	public void setUserWorkadayDao(UserWorkadayDAO userWorkadayDao)
	{
		this.userWorkadayDao = userWorkadayDao;
	}
	
	/**
	 * 通过被共享的组的ID找到共享者
	 */
	public List<Groupshareinfo> findByGroupId(java.lang.Long id)
	{
		return groupshareinfoDAO.findByGroupId(id);
	}
		
	public Users getUserByCaId(String caId) {
		List list = structureDAO.findUserByProperty("caId", caId);
        if (list !=null && list.size() > 0) {
            return (Users) list.get(0);
        }
		return null;
	}	
	
	public Groups findGroupsBySpaceUID(String spaceUID)
	{
		return structureDAO.findGroupsBySpaceUID(spaceUID);
	}
	
	public CustomTeams findTeamBySpaceUID(String spaceUID)
	{
		return structureDAO.findTeamBySpaceUID(spaceUID);
	}
	
	/**
	 * 增加新的角色，如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * @param role 角色内容
	 * @param groupId， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * 
	 */
	public String addRole(Roles role, long groupId, long orgId,
			long teamId)
	{

		if (groupId != -1)
		{
			List<Roles> list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? a.group.id=? ", role.getRoleName(), groupId);
			if (list != null && list.size() > 0)
			{
				return "角色重名了！";
			}
			Groups g = (Groups) structureDAO.find(Groups.class, groupId);
			role.setGroup(g);
			structureDAO.save(role);
		}
		else if (orgId != -1)
		{
			List<Roles> list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? a.organization.id=? ", role.getRoleName(), orgId);
			if (list != null && list.size() > 0)
			{
				return "角色重名了！";
			}
			Organizations o = (Organizations) structureDAO.find(Organizations.class, orgId);
			role.setOrganization(o);
			structureDAO.save(role);
		}
		else if (teamId != -1)
		{
			List<Roles> list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? and a.team.id=? ", role.getRoleName(), teamId);
			if (list != null && list.size() > 0)
			{
				return "角色重名了！";
			}
			CustomTeams c = (CustomTeams) structureDAO.find(CustomTeams.class,	teamId);
			role.setTeam(c);
			structureDAO.save(role);
		}
		else
		{
			List<Roles> list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? ", role.getRoleName());
			if (list != null && list.size() > 0)
			{
				return "角色重名了！";
			}
			structureDAO.save(role);
		}
		return null;
	}
	
	/**
	 * 增加角色
	 * @param role
	 * @param companyId
	 * @param groupId
	 * @param orgId
	 * @param teamId
	 * @return
	 */
	public String addRole(Roles role, long companyId, long groupId, long orgId, long teamId)
	{
		if (companyId != -1)
		{
			Roles list = structureDAO.getCompanyTemplateRole(companyId, role.getRoleName(), role.getType());
			if (list != null)
			{
				return "角色重名了！";
			}
			Company c = (Company)structureDAO.find(Company.class, companyId);
			role.setCompany(c);
			structureDAO.save(role);
		}
		else if (groupId != -1)
		{
			Roles list = structureDAO.getGroupRole(groupId, role.getRoleName());
			if (list != null)
			{
				return "角色重名了！";
			}
			Groups g = (Groups) structureDAO.find(Groups.class, groupId);
			role.setGroup(g);
			structureDAO.save(role);
		}
		else if (orgId != -1)
		{
			Roles list = structureDAO.getOrgRole(orgId, role.getRoleName());
			if (list != null)
			{
				return "角色重名了！";
			}
			Organizations o = (Organizations) structureDAO.find(Organizations.class, orgId);
			role.setOrganization(o);
			structureDAO.save(role);
		}
		else if (teamId != -1)
		{
			Roles list = structureDAO.getTeamRole(teamId, role.getRoleName());
			if (list != null)
			{
				return "角色重名了！";
			}
			CustomTeams c = (CustomTeams) structureDAO.find(CustomTeams.class,teamId);
			role.setTeam(c);
			structureDAO.save(role);
		}
		else
		{
			Roles list = structureDAO.getGlobalRoles(role.getRoleName(), role.getType());
			if (list != null)
			{
				return "角色重名了！";
			}
			structureDAO.save(role);
		}
		return null;
	}
	
	/**
	 * 修改角色的属性
	 * @param role
	 */
	@Deprecated
	public String updateRole(long roleId, Roles role)
	{
		List list = null;
		if(role.getTeam() != null)
		{
			list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? and a.team=? and a.id !=?", role.getRoleName(),role.getTeam(),roleId);
		}
		else if(role.getGroup() != null)
		{
			list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? and a.group=? and a.id !=?", role.getRoleName(),role.getGroup(),roleId);
		}
		else if(role.getOrganization() != null)
		{
			list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? and a.organization=? and a.id !=?", role.getRoleName(),role.getOrganization(),roleId);
		}
		else
		{
			list = normalDAO.findAllBySql("select a from Roles as a where a.roleName=? and a.id !=? and a.group is null and a.team is null and a.organization is null", role.getRoleName(),roleId);
		}
		if (list!=null && list.size()>0)
		{
			return "角色重名了！";
		}
		else
		{
			Roles r = (Roles)structureDAO.find(Roles.class, roleId);
			if (r != null && role != null)
			{
				r.update(role);
				structureDAO.update(r);
			}
		}
		return null;
	}
	
	/**
	 * 增加角色
	 * @param role
	 * @param companyId
	 * @param groupId
	 * @param orgId
	 * @param teamId
	 * @return
	 */
	public String updateRole(Roles role, long roleId, long companyId, long groupId, long orgId, long teamId)
	{
		Roles r = (Roles)structureDAO.find(Roles.class, roleId);
		Roles list;
		if (companyId != -1)
		{
			list = structureDAO.getCompanyTemplateRole(companyId, role.getRoleName(), role.getType());
		}
		else if (groupId != -1)
		{
			list = structureDAO.getGroupRole(groupId, role.getRoleName());			
		}
		else if (orgId != -1)
		{
			list = structureDAO.getOrgRole(orgId, role.getRoleName());
		}
		else if (teamId != -1)
		{
			list = structureDAO.getTeamRole(teamId, role.getRoleName());
		}
		else
		{
			list = structureDAO.getGlobalRoles(role.getRoleName(), role.getType());
		}
		if (list != null && list.getId().longValue() != r.getId().longValue())
		{
			return "角色重名了！";
		}
		r.update(role);
		structureDAO.update(r);
		return null;
	}
	
	/**
	 * 删除公司管理的角色
	 * @param companyId
	 * @param ids
	 */
	public void deleteRole(long companyId, List<Long> ids)
	{
		structureDAO.deleteTemplateRoleById(companyId, ids);
	}
	
	/**
	 * 删除角色
	 * @param roleId
	 */
	public void deleteRole(List<Long> roleId)
	{
		if (roleId != null && roleId.size() > 0)
		{
			structureDAO.deleteEntityByID(Roles.class, "id",  roleId);
		}
	}
	
	/**
	 * 获得的角色定义
	 * 如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * @param pu， 是否获取全局角色定义
	 * @param isTeamp，在获取全局的角色定义的时候，是否是获取模板角色定义
	 * @param groupId， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * @return
	 */
	@Deprecated
	public List<Roles> getRoles(boolean pu, boolean isTemp, long groupId, long orgId, long teamId)
	{
		if (groupId != -1)
		{
			return structureDAO.getGroupRoles(groupId);
		}
		else if (orgId != -1)
		{
			return structureDAO.getOrgRoles(orgId);
		}
		else if (teamId != -1)
		{
			return structureDAO.getTeamRoles(teamId);
		}
		return structureDAO.getGlobalRoles(isTemp ? RoleCons.SPACE : RoleCons.SYSTEM);
	}
	
	/**
	 * 获得的角色定义
	 * 如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * @param pu， 是否获取全局角色定义
	 * @param groupId， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * @return
	 */
	public List<Roles> getRoles(long companyId, long groupId, long orgId, long teamId)
	{
		if (companyId != -1)
		{
			return structureDAO.getCompanyRoles(companyId);
		}
		else if (groupId != -1)
		{
			return structureDAO.getGroupRoles(groupId);
		}
		else if (orgId != -1)
		{
			return structureDAO.getOrgRoles(orgId);
		}
		else if (teamId != -1)
		{
			return structureDAO.getTeamRoles(teamId);
		}
		return null;
	}
	
	/**
	 * type值0表示获取系统角色，1表示获取空间角色，其他值为表示既获取系统角色，也获取空间角色
	 * @param companyId
	 * @param type
	 * @return
	 */
	public List<Roles> getCompanyRoles(Long companyId, int type)
	{
		return structureDAO.getCompanyRoles(companyId, type);
	}
	
	/**
	 * 获取用户当前的系统角色
	 * @param isTemp
	 * @return
	 */
	@Deprecated
	public List<Roles> getGlobalRolesByUserId(Long userId)
	{
		return structureDAO.getGlobalRolesByUserId(userId);
	}
	@Deprecated
	public List<Roles> getGlobalRolesByUserId(Long userId, Long companyId)
	{
		return structureDAO.getGlobalRolesByUserId(userId, companyId);
	}
	/**
	 * 获得的角色定义
	 * 如果groupId、orgId和teamId均为-1值，在表示该角色是属于系统全局性的角色
	 * @param groupId， 该角色属于的组Id，如果该角色不属于组，则该id值为-1；
	 * @param orgId ，该角色属于的组织Id，如果该角色不属于组织，则该id值为-1；
	 * @param teamId， 该角色属于的用户自定义组，如果该角色不属于该teamId，则该id值为-1；
	 * @return
	 */
	public List<Roles> getRoles(long groupId, long orgId, long teamId)
	{
		if (groupId != -1)
		{
			return structureDAO.getGroupRoles(groupId);
		}
		else if (orgId != -1)
		{
			return structureDAO.getOrgRoles(orgId);
		}
		else if (teamId != -1)
		{
			return structureDAO.getTeamRoles(teamId);
		}
		return structureDAO.getGlobalRoles();
	}
	
	
	/**
	 * 给角色分配用户
	 * @param roleId
	 * @param userIds
	 */
	public void addUsersRoles(long roleId, long[] userIds)
	{
		Roles role = (Roles)structureDAO.find(Roles.class, roleId);
		for (long id : userIds)
		{
			Users u = (Users)structureDAO.find(Users.class, id);
			UsersRoles ur = new UsersRoles(u, role);
			structureDAO.save(ur);
		}		
	}
	
	/**
	 * 
	 * @param roleId
	 * @param addIds
	 * @param delIds
	 */
	public void updateUserRoles(long roleId, long[] addIds, long[] delIds)
	{
		if (addIds != null)
		{
			addUsersRoles(roleId, addIds);
		}
		if (delIds != null)
		{
			deleteRolesUsers(roleId, delIds);
		}
	}
	
	/**
	 * 参见空间
	 * @param root
	 * @param name
	 * @return
	 */
	private Spaces createSpace(String root, String name, String spaceName, String spaceDes, String status)
	{
		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
        String spaceUID = jcrService.createSpace(root + name);
        Spaces space = new Spaces(spaceUID, spaceName, spaceDes, spaceUID, status);
		structureDAO.save(space);
		return space;
	}
	/**
	 * 获得某个角色中的用户成员
	 * @param roleId
	 * @return
	 */
	public List<Users> getUsersByRoleId(long roleId)
	{
		return structureDAO.findUsersByRoleId(roleId);
	}
	
	/**
	 * 删除某个角色中的成员
	 * @param roleId
	 * @param userIds
	 */
	public void deleteRolesUsers(long roleId, long[] userIds)
	{
		ArrayList<Long> uids = new ArrayList<Long>();
		for (long t : userIds)
		{
			uids.add(t);
		}
		if (uids.size() > 0)
		{
			structureDAO.deleteRolesUsers(roleId, uids);
		}
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgIds
	 * @param treeFlag
	 * @return
	 */
	public List<Users> getUsersByOrgId(List<Long> orgIds, boolean treeFlag)
	{
		List<Users> ret = new ArrayList<Users>();
		for (Long temp : orgIds)
		{
			ret.addAll(structureDAO.findUsersByOrgId(temp, treeFlag));
		}
		return ret;
	}
	
	/**
	 * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgIds
	 * @param treeFlag
	 * @return
	 */
	public List<AdminUserinfoView> getUserViewByOrgId(List<Long> orgIds, boolean treeFlag)
	{
		List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
		for (Long temp : orgIds)
		{
			ret.addAll(structureDAO.findUserViewByOrgId(temp, treeFlag));
		}
		return ret;
	}
	
	/**
	 * 根据组Id得到组中的所有用户，如果treeFlag为true则递归查询组中所有子组中
	 * 的用户，为false，则值查询该级组中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	public List<AdminUserinfoView> getUserViewByGroupId(List<Long> groupIds, boolean treeFlag)
	{
		return getUserViewByGroupId(groupIds, treeFlag, null);
	}
	
	public List<AdminUserinfoView> getUserViewByGroupId(List<Long> groupIds, boolean treeFlag,String search)
	{
		List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
		for (Long temp : groupIds)
		{
			if(search == null)
			{
				ret.addAll(structureDAO.findUserViewByGroupId(temp, treeFlag));
			}
			else
			{
				ret.addAll(structureDAO.findUserViewByGroupId(temp, treeFlag,search));
			}
		}
		return ret;
	}
	
	/**
	 * 根据组Id得到用户自定义组中的所有用户。
	 * @param teamId
	 * @return
	 */
	public List<AdminUserinfoView> getUserViewByTeamId(Long teamId)
	{
		return structureDAO.findUserViewByTeamId(teamId);
	}
	
	/**
	 * 根据组Id得到用户自定义组中的所有用户。
	 * @param teamIds
	 * @return
	 */
	public List<AdminUserinfoView> getUserViewByTeamId(List<Long> teamIds)
	{
		List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
		for (Long temp : teamIds)
		{
			ret.addAll(structureDAO.findUserViewByTeamId(temp));
		}
		return ret;
	}
	/**
	 * 根据组Id得到组中的所有用户，如果treeFlag为true则递归查询组中所有子组中
	 * 的用户，为false，则值查询该级组中的用户。
	 * @param orgId
	 * @param treeFlag
	 * @return
	 */
	public List<Users> getUsersByGroupId(List<Long> groupIds, boolean treeFlag)
	{
		List<Users> ret = new ArrayList<Users>();
		for (Long temp : groupIds)
		{
			ret.addAll(structureDAO.findUsersByGroupId(temp, treeFlag));
		}
		return ret;
	}
	
	/**
	 * 根据组Id得到用户自定义组中的所有用户。
	 * @param orgId
	 * @return
	 */
	public List<Users> getUsersByTeamId(List<Long> teamIds)
	{
		List<Users> ret = new ArrayList<Users>();
		for (Long temp : teamIds)
		{
			ret.addAll(structureDAO.findUsersByTeamId(temp));
		}
		return ret;
	}
	
	/**
	 * 根据组Id得到用户自定义组中的所有用户。
	 * @param teamId
	 * @return
	 */
	public List<Users> getUsersByTeamId(Long teamId)
	{
		return structureDAO.findUsersByTeamId(teamId);
	}
	
	/**
	 * 获取用户的头像图片名。 
	 * @param userId
	 * @return 如果用户不存在或者用户没有自定义头像，则返回值为默认值
	 */
	public String getUserPortraitName(Long userId)
	{
		String ret = structureDAO.getUserPortraitName(userId);
		return ret == null ? "image.jpg" : ret;
	}
	
	/**
	 * 获取用户的头像图片名。 
	 * @param userId
	 * @return 如果用户不存在或者用户没有自定义头像，则返回值为默认值
	 */
	public String getUserPortraitURL(Long userId)
	{
		String ret = structureDAO.getUserPortraitName(userId);
		ret =  WebConfig.userPortrait + (ret == null ? "image.jpg" : ret);   // 地址先写固定的，后续根据配置获取。
		return ret;
	}
	
	/**
	 * 设置用户头像。
	 * 通过http协议，进行标准的form表单方式进行头像图片上传处理。 
	 * @param userId 用户Id值，如果该用户还没有建立，则该值传null。
	 * @param request
	 * @return 返回用户头像在系统中的名字。
	 */
	public String addOrUpdateUserPortrait(Long userId, String path, HttpServletRequest request)
	{
		Users user = null;		
		String fileName = null;
		boolean uFlag = false;
		if (userId != null)
		{
			user = structureDAO.findUserById(userId);
			if (user != null)
			{
				fileName = user.getImage1();
				if (fileName == null)
				{
					uFlag = true;
				}
			}
		}
		if (fileName == null ||  "image.jpg".equals(fileName) 
				|| fileName.length()==0 || fileName.toLowerCase().equals("null")
				|| fileName.toLowerCase().equals("undefined"))
		{
			fileName = System.currentTimeMillis() + "_.jpg";
		}
		
		List<String> ret = FilesHandler.fileUploadByHttpForm(request, path, fileName);
		if (ret != null && ret.size() > 0)
		{
			if (user != null && uFlag)
			{
				user.setImage(fileName);
				structureDAO.update(user);
				//自己维护的关系表同时进行更新
				structureDAO.sycnUpdateImg(user.getId(),fileName);
			}
			return fileName;
		}
		else
		{
			return null;
		}
	}
	public String addOrUpdateUserPortraitbyCamera(Long userId, String path, HttpServletRequest request) throws IOException
	{
		Users user = null;		
		String fileName = null;
		boolean uFlag = false;
		if (userId != null)
		{
			user = structureDAO.findUserById(userId);
			if (user != null)
			{
				fileName = user.getImage1();
				if (fileName == null)
				{
					uFlag = true;
				}
			}
		}
		if (fileName == null ||  "image.jpg".equals(fileName) 
				|| fileName.length()==0 || fileName.toLowerCase().equals("null")
				|| fileName.toLowerCase().equals("undefined"))
		{
			fileName = System.currentTimeMillis() + "_.jpg";
		}
		InputStream is = request.getInputStream();
		File file = new File(path + File.separatorChar + fileName);
		OutputStream os = new FileOutputStream(file);
		int tt = is.read();
		while(tt != -1) {
		    os.write(tt);
		    tt = is.read();
		}
		os.flush();
		is.close();
		os.close();
		if (file.exists())
		{
//			String ftpurl = WebConfig.userPortrait.substring(6,WebConfig.userPortrait.indexOf("personalset2")-1);
//	    	UserOnlineHandler.uploadToFTPserver(file,ftpurl,"personalset2");
//			file.delete();
			if (user != null && uFlag)
			{
				user.setImage(fileName);
				structureDAO.update(user);
			}
			return fileName;
		}
		else
		{
			return null;
		}
	}
	/**
	 * 获取组的头像图片名。 
	 * @param groupId
	 * @return 如果组不存在或者组没有自定义头像，则返回值为默认值
	 */
	public String getGroupPortraitName(Long groupId)
	{
		String ret = structureDAO.getGroupPortraitName(groupId);
		return ret == null ? "image.jpg" : ret;
	}
	
	/**
	 * 获取组的头像图片名。 
	 * @param groupId
	 * @return 如果组不存在或者组没有自定义头像，则返回值为默认值
	 */
	public String getGroupPortraitURL(Long groupId)
	{
		String ret = structureDAO.getGroupPortraitName(groupId);
		ret =  WebConfig.groupPortrait + (ret == null ? "image.jpg" : ret);   // 地址先写固定的，后续根据配置获取。
		return ret;
	}
	
	/**
	 * 设置组头像。
	 * 通过http协议，进行标准的form表单方式进行头像图片上传处理。 
	 * @param groupId 组Id值，如果该组还没有建立，则该值传null。
	 * @param request
	 * @return 返回组头像在系统中的名字。
	 */
	public String addOrUpdateGroupPortrait(Long groupId, String path, HttpServletRequest request)
	{
		Groups group = null;		
		String fileName = null;
		boolean uFlag = false;
		if (groupId != null)
		{
			group = structureDAO.findGroupById(groupId);
			if (group != null)
			{
				fileName = group.getImage1();
				if (fileName == null)
				{
					uFlag = true;
				}
			}
		}
		if (fileName == null ||  "image.jpg".equals(fileName) 
				|| fileName.length()==0 || fileName.toLowerCase().equals("null")
				|| fileName.toLowerCase().equals("undefined"))
		{
			fileName = System.currentTimeMillis() + "_.jpg";
		}
		
		List<String> ret = FilesHandler.fileUploadByHttpForm(request, path, fileName);
		if (ret != null && ret.size() > 0)
		{
			if (group != null && uFlag)
			{
				group.setImage(fileName);
				structureDAO.update(group);
			}
			return fileName;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 获取组织的头像图片名。 
	 * @param orgId
	 * @return 如果组织不存在或者组织没有自定义头像，则返回值为默认值
	 */
	public String getOrgPortraitName(Long orgId)
	{
		String ret = structureDAO.getOrgPortraitName(orgId);
		return ret == null ? "image.jpg" : ret;
	}
	
	/**
	 * 获取组织的头像图片名。 
	 * @param orgId
	 * @return 如果组织不存在或者组织没有自定义头像，则返回值为默认值
	 */
	public String getOrgPortraitURL(Long groupId)
	{
		String ret = structureDAO.getOrgPortraitName(groupId);
		ret =  WebConfig.orgPortrait + (ret == null ? "image.jpg" : ret);   // 地址先写固定的，后续根据配置获取。
		return ret;
	}
	
	/**
	 * 设置组织头像。
	 * 通过http协议，进行标准的form表单方式进行头像图片上传处理。 
	 * @param orgId 组织Id值，如果该组织还没有建立，则该值传null。
	 * @param request
	 * @return 返回组织头像在系统中的名字。
	 */
	public String addOrUpdateOrgPortrait(Long orgId, String path, HttpServletRequest request)
	{
		Organizations org = null;		
		String fileName = null;
		boolean uFlag = false;
		if (orgId != null)
		{
			org = structureDAO.findOrganizationsById(orgId);
			if (org != null)
			{
				fileName = org.getImage1();
				if (fileName == null)
				{
					uFlag = true;
				}
			}
		}
		if (fileName == null ||  "image.jpg".equals(fileName) 
				|| fileName.length()==0 || fileName.toLowerCase().equals("null")
				|| fileName.toLowerCase().equals("undefined"))
		{
			fileName = System.currentTimeMillis() + "_.jpg";
		}
		
		List<String> ret = FilesHandler.fileUploadByHttpForm(request, path, fileName);
		if (ret != null && ret.size() > 0)
		{
			if (org != null && uFlag)
			{
				org.setImage(fileName);
				structureDAO.update(org);
			}
			return fileName;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 得到user所在的组织，如果用户在多个组织中，则返回多个组织对象。
	 * @param userId
	 * @return
	 */
	public List<Organizations> getOrganizationByUsers(long userId)
	{
		return structureDAO.findOrganizationsByUserId(userId);
	}
	
	/**
	 * 得到user所在的组织，如果用户在多个组织中，则返回多个组织对象。
	 * @param userId
	 * @return
	 */
	public List<Organizations> findOrganizationsByUserName(String userName)
	{
		return structureDAO.findOrganizationsByUserName(userName);
	}
		
	/**
	 * 修改用户所在组织
	 * addOrgIds为添加用户在组织中，
	 * delOrgId为删除用户在组织中。
	 * @param userId
	 * @param addOrgIds
	 * @param delOrgIds
	 */
	public void addOrUpdateUserInOrg(Long userId, List<Long> addOrgIds, List<Long> delOrgIds)
	{
		Users user = structureDAO.findUserById(userId);
		if (user != null)
		{
			if (delOrgIds != null && delOrgIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(userId);
				structureDAO.deleteUsersOrganizationsByOrgId(delOrgIds, ids);
			}
			if (addOrgIds != null && addOrgIds.size() > 0)
			{
				for (Long org : addOrgIds)
				{
					Organizations or = structureDAO.findOrganizationsById(org);
					UsersOrganizations uog = new UsersOrganizations(user, or);
					structureDAO.save(uog);				
				}
			}
		}
	}
	
	/**
	 * 增加或修改用户的信息，user对象为修改用户的信息内容，addOrgIds为添加用户在组织中，
	 * delOrgId为删除用户在组织中。newRoleId为用户新的角色Id值，oldRoleId为用户旧的角色Id值。
	 * @param user
	 * @param addOrgIds
	 * @param delOrgIds
	 * @param newRoleId
	 * @param oldRoleId
	 */
	@Deprecated
	public Users addOrUpdateUser(Users user, List<Long> addOrgIds, List<Long> delOrgIds, Long newRoleId, Long oldRoleId)
	{
		Long userId = user.getId();
		Users entity;
		if (userId == null)
		{
			String p = user.getResetPass();
			user.setRealPass(p);
			if (p != null && p.length() > 0)
			{
				MD5 md5 = new MD5();
				p = md5.getMD5ofStr(p);
				user.setPassW(p);
			}
			
			String spaceUID = createSpace(FileConstants.USER_ROOT, user.getUserName(), user.getUserName(), "", "").getSpaceUID();			
            user.setSpaceUID(spaceUID);
            //加入用户的公钥与私钥
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
            if (newRoleId!=null && newRoleId.intValue()==1)
            {
            	user.setRole((short)1);
            	user.setPartadmin(2);
            }
			structureDAO.save(user);
			
			if (newRoleId != null)
			{
				Roles role = structureDAO.findRoleById(newRoleId);
				UsersRoles ur = new UsersRoles(user, role);
				structureDAO.save(ur);
			}
						
			userId = user.getId();
			entity = user;
		}
		else
		{
			entity = structureDAO.findUserById(userId);
			String p = user.getResetPass();
			user.setRealPass(p);
			if (p != null && p.length() > 0)
			{
				MD5 md5 = new MD5();
				p = md5.getMD5ofStr(p);
				entity.setPassW(p);
			}
			entity.update(user);
			structureDAO.update(entity);
			
			if (!((newRoleId == null && oldRoleId == null) 
					|| (newRoleId != null && oldRoleId != null && newRoleId.longValue() == oldRoleId.longValue()))) 
			{
				if (oldRoleId != null)
				{
					UsersRoles ur = structureDAO.findUserRole(userId,oldRoleId);
					structureDAO.delete(ur);
				}
				if (newRoleId != null)
				{
					Roles role = structureDAO.findRoleById(newRoleId);
					UsersRoles ur = new UsersRoles(user, role);
					structureDAO.save(ur);
				}
			}
			
		}
		if(newRoleId!=null && newRoleId==2&&userId!=null)// 如果是空间管理员 添加接待权限
		{
			serReceptionPower(userId);
		}
		if (delOrgIds != null && delOrgIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(userId);
			structureDAO.deleteUsersOrganizationsByOrgId(delOrgIds, ids);
		}
		if (addOrgIds != null && addOrgIds.size() > 0)
		{
			for (Long org : addOrgIds)
			{
				Organizations or = structureDAO.findOrganizationsById(org);
				UsersOrganizations uog = new UsersOrganizations(entity, or);
				structureDAO.save(uog);				
			}
		}
		return user;
	}
	
	/**
	 * 删除用户
	 * @param userIds
	 */
	public void deleteUsers(List<Long> userIds)
	{
		if (userIds.size() > 0)
		{
			structureDAO.deleteUserByID(userIds);
		}
		// 还需处理用户文件库的删除。
	}
	
	/**
	 * 给组织中增加成员或删除成员
	 * @param orgId
	 * @param userIds
	 */
	public void addOrDeleteOrganizationMembers(long orgId, List<Long> addUserIds, List<Long> delUserIds)	
	{
		Organizations org = structureDAO.findOrganizationsById(orgId);
		if (org != null)
		{	
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(org.getId());
				structureDAO.deleteUsersOrganizationsByOrgId(ids, delUserIds);
			}
			if (addUserIds != null && addUserIds.size() > 0)
			{
				for (Long uid : addUserIds)
				{
					Users or = structureDAO.findUserById(uid);
					UsersOrganizations uog = new UsersOrganizations(or, org);
					structureDAO.save(uog);				
				}
			}
		}
	}
	public  void createGroups(String groupname,Users user,Spaces space)
	{
		Groups groups = new Groups();
        groups.setName(groupname);
        groups.setDescription("用户注册时自动创建");
        groups.setManager(user);
        if (space==null)
        {
        	space = new Spaces();
            space.setName("weboffice");
            space.setDescription("weboffice depart");
        }
        Spaces retSpace = createSpace(FileConstants.GROUP_ROOT, groups.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
        groups.setSpaceUID(retSpace.getSpaceUID());
        groups.setManager(user);
		structureDAO.save(groups);
		Long groupId = groups.getId();
		permissionDAO.copySystemRoleActionToGroup(groupId);
		
		UsersGroups uog = new UsersGroups(user, groups);
		structureDAO.save(uog);			
				
	}
	
	/**
	 * 新建或修改公司信息
	 * @param company 如果id为null表示新建了的公司，否则为修改已有公司信息
	 * @param space 公司的空间，如果在修改公司信息的时候，不需要修改空间的属性，则该值传null。新建公司的时候，该值也可以为null。
	 */
	public Spaces addOrUpdateCompany(Company company, Spaces space)
	{
		Long comId = company.getId();
		Spaces retSpace; 
		if (comId == null)    // 新建了的公司
		{
			if (space == null)
			{
				retSpace = createSpace(FileConstants.COMPANY_ROOT, company.getName(), company.getName(), company.getDescription(), "");
			}
			else
			{
				retSpace = createSpace(FileConstants.COMPANY_ROOT, company.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
			}
	        company.setSpaceUID(retSpace.getSpaceUID());			
			structureDAO.save(company);	
			permissionDAO.copySystemRoleActionToCompany(company.getId());
		}
		else
		{
			Company comUpdate = structureDAO.findCompanyById(comId);
			comUpdate.update(company);
			structureDAO.update(comUpdate);
			if (space != null)
			{
				retSpace = structureDAO.findSpaceByUID(comUpdate.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}
			retSpace = space;
		}
		return retSpace;
	}
	
	
	/**
	 * 新建或修改组织
	 * @param parentId 新的父组织，没有为null
	 * @param org 新增或修改的组织
	 * @param addUserIds 在组织中的新增成员
	 * @param roleIds 组成员对应的role的角色id值，如果没有角色，则赋值为null，该数组的下标需要同
	 * addUserIds的下标对应，即是addUserIds[index]和roleIds[index]是同一个用户的角色。
	 * @param delUserIds 删除组织中的成员
	 * @param leaderId 组织的负责人
	 * @param space 组织的空间，如果在修改组织的时候，不需要修改空间的属性，则该值传null。
	 */
	@Deprecated
	public Spaces addOrUpdateOrganization(Long parentId, Organizations org,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds, Long leaderId, Spaces space)
	{
		Long orgId = org.getId();
		Organizations organization;
		Users leader = null;
		if (space==null)
		{
			space = new Spaces();
            space.setName("weboffice");
            space.setDescription("weboffice depart");
		}
		Spaces retSpace = space;
		if (leaderId != null)
        {
        	leader = structureDAO.findUserById(leaderId); 
        }
		String parentCode="000";
		if (orgId == null)
		{
			retSpace = createSpace(FileConstants.ORG_ROOT, org.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
            org.setSpaceUID(retSpace.getSpaceUID());
            if (parentId != null)
            {
            	Organizations parent = structureDAO.findOrganizationsById(parentId);
            	org.setParent(parent);
            	if (parent!=null)
            	{
	            	parentCode=parent.getOrganizecode();
	            	if (parentCode==null || parentCode.length()==0)
	            	{
	            		parentCode=""+parent.getId();
	            		parent.setOrganizecode(parentCode);
	            	}
            	}
            }
            
            org.setManager(leader);
			structureDAO.save(org);
			//更改组织结构树
			String mycode=parentCode+"-"+org.getId();
			if ("000".equals(parentCode))
			{
				mycode=""+org.getId();
			}
			if (parentId == null)
			{
//				if (org.getSortNum()!=null)
//				{
//					mycode=""+org.getSortNum();
//				}
//				else
				{
					mycode=""+org.getId();
				}
				
			}
			org.setOrganizecode(mycode);
			structureDAO.update(org);
			
			orgId = org.getId();
			organization = org;
			permissionDAO.copySystemRoleActionToOrg(orgId);
		}
		else
		{
			organization = structureDAO.findOrganizationsById(orgId);
			String oldcode=organization.getOrganizecode();
			Organizations parent = organization.getParent();
			if (parent!=null)
			{
				parentCode=parent.getOrganizecode();
				if (parentCode==null || parentCode.length()==0)
	        	{
	        		parentCode=""+parent.getId();
	        		parent.setOrganizecode(parentCode);
	        	}
			}
			else
			{
				
			}
			if (parentId == null)    // 新父为null
			{
				organization.setParent(null);
			}
			else if (parent == null || parent.getId().longValue() != parentId.longValue())  // 原有父为null或者父不一致
			{
				parent = structureDAO.findOrganizationsById(parentId);
				organization.setParent(parent);
			}

			String mycode=parentCode+"-"+org.getId();
			if (parentId == null)
			{
//				if (org.getSortNum()!=null)
//				{
//					if (org.getSortNum()<10)
//					{
//						mycode="00"+org.getSortNum();
//					}
//					else if (org.getSortNum()<100)
//					{
//						mycode="0"+org.getSortNum();
//					}
//				}
//				else
				{
					mycode=""+org.getId();
				}
			}
			else
			{
//				if (org.getSortNum()!=null)
//				{
//					mycode=parentCode+"-"+org.getSortNum();
//				}
//				else
				{
					mycode=parentCode+"-"+org.getId();
				}
			}
			org.setOrganizecode(mycode);
			
			organization.update(org);
			organization.setManager(leader);
			structureDAO.update(organization);

			structureDAO.excute("update Organizations set organizecode=replace(organizecode,'"+oldcode+"-','"+mycode+"-') "
					+" where organizecode like '"+oldcode+"-%'");//更新树结构
			if (space != null && organization.getSpaceUID()!=null)
			{
				retSpace = structureDAO.findSpaceByUID(organization.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}			
		}
		if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(orgId);
			structureDAO.deleteUsersOrganizationsByOrgId(ids, delUserIds);
		}
		if (addUserIds != null)
		{
			Roles role;
			Long uid;
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds[i];
				Users or = structureDAO.findUserById(uid);
				UsersOrganizations uog = new UsersOrganizations(or, organization);
				structureDAO.save(uog);		
				
				if (roleIds == null || roleIds[i] == null)
				{
					UsersRoles ur = structureDAO.findUserRoleInOrg(uid, orgId) ;
					if (ur != null)
					{
						structureDAO.delete(ur);
					}
				}
				else if (roleIds != null && roleIds[i] != null)
				{
					structureDAO.delUserOrgRole(uid, orgId);
					role = structureDAO.findRoleById(roleIds[i]);
					UsersRoles ur = new UsersRoles(or, role);
					structureDAO.save(uog);
				}
			}			
		}
		retSpace.setOrganization(organization);
		return retSpace;
	}
	public void upOrgnizeCode(Organizations org)
	{
		try
		{
			Organizations parent = org.getParent();
			String parentCode="";
			String mycode=""+org.getSortNum();
	       	if (parent!=null)
	        {
	        	parentCode=parent.getOrganizecode();
	        	if (parentCode==null || parentCode.length()==0)
	        	{
	        		parentCode=""+parent.getId();
	        		parent.setOrganizecode(parentCode);
	        		structureDAO.update(parent);
	        	}
	        	mycode=parentCode+"-"+org.getId();
	        }
	       	else
	       	{
				//更改组织结构树
				mycode=""+org.getId();
	       	}
			org.setOrganizecode(mycode);
			structureDAO.update(org);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public Spaces addOrUpdateOrganization(Long parentId, Organizations org,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds, Long leaderId, Spaces space,Users user)
	{
		Long orgId = org.getId();
		Organizations organization;
		Users leader = null;
		Spaces retSpace = space;
		if (leaderId != null)
        {
        	leader = structureDAO.findUserById(leaderId); 
        }
		String parentCode="000";
		if (orgId == null)
		{
			retSpace = createSpace(FileConstants.ORG_ROOT, org.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
            org.setSpaceUID(retSpace.getSpaceUID());
            if (parentId != null)
            {
            	Organizations parent = structureDAO.findOrganizationsById(parentId);
            	org.setParent(parent);
            	if (parent!=null)
            	{
	            	parentCode=parent.getOrganizecode();
	            	if (parentCode==null || parentCode.length()==0)
	            	{
	            		parentCode=""+parent.getId();
	            		parent.setOrganizecode(parentCode);
	            	}
            	}
            }
            
            org.setManager(leader);
			structureDAO.save(org);
			//更改组织结构树
			String mycode=parentCode+"-"+org.getId();
			if (parentId == null)
			{
				mycode=""+org.getId();
			}
			org.setOrganizecode(mycode);
			structureDAO.update(org);
			
			orgId = org.getId();
			organization = org;
			permissionDAO.copySystemRoleActionToOrg(orgId);
		}
		else
		{
			organization = structureDAO.findOrganizationsById(orgId);
			Organizations parent = organization.getParent();
			
			if (parentId == null)    // 新父为null
			{
				organization.setParent(null);
			}
			else if (parent == null || parent.getId().longValue() != parentId.longValue())  // 原有父为null或者父不一致
			{
				parent = structureDAO.findOrganizationsById(parentId);
				organization.setParent(parent);
			}
			if (parentId!=null)
			{
				Organizations newparent = structureDAO.findOrganizationsById(parentId);
				parentCode=newparent.getOrganizecode();
				if (parentCode==null || parentCode.length()==0)
	        	{
	        		parentCode=""+newparent.getId();
	        		newparent.setOrganizecode(parentCode);
	        	}
				else
				{
					
				}
			}
			String mycode=parentCode+"-"+org.getId();
			if (parentId == null)
			{
				mycode=""+org.getId();
			}
			organization.setOrganizecode(mycode);
			
			organization.update(org);
			organization.setManager(leader);
			
			
			structureDAO.update(organization);
			
			if (space != null)
			{
				retSpace = structureDAO.findSpaceByUID(organization.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}			
		}
		if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(orgId);
			structureDAO.deleteUsersOrganizationsByOrgId(ids, delUserIds);
		}
		if (addUserIds != null)
		{
			Roles role;
			Long uid;
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds[i];
				Users or = structureDAO.findUserById(uid);
				UsersOrganizations uog = new UsersOrganizations(or, organization);
				structureDAO.save(uog);		
				
				if (roleIds == null || roleIds[i] == null)
				{
					UsersRoles ur = structureDAO.findUserRoleInOrg(uid, orgId) ;
					if (ur != null)
					{
						structureDAO.delete(ur);
					}
				}
				else if (roleIds != null && roleIds[i] != null)
				{
					structureDAO.delUserOrgRole(uid, orgId);
					role = structureDAO.findRoleById(roleIds[i]);
					UsersRoles ur = new UsersRoles(or, role);
					structureDAO.save(uog);
				}
			}			
		}
		retSpace.setOrganization(organization);
		return retSpace;
	}
	
	/**
	 * 得到所有组织定义
	 * @return
	 */
	public List<Organizations> getAllOrganizations()
	{
		List<Organizations> ret = structureDAO.findAllOrganizations(true);
		for(Organizations temp : ret)
		{
			temp.setMemberSize(structureDAO.getUserCountByOrganizations(temp.getId(), false).intValue());
		}
		return ret;
	}
	/**
	 * 得到组织，start为-1，表示从第一条记录开始得，cont为-1，表示的start开始的所有记录。
	 * @return
	 */
	public List<Organizations> getAllOrganizations(int start, int count, String sort, String dir)
	{
		String queryString = " select u from Organizations u ";
		if (sort != null && dir != null)
		{
			queryString += " order by u." + sort + " " + dir;
		}
		List<Organizations> ret = structureDAO.findAllBySql(start, count, queryString);	
		for(Organizations temp : ret)
		{
			temp.setMemberSize(structureDAO.getUserCountByOrganizations(temp.getId(), false).intValue());
		}
		return ret;
	}
	
	public List<Organizations> getAllOrganizations(Users users)
	{
		List<UsersOrganizations> list=structureDAO.findUsersOrganizationsByUserId(users.getId());
		String orgcode=null;
		if (list!=null && list.size()>0)
		{
			orgcode=list.get(0).getOrganization().getOrganizecode();
			if (orgcode!=null && orgcode.length()>0)
			{
				int index=orgcode.indexOf("-");
				if (index>1)
				{
					orgcode=orgcode.substring(0,index);//找到根节点组织
				}
			}
		}
		List<Organizations> ret = structureDAO.findAllOrganizations(true,orgcode);
		for(Organizations temp : ret)
		{
			temp.setMemberSize(structureDAO.getUserCountByOrganizations(temp.getId(), false).intValue());
		}
		return ret;
	}
    
    /**
     * 得到组织，start为-1，表示从第一条记录开始得，cont为-1，表示的start开始的所有记录。
     * @return
     */
    public List<Organizations> getAllOrganizations(String searchkey,int start, int count, String sort, String dir,Users users)
    {
    	return structureDAO.getAllOrganizations(searchkey,start, count, sort, dir,users);
    	
        
    }

	/**
	 * 删除组织
	 * @param orgIds
	 */
	public void deleteOrganizations(List<Long> orgIds)
	{
		if (orgIds != null && orgIds.size() > 0)
		{
			structureDAO.deleteOrganizationsByID(orgIds);
		}
		// 还需处理组织文件库的删除。
	}
	
	/**
	 * 得到user所在的组，如果用户在多个组中，则返回多个组对象。
	 * @param userId
	 * @return
	 */
	public List<Groups> getGroupsByUsers(long userId)
	{
		return structureDAO.findGroupsByUserId(userId);
	}
		
	/**
	 * 给组中增加成员或删除成员
	 * @param groupId
	 * @param userIds
	 */
	public void addOrDeleteGroupMembers(long groupId, List<Long> addUserIds, List<Long> delUserIds)	
	{
		Groups group = structureDAO.findGroupById(groupId);
		if (group != null)
		{
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(groupId);
				structureDAO.deleteUsersGroupsByGroupId(ids, delUserIds);
			}
			if (addUserIds != null && addUserIds.size() > 0)
			{
				for (Long uid : addUserIds)
				{
					Users or = structureDAO.findUserById(uid);
					UsersGroups uog = new UsersGroups(or, group);
					structureDAO.save(uog);				
				}
			}
		}
	}
	
	/**
	 * 给组中增加成员或删除成员或修改组成员	 
	 * @param groupId 组Id
	 * @param addUserIds 增加或修改的成员id
	 * @param roleIds 成员的角色id
	 * @param delUserIds 删除成员id
	 */
	public void addOrUpdateGroupMembers(long groupId, Long[] addUserIds, Long[] roleIds, List<Long> delUserIds)	
	{
		Groups group = structureDAO.findGroupById(groupId);
		if (group != null)
		{
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(groupId);
				structureDAO.delUserGroupRole(delUserIds, groupId);
				structureDAO.deleteUsersGroupsByGroupId(ids, delUserIds);
			}
			if (addUserIds != null)
			{
				long gid = group.getId();
				long uid;
				int size = addUserIds.length;
				for (int i = 0; i < size; i++) 
				{
					uid = addUserIds[i];
					Users or = structureDAO.findUserById(uid);
					UsersGroups uog = structureDAO.findUserGroup(uid, gid);
					if (uog == null)
					{
						uog = new UsersGroups(or, group);
						structureDAO.save(uog);
						//MessagesService me = (MessagesService)ApplicationContext.getInstance().getBean("messagesService");
						//List<Long> us = new ArrayList<Long>();
						//us.add(uid);
						//me.sendMessage("message.messagePush", "userId", "add you into group", 1, us);
					}
					if (roleIds == null || roleIds[i] == null)
					{
						UsersRoles ur = structureDAO.findUserRoleInGroup(uid, groupId) ;
						if (ur != null)
						{
							structureDAO.delete(ur);
						}
					}
					else if (roleIds != null && roleIds[i] != null)
					{
						UsersRoles ur = structureDAO.findUserRoleInGroup(uid, groupId) ;
						if (ur == null || roleIds[i].longValue() != ur.getRole().getRoleId().longValue())
						{
							Roles role = structureDAO.findRoleById(roleIds[i]);
							if (ur != null)
							{
								structureDAO.delete(ur);
							}
							ur = new UsersRoles(or, role);							
							structureDAO.save(ur);
						}
					}
									
				}
			}
		}
	}
	
	/**
	 * 给用户自定义组中增加成员或删除成员或修改组成员	 
	 * @param groupId 组Id
	 * @param addUserIds 增加或修改的成员id
	 * @param roleIds 成员的角色id
	 * @param delUserIds 删除成员id
	 */
	public void addOrUpdateTeamMembers(long teamId, Long[] addUserIds, Long[] roleIds, List<Long> delUserIds)	
	{
		CustomTeams team = structureDAO.findCustomTeamsById(teamId);
		if (team != null)
		{
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(teamId);
				structureDAO.delUserTeamRole(delUserIds, teamId);
				structureDAO.deleteTeamsByTeamId(ids, delUserIds);
			}
			if (addUserIds != null)
			{
				long gid = team.getId();
				long uid;
				int size = addUserIds.length;
				for (int i = 0; i < size; i++) 
				{
					uid = addUserIds[i];
					Users or = structureDAO.findUserById(uid);
					UsersCustomTeams uog = structureDAO.findUserTeam(uid, gid);
					if (uog == null)
					{
						uog = new UsersCustomTeams(or, team);
						structureDAO.save(uog);
						//MessagesService me = (MessagesService)ApplicationContext.getInstance().getBean("messagesService");
						//List<Long> us = new ArrayList<Long>();
						//us.add(uid);
						//me.sendMessage("message.messagePush", "userId", "add you into group", 1, us);
					}
					if (roleIds == null || roleIds[i] == null)
					{
						UsersRoles ur = structureDAO.findUserRoleInTeam(uid, teamId) ;
						if (ur != null)
						{
							structureDAO.delete(ur);
						}
					}
					else if (roleIds != null && roleIds[i] != null)
					{
						UsersRoles ur = structureDAO.findUserRoleInTeam(uid, teamId) ;
						if (ur == null || roleIds[i].longValue() != ur.getRole().getRoleId().longValue())
						{
							Roles role = structureDAO.findRoleById(roleIds[i]);
							if (ur != null)
							{
								structureDAO.delete(ur);
							}
							ur = new UsersRoles(or, role);							
							structureDAO.save(ur);
						}
					}
									
				}
			}
		}
	}
	
	/**
	 * 给组织中增加成员或删除成员或修改组织成员	 
	 * @param orgId 组织Id
	 * @param addUserIds 增加或修改的成员id
	 * @param roleIds 成员的角色id
	 * @param delUserIds 删除成员id
	 */
	public void addOrUpdateOrgMembers(long orgId, Long[] addUserIds, Long[] roleIds, List<Long> delUserIds)	
	{
		Organizations org = structureDAO.findOrganizationsById(orgId);
		if (org != null)
		{
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(orgId);
				structureDAO.delUserOrgRole(delUserIds, orgId);
				structureDAO.deleteUsersOrganizationsByOrgId(ids, delUserIds);
			}
			if (addUserIds != null)
			{
				long gid = org.getId();
				long uid;
				int size = addUserIds.length;
				for (int i = 0; i < size; i++) 
				{
					uid = addUserIds[i];
					Users or = structureDAO.findUserById(uid);
					UsersOrganizations uog = structureDAO.findUserOrganization(uid, gid);
					if (uog == null)
					{
						uog = new UsersOrganizations(or, org);
						structureDAO.save(uog);
					}
					if (roleIds == null || roleIds[i] == null)
					{
						UsersRoles ur = structureDAO.findUserRoleInOrg(uid, orgId) ;
						if (ur != null)
						{
							structureDAO.delete(ur);
						}
					}
					else if (roleIds != null && roleIds[i] != null)
					{
						UsersRoles ur = structureDAO.findUserRoleInOrg(uid, orgId) ;
						if (ur == null || roleIds[i].longValue() != ur.getRole().getRoleId().longValue())
						{
							Roles role = structureDAO.findRoleById(roleIds[i]);
							if (ur != null)
							{
								structureDAO.delete(ur);
							}
							ur = new UsersRoles(or, role);							
							structureDAO.save(ur);
						}
					}
									
				}
			}
		}
	}
	
	/**
	 * 新建或修改用户自定义组
	 * @param team 新增或修改的组
	 * @param addUserIds 在组中的新增成员
	 * @param roleIds 组成员对应的role的角色id值，如果没有角色，则赋值为null，该数组的下标需要同
	 * addUserIds的下标对应，即是addUserIds[index]和roleIds[index]是同一个用户的角色。
	 * @param delUserIds 删除组中的成员
	 * @param ownerId 组的负责人
	 * @param space 组的空间，如果在修改组的时候，不需要修改空间的属性，则该值传null。
	 */
	public Spaces addOrUpdateTeam(CustomTeams team,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds, Long ownerId, Spaces space)
	{
		Long teamId = team.getId();
		CustomTeams newTeam;
		Users owner = null;
		Spaces retSpace = space;
		if (ownerId != null)
        {
        	owner = structureDAO.findUserById(ownerId); 
        }
		if (teamId == null)
		{
			retSpace = createSpace(FileConstants.TEAM_ROOT, team.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
            team.setSpaceUID(retSpace.getSpaceUID());
            team.setUser(owner);
			structureDAO.save(team);
			
			teamId = team.getId();
			newTeam = team;
			permissionDAO.copySystemRoleActionToTeam(teamId,owner.getCompany().getId());
		}
		else
		{
			newTeam = structureDAO.findCustomTeamsById(teamId);
			newTeam.update(team);
			structureDAO.update(newTeam);
			
			if (space != null)
			{
				retSpace = structureDAO.findSpaceByUID(newTeam.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}			
		}
		if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(teamId);
			structureDAO.deleteTeamsByTeamId(ids, delUserIds);
		}
		if (addUserIds != null)
		{
			Roles role;
			Long uid;
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds[i];
				Users or = structureDAO.findUserById(uid);
				UsersCustomTeams uog = new UsersCustomTeams(or, newTeam);
				structureDAO.save(uog);			
				
				if (roleIds == null || roleIds[i] == null)
				{
					UsersRoles ur = structureDAO.findUserRoleInTeam(uid, teamId) ;
					if (ur != null)
					{
						structureDAO.delete(ur);
					}
				}
				else if (roleIds != null && roleIds[i] != null)
				{
					structureDAO.delUserTeamRole(uid, teamId);
					role = structureDAO.findRoleById(roleIds[i]);
					UsersRoles ur = new UsersRoles(or, role);
					structureDAO.save(ur);
				}
			}
		}
		retSpace.setTeam(newTeam);
		return retSpace;
	}
	
	/**
	 * 新建或修改组
	 * @param parentId 新的父组，没有为null
	 * @param group 新增或修改的组
	 * @param addUserIds 在组中的新增成员
	 * @param roleIds 组成员对应的role的角色id值，如果没有角色，则赋值为null，该数组的下标需要同
	 * addUserIds的下标对应，即是addUserIds[index]和roleIds[index]是同一个用户的角色。
	 * @param delUserIds 删除组中的成员
	 * @param leaderId 组的负责人
	 * @param space 组的空间，如果在修改组的时候，不需要修改空间的属性，则该值传null。
	 */
	public Spaces addOrUpdateGroup(Long parentId, Groups group,
			Long[] addUserIds, Long[] roleIds, List<Long> delUserIds, Long leaderId, Spaces space)
	{
		Long groupId = group.getId();
		Groups newGroup;
		Users leader = null;
		Spaces retSpace = space;
		if (leaderId != null)
        {
        	leader = structureDAO.findUserById(leaderId); 
        }
		if (groupId == null)
		{
			retSpace = createSpace(FileConstants.GROUP_ROOT, group.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
            group.setSpaceUID(retSpace.getSpaceUID());
            if (parentId != null)
            {
            	Groups parent = structureDAO.findGroupById(parentId);
            	group.setParent(parent);
            }            
            group.setManager(leader);
			structureDAO.save(group);
			
			groupId = group.getId();
			newGroup = group;
			permissionDAO.copySystemRoleActionToGroup(groupId);
		}
		else
		{
			newGroup = structureDAO.findGroupById(groupId);
			Groups parent = newGroup.getParent();
			if (parentId == null)    // 新父为null
			{
				newGroup.setParent(null);
			}
			else if (parent == null || parent.getId().longValue() != parentId.longValue())  // 原有父为null或者父不一致
			{
				parent = structureDAO.findGroupById(parentId);
				newGroup.setParent(parent);
			}			
			newGroup.update(group);
			newGroup.setManager(leader);
			structureDAO.update(newGroup);
			
			if (space != null)
			{
				retSpace = structureDAO.findSpaceByUID(newGroup.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}			
		}
		if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(groupId);
			structureDAO.deleteUsersGroupsByGroupId(ids, delUserIds);
		}
		if (addUserIds != null)
		{
			Roles role;
			Long uid;
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds[i];
				Users or = structureDAO.findUserById(uid);
				UsersGroups uog = new UsersGroups(or, newGroup);
				structureDAO.save(uog);			
				
				if (roleIds == null || roleIds[i] == null)
				{
					UsersRoles ur = structureDAO.findUserRoleInGroup(uid, groupId) ;
					if (ur != null)
					{
						structureDAO.delete(ur);
					}
				}
				else if (roleIds != null && roleIds[i] != null)
				{
					structureDAO.delUserGroupRole(uid, groupId);
					role = structureDAO.findRoleById(roleIds[i]);
					UsersRoles ur = new UsersRoles(or, role);
					structureDAO.save(ur);
				}
			}
		}
		retSpace.setGroup(newGroup);
		return retSpace;
	}
	
	/**
	 * 删除组
	 * @param groupIds
	 */
	public void deleteGroups(List<Long> groupIds)
	{
		if (groupIds != null && groupIds.size() > 0)
		{
			structureDAO.deleteGroupsByID(groupIds);
		}
		// 还需处理组织文件库的删除。
	}
	
	/**
	 * 删除组
	 * @param groupIds
	 */
	public void deleteGroupSpaces(List<String> spaceUIDs)
	{
		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
		if (spaceUIDs != null && spaceUIDs.size() > 0)
		{
			structureDAO.deleteGroupSpacesBySpaceUID(spaceUIDs);		
			jcrService.deleteSpace(spaceUIDs);
		}
		// 还需处理组织文件库的删除。
	}
	
	
	/**
	 * 判断用户是否是组空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isGroupManager(Long userId, String spaceUID)
	{
		return structureDAO.isGroupManager(userId, spaceUID);
	}
	
	/**
	 * 判断用户是否是组织空间的管理者
	 * @param userId
	 * @param spaceUID
	 * @return
	 */
	public boolean isOrganizatonManager(Long userId, String spaceUID)
	{
		return structureDAO.isOrganizatonManager(userId, spaceUID);
	}
	
	/**
	 * 得到user所建立的用户自定义组，如果用户建立多个自定义组中，则返回多个组对象。
	 * @param userId
	 * @return
	 */
	public List<CustomTeams> getUserOwnCustomTeams(long userId)
	{
		return structureDAO.findUserOwnCustomTeamsByUserId(userId);
	}
	
	/**
	 * 得到user所在的用户自定义组，如果用户在多个自定义组中，则返回多个组对象。
	 * @param userId
	 * @return
	 */
	public List<CustomTeams> getCustomTeamsByUsers(long userId)
	{
		return structureDAO.findCustomTeamsByUserId(userId);
	}
		
	/**
	 * 给用户自定义组中增加成员或删除成员
	 * @param groupId
	 * @param userIds
	 */
	public void addOrDeleteTeamMembers(long teamId, List<Long> addUserIds, List<Long> delUserIds)	
	{
		CustomTeams team = structureDAO.findCustomTeamsById(teamId);
		if (team != null)
		{
			if (delUserIds != null && delUserIds.size() > 0)
			{
				ArrayList<Long> ids = new ArrayList<Long>();
				ids.add(teamId);
				structureDAO.deleteTeamsByTeamId(ids, delUserIds);
			}
			if (addUserIds != null && addUserIds.size() > 0)
			{
				for (Long uid : addUserIds)
				{
					Users or = structureDAO.findUserById(uid);
					UsersCustomTeams uog = new UsersCustomTeams(or, team);
					structureDAO.save(uog);				
				}
			}
		}
	}
	
	/**
	 * 新建或修改用户自定义组
	 * @param team 新增或修改的用户自定义组，如果是新建立的组，getId（）为null，如果是修改的组
	 * 则getId（）值为原修改组的id值。
	 * @param addUserIds 在组中的新增成员
	 * @param delUserIds 删除组中的成员
	 * @param userId 用户自定义组所属的用户id，如果是修改用户自动定义组，该id可以为null
	 * @param space 用户自定义组的空间，如果在修改组的时候，不需要修改空间的属性，则该值传null。
	 */
	public Spaces addOrUpdateTeam(CustomTeams team,
			List<Long> addUserIds, List<Long> delUserIds, Long userId, Spaces space)
	{
		Long teamId = team.getId();
		CustomTeams newTeam;
		Spaces retSpace = space;
		if (userId == null)
        {
			return retSpace;
        }
//		Users user=this.getUser(userId);
		if (teamId == null)
		{
			retSpace = createSpace(FileConstants.TEAM_ROOT,  team.getName(), space.getName(), space.getDescription(), space.getSpaceStatus());
            team.setSpaceUID(retSpace.getSpaceUID());
            
            Users owner = structureDAO.findUserById(userId); 
            team.setUser(owner);
			structureDAO.save(team);
			
			teamId = team.getId();
			newTeam = team;
			permissionDAO.copySystemRoleActionToTeam(teamId,owner.getCompany().getId());
		}
		else
		{
			newTeam = structureDAO.findCustomTeamsById(teamId);
			newTeam.update(team);
			structureDAO.update(newTeam);
			
			if (space != null)
			{
				retSpace = structureDAO.findSpaceByUID(newTeam.getSpaceUID());
				retSpace.update(space);
				structureDAO.update(retSpace);
			}			
		}
		if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(teamId);
			structureDAO.deleteTeamsByTeamId(ids, delUserIds);
		}
		if (addUserIds != null && addUserIds.size() > 0)
		{
			for (Long uid : addUserIds)
			{
				Users or = structureDAO.findUserById(uid);
				UsersCustomTeams uog = new UsersCustomTeams(or, team);
				structureDAO.save(uog);				
			}
		}
		retSpace.setTeam(newTeam);
		return retSpace;
	}
	
	/**
	 * 获得组组
	 * @param groupIds
	 */
	public CustomTeams findCustomTeamsById(Long teamId)
	{
	  return	structureDAO.findCustomTeamsById(teamId);
		// 还需处理组织文件库的删除。
	}
	
	public Groups findGroupById(Long gId)
	{
	  return	structureDAO.findGroupById(gId);
		// 还需处理组织文件库的删除。
	}
	/**
	 * 删除组
	 * @param groupIds
	 */
	public void delCustomTeams(List<Long> teamIds)
	{
		structureDAO.deleteTeamsById(teamIds);
		// 还需处理组织文件库的删除。
	}
    
    /**
	 * 根据父Id得到子组织的,不递归获得子组织。 
	 * @param parentId 如果parentId为null则返回第一级的所有组组织。
	 * @return
	 */
	@Deprecated
	public List<Organizations> getChildOrganizations(Long parentId)
	{
		return structureDAO.getChildOrganizations(parentId);
	}
	
	public List<Organizations> getChildOrganizations(Long companyId, Long parentId)
	{
		return structureDAO.getChildOrganizations(parentId, companyId, false);
	}
	
	/**
	 * 根据父Id得到子组的，不递归获得子组 
	 * @param parentId 如果parentId为null则返回第一级的所有组。
	 * @return
	 */
	public List<Groups> getChildGroups(Long parentId)
	{
		return structureDAO.getChildGroups(parentId);
	}
	
	/**
	 * 得到分页的用户视图值，该值为adminUserinfoView对象。
	 */
	// 后续有时间在修改该方法的返回值。
	public DataHolder getUserView(Users users,int index, int length, String sort, String dir)
	{
		return structureDAO.getUserView(users,index, length, sort, dir);
	}
	
	/**
	 * 获得用户在组中的角色，如果flag为true,则同时获得用户在该组的系统全局角色，否则仅仅获得
	 * 该组中定义的角色。
	 * @param userId 用户id
	 * @param groupId 组id
	 * @param flag 是否获取组中的全局角色
	 * @return
	 */
	public List<Roles> getUserGroupRole(Long userId, Long groupId, boolean flag)
	{
		return structureDAO.getUserGroupRole(userId, groupId, flag);
	}

	/**
	 * 获得用户在组织中的角色，如果flag为true,则同时获得用户在该组织的系统全局角色，否则仅仅获得
	 * 该组织中定义的角色。
	 * @param userId 用户id
	 * @param groupId 组织id
	 * @param flag 是否获取组织中的全局角色
	 * @return
	 */
	public List<Roles> getUserOrgRole(Long userId, Long orgId, boolean flag)
	{
		return structureDAO.getUserOrgRole(userId, orgId, flag);
	}	
	
	/**
	 * 判断用户是否已经存在
	 * @param name
	 * @return
	 */
	public boolean isExistUser(String name)
	{
		return structureDAO.isExistUser(name) != null;
	}
	public Long isExistOrg(String orgname)
	{
		return structureDAO.isExistOrg(orgname);
	}
	 /**
     * 得到组内的用户caId
     */
    public List<String> getDistinctGMCA(List<Long> groupIds)
    {
        Long[] ids = groupIds.toArray(new Long[0]);
        return groupmemberinfoViewDAO.getDistictGMCA(ids);
    }
    
    public Users getKeyPair(Long userId)
    {
    	return userinfoViewDAO.findUsersById(userId);
    }
    
    /**
     * 获得系统中用户的总数量。
     * @return
     */
    public long getUserCount(int searchType, String keyWord,Users users)
    {
    	if (users==null)
    	{
    		return structureDAO.getUserCount(searchType, keyWord,null);
    	}
    	else
    	{
    		return structureDAO.getUserCount(searchType, keyWord,users);
    	}
    }
    
    /**
     * 获取系统中用户信息。如果start和count不小于0，则表示分页获取。
     * @param start 开始的位置，如果小于0，在表示从第一条记录开始获取。
     * @param count 一次获取的数量，如果小于0，则表示从start开始后的所有用户数据。
     * @param sort 排序字段名
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> getUsers(int start, int count, String sort, String dir)
    {
    	return structureDAO.getUsers(start, count, sort, dir);
    }
    public List<Users> getUsers(int start, int count, String sort, String dir,Users user)
    {
    	return structureDAO.getUsers(start, count, sort, dir,user);
    }
    /**
     * 搜索符合keyword条件的用户。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @return
     */
    public long getUser(int option, String keyWord)
    {
    	return structureDAO.getUser(option, keyWord);
    }
    
    /**
     * 搜索符合keyword条件的用户，并根据需要分页显示。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @param start 搜索的开始位置，小于0，表示从第一个记录开始搜索。
     * @param count 搜索的数量，小于0，表示从start位置开始后的所有记录。
     * @param sort 排序字段
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> getUser(int option, String keyWord, int start, int count, String sort, String dir)
    {
    	return structureDAO.getUser(option, keyWord, start, count, sort, dir);
    }
    
    public List<Users> getUser(int option, String keyWord, int start, int count, String sort, String dir,Users user)
    {
    	return structureDAO.getUser(option, keyWord, start, count, sort, dir,user);
    }
    
    public Long getGroupIdBySpaceUID(String spaceUID)
    {
    	return structureDAO.getGroupIdBySpaceUID(spaceUID);
    }
    public Long getCompanyIdBySpaceUID(String spaceUID)
    {
    	return structureDAO.getCompanyIdBySpaceUID(spaceUID);
    }
    public Long getTeamIdBySpaceUID(String spaceUID)
    {
    	return structureDAO.getTeamIdBySpaceUID(spaceUID);
    }
    
    public String[] getAllFileinfo(Long userId, String[] path) 
    {
    	List<String> list = new ArrayList<String>();
    	JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		for (int i = 0, len = path.length; i < len; i++)
		{
			if (path[i].charAt(0) == '*')
			{
				list.add(path[i]);
				getAllFileinfo_sub(userId, path[i].substring(1), list, jcrService);
			}
			else
			{
				list.add(path[i]);
			}
		}
		String[] result = new String[list.size()];
		return list.toArray(result);
    }
    
	private void getAllFileinfo_sub(Long userId, String path, List<String> list, JCRService jcrService)
	{
		try
		{
			ArrayList<Fileinfo> infos = jcrService.listFileinfos(null, path);
			for (int j=0;j<infos.size();j++)
			{
				if (infos.get(j).isFold())
				{
					getAllFileinfo_sub(userId, infos.get(j).getPathInfo(), list, jcrService);
					list.add('*' + infos.get(j).getPathInfo());
				}
				else
				{
					list.add(infos.get(j).getPathInfo());
				}
			}
		}
		catch(Exception e)
		{
			
		}
	}
		
	public Users getUserBySpaceUID(String uid)
	{
		return structureDAO.searchUserBySpaceUID(uid);
	}
	 
	/**
	 * 用户登录时候，检测在线人数及license的合法行。
	 * @param count
	 * @return
	 */
	public Integer checkOnlinUser(int count, Long companyId)
	{
		List<License> list = structureDAO.findAll("License");
		Company company = structureDAO.findCompanyById(companyId);		
		License license = null;
		if (list != null && list.size() > 0)
		{
			license = list.get(0);
		}
		if(license == null)
		{
			return ErrorCons.USER_ILLEGAL_LICENSE_ERROR;
		}
		
		long onUser = count;
		long liceUser = LicenseEn.decodeOUC(license.getUc());
		if (onUser > liceUser)
		{
			return ErrorCons.USER_ONLINE_MAX_USER_ERROR;
		}
		
		long bdate = LicenseEn.decodeOUD(license.getBud());
		Calendar cal = Calendar.getInstance();
		long ndate = cal.getTimeInMillis();
		if (ndate < bdate)
		{
			return ErrorCons.USER_LICENSE_ILLEGAL_TIME_ERROR;
		}
		long date = LicenseEn.decodeOUD(license.getUd());
		if (ndate > date)
		{
			return ErrorCons.USER_LICENSE_END_ERROR;
		}		
		long companyDate = company.getUd();
		if (ndate > companyDate)     // 公司账户已经到期
		{
			return ErrorCons.USER_COMPANY_END_ERROR;
		}
		return ErrorCons.USER_ONLINE_USER_PER_ERROR;
	}
	
	/**
	 * 用户登录检测。当运行用户正常登录的时候，返回用户信息，否则返回登录错误代码。
	 * @param loginStr
	 * @param password
	 * @param checkPassword 是否需要检测password，当单点登录的时候，不需要检测密码。
	 * @return
	 */
	public Object login(String loginStr, String password, boolean checkPassword)
	{
		return login(loginStr, password, null, checkPassword);
	}

	/**
	 * 用户登录检测。当运行用户正常登录的时候，返回用户信息，否则返回登录错误代码。-----这个方法更改了，要记住把下面的方法也跟着改
	 * @param loginStr
	 * @param password
	 * @param checkPassword 是否需要检测password，当单点登录的时候，不需要检测密码。
	 * @return
	 */
	private Object login(String loginStr, String password, Users user,	boolean checkPassword)
	{
		if (user == null)
		{
			List list2 = structureDAO.findUserByProperty("userName", loginStr);
			if (list2.size() < 1)
			{
				return ErrorCons.USER_NO_EXIST_ERROR;
			}
			if (list2.size() >= 1)
			{
				user = (Users) list2.get(0);
			}
		}

		if ("1".equals(user.getLoginCA()))     // 只运行CA证书登录
		{
			return ErrorCons.USER_CA_LOGIN_ERROR;
		}

		String userPassword = user.getPassW();

		if (user.getRole() != null)
		{
			short role;
			role = user.getRole();
			if (adminSplit && !superAdminEnable && role == Constant.ADIMI) // 三权分离、不允许超级管理员存在的情况下，不允许超级管理员登陆
			{
				return ErrorCons.USER_NO_EXIST_ERROR;
			}
			else if (!adminSplit && (role == Constant.AUDIT_ADMIN
						|| role == Constant.USER_ADMIN || role == Constant.SECURITY_ADMIN)) // 在非三权分离的情况下，不允许用户管理员、审核员以及安全员登陆
			{
				return ErrorCons.USER_NO_EXIST_ERROR;
			}
		}
		
		if (user.getValidate().intValue() == 0)   //
		{
			return ErrorCons.USER_FORBIT_ERROR;
		}
		
		if (checkPassword)
		{
			if (userPassword == null || userPassword.equals(""))
			{
				if (!password.equals(""))
				{
					return ErrorCons.USER_PASSWORD_ERROR;
				}
			}
			else
			{
				MD5 md5 = new MD5();
				String opassword = md5.getMD5ofStr(password);
				if (!(userPassword.equals(opassword)))
				{
					return ErrorCons.USER_PASSWORD_ERROR;
				}
			}
		}
				
		Users needUser = new Users();
		needUser.setId(user.getId());
		needUser.setCompanyId(user.getCompanyId());
		needUser.setRealEmail(user.getRealEmail());
		needUser.setPassW(userPassword);
        //设置发文
        if(user.getFawen()!=null){
            needUser.setFawen(user.getFawen());
        }
		String duty = user.getDuty();
		if (duty != null)
		{
			needUser.setDuty(duty);
		}
		Integer option = user.getMyoption();
		needUser.setMyoption(option);

		needUser.setRole(user.getRole());
		Float size = user.getStorageSize();
		needUser.setStorageSize(size);
		String name = user.getUserName();
		if (name != null)
		{
			needUser.setUserName(name);
		}
		needUser.setSpaceUID(user.getSpaceUID());
		needUser.setRealName(user.getRealName());
		String image = user.getImage();
		needUser.setImage(WebConfig.userPortrait + image);
		needUser.setAddress(user.getAddress());
		needUser.setCompanyName(user.getCompanyName());
		needUser.setFax(user.getFax());
		needUser.setMobile(user.getMobile());
		needUser.setPhone(user.getPhone());
		needUser.setPostcode(user.getPostcode());
		needUser.setCaId(user.getCaId());
		needUser.setPartadmin(user.getPartadmin());
		needUser.setCompany(user.getCompany());
		needUser.setLastsignlevel(user.getLastsignlevel());
		return needUser;
	}
	
	public Users calogin(String caid)
	{//根据CA号获取用户信息
		List<Users> list = structureDAO.findUserByProperty("caId", caid);//不管是否设置为CA登录，都可以用CA登录
		if (list==null || list.size()==0 || list.size()>1)
		{
			return null;//CA不正确或没有注册
		}
		Users user=list.get(0);
		if (user.getValidate().intValue() == 0)   //
		{
			return null;//已经禁用了
		}
		
		
		return user;
	}
	/**
	 * 登录文件库
	 * @param user
	 * @param password
	 * @return
	 */
	public int loginRepository(Users user, String password)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		try
		{
			jcrService.login(user.getSpaceUID(), password);
			jcrService.removeUserAllOpenedFile(user.getUserName(), user.getSpaceUID());
			//jcrService.clearUserOpenFile(user.getSpaceUID());
			return ErrorCons.NO_ERROR;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ErrorCons.FILE_SYSTEM_ERROR;
		}
	}
	 
	 
	public List<Users> getAllUser(Long userId, String searchTxt)
	{
		return structureDAO.getAllUser(userId, searchTxt);
	}
	public List<Users> getAllUsers()
	{
		return structureDAO.findAll(Users.class);
	}
	/**
	 * 从数据库中得某个用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserDept(long userId)
	{
		return structureDAO.findUserDept(userId);
	}
	
	/**
	 * 得到所有有审批权限的用户
	 * @return
	 */
	public List<AdminUserinfoView> getAllAuditUser(Long companyID)
	{
		List<Users> users = permissionDAO.getAllAuditUser(companyID);
		List<AdminUserinfoView> ret = new ArrayList<AdminUserinfoView>();
		if (users != null && users.size() > 0)
		{
			for (Users auv : users)
			{
				AdminUserinfoView au = new AdminUserinfoView(auv);				
				au.setOrganization(structureDAO.findOrganizationsByUserId(auv.getId()));
				ret.add(au);
			}
		}
		return ret;
	}
	
	/**
	 * 得到同部门下所有有审批权限的用户
	 * @return
	 */
	public List<Users> getAuditUserByUser(String account)
	{
		Users u = structureDAO.isExistUser(account);
		ArrayList<Users> ret = new ArrayList<Users>();
		if (u != null)
		{
			List<Organizations> os = structureDAO.findOrganizationsByUserId(u.getId());
			for (Organizations t : os)
			{
				List<Users> users = permissionDAO.getAuditUserByOrgId(t.getId());
				ret.addAll(users);
			}
		}
		return ret;
	}
	
	/**
	 * 得到同部门下所有有审批权限的用户
	 * @return
	 */
	public List<Users> getAuditUserByOrgId(Long orgId)
	{
		List<Users> users = permissionDAO.getAuditUserByOrgId(orgId);
		return users;
	}
	
	/**
     * 获取用户设置的打开文档方式，返回常量值参见com.evermore.weboffice.constants.both.FileSystemCons。
     * 中的“文件打开方式”产量定义。EDIT_TYPE, READ_PIC_TYPE, READ_HTML_TYPE.
     * @param userId
     * @return
     */
    public int getOpenFileType(long userId)
    {
    	UsersConfig uc = structureDAO.getUsersConfig(userId);
    	if (uc != null)
    	{
    		return uc.getOpentype();
    	}
    	//初始化默认桌面打开
    	return FileSystemCons.DESKTOP_EDIT_TYPE;
    }
    
    /**
     * 设置用户常用的文件打开方式。
     * @param userId
     * @param type
     */    
    public void setOpenFileType(long userId, int type)
    {
    	UsersConfig uc = structureDAO.getUsersConfig(userId);
    	if (uc != null)
    	{
    		uc.setOpentype(type);
    		structureDAO.update(uc);    		
    	}
    	else
    	{
    		uc = new UsersConfig();
    		uc.setUser(structureDAO.findUserById(userId));
    		uc.setOpentype(type);
    		structureDAO.save(uc);
    	}
    }
    
    /**
     * 
     * @param userId
     * @return
     */
    public UsersConfig getUsersConfig(long userId)
    {
    	UsersConfig uc = structureDAO.getUsersConfig(userId);
    	if(uc == null)
    	{
    		return new UsersConfig();
    	}
    	return uc;
    }
	
    
    /**
     * 
     * @param userId
     * @return
     */
    public void updateUsersConfig(UsersConfig uc)
    {
    	if (uc != null)
    	{
    		if(uc.getId() ==null)
    		{
    			structureDAO.save(uc); 
    		}
    		else
    		{
    			structureDAO.update(uc); 
    		}
    	}
    }
    
	/*public YzStyle getYzstyle(long yzstyleid)
	{
		YzStyle yzstyle = (YzStyle) normalDAO.find(YzStyle.class, yzstyleid);
		return yzstyle;
	}
    *//**
     * 获取所有app列表
     * @param searchtag
     * @return
     *//*
    public List<YzAPP> getAllAPP()
    {
    	List<YzAPP> lists = normalDAO.findAll(YzAPP.class);
    	return lists;
    }
    
    *//**
     * 通过tag查询
     * @param searchtag
     * @return
     *//*
    public List<YzAPP> getAllAPPByTag(String searchtag)
    {
    	String sql = "from YzAPP as model where model.tag like ?";
    	List<YzAPP> lists = normalDAO.findAllBySql(sql,"%"+searchtag+"%");
    	return lists;
    }
    
    *//**
     * 通过name查询
     * @param searchname
     * @return
     *//*
    public List<YzAPP> getAllAPPByName(String searchname)
    {
    	String sql = "from YzAPP as model where model.name like ?";
    	List<YzAPP> lists = normalDAO.findAllBySql(sql,"%"+searchname+"%");
    	return lists;
    }
    
    *//**
     * 获取所有Moudle列表
     * @return
     *//*
    public List<YzModule> getALLModule()
    {
    	List<YzModule> lists = normalDAO.findAll(YzModule.class);
    	return lists;
    }
    
    *//**
     * 通过tag查询
     * @param searchtag
     * @return
     *//*
    public List<YzModule> getAllModuleByTag(String searchtag)
    {
    	String sql = "from YzModule as model where model.tag like ?";
    	List<YzModule> lists = normalDAO.findAllBySql(sql,"%"+searchtag+"%");
    	return lists;
    }
    
    *//**
     * 通过name查询
     * @param searchname
     * @return
     *//*
    public List<YzModule> getAllModuleByName(String searchname)
    {
    	String sql = "from YzModule as model where model.name like ?";
    	List<YzModule> lists = normalDAO.findAllBySql(sql,"%"+searchname+"%");
    	return lists;
    }
    
    *//**
     * 获取所有yzStyle列表
     * @return
     *//*
    public List<YzStyle> getALLStyle()
    {
    	List<YzStyle> lists = normalDAO.findAll(YzStyle.class);
    	return lists;
    }
    *//**
     * 
     * @param apps
     * @param userId
     *//*
    public void addapps(String[] apps,UsersConfig uc)
    {
    	if(uc.getId() == null)
    	{
    		normalDAO.save(uc);
    	}
    	for (int i = 0; i < apps.length; i++) 
		{
			YzAPP yzapp = (YzAPP) normalDAO.find(YzAPP.class, Long.parseLong(apps[i]));
    		UsersYzAPP uyz = new UsersYzAPP();
    		uyz.setUsersConfig(uc);
    		uyz.setYzapp(yzapp);
    		normalDAO.save(uyz);
		}
    }
    
    *//**
     * 
     * @param apps
     * @param uc
     *//*
    public void removeapps(String[] apps,UsersConfig uc)
    {
    	if(uc.getId() == null)
    	{
    		normalDAO.save(uc);
    	}
    	for (int i = 0; i < apps.length; i++) 
		{
    		try
    		{
    			String queryString = "delete  UsersYzAPP  where usersConfig=? and yzapp.id=?";
    			normalDAO.excute(queryString, uc,Long.parseLong(apps[i]));
    		}
    		catch (RuntimeException re)
    		{
    			LogsUtility.error("delete failed", re);
    			throw re;
    		}
		}
    }
    *//**
     * 设置所有应用
     * @param apps
     * @param userId
     *//*
    public void setapps(String[] apps,UsersConfig uc)
    {
    	if(uc.getId() == null)
    	{
    		normalDAO.save(uc);
    	}
    	{
    		//先删除，
    		Set<UsersYzAPP> yzAPPSet = uc.getYzAPPSet();
    		for (UsersYzAPP usersYzAPP : yzAPPSet) {
    			int i = 0;
    			for ( ;i < apps.length; i++) 
    			{
    				if(apps[i].equals(usersYzAPP.getYzapp().getId()))//有，不删除
    				{
    					break;
    				}
    			}
    			if(i == apps.length)
    			{
    				normalDAO.delete(usersYzAPP);
    			}
			}
    		//后添加
    		for (int i = 0; i < apps.length; i++) 
    		{
    			boolean has = false;
    			for (UsersYzAPP usersYzAPP : yzAPPSet)
    			{
    				if(apps[i].equals(usersYzAPP.getYzapp().getId()))
    				{
    					has = true;
    				}
    			}
    			if(has)
    			{
    				continue;
    			}
    			YzAPP yzapp = (YzAPP) normalDAO.find(YzAPP.class, Long.parseLong(apps[i]));
        		UsersYzAPP uyz = new UsersYzAPP();
        		uyz.setUsersConfig(uc);
        		uyz.setYzapp(yzapp);
        		normalDAO.save(uyz);
    		}
    	}
    }
    
    *//**
     * 设置所有模块
     * @param modules
     * @param userId
     *//*
    public void setmodules(String[][] modules,long userId)
    {
    	UsersConfig uc = structureDAO.getUsersConfig(userId);
    	if (uc == null)
    	{
    		uc = new UsersConfig();
    		uc.setUser(structureDAO.findUserById(userId));
    		structureDAO.save(uc);
    		for (int col = 0; col < modules.length; col++) {
    			if(modules[col] == null)continue;
    			for (int row = 0; row < modules[col].length; row++) 
    			{
    				YzModule yzmoudle = (YzModule) normalDAO.find(YzModule.class, Long.parseLong(modules[col][row]));
    				UsersYzModule uyz = new UsersYzModule();
    				uyz.setUsersConfig(uc);
    				uyz.setYzmodule(yzmoudle);
    				uyz.setCol(col);
    				uyz.setRow(row);
    				normalDAO.save(uyz);
    			}
    		}
    	}
    	else
    	{
    		//先删除，
    		UsersYzModule[] yzModuleSet = uc.getYzModuleSet().toArray(new UsersYzModule[0]);
    		for (UsersYzModule usersYzModule : yzModuleSet) {
    			boolean has = false;
    			lable: for (int col = 0; col < modules.length; col++) {
    				if(modules[col] == null)continue;
        			for (int row = 0; row < modules[col].length; row++)
        			{
        				if(modules[col][row].equals(usersYzModule.getYzmodule().getMid()))//有，不删除//有问题。暂时不处理
        				{
        					has = true;
        					break lable;
        				}
        			}
    			}
    			if(!has)
    			normalDAO.delete(usersYzModule);
			}
    		//后添加
    		for (int col = 0; col < modules.length; col++) {
    			if(modules[col] == null)continue;
    			for (int row = 0; row < modules[col].length; row++)
    			{
    				int i = 0;
    				for (; i < yzModuleSet.length;i++) 
    				{
    					if(modules[col][row].equals(yzModuleSet[i].getYzmodule().getMid()))//有，不删除
        				{
        					break;
        				}
    				}
    				
    				if(i == yzModuleSet.length)//没有，新增加
    				{
    					YzModule yzmoudle = (YzModule) normalDAO.find(YzModule.class, Long.parseLong(modules[col][row]));
        				UsersYzModule uyz = new UsersYzModule();
        				uyz.setUsersConfig(uc);
        				uyz.setYzmodule(yzmoudle);
        				uyz.setCol(col);
        				uyz.setRow(row);
        				normalDAO.save(uyz);
    				}
    				else
    				{
    					UsersYzModule usersYzModule = yzModuleSet[i];
    					if(usersYzModule.getCol() != col || usersYzModule.getRow() != row)
    					{
    						usersYzModule.setRow(row);
    						usersYzModule.setCol(col);
    						normalDAO.update(usersYzModule);
    					}
    				}
    				
    			}
    		}
    	}
    }
    
    public ArrayList<YzModule> getDefaultYzModule()
    {
    	Properties properties = new Properties();
		try
		{
			properties.load(UserService.class.getClassLoader().getResourceAsStream("/conf/userconfiginitData.properties"));
			String value = new  String(properties.getProperty("default.module").getBytes("ISO-8859-1"),"utf8");
			ArrayList<YzModule> lists = new ArrayList<YzModule>();
			String[] ainfos = value.split(";");
			for (int i = 0; i < ainfos.length; i++) {
				String sql = "from YzModule as model where model.mpath = ?";
				lists.add((YzModule)normalDAO.findOneObjectBySql(sql, ainfos[i]));
			}
			return lists;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogsUtility.error(e);
		}
		return null;
    }
    
    public void initYzAPPandYzModule() throws Exception
    {
    	if(normalDAO.getCount(YzAPP.class) > 0)
    	{
    		return;
    	}
    	Properties properties = new Properties();
		try
		{
			properties.load(UserService.class.getClassLoader().getResourceAsStream("/conf/userconfiginitData.properties"));
			//apps
			String value = new  String(properties.getProperty("app.path").getBytes("ISO-8859-1"),"utf8");
			String[] apaths = value.split(";");
			value = new  String(properties.getProperty("app.name").getBytes("ISO-8859-1"),"utf8");
			String[] anames = value.split(";");
			value = new  String(properties.getProperty("app.info").getBytes("ISO-8859-1"),"utf8");
			String[] ainfos = value.split(";");
			value = new  String(properties.getProperty("app.keep").getBytes("ISO-8859-1"),"utf8");
			String[] akeeps = value.split(";");
			value = new  String(properties.getProperty("app.icon").getBytes("ISO-8859-1"),"utf8");
			String[] aicons = value.split(";");
			for (int i = 0; i < apaths.length; i++) {
				YzAPP yza = new YzAPP();
				if(i<apaths.length && apaths[i]!="")
				yza.setPath(apaths[i]);
				if(i<anames.length && anames[i]!="")
				yza.setName(anames[i]);
				if(i<ainfos.length && ainfos[i]!="")
				yza.setInfo(ainfos[i]);
				if(i<aicons.length && aicons[i]!="")
				yza.setIcon(aicons[i]);
				if(i<akeeps.length&& akeeps[i].equalsIgnoreCase("true"))
				{
					yza.setKeep(true);
				}
				else
				{
					yza.setKeep(false);
				}
				normalDAO.save(yza);
			}
			
			//Modules
			value = new String(properties.getProperty("modules.path").getBytes("ISO8859-1"), "utf8");
			String[] mpaths = value.split(";");
			value = new  String(properties.getProperty("modules.name").getBytes("ISO-8859-1"),"utf8");  
			String[] mnames = value.split(";");
			value = new  String(properties.getProperty("modules.info").getBytes("ISO-8859-1"),"utf8");
			String[] minfos = value.split(";");
			value = new  String(properties.getProperty("modules.tag").getBytes("ISO-8859-1"),"utf8");
			String[] mtags = value.split(";");
			for (int i = 0; i < mpaths.length; i++) {
				YzModule yzM = new YzModule();
				if(i<mpaths.length && mpaths[i]!="")
				yzM.setMpath(mpaths[i]);
				if(i<mnames.length && mnames[i]!="")
				yzM.setMname(mnames[i]);
				if(i<minfos.length && minfos[i]!="")
				yzM.setMinfo(minfos[i]);
				if(i<mtags.length && mtags[i]!="")
				yzM.setMtag(mtags[i]);
				normalDAO.save(yzM);
			}
			
			//style
			value = new String(properties.getProperty("style.name").getBytes("ISO-8859-1"),"utf8");
			String[] snames = value.split(";");
			value = new  String(properties.getProperty("style.info").getBytes("ISO-8859-1"),"utf8");
			String[] sinfos = value.split(";");
			value = new  String(properties.getProperty("style.path").getBytes("ISO-8859-1"),"utf8");
			String[] spaths = value.split(";");
			for (int i = 0; i < spaths.length; i++) {
				YzStyle yzs = new YzStyle();
				if(i<spaths.length && spaths[i]!="")
				yzs.setPath(spaths[i]);
				if(i<snames.length && snames[i]!="")
				yzs.setName(snames[i]);
				if(i<sinfos.length && sinfos[i]!="")
				yzs.setInfo(sinfos[i]);
				normalDAO.save(yzs);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogsUtility.error(e);
		}
    }*/
	//////////////////////////////////////////
	    
    
    /**
     * 获取用户的设备使用状态
     * @param mobDevID
     * @param userName
     * @return
     */
    public boolean isDisabledDevice(String mobDevID, String userName)
    {
    	Users user = structureDAO.isExistUser(userName);
        if (user == null) //用户不存在则认为允许该客户设备登录，继续走其他后续的账号校验流程
        {
            return false;
        }
        else
        {
            String sql = "select distinct t from UsersDevice t where t.user.id = ? and t.mobDevID like ?";//语法是否正确?
            UsersDevice dev = (UsersDevice)normalDAO.findOneObjectBySql(sql, user.getId(), mobDevID);
            if (dev == null)//没有该用户的设备记录则添加
            {
            	Long nums=normalDAO.getCountBySql("select count(t) from UsersDevice t where t.user.id = ? and t.mobDevStatus=?", user.getId(),(short)0);//如果用户一次都没有使用，允许第一次登录
                dev = new UsersDevice();
                dev.setMobDevID(mobDevID);
                dev.setMobDevName("未知");
                dev.setMobDevOS("未知");
                dev.setMobDevID(mobDevID);
                dev.setMobDevTime(Calendar.getInstance().getTime());
                dev.setUser(user);
                if (nums!=null && nums.longValue()>1)
                {
                	dev.setMobDevStatus((short)1);//首次添加进来为可用，后面再增加设备为禁用
                	normalDAO.save(dev);
                    return true;
                }
                else
                {
                	normalDAO.save(dev);
                    return false;
                }
                
            }
            else
            {
                return (dev.getMobDevStatus() == 1);
            }
        }
    }
    
    /**
     * 返回某用户的客户端设备登录记录
     * @param userid
     * @return
     */   
    public List<UsersDevice> getUserDeviceList(Long userid)
    {
        String sql = "select distinct t from UsersDevice t where t.user.id = ?";
        return normalDAO.findAllBySql(sql, userid);
    }
    
    /**
     * 禁用或启用某用户的移动设备
     * @param userid
     * @param role
     */
    @Deprecated
    public void forbidDevice(DataHolder mobDevIDS, short mobDevStatus)
    {
        long[] ids = mobDevIDS.getLongData();
        if (ids != null && ids.length > 0)
        {
            for (int i = 0; i < ids.length; i++)
            {
                UsersDevice dev = (UsersDevice)normalDAO.find(UsersDevice.class,new Long(ids[i]));
                dev.setMobDevStatus(mobDevStatus);
                normalDAO.update(dev);
            }
        }
    }
    /**
     * 禁用或启用某用户的移动设备
     * @param userid
     * @param mobDevStatus,1表示该设备需要禁用 0表示该设备正常
     */
    public void forbidDevice(List<Long> mobDevIDS, short mobDevStatus)
    {
    	structureDAO.forbidUserDevice(mobDevIDS, mobDevStatus);
    }
    
    /**
     * @throws Exception 
     * 
     */
    public void serReceptionPower(Long usrId){
    	
    	 try {
    	 Receptionpower receptionpower1 = new Receptionpower();
    	 Receptionpower receptionpower5 = new Receptionpower();
    	 Receptionpower receptionpower10 = new Receptionpower();
		 receptionpower1.setTypeid(0);
		 receptionpower5.setTypeid(0);
		 receptionpower10.setTypeid(0);
		 receptionpower1.setPowernum(1);
		 receptionpower5.setPowernum(5);
		 receptionpower10.setPowernum(10);

		 receptionpower1.setRpuserid(usrId);
		 receptionpower5.setRpuserid(usrId);
		 receptionpower10.setRpuserid(usrId);
		 receptionDao.savePower(receptionpower1);
		 receptionDao.savePower(receptionpower5);
		 receptionDao.savePower(receptionpower10);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    }
    
    /**
     * 获取用户的签名信息
     * @param userName
     * @return
     */
    public UsersConfig getSign(String userName)
    {
    	UsersConfig u = (UsersConfig)normalDAO.findOneObjectBySql(" Select u from UsersConfig as u where u.user.userName = ? ", userName);
    	return u;
    }
    
    /**
     * 更新用户的签名信息
     * @param userName
     * @param content
     */
    public int updateSign(String userName, byte[] content)
    {
    	UsersConfig u = (UsersConfig)normalDAO.findOneObjectBySql(" Select u from UsersConfig as u where u.user.userName = ? ", userName);
    	int ret = 0;
    	if (u != null)
    	{
    		Integer ret0 = u.getSignVersion();
    		ret = ret0 == null ? 1 : ret0 + 1;	
	    	u.setSignVersion(ret);
	    	u.setSign(content);
	    	normalDAO.update(u);
    	}
    	else
    	{
    		Users us = structureDAO.isExistUser(userName);
    		u = new UsersConfig();
    		u.setSignVersion(1);
    		u.setSign(content);
    		u.setUser(us);
    		structureDAO.save(u);
    		ret = 1;
    	}
    	return ret;
    }
    public void save(Object obj)
    {
	    structureDAO.save(obj);
    }
    public void update(Object obj)
    {
	    structureDAO.update(obj);
    }
    
    /**
     * 给定名字的公司是否存在
     * @param name
     * @return
     */
    public boolean isExistCompany(String name)
    {
    	return structureDAO.findCompanyByName(name) != null;
    }

    /**
     * 通过公司代码获取公司
     * @param code
     * @return
     */
    public Company getCompanyByCode(String code)
    {
    	return structureDAO.findCompanyByCode(code);
    }
    
    /**
     * 增加公司信息
     * @param company
     * @param admin
     */
    public int addCompany(Company company, Users admin)
    {
    	return addCompany(company, admin, true);
    }
    /**
     * 增加公司信息
     * @param company
     * @param admin
     */
    public int addCompany(Company company, Users admin, boolean defaultApps)
    {
    	structureDAO.deleteInvalidateRegister(); // 删除用户注册的，没有经过验证，且已经过了验证有效期的所有公司
    	if (structureDAO.findCompanyByName(company.getName()) != null)
    	{
    		return ErrorCons.USER_COMPANY_EXIST;
    	}
    	if (structureDAO.findCompanyByCode(company.getCode()) != null)
    	{
    		return ErrorCons.USER_COMPANY_CODE_EXIST;
    	}
    	if (structureDAO.isExistUser(admin.getUserName()) != null)
    	{
    		return ErrorCons.USER_EXITSEUSERNAME_ERROR;
    	}
    	Spaces space = createSpace(FileConstants.COMPANY_ROOT, company.getName(), company.getName(), "", "");
    	company.setSpaceUID(space.getSpaceUID());
    	structureDAO.save(company);    	
        permissionDAO.copySystemRoleActionToCompany(company.getId());    
        
        MD5 md5 = new MD5();
		String pass = md5.getMD5ofStr(admin.getPassW());
		admin.setPassW(pass);
		
        space = createSpace(FileConstants.USER_ROOT, admin.getUserName(), admin.getUserName(), "", "");
        admin.setSpaceUID(space.getSpaceUID());  
        if (WebConfig.mailEnable)
        {
        	admin.setRealEmail(admin.getUserName() + "@" + WebConfig.mailDomain);
        } 
        // 加入用户的公钥与私钥
        try 
        {        	
        	Map<String,String> keyMap = RSACoder.generateKey();
        	admin.setPrivateKey(keyMap.get("RSAPrivateKey"));
        	admin.setPublicKey(keyMap.get("RSAPublicKey"));
		}
        catch (Exception e) 
        {
        	LogsUtility.error(e);
		}
    	admin.setCompany(company); 
    	structureDAO.save(admin);
    	
    	if (defaultApps)
    	{
	    	AppsService appsService = (AppsService)ApplicationContext.getInstance().getBean(AppsService.NAME);
	    	appsService.copySystemAppToCompany(company);
    	}
    	
    	return ErrorCons.NO_ERROR;
    }
        
    /**
     * 用户注册公司帐号
     * @param company
     * @param admin
     * @return
     */
    public int registerCompany(Company company, Users admin, String url)
    {    	
    	int ret = addCompany(company, admin);
    	if (ret == ErrorCons.NO_ERROR)
    	{
    		try
    		{
    			boolean t = MailSender.sendRegisterCompanyMail(company, admin, url, true);
    			if (!t)    // 发送邮件失败
    			{
    				//structureDAO.delete(company);
        			return ErrorCons.MANAGE_COMANY_REGISTER_ERROR;
    			}
    		}
    		catch(Throwable e)
    		{
    			//structureDAO.delete(company);
    			return ErrorCons.MANAGE_COMANY_REGISTER_ERROR;
    		}
    	}
    	return ret;
    }
        
    /**
     * 获取系统中的所有公司
     * @return
     */
    public List<Company> getCompanyList()
    {
    	return structureDAO.findAll(Company.class);
    }
    
    /**
	 * 查询公司名字中有name的所有公司
	 * @param name
	 * @return
	 */
	public List<Company> searchCompanyByName(String name)
	{
		return structureDAO.searchCompanyByName(name);
	}
	
	/**
     * 获取系统公司
     * @return
     */
    public Company getCompany(long Id)
    {
    	return structureDAO.findCompanyById(Id);
    }
	
	/**
	 * 删除公司
	 * @param id
	 */
	public void deleteCompany(long id)
	{
		structureDAO.deleteEntityByID(Company.class, "id", id);
	}
	
	/**
	 * 删除公司
	 * @param id
	 */
	public void deleteCompany(List<Long> ids)
	{
		structureDAO.deleteEntityByID(Company.class, "id", ids);
	}
	
	/**
	 * 获取公司的管理员用户帐号，目前系统中只支持一个公司只有一个管理员
	 * @param companyId
	 * @return
	 */
	public Users getCompanyAdminUser(long companyId)
	{
		return structureDAO.getCompanyAdminUser(companyId);
	}
	
	/**
	 * 增加新的组织结构
	 * @param parentId
	 * @param org
	 * @param addUserIds
	 * @param leaderId
	 * @return
	 */
	public void addOrganization(Company company, Long parentId, Organizations org, Long[] addUserIds, Users leader)
	{
		
		Spaces retSpace = createSpace(FileConstants.ORG_ROOT, org.getName(), org.getName(), "", "");
		org.setSpaceUID(retSpace.getSpaceUID());
		if (parentId != null)
        {
			Organizations parent = structureDAO.findOrganizationsById(parentId);
			org.setParent(parent);
		}
		org.setManager(leader);
		org.setCompany(company);
		structureDAO.save(org);
		Long orgId = org.getId();
		permissionDAO.copySystemRoleActionToOrg(orgId, company.getId());
		if (addUserIds != null)
		{
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				if (structureDAO.findUserOrganization(addUserIds[i], org.getId()) != null)   // 已经存在
				{
					continue;
				}
				Users or = structureDAO.findUserById(addUserIds[i]);
				UsersOrganizations uog = new UsersOrganizations(or, org);
				structureDAO.save(uog);
			}
		}
		upOrgnizeCode(org);//更新organizecode
	}
	
	/**
	 * 获取公司的部门组织
	 * @param companyId
	 * @param parentId
	 * @return
	 */
	public List<Organizations> getOrganizations(Long companyId, Long parentId, boolean treeFlag)
	{
		return structureDAO.getChildOrganizations(parentId, companyId, treeFlag);		
	}
	
	/**
     * 获取系统中用户信息。如果start和count不小于0，则表示分页获取。
     * @param start 开始的位置，如果小于0，在表示从第一条记录开始获取。
     * @param count 一次获取的数量，如果小于0，则表示从start开始后的所有用户数据。
     * @param sort 排序字段名
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> getCompanyUsers(long companyId, int start, int count, String sort, String dir)
    {
    	return structureDAO.getUsers(companyId, start, count, sort, dir);
    }
    
    /**
     *获取公司的总用户数
     * @return
     */
    public Long getUsersCount(Long companyId)
    {
    	return structureDAO.getUsersCount(companyId);
    }
	
    /**
     * 根据组织Id得到组织中的所有用户，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。如果start和count不小于0，则表示分页获取。
     * @param start 开始的位置，如果小于0，在表示从第一条记录开始获取。
     * @param count 一次获取的数量，如果小于0，则表示从start开始后的所有用户数据。
     * @param sort 排序字段名
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> getOrgUsers(Long companyid,Long orgId, boolean treeFlag, int start, int count, String sort, String dir,String searchcond)
    {
    	return structureDAO.findUsersByOrgId(companyid,orgId, treeFlag, start, count, sort, dir,searchcond);
    }
    /**
	 * 根据组织Id得到组织中的所有用户总数，如果treeFlag为true则递归查询组织中所有子组织中
	 * 的用户，为false，则值查询该级组织中的用户。
	 * @param orgId
	 * @return
	 */
	public Long findUsersCountByOrgId(Long companyid,Long orgId, boolean treeFlag,String searchcond)
	{
		return structureDAO.findUsersCountByOrgId(companyid,orgId, treeFlag,searchcond);
	}
    
    /**
     * 更新部门组织信息
     * @param parentId
     * @param orgId
     * @param leader
     * @param name
     * @param desc
     */
    public void updateOrganization(Long parentId, Long orgId, Long leaderId, String name, String desc, Integer sortNumber)
	{
    	Organizations org = structureDAO.findOrganizationsById(orgId);
    	Organizations parent = parentId != null ? structureDAO.findOrganizationsById(parentId) : null;
		org.setParent(parent);
		Users leader = leaderId != null ? structureDAO.findUserById(leaderId) : null;
		org.setManager(leader);
		org.setName(name);
		org.setDescription(desc);
		if (sortNumber != null)
		{
			org.setSortNum(sortNumber);
		}
		structureDAO.update(org);
		upOrgnizeCode(org);//更新organizecode
	}
    
    /**
     * 更新公司部门组织的成员信息
     * @param delUserIds
     * @param addUserIds
     */
    public void updateOrgUserList(Long orgId, List<Long> delUserIds, List<Long> addUserIds)
    {
    	if (delUserIds != null && delUserIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(orgId);
			structureDAO.deleteUsersOrganizationsByOrgId(ids, delUserIds);
		}
		if (addUserIds != null && addUserIds.size() > 0)
		{
			Organizations organization = structureDAO.findOrganizationsById(orgId);
			int size = addUserIds.size();
			Long uid;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds.get(i);
				if (structureDAO.findUserOrganization(uid, organization.getId()) != null)   // 已经存在
				{
					continue;
				}
				Users or = structureDAO.findUserById(uid);
				UsersOrganizations uog = new UsersOrganizations(or, organization);
				structureDAO.save(uog);						
			}
		}
    }
    
    /**
	  * 搜索公司下的部门组织名字
	  * @param key
	  * @param companyId
	  * @return
	  */
	public List<Organizations> findOrganizationsByKey(String key, Long companyId)
	{
		return structureDAO.findOrganizationsByKey(key, companyId);
	}
	
	/**
     * 搜索符合keyword条件的用户，并根据需要分页显示。 
     * @param option 搜索的方式，0表示搜索用户名，1表示搜索邮件地址，其他值表示即搜索用户名，也搜索邮件地址。
     * @param keyWord 在搜索时候，用户名或邮件地址中包含有的关键字。
     * @param start 搜索的开始位置，小于0，表示从第一个记录开始搜索。
     * @param count 搜索的数量，小于0，表示从start位置开始后的所有记录。
     * @param sort 排序字段
     * @param dir 排序的方式（asc或desc）
     * @return
     */
    public List<Users> searchUser(Long companyId, int option, String keyWord, int start, int count, String sort, String dir)
    {
    	return structureDAO.searchUser(companyId, option, keyWord, start, count, sort, dir);
    }
    
    /**
     * 设置用户是否有效
     * @param userId
     * @param role 1为有效，其他值为无效
     */
    public void forbidUser(Long userId, short role)
    {
    	Users user = structureDAO.findUserById(userId);
    	user.setValidate(role);
    	structureDAO.update(user);
    }
    
    /**
     * 设置用户是否有效
     * @param userId
     * @param role 1为有效，其他值为无效
     */
    public void forbidUser(List<Long> userIds, short role)
    {
    	if (userIds != null && userIds.size() > 0)
    	{
    		structureDAO.forbidUserById(userIds, role);
    	}
    }
    
    /**
     * 判断公司的允许的最大用户数是否已经满了
     * @param companyId
     * @return 运行的最大人数已经满了，则返回true，否则返回false。
     */
    public boolean isCompanyUserFull(Long companyId)
    {
    	return structureDAO.isCompanyUserFull(companyId);
    }
    
    /**
	 * 增加或修改用户的信息，user对象为修改用户的信息内容，addOrgIds为添加用户在组织中，
	 * delOrgId为删除用户在组织中。newRoleId为用户新的角色Id值，oldRoleId为用户旧的角色Id值。
	 * @param user
	 * @param addOrgIds
	 * @param delOrgIds
	 * @param newRoleId
	 * @param oldRoleId
	 */
	public void addOrUpdateUser(Long companyId, Users user, List<Long> addOrgIds, List<Long> delOrgIds, Long newRoleId, Long oldRoleId)
	{
		Long userId = user.getId();
		Users entity;		
		if (userId == null)
		{
			Company company = structureDAO.findCompanyById(companyId);
			String p = user.getResetPass();
			user.setRealPass(p);
			if (p != null && p.length() > 0)
			{
				MD5 md5 = new MD5();
				p = md5.getMD5ofStr(p);
				user.setPassW(p);
			}

			user.setCompany(company);
			String spaceUID = createSpace(FileConstants.USER_ROOT, user.getUserName(), user.getUserName(), "", "").getSpaceUID();			
            user.setSpaceUID(spaceUID);
            //加入用户的公钥与私钥
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
			structureDAO.save(user);			
			if (newRoleId != null)
			{
				Roles role = structureDAO.findRoleById(newRoleId);
				UsersRoles ur = new UsersRoles(user, role);
				structureDAO.save(ur);
			}
						
			userId = user.getId();
			entity = user;
			
			AppsService appsService = (AppsService)ApplicationContext.getInstance().getBean(AppsService.NAME);
			appsService.copyCompanyAppToUser(companyId, user);
			
			//增加默认首页
			String[] desks=new String[]{"我的文库,my,css/img/desk/my.png/我的文库"
					,"待办文档,todo,css/img/desk/vet_todo.png/待办文档"
					,"已办文档,done,css/img/desk/vet_done.png/已办文档"
					,"我的送文,myquest,css/img/desk/myrequest.png/我的送文"
					,"他人共享,shared,css/img/desk/shared.png/他人共享"
					,"协作共享,grouphome,css/img/desk/grouphome.png/协作共享"
					,"我的共享,sharing,css/img/desk/sharing.png/我的共享"
					};//先临时这样写，以后要独立到资源包中
			for (int i=0;i<desks.length;i++)
			{
				UserDesks userDesks=new UserDesks();
				String[] strs=desks[i].split(",");
		    	userDesks.setDisplayname(strs[0]);
		    	userDesks.setHashtag(strs[1]);
		    	userDesks.setPaths(strs[2]);
		    	userDesks.setSourcetype(-1);
		    	userDesks.setUser(user);
		    	structureDAO.save(userDesks);
			}
		}
		else
		{
			entity = structureDAO.findUserById(userId);
			String p = user.getResetPass();
			user.setRealPass(p);
			if (p != null && p.length() > 0)
			{
				MD5 md5 = new MD5();
				p = md5.getMD5ofStr(p);
				entity.setPassW(p);
			}
			entity.update(user);
			structureDAO.update(entity);
			
			if (!((newRoleId == null && oldRoleId == null) 
					|| (newRoleId != null && oldRoleId != null && newRoleId.longValue() == oldRoleId.longValue()))) 
			{
				if (oldRoleId != null)
				{
					UsersRoles ur = structureDAO.findUserRole(userId, oldRoleId);
					structureDAO.delete(ur);
				}
				if (newRoleId != null)
				{
					Roles role = structureDAO.findRoleById(newRoleId);
					UsersRoles ur = new UsersRoles(user, role);
					structureDAO.save(ur);
				}
			}			
		}
		if (delOrgIds != null && delOrgIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(userId);
			structureDAO.deleteUsersOrganizationsByOrgId(delOrgIds, ids);
		}
		if (addOrgIds != null && addOrgIds.size() > 0)
		{
			for (Long org : addOrgIds)
			{
				Organizations or = structureDAO.findOrganizationsById(org);
				UsersOrganizations uog = new UsersOrganizations(entity, or);
				structureDAO.save(uog);				
			}
		}
		
	}
	
	/**
	 * 增加或修改用户的信息，addOrgIds为添加用户在组织中，delOrgId为删除用户在组织中。
	 * @param user
	 * @param addOrgIds
	 * @param delOrgIds
	 * @param newRoleId
	 * @param oldRoleId
	 */
	public void addOrUpdateUser(Long companyId, Long userId, List<Long> addOrgIds, List<Long> delOrgIds)
	{
		Users entity = structureDAO.findUserById(userId);
		if (delOrgIds != null && delOrgIds.size() > 0)
		{
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(userId);
			structureDAO.deleteUsersOrganizationsByOrgId(delOrgIds, ids);
		}
		if (addOrgIds != null && addOrgIds.size() > 0)
		{
			for (Long org : addOrgIds)
			{
				Organizations or = structureDAO.findOrganizationsById(org);
				UsersOrganizations uog = new UsersOrganizations(entity, or);
				structureDAO.save(uog);				
			}
		}
		
	}
	
	/**
	 * 获取用户当前的系统角色,目前前端只支持一个系统角色
	 * @param isTemp
	 * @return
	 */
	public UsersRoles findUserRoleInSystem(Long userId, Long companyId)
	{
		return structureDAO.findUserRoleInSystem(userId, companyId);
	}	
	
	/**
	 * 获取发布的文件路径
	 * @param key
	 * @return
	 */
	public String getPublishAddress(String key)
	{
		PublishAddress pa = (PublishAddress)normalDAO.findOneObjectBySql("from PublishAddress as pa where pa.key = ? ", key); 
		if (pa == null)
		{
			return null;
		}
		Date date = pa.getDate();
		Long current = System.currentTimeMillis();
		if (date == null || date.getTime() < current)     // 已经过期
		{
			return "";
		}
		return pa.getInnerPath();
	}
	
	/**
	 * 新建或修改用户在企业空间中的角色，addUserIds需要同roleIds中的值对应,如果对应值为null，则是删除已有角色
	 */
	public void addOrUpdateCompanyRoles(Long companyId, Long[] addUserIds, Long[] roleIds)
	{
		if (addUserIds != null)
		{
			Roles role;
			Long uid;
			int size = addUserIds.length;
			for (int i = 0; i < size; i++)
			{
				uid = addUserIds[i];
				Users or = structureDAO.findUserById(uid);
				
				if (roleIds == null || roleIds[i] == null || roleIds[i] < 0)
				{
					UsersRoles ur = structureDAO.findUserRoleInCompany(uid, companyId) ;
					if (ur != null)
					{
						structureDAO.delete(ur);
					}
				}
				else if (roleIds != null && roleIds[i] != null)
				{
					structureDAO.delUserCompanyRole(uid, companyId);
					role = structureDAO.findRoleById(roleIds[i]);
					UsersRoles ur = new UsersRoles(or, role);
					structureDAO.save(ur);
				}
			}
		}
	}
	
	
	
	 /**
     *  通过公司名称获取公司实体
     * @param name
     *       公司名称
     * @return 
     *      公司实体或null
     */
    public Company getCompanyByName(String name){
    	    return this.structureDAO.findCompanyByName(name);
    }
    
    /**
     * 通过公司、父部门节点和部门名称获取部门
     *      默认获取第一个
     *      null 表示没有对应的部门
     * @param company
     *           公司
     * @param parent
     *            父部门
     * @param orgName
     *           部门名称
     * @return
     *       部门实体
     */
    public Organizations getOrganizations(Company company,Organizations parent,String orgName){
    	String sql="from Organizations o where o.name='"+orgName+"' and o.parent=null and o.company.id="+company.getId();
    	if(parent!=null){
    		 sql="from Organizations o where o.name='"+orgName+"' and o.parent.id="+parent.getId()+" and o.company.id="+company.getId();
    	}
    	List<Organizations> list=structureDAO.findAllBySql(sql,null);
    	return (list==null||list.isEmpty())?null:list.get(0);
    }
    
    /*
     * 他人共享中筛选获取共享者
     */
    public List<Map<String, Object>> getSharer(Long userId)
    {
    	List<Users> sharers = personshareinfoDAO.findByShareUsers(userId,-1l);
    	int sharerListSize = 0;
    	List<Map<String, Object>> sharerList = new ArrayList<Map<String,Object>>();
    	for (Users user : sharers) {
			Map<String, Object> sharer = new HashMap<String, Object>();
			sharer.put("id", user.getId());
			sharer.put("realName", user.getRealName());
			sharerList.add(sharer);
			sharerListSize++;
		}
    	final int size = sharerListSize;
    	Map sharerSize = new HashMap(){
    		{
    			put("sharerSize",size);
    		}
    	};
    	sharerList.add(sharerSize);
    	return sharerList;
    }
    public Users getUserByname(String outname)
	{
		return structureDAO.findUserByname(outname);
	}
    public Organizations getOrganizationsBydepid(String depid)
	{
		List<Organizations> list=structureDAO.findOrganizationsBydepid(depid);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
    public void delUsersOrganizations(Long userId)
    {
    	if (userId != null )
    	{
    		structureDAO.delUsersOrganizations(userId);
    	}
    }
    public Roles getRoleByUser(Long userId)
	{
		List<Roles> list=structureDAO.getRoleByUser(userId);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
    public Organizations getOrgBydepid(String depid)
    {
    	List list = structureDAO.findOrganizationsByOrgProperty("depid", depid);
    	if (list == null || list.size() < 1)
    	{
    		return null;
    	}
    	return (Organizations)list.get(0);
    }
    public void delUsersOrganizationsByOrg(Long orgid)
    {
    	if (orgid != null )
    	{
    		structureDAO.delUsersOrganizationsByOrg(orgid);
    	}
    }
    public List<Users> getAllNormalUser()
	{
		return structureDAO.getAllNormalUser();
	}
    public List<Organizations> getAllNormalOrganizations()
	{
		return structureDAO.getAllNormalOrganizations();
	}
}

