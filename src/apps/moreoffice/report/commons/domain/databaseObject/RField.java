package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 模板中引用的字段对象
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
public class RField extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -6393267827373020324L;
    // id
    private Long id;
    // 对应的RTable
    private transient RTable rtable;
    // 对应的物理字段
    private DataField dfield;
    // 数据类型
    private DataType dataType;
    /**
     * 基本属性集
     * 0：隐藏
     * 1：不清空
     * 2：不可编辑
     */
    private Long attrFlag;
    // 目标类型
    private Short addressType;
    // 目标所在的sheetID
    private Integer sheetID;
    // 目标地址
    private String address;
    // 别名
    private String alias;
    // 必填提示
    private String notNullTip;
    // 位置
    private Integer position;
    // 超链接
    private HyperLink hyperLink;
    // 数据规范
    private DataRuleCond dataRuleCond;

    // 引用RField的JoinCond左边表集合
    private transient Set<JoinCond> joinCondOnLefts;
    // 引用RField的JoinCond右边表集合
    private transient Set<JoinCond> joinCondOnRights;
    // 引用RField的FillModeItem集合
    private transient Set<FillModeItem> fillModeItems;
    // 引用RField的HyperLinkInfo中本报表字段集合
    private transient Set<HyperLinkInfo> hyperLinkInfos;
    // 引用RField的HyperLinkInfo中目标字段集合
    private transient Set<HyperLinkInfo> hyperLinkInfos_target;
    // 引用RField的DataRuleCond集合
    private transient Set<DataRuleCond> dataRuleConds;
    // 引用RField的WriteModeItem集合
    private transient Set<WriteModeItem> writeModeItems;
    // 引用RField的Permission的隐藏字段集合
    private transient Set<Permission> permissions_hide;
    // 引用RField的Permission的填报字段集合
    private transient Set<Permission> permissions_fill;

    // 值
    private Object values;

    /**
     * 默认构造器
     */
    public RField()
    {
        attrFlag = (long)0;
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
     * @return 返回 rtable
     */
    public RTable getRtable()
    {
        return rtable;
    }

    /**
     * @param rtable 设置 rtable
     */
    public void setRtable(RTable rtable)
    {
        this.rtable = rtable;
    }

    /**
     * @return 返回 dfield
     */
    public DataField getDfield()
    {
        return dfield;
    }

    /**
     * @param dfield 设置 dfield
     */
    public void setDfield(DataField dfield)
    {
        this.dfield = dfield;
    }

    /**
     * @return 返回 dataType
     */
    public DataType getDataType()
    {
        return dataType;
    }

    /**
     * @param dataType 设置 dataType
     */
    public void setDataType(DataType dataType)
    {
        this.dataType = dataType;
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
     * 得到是否不清空
     * 
     * @return boolean 是否不清空
     */
    public boolean isNotClear()
    {
        return DomainTools.isLongFlag(attrFlag, 1);
    }

    /**
     * 设置是否不清空
     * 
     * @param hide 是否不清空
     */
    public void setNotClear(boolean notClear)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 1, notClear);
    }

    /**
     * 得到是否不可编辑
     * 
     * @return boolean 是否不可编辑
     */
    public boolean isNotEdit()
    {
        return DomainTools.isLongFlag(attrFlag, 2);
    }

    /**
     * 设置是否不可编辑
     * 
     * @param hide 是否不可编辑
     */
    public void setNotEdit(boolean notEdit)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 2, notEdit);
    }

    /**
     * @return 返回 addressType
     */
    public Short getAddressType()
    {
        return addressType;
    }

    /**
     * @param addressType 设置 addressType
     */
    public void setAddressType(Short addressType)
    {
        this.addressType = addressType;
    }

    /**
     * @return 返回 sheetID
     */
    public Integer getSheetID()
    {
        return sheetID;
    }

    /**
     * @param sheetID 设置 sheetID
     */
    public void setSheetID(Integer sheetID)
    {
        this.sheetID = sheetID;
    }

    /**
     * @return 返回 address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address 设置 address
     */
    public void setAddress(String address)
    {
        this.address = address;
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
     * @return 返回 notNullTip
     */
    public String getNotNullTip()
    {
        return notNullTip;
    }

    /**
     * @param notNullTip 设置 notNullTip
     */
    public void setNotNullTip(String notNullTip)
    {
        this.notNullTip = notNullTip;
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
     * @return 返回 dataRuleCond
     */
    public DataRuleCond getDataRuleCond()
    {
        return dataRuleCond;
    }

    /**
     * @param dataRuleCond 设置 dataRuleCond
     */
    public void setDataRuleCond(DataRuleCond dataRuleCond)
    {
        this.dataRuleCond = dataRuleCond;
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
     * @return 返回 fillModeItems
     */
    public Set<FillModeItem> getFillModeItems()
    {
        return fillModeItems;
    }

    /**
     * @param fillModeItems 设置 fillModeItems
     */
    public void setFillModeItems(Set<FillModeItem> fillModeItems)
    {
        this.fillModeItems = fillModeItems;
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
     * @return 返回 hyperLinkInfos_target
     */
    public Set<HyperLinkInfo> getHyperLinkInfos_target()
    {
        return hyperLinkInfos_target;
    }

    /**
     * @param hyperLinkInfos_target 设置 hyperLinkInfos_target
     */
    public void setHyperLinkInfos_target(Set<HyperLinkInfo> hyperLinkInfos_target)
    {
        this.hyperLinkInfos_target = hyperLinkInfos_target;
    }

    /**
     * @return 返回 dataRuleConds
     */
    public Set<DataRuleCond> getDataRuleConds()
    {
        return dataRuleConds;
    }

    /**
     * @param dataRuleConds 设置 dataRuleConds
     */
    public void setDataRuleConds(Set<DataRuleCond> dataRuleConds)
    {
        this.dataRuleConds = dataRuleConds;
    }

    /**
     * @return 返回 writeModeItems
     */
    public Set<WriteModeItem> getWriteModeItems()
    {
        return writeModeItems;
    }

    /**
     * @param writeModeItems 设置 writeModeItems
     */
    public void setWriteModeItems(Set<WriteModeItem> writeModeItems)
    {
        this.writeModeItems = writeModeItems;
    }

    /**
     * @return 返回 permissions_hide
     */
    public Set<Permission> getPermissions_hide()
    {
        return permissions_hide;
    }

    /**
     * @param permissions_hide 设置 permissions_hide
     */
    public void setPermissions_hide(Set<Permission> permissions_hide)
    {
        this.permissions_hide = permissions_hide;
    }

    /**
     * @return 返回 permissions_fill
     */
    public Set<Permission> getPermissions_fill()
    {
        return permissions_fill;
    }

    /**
     * @param permissions_fill 设置 permissions_fill
     */
    public void setPermissions_fill(Set<Permission> permissions_fill)
    {
        this.permissions_fill = permissions_fill;
    }

    /**
     * 得到物理字段名
     * 
     * @return String 物理字段名
     */
    public String getRealName()
    {
        return dfield.getRealName();
    }

    /**
     * @return 返回 values
     */
    public Object getValues()
    {
        return values;
    }

    /**
     * @param values 设置 values
     */
    public void setValues(Object values)
    {
        this.values = values;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public RField clone(boolean isSimple)
    {
        RField rfield = new RField();
        rfield.setId(id);
        rfield.setDfield(dfield);
        rfield.setDataType(dataType);
        rfield.setAttrFlag(attrFlag);
        rfield.setAddressType(addressType);
        rfield.setSheetID(sheetID);
        rfield.setAddress(address);
        rfield.setAlias(alias);
        rfield.setNotNullTip(notNullTip);
        rfield.setPosition(position);
        if (hyperLink != null)
        {
            rfield.setHyperLink(hyperLink.clone(isSimple));
        }
        if (dataRuleCond != null)
        {
            rfield.setDataRuleCond(dataRuleCond.clone(isSimple));
        }
        rfield.setValues(values);
        return rfield;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof RField)
        {
            if (id != null && id.equals(((RField)obj).getId()))
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
        return dfield.getName();
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
        // 对应的物理字段
        if (dfield != null)
        {
            params.put(ParamCons.DFIELD, dfield.getJsonObj());
        }
        // 数据类型
        if (dataType != null)
        {
            params.put(ParamCons.DATATYPE, dataType.getJsonObj());
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 目标类型
        params.put(ParamCons.ADDRESSTYPE, addressType);
        // 目标所在的sheetID
        params.put(ParamCons.SHEETID, sheetID);
        // 目标地址
        params.put(ParamCons.ADDRESS, address);
        // 别名
        params.put(ParamCons.ALIAS, alias);
        // 必填提示
        params.put(ParamCons.NOTNULLTIP, notNullTip);
        // 位置
        params.put(ParamCons.POSITION, position);
        // 超链接
        if (hyperLink != null)
        {
            params.put(ParamCons.HYPERLINK, hyperLink.getJsonObj());
        }
        // 数据规范
        if (dataRuleCond != null)
        {
            params.put(ParamCons.DATARULECOND, dataRuleCond.getJsonObj());
        }
        // 值
        params.put(ParamCons.VALUES, values);

        return params;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return RField 逻辑表对象
     */
    @ SuppressWarnings("unchecked")
    public RField convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        // id
        setId(HashMapTools.getLong(paramsMap, ParamCons.ID));
        // 对应的物理字段
        Object objValue = paramsMap.get(ParamCons.DFIELD);
        if (objValue != null)
        {
            DataField dfield = null;
            if (objValue instanceof HashMap)
            {
                dfield = new DataField();
                dfield.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDfield(dfield);
        }
        objValue = paramsMap.get(ParamCons.DATATYPE);
        if (objValue != null)
        {
            DataType dataType = null;
            if (objValue instanceof HashMap)
            {
                dataType = new DataType();
                dataType.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDataType(dataType);
        }
        // 基本属性集
        longValue = HashMapTools.getLong(paramsMap, ParamCons.ATTRFLAG);
        if (longValue != -1)
        {
            setAttrFlag(longValue);
        }
        // 目标类型
        shortValue = HashMapTools.getShort(paramsMap, ParamCons.ADDRESSTYPE);
        if (shortValue != -1)
        {
            setAddressType(shortValue);
        }
        // 目标所在的sheetID
        intValue = HashMapTools.getInt(paramsMap, ParamCons.SHEETID);
        if (longValue != -1)
        {
            setSheetID(intValue);
        }
        // 目标地址
        stringValue = HashMapTools.getString(paramsMap, ParamCons.ADDRESS);
        if (stringValue != null)
        {
            setAddress(stringValue);
        }
        // 别名
        stringValue = HashMapTools.getString(paramsMap, ParamCons.ALIAS);
        if (stringValue != null)
        {
            setAlias(stringValue);
        }
        // 必填提示
        stringValue = HashMapTools.getString(paramsMap, ParamCons.NOTNULLTIP);
        if (stringValue != null)
        {
            setNotNullTip(stringValue);
        }
        // 位置
        intValue = HashMapTools.getInt(paramsMap, ParamCons.POSITION);
        if (longValue != -1)
        {
            setPosition(intValue);
        }
        // 超链接
        objValue = paramsMap.get(ParamCons.HYPERLINK);
        if (objValue != null)
        {
            HyperLink hyperLink = null;
            if (objValue instanceof HashMap)
            {
                hyperLink = new HyperLink();
                hyperLink.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setHyperLink(hyperLink);
        }
        // 数据规范
        objValue = paramsMap.get(ParamCons.DATARULECOND);
        if (objValue != null)
        {
            DataRuleCond dataRuleCond = null;
            if (objValue instanceof HashMap)
            {
                dataRuleCond = new DataRuleCond();
                dataRuleCond.convetJsonToObj((HashMap<String, Object>)objValue);
            }
            setDataRuleCond(dataRuleCond);
        }
        // 值
        setValues(paramsMap.get(ParamCons.VALUES));

        return this;
    }
}