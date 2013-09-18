package apps.transmanager.weboffice.client.constant;

import apps.transmanager.weboffice.constants.both.FileSystemCons;


public interface MainConstant
{
	public final static int SYSTEM_LOGIN = 0;
    // 登录界面
    public static int LOGIN_PANEL = 0;
    // 文件管理界面
    public static int FILEMANAGE_PANEL = 1;
    // 我的空间界面
    public static int MYSPACE_PANEL = 2;
    // 上传界面
    public static int UPDATE_PANEL = 3;
    
    // 用户管理界面split左测窗体最小宽度
    public static int MIN_SPLIT_WIDTH = 270;//185;
    public static int MAX_SPLIT_WIDTH = MIN_SPLIT_WIDTH;
    public static int HEAD_WIDTH = MIN_SPLIT_WIDTH+7;
    // 头＋尾巴的高度    public static int HF_HEIGHT = 110;//60+3+27+30
    //缓冲大小，用于前台iframe给定高度的缓冲高度
    public static int CACHE_HEIGHT = 37;
    //大家正在说面板高度
    public static int CON_PANEL_HEIGHT = 150+5;//150
    // 文件管理界面左右间隔
    public static int LR_SPACE = 40;//20+20;
    
    //以下为文件列表数据第2维的类型
    //文件列表-文件类型
    public static int FILEDATA_FILETYPE = 0;
    //文件列表-星号标记
    public static int FILEDATA_STARMARK = 1;
    //文件列表-文件名称数据
    public static int FILEDATA_FILENAME = 2;
    //文件列表-共享者
    public static int FILEDATA_SHARE = 3;
    //文件列表-文件编辑用户
    public static int FILEDATA_EDITUSER = 4;
    //文件列表-文件状态数据，比如只读
    public static int FILEDATA_FILESTATE = 5;
    //文件列表-文件路径
    public static int FILEDATA_FILEPATH = 6;
    //文件列表-修改时间
    public static int FILEDATA_MODIFYTIME = 7;
    //文件列表-删除时间
    public static int FILEDATA_DELETETIME = 8;
    //文件列表-文件审批状态
    public static int FILEDATA_APPROVAL_STATE = 9;
    //文件列表-文件大小
    public static int FILEDATA_FILESIZE = 10;
    //文件列表-文件实际路径
    public static int FILEDATA_REALPATH = 11;
    //文件列表-共享信息描述
    public static int FILEDATA_SHAREINFO = 12;
    //文件列表-查看
    public static int FILEDATA_FILEVIEW = 13;
    //文件列表-文件权限
    public static int FILEDATA_FILEPERMIT = 14;
    //文件列表-FileStatus，比如打开
    public static int FILEDATA_FILESTATUS = 15;
    //文件列表-Ischeck
    public static int FILEDATA_ISCHECK = 16;
    //文件列表-IsEntrypt
    public static int FILEDATA_ISENTRYPT = 17;
    //文件列表-isHasVersion
    public static int FILEDATA_HASVERSION = 18;
    //文件列表-是否签名
    public static int FILEDATA_ISSIGN = 19;
    //文件列表-VersionCount
    public static int FILEDATA_VERSIONCOUNT = 20;
    //文件列表-创建时间
    public static int FILEDATA_CREATETIME = 21;
    //文件列表-作者
    public static int FILEDATA_AUTHOR = 22;
    //文件列表-共享时间
    public static int FILEDATA_SHARETIME = 23;
    //文件列表-审批动作数
    public static int FILEDATA_APPROVAL_COUNT = 24;
    //文件列表-checkTime
    public static int FILEDATA_CHECKTIME = 25;
    //文件列表-共享信息列表数
    public static int FILEDATA_SHARECOUNT = 26;
    //文件列表-签名个数
    public static int FILEDATA_SIGNCOUNT = 27;
    //属性对话盒需要的文件路径
    public static int FILEDATA_PROP_FILEPATH = 28;
    //文件列表数据第2维的长度，如果有增加，需要及时更新。
    
