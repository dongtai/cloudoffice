package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户消息关联表。此表可以不需要了
 * 即是用户拥有的消息。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="usersmessages")
@IdClass(UsersMessages.UsersMessagesId.class)
public class UsersMessages  implements SerializableAdapter
{
	@Id
	private long userId;
	@Id
	private long messageId;
	@ManyToOne
	//@PrimaryKeyJoinColumn(name="userId", referencedColumnName="ID")
	@JoinColumn(name = "userId", updatable = false, insertable = false, referencedColumnName = "id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;
	@ManyToOne
	//@PrimaryKeyJoinColumn(name="messageId", referencedColumnName="ID")
	@JoinColumn(name = "messageId", updatable = false, insertable = false, referencedColumnName = "id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Messages message;
	private boolean isNew = true;          // 是否处理过
	private boolean isDelete;              // 用户删除获取的消息。采用软删除的方式，以便追踪。
	
	public UsersMessages()
	{
		
	}
	
	public UsersMessages(long u, long m)
	{
		userId = u;
		messageId = m;
	}
	
	public UsersMessages(Users u, Messages m)
	{
		user = u;
		message = m;
	}
	public Users getUser()
	{
		return user;
	}
	public void setUser(Users user)
	{
		this.user = user;
		userId = user.getId();
	}
	public Messages getMessage()
	{
		return message;
	}
	public void setMessage(Messages message)
	{
		this.message = message;
		messageId = message.getId();
	}
	public boolean isNew()
	{
		return isNew;
	}
	public void setNew(boolean isNew)
	{
		this.isNew = isNew;
	}
	
	
	public boolean isDelete()
	{
		return isDelete;
	}
	public void setDelete(boolean isDelete)
	{
		this.isDelete = isDelete;
	}


	public static class UsersMessagesId implements SerializableAdapter
	{
		private long userId;
		private long messageId;
		
		public int hashCode() 
		{
			return (int)(userId + messageId);
		}
		
		public boolean equals(Object object)
		{
			if (object instanceof UsersMessagesId) 
			{
				UsersMessagesId otherId = (UsersMessagesId) object;
				return (otherId.userId == this.userId) && (otherId.messageId == this.messageId);
		    }
		    return false;
		}
	}
}
