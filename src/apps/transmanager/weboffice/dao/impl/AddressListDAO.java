package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IAddressListDAO;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.util.beans.Page;

public class AddressListDAO extends BaseDAOImpl<AddressListPo> implements IAddressListDAO{
	
	public List<AddressListPo> findByGroup(Long groupId,Page page, String sort, String order) {
		int totalRecord = (int)findCountByGroup(groupId,page);
		page.setTotalRecord(totalRecord);
		StringBuffer queryString = new StringBuffer("from AddressListPo t");
		queryString.append(" where 1=1");
		queryString.append(" and t.groupId=").append(groupId);
		if(sort!=null)
		{
			queryString.append(" order by ").append(sort).append(" ").append(order);
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return (List<AddressListPo>)query.list();
	}

	public List<AddressListPo> findByAll(Long ownerId, Page page,String sort,String order) {
		int totalRecord = (int)findCountByAll(ownerId);
		page.setTotalRecord(totalRecord);
		StringBuffer queryString = new StringBuffer("from AddressListPo t");
		queryString.append(" where 1=1 and t.ownerId=").append(ownerId);
		if(sort!=null)
		{
			queryString.append(" order by ").append(sort).append(" ").append(order);
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return (List<AddressListPo>)query.list();
	}
	
	/**
	 * 根据给出的查询条件，获得所有分组的联系人数目
	 * @param key 查询关键字
	 * @return 联系人数目
	 */
	private long findCountByAll(Long ownerId) {
		StringBuffer queryString = new StringBuffer("select count(t.id) from AddressListPo t");
		queryString.append(" where 1=1 and t.ownerId=").append(ownerId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}

	/**
	 * 获得联系人数目
	 * @param groupId 组ID
	 * @param key 关键字
	 * @param page 分页类
	 * @return 联系人数目
	 */
	private long findCountByGroup(Long groupId,Page page) {
		StringBuffer queryString = new StringBuffer("select count(t.id) from AddressListPo t");
		queryString.append(" where 1=1");
		queryString.append(" and t.groupId=").append(groupId);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	
	@Override
	public List<AddressListPo> searchByKey(String key, Long ownerId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AddressListPo where ownerId=").append(ownerId).append(" and userName like '%").append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}
}
