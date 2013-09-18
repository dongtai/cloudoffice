package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;

/**
 * 模板中引用的表对象
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
public class RTable extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -6323777394846658899L;
    // id
    private Long id;
    // 所在模板
    private transient Template template;
    // 对应的物理表
    private DataTable dtable;
    // 对应的字段
    private Set<RField> rfields;
    /**
     * 基本属性集
     * 0：隐藏
     * 1：是否可扩展
     */
    private Long attrFlag;
    // 别名
    private String alias;
    // 模式(0:单一; 1:行模式; 2:列模式; 3:交叉表)
    private Short mode;
    // 位置
    private Integer position;
    // 对应关系(新创建的、添加字段、映射)
    private Short relation;

    // 引用RTable的ReadRule集合
    private transient Set<ReadRule> readRules_from;
    // 引用RTable的ReadRule集合
    private transient Set<ReadRule> readRules_to;
    // 引用RTable的ModifyRule集合
    private transient Set<ModifyRule> modifyRules;
    // 引用RTable的AddDetailRule集合
    private transient Set<AddDetailRule> addDetailRules;
    // 引用RTable的DelDetailRule集合
    private transient Set<DelDetailRule> delDetailRules;
    // 引用RTable的JoinCond左边表集合
    private transient Set<JoinCond> joinCondOnLefts;
    // 引用RTable的JoinCond右边表集合
    private transient Set<JoinCond> joinCondOnRights;
    // 引用RTable的FillMode集合
    private transient Set<FillMode> fillModes;
    // 引用RTable的WriteMode集合
    private transient Set<WriteMode> writeModes;
    // 引用RTable的RecordIndex集合
    private transient Set<RecordIndex> recordIndexs;

    // 重复表的记录ID数组
    private long[] dataIDs;

    /**
     * 默认构造器
     */
    public RTable()
    {
        attrFlag = (long)0;
        mode = TableCons.SINGLE;
    }

    /**
     * 得到字段名列表
     * 
     * @return String[] 字段名列表
     */
    public String[] getFieldNames()
    {
        if (rfields == null)
        {
            return null;
        }

        int index = 0;
        String[] fieldNames = new String[rfields.size()];
        for (RField rfield : rfields)
        {
            fieldNames[index++] = rfield.getRealName();
        }
        return fieldNames;
    }

    /**
     * 初始化记录
     * 
     * @param fieldNames 字段名列表
     * @param values 值列表
     */
    public void initRecord(String[] fieldNames, Vector<Vector<Object>> values)
    {
        if (fieldNames == null || values == null || fieldNames.length < 1)
        {
            return;
        }

        if (rfields != null && !rfields.isEmpty())
        {
            Object[] value;
            String fieldName;
            for (RField rfield : rfields)
            {
                fieldName = rfield.getRealName();
                for (int i = 0, size = fieldNames.length; i < size; i++)
                {
                    if (fieldName.equals(fieldNames[i]))
                    {
                        value = new Object[values.size()];
                        for (int j = 0, count = value.length; j < count; j++)
                        {
                            value[j] = values.get(j).get(i);
                        }
                        rfield.setValues(value);
                    }
                }
            }
        }
    }

    /**
     * 得到当前表里的记录个数
     * 如果是单一数据项，一般情况下是1，如果是导入，可能就>1
     * 如果是重复数据项，则返回重复的数量
     * 
     * @return long 当前表里的记录个数
     */
    public long getRecordCount()
    {
        long count = 1;
        if (dataIDs != null)
        {
            count = dataIDs.length;
        }

        for (RField rfield : rfields)
        {
            Object value = rfield.getValues();
            if (value instanceof Object[])
            {
                count = ((Object[])value).length > count ? ((Object[])value).length : count;
            }
        }

        return count;
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

    /**
     * @return 返回 rfields
     */
    public Set<RField> getRfields()
    {
        return rfields;
    }

    /**
     * @param rfields 设置 rfields
     */
    public void setRfields(Set<RField> rfields)
    {
        this.rfields = rfields;
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
     * 得到是否隐藏
     * 
     * @return boolean 是否隐藏
     */
    public boolean isHide()
    {
        return DomainTools.isLongFlag(attrFlag, 0);
    }

    /**
     * 设置是否隐藏
     * 
     * @param hide 是否隐藏
     */
    public void setHide(boolean hide)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 0, hide);
    }

    /**
     * 得到是否可扩展
     * 
     * @return boolean 是否可扩展
     */
    public boolean canExpand()
    {
        return DomainTools.isLongFlag(attrFlag, 1);
    }

    /**
     * 设置是否可扩展
     * 
     * @param expand 是否可扩展
     */
    public void setCanExpand(boolean expand)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 1, expand);
    }

    /**
     * @return 返回 alias
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * @param alias 设置 alias
     */
    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    /**
     * @return 返回 mode
     */
    public Short getMode()
    {
        return mode;
    }

    /**
     * @param mode 设置 mode
     */
    public void setMode(Short mode)
    {
        this.mode = mode;
    }

    /**
     * @return 返回 position
     */
    public Integer getPosition()
    {
        return position;
    }

    /**
     * @param position 设置 position
     */
    public void setPosition(Integer position)
    {
        this.position = position;
    }

    /**
     * @return 返回 relation
     */
    public Short getRelation()
    {
        return relation;
    }

    /**
     * @param relation 设置 relation
     */
    public void setRelation(Short relation)
    {
        this.relation = relation;
    }

    /**
     * @return 返回 readRules_from
     */
    public Set<ReadRule> getReadRules_from()
    {
        return readRules_from;
    }

    /**
     * @param readRules_from 设置 readRules_from
     */
    public void setReadRules_from(Set<ReadRule> readRules_from)
    {
        this.readRules_from = readRules_from;
    }

    /**
     * @return 返回 readRules_to
     */
    public Set<ReadRule> getReadRules_to()
    {
        return readRules_to;
    }

    /**
     * @param readRules_to 设置 readRules_to
     */
    public void setReadRules_to(Set<ReadRule> readRules_to)
    {
        this.readRules_to = readRules_to;
    }

    /**
     * @return 返回 modifyRules
     */
    public Set<ModifyRule> getModifyRules()
    {
        return modifyRules;
    }

    /**
     * @param modifyRules 设置 modifyRules
     */
    public void setModifyRules(Set<ModifyRule> modifyRules)
    {
        this.modifyRules = modifyRules;
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
     * @return 返回 joinCondOnLefts
     */
    public Set<JoinCond> getJoinCondOnLefts()
    {
        return joinCondOnLefts;
    }

    /**
     * @param joinCondOnLefts 设置 joinCondOnLefts
     */
    public void setJoinCondOnLefts(Set<JoinCond> joinCondOnLefts)
    {
        this.joinCondOnLefts = joinCondOnLefts;
    }

    /**
     * @return 返回 joinCondOnRights
     */
    public Set<JoinCond> getJoinCondOnRights()
    {
        return joinCondOnRights;
    }

    /**
     * @param joinCondOnRights 设置 joinCondOnRights
     */
    public void setJoinCondOnRights(Set<JoinCond> joinCondOnRights)
    {
        this.joinCondOnRights = joinCondOnRights;
    }

    /**
     * @return 返回 fillModes
     */
    public Set<FillMode> getFillModes()
    {
        return fillModes;
    }

    /**
     * @param fillModes 设置 fillModes
     */
    public void setFillModes(Set<FillMode> fillModes)
    {
        this.fillModes = fillModes;
    }

    /**
     * @return 返回 writeModes
     */
    public Set<WriteMode> getWriteModes()
    {
        return writeModes;
    }

    /**
     * @param writeModes 设置 writeModes
     */
    public void setWriteModes(Set<WriteMode> writeModes)
    {
        this.writeModes = writeModes;
    }

    /**
     * @return 返回 recordIndexs
     */
    public Set<RecordIndex> getRecordIndexs()
    {
        return recordIndexs;
    }

    /**
     * @param recordIndexs 设置 recordIndexs
     */
    public void setRecordIndexs(Set<RecordIndex> recordIndexs)
    {
        this.recordIndexs = recordIndexs;
    }

    /**
     * @return 返回 dataIDs
     */
    public long[] getDataIDs()
    {
        return dataIDs;
    }

    /**
     * @param dataIDs 设置 dataIDs
     */
    public void setDataIDs(long[] dataIDs)
    {
        this.dataIDs = dataIDs;
    }

    /**
     * 返回是否是单一数据表
     * 
     * @return boolean 是否是单一数据表
     */
    public boolean isSingleTable()
    {
        return getMode() == TableCons.SINGLE;
    }

    /**
     * 返回是否是重复数据表
     * 
     * @return boolean 是否是重复数据表
     */
    public boolean isRepeatTable()
    {
        return getMode() != TableCons.SINGLE;
    }

    /**
     * 得到物理表名
     * 
     * @return String 物理表名
     */
    public String getRealName()
    {
        return dtable.getRealName();
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public RTable clone(boolean isSimple)
    {
        RTable rtable = new RTable();
        rtable.setId(id);
        rtable.setDtable(dtable.clone(isSimple));
        if (rfields != null && !rfields.isEmpty())
        {
            HashSet<RField> set = new HashSet<RField>();
            for (RField rfield : rfields)
            {
                set.add(rfield.clone(isSimple));
            }
            rtable.setRfields(set);
        }
        rtable.setAttrFlag(attrFlag);
        rtable.setAlias(alias);
        rtable.setMode(mode);
        rtable.setPosition(position);
        rtable.setRelation(relation);
        rtable.setDataIDs(dataIDs);
        return rtable;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof RTable)
        {
            if (id != null && id.equals(((RTable)obj).getId()))
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
        return getDtable().getName();
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
        // 所在模板
        if (template != null)
        {
            params.put(ParamCons.TEMPLATENAME, template.getName());
        }
        // 对应的物理表
        if (dtable != null)
        {
            params.put(ParamCons.DTABLE, dtable.getJsonObj());
        }
        // 对应的字段
        if (rfields != null && !rfields.isEmpty())
        {
            ArrayList<HashMap<String, Object>> rfieldJ = new ArrayList<HashMap<String, Object>>();
            for (RField rfield : rfields)
            {
                rfieldJ.add(rfield.getJsonObj());
            }
            params.put(ParamCons.RFIELDS, rfieldJ);
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 别名
        params.put(ParamCons.ALIAS, alias);
        // 模式(0:单一; 1:行模式; 2:列模式; 3:交叉表)
        params.put(ParamCons.MODE, mode);
        // 位置
        params.put(ParamCons.POSITION, position);
        // 对应关系(新创建的、添加字段、映射)
        params.put(ParamCons.RELATION, relation);
        // 重复表的记录ID数组
        params.put(ParamCons.DATAIDS, dataIDs);

        return params;
    }
}