package templates.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import templates.objectdb.Collection;
import templates.objectdb.Template;
import templates.service.CollectionService;
import templates.service.TemplateService;
import apps.transmanager.weboffice.service.context.ApplicationContext;


/**
 * Servlet implementation class LoginServlet
 */
public class DataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<Collection> collections = new ArrayList<Collection>();
	private List<Template> templates= new ArrayList<Template>();;
	private List dateTemplates = new ArrayList();
	private List okTemplates = new ArrayList();
	private List downloadTemplates = new ArrayList();
	private List scList = new ArrayList();
//	private ApplicationContext ctx =null; 
	private ServletContext  servletContext = null;
	
	static Logger log = Logger.getLogger( DataServlet.class.getName () ) ;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	 public void init() throws ServletException
	    {
		 	servletContext = getServletContext();
	/*		String[] config = new String[] {"spring/applicationContext.xml"};   
			 ApplicationContext ctx = new ClassPathXmlApplicationContext(config);   
		     TemplateService templateService = (TemplateService) ctx.getBean("templateService");
			 CollectionService collectionService = (CollectionService) ctx.getBean("collectionService");*/
		 	TemplateService templateService = (TemplateService) ApplicationContext.getInstance().getBean("templateService");
		 	CollectionService collectionService = (CollectionService) ApplicationContext.getInstance().getBean("collectionService");
		 try{
				
			}catch(Exception e){
				e.printStackTrace();
			}
			/*
			 * 日期查找
			 * <20001PG模板
			 * 20001-30000之间SS模板
			 * 30001-40001WP模板
			 * >40001sc模板
			 * */
			try{
			
		
				
		
				dateTemplates=templateService.findTemplate("from Template  order by date desc");
				downloadTemplates=templateService.findTemplate("from Template order by thitCount desc ");
				okTemplates=templateService.findTemplate("from Template  order by thitCount desc");
				int i=1,a=1,b=1;
				if(dateTemplates.size()>21){
					i =21;
				}else{
					i=dateTemplates.size();
				}
				if(okTemplates.size()>41){
					a =41;
				}else{
					a=okTemplates.size();
				}
				if(downloadTemplates.size()>20){
					b =20;
				}else{
					b=downloadTemplates.size();
				}
				if (dateTemplates.size()>0)
				{
					servletContext.setAttribute("uTemplates", dateTemplates.subList(0, i));
				}
				if (okTemplates.size()>21)
				{
					servletContext.setAttribute("okTemplates", okTemplates.subList(21, a));
				}
				if (downloadTemplates.size()>0)
				{
					servletContext.setAttribute("downloadTemplates", downloadTemplates.subList(0, b));
				}
				/*
				 * 点击率查找
				 * */
				List pgList1= templateService.findTemplate("from Template where  tType < 20001 order by thitCount desc");
				List ssList1=templateService.findTemplate("from Template where tType  between 20000 and 30000  order by thitCount desc ");
				List wpList1=templateService.findTemplate("from Template where tType between 30000 and 40000  order by thitCount desc ");
				List templateList = templateService.findTemplate("from Template  order by thitCount desc ");
//				List templateList1 = templateService.findTemplate("from Template  order by date desc ");
				List typeMenu=templateService.findTemplateType("from TemplateType");
			//	userInfos =userInfoService.findUserInfo("from UserInfo order by point desc ");
				templates=templateService.findTemplate("from Template  order by thitCount desc");
				collections = collectionService.find("from Collection order by date desc");
				int e=1,f=1,g=1,h=1,j=1,k=1;
				
				if(templateList.size()>14){
					j =14;
				}else{
					j=templateList.size();
				}
				if(templates.size()>9){
					k=9;
				}else{
					k=templates.size();
				}
				if(collections.size()>9){
					e=9;
				}else{
					e=collections.size();
				}
				servletContext.setAttribute("templateList", templateList.subList(0, j));
				servletContext.setAttribute("templates", templates.subList(0, k));
				servletContext.setAttribute("collections", collections.subList(0, e));
//				servletContext.setAttribute("pgList1", pgList1.subList(0, e));
//				servletContext.setAttribute("wpList1", wpList1.subList(0, f));
//				servletContext.setAttribute("ssList1", ssList1.subList(0, g));
//				servletContext.setAttribute("typeMenu", typeMenu);
//				servletContext.setAttribute("templatesList", templates);
			}catch(Exception e){
				e.printStackTrace();
			}
	    }
    public DataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	
	
	
	

}
