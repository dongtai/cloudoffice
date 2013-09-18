package apps.transmanager.weboffice.service.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import apps.transmanager.weboffice.client.constant.MainConstant;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.databaseobject.ApprovalSave;
import apps.transmanager.weboffice.databaseobject.CustomTeams;
import apps.transmanager.weboffice.databaseobject.EntityMetadata;
import apps.transmanager.weboffice.databaseobject.Groupshareinfo;
import apps.transmanager.weboffice.databaseobject.NewPersonshareinfo;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Personshareinfo;
import apps.transmanager.weboffice.databaseobject.ReviewFilesInfo;
import apps.transmanager.weboffice.databaseobject.ReviewInfo;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.ServerChangeRecords;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.UserDesks;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetInfo;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetSave;
import apps.transmanager.weboffice.databaseobject.transmanage.TransInfo;
import apps.transmanager.weboffice.databaseobject.transmanage.TransSave;
import apps.transmanager.weboffice.domain.AdminUserinfoView;
import apps.transmanager.weboffice.domain.DataConstant;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.Versioninfo;
import apps.transmanager.weboffice.service.approval.ApprovalUtil;
import apps.transmanager.weboffice.service.approval.MeetUtil;
import apps.transmanager.weboffice.service.approval.MessageUtil;
import apps.transmanager.weboffice.service.approval.SignUtil;
import apps.transmanager.weboffice.service.approval.TransUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.fawen.FawenUtil;
import apps.transmanager.weboffice.service.jcr.FileUtils;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.objects.FileArrayComparator;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.LogServices;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.DateUtils;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.BackgroundSend;
import apps.transmanager.weboffice.util.server.Client;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;
import apps.transmanager.weboffice.util.server.ZipUtils;

public class FilesOpeHandler
{

	private final static int BYTE_SIZE = 1024 * 8;
	private final static String DEFAULT_DOAMIN = "com.yozo.do";
	private final static String OCTET_APP = "application/octet-stream";
	private final static String bigType[] = { "xls", "ppt", "doc", "pdf",
			"txt", "xlsx", "docx", "pptx" };
	private final static String smallType[] = { "xls", "ppt", "doc", "pdf" };
	private static String shareComment = "";

