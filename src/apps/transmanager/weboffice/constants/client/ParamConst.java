package apps.transmanager.weboffice.constants.client;

/**
 * URL请求中与参数有关的常量类。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public final class ParamConst
{
	/**
	 * 静止实例化该类
	 */
	private ParamConst()
	{		
	}
	
	// GWT入口点参数，在URL中该参数指定进入的具体页面
	public final static String ENTRYPOINT = "point";
	// 进入主页面
	public final static String ENTRY_MAIN = "main.html";
	// 进入weboffice页面
	public final static String ENTRY_OFFICE = "WebOffice.html";
	// 进入注册页面
	public final static String ENTRY_REG = "register.html";
	// 进入后台管理页面
	public final static String ENTRY_BACK = "BackManager.html";
	
	
}
