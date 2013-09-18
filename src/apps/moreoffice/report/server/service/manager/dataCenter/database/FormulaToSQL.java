package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.ArrayList;
import java.util.Stack;

import apps.moreoffice.report.commons.formula.RFormula;
import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.moreoffice.report.commons.formula.resource.FunctionResource;
import apps.moreoffice.report.commons.formula.token.FieldToken;
import apps.moreoffice.report.commons.formula.token.FunToken;
import apps.moreoffice.report.commons.formula.token.InputVarToken;
import apps.moreoffice.report.commons.formula.token.ObjectToken;
import apps.moreoffice.report.commons.formula.token.RToken;

/**
 * 把公式对象转换为SQL语句
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云) & 实习生76(魏强)
 * <p>
 * @日期:       2012-7-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class FormulaToSQL
{
    /**
     * 将公式转化为SQL查询语句中的条件
     * 
     * @param formula 公式对象
     * @param clearTableName 是否清空列表，重新添加
     * @return String SQL语句
     */
    public static String revertConditionToSQL(RFormula formula, boolean clearTableName)
    {
        // 检测formula对象是否为null
        if (formula == null)
        {
            return "";
        }
        
        // 重置isStatFunc
        isStatFunc = false;
        
        // 判断是否初始化tableAndFieldNames或清空tableAndFieldNames
        if (tableAndFieldNames == null)
        {
            tableAndFieldNames = new ArrayList<String[]>();
        }
        else if (clearTableName)
        {
            tableAndFieldNames.clear();
        }
        
        // 遍历RToken表
        RToken[] tokenArray = formula.getTokenArray();
        for (RToken token : tokenArray)
        {
            short type;
            revertBuffer.delete(0, revertBuffer.length());
            type = token.getType();
            if (token instanceof FunToken)
            {
                // 函数
                isStatFunc = (type > FormulaCons.SUM && type < FormulaCons.MINDATE);
                revertFunction(type, ((FunToken)token).getParamsCount());
            }
            else if (type == FormulaCons.AND || type == FormulaCons.OR
                || (type >= FormulaCons.PLUS && type <= FormulaCons.NOT_BELONG))
            {
                if (type == FormulaCons.CONCAT)
                {
                    revertFunction("CONCAT", 2);
                }
                else if (type == FormulaCons.POWER)
                {
                    revertFunction("POW", 2);
                }
                else
                {
                    String operator;
                    switch (type)
                    {
                    //算术算子
                        case FormulaCons.PLUS:
                            operator = " + ";
                            break;

                        case FormulaCons.MINUS:
                            operator = " - ";
                            break;

                        case FormulaCons.MULTIPLY:
                            operator = " * ";
                            break;

                        case FormulaCons.DIVIDE:
                            operator = " / ";
                            break;

                        // 比较运算
                        case FormulaCons.LESS:
                            operator = " < ";
                            break;

                        case FormulaCons.GREATER:
                            operator = " > ";
                            break;

                        case FormulaCons.LESS_EQUAL:
                            operator = " <= ";
                            break;

                        case FormulaCons.GREATER_EQUAL:
                            operator = " >= ";
                            break;

                        case FormulaCons.NOT_EQUAL:
                            operator = " <> ";
                            break;

                        case FormulaCons.EQUAL:
                            operator = " = ";
                            break;

                        // 扩展比较
                        case FormulaCons.LIKE:
                            operator = " LIKE ";
                            break;

                        case FormulaCons.BETWEEN:
                            operator = " BETWEEN ";
                            break;

                        case FormulaCons.BELONG:
                            operator = " IN ";
                            break;

                        case FormulaCons.NOT_BELONG:
                            operator = " NOT IN ";
                            break;

                        case FormulaCons.AND:
                            operator = " AND ";
                            break;

                        case FormulaCons.OR:
                            operator = " OR ";
                            break;

                        default:
                            continue;
                    } // switch结束
                    revertBinaryOperator(operator);
                } // 内层if else结束
            }
            else
            {
                switch (type)
                {
                    case FormulaCons.NOT:
                    {
                        String opt = revertTokenStringStack.pop();
                        if (opt.equals("NULL"))
                        {
                            revertBuffer.append("NULL");
                            break;
                        }
                        revertBuffer.append("(NOT ");
                        revertBuffer.append(opt);
                        revertBuffer.append(')');
                        break;
                    }
                        
                    case FormulaCons.HAS_VALUE:
                    {
                        String opt = revertTokenStringStack.pop();
                        if (opt.equals("NULL"))
                        {
                            revertBuffer.append("NULL");
                            break;
                        }
                        revertBuffer.append('(');
                        revertBuffer.append(opt);
                        revertBuffer.append(" IS NOT NULL)");
                        break;
                    }
                        
                    case FormulaCons.NULL_VALUE:
                    {
                        String opt = revertTokenStringStack.pop();
                        if (opt.equals("NULL"))
                        {
                            revertBuffer.append("NULL");
                            break;
                        }
                        revertBuffer.append('(');
                        revertBuffer.append(opt);
                        revertBuffer.append(" IS NULL)");
                        break;
                    }

                    case FormulaCons.NUMBER:
                        revertBuffer.append(String.valueOf(((ObjectToken)token).getValue()));
                        break;

                    case FormulaCons.TEXT:
                    case FormulaCons.DATE:
                        revertBuffer.append('\'');
                        revertBuffer.append((String)(((ObjectToken)token).getValue()));
                        revertBuffer.append('\'');
                        break;

                    case FormulaCons.FIELD_VAR:
                        FieldToken ft = (FieldToken)token;
                        String databaseName = ft.getDatabaseName();
                        String tableName = ft.getTableName();
                        String fieldName = ft.getFieldName();

                        addToTableNamesAndFields(tableName, fieldName);
                        if (databaseName != null)
                        {
                            revertBuffer.append('`');
                            revertBuffer.append(databaseName);
                            revertBuffer.append("`.");
                        }
                        revertBuffer.append('`');
                        revertBuffer.append(tableName);
                        revertBuffer.append("`.`");
                        revertBuffer.append(fieldName);
                        revertBuffer.append('`');
                        break;

                    case FormulaCons.INPUT_VAR:
                        revertBuffer.append("输入.");
                        InputVarToken vt = (InputVarToken)token;
                        revertBuffer.append(vt.getHint());
                        revertBuffer.append('[');
                        revertBuffer.append(vt.getDataType());
                        revertBuffer.append(']');
                        break;

                    case FormulaCons.NULL:
                        revertBuffer.append("NULL");
                        break;

                    default:
                        break;
                } // switch结束
            } // 外层if else结束
            // 发生这种情况，当然错误
            if (revertBuffer.length() == 0)
            {
                return null;
            }
            revertTokenStringStack.push(revertBuffer.toString());
        } //for循环结束

        revertBuffer.delete(0, revertBuffer.length());
        revertBuffer.append(revertTokenStringStack.pop());

        return revertBuffer.toString();
    }

    /**
     * 根据函数在条件上添加sql语句
     * 
     * @param condition
     * @param isStatFunc
     * @return
     */
    public static String dealWithCondition(String condition)
    {
        if (isStatFunc)
        {
            return statFunction(condition);
        }
        return notStatFunction() + condition;
    }

    /**
     * 通过函数ID与参数个数转换函数
     */
    private static void revertFunction(int funID, int params)
    {
        switch (funID)
        {
            case FormulaCons.THISYEAR:
                revertFunctionThisYear();
                return;
            case FormulaCons.LASTYEAR:
                revertFunctionLastYear();
                return;
            case FormulaCons.THISQUARTER:
                revertFunctionThisQuarter();
                return;
            case FormulaCons.LASTQUARTER:
                revertFunctionLastQuarter();
                return;
            case FormulaCons.THISMONTH:
                revertFunctionThisMonth();
                return;
            case FormulaCons.LASTMONTH:
                revertFunctionLastMonth();
                return;
            case FormulaCons.YEARSTART:
                revertFunctionYearStart();
                return;
            case FormulaCons.YEAREND:
                revertFunctionYearEnd();
                return;
            case FormulaCons.QUARTERSTART:
                revertFunctionQuarterStart();
                return;
            case FormulaCons.QUARTEREND:
                revertFunctionQuarterEnd();
                return;
            case FormulaCons.MONTHSTART:
                revertFunctionMonthStart();
                return;
            case FormulaCons.MONTHEND:
                revertFunctionMonthEnd();
                return;
            case FormulaCons.DATEOP:
                revertFunctionDateOp();
                return;
            case FormulaCons.TIMEDIF:
                revertFunctionTimeDif();
                return;
            case FormulaCons.TOTEXT:
                revertFunctionToText(params);
                return;
            case FormulaCons.TODATE:
                revertFunctionToDate();
                return;
            case FormulaCons.REPLACENULL:
                revertFunctionReplaceNull();
                return;
            case FormulaCons.IFELSE:
                revertFunctionIfElse(params);
                return;
        }
        
        String name = getFunctionName(funID);
        revertBuffer.append(name);
        revertBuffer.append('(');
        if (funID == FormulaCons.COUNTUNIQUE)
        {
            revertBuffer.append("DISTINCT ");
        }


        int pos = revertBuffer.length();
        for (int i = 0; i < params; i++)
        {
            if (!revertTokenStringStack.isEmpty())
            {
                String par = revertTokenStringStack.pop();
                if (par.equals("NULL"))
                {
                    revertBuffer.replace(0, revertBuffer.length(), "NULL");
                    return;
                }
                revertBuffer.insert(pos, ',');
                revertBuffer.insert(pos, par);
            }
        }
        if (pos != revertBuffer.length())
        {
            //至少有一个非空参数，去掉最后一个逗号
            revertBuffer.deleteCharAt(revertBuffer.length() - 1);
        }
        revertBuffer.append(')');
    }
    
    /**
     * 通过函数名与参数个数转换函数
     */
    private static void revertFunction(String name, int params)
    {
        revertBuffer.append(name);
        revertBuffer.append('(');

        int pos = revertBuffer.length();
        for (int i = 0; i < params; i++)
        {
            if (!revertTokenStringStack.isEmpty())
            {
                String par = revertTokenStringStack.pop();
                if (par.equals("NULL"))
                {
                    revertBuffer.replace(0, revertBuffer.length(), "NULL");
                    return;
                }
                revertBuffer.insert(pos, ',');
                revertBuffer.insert(pos, par);
            }
        }
        if (pos != revertBuffer.length())
        {
            // 至少有一个非空参数，去掉最后一个逗号
            revertBuffer.deleteCharAt(revertBuffer.length() - 1);
        }
        revertBuffer.append(')');
    }
    
    /**
     * 转换此年函数
     */
    private static void revertFunctionThisYear()
    {
        String year = revertTokenStringStack.pop();
        if (year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str0 = String.format("CONCAT(%s, '-01-01')", year);
        String str1 = " AND ";
        String str2 = String.format("CONCAT(%s, '-12-31')", year);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
    }

    /**
     * 转换去年函数
     */
    private static void revertFunctionLastYear()
    {
        String year = revertTokenStringStack.pop();
        if (year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str0 = String.format("DATE(CONCAT(%s-1, '-01-01'))", year);
        String str1 = " AND ";
        String str2 = String.format("DATE(CONCAT(%s-1, '-12-31'))", year);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
    }
    
    /**
     * 转换此季函数
     */
    private static void revertFunctionThisQuarter()
    {
        String quarter = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (quarter.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str1 = String.format("DATE(CONCAT(%s, '-', %s*3-2, '-01'))", year, quarter);
        String str2 = String.format(" AND ");
        String str3 = String.format("LAST_DAY(CONCAT(%s, '-', %s*3, '-01'))", year, quarter);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
        revertBuffer.append(str3);
    }

    /**
     * 转换上季函数
     */
    private static void revertFunctionLastQuarter()
    {
        String quarter = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (quarter.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str0 = String.format("DATE(CONCAT(%1$s-(%2$s=1), '-', (%2$s-1+(%2$s=1)*4)*3-2, '-01'))", year, quarter);
        String str1 = String.format(" AND ");
        String str2 = String.format("LAST_DAY(CONCAT(%1$s-(%2$s=1), '-', (%2$s-1+(%2$s=1)*4)*3, '-01'))", year, quarter);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
    } 
    
    /**
     * 转换此月函数
     */
    private static void revertFunctionThisMonth()
    {
        String month = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (month.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str0 = String.format("DATE(CONCAT(%s, '-', %s, '-01'))", year, month);
        String str1 = String.format(" AND ");
        String str2 = String.format("LAST_DAY(CONCAT(%s, '-', %s, '-01'))", year, month);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
    }
    
    /**
     * 转换上月函数
     */
    private static void revertFunctionLastMonth()
    {
        String month = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (month.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str0 = String.format("DATE(CONCAT(%1$s-(%2$s=1), '-', %2$s-1+(%2$s=1)*12, '-01'))", year, month);
        String str1 = String.format(" AND ");
        String str2 = String.format("LAST_DAY(CONCAT(%1$s-(%2$s=1), '-', %2$s-1+(%2$s=1)*12, '-01'))", year, month);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
        revertBuffer.append(str2);
    }
    
    /**
     * 转换年初函数
     */
    private static void revertFunctionYearStart()
    {
        String year = revertTokenStringStack.pop();
        if (year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("DATE(CONCAT(%s, '-01-01'))", year);
        revertBuffer.append(str);
    }
    
    /**
     * 转换年末函数
     */
    private static void revertFunctionYearEnd()
    {
        String year = revertTokenStringStack.pop();
        if (year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("DATE(CONCAT(%s, '-12-31'))", year);
        revertBuffer.append(str);
    }
    
    /**
     * 转换季初函数
     */
    private static void revertFunctionQuarterStart()
    {
        String quarter = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (quarter.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("DATE(CONCAT(%s, '-', %s*3-2, '-01'))", year, quarter);
        revertBuffer.append(str);
    }
    
    /**
     * 转换季末函数
     */
    private static void revertFunctionQuarterEnd()
    {
        String quarter = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (quarter.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("LAST_DAY(CONCAT(%s, '-', %s*3, '-01'))", year, quarter);
        revertBuffer.append(str);
    }
    
    /**
     * 转换月初函数
     */
    private static void revertFunctionMonthStart()
    {
        String month = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (month.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("DATE(CONCAT(%s, '-', %s, '-01'))", year, month);
        revertBuffer.append(str);
    }
    
    /**
     * 转换月末函数
     */
    private static void revertFunctionMonthEnd()
    {
        String month = revertTokenStringStack.pop();
        String year = revertTokenStringStack.pop();
        if (month.equals("NULL") || year.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("LAST_DAY(CONCAT(%s, '-', %s, '-01'))", year, month);
        revertBuffer.append(str);
    }
    
    /**
     * 转换日期加减函数
     */
    private static void revertFunctionDateOp()
    {
        String date = revertTokenStringStack.pop();
        String num = revertTokenStringStack.pop();
        String unit = revertTokenStringStack.pop();
        if (date.equals("NULL") || num.equals("NULL") || unit.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String strYear = String.format("DATE_ADD(%s, INTERVAL %s YEAR)", date, num);
        String strQuarter = String.format("DATE_ADD(%s, INTERVAL %s QUARTER)", date, num);
        String strMonth = String.format("DATE_ADD(%s, INTERVAL %s MONTH)", date, num);
        String strWeek = String.format("DATE_ADD(%s, INTERVAL %s WEEK)", date, num);
        String strDay = String.format("DATE_ADD(%s, INTERVAL %s DAY)", date, num);
        String strHour = String.format("DATE(DATE_ADD(%s, INTERVAL %s HOUR))", date, num);
        String strMinute = String.format("DATE(DATE_ADD(%s, INTERVAL %s MINUTE))", date, num);
        String strSecond = String.format("DATE(DATE_ADD(%s, INTERVAL %s SECOND))", date, num);
        String str0 = String.format("(CASE %s WHEN 'yy' THEN %s WHEN 'qq' THEN %s WHEN 'mm' THEN %s", unit, strYear, strQuarter, strMonth);
        String str1 = String.format(" WHEN 'hh' THEN %s WHEN 'ww' THEN %s WHEN 'mi' THEN %s WHEN 'ss' THEN %s ELSE %s END)", strHour, strWeek, strMinute, strSecond, strDay);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
    }
    
    /**
     * 转换间隔时间函数
     */
    private static void revertFunctionTimeDif()
    {
        String dateEnd = revertTokenStringStack.pop();
        String dateStart = revertTokenStringStack.pop();
        String unit = revertTokenStringStack.pop();
        if (dateEnd.equals("NULL") || dateStart.equals("NULL") || unit.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String strYear = String.format("(YEAR(%2$s)-YEAR(%1$s) - (RIGHT(%2$s, 5)<RIGHT(%1$s, 5)))", dateStart, dateEnd);
        String strQuarter = String.format("((YEAR(%2$s)-YEAR(%1$s))*4 + (MONTH(%2$s)-MONTH(%1$s)-(DAYOFMONTH(%2$s)<DAYOFMONTH(%1$s))) DIV 3)", dateStart, dateEnd);
        String strMonth = String.format("((YEAR(%2$s)-YEAR(%1$s))*12 + MONTH(%2$s) - MONTH(%1$s) - (DAYOFMONTH(%2$s)<DAYOFMONTH(%1$s)))", dateStart, dateEnd);
        String strWeek = String.format("(DATEDIFF(%2$s, %1$s) DIV 7)", dateStart, dateEnd);
        String strDay = String.format("DATEDIFF(%2$s, %1$s)", dateStart, dateEnd);
        String strHour = String.format("(DATEDIFF(%2$s, %1$s) * 24)", dateStart, dateEnd);
        String strMinute = String.format("(DATEDIFF(%2$s, %1$s) * 1440)", dateStart, dateEnd);
        String strSecond = String.format("(DATEDIFF(%2$s, %1$s) * 86400)", dateStart, dateEnd);
        String str0 = String.format("(CASE %s WHEN 'yy' THEN %s WHEN 'qq' THEN %s WHEN 'mm' THEN %s", unit, strYear, strQuarter, strMonth);
        String str1 = String.format(" WHEN 'hh' THEN %s WHEN 'ww' THEN %s WHEN 'mi' THEN %s WHEN 'ss' THEN %s ELSE %s END)", strHour, strWeek, strMinute, strSecond, strDay);
        revertBuffer.append(str0);
        revertBuffer.append(str1);
    }
    
    /**
     * 转换转文本函数
     */
    private static void revertFunctionToText(int params)
    {
        if (params == 1)
        {
            String val = revertTokenStringStack.pop();
            String str = String.format("CAST(%s AS CHAR)", val);
            if (val.equals("NULL"))
            {
                revertBuffer.append("NULL");
                return;
            }
            revertBuffer.append(str);
        }
        else
        {
            // params == 2
            String fmt = revertTokenStringStack.pop();
            String val = revertTokenStringStack.pop();
            if (fmt.equals("NULL") || val.equals("NULL"))
            {
                revertBuffer.append("NULL");
                return;
            }
            String str = String.format("DATE_FORMAT(%s, %s)", val, fmt);
            revertBuffer.append(str);
        }
    }

    /**
     * 转换转日期函数
     */
    private static void revertFunctionToDate()
    {
        String val = revertTokenStringStack.pop();
        if (val.equals("NULL"))
        {
            revertBuffer.append("NULL");
            return;
        }
        String str = String.format("CAST(%s AS DATE)", val);
        revertBuffer.append(str);
    }
    
   /**
    * 转换替换空值函数
    */
   private static void revertFunctionReplaceNull()
   {
       String val = revertTokenStringStack.pop();
       String field = revertTokenStringStack.pop();
       if (val.equals("NULL") || field.equals("NULL"))
       {
           revertBuffer.append("NULL");
           return;
       }
       String str = String.format("IFNULL(%s, %s)", field, val);
       revertBuffer.append(str);
   }
   
   /**
    * 转换条件取值函数
    */
   private static void revertFunctionIfElse(int params)
   {
       // 取出其他值
       String otherValue = revertTokenStringStack.pop();
       if (otherValue.equals("NULL"))
       {
           revertBuffer.append("NULL");
           return;
       }
       
       // 取出条件、值
       int pairCount = params / 2;
       String[] conditions = new String[pairCount];
       String[] values = new String[pairCount];
       for (int i=pairCount-1; i>=0; i--)
       {
           values[i] = revertTokenStringStack.pop();
           conditions[i] = revertTokenStringStack.pop();
           if (values[i].equals("NULL") || conditions[i].equals("NULL"))
           {
               revertBuffer.append("NULL");
               return;
           }
       }
       
       // 转换条件、值
       revertBuffer.append("(CASE ");
       for (int i=0; i<pairCount; i++)
       {
           String str = String.format("WHEN %s THEN %s ", conditions[i], values[i]);
           revertBuffer.append(str);
       }
       
       // 转换其他值
       String str = String.format("ELSE %s END)", otherValue);
       revertBuffer.append(str);
   }
    
    
   /**
     * 根据ID返回公式名称
     */
    private static String getFunctionName(int FunID)
    {
        final short[][] functionIdArrays = {STATS_FUNCTION_ID_ARRAY, 
            MATH_FUNCTION_ID_ARRAY, STRING_FUNCTION_ID_ARRAY, 
            DATETIME_FUNCTION_ID_ARRAY, SET_FUNCTION_ID_ARRAY,
            TYPECVT_OTHER_FUNCTION_ID_ARRAY};
        
        final String[][] functionNameArrays = {STATS_SQLFUNCTION_NAME_ARRAY,
            MATH_SQLFUNCTION_NAME_ARRAY, STRING_SQLFUNCTION_NAME_ARRAY,
            DATETIME_SQLFUNCTION_NAME_ARRAY, SET_SQLFUNCTION_NAME_ARRAY,
            TYPECVT_OTHER_SQLFUNCTION_NAME_ARRAY};
        
        for (int i = 0; i < functionIdArrays.length; i++)
        {
            for (int j = 0; j < functionIdArrays[i].length; j++)
            {
                if (FunID == functionIdArrays[i][j])
                {
                    return functionNameArrays[i][j];
                }
            }
        }

        return "";
    }

    /**
     * 转换操作符表达式
     */
    private static void revertBinaryOperator(String operator)
    {
        if (revertTokenStringStack.isEmpty())
        {
            return;
        }
        String rightString = revertTokenStringStack.pop();
        if (revertTokenStringStack.isEmpty())
        {
            return;
        }
        String leftString = revertTokenStringStack.pop();
        if (operator.equals(" AND ") || operator.equals(" OR "))
        {
            if (rightString.equals("NULL"))
            {
                rightString = "TRUE";
            }
            if (leftString.equals("NULL"))
            {
                leftString = "TRUE";
            }
        }
        else if (rightString.equals("NULL") || leftString.equals("NULL"))
        {
            
            revertBuffer.append("NULL");
            return;
        }
        revertBuffer.append('(');
        revertBuffer.append(leftString);
        revertBuffer.append(operator);
        revertBuffer.append(rightString);
        revertBuffer.append(')');
    }

    /**
     * 操作符产生的函数 //注释不正确
     */
    private static String statFunction(String funcStr)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append(funcStr);

        ArrayList<String> tableNames = getTableNames();
        if (tableNames != null && tableNames.size() > 0)
        {
            for (int i = tableNames.size() - 1; i >= 0; i--)
            {
                sb.append(tableNames.get(i));
                if (i != 0)
                {
                    sb.append(", ");
                }
            }
        }

        return sb.toString();
    }

    /**
     * 函数 //注释不正确
     */
    private static String notStatFunction()
    {
        StringBuffer sb = new StringBuffer();
        ArrayList<String> tableNames = getTableNames();
        if (tableNames == null || tableNames.isEmpty())
        {
            sb.append("select ");
        }
        else
        {
            sb.append("select * from ");
            for (int i = tableNames.size() - 1; i >= 0; i--)
            {
                sb.append(tableNames.get(i));
                if (i != 0)
                {
                    sb.append(", ");
                }
            }
            sb.append(" where ");
        }

        return sb.toString();
    }

    /**
     * 收集公式中用到的表名和字段名
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     */
    public static void addToTableNamesAndFields(String tableName, String fieldName)
    {
        if (tableName == null || tableName.equals("") || fieldName == null || fieldName.equals(""))
        {
            return;
        }

        boolean has = false;
        String[] temp;
        for (int i = tableAndFieldNames.size() - 1; i >= 0; i--)
        {
            temp = tableAndFieldNames.get(i);
            if (temp[0].equals(tableName) && temp[1].equals(fieldName))
            {
                has = true;
                break;
            }
        }
        if (!has)
        {
            tableAndFieldNames.add(new String[]{tableName, fieldName});
        }
    }

    /**
     * 获取处理公式中出现的所有表名(没有重复)
     * 必须是revertConditionToSQL处理过后再调此方法。
     * 
     * @return ArrayList<String> 表名列表
     */
    public static ArrayList<String> getTableNames()
    {
        if (tableAndFieldNames == null || tableAndFieldNames.size() == 0)
        {
            return null;
        }

        ArrayList<String> tableNames = new ArrayList<String>();
        for (String[] names : tableAndFieldNames)
        {
            if (!tableNames.contains(names[0]))
            {
                tableNames.add(names[0]);
            }
        }

        return tableNames;
    }

    
    private static boolean isStatFunc = false;
    // 表和字段名列表
    private static ArrayList<String[]> tableAndFieldNames;
    // sql语句
    private static StringBuilder revertBuffer = new StringBuilder();
    // 堆栈
    private static Stack<String> revertTokenStringStack = new Stack<String>();
    
    // 统计函数对应ID
    public static final short[] STATS_FUNCTION_ID_ARRAY = FunctionResource.STATS_FUNCTION_ID_ARRAY;
    
    // 统计函数对应SQL函数名称
    public static final String[] STATS_SQLFUNCTION_NAME_ARRAY = {
        "SUM", "AVG", "COUNT", "COUNT", "MAX", "MIN", "MAX", "MIN"
    };
    
    // 数学函数对应ID
    public static final short[] MATH_FUNCTION_ID_ARRAY = FunctionResource.MATH_FUNCTION_ID_ARRAY;
    
    // 数学函数对应SQL函数名称
    public static final String[] MATH_SQLFUNCTION_NAME_ARRAY = {
        "LOG10", "LN", "EXP", "POW", "ROUND"
    };
    
    // 文本函数对应ID
    public static final short[] STRING_FUNCTION_ID_ARRAY = FunctionResource.STRING_FUNCTION_ID_ARRAY;
    
    // 文本函数对应SQL函数名称
    public static final String[] STRING_SQLFUNCTION_NAME_ARRAY = {
        "SUBSTRING", "LENGTH", "UPPER"
    };
    
    // 日期与时间函数对应ID
    public static final short[] DATETIME_FUNCTION_ID_ARRAY = FunctionResource.DATETIME_FUNCTION_ID_ARRAY;
    
    // 日期与时间函数对应SQL函数名称
    public static final String[] DATETIME_SQLFUNCTION_NAME_ARRAY = {
        "YEAR", "QUARTER", "MONTH", "DAYOFMONTH", "WEEKDAY",
        "", "", "", "", "", "", // 此年、上年、此季、上季、此月、上月
        "", "", "", "", "", "", // 年初、年末、季初、季末、月初、月末
        //"", "", "", "", "", "", // 此结算年、上结算年、此结算季、上结算季、此结算月、上结算月
        //"", "", "", "", "", "", // 结算年初、结算年末、结算季初、结算季末、结算月初、结算月末
        "DATE_ADD", "TIMEDIFF", // 日期加减、时间间隔
    };
    
    // 集合函数对应ID
    public static final short[] SET_FUNCTION_ID_ARRAY = FunctionResource.SET_FUNCTION_ID_ARRAY;
    
    // 集合函数对应SQL函数名称
    public static final String[] SET_SQLFUNCTION_NAME_ARRAY = { 
        "" // 此集合
    };
    
    // 类型转换及其它函数对应ID
    public static final short[] TYPECVT_OTHER_FUNCTION_ID_ARRAY = FunctionResource.TYPECVT_OTHER_FUNCTION_ID_ARRAY;
    
    // 类型转换函数及其它函数对应SQL函数名称
    public static final String[] TYPECVT_OTHER_SQLFUNCTION_NAME_ARRAY = {
        "CAST", "CAST", "", "" // 转文字、转日期、空值转换、条件取值 
    };
}