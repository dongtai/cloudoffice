package apps.transmanager.weboffice.dwr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import apps.moreoffice.LocaleConstant;
import apps.moreoffice.ext.share.QueryDb;
import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.MainConstants;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.databaseobject.ApprovalDefaulter;
import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.ApprovalSave;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetInfo;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetSave;
import apps.transmanager.weboffice.databaseobject.transmanage.TransInfo;
import apps.transmanager.weboffice.databaseobject.transmanage.TransSave;
import apps.transmanager.weboffice.domain.ApproveBean;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.UserinfoView;
import apps.transmanager.weboffice.service.approval.ApprovalUtil;
import apps.transmanager.weboffice.service.approval.MeetUtil;
import apps.transmanager.weboffice.service.approval.SignUtil;
import apps.transmanager.weboffice.service.approval.TransUtil;
import apps.transmanager.weboffice.service.archive.ArchiveUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.PrintClient;
import apps.transmanager.weboffice.util.server.PrintUtil;
import apps.transmanager.weboffice.util.server.convertforread.FileUtil;
import apps.transmanager.weboffice.util.server.convertforread.bean.ConvertForRead;
import emo.net.SimpleFileinfo;

public class ApprovalDwr {
	private static final String NODE_ID = "id"; // 树ID
	private static final String NODE_NAME = "text"; // 树节点标题
	private static final String NODE_LEAF = "leaf";// 是否为叶子节点
	private static final String NODE_EXPANDABLE = "expandable";// 是否能展开
	private static final String NODE_ICON = "icon";// 节点图标
	// private static final String NODE_PERSON = "person";
	private static final String NODE_ICONCLS = "iconCls";// 节点图标的样式

	/**
	 * 普通人员---我的待阅文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getReadingDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getReadingDocument(user.getId(),
				-1, start, limit, sort, dir);
		Map<String, Object> result = convertView(dh,req);
		return result;
	}
	
	public String getUserName(HttpServletRequest req){
		Users users = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		String userName = "";
		if(users!=null){
			userName = users.getRealName();
		}
		return userName;
	}
	/**
	 * 普通人员---我的已阅文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	
	public Map<String, Object> getReadedDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getReadedDocument(user.getId(),
				-1, start, limit, sort, dir);
		Map<String, Object> result = convertView(dh,req);
		return result;
	}
	/**
	 * 普通人员---我的批阅文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getReadAllDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getReadAllDocument(user.getId(),
				-1, start, limit, sort, dir);
		Map<String, Object> result = convertView(dh,req);
		return result;
	}
	private Map<String,Object> convertView(DataHolder dh,HttpServletRequest req)
	{
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
		.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status",bean.getIsRead().equals("0")?LocaleConstant.instance.getValue("approval_read"):LocaleConstant.instance.getValue("approval_readed"));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			// 加入下载
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}
	/**
	 * 普通人员---我的签批文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getAllMyDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllMyDocument(user.getId(),
				-1, start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			// 加入下载
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}
//	public Map<String, Object> getSearchDocument(int start, int limit,int type,String key,int status,
//			 HttpServletRequest req)
//	{
//		return getSearchDocumentB(start, limit, type, key, status,"","desc", req);
//	}
	/**
	 * 搜索我的第一类文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getSearchDocument(int start, int limit,int type,String key,int status,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
		.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllSearchDocument(user.getId(), type, key,status,start,10,sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			// 加入下载
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}
//	public Map<String, Object> getSearchReadDocument(int start, int limit,int type,String key,
//			 HttpServletRequest req)
//	{
//		return getSearchReadDocumentB(start, limit, type, key, "", "desc", req);
//	}
	/**
	 * 搜索我的第一类文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getSearchReadDocument(int start, int limit,int type,String key,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
		.getBean(JCRService.NAME);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllSearchReadDocument(user.getId(), type, key,start,10,sort, dir);
		Map<String, Object> result = convertView(dh, req);
		return result;
	}

	/**
	 * 普通人员--送审中的文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getMyPaendingDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getMyPaending(user.getId(),
				ApproveConstants.APPROVAL_STATUS_PAENDING, start, limit, sort,
				dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", "签批中");
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 普通人员---我的已通过文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getMyPassDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance()
				.getMyPaending(user.getId(),
						ApproveConstants.APPROVAL_STATUS_AGREE, start, limit,
						sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			if ("1".equals(bean.getIsRead())) {
				map.put("showFileName", bean.getFileName());

			} else {
				map.put("showFileName", "<b>" + bean.getFileName() + "</b>");
				map.put("signer", "<b>" + bean.getTaskApprovalUserName()
						+ "</b>");
			}
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 普通人员---我的已退回文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getMyReturnDocuemnt(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getMyPaending(user.getId(),
				ApproveConstants.APPROVAL_STATUS_RETURNED, start, limit, sort,
				dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			if ("1".equals(bean.getIsRead())) {
				map.put("showFileName", bean.getFileName());
			} else {
				map.put("showFileName", "<b>" + bean.getFileName() + "</b>");
				map.put("signer", "<b>" + bean.getTaskApprovalUserName()
						+ "</b>");
			}
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 普通人员---我的已废弃文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getMyAbandonedDocuemnt(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getMyPaending(user.getId(),
				ApproveConstants.APPROVAL_STATUS_ABANDONED, start, limit, sort,
				dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getTaskApprovalUserDept());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 普通人员---我的已办结文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getMyEndDocuemnt(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllEndDocument(user.getId(),
				ApproveConstants.APPROVAL_STATUS_END, start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 普通人员--办公室--领导---查看发布的文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getPublishDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getPublishDocument(
				user.getId(), start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 获取系统我的空间、协作空间文档
	 * 
	 * @param parentID
	 *            父节点ID值
	 * @param req
	 *            请求信息，为了确定用户ID
	 * @return 我的空间、协作空间文件列表
	 */

