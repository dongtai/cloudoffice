package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 填充方式项
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-8-1
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class FillModeItem extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -5658825913481734248L;
    // id
    private Long id;
    // 提取表达式
    private String expression;
    // 操作
    private Short operate;
    // 目的字段名
    private RField rfield;
    // 锁定
    private Boolean lockState;
    // 排序
    private Short sortType;
    // 位置
    private Long position;

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
     * @return 返回 operate
     */
    public Short getOperate()
    {
        return operate;
    }

    /**
     * @param operate 设置 operate
     */
    public void setOperate(Short operate)
    {
        this.operate = operate;
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
     * @return 返回 lockState
     */
    public Boolean getLockState()
    {
        return lockState;
    }

    /**
     * @param lockState 设置 lockState
     */
    public void setLockState(Boolean lockState)
    {
        this.lockState = lockState;
    }

    /**
     * @return 返回 sortType
     */
    public Short getSortType()
    {
        return sortType;
    }

    /**
     * @param sortType 设置 sortType
     */
    public void setSortType(Short sortType)
    {
        this.sortType = sortType;
    }

    /**
     * @return 返回 position
     */
    public Long getPosition()
    {
        return position;
    }

    /**
     * @param position 设置 position
     */
    public void setPosition(Long position)
    {
        this.position = position;
    }

    /**
     * 对话盒用
     */
    public String toString()
    {
        return rfield.getDfield().getName();
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public FillModeItem clone(boolean isSimple)
    {
        FillModeItem fillModeItem = new FillModeItem();
        fillModeItem.setId(id);
        fillModeItem.setExpression(expression);
        fillModeItem.setOperate(operate);
        fillModeItem.setRfield(isClient() ? rfield : rfield.clone(true));
        fillModeItem.setLockState(lockState);
        fillModeItem.setSortType(sortType);
        fillModeItem.setPosition(position);

        return fillModeItem;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof FillModeItem)
        {
            if (id != null && id.equals(((FillModeItem)obj).getId()))
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
        // 提取表达式
        params.put(ParamCons.EXPRESSION, expression);
        // 操作
        params.put(ParamCons.OPERATE, operate);
        // 目的字段名
        if (rfield != null)
        {
            params.put(ParamCons.RFIELD, rfield.getJsonObj());
        }
        // 锁定
        params.put(ParamCons.LOCKSTATE, lockState);
        // 排序
        params.put(ParamCons.SORTTYPE, sortType);
        // 位置
        params.put(ParamCons.POSITION, position);

        return params;
    }
}