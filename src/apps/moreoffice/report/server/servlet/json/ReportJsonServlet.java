package apps.moreoffice.report.server.servlet.json;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.server.servlet.entity.ReportServlet;

/**
 * 报表json格式的Servlet
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-10-9
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class ReportJsonServlet extends ReportServlet
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
        // 得到json请求的具体参数
        HashMap<String, Object> paramsMap = (HashMap<String, Object>)jsonParamsMap
            .get(ParamCons.PARAMS);
        paramsMap.put(ParamCons.ISJSON, true);

        return super.paramsHandler(req, res, jsonParamsMap);
    }
}