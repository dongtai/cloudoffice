package apps.transmanager.weboffice.client.constant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * EIO版本:        WEB Office V1.0
 * <p>
 * 作者:           User604(安陈琳)
 * <p>
 * 日期:           Aug 21, 2008
 * <p>
 * 负责人:         User604(安陈琳)
 * <p>
 * 负责小组:        WEB
 * <p>
 * <p>
 */
public class WebofficeUtility
{
    
    /**
     * 
     * @param findArray 关键字
     * @param dataArray 被搜索文本
     * @return true,dataArray中包含findArray;false,不包含
     */
    public static boolean find(String findArray, String dataArray)
    {
        if (findArray == null || findArray == "" || dataArray == null || dataArray == "")
        {
            return false;
        }
        int findEnd = findArray.length() - 1;
        int dataEnd = dataArray.length() - 1;
        //查找对象的长度大于被查找对象时,肯定不符合。
        if (findEnd > dataEnd)
        {
            return false;
        }
        int i = 0;//标记findArray的字母索引
        int j = 0;//标记dataArray的字母索引
        int type = 0;
        int k = 0; //标记dataArray中，符合findArray的字母个数，从0位开始
        while (j <= dataEnd)
        {
            type = isEquals(findArray, i, dataArray, j);
            if(type == 1)
            {
                if (i == findEnd)
                {
                    return true;
                }
                i++;
                j++;
                k++;
                continue;
            }
            else if (i != 0)//不符合时，
            {
                i = 0;//find从0位开始
                j = j - k;//data减掉k值后，继续往下走。
                //比如find=ak, data=aak时，走到第二位不符合，需要往回退k位继续对比
                k = 0;//k重置
            }
            j++;
        }
        return false;
    }
    
    /**
     * 
     * @param findArray 关键字
     * @param findIndex 索引的字母
     * @param dataArray 被搜索文本
     * @param dataIndex 索引的字母
     * @return 0-不等; 1-相等
     */
    private static int isEquals(String findArray, int findIndex, String dataArray, int dataIndex)
    {
        char findChar = findArray.charAt(findIndex);
        char dataChar = dataArray.charAt(dataIndex);
        findChar = Character.toLowerCase(findChar);
        dataChar = Character.toLowerCase(dataChar);
        
        if (findChar == dataChar)
        {
            return 1;
        }
        
        return 0;
    }
    
    /**
     * 
     * @param obj 被搜索对象
     * @param rows 行号
     * @param keyword 搜索关键字
     * @param param 0-名字；1-邮件名, 3-分类名
     * @return
     */
    public static int[] searchObjects(Object[][] obj, String keyword, int param)
    {
        int length = obj.length;
        int[] showRows = getIntArray(length);
        Vector<Integer> vec = new Vector<Integer>();
        for (int i = 0; i < length; i++)
        {
            if (WebofficeUtility.find(keyword, (String)obj[showRows[i]][param]))
            {
                vec.add(new Integer(showRows[i]));
            }
        }
        return getIntFromVec(vec);
    }
    
    /**
     * 将int[]转化为Vector
     * @param indexs
     * @return
     */
    public static Vector<Integer> getVecFromInt(int[] indexs)
    {
        Vector<Integer> vec = new Vector<Integer>();
        for (int i = 0, length = indexs.length; i < length; i++)
        {
            vec.addElement(new Integer(indexs[i]));
        }
        return vec;
    }
    
    /**
     * 生成一个长度为length的int[]
     * @param length
     * @return
     */
    public static int[] getIntArray(int length)
    {
        int[] indexs = new int[length];
        for (int i = 0; i < length; i++)
        {
            indexs[i] = i;
        }
        return indexs;
    }
    
    /**
     * 将Vector<Integer>转化为int[]
     * @param vec
     * @return
     */
    public static int[] getIntFromVec(Vector<Integer> vec)
    {
        int size = vec.size();
        int[] indexs = new int[size];
        for (int i = 0; i < size; i++)
        {
            indexs[i] = vec.elementAt(i).intValue();
        }
        return indexs;
    }
    
    /**
     * 标签名中不能存在‘/\:*'?\"<>|’字符
     * @param text
     * @return
     */
    public static boolean isTextlegal(String text)
    {
		char[] data = text.toCharArray();
        for (int i = 0; i < data.length; i++)
        {
            if (MainConstant.ERROR_NAME_TEXT.indexOf(data[i]) > -1)
            {
                return false;
            }
        }
//        String[] illegals = getIllegalChar();
//        for (int i = 0; i < illegals.length; i++)
//        {
//            if (text.contains(illegals[i]))
//            {
//                return false;
//            }
//        }
        return true;
    }
    
