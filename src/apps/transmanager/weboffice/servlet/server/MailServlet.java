package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.MailHandler;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.JSONTools;

public class MailServlet extends AbstractServlet {

	public static final String CREATACC = "createaccount";// 账户的创建

	public static final String EDITACC = "editaccount";// 账户的修改

	public static final String DELACC = "deleteaccount";// 删除账户

	public static final String GETATACC = "getaccount";// 账户的获取//获取当前账户，如果已经登录，则在session中获取。

	public static final String GETATACCLIST = "getaccountlist";// 登录时获取默认账户邮件

	public static final String LISTACC = "listaccount";// 获取所有账户

	public static final String LOGIN = "login";// 登录默认邮箱//目前支持

	public static final String SETDEFAULT = "setdefaultacc";// 设置默认邮箱账户

	public static final String SETACTIVEACC = "setactiveacc";// 设置当前活动账户
	// public static final String INITACC = "initaccount";// 邮件初始化---废弃

	public static final String FOLDINFO = "foldinfo";// 邮件文件夹信息

	public static final String LIST = "list";// 邮件列表展示

	public static final String READ = "read";// 阅读邮件

	public static final String RECEIVE = "receive";// 收邮件

	public static final String RECEIVE_STATUS = "receivestatus";// 收邮件状态

	public static final String SEND = "send";// 发邮件

	public static final String SAVETODRAFT = "savetodraft";// 保存至草稿箱

	public static final String REPLY = "reply";// 回复邮件

	public static final String FORWARD = "forward";// 转发邮件

	public static final String DEL = "del";// 删除邮件

	public static final String CLEARTRASH = "cleartrash";// 删除邮件

	public static final String ADDATT = "addatt";// 上传附件

	public static final String DELATT = "delatt";// 删除附件

	public static final String DOWNATT = "downatt";// 下载附件

	public static final String SAVEATTACHTOWEBDOC = "saveAttachTowebDoc";// 保存至文档中心

	public static final String ISFILESEXIST = "isFilesExist";// 是否同名文件

	public static final String PREVIEWATTACH = "previewAttach";// 附件预览

	public static final String OPENATTACH = "openattach";// 在线编辑

	public static final String SEARCH = "search";// 查询

	public static final String GETSIGN = "getsign";// 获取签名

	public static final String SETSIGN = "setsign";// 设置签名

	public static final String SETALLMAILSEEN = "setallmailseen";// 设置所有邮件已读。

	public static final String SHOWCID ="showcid";//显示邮件内容中的图片内容
	
	public static final String SENDNOTIFICATION = "sendnotification"; //发送邮件回执
	
