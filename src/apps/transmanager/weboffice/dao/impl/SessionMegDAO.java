package apps.transmanager.weboffice.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;

import apps.transmanager.weboffice.dao.ISessionMegDAO;
import apps.transmanager.weboffice.domain.SessionMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public class SessionMegDAO extends BaseDAOImpl<SessionMegPo> implements ISessionMegDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<Long, Long>> findRecentTalkRoster(Long ownerId) {
		StringBuffer queryString = new StringBuffer("select max(id),t.sendId as sendId,t.acceptId as acceptId from SessionMegPo as t where");
		queryString.append(" acceptId=:ownerId or sendId=:ownerId group by acceptId,sendId order by max(id) desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}
	@SuppressWarnings("unchecked")
	public List<SessionMegPo> findHisRecord(Long ownerId, Long otherId,
			Page page) {
		//int toalCount = (int) findHisRecordCount(ownerId, otherId);
		//page.setTotalRecord(toalCount);
		StringBuffer queryString = new StringBuffer("from SessionMegPo as t where ");
		queryString.append(" ((acceptId=:ownerId and sendId=:otherId and (accepterDelete=null or accepterDelete =false) ) or (acceptId=:otherId and sendId=:ownerId and  (senderDelele=null or senderDelele =false))) order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setParameter("otherId", otherId);
		query.setMaxResults(page.getPageSize());
		query.setFirstResult(page.getCurrentRecord());
		return query.list();
	}
	
	@Override
	public List<SessionMegPo> findHisRecordByDate(Long ownerId, Long userId,
			Date startDate, Date endDate) {
		StringBuffer queryString = new StringBuffer("from SessionMegPo as s where ");
		queryString.append(" ((acceptId=:ownerId and sendId=:otherId and (accepterDelete=null or accepterDelete =false) ) or (acceptId=:otherId and sendId=:ownerId and  (senderDelele=null or senderDelele =false))) and (s.addDate between :startTime and :endTime ) order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setParameter("otherId", userId);
		query.setParameter("startTime", startDate);
		query.setParameter("endTime", endDate);
		return query.list();
	}
	
	@Override
	public List<SessionMegPo> findHisRecord(Long ownerId, Long otherId) {
		StringBuffer queryString = new StringBuffer("from SessionMegPo as t where ");
		queryString.append(" ((acceptId=:ownerId and sendId=:otherId and (accepterDelete=null or accepterDelete =false) ) or (acceptId=:otherId and sendId=:ownerId and  (senderDelele=null or senderDelele =false))) order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setParameter("otherId", otherId);
		return query.list();
	}

	public long findHisRecordCount(Long ownerId, Long otherId) {
		StringBuffer queryString = new StringBuffer("select count(t.id) from SessionMegPo as t where ");
		queryString.append(" (acceptId=:ownerId and sendId=:otherId  and  (accepterDelete=null or accepterDelete =false)) or (acceptId=:otherId and sendId=:ownerId and  (senderDelele=null or senderDelele =false))");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setParameter("otherId", otherId);
		long count = (Long)query.uniqueResult();
		return count;
	}

	public List<Map<Long, Long>> findAllPNewMegTip(Long acceptId) {
		StringBuffer queryString = new StringBuffer("select count(t.sendId) as count,t.sendId as sendId from SessionMegPo as t  where ");
		queryString.append(" acceptId=:acceptId and readed=false group by t.sendId  ");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("acceptId", acceptId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		Object result = (Object)query.list();
		return (List<Map<Long, Long>>) result;
	}

	@Override
	public long getUnreadMessageCount(Long acceptId) {
		StringBuffer queryString = new StringBuffer("select count(t.sendId) from SessionMegPo as t where ");
		queryString.append(" acceptId=:acceptId and readed=false");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("acceptId", acceptId);
		return Long.parseLong(String.valueOf(query.uniqueResult()));
	}

	@Override
	public List<SessionMegPo> getUnreadMessage(Long acceptId) {
		StringBuffer queryString = new StringBuffer(" from SessionMegPo as t where ");
		queryString.append(" acceptId=:acceptId and readed=false order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("acceptId", acceptId);
		return query.list();
	}

	@Override
	public SessionMegPo getLastestUnreadMessage(Long acceptId) {
		StringBuffer queryString = new StringBuffer(" from SessionMegPo as t where ");
		queryString.append(" acceptId=:acceptId and readed=false order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("acceptId", acceptId);
		query.setMaxResults(1);
		return (SessionMegPo) query.uniqueResult();
	}
	
	/**
	 * 获取最新一条未读信息
	 *     
	 * @param acceptId
	 *        当前用户ID
	 * @return
	 *      null 表示没有未读信息
	 */
	public SessionMegPo getLastestUnreadMessage(Long acceptId,Long sendId){
		StringBuffer queryString = new StringBuffer(" from SessionMegPo as t where ");
		queryString.append(" acceptId=:acceptId and sendId=:sendId and readed=false order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameter("acceptId", acceptId);
		query.setParameter("sendId", sendId);
		query.setMaxResults(1);
		return (SessionMegPo) query.uniqueResult();
	}

	@Override
	public void updateSessionMessageRead(List<Long> mesasgeIds) {
		StringBuffer queryString = new StringBuffer(" update SessionMegPo set readed=true  where id in (:ids)");
		Query query = getSession().createQuery(queryString.toString());//.addEntity("meg",SessionMegPo.class).addScalar("count",Hibernate.BIG_INTEGER);
		query.setParameterList("ids", mesasgeIds);
		query.executeUpdate();
	}

}
