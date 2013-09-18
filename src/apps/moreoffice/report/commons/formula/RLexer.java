package apps.moreoffice.report.commons.formula;

import java.util.LinkedList;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.moreoffice.report.commons.formula.exception.FormulaLexingException;
import apps.moreoffice.report.commons.formula.resource.FormulaResource;
import apps.moreoffice.report.commons.formula.token.LexToken;

/**
 * 公式词法分析器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚) & 实习生76(魏强) 
 * <p>
 * @日期:       2012-12-7
 * <p>
 * @负责人:      实习生76(魏强) 
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */

public class RLexer
{
    private static RLexer lexer = new RLexer();

    // 要解析的原始字符串
    protected String parsingText;
    // 当前解析的文本长度
    protected int strLength;
    // 当前解析的字串位置。进入时指向标记的开头，解析时都是指向当前解析字符的下一个位置。退出时指向标记结束
    protected int curPos;
    // 当前Token开始的位置    
    protected int curStart;

    // 词法分析链表
    protected LinkedList<LexToken> tokenQueue;
    // 当前Token
    protected LexToken curToken;
    // 前一个Token
    protected LexToken preToken;
    // 当前Token类型
    protected int curType;
    // 前一个Token类型
    protected int preType;

    /**
     * 构造器
     */
    private RLexer()
    {
        tokenQueue = new LinkedList<LexToken>();
    }

    /**
     * 词法分析
     * 错误抛出FormulaLexingException异常
     * 正常返回LexToken链表
     */
    public static LinkedList<LexToken> lexicalAnalyze(String text) throws FormulaLexingException
    {
        return lexer.doLexicalAnalyze(text);
    }

    /**
     * 进行词法分析
     * 错误抛出FormulaLexingException异常
     * 正常返回LexToken链表
     */
    private LinkedList<LexToken> doLexicalAnalyze(String text) throws FormulaLexingException
    {
        //检测输入的文本
        if (text == null || text.length() < 1)
        {
            throw new FormulaLexingException(FormulaResource.LEXING_ERROR_004);
        }
        else if (text.length() > FormulaCons.PARSING_TEXT_LIMIT)
        {
            throw new FormulaLexingException(FormulaResource.LEXING_ERROR_007);
        }

        //重置词法分析环境
        tokenQueue.clear();
        parsingText = text;
        strLength = parsingText.length();
        curStart = curPos = 0;
        preToken = curToken = new LexToken();
        curType = preType = FormulaCons.INVALID;
        int leftRoundBracketCounter = 0;
        int rightRoundBracketCounter = 0;

        // 逐字扫描解析文本，进行词法分析
        while (nextToken() != null)
        {
            switch (curType)
            {
                case FormulaCons.INTEGER:
                    if (!processInteger())
                    {
                        String error = String.format(FormulaResource.ERROR_FORMAT,
                            FormulaResource.LEXING_ERROR_006, curToken.getText());
                        throw new FormulaLexingException(error);
                    }
                    break;

                case FormulaCons.DECIMAL:
                case FormulaCons.SCIENCE:
                    if (!processFloat())
                    {
                        String error = String.format(FormulaResource.ERROR_FORMAT,
                            FormulaResource.LEXING_ERROR_002, curToken.getText());
                        throw new FormulaLexingException(error);
                    }
                    else
                    {
                        curToken.setType(FormulaCons.DECIMAL);
                    }
                    break;

                //处理变量
                case FormulaCons.VARIANT:
                    if (!processVariant())
                    {
                        String error = String.format(FormulaResource.ERROR_FORMAT,
                            FormulaResource.LEXING_ERROR_009, curToken.getText());
                        throw new FormulaLexingException(error);
                    }
                    break;

                case FormulaCons.LEFT_ROUND_BRACKET:
                    leftRoundBracketCounter++;
                    if (preToken.typeEquals(FormulaCons.VARIANT))
                    {
                        //未对函数名的正确性做检测
                        preToken.setType(FormulaCons.FUNCTION);
                        preType = FormulaCons.FUNCTION;
                    }
                    break;

                case FormulaCons.RIGHT_ROUND_BRACKET:
                    rightRoundBracketCounter++;
                    if (leftRoundBracketCounter < rightRoundBracketCounter)
                    {
                        throw new FormulaLexingException(FormulaResource.LEXING_ERROR_001);
                    }
                    break;

                default:
                    //默认情况下，其他标记合法，添加至队列
                    break;
            }

            tokenQueue.offer(curToken);
        } // while循环结束

        // 括号不匹配
        if (leftRoundBracketCounter != rightRoundBracketCounter)
        {
            throw new FormulaLexingException(FormulaResource.LEXING_ERROR_001);
        }

        if (tokenQueue.isEmpty())
        {
            throw new FormulaLexingException(FormulaResource.LEXING_ERROR_005);
        }
        else
        {
            return tokenQueue;
        }
    }