    // 审阅领导 user266为南京公安局加
    public static int FILEDATA_APPROVEUSER = 29;
    // 审阅结果user266为南京公安局加
    public static int FILEDATA_APPROVERESULT = 30;
    public static int FILEDATA_APPROVECOMM = 31;//审阅意见
    //文件列表数据第2维的长度，如果有增加，需要及时更新。
    public static int FILEDATA_COUNT = 32;
    
    public static int FILE_LISTINFO_LEN = 5;
    
    public static int ADIMI = 1;
    //角色
    public static int LEADING = 2;//领导
    public static int LOCAL_LEADING = 3;//分管领导
    public static int USER = 4;// 非领导
    public static int USER_ADMIN = 5;    // 用户管理员
    public static int AUDIT_ADMIN = 6;   // 审计员
    public static int SECURITY_ADMIN = 7;  // 安全员
    public static int COMPANY_ADMIN = 8;   // 单位公司管理员
    public static int PART_ADMIN = 9;   // 部门管理员,用户企业文库和编辑部门人员信息，及查看本部门的日志
    
//    public static String ADIMI_NAME = ApplicationParameters.instance.webMessage.sysAdmin();
//    public static String USER_ADMIN_NAME = ApplicationParameters.instance.webMessage.userAdmin();
//    public static String AUDIT_ADMIN_NAME = ApplicationParameters.instance.webMessage.auditAdmin();    
//    public static String SECURITY_ADMIN_NAME= ApplicationParameters.instance.webMessage.secAdmin();   
//    
////    public static int COMMON = 4;//平民
//    public static String[] ROLE_NAMES = new String[]{ApplicationParameters.instance.webMessage.role_0(), ApplicationParameters.instance.webMessage.role_1(), ApplicationParameters.instance.webMessage.role_2(),ApplicationParameters.instance.webMessage.userAdmin(),ApplicationParameters.instance.webMessage.auditAdmin(),ApplicationParameters.instance.webMessage.secAdmin()};
    
    /** -------------------------------资源常量---------------------------------_*/
    /** 提示信息风格统一，红色表示警示*/
    public static String MSG_RED = "text-align:center;color:red;";
    /** 提示信息风格统一，海绿色表示成功*/
    public static String MSG_SEAGREEN = "color:rgb(51,153,102);";
    /** 对话框正中区域背景风格*/
    public static String BODYSTYLE_DLG = "background-color:white";
    /** 文件名不支持的字符*/
    public static String ERROR_NAME_TEXT = "@/:|*[]\\*'?\"<>";//"/\\:*'?\"<>|";
    /** 文件路径最大可视长度*/
    public static int PATH_MAXLENGTH = 20;
    /** 对话框提示信息风格统一：14px，底色：tomato改成无，颜色：white改成红色，居中*/
    public static String DLG_STYLE_MSG = "font-size:12px;background-color:white;color:red;text-align:center;vertical-align:middle";
    /*上传文件限制提示样式*/
    public static String DLG_STYLE_PROMPT = "font-size:12px;background-color:white;color:green;text-align:center;vertical-align:middle";
    /** 对话框灰色标签风格*/
//    public static String DLG_STYLE_GRAY = "font-size:14px;color:#999999";
    /** 对话框标签风格统一,菜单项使用：14px*/
    public static String DLG_STYLE_LABEL = "font-size:12px;font-family:宋体";
    
    /** 对话框标签风格统一：12px*/
    public static String DLG_STYLE_LABEL_12 = "font-size:12px;font-family:宋体";
//    public static String fontStyle0 = "<font style='text-align:left;font-size:12px;'>";
    public static String fontStyle1 = "<font style='text-align:left;font-size:12px;font-family:宋体'>";
    public static String fontStyle0 = fontStyle1;
    public static String fontFace1 = "<b><font face='黑体' size='3'>";
    public static String fontFace2 = "</font></b>";
    public static String fontStyle_12 = fontStyle1;//"<font style=\"font-size:12px;\">";
    public static String fontStyle2 = "</font>";
    public static String DOC_BORDER = "border-right:solid 1px #d9d9d9;";
    
