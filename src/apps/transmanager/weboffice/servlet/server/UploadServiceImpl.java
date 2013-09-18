package apps.transmanager.weboffice.servlet.server;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.approval.ApprovalUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.WebTools;

// 该类将要删除，所有控制统一一个入口处理。

public class UploadServiceImpl extends HttpServlet
{

    /**
     *  
     */
    private static final long serialVersionUID = 1L;     

    public void destroy()
    {
    	UserOnlineHandler.applicationQuit();
    	super.destroy();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException
    {
    	req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String caibiansrc = req.getParameter("caibiansrc");//采编系统的图片
        if (caibiansrc != null)
        {
        	try
            {
        		resp.setCharacterEncoding("utf-8");
        		resp.setContentType("application/octet-stream");
        		String fileName = WebConfig.webContextPath + caibiansrc;
                resp.setHeader("Content-Disposition", "attachment;filename=\"pic.jpg\"");
                resp.setStatus(206);
                FileInputStream in = new FileInputStream(fileName);
                OutputStream oos = resp.getOutputStream();
                byte[] buff = new byte[1024];
                int readed;
                while((readed = in.read(buff)) > 0)
    	        {
                	oos.write(buff, 0, readed);
                }
                //res.setHeader("end", String.valueOf(total));
                oos.flush();
                oos.close();
                in.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        	return;
        }
        String caibian = req.getParameter("caibian");
        if (caibian != null)//判断有没有采编权限
        {
        	String userName = WebTools.converStr(req.getParameter("userName"));
        	PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
        	UserService userS = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        	Users user = userS.getUser(userName);
        	long per = user != null ? service.getSystemPermission(user.getId()) : 0;
        	String temp = req.getParameter("open");
        	if (temp != null)
        	{
        		if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))
        		{
        			FileSystemService fileS = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
        			if (fileS.getSendCollectEdit(WebTools.converStr(temp)) != null)
        			{
        				resp.getWriter().write("true");
        				return;
        			}
        		}
        		resp.getWriter().write("false");
        		return;
        	}
        	else if (!FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_ANY_FLAG))
        	{
        		String error = req.getParameter("error");
	        	resp.sendRedirect(error);;
	        	return;
	        }
	        resp.sendRedirect(caibian);
	        return;
        	        	
        }
        String action = WebTools.converStr(req.getParameter(ServletConst.ACTION_KEY));
        if (action == null || action.length() < 1)
        {
        	System.out.println("doget:command id must be specified.");
            return;
        }
        
