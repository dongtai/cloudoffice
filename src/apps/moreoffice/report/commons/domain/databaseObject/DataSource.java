package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;
import java.util.Set;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 数据源
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
public class DataSource extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 2870200024242771418L;
    // id
    private Long id;
    // 数据源名称
    private String name;

    // 引用DataSource的DataTable集合
    private transient Set<DataTable> dtables;

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
     * @return 返回 dtables
     */
    public Set<DataTable> getDtables()
    {
        return dtables;
    }

    /**
     * @param dtables 设置 dtables
     */
    public void setDtables(Set<DataTable> dtables)
    {
        this.dtables = dtables;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataSource && ((DataSource)obj).getName().equals(name))
        {
            return true;
        }
        return false;
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
        // 数据源名称
        params.put(ParamCons.NAME, name);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataSource 数据源对象
     */
    public DataSource convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 数据源名称
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }

        return this;
    }
}