    public static String style1 = "style='text-align:left;font-size:12px;font-family:宋体'";
    public static String style2 = "style='text-align:left;font-size:12px;font-family:宋体'";
    
    //超链接面板样式
    public static String fontStyleHref_1 = "<font style='font-size:12px;font-family: 宋体;line-height: 200%;color:#444444;text-overflow:ellipsis; white-space:nowrap;word-break:keep-all;overflow:hidden;'>"; 
    public static String fontStyleHref_2 = "<font style='font-size:12px;font-family: 宋体;color:black;line-height: 200%;text-overflow:ellipsis; white-space:nowrap;word-break:keep-all;overflow:hidden;'>";
    public static String fontStyleHref_3 = "<font style='color:white;font-size:12px;font-family: 宋体;line-height: 200%;text-overflow:ellipsis; white-space:nowrap;word-break:keep-all;overflow:hidden;'>";

    // 左侧面板
    public static final int TYPE_MYFOLDER = 0;
    public static final int TYPE_GC = 1;
    public static final int TYPE_MYSHARE = 2;
    public static final int TYPE_OTHERSHARE = 3;
    public static final int TYPE_MYTAG = 4;
    public static final int TYPE_CURRENTFILE = 5;
    public static final int TYPE_SEARCH = 6;
    public static final int TYPE_STARMARK = 7;
    public static final int TYPE_PUBLICWORKSPACE = 8;
    //临时用，由于分页的原因，需要文件总大小
//    public static final int TYPE_TEMP = 8;
    
	public static int TREE_FILE = 0;
	public static int TREE_MYSHARE = 1;
	public static int TREE_MOVE = 2;
	public static int TREE_SEARCH = 3;
	public static int TREE_LINK = 7 ;
	public static int COMPANYTREE_FILE = 4;
	public static int DEPARMENTTREE_FILE = 5;
	public static int DEPARMENTSHARETREE_FILE = 6;
	
	// 权限所在的bit位
	// 将要删除。
	public static final int ISWRITE_BIT = FileSystemCons.WRITE;//读写
    public static final int ISDOWN_BIT =  FileSystemCons.DOWNLOAD;//下载    
    public static final int ISOPEN_BIT =  31;     // 好像是文件正被打开
    public static final int ISSHARE_BIT =  30;    //  好像是文件正被共享
    public static final int ISLOCK_BIT =  29;    // 好像是文件被锁定
    public static final int CAN_ALL_BIT =  28;//完全控制
    public static final int CAN_SHARE_BIT =  27;//是否可以共享
    public static final int ISAMEND_BIT =  26;// 修订，user266为南京公安局添加
    public static final int ISAPPROVE_BIT =  25;// 审阅，user266为南京公安局添加
    public static final int ISREAD_BIT =  FileSystemCons.READ;//只读
    public static final int CAN_NEW_BIT =  FileSystemCons.NEW;//是否可以新建
    public static final int CAN_MOVE_BIT =  FileSystemCons.MOVE;//是否可以移动
    public static final int CAN_COPY_BIT =  FileSystemCons.COPY_PASTE;//是否可以复制
    public static final int CAN_DEL_BIT =  FileSystemCons.DELETE;//是否可以删除
    public static final int CAN_RENAME_BIT =  FileSystemCons.RENAME;//是否可以重命名
    public static final int CAN_PASTE_BIT =  FileSystemCons.COPY_PASTE;//是否可以黏贴
    public static final int CAN_UPLOAD_BIT =  FileSystemCons.UPLOAD;//是否可以上传
    public static final int CAN_NEVAGATION_BIT =  FileSystemCons.BROWSE;//是否可以浏览
    public static final int CAN_PRINT_BIT =  FileSystemCons.PRINT;//是否可以打印    
    public static final int CAN_AMENT_BIT =  FileSystemCons.AMENT;//修订
    public static final int CAN_APPROVE_BIT =  FileSystemCons.APPROVE;//审批
    
