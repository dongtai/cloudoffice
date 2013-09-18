package apps.moreoffice.report.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;

/**
 * JSON工具
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
public class JSONTools
{
    /**
     * 把json字符串转换为基本数据类型：
     * 如果该json对象是数组，则该对象为list，其中值为相应的对象；
     * 如果该json对象是非数组，则该对象为hashmap对象；
     * 如果该json对象的格式错误，则返回为null值。
     * 
     * hashMap对象中的值为key，加value值。
     * 而根据json对象定义，该value值可能又是一个json对象，该对象以同样的方式进行嵌套处理。
     * 
     * @param jsonStr json字符串
     * @param recyFlag 是否嵌套解析
     * @return Object 解析后的值
     */
    public static Object convertJsonToValue(String jsonStr, boolean recyFlag)
    {
        JSON jsonObject = JSONSerializer.toJSON(jsonStr);
        return convertToValue(jsonObject, recyFlag);
    }

    /**
     * 解析自动编号格式
     * 
     * @param jsonStr 自动编号格式
     * @return Vector<Object> 解析结果，按顺序先放key，再放value
     */
    @ SuppressWarnings("unchecked")
    public static Vector<Object> getAutoNumData(String jsonStr)
    {
        JSON jsonObject = JSONSerializer.toJSON(jsonStr);
        // 过滤错误情况
        if (!(jsonObject instanceof JSON) || jsonObject instanceof JSONNull || jsonObject.isEmpty())
        {
            return null;
        }

        Vector<Object> data = new Vector<Object>();
        JSONObject jsonO = (JSONObject)jsonObject;
        Set<String> set = jsonO.keySet();
        for (String key : set)
        {
            data.add(key);
            data.add(jsonO.get(key));
        }

        return data;
    }

    /*
     * 解析json对象
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public static Object convertToValue(Object jsonObject, boolean recyFlag)
    {
        // 过滤错误情况
        if (!(jsonObject instanceof JSON) || jsonObject instanceof JSONNull)
        {
            return null;
        }
        JSON json = (JSON)jsonObject;
        if (json.isEmpty())
        {
            return null;
        }

        if (json.isArray())
        {
            List retList = new ArrayList();
            JSONArray jsonA = (JSONArray)json;
            int size = jsonA.size();
            Object subJson;
            for (int i = 0; i < size; i++)
            {
                subJson = jsonA.get(i);
                if (subJson instanceof JSON)
                {
                    retList.add(convertToValue(subJson, recyFlag));
                }
                else
                {
                    retList.add(subJson);
                }
            }
            return retList;
        }
        else
        {
            HashMap<String, Object> ret = new HashMap<String, Object>();
            JSONObject jsonO = (JSONObject)json;
            Set<String> set = jsonO.keySet();
            Object value;
            for (String key : set)
            {
                value = jsonO.get(key);
                if (recyFlag && value instanceof JSON)
                {
                    ret.put(key, convertToValue(value, recyFlag));
                }
                else
                {
                    ret.put(key, value);
                }
            }

            return ret;
        }
    }

    /**
     * 得到实体对象的json字符串
     * 
     * @param entity 实体对象
     * @return String json字符串
     */
    public static String getJsonString(DataBaseObject entity)
    {
        if (jsonConfig == null)
        {
            jsonConfig = new JsonConfig();
        }
        JSONObject jso = new JSONObject();
        jso.accumulate(ParamCons.PARAMS, entity.getJsonObj(), jsonConfig);
        return jso.toString();
    }

    /*
     * 生成json字符串
     */
    public static String convertParamsToJson(String method, Object params)
    {
        return convertParamsToJson(method, params, null);
    }

    /*
     * 生成json字符串
     */
    public static String convertParamsToJson(String method, Object params, String token)
    {
        if (jsonConfig == null)
        {
            jsonConfig = new JsonConfig();
        }
        JSONObject jso = new JSONObject();
        jso.accumulate(ParamCons.METHOD, method, jsonConfig);
        if (params != null)
        {
            jso.accumulate(ParamCons.PARAMS, params, jsonConfig);
        }
        if (token != null)
        {
            jso.accumulate(ParamCons.TOKEN, token, jsonConfig);
        }
        return jso.toString();
    }

    // 
    private static JsonConfig jsonConfig;
}