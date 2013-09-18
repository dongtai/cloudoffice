package apps.transmanager.weboffice.service.auth.login;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.auth.opensso.OpenSSOUtil;
import apps.transmanager.weboffice.service.objects.PropsValue;

/**
 */
public class OpenSSOAutoLogin implements AutoLogin
{

	private static String[] userMap;
	private static String groupName;
	
	static
	{
		try
		{
			String map = PropsValue.get(PropsConsts.OPENSSO_USER_MAPPING);
			userMap = map.split("[;=]");
			groupName = PropsValue.get(PropsConsts.LDAP_USER_GROUP_NAME);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Hashtable<String, Object> login(HttpServletRequest request, HttpServletResponse response)
	{
		Hashtable<String, Object> userInfo = null;
		try
		{
			if ("false".equalsIgnoreCase(PropsValue.get(PropsConsts.OPENSSO_ENABLED)))
			{
				return userInfo;
			}
			String serviceUrl = PropsValue.get(PropsConsts.OPENSSO_SERVICE);
			if (!OpenSSOUtil.isAuthenticated(request, serviceUrl))
			{
				return userInfo;
			}
			
			Map<String, String> nameValues = OpenSSOUtil.getAttributes(request,	serviceUrl);
			userInfo = new Hashtable<String, Object>();
			userInfo.put(PropsConsts.LOGIN_TYPE, PropsConsts.SSO_LOGIN);
			userInfo.put(PropsConsts.LDAP_USER_GROUP_NAME, groupName);

			Object temp;
			int size = userMap.length;
			for(int i = 0; i < size; i += 2)
			{
				temp = nameValues.get(userMap[i + 1]);
				if (temp != null)
				{
					userInfo.put(userMap[i], temp);
				}
			}			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return userInfo;
	}

}
