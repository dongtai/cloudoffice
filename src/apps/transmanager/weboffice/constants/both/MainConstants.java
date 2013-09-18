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
public interface MainConstants
{
	int SYSTEM_LOGIN = 0;
	int LDAP_LOGIN = SYSTEM_LOGIN + 1;
	int SSO_LOGIN = LDAP_LOGIN + 1;
	
    // 文档审批状态
    public static int APPROVAL_STATUS_NO = -1; // 没有送审    
    public static int APPROVAL_STATUS_PAENDING =  APPROVAL_STATUS_NO + 1; // 待审批
    public static int APPROVAL_STATUS_ACTIVE = APPROVAL_STATUS_PAENDING + 1; // 申批中
    public static int APPROVAL_STATUS_COMPLETED = APPROVAL_STATUS_ACTIVE + 1;// 已完成
    public static int APPROVAL_STATUS_ABORTED = APPROVAL_STATUS_COMPLETED + 1;// 已终止
    // 文档审查Action
    public static final int APPROVAL_ACTION_NO = -2;// 提交
    public static final int APPROVAL_ACTION_PAENDING = APPROVAL_ACTION_NO + 1;//  -1 = 提交
    public static final int APPROVAL_ACTION_PASS = APPROVAL_ACTION_PAENDING + 1; // 0 = 核准
    public static final int APPROVAL_ACTION_REJECT = APPROVAL_ACTION_PASS + 1; // 1 = 拒绝
    // 送审文档做版本控制
    public static final String APPROVAL_VERSION_COMMENT = "送审文档";
    public static final String APPROVAL_VERSION_STATUS_CURRENT = "当前";
    // 送审文档短信通知
    public static final String APPROVEL_NOTICE_MESSAGE = "你有一个文档审批任务";
    public static final String APPROVEL_NOTICE_EMAIL = "你有一个文档审查任务";
    
    public static final String PERSON_DOCUMENT = "我的文库";
    public static final String PROJECT_DOCUMENT = "协作项目";
    
    public static final String TEAM_DOCUMENT = "(共享)";
    
    public static final String GROUP_DOCUMENT = "公文库";
    
}
