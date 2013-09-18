package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IUsersGroupsDAO;
import apps.transmanager.weboffice.databaseobject.UsersGroups;
import apps.transmanager.weboffice.util.beans.Page;

public class UsersGroupsDAO extends BaseDAOImpl<UsersGroups> implements IUsersGroupsDAO{

	/**
	 * 得到项目组里面的联系人
	 * @param teamId 项目组ID
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人id
	 */
	@Override
	public List<Long> findByTeamGroup(Long teamId, Page page, String sort,String order) {
		int totalRecord = (int)findCountByTeamGroup(teamId,page);
		page.setTotalRecord(totalRecord);
		StringBuffer queryString = new StringBuffer("select t.user.id from UsersGroups as t where t.group.id = ");
		queryString.append(teamId);
		if(sort!=null)
		{
			queryString.append(" order by ").append(sort).append(" ").append(order);
		}else{
			queryString.append(" order by t.user.userName ASC");
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return (List<Long>)query.list();

	}
	
	/**
	 * 获得项目组人员数目
	 * @param depId 部门ID
	 * @param page 分页类
	 * @return 联系人数目
	 */
	private long findCountByTeamGroup(Long teamId,Page page) {
		StringBuffer queryString = new StringBuffer("select count(distinct t.user) from UsersGroups as t where t.group.id =");
		queryString.append(teamId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}

}
