package apps.moreoffice.report.server.resource;

/**
 * 错误信息资源
 * 
 * <p>
 * <p>
 * <p>
 */
public interface ErrorMessage
{
    String NO_ERROR = "操作成功";
    String SYSTEM_INIT_ERROR = "系统初始化错误";
    String JSON_FORMAT_ERROR = "json 格式错误";
    String RESULT_FORMAT_ERROR = "返回值格式错误，非序列化对象";
    String NOTKNOWN_ERROR = "未知错误";

    String TEMPLATE_NOT_EXIST = "模板不存在";

    String ERROR_NULLPOINTER = "空指针异常！";
}