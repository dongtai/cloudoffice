package apps.moreoffice.ext.database.datasource;


import java.util.HashMap;

import javax.sql.DataSource;

import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource 
{
	private String driver;
	private String baseDriverUrl;
	private String user;
	private String password;
	private String alias;
	private boolean testBeforeUse;
	private String houseKeepingTestSql;
	private long maximumActiveTime;
	private int maximumConnectionCount;
	private int minimumConnectionCount;
	private boolean trace;
	private boolean verbose;
	private final static String defaultKey = "yozoMain";
	private final static String defaultName = "yozomain";
	
	
	public DynamicDataSource()
	{
	}
	public void afterPropertiesSet() 
	{	
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		ProxoolDataSource datasource = new ProxoolDataSource(); 
		datasource.setDriver(driver);
		datasource.setDriverUrl(baseDriverUrl + defaultName);
		datasource.setUser(user);
		datasource.setPassword(password);
		datasource.setAlias(alias);
		datasource.setTestBeforeUse(testBeforeUse);
		datasource.setHouseKeepingTestSql(houseKeepingTestSql);
		datasource.setMaximumActiveTime(maximumActiveTime);
		datasource.setMaximumConnectionCount(maximumConnectionCount);
		datasource.setMinimumConnectionCount(minimumConnectionCount);
		datasource.setTrace(trace);
		datasource.setVerbose(verbose);
		map.put(defaultKey, datasource);
		
		ProxoolDataSource datasource2 = new ProxoolDataSource(); 
		datasource2.setDriver(driver);
		datasource2.setDriverUrl(baseDriverUrl + "yozotest");
		datasource2.setUser(user);
		datasource2.setPassword(password);
		datasource2.setAlias(alias + "1");
		datasource2.setTestBeforeUse(testBeforeUse);
		datasource2.setHouseKeepingTestSql(houseKeepingTestSql);
		datasource2.setMaximumActiveTime(maximumActiveTime);
		datasource2.setMaximumConnectionCount(maximumConnectionCount);
		datasource2.setMinimumConnectionCount(minimumConnectionCount);
		datasource2.setTrace(trace);
		datasource2.setVerbose(verbose);
		map.put("aaa", datasource2);
		
		
		super.setTargetDataSources(map);
		super.setDefaultTargetDataSource(datasource);
		super.afterPropertiesSet();
	}
	
	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getBaseDriverUrl()
	{
		return baseDriverUrl;
	}

	public void setBaseDriverUrl(String baseDriverUrl)
	{
		this.baseDriverUrl = baseDriverUrl;
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

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public boolean getTestBeforeUse()
	{
		return testBeforeUse;
	}

	public void setTestBeforeUse(boolean testBeforeUse)
	{
		this.testBeforeUse = testBeforeUse;
	}

	public String getHouseKeepingTestSql()
	{
		return houseKeepingTestSql;
	}

	public void setHouseKeepingTestSql(String houseKeepingTestSql)
	{
		this.houseKeepingTestSql = houseKeepingTestSql;
	}

	public long getMaximumActiveTime()
	{
		return maximumActiveTime;
	}

	public void setMaximumActiveTime(long maximumActiveTime)
	{
		this.maximumActiveTime = maximumActiveTime;
	}

	public int getMaximumConnectionCount()
	{
		return maximumConnectionCount;
	}

	public void setMaximumConnectionCount(int maximumConnectionCount)
	{
		this.maximumConnectionCount = maximumConnectionCount;
	}

	public int getMinimumConnectionCount()
	{
		return minimumConnectionCount;
	}

	public void setMinimumConnectionCount(int minimumConnectionCount)
	{
		this.minimumConnectionCount = minimumConnectionCount;
	}

	public boolean getTrace()
	{
		return trace;
	}

	public void setTrace(boolean trace)
	{
		this.trace = trace;
	}

	public boolean getVerbose()
	{
		return verbose;
	}

	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
boolean flag = false;
	protected Object determineCurrentLookupKey()
	{
		//System.out.println("============= thread is  ===================== " + UploadServiceImpl.hashtable.get(Thread.currentThread()));		
		return "";//UploadServiceImpl.hashtable.get(Thread.currentThread());
	}
	
	protected DataSource determineTargetDataSource() 
	{
		DataSource ret =  super.determineTargetDataSource();
		/*if (!flag && UploadServiceImpl.hashtable.get(Thread.currentThread()) != null)
		{
			flag = true;
			//org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean fileService = (org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean)ApplicationContext.getInstance().getBean("emf");
			TestDatabase fileService = (TestDatabase)ApplicationContext.getInstance().getBean("testDabase");
			boolean a = flag;
			fileService.createTable();
			//fileService.getPersistenceProvider().
			//createContainerEntityManagerFactory(fileService.getPersistenceUnitInfo(), fileService.getJpaPropertyMap());
		}*/
		return ret;
	}
	
}
