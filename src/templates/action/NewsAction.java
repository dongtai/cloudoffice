package templates.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import templates.objectdb.News;
import templates.objectdb.NewsAttached;
import templates.objectdb.NewsType;
import templates.service.InitService;
import templates.service.NewsAttachedService;
import templates.service.NewsService;
import templates.service.PageBean;
import apps.moreoffice.ext.share.QueryDb;
import apps.transmanager.weboffice.client.constant.MainConstant;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;

import com.opensymphony.xwork2.ActionContext;

import flowform.AllSupport;

public class NewsAction extends AllSupport {

	private HttpServletRequest request = ServletActionContext.getRequest();
	private HttpServletResponse response = ServletActionContext.getResponse();
	private Map<String, Object> session = ActionContext.getContext().getSession();
    private ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()); 
	private NewsService newsService = (NewsService) ctx.getBean("newsService");
	private InitService initService = (InitService) ctx.getBean("initService");
	private NewsAttachedService newsAttachedService = (NewsAttachedService) ctx.getBean("newsAttachedService");
	private int page;
	private PageBean pageBean; 
	private News newsInfo = new News();
	private List<News> news = new ArrayList<News>();
	private NewsType newsType;
	private List<NewsType> newsTypes = new ArrayList<NewsType>();
	private File[] upload;
	private String[] uploadContentType;
	private String[] uploadFileName;
	private String savePath=request.getRealPath("upload");
	private String pagePath;
	private String jsonResult;
	private String rssResult;
	private NewsAttached newsAttached = new NewsAttached();
	private List<NewsAttached> newsAttacheds = new ArrayList<NewsAttached>();
