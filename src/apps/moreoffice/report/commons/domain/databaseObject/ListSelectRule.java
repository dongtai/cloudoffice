package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范：列表选择
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
public class ListSelectRule extends DataRule
{
    // 序列化ID
    private static final long serialVersionUID = 9010302361313001661L;
    /**
     * 基本属性集
     * 0:列表返回结果是否可改
     * 1:重复数据只显示一次
     */
    private Long attrFlag;
    // 数据源
    private DataTable dtable;
    // 筛选条件
    private String filterCond;
    // 分类
    private Short sortType;
    // 数据项
    private Set<ListSelectItem> listSelectItems;

    /**
     * 构造器
     */
    public ListSelectRule()
    {
        super();
        setType(DataRuleCons.LISTSELECT);
        setAttrFlag((long)0);
        setSortType((short)TableCons.DEFAULT);
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
        return ReportCommonResource.LISTSELECT;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public ListSelectRule clone(boolean isSimple)
    {
        ListSelectRule listSelectRule = new ListSelectRule();
        clone(listSelectRule, isSimple);
        listSelectRule.setAttrFlag(attrFlag);
        if (dtable != null)
        {
            listSelectRule.setDtable(isClient() ? dtable : dtable.clone(true));
        }
        listSelectRule.setFilterCond(filterCond);
        listSelectRule.setSortType(sortType);
        if (listSelectItems != null && !listSelectItems.isEmpty())
        {
            HashSet<ListSelectItem> set = new HashSet<ListSelectItem>();
            for (ListSelectItem listSelectItem : listSelectItems)
            {
                set.add(listSelectItem.clone(isSimple));
            }
            listSelectRule.setListSelectItems(set);
        }

        return listSelectRule;
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
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 数据源
        if (dtable != null)
        {
            params.put(ParamCons.DTABLE, dtable.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        // 分类
        params.put(ParamCons.SORTTYPE, sortType);
        // 数据项
        if (listSelectItems != null && !listSelectItems.isEmpty())
        {
            ArrayList<HashMap<String, Object>> listSelectItemJ = new ArrayList<HashMap<String, Object>>();
            for (ListSelectItem listSelectItem : listSelectItems)
            {
                listSelectItemJ.add(listSelectItem.getJsonObj());
            }
            params.put(ParamCons.LISTSELECTITEMS, listSelectItemJ);
        }

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return ListSelectRule 列表选择对象
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public ListSelectRule convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        super.convetJsonToObj(paramsMap);
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 数据源
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
        // 分类
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.SORTTYPE);
        if (shortValue != -1)
        {
            setSortType(shortValue);
        }
        // 数据项
        objValue = paramsMap.get(ParamCons.LISTSELECTITEMS);
        if (objValue != null)
        {
            HashSet<ListSelectItem> listSelectItems = null;
            if (objValue instanceof ArrayList)
            {
                ListSelectItem listSelectItem;
                listSelectItems = new HashSet<ListSelectItem>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        listSelectItem = new ListSelectItem();
                        listSelectItem.convetJsonToObj((HashMap<String, Object>)obj);
                        listSelectItems.add(listSelectItem);
                    }
                }
            }
            setListSelectItems(listSelectItems);
        }

        return this;
    }
}