package apps.transmanager.weboffice.constants.both;


/**
 * 定义servlet的action参数名字及各种参数的常量。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

// 禁止继承类。
public final class ServletConst
{
	/**
	 * 禁止实例化类
	 */
	private ServletConst()
	{
	}

	/********************************************
	 ************* 参数key值常量定义 ************** ******************************************
	 */
	public final static String ACTION_KEY = "action";
	// json格式的参数
	public final static String JSON_PARAMS_KEY = "jsonParams";
	// 错误代码
	public final static String ERROR_CODE = "errorCode";
	// 错误信息
	public final static String ERROR_MESSAGE = "errorMessage";
	// 结果
	public final static String RESPONSE_RESULT = "result";
	// json请求的方法
	public final static String METHOD_KEY = "method";
	// json请求的具体参数
	public final static String PARAMS_KEY = "params";
	// json请求的token值
	public final static String TOKEN_KEY = "token";

	public final static String COPYURL = "copyurl";// 复制地址
	/********************************************
	 ************* 参数value值常量定义 ************** ******************************************
	 */
	
	//--------------------------标签块常量定义-----------------------------
		public final static String GET_TAGS_ACTION = "getTags";// 获取标签数据
		
		public final static String Add_TAGS_ACTION = "addTags"; //增加标签
		
		public final static String CREATE_TAGS_ACTION = "createTags";// 创建标签数据
		
		public final static String ADD_FILETAGS_ACTION = "addFileTags";// 添加文件标签
		
		public final static String DEL_FILETAGS_ACTION = "delFileTags";// 删除所有文件标签
		
		public final static String DEL_FILETAG_ACTION = "delLitFileTag";// 删除部分文件标签
		
		public final static String DEL_TAGS_ACTION = "delTags";// 删除标签
		
		public final static String RENAME_TAGS_ACTION = "renameTags";// 重命名标签
		
		//--------------------------标签块常量-----------------------------
	public final static String ACTIVE_ACTION = "active";// 激活

	public final static String MOBILECODE_ACTION = "getMobileCode";// 获取手机验证码
	
	public final static String MOBILELOGINCODE_ACTION = "getMobileLoginCode";// 获取手机验证码
	
	public final static String MOBILEFORGETCODE_ACTION = "getMobileForgetCode";// 忘记密码获取手机验证码
	
	public final static String GETMOBILEBACK_ACTION = "getMobileBack";// 获取用户回复的短信
	public final static String LOGINCA_ACTION = "loginCA";//ca登录
	public final static String LOGINCAMOBILE_ACTION = "loginCAMobile";//ca手机登录
	// 登录
	public final static String LOGIN_ACTION = "login";
	// 退出登录
	public final static String LOGOUT_ACTION = "logout";
	// 是否还在线
	public final static String ONLINE_ACTION = "online";
	//
	public final static String GET_ALL_USER_ACTION = "getAllUser";
	//
	public final static String GET_USERINFO_ACTION = "getUserinfo";
	// 判断权限
	public final static String PERMISSTION_ACTION = "getPermission";
	// 判断文件被谁打开
	public final static String FILE_OPENED_ACTION = "fileOpened";
	//
	public final static String GET_FILE_LIST_ACTION = "getFileList";
	//获取服务器更新操作记录
	public final static String GET_FILE_BAOSONG_ACTION = "getFileBaosong";//获取报送文件列表
	
	public final static String GET_SERVER_CHANGE_RECORDS_ACTION = "getServerChangeRecords";
    //为客户端提供时间戳
	public final static String GET_TIMESTAMP__FOR_CLIENT_ACTION = "getTimeStampForClient";
	
	public final static String IS_FILE_EXIST_ACTION = "isFileExist";
	// 文件上传
	public final static String UPLOAD_FILES_ACTION = "uploadFile";
	// 支持断点续传的文件上传
	public final static String UPLOAD_FILES_CON_ACTION = "uploadFileCon";
	// 文件下载
	public final static String DOWNLOAD_FILE_ACTION = "downloadFile";

	// 文件下载
	public final static String DOWNLOAD_FILENEW_ACTION = "downloadFileNew";
		
	public final static String DOWNLOAD_PERMISION_ACTION = "downloadPermision";

	// 判断能否打开文件
	public final static String CANREAD_PERMISSION_ACTION = "canReadPermission";

	// 支持断点续传的文件下载
	public final static String DOWNLOAD_FILE_CON_ACTION = "downloadFileCon";
	// 文件打开
	public final static String OPEN_FILE_ACTION = "openFile";
	// 文件打开，支持断点续传
	public final static String OPEN_FILE_CON_ACTION = "openFileCon";
	// 
	public final static String CLOSE_FILE_ACTION = "closeFile";
	// 文件保存
	public final static String SAVE_FILE_ACTION = "saveFile";
	// 文件保存，支持断点续传
	public final static String SAVE_FILE_CON_ACTION = "saveFileCon";
	//
	public final static String RENAME_ACTION = "rename";
	//
	public final static String CREATE_FOLDER_ACTION = "createFolder";
	//
	public final static String DELETE_ACTION = "delete";
	//
	// 创建文件版本
	public final static String CREATE_VERSION_ACTION = "createVersion";
	//
	public final static String GET_VERSION_ACTION = "getVersion";
	// 获取文件版本信息列表
	public final static String GET_FILE_VERSIONS = "getFileVersions";
	// 恢复版本
	public final static String RESTORY_FILE_VERSIONS = "restoryFileVersions";
	// 回滚版本
	public final static String ROLLBACK_FILE_VERSIONS = "rollbackFileVersions";
	// 删除版本
	public final static String DELETE_FILE_VERSIONS = "deleteFileVersions";
	// 删除所有版本
	public final static String DELETE_FILE_ALL_VERSIONS = "deleteFileAllVersions";
	// 修改摘要
	public final static String UPDATE_FILE_VERSIONMEMO = "updateVersionMemo";
	// 定稿
	public final static String FINALIZE_FILE_VERSION = "finalizeVersion";
	// 下载版本
	public final static String DOWNLOAD_FILE_VERSION = "downloadFileVersion";
	// 获得某个文件共享信息
	public final static String GET_FILE_SHAREINFO = "getFileShareInfo";

	// 取消共享
	public final static String CANCEL_SHARE_ACTION = "cancelShare";
	public final static String GET_FILE_SHARELOG = "getFileShareLog";
	//获取共享人
	public final static String GET_SHARER = "getSharer";
	// 创建群组
	public final static String ADD_TEAM_SPACE = "addTeamSpace";
	// 获取群组内成员
	public final static String GET_USERS_BY_TEAMID = "getUsersByTeamId";
	// 修改组内成员
	public final static String MODIFY_USERS_BY_TEAMID = "modifyUsersByTeamId";
	// 删除群组
	public final static String DELETE_CUSTOMREAMS = "delCustomTeams";
	// 修改群组
	public final static String MODIFY_TEAM_SPACE = "modifyTeamSpace";
	// 还原
	public final static String UNDELETE_FILES_ACTION = "unDelete";
	// 清空或永久删除
	public final static String DELETE_FILES_FOREVER = "clear";
	public final static String DELETE_ALLFILES_FOREVER = "clearall";
	//强制解锁已打开文件
	public final static String UNLOCK_OPENED_FILE = "unlockOpendFile";
	public final static String GET_SHARE_INFO_ACTION = "getShareInfo";
	//
	public final static String SHARE_FILES_ACTION = "shareFiles";
	//
	public final static String AUDIT_FILE_ACTION = "auditfile";

	public final static String COPY_FILE_ACTION = "copyfile";// 复制文件标记

	// public final static String MOVE_FILE_ACTION = "movefile";//移动文件标记

	public final static String DELETE_FILE_ACTION = "deletefile";// 删除文件标记
	//
	public final static String AUDIT_FILE_BY_STATUS_ACTION = "auditfilebystatus";
	//
	public final static String PENDING_FILES_ACTION = "pendingFiles";
	//
	public final static String AUDIT_FILE_FLAG_ACTION = "setFileReadFlag";
	//
	public final static String AUDIT_FILEINFO_ACTION = "auditfileinfo";
	//
	public final static String REQUEST_AUDIT_FILEINFO_ACTION = "requestAudit";
	//
	public final static String GET_APP_VERSION_ACTION = "getAppVersion";
	//
	public final static String DOWNLOAD_SOURCE_ACTION = "downloadSource";
	//
	public final static String SEARCH_AUDIT_FILE_ACTION = "searchauditfile";
	//
	public final static String AUDIT_ACTION = "audit";
	// 发起送审
	public final static String START_AUDIT_ACTION = "startAudit";

	// 已审文件中再次送审
	public final static String RE_AUDIT_ACTION = "reAudit";
	// 得审批流程信息
	public final static String AUDIT_PIC_INFO_ACTION = "getAuditPicInfo";
	//
	public final static String GET_AUDIT_USER_ACTION = "getAllAuditUser";
	//
	public final static String GET_AUDIT_USERS_ACTION = "getAuditUser";
	
	public final static String GET_AUDITFILE_TYPE = "getAuditFileTypes";// 获取签批文件类别
	public final static String GET_DRAFTS_ACTION = "getDrafts";// 获取草稿列表
	public final static String GET_DONE_ACTION = "getDone";// 已办
	public final static String GET_COLLECT_ACTION = "getCollect";// 收藏签批
	public final static String GET_TODO_ACTION = "getTodo";// 待办
	public final static String GET_TOREAD_ACTION = "getToread";// 未阅
	public final static String GET_HADREAD_ACTION = "getHadread";// 已阅
	public final static String GET_ENDWORKS_ACTION = "getEndWorks";// 办结
	public final static String EXPORT_END_ACTION = "exportEnd";// 导出办结
	public final static String GET_MYQUEST_ACTION = "getMyquestfiles";// 我的请求
	public final static String GET_FILETYPES_ACTION = "getAuditFileTypes";// 签批的文档类别
	
	public final static String GET_HADSENDS_ACTION = "getHadSends";// 获取已送列表,暂不用了
	public final static String GET_WAITWORKS_ACTION = "getWaitWorks";// 获取草稿列表
	public final static String GET_HADWORKS_ACTION = "getHadWorks";// 获取草稿列表
	public final static String GET_SUCCESSFILES_ACTION = "getSuccessfiles";// 获取成文列表
	public final static String GET_APPROVALINFO_ACTION = "getApprovalinfo";// 根据ID获取流程数据
	public final static String GET_WEBCONTENT_ACTION = "getWebcontent";// 获取事务详情
	
	public final static String GET_NOMODIFYNUMS = "getNomodifyNums";//获取未处理事务数量
	public final static String GET_TEAMSHARENUMS = "getTeamShareNums";//移动端获取最新协作共享下的数量
	
	// 移动端接口参数
	public final static String APPROVAL_SAVEDRAFT_ACTION = "saveDraft";// 保存草稿
	public final static String APPROVAL_SENDSIGN_ACTION = "sendSign";// 送签
	public final static String APPROVAL_SENDCOOPER_ACTION = "sendCooper";// 送协作
	public final static String APPROVAL_GETAPPROVALSAVE_ACTION = "getApprovalsave";// 获取草稿
	public final static String APPROVAL_DELSIGNINFO_ACTION = "delSignInfo";// 删除
	public final static String APPROVAL_SUCCESSSIGN_ACTION = "successSign";// 成文
	public final static String APPROVAL_ENDSIGN_ACTION = "endSign";// 终止
	public final static String APPROVAL_UNDOSIGN_ACTION = "undoSign";// 反悔
	public final static String APPROVAL_GETHISTORY_ACTION = "getHistory";// 历史
	public final static String APPROVAL_GETCURRENTPERMIT_ACTION = "getCurrentPermit";// 获取签批窗口的内容
	public final static String APPROVAL_MODIFYSIGNREAD_ACTION = "modifySignRead";// 处理签阅
	public final static String APPROVAL_BACKSENDSIGN_ACTION = "backSendSign";// 处理返还送文人
	public final static String APPROVAL_MODIFYSIGNSEND_ACTION = "modifySignSend";// 处理签批，下一个步
	public final static String APPROVAL_RESENDSIGN_ACTION = "reSendSign";// 再次送审
	public final static String MESSAGE_NEWLIST_ACTION = "getNewListMessages";// 获取当前人员的提醒信息列表

	public final static String TRANS_DRAFT_ACTION = "getTransDrafts";// 获取交办事务草稿
	public final static String TRANS_MYQUEST_ACTION = "getTransMyquestfiles";// 获取我的交办事务
	public final static String TRANS_DONE_ACTION = "getTransDone";// 获取已办事务
	public final static String TRANS_TODO_ACTION = "getTransTodo";// 获取待办事务
	public final static String TRANS_SAVE_ACTION = "transSave";// 保存草稿
	public final static String TRANS_GETSAVE_ACTION = "getTransSave";// 获取草稿
	public final static String TRANS_COMMIT_ACTION = "transCommit";// 事务提交
	public final static String TRANS_CONTENT_ACTION = "getTransWebcontent";// 获取事务内容
	public final static String TRANS_INFO_ACTION = "getTransPermit";// 获取事务详情
	public final static String TRANS_MODIFY_ACTION = "transModify";// 事务处理
	public final static String TRANS_DELETE_ACTION = "transDelete";// 事务删除
	public final static String TRANS_HISTORY_ACTION = "getTransHistosy";// 事务历史
	public final static String TRANS_SIGN_ACTION = "transsignreal";// 事务签收
	public final static String TRANS_GETSIGN_ACTION = "getTransSignReal";// 获取签收事务

	public final static String MEET_DRAFT_ACTION = "getMeetDrafts";// 获取会议草稿
	public final static String MEET_MYQUEST_ACTION = "getMeetMyquestfiles";// 获取我的会议通知
	public final static String MEET_DONE_ACTION = "getMeetDone";// 获取已办会议
	public final static String MEET_TODO_ACTION = "getMeetTodo";// 获取待办会议
	public final static String MEET_SAVE_ACTION = "meetSave";// 保存草稿
	public final static String MEET_GETSAVE_ACTION = "getMeetSave";// 获取草稿
	public final static String MEET_COMMIT_ACTION = "meetCommit";// 会议提交
	public final static String MEET_CONTENT_ACTION = "getMeetWebcontent";// 获取会议内容
	public final static String MEET_INFO_ACTION = "getMeetPermit";// 获取会议详情
	public final static String MEET_MODIFY_ACTION = "meetModify";// 会议处理
	public final static String MEET_DELETE_ACTION = "meetDelete";// 会议删除
	public final static String MEET_BACKDETAIL_ACTION = "getMeetBackDetail";// 会议详情，
	public final static String MEET_BACKDETAILCB_ACTION = "getMeetBackDetailCB";// 会议详情，待催办
	public final static String MEET_SIGN_ACTION = "meetsignreal";// 会议签收
	public final static String MEET_GETSIGN_ACTION = "getMeetSignReal";// 获取会议事务


    //公文收发
    public final static String FAWEN_SEND_ACTION = "getFawenSend";//get the documents send by me
    public final static String FAWEN_RECEIVE_ACTION = "getFawenReceive";// get the documents
    public final static String FAWEN_GET_DEPARTMENT= "getFawenDepartment";// get the documents