	public List<Map<String, Object>> getMyFoldAndWorkspace(String parentID,
			HttpServletRequest req) {
		return getMyFoldAndWorkspace(parentID, true, req);
	}
	public List<Map<String, Object>> getMyFoldAndWorkspaceOnlyFold(String parentID,
			HttpServletRequest req) {
		return getMyFoldAndWorkspace(parentID, false, req);
	}
	public List<Map<String, Object>> getMyFoldAndWorkspace(String parentID, boolean iscontainFile,
			HttpServletRequest req) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try
		{
			Users user = (Users) req.getSession().getAttribute(
					PageConstant.LG_SESSION_USER);
			FileSystemService fileService = (FileSystemService) ApplicationContext
					.getInstance().getBean(FileSystemService.NAME);
			JCRService jcrService = (JCRService) ApplicationContext.getInstance()
					.getBean(JCRService.NAME);
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			
			if (user != null) {
				ArrayList json = new ArrayList();
				HashMap<String, Object> files;
				String path = parentID;
				if("firm".equals(parentID))
				{
					path = fileService.getCompanySpaceIdByUserId(user.getId()) + "/"+ FileConstants.DOC; 
				}
				if (path == null || path.length() < 1 || path.equals("/")
						|| "my".equals(path) || "group".equals(path)) {
					//List<Spaces> listS = fileService.getGroupSpacesByUserId(user.getId());
					Spaces company_space = fileService.getSpace(user.getCompany().getSpaceUID());	//更改做法以后的企业文库
					List<Spaces> listS = new ArrayList<Spaces>();
					listS.add(company_space);
					List<Spaces> teamlistS = fileService.getTeamSpacesByUserId(user
							.getId());
					List smplFileList = new ArrayList<SimpleFileinfo>();
					files = new HashMap<String, Object>();
					if ("my".equals(path)) {
						files.put("name", MainConstants.PERSON_DOCUMENT);
						files.put("folder", true);
						files.put("path", user.getSpaceUID() + "/"
								+ FileConstants.DOC);
						files.put("displayPath", MainConstants.PERSON_DOCUMENT);
						json.add(files);
					}
//					if (pubFlag == module) {
//						for (Spaces temp : listS) {
//							if (temp!=null)
//							{
//								files = new HashMap<String, Object>();
//								files.put("name",temp.getName());
//								files.put("folder", true);
//								files.put("path", temp.getSpaceUID() + "/"
//										+ FileConstants.DOC);
//								files.put("displayPath",temp.getName());
//								json.add(files);
//							}
//						}
//					}
					if ("group".equals(path)) {
						for (Spaces temp : teamlistS) {
							files = new HashMap<String, Object>();
							files.put("name", temp.getName()
									+ MainConstants.TEAM_DOCUMENT);
							files.put("folder", true);
							files.put("path", temp.getSpaceUID() + "/"
									+ FileConstants.DOC);
							files.put("displayPath", temp.getName()
									+ MainConstants.TEAM_DOCUMENT);
							json.add(files);
						}
					}
					result = convertFileList(json);
				} else {
					Integer start = 0;
					Integer count = 10000;
					int index = start != null && start >= 0 ? start : 0;
					int c = count != null && count >= 0 ? count : 1000000;
					//判断权限有没有？？？
					PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
					long permission = service.getFileSystemAction(user.getId(), path, true);
					boolean flag = FlagUtility.isValue(permission, FileSystemCons.BROWSE_FLAG);
					if(flag)
					{
					List list = jcrService.listPageFileinfos("", path, index, c);
					if (list != null && list.size() > 0) {
						list.remove(0);
						Fileinfo file;
						for (Object file1 : list) {
							file = (Fileinfo) file1;
							if(!file.isFold())
							{
								if(!iscontainFile)
								continue;
							}
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
					// 过滤权限
					result = convertFileList(json);
					}
				}
				result = ApprovalUtil.instance().filterNoRoleFile(user.getId(), result);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static List<Map<String, Object>> convertFileList(
			List<Map<String, Object>> list) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		try {
			for (Map<String, Object> map : list) {
				Map<String, Object> nodeMap = new HashMap<String, Object>();
				nodeMap.put(NODE_ID, map.get("path"));
				nodeMap.put(NODE_NAME, map.get("name"));
				Boolean isFold = (Boolean) map.get("folder");
				if (!isFold.booleanValue()) {
					String filename = (String) map.get("name");
					int index = filename.lastIndexOf(".");
					if (index == -1) {
						nodeMap.put(NODE_ICON, PageConstant.LG_DEFAULT_FILE);
					} else {
						String filetype = filename.substring(index + 1,
								filename.length());
						if (filetype.toLowerCase().equals("bmp") || filetype.toLowerCase().equals("csv")
								|| filetype.toLowerCase().equals("db")
								|| filetype.toLowerCase().equals("dbf")
								|| filetype.toLowerCase().equals("doc")
								|| filetype.toLowerCase().equals("docx")
								|| filetype.toLowerCase().equals("dot")
								|| filetype.toLowerCase().equals("eio")
								|| filetype.toLowerCase().equals("eit")
								|| filetype.toLowerCase().equals("eiw")
								|| filetype.toLowerCase().equals("emf")
								|| filetype.toLowerCase().equals("gif")
								|| filetype.toLowerCase().equals("htm")
								|| filetype.toLowerCase().equals("html")
								|| filetype.toLowerCase().equals("jpg")
								|| filetype.toLowerCase().equals("pdf")
								|| filetype.toLowerCase().equals("png")
								|| filetype.toLowerCase().equals("pot")
								|| filetype.toLowerCase().equals("pps")
								|| filetype.toLowerCase().equals("ppt")
								|| filetype.toLowerCase().equals("pptx")
								|| filetype.toLowerCase().equals("rar")
								|| filetype.toLowerCase().equals("rtf")
								|| filetype.toLowerCase().equals("tiff")
								|| filetype.toLowerCase().equals("txt")
								|| filetype.toLowerCase().equals("uof")
								|| filetype.toLowerCase().equals("wmf")
								|| filetype.toLowerCase().equals("xls")
								|| filetype.toLowerCase().equals("xlsx")
								|| filetype.toLowerCase().equals("xlt")
								|| filetype.toLowerCase().equals("xml")
								|| filetype.toLowerCase().equals("zip")) {
							nodeMap.put(NODE_ICON,
									PageConstant.LG_DEFAULT_FILEICONPATH + filetype.toLowerCase()
											+ ".gif");
						} else {
							nodeMap.put(NODE_ICON, PageConstant.LG_DEFAULT_FILE);
						}
					}

				}
				nodeMap.put(NODE_LEAF, !isFold);
				nodeMap.put(NODE_EXPANDABLE, isFold);
				nodeList.add(nodeMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeList;
	}

	/**
	 * 送审操作
	 * 
	 * @param filePath
	 *            文件路径
	 * @param showFilePath
	 *            文件名
	 * @param linkManId  接收者ID。如果有预定义的审批任务，则该值为第一个用户的id值，后续的用户id值加入preUserIds中。
	 * @param comment
	 *            备注
	 * @param title
	 *            标题
	 * @param readerIds 阅读者列表
     * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null
	 * @param req
	 * @return
	 */
	public String addAduit(String filePath, String showFilePath,
			String linkManId, String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds,
			String stepName,int issame, HttpServletRequest req) {
		String flag = "0";
		System.out.println(filePath + "---" + linkManId + "---" + comment);
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean result = FilesHandler.uploadFileForAudit(user.getId(),
				filePath, showFilePath, linkManId, comment, title, readerIds, preUserIds, stepName,issame);
		if (result) {
			flag = "1";
		}
		return flag;
	}

	public String getUserInfo(HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		return user.getId() + "-" + user.getRealName();
	}

	/**
	 * 单个文件签批操作
	 * 
	 * @param approveId
	 *            签批ID
	 * @param status
	 *            　操作类型
	 * @param comment
	 *            批注
	 * @param acceptId 接收者ID。如果该次审批是预定的用户，则该值为null。
	 * @param req
	 *            请求信息
	 * @param readerIds 阅读者列表
     * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null 
	 * @return 签批结果
	 */
	public String aduitOperation(ApprovalInfo approvalInfo, String status,
			String comment, String acceptId, ArrayList<Long> readerIds, ArrayList<Long> preUserIds,
			String stepName,int issame,int resend, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().aduitOperation(approvalInfo,
				user.getId(), acceptId, Integer.parseInt(status), comment, readerIds, preUserIds, stepName,issame,resend);
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	public String endOrAbandoned(List approveIds, String status,
			HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		//
		
		String str = ApprovalUtil.instance().endOrAbandoned(approveIds,
				status, user.getId());
		return str;

	}

	/**
	 * 领导---我的签批文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getAllLeaderMyDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllLeaderDocument(
				user.getId() + "", -1, start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());			
			map.put("signerDept", bean.getTaskApprovalUserDept());
			map.put("predefined", bean.getPredefined());
			map.put("signtag", bean.getSigntag());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 领导---我的待签批文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getLeaderNoAduitDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getLeaderPaending(
				user.getId() + "", ApproveConstants.APPROVAL_STATUS_PAENDING,
				start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			map.put("predefined", bean.getPredefined());
			map.put("isRead", bean.getIsRead());
			map.put("signtag", bean.getSigntag());
			if ("1".equals(bean.getIsRead())) {
				map.put("showFileName", bean.getFileName());
			} else {
				map.put("showFileName", "<b>" + bean.getFileName() + "</b>");
				map.put("owner", "<b>" + bean.getUserName() + "</b>");
			}
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 领导---签批通过文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getLeaderAduitDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getLeaderPassOrReturn(
				user.getId(), ApproveConstants.APPROVAL_STATUS_AGREE, start,
				limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 领导---签批退回文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getLeaderAduitReturnDocument(int start,
			int limit, String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getLeaderPassOrReturn(
				user.getId(), ApproveConstants.APPROVAL_STATUS_RETURNED, start,
				limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	private String replaceStatus(int status) {
		String result = "";
		if (status == ApproveConstants.APPROVAL_STATUS_AGREE) {
			result = LocaleConstant.instance.getValue("approval_STATUS_AGREE");//2
		} else if (status == ApproveConstants.APPROVAL_STATUS_RETURNED) {
			result = LocaleConstant.instance.getValue("approval_STATUS_RETURNED");//3
		} else if (status == ApproveConstants.APPROVAL_STATUS_ABANDONED) {
			result = LocaleConstant.instance.getValue("approval_STATUS_ABANDONED");//4
		} else if (status == ApproveConstants.APPROVAL_STATUS_PAENDING) {
			result = LocaleConstant.instance.getValue("approval_STATUSPAENDING");//1
		} else if (status == ApproveConstants.APPROVAL_STATUS_ARCHIVING) {
			result = LocaleConstant.instance.getValue("approval_STATUS_ARCHIVING");//8
		} else if (status == ApproveConstants.APPROVAL_STATUS_PUBLISH) {
			result = LocaleConstant.instance.getValue("approval_STATUS_PUBLISH");//7
		} else if (status == ApproveConstants.APPROVAL_STATUS_END) {
			result = LocaleConstant.instance.getValue("approval_STATUS_END");//5
		}else if (status == ApproveConstants.APPROVAL_STATUS_DESTROY) {
			result = LocaleConstant.instance.getValue("approval_STATUS_DESTROY");//9
		}
		//归档 10
		return result;
	}

	public String returnDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().createApprovalTask(approveIds,
				user.getId(), ApproveConstants.APPROVAL_STATUS_RETURNED);
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 领导---签批并办结文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getLeaderPaendingEnd(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getLeaderPaendingEnd(
				user.getId(), ApproveConstants.APPROVAL_STATUS_RETURNED, start,
				limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", user.getRealName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 办公室人员---查看所有已办结文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getAllEndDocuemnt(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getAllEndDocument(user.getId(),
				ApproveConstants.APPROVAL_STATUS_END, start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			if ("1".equals(bean.getIsRead())) {
				map.put("showFileName", bean.getFileName());
			} else {
				map.put("showFileName", "<b>" + bean.getFileName() + "</b>");
				map.put("owner", "<b>" + bean.getUserName() + "</b>");
				map.put("signer", "<b>" + bean.getTaskApprovalUserName()
						+ "</b>");
			}
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 发布文档
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String publishDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().publishDocument(approveIds,
				user.getId());
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 从办结归档文档
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String filingDocumentDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().filingDocument(approveIds,
				user.getId());
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 从发布归档文档
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String publishToFilingDocument(List approveIds,
			HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().publishToFilingDocument(
				approveIds, user.getId());
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 办公室人员---查看归档的文档
	 * 
	 * @param start
	 * @param limit
	 * @param condition//前台传过来的参数
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getArchiveDocument(int start, int limit,Map condition,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getArchiveDocument(
				user.getId(), start, limit,condition, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 从发布归档-->待销毁
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String toDestoryDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().toDestoryDocument(approveIds,
				user.getId());
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 办公室人员---查看待销毁的文档
	 * 
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @param req
	 * @return
	 */
	public Map<String, Object> getDestoryDocument(int start, int limit,
			String sort, String dir, HttpServletRequest req) {
		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		DataHolder dh = ApprovalUtil.instance().getDestoryDocument(
				user.getId(), start, limit, sort, dir);
		ArrayList<Object> datalist = dh.getFilesData();
		int size = datalist.size();
		for (int i = 0; i < size; i++) {
			ApproveBean bean = (ApproveBean) datalist.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("approveId", bean.getApproveinfoId());
			map.put("filePath", bean.getFilePath());
			map.put("fileName", bean.getFileName());
			map.put("title", bean.getTitle());
			map.put("step", bean.getStepName());
			map.put("nodetype",bean.getNodetype());
			map.put("fileIcon", bean.getFileIcon());
			map.put("owner", bean.getUserName());
			map.put("ownerId", bean.getUserId());
			map.put("signer", bean.getTaskApprovalUserName());
			map.put("status", replaceStatus(bean.getStatus()));
			map.put("ownerDept", bean.getUserDeptName());
			map.put("approveDate", bean.getDate());
			map.put("comment", bean.getComment());
			map.put("signerDept", bean.getTaskApprovalUserDept());
			String downPath = resolverFileList(jcrService, bean.getFilePath(),
					bean.getFileName(), req);
			map.put("downPath",downPath);
			map.put("signtag", bean.getSigntag());
			list.add(map);
		}
		result.put("totalRecords", dh.getIntData());
		result.put("data", list);
		return result;
	}

	/**
	 * 获取用户角色
	 * 
	 * @return
	 */
	public Map<String,Object> getUserApprovalInfo(HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(
        "permissionService");
		long role = service.getSystemPermission(user.getId());
		boolean sendManage = FlagUtility.isValue(role, ManagementCons.AUDIT_SEND_FLAG);
		boolean acceptManage = FlagUtility.isValue(role, ManagementCons.AUDIT_AUDIT_FLAG);
		boolean officeManage = FlagUtility.isValue(role, ManagementCons.AUDIT_MANGE_FLAG);
		boolean archiveManage = FlagUtility.isValue(role, ManagementCons.AUDIT_FILING_FLAG);
		Map<String,Object> map = new HashMap<String, Object>();
//		int count = ApprovalUtil.instance().getAduitFileCount((int)role,
//				user.getId());
		map.put("sendManage", sendManage);
		map.put("acceptManage", acceptManage);
		map.put("officeManage", officeManage);
		map.put("archiveManage",archiveManage);
		return map;
	}

	/**
	 * 获取最近5个联系人
	 * 
	 * @param req
	 * @return
	 */
	public List<Map<String, Object>> getRecentLinkMan(HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().getRecentLinkMan(user.getId());
	}

	/**
	 * 删除文档
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String deleteDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().deleteDocument(user.getId(),
				approveIds);
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 销毁文档
	 * 
	 * @param approveIds
	 * @param publisherId
	 * @param req
	 * @return
	 */
	public String destoryDocument(List approveIds, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		boolean flag = ApprovalUtil.instance().destoryDocument(user.getId(),
				approveIds);
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 获取最近操作历史
	 * 
	 * @param req
	 * @return
	 */
	public Map<String, Object> getApprovalHistory(Long approveId,int historyFlag,
			HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		if(historyFlag ==1)
		{
			return ApprovalUtil.instance().getApprovalHistoryForReader(user.getId(),
					approveId);
		}else{
			return ApprovalUtil.instance().getApprovalHistory(user.getId(),
					approveId);
		}
		
	}
	/**
	 * 代签阅读
	 * @param req
	 * @return
	 */
	public String readApproval(Long approveId,int isread,int isreadFlod, HttpServletRequest req) {
		Users user = (Users) req.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		ApprovalUtil.instance().readApproval(approveId,isread,isreadFlod, user.getId());
		return "1";
	}

	private String resolverFileList(JCRService jcrService, String pathinfo,
			String oldfileName, HttpServletRequest req) {
		String hyperlink = getRealDownFilePath(jcrService, pathinfo,
				oldfileName, req);
		String fileName = oldfileName.toLowerCase();
		if (fileName.endsWith(".jpg") || fileName.endsWith(".JPG")
				|| fileName.endsWith(".gif") || fileName.endsWith(".png")
				|| fileName.endsWith(".PNG") || fileName.endsWith(".gif")
				|| fileName.endsWith(".GIF") || fileName.endsWith(".bmp")
				|| fileName.endsWith(".BMP")) {
			String realhyperlink = "";
			// "http://127.0.0.1:8888/static/downloadService?action=sendmaildown&sendtempfilename=1275031470750.doc&filename=jackrabbit.doc"
			if (hyperlink != null) {

				String[] strs0 = hyperlink.split("\\?");
				if (strs0.length > 1) {
					String[] strs1 = strs0[1].split("&");
					if (strs1.length > 1) {

						realhyperlink = /*
										 * req.getScheme() + "://" +
										 * QueryDb.getIpName
										 * (req.getServerName()) + ":" +
										 * req.getServerPort() +
										 */req.getContextPath()
						// +"/static/open2.jsp"+"?"+strs1[1];
								+ "/data/sendmailfile/" + strs1[1].split("=")[1];
						// System.out.println(realhyperlink);
					}
				}
			}
		}

		return hyperlink;
	}

	public String getRealDownFilePath(JCRService jcrService, String path,
			String fileName, HttpServletRequest request) {
		String tempFolder = WebConfig.sendMailPath;
		try {
			InputStream in = jcrService.getFileContent(path);
			if (fileName==null)
			{
				int index=path.lastIndexOf("/");
				fileName=path.substring(index+1);
			}
			String tempfilenames = System.currentTimeMillis()+fileName.substring(fileName.lastIndexOf('.'));
			File file = new File(tempFolder + File.separatorChar
					+ tempfilenames);

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			byte[] b = new byte[8 * 1024];
			int len = 0;
			FileOutputStream fo = new FileOutputStream(file);
			while ((len = in.read(b)) > 0) {
				fo.write(b, 0, len);
			}
			fo.close();
			String httpUrls = /*
							 * request.getScheme() + "://" +
							 * request.getServerName() + ":" +
							 * request.getServerPort() +
							 */request.getContextPath()
					+ "/static/downloadService?" + "action=sendmaildown"
					+ "&sendtempfilename="
					+ URLEncoder.encode(tempfilenames, "UTF-8") + "&filename="
					+ URLEncoder.encode(fileName, "UTF-8") + "&realpath="
					+ URLEncoder.encode(path, "UTF-8");
			httpUrls = QueryDb.getIpName(httpUrls);

			return httpUrls;
		} catch (Exception e) {
			e.printStackTrace();
			return path;
		}

	}

	/**
	 * 打印文档
	 */
	public Boolean printDocument(List param, HttpServletRequest request) {

		JCRService jcrService = (JCRService) ApplicationContext.getInstance()
				.getBean(JCRService.NAME);
		File file = null;
		WebConfig webConfig = (WebConfig) ApplicationContext.getInstance()
				.getBean("webConfigBean");
		try {
			Map printFileNames = null;
			HttpSession session = request.getSession();
			if (null != session.getAttribute("printFileNames")) {
				printFileNames = (Map) session.getAttribute("printFileNames");
			} else {
				printFileNames = new HashMap();
				session.setAttribute("printFileNames", printFileNames);
			}
			Boolean flag = Boolean.FALSE;
			String filePath = (String) param.get(0);
			String printerName = (String) param.get(1);
			String printNum = (String) param.get(2);
			String printRange = (String) param.get(3);

			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

			// 先产生临时文档
			long time = System.currentTimeMillis();
			int index = fileName.lastIndexOf('.');
			String mime = fileName.substring(index);
			String newName = String.valueOf(time) + mime;
			String temptxtNewName = String.valueOf(time) + "encode" + mime;
			String tempFolder = session.getServletContext().getRealPath(
					"tempfile");
			File foler = new File(tempFolder);
			if (!foler.exists()) {
				foler.mkdir();
			}
			String sourceFileName = tempFolder + File.separatorChar + newName;
			file = new File(sourceFileName);
			file.createNewFile();
			InputStream in = jcrService.getFileContent(filePath);
			FileOutputStream out = new FileOutputStream(file);
			byte[] b = new byte[8 * 1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
			}
			out.close();

			long tempFileSize = file.length();
			String targetFileName = tempFolder + File.separatorChar
					+ String.valueOf(time) + ".pdf";
			// 对txt进行转码
			if (".txt".equals(mime)) {
				// 取得txt的编码
				String encode = FileUtil.getFileEncode(sourceFileName);
				if (!encode.equals("UTF-8")) {
					String outFilename = tempFolder + File.separatorChar
							+ temptxtNewName;
					FileUtil.translateCharset(sourceFileName, outFilename,
							encode, "UTF-8");
					tempFileSize = new File(outFilename).length();
					sourceFileName = outFilename;
					targetFileName = tempFolder + File.separatorChar
							+ String.valueOf(time) + "encode" + ".pdf";
				}
			}

			printFileNames.put(fileName, tempFileSize / 1024 + "kb");
			// 判断文档类型,pdf,图片类型的直接打印,ms,eio,txt转换成pdf打印
			// 支持doc,xls,ppt,docx,xlsx,pptx,rtf--->pdf
			boolean result = false;
			if (".eio".equals(mime) || ".doc".equals(mime)
					|| ".xls".equals(mime) || ".ppt".equals(mime)
					|| ".docx".equals(mime) || ".xlsx".equals(mime)
					|| ".pptx".equals(mime) || ".rtf".equals(mime)
					|| ".txt".equals(mime)) {
				// 调用DCS转换成PDF

				System.out.println("文档转换开始！");
				int convertresult = ConvertForRead.convertMStoPDF(
						sourceFileName, targetFileName);
				System.out.println("文档转换结束！=" + convertresult);
				if (convertresult == 0) {
					if ("1".equals(webConfig.getPrintType()))// 本地打印
					{
						result = PrintUtil.print(param, sourceFileName,
								targetFileName, fileName);
					} else if ("2".equals(webConfig.getPrintType()))// 远程打印
					{
						File tempfile = new File(targetFileName);
						if (null != tempfile) {
							tempFileSize = tempfile.length();
						}
						result = PrintClient.printDocument(
								webConfig.getPrintIP(),
								webConfig.getPrintPort(), targetFileName,
								printerName, printNum, printRange, fileName,
								tempFileSize);
					}

				} else {
					flag = Boolean.FALSE;
				}

			} else if (".jpg".equals(mime) || ".bmp".equals(mime)
					|| ".gif".equals(mime) || ".jpeg".equals(mime)
					|| ".png".equals(mime) || ".pdf".equals(mime)) {
				if ("1".equals(webConfig.getPrintType()))// 本地打印
				{
					result = PrintUtil.print(param, sourceFileName, null,
							fileName);
				} else if ("2".equals(webConfig.getPrintType()))// 远程打印
				{
					result = PrintClient.printDocument(webConfig.getPrintIP(),
							webConfig.getPrintPort(), sourceFileName,
							printerName, printNum, printRange, fileName,
							tempFileSize);
				}
			} else {
				// 不支持的格式不打印
				flag = Boolean.FALSE;
			}
			flag = Boolean.valueOf(result);
			// convert
			// 产生PDF完毕交由打印
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} finally {
			if (null != file) {
				// file.delete();
			}
		}

	}

	public List getPrinters(HttpServletRequest req) {
		WebConfig webConfig = (WebConfig) ApplicationContext.getInstance()
				.getBean("webConfigBean");
		if ("1".equals(webConfig.getPrintType()))// 本地打印
		{
			return PrintUtil.getPrints();
		} else if ("2".equals(webConfig.getPrintType()))// 远程打印
		{
			if (null == webConfig.getPrintIP()
					|| "".equals(webConfig.getPrintIP())) {
				return null;
			}
			return PrintClient.getAllPrinter(webConfig.getPrintIP(),
					webConfig.getPrintPort());
		} else {
			return null;
		}

	}

	public Map<String,Object> getFileCount(Map<String,Boolean> userPerm,HttpServletRequest request) {
		Users user = (Users) request.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().getFileCount(user.getId(),userPerm);
	}
	
	/**
	 * 批阅
	 * @param id 批阅记录ID
	 * @param comment 批阅备注
	 * @param request
	 * @return 是否成功
	 */
	public boolean piyue(Long id,String comment,HttpServletRequest request){
		try{
			ApprovalUtil.instance().piyue(id,comment);
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
		
	}
	/**
	 * 批量转PDF
	 * @param ids
	 * @param request
	 * @return
	 */
	public String changePdf(int[] ids,HttpServletRequest request)
	{
		try{
			Users user = (Users) request.getSession().getAttribute(
					PageConstant.LG_SESSION_USER);
			return ApprovalUtil.instance().changePdf(ids,user);
		}catch (Exception e) {
			e.printStackTrace();
			return LocaleConstant.instance.getValue("changeFail");
		}
	}
	
	public String replaceFile(Long approveId,String filePath,String showFP
			,String filePath_new,String showFP_new,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().replaceFile(approveId,filePath,showFP
				,filePath_new,showFP_new,user);
	}
	
	public String deleteReadinfo(Long[] readIds,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(
				PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().deleteReadinfo(readIds,user);
	}
	public String auditGoBackOper(Long[] ids,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().auditGoBackOper(ids,user);
	}
	public String checkSender(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return ApprovalUtil.instance().checkSender(id,user);
	}
	
	/**
	 * 获取本单位所有人员，包括领导
	 * @param flowinfoid
	 * @param nodeid
	 * @param orgid
	 * @param request
	 * @return
	 */
	public List<UserinfoView> getMans(Long flowinfoid,Long nodeid,Long orgid,HttpServletRequest request)
	{
		
		return ApprovalUtil.instance().getMans(flowinfoid,nodeid,orgid);
	}
	public List<UserinfoView> updateMans(Long flowinfoid,Long nodeid,Long orgid,Long[] pams,HttpServletRequest request)
	{
		ApprovalUtil.instance().updateMans(flowinfoid,nodeid,orgid,pams);
		return getMans(flowinfoid, nodeid, orgid, request);
	}
	
	public List<UserinfoView> getStateMans(Long flowinfoid,Long stateid,Long orgid,HttpServletRequest request)
	{
		return ApprovalUtil.instance().getStateMans(flowinfoid,stateid,orgid);
	}
	
	public List<UserinfoView> updateStateMans(Long flowinfoid,Long stateid,Long orgid,Long[] pams,HttpServletRequest request)
	{
		ApprovalUtil.instance().updateStateMans(flowinfoid,stateid,orgid,pams);
		return getStateMans(flowinfoid, stateid, orgid, request);
	}
	
	public List<UserinfoView> getSubMans(Long flowinfoid,Long nodeid,Long orgid,Long subnodeid,HttpServletRequest request)
	{
		
		return ApprovalUtil.instance().getSubMans(flowinfoid,nodeid,orgid,subnodeid);
	}
	public List<UserinfoView> updateSubMans(Long flowinfoid,Long nodeid,Long orgid,Long subnodeid,Long[] pams,HttpServletRequest request)
	{
		ApprovalUtil.instance().updateSubMans(flowinfoid,nodeid,orgid,subnodeid,pams);
		return getSubMans(flowinfoid, nodeid, orgid,subnodeid, request);
	}
	public FlowAllNode getNodetype(Long nodeid) throws Exception
	{
		return ApprovalUtil.instance().getNodetype(nodeid);
	}
	/**
	 * 获取归档对话框需要的初始化数据
	 * @param request
	 * @return
	 */
	public List<String[]> getArchiveInitData(HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		try
		{
			return ArchiveUtil.instance().getArchiveInitData(user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 处理归档表单
	 * @param id 表单编号，不为空就是编辑
	 * @param appid 签批编号
	 * @param type 类型
	 * @param security  秘密
	 * @param script  说明
	 * @param request
	 * @return
	 */
	public String modifyArchive(List approveIds,String type,String parentid,String security,String script,HttpServletRequest request)
	{
		
		//以下是增加新的归档方法
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		Long pid=0L;
		try
		{
			pid=Long.valueOf(parentid);
		}
		catch (Exception e)
		{
			
		}
		String back = ArchiveUtil.instance().modifyArchive(approveIds, type,pid, security, script, user);
		String oldback=filingDocumentDocument(approveIds, request);//调用原来的归档方法
		if ("0".equals(oldback))
		{
			back="error";
		}
		return back;
	}
	
	public boolean signReal(String id,HttpServletRequest request){
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().signreal(id, user);
	}
	public boolean getSignReal(String id,HttpServletRequest request){
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getSignReal(id, user);
	}
	public Map<String,Object> getArchivePermit(String approvalId,HttpServletRequest request){
		
		return ArchiveUtil.instance().getArchivePermitData(approvalId);
	}
	
	public boolean setArchivePermit(String approvalId,String[][] userPermitList,Date enddate,HttpServletRequest request){
		//System.out.println(userPermitList.size());
		return ArchiveUtil.instance().setArchivePermitData(approvalId,userPermitList,enddate);
	}
	
	/**
	 * 是否具有权限
	 * @param approvalId 签批id
	 * @param type 查阅权限type=1，下载权限type=2，替换权限type=3，删除权限type=4
	 * @param request
	 * @return
	 */
	public boolean hasPermit(String approvalId,int type,HttpServletRequest request){
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return ArchiveUtil.instance().hasPermit(approvalId,type,user.getId());
	}
	/**
	 * 判断目标文件夹中有没有同名文件
	 * @param filenames
	 * @param newpath
	 * @param request
	 * @return
	 */
	public boolean isCopyExist(String[] filenames,String newpath,HttpServletRequest request)
	{
		return ArchiveUtil.instance().isCopyExist(filenames,newpath);
	}
	/**
	 * 将目标文件复制到指定的文件夹
	 * @param oldfiles 选中的文件，绝对路径
	 * @param newpath  选中的新文件夹  绝对路径
	 * @param copysize 复制份数，默认是1份
	 * @param request
	 * @return
	 */
	public boolean copyFiles(String[] oldfiles,String newpath,Integer copysize,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return ArchiveUtil.instance().copyFiles(oldfiles,newpath,copysize,user);
	}
	//以下是新产品方法-----------------------------------------------------
	
	
	

	/**
	 * 送审和保存方法，这里少了只有内容没有文件的现象
	 * @param status 0表示保存，1表示送审
	 * @param id 保存的送审编号，直接送审传0或null
	 * @param sendchecked 送签按钮选中
	 * @param checkread  送阅按钮选中
	 * @param title  送签标题
	 * @param sendfiles  送签的文件，可以为空
	 * @param accepters  接受者
	 * @param samesign   是否会签
	 * @param backsender 返回送审者
	 * @param readers  阅读者
	 * @param filetypeCombo 文件类别
	 * @param backsigners 会签后的处理人
	 * @param comment  备注
	 * @param request
	 * @return
	 */
	public boolean sendSign(Integer status,Long id,Boolean sendchecked,Boolean checkread
	,String title,ArrayList<String> sendfiles,ArrayList<String> sendfilenames,String webcontent
	,ArrayList<Long> accepters,Boolean samesign,Boolean backsender
	,String readers,String filetypeCombo,String backsigners,String comment
	,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript
	,HttpServletRequest request)
	{
		//先判断是插入还是更新，如果是更新文件怎么处理？文件路径有文件库、硬盘、个人文库
		//如果是更新，送审对话框还需要初始化数据
		try
		{
			Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			System.out.println("===============");
			if (status==null || status==0)
			{
				//保存
				return SignUtil.instance().saveSign(id,sendchecked,checkread
						,title,sendfiles,sendfilenames,webcontent,accepters,samesign,backsender,readers,filetypeCombo,backsigners,comment,fileflowid,filesuccdate,fromunit,filecode,filescript,user);
			}
			else
			{
				//直接送审
				return SignUtil.instance().sendSign(id,sendchecked,checkread
						,title,sendfiles,sendfilenames,webcontent,accepters,samesign,backsender,readers,filetypeCombo,backsigners,comment
						,fileflowid,filesuccdate,fromunit,filecode,filescript,user);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 保存或送文档协作
	 * @param status 0表示保存，1表示送审
	 * @param id 保存的送审编号，直接送审传0或null
	 * @param title   送签标题
	 * @param fileList 送写作文件列表
	 * @param cooperId 协作人，用,间隔
	 * @param filetypeCombo 文件类别
	 * @param backsigners 会签后处理人
	 * @param comment 备注
	 * @param request
	 * @return
	 */
	public boolean sendCooper(Integer status,Long id,String title,List<String> fileList,List<String> sendfilenames,String webcontent
			,String cooperId,String filetypeCombo,String backsigners, String comment
			,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript,
			HttpServletRequest request)
	{
		try
		{
			Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
			System.out.println("===============");
			if (status==null || status==0)//保存写作信息
			{
				return SignUtil.instance().saveCooper(id,title,fileList,sendfilenames,webcontent, cooperId, comment,fileflowid,filesuccdate,fromunit,filecode,filescript,user);
			}
			else //送写作信息
			{
				return SignUtil.instance().sendCooper(id,title,fileList,sendfilenames,webcontent,cooperId,comment,fileflowid,filesuccdate,fromunit,filecode,filescript,user);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public ApprovalSave getApprovalSave(Long id,String seltype,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getApprovalSave(id,seltype,user);
	}
	public Map<String,Object> getCurrentPermit(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getCurrentPermit(id,user);
	}
	//获取标签信息
	public List<String> getTagsName(HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getTagsName(user);
	}
	//获取文件标签信息
	public List<String> getFileTagsName(String filename, HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getFileTagsName(filename, user);
	}
	public boolean modifySignRead(Long id,String type,String comment,String readerId,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().modifySignRead(id,type,comment,user,readerId);
	}
	//Boolean sendchecked,Boolean checkread,
	public boolean backSendSign(Long id,String submittype,String comment,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().backSendSign(id, submittype,comment, user);
	}
	public boolean modifySignSend(Long id,ArrayList<Long> signids,Boolean issame,Boolean isreturn,String readids
			,String comment,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		//sendchecked, checkread, 
		return SignUtil.instance().modifySignSend(id, signids, issame, isreturn, readids, comment, user);
	}
	public boolean reSendSign(Long id,ArrayList<Long> personIdList,Boolean issame,Boolean isreturn,String readids,String backsignFieldid
			,String comment,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().reSendSign(id, personIdList, issame, isreturn, readids,backsignFieldid, comment, user);
	}
	public String delSignInfo(List<Long> ids,String deltype,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().delSignInfo(ids,deltype,user);
	}
	public boolean signSuccess(List<Long> ids,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		SignUtil.instance().signSuccess(ids,user);
		return true;
	}
	public boolean endSignInfo(List<Long> ids,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		SignUtil.instance().endSignInfo(ids,user);
		return true;
	}
	public Map<String,Object> getNewHistory(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getNewHistory(user.getId(), id);
	}
	public String undoSign(Long id,String seltype,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().undoSign(id,seltype,user);
	}
	public String getWebcontent(Long id,String type,HttpServletRequest request)
	{
		if ("draft".equals(type))
		{
			return SignUtil.instance().getWebcontent(id,1);
		}
		else
		{
			return SignUtil.instance().getWebcontent(id,0);
		}
	}
	public List getWaitDetail(Long id,Integer type,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getWaitDetail(id,type,user);
	}
	public boolean setWarnMessage(String id,String comment,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
//		if (!id.startsWith("s"))
//		{
//			id="s"+id;
//		}
		return SignUtil.instance().setWarnMessage(id,comment,user,1);
	}
	public boolean delMessage(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().delMessage(id,user);
	}
	public boolean delAllMessage(HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().delAllMessage(user);
	}
	public Long getNewMessageNums(HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getNewMessageNums(user);
	}
	
	public List getNewListMessages(HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().getNewListMessages(user);
	}
	public List getHistoryFiles(Long historyid,Integer type,HttpServletRequest request)
	{//根据历史编号获取相应的文件列表
		//暂时先不判断是否有权限
		if (type==null || type==0)
		{
			return SignUtil.instance().getHistoryFiles(historyid);
		}
		else if (type==1)
		{
			return TransUtil.instance().getHistoryFiles(historyid);
		}
		else if (type==2)
		{
			return MeetUtil.instance().getHistoryFiles(historyid);
		}
		else
		{
			return SignUtil.instance().getHistoryFiles(historyid);//默认是获取移动签批的附件
		}
	}
	public String copyHistoryFiles(String targetPath,String[][] filepaths,HttpServletRequest request)
	{//复制历史版本到指定的目录
		//判断目标文件夹有没有权限
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().copyHistoryFiles(targetPath,filepaths,user);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param id 草稿ID
	 * @param title 标题
	 * @param content  内容
	 * @param filePaths  附件地址
	 * @param fileNames  附件文件名，用;间隔的
	 * @param personIds  办理者ID
	 * @param personNames  办理者名称,用;间隔的
	 * @param comment   备注
	 * @param request
	 * @return
	 */
	public boolean transCommit(Long id,String title,String content,
			ArrayList<String> filePaths,ArrayList<String> fileNames,ArrayList<Long> personIds
			,String personNames,String comment
			,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		
		return TransUtil.instance().transCommit(id,title,content,filePaths
				, fileNames,personIds,personNames,comment,user);
	}
	/**
	 * 保存交办事务
	 * @param id 草稿id
	 * @param title  主题
	 * @param content  内容
	 * @param filePaths  附件路径
	 * @param fileNames  附件名称
	 * @param personIds  处理人编号
	 * @param personNames  处理人名称，多人用;间隔
	 * @param comment  备注说明
	 * @param request
	 * @return
	 */
	public boolean transSave(Long id,String title,String content,
			ArrayList<String> filePaths,ArrayList<String> fileNames,ArrayList<Long> personIds
			,String personNames,String comment
			,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		
		return TransUtil.instance().transSave(id,title,content,filePaths
				, fileNames,personIds,personNames,comment,user);
	}
	public boolean transDelete(Long id,String type
			,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if ("draft".equals(type))
		{
			return TransUtil.instance().transDelete(id,1,user);
		}
		else if ("myquest".equals(type))
		{
			return TransUtil.instance().transDelete(id,2,user);
		}
		else
		{
			return TransUtil.instance().transDelete(id,0,user);
		}
		
	}
	/**
	 * 获取草稿
	 * @param id
	 * @param request
	 * @return
	 */
	public TransSave getTransSave(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return TransUtil.instance().getTransSave(id,user);
	}
	/**
	 * 获取事务内容
	 * @param id
	 * @param type
	 * @param request
	 * @return
	 */
	public String getTransWebcontent(Long id,String type,HttpServletRequest request)
	{
		if ("draft".equals(type))
		{
			return TransUtil.instance().getWebcontent(id,1);
		}
		else
		{
			return TransUtil.instance().getWebcontent(id,0);
		}
	}
	/**
	 * 对话框中获取事务具体信息
	 * @param id
	 * @param request
	 * @return
	 */
	public TransInfo getTransPermit(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return TransUtil.instance().getTransPermit(id,user);
	}
	/**
	 * 事务处理
	 * @param id
	 * @param filePaths
	 * @param fileNames
	 * @param comment
	 * @param handle
	 * @param request
	 * @return
	 */
	public boolean transModify(Long id,String comment,ArrayList<String> filePaths,ArrayList<String> fileNames
			,Boolean handle
			,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);

		return TransUtil.instance().transModify(id,comment,filePaths,fileNames
				,handle,user);
	}
	public Map<String,Object> getTransHistosy(Long id,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return TransUtil.instance().getTransHistosy(user.getId(), id);
	}
	public boolean transsignReal(String id,HttpServletRequest request){
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return TransUtil.instance().transsignreal(id, user);
	}
	public boolean getTransSignReal(String id,HttpServletRequest request){
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return TransUtil.instance().getTransSignReal(id, user);
	}
	
	
	
	public boolean meetSave(Long id,String meetname,Date meetdate,String meettime
			,String meetaddress,String mastername,String[][] meetmannames,String[][] othermannames
			,String meetcontent,String[][] filePaths
			,String comment
			,HttpServletRequest request)
	{//保存草稿
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().meetSave(id, meetname, meetdate, meettime, meetaddress, mastername
				, meetmannames, othermannames, meetcontent, filePaths, comment, user);
	}
	public MeetSave getMeetSave(Long id,HttpServletRequest request)
	{//获取草稿
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().getMeetSave(id, user);
	}
	public boolean meetCommit(Long id,String meetname,Date meetdate,String meettime
			,String meetaddress,String mastername,String[][] meetmannames,String[][] othermannames
			,String meetcontent,String[][] filePaths
			,String comment
			,HttpServletRequest request)
	{//提交会议通知
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		
		return MeetUtil.instance().meetCommit(id, meetname, meetdate, meettime, meetaddress,
				mastername, meetmannames, othermannames, meetcontent, filePaths, comment, user);
	}
	public boolean meetModify(Long id,Long actionid,String comment,Long replaceuserid
			,String othername,String otherunit,String otherphone,HttpServletRequest request)
	{//处理会议回执
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().meetModify(id, actionid, comment, replaceuserid, othername, otherunit, otherphone, user,true);
	}

	public boolean meetAdminChange(Long id,String[][] data,HttpServletRequest request)
	{//外部人员会议信息更改
		//Long sameid,Integer inout
		//,String othername,String otherstatus,String comment
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().meetAdminChange(id, data, user);
	}
	
	public boolean meetDelete(Long id,String type,HttpServletRequest request)
	{//处理会议回执
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if ("draft".equals(type))
		{
			return MeetUtil.instance().meetDelete( id,1,user);
		}
		else if ("myquest".equals(type))
		{
			return MeetUtil.instance().meetDelete( id,2,user);
		}
		else
		{
			return MeetUtil.instance().meetDelete( id,0,user);
		}
		
	}
	public String getMeetWebcontent(Long id,String type,HttpServletRequest request)
	{//获取会议议题
		if ("draft".equals(type))
		{
			return MeetUtil.instance().getWebcontent(id,1);
		}
		else
		{
			return MeetUtil.instance().getWebcontent(id,0);
		}
	}
	public List<String[]> getMeetBackDetail(Long meetid,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().getMeetBackDetail(user.getId(), meetid);
	}
	public List<String[]> getMeetBackDetailCB(Long meetid,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().getMeetBackDetailCB(user.getId(), meetid);
	}
	public MeetInfo getMeetPermit(Long meetid,HttpServletRequest request)
	{
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().getMeetPermit(meetid,user);
	}
	public boolean setMeetWarnMessage(Long sameid,HttpServletRequest request)
	{//会议催办
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().setMeetWarnMessage(sameid,user,1);
	}
	public boolean getMeetSignReal(String meetid,HttpServletRequest request)
	{//获取是否会议签收
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().getMeetSignReal(meetid,user);
	}
	public boolean meetsignreal(String meetid,HttpServletRequest request)
	{//会议签收
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return MeetUtil.instance().meetsignreal(meetid,user);
	}
	//上传或选择的文件转换为html文件
	public String smsCopyFile(String filepath,HttpServletRequest request)
	{
		try
		{
			Users user=(Users)request.getSession().getAttribute("userKey");
			String spaceid=user.getSpaceUID();
			String filename=user.getId()+"_"+System.currentTimeMillis();
			String oldfilename=filename+filepath.substring(filepath.lastIndexOf("."));//原始文件名
			String srcfile=WebConfig.srcSmsPath+File.separator+oldfilename;//原文件和转换后的文件放到同一个目录
			String tarfile=WebConfig.srcSmsPath+File.separator+filename+".html";//目标文件
			 if (spaceid==null){spaceid="";}
			 if ((filepath.startsWith("user_") || filepath.startsWith("group_")
				|| filepath.startsWith("team_")
				|| filepath.startsWith("org_")
				|| filepath.startsWith("system_audit_root")
				|| filepath.startsWith("company_")
				|| filepath.startsWith(spaceid)
				)
				&& (filepath.indexOf("/") > 0))// 文档库文档//其实前台传一个参数，就不要这个繁琐的判断
			{
				//文件库中的文件
				JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
				InputStream in=jcrs.getContent(filepath);
				File sfile=new File(srcfile);
				if (!sfile.exists())
				{
					sfile.createNewFile();
				}
				OutputStream oos = new FileOutputStream(sfile);
				byte[] buff = new byte[4096];
				int readed;
				while ((readed = in.read(buff)) > 0)
				{
					oos.write(buff, 0, readed);
				}
				oos.flush();
				oos.close();
				in.close();
			}
			else// 本地已上传的文档放到文档库中
			{
				try
				{
					 String tempPath = WebConfig.tempFilePath + File.separatorChar;
					 String fname=fileNameReplace(filepath);
					 File file = new File(tempPath + fname);
					 InputStream fin = new FileInputStream(file);
					 OutputStream oos = new FileOutputStream(srcfile);
					byte[] buff = new byte[4096];
					int readed;
					while ((readed = fin.read(buff)) > 0)
					{
						oos.write(buff, 0, readed);
					}
					oos.flush();
					oos.close();
					fin.close();
				     file.delete();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			String lowsrcfile=srcfile.toLowerCase();
			if (lowsrcfile.endsWith("doc")||lowsrcfile.endsWith("docx")
				||lowsrcfile.endsWith("xls")||lowsrcfile.endsWith("xlsx")
				||lowsrcfile.endsWith("ppt")||lowsrcfile.endsWith("pptx")
			)//只有OFFICE文件才进行转换
			{
				ConvertForRead.convertMStoHtml(srcfile, tarfile);//转换文件
				String redirectpath = "http://"+WebConfig.serverurlname+"/"+WebConfig.SRCSMS_FOLDER+"/"+filename+".html";
				return redirectpath;
			}
			else
			{
				String redirectpath = "http://"+WebConfig.serverurlname+"/"+WebConfig.SRCSMS_FOLDER+"/"+oldfilename;
				return redirectpath;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	private String fileNameReplace(String filename)
    {
    	if (filename!=null)
    	{
	    	int index=filename.indexOf("&amp;");
	    	if (index>=0)
	    	{
	    		filename=filename.replaceAll("&amp;", "&");
	    	}
    	}
    	return filename;
    }
	/**
	 * 增加或删除来文单位
	 * @param fromunit 来文单位名称
	 * @param type 为del就是删除，否则为增加
	 * @param request
	 * @return
	 */
	public List<String[]> addFromunit(String fromunit,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFromunit(fromunit, "add", user);
	}
	public List<String[]> delFromunit(String fromunit,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFromunit(fromunit, "del", user);
	}
	public List<String[]> getFromunit(HttpServletRequest request)
	{//获取来文单位
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFromunit(null, null, user);
	}
	public Map<String, Object> getInitDetails(HttpServletRequest request)
	{//获取来文单位
		Map<String, Object> map=new HashMap<String, Object>();
		Users user=(Users)request.getSession().getAttribute("userKey");
		map.put("fromunit", SignUtil.instance().modifyFromunit(null, null, user));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		map.put("years", "["+sdf.format(new Date())+"]");
		map.put("fileflowid",SignUtil.instance().getFileflowid());
		return map;
	}
	/**
	 * 增加或删除文件类型
	 * @param fromunit 来文单位名称
	 * @param type 为del就是删除，否则为增加
	 * @param request
	 * @return
	 */
	public List<String[]> addFiletype(String filetype,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFiletype(filetype, "add", user);
	}
	public List<String[]> delFiletype(String filetype,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFiletype(filetype, "del", user);
	}
	public List<String[]> getFiletype(HttpServletRequest request)
	{//获取来文单位
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyFiletype(null, null, user);
	}
	/**
	 * 增加或删除处理备注
	 * @param fromunit 来文单位名称
	 * @param type 为del就是删除，否则为增加
	 * @param request
	 * @return
	 */
	public List<String[]> addModifyscript(String script,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyScript(script, "add", user);
	}
	public List<String[]> delModifyscript(String script,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyScript(script, "del", user);
	}
	public List<String[]> getModifyscript(HttpServletRequest request)
	{//获取处理备注列表
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().modifyScript(null, null, user);
	}
	public Map<String, Object> getInitModify(Long appid,HttpServletRequest request)
	{//获取来文单位
		Map<String, Object> map=new HashMap<String, Object>();
		Users user=(Users)request.getSession().getAttribute("userKey");
		map.put("modifycomment", SignUtil.instance().modifyScript(null, null, user));
		ApprovalDefaulter ap = SignUtil.instance().getModifyOther(appid,user);
		if (ap!=null)
		{
			map.put("nextselect", ap.getSelecttype());//临时写死
			map.put("issame", ap.getIssame());//会签，写死
			map.put("comment", ap.getComment());
		}
		else
		{
			map.put("nextselect", false);//临时写死
			map.put("issame", false);//会签，写死
			map.put("comment", "");
		}
		map.put("modifiers",SignUtil.instance().getModifyDefault(appid,user));
		return map;
	}
	public Map<String, Object> getInitdata(HttpServletRequest request)
	{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("filetypeCombo", getFiletype(request));
		map.put("commentSelect", getModifyscript(request));
		map.put("personData", getSendDefault(request));
		ApprovalDefaulter ad = getSendOther(request);
		if (ad!=null)
		{
			map.put("sendcomment", ad.getComment());
		}
		return map;
	}
	public Map<String, Object> getFiledetail(Long approvalid,HttpServletRequest request)
	{
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().getFiledetail(approvalid, user);
	}
	public long getFileflowid(HttpServletRequest request)
	{
		return SignUtil.instance().getFileflowid();
	}
	public boolean setSendDefault(ArrayList<Long> accepters,String comment,HttpServletRequest request)
	{//设置送文时的默认接收者
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().setSendDefault(accepters,comment,user);
	}
	public List<String[]> getSendDefault(HttpServletRequest request)
	{//获取送文时的默认接收者
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().getSendDefault(user);
	}
	public ApprovalDefaulter getSendOther(HttpServletRequest request)
	{//获取送文时的默认接收者
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().getSendOther(user);
	}
	public boolean setModifyDefault(Long id,ArrayList<Long> accepters,Boolean nextchecked,String comment,Boolean huiqian,HttpServletRequest request)
	{//设置处理时的默认接收者
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().setModifyDefault(id,accepters,nextchecked,comment,huiqian,user);
	}
	public List<String[]> getModifyDefault(Long approvalid,HttpServletRequest request)
	{//获取处理时的默认接收者
		Users user=(Users)request.getSession().getAttribute("userKey");
		return SignUtil.instance().getModifyDefault(approvalid,user);
	}
	public boolean collectSign(Long id,String seltype,HttpServletRequest request)
	{//收藏签批
		Users user = (Users) request.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		return SignUtil.instance().collectSign(id,seltype,user);
	}
	
	
	
	/**
     * 采编
     */
    public boolean doCB(String[] paths, String[] shows, HttpServletRequest request)
    {
    	Users user=(Users)request.getSession().getAttribute("userKey");
    	Long userID=user.getId();
    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	return service.doCB(paths, shows, userID);
    }
    /**
     * 取消报送
     */
    public boolean doBSC(final String[] paths, final String[] shows, HttpServletRequest request)
    {
    	Users user=(Users)request.getSession().getAttribute("userKey");
    	Long userID=user.getId();
    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	return service.doBSC(paths, shows, userID);
    }
    /**
     * 报送
     */
    public boolean doBS(String[] paths, String[] shows, HttpServletRequest request)
    {
    	Users user=(Users)request.getSession().getAttribute("userKey");
    	Long userID=user.getId();
    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	return service.doBS(paths, shows, userID);
    }
    
    /**
     * 得报送采编列表
     * @param userID
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @return
     */
    public List getBSCB(final Long userID, int start, int limit, String sort, String dir)
    {
    	FileSystemService service = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
    	return service.getBSCB(userID, start, limit, sort, dir);	        
    }
}
