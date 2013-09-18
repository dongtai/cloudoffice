package apps.moreoffice.report.server.servlet.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.server.service.ReportEntityManager;
import apps.moreoffice.report.server.servlet.context.ReportApplicationContext;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 报表实体对象Servlet
 * 专门负责接受实体对象的增删改
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class ReportEntityServlet extends BaseServlet
{
    // 报表实体类管理器
    private ReportEntityManager rem = ((ReportEntityManager)ReportApplicationContext.getInstance()
        .getBean("ReportEntityManager"));

    /**
     * 报表实体对象服务
     * 
     * @param req http请求
     * @param res http返回
     * @throws IOException 异常处理
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        try
        {
            // 得到对象流
            ObjectInputStream entityStream = new ObjectInputStream(req.getInputStream());
            // 读取实体对象
            DataBaseObject entity = (DataBaseObject)entityStream.readObject();
            // 保存
            Serializable resultData = rem.save(entity);
            Result result = null;
            if (resultData != null)
            {
                result = new Result();
                result.setData(resultData);
            }
            else
            {
                result = ErrorManager.getErrorResult(ErrorCodeCons.NOTKNOWN_ERROR);
            }

            // 返回给客户端
            write(res, result == null ? new Result() : result);
        }
        catch(Exception e)
        {
            // 对异常统一进行处理
            write(res, getErrorResult(e));
        }
    }
}