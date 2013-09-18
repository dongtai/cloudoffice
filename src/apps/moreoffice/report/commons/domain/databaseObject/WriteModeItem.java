package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 回写数据项
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-7
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class WriteModeItem extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 5586035407583913411L;
    // id
    private Long id;
    // 提取表达式
    private String expression;
    // 回写字段
    private RField rfield;
    // 基本属性集
    private Long attrFlag;

    // 引用WriteModeItem的ModifyRule
    private transient ModifyRule modifyRule;
    // 引用WriteModeItem的AddDetailRule
    private transient AddDetailRule addDetailRule;
    // 引用WriteModeItem的WriteMode
    private transient WriteMode writeMode;

    /**
     * @return 返回 id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id 设置 id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return 返回 expression
     */
    public String getExpression()
    {
        return expression;
    }

    /**
     * @param expression 设置 expression
     */
    public void setExpression(String expression)
    {
        this.expression = expression;
    }

    /**
     * @return 返回 rfield
     */
    public RField getRfield()
    {
        return rfield;
    }

    /**
     * @param rfield 设置 rfield
     */
    public void setRfield(RField rfield)
    {
        this.rfield = rfield;
    }

    /**
     * @return 返回 attrFlag
     */
    public Long getAttrFlag()
    {
        return attrFlag;
    }

    /**
     * @param attrFlag 设置 attrFlag
     */
    public void setAttrFlag(Long attrFlag)
    {
        this.attrFlag = attrFlag;
    }

    /**
     * @return 返回 modifyRule
     */
    public ModifyRule getModifyRule()
    {
        return modifyRule;
    }

    /**
     * @param modifyRule 设置 modifyRule
     */
    public void setModifyRule(ModifyRule modifyRule)
    {
        this.modifyRule = modifyRule;
    }

    /**
     * @return 返回 addDetailRule
     */
    public AddDetailRule getAddDetailRule()
    {
        return addDetailRule;
    }

    /**
     * @param addDetailRule 设置 addDetailRule
     */
    public void setAddDetailRule(AddDetailRule addDetailRule)
    {
        this.addDetailRule = addDetailRule;
    }

    /**
     * @return 返回 writeMode
     */
    public WriteMode getWriteMode()
    {
        return writeMode;
    }

    /**
     * @param writeMode 设置 writeMode
     */
    public void setWriteMode(WriteMode writeMode)
    {
        this.writeMode = writeMode;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public WriteModeItem clone(boolean isSimple)
    {
        WriteModeItem writeModeItem = new WriteModeItem();
        writeModeItem.setId(id);
        writeModeItem.setExpression(expression);
        writeModeItem.setRfield(isClient() ? rfield : rfield.clone(true));
        writeModeItem.setAttrFlag(attrFlag);

        return writeModeItem;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof WriteModeItem)
        {
            if (id != null && id.equals(((WriteModeItem)obj).getId()))
            {
                return true;
            }
        }

        return super.equals(obj);
    }

    /**
     * 对话盒用
     */
    public String toString()
    {
        return rfield.getDfield().getName();
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // id
        params.put(ParamCons.ID, id);
        // 提取表达式
        params.put(ParamCons.EXPRESSION, expression);
        // 回写字段
        if (rfield != null)
        {
            params.put(ParamCons.RFIELD, rfield.getJsonObj());
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }
}