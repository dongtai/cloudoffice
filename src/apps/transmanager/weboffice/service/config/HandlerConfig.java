package apps.transmanager.weboffice.service.config;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import apps.moreoffice.annotation.HandlerMethod;
import apps.moreoffice.annotation.ServerHandler;
import apps.moreoffice.annotation.ServerPath;

/**
 * 系统servlet的处理方法注册集合
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class HandlerConfig
{
	private String[] packHandlers;
	private String[] classHandlers;
	private static HashMap<String, Method> json;              // 一个json请求参数方式，不需要验证的方法
	private static HashMap<String, Method> validateJson;      // 一个json请求参数方式，需要验证的方法
	private static HashMap<String, Method> doService;         // 一个REST请求参数方式，以service方法执行，不需要验证的方法
	private static HashMap<String, Method> validateService;   // 一个REST请求参数方式，以service方法执行，需要验证的方法
	
	public HandlerConfig()
	{
		json = new HashMap<String, Method>();
		validateJson = new HashMap<String, Method>();
		doService = new HashMap<String, Method>();
		validateService = new HashMap<String, Method>();
	}

	/**
	 * 设置需要扫描的包名列表。在扫描的时候会同时递归扫描所有的子包
	 * @param handlers
	 */
	public void setPackHandlers(String[] handlers)
	{
		this.packHandlers = handlers;
	}
	
	/**
	 * 设置需要扫描的类的列表。
	 * @param handlers
	 */
	public void setClassHandlers(String[] handlers)
	{
		this.classHandlers = handlers;
	}
	
	/**
	 * 由spring初始后调用
	 */
	protected void init()
	{
		if (classHandlers != null)
		{
			for (String temp : classHandlers)
			{
				addHandler(temp);
			}
		}			
		if (packHandlers != null)
		{
			Class cl = this.getClass();
			File file;			
			String packName;
			for (String temp : packHandlers)
			{
				packName = temp;
				temp = "/" + temp.replaceAll("\\.", "/");
				try
				{
					file = new File(cl.getResource(temp).toURI());
					if (file.isDirectory())
					{
						addHandler(packName, file);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();					
				}
			}
		}
	}
	
	private void addHandler(String packName, File file)
	{
		String name = file.getName();
		if (file.isFile())
		{
			if (name.endsWith(".class"))
			{
				int index = name.lastIndexOf(".");
				name = name.substring(0, index);
				addHandler(packName + "." + name);
			}
		}
		else if (file.isDirectory())
		{
			File[] list = file.listFiles();
			for (File temp : list)
			{
				addHandler(packName, temp);
			}
		}
	}
	
	private void addHandler(String clName)
	{	
		try
		{
			//System.out.println("======"+clName);
			Class cl = Class.forName(clName);
			ServerHandler a = (ServerHandler)cl.getAnnotation(ServerHandler.class);
			if (a != null && a.required())
			{
				Method[] methods = cl.getMethods();				
				for (Method temp : methods)
				{
					addJsonMethod(temp);
					addRestMethod(temp);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void addRestMethod(Method m)
	{
		Object ret;
		ServerPath t = m.getAnnotation(ServerPath.class);
		if (t != null)
		{
			String path = t.path();
			if (t.required())
			{
				ret = validateService.put(path, m);
			}
			else
			{
				ret = doService.put(path, m);
			}
			if (ret != null)    // 有重复的请求方法
			{
				System.out.println("==有重复的请求方法====new method is ==" + m + "\n=======old metho is ====" + ret);
				Thread.dumpStack();
			}
		}
	}
	
	private static void addJsonMethod(Method m)
	{
		Object ret;
		HandlerMethod t = m.getAnnotation(HandlerMethod.class);
		if (t != null)
		{
			String methodName = t.methodName();
			if (methodName.length() <= 0)
			{
				methodName = m.getName();
			}
			if (t.required())
			{
				ret = validateJson.put(methodName, m);
			}
			else
			{
				ret = json.put(methodName, m);
			}
			if (ret != null)    // 有重复的请求方法
			{
				System.out.println("==有重复的请求方法====new method is ==" + m + "\n=======old metho is ====" + ret);
				Thread.dumpStack();
			}
		}
		
	}
	
	/**
	 * 获取以一个json参数请求的servlet处理请求的具体方法，如validate为true，则表示获取需要登录验证的处理方法，false为不需要登录验证的方法
	 * @param name
	 * @param validate
	 * @return
	 */
	public static Method getJsonMethod(String name, boolean validate)
	{
		return validate ? validateJson.get(name) : json.get(name);
	}
	
	/**
	 * 获取以REST方式请求的servlet处理请求的具体方法，如validate为true，则表示获取需要登录验证的处理方法，false为不需要登录验证的方法
	 * @param path
	 * @param validate
	 * @return
	 */
	public static Method getDoServiceMethod(String path, boolean validate)
	{
		return validate ? validateService.get(path) : doService.get(path);
	}
	
}
