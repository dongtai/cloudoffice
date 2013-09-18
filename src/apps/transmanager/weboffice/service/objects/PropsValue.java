package apps.transmanager.weboffice.service.objects;

import java.util.Properties;

import apps.transmanager.weboffice.constants.server.PropsConsts;

public class PropsValue
{
	
	
	private static Properties properties = new Properties();
	
	static
	{
		try
		{
			//System.out.println("==="+LDAPUtil.class.getClassLoader().getResource("/config/loginConfig.properties"));
			properties.load(PropsValue.class.getClassLoader().getResourceAsStream(PropsConsts.COFING_FILE));			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String name)
	{
		return properties.getProperty(name);
	}
	
	public static String get(String name)
	{
		String ret = properties.getProperty(name);
		if (ret != null && !ret.equalsIgnoreCase(""))
		{
			ret = ret.trim();
		}
		return ret;
	}
	
	public static boolean isSSOLogin()
	{
		if ("true".equalsIgnoreCase(PropsValue.get(PropsConsts.SSO_LOGIN_ENABLE)))
		{
			return true;
		}
		return false;
	}
	
}
