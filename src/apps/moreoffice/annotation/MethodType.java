package apps.moreoffice.annotation;

/**
 * servlet 请求方法定义。
 * 目前系统中暂时只支持service方式
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public enum MethodType
{
	/**
	 * service方法直接处理
	 */
	SERVICE,
	/**
	 * get方法
	 */
	GET,
	/**
	 * post方法
	 */	
	POST,
	/**
	 * delete方法
	 */
	DELETE,
	/**
	 * head方法
	 */
    HEAD,
    /**
     * options方法     * 
     */
    OPTIONS,
    /**
     * put方法    
     */
    PUT,
    /**
     * trace方法
     */
    TRACE,
    /**
     * 自定义方法
     */
    CUSTOM
    
}
