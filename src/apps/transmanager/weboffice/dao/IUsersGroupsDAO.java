package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.UsersGroups;
import apps.transmanager.weboffice.util.beans.Page;

public interface IUsersGroupsDAO extends IBaseDAO<UsersGroups> {
	
	/**
	 * 得到项目组里面的联系人
	 * @param teamId 项目组ID
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人id
	 */
	List<Long> findByTeamGroup(Long teamId,Page page, String sort, String order);

}
