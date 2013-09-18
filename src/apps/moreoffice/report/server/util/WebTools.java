package apps.moreoffice.report.server.util;

/**
 * 与服务器配置等相关的工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class WebTools
{
    // 解码方式
    private static String encoding;

    /**
     * 根据服务器不同设置不同解码方式
     * 
     * @param encoding 解码方式
     */
    public static void setEncoding(String encoding)
    {
        WebTools.encoding = encoding;
    }

    /**
     * 服务器层的编码转换
     * 如果默认客户端是UTF-8编码方式，则可直接调用此方法，否则调用converStr(String str,String encode)
     * 
     * @param params 需要转换的参数
     * @return String 转换好的参数
     */
    public static String convertParams(String params)
    {
        return convertParams(params, "UTF-8");
    }

    /**
     * 服务器层的编码转换
     * 
     * @param params 需要转换的参数
     * @param encode 解码方式
     * @return String 转换好的参数
     */
    private static String convertParams(String params, String encode)
    {
        if (params == null || params.equals("null"))
        {
            return "";
        }
        try
        {
            byte[] tmpbyte = params.getBytes(encoding);
            if (encode != null)
            {
                // 指定编码方式
                params = new String(tmpbyte, encode);
            }
            else
            {
                // 用系统默认的编码
                params = new String(tmpbyte);
            }
            return params;
        }
        catch(Exception e)
        {
        }
        return params;
    }
}