package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IDepartmentDAO;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.domain.DepartmentPo;

public class DepartmentDAO extends BaseDAOImpl<DepartmentPo> implements IDepartmentDAO {
	
	/**
	 * 获得中带有关键字的组名
	 * @param key 关键字
	 * @return 部门列表
	 */
	@SuppressWarnings("unchecked")
	public List<DepartmentPo> searchOrganizeListByKey(String key) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DepartmentPo as t where t.name like '%").append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return (List<DepartmentPo>)query.list();
	}
	/**
	 * 根据父组织查子组织，按sortNum，issub排序
	 */
	public List<DepartmentPo> findOrgByParentId(Long parentId)
	{
		if (parentId==0)
		{
			//查所有一级单位
			String SQL="from DepartmentPo as t where t.parent.id is null order by t.issub desc ,t.sortNum ";
			Query query = getSession().createQuery(SQL);
//			query.setLong(0,parentId );
			List<DepartmentPo> list = query.list();
			return list;
		}
		else
		{
			String SQL="from DepartmentPo as t where t.parent.id=? order by t.sortNum ";
			Query query = getSession().createQuery(SQL);
			query.setLong(0, parentId);
			return query.list();
		}
	}
	@Override
	public List<DepartmentPo> searchOrgListByKey(Long companyId, String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 通过用户id，查询用户所在的组织结构
	 * @param userId
	 * @return
	 */
	@Override
	public List<Organizations> findOrgByUserId(Long userId) {
		String queryString = "select model.organization from UsersOrganizations as model where model.user.id =:userId";
		Query query = getSession().createQuery(queryString);
		query.setParameter("userId", userId);
		return query.list();
	}
}
