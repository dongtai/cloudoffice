package templates.service;

import java.util.List;

import templates.objectdb.NewsAttached;

public interface NewsAttachedService {

	/**
	 * 保存
	 * @param 
	 * @return
	 */
	public NewsAttached save(NewsAttached newsAttached);
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
	public List<NewsAttached> find(String hql);
	/**
	 * 根据条件修改
	 * @param hql
	 * @return
	 */
	
	public NewsAttached update(NewsAttached newsAttached);
	
	public NewsAttached get(Long id);
}