/*    public final static String FAWEN_SET_ACTION = "setFawenUser";// set the user
    public final static String FAWEN_UNSET_ACTION = "unsetFawenUser";// unset the user
    public final static String FAWEN_GETUSER_ACTION = "getFawenUser";// get the power user of Fawen*/
    public final static String FAWEN_SEND = "sendFawen";// send a document to a company
    public final static String FAWEN_RECEIVE = "receiveFawen";// reveive a document


	public final static String MSG_GET_ACTION = "getMsgCount";// 获取提醒统计数量
	
	public final static String SEND_REVIEW_FILE = "sendReviewFiles";//审阅中的送审文档
	public final static String GET_REVIEW_FILEOF_SEND = "getReviewFilesOfSend";//获取送审的文档列表
	public final static String GET_REVIEW_DETAILS="getReviewDetails";//获取文档详情
	public final static String GET_REVIEW_FILESOF_FILED = "getReviewFilesOfFiled";//获取审结的文档列表
	public final static String GET_REVIEW_FILESOF_TODO = "getReviewFilesOfTodo";//获取待审的文档列表
	public final static String GET_REVIEW_FILESOF_DONE = "getReviewFilesOfDone";//获取已审的文档列表
	public final static String REVIEW_FILE = "reviewFile";//审阅文档
	public final static String GOBACK_REVIEWFILE = "goBackReviewFile";//反悔已审阅的文档
	public final static String GETMOBILEFIRSTNUMS = "getMobileFirstNums";//移动端首页获取的数量
	public final static String SETDESKS = "setDesks";//发布到桌面
	public final static String GETDESKS = "getDesks";//获取首页桌面内容
	public final static String DELDESKS = "delDesks";//删除桌面快捷方式
	
	public final static String SETCOLLECTEDIT = "setCollectEdit";//采编报送
	public final static String GETCOLLECTEDITSEND = "getCollectEditSend";//采编报送
	public final static String GETPIC4SCSAME = "getPic4SCSame";//采编报送
	public final static String GETPIC4SCUSER = "getPic4SCUser";//采编报送
	public final static String GETCB = "getCB";//采编报送
	public final static String GETBS = "getBS";//采编报送
	public final static String BAOSONG = "baosong";//采编报送
	public final static String CAIBIAN = "caibian";//采编
	
	
	// 获得打开文件的临时路径
	public final static String OPEN_FILE_PATH_ACTION = "commandRetrieveFSPath"; // Constants.CommandRetrieveFSPath;
	// 同名文件是否存在
	// public final static String FILE_EXIST_ACTION =
	// "commandCheckDuplicateFileName";
	// //Constants.CommandCheckDuplicateFileName;
	// 保存列表
	public final static String ADD_SAVE_LIST_ACTION = "addSaveFile";
	// 删除保存列表
	public final static String REMOVE_SAVE_LIST_ACTION = "removeSaveFile";
	// 获取保存列表
	public final static String GET_SAVE_LIST_ACTION = "getSaveFile";
	// 删除打开文件列表
	public final static String REMOVE_OPEN_LIST_ACTION = "removeOpenFileList";
	// 获得媒体数据
	public final static String GET_MEDIA_ACTION = "getMedia";
	//
	public final static String GET_ALL_FILES_ACTION = "getAllFiles";
	//
	public final static String GET_ALL_SPACE_ACTION = "getAllSpaces";
	//
	public final static String MOVE_FILE_ACTION = "movefile";
	// 属性
	public final static String GET_FILE_PROPERTIES = "getFileProperties";
	//共享文件的备注
	public final static String GET_SHAREDFILE_COMMENT = "getSharedfileComment";
	//对共享文件添加备注
	public final static String SET_SHAREDFILE_COMMENT = "setSharedfileComment";
	//修改选中的共享文件的备注
	public final static String MODIFY_SHAREDFILE_COMMENT = "modifySharedfileComment";
	//删除所添加的共享文件备注
	public final static String DEL_SHAREDFILE_COMMENT = "delSharedfileComment";
	//获取一个共享文件的属性中的备注
	public final static String GET_DETAIL_COMMENT = "getDetailComment";
	// 心跳
	public final static String HEART_BEAT_ACTION = "heartbeat";

	//
	public final static String CHECK_OPEN_FILE_STATUS_ACTON = "checkOpenFileStatus";
	// 
	public final static String FILE_OPENED_ACTON = "isFileOpened";
	// 登录
	public final static String AUTO_LOGIN_ACTION = "autologin";
	// 获取空间大小
	public final static String WORK_SPACE_SIZE_ACTION = "workspace";
	// 注册
	public final static String REGISTE_ACTION = "registe";
	//
	public final static String FURBISH_ACTION = "furbish";
	//
	public final static String LOGIN_COMPLETE_ACTION = "loginComplete";
	//
	public final static String QUIT_ACTION = "quit";
	//
	public final static String LOAD_NAME_ACTION = "loadName";
	//
	public final static String CHECK_LOGIN_ACTION = "checkLogined";
	//
	public final static String CA_LOGIN_ACTION = "loginCA";
	public final static String LOGIN_CAINFO_ACTION = "loginCaInfo";
	public final static String CA_SESSIONLOGIN_ACTION = "sessionloginCA";
	//
	public final static String UPLOAD_USER_PORTRAIT_ACTION = "uploadPortrait";
	//
	public final static String UPLOAD_USER_PORTRAIT_CAMERA_ACTION = "uploadPortraitbyCamera";
	//
	public final static String UPLOAD_GROUP_PORTRAIT_ACTION = "uploadGroupPortrait";
	//
	public final static String UPLOAD_ORG_PORTRAIT_ACTION = "uploadOrgPortrait";
	// 
	public final static String CAN_SAVE_ACTION = "canSave";
	//
	// public final static String MS_SAVE_ACTION = "mssave";
	//
	public final static String CHECK_OUT_ACTION = "checkout";
	//
	public final static String CHECK_IN_ACTION = "checkIn";
	//
	public final static String PLAY_MEDIA_ACTION = "seemedia";
	//
	public final static String UPLOAD_FILE_ACTION = "commandUploadFile";
	//
	public final static String IMPORT_USER_ACTION = "import_user";
	//
	public final static String IMPORT_LICENSE_ACTION = "import_license";
	// 
	public final static String UPLOADFILE_ACTION = "upload";
	//
	public final static String CREATE_FOLDERS_ACTION = "createFile";
	//
	public final static String GET_EXIST_FILES_ACTION = "getExistFiles";
	//
	public final static String LODA_TREE_ACTION = "treeload";
	//
	public final static String SAME_FILE_CHECK_ACTION = "nodeSameFileCheck";
	//
	public final static String SYS_REPORT_EXPORT_ACTION = "sysReportExport";
	//
	public final static String LOG_EXPORT_ACTION = "export";
	// 导出用户导入模板
	public final static String EXPORT_IMPORT_USER_TEMPLATE_ACTION = "exportImportUsersTemplate";

	public final static String IMPORT_COMPANYUSER_TEMPLATE_ACTION = "userorgtemplate";
	//
	public final static String SEND_MAIL_DOEN_ACTION = "sendmaildown";
	//
	public final static String DELETE_TEMP_FILE_ACTION = "deletetempfile";
	//
	public final static String DEL_MERGE_ACTION = "delmerger";
	//
	public final static String DOWN_MERGE_ACTION = "downmerger";
	//
	public final static String SAVE_ENCRYPT_ACTION = "saveToEncrypt";
	//
	public final static String SAVE_DECRYPT_ACTION = "saveToDecrypt";
	//
	public final static String GET_USERS_MESSAGE = "getMySendMessage";
	
	public final static String GET_RECEIVE_MESSAGE ="getMyReceiveMessage";

	//

	//

	//
	public final static String LOGIN_CHECK_ACTION = "loginCheck";
	// ??
	public final static String GET_FILEINFO_ACTION = "getFileinfos";
	// ???
	public final static String GET_FILEINFOS_ACTION = "getFileinfo";
	//
	public final static String CREATE_FOLDER_APPLET_ACTION = "createFolder";
	//

	//
	public final static String SYSTEM_PERMISSION_ACTION = "systemPermission";
	//
	public final static String PERMISSION_ACTION = "permission";
	//
	public final static String OPEN_ACTION = "open";
	//
	public final static String SAVE_ACTION = "save";
	// 标准html文档上传
	public final static String FORM_UPLOAD_ACTION = "uploadbyform";
	// 
	public final static String UPLOAD_AUDIT_ACTION = "uploadforaudit";
	// 标准html文档下载
	public final static String FORM_DOWNLOAD_ACTION = "downloadbyform";
	// 为移动临时增加
	public final static String GET_AUDIT_FILES_ACTION = "getauditFiles";
	// 为移动临时增加
	// public final static String AUDIT_FILE_ACTION = "auditfile";
	//
	public final static String AUDIT_SPACE_NAME_ACTION = "auditspacename";
	//

	// 密码重置
	public final static String RESET_PASSWORD_AXTION = "resetpassword";

	public final static String UPLOADTEMPFILE = "uploadtempfile";

	public final static String VALIDATEPASSWORD = "validatePassword";

	// 用户的配置
	public final static String GET_USER_CONFIG_ACTION = "getuserconfig";

	// 所有的应用包括查询
	public final static String GET_ALL_APPS_ACTION = "getyzapps";

	public final static String GET_ALL_STYLES_ACTION = "getyzstyles";

	// 所有的模块
	public final static String GET_ALL_MODULES_ACTION = "getyzmodules";

	// 设置用户配置
	public final static String SET_USER_CONFIG_ACTION = "setuserconfig";

	// 获得协作空间的显示名称。 user290 2012-03-20
	public final static String GET_GROUP_SPACE_NAME_ACTION = "getGroupSpaceName";

	// 获得联系人
	public final static String GET_CONTACTS = "getContacts";

	// 获得用户的自定义组和项目组
	public final static String GET_USER_CONTACT_GROUPS = "getUserContactGroups";
	
	public final static String ADD_OUTERADDRESS = "addOuterAddress";//增加外部联系人接口

	public final static String GET_WEB_ACTION_CONTROL = "getWebActionControl";

	public final static String CALENDARSHARE = "setPublicCalendar";// 日程是否公开

	public final static String SIGNREAL = "signreal";// 签收
	public final static String GETSIGNREAL = "getSignReal";// 获取签收情况

	public final static String GET_SIGN_VERSION = "getSignVersion";
	public final static String GET_SIGIN = "getSign";// 签收
	public final static String UPDATE_SIGIN = "updateSign";// 签收

	public final static String GET_SIGN_PROCESS_INFO = "getSignProcessInfo";// 获取会签时候，总的需要签批的人数和已经签批的人数
	public final static String GET_SIGN_INFOS = "getSignInfos";// 签批的相关信息
	public final static String SET_SIGN_RECEIPT = "setSignReceipt";// 会签回执
	public final static String SET_SIGN_WARN = "setSignWarn";// 会签设置提醒
	public final static String GET_SIGN_WARN = "getSignWarn";// 会签设置提醒
	public final static String GET_SIGN_MAN = "getSignMan";// 会签设置提醒

	public final static String GET_FILE_UPLOAD_ACTION = "getFileForUpload"; // 获取上传文件路径的文件列表

	public final static String GET_SEARCHFILES_ACTION = "getSearchfiles";// 获取成文列表

	public final static String CHECK_PATH_PERMISSION = "checkPathPermission";

	// 获取公司的所有角色
	public final static String GET_MANAGE_ROLES = "getManageRoles";
	// 增加公司的所有角色
	public final static String ADD_MANAGE_ROLES = "addManageRoles";
	// 修改公司的所有角色
	public final static String UPDATE_MANAGE_ROLES = "updateManageRoles";
	// 删除公司的所有角色
	public final static String DELETE_MANAGE_ROLES = "deleteManageRoles";

	// 获取权限列表
	public final static String GET_ROLE_PERMISSIONS = "getRolePermissions";
	// 修改权限
	public final static String UPDATE_ROLE_PERMISSIONS = "updateRolePermissions";
	// 公司是否已经存在
	public final static String IS_EXIST_COMPANY = "isExistCompany";
	// 用户是否已经存在
	public final static String IS_EXIST_USER = "isExistUser";
	// 增加新公司及管理员
	public final static String ADD_COMPANY = "addCompany";
	// 获取公司列表
	public final static String GET_COMPANY_LIST = "getCompanyList";
	// 搜索公司
	public final static String SEARCH_COMPANY = "searchCompany";
	// 删除公司
	public final static String DELETE_COMPANY = "deleteCompany";
	// 获取公司详细信息
	public final static String GET_COMPANY_INFO = "getCompanyInfo";
	// 修改公司
	public final static String UPDATE_COMPANY = "updateCompany";
	// 系统重置公司管理员密码
	public final static String RESET_COMPANY_ADMIN_PASS = "resetCompanyAdminPass";
	// 管理员修改自己密码
	public final static String UPDATE_USER_PASS = "updateUserPass";
	// 导入用户
	public final static String IMPORT_USERS = "importUsers";
	// 导入license
	public final static String IMPORT_LICENSE = "importLicense";
	// 新建公司部门
	public final static String ADD_ORG = "addOrg";
	// 得到公司部门
	public final static String GET_ORG_LIST = "getOrgList";
	// 得到公司的所有用户
	public final static String GET_COMPANY_USE_LIST = "getCompanyUserList";
	// 得到公司的总用户数
	public final static String GET_COMPANY_USE_COUNT = "getCompanyUserCount";
	// 得到公司部门的所有用户
	public final static String GET_ORG_USE_LIST = "getOrgUserList";
	// 得到公司部门的总用户
	public final static String GET_ORG_USE_COUNT = "getOrgUserCount";
	// 删除公司部门
	public final static String DELETE_ORG = "deleteOrg";
	// 修改公司部门信息
	public final static String UPDATE_ORG_INFO = "updateOrgInfo";
	// 修改公司部门的用户
	public final static String UPDATE_ORG_USE_LIST = "updateOrgUserList";
	// 查询公司部门
	public final static String SEARCH_ORG = "searchOrg";
	// 查询公司下的所有用户
	public final static String SEARCH_COMPANY_USER_LIST = "searchCompanyUserList";
	// 禁止公司用户
	public final static String FORBID_USER = "forbidUser";
	// 新建公司用户
	public final static String ADD_USER = "addUser";
	public final static String GETCONFIG="getConfig";//获取是否要显示用户注册链接
	// 添加散户
	public final static String ADD_EXTRA_USER = "addExtraUser";
	public final static String VALIDATECODE = "validateCode";// 验证码检查
	public static final String VALIDATE_EXTRA_USER = "validateExtraUser";
	// 修改公司用户信息
	public final static String UPDATE_USER = "updateUserInfo";
	// 删除公司用户
	public final static String DELETE_USER = "deleteUser";
	// 上传用户头像
	public final static String UPLOAD_USER_PORTRAIT = "uploadUserPortrait";
	// 获取用户所在的部门
	public final static String GET_USER_ORG_LIST = "getUserOrgList";
	// 修改用户所在的部门
	public final static String UPLOAD_USER_ORG = "uploadUserOrg";
	// 获取用户的设备列表
	public final static String GET_USER_DEV_LIST = "getUserDevList";
	// 修改用户的设备使用状态
	public final static String UPLOAD_USER_DEV = "uploadUserDev";
	// 获取用户的全部信息
	public final static String GET_USER_ALL_INFO = "getUserAllInfo";
	// 获取用户的部分信息，为用户自己修改信息用
	public final static String GET_USER_PART_INFO = "getUserPartInfo";
	// 修改用户的部分信息，为用户自己修改信息
	public final static String UPDATE_USER_PART_INFO = "updateUserPartInfo";
	// 建立发布路径
	public final static String ADD_PUBLISH_ADDRESS = "addPublishAddress";
	// 导出用户导入模板
	public final static String EXPORT_USERS_TEMPLATE = "exportUsersTemplate";
	// 增加或修改用户的公司角色
	public final static String ADD_UPDATE_USER_COMPANY_ROLE = "addUpdateCompanyRole";

	// 删除日志
	public final static String DELETE_LOGS = "deleteLogs";
	// 获取某段时间中访问日志
	public final static String GET_ACCESS_LOGS_COUNT = "getAccessCount";
	// 获取日志访问数量基本值
	public final static String GET_ACCESS_INFO = "getAccessInfo";
	// 最近访问日志
	public final static String GET_LASTEST_LOG = "getLastestLog";
	// 查询系统日志
	public final static String GET_SEARCH_LOG = "getSearchLogs";
	// 导出日志，以文本文件方式
	public final static String EXPORT_SEARCH_LOGS = "exportSearchLogs";
	// 获取用户的登录退出日志记录
	public final static String GET_USER_LOGIN = "getUserLoginLogs";
	// 获取用户的登录退出日志记录总数量
	public final static String GET_USER_LOGIN_COUNT = "getUserLoginLogsCount";
	
	//根据单位查询相关日志概况
	public final static String GET_DEP_LOGS = "getdeplogs";
	//根据单位导出相关日志
	public final static String EXPORT_DEP_LOGS = "exportdeplogs";
	public final static String SAVEMETADATA = "saveMetadata"; //保存元数据
	
	//获得系统监控信息
	public final static String GET_SYS_MONITOR = "sysMonitor";  
	public final static String GET_SYS_STATUS = "sysStatus";
	public final static String GET_SYS_ONLINE_ACCOUNT = "sysOnline" ;  //在线的账户
	//获得系统报表信息
	public final static String GET_SYS_REPORT = "getsysReport";  
	
	//新闻订阅相关的操作
	public final static String ADD_WEBINFO = "addWebInfo";   //添加或更新一个订阅栏目
	public final static String DELETE_WEBINFO = "delWebInfo";   //删除一个订阅栏目
	public final static String DELETE_WEBINFO_LIST = "delWebInfoList";   //删除多个订阅栏目
	public final static String DELETE_NEWSINFO = "delNewsInfo";  //删除一个新闻表
	public final static String ALLWEBINFO = "getAllWebInfo";  //获得所有的订阅表，后台管理使用
	public final static String GET_WEBINFO = "getWebInfo";  //获得一个的订阅表，后台管理使用
	
	public final static String UPDATE_WEBINFO = "updateWebInfo";  //更新一个订阅表，后台管理使用
	
	public final static String ALLWEBINFOLIST = "getAllWebInfoList";  //查看所有的订阅栏目
	public final static String MYWEBINFOLIST = "getMyWebInfoList";  //查看我的订阅栏目
	public final static String GET_WEBINFOLISTBY_CATEGORY = "getWebInfoListByCategory"; //根据分类名称查找一系列的订阅栏目
	public final static String GET_NEWSINFOLISTBY_CATEGORY = "getNewsInfoListByCategory";  //根据分类名称列出该分类下的新闻
	public final static String ATTEN_WEBINFO = "addAttenWebInfo";  //关注一个订阅栏目
	public final static String DEL_WEBINFO = "delAttenWebInfo";  //取消订阅一个栏目
	public final static String ATTEN_WEBINFOBY_WEBNAME = "addAttenWebInfoByName";  //关注一个订阅栏目,通过订阅栏目的webname
	public final static String DEL_WEBINFOBY_WEBNAME = "delAttenWebInfoByName";  //取消订阅一个栏目，通过订阅栏目的webname
	public final static String LIST_NEWSINFO_IN_WEBINFO = "listNewsInfo";//列出一个订阅栏目下的所有新闻
	public final static String NEWSCONTENT = "newsContent";  //打开一条新闻
	
	//与泰得利通的用户同步
	/**
	 * 添加用户
	 */
	public final static String SYNCH_USER_ADD = "synch_user_add";
	
	/**
	 * 修改用户
	 */
	public final static String SYNCH_USER_UPDATE = "synch_user_update";
	
	
	/**
	 * 删除用户
	 */
	public final static String SYNCH_USER_DEL = "synch_user_del";
	
	
	/**
	 * 添加部门
	 */
	public final static String SYNCH_ORG_ADD = "synch_org_add";
	
	
	/**
	 * 修改部门
	 */
	public final static String SYNCH_ORG_UPDATE = "synch_org_update";
	
	/**
	 * 删除部门
	 */
	public final static String SYNCH_ORG_DEL = "synch_org_del";
	
	/**
	 * 添加公司
	 */
	public final static String SYNCH_COM_ADD = "synch_com_add";
	
	
	/**
	 * 修改公司
	 */
	public final static String SYNCH_COM_UPDATE = "synch_com_update";
	
	/**
	 * 删除公司
	 */
	public final static String SYNCH_COM_DEL = "synch_com_del";
	
	public final static String ADDORUPDATEUSER="addOrupdateUser";//外部系统同步用户接口
	public final static String ADDORUPDATEORGS="addOrupdateOrgs";//外部系统同步部门接口
	public final static String GETALLUSERSLIST="getAllUsersList";//外部系统获取所有用户接口
	public final static String GETALLORGSLIST="getAllOrgsList";//外部系统获取部门接口
	public final static String GETCALOGINTOKEN="getCaLoginToken";//根据caid获取token
	
	public final static String SEND_MESSAGE="sendMessage";//发送消息
	public final static String RECEIVE_MESSAGE="receiveMessage";//接受消息
	public final static String TOTAL_MESSAGE="totalMessage";//消息汇总
	public final static String TOTAL_SMS="getTotalSMS";//总体消息概况
	public final static String TOTAL_COMPANYSMS="getCompanysms";//单位人员的短信列表
	
	
	
	
	public final static String IMP_FLOWINFO="impFlowinfo";//导入流程数据
	public final static String GET_G_AND_M = "getGroupAndMember";
	public final static String GET_DISCU_LIST = "getDiscuList";
	public final static String GET_DISCU_Member_LIST = "getDiscuMemberList";
	// 获取用户能够使用的用户。
	public final static String GET_USER_APPS = "getUserApps"; 
	// 获取系统中的应用。
	public final static String GET_SYSTEM_APPS = "getSystemApps";
	// 获取公司的应用。
	public final static String GET_COMPANY_APPS = "getCompanyApps";
	public final static String GET_PERSONAL_APPS = "getPersonalApps";// 获取个人的应用。
	public final static String SAVE_PERSONAL_APPS = "savePersonalApps";//保存个人用户设置的应用
	// 设置、修改或删除公司的应用。
	public final static String ADD_UPDATE_COMPANY_APPS = "addUpdateCompanyApps";	
	// 设置、修改或删除公司给用户的应用。
	public final static String ADD_UPDATE_USER_APPS = "addUpdateUserApps";
	// 设置、修改或删除系统给用户的应用（用户自己购买的应用）。
	public final static String ADD_UPDATE_USER_APPS_BY_USER = "addUpdateUserAppsByUser";
	// 导入新的应用模块或修改已有应用模块
	public final static String IMPORT_SYSTEM_APPS = "importSystemApps";
	// 用户自己组成公司帐号
	public final static String REGISTER_COMPANY_ACCOUNT = "registerCompany";
	// 用户激活公司帐号
	public final static String ACTIVE_COMPANY_ACCOUNT = "activeCompany";
	
	public final static String GET_PROJECT_FIRST_BLOG = "getProjectAndFirstBlog";
	
	public final static String getPMicroblog = "getPMicroblog";
	public final static String searchBlog = "searchBlog";
	public final static String delMyBlog = "delMyBlog";
	public final static String sendBlog = "sendBlog";
	public final static String getBlogBackList = "getBlogBackList";
	public final static String getCalendarEvent = "getCalendarEvent";
	public final static String saveCalendarEvent = "saveCalendarEvent";
	public final static String deleteCalendarEvent = "deleteCalendarEvent";
	public final static String getTodayCalendarEvents = "getTodayCalendarEvents";
	
	public final static String getAlertCalendarEvent = "getAlertCalendarEvent";
	public final static String getCalendarEventsByDate = "getCalendarEventsByDate";
	public final static String getCalendarEventForMOblie = "getCalendarEventForMOblie";
	public final static String getCalendarEventsByDurationDate = "getCalendarEventsByDurationDate";
	public final static String getCalendarEventCount = "getCalendarEventCount";
	public final static String getMyshareFile = "getMyshareFile";
	public final static String getShareOwnerInfo = "getShareOwnerInfo";
	public final static String getOtherShareFile = "getOtherShareFile";
	public final static String getUserCalendarPublic = "getUserCalendarPublic";
	public final static String getinviterName = "getinviterName";
	
	//手机方式
	public final static String FORGETMOBILEPASSWORD="forgetMobilePassword";
	
	//邮箱方式
	public final static String FORGETEMAILPASSWORD="forgetEmailPassword";
	
	//判断用户是否存在
	public final static String VALIDATEUSEREXISTS="validateUserExists";
	
	//手机修改密码
	public final static String MOBILECHANGEPASS="mobileChangePass";
	
	//邮件修改密码验证
	public final static String MAILREPASSWORDCHECK="mailRepasswordCheck";
	//邮件修改密码
	public final static String MAILREPASSWORD="mailRepassword";
	
	//手机端获取验证码
	public final static String MOBILECALIDATECODE= "mobileValidateCode";
	
	//即时通讯个人消息更新为已读
	public final static String UPDATESESSIONMESSAGEREAD= "updateSessionMessgaeRead";
	
	//即时通讯组消息更新为已读
    public final static String UPDATEGROUPSESSIONMESSAGEREAD= "updateGroupSessionMessgaeRead";
    //即时通讯组消息获取个人消息
    public final static String GETPERSONMESSAGE = "getPersonMessage";
    //即时通讯组消息获取讨论组消息
    public final static String GETGROUPMESSAGE = "getGroupMessage";
    //即时通讯获取联系人
	public static final String GET_CTM_G_AND_M = "getCtmGAndM";
	//即时通讯查找联系人，用于添加好友
	public static final String SEARCH_ROSTERS = "searchRosters";
	//添加好友,发送验证消息
    public static final String SEND_VALIDATE = "sendValidate";
	//添加好友确认消息
    public static final String PRO_VALIDATE = "proValidate";
    //结束一条验证消息
	public static final String END_VALIDATE = "endValidate";
	//短信发送
	public static final String SEND_NOTE = "sendnote";
	//获取所有元数据
	public static final String GET_ALL_MEATDATA = "getAllMetaData";
}