    /**
     * 标签名中首位不能是.
     * @param text
     * @return
     */
    public static boolean isDotlegal(String text)
    {
    	if(text != null)
    	{
	    	if(text.indexOf('.') ==0)
	    	{
	    		return true;
	    	}
    	}
    	
		return false;
    }
//    
//    private static String[] getIllegalChar()
//    {
//       return  new String[]{"/","\\",":","*","'","?","\"","<",">","|"};
//    }
    
    /**
     * 从文本中截取标签
     * @param text
     * @return
     */
    public static ArrayList<String> getTagsFromText(String text)
    {
        ArrayList<String> tags = new ArrayList<String>();
        int length = text.length();
        int k = 0;

        for (int i = 0; i < length; i++)
        {
            if (text.charAt(i) == ',' ||text.charAt(i) == '，' ||text.charAt(i) == '\n')
            {
                if(k != i)
                {
                	String temp = text.substring(k, i).trim();
                	if(!temp.equals(""))
                	{
                		tags.add(temp);
                	}
                }
                k = i + 1;
            }
        }
        //没有逗号时，k一直为0，for循环就走不到了。
        if (k == 0)
        {
            tags.add(text);
        }
        else if(k < length)
        {
            tags.add(text.substring(k, length));
        }
        return tags;
    }
    
    /**
     * 比较strs中的string是否相等
     * @param strs 要求已排序
     * @return strs中如果没有重复的，返回false
     */
    public static boolean compareSame(ArrayList<String> strs)
    {
        if (strs.size() <= 1)
        {
            return false;
        }
        int k = 1;
        for (int i = 0; i < strs.size(); i++)
        {
            for(int j = k;j<strs.size();j++)
            {
                System.out.println(i+"---"+j);
                if (strs.get(i).equalsIgnoreCase(strs.get(j)))
                {
                    return true;
                }
            }
            k++;
        }
        return false;
    }
    
    /**
     * 是否所有页的数据都已从数据库访问并填充
     * @param gridData
     * @param loadCount
     * @return
     */
    public static boolean isAllVisit(String[][] gridData,int loadCount)
    {
        for(int i = 0; i < gridData.length; i += loadCount)
        {
            if(gridData[i][MainConstant.FILEDATA_FILETYPE] == null)
            {
                return false;
            }
        }
        return true;
    }
    
    /*
     * 加密
     */
    public static String passwordEncrypt(String passWord)
    {
        if (passWord == null)
        {
            return null;
        }
        char[] value = new char[passWord.length()];
        int len = value.length;
        for (int i = len - 1, j = 0; i >= 0; i--, j++)
        {
            value[i] = passwordEncrypt(passWord.charAt(j));
        }
        return new String(value);
    }

    /*
     * 解密
     * 
     */
    private static char passwordEncrypt(char ch)
    {
        char c = ch;
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
        {
            c += 4;
            if (c > 'Z' && c <= 'Z' + 4 || c > 'z')
            {
                c -= 26;
            }
        }
        else if (c >= '0' && c <= '9')
        {
            c += 3;
            if (c > '9')
            {
                c -= 10;
            }
        }
        return c;
    }

    /*
     * 解密
     */
    public static String furbishPassword(String pwsrc)
    {
        if (pwsrc == null)
        {
            return null;
        }
        char[] value = new char[pwsrc.length()];
        int len = value.length;
        for (int i = 0, j = len - 1; i < len; i++, j--)
        {
            value[i] = furbishPassword(pwsrc.charAt(j));
        }
        return new String(value);
    }

    /*
     * 解密
     */
    private static char furbishPassword(char ch)
    {
        char c = ch;
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
        {
            if ((c >= 'a' && c <= 'd') || (c >= 'A' && c <= 'D'))
            {
                c += 26;
            }
            c -= 4;
        }
        else if (c >= '0' && c <= '9')
        {
            c -= 3;
            if (c < '0')
            {
                c += 10;
            }
        }
        return c;
    }
    public static String getFormateDate(Date date)
    {
        if (date != null)
        {
            return getFormateDate(date, "-");
        }
        return null;
    }
    public static String getFormateDate(Date date, String dot)
    {
        if (date != null)
        {
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            int hour = date.getHours();
            int minute = date.getMinutes();
            int sec = date.getSeconds();
            return year + dot + (month >= 10 ? month : ("0" + month)) + dot
                + (day >= 10 ? day : ("0" + day)) + " "
                + (hour >= 10 ? hour : ("0" + hour)) + ":"
                + (minute >= 10 ? minute : ("0" + minute))+ ":"
                + (sec >= 10 ? sec : ("0" + sec));
        }
        return null;
    }
    public static String getFormateDate2(Date date, String dot)
    {
        if (date != null)
        {
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            return year + dot + (month >= 10 ? month : ("0" + month)) + dot
                + (day >= 10 ? day : ("0" + day));
        }
        return null;
    }
    
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

