package apps.moreoffice.report.server.util;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.server.resource.ErrorMessage;

/**
 * 错误代码及错误信息管理类
 * 
 * 如果内部要增加新的错误代码及错误信息，请按照下面三步做：
 * 1：在ErrorCodeCons中增加对应的错误代码
 * 2：在ErrorMessage中增加对应的错误信息
 * 3：在本类的方法 initDefaultData() 中添加put一行
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
public class ErrorManager
{
    // 错误信息Map
    private static HashMap<Integer, String> errorMap;

    /**
     * 根据错误代码得到错误信息
     * 
     * @param errorCode 错误代码
     * @return String 错误信息
     */
    public static String getErrorMessage(int errorCode)
    {
        if (errorMap == null)
        {
            return ErrorMessage.SYSTEM_INIT_ERROR;
        }

        String errorMessage = errorMap.get(errorCode);
        if (errorMessage == null)
        {
            return ErrorMessage.NOTKNOWN_ERROR;
        }

        return errorMessage;
    }

    /**
     * 得到错误结果
     * 
     * @return Result 结果
     */
    public static Result getErrorResult()
    {
        Result result = new Result();
        result.setErrorCode(ErrorCodeCons.NOTKNOWN_ERROR);
        return result;
    }

    /**
     * 得到错误结果
     * 
     * @param errorCode 错误代码
     * @return Result 结果
     */
    public static Result getErrorResult(int errorCode)
    {
        Result result = new Result();
        result.setErrorCode(errorCode);
        result.setErrorMessage(getErrorMessage(errorCode));
        return result;
    }

    /**
     * 得到错误结果
     * 
     * @param error 错误信息
     * @return Result 结果集
     */
    public static Result getErrorResult(String error)
    {
        Result result = new Result();
        result.setErrorCode(ErrorCodeCons.NOTKNOWN_ERROR);
        result.setErrorMessage(error);
        return result;
    }

    /**
     * 初始化默认错误信息
     */
    public static void initDefaultData()
    {
        if (errorMap == null)
        {
            errorMap = new HashMap<Integer, String>();
        }

        errorMap.put(ErrorCodeCons.NO_ERROR, ErrorMessage.NO_ERROR);
        errorMap.put(ErrorCodeCons.JSON_FORMAT_ERROR, ErrorMessage.JSON_FORMAT_ERROR);
        errorMap.put(ErrorCodeCons.RESULT_FORMAT_ERROR, ErrorMessage.RESULT_FORMAT_ERROR);
        errorMap.put(ErrorCodeCons.ERROR_NULLPOINTER, ErrorMessage.ERROR_NULLPOINTER);
        errorMap.put(ErrorCodeCons.TEMPLATE_NOT_EXIST, ErrorMessage.TEMPLATE_NOT_EXIST);
    }

    /**
     * 得到异常错误提示
     * 
     * @param e 异常
     * @return String 错误提示
     */
    public static String getExceptionMessage(Exception e)
    {
        // TODO 增加资源常量
        String error;
        String simpleName = e.getClass().getSimpleName();
        if (simpleName.equals("NullPointerException"))
        {
            error = "空指针";
        }
        else if (simpleName.equals("InvocationTargetException"))
        {
            error = "";
        }
        else
        {
            String str = e.getMessage();
            if (str == null)
            {
                error = "未知错误";
            }
            else if (str.indexOf("already exists") != -1)
            {
                error = "数据库中已经存在相同的表或字段";
            }
            else if (str.indexOf("SQL syntax") != -1)
            {
                error = "SQL语句存在问题";
            }
            else if (str.indexOf("Can't DROP") != -1)
            {
                error = "不能删除，表或字段不存在";
            }
            else if (str.indexOf("Duplicate column") != -1)
            {
                error = "字段已存在";
            }
            else if (str.indexOf("Unknown table") != -1)
            {
                error = "表不存在";
            }
            else if (str.indexOf("Could not execute JDBC batch update") != -1)
            {
                error = "存在约束关系，当前对象已经被使用";
            }
            else if (str.indexOf("Can't create table") != -1)
            {
                error = "表名不合法或表已存在";
            }
            else if (str.indexOf("ReportManager") != -1)
            {
                error = "没有对应的方法";
            }
            else if (str.indexOf("Batch update returned unexpected") != -1)
            {
                error = "对象存在id，但在数据库中找不到此对象";
            }
            else if (str.indexOf("not-null property") != -1)
            {
                error = "字段值不能为空：" + str.substring(str.lastIndexOf("."));
            }
            else
            {
                error = "未知错误";
            }
        }
        return error;
    }
}