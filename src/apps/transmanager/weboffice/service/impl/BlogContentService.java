package apps.transmanager.weboffice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IBlogContentDAO;
import apps.transmanager.weboffice.dao.IFavorblogDAO;
import apps.transmanager.weboffice.dao.IFriendShipDAO;
import apps.transmanager.weboffice.dao.IGroupShipDAO;
import apps.transmanager.weboffice.dao.IMicroGroupDAO;
import apps.transmanager.weboffice.dao.IPriLetterDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.domain.FriendshipPo;
import apps.transmanager.weboffice.domain.GroupShipPo;
import apps.transmanager.weboffice.domain.MicroGroupPo;
import apps.transmanager.weboffice.domain.MyFavoritePo;
import apps.transmanager.weboffice.domain.PrivateLetterPo;
import apps.transmanager.weboffice.service.IBlogContentService;
import apps.transmanager.weboffice.util.beans.Page;

@Component(value=BlogContentService.NAME)
public class BlogContentService implements IBlogContentService {
	public static final String NAME = "BlogContentService";
	
	@Autowired
	private IBlogContentDAO blogDAO;
	@Autowired
	private IPriLetterDAO plDAO;
	@Autowired
	private IFavorblogDAO favorDAO;
	@Autowired
	private IFriendShipDAO friendDAO;
	@Autowired
	private IMicroGroupDAO groupDAO;
	@Autowired
	private IGroupShipDAO groupshipDAO;
	
