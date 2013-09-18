package apps.transmanager.weboffice.dao.impl;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IBlogContentDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.util.beans.Page;
public class BlogContentDAO extends BaseDAOImpl<BlogContentPo> implements IBlogContentDAO{
	
	/**
	 * 根据用户名获得关注的全部微博
	 * @param userId 用户ID
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findAllBlog(Long userid,Page page, String sort, String order) {
		int count = (int)findAllBlogCount(userid);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(")  and (isgroup = 0)");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
	/**
	 * 根据用户id获得关注的全部微博数目
	 * @param userid 用户id
	 * @return 微博数目
	 */
	private long findAllBlogCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(") and (isgroup = 0)");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	
	/**
	 * 根据关键字获得全部微博
	 * @param key 关键字
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findBlogByKey(String key, Page page, String sort, String order) {
		int count = (int)findBlogByKeyCount(key);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where ((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))and (filetype = 1 or filetype = 3) and (delete_flag= 0)");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		queryString.append(" and (isGroup=0)");
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		return pmList;
	}
	/**
	 * 根据关键字获得全部微博数目
	 * @param key 关键字
	 * @return 微博数目
	 */
	private long findBlogByKeyCount(String key)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where ((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))and (filetype = 1 or filetype = 3) and (delete_flag= 0)");
		queryString.append(" and (isGroup=0)");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户名获得所有被提及的微博
	 * @param username 用户名
	 *  @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博列表
	 */
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findAtBlog(String username,Page page, String sort, String order) {
		int count = (int)findAtBlogCount(username);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where ((blog_body like '%@").append(username).append(" %') or (zfblog.id in (select id from BlogContentPo where blog_body like '%@").append(username).append("%' and delete_flag=").append(0).append(")) or (zfblog.id in (select id from BlogContentPo where sendUser.userName = '").append(username).append("' and delete_flag= 0))) and (delete_flag= 0)");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		return pmList;
	}
	/**
	 * 根据用户名获得所有被提及的微博数目
	 * @param username 用户名
	 * @return 微博数目
	 */
	private long findAtBlogCount(String username) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where ((blog_body like '%@").append(username).append(" %') or (zfblog.id in (select id from BlogContentPo where blog_body like '%@").append(username).append("%' and delete_flag=").append(0).append(")) or (zfblog.id in (select id from BlogContentPo where sendUser.userName = '").append(username).append("' and delete_flag= 0))) and (delete_flag= 0)");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();

	}
	/**
	 * 根据微博id获得其所有评论
	 * @param blogid 微博id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 评论列表
	 */
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findBlogReply(Long blogid, String sort, String order) {
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where replyblog.id =").append(blogid).append(" and filetype =").append(2).append(" and delete_flag=").append(0);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		List<BlogContentPo> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			pmList.add(pm);
		}
		return pmList;
	}
	/**
	 * 根据用户id获得除他外的所有用户
	 * @param userid 用户id
	 * @return 用户列表
	 */
	@SuppressWarnings("unchecked")
	public List<Users> findAllFanUser(Long userid) {
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where id in ( select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(")");
		Query query = getSession().createQuery(queryString.toString());
		List<Users> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users us = result.get(i);
			userList.add(us);
		}
		return userList;
	}
	/**
	 * 根据用户名获得用户
	 * @param username 用户名
	 * @return 用户
	 */
	public Users findUserByName(String username) {
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where userName = '").append(username).append("'");
		Query query = getSession().createQuery(queryString.toString());
		Users user = (Users)query.uniqueResult();
		return user;
	}
	/**
	 * 根据用户Id获得用户信息
	 * @param userid 用户id
	 * @return 用户信息
	 */
	public Users findUserById(Long userid) {
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where id = ").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		Users user = (Users)query.uniqueResult();
		return user;
	}

	private Long findReplyCount(Long blogid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where replyblog.id =").append(blogid).append(" and filetype =").append(2).append(" and delete_flag=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}

	private Long findZfCount(Long blogid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where zfblog.id =").append(blogid).append(" and filetype =").append(3).append(" and delete_flag=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}
	public BlogContentPo findZfidblog(Long zf_blogid){
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where id =").append(zf_blogid).append(" and delete_flag=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		BlogContentPo blog = (BlogContentPo)query.uniqueResult();
		return blog;
		
	}
	/**
	 * 根据微博id，删除一条微博
	 * @param blogid 微博id
	 * @return 
	 */
	public boolean delBlogBack(Long blog_id){
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update BlogContentPo ").append("set ");
			queryString.append(" delete_flag =").append(1).append(" where id=").append(blog_id);
			Query query = getSession().createQuery(queryString.toString());
			int flag = query.executeUpdate();
			if(flag==0)
			{
				return false;
			}else{
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	/**
	 * 根据用户id获得该用户所有微博的评论
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 评论列表
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findMyBlogReply(Long userid,Page page, String sort,String order) {
		int count = (int)findMyBlogReplyCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where delete_flag = 0 and filetype = 2 and replyblog.sendUser.id =").append(userid);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> blogList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getReplyblog().getId()).intValue();
			int zf_times = findZfCount(result.get(i).getReplyblog().getId()).intValue();
			if((result.get(i).getReplyblog().getFiletype()==3)&&!(result.get(i).getReplyblog().getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getReplyblog().getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getReplyblog().getZfblog().getId()).intValue();
				pm.getReplyblog().getZfblog().setZf_times(zf_zftimes);
				pm.getReplyblog().getZfblog().setReply_times(zf_replytimes);
			}
			pm.getReplyblog().setReply_times(reply_times);
			pm.getReplyblog().setZf_times(zf_times);
			blogList.add(pm);
		}
		return blogList;
	}
	/**
	 * 根据用户id获得该用户所有微博的评论数目
	 * @param userid 用户id
	 * @return 该用户所有微博的评论数目
	 */
	private Long findMyBlogReplyCount(Long userid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where delete_flag = 0 and filetype = 2 and replyblog.sendUser.id =").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}
	/**
	    * 根据用户id获得所有该用户发出的评论
	    * @param userid 用户id
		* @param page 分页辅助类
		* @param sort 排序字段
		* @param order 排序方式
		* @return 评论列表
	    */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findBlogMyReply(Long userId, Page page,
			String sort, String order) {
		int count = (int)findBlogMyReplyCount(userId).intValue();
		page.retSetTotalRecord(count);
		
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where delete_flag = 0 and filetype = 2 and sendUser.id =").append(userId);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> blogList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getReplyblog().getId()).intValue();
			int zf_times = findZfCount(result.get(i).getReplyblog().getId()).intValue();
			if((result.get(i).getReplyblog().getFiletype()==3)&&!(result.get(i).getReplyblog().getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getReplyblog().getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getReplyblog().getZfblog().getId()).intValue();
				pm.getReplyblog().getZfblog().setZf_times(zf_zftimes);
				pm.getReplyblog().getZfblog().setReply_times(zf_replytimes);
			}
			pm.getReplyblog().setReply_times(reply_times);
			pm.getReplyblog().setZf_times(zf_times);
			blogList.add(pm);
		}
		return blogList;
	}
	/**
	 * 根据用户id获得所有该用户发出的评论数目
	 * @param userid 用户id
	 * @return 该用户发出的评论数目
	 */
	private Long findBlogMyReplyCount(Long userid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where delete_flag = 0 and filetype = 2 and sendUser.id =").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}

	/**
	 * 根据关键字获得相关用户
	 * @param key 关键字
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 用户列表
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Users> findUserByKey(String key, Page page, String sort,
			String order) {
		int count = findUserByKeyCount(key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where userName like '%").append(key).append("%'");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Users> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users user = result.get(i);
			int fans_number = findfansCount(result.get(i).getId()).intValue();
			int follows_number = findfollowsCount(result.get(i).getId()).intValue();
			int weibo_number = findweiboCount(result.get(i).getId()).intValue();
			user.setFans_num(fans_number);
			user.setFollow_num(follows_number);
			user.setWeibo_num(weibo_number);
			userList.add(user);
		}
		return userList;
	}
  /**
   * 根据关键字获得相关用户数目
   * @param key 关键字
   * @return 用户数目
   */
   private Long findUserByKeyCount(String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from Users");
		queryString.append(" where userName like '%").append(key).append("%'");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
    /**
	 * 根据用户id获得他所发出的微博总数（一般微博）
	 * @param userid 用户id
	 * @return 微博总数
	 */
	@Override
	public Long findweiboCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户id获得他所有的粉丝数
	 * @param userid 用户id
	 * @return 粉丝数
	 */
	@Override
	public Long findfansCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from FriendshipPo");
		queryString.append(" where follow_user.id =").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户id获得他所有的关注的用户的数目
	 * @param userid 用户id
	 * @return 关注的用户的数量
	 */
    @Override
    public Long findfollowsCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from FriendshipPo");
		queryString.append(" where fan_user.id =").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
    /**
	 * 根据用户id，和被关注用户id判断两用户是否未关注关系
	 * @param userid 用户id
	 * @param follow_userid 关注用户id
	 * @return true：为关注关系；false：不为关注关系
	 */
	@Override
	public boolean isFollow(Long userid,Long follow_userid)
	{
		StringBuffer queryString = new StringBuffer("from FriendshipPo");
		queryString.append(" where fan_user.id = ").append(userid).append(" and follow_user.id = ").append(follow_userid);
		Query query = getSession().createQuery(queryString.toString());
		if(query.list().size()==0)return false;
		else return true;
	}
	/**
	 * 根据用户id得到所有的关注的用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 关注的用户列表
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Users> findMyFollow(Long userid,Page page, String sort, String order) {
		int count = findfollowsCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where id in ( select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(")");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Users> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users user = result.get(i);
			int fans_number = findfansCount(result.get(i).getId()).intValue();
			int follows_number = findfollowsCount(result.get(i).getId()).intValue();
			int weibo_number = findweiboCount(result.get(i).getId()).intValue();
			user.setFans_num(fans_number);
			user.setFollow_num(follows_number);
			user.setWeibo_num(weibo_number);
			userList.add(user);
		}
		return userList;
	}
	/**
	 * 根据用户id得到所有的粉丝用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 粉丝用户列表
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Users> findMyFan(Long userid, Page page, String sort,
			String order) {
		int count = findfansCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where id in ( select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(")");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Users> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users user = result.get(i);
			int fans_number = findfansCount(result.get(i).getId()).intValue();
			int follows_number = findfollowsCount(result.get(i).getId()).intValue();
			int weibo_number = findweiboCount(result.get(i).getId()).intValue();
			user.setFans_num(fans_number);
			user.setFollow_num(follows_number);
			user.setWeibo_num(weibo_number);
			userList.add(user);
		}
		return userList;
	}
	/**
	 * 根据用户id获得所有除他之外的用户
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 用户列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Users> findAllUserByPage(Long userid,Page page, String sort,String order) {
		int count = findAllUserByPageCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from Users");
		queryString.append(" where id<>").append(userid);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<Users> result = query.list();
		
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users user = result.get(i);
			userList.add(user);
		}
		
		return userList;
	}
	/**
	 * 根据用户id获得所有除他之外的用户数目   
	 * @param userid 用户id
	 * @return 用户数目
	 */
    private Long findAllUserByPageCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from Users");
		queryString.append(" where id<>").append(userid);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
    /**
	 * 根据微群号获得该微群的所有微博
	 * @param groupId 微群id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微群微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialGroupBlogById(Long groupId,Page page, String sort, String order) {
		int count = findSpecialGroupBlogByIdCount(groupId).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 根据微群号获得该微群的所有微博数目
     * @param groupId 微群id
     * @return 微博数目
     */
	private Long findSpecialGroupBlogByIdCount(Long groupId) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户id获得他的所有的微群微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微群微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findAllGroupBlogByUserId(Long userid, Page page,String sort, String order) {
		int count = findAllGroupBlogByUserIdCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     *  根据用户id获得他的所有的微群微博数目
     * @param userid 用户id
     * @return 微群微博数目
     */
	private Long findAllGroupBlogByUserIdCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户id获得所有相互关注的用户的微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findFriendBlog(Long userid, Page page,String sort, String order) {
		int count = findFriendBlogCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
   /**
    * 根据用户id获得所有相互关注的用户的微博数目
    * @param userid 用户id
    * @return 微博数目
    */
	private Long findFriendBlogCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据用户id获得所有我的微博
	 * @param userid 用户id
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findMyOnlyBlog(Long userid, Page page,String sort, String order) {
		int count = findMyOnlyBlogCount(userid).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 根据用户id获得所有我的微博数目
     * @param userid 用户id
     * @return 微博数目
     */
	private Long findMyOnlyBlogCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到所有带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部带图片的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findAllBlogWithPicture(Long userid,String key, Page page,String sort, String order) {
		int count = (int)findAllBlogWithPictureCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (delete_flag=0)").append("  and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(") and (isgroup = 0)");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
      * 通过用户id和关键字得到所有带图片的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findAllBlogWithPictureCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (delete_flag=0)").append("  and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(") and (isgroup = 0)");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到相互关注的用户带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户带图片的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findFriendBlogWithPicture(Long userid,String key,Page page, String sort, String order) {
		int count = findFriendBlogWithPictureCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (delete_flag= ").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
   /**
    * 通过用户id和关键字得到相互关注的用户带图片的微博（关键字为空则为全部微博）数目
	* @param userid 用户id
	* @param key 关键字
    * @return 微博数目
    */
	private Long findFriendBlogWithPictureCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (delete_flag= ").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and ((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到我的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的带图片的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findMyOnlyBlogWithPicture(Long userid,String key,Page page, String sort, String order) {
		int count = findMyOnlyBlogWithPictureCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
   /**
    * 通过用户id和关键字得到我的带图片的微博（关键字为空则为全部微博）数目
	* @param userid 用户id
	* @param key 关键字
    * @return 微博数目
    */
	private Long findMyOnlyBlogWithPictureCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer(" select count(id) from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到我的所有微群的带图片的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的所有微群的带图片的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findAllGroupBlogByUserIdWithPicture(Long userid,String key,Page page, String sort, String order) {
		int count = findAllGroupBlogByUserIdWithPictureCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 通过用户id和关键字得到我的所有微群的带图片的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findAllGroupBlogByUserIdWithPictureCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据微群id和关键字得到该微群所有的带图片的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 该微群所有的带图片的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialGroupBlogByIdWithPicture(Long groupId,String key, Page page, String sort, String order) {
		int count = findSpecialGroupBlogByIdWithPictureCount(groupId,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 根据微群id和关键字得到该微群所有的带图片的微博（关键字为空则为全部微博）数目
	 * @param groupId 微群id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findSpecialGroupBlogByIdWithPictureCount(Long groupId,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		queryString.append(" and ((filetype=1 and blog_body like '%#分享图片#%[F]%[/F]%') or(filetype=3 and zfblog.id in (select id from BlogContentPo where blog_body like '%#分享图片#%[F]%[/F]%' and delete_flag=0)))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到所有转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 所有转发的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findAllBlogWithZf(Long userid,String key, Page page,String sort, String order) {
		int count = (int)findAllBlogWithZfCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(")  and (isgroup = 0)");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
	/**
	 * 通过用户id和关键字得到所有转发的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
	 * @return 微博数目
	 */
	private Long findAllBlogWithZfCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(")  and (isgroup = 0)");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到相互关注的用户的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注的用户的转发的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findFriendBlogWithZf(Long userid,String key, Page page,String sort, String order) {
		int count = findFriendBlogWithZfCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 通过用户id和关键字得到相互关注的用户的转发的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findFriendBlogWithZfCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到我的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的转发的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findMyOnlyBlogWithZf(Long userid,String key, Page page,String sort, String order) {
		int count = findMyOnlyBlogWithZfCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 通过用户id和关键字得到我的转发的微博（关键字为空则为全部微博）数目
     * @param userid 用户id
	 * @param key 关键字
     * @return
     */
	private Long findMyOnlyBlogWithZfCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到我的所有微群的转发的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的所有微群的转发的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findAllGroupBlogByUserIdWithZf(Long userid,String key,Page page, String sort, String order) {
		int count = findAllGroupBlogByUserIdWithZfCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
	 * 通过用户id和关键字得到我的所有微群的转发的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findAllGroupBlogByUserIdWithZfCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype = 3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 根据微群id和关键字得到该微群所有转发的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 该微群所有转发的微博列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialGroupBlogByIdWithZf(Long groupId,String key,Page page, String sort, String order) {
		int count = findSpecialGroupBlogByIdWithZfCount(groupId,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 根据微群id和关键字得到该微群所有转发的微博（关键字为空则为全部微博）数目
	 * @param groupId 微群id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findSpecialGroupBlogByIdWithZfCount(Long groupId,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =3) and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupId).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到全部的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialSearchBlog(Long userid, String key,Page page, String sort, String order) {
		int count = findSpecialSearchBlogCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(")  and (isgroup = 0)");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		return pmList;
	}
    /**
     * 通过用户id和关键字得到全部的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findSpecialSearchBlogCount(Long userid, String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (sendUser.id in (select follow_user.id from FriendshipPo where fan_user.id =").append(userid).append(") or sendUser.id =").append(userid).append(")  and (isgroup = 0)");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到全部的微群微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 全部的微群微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialSearchGroupBlog(Long userid,String key, Page page, String sort, String order) {
		int count = findSpecialSearchGroupBlogCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		return pmList;
	}
   /**
    * 通过用户id和关键字得到全部的微群微博（关键字为空则为全部微博）数目
	* @param userid 用户id
	* @param key 关键字
    * @return 微博数目
    */
	private Long findSpecialSearchGroupBlogCount(Long userid, String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id in (select group.id from GroupShipPo where user.id = ").append(userid).append("))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过微群id和关键字得到特定微群的微博（关键字为空则为全部微博）
	 * @param groupId 微群id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 特定微群的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSpecialSearchGroupBlogByGroupId(Long groupid, String key, Page page, String sort, String order) {
		int count = findSpecialGroupBlogByIdCount(groupid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 通过微群id和关键字得到特定微群的微博（关键字为空则为全部微博）数目
	 * @param groupId 微群id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findSpecialGroupBlogByIdCount(Long groupid, String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 1) and (groups.id =").append(groupid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到相互关注的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 相互关注微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSearchFriendBlog(Long userid, String key,Page page, String sort, String order) {
		int count = findSearchFriendBlogCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
    /**
     * 通过用户id和关键字得到相互关注的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
     * @return 微博数目
     */
	private Long findSearchFriendBlogCount(Long userid, String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id in (select fan_user.id from FriendshipPo where follow_user.id =").append(userid).append(" and fan_user.id in (");
		queryString.append("select follow_user.id from FriendshipPo where fan_user.id = ").append(userid).append(")))");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 通过用户id和关键字得到我的微博（关键字为空则为全部微博）
	 * @param userid 用户id
	 * @param key 关键字
	 * @param page 分页
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 我的微博信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlogContentPo> findSearchMyOnlyBlog(Long userid, String key,Page page, String sort, String order) {
		int count = findSearchMyOnlyBlogCount(userid,key).intValue();
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());
		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
		List<BlogContentPo> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			BlogContentPo pm = result.get(i);
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			if((result.get(i).getFiletype()==3)&&!(result.get(i).getZfblog().isDelete_flag())){
				int zf_replytimes = findReplyCount(result.get(i).getZfblog().getId()).intValue();
				int zf_zftimes = findZfCount(result.get(i).getZfblog().getId()).intValue();
				pm.getZfblog().setZf_times(zf_zftimes);
				pm.getZfblog().setReply_times(zf_replytimes);
			}
			pm.setReply_times(reply_times);
			pm.setZf_times(zf_times);
			pmList.add(pm);
		}
		
		return pmList;
	}
   /**
    * 通过用户id和关键字得到我的微博（关键字为空则为全部微博）数目
	 * @param userid 用户id
	 * @param key 关键字
    * @return 微博数目
    */
	private Long findSearchMyOnlyBlogCount(Long userid,String key) {
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where (filetype =").append(1).append("or filetype =").append(3).append(") and (delete_flag=").append(0);
		queryString.append(" ) and (isgroup = 0) and (sendUser.id =").append(userid).append(")");
		queryString.append(" and((blog_body like '%").append(key).append("%') or (zfblog.id in (select id from BlogContentPo where blog_body like '%").append(key).append("%' and delete_flag=").append(0).append(")))");
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
	}
	/**
	 * 获得所有的用户
	 * @return 用户列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Users> findAllUser() {
		StringBuffer queryString = new StringBuffer("from Users");
		Query query = getSession().createQuery(queryString.toString());
		List<Users> result = query.list();
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<Users> userList = new ArrayList<Users>();
		for(int i=0;i<result.size();i++)
		{
			Users user = result.get(i);
			userList.add(user);
		}
		
		return userList;
	}
	/**
	 * 更新用户信息
	 * @param userid 用户id
	 * @param string 要更新的字段
	 * @param value 字段的值
	 * @return
	 */
	@Override
	public boolean updateUser(Long userid, String column, int value) {
		try
		{
			StringBuffer queryString = new StringBuffer();
			queryString.append("update Users set ").append(column).append("=").append(value);
			queryString.append(" where id =").append(userid);
			Query query = getSession().createQuery(queryString.toString());
			int flag = query.executeUpdate();
			if(flag==0)
			{
				return false;
			}else{
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
