package apps.moreoffice.report.commons.formula;

import java.util.HashMap;
import java.util.LinkedList;

import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.moreoffice.report.commons.formula.exception.FormulaLexingException;
import apps.moreoffice.report.commons.formula.exception.FormulaParsingException;
import apps.moreoffice.report.commons.formula.token.LexToken;
import apps.moreoffice.report.commons.formula.token.ObjectToken;
import apps.moreoffice.report.commons.formula.token.RToken;
import apps.moreoffice.report.commons.formula.token.SysVarToken;
import apps.moreoffice.report.commons.formula.token.TableToken;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 报表的公式表达式
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

@ SuppressWarnings("serial")
public class RFormula implements SerializableAdapter
{
    // 公式结果的数据类型
    private short type;
    // RToken数组
    private RToken[] tokenArray;

    /**
     * 设置公式类型
     * 
     * @param type 公式类型
     */
    public void setType(short type)
    {
        this.type = type;
    }

    /**
     * 得到公式类型
     * 
     * @return 公式类型
     */
    public short getType()
    {
        return type;
    }

    /**
     * 得到RToken数组
     * 
     * @return RToken数组
     */
    public RToken[] getTokenArray()
    {
        return tokenArray;
    }
    
    /**
     * 设置RToken数组
     * 
     * @param tokenArray RToken数组
     */
    public void setTokenArray(RToken[] tokenArray)
    {
        this.tokenArray = tokenArray;
    }

    /**
     * 替换公式对象中有关系统变量、输入变量与本报表变量为真实的值
     * 
     * @param currentData 系统变量、输入变量与本报表变量的数据
     * @return RFormula 生成好的公式对象
     */
    @ SuppressWarnings("unchecked")
    public void convert(HashMap<String, Object> currentData)
    {
        if (tokenArray == null)
        {
            return;
        }
        int len = tokenArray.length;
        for (int i = 0; i < len; i++)
        {
            RToken token = tokenArray[i];
            if (token instanceof SysVarToken)
            {
                RToken valueToken;
                SysVarToken sysVarToken = (SysVarToken)token;
                String sysVarName = sysVarToken.getName();
                HashMap<String, Object> sysVarValues = (HashMap<String,Object>)currentData.get(ReportCommonResource.SYSVAR);
                if (sysVarValues == null || sysVarValues.isEmpty())
                {
                    tokenArray[i] = RToken.NULL_TOKEN;
                    continue;
                }
                String value = sysVarValues.get(sysVarName).toString();
                
                if (sysVarToken.getDataType() == FormulaCons.TEXT)
                {
                    valueToken = new ObjectToken(FormulaCons.TEXT, value);
                }
                else if (sysVarToken.getDataType() == FormulaCons.DATE)
                {
                    valueToken = new ObjectToken(FormulaCons.DATE, value);
                }
                else
                {
                    // FormulaCons.NUMBER
                    valueToken = new ObjectToken(FormulaCons.NUMBER, value);
                }
                tokenArray[i] = valueToken;
            }
            else if (token instanceof TableToken)
            {
                RToken valueToken;
                TableToken tableToken = (TableToken)token;
                String tableName = tableToken.getTableName();
                String itemName = tableToken.getFieldName();
                HashMap<String, Object> tableValues = (HashMap<String, Object>)currentData.get(tableName);
                if (tableValues == null || tableValues.isEmpty())
                {
                    tokenArray[i] = RToken.NULL_TOKEN;
                    continue;
                }
                Object value = tableValues.get(itemName);
                if (value == null)
                {
                    valueToken = RToken.NULL_TOKEN;
                }
                else
                {
                    if (value instanceof Object[][])
                    {
                        // 交叉表的数据项, 取第一行第一列
                        Object[][] matrix = (Object[][])value;
                        valueToken = new ObjectToken(FormulaCons.TEXT, matrix[0][0].toString());
                    }
                    else if (value instanceof Object[])
                    {
                        // 重复项,取第一项
                        Object[] arr = (Object[])value;
                        valueToken = new ObjectToken(FormulaCons.TEXT, arr[0].toString());
                    }
                    else
                    {
                        // 单一项
                        valueToken = new ObjectToken(FormulaCons.TEXT, value.toString());
                    }
                }
                tokenArray[i] = valueToken;
            } // if else 结束
        } // for循环结束
    }
 
    /**
     * 公式字符串转换为公式对象
     * 
     * @param strFormula 公式字符串(已被检查过，语法正确)
     * @return RFormula 公式对象
     */
    public static RFormula convertFormula(String strFormula)
    {
        try
        {
            LinkedList<LexToken> lexingResult = RLexer.lexicalAnalyze(strFormula);
            return RParser.syntaxAnalyze(lexingResult, null);
        }
        catch (FormulaLexingException e)
        {
            return null;
        }
        catch(FormulaParsingException e)
        {
            return null;
        }
    }

    /**
     * 公式验证
     * 如果验证成功，返回公式对象；否则返回错误文本
     * 
     * @param strFormula 公式字符串
     * @param fieldVarInfo 数据表信息, (库名.)表名.列名 -> 数据表数据类型(非公式数据类型)
     * @return Object 验证结果
     */
    public static Object verifyFormula(String strFormula, HashMap<String, Short> fieldVarInfo)
    {
        try
        {
            if (fieldVarInfo == null)
            {
                fieldVarInfo = new HashMap<String, Short>();
            }
            LinkedList<LexToken> lexingResult = RLexer.lexicalAnalyze(strFormula);
            return RParser.syntaxAnalyze(lexingResult, fieldVarInfo);
        }
        catch (FormulaLexingException e)
        {
            return "[词法分析错误]" + e.getErrorString();
        }
        catch(FormulaParsingException e)
        {
            return "[语法分析错误]" + e.getErrorString();
        }
    }
}