package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IGroupShipDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.GroupShipPo;

public class GroupShipDAO extends BaseDAOImpl<GroupShipPo> implements IGroupShipDAO{
	 /**
     * 通过用户id和微群id获得该微群除本用户外的用户列表
     * @param userid 用户id
     * @param groupid 微群id
     * @return 用户列表
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<Users> findUserByGroupId(Long userid,Long groupid) {
		
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where (id in ( select user.id from GroupShipPo where group.id = ").append(groupid).append(")) and (id <> ").append(userid).append(")");
		queryString.append(") order by id desc");
		Query query = getSession().createQuery(queryString.toString());
		List<Users> userList = new ArrayList<Users>();
		userList = query.list();
		return userList;
	}
	/**
	    * 根据用户id和微群id解除两者之间的关系
	    * @param userid 用户id
	    * @param groupid 微群id
	    * @return
	    */
	@Override
	public boolean delShip(Long userid, Long groupid) {
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("delete from GroupShipPo ");
			queryString.append(" where user.id =").append(userid).append(" and group.id=").append(groupid);
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
