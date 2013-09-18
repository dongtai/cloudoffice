package apps.transmanager.weboffice.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IGroupSessionMegReadDAO;
import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;

public class GroupSessionMegReadDAO extends BaseDAOImpl<GroupSessionMegReadPo>
		implements IGroupSessionMegReadDAO {

	public void updateReade(Long groupId, Long acceptId) {
		StringBuffer queryString = new StringBuffer("update from GroupSessionMegReadPo");
		queryString.append(" set readed=true")
		.append(" where groupId=:groupId and acceptId=:acceptId and readed=false");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setParameter("acceptId", acceptId);
		query.executeUpdate();
	}

	@Override
	public List<GroupSessionMegReadPo> findHisRecordByDate(Long groupId,
			Long ownerId, Date startDate, Date endDate) {
		StringBuffer queryString = new StringBuffer("select sr from GroupSessionMegReadPo as sr, GroupSessionMegPo as s where ");
		queryString.append(" sr.groupSessionMegId=s.id and sr.acceptId=:ownerId and sr.groupId=:groupId and (sr.deleted=null or sr.deleted=false) and (s.addDate between :startTime and :endTime ) order by s.addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("ownerId", ownerId);
		query.setParameter("groupId", groupId);
		query.setParameter("startTime", startDate);
		query.setParameter("endTime", endDate);
		return (List<GroupSessionMegReadPo>)query.list();
	}
	
}
