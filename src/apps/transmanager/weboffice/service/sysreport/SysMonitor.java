package apps.transmanager.weboffice.service.sysreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import apps.transmanager.weboffice.client.constant.MainConstant;
import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.databaseobject.License;
import apps.transmanager.weboffice.databaseobject.SysReport;
import apps.transmanager.weboffice.domain.SysMonitorInfoBean;
import apps.transmanager.weboffice.domain.SysReportBean;
import apps.transmanager.weboffice.service.cache.IMemCache;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;

import com.jconfig.DiskObject;
import com.jconfig.FileRegistry;
import com.sun.management.OperatingSystemMXBean;

/**
 ** 获取系统信息的业务逻辑实现类. 
 * <p>
 * <p>
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
public class SysMonitor
{
    // 实例
    private static SysMonitor instance = new SysMonitor();
    
    private static final int CPUTIME = 30;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;
    //KB字节参考量
    public static final long SIZE_KB = 1024L;
    //MB字节参考量
    public static final long SIZE_MB = SIZE_KB * 1024L;
    //GB字节参考量
    public static final long SIZE_GB = SIZE_MB * 1024L;
    //TB字节参考量
    public static final long SIZE_TB = SIZE_GB * 1024L;
    // 
    private DecimalFormat df = new DecimalFormat("#.0");
    //
    private boolean isWin;
    private String installPath;
    
    public SysMonitor()
    {
        isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    /**
     * 获得实例
     */
    public static SysMonitor instance()
    {
        return instance;
    }
    
    /** 
     * 获得当前的监控对象. 
     * @return 返回构造好的监控对象 
     * @throws Exception 
     * @author GuoHuang 
     */
    public SysMonitorInfoBean getMonitorInfoBean()
    {
        // 构造返回对象 
        SysMonitorInfoBean infoBean = new SysMonitorInfoBean();
        // === 基本信息 ===
        getBaseInfo(infoBean);
        // 性能
        getPerformance(infoBean);
        // 网络流量,现在是瞎写，以后要修改
        getNetFlow(infoBean);
        // 安装的中间件
        getComInfo(infoBean);
        // 用户信息
        getUserInfo(infoBean);
        // 文档信息
        getDocumentInfo(infoBean);
        
        return infoBean;
    }
    
    /***
     * 为了导出系统报表
     */
    public List<SysReportBean> getSysReport(String userID, Date startDate, Date endDate, int cycleType,int start, int count)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        Calendar calendar = Calendar.getInstance();

        // 开始时间定位在(时分秒)0:0:0
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
        // 结束时间定位在(时分秒)23:59:59
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();
        // 周
        if (cycleType == 1)
        {
            return getSysReportBeanForWeek(userID, jqlService, startDate, endDate,start,count);
        }
        // 月
        else if (cycleType == 2)
        {
            return getSysReportBeanForMonth(userID, jqlService, startDate, endDate,start,count);
        }
        // 天
        else if (cycleType == 0)
        {        
            String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            @ SuppressWarnings("rawtypes")
            List list = jqlService.findAllBySql(start,count,sql, startDate, endDate);
            if (list != null && !list.isEmpty())
            {
                List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
                SysReportBean srb;
                SysReport sysReport;
                for (int i = 0; i < list.size(); i++)
                {
                    srb = new SysReportBean();
                    sysReport = (SysReport)list.get(i);
                    sysReport.databaseToBean(srb);
                    sysReportBean.add(srb);
                    String str = WebofficeUtility.getFormateDate(sysReport.getReportdate(), "/");
                    str = str.substring(0, Math.min(10, str.length()));
                    srb.setReportdate(str);
                }
                return sysReportBean;

            }
        }
        return null;
    }
   
    /**
     * 用来获得报表的长度
     * @param startDate
     * @param endDate
     * @return
     */
	public int getSysReportCount(Date startDate, Date endDate,int cycleType) {
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		Calendar calendar = Calendar.getInstance();
        // 开始时间定位在(时分秒)0:0:0
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
        // 结束时间定位在(时分秒)23:59:59
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();
        
        int totalSize = 0;
        if (cycleType == 1){  //周
        	String uid="1";  //给出的一个uid，用来符合调用函数的参数
        	totalSize = getSysReportBeanForWeek(uid, jqlService, startDate, endDate).size();
        }
        else if (cycleType == 2)   //月
        {
        	String uid="1";  //
        	totalSize = getSysReportBeanForMonth(uid, jqlService, startDate, endDate).size();
        }
        else if (cycleType == 0)   //天
        { 
    		String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            List list = jqlService.findAllBySql(sql, startDate, endDate);
            totalSize = list.size();
        }
        
        return totalSize;
	}
    
	/***
	 * chen 添加，用来分页。
	 * @param userID
	 * @param startDate
	 * @param endDate
	 * @param cycleType
	 * @param startPage
	 * @param count
	 * @return
	 */
    public List<SysReportBean> getSysReportBean(String userID, Date startDate, Date endDate, int cycleType,int startPage,int count)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        Calendar calendar = Calendar.getInstance();

        // 开始时间定位在(时分秒)0:0:0
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
        // 结束时间定位在(时分秒)23:59:59
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();
        // 周
        if (cycleType == 1){
            return getSysReportBeanForWeek(userID, jqlService, startDate, endDate ,startPage, count);
        }
        // 月
        else if (cycleType == 2){
            return getSysReportBeanForMonth(userID, jqlService, startDate, endDate,startPage, count);
        }
        // 天
        else if (cycleType == 0){        
            String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            @ SuppressWarnings("rawtypes")
            List list = jqlService.findAllBySql(startPage, count, sql, startDate, endDate);
            if (list != null && !list.isEmpty())
            {
                List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
                SysReportBean srb;
                SysReport sysReport;
                for (int i = 0; i < list.size(); i++)
                {
                    srb = new SysReportBean();
                    sysReport = (SysReport)list.get(i);
                    sysReport.databaseToBean(srb);
                    sysReportBean.add(srb);
                    String str = WebofficeUtility.getFormateDate(sysReport.getReportdate(), "/");
                    str = str.substring(0, Math.min(10, str.length()));
                    srb.setReportdate(str);
                }
                return sysReportBean;

            }
        }
        return null;
    }
    
    
    
    /**
     * 得到系统报告数据
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param cycleType 周期           =0   一天
     *                          =1   一周
     *                          =2   一个自然月
     */
    public List<SysReportBean> getSysReportBean(String userID, Date startDate, Date endDate, int cycleType)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        Calendar calendar = Calendar.getInstance();
        /*int count = (int)jqlService.getEntityCount("SysReport");
        if (count == 0)
        {  
            SysReport report;
            for (int i = 0; i < 40; i++)
            {
                report = new SysReport();
                report.setReportdate(calendar.getTime());
                //
                report.setAddContentSize(10);
                report.setContentSize(600);
                //
                report.setAccountCount(50);
                report.setAddAccountCount(2);
                //
                report.setSpaceCount(50);
                report.setAddSpaceCount(2);
                //
                report.setDocumentCount(50);
                report.setAddDocumentCount(2);            
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                //
                jqlService.save(report);
            }
        }*/
        // 开始时间定位在(时分秒)0:0:0
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
        // 结束时间定位在(时分秒)23:59:59
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();
        // 周
        if (cycleType == 1)
        {
            return getSysReportBeanForWeek(userID, jqlService, startDate, endDate );
        }
        // 月
        else if (cycleType == 2)
        {
            return getSysReportBeanForMonth(userID, jqlService, startDate, endDate);
        }
        // 天
        else if (cycleType == 0)
        {        
            String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            @ SuppressWarnings("rawtypes")
            List list = jqlService.findAllBySql(sql, startDate, endDate);
            if (list != null && !list.isEmpty())
            {
                List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
                SysReportBean srb;
                SysReport sysReport;
                for (int i = 0; i < list.size(); i++)
                {
                    srb = new SysReportBean();
                    sysReport = (SysReport)list.get(i);
                    sysReport.databaseToBean(srb);
                    sysReportBean.add(srb);
                    String str = WebofficeUtility.getFormateDate(sysReport.getReportdate(), "/");
                    str = str.substring(0, Math.min(10, str.length()));
                    srb.setReportdate(str);
                }
                return sysReportBean;

            }
        }
        return null;
    }
    
    
    /**
     * 
     */
    public File exportSysReport(String userID, Long startDate, Long endDate, 
        int cycleType, String tempFolder, String fileType)
    {
        // 报表数据
        Date sDate = new Date(startDate);
        Date eDate = new Date(endDate);
        List<SysReportBean> sysReportBean = getSysReportBean(userID, sDate, eDate, cycleType);
        if (sysReportBean == null || sysReportBean.isEmpty())
        {
            return null;
        }
        // 写文件
        fileType = fileType == null ?  "xls" : fileType;
        String fileName = "sysReport." + fileType;
        File file = new File(tempFolder + File.separatorChar + fileName);
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream  fis = new FileOutputStream (file);
            if (fileType.equalsIgnoreCase("xls"))
            {
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet();
                int rowIndex = 1;
                short cellIndex = 1;
                HSSFRow row = sheet.createRow(rowIndex++);
                HSSFCell cell = row.createCell(cellIndex++);
                // 日期
                cell.setCellValue(new HSSFRichTextString(MainConstant.SYS_REPORT_DATE));  //chen
                // 用户数
                cell = row.createCell(cellIndex++);
                cell.setCellValue(new HSSFRichTextString(MainConstant.SYS_REPORT_ACCOUNT_COUNT));//chen
                // 空间数
                cell = row.createCell(cellIndex++);
                cell.setCellValue(new HSSFRichTextString(MainConstant.SYS_REPORT_SPACE_COUNT));//chen
                // 文档数
                cell = row.createCell(cellIndex++);
                cell.setCellValue(new HSSFRichTextString(MainConstant.SYS_REPORT_DOCUMENT_COUNT));  //chen
                // 文档库容量
                cell = row.createCell(cellIndex++);
                cell.setCellValue(new HSSFRichTextString(MainConstant.SYS_REPORT_CONTENT_SIZE)); //chen
                
                int size = sysReportBean.size();
                for (int i = 0; i < size; i++)
                {
                    SysReportBean srb = sysReportBean.get(i);
                    cellIndex = 1;
                    row = sheet.createRow(rowIndex++);
                    // 日期
                    cell = row.createCell(cellIndex++);
                    cell.setCellValue(new HSSFRichTextString(srb.getReportdate()));
                    // 用户数
                    cell = row.createCell(cellIndex++);
                    cell.setCellValue(srb.getAccountCount());
                    // 空间数
                    cell = row.createCell(cellIndex++);
                    cell.setCellValue(srb.getSpaceCount());
                    // 文档数
                    cell = row.createCell(cellIndex++);
                    cell.setCellValue(srb.getDocumentCount());
                    // 文档库容量
                    cell = row.createCell(cellIndex++);
                    cell.setCellValue(new HSSFRichTextString(formatSpace(srb.getContentSize())));
                }
                workbook.write(fis);
                fis.close();
            }
            // 文本文件
            else if (fileType.equalsIgnoreCase("txt"))
            {                
            }            
            return file;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    private List<SysReportBean> getSysReportBeanForWeek(String userID, JQLServices jqlService, 
            Date startDate, Date endDate,int startPage, int count)
        {
            Calendar calendar = Calendar.getInstance();
            Date start = startDate;
            calendar.setTime(startDate);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            weekDay = weekDay == 0 ? 7 : weekDay;
            calendar.add(Calendar.DAY_OF_MONTH, 7 - weekDay);
            
            Date end = calendar.getTime();
            if (end.getTime() > endDate.getTime())
            {
                end = endDate;
            }
            List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
            // 时间比较        
            while (start.getTime() < endDate.getTime())
            {
                // 开始时间定位在(时分秒)0:0:0
                calendar.setTime(start);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
                // 结束时间定位在(时分秒)23:59:59
                calendar.setTime(end);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                end = calendar.getTime();calendar.get(Calendar.SECOND);
                
                int addSpaceCount = 0;
                int addAccountCount = 0;
                int addDocumentCount = 0;
                int addContentSize = 0;
                SysReport sysReport = null;    
                String sql = "select s from SysReport s where s.reportdate between ? And ? ";
                @ SuppressWarnings("rawtypes")
                List list = jqlService.findAllBySql(sql, startDate, end); 
                if (list != null && !list.isEmpty()){
                	for (int i = 0; i < list.size(); i++){
                		sysReport = (SysReport)list.get(i);
                		addSpaceCount += sysReport.getAddSpaceCount();
                		addAccountCount += sysReport.getAddAccountCount();
                		addDocumentCount += sysReport.getAddDocumentCount();
                		addContentSize += sysReport.getAddContentSize();
                	}
                }
                //返回,起始位startPage 返回count条
                
//                List list = jqlService.findAllBySql(startPage, count, sql, startDate, end);            
//                if (list != null && !list.isEmpty())
//                {               
//                    for (int i = 0; i < list.size(); i++)
//                    {
//                        sysReport = (SysReport)list.get(i);
//                        addSpaceCount += sysReport.getAddSpaceCount();
//                        addAccountCount += sysReport.getAddAccountCount();
//                        addDocumentCount += sysReport.getAddDocumentCount();
//                        addContentSize += sysReport.getAddContentSize();
//                    }
//                }
                if (sysReport != null)
                {
                    SysReportBean srb = new SysReportBean();
                    // 帐号
                    srb.setAccountCount(sysReport.getAccountCount());
                    srb.setAddAccountCount(addAccountCount);
                    // 空间
                    srb.setSpaceCount(sysReport.getSpaceCount());
                    srb.setAddSpaceCount(addSpaceCount);
                    // 文档
                    srb.setDocumentCount(sysReport.getDocumentCount());
                    srb.setAddDocumentCount(addDocumentCount);
                    // 文档库
                    srb.setContentSize(sysReport.getContentSize());
                    srb.setAddContentSize(addContentSize);
                    
                    String strS = WebofficeUtility.getFormateDate(start, "/");
                    strS = strS.substring(0, Math.min(10, strS.length()));
                    String strE =  WebofficeUtility.getFormateDate(end, "/");
                    strE = strE.substring(0, Math.min(10, strE.length()));
                    srb.setReportdate(strS + "--" + strE);
                    
                    sysReportBean.add(0, srb);
                }            
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, 6);
                end = calendar.getTime();
                if (end.getTime() > endDate.getTime())
                {
                    end = endDate;
                }
            }
			//为了分页
			ArrayList<SysReportBean> listPage = new ArrayList<SysReportBean>(); 
			int j=startPage;
			//判断 ：j < usersOnlineList.size()，是为了防止填充不满页面的情况
			for(int i=0; i < count && j < sysReportBean.size(); i++){
				listPage.add(sysReportBean.get(j++));
			}
             return listPage;
           // return sysReportBean;
        }
    
    
    
    /**
     * 重写了该方法getSysReportBeanForWeek()，为了分页，该方法是否可以删除？
     * @param userID
     * @param startDate
     * @param endDate
     * @param cycleType
     * @return
     */
    private List<SysReportBean> getSysReportBeanForWeek(String userID, JQLServices jqlService, 
        Date startDate, Date endDate)
    {
        Calendar calendar = Calendar.getInstance();
        Date start = startDate;
        calendar.setTime(startDate);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        weekDay = weekDay == 0 ? 7 : weekDay;
        calendar.add(Calendar.DAY_OF_MONTH, 7 - weekDay);
        
        Date end = calendar.getTime();
        if (end.getTime() > endDate.getTime())
        {
            end = endDate;
        }
        List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
        // 时间比较        
        while (start.getTime() < endDate.getTime())
        {
            // 开始时间定位在(时分秒)0:0:0
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            start = calendar.getTime();
            // 结束时间定位在(时分秒)23:59:59
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            end = calendar.getTime();calendar.get(Calendar.SECOND);
            
            int addSpaceCount = 0;
            int addAccountCount = 0;
            int addDocumentCount = 0;
            int addContentSize = 0;
            SysReport sysReport = null;    
            String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            @ SuppressWarnings("rawtypes")
            List list = jqlService.findAllBySql(sql, startDate, end);            
            if (list != null && !list.isEmpty())
            {               
                for (int i = 0; i < list.size(); i++)
                {
                    sysReport = (SysReport)list.get(i);
                    addSpaceCount += sysReport.getAddSpaceCount();
                    addAccountCount += sysReport.getAddAccountCount();
                    addDocumentCount += sysReport.getAddDocumentCount();
                    addContentSize += sysReport.getAddContentSize();
                }
            }
            if (sysReport != null)
            {
                SysReportBean srb = new SysReportBean();
                // 帐号
                srb.setAccountCount(sysReport.getAccountCount());
                srb.setAddAccountCount(addAccountCount);
                // 空间
                srb.setSpaceCount(sysReport.getSpaceCount());
                srb.setAddSpaceCount(addSpaceCount);
                // 文档
                srb.setDocumentCount(sysReport.getDocumentCount());
                srb.setAddDocumentCount(addDocumentCount);
                // 文档库
                srb.setContentSize(sysReport.getContentSize());
                srb.setAddContentSize(addContentSize);
                
                String strS = WebofficeUtility.getFormateDate(start, "/");
                strS = strS.substring(0, Math.min(10, strS.length()));
                String strE =  WebofficeUtility.getFormateDate(end, "/");
                strE = strE.substring(0, Math.min(10, strE.length()));
                srb.setReportdate(strS + "--" + strE);
                
                sysReportBean.add(0, srb);
            }            
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            end = calendar.getTime();
            if (end.getTime() > endDate.getTime())
            {
                end = endDate;
            }
        }
        return sysReportBean;
    }
    
    
    private List<SysReportBean> getSysReportBeanForMonth(String userID, JQLServices jqlService, 
            Date startDate, Date endDate, int startPage, int count)
        {
            Calendar calendar = Calendar.getInstance();
            Date start = startDate;
            calendar.setTime(startDate);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, maxDay);
            Date end = calendar.getTime();
            if (end.getTime() > endDate.getTime())
            {
                end = endDate;
            }
            List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
            // 时间比较        
            while (start.getTime() < endDate.getTime())
            {
                // 开始时间定位在(时分秒)0:0:0
                calendar.setTime(start);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
                // 结束时间定位在(时分秒)23:59:59
                calendar.setTime(end);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                end = calendar.getTime();calendar.get(Calendar.SECOND);
                
                int addSpaceCount = 0;
                int addAccountCount = 0;
                int addDocumentCount = 0;
                int addContentSize = 0;
                SysReport sysReport = null;            
                String sql = "select s from SysReport s where s.reportdate between ? And ? ";
                @ SuppressWarnings("rawtypes")
                List list = jqlService.findAllBySql(sql, startDate, end); 
                if (list != null && !list.isEmpty())
                {               
                    for (int i = 0; i < list.size(); i++)
                    {
                        sysReport = (SysReport)list.get(i);
                        addSpaceCount += sysReport.getAddSpaceCount();
                        addAccountCount += sysReport.getAddAccountCount();
                        addDocumentCount += sysReport.getAddDocumentCount();
                        addContentSize += sysReport.getAddContentSize();
                    }
                }
                //返回,起始位startPage 返回count条
                
//                List list = jqlService.findAllBySql(startPage, count, sql, startDate, end); 
//                if (list != null && !list.isEmpty())
//                {               
//                    for (int i = 0; i < list.size(); i++)
//                    {
//                        sysReport = (SysReport)list.get(i);
//                        addSpaceCount += sysReport.getAddSpaceCount();
//                        addAccountCount += sysReport.getAddAccountCount();
//                        addDocumentCount += sysReport.getAddDocumentCount();
//                        addContentSize += sysReport.getAddContentSize();
//                    }
//                }
                if (sysReport != null)
                {
                    SysReportBean srb = new SysReportBean();
                    // 帐号
                    srb.setAccountCount(sysReport.getAccountCount());
                    srb.setAddAccountCount(addAccountCount);
                    // 空间
                    srb.setSpaceCount(sysReport.getSpaceCount());
                    srb.setAddSpaceCount(addSpaceCount);
                    // 文档
                    srb.setDocumentCount(sysReport.getDocumentCount());
                    srb.setAddDocumentCount(addDocumentCount);
                    // 文档库
                    srb.setContentSize(sysReport.getContentSize());
                    srb.setAddContentSize(addContentSize);
                    
                    String strS = WebofficeUtility.getFormateDate(start, "/");
                    strS = strS.substring(0, Math.min(10, strS.length()));
                    String strE =  WebofficeUtility.getFormateDate(end, "/");
                    strE = strE.substring(0, Math.min(10, strE.length()));
                    srb.setReportdate(strS + "--" + strE);
                    
                    sysReportBean.add(0, srb);
                }            
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTime();
                maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH, maxDay);
                end = calendar.getTime();
                if (end.getTime() > endDate.getTime())
                {
                    end = endDate;
                }
            }
            
			//为了分页
			ArrayList<SysReportBean> listPage = new ArrayList<SysReportBean>(); 
			int j=startPage;
			//判断 ：j < usersOnlineList.size()，是为了防止填充不满页面的情况
			for(int i=0; i < count && j < sysReportBean.size(); i++){
				listPage.add(sysReportBean.get(j++));
			}
             return listPage;
          //  return sysReportBean;
        }
    
    /**
     * 为了分页chen重写了该方法，该方法是否可以删除？
     * @param userID
     * @param startDate
     * @param endDate
     * @param cycleType
     * @return
     */
    private List<SysReportBean> getSysReportBeanForMonth(String userID, JQLServices jqlService, 
        Date startDate, Date endDate)
    {
        Calendar calendar = Calendar.getInstance();
        Date start = startDate;
        calendar.setTime(startDate);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, maxDay);
        Date end = calendar.getTime();
        if (end.getTime() > endDate.getTime())
        {
            end = endDate;
        }
        List<SysReportBean> sysReportBean = new ArrayList<SysReportBean>();
        // 时间比较        
        while (start.getTime() < endDate.getTime())
        {
            // 开始时间定位在(时分秒)0:0:0
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            start = calendar.getTime();
            // 结束时间定位在(时分秒)23:59:59
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            end = calendar.getTime();calendar.get(Calendar.SECOND);
            
            int addSpaceCount = 0;
            int addAccountCount = 0;
            int addDocumentCount = 0;
            int addContentSize = 0;
            SysReport sysReport = null;            
            String sql = "select s from SysReport s where s.reportdate between ? And ? ";
            @ SuppressWarnings("rawtypes")
            List list = jqlService.findAllBySql(sql, startDate, end); 
            if (list != null && !list.isEmpty())
            {               
                for (int i = 0; i < list.size(); i++)
                {
                    sysReport = (SysReport)list.get(i);
                    addSpaceCount += sysReport.getAddSpaceCount();
                    addAccountCount += sysReport.getAddAccountCount();
                    addDocumentCount += sysReport.getAddDocumentCount();
                    addContentSize += sysReport.getAddContentSize();
                }
            }
            if (sysReport != null)
            {
                SysReportBean srb = new SysReportBean();
                // 帐号
                srb.setAccountCount(sysReport.getAccountCount());
                srb.setAddAccountCount(addAccountCount);
                // 空间
                srb.setSpaceCount(sysReport.getSpaceCount());
                srb.setAddSpaceCount(addSpaceCount);
                // 文档
                srb.setDocumentCount(sysReport.getDocumentCount());
                srb.setAddDocumentCount(addDocumentCount);
                // 文档库
                srb.setContentSize(sysReport.getContentSize());
                srb.setAddContentSize(addContentSize);
                
                String strS = WebofficeUtility.getFormateDate(start, "/");
                strS = strS.substring(0, Math.min(10, strS.length()));
                String strE =  WebofficeUtility.getFormateDate(end, "/");
                strE = strE.substring(0, Math.min(10, strE.length()));
                srb.setReportdate(strS + "--" + strE);
                
                sysReportBean.add(0, srb);
            }            
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start = calendar.getTime();
            maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, maxDay);
            end = calendar.getTime();
            if (end.getTime() > endDate.getTime())
            {
                end = endDate;
            }
        }
        return sysReportBean;
    }
    
    /**
     * 基本信息
     */
    private void getBaseInfo(SysMonitorInfoBean infoBean)
    {
        try
        {
            InetAddress address = InetAddress.getLocalHost();
            // 主机名称
            infoBean.setServerName(address.getHostName());
            // 主机IP
            infoBean.setServerIP(isWin ? address.getHostAddress() : getIPForLinux());
            // 域名
            //infoBean.setServerDomain(servlet.getContextPath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 取Linux系统的IP
     * @return
     */
    private String getIPForLinux()
    {
        String ip = "127.0.0.1";
        try
        {
            Enumeration<?> e1 = (Enumeration<?>)NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface)e1.nextElement();
                if (!ni.getName().equals("eth0"))
                {
                    continue;
                }
                else
                {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements())
                    {
                        InetAddress ia = (InetAddress)e2.nextElement();
                        if (ia instanceof Inet6Address)
                        {
                            continue;
                        }
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        }
        catch(SocketException e)
        {
            e.printStackTrace();
        }
        return ip;
    }
    
    /**
     * 性能
     */
    private void getPerformance(SysMonitorInfoBean infoBean)
    {
        //
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        // 操作系统 
        String osName = System.getProperty("os.name");
        infoBean.setOsName(osName);
        // 总的物理内存 
        String totalMemorySize = formatSpace(osmxb.getTotalPhysicalMemorySize());
        infoBean.setTotalMemorySize(totalMemorySize);
        // 剩余的物理内存 
        String freePhysicalMemorySize = formatSpace(osmxb.getFreePhysicalMemorySize());
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        // 已使用的物理内存 
        String usedMemory = formatSpace((osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()));
        infoBean.setUsedMemory(usedMemory);
        infoBean.setMemoryRatio((float)(osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / osmxb.getTotalPhysicalMemorySize());
        // cpu使用率
        float cpuRatio = isWin ? getCpuRatioForWindows() : getCpuRatioForLinux(); 
        infoBean.setCpuRatio(cpuRatio / 100f);
        // cpu频率
        float cpuHZ = isWin ? getCpuHZForWindows() : getCpuHZForLinux();
        cpuHZ /= 1000;
        infoBean.setCpuHZ(df.format(cpuHZ) + "GHz");
        String p = df.format(cpuRatio / 100 * cpuHZ);
        if (p.indexOf(".") == 0)
        {
            p = "0" + p;
        }
        infoBean.setUsedCpuHZ(p + "GHz");
        // 存储设备容量
        String[] dz = getDiskSpace();
        infoBean.setDiskSize(dz[0]);
        infoBean.setDiskUsedSize(dz[1]);
        if (dz[2]==null || dz[2].length()==0)
        {
        	infoBean.setDiskRatio(Float.parseFloat("1"));//孙爱护临时这样加的，原来报异常
        }
        else
        {
        	infoBean.setDiskRatio(Float.parseFloat(dz[2]));  
        }
    }
    
    /**
     * 
     */
    private void getNetFlow(SysMonitorInfoBean infoBean)
    {
        // 上传速度
        infoBean.setNetUpSpeed("200KB");
        // 下载速度
        infoBean.setNetDownSpeed("200KB");
        // 上传流量
        infoBean.setNetUpFlow("200M");
        // 下载流量
        infoBean.setNetDownFlow("200M");
    }
    
    /**
     * 安装的中间件
     */
    private void getComInfo(SysMonitorInfoBean infoBean)
    {
        // DB Server
        WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
        infoBean.setDbServer(webConfig.getDatabaseType());
        // 文件系统
        infoBean.setFileRepository("Jackrabbit2.2.8");
        // 系统运行时间
        infoBean.setSysRunTime(SysMonitorTask.instance().getSysRunTime());
    }
    
    /**
     * 用户信息 
     */
    @ SuppressWarnings("rawtypes")
    private void getUserInfo(SysMonitorInfoBean infoBean)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        // 公司名称，暂时没有地方得到
        List list = jqlService.findAll(License.class);
        if (list != null && list.size() > 0)
        {
            infoBean.setCompanyName(((License)list.get(0)).getCompany());
        }
        // 部门总数
        infoBean.setDepartmentCount(jqlService.getEntityCount("Organizations") + "个");
        // 帐号个数        
        infoBean.setAccountCount(jqlService.getEntityCount("Users") + "个");
        IMemCache memCache = (IMemCache)ApplicationContext.getInstance().getBean("memCacheBean");
        memCache.getLoginUserCount();
        // 在线帐号
        infoBean.setOnlineAccount(memCache.getLoginUserCount() + "个");
        // 在线帐号列表
        /*StringBuilder onlineUser = new StringBuilder();
        Map<String, LoginUserInfo> map = memCache.getAllLoginUser();
        if (map != null)
        {
            Iterator<LoginUserInfo> allUser = map.values().iterator();
            boolean hasNext = allUser.hasNext();
            while (hasNext)
            {
                onlineUser.append(allUser.next().getName());
                hasNext = allUser.hasNext();
                if (hasNext)
                {
                    onlineUser.append("|");
                }
            }
        }
        infoBean.setOnlineAccountList(onlineUser.toString());*/
    }
    
    /**
     * 文档信息 
     */
    private void getDocumentInfo(SysMonitorInfoBean infoBean)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        // 空间总数
        infoBean.setSpaceCount(jqlService.getEntityCount("Spaces") + "个");
        // 部门空间
        infoBean.setDepartmentSpaceCount(infoBean.getDepartmentCount());
        // 项目空间
        infoBean.setProjectSpaceCount(jqlService.getEntityCount("Groups") + "个");
        // 文档总数
        infoBean.setDocumentCount(SysMonitorTask.instance().getRealDocumentCount() + "篇");
        // 文档库大小
        infoBean.setContentSize(formatSpace(getContentRepository()));        
        // 增加的文档数
        infoBean.setTodayAddDocumnetCount(SysMonitorTask.instance().getTodayAddDocumnetCount() + "篇");
        // 删除的文档数
        infoBean.setTodayDeleteDocumentCount(SysMonitorTask.instance().getTodayDeleteDocumnetCount() + "篇");
        // 修改的文档数
        infoBean.setTodayChangeDocumnetCount(SysMonitorTask.instance().getTodayChangeDocumnetCount() + "篇");
    }
    
    /**
     * 
     * @return
     */
    private float getCpuHZForWindows()
    {
        float s = 0;
        try 
        { 
            String procCmd = System.getenv("windir") + "\\system32\\wbem\\wmic.exe cpu get /value"; 
            // 取进程信息 
            Process process = Runtime.getRuntime().exec(procCmd);
            process.getOutputStream().close(); 
            InputStreamReader ir = new InputStreamReader(process.getInputStream()); 
            LineNumberReader input = new LineNumberReader(ir); 
            String line = input.readLine(); 
            while (line != null)
            {
                if (line.startsWith("MaxClockSpeed"))
                {
                    int index = line.indexOf("=");
                    if (index > 0)
                    {
                        line = line.substring(index + 1);
                        s = Float.parseFloat(line);
                        break;
                    }
                }
                line = input.readLine();
            }
            
            ir.close();
            input.close();
            process.getInputStream().close();
        } 
        catch (Exception ex) 
        {           
            ex.printStackTrace(); 
            return s; 
        }        
        return s;
    }
    
    /**
     * 
     * @return
     */
    private float getCpuHZForLinux()
    {
        float s = 0;
        try 
        { 
            String procCmd = "cat /proc/cpuinfo"; 
            // 取进程信息 
            Process process = Runtime.getRuntime().exec(procCmd);
            process.getOutputStream().close(); 
            InputStreamReader ir = new InputStreamReader(process.getInputStream()); 
            LineNumberReader input = new LineNumberReader(ir); 
            String line = input.readLine(); 
            while (line != null)
            {
                if (line.startsWith("cpu MHz"))
                {
                    int index = line.indexOf(":");
                    if (index > 0)
                    {
                        line = line.substring(index + 1);
                        s = Float.parseFloat(line);
                        break;
                    }
                }
                line = input.readLine();
            }            
            ir.close();
            input.close();
            process.getInputStream().close();
        } 
        catch (Exception ex) 
        {           
            ex.printStackTrace(); 
            return s; 
        }        
        return s;
    }
    
    /**
     * 
     * @return
     */
    public float getCpuRatioForLinux()
    {
        float cpuUsed = 0;
        try
        {
            Runtime rt = Runtime.getRuntime();  
            Process p = rt.exec("top -b -n 1");
            BufferedReader in = null;  
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String str = null; 
            String[] strArray = null;
            while ((str = in.readLine()) != null)
            {
                if (str.indexOf("PID") >= 0)
                {
                    break;
                }
            }
            while ((str = in.readLine()) != null) 
            {                
                int m = 0;
                strArray = str.split(" "); 
                if (str.indexOf("top") >= 0)
                {
                    continue;
                }
                for (String tmp : strArray)
                {
                    if (tmp.trim().length() == 0)  
                    {
                        continue;
                    }
                    if (++m == 9) 
                    {
                        cpuUsed += Double.parseDouble(tmp);
                    }
                }
            }
            cpuUsed = cpuUsed < 5 ? 5 : cpuUsed;
            return cpuUsed;
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
            return 1;
        }
    }


    /** 
     * 获得CPU使用率. 
     * @return 返回cpu使用率 
     * @author GuoHuang 
     */
    private float getCpuRatioForWindows()
    {
        try 
        { 
            String procCmd = System.getenv("windir") 
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine," 
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount"; 
            // 取进程信息 
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd)); 
            Thread.sleep(CPUTIME); 
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd)); 
            if (c0 != null && c1 != null)
            { 
                long idletime = c1[0] - c0[0]; 
                long busytime = c1[1] - c0[1]; 
                if (busytime + idletime == 0)
                {
                    return 5f;
                }
                return (float)Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue(); 
            }
            else
            { 
                return 5f; 
            } 
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace(); 
            return 5f; 
        } 
    }
    
    /**
     * 读取CPU信息. 
     * @param proc 
     * @return 
     * @author GuoHuang 
     */
    private long[] readCpu(final Process proc)
    { 
        long[] retn = new long[2]; 
        try 
        { 
            proc.getOutputStream().close(); 
            InputStreamReader ir = new InputStreamReader(proc.getInputStream()); 
            LineNumberReader input = new LineNumberReader(ir); 
            String line = input.readLine(); 
            if (line == null || line.length() < FAULTLENGTH) 
            { 
                return null; 
            } 
            int capidx = line.indexOf("Caption"); 
            int cmdidx = line.indexOf("CommandLine"); 
            int rocidx = line.indexOf("ReadOperationCount"); 
            int umtidx = line.indexOf("UserModeTime"); 
            int kmtidx = line.indexOf("KernelModeTime"); 
            int wocidx = line.indexOf("WriteOperationCount"); 
            long idletime = 0; 
            long kneltime = 0; 
            long usertime = 0; 
            while ((line = input.readLine()) != null) 
            { 
            	try{
	                if (line.length() < wocidx) 
	                {
	                    continue; 
	                } 
	                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount, 
	                // ThreadCount,UserModeTime,WriteOperation 
	                String caption = line.substring(capidx, cmdidx - 1).trim(); 
	                String cmd = line.substring(cmdidx, kmtidx - 1).trim(); 
	                if (cmd.indexOf("wmic.exe") >= 0)
	                {
	                    continue; 
	                }  
	                if (caption.equals("System Idle Process") || caption.equals("System"))
	                { 
	                    idletime += Long.valueOf(line.substring(kmtidx, rocidx - 1).trim()).longValue(); 
	                    idletime += Long.valueOf(line.substring(umtidx, wocidx - 1).trim()).longValue(); 
	                    continue; 
	                }
	                kneltime += Long.valueOf(line.substring(kmtidx, rocidx - 1).trim()).longValue(); 
	                //kneltime +=Long.valueOf(0);  //chen
	                //usertime += Long.valueOf(0);  //chen
	                usertime += Long.valueOf(line.substring(umtidx, wocidx - 1).trim()).longValue(); 
            }
	            catch (Exception e) 
	            {
	                continue;
	            }
            } 
            retn[0] = idletime; 
            retn[1] = kneltime + usertime;
            // 
            ir.close();
            input.close();
            return retn; 
        } 
        catch (Exception ex)
        { 
            ex.printStackTrace(); 
        } 
        finally 
        { 
            try 
            { 
                proc.getInputStream().close(); 
            } 
            catch (Exception e)
            { 
                e.printStackTrace(); 
            } 
        } 
        return null; 
    }
    
    /**
     * 
     * @param dirName
     * @return
     */
    private String[] getDiskSpace()
    {
        String str[] = new String[]{"0GB", "0GB", ""};
        try
        {
            File file = new File(getInstallPath() + "samizdat//jconfig//");
            //设定错误信息输出
            //Trace.setDestination(Trace.TRACE_SYSOUT);
            //初始化文件系统
            FileRegistry.initialize(file,0);
            //创建磁盘对象，可以是磁盘驱动器、目录或文件
            DiskObject diskObj = FileRegistry.createDiskObject(new File(getInstallPath()), 0);
            //获得磁盘容量
            if (diskObj!=null && diskObj.getFile()!=null)
            {
	            long total = diskObj.getFile().getTotalSpace();
	            str[0] = formatSpace(diskObj.getFile().getTotalSpace());    
	            //获得磁盘可用空间
	            str[1] = formatSpace(diskObj.getFile().getTotalSpace() - diskObj.getFile().getFreeSpace());
	            // 
	            str[2] = (float)(total - diskObj.getFile().getFreeSpace()) / total + "";
            }
            return str;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            return str;
        }
    }
    
    /**
     * 得到文档库大小
     * @return
     */
    public long getContentRepository()
    {
        String path = getInstallPath();
        path = path.substring(0, path.indexOf("WEB-INF"));
        path += "filedisks//repositories//public";  //此处修改时参照officedemo_nanjingga。
        return  fileSize(new File(path));
    }
    
    private long fileSize(File file)
    {
        long size = 0;
        if (file.isDirectory())
        {
            size += file.length();
            File[] sf = file.listFiles();
            for (File tempFile : sf )
            {
                size += fileSize(tempFile);
            }
        }
        else
        {
            size += file.length();
        }
        return size;
    }
    
    /**
     * 格式化容量
     */
    public String formatSpace(long size)
    {
        String s = "";
        float dz = 0;
        // TB
        if (size >= SIZE_TB)
        {
            dz = (float)size / SIZE_TB;
            s = df.format(dz) + "TB";
        }
        // GB
        else if (size >= SIZE_GB)
        {
            dz = (float)size / SIZE_GB;
            s = df.format(dz) + "GB";
        }
        // MB
        else if (size >= SIZE_MB)
        {
            dz = (float)size / SIZE_MB;
            s = df.format(dz) + "MB";
        }
        //
        else if (size >= SIZE_KB)
        {
            dz = (float)size / SIZE_KB;
            s = df.format(dz) + "KB";
        }
        else
        {
            s = size + "B";
        }
        return s;
    }
    
    /**
     * 获得安装路径
     * @return
     */
    public String getInstallPath()
    {
        if (installPath != null)
        {
            return installPath;
        }
        String path = "";
        try
        {
            String className = getClass().getName();
            int pos = className.lastIndexOf('.');
            className = className.substring(pos + 1) + ".class";
            path = getClass().getResource(className).toString();
            path = path.substring(0, path.length() - className.length() - 1);
            path = path.replace("+", "<*^*>");
            path = URLDecoder.decode(path, "UTF-8");
            path = path.replace("<*^*>", "+");
            pos = path.indexOf("file:/");
            // 操作系统 
            boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (pos >= 0)
            {
                pos = pos + (isWin ? 6 : 5);
            }
            else if (isWin && path.startsWith("/"))
            {
                pos = 1;
            }
            path = path.substring(pos);
            pos = path.lastIndexOf("jconfig.");
            if (pos > 0)
            {
                path = path.substring(0, pos);

                if (path.indexOf("le:") >= 0)
                {
                    path = path.substring(4);
                }
            }
            else
            {
                path = path.substring(0, path.lastIndexOf("apps/transmanager/weboffice/service/sysreport"));
            }

            if ((pos = path.lastIndexOf(".jar!/")) > 0)
            {
                path = path.substring(0, pos - 1);
                pos = path.lastIndexOf("/");
                if (pos > 0)
                {
                    path = path.substring(0, pos + 1);
                }
            }

            if (File.separator.equals("\\") && path.indexOf("/") >= 0)
            {
                return installPath = path.replace('/', File.separatorChar);
            }
            else if (File.separator.equals("/") && path.indexOf("\\") >= 0)
            {
                return installPath = path.replace('\\', File.separatorChar);
            }
        }
        catch(Exception e)
        {
            return "";
        }
        return installPath = path;
    }
    
    /**测试方法. 
     * @param args 
     * @throws Exception 
     * @author GuoHuang 
       */
    public static void main(String[] args)
    {
        //SysMonitor service = new SysMonitor();
        //SysMonitorTask.instance().init();
        //SysMonitorInfoBean monitorInfo = service.getMonitorInfoBean();
        //System.out.println(monitorInfo);
        ArrayList<String> t = new ArrayList<String>();
        for (int i = 0; i < 6; i++)
        {
            t.add(String.valueOf(i));
        }   
        // 前移
        String a = t.remove(3);
        t.add(4, a);
        for (int i = 0; i < 6; i++)
        {
            System.out.println(i + " = " + t.get(i));
        }
    }
}
