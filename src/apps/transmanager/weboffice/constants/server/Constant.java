package apps.transmanager.weboffice.constants.server;

import apps.moreoffice.LocaleConstant;
import apps.transmanager.weboffice.client.constant.MainConstant;

public interface Constant 
{
    public static int READONLY = 0 ;
    public static int READANDWRITE = 1;
	
    public static int GROUP = 0;//组
    public static int PRIVATE = 1;//个人
	
	//管理员配置页面
    public static String ASSISTADDRESS = "assistAddress";
    public static String MAILSERADDRESS = "mailSerAddress";
    public static String MAIL_ADDRESS = "mailAddress";
    public static String PASSWORD = "password";
    public static String PUBLICID = "public";
    public static String COMPANYID = "companyID";
	
    public static int LONG_SUCESS = -1;
    public static int NO_USER = 0; //用户不存在

    public static int PASSWORD_ERROR = 1; //用户密码错误
    public static int COMPANY_ERROR = 2;  //公司ID错误
    public static int NO_PERMIT_USER = 3; //禁止用户
    public static int PERMIT_USER = 4;//正常用户
    public static int HAS_EXISTED = 5;//当前用户已经登陆
    public static int ENROL_FAILE =6;//注册失败
    public static int ENROL_EXITSEMAIL = 7;//注册失败，邮件地址已经存在
    public static int ENROL_EXITSEUSERNAME = 9;//注册失败，用户名已经存在
    public static int ENROL_EXITSEUSERNAMECA = 10;//证书登录名已存在
    public static int TEMP_ERROR = 8;//文件系统异常
    public static int LOGINCA = 10;//CA方式登录
    // 非法license。
    public static int ILLEGAL_LICENSE = TEMP_ERROR + 1;
    // 正确license.
    public static int LEGAL_LICENSE = ILLEGAL_LICENSE + 1;
    // license到期
    public static int LICENSE_END = LEGAL_LICENSE + 1; 
    // 非法license时间
    public static int LICENSE_ILLEGAL_TIME = LICENSE_END + 1;
    // 非法用户在线数
    public static int ONLINE_USER_ILLEGAL = LICENSE_ILLEGAL_TIME + 1;
    // 已经达到最大用户在线数。
    public static int ONLINE_MAX_USER = ONLINE_USER_ILLEGAL + 1; 
    // 用户在线数不满。
    public static int ONLINE_USER_PER = ONLINE_MAX_USER + 1;
    //指纹验证不通过
    public static int FINGER_ERROR = ONLINE_USER_PER + 1; 
    
    
    //权限部分
    public static final int ISWRITE = MainConstant.ISWRITE;//读写
    public static final int ISDOWN = MainConstant.ISDOWN;//下载    
    public static final int ISOPEN = MainConstant.ISOPEN;
    public static final int ISSHARE = MainConstant.ISSHARE;
    public static final int ISLOCK = MainConstant.ISLOCK;
    public static final int ISREAD = MainConstant.ISREAD;//只读
    public static final int CAN_NEW = MainConstant.CAN_NEW;//是否可以新建
    public static final int CAN_MOVE = MainConstant.CAN_MOVE;//是否可以移动
    public static final int CAN_COPY = MainConstant.CAN_COPY;//是否可以复制
    public static final int CAN_DEL = MainConstant.CAN_DEL;//是否可以删除
    public static final int CAN_RENAME = MainConstant.CAN_RENAME;//是否可以重命名
    public static final int CAN_SHARE = MainConstant.CAN_SHARE;//是否可以共享
    public static final int CAN_PASTE = MainConstant.CAN_PASTE;//是否可以黏贴
    public static final int CAN_UPLOAD = MainConstant.CAN_UPLOAD;//是否可以上传
    public static final int CAN_NEVAGATION = MainConstant.CAN_NEVAGATION_BIT;//是否可以浏览
    public static final int ISAMENT = MainConstant.ISAMENT;//是否可以修订
    
    public static final int ALL_PERMIT = MainConstant.ALL_PERMIT; // 所有权限
    
