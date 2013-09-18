package apps.moreoffice.ext.jcr;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.jcr.Repository;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.api.management.DataStoreGarbageCollector;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.RepositoryConfigurationParser;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springmodules.jcr.RepositoryFactoryBean;
import org.xml.sax.InputSource;

import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * jackrabbit库创建bean。
 * 在该类中，通过配置设置，可以连接同应用绑定的文档库，也可以连接远程的文档库。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class YozoRepositoryFactoryBean extends RepositoryFactoryBean
{
	private boolean standalone;              // 是否是独立的文档库。
	private String standaloneURL;
	private boolean datastore;
	private Timer timer;
	
	/**
	 * 默认文件库配置文件名
	 */
	private static final String DEFAULT_CONF_FILE = "repository.xml";

	/**
	 * 默认文件库路径
	 */
	private static final String DEFAULT_REP_DIR = ".";

	/**
	 * 主路径
	 */
	private Resource homeDir;

	/**
	 * 文件库的配置
	 */
	private RepositoryConfig repositoryConfig;
	
	// 文件库配置文件中变量
	private Properties variables;
	
	private static final long delay = 60 * 1000;
	private static final long period = 24 * 60 * 60 * 1000;
	
	
	public void afterPropertiesSet() throws Exception 
	{
		super.afterPropertiesSet();
		if (!standalone && datastore)
		{
			timer = new Timer(true);
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					DataStoreGarbageCollector gc = null;
				    try 
				    {
				    	gc = ((RepositoryImpl)repository).createDataStoreGarbageCollector();
				        gc.mark();
				        gc.sweep();
				    }
				    catch(Exception e)
				    {
				    	LogsUtility.error(e);
				    }
				    finally 
				    {
				    	if (gc != null)
				    	{
				    		gc.close();
				    	}
				    }
				}
			}
		    ,delay, period);
		}
	}
	
	protected Repository createRepository() throws Exception 
	{
		if (standalone)    // 独立的文档库
		{
			return new URLRemoteRepository(standaloneURL);
		}
		else
		{
			return RepositoryImpl.create(repositoryConfig);
		}		
	}
	
	
	
	/** 
	 */
	protected void resolveConfigurationResource() throws Exception
	{
		if (repositoryConfig != null)
		{
			return;
		}
		if (this.configuration == null)
		{
			configuration = new ClassPathResource(DEFAULT_CONF_FILE);
		}

		if (homeDir == null)
		{
			homeDir = new FileSystemResource(DEFAULT_REP_DIR);
		}
		String path = homeDir.getFile().getAbsolutePath();
		variables.put(RepositoryConfigurationParser.REPOSITORY_HOME_VARIABLE, path);    // 文件库主目录。
		String dataStore = (String)variables.get("datastore");
		if (dataStore == null || dataStore.length() <= 0)
		{				
			variables.put("datastore", path);
		}
		repositoryConfig = RepositoryConfig.create(new InputSource(configuration.getInputStream()), variables);
	}

	/**
	 * 
	 */
	public void destroy() throws Exception
	{
		// force cast (but use only the interface)
		if (repository instanceof JackrabbitRepository)
		{
			((JackrabbitRepository) repository).shutdown();
		}
		if (timer != null)
		{
			timer.cancel();
		}
	}

	/**
	 * 
	 */
	public Resource getHomeDir()
	{
		return this.homeDir;
	}

	/**
	 *
	 */
	public void setHomeDir(Resource defaultRepDir)
	{
		this.homeDir = defaultRepDir;
	}

	/**
	 *
	 */
	public RepositoryConfig getRepositoryConfig()
	{
		return this.repositoryConfig;
	}

	/**
	 * 
	 */
	public void setRepositoryConfig(RepositoryConfig repositoryConfig)
	{
		this.repositoryConfig = repositoryConfig;
	}

	public Properties getVariables()
	{
		return variables;
	}

	public void setVariables(Properties variables)
	{
		this.variables = variables;
	}

	public boolean isStandalone()
	{
		return standalone;
	}

	public void setStandalone(boolean standalone)
	{
		this.standalone = standalone;
	}

	public String getStandaloneURL()
	{
		return standaloneURL;
	}

	public void setStandaloneURL(String standaloneURL)
	{
		this.standaloneURL = standaloneURL;
	}

	public boolean isDatastore()
	{
		return datastore;
	}

	public void setDatastore(boolean datastore)
	{
		this.datastore = datastore;
	}
	
	

}
