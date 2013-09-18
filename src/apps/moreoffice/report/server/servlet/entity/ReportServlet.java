package apps.moreoffice.report.server.servlet.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.server.service.ReportManager;
import apps.moreoffice.report.server.servlet.context.ReportApplicationContext;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 报表Servlet
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
public class ReportServlet extends BaseServlet
{
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
    @ SuppressWarnings({"unchecked"})
    protected Result paramsHandler(HttpServletRequest req, HttpServletResponse res,
        HashMap<String, Object> jsonParamsMap) throws ServletException, IOException
    {
        // 得到方法名
        Object obj = jsonParamsMap.get(ParamCons.METHOD);
        if (obj == null || !(obj instanceof String) || ((String)obj).length() <= 0)
        {
            return ErrorManager.getErrorResult(ErrorCodeCons.JSON_FORMAT_ERROR);
        }
        String method = (String)obj;

        // 得到json请求的具体参数
        HashMap<String, Object> paramsMap = (HashMap<String, Object>)jsonParamsMap
            .get(ParamCons.PARAMS);

        // 通过类反射执行方法
        ReportManager manager = (ReportManager)ReportApplicationContext.getInstance().getBean(
            "ReportManager");
        Result result = null;
        try
        {
            obj = manager.getClass().getMethod(method, HashMap.class).invoke(manager, paramsMap);
        }
        catch(Exception e)
        {
            result = getErrorResult(e);
        }

        if (obj instanceof Result)
        {
            result = (Result)obj;
        }
        else if (obj == null)
        {
            result = new Result();
        }
        else if (result == null)
        {
            result = new Result();
            result.setData((Serializable)obj);
        }
        return result;
    }
}