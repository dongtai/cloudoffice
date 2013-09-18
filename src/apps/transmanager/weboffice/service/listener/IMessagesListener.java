package apps.transmanager.weboffice.service.listener;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Messages;

/**
 * 消息发送监听器。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface IMessagesListener
{
	/**
	 * 推送总数量和记录
	 * @param fun  js函数
	 * @param match 需要匹配的key值，一般是Constant.LG_USER_ID。
	 * @param message 具体的消息
	 * @param totalnum  消息总数量
	 * @param target  目标用户
	 * @param snyc  是否同步
	 * @return
	 */
	public boolean sendMessageNums(String fun, String match,Messages message, Long totalnum, List target, boolean snyc);
	/**
	 * 发送需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param message 发送的信息内容。
	 * @param target 发送的目的地。
	 * @return 是否发送成功。
	 */
	boolean sendMessages(String fun, String match, Messages message, List target);
	
	/**
	 * 发送不需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param content 消息内容
	 * @param type 消息类型
	 * @param target 发送的目的地
	 * @return
	 */
	boolean sendMessages(String fun, String match, String content, int type, List target);
	
	/**
	 * 发送不需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param content 消息内容
	 * @param type 消息类型
	 * @param target 发送的目的地
	 * @return
	 */
	boolean sendMessages(String fun, String match, Object content, int type, List target);

	/**
	 * 发送信息
	 * @param fun js函数名
	 * @param match 匹配的KEY
	 * @param data 数据
	 * @param userIds 过滤用户
	 * @return
	 */
	boolean sendMessages(String fun, String match, Map<String, Object> data, List<Long> userIds);
	
	/**
	 * 发送信息
	 * @param fun js函数名
	 * @param match 需要匹配的key值
	 * @param target 需要匹配的内容
	 * @param contents 发送的消息内容，及js的参数
	 * @return
	 */
	boolean sendMessages(String fun, String match, List target, Object... contents);
	
	
}
