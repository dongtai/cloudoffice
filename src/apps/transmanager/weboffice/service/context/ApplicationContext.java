package apps.transmanager.weboffice.service.context;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;



public class ApplicationContext 
{	
	//----
	// Constants
	//----
	/**
	 * The stateless configuration file
	 */
	private static final String STATELESS_CONTEXT_FILE = 
		"resource/applicationContext.xml";
	
	
	//----
	// Attributes
	//----
	/**
	 * Unique instance of the singleton
	 */
	private static ApplicationContext _instance;
	
	/**
	 * Spring context
	 */
	protected WebApplicationContext _springContext;

	//----
	// Properties
	//----
	/**
	 * @return the unique of the instance
	 */
	public static synchronized final ApplicationContext getInstance()
	{
		if (_instance == null)
		{
			_instance = new ApplicationContext();
		}
		return _instance;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	protected ApplicationContext()
	{
		//initContextFile();
	}
	
	public static void init(ServletContext sc)		
	{
		_instance = new ApplicationContext();
		_instance.initContextFile(sc);
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Get a bean from its name
	 */
	public Object getBean(String beanName)
	{
		return _springContext.getBean(beanName);
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal method
	//
	//-------------------------------------------------------------------------
	/**
	 * Init Spring context
	 */
	private void initContextFile(ServletContext sc)
	{
		_springContext =	WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
//		_springContext = new GenericApplicationContext();
//		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(_springContext);
//		xmlReader.loadBeanDefinitions(new ClassPathResource(STATELESS_CONTEXT_FILE));
//		_springContext.refresh();
	}
}
