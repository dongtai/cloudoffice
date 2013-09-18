package apps.moreoffice.ext.dwr;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;

import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.service.handler.MessageSnyHandler;
import apps.transmanager.weboffice.service.listener.IMessagesListener;

/**
 * 采用dwr框架的方式推送信息到客户端.
 * 如果以后需要采用其他框架来推送信息，则可以仅仅替换该类即可。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class DwrMessagesPush implements IMessagesListener
{
	public boolean sendMessages(String fun, String match, Messages message, List target)
	{
		return sendMessages(fun, match, message, target, true);
	}
	public boolean sendMessageNums(String fun, String match,Messages message, Long totalnum, List target, boolean snyc)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("messagenum", totalnum);
		TaskRun tr = new TaskRun(fun, data);
		TaskFileter tf = new TaskFileter(target, match);
		Browser.withAllSessionsFiltered(tf, tr);
		if (snyc)
		{
			MessageSnyHandler.syncToMessages(fun, match, message, target);
		}
		return true;
	}
	public boolean sendMessages(String fun, String match, Messages message, List target, boolean snyc)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type", message.getType());
		data.put("message", message);
		TaskRun tr = new TaskRun(fun, data);
		TaskFileter tf = new TaskFileter(target, match);
		Browser.withAllSessionsFiltered(tf, tr);
		if (snyc)
		{
			MessageSnyHandler.syncToMessages(fun, match, message, target);
		}
		return true;
	}

	public boolean sendMessages(String fun, String match, String content, int type, List target)
	{
		return sendMessages(fun, match, content, type, target, true);
	}
	
	public boolean sendMessages(String fun, String match, String content, int type, List target, boolean snyc)
	{
		System.out.println("在dwrpush中的+++++++++++++++++++++++++++++++3");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type", type);
		data.put("message", content);
		TaskRun tr = new TaskRun(fun, data);
		TaskFileter tf = new TaskFileter(target, match);
		Browser.withAllSessionsFiltered(tf, tr);
		if (snyc)
		{
			MessageSnyHandler.syncToMessages(fun, match, content, type, target);
		}
		return true;
	}
	
	public boolean sendMessages(String fun, String match, Object content, int type, List target)
	{
		return sendMessages(fun, match, content, type, target, true);
	}
	
	public boolean sendMessages(String fun, String match, Object content, int type, List target, boolean snyc)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type", type);
		if (content instanceof Map)
		{
			data.putAll((Map)content);
		}
		else
		{
			data.put("message", content);
		}
		TaskRun tr = new TaskRun(fun, data);
		TaskFileter tf = new TaskFileter(target, match);
		Browser.withAllSessionsFiltered(tf, tr);
		if (snyc)
		{
			MessageSnyHandler.syncToMessages(fun, match, content, type, target);
		}
		
		return true;
	}
	
	public boolean sendMessages(String fun, String match, Map<String, Object> data, List<Long> targetList)
	{
		return sendMessages(fun, match, data, targetList, true);
	}
	public boolean sendMessages(String fun, String match, Map<String, Object> data, List<Long> targetList, boolean snyc) 
	{
		TaskRun tr = new TaskRun(fun, data);
		TaskFileter tf = new TaskFileter(targetList, match);
		try
		{
			Browser.withAllSessionsFiltered(tf, tr);
			if (snyc)
			{
				MessageSnyHandler.syncToMessages(fun, match, data, targetList);
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
		return true;
	}

	/**
	 * 发送信息
	 * @param fun js函数名
	 * @param match 需要匹配的key值
	 * @param target 需要匹配的内容
	 * @param contents 发送的消息内容，及js的参数
	 * @return
	 */
	public boolean sendMessages(String fun, String match, List target, Object... contents)
	{
		return sendMessages(fun, match, target, true, contents);
	}

	/**
	 * 发送信息
	 * @param fun js函数名
	 * @param match 需要匹配的key值
	 * @param target 需要匹配的内容
	 * @param contents 发送的消息内容，及js的参数
	 * @return
	 */
	public boolean sendMessages(String fun, String match, List target, boolean snyc, Object... contents)
	{
		TaskRun tr = new TaskRun(fun, contents);
		TaskFileter tf = new TaskFileter(target, match);
		Browser.withAllSessionsFiltered(tf, tr);
		if (snyc)
		{
			MessageSnyHandler.syncToMessages(fun, match, target, contents);
		}
		return true;
	}

	static class TaskRun implements Runnable
	{
		private ScriptBuffer script = new ScriptBuffer();
		//String func ;
		//Object[] o;
		public TaskRun(String func, Object ... d)
		{
			script.appendCall(func, d);
			//this.func = func;
			//o = d;
		}
		public void run()
		{
			Collection<ScriptSession> sessions = Browser.getTargetSessions();
            for (ScriptSession scriptSession : sessions)
            {
                scriptSession.addScript(script);
            }
            
			//ScriptSessions.addFunctionCall(func, o);
			//ScriptSessions.addScript("document.title = 'My new title, from DWR reverse AJAX!';");
		}
	}
	
	static class TaskFileter implements ScriptSessionFilter
	{
		private List target;
		private String match;
		public TaskFileter(List t, String match)
		{
			this.target = t;
			this.match = match;
		}
		
		public boolean match(ScriptSession session)
		{
			Object key = session.getAttribute(match);
			for (Object tar : target)
			{
				//System.out.println("===========  "+tar);
				if (key != null && key.equals(tar))
				{
					return true;
				}				
			}
			return false;
		}
		
	}

}
