package apps.moreoffice.report.commons;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import apps.moreoffice.report.commons.domain.DomainTools;

/**
 * 报表常用工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-25
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportTools extends DomainTools
{
    /**
     * 按UTF-8编码
     * 
     * @param params 参数
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public static void toEncodedString(HashMap<String, Object> params)
    {   
        String key;
        Object value;
        String[] strs;
        ArrayList list;
        Map.Entry entry;
        Iterator it = params.entrySet().iterator();
        try
        {
            while (it.hasNext())
            {
                entry = (Map.Entry)it.next();
                key = (String)entry.getKey();
                value = entry.getValue();
                if (value instanceof String)
                {
                    params.put(key, URLEncoder.encode((String)value, "UTF-8"));
                }
                else if (value instanceof String[])
                {
                    strs = ((String[])value);
                    for (int i = 0; i < strs.length; i++)
                    {
                        strs[i] = URLEncoder.encode(strs[i], "UTF-8");
                    }
                    params.put(key, strs);
                }
                else if (value instanceof HashMap)
                {
                    toEncodedString((HashMap)value);
                }
                else if (value instanceof ArrayList)
                {
                    list = (ArrayList)value;
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (list.get(i) instanceof HashMap)
                        {
                            toEncodedString((HashMap<String, Object>)list.get(i));
                        }
                        else if (list.get(i) instanceof String)
                        {
                            list.set(i, URLEncoder.encode((String)list.get(i), "UTF-8"));
                        }
                    }
                }
            }
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
}