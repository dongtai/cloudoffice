package apps.transmanager.weboffice.util.beans;

public class PageConstant {

	/**
	 * 树
	 */
	public static final String NODE_ID = "id"; //传递ID
	public static final String NODE_NAME = "text"; //节点显示文本
	public static final String NODE_LEAF = "leaf"; //是否为叶子节点
	public static final String NODE_ICON = "icon"; //图标
	
	
	/**
	 * 验证
	 */
	public static final int VALIDATOR_NAME_DUP = -1;//名字重复
	public static final int VALIDATOR_NAME_SUC = 1;//成功
	public static final int VALIDATOR_NAME_FAIL = 0;//失败
	
	/**
	 * 逻辑命名
	 */
	/** 当前会话中的用户 */
	public static final String LG_SESSION_USER = "userKey";
	/** 在线用户列表 */
	public static final String LG_ONLINE_ID_LIST = "onlineIdList";
	/** 在线用户列表 */
	public static final String LG_USER_ID = "userId";
	/** 会话的在线信息 */
	public static final String LG_SESSION_ONLINE = "onlineMeg";
	/** 当前用户登录时间 */
	public static final String LG_SESSION_LOGINTIME = "loginTime";
	
	/*
	 * 通知的四种类型
	 */
	/** 系统通知 */
	public static final int MSG_TYPE_SYS  = 0;
	/** 即时通讯 */
	public static final int MSG_TYPE_IM = 1;
	/** 微言通讯 */
	public static final int MSG_TYPE_BLOG = 2;
	/** 日程通讯 */
	public static final int MSG_TYPE_DATE = 3;
	/** @user通讯 */
	public static final int MSG_TYPE_BLOG_AT = 4;
	/** 私信通讯 */
	public static final int MSG_TYPE_BLOG_PRILETTER = 5;
	/**粉丝通知 */
	public static final int MSG_TYPE_Fan = 6;
	
	public static final String DESC = "desc";
	//public static final String LG_DEFAULT_ICON = "/static/images/personalset2/image.jpg";//默认个人头像
	//public static final String LG_DEFAULT_ICON_FIX = "/static/images/personalset2/";//默认个人头像
	public static final String LG_DEFAULT_FILE = "/static/images/fileicon/other.gif";//文件图标
	public static final String LG_DEFAULT_FILEICONPATH = "/static/images/fileicon/";//默认图标路径
}
