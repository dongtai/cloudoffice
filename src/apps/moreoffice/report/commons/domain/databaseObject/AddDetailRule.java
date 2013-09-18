package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 回写：补充明细
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
public class AddDetailRule extends WriteTableRule
{
    // 序列化ID
    private static final long serialVersionUID = -587865530122726622L;
    // 选择的模板
    private Template template;
    // 明细表
    private RTable rtable;
    // 主表条件
    private String primaryTableCond;
    // 筛选条件
    private String filterCond;
    // 回写项
    private Set<WriteModeItem> writeModeItems;

    /**
     * 默认构造器
     */
    public AddDetailRule()
    {
        super();
        setType(TableRuleCons.ADDDETAIL_RULE);
    }

    /**
     * @return 返回 template
     */
    public Template getTemplate()
    {
        return template;
    }

    /**
     * @param template 设置 template
     */
    public void setTemplate(Template template)
    {
        this.template = template;
    }

    /**
     * @return 返回 rtable
     */
    public RTable getRtable()
    {
        return rtable;
    }

    /**
     * @param rtable 设置 rtable
     */
    public void setRtable(RTable rtable)
    {
        this.rtable = rtable;
    }

    /**
     * @return 返回 primaryTableCond
     */
    public String getPrimaryTableCond()
    {
        return primaryTableCond;
    }

    /**
     * @param primaryTableCond 设置 primaryTableCond
     */
    public void setPrimaryTableCond(String primaryTableCond)
    {
        this.primaryTableCond = primaryTableCond;
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
     * @return 返回 writeModeItems
     */
    public Set<WriteModeItem> getWriteModeItems()
    {
        return writeModeItems;
    }

    /**
     * @param writeModeItems 设置 writeModeItems
     */
    public void setWriteModeItems(Set<WriteModeItem> writeModeItems)
    {
        this.writeModeItems = writeModeItems;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public AddDetailRule clone(boolean isSimple)
    {
        AddDetailRule addDetailRule = new AddDetailRule();
        clone(addDetailRule, isSimple);
        addDetailRule.setTemplate(isClient() ? template : template.clone(true));
        addDetailRule.setRtable(isClient() ? rtable : rtable.clone(true));
        addDetailRule.setPrimaryTableCond(primaryTableCond);
        addDetailRule.setFilterCond(filterCond);
        if (writeModeItems != null && !writeModeItems.isEmpty())
        {
            HashSet<WriteModeItem> set = new HashSet<WriteModeItem>();
            for (WriteModeItem writeModeItem : writeModeItems)
            {
                set.add(writeModeItem.clone(isSimple));
            }
            addDetailRule.setWriteModeItems(set);
        }

        return addDetailRule;
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
        // 选择的模板
        if (template != null)
        {
            params.put(ParamCons.TEMPLATENAME, template.getJsonObj());
        }
        // 明细表
        if (rtable != null)
        {
            params.put(ParamCons.RTABLE, rtable.getJsonObj());
        }
        // 主表条件
        params.put(ParamCons.PRIMARYTABLECOND, primaryTableCond);
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        // 回写项
        if (writeModeItems != null && !writeModeItems.isEmpty())
        {
            ArrayList<HashMap<String, Object>> writeModeItemJ = new ArrayList<HashMap<String, Object>>();
            for (WriteModeItem writeModeItem : writeModeItems)
            {
                writeModeItemJ.add(writeModeItem.getJsonObj());
            }
            params.put(ParamCons.WRITEMODEITEMS, writeModeItemJ);
        }

        return params;
    }
}