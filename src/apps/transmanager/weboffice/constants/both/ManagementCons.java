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
// 禁止继承类。
public final class ManagementCons
{
	/**
	 * 禁止实例化类
	 */
	private ManagementCons()
	{
	}    
	
	/**
	 * 权限定义位置!
	 * 
	 * 在正式运行后，为了保持权限的前后兼容，该位置顺序不可改变！
	 * 
	 * 如果后续需要增加权限，则在该常量后继续增加即可。
	 * 如果后续需要删除权限，则需要保证原有的位置不变，对显示的内容可以以空串表示，外界使用需要过滤。
	 */
	//public final static int ALL = 0;                              // 完全控制
	public final static int PERMISSION = 0;                       // 0 赋值权限，权限的再分配
	public final static int CREATE_USER = PERMISSION + 1;         // 1增删用户
	public final static int MODIFY_USER = CREATE_USER + 1;        // 2修改用户 
	public final static int CREATE_GROUP = MODIFY_USER + 1;       // 3新增组	
	public final static int MODIFY_GROUP = CREATE_GROUP + 1;      // 4 修改组
	public final static int CREATE_ROLE = MODIFY_GROUP + 1;       // 5 增删角色 
	public final static int DISPATCH_ROLE = CREATE_ROLE + 1;      // 6 分配角色
	public final static int CREATE_SPACE = DISPATCH_ROLE + 1;     // 7 增删空间
	public final static int STATIC_LOG = CREATE_SPACE + 1;        // 8 查看日志
	public final static int DELETE_LOG = STATIC_LOG + 1;          // 9 删除日志
	public final static int AUDIT_SEND = DELETE_LOG + 1;          // 10 公文送审权限
	public final static int AUDIT_MANGE = AUDIT_SEND + 1;         // 11 公文管理权限
	public final static int AUDIT_AUDIT = AUDIT_MANGE + 1;        // 12 公文签批权限
	public final static int AUDIT_FILING = AUDIT_AUDIT + 1;       // 13 公文归档权限
    public final static int NOTICE_RELEASE = AUDIT_FILING + 1;		//14  公告发布员
    public final static int NOTICE_AUDIT = NOTICE_RELEASE + 1;		//15  公告审核员
    public final static int RECEPTION_MANAGE = NOTICE_AUDIT + 1;	//16  接待管理
    
    public final static int COLLECT_EDIT = RECEPTION_MANAGE + 1;        // 17 采编
    public final static int COLLECT_EDIT_ANY = COLLECT_EDIT + 1;        // 18 采编分析
    public final static int COLLECT_BAOSONG = COLLECT_EDIT_ANY + 1;        // 19 报送权限
    
    public final static int[] RATIONAL_MODELID=new int[]{RECEPTION_MANAGE};//角色权限需要与模块一起判断，如果，
    	//在模块显示时,根据是否的权限，当前用户的角色判断是否设置这一项，如果没有设置这一项，即使该单位有这个模块，也不会显示-----临时，应该入数据库
    public final static String[] RATIONAL_MODELNAME=new String[]{"recepwork"};//此项要与RATIONAL_MODELID配合使用
    public final static String[] NOTFILT=new String[]{"im","personalfiles","sharefiles","mail","contact","calendar"};//不进行过滤的模块
	
	// 以下为临时增加的一些权限，应该毫无用处,2012-08-07,权限公司动态，改为公文归档权限
	//public final static int COMPANY_NEWS = AUDIT_AUDIT + 1;       // 13 公司动态
	//public final static int OUT_NEWS = COMPANY_NEWS + 1;          // 14 行业资讯
	
//	public final static int OUT_NEWS = AUDIT_FILING + 1;          // 14 行业资讯
//	public final static int BULLINTS = AUDIT_FILING + 1;              // 15 公告中心
//    public final static int GROUPS_NEWS = BULLINTS + 1;           // 16 团队建设
//    public final static int LABOUR_NEWS = GROUPS_NEWS + 1;        // 17 工会服务
//    public final static int VERSION_NEWS = LABOUR_NEWS + 1;        // 18 工会服务
//  public final static int MEETING_RELEASE = NOTICE_AUDIT + 1;		//21 会议等级
//  public final static int MEETING_AUDIT = MEETING_RELEASE + 1;	//22  会议审核
	public final static int ERROR = 63;                 // 该位暂时保留
	
	// 先定义在此，后续移到相应的位置。具体的内容位置需要同上面的常量位置对应一致
	public final static String[] MANAGEMENT_ACTION_NAME = {"分配权限", "增删用户", "修改用户", "增删组", "修改组", 
			 "增删角色", "分配角色", "增删空间", "查看日志", "删除日志", "公文送审", "公文管理", "公文签批",
			 "公文归档","公告发布员","公告审核员","接待管理","采编","采编分析","报送"};
//	 "行业资讯",
//	 "公告中心",
//	 "团队建设", "工会服务", "版本控制",
//	 "会议登记","会议审核",
	
	/**
	 * 几种预定义权限
	 */
	// 管理员
	public final static long SUPER_ADMIN = 0xffffffffffffffffL;
	// 用户管理员
	public final static long SYS_ADMIN = (1L << CREATE_USER) | (1L << MODIFY_USER) | (1L << CREATE_GROUP) | (1L << MODIFY_GROUP);	
	// 空间创建者
	public final static long SPACE_CREATE = (1L << CREATE_SPACE) | (1L << AUDIT_SEND);		
	// 普通人员
	public final static long NORMAL_PEP =  (1L << AUDIT_SEND);
	// 办公室
	public final static long OFFICE_PEP =  (1L << AUDIT_SEND) | (1L << AUDIT_AUDIT);
	// 签批领导
	public final static long LEADER_PEP =  (1L << AUDIT_AUDIT);
	
