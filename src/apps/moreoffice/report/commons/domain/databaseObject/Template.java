package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.interfaces.IReportService;

/**
 * 模板
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
public class Template extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 2433513814818203592L;
    // id
    private Long id;
    // 模板编号
    private String number;
    // 模板名称
    private String name;
    // 文件名
    private String fileName;
    // 模板路径
    private String path;
    /**
     * 基本属性集
     * 0：本模板只用作查询，不保存数据
     * 1：能否保存到本地
     * 2：其它有相应权限的人亦可修改
     * 3：是否停用
     */
    private Long attrFlag;
    // 权限集
    private Set<Permission> permissions;
    // 用户表
    private Set<RTable> rtables;
    // 表间规则
    private Set<TableRule> tableRules;
    // 锁定条件
    private String lockCond;
    // 创建者ID
    private Long creatorId;
    // 创建时间
    private Date createDate;
    // 上次修改者ID
    private Long modifierId;
    // 上次修改时间
    private Date modifyDate;

    // 记录对象
    private transient Set<Record> records;
    // 引用Template的AddDetailRule集合
    private transient Set<AddDetailRule> addDetailRules;
    // 引用Template的DelDetailRule集合
    private transient Set<DelDetailRule> delDetailRules;
    // 引用Template的NewFormRule集合
    private transient Set<NewFormRule> newFormRules;
    // 引用Template的DelFormRule集合
    private transient Set<DelFormRule> delFormRules;
    // 引用Template的HyperLink集合
    private transient Set<HyperLink> hyperlinks;
    // 权限是否有改变
    private boolean permissionChanged;

    /**
     * 外部不能使用，仅为了给hibernate用
     */
    public Template()
    {
        attrFlag = (long)0;
    }

    /**
     * 模板对象
     * 
     * @param number 模板编号
     * @param name 模板名称
     * @param path 模板路径
     */
    public Template(String number, String name, String path)
    {
        setNumber(number);
        setName(name);
        setPath(path);
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
     * @return 返回 number
     */
    public String getNumber()
    {
        return number;
    }

    /**
     * @param number 设置 number
     */
    public void setNumber(String number)
    {
        this.number = number;
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
     * @return 返回 fileName
     */
    public String getFileName()
    {
        return fileName == null ? name : fileName;
    }

    /**
     * @param fileName 设置 fileName
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
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
     * 是否是纯查询报表
     * 
     * @return boolean 是否是纯查询报表
     */
    public boolean isPureQuery()
    {
        return DomainTools.isLongFlag(attrFlag, 0);
    }

    /**
     * 设置是否是纯查询报表
     * 
     * @param pureQuery 是否是纯查询报表
     */
    public void setPureQuery(boolean pureQuery)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 0, pureQuery);
    }

    /**
     * 得到能否保存到本地
     * 
     * @return boolean 能否保存到本地
     */
    public boolean canSaveNative()
    {
        return DomainTools.isLongFlag(attrFlag, 1);
    }

    /**
     * 设置能否保存到本地
     * 
     * @param canSaveNative 能否保存到本地
     */
    public void setCanSaveNative(boolean canSaveNative)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 1, canSaveNative);
    }

    /**
     * 得到其它有相应权限的人亦可修改
     * 
     * @return 其它有相应权限的人亦可修改
     */
    public boolean getOthersCanOperate()
    {
        return DomainTools.isLongFlag(attrFlag, 2);
    }

    /**
     * 设置其它有相应权限的人亦可修改
     * 
     * @param canOperate 其它有相应权限的人亦可修改
     */
    public void setOthersCanOperate(boolean canOperate)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 2, canOperate);
    }

    /**
     * 得到模板的使用状态(停用或启用)
     * 
     * @param service 报表服务
     * @return boolean 模板的使用状态(停用或启用)
     */
    public boolean getUseState(IReportService service)
    {
        if (service != null)
        {
            Result result = service.getTemplateUseState(getId());
            if (!result.hasError())
            {
                return result.getBoolean();
            }
        }
        return DomainTools.isLongFlag(attrFlag, 3);
    }

    /**
     * 设置模板的使用状态(停用或启用)
     * 
     * @param useState 模板的使用状态(停用或启用)
     */
    public void setUseState(boolean useState)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 3, useState);
    }

    /**
     * 客户端动态得到报表权限
     * 
     * @param service 报表服务
     * @return 返回 permissions
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public Set<Permission> getPermissions(IReportService service)
    {
        // 如果是客户端，则动态从服务器拿权限
        if (service != null && getId() != null && !permissionChanged)
        {
            Result result = service.getTemplatePermission(getId());
            if (!result.hasError())
            {
                return (Set)result.getData();
            }
        }
        return permissions;
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
        if (isClient())
        {
            /**
             * 如果权限中的隐藏或填报字段已经被删除了，则删除Permission中记录的字段
             * 没有在服务器端做级联删除是因为可能先后顺序导致问题。如果先删除RField
             * 再保存Permission，就找不到对应的字段了。
             */
            // TODO
            // 处理HyperLinkInfo中的RField,原理同上
            // TODO
        }
        this.rtables = rtables;
    }

    /**
     * @return 返回 tableRules
     */
    public Set<TableRule> getTableRules()
    {
        return tableRules;
    }

    /**
     * @param tableRules 设置 tableRules
     */
    public void setTableRules(Set<TableRule> tableRules)
    {
        this.tableRules = tableRules;
    }

    /**
     * @return 返回 lockCond
     */
    public String getLockCond()
    {
        return lockCond;
    }

    /**
     * @param lockCond 设置 lockCond
     */
    public void setLockCond(String lockCond)
    {
        this.lockCond = lockCond;
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
        // 只允许服务器端设置
        if (!isClient())
        {
            this.createDate = createDate;
        }
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
        // 只允许服务器端设置
        if (!isClient())
        {
            this.modifyDate = modifyDate;
        }
    }

    /**
     * @return 返回 records
     */
    public Set<Record> getRecords()
    {
        return records;
    }

    /**
     * @param records 设置 records
     */
    public void setRecords(Set<Record> records)
    {
        this.records = records;
    }

    /**
     * @return 返回 addDetailRules
     */
    public Set<AddDetailRule> getAddDetailRules()
    {
        return addDetailRules;
    }

    /**
     * @param addDetailRules 设置 addDetailRules
     */
    public void setAddDetailRules(Set<AddDetailRule> addDetailRules)
    {
        this.addDetailRules = addDetailRules;
    }

    /**
     * @return 返回 delDetailRules
     */
    public Set<DelDetailRule> getDelDetailRules()
    {
        return delDetailRules;
    }

    /**
     * @param delDetailRules 设置 delDetailRules
     */
    public void setDelDetailRules(Set<DelDetailRule> delDetailRules)
    {
        this.delDetailRules = delDetailRules;
    }

    /**
     * @return 返回 newFormRules
     */
    public Set<NewFormRule> getNewFormRules()
    {
        return newFormRules;
    }

    /**
     * @param newFormRules 设置 newFormRules
     */
    public void setNewFormRules(Set<NewFormRule> newFormRules)
    {
        this.newFormRules = newFormRules;
    }

    /**
     * @return 返回 delFormRules
     */
    public Set<DelFormRule> getDelFormRules()
    {
        return delFormRules;
    }

    /**
     * @param delFormRules 设置 delFormRules
     */
    public void setDelFormRules(Set<DelFormRule> delFormRules)
    {
        this.delFormRules = delFormRules;
    }

    /**
     * @return 返回 hyperlinks
     */
    public Set<HyperLink> getHyperlinks()
    {
        return hyperlinks;
    }

    /**
     * @param hyperlinks 设置 hyperlinks
     */
    public void setHyperlinks(Set<HyperLink> hyperlinks)
    {
        this.hyperlinks = hyperlinks;
    }

    /**
     * @return 返回 permissionChanged
     */
    public boolean isPermissionChanged()
    {
        return permissionChanged;
    }

    /**
     * @param permissionChanged 设置 permissionChanged
     */
    public void setPermissionChanged(boolean permissionChanged)
    {
        this.permissionChanged = permissionChanged;
    }

    /**
     * 得到当前模板对应的RTable列表
     * 
     * @param service 报表服务
     * @param tableType 表类型(单一、重复、所有)
     * @param tableOperateType 表操作类型(提数、回写)
     * @param dataOperateType 记录操作类型(新建、修改、删除)
     * @return ArrayList<RTable> RTable列表
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public ArrayList<RTable> getRTableList(IReportService service, int tableType,
        int tableOperateType, int dataOperateType)
    {
        if (service != null)
        {
            Result result = service.getRTableList(TableCons.TEMPLATE, getName(), tableType,
                tableOperateType, dataOperateType);
            if (!result.hasError())
            {
                return (ArrayList)result.getData();
            }
        }
        return null;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public Template clone(boolean isSimple)
    {
        Template template = new Template();
        template.setId(id);
        template.setNumber(number);
        template.setName(name);
        template.setFileName(fileName);
        template.setPath(path);
        template.setAttrFlag(attrFlag);
        template.setLockCond(lockCond);
        template.setCreatorId(creatorId);
        template.setCreateDate(createDate);
        template.setModifierId(modifierId);
        template.setModifyDate(modifyDate);

        if (!isSimple)
        {
            if (permissions != null && !permissions.isEmpty())
            {
                HashSet<Permission> set = new HashSet<Permission>();
                for (Permission permission : permissions)
                {
                    set.add(permission.clone(isSimple));
                }
                template.setPermissions(set);
            }

            if (rtables != null && !rtables.isEmpty())
            {
                HashSet<RTable> set = new HashSet<RTable>();
                for (RTable rtable : rtables)
                {
                    set.add(rtable.clone(isSimple));
                }
                template.setRtables(set);
            }

            if (tableRules != null && !tableRules.isEmpty())
            {
                HashSet<TableRule> set = new HashSet<TableRule>();
                for (TableRule tableRule : tableRules)
                {
                    set.add(tableRule.clone(isSimple));
                }
                template.setTableRules(set);
            }
        }

        return template;
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
        // 模板编号
        params.put(ParamCons.NUMBER, number);
        // 模板名称
        params.put(ParamCons.NAME, name);
        // 文件名
        params.put(ParamCons.FILENAME, fileName);
        // 模板路径
        params.put(ParamCons.PATH, path);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 锁定条件
        params.put(ParamCons.LOCKCOND, lockCond);
        // 创建者ID
        params.put(ParamCons.CREATORID, creatorId);
        // 创建时间
        params.put(ParamCons.CREATEDATE, createDate);
        // 上次修改者ID
        params.put(ParamCons.MODIFIERID, modifierId);
        // 上次修改时间
        params.put(ParamCons.MODIFYDATE, modifyDate);
        // 权限改变
        params.put(ParamCons.PERMISSIONCHANGED, permissionChanged);

        ArrayList<HashMap<String, Object>> list;
        // 权限集
        if (permissions != null && !permissions.isEmpty())
        {
            list = new ArrayList<HashMap<String, Object>>();
            for (Permission permission : permissions)
            {
                list.add(permission.getJsonObj());
            }
            params.put(ParamCons.PERMISSIONS, list);
        }
        // 用户表
        if (rtables != null && !rtables.isEmpty())
        {
            list = new ArrayList<HashMap<String, Object>>();
            for (RTable rtable : rtables)
            {
                list.add(rtable.getJsonObj());
            }
            params.put(ParamCons.RTABLES, rtables);
        }
        // 表间规则
        if (tableRules != null && !tableRules.isEmpty())
        {
            list = new ArrayList<HashMap<String, Object>>();
            for (TableRule tableRule : tableRules)
            {
                list.add(tableRule.getJsonObj());
            }
            params.put(ParamCons.TABLERULES, tableRules);
        }

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataBaseObject 对象
     */
    @ SuppressWarnings("unchecked")
    public DataBaseObject convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 模板编号
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NUMBER);
        if (stringValue != null)
        {
            setNumber(stringValue);
        }
        // 模板名称
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NAME);
        if (stringValue != null)
        {
            setName(stringValue);
        }
        // 文件名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.FILENAME);
        if (stringValue != null)
        {
            setFileName(stringValue);
        }
        // 模板路径
        stringValue = HashMapTools.getString(paramsMap, ParamCons.PATH);
        if (stringValue != null)
        {
            setPath(stringValue);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 锁定条件
        stringValue = HashMapTools.getString(paramsMap, ParamCons.LOCKCOND);
        if (stringValue != null)
        {
            setLockCond(stringValue);
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
        // 权限改变
        booleanValue = HashMapTools.getBoolean(paramsMap, ParamCons.PERMISSIONCHANGED);
        if (booleanValue != null)
        {
            setPermissionChanged(booleanValue);
        }

        ArrayList<HashMap<String, Object>> objs;
        // 权限集
        Object objValue = paramsMap.get(ParamCons.PERMISSIONS);
        if (objValue != null)
        {
            HashSet<Permission> permissions = null;
            if (objValue instanceof ArrayList)
            {
                Permission permission;
                permissions = new HashSet<Permission>();
                objs = (ArrayList<HashMap<String, Object>>)paramsMap.get(ParamCons.PERMISSIONS);
                for (HashMap<String, Object> obj : objs)
                {
                    permission = new Permission();
                    permissions.add((Permission)permission.convetJsonToObj(obj));
                }
            }
            setPermissions(permissions);
        }
        // 用户表
        objValue = paramsMap.get(ParamCons.RTABLES);
        if (objValue != null)
        {
            HashSet<RTable> rtables = null;
            if (objValue instanceof ArrayList)
            {
                RTable rtable;
                rtables = new HashSet<RTable>();
                objs = (ArrayList<HashMap<String, Object>>)paramsMap.get(ParamCons.RTABLES);
                for (HashMap<String, Object> obj : objs)
                {
                    rtable = new RTable();
                    rtables.add((RTable)rtable.convetJsonToObj(obj));
                }
            }
            setRtables(rtables);
        }
        // 表间规则
        objValue = paramsMap.get(ParamCons.TABLERULES);
        if (objValue != null)
        {
            HashSet<TableRule> tableRules = null;
            if (objValue instanceof ArrayList)
            {
                TableRule tableRule;
                tableRules = new HashSet<TableRule>();
                objs = (ArrayList<HashMap<String, Object>>)paramsMap.get(ParamCons.TABLERULES);
                for (HashMap<String, Object> obj : objs)
                {
                    tableRule = new TableRule();
                    tableRules.add((TableRule)tableRule.convetJsonToObj(obj));
                }
            }
            setTableRules(tableRules);
        }

        return this;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Template)
        {
            if (id != null && id.equals(((Template)obj).getId()))
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
}