package apps.transmanager.weboffice.constants.server;

import java.util.HashMap;

/**
 * 。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
final class LogsMessageCons extends HashMap<Integer, String>
{
	/**
	 * 禁止实例化类
	 */
	LogsMessageCons()
	{
		super(30);
		initData();
	}
	
	private void initData()
	{
		/**
		 * 
		 * 日志种类
		 * -1———— -1000000之间
		 */
		put(LogConstant.TYPE_ONLINE, "访问日志");
		put(LogConstant.TYPE_FILE, "文件操作日志");
		/**
		 * 日志的标题常量
		 * -2000000—— -3000000
		 */	
		put(LogConstant.COMPANY_TIP, "公司：");
		put(LogConstant.USERS_TIP, "用户：");
		put(LogConstant.START_DATE_TIP, "操作开始时间：");
		put(LogConstant.END_DATE_TIP, "操作结束时间：");
		put(LogConstant.IP_TIP, "IP地址：");
		put(LogConstant.TYPE_TIP, "日志类型：");
		put(LogConstant.OPE_TYPE_TIP, "操作类型：");
		put(LogConstant.CONTENT_TIP, "具体操作内容：");
		
		
		/**
		 * 日志的具体操作操作类型
		 */
		put(LogConstant.OPER_TYPE_LOGIN, "登录");
		put(LogConstant.OPER_TYPE_LOGOUT, "退出");
		put(LogConstant.OPER_TYPE_NEW_FILE, "新建文件");
		put(LogConstant.OPER_TYPE_DEL_FILE, "删除文件");
		put(LogConstant.OPER_TYPE_COPY_FILE, "拷贝文件");
		put(LogConstant.OPER_TYPE_MOVE_FILE, "移动文件");
		put(LogConstant.OPER_TYPE_RENAME_FILE, "重命名文件");
		put(LogConstant.OPER_TYPE_CLEAR_REC, "清空回收站");
		put(LogConstant.OPER_TYPE_NEW_FOLDER, "新建文件夹");
		put(LogConstant.OPER_TYPE_DEL_FOLDER, "删除文件夹");
		put(LogConstant.OPER_TYPE_COPY_FOLDER, "拷贝文件夹");
		put(LogConstant.OPER_TYPE_MOVE_FOLDER, "移动文件夹");
		put(LogConstant.OPER_TYPE_RENAME_FOLDER, "重命名文件夹");
		put(LogConstant.OPER_TYPE_PRINT_FILE, "打印文件 ");
		put(LogConstant.OPER_TYPE_DELETE_FILES, "永久删除 ");
		put(LogConstant.OPER_TYPE_DOWNLOAD_FILES, "下载文件");
		put(LogConstant.OPER_TYPE_UPLOAD_FILES, "上传文件");
		put(LogConstant.OPER_TYPE_RESTORE_FILES, "还原文件");
	}
	
	
}
