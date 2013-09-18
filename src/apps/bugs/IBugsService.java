package apps.bugs;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.bug.BugActions;
import apps.transmanager.weboffice.databaseobject.bug.BugFiles;
import apps.transmanager.weboffice.databaseobject.bug.BugHistories;
import apps.transmanager.weboffice.databaseobject.bug.BugInfos;
import apps.transmanager.weboffice.databaseobject.bug.BugModifyInfos;


public interface IBugsService {

	public void save(Object entity) throws Exception;
	public void update(Object entity) throws Exception;
	public void deleteByID(Long id);
	public BugInfos findById(Long id);
	public List<BugFiles> findFilesByBugId(Long bugid);
	public void savehistory(BugHistories history) throws Exception;
	public List<BugInfos> findByBugs(BugInfos bugInfos,Users user,int start,int count,String sort);
	public int findByBugsize(BugInfos bugInfos,Users user);
	public Long gethistorysize(Long bugid) throws Exception;
	public List<BugHistories> gethistory(Long bugid,int start,int count,String sort) throws Exception;
	public Map<String,Object> getRootGroupId(Long userid) throws Exception;
	public Organizations getGroupByuserId(Long userid);
	public Organizations getRootGroupByCode(String groupcode);
	public List<BugFiles> findBugFilesById(Long bugid);
	public void saveFiles(List<BugFiles> fileList);
	public void delfiles(String[] bugfileids);
	public List<Users> getReportList() throws Exception;
	
	public BugModifyInfos findModifyById(Long bugid);
	public List<BugActions> findActions(Long stateid);
	public List<Users> findOwnerUsers();
	public boolean ismodifyUser(Long userid);
	public Object findObjById(Class entity,Long id);
	public Users getCompanyAdmin(Long company_id);
	
}