    /*
     * 从curPos开始解析文本，并取得当前的Token
     */
    private LexToken nextToken() throws FormulaLexingException
    {
        if (curPos >= strLength)
        {
            return null;
        }

        preToken = curToken;
        preType = curType;

        // 当前token类型
        short tokenType = FormulaCons.INVALID;
        // 当前操作数类型。因为操作数遇到分隔符才能确定，所以要保存中间状态。
        // 当operandType为INVALID时，tokenType表示当前Token类型；
        // 当operandType非INVALID时，tokenType表示下一个Token类型。
        short operandType = FormulaCons.INVALID;

        for (curStart = curPos; curPos < strLength && tokenType == FormulaCons.INVALID; curPos++)
        {
            // 取得当前字符。 位置指针不移动
            char curChar = parsingText.charAt(curPos);
            switch (curChar)
            {
            // 运算符、控制符等
                case '+':
                    if (curStart == curPos)
                    {
                        if (preType == FormulaCons.RIGHT_ROUND_BRACKET
                            || (preType != FormulaCons.INVALID && preType < FormulaCons.OP_START))
                        {
                            //上一个Token为常量、变量或右括号，则一定为加号
                            tokenType = FormulaCons.PLUS;
                        }
                        else
                        {
                            //此时为符号位
                            operandType = FormulaCons.INTEGER;
                        }
                    }
                    else if (operandType != FormulaCons.SCIENCE)
                    {
                        //当前操作数非科学计数法
                        tokenType = FormulaCons.PLUS;
                    }
                    else
                    {
                        char preChar = parsingText.charAt(curPos - 1);
                        if (preChar != 'e' && preChar != 'E')
                        {
                            tokenType = FormulaCons.PLUS;
                        }
                    }
                    break;

                case '-':
                    if (curStart == curPos)
                    {
                        if (preType == FormulaCons.RIGHT_ROUND_BRACKET
                            || (preType != FormulaCons.INVALID && preType < FormulaCons.OP_START))
                        {
                            //上一个Token为常量、变量或右括号，则一定为加号
                            tokenType = FormulaCons.MINUS;
                        }
                        else
                        {
                            //此时为符号位
                            operandType = FormulaCons.INTEGER;
                        }
                    }
                    else if (operandType != FormulaCons.SCIENCE)
                    {
                        //当前操作数非科学计数法
                        tokenType = FormulaCons.MINUS;
                    }
                    else
                    {
                        char preChar = parsingText.charAt(curPos - 1);
                        if (preChar != 'e' && preChar != 'E')
                        {
                            tokenType = FormulaCons.MINUS;
                        }

                    }
                    break;

                case '*':
                    tokenType = FormulaCons.MULTIPLY;
                    break;

                case '/':
                    tokenType = FormulaCons.DIVIDE;
                    break;

                case '^':
                    tokenType = FormulaCons.POWER;
                    break;

                case '&':
                    tokenType = FormulaCons.CONCAT;
                    break;

                case '=':
                    tokenType = FormulaCons.EQUAL;
                    break;

                case '<':
                    tokenType = FormulaCons.LESS;
                    //<>,<=
                    if (curPos + 1 < strLength)
                    {
                        char nextChar = parsingText.charAt(curPos + 1);
                        if (nextChar == '>')
                        {
                            tokenType = FormulaCons.NOT_EQUAL;
                            curPos++;
                            break;
                        }

                        if (nextChar == '=')
                        {
                            tokenType = FormulaCons.LESS_EQUAL;
                            curPos++;
                            break;
                        }
                    }
                    break;

                case '>':
                    tokenType = FormulaCons.GREATER;
                    //>=
                    if (curPos + 1 < strLength && parsingText.charAt(curPos + 1) == '=')
                    {
                        tokenType = FormulaCons.GREATER_EQUAL;
                        curPos++;
                    }
                    break;

                case '(':
                    tokenType = FormulaCons.LEFT_ROUND_BRACKET;
                    break;

                case ')':
                    tokenType = FormulaCons.RIGHT_ROUND_BRACKET;
                    break;

                case ',':
                    tokenType = FormulaCons.COMMA;
                    break;

                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (curStart == curPos)
                    {
                        //略过空白字符
                        curStart++;
                        continue;
                    }
                    else
                    {
                        //操作数已结束
                        tokenType = FormulaCons.BLANK;
                    }
                    break;

                case '\'':
                    if (curStart == curPos)
                    {
                        if (processText(curChar))
                        {
                            //此时curPos指向下一个引号
                            operandType = FormulaCons.TEXT;
                        }
                        else
                        {
                            //此时curPos指向文本末尾
                            String error = String.format(FormulaResource.ERROR_FORMAT,
                                FormulaResource.LEXING_ERROR_003,
                                parsingText.substring(curStart, strLength));
                            throw new FormulaLexingException(error);
                        }
                    }
                    else
                    {
                        //非字符串操作数中不能含有单引号
                        String error = String.format(FormulaResource.ERROR_FORMAT,
                            FormulaResource.LEXING_ERROR_008,
                            parsingText.substring(curStart, curPos + 1));
                        throw new FormulaLexingException(error);
                    }
                    break;

                case 'e':
                case 'E':
                    if (curStart == curPos)
                    {
                        // 非数字开头的作为变量
                        operandType = FormulaCons.VARIANT;
                    }
                    else if (operandType == FormulaCons.INTEGER
                        || operandType == FormulaCons.DECIMAL)
                    {
                        operandType = FormulaCons.SCIENCE;
                    }
                    break;

                case '.':
                    if (curStart == curPos || operandType == FormulaCons.INTEGER)
                    {
                        //如果是小数点就处理为浮点值
                        operandType = FormulaCons.DECIMAL;
                        break;
                    }
                    break;

                default:
                    if (curStart == curPos)
                    {
                        if (Character.isDigit(curChar))
                        {
                            operandType = FormulaCons.INTEGER;
                        }
                        else
                        {
                            // 其他字符
                            operandType = FormulaCons.VARIANT;
                        }
                    }
                    break;
            } // 外部switch结束
        } // for循环结束结束

        //操作数已经遇到后面的分隔符，所以后退一个字符。
        if (operandType != FormulaCons.INVALID)
        {
            if (tokenType != FormulaCons.INVALID)
            {
                curPos--;
            }
            curToken = new LexToken(parsingText.substring(curStart, curPos), operandType);
            curType = curToken.getType();
            return curToken;
        }

        // 操作符的情况
        curToken = new LexToken(parsingText.substring(curStart, curPos), tokenType);
        curType = curToken.getType();
        return curToken;
    }

