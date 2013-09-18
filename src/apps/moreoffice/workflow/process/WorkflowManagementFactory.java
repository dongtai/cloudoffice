package apps.moreoffice.workflow.process;

import java.util.List;
import java.util.Map;

import org.jbpm.task.query.TaskSummary;

/**
 * 工作流的各种事务管理类。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class WorkflowManagementFactory
{

	private static ProcessHandler pm;
	private static GraphViewerHandler gvh;
	private String bpmnDir;
	private String jtaName;
	private String driverClass;
	private int maxPoolSize;
	private boolean allowLocalTransactions;
	private String user;
	private String password;
	private String databaseURL;
	private int port = 9123;
	private Map properties;
		
	public void dispose()
	{
		if (gvh != null)
		{
			gvh.dispose();
			gvh = null;
		}
		if (pm != null)
		{
			pm.dispose();
			pm = null;
		}
	}
		
	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getJtaName()
	{
		return jtaName;
	}

	public void setJtaName(String jtaName)
	{
		this.jtaName = jtaName;
	}

	public String getDriverClass()
	{
		return driverClass;
	}

	public void setDriverClass(String driverClass)
	{
		this.driverClass = driverClass;
	}

	public int getMaxPoolSize()
	{
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize)
	{
		this.maxPoolSize = maxPoolSize;
	}

	public boolean isAllowLocalTransactions()
	{
		return allowLocalTransactions;
	}

	public void setAllowLocalTransactions(boolean allowLocalTransactions)
	{
		this.allowLocalTransactions = allowLocalTransactions;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getDatabaseURL()
	{
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}

	public String getBpmnDir()
	{
		return bpmnDir;
	}

	public void setBpmnDir(String bpmnDir)
	{
		this.bpmnDir = bpmnDir;
	}

	
	public Map getProperties()
	{
		return properties;
	}

	public void setProperties(Map properties)
	{
		this.properties = properties;
	}

	public void init()
	{
		if (pm == null)
		{
			pm = new ProcessHandler();
			pm.setDirectory(bpmnDir);
			pm.setProperties(properties);
			pm.init(jtaName, driverClass, maxPoolSize, allowLocalTransactions, user, password, databaseURL);			
			gvh = new GraphViewerHandler(pm);
		}
	}
	
	/**
	 * 启动一个新的工作流实例。
	 * @param processId 需要启动的工作流定义ID
	 * @param startUser 启动工作流新实例的用户
	 * @param parameters 工作流的参数
	 * @return
	 */
	public void newProcess(String defId, String userId, Map<String, Object> params, String disc)
	{
		pm.startProcess(defId, userId, params, disc); 
	}
	
	public List<TaskSummary> getTask(String userId)
	{
		return pm.getAssignedTasks(userId);
	}
	
	public void completeTask(long taskId, Map data, String userId)
	{
		pm.completeTask(taskId, data, userId);
	}
	
	public ProcessHandler getProcessHandler()
	{
		return pm;
	}

	public GraphViewerHandler getGraphViewerHandler()
	{
		return gvh;
	}

}