	// 权限部分
    // 将要删除。
    public static final int ISAMEND = 0x00000001 << ISAMEND_BIT;
    public static final int ISAPPROVE = 0x00000001 << ISAPPROVE_BIT;
	public static final int ISWRITE = 0x00000001 << ISWRITE_BIT;//读写
    public static final int ISDOWN = 0x00000001 << ISDOWN_BIT;//下载    
    public static final int ISOPEN = 0x00000001 << ISOPEN_BIT;
    public static final int ISSHARE = 0x00000001 << ISSHARE_BIT;
    public static final int ISLOCK = 0x00000001 << ISLOCK_BIT;
    public static final int ISREAD = 0x00000001 << ISREAD_BIT;//只读
    public static final int CAN_NEW = 0x00000001 << CAN_NEW_BIT;//是否可以新建
    public static final int CAN_MOVE = 0x00000001 << CAN_MOVE_BIT;//是否可以移动
    public static final int CAN_COPY = 0x00000001 << CAN_COPY_BIT;//是否可以复制
    public static final int CAN_DEL = 0x00000001 << CAN_DEL_BIT;//是否可以删除
    public static final int CAN_RENAME = 0x00000001 << CAN_RENAME_BIT;//是否可以重命名
    public static final int CAN_SHARE = 0x00000001 << CAN_SHARE_BIT;//是否可以共享
    public static final int CAN_PASTE = 0x00000001 << CAN_PASTE_BIT;//是否可以黏贴
    public static final int CAN_UPLOAD = 0x00000001 << CAN_UPLOAD_BIT;//是否可以上传
    public static final int CAN_NEVAGATION = 0x00000001 << CAN_NEVAGATION_BIT;//是否可以浏览
    public static final int CAN_PRINT =  0x00000001 << CAN_PRINT_BIT;//是否可以打印    
    public static final int CAN_ALL =  0x00000001 << CAN_ALL_BIT;//完全控制
    
    public static final int ISAMENT  =  0x00000001 << CAN_AMENT_BIT;//修订
//    public static final int ISAPPROVE = 0x00000001 << CAN_APPROVE_BIT;//审阅
    
    public static final int ALL_PERMIT = 0xffffffff & 0x00ffffff;  // 所有权限
    
