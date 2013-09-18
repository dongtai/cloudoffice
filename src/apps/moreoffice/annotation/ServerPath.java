package apps.moreoffice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * servlet请求的路径及方法定义
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ServerPath
{
	/**
	 * 请求的路径
	 */
	String path();
	
	/**
	 * 是否需要进行登录验证，默认不需要进行验证
	 * @return
	 */
	boolean required() default false;
	
	/**
	 * 请求的处理方法，值为GET,POST,
	 * @return
	 */
	MethodType method() default MethodType.SERVICE;
	
}
