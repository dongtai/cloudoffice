package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 超链接详细信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-3
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class HyperLinkInfo extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -5807148029727229359L;
    // id
    private Long id;
    // 本报表字段
    private RField currentField;
    // 目标模板上的字段
    private RField targetField;
    // URL参数
    private String urlParam;
    // 基本属性集
    private Long attrFlag;

    // HyperLink
    private transient HyperLink hyperLink;

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
     * @return 返回 currentField
     */
    public RField getCurrentField()
    {
        return currentField;
    }

    /**
     * @param currentField 设置 currentField
     */
    public void setCurrentField(RField currentField)
    {
        this.currentField = currentField;
    }

    /**
     * @return 返回 targetField
     */
    public RField getTargetField()
    {
        return targetField;
    }

    /**
     * @param targetField 设置 targetField
     */
    public void setTargetField(RField targetField)
    {
        this.targetField = targetField;
    }

    /**
     * @return 返回 urlParam
     */
    public String getUrlParam()
    {
        return urlParam;
    }

    /**
     * @param urlParam 设置 urlParam
     */
    public void setUrlParam(String urlParam)
    {
        this.urlParam = urlParam;
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
     * @return 返回 hyperLink
     */
    public HyperLink getHyperLink()
    {
        return hyperLink;
    }

    /**
     * @param hyperLink 设置 hyperLink
     */
    public void setHyperLink(HyperLink hyperLink)
    {
        this.hyperLink = hyperLink;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public HyperLinkInfo clone(boolean isSimple)
    {
        HyperLinkInfo hyperLinkInfo = new HyperLinkInfo();
        hyperLinkInfo.setId(id);
        hyperLinkInfo.setCurrentField(currentField.clone(true));
        hyperLinkInfo.setTargetField(targetField.clone(true));
        hyperLinkInfo.setUrlParam(urlParam);
        hyperLinkInfo.setAttrFlag(attrFlag);

        return hyperLinkInfo;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof HyperLinkInfo)
        {
            if (id != null && id.equals(((HyperLinkInfo)obj).getId()))
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
        // 本报表字段
        if (currentField != null)
        {
            params.put(ParamCons.CURRENTFIELD, currentField.getJsonObj());
        }
        // 目标模板上的字段
        if (targetField != null)
        {
            params.put(ParamCons.TARGETFIELD, targetField.getJsonObj());
        }
        // URL参数
        params.put(ParamCons.URLPARAM, urlParam);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return HyperLinkInfo 超链接信息对象
     */
    @ SuppressWarnings("unchecked")
    public HyperLinkInfo convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 本报表字段
        Object objValue = paramsMap.get(ParamCons.CURRENTFIELD);
        if (objValue != null)
        {
            RField currentField = null;
            if (objValue instanceof HashMap)
            {
                currentField = new RField();
                currentField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setCurrentField(currentField);
        }
        // 目标模板上的字段
        objValue = paramsMap.get(ParamCons.TARGETFIELD);
        if (objValue != null)
        {
            RField targetField = null;
            if (objValue instanceof HashMap)
            {
                targetField = new RField();
                targetField.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setTargetField(targetField);
        }
        // URL参数
        stringValue = HashMapTools.getString(paramsMap, ParamCons.URLPARAM);
        if (stringValue != null)
        {
            setUrlParam(stringValue);
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