package apps.transmanager.weboffice.rmi;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface ISession
{
	void setAttribute(String ID, String key, Object value);
	Object getAttribute(String ID, String key);
}
