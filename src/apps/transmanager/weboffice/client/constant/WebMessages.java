package apps.transmanager.weboffice.client.constant;

import com.google.gwt.i18n.client.Messages;

/**
 * 用到的常量
 * <p>
 * <p>
 */
public interface WebMessages extends Messages
{ 
    String colon();// ":";
    String dialog_ok();// "确定";
    String dialog_cancel();// "取消";
    String dialog_close();// "关闭";
    String new_();//new();// "新建";
    String fileMenu();// "文件";
    String version();// "版本";
    String dealMenu();// "流转";
    String sortMenu();// "查看";
    String read();// "浏览";
    String unlock();// "解锁";
    String lock();// "锁定";
    String return_up();// "向上";
    String delete();// "删除";
    String rename();// "重命名";
    String upload();// "上传";
    String download();// "下载";
    String detail();// "详细信息";
    String breItem();// "缩略图";
    String offLine();//"离线编辑";
    String clearshare();//"放弃";
    String print();//"打印";
    String webPrint();//"网络打印"
    String printDocument();//"打印文档";
    String printState();//"打印任务";
    String sign();//"签名"
    String checkSign();//"验签"
    String picture();//"图片"
    String html();//"网页"
    String spaceStates();//"空间管理"
    String amendment();//修订
    String approve();//审阅
    
    String download_zip();// "压缩打包下载";
    String share();// "共享";
    String cancel_share();// "取消共享";
    String share_log();// "共享日志";
    String onlineFolder();// "在线文档";
    String myFolder();// "我的文档";
    String gc();// "回收站";
    String myShare();// "我的共享";
    String otherShare();// "他人共享";
    String workspace();// "工作空间";
    String currentFile();// "最近的文档";
    String starMark();// "星号标记";
    String locker();// "当前编辑用户";
    String owner();//"文档作者"
    String commonality();//public();// "公共区域";
    String individual();//private();// "个人区域";
    String company();// "公司区域";
    String search();// "搜索";
    String create_folder();// "创建文件夹";
    String sendMail();// "发送";
    String sendAPProve();// "送审";
    String move();// "移动";
    String move_colon();// "移动:";
    String copy();// "复制";
    String copyurl();//复制地址
    String uncopyurl();//取消复制地址
    String forever_delete();// "永久删除";
    String redelete();// "还原";
    String new_wp();// "文字处理";
    String new_ss();// "电子表格";
    String new_pg();// "简报制作";
    String new_ot();// "基于模板";
    String folder();// "文件夹";
    String search_all();// "所有项目";
    String search_space();// "空间和空间描述";
    String search_and();// "文件名和内容";
    String search_only();// "文件名";
    String search_high();// "高级搜索";
    String clear_gc();// "清空回收站";
    String show_all();// "全部显示";
    String show_by_page();// "分页显示";
    String open();// "打开";
    String new_folder();// "新建文件夹";
    String properties();// "属性";
    String prop_normalTitle();//基本
    String prop_detailedTitle();//详细
    String prop_safeTitle();//安全
    String prop_create();//"已创建:"
    String prop_lockName();//"锁定者:"
    String prop_lockTime();//"锁定时间:"
    String prop_list_state_colon();//"权限:"
    String prop_approvalInfo();//审批：
    String prop_shareInfo();//共享：
    String prop_version();//版本;
    String prop_sign();//签名
    String fileName();// "文件名";
    String fileName_colon();// "文件名:";
    String fileType_colon();// "文件类型:";
    String position();// "位置:";
    String size();// "大小";
    String sharecomment();// "共享备注:";
    String size_colon();// "大小:";
    String modifyTime();// "修改时间:";
    String type();// "类型";
    String name();// "名称";
    String name_colon();// "名称:";
    String mail();// "邮件";
    String mail_colon();// "邮件:";
    String allDepartment();// "所有部门";
    String department();// "部门";
    String department_colon();// "部门:";
    String departmentStru();// "部门结构";
    String selectDepartment();// "选择部门";
    String sort();// "分类";
    String sort_colon();// "分类:";
    String subject_group();// "隶属组:";
    String list_state();// "权限";
    String list_change_time();// "最后修改时间";
    String list_path();// "路径";
    String list_primary_path();// "原路径";
    String list_delete_time();// "删除时间";
    String myShareFolder();// "共享文件夹";
    String othersShareFolder();// "共享者";
    String webOffice_share_2();// "共享者:";
    String search_option();// "搜索选项";
    String search_file();// "开始搜索";
    String author();// "作者";
    String title();// "标题";
    String search_summary();// "摘要";
    String search_allText();// "文件内容";
    String search_folder();// "搜索范围:";
    String all_folder();// "所有文件夹";
    String search_type();// "文件类型";
    String date();// "修改时间";
    String search_start_l();// "起始:";
    String search_end_l();// "结束:";
    String search_start();// "起始时间";
    String search_end();// "结束时间";
    String search_area();// "时间范围:";
    String search_select();// "选择";
    String add();// "添加";
    String edit();// "编辑";
    String ot_wp();// "常用文档";
    String head_welcome();// "欢迎";
    String head_file_manager();// "| 文件管理";
    String head_set();// "| 个人设置";
    String head_help();// "| 帮助";
    String head_out();// "| 退出";
    String rename_folder_text();// "文件夹名称：";
    String rename_text_1();// "请输入新名称：";
    String starMark_all();// "所有带星号文件";
    String fileFormat_title();// "格式转换";
    String fileFormat_format();// "转换后的目标格式";
    String fileManager_pageError();// "您输入的页数有误，请重新输入。";
    String fileManager_page_show();// "当前每页显示的记录数为";
    String fileManager_page_num();// "条";
    String fileManager_page_set();// "请输入要显示的记录个数，按回车";
    String fileListPanel_gridData_4();// "，可下载";    
    
    String fileListPanel_srarch();// "搜索结果";
    String none();// "无";
    String sendMessage();// "发送通知";
    String sendMail_title();// "发送文档";
    String sendMail_label();// "请输入邮件地址：(可输入多个地址，请用分号隔开)";
    String move_select();// "请选择";
    String move_goal();// "目标：";
    String fileType_eio();// "系统集成Office 文件";
    String fileType_doc();// "Microsoft Word 文档";
    String fileType_xls();// "Microsoft Office Excel 97-2003 工作表";
    String fileType_ppt();// "Microsoft Office PowerPoint 97-2003 演示文稿";
    String fileType_txt();// "文本文档";
    String fileType_rtf();// "RTF 格式";
    String fileType_gif();// "GIF 图像";
    String fileType_png();// "PNG 图像";
    String fileType_jpeg();// "JPEG 图像";
    String fileType_bmp();// "BMP 图像";
    String fileType_rar();// "WinRAR 压缩文件";
    String fileType_zip();// "WinRAR ZIP 压缩文件";
    String fileType_uof();// "中文办公软件标准格式";
    String fileType_db();// "数据库文件";
    String fileType_dot();// "Microsoft Word 模板";
    String fileType_xlt();// "Microsoft Office Excel 97-2003 模板";
    String fileType_pot();// "Microsoft Office PowerPoint 97-2003 模板";
    String fileType_eit();// "系统集成Office 模板文件";
    String fileType_eiw();// "系统集成Office 文件";
    String fileType_wmf();// "WMF 图像";
    String fileType_emf();// "EMF 图像";
    String fileType_tiff();// "Microsoft Office Document Imaging 文件";
    String fileType_dbf();// "DBF 文件";
    String loginName();//"登录名"
    String uploadService_error_12();// "该邮件地址已存在";
    String file();// "文件";
    String user();// "用户管理";
    String set();// " 服务器邮箱设置";
    String address();// "地址";
    String submit();// "提交";
    String server_mail();// "服务器电子信箱";
    String mailAddress();// "邮件服务器地址";
    String poss();// "登录服务器密码";
    String warn_modify_sucess();// "成功修改了信息。";
    String warn_modify_failure();// "修改信息失败。";
    String password_old();// "当前密码:";
    String password_new();// "新密码:";
    String password_again();// "确认密码:";
    String warn_pw_nopw();// "请输入新密码。";
    String warn_pw_old();// "请输入当前密码。";
    String warn_pw_old_error();// "您输入的当前密码不正确。";
    String warn_pw_norepw();// "请输入确认密码。";
    String warn_pw_errorPw();// "密码不一致。";
    String warn_pw_success();// "密码已经改变。";
    String code_browse();// "浏览...";
    String lead_button();// "用户导入";
    String loadFile_button();// "License导入";
    String delete_title();// "删除用户";
    String deleta_content();// "您确定要删除所选用户吗？";
    String warn_forbid();// "请选择要禁止的用户。";
    String warn_delete();// "请选择要删除的用户。";
    String forbided();//用户已被禁止。
    String freeHibided();//用户已被释放。
    String edit_forbid();// "是/否禁止用户";
    String edit_device();// "用户移动设备管理";<发改委需求>
    String edit_device_true();//启用
    String edit_device_false();//禁用
    
