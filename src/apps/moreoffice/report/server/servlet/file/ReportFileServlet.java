package apps.moreoffice.report.server.servlet.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.constants.MethodCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.server.resource.TemplateSortResource;
import apps.moreoffice.report.server.servlet.entity.BaseServlet;
import apps.moreoffice.report.server.util.ErrorManager;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.handler.FilesHandler;

/**
 * 报表文件服务
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2013-1-17
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class ReportFileServlet extends BaseServlet
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
    @ SuppressWarnings("unchecked")
    protected Result paramsHandler(HttpServletRequest req, HttpServletResponse res,
        HashMap<String, Object> jsonParamsMap) throws ServletException, IOException
    {
        // 得到方法名
        Object obj = jsonParamsMap.get(ParamCons.METHOD);
        if (obj == null || !(obj instanceof String) || ((String)obj).length() <= 0)
        {
            return ErrorManager.getErrorResult(ErrorCodeCons.JSON_FORMAT_ERROR);
        }
        String action = (String)obj;

        // 得到json请求的具体参数
        HashMap<String, Object> paramsMap = (HashMap<String, Object>)jsonParamsMap
            .get(ParamCons.PARAMS);
        // 文件名数组
        String[] fileNames = null;
        ArrayList<String> fileNameList = (ArrayList<String>)paramsMap.get(ParamCons.FILENAME);
        if (fileNameList != null && fileNameList.size() > 0)
        {
            fileNames = new String[fileNameList.size()];
            fileNameList.toArray(fileNames);
        }

        // 文件路径
        String filePath = tempPath;
        String path = (String)paramsMap.get(ParamCons.PATH);
        if (path != null && path.length() > 0)
        {
            if (path.startsWith(TemplateSortResource.TEMPLATEPATH))
            {
                path = path.substring(TemplateSortResource.TEMPLATEPATH.length());
            }
            path = path.replace("/", File.separator);
            path = path.startsWith(File.separator) ? path.substring(1) : path; 
            filePath += path;
            filePath = filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
        }
        File folder = new File(filePath);
        if (!folder.exists())
        {
            folder.mkdirs();
        }
        // 保存文件
        FilesHandler.fileUploadByHttpForm(req, filePath, fileNames);

        if (action.equals(MethodCons.UPLOADHTML))
        {
        }
        else if (action.equals(MethodCons.UPLOADWORKFLOW))
        {
        }
        Result result = new Result();
        return result;
    }

    // 表单临时路径
    private static final String tempPath = WebConfig.webContextPath + File.separatorChar + "static"
        + File.separatorChar + "report" + File.separatorChar;
}