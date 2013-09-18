package apps.transmanager.weboffice.service.auth.login;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.util.server.CheckUtility;
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 */
public class CASAutoLogin implements AutoLogin
{
	private static String[] userMap;
	private static String groupName;
	
	static
	{
		try
		{
			String map = PropsValue.get(PropsConsts.CAS_USER_MAPPING);
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
			if ("false".equalsIgnoreCase(PropsValue.get(PropsConsts.CAS_AUTH_ENABLED)))
			{
				return userInfo;
			}
			HttpSession session = request.getSession();
			String userName = (String)session.getAttribute(CASFilter.CAS_FILTER_USER);
			if (CheckUtility.isNull(userName))
			{
				return userInfo;
			}
			userInfo = new Hashtable<String, Object>();
			userInfo.put(PropsConsts.USER_NAME, userName);
			userInfo.put(PropsConsts.LOGIN_TYPE, PropsConsts.SSO_LOGIN);
			userInfo.put(PropsConsts.LDAP_USER_GROUP_NAME, groupName);
			Object temp;
			int size = userMap.length;
			for(int i = 0; i < size; i += 2)
			{
				temp = session.getAttribute(userMap[i + 1]);
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
