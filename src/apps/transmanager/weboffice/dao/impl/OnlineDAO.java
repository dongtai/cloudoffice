package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.domain.OnlinePo;

public class OnlineDAO extends BaseDAOImpl<OnlinePo> implements IOnlineDAO {

	@SuppressWarnings("unchecked")
	public List<Long> findAllUserId() {
		String queryString = "select userId from OnlinePo";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}
	
	public List<Long> findAllOnlineUserIdsByOwnerId(Long ownerId){
		String queryString = "select o.userId from OnlinePo o ,CtmGroupMemberPo c where c.userId=:ownerId and c.ownerId =o.userId";
		Query query = getSession().createQuery(queryString);
		query.setParameter("ownerId", ownerId);
		return query.list();
	}
	
	@Deprecated
	public List<OnlinePo> findAllOnlineUserByOwnerId(Long ownerId){
		String queryString = "select o from OnlinePo o ,CtmGroupMemberPo c where c.ownerId=:ownerId and c.userId =o.userId";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}

}
