package apps.transmanager.weboffice.domain;


/**
 * 为有自关联的对象定义使用。
 * 如果某个对象有父对象，为了快速获得对象的子对象或父对象，在对象定义中实现该接口。
 * <p>
 * 例如：
 * 为了快速获得组的子组或父组，增加在组中增加parentKey字段。
 * 该字段的编码方式为从父组中得到同样的字段的值，加上父组id值加上“-”字符而得到 。
 * 如果该组为没有父组，则该字段为null。
 * 如果该组的父组的该字段为null,而父组的id为2，则该组的该字段值为“2-”。
 * 如果该组的父组的该字段为“1-”,而父组的id为3，则该组的该字段值为“1-3-”。
 * 如果如为该组下的子组，则该子组的该字段值按上述规则处理。
 * 
 * 如此处理后，在查询某个对象的所有级别的父对象的时候就不需要递归查询，只需要得到该
 * 对象的parentKey值，把该值按‘-’分割符合切分，得到的数字即是该对象的所有级别的父
 * 对象的id值。
 * 同样，在查询某个对象的所有级别的子对象的时候，通过查询对象的parentKey值，只要对象的
 * parentKey值like该对象”parentKey+id+-%“值（按上述规则生成的parentKey值）时候，
 * 即是该对象的所有级别的子对象，也避免对数据库的递归查询。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface IParentKey
{

	/**
	 * 
	 * @return
	 */
	Long getId();
	
	/**
	 * 获得该对象的父对象
	 * @return
	 */
	IParentKey getParent();
	
	/**
	 * 获得该对象的parentKey值
	 * @return
	 */
	String getParentKey();
	
	/**
	 * 设置该对象的parentKey值
	 * @param parentKey
	 */
	void setParentKey(String parentKey);
	
}
