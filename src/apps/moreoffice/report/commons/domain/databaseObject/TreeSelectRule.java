package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范：树型选择
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TreeSelectRule extends DataRule
{
    // 序列化ID
    private static final long serialVersionUID = 6361678659289223181L;
    // 数据源类型
    private Short dataSourceType;
    // 基本属性集
    private Long attrFlag;
    // 固定取值
    private String fixedValue;
    // 数据表
    private DataTable dtable;
    // 筛选条件
    private String filterCond;
    // 构造方式
    private Short formatType;
    // 分级字段
    private Set<LevelField> levelFields;
    // 编码或关键字段
    private DataField codeorPrimaryField;
    // 父关键字段
    private DataField parentPrimaryField;
    // 显示字段
    private DataField viewField;
    // 各级位数 
    private String levelNum;
    // 排序选择
    private String sortSelect;
    // 排序类型
    private String sortType;

    // 引用TreeSelectRule的ListSelectDataItem
    private transient Set<ListSelectItem> listSelectItems;

    /**
     * 构造器
     */
    public TreeSelectRule()
    {
        super();
        setType(DataRuleCons.TREESELECT);
        setDataSourceType(DataRuleCons.DATASOURCE_FIXED);
        setAttrFlag((long)0);
        setFormatType((short)0);
    }

    /**
     * @return 返回 dataSourceType
     */
    public Short getDataSourceType()
    {
        return dataSourceType;
    }

    /**
     * @param dataSourceType 设置 dataSourceType
     */
    public void setDataSourceType(Short dataSourceType)
    {
        this.dataSourceType = dataSourceType;
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
     *        DataRuleCons.ONLYSELECTBOTTOM:是否只能选择最底层节点
     *        DataRuleCons.MULTSELECT:是否能多选
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
     *        DataRuleCons.ONLYSELECTBOTTOM:是否只能选择最底层节点
     *        DataRuleCons.MULTSELECT:是否能多选
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
    }

    /**
     * @return 返回 fixedValue
     */
    public String getFixedValue()
    {
        return fixedValue;
    }

    /**
     * @param fixedValue 设置 fixedValue
     */
    public void setFixedValue(String fixedValue)
    {
        this.fixedValue = fixedValue;
    }

    /**
     * @return 返回 dtable
     */
    public DataTable getDtable()
    {
        return dtable;
    }

    /**
     * @param dtable 设置 dtable
     */
    public void setDtable(DataTable dtable)
    {
        this.dtable = dtable;
    }

    /**
     * @return 返回 filterCond
     */
    public String getFilterCond()
    {
        return filterCond;
    }

    /**
     * @param filterCond 设置 filterCond
     */
    public void setFilterCond(String filterCond)
    {
        this.filterCond = filterCond;
    }

    /**
     * @return 返回 formatType
     */
    public Short getFormatType()
    {
        return formatType;
    }

    /**
     * @param formatType 设置 formatType
     */
    public void setFormatType(Short formatType)
    {
        this.formatType = formatType;
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
     * @return 返回 codeorPrimaryField
     */
    public DataField getCodeorPrimaryField()
    {
        return codeorPrimaryField;
    }

    /**
     * @param codeorPrimaryField 设置 codeorPrimaryField
     */
    public void setCodeorPrimaryField(DataField codeorPrimaryField)
    {
        this.codeorPrimaryField = codeorPrimaryField;
    }

    /**
     * @return 返回 parentPrimaryField
     */
    public DataField getParentPrimaryField()
    {
        return parentPrimaryField;
    }

    /**
     * @param parentPrimaryField 设置 parentPrimaryField
     */
    public void setParentPrimaryField(DataField parentPrimaryField)
    {
        this.parentPrimaryField = parentPrimaryField;
    }

    /**
     * @return 返回 viewField
     */
    public DataField getViewField()
    {
        return viewField;
    }

    /**
     * @param viewField 设置 viewField
     */
    public void setViewField(DataField viewField)
    {
        this.viewField = viewField;
    }

    /**
     * @return 返回 levelNum
     */
    public String getLevelNum()
    {
        return levelNum;
    }

    /**
     * @param levelNum 设置 levelNum
     */
    public void setLevelNum(String levelNum)
    {
        this.levelNum = levelNum;
    }

    /**
     * @return 返回 sortSelect
     */
    public String getSortSelect()
    {
        return sortSelect;
    }

    /**
     * @param sortSelect 设置 sortSelect
     */
    public void setSortSelect(String sortSelect)
    {
        this.sortSelect = sortSelect;
    }

    /**
     * @return 返回 sortType
     */
    public String getSortType()
    {
        return sortType;
    }

    /**
     * @param sortType 设置 sortType
     */
    public void setSortType(String sortType)
    {
        this.sortType = sortType;
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
     * 得到类型名称
     * 
     * @return String 类型名称
     */
    public String getTypeName()
    {
        return ReportCommonResource.TREESELECT;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public TreeSelectRule clone(boolean isSimple)
    {
        TreeSelectRule treeSelectRule = new TreeSelectRule();
        clone(treeSelectRule, isSimple);
        treeSelectRule.setDataSourceType(dataSourceType);
        treeSelectRule.setAttrFlag(attrFlag);
        treeSelectRule.setFixedValue(fixedValue);
        if (dtable != null)
        {
            treeSelectRule.setDtable(isClient() ? dtable : dtable.clone(true));
        }
        treeSelectRule.setFilterCond(filterCond);
        treeSelectRule.setFormatType(formatType);
        if (levelFields != null && !levelFields.isEmpty())
        {
            HashSet<LevelField> set = new HashSet<LevelField>();
            for (LevelField levelField : levelFields)
            {
                set.add(levelField.clone(isSimple));
            }
            treeSelectRule.setLevelFields(set);
        }
        if (codeorPrimaryField != null)
        {
            treeSelectRule.setCodeorPrimaryField(isClient() ? codeorPrimaryField
                : codeorPrimaryField.clone(true));
        }
        if (parentPrimaryField != null)
        {
            treeSelectRule.setParentPrimaryField(isClient() ? parentPrimaryField
                : parentPrimaryField.clone(true));
        }
        if (viewField != null)
        {
            treeSelectRule.setViewField(isClient() ? viewField : viewField.clone(true));
        }
        treeSelectRule.setLevelNum(levelNum);
        treeSelectRule.setSortSelect(sortSelect);
        treeSelectRule.setSortType(sortType);

        return treeSelectRule;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.putAll(super.getJsonObj());
        // 数据源类型
        params.put(ParamCons.DATASOURCETYPE, dataSourceType);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 固定取值
        params.put(ParamCons.FIXEDVALUE, fixedValue);
        // 数据表
        if (dtable != null)
        {
            params.put(ParamCons.DTABLE, dtable.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        // 构造方式
        params.put(ParamCons.FORMATTYPE, formatType);
        // 分级字段
        if (levelFields != null && !levelFields.isEmpty())
        {
            ArrayList<HashMap<String, Object>> levelFieldJ = new ArrayList<HashMap<String, Object>>();
            for (LevelField levelField : levelFields)
            {
                levelFieldJ.add(levelField.getJsonObj());
            }
            params.put(ParamCons.LEVELFIELDS, levelFieldJ);
        }
        // 编码或关键字段
        if (codeorPrimaryField != null)
        {
            params.put(ParamCons.CODEORPRIMARYFIELD, codeorPrimaryField.getJsonObj());
        }
        // 父关键字段
        if (parentPrimaryField != null)
        {
            params.put(ParamCons.PARENTPRIMARYFIELD, parentPrimaryField.getJsonObj());
        }
        // 显示字段
        if (viewField != null)
        {
            params.put(ParamCons.VIEWFIELD, viewField.getJsonObj());
        }
        // 各级位数
        params.put(ParamCons.LEVELNUM, levelNum);
        // 排序选择
        params.put(ParamCons.SORTSELECT, sortSelect);
        // 排序类型
        params.put(ParamCons.SORTTYPE, sortType);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return TreeSelectRule 树型选择对象
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public TreeSelectRule convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        super.convetJsonToObj(paramsMap);
        // 数据源类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.DATASOURCETYPE);
        if (shortValue != -1)
        {
            setDataSourceType(shortValue);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 固定取值
        stringValue = HashMapTools.getString(paramsMap, ParamCons.FIXEDVALUE);
        if (stringValue != null)
        {
            setFixedValue(stringValue);
        }
        // 数据表
        Object objValue = paramsMap.get(ParamCons.DTABLE);
        if (objValue != null)
        {
            DataTable dtable = null;
            if (objValue instanceof HashMap)
            {
                dtable = new DataTable();
                dtable.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDtable(dtable);
        }
        // 筛选条件
        stringValue = HashMapTools.getString(paramsMap, ParamCons.FILTERCOND);
        if (stringValue != null)
        {
            setFilterCond(stringValue);
        }
        // 构造方式
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.FORMATTYPE);
        if (shortValue != -1)
        {
            setFormatType(shortValue);
        }
        // 分级字段
        objValue = paramsMap.get(ParamCons.LEVELFIELDS);
        if (objValue != null)
        {
            HashSet<LevelField> levelFields = null;
            if (objValue instanceof ArrayList)
            {
                LevelField levelField;
                levelFields = new HashSet<LevelField>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        levelField = new LevelField();
                        levelField.convetJsonToObj((HashMap<String, Object>)obj);
                        levelFields.add(levelField);
                    }
                }
            }
            setLevelFields(levelFields);
        }
        // 编码或关键字段
        objValue = paramsMap.get(ParamCons.CODEORPRIMARYFIELD);
        if (objValue != null)
        {
            DataField codeorPrimaryField = null;
            if (objValue instanceof HashMap)
            {
                codeorPrimaryField = new DataField();
                codeorPrimaryField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setCodeorPrimaryField(codeorPrimaryField);
        }
        // 父关键字段
        objValue = paramsMap.get(ParamCons.PARENTPRIMARYFIELD);
        if (objValue != null)
        {
            DataField parentPrimaryField = null;
            if (objValue instanceof HashMap)
            {
                parentPrimaryField = new DataField();
                parentPrimaryField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setParentPrimaryField(parentPrimaryField);
        }
        // 显示字段
        objValue = paramsMap.get(ParamCons.VIEWFIELD);
        if (objValue != null)
        {
            DataField viewField = null;
            if (objValue instanceof HashMap)
            {
                viewField = new DataField();
                viewField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setViewField(viewField);
        }
        // 各级位数
        stringValue = HashMapTools.getString(paramsMap, ParamCons.LEVELNUM);
        if (stringValue != null)
        {
            setLevelNum(stringValue);
        }
        // 排序选择
        stringValue = HashMapTools.getString(paramsMap, ParamCons.SORTSELECT);
        if (stringValue != null)
        {
            setSortSelect(stringValue);
        }
        // 排序类型
        stringValue = HashMapTools.getString(paramsMap, ParamCons.SORTTYPE);
        if (stringValue != null)
        {
            setSortType(stringValue);
        }

        return this;
    }
}