    public static String getTypeImage(String value,boolean isEncrypt,boolean isSign, boolean isApproval)
	{
		boolean shareflag = false;
		String name = value.toString();
        int index = name.indexOf('*');
        String temp0 = index >= 0 ? name.substring(0, index) : null;
        if (temp0 != null && temp0.equals("ISSHARE"))
        {
        	shareflag = true;
        }
        if (value != null)
        {
        	String text=value.toString();
        	boolean isVersion  = false;
        	boolean isCheck  = false;
        	if (text != null)
        	{
        		if ("link".equals(text) || "ISSHARE*link".equals(text))
        		{
        			
        		}
        		else
        		{
            		String aa = text.substring(text.length()-2,text.length()-1);
            		if ("&".equals(aa))
            		{
            			isVersion = true;
            		}
            		String bb = text.substring(text.length()-1);
            		if ("&".equals(bb))
            		{
            			isCheck = true;
            		}
            		text = text.substring(0,text.length()-2);
        		}
        	}
			if(value.toString().equals("folder") || value.toString().equals("ISSHARE*folder"))
			{
				text ="dir";
			}
			else
			{
				text = text.toLowerCase();
			}
			return getFileIconPath(text, shareflag, isVersion, isCheck,
					isEncrypt, isSign, isApproval);
        }
        if(shareflag)
        {
        	return "<img src='images/sharefile/other.gif'/>";
        }
        return "<img src='images/fileicon/other.gif'/>";
	}
	
	public static String getTypeImageSrc(String value) {
		boolean shareflag = false;
		String name = value.toString();
		int index = name.indexOf('*');
		String temp0 = index >= 0 ? name.substring(0, index) : null;
		if (temp0 != null && temp0.equals("ISSHARE")) {
			shareflag = true;
		}
		//这个方法里有不少可能存在的bug。FileDataSource，里将filetype复杂化了。
		if (value != null) {
			String text = value.toString();
			if (text != null) {
				if ("link".equals(text) || "ISSHARE*link".equals(text)) {

				} else {
					String aa = text.substring(text.length() - 2,
							text.length() - 1);
					if ("&".equals(aa)) {
					}
					String bb = text.substring(text.length() - 1);
					if ("&".equals(bb)) {
					}
					text = text.substring(0, text.length() - 2);
				}
			}
			if (value.toString().equals("folder")
					|| value.toString().equals("ISSHARE*folder")) {
				text = "dir";
			} else {
				text = text.toLowerCase();
			}
			return getFileIconPathSrc(text, shareflag, false, false, "48", ".png");
		}
		if (shareflag) {
			return "images/sharefile/other.gif";
		}
		return "images/fileicon/other.gif";
	}
	
	
	public static String getFileIconPathSrc(String text,boolean isshare,boolean isVersion,boolean isCheck,String size,String suddix)
	{
		String  ppth = "images/fileicon/";
		String  shareppth = "images/sharefile/";
		
		if(size != null)
		{
			ppth = "images/fileicon"+size+"/";
			shareppth = "images/sharefile"+size+"/";
		}
		if (isshare)
		{
			if(text.contains("*"))
			{
				text = text.substring(8);
			}
		}
		text = text.toLowerCase();//基于都是小写模式，转换为小写
        if (text.equals("bmp") || text.equals("db") || text.equals("dbf")
			|| text.equals("dir") || text.equals("doc")
			|| text.equals("dot") || text.equals("eio")
			|| text.equals("eit") || text.equals("eiw")
			|| text.equals("emf") || text.equals("gif")
			|| text.equals("htm") || text.equals("html")
			|| text.equals("jpg") || text.equals("other")
			|| text.equals("pdf") || text.equals("png")
			|| text.equals("pot") || text.equals("ppt")
			|| text.equals("rar") || text.equals("rtf")
			|| text.equals("tiff") || text.equals("txt")
			|| text.equals("uof") || text.equals("wmf")
			|| text.equals("xls") || text.equals("xlt")
			|| text.equals("zip") || "link".equals(text))
        {
        	String path="";
        	if(isshare)
        	{
        		path = shareppth + text + suddix;
        	}
        	else
        	{
        		
        	 path = ppth + text + suddix;
        	}
        	if (isVersion)
        	{
        		path += "images/green/version.png";
        	}
        	if (isCheck)
        	{
        		path += "images/green/unlock.png";
        	}
        	return path;
        }
        else if (text.equals("cvs") || text.equals("pps")
            || text.equals("docx") || text.equals("pptx") || text.equals("xlsx"))
        {
        	String path="";
        	if(isshare)
        	{
        		path = shareppth + text + suddix;
        	}
        	else
        	{
             path = ppth + text + ".png";
        	}
        	if (isVersion)
        	{
        		path += "images/green/version.png";
        	}
        	if (isCheck)
        	{
        		path += "images/green/unlock.png";
        	}
            return path;
        }
        if(isshare)
        {
        	String path = shareppth+"other"+suddix;
        	if (isVersion)
        	{
        		path += "images/green/version.png";
        	}
        	if (isCheck)
        	{
        		path += "images/green/unlock.png";
        	}
        	return path;
        }
        String path = ppth+"other"+suddix;
        if (isVersion)
    	{
    		path += "images/green/version.png";
    	}
        if (isCheck)
    	{
    		path += "images/green/unlock.png";
    	}
        return path;
	}
	
