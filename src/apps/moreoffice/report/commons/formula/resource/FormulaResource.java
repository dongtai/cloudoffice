package apps.moreoffice.report.commons.formula.resource;

/**
 * 公式语法分析器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       实习生76(魏强) 
 * <p>
 * @维护者:      实习生76(魏强) 
 * <p>
 * @日期:       2012-12-26
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */

public interface FormulaResource
{
    // 分类
    String THIS_REPORT = "本报表";
    String STATS_FUNCTION = "统计函数";
    String DATETIME_FUNCTION = "日期和时间函数";
    String SET_FUNCTION = "集合函数";
    String STRING_FUNCTION = "字符串函数";
    String TYPECVT_OTHER_FUNCTION = "类型转换及其它函数";
    String MATH_FUNCTION = "数学函数";
    String ARITHMETIC_OPERATOR = "算术运算符";
    String STRING_OPERATOR = "字符串运算符";
    String RELATIONAL_OPERATOR = "关系运算符";
    String LOGICAL_OPERATOR = "逻辑运算符";
    String MISCELLANEOUS = "杂类";
    String INPUT_VALUE = "输入值";
    String SYSTEM_VARIABLE = "系统变量";
    
    // 操作符
    String LIKE = "形如";
    String BELONG = "属于";
    String NOT_BELONG = "不属于";
    String BETWEEN = "介于";
    String HAS_VALUE = "有值";
    String NULL_VALUE = "无值";
    String AND = "并且";
    String OR = "或者";
    String NOT = "不满足";
    String NULL = "常量.空值";
    
    // 错误信息格式
    String ERROR_FORMAT = "%s: %s";
    
    // 词法分析错误信息
    String LEXING_ERROR_001 = "左右括号不匹配";
    String LEXING_ERROR_002 = "输入小数不合法";
    String LEXING_ERROR_003 = "字符串解析错误";
    String LEXING_ERROR_004 = "输入文本为空";
    String LEXING_ERROR_005 = "无合法Token";
    String LEXING_ERROR_006 = "输入整数不合法";
    String LEXING_ERROR_007 = "输入文本超过长度限制";
    String LEXING_ERROR_008 = "标识符中不能含有单引号";
    String LEXING_ERROR_009 = "标识符不合法";
    
    // 语法分析错误信息
    String PARSING_ERROR_001 = "输入LexToken表为空";
    String PARSING_ERROR_002 = "解析后队列不空";
    String PARSING_ERROR_003 = "不支持的系统变量";
    String PARSING_ERROR_004 = "未输入输入变量的标识";
    String PARSING_ERROR_005 = "不支持的数据类型";
    String PARSING_ERROR_006 = "括号不匹配";
    String PARSING_ERROR_007 = "无法识别的变量";
    String PARSING_ERROR_008 = "不支持的函数";
    String PARSING_ERROR_009 = "语法分析数据类型不匹配";
    String PARSING_ERROR_010 = "函数解析错误";
    String PARSING_ERROR_011 = "不支持的数据表变量";
    String PARSING_ERROR_012 = "表达式类型不正确";
}