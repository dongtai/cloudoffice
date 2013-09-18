package apps.transmanager.weboffice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {

	//时间模式
	public static final String DATE_P_LINE = "yyyy-MM-dd HH:mm";
	public static final String DATE_P_COLON = "yyyy:MM:dd HH:mm";
	public static final String DATE_P_ZH = "yyyy年MM月dd日 HH时mm分";
	public static final String DATE_P_DIAG = "yyyy/MM/dd HH:mm";
	
	public static final String SCT = "SCT";
	public static final String GMT = "GMT";
	
	/**
	 * 将时间转换成GTM格式的(yyyy年MM月dd日 hh时mm分)
	 * @param date 时间
	 * @return GMT格式的时间
	 */
	public static String formatGTMZH(Date date)
	{
		if(date==null)
		{
			return null;
		}
		SimpleDateFormat ftm = new SimpleDateFormat(DATE_P_ZH);
		//ftm.setTimeZone(TimeZone.getTimeZone(SCT));
		return ftm.format(date);
	}

	/**
	 * 将字符串转换成日期，根据给出的模式
	 * @param pattern 模式
	 * @param dateS 日期类型的字符串
	 * @return 格式化后的日期
	 * @throws ParseException
	 */
	public static Date ftmStringToDate(String pattern,String dateS) throws ParseException
	{
		if(pattern==null || dateS==null)
		{
			return null;
		}
		SimpleDateFormat ftm = new SimpleDateFormat(pattern);
		return ftm.parse(dateS);
	}
	
	/**
	 * 将日期转换成给出模式的字符串
	 * @param pattern 模式
	 * @param date 日期
	 * @return 日期类型的字符串
	 * @throws Exception
	 */
	public static String ftmDateToString(String pattern,Date date) throws Exception{
		if(pattern==null || date==null)
		{
			return null;
		}
		SimpleDateFormat ftm = new SimpleDateFormat(pattern);
		return ftm.format(date);
	}
	
}
