package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IDepMemberDAO;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.util.beans.Page;

@SuppressWarnings("unchecked")
public class DepMemberDAO extends BaseDAOImpl<DepMemberPo> implements
		IDepMemberDAO {

	/**
	 * 获取所有签批领导
	 */
	public List<DepMemberPo> findOrgUsers(Long orgid) {
		String SQL = "from DepMemberPo as model where model.organization.id=? order by model.user.sortNum ";
		Query query = getSession().createQuery(SQL);
		query.setLong(0, orgid);
		return query.list();
	}

	public List<DepMemberPo> searchByKey(String key) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DepMemberPo where user.realName like '%")
				.append(key).append("%'").append("or user.userName like '%")
				.append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}

	public List<DepMemberPo> searchByKeyAndCompany(String key, Company company) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DepMemberPo where user.company.id=")
				.append(company.getId()).append(" and (user.realName like '%")
				.append(key).append("%'").append("or user.userName like '%")
				.append(key).append("%')");
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}
	public List<Long> findByDepartment(DepartmentPo departmentPo, Page page,
			String sort, String order)
	{
		return findByDepartment(departmentPo, page,
				sort, order,0);
	}
	/**
	 * 得到部门里面的联系人
	 * 
	 * @param depId
	 *            部门ID
	 * @param page
	 *            分页类
	 * @param sort
	 *            排序关键字
	 * @param order
	 *            排序顺序
	 * @return 联系人id
	 */
	public List<Long> findByDepartment(DepartmentPo departmentPo, Page page,
			String sort, String order,int usertype) {
		int totalRecord = (int) findCountByDepartment(departmentPo, page);
		page.setTotalRecord(totalRecord);
		DepartmentPo parent = departmentPo;
		Long depId = departmentPo.getId();
		
		if (usertype==0) {
			String key = parent.getParentKey();
			if (key == null) {
				key = "";
			}
			key += parent.getId() + "-%";
			StringBuffer queryString = new StringBuffer(
					"select distinct t.user.id from DepMemberPo as t where t.organization.id = ");
			queryString.append(depId).append(" or t.organization.parentKey like '")
					.append(key).append("'");
			if (sort != null) {
				queryString.append(" order by ").append(sort).append(" ")
						.append(order);
			} else {
				queryString.append(" order by t.user.userName ASC");
			}
			Query query = getSession().createQuery(queryString.toString());
			query.setFirstResult(page.getCurrentRecord());
			query.setMaxResults(page.getPageSize());
			return (List<Long>) query.list();
		}
		else
		{
			return new ArrayList<Long>();
		}
	}

	/**
	 * 获得部门人员
	 * 
	 * @param depId
	 *            部门ID
	 * @param page
	 *            分页类
	 * @return 联系人数目
	 */
	private long findCountByDepartment(DepartmentPo departmentPo, Page page) {
		DepartmentPo parent = departmentPo;
		Long depId = departmentPo.getId();
		String key = parent.getParentKey();
		if (key == null) {
			key = "";
		}
		key += parent.getId() + "-%";
		StringBuffer queryString = new StringBuffer(
				"select count(distinct t.user) from DepMemberPo as t where t.organization.id = ");
		queryString.append(depId).append(" or t.organization.parentKey like '")
				.append(key).append("'");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	public List<Long> findByDepAll(Long companyId, Page page, String sort,
			String order)
	{
		return findByDepAll(companyId, page, sort,order,0);
	}
	/**
	 * 得到部门里面的联系人
	 * 
	 * @param page
	 *            分页类
	 * @param sort
	 *            排序关键字
	 * @param order
	 *            排序顺序
	 * @return 联系人id
	 */
	public List<Long> findByDepAll(Long companyId, Page page, String sort,
			String order,int usertype) {
		int totalRecord = (int) findCountByDepAll(companyId, page);
		page.setTotalRecord(totalRecord);
		if (usertype==0)
		{
			StringBuffer queryString = new StringBuffer(
					"select distinct t.user.id from DepMemberPo as t where t.organization.id > 0 and t.user.company.id="
							+ companyId);
			if (sort != null) {
				queryString.append(" order by ").append(sort).append(" ")
						.append(order);
			} else {
				queryString.append(" order by t.user.userName ASC");
			}
			Query query = getSession().createQuery(queryString.toString());
			query.setFirstResult(page.getCurrentRecord());
			query.setMaxResults(page.getPageSize());
			return (List<Long>) query.list();
		}
		else
		{
			return new ArrayList<Long>();
		}
	}

	/**
	 * 得到部门里面的联系人数目
	 * 
	 * @param page
	 *            分页类
	 * @return 联系人id
	 */
	private long findCountByDepAll(Long companyId, Page page) {
		StringBuffer queryString = new StringBuffer(
				"select count(distinct t.user) from DepMemberPo as t where t.organization.id > 0  and t.user.company.id="
						+ companyId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}

	/**
	 * 获得帐号、姓名、邮件中带有关键字的用户
	 * 
	 * @param key
	 *            关键字
	 * @return 部门成员列表
	 */
	public List<DepMemberPo> searchUsersByKey(Long companyId, String key) {
		StringBuffer queryString = new StringBuffer();
		if (key.equalsIgnoreCase("") || key == null) {
			queryString
					.append("from DepMemberPo where user.id > 0 and user.company.id="
							+ companyId);
		} else {
			queryString.append("from DepMemberPo where user.company.id=")
					.append(companyId).append(" and (user.userName like '%")
					.append(key).append("%'")
					.append(" or user.realName like '%").append(key)
					.append("%'").append(" or user.realEmail like '%")
					.append(key).append("%')");
		}
		Query query = getSession().createQuery(queryString.toString());
		return (List<DepMemberPo>) query.list();
	}

	// ///////////////////////////////////////修改/////////////////////////////////////////////////////////////
	@Override
	public List<DepMemberPo> findOrgSharedUsers(Long userId, Long parentID) {
		// System.out.println("==============================%%%%%%%%%========================"+userId);
		String SQL = "from DepMemberPo as model where (model.organization.id=? )and ((model.user.calendarPublic=1) or ((model.user.calendarPublic=0) and (model.user.id in (select relativedUserId from CalendarRelation where userinfo.id = ?))))order by model.user.sortNum ";
		Query query = getSession().createQuery(SQL);
		query.setLong(0, parentID);
		query.setLong(1, userId);
		return query.list();
	}

	/**
	 * 获得帐号、姓名、邮件中带有关键字的用户
	 * 
	 * @param key
	 *            关键字
	 * @return 部门成员列表
	 */
	public List<DepMemberPo> searchSharedUsersByKey(Long userid,
			Long companyId, String key) {
		StringBuffer queryString = new StringBuffer();
		if (key.equalsIgnoreCase("") || key == null) {
			queryString
					.append("from DepMemberPo where (user.company.id = ")
					.append(companyId)
					.append(" ) and (user.id > 0) and ((user.calendarPublic=1) or ((user.calendarPublic=0) and (user.id in (select relativedUserId from CalendarRelation where userinfo.id = ")
					.append(userid).append("))))");
		} else {
			queryString
					.append("from DepMemberPo where (user.company.id = ")
					.append(companyId)
					.append(" ) and (user.userName like '%")
					.append(key)
					.append("%'")
					.append(" or user.realName like '%")
					.append(key)
					.append("%'")
					.append(" or user.realEmail like '%")
					.append(key)
					.append("%') and ((user.calendarPublic=1) or ((user.calendarPublic=0) and (user.id in (select relativedUserId from CalendarRelation where userinfo.id = ")
					.append(userid).append("))))");
		}
		Query query = getSession().createQuery(queryString.toString());
		return (List<DepMemberPo>) query.list();
	}
	// //////////////////////////////////////修改结束/////////////////////////////////////////////////////////////

}