    /*
     * 字符串分析
     * 参数sbChar为文本分隔符
     */
    private boolean processText(char sbChar)
    {
        curPos++;
        if (curPos < strLength)
        {
            int next = parsingText.indexOf(sbChar, curPos);
            if (next > 0)
            {
                curPos = next;
                return true;
            }
        }
        curPos = strLength;
        return false;
    }

    private boolean processInteger()
    {
        try
        {
            Long.parseLong(curToken.getText());
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /*
     * 浮点数处理
     */
    private boolean processFloat()
    {
        try
        {
            double value = Double.parseDouble(curToken.getText());
            curToken.setType(FormulaCons.DECIMAL);
            if (Double.isNaN(value) || Double.isInfinite(value))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /*
     * 变量（标识符）处理
     */
    /**
     * @return
     */
    private boolean processVariant()
    {
        // 数字或小数点开头是非法的标识符
        char ch = curToken.getText().charAt(0);
        if (Character.isDigit(ch) || ch == '.')
        {
            return false;
        }

        // 中文运算符
        if (curToken.textEquals("并且"))
        {
            curToken.setType(FormulaCons.AND);
        }
        else if (curToken.textEquals("或者"))
        {
            curToken.setType(FormulaCons.OR);
        }
        else if (curToken.textEquals("不满足"))
        {
            curToken.setType(FormulaCons.NOT);
        }
        else if (curToken.textEquals("形如"))
        {
            curToken.setType(FormulaCons.LIKE);
        }
        else if (curToken.textEquals("介于"))
        {
            curToken.setType(FormulaCons.BETWEEN);
        }
        else if (curToken.textEquals("属于"))
        {
            curToken.setType(FormulaCons.BELONG);
        }
        else if (curToken.textEquals("不属于"))
        {
            curToken.setType(FormulaCons.NOT_BELONG);
        }
        else if (curToken.textEquals("有值"))
        {
            curToken.setType(FormulaCons.HAS_VALUE);
        }
        else if (curToken.textEquals("无值"))
        {
            curToken.setType(FormulaCons.NULL_VALUE);
        }
        // 中文操作数
        else if (curToken.textEquals("常量.空值"))
        {
            curToken.setType(FormulaCons.NULL);
        }
        else if (curToken.textStartsWith("系统变量."))
        {
            // 检查系统变量名
            String curText = curToken.getText();
            String sysVarName = curText.substring(5);
            if (sysVarName.length() < 1 || sysVarName.indexOf('.') > -1
                || Character.isDigit(sysVarName.charAt(0)))
            {
                return false;
            }

            curToken.setType(FormulaCons.SYS_VAR);
            curToken.setText(sysVarName);
        }
        else if (curToken.textStartsWith("输入."))
        {
            // 检查输入提示与输入类型
            String curText = curToken.getText();
            int curLength = curText.length();
            if (curLength < 7 || curText.charAt(curLength - 3) != '['
                || curText.charAt(curLength - 1) != ']')
            {
                return false;
            }
            char inputVarType = curText.charAt(curLength - 2);
            if (inputVarType != 'S' && inputVarType != 'N' && inputVarType != 'D')
            {
                return false;
            }
            String inputVarHint = curText.substring(3, curLength - 3);
            if (inputVarHint.indexOf('.') > -1 || Character.isDigit(inputVarHint.charAt(0)))
            {
                return false;
            }

            curToken.setType(FormulaCons.INPUT_VAR);
            curToken.setText(curText.substring(3));

        }
        else if (curToken.textStartsWith("本报表."))
        {
            // 检查表名与字段名
            String[] strs = curToken.getText().split("[.]", 3);
            if (strs.length < 3)
            {
                return false;
            }
            String tableName = strs[1];
            if (tableName.length() < 1 || tableName.indexOf('.') > -1
                || Character.isDigit(tableName.charAt(0)))
            {
                return false;
            }
            String fieldName = strs[2];
            if (fieldName.length() < 1 || fieldName.indexOf('.') > -1
                || Character.isDigit(fieldName.charAt(0)))
            {
                return false;
            }

            // 本报表变量
            curToken.setType(FormulaCons.TABLE_VAR);
            curToken.setText(curToken.getText().substring(4));
        }
        else if (curToken.textContains("."))
        {
            // 检查库名表名与字段名
            String[] strs = curToken.getText().split("[.]", 3);
            if (strs.length < 2)
            {
                return false;
            }
            for (String str : strs)
            {
                if (str.length() < 1 || str.indexOf('.') > -1 || Character.isDigit(str.charAt(0)))
                {
                    return false;
                }
            }

            // 数据表变量
            curToken.setType(FormulaCons.FIELD_VAR);
        }
        else if (FormulaUtil.getFunctionId(curToken.getText()) >= FormulaCons.FUNCTION_START)
        {
            curToken.setType(FormulaCons.FUNCTION);
        }
        else
        {
            return false;
        }

        //更新curType
        curType = curToken.getType();
        return true;
    }
}
