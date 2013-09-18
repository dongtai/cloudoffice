package templates.service;

import java.util.List;

import templates.objectdb.News;
import templates.objectdb.NewsType;

public interface NewsService {
	/**
	 * 保存
	 * @param 
	 * @return
	 */
	public News save(News newsInfo);
	/**
	 * 删除
	 * @param id
	 */
	public void delete(Long id);
	/**
	 * 根据条件查找
	 * @param hql
	 * @return
	 */
	public List<News> find(String hql);
	public List<News> find(String hql,int limit);
	/**
	 * 根据条件修改
	 * @param hql
	 * @return
	 */
	
	public News update(News newsInfo);
	
	public News get(Long id);
	
	
	public PageBean queryForPage(String hql,int pageSize, int page,int allRow );
	
	public List<NewsType> findType(String hql);
}
