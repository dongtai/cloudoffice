package apps.transmanager.weboffice.service.dao;

import java.util.List;

import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.databaseobject.Messages;


/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class MessagesDAO extends BaseDAO
{
	/**
	 * 获得用户存在的消息。
	 * @param userId
	 * @param isNew 是否是只获取没有看过的消息。 
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUsersMessages(Long userId, boolean isNew, int start, int count)
	{		
		String sql = "select um.message from UsersMessages um where um.isDelete = false and um.user.id = ?";
		if (isNew)
		{
			sql +=  " and um.isNew = true";
		}
		return (List<Messages>)findAllBySql(start, count, sql, userId);
	}
	
	/**
	 * 获得用户发送的消息。
	 * @param userId
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUserDispatchMessages(Long userId, int start, int count)
	{
		String sql = "select m from Messages m where m.user.id = ? ";
		return (List<Messages>)findAllBySql(start, count, sql, userId);
	}
	public List<Messages> getMessagesByUserAndAttach(Long userId, String Attach)
	{
		String sql = "select m from Messages m where m.user.id = ? and m.attach like ?";
		return (List<Messages>)findAllBySql(sql, userId,Attach);
	}
	
	/**
	 * 更新某个消息的阅读状态
	 * @param userId
	 * @param messId
	 * @param st
	 */
	public void setMessageStatus(Long userId, Long messId, boolean st)
	{
		String sql = "update UsersMessages um set um.isNew = ? where um.user.id = ? and um.message.id = ? ";
		excute(sql, st, userId, messId);
		if (st)
		{
			sql = "update Messages um set um.state = 1 where um.msguser.id=? and um.id = ? ";
			excute(sql,userId, messId);
		}
		else
		{
			sql = "update Messages um set um.state = 0 where um.msguser.id=? and um.id = ? ";
			excute(sql,userId, messId);
		}
	}
	
	/**
	 * 删除 某个消息
	 * @param userId
	 * @param messId
	 */
	public void deleteMessage(Long userId, Long messId)
	{
		String sql = "update UsersMessages um set um.isDelete = true where um.user.id = ? and um.message.id = ? ";
		excute(sql, userId, messId);
		
		sql = "update Messages um set um.deleted = 1 where um.msguser.id=? and um.id = ? ";
		excute(sql, userId, messId);
	}
	
	/**
	 * 获得用户存在的消息。
	 * @param userId
	 * @param isNew 是否是只获取没有看过的消息。 
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUsersMessages(Long userId, int type, boolean isNew, int start, int count, boolean orderByDate, boolean asc)
	{		
		StringBuffer sql = new StringBuffer("select um.message from UsersMessages um where um.isDelete = false and um.user.id = ? and um.message.type = ?");
		if (isNew)
		{
			sql.append(" and um.isNew = true");
		}
		if (orderByDate)
		{
			sql.append(" order by um.message.date ");
			sql.append(asc ? " ASC" : " DESC");
		}
		return (List<Messages>)findAllBySql(start, count, sql.toString(), userId, type);
	}
	
	/**
	 * 获得用户存在的消息。
	 * @param userId
	 * @param isNew 是否是只获取没有看过的消息。 
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public Long getUsersMessagesCount(Long userId, int type, boolean isNew)
	{		
		StringBuffer sql = new StringBuffer("select count(*) from UsersMessages um where um.isDelete = false and um.user.id = ? and um.message.type = ?");
		if (isNew)
		{
			sql.append(" and um.isNew = true");
		}
		return getCountBySql(sql.toString(), userId, type);
	}
	
	/**
	 * 获得用户发送的消息。
	 * @param userId
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUserDispatchMessages(Long userId, int type, int start, int count, boolean orderByDate, boolean asc)
	{
		StringBuffer sql = new StringBuffer("select m from Messages m where m.user.id = ? and m.type = ?");
		if (orderByDate)
		{
			sql.append(" order by m.date ");
			sql.append(asc ? " ASC" : " DESC");
		}
		return (List<Messages>)findAllBySql(start, count, sql.toString(), userId, type);
	}
	
	/**
	 * 获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件总数
	 * @param userId 用户Id
	 * @return
	 */
	public long getAllSpaceNewMessagesCountByUseId(Long userId)
	{
		String query = " select count(*) from UsersMessages um where um.isDelete = false and um.isNew = true and um.message.type in (?,?) and um.user.id = ?";
		return getCountBySql(query, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, userId);
	}
	
	/**
	 * 	获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息。
	 * 消息的内容是：message.content是文件名，message.attach是文件所在的全路径。
	 * @param userId 用户id
	 * @param start 需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count 需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public List<Messages> getAllSpaceNewMessagesByUserId(Long userId, int start, int count)
	{
		String sql = "select um.message from UsersMessages um where um.isDelete = false and um.isNew = true "
				+ "and um.message.type in (?,?) and um.user.id = ?  order by um.message.date DESC";
		return (List<Messages>)findAllBySql(start, count, sql, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, userId);
	}
	
	/**
	 * 获取用户所参与的某个空间中的新加入没有看的文件及需要其审批的文件总数
	 * @param userId 用户id
	 * @param spaceUID 空间的spaceUID
	 * @return
	 */
	public long getSpaceNewMessagesCountByUseId(Long userId, String spaceUID)
	{
		String query = " select count(*) from UsersMessages um where um.isDelete = false and um.isNew = true and um.message.type in (?,?)"
				+ " and um.user.id = ? and um.message.attach like ? ";
		return getCountBySql(query, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, userId, spaceUID + "%");
	}
	
	/**
	 *	获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息,
	 * 消息的内容是：message.content是文件名，message.attach是文件所在的全路径。
	 * @param userId 用户id
	 * @param spaceUID 空间的spaceUID
	 * @param start 需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count 需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public List<Messages> getSpaceNewMessagesByUserId(Long userId, String spaceUID, int start, int count)
	{
		String sql = "select um.message from UsersMessages um where um.isDelete = false and um.isNew = true "
			+ "and um.message.type in (?,?) and um.user.id = ? and um.message.attach like ? order by um.message.date DESC";
		return (List<Messages>)findAllBySql(start, count, sql, MessageCons.ADUIT_DOC_TYPE, MessageCons.ADD_DOC_TYPE, userId, spaceUID + "%");
	}
	/**
	 * 由于文件更名，需要更新消息中的内容。
	 */// 该需求不太合理。
	public void updateSpaceNewMessages(String oldPath, String path, String fileName)
	{		
		int index = oldPath.lastIndexOf("/");
		String oldName = index >= 0 ? oldPath.substring(index + 1) : oldPath;
		String  queryString = "update Messages as um set um.attach = replace(um.attach, ?, ?), um.content = replace(um.content, ?, ?)" 
			+ "where um.attach like ? ";
		excute(queryString, oldPath, path, oldName, fileName, oldPath + "%");		
		
	}
	
	/**
	 * 由于文件移动，需要更新消息中的内容。
	 */// 该需求不太合理。
	public void updateSpaceNewMessages(String[] oldPaths, String path)
	{
		int index;
		String old;
		String  queryString;
		for(String temp : oldPaths)
		{
			index = temp.lastIndexOf("/");
			old = index >= 0 ? temp.substring(0, index) : temp;
			queryString = "update Messages as um set um.attach = replace(um.attach, ?, ?) where um.attach like ? ";
			excute(queryString, old, path, temp + "%");		
		}
	}
	
	/**
	 * 删除空间的文件消息内容。
	 */
	public void deleteSpaceMessage(String[] path)
	{
		for(String temp : path)
		{
			String  queryString = "delete from Messages as um where um.attach  like ? ";
			excute(queryString, temp + "%");
		}
				
	}
	
}
