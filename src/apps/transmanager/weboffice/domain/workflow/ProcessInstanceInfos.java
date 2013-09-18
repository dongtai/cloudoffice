package apps.transmanager.weboffice.domain.workflow;

import java.util.Date;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 流程实例信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
public class ProcessInstanceInfos implements SerializableAdapter
{
	/**
	 * 流程实例状态常量
	 */
	public final static int STATE_PENDING   = 0;
	public final static int STATE_ACTIVE    = 1;
	public final static int STATE_COMPLETED = 2;
	public final static int STATE_ABORTED   = 3;
	public final static int STATE_SUSPENDED = 4;
	
	/**
	 * 流程实例Id
	 */
	private long id;
	/**
	 * 流程定义Id
	 */
	private String definitionId;
	/**
	 * 流程实例开始时间
	 */
	private Date startDate;
	/**
	 * 流程实例结束时间，流程实例没有结束，则该时间为空
	 */
	private Date endDate;
	/**
	 * 流程实例状态
	 */
	private int state;
	/**
	 * 流程发起者
	 */
	private String startUser;
	/**
	 * 流程发起描述说明
	 */
	private String description;
	/**
	 * 流程名字
	 */
	private String name;
	
	public ProcessInstanceInfos()
	{		
	}

	public ProcessInstanceInfos(String definitionId, long id, Date startDate,
			Date endDate, String startUser, String description, String name)
	{	
		this.definitionId = definitionId;
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startUser = startUser;
		this.description = description;
		this.name = name; 
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getState()
	{
		if (endDate == null)
		{
			return STATE_ACTIVE;
		}
		return STATE_COMPLETED;
		//return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public String getStartUser()
	{
		return startUser;
	}

	public void setStartUser(String startUser)
	{
		this.startUser = startUser;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getDefinitionId()
	{
		return definitionId;
	}

	public void setDefinitionId(String definitionId)
	{
		this.definitionId = definitionId;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public boolean isRunning()
	{
		return this.startDate != null && !isSuspended();
	}

	public boolean hasEnded()
	{
		return this.startDate != null && this.endDate != null;
	}

	public boolean isSuspended()
	{
		return null == this.endDate && state == STATE_PENDING;
	}


	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		ProcessInstanceInfos that = (ProcessInstanceInfos) o;

		if (definitionId != null ? !definitionId.equals(that.definitionId)
				: that.definitionId != null)
		{
			return false;
		}
		if (id != that.id)
		{
			return false;
		}
		return true;
	}

}
