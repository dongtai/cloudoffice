package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IPMicroblogMegDAO;
import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public class PMicroblogMegDAO extends BaseDAOImpl<PMicroblogMegPo> implements
		IPMicroblogMegDAO {
	
	public PMicroblogMegPo findLastNew(Long groupId)
	{
		StringBuffer queryString = new StringBuffer("from PMicroblogMegPo");
		queryString.append(" where groups.id=:groupId order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setFirstResult(0);
		query.setMaxResults(1);
		return (PMicroblogMegPo) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PMicroblogMegPo> findByKey(Long groupId, String key, Page page) {
		int count = (int)findByKeyCount(groupId,key);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("select tb1,(select count(id) from PMicroblogMegPo as tb2 where tb2.parent=tb1) as backCount from PMicroblogMegPo as tb1");
		queryString.append(" where groups.id=:groupId and parent is null and (sendUser.realName like '%")
		.append(key).append("%'").append(" or meg like '%").append(key).append("%') order by addDate desc");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Object[]> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<PMicroblogMegPo> pmList = new ArrayList<PMicroblogMegPo>();
		for(Object[] array:result)
		{
			PMicroblogMegPo pm = (PMicroblogMegPo) array[0];
			if((Long)array[1]!=null)
			pm.setBackCount(((Long)array[1]).intValue());
			pmList.add(pm);
		}
		return pmList;
	}

	/**
	 * 根据关键字搜索微博的总条数
	 * @param groupId 组ID
	 * @param key 关键字
	 * @return
	 */
	private long findByKeyCount(Long groupId, String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from PMicroblogMegPo");
		queryString.append(" where groups.id=:groupId and parent is null and (sendUser.realName like '%")
		.append(key).append("%'").append(" or meg like '%").append(key).append("%')");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PMicroblogMegPo> findGroupBlog(Long groupId, Long userId,
			Page page, String sort, String order) {
		int count = (int)findGroupBlogCount(groupId,userId);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("select tb1,(select count(id) from PMicroblogMegPo as tb2 where tb2.parent=tb1) as backCount from PMicroblogMegPo as tb1");
		queryString.append(" where groups.id=:groupId and parent is null");
		if(userId!=null)
		{
			queryString.append(" and sendUser.id=:userId");
		}
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		if(userId!=null)
		{
			query.setParameter("userId", userId);
		}
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Object[]> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<PMicroblogMegPo> pmList = new ArrayList<PMicroblogMegPo>();
		for(Object[] array:result)
		{
			PMicroblogMegPo pm = (PMicroblogMegPo) array[0];
			if((Long)array[1]!=null)
			pm.setBackCount(((Long)array[1]).intValue());
			pmList.add(pm);
		}
		return pmList;
	}

	private long findGroupBlogCount(Long groupId, Long userId) {
		StringBuffer queryString = new StringBuffer("select count(id) from PMicroblogMegPo");
		queryString.append(" where groups.id=:groupId and parent is null");
		if(userId!=null)
		{
			queryString.append(" and sendUser.id=:userId");
		}
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("groupId", groupId);
		if(userId!=null)
		{
			query.setParameter("userId", userId);
		}
		return (Long) query.uniqueResult();

	}
	
	

}
