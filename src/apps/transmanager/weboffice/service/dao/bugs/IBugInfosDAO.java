package apps.transmanager.weboffice.service.dao.bugs;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.bug.BugInfos;

public interface IBugInfosDAO extends IBaseDAO<BugInfos>{

	public void delete(BugInfos persistentInstance);
	public void deleteByID(Integer id);
	public BugInfos findById(Integer id);
	public List findByExample(BugInfos instance);
	public List findByProperty(String propertyName, Object value);
	public List findByBugs(BugInfos bugInfos, int start,int count,String sort);
	public int findByBugsize(BugInfos bugInfos,Users user);
	public int[] totalBugs(BugInfos bugInfos,Users user);
	
}
