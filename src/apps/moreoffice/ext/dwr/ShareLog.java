package apps.moreoffice.ext.dwr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import apps.moreoffice.ext.share.QueryDb;
import apps.moreoffice.ext.share.ShareFileTip;
import apps.transmanager.weboffice.domain.Fileinfo;

public class ShareLog 
{
	public ArrayList isReaded(HttpServletRequest request)
	{
		ShareFileTip queryTip=new ShareFileTip();
		String[] pathinfo=(String[])request.getAttribute("paths");
		if(pathinfo != null)
		{
			ArrayList list = queryTip.queryLog(pathinfo[0]);
			return list;
		}
		return null;
	}
    public void insertFileLog(HttpServletRequest request)
    {
    	List result=new ArrayList();
    	HttpSession session=request.getSession();
		if (session==null)
		{
			return ;
		}
		try
		{
			String session_id=session.getId();
			String server_id=(String)session.getAttribute("server_id");
			if (server_id==null)
			{
				server_id="1";
			}
			QueryDb querydb=(QueryDb)session.getAttribute("querydb");
			if (querydb==null)
			{
				querydb=new QueryDb();
				session.setAttribute("querydb",querydb);
			}
			

			Fileinfo fileinfo =(Fileinfo)session.getAttribute("fileinfo");
			long uid = Long.parseLong((String)session.getAttribute("openuid"));
			String pi = fileinfo.getPathInfo();
			String sql ="select * from file a  where a.pathinfo =\'"+pi+"\' ";
			Vector vector  =querydb.query(sql,1);
			if(vector!=null && vector.size()>0)
			{
				sql = "insert into file(fileName,pathInfo) values(\'"+(String)fileinfo.getFileName()+"\',\'"+pi+"\')";
				
				vector  =querydb.query(sql,1);
				
			}
			sql ="select fileId from file a  where a.pathinfo =\'"+pi+"\' ";
			vector  =querydb.query(sql,1);
			long fid = ((Long)vector.get(0)).longValue();
			java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
			sql = "insert into filelog(fileid,uid,optime,optype) values("+fid+","+uid+","+date+",1)";		
			vector  =querydb.query(sql,1);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
