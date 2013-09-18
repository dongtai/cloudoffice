package apps.moreoffice.report.client.core;


/**
 * 登录信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-10-13
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class LoginInfo
{
    /**
     * 得到用户ID
     * 
     * @return 用户ID
     */
    public static long getUserID()
    {
    	return 2L;
//        return ApplicationParameters.instance.user.getId();
    }

    /**
     * 得到用户名称
     * 
     * @return String 用户名称
     */
    public static String getUserName()
    {
    	return "admin";
//        return ApplicationParameters.instance.user.getUserName();
    }
}