package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 分级字段
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-3
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class LevelField extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -3305498670980968069L;
    // id
    private Long id;
    // 分级字段
    private DataField dfield;
    // 排序方式
    private Short sortType;
    // 位置
    private Integer position;

    // TreeSelectRule
    private transient TreeSelectRule treeSelectRule;

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
     * @return 返回 dfield
     */
    public DataField getDfield()
    {
        return dfield;
    }

    /**
     * @param dfield 设置 dfield
     */
    public void setDfield(DataField dfield)
    {
        this.dfield = dfield;
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
    public Integer getPosition()
    {
        return position;
    }

    /**
     * @param position 设置 position
     */
    public void setPosition(Integer position)
    {
        this.position = position;
    }

    /**
     * @return 返回 treeSelectRule
     */
    public TreeSelectRule getTreeSelectRule()
    {
        return treeSelectRule;
    }

    /**
     * @param treeSelectRule 设置 treeSelectRule
     */
    public void setTreeSelectRule(TreeSelectRule treeSelectRule)
    {
        this.treeSelectRule = treeSelectRule;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public LevelField clone(boolean isSimple)
    {
        LevelField levelField = new LevelField();
        levelField.setId(id);
        if (dfield != null)
        {
            levelField.setDfield(isClient() ? dfield : dfield.clone(true));
        }
        levelField.setSortType(sortType);
        levelField.setPosition(position);

        return levelField;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof LevelField)
        {
            if (id != null && id.equals(((LevelField)obj).getId()))
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
        // 分级字段
        if (dfield != null)
        {
            params.put(ParamCons.DFIELD, dfield.getJsonObj());
        }
        // 排序方式
        params.put(ParamCons.SORTTYPE, sortType);
        // 位置
        params.put(ParamCons.POSITION, position);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return LevelField 分级字段对象
     */
    @ SuppressWarnings("unchecked")
    public LevelField convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 分级字段
        Object objValue = paramsMap.get(ParamCons.DFIELD);
        if (objValue != null)
        {
            DataField dfield = null;
            if (objValue instanceof HashMap)
            {
                dfield = new DataField();
                dfield.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDfield(dfield);
        }
        // 排序方式
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.SORTTYPE);
        if (shortValue != -1)
        {
            setSortType(shortValue);
        }
        // 位置
        intValue = HashMapTools.getInt(paramsMap, ParamCons.POSITION);
        if (intValue != -1)
        {
            setPosition(intValue);
        }

        return this;
    }
}