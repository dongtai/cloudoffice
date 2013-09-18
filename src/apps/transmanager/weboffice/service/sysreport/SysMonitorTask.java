package apps.transmanager.weboffice.service.sysreport;

import java.util.Date;

import apps.transmanager.weboffice.databaseobject.SysReport;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.JQLServices;

/**
 * 记录系统监控需要存储的数据
 * <p>
 * <p>
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
public class SysMonitorTask
{
    private static SysMonitorTask instance = new SysMonitorTask();
    /**
     * 
     */
    public static SysMonitorTask instance()
    {
        return instance;
    }
    
    /**
     * 系统启动时调用
     */
    public void init()
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        // 启动时间
        sysStartTime = System.currentTimeMillis();
        // 日期
        setDate(new Date());
        // 用户数据
        userCount = jqlService.getEntityCount("Users");
        // 空间数
        spaceCount = jqlService.getEntityCount("Spaces");
        // 文档库大小
        contentSize = SysMonitor.instance().getContentRepository();
        // 文档数量
        JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
        documentCount = jcrService.getDocumnetCount();
    }
    
    /**
     * 得到系统 运行时间
     */
    public String getSysRunTime()
    {
        long t = System.currentTimeMillis() - sysStartTime;
        // 天
        long day = t / 84600 / 1000;
        t -= (day * 84600 * 1000);
        // 时
        long hours = (int)t / 3600 / 1000;
        t -= (hours * 3600 * 1000);
        // 分
        long minutes  = t / 60 / 1000;
        
        return day + "天" + hours + "时" + minutes + "分";
    }
    
    /**
     * 获得当天增加文档数
     */
    public int getTodayAddDocumnetCount()
    {
        return this.todayAddDocumnetCount;
    }
    /**
     * 更新当天增加文档数
     * 
     * @param number 增加的文档数
     */
    public void updateTodayAddDocumentCount(int number)
    {
        todayAddDocumnetCount += number;
    }    
    
    /**
     * 获得当天删除文档数
     */
    public int getTodayDeleteDocumnetCount()
    {
        return this.todayDeleteDocumentCount;
    }
    /**
     * 更新当天删除的文档 
     * 
     * @param number 删除的文档数
     */
    public void updateTodayDeleteDocumentCount(int number)
    {
        this.todayDeleteDocumentCount += number;
    }
    
    /**
     * 得到当天更改的文档数
     */
    public int getTodayChangeDocumnetCount()
    {
        return this.todayChangeDocumnetCount;
    }
    /**
     *  更新当天更新的文档数
     */
    public void updateTodayChangeDocunetCount(int number)
    {
        this.todayChangeDocumnetCount += number;
    }
    
    
    public void setDocumentCount(int documentCount)
    {
        this.documentCount = documentCount;
    }

    public long getDocumentCount()
    {
        return documentCount;
    }
    
    public long getRealDocumentCount()
    {
        return documentCount + todayAddDocumnetCount - todayDeleteDocumentCount;
    }

    public void setContentSize(long contentSize)
    {
        this.contentSize = contentSize;
    }

    public long getContentSize()
    {
        return contentSize;
    }

    public void setUserCount(long userCount)
    {
        this.userCount = userCount;
    }

    public long getUserCount()
    {
        return userCount;
    }

    public void setSpaceCount(long spaceCount)
    {
        this.spaceCount = spaceCount;
    }

    public long getSpaceCount()
    {
        return spaceCount;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }

    public Date getDate()
    {
        return date;
    }

    /**
     * 保存一些监控数据
     */
    public void excuteTask()
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String str = "select count(*) from ";
        SysReport sysReport = new SysReport();
        //userService.sav
        // 日期
        sysReport.setReportdate(date);        
        date = new Date();
        // 用户总数
        long count = jqlService.getEntityCount("Users");
        sysReport.setAccountCount(count);
        // 增加的用户数
        sysReport.setAddAccountCount(count - userCount);
        userCount = count;
        // 文档库容量
        count = SysMonitor.instance().getContentRepository();
        sysReport.setContentSize(count);
        // 增加的文档库容量
        sysReport.setAddContentSize(count - contentSize);
        contentSize = count;
        // 空间总数
        count = jqlService.getEntityCount("Spaces");
        sysReport.setSpaceCount(count);
        // 增加的空间数
        sysReport.setSpaceCount(count - spaceCount);
        spaceCount = count;
        // 文档总数
        count = getRealDocumentCount();
        sysReport.setDocumentCount(count);
        // 增加文档数
        sysReport.setAddDocumentCount(todayAddDocumnetCount);
        documentCount = count;
        // 保存至数据库
        jqlService.save(sysReport);        
        // 重置计数器
        todayAddDocumnetCount = 0;
        todayDeleteDocumentCount = 0;
        todayChangeDocumnetCount = 0;
    }
    
    /**
     * 重置一些监控数据
     */
    public void reset()
    {
        todayAddDocumnetCount = 0;
        todayDeleteDocumentCount = 0;
        todayChangeDocumnetCount = 0;
    }

    // 日期
    private Date date;
    // 系统启动时间
    private long sysStartTime;
    // 当天增加的文档数量 
    private int todayAddDocumnetCount;
    // 当天删除的文档数量 
    private int todayDeleteDocumentCount;
    // 当天修改的文档数据  
    private int todayChangeDocumnetCount;
    // 文档库文档总个数
    private long documentCount;
    // 文档库的大小
    private long contentSize;
    // 用户总数
    private long userCount;
    // 空间总数
    private long spaceCount;    
}

