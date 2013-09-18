package apps.transmanager.weboffice.util.server;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.service.config.WebConfig;


public class WebTools 
{

	private static String encoding = "ISO-8859-1";
	
	public static String getEncoding()
	{
		return encoding;
	}

	public static void setEncoding(String encoding)
	{
		WebTools.encoding = encoding;
	}

	public static String converStr(String str)
    {
    	return converStr(str,"UTF-8");
    }	
	public static String getRealIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader(WebConfig.nginxforwardtag);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader(WebConfig.nginxiptag);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {   
            ip = request.getHeader("HTTP_CLIENT_IP");   
        }   
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
        {   
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");   
        }   
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
    		ip = request.getRemoteAddr();
        }
        
        return ip;
    }
    /**
     * 
     * @param str
     * @param encode
     * @return
     */
    public static String converStr(String str,String encode)
    {
        if (str==null || str.equals("null"))
        {
            return "";
        }
        try
        {        	
            byte[] tmpbyte=str.getBytes(encoding);
            if(encode != null)
            {
                //如果指定编码方式
                    str=new String(tmpbyte,encode);
            }
            else
            {
                //用系统默认的编码
                str = new String(tmpbyte);
            }
            return str;
        }
        catch (Exception e)
        {
        }
        return str;
    }    
    
    /**
     * 解密字符串
     * @param s 需要解码的字符内容
     * @param enc 需要解码的编码方式。
     * @return
     * @throws UnsupportedEncodingException
     */
	public static String decode(String s, String enc) throws UnsupportedEncodingException
	{
		boolean needToChange = false;
		int numChars = s.length();
		StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2	: numChars);
		int i = 0;

		if (enc.length() == 0)
		{
			throw new UnsupportedEncodingException(	"URLDecoder: empty string enc parameter");
		}

		char c;
		byte[] bytes = null;
		while (i < numChars)
		{
			c = s.charAt(i);
			switch (c)
			{
				case '%':
					try
					{
						if (bytes == null)
						{
							bytes = new byte[(numChars - i) / 3];
						}
						int pos = 0;
						while (((i + 2) < numChars) && (c == '%'))
						{
							bytes[pos++] = (byte) Integer.parseInt(s.substring(i + 1, i + 3), 16);
							i += 3;
							if (i < numChars)
							{
								c = s.charAt(i);
							}
						}

						if ((i < numChars) && (c == '%'))
						{
							throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
						}
						sb.append(new String(bytes, 0, pos, enc));
					}
					catch (NumberFormatException e)
					{
						throw new IllegalArgumentException(	"URLDecoder: Illegal hex characters in escape (%) pattern - "
										+ e.getMessage());
					}
					needToChange = true;
					break;
				default:
					sb.append(c);
					i++;
					break;
			}
		}

		return (needToChange ? sb.toString() : s);
	}
    
    /**
     * @param object排序对象集合
     * @param property, 排序的对象属性名，该对象中需要有该属性名称的get方法，即是如果属性名为abc，则对象中应该有一个getAbc()的方法。
     * @param subProperty,  排序的对象中子属性名，即是该属性是上property对象的属性，即是如果对象中有abc子对象，abc子对象中有属性efg，
     * 则该abc对象中应该有getEfg()方法。
     * @param dir 升序或降序。true为 升序（ASC）， false为降序（DSC）。 
     */
    public static <T> List<T> sortUsers(List<T> users, String property, String subProperty, boolean asc)
    {
    	Collections.sort(users, new SubObjectComparator(property, subProperty, asc));
    	return users;
    }
    
    
    /**
     * @param objects 排序对象集合
     * @param property, 排序的对象属性名，该对象中需要有该属性名称的get方法，即是如果属性名为abc，则对象中应该有一个getAbc()的方法。
     * @param dir 升序或降序。true为 升序（ASC）， false为降序（DSC）。 
     */
    public static <T>  List<T> sortUsers(List<T> users, String property, boolean asc)
    {
    	Collections.sort(users, new ObjectComparator(property, asc));
    	return users;
    }
    
    static class ObjectComparator implements Comparator
    {
        private String sort;
        private int dir;//1 ASC -1 DSC

        public ObjectComparator(String sort, boolean asc)
        {
            this.sort = sort.substring(0, 1).toUpperCase() + sort.substring(1);
            this.dir = asc ? 1 : -1;
        }

        public int compare(Object o1, Object o2)
        {
        	if (o1 == null | o2 == null)
        	{
        		return 0;
        	}
            Object a1 = "";
            Object a2 = "";
            try
            {
	            Class c = o1.getClass();
	            Method method = c.getMethod("get" + sort);
	            if (method == null)
	            {
	            	return 0;
	            }
            
	            a1 = method.invoke(o1);
	            a2 = method.invoke(o2);
	            if ((a1 instanceof Integer) && (a2 instanceof Integer))
				{
					if (((Integer)a1).intValue()>((Integer)a2).intValue())
					{
						return dir;
					}
					else 
					{
						return 0;
					}
				}
	            return dir * ((RuleBasedCollator)Collator.getInstance(Locale.CHINA)).compare(a1, a2);
            }
            catch(Exception e)
            {
            	LogsUtility.error(e);
            	return 0;
            }
        }
    }
    
    static class SubObjectComparator implements Comparator
    {
        private String sort;
        private String subSort;
        private int dir;//1 ASC -1 DSC

        public SubObjectComparator(String sort, String subSort, boolean asc)
        {
            this.sort = sort.substring(0, 1).toUpperCase() + sort.substring(1);
            this.subSort = subSort.substring(0, 1).toUpperCase() + subSort.substring(1);
            this.dir = asc ? 1 : -1;
        }

        public int compare(Object o1, Object o2)
        {
        	if (o1 == null | o2 == null)
        	{
        		return 0;
        	}
            Object a1 = "";
            Object a2 = "";
            try
            {
	            Class c = o1.getClass();
	            Method method = c.getMethod("get" + sort);
	            if (method == null)
	            {
	            	return 0;
	            }
	            Class c2 = method.getReturnType();
	            Method method2 = c2.getMethod("get" + subSort);
	            if (method2 == null)
	            {
	            	return 0;
	            }
            
	            a1 = method2.invoke(method.invoke(o1));
	            a2 = method2.invoke(method.invoke(o2));            
	            return dir * ((RuleBasedCollator)Collator.getInstance(Locale.CHINA)).compare(a1, a2);
            }
            catch(Exception e)
            {
            	LogsUtility.error(e);
            	return 0;
            }
        }
    }
    
    public static String getCode()
    {
    	// 验证码图片的宽度。
    	char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
    	   'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
    	   'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    	// 创建一个随机数生成器类
    	Random random = new Random();
    	// randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
    	StringBuffer randomCode = new StringBuffer();
    	for (int i = 0; i < 4; i++) 
    	{
    	   // 得到随机产生的验证码数字。
    	   String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
    	   randomCode.append(strRand);
    	}
    	// 将四位数字的验证码保存到Session中。
    	return randomCode.toString();
    }
}
