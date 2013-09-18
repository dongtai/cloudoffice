package apps.moreoffice.report.client.util;

import java.util.Set;

import apps.moreoffice.report.client.resource.DialogResource;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 数据规范工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-10-23
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataRuleUtil
{
    /**
     * 得到自动编号的编号格式显示字符串
     * 
     * @param format 自动编号的编号格式存盘字符串
     * @return String 自动编号的编号格式显示字符串
     */
    public static String getAutoNumFormat(String format)
    {
        JSONValue value = JSONParser.parseLenient(format);
        JSONObject jso = value.isObject();
        Set<String> set = jso.keySet();
        if (!set.isEmpty())
        {
            String secondValue;
            StringBuffer sb = new StringBuffer();
            for (String firstValue : set)
            {
                secondValue = jso.get(firstValue).isString().stringValue();
                if (firstValue.equals(DialogResource.AUTONUM_DATA[0]))
                {
                    sb.append(secondValue);
                }
                else if (firstValue.equals(DialogResource.AUTONUM_DATA[3]))
                {
                    sb.append("<");
                    try
                    {
                        int number = Integer.parseInt(secondValue);
                        StringBuffer num = new StringBuffer();
                        for (int i = 1; i < number; i++)
                        {
                            num.append(0);
                        }
                        num.append(1);
                        sb.append(num);
                    }
                    catch(Exception e)
                    {
                        sb.append(secondValue);
                    }
                    sb.append(">");
                }
                else
                {
                    sb.append("<");
                    sb.append(secondValue);
                    sb.append(">");
                }
            }
            return sb.toString();
        }
        return "";
    }
}