	/**
	 * 获取文件列表
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getFileList(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		String error;
		/*try
		{*/
 			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path"); // 文件或目录的路径
//			System.out.println("path===="+path);
//			getTeamShareNums(req,resp, jsonParams);//测试用的，要删除
			//回收站
			if (path.endsWith(FileConstants.RECYCLER))
			{
				if (path.startsWith("group")
						&& (!path.startsWith("group_company")))
				{
					String spaceUID = path.substring(0,
							path.indexOf("/" + FileConstants.RECYCLER));
					UserService userService = (UserService) ApplicationContext
							.getInstance().getBean(UserService.NAME);
					Long groupId = userService.getGroupIdBySpaceUID(spaceUID);
					Long spacepermission = BFilesOpeHandler
							.getGroupSpacePermission(user.getId(), groupId);
					boolean spacepermissionflag = FlagUtility.isValue(
							spacepermission, SpaceConstants.TRASH_FLAG);
					if (!spacepermissionflag)// 没有权限
					{
						return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						/*resp.setHeader("Cache-Control",
								"no-store,no-cache,must-revalidate");
						resp.getWriter().write(error);
						return;*/
					}
				}
			}
			//判断企业文库浏览权限
			else if (path.startsWith(FileConstants.COMPANY_ROOT)&&(!BFilesOpeHandler.canCompanyOperationFileSystemAction(user.getId(), path, FileSystemCons.BROWSE_FLAG)))
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
				/*resp.setHeader("Cache-Control",	"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			
			String account = (String) param.get("account"); // 登录的账户
//			System.out.println("account===="+account);
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
//			System.out.println(start+"===="+count);
			String filertag = (String) param.get("filtag"); //筛选数据
			String sort = (String) param.get("sort"); // 排序类型
			String order = (String) param.get("order"); // 升序||降序
//			System.out.println("filertag===="+filertag+",sort==="+sort+",order==="+order);
			if (order == null || order.equals(""))
			{
				order = "DESC";
			}
			if (sort == null || sort.equals(""))
			{
				sort = "lastChanged";
			}
			if("".equals(filertag))
			{ 
				filertag = null;
			}
			if (filertag!=null)
			{
				filertag += ",";
			}	
			String fileListType = (String) param.get("fileListType"); // 文档列表类型（个人文件、、群组文档、他人共享、我的贡献,回收站）
//			System.out.println("fileListType===="+fileListType);
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);

			ArrayList json = new ArrayList();
			HashMap<String, Object> files = new HashMap<String, Object>();
			HashMap<String, Object> retJson = new HashMap<String, Object>();
			int fileListSize = 0;
			boolean fileFlag = false;
//			if (fileListType == null && filertag!=null && filertag.length()>1)
//			{
//				fileListType="privateTagFile";
//			}
			if (fileListType == null || fileListType.equals(""))
			{
				fileFlag = true;
			}

			else if (fileListType.equals("privateFile")||fileListType.equals("privateTagFile")) // 个人文档
			{
				HashMap<String, Object> filetagjson = relationTagFile(user.getUserName());
				int index = start != null && start >= 0 ? start : 0;
				int c = count != null && count >= 0 ? count : 1000000;
				String keyword = (String)param.get("keyword");
				List list;
				boolean isSearch = true;
				DataHolder dataHolder = null;
				if(keyword == null )
				{
					list = jcrService.listPageFileinfos("", user.getSpaceUID()
							+ "/" + FileConstants.DOC, index, c, sort, order, filetagjson, filertag);//个人文档不能处理
				}else{
					isSearch = false;
					dataHolder = new DataHolder();
					String[] contents = new String[12];
					ArrayList<Object> array = new ArrayList<Object>();
					contents[0] = keyword;
					contents[2] = keyword;
					contents[3] = keyword;
					contents[4] = keyword;
					contents[5] = keyword;
					contents[7] = keyword;
					try
					{
						dataHolder = jcrService.searchFile(user.getSpaceUID() + "/"
								+ FileConstants.DOC, contents, null, null, index,
								c, null);
					}
					catch (RepositoryException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					list = dataHolder.getFilesData();
				}
				if (list != null && list.size() > 0)
				{
					if(isSearch)
					{
						fileListSize = Integer.parseInt(list.get(0).toString());
						list.remove(0);
					}else {
						fileListSize = dataHolder.getIntData();
					}
					
					Fileinfo file;
					for (Object file1 : list)
					{
						file = (Fileinfo) file1;
						files = new HashMap<String, Object>();
						files.put("tagname", file.getTag());
						files.put("name", file.getFileName());
						files.put("folder", file.isFold());
						files.put("path", file.getPathInfo());
						if ((fileService.getshareInfo(file.getPathInfo()) != null)
								&& (fileService
										.getshareInfo(file.getPathInfo())
										.size() > 0)
								|| (fileService.getAllNewShareinfo(file
										.getPathInfo()) != null)
								&& (fileService.getAllNewShareinfo(
										file.getPathInfo()).size() > 0))
						{
							files.put("isShared", true);
						}
						else
						{
							files.put("isShared", false);
						}
						files.put("displayPath", file.getShowPath());
						files.put(
								"size",
								file.isFold() ? "" : fileSizeOperation(file
										.getFileSize() == null ? 0 : file
										.getFileSize()));
						try
						{
							files.put(
									"modifyTime",
									apps.transmanager.weboffice.util.DateUtils
											.ftmDateToString(
													"yyyy-MM-dd HH:mm:ss",
													(file.getLastedTime() == null ? file
															.getCreateTime()
															: file.getLastedTime())));
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						json.add(files);
					}
				}
				retJson.put("fileListSize", fileListSize);
				retJson.put("currentFolderPath", user.getSpaceUID() + "/"
						+ FileConstants.DOC);
				retJson.put("fileList", json);
			}
//			else if (fileListType.equals("searchPrivateFile")) // 搜索个人文档
//			{
//				int index = start != null && start >= 0 ? start : 0;
//				int c = count != null && count >= 0 ? count : 1000000;
//				String keyword = (String) param.get("keyword");
//				String[] contents = new String[12];
//				ArrayList<Object> array = new ArrayList<Object>();
//				DataHolder dataHolder = new DataHolder();
//				contents[0] = keyword;
//				contents[2] = keyword;
//				contents[3] = keyword;
//				contents[4] = keyword;
//				contents[5] = keyword;
//				contents[7] = keyword;
//				try
//				{
//					dataHolder = jcrService.searchFile(user.getSpaceUID() + "/"
//							+ FileConstants.DOC, contents, null, null, index,
//							c, null);
//				}
//				catch (RepositoryException e1)
//				{
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				List list = dataHolder.getFilesData();
//				if (list != null && list.size() > 0)
//				{
//					fileListSize = dataHolder.getIntData();
//					Fileinfo file;
//					for (Object file1 : list)
//					{
//						file = (Fileinfo) file1;
//						files = new HashMap<String, Object>();
//						files.put("name", file.getFileName());
//						files.put("folder", file.isFold());
//						files.put("path", file.getPathInfo());
//						if (fileService.getSharedUser(file.getPathInfo()) != null
//								&& fileService
//										.getSharedUser(file.getPathInfo())
//										.size() > 0)
//						{
//							files.put("isShared", true);
//						}
//						else
//						{
//							files.put("isShared", false);
//						}
//						files.put("displayPath", file.getShowPath());
//						files.put(
//								"size",
//								file.isFold() ? "" : fileSizeOperation(file
//										.getFileSize() == null ? 0 : file
//										.getFileSize()));
//						try
//						{
//							files.put(
//									"modifyTime",
//									com.yozo.wapps.transmanager.weboffice.util.
//											.ftmDateToString(
//													"yyyy-MM-dd HH:mm:ss",
//													(file.getLastedTime() == null ? file
//															.getCreateTime()
//															: file.getLastedTime())));
//						}
//						catch (Exception e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						json.add(files);
//					}
//				}
//				retJson.put("fileListSize", fileListSize);
//				retJson.put("currentFolderPath", user.getSpaceUID() + "/"
//						+ FileConstants.DOC);
//				retJson.put("fileList", json);
//
//			}
			else if (fileListType.equals("searchGroupFile"))// 搜索企业文库//搜索群组文库
			{
				return BFilesOpeHandler.searchFile(req, resp, jsonParams, user);

			}
			else if (fileListType.equals("teamFile"))// 群组文档
			{
				List<Spaces> listS = fileService.getTeamSpacesByUserId(user.getId());
				for (Spaces temp : listS)
				{
					files = new HashMap<String, Object>();
					files.put("name", temp.getName());// +
					files.put("folder", true);
					files.put("id", temp.getTeam().getId());
					files.put("description", temp.getDescription());
					files.put("status", temp.getSpaceStatus());
					files.put("createrName", temp.getTeam() != null ? temp
							.getTeam().getUser().getRealName() : "");
					files.put("path", temp.getSpaceUID() + "/"
							+ FileConstants.DOC);
					files.put("spaceUID", temp.getSpaceUID());
					PermissionService service = (PermissionService) ApplicationContext
							.getInstance().getBean("permissionService");
					files.put("groupSpacePermission", service
							.getTeamSpacePermission(user.getId(), temp
									.getTeam().getId()));
					try
					{
						files.put("createDate", DateUtils.ftmDateToString(
								"yyyy-MM-dd HH:mm:ss", temp.getDate()));
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					json.add(files);
				}
				retJson.put("fileListSize", listS.size());
				retJson.put("fileList", json);
			}
			else if (fileListType.equals("companyFile"))// 企业文库文档
			{
				
				Spaces listS = fileService.findCompanySpaceByUserId(user.getId()); 
				path=listS.getSpaceUID()+"/"+ FileConstants.DOC;
				fileFlag=true;
//				files = new HashMap<String, Object>();
//				files.put("name", listS.getName());	
//				files.put("folder", true);
//				files.put("id", listS.getCompany().getId());
//				files.put("description", listS.getDescription());
//				files.put("status", listS.getSpaceStatus());
//				files.put("path", listS.getSpaceUID() + "/"	+ FileConstants.DOC);
//				files.put("spaceUID", listS.getSpaceUID());
//				PermissionService service = (PermissionService) ApplicationContext.getInstance().getBean("permissionService");
//				files.put("companySpacePermission", service.getCompanySpacePermission(user.getId()));
//				try
//				{
//					files.put("createDate", DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss", listS.getDate()));
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//				json.add(files);
//				retJson.put("fileListSize", 1);
//				retJson.put("fileList", json);			
			}
			else if (fileListType.equals("groupFile"))// 企业文库文档
			{
				fileFlag = true;
				//path = JCRService.COMPANY_ROOT + "/" + FileConstants.DOC;
				/*
				 * List<Spaces> listS = fileService.getGroupSpacesByUserId(user
				 * .getId()); for (Spaces temp : listS) { files = new
				 * HashMap<String, Object>(); files.put("name",
				 * temp.getName());// + files.put("folder", true);
				 * files.put("id", temp.getGroup().getId());
				 * files.put("description", temp.getDescription());
				 * files.put("status", temp.getSpaceStatus());
				 * files.put("createrName", temp.getGroup() != null ? temp
				 * .getGroup().getManager().getRealName() : "");
				 * files.put("path", temp.getSpaceUID() + "/" +
				 * FileConstants.DOC); files.put("spaceUID",
				 * temp.getSpaceUID()); files.put( "groupSpacePermission",
				 * BFilesOpeHandler.getGroupSpacePermission( user.getId(),
				 * temp.getGroup().getId())); try { files.put("createDate",
				 * DateUtils.ftmDateToString( "yyyy-MM-dd HH:mm:ss",
				 * temp.getDate())); } catch (Exception e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 * json.add(files); } retJson.put("fileListSize", listS.size());
				 * retJson.put("fileList", json);
				 */
			}
			else if (fileListType.equals("garbageFile"))// 回收站
			{
				int index = start != null && start >= 0 ? start : 0;
				int c = count != null && count >= 0 ? count : 1000000;

				// group_xxx_1347241135041/Recycler,如果选择group的话，需要传入group的space
				String recyclerpath = null;
				String spaceUid = (String) param.get("spaceUid");
				if (spaceUid != null)
				{
					recyclerpath = spaceUid + "/" + FileConstants.RECYCLER;// 有问题
					// getGroupSpacePermission
					if (!BFilesOpeHandler.canGroupOperationFileSystemAction(
							user.getId(), recyclerpath,
							FileSystemCons.BROWSE_FLAG))
					{
						return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
						/*resp.setHeader("Cache-Control",
								"no-store,no-cache,must-revalidate");
						resp.getWriter().write(error);
						return;*/
					}
				}
				else
				{
					recyclerpath = user.getSpaceUID() + "/"
							+ FileConstants.RECYCLER;
				}
				List list = jcrService.listPageFileinfos("", recyclerpath,
						index, c, sort, null, null, null);
				if (list != null && list.size() > 0)
				{
					fileListSize = Integer.parseInt(list.get(0).toString());
					list.remove(0);
					Fileinfo file;
					for (Object file1 : list)
					{
						file = (Fileinfo) file1;
						files = new HashMap<String, Object>();
						files.put("name", file.getFileName());
						files.put("folder", file.isFold());
						files.put("path", file.getPathInfo());
						files.put("isShared", file.isShared());
						files.put("displayPath", file.getShowPath());
						files.put(
								"size",
								file.isFold() ? "" : fileSizeOperation(file
										.getFileSize() == null ? 0 : file
										.getFileSize()));
						try
						{
							files.put(
									"modifyTime",
									apps.transmanager.weboffice.util.DateUtils
											.ftmDateToString(
													"yyyy-MM-dd HH:mm:ss",
													(file.getLastedTime() == null ? file
															.getDeletedTime()
															: file.getLastedTime())));
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						json.add(files);
					}
				}
				retJson.put("fileListSize", fileListSize);
				retJson.put("currentFolderPath", user.getSpaceUID() + "/"
						+ FileConstants.DOC);
				retJson.put("fileList", json);
			}
			else if (fileListType.equals("sharedFile"))// 他人共享
			{
//				String searchName = (String)param.get("searchName");
				String personIds = (String)param.get("personIds");
				if (personIds!=null)
				{
					System.out.println("personIds===="+personIds);
				}
				ArrayList<Long> userIds = new ArrayList<Long>();
				String selectedTime = (String)param.get("selectedTime");
//				if (selectedTime!=null)
//				{
//					System.out.println("selectedTime===="+selectedTime);
//				}
				String eSelectedTime = (String)param.get("eSelectedTime");
//				if (eSelectedTime!=null)
//				{
//					System.out.println("eSelectedTime===="+eSelectedTime);
//				}
				String selectedSize = (String)param.get("selectedSize");
//				if (selectedSize!=null)
//				{
//					System.out.println("selectedSize===="+selectedSize);
//				}
				if(personIds != null && personIds.length() > 0)
				{
					String[] personIdsList = personIds.split(",");				
					for (int i = 0;i < personIdsList.length;i++) {
						userIds.add(Long.valueOf(personIdsList[i]));
					}
				}else{
					userIds.add(0l);
				}
				int index = start != null && start >= 0 ? start : 0;
				int c = count != null && count >= 0 ? count : 1000000;
				if ("pclastChanged".equals(sort))//为了移动端默认按共享时间排序
				{
					sort="lastChanged";
				}
				else if ("lastChanged".equals(sort))
				{
					sort="sharedTime";
					order="DESC";
				}
				List list = fileService.getfilterOtherShare(user.getId(), userIds,
						index, c, sort, order,selectedTime,eSelectedTime,selectedSize);
				if (list != null && list.size() > 0)
				{
					fileListSize = Integer.parseInt(list.get(0).toString());
					list.remove(0);
					Fileinfo file;
					for (Object file1 : list)
					{
						file = (Fileinfo) file1;
						files = new HashMap<String, Object>();
						files.put("name", file.getFileName());
						files.put("folder", file.isFold());
						files.put("path", file.getPathInfo());
						files.put("isShared", true); // file.isShared());
						files.put("displayPath", file.getShowPath());
						files.put("sharer", file.getAuthor());
						files.put(
								"size",
								file.isFold() ? "" : fileSizeOperation(file
										.getFileSize() == null ? 0 : file
										.getFileSize()));
						files.put("shareId", file.getFileId());//共享文件的id
						files.put("sharerId", file.getSharerId());//共享人的id
						try
						{
							files.put(
									"modifyTime",
									apps.transmanager.weboffice.util.DateUtils
											.ftmDateToString(
													"yyyy-MM-dd HH:mm:ss",
													(file.getLastedTime() == null ? file
															.getCreateTime()
															: file.getLastedTime())));
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						files.put("sharedTime", apps.transmanager.weboffice.util.DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss", file.getShareTime()));
						json.add(files);
					}
				}
				retJson.put("fileListSize", fileListSize);
				retJson.put("currentFolderPath", user.getSpaceUID() + "/"
						+ FileConstants.DOC);
				retJson.put("fileList", json);
			}
			else if (fileListType.equals("sharingFile"))// 我的共享
			{
				int index = start != null && start >= 0 ? start : 0;
				int c = count != null && count >= 0 ? count : 1000000;
				long time=System.currentTimeMillis();
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				List list = fileSystemService.getMyShare(user.getSpaceUID(),
						user.getId(), "public", index, c, sort, "DESC");
				System.out.println("sharingFile time=================="+((System.currentTimeMillis()-time)/1000.0));
				if (list != null && list.size() > 0)
				{
					fileListSize = Integer.parseInt(list.get(0).toString());
					list.remove(0);
					Fileinfo file;
					for (Object file1 : list)
					{
						file = (Fileinfo) file1;
						files = new HashMap<String, Object>();
						files.put("name", file.getFileName());
						files.put("folder", file.isFold());
						files.put("path", file.getPathInfo());
						files.put("isShared", true);// file.isShared());
						files.put("displayPath", file.getShowPath());
						files.put(
								"size",
								file.isFold() ? "" : fileSizeOperation(file
										.getFileSize() == null ? 0 : file
										.getFileSize()));
						try
						{
							files.put(
									"modifyTime",
									apps.transmanager.weboffice.util.DateUtils
											.ftmDateToString(
													"yyyy-MM-dd HH:mm:ss",
													(file.getLastedTime() == null ? file
															.getCreateTime()
															: file.getLastedTime())));
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						json.add(files);
					}
				}
				retJson.put("fileListSize", fileListSize);
				retJson.put("currentFolderPath", user.getSpaceUID() + "/"
						+ FileConstants.DOC);
				retJson.put("fileList", json);
			}
			else if (fileListType.equals("sendAudit"))// 送审文档
			{
				return getReviewFilesOfSend(req,resp,jsonParams,user);
			}
			else if (fileListType.equals("endAudit"))// 审结文档
			{
				return getReviewFilesOfFiled(req,resp,jsonParams,user);
			}
			else if (fileListType.equals("waitAudit"))// 待审文档
			{
				return getReviewFilesOfTodo(req,resp,jsonParams,user);
			}
			else if (fileListType.equals("hadAudit"))// 已审文档
			{
				return getReviewFilesOfDone(req,resp,jsonParams,user);
			}
			if (fileFlag)
			{
				if(param.get("keyword")!=null){
					return BFilesOpeHandler.searchFile(req, resp, jsonParams, user); 
				}
				int index = start != null && start >= 0 ? start : 0;
				int c = count != null && count >= 0 ? count : 1000000;
				
				HashMap<String, Object> filetagjson = relationTagFile(user.getUserName());
				List list = jcrService.listPageFileinfos("", path, index, c,
						sort, order, filetagjson, filertag);
				if (list != null && list.size() > 0)
				{
					fileListSize = Integer.parseInt(list.get(0).toString());
					list.remove(0);
					Fileinfo file;
					for (Object file1 : list)
					{
						file = (Fileinfo) file1;
						files = new HashMap<String, Object>();
						files.put("tagname", file.getTag());
						files.put("name", file.getFileName());
						files.put("folder", file.isFold());
						files.put("path", file.getPathInfo());
						if ((fileService.getshareInfo(file.getPathInfo()) != null)
								&& (fileService
										.getshareInfo(file.getPathInfo())
										.size() > 0)
								|| (fileService.getAllNewShareinfo(file
										.getPathInfo()) != null)
								&& (fileService.getAllNewShareinfo(
										file.getPathInfo()).size() > 0))
						{
							files.put("isShared", true);
							files.put("sharer", file.getAuthor());
							files.put("sharedTime", apps.transmanager.weboffice.util.DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss", file.getShareTime()));
						}
						else
						{
							files.put("isShared", false);
						}
						files.put("displayPath", file.getShowPath());
						files.put(
								"size",
								file.isFold() ? "" : fileSizeOperation(file
										.getFileSize() == null ? 0 : file
										.getFileSize()));
						try
						{
							if (path.endsWith(FileConstants.RECYCLER))
							{
								files.put(
										"modifyTime",
										DateUtils.ftmDateToString(
												"yyyy-MM-dd HH:mm:ss",
												(file.getLastedTime() == null ? file
														.getDeletedTime()
														: file.getLastedTime())));
							}
							else
							{
								files.put(
										"modifyTime",
										DateUtils.ftmDateToString(
												"yyyy-MM-dd HH:mm:ss",
												(file.getLastedTime() == null ? file
														.getCreateTime() : file
														.getLastedTime())));
							}

						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						json.add(files);

					}
				}
				if(path.startsWith(FileConstants.TEAM_ROOT))
				{
					String spaceUID = path.substring(0, path.indexOf("/"));
					Spaces space = fileService.getSpace(spaceUID);
					retJson.put("currentLibName", space.getName());
							
				}
				retJson.put("fileListSize", fileListSize);
				retJson.put("currentFolderPath", path);
				retJson.put("fileList", json);
			}
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
	 * 获取文件标签关系
	 * @param userId
	 * @return
	 */
	public static HashMap<String, Object> relationTagFile(String userName)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryString = "select model.taginfo.tag,model.fileName from Filetaginfo as model where model.companyId=?";
		List filetag=(List)jqlService.findAllBySql(queryString, userName);
		HashMap<String, Object> filetagjson = new HashMap<String, Object>();
		for(int i = 0; i < filetag.size(); i++)
		{
			Object[] tt = (Object[]) filetag.get(i);
			String tf = (String) filetagjson.get((String) tt[1]);
			if(tf != null)
			{
				filetagjson.put((String) tt[1], tf + "," + tt[0]);
			}
			else
			{
				filetagjson.put((String) tt[1], tt[0]);
			}
		}
		return filetagjson;
	}
	
	/**
	 * 获取标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams,Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		List list = userService.getAllTags(user.getId());
		HashMap<String, Object> tagjson = new HashMap<String, Object>();
		tagjson.put("taglist", list);
		tagjson.put("tagsize", list.size());
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, tagjson);
	    return error;
	}
	/**
	 * 创建标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getCreateTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error, isN = null;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		List<String> names = (List<String>) param.get("name");		
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		if(names != null)
		{
			isN = userService.isNameEx(names, user.getId());
			if(isN != null)
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, isN);
			}else{
				userService.createTags(names, user.getId());
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
		    	List<Long> userIds = new ArrayList<Long>(0);
				userIds.add(user.getId());
		    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);//前台刷新
			}
		}else{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
	    return error;
	}
	
	/**
	 * 文件标签信息
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getAddTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		List<String> name = (List<String>) param.get("tagName");
		List<String> paths = (List<String>) param.get("filePath");
		if(paths != null && name != null){
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			if(userService.addTagFile(name, user.getId(), user.getUserName(), paths))
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, null);
			}else{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
		    	List<Long> userIds = new ArrayList<Long>(0);
				userIds.add(user.getId());
		    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);//前台刷新
			}		
		}else{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}	
	    return error;
	}
	
	/**
	 * 删除文件所有标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getDelTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		List<String> paths = (List<String>) param.get("fileName");
		if(paths != null){
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			userService.delFileTag(paths, user.getId());
			
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}else{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		
		return error;
	}
	
	/**
	 * 删除文件部分标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getDelLitTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String path = (String) param.get("filename");
		List<String> tags = (List<String>) param.get("tags");
		if(path != null && tags != null){
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			userService.delLitFileTag(path, tags, user.getUserName());
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}else{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		return error;
	}
	
	/**
	 * 删除用户标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String getDelTTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		List TagName = (List) param.get("delData");
		if(TagName != null){
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			userService.delTTag(TagName, user.getId());
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
	    	List<Long> userIds = new ArrayList<Long>(0);
			userIds.add(user.getId());
	    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);//前台刷新
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		return error;
	}
	
	/**
	 * 重命名标签
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws IOException
	 */
	public static String  getremTags(HttpServletRequest req,HttpServletResponse resp, HashMap<String, Object> jsonParams, Users user) throws IOException
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String tagName = (String) param.get("tagName");
		String newName = (String) param.get("newName");	
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		if(userService.isNameExOne(newName, user.getId())){
			error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, null);
		}else{
			userService.remTag(tagName, newName, user.getId());
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
	    	List<Long> userIds = new ArrayList<Long>(0);
			userIds.add(user.getId());
	    	messageService.sendMessage("CLOUD.handleHash", "userId", "", user.getId().intValue(), userIds);//前台刷新
		}	
		return error;
	}

	/**
	 * 获得共享日志
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getFileShareLog(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path"); // 文件或目录的路径
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			List list = fileService.getFileLog(path, user.getId());
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, list);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 取消共享
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String cancelShare(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			// /static/fileOpeService?jsonParams={method:"shareFiles",params:{paths:'+encodeURIComponent(config.paths)+',comment:"'+encodeURIComponent(comment)+'",info:'+encodeURIComponent(info)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			String[] paths = (String[]) pathslist.toArray(new String[pathslist
					.size()]);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);

			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);

			fileService.checkShare(paths);
			fileService.cancelShare(user.getId(), paths);
			fileService.deleteFilelog(paths, user.getId());
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getShareInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			String[] paths = (String[]) pathslist.toArray(new String[pathslist
					.size()]);
			String account = (String) param.get("account"); // 登录的账户

			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			HashMap<String, Object> backshare = new HashMap<String, Object>();
			ArrayList list = new ArrayList();
			ArrayList newList = new ArrayList();
			for (int i = 0; i < paths.length; i++)
			{
				list.addAll(fileService.getshareInfo(paths[i]));
				if (list.size() == 0)
				{
					list.addAll(fileService.getAllShareinfo(paths[i]));
				}
				if (list.size() == 0)
				{
					newList.addAll(fileService.getAllNewShareinfo(paths[i]));
				}
			}
			ArrayList slist = initData(list, paths);// 为了跟原来的一致，没有做优化
			ArrayList<String> datalist = changedata(slist, paths);// 为了跟原来的一致，没有做优化
			List backlist = getObject(datalist);// 为了跟原来的一致，没有做优化

			backshare.put("sharepermision", backlist);// 已经共享的权限
			if (slist.size() > 0)
			{
				backshare.put("shareComment", shareComment);// 共享备注
			}
			else
			{
				backshare.put("shareComment", "");// 共享备注
			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, backshare);
			if (newList.size() > 0)
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_PATH__ERROR,	null);
			}
			return error;
//
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 共享文件
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String shareFiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
		try
		{
			// /static/fileOpeService?jsonParams={method:"shareFiles",params:{paths:'+encodeURIComponent(config.paths)+',comment:"'+encodeURIComponent(comment)+'",info:'+encodeURIComponent(info)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			String[] paths = (String[]) pathslist.toArray(new String[pathslist
					.size()]);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			String sharecomment = (String) param.get("comment");// 共享注释
			List sharelist = (List) param.get("info");// 共享信息
			Boolean isCreatediscuss = Boolean.valueOf((String)param.get("isCreatediscuss"));//是否创建讨论组
			Boolean isInfo = Boolean.valueOf((String)param.get("isInfo"));//是否系统内消息提示
			Boolean isMobile = Boolean.valueOf((String)param.get("isMobile"));//是否手机短信提示
			List<Long> shareuserslist=new ArrayList<Long>();//共享的用户列表
			String sharetotal="";//共享的所有文件或目录
			String sharetotalname="";//共享的文件名，移动短信用的
//			long times=System.currentTimeMillis();
			for (String tempP : paths)
			{
				sharetotal+=tempP+";";
				if (isMobile)
				{
					sharetotalname+=tempP.substring(tempP.lastIndexOf("/"))+";";
				}
			}
			ArrayList<String> delIdList = (ArrayList<String>) param
					.get("info_del");
			List addIdList = (List) param.get("info_add");
			List modifyList = (List) param.get("info_modify");
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			HashMap<String, Object> backshare = new HashMap<String, Object>();

			// 处理共享，临时处理法，将原来的全部删除，再将所有共享信息插入到数据库
			ArrayList<HashMap> modifylist = new ArrayList<HashMap>();
			ArrayList<Personshareinfo> savelist = new ArrayList<Personshareinfo>();
			if (modifyList != null)
			{
				for (int i = 0; i < modifyList.size(); i++)
				{
					String shareinfo = (String) modifyList.get(i);
					String[] infos = shareinfo.split("-");
					Long userid = Long.parseLong(infos[1]);
					int permit = Integer.parseInt(infos[2]);
					/*
					 * Boolean readonly=Boolean.valueOf(infos[2]); Boolean
					 * write=Boolean.valueOf(infos[3]); Boolean
					 * download=Boolean.valueOf(infos[4]); if (readonly) {
					 * permit = FlagUtility.setIntFlag(permit,
					 * MainConstant.ISREAD_BIT, true); permit =
					 * FlagUtility.setIntFlag(permit, MainConstant.ISWRITE_BIT,
					 * false); } else if (write) { permit =
					 * FlagUtility.setIntFlag(permit, MainConstant.ISREAD_BIT,
					 * false); permit = FlagUtility.setIntFlag(permit,
					 * MainConstant.ISWRITE_BIT, true); } if (download) { permit
					 * = FlagUtility.setIntFlag(permit, MainConstant.ISDOWN_BIT,
					 * true); } else { permit = FlagUtility.setIntFlag(permit,
					 * MainConstant.ISDOWN_BIT, false); }
					 */
					Long shareid = Long.parseLong(infos[0]);
					for (String tempP : paths)
					{
						
						HashMap modifyInfo = new HashMap();
						modifyInfo.put("id", shareid);
						modifyInfo.put("permit", permit);
						modifylist.add(modifyInfo);
						shareid = shareid + 1;
					}
				}
			}
//			System.out.println(System.currentTimeMillis()-times);
//			times=System.currentTimeMillis();
			if (addIdList != null)
			{
				// for (String tempP : paths)
				// {
				// fileService.delPersonShareinfoByPath(tempP,user.getId());//临时这样，下午再改
				// }
				//
				for (String tempP : paths)
				{
					long index = -1;
					List<Personshareinfo> shareinfos = fileService.getAllShareinfo(tempP);
					for (int i = 0; i < addIdList.size(); i++)
					{
						String shareinfo = (String) addIdList.get(i);
						String[] infos = shareinfo.split("-");
						Long userid = Long.valueOf(infos[0]);
						shareuserslist.add(userid);
						int permit = Integer.parseInt(infos[1]);
					
						if ((shareinfos != null) && (shareinfos.size() > 0))
						{
							for (Personshareinfo share_info : shareinfos)
							{
								if (share_info.getUserinfoBySharerUserId().getId().longValue() == userid.longValue()
//										&& share_info.getShareFile().equalsIgnoreCase(tempP)//不需要
										)
								{
									index = share_info.getPersonShareId();
									break;
								}
							}
						}

						if (index != -1)
						{
							HashMap modifyInfo = new HashMap();
							modifyInfo.put("id", index);
							modifyInfo.put("permit", permit);
							modifylist.add(modifyInfo);
						}
						else
						{
							Personshareinfo personshareinfo = new Personshareinfo();
							personshareinfo.setCompanyId("public");
							personshareinfo.setDate(new Date());
							int dx = tempP.lastIndexOf('/');
							String aa = tempP.substring(dx + 1);
							int dd = aa.lastIndexOf('.');
							if (dd == -1)
							{
								personshareinfo.setIsFolder(1);
							}
							else
							{
								personshareinfo.setIsFolder(0);
							}
							personshareinfo.setIsNew(0);
							personshareinfo.setUserinfoByShareowner(user);
							personshareinfo.setShareFile(tempP);
							personshareinfo.setShareComment(sharecomment);
							personshareinfo
									.setUserinfoBySharerUserId(userService
											.getUser(userid));
							personshareinfo.setPermit(permit);
							// fileService.savePersonshareinfo(personshareinfo);
							savelist.add(personshareinfo);
						}
					}
				}
			}
//			System.out.println(System.currentTimeMillis()-times);
//			times=System.currentTimeMillis();
			fileService.setFileShareinfo(savelist, delIdList, modifylist,
					sharecomment, false);
//			System.out.println(System.currentTimeMillis()-times);
//			times=System.currentTimeMillis();
			if ((savelist == null || savelist.size() == 0)
					&& (delIdList == null || delIdList.size() == 0)
					&& (modifylist == null || modifylist.size() == 0))
			{
				// List<Personshareinfo> personshareinfos=new
				// ArrayList<Personshareinfo>();
				for (String tempP : paths)
				{
					List<Personshareinfo> shareinfos = fileService
							.getAllShareinfo(tempP);
					for (Personshareinfo personshareinfo : shareinfos)
					{
						if (sharecomment==null && personshareinfo.getShareComment()!=null)
						{
							personshareinfo.setShareComment(sharecomment);
							fileService.savePersonshareinfo(personshareinfo);
						}
						else if (!sharecomment.equalsIgnoreCase(personshareinfo.getShareComment()))
						{
							personshareinfo.setShareComment(sharecomment);
							// personshareinfos.add(personshareinfo);
							fileService.savePersonshareinfo(personshareinfo);
						}
					}
				}
				// fileService.savePersonshareinfos(personshareinfos);
			}
//			System.out.println(System.currentTimeMillis()-times);
//			times=System.currentTimeMillis();
			if(isCreatediscuss)
			{
				List<Users> userList = new ArrayList<Users>();
//				for (String tempP : paths)
//				{
					List<Personshareinfo> shareinfos = fileService.getAllShareinfo(paths[0]);
					if (shareinfos!=null) {
						for (Personshareinfo personshareinfo : shareinfos) {
							Users usr = personshareinfo.getUserinfoBySharerUserId();
							if(!userList.contains(usr)){
								userList.add(usr);
							}
						}
						
					}
				//}
				backshare.put("userList", userList);
			}
			if (isInfo)//系统内消息提示
			{
				//李孟生添加一下，看看原来怎么做的
			}
			if (isMobile)//发送手机短信
			{
				List<String> mobilelist=new ArrayList<String>();
				List<Long> templist=new ArrayList<Long>();
				for (int i=0;i<shareuserslist.size();i++)
				{
					Users tempuser=userService.getUser(shareuserslist.get(i));
					if (tempuser.getMobile()!=null && tempuser.getMobile().length()==11)
					{
						try
						{
							Long.parseLong(tempuser.getMobile());
							mobilelist.add(tempuser.getMobile());
							templist.add(0L);
						}
						catch (Exception e){}
					}
				}
				String content=user.getRealName()+"在政务协同办公系统共享了文件："+sharetotalname+"给您";
		    	Thread receiveT = new Thread(new BackgroundSend(mobilelist.toArray(new String[mobilelist.size()]),
		    			content,user.getCompany().getId(),user.getCompany().getName()
		    			,Constant.SHAREINFO,templist.toArray(new Long[templist.size()]),false,user));//这里的1先临时写死
				receiveT.start();

			}
//			System.out.println(System.currentTimeMillis()-times);
//			times=System.currentTimeMillis();
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, backshare);
			return error;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			return error;
		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 取消共享
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String cancelSharingFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			String[] paths = (String[]) pathslist.toArray(new String[pathslist
					.size()]);
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			fileService.cancelShare(user.getId(), paths);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	public static List getObject(ArrayList<String> userList)
	{
		int len = userList != null ? userList.size() : 0;
		int alen = 5;
		List gridData = new ArrayList();
		for (int i = 0; i < len; i++)
		{
			String info = userList.get(i);
			// Object[] pvalues=new
			// Object[6];//0为人员ID，1为人员名称，权限是从2开始，放如果权限增加可以扩展，将数组增加
			// 2为只读，选中为true，3为读写,选中为true,4为下载,选中为true
			// 2012-10-10:0为人员Id，1
			Object[] pvalues = new Object[4];// 0为共享Id，1位人员ID，2位人员名称，3为权限值
			int index = info.lastIndexOf(",");
			String useridname = info.substring(0, index);// 共享用户信息中间用"-"间隔
			// /^[^-]+-[^-]+-(.*?)-[^-]+$/
			String[] idnames = useridname.split("-",3);	//切分成3部分
			if (idnames != null && idnames.length > 1)
			{
				pvalues[0] = idnames[0];// 共享ID编号
				pvalues[1] = idnames[1];
				int tempIndex = idnames[2].lastIndexOf("-");
				pvalues[2] = idnames[2].substring(0,tempIndex);
			}
			Integer permit = Integer.parseInt(info.substring(index + 1));
			pvalues[3] = permit;
			/*
			 * // 权限 boolean isWrite = (permit & MainConstant.ISWRITE) != 0;
			 * pvalues[3]=Boolean.FALSE;//预分配值 pvalues[4]=Boolean.FALSE;
			 * pvalues[5]=Boolean.FALSE; if (!isWrite)//only { pvalues[3] =
			 * Boolean.TRUE;
			 * 
			 * } else//write { pvalues[4] = Boolean.TRUE; } if((permit &
			 * MainConstant.ISDOWN) != 0) { pvalues[5] = Boolean.TRUE; }
			 * 
			 * if((permit & MainConstant.ISAMEND) != 0)//加入了修订的权限 { pvalues[6] =
			 * "true"; } if((permit & MainConstant.ISAPPROVE) != 0)//加入了审阅的权限 {
			 * pvalues[7] = "true"; }
			 */
			gridData.add(pvalues);

		}
		return gridData;
	}

	private static String getPropertiesShareInfo(ArrayList<String> userList)
	{
		if (userList == null || userList.size() < 0)
		{
			return "";
		}
		int len = userList != null ? userList.size() : 0;
		int alen = 5;
		List gridData = new ArrayList();
		String header = "<table width = '200' style = 'table-layout :fixed'><tr><td width='70' nowrap='nowrap'><span style = 'font-size:12px;font-family:宋体'>共享用户</span></td>"
				+ "<td width='30' nowrap='nowrap'><span style='font-size:12px;font-family:宋体'>只读</span></td>"
				+ "<td width='30'nowrap='nowrap'><span style='font-size:12px;font-family:宋体'>读写</span></td>"
				+ "<td width='30'nowrap='nowrap'><span style='font-size:12px;font-family:宋体'>下载</span></td></tr>";
		String content = "";
		for (int i = 0; i < len; i++)
		{
			String info = userList.get(i);
			Object[] pvalues = new Object[6];// 0为人员ID，1为人员名称，权限是从2开始，放如果权限增加可以扩展，将数组增加
			// 2为只读，选中为true，3为读写,选中为true,4为下载,选中为true
			int index = info.lastIndexOf(",");
			String useridname = info.substring(0, index);// 共享用户信息中间用"-"间隔
			String[] idnames = useridname.split("-",3);
			if (idnames != null && idnames.length > 1)
			{
				pvalues[0] = idnames[0];// 共享ID编号
				pvalues[1] = idnames[1];
				int tempIndex = idnames[2].lastIndexOf("-");
				pvalues[2] = idnames[2].substring(0,tempIndex);
			}

			Integer permit = Integer.parseInt(info.substring(index + 1));
			// 权限
			boolean isWrite = (permit & MainConstant.ISWRITE) != 0;
			pvalues[3] = "";// 预分配值
			pvalues[4] = "";
			pvalues[5] = "";
			if (!isWrite)// only
			{
				pvalues[3] = "&nbsp;&nbsp;\u221A";

			}
			else
			// write
			{
				pvalues[4] = "&nbsp;&nbsp;\u221A";
			}
			if ((permit & MainConstant.ISDOWN) != 0)
			{
				pvalues[5] = "&nbsp;&nbsp;\u221A";
			}
			content += "<tr><td width='70'nowrap='nowrap'><span style='font-size:12px;font-family:宋体'>"
					+ pvalues[2]
					+ "</span></td>"
					+ "<td width='30'nowrap='nowrap'><span style = font-size:12px>"
					+ pvalues[3]
					+ "</span></td>"
					+ "<td width='30' nowrap='nowrap'><span style = font-size:12px>"
					+ pvalues[4]
					+ "</span></td>"
					+ "</td><td width='30' nowrap='nowrap'><span style = font-size:12px>"
					+ pvalues[5] + "</span></td></tr>";

		}
		content += "</table>";
		return header + content;
	}

	public static ArrayList<String> changedata(ArrayList oldGridFiles,
			String[] filesPath)
	{
		ArrayList<String> userList = new ArrayList<String>();
		int size = oldGridFiles == null ? 0 : oldGridFiles.size();
		if (size > 0)
		{

			String userName;
			String userEmail;
			String realName;
			String companyName;
			String department;

			int index;
			Users userinfo;
			for (int i = 0; i < size; i += filesPath.length)
			{
				if (oldGridFiles.get(i) instanceof Personshareinfo)
				{
					Personshareinfo shareinfo = (Personshareinfo) oldGridFiles
							.get(i);
					shareComment = shareinfo.getShareComment();
					userinfo = shareinfo.getUserinfoBySharerUserId();
					userName = userinfo.getUserName();

					realName = userinfo.getRealName();
					// department=userinfo.getDepartment();
					companyName = userinfo.getCompanyName();

					if (userName == null || userName.equals(""))
					{
						// userEmail= userinfo.getEmail();
						// index = userEmail.lastIndexOf("@");
						// if(index > 0)
						// {
						// userName = userEmail.substring(0, index);
						//
						// }
					}
					if (realName == null)
					{
						realName = userName;
					}
					/*
					 * if(department ==null) { department=""; }
					 */
					if (companyName == null)
					{
						companyName = "";
					}
					userList.add(shareinfo.getPersonShareId() + "-"
							+ userinfo.getId() + "-" + realName + "-"
							+ companyName + "," + shareinfo.getPermit());
					// userList.add(userName + "," + shareinfo.getPermit());

				}
				else if (oldGridFiles.get(i) instanceof Groupshareinfo)
				{
					Groupshareinfo shareinfo = (Groupshareinfo) oldGridFiles
							.get(i);
					userList.add(shareinfo.getFileShareId() + "-"
							+ shareinfo.getGroupinfo().getId() + "-"
							+ shareinfo.getGroupinfo().getName() + ","
							+ shareinfo.getPermit());
				}
				else if (oldGridFiles.get(i) instanceof NewPersonshareinfo)
				{
					NewPersonshareinfo shareinfo = (NewPersonshareinfo) oldGridFiles
							.get(i);
					shareComment = shareinfo.getShareComment();
					userinfo = shareinfo.getUserinfoBySharerUserId();
					realName = userinfo.getRealName();
					companyName = userinfo.getCompanyName();
					if (realName == null || "".equals(realName))
					{
						realName = userinfo.getUserName();
					}
					if (companyName == null)
					{
						companyName = "";
					}
					userList.add(shareinfo.getPersonShareId() + "-"
							+ userinfo.getId() + "-" + realName + "-"
							+ companyName + "," + shareinfo.getPermit());
				}
			}
		}
		return userList;
	}
	
	/*
	 * 信电局获取单一文件的备注
	 */
	private static ArrayList initComment(List nf, String[] filesPath)
	{
		// 根据filespath分组
		int pathLen = filesPath.length;
		int nfLen = nf.size();
		ArrayList result = new ArrayList();
		for (int j = 0; j < pathLen; j++)
		{
			ArrayList fs = new ArrayList();
			for (int i = 0; i < nfLen; i++)
			{
				if (nf.get(i) instanceof Personshareinfo)
				{
					Personshareinfo fsi = (Personshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						HashMap<String, Object> commentInfo = new HashMap<String, Object>();
			    		commentInfo.put("sharedRealname", fsi.getUserinfoBySharerUserId().getRealName());
			    		commentInfo.put("comment", fsi.getComment()!=null?fsi.getComment():"");
			    		try {
							commentInfo.put("date", apps.transmanager.weboffice.util.DateUtils
									.ftmDateToString("yyyy-MM-dd HH:mm:ss",fsi.getAddDate()));
						}
						catch(Exception e)
				        {
				            e.printStackTrace();
				        }
			    		if(fsi.getComment() != null)
			    		{
				    		result.add(commentInfo);			    			
			    		}
					}
				}
				else if (nf.get(i) instanceof Groupshareinfo)
				{
					Groupshareinfo fsi = (Groupshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						
					}
				}
				else if (nf.get(i) instanceof NewPersonshareinfo)
				{
					NewPersonshareinfo fsi = (NewPersonshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						HashMap<String, Object> commentInfo = new HashMap<String, Object>();
			    		commentInfo.put("sharedRealname", fsi.getUserinfoBySharerUserId().getRealName());
			    		commentInfo.put("comment", fsi.getComment()!=null?fsi.getComment():"");
			    		try {
							commentInfo.put("date", apps.transmanager.weboffice.util.DateUtils
									.ftmDateToString("yyyy-MM-dd HH:mm:ss",fsi.getAddDate()));
						}
						catch(Exception e)
				        {
				            e.printStackTrace();
				        }
			    		if(fsi.getComment() != null)
			    		{
				    		result.add(commentInfo);			    			
			    		}
					}
					;
				}
			}
		}
		return result;

	}

	public static ArrayList initData(List nf, String[] filesPath)
	{
		// 根据filespath分组
		int pathLen = filesPath.length;
		int nfLen = nf.size();
		int fslen = nfLen / pathLen;
		ArrayList<ArrayList> bag = new ArrayList<ArrayList>();
		ArrayList result = new ArrayList();
		for (int j = 0; j < pathLen; j++)
		{
			ArrayList fs = new ArrayList();
			// int fs
			for (int i = 0; i < nfLen; i++)
			// for(int i=j*fslen;i<(j+1)*fslen;i++)
			{
				if (nf.get(i) instanceof Personshareinfo)
				{
					Personshareinfo fsi = (Personshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						fs.add(fsi);
					}
				}
				else if (nf.get(i) instanceof Groupshareinfo)
				{
					Groupshareinfo fsi = (Groupshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						fs.add(fsi);
					}
				}
				else if (nf.get(i) instanceof NewPersonshareinfo)
				{
					NewPersonshareinfo fsi = (NewPersonshareinfo) nf.get(i);
					if (fsi != null && fsi.getShareFile().equals(filesPath[j]))
					{
						fs.add(fsi);
					}
					;
				}
			}
			if (fs.size() > 0)
			{
				bag.add(fs);
			}
		}
		// 无共享的文件path没有对象fileshare，所以长度不一致表示文件共享属性不一致

		if (bag.size() != pathLen)
		{
			return result;
		}
		int bagLen = bag.size();
		// 找出长度最短的为基准进行比较

		ArrayList minList = bag.get(0);
		for (int i = 0; i < bagLen; i++)
		{
			if (bag.get(i).size() < minList.size())
			{
				minList = bag.get(i);
			}
		}
		result = getSameSharer(bag, minList);
		return result;

	}

	private static ArrayList getSameSharer(ArrayList<ArrayList> shareList,
			ArrayList minList)
	{
		int len = minList.size();
		// int pathLen = filesPath.length;
		ArrayList result = new ArrayList();
		ArrayList temp = new ArrayList();
		if (shareList.size() == 1)
		{
			result = shareList.get(0);
			return result;
		}
		for (int i = 0; i < len; i++)
		{
			Object shareinfo = minList.get(i);
			temp.add(shareinfo);
			boolean isFound = false;
			for (int j = 0; j < shareList.size(); j++)
			{
				List list = shareList.get(j);
				if (list == minList)
				{
					continue;
				}
				isFound = false;
				for (int k = 0; k < list.size(); k++)
				{
					if (shareinfo instanceof Personshareinfo
							&& list.get(k) instanceof Personshareinfo)
					{
						Personshareinfo shareFile1 = (Personshareinfo) shareinfo;
						Personshareinfo shareFile0 = (Personshareinfo) list
								.get(k);
						if (shareFile1.getPermit().equals(
								shareFile0.getPermit())
								&& shareFile1
										.getUserinfoBySharerUserId()
										.getId()
										.equals(shareFile0
												.getUserinfoBySharerUserId()
												.getId()))
						{
							isFound = true;
							temp.add(shareFile0);
							break;
						}
					}
					else if (shareinfo instanceof Groupshareinfo
							&& list.get(k) instanceof Groupshareinfo)
					{
						Groupshareinfo shareFile1 = (Groupshareinfo) shareinfo;
						Groupshareinfo shareFile0 = (Groupshareinfo) list
								.get(k);
						if (shareFile1.getPermit().equals(
								shareFile0.getPermit())
								&& shareFile1
										.getGroupinfo()
										.getId()
										.equals(shareFile0.getGroupinfo()
												.getId()))
						{
							isFound = true;
							temp.add(shareFile0);
							break;
						}
					}
					else if (shareinfo instanceof NewPersonshareinfo)
					{
						NewPersonshareinfo shareFile1 = (NewPersonshareinfo) shareinfo;
						NewPersonshareinfo shareFile0 = (NewPersonshareinfo) list
								.get(k);
						if (shareFile1.getPermit().equals(
								shareFile0.getPermit())
								&& shareFile1.getGroupinfoOwner().equals(
										shareFile0.getGroupinfoOwner()))
						{
							isFound = true;
							temp.add(shareFile0);
							break;
						}
					}
				}
				if (isFound)
				{
					continue;
				}
				else
				{
					temp.clear();
					break;
				}

			}
			if (isFound)
			{
				for (int q = 0; q < temp.size(); q++)
				{
					result.add(temp.get(q));
				}
				temp.clear();
			}
		}
		return result;
	}

	public static String getPathPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String tag = (String) param.get("tag");
			boolean permitwrite = true;
			if(tag != null)
			{
				permitwrite = hasPermission(path, user, FileSystemCons.WRITE_FLAG);				
			}
			boolean permit = hasPermission(path, user,
					FileSystemCons.UPLOAD_FLAG);
			if (!permit && permitwrite)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
						null);
