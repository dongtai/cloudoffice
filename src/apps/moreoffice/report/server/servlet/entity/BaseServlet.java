package apps.moreoffice.report.server.servlet.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.report.commons.JSONTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.server.util.ErrorManager;
import apps.moreoffice.report.server.util.WebTools;

/**
 * 服务器接受参数统一处理类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class BaseServlet extends HttpServlet
{
    /**
     * <P>
     * <pre>
     * 用户请求参数传输方式及格式说明：
     *  1.参数及数据传输格式：json格式、原始二进制数据。数据内容的字符编码方式为UTF-8。
     *  2.Request json: 请求说明：
     *      a)请求的参数名为：jsonParams，值为json格式。
     *      b)请求json 格式：
     *      c)json 格式描述如下：
     *      {
     *          method:"method1",
     *          params : {key:"value1",key2:"value2"},
     *          token: "xxxxxxxx"
     *      }
     *      d)method:API 名称，必须填写
     *      e)params:为请求参数;每个API 中都对参数的说明如果参数为空，用params:{}描述。
     *      f)token:请求的令牌，部分API 需要用户登录后的toeken 进行调用，系统会根据token 判断当前用户是否有权限操作。对于不需要token 的API，该参数可以省略。
     *      g)如果在请求中涉及到文件的上传，则文件内容以文件原始二进制内容的方式在body中，即是以form表单的方式提交文件。
     *      h)请求参数可以设置在URL地址的参数中：
     *          例：
     *          https://127.0.0.1:8080/static/userservice?jsonParams={method:"login",params:{account:"sky",password:"123456"}}
     *      i)也可以把请求参数放到Header 中而不放在请求url中：
     *          如果把请求参数放在header中，此时header的name为：jsonParams，值为请求参数的json对象。
     *          例：
     *          var xmlHttp = createXMLHttpRequest();
     *          var header = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
     *          xmlHttp.open("POST","userservice");
     *          xmlHttp.onreadystatechange = loginAfterHandler;     
     *          xmlHttp.setRequestHeader("jsonParams", header); 
     *          xmlHttp.send(null); 
     *      j)也可以把请求参数放在request的属性：
     *          如果把请求参数在request的属性中，此时attritue的name为：jsonParams，值为请求参数的json对象。
     *          例：
     *          <%
     *              String params = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
     *              request.setAttribute("jsonParams", params);
     *          %>
     *          <jsp:forward page="/static/userservice"/>
     *      k)也可以把参数通过post方式，放在请求内容的中:
     *          此时请求header中name为Content-Type的值需要设置为jsonParams。
     *          此时该post的内容中的值为：具体的请求json格式数据。
     *          此种方式进行提交参数时候，目前只支持提交参数内容后，就不能同时再提交其他内容（包括文件内容），也就是如果post的内容是请求参数，则系统目前会把所有内容都作为请求参数处理。
     *          例：
     *          var xmlHttp = createXMLHttpRequest();
     *          var data = "{method:’login’,params:{domain:’com.yozo.do’,account:’test1’,password:’123456’,autoDirect:’xxxxx’}}";
     *          xmlHttp.open("POST","userservice"); 
     *          xmlHttp.onreadystatechange = loginAfterHandler;
     *          xmlHttp.setRequestHeader("Content-Type", "jsonParams");
     *          xmlHttp.send(qS);
     *  
     *      l)请求参数获取的顺序是：
     *          先获取url地址中的参数，没有再获取header中的参数，没有再获取request的attribute中的参数，没有再获取post的内容数据中的值。请求参数不支持放在session中。
     *      4.Response json: 返回值说明
     *          a)返回的数据json 格式：
     *          {
     *              errorCode:0,
     *              errorMessage:"xxxxxxxx",
     *              result:"xxx"
     *          }
     *          b)result 是对应API的返回结果。
     *          c)message 为正确或错误提示信息。
     *          d)errorCode 为错误异常码，正常返回0。
     *          e)错误码可参见错误码与错误信息。
     * <pre>
     * <p>
     */
    @ SuppressWarnings("unchecked")
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,
        IOException
    {
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");
        // 从url中获取请求参数 
        String stringParam = req.getParameter(ParamCons.JSONPARAMS);
        // 从header中获取请求参数
        if (stringParam == null || stringParam.length() <= 0)
        {
            stringParam = req.getHeader(ParamCons.JSONPARAMS);
        }
        // 从request属性中获取请求参数
        if (stringParam == null || stringParam.length() <= 0)
        {
            stringParam = (String)req.getAttribute(ParamCons.JSONPARAMS);
        }
        // 从post的数据中获取请求参数
        if (stringParam == null || stringParam.length() <= 0)
        {
            String content = req.getHeader("Content-Type");
            if (content != null && content.indexOf(ParamCons.JSONPARAMS) >= 0)
            {
                InputStream in = req.getInputStream();
                byte[] con = new byte[1024];
                int size;
                StringBuffer sb = new StringBuffer();
                while ((size = in.read(con)) >= 0)
                {
                    sb.append(new String(con, 0, size, "utf-8"));
                }
                stringParam = sb.toString();
            }
        }

        /**
         * 服务器层的编码转换，虽然客户端传递数据时已经转换为UTF-8,但是对于不同的服务部署，传递时编码又不同
         * 比如对于Tomcat，其传递参数使用ISO-8859-1编码，所以此处是根据服务器不同进行编码转换
         */
        String jsonParams = WebTools.convertParams(stringParam);
        // 内部编码转换，默认编辑器内采用UTF-8编码
        jsonParams = URLDecoder.decode(jsonParams, "UTF-8");
        // 把json字符串转换为对象
        Object params = JSONTools.convertJsonToValue(jsonParams, true);

        // 得到返回结果
        if (params != null && params instanceof HashMap)
        {
            HashMap<String, Object> jsonParamsMap = (HashMap<String, Object>)params;
            write(res, paramsHandler(req, res, jsonParamsMap));
        }
        else
        {
            write(res, ErrorManager.getErrorResult(ErrorCodeCons.JSON_FORMAT_ERROR));
        }
    }

    /**
     * 具体处理各个请求内容
     * 
     * @param req http请求
     * @param res http返回
     * @param jsonParamsMap 请求参数(已经处理好的参数)
     * @return Result 返回结果
     * @throws ServletException 服务器异常
     * @throws IOException 文件异常
     */
    protected Result paramsHandler(HttpServletRequest req, HttpServletResponse res,
        HashMap<String, Object> jsonParamsMap) throws ServletException, IOException
    {
        return null;
    }

    /**
     * 回写序列化对象
     * 
     * @param res http返回
     * @param obj 序列化对象
     */
    protected void write(HttpServletResponse res, Serializable obj)
    {
        try
        {
            res.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
            ObjectOutputStream out = new ObjectOutputStream(res.getOutputStream());
            out.writeObject(obj);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 得到错误的结果
     * 
     * @param e 异常
     * @return Result 错误Result
     */
    protected Result getErrorResult(Exception e)
    {
        Result result = new Result();
        result.setErrorMessage(ErrorManager.getExceptionMessage(e));
        return result;
    }
}