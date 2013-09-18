package apps.transmanager.weboffice.service.cache;

import java.util.Map;

import apps.transmanager.weboffice.service.objects.LoginUserInfo;

/**
 * 
 * 记录系统中用户登录认证信息。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface IMemCache
{
	/**
	 * 添加集群缓存监听器
	 * @param lis
	 */
	void addCacheListener(Object lis);
	
	/**
	 * 删除集群缓存监听器
	 * @param lis
	 */
	void removeCacheListener(Object lis);
	
	/**
	 * 集群缓存启动
	 */
	void cacheStart();
	
	/**
	 * 集群缓存停止
	 */
	void cacheStop();
	
	/**
	 * 设置用户登录认证信息
	 * @param key 该值为domain+name。
	 * @param user
	 */
	void setLoginUserInfo(String key, LoginUserInfo user);
	
	/**
	 * 获得某个登录用户认证信息
	 * @param key 该值为domain+name。
	 * @return
	 */
	LoginUserInfo getLoginUserInfo(String key);
	
	/**
	 * 删除某个用户登录认证信息
	 * @param key
	 */
	void removeLoginUserInfo(String key);
	
	/**
	 * 获得目前登录的在线用户数	
	 * @return
	 */
	Integer getLoginUserCount();
		
	//boolean isRepUserEvent(NodeEvent e);
	
	/**
	 * 获得系统中所有在线用户信息
	 */
	Map<String, LoginUserInfo> getAllLoginUser();
	
}
