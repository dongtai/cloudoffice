package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 关联条件
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class JoinCond extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 5860833704223759388L;
    // id
    private Long id;
    // 左表
    private RTable leftTable;
    // 左字段
    private RField leftField;
    // 关系
    private String relation;
    // 右表
    private RTable rightTable;
    // 右字段
    private RField rightField;

    // 引用JoinCond的ReadRule
    private transient ReadRule readRule;

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
     * @return 返回 leftTable
     */
    public RTable getLeftTable()
    {
        return leftTable;
    }

    /**
     * @param leftTable 设置 leftTable
     */
    public void setLeftTable(RTable leftTable)
    {
        this.leftTable = leftTable;
    }

    /**
     * @return 返回 leftField
     */
    public RField getLeftField()
    {
        return leftField;
    }

    /**
     * @param leftField 设置 leftField
     */
    public void setLeftField(RField leftField)
    {
        this.leftField = leftField;
    }

    /**
     * @return 返回 relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @param relation 设置 relation
     */
    public void setRelation(String relation)
    {
        this.relation = relation;
    }

    /**
     * @return 返回 rightTable
     */
    public RTable getRightTable()
    {
        return rightTable;
    }

    /**
     * @param rightTable 设置 rightTable
     */
    public void setRightTable(RTable rightTable)
    {
        this.rightTable = rightTable;
    }

    /**
     * @return 返回 rightField
     */
    public RField getRightField()
    {
        return rightField;
    }

    /**
     * @param rightField 设置 rightField
     */
    public void setRightField(RField rightField)
    {
        this.rightField = rightField;
    }

    /**
     * @return 返回 readRule
     */
    public ReadRule getReadRule()
    {
        return readRule;
    }

    /**
     * @param readRule 设置 readRule
     */
    public void setReadRule(ReadRule readRule)
    {
        this.readRule = readRule;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public JoinCond clone(boolean isSimple)
    {
        JoinCond joinCond = new JoinCond();
        joinCond.setId(id);
        joinCond.setLeftTable(isClient() ? leftTable : leftTable.clone(true));
        joinCond.setLeftField(isClient() ? leftField : leftField.clone(true));
        joinCond.setRelation(relation);
        joinCond.setRightTable(isClient() ? rightTable : rightTable.clone(true));
        joinCond.setRightField(isClient() ? rightField : rightField.clone(true));

        return joinCond;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof JoinCond)
        {
            if (id != null && id.equals(((JoinCond)obj).getId()))
            {
                return true;
            }
        }

        return super.equals(obj);
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
        // 左表
        if (leftTable != null)
        {
            params.put(ParamCons.LEFTTABLE, leftTable.getJsonObj());
        }
        // 左字段
        if (leftField != null)
        {
            params.put(ParamCons.LEFTFIELD, leftField.getJsonObj());
        }
        // 关系
        params.put(ParamCons.RELATION, relation);
        // 右表
        if (rightTable != null)
        {
            params.put(ParamCons.RIGHTTABLE, rightTable.getJsonObj());
        }
        // 右字段
        if (rightField != null)
        {
            params.put(ParamCons.RIGHTFIELD, rightField.getJsonObj());
        }

        return params;
    }
}