package apps.transmanager.weboffice.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.ITalkService;
import apps.transmanager.weboffice.service.impl.TalkService;
import apps.transmanager.weboffice.util.beans.PageConstant;

import com.opensymphony.xwork2.ActionSupport;

public class ImFileTransAction extends ActionSupport{
	private static final long serialVersionUID = 1L;
	    private ITalkService talkService;

		public  void downLoad() throws Exception{
	    	    String uploadUrl = "data/uploadfile/talk/resource";
	    		HttpServletResponse response=ServletActionContext.getResponse();
	    		HttpServletRequest request=ServletActionContext.getRequest();
	    		String trueName=request.getParameter("trueName");
	    		String fileName=request.getParameter("fileName");
	    		trueName=new String(trueName.getBytes("ISO-8859-1"),"utf-8");
	    		fileName=new String(fileName.getBytes("ISO-8859-1"),"utf-8");
	    		String  type=request.getParameter("type");
	    		String msgId=request.getParameter("msgId");
	    		String id=request.getParameter("acceptId");
	    		long acceptId=0;
	    		long msgid=0;
	    		if(org.apache.commons.lang.StringUtils.isNumeric(id) &&org.apache.commons.lang.StringUtils.isNumeric(msgId)){
	    			acceptId=Long.parseLong(id);
	    			msgid=Long.parseLong(msgId);
	    			Users user=(Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
	    			if(type.equals("0")){
	    				this.getTalkService().personFileTransBefore(msgid);
		    		    this.noticeUserPersonAgree(user, acceptId, trueName);
		    		    downloadFile(response,request.getRealPath("/"),uploadUrl,fileName,trueName);
	    			}if(type.equals("1")){
	    				this.getTalkService().groupFileTransBefore(msgid, acceptId);
	    				this.noticeUserGroupAgree(user, acceptId, trueName);
	    				downloadFile(response,request.getRealPath("/"),uploadUrl,fileName,trueName);
	    			}else{
	    				
	    			}
	    		}
	    }
	    
	    /**
	     * 通知用户同意接收文件
	     * @param user
	     *     当前用户
	     * @param acceptId
	     *            消息接收者的ID
	     * @param fileName
	     *            接收的文件名称
	     */
	    private void noticeUserPersonAgree(Users user,long acceptId,String fileName){
	    	this.getTalkService().sendSessionMeg("\t [<span  style='color:red;'>系统提示</span>]"+"同意并接收您的传输文件("+fileName+")<br/>", user, acceptId, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    }
	    
	    /**
	     * 通知用户同意接收文件
	     * @param user
	     *     当前用户
	     * @param acceptId
	     *            消息接收者的ID
	     * @param fileName
	     *            接收的文件名称
	     */
	    private void noticeUserGroupAgree(Users user,long groupId,String fileName){
	    	this.getTalkService().sendGroupSessionMeg("\t [<span  style='color:red;'>系统提示</span>]"+"同意并接收您的传输文件("+fileName+")<br/>", user, groupId, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    }
	    
	    /**
	     * 下载文件
	     * @param response
	     *           输出流
	     * @param basePath
	     *           基本路径
	     * @param path
	     *           路径
	     * @param filename
	     *            文件名称
	     * @param trueName
	     *            文件的真实名称
	     * @throws Exception
	     */
	    private void downloadFile(HttpServletResponse response,String basePath,String path,String filename,String trueName) throws Exception{
	    	// path是指欲下载的文件的路径。
            File file = new File(basePath+File.separator+path+File.separator+filename);
            if(file.exists()&&file.canRead()){
	            InputStream fis = new BufferedInputStream(new FileInputStream(file));
	            byte[] buffer = new byte[fis.available()];
	            fis.read(buffer);
	            fis.close();
	            // 清空response
	            response.reset();
	            // 设置response的Header
	            response.addHeader("Content-Disposition", "attachment;filename=" +new String(trueName.getBytes(),"ISO-8859-1"));
	            response.addHeader("Content-Length", "" + file.length());
	            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	            response.setContentType("application/octet-stream");
	            toClient.write(buffer);
	            toClient.flush();
	            toClient.close();
            }
	    }
	    
	    /**
	     * 通知用户同意接收文件
	     * @param user
	     *     当前用户
	     * @param acceptId
	     *            消息接收者的ID
	     * @param fileName
	     *            接收的文件名称
	     */
	    private void noticeUserEnd(Users user,long acceptId,String fileName){
	    		this.getTalkService().sendSessionMeg(" "+"成功接收文件("+fileName+")<br/>", user, acceptId, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    }

		public ITalkService getTalkService() {
			if( null == talkService){
				talkService=(ITalkService) WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext()).getBean(TalkService.NAME);
			}
			return talkService;
		}
	    
	    
}