    String mobDevName();//设备名称
    String mobDevOS();//操作系统
    String mobDevID();//设备ID
    String mobDevTime();//时间
    String mobDevStatus();//状态
    
    
    
    String edit_email();// "电子邮件地址";
    String edit_name();// "用户名";
    String edit_duty();// "职务";
    String edit_storay();// "存储空间";
    String new_user();// "新建用户";
    String password();// "密码";
    String groupName();// "组名";
    String groupName_colon();// "组名:";
    String new_storay();// "存储空间(MB)";
    String repassword();// "确认密码";
    String passowrd_null();// "密码不能为空。";
    String repassowrd_null();// "确认密码不能为空。";
    String errorPassowrd();// "两次输入的密码不一致。";
    String warn_valid_nember();// "存储空间必须大于等于1MB。";
    String warn_create_sucess();// "成功创建一个用户。";
    String warn_create_error();// "邮件名或者用户名重复。";
    String email_error();// "邮件格式不合法。";
    String editGroupInfo();// "编辑组";
    String newGroup_title();// "新建组";
    String newGroup_des();// "描述:";
    String edit_member();// "编辑成员";
    String edit_member_colon();// "编辑成员:";
    String newGroup_modify();// "修改";
    String warn_successGroup();// "成功创建一个组。";
    String fresh();// "刷新";
    String allSelected();// "全选";
    String noAllSelected();// "全不选";
    String reverse();// "反向选择";
    String name2();// "姓名";
    String warn_ondi();// "存储空间只能是数字。";
    String warn_max_userNameLeng();// "用户名的长度不能大于32。";
    String warn_tooLeng();// "组名长度不能大于32。";
    String warn_passwordLength();// "用户密码最大长度为10。";
    String warn_pw_errorLen();// "密码最大长度为10。";
    String warn_userName_null();// "姓名不能为空。";
    String log_showWarn();// "起始时间不能大于截止时间。";
    String system_mainTenance();// "系统维护";	
    String db_backup_title();// "库备份";
    String db_backup_auto();// "数据库自动定时热备份设置：";
    String db_backup_state();// "状态：";
    String db_backup_startup();// "已启用";
    String db_backup_stop();// "已停用";
    String db_backup_savePath();// "数据库热备份保存路径：";
    String db_backup_manPower();// "手动热备份数据库整库：";
    String db_backup_restore();// "数据库还原:";
    String db_backup_importPath();// "恢复数据库导入文件路径：";
    String db_backup_save();// "保存设置";
    String db_backup_error_0();// "数据库备份将需要花费较长的时间，请确定是否继续。";
    String db_backup_error_1();// "您将进行整库恢复还原，原数据库将被覆盖，您确认要进行此操作吗？";
    String db_backup_error_2();// "您的数据库已完成备份。";
    String db_backup_error_3();// "您的数据库已完成还原。";
    String db_backup_error_7();// "由于服务器空间不够，您的备份请求被中止，您可以在增加空间后再进行尝试。";
    String db_backup_error_8();// "文件不存在，无法完成操作。";
    String db_backup_error_9();// "您没有权限，无法完成操作。";
    String db_backup_error_10();// "备份失败。";
    String db_backup_error_11();// "请选择文件。";
    String db_backup_error_12();// "还原失败。";
    String db_backup_error_13();// "保存设置成功。";
    String db_backup_error_14();// "保存设置失败。";
    String db_backup_error_15();// "请经常检查您的备份磁盘空间，以免空间不够。你确定要保存设置吗？";
    String immediate_backup();// "立即备份";
    String day();// "每天";
    String week();// "每周";
    String month();// "每月";
    String startTime();// "备份时间：";
    String backup_startDate();// "备份起始日期：";
    String restore();// "恢复还原";
    String selectPath();// "选择路径：";
    String monday();// "星期一";
    String tuesday();// "星期二";
    String wednesday();// "星期三";
    String thursday();// "星期四";
    String friday();// "星期五";
    String saturday();// "星期六";
    String sunday();// "星期日";
    String one();// "一日";
    String two();// "二日";
    String three();// "三日";
    String four();// "四日";
    String five();// "五日";
    String six();// "六日";
    String seven();// "七日";
    String eight();// "八日";
    String nine();// "九日";
    String ten();// "十日";
    String eleven();// "十一日";
    String twelve();// "十二日";
    String thirteen();// "十三日";
    String fourteen();// "十四日";
    String fifteen();// "十五日";
    String sixteen();// "十六日";
    String seventeen();// "十七日";
    String eighteen();// "十八日";
    String ninteen();// "十九日";
    String twenty();// "二十日";
    String twenty_one();// "二十一日";
    String twenty_two();// "二十二日";
    String twenty_three();// "二十三日";
    String twenty_four();// "二十四日";
    String twenty_five();// "二十五日";
    String twenty_six();// "二十六日";
    String twenty_seven();// "二十七日";
    String twenty_eight();// "二十八日";
    String twenty_nine();// "二十九日";
    String thirty();// "三十日";
    String thirty_one();// "三十一日";
    String import_export();// "导入导出";
    String file_import();// "文件导入";
    String file_export();// "文件导出";
    String forbid_1();// "禁止";
    String cancel_forbid();// "取消禁止";
    String userId();// "用户ID";
    String import_selectFile();// "请选择文件:";
    String import_file();// "导入";
    String import_update();// "文件上传";
    String import_member_label_0();// "1.您可以选择一个EIO文件作为信息来源，批量增加系统用户";
    String import_member_label_1();// "2.您也可以选择下载系统提供的模板文件来编辑用户信息";
    String import_member_label_3();// "系统模板:";
    String import_member_label_4();// "用户信息模板.eio";
    String import_member_next();// "下一步";
    String import_member_last();// "上一步";
    String import_member_complete();// "完成";
    String modifeUser_passwordReset();// "密码重置";
    String hour();// "时";
    String minute();// "分";
    String second();// "秒";
    String operate_type_login();// "登录日志";
    String operate_type_quit();// "退出系统";
    String manage_log();// "日志管理";
    String log_summary();// "日志概况";
    String log_manage();// "日志管理";
    String total_days();// "总统计天数：";
    String total_access();// "总访问量：";
    String year_access();// "今年访问量：";
    String month_access();// "本月访问量：";
    String day_access();// "今日访问量：";
    String average_access();// "平均每日访问量：";
    String recent_record();// "最近10条日志";
    String log_type_result();// "日志类型";
    String log_type();// "日志类型：";
    String all_log();// "所有日志";
    String user_id();// "用户：";
    String clear_user();// "清空";
    String start_time();// "起始时间：";
    String end_time();// "截止时间：";
    String log_ip();// "IP地址";
    String ip_address();// "IP地址：";
    String log_remark_result();// "备注";
    String remark();// "备注：";
    String operate_type();// "操作类型：";
    String operate_time();// "操作时间：";
    String type_search();// "查询";
    String type_export();// "导出";
    String start_execute();// "开始执行";
    String log_visitor();// "访问者";
    String log_date_time();// "日期时间";
    String log_visit_type();// "访问类型";
    String log_search_result();// "查询结果";
    String log_user_name();// "用户姓名";
    String log_time();// "时间";
    String spaceSize();// "空间大小";
    String personalInfo();// "个人信息";
    String contacts();// "联系人";
    String group();// "组";
    String option();// "目录设置";
    String include();// "共包含";
    String files_count();// "个文件";
    String _folder();// "个文件夹";
    String use_space();// "已用空间";
    String email();// "邮件地址:";
    String save();// "保存";
    String modify_password();// "修改密码";
    String delete_group();// "删除组";
    String memList();// "成员列表";
    String gp_create();//创建协作项目
    String gp_del();//删除项目
    String gp_modify();//修改协作项目
    String gp_allMeber();// "成员数";
    String gp_mifMeb();// "成员管理";
    String gp_editInfo();// "编辑组信息";
    String hide_share();// "隐藏“我的共享”";
    String hide_label();// "隐藏“我的标签”";
    String hide_usedDocument();// "隐藏“最近的文档”";
    String insert_contacts();// "添加联系人";
    String delGroup_clew();// "您确定要删除所选组?";
    String info_title_data();// "数据读取中……";
    String info_title_data2();//数据处理中……
    String info_title_file();// "服务器正在解析文件……";
    String grid_loading();// "正在加载数据……";
    String open_local_title();// "打开本地文件";
    String open_local_label_2();// "注：*支持的文件格式是eio/xls/doc/ppt";
    String window_title();// "共享文档";
    String field_title();// "电子邮件主题:";
    String field_text();// "与您共享的文档";
    String label_text();// "*邮件内容中将会包含在线文档的链接；邮件内容最多250个字符";
    String sendInvite();// "发送邀请";
    String checkBox_text();// "指定邮件内容";
    String add_group();// "添加联系组";
    String grid_title_0();// "联系人/组";
    String grid_title_1();// "共享用户";
    String read_only();// "只读";
    String read_write();// "读写";
    
