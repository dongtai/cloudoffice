package templates.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import templates.service.InitService;

import com.opensymphony.xwork2.ActionContext;

import flowform.AllSupport;

public class InitAction extends AllSupport{
	
	
	private HttpServletRequest request = ServletActionContext.getRequest();
	private Map<String, Object> session = ActionContext.getContext().getSession();
    private ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()); 
	private InitService initService = (InitService) ctx.getBean("initService");
	
	public String addNewsType(){
	
			String sql =null;
			sql="insert into newstype(tid,typeNames) values('1','新闻');"; 
			initService.insert(sql);
			sql="insert into newstype(tid,typeNames) values('2','公告');"; 
			initService.insert(sql);
			sql="insert into newstype(tid,typeNames) values('3','行业');"; 
			initService.insert(sql);
			sql="insert into newstype(tid,typeNames) values('4','关注企业');"; 
			initService.insert(sql);
			  String user = "root"; // 数据库帐号 
			   String password = "hot.com.3"; // 登陆密码 
			   String database = "templatedb"; // 数据库名 
			   String filepath = "/xyj/1.sql"; // 备份的路径地址 
			   /**导出
			   String stmt1 = "mysqldump " + database + " -u " + user + " -p" + 
			       password + " --default-character-set=utf8 --result-file=" + filepath; 
			   */
			   /**导入
			   String stmt  =  "mysql -uroot -phot.com.3 < /xyj/1.sql";
			   System.out.println("dd"+stmt); 
			   System.out.println("dd"+stmt1); 
			   Runtime.getRuntime().exec(stmt);
			    System.out.println("数据已导出到文件" + filepath + "中"); 
			    */
		return SUCCESS;
	}

}
