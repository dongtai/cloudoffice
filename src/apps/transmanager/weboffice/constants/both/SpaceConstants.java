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

//采用class定义方式是禁止或实现继承类。
public final class SpaceConstants
{
	/**
	 * 禁止实例化类
	 */
	private SpaceConstants()
	{
	}
	
	/**
	 * 空间权限定义
	 */
	public final static int PERMISSION = 0;                                  // 0 赋值权限，权限的再分配	
	public final static int ADD_MEMBER = PERMISSION + 1;                     // 1增加成员
	public final static int CREATE_ROLE = ADD_MEMBER + 1;                    // 2增删角色
	public final static int PUBLIC_BULLETIN = CREATE_ROLE + 1;               // 3 发布公告
	public final static int TRASH = PUBLIC_BULLETIN + 1;                     // 4 回收站
	public final static int DELETE_SPACE = TRASH + 1;                        // 5 删除空间
	
	// 先定义在此，后续移到相应的位置。具体的内容位置需要同上面的常量位置对应一致
	public final static String[] SAPCE_ACTION_NAME = {"分配权限", "增删成员", "增删角色", "发布公告", "回收站", "删除空间"};
	
	
	/**
	 * 预定义空间权限
	 */
	// 空间管理员
	public final static long SPACE_MANAGER = 0xffffffffffffffffL; 
	
	/**
	 * 根据权限矩阵关联表，设置某个权限时候，包含权限自动设置。
	 */
	// 设置赋值权限，权限的再分配 
	public final static long PERMISSION_SET = (1L << PERMISSION) | (1L << CREATE_ROLE) | (1L << TRASH) | (1L << ADD_MEMBER);
	// 设置增加成员权限
	public final static long ADD_MEMBER_SET = (1L << ADD_MEMBER);
	// 设置增删角色权限
	public final static long CREATE_ROLE_SET = (1L << CREATE_ROLE);
	// 设置发布公告权限
	public final static long PUBLIC_BULLETIN_SET = (1L << PUBLIC_BULLETIN);               
	// 设置回收站权限
	public final static long TRASH_SET = (1L << TRASH);
	// 设置删除空间权限
	public final static long DELETE_SPACE_SET = (1L << DELETE_SPACE);
	
	/**
	 * 空间权限判断的短路值
	 */
	// 分配权限短路值
	public final static long PERMISSION_FLAG = (1L << PERMISSION);
	// 删除空间短路值
	public final static long DELETE_SPACE_FLAG = (1L << DELETE_SPACE) | PERMISSION_FLAG;
	// 增加成员短路值
	public final static long ADD_MEMBER_FLAG = (1L << ADD_MEMBER) | PERMISSION_FLAG;
	// 发布公告的短路值。
	public final static long PUBLIC_BULLETIN_FLAG = (1L << PUBLIC_BULLETIN) | PERMISSION_FLAG;
	// 增删角色短路值
	public final static long CREATE_ROLE_FLAG = (1L << CREATE_ROLE) | PERMISSION_FLAG;
	// 回收站操作短路值
	public final static long TRASH_FLAG = (1L << TRASH) | PERMISSION_FLAG;
	
	
	/****************************************
	 *             节点类型常量定义
	 ****************************************
	 */
	/**
	 * 节点类型为空间
	 */
	public final static int SPACE = 0;                        // 0
	/**
	 * 节点类型为文件夹
	 */
	public final static int FOLDER = SPACE + 1;              // 1
	/**
	 * 节点类型为文件
	 */
	public final static int FILE = FOLDER + 1;              // 2

	/************************************************************************
	 *             节点类型为空间类型时候，具体空间的类型常量定义
	 ************************************************************************
	 */
	public final static int USER_SPACE = 0;					   //0 用户空间 
	public final static int ORG_SPACE = USER_SPACE + 1;        // 1 组织空间
	public final static int GROUP_SPACE = ORG_SPACE + 1;       // 2 组空间
	public final static int TEAM_SPACE = GROUP_SPACE + 1;      // 3 用户自定义空间
	
}
