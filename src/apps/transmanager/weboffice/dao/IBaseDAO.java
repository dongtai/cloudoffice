package apps.transmanager.weboffice.dao;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.util.beans.Page;

public interface IBaseDAO<T> {

	/**
	 * 根据ID查找一个实体对象
	 * @param className 类名称
	 * @param id 
	 * @return 实体对象
	 */
	T findById(String className,Object id);
	T findById(String className,Object id,int usertype);
	
	/**
	 * 增加或者更新一个实体
	 * 
	 * @param entity
	 *            实体
	 */
	void saveOrUpdate(T entity);
	
	public void saveOrUpdateAll(List<T> entitis);

	/**
	 * 删除一个实体
	 * 
	 * @param entity
	 *            实体
	 */
	void delete(T entity);

	/**
	 * 根据ID集合删除多条记录
	 * @param className 类名
	 * @param idList id数组
	 * @return 删除结果
	 */
	public boolean deleteByIdList(String className,List<?> idList);
	
	/**
	 * 根据属性删除记录
	 * @param className 类名
	 * @param property 属性
	 * @param value 属性值
	 * @return 删除结果
	 */
	public boolean deleteByProperty(String className,String property,Object value);
	
	/**
	 * 根据字段查找数据
	 * 
	 * @param className
	 *            对象名
	 * @param name
	 *            字段名
	 * @param value
	 *            字段值
	 * @return 结果数据
	 */
	List<T> findByProperty(String className, String name, Object value);
	
	/**
	 * 根据单个字段查找唯一数据
	 * @param className 实体名
	 * @param string 字段名
	 * @param value  字段值
	 * @return 结果数据
	 */
	T findByPropertyUnique(String className, String string, Object value);
	
	/**
	 * 根据字段名称和实体对象查找唯一记录(obj对象中属性不能为NULL，否者查询不正确)
	 * @param columNames 字段名称
	 * @param obj 实体对象
	 * @return 唯一记录
	 */
	public T findByPropertyUnique(List<String> columNames,T obj);
	
	/**
	 * 根据字段名称列表和对象查找记录集合(obj对象中属性不能为NULL，否者查询不正确)
	 * @param columNames 对象名称列表
	 * @param obj 实体对象
	 * @return 记录集合
	 */
	public List<T> findByProperty(List<String> columNames,T obj);
	
	/**
	 * 根据列名和值查找记录集合（将条件组合为MAP对象）
	 * @param className 类名
	 * @param propertyMap 列名和值的MAP
	 * @return 记录集合
	 */
	public List<T> findByProperty(String className,Map<String,Object> propertyMap);
	
	/**
	 * 根据列名和值查找唯一记录值(将条件组合为MAP对象)
	 * @param className 类名
	 * @param propertyMap 列名和值的MAP参数
	 * @return 唯一记录
	 */
	public T findByPropertyUnique(String className,Map<String,Object> propertyMap);
	
	/**
	 * 查询单表所有记录，可以分页(分页项设置为NULL则不分页)
	 * @param className 类名
	 * @param page 分页项
	 * @return 单表记录列表
	 */
	public List<T> findAll(String className,Page page);

	/**
	 * 根据单一条件查询记录(同时要求分页和排序条件)
	 * @param className 类名
	 * @param name 字段名称
	 * @param value 字段值
	 * @param page  分页条件
	 * @param sortColume 排序字段
	 * @param sort 排序方式
	 * @return 记录集
	 */
	List<T> findByProperty(String className, String name, Object value,
			Page page, String sortColume, String sort);
	
	/**
	 * 根据单多条件查询记录(同时要求分页和排序条件)
	 * @param className 类名
	 * @param propertyMap 条件集合
	 * @param page  分页条件
	 * @param sortColume 排序字段
	 * @param sort 排序方式
	 * @return 记录集
	 */
	List<T> findByProperty(String className,
			Map<String, Object> propertyMap, Page page, String sort,
			String order);
	
	/**
	 * 根据单多条件查询记录(同时要求分页和排序条件)
	 * @param className 类名
	 * @param propertyMap 条件集合
	 * @param page  分页条件
	 * @param sortColume 排序字段
	 * @param sort 排序方式
	 * @return 记录集
	 */
	List<T> findByProperty(String className,
			Map<String, Object> propertyMap, String sort,
			String order);


	/**
	 * 根据单一条件查询记录(同时要求分页)
	 * @param className 类名
	 * @param name 字段名称
	 * @param value 字段值
	 * @param page 分页条件
	 * @return 记录集
	 */
	List<T> findByProperty(String className, String name, Object value,
			Page page);

	/**
	 * 根据单一条件查询记录(同时要求排序)
	 * @param className 类名
	 * @param name 字段名称
	 * @param value 字段值
	 * @param sortColume 排序字段
	 * @param sort 排序方式
	 * @return 记录集
	 */
	List<T> findByProperty(String className, String name, Object value,
			String sortColume, String sort);
	
	/**
	 * 多条件查询(分页)
	 * @param className 类名
	 * @param propertyMap 多条件（列-值MAP）
	 * @param page 分页类
	 * @return 记录集
	 */
	public List<T> findByProperty(String className,
			Map<String, Object> propertyMap,Page page);
	
	/**
	 * 更新对象(根据给出的条件，将符合条件的对象更新为同一值，请设置合适的条件)
	 * @param className 类名
	 * @param columNames 字段列表
	 * @param conditions 条件
	 * @param propertyMap map(这个Map应该具备已经修改的属性值和条件值)
	 * @return 是否更新成功
	 */
	public boolean update(String className,List<String> columNames,List<String> conditions,Map<String,Object> propertyMap);
	
	/**
	 * 更新对象(根据给出的条件，将符合条件的对象更新为同一值)
	 * @param className 类名
	 * @param columNames 要更新的字段
	 * @param conditions 条件(List<String> 类型)
	 * @param object 值对象
	 * @return 是否更新
	 */
	public boolean update(String className,List<String> columNames,List<String> conditions,T object);
	
	/**
	 * 根据ID和类名删除数据库记录
	 * @param className 类名
	 * @param id ID值
	 * @return 删除结果
	 */
	public boolean deleteById(String className,Object id);

	/**
	 * 更新数据
	 * @param className 实体类名
	 * @param columName 列名
	 * @param condition 条件
	 * @param object 实体对象
	 * @return 更新是否成功
	 */
	boolean update(String className, String columName, String condition,
			T object);

}
