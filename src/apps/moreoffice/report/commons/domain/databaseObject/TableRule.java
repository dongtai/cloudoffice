package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 表间规则
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
public class TableRule extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -4456622769022220129L;
    // id
    private Long id;
    // 名称
    private String name;
    // 类型
    private Short type;
    // 规则说明
    private String explain;
    // 位置
    private Long position;
    // 应用方式
    private Long appMode;
    // 公共属性集
    private Long commonAttrFlag;

    // 表间规则所在的模板
    private transient Template currentTemplate;

    /**
     * 默认构造器
     */
    public TableRule()
    {
        appMode = (long)0;
        commonAttrFlag = (long)0;
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
     * @return 返回 type
     */
    public Short getType()
    {
        return type;
    }

    /**
     * @param type 设置 type
     */
    public void setType(Short type)
    {
        this.type = type;
    }

    /**
     * @return 返回 explain
     */
    public String getExplain()
    {
        return explain;
    }

    /**
     * @param explain 设置 explain
     */
    public void setExplain(String explain)
    {
        this.explain = explain;
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
     * @return 返回 appMode
     */
    public Long getAppMode()
    {
        return appMode;
    }

    /**
     * @param appMode 设置 appMode
     */
    public void setAppMode(Long appMode)
    {
        this.appMode = appMode;
    }

    /**
     * 得到具体的应用方式
     * 
     * @param flag 应用方式标记
     * @return boolean 是否选择
     */
    public boolean getAppMode(int flag)
    {
        return DomainTools.isLongFlag(appMode, flag);
    }

    /**
     * 设置具体的应用方式
     * 
     * @param flag 应用方式标记
     * @param value 是否选择
     */
    public void setAppMode(int flag, boolean value)
    {
        appMode = DomainTools.setLongFlag(appMode, flag, value);
    }

    /**
     * @return 返回 commonAttrFlag
     */
    public Long getCommonAttrFlag()
    {
        return commonAttrFlag;
    }

    /**
     * @param commonAttrFlag 设置 commonAttrFlag
     */
    public void setCommonAttrFlag(Long commonAttrFlag)
    {
        this.commonAttrFlag = commonAttrFlag;
    }

    /**
     * 得到boolean属性
     * 
     * @param flag 标记位
     *        TableRuleCons.DEFAULTVALUETONULL:自动用默认值替换数据中的空值
     * @return boolean boolean属性
     */
    public boolean getCommonAttr(int flag)
    {
        return DomainTools.isLongFlag(commonAttrFlag, flag);
    }

    /**
     * 设置boolean属性
     * 
     * @param flag 标记位
     *        TableRuleCons.DEFAULTVALUETONULL:自动用默认值替换数据中的空值
     * @param value boolean属性
     */
    public void setCommonAttr(int flag, boolean value)
    {
        commonAttrFlag = DomainTools.setLongFlag(commonAttrFlag, flag, value);
    }

    /**
     * @return 返回 currentTemplate
     */
    public Template getCurrentTemplate()
    {
        return currentTemplate;
    }

    /**
     * @param currentTemplate 设置 currentTemplate
     */
    public void setCurrentTemplate(Template currentTemplate)
    {
        this.currentTemplate = currentTemplate;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public TableRule clone(boolean isSimple)
    {
        TableRule tableRule = new TableRule();
        clone(tableRule, isSimple);
        return tableRule;
    }

    /*
     * 父对象克隆
     */
    protected void clone(TableRule tableRule, boolean isSimple)
    {
        tableRule.setId(id);
        tableRule.setName(name);
        tableRule.setType(type);
        tableRule.setExplain(explain);
        tableRule.setPosition(position);
        tableRule.setAppMode(appMode);
        tableRule.setCommonAttrFlag(commonAttrFlag);
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof TableRule)
        {
            if (id != null && id.equals(((TableRule)obj).getId()))
            {
                return true;
            }
        }

        return super.equals(obj);
    }

    /**
     * 对话盒用
     */
    public String toString()
    {
        switch (type)
        {
            case TableRuleCons.READ_RULE:
                return ReportCommonResource.READ;
            case TableRuleCons.MODIFY_RULE:
                return ReportCommonResource.MODIFY;
            case TableRuleCons.ADDDETAIL_RULE:
                return ReportCommonResource.ADDDETAIL;
            case TableRuleCons.DELDETAIL_RULE:
                return ReportCommonResource.DELDETAIL;
            case TableRuleCons.NEWFORM_RULE:
                return ReportCommonResource.NEWFORM;
            case TableRuleCons.DELFORM_RULE:
                return ReportCommonResource.DELFORM;
            default:
                return "";
        }
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
        // 名称
        params.put(ParamCons.NAME, name);
        // 类型
        params.put(ParamCons.TYPE, type);
        // 规则说明
        params.put(ParamCons.EXPLAIN, explain);
        // 位置
        params.put(ParamCons.POSITION, position);
        // 应用方式
        params.put(ParamCons.APPMODE, appMode);
        // 公共属性集
        params.put(ParamCons.COMMONATTRFLAG, commonAttrFlag);

        return params;
    }
}