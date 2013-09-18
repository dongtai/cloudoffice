package apps.transmanager.weboffice.service.auth.ldap;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.objects.PropsValue;
import apps.transmanager.weboffice.util.server.PasswordEncryptor;


public class LDAPUtil
{

	private static Log _log = LogFactory.getLog(LDAPUtil.class);
	private static String[] userMap;
	private static String[] groupMap;
	private static String groupName;
	
	static
	{
		try
		{
			String map = PropsValue.get(PropsConsts.LDAP_USER_MAPPING);
			userMap = map.split("[;=]");
			map = PropsValue.get(PropsConsts.LDAP_GROUP_MAPPING);
			groupMap = map.split("[;=]");
			groupName = PropsValue.get(PropsConsts.LDAP_USER_GROUP_NAME);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static LdapContext getContext()
	{

		Properties env = new Properties();

		env.put(Context.INITIAL_CONTEXT_FACTORY, PropsValue.get(PropsConsts.LDAP_CONTEXT_FACTORY));
		env.put(Context.PROVIDER_URL, PropsValue.get(PropsConsts.LDAP_PROVIDER_URL));
		env.put(Context.SECURITY_PRINCIPAL, PropsValue.get(PropsConsts.LDAP_SECURITY_PRINCIPAL));
		env.put(Context.SECURITY_CREDENTIALS, PropsValue.get(PropsConsts.LDAP_SECURITY_CREDENTIALS));
		env.put(Context.REFERRAL, PropsValue.get(PropsConsts.LDAP_REFERRAL));
		env.put(Context.SECURITY_AUTHENTICATION, PropsValue.get(PropsConsts.LDAP_SECURITY_AUTHENTICATION));

		// Enable pooling

		env.put("com.sun.jndi.ldap.connect.pool", PropsValue.get(PropsConsts.LDAP_CONNECT_POOL_ENABLED));
		env.put("com.sun.jndi.ldap.connect.pool.maxsize", PropsValue.get(PropsConsts.LDAP_CONNECT_POOL_MAXSIZE));
		env.put("com.sun.jndi.ldap.connect.pool.timeout", PropsValue.get(PropsConsts.LDAP_CONNECT_POOL_TIMEOUT));

		LdapContext ctx = null;
		try
		{
			ctx = new InitialLdapContext(env, null);
		}
		catch (Exception e)
		{
			_log.warn("Failed to bind to the LDAP server " + e);
			e.printStackTrace();
		}

		return ctx;
	}
		
	public static Hashtable<String, Object> authenticateUser(String emailAddress, String password) throws Exception
	{
		String baseDN = PropsValue.get(PropsConsts.LDAP_BASE_USER_DN);
		LdapContext ctx = getContext();
		if (ctx == null)
		{
			return null;
		}
		// Process LDAP auth search filter
		String filter = getAuthSearchFilter(emailAddress);
		Hashtable<String, Object> retA = null;
		try
		{
			SearchControls cons = new SearchControls(getSearchScop(), 1, 0, null, false, false);
			NamingEnumeration<SearchResult> enu = ctx.search(baseDN, filter, cons);
			if (enu.hasMoreElements())  // 搜索到有相应邮件地址的用户


			{
				SearchResult result = enu.nextElement();
				String fullUserDN = getUserRDN(result);
				Attributes attrs = result.getAttributes();

				Attributes authResult = authenticate(ctx, attrs, fullUserDN, password);
				if (authResult == null)
				{
					return null;
				}
				retA = getAuthUserInfo(authResult);
			}
			enu.close();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			ctx.close();
		}
		return retA;
	}

	private static Attributes authenticate(LdapContext ctx,
			Attributes attrs, String userDN, String password) throws Exception
	{
		String authMethod = PropsValue.get(PropsConsts.LDAP_AUTH_METHOD);
		InitialLdapContext innerCtx = null;
		Attributes retAttr = null;
		if (authMethod.equals("bind"))
		{
			try
			{
				Hashtable<String, Object> env = (Hashtable<String, Object>) ctx.getEnvironment();
				env.put(Context.SECURITY_PRINCIPAL, userDN);
				env.put(Context.SECURITY_CREDENTIALS, password);
				// Do not use pooling because principal changes
				env.put("com.sun.jndi.ldap.connect.pool", "false");
				innerCtx = new InitialLdapContext(env, null);
				retAttr = innerCtx.getAttributes(userDN);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (innerCtx != null)
				{
					innerCtx.close();
				}
			}
		}
		else if (authMethod.equals("password-compare"))
		{
			Attribute userPassword = attrs.get(PropsValue.get(PropsConsts.LDAP_AUTH_PASS_NAME));
			if (userPassword != null)
			{
				String ldapPassword = new String((byte[])userPassword.get());
				String encryptedPassword = password;
				String algorithm = PropsValue.get(PropsConsts.LDAP_AUTH_PASS_EN_ALG);
				encryptedPassword =	"{" + algorithm + "}"
						+ PasswordEncryptor.encrypt(algorithm, password, ldapPassword);
				if (ldapPassword.equals(encryptedPassword))
				{
					retAttr = attrs;
				}
			}
		}

		return retAttr;
	}
	
	private static Hashtable<String, Object> getAuthUserInfo(Attributes attrs)
	{	
		int size = userMap.length;
		Hashtable<String, Object> ret = new Hashtable<String, Object>();
		Attribute at;
		for(int i = 0; i < size; i += 2)
		{
			at = attrs.get(userMap[i + 1]);
			if (at != null)
			{
				try
				{
					ret.put(userMap[i], at.get(0));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}		
		ret.put(PropsConsts.LOGIN_TYPE, PropsConsts.LDAP_LOGIN);
		ret.put(PropsConsts.LDAP_USER_GROUP_NAME, groupName);
		return ret;
	}
	
	private static int getSearchScop()
	{
		String filter = PropsValue.get(PropsConsts.LDAP_AUTH_SEARCH_SCOPE);
		if (filter.equals("subtree"))
		{
			return SearchControls.SUBTREE_SCOPE;
		}
		else if (filter.equals("onelevel"))
		{
			return SearchControls.ONELEVEL_SCOPE;
		}
		return SearchControls.OBJECT_SCOPE;
	}
	
	private static String getUserRDN(Binding binding)
			throws Exception
	{

		String baseDN = PropsValue.get(PropsConsts.LDAP_BASE_USER_DN);
		StringBuffer sb = new StringBuffer();
		sb.append(binding.getName());
		sb.append(",");
		sb.append(baseDN);
		return sb.toString();
	}
	
	private static String getAuthSearchFilter(String emailAddress)
			throws Exception
	{

		String filter = PropsValue.get(PropsConsts.LDAP_AUTH_SEARCH_USER_FILTER);

		filter = replace(filter, new String[]{PropsConsts.LDAP_USER_FILTER}, 
						new String[]{emailAddress});

		return filter;
	}
	
	private static String replace(String source, String[] oldS, String[] newS)
	{
		StringBuffer ret = new StringBuffer();
		if (oldS.length != newS.length)
		{
			return source;
		}
		int size = oldS.length;
		int index;
		for (int i = 0; i < size; i++)
		{
			if ((index = source.indexOf(oldS[i])) != -1)
			{
				ret.append(source.substring(0, index));
				ret.append(newS[i]);
				source = source.substring(index + oldS[i].length());
			}
		}
		ret.append(source);
		
		return ret.toString();
	}


}
