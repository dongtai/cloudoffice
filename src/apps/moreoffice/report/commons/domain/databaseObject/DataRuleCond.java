package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 数据规范条件
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-4
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataRuleCond extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -4961725073654826672L;
    // id
    private Long id;
    // 数据规范
    private DataRule dataRule;
    // 数据规范条件
    private String dataRuleCond;
    // 父单元格
    private RField parentField;
    // 基本属性集
    private Long attrFlag;

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
     * @return 返回 dataRule
     */
    public DataRule getDataRule()
    {
        return dataRule;
    }

    /**
     * @param dataRule 设置 dataRule
     */
    public void setDataRule(DataRule dataRule)
    {
        this.dataRule = dataRule;
    }

    /**
     * @return 返回 dataRuleCond
     */
    public String getDataRuleCond()
    {
        return dataRuleCond;
    }

    /**
     * @param dataRuleCond 设置 dataRuleCond
     */
    public void setDataRuleCond(String dataRuleCond)
    {
        this.dataRuleCond = dataRuleCond;
    }

    /**
     * @return 返回 parentField
     */
    public RField getParentField()
    {
        return parentField;
    }

    /**
     * @param parentField 设置 parentField
     */
    public void setParentField(RField parentField)
    {
        this.parentField = parentField;
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
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DataRuleCond clone(boolean isSimple)
    {
        DataRuleCond dataRuleCond = new DataRuleCond();
        dataRuleCond.setId(id);
        dataRuleCond.setDataRule(isClient() ? dataRule : dataRule.clone(true));
        dataRuleCond.setDataRuleCond(this.dataRuleCond);
        if (parentField != null)
        {
            dataRuleCond.setParentField(isClient() ? parentField : parentField.clone(true));
        }
        dataRuleCond.setAttrFlag(attrFlag);

        return dataRuleCond;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataRuleCond)
        {
            if (id != null && id.equals(((DataRuleCond)obj).getId()))
            {
                return true;
            }
        }

        return super.equals(obj);
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
        // 数据规范
        if (dataRule != null)
        {
            params.put(ParamCons.DATARULE, dataRule.getJsonObj());
        }
        // 数据规范条件
        params.put(ParamCons.DATARULECOND, dataRuleCond);
        // 父单元格
        if (parentField != null)
        {
            params.put(ParamCons.PARENTFIELD, parentField.getJsonObj());
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataRuleCond 数据规范条件对象
     */
    @ SuppressWarnings("unchecked")
    public DataRuleCond convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 数据规范
        Object objValue = paramsMap.get(ParamCons.DATARULE);
        if (objValue != null)
        {
            DataRule dataRule = null;
            if (objValue instanceof HashMap)
            {
                int type = HashMapTools.getInt((HashMap<String, Object>)objValue, ParamCons.TYPE);
                switch (type)
                {
                    case DataRuleCons.SYSVAR:
                        dataRule = new SysVarRule();
                        break;
                    case DataRuleCons.AUTONUM:
                        dataRule = new AutoNumRule();
                        break;
                    case DataRuleCons.DOWNLIST:
                        dataRule = new DownListRule();
                        break;
                    case DataRuleCons.TREESELECT:
                        dataRule = new TreeSelectRule();
                        break;
                    case DataRuleCons.LISTSELECT:
                        dataRule = new ListSelectRule();
                        break;
                    default:
                        break;
                }
                if (dataRule != null)
                {
                    dataRule.convetJsonToObj((HashMap<String, Object>)objValue);
                }
            }
            setDataRule(dataRule);
        }
        // 数据规范条件
        stringValue = HashMapTools.getString(paramsMap, ParamCons.DATARULECOND);
        if (stringValue != null)
        {
            setDataRuleCond(stringValue);
        }
        // 父单元格
        objValue = paramsMap.get(ParamCons.PARENTFIELD);
        if (objValue != null)
        {
            RField parentField = null;
            if (objValue instanceof HashMap)
            {
                parentField = new RField();
                parentField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setParentField(parentField);
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