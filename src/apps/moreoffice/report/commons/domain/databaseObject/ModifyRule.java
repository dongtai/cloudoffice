package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 回写：修改
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
public class ModifyRule extends WriteTableRule
{
    // 序列化ID
    private static final long serialVersionUID = -1396172043500589979L;
    // 回写数据表
    private RTable rtable;
    // 筛选条件
    private String filterCond;
    // 回写项
    private Set<WriteModeItem> writeModeItems;

    /**
     * 默认构造器
     */
    public ModifyRule()
    {
        super();
        setType(TableRuleCons.MODIFY_RULE);
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
    public ModifyRule clone(boolean isSimple)
    {
        ModifyRule modifyRule = new ModifyRule();
        clone(modifyRule, isSimple);
        modifyRule.setRtable(isClient() ? rtable : rtable.clone(true));
        modifyRule.setFilterCond(filterCond);
        if (writeModeItems != null && !writeModeItems.isEmpty())
        {
            HashSet<WriteModeItem> set = new HashSet<WriteModeItem>();
            for (WriteModeItem writeModeItem : writeModeItems)
            {
                set.add(writeModeItem.clone(isSimple));
            }
            modifyRule.setWriteModeItems(set);
        }

        return modifyRule;
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
        // 回写数据表
        if (rtable != null)
        {
            params.put(ParamCons.RTABLE, rtable.getJsonObj());
        }
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