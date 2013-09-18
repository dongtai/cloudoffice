package apps.transmanager.weboffice.service.objects;

import java.io.Serializable;


public class LoginUserInfo implements Serializable
{
	/**
	 */
	private static final long serialVersionUID = 0x9876543210abcdefL;
	
	private String domain;               // 用户所属domain
	private String name;                 // 用户登录名
	private String token;                // 用户登录认证token               
	private String ip;                   // 用户登录ip地址。
	
	public LoginUserInfo(String domain, String name, String token, String ip)
	{
		this.domain = domain;
		this.name = name;
		this.token = token;
		this.ip = ip;
	}
	
	public void changeAuth(String token, String ip)
	{
		this.token = token;
		this.ip = ip;
	}
	
	public String getName()
	{	
		return name;
	}
	
	public void setName(String name)
	{	
		this.name = name;
	}
	
	public String getToken()
	{	
		return token;
	}
	
	public void setToken(String token)
	{	
		this.token = token;
	}
	
	public String getIp()
	{	
		return ip;
	}
	
	public void setIp(String ip)
	{	
		this.ip = ip;
	}
	
	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public boolean equals(LoginUserInfo o)
	{
		if (o == null)
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		if (((name == null && o.name == null) || name.equals(o.name))
				&& ((domain == null && o.domain == null) || domain.equals(o.domain))
				&& ((ip == null && o.ip == null) || ip.equals(o.ip))
				&& ((token == null && o.token == null) || token.equals(o.token)))
		{
			return true;
		}
		return false;
		
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("domain=:").append(domain);
		sb.append(",login user name=:").append(name);		
		sb.append(",token=:").append(token);
		sb.append(",ip=:").append(ip);
		return sb.toString();
	}	
}