//				resp.setHeader("Cache-Control",
//						"no-store,no-cache,must-revalidate");
//				resp.getWriter().write(error);
//				return;
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
//				resp.setHeader("Cache-Control",
//						"no-store,no-cache,must-revalidate");
//				resp.getWriter().write(error);
//				return;
			}
			return error;
//		}
//		catch (Exception e)
//		{
//
//		}
	}

	/*
	 * 打开文件之前进行的权限判断
	 */
	public static String getReadPermission(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("filePath");
			boolean permit = hasPermission(path, user,
					FileSystemCons.READ_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
						null);
//				resp.setHeader("Cache-Control",
//						"no-store,no-cache,must-revalidate");
//				resp.getWriter().write(error);
//				return;
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
//				resp.setHeader("Cache-Control",
//						"no-store,no-cache,must-revalidate");
//				resp.getWriter().write(error);
//				return;
			}
			return error;
//		}
//		catch (Exception e)
//		{
//
//		}

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
	public static String uploadFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String fileName = (String) param.get("fileName");
			String replace = (String) param.get("replace");
			int isSameName = Integer.valueOf((String) param.get("isSameName"));
			if (path.startsWith("team_") && path.indexOf("/")<0)
			{
				path+="/Document";
			}
			
			boolean permit = hasPermission(path, user,
					FileSystemCons.UPLOAD_FLAG);
			if (!permit) // 无权限上传文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			//===添加serverchangerecords记录=====
			Client client=new Client();//调用Client中的程序
			client.addSerChangeRecords(path+'/'+fileName, fileName, 0);
           //==================================================//
			fileName = normalName(fileName);
			Fileinfo[] fileinfos = getFileList(null,
							String.valueOf(user.getId()),
							path);//读取已有的文件，为下步检查文件是否重名准备
			final List<String> exitesNames = new ArrayList<String>();
			int size = fileinfos.length;
			if (size > 0 && isSameName == 1)
			{
				final String[] newNames = new String[size];
				for (int i = 0; i < size; i++)
				{
					newNames[i] = fileinfos[i].getFileName();// ((Fileinfo)objFiles.get(i)).getFileName();
					exitesNames.add(newNames[i]);
				}				
				boolean exitFlag = false;
				if (exitesNames.contains(fileName))
				{
					exitFlag = true;
				}
				if (exitFlag)
				{
					return  JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR, null);
					/*resp.setHeader("Cache-Control","no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			}//t
			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			String tempName = System.currentTimeMillis() + fileName;
			List ret = fileUploadByHttpForm(req, tempPath, tempName);//将客户端文件写入服务器的临时文件夹，路径即为tempPath，存入后即删除此文件
			
			//上传所有用户文件，wch增加
			File rootFiles[] = File.listRoots();
			/*for(File tempf: rootFiles){
				System.out.println(tempf);
			}*/
		
			
			//上传所有用户文件
			if (ret != null)
			{
				if (ret.size() == 0)
				{
					error = JSONTools.convertToJson(ErrorCons.FILESIZE_ERROR, null);
				}
				else
				{
					FileSystemService fileSystemService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
					File file = new File(tempPath + tempName);//以下操作即是将此文件存入文件库
					InputStream fin = new FileInputStream(file);
					InputStream ois = new FileInputStream(file);
					
					Fileinfo info = fileSystemService.createFile(user.getId(),
							user.getRealName(), path, fileName, fin, ois,
							false, null, "1".equals(replace));
//						共享新传入的文件
//						if("user".equals(path.substring(0, path.indexOf("_"))))
//						{
//							int dx = path.lastIndexOf('/');
//							String[] paths = {path};
//							if(fileSystemService.isShare(paths) != 100)
//							{
//								List<Personshareinfo> fPersonshareinfo = new ArrayList<Personshareinfo>();
//								fPersonshareinfo = fileSystemService.getshareInfo(path);
//								Integer sharePermit = fPersonshareinfo.get(0).getPermit();
//								String shareComment = fPersonshareinfo.get(0).getShareComment();
//								Personshareinfo personshareinfo = new Personshareinfo();
//								personshareinfo.setCompanyId("public");
//								personshareinfo.setDate(new Date());
//								personshareinfo.setIsFolder(0);
//								personshareinfo.setIsNew(0);
//								personshareinfo.setUserinfoByShareowner(user);
//								personshareinfo.setShareFile(info.getPathInfo());
//								personshareinfo
//										.setUserinfoBySharerUserId(fPersonshareinfo.get(0).getUserinfoBySharerUserId());
//								personshareinfo.setPermit(sharePermit);
//								personshareinfo.setShareComment(shareComment);
//								fileSystemService.savePersonshareinfo(personshareinfo);
//							}
//						}
					String[] paths = {path};
					fileSystemService.shareNewFile(paths, user, info);
					fin.close();
					file.delete();

					if (path.startsWith("team_"))//协作共享的文件，需要增加是否阅读属性
					{
						changeTeamFiles(new String[]{path},true);//上传文件
					}
					
					HashMap<String, Object> files = new HashMap<String, Object>();
					files.put("name", info.getFileName());
					files.put("folder", info.isFold());
					files.put("path", info.getPathInfo());
					files.put("displayPath", info.getShowPath());
					files.put("size", info.isFold() ? ""
							: fileSizeOperation(info.getFileSize()));
					files.put("modifyTime", info.getLastedTime());
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
				}
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_FORM_ERROR, null);
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
	public static String uploadFileCon(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
		/*try
		{*/
			String c = req.getHeader("Content-Type");
			if (!OCTET_APP.equalsIgnoreCase(c))
			{
				return JSONTools.convertToJson(	ErrorCons.FILE_OCTET_STREAM_ERROR, null);
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String fileName = (String) param.get("fileName");
			String replace = (String) param.get("replace");
			boolean permit = hasPermission(path, user,
					FileSystemCons.UPLOAD_FLAG);
			if (!permit) // 无权限上传文件到path中
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			//===添加serverchangerecords记录=====
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
			String end = (String) param.get("end"); // 0表示没有结束，1表示结束文件的所有传输
			fileName = normalName(fileName);

			MD5 md5 = new MD5();
			String tempPath = WebConfig.tempFilePath + File.separatorChar;
			StringBuffer sb = new StringBuffer();
			sb.append(user.getUserName());
			sb.append(path);
			sb.append(token);
			sb.append(fileName);
			String tempName = md5.getMD5ofStr(sb.toString());

			String ret = uploadFile(req, tempPath, tempName, offset);

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
					if (path.startsWith("team_"))//协作共享的文件，需要增加是否阅读属性
					{
						changeTeamFiles(new String[]{path},true);//上传文件
					}
					if (zipFolder != null)
					{
						zipFolder.delete();
					}

					HashMap<String, Object> files = new HashMap<String, Object>();
					files.put("name", info.getFileName());
					files.put("folder", info.isFold());
					files.put("path", info.getPathInfo());
					files.put("displayPath", info.getShowPath());
					files.put("size", info.isFold() ? ""
							: fileSizeOperation(info.getFileSize()));
					files.put("modifyTime", info.getLastedTime());
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, files);
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
	 * 文件下载,先临时要检查权限，以后在下载之前统一将权限处理掉
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String downloadFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
	/*	try
		{*/

		String path = "";
		List<String> paths = null;
		String account = "";
		String filename="";
		Object temp;
		String copyurl = WebTools.converStr(req.getParameter(ServletConst.COPYURL));
		if (jsonParams != null)
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			temp = param.get("path");
			if (temp instanceof String)
			{
				path = (String)temp;
			}
			else
			{
				paths = (List<String>)temp;
			}
			filename = (String) param.get("filename");
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
				}
				account = "admin";// 外部下载全部记录在admin名下
			}
		}
		boolean permit;
		if (paths == null)     // 单文件下载
		{
			if (path.startsWith(FileConstants.AUDIT_ROOT) )//签批的文档是否有下载权限,按理应该查数据库的
			{
				permit=true;
			}
			else
			{
				permit = hasPermission(path, user,	FileSystemCons.DOWNLOAD_FLAG);
			}
			// if (!permit && copyurl == null) // 无权限下载
			if (!permit)
			{
				return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
			}
			else
			{
				getFileContent(req, resp, path,filename, false);
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
		}
		else    // 多文件或目录下载
		{
			for(String p : paths)
			{
				permit = hasPermission(p, user,	FileSystemCons.DOWNLOAD_FLAG);
				// if (!permit && copyurl == null) // 无权限下载
				if (!permit)
				{
					return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	p);
				}
			}
			getZipFileContent(req, resp, paths, false);
			
			// 采用多线程方法
			String[] t = new String[paths.size()];
			paths.toArray(t);
			Thread receiveT = new Thread(new InsertFileLog(t, user.getId(), 16, null, null));
			receiveT.start();
			return null;
		}
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
	
	public static String downloadFileNew(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{//新的下载方法，为了不影响原来的下载，重写了个方法
		String error=null;
	/*	try
		{*/

			List<String> path = null;
			String account = "";
			String filename="";
			String copyurl = WebTools.converStr(req
					.getParameter(ServletConst.COPYURL));
			if (jsonParams != null)
			{
				HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
						.get(ServletConst.PARAMS_KEY);
				path = (List<String>) param.get("path");
				filename = (String) param.get("filename");
				account = (String) param.get("account");
			}
			else
			{

				if (copyurl != null && copyurl.length() > 5)
				{
					FileSystemService fileSystemService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
					path = fileSystemService.getCopyPathsNew(copyurl);
					if (path == null)
					{
						return Constant.DOWNLOADERROR;
					}
					account = "admin";// 外部下载全部记录在admin名下
				}
			}
			boolean permit = false;
			if (path!=null)
			{
				for (int i=0;i<path.size();i++)//循环判断有没有下载权限
				{
					permit=hasPermission(path.get(i), user,FileSystemCons.DOWNLOAD_FLAG);
					if (!permit)
					{
						break;//只要有一个没有下载权限的文档就不给下载
					}
				}
			}
			// if (!permit && copyurl == null) // 无权限下载
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
			}
			else
			{
				if (path!=null && path.size()>0)
				{
					if (path.size()==1)//选中一个文件时与原来是一样的
					{
						getFileContent(req, resp, path.get(0),filename, false);
						// 采用多线程方法
						Thread receiveT = new Thread(new InsertFileLog(
								new String[] { path.get(0) }, user.getId(), 16, null, null));
						receiveT.start();
						return null;
					}
					else
					{
						
						
					}
				}
				return null;
			}
			return error;
	}
	/**
	 * 判断有没有下载权限，true有，false为无
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String downloadPermision(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error="false";//默认没有下载权限
		String path = "";
		List<String> paths = null;
		String account = "";
		String filename="";
		Object temp;
		String copyurl = WebTools.converStr(req.getParameter(ServletConst.COPYURL));
		if (jsonParams != null)
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
			temp = param.get("path");
			if (temp instanceof String)
			{
				path = (String)temp;
			}
			else
			{
				paths = (List<String>)temp;
			}
			account = (String) param.get("account");
		}
		else
		{

			if (copyurl != null && copyurl.length() > 5)
			{
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				path = fileSystemService.getCopyPaths(copyurl);
				if (path == null)
				{
					return Constant.DOWNLOADERROR;
				}
				account = "admin";// 外部下载全部记录在admin名下
			}
		}
		boolean permit;
		if (paths == null)     // 但文件下载
		{
			permit = hasPermission(path, user,	FileSystemCons.DOWNLOAD_FLAG);
			if (!permit)
			{
				error = "false";// JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,null);
			}
			else
			{
				error = "true";
			}
		}
		else    // 多文件下载
		{
			error = "true";
			for(String p : paths)
			{
				permit = hasPermission(p, user,	FileSystemCons.DOWNLOAD_FLAG);
				if (!permit)
				{
					error = "false";
					break;
				}
			}				
		}
		return error;
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
	public static String downloadFileCon(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		/*String error;
		try
		{*/
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account");
		String path = (String) param.get("path");
		boolean permit = hasPermission(path, user,
				FileSystemCons.DOWNLOAD_FLAG);
		if (!permit) // 无权限下载
		{
			return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
			/*resp.setHeader("Cache-Control",
					"no-store,no-cache,must-revalidate");
			resp.getWriter().write(error);
			return;*/
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
		JCRService jcrService = (JCRService) ApplicationContext
				.getInstance().getBean(JCRService.NAME);
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
	 * 创建文件夹
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String createFolder(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error = null;
		String path = null;
		try
		{
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			path = (String) param.get("path");
			if (!jcrService.isPathExist(path))
			{
				int index=path.lastIndexOf("/");
				Fileinfo folderFileinfo = jcrService.createFolder(user.getRealName(), path.substring(0,index), path.substring(index+1));
			}
			if (!BFilesOpeHandler.canGroupOperationFileSystemAction(
					user.getId(), path, FileSystemCons.NEW_FLAG))
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	"no permission");
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			if (path.indexOf("&&&")>=0)
			{
				return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,"不能使用2个以上&符号");
			}
			String name = (String) param.get("name");
			String replace = (String) param.get("replace");

			
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
					error = JSONTools.convertToJson(
							ErrorCons.FILE_SAME_NAME_ERROR, null);
				}
				if (error == null)
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			if (error == null)
			{
				boolean permit = hasPermission(path, user,
						FileSystemCons.NEW_FLAG);
				if (permit)
				{
					Fileinfo folderFileinfo = jcrService.createFolder(user.getRealName(), path, name);
					//如果在桌面文件夹中创建文件夹，需要在首页桌面中创建快捷方式
					if ((user.getSpaceUID()+"/Document/desktop").equals(path))
					{
						createDeskLink(path + "/" + name,false,true,user);
					}
					FileSystemService fileService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
					String[] paths = {path};
					fileService.shareNewFile(paths, user, folderFileinfo);
					//===添加serverchangerecords记录=====
					Client client=new Client();//调用Client中的程序
					client.addSerChangeRecords(path+'/'+name, name, 1);
		           //==================================================//
					
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				}
				else
				{
					error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
							null);
				}
			}
			if (error == null)
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
		}
		catch (ClassCastException e)
		{
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			if ("undefined".equals(path) || path == null || path.length() == 0)
			{
				error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
						null);
			}
			else
			{
				error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR,
						null);
			}
		}
		return error;
		/*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/
	}
	public static void createDeskLink(String path,boolean isshare,boolean folder,Users user)
	{//创建桌面快捷方式
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List list=jqlService.findAllBySql("select a from UserDesks as a where a.paths=?", path);
			if (list==null || list.size()==0)
			{
				UserDesks userDesks=new UserDesks();
		    	String displayname=path.substring(path.lastIndexOf("/")+1);
		    	userDesks.setDisplayname(displayname);
		    	userDesks.setIsshare(isshare);
		    	userDesks.setPaths(path);
		    	if (folder)
		    	{
		    		userDesks.setSourcetype(0);
		    	}
		    	else
		    	{
		    		userDesks.setSourcetype(1);
		    	}
		    	userDesks.setUser(user);
		    	jqlService.save(userDesks);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除或删除文件夹
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String delete(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException
	{
		String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			JSONArray jsonArray = new JSONArray();
			JSONArray jsonArray2 = new JSONArray();
			JSONObject jsonObject;
			List<String> tempPath = new ArrayList<String>();
			List<String> path = (List<String>) param.get("filePath");
			if (path==null)
			{
				path = (List<String>) param.get("paths");
			}
			// jsonArray2 = jsonArray.fromObject(path);
			// for (int i = 0; i < path.size(); i++)
			// {
			// jsonObject = jsonArray2.getJSONObject(i);
			// String delePath = (String) jsonObject.get("path");
			// tempPath.add(delePath);
			// }
			// path = tempPath;
			// String account = (String) param.get("account");
			JCRService jcrService = (JCRService) ApplicationContext
			.getInstance().getBean(JCRService.NAME);
			
			for (String temp : path)
			{
				boolean permit = hasPermission(temp, user,
						FileSystemCons.DELETE_FLAG);
				//文件是否被打开的标记
				boolean flag = FilesHandler.isFileOpened(null, temp) == null?true:false;
				String fileName = temp.substring(temp.lastIndexOf("/")+1);
				if (!permit || !flag)
				{
					if(flag)
					{
						error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
								"无操作权限 ");
					}
					else {
						error = JSONTools.convertToJson(ErrorCons.FILE_IS_BEING_OPENED,fileName);
					}
					return error;
					/*resp.setHeader("Cache-Control",
							"no-store,no-cache,must-revalidate");
					resp.getWriter().write(error);
					return;*/
				}
			    //===添加serverchangerecords记录=====
    			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    			String queryString = "update ServerChangeRecords set deleteDate=? where path=?";
    			jqlService.excute(queryString,new Date().getTime(),temp);	
    			
    			//删除文件对应的标签
    			String tName = temp.substring(temp.lastIndexOf("/Document/") + 9);
    			String queryString1 = "delete from Filetaginfo where fileName = ? and companyId = ?";
				jqlService.excute(queryString1, tName, user.getUserName());
    			if (temp.startsWith(user.getSpaceUID()+"/Document/desktop"))
    			{
    				queryString1 = "delete from UserDesks where paths = ? ";
    				jqlService.excute(queryString1, temp);
    			}
    			
				//如果是文件夹的话，还需要设置ServerChangeRecords表中文件夹下文件的删除时间 			
    			boolean b =jcrService.isFoldExist(temp);
    			if(true == b){
    			//删除文件夹下所有文件标签
					String queryString0 = "delete from Filetaginfo where fileName like ? and companyId = ?";
					jqlService.excute(queryString0, tName+"/%", user.getUserName());
					
					String queryString2 = "update ServerChangeRecords set deleteDate=? where path like ?";
					jqlService.excute(queryString2,new Date().getTime(),temp+"/%");
				//===================================
				}
			}
			
			// 更改处
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			String[] delPath = new String[path.size()];
			path.toArray(delPath);
			// 更改处
			fileService.cancelShare(user.getId(), delPath);

			jcrService.delete(user.getUserName(), delPath);
			PermissionService permissionService = (PermissionService) ApplicationContext
					.getInstance().getBean(PermissionService.NAME);
			permissionService.deleteFileSystemAction(delPath);
			if (delPath[0].startsWith("team_"))//协作共享的文件，更新总文件数
			{
				changeTeamFiles(delPath,false);//删除文件
			}
			LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
			for (String temp : path){
				logService.setFileLog(user, "", LogConstant.OPER_TYPE_DEL_FILE, temp);	
			}
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
	 * 文件或文件夹改名
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String rename(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException
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
			if(FilesHandler.isFileOpened(null, path) == null ? true : false)
			{
				if (jcrService.isPathExist(tempP + "/" + name))
				{
					error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR,
							"有同名文件或文件夹存在");
				}
				else if (name.indexOf("&&&")>=0)//这是版本比较多个文件之间的间隔符
				{
					error = JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR,
							"不能使用2个以上&符号");
				}
				else
				{
					MessagesService messageService = (MessagesService) ApplicationContext
							.getInstance().getBean(MessagesService.NAME);
					PermissionService permissionService = (PermissionService) ApplicationContext
							.getInstance().getBean(PermissionService.NAME);
					FileSystemService fileService = (FileSystemService) ApplicationContext
							.getInstance().getBean(FileSystemService.NAME);
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
						
						//修改标签对应的文件名
						String tnewName = newPath.substring(newPath.lastIndexOf("/Document/") + 9);
						String tName = path.substring(path.lastIndexOf("/Document/") + 9);
						
						String queryString0 = "update Filetaginfo set fileName = ? where fileName = ? and companyId = ?";
						jqlService.excute(queryString0, tnewName, tName, user.getUserName());
//						if (newPath.startsWith(user.getSpaceUID()+"/Document/desktop"))
		    			{
		    				String queryString1 = "update UserDesks set paths = ?,displayname=? where paths = ? ";
		    				jqlService.excute(queryString1, newPath,name,path);
		    			}
		     			//如果是文件夹的话，必须更新ServerChangeRecords表中该文件夹下所有文件的path和renameDate
						boolean b =jcrService.isFoldExist(newPath);//path路径已经不存在，newPath代替path判断是否为文件夹
		    			if(true == b){
							String queryString1 = "from ServerChangeRecords where path like ?";
							List<ServerChangeRecords> list = jqlService.findAllBySql(queryString1,path+"/%");	
							String path11;							
							for(ServerChangeRecords temp:list)
							{
								int index = path.length();//path=../aa  ;  temp.getPath()=../aa/bb/a.txt
								path11 = temp.getPath().substring(index + 1);//获取bb/a.txt
								jqlService.excute(queryString,new Date().getTime(),newPath+'/'+path11,temp.getPath());
								jqlService.excute(queryString0, tnewName+'/'+path11, tName+'/'+path11, user.getUserName());
							}
		    			}
		               //====================================================
						error = JSONTools.convertToJson(ErrorCons.NO_ERROR, "true");
					}
					else
					{
						error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
								"无操作权限 ");
					}
				}
			}
			else {
				error = JSONTools.convertToJson(ErrorCons.FILE_IS_BEING_OPENED,null);
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
	 * 复制文件或文件夹
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String copyFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{

		String error = null;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			String paths = (String) param.get("paths");// 选择的文件或目录路径，如多个，中间用分号间隔;
			String targetpath = (String) param.get("targetpath");// 目标文件夹
			int copyCount = Integer.parseInt((String) param.get("copyCount"));// 复制份数
			String[] result = new String[2];
			String[] srcpaths = paths.split(";");
			Boolean isJudgeTarget = Boolean.valueOf((String)param.get("isJudgeTarget"));
			Boolean isReplace = Boolean.valueOf((String)param.get("isReplace"));
			if (srcpaths != null && srcpaths.length > 0)
			{
				if (targetpath != null && targetpath.length() > 0)
				{
					Long permit = getFileSystemAction(user.getId(), targetpath, true);
					long pd = FileSystemCons.COPY_PASTE_FLAG;
					boolean flag = permit == null || permit == 0 ? false
							: FlagUtility.isValue(permit, pd);

					for (int i = 0; flag && i < srcpaths.length; i++)
					{
						Long srcpermit = getFileSystemAction(user.getId(), srcpaths[i],
										true);
						boolean srcflag = permit == null || permit == 0 ? false
								: FlagUtility.isValue(srcpermit, pd);
						if (!srcflag)
						{
							flag = false;
							break;
						}
					}

					if (flag)
					{
						final DataHolder srcPathHolder = new DataHolder();
						srcPathHolder.setStringData(srcpaths);
						// 先判断目标文件夹有没有权限，再进行复制或移动
						// 目标文件夹中有没有重名的
						Fileinfo[] fileinfos = getFileList(null,
										String.valueOf(user.getId()),
										targetpath);
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
						    for (int i = 0 ;isJudgeTarget && i < srcpaths.length;i++) {
						    	String srcFolderPath = srcpaths[i].substring(0,srcpaths[i].lastIndexOf("/"));
						    	Boolean isCurrentFolder = srcFolderPath.equals(targetpath)?true:false;
						    	result[0] = isCurrentFolder.toString();
								String fileName = srcpaths[i].substring(srcpaths[i].lastIndexOf("/")+1);
								result[1] = fileName;
								if(isCurrentFolder)
								{
									break;
								}
								if(exitesNames.contains(fileName))
								{
									return JSONTools.convertToJson(ErrorCons.FILE_SAME_NAME_ERROR,result);
									/*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
									resp.getWriter().write(error);
									return;*/
								}
							}
						}
						
						JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
							copyFiles(user.getId(),
									srcPathHolder, targetpath, copyCount,
									exitesNames,isReplace);
							JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
							String tPath = targetpath.substring(targetpath.lastIndexOf("/Document") + 9);
							for (String path : srcpaths)
							{
								String tName = path.substring(path.lastIndexOf("/Document/") + 9);
								String tNewName = path.substring(path.lastIndexOf("/"));
								String temppath=tPath + tNewName;
								
								String queryString1 = "insert into Filetaginfo (taginfo, fileName, companyId) select taginfo, '"+temppath+"', companyId " +
										"from Filetaginfo where fileName = ? and companyId = ?";
								jqlService.excute(queryString1, tName, user.getUserName());
								String queryString0 = "insert into Filetaginfo (taginfo, fileName, companyId) select taginfo, " +
										"CONCAT('"+temppath+"', SUBSTRING(fileName, LENGTH('"+tName+"')+1, LENGTH(fileName)-LENGTH('"+tName+"'))), companyId " +
										"from Filetaginfo where fileName like ? and companyId = ?";
								jqlService.excute(queryString0, tName+"/%", user.getUserName());
								
								
								if (targetpath.equals(user.getSpaceUID()+"/Document/desktop"))//目标文件夹为桌面时要创建桌面快捷方式
								{
									String link=targetpath+tNewName;
									if (jcrService.isFoldExist(link))
									{
										createDeskLink(link,false,true,user);//创建桌面快捷方式
									}
									else
									{
										createDeskLink(link,false,false,user);//创建桌面快捷方式
									}
								}
							}
							if (targetpath.startsWith("team_"))
							{
								changeTeamFiles(new String[]{targetpath},true);//复制文件
							}  
						// response.getWriter().write("true");
						error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
								"true");
					}
					else
					{
						// response.getWriter().write("notpermission");// 没有权限
						error = JSONTools.convertToJson(
								ErrorCons.PERMISSION_ERROR, "no permission");
					}
				}

			}
			return error;
		/*}
		catch (Exception e)
		{
			e.printStackTrace();
			// response.getWriter().write("false");// 复制失败
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
					"false");
		}
		// String result = JSONTools.convertToJson(ErrorCons.NO_ERROR, error);
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/

	}

	/**
	 * 移动文件或文件夹
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String moveFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error = null;
	/*	try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String userName = (String) param.get("account");
			String paths = (String) param.get("paths");// 选择的文件或目录路径，如多个，中间用分号间隔;
			String targetpath = (String) param.get("targetpath");// 目标文件夹
			String filetype = (String) param.get("filetype");// 公共空间或我的空间类别
			String[] srcpaths = paths.split(";");
			int replace = (Integer) param.get("replace");
			boolean beOpenFlag = true;
			boolean isHasPermit = true;
			String fileName = null;
			if (srcpaths != null && srcpaths.length > 0)
			{
				if (targetpath != null && targetpath.length() > 0)
				{
					Long permit = getFileSystemAction(user.getId(), targetpath, true);
					long pd = FileSystemCons.MOVE_FLAG;
					boolean flag = permit == null || permit == 0 ? false
							: FlagUtility.isValue(permit, pd);
					isHasPermit = flag;
					for (int i = 0; flag && i < srcpaths.length; i++)
					{
						Long srcpermit = getFileSystemAction(user.getId(), srcpaths[i],
										true);
						boolean srcflag = permit == null || permit == 0 ? false
								: FlagUtility.isValue(srcpermit, pd);
						isHasPermit = srcflag;
						beOpenFlag = FilesHandler.isFileOpened(null, srcpaths[i])==null?false:true;
						if (!srcflag || beOpenFlag)
						{
							fileName = srcpaths[i].substring(srcpaths[i].lastIndexOf("/")+1);
							flag = false;
							break;
						}
					}
					if (flag)
					{
						final DataHolder srcPathHolder = new DataHolder();
						srcPathHolder.setStringData(srcpaths);
						// 先判断目标文件夹有没有权限，再进行复制或移动
						// 目标文件夹中有没有重名的
						Fileinfo[] fileinfos = getFileList(null,
										String.valueOf(user.getId()),
										targetpath);
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

						JCRService jcrService = (JCRService) ApplicationContext
								.getInstance().getBean(JCRService.NAME);
						boolean exitFlag = false;
						// replace=1,代表不判断是否存在，覆盖之前存在的文件
						if (replace == 0)
						{
							for (String path : srcpaths)
							{
								Fileinfo file = jcrService.getFileInfo(path);
								if (exitesNames.contains(file.getFileName()))
								{
									exitFlag = true;
									break;
								}
							}
						}
						if (exitFlag)
						{
							error = JSONTools.convertToJson(
									ErrorCons.FILE_SAME_NAME_ERROR, "false");
						}
						else
						{
							// Constant.DOC_PUBLIC.equals(filetype)//公共空间
							moveFiles(filetype,
									user.getId(), srcPathHolder, targetpath,replace);
							// response.getWriter().write("true");
							// 更改处
							FileSystemService fileService = (FileSystemService) ApplicationContext
									.getInstance().getBean(FileSystemService.NAME);
							
							//删除文件对应的标签
							JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
							String tPath = targetpath.substring(targetpath.lastIndexOf("/Document") + 9);
							for (String path : srcpaths)
							{
								String tName = path.substring(path.lastIndexOf("/Document/") + 9);
								String tNewName = path.substring(path.lastIndexOf("/"));
								String temppath=tPath + tNewName;
//								if(filetype.equals("public"))
//								{
//								String queryString0 = "delete from Filetaginfo where fileName = ? and companyId = ?";
//								jqlService.excute(queryString0, tName, user.getUserName());
//								
//								String queryString1 = "delete from Filetaginfo where fileName like ? and companyId = ?";
//								jqlService.excute(queryString1, tName+"/%", user.getUserName());
//								replace(fileName,'"+ tName +"','"+ tPath +"')
//								}else
//								{
								String queryString1 = "update Filetaginfo set fileName= ? where fileName = ? and companyId = ?";
								jqlService.excute(queryString1, temppath, tName, user.getUserName());
								String queryString0 = "update Filetaginfo set fileName= CONCAT('"+temppath+"', SUBSTRING(fileName, LENGTH('"+tName+"')+1," +
										" LENGTH(fileName)-LENGTH('"+tName+"'))) where fileName like ? and companyId = ?";
								jqlService.excute(queryString0, tName+"/%", user.getUserName());
//								}
								if (targetpath.equals(user.getSpaceUID()+"/Document/desktop"))
								{
									String link=targetpath+tNewName;
									if (jcrService.isFoldExist(link))
									{
										createDeskLink(link,false,true,user);//创建桌面快捷方式
									}
									else
									{
										createDeskLink(link,false,false,user);//创建桌面快捷方式
									}
								}
								else//移到非桌面文件夹中，直接删除桌面快捷方式
								{
									if (path.startsWith(user.getSpaceUID()+"/Document/desktop"))
					    			{
					    				queryString1 = "delete from UserDesks where paths = ?  ";
					    				jqlService.excute(queryString1, path);
					    			}
								}
								
							}
							// 更改处
							List<String> pathsList = new ArrayList<String>();
							for (String path : srcpaths)
							{
								if ((fileService.getshareInfo(path) != null)
										&& (fileService.getshareInfo(path)
												.size() > 0))
								{
									pathsList.add(path);
								}
							}
							if (pathsList.size() > 0)
							{
								String[] cancel_paths = new String[pathsList
										.size()];
								for (int i = 0; i < pathsList.size(); i++)
								{
									cancel_paths[i] = pathsList.get(i);
								}
								fileService.cancelShare(user.getId(),
										cancel_paths);
							}
							if (targetpath.startsWith("team_") && !srcpaths[0].startsWith("team_"))//协作共享的文件，更新总文件数
							{
								changeTeamFiles(new String[]{targetpath},true);//移动文件
							}
							else if (srcpaths[0].startsWith("team_") && !targetpath.startsWith("team_"))
							{
								changeTeamFiles(srcpaths,false);//移除文件
							}
							error = JSONTools.convertToJson(ErrorCons.NO_ERROR,
									"true");
						}
					}
					else
					{
						// response.getWriter().write("notpermission");// 没有权限
						if(!isHasPermit)
						{
							return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
							/*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
							resp.getWriter().write(error);
							return;*/
						}
						else if(beOpenFlag)
						{
							return  JSONTools.convertToJson(ErrorCons.FILE_IS_BEING_OPENED,fileName);
							/*resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
							resp.getWriter().write(error);
							return;*/
						}						
					}
				}

			}
			return error;
		/*}
		catch (Exception e)
		{
			e.printStackTrace();
			// response.getWriter().write("false");// 移动失败
			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,
					"false");
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/

	}

	/**
	 * 新建文件 （文字处理WP、电子表格SS、简报制作PG）
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void CreatFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

	}

	/**
	 * 预览文件
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void previewFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

	}

	/**
	 * 获取文件属性
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getFileProperties(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		String error;
		/*try
		{*/
			HashMap<String, Object> retJson = new HashMap<String, Object>();
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String paths = (String) param.get("path");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			Fileinfo file = jcrService.getFileInfo(paths);
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			ArrayList list = new ArrayList();
			String pathArray[] = new String[1];
			pathArray[0] = paths;
			if ((fileService.getAllShareinfo(paths) != null)
					&& (fileService.getAllShareinfo(paths).size() != 0))
			{
				list.addAll(fileService.getshareInfo(paths));
			}
			else
			{
				if ((fileService.getAllNewShareinfo(paths) != null)
						&& (fileService.getAllNewShareinfo(paths).size() != 0))
				{
					list.addAll(fileService.getAllNewShareinfo(paths));
				}
			}
			ArrayList commentList = initComment(list, pathArray);//获取该文档的备注
			ArrayList slist = initData(list, pathArray);// 为了跟原来的一致，没有做优化
			ArrayList<String> datalist = changedata(slist, pathArray);// 为了跟原来的一致，没有做优化
			String shareInfo = getPropertiesShareInfo(datalist);// 为了跟原来的一致，没有做优化
			if (file != null)
			{
				retJson.put("fileName", file.getFileName());
				retJson.put("fileType", getFileType(file.getFileName()));
				retJson.put("showPath", file.getShowPath());
				retJson.put("fileSize", fileSizeOperation(file.getFileSize()));
				retJson.put("shareCommet", shareComment);
				retJson.put("author", file.getAuthor());
				retJson.put("createTime",
						DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
								file.getCreateTime()));
				retJson.put(
						"lastedTime",
						DateUtils.ftmDateToString("yyyy-MM-dd HH:mm:ss",
								file.getLastedTime()));
				retJson.put("sharepermision", shareInfo);// 已经共享的权限
				retJson.put("permitName", file.getPermitName());
				retJson.put("comment", commentList);
			}
			return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retJson);
		/*}
		catch (Exception e)
		{
			e.printStackTrace();
			// response.getWriter().write("false");// 移动失败
			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
		}
		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
		resp.getWriter().write(error);*/

	}

	/**
	 * 获取文件版本信息列表
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getFileVersions(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			List<Versioninfo> fileVersionList = jcrService.getAllVersion(path);
			HashMap<String, Object> versions;
			ArrayList versionListjson = new ArrayList();
			if (fileVersionList != null && fileVersionList.size() > 0)
			{
				for (Versioninfo f : fileVersionList)
				{
					versions = new HashMap<String, Object>();
					versions.put("versionName", f.getName()); // 版本号
					versions.put("status", f.getStatus()); // 状态
					versions.put("createTime", f.getCreateTime()); // 时间
					versions.put("createName", f.getCreateName()); // 创建者
					versions.put("remark", f.getRemark()); // 摘要内容
					versions.put("path", f.getPath()); // 下载路径
					versions.put("isRefVersion", f.isRefVersion());
					versionListjson.add(versions);
				}
			}
			return  JSONTools
					.convertToJson(ErrorCons.NO_ERROR, versionListjson);
/*		}
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
	 * 创建文件版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String creatFileVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				return  JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			String coment = (String) param.get("coment");
			String status = (String) param.get("status");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.createVersion(path, user.getRealName(), coment, status);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
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
	 * 恢复版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */

	public static String restoryVersions(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,		null);
				return error;
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			String coment = (String) param.get("coment");
			String versionName = (String) param.get("versionName");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.restoryVersions(path, versionName, user.getRealName(),
					coment);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
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
	 * 回滚版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String rollbackVersions(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				return error;
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			String coment = (String) param.get("coment");
			String versionName = (String) param.get("versionName");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.rollbackVersions(path, versionName, user.getRealName(),
					coment);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
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
	 * 删除版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String delVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
		/*try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,	null);
				return error;
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			String vNameStr = (String) param.get("vNameStr");
			List<String> vName = new ArrayList<String>();
			String[] vNames = vNameStr.split(",");
			for (int i = 0; i < vNames.length; i++)
			{
				vName.add(vNames[i]);
			}
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.delVersions(path, vName);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
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
		resp.getWriter().write(error);
*/
	}

	/**
	 * 删除所有版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String delAllVersions(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
/*		try
		{*/
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
						null);
				return error;
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.delAllVersions(path);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
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

	/***
	 * 修改摘要
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String updateVersionMemo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
						null);
				return error;
				/*resp.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				resp.getWriter().write(error);
				return;*/
			}
			String vName = (String) param.get("vName");
			String memo = (String) param.get("memo");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.updateVersionMemo(path, vName, memo);

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 定稿
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String finalizeVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{

		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			boolean permit = hasPermission(path, user,
					FileSystemCons.VERSION_FLAG);
			if (!permit)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
						null);
				return error;
