package apps.transmanager.weboffice.domain.workflow;

import java.util.Date;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户任务信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
@ SuppressWarnings("serial")
public class TaskInfo implements SerializableAdapter
{
	/**
	 * 任务状态常量定义
	 */
	public final static int Created = 0;
	public final static int Ready = 1; 
	public final static int Reserved = 2;
	public final static int InProgress = 3;
	public final static int Suspended = 4;
	public final static int Completed = 5;
	public final static int Failed = 6; 
	public final static int Error = 7;
	public final static int Exited = 8;
	public final static int Obsolete = 9;
	/**
	 * 任务实例Id
	 */
	private long id;
	/**
	 * 流程实例id
	 */
	private long processInstanceId;
	/**
	 * 
	 */
	private int processInstanceState;
	
	/**
	 * 任务名
	 */
	private String name;

	/**
	 * 任务当前状态
	 */
	private int currentState;
	/**
	 * 任务逾期时间
	 */
	private Date dueDate;
	/**
	 * 任务达到时间
	 */
	private Date createDate;
	/**
	 * 任务优先级
	 */
	private int priority;

	/**
	 * 任务说明
	 */
	private String description;
	
	/**
	 * 流程名
	 */
	private String processName;
		
	/**
	 * 流程实例描述
	 */
	private String processDes;
	
	/**
	 * 流程实例发起者
	 */
	private String processUser;
	
	public TaskInfo()
	{
	}

	public TaskInfo(long id, long processInstanceId, String name, int currentState,
			Date dueDate, Date createDate, int priority, String description,
			String processName, String processDes, String processUser, int processInstanceState)
	{
		this.id = id;
		this.processInstanceId = processInstanceId;
		this.name = name;
		this.currentState = currentState;
		this.dueDate = dueDate;
		this.createDate = createDate;
		this.priority = priority;
		this.description = description;
		this.processName = processName;
		this.processDes = processDes;
		this.processUser = processUser;
		this.processInstanceState = processInstanceState;
	}
	

	public String getProcessUser()
	{
		return processUser;
	}

	public void setProcessUser(String processUser)
	{
		this.processUser = processUser;
	}

	
	public String getProcessName()
	{
		return processName;
	}

	public void setProcessName(String processName)
	{
		this.processName = processName;
	}

	public String getProcessDes()
	{
		return processDes;
	}

	public void setProcessDes(String processDes)
	{
		this.processDes = processDes;
	}
	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getProcessInstanceId()
	{
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId)
	{
		this.processInstanceId = processInstanceId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getCurrentState()
	{
		return currentState;
	}


	public Date getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(Date dueDate)
	{
		this.dueDate = dueDate;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String toString()
	{
		return "TaskInfo{id:" + id + ",state:" + currentState + "}";
	}

    /**
     * @param processInstanceState The processInstanceState to set.
     */
    public void setProcessInstanceState(int processInstanceState)
    {
        this.processInstanceState = processInstanceState;
    }

    /**
     * @return Returns the processInstanceState.
     */
    public int getProcessInstanceState()
    {
        return processInstanceState;
    }
}
