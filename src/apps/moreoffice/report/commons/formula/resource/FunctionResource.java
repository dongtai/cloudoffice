package apps.moreoffice.report.commons.formula.resource;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 公式语法分析器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云) & User295(张琛琛) & 实习生76(魏强) 
 * <p>
 * @日期:       2012-12-27
 * <p>
 * @负责人:      实习生76(魏强) 
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface FunctionResource
{
    // 统计函数ID(8)
    short[] STATS_FUNCTION_ID_ARRAY = {
        FormulaCons.SUM, FormulaCons.AVERAGE, FormulaCons.COUNT, FormulaCons.COUNTUNIQUE, 
        FormulaCons.MAXVALUE, FormulaCons.MINVALUE, FormulaCons.MAXDATE, FormulaCons.MINDATE
    };
    
    // 统计函数名称(8)
    String[] STATS_FUNCTION_NAME_ARRAY = {
        "合计", "平均", "计数", "唯一计数",
        "最大值", "最小值", "最晚", "最早"
    };
    
    // 统计函数参数表(8)
    String[] STATS_FUNCTION_PARAMLIST_ARRAY = {
        "(<数字型字段名>)", "(<数字型字段名>)", "(<字段名>)", "(<字段名>)",
        "(<字段名>)", "(<字段名>)", "(<日期型字段名>)", "(<日期型字段名>)"
    };
    
    // 统计函数说明信息(8)
    String[] STATS_FUNCTION_INFO_ARRAY = {
        "此函数返回数字型字段的合计值。", 
        "此函数返回数字型字段的平均值.",
        "此函数返回指定字段的个数.",
        "此函数返回指定字段的个数（重复值不计数）.",
        "此函数返回指定字段的最大值.",
        "此函数返回指定字段的最小值.",
        "此函数返回指定日期型字段的最大者，比如，要得到最晚的发货日期，可以这样写：最晚(产品销货单_主表.发货日期).",
        "此函数返回指定日期型字段的最小者，比如，要得到最早的发货日期，可以这样写：最早(产品销货单_主表.发货日期)."
    };
    
    // 数学函数ID(5)
    short[] MATH_FUNCTION_ID_ARRAY = {
        FormulaCons.LOG, FormulaCons.LN, FormulaCons.EXP, FormulaCons.POWFUN, FormulaCons.ROUND    };
    
    // 数学函数名称(5)
    String[] MATH_FUNCTION_NAME_ARRAY = {
        "LOG10", "LN", "EXP", "POWER", "ROUND"
    };
    
    // 数学函数参数表(5)
    String[] MATH_FUNCTION_PARAMLIST_ARRAY = {
        "(<数字型字段名>)", "(<数字型字段名>)", "(<数字型字段名>)", "(<数字型字段名>, <指数>)", "(<数字型字段名>, <精度>)"
    };
    
    // 数学函数说明信息(5)
    String[] MATH_FUNCTION_INFO_ARRAY = {
        "此函数返回数字型字段以10为底的对数。例如：LOG10(100) 返回 100 以 10 为底的对数，即2。",
        "此函数返回数字型字段的自然对数。",
        "此函数返回以e为底，数字型字段为指数的次幂乘方值。",
        "此函数返回以数字型字段为底的指定指数的次幂乘方值。例如：POWER(2, 3) 返回 2 的 3 次幂，即 8。",
        "此函数返回数字型字段四舍五入为指定的精度的值。例如：ROUND(123.9994, 3) 返回 123.999。"
    };
        
    // 字符串函数ID(3)
    short[] STRING_FUNCTION_ID_ARRAY = {
        FormulaCons.SUBSTRING, FormulaCons.LEN, FormulaCons.UPPER
    };
    
    // 字符串函数名称(3)
    String[] STRING_FUNCTION_NAME_ARRAY = {
        "取子串", "文本长度", "转大写"
    };
    
    // 字符串函数参数表(3)
    String[] STRING_FUNCTION_PARAMLIST_ARRAY = {
        "(<字符串>, <起始位置>, <截取长度>)", "(<字符串>)", "(<字符串>)"
    };
    
    // 文本函数说明信息(3)
    String[] STRING_FUNCTION_INFO_ARRAY = {
        "此函数截取指定字符串的一部分。比如：取子串('abcdefg',2,3)，返回子串'bcd'。",
        "函数返回指定字符串的长度。",
        "此函数将字符串中的小写字母转换成大写字母"
    };
        
    // 日期与时间函数ID(19)
    short[] DATETIME_FUNCTION_ID_ARRAY = {
        FormulaCons.YEAR, FormulaCons.QUARTER, FormulaCons.MONTH, FormulaCons.DAY, FormulaCons.WEEKDAY, 
        FormulaCons.THISYEAR, FormulaCons.LASTYEAR, FormulaCons.THISQUARTER, FormulaCons.LASTQUARTER, FormulaCons.THISMONTH, FormulaCons.LASTMONTH, 
        FormulaCons.YEARSTART, FormulaCons.YEAREND, FormulaCons.QUARTERSTART, FormulaCons.QUARTEREND, FormulaCons.MONTHSTART, FormulaCons.MONTHEND,
        //FormulaCons.THISACCYEAR, FormulaCons.LASTACCYEAR, FormulaCons.THISACCQUARTER, FormulaCons.LASTACCQUARTER, FormulaCons.THISACCMONTH, FormulaCons.LASTACCMONTH,
        //FormulaCons.ACCYEARSTART, FormulaCons.ACCYEAREND, FormulaCons.ACCQUARTERSTART, FormulaCons.ACCQUARTEREND, FormulaCons.ACCMONTHSTART, FormulaCons.ACCMONTHEND,
        FormulaCons.DATEOP, FormulaCons.TIMEDIF,
    };
    
    // 日期与时间函数名称(19)
    String[] DATETIME_FUNCTION_NAME_ARRAY = {
        "年份值", "季度值", "月份值", "日期值", "星期几",
        "此年", "上年", "此季", "上季", "此月", "上月",
        "年初", "年末", "季初", "季末", "月初", "月末",
        //"此结算年", "上结算年", "此结算季", "上结算季", "此结算月", "上结算月", 
        //"结算年初", "结算年末", "结算季初", "结算季末", "结算月初", "结算月末", 
        "日期加减", "间隔时间",
    };
    
    // 日期与时间函数参数表(19)
    String[] DATETIME_FUNCTION_PARAMS_ARRAY = {
        "(<日期>)", "(<日期>)", "(<日期>)", "(<日期>)", "(<日期>)",
        "(<年份>)", "(<年份>)", "(<年份>, <季度>)", "(<年份>, <季度>)", "(<年份>, <月份>)", "(<年份>, <月份>)",
        "(<年份>)", "(<年份>)", "(<年份>, <季度>)", "(<年份>, <季度>)", "(<年份>, <月份>)", "(<年份>, <月份>)",
        //"此结算年", "上结算年", "此结算季", "上结算季", "此结算月", "上结算月", 
        //"结算年初", "结算年末", "结算季初", "结算季末", "结算月初", "结算月末", 
        "(<时间单位>, <加减数>, <基准日期>)", "(<时间单位>, <起始时间>, <终止时间>)",
    };
    
    // 日期与时间函数说明信息(19)
    String[] DATETIME_FUNCTION_INFO_ARRAY = {
        "此函数返回指定日期的年份值。例：年份值('2004-1-1')，年份值(产品销货单_主表.发货日期)。",
        "此函数返回指定日期的季度值，分别用1，2，3，4表示四个季度。比如：季度值('2004-3-5')=1。",
        "此函数返回制定日期的月份值。比如：月份值('2004-3-5')=3",
        "此函数返回指定日期的日期值。参数可以是任何日期型的表达式。比如：日期值('2004-3-5')=5。",
        "此函数返回指定日期是星期几。",
        "此函数返回由指定年份的起始日期和终止日期组成的时间区间。如，此年(2004)，返回('2004-1-1','2004-12-31')。此函数要求与“介于”一起使用。",
        "此函数返回一个时间区间。该区间的起点是指定年份上一年度的1月1日，该区间的终点是指定年份上一年度的12月31日。此函数要求与“介于”一起使用。",
        "此函数返回一个时间区间。该区间的起点是指定年、季的第一天，终点是指定年、季的最后一天。第一个参数年份取整数，第二个参数为季度值，分别用1，2，3，4表示四个季度。此函数要求与“介于”一起使用。",
        "此函数返回一个时间区间。该区间的起点是指定年、季的第一天；该区间的终点是指定年、季的最后一天。该函数要求和“介于”一起使用。",
        "此函数返回一个时间区间，该区间的起点是指定年、月的第一天，终点是指定年、月的最后一天。此函数要求与“介于”一起使用。",
        "此函数返回一个时间区间。该区间的起点是指定年、月的上一个月的第一天，终点是指定年、月的上一个月的最后一天。此函数要求与“介于”一起使用。",
        "此函数返回指定年的第一天。",
        "此函数返回指定年的最后一天。",
        "此函数返回指定年份、季度的第一天。",
        "此函数返回指定年份、季度的最后一天。",
        "此函数返回指定年、月的第一天。", 
        "此函数返回指定年、月的最后一天。",
        //"此结算年", "上结算年", "此结算季", "上结算季", "此结算月", "上结算月", 
        //"结算年初", "结算年末", "结算季初", "结算季末", "结算月初", "结算月末", 
        "此日期在基准日期的基础上，加上或减去某单位的时间，得到一个新的日期。第一个参数为时间单位，yy表示年，qq表示季，mm表示月，ww表示周，dd表示天，hh表示小时，mi表示分钟，ss表示秒。第二个参数为整数，正数表示增加，负数表示减少。比如：日期加减(mm,1,'2004-3-5')='2004-4-5'。",
        "此函数返回两个时间之间的间隔时间单位数。",
    };
    
    // 集合函数ID(1)
    short[] SET_FUNCTION_ID_ARRAY = { FormulaCons.GETSET };
    
    // 集合函数名称(1)
    String[] SET_FUNCTION_NAME_ARRAY = { "此集合" };
    
    // 集合函数参数(1)
    String[] SET_FUNCTION_PARAMLIST_ARRAY = { "(<值1>, <值2>, ..., <值n>)" };
    
    // 集合函数说明信息(1)
    String[] SET_FUNCTION_INFO_ARRAY = { "此函数表示由若干指定的值组成的集合。各取值的数据类型要求相同。" };
    
    // 类型转换函数及其它函数ID(4)
    short[] TYPECVT_OTHER_FUNCTION_ID_ARRAY = { FormulaCons.TOTEXT, FormulaCons.TODATE, FormulaCons.REPLACENULL, FormulaCons.IFELSE };
    
    // 类型转换函数及其它函数名称(4)
    String[] TYPECVT_OTHER_FUNCTION_NAME_ARRAY = { "转文本", "转日期", "空值替换", "条件取值" };
    
    // 类型转换函数及其它函数参数表(4)
    String[] TYPECVT_OTHER_FUNCTION_PARAMLIST_ARRAY = { 
        "(<值>, <日期格式>)", "(<值>)", "(<字段名>, <替换值>)", "条件取值(<条件1>, <值1>, <条件2>, <值2>, ... <其他值>))" 
    };
    
    // 转文本函数
    String TO_TEXT_FUNCTION = TYPECVT_OTHER_FUNCTION_NAME_ARRAY[0] + "(<值>, 'YYYYMMDD')";
    
    // 类型转换函数及其它函数说明信息(4)
    String[] TYPECVT_OTHER_FUNCTION_INFO_ARRAY = { 
        "此函数将指定内容转换为字符串。将日期类型的值转换为字符串时，需指定日期格式。",
        "此函数将指定内容转换为日期类型。",
        "使用指定的替换值替换空值。例如：合计(空值替换(订单_明细.数量, 0))",
        "此函数的参数列表由多对条件表达式和数值组成。条件表达式是由左至右加以计算的，当某个条件满足时,返回与此条件配对的值；若所有条件都不满足，则返回<其它值>。若确定必会满足某一条件，则参数<其他值>可以省略。"
    };
}