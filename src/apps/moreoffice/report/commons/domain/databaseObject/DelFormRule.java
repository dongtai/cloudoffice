package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 表间规则：删除表单
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
public class DelFormRule extends WriteTableRule
{
    // 序列化ID
    private static final long serialVersionUID = -886997896071023652L;
    // 需要删除的报表
    private Template template;
    // 筛选条件
    private String filterCond;

    /**
     * 默认构造器
     */
    public DelFormRule()
    {
        super();
        setType(TableRuleCons.DELFORM_RULE);
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
    public DelFormRule clone(boolean isSimple)
    {
        DelFormRule delFormRule = new DelFormRule();
        clone(delFormRule, isSimple);
        delFormRule.setTemplate(isClient() ? template : template.clone(true));
        delFormRule.setFilterCond(filterCond);
        return delFormRule;
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
        // 需要删除的报表
        if (template != null)
        {
            params.put(ParamCons.TEMPLATE, template.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);

        return params;
    }
}