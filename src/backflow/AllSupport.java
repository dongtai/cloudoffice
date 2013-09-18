package backflow;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.mail.SimpleMailMessage;

import apps.transmanager.weboffice.databaseobject.Users;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


/**
 * Base Action class for the Tutorial package.
 */
public class AllSupport extends ActionSupport implements Action {
	/**
	 * 用于操作后给用户的提示
	 * @author sunaihua
	 * @date 2011-4-8
	 */
	public static int defaultnum=20;
	public String message="";
	
	public String url="";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 详细信息页面
	 * */
	protected static final String VIEW = "view";
	/**
	 * 列表页面
	 * */
	protected static final String LIST = "list";
	/**
	 * 帶錯誤信息的列表页面
	 * */
	protected static final String LISTR = "listr";
	/**
	 * 添加页面
	 * */
	protected static final String ADD = "add";
	/**
	 * 修改页面
	 * */
	protected static final String EDIT = "edit";
	/**
	 * 查询结果页面
	 * */
	protected static final String RESULT = "result";
	/**
	 * 弹出信息页面
	 * */
	protected static final String INFO = "info";
	/**
	 * 
	 */
	protected Logger log = Logger.getLogger(this.getClass());
	

	/**
	 * A message pre-populated with default data
	 */
	protected SimpleMailMessage mailMessage;
	
	protected Integer id;
	
	/**
     * 返回當前進程的Session
     * 
     * @return Map<Object, Object>
     */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSession() {
		return ActionContext.getContext().getSession();
	}
	
	/**
     * 返回當前進程的request
     * 
     * @return HttpServletRequest
     */
	@SuppressWarnings("unchecked")
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
	
	/**
     * 返回當前進程的response
     * 
     * @return HttpServletResponse
     */
	@SuppressWarnings("unchecked")
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/**
     * 返回Request中name的信息
     * 
     * @param name
     * @return String
     */
	public String getParameter(String name) {
		return ServletActionContext.getRequest().getParameter(name);
	}
	
	public Users getUsers()
	{
		return (Users)getRequest().getSession().getAttribute("userKey");
	}
	
	public void setMailMessage(SimpleMailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
