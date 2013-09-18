package apps.transmanager.weboffice.servlet.listener;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dwr.ICalendarService;
import apps.transmanager.weboffice.service.server.InitDataService;
import apps.transmanager.weboffice.service.server.MailService;
import apps.transmanager.weboffice.service.sysreport.SysMonitorTask;


public class WebServletContextListener implements ServletContextListener  
{

	private Timer timer;
	
	public void contextInitialized(ServletContextEvent arg0)
	{
		try
		{
			ServletContext sc = arg0.getServletContext();
			ApplicationContext.init(sc);			
			WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
			webConfig.init(sc);			
			
			InitDataService ids = (InitDataService)ApplicationContext.getInstance().getBean(InitDataService.NAME);
			ids.initDatabase();
			
			
			MailService mss = (MailService)ApplicationContext.getInstance().getBean(MailService.NAME);
			mss.initData();
			/*UserService uss = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			uss.initYzAPPandYzModule();*/
			// 初始化系统监控系统
			SysMonitorTask.instance().init();
			
			timer = new Timer(true);
			timer.schedule(new TimerTask()
				{
					public void run() 
					{
						try
						{
							ICalendarService calendarService = (ICalendarService)ApplicationContext.getInstance().getBean("calendarService");
							calendarService.sendCalendarMessage();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}, 60 * 1000, 30 * 1000);			
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}
	}

	public void contextDestroyed(ServletContextEvent arg0)
	{
		if (timer != null)
		{
			timer.cancel();
		}
		//System.out.println("------------------------------------------end");
		//UploadServiceImpl.applicationQuit();
	}

}
