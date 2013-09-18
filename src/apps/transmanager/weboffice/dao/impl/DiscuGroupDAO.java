package apps.transmanager.weboffice.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IDiscuGroupDAO;
import apps.transmanager.weboffice.domain.DiscuGroupPo;

public class DiscuGroupDAO extends BaseDAOImpl<DiscuGroupPo> implements
		IDiscuGroupDAO {

	@SuppressWarnings("unchecked")
	public List<DiscuGroupPo> findByMbId(Long id) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DiscuGroupPo where id in(");
		queryString
				.append("select distinct groupId from DiscuGroupMemberPo where memberId=:id and ownerId!=:id)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("id", id);
		return query.list();
	}
	
	@Override
	public List<DiscuGroupPo> findByOwner(Long ownerId) {
		return this.findByProperty(DiscuGroupPo.class.getName(), "ownerId", ownerId);
	}
	
	@Override
	public DiscuGroupPo findByNameAndOwner(String gname, Long ownerId) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("ownerId", ownerId);
		propertyMap.put("name", gname);
		return findByPropertyUnique(DiscuGroupPo.class.getName(), propertyMap);
	}

	public List<DiscuGroupPo> searchDiscuGroupPoByKey(String key,String cols[],Long userId){
		if(cols.length <=0){
			return null;
		}
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DiscuGroupPo discuGroupPo where (1=0");
		for(int i=0;i<cols.length;i++){
			queryString.append(" or discuGroupPo."+cols[i]+" like '%").append(key).append("%'");
		}
		queryString.append(" )");
		queryString.append(" and discuGroupPo.id not in (select distinct groupId from DiscuGroupMemberPo where memberId=:id or ownerId=:id)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("id", userId);
		return query.list();
	}

	@Override
	public List<DiscuGroupPo> searchByKey(String key) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DiscuGroupPo  where name like '%").append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}

}
