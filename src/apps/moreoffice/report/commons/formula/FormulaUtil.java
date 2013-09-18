package apps.moreoffice.report.commons.formula;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.moreoffice.report.commons.formula.resource.FunctionResource;

/**
 * 公式工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       实习生76(魏强) & User381(俞志刚)
 * <p>
 * @日期:       2012-12-27
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class FormulaUtil
{
    // 函数名 - 函数id Hash表
    private static HashMap<String, Short> nameIdTable;
    // 函数id - 函数名 Hash表
    private static HashMap<Short, String> idNameTable;
    
    // 函数id数组
    private static final short[][] functionIdArrays = {FunctionResource.STATS_FUNCTION_ID_ARRAY, 
        FunctionResource.MATH_FUNCTION_ID_ARRAY, FunctionResource.STRING_FUNCTION_ID_ARRAY, 
        FunctionResource.DATETIME_FUNCTION_ID_ARRAY, FunctionResource.SET_FUNCTION_ID_ARRAY,
        FunctionResource.TYPECVT_OTHER_FUNCTION_ID_ARRAY};
    
    // 函数名数组
    private static final String[][] functionNameArrays = {FunctionResource.STATS_FUNCTION_NAME_ARRAY,
        FunctionResource.MATH_FUNCTION_NAME_ARRAY, FunctionResource.STRING_FUNCTION_NAME_ARRAY,
        FunctionResource.DATETIME_FUNCTION_NAME_ARRAY, FunctionResource.SET_FUNCTION_NAME_ARRAY,
        FunctionResource.TYPECVT_OTHER_FUNCTION_NAME_ARRAY};

    // 返回与公式函数名相对应的函数id；如果输入函数名不存在，则返回-1
    public static short getFunctionId(String functionName)
    {
        if (nameIdTable == null)
        {
            initializeNameIdTable();
        }

        Short id = nameIdTable.get(functionName);
        if (id != null)
        {
            return id.shortValue();
        }
        else
        {
            return -1;
        }
    }
    
    // 返回与公式函数id相对应的函数名；如果输入函数id不存在，则返回null
    public static String getFunctionName(short functionId)
    {
        if (idNameTable == null)
        {
            initializeIdNameTable();
        }

        return idNameTable.get(functionId);
    }

    // 对输入参数类型做检查，并返回结果数据类型
    public static short getReturnType(short functionId, short[] paramTypes)
    {
        switch (functionId)
        {
            // 统计函数(合计、平均)
            // 数学函数(log、ln、取指数)
            case FormulaCons.SUM:
            case FormulaCons.AVERAGE:
            case FormulaCons.LOG:
            case FormulaCons.LN:
            case FormulaCons.EXP:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;

            // 统计函数(计数、唯一计数)
            case FormulaCons.COUNT:
            case FormulaCons.COUNTUNIQUE:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.TEXT
                        || paramTypes[0] == FormulaCons.DATE || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;
            
            // 统计函数(最大值、最小值)
            case FormulaCons.MAXVALUE:
            case FormulaCons.MINVALUE:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER)
                    {
                        return FormulaCons.NUMBER;
                    }
                    else if (paramTypes[0] == FormulaCons.TEXT)
                    {
                        return FormulaCons.TEXT;
                    }
                    else if (paramTypes[0] == FormulaCons.DATE)
                    {
                        return FormulaCons.DATE;
                    }
                    else if (paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.VARIANT;
                    }
                }
                return FormulaCons.INVALID;

            // 统计函数(最晚、最早)
            case FormulaCons.MAXDATE:
            case FormulaCons.MINDATE:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.DATE || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.DATE;
                    }
                }
                return FormulaCons.INVALID;
            
            // 数学函数(求乘幂、四舍五入)
            case FormulaCons.POWFUN:
            case FormulaCons.ROUND:
                if (paramTypes.length == 2)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.NUMBER || paramTypes[1] == FormulaCons.VARIANT;
                    if (c0 && c1)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;
                
            // 字符串函数(取子串)
            case FormulaCons.SUBSTRING:
                if (paramTypes.length == 3)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.TEXT || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.NUMBER || paramTypes[1] == FormulaCons.VARIANT;
                    boolean c2 = paramTypes[2] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT;
                    if (c0 & c1 & c2)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;
                
            // 字符串函数(文本长度)
            case FormulaCons.LEN:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.TEXT || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;
                
             // 字符串函数(转大写)
            case FormulaCons.UPPER:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.TEXT || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.TEXT;
                    }
                }
                return FormulaCons.INVALID;
                
            // 日期函数(年份值、季度值、月份值、日期值、星期几)
            case FormulaCons.YEAR:
            case FormulaCons.QUARTER:
            case FormulaCons.MONTH:
            case FormulaCons.DAY:
            case FormulaCons.WEEKDAY:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.DATE || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;
           
            // 日期函数(此年、上年)
            case FormulaCons.THISYEAR:
            case FormulaCons.LASTYEAR:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.DATE_RANGE;
                    }
                }
                return FormulaCons.INVALID;
                
            // 日期函数(此季、上季、此月、上月)
            case FormulaCons.THISQUARTER:
            case FormulaCons.LASTQUARTER:
            case FormulaCons.THISMONTH:
            case FormulaCons.LASTMONTH:
                if (paramTypes.length == 2)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.NUMBER || paramTypes[1] == FormulaCons.VARIANT;
                    if (c0 && c1)
                    {
                        return FormulaCons.DATE_RANGE;
                    }
                }
                return FormulaCons.INVALID;
                
            // 日期函数(年初、年末)
            case FormulaCons.YEARSTART:
            case FormulaCons.YEAREND:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.DATE;
                    }
                }
                return FormulaCons.INVALID;
                
            // 日期函数(季初、季末、月初、月末)
            case FormulaCons.QUARTERSTART:
            case FormulaCons.QUARTEREND:
            case FormulaCons.MONTHSTART:
            case FormulaCons.MONTHEND:
                if (paramTypes.length == 2)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.NUMBER || paramTypes[1] == FormulaCons.VARIANT;
                    if (c0 && c1)
                    {
                        return FormulaCons.DATE;
                    }
                }
                return FormulaCons.INVALID;
                
            // 日期函数(日期加减)
            case FormulaCons.DATEOP:
                if (paramTypes.length == 3)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.TEXT || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.NUMBER || paramTypes[1] == FormulaCons.VARIANT;
                    boolean c2 = paramTypes[2] == FormulaCons.DATE || paramTypes[2] == FormulaCons.VARIANT;
                    if (c0 && c1 && c2)
                    {
                        return FormulaCons.DATE;
                    }
                }
                return FormulaCons.INVALID;

            // 日期函数(日期加减)
            case FormulaCons.TIMEDIF:
                if (paramTypes.length == 3)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.TEXT || paramTypes[0] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.DATE || paramTypes[1] == FormulaCons.VARIANT;
                    boolean c2 = paramTypes[2] == FormulaCons.DATE || paramTypes[2] == FormulaCons.VARIANT;
                    if (c0 && c1 && c2)
                    {
                        return FormulaCons.NUMBER;
                    }
                }
                return FormulaCons.INVALID;

            // 集合函数(此集合)
            case FormulaCons.GETSET:
                return getReturnTypeGetSet(paramTypes);
                
            // 类型转换及其它函数(转文本)
            case FormulaCons.TOTEXT:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.TEXT
                        || paramTypes[0] == FormulaCons.DATE || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.TEXT;
                    }
                }
                else if (paramTypes.length == 2)
                {
                    boolean c0 = paramTypes[0] == FormulaCons.DATE || paramTypes[1] == FormulaCons.VARIANT;
                    boolean c1 = paramTypes[1] == FormulaCons.TEXT || paramTypes[1] == FormulaCons.VARIANT;
                    if (c0 && c1)
                    {
                        return FormulaCons.TEXT;
                    }
                }
                return FormulaCons.INVALID;
                
            // 类型转换及其它函数(转日期)
            case FormulaCons.TODATE:
                if (paramTypes.length == 1)
                {
                    if (paramTypes[0] == FormulaCons.NUMBER || paramTypes[0] == FormulaCons.TEXT
                        || paramTypes[0] == FormulaCons.DATE || paramTypes[0] == FormulaCons.VARIANT)
                    {
                        return FormulaCons.DATE;
                    }
                }
                return FormulaCons.INVALID;
                
            // 类型转换及其它函数(空值替换)
            case FormulaCons.REPLACENULL:
                return getReturnTypeReplaceNull(paramTypes);
                
            // 类型转换及其它函数(条件取值)
            case FormulaCons.IFELSE:
                return getReturnTypeIfElse(paramTypes);

            default:
                return FormulaCons.VARIANT;
        }
    }

    // 对此集合函数作参数检查，并返回结果数据类型
    private static short getReturnTypeGetSet(short[] paramTypes)
    {
        if (paramTypes.length > 0)
        {
            short matchType = paramTypes[0];
            for (int i = 1; i < paramTypes.length; i++)
            {
                if (matchType == FormulaCons.NUMBER)
                {
                    if (paramTypes[i] == FormulaCons.NUMBER || paramTypes[i] == FormulaCons.VARIANT)
                    {
                        continue;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else if (matchType == FormulaCons.TEXT)
                {
                    if (paramTypes[i] == FormulaCons.TEXT || paramTypes[i] == FormulaCons.DATE
                        || paramTypes[i] == FormulaCons.VARIANT)
                    {
                        continue;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else if (matchType == FormulaCons.DATE)
                {
                    if (paramTypes[i] == FormulaCons.DATE || paramTypes[i] == FormulaCons.VARIANT)
                    {
                        continue;
                    }
                    else if (paramTypes[i] == FormulaCons.TEXT)
                    {
                        matchType = FormulaCons.TEXT;
                        continue;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else if (matchType == FormulaCons.VARIANT)
                {
                    if (paramTypes[i] == FormulaCons.VARIANT)
                    {
                        continue;
                    }
                    else if (paramTypes[i] == FormulaCons.NUMBER || paramTypes[i] == FormulaCons.TEXT
                        || paramTypes[i] == FormulaCons.DATE)
                    {
                        matchType = paramTypes[i];
                        continue;
                    }
                    return FormulaCons.INVALID;
                }
                else
                {
                    return FormulaCons.INVALID;
                }
            } // switch 结束
            if (matchType == FormulaCons.NUMBER)
            {
                return FormulaCons.NUMBER_SET;
            }
            else if (matchType == FormulaCons.TEXT)
            {
                return FormulaCons.TEXT_SET;
            }
            else if (matchType == FormulaCons.DATE)
            {
                return FormulaCons.DATE_SET;
            }
            else
            {
                // matchType一定为VARIANT_SET
                return FormulaCons.VARIANT_SET;
            }
        }
        else
        {
            // 参数个数为零
            return FormulaCons.INVALID;
        }
    }
    
    // 对此空值替换函数作参数检查，并返回结果数据类型
    private static short getReturnTypeReplaceNull(short[] paramTypes)
    {
        if (paramTypes.length == 2)
        {
            short type0 = paramTypes[0];
            short type1 = paramTypes[1];
            if (type0 == FormulaCons.NUMBER)
            {
                if (type1 == FormulaCons.NUMBER || type1 == FormulaCons.VARIANT)
                {
                    return FormulaCons.NUMBER;
                }
            }
            else if (type0 == FormulaCons.TEXT)
            {
                if (type1 == FormulaCons.TEXT || type1 == FormulaCons.DATE || type1 == FormulaCons.VARIANT)
                {
                    return FormulaCons.TEXT;
                }
            }
            else if (type0 == FormulaCons.DATE)
            {
                if (type1 == FormulaCons.DATE || type1 == FormulaCons.VARIANT)
                {
                    return FormulaCons.DATE;
                }
            }
            else if (type0 == FormulaCons.VARIANT)
            {
                if (type1 == FormulaCons.NUMBER || type1 == FormulaCons.TEXT || type1 == FormulaCons.DATE || type1 == FormulaCons.VARIANT)
                {
                    return FormulaCons.VARIANT;
                }
            }
        }
        return FormulaCons.INVALID;
    }
    
    private static short getReturnTypeIfElse(short[] paramTypes)
    {
        if (paramTypes.length % 2 == 1 && paramTypes.length >= 3)
        {
            // 验证条件参数
            for (int i=0; i<paramTypes.length-1; i=i+2)
            {
                if (paramTypes[i] != FormulaCons.BOOLEAN)
                {
                    return FormulaCons.INVALID;
                }
            }
            
            // 验证取值参数
            short matchType = FormulaCons.VARIANT;
            for (int i=1; i<paramTypes.length; i=i+2)
            {
                short type = paramTypes[i];
                if (type == FormulaCons.VARIANT || type == matchType)
                {
                    continue;
                }
                else if (type == FormulaCons.NUMBER)
                {
                    if (matchType == FormulaCons.VARIANT)
                    {
                        matchType = type;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else if (type == FormulaCons.TEXT)
                {
                    if (matchType == FormulaCons.VARIANT || matchType == FormulaCons.DATE)
                    {
                        matchType = type;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else if (type == FormulaCons.DATE)
                {
                    if (matchType == FormulaCons.VARIANT)
                    {
                        matchType = type;
                    }
                    else if (matchType == FormulaCons.TEXT)
                    {
                        continue;
                    }
                    else
                    {
                        return FormulaCons.INVALID;
                    }
                }
                else
                {
                    return FormulaCons.INVALID;
                }
            }
            return matchType;
        }
        return FormulaCons.INVALID;
    }

    private static void initializeNameIdTable()
    {
        nameIdTable = new HashMap<String, Short>();
        
        for (int i=0; i < functionNameArrays.length; i++)
        {
            for (int j=0; j < functionNameArrays[i].length; j++)
            {
                nameIdTable.put(functionNameArrays[i][j], functionIdArrays[i][j]);
            }
        }
    }
    
    private static void initializeIdNameTable()
    {
        idNameTable = new HashMap<Short, String>();
        
        for (int i=0; i < functionIdArrays.length; i++)
        {
            for (int j=0; j < functionIdArrays[i].length; j++)
            {
                idNameTable.put(functionIdArrays[i][j], functionNameArrays[i][j]);
            }
        }
    }
    
    /**
     * 验证日期字符串是否是合法的日期
     * @param strDate
     * @return 若日期字符串合法返回true，否则返回false
     */
    public static boolean isDate(String strDate)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try
        {
            format.parse(strDate);
            return true;
        }
        catch (ParseException ex)
        {
            return false;
        }
    }
    
    /**
     * 日期字符串解析
     * 
     * @param strDate 日期字符串
     * @return double 日期long值
     */
    public static double parseDate(String strDate)
    {
        boolean timeFlag = false;
        Date date = null;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            date = format1.parse(strDate);
            timeFlag = true;
        }
        catch(ParseException exp)
        {
        }

        if (date == null)
        {
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                date = format2.parse(strDate);
            }
            catch(ParseException exp)
            {
            }
        }

        if (date == null)
        {
            return Double.NaN;
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        if (year < 0 || year > 9999)
        {
            return Double.NaN;
        }
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        double serial = getSerial(year, month, day);
        if (timeFlag)
        {
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            double ts = (hour + (minute + second / 60.0) / 60.0) / 24.0;
            serial += ts;
        }
        return serial;
    }

    /*
     * 通过年月日得到序列号 
     */
    public static int getSerial(int year, int month, int day)
    {
        if (year < 0 || year > 9999)
        {
            // error
            return 0;
        }

        if (year < 1900)
        {
            year += 1900;
        }

        int ser = 0;
        for (int i = 1900; i < year; i++)
        {
            ser += 365;
            ser += isLeapYear(i) ? 1 : 0;
        }
        daysOfMonth[1] = isLeapYear(year) ? 29 : 28;
        for (int i = 0; i < month - 1; i++)
        {
            ser += daysOfMonth[i];
        }

        // 加1为了1900-2-29
        ser += (day + 1);
        return ser;
    }

    /*
     * 闰年判断 
     */
    private static boolean isLeapYear(int year)
    {
        return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
    }

    /**
     * 转换model值为指定格式的日期字符串
     * 
     * @param value model值
     * @param formatStr 格式字符串
     * @return String 日期字符串
     */
    public static String convertValueToDateString(int value, String formatStr)
    {
        getDate(value);
        GregorianCalendar cal = new GregorianCalendar(sdate[0], sdate[1] - 1, sdate[2]);
        Date date = cal.getTime();
        return convertDateToDateString(date, formatStr);
    }

    /**
     * 转换model值为指定格式的日期字符串
     * 
     * @param value model值
     * @param formatStr 格式字符串
     * @return String 日期字符串
     */
    public static String convertValueToDateString(double value, String formatStr)
    {
        int serial = (int)value;
        getDate(serial);
        Calendar cal = new GregorianCalendar();
        double rd = value - serial;
        rd *= 24;
        int hourOfDay = (int)(rd);
        rd -= hourOfDay;
        rd *= 60;
        int minute = (int)rd;
        rd -= minute;
        rd *= 60;
        int second = (int)rd;
        cal.set(sdate[0], sdate[1] - 1, sdate[2], hourOfDay, minute, second);
        Date date = cal.getTime();
        return convertDateToDateString(date, formatStr);
    }

    /*
     * 转换日期对象为指定格式的日期字符串
     */
    public static String convertDateToDateString(Date date, String formatStr)
    {
        if (formatStr == null || formatStr.length() == 0)
        {
            return DateFormat.getDateInstance(1).format(date);
        }

        DateFormat df = new SimpleDateFormat(formatStr);
        return df.format(date);
    }

    /**
     * 将日期值转化为年月日
     * 
     * @param serial 日期值
     * @return int[] 年月日数组
     */
    public static int[] getDate(int serial)
    {
        int days_4 = 1461;
        int day_2000 = 36525;
        int days_400 = 146097;
        int year = 1900;
        int month = 0;
        int day = 0;
        // 先处理假设的两天（为了兼容，虚构两天日期1900-1-0和1900-2-29）
        if (serial == 0)
        {
            setDate(1900, 1, 0);
            return sdate;
        }
        else if (serial == 60)
        {
            setDate(1900, 2, 29);
            return sdate;
        }
        /**
         * 从 1900 年 1 月 1 日至 1903 年 12 月 31 日之间的四年没有闰年，但是是从系列数 2 开始。
         */
        else if (serial <= days_4)
        {
            if (serial > 60)
            {
                serial--;
            }
            year += serial / 365;
            serial = serial % 365;
        }
        /**
         * 在 2000 年 1 月 1 日之前，每四年的情况相同（虽然 1900 年不是闰年，但由于基数 为 2 ，因此可看作 1900
         * 年为闰年而基数为 1 ，结果是一样的）。
         */
        else if (serial <= day_2000)
        {
            year += (serial / days_4) * 4;
            serial = serial % days_4;
            /**
             * 在每四年之内，第一年是闰年，因此分两种情况处理： 在第一年之外 在第一年之内
             */
            if (serial >= 366)
            {
                /**
                 * 如果在第一年之外，先把第一年减掉，再减掉剩下的年，剩下的在一年之内。
                 */
                year++;
                serial -= 366;
                year += serial / 365;
                serial = serial % 365;
            }
        }
        /**
         * 在 2000 年 1 月 1 日之后，每 400 年的情况是一样的。
         */
        else
        {
            year = 2000;
            serial -= day_2000;
            year += (serial / days_400) * 400;
            serial = serial % days_400;
            /**
             * 在 400 年内，第一个 100 年与后三个 100 年情况是不一样的，因为第一个 100 年 的第一年是闰年而后三个 100
             * 年的第一年不是闰年。因此分两种情况处理： 在第一个 100 年之外 在第一个 100 年之内
             */
            if (serial > day_2000)
            {
                /**
                 * 如果在第一个 100 年之外，先把第一个 100 年减掉，再减掉剩下的 100 年，剩 下的在 100 年之内。
                 */
                year += 100;
                serial -= day_2000;
                year += (serial / (day_2000 - 1)) * 100;
                serial = serial % (day_2000 - 1);
            }
            /**
             * 现在在某个 100 年之内了。
             */
            if (year % 400 == 0)
            {
                /**
                 * 如果在第一个 100 年之内，每四年是一样的。第一年闰年后面三个平年。
                 */
                year += (serial / days_4) * 4;
                serial = serial % days_4;
                if (serial >= 366)
                {
                    year++;
                    serial -= 366;
                    year += serial / 365;
                    serial = serial % 365;
                }
            }
            else
            {
                /**
                 * 如果在后三个 100 年，第一个四年与后面的四年是不同的，因为第一个四年没有 闰年。因此也要分两种情况处理：
                 * 在第一个四年之外 在第一个四年之内
                 */
                if (serial > (days_4 - 1))
                {
                    /**
                     * 如果在第一个四年之外，先把第一个四年减掉，再减剩下的四年，剩下的在 四年之内。
                     */
                    year += 4;
                    serial -= days_4 - 1;
                    year += (serial / days_4) * 4;
                    serial = serial % days_4;
                    if (serial >= 366)
                    {
                        year++;
                        serial -= 366;
                        year += serial / 365;
                        serial = serial % 365;
                    }
                }
                else
                {
                    /**
                     * 如果在第一个四年之内，四年的情况是一样的。
                     */
                    year += serial / 365;
                    serial = serial % 365;
                }
            }
        }
        /**
         * 现在在一年之内了，先确定当前年是否闰年以决定二月的天数，再依次减掉每月的 天数即可得到相应的月份。
         */
        daysOfMonth[1] = isLeapYear(year) ? 29 : 28;
        while (serial > daysOfMonth[month])
        {
            serial = serial - daysOfMonth[month++];
        }
        /**
         * 实际月份的值比相应数组下标应大 1 。
         */
        month++;
        if (serial == 0)
        {
            /**
             * 如果天数变成 0 了，只有一种情况，那就是到年底了。而此时计算的结果是下一 年的 0 月 0 日，因此应做相应修正。
             */
            year--;
            month = 12;
            serial = 31;
        }
        /**
         * 最后剩下的为在当前月份的天数。
         */
        day = serial;
        setDate(year, month, day);
        return sdate;
    }

    /*
     * 共用一个数组
     */
    private static void setDate(int year, int month, int day)
    {
        sdate[0] = year;
        sdate[1] = month;
        sdate[2] = day;
    }

    /**
     * 设置闰月的天数
     * 
     * @param year 年份
     */
    public static void setMonthDays(int year)
    {
        daysOfMonth[1] = isLeapYear(year) ? 29 : 28;
    }

    // 共用数组
    private static int[] sdate = {1900, 1, 1};
    // 一年中每个月的天数(使用前，根据闰年设置二月份天数)
    public static int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
}