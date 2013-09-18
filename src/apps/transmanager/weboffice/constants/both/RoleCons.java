package apps.transmanager.weboffice.constants.both;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
public final class RoleCons
{
	/**
	 * 禁止实例化类
	 */
	private RoleCons()
	{
	}
	
	/**
	 * 角色类型定义。所有的角色都是作为模板定义存在，在具体创建相应的组织等机构的时候，依附于该组织等机构
	 */
	public final static int SYSTEM = 0;                        // 0 系统角色。
	public final static int SPACE = SYSTEM + 1;             // 1 空间角色。

}
