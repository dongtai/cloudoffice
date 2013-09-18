package apps.moreoffice.report.server.servlet.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import apps.moreoffice.report.server.service.manager.dataCenter.database.connection.UserDBManager;
import apps.moreoffice.report.server.servlet.config.InitDataManager;
import apps.moreoffice.report.server.servlet.context.ReportApplicationContext;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 应用启动和停止
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-8
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportServletContextListener implements ServletContextListener
{
    /**
     * 应用启动
     */
    public void contextInitialized(ServletContextEvent arg0)
    {
        try
        {
            ServletContext sc = arg0.getServletContext();

            // 初始化组件
            ReportApplicationContext.init(sc);

            // 初始化用户数据库
            UserDBManager userM = (UserDBManager)ReportApplicationContext.getInstance().getBean(
                "ReportUserDBManager");
            userM.init(sc);

            // 初始化错误信息
            ErrorManager.initDefaultData();

            // 初始化数据库数据
            InitDataManager initDataM = (InitDataManager)ReportApplicationContext.getInstance()
                .getBean("ReportInitDataManager");
            initDataM.initData();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 应用停止
     */
    public void contextDestroyed(ServletContextEvent arg0)
    {
    }
}