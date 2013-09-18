package apps.transmanager.weboffice.service.cache;

import java.util.Hashtable;
import java.util.Map;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.notifications.annotation.CacheListener;
import org.jboss.cache.notifications.annotation.NodeModified;
import org.jboss.cache.notifications.event.NodeEvent;

import apps.transmanager.weboffice.service.objects.LoginUserInfo;

@CacheListener
public class MemCache  implements IMemCache
{
	private boolean cluster;
	private IMemCache memCache;
	
	public MemCache()
	{
	}

	protected void init()
	{
		cacheStart();
        addCacheListener(this);
	}
	
    @NodeModified
	public void nodeEvent(NodeEvent e)
	{
		switch (e.getType())
		{
			case NODE_MODIFIED:
				//System.out.println("the ==============================   "
				//		+",usercount="+memCache.getLoginUserCount(USER_LOGING_COUNT));
				/*if (memCache.isRepUserEvent(e))
				{
					Map<String, String> repUse = memCache.getRepUser();
					//System.out.println("the size ====================  "+repUse.size());
					if (repUse == null || repUse.size() < 1)
					{
						break;
					}
					
					memCache.removeAllRepUser();
					//System.out.println("the size 222 ====================  "+memCache.getRepUser().size());
					Set<String> keys = repUse.keySet();
					UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
					LicenseService licenseService = (LicenseService)ApplicationContext.getInstance().getBean("licenseService");
					for (String key: keys)
					{
						//System.out.println("the keys is  ===================  "+key);
						handleRepUser(userService, key, repUse.get(key), null);
					}
				}*/
				break;
		}
	}
    
	public boolean isCluster()
	{
		return cluster;
	}
	
	public void setCluster(boolean cluster)
	{
		this.cluster = cluster;		
	}

	public void addCacheListener(Object lis)
	{
		memCache.addCacheListener(lis);
	}

	/**
	 * 删除集群缓存监听器
	 * @param lis
	 */
	public void removeCacheListener(Object lis)
	{
		memCache.removeCacheListener(lis);
	}
	
	public void cacheStart()
	{
		if (cluster)
		{
			memCache = new ClusterCache();
		}
		else
		{
			memCache = new LocalCache();
		}
		memCache.cacheStart();
	}

	public void cacheStop()
	{
		memCache.cacheStop();
	}

	public Integer getLoginUserCount()
	{
		return memCache.getLoginUserCount();
	}

	public LoginUserInfo getLoginUserInfo(String mail)
	{
		return memCache.getLoginUserInfo(mail);
	}

	/*public boolean isRepUserEvent(NodeEvent e)
	{
		return memCache.isRepUserEvent(e);
	}*/


	public void removeLoginUserInfo(String key)
	{
		memCache.removeLoginUserInfo(key);
	}

	public void setLoginUserInfo(String key, LoginUserInfo user)
	{
		memCache.setLoginUserInfo(key, user);
	}
	
	/**
	 * 
	 */
	public Map<String, LoginUserInfo> getAllLoginUser()
	{
	    return memCache.getAllLoginUser();
	}
	
	static class LocalCache implements IMemCache
	{
		private Map<String, LoginUserInfo> allLoginUser = new Hashtable<String, LoginUserInfo>();

		public void addCacheListener(Object lis)
		{
		}

		/**
		 * 删除集群缓存监听器
		 * @param lis
		 */
		public void removeCacheListener(Object lis)
		{
		}
		public void cacheStart()
		{
			allLoginUser.clear();
		}

		public void cacheStop()
		{
			allLoginUser.clear();
		}

		public Integer getLoginUserCount()
		{
			return allLoginUser.size();
		}

		public LoginUserInfo getLoginUserInfo(String key)
		{
			return allLoginUser.get(key);
		}

		/*public boolean isRepUserEvent(NodeEvent e)
		{
			return false;
		}*/


		public void removeLoginUserInfo(String key)
		{
		    allLoginUser.remove(key);
		}

		public void setLoginUserInfo(String key, LoginUserInfo user)
		{
		    allLoginUser.put(key, user);
		}
		
		/**
	     * 
	     */
	    public Map<String, LoginUserInfo> getAllLoginUser()
	    {
	        return allLoginUser;
	    }
		
	}
	
	static class ClusterCache  implements IMemCache
	{
		private static String USER_LOGIN = "loginUserInfo";		
		private Cache cache;
		private Node<String, LoginUserInfo> userNode;
		
		public ClusterCache()
		{
			init();
		}
		
		private void init()
		{
			CacheFactory cf = new DefaultCacheFactory();
			cache = cf.createCache("/resource/jboss-cache.xml");
			Node root = cache.getRoot();
			Fqn userInfo = Fqn.fromString("/" + USER_LOGIN);
			userNode = root.addChild(userInfo);
		}
		
		public void addCacheListener(Object lis)
		{
			cache.addCacheListener(lis);
		}
		/**
		 * 删除集群缓存监听器
		 * @param lis
		 */
		public void removeCacheListener(Object lis)
		{
			cache.removeCacheListener(lis);
		}
		public void cacheStart()
		{
			cache.start();
		}
		
		public void cacheStop()
		{
			cache.stop();
		}
		
		public void setLoginUserInfo(String key, LoginUserInfo user)
		{
			userNode.put(key, user);
		}
		
		public LoginUserInfo getLoginUserInfo(String key)
		{
			/*Map<String, LoginUserInfo> aaa = userNode.getData();
			for (String temp : aaa.keySet())
			{
				System.out.println("==============key======="+temp);
			}*/
			return userNode.get(key);
		}
		
		public void removeLoginUserInfo(String key)
		{
			userNode.remove(key);
		}
		
		
		public Integer getLoginUserCount()
		{
			return userNode.dataSize();
		}
		
		/*public boolean isRepUserEvent(NodeEvent e)
		{
			//System.out.println("the ---------------------- "+e.isOriginLocal()+",|type="+e.getType()
			//		+ ",|element=" + e.getFqn().getLastElementAsString()+ ",|size=" + getRepUser().size());
			if (e.isPre() || e.isOriginLocal())
			{
				return false;
			}
			String name = e.getFqn().getLastElementAsString();
			//System.out.println("+++++++++++++++++++++++++++  "+name);
			if (REP_USER.equals(name))
			{
				return true;
			}
			return false;
		}*/
		
		 /**
	     * 
	     */
	    public Map<String, LoginUserInfo> getAllLoginUser()
	    {
	        return userNode.getData();
   	    }
	}
	
}
