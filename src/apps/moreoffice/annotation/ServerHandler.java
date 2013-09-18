package apps.moreoffice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统的servlet处理方法类的定义。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface  ServerHandler
{
	/**
	 * 是否需要处理
	 * @return
	 */
	boolean required() default true;
}
