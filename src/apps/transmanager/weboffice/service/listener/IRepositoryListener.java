package apps.transmanager.weboffice.service.listener;

import java.util.Map;

/**
 * 文件库的事件监听器，当文件库中内容改变的时候，发送相应的事件。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface IRepositoryListener
{
	/**
	 * 文件库发生内容改变的事件
	 * @param type 事件类型，参见com.evermore.weboffice.constants.server.RepositoryCons中定义。
	 * @param contents，具体的内容改变值。采用key/value方式。如fileName:name，则表示改变的内容
	 * 是文件名字，名字为name。
	 */
	void changeEvent(int type, Map<String, String>  contents);
	
}
