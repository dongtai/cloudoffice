package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 系统变量Token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚)
 * <p>
 * @日期:       2012-8-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class SysVarToken extends RToken
{
    /**
     * 构造器
     * 
     * @param name 系统变量名
     */
    public SysVarToken(String name, short dataType)
    {
        super(FormulaCons.SYS_VAR);
        this.name = name;
        this.dataType = dataType;
    }

    /**
     * @return 返回系统变量名
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return 返回系统变量类型
     */
    public short getDataType()
    {
        return dataType;
    }

    // 系统变量名
    private String name;
    // 系统变量类型
    private short dataType;
}