package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IDiscuGroupMemberDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPo;

public class DiscuGroupMemberDAO extends BaseDAOImpl<DiscuGroupMemberPo>
		implements IDiscuGroupMemberDAO {

	public void delete(Long groupId, Long userId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("delete from DiscuGroupMemberPo where groupId=:groupId");
		queryString.append(" and memberId=:memberId");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setParameter("memberId", userId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<DiscuGroupMemberPo> findNoOwner(Long groupId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from DiscuGroupMemberPo where groupId=:groupId");
		queryString.append(" and memberId!=ownerId");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> findGroupUser(long gId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("select u from Users u, DiscuGroupMemberPo g where g.groupId=:groupId");
		queryString.append(" and g.memberId != g.ownerId and u.id = g.memberId ");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", gId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DiscuGroupMemberPo> findGroupUser(Long groupId,long memberId){
		StringBuffer queryString = new StringBuffer();
		queryString.append(" from DiscuGroupMemberPo g where g.groupId=:groupId");
		queryString.append(" and g.memberId != g.ownerId and g.memberId=:memberId ");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setParameter("memberId", memberId);
		return query.list();
	}

}