	private static String getFileIconPath(String text,boolean isshare,boolean isVersion,boolean isCheck,
	    boolean isEncrypt,boolean isSign, boolean isApproval)
	{
		if (isshare)
		{
			if(text.contains("*"))
			{
			text = text.substring(8);
			}
		}
        if (text.equals("bmp") || text.equals("db") || text.equals("dbf")
			|| text.equals("dir") || text.equals("doc")
			|| text.equals("dot") || text.equals("eio")
			|| text.equals("eit") || text.equals("eiw")
			|| text.equals("emf") || text.equals("gif")
			|| text.equals("htm") || text.equals("html")
			|| text.equals("jpg") || text.equals("other")
			|| text.equals("pdf") || text.equals("png")
			|| text.equals("pot") || text.equals("ppt")
			|| text.equals("rar") || text.equals("rtf")
			|| text.equals("tiff") || text.equals("txt")
			|| text.equals("uof") || text.equals("wmf")
			|| text.equals("xls") || text.equals("xlt")
			|| text.equals("zip") || "link".equals(text))
        {
        	String path="";
        	if(isshare)
        	{
        		path = "<img src='images/sharefile/" + text + ".gif'/>";
        	}
        	else
        	{
        	 path = "<img src='images/fileicon/" + text + ".gif'/>";
        	}
        	if (isVersion)
        	{
        		path += "<img src='images/green/version.png'/>";
        	}
        	if (isCheck)
        	{
        		path += "<img src='images/green/unlock.png'/>";
        	}
        	if (isEncrypt)
        	{
        		path += "<img src='images/green/encrypt.png'/>";
        	}
        	if (isSign)
        	{
        		path += "<img src='images/green/sign.png'/>";
        	}
        	if (isApproval)
        	{
        	    path += "<img src='images/menu/approval_File.png'/>";
        	}
        	return path;
        }
        else if (text.equals("cvs") || text.equals("pps")
            || text.equals("docx") || text.equals("pptx") || text.equals("xlsx"))
        {
        	String path="";
        	if(isshare)
        	{
        		path = "<img src='images/sharefile/" + text + ".gif'/>";
        	}
        	else
        	{
             path = "<img src='images/fileicon/" + text + ".png'/>";
        	}
        	if (isVersion)
        	{
        		path += "<img src='images/green/version.png'/>";
        	}
        	if (isCheck)
        	{
        		path += "<img src='images/green/unlock.png'/>";
        	}
        	if (isEncrypt)
        	{
        		path += "<img src='images/green/encrypt.png'/>";
        	}
        	if (isSign)
        	{
        		path += "<img src='images/green/sign.png'/>";
        	}
            if (isApproval)
            {
                path += "<img src='images/menu/approval_File.png'/>";
            }
            return path;
        }
        if(isshare)
        {
        	String path = "<img src='images/sharefile/other.gif'/>";
        	if (isVersion)
        	{
        		path += "<img src='images/green/version.png'/>";
        	}
        	if (isCheck)
        	{
        		path += "<img src='images/green/unlock.png'/>";
        	}
            if (isApproval)
            {
                path += "<img src='images/menu/approval_File.png'/>";
            }
        	return path;
        }
        String path = "<img src='images/fileicon/other.gif'/>";
        if (isVersion)
    	{
    		path += "<img src='images/green/version.png'/>";
    	}
        if (isCheck)
    	{
    		path += "<img src='images/green/unlock.png'/>";
    	}
        if (isEncrypt)
    	{
    		path += "<img src='images/green/encrypt.png'/>";
    	}
    	if (isSign)
    	{
    		path += "<img src='images/green/sign.png'/>";
    	}
        if (isApproval)
        {
            path += "<img src='images/menu/approval_File.png'/>";
        }
        return path;
	}
}
