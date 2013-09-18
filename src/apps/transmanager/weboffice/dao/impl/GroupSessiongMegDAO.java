package apps.transmanager.weboffice.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IGroupSessionMegDAO;
import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public class GroupSessiongMegDAO extends BaseDAOImpl<GroupSessionMegPo>
		implements IGroupSessionMegDAO {

	@SuppressWarnings("unchecked")
	public List<GroupSessionMegPo> findReadSessionMeg(Long acceptId,
			Long groupId) {
		StringBuffer queryString = new StringBuffer("from GroupSessionMegPo");
		queryString.append(" where id in(select t2.groupSessionMegId from GroupSessionMegReadPo as t2 where t2.acceptId=:acceptId and t2.groupId=:groupId and t2.readed=false)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		query.setParameter("groupId", groupId);
		return query.list();
	}

	public long findCount(Long groupId) {
		StringBuffer queryString = new StringBuffer("select count(t.id) from GroupSessionMegPo as t");
		queryString.append(" where t.groupId=:groupId");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		Long countId = (Long) query.uniqueResult();
		return (long)countId;
	}

	public List<Map<Long, Long>> findAllGNewMegTip(Long acceptId) {
		StringBuffer queryString = new StringBuffer("select new map(count(t.id) as count,t.groupId as groupId) from GroupSessionMegPo as t");
		queryString.append(" where t.id in(select t2.groupSessionMegId from GroupSessionMegReadPo as t2 where t2.acceptId=:acceptId and t2.readed=false) group by t.groupId");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		Object o =  query.list();
		return (List<Map<Long, Long>>) o;
	}
	
	public GroupSessionMegPo getLastUnreadGroupMessage(Long groupId){
		StringBuffer queryString = new StringBuffer("from GroupSessionMegPo as t");
		queryString.append(" where t.id in(select t2.groupSessionMegId from GroupSessionMegReadPo as t2 where t2.groupId=:groupId and t2.readed=false) order by t.addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setMaxResults(1);
		return (GroupSessionMegPo) query.uniqueResult();
	}
	
	/**
	 * 用户组信息更新为已读状态
	 * @param userId
	 *           当前用户
	 * @param mesasgeIds
	 *           消息ID列表
	 */
	public void updateGroupSessionMessageRead(Long groupId,Long userId,List<Long> mesasgeIds){
		StringBuffer queryString = new StringBuffer(" update GroupSessionMegReadPo set readed=true  where groupId=:groupId and acceptId=:userId and groupSessionMegId in (:ids)");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("userId", userId);
		query.setParameter("groupId", groupId);
		query.setParameterList("ids", mesasgeIds);
		query.executeUpdate();
	}

	@Override
	public List<GroupSessionMegPo> findUndeleteSessionMeg(Long acceptId,Long groupId,
			Page page, String orderColumn, String order) {
		StringBuffer queryString = new StringBuffer("from GroupSessionMegPo");
		queryString.append(" where id in(select t2.groupSessionMegId from GroupSessionMegReadPo as t2 where t2.acceptId=:acceptId and t2.groupId=:groupId and (t2.deleted=false or t2.deleted =null)) order by :orderColumn "+order );
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		query.setParameter("groupId", groupId);
		query.setParameter("orderColumn", orderColumn);
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		return query.list();
	}

	@Override
	public long findUndeleteSessionMegCount(Long acceptId,Long groupId) {
		StringBuffer queryString = new StringBuffer(" select count(id) from GroupSessionMegPo");
		queryString.append(" where id in(select t2.groupSessionMegId from GroupSessionMegReadPo as t2 where t2.acceptId=:acceptId and t2.groupId=:groupId and (t2.deleted=false or t2.deleted =null))");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		query.setParameter("groupId", groupId);
		return (Long) query.uniqueResult();
	}

}
