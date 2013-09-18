package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 输入变量Token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚)
 * <p>
 * @日期:       2012-8-23
 * <p>
 * @负责人:     实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class InputVarToken extends RToken
{
    /**
     * 构造器
     */
    public InputVarToken()
    {
        super(FormulaCons.INPUT_VAR);
    }

    /**
     * 构造器
     * 
     * @param type 数据类型
     */
    public InputVarToken(char type)
    {
        super(FormulaCons.INPUT_VAR);
        dataType = type;
    }

    /**
     * @return 返回 hint
     */
    public String getHint()
    {
        return hint;
    }

    /**
     * @return 返回 dataType
     */
    public char getDataType()
    {
        return dataType;
    }
    
    // 输入提示
    private String hint;
    // 数据类型
    private char dataType;
}