    //从ShareConstant类移过来的，还没有看用处
    public static final int PERSIZE = 30;
    public static final Integer READONLY = 0 ;// 只读
    public static final Integer READANDWRITE = 1;// 读写
//        
//      public static String NEW_TEMPLATES = ApplicationParameters.instance.webMessage.new_templates();
//      //template
//      public static String TEMPLATES_1 = ApplicationParameters.instance.webMessage.templates_1();
//      public static String TEMPLATES_2 = ApplicationParameters.instance.webMessage.templates_2();
//      public static String TEMPLATES_3 = ApplicationParameters.instance.webMessage.templates_3();
//      public static String TEMPLATES_4 = ApplicationParameters.instance.webMessage.templates_4();
//      public static String TEMPLATES_5 = ApplicationParameters.instance.webMessage.templates_5();
//      public static String TEMPLATES_6 = ApplicationParameters.instance.webMessage.templates_6();
//      public static String TEMPLATES_7 = ApplicationParameters.instance.webMessage.templates_7();
//      public static String TEMPLATES_8 = ApplicationParameters.instance.webMessage.templates_8();
//      public static String TEMPLATES_9 = ApplicationParameters.instance.webMessage.templates_9();
//      public static String TEMPLATES_10 = ApplicationParameters.instance.webMessage.templates_10();
//      public static String TEMPLATES_11 = ApplicationParameters.instance.webMessage.templates_11();
//      public static String TEMPLATES_12 = ApplicationParameters.instance.webMessage.templates_12();
//      public static String TEMPLATES_13 = ApplicationParameters.instance.webMessage.templates_13();
//      public static String TEMPLATES_14 = ApplicationParameters.instance.webMessage.templates_14();
//      public static String TEMPLATES_15 = ApplicationParameters.instance.webMessage.templates_15();
//      public static String TEMPLATES_16 = ApplicationParameters.instance.webMessage.templates_16();
//      public static String TEMPLATES_17 = ApplicationParameters.instance.webMessage.templates_17();
//      public static String TEMPLATES_18 = ApplicationParameters.instance.webMessage.templates_18();
//      public static String TEMPLATES_19 = ApplicationParameters.instance.webMessage.templates_19();
//      public static String TEMPLATES_20 = ApplicationParameters.instance.webMessage.templates_20();
//      public static String TEMPLATES_21 = ApplicationParameters.instance.webMessage.templates_21();
//      public static String TEMPLATES_22 = ApplicationParameters.instance.webMessage.templates_22();
//      public static String TEMPLATES_23 = ApplicationParameters.instance.webMessage.templates_23();
//      public static String TEMPLATES_24 = ApplicationParameters.instance.webMessage.templates_24();
////    //20个模板名字//    public static String TEMPLATES_NAME_1 = ApplicationParameters.instance.webMessage.templates_name_1();
//    public static String TEMPLATES_NAME_2 = ApplicationParameters.instance.webMessage.templates_name_2();
//    public static String TEMPLATES_NAME_3 = ApplicationParameters.instance.webMessage.templates_name_3();
//    public static String TEMPLATES_NAME_4 = ApplicationParameters.instance.webMessage.templates_name_4();
//    public static String TEMPLATES_NAME_5 = ApplicationParameters.instance.webMessage.templates_name_5();
//    public static String TEMPLATES_NAME_6 = ApplicationParameters.instance.webMessage.templates_name_6();
//    public static String TEMPLATES_NAME_7 = ApplicationParameters.instance.webMessage.templates_name_7();
//    public static String TEMPLATES_NAME_8 = ApplicationParameters.instance.webMessage.templates_name_8();
//    public static String TEMPLATES_NAME_9 = ApplicationParameters.instance.webMessage.templates_name_9();
//    public static String TEMPLATES_NAME_10 = ApplicationParameters.instance.webMessage.templates_name_10();
//    public static String TEMPLATES_NAME_11 = ApplicationParameters.instance.webMessage.templates_name_11();
//    public static String TEMPLATES_NAME_12 = ApplicationParameters.instance.webMessage.templates_name_12();
//    public static String TEMPLATES_NAME_13 = ApplicationParameters.instance.webMessage.templates_name_13();
//    public static String TEMPLATES_NAME_14 = ApplicationParameters.instance.webMessage.templates_name_14();
//    public static String TEMPLATES_NAME_15 = ApplicationParameters.instance.webMessage.templates_name_15();
//    public static String TEMPLATES_NAME_16 = ApplicationParameters.instance.webMessage.templates_name_16();
//    public static String TEMPLATES_NAME_17 = ApplicationParameters.instance.webMessage.templates_name_17();
//    public static String TEMPLATES_NAME_18 = ApplicationParameters.instance.webMessage.templates_name_18();
//    public static String TEMPLATES_NAME_19 = ApplicationParameters.instance.webMessage.templates_name_19();
//    public static String TEMPLATES_NAME_20 = ApplicationParameters.instance.webMessage.templates_name_20();
//    
//    //信电局模板
//    public static String TEMPLATES_XT_1 = ApplicationParameters.instance.webMessage.templates_xt_1();
//    public static String TEMPLATES_XT_2 = ApplicationParameters.instance.webMessage.templates_xt_2();
//    public static String TEMPLATES_XT_3 = ApplicationParameters.instance.webMessage.templates_xt_3();
//    public static String TEMPLATES_XT_4 = ApplicationParameters.instance.webMessage.templates_xt_4();
//    public static String TEMPLATES_XT_5 = ApplicationParameters.instance.webMessage.templates_xt_5();
//    
//    //旅游局模板
//    public static String TEMPLATES_LT_1 = ApplicationParameters.instance.webMessage.templates_lt_1();
//    public static String TEMPLATES_LT_2 = ApplicationParameters.instance.webMessage.templates_lt_2();
//    public static String TEMPLATES_LT_3 = ApplicationParameters.instance.webMessage.templates_lt_3();
    
//    public static String DOC_LEADER_AU_UR = "DOC_LEADER_AU_UR";    // 主要领导已审急件文件夹
//    public static String DOC_LEADER_AU_NOR = "DOC_LEADER_AU_NOR";    // 主要领导已审普通文件夹
//    public static String DOC_LEADER_WT_UR = "DOC_LEADER_WT_UR";    // 主要领导待批急件文件夹
//    public static String DOC_LEADER_WT_NOR = "DOC_LEADER_WT_NOR";    // 主要领导待批普通文件夹
//    public static String DOC_VI_LEADER_AU_UR = "DOC_VI_LEADER_AU_UR";    // 分管领导已审急件文件夹
//    public static String DOC_VI_LEADER_AU_NOR = "DOC_VI_LEADER_AU_NOR";    // 分管领导已审普通文件夹
//    public static String DOC_VI_LEADER_WT_UR = "DOC_VI_LEADER_WT_UR";    // 分管领导待批急件文件夹
//    public static String DOC_VI_LEADER_WT_NOR = "DOC_VI_LEADER_WT_NOR";    // 分管领导待批普通文件夹
//    public static String DOC_DEPARTMENT = "DOC_DEPARTMENT";    // 部门空间的根目录
    public static String DOC_PUBLIC = "DOC_PUBLIC";           // 公共工作空间
    
//    public static String ZYLDYSWJJ = "主管领导已审文件夹";    // 主要领导已审文件夹
//    public static String ZYLDDPWJJ = "主管领导待批文件夹";    // 主要领导待批文件夹
//    public static String FGLDYSWJJ = "分管领导已审文件夹";    // 分管领导已审文件夹
//    public static String FGLDDPWJJ = "分管领导待批文件夹";    // 分管领导待批文件夹
    
