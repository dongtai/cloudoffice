package apps.transmanager.weboffice.domain;

/**
 * 空间中节点定义接口，在空间中节点类型有多种，包括文件夹、子空间等等。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface ISpacesNode extends SerializableAdapter
{
	/**
	 * 节点类型，具体定义参见com.evermore.weboffice.constants.both.SpaceConstants中的相关定义。
	 * @return
	 */
	int getNodeType();
	
	/**
	 * 节点的显示名字
	 * @return
	 */
	String getDisplayName();
	
	/**
	 * 节点在文档库中的绝对路径 
	 * @return
	 */
	String getPath();
	
	/**
	 * 
	 * @return
	 */
	//long getPermission();
	
}
