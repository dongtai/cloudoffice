package apps.moreoffice.workflow.server;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

//import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * 工作流的人工任务流程服务器。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class WorkFlowServer
{
	private boolean start;
	private int port = 9123;
	private TaskService taskService;
	private Map<String, String> properties;
	
	public void startService()
	{
		// PoolingDataSource ds1 = new PoolingDataSource();
		// ds1 = new PoolingDataSource();
		// ds1.setUniqueName( "jdbc/webofficeDS" );
		// ds1.setClassName( "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		// ds1.setMaxPoolSize( 3 );
		// ds1.setAllowLocalTransactions( true );
		// ds1.getDriverProperties().put( "user", "root" );
		// ds1.getDriverProperties().put( "password", "123456" );
		// ds1.getDriverProperties().put( "URL",
		// "jdbc:mysql://localhost:3306/jbpmtest" );
		// ds1.init();

		if (start)  // 服务已经启动
		{
			return;
		}
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task", properties);
		taskService = new TaskService(emf,	SystemEventListenerFactory.getSystemEventListener());
		initAdmin();
		// start server
		MinaTaskServer server = new MinaTaskServer(taskService, port);
		Thread thread = new Thread(server);
		thread.start();		
		start = true;
		System.out.println("ProcessTask service started correctly!\nProcessTask service running ...");
	}
	
	private void initAdmin()
	{
		TaskServiceSession taskSession = taskService.createSession();
		EntityManager em = taskSession.getEntityManager();
		User user = em.find(User.class, "Administrator");
		if (user == null)
		{
			user = new User("Administrator");
			taskSession.addUser(user);
			/*user = new User("admin");
			taskSession.addUser(user);
			user = new User("sysAdmin");
			taskSession.addUser(user);
			user = new User("auditAdmin");
			taskSession.addUser(user);
			user = new User("securityAdmin");
			taskSession.addUser(user);*/
		}
		taskSession.dispose();
	}
	
	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
		
	public Map<String, String> getProperties()
	{
		return properties;
	}

	/**
	 * 设置JPA对数据库操作的各种属性
	 * @param properties
	 */
	public void setProperties(Map<String, String> properties)
	{
		this.properties = properties;
	}

	/**
	 * 增加用户
	 * @param userId 用户的登录名
	 */
	public void addUser(String userId)
	{
		try
		{
			TaskServiceSession taskSession = taskService.createSession();
			EntityManager em = taskSession.getEntityManager();
			User user = em.find(User.class, userId);
			if (user == null)
			{
				// Add users
				user = new User(userId);
				taskSession.addUser(user);
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}
	}
	
	/**
	 * 修改用户。
	 * @param oldId
	 * @param newId
	 */	
	public void modifyUser(String oldId, String newId)
	{
		try
		{
			if (oldId.equals(newId))
			{
				return;
			}
			TaskServiceSession taskSession = taskService.createSession();
			// modify users
			EntityManager em = taskSession.getEntityManager();
			if (em != null)
			{
				final EntityTransaction tx = em.getTransaction();
		        try
		        {
		            if (!tx.isActive())
		            {
		                tx.begin();
		            }
		            User user = em.find(User.class, oldId);
		            if (user != null)
		            {
						user.setId(newId);
						em.merge(user);
		            }
		            else
		            {
		            	user = new User(newId);
		            	em.persist(user);
		            }
		            tx.commit();			            
		        }
		        finally
		        {
		            if (tx.isActive()) 
		            {
		                tx.rollback();
		            }
		        }				
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 批量增加用户
	 * @param userIds
	 */	
	public void addUsers(List<String> userIds)
	{
		try
		{
			TaskServiceSession taskSession = taskService.createSession();
			EntityManager em = taskSession.getEntityManager();
			// Add users
			for (String userId : userIds)
			{
				User user = em.find(User.class, "userId");
				if (user == null)
				{
					user = new User(userId);
					taskSession.addUser(user);
				}
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 增加用户组
	 * @param groupId
	 */
	public void addGroup(String groupId)
	{
		try
		{
			TaskServiceSession taskSession = taskService.createSession();
			EntityManager em = taskSession.getEntityManager();
			Group group = em.find(Group.class, groupId);
			if (group == null)
			{
				// Add users
				group = new Group(groupId);
				taskSession.addGroup(group);
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改用户组
	 * @param oldId
	 * @param groupId
	 */
	public void modifyGroup(String oldId, String groupId)
	{
		try
		{
			if (oldId.equals(groupId))
			{
				return;
			}
			TaskServiceSession taskSession = taskService.createSession();
			// modify group			
			EntityManager em = taskSession.getEntityManager();
			if (em != null)
			{
				final EntityTransaction tx = em.getTransaction();
		        try
		        {
		            if (!tx.isActive())
		            {
		                tx.begin();
		            }
		            Group user = em.find(Group.class, oldId);
		            if (user != null)
		            {
						user.setId(groupId);
						em.merge(user);
		            }
		            else
		            {
		            	user = new Group(groupId);
		            	em.persist(user);
		            }
		            tx.commit();			            
		        }
		        finally
		        {
		            if (tx.isActive()) 
		            {
		                tx.rollback();
		            }
		        }				
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 批量增加用户组
	 * @param groupIds
	 */
	public void addGroups(List<String> groupIds)
	{
		try
		{
			TaskServiceSession taskSession = taskService.createSession();
			EntityManager em = taskSession.getEntityManager();			
			// Add users
			for (String groupId : groupIds)
			{
				Group group = em.find(Group.class, groupId);
				if (group == null)
				{
					group = new Group(groupId);
					taskSession.addGroup(group);
				}
			}
			taskSession.dispose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