	/**
	 * 根据权限矩阵关联表，设置某个权限时候，包含权限自动设置。
	 */
	// 设置 赋值权限，权限的再分配权限
	public final static long PERMISSION_SET = 0xffffffffffffffffL;       //(1L << PERMISSION);
	// 设置增删用户权限
	public final static long CREATE_USER_SET = (1L << CREATE_USER);
	// 设置修改用户权限
	public final static long MODIFY_USER_SET =  (1L << MODIFY_USER);
	// 设置新增组权限
	public final static long CREATE_GROUP_SET = (1L << CREATE_GROUP);
	// 设置 修改组权限
	public final static long MODIFY_GROUP_SET = (1L << MODIFY_GROUP);
	// 设置增删角色权限
	public final static long CREATE_ROLE_SET =  (1L << CREATE_ROLE); 
	// 设置分配角色权限
	public final static long DISPATCH_ROLE_SET = (1L << DISPATCH_ROLE); 
	// 设置增删空间权限
	public final static long CREATE_SPACE_SET = (1L << CREATE_SPACE); 
	// 设置查看日志权限
	public final static long STATIC_LOG_SET = (1L << STATIC_LOG);   
	// 设置删除日志权限
	public final static long DELETE_LOG_SET = (1L << DELETE_LOG);         
	// 公文送审权限
	public final static long AUDIT_SEND_SET = (1L << AUDIT_SEND); 
	// 公文管理权限
	public final static long AUDIT_MANGE_SET = (1L << AUDIT_MANGE);   
	// 公文签批限
	public final static long AUDIT_AUDIT_SET = (1L << AUDIT_AUDIT); 
	//  接待管理
	public final static long RECEPTION_MANAGE_ADD = (1L << RECEPTION_MANAGE);
	
	
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
	// 该资源的管理者拥有所有权限
	public final static long MANAGER = 0xffffffffffffffffL;
	// 完全控制短路值
	//public final static long ALL_FLAG = (1L << ALL);
	// 分配权限短路值
	public final static long PERMISSION_FLAG = (1L << PERMISSION);  // | ALL_FLAG;	
	// 修改用户短路值
	public final static long MODIFY_USER_FLAG = (1L << MODIFY_USER) | PERMISSION_FLAG; 
	// 创建用户短路值
	public final static long CREATE_USER_FLAG = (1L << CREATE_USER) | PERMISSION_FLAG;   // | ALL_FLAG;
	// 创建组短路值
	public final static long CREATE_GROUP_FLAG = (1L << CREATE_GROUP) | PERMISSION_FLAG;  // | ALL_FLAG;
	// 修改组短路值
	public final static long MODIFY_GROUP_FLAG = (1L << MODIFY_GROUP) | PERMISSION_FLAG;  // | ALL_FLAG;	
	// 增加角色短路值
	public final static long CREATE_ROLE_FLAG = (1L << CREATE_ROLE) | PERMISSION_FLAG;   // | ALL_FLAG;
	// 空间创建短路值
	public final static long CREATE_SPACE_FLAG = (1L << CREATE_SPACE) | PERMISSION_FLAG;
	// 分配角色短路值
	public final static long DISPATCH_ROLE_FLAG = (1L << DISPATCH_ROLE) | PERMISSION_FLAG;
	// 查看日志短路值
	public final static long STATIC_LOG_FLAG = (1L << STATIC_LOG) | PERMISSION_FLAG;
	// 删除日志短路值
	public final static long DELETE_LOG_FLAG = (1L << DELETE_LOG) | PERMISSION_FLAG;
	// 公文送审短路值
	public final static long AUDIT_SEND_FLAG = (1L << AUDIT_SEND); 
	// 公文管理短路值
	public final static long AUDIT_MANGE_FLAG = (1L << AUDIT_MANGE);   
	// 公文签批短路值
	public final static long AUDIT_AUDIT_FLAG = (1L << AUDIT_AUDIT);
	//公文归档短路值
	public final static long AUDIT_FILING_FLAG  = (1L << AUDIT_FILING);
	
	//	以下为临时增加的一些权限，应该毫无用处
	//public final static long COMPANY_NEWS_FLAG  = (1L << COMPANY_NEWS);       // 13 公司动态
//	public final static long OUT_NEWS_FLAG = (1L << OUT_NEWS);          // 14 行业资讯
//	public final static long BULLINTS_FLAG = (1L << BULLINTS);              // 15 公告中心
//    public final static long GROUPS_NEWS_FLAG = (1L << GROUPS_NEWS);           // 16 团队建设
//    public final static long LABOUR_NEWS_FLAG = (1L << LABOUR_NEWS);        // 17 工会服务
	//  新版公告中心：三种权限状态：普通人员，公告发布员，公告审核员
    public final static long NOTICE_RELEASE_FLAG = (1L << NOTICE_RELEASE);	//19 公告发布员
    public final static long NOTICE_AUDIT_FLAG = (1L << NOTICE_AUDIT);		//20 公告审核员
//    public final static long MEETING_RELEASE_FLAG = (1L << MEETING_RELEASE);	//21 会议登记
//    public final static long MEETING_AUDIT_FLAG = (1L << MEETING_AUDIT);		//22 会议审核
    // 接待管理
    public final static long RECEPTION_ADD_FLAG = (1L << RECEPTION_MANAGE);		//
	
 // 采编短路值
 	public final static long COLLECT_EDIT_FLAG = (1L << COLLECT_EDIT);
 	// 采编分析短路值
 	public final static long COLLECT_EDIT_ANY_FLAG = (1L << COLLECT_EDIT_ANY);
 	public final static long COLLECT_BAOSONG_FLAG = (1L << COLLECT_BAOSONG); //报送权限
}
