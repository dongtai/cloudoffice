package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IPriLetterDAO;
import apps.transmanager.weboffice.domain.PrivateLetterPo;
import apps.transmanager.weboffice.util.beans.Page;

public class PriLetterDAO extends BaseDAOImpl<PrivateLetterPo> implements IPriLetterDAO{
	/**
     * 通过用户id获得用户收到的私信
     * @param userid  用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 私信列表
     */
	@SuppressWarnings("unchecked")
	public List<PrivateLetterPo> findLetterByReceivedUser(Long userid,Page page,String sort, String order) {
		int count = (int)findReceiveLetterCount(userid);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from PrivateLetterPo");
		queryString.append(" where sendto_userid =").append(userid).append(" and send_to_del=").append(0);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<PrivateLetterPo> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<PrivateLetterPo> pmList = new ArrayList<PrivateLetterPo>();
		
		for(int i=0;i<result.size();i++)
		{
			PrivateLetterPo pm = result.get(i);
			pmList.add(pm);
		}
		return pmList;
	}
	/**
	 * 通过用户id获得用户收到的私信数目 
	 * @param userid 用户id
	 * @return 私信数目
	 */
	private long findReceiveLetterCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from PrivateLetterPo");
		queryString.append(" where sendto_userid =").append(userid).append(" and send_to_del=").append(0);
		Query query = getSession().createQuery(queryString.toString());

		return (Long) query.uniqueResult();

	}
	/**
     * 通过用户id获得用户发出的私信
     * @param userid  用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 私信列表
     */
	@SuppressWarnings("unchecked")
	public List<PrivateLetterPo> findLetterBySendUser(Long userid,Page page,String sort, String order) {
		int count = (int)findSendLetterCount(userid);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from PrivateLetterPo");
		queryString.append(" where send_userid =").append(userid).append(" and send_del=").append(0);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<PrivateLetterPo> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<PrivateLetterPo> pmList = new ArrayList<PrivateLetterPo>();
		
		for(int i=0;i<result.size();i++)
		{
			PrivateLetterPo pm = result.get(i);
			pmList.add(pm);
		}
		return pmList;
	}
	/**
	 * 通过用户id获得用户发出的私信数目
     * @param userid  用户id
	 * @return 私信数目
	 */
	private long findSendLetterCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from PrivateLetterPo");
		queryString.append(" where send_userid =").append(userid).append(" and send_del=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();

	}
	/**
	 * 通过私信id获得私信
	 * @param id 私信id
	 * @return 私信信息
	 */
	@Override
	public PrivateLetterPo findLetterById(Long id) {
		StringBuffer queryString = new StringBuffer("from PrivateLetterPo");
		queryString.append(" where id = ").append(id);
		Query query = getSession().createQuery(queryString.toString());
		
		PrivateLetterPo letter = (PrivateLetterPo)query.uniqueResult();
		return letter;
	}
	/**
	 * 删除私信（软删除）
	 * @param column 删除的用户（接受用户或发送用户）
	 * @param priletter_id 私信id
	 * @return
	 */
	@Override
	public boolean delByUser(String column,Long priletter_id) {
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update PrivateLetterPo ").append("set ");
			queryString.append(column).append(" =").append(1).append(" where id=").append(priletter_id);
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
