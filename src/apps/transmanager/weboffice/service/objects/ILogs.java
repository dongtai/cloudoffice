package apps.transmanager.weboffice.service.objects;

import java.io.DataOutput;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface ILogs
{
	/**
	 * 把log内容保存到给定的inputstream流中。
	 * 记录方式为：log内容以utf-8的编码方式保存。
	 * @param ot
	 */
	void writeLogs(DataOutput ot);
}
