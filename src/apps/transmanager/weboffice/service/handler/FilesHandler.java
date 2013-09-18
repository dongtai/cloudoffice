package apps.transmanager.weboffice.service.handler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.jbpm.task.User;

import applets.common.Constants;
import applets.common.DocumentObject;
import apps.moreoffice.annotation.HandlerMethod;
import apps.moreoffice.annotation.ServerHandler;
import apps.moreoffice.annotation.ServerPath;
import apps.moreoffice.ext.share.ShareFileTip;
import apps.transmanager.weboffice.client.constant.MainConstant;
import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.MainConstants;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.CalendarEvent;
import apps.transmanager.weboffice.databaseobject.CalendarInviteer;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.CustomTeamsFiles;
import apps.transmanager.weboffice.databaseobject.EntityMetadata;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Personshareinfo;
import apps.transmanager.weboffice.databaseobject.PublishAddress;
import apps.transmanager.weboffice.databaseobject.ServerChangeRecords;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.UserDesks;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.caibian.CollectEdit;
import apps.transmanager.weboffice.databaseobject.caibian.CollectEditSend;
import apps.transmanager.weboffice.domain.ApprovalBean;
import apps.transmanager.weboffice.domain.ApproveBean;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.service.IPMicroblogService;
import apps.transmanager.weboffice.service.approval.ApprovalUtil;
import apps.transmanager.weboffice.service.approval.MessageUtil;
import apps.transmanager.weboffice.service.approval.SignUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.PersonshareinfoDAO;
import apps.transmanager.weboffice.service.dwr.CalendarEventImpl;
import apps.transmanager.weboffice.service.dwr.ICalendarService;
import apps.transmanager.weboffice.service.impl.PMicroblogService;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.license.LicenseService;
import apps.transmanager.weboffice.service.objects.FileOperLog;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.InitDataService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.LogServices;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.service.sysreport.SysMonitor;
import apps.transmanager.weboffice.util.GridUtil;
import apps.transmanager.weboffice.util.beans.Page;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.Client;
import apps.transmanager.weboffice.util.server.FileMD5;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;
import apps.transmanager.weboffice.util.server.ZipUtils;
import emo.net.SimpleFileinfo;

