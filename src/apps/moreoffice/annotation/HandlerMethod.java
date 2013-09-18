package apps.moreoffice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统的servlet处理方法的方法名定义。
 * 使用该方法，则请求中，请求参数需要以如下方式传输。
 * 1.参数及数据传输格式：json格式、原始二进制数据。数据内容的字符编码方式为UTF-8。
 * 2.Request json: 请求说明：
 *		a)请求的参数名为：jsonParams，值为json 格式。
 *		b)请求json 格式：
 *		c)json 格式描述如下：
 *		{
 *			method:"method1",
 *			params : {key:"value",key2:"value2"},
 *			token: "xxxxxxxx"
 *		}
 * 具体参见AbstractServlet类中定义说明。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface  HandlerMethod
{
	/**
	 * 是否需要进行登录验证，默认需要验证
	 * @return
	 */
	boolean required() default true;
	/**
	 * servlet处理的方法名，默认为""，则系统自动取方法名为servlet处理的方法名
	 * @return
	 */
	String methodName() default "";
}
