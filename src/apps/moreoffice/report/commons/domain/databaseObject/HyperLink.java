package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 超链接
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
public class HyperLink extends DataBaseObject
{
    //序列化ID
    private static final long serialVersionUID = 3575185685726053091L;
    // id
    private Long id;
    // 链接类型
    private Short type;
    // 链接到的模板
    private Template targetTemplate;
    // 外部URL
    private String urlPath;
    // 超链接信息
    private Set<HyperLinkInfo> hyperLinkInfos;

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
     * @return 返回 targetTemplate
     */
    public Template getTargetTemplate()
    {
        return targetTemplate;
    }

    /**
     * @param targetTemplate 设置 targetTemplate
     */
    public void setTargetTemplate(Template targetTemplate)
    {
        this.targetTemplate = targetTemplate;
    }

    /**
     * @return 返回 urlPath
     */
    public String getUrlPath()
    {
        return urlPath;
    }

    /**
     * @param urlPath 设置 urlPath
     */
    public void setUrlPath(String urlPath)
    {
        this.urlPath = urlPath;
    }

    /**
     * @return 返回 hyperLinkInfos
     */
    public Set<HyperLinkInfo> getHyperLinkInfos()
    {
        return hyperLinkInfos;
    }

    /**
     * @param hyperLinkInfos 设置 hyperLinkInfos
     */
    public void setHyperLinkInfos(Set<HyperLinkInfo> hyperLinkInfos)
    {
        this.hyperLinkInfos = hyperLinkInfos;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public HyperLink clone(boolean isSimple)
    {
        HyperLink hyperlink = new HyperLink();
        hyperlink.setId(id);
        hyperlink.setType(type);
        hyperlink.setTargetTemplate(targetTemplate.clone(true));
        hyperlink.setUrlPath(urlPath);
        if (hyperLinkInfos != null && !hyperLinkInfos.isEmpty())
        {
            HashSet<HyperLinkInfo> set = new HashSet<HyperLinkInfo>();
            for (HyperLinkInfo hyperlinkInfo : hyperLinkInfos)
            {
                set.add(hyperlinkInfo.clone(isSimple));
            }
            hyperlink.setHyperLinkInfos(set);
        }

        return hyperlink;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof HyperLink)
        {
            if (id != null && id.equals(((HyperLink)obj).getId()))
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
        // 链接类型
        params.put(ParamCons.TYPE, type);
        // 链接到的模板
        if (targetTemplate != null)
        {
            params.put(ParamCons.TEMPLATEID, targetTemplate.getId());
            params.put(ParamCons.TARGETTEMPLATENAME, targetTemplate.getName());
        }
        // 外部URL
        params.put(ParamCons.URLPATH, urlPath);
        // 超链接信息
        if (hyperLinkInfos != null && !hyperLinkInfos.isEmpty())
        {
            ArrayList<HashMap<String, Object>> hyperLinkInfoJ = new ArrayList<HashMap<String, Object>>();
            for (HyperLinkInfo hyperLinkInfo : hyperLinkInfos)
            {
                hyperLinkInfoJ.add(hyperLinkInfo.getJsonObj());
            }
            params.put(ParamCons.HYPERLINKINFOS, hyperLinkInfoJ);
        }

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return HyperLink 超链接对象
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public HyperLink convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 链接类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.TYPE);
        if (shortValue != -1)
        {
            setType(shortValue);
        }
        // 链接到的模板
        longValue = HashMapTools.getLong(paramsMap, ParamCons.TEMPLATEID);
        if (longValue != -1)
        {
            Template template = new Template();
            template.setId(longValue);
            setTargetTemplate(template);
        }
        // 外部URL
        stringValue = HashMapTools.getString(paramsMap, ParamCons.URLPATH);
        if (stringValue != null)
        {
            setUrlPath(stringValue);
        }
        // 超链接信息
        Object objValue = paramsMap.get(ParamCons.HYPERLINKINFOS);
        if (objValue != null)
        {
            HashSet<HyperLinkInfo> hyperLinkInfos = null;
            if (objValue instanceof ArrayList)
            {
                hyperLinkInfos = new HashSet<HyperLinkInfo>();
                HyperLinkInfo hyperLinkInfo;
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        hyperLinkInfo = new HyperLinkInfo();
                        hyperLinkInfo.convetJsonToObj((HashMap<String, Object>)obj);
                        hyperLinkInfos.add(hyperLinkInfo);
                    }
                }
            }
            setHyperLinkInfos(hyperLinkInfos);
        }

        return this;
    }
}