        if (action.equals(ServletConst.HEART_BEAT_ACTION))
        {
        	UserOnlineHandler.heartbeat(req, resp);
            return;
        }
        if (action.equals(ServletConst.ONLINE_ACTION))
        {
        	UserOnlineHandler.onlineCheck(req, resp);
            return;
        }        
        if (action.equals(ServletConst.OPEN_FILE_PATH_ACTION))   
        {
        	FilesHandler.getOpenFilePath(req, resp);
            return;
        }
        /*else if (action.equals(ServletConst.FILE_EXIST_ACTION))
        {
        	FilesHandler.doCheckDuplicateFileName(req, resp);
            return;
        }*/
        if (action.equals(ServletConst.ADD_SAVE_LIST_ACTION))
        {
        	FilesHandler.addSaveList(req, resp);
            return;
        }
        if (action.equals(ServletConst.GET_MEDIA_ACTION))  // 视频预览参见网页openmedia.html
        {
        	FilesHandler.getMedia(req, resp);
        	return;
        }
        if (action.equals(ServletConst.GET_ALL_FILES_ACTION))
        {
        	FilesHandler.getAllFileList(req, resp);
        	return;
        }
        if (action.equals(ServletConst.GET_ALL_SPACE_ACTION))
        {
        	FilesHandler.getAllSpaceByUserID(req, resp);
        	return;
        }
        if (action.equals(ServletConst.REMOVE_SAVE_LIST_ACTION))
        {
            FilesHandler.removeSaveList(req, resp);
            return;
        }
        if (action.equals(ServletConst.GET_SAVE_LIST_ACTION))
        {
            FilesHandler.getSaveList(req, resp);
            return;
        }        
        if(action.equals(ServletConst.REMOVE_OPEN_LIST_ACTION))  //网络另存到本地，需做一件事情，就是清打开列表，所以还需与服务器交互
        {
            FilesHandler.removeOpenList(req, resp);
            return;
        }       
        if (action.equals(ServletConst.MOVE_FILE_ACTION))
        {
        	FilesHandler.moveFile(req, resp);
        	return;
        }        
        if (action.equals(ServletConst.LOGIN_ACTION))
        {
        	UserOnlineHandler.login(req, resp);
            return;
        }
        if (action.equals(ServletConst.AUTO_LOGIN_ACTION))
        {
        	UserOnlineHandler.autoLogin(req, resp);
            return;
        }
        if (action.equals(ServletConst.WORK_SPACE_SIZE_ACTION))
        {
        	FilesHandler.workspaceSize(req, resp);
        	return;
        }
        if (action.equals(ServletConst.FURBISH_ACTION)) //登录页面需要拿历史记录,数据是加密过的,所以需要解密(登录名自动填写)
        {
        	UserOnlineHandler.furbish(req, resp);
            return;
        }
        if (action.equals(ServletConst.LOGIN_COMPLETE_ACTION))
        {
            HttpSession session = req.getSession();
            session.removeAttribute("isLoginContinue");
            return;
        }
        if(action.equals(ServletConst.QUIT_ACTION))
        {      
        	try{
        	UserOnlineHandler.quit(req, resp);
        	//resp.sendRedirect("login.html");
        	}catch (Exception e) {
				e.printStackTrace();
			}
        	return;
        }
        if(action.equals(ServletConst.LOAD_NAME_ACTION))
        {
        	UserOnlineHandler.loadName(req, resp);
        	return ;
        }
        if(action.equals(ServletConst.CHECK_LOGIN_ACTION))//检查是否已经登录
        {
        	UserOnlineHandler.checkLogin(req, resp);
        	return;
        }
        if(action.equals(ServletConst.CA_LOGIN_ACTION))
        {
        	UserOnlineHandler.CALogin(req, resp);
        	return;
        }
        if(action.equals(ServletConst.LOGIN_CAINFO_ACTION))
        {
        	UserOnlineHandler.loginCaInfo(req, resp);
        	return;
        }
        
