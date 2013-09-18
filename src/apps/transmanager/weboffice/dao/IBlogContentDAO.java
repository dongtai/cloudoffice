package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.util.beans.Page;
/**
 * 处理微博以及用户与数据库的交互
 * @author 胡晓燕
 *
 */
public interface IBlogContentDAO extends IBaseDAO<BlogContentPo>{
	/**
	 * 根据用户名获得关注的全部微博
	 * @param userId 用户ID
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	public List<BlogContentPo> findAllBlog(Long userid,Page page, String sort, String order);
	/**
	 * 根据关键字获得全部微博
	 * @param key 关键字
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	public List<BlogContentPo> findBlogByKey(String key,Page page, String sort, String order);
	/**
	 * 根据微博id获得其所有评论
	 * @param blogid 微博id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 评论列表
	 */
	public List<BlogContentPo> findBlogReply(Long blogid, String sort, String order);
	/**
	 * 根据用户id获得除他外的所有用户
	 * @param userid 用户id
	 * @return 用户列表
	 */
	public List<Users> findAllFanUser(Long userid);
	/**
	 * 根据转发的微博id获得微博
	 * @param blogid 微博id
	 * @return 转发微博
	 */
	public BlogContentPo findZfidblog(Long blogid);
	/**
	 * 根据用户名获得用户
	 * @param username 用户名
	 * @return 用户
	 */
	public Users findUserByName(String username) ;
	/**
	 * 根据用户名获得所有被提及的微博
	 * @param username 用户名
	 *  @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	public List<BlogContentPo> findAtBlog(String username,Page page, String sort, String order);
	/**
	 * 根据用户Id获得用户信息
	 * @param userid 用户id
	 * @return 用户信息
	 */
	public Users findUserById(Long userid);
	/**
	 * 根据微博id，删除一条微博
	 * @param blogid 微博id
	 * @return 
	 */
	public boolean delBlogBack(Long blogid);
	/**
	 * 根据用户id获得该用户所有微博的评论
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 评论列表
	 */
	public List<BlogContentPo> findMyBlogReply(Long userid,Page page, String sort,String order);
   /**
    * 根据用户id获得所有该用户发出的评论
    * @param userid 用户id
	* @param page 分页辅助类
	* @param sort 排序字段
	* @param order 排序方式
	* @return 评论列表
    */
	public List<BlogContentPo> findBlogMyReply(Long userId, Page page,String sort, String order);
	/**
	 * 根据关键字获得相关用户
	 * @param key 关键字
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 用户列表
	 */
	public List<Users> findUserByKey(String key, Page page, String sort,String order);
	/**
	 * 根据用户id，和被关注用户id判断两用户是否未关注关系
	 * @param userid 用户id
	 * @param follow_userid 关注用户id
	 * @return true：为关注关系；false：不为关注关系
	 */
	public boolean isFollow(Long userid,Long follow_userid);
	/**
	 * 根据用户id获得他所发出的微博总数（一般微博）
	 * @param userid 用户id
	 * @return 微博总数
	 */
	Long findweiboCount(Long userid);
	/**
	 * 根据用户id获得他所有的粉丝数
	 * @param userid 用户id
	 * @return 粉丝数
	 */
	Long findfansCount(Long userid);
	/**
	 * 根据用户id获得他所有的关注的用户的数目
	 * @param userid 用户id
	 * @return 关注的用户的数量
	 */
	Long findfollowsCount(Long userid);
	/**
	 * 根据用户id得到所有的关注的用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 关注的用户列表
	 */
	public List<Users> findMyFollow(Long userid,Page page, String sort, String order);
	/**
	 * 根据用户id得到所有的粉丝用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 粉丝用户列表
	 */
	public List<Users> findMyFan(Long userid, Page page, String sort,String order);
	/**
	 * 根据用户id获得所有除他之外的用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 用户列表
	 */
	public List<Users> findAllUserByPage(Long userid,Page page, String sort,String order);
	/**
	 * 根据微群号获得该微群的所有微博
	 * @param groupId 微群id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微群微博列表
	 */
	public List<BlogContentPo> findSpecialGroupBlogById(Long groupId,Page page, String sort, String order);
	/**
	 * 根据用户id获得他的所有的微群微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微群微博列表
	 */
	public List<BlogContentPo> findAllGroupBlogByUserId(Long userid, Page page,String sort, String order);
	/**
	 * 根据用户id获得所有相互关注的用户的微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户的微博列表
	 */
	public List<BlogContentPo> findFriendBlog(Long userid, Page page,String sort, String order);
	/**
	 * 根据用户id获得所有我的微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的微博列表
	 */
	public List<BlogContentPo> findMyOnlyBlog(Long userid, Page page,String sort, String order);
	/**
	 * 通过用户id和关键字得到所有带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部带图片的微博信息列表
	 */
	public List<BlogContentPo> findAllBlogWithPicture(Long userid,String key, Page page,String sort, String order);
	/**
	 * 通过用户id和关键字得到相互关注的用户带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户带图片的微博信息列表
	 */
	public List<BlogContentPo> findFriendBlogWithPicture(Long userid,String key,Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到我的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的带图片的微博信息列表
	 */
	public List<BlogContentPo> findMyOnlyBlogWithPicture(Long userid,String key,Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到我的所有微群的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的所有微群的带图片的微博信息列表
	 */
	public List<BlogContentPo> findAllGroupBlogByUserIdWithPicture(Long userid,String key,Page page, String sort, String order);
	/**
	 * 根据微群id和关键字得到该微群所有的带图片的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 该微群所有的带图片的微博列表
	 */
	public List<BlogContentPo> findSpecialGroupBlogByIdWithPicture(Long groupId,String key, Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到所有转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 所有转发的微博列表
	 */
	public List<BlogContentPo> findAllBlogWithZf(Long userid, String key,Page page,String sort, String order);
	/**
	 * 通过用户id和关键字得到相互关注的用户的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户的转发的微博列表
	 */
	public List<BlogContentPo> findFriendBlogWithZf(Long userid,String key, Page page,String sort, String order);
	/**
	 * 通过用户id和关键字得到我的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的转发的微博列表
	 */
	public List<BlogContentPo> findMyOnlyBlogWithZf(Long userid,String key, Page page,String sort, String order);
	/**
	 * 通过用户id和关键字得到我的所有微群的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的所有微群的转发的微博信息列表
	 */
	public List<BlogContentPo> findAllGroupBlogByUserIdWithZf(Long userid,String key,Page page, String sort, String order);
	/**
	 * 根据微群id和关键字得到该微群所有转发的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 该微群所有转发的微博列表
	 */
	public List<BlogContentPo> findSpecialGroupBlogByIdWithZf(Long groupId,String key,Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到全部的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部的微博信息列表
	 */
	public List<BlogContentPo> findSpecialSearchBlog(Long userid, String key,Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到全部的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部的微群微博信息列表
	 */
	public List<BlogContentPo> findSpecialSearchGroupBlog(Long userid,String key, Page page, String sort, String order);
	/**
	 * 通过微群id和关键字得到特定微群的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 特定微群的微博信息列表
	 */
	public List<BlogContentPo> findSpecialSearchGroupBlogByGroupId(Long groupid, String key, Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到相互关注的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注微博信息列表
	 */
	public List<BlogContentPo> findSearchFriendBlog(Long userid, String key,Page page, String sort, String order);
	/**
	 * 通过用户id和关键字得到我的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的微博信息列表
	 */
	public List<BlogContentPo> findSearchMyOnlyBlog(Long userid, String key,Page page, String sort, String order);
	/**
	 * 获得所有的用户
	 * @return 用户列表
	 */
	public List<Users> findAllUser();
	/**
	 * 更新用户信息
	 * @param userid 用户id
	 * @param string 要更新的字段
	 * @param value 字段的值
	 * @return
	 */
	public boolean updateUser(Long userid, String string, int value);
}

