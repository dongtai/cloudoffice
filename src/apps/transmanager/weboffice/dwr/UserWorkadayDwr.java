package apps.transmanager.weboffice.dwr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.io.FileTransfer;

import apps.transmanager.weboffice.databaseobject.UserWorkaday;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dwr.IUserWorkadayService;

/**
 * 用于处理工作日志
 * @author user733(熊明威)2010-5-31
 *
 */
public class UserWorkadayDwr {

	private IUserWorkadayService userWorkadayService = (IUserWorkadayService) ApplicationContext
			.getInstance().getBean("userWorkadayService");

	/**
	 * 根据登陆用户和日期获得当前日志
	 * @param req
	 * @return
	 */
	public UserWorkaday getCurrentWorkaday(Date date, HttpServletRequest req) {
		//当前登录用户
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			return userWorkadayService.findWorkadayByUserAndDate(uinfo
					.getId(), date);
		} else {
			return null;
		}
	}

	/**
	 * 保存新日志
	 * @param date 日志的日期
	 * @param contentAm 上午日志
	 * @param contentPm 下午日志
	 * @param req
	 */
	public void saveWorkaday(Date date, String titel, String contentAm,
			String contentPm, HttpServletRequest req) {
		//当前登录用户
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			userWorkadayService.saveWorkaday(uinfo, titel, contentAm,
					contentPm, date);
		}
	}

	/**
	 * 更新工作日志
	 * @param date
	 * @param req
	 */
	public void updateWoraday(Date date, String title, String contentAm,
			String contenPm, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		UserWorkaday userWorkaday = userWorkadayService
				.findWorkadayByUserAndDate(uinfo.getId(), date);
		if (userWorkaday != null) {
			userWorkadayService.updateWorkaday(userWorkaday, title, contentAm,
					contenPm);
		}
	}

	/**
	 * 根据关键字查询相关工作日志
	 * @param keyWord
	 * @param req
	 * @return
	 */
	public List searchWorkaday(String keyWord, HttpServletRequest req) {
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if (uinfo != null) {
			List<UserWorkaday> returnList = userWorkadayService
					.findWorkadaysByKeyWord(uinfo.getId(), keyWord);
			return returnList;
		} else {
			return null;
		}
	}
	/**
	 * 根据关键字和起止日期查询相关日志
	 * @param keyWord 关键字
	 * @param fromDate 开始日期
	 * @param toDate 结束日期
	 * @param req
	 * @return
	 */
	public List srhByCon(String keyWord,Date fromDate,Date toDate,HttpServletRequest req)
	{
		Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if(uinfo!=null)
		{
			return userWorkadayService.findWorkadaysByKeyWordAndDate(uinfo.getId(), keyWord, fromDate, toDate);
		}
		return null;
	}
	
	/**
	 * 取得今年的日志并导出
	 * @throws UnsupportedEncodingException 
	 * @throws ParseException 
	 */
	public FileTransfer exportNode(int selYear,HttpServletRequest req,HttpServletResponse res) throws UnsupportedEncodingException, ParseException
	{
		 int year = Calendar.getInstance().get(Calendar.YEAR);
		 if(selYear!=0)
		 {
			 year = selYear;
		 }
		 Calendar cal =Calendar.getInstance();
		 cal.set(year , 0, 1,0,0,0);
		 Date fromDate = cal.getTime();
		 cal.set(year+1, 0, 1,0,0,0);
		 Date toDate = cal.getTime();
		 String str = "";
		 Users uinfo = (Users) req.getSession().getAttribute("userKey");
		if(uinfo==null)
		{
			    return null;
		}
		 List<UserWorkaday> workList = userWorkadayService.findWorkadaysByKeyWordAndDate(uinfo.getId(),null,fromDate, toDate);
		 if(null!=workList && !workList.isEmpty())
		 {
			 StringBuffer buffer = new StringBuffer();
			 buffer.append(year+"年工作日志\r\n");
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			 for(UserWorkaday workDay : workList)
			 {
				buffer.append("\r\n"+format.format(workDay.getDate())).append("\r\n")
				.append("上午:\r\n")
				.append("     "+workDay.getContentAm()).append("\r\n")
				.append("下午:\r\n")
				.append("     "+workDay.getContentPm()).append("\r\n");
			 }
			 str = buffer.toString();
			 //try {
				
				//ServletOutputStream out = res.getOutputStream();
				//res.setContentLength(str.length());
				//res.setContentType("application/msword;charset=UTF-8");
				//res.setHeader("Content-Disposition","attachment;filename=test.doc");    //用word打开页面
				//out.print(str);
				 
			
			//} catch (Exception e) {
			//	e.printStackTrace();
			//}
		 }
		 ByteArrayOutputStream buf = new ByteArrayOutputStream();
		 String language = System.getProperty("file.encoding");
		 String filename = year+"年工作日志.txt";
		if (req.getHeader("User-Agent").toLowerCase().indexOf("firefox") >0)
		{
			filename = new String(filename.getBytes(language), "ISO8859-1");//firefox浏览器
		}else if (req.getHeader("User-Agent").toUpperCase().indexOf("MSIE") >0){
			filename = URLEncoder.encode(filename, "UTF-8");
		}
		 try {
			buf.write(str.getBytes(language));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		   // res.setContentType("application/msword;charset="+System.getProperty("file.encoding"));
		 FileTransfer f =  new FileTransfer(filename, "application/x-download",buf.toByteArray());
		 return f;
	}

}
