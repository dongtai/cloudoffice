package apps.transmanager.weboffice.dwr;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.domain.FriendshipPo;
import apps.transmanager.weboffice.domain.GroupShipPo;
import apps.transmanager.weboffice.domain.MicroGroupPo;
import apps.transmanager.weboffice.domain.MyFavoritePo;
import apps.transmanager.weboffice.domain.PrivateLetterPo;
import apps.transmanager.weboffice.service.IBlogContentService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.DateUtils;
import apps.transmanager.weboffice.util.GridUtil;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;
/**
 * 与前台交互
 * @author 胡晓燕
 *
 */
public class BlogContentDwr {
	private IBlogContentService blogContentService;

	public void setBlogContentService(IBlogContentService blogContentService) {
		this.blogContentService = blogContentService;
	}

	/**
	 * 发送微博(一般微博)
	 * @param meg 微博信息
	 * @param req 请求信息
	 */
	public void sendBolg(String meg,String dateS,HttpServletRequest req)
	{
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		BlogContentPo blog = new BlogContentPo();
		
		try{
			blog.setPost_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			blog.setBlog_body(meg);
			blog.setSendUser(user);
			blog.setFiletype(1);
			blogContentService.add(blog);
			//发送通知,仅其在线粉丝推送
			List<Users> memberList = blogContentService.getFanMemberList(user.getId());
			if(memberList!=null){
			final List<Long> targetIdList = new ArrayList<Long>();
			for(int i=0;i<memberList.size();i++)
			{
					targetIdList.add(memberList.get(i).getId());
				
			}
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			data.put("pmblogMeg", blog);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG, 1, data, null);
			}
			//若微博中@了用户，对其进行推送
			sendAt(blog);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 发送微博评论
	 * @param blogid 所评论的微博号
	 * @param meg 微博信息
	 * @param req 请求信息
	 */
	public void sendBolgReply(Long blogid,String meg,String dateS,HttpServletRequest req)
	{
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		BlogContentPo blog = new BlogContentPo();
		BlogContentPo replyblog = blogContentService.getReplyBlog(blogid);
		try{
			blog.setPost_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			blog.setBlog_body(meg);
			blog.setSendUser(user);
			blog.setReplyblog(replyblog);
			blog.setFiletype(2);
			blogContentService.add(blog);
			//若评论中@了用户，对其进行推送
			sendAt(blog);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 发送转发的微博
	 * @param zf_id 所转发的微博号
	 * @param meg 微博信息
	 * @param req 请求信息
	 */
	public void sendBolgZf(Long zf_id,String meg,String dateS,HttpServletRequest req)
	{
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		BlogContentPo blog = new BlogContentPo();
		
		try{
			BlogContentPo zfblog = blogContentService.getZfBlog(zf_id);
			blog.setPost_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			blog.setBlog_body(meg);
			blog.setSendUser(user);
			blog.setZfblog(zfblog);
			blog.setFiletype(3);
			blogContentService.add(blog);
			//发送通知,仅对其在线粉丝推送
			List<Users> memberList = blogContentService.getFanMemberList(user.getId());
			if(memberList!=null){
			final List<Long> targetIdList = new ArrayList<Long>();
			for(int i=0;i<memberList.size();i++)
			{
				
					targetIdList.add(memberList.get(i).getId());
				
			}
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			data.put("pmblogMeg", blog);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG, 1, data, null);
			}
			//若微博中@了用户，对其进行推送
			sendAt(blog);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 获得一般微博的所有微博集合和分页信息
	 * @param userId 用户ID
	 * @param goPage 跳转的页面
	 * @param pageSize 每页显示多少条
	 * @return 微博集合
	 */
	public Map<String,Object> getPMBlogMegMap(Long userId,Integer goPage,Integer pageSize){
		
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = null;
		try{	
			pmblogList = blogContentService.getAllBlog(userId,page);
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
	 * 删除一条微博
	 * @param blogId 微博号
	 */
	public void delBlog(Long blogId,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delBolg(blogId,user);
	}
	
	/**
	 * 获得项目微博评论集合和分页信息
	 * @param blogId 微博号
	 * @return 微博评论集合
	 */
	public Map<String,Object> getBlogReplyList(Long blogid){
		List<BlogContentPo> pmblogList = blogContentService.getBlogReply(blogid);
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		
		
	}
	/**
	 * 删除一条微博评论
	 * @param blogId 微博号
	 * @param req 请求信息
	 */

	public void delBlogBack(Long blogId,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delBolgBack(blogId,user);
	}
	
	/**
	 * 获得项目微博提及@集合和分页信息
	 * @param userId 用户ID
	 * @param goPage 跳转的页面
	 * @param pageSize 每页显示多少条
	 * @return 微博提及@集合
	 */
	public Map<String,Object> getAtPMBlogMegMap(Long userId,Integer goPage,Integer pageSize){
		
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = null;
		try{	
			pmblogList = blogContentService.getAtBlog(userId,page);
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
	 * 删除上传的图片
	 * @param URLPath 图片的服务器路径
	 */
	public void deletePicture(String URLPath){
		String foldername = URLPath.substring(URLPath.indexOf("WeiboUpload"));
		String filename = foldername.substring(foldername.indexOf('/')+1);
		String absolutepath = WebConfig.webContextPath+"cloud\\weibo\\WeiboUpload"+"\\"+ filename;
		File file = new File(absolutepath);  
		file.delete();
	}
	
	/**
	 * 提及的用户推送
	 * 包括转发的微博所涉及的用户
	 */
	public void sendAt(BlogContentPo blog){
		List<String> membernameList = new ArrayList<String>();
		if(blog.getBlog_body().indexOf('@')!=-1){
			String array1[] = blog.getBlog_body().split("@");
			for(int i = 0;i<array1.length;i++)
			{   
				String atuser_name=null;
				if(array1[i].indexOf(' ')!=-1){
					atuser_name = array1[i].substring(0,array1[i].indexOf(' '));
					if(!(atuser_name.equals(null))&&!(atuser_name.equals(" "))){
						if(!(membernameList.contains(atuser_name)))membernameList.add(atuser_name);
					}
						}
				if(array1[i].indexOf(':')!=-1){
					atuser_name = array1[i].substring(0,array1[i].indexOf(':'));
					if(!(atuser_name.equals(null))&&!(atuser_name.equals(" "))){
						if(!(membernameList.contains(atuser_name)))membernameList.add(atuser_name);
					}
					}
				
			}
			}
		if(blog.getFiletype()==3)
		{
			
			BlogContentPo zfblog = blog.getZfblog();
			if(zfblog.getBlog_body().indexOf('@')!=-1){
				String array1[] = zfblog.getBlog_body().split("@");
				for(int i = 0;i<array1.length;i++)
				{   
					String atuser_name=null;
					if(array1[i].indexOf(' ')!=-1){
						atuser_name = array1[i].substring(0,array1[i].indexOf(' '));
					    if(!(atuser_name.equals(null))&&!(atuser_name.equals(" "))){
					    	if(!(membernameList.contains(atuser_name)))membernameList.add(atuser_name);
					}
					}
				}
				}
			if(!(membernameList.contains(zfblog.getSendUser().getUserName())))membernameList.add(zfblog.getSendUser().getUserName());

			
		}
		//发送通知,仅对@用户推送
		if(membernameList!=null){
		final List<Long> targetIdList = new ArrayList<Long>();
		for(int i=0;i<membernameList.size();i++)
		{
			    Users user = blogContentService.getUser(membernameList.get(i));
			    if(user!=null){
			    updateUserNewInfo(user.getId(),1,1);
				targetIdList.add(user.getId());
			    }
			
		}
		final Map<String,Object> data = new HashMap<String, Object>();
		data.put("memberIdList", targetIdList);
		data.put("pmblogMeg", blog);
		MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
		messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG_AT, 1, data, null);
		//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG_AT, 1, data, null);
		}
	}
	
    /**
     * 发送私信
     * @param username 发送对象的用户名
     * @param letter 私信内容
     * @param dateS 发送时间
     * @param req 请求信息
     */
	public void sendPriLetter(String username,String letter,String dateS,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		PrivateLetterPo priletter = new PrivateLetterPo();
		Users sendtoUser = blogContentService.getUser(username);
		try{
			priletter.setSendtime(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			priletter.setMessagebody(letter);
			priletter.setSendmegto_user(sendtoUser);
			priletter.setSendmeg_user(user);
			priletter.setSend_del(false);
			priletter.setSend_to_del(false);
			priletter.setIsread(false);
			blogContentService.addPriLetter(priletter);
			if(sendtoUser!=null){
			updateUserNewInfo(sendtoUser.getId(),2,1);	
			final List<Long> targetIdList = new ArrayList<Long>();
			//发送通知,仅对收件人推送
			if(sendtoUser.getId()!=null){
			targetIdList.add(sendtoUser.getId());
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			data.put("pmblogMeg", priletter);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG_PRILETTER, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG_PRILETTER, 1, data, null);
			}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 得到私信收件箱和分页信息
	 * @param userId 用户id
	 * @param goPage 跳转的页号
	 * @param pageSize 分页大小
	 * @return 收件信息
	 */
	public Map<String,Object> getReceivedLetterMap(Long userId,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<PrivateLetterPo> pletterList = null;
		try{	
			pletterList = blogContentService.getReceivedLetter(userId,page);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(pletterList==null || pletterList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pletterList", pletterList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		}

	/**
	 * 得到私信发件箱
	 * @param userId 用户id
	 * @param goPage 跳转的页号
	 * @param pageSize 分页大小
	 * @return 发件信息
	 */
	public Map<String,Object> getSendLetterMap(Long userId,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<PrivateLetterPo> pletterList = null;
		try{	
			pletterList = blogContentService.getSendLetter(userId,page);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(pletterList==null || pletterList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pletterList", pletterList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		}
 /**
  * 删除一条私信
  * @param priletter_id 私信号
  * @param req 请求信息
  */
	public void deleteletter(Long priletter_id,HttpServletRequest req){
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delPriLetter(priletter_id,user);
	}
/**
 * 搜索微博（整体搜索全部微博）
 * @param key 关键字
 * @param goPage 分页跳转页号
 * @param pageSize 分页大小
 * @return 搜索到的微博信息
 */
	public Map<String,Object> searchBlog(String key,Integer goPage,Integer pageSize)
	{
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = blogContentService.searchBlog(key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
/**
 * 收藏微博
 * @param blogid 收藏的微博号
 * @param dateS 收藏的时间
 * @param req 请求信息
 */
	public void sendBolgfavor(Long blogid,String dateS,HttpServletRequest req){
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		
		MyFavoritePo myfavor = new MyFavoritePo();
		BlogContentPo blog = blogContentService.getfavorBlog(blogid);
		try{
			myfavor.setFavor_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			myfavor.setFavorUser(user);
			myfavor.setFavorblog(blog);
			blogContentService.addFavorblog(myfavor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/**
 * 得到收藏的全部微博
 * @param userId 用户id
 * @param goPage 分页跳转的页号
 * @param pageSize 分页大小
 * @return 收藏的微博信息
 */
	public Map<String,Object> getFavorPMBlogMegMap(Long userId,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = null;
		try{	
			pmblogList = blogContentService.getFavorBlogList(userId,page);
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
	 * 判断是否为用户收藏的微博
	 * @param blogid 微博Id
	 * @param userid 用户Id
	 * @return 是收藏的微博：返回1；不是收藏的微博：返回0
	 */
	public int ismyfavor(Long blogid,Long userid)
	{
		boolean flag = false;
		flag = blogContentService.judgeFavor(blogid,userid);
		if(flag)return 1;
		else return 0;
	}
	/**
	 * 取消微博的收藏
	 * @param blogid 微博Id
	 * @param req 请求信息
	 */
	public void delFavorBlog(Long blogid,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delFavorBlog(blogid,user);
	}
/**
 * 得到对我的微博的评论
 * @param userId 用户Id
 * @param goPage 分页跳转的页号
 * @param pageSize 分页大小
 * @return 对我的微博的评论信息
 */
	public Map<String,Object> getReceivedCommentMap(Long userId,Integer goPage,Integer pageSize){
		
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> comments = null;
		try{	
			comments = blogContentService.getMyBlogReply(userId,page);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(comments==null || comments.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("comments", comments);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		
		return pmblogMap;
	}
	/**
	 * 我发出的微博的评论
	 * @param userId 用户id
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页大小
	 * @return 我发出的微博的评论信息
	 */
	public Map<String,Object> getSendCommentMap(Long userId,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> comments = null;
		try{	
			comments = blogContentService.getBlogMyReply(userId,page);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(comments==null || comments.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("comments", comments);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	/**
	 * 搜索用户及我本身与用户的关系，总体搜索
	 * @param userid 用户id
	 * @param key 搜索关键字
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页大小
	 * @return 用户及与本用户的关系信息
	 */
	public Map<String,Object> searchUser(Long userid,String key,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<Users> userList = blogContentService.searchUser(key,page);
		List<String> flagList =blogContentService.getfriendshipFlag(userid, key, page);
		if(userList==null || userList.isEmpty())return null;
		else{
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("userList", userList);
		pmblogMap.put("flagList", flagList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		}
	}
	/**
	 * 关注用户
	 * @param follow_userid 所关注的用户id
	 * @param req 请求信息
	 */
	public void makefollow(Long follow_userid,HttpServletRequest req){
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		
		Users followuser = blogContentService.getUserById(follow_userid);
		FriendshipPo friendship = new FriendshipPo();
		try{
			friendship.setFan_user(user);
			friendship.setFollow_user(followuser);
			blogContentService.addFriendship(friendship);
			if(followuser!=null){
			updateUserNewInfo(followuser.getId(),3,1);	
			//对关注者发送信息
			final List<Long> targetIdList = new ArrayList<Long>();
			if(followuser!=null){
			targetIdList.add(followuser.getId());
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_Fan, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_Fan, 1, data, null);
			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 取消关注
	 * @param follow_userid 所关注的用户id
	 * @param req 请求信息
	 */
	public void deletefollow(Long follow_userid,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delFollow(follow_userid,user.getId());
	}
	/**
	 * 移除粉丝
	 * @param fan_id 粉丝id
	 * @param req 请求信息
	 */
	public void deletefan(Long fan_id,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		blogContentService.delFollow(user.getId(),fan_id);
	}
	
	/**
	 * 初始化用户基本信息
	 * @param userid 用户id
	 * @return 用户的真实姓名，关注数，粉丝数，微博数的集合
	 */
	public Map<String,Object> initUserInfo(Long userid){
		List<Object> info = blogContentService.getUserInfo(userid);
		String realname = blogContentService.getUserById(userid).getRealName();
		int newmention = blogContentService.getUserById(userid).getNewmention();
		int newfan = blogContentService.getUserById(userid).getNewfan();
		int priread = blogContentService.getUserById(userid).getPriread();
		Map<String,Object> infoMap = new HashMap<String, Object>();
		infoMap.put("follow", info.get(0));
		infoMap.put("fan", info.get(1));
		infoMap.put("weibo", info.get(2));
		infoMap.put("realname", realname);
		infoMap.put("newmention", newmention);
		infoMap.put("newfan", newfan);
		infoMap.put("priread", priread);
		return infoMap;
	}
	/**
	 * 获得我关注的用户信息
	 * @param userid 用户id
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页的大小
	 * @return 关注的用户信息
	 */
	public Map<String,Object> getMyFollow(Long userid,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<Users> userList = blogContentService.getfollow(userid,page);
		List<String> flagList =blogContentService.getfriendshipFlag_follow(userid,page);
		if(userList==null || userList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("userList", userList);
		pmblogMap.put("flagList", flagList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	/**
	 * 获得我粉丝的用户信息
	 * @param userid 用户id
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页的大小
	 * @return 粉丝的用户信息
	 */
	public Map<String,Object> getMyFan(Long userid,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<Users> userList = blogContentService.getfan(userid,page);
		List<String> flagList =blogContentService.getfriendshipFlag_fan(userid,page);
		if(userList==null || userList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("userList", userList);
		pmblogMap.put("flagList", flagList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
		
	}
	/**
	 * 获得可供选择的全部用户信息（微群创建中）
	 * @param userid 用户id
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页大小
	 * @return
	 */
	public Map<String,Object> getMyFriend(Long userid,Integer goPage,Integer pageSize){
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<Users> userList = new ArrayList<Users>();
		userList = blogContentService.getfriendList(userid,page);
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("userList", userList);
		return pmblogMap;
		
	}
	/**
	 * 创建新的微群
	 * @param groupName 微群名
	 * @param groupMember 微群成员
	 * @param groupDes 微群描述
	 * @param dateS 创建时间
	 * @param req 请求信息
	 */
	public void CreateGroup(String groupName,String groupMember,String groupDes,String dateS,HttpServletRequest req){
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		MicroGroupPo group = new MicroGroupPo();
		try{
			group.setCreate_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			group.setGroup_manager(user);
			group.setGroup_name(groupName);
			group.setGroup_description(groupDes);
			blogContentService.addGroup(group);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		MicroGroupPo group1 = blogContentService.getGroupByName(groupName);
		String member[] = groupMember.split(",");
		List<String> usernamelist = new ArrayList<String>();
		for(int i = 0;i<member.length;i++){
			if(!(member[i].equals(null))&&!(member[i].equals(" "))){
				if(!(usernamelist.contains(member[i])))usernamelist.add(member[i]);
			}	
		}
		usernamelist.add(user.getUserName());
		if(usernamelist!=null){
		for(int i= 0;i<usernamelist.size();i++){
			Users group_user = blogContentService.getUser(usernamelist.get(i));
			GroupShipPo groupship = new GroupShipPo();
			try{
			groupship.setGroup(group1);
			groupship.setUser(group_user);
			blogContentService.addGroupShip(groupship);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		}
	}
	/**
	 * 获得微群列表
	 * @param userid 用户Id
	 * @return 该用户的微群列表
	 */
	public List<MicroGroupPo> getProjectList(Long userid){
		List<MicroGroupPo> grouplist =new  ArrayList<MicroGroupPo>();
		grouplist = blogContentService.getGroupList(userid);
		return grouplist;
	}
	/**
	 * 获得该微群的基本信息
	 * @param userid 用户id
	 * @param groupid 该群的群号
	 * @return 微群的基本信息
	 */
	public Map<String,Object> getProjectInfo(Long userid,Long groupid){
		MicroGroupPo group = blogContentService.getGroupById(groupid);
		List<Users> userList = new ArrayList<Users>();
		userList = blogContentService.getUserByGroupId(userid,groupid);
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("group", group);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		pmblogMap.put("memberList", userList);
		return pmblogMap;
	}
	/**
	 * 发送微群微博
	 * @param meg 微博信息
	 * @param groupid 发送至的微博的id
	 * @param dateS 发送时间
	 * @param req 请求信息
	 */
	public void sendGroupBolg(String meg,Long groupid,String dateS,HttpServletRequest req)
	{
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		BlogContentPo blog = new BlogContentPo();
		MicroGroupPo group = blogContentService.getGroupById(groupid);
		try{
			blog.setPost_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			blog.setBlog_body(meg);
			blog.setSendUser(user);
			blog.setFiletype(1);
			blog.setIsgroup(true);
			blog.setGroups(group);
			blogContentService.add(blog);
		
			//发送通知,仅对在线微群成员推送
			List<Users> memberList = blogContentService.getUserByGroupId(user.getId(),groupid);
			final List<Long> targetIdList = new ArrayList<Long>();
			for(int i=0;i<memberList.size();i++)
			{
					targetIdList.add(memberList.get(i).getId());
				
			}
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			data.put("pmblogMeg", blog);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG, 1, data, null);
			sendAt(blog);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
/**
 * 发送转发的微博（发送至微群）
 * @param groupName 微群名
 * @param zf_id 转发的微博的id
 * @param meg 转发的信息
 * @param dateS 转发的时间
 * @param req 请求信息
 */
	public void sendGroupBolgZf(String groupName,Long zf_id,String meg,String dateS,HttpServletRequest req)
	{
		
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		BlogContentPo blog = new BlogContentPo();
		MicroGroupPo group = blogContentService.getGroupByName(groupName);
		try{
			BlogContentPo zfblog = blogContentService.getZfBlog(zf_id);
			blog.setPost_time(DateUtils.ftmStringToDate("yyyy-MM-dd HH:mm:ss", dateS));
			blog.setBlog_body(meg);
			blog.setSendUser(user);
			blog.setZfblog(zfblog);
			blog.setFiletype(3);
			blog.setIsgroup(true);
			blog.setGroups(group);
			blogContentService.add(blog);
			//发送通知,仅对项目组成员和在线用户推送
			List<Users> memberList = blogContentService.getUserByGroupId(user.getId(),group.getId());
			final List<Long> targetIdList = new ArrayList<Long>();
			for(int i=0;i<memberList.size();i++)
			{
				
					targetIdList.add(memberList.get(i).getId());
				
			}
			final Map<String,Object> data = new HashMap<String, Object>();
			data.put("memberIdList", targetIdList);
			data.put("pmblogMeg", blog);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			messageService.sendMessage("top.MegHandler.showMeg", PageConstant.LG_USER_ID, targetIdList, PageConstant.MSG_TYPE_BLOG, 1, data, null);
			//messageService.sendMessage("MegHandler.showMeg", Constant.LG_USER_ID, targetIdList, Constant.MSG_TYPE_BLOG, 1, data, null);
			sendAt(blog);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 解散微群
	 * @param groupid 微群号
	 * @param req 请求信息
	 */
	public void deleteGroup(Long groupid,HttpServletRequest req)
	{
		blogContentService.delGroup(groupid);
	}
	/**
	 * 编辑微群
	 * @param groupid 编辑的微群号
	 * @param groupName 微群名
	 * @param groupMember 微群成员
	 * @param groupDes 微群描述
	 * @param req 请求信息
	 */
	public void EditGroup(Long groupid,String groupName,String groupMember,String groupDes,HttpServletRequest req){
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		MicroGroupPo group = new MicroGroupPo();
		try{
			group.setGroup_manager(user);
			group.setGroup_name(groupName);
			group.setGroup_description(groupDes);
			blogContentService.EditGroup(groupid,group);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		MicroGroupPo group1 = blogContentService.getGroupByName(groupName);
		String member[] = groupMember.split(",");
		List<String> usernamelist = new ArrayList<String>();
		for(int i = 0;i<member.length;i++){
			if(!(member[i].equals(null))&&!(member[i].equals(" "))){
				if(!(usernamelist.contains(member[i])))usernamelist.add(member[i]);
			}	
		}
		usernamelist.add(user.getUserName());
		if(usernamelist!=null){
		for(int i= 0;i<usernamelist.size();i++){
			Users group_user = blogContentService.getUser(usernamelist.get(i));
			GroupShipPo groupship = new GroupShipPo();
			try{
			groupship.setGroup(group1);
			groupship.setUser(group_user);
			blogContentService.addGroupShip(groupship);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		}
     }
	/**
	 * 退出微群（一般用户）
	 * @param groupid 微群号
	 * @param req 请求信息
	 */
	public void exitFromGroup(Long groupid,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		blogContentService.exitGroup(user.getId(),groupid);
	}
	/**
	 * 退出微群（管理员）
	 * @param managerName 新指定的管理员
	 * @param groupid 微群id
	 * @param req 请求信息
	 */
	public void ManagerExitGroup(String managerName,Long groupid,HttpServletRequest req)
	{
		Users user = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
		if(user==null)
		{
			return;
		}
		Users newManager = blogContentService.getUser(managerName);
		blogContentService.changeGroupManager(newManager,groupid);
		blogContentService.exitGroup(user.getId(),groupid);
	}
	 /**
	  * 获得全部微博（一般微博，第二层1，第一项或者第三层，第一项）
	  * @param userid 用户id
	  * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
	  * @param goPage 分页跳转的页号
	  * @param pageSize 分页的大小
	  * @return 全部微博信息
	  */
	 public Map<String,Object> getSpecialSearchBlog(Long userid,String key,Integer goPage,Integer pageSize)
	    {
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = blogContentService.getSpecialSearchBlog(userid,key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	    }
	 /**
	  * 获得微群的全部微博（微群微博，第二层2，第一项或者第三层，第一项）
	  * @param userid 用户id
	  * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
	  * @param goPage 分页跳转的页号
	  * @param pageSize 分页的大小
	  * @return 全部微博信息
	  */
	    public Map<String,Object> getSpecialSearchGroupBlog(Long userid,String key,Integer goPage,Integer pageSize)
	    {
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = blogContentService.getSpecialSearchGroupBlog(userid,key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	    }
	   /**
	    * 获得具体微群的微博（微群微博，第二层2，第2。。。项或者第三层，第一项）
	    * @param groupid 微群id
	    * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
	    * @param goPage 分页跳转的页号
	    * @param pageSize 分页的大小
	    * @return 具体微群的微博信息
	    */
	    public Map<String,Object> getSpecialSearchGroupBlogMegMap(Long groupid,String key,Integer goPage,Integer pageSize)
	    {
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = blogContentService.getSpecialSearchGroupBlogByGroupId(groupid,key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	    /**
	     * 获得相互关注的用户的微博（一般微博，第二层1，第二项或者第三层，第一项）
	     * @param userid 用户id
	     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
	     * @param goPage 分页跳转的页号
	     * @param pageSize 分页的大小
	     * @return 相互关注的用户的微博信息
	     */
	public Map<String,Object> getFriendBlogMegMapSearch(Long userid,String key,Integer goPage,Integer pageSize)
			{
				Page page = GridUtil.getGridPage(goPage, pageSize);
				List<BlogContentPo> pmblogList = blogContentService.getSearchFriendBlog(userid,key,page);
				if(pmblogList==null || pmblogList.isEmpty())
					return null;
				for(int i = 0;i<pmblogList.size();i++)System.out.println("============="+pmblogList.get(i).getBlog_body()+"===================");
				Map<String,Object> pmblogMap = new HashMap<String, Object>();
				pmblogMap.put("page", page);
				pmblogMap.put("pmblogList", pmblogList);
				pmblogMap.put("baseURL", WebConfig.userPortrait);
				return pmblogMap;
			}
	 /**
     * 获得我的微博（一般微博，第二层1，第三项或者第三层，第一项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 我的微博信息
     */
	public Map<String,Object> getMyOnlyBlogMegMapSearch(Long userid,String key,Integer goPage,Integer pageSize)
	{
		Page page = GridUtil.getGridPage(goPage, pageSize);
		System.out.println("ssss"+key+"ddd");
		List<BlogContentPo> pmblogList = blogContentService.getSearchMyOnlyBlog(userid,key,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
	
	/**
	 * 获得全部带图片的微博（一般微博，全部，第三层，第二项）
	 * @param userid 用户id
	 * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
	 * @param goPage 分页跳转的页号
	 * @param pageSize 分页的大小
	 * @return 全部带图片的微博信息
	 */
    public Map<String,Object> getPMBlogMegMapWithPicture(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getAllBlogWithPicture(userid,key,page);
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
 * 获得相互关注的带图片的微博（一般微博，相互关注，第三层，第二项）
  * @param userid 用户id
  * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
  * @param goPage 分页跳转的页号
  * @param pageSize 分页的大小
 * @return 相互关注的带图片的微博信息
 */
    public Map<String,Object> getFriendBlogMegMapWithPicture(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getFriendBlogWithPicture(userid,key,page);
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
     * 获得我的带图片的微博（一般微博，我的，第三层，第二项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 我的带图片的微博信息
     */
    public Map<String,Object> getMyOnlyBlogMegMapWithPicture(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getMyOnlyBlogWithPicture(userid,key,page);
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
     * 获得所有的带图片的群微博（微群微博，全部，第三层，第二项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 所有的带图片的群微博信息
     */
    public Map<String,Object> getAllGroupBlogMegMapWithPicture(Long userid,String key,Integer goPage,Integer pageSize){
	
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getAllGroupBlogByUserIdWithPicture(userid,key,page);
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
   * 获得特定的群的带图片的微博（微群微博，特定微群，第三层，第二项）
   * @param groupId 群的id
   * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
   * @param goPage 分页跳转的页号
   * @param pageSize 分页的大小
   * @return  特定的群的带图片的微博信息
   */
    public Map<String,Object> getGroupBlogMegMapWithPicture(Long groupId,String key,Integer goPage,Integer pageSize){
	
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getSpecialGroupBlogByIdWithPicture(groupId,key,page);
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
     * 获得全部微博的转发微博（一般微博，全部，第三层，第三项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 全部微博的转发微博信息
     */
    public Map<String,Object> getPMBlogMegMapWithZf(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getAllBlogWithZf(userid,key,page);
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
     * 获得相互关注的转发微博（一般微博，相互关注，第三层，第三项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 相互关注的转发微博信息
     */
    public Map<String,Object> getFriendBlogMegMapWithZf(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getFriendBlogWithZf(userid,key,page);
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
     * 获得我的转发微博（一般微博，我的，第三层，第三项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 我的转发微博信息
     */
    public Map<String,Object> getMyOnlyBlogMegMapWithZf(Long userid,String key,Integer goPage,Integer pageSize){
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getMyOnlyBlogWithZf(userid,key,page);
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
     * 获得全部的微群的转发微博（微群微博，全部，第三层，第三项）
     * @param userid 用户id
     * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
     * @param goPage 分页跳转的页号
     * @param pageSize 分页的大小
     * @return 全部的微群的转发微博信息
     */
    public Map<String,Object> getAllGroupBlogMegMapWithZf(Long userid,String key,Integer goPage,Integer pageSize){
	
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getAllGroupBlogByUserIdWithZf(userid,key,page);
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
    * 获得特定的微群的转发微博（微群微博，特定，第三层，第三项）
    * @param groupId 微群号
    * @param key 搜索关键字，若key值为空，则为全部微博，有key值则为该项的搜索结果
    * @param goPage 分页跳转的页号
    * @param pageSize 分页的大小
    * @return 特定的微群的转发微博信息
    */
    public Map<String,Object> getGroupBlogMegMapWithZf(Long groupId,String key,Integer goPage,Integer pageSize){
	
	Page page = GridUtil.getGridPage(goPage, pageSize);
	List<BlogContentPo> pmblogList = null;
	try{	
		pmblogList = blogContentService.getSpecialGroupBlogByIdWithZf(groupId,key,page);
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
     * 获得其他用户的用户信息
     * @param userid 用户id
     * @param otheruserid 特定用户的id
     * @return 其他用户的用户信息
     */
    public Map<String,Object> getOtherUserInfo(Long userid,Long otheruserid){
    	Users otherUser = blogContentService.getUserById(otheruserid);
    	List<Object> info = blogContentService.getUserInfo(otheruserid);
    	String friendShip = blogContentService.getSpecialFriendShip(userid,otheruserid);
		Map<String,Object> infoMap = new HashMap<String, Object>();
		infoMap.put("follow", info.get(0));
		infoMap.put("fan", info.get(1));
		infoMap.put("weibo", info.get(2));
		infoMap.put("otheruser", otherUser);
		infoMap.put("friendShip", friendShip);
		infoMap.put("baseURL", WebConfig.userPortrait);
		return infoMap;
   }
    /**
     * 得到其他用户的微博
     * @param userid 用户id
     * @param goPage 分页跳转的页号
     * @param pageSize 分页大小
     * @return 其他用户的微博信息
     */
    public Map<String,Object> getOtherUserBlogMegMap(Long userid,Integer goPage,Integer pageSize)
	{
		Page page = GridUtil.getGridPage(goPage, pageSize);
		List<BlogContentPo> pmblogList = blogContentService.getMyOnlyBlog(userid,page);
		if(pmblogList==null || pmblogList.isEmpty())
			return null;
		Map<String,Object> pmblogMap = new HashMap<String, Object>();
		pmblogMap.put("page", page);
		pmblogMap.put("pmblogList", pmblogList);
		pmblogMap.put("baseURL", WebConfig.userPortrait);
		return pmblogMap;
	}
    /**
     * 判断该用户是否存在
     * @param username 用户名
     * @return 存在：true，不存在：false
     */
    public boolean userExist(String username){
    	boolean flag=false;
    	List<Users> allusers= blogContentService.getAllUsers();
    	if(allusers!=null){
    	List<String> usernames = new ArrayList<String>();
    	for(int i = 0;i<allusers.size();i++){
    		usernames.add(allusers.get(i).getUserName());
    	}
    	if(usernames.contains(username))flag=true;
    	}
    	return flag;
    }
    /**
     * 创建群组时判断该微群名是否存在
     * @param groupname 微群名
     * @return
     */
    public boolean groupExist(String groupname){
    	boolean flag= false;
    	List<MicroGroupPo> allgroup= blogContentService.getAllGroup();
    	if(allgroup!=null){
    	List<String> groupnames = new ArrayList<String>();
    	for(int i = 0;i<allgroup.size();i++){
    		groupnames.add(allgroup.get(i).getGroup_name());
    	}
    	if(groupnames.contains(groupname))flag=true;
    	}
    	return flag;
    }
    /**
     * 编辑微群时判断该微群名是否在其他微群中存在
     * @param groupid 编辑的用户微群id
     * @param groupname 微群名
     * @return
     */
    public boolean groupOtherExist(Long groupid,String groupname){
    	boolean flag= false;
    	List<MicroGroupPo> allgroup= blogContentService.getAllOtherGroup(groupid);
    	if(allgroup!=null){
    	List<String> groupnames = new ArrayList<String>();
    	for(int i = 0;i<allgroup.size();i++){
    		groupnames.add(allgroup.get(i).getGroup_name());
    	}
    	if(groupnames.contains(groupname))flag=true;
    	}
    	return flag;
    }
    /**
     * 更新用户信息
     * @param Userid 用户id
     * @param type 1:新被提及；2：心私信；3：新粉丝
     * @param value 0：无新内容；有新内容
     */
   public void updateUserNewInfo(Long Userid,int type,int value){
	   blogContentService.updateUser(Userid,type,value);
   }
}
