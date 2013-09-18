package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 回写：删除明细
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
public class DelDetailRule extends WriteTableRule
{
    // 序列化ID
    private static final long serialVersionUID = 3533608388666614848L;
    // 模板
    private Template template;
    // 明细表
    private RTable rtable;
    // 筛选条件
    private String filterCond;

    /**
     * 默认构造器
     */
    public DelDetailRule()
    {
        super();
        setType(TableRuleCons.DELDETAIL_RULE);
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
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DelDetailRule clone(boolean isSimple)
    {
        DelDetailRule delDetailRule = new DelDetailRule();
        clone(delDetailRule, isSimple);
        delDetailRule.setTemplate(isClient() ? template : template.clone(true));
        delDetailRule.setRtable(isClient() ? rtable : rtable.clone(true));
        delDetailRule.setFilterCond(filterCond);

        return delDetailRule;
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
        // 模板
        if (template != null)
        {
            params.put(ParamCons.TEMPLATE, template.getJsonObj());
        }
        // 明细表
        if (rtable != null)
        {
            params.put(ParamCons.RTABLE, rtable.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        
        return params;
    }
}