package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

public interface IBaseDAO<E> {
	/**
     * 返回ID=ID的持久对象
     * 
     * @param id
     * @return E
     * @throws Exception
     */
	public E get(Integer id) throws Exception;


	/**
     * 返回ID=ID的代理对象
     * 
     * @param id
     * @return E
     * @throws Exception
     */
	public E load(Integer id) throws Exception;

	/**
     * 返回根据字段的得到的持久对象
     * 
     * @param name
     * @param value
     * @return E
     * @throws Exception
     */
	public E loadBy(String name, Object value) throws Exception;

	/**
     * 返回根据数组字段的得到的持久对象
     * 
     * @param names
     * @param values
     * @return E
     * @throws Exception
     */
	public E loadBy(String[] names, Object[] values) throws Exception;

	/**
     * 返回保存的对象的ID
     * 
     * @param entity
     * @return Integer
     * @throws Exception
     */
	public Long save(E entity) throws Exception;

	/**
     * 更新对象信息
     * 
     * @param entity
     * @throws Exception
     */
	public void update(E entity) throws Exception;
	
	/**
     * 更新对象信息
     * 
     * @param entity
     * @throws Exception
     */
	public void merge(E entity) throws Exception;

	/**
     * saveOrUpdate对象信息
     * 
     * @param entity
     * @throws Exception
     */
	public void saveOrUpdate(E entity) throws Exception;

	/**
     * 刪除对象
     * 
     * @param entity
     * @throws Exception
     */
	public void delete(E entity) throws Exception;

	/**
     * 返回对象信息list
     * 
     * @return List<E>
     * @throws Exception
     */
	public List<E> listAll() throws Exception;

	/**
     * 根据字段name和数据value返回对象list
	 * @param name
	 * @param value
	 * @return List<E>
	 * @throws Exception
	 */
	public List<E> findBy(String name, Object value) throws Exception;
	/**
     * 根据数组字段name,数据value返回对象list
     * 
     * @param names
     * @param values
     * @return List<E>
     * @throws Exception
     */
	public List<E> findBy(String[] names, Object[] values) throws Exception;

	/**
     * 更新内存中的数据
     * 
     * @param entity
     * @throws Exception
     */
	public void refresh(E entity) throws Exception;

	/**
     * 提交内存中的数据
     * 
     * @throws Exception
     */
	public void flush() throws Exception;
	
	/**
	 * 保存所有的数据
	 * @param entityList
	 */
	public void saveAll(List<E> entityList);
}