	/** 国际化常量*/
    public static String OTHERSHARE = LocaleConstant.instance.getValue("other_share");
    public static final String GRID_TITLE_2 = LocaleConstant.instance.getValue("GRID_TITLE_2");
    public static final String GRID_TITLE_3 = LocaleConstant.instance.getValue("GRID_TITLE_3");
    
    public static String FILELISTPANEL_GRIDDATA_4 = LocaleConstant.instance.getValue("FILELISTPANEL_GRIDDATA_4");
    public static String SENDMAILWIN_MAILERROR = LocaleConstant.instance.getValue("SENDMAILWIN_MAILERROR");
	
	//com.evermore.weboffice.server.auth.login.AutoLoginFilter
    public static String AUTOLOGIN_ERROR_0 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_0");
    public static String AUTOLOGIN_ERROR_1 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_1");
    public static String AUTOLOGIN_ERROR_2 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_2");
    public static String AUTOLOGIN_ERROR_3 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_3");
    public static String AUTOLOGIN_ERROR_4 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_4");
    public static String AUTOLOGIN_ERROR_5 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_5");
    public static String AUTOLOGIN_ERROR_6 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_6");
    public static String AUTOLOGIN_ERROR_7 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_7");
    public static String AUTOLOGIN_ERROR_8 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_8");
    public static String AUTOLOGIN_ERROR_9 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_9");
    public static String AUTOLOGIN_ERROR_10 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_10");
    public static String AUTOLOGIN_ERROR_11 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_11");
    public static String AUTOLOGIN_ERROR_12 = LocaleConstant.instance.getValue("AUTOLOGIN_ERROR_12");
    
    //com.evermore.weboffice.server.gwt.UploadServiceImpl
//    public static String UPLOADSERVICE_ERROR_0 = "操作系统不支持图形界面,忽略此特性...";
    public static String UPLOADSERVICE_ERROR_1 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_1");
    public static String UPLOADSERVICE_ERROR_2 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_2");
    public static String UPLOADSERVICE_ERROR_3 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_3");
    public static String UPLOADSERVICE_ERROR_4 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_4");
    public static String UPLOADSERVICE_ERROR_6 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_6");
    public static String UPLOADSERVICE_ERROR_7 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_7");
    public static String UPLOADSERVICE_ERROR_8 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_8");
    public static String UPLOADSERVICE_ERROR_9 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_9");
    public static String UPLOADSERVICE_ERROR_10 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_10");
    public static String UPLOADSERVICE_ERROR_11 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_11");
    public static String UPLOADSERVICE_ERROR_12 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_12");
    public static String UPLOADSERVICE_ERROR_13 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_13");
    public static String UPLOADSERVICE_ERROR_13CA = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_13CA");
    public static String UPLOADSERVICE_ERROR_14_0 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_14_0");
    public static String UPLOADSERVICE_ERROR_14_1 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_14_1");
//    public static String UPLOADSERVICE_ERROR_15 = "您的登录状态已被注销,这将导致无法保存文件至服务器,请重新登录。(提示:打开新窗口进行登录后,本页面仍然有效)";
    public static String UPLOADSERVICE_ERROR_16 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_16");
    public static String UPLOADSERVICE_ERROR_17 = LocaleConstant.instance.getValue("UPLOADSERVICE_ERROR_17");
//    public static String UPLOADSERVICE_ERROR_18 = "图片文件大小为0,上传失败!";
//    public static String UPLOADSERVICE_ERROR_19 = "图片文件大小超过300K,上传失败!";
//    public static String UPLOADSERVICE_ERROR_20 = "  消亡时间 ";
//    public static String UPLOADSERVICE_ERROR_21 = "  脉搏: ";
//    public static String UPLOADSERVICE_ERROR_22 = "退出";
//    public static String UPLOADSERVICE_ERROR_23 = "  日期: ";
//    public static String UPLOADSERVICE_ERROR_24 = "  浏览器类型";
//    public static String UPLOADSERVICE_ERROR_25 = "  正常退出";
//    public static String UPLOADSERVICE_ERROR_26 = "登录成功";
//    public static String UPLOADSERVICE_ERROR_27 = "  被强制退出 ";
//    public static String UPLOADSERVICE_FRAME_0 = "开始监测";
//    public static String UPLOADSERVICE_FRAME_1 = "清除";
//    public static String UPLOADSERVICE_FRAME_2 = "当前在线人数: ";
//    public static String UPLOADSERVICE_FRAME_3 = "Session实例: ";
//    public static String UPLOADSERVICE_FRAME_4 = "在线状态实时监测";
//    public static String UPLOADSERVICE_FRAME_5 = "关闭本程序将导致Tomcat服务关闭,确定?";
//    public static String UPLOADSERVICE_FRAME_6 = "监测器";
//    public static String UPLOADSERVICE_FRAME_7 = "停止监测";
    //com.evermore.weboffice.server.gwt.UserRemoteImpl
//    public static String USERREMOTE_ERROR_0 = "文件中没有用户记录!";
//    public static String USERREMOTE_ERROR_1 = " 记录";
//    public static String USERREMOTE_ERROR_2 = "服务器异常,导入失败,请尝试重新导入!";
//    public static String USERREMOTE_ERROR_3 = "用户导入成功!";
//    public static String USERREMOTE_ERROR_4 = "部分用户信息导入失败!";
//    public static String USERREMOTE_ERROR_5 = "<邮件地址>格式错误!\n";
//    public static String USERREMOTE_ERROR_6 = "在数据库中已存在!";
//    public static String USERREMOTE_ERROR_7 = "用户名不合法、或数据库中已存在该用户名!";
//    public static String USERREMOTE_ERROR_8 = "共计";
//    public static String USERREMOTE_ERROR_9 = "文件,";
//    public static String USERREMOTE_ERROR_10 = "成功转换";
    public static String FILE = LocaleConstant.instance.getValue("FILE");
    
