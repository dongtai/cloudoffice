package apps.transmanager.weboffice.constants.server;

import java.util.HashMap;
import java.util.Locale;

import apps.moreoffice.LocaleConstant;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
public final class LogConstant
{

	/**
	 * 日志种类
	 * -1———— -1000000之间
	 */
	public static final Integer TYPE_ONLINE = -1;             //  访问类日志，包括登录、退出
	public static final Integer TYPE_FILE = -2;           //  操作文件类日志，包括所有文件操作 
	
	/**
	 * 日志的标题常量
	 * -2000000—— -3000000
	 */	
	public static final Integer COMPANY_TIP = -2000000;            // 公司
	public static final Integer USERS_TIP = -2000001;              // 用户;
	public static final Integer START_DATE_TIP = -2000002;         // 操作开始时间
	public static final Integer END_DATE_TIP = -2000003;           // 操作结束时间
	public static final Integer IP_TIP = -2000004;                 // IP地址
	public static final Integer TYPE_TIP = -2000005;               // 日志类型
	public static final Integer OPE_TYPE_TIP = -2000006;           // 操作类型
	public static final Integer CONTENT_TIP = -2000007;            // 具体操作内容	
	
	
	/**
	 * 日志的具体操作操作类型
	 */
	public static final Integer OPER_TYPE_LOGIN = 0;             // 登录
	public static final Integer OPER_TYPE_LOGOUT = 1;             // 退出
	public static final Integer OPER_TYPE_NEW_FILE = 2;           // 新建文件
	public static final Integer OPER_TYPE_DEL_FILE = 3;           // 删除文件
	public static final Integer OPER_TYPE_COPY_FILE = 4;          // 拷贝文件
	public static final Integer OPER_TYPE_MOVE_FILE = 5;          // 移动文件
	public static final Integer OPER_TYPE_RENAME_FILE = 6;        // 重命名文件
	public static final Integer OPER_TYPE_CLEAR_REC = 7;          // 清空回收站
	public static final Integer OPER_TYPE_NEW_FOLDER = 8;         // 新建文件夹
	public static final Integer OPER_TYPE_DEL_FOLDER = 9;         // 删除文件夹
	public static final Integer OPER_TYPE_COPY_FOLDER = 10;       // 拷贝文件夹
	public static final Integer OPER_TYPE_MOVE_FOLDER = 11;       // 移动文件夹
	public static final Integer OPER_TYPE_RENAME_FOLDER = 12;     // 重命名文件夹
	public static final Integer OPER_TYPE_PRINT_FILE = 13;        // 打印文件 
	public static final Integer OPER_TYPE_DELETE_FILES = 14;      // 永久删除
	public static final Integer OPER_TYPE_DOWNLOAD_FILES = 15;      // 下载文件
	public static final Integer OPER_TYPE_UPLOAD_FILES = 16;      // 上传文件
	public static final Integer OPER_TYPE_RESTORE_FILES = 17;      // 还原文件
	
	
	private final static HashMap<Integer, String> defaultMessage = new LogsMessageCons();
	
	private final static HashMap<Locale, HashMap<Integer, String>> messages = new HashMap<Locale, HashMap<Integer, String>>();
		
	public static String get(Integer code)
	{
		return defaultMessage.get(code);
	}
	
	public static String get(int code, Locale locale)
	{
		HashMap<Integer, String> m = messages.get(locale);
		if (m == null)
		{
			try
			{
				String className = "com.evermore.weboffice.constants.server.LogsMessageCons_" + locale.toString();
				Class c = Class.forName(className);
				Object o = c.newInstance();
				if (o != null)
				{
					m = (HashMap<Integer, String>)o;
					messages.put(locale, m);
				}
			}
			catch(Exception e)
			{				
			}
		}
		if (m != null)
		{
			return m.get(code);
		}
		return defaultMessage.get(code);
	}
		
		
	/**
	 * 日志的操作类型。
	 */
	@Deprecated
	public static final String OPERATE_TYPE_LOGIN = LocaleConstant.instance.getValue("OPERATE_TYPE_LOGIN");
	public static final String OPERATE_TYPE_QUIT = LocaleConstant.instance.getValue("OPERATE_TYPE_QUIT");
	public static final String OPERATE_TYPE_NEW_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_NEW_FILE");
	public static final String OPERATE_TYPE_DEL_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_DEL_FILE");
	public static final String OPERATE_TYPE_COPY_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_COPY_FILE");
	public static final String OPERATE_TYPE_MOVE_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_RENAME_FILE");
	public static final String OPERATE_TYPE_RENAME_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_RENAME_FILE");
	public static final String OPERATE_TYPE_CLEAR_REC = LocaleConstant.instance.getValue("OPERATE_TYPE_CLEAR_REC");
	public static final String OPERATE_TYPE_NEW_FOLDER = LocaleConstant.instance.getValue("OPERATE_TYPE_NEW_FOLDER");
	public static final String OPERATE_TYPE_DEL_FOLDER = LocaleConstant.instance.getValue("OPERATE_TYPE_DEL_FOLDER");
	public static final String OPERATE_TYPE_COPY_FOLDER = LocaleConstant.instance.getValue("OPERATE_TYPE_COPY_FOLDER");
	public static final String OPERATE_TYPE_MOVE_FOLDER = LocaleConstant.instance.getValue("OPERATE_TYPE_MOVE_FOLDER");
	public static final String OPERATE_TYPE_RENAME_FOLDER = LocaleConstant.instance.getValue("OPERATE_TYPE_RENAME_FOLDER");
	public static final String OPERATE_TYPE_PRINT_FILE = LocaleConstant.instance.getValue("OPERATE_TYPE_PRINT_FILE");
	
	// 内容常量	
	@Deprecated
	public static final String USER_NAME=LocaleConstant.instance.getValue("USER_NAME");
	public static final String USER_REALNAME=LocaleConstant.instance.getValue("USER_REALNAME");
	public static final String USER_ID = LocaleConstant.instance.getValue("USER_ID");
	public static final String OPERATE_TIME = LocaleConstant.instance.getValue("OPERATE_TIME");
	public static final String IP_ADDRESS = LocaleConstant.instance.getValue("IP_ADDRESS");
	public static final String OPERATE_TYPE = LocaleConstant.instance.getValue("OPERATE_TYPE");
	public static final String OPERATE_CONTENT = LocaleConstant.instance.getValue("OPERATE_CONTENT");
	public static final String OPERATE_FILENAME = LocaleConstant.instance.getValue("OPERATE_FILENAME");
	public static final String OPERATE_PATH = LocaleConstant.instance.getValue("OPERATE_PATH");

}
