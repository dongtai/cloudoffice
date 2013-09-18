package apps.transmanager.weboffice.service.handler;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.annotation.ServerHandler;
import apps.moreoffice.annotation.ServerPath;
import apps.moreoffice.ext.dwr.DwrMessagesPush;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.util.server.WebTools;

/**
 * 处理集群中消息在服务器中的同步问题。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@ServerHandler
public class MessageSnyHandler
{
	public final static String MESSAGE = "message";
	public final static String CONTENT = "content";
	public final static String OBJECT = "object";
	public final static String MAP = "map";
	public final static String PARAMS = "params";
	
	/**
	 * 发送信息到其他服务器
	 * 
	 * 发送需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param message 发送的信息内容。
	 * @param target 发送的目的地。
	 * @return 是否发送成功。
	 */
	public static void syncToMessages(String fun, String match, Messages message, List target)
	{
		if (!WebConfig.cluster)
		{
			return;
		}
		URL url;
		URLConnection uc;
		String params = "/syncMessages?method=" + MESSAGE;
		for (String temp : WebConfig.clusterIPs)
		{
			try
			{
				url = new URL(temp + params);
				uc = url.openConnection();
		        uc.setDoInput(true);
		        uc.setDoOutput(true);
		        uc.setRequestProperty("Content-Type", "application/octet-stream");
		        
		        ObjectOutputStream ot = new ObjectOutputStream(uc.getOutputStream());
		        ot.writeUTF(fun);
		        ot.writeUTF(match);
		        ot.writeObject(message);
		        ot.writeObject(target);
		        ot.flush();
		        ot.close();
		        
		        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		        
		        in.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送信息到其他服务器
	 *  
	 * 发送不需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param content 消息内容
	 * @param type 消息类型
	 * @param target 发送的目的地
	 * @return
	 */
	public static void syncToMessages(String fun, String match, String content, int type, List target)
	{
		if (!WebConfig.cluster)
		{
			return;
		}
		URL url;
		URLConnection uc;
		String params = "/syncMessages?method=" + CONTENT;
		for (String temp : WebConfig.clusterIPs)
		{
			try
			{
				url = new URL(temp + params);
				uc = url.openConnection();
		        uc.setDoInput(true);
		        uc.setDoOutput(true);
		        uc.setRequestProperty("Content-Type", "application/octet-stream");
		        
		        ObjectOutputStream ot = new ObjectOutputStream(uc.getOutputStream());
		        ot.writeUTF(fun);
		        ot.writeUTF(match);
		        ot.writeUTF(content);
		        ot.writeInt(type);
		        ot.writeObject(target);
		        ot.flush();
		        ot.close();
		        
		        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		        
		        in.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  发送信息到其他服务器
	 *  
	 * 发送不需要记录到库中保存的信息。
	 * @param fun 需要调用的js名。
	 * @param match 需要匹配的key值。
	 * @param content 消息内容
	 * @param type 消息类型
	 * @param target 发送的目的地
	 * @return
	 */
	public static void syncToMessages(String fun, String match, Object content, int type, List target)
	{
		if (!WebConfig.cluster)
		{
			return;
		}
		URL url;
		URLConnection uc;
		String params = "/syncMessages?method=" + OBJECT;
		for (String temp : WebConfig.clusterIPs)
		{
			try
			{
				url = new URL(temp + params);
				uc = url.openConnection();
		        uc.setDoInput(true);
		        uc.setDoOutput(true);
		        uc.setRequestProperty("Content-Type", "application/octet-stream");
		        
		        ObjectOutputStream ot = new ObjectOutputStream(uc.getOutputStream());
		        ot.writeUTF(fun);
		        ot.writeUTF(match);
		        ot.writeObject(content);
		        ot.writeInt(type);
		        ot.writeObject(target);
		        ot.flush();
		        ot.close();
		        
		        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		        
		        in.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 *  发送信息到其他服务器
	 *  
	 * 发送信息
	 * @param fun js函数名
	 * @param match 匹配的KEY
	 * @param data 数据
	 * @param userIds 过滤用户
	 * @return
	 */
	public static void syncToMessages(String fun, String match, Map<String, Object> data, List<Long> userIds)
	{	
		if (!WebConfig.cluster)
		{
			return;
		}
		URL url;
		URLConnection uc;
		String params = "/syncMessages?method=" + MAP;
		for (String temp : WebConfig.clusterIPs)
		{
			try
			{
				url = new URL(temp + params);
				uc = url.openConnection();
		        uc.setDoInput(true);
		        uc.setDoOutput(true);
		        uc.setRequestProperty("Content-Type", "application/octet-stream");
		        
		        ObjectOutputStream ot = new ObjectOutputStream(uc.getOutputStream());
		        ot.writeUTF(fun);
		        ot.writeUTF(match);
		        ot.writeObject(data);
		        ot.writeObject(userIds);
		        ot.flush();
		        ot.close();
		        
		        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		        
		        in.close();
		        
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  发送信息到其他服务器
	 *  
	 * 发送信息
	 * @param fun js函数名
	 * @param match 匹配的KEY
	 * @param data 数据
	 * @return
	 */
	public static void syncToMessages(String fun, String match, List target, Object... contents)
	{	
		if (!WebConfig.cluster)
		{
			return;
		}
		URL url;
		URLConnection uc;
		String params = "/syncMessages?method=" + PARAMS;
		for (String temp : WebConfig.clusterIPs)
		{
			try
			{
				url = new URL(temp + params);
				uc = url.openConnection();
		        uc.setDoInput(true);
		        uc.setDoOutput(true);
		        uc.setRequestProperty("Content-Type", "application/octet-stream");
		        
		        ObjectOutputStream ot = new ObjectOutputStream(uc.getOutputStream());
		        ot.writeUTF(fun);
		        ot.writeUTF(match);
		        ot.writeObject(target);
		        ot.writeObject(contents);
		        ot.flush();
		        ot.close();
		        
		        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		        
		        in.close();
		        
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@ServerPath(path = "/syncMessages")
	public static String syncFromMessages(HttpServletRequest req, HttpServletResponse res, String path) throws Exception
	{
		req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");
        String method = WebTools.converStr(req.getParameter("method"));
        DwrMessagesPush dmp = (DwrMessagesPush)ApplicationContext.getInstance().getBean("dwrMessagesPush");
        if (MESSAGE.equals(method))
        {
        	ObjectInputStream in = new ObjectInputStream(req.getInputStream());
	        String fun = in.readUTF();
	        String match = in.readUTF();
	        Messages me = (Messages)in.readObject();
	        List target = (List)in.readObject();
	        in.close();
	        dmp.sendMessages(fun, match, me, target, false);
        	return null;
        }
        if (CONTENT.equals(method))
        {
        	ObjectInputStream in = new ObjectInputStream(req.getInputStream());
	        String fun = in.readUTF();
	        String match = in.readUTF();
	        String content = in.readUTF();
	        int type = in.readInt();	        
	        List target = (List)in.readObject();
	        in.close();
	        dmp.sendMessages(fun, match, content, type, target, false);
        	return null;
        }
        if (OBJECT.equals(method))
        {
        	ObjectInputStream in = new ObjectInputStream(req.getInputStream());
        	String fun = in.readUTF();
	        String match = in.readUTF();
	        Object content = in.readObject();
	        int type = in.readInt();	        
	        List target = (List)in.readObject();
	        in.close();
	        dmp.sendMessages(fun, match, content, type, target, false);
        	return null;
        }
        if (MAP.equals(method))
        {
        	ObjectInputStream in = new ObjectInputStream(req.getInputStream());
	        String fun = in.readUTF();
	        String match = in.readUTF();
	        Map<String, Object> data = (Map<String, Object>)in.readObject();
	        List<Long> target = (List<Long>)in.readObject();
	        in.close();
	        dmp.sendMessages(fun, match, data, target, false);
        	return null;
        }
        if (PARAMS.equals(method))
        {
        	ObjectInputStream in = new ObjectInputStream(req.getInputStream());
	        String fun = in.readUTF();
	        String match = in.readUTF();
	        List<Long> target = (List<Long>)in.readObject();
	        Object[] contents = (Object[])in.readObject();
	        in.close();
	        dmp.sendMessages(fun, match, target, false, contents);
        	return null;
        }
        return "";
	}
	
	
}