    public static String ENTERPOINT = "entryPoint";             // 进入点参数名
    public static String WORKSPACE = "workspace";     // 工作空间文件页面
    public static String REGISTER = "register";       // 注册页面
    public static String INDEXPAGE = "indexpage";     // 首页面
    public static String MANAGE = "manage";           // 后台管理页面
    
    
//    public static String ZGLD = "主管领导";
    
//    public static String APPROVESPACE = "审阅空间";   
//    public static String PUBLIC_SPACE = ApplicationParameters.instance.webMessage.public_space();
//    public static String ADDUSER = ApplicationParameters.instance.webMessage.addUser();
//    public static String SHARESPACE = ApplicationParameters.instance.webMessage.shareSpace();
//    public static String PUBLICWORKSPACE = ApplicationParameters.instance.webMessage.publicworkspace();
//    public static String WORKFLOWFILESPACE = ApplicationParameters.instance.webMessage.workflowfilespace();
//    public static String DEPARMENTFOLDER = ApplicationParameters.instance.webMessage.departementFolder();
//    public static String LATELYSHARE = ApplicationParameters.instance.webMessage.latelyShare();
//    public static String MYSPACE = ApplicationParameters.instance.webMessage.myspace();
//    public static String JOBSPACE = ApplicationParameters.instance.webMessage.jobspace();
//    public static String WORKDOC = ApplicationParameters.instance.webMessage.workdoc();
//    public static String POWER = ApplicationParameters.instance.webMessage.power();
//    public static String BROWSE = ApplicationParameters.instance.webMessage.browse();
//    public static String ERROR = ApplicationParameters.instance.webMessage.error();
//    public static String ERROR_1 = ApplicationParameters.instance.webMessage.error_1();
//    public static String ERROR_2 = ApplicationParameters.instance.webMessage.error_2();
//    public static String ERROR_3 = ApplicationParameters.instance.webMessage.error_3();
//    public static String ERROR_4 = ApplicationParameters.instance.webMessage.error_4();
//    public static String ERROR_5 = ApplicationParameters.instance.webMessage.error_5();
//    public static String ERROR_6 = ApplicationParameters.instance.webMessage.error_6();
//    public static String ERROR_7 = ApplicationParameters.instance.webMessage.error_7();
//    public static String ERROR_8 = ApplicationParameters.instance.webMessage.error_8();
//    public static String DEPFOLDER =ApplicationParameters.instance.webMessage.depfolder();
//    public static String FILE =ApplicationParameters.instance.webMessage.file();
//    public static String FILE1 = ApplicationParameters.instance.webMessage.file1();
    
