package apps.transmanager.weboffice.constants.both;

/**
 * 文件资源常量定义
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
// 禁止继承类。
public final class FileSystemCons
{
	/**
	 * 禁止实例化类
	 */
	private FileSystemCons()
	{
	}
	
	/**
	 * 权限定义位置
	 */	
	//public final static int ALL = 0;                 // 完全控制
	public final static int BROWSE = 0;              // 0 浏览
	public final static int READ = BROWSE + 1;        // 1 读
	public final static int WRITE = READ + 1;        // 2 写
	public final static int SAVE_AS = WRITE + 1;     // 3 另存
	public final static int NEW = SAVE_AS + 1;       // 4 新建
	public final static int DOWNLOAD = NEW + 1;      // 5下载
	public final static int PRINT = DOWNLOAD + 1;    // 6 打印
	public final static int RENAME = PRINT + 1;      // 7 改名
	public final static int DELETE = RENAME + 1;     // 8 删除
	public final static int UPLOAD = DELETE + 1;     // 9 上传
	public final static int COPY_PASTE = UPLOAD + 1;     // 10 复制粘贴
	public final static int LOCAL_EDIT = COPY_PASTE + 1;  // 11 离线编辑
	public final static int VERSION = LOCAL_EDIT + 1;     // 12 版本
	public final static int CHECK = VERSION + 1;          // 13 check out/check in
	public final static int SEND = CHECK + 1;             // 14 发送
	public final static int AUDIT = SEND + 1;             // 15 审批
	public final static int MOVE = AUDIT + 1;             // 16 移动
	
	public final static int AMENT = MOVE + 1;    // 17修订
	public final static int APPROVE = AMENT + 1;     // 18审阅
	
	
	
	// 先定义在此，后续移到相应的位置。具体的内容位置需要同上面的常量位置对应一致
	public final static String[] FILE_SYSTEM_ACTION_NAME = {"浏览", "读", "写", "另存", "新建", 
			"下载", "打印", "重命名", "删除", "上传", "复制粘贴", "离线编辑", "版本", "锁定/解锁", "发送", "审批",
			"移动"};
	
	
	
	/**
	 * 几种预定义的权限集合
	 */
	// 读者权限
	public final static long  BROWSER = (1L << BROWSE);
	// 读者权限
	public final static long  READER = (1L << READ) | (1L << BROWSE)  | (1L << UPLOAD);
	// 作者权限
	public final static long AUTHOR = (1L << READ) | (1L << WRITE) | (1L << SAVE_AS) | (1L << DELETE) | (1L << UPLOAD);
	// 空间管理者权限
	public final static long SPACE_MANAGER = 0xffffffffffffffffL;
	/**
	 * 根据权限矩阵关联表，设置某个权限时候，包含权限自动设置。
	 */
	// 设置浏览权限
	public final static long BROWSE_SET = (1L << BROWSE);
	// 设置只读权限
	public final static long READ_SET = (1L << READ);
	// 设置另存权限
	public final static long SAVE_AS_SET = (1L << SAVE_AS) | (1L << WRITE);
	// 设置写权限
	public final static long WRITE_SET = (1L << WRITE) | (1L << READ);
	// 设置copy/paste权限
	public final static long COPY_PASTE_SET = (1L << COPY_PASTE);
	// 设置离线权限
	public final static long LOCAL_EDIT_SET = (1L << LOCAL_EDIT) | (1L << DOWNLOAD) | (1L << UPLOAD);
	// 设置下载权限
	public final static long DOWNLOAD_SET = (1L << DOWNLOAD) | (1L << READ);
	// 设置shangc权限
	public final static long UPLOAD_SET = (1L << UPLOAD) | (1L << BROWSE);
	// 设置打印权限
	public final static long PRINT_SET = (1L << PRINT) | (1L << READ);
	// 设置审核权限
	public final static long AUDIT_SET = (1L << AUDIT) | (1L << READ);
	// 设置check in/ check out权限
	public final static long CHECK_SET = (1L << CHECK) | (1L << READ);
	// 设置版本权限
	public final static long VERSION_SET = (1L << VERSION) | (1L << READ); 
	// 设置删除权限
	public final static long DELETE_SET = (1L << DELETE) | (1L << RENAME);
	// 设置更名权限
	public final static long RENAME_SET = (1L << RENAME) | (1L << BROWSE);
	// 设置新建权限
	public final static long NEW_SET = (1L << NEW) |  (1L << BROWSE);	
	// 设置发送权限
	public final static long SEND_SET = (1L << SEND);
	// 设置移动权限
	public final static long MOVE_SET = (1L << MOVE) | (1L << COPY_PASTE) | (1L << DELETE);
	
	
	/**
	 * 权限判断短路值。
	 * 根据权限矩阵定义得到各个权限的短路值，
	 * 这样，在外界判断是否有某个权限的时候，只需要根据权限的相应短路值进行判断即可，
	 * 而不需要一步步的判断权限矩阵表。
	 * 例如：
	 * 如果判断READ权限，则仅仅需要判断READ的短路值READ_FLAG，即可知道是否具有READ
	 * 权限，此时外界就不需要先判断READ位，再判断WRITE位，再判断其他相关位。
	 * 
	 */		
	// 完全控制短路值
	//public final static long ALL_FLAG =  (1L << ALL);
	// 发送的短路值
	public final static long SEND_FLAG = (1L << SEND);
	// 另存短路值
	public final static long SAVE_AS_FLAG = (1L << SAVE_AS);       // | ALL_FLAG;
	// 写短路值
	public final static long WRITE_FLAG = (1L << WRITE) | SAVE_AS_FLAG;       //  | ALL_FLAG;
	// 离线编辑短路值
	public final static long LOCAL_EDIT_FLAG = (1L << LOCAL_EDIT);	
	//  下载短路值
	public final static long DOWNLOAD_FLAG = (1L << DOWNLOAD) | LOCAL_EDIT_FLAG;       //  | ALL_FLAG;
	// 上传短路值
	public final static long UPLOAD_FLAG = (1L << UPLOAD) | LOCAL_EDIT_FLAG;       // | ALL_FLAG;	
	//  打印短路值
	public final static long PRINT_FLAG = (1L << PRINT);       // | ALL_FLAG;
	// 审批短路值
	public final static long AUDIT_FLAG = (1L << AUDIT);
	// 移动短路值
	public final static long MOVE_FLAG = (1L << MOVE);
	// 锁定/解锁短路值
	public final static long CHECK_FLAG = (1L << CHECK);
	// 版本短路值
	public final static long VERSION_FLAG = (1L << VERSION);
	//  删除短路值
	public final static long DELETE_FLAG = (1L << DELETE) | MOVE_FLAG;       // | ALL_FLAG;
	// 更名短路值
	public final static long RENAME_FLAG = (1L << RENAME) | DELETE_FLAG;       // | ALL_FLAG;
	// 复制短路值
	public final static long COPY_PASTE_FLAG = (1L << COPY_PASTE) | MOVE_FLAG;       // | ALL_FLAG;
	// 新建
	public final static long NEW_FLAG = (1L << NEW);
    //  读短路值
	public final static long READ_FLAG = (1L << READ) | WRITE_FLAG | PRINT_FLAG 
			| DOWNLOAD_FLAG | AUDIT_FLAG | CHECK_FLAG | VERSION_FLAG | SEND_FLAG;       // | ALL_FLAG;
	// 浏览短路值
	public final static long BROWSE_FLAG = (1L << BROWSE) | RENAME_FLAG | READ_FLAG | NEW_FLAG | UPLOAD_FLAG | SEND_FLAG;       // | ALL_FLAG;
	
	/**
	 * 文件系统资源
	 */
	public final static int USER_OWN = 0;                      // 0 文件资源是用户拥有
	public final static int GROUP_OWN = USER_OWN + 1;         // 1 文件资源是组拥有
	public final static int ORG_OWN = GROUP_OWN + 1;           // 2 文件资源是组织拥有 
	public final static int TEAM_OWN = ORG_OWN + 1;            // 3 文件资源是用户自定义拥有 
	public final static int COMPANY_OWN = TEAM_OWN + 1;            // 4 文件资源是公司拥有
	
	
	/**
	 * 文件打开方式。
	 */
	public final static int EDIT_TYPE = 0;                     // 编辑方式 0
	public final static int READ_HTML_TYPE = EDIT_TYPE + 1;     // 图片阅读方式1
	public final static int READ_PIC_TYPE = READ_HTML_TYPE + 1;   // html阅读方式2。
	public final static int DESKTOP_EDIT_TYPE = READ_PIC_TYPE + 1;  // 桌面编辑方式3
	
}