    String no_linkMan_or_group();// "没有联系人";
    String no_share_linkMan_or_group();// "没有共享用户";
    String contacts_title();// "联系人列表";
    String group_title();// "联系组列表";
    String grid_emptyText_2();// "没有联系人";
    String grid_emptyText_3();// "没有联系组";
    String grid_no_message();// "没有找到匹配的联系人";
    String grid_name_emal();// "名称或邮件";
    String grid_search_info();// "按Enter键即可进行搜索";
    String grid_search_keyword();// "请输入搜索关键字";
    String group_member_title();// "成员";
    String file_share_alter();// "重命名该文件夹（文档）后，将不能共享它，确定要继续吗？";
    String sendMail_cont_1();// "通过系统网络Office系统给您发送了文件,此邮件接收的文件URL如下,文件将在服务器上保存10天,请尽快点击此URL链接下载。";
    String sendMail_cont_2();// "给您发送了文档";
    String sendMail_cont_5();// "自定义邮件地址";
    String sendMail_title_2();  //"您可以把文件的地址发送到以下列表中的人员"
    String logID();//序号
    String logreader();//操作者
    String readedDate();//操作日期
    String logfile();//操作类型
    
    String access_log();//访问日志
    String fileOper_log();//文件操作日志
    String filePath();//文件路径
    
    
    //===============  工作流增加 =====================
    String fileManage();//文件管理;
    String workflowManage(); // 流程管理
    String userTask();// 我的任务
    String userCreateTask();//我发起;
    String userProcess();// 待处理;
    String userAgent();// 我代理
    String userReply();// 我批复
    String templatesList();// 模板列表
    String workflowCount();// 流程统计
    String listMode();// 列表;
    String chartMode();// 图表
    String state();//状态
    String createAuthor();//发起人
    String createTime();// 发起时间
    String reachTime();// 到达时间
    String overTime();// 逾期时间
    String applyNote();// 申请说明
    String operation();// 操作
    String accessory(); // 附件
    String detailContent() ;// 详细内容
    String reply(); // 批复
    String agent(); // 代理
    String description_wf();// 描述
    String create_wf();// 发起
    String view();//查看
    String state_paending(); //待审批
    String state_active(); // 审批中
    String state_completed();// 已完成
    String state_aborted();// 已中断
    String state_suspended();// 已挂起
    String count_completed();// 已完成
    String count_active();// 审批中
    String count_amount();// 总数
    String count_averageTime();// 平均时间
    String count_maxTime(); // 最长时间
    String count_minTime();// 最短时间
    String count_summary();// 汇总
    //weboffice2.0
    String roleManager();// "角色管理";
    String roleDes();// "角色描述";
    String userPower();//用户权限设置
    String role();// "角色";
    String addRole();//"增加角色"
    String delRole();//"删除角色"
    String editRole();//"修改角色"
    String editUserRole();//"编辑角色成员"
    String editPowerRole();//"编辑角色权限"
    
    // 系统监控和系统报表增加国际化资源
    String sys_Monitor();// 系统监控
    String sys_Report(); // 系统报表
    String sys_ServerStatus(); // 服务器状态
    String sys_BaseInfo();// 基本信息
    String sys_ServerName(); // 名称
    String sys_Domain(); // 域名
    String sys_Performance(); // 性能
    String sys_Memory() ;// 内存
    String sys_Store(); // 存储
    String sys_Used();//使用
    String sys_InstallComponent(); // 中间件
    String sys_SystemRunTime(); // 系统运行时间
    String sys_NormalRun(); //正常运行 
    String sys_SystemStatus(); // 系统状态
    String sys_UserInfo(); // 用户信息
    String sys_CompanyName(); // 公司名称
    String sys_DepartmentCount();// 部门数量
    String sys_AccountCount();// 帐号数量
    String sys_OnlineAccount(); // 在线帐号
    String sys_OnlineAccountList(); // 在线帐号列表
    String sys_DetailedList(); // 详细列表
    String sys_DocumentInfo();// 文档信息
    String sys_SpaceCount();//空间数量
    String sys_DepartmentSpaceCount(); //部门空间数量
    String sys_ProjectSpaceCount(); //项目空间数量
    String sys_documentCount();// 文档数量
    String sys_contentSize(); // 文档库容量
    String sys_TodayAddDocumentCount(); // 今天增加的文档数
    String sys_TodayChangeDocumentCount(); // 今天修改的文档数
    String sys_TodayDeleteDocumentCount(); // 今天删除的文档数
    String sys_StartDate(); //开始日期
    String sys_EndDate(); // 结束日期
    String sys_Cycle(); // 周期
    String sys_Day(); // 天周月
    String sys_Week(); // 周
    String sys_Month(); // 月
    String sys_Reset(); // 重置
    String sys_CreateReport(); // 产生报表
    String sys_Grid_Date();// 日期
    String sys_Grid_AccountCount();// 用总数;
    String sys_Grid_ContentSize();// 文档库容量;
    String sys_Grid_SpaceCount();// 空间数;
    String sys_Grid_DocumentCount();// 文档数;
    String sys_Export(); //导出
    String search_anytime();// "任何时间";
    String search_week();// "最近一周";
    String search_month();// "最近一月";
    String search_threeMonth();// "最近三月";
    String search_sixMonth();// "最近六月";
    String search_preciousYear();// "去年";
    String search_customize();// "自定义";
    String search_fileType_allType();// "所有类型";
    String search_fileType_eio();// "eio";
    String search_fileType_doc();// "doc";
    String search_fileType_xls();// "xls";
    String search_fileType_ppt();// "ppt";
    String search_fileType_webFile();// "网页文件";
    String search_fileType_pdf();// "pdf";
    String search_fileType_eit();// "eit";
    String search_fileType_txt();// "txt";
    String search_fileType_rtf();// "rtf";
    String search_star_field();// "星号标记";
    String search_star_none();// "无";
    String search_star_one();// "*";
    String search_star_two();// "**";
    String search_star_three();// "***";
    String approval_Approval();// 审批
    String approval_Set();// 审批设置;
    String approval_ApprovalStep(); // 审批步骤;
    String approval_Step(); // 步骤
    String approval_Reject(); // 拒绝
    String approval_Pass(); // 核准
    String approval_Comment(); // 备注
    String approval_AddStep(); // 添加步骤
    String approval_DeleteStep(); // 删除步骤
    String approval_UpStep();// 上移步骤
    String approval_DownStep(); // 下移步骤
    String approval_View(); // 查看
    String approval_Status();// 审批状态
    String approval_State_Paending(); //待审批
    String approval_State_Active(); // 审批中
    String approval_State_Completed();// 已审结
    String approval_State_Aborted();// 已终止
    String approval_ViewMessage_Paending();// 已由(1)提交，(2)审批，当前正由(3)复审，下一个复审者(4)
    String approval_ViewMessage_Active();// (2)已审批，当前正由(3)复审，下一个复审者(4)
    String approval_ViewMessage_Aborted();// (2)审批不通过
    String approval_ViewMessage_Commpleted();// 已由(2)审查通过
    String approval_SelectUsers();// 请选择审批用户
    String approval_SendCommentMessage();// 请在此编辑你需要发送给审批者的备注信息
    String approval_ApprovalAction(); //动作
    String approval_ApprovalCreate();// 提交
    String approval_ApprovalPending(); // 待定
    String approval_approvalDoc();//审批文档
    String modifeUser_currentpswd();//当前密码
    String admin_set();// "管理员设置";
    String add_member();//添加成员
    String license_manage();//许可证管理
    String news_release();//新闻发布
    
