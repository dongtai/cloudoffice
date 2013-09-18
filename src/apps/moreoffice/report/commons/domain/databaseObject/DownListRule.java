package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范：下拉列表
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
public class DownListRule extends DataRule
{
    // 序列化ID
    private static final long serialVersionUID = 6632180258446777930L;
    // 数据源类型
    private Short dataSourceType;
    // 基本属性集
    private Long attrFlag;
    // 固定取值
    private String fixedValue;
    // 来自数据表时的表名
    private DataTable dtable;
    // 来自数据表时的字段名
    private DataField dfield;
    // 来自数据表时的排序字段名
    private DataField sortField;
    // 筛选条件
    private String filterCond;

    /**
     * 构造器
     */
    public DownListRule()
    {
        super();
        setType(DataRuleCons.DOWNLIST);
        setDataSourceType(DataRuleCons.DATASOURCE_FIXED);
        setAttrFlag((long)0);
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
     *        DataRuleCons.CANEDIT:是否能编辑
     *        DataRuleCons.SORT:排序
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
     *        DataRuleCons.CANEDIT:是否能编辑
     *        DataRuleCons.SORT:排序
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
     * @return 返回 sortField
     */
    public DataField getSortField()
    {
        return sortField;
    }

    /**
     * @param sortField 设置 sortField
     */
    public void setSortField(DataField sortField)
    {
        this.sortField = sortField;
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
     * 得到类型名称
     * 
     * @return String 类型名称
     */
    public String getTypeName()
    {
        return ReportCommonResource.DOWNLIST;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DownListRule clone(boolean isSimple)
    {
        DownListRule downListRule = new DownListRule();
        clone(downListRule, isSimple);
        downListRule.setDataSourceType(dataSourceType);
        downListRule.setAttrFlag(attrFlag);
        downListRule.setFixedValue(fixedValue);
        if (dtable != null)
        {
            downListRule.setDtable(isClient() ? dtable : dtable.clone(true));
        }
        if (dfield != null)
        {
            downListRule.setDfield(isClient() ? dfield : dfield.clone(true));
        }
        if (sortField != null)
        {
            downListRule.setSortField(isClient() ? sortField : sortField.clone(true));
        }
        downListRule.setFilterCond(filterCond);

        return downListRule;
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
        // 来自数据表时的表名
        if (dtable != null)
        {
            params.put(ParamCons.DTABLE, dtable.getJsonObj());
        }
        // 来自数据表时的字段名
        if (dfield != null)
        {
            params.put(ParamCons.DFIELD, dfield.getJsonObj());
        }
        // 来自数据表时的排序字段名
        if (sortField != null)
        {
            params.put(ParamCons.SORTFIELD, sortField.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DownListRule 下拉列表对象
     */
    @ SuppressWarnings("unchecked")
    public DownListRule convetJsonToObj(HashMap<String, Object> paramsMap)
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
        // 来自数据表时的表名
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
        // 来自数据表时的字段名
        objValue = paramsMap.get(ParamCons.DFIELD);
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
        // 来自数据表时的排序字段名
        objValue = paramsMap.get(ParamCons.SORTFIELD);
        if (objValue != null)
        {
            DataField sortfield = null;
            if (objValue instanceof HashMap)
            {
                sortfield = new DataField();
                sortfield.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setSortField(sortfield);
        }
        // 筛选条件
        stringValue = HashMapTools.getString(paramsMap, ParamCons.FILTERCOND);
        if (stringValue != null)
        {
            setFilterCond(stringValue);
        }

        return this;
    }
}