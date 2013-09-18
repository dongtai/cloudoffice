package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范：自动编号
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
public class AutoNumRule extends DataRule
{
    // 序列化ID
    private static final long serialVersionUID = 6617041656261900323L;
    // 编号格式
    private String format;
    // 顺序号
    private Long numbers;
    // 基本属性集
    private Long attrFlag;

    /**
     * 构造器
     */
    public AutoNumRule()
    {
        super();
        setType(DataRuleCons.AUTONUM);
        setNumbers((long)0);
        setAttrFlag((long)0);
    }

    /**
     * @return 返回 format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * @param format 设置 format
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * @return 返回 numbers
     */
    public Long getNumbers()
    {
        return numbers;
    }

    /**
     * @param numbers 设置 numbers
     */
    public void setNumbers(Long numbers)
    {
        this.numbers = numbers;
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
     *        DataRuleCons.CREATEATSAVE:保存时产生
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
     *        DataRuleCons.CREATEATSAVE:保存时产生
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
    }

    /**
     * 得到类型名称
     * 
     * @return String 类型名称
     */
    public String getTypeName()
    {
        return ReportCommonResource.AUTONUM;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public AutoNumRule clone(boolean isSimple)
    {
        AutoNumRule autoNumRule = new AutoNumRule();
        clone(autoNumRule, isSimple);
        autoNumRule.setFormat(format);
        autoNumRule.setNumbers(numbers);
        autoNumRule.setAttrFlag(attrFlag);

        return autoNumRule;
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
        // 编号格式
        params.put(ParamCons.FORMAT, format);
        // 顺序号
        params.put(ParamCons.NUMBERS, numbers);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return AutoNumRule 自动编号对象
     */
    public AutoNumRule convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        super.convetJsonToObj(paramsMap);
        // 编号格式
        stringValue = HashMapTools.getString(paramsMap, ParamCons.FORMAT);
        if (stringValue != null)
        {
            setFormat(stringValue);
        }
        // 顺序号
        longValue = HashMapTools.getLong(paramsMap, ParamCons.NUMBERS);
        if (longValue != -1)
        {
            setNumbers(longValue);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }

        return this;
    }
}