    String roleerror();//角色名称必须有。
    String error_no_user();// "您已经处于离线状态，请重新登录。";
    String select_one_file();// "请选取一个文件。";
    String offLine_error_1();// "请选择要离线的文件。"
    String file_error_8();// "此文档已被删除。";
    String noSelect_error_0();// "请选中至少一个文件。";
    String sendMail_error_0();// "请选择发送的文件。";
    String sendMail_error_1();// "不允许发送文件夹，请选择文件。";
    String download_error();// "您没有权限下载或者文件不存在。";
    String offLine_error();// "您没有权限离线编辑或者文件不存在。";
    String login_error_1();// "邮件地址不存在。";
    String msg_no_select();// "未选择任何文件或文件夹。";
    String select_one();// "请选择单个文件或文件夹。";
    String msg_forever_delete_0();// "您确定要删除？";
    String msg_forever_delete_1();// "项目已被永久删除。";
    String msg_gc_null();// "回收站中没有项目。";
    String msg_redelete_0();// "还原回收站中的所有项目吗？";
    String msg_redelete_1();// "选中的项目已恢复。";
    String msg_clear_gc_0();// "清空回收站将会永久删除回收站中的所有项目。是否继续？";
    String msg_clear_gc_1();// "回收站已被清空。";
    String msg_clear_gc_2();// "已存在";
    String msg_clear_gc_3();// "个同名文件或文件夹，如果还原将删除现有的同名文件或文件夹。是否确定要还原？";
    String msg_samename_0();// "个同名文件或文件夹，如果";
    String msg_samename_1();// "将覆盖现有的同名文件或文件夹。是否确定要";
    String msg_samename_2();// "？";
    String msg_num();// "个项目";
    String msg_delete_0();// "已移至回收站。";
    String msg_delete_1();// "未作删除。";
    String msg_copy_1();// "未作复制。";
    String msg_copy_2();// "为他人共享文件，";
    String msg_move_1();// "未作移动。";
    String msg_move_2();// "为共享文件，";
    String msg_move_3();// "正在编辑中，";
    String msg_move_4();// "为同名文件，";
    String msg_move_5();// "为审批文件，";
    String msg_move_error_2();// "目标文件夹和源文件夹相同，无法移动。";
    String msg_move_error_0();// "无法移动";
    String msg_copy_error_0();// "无法复制";
    String msg_copy_error_1();// "，目标文件夹和源文件夹相同。";
    String msg_move_error_1();// "该文件已设为共享，不允许移动。";
    String msg_edit_file_error();// "该文件正在编辑中，暂不支持此项操作，请退出编辑后重试。";
    String rename_error_0();// "请输入新名称。";
    String rename_error_1();// "已存在同名文件。";
    String rename_error_2();// "名称中含有不合法字符。";
    String rename_error_3();// "共享给其他人的文件不能重命名。";
    String rename_create_0();// "请输入文件夹名称。";
    String rename_create_1();// "已存在同名文件夹。";
    String rename_create_2();// "文件夹名称不合法。";
    String mail_null();// "邮件地址不能为空。";
    String mail_error();// "邮件地址不合法。";
    String time_set_error();// "时间范围设定不合法。";

    String edit_selectUser();// "请选中一个用户进行编辑。";
    String edit_select();// "请选中要释放的用户。";
    String import_error_0();// "无效文件名，请重新选择文件。";
    String import_error_1();// "文件解析错误，请重新选择文件。";
    String shareGroup_alert_0();// "请选中要添加的用户。";
    String null_group_name();// "组名不能为空。";
    String isRepeat();// "组名不能重复。";
    String no_groupIn();// "请选择要删除的组。";
    String inputGroup();// "请输入部门名。";
    String save_success_clew();// "选项设置修改成功。";
    String upload_img_msg_0();// "无效的文件格式。";
    String upload_img_msg_5();// "无效的文件路径。";
    String label_msg();// "邮件内容不能超过250个字符。";
    String contacts_error_message();// "请至少选择一个联系人。";
    String group_error_message();// "请至少选择一个组。";
    String share_error_0();// "请选择一个或者多个联系人/组。";
    String share_error_1();// "已将";
    String share_error_2();// "共享给你了，一起修改。";
    String share_error_3();// "没有可设置的共享信息，请添加、删除或修改您的共享信息。";
    String sendMail_cont_3();// "服务器发送邮件失败，请联系系统管理员。";
    String sendMail_cont_4();// "服务器中转文件失败，请联系系统管理员。";
    String error_remote(); //"远程调用异常，请联系系统管理员。"
	String share_no_user();//"请选择需要共享的用户。";
    String text_size_limited();//限制文本框长度。
    String task_exe_message_error();//流程任务执行错误。
    String task_exe_message_success();//流程任务执行成功。
    String approval_ErrorMessage();// 审批中的文档不能提交审批。
    String approval_ErrorMessage_ResetApproval();// 只有原提交审批者才能重新提交审批。
    String approval_ErrorMessage_NoOne();//至少要选择一个审批人。
    
    String limitUpload();//你没有上传的权限。
    String limitDel();//你没有删除的权限。
    String limitCopy();//你没有复制粘贴的权限。
    String limitDown();//你没有下载的权限。
    String limitOff();//你没有离线编辑的权限。
    String uploadFolder();//你没有选择上传的目录。
    String limitRename();//你没有重命名的权限。
    String limitLook();//你没有浏览的权限。
    String limitSend();//你没有发送的权限。
    String limitRead();//你没有读取的权限。
    String limitMove();//你没有移动的权限。
    String limitPrint();//你没有打印的权限。
    String limitCreate();//你没有新建的权限。
    String limitVersion();//你没有版本的权限。
    String limitLock();//你没有解锁/锁定的权限。
    String limitApproval();//你没有审批的权限。
    String noPermission();//没有权限进行该操作。
    String rename_error_4();//文件夹名称过长
    
