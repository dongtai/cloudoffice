package apps.transmanager.weboffice.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.databaseobject.Bulletins;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.service.IPMicroblogService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.DateUtils;
import apps.transmanager.weboffice.util.GridUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;

public class ProjectMicroblogDwr {
	
	private IPMicroblogService pmicroblogService;
	private FileSystemService fileSystemService;

	public void setPmicroblogService(IPMicroblogService pmicroblogService) {
		this.pmicroblogService = pmicroblogService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	/**
	 * 获得项目组和最新的一条微博
	 * @param req 请求信息
	 * @return 项目组和最新的一条微博
	 */
	public List<PMicroblogMegPo> getProAndFirstBlog(HttpServletRequest req)
	{
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		List<PMicroblogMegPo> groupAndMegList = pmicroblogService.getGroupsAndNewMeg(user.getId());
		
		return groupAndMegList;
	}
	
	/**
	 * 获得项目组信息,和成员信息(组ID为null则默认最近参与的项目组)
	 * @param groupId 项目组ID
	 * @param req
	 * @return 
	 */
	public Map<String,Object> getProjectInfo(Long groupId,HttpServletRequest req){
		try{
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(groupId==null)
		{
			Map<String,Object> projectMap = pmicroblogService.getLastestGroupAndMember(user.getId());
			return projectMap;
		}
		Map<String,Object> projectMap = pmicroblogService.getGroupAndMember(groupId);
		return projectMap;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获得公告，前10条
	 * @param spaceUID 空间ID
	 * @param start 起始位置
	 * @param count 所取条数
	 * @return 公告
	 */
	public List<Bulletins> getNotices(String spaceUID,int start,int count)
	{
		List<Bulletins> bulletins = fileSystemService.getSpaceBulletins(spaceUID, start, count);
		return bulletins;
	}
	
	/**
	 * 获得当前用户参与的项目组
	 * @param req 请求信息
	 * @return 用户参与的项目组集合
	 */
	public List<Groups> getProjectList(HttpServletRequest req)
	{
		Users user = (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		List<Groups> groupList = pmicroblogService.getGroupList(user.getId());
		return groupList;
	}
	
	
	/**
	 * 获得项目微博集合和分页信息
	 * @param groupId 项目组ID
	 * @param userId 用户ID
	 * @param goPage 跳转的页面
	 * @param pageSize 每页显示多少条
	 * @return 微博集合
	 */
	public Map<String,Object> getPMBlogMegMap(Long groupId,Long userId,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<PMicroblogMegPo> pmblogList = null;
		try{	
			pmblogList = pmicroblogService.getGroupBlog(groupId,userId,page);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		
	}
	
	/**
	 * 获得微博的回复
	 * @param parentId 父节点ID
	 * @return 回复的集合
	 */
	public Map<String,Object> getBlogBackList(Long parentId)
	{
		List<PMicroblogMegPo> pmblogList = pmicroblogService.getBlogBack(parentId);
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	
	/**
	 * 搜索微博
	 * @param groupId 项目组ID
	 * @param key 搜索关键字
	 * @param goPage 跳转的页面
	 * @return 搜索到的微博
	 */
	public Map<String,Object> searchBlog(Long groupId,String key,Integer goPage,Integer pageSize)
	{
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<PMicroblogMegPo> pmblogList = pmicroblogService.searchBlog(groupId,key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	
	
	/**
	 * 发送微博（分为留言和回复）
	 * @param group 项目组
	 * @param parentId 父节点ID
	 * @param meg 微博信息
	 * @param req 请求信息
	 */
	public void sendBolg(Groups group,Long parentId,String meg,String dateS,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		PMicroblogMegPo pmblogMeg = new PMicroblogMegPo();
		try{
		pmblogMeg.setAddDate(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
		pmblogMeg.setGroups(group);
		pmblogMeg.setMeg(meg);
		pmblogMeg.setSendUser(user);
		if(parentId!=null)
		{
			PMicroblogMegPo parent = new PMicroblogMegPo();
			parent.setId(parentId);
			pmblogMeg.setParent(parent);
			pmicroblogService.add(pmblogMeg);
			return;
		}
		pmblogMeg.setParent(null);
		pmicroblogService.add(pmblogMeg);
		//发送通知,仅对项目组成员和在线用户推送
		List<Users> memberList = pmicroblogService.getMemberList(group.getId());
		final List<Long> targetIdList = new ArrayList<Long>();
		for(Users member : memberList)
		{
			if(member.getId().longValue()!=user.getId().longValue())
			{
				targetIdList.add(member.getId());
			}
		}
		
		final Map<String,Object> data = new HashMap<String, Object>();
		data.put("group",group);
		data.put("memberIdList", targetIdList);
		data.put("pmblogMeg", pmblogMeg);
		
		MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
		
		messageService.sendMessage("MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG, 1, data, null);
		
		/*
		Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
		public boolean match(ScriptSession session) {
			Long loginUserId = (Long) session.getAttribute(Constant.LG_USER_ID);
			if(loginUserId!=null && targetIdList.contains(loginUserId))
			{
				return true;
			}
			return false;
			}
		}, new Runnable() {
			
			public void run() {
				
				ScriptSessions.addFunctionCall("MegHandler.showMeg", Constant.MSG_TYPE_BLOG,1,data,null);
				//ScriptSessions.addFunctionCall("Tui.showNew", data);
			}
		});*/
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 删除自己的一条微博
	 * @param blogId 微博ID
	 * @param req 请求信息
	 */
	public void delMyBlog(Long blogId,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		pmicroblogService.del(blogId,user);
	}

}