/**
 * 处理系统中与文件库有管理的内容。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
// 所有方法从原UploadServiceImpl中移植过来，后续在修改。

@ServerHandler
public class FilesHandler
{

	private final static int BYTE_SIZE = 1024 * 8;
	private final static String DEFAULT_DOAMIN = "com.yozo.do";
	private final static String OCTET_APP = "application/octet-stream";
	private final static String bigType[] = { "xls", "ppt", "doc", "pdf",
			"txt", "xlsx", "docx", "pptx" };
	private final static String smallType[] = { "xls", "ppt", "doc", "pdf" };
	private static Properties officeVersion = new Properties();
	private static long officeVersionTime;

	/**
	 * 在applet中打开文件时候。 文件被打开的时候，标记文件为锁定状态。
	 * 
	 * @param req
	 * @param res
	 */
	public static void lockFile(HttpServletRequest req, HttpServletResponse res)
	{
		String path = null;
		String email = null;
		JCRService jcrService = null;
		try
		{
			jcrService = (JCRService) ApplicationContext.getInstance().getBean(
					JCRService.NAME);
			path = WebTools.converStr(req.getParameter(Constants.FilePath));
			email = WebTools.converStr(req.getParameter("email")).replace('@',
					'_');
			// jcrService.lock(email, path);//(path, "", fin);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 在applet中关闭文件时候。 文件关闭后，解锁文件
	 * 
	 * @param req
	 * @param res
	 */
	public static void unlockFile(HttpServletRequest req,
			HttpServletResponse res)
	{
		String path = null;
		String email = null;
		JCRService jcrService = null;
		String userName = "";
		try
		{
			// String companyId =
			// WebTools.converStr(req.getParameter(Constants.CommandId));
			jcrService = (JCRService) ApplicationContext.getInstance().getBean(
					JCRService.NAME);
			path = WebTools.converStr(req.getParameter(Constants.FilePath));
			email = WebTools.converStr(req.getParameter("email")).replace('@',
					'_');
			userName = WebTools.converStr(req.getParameter("username"));
			// String fileName =
			// WebTools.converStr(req.getParameter("fileName"));
			// String appletIsLock =
			// WebTools.converStr(req.getParameter("appletIsLock"));
			// isSaved的实际含义是该文件是否应该加入到打开列表
			// 打开的文件，已进行过保存操作的文件，都属于应该加入到打开列表的文件
			// saveUploadFile(req,res);

			String isSaved = WebTools.converStr(req.getParameter("isSaved"));
			int idx = path.indexOf("jcr:system/jcr:versionStorage");
			if (isSaved.equals("true") && idx < 0)
			{
				jcrService.removeUserOpenFile(email, path);
				jcrService.removeOpenedFile(userName, path);
			}

			// 解锁
			// if (appletIsLock.equals("appletIsLock"))
			// {
			// jcrService.unLock(email, path);//(path, "", fin);
			//
			// // System.out.println();
			// // System.out.println("unlock path: "+path);
			// // System.out.println();
			// }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			// try
			// {
			// String isSaved = WebTools.converStr(req.getParameter("isSaved"));
			//
			// /**
			// * 容错处理，如果打开列表里没有该文件，说明已出错，将该文件加入关闭列表,
			// * 不应加入到打开列表的文件不予处理
			// */
			// ArrayList openList = jcrService.getFileList(email, email, 0);
			// if (isSaved.equals("true") && !openList.contains(path))
			// {
			// jcrService.addFileList(email, path, 2);
			// String userID = email.replace('@', '_');
			// Thread.sleep(1500);
			// jcrService.clearFileList(userID, 1);
			// // System.out.println();
			// // System.out.println("exception add to close file");
			// // System.out.println();
			// }
			// //关档
			// if (isSaved.equals("true"))
			// {
			// //jcrService.removeFileList(email, path, 0);
			// jcrService.removeUserOpenFile(email, path);
			// jcrService.removeOpenedFile(userName, path);
			// // System.out.println("clear open list");
			// }
			// }
			// catch(Exception e)
			// {
			//
			// }
		}
	}

	/**
	 * 打开文件库中的文件，把文件库中的文件保存到系统临时目录中，response返回为该文件在系统临时
	 * 目录中的服务位置。具体格式为“工程名/临时目录名/文件名”。
	 * 
	 * @param req
	 * @param res
	 */
	public static void getOpenFilePath(HttpServletRequest req,
			HttpServletResponse res)
	{
		try
		{
			// String companyId =
			// WebTools.converStr(req.getParameter(Constants.CommandId));
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			String path = WebTools.converStr(req
					.getParameter(Constants.FilePath));
			String appletIsLock = WebTools.converStr(req
					.getParameter("appletIsLock"));
			String email = WebTools.converStr(req.getParameter("email"))
					.replace('@', '_');
			String fileName = WebTools.converStr(req.getParameter("fileName"));
			// check out
			Users userInfo = (Users) req.getSession().getAttribute("userKey");
			if (userInfo == null)
			{
				UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
				userInfo = userService.getUserBySpaceUID(email);
			}
			if (appletIsLock.equals("appletIsLock"))
			{
				// jcrService.lock(email, path);
				if (userInfo != null)
				{
					// jcrService.lock(email, userInfo.getRealName(), path);
				}
			}
			// 设置被共享者的新标记
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			// Userinfo userInfo =
			// (Userinfo)req.getSession().getAttribute("userKey");
			String username = "";
			String spaceUID = "";
			if (userInfo != null)
			{
				fileSystemService.setFileNewFlagByShareUser(path, userInfo
						.getId(), 1);
				fileSystemService.setFileNewFlagByShareUser2(path, userInfo
						.getId(), 1);
				fileSystemService.setFileNewFlagByMember(path,
						userInfo.getId(), 1);
				username = userInfo.getUserName();
				spaceUID = userInfo.getSpaceUID();
			}

			Fileinfo fileinfo = new Fileinfo();
			fileinfo.setPathInfo(path);
			fileinfo.setFileName(fileName);

			// String author = path.substring(0,path.indexOf('_'));
			int idx = path.indexOf("jcr:system/jcr:versionStorage");
			int idx2 = path.indexOf("system_audit_root");
			int index = path.indexOf('/');
			if (index != -1 && !path.startsWith(FileConstants.WORKFLOW)
					&& idx < 0 && idx2 < 0)
			{
				String cc = path.substring(0, index);
				System.out.println("cc == " + cc);
				int index2 = cc.lastIndexOf('_');

				String dd = cc.substring(0, index2);
				System.out.println("dd == " + dd);

				int index0 = dd.indexOf('_');// 去除user_开头部分
				if (index0 >= 0 && index0 + 1 < dd.length())
				{
					dd = dd.substring(index0 + 1);
					System.out.println("dd == " + dd);
				}

				String author = dd;

				// if (!username.equals(author))
				// {
				// System.out.println("author::::" + author + ",username+++++" +
				// username);
				// ShareFileTip queryTip = new ShareFileTip();
				//
				// String pi = fileinfo.getPathInfo();
				// // long uid =ApplicationParameters.instance.user.getUserId();
				// //boolean fileIsExist = queryTip.queryFileByFN(pi,
				// userInfo.getUserId());
				// //System.out.println("queryTip.queryFileByFN======"+fileIsExist);
				// Long fid = queryTip.queryFileID(pi);//孙爱华优化，可节省查询一次数据库
				// if (fid == null)
				// {
				// String realname = fileinfo.getShareRealName();
				// // fileinfo.
				// long authorid = 0l;
				// if (realname == null)
				// {
				// realname = author;
				// }
				// System.out.println("author========" + author);
				// if (realname != null)
				// {
				// Long uI = queryTip.queryUserinfoID(realname);
				// if (uI != null)
				// {
				// authorid = uI.longValue();
				// queryTip.insertFileinfo(fileinfo, authorid);
				//
				// fid = queryTip.queryFileID(pi);
				// System.out.println("queryTip.queryFileID======" + fid);
				// queryTip.insertFileLog(fid.longValue(), userInfo.getId(), 1);
				// }
				// }
				// }
				// else
				// {
				// queryTip.insertFileLog(fid.longValue(), userInfo.getId(),
				// 1);//1为阅读
				// }
				// }
			}
			DocumentObject documentObject = new DocumentObject();
			documentObject.data = downLoadForOpenFile(req, res, path, path
					.substring(path.lastIndexOf("/") + 1), username, spaceUID);
			ObjectOutputStream oos = new ObjectOutputStream(res
					.getOutputStream());
			oos.writeObject(documentObject);
			oos.flush();
			oos.close();

			if (idx < 0 && idx2 < 0)
			{
				jcrService.setUserRecentFile(userInfo.getSpaceUID(), path);
				boolean permitwrite = hasPermission(path, userInfo, FileSystemCons.WRITE_FLAG);
				if (permitwrite)//只有写权限的用户才记录打开标记
				{
					jcrService.addOpenedFile(userInfo.getUserName(), path);
				}
				jcrService.addUserOpenFile(userInfo.getSpaceUID(), path);
			}

			FileOperLog fileOperLog = new FileOperLog(fileName, path, username,
					userInfo.getRealName(), "", "打开", "");
			// LogsUtility.logToFile(fileOperLog.getUserName(),
			// DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true,
			// fileOperLog);
			Thread receiveT = new Thread(new InsertFileLog(
					new String[] { path }, userInfo.getId(), 1, fileOperLog,
					null));
			receiveT.start();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * 从文件流中把文件数据保存在临时目录
	 * 
	 * @param request
	 * @param response
	 * @param data
	 * @param fileName
	 * @throws Exception
	 */
	private static byte[] downLoadForOpenFile(HttpServletRequest request,
			HttpServletResponse response, String repPath, String fileName,
			String userName, String spaceUID) throws Exception
	{
		long time = System.currentTimeMillis();
		int index = fileName.lastIndexOf('.');
		String mime = fileName.substring(index);
		String newName = String.valueOf(time) + mime;
		System.out.println("downLoadForOpenFile fileName :::" + fileName);
		newName = getFileFromRepository(repPath, WebConfig.tempFilePath,
				newName, true, userName, spaceUID);
		String httpUrl = request.getContextPath() + "/"
				+ WebConfig.TEMPFILE_FOLDER + "/" + newName;
		try
		{
			return httpUrl.getBytes("UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new byte[4];
	}

	/**
	 * 获得需要文件的数据流。在response中返回文件的完整数据内容。
	 * 
	 * @param req
	 * @param res
	 * @deprecated
	 */
	@Deprecated
	public static void openFile(HttpServletRequest req, HttpServletResponse res)
	{
		String path = WebTools.converStr(req.getParameter("path"));
		getFileContent(req, res, path, true);
	}

	/**
	 * 下载文件。以标准的网页下载方式返回文件
	 * 
	 * @param req
	 * @param res
	 * @deprecated
	 */
	@Deprecated
	public static void dowloadFile(HttpServletRequest req,
			HttpServletResponse res)
	{
		String path = WebTools.converStr(req.getParameter("path"));
		getFileContent(req, res, path, false);
		// -----------记录共享文件的下载日志
		// res.setContentType("application/octet-stream");
		// JCRService jcrService =
		// (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
		// String path = WebTools.converStr(req.getParameter("path"));
		Users userInfo = null;
		userInfo = (Users) req.getSession().getAttribute("userKey");
		if (userInfo == null)
		{
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			String userId = req.getParameter("userID");
			userInfo = userService.getUser(Long.valueOf(userId));
		}
		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		fileSystemService.insertFileListLog(new String[] { path }, userInfo
				.getId(), 16, null, null);// 16为下载
	}

	/**
	 * applet 中更新文档
	 * 
	 * 后续同直接上传的文件方法合并。
	 * 
	 * @param req
	 * @param resp
	 */
	public static void saveUploadFile(HttpServletRequest req,
			HttpServletResponse resp)
	{
		try
		{
			req.getSession().setAttribute("savestart", "start");
			PrintWriter writer = resp.getWriter();
			String mail = WebTools.converStr(req.getParameter("email"));
			// String companyId =
			// WebTools.converStr(req.getParameter(Constants.CommandId));
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			String path = WebTools.converStr(req
					.getParameter(Constants.FilePath));
			String fileName = WebTools.converStr(req
					.getParameter(Constants.FileName));

			String mailPath = mail.replace('@', '_');
			String oldPath = WebTools.converStr(req.getParameter("oldPath"));
			// InputStream stream = req.getInputStream();
			// ObjectInputStream ois = new ObjectInputStream(stream);
			// Object obj = ois.readObject();
			// DocumentObject documentObject = (DocumentObject)obj;
			// byte[] data = documentObject.data;
			// 上传文件的修改
			// byte[] data = null;
			FileInputStream in = null;
			FileInputStream indata = null;
			File file = null;
			Users userInfo = null;
			Fileinfo fileinfo = new Fileinfo();// added by zzy
			try
			{
				String fPath = WebConfig.webContextPath + File.separatorChar
						+ "data"+File.separatorChar+"uploadfile" + File.separatorChar;
				fPath = fPath
						+ WebTools
								.converStr(req.getParameter("uploadfilename"));
				file = new File(fPath);
				if (!file.exists())
				{
					writer.print("exception1");
					return;
				}
				in = new FileInputStream(file);
				indata = new FileInputStream(file);
				/*
				 * data = new byte[fis.available()]; fis.read(data);
				 * fis.close(); file.delete();
				 */
			}
			catch (Exception e)
			{
				writer.print("exception1");
				e.printStackTrace();
			}
			/*
			 * int fileSize = data.length; ByteArrayInputStream fin = new
			 * ByteArrayInputStream(data);
			 */
			// Users userInfo = (Users)req.getSession().getAttribute("userKey");
			userInfo = (Users) req.getSession().getAttribute("userKey");
			if (userInfo == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				String userId = req.getParameter("userID");
				userInfo = userService.getUser(Long.valueOf(userId));
			}
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			PermissionService permissionService = (PermissionService) ApplicationContext
					.getInstance().getBean(PermissionService.NAME);
			String isUpdate = WebTools.converStr(req.getParameter("isUpdate"));
			if (isUpdate.equals("true"))
			{
				jcrService.update(mailPath, path, in, in.available(), oldPath,
						true);

				if (userInfo != null)
				{
					fileSystemService.setFileNewFlagByShareOwner(path, userInfo
							.getId(), 0);
					fileSystemService.setFileNewFlagByMember2(path, userInfo
							.getId(), 0);

					// 如果是送审文档，则需要做版本控制
					if (ApprovalUtil.instance().hasApproval(path))
					{
						jcrService.createVersion(path, userInfo.getRealName(),
								MainConstants.APPROVAL_VERSION_COMMENT,
								MainConstants.APPROVAL_VERSION_STATUS_CURRENT);
					}
				}
				String shenlue = WebTools
						.converStr(req.getParameter("shenlue"));
				if ("true".equals(shenlue))
				{
					String targetPath = path.substring(0, path
							.indexOf(fileName));
					jcrService.move(mailPath, new String[] { path }, targetPath
							.replace("_WT_", "_AU_"),1);
					MessagesService messageService = (MessagesService) ApplicationContext
							.getInstance().getBean(MessagesService.NAME);
					messageService.updateSpaceNewMessages(
							new String[] { path }, targetPath.replace("_WT_",
									"_AU_"));
					permissionService.updateFileSystemActionForMove(targetPath
							.replace("_WT_", "_AU_"), path);
				}

				FileOperLog fileOperLog = new FileOperLog(fileName, path,
						userInfo.getUserName(), userInfo.getRealName(), "",
						"更新", "");
				LogsUtility.logToFile(fileOperLog.getUserName(), DateUtils
						.format(new Date(), "yyyy-MM-dd")
						+ ".log", true, fileOperLog);
				// LogUtility.log(webServerPath, fileOperLog);
			}
			else
			{
				Fileinfo info = fileSystemService.createFile(userInfo.getId(),
						userInfo.getRealName(), path, fileName, in, indata,
						true, oldPath);
				jcrService.setUserRecentFile(userInfo.getSpaceUID(), info
						.getPathInfo());
				jcrService.addOpenedFile(userInfo.getUserName(), info
						.getPathInfo());
				jcrService.addUserOpenFile(userInfo.getSpaceUID(), path);
				fileinfo = info;
				// String realPath = info.getPathInfo();
				// String tempRealPath = URLEncoder.encode(realPath, "utf-8");
				try
				{
					fileSystemService.addIntoSharePath(path, fileName);
					/*
					 * String sharePath = isShare(path+"/"+fileName); if
					 * (sharePath != null) { //如果向上查共享信息有的话，设置新共享信息
					 * List<Personshareinfo> list =
					 * getPersonshareinfo(sharePath); for(int i=0; i<
					 * list.size(); i++) { Personshareinfo shareinfo =
					 * list.get(i); saveNewPersonShareinfo(shareinfo,
					 * path+"/"+fileName); } }
					 */

				}
				catch (Exception e)
				{
				}
				FileOperLog fileOperLog = new FileOperLog(fileName, path,
						userInfo.getUserName(), userInfo.getRealName(), "",
						"新建", "");
				LogsUtility.logToFile(fileOperLog.getUserName(), DateUtils
						.format(new Date(), "yyyy-MM-dd")
						+ ".log", true, fileOperLog);
				// LogUtility.log(webServerPath, fileOperLog);

				// ServletOutputStream sO = resp.getOutputStream();
				// sO.write(tempRealPath.getBytes());
				// sO.close();
				// writer.print(tempRealPath);
			}
			writer.print("success");

			in.close();
			indata.close();
			file.delete();
			// check in
			/*
			 * String versionControl =
			 * WebTools.converStr(req.getParameter("versionControl")); String
			 * versionComment =
			 * "";//WebTools.converStr(req.getParameter("versionControl")); if
			 * (versionControl.equals("versionControl")) { fs.checkin(path,
			 * versionComment, ois); }
			 */
			req.getSession().setAttribute("savestate", "over");// 文件保存结束标记

			System.out.println("saveUploadFile  path:::" + path);
			String username = userInfo.getUserName();
			String paranetpath = path.substring(0, path.indexOf('/'));
			String author = path.substring(0, paranetpath.lastIndexOf('_'));
			int index0 = author.indexOf('_');// 去除user_开头部分
			if (index0 >= 0 && index0 + 1 < author.length())
			{
				author = author.substring(index0 + 1);
			}

			if (!username.equals(author))
			{
				System.out.println("author::::" + author + ",username+++++"
						+ username);
				ShareFileTip queryTip = new ShareFileTip();

				// String pi = fileinfo.getPathInfo();
				Long fid = queryTip.queryFileID(path);
				if (fid == null)
				{

					System.out.println("author========" + author);
					Long authorid = (queryTip.queryUserinfoID(author));
					if (authorid != null)
					{
						queryTip.insertFileinfo(fileinfo, authorid);
						fid = queryTip.queryFileID(path);
					}
				}

				System.out.println("queryTip.queryFileID======" + fid);
				if (fid != null)
				{
					queryTip
							.insertFileLog(fid.longValue(), userInfo.getId(), 2);// 2为编辑
				}

			}
			// int index = path.indexOf('/');
			// if (index != -1)
			// {
			// String cc = path.substring(0, index);
			// System.out.println("cc == "+cc);
			// int index2 = cc.lastIndexOf('_');
			//
			// String dd = cc.substring(0, index2);
			// System.out.println("dd == "+dd);
			//
			// String author = dd;
			//
			// if(author != null && !userInfo.getUserName().equals(author))
			// {
			//
			// String[] pathinfo ={path};
			// ShareFileTip queryTip=new ShareFileTip();
			// Long uid = queryTip.queryUserinfoID(author);
			// queryTip.insertFileListinfo(pathinfo,uid.longValue());
			// ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);
			// queryTip.insertFileListLog(fid,uid.longValue(),2);
			// }
			// }

			// long uid = userInfo.getUserId();
			// String[] pathinfo ={path};
			// ShareFileTip queryTip=new ShareFileTip();
			// queryTip.insertFileListinfo(pathinfo,uid);
			// ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);
			// int optype=2;
			// if (isUpdate.equals("true"))
			// {
			// optype=9;
			// }
			// queryTip.insertFileListLog(fid,uid,optype);

			//
			// long uid = userInfo.getUserId();
			// String[] pathinfo ={path};
			// ShareFileTip queryTip=new ShareFileTip();
			// queryTip.insertFileListinfo(pathinfo,uid);
			// ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);
			// int optype=2;
			// // if (isUpdate.equals("true"))
			// // {
			// // optype=9;
			// // }
			// queryTip.insertFileListLog(fid,uid,optype);
			//
		}
		catch (Exception ex)
		{
			req.getSession().setAttribute("savestate", "error");// 文件保存出错
			try
			{
				resp.getWriter().print("exception2");
			}
			catch (Exception e)
			{
				// 这里再出异常就没办法了
			}
			ex.printStackTrace();
		}

	}

	// ?????
	public static void addSaveList(HttpServletRequest req,
			HttpServletResponse resp)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String file = req.getParameter("file");
		String email = req.getParameter("email");
		jcrService.addSaveFile(file, email);
	}

	// ?????
	public static void getSaveList(HttpServletRequest req,
			HttpServletResponse resp)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String email = req.getParameter("email");
		String[] files = jcrService.getSaveFile(email);
		if (files == null || files.length == 0)
		{
			return;
		}
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < files.length; i++)
		{
			if (i != files.length - 1)
			{
				content.append(files[i] + ",");
			}
			else
			{
				content.append(files[i]);
			}
		}
		try
		{
			resp.getWriter().print(content);
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
	}

	// ????
	public static void removeSaveList(HttpServletRequest req,
			HttpServletResponse resp)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String file = req.getParameter("file");
		String email = req.getParameter("email");
		jcrService.removeSaveFile(email, file);
	}

	// ?????
	public static void removeOpenList(HttpServletRequest req,
			HttpServletResponse resp)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String path = WebTools.converStr(req.getParameter("oldPath"));
		String email = WebTools.converStr(req.getParameter("email")).replace(
				'@', '_');
		boolean isLock = WebTools.converStr(req.getParameter("appletIsLock"))
				.equals("appletIsLock");
		try
		{
			// jcrService.removeFileList(email, path, 0);
			if (isLock)
			{
				// jcrService.unLock(email, path);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ???
	public static void getMedia(HttpServletRequest req, HttpServletResponse resp)
	{
		String path = WebTools.converStr(req.getParameter("path"));
		String email = WebTools.converStr(req.getParameter("email"));
		String name = path.substring(path.lastIndexOf('/') + 1, path.length());
		int index = name.lastIndexOf(".");
		if (index == -1)
		{
			return;
		}
		String endName = name.substring(index + 1, name.length());
		String contenttype = null;
		if (endName.equalsIgnoreCase("avi"))
		{
			contenttype = "video/x-msvideo";
		}
		else if (endName.equalsIgnoreCase("asf"))
		{
			contenttype = "video/x-ms-asf";
		}
		else if (endName.equalsIgnoreCase("wmv"))
		{
			contenttype = "audio/x-ms-wmv";
		}
		else if (endName.equalsIgnoreCase("wma"))
		{
			contenttype = "audio/x-ms-wma";
		}
		else if (endName.equalsIgnoreCase("rm"))
		{
			contenttype = "audio/x-pn-realaudio";
		}
		else if (endName.equalsIgnoreCase("rmvb"))
		{
			contenttype = "video/vnd.rn-realvideo";
		}
		else if (endName.equalsIgnoreCase("mp3"))
		{
			contenttype = "audio/mpeg";
		}
		else if (endName.equalsIgnoreCase("swf"))
		{
			contenttype = "application/x-shockwave-flash";
		}
		resp.setContentType(contenttype);
		resp.setHeader("Content-Disposition", "attachment;filename=\"" + name
				+ "\"");
		resp.setHeader("Content-Transfer-Encoding", "binary");
		resp.setHeader("Cache-Control",
				"must-revalidate,post-check=0,pre-check=0");
		resp.setHeader("Pragma", "public");
		InputStream in = null;
		try
		{
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			in = jcrService.getContent(email.replace('@', '_'), path, false);
		}
		catch (RepositoryException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			byte[] b = new byte[BYTE_SIZE];
			int len = 0;
			while ((len = in.read(b)) > 0)
			{
				resp.getOutputStream().write(b, 0, len);
			}
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	// ???
	public static void moveFile(HttpServletRequest req, HttpServletResponse resp)
	{
		/*
		 * try { String path =
		 * WebTools.converStr(req.getParameter("sourcePath")); String targetPath
		 * = path.replace("_WT_", "_AU_"); JCRService jcrService =
		 * (JCRService)ApplicationContext
		 * .getInstance().getBean(JCRService.NAME); jcrService.move("", new
		 * String[]{path}, targetPath); resp.getWriter().print("true"); }
		 * catch(Exception e) { LogsUtility.error(e); try {
		 * resp.getWriter().print("false"); } catch(Exception ee) {
		 * LogsUtility.error(ee); } }
		 */
	}

	// ?????
	public static void workspaceSize(HttpServletRequest req,
			HttpServletResponse resp)
	{
		HttpSession session = req.getSession();
		Users userinfo = (Users) session.getAttribute("userKey");
		String email = userinfo.getSpaceUID();
		try
		{
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			DataHolder holder = jcrService.getWorkSpace(email);

			long[] data = holder.getLongData();
			long filesSize = data[2];
			long fileCount = data[0];
			long floderCount = data[1];
			// 换算(将字节转换成MB)
			double mb = (double) filesSize / 1048576;
			// 换算(将字节转换成KB)
			double size = (double) filesSize / 1024;
			String size2;
			if (size > 1024)// 大于1024时，将KB转换成MB，否则用KB作单位。
			{
				// 先将KB转换成MB，以MB为单位时保留二位小数，且小数点后第三位四舍五入处理。
				size = (double) (int) (size / 1024 * 100 + 0.5) / 100;
				size2 = size + "M";
			}
			else
			{
				// 以KB为单位时保留一位小数，且小数点后第二位四舍五入处理。
				size = (double) (int) (size * 10 + 0.5) / 10;
				if (size < 1)
				{
					size = 1;
				}
				size2 = size + "K";
			}
			// 空间大小
			boolean hasSize = true;
			if (userinfo.getStorageSize() == null)
			{
				hasSize = false;
			}

			final float sum = hasSize ? userinfo.getStorageSize().floatValue()
					: WebConfig.defaultsize;
			final String sumValue;
			if (sum == 1024)
			{
				sumValue = "1G";
			}
			else if (sum > 1024)
			{
				double sum0 = (double) (int) (sum / 1024 * 100 + 0.5) / 100;
				sumValue = numChange(sum0) + "G";
			}
			else
			{
				sumValue = sum + "M";
			}

			float size3 = (float) filesSize / (1024 * 1024);
			double wid = (size3 / sum);

			double leftSize = sum - mb;
			BigDecimal b = new BigDecimal(wid);

			wid = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			b = new BigDecimal(leftSize);

			leftSize = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			wid = wid * 100;

			wid = wid < 1 ? 1 : wid;

			resp.getWriter().print(
					size2 + "&" + sumValue + "&" + wid + "&" + fileCount + "&"
							+ floderCount + "&" + leftSize);

		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * 如果小数点后只有一位小数，则在小数后第二位补0
	 * 
	 * @param d
	 * @return
	 */
	private static String numChange(double d)
	{
		String newStr = d + "";
		int i = newStr.indexOf(".");
		if (i != -1)
		{
			String s = newStr.substring(i, newStr.length());
			if (s.length() == 2)
			{
				if (!(newStr.substring(i + 1, i + 2).equals("0")))
				{
					newStr = newStr.concat("0");
				}
			}
		}
		return newStr;
	}

	// ???
	public final static void canSave(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String email = WebTools.converStr(req.getParameter("email"));
		PrintWriter writer = resp.getWriter();
		if (!UserOnlineHandler.checkUserLoginStatus(req.getSession(), email))
		{
			writer.print("forbid");// 如果用户已离线,则禁止存盘
			return;
		}
		writer.print("allow");
	}

	private static Hashtable<String, Boolean> hashmap = new Hashtable<String, Boolean>();
	private static Vector<String> filenames = new Vector<String>();

	// ?????
	public final static void handleMedia(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String temp = WebTools.converStr(req.getParameter("oper"));
		if (temp.equals("getMediabefore"))
		{
			String path = WebTools.converStr(req.getParameter("path"));
			String fileName = path.substring(path.lastIndexOf("/"));
			long time = System.currentTimeMillis();
			int index = fileName.lastIndexOf('.');
			String mime = fileName.substring(index);
			String newName = String.valueOf(time) + mime;
			hashmap.put(newName, Boolean.FALSE);
			resp.getWriter().print(newName);
		}
		else if (temp.equals("getMedia"))
		{
			String path = WebTools.converStr(req.getParameter("path"));
			String email = WebTools.converStr(req.getParameter("email"));
			String newName = WebTools.converStr(req.getParameter("newName"));
			InputStream in = null;
			try
			{
				JCRService jcrService = (JCRService) ApplicationContext
						.getInstance().getBean(JCRService.NAME);
				in = jcrService
						.getContent(email.replace('@', '_'), path, false);
			}
			catch (RepositoryException e1)
			{
				e1.printStackTrace();
			}

			try
			{
				String tempFolder = WebConfig.tempFilePath; // getServletContext().getRealPath("tempfile");
				File foler = new File(tempFolder);
				if (!foler.exists())
				{
					foler.mkdir();
				}
				File file = new File(tempFolder + File.separatorChar + newName);
				file.createNewFile();

				FileOutputStream out = new FileOutputStream(file);
				byte[] b = new byte[BYTE_SIZE];
				int len = 0;
				while ((len = in.read(b)) > 0)
				{
					Boolean boo = hashmap.get(newName);
					if (boo != null && !boo.booleanValue())
					{
						out.write(b, 0, len);
					}
					else
					{
						out.close();
						file.delete();
						hashmap.remove(newName);
						resp.getWriter().print("false");
					}
				}
				out.close();
				String httpUrl = "tempfile/" + newName;
				hashmap.remove(newName);
				in.close();
				resp.getWriter().print(httpUrl);
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
		}
		else if (temp.equals("removeMedia"))
		{
			String newName = WebTools.converStr(req.getParameter("newName"));
			if (newName == null)
			{
				return;
			}
			Boolean boo = hashmap.get(newName);
			if (boo != null && !boo.booleanValue())// 表明还没生成完。
			{
				hashmap.put(newName, Boolean.TRUE);
				return;
			}
			try
			{
				if (filenames.size() > 0)
				{
					for (int i = filenames.size() - 1; i >= 0; i--)
					{
						File file = new File(filenames.get(i));
						boolean flag1 = file.delete();
						if (flag1)
						{
							filenames.remove(i);
						}
					}

				}
				String tempFolder = WebConfig.tempFilePath; // getServletContext().getRealPath("tempfile");
				File foler = new File(tempFolder);
				if (!foler.exists())
				{
					foler.mkdir();
				}
				File file = new File(tempFolder + File.separatorChar + newName);
				boolean flag1 = file.delete();
				if (!flag1)// 多线程有问题。。。
				{
					filenames.add(tempFolder + File.separatorChar + newName);
				}
				// resp.getWriter().print(flag1);
				return;// file.delete();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
			// resp.getWriter().print("false");
			return;
		}
	}

	/**
	 * 导入用户的组织结构及用户
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@Deprecated
	public static void importUser(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
		fileUploadByHttpForm(req, tempPath, "importStructure.txt");
		InitDataService initService = (InitDataService) ApplicationContext
				.getInstance().getBean(InitDataService.NAME);
		String ret = initService.importUsers(tempPath + "importStructure.txt", null);
		if (ret.length() > 0)
		{
			resp.getWriter().print(ret);
		}
		else
		{
			resp.getWriter().print("");
		}
	}
	
	/**
	 * 导入用户的组织结构及用户
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IMPORT_USERS)
	public static String importUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     // 后续验证权限
    	String temp = (String)param.get("companyId");
    	Long companyId = Long.valueOf(temp);
    	
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
		String name = System.currentTimeMillis() + "importStructure.txt"; 
		fileUploadByHttpForm(req, tempPath, name);
		InitDataService initService = (InitDataService) ApplicationContext.getInstance().getBean(InitDataService.NAME);
		String ret = initService.importUsers(tempPath + name, companyId);
		if (ret.length() > 0)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		}
		else
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}    
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
		File file = new File(tempPath + name);
		if (file.exists())
		{
			file.delete();
		}
		return error;
	}

	/**
	 * 导入系统license文件，并记录到系统中的表中。
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@Deprecated
	public static void importLicense(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		LicenseService licenseService = (LicenseService) ApplicationContext.getInstance().getBean("licenseService");
		// String tempPath =
		// userService.getTempPath(req.getSession().getServletContext());
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
		String[] ret = { "" };
		String fileName = System.currentTimeMillis() + "license";
		File file = new File(tempPath + fileName);
		fileUploadByHttpForm(req, tempPath, fileName);
		int value = licenseService.importLicense(file, ret);
		resp.getWriter().print(ret[0]);
		file.delete();
	}
	
	/**
	 * 导入系统license文件，并记录到系统中的表中。
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IMPORT_LICENSE)
	public static String importLicense(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     // 后续验证权限
    	
		LicenseService licenseService = (LicenseService) ApplicationContext.getInstance().getBean("licenseService");
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
		String[] ret = { "" };
		String fileName = System.currentTimeMillis() + "license";
		File file = new File(tempPath + fileName);
		fileUploadByHttpForm(req, tempPath, fileName);
		int value = licenseService.importLicense(file, ret);
		file.delete();
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret[0]);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
		//file.delete();
	}
	

	// 上传文件，将同applet中保存文件合并。
	public static void handleUploadFile(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String name = "";
		try
		{
			// "上传文件"
			// String targetFolderPath =
			// WebTools.converStr(req.getParameter("targetFolder"));
			String email = WebTools.converStr(req.getParameter("email"))
					.replace('@', '_');
			String userName = WebTools.converStr(req.getParameter("username"),
					"UTF-8");
			String offline = WebTools.converStr(req.getParameter("offline"),
					"UTF-8");
			String path = WebTools.converStr(req.getParameter("path"), "UTF-8");
			System.out.println("上传文件路径1:" + req.getParameter("path"));
			System.out.println("上传文件路径2:" + path);
			name = WebTools.converStr(req.getParameter("uploadfilename"),
					"UTF-8");
			String savePath = path + "/" + name;
			String updateFiles = WebTools.converStr(req
					.getParameter("updateFiles"), "UTF-8");
			StringTokenizer st = new StringTokenizer(updateFiles, "?");
			ArrayList<String> updateFilesList = new ArrayList<String>();
			while (st.hasMoreElements())
			{
				updateFilesList.clear();
				String filename = st.nextToken();
				if (filename.equals(savePath))
					updateFilesList.add(filename);
			}
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			if (offline != null && offline.length() > 0)
			{
				path = jcrService.createFolders(path);
			}
			ArrayList succResult = new ArrayList();
			String errorResult = "";
			// name =
			// WebTools.converStr(req.getParameter("uploadfilename"),"UTF-8");
			System.out.println("上传的文件1：" + name);
			System.out.println("上传的文件2:"
					+ WebTools.converStr(req.getParameter("uploadfilename"),
							"UTF-8"));
			// byte[] data = null;
			try
			{
				String fPath = req.getSession().getServletContext()
						.getRealPath("data"+File.separatorChar+"uploadfile")
						+ File.separatorChar;

				fPath = fPath
						+ WebTools.converStr(req.getParameter("tempfilename"));
				System.out.println("====================== fpath  " + fPath);
				File file = new File(fPath);
				if (!file.exists())
				{
					System.out.println("====================== fpath 文件不存在");
					return;
				}
				// 文件上传失败，删除临时文件F
				if (WebTools.converStr(req.getParameter("uploadfail"), "UTF-8")
						.equals("uploadfail"))
				{
					System.out
							.println("====================== 文件上传失败：uploadfail");
					file.delete();
					return;
				}
				System.out.println("===================== uploda file "
						+ req.getParameter("uploadfail"));
				String beforeHash = WebTools.converStr(req
						.getParameter("fileHash"), "UTF-8");
				if (beforeHash != null)
				{
					String fileHash = FileMD5.getFileMD5Code(fPath);
					// System.out.println("the befor hash is =================  "+beforeHash
					// + "\n validate hash is ==============  "+fileHash);
					if (!beforeHash.equalsIgnoreCase(fileHash))
					{
						System.out.println("===================  文件上传后MD5验证错误");
						// return;
						// resp.getWriter().print("success");
					}
				}

				FileInputStream in = new FileInputStream(file);
				FileInputStream indata = new FileInputStream(file);
				/*
				 * data = new byte[fis.available()]; fis.read(data);
				 * fis.close(); file.delete(); ByteArrayInputStream fin = new
				 * ByteArrayInputStream(data);
				 */
				boolean isUpdate = false;
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Users userInfo = null;
				if (userName != null)
				{
					userInfo = userService.getUser(userName);
				}
				if (userInfo == null)
				{
					userInfo = (Users) req.getSession().getAttribute("userKey");
				}
				if (userInfo == null)
				{
					// user290  2012-11-02
					userInfo = userService.getUserBySpaceUID(email);
				}
				for (String temp : updateFilesList)
				{
					// int index = temp.indexOf("*");
					// if(temp.substring(0, index).equals(name))
					// {
					isUpdate = true;
					// String filePath = temp.substring(index + 1);
					// jcrService.update(email, filePath, in, in.available(),
					// null, false);
					jcrService.update(email, temp, in, in.available(), null,
							false);

					if (userInfo != null)
					{
						fileSystemService.setFileNewFlagByShareOwner(temp,
								userInfo.getId(), 0);
						// 如果是送审文档，则需要做版本控制
						if (ApprovalUtil.instance().hasApproval(temp))
						{
							jcrService
									.createVersion(
											temp,
											userInfo.getRealName(),
											MainConstants.APPROVAL_VERSION_COMMENT,
											MainConstants.APPROVAL_VERSION_STATUS_CURRENT);
						}
					}
					break;
					// }
				}
				if (!isUpdate)
				{
					System.out.println("新建文件,路径:" + path + "，文件:" + name);
					// Users userInfo =
					// (Users)req.getSession().getAttribute("userKey");
					System.out
							.println("userInfo================   " + userInfo);
					long userId = 0;
					String userName1 = "";	
					if (userInfo != null)
					{
						userId = userInfo.getId();
						userName1 = userInfo.getRealName();
					}
					Fileinfo fileinfo = fileSystemService.createFile(userId,
							userName1, path, name, in, indata, false, null);					
					//===添加serverchangerecords记录======
					Client client=new Client();//调用Client中的程序
					client.addSerChangeRecords(path+'/'+name, name,0);
		           //==================================================//
					String[] paths = {path};
					fileSystemService.shareNewFile(paths, userInfo, fileinfo);
					if (userInfo != null)
					{
						fileSystemService.setFileNewFlagByShareOwner(
								(path + name), userInfo.getId(), 0);
					}

					try
					{
						fileSystemService.addIntoSharePath(path, name);
						/*
						 * String sharePath = isShare(path+"/"+name); if
						 * (sharePath != null) { //如果向上查共享信息有的话，设置新共享信息
						 * List<Personshareinfo> list =
						 * getPersonshareinfo(sharePath); for(int i=0; i<
						 * list.size(); i++) { Personshareinfo shareinfo =
						 * list.get(i); saveNewPersonShareinfo(shareinfo,
						 * path+"/"+name); } }
						 */
					}
					catch (Exception e)
					{

					}
					/*
					 * 增加为写入文件表信息
					 */
					// ShareFileTip queryTip=new ShareFileTip();
					// ArrayList arr = new ArrayList();
					// arr.add(fileinfo);
					// long uid=0l;
					// if (userInfo !=null)
					// {
					// uid = userInfo.getUserId();
					// }
					// queryTip.insertFileinfo(fileinfo,uid);
				}
				succResult.add(name);

				in.close();
				indata.close();
				file.delete();
				if (userInfo != null)
				{
					LogServices logServices = (LogServices)ApplicationContext.getInstance().getBean(LogServices.NAME);
	                logServices.setFileLog(userInfo, "", LogConstant.OPER_TYPE_UPLOAD_FILES, path);
//					FileOperLog fileOperLog = new FileOperLog(name, path,
//							userInfo.getUserName(), userInfo.getRealName(), "",
//							"上传", "");
//					LogsUtility.logToFile(fileOperLog.getUserName(), DateUtils
//							.format(new Date(), "yyyy-MM-dd")
//							+ ".log", true, fileOperLog);
				}
				// LogUtility.log(logDir, fileOperLog);
				// user290  2012-09-12
				MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
				List<Long> userIds = new ArrayList<Long>(0);
				userIds.add(userInfo.getId());
				messageService.sendMessage("CLOUD.handleHash", "userId", "", userInfo.getId().intValue(), userIds);
			}
			catch (Exception e)
			{
				LogsUtility.error(e);
				errorResult += name + ",";
				resp.getWriter().print(
						"{success:false,msg:'"
								+ Constant.UPLOADSERVICE_ERROR_16 + "'}");
				// continue;
			}
			resp.getWriter().print(
					"{success:true,msg:'" + Constant.UPLOADSERVICE_ERROR_17
							+ "'}");
			return;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
			resp.getWriter().print(
					"{success:false,msg:'" + Constant.UPLOADSERVICE_ERROR_16
							+ "'}");
			e.printStackTrace();
		}
		resp.getWriter().print("exception:");
	}

	//
	public static void createFolder(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String path = WebTools.converStr(req.getParameter("path"), "UTF-8");
		String name = WebTools.converStr(req.getParameter("uploadfilename"),
				"UTF-8");
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		try
		{
			Users userInfo = (Users) req.getSession().getAttribute("userKey");
			String realname = userInfo != null ? userInfo.getRealName() : " ";
			// jcrService.initFolder(path);
			jcrService.createFolder(realname, path, name);
			//===添加serverchangerecords记录=====//C++程序调用的接口
			Client client=new Client();//调用Client中的程序
			client.addSerChangeRecords(path+'/'+name, name, 1);
	       //==================================================//
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ????
	public static void getExistFiles(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		// String parentPath =
		// WebTools.converStr(req.getParameter("path"),"UTF-8");
		String parentPath = java.net.URLDecoder.decode(
				req.getParameter("path"), "UTF-8");
		List<String> fileNamesTemp = jcrService.getExistFiles(parentPath);
		List<String> fileNames = new ArrayList<String>();
		if (null != fileNamesTemp && !fileNamesTemp.isEmpty())
		{
			for (String fileName : fileNamesTemp)
			{
				fileName = fileName.substring(0, fileName.indexOf("."))
						+ fileName.substring(fileName.indexOf("."))
								.toLowerCase();
				fileNames.add(fileName);
			}
		}
		String names = fileNames.toString();
		resp.getWriter().println(names);
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static void isFilesExist(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String path = java.net.URLDecoder.decode(req.getParameter("path"),
				"UTF-8");
		Boolean ret = jcrService.isFileExist(path);
		resp.getWriter().println(ret);
	}

	// ????
	public static void loadTree(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		String parentPath = WebTools.converStr(req.getParameter("path"),
				"UTF-8");
		System.out.println("接受的树节点路径:" + req.getParameter("path"));
		System.out.println("接受的文件路径1:" + parentPath);
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		List<Fileinfo> folders = jcrService.getFolders(parentPath);
		StringBuffer treeInfo = new StringBuffer("");
		for (Fileinfo fileInfo : folders)
		{
			treeInfo.append(fileInfo.getPrimalPath()).append("#@#").append(
					fileInfo.isFold()).append("&");
		}
		resp.setContentType("application/x-java-serialized-object ");
		OutputStream outstrm = resp.getOutputStream();
		DataOutputStream objoutputstrm = new DataOutputStream(outstrm);
		objoutputstrm.writeUTF(treeInfo.toString());
		objoutputstrm.flush();
		objoutputstrm.close();
	}

	// ???
	public static void checkSameFiles(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String path = WebTools.converStr(req.getParameter("path"), "UTF-8");
		System.out.println("检查同名文件:" + path);
		try
		{
			Fileinfo fileinfo = jcrService.getFile(null, path);
			resp.setContentType("application/x-java-serialized-object ");
			OutputStream outstrm = resp.getOutputStream();
			DataOutputStream objoutputstrm = new DataOutputStream(outstrm);
			if (null != fileinfo && null != fileinfo.getPathInfo())
			{
				objoutputstrm.writeUTF(fileinfo.getPathInfo());
			}
			else
			{
				objoutputstrm.writeUTF("");
			}
			objoutputstrm.flush();
			objoutputstrm.close();

		}
		catch (RepositoryException e)
		{
			e.printStackTrace();
		}
	}

	// ???
	public static void systeReportExport(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String tempFolder = WebConfig.tempFilePath; // getServletContext().getRealPath("tempfile");
		File foler = new File(tempFolder);
		if (!foler.exists())
		{
			foler.mkdir();
		}
		// 参数
		String fileType = WebTools.converStr(request.getParameter("fileType"));
		Long startDate = Long.parseLong(WebTools.converStr(request
				.getParameter("startDate")));
		Long endDate = Long.parseLong(WebTools.converStr(request
				.getParameter("endDate")));
		String userID = WebTools.converStr(request.getParameter("userID"));
		int cycleType = Integer.parseInt(WebTools.converStr(request
				.getParameter("cycleType")));

		File file = SysMonitor.instance().exportSysReport(userID, startDate,
				endDate, cycleType, tempFolder, fileType);
		if (file == null)
		{
			return;
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ "sysReport." + fileType + "\"");
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		out.close();
		file.delete();
	}

	// ????
	public static void exportLogs(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String searchs = URLDecoder.decode(request.getParameter("searchs"),
				"utf-8");
		String userIDs = null;
		if (null != request.getParameter("userIDs"))
		{
			userIDs = URLDecoder.decode(request.getParameter("userIDs"),
					"utf-8");
		}
		List<String> searchList = new ArrayList<String>();
		List<String> userIDList = null;
		if (searchs != null && !"".equals(searchs))
		{
			String[] searchConditions = searchs.split(",");
			for (String searchCondition : searchConditions)
			{
				searchList.add(searchCondition.trim());
			}
			if ("1".equals(searchList.get(0)))
			{
				searchList.set(0, "访问日志");
			}
			else if ("2".equals(searchList.get(0)))
			{
				searchList.set(0, "文件操作日志");
			}
		}
		if (userIDs != null && !"".equals(userIDs))
		{
			userIDList = new ArrayList<String>();
			String[] userIDArray = userIDs.split(",");
			for (String userID : userIDArray)
			{
				userIDList.add(userID);
			}
		}
		// UserService userService =
		// (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String path = WebConfig.userLogPath; // getServletContext().getRealPath("WEB-INF/log/");
		path = path.replaceAll("\\\\", "/");
		if (!path.endsWith("/"))
		{
			path += "/";
		}
		LogServices logService = (LogServices) ApplicationContext.getInstance()
				.getBean(LogServices.NAME);
		List<String> logList = logService.getSearchLog(path, searchList,
				userIDList);

		StringBuffer logtxt = new StringBuffer();
		for (String logInfo : logList)
		{
			logtxt.append(logInfo).append("\r\n");
		}
		// response.setContentType("application/octet-stream");
		response.setContentType("text/plain;charset=UTF-8");
		response
				.setHeader("Content-Disposition", "attachment;filename=log.txt");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		// response.setHeader("Cache-Control",
		// "must-revalidate,post-check=0,pre-check=0");
		// response.setHeader("Pragma", "public");
		OutputStream out = response.getOutputStream();
		out.write(logtxt.toString().getBytes("utf-8"));
		// out.flush();
		// out.close();
		// response.flushBuffer();
	}

	// 导出用户模板
	@Deprecated
	public static void exportImportUserTemplate(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		char sep = File.separatorChar;
		String tempPath = WebConfig.webContextPath + sep + "static" + sep
				+ "userinfo" + sep + "Member.txt";
		FileInputStream in = new FileInputStream(tempPath);
		byte[] data = org.apache.commons.io.IOUtils.toByteArray(in);
		// downLoad(request, response, data, "Member.eio");
		response.setContentType("application/octet-stream");
		String tempName = URLEncoder.encode("Member.txt", "utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ tempName + "\"");
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		out.close();
		in.close();
	}
	public static void userorgtemplate(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{//下载导入用户的模板
		char sep = File.separatorChar;
		String tempPath = WebConfig.webContextPath + sep + "static" + sep
				+ "userinfo" + sep + "Member.txt";
		FileInputStream in = new FileInputStream(tempPath);
		byte[] data = org.apache.commons.io.IOUtils.toByteArray(in);
		// downLoad(request, response, data, "Member.eio");
		response.setContentType("application/octet-stream");
		String tempName = URLEncoder.encode("Member.txt", "utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ tempName + "\"");
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		out.close();
		in.close();
	}
	// 下载邮件发送的附近。
	public static void sendMailDown(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String tempfilename = WebTools.converStr(request
				.getParameter("sendtempfilename"));
		String fileName = WebTools.converStr(request.getParameter("filename"));
		// fileName = URLEncoder.encode(fileName,
		// "utf-8");//用此编码火狐下下载提示框会显示乱码文件名
		fileName = encodeDownloadName(request.getHeader("User-Agent"), fileName); // new
		// String(fileName.getBytes("gb2312"),
		// "ISO8859_1");
		String tempFolder = WebConfig.sendMailPath;

		File file = new File(tempFolder + File.separatorChar + tempfilename);
		if (file.exists())
		{
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[fis.available()];
			fis.read(data);
			fis.close();

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ fileName + "\"");
			ServletOutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
		}
		else
		{
			// URLEncoder.encode("因过期已被服务器自动删除(文件从创建开始10天内下载有效)", "utf-8")
			response.getWriter().println(
					fileName + "----has been deleted because of invaldate!");
		}
	}

	// 检测打开文件时候，文件的各种状态
	public static void chekOpenFileStatus(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			Users userInfo = (Users) request.getSession().getAttribute(
					"userKey");
			String account = WebTools.converStr(request.getParameter("account"),"utf-8");//调用桌面提供的参数1
			if (userInfo==null && account!=null)
			{
				UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
				userInfo = userService.getUser(account);
			}
			String userID = request.getParameter("userID");
			if (userInfo==null && userID!=null)
			{
				UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
				userInfo = userService.getUser(Long.valueOf(userID));
			}
			String companyId = request.getParameter("companyId");
			String spaceUID = userInfo.getSpaceUID();
			if (null == userID || "".equals(userID))
			{
				if (null != userInfo)
				{
					userID = String.valueOf(userInfo.getId());
				}
			}
			long uid = Long.valueOf(userID);
			// URLDecoder.decode(request.getParameter("path"), "utf-8");//
			String path = WebTools.converStr(request.getParameter("path"));
			String sid=request.getParameter("sid");
			System.out.println("path -== " + path);
			Boolean exit = jcrService.isFileExist(path);
			if (path.indexOf("/")<0)
			{
				response.getWriter().print(Constant.ISREAD);//如果给可写权限，所有保存的地方都要改，临时这样,这种情况，集群是有问题的
				return ;
			}
			int idx = path.indexOf("jcr:system/jcr:versionStorage");
			if (idx > -1)
			{
				response.getWriter().print(Constant.ISREAD);
				return;
			}
			int idx2 = path.indexOf("system_audit_root");
			if (idx2 > -1)
			{
//				response.getWriter().print(Constant.ISWRITE);
				//判断签批的权限
				int permit=SignUtil.instance().getPermit(path, userInfo);
				String ret=isFileOpened("", path);
				if (ret!=null)
				{
					response.getWriter().print("h|"+ret);//是否有别人打开
				}
				else if (permit==2)
				{
					response.getWriter().print("n");//无权限
				}
				else if (permit==1)
				{
					response.getWriter().print(Constant.ISWRITE);
				}
				else
				{
					response.getWriter().print(Constant.ISREAD);
				}
				return;
			}
			int  idx3 = path.indexOf("sign_audit_root");
			if(idx3 > -1)
			{
				int permit = fileSystemService.getPermitOfReviewFile(path, uid);
				if ((permit & Constant.ISWRITE) != 0)
				{
					String ret=isFileOpened("", path);
					if (ret!=null)
					{
						String[] name = ret.split(",");
						if(!(name[0].equals(userInfo.getUserName())))
						{
							response.getWriter().print("o|" + ret + "|" + permit);
							return;						
						}
					}
					if((permit & Constant.ISAMENT) != 0)//修订
					{
						response.getWriter().print("x|"+Constant.ISWRITE);
					}else
					{
						response.getWriter().print(Constant.ISWRITE);
					}
				}
				else
				{
					response.getWriter().print(Constant.ISREAD);
					return;
				}
				return;
			}
			if ((exit == null || !exit))
			{
				response.getWriter().print("d");
				return;
			}
			
			
			
			String sharePath = WebTools.converStr(request
					.getParameter("sharePath"));
			String title = WebTools.converStr(request.getParameter("title"));
			String parentPath = WebTools.converStr(request
					.getParameter("parentPath"));
			DataHolder holder = new DataHolder();
			if (sharePath != null && !sharePath.equals(""))
			{
				holder.setStringData(new String[] { sharePath });
			}
			else
			{
				holder.setStringData(new String[] { path });
			}
			int permit = 0;
			permit |= Constant.ISSHARE;
			permit |= Constant.ISOPEN;
			permit |= Constant.ISWRITE;
			permit |= Constant.ISLOCK;
			permit |= Constant.ISDOWN;

			DataHolder holders = fileSystemService.checkFile1(companyId, spaceUID,
					Long.valueOf(userID), permit, holder, parentPath);

			int permits = holders.getIntData();
			if (path.startsWith(userInfo.getSpaceUID()))//如果是自己的文件强制更新到可编辑状态
			{
				permits = 0;
			}
			if (title == Constant.OTHERSHARE
					&& ((permits & Constant.ISSHARE) == 0))
			{
				response.getWriter().print("e");
				return;
			}
			String[] opened = holders.getStringData();
			if ((permits & Constant.ISSHARE) != 0) // 共享文件
			{
				if (opened != null)//已有可写的人打开文件
				{
					Object[] objs=jcrService.getFileProperty(path);
					response.getWriter()
							.print("o|" + opened[0] + "|" + permits+"|status:nowrite|opentime:"
					+(String)objs[0]+"|pera:"+(String)objs[1]+"|perb:"+(String)objs[2]);//这里增加桌面OFFICE返回参数，其中status对应o
					return;
				}
				if((permits & Constant.ISAMENT) != 0)//修订
				{
					permits &= (Constant.ISAMENT - 1);
					response.getWriter().print("x|"+permits);
					return;
				}
				response.getWriter().print(permits);
				return;
			}
			/*
			 * if ((permits & Constant.ISOPEN) != 0) { if ((permits &
			 * Constant.ISLOCK) == 0) { response.getWriter().print("i"); return;
			 * } response.getWriter().print("o"); return; } if ((permits &
			 * Constant.ISLOCK) != 0) { response.getWriter().print(permits);
			 * return; }
			 */

			PermissionService permissionService = (PermissionService) ApplicationContext
					.getInstance().getBean(PermissionService.NAME);
			if (!permissionService.isShareFile(uid, path)
					&& !path.startsWith("system_audit_root")) // 共享文件先单独处理，后续再修改为同一判断。
			{
				Long p = permissionService.getFileSystemAction(uid, path, true);
				if (p == 0 || !FlagUtility.isValue(p, FileSystemCons.READ_FLAG)) // 无打开权限
				{
					response.getWriter().print("n");
					return;
				}
				permits |= (p & FileSystemCons.WRITE_SET);
			}
			if (opened != null)
			{
				response.getWriter().print("o|" + opened[0] + "|" + permits);
				return;
			}
			
			//判断有没有采编权限
			if ("caibian".equals(WebConfig.projectname))
			{
				//有采编权限，但要判断是否在报送表中
        		if (fileSystemService.isBSFile(path))//是报送文件
        		{
					long per = userInfo != null ? permissionService.getSystemPermission(userInfo.getId()) : 0;
		        	if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))//判断是否有采编权限
		    		{
//	        			response.getWriter().print(Constant.ISREAD);
	        			response.getWriter().print("x|"+Constant.ISWRITE);
						return;
	        		}
	    		}
			}
			
			response.getWriter().print(permits);
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ???
	public static void deleteTempFile(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String fPath = WebConfig.tempFilePath + File.separatorChar
				+ WebTools.converStr(request.getParameter("tempfilename"));
		File file = new File(fPath);
		if (file.exists())
		{
			file.delete();
		}
	}

	// ????
	public static void deleteMerge(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String tempFolder = WebConfig.webContextPath + File.separatorChar
				+ "mergerfile";
		File file = new File(tempFolder);
		if (!file.exists())
		{
			file.mkdir();
		}

		try
		{
			String s = request.getParameter("path");

			String[] args = s.split(",");
			// String templateFile = args[0];
			int len = args.length;
			String[] array = null;
			if (len > 0)
			{
				array = new String[len - 1];
				for (int i = 1; i < len; i++)
				{
					array[i - 1] = args[i];
				}
			}
			// String httpUrl = request.getScheme() + "://" +
			// request.getServerName() + ":"
			// + request.getServerPort() + request.getContextPath() +
			// "/mergerfile/";
			// httpUrl = com.share.QueryDb.getIpName(httpUrl);
			int ss = array.length;
			for (int i = 0; i < ss; i++)
			{
				String temp = array[i];
				System.out.println("temp   --- " + temp);
				// File f = new File(temp);
				// if (f.exists())
				// {
				// System.out.println("121212212");
				// f.delete();
				// }
				String fPath = tempFolder // request.getSession().getServletContext().getRealPath("mergerfile")
						+ File.separatorChar
						+ temp.substring(temp.lastIndexOf('/') + 1);
				System.out.println("fPath   --- " + fPath);
				File f = new File(fPath);
				if (f.exists())
				{
					f.delete();
				}
			}
			response.getWriter().print("pass");
		}
		catch (Exception e)
		{
			response.getWriter().print("");
		}
	}

	// 下载合并的模板和区分周报合并和会议合并类型 DOWN_MERGE_ACTION = "downmerger"
	public static String downloadMerge(HttpServletRequest request,HttpServletResponse response,HashMap<String, Object> jsonParams,Users user) throws ServletException, IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String tempFolder = WebConfig.webContextPath + File.separatorChar + "mergerfile";
		try
		{
			String type = (String) param.get("type"); // 合并类型
			String pathList = (String)param.get("path");
			//String tt = request.getParameter("path");
			//System.out.println("ttt ==ttttt= " + tt);
			//String pathList = WebTools.converStr(tt);// URLDecoder.decode(tt,"GBK");
			System.out.println("1212121 ==pathList= " + pathList);
			// pathList = pathList.substring(1, pathList.length()-1);
			String[] args = pathList.split(",");

			String templateFile = request.getContextPath()
					+ "/templateFile/template.doc";
			// templateFile =
			// com.evermore.ext.share.QueryDb.getIpName(templateFile);

			String temp = templateFile;
			System.out.println("temp === " + temp);
			String httpUrl = request.getContextPath() + "/mergerfile/";//这里有反向代理时可能有问题
			// httpUrl = com.evermore.ext.share.QueryDb.getIpName(httpUrl);

			int size = args.length;
			if (size > 0)
			{
				temp = temp.concat(",");
			}
			temp="";//模板就不要传了
			// 生成zip文件
			/*
			 * String aa = System.currentTimeMillis()+ ".zip"; String zipPath =
			 * tempFolder+File.separator+aa;
			 * 
			 * FileOutputStream f = new FileOutputStream(zipPath);
			 * ZipOutputStream out = new ZipOutputStream(new
			 * DataOutputStream(f)); for(int i = 0; i < size; i ++) { String
			 * realPath = args[i].trim(); String name =
			 * System.currentTimeMillis() +
			 * realPath.substring(realPath.lastIndexOf
			 * ('.'));//realPath.substring(realPath.lastIndexOf('/')+1);
			 * out.putNextEntry(new ZipEntry(name));
			 * 
			 * InputStream in = jcrService.getContent(email.replace('@', '_'),
			 * realPath, false); byte[] con = readStream(in, 8192);
			 * out.write(con);
			 * 
			 * in.close(); }
			 * 
			 * out.close(); temp = temp.concat(httpUrl).concat(aa).concat(",");
			 */
			// ---------end

			for (int i = 0; i < size; i++)
			{
				String realPath = args[i].trim();
//				String name = System.currentTimeMillis()
//						+ realPath.substring(realPath.lastIndexOf('.'));// realPath.substring(realPath.lastIndexOf('/')+1);
//				System.out.println("name === " + name);
//				// realPath = URLDecoder.decode(realPath, "UTF-8");
//				System.out.println("realPath ==2222= " + realPath);
//
//				name = getFileFromRepository(realPath, tempFolder, name, false,
//						"", "");

//				temp = temp.concat(httpUrl).concat(name);
				temp = temp.concat(realPath);
				if (i != (size - 1))
				{
					temp = temp.concat("@");//用@符号间隔
				}
			}
			// System.out.println("temp =00= "+temp);
			// temp = URLEncoder.encode(temp,"UTF-8");
			System.out.println("temp =11= " + temp);
			//response.getWriter().print(temp);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, temp);
		}
		catch (Exception e)
		{
			//response.getWriter().print("");
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR,"");
		}
		return error;
	}

	// ???
	public static void saveEncypt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String path = WebTools.converStr(request.getParameter("path"));

		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		jcrService.setNodeStatus(path, "1", FileConstants.ISENTRYPT);
	}

	// ????
	public static void saveDecypt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String path = WebTools.converStr(request.getParameter("path"));

		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		jcrService.setNodeStatus(path, "0", FileConstants.ISENTRYPT);
	}

	// 为移动临时加的获得用户指定路径下的文件及文件夹
	/**
	 * @deprecated
	 */
	@Deprecated
	public static void getFileList(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, Exception
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String uid = WebTools.converStr(request.getParameter("userId"));
		String path = WebTools.converStr(request.getParameter("path"));
		StringBuffer fileName = new StringBuffer();
		if (path == null || path.length() < 1 || path.equals("/"))
		{
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(uid);
			if (user != null)
			{
				List<Spaces> listS = fileService.getGroupSpacesByUserId(user
						.getId());
				List smplFileList = new ArrayList<SimpleFileinfo>();

				fileName.append("fileName:");
				fileName.append(MainConstants.PERSON_DOCUMENT);
				fileName.append(",");
				fileName.append("folder:");
				fileName.append("true");
				fileName.append(",");
				fileName.append("path:");
				fileName.append(user.getSpaceUID() + "/" + FileConstants.DOC);

				for (Spaces temp : listS)
				{
					fileName.append(";fileName:");
					fileName.append(temp.getName()
							+ MainConstants.PROJECT_DOCUMENT);
					fileName.append(",");
					fileName.append("folder:");
					fileName.append("true");
					fileName.append(",");
					fileName.append("path:");
					fileName.append(temp.getSpaceUID() + "/"
							+ FileConstants.DOC);
				}
			}
		}
		else
		{
			List list = jcrService.listPageFileinfos("", path, 0, 1000);
			if (list == null || list.size() == 0)
			{
				return;
			}
			list.remove(0);
			boolean flag = false;
			Fileinfo file;
			for (Object file1 : list)
			{
				file = (Fileinfo) file1;
				if (flag)
				{
					fileName.append(";");
				}
				else
				{
					flag = true;
				}
				fileName.append("fileName:");
				fileName.append(file.getFileName());
				fileName.append(",");
				fileName.append("folder:");
				fileName.append(file.isFold());
				fileName.append(",");
				fileName.append("path:");
				fileName.append(file.getPathInfo());
			}
		}
		byte[] downURL = fileName.toString().getBytes("UTF-8");
		DataOutputStream out = new DataOutputStream(response.getOutputStream());
		byte[] temp = new byte[4];
		out.write(convert(downURL.length, temp));
		out.write(downURL);
		out.flush();
		out.close();
	}

	// 为移动临时加的获得用户指定路径下的审批文件
	public static void getApprovalFileList(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		String uid = WebTools.converStr(request.getParameter("userId"));
		String groupId = WebTools.converStr(request.getParameter("groupId"));
		StringBuffer fileName = new StringBuffer();
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(uid);
		long groupID = Long.valueOf(groupId);/*
											 * userService.getGroupIdBySpaceUID(
											 * spaceUID)
											 */
		if (user != null)
		{
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			DataHolder dataHolder = ApprovalUtil.instance()
					.getApprovalFileList_Task(jcrService, "", "", 0, 100,
							user.getId(), groupID);
			ArrayList<Object> filesData = dataHolder.getFilesData();
			int size = filesData != null ? filesData.size() : 0;
			Fileinfo fileinfo;
			for (int i = 0; i < size; i++)
			{
				fileinfo = (Fileinfo) filesData.get(i);

				fileName.append("fileName:");
				fileName.append(fileinfo.getFileName());
				fileName.append(",");
				fileName.append("path:");
				fileName.append(fileinfo.getPathInfo());
				fileName.append(",");
				fileName.append("permit:");
				fileName.append(fileinfo.getPermit());
				fileName.append(",");
				fileName.append("modifiedTime:");
				if (fileinfo.getLastedTime() != null)
				{
					fileName.append(WebofficeUtility.getFormateDate(fileinfo
							.getLastedTime(), "-"));
				}
				else
				{
					fileName.append("");
				}
				fileName.append(",");
				fileName.append("status:");
				fileName.append(fileinfo.getApprovalStatus());
				fileName.append(",");
				fileName.append("size:");
				fileName.append(fileinfo.getFileSize());
				if (i < size - 1)
				{
					fileName.append(";");
				}
			}
			if (fileName.length() > 0)
			{
				byte[] downURL = fileName.toString().getBytes("UTF-8");
				DataOutputStream out = new DataOutputStream(response
						.getOutputStream());
				byte[] temp = new byte[4];
				out.write(convert(downURL.length, temp));
				out.write(downURL);
				out.flush();
				out.close();
			}
		}
	}

	// 为移动临时加的获得到用户有审批任务的所有空间名
	public static void getApprovalSpaceName(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String uid = WebTools.converStr(request.getParameter("userId"));
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		FileSystemService fileService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		Users user = userService.getUser(uid);
		long spaceId = 0L;
		StringBuffer spaceName = new StringBuffer();
		if (user != null)
		{
			long userId = user.getId().longValue();
			List<Groups> list = userService.getGroupsByUsers(userId);
			if (list != null && list.size() > 0)
			{
				int size = list.size();
				Spaces sp;
				for (Groups group : list)
				{
					if (ApprovalUtil.instance().hasApproval(userId,
							group.getId()))
					{
						sp = fileService.getSpace(group.getSpaceUID());
						spaceName.append("groupId:");
						spaceName.append(group.getId());
						spaceName.append(",");
						spaceName.append("spaceName:");
						spaceName.append(sp.getName() + ";");
					}
				}
			}
		}
		if (spaceName.length() > 0)
		{
			String spaceNameStr = spaceName.toString();
			if (spaceNameStr.endsWith(";"))
			{
				spaceNameStr = spaceNameStr.substring(0,
						spaceNameStr.length() - 1);
			}
			byte[] downURL = spaceNameStr.getBytes("UTF-8");
			DataOutputStream out = new DataOutputStream(response
					.getOutputStream());
			byte[] temp = new byte[4];
			out.write(convert(downURL.length, temp));
			out.write(downURL);
			out.flush();
			out.close();
		}
	}

	public static void processApproval(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String uid = WebTools.converStr(request.getParameter("userId"));
		String path = WebTools.converStr(request.getParameter("path"));
		String comment = WebTools.converStr(request.getParameter("comment"));
		String type = WebTools.converStr(request.getParameter("type")); // 0核准,
		// 其他值为拒绝
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(uid);
		if (user != null && path != null)
		{
			ApprovalBean infoBean = ApprovalUtil.instance().getApproval(
					user.getId(), path);
			if (infoBean != null)
			{
				infoBean.setTaskComment(comment);
				infoBean
						.setTaskAction(type.equals("0") ? MainConstant.APPROVAL_ACTION_PASS
								: MainConstant.APPROVAL_ACTION_REJECT);
				ApprovalUtil.instance().processApproval(user.getId(), infoBean,
						false);
			}
		}
	}

	//
	public static void downloadFiles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		System.out
				.println("download file ============================  begin============= ");
		response.reset();
		// String companyID =
		// WebTools.converStr(request.getParameter("companyID"));
		String filePath = WebTools.converStr(request.getParameter("filePath"));
		String fileName = WebTools.converStr(request.getParameter("fileName"));
		String userName = WebTools.converStr(request.getParameter("username"));
		String email = WebTools.converStr(request.getParameter("email"));
		String isZip = WebTools.converStr(request.getParameter("zipFile"));
		String copyurl = WebTools.converStr(request.getParameter(ServletConst.COPYURL));
		try
		{
			if (isZip != null && isZip.equals("true"))
			{
				zipDownload(request, response, filePath, email);
				return;
			}
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			if (copyurl != null && copyurl.length() > 5)
			{
				filePath = fileSystemService.getCopyPaths(copyurl);
				if (filePath == null)
				{
					response.getWriter().print("1");
					return;
				}
				int nameindex = filePath.lastIndexOf("/");
				fileName = filePath.substring(nameindex + 1);
			}
			long time = System.currentTimeMillis();
			int index = fileName.lastIndexOf('.');
			String mime = fileName.substring(index);
			String newName = String.valueOf(time) + mime;
			newName = getFileFromRepository(filePath, WebConfig.tempFilePath,
					newName, false, "", "");
			String httpUrl = request.getContextPath() + "/"
					+ WebConfig.TEMPFILE_FOLDER + "/" + newName;
			byte[] downURL = httpUrl.getBytes("UTF-8");
			DataOutputStream out = new DataOutputStream(response
					.getOutputStream());
			byte[] temp = new byte[4];
			out.write(convert(downURL.length, temp));
			out.write(downURL);
			out.flush();
			out.close();

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users userInfo = null;
			LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
			if (userName != null)
			{
				userInfo = userService.getUser(userName);
			}
			if (userInfo == null)
			{
				userInfo = (Users) request.getSession().getAttribute("userKey");
			}
			if (userInfo != null)
			{
				fileSystemService.setFileNewFlagByShareUser(filePath, userInfo.getId(), 1);
				fileSystemService.setFileNewFlagByMember(filePath, userInfo.getId(), 1);
				logService.setFileLog(userInfo, "", LogConstant.OPER_TYPE_DOWNLOAD_FILES, filePath);
//				FileOperLog fileOperLog = new FileOperLog("", filePath,
//						userInfo.getUserName(), userInfo.getRealName(), "",
//						"下载", "");
//				LogsUtility.logToFile(fileOperLog.getUserName(), DateUtils
//						.format(new Date(), "yyyy-MM-dd")
//						+ ".log", true, fileOperLog);
			}
			else
			{
				userInfo = userService.getUser("admin");
				Thread receiveT = new Thread(new InsertFileLog(
						new String[] { filePath }, userInfo.getId(), 16, null,
						null));
				receiveT.start();
			}
		}
		catch (Exception e)
		{
			if (e.getCause() instanceof PathNotFoundException)
			{
				response.getWriter().print("1");
			}
			System.out
					.println("download file ============================  error ============= ");
		}
		System.out
				.println("download file ============================  end  ============= ");

	}

	/**
	 * 移动端复制文件
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.COPY_FILE_ACTION)
	public static String copyfile(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error = null;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			long uid = userService.getUser(userName).getId();
			String paths = (String) param.get("paths");// 选择的文件或目录路径，如多个，中间用分号间隔;
			String targetpath = (String) param.get("targetpath");// 目标文件夹
			String[] srcpaths = paths.split(";");
			PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			if (srcpaths != null && srcpaths.length > 0)
			{
				Users user = userService.getUser(uid);
				if (targetpath != null && targetpath.length() > 0)
				{
					Long permit = service.getFileSystemAction(user.getId(), targetpath, true);
					long pd = FileSystemCons.COPY_PASTE_FLAG;
					boolean flag = permit == null || permit == 0 ? false
							: FlagUtility.isValue(permit, pd);
					if (flag)
					{
						final DataHolder srcPathHolder = new DataHolder();
						srcPathHolder.setStringData(srcpaths);
						// 先判断目标文件夹有没有权限，再进行复制或移动
						// 目标文件夹中有没有重名的
						Fileinfo[] fileinfos = fileService.getFileList(null, String.valueOf(uid),	targetpath);
						final List<String> exitesNames = new ArrayList<String>();
						int size = fileinfos.length;
						if (size > 0)
						{
							final String[] newNames = new String[size];
							for (int i = 0; i < size; i++)
							{
								newNames[i] = fileinfos[i].getFileName();// ((Fileinfo)objFiles.get(i)).getFileName();
								exitesNames.add(newNames[i]);
							}
						}
						fileService.copyFiles(user.getId(), srcPathHolder, targetpath, 1, exitesNames);
						// response.getWriter().write("true");
						error = "true";
					}
					else
					{
						// response.getWriter().write("notpermission");// 没有权限
						error = "notpermission";
					}
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// response.getWriter().write("false");// 复制失败
			error = "false";
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, error);
		//response
		//		.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//response.getWriter().write(result);
	}

	/**
	 * 移动端移动文件
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.MOVE_FILE_ACTION)
	public static String movefile(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String errror = null;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long uid = userService.getUser(userName).getId();
			String paths = (String) param.get("paths");// 选择的文件或目录路径，如多个，中间用分号间隔;
			String targetpath = (String) param.get("targetpath");// 目标文件夹
			String filetype = (String) param.get("filetype");// 公共空间或我的空间类别
			String[] srcpaths = paths.split(";");
			PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			if (srcpaths != null && srcpaths.length > 0)
			{
				Users user = userService.getUser(uid);
				if (targetpath != null && targetpath.length() > 0)
				{
					Long permit = service.getFileSystemAction(user.getId(), targetpath, true);
					long pd = FileSystemCons.COPY_PASTE_FLAG;
					boolean flag = permit == null || permit == 0 ? false
							: FlagUtility.isValue(permit, pd);
					if (flag)
					{
						final DataHolder srcPathHolder = new DataHolder();
						srcPathHolder.setStringData(srcpaths);
						// 先判断目标文件夹有没有权限，再进行复制或移动
						// 目标文件夹中有没有重名的
						Fileinfo[] fileinfos = fileService.getFileList(null, String.valueOf(uid), targetpath);
						final List<String> exitesNames = new ArrayList<String>();
						int size = fileinfos.length;
						if (size > 0)
						{
							final String[] newNames = new String[size];
							for (int i = 0; i < size; i++)
							{
								newNames[i] = fileinfos[i].getFileName();// ((Fileinfo)objFiles.get(i)).getFileName();
								exitesNames.add(newNames[i]);
							}
						}
						// Constant.DOC_PUBLIC.equals(filetype)//公共空间
						fileService.moveFiles(filetype, user.getId(), srcPathHolder, targetpath,1);
						// response.getWriter().write("true");
						errror = "true";

					}
					else
					{
						// response.getWriter().write("notpermission");// 没有权限
						errror = "notpermission";
					}
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// response.getWriter().write("false");// 移动失败
			errror = "false";
		}
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, errror);
		//response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//response.getWriter().write(result);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static byte[] convert(int a, byte[] b)
	{
		b[0] = (byte) (a & 0xFF);
		b[1] = (byte) ((a >> 8) & 0xFF);
		b[2] = (byte) ((a >> 16) & 0xFF);
		b[3] = (byte) ((a >> 24) & 0xFF);
		return b;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param data
	 * @param fileName
	 * @throws Exception
	 */
	private static void zipDownload(HttpServletRequest request,
			HttpServletResponse response, String filePath, String userID)
			throws Exception
	{
		StringTokenizer st = new StringTokenizer(filePath, "|");
		if (st.countTokens() > 0)
		{
			File userFolder = null;
			ArrayList<File> files = new ArrayList<File>();
			try
			{
				String str = WebConfig.tempFilePath; // getServletContext().getRealPath("tempfile");
				String zipFolder = str;
				str += File.separatorChar + userID;
				String path;
				String name;

				while (st.hasMoreTokens())
				{
					path = st.nextToken();
					name = path.substring(path.lastIndexOf("/") + 1);
					name = getFileFromRepository(path, str, name, false, "", "");
					File userFile = new File(str + File.separatorChar + name);
					files.add(userFile);
				}

				String os = WebTools
						.converStr(request.getParameter("clientos"));
				String code = WebTools.converStr(request.getParameter("code"));
				boolean isWin = os.equals("true");
				// 生成压缩文件
				String fileName = System.currentTimeMillis() + ".zip";
				File zipFile = new File(zipFolder + File.separatorChar
						+ fileName);
				ZipOutputStream zipOut = new ZipOutputStream(
						new FileOutputStream(zipFile));
				zipOut.setEncoding(code);
				zipFile(zipOut, userFolder, "");
				zipOut.close();

				String httpUrl = request.getContextPath() + "/"
						+ WebConfig.TEMPFILE_FOLDER + "/" + fileName;
				byte[] downURL = httpUrl.getBytes("UTF-8");
				DataOutputStream out = new DataOutputStream(response
						.getOutputStream());
				byte[] temp = new byte[4];
				out.write(convert(downURL.length, temp));
				out.write(downURL);
				out.flush();
				out.close();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				int size = files.size();
				for (int i = 0; i < size; i++)
				{
					files.get(i).delete();
				}
				if (userFolder != null)
				{
					userFolder.delete();
				}
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param sourFile
	 * @param base
	 */
	private static void zipFile(ZipOutputStream out, File sourFile, String base)
	{
		try
		{
			if (sourFile.isDirectory())
			{
				ZipEntry entry = new ZipEntry(base + "/");
				entry.setUnixMode(755);
				out.putNextEntry(entry);

				File[] subFile = sourFile.listFiles();
				base = base.length() > 0 ? base + "/" : "";
				for (File tempFile : subFile)
				{
					zipFile(out, tempFile, base + tempFile.getName());
				}
			}
			else
			{
				ZipEntry entry = new ZipEntry(base);
				entry.setUnixMode(644);
				out.putNextEntry(entry);
				FileInputStream in = new FileInputStream(sourFile);
				byte[] buffer = new byte[BYTE_SIZE];
				int size;
				while ((size = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, size);
				}
				in.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{

		try
		{
			InputStream in = request.getInputStream();
			byte[] temp = new byte[4];
			// 上传的类型

			in.read(temp);
			int type = convert(temp);
			// 文件名长度
			in.read(temp);
			int nameLen = convert(temp);
			// 文件名

			byte[] name = new byte[nameLen];
			in.read(name);
			// 删除文件
			if (type == 3)
			{
				String fPath = WebConfig.tempFilePath + File.separatorChar;
				fPath = fPath + new String(name, "UTF-8");
				File file = new File(fPath);
				if (file.exists())
				{
					file.delete();
				}
				return;
			}
			// 块开始位置

			in.read(temp);
			int offset = convert(temp);
			// 块长度

			in.read(temp);
			int length = convert(temp);

			String fPath = WebConfig.webContextPath;
			if (fPath.endsWith(""+File.separatorChar))
			{
				fPath+="data"+File.separatorChar+"uploadfile";
			}
			else
			{
				fPath+=File.separatorChar
						+ "data"+File.separatorChar+"uploadfile";
			}
			File file = new File(fPath);
			if (!file.exists())
			{
				file.mkdir();
			}
			fPath += File.separatorChar;
			fPath = fPath + new String(name, "UTF-8");
			file = new File(fPath);
			System.out.println("uploadFile path=" + fPath);
			System.out.println("uploadFile name length=" + nameLen);
			System.out.println("uploadFile offset=" + offset);
			System.out.println("uploadFile 块长度=" + length);
			/*
			 * System.out.println("The naeme is  " + fil + "   " + nameL +
			 * "   offset=" + offset + " length=" + length + " file=" +
			 * fil.length() + "  " + this);
			 */
			OutputStream reout = response.getOutputStream();
			reout.write(6);
			RandomAccessFile out = new RandomAccessFile(file, "rw");
			out.seek(offset);
			byte[] b = new byte[length];
			int a = 0;
			while ((a = in.read(b)) > 0)
			{
				out.write(b, 0, a);
			}
			// System.out.println("------------  " + offset + "   " +
			// fil.length() + "  " + this);
			System.out.println("uploadFile size=" + file.length());
			reout.write(5);
			reout.close();
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public static String uploadPicFile(HttpServletRequest request,
			HttpServletResponse response,String name) throws ServletException, IOException, FileUploadException
	{
		//文件保存目录路径
		String savePath = request.getSession().getServletContext().getRealPath("/") + "attached/";

		//文件保存目录URL
		String saveUrl  = request.getContextPath() + "/attached/";

		//定义允许上传的文件扩展名
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

		//最大文件大小
		long maxSize = 1000000;

		response.setContentType("text/html; charset=UTF-8");

		if(!ServletFileUpload.isMultipartContent(request)){
			return null;
		}
		//检查目录
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			return null;
		}
		//检查目录写权限
		if(!uploadDir.canWrite()){
			return null;
		}

		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}
		if(!extMap.containsKey(dirName)){
			return null;
		}
		//创建文件夹
		savePath += dirName + "/";
		saveUrl += dirName + "/";
		File saveDirFile = new File(savePath);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "/";
		saveUrl += ymd + "/";
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			long fileSize = item.getSize();
			if (!item.isFormField()) {
				//检查文件大小
				if(item.getSize() > maxSize){
					return null;
				}
				//检查扩展名
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
//					out.write(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。").getBytes());
//					return;
					return null;
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
				try{
					File uploadedFile = new File(savePath, newFileName);
					item.write(uploadedFile);
				}catch(Exception e){
//					out.write(getError("上传文件失败。").getBytes());
//					return;
				}
				
			    	JSONObject obj = new JSONObject();
					obj.put("error", 0);
					obj.put("url", saveUrl + newFileName);
					response.getWriter().write(JSONTools.convertObjectToString(obj));
			}
		}
		return null;
		
	}
	private static String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toString();
	}
	/**
	 * 
	 * @param b
	 * @return
	 */
	private static int convert(byte[] b)
	{
		int ret = 0;
		ret = (b[0] & 0xFF) | ((b[1] << 8) & 0xFF00)
				| ((b[2] << 16) & 0xFF0000) | ((b[3] << 24) & 0xFF000000);
		return ret;
	}

	private static void getRootFiles(HttpServletRequest req,
			HttpServletResponse res)
	{
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		if (userInfo != null)
		{
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			long userId = userInfo.getId();
			List<Spaces> list = fileService.getGroupSpacesByUserId(userId);
			List smplFileList = new ArrayList<SimpleFileinfo>();

			SimpleFileinfo smplFileinfo = new SimpleFileinfo();
			smplFileinfo.setFileName(MainConstants.PERSON_DOCUMENT);
			smplFileinfo.setFolder(true);
			smplFileinfo.setShowPath(FileConstants.DOC);
			smplFileinfo.setRealPath(userInfo.getSpaceUID() + "/"
					+ FileConstants.DOC);
			smplFileList.add(smplFileinfo);

			for (Spaces temp : list)
			{
				smplFileinfo = new SimpleFileinfo();
				smplFileinfo.setFileName(temp.getName()
						+ MainConstants.PROJECT_DOCUMENT);
				smplFileinfo.setFolder(true);
				smplFileinfo.setShowPath(FileConstants.DOC);
				smplFileinfo.setRealPath(temp.getSpaceUID() + "/"
						+ FileConstants.DOC);
				smplFileList.add(smplFileinfo);
			}
			// 还需要列出组织空间的地址
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(res
						.getOutputStream());
				oos.writeObject(smplFileList);
				oos.flush();
				oos.close();
			}
			catch (Exception e)
			{
				LogsUtility.error(e);
			}
		}
	}

	// 为公告等临时增加的一个方法
	public static void getPermission(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String account = WebTools.converStr(req.getParameter("account"),
				"utf-8");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users owner = userService.getUser(account);
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
		Long permission = service.getSystemPermission(owner.getId());
		resp.getWriter().print(permission);
		/*
		 * String ret = ""; if (FlagUtility.isValue(permission,
		 * ManagementCons.COMPANY_NEWS)) { ret += "公司动态"; } if
		 * (FlagUtility.isValue(permission, ManagementCons.OUT_NEWS)) { ret +=
		 * "行业资讯"; } if (FlagUtility.isValue(permission,
		 * ManagementCons.BULLINTS)) { ret += "公告中心"; } if
		 * (FlagUtility.isValue(permission, ManagementCons.GROUPS_NEWS)) {
		 * 
		 * ret += "团队建设"; } if (FlagUtility.isValue(permission,
		 * ManagementCons.LABOUR_NEWS)) { ret += "工会服务"; }
		 */

	}

	private static boolean hasPermission(String path, Users user, long target)
	{
	    if (path.startsWith("我的报表") || path.startsWith("myReport"))
        {
            return true;
        }
		if (user == null) 
		{
			return false;
		}
		if (path.indexOf(FileConstants.SIGN_ROOT) != -1)
		{
			boolean isRead = FlagUtility.isValue(FileSystemCons.READ_SET,target);
			if(isRead)
			{
				return true;
			}
			if(path.indexOf(user.getSpaceUID()) != -1)
			{
				return false;
			}else
			{
				return true;				
			}
		}
		if (path.indexOf(user.getSpaceUID()) != -1) // 个人空间或审批目录中的送审文档
		{
			return true;
		}
		//判断有没有采编权限
		if ("caibian".equals(WebConfig.projectname))
		{
			PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
        	if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))//判断是否有采编权限
    		{
        		return true;
    		}
		}
		boolean auditFlag = path.startsWith(FileConstants.AUDIT_ROOT)
				|| path.startsWith("jcr:system");
		if (!auditFlag && !path.startsWith(FileConstants.TEAM_ROOT)
				 && !path.startsWith(FileConstants.COMPANY_ROOT)
				) // 可能是共享共享的，临时这样处理
		{
			long p = hasShare(user.getId(), path, null, null);
			return FlagUtility.isValue(p, target);
		}

		PermissionService service = (PermissionService) ApplicationContext.getInstance().getBean("permissionService");
		Long permission;
		if (auditFlag) // 审批的文档，目前只有审批者及管理者有权限
		{
			permission = service.getSystemPermission(user.getId());
			boolean flag = FlagUtility.isValue(permission,
					ManagementCons.AUDIT_AUDIT_FLAG)
					|| FlagUtility.isValue(permission,
							ManagementCons.AUDIT_MANGE_FLAG)
					|| FlagUtility.isValue(permission,
							ManagementCons.AUDIT_SEND_FLAG);
			if (path.startsWith("jcr:system"))
			{
				boolean tempback = FlagUtility.isValue(FileSystemCons.READ_SET,
						target);
				return tempback;
			} 
			if (flag)
			{
				return FlagUtility.isValue(FileSystemCons.WRITE_SET, target);
			}
			else
			{
				return FlagUtility.isValue(FileSystemCons.READ_SET, target);
			}
		}

		permission = service.getFileSystemAction(user.getId(), path, true);
		if (permission == null)
		{
			return false;
		}
		return FlagUtility.isValue(permission, target);

	}

	// 共享的混乱做法，原有的方法分离处理，后续统一处理
	private static long hasShare(long userID, String path, String parentPath,
			String sharePath)
	{
		int permit = 0;
		permit |= Constant.ISSHARE; 
		permit |= Constant.ISOPEN;
		permit |= Constant.ISWRITE;
		permit |= Constant.ISLOCK;
		permit |= Constant.ISDOWN;

		DataHolder holder = new DataHolder();
		if (sharePath != null && !sharePath.equals(""))
		{
			holder.setStringData(new String[] { sharePath });
		}
		else
		{
			holder.setStringData(new String[] { path });
		}

		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		try
		{
			DataHolder holders = fileSystemService.checkFile1("public", "",
					Long.valueOf(userID), permit, holder, parentPath);

			int permits = holders.getIntData();
			if (((permits & Constant.ISSHARE) == 0))
			{
				return 0;
			}
			String[] opened = holders.getStringData();
			if ((permits & Constant.ISSHARE) != 0) // 共享文件
			{
				if (opened != null)
				{
					return FileSystemCons.READ_FLAG;
				} 
				return permits;
			}
		}
		catch (Exception e)
		{
		}
		return 0;
	}

	public static void getAllFileList(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String caId = req.getParameter("parseFiles");
		caId = URLDecoder.decode(caId, "UTF-8");
		// caId = caId.replace(',', '/');
		String[] filePaths = caId.split(";");
		String userIdStr = req.getParameter("userId");

		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		String[] resultPaths = userService.getAllFileinfo(Long
				.valueOf(userIdStr), filePaths);
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = resultPaths.length; i < len; i++)
		{
			sb.append(resultPaths[i]/* .replace('/', ',') */);
			if (i != len - 1)
			{
				sb.append(";");
			}
		}
		// resp.getWriter().print(URLEncoder.encode(sb.toString(), "UTF-8"));
		String filePath = sb.toString();
		// System.out.println("UploadServiceImpl.getAllFiles = " + filePath);
		byte[] downURL = URLEncoder.encode(filePath, "UTF-8").getBytes();// filePath.getBytes("UTF-8");
		DataOutputStream out = new DataOutputStream(resp.getOutputStream());
		byte[] temp = new byte[4];
		out.write(convert(downURL.length, temp));
		out.write(downURL);
		out.flush();
		out.close();
	}

	public static void getAllSpaceByUserID(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String tempId = req.getParameter("userId");
		Long userid = new Long(tempId);
		final StringBuffer mappings = new StringBuffer();

		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		List<Spaces> sps = fileSystemService.getGroupSpacesByUserId(userid);
		if (sps != null && sps.size() > 0)
		{
			for (int i = 0, size = sps.size(); i < size; i++)
			{
				if (sps.get(i) != null)
				{
					mappings.append(sps.get(i).getSpaceUID());
					mappings.append(";");
					mappings.append(sps.get(i).getName());
					mappings.append(";");
					mappings.append(sps.get(i).getSpaceUID() + "/Document");
					mappings.append(";");
					mappings.append("\u6587\u6863\u4E2D\u5FC3");
					if (i != size - 1)
					{
						mappings.append(";");
					}
				}
			}
		}

		String str = mappings.toString();

		// System.out.println("mappings str = " + str);
		byte[] downURL = URLEncoder.encode(str, "UTF-8").getBytes();// filePath.getBytes("UTF-8");
		DataOutputStream out = new DataOutputStream(resp.getOutputStream());
		byte[] temp = new byte[4];
		out.write(convert(downURL.length, temp));
		out.write(downURL);
		out.flush();
		out.close();
	}

	/**
	 * 直接把文件上传到文件库中
	 * 
	 * @param req
	 * @param res
	 * @deprecated
	 */
	@Deprecated
	public static Fileinfo uploadFileToRepository(HttpServletRequest req,
			HttpServletResponse res)
	{
		try
		{
			String path = WebTools.converStr(req
					.getParameter(Constants.FilePath));
			String fileName = WebTools.converStr(req
					.getParameter(Constants.FileName));
			fileName = normalName(fileName);

			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			String tempName = System.currentTimeMillis() + fileName;
			if (tempName.length() > 30) // 避免长文件名在不同操作系统的可能有问题。
			{
				MD5 md5 = new MD5();
				tempName = md5.getMD5ofStr(tempName);
			}
			List result = fileUploadByHttpForm(req, tempPath, tempName);
			if (result != null)
			{
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				File file = new File(tempPath + tempName);
				InputStream fin = new FileInputStream(file);
				InputStream ois = new FileInputStream(file);
				Users userInfo = (Users) req.getSession().getAttribute(
						"userKey");
				Fileinfo info = fileSystemService.createFile(userInfo.getId(),
						userInfo.getRealName(), path, fileName, fin, ois,
						false, null);
				fin.close();
				file.delete();
				return info;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;

	}

	/**
	 * 直接把文件上传到用户的送审目录中
	 * 
	 * @param req
	 * @param res
	 */
	public static Fileinfo uploadFileForAudit(HttpServletRequest req,
			HttpServletResponse res)
	{
		try
		{
			String path = WebTools.converStr(req
					.getParameter(Constants.FilePath));
			String fileName = WebTools.converStr(req
					.getParameter(Constants.FileName));
			fileName = normalName(fileName);

			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			String tempName = System.currentTimeMillis() + fileName;
			fileUploadByHttpForm(req, tempPath, tempName);

			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			File file = new File(tempPath + tempName);
			InputStream fin = new FileInputStream(file);
			InputStream ois = new FileInputStream(file);
			Users userInfo = (Users) req.getSession().getAttribute("userKey");
			Fileinfo info = fileSystemService.addAuditFile(userInfo.getId(),
					fileName, fin, ois);
			fin.close();
			file.delete();
			return info;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;

	}

	/**
	 * 获得需要文件的数据流。在response中返回文件的完整数据内容。
	 * 
	 * @param req
	 * @param res
	 */
	private static void getFileContent(HttpServletRequest req,	HttpServletResponse res, String path, boolean isOpen)
	{
		try
		{
			res.setCharacterEncoding("utf-8");
			// res.setHeader("Content-Type", "utf-8");
			res.setContentType("application/octet-stream");
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			// String path = WebTools.converStr(req.getParameter("path"));
			int index = path.lastIndexOf("/");
			String tempName = path.substring(index + 1);
			tempName = encodeDownloadName(req.getHeader("User-Agent"), tempName);
			res.setHeader("Content-Disposition", "attachment;filename=\""	+ tempName + "\"");
			res.setHeader("errorCode", "0");

			InputStream in;

			if (path.startsWith("jcr:system"))
			{
				in = jcrService.getVersionContent(path, "1.0");
				// in=jcrService.getContent(path);//"system_audit_root/user_sah_1338897652562/2011.doc"
			}
			else if (path.indexOf("/")>0)
			{
				in = jcrService.getContent("", path, isOpen);
			}
			else
			{
				File file = new File(WebConfig.tempFilePath + File.separatorChar + path);
				in = new FileInputStream(file);
			}
			res.setHeader("Content-Length", String.valueOf(in.available()));

			OutputStream oos = res.getOutputStream();
			byte[] buff = new byte[BYTE_SIZE];
			int readed;
			while ((readed = in.read(buff)) > 0)
			{
				oos.write(buff, 0, readed);
			}
			oos.flush();
			oos.close();
			in.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 获得需要文件的数据流。在response中返回文件的完整数据内容。
	 * 
	 * @param req
	 * @param res
	 */
	private static void getFileContent(HttpServletResponse res, InputStream in,
			String fileName)
	{
		try
		{
			res.setCharacterEncoding("utf-8");
			res.setContentType("application/octet-stream");
			fileName = encodeDownloadName("", fileName);
			res.setHeader("Content-Disposition", "attachment;filename=\""
					+ fileName + "\"");

			OutputStream oos = res.getOutputStream();
			byte[] buff = new byte[BYTE_SIZE];
			int readed;
			while ((readed = in.read(buff)) > 0)
			{
				oos.write(buff, 0, readed);
			}
			oos.flush();
			oos.close();
			in.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 从文件库中获取文件，并临时保存在服务器的临时目录中。
	 * 
	 * @param reporsitoryPath
	 *            ，文件库中文件的path
	 * @param path
	 *            ， 文件保存在服务器中的临时目录
	 * @param fileName
	 *            文件的名字。
	 * @return 
	 *         返回取出的文件在临时目录中的文件名字，如果传入的fileName不为null，则返回该传入的名字，否则返回由系统生产的临时文件名字。
	 */
	private static String getFileFromRepository(String reporsitoryPath,
			String path, String fileName, boolean isOpen, String userName,
			String spaceUID)
	{
		InputStream in = null;
		OutputStream out = null;
		try
		{
			File file = new File(path);
			if (!file.exists())
			{
				file.mkdirs();
			}
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			if (fileName == null || fileName.length() < 1)
			{
				fileName = System.currentTimeMillis() + ".tmp";
			}
			int idx = reporsitoryPath.indexOf("jcr:system/jcr:versionStorage");
			int idx2 = reporsitoryPath.indexOf("system_audit_root");
			if (idx > -1)
			{
				in = jcrService.getVersionContent(reporsitoryPath, fileName);
			}
			else if (idx2 > -1)
			{
				in = jcrService.getContent(reporsitoryPath);
			}
			else
			{
				in = jcrService.getContent(userName, reporsitoryPath, isOpen); // 从文件库中获取文件流
			}
			File userFile = new File(path + File.separatorChar + fileName);
			out = new FileOutputStream(userFile);
			byte[] b = new byte[BYTE_SIZE];
			int len = 0;
			while ((len = in.read(b)) > 0)
			{
				out.write(b, 0, len);
			}
			out.flush();
			if (isOpen && idx < 0)
			{
				UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
				Users users = userService.getUser(userName);
				boolean permitwrite = hasPermission(reporsitoryPath, users, FileSystemCons.WRITE_FLAG);
				if (permitwrite)//只有写权限的用户才记录打开标记
				{
					jcrService.addOpenedFile(userName, reporsitoryPath);
				}
				jcrService.addUserOpenFile(spaceUID, reporsitoryPath);
			}
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
				if (out != null)
				{
					out.close();
				}
			}
			catch (Exception ee)
			{
			}
		}
		return fileName;
	}

	/**
	 * 直接从请求流中获取文件内容
	 * 
	 * @param request
	 * @param path
	 * @param name
	 * @param offset 文件偏移位置
	 * @param totalSize 为需要验证的文件长度，如果为-1，表示不需要验证文件长度
	 * @return
	 */
	private static String uploadFile(HttpServletRequest request, String path,
			String name, long offset, long totalSize)
	{
		try
		{
			InputStream in = request.getInputStream();
			File file = new File(path);
			if (!file.exists())
			{
				file.mkdir();
			}
			path = path + name;
			file = new File(path);

			RandomAccessFile out = new RandomAccessFile(file, "rw");
			out.seek(offset);
			byte[] b = new byte[BYTE_SIZE];
			int a = 0;
			long size = 0;
			while ((a = in.read(b)) > 0)
			{	
				out.write(b, 0, a);
				size += a;
			}
			out.close();
			in.close();
			if (totalSize != -1 && totalSize != size)    // 验证文件长度
			{
				return null;
			}
			return name;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
			return null;
		}
	}

	public static List<String> fileUploadByHttpForm(HttpServletRequest request,
			String path, long maxfilesize, String... newFileName)
	{
		// System.out.println("=============  "+request.getRequestURI()+"\n"+request.getQueryString());
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		List<String> retName = new ArrayList<String>();
		if (isMultipart)
		{
			try
			{
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> fileItems = upload.parseRequest(request);
				Iterator<FileItem> iter = fileItems.iterator();
				String tempName;
				// 依次处理每个表单域
				int i = 0;
				int size = newFileName != null ? newFileName.length : 0;
				while (iter.hasNext())
				{
					FileItem item = iter.next();
					long filesize = item.getSize();
					if (!item.isFormField()) // 文件域
					{
						if (item.getSize() > maxfilesize)
						{
							return new ArrayList<String>();
						}
						if (i < size)
						{
							tempName = uplaodFile(item, path, newFileName[i]);
						}
						else
						{
							tempName = uplaodFile(item, path, null);
						}
						retName.add(tempName);
						i++;
					}
				}
			}
			catch (Exception e)
			{
				LogsUtility.error(e);
				return null;
			}
		}
		else
		{
			System.out.println("the enctype must be multipart/form-data");
			return null;
		}
		return retName;
	}

	public static String fileUploadByHttpFormForSave(HttpServletRequest request,
			String path, String newFileName)
	{
		// user290 2012-10-30
		// System.out.println("=============  "+request.getRequestURI()+"\n"+request.getQueryString());
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String retName = null;
		if (isMultipart)
		{
			try
			{
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> fileItems = upload.parseRequest(request);
				if (fileItems != null && fileItems.size() > 0)
				{
					FileItem item = fileItems.get(0);
					if (item != null && !item.isFormField())  // 文件域
					{
						long filesize = item.getSize();
						if (filesize <= 0)
						{
							return null;
						}
						retName = uplaodFile(item, path, newFileName);
					}
				}
			}
			catch (Exception e)
			{
				LogsUtility.error(e);
				return null;
			}
		}
		else
		{
			System.out.println("the enctype must be multipart/form-data");
			return null;
		}
		return retName;
	}
	
	/**
	 * 通过http协议，进行标准的form表单方式进行文档上传处理。
	 * 
	 * @param request
	 *            http上传请求
	 * @param path
	 *            上传文档后，要求文档的保持路径。
	 * @param newFileName
	 *            上传文档后，文档名是否用新的文件名，如果该名为空，则所有的保留原来的文档名，
	 *            如果上传的文档有多个，而该新文件名不够上传的文档个数，则后续的文档名采用原有的文件名。
	 */
	public static List<String> fileUploadByHttpForm(HttpServletRequest request,
			String path, String... newFileName)
	{
		long maxfilesize = 100 * 1024 * 1024;
		return fileUploadByHttpForm(request, path, maxfilesize, newFileName);
	}

	/**
     * 
     */
	private static String uplaodFile(FileItem fileItem, String path,
			String newFileName) throws Exception
	{
		String fileName = newFileName == null ? normalName(fileItem.getName())
				: newFileName;

		File foler = new File(path);
		if (!foler.exists())
		{
			foler.mkdir();
		}
		File file = new File(path + File.separatorChar + fileName);
		fileItem.write(file);
		return fileName;
	}

	public static String normalName(String fileName)
	{
		int index = fileName.lastIndexOf("/");
		if (index >= 0)
		{
			fileName = fileName.substring(index + 1);
		}
		index = fileName.lastIndexOf("\\");
		if (index >= 0)
		{
			fileName = fileName.substring(index + 1);
		}
		if (fileName.length() < 1)
		{
			fileName = "fileName";
		}
		return fileName;
	}

	private static String encodeDownloadName(String useragent, String name)
	{
		try
		{
			useragent = useragent.toLowerCase();
			if (useragent.indexOf("msie") != -1) // IE
			{
				name = URLEncoder.encode(name, "utf-8");
				name = name.replace("+", "%20");
				if (name.length() > 150)
				{
					name = new String(name.getBytes("GB2312"), "ISO-8859-1");
				}
			}
			else if (useragent.indexOf("firefox") != -1) // firefox
			{
				name = new String(name.getBytes("utf-8"), "ISO-8859-1");
			}
			else
			// Chrome、Safari、java
			{
				name = URLEncoder.encode(name, "utf-8");
				name = name.replace("+", "%20");
			}
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		return name;
	}

	/**
	 * 获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息。 消息的内容是：message.attach是文件所在的全路径,
	 * message.content是"文件名/文件大小/文件权限"。
	 * 
	 * @param userId
	 *            用户id
	 * @param start
	 *            需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count
	 *            需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public static long getAllSpaceNewMessagesCountByUserId(Long userId)
	{
		MessagesService messageService = (MessagesService) ApplicationContext
				.getInstance().getBean(MessagesService.NAME);
		return messageService.getAllSpaceNewMessagesCountByUseId(userId);
	}

	/**
	 * 获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息。 消息的内容是：message.attach是文件所在的全路径,
	 * message.content是"文件名/文件大小/文件权限"。
	 * 
	 * @param userId
	 *            用户id
	 * @param start
	 *            需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count
	 *            需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public static List<Messages> getAllSpaceNewMessagesByUserId(Long userId,
			int start, int count)
	{
		MessagesService messageService = (MessagesService) ApplicationContext
				.getInstance().getBean(MessagesService.NAME);
		PermissionService pemissionService = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		List<Messages> ret = messageService.getAllSpaceNewMessagesByUserId(
				userId, start, count);
		for (Messages me : ret)
		{
			if (me.getType() == MessageCons.ADUIT_DOC_TYPE)
			{
				me.setPermit(FileSystemCons.AUDIT_SET);
			}
			else
			{
				Long per = pemissionService.getFileSystemAction(userId, me
						.getAttach(), true);
				if (per != null)
				{
					me.setPermit(per);
				}
				else
				{
					me.setPermit(0L);
				}
			}
		}
		return ret;
	}

	/**
	 * 获取用户所参与的所有空间中的新加入没有看的文件及需要其审批的文件消息,
	 * 消息的内容是：message.content是"文件名/文件大小/文件权限"，message.attach是文件所在的全路径。
	 * 
	 * @param userId
	 *            用户id
	 * @param spaceUID
	 *            空间的spaceUID
	 * @param start
	 *            需要开始的消息开始位置，如果小于0， 则表示从开始位置开始。
	 * @param count
	 *            需要获取的消息条数，如果小于0，则表示从start位置后的所有消息。
	 * @return
	 */
	public List<Messages> getSpaceNewMessagesByUserId(Long userId,
			String spaceUID, int start, int count)
	{
		MessagesService messageService = (MessagesService) ApplicationContext
				.getInstance().getBean(MessagesService.NAME);
		PermissionService pemissionService = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		List<Messages> ret = messageService.getSpaceNewMessagesByUserId(userId,
				spaceUID, start, count);
		for (Messages me : ret)
		{
			if (me.getType() == MessageCons.ADUIT_DOC_TYPE)
			{
				me.setPermit(FileSystemCons.AUDIT_SET);
			}
			else
			{
				Long per = pemissionService.getFileSystemAction(userId, me
						.getAttach());
				if (per != null)
				{
					me.setPermit(per);
				}
			}
		}
		return ret;
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * request
	 * {
	 * 		method:"getPermission",
	 * 		params : {path:"xxxxx",account:"xxxxx",parent:true|false},
	 * 		token: "xxxxxxxx"
	 * }
	 * </pre>
	 * 
	 * <pre>
	 * Response:
	 * {
	 * 		errorCode:"0",
	 * 	errorMessage:null,
	 * 		result:
	 * 		{
	 * 			token:"xxxxx”
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.PERMISSTION_ACTION)
	public static String getPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			Object parent = param.get("parent");
			Long result;
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://"))//模板文件
			{
				return JSONTools.convertToJson(ErrorCons.NO_ERROR, FileSystemCons.SPACE_MANAGER);
			}
			else if (path.indexOf(user.getSpaceUID()) != -1) // 个人空间或审批目录中的送审文档
			{
				return JSONTools.convertToJson(ErrorCons.NO_ERROR, FileSystemCons.SPACE_MANAGER);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			if (path.startsWith(FileConstants.SIGN_ROOT))
			{
				long permit = (long)fss.getPermitOfReviewFile(path, user.getId());
				return JSONTools.convertToJson(ErrorCons.NO_ERROR, permit);
			}
			if (path.startsWith("jcr:system"))
			{
				return JSONTools.convertToJson(ErrorCons.NO_ERROR,	FileSystemCons.READ_SET);
			}
			//判断有没有采编权限
			PermissionService pemissionService = (PermissionService) ApplicationContext.getInstance().getBean(PermissionService.NAME);
			if ("caibian".equals(WebConfig.projectname))
			{
				//有采编权限，但要判断是否在报送表中
				if (fss.isBSFile(path))//是报送文件
        		{
					long per = user != null ? pemissionService.getSystemPermission(user.getId()) : 0;
		        	if (FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))//判断是否有采编权限
		    		{
	        			error = JSONTools.convertToJson(ErrorCons.NO_ERROR,	FileSystemCons.WRITE_SET);
	        			return error;
	        		}
	    		}
			}
			boolean auditFlag = path.startsWith(FileConstants.AUDIT_ROOT);
			if (!auditFlag 
//					&& path.startsWith(FileConstants.USER_ROOT)//这里由于原来老数据不是以user开头的——孙爱华
					 && !path.startsWith(FileConstants.TEAM_ROOT)
					 && !path.startsWith(FileConstants.COMPANY_ROOT)
					&& !path.startsWith(FileConstants.GROUP_ROOT)
					&& !path.startsWith(FileConstants.ORG_ROOT)
					&& !path.startsWith(FileConstants.AUDIT_ROOT)
					&& !path.startsWith(FileConstants.SIGN_ROOT)
					&& !path.startsWith(FileConstants.ARCHIVES)
					) // 可能是共享共享的，临时这样处理
			{
				long p = hasShare(user.getId(), path, null, null);
				return  JSONTools.convertToJson(ErrorCons.NO_ERROR, p);
				//resp.setHeader("Cache-Control",
				//		"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			
			Long permission;
			if (auditFlag) // 审批的文档，目前只有审批者及管理者有权限，其他为阅读者权限
			{
				permission = pemissionService.getSystemPermission(user.getId());
				if (FlagUtility.isValue(permission,
						ManagementCons.AUDIT_AUDIT_FLAG)
						|| FlagUtility.isValue(permission,
								ManagementCons.AUDIT_MANGE_FLAG)
						|| FlagUtility.isValue(permission,
								ManagementCons.AUDIT_SEND_FLAG))
				{
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR,	FileSystemCons.WRITE_SET);
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR,	FileSystemCons.READ_SET);
				}
				return error;
				//resp.setHeader("Cache-Control",
				//		"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			
			// 取具体的权限设置
			if (parent != null)
			{
				result = pemissionService.getFileSystemAction(account, path,
						Boolean.valueOf(parent.toString()));
			}
			else
			{
				result = pemissionService.getFileSystemAction(account, path);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR,	result != null ? result.longValue() : 0);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 文件被谁打开着的。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.FILE_OPENED_ACTION)
	public static String isFileOpened(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		//String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String ret = isFileOpened("", path);
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_FILE_LIST_ACTION)
	public static String getFileList(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			Integer start = (Integer) param.get("start");
			Integer count = (Integer) param.get("count");
			String mobile = (String)param.get("mobile");
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			//wch增加
			String tempPath="";
			try{
				 tempPath = user.getCompany().getSpaceUID();//user947增加
			}
			catch(Exception e){
//				tempFlag = true;
			}//wch;
			
			if (user != null) // 还需要进行登录认证
			{
				ArrayList json = new ArrayList();
				HashMap<String, Object> files;
				boolean fileFlag = true;
				if (path == null || path.length() < 1 || path.equals("/"))
				{
					fileFlag = false;
					List smplFileList = new ArrayList<SimpleFileinfo>();
					files = new HashMap<String, Object>();
					Integer pr = (Integer) param.get("private");
					boolean priFlag = true;
					boolean pubFlag = true;
					boolean teamFlag = true;
					if (pr == null || pr == 0)
					{
					}
					else if (pr == 1) // 只获取私有空间
					{
						pubFlag = false;
						teamFlag = false;
					}
					else if (pr == 2) // 只获取项目空间
					{
						priFlag = false;
						teamFlag = false;
					}
					else if (pr == 3) // 只获取群组空间
					{
						priFlag = false;
						pubFlag = false;
					}
					if (priFlag)
					{
						files.put("name", MainConstants.PERSON_DOCUMENT);
						files.put("folder", true);
						files.put("path", user.getSpaceUID() + "/"
								+ FileConstants.DOC);
						files.put("displayPath", MainConstants.PERSON_DOCUMENT);
						json.add(files);
					}
					if (pubFlag)
					{
						if (mobile != null)
						{
							fileFlag = true;
//							path = JCRService.COMPANY_ROOT  + "/"	+ FileConstants.DOC;
							//wch修改
							if(tempPath == "")
							{
								//path = JCRService.COMPANY_ROOT + "/" + FileConstants.DOC;
							}
							else
							{
								path = tempPath + "/" + FileConstants.DOC;
							}
							//wch
						}
						else
						{
							//List<Spaces> listS = fileService.getGroupSpacesByUserId(user.getId());
							//for (Spaces temp : listS)
							{
								files = new HashMap<String, Object>();
								files.put("name", MainConstants.GROUP_DOCUMENT);// + // MainConstants.PROJECT_DOCUMENT);
								files.put("folder", true);
//								files.put("path", path = JCRService.COMPANY_ROOT  + "/"	+ FileConstants.DOC);
								//wch修改
								if(tempPath == "")
								{
									//files.put("path", path = JCRService.COMPANY_ROOT  + "/"	+ FileConstants.DOC);
								}
								else
								{
									files.put("path", path = tempPath  + "/"	+ FileConstants.DOC);
								}//wch
								
								files.put("displayPath", MainConstants.GROUP_DOCUMENT);// + // MainConstants.PROJECT_DOCUMENT);
								//json.add(files); //信电局版无公文库
							}
						}
					}
					if (teamFlag)
					{
						List<Spaces> listS = fileService.getTeamSpacesByUserId(user.getId());
						for (Spaces temp : listS)
						{
							files = new HashMap<String, Object>();
							files.put("name", temp.getName());// +	// MainConstants.PROJECT_DOCUMENT);
							files.put("folder", true);
							files.put("path", temp.getSpaceUID() + "/"	+ FileConstants.DOC);
							files.put("displayPath", temp.getName());// + // MainConstants.PROJECT_DOCUMENT);
							json.add(files);
						}
					}
				}
				if (fileFlag)
				{
					int index = start != null && start >= 0 ? start : 0;
					int c = count != null && count >= 0 ? count : 1000000;
					List list = jcrService
							.listPageFileinfos("", path, index, c);
					if (list != null && list.size() > 0)
					{
						list.remove(0);
						Fileinfo file;
						for (Object file1 : list)
						{
							file = (Fileinfo) file1;

							files = new HashMap<String, Object>();
							files.put("name", file.getFileName());
							files.put("folder", file.isFold());
							files.put("path", file.getPathInfo());
							files.put("displayPath", file.getShowPath());
							files.put("size", file.getFileSize());
							files.put("modifyTime", file.getLastedTime());
							json.add(files);

						}
					}
				}
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	@HandlerMethod(methodName = ServletConst.GET_SERVER_CHANGE_RECORDS_ACTION)
	public static String getServerChangeRecords(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws ServletException, IOException
	{//获取云盘需要的改动文件

		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
//			String time = (String)param.get("timestamp"); // 上次同步完成的时间戳？
		long time = Long.parseLong((String)param.get("timestamp"));
		String account = (String) param.get("account"); // 登录的账户
		String type=(String)param.get("type");//类型，默认null为个人文档,判断是个人文档还是共享文档
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService) ApplicationContext
		.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(account);
		HashMap<String, Object> changeRecords = new HashMap<String, Object>();
		if (type==null)//个人文档
		{
			String queryString = "from ServerChangeRecords where (addDate>? or deleteDate>? or modifyDate>? or renameDate>?) and path like '"+user.getSpaceUID()+"%'";
			List<ServerChangeRecords> list=jqlService.findAllBySql(queryString,time,time,time,time);
			ArrayList json = new ArrayList();
			if (list!=null)
			{
				for(ServerChangeRecords temp:list)
				{
					HashMap<String, Object> records = new HashMap<String, Object>();
					records.put("path",temp.getPath());
					records.put("oldFileName", temp.getOldFileName());
					records.put("isFolder", temp.getIsFolder());
					records.put("addDate", Long.toString(temp.getAddDate()));
					records.put("deleteDate", Long.toString(temp.getDeleteDate()));
					records.put("modifyDate", Long.toString(temp.getModifyDate()));
					records.put("renameDate", Long.toString(temp.getRenameDate()));				
					json.add(records);
				}
			}
			changeRecords.put("records", json);
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, changeRecords);
		}
		else if ("share".equals(type))
		{
			String queryString = "from ServerChangeRecords where (addDate>? or deleteDate>? or modifyDate>? or renameDate>?) and path not like '"+user.getSpaceUID()+"%'";
			List<ServerChangeRecords> list=jqlService.findAllBySql(queryString,time,time,time,time);
			String sql = "from Personshareinfo as model where model.userinfoBySharerUserId.id= ?";
			List<Personshareinfo> sharelist=jqlService.findAllBySql(queryString,user.getId());
			
			ArrayList json = new ArrayList();
			if (sharelist!=null && list!=null && list.size()>0 && sharelist.size()>0)
			{
				for(ServerChangeRecords temp:list)
				{
					HashMap<String, Object> records = new HashMap<String, Object>();
					boolean isshare=false;
					for (Personshareinfo share:sharelist)
					{
						if (temp.getPath().equals(share.getShareFile()))//判断是否为共享文件
						{
							isshare=true;
							break;
						}
					}
					if (isshare)
					{
						records.put("path",temp.getPath());
						records.put("oldFileName", temp.getOldFileName());
						records.put("isFolder", temp.getIsFolder());
						records.put("addDate", Long.toString(temp.getAddDate()));
						records.put("deleteDate", Long.toString(temp.getDeleteDate()));
						records.put("modifyDate", Long.toString(temp.getModifyDate()));
						records.put("renameDate", Long.toString(temp.getRenameDate()));				
						json.add(records);
					}
				}
			}
			changeRecords.put("records", json);
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, changeRecords);
		}
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, changeRecords);
	}
	
	@HandlerMethod(required = false,methodName = ServletConst.GET_TIMESTAMP__FOR_CLIENT_ACTION)
	public static String getTimeStampForClient(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams) throws ServletException, IOException
	{
		try
		{
 			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
 			String account = (String) param.get("account"); // 登录的账户
			HashMap<String, Object> timestamp = new HashMap<String, Object>();
			long time = new Date().getTime();
			timestamp.put("timestamp",Long.toString(time));
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, timestamp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR,  new Date().getTime());
	}
	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IS_FILE_EXIST_ACTION)
	public static String isFileExist(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		/*String error;
		try
		{*/
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			// String account = (String)param.get("account");
			Boolean ret = jcrService.isFileExist(path);
			HashMap<String, Object> retJson = new HashMap<String, Object>();
			retJson.put("exist", ret);
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retJson);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 文件上传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPLOAD_FILES_ACTION)
	public static String uploadFile(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String fileName = (String) param.get("fileName");
			String replace = (String) param.get("replace");

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user,
					FileSystemCons.UPLOAD_FLAG);
			if (!permit) // 无权限上传文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			//===添加serverchangerecords记录=====
			Client client=new Client();//调用Client中的程序
			client.addSerChangeRecords(path+'/'+fileName, fileName, 0);
           //==================================================//
			fileName = normalName(fileName);

			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			String tempName = System.currentTimeMillis() + fileName;
			List ret = fileUploadByHttpForm(req, tempPath, tempName);

			if (ret != null)
			{
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				File file = new File(tempPath + tempName);
				InputStream fin = new FileInputStream(file);
				InputStream ois = new FileInputStream(file);

				Fileinfo info = fileSystemService.createFile(user.getId(), user
						.getRealName(), path, fileName, fin, ois, false, null,
						"1".equals(replace));
				fin.close();
				file.delete();

				HashMap<String, Object> files = new HashMap<String, Object>();
				files.put("name", info.getFileName());
				files.put("folder", info.isFold());
				files.put("path", info.getPathInfo());
				files.put("displayPath", info.getShowPath());
				files.put("size", info.getFileSize());
				files.put("modifyTime", info.getLastedTime());
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
			}
			else
			{
				error = JSONTools
						.convertToJson(ErrorCons.FILE_FORM_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 文件上传，支持断点续传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.UPLOAD_FILES_CON_ACTION)
	public static String uploadFileCon(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)	throws ServletException, IOException
	{
		String error;
		//try
		//{
			String c = req.getHeader("Content-Type");
			if (!OCTET_APP.equalsIgnoreCase(c))
			{
				return JSONTools.convertToJson(ErrorCons.FILE_OCTET_STREAM_ERROR, null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String fileName = (String) param.get("fileName");
			String replace = (String) param.get("replace");
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user,	FileSystemCons.UPLOAD_FLAG);
			if (!permit) // 无权限上传文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
		   //===添加serverchangerecords记录=====//C++程序调用的接口
			Client client=new Client();//调用Client中的程序
			client.addSerChangeRecords(path+'/'+fileName, fileName, 0);
	       //==================================================//
			String token = (String) param.get("fileToken");
			Object tempOffset = param.get("offset"); //
			long offset = 0;
			if (tempOffset instanceof Integer)
			{
				offset = ((Integer) tempOffset).longValue();
			}
			else if (tempOffset instanceof Long)
			{
				offset = (Long) tempOffset;
			}
			tempOffset = param.get("length"); //
			long totalSize = -1;
			if (tempOffset instanceof Integer)
			{
				totalSize = ((Integer) tempOffset).longValue();
			}
			else if (tempOffset instanceof Long)
			{
				totalSize = (Long) tempOffset;
			}
			String end = (String) param.get("end"); // 0表示没有结束，1表示结束文件的所有传输
			fileName = normalName(fileName);

			MD5 md5 = new MD5();
			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			StringBuffer sb = new StringBuffer();
			sb.append(account);
			sb.append(path);
			sb.append(token);
			sb.append(fileName);
			String tempName = md5.getMD5ofStr(sb.toString());

			String ret = uploadFile(req, tempPath, tempName, offset, totalSize);

			if (ret != null)
			{
				if ("1".equals(end))
				{
					FileSystemService fileSystemService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
					File file = new File(tempPath + tempName);

					// user242,zipEnable
					File unZipFile = null;
					File zipFolder = null;
					if (Boolean.TRUE.equals(param.get("zipEnable")))
					{
						try
						{
							String zipFolderPath = tempPath + tempName + ".zip";
							zipFolder = new File(zipFolderPath);
							if (!zipFolder.exists() || !zipFolder.isDirectory())
							{
								zipFolder.mkdirs();
							}

							unZipFile = new File(zipFolder + "/" + fileName);
							if (!unZipFile.exists())
							{
								ZipUtils.unZip(tempPath + tempName,
										zipFolderPath);
							}
						}
						catch (Exception e)
						{
							unZipFile = null;
						}
						if (unZipFile != null && unZipFile.exists())
						{
							file.delete();
							file = unZipFile;
						}
					}

					InputStream fin = new FileInputStream(file);
					InputStream ois = new FileInputStream(file);

					Fileinfo info = fileSystemService.createFile(user.getId(),
							user.getRealName(), path, fileName, fin, ois,
							false, null, "1".equals(replace));
					fin.close();
					file.delete();
					if (zipFolder != null)
					{
						zipFolder.delete();
					}

					HashMap<String, Object> files = new HashMap<String, Object>();
					files.put("name", info.getFileName());
					files.put("folder", info.isFold());
					files.put("path", info.getPathInfo());
					files.put("displayPath", info.getShowPath());
					files.put("size", info.getFileSize());
					files.put("modifyTime", info.getLastedTime());
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
				}
				else
				{
					error = JSONTools.convertToJson(
							ErrorCons.FILE_RANGE_SUCCESS_ERROR, null);
				}
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR,
						null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SAVE_FILE_ACTION)
	public static String saveFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		String pathinfo = null;
		Users user = null;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String name = (String) param.get("name");
			String replace = (String) param.get("replace");
			String fullPath = path + "/" + name; // user290 2012-04-11

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			user = userService.getUser(account);
			// boolean permit = hasPermission(path, user,
			// FileSystemCons.WRITE_FLAG);
			boolean permit = hasPermission(fullPath, user,
					FileSystemCons.WRITE_FLAG); // user290 2012-04-11
			if (!permit) // 无权限保存文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",				"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			if (fullPath.indexOf("&&&")>=0)
			{
				return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,"不能使用2个以上&符号");
			}
			String tempPath = WebConfig.webContextPath + File.separatorChar
					+ "data"+File.separatorChar+"uploadfile" + File.separatorChar;
			// String tempName = System.currentTimeMillis() + name;
			// String ret = uploadFile(req, tempPath, tempName);
			String extendName = name.substring(name.lastIndexOf("."));
			String tempName = System.currentTimeMillis() + extendName;  // user290 2012-11-08
			String ret = fileUploadByHttpFormForSave(req, tempPath, tempName);  // user290 2012-10-30
			if (ret != null)
			{
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				File file = new File(tempPath + tempName);
				InputStream fin = new FileInputStream(file);
				InputStream ois = new FileInputStream(file);

				Fileinfo info = fileSystemService.createFile(user.getId(), user
						.getRealName(), path, name, fin, ois, false, null, "1"
						.equals(replace));
				String[] paths ={path};
				fileSystemService.shareNewFile(paths, user, info);
				fin.close();
				file.delete();

				HashMap<String, Object> files = new HashMap<String, Object>();
				files.put("name", info.getFileName());
				files.put("folder", info.isFold());
				pathinfo = info.getPathInfo();
				files.put("path", info.getPathInfo());
				files.put("displayPath", info.getShowPath());
				files.put("size", info.getFileSize());
				files.put("modifyTime", info.getLastedTime());
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
				List<Long> userIds = new ArrayList<Long>(0);
				userIds.add(user.getId());
				messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);
				if ((user.getSpaceUID()+"/Document/desktop").equals(path))
				{
					FilesOpeHandler.createDeskLink(path + "/" + name,false,false,user);//创建桌面快捷方式
				}
				//===添加serverchangerecords记录=====//C++程序调用的接口
				Client client=new Client();//调用Client中的程序
				int isFolder = 0;
				if(true == info.isFold())
					isFolder = 1;
				client.addSerChangeRecords(info.getPathInfo(),info.getFileName(),isFolder);
				modifyTeamFile(path,user,1);
	           //==================================================//					
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_FORM_ERROR, null);
			}

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
		if (pathinfo != null)
		{
			Thread receiveT = new Thread(new InsertFileLog(
					new String[] { pathinfo }, user.getId(), 2, null, 2));
			receiveT.start();
		}
		return error;
	}

	/**
	 * 文件保存，支持断点续传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SAVE_FILE_CON_ACTION)
	public static String saveFileCon(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			String c = req.getHeader("Content-Type");
			if (!OCTET_APP.equalsIgnoreCase(c))
			{
				return  JSONTools.convertToJson(ErrorCons.FILE_OCTET_STREAM_ERROR, null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String fileName = (String) param.get("fileName");
			String replace = (String) param.get("replace");
			
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user,	FileSystemCons.WRITE_FLAG);
			if (!permit) // 无权限保存文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			String token = (String) param.get("fileToken");
			Object tempOffset = param.get("offset"); //
			long offset = 0;
			if (tempOffset instanceof Integer)
			{
				offset = ((Integer) tempOffset).longValue();
			}
			else if (tempOffset instanceof Long)
			{
				offset = (Long) tempOffset;
			}
			tempOffset = param.get("length"); //
			long totalSize = -1;
			if (tempOffset instanceof Integer)
			{
				totalSize = ((Integer) tempOffset).longValue();
			}
			else if (tempOffset instanceof Long)
			{
				totalSize = (Long) tempOffset;
			}
			
			String end = (String) param.get("end"); // 0表示没有结束，1表示结束文件的所有传输
			fileName = normalName(fileName);

			MD5 md5 = new MD5();
			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			StringBuffer sb = new StringBuffer();
			sb.append(account);
			sb.append(path);
			sb.append(token);
			sb.append(fileName);
			String tempName = md5.getMD5ofStr(sb.toString());
			if (offset <= 0)
			{
				File file = new File(tempPath + tempName);
				if (file.exists()) // 清除可能存在的错误
				{
					file.delete();
				}
			}

			String ret = uploadFile(req, tempPath, tempName, offset, totalSize);

			if (ret != null)
			{
				if ("1".equals(end))
				{
					FileSystemService fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);

					File file = new File(tempPath + tempName);

					// user242,zipEnable
					File unZipFile = null;
					File zipFolder = null;
					if (Boolean.TRUE.equals(param.get("zipEnable")))
					{
						try
						{
							String zipFolderPath = tempPath + tempName + "zip";
							zipFolder = new File(zipFolderPath);
							if (!zipFolder.exists() || !zipFolder.isDirectory())
							{
								zipFolder.mkdirs();
							}

							ZipUtils.unZip(tempPath + tempName, zipFolderPath);
							unZipFile = new File(zipFolder + "/" + fileName);
						}
						catch (Exception e)
						{
							unZipFile = null;
						}
						if (unZipFile != null && unZipFile.exists())
						{
							file.delete();
							file = unZipFile;
						}
					}

					InputStream fin = new FileInputStream(file);
					InputStream ois = new FileInputStream(file);
					if (path.endsWith(fileName))
					{
						path=path.substring(0,path.lastIndexOf("/"));
					}
					Fileinfo info = fileSystemService.createFile(user.getId(),	user.getRealName(), path, fileName, fin, ois,
							false, null, "1".equals(replace));
					fin.close();
					file.delete();
					if (zipFolder != null)
					{
						zipFolder.delete();
					}

					HashMap<String, Object> files = new HashMap<String, Object>();
					files.put("name", info.getFileName());
					files.put("folder", info.isFold());
					files.put("path", info.getPathInfo());
					files.put("displayPath", info.getShowPath());
					files.put("size", info.getFileSize());
					files.put("modifyTime", info.getLastedTime());
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
					//===添加serverchangerecords记录=====//C++程序调用的接口
					Client client=new Client();//调用Client中的程序
					int isFolder = 0;
					if(true == info.isFold())
						isFolder = 1;
					client.addSerChangeRecords(info.getPathInfo(),info.getFileName(),isFolder);
					modifyTeamFile(path,user,1);
		           //==================================================//			
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.FILE_RANGE_SUCCESS_ERROR, null);
				}
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.CLOSE_FILE_ACTION)
	public static String closeFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			if (!path.startsWith("jcr:system"))
			{
				jcrService.removeUserOpenFile(user.getSpaceUID(), path);
				jcrService.removeOpenedFile(user.getUserName(), path);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*}
		catch (RepositoryException er)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR, null);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DOWNLOAD_FILE_ACTION)
	public static String downloadFile(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		String path = "";
		String account = "";
		String copyurl = WebTools.converStr(req.getParameter(ServletConst.COPYURL));
		if (jsonParams != null)
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			path = (String) param.get("path");
			account = (String) param.get("account");
		}
		else
		{

			if (copyurl != null && copyurl.length() > 5)
			{
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
				path = fileSystemService.getCopyPaths(copyurl);
				if (path == null)
				{
					return Constant.DOWNLOADERROR;
					//return;
				}
				account = "admin";// 外部下载全部记录在admin名下
			}
		}
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(account);
		boolean permit = hasPermission(path, user,	FileSystemCons.DOWNLOAD_FLAG);
		if (!permit && copyurl == null) // 无权限下载
		{
			error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
		}
		else
		{
			getFileContent(req, resp, path, false);
				// 采用多线程方法
			Thread receiveT = new Thread(new InsertFileLog(new String[] { path }, user.getId(), 16, null, null));
			receiveT.start();
				// FileSystemService fileSystemService =
				// (FileSystemService)ApplicationContext
				// .getInstance().getBean(FileSystemService.NAME);
				// fileSystemService.insertFileListLog(new String[]{path},
				// user.getId(), 16);//16为下载
			return null;
				// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/**
	 * 下载资源文件，支持断点续传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DOWNLOAD_FILE_CON_ACTION)
	public static String downloadFileCon(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		//String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String path = (String) param.get("path");
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user,	FileSystemCons.DOWNLOAD_FLAG);
			if (!permit) // 无权限下载
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}

			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = 0;
			}
			Integer end = (Integer) param.get("end");
			if (end == null)
			{
				end = -1;
			}
			String token = (String) param.get("fileToken");
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			Object[] obj = jcrService.getContentProperty(path);

			StringBuilder buffer = new StringBuilder();
			buffer.append(path);
			buffer.append(obj[0]);
			buffer.append(obj[1]);
			MD5 md5 = new MD5();
			String tempS = md5.getMD5ofStr(buffer.toString());
			if (!tempS.equals(token)) // 非同一文件
			{
				token = tempS;
				start = 0;
			}

			int index = path.lastIndexOf("/");
			String tempName = path.substring(index + 1);
			//tempName = encodeDownloadName(req.getHeader("User-Agent"), tempName);

			InputStream in = (InputStream) obj[2];

			// user242,zipEnable
			File zipFile = null;
			if (Boolean.TRUE.equals(param.get("zipEnable")))
			{
				String tempPath = WebConfig.tempFilePath + File.separatorChar;
				String zipName = token + encodeDownloadName(req.getHeader("User-Agent"), tempName) + ".zip";
				zipFile = new File(tempPath + zipName);
				if (!zipFile.exists())
				{
					try
					{
						zipFile.createNewFile();

						ZipUtils.zipSingleFile(zipFile, tempName, in, "", null);
					}
					catch (Exception e)
					{
						if (zipFile != null)
						{
							zipFile.delete();
							zipFile = null;
						}
					}
				}
				if (zipFile != null)
				{
					in = new FileInputStream(zipFile);
					resp.setHeader("zipEnable", "true");
				}
			}

			boolean isSuccessFinish = getFileRangeContent(req, resp, in, start,
					end, token, tempName);
			if (isSuccessFinish && zipFile != null)
			{
				zipFile.delete();
			}

			return null;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 打开文件资源文件，支持断点续传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.OPEN_FILE_CON_ACTION)
	public static String openFileCon(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String path = (String) param.get("path");
			String lockTag = (String) param.get("lockTag");//合并文档时用到的，值为unlock表示不加锁
			
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user, FileSystemCons.READ_FLAG);
			if (!permit && path.indexOf("/")>0) // 无权限打开
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}

			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = 0;
			}
			Integer end = (Integer) param.get("end");
			if (end == null)
			{
				end = -1;
			}
			String token = (String) param.get("fileToken");
			if (path.indexOf("/")>0)
			{
				JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
				Object[] obj = jcrService.getContentProperty(path);
				StringBuilder buffer = new StringBuilder();
				buffer.append(path);
				buffer.append(obj[0]);
				buffer.append(obj[1]);
				MD5 md5 = new MD5();
				String tempS = md5.getMD5ofStr(buffer.toString());
				if (!tempS.equals(token)) // 非同一文件
				{
					token = tempS;
					start = 0;
				}
	
				int index = path.lastIndexOf("/");
				String tempName = path.substring(index + 1);
				tempName = encodeDownloadName(req.getHeader("User-Agent"), tempName);
	
				InputStream in = (InputStream) obj[2];
	
				// user242,zipEnable
				File zipFile = null;
				if (Boolean.TRUE.equals(param.get("zipEnable")))
				{
					String tempPath = WebConfig.tempFilePath + File.separatorChar;
					String zipName = token + tempName + ".zip";
					zipFile = new File(tempPath + zipName);
					if (!zipFile.exists())
					{
						try
						{
							zipFile.createNewFile();
	
							ZipUtils.zipSingleFile(zipFile, tempName, in, "", null);
						}
						catch (Exception e)
						{
							if (zipFile != null)
							{
								zipFile.delete();
								zipFile = null;
							}
						}
					}
					if (zipFile != null)
					{
						in = new FileInputStream(zipFile);
						resp.setHeader("zipEnable", "true");
					}
				}
	
				boolean isSuccessFinish = getFileRangeContent(req, resp, in, start,
						end, token, tempName);
				if (isSuccessFinish && zipFile != null)
				{
					in = null;
					zipFile.delete();
				}
				if (isSuccessFinish)
				{
					if (!path.startsWith("jcr:system") && !"unlock".equals(lockTag))
					{
						jcrService.setUserRecentFile(user.getSpaceUID(), path);
						jcrService.addOpenedFile(user.getUserName(), path);
						jcrService.addUserOpenFile(user.getSpaceUID(), path);
					}
				}
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				fileSystemService.setFileNewFlagByShareOwner(path, user.getId(), 1);
				modifyTeamFile(path,user,0);
			}
			else//这里是临时为移动端增加的草稿打开文件——孙爱华
			{
				File file = new File(WebConfig.tempFilePath + File.separatorChar + path);
				InputStream in = new FileInputStream(file);
				resp.setHeader("Content-Length", String.valueOf(in.available()));
				resp.setCharacterEncoding("utf-8");
				resp.setContentType("application/octet-stream");
				StringBuilder buffer = new StringBuilder();
				buffer.append(path);
				buffer.append(in.available());
				MD5 md5 = new MD5();
				String tempS = md5.getMD5ofStr(buffer.toString());
				if (!tempS.equals(token)) // 非同一文件
				{
					token = tempS;
					start = 0;
				}
				
				resp.setHeader("sourceToken", token);
				resp.setStatus(206);
				if (start < 0)
				{
					start = 0;
				}
				resp.setHeader("start", String.valueOf(start));
				if (end < 0)
				{
					end = in.available();
				}
				resp.setHeader("end", String.valueOf(end));
				resp.setHeader("Content-Length", String.valueOf(in.available()));
				resp.setHeader("errorCode", "0");
				
				OutputStream oos = resp.getOutputStream();
				byte[] buff = new byte[BYTE_SIZE];
				int readed;
				while ((readed = in.read(buff)) > 0)
				{
					oos.write(buff, 0, readed);
				}
				oos.flush();
				oos.close();
				in.close();
			}
			return null;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.OPEN_FILE_ACTION)
	public static String openFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String lockTag = (String) param.get("lockTag");//合并文档时用到的，值为unlock表示不加锁
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user, FileSystemCons.READ_FLAG);
			if (!permit && path.indexOf("/")>0)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
			}
			else
			{
				getFileContent(req, resp, path, false);
				if (!path.startsWith("jcr:system") && path.indexOf("/")>0)
				{
					JCRService jcrService = (JCRService) ApplicationContext
							.getInstance().getBean(JCRService.NAME);
					jcrService.setUserRecentFile(user.getSpaceUID(), path);
					boolean permitwrite = hasPermission(path, user, FileSystemCons.WRITE_FLAG);
					if (permitwrite && !"unlock".equals(lockTag))//只有写权限的用户才记录打开标记
					{
						jcrService.addOpenedFile(user.getUserName(), path);
					}
					jcrService.addUserOpenFile(user.getSpaceUID(), path);
				}
				int optype = 1;
				// PermissionService service =
				// (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
				// Long permission = service.getFileSystemAction(user.getId(),
				// path, true);
				// boolean iswrite=FlagUtility.isValue(FileSystemCons.WRITE_SET,
				// permission);
				// if (iswrite)
				// {
				// optype=2;//有编辑权限
				// }
				if (path.indexOf("/")>0)
				{
					modifyTeamFile(path,user,0);
					FileSystemService fileSystemService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
					fileSystemService.setFileNewFlagByShareOwner(path, user.getId(), 1);
							
					Thread receiveT = new Thread(
							new InsertFileLog(new String[] { path }, user.getId(),
									optype, null, null));
					receiveT.start();
				}
				// FileSystemService fileSystemService =
				// (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
				// fileSystemService.insertFileListLog(new String[]{path},
				// user.getId(), 1);//1开档阅读
				// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				return null;
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.RENAME_ACTION)
	public static String rename(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams) throws Exception,
			IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String name = (String) param.get("name");

			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			String tempP = path.substring(0, path.lastIndexOf("/"));
			if (jcrService.isPathExist(tempP + "/" + name))
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR,
						"有同名文件或文件夹存在");
			}
			else
			{
				MessagesService messageService = (MessagesService) ApplicationContext
						.getInstance().getBean(MessagesService.NAME);
				PermissionService permissionService = (PermissionService) ApplicationContext
						.getInstance().getBean(PermissionService.NAME);
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				FileSystemService fileService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				Users user = userService.getUser(account);
				boolean permit = hasPermission(path, user,
						FileSystemCons.RENAME_FLAG);
				if (permit)
				{
					String newPath = jcrService.rename(null, path, name);
					messageService.updateSpaceNewMessages(path, newPath, name);
					permissionService.updateFileSystemActionForRename(path,
							newPath);
					fileService.delPersonShareinfoByPath(path, user.getId());
					//=======//如果是文件的话，只更新ServerChangeRecords表中对应文件记录的renameDate
					JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);						
					String queryString = "update ServerChangeRecords set renameDate=?,path=? where path=?";
					jqlService.excute(queryString,new Date().getTime(),newPath,path);
					
	     			//如果是文件夹的话，必须更新ServerChangeRecords表中该文件夹下所有文件的path和renameDate
					boolean b =jcrService.isFoldExist(newPath);//有问题，此时path路径已经不存在
	    			if(true == b){
						String queryString1 = "from ServerChangeRecords where path like ?";
						List<ServerChangeRecords> list = jqlService.findAllBySql(queryString1,path+"/%");	
						String path11;							
						for(ServerChangeRecords temp:list)
						{
							int index = path.length();//path=../aa  ;  temp.getPath()=../aa/bb/a.txt
							path11 = temp.getPath().substring(index + 1);//获取bb/a.txt
							jqlService.excute(queryString,new Date().getTime(),newPath+'/'+path11,temp.getPath());
						}
	    			}
	               //====================================================	
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "true");
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"无操作权限 ");
				}
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.CREATE_FOLDER_ACTION)
	public static String createFolder(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		String error = null;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String name = (String) param.get("name");
			String replace = (String) param.get("replace");

			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			if (jcrService.isPathExist(path + "/" + name))
			{
				if ("1".equals(replace))
				{
					String temp = name;
					int index = 1;
					name = temp + "(" + index + ")";
					while (jcrService.isPathExist(path + "/" + name))
					{
						index++;
						name = temp + "(" + index + ")";
					}
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, null);
				}
			}
			if (error == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Users user = userService.getUser(account);
				boolean permit = hasPermission(path, user,
						FileSystemCons.NEW_FLAG);
				if (permit)
				{
					jcrService.createFolder(account, path, name);
					//===添加serverchangerecords记录=====//C++程序调用的接口
					Client client=new Client();//调用Client中的程序
					client.addSerChangeRecords(path+'/'+name, name, 1);
			       //==================================================//
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				}
			}

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DELETE_ACTION)
	public static String delete(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams) throws Exception,
			IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			//JSONArray jsonArray = new JSONArray();
			//JSONArray jsonArray2 = new JSONArray();
			//JSONObject jsonObject;
			//List<String> tempPath = new ArrayList<String>();
			List<String> path = (List<String>) param.get("paths");
			if (path==null)
			{
				path = (List<String>) param.get("filePath");
			}
			/*jsonArray2 = jsonArray.fromObject(path);
			for (int i = 0; i < path.size(); i++)
			{
				jsonObject = jsonArray2.getJSONObject(i);
				String delePath = (String) jsonObject.get("path");
				tempPath.add(delePath);
			}
			path = tempPath;*/
			String account = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			JCRService jcrService = (JCRService) ApplicationContext	.getInstance().getBean(JCRService.NAME);
			for (String temp : path)
			{
				boolean permit = hasPermission(temp, user,	FileSystemCons.DELETE_FLAG);
				if (!permit)
				{
					return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"无操作权限 ");
					//resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
					//resp.getWriter().write(error);
					//return;
				}
				//===更新serverchangerecords记录=====
    			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    			String queryString = "update ServerChangeRecords set deleteDate=? where path=?";
    			jqlService.excute(queryString,new Date().getTime(),temp);	
    			
				//如果是文件夹的话，还需要设置ServerChangeRecords表中文件夹下文件的删除时间 			
    			boolean b =jcrService.isFoldExist(temp);
    			if(true == b){
				String queryString2 = "update ServerChangeRecords set deleteDate=? where path like ?";
				jqlService.excute(queryString2,new Date().getTime(),temp+"/%");
				//===================================
    			}	
			}
			String[] delPath = new String[path.size()];
			path.toArray(delPath);
			jcrService.delete(account, delPath);
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, "true");
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.CREATE_VERSION_ACTION)
	public static String createVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		//String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				//resp.setHeader("Cache-Control",
				//		"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				//return;
			}
			String coment = (String) param.get("coment");
			String status = (String) param.get("status");
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			jcrService.createVersion(path, account, coment, status);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_VERSION_ACTION)
	public static String getVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String account = (String) param.get("account");
			String version = (String) param.get("version");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			InputStream is = jcrService.getVersionContent1(path, version);
			if (is == null)
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_NO_VERSION_ERROR, null);
			}
			else
			{
				int index = path.lastIndexOf("/");
				String fileName = path.substring(index + 1);
				getFileContent(resp, is, fileName);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SHARE_INFO_ACTION)
	public static String getShareInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			Map ret = fileService.findShareFileByUser(account);
			ArrayList result = new ArrayList();
			HashMap<String, Object> resultMap;
			if (ret != null)
			{
				Iterator<String> keys = ret.keySet().iterator();
				while (keys.hasNext())
				{
					resultMap = new HashMap<String, Object>();
					String user = keys.next();
					String[] temp = user.split(";");
					resultMap.put("account", temp[0]);
					resultMap.put("userName", temp[1]);
					List path = (List) ret.get(user);
					resultMap.put("newCount", path.get(0));
					path.remove(0);
					resultMap.put("path", path);
					result.add(resultMap);
				}

			}
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static int getShareInfoNum(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		int size = 0;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			Map ret = fileService.findShareFileByUser(account);
			ArrayList result = new ArrayList();
			HashMap<String, Object> resultMap;
			if (ret != null)
			{
				Iterator<String> keys = ret.keySet().iterator();
				while (keys.hasNext())
				{
					resultMap = new HashMap<String, Object>();
					String user = keys.next();
					String[] temp = user.split(";");
					resultMap.put("account", temp[0]);
					resultMap.put("userName", temp[1]);
					List path = (List) ret.get(user);
					resultMap.put("newCount", path.get(0));
					path.remove(0);
					resultMap.put("path", path);
					result.add(resultMap);
				}

			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			size = result.size();
		}
		catch (ClassCastException e)
		{
			// error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
			// null);
		}
		catch (Exception ee)
		{
			// error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR,
			// null);
		}
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);
		return size;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SHARE_FILES_ACTION)
	public static String shareFiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			List<String> paths = (List<String>) param.get("path");
			List<String> users = (List<String>) param.get("user");
			Integer permit = (Integer) param.get("permit");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			ArrayList list = new ArrayList();
			Date date = new Date();
			for (String tempU : users)
			{
				Users share = userService.getUser(tempU);
				for (String tempP : paths)
				{
					Personshareinfo ps = new Personshareinfo();
					ps.setUserinfoByShareowner(owner);
					ps.setUserinfoBySharerUserId(share);
					ps.setShareFile(tempP);
					ps.setPermit(permit);
					ps.setIsFolder(0);
					ps.setCompanyId("public");
					ps.setDate(date);
					list.add(ps);
				}
			}
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			fileService.setShareinfo(list, null, null, false);
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * flag true为获取送审者，false为获取审批者
	 * 
	 * @param list
	 * @param flag
	 * @return
	 */
	private static List getAuditFileInfo(List list, boolean flag)
	{
		ArrayList result = new ArrayList();
		HashMap<String, Object> resultMap;
		if (list != null && list.size() > 0)
		{
			ApproveBean ab;
			for (Object temp : list)
			{
				ab = (ApproveBean) temp;
				resultMap = new HashMap<String, Object>();
				resultMap.put("id", ab.getApproveinfoId());
				resultMap.put("title", ab.getTitle());
				resultMap.put("step", ab.getStepName());
				resultMap.put("path", ab.getFilePath());
				resultMap.put("fileName", ab.getFileName());
				if (flag)
				{
					resultMap.put("useName", ab.getUserName());
					resultMap.put("depName", ab.getUserDeptName());
				}
				else
				{
					resultMap.put("useName", ab.getTaskApprovalUserName());
					resultMap.put("depName", ab.getTaskApprovalUserDept());
				}
				resultMap.put("status", ab.getStatusName());
				resultMap.put("date", ab.getDate());
				resultMap.put("comment", ab.getComment());
				resultMap.put("nodetype", ab.getNodetype());
				resultMap.put("signtag", ab.getSigntag());
				Boolean f = ab.getPredefined();
				resultMap.put("predefined", f != null ? f : false);
				result.add(resultMap);
			}
		}
		return result;
	}

	/**
	 * 为移动增加的审批文件获取，已读过或未读过
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.AUDIT_FILE_BY_STATUS_ACTION)
	public static String getAuditFilesByType(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Boolean status = (Boolean) param.get("status"); // true为已经阅读过的,
			// false为没有阅读过的
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = (String) param.get("sortName"); // 排序方式
			String sortDir = (String) param.get("sortDir"); // 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh;
				if (status)
				{
					dh = ApprovalUtil.instance().getReadedDocument(
							owner.getId(), 0, start, count, sortName, sortDir);
				}
				else
				{
					dh = ApprovalUtil.instance().getReadingDocument(
							owner.getId(), 0, start, count, sortName, sortDir);
				}
				list = dh.getFilesData();
				List result = getAuditFileInfo(list, true);

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static int getAuditFilesByTypeNum(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		int size = 0;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Boolean status = Boolean.FALSE;// (Boolean)param.get("status"); //
			// true为已经阅读过的, false为没有阅读过的
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = "approveDate";// (String)param.get("sortName"); //
			// 排序方式
			String sortDir = "desc";// (String)param.get("sortDir"); //
			// 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh;
				if (status)
				{
					dh = ApprovalUtil.instance().getReadedDocument(
							owner.getId(), 0, start, count, sortName, sortDir);
				}
				else
				{
					dh = ApprovalUtil.instance().getReadingDocument(
							owner.getId(), 0, start, count, sortName, sortDir);
				}
				list = dh.getFilesData();
				if (list != null)
				{
					size = list.size();
				}
				// size = filterFileByType(1, list);
				// List result = getAuditFileInfo(list, true);
				//
				// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);
		return size;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.AUDIT_PIC_INFO_ACTION)
	public static String getAuditPic(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Object aid = param.get("approveId"); // true为已经阅读过的, false为没有阅读过的
			Long approveId = null;
			if (aid instanceof Integer)
			{
				approveId = ((Integer) aid).longValue();
			}
			if (aid instanceof Long)
			{
				approveId = (Long) aid;
			}
			if (approveId == null)
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						null);
			}
			else
			{
				System.out.println("===========" + approveId);
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Users owner = userService.getUser(account);
				if (owner == null)
				{
					error = JSONTools.convertToJson(
							ErrorCons.USER_NO_EXIST_ERROR, null);
				}
				else
				{
					Map ret = ApprovalUtil.instance().getFlowPicData(
							owner.getId(), approveId);
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
				}
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 为移动增加的设置审批文件为已读过状态
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.AUDIT_FILE_FLAG_ACTION)
	public static String setAuditFilesStatus(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String comment = (String) param.get("comment");
			Object tempId = param.get("id");
			Long id;
			if (tempId instanceof Long)
			{
				id = (Long) tempId;
			}
			else
			{
				id = new Long(((Integer) tempId).longValue());
			}

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				ApprovalUtil.instance()
						.readApproval(id, owner.getId(), comment);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 为移动增加的审批文件获取
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.AUDIT_FILE_ACTION)
	public static String getAuditFiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String status = (String) param.get("status"); // 0 为待签，1为已签 ,
			// 2为以成文，3为发布，4为以归档，5为以销毁
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = (String) param.get("sortName"); // 排序方式
			String sortDir = (String) param.get("sortDir"); // 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh = null;
				if (status.equals("0"))
				{
					dh = ApprovalUtil.instance().getLeaderPaending(
							String.valueOf(owner.getId()),
							ApproveConstants.APPROVAL_STATUS_PAENDING, start,
							count, sortName, sortDir);
				}
				else if (status.equals("1"))
				{
					dh = ApprovalUtil.instance().getLeaderPaending(
							owner.getId(), start, count, sortName, sortDir);
				}
				else if (status.equals("2"))
				{
					dh = ApprovalUtil.instance().getAllEndDocument(
							owner.getId(),
							ApproveConstants.APPROVAL_STATUS_END, start, count,
							sortName, sortDir);
				}
				else if (status.equals("3"))
				{
					dh = ApprovalUtil.instance().getPublishDocument(
							owner.getId(), start, count, sortName, sortDir);
				}
				else if (status.equals("4"))
				{
					dh = ApprovalUtil.instance().getArchiveDocument(
							owner.getId(), start, count, null, sortName,
							sortDir);
				}
				else if (status.equals("5"))
				{
					dh = ApprovalUtil.instance().getDestoryDocument(
							owner.getId(), start, count, sortName, sortDir);
				}
				if (dh != null)
				{
					list = dh.getFilesData();
					List result = getAuditFileInfo(list, true);
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				}

			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static int filterFileByType(int type, List list)
	{
		int size = 0;
		try
		{
			String filter[] = null;
			if (type == 0)
			{
				filter = smallType;
			}
			else if (type == 1)
			{
				filter = bigType;
			}

			if (list != null && list.size() > 0)
			{
				ApproveBean ab;
				for (Object temp : list)
				{
					ab = (ApproveBean) temp;
					String fileName = ab.getFileName();
					String suff = fileName.substring(
							fileName.lastIndexOf('.') + 1).toLowerCase();
					for (String str : filter)
					{
						if (suff.equals(str))
						{
							size++;
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
		return size;
	}

	public static int getAuditFilesNum(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		int size = 0;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String status = "0";// (String)param.get("status"); // 0 为待签，1为已签 ,
			// 2为以成文，3为发布，4为以归档，5为以销毁
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = "approveDate";// (String)param.get("sortName"); //
			// 排序方式
			String sortDir = "desc";// (String)param.get("sortDir"); //
			// 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh = null;
				if (status.equals("0"))
				{
					dh = ApprovalUtil.instance().getLeaderPaending(
							String.valueOf(owner.getId()),
							ApproveConstants.APPROVAL_STATUS_PAENDING, start,
							count, sortName, sortDir);
				}
				else if (status.equals("1"))
				{
					dh = ApprovalUtil.instance().getLeaderPaending(
							owner.getId(), start, count, sortName, sortDir);
				}
				else if (status.equals("2"))
				{
					dh = ApprovalUtil.instance().getAllEndDocument(
							owner.getId(),
							ApproveConstants.APPROVAL_STATUS_END, start, count,
							sortName, sortDir);
				}
				else if (status.equals("3"))
				{
					dh = ApprovalUtil.instance().getPublishDocument(
							owner.getId(), start, count, sortName, sortDir);
				}
				else if (status.equals("4"))
				{
					dh = ApprovalUtil.instance().getArchiveDocument(
							owner.getId(), start, count, null, sortName,
							sortDir);
				}
				else if (status.equals("5"))
				{
					dh = ApprovalUtil.instance().getDestoryDocument(
							owner.getId(), start, count, sortName, sortDir);
				}
				if (dh != null)
				{
					list = dh.getFilesData();
					if (list != null)
					{
						size = list.size();
					}
					// size = filterFileByType(0, list);
					// List result = getAuditFileInfo(list, true);
					// error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
					// result);
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				}

			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);
		return size;
	}

	/**
	 * 为移动增加的送审的审批文件获取
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.REQUEST_AUDIT_FILEINFO_ACTION)
	public static String getRequestAuditFiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String status = (String) param.get("status"); // 0 为待签，1为已签
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = (String) param.get("sortName"); // 排序方式
			String sortDir = (String) param.get("sortDir"); // 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			// String tempStatus;
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh;
				if (status.equals("0"))
				{
					// tempStatus = "签批中";
					dh = ApprovalUtil.instance().getMyPaending(owner.getId(),
							ApproveConstants.APPROVAL_STATUS_PAENDING, start,
							count, sortName, sortDir);
				}
				else
				{
					// tempStatus = "已签批";
					dh = ApprovalUtil.instance().getMyPaending(owner.getId(),
							ApproveConstants.APPROVAL_STATUS_AGREE, start,
							count, sortName, sortDir);
				}
				list = dh.getFilesData();
				List result = getAuditFileInfo(list, false);

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	public static int getRequestAuditFilesNum(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		int size = 0;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String status = "0";// (String)param.get("status"); // 0 为待签，1为已签
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = "approveDate";// (String)param.get("sortName"); //
			// 排序方式
			String sortDir = "desc";// (String)param.get("sortDir"); //
			// 排序方向asc，desc

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			// String tempStatus;
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh;
				if (status.equals("0"))
				{
					// tempStatus = "签批中";
					dh = ApprovalUtil.instance().getMyPaending(owner.getId(),
							ApproveConstants.APPROVAL_STATUS_PAENDING, start,
							count, sortName, sortDir);
				}
				else
				{
					// tempStatus = "已签批";
					dh = ApprovalUtil.instance().getMyPaending(owner.getId(),
							ApproveConstants.APPROVAL_STATUS_AGREE, start,
							count, sortName, sortDir);
				}
				list = dh.getFilesData();
				if (list != null)
				{
					size = list.size();
				}
				// size = filterFileByType(1, list);
				// List result = getAuditFileInfo(list, false);
				//
				// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
		}
		catch (ClassCastException e)
		{
			// error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
			// null);
		}
		catch (Exception ee)
		{
			// error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR,
			// null);
		}
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(error);
		return size;
	}

	/**
	 * 为移动增加的搜索审批文件信息
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SEARCH_AUDIT_FILE_ACTION)
	public static String searchAuditFiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = (String) param.get("sortName"); // 排序方式
			String sortDir = (String) param.get("sortDir"); // 排序方向asc，desc
			Integer condition = (Integer) param.get("condition"); // 搜索条件
			String keyWord = (String) param.get("keyword"); // 搜索关键字

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			if (owner == null)
			{
				error = JSONTools.convertToJson(ErrorCons.USER_NO_EXIST_ERROR,
						null);
			}
			else
			{
				List list;
				DataHolder dh = ApprovalUtil.instance().searchLeaderPaending(
						owner.getId(), condition, keyWord, start, count,
						sortName, sortDir);
				list = dh.getFilesData();
				List result = getAuditFileInfo(list, true);

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 为移动增加的获取审批文件的所有过程信息
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.AUDIT_FILEINFO_ACTION)
	public static String getAuditFileInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		//String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = -1;
			}
			Integer count = (Integer) param.get("count");
			if (count == null)
			{
				count = -1;
			}
			String sortName = (String) param.get("sortName"); // 排序方式
			String sortDir = (String) param.get("sortDir"); // 排序方向asc，desc
			String path = (String) param.get("path"); // 搜索关键字

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			List list;
			DataHolder dh = ApprovalUtil.instance().getAuditFileInfo(path,
					start, count, sortName, sortDir);
			list = dh.getFilesData();
			List result = getAuditFileInfo(list, true);

			return JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 为移动增加的审批文件的方法。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.AUDIT_ACTION)
	public static String auditFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// TODO
//		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			Object tempId = param.get("id");
			Long apid;
			if (tempId instanceof Long)
			{
				apid = (Long) tempId;
			}
			else
			{
				apid = new Long(((Integer) tempId).longValue());
			}

			String status = (String) param.get("status");
			String comment = (String) param.get("comment"); // 排序方式
			String path = (String) param.get("path"); // 搜索关键字
			String stepName = (String) param.get("stepName"); // 搜索关键字
			tempId = param.get("auditId");
			String issame = (String) param.get("issame");
			String resend = (String) param.get("resend");
			int renum = 0;
			try
			{
				renum = Integer.parseInt(resend);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			int nextissame = 0;
			if (issame != null && issame.length() > 0 && !issame.equals("null"))
			{
				nextissame = Integer.parseInt(issame);
			}
			Long auditId = null;
			if (tempId instanceof Long)
			{
				auditId = (Long) tempId;
			}
			else if (tempId != null)
			{
				auditId = new Long(((Integer) tempId).longValue());
			}
			List ids = (List) param.get("readerIds");
			ArrayList<Long> readerIds = null;
			if (ids != null)
			{
				readerIds = new ArrayList<Long>();
				for (Object te : ids)
				{
					if (te instanceof Long)
					{
						readerIds.add((Long) te);
					}
					else if (te != null)
					{
						readerIds.add(new Long(((Integer) te).longValue()));
					}
				}
			}
			ids = (List) param.get("preUserIds");
			ArrayList<Long> preUserIds = null;
			if (ids != null)
			{
				preUserIds = new ArrayList<Long>();
				for (Object te : ids)
				{
					if (te instanceof Long)
					{
						preUserIds.add((Long) te);
					}
					else if (te != null)
					{
						preUserIds.add(new Long(((Integer) te).longValue()));
					}
				}
			}

			int aus = ApproveConstants.APPROVAL_STATUS_PAENDING;
			if (status.equals("1"))
			{
				aus = ApproveConstants.APPROVAL_STATUS_AGREE;
			}
			else if (status.equals("0"))
			{
				aus = ApproveConstants.APPROVAL_STATUS_RETURNED;
			}
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);
			ApprovalInfo approvalInfo = new ApprovalInfo();
			approvalInfo.setId((long) apid);
			approvalInfo.setDocumentPath(path);
			boolean ret = ApprovalUtil.instance()
					.aduitOperation(approvalInfo, owner.getId(),
							auditId == null ? null : String.valueOf(auditId),
							aus, comment, readerIds, preUserIds, stepName,
							nextissame, renum);
			return JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (NullPointerException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 为移动增加的发起审批文件的方法。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.START_AUDIT_ACTION)
	public static String startAudit(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		//String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String path = (String) param.get("path");
			String fileName = (String) param.get("fileName");
			String title = (String) param.get("title");
			String comment = (String) param.get("comment");
			String stepName = (String) param.get("stempName");
			String isSame = (String) param.get("isSame");
			String nextId = (String) param.get("nextId");
			String up = (String) param.get("up"); // 0表示是客户端上传的文件，其他表示从空间选择的文件或再审批的文件。
			int flag = 0;
			if (isSame.equals("1"))
			{
				flag = 1;
			}
			ArrayList reader = (ArrayList) param.get("readerIds");
			ArrayList<Long> readerIds = null;
			if (reader != null && reader.size() > 0)
			{
				readerIds = new ArrayList<Long>();
				for (Object t : reader)
				{
					readerIds.add(Long.valueOf((String) t));
				}
			}

			ArrayList preUser = (ArrayList) param.get("preUserIds");
			ArrayList<Long> preUserIds = null;
			if (preUser != null && preUser.size() > 0)
			{
				preUserIds = new ArrayList<Long>();
				for (Object t : preUser)
				{
					preUserIds.add(Long.valueOf((String) t));
				}
			}
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);

			if ("0".equals(up))
			{
				// 上传文件
				fileName = normalName(fileName);
				String tempPath = WebConfig.tempFilePath + File.separatorChar;
				String tempName = System.currentTimeMillis() + fileName;
				Object tempOffset = param.get("length"); //
				long totalSize = -1;
				if (tempOffset instanceof Integer)
				{
					totalSize = ((Integer) tempOffset).longValue();
				}
				else if (tempOffset instanceof Long)
				{
					totalSize = (Long) tempOffset;
				}
				uploadFile(req, tempPath, tempName, 0, totalSize);
				path = tempPath + tempName;
			}

			boolean ret = uploadFileForAudit(owner.getId(), path, fileName,
					nextId, comment, title, readerIds, preUserIds, stepName,
					flag);

			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (NullPointerException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	@HandlerMethod(methodName = ServletConst.RE_AUDIT_ACTION)
	public static String reAudit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String approvalid = (String) param.get("approvalid");
			String title = (String) param.get("title");
			String comment = (String) param.get("comment");
			String stepName = (String) param.get("stempName");
			String isSame = (String) param.get("isSame");
			String nextId = (String) param.get("nextId");
			String up = (String) param.get("up"); // 0表示是客户端上传的文件，其他表示从空间选择的文件或再审批的文件。
			int flag = 0;
			if (isSame.equals("1"))
			{
				flag = 1;
			}
			ArrayList reader = (ArrayList) param.get("readerIds");
			ArrayList<Long> readerIds = null;
			if (reader != null && reader.size() > 0)
			{
				readerIds = new ArrayList<Long>();
				for (Object t : reader)
				{
					readerIds.add(Long.valueOf((String) t));
				}
			}

			ArrayList preUser = (ArrayList) param.get("preUserIds");
			ArrayList<Long> preUserIds = null;
			if (preUser != null && preUser.size() > 0)
			{
				preUserIds = new ArrayList<Long>();
				for (Object t : preUser)
				{
					preUserIds.add(Long.valueOf((String) t));
				}
			}
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users owner = userService.getUser(account);

			boolean ret = reForAudit(owner.getId(), Long.valueOf(approvalid),
					nextId, comment, title, readerIds, preUserIds, stepName,
					flag);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, ret);
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (NullPointerException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}*/
		return  error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	public static void uploadTempFile(HttpServletRequest request,
			HttpServletResponse resp) throws ServletException, IOException
	{
		String fileName = WebTools.converStr(request
				.getParameter(Constants.FileName));
		fileName = normalName(fileName);
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
//		tempPath=tempPath.replace(WebConfig.TEMPFILE_FOLDER,"data"+ File.separatorChar+WebConfig.TEMPFILE_FOLDER);
		String tempName = System.currentTimeMillis() + fileName;
		List<String> ret = fileUploadByHttpForm(request, tempPath, tempName);
		if (ret != null)
		{
			if(ret.size()==0){
				resp.getWriter().print("{success:false,msg:'maxSize'}");
			}else{
				resp.getWriter().print("{success:true,fileName:'" + tempName + "'}");
			}
			
		}
		else
		{
			resp.getWriter().print("{success:false,msg:'error'}");
		}
	}

	/**
	 * 根据fileName判断上传的是本地文件，还是从文档库的文档传入
	 * 
	 * @param title
	 *            标题
	 * @param req
	 * @param res
	 * @param readerIds
	 *            阅读者列表
	 * @param preUserIds
	 *            预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null
	 */
	public static boolean uploadFileForAudit(Long userId, String filePath,
			String showFilePath, String linkManId, String comment,
			String title, ArrayList<Long> readerIds,
			ArrayList<Long> preUserIds, String stepName, int issame)
	{
		// TODO
		boolean flag = false;
		try
		{
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user=userService.getUser(userId);
			String spaceid=user.getSpaceUID();
			if (spaceid==null){spaceid="";}
			if ((filePath.startsWith("user_") || filePath.startsWith("group_") || filePath.startsWith(spaceid))
					&& (filePath.indexOf("/") > 0))// 文档库文档
			{
				// 初次送审文档
				ApprovalUtil.instance().addAduit(userId, filePath,
						showFilePath, linkManId, comment, title, readerIds,
						preUserIds, stepName, issame);
			}
			else if (filePath.startsWith("system_audit_root")
					&& filePath.indexOf("/") > 0)// 已送审过的文档再次送审
			{
				// 再次送审
				ApprovalUtil.instance().reAddAduit(userId, filePath, linkManId, comment,
								title, readerIds, preUserIds, stepName, issame);
			}
			else
			// 本地已上传的文档
			{
				// approvalinfo表，approvalTask表中都加入记录
				ApprovalUtil.instance().upAddAduit(userId, filePath,
						showFilePath, linkManId, comment, title, readerIds,
						preUserIds, stepName, issame);
			}
			flag = true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return flag;

	}

	public static boolean reForAudit(Long userId, Long approvalid,
			String linkManId, String comment, String title,
			ArrayList<Long> readerIds, ArrayList<Long> preUserIds,
			String stepName, int issame)
	{
		// TODO
		boolean flag = false;
		try
		{

			ApprovalUtil.instance().retempAddAduit(userId, approvalid,
					linkManId, comment, title, readerIds, preUserIds, stepName,
					issame);

			flag = true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return flag;

	}

	/**
	 * 获取审批的记录数量
	 * 
	 * @param userId
	 * @return
	 */
	public static long getAuditFileCount(Long userId)
	{
		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		return fileSystemService.getAuditFileCount(userId);
	}

	/**
	 * 获取审批的文件信息。
	 * 
	 * @param userId
	 * @param start
	 * @param count
	 * @return
	 */
	public static List getAuditFile(Long userId, int start, int count)
	{
		return getAuditFile(userId, start, count, 0);
	}

	public static List getAuditFile(Long userId, int start, int count,
			int stateid)
	{
		FileSystemService fileSystemService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		return fileSystemService.getAuditFile(userId, start, count, stateid);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void isFileOpened(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String spaceUID = WebTools.converStr(request.getParameter("spaceUID"),
				"utf-8");
		String path = WebTools.converStr(request.getParameter("path"), "utf-8");
		String ret = isFileOpened(spaceUID, path);
		PermissionService permissionService = (PermissionService) ApplicationContext
				.getInstance().getBean(PermissionService.NAME);
		Users user = (Users) request.getSession().getAttribute("userKey");
		Long p = 0L;
		if (user != null)
		{
			p = permissionService.getFileSystemAction(user.getId(), path, true);
		}
		String retString = (ret != null ? ret : ":f") + "|" + p;
		response.getWriter().write(retString);
	}

	/**
	 * 判断文件是否已经处于打开状态。返回打开者的名字
	 * 
	 * @param spaceUID
	 * @param path
	 * @return
	 */
	public static String isFileOpened(final String spaceUID, final String path)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		try
		{
			if(!path.startsWith("jcr:system"))
			{
				String ret = jcrService.isFileOpened(path);
				if (ret != null) // 空间节点中有被打开记录
				{
					UserService userService = (UserService) ApplicationContext
							.getInstance().getBean(UserService.NAME);
					Users o = userService.getUser(ret);
					if (o != null)
					{
						boolean opened = jcrService.isUserOpenFile(o.getSpaceUID(),
								path);
						if (opened) // 个人节点中有被打开记录
						{
							// 个人在线，则表示是该文件打开着。
							if (UserOnlineHandler.isUserOnline(DEFAULT_DOAMIN, o
									.getUserName()))
							{
								return o.getUserName()+","+o.getRealName();
							}
							jcrService.removeUserOpenFile(o.getSpaceUID(), path);
						}
						else
						{
							jcrService.removeOpenedFile(ret, path);
						}
					}
				}
			}
			return null;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}

	/**
	 * 获得给定path下的打开文件列表。返回打开的文件全路径和打开者的名字
	 * 
	 * @param spaceUID
	 * @param path
	 * @return
	 */
	public static List<String> getFileOpened(final String spaceUID,
			final String path)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		try
		{
			List<String> ret = jcrService.getFileOpened(path);
			if (ret != null && ret.size() > 0) // 空间节点中有被打开记录
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Users o;
				ArrayList<String> tempRet = new ArrayList<String>();
				String key;
				String value;
				int index;
				for (String tempP : ret)
				{
					if ((index = tempP.indexOf("|")) > 0)
					{
						key = tempP.substring(0, index);
						value = tempP.substring(index + 1);
						o = userService.getUser(value);
						if (o != null)
						{
							boolean opened = jcrService.isUserOpenFile(o
									.getSpaceUID(), key);
							if (opened) // 个人节点中有被打开记录
							{
								// 个人在线，则表示是该文件打开着。
								if (UserOnlineHandler.isUserOnline(
										DEFAULT_DOAMIN, o.getUserName()))
								{
									tempRet.add(key);
									continue;
								}
								jcrService.removeUserOpenFile(o.getSpaceUID(),
										key);
							}
							else
							{
								jcrService.removeOpenedFile(value, key);
							}
						}
					}
				}
				if (tempRet.size() > 0)
				{
					return tempRet;
				}
			}
			return null;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}

	/**
	 * 为返回是否有新的版本的方法。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(required = false, methodName = ServletConst.GET_APP_VERSION_ACTION)
	public static String getAppVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		//{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String name = (String) param.get("appName");

			InitDataService initService = (InitDataService) ApplicationContext
					.getInstance().getBean(InitDataService.NAME);
			String[] ret = initService.getVersionContent(name);
			if (ret != null)
			{
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("version", ret[0]);
				hm.put("resource", ret[1].split("\\|"));
				hm.put("path", ret[2]);
				hm.put("update", ret[3]);//是否强制更新
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			return error;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (NullPointerException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	private static String getFileMD5(String path, String source, String fileName)
	{
		try
		{
			File md5 = new File(WebConfig.webContextPath + path + "/" + source
					+ ".md5");
			String tempS;
			if (!md5.exists()) // 没有验证码文件，生成一个
			{
				RandomAccessFile fi = new RandomAccessFile(md5, "rw");
				tempS = FileMD5.getFileHash(fileName, "MD5");
				fi.write(tempS.getBytes());
				fi.close();
			}
			else
			{
				RandomAccessFile fi = new RandomAccessFile(md5, "r");
				byte[] b = new byte[32];
				fi.read(b);
				tempS = new String(b);
				fi.close();
			}
			return tempS;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		return "";
	}

	/**
	 * 下载资源文件，支持断点续传
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.DOWNLOAD_SOURCE_ACTION)
	public static String downloadSource(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException
	{
		/*String error;
		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String path = (String) param.get("path");
			String source = (String) param.get("source");
			String fileName = WebConfig.webContextPath + path + "/" + source;
			File file = new File(fileName);
			if (!file.exists()) // 资源不存在
			{
				return JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR,	null);
				//resp.setHeader("Cache-Control",				"no-store,no-cache,must-revalidate");
				//resp.getWriter().write(error);
				///return;
			}
			Integer start = (Integer) param.get("start");
			if (start == null)
			{
				start = 0;
			}
			Integer end = (Integer) param.get("end");
			if (end == null)
			{
				end = -1;
			}
			String token = (String) param.get("sourceToken");
			// 读文件的验证码
			String tempS = getFileMD5(path, source, fileName);
			// String tempS2 = FileMD5.getFileHash(fileName, "MD5");
			if (!tempS.equals(token)) // 非同一文件
			{
				token = tempS;
				start = 0;
			}
			FileInputStream in = new FileInputStream(file);
			getFileRangeContent(req, resp, in, start, end, token, source);
			return null;
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_EXIST_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}

	/**
	 * 获得需要文件的部分数据流。在response中返回文件的完整数据内容。
	 * 
	 * @param req
	 * @param res
	 */
	private static boolean getFileRangeContent(HttpServletRequest req,
			HttpServletResponse res, InputStream in, int start, int end,
			String token, String fileName)
	{
		// success to over the whole file
		boolean isSuccessFinish = false;
		try
		{
			res.setCharacterEncoding("utf-8");
			res.setContentType("application/octet-stream");
			fileName = encodeDownloadName(req.getHeader("User-Agent"), fileName);
			res.setHeader("Content-Disposition", "attachment;filename=\""
					+ fileName + "\"");
			res.setHeader("sourceToken", token);
			res.setStatus(206);
			if (start < 0)
			{
				start = 0;
			}
			res.setHeader("start", String.valueOf(start));
			if (end < 0)
			{
				end = in.available();
			}
			res.setHeader("end", String.valueOf(end));
			res.setHeader("Content-Length", String.valueOf(in.available()));
			res.setHeader("errorCode", "0");

			int totalLength = in.available();
			if (start < end)
			{
				in.skip(start);

				OutputStream oos = res.getOutputStream();
				byte[] buff = new byte[BYTE_SIZE];
				int readed;
				int total = 0;
				int size = Math.min(end, BYTE_SIZE);
				while ((readed = in.read(buff, 0, size)) > 0)
				{
					oos.write(buff, 0, readed);
					total += readed;
					if (total >= end) // 还没有读完
					{
						break;
					}
					else
					{
						size = Math.min(end - total, BYTE_SIZE);
					}
				}
				// res.setHeader("end", String.valueOf(total));
				oos.flush();
				oos.close();
			}
			if (end == totalLength)
			{
				isSuccessFinish = true;
			}
		}
		catch (Exception ex)
		{
			isSuccessFinish = false;
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return isSuccessFinish;
	}

	/**
	 * 获得打开的文档所在的协作空间显示名称。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_GROUP_SPACE_NAME_ACTION)
	public static String getGroupSpaceName(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user290 2012-03-20
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account");
			String path = (String) param.get("path");
			String spaceUID = path.substring(0, path.indexOf("/"));
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			Spaces spaces = fileService.getSpace(spaceUID);
			if (spaces != null)
			{
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("groupSpaceName", spaces.getName());
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, hm);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
		/*}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (NullPointerException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}*/
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	@HandlerMethod(methodName = ServletConst.GET_WEB_ACTION_CONTROL)
	public static String getWebActionControl(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");
		String path = (String) param.get("path");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		String error;
		Users user = userService.getUser(account);
		if (user != null)
		{
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			boolean amend = fileService.getWebActionControl(user.getId(), path);
			String trackStr = "track=" + amend;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("webActionControl", trackStr);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, map);
		}
		else
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/**
	 * 获得当前用户参与的项目组列表和第一个项目的微博
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_PROJECT_FIRST_BLOG)
	public static String getProjectAndFirstBlog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		// 根据用户名得到用户id
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		// 根据用户id返回参与的项目组列表
		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		List<Groups> groups = microblogService.getGroupList(userID);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("type", 0);
		ArrayList<String> groupNames = new ArrayList<String>();
		long groupId = -1;
		if (groups != null || groups.size() > 0)
		{
			for (int i = 0; i < groups.size(); i++)
			{
				if (i == groups.size() - 1)
				{
					groupId = groups.get(i).getId();
				}
				groupNames.add(groups.get(i).getName());
			}
			resultMap.put("groupNames", groupNames);

			// 页数
			int goPage = 0;
			int pageSize = Integer.parseInt((String) param.get("pageSize"));
			Page page = GridUtil.getGridPage(goPage, pageSize);

			List<PMicroblogMegPo> pmblogList = microblogService.getGroupBlog(
					groupId, null, page);
			ArrayList<HashMap<String, Object>> list = getList(pmblogList);
			if (list != null)
			{
				resultMap.put("microBlog", list);
			}
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, resultMap);

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/**
	 * 获得项目微博
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.getPMicroblog)
	public static String getPMicroblog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		// 根据用户名得到用户id
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		// 项目名称
		String groupName = (String) param.get("groupName");

		// 页数
		int goPage = Integer.parseInt((String) param.get("goPage"));
		int pageSize = Integer.parseInt((String) param.get("pageSize"));

		return  getPMicroblog(groupName, userID, goPage, pageSize, 1);

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	public static String getPMicroblog(String groupName, long userID,
			int goPage, int pageSize, int type)
	{
		// 根据项目组名称得到项目组id
		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		List<Groups> groups = microblogService.getGroupList(userID);
		long groupId = 0;
		for (Groups group : groups)
		{
			if (group.getName().equals(groupName))
			{
				groupId = group.getId();
				break;
			}
		}

		// 页数
		Page page = GridUtil.getGridPage(goPage, pageSize);

		List<PMicroblogMegPo> pmblogList = microblogService.getGroupBlog(
				groupId, null, page);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("type", type);
		ArrayList<HashMap<String, Object>> list = getList(pmblogList);
		if (list != null)
		{
			resultMap.put("microBlog", list);
		}
		else
		{
			resultMap.put("microBlog", new ArrayList<String>());
		}

		return JSONTools.convertToJson(ErrorCons.NO_ERROR, resultMap);
	}

	/**
	 * 得到微博的回复，即评论
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.getBlogBackList)
	public static String getBlogBackList(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		// 根据用户名得到用户id
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);

		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		long parentId = Long.parseLong((String) param.get("blogId"));
		List<PMicroblogMegPo> pmblogList = microblogService
				.getBlogBack(parentId);
		ArrayList<HashMap<String, Object>> list = getList(pmblogList);
		HashMap map = new HashMap();
		map.put("type", 5);
		if (list != null)
		{
			map.put("microBlog", list);
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, map);

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/**
	 * 搜索微博
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.searchBlog)
	public static String searchBlog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		// 根据用户名得到用户id
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		// 搜索内容
		String key = (String) param.get("key");

		// 根据用户名得到用户id
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();
		// 根据项目组名称得到项目组id
		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		List<Groups> groups = microblogService.getGroupList(userID);
		String groupName = (String) param.get("groupName");
		long groupId = 0;
		for (Groups group : groups)
		{
			if (group.getName().equals(groupName))
			{
				groupId = group.getId();
				break;
			}
		}

		int goPage = 0;
		int pageSize = Integer.parseInt((String) param.get("pageSize"));
		Page page = GridUtil.getGridPage(goPage, pageSize);

		List<PMicroblogMegPo> pmblogList = microblogService.searchBlog(groupId,
				key, page);
		HashMap map = new HashMap();
		map.put("type", 2);
		ArrayList<HashMap<String, Object>> list = getList(pmblogList);
		if (list != null)
		{
			map.put("microBlog", list);
		}
		else
		{
			map.put("microBlog", new ArrayList<String>());
		}
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, map);

		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/**
	 * 删除微博
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.delMyBlog)
	public static String delMyBlog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		// 根据用户名得到用户id
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);

		// 根据用户名得到用户
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(userName);
		// 微博id
		long blogId = Long.parseLong((String) param.get("blogId"));

		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		List<PMicroblogMegPo> microBlogList = microblogService
				.getMicroBlog(blogId);
		String groupName = "";
		PMicroblogMegPo parent = null;
		if (microBlogList != null && microBlogList.size() > 0)
		{
			parent = microBlogList.get(0).getParent();
			groupName = microBlogList.get(0).getGroups().getName();
		}
		microblogService.del(blogId, user);

		String result = null;
		if (parent == null)
		{
			result = getPMicroblog(groupName, user.getId(), 0, 10, 4);
		}
		else
		{
			List<PMicroblogMegPo> pmblogList = microblogService
					.getBlogBack(parent.getId());
			ArrayList<HashMap<String, Object>> list = getList(pmblogList);
			HashMap map = new HashMap();
			map.put("type", 5);
			if (list != null)
			{
				map.put("microBlog", list);
			}
			result = JSONTools.convertToJson(ErrorCons.NO_ERROR, map);
		}
		return result;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/**
	 * 发送微博
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.sendBlog)
	public static String sendBlog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		// user266
		// 根据用户名得到用户id
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);

		// 创建一个微博对象
		IPMicroblogService microblogService = (IPMicroblogService) ApplicationContext
				.getInstance().getBean(PMicroblogService.NAME);
		PMicroblogMegPo pmblogMeg = new PMicroblogMegPo();

		// 发送日期
		pmblogMeg.setAddDate(new Date());
		// 项目组
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(userName);
		List<Groups> groups = microblogService.getGroupList(user.getId());
		String groupName = "";
		boolean isComment = true;
		if (param.get("groupName") instanceof String)
		{
			isComment = false;
			groupName = (String) param.get("groupName");
			for (Groups groupObj : groups)
			{
				if (groupObj.getName().equals(groupName))
				{
					pmblogMeg.setGroups(groupObj);
					break;
				}
			}
		}

		// 微博内容
		String meg = (String) param.get("meg");
		pmblogMeg.setMeg(meg);

		// 发送者
		pmblogMeg.setSendUser(user);

		// 父微博
		long parentID = -1;
		if (param.get("parentId") instanceof String)
		{
			parentID = Long.parseLong((String) param.get("parentId"));
			List<PMicroblogMegPo> parentList = microblogService
					.getMicroBlog(parentID);
			if (parentList != null && parentList.size() == 1)
			{
				pmblogMeg.setParent(parentList.get(0));
				pmblogMeg.setGroups(parentList.get(0).getGroups());
			}
		}

		microblogService.add(pmblogMeg);

		String result;
		if (isComment)
		{
			List<PMicroblogMegPo> pmblogList = microblogService
					.getBlogBack(parentID);
			ArrayList<HashMap<String, Object>> list = getList(pmblogList);
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("type", 3);
			if (list != null)
			{
				resultMap.put("microBlog", list);
			}
			result = JSONTools.convertToJson(ErrorCons.NO_ERROR, resultMap);
		}
		else
		{
			result = getPMicroblog(groupName, user.getId(), 0, 10, 3);
		}
		return result;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	private static ArrayList getList(List<PMicroblogMegPo> pmblogList)
	{
		if (pmblogList != null && pmblogList.size() > 0)
		{
			ArrayList list = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map;
			for (PMicroblogMegPo obj : pmblogList)
			{
				map = new HashMap<String, Object>();
				map.put("id", obj.getId());
				map.put("message", obj.getMeg());
				map.put("backCount", obj.getBackCount());
				try
				{
					map.put("addDate", apps.transmanager.weboffice.util.DateUtils
							.ftmDateToString("yyyy-MM-dd HH:mm:ss", obj
									.getAddDate()));
				}
				catch (Exception e)
				{
				}
				map.put("sendUser", obj.getSendUser().getRealName());
				map.put("image", WebConfig.userPortrait
						+ obj.getSendUser().getImage());
				list.add(map);
			}
			return list;
		}
		return null;
	}

	@HandlerMethod(methodName = ServletConst.getCalendarEvent)
	public static String getCalendarEvent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();
		String startDate1 = (String) param.get("startDate1");
		String startDate12 = (String) param.get("startDate2");
		String isOther = (String) param.get("isOthers");
		String other_Names = (String) param.get("otherName");
		if (isOther.contentEquals("true") && other_Names != null)
		{
			userID = userService.getUser(other_Names).getId();
			userName = null;
		}
		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		HashMap calendarMap = calendarService.getCalendarsOfDates(userID,
				userName, startDate1, startDate12);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	@HandlerMethod(methodName = ServletConst.getCalendarEventForMOblie)
	 public static String getCalendarEventForMOblie(HttpServletRequest req, HttpServletResponse resp,
	            HashMap<String, Object> jsonParams) throws ServletException, IOException
	        {
	            HashMap<String, Object> param = (HashMap<String, Object>)jsonParams
	                .get(ServletConst.PARAMS_KEY);
	            String userName = (String)param.get("account");
	            int start=Integer.valueOf(param.get("start").toString());
	            int length=Integer.valueOf(param.get("length").toString());
	            UserService userService = (UserService)ApplicationContext.getInstance().getBean(
	                UserService.NAME);
	            long userID = userService.getUser(userName).getId();
	            
	            ICalendarService calendarService = (ICalendarService)ApplicationContext.getInstance()
	                .getBean(CalendarEventImpl.NAME);
	            HashMap calendarMap = calendarService.getEvents(userID, userName,start,length);
	            if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
	            {
	                ArrayList list = (ArrayList)calendarMap.get("evts");
	                Object object;
	                HashMap map;
	                Date date;
	                for (int i = 0; i < list.size(); i++)
	                {
	                    object = list.get(i);
	                    if (object instanceof HashMap)
	                    {
	                        map = (HashMap)object;
	                        try
	                        {
	                            date = (Date)map.get("start");
	                            map.put("start", apps.transmanager.weboffice.util.DateUtils.ftmDateToString(
	                                "yyyy-MM-dd HH:mm:ss", date));
	                            date = (Date)map.get("end");
	                            map.put("end", apps.transmanager.weboffice.util.DateUtils.ftmDateToString(
	                                "yyyy-MM-dd HH:mm:ss", date));
	                        }
	                        catch(Exception e)
	                        {
	                        }
	                    }
	                }
	            }
	            return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
	            //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	            //resp.getWriter().write(result);
	        }
	 
	 @HandlerMethod(methodName = ServletConst.saveCalendarEvent)
	public static String saveCalendarEvent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		CalendarEvent event = new CalendarEvent();
		long id = -1;
		if (param.get("id").equals("null") || param.get("id").equals(""))
		{
			id = -1;
		}
		else
		{
			id = Long.parseLong(param.get("id").toString());
			event.setId(id);
		}
		if (!param.get("cid").equals("null") && !param.get("cid").equals(""))
		{
			// event.setCalendarId(((Number)param.get("cid")).longValue());
			Long cid = Long.parseLong(param.get("cid").toString());
			event.setCalendarId(cid);
		}
		event.setTitle((String) param.get("title"));
		Date date = new Date();
		try
		{
			event.setStartDate(apps.transmanager.weboffice.util.DateUtils
					.ftmStringToDate("yyyy-MM-dd HH:mm:ss", (String) param
							.get("start")));
			event.setEndDate(apps.transmanager.weboffice.util.DateUtils.ftmStringToDate(
					"yyyy-MM-dd HH:mm:ss", (String) param.get("end")));
		}
		catch (Exception e)
		{
		}
		event.setIsAllDay(Boolean.parseBoolean((String) param.get("ad")));
		event.setNotes((String) param.get("notes"));
		event.setLocation((String) param.get("loc"));
		event.setReminder((String) param.get("rem"));
		event.setIsInvite(Boolean.parseBoolean((String) param.get("isPublic")));
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		event.setUserinfo(userService.getUser(userName));

		event = calendarService.saveCalendarEvent(event);
		if (param.containsKey("inviterUserNames"))
		{
			if ((String) param.get("inviterUserNames") != null)
			{
				String inivterUserNames = (String) param
						.get("inviterUserNames");
				calendarService.dealEventInviteerCon(event, inivterUserNames);
			}
		}
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, event.getId());
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	 @HandlerMethod(methodName = ServletConst.deleteCalendarEvent)
	public static String deleteCalendarEvent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		CalendarEvent event = new CalendarEvent();
		long id = ((Number) param.get("id")).longValue();

		boolean success = calendarService.deleteEvent(id);
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, success);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	 @HandlerMethod(methodName = ServletConst.getTodayCalendarEvents)
	public static String getTodayCalendarEvents(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		HashMap calendarMap = calendarService.getTodayCalendarsEvent(userID);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	 @HandlerMethod(methodName = ServletConst.getAlertCalendarEvent)
	public static String getAlertCalendarEvent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		HashMap calendarMap = calendarService.getAlertCalendarsEvent(userID);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		return JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	public static int getAlertCalendarEventNum(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		int alertEventSize = 0;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		HashMap calendarMap = calendarService.getAlertCalendarsEvent(userID);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			alertEventSize = list.size();
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		// String result = JSONTools.convertToJson(ErrorCons.NO_ERROR,
		// calendarMap)
		// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		// resp.getWriter().write(result);
		return alertEventSize;
	}

	@HandlerMethod(methodName = ServletConst.getCalendarEventsByDate)
	public static String getCalendarEventsByDate(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();
		String isOther = (String) param.get("isOthers");
		String selectdate = (String) param.get("selectDate");
		// Date selectdate =null;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// try {
		// selectdate=sdf.parse(param.get("selectDate").toString());
		// } catch (ParseException e1) {
		// e1.printStackTrace();
		// }
		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		String other_Names = (String) param.get("otherName");
		if (isOther.contentEquals("true") && other_Names != null)
		{
			userID = userService.getUser(other_Names).getId();
			userName = null;
		}
		HashMap calendarMap = calendarService.getCalendarsByDate(userID,
				userName, selectdate);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	@HandlerMethod(methodName = ServletConst.getCalendarEventsByDurationDate)
	public static String getCalendarEventsByDurationDate(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();
		String startDate1 = (String) param.get("startDate1");
		String startDate12 = (String) param.get("startDate2");
		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		HashMap calendarMap = calendarService.getCalendarsDurationDate(userID,
				startDate1, startDate12);
		if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
		{
			ArrayList list = (ArrayList) calendarMap.get("evts");
			Object object;
			HashMap map;
			Date date;
			for (int i = 0; i < list.size(); i++)
			{
				object = list.get(i);
				if (object instanceof HashMap)
				{
					map = (HashMap) object;
					try
					{
						date = (Date) map.get("start");
						map.put("start", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						date = (Date) map.get("end");
						map.put("end", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/*
	 * get all calendarEvent
	 */
	@HandlerMethod(methodName = ServletConst.getCalendarEventCount)
	public static String getCalendarEventCount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		long userID = userService.getUser(userName).getId();

		ICalendarService calendarService = (ICalendarService) ApplicationContext
				.getInstance().getBean(CalendarEventImpl.NAME);
		int eventCount = calendarService.getCalendarCount(userID);
		HashMap calendarMap = new HashMap<Object, Object>();
		calendarMap.put("eventCount", eventCount);
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, calendarMap);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(result);
	}

	/*
	 * get share personal files for moblie by junyang.zheng
	 */
	public static void getMyshareFiles(HttpServletRequest req,
			HttpServletResponse res, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			Users userInfo = (Users) req.getSession().getAttribute("userKey");
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			List fileList = new ArrayList<Fileinfo>();
			fileList = fileService.getMyShare(String.valueOf(userID), userID,
					"1", 0, 10, "desc", "");

			String path = WebTools.converStr(req
					.getParameter(Constants.FilePath));
			String fileName = WebTools.converStr(req
					.getParameter(Constants.FileName));
			SimpleFileinfo smplFileinfo = null;
			for (int i = 1; i < fileList.size(); i++)
			{
				Fileinfo fileinfo = (Fileinfo) fileList.get(1);
				if (true)
				{
					smplFileinfo = new SimpleFileinfo();
					smplFileinfo.setFileName(fileinfo.getFileName());
					smplFileinfo.setFolder(fileinfo.isFold());
					smplFileinfo.setShowPath(fileinfo.getShowPath());
					smplFileinfo.setRealPath(fileinfo.getPathInfo());
					smplFileinfo.setModifiedTime(fileinfo.getLastedTime());
					break;
				}
			}
			ObjectOutputStream oos = new ObjectOutputStream(res
					.getOutputStream());
			if (smplFileinfo != null)
				oos.writeObject(smplFileinfo);
			oos.flush();
			oos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	@HandlerMethod(methodName = ServletConst.getMyshareFile)
	public static String getMyshareFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			List fileList = new ArrayList<Fileinfo>();
			String path = (String) param.get("path");
			Integer start = Integer.valueOf((String) param.get("start"));
			Integer count = Integer.valueOf((String) param.get("count"));
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			Users user = userService.getUser(userName);
			if (user != null) // 还需要进行登录认证
			{
				ArrayList json = new ArrayList();
				HashMap<String, Object> files;
				if (path == null || path.length() < 1 || path.equals("/"))
				{
					fileList = fileService.getMyShare(String.valueOf(userID),
							userID, "1", 0, 10, "desc", "");
				}
				else
				{
					fileList = jcrService.listPageFileinfos("", path, 0, 1000);
				}

				if (fileList != null && fileList.size() > 0)
				{
					fileList.remove(0);
					Fileinfo file;
					for (Object file1 : fileList)
					{
						file = (Fileinfo) file1;
						if (!file.isShared())
						{
							files = new HashMap<String, Object>();
							files.put("name", file.getFileName());
							files.put("folder", file.isFold());
							files.put("path", file.getPathInfo());
							files.put("displayPath", file.getShowPath());
							files.put("size", file.getFileSize());
							files.put("modifyTime", file.getLastedTime());
							json.add(files);
						}

					}
				}

				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						null);
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/*
	 * get share personInfo
	 */
	@HandlerMethod(methodName = ServletConst.getShareOwnerInfo)
	public static String getShareOwnerInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			long groupID = 0;
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			List fileList = new ArrayList<Fileinfo>();
			Users user = userService.getUser(userName);
			if (user != null) // 还需要进行登录认证
			{
				ArrayList json = new ArrayList();
				HashMap<String, Object> files;
				PersonshareinfoDAO personshareinfoDAO = fileService
						.getPersonshareinfoDAO();
				List<Users> fs = personshareinfoDAO.findByShareUsers(userID,
						-1L);
				if (fs != null && fs.size() > 0)
				{
					User file = new User();
					for (int i = 0; i < fs.size(); i++)
					{
						files = new HashMap<String, Object>();
						files.put("shareId", fs.get(i).getId());
						files.put("shareName", fs.get(i).getRealName());
						json.add(files);
					}
				}
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/*
	 * get otherShareInfo
	 */
	@HandlerMethod(methodName = ServletConst.getOtherShareFile)
	public static String getOtherShareFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			List fileList = new ArrayList<Fileinfo>();
			String path = (String) param.get("path");
			Integer start = Integer.valueOf((String) param.get("start"));
			Integer count = Integer.valueOf((String) param.get("count"));
			String sort = (String) param.get("sort");
			String dir = (String) param.get("dir");
			long owerID = Long.valueOf((String) param.get("owerId"));
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			Users user = userService.getUser(userName);

			if (user != null) // 还需要进行登录认证
			{
				List json = new ArrayList();
				HashMap<String, Object> files;
				if (path == null || path.length() < 1 || path.equals("/"))
				{
					fileList = fileService.getPersonOtherShare(userID, owerID,
							start, count, sort, dir);
				}
				else
				{
					fileList = jcrService.listPageFileinfos("", path, 0, 1000);
				}
				// fileList = fileService.getPersonOtherShare(3L, 2L, 0,
				// 10,"desc", "");

				if (fileList != null && fileList.size() > 0)
				{
					fileList.remove(0);
					Fileinfo file;
					for (Object file1 : fileList)
					{
						file = (Fileinfo) file1;
						if (!file.isShared())
						{
							files = new HashMap<String, Object>();
							files.put("name", file.getFileName());
							files.put("folder", file.isFold());
							files.put("path", file.getPathInfo());
							files.put("displayPath", file.getShowPath());
							files.put("size", file.getFileSize());
							files.put("modifyTime", file.getLastedTime());
							json.add(files);
						}
					}
				}
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						null);
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/*
	 * get calendar_shared person infomation
	 */
	public static String getShareCalendarOwnerInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error = null;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			String searchKeyStr = (String) param.get("searcheKey");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			ICalendarService calendarService = (ICalendarService) ApplicationContext
					.getInstance().getBean(CalendarEventImpl.NAME);
			List<String[]> sharedList = calendarService.searchUser(
					searchKeyStr, userID);

		}
		catch (Exception e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/*
	 * get person who `s calendar is public
	 */
	@HandlerMethod(methodName = ServletConst.getUserCalendarPublic)
	public static String getUserCalendarPublic(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			ICalendarService calendarService = (ICalendarService) ApplicationContext
					.getInstance().getBean(CalendarEventImpl.NAME);
			List result = new ArrayList();
			Map<String, Object> temp;
			List<Users> users;
			users = calendarService.getCalendarPublic(userID);
			for (Users u : users)
			{

				temp = new HashMap<String, Object>();

				temp.put("account", u.getUserName());
				temp.put("name", u.getRealName());
				temp.put("portrait", WebConfig.userPortrait + u.getImage());
				temp.put("mail", u.getRealEmail());
				temp.put("address", u.getAddress());
				temp.put("mobile", u.getMobile());
				temp.put("phone", u.getPhone());
				temp.put("duty", u.getDuty());
				result.add(temp);

			}

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		}
		catch (Exception e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/*
	 * get inviter for moblie
	 */
	@HandlerMethod(methodName = ServletConst.getinviterName)
	public static String getinviterName(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			long userID = userService.getUser(userName).getId();
			long eventID = Long.valueOf(param.get("eventID").toString());
			ICalendarService calendarService = (ICalendarService) ApplicationContext
					.getInstance().getBean(CalendarEventImpl.NAME);
			List result = new ArrayList();
			List<CalendarInviteer> users;
			users = calendarService.getInviteersByEvents(eventID);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, users);
		}
		catch (Exception e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/**
	 * 设置日程是否公开
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.CALENDARSHARE)
	public static String modifyCalendarShare(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String isshare = (String) param.get("isshare");// 是否共享参数
			String userstrs = (String) param.get("userstrs");// 共享给部分人员，留有接口
			String account = (String) param.get("account");

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			boolean calendarSetting = false;
			if (isshare != null && isshare.toLowerCase().equals("true"))
			{
				calendarSetting = true;
			}
			ICalendarService calendarService = (ICalendarService) ApplicationContext
					.getInstance().getBean("calendarService");
			calendarService.updateCalendarSetting(user, calendarSetting);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 1);// 设置成功
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);// 设置失败
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}

	/**
	 * 签批确认，第一次双击打开文档时才弹框
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SIGNREAL)
	public static String signreal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");// 签批文件路径
			String id = (String) param.get("appid");// 签批编号
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			// 更新签收标记
			ApprovalInfo approvalInfo = new ApprovalInfo();
			boolean ret = ApprovalUtil.instance().signreal(path, id, user);
			if (ret)
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 1);// 签收成功
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);// 签收失败
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);// 签收失败
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	/**
	 * 获取会签时候，总的需要签批的人数和已经签批的人数。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SIGN_PROCESS_INFO)
	public static String getSignProcessInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
//			String path = (String) param.get("path");// 签批文件路径
			String appids = (String) param.get("appids");// 签批编号,多个编号之间用,间隔
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
			//   
			String[] ids=appids.split(",");
			HashMap temp = new HashMap<String, Object>();
			List result = ApprovalUtil.instance().getSignProcessInfo(ids, user);
//			for ()
//			{
//				temp.put("id", id);
//				temp.put("total", 0);     // 总的需要签批人数
//				temp.put("isDone", 0);    // 已经签批的人数
//			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	/**
	 * 获取会签时候，签批的相关信息
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getSignInfos(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error="";
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String appid = (String) param.get("appid");// 签批编号,只处理一个签批
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
//			List result = new ArrayList();
//			Map<String, Object> temp2 = new HashMap<String, Object>();
//			Map<String, Object> temp;	//
//			List tempR = new ArrayList();			
//			//for ()
//			{	
//				temp = new HashMap<String, Object>();
//			
//				temp.put("name", "");       // 签批人(已签的)
//				temp.put("realName", "");     // 签批人真实名
//				temp.put("time", "");    // 签批时间
//				tempR.add(temp);
//			}
//			temp2.put("isDone", tempR);
//			result.add(temp2);
//			
//			// 未签批的
//			temp2 = new HashMap<String, Object>();	
//			tempR = new ArrayList();	
//			//for ()
//			{
//				temp = new HashMap<String, Object>();
//				
//				temp.put("name", "");       // 签批人(未签的)
//				temp.put("realName", "");     // 签批人真实名
//				temp.put("time", "");    // 签批时间
//				tempR.add(temp);
//			}
//			
//			temp2.put("notDo", tempR);
//			result.add(temp2);
			if (appid!=null)
			{
				List result = ApprovalUtil.instance().getSignInfos(appid, user);		
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			}
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	
	/**
	 * 会签回执
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SET_SIGN_RECEIPT)
	public static String setSignReceipt(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");// 签批文件路径
			String id = (String) param.get("appid");// 签批编号
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
			boolean back=ApprovalUtil.instance().signreal(path, id,user);
						
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	@HandlerMethod(methodName = ServletConst.GET_SIGN_MAN)
	public static String getSignMan(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("appid");// 签批编号
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
			List back=ApprovalUtil.instance().getSignMan(id,user);
			//人名id、人名、签批（阅读）日期、是否回执、提醒次数		
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	/**
	 * 会签催办
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SET_SIGN_WARN)
	public static String setSignWarn(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("appid");// 签批编号
			String uids=(String)param.get("uids");//提醒接收者，多个人之间用,间隔
			String content=(String)param.get("content");//提醒内容，所有人都一样，如果要不同需要扩展一下
			String type=(String)param.get("type");//提醒内容，所有人都一样，如果要不同需要扩展一下
			Integer typevalue=null;
			try
			{
				typevalue=Integer.valueOf(type);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			String title=(String)param.get("title");//提醒标题
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
			boolean back=MessageUtil.instance().setSignWarn(Long.valueOf(id),typevalue,uids.split(","),content,title,user,1);
						
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, false);
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	/**
	 * 会签提醒
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_SIGN_WARN)
	public static String getSignWarn(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		String error;
		//try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("appid");// 签批编号
			String account = (String) param.get("account");// 当前用户账号

			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(account);
			
			Map<String, Object> back=MessageUtil.instance().getSignWarn(Long.valueOf(id),user);
						
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
		}
		//catch (ClassCastException e)
		{
			//error = JSONTools.convertToJson(ErrorCons.NO_ERROR, 0);     
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	
	/**
	 * 建立发布文件路径
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.ADD_PUBLISH_ADDRESS)
	public static String addPublishAddress(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
    {
    	//String error;  
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");   // 需要做权限验证                               
    	String temp = (String)param.get("userId");
    	Long userId = Long.valueOf(temp);    	    	
    	String path = (String)param.get("path");    	
    	temp = (String)param.get("date");
    	Date date = temp != null ? new Date(Long.valueOf(temp)): null;
    	
    	String innerPath = path;
    	MD5 md5 = new MD5();
		String key = md5.getMD5ofStr(path + System.currentTimeMillis());
		
    	PublishAddress pa = new PublishAddress(key, innerPath, date, userId);
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME); 
    	userService.save(pa);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR, "publish/" + key); 
	    //resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    //resp.getWriter().write(error);
    }
	
	/**
	 * 下载发布的文件
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String downloadPushFile(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String error;
		String key = (String) param.get("key");
		String account = (String) param.get("account");     // 后续验证用
		
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		String ret = userService.getPublishAddress(key);
		if (ret == null)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SHARE_EXIST_ERROR, null); 
		}
		else if (ret.length() <= 0)
		{
			error = JSONTools.convertToJson(ErrorCons.FILE_SHARE_VALIDATE_ERROR, null); 
		}
		else
		{
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			if (jcrService.isFileExist(ret))
			{
				getFileContent(req, resp, ret, false);
				return null;
			}
			error = JSONTools.convertToJson(ErrorCons.FILE_SHARE_EXIST_ERROR, null); 
		}
		return error;
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
	}
	 
	/**
	 * 导出用户模板
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.EXPORT_USERS_TEMPLATE)
	public static String exportUsersTemplate(HttpServletRequest req,	HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException
	{
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");     // 后续验证用
		char sep = File.separatorChar;
		String tempPath = WebConfig.webContextPath + sep + "static" + sep + "userinfo" + sep + "Member.txt";
		FileInputStream in = new FileInputStream(tempPath);
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment;filename=\"Member.txt\"");		
		resp.setHeader("Content-Length", String.valueOf(in.available()));	
		
		OutputStream oos = resp.getOutputStream();
		byte[] buff = new byte[BYTE_SIZE];
		int readed;
		while ((readed = in.read(buff)) > 0)
		{
			oos.write(buff, 0, readed);
		}
		oos.close();
		in.close();
		return null;
	}
	
	/**
	 * 导入系统应用模块配置文件
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.IMPORT_SYSTEM_APPS)
	public static String importSystemApps(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		//String error;   
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     // 后续验证权限
    	
		String tempPath = WebConfig.tempFilePath + File.separatorChar;
		String fileName = System.currentTimeMillis() + "system_apps";
		File file = new File(tempPath + fileName);
		fileUploadByHttpForm(req, tempPath, fileName);
		
		Integer ret = AppsHandler.importApps(tempPath + fileName);
		file.delete();
		
		return JSONTools.convertToJson(ret, null);
		//resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		//resp.getWriter().write(error);
		
	}
	/**
	 * 首页获取未处理事务——孙爱华
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GET_NOMODIFYNUMS)
	public static String getNomodifyNums(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     // 获取当前用户的账号
    	Users user = (Users) req.getSession().getAttribute("userKey");
		if (user==null && account!=null)
		{
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			user = userService.getUser(account);
		}
		List<String[]> back=new ArrayList<String[]>();
		long waitwork=SignUtil.instance().getWaitwork(user);//待办——waitwork
		back.add(new String[]{"waitwork",""+waitwork});
		long waitread=SignUtil.instance().getWaitread(user);//待阅——waitread
		back.add(new String[]{"waitread",""+waitread});
		long waitsign=SignUtil.instance().getWaitsign(user);//待签收——waitsign
		back.add(new String[]{"waitsign",""+waitsign});
		long hadcb=SignUtil.instance().getHadcb(user);//被催办——hadcb
		back.add(new String[]{"hadcb",""+hadcb});
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
	}
	
	/**
	 * 简单判断是否是图片
	 * @param fileName
	 * @return
	 */
	private static String isPic(String path) 
	{
		int index = path.lastIndexOf(".");
		if (index == -1) 
		{
			return null;
		}
		String endName = path.substring(index + 1);
		endName = endName.toLowerCase();
		if (endName.equals("gif") || endName.equals("bmp")
				|| endName.equals("jpg") || endName.equals("jpeg")
				|| endName.equals("tif") || endName.equals("png")) 
		{
			return endName;
		}
		return null;
	}
	
	/**
	 * 获取图片的文件流
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ServerPath(path = "/photo")
	public static String getImgForDisplay(HttpServletRequest req, HttpServletResponse resp, String path)  throws ServletException,  IOException
	{
		String end = isPic(path);
		if(end == null)
		{
			return null;
		};
		
		resp.setContentType("image/" + end);
		JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		InputStream in=null;
		try 
		{
			in = jcrService.getContent("",path,false);
		} 
		catch (RepositoryException e) 
		{
			e.printStackTrace();
		}
		if (in == null)
		{
			return null;
		}
		int size;
		byte[] cont = new byte[1024 * 100];
		OutputStream out = resp.getOutputStream();
		while ((size = in.read(cont)) >= 0)
		{
			out.write(cont, 0, size);
		}
		in.close();
		out.close();
		return null;
	}
	
	/**
	 * 获取文件的缩略图
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ServerPath(path = "/thumb")
	public static String getFileThumb(HttpServletRequest req, HttpServletResponse resp, String path)  throws ServletException,  IOException
	{		
		resp.setContentType("image/gif");
		JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		InputStream in = jcrService.getThumbnail(path);
		if (in == null)
		{
			return null;
		}
		int size;
		byte[] cont = new byte[1024 * 100];
		OutputStream out = resp.getOutputStream();
		while ((size = in.read(cont)) >= 0)
		{
			out.write(cont, 0, size);
		}
		in.close();
		out.close();
		return null;
	}
	
	/**
	 * 获取发布的文件
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ServerPath(path = "/publish")
	public static String getPublishFile(HttpServletRequest req, HttpServletResponse resp, String path)  throws ServletException,  IOException
	{
		HashMap<String, Object> jsonParams = new HashMap<String, Object>();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", path);
		jsonParams.put(ServletConst.PARAMS_KEY, params);
		downloadPushFile(req,	resp, jsonParams);		
		
		return null;
	}
	
	/**
	 * 获取office的版本号
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ServerPath(path = "/static/yozoAppletConfig")
	public static String getOfficeVersion(HttpServletRequest req, HttpServletResponse resp, String path)  throws ServletException,  IOException
	{
		try
		{
			File file = new File(WebConfig.webContextPath +  "/setup/office/version.properties");
			long temp = file.lastModified();
			if (temp != officeVersionTime)
			{
				FileInputStream input = new FileInputStream(file);
				officeVersion.load(input);
				input.close();
				officeVersionTime = temp;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		OutputStream reout = resp.getOutputStream();
		String version = officeVersion.getProperty("officeVersion");
		byte[] cont = version.getBytes("utf-8");
		resp.setHeader("Content-Length", "" + cont.length);
		reout.write(cont);	
		
		return null;
	}
	
	/*@ServerPath(path = "/yozorestservice/xxxx")
	public static String xxxx(HttpServletRequest req, HttpServletResponse resp, String path)  throws ServletException,  IOException
	{
		return null;
	}*/
	
	/*
	 * 强制解锁已打开的文件
	 */
	public static String unlockOpendFile(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String path = (String)param.get("path");
		String userName = (String)param.get("userName");
		JCRService jcr = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
		try {
			String ret = jcr.isFileOpened(path);
			if (ret != null) // 空间节点中有被打开记录
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Users o = userService.getUser(ret);
				if (o != null)
				{
					boolean opened = jcr.isUserOpenFile(o.getSpaceUID(),
							path);
					if (opened) // 个人节点中有被打开记录
					{
						if(!path.startsWith("jcr:system"))
						{
							jcr.removeUserOpenFile(user.getSpaceUID(), path);
							jcr.removeOpenedFile(user.getUserName(), path);						
						}
					}
				}
			}		
		} catch (Exception e) {
			LogsUtility.error(e);
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
		return error;
	}
	
	/*
     * 信电局审阅选中的文档
     */
	@HandlerMethod(required = false, methodName = ServletConst.REVIEW_FILE)
    public static String reviewFile(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {
    	String error;
    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String sid = (String)param.get("sid");
    	long reviewId = Long.valueOf(sid.substring(1));
    	int agree = Integer.valueOf((String)param.get("signresult"));
    	String reviewComment = (String)param.get("reviewComment");
    	String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
    	Users user = userService.getUser(userName);
    	fss.reviewFile(reviewId,agree,reviewComment);
    	List<Long> userIds = new ArrayList<Long>(0);
		userIds.add(user.getId());
    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	return error;
    }
    
    /*
     * 信电局反悔已审阅文档
     */
	@HandlerMethod(required = false, methodName = ServletConst.GOBACK_REVIEWFILE)
    public static String goBackReviewFile(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {
    	String error;
    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String sid = (String)param.get("sid");
    	long reviewId = Long.valueOf(sid.substring(1));
    	String userName = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	Users user = userService.getUser(userName);
    	fss.goBackReviewFile(reviewId);
    	List<Long> userIds = new ArrayList<Long>(0);
		userIds.add(user.getId());
    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	return error;
    }
	public static void modifyTeamFile(String path,Users user,Integer doid)
	{//处理协作共享中的文档状态,0为用户阅读，1为用户保存
		try
		{
			if (path.startsWith("team_"))
			{
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				String spaceid=path.substring(0,path.indexOf("/"));
				List<CustomTeams> ctlist=(List<CustomTeams>)jqlService.findAllBySql("select a from CustomTeams as a where a.spaceUID=?", spaceid);
				if (ctlist!=null && ctlist.size()>0)
				{
					CustomTeams customTeams=ctlist.get(0);
					String sql="select a from CustomTeamsFiles as a where a.customTeams.id=? and a.paths=? and a.user.id=?";
					List<CustomTeamsFiles> cfilelist=
							(List<CustomTeamsFiles>)jqlService.findAllBySql(sql, customTeams.getId(),path,user.getId());
					if (cfilelist!=null && cfilelist.size()>0)
					{
						CustomTeamsFiles cFiles=cfilelist.get(0);
						if (doid==1 && cFiles.getDetaildo()!=null && cFiles.getDetaildo().intValue()<1)//用户保存文件
						{
							cFiles.setDetaildo(1);
							jqlService.update(cFiles);
						}
					}
					else
					{
						CustomTeamsFiles cFiles=new CustomTeamsFiles();
						cFiles.setPaths(path);
						cFiles.setUser(user);
						cFiles.setDetaildo(0);
						cFiles.setCustomTeams(customTeams);
						jqlService.save(cFiles);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	@HandlerMethod(methodName = ServletConst.GETMOBILEFIRSTNUMS)
    public static String getMobileFirstNums(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {
    	String error;
    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String userName = (String) param.get("account");
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	Users user = jqlService.getUsers(userName);
    	HashMap<String,Object> back=new HashMap<String,Object>();
    	back.put("newemail", 0);//最新邮件
    	back.put("newschedule", 0);//最新日程
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,back);
    	return error;
    }
	@HandlerMethod(methodName = ServletConst.SETDESKS)
    public static String setDesks(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {//发布到桌面
    	String error;
    	try
    	{
	    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
	    	String userName = (String) param.get("account");
	    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	    	Users user = jqlService.getUsers(userName);
	    	List<HashMap<String,Object>> paramlist=(List<HashMap<String,Object>>)param.get("filePath");
	    	String hashtag=(String) param.get("hashtag");
	    	if (paramlist!=null)
	    	{
	    		for (HashMap<String,Object> values:paramlist)
	    		{
	    			Integer folder=(Integer)values.get("folder");//类型，0是文件夹，1是文件，2 其他，-1固有
			    	Boolean isshare=(Boolean)values.get("shared");
			    	String path=(String)values.get("path");
			    	//1、先判断有没有在桌面创建快捷
			    	List<UserDesks> list=(List<UserDesks>)jqlService.findAllBySql("select a from UserDesks as a where a.paths=? and a.user.id=? ", path,user.getId());
			    	if (list!=null && list.size()>0)
			    	{
			    		//已经存在，不能再创建快捷方式，提示用户
			    	}
			    	else
			    	{
				    	//2、没有创建文件，插入记录
				    	UserDesks userDesks=new UserDesks();
				    	String displayname=path.substring(path.lastIndexOf("/")+1);
				    	userDesks.setDisplayname(displayname);
				    	userDesks.setIsshare(isshare);
				    	userDesks.setPaths(path);
				    	userDesks.setSourcetype(folder);
				    	userDesks.setUser(user);
				    	if (hashtag!=null)
				    	{
				    		userDesks.setHashtag(hashtag);
				    	}
				    	jqlService.save(userDesks);
			    	}
	    		}
	    	}
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"创建桌面快捷方式失败");
    	}
    	return error;
    }
	
	@HandlerMethod(methodName = ServletConst.DELDESKS)
    public static String delDesks(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {//删除桌面快捷方式
    	String error;
    	try
    	{
	    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
	    	String userName = (String) param.get("account");
	    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	    	Users user = jqlService.getUsers(userName);
	    	Number id=(Number)param.get("id");//根据ID来删除
	    	jqlService.deleteEntityByID(UserDesks.class, "id", id.longValue());
	    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,"删除桌面快捷方式失败");
    	}
    	return error;
    }
	@HandlerMethod(methodName = ServletConst.GETDESKS)
    public static String getDesks(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {//获取首页显示的内容
    	String error;
//    	{"items": [{
//    			"id": 2,
//    			"name": "日报",
//    			"type": 0,
//    			"isshare": true,
//    			"path": "路径/Document/日报"
//    		}, {
//    			"id": 10,
//    			"name": "周报2013.13.14.xls",
//    			"type": 1,
//    			"isshare": true,
//    			"path": "路径/Document/周报/周报2013.13.14.xls"
//    		}]
//    	}
    	//我的文库、待审文档、已审文档、他人共享、送审文档、我的共享为固有内容,后面再放文件
    	HashMap<String,Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String userName = (String) param.get("account");
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    	Users user = jqlService.getUsers(userName);
    	HashMap<String,Object> back=new HashMap<String,Object>();//这里面放items对象
    	List<HashMap<String,Object>> resultlist=new ArrayList<HashMap<String,Object>>();
    	List<UserDesks> list=(List<UserDesks>)jqlService.findAllBySql("select a from UserDesks as a where a.user.id=? order by sourcetype", user.getId());
    	if (list!=null && list.size()>0)
    	{
    		for (UserDesks userDesks:list)
    		{
    			HashMap<String,Object> hash=new HashMap<String,Object>();
    			hash.put("id", userDesks.getId());
    			hash.put("name", userDesks.getDisplayname());
    			hash.put("type", userDesks.getSourcetype());
    			hash.put("isshare", userDesks.getIsshare());
    			hash.put("path", userDesks.getPaths());
    			hash.put("hashtag", userDesks.getHashtag());
    			resultlist.add(hash);
    		}
    		back.put("items", resultlist);//最新邮件
    	}
    	back.put("hashnums", getShareDesk(jqlService,user));//固有hash的数量，主要是待审和他人共享
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,back);
    	return error;
    }
	private static List<HashMap<String,Object>> getShareDesk(JQLServices jqlService,Users user)
	{//获取固有桌面快捷方式的数量
		//我的文库、待审文档、已审文档、他人共享、送审文档、我的共享为固有内容,后面再放文件
		HashMap<String, Object> back=FilesOpeHandler.getViewNums(user);
		String waitaudits=(String)back.get("waitaudits");//待审文档数,不一定是最新待审的
		String othershares=(String)back.get("othershares");//最新他人共享的文档数
		String teamshares=(String)back.get("teamshares");//新增协作共享的文档数
		
		
		List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> hash=new HashMap<String,Object>();
		hash.put("hashtag", "my");//我的文库
		hash.put("nums", 0);
		list.add(hash);
		
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "vet_todo");//待审文档
//		FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
//		Long vet_todo=fss.getAllReviewFilesnums(user.getId(),2);
//		if (vet_todo==null)
//		{
//			hash.put("nums", vet_todo);
//		}
//		else
//		{
//			hash.put("nums", 0);
//		}
		hash.put("nums", Long.valueOf(waitaudits));
		list.add(hash);
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "vet_done");//已审文档
		hash.put("nums", 0);
		list.add(hash);
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "shared");//他人共享
		hash.put("nums", Long.valueOf(othershares));
		System.out.println("othershares========"+othershares);
		list.add(hash);
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "vet_send");//送审文档
		hash.put("nums", 0);
		list.add(hash);
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "sharing");//我的共享
		hash.put("nums", 0);
		list.add(hash);
		hash=new HashMap<String,Object>();
		hash.put("hashtag", "grouphome");//协作共享
		hash.put("nums", Long.valueOf(teamshares));
		
		list.add(hash);
		return list;
	}
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.SETCOLLECTEDIT)
	public static void setCollectEdit(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String path = (String)param.get("path");
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			CollectEditSend ces = fileService.getSendCollectEdit(path);
			if (ces != null)
			{
				String fileName = (String)param.get("fileName");
				String des = (String)param.get("des");
				
				fileService.saveCollect(userName, ces.getId(), path, fileName, des);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			}
	    }
	    catch(ClassCastException e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    catch(NullPointerException e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    catch(Exception ee)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GETCOLLECTEDITSEND)
	public static void getCollectEditSend(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String from = (String)param.get("from");
			String to = (String)param.get("to");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = sdf.parse(from);
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = sdf.parse(to);
			end.setHours(23);
			end.setMinutes(59);
			end.setSeconds(59);
			
			String date = sdf.format(start) + " & " + sdf.format(end);
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			List ces = fileService.getSendCollectEdit(start, end);
			StringBuffer sb = new StringBuffer("{\"total\":");			
			if (ces != null)
			{
				int size = ces.size();
				sb.append(size);
				sb.append(",\"rows\":[");
				boolean flag = false;
				Object[] temp;
				for (int i = 0; i < size; i++)
				{
					temp = (Object[])ces.get(i);
					if (flag)
					{
						sb.append(",");
					}
					else
					{
						flag = true;
					}
					sb.append("{\"orgid\":\"");
					sb.append(String.valueOf(temp[0]));
					sb.append("\",\"orgname\":\"");
					sb.append(temp[1]);
					sb.append("\",\"datefrom\":\""); 
					sb.append(date);
					sb.append("\",\"bcount\":\""); 
					sb.append(temp[2]);
					sb.append("\",\"ccount\":\"");
					sb.append(temp[3]);
					sb.append("\"}");
				}
				sb.append("]");
			}
			else
			{
				sb.append(0);
				sb.append(",\"rows\":[]");
			}
			sb.append("}");
			error = sb.toString();
	    }
	    catch(Exception ee)
	    {
	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GETPIC4SCSAME)
	public static void getPic4SCSame(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String from = (String)param.get("from");
			String to = (String)param.get("to");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = sdf.parse(from);
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = sdf.parse(to);
			end.setHours(23);
			end.setMinutes(59);
			end.setSeconds(59);
			List<String> orgids = (List<String>)param.get("orgids");
			List<Long> ids=new ArrayList<Long>();
			if (orgids!=null)
			{
				for (String t:orgids)
				{
					ids.add(Long.valueOf(t));
				}
			}
			//String date = sdf.format(start) + " & " + sdf.format(end);
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			String ces = fileService.getPic4SC(start, end, ids);
			
			if (ces != null)
			{			
				//error = "<input type='button' value='保存' onclick='save(\"" + ces + "\")'><p>"; 
				error = "<table align=\"center\"><tr><td><a href=\"/static/UploadService?caibiansrc=" + ces + "\"><font size=\"22px\" color=\"#000000\"><strong>保存</strong></font></a></td></tr></table><p>";
				error += "<img src=\"" + ces + "\">";
			}
			else
			{
				error = "";
			}
	    }
	    catch(Exception ee)
	    {
	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GETPIC4SCUSER)
	public static void getPic4SCUser(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String orgid = (String)param.get("orgid");
			List dates = (ArrayList)param.get("dates");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int size = dates.size();			
			Date[] starts = new Date[size];
			Date[] ends = new Date[size];
			Map temp;
			for (int i = 0; i < size; i++)
			{
				temp = (Map)dates.get(i); 
				starts[i] = sdf.parse((String)temp.get("from"));
				starts[i].setHours(0);
				starts[i].setMinutes(0);
				starts[i].setSeconds(0);
				ends[i] = sdf.parse((String)temp.get("to"));
				ends[i].setHours(23);
				ends[i].setMinutes(59);
				ends[i].setSeconds(59);
			}
			
			//String date = sdf.format(start) + " & " + sdf.format(end);
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			String ces = fileService.getPic4SC(starts, ends, Long.valueOf(orgid));
			
			if (ces != null)
			{			
				//error = "<input type='button' value='保存' onclick='save(\"" + ces + "\")'><p>"; 
				error = "<table align=\"center\"><tr><td><a href=\"/static/UploadService?caibiansrc=" + ces + "\"><font size=\"22px\" color=\"#000000\"><strong>保存</strong></font></a></td></tr></table><p>";
				error += "<img src=\"" + ces + "\">";
			}
			else
			{
				error = "";
			}
	    }
	    catch(Exception ee)
	    {
	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GETCB)
	public static void getCB(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String from = (String)param.get("from");
			String to = (String)param.get("to");
			String orgid = (String)param.get("orgid");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = sdf.parse(from);
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = sdf.parse(to);
			end.setHours(23);
			end.setMinutes(59);
			end.setSeconds(59);
			
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			List<CollectEdit> ces = fileService.getCollectEdit(Long.valueOf(orgid), -1, -1, "collectTime", "asc", start, end);
			StringBuffer sb = new StringBuffer("{\"total\":");			
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
			if (ces != null)
			{
				int size = ces.size();
				sb.append(size);
				sb.append(",\"rows\":[");
				boolean flag = false;
				CollectEdit temp;
				for (int i = 0; i < size; i++)
				{
					temp = ces.get(i);
					if (flag)
					{
						sb.append(",");
					}
					else
					{
						flag = true;
					}
					sb.append("{\"cbuser\":\"");
					sb.append(temp.getRealName());
					sb.append("\",\"cbdate\":\"");
					sb.append(sdf1.format(temp.getCollectTime()));
					sb.append("\",\"cbdocname\":\""); 
					sb.append(temp.getFileName());
					sb.append("\"}");
				}
				sb.append("]");
			}
			else
			{
				sb.append(0);
				sb.append(",\"rows\":[]");
			}
			sb.append("}");
			error = sb.toString();
	    }
	    catch(Exception ee)
	    {
	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	/**
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	@HandlerMethod(methodName = ServletConst.GETBS)
	public static void getBS(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			
			String userName = (String)param.get("account");
			String from = (String)param.get("from");
			String to = (String)param.get("to");
			String orgid = (String)param.get("orgid");//报送单位编号
			String orgname=(String)param.get("orgname");//报送单位名称
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = sdf.parse(from);
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = sdf.parse(to);
			end.setHours(23);
			end.setMinutes(59);
			end.setSeconds(59);
			
			FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			List<CollectEditSend> ces = fileService.getSendCollectEdit(Long.valueOf(orgid), -1, -1, "sendTime", "asc", start, end);
			StringBuffer sb = new StringBuffer("{\"total\":");	
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
			if (ces != null)
			{
				int size = ces.size();
				sb.append(size);
				sb.append(",\"rows\":[");
				boolean flag = false;
				CollectEditSend temp;
				for (int i = 0; i < size; i++)
				{
					temp = ces.get(i);
					if (flag)
					{
						sb.append(",");
					}
					else
					{
						flag = true;
					}
					sb.append("{\"bsDate\":\"");
					sb.append(sdf1.format(temp.getSendTime()));
					sb.append("\",\"bsName\":\"");
					sb.append(temp.getFileName());
					sb.append("\",\"realName\":\"");
					sb.append(temp.getRealName());//报送人
					sb.append("\"}");
				}
				sb.append("]");
			}
			else
			{
				sb.append(0);
				sb.append(",\"rows\":[]");
			}
			sb.append("}");
			error = sb.toString();
	    }
	    catch(Exception ee)
	    {
	    	
	    }
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	
	
	
	@HandlerMethod(methodName = ServletConst.CAIBIAN)
	public static void caibian(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {//采编文件
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			String userName = (String)param.get("account");
			String path = (String)param.get("path");
			String name = (String) param.get("name");
			Users user=(Users)req.getSession().getAttribute("userKey");
			if (user==null)
			{
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		    	user = jqlService.getUsers(userName);
			}
	    	Long userID=user.getId();
	    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
	    	boolean result=service.doCB(new String[]{path}, new String[]{name}, userID);
	    	if (result)
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	    	}
	    	else
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.ERRORMOBILE_ERROR, result);
	    	}
	    }
	    catch(Exception e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	@HandlerMethod(methodName = ServletConst.BAOSONG)
	public static void baosong(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)
		throws ServletException,  IOException
    {//报送
		String error = ""; 
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
			String userName = (String)param.get("account");
			String path = (String)param.get("path");
			String name = (String) param.get("name");
			Users user=(Users)req.getSession().getAttribute("userKey");
			if (user==null)
			{
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		    	user = jqlService.getUsers(userName);
			}
	    	Long userID=user.getId();
	    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
	    	boolean result=service.doBS(new String[]{path}, new String[]{name}, userID);
	    	if (result)
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	    	}
	    	else
	    	{
	    		error = JSONTools.convertToJson(ErrorCons.ERRORMOBILE_ERROR, result);
	    	}
	    	
	    }
	    catch(Exception e)
	    {
	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
	    }
	    
	    resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	    resp.getWriter().write(error);
    }
	@HandlerMethod(methodName = ServletConst.SAVEMETADATA,required=true)
	public static String saveMetadata(HttpServletRequest req,HttpServletResponse resp, 
		HashMap<String, Object> jsonParams) throws IOException {
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	HashMap<String, Object> params = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String filePath = (String)params.get("path");
    	ArrayList<String> codes = (ArrayList<String>) params.get("code");
    	ArrayList<String> metadataNames = (ArrayList<String>) params.get("metaName");
    	ArrayList<String> metadataValues = (ArrayList<String>) params.get("metaValue");
    	ArrayList<EntityMetadata> metadataList = new ArrayList<EntityMetadata>();
    	if(codes.size() > 0 && metadataNames.size() > 0 && metadataValues.size() > 0 )
    	{
    		for(int i = 0 ; i < codes.size(); i++)
    		{
    			EntityMetadata metadata = new EntityMetadata();
    			metadata.setFilePath(filePath);
    	    	metadata.setCode(codes.get(i));
    	    	metadata.setMetadataName(metadataNames.get(i));
    	    	metadata.setMetadataValue(metadataValues.get(i));
    	    	metadataList.add(metadata);
    		}
    	}
    	FileSystemService fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	fileSystemService.saveMetadata(metadataList);
    	return JSONTools.convertToJson(ErrorCons.NO_ERROR,result);
    	
	}
}