//	private String ip;

	public String getAttachName(String tid)
	{
		try
		{
			return newsAttachedService.get(Long.valueOf(tid)).getAttached();
		}
		catch (Exception e)
		{
			return null;
		}
	} 
	public String encodeDownloadName(String useragent, String name)
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
        catch(Exception e)
        {
            LogsUtility.error(e);
        }
        return name;
    }
	public void delAtt(){
		try {
		String id = request.getParameter("nid");
		String aname = request.getParameter("aname");
		newsInfo.setNewId(Long.parseLong(id)); 
		newsInfo=newsService.get(Long.parseLong(id));
		Long aid= newsInfo.getAttached().getTid();
		aname = newsInfo.getAttached().getAttached();
		String rootPath =request.getSession().getServletContext().getRealPath("");  
		String tarUrl1 =  rootPath+"data"+File.separator+"notice"+File.separator+"attached"+File.separator+aname;  
		File file = new File(tarUrl1);
		JSONObject jo1 = new JSONObject();  
		 jo1.element("state", "error");
		 boolean aExists = file.exists();
		 boolean aFile = file.isFile();
		   if(aExists&&aFile){
		    boolean d = file.delete();
		    if(d){
		    	newsInfo.setAttached(null);
				newsService.update(newsInfo);
				newsAttachedService.delete(aid);
		    	jo1.element("state", "success");  
		    }else{
		    	jo1.element("state", "error");  
		    }
		   }else{
			   newsInfo.setAttached(null);
			   newsService.update(newsInfo);
			   newsAttachedService.delete(aid);
		   }
		  
	    String responseText = jo1.toString();
		response.getWriter().write(responseText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String findNews(){
		try {
		request.setCharacterEncoding("UTF-8");
		String node = request.getParameter("node");//公告功能模块的序号
		String email = request.getParameter("email");
		String path = request.getParameter("path");
		Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		Long userid = userInfo.getId();
		String username = userInfo.getUserName();
		if(node == null){
			node = (String) session.get("node");
		}
		if(username == null){
			username = (String)session.get("userName");
		}
		if(session.get("userID") == null){
			session.put("userID", userid);
			session.put("userName", username);
			session.put("email",email);
			session.put("path", path);
		}
		
		String serviceURL = "&http://"+getIP()+"/static/UploadService&userID="+session.get("userID")+"&userName="+session.get("userName")+"&email="+session.get("email")+"&path="+session.get("path");
		String openUrl="http://"+getIP()+"/static/open.jsp?commandId=public&appType=OPEN" +
				"&action=open&fileName=http://"+getIP()+"/data/notice/attached/";
		session.put("openUrl", openUrl);
		session.put("serviceURL", serviceURL);
		
	    String typeid =request.getParameter("type"); 
		if("0".equals(typeid) && username !=null){
			
			
		    /**
		     * 权限判断采用位运算来判断，目前总共有21位：如1 0000 0000 0000 0000 0000
		     * 最高位为公告审核员权限，次高位为公告发布员权限
		     * 要判断是否具有公告审核员权限，只需判断flag = (1 0000 0000 0000 0000 0000 = (1 0000 0000 0000 0000 0000 & 权限值的二进制表示 ))值为true或false即可
		     * 要判断是否具有公告发布员权限，只需判断flag = (0 1000 0000 0000 0000 0000 = (0 1000 0000 0000 0000 0000 & 权限值得二进制表示 ))值为true或false即可
		     * 而在此处，我们获取的权限值为十进制long型，直接用十进制进行 & 运算也是可以的，两边的数字都要是十进制
		     *  0100 0000 0000 0000 0000的十进制为16384---15// 524288---19
		     * 0 1000 0000 0000 0000 0000的十进制为32768----16 //1048576---20
		     * 的十进制为32768
		     * 公告审核员的权限更高一些，我们首先判断
		     */
			PermissionService service = (PermissionService)apps.transmanager.weboffice.service.context.ApplicationContext.getInstance().getBean("permissionService");
			Long permission = service.getSystemPermission(userid);
		    boolean isNoticePub = (16384 == (16384 & permission));	//是否是公告发布员
		    boolean isNoticeAudit = (32768 == (32768 & permission));	//是否是公告审核员
		    //isNoticeAudit = true;
		    if(isNoticeAudit || userInfo.getRole()==((short)MainConstant.PART_ADMIN)){
		    	session.put("roleType","2");	//公告审核员设为2
		    }else{
		    	if (isNoticePub || userInfo.getRole()==((short)MainConstant.PART_ADMIN)) {
					session.put("roleType","1");	//公告发布员设为1
				}else{
					session.put("roleType","0");	//普通人员
				}
		    }
		    /*采用无符号右移操作
			if (roleType != null && (roleType >>> 15 & 1L) == 1L)
		    {
		    	//有编辑权限公司动态
				session.put("roleType", "3");
		    }
			*/
		}
		
//		and orgid="+getTopOrganizations()
		String hql ="from News n where 1=1 and orgid="+ userInfo.getCompany().getId(); 
		
		if(typeid!=null && !"0".equals(typeid)){
			hql +=" and n.newType='"+typeid+"' ";
		}else if(session.get("type")!=null && !"0".equals(typeid) && typeid==null && !"0".equals(session.get("type"))){
			hql +=" and n.newType='"+session.get("type")+"' ";
			typeid=(String)session.get("type");
		}
		
		if(node.equals("2")){//我发布的公告
			hql += " and n.userid='"+userid+"' ";
		}else if(node.equals("3")){//待审核的公告
			hql += " and n.status=0 ";
		}else if(node.equals("4")){//公告管理
			hql += " and n.status=1 ";
		}else{//公告栏
			hql += " and n.isPublic=1 and n.status=1 ";
		}
		hql +="order by n.new_date desc";
		news = newsService.find(hql);
		int allRow=news.size(); 
		this.pageBean = newsService.queryForPage(hql ,12,page,allRow);
		int total=this.pageBean.getTotalPage();
		
        List tLlist = new ArrayList();
        for(int i=1;i<=total;i++)
        {
        	tLlist.add(i);
        	
        }
        session.put("search","false");
		session.put("search_title","");
		session.put("search_content","");
		session.put("search_pubName","");
		session.put("search_startDate","");
		session.put("search_endDate","");
		
        session.put("type", typeid);
		session.put("tLlist", tLlist);
		session.put("pageBean", pageBean);
		session.put("node",node);
		if("0".equals(typeid) && page == 0){
			return "main";
		}
		return "findsuccess";
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
	}
	
	
	//isNumeric
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();   
	 } 
	//输出新闻json接口
	public String newsCenterPage(){
		try {
			System.out.println("news==============================start");
			request.setCharacterEncoding("UTF-8");
			//news type
			String type = request.getParameter("type");
			//防注入
			if(!isNumeric(type)){
				return ERROR;
			}
			//list length
			String length = request.getParameter("length");
			//防注入
			if(length==""){
				length="10";
			}else{
				if(!isNumeric(length)){
					return ERROR;
				}
			}
			int len = Integer.valueOf(length).intValue();
			//userid
			Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			Long userid = userInfo.getId();
			//String username = userInfo.getUserName();
	
			
			String hql ="from News as n where orgid="+ userInfo.getCompany().getId(); 
			hql +=" and n.newType.tid="+type;
			hql += " and n.isPublic=1 and n.status=1 ";
			//hql +="limit "+len;
			hql +=" order by n.new_date desc";
			System.out.println("news=============================="+hql);
			news = newsService.find(hql,len);
			
			News n = new News();
			Map<String,Object> result=new HashMap<String, Object>();
			Map<String, Object>[] map = new HashMap[news.size()]; 
			System.out.println("size==================="+news.size());
			if (len>news.size())//孙爱华增加，否则会报错
			{
				len=news.size();
			}
			System.out.println("len==================="+len);
			for(int i = 0; i < len; i++)
			{
				n = news.get(i);
				
				//判断是否已经被该该读过来决定在公告中心上是否显示"new"标记
				String uId = Long.toString(userInfo.getId());
				String readIdString=newsInfo.getUserReadId();
				/**isRead默认该读者未读过该公告*/
				boolean isRead = false;
				if(readIdString != null)
				{
					if(readIdString.indexOf(";"+uId +";") >= 0)
					{
						isRead = true;
					}
				}
				
				map[i] = new HashMap<String, Object>();
				map[i].put("newId", n.getNewId());
				map[i].put("new_title", n.getNew_title());
				map[i].put("new_date", n.getDate());
				map[i].put("username", n.getUsername());
				map[i].put("isread", isRead);
			}
			result.put("news",map);
			
			/*JSON
			 {
			 errorCode:"0",
			 errorMessage:"",
			 result:[{
			    公告标题：new_title:
			    公告的ID：newId:
			   发布者：uesrId:"";
			   是否已读：isRead:"";
			   公告建立日期：new_date:"";
			 }]
			 }
			*/
			/*
			int allRow=news.size(); 
			this.pageBean = newsService.queryForPage(hql ,10,page,allRow);
			int total=this.pageBean.getTotalPage();
			
	        List tLlist = new ArrayList();
	        for(int i=1;i<=total;i++)
	        {
	        	tLlist.add(i);
	        	
	        }
	        session.put("search","false");
			session.put("search_title","");
			session.put("search_content","");
			session.put("search_pubName","");
			session.put("search_startDate","");
			session.put("search_endDate","");
			
	        session.put("type", typeid);
			session.put("tLlist", tLlist);
			session.put("pageBean", pageBean);
			session.put("node",node);*/
	//		if("0".equals(typeid) && page == 0){
	//			return "main";
	//		}
			System.out.println("news==============================end==============="+len);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JSONTools.convertToJson(ErrorCons.NO_ERROR,result));
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return ERROR;
		}
		
	}

	public String searchNews(){
		try {
			request.setCharacterEncoding("UTF-8");
			System.out.println("1-----title " + request.getParameter("title"));
			System.out.println("   ---content "
					+ request.getParameter("content"));
			System.out.println("   ---pubName "
					+ request.getParameter("pubName"));
			System.out.println("   ---startDate "
					+ request.getParameter("startDate"));
			System.out.println("   ---endDate "
					+ request.getParameter("endDate"));
			String title = request.getParameter("title");
			String content =  request.getParameter("content");
			String pubName = request.getParameter("pubName");
			String startDate = WebTools.converStr(
					request.getParameter("startDate"), "UTF-8");
			String endDate = WebTools.converStr(
					request.getParameter("endDate"), "UTF-8");
			String pageFlag = WebTools.converStr(request.getParameter("page"),
					"UTF-8");
			System.out.println("2-----title " + title);
			System.out.println("   ---content " + content);
			System.out.println("   ---pubName " + pubName);
			System.out.println("   ---startDate " + startDate);
			System.out.println("   ---endDate " + endDate);
			if (pageFlag == null || "".equals(pageFlag)) {
				session.put("search", "false");
			}

			String searchFlag = (String) session.get("search");
			if ("true".equals(searchFlag)) {
				title = (String) session.get("search_title");
				content = (String) session.get("search_content");
				pubName = (String) session.get("search_pubName");
				startDate = (String) session.get("search_startDate");
				endDate = (String) session.get("search_endDate");
			} else {
				session.put("search", "true");
				session.put("search_title", title);
				session.put("search_content", content);
				session.put("search_pubName", pubName);
				session.put("search_startDate", startDate);
				session.put("search_endDate", endDate);
			}

			String node = (String) session.get("node");
			Long userid = (Long) session.get("userID");
			String typeid = (String) session.get("type");
			Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			String hql = "from News where 1=1 and orgid="+ userInfo.getCompany().getId()+" ";

			if (typeid != null && !"0".equals(typeid)) {
				hql += "and newType='" + typeid + "' ";
			} else if (session.get("type") != null && !"0".equals(typeid)
					&& typeid == null && !"0".equals(session.get("type"))) {
				hql += "and newType='" + session.get("type") + "' ";
				typeid = (String) session.get("type");
			}

			if (node.equals("2")) {
				hql += "and userid='" + userid + "' ";
			} else if (node.equals("3")) {
				hql += "and status=0 ";
			} else if (node.equals("4")) {
				hql += "and status=1 ";
			} else {
				hql += "and isPublic=1 and status=1 ";
			}

			if ((title != null) && (!title.trim().equals(""))) {
				hql += "and new_title like '%" + title.trim() + "%' ";
			}
			if ((content != null) && (!content.trim().equals(""))) {
				hql += "and new_content like '%" + content.trim() + "%' ";
			}
			if ((pubName != null) && (!pubName.trim().equals(""))) {
				hql += "and publisher like '%" + pubName.trim() + "%' ";
			}

			if ((startDate != null) && (!startDate.trim().equals(""))) {
				if ((endDate != null) && (!endDate.trim().equals(""))) {
					hql += "and new_date between '" + startDate.trim()
							+ "' and '" + endDate + "' ";
				} else {
					hql += "and new_date >='" + startDate.trim() + "' ";
				}
			} else {
				if ((endDate != null) && (!endDate.trim().equals(""))) {
					hql += "and new_date <='" + endDate.trim() + "' ";
				}
			}

			hql += "order by new_date desc";
			news = newsService.find(hql);
			int allRow = news.size();
			this.pageBean = newsService.queryForPage(hql, 12, page, allRow);
			int total = this.pageBean.getTotalPage();

			List tLlist = new ArrayList();
			for (int i = 1; i <= total; i++) {
				tLlist.add(i);
			}
			session.put("type", typeid);
			session.put("tLlist", tLlist);
			session.put("pageBean", pageBean);
			session.put("node", node);
			return "findsuccess";
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
	}
	
	public String save() throws IOException{
		try {
			if((newsInfo.getNew_title() == null) || newsInfo.getPublisher() == null || newsInfo.getNew_content() == null 
					|| "".equals(newsInfo.getNew_title().trim()) || "".equals(newsInfo.getNew_content().trim()) || "".equals(newsInfo.getPublisher().trim())){
				request.setAttribute("necessary","标题、发布人、日期和正文为必填项，不能为空！");
				return "addNews"; 
			}
			Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			newsInfo.setNew_date(new Date());
			newsInfo.setUserid(userInfo.getId());
			newsInfo.setUsername(userInfo.getUserName());
			newsInfo.setOrgid(userInfo.getCompany().getId());
     		newsInfo.setNewType(newsType);
		    newsTypes=newsService.findType("from NewsType");
     		 if(uploadFileName !=null){
     			String fileType = uploadFileName[0].substring(uploadFileName[0].lastIndexOf( "."),uploadFileName[0].length());
         		if(!fileType.equalsIgnoreCase(".eio")&&!fileType.equalsIgnoreCase(".doc")&&!fileType.equalsIgnoreCase(".docx")&&!fileType.equalsIgnoreCase(".xlt")&&
         		   !fileType.equalsIgnoreCase(".ppt")&&!fileType.equalsIgnoreCase(".xls")&&!fileType.equalsIgnoreCase(".pot")&&!fileType.equalsIgnoreCase(".pdf")){
         			request.setAttribute("msg", "文件格式为doc,docx,xlt,ppt,xls,pot,pdf");
         			return "addNews"; 
         		}
     			String rootPath =request.getSession().getServletContext().getRealPath("");  
     			File file=new File(rootPath+"/data/notice/attached/");
     			if (!file.exists())
     			{
     				file.mkdirs();
     			}
				String test[];
				test=file.list();
//				for(int i=0;i<test.length;i++)
//				{
//				  if(test[i].equals(uploadFileName[0])){
//					  request.setAttribute("msg", "上传文件名已存在");
//					  return "addNews";
//				  }
//				} 
            	 File[] files=this.getUpload();
                 for(int i=0;i<files.length;i++){
                	 
                     FileOutputStream fos = new FileOutputStream( rootPath+"/data/notice/attached/"+getUploadFileName()[i]);
                     FileInputStream fis = new FileInputStream(files[i]);
                     byte[] buffer = new byte[1024];
                     int len = 0;
                     while ((len = fis.read(buffer)) > 0)
                     {
                         fos.write(buffer , 0 , len);
                     }
                     fos.flush();
                     fos.close();
                     fis.close();
                 }
                 newsAttached.setAttached(uploadFileName[0]);
                 newsAttachedService.save(newsAttached);
 				 newsInfo.setAttached(newsAttached);
        
			}
     		newsInfo.setFlag(1);
     		newsService.save(newsInfo);
     		session.put("type", newsInfo.getNewType().getTid());
     		session.put("newsInfo", newsInfo);
     	
     		String typeid =newsInfo.getNewType().getTid().toString();
     		this.find(typeid);
     		return "oksuccess";
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		
	}


	public String review() {
		try {
			session.put("admin", null);
			String nid = request.getParameter("nid");
			news = newsService.find("from News where newId ='"+nid+"'");
			if(news.size()>0){
				newsInfo = news.get(0);
				newsInfo.setCount(newsInfo.getCount()+1);
				
				Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
				String uId = Long.toString(userInfo.getId());
				String readIdString=newsInfo.getUserReadId();
				if(readIdString == null)
				{
					newsInfo.setUserReadId(uId);
				}
				else if(readIdString.indexOf(";"+uId +";") < 0)
				{
					newsInfo.setUserReadId(uId);
				}
				
				newsService.update(newsInfo);
			}
			request.getSession().setAttribute("content", newsInfo.getNew_content());
			session.put("newsInfo", newsInfo);
			return "newsCenter";
		} catch (Exception e) {
			return ERROR;
		}
		
	}
	
	public String view() {
		try {
			String nid = request.getParameter("nid");
			news = newsService.find("from News where newId ='"+nid+"'");
			if(news.size()>0){
				newsInfo = news.get(0);
				newsInfo.setCount(newsInfo.getCount()+1);
				
				Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
				String uId = Long.toString(userInfo.getId());
				String readIdString=newsInfo.getUserReadId();
				if(readIdString == null)
				{
					newsInfo.setUserReadId(uId);
				}
				else if(readIdString.indexOf(";"+uId +";") < 0)
				{
					newsInfo.setUserReadId(uId);
				}
				
				newsService.update(newsInfo);
			}
			request.getSession().setAttribute("content", newsInfo.getNew_content());
			session.put("newsInfo", newsInfo);
			return "newsCenter";
		} catch (Exception e) {
			return ERROR;
		}
		
	}
	
	public String delete()  {
		try {
			String did = request.getParameter("did");
			news = newsService.find("from News where newId ='"+did+"'");
			if(news.get(0).getAttached() !=null){
				Long aid = news.get(0).getAttached().getTid();
				newsService.delete(Long.parseLong(did));
				newsAttachedService.delete(aid);
			}else{
				newsService.delete(Long.parseLong(did));
			}
			
			
			String typeid =news.get(0).getNewType().getTid().toString();
	 		this.find(typeid);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return findNews();
	}
	
	public String deletes()  {
		try {
			String dids = request.getParameter("dids");
			String[] did = dids.split(",");
			for(int i=0;i<did.length;i++){
				news = newsService.find("from News where newId ='"+did[i]+"'");
				if(news.get(0).getAttached() !=null){
					Long aid = news.get(0).getAttached().getTid();
					newsService.delete(Long.parseLong(did[i]));
					newsAttachedService.delete(aid);
				}else{
					newsService.delete(Long.parseLong(did[i]));
				}
				String typeid =news.get(0).getNewType().getTid().toString();
				this.find(typeid);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return findNews();
	}
	
	public String setPublic()  {
		try {
			String ids = request.getParameter("ids");
			String flag = request.getParameter("flag");
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++){
				news = newsService.find("from News where newId ='"+id[i]+"'");
				newsInfo = news.get(0);
				if(flag.equals("true")){
					newsInfo.setIsPublic(1);
				}else {
					newsInfo.setIsPublic(0);
				}
				newsService.update(newsInfo);
				String typeid =news.get(0).getNewType().getTid().toString();
				this.find(typeid);
			}
			session.put("message", "更新成功");			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return findNews();
	}
	
	public String setStatus()  {
		try {
			String ids = request.getParameter("ids");
			String status = request.getParameter("status");
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++){
				news = newsService.find("from News where newId ='"+id[i]+"'");
				newsInfo = news.get(0);
				if(status.equals("1")){
					newsInfo.setStatus(1);
					newsInfo.setIsPublic(1);
				}else if(status.equals("2")){
					newsInfo.setStatus(2);
					newsInfo.setIsPublic(0);
				}else{
					newsInfo.setStatus(0);
					newsInfo.setIsPublic(0);
				}
				newsService.update(newsInfo);
				String typeid =news.get(0).getNewType().getTid().toString();
				this.find(typeid);
			}
			session.put("message", "更新成功");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return findNews();
	}
	
	public void upload() {
	try {
	    if(uploadFileName !=null){
        	 File[] files=this.getUpload();
             for(int i=0;i<files.length;i++){
            	 String rootPath =request.getSession().getServletContext().getRealPath("");  
                 FileOutputStream fos = new FileOutputStream( rootPath+"/images/"+getUploadFileName()[i]);
                 FileInputStream fis = new FileInputStream(files[i]);
                 byte[] buffer = new byte[1024];
                 int len = 0;
                 while ((len = fis.read(buffer)) > 0)
                 {
                     fos.write(buffer , 0 , len);
                 }
             }
     		String jpgType = uploadFileName[0].substring(uploadFileName[0].lastIndexOf( "."),uploadFileName[0].length());
     		if(!jpgType.equalsIgnoreCase(".jpg")&&!jpgType.equalsIgnoreCase(".gif")&&!jpgType.equalsIgnoreCase(".bmp")&&!jpgType.equalsIgnoreCase(".jpeg")){
     			request.setAttribute("img", "上传头像文件扩展名仅为:   .jpg   .jpeg   .bmp   .gif! ");
     		}
     		pagePath = "images/"+uploadFileName[0];
     		
        }
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	public String edit(){
		String eid = request.getParameter("eid");
		newsInfo=newsService.get(Long.parseLong(eid));
		Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		newsInfo.setUserid(userInfo.getId());
		newsInfo.setUsername(userInfo.getUserName());
		newsInfo.setOrgid(userInfo.getCompany().getId());
		request.setAttribute("nContent", newsInfo.getNew_content());
		return "updateNews";
		
	}
	
	public String update(){
		try {
			if((newsInfo.getNew_title() == null) || newsInfo.getPublisher() == null || newsInfo.getNew_content() == null || 
					"".equals(newsInfo.getNew_title().trim()) || "".equals(newsInfo.getNew_content().trim()) || "".equals(newsInfo.getPublisher().trim())){
				request.setAttribute("necessary","标题、发布人、日期和正文为必填项，不能为空！");
				request.setAttribute("nContent", newsInfo.getNew_content());
				System.out.println("newsInfo.getNew_title()="+newsInfo.getNew_title());
				System.out.println("newsInfo.getNew_date()="+newsInfo.getNew_date());
				System.out.println("newsInfo.getpublisher()="+newsInfo.getPublisher());
				System.out.println("newsInfo.getNew_content()="+newsInfo.getNew_content());
				return "updateNews";
			}
			
			if(uploadFileName ==null){
				newsInfo.setAttached(newsService.get(newsInfo.getNewId()).getAttached());
			}
			if(uploadFileName !=null){
	     			String rootPath =request.getSession().getServletContext().getRealPath("");  
	     			File file=new File(rootPath+"/data/notice/attached/");
					String test[];
					test=file.list();
					for(int i=0;i<test.length;i++)
					{
					  if(test[i].equals(uploadFileName[0])){
						  request.setAttribute("message", "上传文件名已存在");
						  return "input";
					  }
					} 
	            	 File[] files=this.getUpload();
	                 for(int i=0;i<files.length;i++){
	                	 
	                     FileOutputStream fos = new FileOutputStream( rootPath+"/data/notice/attached/"+getUploadFileName()[i]);
	                     FileInputStream fis = new FileInputStream(files[i]);
	                     byte[] buffer = new byte[1024];
	                     int len = 0;
	                     while ((len = fis.read(buffer)) > 0)
	                     {
	                         fos.write(buffer , 0 , len);
	                     }
	                 }
	                 newsAttached.setAttached(uploadFileName[0]);
	                 if(newsInfo.getAttached() !=null){
	 					newsAttached.setTid(newsInfo.getAttached().getTid());
	 					newsAttachedService.update(newsAttached);
	 				}else{
	 					newsAttachedService.save(newsAttached);
	 				}
	 				 newsInfo.setAttached(newsAttached);
	        
				}
			Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			newsInfo.setUserid(userInfo.getId());
			newsInfo.setOrgid(userInfo.getCompany().getId());
			newsInfo.setUsername(userInfo.getUserName());
     		newsInfo.setNewType(newsType);
     		newsInfo.setFlag(1);
     		session.put("type", newsInfo.getNewType().getTid());
     		session.put("newsInfo", newsInfo);
     		
     		newsService.update(newsInfo);
     		String typeid =newsInfo.getNewType().getTid().toString();
     		this.find(typeid);
     		session.put("message", "更新成功");
			return "oksuccess";
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
	}
	
	/**
	 * 
	 * @param 生成rss
	 * @return  
	 */
	public void find(String typeid) throws FileNotFoundException, UnsupportedEncodingException{
		 /**生成rss.xml*/
		String newsType=null;
		String xml_end ="</channel>"+"\n";
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><rss version=\"2.0\">"+"\n";
//		ip=ipAddress(ip);
		String rssType=null;
		if("1".equals(typeid)){
			rssType="/templates/ggzx.xml";
			newsType="公告中心";
		}
		
		String xml_start = "<channel><title>"+newsType+"</title><language>zh-cn</language>"+"\n";
		String hql ="from News where newType='"+typeid+"' and isPublic=1 and status=1 order by new_date desc";
		news = newsService.find(hql);
		String rssxml=null;
		int a=10;
		if(news.size()<10){
			a=news.size();
		}
		for(int i=0;i<a;i++){
			xml_start+="<item>"+"\n"+"<title><![CDATA[" +news.get(i).getNew_title()+"]]></title>"+"\n"+
			"<link>/templates/notice_review.action?nid="+news.get(i).getNewId()
			+"</link>"+"\n"+"<description><![CDATA["+news.get(i).getNew_content()+"]]></description>"+"\n"+
			"<flag><![CDATA[" +news.get(i).getFlag()+"]]></flag>"+"\n"+
			"<date><![CDATA[" +news.get(i).getDate()+"]]></date>"+"\n"+
			"<author><![CDATA[" +news.get(i).getPublisher()+"]]></author>"+"</item>"+"\n";
		}
		rssxml=xml+xml_start+xml_end+"</rss>";
		String rootPath =request.getSession().getServletContext().getRealPath("");  
		File f=new File(rootPath+rssType);
		PrintWriter pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssxml);
		pw.close();

	}
	
	/**
	 * 
	 * @param 移动公告借口
	 * @return  jsonResult
	 */
	public String getNewsList(){

		news = newsService.find("from News where newType=1 and isPublic=1 and status=1 order by new_date desc");
		 JSONArray ja = new JSONArray();  
		String link=null;
//		ip=ipAddress(ip);
		JsonConfig config = new JsonConfig();//过滤不需要的字段
        config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        config.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (name.equals("newType") || name.equals("attached") ||  name.equals("newId")
                	|| name.equals("new_date") ||name.equals("new_content") || name.equals("flag")
                	|| name.equals("userid") || name.equals("news_images")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        JSONObject jsonObject = null;
		for(int i =0; i<news.size();i++){
			newsInfo = news.get(i);
			link= "/templates/notice_review.action?nid="+news.get(i).getNewId();
			jsonObject = JSONObject.fromObject(newsInfo,config);  
			jsonObject.element("link", link);
			ja.add(jsonObject);
		}

		
        jsonResult=ja.toString();
		return SUCCESS;
	}
	
	public Long getTopOrganizations(){
		Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		Long userId = userInfo.getId();
		IAddressListService addressListService = (IAddressListService)apps.transmanager.weboffice.service.context.ApplicationContext.getInstance().getBean("addressListService");
		Long orgId = addressListService.getRootGroupByUserId(userId);
		return orgId;
	}
	
	/**
	 * 
	 * @param 获取ip.xml中域名
	 * @return
	 *//*
	public String ipAddress(String address){
		try {
			DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
	        DocumentBuilder dombuilder=domfac.newDocumentBuilder();
	        String rootPath=request.getSession().getServletContext().getRealPath("");  
	        InputStream is=new FileInputStream(rootPath+"/ip.xml");  //xml 的路径          
	        Document doc=dombuilder.parse(is);
	        Element root=doc.getDocumentElement();
	        NodeList books=root.getChildNodes();
	       
	        if(books!=null) {
	            for(int i=0;i<books.getLength();i++) {
	                Node book=books.item(i);
	                    for(Node node=book.getFirstChild();node!=null;node=node.getNextSibling()) {
	                        if(node.getNodeType()==Node.ELEMENT_NODE) {
	                            if(node.getNodeName().equals("value")) {
	                            	address=node.getFirstChild().getNodeValue();
	                            }
	                    }
	                }
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return address;
	}*/
	
	public News getNewsInfo() {
		return newsInfo;
	}
	public void setNewsInfo(News newsInfo) {
		this.newsInfo = newsInfo;
	}
	public List<News> getNews() {
		return news;
	}
	public void setNews(List<News> news) {
		this.news = news;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	public File[] getUpload() {
		return upload;
	}

	public void setUpload(File[] upload) {
		this.upload = upload;
	}

	public String[] getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String[] uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String[] getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String[] uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public NewsType getNewsType() {
		return newsType;
	}

	public void setNewsType(NewsType newsType) {
		this.newsType = newsType;
	}

	public List<NewsType> getNewsTypes() {
		return newsTypes;
	}

	public void setNewsTypes(List<NewsType> newsTypes) {
		this.newsTypes = newsTypes;
	}

	public PageBean getPageBean() {
		return pageBean;
	}

	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	public String getJsonResult() {
		return jsonResult;
	}

	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
	}

	public String getRssResult() {
		return rssResult;
	}

	public void setRssResult(String rssResult) {
		this.rssResult = rssResult;
	}


	public NewsAttached getNewsAttached() {
		return newsAttached;
	}


	public void setNewsAttached(NewsAttached newsAttached) {
		this.newsAttached = newsAttached;
	}


	public List<NewsAttached> getNewsAttacheds() {
		return newsAttacheds;
	}


	public void setNewsAttacheds(List<NewsAttached> newsAttacheds) {
		this.newsAttacheds = newsAttacheds;
	}
	@Override
	public void addActionError(String anErrorMessage) {
		if(anErrorMessage.contains("the request was rejected because its size")){
			request.setAttribute("msg","上传的文件大小超过限制：10M，请重新上传！");
		}else {
			super.addActionError(anErrorMessage);
		}
	}
	
	public String getIP(){
		//String ip=request.getLocalAddr()+":"+request.getLocalPort();
		//ip=ip.replaceAll(QueryDb.urlip, QueryDb.urlname);
		String ip = QueryDb.urlname + ":" + request.getLocalPort();
		return ip;
	}
}
