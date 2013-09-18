package apps.transmanager.weboffice.service.server;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.SystemLogs;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.SysMonitorInfoBean;
import apps.transmanager.weboffice.service.dao.SystemLogsDAO;
import apps.transmanager.weboffice.service.sysreport.SysMonitor;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 处理整个系统log相关的所有内容
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
// 从原来分散处统一移动到该类中，后续在修改。

@Component(value = LogServices.NAME)
public class LogServices
{
	public final static String NAME = "logService";
	@Autowired
    private SystemLogsDAO logsDAO;
	

	/**
	 * 
	 * @param companyId  公司id
	 * @param type  日志类型，见LogConstant中常量定义
	 * @param startD 开始时间
	 * @param endD 结束时间
	 * @param ip ip值
	 * @param userIds 用户id
	 * @param start 小于0为从头开始
	 * @param count 小于0为所有符合条件的记录
	 * @param order 排序方式，值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
	 * @param dir 升降序asc或desc
	 * @return
	 */
	public List<SystemLogs> getSearchLogs(Long companyId, Integer type, Date startD, Date endD, String ip, List<Long> userIds, 
			int start, int count, String order, String dir)
	{
		return logsDAO.getSearchLogs(companyId, type, startD, endD, ip, userIds, start, count, order, dir);
	}
	
	/**
	 * 
	 × @param companyId  公司id
	 * @param type  日志类型，见LogConstant中常量定义
	 * @param startD 开始时间
	 * @param endD 结束时间
	 * @param ip ip值
	 * @param userIds 用户id
	 * @return
	 */
	public Long getSearchLogsCount(Long companyId, Integer type, Date startD, Date endD, String ip, List<Long> userIds)
	{
		return logsDAO.getSearchLogsCount(companyId, type, startD, endD, ip, userIds);
	}
	
	/**
	 * 获取用户登录推出记录，在sysetmlogs中startDate为登录时间，endDate为退出时间
	 * @param companyId
	 * @param startD
	 * @param endD
	 * @param ip
	 * @param userIds
	 * @param start
	 * @param count
	 * @param order
	 * @param dir
	 * @return
	 */
	public List getUserLoginLogs(Long companyId, Date startD, Date endD, String ip, List<Long> userIds, 
			int start, int count, String order, String dir)
	{
		return logsDAO.getUserLoginLogs(companyId, startD, endD, ip, userIds, start, count, order, dir);
	}
	
	/**
	 * 获取用户登录推出记录，总数量
	 * @param companyId
	 * @param startD
	 * @param endD
	 * @param ip
	 * @param userIds
	 * @return
	 */
	public Long getUserLoginLogsCount(Long companyId, Date startD, Date endD, String ip, List<Long> userIds)
	{
		return logsDAO.getUserLoginLogsCount(companyId, startD, endD, ip, userIds);
	}
	
	/**
	 * 删除date之前的日志
	 * @param companyId
	 * @param startD
	 * @param endD
	 */
	public void deleteLogs(Long companyId, Date date, Integer type)
	{
		logsDAO.deleteLogs(companyId, date, type);
	}
		
