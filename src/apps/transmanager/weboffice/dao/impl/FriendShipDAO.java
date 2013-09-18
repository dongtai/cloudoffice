package apps.transmanager.weboffice.dao.impl;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IFriendShipDAO;
import apps.transmanager.weboffice.domain.FriendshipPo;

public class FriendShipDAO extends BaseDAOImpl<FriendshipPo> implements IFriendShipDAO{
	/**
     * 取消关注
     * @param follow_userid 被关注的用户id
     * @param userid 用户id
     * @return
     */
	@Override
	public boolean deletefollow(Long follow_userid, Long userid) {
		try
		{
		StringBuffer queryString = new StringBuffer("delete from FriendshipPo");
		queryString.append(" where follow_user.id = ").append(follow_userid).append(" and fan_user.id = ").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		int flag = query.executeUpdate();
		if(flag==0)
		{
			return false;
		}else{
			return true;
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return false;
	}
}
