package apps.moreoffice.report.commons.formula.exception;

/**
 * 词法分析异常
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       实习生76(魏强) 
 * <p>
 * @日期:       2012-12-7
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */

@ SuppressWarnings("serial")
public class FormulaLexingException extends Exception
{
    private String errorString;
    
    public FormulaLexingException(String errorString)
    {
        this.errorString = errorString;
    }
    
    public String getErrorString()
    {
        return errorString;
    }
}