	/**
	 * 导出符合某些条件的日志, 如果日志内容太多，最好不使用该方法，而是使用导出为文件的方法
	 * @param companyId  公司id
	 * @param type  日志类型，见LogConstant中常量定义
	 * @param startD 开始时间
	 * @param endD 结束时间
	 * @param ip ip值
	 * @param userIds 用户id
	 * @param start 小于0为从头开始
	 * @param count 小于0为所有符合条件的记录
	 * @param order 排序方式，值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
	 * @param dir 升降序asc或desc
	 * @return 
	 */
	public String exportSearchLogs(Long companyId, Integer type, Date startD, Date endD, String ip, List<Long> userIds, 
			int start, int count, String order, String dir)
	{
		// 该方法还需要做优化处理，以免内容过多导致内存溢出
		List<SystemLogs> logs = logsDAO.getSearchLogs(companyId, type, startD, endD, ip, userIds, start, count, order, dir);
		StringBuffer sb = new StringBuffer();
		if (logs != null && logs.size() > 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (SystemLogs t : logs)
			{
				try
				{
					sb.append(LogConstant.get(LogConstant.COMPANY_TIP));
					sb.append(t.getCompany().getName());
					sb.append(",");
					sb.append(LogConstant.get(LogConstant.USERS_TIP));					
					sb.append(t.getUser().getUserName());
					sb.append("(");
					sb.append(t.getUser().getRealName());
					sb.append("),");				
					sb.append(LogConstant.get(LogConstant.TYPE_TIP));
					sb.append(LogConstant.get(t.getType()));
					sb.append(",");
					sb.append(LogConstant.get(LogConstant.OPE_TYPE_TIP));
					sb.append(LogConstant.get(t.getOperType()));
					sb.append(",");
					sb.append(LogConstant.get(LogConstant.START_DATE_TIP));
					sb.append(sdf.format(t.getStartDate()));
					sb.append(",");
					sb.append(LogConstant.get(LogConstant.IP_TIP));
					sb.append(t.getIp());
					sb.append(",");
					sb.append(LogConstant.get(LogConstant.CONTENT_TIP));
					sb.append(t.getContent());
					sb.append("\n\r");
				}
				catch(Exception e)
				{
					LogsUtility.error(e);
				}
			}
		}
		
		return sb.toString();
	}	
	
	/**
	 * 获取某时间段的访问量
	 * @param companyId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public long getAccessCount(Long companyId, Date startDate, Date endDate)
	{
		return logsDAO.getSearchLogsCount(companyId, LogConstant.TYPE_ONLINE, startDate, endDate, null, null);
	}
	/**
	 * 获得总访问天数
	 */
	public long getAllDay(Long companyId)
	{
		return logsDAO.getAllDayCount(companyId, LogConstant.TYPE_ONLINE, null, null);
	}
	/**
	 * 获取最近及条日志记录
	 * @param companyId
	 * @param type
	 * @param count
	 * @return
	 */
	public List<SystemLogs> getLastestLog(Long companyId, Integer type, int count)
	{
		return logsDAO.getSearchLogs(companyId, type, null, null, null, null, 0, count, "startDate", "desc");
	}
	
	/**
	 * 
	 * @param user
	 * @param ip
	 * @param token
	 */
	public void setLogin(Users user, String ip, String token)
	{
		SystemLogs log = new SystemLogs(user.getCompany(), user, LogConstant.TYPE_ONLINE,  LogConstant.OPER_TYPE_LOGIN, ip, token);
		logsDAO.save(log);
	}
	
	/**
	 * 
	 * @param user
	 * @param ip
	 * @param token
	 */
	public void setLogout(Users user, String ip, String token)
	{
		//SystemLogs log = new SystemLogs(user.getCompany(), user, LogConstant.TYPE_ONLINE,  LogConstant.OPER_TYPE_LOGOUT, ip, token);
		//logsDAO.save(log);
		logsDAO.setLogout(user.getId(), token);
	}
	
	/**
	 * 
	 * @param user
	 * @param ip
	 * @param token
	 */
	public void setFileLog(Users user, String ip, Integer opetType, String content)
	{
		SystemLogs log = new SystemLogs(user.getCompany(), user, LogConstant.TYPE_FILE,  opetType, ip, content);
		logsDAO.save(log);
	}	
		
	public String getLogCompanyName(Long companyId)
	{
		Company c = (Company)logsDAO.find(Company.class, companyId);
		return c != null ? c.getName() : "";
	}
	
	
	/**
	 * 统计日志的天数（日志是用文件来计数的，最外层记录登录日志，所以只需统计最外层文件数目即可）
	 * 
	 * @param path
	 *            日志路径
	 */
	@Deprecated
	public int getTotalDays(String path)
	{
		File fileRoot = new File(path);
		File[] files = fileRoot.listFiles();
		int total = 0;
		for (File file : files)
		{
			if (file.isFile())
			{
				total += 1;
			}
		}
		return total;
	}

