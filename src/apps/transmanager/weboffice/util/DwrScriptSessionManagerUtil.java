package apps.transmanager.weboffice.util;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.impl.DefaultScriptSession;
import org.directwebremoting.impl.DefaultScriptSessionManager;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.util.beans.PageConstant;

/**
 * 页面每次刷新，DWR会产生一个新的scriptSession,所以需要自己进行管理，销毁旧的scriptSession
 * @author 彭俊杰
 *
 */
public class DwrScriptSessionManagerUtil extends DefaultScriptSessionManager{
	public static final String SS_ID="DWR_ScriptSession_Id";
	public DwrScriptSessionManagerUtil(){
		try{
			addScriptSessionListener(new ScriptSessionListener() {
				
				public void sessionDestroyed(ScriptSessionEvent event) {
					
				}
				
				public void sessionCreated(ScriptSessionEvent event) {
					ScriptSession scriptSession = event.getSession();//得到新创建的scriptSession
					HttpSession httpSession = WebContextFactory.get().getSession();//得到产生的httpSession
					Users user = (Users) httpSession.getAttribute(PageConstant.LG_SESSION_USER);//得到当前用户
					if(user==null)
					{
						scriptSession.invalidate();   
						httpSession.invalidate();   
						return;
					}
					String ssId = (String) httpSession.getAttribute(SS_ID);//查找SS_ID
					if(ssId!=null)
					{
						//说明已经存在旧的scriptSession.注销这个旧的scriptSession
						DefaultScriptSession oldScriptSession = sessionMap.get(ssId);
						if(oldScriptSession!=null)
						{
							invalidate(oldScriptSession);
						}
					}
					httpSession.setAttribute(SS_ID, scriptSession.getId());
					scriptSession.setAttribute(PageConstant.LG_USER_ID, user.getId());//绑定用户ID到ScriptSession上
				}
			});
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void invalidate(String ssId)
	{
		Browser.withSession(ssId, new Runnable() {
			
			public void run() {
				 Collection<ScriptSession> sessions = Browser.getTargetSessions();
				 for(ScriptSession session : sessions)
				 {
					 session.invalidate();
				 }
			}
		});	
	}
	

}
