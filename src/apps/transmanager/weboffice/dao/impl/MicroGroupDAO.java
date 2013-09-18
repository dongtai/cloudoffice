package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IMicroGroupDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.MicroGroupPo;

public class MicroGroupDAO extends BaseDAOImpl<MicroGroupPo> implements IMicroGroupDAO{
	/**
     * 通过用户id获得该用户的所有微群
     * @param userid 用户id
     * @param sort 排序字段
	 * @param order 排序方式
     * @return 微群列表
     */
	@Override
	@SuppressWarnings("unchecked")
	public List<MicroGroupPo> findListByUserid(Long userid,String sort,String order) {
		StringBuffer queryString = new StringBuffer("from MicroGroupPo");
		queryString.append(" where id in ( select group.id from GroupShipPo where user.id = ").append(userid).append(")");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		List<MicroGroupPo> groupList = query.list();
		return groupList;
	}
	/**
     * 更新微群的管理员
     * @param newManager 新的管理员
     * @param groupid 微群号
     * @return
     */
	@Override
	public boolean updateManager(Users newManager, Long groupid) {
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update MicroGroupPo set group_manager.id=").append(newManager.getId());
			queryString.append(" where id =").append(groupid);
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
	 /**
     * 得到全部的微群
     * @return 微群列表
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<MicroGroupPo> findAllGroup() {
		StringBuffer queryString = new StringBuffer("from MicroGroupPo");
		Query query = getSession().createQuery(queryString.toString());
		List<MicroGroupPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<MicroGroupPo> groupList = new ArrayList<MicroGroupPo>();
		for(int i=0;i<result.size();i++)
		{
			MicroGroupPo group = result.get(i);
			groupList.add(group);
		}
		return groupList;
	}
	/**
     * 根据微群名获得除本微群以外的所有微群
     * @param groupname 微群名
     * @return 微群列表
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<MicroGroupPo> findAllOtherGroup(Long groupid) {
		StringBuffer queryString = new StringBuffer("from MicroGroupPo");
		queryString.append(" where id<>").append(groupid);
		Query query = getSession().createQuery(queryString.toString());
		List<MicroGroupPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<MicroGroupPo> groupList = new ArrayList<MicroGroupPo>();
		for(int i=0;i<result.size();i++)
		{
			MicroGroupPo group = result.get(i);
			groupList.add(group);
		}
		return groupList;
	}
	/**
     * 更新微群
     * @param groupid 微群号
     * @param group 新的内容
     * @return
     */
	@Override
	public boolean UpdateGroup(Long groupid,MicroGroupPo group) {
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update MicroGroupPo set group_manager.id=").append(group.getGroup_manager().getId());
			queryString.append(",group_name='").append(group.getGroup_name()).append("',group_description='").append(group.getGroup_description()).append("'");
			queryString.append(" where id =").append(groupid);
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
	

