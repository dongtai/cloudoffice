package apps.moreoffice.report.commons.domain.info;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 基本信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-18
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class BaseInfo implements SerializableAdapter
{
    // 序列化ID
    private static final long serialVersionUID = 8048160598046312368L;
    // ID
    protected long id;
    // 名称
    protected String name;

    /**
     * 得到ID
     * 
     * @return long ID
     */
    public long getId()
    {
        return id;
    }

    /**
     * 设置ID
     * 
     * @param id ID
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * 得到名称
     * 
     * @return String 名称
     */
    public String getName()
    {
        return name;
    }

    /**
     * 设置名称
     * 
     * @param name 名称
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * 对话盒用
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
        // ID
        params.put(ParamCons.ID, id);
        // 名称
        params.put(ParamCons.NAME, name);

        return params;
    }
}