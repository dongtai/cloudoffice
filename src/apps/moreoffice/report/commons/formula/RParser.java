package apps.moreoffice.report.commons.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import apps.moreoffice.report.commons.domain.constants.DataTypeCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.moreoffice.report.commons.formula.exception.FormulaParsingException;
import apps.moreoffice.report.commons.formula.resource.FormulaResource;
import apps.moreoffice.report.commons.formula.token.FieldToken;
import apps.moreoffice.report.commons.formula.token.FunToken;
import apps.moreoffice.report.commons.formula.token.InputVarToken;
import apps.moreoffice.report.commons.formula.token.LexToken;
import apps.moreoffice.report.commons.formula.token.ObjectToken;
import apps.moreoffice.report.commons.formula.token.RToken;
import apps.moreoffice.report.commons.formula.token.SysVarToken;
import apps.moreoffice.report.commons.formula.token.TableToken;

/**
 * 公式语法分析器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚) & 实习生76(魏强) 
 * <p>
 * @维护者:      实习生76(魏强) 
 * <p>
 * @日期:       2012-7-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class RParser
{
	private static RParser parser = new RParser();
	
    // LexToken表, RToken表
    private LinkedList<LexToken> lexTokenQueue;
    private ArrayList<RToken> rTokenList = new ArrayList<RToken>();
    
    // 当前LexToken, 当前LexToken类型
    private LexToken curLexToken; 
    private int curLexTokenType;
    
    // 系统变量信息, 数据表信息
    private HashMap<String, Short> sysVarInfo;
    private HashMap<String, Short> fieldVarInfo;
    
    // 语法检查标记
    private boolean checkFlag;
    
    // 表达式类型(表达式结果的数据类型)
    private short formulaType;

    /**
     * 语法分析对外接口
     * 当fieldVarInfo为null时，不进行语法检查
     * 当fieldVarInfo不为null是，进行语法检查
     * 
     * @param lexTokenQueue
     * @param fieldVarInfo 数据表信息, (库名.)表名.列名 -> 数据表数据类型(非公式数据类型)
     * @return RFormula对象
     * @throws FormulaParsingException
     */
    public static RFormula syntaxAnalyze(LinkedList<LexToken> lexTokenQueue, HashMap<String, Short> fieldVarInfo) throws FormulaParsingException
    {
        return parser.doSyntaxAnalyze(lexTokenQueue, fieldVarInfo);
    }

    /**
     * 进行语法分析
     * @param lexTokenQueue
     * @param fieldVarInfo 数据表信息, (库名.)表名.列名 -> 数据表数据类型(非公式数据类型)
     * @return RFormula对象
     * @throws FormulaParsingException
     */
    private RFormula doSyntaxAnalyze(LinkedList<LexToken> lexTokenQueue, HashMap<String, Short> fieldVarInfo) throws FormulaParsingException
    {
        // 检测输入的LexToken表
        if (lexTokenQueue == null || lexTokenQueue.isEmpty())
        {
            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_001);
        }
        
        // 重置语法分析环境
        this.lexTokenQueue = lexTokenQueue;
        rTokenList.clear();
        curLexToken = null;
        curLexTokenType = FormulaCons.INVALID;
        formulaType = FormulaCons.INVALID;
        if (sysVarInfo == null)
        {
            initializeSysVarInfo();
        }
        if (fieldVarInfo == null)
        {
            checkFlag = false;
            this.fieldVarInfo = null;
        }
        else
        {
            checkFlag = true;
            this.fieldVarInfo = convertDataType(fieldVarInfo);           
        }
        
        // 自顶向下进行语法分析
        nextLexToken();
        formulaType = processExpression();
        
        // 如果解析后队列不空, 报错。
        if (curLexToken != null || !(lexTokenQueue == null || lexTokenQueue.isEmpty()))
        {
            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_002);
        }
        
        // 如果解析后结果非最终数据类型, 报错
        if (formulaType != FormulaCons.NULL && formulaType != FormulaCons.NUMBER
            && formulaType != FormulaCons.TEXT && formulaType != FormulaCons.DATE
            && formulaType != FormulaCons.BOOLEAN && formulaType != FormulaCons.VARIANT)
        {
            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_012);
        }

        // 返回RFormula对象
        int size = rTokenList.size();
        RFormula formula = new RFormula();
        formula.setType(formulaType);
        formula.setTokenArray(rTokenList.toArray(new RToken[size]));
        rTokenList.clear();
        return formula;
    }

    /**
     * 从LexToken队列中取出一个LexToken
     */
    private LexToken nextLexToken()
    {
        if (lexTokenQueue == null || lexTokenQueue.isEmpty())
        {
            curLexToken = null;
            curLexTokenType = FormulaCons.INVALID;
            return null;
        }

        curLexToken = lexTokenQueue.poll();
        curLexTokenType = curLexToken.getType();
        return curLexToken;
    }

    /**
     * 添加一个RToken入RToken表
     */
    private void appendRToken(RToken rtk)
    {
        rTokenList.add(rtk);
    }
    
    /**
     * 处理Expression, 生成逆波兰形式的表达式(即后缀形式)
     */
    private short processExpression() throws FormulaParsingException
    {
        return processExpressionOr();
    }
    
    /**
     * 处理ExpressionOr(逻辑或)
     */
    private short processExpressionOr() throws FormulaParsingException
    {
        if (checkFlag)
        {
            short type = processExpressionAnd();
            while (curLexTokenType == FormulaCons.OR)
            {
                if (type != FormulaCons.BOOLEAN)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                nextLexToken();
                type = processExpressionAnd();
                if (type != FormulaCons.BOOLEAN)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken(RToken.OR_TOKEN);
            }
            return type;
        }
        else
        {
            short type = processExpressionAnd();
            while (curLexTokenType == FormulaCons.OR)
            {
                nextLexToken();
                type = processExpressionAnd();
                appendRToken(RToken.OR_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理ExpressionAnd(逻辑与)
     */
    private short processExpressionAnd() throws FormulaParsingException
    {
        if (checkFlag)
        {
            short type = processExpressionNot();
            while (curLexTokenType == FormulaCons.AND)
            {
                if (type != FormulaCons.BOOLEAN)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                nextLexToken();
                type = processExpressionNot();
                if (type != FormulaCons.BOOLEAN)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken(RToken.AND_TOKEN);
            }
            return type;
        }
        else
        {
            short type = processExpressionNot();
            while (curLexTokenType == FormulaCons.AND)
            {
                nextLexToken();
                processExpressionNot();
                appendRToken(RToken.AND_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理ExpressionNot(逻辑非)
     */
    private short processExpressionNot() throws FormulaParsingException
    {
        if (checkFlag)
        {
            if (curLexTokenType == FormulaCons.NOT)
            {
                nextLexToken();
                short type = processExpressionNot();
                if (type != FormulaCons.BOOLEAN)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken(RToken.NOT_TOKEN);
                return FormulaCons.BOOLEAN;
            }
            return processExpressionCompare();
        }
        else
        {
            if (curLexTokenType == FormulaCons.NOT)
            {
                nextLexToken();
                processExpressionNot();
                appendRToken(RToken.NOT_TOKEN);
                return FormulaCons.BOOLEAN;
            }
            return processExpressionCompare();
        }
    }

    /**
     * 处理ExpressionCompare(比较运算)
     */
    private short processExpressionCompare() throws FormulaParsingException
    {
        short type = processExpressionConcat();
        
        // 有值、无值
        if (curLexTokenType == FormulaCons.HAS_VALUE || curLexTokenType == FormulaCons.NULL_VALUE)
        {
            if (checkFlag)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.TEXT && type != FormulaCons.DATE
                    && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            int operator = curLexTokenType;
            nextLexToken();
            appendRToken(RToken.COMPARE_TOKENS[operator - FormulaCons.LESS]);
            return FormulaCons.BOOLEAN;
        }
    
        // 等于、不等于、小于、大于、小于或等于、大于或等于
        if (curLexTokenType == FormulaCons.EQUAL || curLexTokenType == FormulaCons.NOT_EQUAL
            || curLexTokenType == FormulaCons.LESS || curLexTokenType == FormulaCons.GREATER
            || curLexTokenType == FormulaCons.LESS_EQUAL
            || curLexTokenType == FormulaCons.GREATER_EQUAL)
        {
            if (checkFlag)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.TEXT && type != FormulaCons.DATE
                    && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            int operator = curLexTokenType;
            nextLexToken();
            short type2 = processExpressionConcat();
            if (checkFlag)
            {
                if (type == FormulaCons.NUMBER)
                {
                    if (type2 != FormulaCons.NUMBER && type2 != FormulaCons.VARIANT)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else if (type == FormulaCons.TEXT)
                {
                    if (type2 != FormulaCons.TEXT && type2 != FormulaCons.DATE
                        && type2 != FormulaCons.VARIANT)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else if (type == FormulaCons.DATE)
                {
                    if (type2 != FormulaCons.DATE && type2 != FormulaCons.TEXT
                        && type2 != FormulaCons.VARIANT)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else
                {
                    // type == FormulaCons.VARIANT
                        if (type2 != FormulaCons.NUMBER && type2 != FormulaCons.TEXT
                        && type2 != FormulaCons.DATE && type2 != FormulaCons.VARIANT)
                        {
                            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                        }
                }
            }
            appendRToken(RToken.COMPARE_TOKENS[operator - FormulaCons.LESS]);
            return FormulaCons.BOOLEAN;
        }
 
        // 形如
        if (curLexTokenType == FormulaCons.LIKE)
        {
            int operator = curLexTokenType;
            if (checkFlag)
            {
                if (type != FormulaCons.TEXT && type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            nextLexToken();
            type = processExpressionConcat();
            if (checkFlag)
            {
                if (type != FormulaCons.TEXT && type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            appendRToken(RToken.COMPARE_TOKENS[operator - FormulaCons.LESS]);
            return FormulaCons.BOOLEAN;
        }
            
        // 介于
        if (curLexTokenType == FormulaCons.BETWEEN)
        {
            int operator = curLexTokenType;
            if (checkFlag)
            {
                if (type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            nextLexToken();
            type = processExpressionConcat();
            if (checkFlag)
            {
                if (type != FormulaCons.DATE_RANGE)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            appendRToken(RToken.COMPARE_TOKENS[operator - FormulaCons.LESS]);
            return FormulaCons.BOOLEAN;
        }

        // 属于、不属于
        if (curLexTokenType == FormulaCons.BELONG || curLexTokenType == FormulaCons.NOT_BELONG)
        {
            int operator = curLexTokenType;
            if (checkFlag)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.TEXT
                    && type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
            }
            nextLexToken();
            short type2 = processExpressionConcat();
            if (checkFlag)
            {
                if (type == FormulaCons.NUMBER)
                {
                    if (type2 != FormulaCons.NUMBER_SET && type2 != FormulaCons.VARIANT_SET)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else if (type == FormulaCons.TEXT)
                {
                    if (type2 != FormulaCons.TEXT_SET && type2 != FormulaCons.VARIANT_SET)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else if (type == FormulaCons.DATE)
                {
                    if (type2 != FormulaCons.DATE_SET && type2 != FormulaCons.TEXT_SET
                        && type2 != FormulaCons.VARIANT_SET)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
                else
                {
                    // type一定为DataType.VARIANT
                    if (type2 != FormulaCons.NUMBER_SET && type2 != FormulaCons.TEXT_SET
                        && type2 != FormulaCons.DATE_SET && type2 != FormulaCons.VARIANT_SET)
                    {
                        throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                    }
                }
            }
            appendRToken(RToken.COMPARE_TOKENS[operator - FormulaCons.LESS]);
            return FormulaCons.BOOLEAN;
        }
            
        return type;
    }

    /**
     * 处理ExpressionConcat(字符串连接)
     */
    private short processExpressionConcat() throws FormulaParsingException
    {
        short type = processExpressionPlusMinus();
        if (checkFlag)
        {
            while (curLexTokenType == FormulaCons.CONCAT)
            {
                if (type != FormulaCons.TEXT && type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                nextLexToken();
                type = processExpressionPlusMinus();
                if (type != FormulaCons.TEXT && type != FormulaCons.DATE && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken(RToken.CONCAT_TOKEN);
            }
            return type;
        }
        else
        {
            while (curLexTokenType == FormulaCons.CONCAT)
            {
                nextLexToken();
                processExpressionPlusMinus();
                appendRToken(RToken.CONCAT_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理ExpressionPlusMinus(加减)
     */
    private short processExpressionPlusMinus() throws FormulaParsingException
    {
        // 乘除比加减高一个优先级
        short type = processExpressionMultiplyDivide();
        if (checkFlag)
        {
            while (curLexTokenType == FormulaCons.PLUS || curLexTokenType == FormulaCons.MINUS)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                int operator = curLexTokenType;
                nextLexToken();
                type = processExpressionMultiplyDivide();
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken((operator == FormulaCons.PLUS) ? RToken.PLUS_TOKEN : RToken.MINUS_TOKEN);
            }
            return type;
        }
        else
        {
            while (curLexTokenType == FormulaCons.PLUS || curLexTokenType == FormulaCons.MINUS)
            {
                int operator = curLexTokenType;
                nextLexToken();
                processExpressionMultiplyDivide();
                appendRToken((operator == FormulaCons.PLUS) ? RToken.PLUS_TOKEN : RToken.MINUS_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理ExpressionMultiplyDivide(乘除)
     */
    private short processExpressionMultiplyDivide() throws FormulaParsingException
    {
        // 幂次比乘除高一个优先级
        short type = processExpressionPower();
        // 循环处理直到乘除处理完毕
        if (checkFlag)
        {
            while (curLexTokenType == FormulaCons.MULTIPLY || curLexTokenType == FormulaCons.DIVIDE)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                int operator = curLexTokenType;
                nextLexToken();
                type = processExpressionPower();
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken((operator == FormulaCons.MULTIPLY) ? RToken.MULTIPLY_TOKEN
                    : RToken.DIVIDE_TOKEN);
            }
            return type;
        }
        else
        {
            while (curLexTokenType == FormulaCons.MULTIPLY || curLexTokenType == FormulaCons.DIVIDE)
            {
                int operator = curLexTokenType;
                nextLexToken();
                processExpressionPower();
                appendRToken((operator == FormulaCons.MULTIPLY) ? RToken.MULTIPLY_TOKEN
                    : RToken.DIVIDE_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理ExpressionPower(乘幂)
     */
    private short processExpressionPower() throws FormulaParsingException
    {
        short type = processOperand();
        if (checkFlag)
        {
            if (curLexTokenType == FormulaCons.POWER)
            {
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                nextLexToken();
                type = processExpressionPower();
                if (type != FormulaCons.NUMBER && type != FormulaCons.VARIANT)
                {
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_009);
                }
                appendRToken(RToken.POWER_TOKEN);
            }
            return type;
        }
        else
        {
            if (curLexTokenType == FormulaCons.POWER)
            {
                nextLexToken();
                processExpressionPower();
                appendRToken(RToken.POWER_TOKEN);
            }
            return type;
        }
    }

    /**
     * 处理操作数
     */
    private short processOperand() throws FormulaParsingException
    {
        short type;
        String curText = curLexToken.getText();
        RToken curRToken;
        
        switch (curLexTokenType)
        {
            //常量
        	case FormulaCons.NULL:
        	    curRToken = RToken.NULL_TOKEN;
        	    appendRToken(curRToken);
        	    type = FormulaCons.NULL;
        	    nextLexToken();
        	    break;

            case FormulaCons.INTEGER:
                curRToken = new ObjectToken(FormulaCons.NUMBER, curText);
                appendRToken(curRToken);
                type = FormulaCons.NUMBER;
                nextLexToken();
                break;
                
            case FormulaCons.DECIMAL:
                curRToken = new ObjectToken(FormulaCons.NUMBER, curText);
                appendRToken(curRToken);
                type = FormulaCons.NUMBER;
                nextLexToken();
                break;

            case FormulaCons.TEXT:
                // 去掉首尾的引号
                curText = curText.substring(1, curText.length() - 1);
                // 先判断是否为日期类型
                if ( FormulaUtil.isDate(curText) )
                {
                    curRToken = new ObjectToken(FormulaCons.DATE, curText);
                    appendRToken(curRToken);
                    type = FormulaCons.DATE;
                }
                else
                {
                    curRToken = new ObjectToken(FormulaCons.TEXT, curText);
                    appendRToken(curRToken);
                    type = FormulaCons.TEXT;
                }
                nextLexToken();
                break;
            
            // 系统变量
            case FormulaCons.SYS_VAR:
            {
                Short typeOfShort = sysVarInfo.get(curText);
                if (checkFlag)
                {
                    if (typeOfShort == null || typeOfShort.shortValue() == FormulaCons.INVALID)
                    {
                        String error = String.format(FormulaResource.ERROR_FORMAT, FormulaResource.PARSING_ERROR_003, curText);
                        throw new FormulaParsingException(error);
                    }
                }
                type = typeOfShort;
                curRToken = new SysVarToken(curText, type);
                appendRToken(curRToken);
                nextLexToken();
                break;
            }

            // 字段变量(数据库中的数据)
            case FormulaCons.FIELD_VAR:
            {
                // 处理数据库名、表名、字段名
                String databaseName, tableName, fieldName;
                String strs[] = curText.split("[.]");
                if (strs.length == 2)
                {
                    databaseName = null;
                    tableName = strs[0];
                    fieldName = strs[1];
                }
                else
                {
                    // strs.length == 3
                    databaseName = strs[0];
                    tableName = strs[1];
                    fieldName = strs[2];
                }
                // 获取数据表变量类型
                if (checkFlag && !fieldVarInfo.isEmpty())
                {
                    Short typeOfShort = fieldVarInfo.get(curText);
                    if (typeOfShort == null || typeOfShort.shortValue() == FormulaCons.INVALID)
                    {
                        String error = String.format(FormulaResource.ERROR_FORMAT, FormulaResource.PARSING_ERROR_011, curText);
                        throw new FormulaParsingException(error);
                    }
                    type = typeOfShort;
                }
                else
                {
                    type = FormulaCons.VARIANT;
                }
                curRToken = new FieldToken(databaseName, tableName, fieldName);
                appendRToken(curRToken);
                nextLexToken();
                break;
            }
                
            // 报表变量 (本报表的数据项)
            case FormulaCons.TABLE_VAR:
            {
                int pos = curLexToken.getText().indexOf('.');
                String tableName = curText.substring(0, pos);
                String fieldName = curText.substring(pos + 1);
                // 暂时不做表名、字段名的正确性的检查
                curRToken = new TableToken(tableName, fieldName);        
                type = FormulaCons.VARIANT;
                appendRToken(curRToken);
                nextLexToken();
                break;
            }

            // 输入变量
            case FormulaCons.INPUT_VAR:
                // 获取：提示字符串和[格式]
                int pos1 = curText.indexOf('[');
                int pos2 = curText.indexOf(']');
                String hint = curText.substring(0, pos1);
                if (hint.length() == 0)
                {
                    // error(未输入输入变量的标识)
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_004);
                }

                String dtype = curText.substring(pos1 + 1, pos2);
                char dataType = 'S';
                if ("S".equalsIgnoreCase(dtype))
                {
                    dataType = 'S';
                    type = FormulaCons.TEXT;
                }
                else if ("N".equalsIgnoreCase(dtype))
                {
                    dataType = 'N';
                    type = FormulaCons.NUMBER;
                }
                else if ("D".equalsIgnoreCase(dtype))
                {
                    dataType = 'D';
                    type = FormulaCons.DATE;
                }
                else
                {
                    // error（dataType数据类型错误）
                    String error = String.format(FormulaResource.ERROR_FORMAT, FormulaResource.PARSING_ERROR_005, dtype);
                    throw new FormulaParsingException(error);
                }
                appendRToken(new InputVarToken(dataType));
                nextLexToken();
                break;

            case FormulaCons.LEFT_ROUND_BRACKET:
                nextLexToken();
                type = processExpression();
                //对于括号表达式，最后一定要找到)，否则报错。
                if (curLexTokenType != FormulaCons.RIGHT_ROUND_BRACKET)
                {
                    // error;
                    throw new FormulaParsingException(FormulaResource.PARSING_ERROR_006);
                }
                nextLexToken();

                // 按理后缀表达式不需要括号，故暂将此处注释掉
                // 括号Token
                // appendCode(RToken.CIRCULAR_BRACKET_TOKEN);
                break;

            case FormulaCons.FUNCTION:
                type = processFunction();
                break;

            default:
                // error
                String errorTokenText = curLexToken == null ? "Null LexToken" : curLexToken.getText();
                String error = String.format(FormulaResource.ERROR_FORMAT, FormulaResource.PARSING_ERROR_007, errorTokenText);
                nextLexToken();
                throw new FormulaParsingException(error);
        }

        return type;
    }

    /**
     * 函数分析 
     */
    private short processFunction() throws FormulaParsingException
    {
        short functionType = FormulaUtil.getFunctionId(curLexToken.getText());
        if (functionType < FormulaCons.FUNCTION_START)
        {
            String error = String.format(FormulaResource.ERROR_FORMAT, FormulaResource.PARSING_ERROR_008,
                curLexToken.getText());
            throw new FormulaParsingException(error);
        }
        
        // 左括号
        nextLexToken();
        if (curLexTokenType != FormulaCons.LEFT_ROUND_BRACKET)
        {
            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_010);
        }
        
        // 寻找第一个参数
        nextLexToken();
        int paramCounter = 0;
        ArrayList<Short> paramTypeList = new ArrayList<Short>();
        //队列中已经对括号数量进行了处理，
        while (curLexTokenType != FormulaCons.RIGHT_ROUND_BRACKET)
        {
            //在输入过程中，输入过程中，可能会出现错误。
            if (curLexToken == null)
            {
                throw new FormulaParsingException(FormulaResource.PARSING_ERROR_010);
            }

            paramTypeList.add(processExpression());
            paramCounter++;

            //每个逗号，都要加1
            if (curLexTokenType == FormulaCons.COMMA)
            {
                nextLexToken();
                continue;
            }
            else if (curLexTokenType == FormulaCons.RIGHT_ROUND_BRACKET)
            {
                nextLexToken();
                break;
            }
            else
            {
                throw new FormulaParsingException(FormulaResource.PARSING_ERROR_010);
            }
        }
        
        // 转换参数类型队列为参数类型数组
        short[] paramTypes = new short[paramCounter];
        for (int i=0; i<paramCounter; i++)
        {
            paramTypes[i] = paramTypeList.get(i);
        }

        // 参数个数与类型检测，并获取返回类型
        Short type = FormulaUtil.getReturnType(functionType, paramTypes);
        if (type == FormulaCons.INVALID)
        {
            throw new FormulaParsingException(FormulaResource.PARSING_ERROR_010);
        }

        appendRToken(new FunToken(functionType, (byte)paramCounter));
        return type;
    }
    
    /**
     * 数据类型格式转换
     * 
     * @param dataType DataTypeCons中的数据表数据类型
     * @return DataType中的公式数据类型
     */
    private short convertDataType(short dataType)
    {
        switch (dataType)
        {
            case DataTypeCons.NUMBER_TYPE:
                return FormulaCons.NUMBER;
            case DataTypeCons.TEXT_TYPE:
                return FormulaCons.TEXT;
            case DataTypeCons.DATE_TYPE:
                return FormulaCons.DATE;
            default:
                return FormulaCons.INVALID;
        }
    }

    /**
     * 数据类型格式转换
     * 
     * @param info 以DataTypeCons中的数据表数据类型表示的HashMap
     * @return 以DataType中的公式数据类型表示的HashMap
     */
    private HashMap<String, Short> convertDataType(HashMap<String, Short> info)
    {
        HashMap<String, Short> result = new HashMap<String, Short>();
        for (String k : info.keySet())
        {
            result.put( k, convertDataType(info.get(k)) );
        }
        return result;
    }
    
    private void initializeSysVarInfo()
    {
        sysVarInfo = new HashMap<String, Short>();
        sysVarInfo.put(ReportCommonResource.CURRENTDATE, FormulaCons.DATE);
        sysVarInfo.put(ReportCommonResource.CURRENTDATETIME, FormulaCons.DATE);
        sysVarInfo.put(ReportCommonResource.CURRENTORGUSER, FormulaCons.TEXT);
        sysVarInfo.put(ReportCommonResource.CURRENTUSERNAME, FormulaCons.TEXT);
        sysVarInfo.put(ReportCommonResource.CURRENTLOGINNAME, FormulaCons.TEXT);
    }
}