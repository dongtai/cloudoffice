package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 用户表信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-8
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataTable extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -5503879536726158844L;
    // id
    private Long id;
    // 表名
    private String name;
    // 物理上的表或视图名，即真正的表或视图名
    private String realName;
    // 创建状态
    private Short createState;
    // 字段
    private Set<DataField> dfields;
    // 权限集
    private Set<Permission> permissions;
    // 数据源
    private DataSource dataSource;

    // 引用DataTable的DownListRule集合
    private transient Set<DownListRule> downListRules;
    // 引用DataTable的ListSelectRule集合
    private transient Set<ListSelectRule> listSelectRules;
    // 引用DataTable的TreeSelectRule集合
    private transient Set<TreeSelectRule> treeSelectRules;
    // 引用DataTable的RTable集合
    private transient Set<RTable> rtables;

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
     * @return 返回 realName
     */
    public String getRealName()
    {
        return realName == null ? name : realName;
    }

    /**
     * @param realName 设置 realName
     */
    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    /**
     * @return 返回 createState
     */
    public Short getCreateState()
    {
        return createState;
    }

    /**
     * @param createState 设置 createState
     */
    public void setCreateState(Short createState)
    {
        this.createState = createState;
    }

    /**
     * @return 返回 dfields
     */
    public Set<DataField> getDfields()
    {
        return dfields;
    }

    /**
     * @param dfields 设置 dfields
     */
    public void setDfields(Set<DataField> dfields)
    {
        this.dfields = dfields;
    }

    /**
     * @return 返回 permissions
     */
    public Set<Permission> getPermissions()
    {
        return permissions;
    }

    /**
     * @param permissions 设置 permissions
     */
    public void setPermissions(Set<Permission> permissions)
    {
        this.permissions = permissions;
    }

    /**
     * @return 返回 dataSource
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * @param dataSource 设置 dataSource
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @return 返回 downListRules
     */
    public Set<DownListRule> getDownListRules()
    {
        return downListRules;
    }

    /**
     * @param downListRules 设置 downListRules
     */
    public void setDownListRules(Set<DownListRule> downListRules)
    {
        this.downListRules = downListRules;
    }

    /**
     * @return 返回 listSelectRules
     */
    public Set<ListSelectRule> getListSelectRules()
    {
        return listSelectRules;
    }

    /**
     * @param listSelectRules 设置 listSelectRules
     */
    public void setListSelectRules(Set<ListSelectRule> listSelectRules)
    {
        this.listSelectRules = listSelectRules;
    }

    /**
     * @return 返回 treeSelectRules
     */
    public Set<TreeSelectRule> getTreeSelectRules()
    {
        return treeSelectRules;
    }

    /**
     * @param treeSelectRules 设置 treeSelectRules
     */
    public void setTreeSelectRules(Set<TreeSelectRule> treeSelectRules)
    {
        this.treeSelectRules = treeSelectRules;
    }

    /**
     * @return 返回 rtables
     */
    public Set<RTable> getRtables()
    {
        return rtables;
    }

    /**
     * @param rtables 设置 rtables
     */
    public void setRtables(Set<RTable> rtables)
    {
        this.rtables = rtables;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public DataTable clone(boolean isSimple)
    {
        DataTable dtable = new DataTable();
        dtable.setId(id);
        dtable.setName(name);
        dtable.setRealName(realName);
        dtable.setCreateState(createState);
        if (dfields != null && !dfields.isEmpty())
        {
            HashSet<DataField> set = new HashSet<DataField>();
            for (DataField dfield : dfields)
            {
                set.add(dfield.clone(isSimple));
            }
            dtable.setDfields(set);
        }
        if (permissions != null && !permissions.isEmpty())
        {
            HashSet<Permission> set = new HashSet<Permission>();
            for (Permission permission : permissions)
            {
                set.add(permission.clone(isSimple));
            }
            dtable.setPermissions(set);
        }
        return dtable;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataTable)
        {
            if (id != null && id.equals(((DataTable)obj).getId()))
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
        // 表名
        params.put(ParamCons.NAME, name);
        // 物理上的表或视图名，即真正的表或视图名
        params.put(ParamCons.REALNAME, realName);
        // 创建状态
        params.put(ParamCons.CREATESTATE, createState);
        // 字段
        if (dfields != null && !dfields.isEmpty())
        {
            ArrayList<HashMap<String, Object>> dfieldJ = new ArrayList<HashMap<String, Object>>();
            for (DataField dfield : dfields)
            {
                dfieldJ.add(dfield.getJsonObj());
            }
            params.put(ParamCons.DFIELDS, dfieldJ);
        }
        // 权限集
        if (permissions != null && !permissions.isEmpty())
        {
            ArrayList<HashMap<String, Object>> permissionJ = new ArrayList<HashMap<String, Object>>();
            for (Permission permission : permissions)
            {
                permissionJ.add(permission.getJsonObj());
            }
            params.put(ParamCons.PERMISSIONS, permissionJ);
        }
        // 数据源
        if (dataSource != null)
        {
            params.put(ParamCons.DATASOURCE, dataSource.getJsonObj());
        }

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataTable 物理表对象
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public DataTable convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 表名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }
        // 物理上的表或视图名，即真正的表或视图名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.REALNAME);
        if (stringValue != null)
        {
            setRealName(stringValue);
        }
        // 创建状态
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.CREATESTATE);
        if (shortValue != -1)
        {
            setCreateState(shortValue);
        }
        // 字段
        Object objValue = paramsMap.get(ParamCons.DFIELDS);
        if (objValue != null)
        {
            HashSet<DataField> dfields = null;
            if (objValue instanceof ArrayList)
            {
                DataField dfield;
                dfields = new HashSet<DataField>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        dfield = new DataField();
                        dfield.convetJsonToObj((HashMap<String, Object>)obj);
                        dfields.add(dfield);
                    }
                }
            }
            setDfields(dfields);
        }
        // 权限集
        objValue = paramsMap.get(ParamCons.PERMISSIONS);
        if (objValue != null)
        {
            HashSet<Permission> permissions = null;
            if (objValue instanceof ArrayList)
            {
                Permission permission;
                permissions = new HashSet<Permission>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        permission = new Permission();
                        permission.convetJsonToObj((HashMap<String, Object>)obj);
                        permissions.add(permission);
                    }
                }
            }
            setPermissions(permissions);
        }
        // 数据源
        objValue = paramsMap.get(ParamCons.DATASOURCE);
        if (objValue != null)
        {
            DataSource dataSource = null;
            if (objValue instanceof HashMap)
            {
                dataSource = new DataSource();
                dataSource.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDataSource(dataSource);
        }

        return this;
    }
}