    public static int MODEL_MYSPACE = 0;
    public static int MODEL_SHARESPACE = 1;
    public static int MODEL_PUBLICWORKSPACE = 2;
    // 流程管理模块
    public static int MODEL_WORKFLOW = 3;
    // user266
    public static int MODEL_MYREPORT = 4;
    
//    public static String FILEPATH = ApplicationParameters.instance.webMessage.filepath();
//    public static String MERGER = ApplicationParameters.instance.webMessage.merger();
//    
//    public static String GROUP_CODE = ApplicationParameters.instance.webMessage.group_code();
//    public static String USER_CODE = ApplicationParameters.instance.webMessage.user_code();
//    public static String PGROUP = ApplicationParameters.instance.webMessage.pgroup();
//    public static String ISREPEAT = ApplicationParameters.instance.webMessage.isrepeat2();
//    public static String WEBADDRESS = ApplicationParameters.instance.webMessage.webaddress();
    public static String DEFAULTNUM="10000";
    
    
//    public static String XT = ApplicationParameters.instance.webMessage.xt();
//    public static String LT = ApplicationParameters.instance.webMessage.lt();

    
    public static String LOGROOT = "WEB-INF/";

    
//    public static String MOVEFIlE = ApplicationParameters.instance.webMessage.moveFile();
//    public static String OPENERROR = ApplicationParameters.instance.webMessage.openError();
//    public static String OPENERROR2 = ApplicationParameters.instance.webMessage.openError2();
//    public static String OPENERROR3 = ApplicationParameters.instance.webMessage.openError3();
    // 系统报表
    public static String SYS_REPORT_DATE = "";//ApplicationParameters.instance.webMessage.sys_report_DATE();
    public static String SYS_REPORT_ACCOUNT_COUNT = "";//ApplicationParameters.instance.webMessage.sys_report_ACCOUNT_COUNT();
    public static String SYS_REPORT_SPACE_COUNT = "";//ApplicationParameters.instance.webMessage.sys_report_SPACE_COUNT();
    public static String SYS_REPORT_DOCUMENT_COUNT = "";//ApplicationParameters.instance.webMessage.sys_report_DOCUMENT_COUNT();
    public static String SYS_REPORT_CONTENT_SIZE = "";//ApplicationParameters.instance.webMessage.sys_report_CONTENT_SIZE();
    
    //视图类型
    public static String VIEW_TABLE = "view_table";//表格类型
    public static String VIEW_LIST = "view_large_list";//大视图类型
    // 文档审批状态
    public static final int APPROVAL_STATUS_NO = -1; // 没有送审    
    public static final int APPROVAL_STATUS_PAENDING =  APPROVAL_STATUS_NO + 1; // 0 = 待审批
    public static final int APPROVAL_STATUS_ACTIVE = APPROVAL_STATUS_PAENDING + 1; // 1 = 申批中
    public static final int APPROVAL_STATUS_COMPLETED = APPROVAL_STATUS_ACTIVE + 1;//  2 = 已完成
    public static final int APPROVAL_STATUS_ABORTED = APPROVAL_STATUS_COMPLETED + 1;// 3 = 已终止
    // 文档审查Action
    public static final int APPROVAL_ACTION_NO = -2;// 提交
    public static final int APPROVAL_ACTION_PAENDING = APPROVAL_ACTION_NO + 1;//  -1 提交
    public static final int APPROVAL_ACTION_PASS = APPROVAL_ACTION_PAENDING + 1; // 0 核准
    public static final int APPROVAL_ACTION_REJECT = APPROVAL_ACTION_PASS + 1; // 1 = 拒绝
    
}
