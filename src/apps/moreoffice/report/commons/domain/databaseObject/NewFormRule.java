package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 回写：新建表单
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
public class NewFormRule extends WriteTableRule
{
    // 序列化ID
    private static final long serialVersionUID = -2111926380612176412L;
    // 要新建的报表
    private Template template;
    // 筛选条件
    private String filterCond;
    // 回写方式
    private Set<WriteMode> writeModes;
    // 基本属性集
    private Long attrFlag;

    /**
     * 默认构造器
     */
    public NewFormRule()
    {
        super();
        setType(TableRuleCons.NEWFORM_RULE);
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
     * @return 返回 writeModes
     */
    public Set<WriteMode> getWriteModes()
    {
        return writeModes;
    }

    /**
     * @param writeModes 设置 writeModes
     */
    public void setWriteModes(Set<WriteMode> writeModes)
    {
        this.writeModes = writeModes;
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
     *        TableRuleCons.OPENMODIFY:新建完毕后立即打开修改
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
     *        TableRuleCons.OPENMODIFY:新建完毕后立即打开修改
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public NewFormRule clone(boolean isSimple)
    {
        NewFormRule newFormRule = new NewFormRule();
        clone(newFormRule, isSimple);
        newFormRule.setTemplate(isClient() ? template : template.clone(true));
        newFormRule.setFilterCond(filterCond);
        if (writeModes != null && !writeModes.isEmpty())
        {
            HashSet<WriteMode> set = new HashSet<WriteMode>();
            for (WriteMode writeMode : writeModes)
            {
                set.add(writeMode.clone(isSimple));
            }
            newFormRule.setWriteModes(set);
        }
        newFormRule.setAttrFlag(attrFlag);
        return newFormRule;
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
        // 要新建的报表
        if (template != null)
        {
            params.put(ParamCons.TEMPLATE, template.getJsonObj());
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        // 回写方式
        if (writeModes != null && !writeModes.isEmpty())
        {
            ArrayList<HashMap<String, Object>> writeModeJ = new ArrayList<HashMap<String, Object>>();
            for (WriteMode writeMode : writeModes)
            {
                writeModeJ.add(writeMode.getJsonObj());
            }
            params.put(ParamCons.WRITEMODES, writeModeJ);
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }
}