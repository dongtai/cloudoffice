package templates.service;

import java.util.List;

import templates.objectdb.Collection;
import templates.objectdb.CollectionBean;
import templates.objectdb.Template;


public interface CollectionService {

	
	/**
	 * 保存模板
	 * @param 
	 * @return
	 */
	public Collection save(Collection collection);
	/**
	 * 删除指定ID的模板
	 * @param id
	 */
	public void delete(Long id);
	/**
	 * 根据条件查找模板
	 * @param hql
	 * @return
	 */
	public List<Collection> find(String hql);
	/**
	 * 根据条件修改模板
	 * @param hql
	 * @return
	 */
	
	public Collection update(Collection collection);
	
	
	public PageBean queryForPage(String hql,int pageSize, int page,int allRow );
	
	public List<Collection> query(CollectionBean queryBean) throws Exception;
	
	public void flush();
	public void clear();
}
