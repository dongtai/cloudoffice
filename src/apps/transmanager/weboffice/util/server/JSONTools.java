package apps.transmanager.weboffice.util.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;


/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public final class JSONTools
{

	private static JsonConfig jsonConfig = new JsonConfig();
	
	/**
	 * 返回通过json内容解析出来的对象。如果该json对象是数组，则该对象为list，其中值为相应的
	 * 对象；如果该json对象中是非数组，则该对象为hashmap对象；
	 * 如果json对象的格式错误，则返回为null值。
	 * hashMap对象中的值为key，加value值，而根据json对象定义，该value值
	 * 可能又是一个json对象，该对象以同样的方式进行嵌套处理。
	 * @param jsonParams
	 * @return
	 */
	public static Object convertParams(String jsonParams)
	{
		return convertParams(jsonParams, true);
	}
	
	/**
	 * 返回通过json内容解析出来的对象。如果该json对象是数组，则该对象为list，其中值为相应的
	 * 对象；如果该json对象中是非数组，则该对象为hashmap对象；
	 * 如果json对象的格式错误，则返回为null值。
	 * hashMap对象中的值为key，加value值，而根据json对象定义，该value值
	 * 可能又是一个json对象，该对象以同样的方式进行嵌套处理。
	 * @param jsonParams
	 * @return
	 */
	public static Object convertParams(String jsonParams, boolean recyFlag)
	{
        try
        {
        	JSON json = JSONSerializer.toJSON(jsonParams, jsonConfig);
        	if (json.isEmpty())
        	{
        		return null;
        	}
        	return convertJSONToList(json, recyFlag);
        }
        catch(JSONException j)
        {
        	return null;
        }
        catch(Exception e)
        {
        	return null;
        }
	}
	
	/**
	 * 把json对象转换为hashMap值。
	 * 如果该json对象是数组，则该返回为list对象，对象中有多个相应值
	 * 如果该json对象中是非数组，则该list中只有一个hashMap对象；
	 * 如果json对象的格式错误，则返回为null值。
	 * 如果json对象的格式错误，则返回为null值。
	 * hashMap对象中的值为key，加value值，而根据json对象定义，该value值
	 * 可能又是一个json对象，该对象以同样的方式进行嵌套处理。
	 * 如果对象中还有子json对象，则递归转换。
	 * @param jsonObject json对象
	 * @return
	 */
	public static Object convertJSONToList(Object jsonObject)
	{
		return convertJSONToList(jsonObject, true);
	}
	/**
	 * 把对象转换为json对象字符串
	 * @param errorCode 错误代码
	 * @param o 结果对象
	 * @return 转换后的json对象，内容为：
	 * <p>
	 * 	{
	 * 		result:"xxx",
	 * 		errorMessage:"xxxxxxxx",
	 * 		errorCode:xxx
	 * 	}
	 * </p>
	 */
	public static String convertToJson(Integer errorCode, Object result,JsonConfig jsonConfig)
	{
		JSONObject jso = new JSONObject();
		jso.accumulate(ServletConst.ERROR_CODE, String.valueOf(errorCode), jsonConfig);
		jso.accumulate(ServletConst.ERROR_MESSAGE, ErrorCons.get(errorCode), jsonConfig);
		jso.accumulate(ServletConst.RESPONSE_RESULT, result, jsonConfig); 
		return jso.toString();
	}
	/**
	 * 把json对象转换为hashMap值。
	 * 如果该json对象是数组，则该返回为list对象，对象中有多个相应值
	 * 如果该json对象中是非数组，则该list中只有一个hashMap对象；
	 * 如果json对象的格式错误，则返回为null值。
	 * 如果json对象的格式错误，则返回为null值。
	 * hashMap对象中的值为key，加value值，而根据json对象定义，该value值
	 * 可能又是一个json对象，该对象以同样的方式进行嵌套处理。
	 * 如果对象中还有子json对象，则递归转换。
	 * @param jsonObject json对象
	 * @return
	 */
	public static Object convertJSONToList(Object jsonObject, boolean recyFlag)
	{
		if (!(jsonObject instanceof JSON) || jsonObject instanceof JSONNull)
		{
			return null;			
		}
		JSON json = (JSON)jsonObject;
		if (json.isEmpty())
    	{
    		return null;
    	}		
    	boolean flag = json.isArray();
    	if (flag)
    	{
    		List retList = new ArrayList();
    		JSONArray jsonA = (JSONArray)json;
    		int size = jsonA.size();
    		for (int i = 0; i < size; i++)
    		{
    			Object subJson = jsonA.get(i);
    			if (subJson instanceof JSON)
    			{
    				retList.add(convertJSONToList(subJson, recyFlag));
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
    		Iterator<String> it = jsonO.keys();
    		String key;
    		Object value;
    		while(it.hasNext())
    		{
    			key = it.next();
    			value = jsonO.get(key); 
    			if (recyFlag && value instanceof JSON)
    			{
    				ret.put(key, convertJSONToList(value, recyFlag));
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
	 * 把对象转换为json对象字符串
	 * @param errorCode 错误代码
	 * @param o 结果对象
	 * @return 转换后的json对象，内容为：
	 * <p>
	 * 	{
	 * 		result:"xxx",
	 * 		errorMessage:"xxxxxxxx",
	 * 		errorCode:xxx
	 * 	}
	 * </p>
	 */
	public static String convertToJson(Integer errorCode, Object result)
	{
		JSONObject jso = new JSONObject();
		jso.accumulate(ServletConst.ERROR_CODE, String.valueOf(errorCode), jsonConfig);
		jso.accumulate(ServletConst.ERROR_MESSAGE, ErrorCons.get(errorCode), jsonConfig);
		jso.accumulate(ServletConst.RESPONSE_RESULT, result, jsonConfig); 
		return jso.toString();
	}
	
	/**
	 * 把对象转换为json对象字符串。该方法表示调用成功，返回的错误码为ErrorCons.NO_ERROR值。
	 * @param message 需要返回的提示信息。
	 * @param result 需要返回的结果。
	 * @return 转换后的json对象，内容为：
	 * <p>
	 * 	{
	 * 		result:"xxx",
	 * 		errorMessage:"xxxxxxxx",
	 * 		errorCode:xxx
	 * 	}
	 * </p>
	 */
	public static String convertToJson(Object message, Object result)
	{
		JSONObject jso = new JSONObject();
		jso.accumulate(ServletConst.ERROR_CODE, String.valueOf(ErrorCons.NO_ERROR), jsonConfig);
		jso.accumulate(ServletConst.ERROR_MESSAGE, message, jsonConfig);
		jso.accumulate(ServletConst.RESPONSE_RESULT, result, jsonConfig); 
		return jso.toString();
	}
	
	public static String convertToJson(Integer errorCode,Object message, Object result)
	{
		JSONObject jso = new JSONObject();
		jso.accumulate(ServletConst.ERROR_CODE, errorCode, jsonConfig);
		jso.accumulate(ServletConst.ERROR_MESSAGE, message, jsonConfig);
		jso.accumulate(ServletConst.RESPONSE_RESULT, result, jsonConfig); 
		return jso.toString();
	}
	
	public static String convertToJson(Object result)
	{
		JSONObject jso = new JSONObject();
		jso.accumulate(ServletConst.RESPONSE_RESULT, result, jsonConfig); 
		return jso.toString();
	}
	/**
	 * 把java对象转换为json格式的字符内容。如果该对象不能转换，则返回值为null。
	 * @param object
	 * @return
	 */
	public static String convertObjectToString(Object object)
	{
        try
        {
        	JSON json = JSONSerializer.toJSON(object, jsonConfig);
        	if (json == null)
        	{
        		return null;
        	}
        	return json.toString();
        }
        catch(JSONException j)
        {
        	return null;
        }
        catch(Exception e)
        {
        	return null;
        }
	}

	public static void convert2List(List list, Object json, Class clazz) {
		if (json instanceof ArrayList) {
			ArrayList list0 = (ArrayList) json;
			for (int i = 0; i < list0.size(); i++) {
				Object obj = list0.get(i);
				if (clazz == String.class) {
					list.add(list0.get(i).toString());
				} else if (obj instanceof HashMap) {
					JSONObject jsonObject = new JSONObject();
					HashMap<Object, Object> map = (HashMap<Object, Object>)obj;
					jsonObject.putAll(map);
					list.add(JSONObject.toBean(jsonObject, clazz));
				} else if(obj instanceof Number){
					if (clazz == Long.class) {
						list.add(((Number)obj ).longValue());
					} else if (clazz == Integer.class) {
						list.add(((Number) obj).intValue());
					} else if (clazz == Double.class) {
						list.add(((Number) obj).doubleValue());
					} 
					else
					{
						list.add(obj);
					}
				} else if(obj instanceof String){
					if (clazz == Long.class) {
						list.add(Long.parseLong((String)obj));
					} else if (clazz == Integer.class) {
						list.add(Integer.parseInt((String)obj));
					} else if (clazz == Double.class) {
						list.add(Double.parseDouble((String)obj));
					} else	{
						list.add(obj);
					}
				}
				else {
					list.add(obj);
				}
			}
		}
	}

	public static Long[] convert2LongArray(Object json) {
		if (json instanceof ArrayList) {
			ArrayList list0 = (ArrayList) json;
			Long[] arraylongs = new Long[list0.size()];
			for (int i = 0; i < list0.size(); i++) {
				arraylongs[i] = ((Number) list0.get(i)).longValue();
			}
			return arraylongs;
		}
		return null;
	}
}
