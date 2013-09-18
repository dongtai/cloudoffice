package apps.moreoffice.ext.share;

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TODO: 文件注释
 * <p>
 * <p>
 * <p>
 * 作者:           SunAiHua
 * <p>
 * 日期:           2010-2-8
 * <p>
 * 负责人:         SunAiHua
 */
public class MyEcache implements ServletContextListener 
{
    public static CacheManager manager;
    private static final Log log = LogFactory.getLog(MyEcache.class);
    
    
    public void contextInitialized(ServletContextEvent e) {
		CacheManager ehcache = new CacheManager();
		Cache cache = ehcache.getCache("mycache");
		e.getServletContext().setAttribute("cache", cache);
	}

	public void contextDestroyed(ServletContextEvent e) {
		e.getServletContext().removeAttribute("cache");
	}
	
    /**
     * 获取公共缓存
     * @return
     */
    
    public CacheManager getCache()
    {
        if (manager==null)
        {
            URL url = getClass().getResource("/conf/ehcache.xml");
            manager = new CacheManager(url);
        }
        return manager;
    }
    /**
     * 存放缓存数据
     * @param request
     * @param id
     */
    public static void setEhcache(HttpServletRequest request,String id)
    {
        CacheManager manager=(new MyEcache()).getCache();
        try
        {
            if (id!=null)
            {
                Cache cache = manager.getCache("userlogin");
                double total=0.0;
                synchronized(manager)
                {
                    total=Math.random();
                }
                String userloginid="U"+id;
                HttpSession session=request.getSession();
                //String old=(String)getEhcache(userloginid);
                session.setAttribute("HADLOGIN",userloginid+total);
                session.setAttribute("LOGINUSERID",userloginid);
                Element element = new Element(userloginid, userloginid+total);
                cache.put(element);
                //setCache();
            }
        }
        catch (Exception e)
        {
            log.info(e);
        }
    }
    /**
     * 获取缓存中的内容
     * @param userloginid
     * @return
     */
    public static Object getEhcache(String userloginid)
    {
        CacheManager manager=(new MyEcache()).getCache();
        try
        {
            Cache cache = manager.getCache("userlogin");
            Element element = cache.get(userloginid);
            if (element!=null)
            {
                Object value = element.getObjectValue();
                return value;
            }
        }
        catch (Exception e)
        {
            log.info(e);
        }
        return null;
    }
    public static void delEhcache(String userloginid)
    {
        CacheManager manager=(new MyEcache()).getCache();
        try
        {
            if (userloginid!=null)
            {
                Cache cache = manager.getCache("userlogin");
                try
                {
	                Element element = cache.get(userloginid);
	                element.getValue();
	                
                }
                catch (Exception e)
                {
                	
                }
                cache.remove(userloginid);
                //清除会话信息，还是加一个会话监听比较好
//            	com.share.QueryDb querydb=new com.share.QueryDb();
//            	String SQL="update serverlogon set effect='N',logonout=now() where server_id="+server_id;
//            	querydb.modifydata(SQL);
//            	session.invalidate();
            }
        }
        catch (Exception e)
        {
            log.info(e);
        }
    }
    public static void setCache()
    {
    	CacheManager ehcache = new CacheManager();
		Cache cache = ehcache.getCache("mycache");
		Element obj = new Element( "name", "Winter Lau" );
		cache.put(obj);
		Element cache_obj = cache.get("name");
    }
    
    public static Cache getCache(String cacheName){
    	if(manager==null)
    	{
    		URL url = MyEcache.class.getResource("/conf/ehcache.xml");
    		manager = new CacheManager(url);
    	}
    	Cache cache = manager.getCache(cacheName);
		return cache;
    }
    
    public static void main(String[] args) {
		CacheManager ehcache = new CacheManager();
		Cache cache = ehcache.getCache("mycache");
		Element obj = new Element( "name", "Winter Lau" );
		cache.put(obj);
		Element cache_obj = cache.get("name");
	}
}



