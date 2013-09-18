package apps.transmanager.weboffice.service;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.domain.FriendshipPo;
import apps.transmanager.weboffice.domain.GroupShipPo;
import apps.transmanager.weboffice.domain.MicroGroupPo;
import apps.transmanager.weboffice.domain.MyFavoritePo;
import apps.transmanager.weboffice.domain.PrivateLetterPo;
import apps.transmanager.weboffice.util.beans.Page;
public interface IBlogContentService {
	/**
	 * 添加一条微博
	 * @param blog 微博信息
	 */
	void add(BlogContentPo blog);
	/**
	 * 添加一条新私信
	 * @param priletter 私信信息
	 */
	void addPriLetter(PrivateLetterPo priletter);
	/**
	 * 添加一条新的收藏
	 * @param myfavor 收藏的微博信息
	 */ 
	void addFavorblog(MyFavoritePo myfavor);
	/**
	 * 添加一个新的微群
	 * @param group 微群信息
	 */
	void addGroup(MicroGroupPo group);
	/**
	 * 为微群添加一个新的成员
	 * @param groupship
	 */
	void addGroupShip(GroupShipPo groupship);
	/**
	 * 添加一对新的用户关系
	 * @param friendship
	 */
	void addFriendship(FriendshipPo friendship);
	/**
	 * 删除一条新的私信（软删除）
	 * @param priletter_id 私信id
	 * @param user 删除的用户
	 */
	void delPriLetter(Long priletter_id,Users user);
	/**
	 * 删除一条微博评论（软删除）
	 * @param blogId 微博号
	 * @param user 删除的用户
	 */
	void delBolgBack(Long blogId, Users user);
	/**
	 * 删除一条微博（软删除）
	 * @param blogId 微博号
	 * @param user 删除的用户
	 */
	void delBolg(Long blogId, Users user);
	/**
	 * 删除一条微博的收藏（直接删除）
	 * @param blogid 微博号
	 * @param user 删除的用户
	 */
	void delFavorBlog(Long blogid, Users user);
	/**
	 * 取消一条用户关注（或粉丝）
	 * @param follow_userid 被关注的用户
	 * @param id 粉丝用户
	 */
	void delFollow(Long follow_userid, Long id);
	/**
	 * 删除一个微群（直接删除）
	 * @param groupid 微群id
	 */
	void delGroup(Long groupid);
   /**
    * 退出微群，删除一个用户与微群的关系（直接删除）
    * @param id 用户id
    * @param groupid 微群id
    */
	void exitGroup(Long id, Long groupid);
	/**
	 * 更新一个微群的管理员
	 * @param newManager 新的管理员
	 * @param groupid 微群id
	 */
	void changeGroupManager(Users newManager, Long groupid);
	/**
	 * 更新一个微群的基本信息
	 * @param groupid 微群号
	 * @param group 新的微群信息
	 */
	void EditGroup(Long groupid, MicroGroupPo group);
	/**
	 * 更新用户信息
	 * @param Userid 用户id
     * @param type 1:新被提及；2：心私信；3：新粉丝
     * @param value 0：无新内容；有新内容
	 */
	void updateUser(Long userid, int type,int value);
	/**
	 * 判断是否为已经收藏的微博
	 * @param blogid 微博id
	 * @param userid 用户id
	 * @return true：已经收藏；false：未收藏
	 */
	boolean judgeFavor(Long blogid, Long userid);
	/**
	 * 判断两个用户之间的关系
	 * @param userid 用户id
	 * @param otheruserid 另一用户id
	 * @return myself:自身;both_follow:相互关注;just_follow;只关注;just_fan：仅是粉丝;no_follow:未关注
	 */
	String getSpecialFriendShip(Long userid, Long otheruserid);
	/**
	 * 根据用户id和分页得到所有微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 所有微博信息列表
	 */
	List<BlogContentPo> getAllBlog(Long userid,Page page);
	/**
	 * 根据用户id得到用户被@的微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户被@的微博信息列表
	 */
	List<BlogContentPo> getAtBlog(Long userid,Page page);
	/**
	 * 通过用户id得到该用户的所有粉丝
	 * @param userid 用户id
	 * @return 该用户的所有粉丝信息列表
	 */
	List<Users> getFanMemberList(Long userid);
	/**
	 * 通过该微博的id得到该微博所有的评论
	 * @param blogid 微博的id
	 * @return 该微博所有的评论列表
	 */
	List<BlogContentPo> getBlogReply(Long blogid);
	/**
	 * 通过关键字得到所有的相关微博
	 * @param key 关键字
	 * @param page 分页
	 * @return 相关微博信息列表
	 */
	List<BlogContentPo> searchBlog(String key,Page page);
	/**
	 * 通过转发的id得到该微博的转发微博
	 * @param zf_id
	 * @return 转发微博号
	 */
	BlogContentPo getZfBlog(Long zf_id);
	/**
	 * 通过收藏的id得到该微博
	 * @param favor_id 收藏的id
	 * @return 收藏的微博
	 */
	BlogContentPo getfavorBlog(Long favor_id);
	/**
	 * 通过用户名得到用户
	 * @param username 用户名
	 * @return 用户信息
	 */
	Users getUser(String username);
	/**
	 * 通过用户id得到用户
	 * @param userid 用户id
	 * @return 用户信息
	 */
	Users getUserById(Long userid);
	/**
	 * 通过用户id得到收到的私信
	 * @param userId 用户id
	 * @param page 分页
	 * @return 收到的私信信息列表
	 */
	List<PrivateLetterPo> getReceivedLetter(Long userId,Page page);
	/**
	 * 通过用户id得到发出的私信
	 * @param userId 用户id
	 * @param page 分页
	 * @return 发出的私信信息列表
	 */
	List<PrivateLetterPo> getSendLetter(Long userId,Page page);
	/**
	 * 通过用户id得到用户收藏的微博
	 * @param userId 用户id
	 * @param page 分页
	 * @return 收藏的微博信息列表
	 */
	List<BlogContentPo> getFavorBlogList(Long userId, Page page);
	/**
	 * 通过用户id得到用户的微博收到的评论
	 * @param userId 用户id
	 * @param page 分页
	 * @return 用户的微博收到的评论信息列表
	 */
	List<BlogContentPo> getMyBlogReply(Long userId, Page page);
	/**
	 * 通过微博id得到评论的微博
	 * @param blogid 微博id
	 * @return 评论的微博信息
	 */
	BlogContentPo getReplyBlog(Long blogid);
	/**
	 * 通过用户的id得到用户的所有的评论
	 * @param userId 用户id
	 * @param page 分页
	 * @return 用户的所有的评论信息列表
	 */
	List<BlogContentPo> getBlogMyReply(Long userId, Page page);
	/**
	 * 通过关键字查询所有用户
	 * @param key 关键字
	 * @param page 分页
	 * @return 搜索到的相关用户的信息列表
	 */
	List<Users> searchUser(String key, Page page);
	/**
	 * 得到我与其他用户的关系
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 关系信息列表
	 */
	List<String> getfriendshipFlag(Long userid, String key, Page page);
	/**
	 * 通过用户id得到用户的基本信息
	 * @param userid 用户id
	 * @return 用户的基本信息
	 */
	List<Object> getUserInfo(Long userid);
	/**
	 * 通过用户id得到该用户关注的用户
	 * @param userid 用户Id
	 * @param page 分页
	 * @return 关注用户的信息列表
	 */
	List<Users> getfollow(Long userid,Page page);
	/**
	 * 通过用户id得到用户与关注我的用户之间的关系
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户与关注我的用户之间的关系列表
	 */
	List<String> getfriendshipFlag_follow(Long userid, Page page);
	/**
	 * 
	 * 通过用户id得到该用户的粉丝
	 * @param userid 用户Id
	 * @param page 分页
	 * @return 用户的粉丝信息列表
	 */
	List<Users> getfan(Long userid, Page page);
	/**
	 * 通过用户id得到用户与我的粉丝之间的关系
	 * @param userid 用户id
	 * @param page 分页
	 * @return 用户与与我的粉丝之间的关系列表
	 */
	List<String> getfriendshipFlag_fan(Long userid, Page page);
	/**
	 * 通过用户id得到所有的用户（本用户除外）
	 * @param userid 用户id
	 * @param page 分页
	 * @return 所有用户的信息列表
	 */
	List<Users> getfriendList(Long userid,Page page);
	/**
	 * 通过微群名得到微群信息
	 * @param groupName 微群名
	 * @return 微群信息
	 */
	MicroGroupPo getGroupByName(String groupName);
	/**
	 * 通过用户id得到他的所有微群
	 * @param userid 用户id
	 * @return 所有微群信息列表
	 */
	List<MicroGroupPo> getGroupList(Long userid);
	/**
	 * 通过微群id得到微群信息
	 * @param groupid 微群id
	 * @return 微群信息
	 */
	MicroGroupPo getGroupById(Long groupid);
	/**
	 * 通过微群id和用户id得到该微群除了本用户之外的所有用户
	 * @param userid 用户id
	 * @param groupid 微群id
	 * @return 所有用户的信息列表
	 */
	List<Users> getUserByGroupId(Long userid,Long groupid);
	/**
	 * 通过微群id得到该微群的微博
	 * @param groupId 微群id
	 * @param page 分页
	 * @return 该微群的所有微博信息列表
	 */
	List<BlogContentPo> getSpecialGroupBlogById(Long groupId, Page page);
	/**
	 * 通过用户id得到该用户的所有微群的微博
	 * @param userid 用户id
	 * @param page 分页
	 * @return 为群微博的信息列表
	 */
	List<BlogContentPo> getAllGroupBlogByUserId(Long userid, Page page);
	/**
	 * 通过用户id得到相互关注的用户的微博
	 * @param userid 用户Id
	 * @param page分页
	 * @return 微博信息列表
	 */
	List<BlogContentPo> getFriendBlog(Long userid, Page page);
	/**
	 * 通过用户id得到我的的微博
	 * @param userid 用户Id
	 * @param page分页
	 * @return 微博信息列表
	 */
	List<BlogContentPo> getMyOnlyBlog(Long userid, Page page);
	/**
	 * 通过用户id和关键字得到所有带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部带图片的微博信息列表
	 */
	List<BlogContentPo> getAllBlogWithPicture(Long userid, String key,Page page);
	/**
	 * 通过用户id和关键字得到相互关注的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注的带图片的微博信息列表
	 */
	List<BlogContentPo> getFriendBlogWithPicture(Long userid,String key, Page page);
	/**
	 * 通过用户id和关键字得到我的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的带图片的微博信息列表
	 */
	List<BlogContentPo> getMyOnlyBlogWithPicture(Long userid,String key, Page page);
	/**
	 * 通过用户id和关键字得到所有带图片的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部带图片的微群微博信息列表
	 */
	List<BlogContentPo> getAllGroupBlogByUserIdWithPicture(Long userid,String key,Page page);
	/**
	 * 通过微群id和关键字得到特定微群带图片的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群带图片的微博信息列表
	 */
	List<BlogContentPo> getSpecialGroupBlogByIdWithPicture(Long groupId,String key,Page page);
	/**
	 * 通过用户id和关键字得到所有转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部转发的微群微博信息列表
	 */
	List<BlogContentPo> getAllBlogWithZf(Long userid,String key, Page page);
	/**
	 * 通过用户id和关键字得到相互关注的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注的转发的微博信息列表
	 */
	List<BlogContentPo> getFriendBlogWithZf(Long userid,String key, Page page);
	/**
	 * 通过用户id和关键字得到我的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的转发的微博信息列表
	 */
	List<BlogContentPo> getMyOnlyBlogWithZf(Long userid,String key, Page page);
	/**
	 * 通过用户id和关键字得到所有的转发的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 所有转发的微群微博信息列表
	 */
	List<BlogContentPo> getAllGroupBlogByUserIdWithZf(Long userid,String key, Page page);
	/**
	 * 通过微群id和关键字得到特定微群转发的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群转发的微博信息列表
	 */
	List<BlogContentPo> getSpecialGroupBlogByIdWithZf(Long groupId,String key, Page page);
	/**
	 * 通过用户id和关键字得到全部的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部的微博信息列表
	 */
	List<BlogContentPo> getSpecialSearchBlog(Long userid, String key, Page page);
	/**
	 * 通过用户id和关键字得到全部的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 全部的微群微博信息列表
	 */
	List<BlogContentPo> getSpecialSearchGroupBlog(Long userid, String key,Page page);
	/**
	 * 通过微群id和关键字得到特定微群的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @return 特定微群的微博信息列表
	 */
	List<BlogContentPo> getSpecialSearchGroupBlogByGroupId(Long groupid,String key, Page page);
	/**
	 * 通过用户id和关键字得到相互关注的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 相互关注微博信息列表
	 */
	List<BlogContentPo> getSearchFriendBlog(Long userid, String key, Page page);
	/**
	 * 通过用户id和关键字得到我的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @return 我的微博信息列表
	 */
	List<BlogContentPo> getSearchMyOnlyBlog(Long userid, String key, Page page);
	/**
	 * 得到全部的用户
	 * @return 全部的用户列表
	 */
	List<Users> getAllUsers();
	/**
	 * 得到全部的微群
	 * @return 全部的微群列表
	 */
	List<MicroGroupPo> getAllGroup();
	/**
	 * 得到除了自身外全部的微群
	 * @param groupid 微群id
	 * @return 除自身外的全部微群列表
	 */
	List<MicroGroupPo> getAllOtherGroup(Long groupid);
}
