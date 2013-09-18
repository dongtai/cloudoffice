package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 交叉表数据Token
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
public class MatrixToken extends RToken
{
    /**
     * 构造器
     * 
     * @param dataType 数据类型
     * @param objArray 值数组
     */
    public MatrixToken(short dataType, Object[][] objArray)
    {
        super(FormulaCons.VARIANT_SET);
        this.dataType = dataType;
        this.objArray = objArray;
    }

    /**
     * @return 返回 dataType
     */
    public short getDataType()
    {
        return dataType;
    }

    /**
     * @return 返回 objArray
     */
    public Object[][] getObjArray()
    {
        return objArray;
    }

    // 数据类型
    private short dataType;
    // 值数组
    private Object[][] objArray;
}