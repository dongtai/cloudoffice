package apps.transmanager.weboffice.service.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.License;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersMessages;
import apps.transmanager.weboffice.service.dao.MessagesDAO;
import apps.transmanager.weboffice.service.listener.IMessagesListener;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 系统消息统一处理服务类。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Component(value=MessagesService.NAME)
public class MessagesService
{
    public static final String NAME = "messagesService";
	@Autowired
	private MessagesDAO messageDAO;
	private List<IMessagesListener> mlistener = new ArrayList<IMessagesListener>(); 
	
	/**
	 * 加入派发消息的事件。
	 * @param li
	 */
	@Autowired
	public void setMessagesListener(IMessagesListener li)
	{
		if (!mlistener.contains(li))
		{
			mlistener.add(li);
		}
	}
	
	/**
	 * 加入派发消息的事件。
	 * @param li
	 */
	public void addMessagesListener(IMessagesListener li)
	{
		if (!mlistener.contains(li))
		{
			mlistener.add(li);
		}
	}
	
	/**
	 * 移除需要派发的消息事件。
	 * @param li
	 */
	public void removeMessagesListener(IMessagesListener li)
	{
		mlistener.remove(li);
	}
	
	/**
	 * 用户发送消息给其他用户。该消息需要持久保存。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(Messages m, Long sendUserId, List<Long> userIds)
	{
		sendMessage("MegHandler.showNote",  "userId", m, sendUserId, userIds);
	}
	/**
	 * 用户发送消息给其他用户。该消息需要持久保存。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(String fun, String match, Messages m, Long sendUserId, List<Long> userIds)
	{
		addMessage(m, sendUserId, userIds);
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessages(fun, match, m, userIds);
		}
	}
	public void sendNewMessage(String fun, String match, Messages m,Long totalnum, Long sendUserId, List<Long> userIds)
	{
		addMessage(m, sendUserId, userIds);
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessageNums(fun, match, m,totalnum, userIds,true);
		}
	}
	/**
	 * 已经保存了，直接发送消息
	 * @param fun
	 * @param match
	 * @param m
	 * @param sendUserId
	 * @param userIds
	 */
	public void sendMessageTo(String fun, String match, Messages m,Long totalnum, Long sendUserId, List<Long> userIds)
	{
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessageNums(fun, match, m,totalnum, userIds,true);
		}
	}
	/**
	 * 发送消息给其他用户。该消息不需要持久保存。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(Object content, int type, List<Long> userIds)
	{
		sendMessage("MegHandler.showNote",  "userId", content, type, userIds);
	}
	
	/**
	 * 发送消息给其他用户。该消息不需要持久保存。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(String fun, String match, Object content, int type, List<Long> userIds)
	{
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessages(fun, match, content, type, userIds);
		}
	}
	
	/**
	 * 发送消息给其他用户。该消息不需要持久保存
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(String content, int type, List<Long> userIds)
	{
		//System.out.println("在messageService中的+++++++++++++++++++++++++++++++");
		sendMessage("MegHandler.showNote",  "userId", content, type, userIds);
	}
	/**
	 * 发送消息给其他用户。该消息不需要持久保存。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者	 * 
	 */
	public void sendMessage(String fun, String match, String content, int type, List<Long> userIds)
	{
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessages(fun, match, content, type, userIds);
		}
	}
	
	/**
	 * 发送消息
	 * @param fun js函数 
	 * @param match 要匹配的key 
	 * @param data 数据
	 * @param userIds 过滤集合
	 */
	public void sendMessage(String fun, String match, Map<String, Object> data, List<Long> userIds)
	{
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessages(fun, match, data, userIds);
		}		
	}
	
	/**
	 * 发送信息
	 * @param fun js函数名
	 * @param match 需要匹配的key值
	 * @param target 需要匹配的内容
	 * @param contents 发送的消息内容，及js的参数
	 * @return
	 */
	public void sendMessage(String fun, String match, List target, Object... contents)
	{
		for (IMessagesListener temp : mlistener)
		{
			temp.sendMessages(fun, match, target, contents);
		}	
	}
	
	/**
	 * 用户发送消息给其他用户。
	 * @param m 消息内容
	 * @param sendUserId 发送消息者
	 * @param userIds 接收消息者
	 */
	public void addMessage(Messages m, Long sendUserId, List<Long> userIds)
	{
		if (sendUserId != null)
		{
			Users u = (Users)messageDAO.find(Users.class, sendUserId);		
			m.setUser(u);
		}
		for (Long id : userIds)
		{
			Users u = (Users)messageDAO.find(Users.class, id);
			m.setMsguser(u);
			messageDAO.save(m);
		}
		
		if (userIds != null)
		{
			for (Long id : userIds)
			{
				UsersMessages um = new UsersMessages(id, m.getId());
				messageDAO.save(um);
			}
		}		
	}
	
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
		return messageDAO.getUsersMessages(userId, isNew, start, count);
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
		return messageDAO.getUserDispatchMessages(userId, start, count);
	}
	
	/**
	 * 获得用户存在的消息。
	 * @param userId
	 * @param type 消息的类别。具体参见com.evermore.weboffice.constants.both.MessageCons中的定义。
	 * @param isNew 是否是只获取没有看过的消息。 
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUsersMessages(Long userId, int type, boolean isNew, int start, int count)
	{	
		return messageDAO.getUsersMessages(userId, type, isNew, start, count, false, false);
	}
	
	/**
	 * 获得用户发送的消息。
	 * @param userId
	 * @param type 消息的类别。具体参见com.evermore.weboffice.constants.both.MessageCons中的定义。
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @return
	 */
	public List<Messages> getUserDispatchMessages(Long userId, int type, int start, int count)
	{
		return messageDAO.getUserDispatchMessages(userId, type, start, count, false, false);
	}
	
	/**
	 * 获得用户存在的消息。
	 * @param userId
	 * @param type 消息的类别。具体参见com.evermore.weboffice.constants.both.MessageCons中的定义。
	 * @param isNew 是否是只获取没有看过的消息。 
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @param orderByDate 是否根据消息的创建日期排序。
	 * @param asc 是否是升序
	 * @return
	 */
	public List<Messages> getUsersMessages(Long userId, int type, boolean isNew, int start, int count, boolean orderByDate, boolean asc)
	{	
		return messageDAO.getUsersMessages(userId, type, isNew, start, count, orderByDate, asc);
	}
	
	/**
	 * 获得用户发送的消息。
	 * @param userId
	 * @param type 消息的类别。具体参见com.evermore.weboffice.constants.both.MessageCons中的定义。
	 * @param start 分页显示开始的索引。如果从头开始，则传值为-1.	 
	 * @param count 分页显示的数量。如果没有数量限制，则传值为-1。
	 * @param orderByDate 是否根据消息的创建日期排序。
	 * @param asc 是否是升序
	 * @return
	 */
	public List<Messages> getUserDispatchMessages(Long userId, int type, int start, int count, boolean orderByDate, boolean asc)
	{
		return messageDAO.getUserDispatchMessages(userId, type, start, count, orderByDate, asc);
	}
	
	/**
	 * 更新某个消息的阅读状态
	 * @param userId
	 * @param messId
	 * @param st
	 */
	public void setMessageStatus(Long userId, Long messId, boolean st)
	{
		messageDAO.setMessageStatus(userId, messId, st);
	}
	
	/**
	 * 删除 某个消息
	 * @param userId
	 * @param messId
	 */
	public void deleteMessage(Long userId, Long messId)
	{
		messageDAO.deleteMessage(userId, messId);
	}
	
	/**
	 * 获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件总数
	 * @param userId 用户Id
	 * @return
	 */
	public long getAllSpaceNewMessagesCountByUseId(Long userId)
	{
		return messageDAO.getAllSpaceNewMessagesCountByUseId(userId);
	}
	
	/**
	 * 	获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息。
	 * 消息的内容是：message.attach是文件所在的全路径, message.content是"文件名/文件大小/文件权限"。
	 * @param userId 用户id
	 * @param start 需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count 需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public List<Messages> getAllSpaceNewMessagesByUserId(Long userId, int start, int count)
	{
		return messageDAO.getAllSpaceNewMessagesByUserId(userId, start, count);
	}
	
	/**
	 * 获取用户所参与的某个空间中的新加入没有看的文件及需要其审批的文件总数
	 * @param userId 用户id
	 * @param spaceUID 空间的spaceUID
	 * @return
	 */
	public long getSpaceNewMessagesCountByUseId(Long userId, String spaceUID)
	{
		return messageDAO.getSpaceNewMessagesCountByUseId(userId, spaceUID);
	}
	
	/**
	 *	获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息,
	 * 消息的内容是：message.content是"文件名/文件大小/文件权限"，message.attach是文件所在的全路径。
	 * @param userId 用户id
	 * @param spaceUID 空间的spaceUID
	 * @param start 需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count 需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public List<Messages> getSpaceNewMessagesByUserId(Long userId, String spaceUID, int start, int count)
	{
		return messageDAO.getSpaceNewMessagesByUserId(userId, spaceUID, start, count);
	}
	
	/**
	 * 由于文件更名，需要更新消息中的内容。
	 */
	public void updateSpaceNewMessages(String oldPath, String path, String fileName)
	{
		messageDAO.updateSpaceNewMessages(oldPath, path, fileName);
	}
	
	/**
	 * 由于文件更名，需要更新消息中的内容。
	 */
	public void updateSpaceNewMessages(String[] oldPaths, String path)
	{
		messageDAO.updateSpaceNewMessages(oldPaths, path);
	}
	
	/**
	 * 删除空间的文件消息内容。
	 */
	public void deleteSpaceMessage(String[] path)
	{
		messageDAO.deleteSpaceMessage(path);
	}
		
	/**
	 * 
	 * @param from
	 * @param toMail
	 * @param content
	 * @param subject
	 * @return
	 */
	public boolean sendMail(String from, String toMail, String content, String subject)
	{
		List<License> license = messageDAO.findAll(License.class);
		if (license != null && license.size() > 0)
		{
			License temp = license.get(0);
			String mailAddress = temp.getMailAddress();
			int index = mailAddress.lastIndexOf("@");
	        String name = mailAddress.substring(0, index); 
			return sendMail(temp.getMailHost(), name, temp.getMailPwd(),
					from, mailAddress, toMail, content, subject);
		}
		return false;
	}
	
	/**
	 * 
	 * @param toMail
	 * @param content
	 * @param subject
	 * @return
	 */
	public boolean sendMail(String toMail, String content, String subject)
	{
		List<License> license = messageDAO.findAll(License.class);
		if (license != null && license.size() > 0)
		{
			License temp = license.get(0);
			String mailAddress = temp.getMailAddress();
			int index = mailAddress.lastIndexOf("@");
	        String name = mailAddress.substring(0, index); 
			return sendMail(temp.getMailHost(), name, temp.getMailPwd(),
					mailAddress, mailAddress, toMail, content, subject);
		}
		return false;
	}
	
	/**
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param showFromMail
	 * @param realFromMail
	 * @param toMail
	 * @param content
	 * @param subject
	 * @return
	 */
	public boolean sendMail(String host, String user, String password,
			String showFromMail, String realFromMail, String toMail, String content, String subject)
    {
        try
        {
        	if (toMail == null || toMail.length() <= 0)
        	{
        		return false;
        	}
        	String[] to = toMail.split(";");
        	if (to == null || to.length <= 0)
        	{
        		return false;
        	}
        	InternetAddress[] from1 = new InternetAddress[1];     //显示的发送者邮箱账号
        	from1[0] = new InternetAddress(showFromMail);
            
        	InternetAddress[] from2 = new InternetAddress[1];     //真实的发送者邮箱账号
            from2[0] = new InternetAddress(showFromMail+" <"+realFromMail+">");            
            
            Address [] toAddress = new InternetAddress[to.length];
            for(int i = to.length - 1; i >= 0; i--)
            {
            	toAddress[i] = new InternetAddress(to[i]);
            }
            Properties p = new Properties();
            p.put("mail.smtp.host", host);
            p.put("mail.smtp.ehlo", "true");
            p.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(p, null);
            MimeMessage message = new MimeMessage(session);
            message.addFrom(from2);
            message.addRecipients(Message.RecipientType.TO, toAddress);
            message.setSubject(subject, "gb2312");
            message.setHeader("Reply-To", showFromMail);
            message.setHeader("Return-Path", showFromMail);
            message.setSender(from1[0]);
            message.setContent(content, "text/html; charset=gb2312");
            
            Transport tr = session.getTransport("smtp");    
            tr.connect(host, user, password);
            tr.sendMessage(message, toAddress);
            tr.close();
            return true;
        }
        catch(MessagingException e)
        {
            LogsUtility.error(e);            
        }
        return false;
    }

//////////////////////以下是孙爱华增加
	/**
	 * 获取当前用户的消息
	 * @param user
	 * @return
	 */
	public long getMessagenums(Users user)
	{
		
		long count=messageDAO.getCountBySql("select count(model) from Messages as model where model.state is null and model.deleted is null and model.msguser.id=? ", user.getId());
		
		//SYS_TYPE  1  系统消息。
		
		//SPACE_TYPE 2 空间邀请消息。
		//DOC_TYPE 3 文档消息。
		//CALENDER_TYPE 4 日程消息。
		//IM_TYPE 5 即时消息。 
		//ADD_DOC_TYPE 6公共空间增加文档 消息。
		//ADUIT_DOC_TYPE 7审核文档 消息。
	    //SHARE_TYPE 8共享文档 消息。
		//FORCE_QUIT 9 被迫退出登录 
	    //READDOC = 10 传阅文档消息
	    //AVMSG = 11 公告消息
	    //MEETING 12会议邀请消息
		//1、共享文档信息
		
		
		//2、签批信息
		
		
		//3、通知公告
		
		
		//4、及时消息
		return count;
	}
	
	
}
