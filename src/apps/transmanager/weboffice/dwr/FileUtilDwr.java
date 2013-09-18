package apps.transmanager.weboffice.dwr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import apps.moreoffice.ext.share.QueryDb;
import apps.moreoffice.ext.share.ShareFileTip;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.GridUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;
import apps.transmanager.weboffice.util.server.FileMD5;


public class FileUtilDwr {
	
	private JCRService jcrService;
	private FileSystemService fileSystemService;
	private MessagesService messagesService;
	
	
	
	//private UserService userService;
	//private LicenseService licenseS;
	//private IPersonshareinfoDAO personShareDao = (IPersonshareinfoDAO) ApplicationContext.getInstance().getBean("PersonshareinfoDAO");
	//private IGroupmemberinfoDAO groupmemberinfoDAO = (IGroupmemberinfoDAO) ApplicationContext.getInstance().getBean("GroupmemberinfoDAO");
	//private IGroupmembershareinfoDAO groupmembershareinfoDAO = (IGroupmembershareinfoDAO) ApplicationContext.getInstance().getBean("GroupmembershareinfoDAO");
	
	
	public void setJcrService(JCRService jcrService) {
		this.jcrService = jcrService;
	}

	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	/**
	 * 获得所有所需的信息
	 * @param req 请求
	 * @return 请求信息
	 */
	public Map<String,Object> getTipFiles(HttpServletRequest req)
	{
		Map<String,Object> map = null;
		try
		{
		List<Fileinfo> readFiles = getRecentReadFiles(req);
		List<Fileinfo> shareFiles = getShareFiles(req);
		
		if(null!=shareFiles && shareFiles.size()>5)
		{
			shareFiles = shareFiles.subList(0, 5);
		}
		
//		if(null!=shareFiles)
//		{
//			Collections.reverse(shareFiles);
//		}
		
		if(null!=readFiles && readFiles.size()>5)
		{
			readFiles = readFiles.subList(readFiles.size()-5, readFiles.size());
		}
		
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		if((null!=readFiles && !readFiles.isEmpty()) ||
				(null!=shareFiles && !shareFiles.isEmpty())
				)
		{
			map = new HashMap<String, Object>();
			map.put("readFiles", readFiles);
			map.put("shareFiles", shareFiles);
			map.put("userKey", userInfo);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public Map<String,Object> getAllWorkFiles(int goPage,int pageSize,HttpServletRequest req)
	{
		return getAllWorkFilesTop(goPage,pageSize,0,req);
	}
	/**
	 * 获得最近的工作空间文档
	 * @param goPage 要跳转的页面
	 * @param pageSize 每页数量
	 * @param req 请求信息
	 * @return 最近的工作空间文档
	 */
	public Map<String,Object> getAllWorkFilesTop(int goPage,int pageSize,int stateid,HttpServletRequest req)
	{
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		Page page = GridUtil.getGridPage(goPage, pageSize);
		//long totalRecord = messagesService.getAllSpaceNewMessagesCountByUseId(user.getId());
		long totalRecord = FilesHandler.getAuditFileCount(user.getId());
		if(totalRecord==0L)
		{
			return null;
		}
		Map<String,Object> resultMap = new HashMap<String, Object>();
		page.retSetTotalRecord((int)totalRecord);
		/*List<Messages> messageList = FilesHandler.getAllSpaceNewMessagesByUserId(user.getId(), page.getCurrentRecord(), pageSize);
		List<Fileinfo> fileInfo = new ArrayList<Fileinfo>();
		for(Messages meg : messageList)
		{
			Fileinfo file = new Fileinfo();
			file.setAuthor(meg.getUser().getRealName());
			file.setFileName(meg.getContent());
			file.setPrimalPath(meg.getAttach());
			file.setFileSize(meg.getSize());
			file.setPermit(meg.getPermit() != null ? meg.getPermit().intValue() : 0);
			file.setCreateTime(meg.getDate());
			fileInfo.add(file);
		}*/
		List<Fileinfo> fileInfo = FilesHandler.getAuditFile(user.getId(), page.getCurrentRecord(), pageSize,stateid);
		fileInfo = resolverFileList(fileInfo, req);
		resultMap.put("workCount", totalRecord);
		resultMap.put("workFiles", fileInfo);
		resultMap.put("page", page);
		resultMap.put("userInfo", user);
		return resultMap;
	}
	
	/**
	 * 获得项目的最新10个文档
	 * @param start 起始位置
	 * @param count 所取条数
	 * @param req 请求信息
	 * @return 所取文档
	 */
	public List<Messages> getWorkFiles(String spaceUID,int start,int count,HttpServletRequest req)
	{
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		List<Messages> workFiles = messagesService.getSpaceNewMessagesByUserId(user.getId(), spaceUID, start, count);
		return workFiles;
	}
	
	
	/**
	 * 获得最近共享的文件
	 * @param req 请求
	 * @return 请求信息
	 */
	public Map<String,Object> getRecShareFiles(HttpServletRequest req)
	{
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		if(userInfo==null)
			return null;
		Map<String,Object> map = null;
		try
		{
		List<Fileinfo> shareFiles = getShareFiles(req);
		
		if(null!=shareFiles && shareFiles.size()>5)
		{
			shareFiles = shareFiles.subList(0, 5);
		}
		
//		if(null!=shareFiles)
//		{
//			Collections.reverse(shareFiles);
//		}
		
		if(null!=shareFiles && !shareFiles.isEmpty())
		{
			map = new HashMap<String, Object>();
			map.put("shareFiles", shareFiles);
			map.put("userKey", userInfo);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 添加下载日志
	 * @param path 文件路径
	 * @param author 文件作者
	 * @param name 文件名称
	 * @param req 请求信息
	 */
	public void LogDown(String path,String author,String name,HttpServletRequest req){
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		ShareFileTip queryTip=new ShareFileTip();
  		Fileinfo fileinfo = new Fileinfo();
  		fileinfo.setAuthor(author);
  		fileinfo.setFileName(name);
  		fileinfo.setPathInfo(path);
		Long fid = queryTip.queryFileID(path);
		if(fid==null)
		{
			long authorid =(queryTip.queryUserinfoID(author)).longValue();
			queryTip.insertFileinfo(fileinfo, authorid);
			fid = queryTip.queryFileID(path);
		}
		queryTip.insertFileLog(fid.longValue(),userInfo.getId(),16);
	}
	
	/**
	 * 获得最近阅读的文件信息
	 * @param type 获取类型。0表示个人空间的，1表示公共空间的
	 * @param req 请求
	 * @return 请求信息
	 */
	public Map<String,Object> getRecReadFiles(int type, HttpServletRequest req)
	{
		Map<String,Object> map = null;
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		if(userInfo==null)
			return null;
		try
		{
		List<Fileinfo> readFiles = getUserRecentFile(type, req);//getRecentReadFiles(req);
		if(null!=readFiles && readFiles.size()>5)
		{
			readFiles = readFiles.subList(readFiles.size()-5, readFiles.size());
		}

		
		if(null!=readFiles && !readFiles.isEmpty())
		{
			readFiles = resolverFileList(readFiles, req);
			map = new HashMap<String, Object>();
			map.put("readFiles", readFiles);
			map.put("userKey", userInfo);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
//	/**
//	 * 获得最新消息
//	 * @param req
//	 * @return
//	 */
//	public List<Fileinfo> getReceiveUser(HttpServletRequest req) {
//		List<Fileinfo> noticeFileList = null;
//		Users userInfo = (Users) req.getSession().getAttribute("userKey");
//		List<ReceiveUser> noticeFiles = userService.getReceiveUser(userInfo, 0, 5);
//		if(null!=noticeFiles && !noticeFiles.isEmpty())
//		{
//			noticeFileList = new ArrayList<Fileinfo>();
//			for(ReceiveUser receiveUser : noticeFiles)
//			{
//				UserNotices userNotice = receiveUser.getNotices();
//				Fileinfo fileinfo = new Fileinfo();
//				fileinfo.setAuthor(userNotice.getUserinfo().getUserName());
//				fileinfo.setPrimalPath(userNotice.getContent());
//				fileinfo.setFileName(userNotice.getTitle());
//				noticeFileList.add(fileinfo);
//			}
////			if(null!=noticeFileList && !noticeFileList.isEmpty()){
////				noticeFileList = resolverFileListTwo(noticeFileList,req);
////			}
//			
//		}
//		return noticeFileList;
//	}

	/**
	 * 获取最新共享文件
	 * @param req 请求信息
	 * @return 最新共享文件记录集
	 */
	@SuppressWarnings("unchecked")
	public List<Fileinfo> getShareFiles(HttpServletRequest req) {
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		List<Fileinfo> fileList = null;
		Map<String,Object> fileListTempMap = null;
		List<String> fileListTemp = null;
		List<Fileinfo> newFileList = null;
		try{
			fileListTempMap  = fileSystemService.findByMemberAndNew(userInfo.getId(),-1,0,0,5);
			fileListTemp = (List<String>) fileListTempMap.get("paths");
			if(null!=fileListTemp && !fileListTemp.isEmpty())
			{
				String[] filePath = (String[]) fileListTemp.toArray(new String[fileListTemp.size()]);
				fileList = (List)jcrService.getFileinfos(req,userInfo.getEmail().replace("@", "_"), filePath);
				
				if(null!=fileList && !fileList.isEmpty())
				{
					newFileList=new ArrayList<Fileinfo>();
					for(Fileinfo fileinfo : fileList)
					{
						if(!fileinfo.isFold() && !"out_linker".equals(fileinfo.getKeyWords()))
						{
							fileinfo.setPrimalPath(fileinfo.getPathInfo());
							newFileList.add(fileinfo);
						}
						
						if(!fileinfo.isFold() && "out_linker".equals(fileinfo.getKeyWords()))
						{
							fileinfo.setImageUrl(fileinfo.getPrimalPath());
							fileinfo.setPrimalPath(fileinfo.getPathInfo());
							
							newFileList.add(fileinfo);
						}
						
						
					}
				}
				if(null!=newFileList && !newFileList.isEmpty())
				{
					Map<String,Integer> permitMap = (Map<String, Integer>) fileListTempMap.get("permits");
					Map<String,String> creatorMap = (Map<String,String>) fileListTempMap.get("creatorName");
					Map<String,String> megMap = (Map<String, String>) fileListTempMap.get("megs");
					for(Fileinfo fileInfo : newFileList)
					{
							if(null!=permitMap.get(fileInfo.getPrimalPath()))
							{
								String key = fileInfo.getPrimalPath();
								fileInfo.setPermit(permitMap.get(key));
								/*if((permitMap.get(key) & MainConstant.CAN_ALL) == MainConstant.CAN_ALL)
								{
									fileInfo.setPermit(10);//完全控制
								}
								else
								{
									if((permitMap.get(key) & MainConstant.READONLY) == MainConstant.READONLY){
										 fileInfo.setPermit(0);
									}
									if((permitMap.get(key) & MainConstant.ISDOWN) == MainConstant.ISDOWN){
										 fileInfo.setPermit(1);
									}
									if((permitMap.get(key) & MainConstant.READANDWRITE) == MainConstant.READANDWRITE){
										 fileInfo.setPermit(2);
									}
									if((permitMap.get(key) & MainConstant.READONLY) == MainConstant.READONLY && (permitMap.get(key) & MainConstant.ISDOWN)==MainConstant.ISDOWN )
									{
										 fileInfo.setPermit(3);
									}
									if((permitMap.get(key) & MainConstant.ISDOWN) == MainConstant.ISDOWN && (permitMap.get(key) & MainConstant.READANDWRITE)==MainConstant.READANDWRITE )
									{
										fileInfo.setPermit(4);
									}
									if((permitMap.get(key) & MainConstant.CAN_NEVAGATION) == MainConstant.CAN_NEVAGATION)
									{
										fileInfo.setPermit(5);
									}
									if((permitMap.get(key) & MainConstant.ISDOWN) == MainConstant.ISDOWN && (permitMap.get(key) & MainConstant.CAN_NEVAGATION)==MainConstant.CAN_NEVAGATION )
									{
										fileInfo.setPermit(6);
									}
								}*/
							}
							if(null!=creatorMap.get(fileInfo.getPrimalPath()))
							{
								fileInfo.setAuthor(creatorMap.get(fileInfo.getPrimalPath()));
							}
							if(null!=megMap.get(fileInfo.getPrimalPath()))
							{
								fileInfo.setShareCommet(megMap.get(fileInfo.getPrimalPath()));
							}
							
						}
					}
				newFileList = resolverFileList(newFileList,req);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return newFileList;
	}
	
	
	private List<Fileinfo> resolverFileList(List<Fileinfo> fileList,
			HttpServletRequest req) { 
		if(null!=fileList && !fileList.isEmpty())
		{
			for(Fileinfo fileinfo : fileList)
			{
				if("out_linker".equals(fileinfo.getKeyWords())){
					continue;
				}
				
				String hyperlink = getRealDownFilePath(fileinfo.getPrimalPath(),fileinfo.getFileName(),req);
				String fileName = fileinfo.getFileName().toLowerCase();
				if(fileName.endsWith(".jpg") || fileName.endsWith(".JPG") || fileName.endsWith(".gif") || fileName.endsWith(".png") || fileName.endsWith(".PNG")
						|| fileName.endsWith(".gif") || fileName.endsWith(".GIF") || fileName.endsWith(".bmp") || fileName.endsWith(".BMP"))
				{
           	      String realhyperlink = "";
           	 	//"http://127.0.0.1:8888/static/downloadService?action=sendmaildown&sendtempfilename=1275031470750.doc&filename=jackrabbit.doc"
	           	 if(hyperlink != null)
	           	 {

	           		 String[] strs0 = hyperlink.split("\\?");
	           		 if(strs0.length > 1)
	           		 {
	           			 String[] strs1 = strs0[1].split("&");
	           			 if(strs1.length > 1)
	           			 {

	           				 realhyperlink = /*req.getScheme() + "://" + QueryDb.getIpName(req.getServerName()) + ":"
	           		        + req.getServerPort() + */req.getContextPath()
	           		        //+"/static/open2.jsp"+"?"+strs1[1];
	           		        +"/data/sendmailfile/"+strs1[1].split("=")[1];
	           				//System.out.println(realhyperlink);
	           			 }
	           		 }
	           	 }
	           	 fileinfo.setImageUrl(realhyperlink);
				}
				fileinfo.setPathInfo(hyperlink);
				
			}
		}
		return fileList;
	}
	
	private List<Fileinfo> resolverFileListTwo(List<Fileinfo> fileList,
			HttpServletRequest req) {
		if(null!=fileList && !fileList.isEmpty())
		{
			for(Fileinfo fileinfo : fileList)
			{
				String hyperlink = fileinfo.getPrimalPath();
           	 	String realhyperlink = "";
           	 	//"http://127.0.0.1:8888/static/downloadService?action=sendmaildown&sendtempfilename=1275031470750.doc&filename=jackrabbit.doc"
	           	 if(hyperlink != null)
	           	 {
	           		 String[] strs0 = hyperlink.split("\\?");
	           		 if(strs0.length > 1)
	           		 {
	           			 String[] strs1 = strs0[1].split("&");
	           			 if(strs1.length > 1)
	           			 {
	           				 //System.out.println(GWT.getHostPageBaseURL());
	           				 realhyperlink = /*req.getScheme() + "://" + req.getServerName() + ":"
	           		        + req.getServerPort() +*/ req.getContextPath()+"/static/open2.jsp"+"?"+strs1[1];
	           			 }
	           		 }
	           	 }
				fileinfo.setPrimalPath(realhyperlink);
				
			}
		}
		return fileList;
	}


//	public List<Fileinfo> getGroupShareFiles(HttpServletRequest req) 
//	{
//		Users userInfo = (Users) req.getSession().getAttribute("userKey");
//		List<Fileinfo> fileList = null;
//		List<String> fileListTemp = null;
//		List<UsersGroups> group = fileSystemService.findGroupByUserId(userInfo.getId());
//		try
//		{
//			fileListTemp = fileSystemService.findByGroupAndNew(group.get(0).getGroupinfo().getGroupId(),0);
//		
//			if(null!=fileListTemp && !fileListTemp.isEmpty())
//			{
//				String[] filePath = (String[]) fileListTemp.toArray(new String[fileListTemp.size()]);
//				fileList = (List)jcrService.getFileinfos(userInfo.getEmail().replace("@", "_"), filePath);
//				if(null!=fileList && !fileList.isEmpty())
//				{
//					for(Fileinfo fileinfo : fileList)
//					{
//						fileinfo.setPrimalPath(fileinfo.getPathInfo());
//					}
//				}
//				
//				
//				fileList = resolverFileList(fileList,req);
//				
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		return fileList;
//	}

	/**
	 * 获得最新阅读过的文件
	 * @param req 请求信息
	 * @return 最新阅读文件列表
	 */
	public List<Fileinfo> getRecentReadFiles(HttpServletRequest req)
	{
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		List<Fileinfo> fileList = null;
		try {
			fileList = jcrService.getRecentRWtFile(req,userInfo.getEmail().replace("@", "_"),FileConstants.OPENLIST);
			fileList = resolverFileList(fileList,req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}
	
	/**
	 * 获得最新阅读过的文件
	 * @param req 请求信息
	 * @return 最新阅读文件列表
	 */
	public List<Fileinfo> getRecentEditFiles(HttpServletRequest req)
	{
		Users userInfo = (Users) req.getSession().getAttribute("userKey");
		List<Fileinfo> fileList = null;
		try {
			fileList = jcrService.getRecentRWtFile(userInfo.getEmail().replace("@", "_"),FileConstants.SAVELIST);
			//fileList = resolverFileList(fileList,req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}

	public String getRealDownFilePath(String path,String fileName,HttpServletRequest request) {
		String tempFolder = WebConfig.sendMailPath;
		try
		{
		InputStream in = jcrService.getContent("",path, false);                
        String tempfilenames = System.currentTimeMillis()
            + fileName.substring(fileName.lastIndexOf('.'));
        File file = new File(tempFolder + File.separatorChar + tempfilenames);   

        if(!file.getParentFile().exists())
        {
        	file.getParentFile().mkdirs();
        }
        file.createNewFile();
        byte[] b = new byte[8 * 1024];
        int len = 0;
        FileOutputStream fo = new FileOutputStream(file);
        while ((len = in.read(b)) > 0)
        {
            fo.write(b, 0, len);
        }
        fo.close();
        String httpUrls = /*request.getScheme() + "://" + request.getServerName() + ":"
        + request.getServerPort() +*/ request.getContextPath() + "/static/downloadService?"
        + "action=sendmaildown" + "&sendtempfilename="
        + URLEncoder.encode(tempfilenames, "UTF-8") + "&filename="
        + URLEncoder.encode(fileName, "UTF-8")
        +"&realpath="+URLEncoder.encode(path, "UTF-8");
        httpUrls = QueryDb.getIpName(httpUrls);

        return httpUrls;
		}catch(Exception e)
		{
			e.printStackTrace();
			return path;
		}
		
	}

	/**
	 * 获取上传文件时需要的信息数据
	 * @param request 请求信息
	 * @return 数据信息
	 */
	public Map<String,String> uploadFile(HttpServletRequest request)
	{
        Map<String,String> resultMap = new HashMap<String,String>();
        Users userinfo = (Users) request.getSession().getAttribute("userKey");
        String baseUrl = /*request.getScheme() + "://" + request.getServerName() + ":"
	        + request.getServerPort() +*/ request.getContextPath();
        baseUrl=QueryDb.getIpName(baseUrl);
        if(null!=userinfo)
        {
        	String path = userinfo.getEmail().replace("@","_").concat("/").concat(FileConstants.DOC);
        	try {
				List<String> filenameList = jcrService.getFileForUpload(path);
				int len = filenameList.size();
				String serverFP ="";
                for (int i = 0; i < len; i++)
                {
                    if ( i > 0)
                    {
                        serverFP += "?" + filenameList.get(i);
                    }
                    else
                    {
                        serverFP += filenameList.get(i);
                    }
                }
                
                baseUrl = baseUrl + "/data/uploadfile";
                String email = userinfo.getEmail();
                resultMap.put("baseUrl", baseUrl);
                resultMap.put("email", email);
                resultMap.put("path", path);
                resultMap.put("serverFP", serverFP);
                
			} catch (RepositoryException e) {
				e.printStackTrace();
				resultMap = null;
			}
        }
		return resultMap;
	}
	
	public String getEIOVersion()
    {
        try
        {
            Properties p = new Properties();
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "conf/appletConfig.properties"));
            String versionid = p.getProperty("officeVersion");
            return versionid;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
	
	
	/**
	 * 插入签名 
	 * @param fileName
	 * @param filePath
	 * @param content
	 * @param req
	 * @return  -1代表插入失败,0代表已经签名过,>0代表签名成功
	 */
	public int insertSign(String fileName,String filePath,String content,HttpServletRequest req)
	{
		Users userInfo = (Users) req.getSession().getAttribute("userKey");

//		String newPath = WebTools.converStr(filePath);
		return fileSystemService.insertSign(userInfo.getId(), fileName, filePath, content,"1");
	}
	
	public String getSignMessage(String userId,String filePath,HttpServletRequest req)
	{
		return fileSystemService.getSignMessage(Long.valueOf(userId), filePath);
	}
	
	public List getAllSign(String filePath,HttpServletRequest request)
	{
		Users userinfo = (Users) request.getSession().getAttribute("userKey");
		return fileSystemService.findAllSign(userinfo.getId(), filePath);
	}
	
	public String getMessageBySignId(String id)
	{
		return fileSystemService.findSignMessageById(Long.valueOf(id));
	}
	
	public String getFileHashCode(String path)
	{
		try{

//			String newPath = WebTools.converStr(path);

			InputStream in = jcrService.getFileContent(path);
			String encrypt = FileMD5.getStreamHash(in,"MD5");
			return encrypt;
		}catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public List getAllSystemSign(String filePath,HttpServletRequest request)
	{
		List result = new ArrayList();
		try {
			Users userinfo = (Users) request.getSession().getAttribute("userKey");
			InputStream in = jcrService.getFileContent(filePath);
			String encrypt = FileMD5.getStreamHash(in,"MD5");
			String author = jcrService.getFileAuthor(filePath); 
			result.add(author);
			List list =  fileSystemService.findAllSystemSign(userinfo.getId(), filePath,encrypt);
			result.add(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	  /**
     * 得到用户最近操作的文档，目前需求是要打开及新建保持的文档,包括操作的所有空间中的文档。
     * @param type 获取类型。0表示个人空间的，1表示公共空间的
     * @return 最近使用文档
     * 
     */
	public List<Fileinfo> getUserRecentFile(int type, HttpServletRequest req){
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return null;
		}
		List<Fileinfo> fileInfos = fileSystemService.getUserRecentFile(user.getId(), type);
		if(fileInfos==null || fileInfos.isEmpty())
		{
			return null;
		}
		for(Fileinfo file : fileInfos)
		{
			file.setPrimalPath(file.getPathInfo());
		}
		fileInfos = resolverFileList(fileInfos, req);
		return fileInfos;
	}
	
	
}
