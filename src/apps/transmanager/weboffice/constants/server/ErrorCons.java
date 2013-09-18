package apps.transmanager.weboffice.constants.server;

import java.util.HashMap;
import java.util.Locale;

/**
 * 系统错误信息代码，及错误提示内容定义。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
public final class ErrorCons
{
	/**
	 * 禁止实例化类
	 */
	private ErrorCons()
	{
	}

	public final static Integer NO_ERROR = 0;                // 无错误
	
	/**
	 * 100000——199999段，系统错误信息段
	 */
	public final static Integer SYSTEM_ERROR = 100000;                 // 系统错误 
	public final static Integer SYSTEM_TOKEN_ERROR =  100001;         //  无效验证token错误
	public final static Integer SYSTEM_TOKEN_TIME_OUT_ERROR =  100002;         //  验证token过期错误
	public final static Integer SYSTEM_REQUEST_ERROR = 100003;                 // 无效请求方法
	
	/**
	 *  200000——299999段，数据格式错误信息
	 */
	public final static Integer JSON_FORMAT_ERROR = 200000;       // json 格式错误
	public final static Integer JSON_PARAM_ERROR = 200001;       // 参数错误错误
	
	/**
	 * 300000——399999段，用户错误信息
	 */
	public final static Integer USER_REGISTER_ERROR = 300000;    // 用户注册错误
	public final static Integer USER_NO_EXIST_ERROR = 300001;    // 用户不存在
    public final static Integer USER_PASSWORD_ERROR = 300002;      //用户密码错误
    public final static Integer USER_COMPANY_ERROR = 300003;     //公司ID错误
    public final static Integer USER_FORBIT_ERROR = 300004;    //用户已被禁止
    public final static Integer USER_PERMIT_ERROR = 300005;       //正常用户
    public final static Integer USER_LOGINED_ERROR = 300006;   //当前用户已经登陆
    public final static Integer USER_ENROL__ERROR = 300007;   //注册失败
    public final static Integer USER_EXITSEMAIL_ERROR = 300008;  //注册失败，邮件地址已经存在
    public final static Integer USER_EXITSEUSERNAME_ERROR = 300009;   //注册失败，用户名已经存在
    public final static Integer USER__EXITSEUSERNAMECA_ERROR = 300010;   //证书登录名已存在    
    public final static Integer USER_ILLEGAL_LICENSE_ERROR =  300011; // 非法license。    
    public final static Integer USER_LEGAL_LICENSE_ERROR =  300012;   // 正确license.    
    public final static Integer USER_LICENSE_END_ERROR =  300013;   // license到期
    public final static Integer USER_LICENSE_ILLEGAL_TIME_ERROR =  300014;    // 非法license时间    
    public final static Integer USER_ONLINE_USER_ILLEGAL_ERROR =  300015;    // 非法用户在线数    
    public final static Integer USER_ONLINE_MAX_USER_ERROR =  300016;    // 已经达到最大用户在线数。    
    public final static Integer USER_ONLINE_USER_PER_ERROR = 300017;    // 用户在线数不满。
    public final static Integer USER_FINGER_ERROR_ERROR =  300018;   //指纹验证不通过 
    public final static Integer USER_CA_LOGIN_ERROR = 300019;        // 只允许CA方式登录
    public final static Integer USER_DEVICE_FORBID_ERROR = 300020;   //该设备禁止登录
    public final static Integer USER_PASSWORD_FORCE_ERROR = 300021;   // 密码非强密码
    public final static Integer USER_COMPANY_EXIST = 300022;   // 存在同名的公司
    public final static Integer USER_INFOR_ERROR = 300023;   // 用户信息错误
    public final static Integer USER_IMAGE_SIZE_ERROR = 300024;   // 不允许上传大于100k的头像图片
    public final static Integer USER_COMPANY_END_ERROR = 300025;   // 公司账户到期
    public final static Integer USER_COMPANY_CODE_EXIST = 300026;   // 公司编码已经存在
    public final static Integer USER_COMPANY_NAME_NOT_EXIST = 300027;   // 公司名称不存在
    public final static Integer USER_ORG_NAME_NOT_EXIST = 300028;   // 部门名称不存在
    public final static Integer USER_ORG_NAME_EXIST = 300029;      //公司组织已经存在
    
    public final static Integer USER_MOBILE_NOT_MATCH = 300030;      //用户手机和账号不匹配
    public final static Integer USER_Email_NOT_MATCH = 300031;      //用户邮箱和账号不匹配
    public final static Integer USER_MOBILECODE_ERROR = 300032;      //用户手机验证码无效
    public final static Integer USER_MOBILECODE_TIMEOUT = 300033;      //用户手机验证码过期
    public final static Integer USER_VALIDATECODE_NOT_MATCH = 300034;      //用户验证码不匹配
    
    public final static Integer USER_MAIL_VALIDATECODE_NOT_ERROR = 300035;      //用户邮件验证码无效
    public final static Integer USER_MAIL_VALIDATECODE_NOT_MATCH = 300036;      //用户邮件验证码过期
    
	/**
	 * 400000——499999段，文件错误信息
	 */
    public final static Integer FILE_SYSTEM_ERROR = 400000;    // 文件系统异常
	public final static Integer FILE_EXIST_ERROR = 400001;      // 文件不存在错误	
	public final static Integer FILE_FORM_ERROR = 400002;      // 上传文件需要以Form方式提交
	public final static Integer FILE_SAME_NAME_ERROR = 400003;      // 有同名文件或文件夹存在
	public final static Integer FILE_NO_VERSION_ERROR = 400004;      // 不存在的文件版本。
	public final static Integer FILE_RANGE_SUCCESS_ERROR = 400005;      //文件片段上传成功。
	public final static Integer FILE_OCTET_STREAM_ERROR = 400006;      // 上传文件内容需要以application/octet-stream方式提交。
	public final static Integer FILE_PATH__ERROR = 400007;            // 文件路径错误。
	public final static Integer FILESIZE_ERROR = 400008;               //上传文件大于10M。
	public final static Integer FILESIZE_UPLOAD_ERROR = 400009;        //上传文件失败。
	public final static Integer FILE_SHARE_EXIST_ERROR = 400010;        //没有共享发布文件存在，请确认该文件是否已经取消共享发布了。
	public final static Integer FILE_SHARE_VALIDATE_ERROR = 400011;        //该共享发布文件已经失效。
	public final static Integer FILE_IS_BEING_OPENED = 400012;           //该文件正在被使用
	public final static Integer FILE_FORMAT_ERROR = 400013;           // 文件格式错误
	public final static Integer FILE_INVALIDATE_ERROR = 400014;           //文件中无有效数据
	
	/**
	 * 500000——599999段，权限错误信息
	 */
	public final static Integer PERMISSION_ERROR = 500000;      // 无操作权限 
	
	public final static Integer PASS_ERROR = 500005;      // 获取验证码超过5次 
	public final static Integer VALIDATE_ERROR = 500006;      // 验证码已失效 
	public final static Integer ERRORMOBILE_ERROR = 500007;      // 手机号不正确 
	/**
	 * 600000——699999段，系统管理错误信息
	 */
	public final static Integer MANAGE_EXIST_ERROR = 600000;      // 存在同名角色
	public final static Integer MANAGE_COMANY_EXIST_ERROR = 600001;      // 公司不存在
    public final static Integer MANAGE_COMANY_USER_MAX_SIZE_ERROR = 600002;   // 已经达到最大授权数
    public final static Integer MANAGE_COMANY_REGISTER_ERROR = 600003;   // 公司注册失败，请确认邮箱有效
	
		
	
	private final static HashMap<Integer, String> defaultMessage = new ErrorMessageCons();
	
	private final static HashMap<Locale, HashMap<Integer, String>> messages = new HashMap<Locale, HashMap<Integer, String>>();
		
	public static String get(Integer code)
	{
		return defaultMessage.get(code);
	}
	
	public static String get(int code, Locale locale)
	{
		HashMap<Integer, String> m = messages.get(locale);
		if (m == null)
		{
			try
			{
				String className = "com.evermore.weboffice.constants.server.ErrorMessageCons_" + locale.toString();
				Class c = Class.forName(className);
				Object o = c.newInstance();
				if (o != null)
				{
					m = (HashMap<Integer, String>)o;
					messages.put(locale, m);
				}
			}
			catch(Exception e)
			{				
			}
		}
		if (m != null)
		{
			return m.get(code);
		}
		return defaultMessage.get(code);
	}
	
}