	// 总访问量
	@Deprecated
	public int getAllAccess(String path)
	{
		File a = new File(path);
		String[] file = a.list();
		int acceCount = 0;
		for (int i = 0; i < file.length; i++)
		{
			File temp = new File(path + File.separatorChar + file[i]);
			RandomAccessFile fs = null;
			try
			{
				if (temp.isDirectory())
				{
					continue;
				}
				fs = new RandomAccessFile(temp, "r");
				String data = null;
				while (fs.getFilePointer() != fs.length())
				{
					data = fs.readUTF();
					String operType = data.substring(data.lastIndexOf(":") + 1,	data.length());
					if (!"".equals(data))
					{
						if ("登录日志".equalsIgnoreCase(operType.trim()))
						{
							acceCount++;
						}
					}
				}
			}
			catch (Exception e)
			{
				LogsUtility.error(e);
			}
			finally
			{
				if (fs != null)
				{
					try
					{
						fs.close();
					}
					catch(Exception ee)
					{						
					}
				}
			}
		}
		return acceCount;
	}

	// 今年访问量
	@Deprecated
	public int getYearAccess(String path, String date)
	{
		String year = date.substring(0, date.indexOf("-"));
		int count = 0;
		String[] file = getAllFiles(path);
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].indexOf(".") == -1)
			{
				continue;
			}
			String temp = file[i].substring(0, file[i].indexOf("-"));
			if (year.equals(temp))
			{
				count += getDayAccess(path, file[i].substring(0, file[i].lastIndexOf(".")));
			}
		}
		return count;
	}
	
	// 本月访问量
	@Deprecated
	public int getMonthAccess(String path, String date)
	{
		// String date = WebofficeUtility.getFormateDate2(new Date(), "-");
		String month = date.substring(0, date.lastIndexOf("-"));
		// File a = new File(path);
		String[] file = getAllFiles(path);
		int count = 0;
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].lastIndexOf(".") == -1)
			{
				continue;
			}
			String temp = file[i].substring(0, file[i].lastIndexOf("-"));
			if (month.equalsIgnoreCase(temp))
			{
				count += getDayAccess(path, file[i].substring(0, file[i].lastIndexOf(".")));
			}
		}
		return count;
	}

	// 日访问量
	@Deprecated
	public int getDayAccess(String path, String date)
	{
		// String date = WebofficeUtility.getFormateDate2(new Date(), "-");
		// File a = new File(path);
		String[] file = getAllFiles(path);
		int acceCount = 0;
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].lastIndexOf(".") != -1	&& date.equals(file[i].substring(0, file[i].lastIndexOf("."))))
			{
				File temp = new File(path + File.separatorChar + file[i]);
				RandomAccessFile fs = null;
				try
				{
					fs = new RandomAccessFile(temp, "r");
					String data = null;
					while (fs.getFilePointer() != fs.length())
					{
						data = fs.readUTF();
						String operType = data.substring(data.lastIndexOf(":") + 1, data.length());
						if (!"".equals(data))
						{
							if ("登录日志".equalsIgnoreCase(operType.trim()))
							{
								acceCount++;
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (fs != null)
					{
						try
						{
							fs.close();
						}
						catch(Exception ee)
						{						
						}
					}
				}
			}
		}
		return acceCount;
	}

	// 平均访问量
	@Deprecated
	public int getAverAccess(String path)
	{
		return getAllAccess(path) / getTotalDays(path);
	}

	/**
	 * 得到路径下的总文件数目
	 * 
	 * @param path
	 *            路径
	 * @return 文件数目
	 */
	@Deprecated
	public String[] getAllFiles(String path)
	{
		return new File(path).list();
	}

	// 得到指定路径下的所有文件
	private String[] getAllFiles(String path, Date starttime, Date endtime)
	{
		String[] files = new File(path).list();
		long start = starttime.getTime();
		long end = endtime.getTime();
		ArrayList<String> filess = new ArrayList<String>();

		if (files == null)
		{
			return filess.toArray(new String[0]);
		}

		for (int i = 0; i < files.length; i++)
		{
			if (files[i].indexOf(".") == -1)
			{
				continue;
			}
			String temp = files[i].substring(0, files[i].indexOf('.'));
			long date = this.getFormatDate(temp).getTime();
			if (date >= start && date <= end)
			{
				filess.add(temp.concat(".log"));
			}
		}
		return filess.toArray(new String[0]);
	}

	// 分页后的查询日志
	@Deprecated
	public DataHolder getLimitSearchLog(int start, int limit, String path,
			List<String> search, List<String> userIDs)
	{
		List<String> logList = getSearchLog(path, search, userIDs);
		String logType = search.get(0);
		List<String> limitLogList = new ArrayList<String>();
		int size = logList.size();
		limit = limit > size ? size : limit;
		int count = limit + start;
		if (count > size)
		{
			count = size;
		}
		for (int i = start; i < count; i++)
		{
			limitLogList.add(logList.get(i));
		}
		DataHolder dh = new DataHolder();
		dh.setIntData(size);
		dh.setAdminData(limitLogList);
		if ("访问日志".equalsIgnoreCase(logType.trim()))
		{
			dh.setEditCount(0);
		}
		if ("文件操作日志".equalsIgnoreCase(logType.trim()))
		{
			dh.setEditCount(1);
		}
		return dh;
	}

	/**
	 * 根据条件查询日志
	 */
	@Deprecated
	public List<String> getSearchLog(String path, List<String> search,
			List<String> userIDs)
	{
		List<String> searchLog = new ArrayList<String>();
		// 访问日志，操作日志
		String logType = search.get(0);
		String startTime = search.get(1);
		String endTime = search.get(2);
		String IPAddr = search.get(3);
		String userEmail = null;
		Date startTimeFormat = getFormatDate(startTime);
		Date endTimeFormat = getFormatDate(endTime);
		String[] file = getAllFiles(path, startTimeFormat, endTimeFormat);
		if ("访问日志".equalsIgnoreCase(logType.trim()))
		{
			searchLog = searchAccessLog(path, startTime, endTime, IPAddr,
					userEmail, file, userIDs);
		}
		if ("文件操作日志".equalsIgnoreCase(logType.trim()))
		{
			searchLog = searchOperLog(path, startTimeFormat, endTimeFormat,
					startTime, endTime, IPAddr, userEmail, file, userIDs);
		}
		java.util.Collections.reverse(searchLog);
		return searchLog;
	}

	/**
	 * 根据条件查询操作日志
	 * 
	 * @param path
	 *            日志路径
	 * @param endTimeFormat
	 * @param startTimeFormat
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param iPAddr
	 *            IP地址
	 * @param userEmail
	 *            用户Email
	 * @param file
	 *            文件列表
	 * @param userIDs
	 *            用户ID列表
	 */
	private List<String> searchOperLog(String path, Date startTimeFormat,
			Date endTimeFormat, String startTime, String endTime,
			String iPAddr, String userEmail, String[] file, List<String> userIDs)
	{
		List<String> logList = null;
		List<String> operFileList = new ArrayList<String>();
		String[] operFiles = new String[] {};
		if (userIDs != null)
		{
			for (String userID : userIDs)
			{
				operFiles = getAllFiles(path + userID, startTimeFormat,	endTimeFormat);
				for (String filePath : operFiles)
				{
					operFileList.add(userID + "/" + filePath);
				}
			}
		}
		else
		{
			operFileList = getAllOperFile(path, startTimeFormat, endTimeFormat);
		}
		if (operFileList != null)
		{
			logList = getLogInfo(operFileList, path, null, userIDs, iPAddr,
					userEmail, startTime, endTime);
		}
		return logList;
	}

	/**
	 * 获取所有的操作日志文件
	 * 
	 * @param path
	 *            路径
	 * @param startTimeFormat
	 *            起始时间
	 * @param endTimeFormat
	 *            结束时间
	 * @return 日志路径
	 */
	private List<String> getAllOperFile(String path, Date startTimeFormat,
			Date endTimeFormat)
	{
		List<String> logPathList = new ArrayList<String>();
		File logDir = new File(path);
		File[] allChildFiles = logDir.listFiles();
		for (File childFile : allChildFiles)
		{
			if (childFile.isDirectory())
			{
				String[] logPaths = getAllFiles(childFile.getAbsolutePath(),
						startTimeFormat, endTimeFormat);
				for (String logPath : logPaths)
				{
					logPathList.add(childFile.getName() + "/" + logPath);
				}
			}
		}
		return logPathList;
	}

	/**
	 * 根据条件查询访问日志
	 * 
	 * @param path
	 *            日志路径
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param iPAddr
	 *            IP地址
	 * @param userEmail
	 *            用户Email
	 * @param userIDs
	 * @param file
	 *            文件路径集合
	 */
	private List<String> searchAccessLog(String path, String startTime,
			String endTime, String iPAddr, String userEmail, String[] files,
			List<String> userIDs)
	{
		// 访问日志需要过滤掉文件夹
		List<String> logList = null;
		List<String> accessFileList = new ArrayList<String>();
		for (String file : files)
		{
			if (file.indexOf(".") != -1)
			{
				accessFileList.add(file);
			}
		}
		if (accessFileList != null)
		{
			logList = getLogInfo(accessFileList, path, null, userIDs, iPAddr,
					userEmail, startTime, endTime);
		}
		return logList;
	}

	/**
	 * 根据查询条件从文件中获取相符合的日志记录
	 * 
	 * @param fileList
	 *            代查询的文件列表
	 * @param path
	 *            日志路径
	 * @param logType
	 *            查询类型
	 * @param userIDs
	 *            用户ID列表
	 * @param IPAddr
	 *            IP地址
	 * @param userEmail
	 *            用户Email
	 * @param startTime
	 *            起始时间
	 * @param endTime
	 *            结束时间
	 * @return 日志信息
	 */
	private List<String> getLogInfo(List<String> fileList, String path,
			String logType, List<String> userIDs, String IPAddr,
			String userEmail, String startTime, String endTime)
	{
		List<String> searchLog = new ArrayList<String>();
		for (int i = 0; i < fileList.size(); i++)
		{
			File temp = new File(path + File.separatorChar + fileList.get(i));
			RandomAccessFile fs = null;
			try
			{
				fs = new RandomAccessFile(temp, "r");
				String data = null;
				while (fs.getFilePointer() != fs.length())
				{
					data = fs.readUTF();
					if (!"".equals(data))
					{
						if ((userIDs == null || userIDs.size() == 0) && (IPAddr == null || IPAddr.equals("")))
						{
							searchLog.add(data);
							continue;
						}
						String[] record = data.split(";");
						String userID = record[0].substring(record[0].indexOf(":") + 1, record[0].length());
						String IPAddress = record[2].substring(record[2].indexOf(":") + 1, record[2].length());
						if (userIDs != null && userIDs.size() > 0)
						{
							if (!userIDs.contains(userID))
							{
								continue;
							}
						}
						if (IPAddr != null && !IPAddr.equals(""))
						{
							if (!IPAddr.equals(IPAddress))
							{
								continue;
							}
						}
						searchLog.add(data);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (fs != null)
				{
					try
					{
						fs.close();
					}
					catch(Exception ee)
					{						
					}
				}
			}
		}
		return searchLog;
	}

	// 导出指定条件的日志
	@Deprecated
	public List<String> exportLog(String path, List<String> search,
			List<String> userIDs)
	{
		return getSearchLog(path, search, userIDs);

		/*
		 * try { String filePath = targetPath + "/exportLog.log"; filePath =
		 * filePath.toString(); File myFilePath = new File(filePath); if
		 * (!myFilePath.exists()) { myFilePath.createNewFile(); }
		 * OutputStreamWriter resultFile = new OutputStreamWriter(new
		 * FileOutputStream(filePath)); PrintWriter myFile = new
		 * PrintWriter(resultFile); Iterator<String> it = searchLog.iterator();
		 * while(it.hasNext()) { myFile.println(it.next()); } myFile.close();
		 * resultFile.close(); } catch (Exception e) { e.printStackTrace(); }
		 */
	}
	
	// 删除指定条件日志
	@Deprecated
	public void deleteLog(String path, List<String> search, List<String> userIDs)
	{
		List<String> searchLog = new ArrayList<String>();
		// 访问日志，操作日志
		String logType = search.get(0);
		String startTime = search.get(1);
		String endTime = search.get(2);
		Date startTimeFormat = getFormatDate(startTime);
		Date endTimeFormat = getFormatDate(endTime);
		String[] file = getAllFiles(path, startTimeFormat, endTimeFormat);
		if ("文件操作日志".equals(logType))
		{
			List<String> operFileList = new ArrayList<String>();
			String[] operFiles = new String[] {};
			if (userIDs != null)
			{
				for (String userID : userIDs)
				{
					operFiles = getAllFiles(path + userID, startTimeFormat,	endTimeFormat);
					for (String filePath : operFiles)
					{
						operFileList.add(filePath);
					}
				}
			}
			else
			{
				operFileList = getAllOperFile(path, startTimeFormat, endTimeFormat);
			}
			file = operFileList.toArray(new String[0]);
		}
		File temp = null;
		for (int i = 0; i < file.length; i++)
		{
			temp = new File(path + File.separatorChar + file[i]);
			if (temp.exists())
			{
				temp.delete();
			}
		}
	}

	// 最近10条日志
	@Deprecated
	public List<String> getLatestLog(String path)
	{
		int logCount = 10;
		// int tempCount = 0;
		List<String> latestLog = new ArrayList<String>();
		// List<String> tempLog = new ArrayList<String>();
		String date = WebofficeUtility.getFormateDate2(new Date(), "-");
		/*
		 * getLogTemp(path, date, logCount); String[] file = getAllFiles(path);
		 * for (int i = 0; i < file.length; i++) { if
		 * (date.equals(file[i].substring(0, file[i].lastIndexOf(".")))) { File
		 * temp = new File(path + file[i]); try { FileInputStream fs = new
		 * FileInputStream(temp); InputStreamReader isr = new
		 * InputStreamReader(fs); BufferedReader br = new BufferedReader(isr);
		 * String data = null; while ((data = br.readLine()) != null) { if
		 * (!"".equals(data)) { tempLog.add(data); tempCount++; } } if(tempCount
		 * >= logCount) { for (int j = tempCount - 1; j >= tempCount - logCount;
		 * j--) { latestLog.add(tempLog.get(j)); } } else { latestLog = tempLog;
		 * } br.close(); isr.close(); fs.close(); } catch(FileNotFoundException
		 * e1) { e1.printStackTrace(); } catch(Exception e) {
		 * e.printStackTrace(); } } }
		 */
		return getLogTemp(path, latestLog, date, logCount);
	}

	@Deprecated
	public List<String> getLogTemp(String path, List<String> latestLog,
			String date, int count)
	{
		int countTemp = 0;
		// List<String> latestLogtemp = new ArrayList<String>();
		List<String> LogTemp = new ArrayList<String>();
		String[] file = getAllFiles(path);
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].indexOf(".") != -1	&& date.equals(file[i].substring(0, file[i].lastIndexOf("."))))
			{
				File temp = new File(path + File.separatorChar + file[i]);
				RandomAccessFile fs = null;
				try
				{
					fs = new RandomAccessFile(temp, "r");
					String data = null;
					while (fs.getFilePointer() != fs.length())
					{
						data = fs.readUTF();
						if (!"".equals(data))
						{
							LogTemp.add(data);
							countTemp++;
						}
					}
					if (countTemp >= count)
					{
						for (int j = countTemp - 1; j >= countTemp - count; j--)
						{
							latestLog.add(LogTemp.get(j));
						}
					}
					else
					{
						for (int k = countTemp - 1; k >= 0; k--)
						{
							latestLog.add(LogTemp.get(k));
						}
						// getLogTemp(path,latestLog,getPreviousDay(path,date),
						// count-countTemp);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (fs != null)
					{
						try
						{
							fs.close();
						}
						catch(Exception ee)
						{						
						}
					}
				}
			}
		}
		return latestLog;
	}

	@Deprecated
	public String getPreviousDay(String path, String date)
	{
		if (date != null)
		{
			String[] record = date.split("-");
			int year = Integer.parseInt(record[0]);
			int month = Integer.parseInt(record[1]);
			int day = Integer.parseInt(record[2]) - 1;
			if (month == 0)
			{
				year -= year;
				month = 12;
			}
			if (day == 0)
			{
				month -= month;
				if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
				{
					day = 31;
				}
				else if (month == 2)
				{
					day = year / 400 == 0 ? 29 : 28;
				}
				else
				{
					day = 30;
				}
			}
			date = year + "-" + (month >= 10 ? month : ("0" + month)) + "-"	+ (day >= 10 ? day : ("0" + day));

			if (new File(path + File.separatorChar + date + ".log").exists())
			{
				return date;
			}
			else
			{
				return getPreviousDay(path, date);
			}
		}
		return null;
	}

	@Deprecated
	public Date getFormatDate(String date)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		String[] record = date.split(" ");
		String[] record1 = record[0].split("-");
		if (record.length > 1)
		{
			String[] record2 = record[1].split(":");
			cal.set(Integer.parseInt(record1[0]),
					Integer.parseInt(record1[1]) - 1, Integer.parseInt(record1[2]),
					Integer.parseInt(record2[0]), Integer.parseInt(record2[1]));
		}
		else
		{
			cal.set(Integer.parseInt(record1[0]),
					Integer.parseInt(record1[1]) - 1, Integer.parseInt(record1[2]));
		}
		return cal.getTime();
	}
	
	@Deprecated
	public Date getFormatDate1(String date)
	{
		String[] record = date.split(" ");
		String[] record1 = record[0].split("-");
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Integer.parseInt(record1[0]), Integer.parseInt(record1[1]) - 1,
				Integer.parseInt(record1[2]));
		return cal.getTime();
	}
	
 	/**
 	 * 获得系统监控信息
 	 */
 	public static SysMonitorInfoBean getSysMonitorInfo(HttpServletRequest request)
 	{
       SysMonitorInfoBean infoBean = SysMonitor.instance().getMonitorInfoBean();
       
       String str = request.getRequestURL().toString();
       int index = str.indexOf("//");
       if (index > 0)
       {
           str = str.substring(index + 2);
       }
       index = str.indexOf("/");
       if (index > 0)
       {
           str = str.substring(0, index);
       }
       index = str.indexOf(":");
       if (index > 0)
       {
           str = str.substring(0, index);
       }
       // 域名
       infoBean.setServerDomain(str);
       // web Server
       infoBean.setWebServer(request.getServerName());
       return infoBean;
 	}

	public void deleteLogs(Long companyId, Integer type, Date startD,Date endD, String ip, List<Long> userIds) {
		logsDAO.deleteLogs(companyId,  type,startD,endD,ip,userIds);
		
	}

	public List<HashMap<String, String>> getDepLogs(String companyId,Date startD, Date endD, int start, int count, String sort, String dir) {
		// TODO Auto-generated method stub
		return logsDAO.getDepLogs(companyId, startD, endD, start, count ,sort,dir);
	}

	public int getDepLogsCount(String companyId, Date startD, Date endD) {
		// TODO Auto-generated method stub
		return logsDAO.getDepLogsCount(companyId, startD, endD);
	}
}
