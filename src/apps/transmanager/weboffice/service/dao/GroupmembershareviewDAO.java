package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.domain.Groupmembershareview;

/**
  */

public class GroupmembershareviewDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(GroupmembershareviewDAO.class);
	private static final String VIEW = "select new com.evermore.weboffice.domain.Groupmembershareview("
			 + "groupmembershareinfo.shareFile, groupmembershareinfo.shareowner,"
			 + "groupshareinfo.permit, groupmembershareinfo.userinfo.id," 
			 + "groupmembershareinfo.isNew)"
			 + "from Groupmembershareinfo groupmembershareinfo, Groupshareinfo groupshareinfo "
			+ " where groupmembershareinfo.groupinfo.id = groupshareinfo.groupinfo.id "
			+ " and groupmembershareinfo.shareFile = groupshareinfo.shareFile "
			+ " and groupmembershareinfo.shareowner = groupshareinfo.userinfo.id";
	

	public List findByMemberId(Object value)
	{
		log.debug("finding Groupmembershareview instance with property: memberid, value: " + value);
		try
		{
			String queryString = VIEW + " and groupmembershareinfo.userinfo.id= ?";
			return findAllBySql(queryString, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll()
	{
		log.debug("finding all Groupmembershareview instances");
		try
		{
			return findAllBySql(VIEW);
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}


	public List<Groupmembershareview> getByOwnerAndShare(long ownerID,
			long shareID)
	{
		try
		{
			String queryString = VIEW + " and groupmembershareinfo.shareowner"
					+ "=? and groupmembershareinfo.userinfo.id= ? ";
			return findAllBySql(queryString,  ownerID, shareID);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Groupmembershareview> findByMemberAndNew(long memberID,
			int isNew)
	{
		try
		{
			String queryString = VIEW + " and groupmembershareinfo.userinfo.id"
					+ "=? groupmembershareinfo.isNew= ? ";
			return findAllBySql(queryString,  memberID, isNew);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by isNew failed", re);
			re.printStackTrace();
			throw re;
		}
	}
}