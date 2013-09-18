package apps.transmanager.weboffice.constants.server;

import java.util.HashMap;

/**
 * 系统错误提示内容定义，因为内容。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
final class ErrorMessageCons_en extends HashMap<Integer, String>
{
	/**
	 * 禁止实例化类
	 */
	ErrorMessageCons_en()
	{
		super(30);
		initData();
	}
	
	private void initData()
	{
		put(ErrorCons.NO_ERROR, "无错误");
		/**
		 * 100000——199999段，系统错误信息段
		 */
		put(ErrorCons.SYSTEM_ERROR, "系统错误"); 
		put(ErrorCons.SYSTEM_TOKEN_ERROR, "无效验证token错误");		
		put(ErrorCons.SYSTEM_TOKEN_TIME_OUT_ERROR, "验证token过期错误");
		put(ErrorCons.SYSTEM_REQUEST_ERROR, "无效请求方法");
		
		/**
		 *  200000——299999段，数据格式错误信息
		 */
		put(ErrorCons.JSON_FORMAT_ERROR, "json 格式错误");
		put(ErrorCons.JSON_PARAM_ERROR, "参数错误错误");
		
		/**
		 * 300000——399999段，用户错误信息
		 */
		put(ErrorCons.USER_REGISTER_ERROR, "用户注册错误");
		put(ErrorCons.USER_NO_EXIST_ERROR, "用户不存在");
		put(ErrorCons.USER_PASSWORD_ERROR, "用户密码错误");
		put(ErrorCons.USER_COMPANY_ERROR, "公司ID错误");
		put(ErrorCons.USER_FORBIT_ERROR, "用户已被禁止");
		put(ErrorCons.USER_PERMIT_ERROR, "正常用户");
		put(ErrorCons.USER_LOGINED_ERROR, "当前用户已经登陆");
		put(ErrorCons.USER_ENROL__ERROR, "注册失败");
		put(ErrorCons.USER_EXITSEMAIL_ERROR, "注册失败，邮件地址已经存在");
		put(ErrorCons.USER_EXITSEUSERNAME_ERROR, "注册失败，用户名已经存在");
		put(ErrorCons.USER__EXITSEUSERNAMECA_ERROR, "证书登录名已存在 ");
		put(ErrorCons.USER_ILLEGAL_LICENSE_ERROR, "非法license");    
		put(ErrorCons.USER_LEGAL_LICENSE_ERROR, "正确license");   
		put(ErrorCons.USER_LICENSE_END_ERROR, "license到期");
		put(ErrorCons.USER_LICENSE_ILLEGAL_TIME_ERROR, "非法license时间 ");    
		put(ErrorCons.USER_ONLINE_USER_ILLEGAL_ERROR, "非法用户在线数");    
		put(ErrorCons.USER_ONLINE_MAX_USER_ERROR, "已经达到最大用户在线数");    
		put(ErrorCons.USER_ONLINE_USER_PER_ERROR, "用户在线数不满");
		put(ErrorCons.USER_FINGER_ERROR_ERROR, "指纹验证不通过"); 
		put(ErrorCons.USER_CA_LOGIN_ERROR, "只允许CA方式登录");
		put(ErrorCons.USER_DEVICE_FORBID_ERROR, "该设备禁止登录");
		put(ErrorCons.USER_PASSWORD_FORCE_ERROR, "密码非强密码");
		put(ErrorCons.USER_COMPANY_EXIST, "存在同名的公司");
		put(ErrorCons.USER_INFOR_ERROR, "用户信息错误");
		put(ErrorCons.USER_IMAGE_SIZE_ERROR, "不允许上传大于100k的头像图片");
		put(ErrorCons.USER_COMPANY_END_ERROR, "公司账户到期");
		put(ErrorCons.USER_COMPANY_CODE_EXIST, "公司编码已经存在");
		
		put(ErrorCons.USER_MOBILE_NOT_MATCH, "用户手机和账号不匹配");
		put(ErrorCons.USER_Email_NOT_MATCH, "用户邮箱和账号不匹配");
		put(ErrorCons.USER_MOBILECODE_ERROR, "用户手机验证码无效");
		put(ErrorCons.USER_MOBILECODE_TIMEOUT, "用户手机验证码过期");
		put(ErrorCons.USER_VALIDATECODE_NOT_MATCH, "用户验证码不匹配");
		
		put(ErrorCons.USER_MAIL_VALIDATECODE_NOT_ERROR, "邮件验证码无效");
		put(ErrorCons.USER_MAIL_VALIDATECODE_NOT_MATCH, "邮件验证码过期");

		
		/**
		 * 400000——499999段，文件错误信息
		 */
		put(ErrorCons.FILE_SYSTEM_ERROR, "文件系统异常");
		put(ErrorCons.FILE_EXIST_ERROR, "文件不存在错误");	
		put(ErrorCons.FILE_FORM_ERROR, "上传文件需要以Form方式提交");
		put(ErrorCons.FILE_SAME_NAME_ERROR, "有同名文件或文件夹存在");
		put(ErrorCons.FILE_NO_VERSION_ERROR, "不存在的文件版本");
		put(ErrorCons.FILE_RANGE_SUCCESS_ERROR, "文件片段上传成功");
		put(ErrorCons.FILE_OCTET_STREAM_ERROR, "上传文件内容需要以application/octet-stream方式提交");
		put(ErrorCons.FILE_PATH__ERROR, "文件路径错误");
		put(ErrorCons.FILESIZE_ERROR, "上传文件大于100M");
		put(ErrorCons.FILESIZE_UPLOAD_ERROR, "上传文件失败");
		put(ErrorCons.FILE_SHARE_EXIST_ERROR, "没有共享发布文件存在，请确认该文件是否已经取消共享发布了。");
		put(ErrorCons.FILE_SHARE_VALIDATE_ERROR, "该共享发布文件已经失效。");
		put(ErrorCons.FILE_IS_BEING_OPENED, "该文件正在被使用");
		put(ErrorCons.FILE_FORMAT_ERROR, "文件格式错误");
		put(ErrorCons.FILE_INVALIDATE_ERROR, "文件中无有效数据");
		
		/**
		 * 500000——599999段，权限错误信息
		 */
		put(ErrorCons.PERMISSION_ERROR, "无操作权限");
		
		/**
		 * 600000——699999段，系统管理错误信息
		 */
		put(ErrorCons.MANAGE_EXIST_ERROR, "存在同名角色");      // 存在同名角色
		put(ErrorCons.MANAGE_COMANY_EXIST_ERROR, "公司不存在");
		put(ErrorCons.MANAGE_COMANY_USER_MAX_SIZE_ERROR, "已经达到最大授权数");
		put(ErrorCons.MANAGE_COMANY_REGISTER_ERROR, "公司注册失败，请确认邮箱有效");
		
	}
	
	
}
