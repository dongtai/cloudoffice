package apps.moreoffice.report.commons.domain;

import java.util.Date;
import java.util.HashMap;

/**
 * HashMap工具
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-9
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class HashMapTools
{
    /**
     * 得到long值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return long 值
     */
    public static long getLong(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof Number)
        {
            return ((Number)paramsMap.get(param)).longValue();
        }
        return -1;
    }

    /**
     * 得到int值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return int 值
     */
    public static int getInt(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof Number)
        {
            return ((Number)paramsMap.get(param)).intValue();
        }
        return -1;
    }

    /**
     * 得到short值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return int 值
     */
    public static short getShort(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof Number)
        {
            return ((Number)paramsMap.get(param)).shortValue();
        }
        return -1;
    }

    /**
     * 得到String值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return String 值
     */
    public static String getString(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof String)
        {
            if (((String)obj).length() < 1)
            {
                return null;
            }
            return (String)paramsMap.get(param);
        }
        return null;
    }

    /**
     * 得到Boolean值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return boolean 值
     */
    public static Boolean getBoolean(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof Boolean)
        {
            return (Boolean)paramsMap.get(param);
        }
        return null;
    }

    /**
     * 得到Date值
     * 
     * @param paramsMap 参数HashMap
     * @param param 具体参数
     * @return Date 值
     */
    public static Date getDate(HashMap<String, Object> paramsMap, String param)
    {
        Object obj = paramsMap.get(param);
        if (obj instanceof Date)
        {
            return (Date)paramsMap.get(param);
        }
        return null;
    }
}