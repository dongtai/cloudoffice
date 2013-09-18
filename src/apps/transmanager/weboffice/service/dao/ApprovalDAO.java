package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.databaseobject.Users;

/**
 * 文档审批DAO
 * <p>
 * <p>
 * <p>
 * <p>
 */
public class ApprovalDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(ApprovalDAO.class);
	//通过用户id查找待办中原始发文人组
	public List<Users> findTodoSenderByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from ApprovalInfo as a,SameSignInfo as b,Users as u"
					+" where a.id=b.approvalID and b.isnew=0 and b.islast=0 and a.userID=u.id and a.status="+ApproveConstants.NEW_STATUS_WAIT+" and "
					+" a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer.id= ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找已办中原始发文人组
	public List<Users> findDoneSenderByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from ApprovalInfo as a,SameSignInfo as b,Users as u"
					+" where a.id=b.approvalID and (b.isview is null or b.isview=0) and b.state="+ApproveConstants.NEW_STATUS_HAD+"and a.userID=u.id and ("
					+" a.modifytype=1 and b.islast=0 and b.signer.id= ?)";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找送阅/收阅中原始发文人组
	public List<Users> findToreadSenderByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from ApprovalInfo as a,ApprovalReader as d,Users as u"
					+" where a.id=d.approvalInfoId and a.userID=u.id and (((d.isview is null or d.isview=0) and (a.modifytype=1 and d.islast=0 and" +
					" d.readUser= ?)) or a.userID= ?)";
			return findAllBySql(queryString, userId,userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找草稿中收文人
	public List<Users> findDraftAccepterByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from ApprovalSave as a,Users as u"
					+" where a.userID = u.id and a.userID = ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找我的送文中送签的收文人
	public List<Users> findMyquestAccepterByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from ApprovalInfo as a,Users as u,SameSignInfo as b"
					+" where b.approvalID=a.id and b.isnew=0 and a.status<8 and b.signer.id = u.id and a.userID = ? ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找事务交办中的待办的交办人
	public List<Users> findAssignederOfTodoByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from TransInfo as a , TransSameInfo as b ,Users as u where a.id=b.transid and (a.isview is null or a.isview=0) and b.isnew=0 and b.islast=0 and b.isview=0 and (b.state="+ApproveConstants.TRANS_STATUS_START
					+" or b.state="+ApproveConstants.TRANS_STATUS_WAIT+")"
					+" and a.userID=u.id "
					+" and b.signer.id= ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找事务交办中的办结的交办人
	public List<Users> findAssignederOfFiledByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from TransInfo as a , TransSameInfo as b ,Users as u where a.id=b.transid and (a.isview is null or a.isview=0) and (b.isview is null or b.isview=0 ) and b.isnew=0 and b.state="+ApproveConstants.TRANS_STATUS_HAD
			+" and a.userID=u.id "
			+" and b.signer.id= ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找事务交办中的我的交办的办理人
	public List<Users> findTransactorOfMyquestByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct u from TransInfo as a , TransSameInfo as b ,Users as u where b.signer.id=u.id and b.transid=a.id and b.isnew=0 and (a.isview is null or a.isview=0) and a.status<5 and a.userID= ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找会议通知中的待办的通知者
	public List<Users> findInformerOfTodoByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct c from MeetInfo as a,MeetSameInfo as b,Users as c where a.senduser=c.id and a.id=b.meetid and (a.isview is null or a.isview=0) and b.meeter.id= ?"
				+" and (b.actionid is null or b.actionid=0) ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找会议通知中的待办的召开人
	public List<String> findConvenerOfTodoByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct a.mastername from MeetInfo as a,MeetSameInfo as b where a.id=b.meetid and (a.isview is null or a.isview=0) and b.meeter.id= ?"
				+" and (b.actionid is null or b.actionid=0) ";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找会议通知中的已办的通知者
	public List<Users> findInformerOfDoneByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct c from MeetInfo as a,MeetSameInfo as b,Users as c where a.senduser=c.id and a.id=b.meetid and (a.isview is null or a.isview=0) and b.isview=0 "
					+" and (b.meeter.id= ? or b.replaceid= ?)"
					+" and b.actionid>0 ";
			return findAllBySql(queryString, userId,userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找会议通知中的已办的召开人
	public List<String> findConvenerOfDoneByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct a.mastername from MeetInfo as a,MeetSameInfo as b where a.id=b.meetid and (a.isview is null or a.isview=0) and b.isview=0 "
					+" and (b.meeter.id= ? or b.replaceid= ?)"
					+" and b.actionid>0 ";
			return findAllBySql(queryString, userId,userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	//通过用户id查找会议通知中的我的通知的召开人
	public List<String> findConvenerOfMyquestByUserId(Long userId)
	{
		try
		{
			String queryString = "select distinct a.mastername from MeetInfo as a where (a.isview is null or a.isview=0) and a.senduser= ?";
			return findAllBySql(queryString, userId);
		}
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
}
