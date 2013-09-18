package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Users;

public class UserDAO extends BaseDAOImpl<Users> implements IUserDAO {

	public void syncUpdate(Long id, String fileName, String realName, String realEmail) {
		StringBuffer query = new StringBuffer("update SessionMegPo");
		query.append(" set sendImg='").append(fileName).append("',sendName='").append(realName).append("' where sendId=").append(id);
		Query q = getSession().createQuery(query.toString());
		q.executeUpdate();
		query = new StringBuffer("update GroupSessionMegPo");
		query.append(" set sendImg='").append(fileName).append("',sendName='").append(realName).append("' where sendId=").append(id);
		q = getSession().createQuery(query.toString());
		q.executeUpdate();
		query = new StringBuffer("update DiscuGroupMemberPo");
		query.append(" set image='").append(fileName).append("',memberName='").append(realName).append("' where memberId=").append(id);
		q = getSession().createQuery(query.toString());
		q.executeUpdate();
		query = new StringBuffer("update CtmGroupMemberPo");
		query.append(" set image='").append(fileName).append("',userName='").append(realName).append("',mail='").append(realEmail).append("' where userId=").append(id);
		q = getSession().createQuery(query.toString());
		q.executeUpdate();
	}
	
	public void updateCalendarSetting(Users user,boolean calendarSetting)
	{
		String query = " update Users set calendarPublic = ? where id = ?";
		Query q = getSession().createQuery(query).setParameter(0, calendarSetting).setParameter(1, user.getId());
		q.executeUpdate();
		
	}
	
	public List<Users> searchByKey(String key, Long userId, String cols[]) {
		if (cols.length <= 0) {
			return null;
		}
		
		StringBuffer queryString = new StringBuffer();
		queryString.append("from Users user where (1=0");
		for (int i = 0; i < cols.length; i++) {
			queryString.append(" or user." + cols[i] + " like '%").append(key).append("%'");
		}
		queryString.append(" )");
		queryString.append(" and user.id not in ( select distinct userId from CtmGroupMemberPo where ownerId =:id ) and user.id !=:id");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("id", userId);
		return query.list();
	}
	
}
