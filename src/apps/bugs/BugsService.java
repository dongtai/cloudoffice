package apps.bugs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.bug.BugActions;
import apps.transmanager.weboffice.databaseobject.bug.BugFiles;
import apps.transmanager.weboffice.databaseobject.bug.BugHistories;
import apps.transmanager.weboffice.databaseobject.bug.BugInfos;
import apps.transmanager.weboffice.databaseobject.bug.BugModifyInfos;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.dao.bugs.BugInfosDAO;

@Component(value=BugsService.NAME)
public class BugsService implements IBugsService {
	public final static String NAME = "bugsService";
	
	@Autowired
	private StructureDAO  structureDAO;
	@Autowired
	private BugInfosDAO bugInfosDao;
	
	public BugInfosDAO getBugInfosDao() {
		return bugInfosDao;
	}
	public void setBugInfosDao(BugInfosDAO bugInfosDao) {
		this.bugInfosDao = bugInfosDao;
	}
	public void save(Object entity) throws Exception
	{
		structureDAO.save(entity);
	}
	public void update(Object entity) throws Exception
	{
		structureDAO.update(entity);
	}
	public void deleteByID(Long id)
	{
		structureDAO.excute("update BugInfos set isdelete=1 where id=?",id);
	}
	public BugInfos findById(Long id)
	{
		return (BugInfos)structureDAO.find(BugInfos.class, id);
	}
	public List<BugFiles> findFilesByBugId(Long bugid)
	{
		return (List<BugFiles>)structureDAO.findAllBySql("select a from BugFiles as a where a.isdelete=0 and a.bugInfos.id=?", bugid);
	}
	public void savehistory(BugHistories history) throws Exception
	{
		structureDAO.save(history);
	}
	public List<BugInfos> findByBugs(BugInfos bugInfos,Users user,int start,int count,String sort)
	{
		return bugInfosDao.findByBugs(bugInfos,user, start, count, sort);
//		List<BugInfos> list=(List<BugInfos>)structureDAO.findAllBySql(start, count, "select a from BugInfos as a where a.isdelete=?", 0);
//		return list;
	}
	public int findByBugsize(BugInfos bugInfos,Users user)
	{
		return bugInfosDao.findByBugsize(bugInfos,user);
//		Long num=(Long)structureDAO.getCountBySql("select count(a) from BugInfos as a where a.isdelete=?", 0);
//		return num.intValue();
	}
	
	
	public Long gethistorysize(Long bugid) throws Exception
	{
		return (Long)structureDAO.getCountBySql("select count(a) from BugHistories as a where a.bugid=? ", bugid);
	}
	public List<BugHistories> gethistory(Long bugid,int start,int count,String sort) throws Exception
	{
		return (List<BugHistories>)structureDAO.findAllBySql(start, count, "select a from BugHistories as a where a.bugid=? ", bugid);
	}
	
	public Map<String,Object> getRootGroupId(Long userid) throws Exception
	{
		Map<String,Object> groupIDsAndRootCode = new HashMap<String, Object>(); 
		Long[] result=new Long[2];
		List<UsersOrganizations> list = structureDAO.findUsersOrganizationsByUserId(Long.valueOf(userid.intValue()));
		Organizations groupinfo=null;
		if (list!=null && list.size()>0)
		{
			groupinfo=list.get(0).getOrganization();
			result[0]=groupinfo.getId();
			String rootcode="000";
	   		rootcode=groupinfo.getOrganizecode();
	   		int index=rootcode.indexOf("-");
	   		if (index>0)
	   		{
	   			rootcode=rootcode.substring(0,index);
	   		}
	   		List<Organizations> grouplist=structureDAO.findOrganizationsByOrgProperty("organizecode", rootcode);
	   		if (grouplist!=null && grouplist.size()>0)
	   		{
	   			result[1]=grouplist.get(0).getId();
	   		}
	   		groupIDsAndRootCode.put("groupIDs", result);
	   		groupIDsAndRootCode.put("depRootCode", rootcode);
		}
		return groupIDsAndRootCode;
	}
	
	public Organizations getGroupByuserId(Long userid)
	{
		List<UsersOrganizations> list =structureDAO.findUsersOrganizationsByUserId(userid);
		if (list!=null)
		{
			return list.get(0).getOrganization();
		}
		return null;
	}
	
	public Organizations getRootGroupByCode(String groupcode)
	{
		List<Organizations> list=structureDAO.findOrganizationsByOrgProperty("organizecode", groupcode);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}


	public List<BugFiles> findBugFilesById(Long bugid) {
		List<BugFiles> bugFiles = null;
		try {
			bugFiles = (List<BugFiles>)structureDAO.findByProperty(BugFiles.class, "bugInfos.id", bugid, -1, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugFiles;
	}
	public void saveFiles(List<BugFiles> fileList) {
		structureDAO.saveAll(fileList);
		
	}
	public void delfiles(String[] bugfileids)//删除附件
	{
		if (bugfileids!=null)
		{
			for (int i=0;i<bugfileids.length;i++)
			{
//				BugFiles bugFiles=(BugFiles)structureDAO.find(BugFiles.class, Long.valueOf(bugfileids[i]));
//				bugFiles.setIsdelete(1);
				structureDAO.excute("update BugFiles set isdelete=1 where id="+bugfileids[i]);
			}
		}
	}
	public List<Users> getReportList() throws Exception
	{
		List<Users> list=(List<Users>)structureDAO.findAllBySql("select distinct a from Users as a,BugInfos as b where a.id=b.userid ");
		return list;
	}
	public BugModifyInfos findModifyById(Long bugid)
	{
		List<BugModifyInfos> list=(List<BugModifyInfos>)structureDAO.findAllBySql("select a from BugModifyInfos as a where a.bugInfos.id=?", bugid);
		if (list!=null)
		{
			return list.get(0);
		}
		return null;
	}
	public List<BugActions> findActions(Long stateid)//获取当前状态对应的动作列表
	{
		List<BugActions> list=(List<BugActions>)structureDAO.findAllBySql("select a.bugActions from StateActions as a where a.bugStates.id=?", stateid);
		return list;
	}
	public List<Users> findOwnerUsers()//获取所有处理BUG的用户列表，目前不进行组织筛选
	{
		List<Users> list=(List<Users>)structureDAO.findAllBySql("select a.owners from BugOwnerUsers as a order by a.owners.userName ");
		return list;
	}
	public boolean ismodifyUser(Long userid)
	{
		List<Users> list=(List<Users>)structureDAO.findAllBySql("select a.owners from BugOwnerUsers as a where a.owners.id=? ",userid);
		if (list!=null && list.size()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public Object findObjById(Class entity,Long id)
	{
		return structureDAO.find(entity, id);
	}
	public Users getCompanyAdmin(Long company_id)//获取单位管理员
	{
		List<Users> list=(List<Users>)structureDAO.findAllBySql("select a from Users as a where a.company.id=? and a.role=8 ", company_id);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
	/**
	 * @param structureDAO the structureDAO to set
	 */
	public void setStructureDAO(StructureDAO structureDAO) {
		this.structureDAO = structureDAO;
	}
	/**
	 * @return the structureDAO
	 */
	public StructureDAO getStructureDAO() {
		return structureDAO;
	}
	
}
