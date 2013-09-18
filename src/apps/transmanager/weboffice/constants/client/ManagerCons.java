package apps.transmanager.weboffice.constants.client;

/**
 * 后台管理用到的常量
 * <p>
 * <p>
 * 负责小组:        WEB
 * <p>
 * <p>
 */
public class ManagerCons
{
    public static final int WIDTH = 900;
    public static final int HEIGHT = 350;
    public static final int INSIDE_WIDTH = 660;
    public static final int HF_SPACE = 81+33;
    public static final int LABEL_HEIGHT = 25;
    public static final int ROW_SPACE = 20;
    //注意：被禁止的用户的role是为2.
    public static final short FORBIDUSER = 0;
    public static final short COMMON_USER = 1;
    
    //以下几个字段需要跟后台数据库中AdminUserinfoView表中的字段一一对应。user663
	public static String userName = "userName";
    public static String email = "email";
    public static String storageSize = "storageSize";
    public static String duty = "duty";
    public static String role = "role";
    
    //统一设置"*"的字号、颜色等属性。
    public static String SETSTYLEXH = "</font></p>" +"<p><font style=\"font-size: 16px;color:red\">"+"*"+"</font></p>";
	
	//以下几个产量跟bean属性绑定。
	public static String LOG_DATALOGINTIME = "loginTime";
	public static String LOG_DATAQUITIME = "quitTime";
	public static String LOG_DATALLTIME = "allTime";
	
    public static String department = "department";
    public static String sort = "sort";
    
    public static int BACKUP_TYPE_ALL = 0; // 整库备份
    public static int BACKUP_TYPE_INCREMENT = 1; //增量备份
    public static int BACKUP_TASKTYPE_DAY = 0; //每天
    public static int BACKUP_TASKTYPE_WEEK = 1; //每周
    public static int BACKUP_TASKTYPE_MONTH = 2; //每月
    public static int BACKUP_TASKTYPE_ALL = 3; //一次性
	
}