//				resp.setHeader("Cache-Control",
//						"no-store,no-cache,must-revalidate");
//				resp.getWriter().write(error);
//				return;
			}
			String remark = (String) param.get("remark");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			jcrService.setNodeStatus(path, "1", FileConstants.STATUS);
			// 再创建个新版本
			remark = remark.replace("\n", "");
			remark = remark.replace("\r", "");
			jcrService.createVersion(path, user.getRealName(), remark, "定稿");

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 下载版本
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getVersion(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			String version = (String) param.get("version");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			InputStream is = jcrService.getVersionContent1(path, version);
			if (is == null)
			{
				error = JSONTools.convertToJson(
						ErrorCons.FILE_NO_VERSION_ERROR, null);
			}
			else
			{
				int index = path.lastIndexOf("/");
				String fileName = path.substring(index + 1);
				getFileContent(resp, is, fileName);
				error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			}
			return error;
//
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 添加或修改 群组
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String addTeamSpace(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String name = (String) param.get("name");// 群组名称
			String teamId = (String) param.get("teamId");// 群组Id(如果是修改组是用到)
			String description = (String) param.get("description"); // 群组描述
			String status = (String) param.get("status"); // 群组状态
			List<String> addUserIds = (List<String>) param.get("addUserIds");
			List<String> delUserIds = (List<String>) param.get("delUserIds");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);

			CustomTeams team = new CustomTeams();
			Spaces ss = new Spaces();
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			if (teamId != null && !teamId.equals(""))
			{
				team.setId(Long.parseLong(teamId));
				team.setName(name);
				team.setDescription(description);
				ss.setName(name);
				ss.setDescription(description);
				ss.setSpaceStatus(status);
				userService.addOrUpdateTeam(team, stringToLong(addUserIds),
						stringToLong(delUserIds), user.getId(), ss);
			}
			else
			{
				team.setName(name);
				ss.setName(name);
				ss.setDescription(description);
				ss.setSpaceStatus(status);
				// ss.SET
				Spaces sp = userService.addOrUpdateTeam(team, stringToLong(addUserIds),stringToLong(delUserIds), user.getId(), ss);
				//更新权限
				List<Roles> roles = userService.getRoles(-1,-1,sp.getTeam().getId());
				long roleId=0l;
				if(roles!=null && roles.size()>0){
					roleId=roles.get(roles.size()-1).getRoleId();
				}
				List<Long> addLongs = stringToLong(addUserIds);
				if(addLongs.contains(user.getId())){
					addLongs.remove(user.getId());
				}
				long[] addIds = new long[addLongs.size()];
				for (int i=0;i<addLongs.size();i++) {
					addIds[i]=addLongs.get(i).longValue();
				}
				userService.addUsersRoles(roleId,addIds);
			}

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 还原
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String undelete(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			int replace = (Integer) param.get("replace");
			String[] paths = (String[]) pathslist.toArray(new String[pathslist
					.size()]);
			boolean haspermission = checkRecyclerPermission(paths, user);
			/*if (!haspermission)
			{
				error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,"无操作权限 ");
			}
			else
			{*/
				JCRService jcrService = (JCRService) ApplicationContext
						.getInstance().getBean(JCRService.NAME);
				FileSystemService fileSystemService = (FileSystemService) ApplicationContext
						.getInstance().getBean(FileSystemService.NAME);
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				String userID = user.getUruid();

				ArrayList<String> list = jcrService.checkRestoreFile(userID,
						paths);
				ArrayList<String> list1 = jcrService.containSameinRestoreFile(
						userID, paths);
				list.addAll(list1);
				if (list.size() > 0 && replace == 0)
				{
					error = JSONTools.convertToJson(
							ErrorCons.FILE_SAME_NAME_ERROR, null);
				}
				else
				{
					if (paths != null && paths.length > 0)
					{
						ArrayList<String> sr = jcrService
								.undeleted(null, paths);
						int len = sr.size();
						String[] pathinfo = new String[len];
						LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
						for (int i = 0; i < len; i++)
						{
							String path = sr.get(i);
							pathinfo[i] = path.substring(path.indexOf('/') + 1);
							String path2 = path.substring(1,
									path.lastIndexOf('/'));
							String name = path
									.substring(path.lastIndexOf('/') + 1);
							fileSystemService.setNewShareFolder(user.getId(),
									path2, name, path, true);
					        //FileOperLog fileOperLog = new FileOperLog("", path,user.getUserName(), user.getRealName(), "","还原", "");
							//LogsUtility.logToFile(fileOperLog.getUserName(),DateUtils.ftmDateToString("yyyy-MM-dd",new Date()) + ".log", true,fileOperLog);
							logService.setFileLog(user, "", LogConstant.OPER_TYPE_RESTORE_FILES, path);
							//=======//如果是文件的话，只更新ServerChangeRecords表记录addDate
							JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);						
							String queryString = "update ServerChangeRecords set addDate=? where path=?";
							jqlService.excute(queryString,new Date().getTime(),path2+'/'+name);
							
			     			//如果是文件夹的话，必须更新ServerChangeRecords表中该文件夹下所有文件的addDate
							boolean b =jcrService.isFoldExist(path2+'/'+name);
			    			if(true == b){
								String queryString1 = "from ServerChangeRecords where path like ?";
								List<ServerChangeRecords> listsChangeRecords = jqlService.findAllBySql(queryString1,path2+'/'+name+"/%");							
								for(ServerChangeRecords temp:listsChangeRecords)
								{
									jqlService.excute(queryString,new Date().getTime(),temp.getPath());
								} 
			    			}
			               //====================================================
						}
					}
					error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
				}
//			}
				return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 清空或永久删除
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String clear(HttpServletRequest req, HttpServletResponse resp,
			HashMap<String, Object> jsonParams, Users user)
			throws Exception, IOException
	{
		return clearall(req, resp, jsonParams, user, null);
	}

	public static String clearall(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user, String type) throws Exception, IOException
	{
		// 清空文件和回收站调用
		String error;
//		boolean deletePermission = false;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> pathslist = (List<String>) param.get("paths"); // 文件或目录的路径
			String[] paths = null;
			if (pathslist != null)
			{
				paths = (String[]) pathslist.toArray(new String[pathslist
						.size()]);
//				 deletePermission = checkRecyclerPermission(paths, user);
			}
			// 权限判断
			if (paths != null 
					&& paths.length == 1
					&& paths[0].endsWith(FileConstants.RECYCLER)
					&& (paths[0].startsWith(FileConstants.COMPANY_ROOT)||paths[0].startsWith("team"))
					)
			{
				String spaceUID = paths[0].substring(0,
						paths[0].indexOf("/" + FileConstants.RECYCLER));
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				Long groupId = userService.getGroupIdBySpaceUID(spaceUID);
				Long spacepermission = BFilesOpeHandler
						.getGroupSpacePermission(user.getId(), groupId);
				boolean spacepermissionflag = FlagUtility.isValue(
						spacepermission, SpaceConstants.TRASH_FLAG);
				
				if (!spacepermissionflag)// 没有权限
				{
					error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
							"no permission");
					return error;
//					resp.setHeader("Cache-Control",
//							"no-store,no-cache,must-revalidate");
//					resp.getWriter().write(error);
//					return;
				}
			}
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
			if (paths != null && paths.length > 0)
			{
				if ("all".equals(type))
				{
					for (String path : paths)
					{
						jcrService.clearRecyler(path);
						logService.setFileLog(user, "", LogConstant.OPER_TYPE_DELETE_FILES,  path);
						
//						FileOperLog fileOperLog = new FileOperLog("", path,
//								user.getUserName(), user.getRealName(), "",
//								"永久删除", "");
//						LogsUtility
//								.logToFile(
//										fileOperLog.getUserName(),
//										DateUtils.ftmDateToString("yyyy-MM-dd",
//												new Date()) + ".log", true,
//										fileOperLog);
						// LogUtility.log(logDir, fileOperLog);
					}
					fileSystemService.delSignInfo(paths);
				}
				else
				{
					/*if(!deletePermission)
					{
						error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,
								"no permission");
						resp.setHeader("Cache-Control",
								"no-store,no-cache,must-revalidate");
						resp.getWriter().write(error);
						return;
					}*/
					jcrService.clear(user.getUruid(), paths);
					for (String path : paths)
					{
						logService.setFileLog(user, "", LogConstant.OPER_TYPE_DELETE_FILES,  path);
						/*FileOperLog fileOperLog = new FileOperLog("", path,
								user.getUserName(), user.getRealName(), "",
								"永久删除", "");
						LogsUtility
								.logToFile(
										fileOperLog.getUserName(),
										DateUtils.ftmDateToString("yyyy-MM-dd",
												new Date()) + ".log", true,
										fileOperLog);*/
						// LogUtility.log(logDir, fileOperLog);
					}
					fileSystemService.delSignInfo(paths);
				}
			}

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);

	}

	/**
	 * 修改自定义组内的成员和成员的角色
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String modifyUsersByTeamId(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String teamId = (String) param.get("teamId");// 群组Id(如果是修改组是用到)
			Long[] addUserIds = JSONTools.convert2LongArray(param
					.get("addUserIds"));
			List<String> roleIdsList = (List<String>) param.get("roleIds");
			Long[] roleIds = roleIdsList == null ? null : stringToLong(
					roleIdsList).toArray(new Long[0]);
			List<String> delUserIdsList = (List<String>) param
					.get("delUserIds");
			List<Long> delUserIds = stringToLong(delUserIdsList);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			userService.addOrUpdateTeamMembers(Long.parseLong(teamId),
					addUserIds, roleIds, delUserIds);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 获取自定义组内成员和成员所属角色
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getUsersByTeamId(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String teamId = (String) param.get("teamId");// 群组Id(如果是修改组是用到)
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			List<AdminUserinfoView> list = userService.getUserViewByTeamId(Long
					.parseLong(teamId));
			CustomTeams team = userService.findCustomTeamsById(Long
					.parseLong(teamId));
			ArrayList json = new ArrayList();
			for (AdminUserinfoView A : list)
			{
				HashMap<String, Object> userInfo = new HashMap<String, Object>();
				userInfo.put("id", A.getId());
				userInfo.put("realName", A.getRealName());
				userInfo.put("userName", A.getUserName());
				List<Roles> roleList = A.getRoles();
				if (roleList == null || roleList.size() < 1)
				{
					if (A.getUserId().longValue() == team.getUser().getId()
							.longValue())
					{
						userInfo.put("roleName", "创建者");
						userInfo.put("roleId", "");
					}
					else
					{
						userInfo.put("roleName", "无");
						userInfo.put("roleId", "");
					}
				}
				else
				{
					Roles role = roleList.get(0);
					if (role != null)
					{
						userInfo.put("roleName", role.getRoleName());
						userInfo.put("roleId", role.getRoleId().toString());
					}
					else
					{
						userInfo.put("roleName", "无");
						userInfo.put("roleId", "");
					}
				}
				userInfo.put("role", A.getRole());
				userInfo.put("email", A.getEmail());
				json.add(userInfo);
			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 删除群组
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String delCustomTeams(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> teamIds = (List<String>) param.get("teamIds");// 群组Id(如果是修改组是用到)
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			userService.delCustomTeams(stringToLong(teamIds));
			ArrayList json = new ArrayList();

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, json);
			return error;	
//	}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		catch (Exception ee)
//		{
//			error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 文件类型
	 * 
	 * @param text
	 * @return
	 */
	private static String getFileType(String fileName)
	{
		int index = fileName.lastIndexOf(".");
		if (index == -1)
		{
			return "未知文件类型";
		}
		String text = fileName.substring(index + 1, fileName.length());
		if (text.equals("eio"))
		{
			return "系统集成Office 文件";
		}
		if (text.equals("doc"))
		{
			return "Microsoft Word 文档";
		}
		if (text.equals("xls"))
		{
			return "Microsoft Office Excel 97-2003 工作表";
		}
		if (text.equals("ppt"))
		{
			return "Microsoft Office PowerPoint 97-2003 演示文稿";
		}
		if (text.equals("txt"))
		{
			return "文本文档";
		}
		if (text.equals("rtf"))
		{
			return "RTF 格式";
		}
		if (text.equals("gif"))
		{
			return "GIF 图像";
		}
		if (text.equals("png"))
		{
			return "PNG 图像";
		}
		if (text.equals("jpg"))
		{
			return "JPEG 图像";
		}
		if (text.equals("bmp"))
		{
			return "BMP 图像";
		}
		if (text.equals("pdf"))
		{
			return "Adobe Acrobat Document";
		}
		if (text.equals("htm") || text.equals("html"))
		{
			return "HTML Document";
		}
		if (text.equals("rar"))
		{
			return "WinRAR 压缩文件";
		}
		if (text.equals("zip"))
		{
			return "WinRAR ZIP 压缩文件";
		}
		if (text.equals("uof"))
		{
			return "中文办公软件标准格式";
		}
		if (text.equals("db"))
		{
			return "数据库文件";
		}
		if (text.equals("dot"))
		{
			return "Microsoft Word 模板";
		}
		if (text.equals("xlt"))
		{
			return "Microsoft Office Excel 模板";
		}
		if (text.equals("pot"))
		{
			return "Microsoft Office PowerPoint 97-2003 模板";
		}
		if (text.equals("eit"))
		{
			return "系统集成Office 模板文件";
		}
		if (text.equals("eiw"))
		{
			return "系统集成Office 文件";
		}
		if (text.equals("wmf"))
		{
			return "WMF 图像";
		}
		if (text.equals("emf"))
		{
			return "EMF 图像";
		}
		if (text.equals("tiff"))
		{
			return "Microsoft Office Document Imaging 文件";
		}
		if (text.equals("dbf"))
		{
			return "DBF 文件";
		}
		if (text.equals("pps"))
		{
			return "Microsoft Office PowerPoint 97-2003 Slide Show";
		}
		return text;
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
	private static void getFileContent(HttpServletRequest req,
			HttpServletResponse res, String path, boolean isOpen)
	{
		getFileContent(req,res, path,null, isOpen);
	}
	/**
	 * 获得需要文件的数据流。在response中返回文件的完整数据内容。
	 * 
	 * @param req
	 * @param res
	 */
	private static void getFileContent(HttpServletRequest req,
			HttpServletResponse res, String path,String filename, boolean isOpen)
	{
		try
		{
			res.setCharacterEncoding("utf-8");
			// res.setHeader("Content-Type", "utf-8");
			res.setContentType("application/octet-stream");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			// String path = WebTools.converStr(req.getParameter("path"));
			int index = path.lastIndexOf("/");
			String tempName = path.substring(index + 1);
			if (filename!=null)
			{
				tempName=filename;
			}
			tempName = checkDownloadName(req.getHeader("User-Agent"), tempName);  // user290 2012-11-02
			tempName = encodeDownloadName(req.getHeader("User-Agent"), tempName);
			
			res.setHeader("Content-Disposition", "attachment;filename=\""
					+ tempName + "\"");
			res.setHeader("errorCode", "0");

			InputStream in;
			if (path.startsWith("jcr:system"))
			{
				in = jcrService.getVersionContent(path, "1.0");
				// in=jcrService.getContent(path);//"system_audit_root/user_sah_1338897652562/2011.doc"
			}
			else
			{
				in = jcrService.getContent("", path, isOpen);
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
	private static void getZipFileContent(HttpServletRequest req,
			HttpServletResponse res, List<String> paths, boolean isOpen)
	{
		try
		{
			res.setCharacterEncoding("utf-8");
			res.setContentType("application/octet-stream");
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
			
			String tempName = "压缩包" + sdf.format(new Date()) + ".zip";
			tempName = encodeDownloadName(req.getHeader("User-Agent"), tempName);
			
			res.setHeader("Content-Disposition", "attachment;filename=\"" + tempName + "\"");
			res.setHeader("errorCode", "0");

			//res.setHeader("Content-Length", String.valueOf(in.available()));
			ZipOutputStream zipOut = new ZipOutputStream(res.getOutputStream());
			zipOut.setEncoding("GB2312");
			for (String p : paths)
			{
				zipFile(zipOut, p, "", jcrService);
			}
			zipOut.flush();
			zipOut.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private static void zipFile(ZipOutputStream out, String path, String base, JCRService jcrService)
	{
		try
		{
			Object child = jcrService.getFiles(path);			
			int index = path.lastIndexOf("/");
			String name = path.substring(index + 1);
			base = (base.length() > 0 ? base + "/" : "") + name;
			if (child == null)    // path no file or folder
			{
				return;
			}
			else if (child instanceof List)   // path is folder
			{
				List<String> paths = (List<String>)child;
				if (paths.size() <= 0)   // 空目录也压缩进
				{
					ZipEntry entry = new ZipEntry(base + "/");
					entry.setUnixMode(0644);
					out.putNextEntry(entry);
					out.closeEntry();
				}
				for (String p : paths)
				{
					zipFile(out, p, base, jcrService);
				}
				return;
			}
			
			InputStream in;			
			if (path.startsWith("jcr:system"))
			{
				in = jcrService.getVersionContent(path, "1.0");
			}
			else
			{
				in = jcrService.getContent("", path, false);
			}
			ZipEntry entry = new ZipEntry(base);
			entry.setUnixMode(0755);
			out.putNextEntry(entry);
			byte[] buffer = new byte[1024 * 5];
			int size;
			while ((size = in.read(buffer)) > 0)
			{
				out.write(buffer, 0, size);
			}
			in.close();
			out.closeEntry();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 文件下载名称转码
	 * 
	 * @param useragent
	 * @param name
	 * @return
	 */
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
	
	private static String checkDownloadName(String useragent, String name)
	{
		// user290 2012-11-02
		try
		{
			useragent = useragent.toLowerCase();
			String extendName = name.substring(name.lastIndexOf("."));
			String fileName = name.substring(0, name.lastIndexOf("."));
			if (useragent.indexOf("msie 6.") != -1)
			{
				if (fileName.length() > 16)
				{
					int len = fileName.length() - (fileName.length()-16) - 3;
					fileName = fileName.substring(0, len) + "..." + extendName;
					name = fileName;
				}
			}
			else if (useragent.indexOf("msie 7.") != -1)
			{
				if (fileName.length() > 148)
				{
					int len = fileName.length() - (fileName.length()-148) - 3;
					fileName = fileName.substring(0, len) + "..." + extendName;
					name = fileName;
				}
			}
			else if (useragent.indexOf("msie 8.") != -1)
			{
				if (fileName.length() > 148)
				{
					int len = fileName.length() - (fileName.length()-148) - 3;
					fileName = fileName.substring(0, len) + "..." + extendName;
					name = fileName;
				}
			}
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
		}
		return name;
	}

	/**
	 * 根据路径判断是否有操作的权限
	 * 
	 * @param path
	 * @param user
	 * @param target
	 * @return
	 */
	private static boolean hasPermission(String path, Users user, long target)
	{
		if (user == null)
		{
			return false;
		}
		if (path.indexOf(user.getSpaceUID()) != -1) // 个人空间或审批目录中的送审文档
		{
			return true;
		}
		if (path.startsWith(FileConstants.SIGN_ROOT))
		{
			FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			long permit = (long)fss.getPermitOfReviewFile(path, user.getId());
			return FlagUtility.isValue(permit, target);
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
				 && !path.startsWith("company_")
				) // 可能是共享共享的，临时这样处理
		{
			long p = hasShare(user.getId(), path, null, null);
			return FlagUtility.isValue(p, target);
		}
		
		PermissionService service = (PermissionService) ApplicationContext
				.getInstance().getBean("permissionService");
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
				if (!tempback)
				{
					tempback = FlagUtility.isValue(FileSystemCons.DOWNLOAD_SET,
						target);
				}
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
							// return null;
							List<String> flag = new ArrayList<String>();
							return flag;
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

	/**
	 * 直接从请求流中获取文件内容
	 * 
	 * @param request
	 * @param path
	 * @param name
	 * @param offset
	 *            文件偏移位置
	 * @return
	 */
	private static String uploadFile(HttpServletRequest request, String path,
			String name, long offset)
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
			while ((a = in.read(b)) > 0)
			{
				out.write(b, 0, a);
			}
			out.close();
			in.close();
			return name;
		}
		catch (Exception e)
		{
			LogsUtility.error(e);
			return null;
		}
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

	/**
	 * 处理文件大小 转换成kb 或MB
	 * 
	 * @param filesSize
	 * @return
	 */
	public static String fileSizeOperation(Long filesSize)
	{
		if (filesSize == null || filesSize == 0)
		{
			return "";
		}
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

		return size2;

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
	 * 获取上传文件路径的文件列表。
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getFileForUpload(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			ArrayList<String> list = (ArrayList<String>) jcrService
					.getFileForUpload(path);
			HashMap<String, Object> retJson = new HashMap<String, Object>();
			String fileListStr = "";
			if (list != null && list.size() > 0)
			{
				int len = list.size();
				for (int i = 0; i < len; i++)
				{
					if (i > 0)
					{
						fileListStr += "?" + list.get(i);
					}
					else
					{
						fileListStr += list.get(i);
					}
				}
			}
			retJson.put("fileList", fileListStr);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, retJson);
			return error;
//		}
//		catch (Exception e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getDrafts(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getDrafts",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			Integer isSendSign = (Integer) param.get("isSendSign");//是否是送签
			String personIds = (String)param.get("personIds");//筛选的人员
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = SignUtil.instance().getDrafts(
					start, count, sort, order, user, userIds, searchName, isSendSign);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	/**
	 * 已送签批数据，已送签批、批阅、协作
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getDone(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用,获取已办列表
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			String fileflowid = (String)param.get("fileflowid");//流水号
			String filetype = (String)param.get("filetype");//文件类型
			String fromunit = (String)param.get("fromunit");//来文单位
			String filecode = (String)param.get("filecode");//文号
			String successdate = (String)param.get("successdate");//成文时间段
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "sendtime";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = SignUtil.instance().getDone(start,
					count, sort, order, user,userIds,selectedTime,searchName
					,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getCollect(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，获取收藏列表
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		String personIds = (String)param.get("personIds");//筛选的人员
		String selectedTime = (String)param.get("selectedTime");//筛选的时间段
		String searchName = (String)param.get("searchName");//筛选的名称
		String fileflowid = (String)param.get("fileflowid");//流水号
		String filetype = (String)param.get("filetype");//文件类型
		String fromunit = (String)param.get("fromunit");//来文单位
		String filecode = (String)param.get("filecode");//文号
		String successdate = (String)param.get("successdate");//成文时间段
		ArrayList<Long> userIds = new ArrayList<Long>();
		if(personIds != null && personIds.length() > 0)
		{
			String[] personIdsList = personIds.split(",");				
			for (int i = 0;i < personIdsList.length;i++) {
				userIds.add(Long.valueOf(personIdsList[i]));
			}
		}else{
			userIds = null;
		}
		if (user == null)
		{
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			user = userService.getUser(account);
		}
		String sort = (String) param.get("sort"); // 排序类型
		if (sort == null || sort.equals(""))
		{
			sort = "sendtime";
		}
		String order = (String) param.get("order"); // 排序类型
		if (order == null || order.equals(""))
		{
			order = "desc";
		}
		HashMap<String, Object> result = SignUtil.instance().getCollect(start,
				count, sort, order, user,userIds,selectedTime,searchName
				,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		return error;
	}
	public static String getTodo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用，根据当前账号获取待办列表（收到的所有未处理的事务，返还给送文人的事务也算的）
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			String fileflowid = (String)param.get("fileflowid");//流水号
			String filetype = (String)param.get("filetype");//文件类型
			String fromunit = (String)param.get("fromunit");//来文单位
			String filecode = (String)param.get("filecode");//文号
			String successdate = (String)param.get("successdate");//成文时间段

			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "sendtime";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = SignUtil.instance().getTodo(start,
					count, sort, order, user, userIds, selectedTime,searchName
					,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getToread(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用,获取收阅列表
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			Integer isToread = (Integer) param.get("isToread");//是收阅还是送阅
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			String fileflowid = (String)param.get("fileflowid");//流水号
			String filetype = (String)param.get("filetype");//文件类型
			String fromunit = (String)param.get("fromunit");//来文单位
			String filecode = (String)param.get("filecode");//文号
			String successdate = (String)param.get("successdate");//成文时间段
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "sendtime";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = SignUtil.instance().getToread(
					start, count, sort, order, user, userIds, selectedTime
					,isToread,searchName,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getHadread(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用,获取收阅列表
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		Integer isToread = (Integer) param.get("isToread");//是收阅还是送阅
		String personIds = (String)param.get("personIds");//筛选的人员
		String selectedTime = (String)param.get("selectedTime");//筛选的时间段
		String searchName = (String)param.get("searchName");//筛选的名称
		String fileflowid = (String)param.get("fileflowid");//流水号
		String filetype = (String)param.get("filetype");//文件类型
		String fromunit = (String)param.get("fromunit");//来文单位
		String filecode = (String)param.get("filecode");//文号
		String successdate = (String)param.get("successdate");//成文时间段
		ArrayList<Long> userIds = new ArrayList<Long>();
		if(personIds != null && personIds.length() > 0)
		{
			String[] personIdsList = personIds.split(",");				
			for (int i = 0;i < personIdsList.length;i++) {
				userIds.add(Long.valueOf(personIdsList[i]));
			}
		}else{
			userIds = null;
		}
		
		if (user == null)
		{
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			user = userService.getUser(account);
		}
		String sort = (String) param.get("sort"); // 排序类型
		if (sort == null || sort.equals(""))
		{
			sort = "sendtime";
		}
		String order = (String) param.get("order"); // 排序类型
		if (order == null || order.equals(""))
		{
			order = "desc";
		}
		HashMap<String, Object> result = SignUtil.instance().getHadread(
				start, count, sort, order, user, userIds, selectedTime
				,isToread,searchName,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		return error;
	}
	// public static void getHadWorks(HttpServletRequest req,
	// HttpServletResponse resp, HashMap<String, Object> jsonParams,Users user)
	// throws ServletException, IOException
	// {
	// //孙爱华增加的，移动也可直接调用
	// String error;
	// try
	// {
	// //static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
	// HashMap<String, Object> param = (HashMap<String, Object>)
	// jsonParams.get(ServletConst.PARAMS_KEY);
	// String account = (String) param.get("account"); //登录的账户
	// Integer start = (Integer) param.get("start"); // 分页起始位置
	// Integer count = (Integer) param.get("count"); // 每页显示的数量
	//
	// if (user==null)
	// {
	// UserService userService =
	// (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	// user=userService.getUser(account);
	// }
	// String sort = (String) param.get("sort"); // 排序类型
	// if(sort==null||sort.equals(""))
	// {
	// sort = "";
	// }
	// String order = (String) param.get("order"); // 排序类型
	// if(order==null||order.equals(""))
	// {
	// order = "asc";
	// }
	//
	// HashMap<String, Object>
	// result=SignUtil.instance().getHadWorks(start,count,sort,order,user);//获取草稿数据
	// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	//
	// }
	// catch (ClassCastException e)
	// {
	// error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
	// }
	// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	// resp.getWriter().write(error);
	// }
	public static String getEndWorks(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String sSelectedTime = (String)param.get("sSelectedTime");//筛选的送文时间段
			String eSelectedTime = (String)param.get("eSelectedTime");//筛选的办结时间段
			Integer isFinished = (Integer)param.get("isFinish");//是否完结
			String searchName = (String)param.get("searchName");//筛选的名称
			String fileflowid = (String)param.get("fileflowid");//流水号
			String filetype = (String)param.get("filetype");//文件类型
			String fromunit = (String)param.get("fromunit");//来文单位
			String filecode = (String)param.get("filecode");//文号
			String successdate = (String)param.get("successdate");//成文时间段
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "sendtime";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}

			HashMap<String, Object> result = SignUtil.instance().getEndWorks(
					start, count, sort, order, user,sSelectedTime,eSelectedTime,isFinished,searchName
					,fileflowid,filetype,fromunit,filecode,successdate,req
					);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getAuditFileTypes(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，获取签批的文档类别
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		String modifytype = (String) param.get("type");//文件类型名称
//		String sSelectedTime = (String)param.get("sSelectedTime");//筛选的送文时间段
//		String eSelectedTime = (String)param.get("eSelectedTime");//筛选的办结时间段
//		Integer isFinished = (Integer)param.get("isFinish");//是否完结
//		String searchName = (String)param.get("searchName");//筛选的名称
//
//		if (user == null)
//		{
//			UserService userService = (UserService) ApplicationContext
//					.getInstance().getBean(UserService.NAME);
//			user = userService.getUser(account);
//		}
//		String sort = (String) param.get("sort"); // 排序类型
//		if (sort == null || sort.equals(""))
//		{
//			sort = "sendtime";
//		}
//		String order = (String) param.get("order"); // 排序类型
//		if (order == null || order.equals(""))
//		{
//			order = "desc";
//		}
		
		HashMap<String, Object> result = new HashMap<String, Object>();// 获取草稿数据
		List<String[]> list=SignUtil.instance().getFiletypes(modifytype,user);
//		list.add(new String[]{"1","文件阅办","pic1"});
//		list.add(new String[]{"2","领导活动","pic2"});
//		list.add(new String[]{"3","内部明电","pic3"});
//		list.add(new String[]{"4","请示报告","pic4"});
//		list.add(new String[]{"5","公文审签","pic5"});
//		list.add(new String[]{"6","无锡日报","pic6"});
//		list.add(new String[]{"7","领导信箱","pic7"});
		result.put("fileList", list);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		return error;

	}
	public static String getMyquestfiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用,我的送文列表
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			Integer isSendSign = (Integer) param.get("isSendSign");//是收阅还是送阅
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			String fileflowid = (String)param.get("fileflowid");//流水号
			String filetype = (String)param.get("filetype");//文件类型
			String fromunit = (String)param.get("fromunit");//来文单位
			String filecode = (String)param.get("filecode");//文号
			String successdate = (String)param.get("successdate");//成文时间段
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "sendtime";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = SignUtil.instance()
					.getMyquestfiles(start, count, sort, order, user,userIds, selectedTime, searchName, isSendSign
					,fileflowid,filetype,fromunit,filecode,successdate,req);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	// public static void getSuccessfiles(HttpServletRequest req,
	// HttpServletResponse resp, HashMap<String, Object> jsonParams,Users user)
	// throws ServletException, IOException
	// {
	// //孙爱华增加的，移动也可直接调用,获取成文列表
	// String error;
	// try
	// {
	// //static/fileOpeService?jsonParams={method:"getHadSends",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
	// HashMap<String, Object> param = (HashMap<String, Object>)
	// jsonParams.get(ServletConst.PARAMS_KEY);
	// String account = (String) param.get("account"); //登录的账户
	// Integer start = (Integer) param.get("start"); // 分页起始位置
	// Integer count = (Integer) param.get("count"); // 每页显示的数量
	//
	// if (user==null)
	// {
	// UserService userService =
	// (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	// user=userService.getUser(account);
	// }
	// String sort = (String) param.get("sort"); // 排序类型
	// if(sort==null||sort.equals(""))
	// {
	// sort = "";
	// }
	// String order = (String) param.get("order"); // 排序类型
	// if(order==null||order.equals(""))
	// {
	// order = "asc";
	// }
	// HashMap<String, Object>
	// result=SignUtil.instance().getSuccessfiles(start,count,sort,order,user);//获取草稿数据
	// error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
	//
	// }
	// catch (ClassCastException e)
	// {
	// error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
	// }
	// resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
	// resp.getWriter().write(error);
	// }
	public static String getApprovalinfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，根据ID获取流程的相关信息
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			Map<String, Object> result = SignUtil.instance().getCurrentPermit(
					Long.valueOf(id), user);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getWebcontent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，根据ID获取流程的事务详情
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			String type = (String) param.get("type");
			int typevalue = 0;// 签批中事务详情
			if ("1".equals(type))
			{
				typevalue = 1;// 草稿中查看
			}
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			String result = SignUtil.instance().getWebcontent(Long.valueOf(id),
					typevalue);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getNewListMessages(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，根据当前用户获取提醒信息列表
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			List result = SignUtil.instance().getNewListMessages(user);// 获取信息列表
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getNewListMessagesNum(HttpServletRequest req,
	        HttpServletResponse resp, HashMap<String, Object> jsonParams,
	        Users user) throws ServletException, IOException
    {
    String error;
//	      try
//	      {
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account"); // 登录的账户

        if (user == null)
        {
            UserService userService = (UserService) ApplicationContext
                    .getInstance().getBean(UserService.NAME);
            user = userService.getUser(account);
        }

        List result = SignUtil.instance().getNewListMessages(user);
        error = JSONTools.convertToJson(ErrorCons.NO_ERROR, String.valueOf(result.size()));
        return error;
    }
	public static String signreal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 签收设置
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = SignUtil.instance().signreal(id, user);// 签收设置
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getSignReal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 签收设置
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = SignUtil.instance().getSignReal(id, user);// 获取签收情况
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String saveDraft(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 移动的保存和送签入口
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sendsigntag = (String) param.get("sendsigntag");// 送签选中标识
			String sendreadtag = (String) param.get("sendreadtag");// 送阅选中标识
			String title = (String) param.get("title");// 标题
			List<String> sendfiles = (List<String>) param.get("sendfiles");// 送审文件
			List<String> sendfilenames = (List<String>) param
					.get("sendfilenames");// 送审文件名
			String webcontent = (String) param.get("webcontent");// 网页内容
			List<String> accepters = (List<String>) param.get("accepters");// 签收人
			String issame = (String) param.get("issame");// 是否会签
			String sendreaders = (String) param.get("sendreaders");// 送阅人，用,间隔
			Long fileflowid=null;
			String filetype=(String) param.get("filetype");//文件类别
			String temp=(String) param.get("fileflowid");//文件流水号
			if (temp!=null)
			{
				fileflowid=Long.valueOf(temp);
			}
			Date filesuccdate=(Date) param.get("filesuccdate");//成文日期

			String fromunit=(String) param.get("fromunit");//来文单位
			String filecode=(String) param.get("filecode");//文号
			String filescript=(String) param.get("filescript");//文件备注
			
			String backsigners=(String) param.get("backsigners");//会签后处理人，用,间隔，多个节点用;间隔
			String comment = (String) param.get("comment");// 备注
			Long submittype = ApprovalUtil.strToLong((String) param
					.get("submittype"));// 类别，0为保存，1为送审

			boolean result = false;
			if (submittype == null || submittype == 0l)
			{
				result = SignUtil.instance().saveSign(
						ApprovalUtil.strToLong(id),
						ApprovalUtil.strToBoolean(sendsigntag),
						ApprovalUtil.strToBoolean(sendreadtag), title,
						sendfiles, sendfilenames, webcontent,
						ApprovalUtil.strsToLongs(accepters),
						ApprovalUtil.strToBoolean(issame), false, sendreaders,filetype,backsigners,
						comment,fileflowid,filesuccdate,fromunit,filecode,filescript, user);// 保存草稿
			}
			else
			{
				// 直接送审
				result = SignUtil.instance().sendSign(
						ApprovalUtil.strToLong(id),
						ApprovalUtil.strToBoolean(sendsigntag),
						ApprovalUtil.strToBoolean(sendreadtag), title,
						sendfiles, sendfilenames, webcontent,
						ApprovalUtil.strsToLongs(accepters),
						ApprovalUtil.strToBoolean(issame), false, sendreaders,filetype,backsigners,
						comment,fileflowid,filesuccdate,fromunit,filecode,filescript, user);
			}

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String sendSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		return saveDraft(req, resp, jsonParams, user);
	}

	public static String getApprovalsave(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 根据草稿编号获取草稿的具体内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String seltype = (String) param.get("seltype"); // 当前所在的类别
															// draft草稿，send已送，todo待办，done已办
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			Map<String, Object> result = new HashMap<String, Object>();

			ApprovalSave approvalSave = SignUtil.instance().getApprovalSave(
					ApprovalUtil.strToLong(id), seltype, user);
			result.put("id", approvalSave.getId());
			result.put("title", approvalSave.getTitle());// 标题
			result.put("filepaths", approvalSave.getFilepaths());// 附件路径，多个用,间隔
			result.put("filepathnames", approvalSave.getFilepathnames());// 附件名称，多个用,间隔
			result.put("webcontent", approvalSave.getWebcontent());// 网页内容
			result.put("modifytype", approvalSave.getModifytype());// 处理方式，0表示文档协作，1表示签阅
			result.put("coopers", approvalSave.getCoopers());// 文档协作者，多人之间用,间隔
			result.put("sendsigntag", approvalSave.getSendsigntag());// 送签是否选中，0不选中，1表示选中
			result.put("sendreadtag", approvalSave.getSendreadtag());// 送阅是否选中，0不选中，1表示选中
			result.put("signers", approvalSave.getSigners());// 签批者的人ID,多人用,间隔
			result.put("issame", approvalSave.getIssame());// 会签是否选中，0不选中，1表示选中
			result.put("sendreaders", approvalSave.getSendreaders());// 送阅人，多人用,间隔
			result.put("comment", approvalSave.getComment());// 备注
			result.put("userID", approvalSave.getUserID());// 保存人号
			result.put("sendreadnames", approvalSave.getSendreadnames());// 送阅人名称，用,间隔
			result.put("signernames", approvalSave.getSignernames());// 签批人名称，用,间隔
			result.put("coopernames", approvalSave.getCoopernames());// 协作者名称，用,间隔

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String delSignInfo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 根据传递的类型和编号对应删除签批信息
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> ids = (List<String>) param.get("ids"); // 流程编号
			String deltype = (String) param.get("deltype");// 删除类型"draft"草稿，send已送，todo待办，done已办，filed成文
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			Map<String, Object> result = new HashMap<String, Object>();
			SignUtil.instance().delSignInfo(ApprovalUtil.strsToLongs(ids),
					deltype, user);// 获取草稿数据

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String successSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 根据编号成文
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> ids = (List<String>) param.get("ids"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = SignUtil.instance().signSuccess(
					ApprovalUtil.strsToLongs(ids), user);// 成文
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String endSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 根据编号终止
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			List<String> ids = (List<String>) param.get("ids"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = SignUtil.instance().endSignInfo(
					ApprovalUtil.strsToLongs(ids), user);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String undoSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 根据编号撤销
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String seltype = (String) param.get("seltype");// 类型，send 已送 done已签
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			String result = SignUtil.instance().undoSign(
					ApprovalUtil.strToLong(id), seltype, user);// 撤销
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getCurrentPermit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws Exception, IOException
	{
		// 获取签批对话框内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			Map<String, Object> result = SignUtil.instance().getCurrentPermit(
					ApprovalUtil.strToLong(id), user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String modifySignRead(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 处理签阅的情况
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String type = (String) param.get("type");// 提交类型 submit/end 提交或终止
			String comment = (String) param.get("comment");// 备注信息
			String readerId=(String) param.get("readerId");//批阅人员
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = SignUtil.instance().modifySignRead(
					ApprovalUtil.strToLong(id), type, comment, user,readerId);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String backSendSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 返回送审人
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String type = (String) param.get("type");// 提交类型 submit/end 提交或终止
			String comment = (String) param.get("comment");// 备注信息

			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = SignUtil.instance().backSendSign(
					ApprovalUtil.strToLong(id), type, comment, user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String modifySignSend(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 处理签批
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String type = (String) param.get("type");// 提交类型 submit/end 提交或终止
			String comment = (String) param.get("comment");// 备注信息
			List<String> signids = (List<String>) param.get("signids");
			String issame = (String) param.get("issame");
			String readids = (String) param.get("readids");
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = SignUtil.instance().modifySignSend(
					ApprovalUtil.strToLong(id),
					(ArrayList<Long>) (ApprovalUtil.strsToLongs(signids)),
					ApprovalUtil.strToBoolean(issame), false, readids, comment,
					user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String reSendSign(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 再次送审
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String comment = (String) param.get("comment");// 备注信息
			List<String> signids = (List<String>) param.get("signids");
			String issame = (String) param.get("issame");
			String readids = (String) param.get("readids");
			String account = (String) param.get("account"); // 登录的账户
			String backsigners=(String) param.get("backsigners");//会签后处理人
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = SignUtil.instance().reSendSign(
					ApprovalUtil.strToLong(id),
					(ArrayList<Long>) (ApprovalUtil.strsToLongs(signids)),
					ApprovalUtil.strToBoolean(issame), false, readids,backsigners, comment,
					user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String sendCooper(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 保存和送协作
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String title = (String) param.get("title");// 标题
			List<String> sendfiles = (List<String>) param.get("sendfiles");// 送审文件
			List<String> sendfilenames = (List<String>) param
					.get("sendfilenames");// 送审文件名
			String webcontent = (String) param.get("webcontent");// 网页内容
			String comment = (String) param.get("comment");// 备注信息
			String cooperId = (String) param.get("cooperId");// 送阅人，用,间隔
			Integer submittype = ApprovalUtil.strToInteger((String) param
					.get("submittype"));// 类别，0为保存，1为送审
			Long fileflowid=null;
			String filetype=(String) param.get("filetype");//文件类别
			String temp=(String) param.get("fileflowid");//文件流水号
			if (temp!=null)
			{
				fileflowid=Long.valueOf(temp);
			}
			Date filesuccdate=(Date) param.get("filesuccdate");//成文日期

			String fromunit=(String) param.get("fromunit");//来文单位
			String filecode=(String) param.get("filecode");//文号
			String filescript=(String) param.get("filescript");//文件备注
			boolean result = false;
			if (submittype == null || submittype == 0)// 保存写作信息
			{
				result = SignUtil.instance().saveCooper(
						ApprovalUtil.strToLong(id), title, sendfiles,
						sendfilenames, webcontent, cooperId, comment,fileflowid,filesuccdate,fromunit,filecode,filescript, user);
			}
			else
			// 送写作信息
			{
				result = SignUtil.instance().sendCooper(
						ApprovalUtil.strToLong(id), title, sendfiles,
						sendfilenames, webcontent, cooperId, comment,fileflowid,filesuccdate,fromunit,filecode,filescript, user);
			}
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getHistory(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String id = (String) param.get("id"); // 流程编号
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			Map<String, Object> result = SignUtil.instance().getNewHistory(
					user.getId(), Long.valueOf(id),true);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static List<Long> stringToLong(List<String> stringList)
	{

		if (stringList == null || stringList.size() < 1)
		{
			return null;
		}
		List<Long> longList = new ArrayList<Long>();
		for (int i = 0; i < stringList.size(); i++)
		{
			try
			{
				if (stringList.get(i) != null && !stringList.get(i).equals(""))
				{
					longList.add(Long.parseLong(stringList.get(i)));
				}
				else
				{
					longList.add(null);
				}

			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return longList;
	}

	private static List<Fileinfo> getShareSearchInfo(long userID,
			String spaceUID, String companyID, String special, boolean isMulti)
	{
		FileSystemService fileService = (FileSystemService) ApplicationContext
				.getInstance().getBean(FileSystemService.NAME);
		List<Fileinfo> searchPathFileInfo = null;
		if (special.equals("1"))// 在我的共享里搜索
		{
			searchPathFileInfo = getMyShare(fileService, spaceUID, userID,
					companyID, 0, Integer.MAX_VALUE, null, null);
			if (searchPathFileInfo != null)
			{
				searchPathFileInfo.remove(0);// 剔除第一个integer对象
			}
		}
		else if (special.equals("2"))// 在他人共享里搜索
		{
			searchPathFileInfo = getOthersShare(fileService, spaceUID, userID,
					companyID, 0, Integer.MAX_VALUE, null, null);
			if (searchPathFileInfo != null)
			{
				searchPathFileInfo.remove(0);// 剔除第一个integer对象
			}
		}
		else if (special.equals("3"))// 最新共享：按规则应该是包括他人共享和我的共享
		{
			searchPathFileInfo = getMyShare(fileService, spaceUID, userID,
					companyID, 0, Integer.MAX_VALUE, null, null);
			if (searchPathFileInfo != null)
			{
				searchPathFileInfo.remove(0);// 剔除第一个integer对象
			}
			List<Fileinfo> otherInfo = getOthersShare(fileService, spaceUID,
					userID, companyID, 0, Integer.MAX_VALUE, null, null);
			if (otherInfo != null)
			{
				otherInfo.remove(0);// 剔除第一个integer对象
				if (searchPathFileInfo == null)
				{
					searchPathFileInfo = new ArrayList<Fileinfo>();
				}
				searchPathFileInfo.addAll(otherInfo);
			}
		}
		else if (special.equals("4"))// 个人有权限的所有空间
		{
			// PermissionService permissService =
			// (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			List<Spaces> list = fileService.getGroupSpacesByUserId(userID);
			searchPathFileInfo = new ArrayList<Fileinfo>();
			for (Spaces temp : list)
			{
				// Long pe = permissService.getFileSystemAction(userID,
				// temp.getSpaceUID());
				// if (pe != null && pe != 0)
				Fileinfo fileinfo = new Fileinfo();
				fileinfo.setPathInfo(temp.getSpaceUID() + "/"
						+ FileConstants.DOC);
				fileinfo.setFold(true);
				searchPathFileInfo.add(fileinfo);
			}
		}
		else
		// if (isMulti)
		{
			Fileinfo fileinfo = new Fileinfo();
			fileinfo.setPathInfo(spaceUID + "/" + FileConstants.DOC);
			fileinfo.setFold(true);
			searchPathFileInfo = new ArrayList<Fileinfo>();
			searchPathFileInfo.add(fileinfo);
		}
		return searchPathFileInfo;
	}

	private static List getOthersShare(FileSystemService fileService,
			String loginMail, long sharedID, String companyID, int start,
			int limit, String sort, String dir)
	{
		return fileService.getOthersShare(loginMail, sharedID, companyID,
				start, limit, sort, dir);
	}

	private static List<Fileinfo> getMyShare(FileSystemService fileService,
			String loginMail, long creatorID, String companyID, int start,
			int limit, String sort, String dir)
	{
		return fileService.getMyShare(loginMail, creatorID, companyID, start,
				limit, sort, dir);
	}

	/**
	 * 搜索文件
	 * 
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getSearch(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);

			String Id = (String) param.get("userId");
			String account = (String) param.get("account");

			HashMap<String, Object> con = (HashMap<String, Object>) param
					.get("condition");
			String spaceUID = (String) con.get("spaceUID");
			String sort = (String) con.get("sort");
			String dir = (String) con.get("dir"); // asc,desc
			String s = (String) con.get("start");
			String l = (String) con.get("count");
			int start = s != null ? Integer.valueOf(s) : 0;
			int limit = l != null ? Integer.valueOf(l) : Integer.MAX_VALUE;

			String[] contents = new String[12];
			// 获取搜索条件
			contents[0] = (String) con.get("fileName");
			contents[2] = (String) con.get("author");
			contents[3] = (String) con.get("title");
			contents[4] = (String) con.get("keyword");
			contents[5] = (String) con.get("content");
			contents[6] = (String) con.get("special"); // 1在我的共享里搜索, 2 在他人共享里搜索,
														// 3
														// 最新共享：按规则应该是包括他人共享和我的共享,
														// 4 个人有权限的所有空间
			contents[7] = (String) con.get("mimetype");
			contents[8] = (String) con.get("time"); // "任何时间","最近一周","最近一月","最近三月","最近六月","去年"
			contents[9] = (String) con.get("beginTime"); // 2012-12-12
			contents[10] = (String) con.get("endTime"); // 2012-12-12

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Long userID = Long.valueOf(Id);
			FileSystemService fileSystemService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);

			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			contents[7] = FileUtils.convertFileType(contents[7]);
			if (null != contents[8])
			{
				if (contents[8].equals(DataConstant.ANYTIME))// 时间的设置
				{
					from = null;
				}
				else
				{
					long totime = from.getTime().getTime();
					to.setTime(new Date(totime));
					from.setTime(FileUtils.convertCalByRange(contents[8],
							from.getTime()));
				}
			}
			else
			{
				try
				{
					if (contents[9] != null)
					{
						from.setTime(sdf.parse(contents[9]));
					}
					else
					{
						from = null;
					}
					if (contents[10] != null)
					{
						to.setTime(sdf.parse(contents[10]));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			boolean isSpecialSearch = contents[6].equals("1")
					|| contents[6].equals("2") || contents[6].equals("3")
					|| contents[6].equals("4");
			List<Fileinfo> searchPathFileInfo = getShareSearchInfo(userID,
					spaceUID, "", contents[6], true);
			if (searchPathFileInfo == null)
			{
				searchPathFileInfo = new ArrayList<Fileinfo>();
			}

			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);

			DataHolder dh = new DataHolder();
			if (searchPathFileInfo.size() > 0)
			{
				if (sort != null && sort.length() > 0 && dir != null
						&& dir.length() > 0)
				{
					try
					{
						dh = jcrService.searchFile(
								searchPathFileInfo.toArray(new Fileinfo[0]),
								contents, from, to, 0, Integer.MAX_VALUE);
						ArrayList<Object> fileinfos = dh.getFilesData();
						for (int i = 0; i < fileinfos.size(); i++)
						{
							Fileinfo fileInfo = (Fileinfo) fileinfos.get(i);
							String pathinfo = fileInfo.getPathInfo();
							List signList = fileSystemService.getAllSign(
									userID, pathinfo);
							if (signList != null && signList.size() > 0)
							{
								fileInfo.setIsSign("1");
								fileInfo.setSignCount(signList.size());
							}
						}
					}
					catch (RepositoryException e)
					{
						e.printStackTrace();
					}
					List<Object> arr = dh.getFilesData();
					int length = arr.size();
					if (sort != null)
					{
						int sgn = dir.equalsIgnoreCase("ASC") ? 1 : -1;
						FileArrayComparator cp = new FileArrayComparator(sort,
								sgn);
						Collections.sort(arr, cp);
						arr = arr.subList(start,
								start + limit >= length ? length : start
										+ limit);
					}
					ArrayList temp = new ArrayList(arr.size());
					temp.addAll(arr);
					dh.setFilesData(temp);
				}
				else
				{
					try
					{
						if (isSpecialSearch)
						{
							dh = jcrService
									.searchFile(searchPathFileInfo
											.toArray(new Fileinfo[0]),
											contents, from, to, start, limit);
							ArrayList<Object> fileinfos = dh.getFilesData();
							for (int i = 0; i < fileinfos.size(); i++)
							{
								Fileinfo fileInfo = (Fileinfo) fileinfos.get(i);
								String pathinfo = fileInfo.getPathInfo();
								List signList = fileSystemService.getAllSign(
										userID, pathinfo);
								if (signList != null && signList.size() > 0)
								{
									fileInfo.setIsSign("1");
									fileInfo.setSignCount(signList.size());
								}
							}
						}
						else
						{
							dh = jcrService.searchFile(contents[6], contents,
									from, to, start, limit, null);
							ArrayList<Object> fileinfos = dh.getFilesData();
							for (int i = 0; i < fileinfos.size(); i++)
							{
								Fileinfo fileInfo = (Fileinfo) fileinfos.get(i);
								String pathinfo = fileInfo.getPathInfo();
								List signList = fileSystemService.getAllSign(
										userID, pathinfo);
								if (signList != null && signList.size() > 0)
								{
									fileInfo.setIsSign("1");
									fileInfo.setSignCount(signList.size());
								}
							}
						}
					}
					catch (RepositoryException e)
					{
						e.printStackTrace();
					}
				}
			}

			ArrayList<Object> array = dh.getFilesData();

			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, null);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getTransDrafts(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取交办事务的草稿 移动也可直接调用
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "DESC";
			}
			HashMap<String, Object> result = TransUtil.instance()
					.getTransDrafts(start, count, sort, order, user);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getTransMyquestfiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getDrafts",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = TransUtil.instance()
					.getTransMyquestfiles(start, count, sort, order, user, userIds, selectedTime, searchName);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getTransDone(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getDrafts",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = TransUtil.instance().getTransDone(start, count, sort, order, user, userIds, selectedTime, searchName);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getMyReceiveMessage(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
				.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		FileSystemService fileservice = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		HashMap<String, Object> result = new HashMap<String, Object>();
		String sort = (String) param.get("sort"); // 排序类型
		String dir = (String) param.get("order"); // 排序类型
		
		String mobile = user.getMobile();
		if(mobile != null){
			result = fileservice.getReceiverMessageInfo(mobile,start, count, sort, dir);
		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		return error;
	}
	
	public static String getMySendMessage(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
			String error;
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			FileSystemService fileservice = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			String dir = (String) param.get("order"); // 排序类型
			HashMap<String, Object> result = fileservice.getSenderMessageInfo(user.getId(), start, count, sort, dir);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
	}

	public static String getTransTodo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 孙爱华增加的，移动也可直接调用
		String error;
//		try
//		{
			// static/fileOpeService?jsonParams={method:"getDrafts",params:{start:0,count:10,sort:'+encodeURIComponent(sort)+'}}
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = TransUtil.instance().getTransTodo(start, count, sort, order, user, userIds, selectedTime, searchName);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String transSave(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 保存草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String title = (String) param.get("title"); // 主题
			String webcontent = (String) param.get("webcontent"); // 内容
			ArrayList<String> filePaths = (ArrayList<String>) param.get("filePaths"); // 附件路径
			ArrayList<String> fileNames = (ArrayList<String>) param.get("fileNames"); // 附件名称
			ArrayList<String> personIds = (ArrayList<String>) param.get("personIds"); // 处理人编号
			String personNames = (String) param.get("personNames"); // 处理人名称，多人用;间隔
			String comment = (String) param.get("comment"); // 备注说明
			
			//String的array转成Long类型的arrary
			ArrayList<Long> ids = new ArrayList<Long>();
			for (String person : personIds) {
				ids.add(Long.valueOf(person));
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = TransUtil.instance().transSave(Long.valueOf(id), title, webcontent, filePaths, fileNames, ids, personNames, comment, user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String transCommit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 提交事务
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String title = (String) param.get("title"); // 主题
			String webcontent = (String) param.get("webcontent"); // 内容
			ArrayList<String> filePaths = (ArrayList<String>) param.get("filePaths"); // 附件路径
			ArrayList<String> fileNames = (ArrayList<String>) param.get("fileNames"); // 附件名称
			ArrayList<String> personIds = (ArrayList<String>) param.get("personIds"); // 处理人编号
			String personNames = (String) param.get("personNames"); // 处理人名称，多人用;间隔
			String comment = (String) param.get("comment"); // 备注说明

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			
			//String的array转成Long类型的arrary
			ArrayList<Long> ids = new ArrayList<Long>();
			for (String person : personIds) {
				ids.add(Long.valueOf(person));
			}
			boolean result = TransUtil.instance().transCommit(Long.valueOf(id), title, webcontent, filePaths, fileNames, ids, personNames, comment, user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getTransSave(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String type =(String)param.get("type");//类型，0为事务，1为草稿

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			TransSave transSave = TransUtil.instance().getTransSave(Long.valueOf(id), user);
			HashMap<String, Object> values = new HashMap<String, Object>();//需要什么数据自己装载
			values.put("id", transSave.getId());
			values.put("title", transSave.getTitle());
			values.put("filepaths", transSave.getFilepaths());
			values.put("filepathnames", transSave.getFilepathnames());
			values.put("webcontent", transSave.getWebcontent());
			values.put("modifierlist", transSave.getModifierlist());//办理者的人ID,多人用,间隔
			values.put("signernames", transSave.getSignernames());
			values.put("comment", transSave.getComment());// 送审说明（备注）
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	
	public static String getTransPermit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取事务的全部内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			TransInfo transInfo = TransUtil.instance().getTransPermit(Long.valueOf(id), user);
			HashMap<String, Object> values = new HashMap<String, Object>();//需要什么数据自己装载
			values.put("id", transInfo.getId());
			values.put("title", transInfo.getTitle());
			values.put("filelist", transInfo.getFilelist());//附件
			values.put("webcontent", transInfo.getWebcontent());
			values.put("comment", transInfo.getComment());// 事务说明（备注）
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getTransWebcontent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取事务内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String type =(String)param.get("type");//类型，0为事务，1为草稿

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String result = TransUtil.instance().getWebcontent(Long.valueOf(id), Integer.parseInt(type));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	
	
	public static String transModify(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 处理事务
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 事务id
			String title = (String) param.get("title"); // 主题
			String handle = (String) param.get("handle"); //在办，还是办结 在办为true,办结为false
			ArrayList<String> filePaths = (ArrayList<String>) param.get("filePaths"); // 处理过程中上传的文件
			ArrayList<String> fileNames = (ArrayList<String>) param.get("fileNames"); // 上传的文件名
			String comment = (String) param.get("comment"); // 备注说明
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = TransUtil.instance().transModify(Long.valueOf(id), comment, filePaths, fileNames, Boolean.valueOf(handle), user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String transDelete(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 删除事务或草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 事务id
			String type = (String) param.get("type"); // 0为事务，1为草稿,2我的事务
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = TransUtil.instance().transDelete(Long.valueOf(id),Integer.parseInt(type),user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getTransHistosy(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取事务内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String type =(String)param.get("type");//类型，0为事务，1为草稿

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			Map<String,Object> result = TransUtil.instance().getTransHistosy(user.getId(), Long.valueOf(id));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String transsignreal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 事务签收设置
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = TransUtil.instance().transsignreal(id, user);// 签收设置
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getTransSignReal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 签收设置
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = TransUtil.instance().getTransSignReal(id, user);// 获取签收情况
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	//获取我的发文
    public static String getFawenSend(HttpServletRequest req,
                                       HttpServletResponse resp, HashMap<String, Object> jsonParams,
                                       Users user) throws ServletException, IOException
    {
        String error;
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account"); // 登录的账户
        Integer start = (Integer) param.get("start"); // 分页起始位置
        Integer count = (Integer) param.get("count"); // 每页显示的数量

        if (user == null)
        {
            UserService userService = (UserService) ApplicationContext
                    .getInstance().getBean(UserService.NAME);
            user = userService.getUser(account);
        }
        String sort = (String) param.get("sort"); // 排序类型
        if (sort == null || sort.equals(""))
        {
            sort = "";
        }
        String order = (String) param.get("order"); // 排序类型
        if (order == null || order.equals(""))
        {
            order = "desc";
        }
        HashMap<String, Object> result = FawenUtil.instance().getFawenSend(
                start, count, sort, order, user);//   我的发文
                error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        return error;
    }
    //获取单位收文
    public static String getFawenReceive(HttpServletRequest req,
                                      HttpServletResponse resp, HashMap<String, Object> jsonParams,
                                      Users user) throws ServletException, IOException
    {
        String error;
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account"); // 登录的账户
        String scope = (String) param.get("scope"); // 登录的账户
        Integer start = (Integer) param.get("start"); // 分页起始位置
        Integer count = (Integer) param.get("count"); // 每页显示的数量
        UserService userService = (UserService) ApplicationContext
                .getInstance().getBean(UserService.NAME);
        if (user == null)
        {
            user = userService.getUser(account);
        }
        String sort = (String) param.get("sort"); // 排序类型
        if (sort == null || sort.equals(""))
        {
            sort = "";
        }
        String order = (String) param.get("order"); // 排序类型
        if (order == null || order.equals(""))
        {
            order = "desc";
        }
        //没有发文权限
        if(user.getFawen()==false){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        //获取自己的组织id和name
        List<Organizations> organizations = userService.getOrganizationByUsers(user.getId());
        //没有组织的人民是不允许发文滴
        if(organizations.size()==0){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        Organizations parentOrg=organizations.get(0);
        while(parentOrg.getParent()!=null){
            parentOrg=parentOrg.getParent();
        }

        HashMap<String, Object> result = FawenUtil.instance().getFawenReceive(
                start, count, sort, order, user,scope,parentOrg.getId());//   我的发文
        error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        return error;
    }
    //获取可发文的单位列表
    public static String getFawenDepartment(HttpServletRequest req,
                                         HttpServletResponse resp, HashMap<String, Object> jsonParams,
                                         Users user) throws ServletException, IOException
    {
        String error;
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account"); // 登录的账户
        UserService userService = (UserService) ApplicationContext
                .getInstance().getBean(UserService.NAME);
        if (user == null)
        {
            user = userService.getUser(account);
        }
        //没有发文权限
        if(user.getFawen()==false){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        //获取自己的组织id和name
        List<Organizations> organizations = userService.getOrganizationByUsers(user.getId());
        //没有组织的人民是不允许发文滴
        if(organizations.size()==0){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        Organizations parentOrg=organizations.get(0);
        while(parentOrg.getParent()!=null){
            parentOrg=parentOrg.getParent();
        }

        HashMap<String, Object> result = FawenUtil.instance().getFawenDepartment(user,parentOrg.getId());//   我的发文
        error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        return error;
    }
    //发送公文
    public static String sendFawen(HttpServletRequest req,
                                         HttpServletResponse resp, HashMap<String, Object> jsonParams,
                                         Users user) throws ServletException, IOException
    {
        String error;
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account");
        String filePath = (String) param.get("path");
        String fileName = (String) param.get("name");
        Long departid = Long.valueOf((Integer) param.get("departid"));
        UserService userService = (UserService) ApplicationContext
                .getInstance().getBean(UserService.NAME);
        if (user == null)
        {
            user = userService.getUser(account);
        }

        //没有发文权限
        if(user.getFawen()==false){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        //获取自己的组织id和name
        List<Organizations> organizations = userService.getOrganizationByUsers(user.getId());
        //没有组织的人民是不允许发文滴
        if(organizations.size()==0){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        Organizations parentOrg=organizations.get(0);
        while(parentOrg.getParent()!=null){
            parentOrg=parentOrg.getParent();
        }
        //获取接收的组织
        //Organizations receiveDepart=userService.getOrganizationsBydepid(departid);
        Organizations receiveDepart= userService.getGroup(departid);
        //接收组织验证:是否同一个公司？是否是没有parentId
        //恶意操作，理论上是不与返回任何信息
        //if部门id不对、部门不是单位、部门不是本公司的就报权限错误

       if(null == receiveDepart || receiveDepart.getParent()!=null || !receiveDepart.getCompany().getId().equals(user.getCompany().getId())){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }

        HashMap<String, Object> result = FawenUtil.instance().sendFawen(filePath,fileName,receiveDepart, parentOrg,user);//   我的发文
        error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        return error;
    }

    //收取公文
    public static String receiveFawen(HttpServletRequest req,
                                   HttpServletResponse resp, HashMap<String, Object> jsonParams,
                                   Users user) throws ServletException, IOException
    {
        String error;
        HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
                .get(ServletConst.PARAMS_KEY);
        String account = (String) param.get("account");
        Long fid=Long.valueOf((String)  param.get("fid"));
        UserService userService = (UserService) ApplicationContext
                .getInstance().getBean(UserService.NAME);

        if (user == null)
        {
            user = userService.getUser(account);
        }
        //获取自己的组织id和name
        List<Organizations> organizations = userService.getOrganizationByUsers(user.getId());
        //没有组织的人民是不允许发文滴
        if(organizations.size()==0){
            return JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, null);
        }
        Organizations parentOrg=organizations.get(0);
        while(parentOrg.getParent()!=null){
            parentOrg=parentOrg.getParent();
        }

        HashMap<String, Object> result = FawenUtil.instance().receiveFawen(fid, user,parentOrg.getId());//   我的发文
        error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
        return error;
    }














    //会议
	public static String getMeetDrafts(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = MeetUtil.instance().getMeetDrafts(
					start, count, sort, order, user);// 获取草稿数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getMeetMyquestfiles(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议请求
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds_1");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<String> userIds = new ArrayList<String>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(";");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(personIdsList[i]);
				}
			}else{
				userIds = null;
			}
			String eSelectedTime = (String)param.get("eSelectedTime");//筛选的会议时间段
			String searchSpace = (String)param.get("searchSpace");//筛选的地点

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = MeetUtil.instance().getMeetMyquestfiles(start, count, sort, order, user, userIds,selectedTime, searchName,  eSelectedTime, searchSpace);// 获取我的会议通知列表
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getMeetDone(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议办结
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			String personIds_1 = (String)param.get("personIds_1");//筛选的召开人
			String eSelectedTime = (String)param.get("eSelectedTime");//筛选的会议时间段
			String searchSpace = (String)param.get("searchSpace");//筛选的地点
			ArrayList<String> userIds_1 = new ArrayList<String>();
			if(personIds_1 != null && personIds_1.length() > 0)
			{
				String[] personIdsList = personIds_1.split(";");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds_1.add(personIdsList[i]);
				}
			}else{
				userIds_1 = null;
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = MeetUtil.instance().getMeetDone(start, count, sort, order, user, userIds, selectedTime, searchName, userIds_1, eSelectedTime, searchSpace);// 获取会议办结数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getMeetTodo(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取待办会议
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			Integer start = (Integer) param.get("start"); // 分页起始位置
			Integer count = (Integer) param.get("count"); // 每页显示的数量
			String personIds = (String)param.get("personIds");//筛选的人员
			String selectedTime = (String)param.get("selectedTime");//筛选的时间段
			String searchName = (String)param.get("searchName");//筛选的名称
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(personIds != null && personIds.length() > 0)
			{
				String[] personIdsList = personIds.split(",");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds.add(Long.valueOf(personIdsList[i]));
				}
			}else{
				userIds = null;
			}
			String personIds_1 = (String)param.get("personIds_1");//筛选的召开人
			String eSelectedTime = (String)param.get("eSelectedTime");//筛选的会议时间段
			String searchSpace = (String)param.get("searchSpace");//筛选的地点
			ArrayList<String> userIds_1 = new ArrayList<String>();
			if(personIds_1 != null && personIds_1.length() > 0)
			{
				String[] personIdsList = personIds_1.split(";");				
				for (int i = 0;i < personIdsList.length;i++) {
					userIds_1.add(personIdsList[i]);
				}
			}else{
				userIds_1 = null;
			}

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String sort = (String) param.get("sort"); // 排序类型
			if (sort == null || sort.equals(""))
			{
				sort = "";
			}
			String order = (String) param.get("order"); // 排序类型
			if (order == null || order.equals(""))
			{
				order = "desc";
			}
			HashMap<String, Object> result = MeetUtil.instance().getMeetTodo(start, count, sort, order, user, userIds, selectedTime, searchName, userIds_1, eSelectedTime, searchSpace);// 获取待办会议数据
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String meetSave(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 保存草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String meetname = (String) param.get("meetname");//会议名称
			Date meetdate = (Date) param.get("meetdate");//会议日期 yyyy-mm-dd
			String meettime = (String) param.get("meettime");//会议时间
			String meetaddress = (String) param.get("meetaddress");//会议地点
			String mastername = (String) param.get("mastername");//会议名称
			String[][] meetmannames = (String[][]) param.get("meetmannames");//与会人员[与会人员id,与会人员名称]
			String[][] othermannames = (String[][]) param.get("othermannames");//其他人员[id(默认是0),人名，单位,电话]
			String meetcontent = (String) param.get("meetcontent");//会议议程
			String[][] filePaths = (String[][]) param.get("filePaths");//会议资料[附件地址,附件名称]
			String comment = (String) param.get("comment");//备注
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = MeetUtil.instance().meetSave(Long.valueOf(id), meetname, meetdate, meettime, meetaddress, mastername, meetmannames, othermannames, meetcontent, filePaths, comment, user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String meetCommit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 保存草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String meetname = (String) param.get("meetname");//会议名称
			Date meetdate = (Date) param.get("meetdate");//会议日期 yyyy-mm-dd
			String meettime = (String) param.get("meettime");//会议时间
			String meetaddress = (String) param.get("meetaddress");//会议地点
			String mastername = (String) param.get("mastername");//会议名称
			String[][] meetmannames = (String[][]) param.get("meetmannames");//与会人员[与会人员id,与会人员名称]
			String[][] othermannames = (String[][]) param.get("othermannames");//其他人员[id(默认是0),人名，单位,电话]
			String meetcontent = (String) param.get("meetcontent");//会议议程
			String[][] filePaths = (String[][]) param.get("filePaths");//会议资料[附件地址,附件名称]
			String comment = (String) param.get("comment");//备注
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = MeetUtil.instance().meetCommit(Long.valueOf(id), meetname, meetdate, meettime, meetaddress, mastername, meetmannames, othermannames, meetcontent, filePaths, comment, user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getMeetSave(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String type =(String)param.get("type");//类型，0为事务，1为草稿

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			MeetSave meetSave = MeetUtil.instance().getMeetSave(Long.valueOf(id), user);
			HashMap<String, Object> values = new HashMap<String, Object>();//需要什么数据自己装载
			values.put("id", meetSave.getId());
			values.put("meetname", meetSave.getMeetname());//会议名称
			values.put("meetcontent", meetSave.getMeetcontent());//会议内容
			values.put("mastername", meetSave.getMastername());//会议召开人
			values.put("meetaddress", meetSave.getMeetaddress());//会议地址
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getMeetWebcontent(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			String type =(String)param.get("type");//类型，0为事务，1为草稿

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			String result = MeetUtil.instance().getWebcontent(Long.valueOf(id), Integer.parseInt(type));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getMeetPermit(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议的全部内容
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id

			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			MeetInfo meetInfo = MeetUtil.instance().getMeetPermit(Long.valueOf(id), user);
			HashMap<String, Object> values = new HashMap<String, Object>();//需要什么数据自己装载
			values.put("id", meetInfo.getId());
			values.put("meetname", meetInfo.getMeetname());//会议名称
			values.put("mastername", meetInfo.getMastername());//会议召开人
			values.put("meetcontent", meetInfo.getMeetcontent());//会议内容
			values.put("meetaddress", meetInfo.getMeetaddress());//会议地址
			values.put("meetcomment", meetInfo.getComment());//会议地址
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String meetDelete(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 删除会议或草稿
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 事务id
			String type = (String) param.get("type"); // 0为事务，1为草稿,2我的会议通知
			
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = MeetUtil.instance().meetDelete(Long.valueOf(id),Integer.parseInt(type),user);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String meetModify(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 处理会议
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 会议id
			String actionid = (String) param.get("actionid"); //回执选项，1参会，2替会，3不参会
			String comment = (String) param.get("comment"); // 备注
			String replaceuserid = (String) param.get("replaceuserid"); //  替会人员ID，0就是系统外人员
			String othername = (String) param.get("othername");//替换人名
			String otherunit = (String) param.get("otherunit");//替换人单位
			String otherphone = (String) param.get("otherphone");//替换人电话
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			boolean result = MeetUtil.instance().meetModify(Long.valueOf(id), Long.valueOf(actionid), comment, 
					Long.valueOf(replaceuserid), othername, otherunit, otherphone, user,false);
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getMeetBackDetail(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取会议详情
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			List<String[]> values = MeetUtil.instance().getMeetBackDetail(user.getId(),Long.valueOf(id));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String getMeetBackDetailCB(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// //会议详情，需要催办
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id"); // 草稿id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}
			List<String[]> values = MeetUtil.instance().getMeetBackDetailCB(user.getId(),Long.valueOf(id));
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, values);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	public static String meetsignreal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 会议签收设置
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = MeetUtil.instance().meetsignreal(id, user);// 签收设置
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}

	public static String getMeetSignReal(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 会议签收获取
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			String id = (String) param.get("id");// 流程id
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			boolean result = MeetUtil.instance().getMeetSignReal(id, user);// 获取签收情况
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	
	public static String getMsgCount(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams,
			Users user) throws ServletException, IOException
	{
		// 获取消息数量
		String error;
//		try
//		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String account = (String) param.get("account"); // 登录的账户
			if (user == null)
			{
				UserService userService = (UserService) ApplicationContext
						.getInstance().getBean(UserService.NAME);
				user = userService.getUser(account);
			}

			List<Long[]> result = MessageUtil.instance().getMsgCount(user);// 获取消息数量
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
			return error;
//		}
//		catch (ClassCastException e)
//		{
//			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
//		}
//		resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
//		resp.getWriter().write(error);
	}
	/**
	 * 判断回收站中的文件是否具有还原、删除的操作权限，判断方法暂定为：判定原文件是否具有操作权限
	 */
	public static boolean checkRecyclerPermission(String[] paths, Users user)
			throws ServletException, IOException
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		try
		{
			ArrayList<String> pathList = jcrService.getRecyclerRealPath(paths);
			String[] path = (String[]) pathList.toArray(new String[pathList
					.size()]);
			for(String tempPath : path)
			{
				boolean permit = hasPermission(tempPath, user, FileSystemCons.DELETE_FLAG);
				if(!permit)
				{
					return false;
				}
			}
			
		}
		catch (RepositoryException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	/*
	 * 获取他人共享中的共享人
	 */
	public static String getSharer(HttpServletRequest req,HttpServletResponse resp,Users user)
	{
		String error;
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		List<Map<String,Object>> sharerList = userService.getSharer(user.getId());
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,sharerList);
		return error;
	}
	
	/*
	 * 信电局版本中审阅的送审
	 */
	public static String sendReviewFiles(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String path = (String)param.get("filePath");
		String fileName = (String)param.get("fileName");
		String comment = (String)param.get("comment");
		List addList = (List)param.get("permitInfo");
		Boolean isMobile = Boolean.valueOf((String)param.get("isMobile"));//是否手机短信提示
		FileSystemService fss = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		JCRService jcr = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		UserService us = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		Fileinfo fileinfo = jcr.addSignFile(path, user.getSpaceUID());
		Date sendTime = new Date();
		ReviewInfo ri = new ReviewInfo();
		ri.setOldPath(fileinfo.getPathInfo());
		ri.setComment(comment);
		ri.setSender(user);
		ri.setFileName(fileName);
		ri.setSendDate(sendTime);
		ri.setCount(addList.size());
		fss.setReviewInfo(ri,addList);
		if (isMobile)//发送手机短信
		{
			List<String> mobilelist=new ArrayList<String>();
			List<Long> templist=new ArrayList<Long>();
			for (int i=0;i<addList.size();i++)
			{
				String[] info = ((String)addList.get(i)).split("-");
	    		ReviewFilesInfo rfi = new ReviewFilesInfo();
	    		Long reviewer = Long.valueOf(info[0]);
				Users tempuser=us.getUser(reviewer);
				if (tempuser.getMobile()!=null && tempuser.getMobile().length()==11)
				{
					try
					{
						Long.parseLong(tempuser.getMobile());
						mobilelist.add(tempuser.getMobile());
						templist.add(0L);
					}
					catch (Exception e){}
				}
			}
			String content=user.getRealName()+"在政务协同办公系统上送审文件："+fileName+"给您";
	    	Thread receiveT = new Thread(new BackgroundSend(mobilelist.toArray(new String[mobilelist.size()]),
	    			content,user.getCompany().getId(),user.getCompany().getName()
	    			,Constant.SHAREINFO,templist.toArray(new Long[templist.size()]),false,user));//这里的1先临时写死
			receiveT.start();

		}
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
		return error;
	}
	
	/*
	 * 信电局版本中获取送审的文档
	 */
	public static String getReviewFilesOfSend(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		return getReviewFiles(req, resp, jsonParams, user, 0);
	}
	
	/*
	 * 信电局获取审结的文档
	 */
	public static String getReviewFilesOfFiled(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		return getReviewFiles(req, resp, jsonParams, user, 1);
	}
	
	/*
	 * 信电局获取待审的文档
	 */
	public static String getReviewFilesOfTodo(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		return getReviewFiles(req, resp, jsonParams, user, 2);
	}
	
	/*
	 * 信电局获取已审的文档
	 */
	public static String getReviewFilesOfDone(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		return getReviewFiles(req, resp, jsonParams, user, 3);
	}
	
	/*
	 * 信电局获取文档
	 */
	public static String getReviewFiles(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user,int type)
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		String sort = (String) param.get("sort"); // 排序类型
		String order = (String) param.get("order"); // 升序||降序
		int index = start != null && start >= 0 ? start : 0;
		int c = count != null && count >= 0 ? count : 1000000;
		long userid = user.getId();
		FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		HashMap<String, Object> result = fss.getAllReviewFiles(userid,index,c,order,sort,type);
		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,result);
		return error;
	}
	
	/*
	 * 信电局版本中获取单个文件的审阅详情
	 */
    public static String getReviewDetails(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
    {
    	String error;
    	HashMap<String , Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	Long reviewId = Long.valueOf((String) param.get("reviewId"));
    	boolean isOwn = Boolean.valueOf((String)param.get("isOwn"));
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	ArrayList result = fss.getReviewDetailsById(reviewId,isOwn);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,result);
    	return error;
    }    
    
    /*
     * 对共享文档添加备注
     */
    public static String setSharedfileComment(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
    {
    	String error;
    	HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	int id = (Integer)param.get("personshareId");
    	String comment = (String)param.get("comment");
    	long personshareId = Long.valueOf(id);
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	fss.setCommentOfsharedfile(user, comment, personshareId);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	return error;
    }
    
    /*
     * 修改当前用户所添加的备注
     */
    public static String modifySharedfileComment(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		String error;
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String comment = (String)param.get("comment");
		int id = (Integer)param.get("commentId");
		long commentId = Long.valueOf(id);
		FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		boolean isCurrent = fss.modifyCommentOfsharedfile(comment, commentId,user);
		if(isCurrent)
		{
			error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
		}else
		{
			error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,null);
		}
		return error;
	}
    
    
    /*删除当前用户所添加的备注*/
     
    public static String delSharedfileComment(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
    {
    	String error;
    	HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	int id = (Integer)param.get("commentId");
    	long commentId = Long.valueOf(id);
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	boolean isCurrent = fss.delCommentOfsharedfile(commentId,user);
    	if(isCurrent)
    	{
    		error = JSONTools.convertToJson(ErrorCons.NO_ERROR,null);
    	}else
    	{
    		error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR,null);
    	}
    	return error;
    }
    
    
     /*个人文库文件属性中获取选中的备注的具体信息*/
     
    public static String getDetailComment(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams)
    {
    	String error;
    	HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String id = (String)param.get("commentId");
    	long commentId = Long.valueOf(id);
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	String result = fss.getOneComment(commentId);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
    	return error;
    }
    
    
      /*他人共享获取共享文件的备注*/
     
    public static String getSharedfileComment(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
    {
    	String error;
    	HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
    	String path = (String)param.get("path");
    	int id = (Integer)param.get("sharerId");
    	long sharerId = Long.valueOf(id);
    	long userId = user.getId();
    	FileSystemService fss = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	ArrayList result = fss.getCommentOfSharedfile(sharerId,userId,path);
    	error = JSONTools.convertToJson(ErrorCons.NO_ERROR,result);
    	return error;
    }
    
    
	public static String getTeamShareNums(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{//移动端获取最新协作共享下的数量
    	HashMap<String, Object> param = (HashMap<String, Object>)jsonParams.get(ServletConst.PARAMS_KEY);
    	String account = (String)param.get("account");     // 获取当前用户的账号
    	Users user = (Users) req.getSession().getAttribute("userKey");
		if (user==null && account!=null)
		{
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			user = userService.getUser(account);
		}
		HashMap<String, Object> back=getViewNums(user);
		
		return JSONTools.convertToJson(ErrorCons.NO_ERROR, back);
	}
	public static HashMap<String, Object> getViewNums(Users user)
	{
		long waitaudits=0;//待审文档数,不一定是最新待审的
		long othershares=0;//最新他人共享的文档数
		long teamshares=0;//最新协作共享的文档数
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		//最新待审文档数
		waitaudits=(Long)jqlService.getCount("select count(a) from ReviewFilesInfo as a where a.state=0 and a.reviewer.id=?", user.getId());
		othershares=(Long)jqlService.getCount("select count(a) from Personshareinfo as a where a.isNew=0 and a.userinfoBySharerUserId.id=?", user.getId());
		long hadread=(Long)jqlService.getCount("select count(a) from CustomTeamsFiles as a where a.user.id=?", user.getId());
		String sql="select sum(a.filenums) from CustomTeams as a,UsersCustomTeams as b where a.id=b.customTeam.id and b.user.id=?";
		Long obj=(Long)jqlService.findOneObjectBySql(sql,user.getId());
		long total=0;
		if (obj!=null)
		{
			total=obj.longValue();
		}
		if (total>hadread)
		{
			teamshares=total-hadread;
		}
		HashMap<String, Object> back=new HashMap<String, Object>();
		back.put("waitaudits",""+waitaudits);//待审文档数,不一定是最新待审的
		back.put("othershares",""+othershares);//最新他人共享的文档数
		back.put("teamshares",""+teamshares);//新增协作共享的文档数
		return back;
	}
	private static void changeTeamFiles(String[] paths,boolean isadd)
	{//更改CustomTeams中的总文件数,path为改空间的所有文件,isadd为增加还是减少
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String spaceid=paths[0].substring(0,paths[0].indexOf("/"));
			List<CustomTeams> ctlist=(List<CustomTeams>)jqlService.findAllBySql("select a from CustomTeams as a where a.spaceUID=?", spaceid);
			if (ctlist!=null && ctlist.size()>0)
			{
				JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
				int nums=jcrService.getFilenums(new String[]{spaceid+"/Document"});//获取该协作共享的所有文件数
				CustomTeams customTeams=ctlist.get(0);
				customTeams.setFilenums(nums);
				jqlService.update(customTeams);//增加文件进行增加数量，用户重设自定义组权限或加人时要重置一下
				if (!isadd)//移除或删除
				{
					for (int i=0;i<paths.length;i++)
					{
						jqlService.excute("delete from CustomTeamsFiles where paths like '"+paths[i]+"%'");
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public static String getFileBaosong(HttpServletRequest req,HttpServletResponse resp,HashMap<String, Object> jsonParams,Users user)
	{
		
		FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String account = (String) param.get("account"); // 登录的账户
    	user = (Users) req.getSession().getAttribute("userKey");
		if (user==null && account!=null)
		{
			UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
			user = userService.getUser(account);
		}
		Integer start = (Integer) param.get("start"); // 分页起始位置
		Integer count = (Integer) param.get("count"); // 每页显示的数量
		String filertag = (String) param.get("filtag"); //筛选数据
		String sort = (String) param.get("sort"); // 排序类型
		String order = (String) param.get("order"); // 升序||降序
//		System.out.println("filertag===="+filertag+",sort==="+sort+",order==="+order);
		if (order == null || order.equals(""))
		{
			order = "DESC";
		}
		if (sort == null || sort.equals(""))
		{
			sort = "sendTime";
		}
		if ("orgname".equals(sort))
		{
			sort="org.name";
		}
		if("".equals(filertag))
		{ 
			filertag = null;
		}
		if (filertag!=null)
		{
			filertag += ",";
		}	
		String fileListType = (String) param.get("fileListType"); // 文档列表类型（个人文件、、群组文档、他人共享、我的贡献,回收站）
		int index = start != null && start >= 0 ? start : 0;
		int c = count != null && count >= 0 ? count : 1000000;
		String keyword = (String)param.get("keyword");
		List values=service.getBSCB(user.getId(), start, count, sort, order);
		Integer length=(Integer)values.get(0);
		values.remove(0);
		HashMap<String, Object> retJson = new HashMap<String, Object>();
		retJson.put("fileListSize", length);
		retJson.put("fileList", values);
		return  JSONTools.convertToJson(ErrorCons.NO_ERROR, retJson);
	}
	
	/**
	 * 获取所有文件元数据
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getAllMetadata(HttpServletRequest req, HttpServletResponse resp, HashMap<String, Object> jsonParams)  throws ServletException,  IOException
	{
		HashMap<String, Object> params = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		FileSystemService filesystemservice = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		List<EntityMetadata> metadataList = filesystemservice.getAllMetadata();
		List<Object> result = new ArrayList<Object>();
		for(EntityMetadata metadata : metadataList)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code", metadata.getCode());
			map.put("filePath", metadata.getFilePath());
			map.put("metadataName",metadata.getMetadataName());
			map.put("metadataValue", metadata.getMetadataValue());
			result.add(map);
		}
		String error = JSONTools.convertToJson(ErrorCons.NO_ERROR, result);
		return error;
	}
	public static Fileinfo[] getFileList(String companyID, String userID, String path)
    {
        try
        {
        	JCRService jcrService = (JCRService) ApplicationContext
    				.getInstance().getBean(JCRService.NAME);
            List list = jcrService.listPageFileinfos(userID, path, 0, 1000);
            if (null != list && !list.isEmpty())
                list.remove(0);
            ArrayList<Fileinfo> fileinfoList = new ArrayList<Fileinfo>(list);
            if (list != null)
            {
                return fileinfoList.toArray(new Fileinfo[list.size()]);
            }
            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new Fileinfo[]{};
    }
	/**
     * 获得用户对某个资源拥有的action值
     * @param userId
     * @param path
     * @return
     */
    public static Long getFileSystemAction(Long userId, String path)
    {
        PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
        return service.getFileSystemAction(userId, path);
    }
    /**
	 * 获得用户对某个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限，
	 * 直到获取到空间根为止。
	 * @param userId 用户Id
	 * @param path 具体的文件资源路径
	 * @param treeFlag 是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	public static Long getFileSystemAction(Long userId, String path, boolean treeFlag)
	{
		PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
		return service.getFileSystemAction(userId, path, treeFlag);
	}

	/**
	 * 获得用户对多个资源拥有的action值, 如果用户对该文件资源本身没有设置过权限，
	 * 则根据treeFlag标记是否获取其父的权限， 如果父没有设置权限，则在获取其父的权限，
	 * 直到获取到空间根为止。该方法的paths参数，除了最后一级路径不同外，以后各级的路径
	 * 必须相同，即是该方法中的paths参数，是同一级父目录下的不同文件及文件夹的权限判断。
	 * @param userId 用户Id
	 * @param paths 具体的文件资源路径
	 * @param treeFlag 是否递归获取其父的权限。如果为false，则不递归获取权限，否则则递归获取权限
	 * @return
	 */
	public static List<Long> getFileSystemAction(Long userId, String[] paths, boolean treeFlag)
	{
		PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
		return service.getFileSystemAction(userId, paths, treeFlag);
	}
    /**
     * 获得用户对多个资源拥有的action值
     * @param userId
     * @param path
     * @return
     */
    public static List<Long> getFileSystemAction(Long userId, String[] path)
    {
        PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
        int len = path.length;
        List<Long> premitList = new ArrayList<Long>();
        for (int i = 0; i < len; i++)
        {
            Long permit = service.getFileSystemAction(userId, path[i]);
            if (permit != null)
            {
                premitList.add(permit);
            }
        }
        return premitList;
    }
    public static void copy(Long userId, DataHolder srcPathHolder, String targetName, int count,
            List<String> existNames)
    {
    	copyFiles(userId, srcPathHolder, targetName, count,existNames ,false);
    }
    public static void copyFiles(Long userId, DataHolder srcPathHolder, String targetName, int count,
            List<String> existNames ,boolean isReplace )
    {
    	FileSystemService fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	if(!isReplace)
    	{
    		fileSystemService.copy(userId, srcPathHolder, targetName, count, existNames);
    	}else{
    		fileSystemService.copyAndReplace(userId, srcPathHolder, targetName, count, existNames);
    	}
    	UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
        String[] paths = srcPathHolder.getStringData();
        Users userInfo = userService.getUser(userId);
        LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
        for (String path : paths)
        {
          //  FileOperLog fileOperLog = new FileOperLog("", path, userInfo.getUserName(),userInfo.getRealName(), "", "复制", "");
           // LogsUtility.logToFile(fileOperLog.getUserName(), DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
            //LogUtility.log(logDir, fileOperLog);
            logService.setFileLog(userInfo, "", LogConstant.OPER_TYPE_COPY_FILE,  path);
        }
    }
    public static void moveFile(String companyID, Long userId, DataHolder srcPath, String targetName)
    {
    	moveFiles(companyID, userId, srcPath, targetName,0);
    }
    public static void moveFiles(String companyID, Long userId, DataHolder srcPath, String targetName,int replace)
    {
        try
        {
            //System.out.println("moveFile srcPath::"+srcPath+", targetname：：："+targetName);
            if (Constant.DOC_PUBLIC.equals(companyID))
            {
                UserService userService = (UserService)ApplicationContext.getInstance().getBean(
                    UserService.NAME);
                String[] srcName = srcPath.getStringData();
                //userService.deleteAllPower(srcName);
            }
            FileSystemService fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
            fileSystemService.moveFile(companyID, userId, srcPath, targetName,replace);
            UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
            Users userInfo = userService.getUser(userId);
            String[] paths = srcPath.getStringData();
            LogServices logService = (LogServices) ApplicationContext.getInstance().getBean(LogServices.NAME);
            for (String path : paths)
            {
               // FileOperLog fileOperLog = new FileOperLog("", path, userInfo.getUserName(),userInfo.getRealName(), "", "移动", "");
                //LogsUtility.logToFile(fileOperLog.getUserName(),DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
                //LogUtility.log(logDir, fileOperLog);
                logService.setFileLog(userInfo, "", LogConstant.OPER_TYPE_MOVE_FILE,  path);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
