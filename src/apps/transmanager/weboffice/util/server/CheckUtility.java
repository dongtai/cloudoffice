package apps.transmanager.weboffice.util.server;

/**
 * 工具栏，提供一些通用的方法
 * @author sch
 *
 */

public class CheckUtility
{
    /**
     * 检查mail 字符串的有效性,工具方法 user308
     */
    public static boolean checkMailStringFormat(String mail)
    {
        char[] chs = mail.toCharArray();
        for(int i=0;i<chs.length;i++)
        {
            if ((chs[i] < 'A' || chs[i] > 'Z') && (chs[i] < 'a' || chs[i] > 'z')
                && (chs[i] < '0' || chs[i] > '9') && chs[i] != '_' && chs[i] != '-'
                && chs[i] != '@' && chs[i] != '.')
            {
                return false;
            }
        }
        if (mail.trim().length() > 0 && mail.indexOf("@") >= 1
            && mail.indexOf("@") < mail.length() - 1)//必须有@符号
        {
            String[] str = mail.split("@");
            if (str.length == 2 && str[1].indexOf(".") >= 0 && !str[0].contains("^"))//@符号只能有一个,且后半个字符串必须包含.号
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkUserNameStringFormat(String userName)
    {
        if (userName == null || userName.trim().equals(""))
        {
            return false;
        }       
        if (userName.contains("^") || userName.contains("?") || userName.contains("/")
            || userName.contains("'\'"))
        {
            return false;
        }
        return true;
    }
    
    /**
     * 判断给定字符串是否为空。
     * 请注意：这里把只有空格的字符串也当为空来处理。
     * @param s
     * @return
     */
    public static boolean isNull(String s)
	{
		if (s == null)
		{
			return true;
		}
		s = s.trim();
		if (s.length() == 0)
		{
			return true;
		}
		return false;
	}
}
