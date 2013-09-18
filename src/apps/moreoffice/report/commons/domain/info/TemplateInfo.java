package apps.moreoffice.report.commons.domain.info;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 提供给页面客户端的模板基本信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-9-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TemplateInfo implements SerializableAdapter
{
    // 序列化ID
    private static final long serialVersionUID = -8780528137709743904L;
    // 模板索引ID
    private long id;
    // 名称
    private String name;
    // 全路径
    private String path;
    // 状态
    private boolean status;
    // 创建者
    private String creatorName;
    // 修改者
    private String modifierName;
    // 最后修改时间
    private String lastModifyDate;

    /**
     * @return 返回 id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id 设置 id
     */
    public void setId(long id)
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
     * @return 返回 path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @param path 设置 path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * 得到模板所在分类
     * 
     * @return String 所在分类
     */
    public String getSort()
    {
        if (path != null && path.lastIndexOf("/") != -1)
        {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return "";
    }

    /**
     * @return 返回 status
     */
    public boolean isStatus()
    {
        return status;
    }

    /**
     * @param status 设置 status
     */
    public void setStatus(boolean status)
    {
        this.status = status;
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
     * @return 返回 lastModifyDate
     */
    public String getLastModifyDate()
    {
        return lastModifyDate;
    }

    /**
     * @param lastModifyDate 设置 lastModifyDate
     */
    public void setLastModifyDate(String lastModifyDate)
    {
        this.lastModifyDate = lastModifyDate;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // 模板索引ID
        params.put(ParamCons.ID, id);
        // 名称
        params.put(ParamCons.NAME, name);
        // 全路径
        params.put(ParamCons.PATH, path);
        // 状态
        params.put(ParamCons.STATUS, status);
        // 创建者
        params.put(ParamCons.CREATORNAME, creatorName);
        // 修改者
        params.put(ParamCons.MODIFIERNAME, modifierName);
        // 最后修改时间
        params.put(ParamCons.LASTMODIFYDATE, lastModifyDate);

        return params;
    }
}