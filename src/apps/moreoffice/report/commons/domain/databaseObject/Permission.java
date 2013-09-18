package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;

/**
 * 权限集
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class Permission extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -6635409832705370368L;
    // id
    private Long id;
    /**
     * 权限类型
     * 0：基本权限
     * 1：设计权限
     */
    private Short type;
    // 权限对应的作用对象类型
    private Short objectType;
    // 权限对应的作用对象ID
    private Long objectID;
    // 权限集
    private Long permission;
    // 查阅范围
    private Short accessRange;
    // 动态条件
    private String dynamicCond;
    // 隐藏字段
    private Set<RField> hideFields;
    // 填报字段
    private Set<RField> fillFields;

    // 权限所在的模板
    private transient Template template;
    // 权限所在的表
    private transient DataTable dtable;

    /**
     * 不要通过此构造器创建权限，这样会忽略非空字段
     */
    public Permission()
    {
        permission = (long)0;
    }

    /**
     * 构造一个权限
     * 
     * @param type 类型(基本权限还是设计权限)
     * @param objectType 权限对应的作用对象类型
     * @param objectID 权限对应的作用对象ID
     * @param permission 权限集
     */
    public Permission(short type, short objectType, long objectID, long permission)
    {
        setType(type);
        setObjectType(objectType);
        setObjectID(objectID);
        setPermission(permission);
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
     * @return 返回 objectType
     */
    public Short getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType 设置 objectType
     */
    public void setObjectType(Short objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return 返回 objectID
     */
    public Long getObjectID()
    {
        return objectID;
    }

    /**
     * @param objectID 设置 objectID
     */
    public void setObjectID(Long objectID)
    {
        this.objectID = objectID;
    }

    /**
     * @return 返回 permission
     */
    public Long getPermission()
    {
        return permission;
    }

    /**
     * @param permission 设置 permission
     */
    public void setPermission(Long permission)
    {
        this.permission = permission;
    }

    /**
     * @return 返回 accessRange
     */
    public Short getAccessRange()
    {
        return accessRange;
    }

    /**
     * @param accessRange 设置 accessRange
     */
    public void setAccessRange(Short accessRange)
    {
        this.accessRange = accessRange;
    }

    /**
     * @return 返回 dynamicCond
     */
    public String getDynamicCond()
    {
        return dynamicCond;
    }

    /**
     * @param dynamicCond 设置 dynamicCond
     */
    public void setDynamicCond(String dynamicCond)
    {
        this.dynamicCond = dynamicCond;
    }

    /**
     * @return 返回 hideFields
     */
    public Set<RField> getHideFields()
    {
        return hideFields;
    }

    /**
     * @param hideFields 设置 hideFields
     */
    public void setHideFields(Set<RField> hideFields)
    {
        this.hideFields = hideFields;
    }

    /**
     * @return 返回 fillFields
     */
    public Set<RField> getFillFields()
    {
        return fillFields;
    }

    /**
     * @param fillFields 设置 fillFields
     */
    public void setFillFields(Set<RField> fillFields)
    {
        this.fillFields = fillFields;
    }

    /**
     * @return 返回 template
     */
    public Template getTemplate()
    {
        return template;
    }

    /**
     * @param template 设置 template
     */
    public void setTemplate(Template template)
    {
        this.template = template;
    }

    /**
     * @return 返回 dtable
     */
    public DataTable getDtable()
    {
        return dtable;
    }

    /**
     * @param dtable 设置 dtable
     */
    public void setDtable(DataTable dtable)
    {
        this.dtable = dtable;
    }

    // ----------权限操作----------

    /**
     * 是否具有填报权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canFill()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANFILL);
    }

    /**
     * 设置是否具有填报权限
     * 
     * @param canFill true：有，false：没有
     */
    public void setCanFill(boolean canFill)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANFILL, canFill);
        // 填报权限会同时影响到这三个权限
        if (canFill)
        {
            permission = DomainTools.setLongFlag(permission, PermissionCons.CANNEW, canFill);
        }
        else
        {
            permission = DomainTools.setLongFlag(permission, PermissionCons.CANNEW, false);
            permission = DomainTools.setLongFlag(permission, PermissionCons.CANMODIFY, false);
            permission = DomainTools.setLongFlag(permission, PermissionCons.CANDELETE, false);
            permission = DomainTools.setLongFlag(permission, PermissionCons.CANSAVE, false);
        }
    }

    /**
     * 是否具有查阅权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canRead()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANREAD);
    }

    /**
     * 设置是否具有查阅权限
     * 
     * @param canRead true：有，false：没有
     */
    public void setCanRead(boolean canRead)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANREAD, canRead);
    }

    /**
     * 是否具有打印权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canPrint()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANPRINT);
    }

    /**
     * 设置是否具有打印权限
     * 
     * @param canPrint true：有，false：没有
     */
    public void setCanPrint(boolean canPrint)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANPRINT, canPrint);
    }

    /**
     * 是否具有新建权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canNew()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANNEW);
    }

    /**
     * 设置是否具有新建权限
     * 
     * @param canNew true：有，false：没有
     */
    public void setCanNew(boolean canNew)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANNEW, canNew);
        if (canNew)
        {
            setCanFill(true);
        }
        else if (!canModify() && !canDelete() && !canSave())
        {
            setCanFill(false);
        }
    }

    /**
     * 是否具有修改权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canModify()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANMODIFY);
    }

    /**
     * 设置是否具有修改权限
     * 
     * @param canModify true：有，false：没有
     */
    public void setCanModify(boolean canModify)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANMODIFY, canModify);
        if (canModify)
        {
            setCanFill(true);
        }
        else if (!canNew() && !canDelete() && !canSave())
        {
            setCanFill(false);
        }
    }

    /**
     * 是否具有删除权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canDelete()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANDELETE);
    }

    /**
     * 设置是否具有删除权限
     * 
     * @param canDelete true：有，false：没有
     */
    public void setCanDelete(boolean canDelete)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANDELETE, canDelete);
        if (canDelete)
        {
            setCanFill(true);
        }
        else if (!canNew() && !canModify() && !canSave())
        {
            setCanFill(false);
        }
    }

    /**
     * 是否具有存本地权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canSave()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANSAVE);
    }

    /**
     * 设置是否具有存本地权限
     * 
     * @param canSave true：有，false：没有
     */
    public void setCanSave(boolean canSave)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANSAVE, canSave);
        if (canSave)
        {
            setCanFill(true);
        }
        else if (!canNew() && !canModify() && !canDelete())
        {
            setCanFill(false);
        }
    }

    /**
     * 是否具有导出权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canExport()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANEXPORT);
    }

    /**
     * 设置是否具有导出权限
     * 
     * @param canExport true：有，false：没有
     */
    public void setCanExport(boolean canExport)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANEXPORT, canExport);
    }

    /**
     * 是否具有设计权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canDesign()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANDESIGN);
    }

    /**
     * 设置是否具有设计权限
     * 
     * @param canDesign true：有，false：没有
     */
    public void setCanDesign(boolean canDesign)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANDESIGN, canDesign);
    }

    /**
     * 是否具有提数权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canExtract()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANEXTRACT);
    }

    /**
     * 设置是否具有提数权限
     * 
     * @param canExtract true：有，false：没有
     */
    public void setCanExtract(boolean canExtract)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANEXTRACT, canExtract);
    }

    /**
     * 是否具有回写权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canWrite()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANWRITE);
    }

    /**
     * 设置是否具有回写权限
     * 
     * @param canWrite true：有，false：没有
     */
    public void setCanWrite(boolean canWrite)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANWRITE, canWrite);
    }

    /**
     * 是否具有添加字段权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canAddField()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANADDFIELD);
    }

    /**
     * 设置是否具有添加字段权限
     * 
     * @param canAddField true：有，false：没有
     */
    public void setCanAddField(boolean canAddField)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANADDFIELD, canAddField);
    }

    /**
     * 是否具有映射权限
     * 
     * @return boolean true：有，false：没有
     */
    public boolean canMap()
    {
        return DomainTools.isLongFlag(permission, PermissionCons.CANMAP);
    }

    /**
     * 设置是否具有映射权限
     * 
     * @param canMap true：有，false：没有
     */
    public void setCanMap(boolean canMap)
    {
        permission = DomainTools.setLongFlag(permission, PermissionCons.CANMAP, canMap);
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public Permission clone(boolean isSimple)
    {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setType(type);
        permission.setObjectType(objectType);
        permission.setObjectID(objectID);
        permission.setPermission(this.permission);
        permission.setAccessRange(accessRange);
        permission.setDynamicCond(dynamicCond);
        if (hideFields != null && !hideFields.isEmpty())
        {
            HashSet<RField> set = new HashSet<RField>();
            for (RField rfield : hideFields)
            {
                set.add(isClient() ? rfield : rfield.clone(isSimple));
            }
            permission.setHideFields(set);
        }

        if (fillFields != null && !fillFields.isEmpty())
        {
            HashSet<RField> set = new HashSet<RField>();
            for (RField rfield : fillFields)
            {
                set.add(isClient() ? rfield : rfield.clone(isSimple));
            }
            permission.setFillFields(set);
        }

        return permission;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Permission)
        {
            if (id != null && id.equals(((Permission)obj).getId()))
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
        // 权限类型
        params.put(ParamCons.TYPE, type);
        // 权限对应的作用对象类型
        params.put(ParamCons.OBJECTTYPE, objectType);
        // 权限对应的作用对象ID
        params.put(ParamCons.OBJECTID, objectID);
        // 权限集
        params.put(ParamCons.PERMISSION, permission);
        // 查阅范围
        params.put(ParamCons.ACCESSRANGE, accessRange);
        // 动态条件
        params.put(ParamCons.DYNAMICCOND, dynamicCond);
        // 隐藏字段
        if (hideFields != null && !hideFields.isEmpty())
        {
            ArrayList<HashMap<String, Object>> hideFieldJ = new ArrayList<HashMap<String, Object>>();
            for (RField rfield : hideFields)
            {
                hideFieldJ.add(rfield.getJsonObj());
            }
            params.put(ParamCons.HIDEFIELDS, hideFieldJ);
        }
        // 填报字段
        if (fillFields != null && !fillFields.isEmpty())
        {
            ArrayList<HashMap<String, Object>> fillFieldJ = new ArrayList<HashMap<String, Object>>();
            for (RField rfield : fillFields)
            {
                fillFieldJ.add(rfield.getJsonObj());
            }
            params.put(ParamCons.FILLFIELDS, fillFieldJ);
        }

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return Permission 权限对象
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public Permission convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 权限类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.TYPE);
        if (shortValue != -1)
        {
            setType(shortValue);
        }
        // 权限对应的作用对象类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.OBJECTTYPE);
        if (shortValue != -1)
        {
            setObjectType(shortValue);
        }
        // 权限对应的作用对象ID
        longValue = HashMapTools.getLong(paramsMap, ParamCons.OBJECTID);
        if (longValue != -1)
        {
            setObjectID(longValue);
        }
        // 权限集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.PERMISSION);
        if (longValue != -1)
        {
            setPermission(longValue);
        }
        // 查阅范围
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.ACCESSRANGE);
        if (shortValue != -1)
        {
            setAccessRange(shortValue);
        }
        // 动态条件
        stringValue = HashMapTools.getString(paramsMap, ParamCons.DYNAMICCOND);
        if (stringValue != null)
        {
            setDynamicCond(stringValue);
        }
        // 隐藏字段
        Object objValue = paramsMap.get(ParamCons.HIDEFIELDS);
        if (objValue != null)
        {
            HashSet<RField> hideFields = null;
            if (objValue instanceof ArrayList)
            {
                RField rfield;
                hideFields = new HashSet<RField>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        rfield = new RField();
                        rfield.convetJsonToObj((HashMap<String, Object>)obj);
                        hideFields.add(rfield);
                    }
                }
            }
            setHideFields(hideFields);
        }
        // 填报字段
        objValue = paramsMap.get(ParamCons.FILLFIELDS);
        if (objValue != null)
        {
            HashSet<RField> fillFields = null;
            if (objValue instanceof ArrayList)
            {
                RField rfield;
                fillFields = new HashSet<RField>();
                for (Object obj : (ArrayList)objValue)
                {
                    if (obj instanceof HashMap)
                    {
                        rfield = new RField();
                        rfield.convetJsonToObj((HashMap<String, Object>)obj);
                        fillFields.add(rfield);
                    }
                }
            }
            setFillFields(fillFields);
        }

        return this;
    }
}