package apps.moreoffice.report.commons.formula.token;

import java.util.ArrayList;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 集合Token
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
public class SetToken extends RToken
{
    /**
     * 构造器
     * 
     * @param list 集合
     */
    public SetToken(ArrayList<Object> list)
    {
        super(FormulaCons.VARIANT_SET);
        if (list != null && list.size() > 0)
        {
            objList = new Object[list.size()];
            list.toArray(objList);
        }
    }

    /**
     * 构造器
     * 
     * @param dataType 数据类型
     * @param list 集合
     */
    public SetToken(short dataType, Object[] list)
    {
        super(FormulaCons.VARIANT_SET);
        this.dataType = dataType;
        this.objList = list;
    }

    /**
     * @return 返回 dataType
     */
    public short getDataType()
    {
        return dataType;
    }

    /**
     * @return 返回 objList
     */
    public Object[] getObjList()
    {
        return objList;
    }

    // 数据类型
    private short dataType;
    // 集合
    private Object[] objList;
}