    String desktopEdit(); // 启用桌面Office
    String onlineEdit();  // 启用网络Office
    String signFile();    // 签批文档
    String fileTitle();   // 文件标题
    String flowProceeding();   // 流程事项
    String number();    // 编号
    String kind  ();    // 分类
    String currentPermit();   // 当前权限
    String currentStep();     // 当前步骤
    String currentUser();     // 当前处理人
    String viewVersion();     // 查看版本
    //国际化处理找出的程序内部变量
    String backflow_unprocessed();	//未处理
    String backup_exceptionError();	//异常错误：
    String ext_pleaseSelect();	//请选择
    String ext_others();	//其他
    String ext_errors();	//错错错错
    String ext_linkNetworkCompany();	//林克网络有限公司
    String ext_haveNewEmail();	//你有新邮件了！
    String ext_haveNewSuggest();	//有新建议了!
    String ext_sendedMailTip();	//已发邮件提醒了！
    String drag_moveInfo();	//文件移动信息
    String drag_moveLostInfo();	//文件移动将丢失版本与签名信息，是否继续？
    String drag_appFileCannotMove();//审批文件不允许移动。
    String drag_sameFileName();	//已存在同名文件或文件夹，如果移动将覆盖现有的同名文件或文件夹。是否确定要移动?
    String drag_systemNotice();//系统通知
    String file_publicFiles();	//公共文档
    String power_permitProperty();//权限属性：
    String power_selectPerson();//请选择您需要分配权限的用户：
    String hideDepartment();//隐藏部门
    String showDepartment();//显示部门
    String power_columnUser();	//用户
    String power_columnAllPermit();//所有权限
    String power_msgSelectPermitUser();//请选择需要分配权限的用户。
    String user_clickInfo();//单击头像可以直接与其聊天
    String workspace_icon();//图标:
    String workspace_changeIcon();//更改图片
    String workspace_spaceName();//名称
    String workspace_spaceStatus();//状态
    String workspace_spacedesc();//描述
    String workspace_groupName();//工作组名
    String workspace_editMember();//成员:
    String workspace_creator();//创建者
    String workspace_desc();//工作组描述
    String workspace_msgEmpty();//'空间名称'不能为空或空格。
    String workspace_msgLimitNameLength();//'空间名称'字符数不能超255。
    String workspace_msgLimitStatusLength();//'空间状态'字符数不能超255。
    String workspace_msgLimitDescLength();//'空间描述'字符数不能超255。
    String workspace_msgGroupNameEmpty();//'工作组名'不能为空。
    String workspace_msgLimitGroupNameLength();//'工作组名'字符数不能超255。
    String workspace_msgNoCharger();//工作组没有指定负责人。
    String workspace_msgLimitGroupDescLength();//'工作组描述'字符数不能超255。
    String workspace_uploadPicture();//上传图片
    String workspace_image();//图片:
    String workspace_selectOneFile();//请选择一个文件
    String workspace_formatNotSupport();//上传的文件格式不支持
    String workspace_sizeLimit();//上传图像文件大小不能超过80k!
    String workspace_uploadFail();//文件上传失败
    String workspace_atLeastOneSelect();//请至少选中一个工作空间。
    String workspace_delConfirm();//用户空间删除后不可恢复,是否要确定删除?
    String workspace_columnSpaceName();//空间名
    String workspace_columnSpaceDesc();//空间描述
    String workspace_columnSpaceID();//空间ID
    String workspace_editOrDelNotice();//编辑或删除公告
    String workspace_atLeatOneNotice();//请至少选中一项公告。
    String workspace_cannotEditMore();//不可同时选择多项进行编辑。
    String workspace_addNotice();//添加公告
    String workspace_notice_title();//标题
    String workspace_notice_titleEmpty();//'标题'不能为空。
    String workspace_notice_titleLimit();//'标题'字符数不能超255。
    String workspace_notice_createTime();//创建时间
    String workspace_notice_content();//内容
    String workspace_notice_contentLimit();//'内容'字符数不能超255。
    String workspace_notice_ID();//公告ID
    String workspace_noSelectUser();//没有选择用户
    String workspace_spaceCreator();//工作组创建者(
    String workspace_mustBeOne();//)必须是成员之一。
    String workspace_cannotDelete();//)不能删除。
    String workspace_notAllowEidtCreator();//"不允许修改创建者("
    String workspace_permitType();//)的角色类型。
    String workspace_selAddPerson();//请选择需要增加的成员：
    String workspace_defaultRole();//默认角色：
    String workspace_notice();//公告
    String workspace_currentSpace();//当前工作空间
    String workspace_noPermitCreate();//你没有权限创建新的工作空间。
    String approvalFile();//送审文档
    String concludeFile();//审结文档
    String pendingFile();//待审文档
    String passedFile();//已审文档
    String file_error();//出错了
    String file_eidtTime();//文件 修改日期: 
    String select();//选择
    String select_msg();//个对象
    String file_unitFolder();//单位文件夹
    String file_departFolder();//部门文件夹
    String file_departShare();//部门共享
    String file_otherShare();//其他共享
    String shareDocument();//共享文档
    String spaceNotExits();//空间不存在，无法搜索。
    String fileAlreadyLocked();//该文件已经锁定!
    String fileNotLocked();//该文件没有锁定!
    String fileOnlyBe();//该文件只能由 
    String fileUnlock();// 解锁
    String lockSuccess();//锁定成功
    String unlockSuccess();//解锁成功
    String cannotReadFormat();//不是支持的格式，无法浏览。
    String noShareInfo();//没有共享记录。
    String noSpaceToCreateFolder();//空间不存在，无法创建文件夹。
    String fileBeLockedBy();//该文件被 
    String lockedCannotRename();// 锁定，不能重命名！
    String msg_delete1();//确实要将这 
    String msg_delete2();// 项放入回收站吗？
    String msg_delete3();//确实要把“
    String msg_delete4();//”放入回收站吗？
    String msg_noPermitToDownload();// 没有权限下载,你确定要继续下载吗？
    String msg_signSuccess();//签名成功。
    String msg_getSignInfoFail();//获取签名信息失败。
    String msg_getPrinterFail();//获取打印机失败。
    String msg_noOfflinePermit();// 没有权限离线编辑,你确定要继续离线编辑吗？
    String msg_limitUpload();//不能上传exe/msi/jsp/bat文件，小于10M
    String msg_fileFomartError();//对不起，不能上传exe、jsp、msi、bat文件
    String uploadInfo();//文件上传信息
    String uploadTips();//上传文件名中有系统中非法字符\"@/:|*[]\"，如果继续上传，则该非法字符会被替换为\"_\"，是否继续？
    String sameNameTips();//有同名文件，是否继续？
    String msg_noPermitAddMem();//你没有权限添加新成员。
    String dbClickType();//设置
    String createSpace();//创建空间
    String deleteSpace();//删除空间
    String managePerson();//管理成员
    String uploadMulti();//批量上传
    String copyNumbers();//复制份数
    String copies();//份
    String selectFolders();//请选择文件夹。
    String noFolderPermit();//该文件夹没有权限。
    String fail();//失败
    String limitCopyNumbers();//复制份数必须介于1-10份之间，请重新输入。
    String cannotReadAsPic();//不支持图片方式浏览。
    String cannotOpenFomat();//不是支持的格式，无法打开
    String errorTips();//错误提示
    String page();//页数
    String commitTime();//提交时间
    String selectPrinter();//选择打印机：
    String printCopies();//打印份数：
    String totalPrint();//全部打印
    String partPrint();//区间打印
    String selectPrinterMsg();//请选择打印机。
    String inputNumberMsg();//打印份数请输入正整数。
    String inputPageNumMsg();//请输入开始与结束打印页码。
    String inputNumMsg();//开始与结束打印页码请输入正整数。
    String fileTypeNotSupport();//网络打印不支持该格式文档!
    String printFail();//打印失败。
    String printSuccess();//打印成功。
    String to();//到：
    String from();//由
    String yi();//已
    String total();//共
    String ge();//个
    String approvePerson();//审批人员
    String approveTime();//审批时间
    String modifyAuthor();//修改者
    String changeSumm();//更改摘要
    String rename_error_5();//文件名称的第一位不能够用 '.' !
    String shareLog();//查看共享文件日志
    String col_unRead();//没有阅读
    String col_read();//阅读
    String col_delete();//删除
    String backup();//备份
    String combine();//合并
    String deleteShare();//删除共享
    String modifyShare();//修改共享
    String digCerSigned();//数字证书签名
    String fileSysSigned();//文档系统签名
    String fileNotDigCerSigned();//该文档没有进行数字证书签名
    String fileNotSigned();//该文档没有进行签名
    String fileSign();//对该文档签名，签名验证
    String cerIs();//证书为
    String digCerFrom();//数字证书系统，由
    String zai();//在
    String ideaBank();//意源
    String song();//宋体
    String pass();//通过
    String notPass();//不通过
    String viewSign();//查看签名
    String document();//文档
    String signResult();//签名验证结果：
    String memoContent();//摘要内容:
    String memoContent_col();//摘要内容
    String inputMemo();//请输入摘要。
    String memoLimitSize();//摘要输入请小于100个字符!
    String memoContentIllg();//输入内容中有非法字符!
    String userNameIllg();//用户名中含有非法字符，无法保存版本信息！
    String current();//当前
    String newVerSuccess();//新建版本成功。
    String modifyMemoSuccess();//修改版本摘要成功。
    String restoreVerSuccess();//复原版本成功。
    String backVerSuccess();//回滚版本成功。
    String finalVerSuccess();//定稿成功。
    String createVersion();//创建版本
    String recoverVersion();//恢复版本
    String rollbackVersion();//回滚版本
    String deleteVersion();//删除版本
    String deleteAllVerson();//删除所有版本
    String modifyMemo();//修改摘要
    String finalVersion();//定稿
    String fianlEditTime();//最后修改时间
    String selectOneVersion();//请选择一个版本。
    String msg_delVer();//删除版本中包含最后一个版本，最后一个版本将保留，不能删除。
    String msg_delVerInfo();//删除版本信息
    String msg_delAllVerInfo();//删除所有版本信息
    String msg_delVerConfirm();//最后的版本不允许删除，将保留最后一个版本，确认删除版本？
    String msg_delAllVerComfirm();//最后的版本不允许删除，将保留最后一个版本，确认删除所有版本？
    String msg_delVerSuccess();//删除版本成功。
    String msg_delAllVerSuccess();//删除所有版本成功。
    String msg_delVerFail();//删除版本失败。
    String msg_delAllVerFail();//删除所有版本失败。
    String versionControl();//版本控制
    String selectPerson();//请选择联系人
    String selectSend();//请选择要发送的人员。
    String inputMailTitle();//请输入邮件主题。
    String sharePerson();//请选择您需要共享的用户：
    String shareProperty();//共享属性：
    String share_comment();//备注(字数限制在100字以内)：
    String shareCommentTip();//请在此编辑您需要发送给对方的共享备注信息
    String sendSMSNotice();//发送短信通知
    String initMsg();//正在初始化，请稍等…… 
    String user_col();//用户
    String setShareShowName();//设置共享显示名
    String shareFile();//共享文件
    String shareName();//共享名
    String reEditShareName();//已经存在，请重新编辑共享名。
    String shareNameNotEmp();//必须有共享名
    String sameFileOrFolder();//已存在同名文件或文件夹,是否覆盖?
    String sendSuccess();//发送成功
    String yishen();//已审
    String departmentFiles();//部门文档
    String fileCopyFail();//文件复制失败!
    String sysRole();//系统角色
    String spaceRole();//空间角色
    String roleType();//角色类型
    String selectRoleUser();//请选择您需要设置角色的用户：
    String selectUserDel();//请选中用户后进行删除。
    String sysPermit();//系统权限
    String spacePermit();//空间权限
    String filePermit();//文件权限
    String roleID();//角色ID
    String selectRoleDel();//请选中角色后进行删除。
    String confirmDelRole();//您确定要删除选中角色?
    String delRoleSuccess();//角色删除成功。
    String selectOneRole();//请选中一个角色后进行修改。
    String sysRoleList();//系统角色列表
    String spaceRoleList();//空间角色列表
    String allGeroup();//所有组
    String selectOneDep();//请选择一个部门。
    String depName();//部门名
    String depDes();//部门描述
    String depID();//部门id
    String getBackupFail();//获取备份失败。
    String adminNoPermit();//管理员自己没有权限，不能删除自己的账号
    String selctOneUser();//请选择一个用户
    String departmentIn();//所在部门
    String cardID();//证书号
    String lastVisitTIme();//最后访问时间
    String onlyShowParOrg();//仅显示父组织
    String confirmDelOrg();//该部门含有子部门，确定要删除吗？
    String workflowDef();//工作流定义
    String exit();//退出
    String memUploadLimt();//上传的文件格式不支持，请选择txt格式的文件。
    String importUserSameName();//导入用户中有同名用户：
    String importUserSuccess();//导入成功。
    String log_startTime();//起始时间
    String log_endTime();//截止时间
    String noContentExport();//没有内容可以导出。
    String deleteSuss();//删除成功。
    String depManager();//部门负责人
    String upDepbeSelf();//上级部门不能是自身
    String upDepbeNull();//父组织不能为空
    String changePhoto();//更改头像
    String cardName();//证书登录名
    String yes();//是
    String depAdmin();//部门管理员
    String birthday();//生日
    String mobile();//手机
    String workTel();//工作电话
    String workAddress();//工作地址
    String homeAddress();//家庭地址
    String myself();//自我介绍
    String sex();//性别:
    String man();//男:
    String woman();//女:
    String logNameisEmp();//登录名不能为空
    String inputName();//请输入姓名
    String inputDuty();//请输入职务
    String uploadImages();//上传头像
    String selectUserDep();//请选择用户所在的部门
    String selectRole();//请选择角色
    String loginNameExist();//登录名已经存在。
    String selectUpDep();//选择上级部门
    String upDepartment();//上级部门
    String inputLegalNumber();//请输入合法的要显示的记录个数。
    String selectDevice();//请至少选择一个设备
    String userGroup();//用户所在的组
    String addGroup();//新增部门
    String atLeastOneOrg();//至少需要一个部门。
    String noOrgsAdd();//没有部门增加。
    String encryption();//加密
    String propertySel();//属性选择
    String sendFail();//发送失败
    String attachPath();//附件路径：
    String error_mail();//邮件地址格式错误
    String emailAddress();//邮件地址
    String depIn();//所在组织
    String inputLimit();//请输入6-10个字符
    String iAgree();//我同意
    String privacyStatement();//隐私声明
    String passwordLimit();//密码请设置6-10个字符
    String passwordNotSame();//密码验证不一致
    String regist();//注册
    String inputLoginName();//请输入登录名
    String error_LoginName();//登录名格式错误
    String inputMail();//请输入邮件地址
    String inputPassword();//请设置密码
    String inputSuitPass();//请输入合适长度的密码
    String inputConfirmPass();//请设置确认密码
    String error_userName();//姓名格式错误
    String selectOrg();//请选择部门
    String selectDuty();//请选择职务
    String agreeStatement();//请签署隐私声明
    String registSuccess();//注册成功
    String backHome();//返回首页
    String tip_1();//点击\"继续\"按钮将直接登录政务公文包系统,按指纹注册将进入指纹注册系统
    String tip_2();//点击\"继续\"按钮将直接登录政务公文包系统
    String fingerRegister();//指纹注册
    String oper_continue();//继续
    String privateState_1();//隐私权是您的重要权利。在进行用户注册、产品注册或访问系统公司网站获得某项服务或信息时您需要提供一些个人信息，您向我们提供您的个人信息是基于得到我们的服务和对我们的信任，您相信我们会以负责的态度对待您的个人信息。本隐私声明解释了该情况下需要收集的数据及其使用。请详细阅读本隐私声明。
    String privateState_2();//收集个人信息
    String privateState_3();//通常在进行用户注册或产品注册（包括电话、电传、信件或上网进行注册）时我们会收集能够识别或可以联系您的个人信息。这些信息包括姓名、性别、出生年月、身份证号、电子邮件、电话、手机等个人信息。注册后，我们会将您所提供的信息进行整理，所有信息将作为您的个人档案。
    String privateState_4();//在进行网上注册时，为保证只有您才有权对该信息进行浏览或访问，我们会要求您提供注册名并设置密码。通过电话、电传或信件进行注册的用户，在登录网站后，系统公司会根据您提供的注册名调用已有的用户档案。
    String privateState_5();//如果您想浏览或更新个人档案，只需访问系统公司网站
    String privateState_6();//，在首页位置单击修改用户信息后进行编辑。为保证只有您才可以访问或修改它，我们会要求您提供访问系统网站的用户名和密码。
    String privateState_7();//使用个人信息
    String privateState_8();//个人信息有下列作用：
    String privateState_9();//对产品进行注册，以便系统公司能及时为您提供技术支持和帮助
    String privateState_10();//系统公司能提供给您产品的升级版及最新产品的发布情况等信息
    String privateState_11();//系统公司以及系统公司网站不会在您不知情的情况下将您的个人信息透露或公开给第三方个人或公司，除非法律要求必须如此或出于善意，相信这样做的目的是为了：
    String privateState_12();//遵守法律法令或遵照司法部门对系统公司或其网站所要求的法律程序
    String privateState_13();//保护系统公司及其网站成员的权益和财产
    String privateState_14();//在紧急情况下保护系统公司、网站和公众的利益和安全
    String privateState_15();//控制和访问个人信息
    String privateState_16();//注册或通过其他方式提供的个人信息，除了以上列出的三种情况，未经您允许，系统公司不会和第三方共享这些信息。这些个人信息只用于“使用个人信息”中列出的几种情况。
    String privateState_17();//我们将为您提供确认个人信息是否正确的途径，您可以随时访问系统公司网站以阅读和更新您的个人信息。
    String privateState_18();//个人信息的安全性
    String privateState_19();//系统公司严格地保护您的个人信息并尊重您对该个人信息的使用方法。我们将细心地保护您的数据以防丢失、误用、不经授权的访问、变更或破坏。
    String privateState_20();//除上述列举的原因之外，在未经您允许的情况下系统公司不会将您的信息透露给第三方个人或公司。在本公司内部，只有很少数人才可以访问用于储藏个人信息的经密码保护的服务器。您的个人信息保存在中国或其他国家内的系统公司总部、分部或代表处。
    String privateState_21();//当然在个人信息的保密方面，您也担当着重要的角色。请不要将您的登录名和密码透露给他人，以防他人浏览或更改您的个人信息。
    String privateState_22();//系统公司保证不会在您不知情或不许可的情况下，擅自将您的个人信息或在发送电子邮件至本公司时可能产生的其他信息以任何形式透露给第三者，以保障您的隐私权。
    String yozo();//版权所有@2001-2010无锡系统软件公司
    String spaceIsNull();//空间不存在，无法新建文件。
    String approval_status_paending();//待审批
    String approval_status_agree();//审批通过
    String approval_status_returned();//审批退回
    String approval_status_abandoned();//废弃
    String approval_status_end();//已办结
    String approval_status_endToOffice();//已提交
    String approval_status_publish();//已发布
    String approval_status_archiving();//已归档
    String approval_status_destory();//待销毁
    String approval_replace_agree();//签批
    String approval_replace_returned();//退回
    String approval_replace_abandoned();//已废弃
    String approval_replace_paending();//送审
    String approval_replace_archiving();//已归档
    String approval_replace_publish();//发布
    String approval_replace_end();//办结
    String permit_1();//读写，下载
    String permit_2();//只读，下载
    String permit_3();//读写
    String permit_4();//只读
    String permit_5();//浏览
    String sysInfo_basicInfo();//基本信息
    String sysInfo_serverName();//服务器名称
    String sysInfo_serverIP();//服务器IP
    String sysInfo_serverDomain();//服务器域名
    String sysInfo_performance();//性能
    String sysInfo_totalMemorySize();//内存总数
    String sysInfo_usedMemory();//已用内存
    String sysInfo_memoryRatio();//内存使用率
    String sysInfo_cpuHZ();//CPU频率
    String sysInfo_usedCpuHZ();//CPU已使用频率
    String sysInfo_cpuRatio();//CPU占用率
    String sysInfo_diskSize();//存储设备容量
    String sysInfo_diskUsedSize();//已用存储设备容量
    String sysInfo_diskRatio();//存储设备使用率
    String sysInfo_network();//网络流量
    String sysInfo_netUpSpeed();//上传速度
    String sysInfo_netDownSpeed();//下载速度
    String sysInfo_netUpFlow();//上传流量
    String sysInfo_netDownFlow();//下载流量
    String sysInfo_middleCom();//安装的中间件
    String sysInfo_webServer();//Web服务器
    String sysInfo_dbServer();//DB 服务 器
    String sysInfo_fileRepository();//文件系统
    String sysInfo_sysRunTime();//系统运行时间
    String sysInfo_userInfo();//用户信息
    String sysInfo_companyName();//公司名称
    String sysInfo_departmentCount();//部门数量
    String sysInfo_accountCount();//帐号数量
    String sysInfo_onlineAccount();//在线帐号
    String sysInfo_onlineAccountList();//在线帐号列表
    String sysInfo_fileInfo();//文档信息
    String sysInfo_spaceCount();//空间总数
    String sysInfo_departmentSpaceCount();//部门空间部数
    String sysInfo_projectSpaceCount();//项目空间总数
    String sysInfo_documentCount();//文档总数
    String sysInfo_contentSize();//文档库的容量
    String sysInfo_todayAddDocumnetCount();//当天增加文档数
    String sysInfo_todayChangeDocumnetCount();//当天修改文档数
    String sysInfo_todayDeleteDocumentCount();//当天删除文档数
    String sysInfo_add_1();//个
    String sysInfo_add_2();//篇
    String day_();//天
    String hours_();//时
    String minutes_();//分
    String sysRep_ID();//ID
    String sysRep_reportdate();//日期
    String sysRep_accountCount();//用户数
    String sysRep_addAccountCount();//增加的用户数
    String sysRep_contentSize();//文档库容量
    String sysRep_addContentSize();//增加的文档容量
    String sysRep_spaceCount();//空间数
    String sysRep_addSpaceCount();//增加的空间数
    String sysRep_documentCount();//文档数
    String sysRep_addDocumentCount();//增加的文档总数
    String approvalMsg_1();//对不起，已有人签批过，请查看签批“历史”
    String approvalMsg_2();//对不起，
    String approvalMsg_3();//已查看过签批文档，不能被终止！
    String approvalMsg_4();//对不起，你的文档已被
    String approvalMsg_5();//送审，只有
    String approvalMsg_6();//才能终止！
    String approvalMsg_7();//对不起，已有人处理了，不能反悔！
    String approvalMsg_8();//反悔失败！
    String approvalMsg_9();//对不起，已被
    String approvalMsg_10();//送审，您不能再送审了！
    String approvalMsg_11();//对不起，您不能再送审！
    String tipMsg_1();//转PDF
    String tipMsg_2();//送审 该文档给
    String tipMsg_3();//签批 该文档给
    String tipMsg_4();//退回 该文档给
    String tipMsg_5();//成文 该文档
    String tipMsg_6();//发布 该文档
    String tipMsg_7();//已归档 该文档
    String tipMsg_8();//待销毁 该文档
    String tipMsg_9();//已终止 该文档
    String read_true();//已经阅读
    String read_false();//未阅读
    String commentIs();//备注信息为
    String exists();//已经存在！
    String convertFail();//转换失败!
    String getFileFail();//获取文件失败!
    String replaceFile();//替换文件
    String toSign();//待签
    String calendar_msg_1();//活动-【
    String calendar_msg_2();//】将在
    String calendar_msg_3();//开始
    String update();//更新
    String noPermitSave();//没有权限保存
    String mailTip_1();//一次收取邮件超过
    String mailTip_2();//封，我们将采用后台收取方式.
    String mailNotExist();//邮件不存在
    String mailNoTitle();//无主题
    String passResetMsg_1();//您的密码已经重置为
    String passResetMsg_2();//，请登录后修改！
    String resetMessage();//系统密码重置通知
    String uploadSuccess();//上传成功
    String createApproFile();//建立审核文档
    String signDocument();//审核该文档！
    String restoreVersion();//复原
    String rollback();//回滚
    String shareToyou_1();//给您共享了《
    String shareToyou_2();//》，备注：
    String shareToyou_3();// 请登录政务移动协同办公系统查看。祝您工作愉快！
    String fileTip_approval();//送审了文件
    String fileTip_share();//共享了文件
    String fileTip_add();//夹
    String fileTip_toyou();//给您
    String fileTip_operfile();//对文件
    String fileTip_cancelshare();//取消了共享
    String fileTip_youhave();//您有一个
    String fileTip_youhaveFile();//您有一个文件
    String fileTip_needSign();//待审任务
    String fileTip_shareInfo();//共享的信息
    String fileTip_cclShareInfo();//被取消共享的信息
    String regret();//反悔
    String fileTip_hanFile();//了文件
    String unknown();//未知
    String copyFile();//复件
    String timeStart();//定时器已启动
    String alAddTask();//已经添加任务
    String leaderAudit();//领导签批
    String date_col();//日期：
    String workflow_createFinish();//创建好
    String workflow_ready();//准备好
    String workflow_assigned();//已分配
    String workflow_handing();//处理中
    String workflow_hangup();//挂起中
    String workflow_complete();//已完成
    String workflow_fail();//失败
    String workflow_error();//错误
    String workflow_exit();//退出
    String workflow_abolish();//废止
    String myContacts();//我的联系人
    String sysOuterAddr();//系统外部联系人
    String notRead();//未阅
    String readed();//已阅
    String yearworkday();//年工作日志
    String am();//上午
    String pm();//下午
    String windowRegist();//窗口登记
    String BasicInfoExist();//该基本信息已经存在
    String captureInfo();//按住鼠标左键不放选择截图区,双击左键截图并保存,单击鼠标右键退出程序!
    String selectFile();//请选择文件进行操作!
    String regretSuccess();//文档反悔成功!
    String regretFail();//文档反悔失败!
    
