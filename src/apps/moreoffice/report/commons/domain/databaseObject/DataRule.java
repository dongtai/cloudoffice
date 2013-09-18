package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.interfaces.IReportService;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范
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
public class DataRule extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -8649701701598953820L;
    // ID
    private Long id;
    // 数据规范名称
    private String name;
    // 数据规范类型
    private Short type;
    // 是否允许其他设计者修改
    private Boolean allowOthersModify;
    // 是否系统预定义
    private Boolean systemDefined;
    // 创建者ID
    private Long creatorId;
    // 创建时间
    private Date createDate;
    // 修改者ID
    private Long modifierId;
    // 修改时间
    private Date modifyDate;

    // 创建者
    private String creatorName;
    // 修改者
    private String modifyName;

    // 引用DataRule的DataRuleCond集合
    private transient Set<DataRuleCond> dataRuleConds;

    /**
     * 默认构造器
     */
    public DataRule()
    {
        allowOthersModify = false;
        systemDefined = false;
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
     * 得到类型名称
     * 
     * @return String 类型名称
     */
    public String getTypeName()
    {
        return ReportCommonResource.SYSVAR;
    }

    /**
     * @return 返回 allowOthersModify
     */
    public Boolean getAllowOthersModify()
    {
        return allowOthersModify;
    }

    /**
     * @param allowOthersModify 设置 allowOthersModify
     */
    public void setAllowOthersModify(Boolean allowOthersModify)
    {
        this.allowOthersModify = allowOthersModify;
    }

    /**
     * @return 返回 systemDefined
     */
    public Boolean getSystemDefined()
    {
        return systemDefined;
    }

    /**
     * @param systemDefined 设置 systemDefined
     */
    public void setSystemDefined(Boolean systemDefined)
    {
        this.systemDefined = systemDefined;
    }

    /**
     * @return String 返回系统预定义字符串
     */
    public String getSystemDefinedStr()
    {
        if (getSystemDefined())
        {
            return ReportCommonResource.SYSTEMDEFINED;
        }
        return "";
    }

    /**
     * @return 返回 creatorId
     */
    public Long getCreatorId()
    {
        return creatorId;
    }

    /**
     * @param creatorId 设置 creatorId
     */
    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    /**
     * @return 返回 createDate
     */
    public Date getCreateDate()
    {
        return createDate;
    }

    /**
     * @param createDate 设置 createDate
     */
    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    /**
     * @return 返回 modifierId
     */
    public Long getModifierId()
    {
        return modifierId;
    }

    /**
     * @param modifierId 设置 modifierId
     */
    public void setModifierId(Long modifierId)
    {
        this.modifierId = modifierId;
    }

    /**
     * @return 返回 modifyDate
     */
    public Date getModifyDate()
    {
        return modifyDate;
    }

    /**
     * @param modifyDate 设置 modifyDate
     */
    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    /**
     * @return 返回 creatorName
     */
    public String getCreatorName()
    {
        return creatorName;
    }

    /**
     * @param creatorName 设置 creatorName
     */
    public void setCreatorName(String creatorName)
    {
        this.creatorName = creatorName;
    }

    /**
     * @return 返回 modifyName
     */
    public String getModifyName()
    {
        return modifyName;
    }

    /**
     * @param modifyName 设置 modifyName
     */
    public void setModifyName(String modifyName)
    {
        this.modifyName = modifyName;
    }

    /**
     * @return 返回 dataRuleConds
     */
    public Set<DataRuleCond> getDataRuleConds()
    {
        return dataRuleConds;
    }

    /**
     * @param dataRuleConds 设置 dataRuleConds
     */
    public void setDataRuleConds(Set<DataRuleCond> dataRuleConds)
    {
        this.dataRuleConds = dataRuleConds;
    }

    /**
     * 得到数据规范数据
     * 
     * @param service 报表服务
     * @return Object 数据
     */
    public Result getData(IReportService service)
    {
        if (service != null)
        {
            return service.getDataRuleData(id);
        }
        return null;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DataRule clone(boolean isSimple)
    {
        DataRule dataRule = new DataRule();
        clone(dataRule, isSimple);
        return dataRule;
    }

    /*
     * 父对象克隆
     */
    protected void clone(DataRule dataRule, boolean isSimple)
    {
        dataRule.setId(id);
        dataRule.setName(name);
        dataRule.setType(type);
        dataRule.setAllowOthersModify(allowOthersModify);
        dataRule.setSystemDefined(systemDefined);
        dataRule.setCreatorId(creatorId);
        dataRule.setCreateDate(createDate);
        dataRule.setModifierId(modifierId);
        dataRule.setModifyDate(modifyDate);
        dataRule.setCreatorName(creatorName);
        dataRule.setModifyName(modifyName);
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataRule)
        {
            if (id != null && id.equals(((DataRule)obj).getId()))
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
        // 数据规范名称
        params.put(ParamCons.NAME, name);
        // 数据规范类型
        params.put(ParamCons.TYPE, type);
        // 是否允许其他设计者修改
        params.put(ParamCons.ALLOWOTHERSMODIFY, allowOthersModify);
        // 是否系统预定义
        params.put(ParamCons.SYSTEMDEFINED, systemDefined);
        // 创建者ID
        params.put(ParamCons.CREATORID, creatorId);
        // 创建时间
        params.put(ParamCons.CREATEDATE, createDate);
        // 修改者ID
        params.put(ParamCons.MODIFIERID, modifierId);
        // 修改时间
        params.put(ParamCons.MODIFYDATE, modifyDate);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataRule 数据规范对象
     */
    public DataRule convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 数据规范名称
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }
        // 数据规范类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.TYPE);
        if (shortValue != -1)
        {
            setType(shortValue);
        }
        // 是否允许其他设计者修改
        booleanValue = HashMapTools.getBoolean(paramsMap, ParamCons.ALLOWOTHERSMODIFY);
        if (booleanValue != null)
        {
            setAllowOthersModify(booleanValue);
        }
        // 是否系统预定义
        booleanValue = HashMapTools.getBoolean(paramsMap, ParamCons.SYSTEMDEFINED);
        if (booleanValue != null)
        {
            setSystemDefined(booleanValue);
        }
        // 创建者ID
        longValue = HashMapTools.getLong(paramsMap, ParamCons.CREATORID);
        if (longValue != -1)
        {
            setCreatorId(longValue);
        }
        // 创建时间
        dateValue = HashMapTools.getDate(paramsMap, ParamCons.CREATEDATE);
        if (dateValue != null)
        {
            setCreateDate(dateValue);
        }
        // 修改者ID
        longValue = HashMapTools.getLong(paramsMap, ParamCons.MODIFIERID);
        if (longValue != -1)
        {
            setModifierId(longValue);
        }
        // 修改时间
        dateValue = HashMapTools.getDate(paramsMap, ParamCons.MODIFYDATE);
        if (dateValue != null)
        {
            setModifyDate(dateValue);
        }

        return this;
    }
}