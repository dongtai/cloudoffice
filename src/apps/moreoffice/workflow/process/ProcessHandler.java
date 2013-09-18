package apps.moreoffice.workflow.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.definition.process.Process;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingAddCommentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingSetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

import apps.moreoffice.workflow.domain.FreeProcessDefined;
import apps.moreoffice.workflow.domain.ProcessInstanceUser;
import apps.transmanager.weboffice.domain.workflow.ProcessInstanceInfos;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * 工作流的流程处理类。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class ProcessHandler
{

	private static StatefulKnowledgeSession ksession;
	private static PoolingDataSource ds1;
	private String directory = "bpmresources";
	InstanceLogHandler  loggerHandler;	
	private final static long RESPONSE = 5000; 
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private TaskClient client;
	private Map properties;

	public ProcessHandler()
	{
	}
	
	public void setConnection(String ipAddress, int port)
	{
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void disconnect()
	{
		if (client != null)
		{
			try
			{
				client.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				client = null;
			}
		}
	}		
	
	public void dispose()
	{
		if (loggerHandler != null)
		{
			loggerHandler.dispose();
			loggerHandler = null;
		}
		if (ds1 != null)
		{
			ds1.close();
			ds1 = null;
		}
		if (ksession != null)
		{
			ksession.dispose();
			ksession = null;
		}
		disconnect();
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public void init(String jta, String classN, int size, boolean alt, String user, String pass, String url)
	{
		if (ds1 == null)
		{
			createDataSource(jta, classN, size, alt, user, pass, url);
		}
		getSession();
	}
	
	public void setProperties(Map p)
	{
		properties = p;
	}

	public void setDirectory(String directory)
	{
		this.directory = directory;
	}
	
	private void createDataSource(String jta, String classN, int size, boolean alt, String user, String pass, String url)
	{
		if (ds1 == null)
		{
	        ds1 = new PoolingDataSource();
	        ds1.setUniqueName(jta);
	        ds1.setClassName(classN);
	        ds1.setMaxPoolSize(size);
	        ds1.setAllowLocalTransactions(alt);
	        ds1.getDriverProperties().put("user", user);
	        ds1.getDriverProperties().put("password", pass);
	        ds1.getDriverProperties().put("URL", url);
	        ds1.init();
		}
    }
	
	private void createDataSource()
	{
		if (ds1 == null)
		{
	        ds1 = new PoolingDataSource();
	        ds1.setUniqueName("jdbc/webofficeDS");
	        ds1.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
	        ds1.setMaxPoolSize(3);
	        ds1.setAllowLocalTransactions(true);
	        ds1.getDriverProperties().put("user", "root");
	        ds1.getDriverProperties().put("password", "123456");
	        ds1.getDriverProperties().put("URL", "jdbc:mysql://localhost:3306/jbpmtest");
	        ds1.init();
	        
	        properties = new HashMap();
	    	properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	    	properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
	    	properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/jbpmtest");
	    	properties.put("hibernate.connection.username", "root");
	    	properties.put("hibernate.connection.password", "123456");
	    	properties.put("hibernate.connection.autocommit", "true");
	    	properties.put("hibernate.max_fetch_depth", "3");
	    	properties.put("hibernate.hbm2ddl.auto", "update");
	    	properties.put("hibernate.show_sql", "true");
		} 
    }
	
	private StatefulKnowledgeSession newStatefulKnowledgeSession()
	{
		try
		{
			createDataSource();
			Configuration conf = TransactionManagerServices.getConfiguration();
			conf.setDefaultTransactionTimeout(30000);
			KnowledgeBase kbase =  KnowledgeBaseFactory.newKnowledgeBase();
			URL url = this.getClass().getClassLoader().getResource(directory);
			File file = new File(url.toURI());
			if (!file.exists())
			{
				throw new IllegalArgumentException("Could not find " + directory);
			}
			if (!file.isDirectory())
			{
				throw new IllegalArgumentException(directory + " is not a directory");
			}
			ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
			ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
			ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
			BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			for (File subfile : file.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".bpmn") || name.endsWith("bpmn2");
				}
			}))
			{
				System.out.println("Loading process from file system: "	+ subfile.getName());
				kbuilder.add(ResourceFactory.newFileResource(subfile),	ResourceType.BPMN2);
			}
			kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			
			StatefulKnowledgeSession ksession = null;			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.jpda", properties);
			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			env.set(EnvironmentName.TRANSACTION, TransactionManagerServices.getTransactionManager());
			
			Properties properties = new Properties();
			properties.put("drools.processInstanceManagerFactory",	"org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
			properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
			KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
			//ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
			//ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(1,	kbase, config, env);
			try
			{
				System.out.println("Loading session data ...");
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(1,	kbase, config, env);
			}
			catch (RuntimeException e)
			{
				System.out.println("Error loading session data: " + e.getMessage());
				if (e instanceof IllegalStateException)
				{
					Throwable cause = ((IllegalStateException) e).getCause();
					if (cause instanceof InvocationTargetException)
					{
						cause = cause.getCause();
						if (cause != null && "Could not find session data for id 1".equals(cause.getMessage()))
						{
							System.out.println("Creating new session data ...");
							env = KnowledgeBaseFactory.newEnvironment();
							env.set(EnvironmentName.ENTITY_MANAGER_FACTORY,	emf);
							env.set(EnvironmentName.TRANSACTION, TransactionManagerServices.getTransactionManager());
							ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
						}
						else
						{
							System.err.println("Error loading session data: " + cause);
							throw e;
						}
					}
					else
					{
						System.err.println("Error loading session data: " + cause);
						throw e;
					}
				}
				else
				{
					System.err.println("Error loading session data: " + e.getMessage());
					throw e;
				}
			}
			CommandBasedWSHumanTaskHandler handler = new CommandBasedWSHumanTaskHandler(ksession);			
			connect();			
			//handler.setConnection("127.0.0.1", port);
			handler.setClient(client);
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task",	handler);
			handler.connect();
			System.out.println("Successfully loaded default package from Guvnor");
			loggerHandler = new InstanceLogHandler(ksession);
			return ksession;
		}
		catch (Throwable t)
		{
			throw new RuntimeException("Could not initialize stateful knowledge session: "	+ t.getMessage(), t);
		}
	}
	
	private void beginTransaction()
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			
			
			ut.commit();
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
	}
	
	/**
	 * 当服务重新启动的时候，重新装载非预定义的自由流程，并加入到统一的流程管理中。
	 */
	private void reloadProcesses()
	{
		
	}
	
	public void saveProcess()
	{
		
	}
	

	public StatefulKnowledgeSession getSession()
	{
		if (ksession == null)
		{
			ksession = newStatefulKnowledgeSession();
		}
		return ksession;
	}

	/**
	 * 取得当前环境中的所有工作流流程定义。包括预定义的固定流程和非预定义的自由流程
	 * @return 返回工作流定义对象列表
	 */
	public List<Process> getProcesses()
	{
		List<Process> result = new ArrayList<Process>();
		result.addAll(getSession().getKnowledgeBase().getProcesses());
		/*List<Process> result = new ArrayList<Process>();
		for (KnowledgePackage kpackage : getSession().getKnowledgeBase().getKnowledgePackages())
		{
			result.addAll(kpackage.getProcesses());
		}*/
		return result;
	}

	/**
	 * 取得工作流定义Id为processId的工作流流程定义。包括预定义的固定流程和非预定义的自由流程
	 * @param processId 工作流定义Id
	 * @return 返回需要的工作流
	 */
	public Process getProcess(String processId)
	{
		return ksession.getKnowledgeBase().getProcess(processId);
		/*for (KnowledgePackage kpackage : getSession().getKnowledgeBase().getKnowledgePackages())
		{
			for (Process process : kpackage.getProcesses())
			{
				if (processId.equals(process.getId()))
				{
					return process;
				}
			}
		}
		return null;*/
	}

	/**
	 * 取得名字为name的工作流流程定义
	 * @param name 工作流的名字
	 * @return
	 */
	public Process getProcessByName(String name)
	{
		Collection<Process> process = getSession().getKnowledgeBase().getProcesses();
		for (Process temp : process)
		{
			if (name.equals(temp.getName()))
			{
				return temp;
			}			
		}
		return null;
		/*for (KnowledgePackage kpackage : getSession().getKnowledgeBase().getKnowledgePackages())
		{
			for (Process process : kpackage.getProcesses())
			{
				if (name.equals(process.getName()))
				{
					return process;
				}
			}
		}
		return null;*/
	}

	/**
	 * 通过工作流实例Id获取工作流实例值
	 * @param processInstanceId
	 * @return
	 */
	public ProcessInstanceLog getProcessInstanceLog(long processInstanceId)
	{
		return loggerHandler.findProcessInstance(processInstanceId);
	}

	/**
	 * 获取工作流实例id为processInstanceId的节点实例	
	 * @param processInstanceId
	 * @return
	 */
	public List<NodeInstanceLog> getNodeInstances(long processInstanceId)
	{
		return loggerHandler.findNodeInstances(processInstanceId);
	}
	
	/**
	 * 通过工作流定义id获取所有相关工作流实例值
	 * @param processInstanceId
	 * @return
	 */
	public List<ProcessInstanceLog> getProcessInstanceLogsByProcessId(String processId)
	{
		return loggerHandler.findProcessInstances(processId);
	}

	public void save(Object o)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			em.persist(o);
			
			ut.commit();
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}		
	}
	
	public Object find(Long id)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			Object o = em.find(FreeProcessDefined.class, id);
			
			ut.commit();
			return o;
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
		
		return null;
	}
	
	/**
	 * 无流程参数的方式启动一个工作流的新实例。
	 * @param processId 需要启动的工作流定义ID 
	 * @param startUser 启动工作流新实例的用户
	 * @return
	 */
	public ProcessInstance startProcess(String processId, String startUser, String disc)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			ProcessInstance ret = ksession.startProcess(processId, null);
			loggerHandler.addProcessInstanceUser(startUser, ret.getId(), ret.getProcessName(), disc);
			
			ut.commit();
			return ret;
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
		
		return null;
	}
	
	/**
	 * 启动一个新的工作流实例。
	 * @param processId 需要启动的工作流定义ID
	 * @param startUser 启动工作流新实例的用户
	 * @param parameters 工作流的参数
	 * @return
	 */
	public ProcessInstance startProcess(String processId, String startUser, Map<String, Object> parameters, String disc)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			ProcessInstance ret = ksession.startProcess(processId, parameters);
			loggerHandler.addProcessInstanceUser(startUser, ret.getId(), ret.getProcessName(), disc);
			
			ut.commit();
			return ret;
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
		
		return null;
	}
	
	/**
	 * 无流程参数的方式启动一个工作流的新实例。
	 * @param processId 需要启动的工作流定义ID 
	 * @param startUser 启动工作流新实例的用户
	 * @param pn 用户自定义的工作流流程名称
	 * @return
	 */
	public ProcessInstance startProcess(String processId, String startUser, String pn, String disc)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			ProcessInstance ret = ksession.startProcess(processId, null);
			loggerHandler.addProcessInstanceUser(startUser, ret.getId(), pn, disc);
			 
			ut.commit();
			return ret;
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
		
		return null;
	}
	
	/**
	 * 启动一个新的工作流实例。
	 * @param processId 需要启动的工作流定义ID
	 * @param startUser 启动工作流新实例的用户
	 * @param pn 用户自定义的工作流流程名称
	 * @param parameters 工作流的参数
	 * @return
	 */
	public ProcessInstance startProcess(String processId, String startUser, String pn, Map<String, Object> parameters, String disc)
	{
		UserTransaction ut = null;
		try
		{			
			ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();			
			EntityManager em = (EntityManager)ksession.getEnvironment().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			em.joinTransaction();
			
			ProcessInstance ret = ksession.startProcess(processId, parameters);
			loggerHandler.addProcessInstanceUser(startUser, ret.getId(), pn, disc);
			
			ut.commit();
			return ret;
		}
		catch ( Exception e )
		{
			if (ut != null)
			{
				try
				{
					ut.rollback();
				}
				catch(Exception ee)
				{	
				}
			}
			e.printStackTrace();
		}
		finally
		{
		}
		
		return null;
	}
	
	/**
	 * 获取工作流实例的信息
	 * @param processId
	 * @return
	 */
	public List<ProcessInstanceInfos> getProcessInstanceInfo(long processId)
	{
		return loggerHandler.getProcessInstanceInfo(processId);
	}
	
	/**
	 * 获取由uid用户启动的工作流流程
	 * @param uid
	 * @return
	 */
	public List<ProcessInstanceInfos> getProcessInstanceInfoByUserId(String uid)
	{
		return loggerHandler.getProcessUserInstanceInfo(uid);
	}
	
	/**
	 * 获取由uid用户启动的工作流流程
	 * @param uid
	 * @return
	 */
	public List<ProcessInstanceUser> getUserStartedProcess(String uid)
	{
		return loggerHandler.getProcessInstanceUser(uid);
	}
	
	/**
	 * 取得流程实例Id为processInstanceid的用户开始执行的工作流流程
	 * @param processInstanceId 流程实例Id
	 * @return
	 */
	public ProcessInstanceUser getProcessInstanceByProcessInstanceId(long processInstanceId)
	{
		return loggerHandler.getProcessInstanceByProcessInstanceId(processInstanceId);
	}
	
	/**
	 * 通过流程实例Id获得列出的定义名字
	 */
	public String getProcessName(long processInstanceId)
	{
		return loggerHandler.getProcessName(processInstanceId);
	}
	
	/**
	 * 通过流程实例Id取得该流程实例中已经执行过或已经分配的流程任务Id集合值。
	 * @param processInstanceId
	 * @return
	 */
	public List<Long> getProcessTaskId(long processInstanceId)
	{
		return loggerHandler.getProcessTaskId(processInstanceId);
	}

	/**
	 * 终止某个工作流流程
	 * @param processInstanceId
	 */
	public void abortProcessInstance(long processInstanceId)
	{
		try
		{
			ksession.abortProcessInstance(processInstanceId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得某个活动流程实例
	 * @param processInstanceId
	 * @return
	 */
	public ProcessInstance getProcessInstance(long processInstanceId)
	{
		return ksession.getProcessInstance(processInstanceId);
	}

	/**
	 * 获取活动工作流流程中的变量及变量的值。
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, Object> getProcessInstanceVariables(long processInstanceId)
	{
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		if (processInstance != null)
		{
			Map<String, Object> variables = ((WorkflowProcessInstanceImpl) processInstance).getVariables();
			if (variables == null)
			{
				return new HashMap<String, Object>();
			}
			return variables;
		}
		else
		{
			throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
		}
	}

	/**
	 * 设置活动工作流流程中的变量名及相应的值。
	 * @param processInstanceId
	 * @param variables
	 */
	public void setProcessInstanceVariables(long processInstanceId,	Map<String, Object> variables)
	{
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		if (processInstance != null)
		{
			WorkflowProcessInstanceImpl wpi = (WorkflowProcessInstanceImpl)processInstance;
			for (Map.Entry<String, Object> entry : variables.entrySet())
			{
				wpi.setVariable(entry.getKey(), entry.getValue());
			}
		}
		else
		{
			throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
		}
	}

	/**
	 * 
	 * @param executionId
	 * @param signal
	 */
	public void signalExecution(String executionId, String signal)
	{
		ksession.getProcessInstance(new Long(executionId)).signalEvent("signal", signal);
	}
	
	/**
	 * 流程客户端连接
	 */
	private void connect()
	{
		if (client == null)
		{
			client = new TaskClient(new MinaTaskClientConnector("WSHumanTaskHandler client", 
					new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			boolean connected = client.connect(ipAddress, port);
			if (!connected)
			{
				throw new IllegalArgumentException("Could not connect task client");
			}
		}
	}

	/**
	 * 根据任务id获取某个任务
	 * @param taskId 任务id
	 * @return
	 */
	public Task getTaskById(long taskId)
	{
		connect();
		BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
		client.getTask(taskId, responseHandler);
		return responseHandler.getTask();
	}
	
	/**
	 * 获得某个任务的内容
	 * @param contentId 内容id
	 * @return
	 */
	public Content getContent(long contentId)
	{
		connect();
		BlockingGetContentResponseHandler handlerC = new BlockingGetContentResponseHandler();
        client.getContent(contentId, handlerC);
        return handlerC.getContent();
	}

	/**
	 * 执行完成某任务
	 * @param taskId 任务id
	 * @param data 任务执行数据
	 * @param userId 执行任务者（登录名）
	 */	
	public void completeTask(long taskId, Map data, String userId)
	{
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new FileTaskOperationResponseHandler();
		client.start(taskId, userId, responseHandler);
		responseHandler.waitTillDone(RESPONSE);
		responseHandler = new FileTaskOperationResponseHandler();
		ContentData contentData = null;
		if (data != null)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try
			{
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		client.complete(taskId, userId, contentData, responseHandler);
		responseHandler.waitTillDone(RESPONSE);
	}

	/**
	 * 把某个用户名下的某个任务放弃, 放弃后该任务处于无人员可以执行，要执行需要重新赋予执行人员
	 * @param taskId 需要放弃的任务
	 * @param userId 需要放弃任务的用户
	 */
	public void releaseTask(long taskId, String userId)
	{
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new FileTaskOperationResponseHandler();
		client.release(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	
	/**
	 * 把某个任务重新赋予新用户，在重新把任务赋予新用户之前，需要先把该任务的原有用户释放，也就是需要先调用releaseTask方法。
	 * 任务重新赋予新用户后，只有该新用户可以执行该任务，原有用户不能再执行该任务了
	 * @param taskId 需要放弃的任务
	 * @param userId 需要放弃任务的用户
	 */
	public void claimTask(long taskId, String userId)
	{
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new FileTaskOperationResponseHandler();
		client.claim(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	
	/**
	 * 把某个用户名下的某个任务重新赋予给另外一个用户，赋予成功后，某用户不再能执行该任务，被赋予的新用户可以执行该任务
	 * @param taskId 需要赋予的任务
	 * @param userId 需要赋予任务的用户
	 * @param targetId 被赋予任务的用户
	 */
	public void reassignTask(long taskId, String userId, String targetId)
	{
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new FileTaskOperationResponseHandler();
		client.release(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
		
		responseHandler = new FileTaskOperationResponseHandler();
		client.claim(taskId, targetId, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	
	
	/**
	 * 把某个用户名下的某个任务委托给另外一个用户，委托成功后，某用户任然可以执行该任务，被委托用户也可以执行该任务
	 * @param taskId 需要委托的任务
	 * @param userId 需要委托任务的用户
	 * @param targetId 被委托任务的用户
	 */
	public void delegateTask(long taskId, String userId, String targetId)
	{
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new FileTaskOperationResponseHandler();
		client.delegate(taskId, userId, targetId, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	

	/**
	 * 获得用户拥有的任务列表，在该列表中包含已经执行完成的任务。
	 * @param userId
	 * @return
	 */
	public List<TaskSummary> getOwnedTasks(String userId)
	{
		connect();
		try
		{
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksOwned(userId, "en-UK", responseHandler);
			return responseHandler.getResults();			
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return new ArrayList<TaskSummary>();
	}
	
	/**
	 * 获得用户拥有的已经执行完成的任务列表。
	 * @param userId
	 * @return
	 */
	public List<TaskSummary> getOwnedCompletedTasks(String userId)
	{
		connect();
		try
		{
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksOwned(userId, "en-UK", responseHandler);
			List<TaskSummary> ret = responseHandler.getResults();
			if (ret != null && ret.size() > 0)
			{
				int size = ret.size();
				TaskSummary ts;
				ArrayList<TaskSummary> cret = new ArrayList<TaskSummary>();
				for (int i = 0; i < size; i++)
				{
					ts = ret.get(i);
					Status st = ts.getStatus(); 
					if (st == Status.Completed || st == Status.Failed ||  st == Status.Error
							 || st == Status.Exited || st == Status.Obsolete)
					{
						cret.add(ts);
					}
				}
				return cret;
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return new ArrayList<TaskSummary>();
	}

	/**
	 * 获得用户可执行的任务列表，在该列表中不包含已经执行完成的任务。
	 * 该任务可以不属于用户拥有，但其有可以执行的权限
	 * @param userId
	 * @return
	 */
	public List<TaskSummary> getAssignedTasks(String userId)
	{
		connect();
		try
		{
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksAssignedAsPotentialOwner(userId, "en-UK", responseHandler);
			//client.getTasksAssignedAsPotentialOwner(userId, groupId, "en-UK", responseHandler);
			return responseHandler.getResults();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return new ArrayList<TaskSummary>();
	}
	
	/**
	 * 给某个任务增加注释
	 * @param taskId 任务的任务ID值
	 * @param userId 添加注释的人员
	 * @param comments 具体注释的内容
	 */
	public void addTaskComment(long taskId, String userId, String comments)
	{
		connect();
		try
		{
			Comment comment = new Comment();
			comment.setText(comments);
			comment.setAddedBy(new User(userId));
			comment.setAddedAt(new Date());
			client.addComment(taskId, comment, new BlockingAddCommentResponseHandler());			
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	/**
	 * 给某个任务增加表单内容, 即是保存用户任务的表单内容。
	 * @param taskId 任务的任务ID值
	 * @param comments 具体的内容
	 */
	public void addTaskFormContent(long taskId, Map<String, String> data)
	{
		connect();
		try
		{
			Content content = null;
			if (data != null)
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out;
				try
				{
					out = new ObjectOutputStream(bos);
					out.writeObject(data);
					out.close();
					content = new Content(bos.toByteArray());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			client.setDocumentContent(taskId, content, new BlockingSetContentResponseHandler());			
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	/**
	 * 返回某个任务的表单内容，为任务继续执行提供先前保存的数据或为历史查询提供数据
	 * @param taskId 任务的任务ID值
	 */
	public Map<String, String> getTaskFormContent(long taskId)
	{
		connect();
		try
		{
			BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
			client.getTask(taskId, responseHandler);
			Task task = responseHandler.getTask();
			if (task != null)
			{
				TaskData taskData = task.getTaskData();
				long cId = -1;
				if (taskData != null && (cId = taskData.getDocumentContentId()) != -1)
				{
					BlockingGetContentResponseHandler handlerC = new BlockingGetContentResponseHandler();
			        client.getContent(cId, handlerC);
			        Content content = handlerC.getContent();
			        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());	       
			        ObjectInputStream ois = new ObjectInputStream(bais);
			        Object o = ois.readObject();
			        if (o instanceof Map)
			        {
				        Map<String, String> data =(Map<String, String>)o; 
				        return data;
			        }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
        
	}
	
	/**
	 * 返回某个任务的表单内容及任务名字；任务名字的key值为"taskNameValue"
	 * @param taskId 任务的任务ID值
	 */
	public Map<String, String> getTaskFormContentAndName(long taskId)
	{
		connect();
		try
		{
			BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
			client.getTask(taskId, responseHandler);
			Task task = responseHandler.getTask();
			if (task != null)
			{
				TaskData taskData = task.getTaskData();
				long cId = -1;
				if (taskData != null && (cId = taskData.getDocumentContentId()) != -1)
				{
					BlockingGetContentResponseHandler handlerC = new BlockingGetContentResponseHandler();
			        client.getContent(cId, handlerC);
			        Content content = handlerC.getContent();
			        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());	       
			        ObjectInputStream ois = new ObjectInputStream(bais);
			        Object o = ois.readObject();
			        if (o instanceof Map)
			        {
				        Map<String, String> data =(Map<String, String>)o; 
				        List<I18NText> names = task.getNames();
				        if (names != null && names.size() > 0)
				        {
				        	data.put("taskNameValue", names.get(0).getText());
				        }
				        data.put("taskStatus", getStatus(taskData.getStatus()));
				        return data;
			        }
			        else     // 没有内容
			        {
			        	 Map<String, String> data = new HashMap<String, String>();
			        	 List<I18NText> names = task.getNames();
			        	 if (names != null && names.size() > 0)
					     {
					       	data.put("taskNameValue", names.get(0).getText());
					     }
			        	 PeopleAssignments pa = task.getPeopleAssignments();
			        	 if (pa != null)
			        	 {
			        		 List<OrganizationalEntity> list =  pa.getPotentialOwners();
			        		 if (list != null && list.size() > 0)
			        		 {
			        			 StringBuffer sb = new StringBuffer();
			        			 for (OrganizationalEntity oe : list)
			        			 {
			        				 sb.append(oe.getId());
			        				 sb.append(",");
			        			 }
			        			 data.put("userId", sb.toString());
			        		 }
			        	 }
			        	 data.put("taskStatus", getStatus(taskData.getStatus()));
					     return data;
			        }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
        
	}
	
	private static String getStatus(Status st)
	{
		switch(st.ordinal())
		{
			case 0:
				return "创建好";
				
			case 1:
				return "准备好";
				
			case 2:
				return "已分配";
				
			case 3:
				return "处理中";
				
			case 4:
				return "挂起中";
				
			case 5:
				return "已完成";
				
			case 6:
				return "失败";
				
			case 7:
				return "错误";
				
			case 8:
				return "退出";
				
			case 9:
				return "废止";
				
			default :
				return "";
		}	
	}

}
