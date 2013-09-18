package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.Date;
import java.util.HashMap;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataTypeCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据类型
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
public class DataType extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -1978637016040706049L;
    // id
    private Long id;
    // 数据类型名称
    private String name;
    // 基本类型
    private Short basicType;
    // 基本属性集
    private Long attrFlag;
    // 限定长度
    private Short limitLength;
    // 小数位数
    private Short decimalDigit;
    // 匹配值
    private String matchPattern;
    // 说明
    private String description;
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
    private String modifierName;

    /**
     * 默认构造器
     */
    public DataType()
    {
        attrFlag = (long)0;
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
     * @return 返回 basicType
     */
    public Short getBasicType()
    {
        return basicType;
    }

    /**
     * @param basicType 设置 basicType
     */
    public void setBasicType(Short basicType)
    {
        this.basicType = basicType;
    }

    /**
     * 得到基本类型名称
     * 
     * @return String 基本类型名称
     */
    public String getBasicTypeName()
    {
        switch (basicType)
        {
            case DataTypeCons.NUMBER_TYPE:
                return ReportCommonResource.NUMBER;
            case DataTypeCons.DATE_TYPE:
                return ReportCommonResource.DATE;
            case DataTypeCons.PICTURE_TYPE:
                return ReportCommonResource.PICTURE;
            case DataTypeCons.FILE_TYPE:
                return ReportCommonResource.FILE;
            default:
                return ReportCommonResource.TEXT;
        }
    }

    /**
     * 设置基本类型名称
     * 
     * @param name 基本类型名称
     */
    public void setBasicTypeName(String name)
    {
        if (name == null || name.length() < 0)
        {
            return;
        }
        if (name.equals(ReportCommonResource.TEXT))
        {
            basicType = DataTypeCons.TEXT_TYPE;
        }
        else if (name.equals(ReportCommonResource.NUMBER))
        {
            basicType = DataTypeCons.NUMBER_TYPE;
        }
        else if (name.equals(ReportCommonResource.DATE))
        {
            basicType = DataTypeCons.DATE_TYPE;
        }
        else if (name.equals(ReportCommonResource.PICTURE))
        {
            basicType = DataTypeCons.PICTURE_TYPE;
        }
        else if (name.equals(ReportCommonResource.FILE))
        {
            basicType = DataTypeCons.FILE_TYPE;
        }
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
     *        DataTypeCons.ALLOWOTHERMODIFY:是否允许其它设计者修改
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
     *        DataTypeCons.ALLOWOTHERMODIFY:是否允许其它设计者修改
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
    }

    /**
     * @return 返回 limitLength
     */
    public Short getLimitLength()
    {
        return limitLength;
    }

    /**
     * @param limitLength 设置 limitLength
     */
    public void setLimitLength(Short limitLength)
    {
        this.limitLength = limitLength;
    }

    /**
     * @return 返回 decimalDigit
     */
    public Short getDecimalDigit()
    {
        return decimalDigit;
    }

    /**
     * @param decimalDigit 设置 decimalDigit
     */
    public void setDecimalDigit(Short decimalDigit)
    {
        this.decimalDigit = decimalDigit;
    }

    /**
     * @return 返回 matchPattern
     */
    public String getMatchPattern()
    {
        return matchPattern;
    }

    /**
     * @param matchPattern 设置 matchPattern
     */
    public void setMatchPattern(String matchPattern)
    {
        this.matchPattern = matchPattern;
    }

    /**
     * @return 返回 description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description 设置 description
     */
    public void setDescription(String description)
    {
        this.description = description;
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
     * @return 返回 modifierName
     */
    public String getModifierName()
    {
        return modifierName;
    }

    /**
     * @param modifierName 设置 modifierName
     */
    public void setModifierName(String modifierName)
    {
        this.modifierName = modifierName;
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
        // 数据类型名称
        params.put(ParamCons.NAME, name);
        // 基本类型
        params.put(ParamCons.BASICTYPE, basicType);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 限定长度
        params.put(ParamCons.LIMITLENG, limitLength);
        // 小数位数
        params.put(ParamCons.DECIMALDIGIT, decimalDigit);
        // 匹配值
        params.put(ParamCons.MATCHPATTERN, matchPattern);
        // 说明
        params.put(ParamCons.DESCRIPTION, description);
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
     * @return DataType 数据类型对象
     */
    public DataType convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 数据类型名称
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }
        // 基本类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.BASICTYPE);
        if (shortValue != -1)
        {
            setBasicType(shortValue);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 限定长度
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.LIMITLENG);
        if (shortValue != -1)
        {
            setLimitLength(shortValue);
        }
        // 小数位数
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.DECIMALDIGIT);
        if (shortValue != -1)
        {
            setDecimalDigit(shortValue);
        }
        // 匹配值
        stringValue = HashMapTools.getString(paramsMap, ParamCons.MATCHPATTERN);
        if (stringValue != null)
        {
            setMatchPattern(stringValue);
        }
        // 说明
        stringValue = HashMapTools.getString(paramsMap, ParamCons.DESCRIPTION);
        if (stringValue != null)
        {
            setDescription(stringValue);
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