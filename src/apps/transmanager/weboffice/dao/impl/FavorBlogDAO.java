package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IFavorblogDAO;
import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.domain.MyFavoritePo;
import apps.transmanager.weboffice.util.beans.Page;

public class FavorBlogDAO extends BaseDAOImpl<MyFavoritePo> implements IFavorblogDAO{
	/**
     * 根据用户id获得用户收藏的微博
     * @param userId 用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 收藏的微博列表
     */
	@Override
	@SuppressWarnings("unchecked")
	public List<BlogContentPo> findFavorBolg(Long userId, Page page,String sort,String order) {
		int count = (int)findFavorBlogCount(userId);
		page.retSetTotalRecord(count);
		StringBuffer queryString = new StringBuffer("from MyFavoritePo");
		queryString.append(" where favorUser.id = ").append(userId);
		queryString.append(" order by ").append(sort).append(" ").append(order);
		Query query = getSession().createQuery(queryString.toString());

		query.setFirstResult(page.getCurrentRecord());
		query.setMaxResults(page.getPageSize());
	    
		List<MyFavoritePo> favorresult = query.list();
		List<BlogContentPo> result = new ArrayList<BlogContentPo>();
		for (int i = 0;i<favorresult.size();i++){
			result.add(favorresult.get(i).getFavorblog());
		}
		if(result==null || result.isEmpty())
		{
			return null;
		}
		List<BlogContentPo> pmList = new ArrayList<BlogContentPo>();
		for(int i=0;i<result.size();i++)
		{
			int reply_times = findReplyCount(result.get(i).getId()).intValue();
			int zf_times = findZfCount(result.get(i).getId()).intValue();
			BlogContentPo pm = result.get(i);
			
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
	 * 根据用户id获得用户收藏的微博数目
     * @param userId 用户id
	 * @return 微博数目
	 */
	private long findFavorBlogCount(Long userid) {
		StringBuffer queryString = new StringBuffer("select count(id) from MyFavoritePo");
		queryString.append(" where favorUser.id = ").append(userid);
		Query query = getSession().createQuery(queryString.toString());
        return (Long) query.uniqueResult();

	}
	/**
	 * 得到微博的评论数
	 * @param blogid 微博id
	 * @return 评论数
	 */
	private Long findReplyCount(Long blogid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where reply_blogid =").append(blogid).append(" and filetype =").append(2).append(" and delete_flag=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}
	/**
	 * 得到微博的转发数
	 * @param blogid 微博id
	 * @return 转发数
	 */
	private Long findZfCount(Long blogid)
	{
		StringBuffer queryString = new StringBuffer("select count(id) from BlogContentPo");
		queryString.append(" where zfblog.id =").append(blogid).append(" and filetype =").append(3).append(" and delete_flag=").append(0);
		Query query = getSession().createQuery(queryString.toString());
		return (Long) query.uniqueResult();
		
	}
	/**
	 * 根据微博id和用户id判断是否为该用户收藏的微博
	 * @param blogid  微博id
	 * @param userid 用户id
	 * @return true：为收藏的微博；false：不为收藏的微博
	 */
	@Override
	public boolean isfavorblog(Long blogid, Long userid) {
		StringBuffer queryString = new StringBuffer("from MyFavoritePo");
		queryString.append(" where favorUser.id = ").append(userid).append(" and favorblog.id = ").append(blogid);
		Query query = getSession().createQuery(queryString.toString());
		if(query.list().size()==0)return false;
		else return true;
	}
	/**
     * 取消微博的收藏
     * @param blogid 微博id
     * @param userid 用户id
     * @return
     */
	@Override
	public boolean deleteFavorBlog(Long blogid, Long userid) {
		try
		{
		StringBuffer queryString = new StringBuffer("delete from MyFavoritePo");
		queryString.append(" where favorUser.id = ").append(userid).append(" and favorblog.id = ").append(blogid);
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
