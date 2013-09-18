package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 用户表字段信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-8
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataField extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -4340676580244029683L;
    // id
    private Long id;
    // 字段名
    private String name;
    // 物理上的字段名，即真正的字段名
    private String realName;
    /**
     * 基本属性集
     * 0:主键
     * 1:必填
     */
    private Long attrFlag;

    // 引用DataField的DownListRule集合
    private transient Set<DownListRule> downListRules;
    // 引用DataField的DownListRule的排序字段集合
    private transient Set<DownListRule> downListRuleSs;
    // 引用DataField的ListSelectDataItem集合
    private transient Set<ListSelectItem> listSelectItems;
    // 引用DataField的TreeSelectRule的编码或关键字段集合
    private transient Set<TreeSelectRule> treeSelectRule1;
    // 引用DataField的TreeSelectRule的父关键字段集合
    private transient Set<TreeSelectRule> treeSelectRule2;
    // 引用DataField的TreeSelectRule的显示字段集合
    private transient Set<TreeSelectRule> treeSelectRule3;
    // 引用DataField的LevelField集合
    private transient Set<LevelField> levelFields;
    // 引用DataField的RField集合
    private transient Set<RField> rfields;

    /**
     * 默认构造器
     */
    public DataField()
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
     * @return 返回 name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name 设置 name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return 返回 realName
     */
    public String getRealName()
    {
        return realName == null ? name : realName;
    }

    /**
     * @param realName 设置 realName
     */
    public void setRealName(String realName)
    {
        this.realName = realName;
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
     * 得到是否是主键
     * 
     * @return boolean 是否是主键
     */
    public boolean isPrimary()
    {
        return DomainTools.isLongFlag(attrFlag, 0);
    }

    /**
     * 设置是否是主键
     * 
     * @param primary 是否是主键
     */
    public void setPrimary(boolean primary)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 0, primary);
    }

    /**
     * @return boolean 得到是否必填
     */
    public boolean getNotNull()
    {
        return DomainTools.isLongFlag(attrFlag, 1);
    }

    /**
     * @param notNull 设置是否必填
     */
    public void setNotNull(boolean notNull)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 1, notNull);
    }

    /**
     * @return 返回 downListRules
     */
    public Set<DownListRule> getDownListRules()
    {
        return downListRules;
    }

    /**
     * @param downListRules 设置 downListRules
     */
    public void setDownListRules(Set<DownListRule> downListRules)
    {
        this.downListRules = downListRules;
    }

    /**
     * @return 返回 downListRuleSs
     */
    public Set<DownListRule> getDownListRuleSs()
    {
        return downListRuleSs;
    }

    /**
     * @param downListRuleSs 设置 downListRuleSs
     */
    public void setDownListRuleSs(Set<DownListRule> downListRuleSs)
    {
        this.downListRuleSs = downListRuleSs;
    }

    /**
     * @return 返回 listSelectItems
     */
    public Set<ListSelectItem> getListSelectItems()
    {
        return listSelectItems;
    }

    /**
     * @param listSelectItems 设置 listSelectItems
     */
    public void setListSelectItems(Set<ListSelectItem> listSelectItems)
    {
        this.listSelectItems = listSelectItems;
    }

    /**
     * @return 返回 treeSelectRule1
     */
    public Set<TreeSelectRule> getTreeSelectRule1()
    {
        return treeSelectRule1;
    }

    /**
     * @param treeSelectRule1 设置 treeSelectRule1
     */
    public void setTreeSelectRule1(Set<TreeSelectRule> treeSelectRule1)
    {
        this.treeSelectRule1 = treeSelectRule1;
    }

    /**
     * @return 返回 treeSelectRule2
     */
    public Set<TreeSelectRule> getTreeSelectRule2()
    {
        return treeSelectRule2;
    }

    /**
     * @param treeSelectRule2 设置 treeSelectRule2
     */
    public void setTreeSelectRule2(Set<TreeSelectRule> treeSelectRule2)
    {
        this.treeSelectRule2 = treeSelectRule2;
    }

    /**
     * @return 返回 treeSelectRule3
     */
    public Set<TreeSelectRule> getTreeSelectRule3()
    {
        return treeSelectRule3;
    }

    /**
     * @param treeSelectRule3 设置 treeSelectRule3
     */
    public void setTreeSelectRule3(Set<TreeSelectRule> treeSelectRule3)
    {
        this.treeSelectRule3 = treeSelectRule3;
    }

    /**
     * @return 返回 levelFields
     */
    public Set<LevelField> getLevelFields()
    {
        return levelFields;
    }

    /**
     * @param levelFields 设置 levelFields
     */
    public void setLevelFields(Set<LevelField> levelFields)
    {
        this.levelFields = levelFields;
    }

    /**
     * @return 返回 rfields
     */
    public Set<RField> getRfields()
    {
        return rfields;
    }

    /**
     * @param rfields 设置 rfields
     */
    public void setRfields(Set<RField> rfields)
    {
        this.rfields = rfields;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DataField clone(boolean isSimple)
    {
        DataField dfield = new DataField();
        dfield.setId(id);
        dfield.setName(name);
        dfield.setRealName(realName);
        dfield.setAttrFlag(attrFlag);
        return dfield;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataField)
        {
            if (id != null && id.equals(((DataField)obj).getId()))
            {
                return true;
            }
        }

        return super.equals(obj);
    }

    /**
     * 对话盒使用
     */
    public String toString()
    {
        return name;
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
        // 字段名
        params.put(ParamCons.NAME, name);
        // 物理上的字段名，即真正的字段名
        params.put(ParamCons.REALNAME, realName);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataField 物理字段对象
     */
    public DataField convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 字段名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }
        // 物理上的字段名，即真正的字段名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.REALNAME);
        if (stringValue != null)
        {
            setRealName(stringValue);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }

        return this;
    }
}