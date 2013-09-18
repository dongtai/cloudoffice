package apps.moreoffice.report.server.servlet.context;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 报表ServletContext
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportApplicationContext
{
    // 当前实例
    private static ReportApplicationContext _instance;
    // 采用Spring的管理
    protected WebApplicationContext _springContext;

    /**
     * 得到当前实例
     */
    public static synchronized final ReportApplicationContext getInstance()
    {
        if (_instance == null)
        {
            _instance = new ReportApplicationContext();
        }
        return _instance;
    }

    /**
     * 初始化组件
     * 
     * @param sc ServletContext
     */
    public static void init(ServletContext sc)
    {
        _instance = new ReportApplicationContext();
        _instance._springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
    }

    /**
     * 得到bean
     * 
     * @param beanName bean名称
     * @return Object 对象
     */
    public Object getBean(String beanName)
    {
        return _springContext.getBean(beanName);
    }
}