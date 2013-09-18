package apps.transmanager.weboffice.domain;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class TaskExcutor implements SerializableAdapter
{
	public final static int USER = 0;       // 节点执行者类型为用户
	public final static int ROLE = USER + 1; // 节点执行者类型为角色
	public final static int ORG = ROLE + 1;  // 节点执行者类型为组织
	public final static int GROUP = ORG + 1; //  节点执行者类型为组
	
	private int type;             // 节点执行者的类型
	private Long id;              // 节点执行者ID。
	
	public TaskExcutor()
	{	
	}
	public TaskExcutor(int type, Long id)
	{
		this.type = type;
		this.id = id;
	}
	
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public boolean equals(TaskExcutor e)
	{
		if (e == null)
		{
			return false;
		}
		if (e.type != type)
		{
			return false;
		}
		if (id == null)
		{
			return e.id == null;
		}
		if (e.id == null)
		{
			return id == null;
		}
		return e.id.longValue() == id.longValue();
	}
	
}