    //com.evermore.weboffice.server.service.LicenseService
    public static String LICENSE_ERROR = LocaleConstant.instance.getValue("LICENSE_ERROR");
    public static String LICENSE_SUCCESS_0 = LocaleConstant.instance.getValue("LICENSE_SUCCESS_0");
    public static String LICENSE_SUCCESS_1 = LocaleConstant.instance.getValue("LICENSE_SUCCESS_1");
    public static String DOC_PUBLIC = MainConstant.DOC_PUBLIC;           // 公共工作空间
    
    public static int ADIMI = MainConstant.ADIMI;    
    public static int USER = MainConstant.USER;  // 非领导    
    //角色
    public static int LEADING = MainConstant.LEADING;//领导
    public static int LOCAL_LEADING = MainConstant.LOCAL_LEADING;//分管领导
    public static int USER_ADMIN = MainConstant.USER_ADMIN;    // 用户管理员
    public static int AUDIT_ADMIN = MainConstant.AUDIT_ADMIN;   // 审计员
    public static int SECURITY_ADMIN = MainConstant.SECURITY_ADMIN;  // 安全员
    public static int COMPANY_ADMIN = MainConstant.COMPANY_ADMIN;   // 单位公司管理员
    public static int PART_ADMIN = MainConstant.PART_ADMIN;//部门管理员
    
    public static String SHARE1 = LocaleConstant.instance.getValue("SHARE1");
    
    //共享常量，-1是以前的文档，0是源文档，1是copy文档
    public static int ISCOPY_OLD = -1;
    public static int ISCOPY_NOW = ISCOPY_OLD+1;
    public static int ISCOPY_LATER = ISCOPY_NOW+1;
    
    
    
    public static String LOGINCA_SET_MSG = LocaleConstant.instance.getValue("LOGINCA_SET_MSG");
    public static String DOWNLOADERROR=LocaleConstant.instance.getValue("DOWNLOADERROR");
    
    public static Integer REGEST=0; //外部注册
    public static Integer MEETING=1; //会议模块
    public static Integer SHAREINFO=2; //共享模块
    public static Integer COMPANYFILE=3; //企业文库
    public static Integer GROUPTEAM=4; //群组协作模块
    public static Integer MOBILESIGN=5; //移动签批模块
    public static Integer TRANSSPLIT=6; //事务分发模块
    public static Integer SHAREDIALOG=7; //共享日程模块
    public static Integer GETMOBILELOGIN=8; //移动端登录获取验证码
    public static Integer SENDMSG=9; //短信模块使用
    //可以往下添加

}
