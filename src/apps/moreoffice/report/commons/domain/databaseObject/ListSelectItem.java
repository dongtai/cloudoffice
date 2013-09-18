package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 列表选择的数据项
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
public class ListSelectItem extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 3744623209435381902L;
    // id
    private Long id;
    // 基本属性集
    private Long attrFlag;
    // 位置
    private Long position;
    // 字段名
    private DataField dfield;
    // 排序类型
    private Short sortType;
    // 显示名称
    private String showName;
    // 关联树型规范
    private TreeSelectRule treeSelectRule;
    // 关联类型
    private Short associationType;

    // ListSelectRule
    private transient ListSelectRule listSelectRule;

    /**
     * 默认构造器
     */
    public ListSelectItem()
    {
        attrFlag = (long)0;
    }

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
     * 得到boolean属性
     * 
     * @param flag 标记位
     *        DataRuleCons.LISTSELECTITEM_SORT:分类
     *        DataRuleCons.LISTSELECTITEM_HIDE:隐藏
     *        DataRuleCons.LISTSELECTITEM_RETURN:返回项
     *        DataRuleCons.LISTSELECTITEM_STATISTICS:统计显示
     * @return boolean boolean属性
     */
    public boolean getBooleanAttr(int flag)
    {
        return DomainTools.isLongFlag(attrFlag, flag);
    }

    /**
     * 设置boolean属性
     * 
     * @param flag 标记位
     *        DataRuleCons.LISTSELECTITEM_SORT:分类
     *        DataRuleCons.LISTSELECTITEM_HIDE:隐藏
     *        DataRuleCons.LISTSELECTITEM_RETURN:返回项
     *        DataRuleCons.LISTSELECTITEM_STATISTICS:统计显示
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
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
     * @return 返回 showName
     */
    public String getShowName()
    {
        return showName;
    }

    /**
     * @param showName 设置 showName
     */
    public void setShowName(String showName)
    {
        this.showName = showName;
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
     * @return 返回 associationType
     */
    public Short getAssociationType()
    {
        return associationType;
    }

    /**
     * @param associationType 设置 associationType
     */
    public void setAssociationType(Short associationType)
    {
        this.associationType = associationType;
    }

    /**
     * @return 返回 listSelectRule
     */
    public ListSelectRule getListSelectRule()
    {
        return listSelectRule;
    }

    /**
     * @param listSelectRule 设置 listSelectRule
     */
    public void setListSelectRule(ListSelectRule listSelectRule)
    {
        this.listSelectRule = listSelectRule;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public ListSelectItem clone(boolean isSimple)
    {
        ListSelectItem listSelectItem = new ListSelectItem();
        listSelectItem.setId(id);
        listSelectItem.setAttrFlag(attrFlag);
        listSelectItem.setPosition(position);
        if (dfield != null)
        {
            listSelectItem.setDfield(isClient() ? dfield : dfield.clone(true));
        }
        listSelectItem.setSortType(sortType);
        listSelectItem.setShowName(showName);
        if (treeSelectRule != null)
        {
            listSelectItem.setTreeSelectRule(isClient() ? treeSelectRule : treeSelectRule
                .clone(isSimple));
        }
        listSelectItem.setAssociationType(associationType);

        return listSelectItem;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof ListSelectItem)
        {
            if (id != null && id.equals(((ListSelectItem)obj).getId()))
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
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 位置
        params.put(ParamCons.POSITION, position);
        // 字段名
        if (dfield != null)
        {
            params.put(ParamCons.DFIELD, dfield.getJsonObj());
        }
        // 排序类型
        params.put(ParamCons.SORTTYPE, sortType);
        // 显示名称
        params.put(ParamCons.SHOWNAME, showName);
        // 关联树型规范
        if (treeSelectRule != null)
        {
            params.put(ParamCons.TREESELECTRULE, treeSelectRule.getJsonObj());
        }
        // 关联类型
        params.put(ParamCons.ASSOCIATIONTYPE, associationType);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return ListSelectItem 列表选择项对象
     */
    @ SuppressWarnings("unchecked")
    public ListSelectItem convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 位置
        longValue = HashMapTools.getLong(paramsMap, ParamCons.POSITION);
        if (longValue != -1)
        {
            setPosition(longValue);
        }
        // 字段名
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
        // 排序类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.SORTTYPE);
        if (shortValue != -1)
        {
            setSortType(shortValue);
        }
        // 显示名称
        stringValue = HashMapTools.getString(paramsMap, ParamCons.SHOWNAME);
        if (stringValue != null)
        {
            setShowName(stringValue);
        }
        // 关联树型规范
        objValue = paramsMap.get(ParamCons.TREESELECTRULE);
        if (objValue != null)
        {
            TreeSelectRule treeSelectRule = null;
            if (objValue instanceof HashMap)
            {
                treeSelectRule = new TreeSelectRule();
                treeSelectRule.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setTreeSelectRule(treeSelectRule);
        }
        // 关联类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.ASSOCIATIONTYPE);
        if (shortValue != -1)
        {
            setAssociationType(shortValue);
        }

        return this;
    }
}