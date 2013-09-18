package apps.transmanager.weboffice.servlet.listener;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.handler.MailHandler;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.util.DwrScriptSessionManagerUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.server.convertforread.bean.ConvertForRead;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * 作者:           User266(胡鹏云)
 * <p>
 * 日期:           Oct 20, 2008
 * <p>
 * 负责人:         User266(胡鹏云)
 * <p>
 * 负责项目组:      DCS
 * <p>
 * <p>
 */
public class WebOfficeSessionListener implements HttpSessionListener
{
    public void sessionCreated(HttpSessionEvent arg0)
    {
    }

    public void sessionDestroyed(HttpSessionEvent req)
    {
    	HttpSession session = req.getSession();
    	//文档转换的垃圾文档删除
    	ConvertForRead.clear(session.getId());
    	
    	MailHandler.clearMailBean(session);
    	
    	
//    	Object key;
//    	if (se != null && (key = se.getAttribute(UploadServiceImpl.USER_LIST_KEY)) != null)
//    	{
//    		UploadServiceImpl.sessiontable.remove(key);
//    	}
    	try
    	{
    		//孙爱华增加，用户检测用户是否退出
//    		String userid=(String)session.getAttribute("userid");
//			if (userid==null )
//			{
//				userid="0";
//			}
//    		QueryDb querydb=(QueryDb)session.getAttribute("querydb");
//			if (querydb==null)
//			{
//				querydb=new QueryDb();
//			}
//    		String SQL="delete from clientinfo where session_id='"+userid+"'";
//    		System.out.println("logout============="+SQL);
//    		querydb.modifydata(SQL);

    		if (session!=null)
    		{
    			//处理session注销事件，删除在线用户
    			Users user = (Users) session.getAttribute(PageConstant.LG_SESSION_USER);
    			if(user!=null)
    			{
    				//删除在线用户
    				String ssId = (String) req.getSession().getAttribute("DWR_ScriptSession_Id");
    				DwrScriptSessionManagerUtil.invalidate(ssId);
    				delOnline(user.getId());
    			}
    			
    			
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	if (session!=null)
    	{
    		UserOnlineHandler.quit(session, false, null);
    	}
    	
    }
    
    private void delOnline(Long userId)
    {
    	Connection con = null;
    	String url = "jdbc:mysql://localhost/weboffice";
    	String user = null;
    	String pwd = null;
    	InputStream in = WebOfficeSessionListener.class.getResourceAsStream("/conf/funcConfig.properties");
    	Properties p = new Properties();
    	PreparedStatement statem = null;
    	try {
    		p.load(in);
    		url = p.getProperty("jdbc-0.proxool.driver-url");
    		user = p.getProperty("jdbc-0.user");
    		pwd = p.getProperty("jdbc-0.password");
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		con = DriverManager.getConnection(url, user, pwd);
    		if(con!=null)
    		{
    		    String sql = "delete from online_tb where userId="+userId;
				statem = con.prepareStatement(sql);
				statem.execute();
				statem.close();
				con.close();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(statem!=null)
			{
				try {
					statem.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
}