    String sysAdmin();//系统管理员
    String userAdmin();//用户管理员
    String auditAdmin();//审计员
    String secAdmin();//安全员
    String role_0();//主管领导
    String role_1();//分管领导
    String role_2();//非领导
    String new_templates();//基于模板新建
    String templates_1();//办法
    String templates_2();//报告
    String templates_3();//公告
    String templates_4();//公约
    String templates_5();//规程
    String templates_6();//规定
    String templates_7();//规范
    String templates_8();//规则
    String templates_9();//函
    String templates_10();//会议纪要
    String templates_11();//决定
    String templates_12();//令
    String templates_13();//批复
    String templates_14();//请示
    String templates_15();//守则
    String templates_16();//条例
    String templates_17();//通报
    String templates_18();//通告
    String templates_19();//通知
    String templates_20();//细则
    String templates_21();//议案
    String templates_22();//意见
    String templates_23();//章程
    String templates_24();//制度
    String templates_name_1();//"表格型个人简历";
    String templates_name_2();//"贷款计算器";
    String templates_name_3();//"工资表";
    String templates_name_4();//"会议记录";
    String templates_name_5();//"利润表";
    String templates_name_6();//"路线图";
    String templates_name_7();//"席位卡";
    String templates_name_8();//"现金流量表";
    String templates_name_9();//"英文简历";
    String templates_name_10();//"资产负债表";
    String templates_name_11();//"创意";
    String templates_name_12();//"和谐";
    String templates_name_13();//"极限";
    String templates_name_14();//"简洁";
    String templates_name_15();//"龙";
    String templates_name_16();//"京剧脸谱";
    String templates_name_17();//"三色";
    String templates_name_18();//"水墨山水";
    String templates_name_19();//"数码世界";
    String templates_name_20();//"天地之间";
    //信电局模板
    String templates_xt_1();//"信息化动态";
    String templates_xt_2();//"信电局模板";
    String templates_xt_3();//"信电局机关党委文件";
    String templates_xt_4();//"物联网工作动态文头";
    String templates_xt_5();//"信电局通知";
    //旅游局模板
    String templates_lt_1();//"无锡市旅游局文件";
    String templates_lt_2();//"旅游局委员会文件";
    String templates_lt_3();//"旅游局旅游发展领导小组办公室文件";
    String public_space();//"公共工作空间";
    String addUser();//"添加用户";
    String shareSpace();//"共享空间";
    String publicworkspace();//"工作空间";
    String workflowfilespace();//"流程文档";
    String departementFolder();//"部门文件夹";
    String latelyShare();//"最新共享";
    String myspace();//"我的空间";
    String jobspace();//"工作空间";
    String workdoc();//"文档中心";
    String power();//"权限";
    String browse();//"浏览";
    String error();//" 已经设置共享，取消共享后才能重新设置。";
    String error_1();//"没有注册用户所在的组，请通过管理员修改组信息，再重新登录。";
    String error_2();//"注册用户所在的组信息不对，请通过管理员修改组信息，再重新登录。";
    String error_3();//"您的共享空间有新的共享文件，请至<共享空间>查看详情。";
    String error_4();//"不允许移动到相同的目录。";
    String error_5();//"没有取消共享的信息。";
    String error_6();//"不允许合并文件夹，请选择文件。";
    String error_7();//"请选中至少两个文件。";
    String error_8();//"部门序号不能重复。";
    String depfolder();//"部门文档";
    String file1();//"已被删除，请重新选择。";
    String filepath();//"当前路径: "; 
    String merger();//"合并";
    String group_code();//"部门序号";
    String user_code();//"用户序号";
    String pgroup();//"上级部门";
    String isrepeat2();//"部门名不能重复。";
    String webaddress();//"单位网站";
    String xt();//"信电局模板";
    String lt();//"旅游局模板";
    String moveFile();//"移动文件夹，将删除共享部分的属性，确定要继续吗?";
    String openError();//"你仅仅是浏览权限，无法打开文件。";
    String openError2();//"你仅仅是浏览权限，无法上传文件。";
    String openError3();//"你仅仅是浏览权限，无法新建文件。";
    // 系统报表
    String sys_report_DATE();//"日期";
    String sys_report_ACCOUNT_COUNT();//"帐户数";
    String sys_report_SPACE_COUNT();//"空间数";
    String sys_report_DOCUMENT_COUNT();//"文档数";
    String sys_report_CONTENT_SIZE();//"文件库容量";
    