        if(action.equals(ServletConst.CA_SESSIONLOGIN_ACTION))
        {
        	UserOnlineHandler.CASessionLogin(req, resp);
        	return;
        }
        if (ServletConst.FORM_DOWNLOAD_ACTION.equals(action))
        {
        	FilesHandler.dowloadFile(req, resp);
            return;
        }
        if (ServletConst.GET_AUDIT_FILES_ACTION.equals(action))
        {
        	FilesHandler.getApprovalFileList(req, resp);
            return;
        }
        if (ServletConst.AUDIT_FILE_ACTION.equals(action))
        {
        	FilesHandler.processApproval(req, resp);
            return;
        }
        if (ServletConst.AUDIT_SPACE_NAME_ACTION.equals(action))
        {
        	FilesHandler.getApprovalSpaceName(req, resp);
            return;
        }        
        if (ServletConst.IS_FILE_EXIST_ACTION.equals(action))
        {
        	FilesHandler.isFilesExist(req, resp);
            return;
        }
        if (ServletConst.RESET_PASSWORD_AXTION.equals(action))
        {
        	UserOnlineHandler.resetUserPassword(req, resp);
            return;
        }
        if (ServletConst.SYSTEM_PERMISSION_ACTION.equals(action))
        {
        	FilesHandler.getPermission(req, resp);
            return;
        }
        
    }
           
    	
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        //        resp.setCharacterEncoding("UTF-8");
    
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");    
        String action = WebTools.converStr(req.getParameter(ServletConst.ACTION_KEY));
        String nopassword = (String)req.getAttribute("nopassword");
        if(null!=nopassword&&"true".equals(nopassword))
        {
        	action = ServletConst.LOGIN_ACTION;
        }
        if (action == null)   // || action.length() < 1)   // 该条件需要applet同时修改
        {
            System.out.println("doget:command id must be specified.");
            return;
        }        
        if (action.equals(ServletConst.LOGIN_ACTION))           ///   ???????????
        {
        	doGet(req, resp);
        	return;
        }        
        if (action.equals(ServletConst.UPLOAD_USER_PORTRAIT_ACTION))
        {
        	UserOnlineHandler.uploadUserPortrait(req, resp);
        	return;
        }        
        if (action.equals(ServletConst.UPLOAD_USER_PORTRAIT_CAMERA_ACTION))
        {
        	UserOnlineHandler.uploadUserPortraitbyCamera(req, resp);
        	return;
        } 
        if (action.equals(ServletConst.UPLOAD_GROUP_PORTRAIT_ACTION))
        {
        	UserOnlineHandler.uploadGroupPortrait(req, resp);
        	return;
        }
        if (action.equals(ServletConst.UPLOAD_ORG_PORTRAIT_ACTION))
        {
        	UserOnlineHandler.uploadOrgPortrait(req, resp);
        	return;
        }
        if(action.equals(ServletConst.CAN_SAVE_ACTION))
        { 
            FilesHandler.canSave(req, resp);
            return;
        }        
        if (action.equals(ServletConst.PLAY_MEDIA_ACTION))  // 视频的预览
        {
        	FilesHandler.handleMedia(req, resp);
        	return;
        }
        if (action.equals(ServletConst.UPLOAD_FILE_ACTION))
        {
        	FilesHandler.saveUploadFile(req, resp);
        	return;
        }
        /*if (action.equals(ServletConst.MS_SAVE_ACTION))
        {
        	FilesHandler.mssaveFile(req, resp);
        	return;
        }*/
        if (action.equals(ServletConst.CHECK_IN_ACTION))
        {
        	FilesHandler.unlockFile(req, resp);
        	return;
        }
        if (action.equals(ServletConst.CHECK_OUT_ACTION))
        {
        	FilesHandler.lockFile(req, resp);
        	return;
        }    
        if (action.equals(ServletConst.IMPORT_USER_ACTION))
        {
        	FilesHandler.importUser(req, resp);
            return;
        }
        if (action.equals(ServletConst.IMPORT_LICENSE_ACTION))
        {
            FilesHandler.importLicense(req, resp);
            return;
        }
        if(action.equals(ServletConst.GET_EXIST_FILES_ACTION))
        {
        	FilesHandler.getExistFiles(req, resp);
        	return;
        }
        if (ServletConst.FORM_UPLOAD_ACTION.equals(action))
        {
        	FilesHandler.uploadFileToRepository(req, resp);
            return;
        }
        if (ServletConst.UPLOAD_AUDIT_ACTION.equals(action))
        {
        	FilesHandler.uploadFileForAudit(req, resp);
            return;
        }
        if(ServletConst.UPLOADTEMPFILE.equals(action))
        {
        	FilesHandler.uploadTempFile(req, resp);
        }
        
        // user290 2012-09-03 桌面签批功能
        if ("isDaiQianPermit".equals(action))
        {
        	ApprovalUtil.instance().isDaiQianPermit(req, resp);
        	return;
        }
        if ("getLeaderNoAduitRecord".equals(action))
        {
        	ApprovalUtil.instance().getLeaderNoAduitRecord(req, resp);
        	return;
        }
        if ("readApprovalRecord".equals(action))
        {
        	ApprovalUtil.instance().readApprovalRecord(req, resp);
        	return;
        }
        if ("aduitOperationRecord".equals(action))
        {
        	ApprovalUtil.instance().aduitOperationRecord(req, resp);
        	return;
        }
        if ("getApproveGroupMembers".equals(action))
        {
        	ApprovalUtil.instance().getApproveGroupMembers(req, resp);
        	return;
        }
        if ("isYiQianPermit".equals(action))
        {
        	ApprovalUtil.instance().isYiQianPermit(req, resp);
        	return;
        }
        if ("auditGoBackRecord".equals(action))
        {
        	ApprovalUtil.instance().auditGoBackRecord(req, resp);
        	return;
        }
        // end
        
        // 需要同时修改applet
        String load = WebTools.converStr(req.getParameter("load"));    
        if (ServletConst.CREATE_FOLDERS_ACTION.equals(load))
        {
        	FilesHandler.createFolder(req, resp);
        	return;
        }
        if (ServletConst.UPLOADFILE_ACTION.equals(load))
        {
        	FilesHandler.handleUploadFile(req, resp);
        	return;
        }
        if(ServletConst.LODA_TREE_ACTION.equals(load))
        {
        	FilesHandler.loadTree(req, resp);
        	return;
        }
        if(ServletConst.SAME_FILE_CHECK_ACTION.equals(load))
        {
        	FilesHandler.checkSameFiles(req, resp);
        	return;
        } 
       
    }
    
} 

