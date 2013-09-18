package apps.transmanager.weboffice.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.ICtmGroupMemberDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.util.beans.Page;

public class CtmGroupMemberDAO extends BaseDAOImpl<CtmGroupMemberPo> implements
		ICtmGroupMemberDAO {
	
	/**
	 * 获得分组内的联系人用户ID
	 * @param groupId 组ID
	 * @param page 分页类
	 * @return 联系人数目
	 */
	public List<Long> findByGroup(Long groupId,Page page,String sort,String order) {
		int totalRecord = (int)findCountByGroup(groupId,page);
		page.setTotalRecord(totalRecord);
		StringBuffer queryString = new StringBuffer("select t.userId from CtmGroupMemberPo t");
		queryString.append(" where 1=1");
		queryString.append(" and t.groupId=").append(groupId);
		if(sort != null) {
			queryString.append(" order by ").append(sort).append(" ").append(order);
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return (List<Long>)query.list();
	}
	
	/**
	 * 获得联系人数目
	 * @param groupId 组ID
	 * @param page 分页类
	 * @return 联系人数目
	 */
	private long findCountByGroup(Long groupId,Page page) {
		StringBuffer queryString = new StringBuffer("select count(distinct t) from CtmGroupMemberPo t");
		queryString.append(" where 1=1");
		queryString.append(" and t.groupId=").append(groupId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	
	/**
	 * 获得所有分组联系人用户ID
	 * @param groupId 组ID
	 * @param page 分页类
	 * @return 联系人数目
	 */
	public List<Long> findByAll(Long ownerId,Page page,String sort,String order) {
		int totalRecord = (int)findCountByAll(ownerId);
		page.setTotalRecord(totalRecord);
		StringBuffer queryString = new StringBuffer("select distinct t.userId from CtmGroupMemberPo t");
		queryString.append(" where 1=1 and t.ownerId=").append(ownerId);
		if(sort != null) {
			queryString.append(" order by ").append(sort).append(" ").append(order);
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return (List<Long>)query.list();
	}
	
	/**
	 * 获得所有分组联系人用户ID
	 * 
	 * @param groupId
	 *            组ID
	 * @return 联系人ID
	 */
	public List<Long> findByGroup(Long groupId) {
		StringBuffer queryString = new StringBuffer(
				"select t.userId from CtmGroupMemberPo t");
		queryString.append(" where 1=1");
		queryString.append(" and t.groupId=").append(groupId);
		Query query = getSession().createQuery(queryString.toString());
		return (List<Long>) query.list();
	}
	
	/**
	 * 根据给出的查询条件，获得所有分组的联系人数目
	 * @param key 查询关键字
	 * @return 联系人数目
	 */
	private long findCountByAll(Long ownerId) {
		StringBuffer queryString = new StringBuffer("select count(distinct t) from CtmGroupMemberPo t");
		queryString.append(" where 1=1 and t.ownerId=").append(ownerId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	
	@Override
	public List<Users> findRostersByOwner(Long ownerId) {
		StringBuffer queryString = new StringBuffer("select distinct u from Users u, CtmGroupMemberPo t");
		queryString.append(" where u.id = t.userId and t.ownerId =:ownerId ");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		return query.list();
	}
	
	@Override
	public List<CtmGroupMemberPo> findRosterListByOwner(Long ownerId, Long rosterId) {
		StringBuffer queryString = new StringBuffer("select distinct t from CtmGroupMemberPo t");
		queryString.append(" where t.userId =:rosterId and t.ownerId =:ownerId ");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("rosterId", rosterId);
		query.setParameter("ownerId", ownerId);
		return query.list();
	}

	@Override
	public CtmGroupMemberPo findRosterByOwner(Long ownerId, Long rosterId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", rosterId);
		paramMap.put("ownerId", ownerId);
		return findByPropertyUnique(CtmGroupMemberPo.class.getName(), paramMap);
	}
	
	@Override
	public CtmGroupMemberPo findRosterByOwner(Long ownerId, Long rosterId, Long groupId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", rosterId);
		paramMap.put("ownerId", ownerId);
		paramMap.put("groupId", groupId);
		return findByPropertyUnique(CtmGroupMemberPo.class.getName(), paramMap);
	}

	/**
	 */
	public List<Users> findByGroupUser(Long groupId) {
		StringBuffer queryString = new StringBuffer("select u from Users u, CtmGroupMemberPo t ");
		queryString.append(" where u.id = t.userId and t.groupId =:id ");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("id", groupId);
		return query.list();
	}

	@Override
	public List<CtmGroupMemberPo> searchByKey(String key, Long ownerId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from CtmGroupMemberPo where ownerId=").append(ownerId).append(" and userName like '%").append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}
}

