package apps.transmanager.weboffice.service.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.MessageInfo;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * TODO: 该类文件注释说明
 * <p>
 * Copyright(C) 2009-2010 Yozosoft Co. All Rights Reserved.
 * <p>
 * <p>
 * <p>
 */
public class MessageInfoDAO extends BaseDAO
{
private static final Log log = LogFactory.getLog(MessageInfo.class);
	
	public void save(MessageInfo transientInstance)
	{
		try
		{
			if (transientInstance.getId() == null)
			{
				super.save(transientInstance);
			}
			else
			{
				update(transientInstance);
			}
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}
	
	public List<MessageInfo> getMessageInfoBySender(Long senderId){
		try 
		{
			String queryString = "select mi from MessageInfo mi where mi.sendUsers.id = ?";
				return (List<MessageInfo>) findAllBySql(queryString, senderId);	
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	
	public List<MessageInfo> getMessageInfoByReceiver(String receiver){
		try 
		{
			String sql = "select mi from MessageInfo mi where mi.receiver = ?";
			
				return findAllBySql(sql, receiver);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	
	public void deleteMessage(Long messageId){
		try{
			String sql = "delete from MessageInfo where id = ?";
			excute(sql,messageId);
		}catch (RuntimeException re)
		{
			LogsUtility.error("delete failed", re);
			throw re;
		}
	}
	
	
	 public Long getUserMessageInfoCount(Long userId,Date start,Date end){
		 String queryString = "select count(mi) from MessageInfo mi where mi.sendUsers.id = ? and mi.date between ?  and  ?";
			return (Long) this.findAllBySql(queryString, userId,start ,end).get(0);
	 }
	
}