	@Override
	protected String handleService(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws Exception, IOException {
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String method = (String) jsonParams.get(ServletConst.METHOD_KEY); //
		if (method == null || method.length() <= 0) // json请求方法参数不可以为null
		{
			return  JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR,	null);
		}
		if (param == null) {
			jsonParams.put(ServletConst.PARAMS_KEY,
					new HashMap<String, Object>());
			param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		}
		if (method.equals(SHOWCID)) {
			return MailHandler.showCid(req, resp, jsonParams);
		}
		String account = (String) param.get("account");
		Users user = null;
				//(Users) req.getSession().getAttribute("userKey");  // 错误做法，将删除，由api自己带相应参数
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		if(user ==null && account!=null)
		{
			Users userFromMobile = userService.getUser(account);
			user = userFromMobile;
		}
		if (user ==null)
		{
			//要加容錯信息
			return null;
		}
		if (user != null) {

			// 除了登录外，目前其他操作都需要验证用户的登录token是否有效。
			if (!UserOnlineHandler.isValidate(jsonParams))//目前只移动端检查token
			{
				return  JSONTools.convertToJson(ErrorCons.SYSTEM_TOKEN_ERROR, null);
			}
			jsonParams = param;
			if (method.equals(CREATACC))
			{
				return MailHandler.createUserAccount(req, resp, jsonParams,user);
			} 
			if (method.equals(EDITACC)) {
				return MailHandler.editUserAccount(req, resp, jsonParams,user);
			} 
			if (method.equals(DELACC)) {
				return MailHandler.deleteUserAccount(req, resp, jsonParams,user);
			} 
			if (method.equals(LOGIN)) {
				return MailHandler.login(req, resp, jsonParams,user);
			} 
			if (method.equals(GETATACC)) {
				return MailHandler.getUserAccount(req, resp, jsonParams,user);
			} 
			if (method.equals(GETATACCLIST)) {
				return MailHandler.deflistmail(req, resp, jsonParams,user);
			} 
			if (method.equals(LISTACC)) {
				return MailHandler.listUserAccount(req, resp, jsonParams,user);
			}
			if (method.equals(SETDEFAULT)) {
				return MailHandler.setdefaultacc(req, resp, jsonParams,user);
			}
			if (method.equals(SETACTIVEACC)) {
				return MailHandler.setactiveacc(req, resp, jsonParams,user);
			}
			
			if (method.equals(LIST)) {
				return MailHandler.listmail(req, resp, jsonParams,user);
			}
			if (method.equals(FOLDINFO)) {
				return MailHandler.getFolderInfo(req, resp, jsonParams,user);
			} 
			if (method.equals(READ)) {
				return MailHandler.open(req, resp, jsonParams,user);
			} 
			if (method.equals(RECEIVE)) {
				return MailHandler.receive(req, resp, jsonParams,user);
			} 
			if (method.equals(RECEIVE_STATUS)) {
				return MailHandler.receivestatus(req, resp, jsonParams,user);
	
			}
			if (method.equals(SEND)) {
				return MailHandler.send(req, resp, jsonParams,user);
	
			} 
			if (method.equals(SENDNOTIFICATION)) {
				return MailHandler.sendnotification(req, resp, jsonParams,user);
	
			}
			if (method.equals(SAVETODRAFT)) {
				return MailHandler.save(req, resp, jsonParams,user);
	
			}
			if (method.equals(REPLY)) {// 需要考虑
				return MailHandler.send(req, resp, jsonParams,user);
	
			}
			if (method.equals(FORWARD)) {// 需要考虑
				// 更多的是邮件的附件..
				return MailHandler.send(req, resp, jsonParams,user);
			}
			if (method.equals(DEL)) {
				return MailHandler.delete(req, resp, jsonParams,user);
	
			}
			if (method.equals(CLEARTRASH)) {
				return MailHandler.clearTrash(req, resp, jsonParams,user);
	
			}
			if (method.equals(ADDATT)) {
				return MailHandler.uploadAttach(req, resp, jsonParams);
	
			}
			if (method.equals(DELATT)) {
				return MailHandler.delAttach(req, resp, jsonParams);
	
			}
			if (method.equals(DOWNATT)) {
				return MailHandler.downloadAttach(req, resp, jsonParams,user);
	
			}
			if (method.equals(ISFILESEXIST)) {
				return MailHandler.isFilesExist(req, resp, jsonParams,user);
	
			}
			if (method.equals(SAVEATTACHTOWEBDOC)) {
				return MailHandler.saveAttachTowebDoc(req, resp, jsonParams,user);
	
			}
			if (method.equals(PREVIEWATTACH)) {
				return MailHandler.previewAttach(req, resp, jsonParams, this,user);
	
			} 
			if (method.equals(OPENATTACH)) {
				return MailHandler.openAttach(req, resp, jsonParams,user);
	
			}
			
			if (method.equals(SEARCH)) {
				return MailHandler.search(req, resp, jsonParams,user);
	
			} 
			if (method.equals(GETSIGN)) {
				return MailHandler.getSign(req, resp, jsonParams,user);
	
			}
			if (method.equals(SETSIGN)) {
				return MailHandler.saveSign(req, resp, jsonParams,user);
			}
			if (method.equals(SETALLMAILSEEN)) {
				return MailHandler.setallmailseen(req, resp, jsonParams,user);
			}

		}
			// 无效请求方法
		return  JSONTools.convertToJson(ErrorCons.SYSTEM_REQUEST_ERROR, method);
			
	}

}
