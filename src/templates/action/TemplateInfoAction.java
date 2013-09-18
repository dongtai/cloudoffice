package templates.action;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import templates.objectdb.Collection;
import templates.objectdb.CollectionBean;
import templates.objectdb.Template;
import templates.objectdb.TemplateType;
import templates.service.CollectionService;
import templates.service.PageBean;
import templates.service.TemplateService;
import apps.moreoffice.ext.share.QueryDb;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dcs.core.ConvertDecorate;

public class TemplateInfoAction extends ActionSupport{
  
	
	private CollectionBean queryBean = new CollectionBean();
	private Collection collection = new Collection();
	private List<Collection> collections = new ArrayList<Collection>();
	private Template template = new Template();
	private List<Template> templates= new ArrayList<Template>();
	private TemplateType templateType;
	private List<TemplateType> templateTypes;
	private List pgList = new ArrayList();
	private List wpList = new ArrayList();
	private List ssList = new ArrayList();
	private List scList = new ArrayList();
	private List <Template>dateTemplates = new ArrayList<Template>();
	private HttpServletRequest request = ServletActionContext.getRequest();
	private HttpServletResponse response = ServletActionContext.getResponse();
	private Map<String, Object> session = ActionContext.getContext().getSession();
    private File[] upload;
    private String[] uploadContentType;
    private String[] uploadFileName;
    private String savePath=request.getRealPath("upload");
	private ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()); 

	
	private CollectionService collectionService = (CollectionService) ctx.getBean("collectionService");
	private TemplateService templateService = (TemplateService) ctx.getBean("templateService");
	static Logger log = Logger.getLogger( TemplateInfoAction.class.getName () ) ;
	private int page;    //第几页
	private PageBean pageBean; 
	private PageBean wpPageBean;
	private PageBean pgPageBean;
	private PageBean ssPageBean;
	List<TemplateType> industrys;
    private String ip;

	public String priview(){
		try {
		String tUrl = request.getParameter("tname");
		String rootPath =request.getSession().getServletContext().getRealPath("");  
		ConvertDecorate cmp = new ConvertDecorate();
		//String tmpURL=rootPath.substring(0, rootPath.length()-"/templates".length())+"/tdownload/"+tUrl;
		String tmpURL=rootPath + File.separator + "templates" + File.separator + "tdownload" + File.separator + tUrl;
		String picURL=rootPath + File.separator + "templates" + File.separator + "highslide4" + File.separator + "images";
		int end = -1;
		float zoom =1.0f;
		String fileType = "gif";
		System.out.println("============tmpURL="+tmpURL);
		System.out.println("============picURL="+picURL);
		int converPic = cmp.convertMStoPic(tmpURL, picURL, end, fileType, zoom).length;
		ip=getIP();
		session.put("host", String.valueOf(ip));
		session.put("content", String.valueOf(converPic));
		String returnPage = request.getParameter("my");
		if("mb".equals(returnPage)){
			return "mb";
		}
		if("tp".equals(returnPage)){
			return "tp";
		}
		if("up".equals(returnPage)){
			return "up";
		}
		if("ft".equals(returnPage)){
			return "ft";
		}
		if("ct".equals(returnPage)){
			return "ct";
		}
		if("ca".equals(returnPage)){
			return "ca";
		}
		response.setHeader("Pragma","No-cache"); 
		response.setHeader("Cache-Control","no-cache"); 
		response.setDateHeader("Expires", 0); 
		return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SUCCESS;
		}
	}
	
	public void download() throws IOException{
		try {
			int count=0;
			String tUrl = request.getParameter("tname");
			templates=templateService.findTemplate("from Template where tUrl='"+tUrl+"'");
			if(templates.size()>0){
				template=templates.get(0);
				count=template.getThitCount();
				count++;
				template.setThitCount(count);
				templateService.updateTemplate(template);
			}
			
			response.setContentType("text/xml;charset=gbk");
			response.setHeader("Cache-Control", "no-cache");
//			ipAddress(ip);
			ip = getIP();
			String tURL= ip + "/templates/tdownload/"+tUrl;
			String xml_start = "<selects>";
			String xml_end ="</selects>";
			String xml="<?xml version=\"1.0\" encoding=\"gbk\"?>";
			xml+="<select><thitcount>"+count+"</thitcount><turl>"+tURL+"</turl>" +
					"<tid>"+template.getTid()+"</tid><tname>"+tUrl+"</tname></select>";
			response.getWriter().write(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String findTemp(){
		/*
		 * 三大类查找
		 * <20001PG模板
		 * 20001-30000之间SS模板
		 * 》30001WP模板
		 * 
		 * */
		try{
			
			String type=request.getParameter("type");
			
			if(Integer.parseInt(type)>30000 && Integer.parseInt(type)<40001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid between 30000 and 40000  order by ttid asc ");
				dateTemplates=templateService.findTemplate("from Template where  tType between 30000 and 40000  order by date desc");
				session.put("type", "40000");
				session.put("ptName", "文字处理");
			}
			if(Integer.parseInt(type)>20000 && Integer.parseInt(type)<30001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid  between 20000 and 30000 order by ttid asc ");
				dateTemplates=templateService.findTemplate("from Template where  tType between 20000 and 30000 order by date desc");
				session.put("type", "30000");
				session.put("ptName", "电子表格");
			}
			if(Integer.parseInt(type)<20001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid < 20001 order by ttid asc ");
				dateTemplates=templateService.findTemplate("from Template where  tType < 20001 order by date desc");
				session.put("type", "20000");
				session.put("ptName", "简报制作");
			}
			templates=templateService.findTemplate("from Template where tType ='"+templateTypes.get(0).getTtid()+"' order by thitCount desc");
			int t=0,dt=0;;
			if(templates.size()>9){
				t =10;
			}else{
				t=templates.size();
			}
			if(dateTemplates.size()>23){
				dt=24;
			}else{
				dt= dateTemplates.size();
			}
			for(int i=0;i<templateTypes.size();i++){
					session.put("typeImg", templateTypes.get(i).getHyImages());
			}
			session.put("templates", templates.subList(0, t));
			session.put("dateTemplates", templates.subList(0, dt));
			session.put("templateTypes", templateTypes);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
	}
	/**
	 * 按类型查找
	 * @return
	 * <20001PG模板
	 * 20001-30000之间SS模板
	 * 30001-40001WP模板
	 */
	public String findType(){
		try{
			String userID = request.getParameter("userID");
			String userName = request.getParameter("userName");
			String email = request.getParameter("email");
			String path = request.getParameter("path");
			session.put("userID", userID);
			session.put("userName", userName);
			session.put("email", email);
			session.put("path", path);
			String type=request.getParameter("type");
			if(type == null){
				type= (String) request.getAttribute("tmpid");
			}
			dateTemplates=templateService.findTemplate("from Template where  tType ='"+type+"'  order by date desc");
			if(Integer.parseInt(type)>30000 && Integer.parseInt(type)<40001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid between 30000 and 40000  order by ttid asc ");
				session.put("type", "40000");
			}
			if(Integer.parseInt(type)>20000 && Integer.parseInt(type)<30001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid  between 20000 and 30000 order by ttid asc ");
				session.put("type", "30000");
			}
			if(Integer.parseInt(type)<20001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid < 20001 order by ttid asc ");
				session.put("type", "20000");
			}
			templates = templateService.findTemplate("from Template where tType ='"+type+"' order by thitCount desc");
			int t=0,dt=0;;
			if(templates.size()>10){
				t =10;
			}else{
				t=templates.size();
			}
			if(dateTemplates.size()>24){
				dt=24;
			}else{
				dt= dateTemplates.size();
			}
			for(int i=0;i<templateTypes.size();i++){
				if(templateTypes.get(i).getTtid()==Integer.parseInt(type)){
					session.put("typeImg", templateTypes.get(i).getHyImages());
					session.put("ptName", templateTypes.get(i).getPtName());
				}
			}
			session.put("templates", templates.subList(0, t));
			session.put("dateTemplates", dateTemplates.subList(0, dt));
			session.put("templateTypes", templateTypes);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
	}
	
	/**
	 * 查找全部模板
	 * @return
	 */
	public String findAll(){
		try{
			/**
			 * wp模板
			 * */
			String type=request.getParameter("type");
			if(type == null){  
				type= (String) request.getAttribute("tmpid");
			}
			if(Integer.parseInt(type)>30000 && Integer.parseInt(type)<40001){
				String wphql="";
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid  >30000 order by ttid asc ");
				if(Integer.parseInt(type)== 40000){
					wphql ="from Template where tType between 30000 and 40000  order by date desc";
				}else{
					wphql ="from Template where tType ='"+type+"'   order by date desc";
				}
			
				wpList=templateService.findTemplate(wphql);
				List wpTemplates = templateService.findTemplate(wphql);
				int allRow=wpTemplates.size();
				this.pageBean = templateService.queryForPage(wphql ,24,page,allRow);
				int total=this.pageBean.getTotalPage();
				
		        List tLlist = new ArrayList();
		        for(int i=1;i<=total;i++)
		        {
		        	tLlist.add(i);
		        	
		        }
		        session.put("tLlist", tLlist);
		        session.put("pageBean", pageBean);
			}
			
			
			/**
			 * ss模板
			 * */
			if(Integer.parseInt(type)>20000 && Integer.parseInt(type)<30001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid  between 20000 and 30000 order by ttid asc ");
				String sshql="";
				if(Integer.parseInt(type)== 30000){
					sshql ="from Template where tType  between 20000 and 30000  order by date desc";
				}else{
					sshql ="from Template where tType ='"+type+"'   order by date desc";
				}
				ssList=templateService.findTemplate(sshql);
				List ssTemplates = templateService.findTemplate(sshql);
				int allRow=ssTemplates.size();
				this.pageBean = templateService.queryForPage(sshql ,24,page,allRow);
				int total=this.pageBean.getTotalPage();
				
		        List tLlist = new ArrayList();
		        for(int i=1;i<=total;i++)
		        {
		        	tLlist.add(i);
		        }
		        session.put("pageBean", pageBean);
		        session.put("tLlist", tLlist);
				
			}
			
			/**
			 * pg模板
			 * 
			 * */
			if(Integer.parseInt(type)<20001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid < 20001 order by ttid asc ");
				String pghql="";
				if(Integer.parseInt(type)== 20000){
					pghql ="from Template where  tType < 20001 order by date desc";
				}else{
					pghql ="from Template where tType ='"+type+"'   order by date desc";
				}
				pgList=templateService.findTemplate(pghql);
				List pgTemplates = templateService.findTemplate(pghql);
				int allRow=pgTemplates.size();
				this.pageBean = templateService.queryForPage(pghql ,28,page,allRow);
				int total=this.pageBean.getTotalPage();
				
		        List tLlist = new ArrayList();
		        for(int i=1;i<=total;i++)
		        {
		        	tLlist.add(i);
		        }
		        session.put("pageBean", pageBean);
		        session.put("tLlist", tLlist);
			}
			if(Integer.parseInt(type) == 40000 || Integer.parseInt(type) == 30000 || Integer.parseInt(type) == 20000){
				templates = templateService.findTemplate("from Template where tType ='"+templateTypes.get(0).getTtid()+"' order by thitCount desc");
			}else{
				templates = templateService.findTemplate("from Template where tType ='"+type+"' order by thitCount desc");
				
			}
			
			
			int a=0;
			if(templates.size()>10){
				a = 10;
			}else{
				a=templates.size();
			}
			for(int i=0;i<templateTypes.size();i++){
				if(templateTypes.get(i).getTtid()==Integer.parseInt(type)){
					session.put("typeImg", templateTypes.get(i).getHyImages());
					session.put("ptName", templateTypes.get(i).getPtName());
					
				}else if("20000".equals(type) ){
					session.put("typeImg", templateTypes.get(0).getHyImages());
					session.put("ptName", "简报制作");
				}else if("30000".equals(type) ){
					session.put("typeImg", templateTypes.get(0).getHyImages());
					session.put("ptName", "电子表格");
				}else if( "40000".equals(type)){
					session.put("typeImg", templateTypes.get(0).getHyImages());
					session.put("ptName", "文字处理");
				}
				
			}
			session.put("templateTypes", templateTypes);
			session.put("type", type);
			session.put("templates", templates.subList(0, a));
			return "tp";
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
	}
	
	
	/**
	 * 按查找全部行业类型
	 * @return
	 */
	public String findUpload(){
		try{
			String type=request.getParameter("type");
		
			if(Integer.parseInt(type)>30000 && Integer.parseInt(type)<40001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid between 30000 and 40000 order by ttid asc ");
				session.put("type", "40000");
			}
			if(Integer.parseInt(type)>20000 && Integer.parseInt(type)<30001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid  between 20000 and 30000 order by ttid asc ");
				session.put("type", "30000");
			}
			if(Integer.parseInt(type)<20001){
				templateTypes = templateService.findTemplateType("from TemplateType where  ttid < 20001 order by ttid asc ");
				session.put("type", "20000");
			}
			industrys= templateService.findTemplateType("from TemplateType order by ttid desc");
			session.put("industrys", industrys);
			session.put("templateTypes", templateTypes);
			session.put("ptName", "");
			return "up";
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
	}
	
	
	/**
	 * 点击率
	 * @return
	 */
	public void thitCount(){
		try{
			String tmpid =request.getParameter("tmpid");
			String moban=request.getParameter("moban");
			int count =0;
			List cList = templateService.findTemplate(" from Template  where tid ='"+tmpid+"'  ");
			if(cList.size()>0){
				template=(Template) cList.get(0);
				count=template.getThitCount();
			count++;
			template.setThitCount(count);
			templateService.updateTemplate(template);
			DownloadCount();
			
			//取得session里的用户信息
			String userID= (String)request.getSession().getAttribute("userID");
			String userName= (String)session.get("userName");
			String email= (String)request.getSession().getAttribute("email");
			String path = (String) session.get("path");
			
			String tUrl=template.getTUrl();
//			ip=ipAddress(ip);
			ip = getIP();
			//String ip = request.getLocalAddr()+":"+request.getLocalPort();
			String serviceURL = "http://"+ip+"/static/UploadService";
			String templateUrl="http://"+ip+"/static/open.jsp?commandId=public&appType=OnlineTemplates" +
					"&action=new&templateName=http://"+ip+"/templates/tdownload/"+tUrl+"&serviceURL="
			        +serviceURL+"&userID="+userID+"&userName="+userName+"&email="+email+"&path="+path;
			response.setContentType("text/xml;charset=gbk");
			response.setHeader("Cache-Control", "no-cache");
			String xml_start = "<selects>";
			String xml_end ="</selects>";
			String xml="<?xml version=\"1.0\" encoding=\"gbk\"?>";
			xml+="<select><ttype>"+template.getImgType().toUpperCase()+"</ttype><thitcount>"+count+"</thitcount><tid>"+template.getTid()+"</tid><turl>"+templateUrl.replace("&", "&amp;")+"</turl></select>";
			response.getWriter().write(xml);
			
			String a = "0";
			session.put("a", "1");
			session.put("tempid", tmpid);
			session.put("templateUrl", templateUrl);
			session.put("count", String.valueOf(count));
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 网络office新建模版到tab
	 * 
	 * */
	public void newTemplate(){
		try{
			String tmpid =request.getParameter("tmpid");
			String moban=request.getParameter("moban");
			int count =0;
			List cList = templateService.findTemplate(" from Template  where tid ='"+tmpid+"'  ");
			if(cList.size()>0){
				template=(Template) cList.get(0);
				count=template.getThitCount();
			count++;
			template.setThitCount(count);
			templateService.updateTemplate(template);
			DownloadCount();
			
			//取得session里的用户信息
			String userID= (String)request.getSession().getAttribute("userID");
			String userName= (String)session.get("userName");
			String email= (String)request.getSession().getAttribute("email");
			String path = (String) session.get("path");
			
			String tUrl=template.getTUrl();
			String serviceURL = "/static/UploadService";
			String templateUrl="OnlineTemplates" +
			"&templateName=/templates/tdownload/"+tUrl+"&serviceURL="
	        +serviceURL;
			response.setContentType("text/xml;charset=gbk");
			response.setHeader("Cache-Control", "no-cache");
			String xml_start = "<selects>";
			String xml_end ="</selects>";
			String xml="<?xml version=\"1.0\" encoding=\"gbk\"?>";
			xml+="<select><ttype>"+template.getImgType().toUpperCase()+"</ttype><thitcount>"+count+"</thitcount><tid>"+template.getTid()+"</tid><turl>"+templateUrl.replace("&", "&amp;")+"</turl></select>";
			response.getWriter().write(xml);
			
			String a = "0";
			session.put("a", "1");
			session.put("tempid", tmpid);
			session.put("templateUrl", templateUrl);
			session.put("count", String.valueOf(count));
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 排行榜
	 * @return
	 */
   public void DownloadCount(){
	   List templateList = templateService.findTemplate("from Template  order by thitCount desc ");
	   ActionContext.getContext().getApplication().put("templateList", templateList.subList(0, 12));
   }
	
   /**
	 * 按三大类查找全部
	 * @return
	*/
	public String addTemplate(){
		try {
		String tType = request.getParameter("tType");
		
		if(template.getTUrl()==null || "null".equals(template.getTUrl()) || template.getImgUrl().length()==0){
			session.put("message", "上传失败，请重新上传");
			return "up";
		}else{
			
			if(template.getTName().trim() == null || "".equals(template.getTName().trim())){
				session.put("message", "模板添加失败");
				return "up";
			}
			if(template.getImgUrl() ==null || "null".equals(template.getImgUrl())){
				template.setImgUrl("default.jpg");
			}
			template.setImgUrl("../"+template.getImgUrl());
			TemplateType tt= new TemplateType();
			tt.setTtid(Long.parseLong(tType));
			template.setTType(tt);
			Date data = new Date();
			template.setDate(data);
			template.setThitCount(0);
			template.setIsPay(0);
			template.setPrice(0);
			template.setCheckupResult(0);
			template.setTDownloadCount(0);
			template.setT_number("upload");
			templateService.saveTemplate(template);
			templates =templateService.findTemplate("from Template order by tid desc");
			int u=0;
			if(templates.size()>21){
				u =21;
			}else{
				u=templates.size();
			}
			session.put("tmptUrl",null);
			session.put("tmpImgUrl",null);
			session.put("message", "模板添加成功");
			ActionContext.getContext().getApplication().put("uTemplates", templates.subList(0, u));
		}
		return "up";
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	/**
	 * 收藏
	 * @return
	 * @throws Exception 
	*/
	public void collect() throws Exception{
		try {
			String userID= (String)session.get("userID");
			String tmpid = request.getParameter("tmpid");
			Template tl = new Template();
			collection.setCUser(userID);
			collection.setThitCount(1); 
			Date date = new Date();
			collection.setDate(date);
			tl.setTid(Long.parseLong(tmpid));
			collection.setTemplateid(tl);
			List clist=collectionService.find("from Collection where cUser='"+userID+"' and templateid ='"+tmpid+"'");
			if(clist.size()==0){
				if(userID !=null){
					collectionService.save(collection);
					collectionService.flush();
					collectionService.clear();
				}
				
			}
			queryBean.setCUser(userID);
			List<Collection> cs = collectionService.query(queryBean);
			int a =0;
			if(cs.size()>9){
				a=9;
			}else{
				a=cs.size();
			}
			session.put("collections", cs.subList(0, a));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 收藏全部
	 * @return
	*/
	public  String collectAll(){
		try {
			String userID = request.getParameter("userID");
			String userName = request.getParameter("userName");
			String email = request.getParameter("email");
			String path = request.getParameter("path");
			if(userID!=null){
				session.put("userID", userID);
			}else{
				userID=(String) session.get("userID");
			}
			if(userName!=null){
				session.put("userName", userName);
					}
			if(email!=null){
				session.put("email", email);
			}
			if(path!=null){
				session.put("path", path);
			}
			String hql="from Collection where cUser ='"+userID+"'";
			List cTmps = collectionService.find(hql);
			int allRow=cTmps.size();
			this.pageBean = collectionService.queryForPage(hql ,40,page,allRow);
			int total=this.pageBean.getTotalPage();
			
	        List tLlist = new ArrayList();
	        for(int i=1;i<=total;i++)
	        {
	        	tLlist.add(i);
	        	
	        }
	        session.put("tLlist", tLlist);
	        session.put("pageBean", pageBean);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		

		return "ca";
	}
	
	public String delCollection(){
		try {
		String[] cid = request.getParameter("cid").split(",");
		for(int i =0;i<cid.length;i++){
			collectionService.delete(Long.parseLong(cid[i]));
		}
		
		String userID=userID=(String) session.get("userID");
		String hql="from Collection where cUser ='"+userID+"'";
		List cTmps = collectionService.find(hql);
		int allRow=cTmps.size();
		this.pageBean = collectionService.queryForPage(hql ,40,page,allRow);
		int total=this.pageBean.getTotalPage();
		
        List tLlist = new ArrayList();
        for(int i=1;i<=total;i++)
        {
        	tLlist.add(i);
        	
        }
        session.put("tLlist", tLlist);
        session.put("pageBean", pageBean);
        queryBean.setCUser(userID);
		List<Collection> cs = collectionService.query(queryBean);
		int a =0;
		if(cs.size()>9){
			a=9;
		}else{
			a=cs.size();
		}
		ActionContext.getContext().getApplication().put("collections", cs.subList(0, a));
		
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return "ca";
	}
	
	public String collectionTmp(){
		try {
			String userID = request.getParameter("userID");
			String userName = request.getParameter("userName");
			String email = request.getParameter("email");
			String path = request.getParameter("path");
			session.put("userID", userID);
			session.put("userName", userName);
			session.put("email", email);
			session.put("path", path);
			int e=0;
			collections = collectionService.find("from Collection where cUser ='"+userID+"'   order by date desc");
			if(collections.size()>9){
				e=9;
			}else{
				e=collections.size();
			}
			session.put("collections", collections.subList(0, e));
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return "ct";
		
	}
	
	public String getIP(){
		//String ip=request.getLocalAddr()+":"+request.getLocalPort();
		//ip=ip.replaceAll(QueryDb.urlip, QueryDb.urlname);
		String ip = QueryDb.urlname + ":" + request.getLocalPort();
		return ip;
	}
	/*public String ipAddress(String address){
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
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public List<Template> getTemplates() {
		return templates;
	}
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
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


	public TemplateType getTemplateType() {
		return templateType;
	}


	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}


	public List<TemplateType> getTemplateTypes() {
		return templateTypes;
	}


	public void setTemplateTypes(List<TemplateType> templateTypes) {
		this.templateTypes = templateTypes;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public PageBean getPageBean() {
		return pageBean;
	}
	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}
	public PageBean getWpPageBean() {
		return wpPageBean;
	}
	public void setWpPageBean(PageBean wpPageBean) {
		this.wpPageBean = wpPageBean;
	}
	public PageBean getPgPageBean() {
		return pgPageBean;
	}
	public void setPgPageBean(PageBean pgPageBean) {
		this.pgPageBean = pgPageBean;
	}
	public PageBean getSsPageBean() {
		return ssPageBean;
	}
	public void setSsPageBean(PageBean ssPageBean) {
		this.ssPageBean = ssPageBean;
	}
	public List<TemplateType> getIndustrys() {
		return industrys;
	}
	public void setIndustrys(List<TemplateType> industrys) {
		this.industrys = industrys;
	}
	public List<Collection> getCollections() {
		return collections;
	}
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
	public Collection getCollection() {
		return collection;
	}
	public void setCollection(Collection collection) {
		this.collection = collection;
	}
	public CollectionBean getQueryBean() {
		return queryBean;
	}
	public void setQueryBean(CollectionBean queryBean) {
		this.queryBean = queryBean;
	}
}