	public void setGroupshipDAO(IGroupShipDAO groupshipDAO) {
		this.groupshipDAO = groupshipDAO;
	}
	public void setGroupDAO(IMicroGroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}
	public void setFriendDAO(IFriendShipDAO friendDAO) {
		this.friendDAO = friendDAO;
	}
	public void setFavorDAO(IFavorblogDAO favorDAO) {
		this.favorDAO = favorDAO;
	}
	public void setPldao(IPriLetterDAO plDAO) {
		this.plDAO = plDAO;
	}
	public void setBlogDAO(IBlogContentDAO blogDAO) {
		this.blogDAO = blogDAO;
	}
	/**
	 * 添加一条微博
	 * @param blog 微博信息
	 */
	public void add(BlogContentPo blog) {
		blogDAO.saveOrUpdate(blog);
		}
	/**
	 * 添加一条新私信
	 * @param priletter 私信信息
	 */
	public void addPriLetter(PrivateLetterPo priletter) {
		plDAO.saveOrUpdate(priletter);
		}
	/**
	 * 添加一条新的收藏
	 * @param myfavor 收藏的微博信息
	 */ 
	public void addFavorblog(MyFavoritePo favorblog) {
		favorDAO.saveOrUpdate(favorblog);
		}
	/**
	 * 添加一对新的用户关系
	 * @param friendship
	 */
	public void addFriendship(FriendshipPo friendship){
		friendDAO.saveOrUpdate(friendship);
	}
	/**
	 * 添加一个新的微群
	 * @param group 微群信息
	 */
	public void addGroup(MicroGroupPo group){
		groupDAO.saveOrUpdate(group);
	}
	/**
	 * 为微群添加一个新的成员
	 * @param groupship
	 */
	public void addGroupShip(GroupShipPo groupship){
		groupshipDAO.saveOrUpdate(groupship);
	}
	/**
	 * 根据用户id和分页得到所有微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 所有微博信息列表
	 */
	public List<BlogContentPo> getAllBlog(Long userid,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllBlog(userid,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过该微博的id得到该微博所有的评论
	 * @param blogid 微博的id
	 * @return 该微博所有的评论列表
	 */
	public List<BlogContentPo> getBlogReply(Long blogid) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findBlogReply(blogid,"post_time","desc");
		return pmBlogList;
	}
	/**
	 * 通过转发的id得到该微博的转发微博
	 * @param zf_id
	 * @return 转发微博号
	 */
	public BlogContentPo getZfBlog(Long blogid) {
		BlogContentPo pmBlog = null;
		pmBlog = blogDAO.findZfidblog(blogid);
		return pmBlog;
	}
	/**
	 * 通过用户id得到该用户的所有粉丝
	 * @param userid 用户id
	 * @return 该用户的所有粉丝信息列表
	 */
	public List<Users> getFanMemberList(Long userid){
		List<Users> memberList = blogDAO.findAllFanUser(userid);
		return memberList;
	}
	/**
	 * 通过用户名得到用户
	 * @param username 用户名
	 * @return 用户信息
	 */
	public Users getUser(String username) {
		Users member = blogDAO.findUserByName(username);
		return member;
	}
	/**
	 * 通过用户id得到用户
	 * @param userid 用户id
	 * @return 用户信息
	 */
	public Users getUserById(Long userid) {
		Users member = blogDAO.findUserById(userid);
		return member;
	}
	/**
	 * 根据用户id得到用户被@的微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户被@的微博信息列表
	 */
	public List<BlogContentPo> getAtBlog(Long userid,Page page) {
		List<BlogContentPo> pmBlogList = null;
		Users member = blogDAO.findUserById(userid);
		String username = member.getUserName();
		pmBlogList = blogDAO.findAtBlog(username,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id得到收到的私信
	 * @param userId 用户id
	 * @param page 分页
	 * @return 收到的私信信息列表
	 */
	public List<PrivateLetterPo> getReceivedLetter(Long userId,Page page) {
		List<PrivateLetterPo> pletter = null;
		pletter = plDAO.findLetterByReceivedUser(userId,page, "sendtime", "desc");
		return pletter;
	}
	/**
	 * 通过用户id得到发出的私信
	 * @param userId 用户id
	 * @param page 分页
	 * @return 发出的私信信息列表
	 */
	public List<PrivateLetterPo> getSendLetter(Long userId,Page page) {
		List<PrivateLetterPo> pletter = null;
		pletter = plDAO.findLetterBySendUser(userId,page, "sendtime", "desc");
		return pletter;
	}
	/**
	 * 通过关键字得到所有的相关微博
	 * @param key 关键字
	 * @param page 分页
	 * @return 相关微博信息列表
	 */
	@Override
	public List<BlogContentPo> searchBlog(String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findBlogByKey(key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 删除一条微博评论（软删除）
	 * @param blogId 微博号
	 * @param user 删除的用户
	 */
	@Override
	public void delBolgBack(Long blogId, Users user) {
		BlogContentPo pmblog = blogDAO.findById(BlogContentPo.class.getName(), blogId);
		if(pmblog!=null && pmblog.getSendUser().getId().longValue()==user.getId().longValue())
		{
			blogDAO.delBlogBack(blogId);
		}
		
	}
	/**
	 * 删除一条微博（软删除）
	 * @param blogId 微博号
	 * @param user 删除的用户
	 */
	@Override
	public void delBolg(Long blogId, Users user) {
		BlogContentPo pmblog = blogDAO.findById(BlogContentPo.class.getName(), blogId);
		if(pmblog!=null && pmblog.getSendUser().getId().longValue()==user.getId().longValue())
		{
			blogDAO.delBlogBack(blogId);
            List<BlogContentPo> replylist = null;
            replylist = blogDAO.findBlogReply(blogId,"post_time","desc");
            if(replylist==null) System.out.println(" ");
            else{
            	for(int i=0;i<replylist.size();i++)
            	{
            		blogDAO.delBlogBack(replylist.get(i).getId());
            	}
            }
		}
		
	}
	/**
	 * 删除一条新的私信（软删除）
	 * @param priletter_id 私信id
	 * @param user 删除的用户
	 */
	@Override
	public void delPriLetter(Long priletter_id,Users user) {
		Long userid = user.getId();
		PrivateLetterPo letter = plDAO.findById(PrivateLetterPo.class.getName(), priletter_id);
		if(letter!=null){
		if(userid.equals(letter.getSendmeg_user().getId())){
			plDAO.delByUser("send_del", priletter_id);
		}
		else{
			plDAO.delByUser("send_to_del", priletter_id);
		}
		}
		
	}
	/**
	 * 通过收藏的id得到该微博
	 * @param favor_id 收藏的id
	 * @return 收藏的微博
	 */
	@Override
	public BlogContentPo getfavorBlog(Long favor_id) {
		BlogContentPo pmBlog = null;
		pmBlog = blogDAO.findById(BlogContentPo.class.getName(), favor_id);
		
		return pmBlog;
	}
	/**
	 * 通过用户id得到用户收藏的微博
	 * @param userId 用户id
	 * @param page 分页
	 * @return 收藏的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getFavorBlogList(Long userId, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = favorDAO.findFavorBolg(userId,page, "favor_time", "desc");
	    return pmBlogList;
	}
	/**
	 * 判断是否为已经收藏的微博
	 * @param blogid 微博id
	 * @param userid 用户id
	 * @return true：已经收藏；false：未收藏
	 */
	@Override
	public boolean judgeFavor(Long blogid, Long userid) {
		boolean flag = false;
		flag = favorDAO.isfavorblog(blogid,userid);
		return flag;
	}
	/**
	 * 删除一条微博的收藏（直接删除）
	 * @param blogid 微博号
	 * @param user 删除的用户
	 */
	@Override
	public void delFavorBlog(Long blogid, Users user) {
		Long userid = user.getId();
		boolean flag = favorDAO.isfavorblog(blogid, userid);
		if(flag)
		{
			favorDAO.deleteFavorBlog(blogid, userid);
		}
	}
	/**
	 * 通过用户id得到用户的微博收到的评论
	 * @param userId 用户id
	 * @param page 分页
	 * @return 用户的微博收到的评论信息列表
	 */
	@Override
	public List<BlogContentPo> getMyBlogReply(Long userId, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findMyBlogReply(userId,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过微博id得到评论的微博
	 * @param blogid 微博id
	 * @return 评论的微博信息
	 */
	@Override
	public BlogContentPo getReplyBlog(Long blogid) {
		BlogContentPo pmBlog = null;
		pmBlog = blogDAO.findById(BlogContentPo.class.getName(),blogid);
		return pmBlog;
	}
	/**
	 * 通过用户的id得到用户的所有的评论
	 * @param userId 用户id
	 * @param page 分页
	 * @return 用户的所有的评论信息列表
	 */
	@Override
	public List<BlogContentPo> getBlogMyReply(Long userId, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findBlogMyReply(userId,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过关键字查询所有用户
	 * @param key 关键字
	 * @param page 分页
	 * @return 搜索到的相关用户的信息列表
	 */
	@Override
	public List<Users> searchUser(String key, Page page) {
		List<Users>  userList = null;
		userList = blogDAO.findUserByKey(key,page,"id", "desc");
		return userList;
	}
	/**
	 * 得到我与其他用户的关系
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 关系信息列表
	 */
	@Override
	public List<String> getfriendshipFlag(Long userid, String key, Page page) {
		List<String> flagList = new ArrayList<String>();
		List<Users> userlist= blogDAO.findUserByKey(key,page,"id", "desc"); 
		if(userlist!=null){
		for(int i =0;i<userlist.size();i++){
			if(userid.equals(userlist.get(i).getId()))flagList.add("myself");
			else{
			if(blogDAO.isFollow(userid,userlist.get(i).getId()))
				{//我关注的
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("both_follow");
				else flagList.add("just_follow");
				}
			else {//我的粉丝
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("just_fan");
				else flagList.add("no_follow");
			   }
			}
		}
		return flagList;
		}
		else return null;
	}
	/**
	 * 取消一条用户关注（或粉丝）
	 * @param follow_userid 被关注的用户
	 * @param id 粉丝用户
	 */
	@Override
	public void delFollow(Long follow_userid, Long userid) {
		friendDAO.deletefollow(follow_userid,userid);
	}
	/**
	 * 通过用户id得到用户的基本信息
	 * @param userid 用户id
	 * @return 用户的基本信息
	 */
	@Override
	public List<Object> getUserInfo(Long userid) {
		List<Object> info = new ArrayList<Object>();;
		info.add(0,blogDAO.findfollowsCount(userid));
		info.add(1,blogDAO.findfansCount(userid));
		info.add(2,blogDAO.findweiboCount(userid));
		return info;
	}
	/**
	 * 通过用户id得到该用户关注的用户
	 * @param userid 用户Id
	 * @param page 分页
	 * @return 关注用户的信息列表
	 */
	@Override
	public List<Users> getfollow(Long userid,Page page) {
		List<Users>  userList = null;
		userList = blogDAO.findMyFollow(userid,page,"id", "desc");
		return userList;
	}
	/**
	 * 通过用户id得到用户与关注我的用户之间的关系
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户与关注我的用户之间的关系列表
	 */
	@Override
	public List<String> getfriendshipFlag_follow(Long userid, Page page) {
		List<String> flagList = new ArrayList<String>();
		List<Users> userlist= blogDAO.findMyFollow(userid,page,"id", "desc"); 
		if(userlist==null)return null;
		else{
		for(int i =0;i<userlist.size();i++){
			if(userid.equals(userlist.get(i).getId()))flagList.add("myself");
			else{
			if(blogDAO.isFollow(userid,userlist.get(i).getId()))
				{//我关注的
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("both_follow");
				else flagList.add("just_follow");
				}
			else {//我的粉丝
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("just_fan");
				else flagList.add("no_follow");
			   }
			}
		}
		return flagList;
		}
	}
	/**
	 * 
	 * 通过用户id得到该用户的粉丝
	 * @param userid 用户Id
	 * @param page 分页
	 * @return 用户的粉丝信息列表
	 */
	@Override
	public List<Users> getfan(Long userid, Page page) {
		List<Users>  userList = null;
		userList = blogDAO.findMyFan(userid,page,"id", "desc");
		return userList;
	}
	/**
	 * 通过用户id得到用户与我的粉丝之间的关系
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户与与我的粉丝之间的关系列表
	 */
	@Override
	public List<String> getfriendshipFlag_fan(Long userid, Page page) {
		List<String> flagList = new ArrayList<String>();
		List<Users> userlist= blogDAO.findMyFan(userid,page,"id", "desc"); 
		if(userlist==null)return null;
		else{
		for(int i =0;i<userlist.size();i++){
			if(userid.equals(userlist.get(i).getId()))flagList.add("myself");
			else{
			if(blogDAO.isFollow(userid,userlist.get(i).getId()))
				{//我关注的
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("both_follow");
				else flagList.add("just_follow");
				}
			else {//我的粉丝
				if(blogDAO.isFollow(userlist.get(i).getId(),userid))flagList.add("just_fan");
				else flagList.add("no_follow");
			   }
			}
		}
		return flagList;
		}
	}
	/**
	 * 通过用户id得到所有的用户（本用户除外）
	 * @param userid 用户id
	 * @param page 分页
	 * @return 所有用户的信息列表
	 */
	@Override
	public List<Users> getfriendList(Long userid,Page page) {
		List<Users> memberList = blogDAO.findAllUserByPage(userid,page, "id", "desc");
		return memberList;
	}
	/**
	 * 通过微群名得到微群信息
	 * @param groupName 微群名
	 * @return 微群信息
	 */
	@Override
	public MicroGroupPo getGroupByName(String groupName) {
		MicroGroupPo group = groupDAO.findByPropertyUnique(MicroGroupPo.class.getName(), "group_name", groupName);
		return group;
	}
	/**
	 * 通过用户id得到他的所有微群
	 * @param userid 用户id
	 * @return 所有微群信息列表
	 */
	@Override
	public List<MicroGroupPo> getGroupList(Long userid) {
		List<MicroGroupPo>  grouplist= groupDAO.findListByUserid(userid,"create_time","desc");
		return grouplist;
	}
	/**
	 * 通过微群id得到微群信息
	 * @param groupid 微群id
	 * @return 微群信息
	 */
	@Override
	public MicroGroupPo getGroupById(Long groupid) {
		MicroGroupPo group = groupDAO.findById(MicroGroupPo.class.getName(),groupid);
		return group;
	}
	/**
	 * 通过微群id和用户id得到该微群除了本用户之外的所有用户
	 * @param userid 用户id
	 * @param groupid 微群id
	 * @return 所有用户的信息列表
	 */
	@Override
	public List<Users> getUserByGroupId(Long userid,Long groupid) {
		List<Users> userList = new ArrayList<Users>();
		userList = groupshipDAO.findUserByGroupId(userid,groupid);
		return userList;
	}
	/**
	 * 通过微群id得到该微群的微博
	 * @param groupId 微群id
	 * @param page 分页
	 * @return 该微群的所有微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialGroupBlogById(Long groupId, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialGroupBlogById(groupId,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id得到该用户的所有微群的微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 为群微博的信息列表
	 */
	@Override
	public List<BlogContentPo> getAllGroupBlogByUserId(Long userid, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllGroupBlogByUserId(userid,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 删除一个微群（直接删除）
	 * @param groupid 微群id
	 */
	@Override
	public void delGroup(Long groupid) {
		groupDAO.deleteById(MicroGroupPo.class.getName(), groupid);
	}
	/**
	    * 退出微群，删除一个用户与微群的关系（直接删除）
	    * @param id 用户id
	    * @param groupid 微群id
	    */
	@Override
	public void exitGroup(Long userid, Long groupid) {
		groupshipDAO.delShip(userid,groupid);
	}
	/**
	 * 更新一个微群的管理员
	 * @param newManager 新的管理员
	 * @param groupid 微群id
	 */
	@Override
	public void changeGroupManager(Users newManager, Long groupid) {
		groupDAO.updateManager(newManager,groupid);
	}
	/**
	 * 通过用户id得到相互关注的用户的微博
	 * @param userid 用户Id
	 * @param page分页
	 * @return 微博信息列表
	 */
	@Override
	public List<BlogContentPo> getFriendBlog(Long userid, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findFriendBlog(userid,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id得到我的的微博
	 * @param userid 用户Id
	 * @param page分页
	 * @return 微博信息列表
	 */
	@Override
	public List<BlogContentPo> getMyOnlyBlog(Long userid, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findMyOnlyBlog(userid,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到所有带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部带图片的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getAllBlogWithPicture(Long userid, String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllBlogWithPicture(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到相互关注的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注的带图片的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getFriendBlogWithPicture(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findFriendBlogWithPicture(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到我的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的带图片的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getMyOnlyBlogWithPicture(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findMyOnlyBlogWithPicture(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到所有带图片的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部带图片的微群微博信息列表
	 */
	@Override
	public List<BlogContentPo> getAllGroupBlogByUserIdWithPicture(Long userid,String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllGroupBlogByUserIdWithPicture(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过微群id和关键字得到特定微群带图片的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群带图片的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialGroupBlogByIdWithPicture(Long groupId,String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialGroupBlogByIdWithPicture(groupId,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到所有转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部转发的微群微博信息列表
	 */
	@Override
	public List<BlogContentPo> getAllBlogWithZf(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllBlogWithZf(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到相互关注的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注的转发的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getFriendBlogWithZf(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findFriendBlogWithZf(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到我的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的转发的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getMyOnlyBlogWithZf(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findMyOnlyBlogWithZf(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到所有的转发的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 所有转发的微群微博信息列表
	 */
	@Override
	public List<BlogContentPo> getAllGroupBlogByUserIdWithZf(Long userid,String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findAllGroupBlogByUserIdWithZf(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过微群id和关键字得到特定微群转发的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群转发的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialGroupBlogByIdWithZf(Long groupId,String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialGroupBlogByIdWithZf(groupId,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到全部的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialSearchBlog(Long userid, String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialSearchBlog(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到全部的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部的微群微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialSearchGroupBlog(Long userid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialSearchGroupBlog(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过微群id和关键字得到特定微群的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSpecialSearchGroupBlogByGroupId(Long groupid,String key, Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSpecialSearchGroupBlogByGroupId(groupid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到相互关注的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSearchFriendBlog(Long userid, String key,Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSearchFriendBlog(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 通过用户id和关键字得到我的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的微博信息列表
	 */
	@Override
	public List<BlogContentPo> getSearchMyOnlyBlog(Long userid, String key,
			Page page) {
		List<BlogContentPo> pmBlogList = null;
		pmBlogList = blogDAO.findSearchMyOnlyBlog(userid,key,page,"post_time", "desc");
		return pmBlogList;
	}
	/**
	 * 判断两个用户之间的关系
	 * @param userid 用户id
	 * @param otheruserid 另一用户id
	 * @return myself:自身;both_follow:相互关注;just_follow;只关注;just_fan：仅是粉丝;no_follow:未关注
	 */
	@Override
	public String getSpecialFriendShip(Long userid, Long otheruserid) {
		String flag=null;
		if(userid.equals(otheruserid))flag="myself";
		else{
		if(blogDAO.isFollow(userid,otheruserid))
			{//我关注的
			if(blogDAO.isFollow(otheruserid,userid))flag="both_follow";
			else flag="just_follow";
			}
		else {//我的粉丝
			if(blogDAO.isFollow(otheruserid,userid))flag="just_fan";
			else flag="no_follow";
		   }
		}
		return flag;
	}
	/**
	 * 得到全部的用户
	 * @return 全部的用户列表
	 */
	@Override
	public List<Users> getAllUsers() {
		List<Users> userList = new ArrayList<Users>();
		userList = blogDAO.findAllUser();
		return userList;
	}
	/**
	 * 得到全部的微群
	 * @return 全部的微群列表
	 */
	@Override
	public List<MicroGroupPo> getAllGroup() {
		List<MicroGroupPo> groupList = new ArrayList<MicroGroupPo>();
		groupList = groupDAO.findAllGroup();
		return groupList;
	}
	/**
	 * 得到除了自身外全部的微群
	 * @param groupid 微群id
	 * @return 除自身外的全部微群列表
	 */
	@Override
	public List<MicroGroupPo> getAllOtherGroup(Long groupid) {
		List<MicroGroupPo> groupList = new ArrayList<MicroGroupPo>();
		groupList = groupDAO.findAllOtherGroup(groupid);
		return groupList;
	}
	/**
	 * 更新一个微群的基本信息
	 * @param groupid 微群号
	 * @param group 新的微群信息
	 */
	@Override
	public void EditGroup(Long groupid, MicroGroupPo group) {
		groupDAO.UpdateGroup(groupid,group);
	}
	/**
	 * 更新用户信息
	 * @param Userid 用户id
     * @param type 1:新被提及；2：心私信；3：新粉丝
     * @param value 0：无新内容；有新内容
	 */
	@Override
	public void updateUser(Long userid, int type,int value) {
		if(type==1)blogDAO.updateUser(userid,"newmention",value);
		else if(type==2)blogDAO.updateUser(userid,"priread",value);
		else blogDAO.updateUser(userid,"newfan",value);
		
	}
}