    String file_system_action_name_0();//"浏览";
    String file_system_action_name_1();//"读";
    String file_system_action_name_2();//"写";
    String file_system_action_name_3();// "另存";
    String file_system_action_name_4();//"新建";
    String file_system_action_name_5();//"下载"; 
    String file_system_action_name_6();//"打印";
    String file_system_action_name_7();//"重命名";
    String file_system_action_name_8();//"删除";
    String file_system_action_name_9();//"上传";
    String file_system_action_name_10();//"复制粘贴";
    String file_system_action_name_11();//"离线编辑"; 
    String file_system_action_name_12();//"版本";
    String file_system_action_name_13();//"锁定/解锁";
    String file_system_action_name_14();//"发送";
    String file_system_action_name_15();//"审批";
    String file_system_action_name_16();//"移动";
    
    // 送审文档做版本控制
    String APPROVAL_VERSION_COMMENT();//"送审文档";
    String APPROVAL_VERSION_STATUS_CURRENT();// "当前";
    // 送审文档短信通知
    String APPROVEL_NOTICE_MESSAGE();// "你有一个文档审批任务";
    String APPROVEL_NOTICE_EMAIL();// "你有一个文档审查任务";
    String PERSON_DOCUMENT();// "个人文档";
    String PROJECT_DOCUMENT();// "协作项目";
    String management_action_name_0();//"分配权限";
    String management_action_name_1();//"增删用户";
    String management_action_name_2();//"修改用户";
    String management_action_name_3();//"增删组";
    String management_action_name_4();//"修改组";
    String management_action_name_5();//"增删角色";
    String management_action_name_6();//"分配角色";
    String management_action_name_7();//"增删空间";
    String management_action_name_8();//"查看日志";
    String management_action_name_9();//"删除日志";
    String management_action_name_10();//"公文送审";
    String management_action_name_11();//"公文管理";
    String management_action_name_12();//"公文签批";
    String management_action_name_13();//"公司动态";
    String management_action_name_14();//"行业资讯";
    String management_action_name_15();//"公告中心";
    String management_action_name_16();//"团队建设";
    String management_action_name_17();//"工会服务";
    String management_action_name_18();//"版本控制";
    String management_action_name_19();//"公告发布员";
    String management_action_name_20();//"公告审核员";
    String management_action_name_21();//"会议登记";
    String management_action_name_22();//"会议审核";
    String space_action_name_0();//"分配权限";
    String space_action_name_1();//"增删成员";
    String space_action_name_2();//"增删角色";
    String space_action_name_3();//"发布公告";
    String space_action_name_4();//"回收站";
    String space_action_name_5();//"删除空间";
}