package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.util.server.DES;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;

/**
 * 统一处理参数信息。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public abstract class AbstractServlet extends HttpServlet
{
	/**
	 * <P>
	 * <pre>
	 * 用户请求参数传输方式及格式说明：
	 *  1.参数及数据传输格式：json格式、原始二进制数据。数据内容的字符编码方式为UTF-8。
	 *	2.Request json: 请求说明：
	 *		a)请求的参数名为：jsonParams，值为json 格式。
	 *		b)请求json 格式：
	 *		c)json 格式描述如下：
	 *		{
	 *			method:"method1",
	 *			params : {key:"value",key2:"value2"},
	 *			token: "xxxxxxxx"
	 *		}
	 *		d)method:API 名称，必须填写
	 *		e)params:为请求参数;每个API 中都对参数的说明如果参数为空，用params:{}描述。
	 *		f)token:请求的令牌，部分API 需要用户登录后的toeken 进行调用，系统会根据token 判断当前用户是否有权限操作。对于不需要token 的API，该参数可以省略。
	 *		g)如果在请求中涉及到文件的上传，则文件内容以文件原始二进制内容的方式在body中，即是以form表单的方式提交文件。
	 *		h)请求参数可以设置在URL地址的参数中：
	 *			例：
	 *			https://127.0.0.1:8080/static/userservice?jsonParams={method:"login",params:{account:"sky",password:"123456"}}
	 *		i)也可以把请求参数放到Header 中而不放在请求url中：
	 *			如果把请求参数放在header中，此时header的name为：jsonParams，值为请求参数的json对象。
	 *			例：
	 *			var xmlHttp = createXMLHttpRequest();
	 *			var header = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
	 *			xmlHttp.open("POST","userservice");
	 *			xmlHttp.onreadystatechange = loginAfterHandler;		
	 *			xmlHttp.setRequestHeader("jsonParams", header);	
	 *			xmlHttp.send(null); 
	 *		j)也可以把请求参数放在request的属性：
	 *			如果把请求参数在request的属性中，此时attritue的name为：jsonParams，值为请求参数的json对象。
	 *			例：
	 *			<%
	 *				String params = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
	 *				request.setAttribute("jsonParams", params);
	 *			%>
	 *			<jsp:forward page="/static/userservice"/>
	 *		k)也可以把参数通过post方式，放在请求内容的中:
	 *			此时请求header中name为Content-Type的值需要设置为jsonParams。
	 *			此时该post的内容中的值为：具体的请求json格式数据。
	 *			此种方式进行提交参数时候，目前只支持提交参数内容后，就不能同时再提交其他内容（包括文件内容），也就是如果post的内容是请求参数，则系统目前会把所有内容都作为请求参数处理。
	 *			例：
	 *			var xmlHttp = createXMLHttpRequest();
	 *			var data = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
	 *			xmlHttp.open("POST","userservice");	
	 *			xmlHttp.onreadystatechange = loginAfterHandler;
	 *			xmlHttp.setRequestHeader("Content-Type", "jsonParams");
	 *			xmlHttp.send(qS);
	 *	
	 *		l)请求参数获取的顺序是：
	 *			先获取url地址中的参数，没有再获取header中的参数，没有再获取request的attribute中的参数，没有再获取post的内容数据中的值。请求参数不支持放在session中。
	 *		4.Response json: 返回值说明
	 *			a)返回的数据json 格式：
	 *			{
	 *				result:"xxx",
	 *				errorMessage:"xxxxxxxx",
	 *				errorCode:0
	 *			}
	 *			b)result 是对应API 的返回结果。
	 *			c)message 为正确或错误提示信息。
	 *			d)errorCode 为错误异常码，正常返回0。
	 *			e)错误码可参见错误码与错误信息。
	 * <pre>
	 * <p>
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,  IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String stringParam = request.getParameter(ServletConst.JSON_PARAMS_KEY);   //  从url中获取请求参数 
		if (stringParam == null || stringParam.length() <= 0)    // 从header中获取请求参数
		{
			stringParam = request.getHeader(ServletConst.JSON_PARAMS_KEY);
		}
		if (stringParam == null || stringParam.length() <= 0)    // 从request属性中获取请求参数
		{
			stringParam = (String)request.getAttribute(ServletConst.JSON_PARAMS_KEY);
		}
		if (stringParam == null || stringParam.length() <= 0)
		{
			String ct = request.getHeader("Content-Type"); 
			if (ct != null && ct.indexOf(ServletConst.JSON_PARAMS_KEY) >= 0)    // 从post的数据中获取请求参数
			{
				InputStream in = request.getInputStream();
				byte[] con = new byte[1024];
				int size;
				StringBuffer sb = new StringBuffer();
				while((size = in.read(con)) >= 0)
				{
					sb.append(new String(con, 0, size));
				}
				stringParam = sb.toString();
			}
		}
		
		String jsonParams = WebTools.converStr(stringParam);
		
		/**
		 * 此处是用于解密,主要用于邮件方面
		 */
		if(!jsonParams.contains(ServletConst.METHOD_KEY)){
			try {
				jsonParams=new DES().decrypt(jsonParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        try
        {
        	jsonParams = WebTools.decode(jsonParams, "utf-8");
        	jsonParams = jsonParams.replace("\n", "\\n");//需要将回车符替换掉，否则会报json格式错误
        }
        catch(Exception e)
        {
        	LogsUtility.error(e);
        }
        Object params = JSONTools.convertParams(jsonParams, true);
        String error = null;
        if (params != null)
        {
        	HashMap<String, Object> paramsMap = (HashMap<String, Object>)params;     // 根据规范定义，json参数应该有一个对象。
        	try
        	{
        		String method = (String) paramsMap.get(ServletConst.METHOD_KEY); //
//        		System.out.println("method333333333333========="+method);
        		if (method == null || method.length() <= 0) // json请求方法参数不可以为null
        		{
        			error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);        			
        		}
        		else
        		{
        			error = handleService(request, response, paramsMap);
        		}
        	}
    		catch(ClassCastException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);	    	
    	    }
    	    catch(NullPointerException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);	    	
    	    }
    	    catch(NumberFormatException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);	    	
    	    }
    	    catch(ParseException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.JSON_PARAM_ERROR, null);	    	
    	    }
    	    catch(PathNotFoundException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
    	    }
    	    catch(RepositoryException e)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
    	    }
    	    catch(IOException ee)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.FILE_SYSTEM_ERROR, null);
    	    }
    	    catch(Exception ee)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);
    	    }
    	    catch(Throwable ee)
    	    {
    	    	error = JSONTools.convertToJson(ErrorCons.SYSTEM_ERROR, null);
    	    }
        }
        else
        {
        	error = JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
        }
        if (error != null)
	    {
    	    response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
    	    response.getWriter().write(error);
	    }
	}
	
	/**
	 * 具体处理各个请求内容的业务逻辑。
	 * @param request
	 * @param response
	 * @param params
	 * @throws ServletException
	 * @throws IOException
	 * @return 返回后端处理结束后，需要返回给前端的相应结果内容，如果不需要返回给前端请求任何内容，或者在
	 * 业务处理内部自己已经做过相应的返回处理，则返回值必须为null。
	 */
	protected abstract String handleService(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> params) throws ServletException,  Exception;
	
}
