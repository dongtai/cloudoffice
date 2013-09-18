package apps.moreoffice.report.commons.formula.constants;

/**
 * 报表Token符号与函数ID表
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚) & 实习生76(魏强)  
 * <p>
 * @日期:       2012-7-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface FormulaCons
{
    // 非法
    short INVALID = -1; //非法 
    
    // 常量
    short NULL = 0; // 空值
    short INTEGER = 1; // 整数
    short DECIMAL = 2; // 小数(包含科学计数法)
    short SCIENCE = 3; // 科学计数法
    short NUMBER = 4; // 数字(包括整数与小数)
    short BOOLEAN = 5; // 布尔值
    short TEXT = 6; // 文本
    short DATE = 7; // 日期
    short PICTURE = 8; // 图片(暂时不考虑)
    short FILE = 9; // 文件(暂时不考虑)
    short NUMBER_SET = 10; // 数字集合
    short TEXT_SET = 11; // 文字集合
    short DATE_SET = 12; // 日期集合
    short DATE_RANGE = 13; // 日期区间

    // 变量
    short VARIANT = 20; // 变量
    short SYS_VAR = 21; // 系统变量
    short INPUT_VAR = 22; // 输入变量
    short TABLE_VAR = 23; // 本报表变量
    short FIELD_VAR = 24; // 数据表变量
    short VARIANT_SET = 25; // 变量集合

    // 所有操作符
    short OP_START = 30; // 操作符开始标记
    
    // 算术运算符（参数为数值，返回数值）
    short PLUS = 33; // 加号
    short MINUS = 34; // 减号
    short MULTIPLY = 35; // 乘号
    short DIVIDE = 36; // 除号
    short POWER = 37; // 次方

    // 字符串运算符
    short CONCAT = 38; // 连接

    // 关系运算符（参数同类型，返回逻辑值）
    short LESS = 39; // 小于
    short LESS_EQUAL = 40; // 小于或等于
    short EQUAL = 41; // 等于
    short GREATER_EQUAL = 42; // 大于或等于
    short GREATER = 43; // 大于
    short NOT_EQUAL = 44; // 不等于
    short LIKE = 45; // 形如
    short BETWEEN = 46; // 介于
    short BELONG = 47; // 属于
    short NOT_BELONG = 48; // 不属于
    short HAS_VALUE = 49; // 有值
    short NULL_VALUE = 50; // 无值

    // 逻辑运算符（参数逻辑值，返回逻辑值）
    short AND = 60; // 并且
    short OR = 61; // 或者
    short NOT = 62; // 不满足

    // 函数
    short FUNCTION_START = 256; // 函数开始标记
    short FUNCTION = 256; // 函数

    // 统计函数
    short SUM = 257; // 合计
    short AVERAGE = 258; // 平均
    short COUNT = 259; // 计数
    short COUNTUNIQUE = 260; // 唯一计数
    short MAXVALUE = 261; // 最大值
    short MINVALUE = 262; // 最小值
    short MAXDATE = 263; // 最晚
    short MINDATE = 264; // 最早

    // 数学函数
    short LOG = 384; // LOG10
    short LN = 385; // LN
    short EXP = 386; // EXP
    short POWFUN = 387; // POWER
    short ROUND = 388; // ROUND

    // 字符串函数
    short SUBSTRING = 512; // 取子串
    short LEN = 513; // 文本长度
    short UPPER = 514; // 转大写

    // 日期与时间函数
    short YEAR = 640; // 年份值
    short QUARTER = 641; // 季度值
    short MONTH = 642; // 月份值
    short DAY = 643; // 日期值
    short WEEKDAY = 644; // 星期几
    short THISYEAR = 645; // 此年
    short LASTYEAR = 646; // 去年
    short THISQUARTER = 647; // 此季
    short LASTQUARTER = 648; // 上季
    short THISMONTH = 649; // 此月
    short LASTMONTH = 650; // 上月
    short YEARSTART = 651; // 年初
    short YEAREND = 652; // 年末
    short QUARTERSTART = 653; // 季初
    short QUARTEREND = 654; // 季末
    short MONTHSTART = 655; // 月初
    short MONTHEND = 656; // 月末

    // 结算类型日期函数(暂不考虑)
    short THISACCYEAR = 657; // 此结算年
    short LASTACCYEAR = 658; // 上结算年
    short THISACCQUARTER = 659; // 此结算季
    short LASTACCQUARTER = 660; // 上结算季
    short THISACCMONTH = 661; // 此结算月
    short LASTACCMONTH = 662; // 上结算月
    short ACCYEARSTART = 663; // 结算年初
    short ACCYEAREND = 664; // 结算年末
    short ACCQUARTERSTART = 665; // 结算季初
    short ACCQUARTEREND = 666; // 结算季末
    short ACCMONTHSTART = 667; // 结算月初
    short ACCMONTHEND = 668; // 结算月末

    // 日期计算函数
    short DATEOP = 669; // 日期加减
    short TIMEDIF = 670; // 间隔时间

    // 集合函数
    short GETSET = 768; // 此集合

    // 类型转换函数
    short TOTEXT = 780; // 转文本
    short TODATE = 781; // 转日期

    // 其他函数
    short REPLACENULL = 800; // 空值替换
    short IFELSE = 801; // 条件取值 

    // 每一类函数都预留了一些ID
    short FUNCTION_END = 899; // 函数结束标记（ID不能超过CONTROL_STRAT）

    // 控制符号（优先级与分隔）
    short CONTROL_STRAT = 900; // 控制符号标记
    short LEFT_ROUND_BRACKET = 901; // 左括号
    short RIGHT_ROUND_BRACKET = 902; // 右括号
    short COMMA = 903; // 逗号(参数分隔符)
    short BLANK = 904; // 空白(包括空格、制表符、换行符)
    
    // 解析字符串长度限制
    short PARSING_TEXT_LIMIT = 1024;
}