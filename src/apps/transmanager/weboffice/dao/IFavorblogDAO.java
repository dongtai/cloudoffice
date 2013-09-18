package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.BlogContentPo;
import apps.transmanager.weboffice.domain.MyFavoritePo;
import apps.transmanager.weboffice.util.beans.Page;
/**
 * 对用户收藏的微博的数据库处理
 * @author 胡晓燕
 *
 */
public interface IFavorblogDAO extends IBaseDAO<MyFavoritePo>{
    /**
     * 根据用户id获得用户收藏的微博
     * @param userId 用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 收藏的微博列表
     */
	List<BlogContentPo> findFavorBolg(Long userId, Page page, String sort, String order);
   
	/**
	 * 根据微博id和用户id判断是否为该用户收藏的微博
	 * @param blogid  微博id
	 * @param userid 用户id
	 * @return true：为收藏的微博；false：不为收藏的微博
	 */
	boolean isfavorblog(Long blogid, Long userid);
    /**
     * 取消微博的收藏
     * @param blogid 微博id
     * @param userid 用户id
     * @return
     */
	boolean deleteFavorBlog(Long blogid, Long userid);

}
