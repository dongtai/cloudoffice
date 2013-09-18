package apps.transmanager.weboffice.servlet.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import apps.moreoffice.ext.share.MyEcache;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.service.approval.SignUtil;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.util.server.WebTools;


// 该类将要删除，所有的请求统一到一个类中控制

public class DownLoadFile extends HttpServlet
{

    private static final String CONTENT_TYPE = "text/html;charset=utf-8";

    /*protected void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        System.out.println("");
    }*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        doPost(request, response);
        //System.out.println("");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
        	request.setCharacterEncoding("UTF-8");
        	response.setContentType(CONTENT_TYPE); 
            
            String action = WebTools.converStr(request.getParameter(ServletConst.ACTION_KEY));
            String copyurl = WebTools.converStr(request.getParameter(ServletConst.COPYURL));
            // 导出报表
            if (ServletConst.SYS_REPORT_EXPORT_ACTION.equals(action))
            {
                FilesHandler.systeReportExport(request, response);
                return;
            }
            if (ServletConst.LOG_EXPORT_ACTION.equals(action))
            {            	
            	FilesHandler.exportLogs(request, response);
            	return;
            }
            if (ServletConst.EXPORT_IMPORT_USER_TEMPLATE_ACTION.equals(action))
            {
            	FilesHandler.exportImportUserTemplate(request, response);
                return;
            }
            if (ServletConst.IMPORT_COMPANYUSER_TEMPLATE_ACTION.equals(action))
            {
            	FilesHandler.userorgtemplate(request, response);
                return;
            }
            if (ServletConst.SEND_MAIL_DOEN_ACTION.equals(action))
            {
                FilesHandler.sendMailDown(request, response);
                return;
            }
            if (ServletConst.CHECK_OPEN_FILE_STATUS_ACTON.equals(action))
            {
                FilesHandler.chekOpenFileStatus(request, response);
                return;
            }
            if (ServletConst.FILE_OPENED_ACTON.equals(action))
            {
                FilesHandler.isFileOpened(request, response);
                return;
            }
            if (ServletConst.DELETE_TEMP_FILE_ACTION.equals(action))
            {
            	FilesHandler.deleteTempFile(request, response);
                return;
            }
            if (ServletConst.DEL_MERGE_ACTION.equals(action))
            {            	
                FilesHandler.deleteMerge(request, response);
                return;
            }
            if (action.equals(ServletConst.EXPORT_END_ACTION))
			{
            	SignUtil.instance().exportEnd(request, response);//办结
				return ;
			}
            /*if (ServletConst.DOWN_MERGE_ACTION.equals(action))//转移到FileOpeServlet中，规范传输
            {            	
                FilesHandler.downloadMerge(request, response);
                return;
            }*/
            if (ServletConst.SAVE_ENCRYPT_ACTION.equals(action))
            {
            	FilesHandler.saveEncypt(request, response);
            	return;
            }
            if (ServletConst.SAVE_DECRYPT_ACTION.equals(action))
            {
            	FilesHandler.saveDecypt(request, response);
            	return;
            }
            if (ServletConst.GET_FILE_LIST_ACTION.equals(action))   // 为移动临时增加。
            {
            	FilesHandler.getFileList(request, response);
            	return;
            }if("rssDown".equals(action)){
            	response.setContentType("text/xml;charset=utf-8");
            	String url = request.getParameter("url");
            	StringBuffer content = new StringBuffer();
            	String line = "";
            	String xml = "";
            	Element el = null;
            	//先从缓存中获取
            	Cache rssCache = MyEcache.getCache("rsscache");
        		el = rssCache.get(url);
        		if(el!=null)
        		{
        			xml = (String) el.getObjectValue();
        			if(xml!=null && !"".equals(xml) && !xml.contains("访问出错"))
        			{
	        			response.getWriter().print(xml);
	                	response.getWriter().flush();
	                	response.getWriter().close();
	                	return;
        			}
        		}
            	URL ser = new URL(url);
            	HttpURLConnection con = (HttpURLConnection) ser.openConnection();
            	//con.connect();
            	InputStream in = con.getInputStream();
            	BufferedReader br = new BufferedReader(new InputStreamReader(in,"gb2312"));
            	while((line=br.readLine())!=null)
            	{
            		content.append(line);
            	}
            	in.close();
            	con.disconnect();
            	xml = content.toString();
            	//byte[] b = cc.getBytes("UTF-8");
            	//String xml = new String(b, 0, b.length, "UTF-8");
//            	Document doc = DocumentHelper.parseText(xml);
//            	System.out.println("(2)"+xml);
            	xml = xml.replace("gb2312", "UTF-8");
            	el = new Element(url, xml);
                rssCache.putQuiet(el);
            	response.getWriter().print(xml);
            	response.getWriter().flush();
            	response.getWriter().close();
            }
            else if (action != null && action.equals("ckeyhelp"))
            {
                char sep = File.separatorChar;
                String tempPath = request.getSession().getServletContext().getRealPath("setup")
                    + sep + "ckeypcsc.chm";
                FileInputStream in = new FileInputStream(tempPath);
                byte[] data = org.apache.commons.io.IOUtils.toByteArray(in);
//                downLoad(request, response, data, "Member.eio");
                response.setContentType("application/octet-stream");
                String tempName = URLEncoder.encode("ckeyhelp.chm", "utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + tempName + "\"");
                ServletOutputStream out = response.getOutputStream();
                out.write(data);
                return;
            }
            else if (copyurl!=null && copyurl.length()>0)
            {
            	FilesHandler.downloadFile(request, response, null);
            }
            else  // if (ServletConst.DOWNLOAD_FILE_ACTION.equals(action))
            {
            	FilesHandler.downloadFiles(request, response);
            	return;
            }
        }
        catch(Exception e)
        {
            response.getWriter().print("0");
            e.printStackTrace();
        }
    }    

}
 