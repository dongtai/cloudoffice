package apps.moreoffice.report.commons.domain.constants;

/**
 * 错误编码常量接口
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
public interface ErrorCodeCons
{
    // 无错误 
    public final static int NO_ERROR = 0;

    // json格式错误
    public final static int JSON_FORMAT_ERROR = 10;
    // 返回值格式错误
    public final static int RESULT_FORMAT_ERROR = 11;
    
    // 模板不存在
    public final static int TEMPLATE_NOT_EXIST = 100000;

    /** 没有相应记录*/
    public final static Integer ERROR_NO_DATA = 600000 + 1;
    /** 存在不合法的多条记录*/
    public final static Integer ERROR_MOREDATA = ERROR_NO_DATA + 1;
    /** 已经存在*/
    public final static Integer ERROR_ALREADY_EXISTS = ERROR_MOREDATA + 1;
    /** SQL语句存在问题*/
    public final static Integer ERROR_SQL_SYNTAX = ERROR_ALREADY_EXISTS + 1;
    /** 不能删除，可能字段不存在*/
    public final static Integer ERROR_CANNOT_DROP = ERROR_SQL_SYNTAX + 1;
    /** 表不存在*/
    public final static Integer ERROR_UNKNOWN_TABLE = ERROR_CANNOT_DROP + 1;
    /** 表名不合法或表已存在*/
    public final static Integer ERROR_UNKNOWN_TABLENAME = ERROR_UNKNOWN_TABLE + 1;
    /** 存在约束关系*/
    public final static Integer ERROR_RESTRICT = ERROR_UNKNOWN_TABLENAME + 1;
    /** 字段已存在*/
    public final static Integer ERROR_DUPLICATE_COLUMN = ERROR_RESTRICT + 1;
    /** SQL出错*/
    public final static Integer ERROR_SQL = ERROR_DUPLICATE_COLUMN + 1;
    /** 参数出错*/
    public final static Integer ERROR_PARAM = ERROR_SQL + 1;
    /** 空指针*/
    public final static Integer ERROR_NULLPOINTER = ERROR_PARAM + 1;

    // 未知错误
    public final static int NOTKNOWN_ERROR = Integer.MAX_VALUE;
}