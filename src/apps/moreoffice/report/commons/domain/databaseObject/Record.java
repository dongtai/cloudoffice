package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 记录
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
public class Record extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 8430441896114374238L;
    // id
    private Long id;
    // 当前记录所在的模板
    private transient Template template;
    // 创建者所在部门ID
    private Long creatorOrgID;
    // 基本属性集
    private Long attrFlag;
    // 锁状态
    private Long lockState;
    // 数据记录索引
    private transient Set<RecordIndex> recordIndexs;
    // 创建者ID
    private Long creatorId;
    // 创建时间
    private Date createDate;
    // 修改者ID
    private Long modifierId;
    // 修改时间
    private Date modifyDate;

    // 模板ID
    private long templateID;
    // RTable(主要是记录)
    private Set<RTable> rtables;
    // 是否是导入记录模式
    private boolean isImportData;

    /**
     * 默认构造器
     */
    public Record()
    {
        attrFlag = (long)0;
        lockState = (long)0;
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
        if (template != null)
        {
            templateID = template.getId();
        }
        this.template = template;
    }

    /**
     * @return 返回 creatorOrgID
     */
    public Long getCreatorOrgID()
    {
        return creatorOrgID;
    }

    /**
     * @param creatorOrgID 设置 creatorOrgID
     */
    public void setCreatorOrgID(Long creatorOrgID)
    {
        this.creatorOrgID = creatorOrgID;
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
     * @return 返回 lockState
     */
    public Long getLockState()
    {
        return lockState;
    }

    /**
     * @param lockState 设置 lockState
     */
    public void setLockState(Long lockState)
    {
        this.lockState = lockState;
    }

    /**
     * 得到锁定状态
     * 
     * @return flag 标记位
     */
    public boolean isLock(int flag)
    {
        return DomainTools.isLongFlag(lockState, flag);
    }

    /**
     * 设置锁定状态
     * 
     * @param flag 标记位
     * @param lock 锁定状态
     */
    public void setLock(int flag, boolean lock)
    {
        lockState = DomainTools.setLongFlag(lockState, flag, lock);
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
        this.createDate = createDate;
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
        this.modifyDate = modifyDate;
    }

    /**
     * @return 返回 templateID
     */
    public long getTemplateID()
    {
        return templateID;
    }

    /**
     * @param templateID 设置 templateID
     */
    public void setTemplateID(long templateID)
    {
        this.templateID = templateID;
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
     * @return 返回 isImportData
     */
    public boolean isImportData()
    {
        return isImportData;
    }

    /**
     * @param isImportData 设置 isImportData
     */
    public void setImportData(boolean isImportData)
    {
        this.isImportData = isImportData;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public Record clone(boolean isSimple)
    {
        Record record = new Record();
        record.setId(id);
        record.setCreatorOrgID(creatorOrgID);
        record.setAttrFlag(attrFlag);
        record.setLockState(lockState);
        record.setCreatorId(creatorId);
        record.setCreateDate(createDate);
        record.setModifierId(modifierId);
        record.setModifyDate(modifyDate);
        record.setTemplateID(templateID);
        if (!isSimple && rtables != null && !rtables.isEmpty())
        {
            HashSet<RTable> set = new HashSet<RTable>();
            for (RTable rtable : rtables)
            {
                set.add(rtable.clone(true));
            }
            record.setRtables(set);
        }

        return record;
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
        // 创建者所在部门ID
        params.put(ParamCons.CREATORORGID, creatorOrgID);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);
        // 锁状态
        params.put(ParamCons.LOCKSTATE, lockState);
        // 数据记录索引
        if (recordIndexs != null && !recordIndexs.isEmpty())
        {
            ArrayList<HashMap<String, Object>> recordIndexJ = new ArrayList<HashMap<String, Object>>();
            for (RecordIndex recordIndex : recordIndexs)
            {
                recordIndexJ.add(recordIndex.getJsonObj());
            }
            params.put(ParamCons.RECORDINDEXS, recordIndexJ);
        }
        // 创建者ID
        params.put(ParamCons.CREATORID, creatorId);
        // 创建时间
        params.put(ParamCons.CREATEDATE, createDate);
        // 修改者ID
        params.put(ParamCons.MODIFIERID, modifierId);
        // 修改时间
        params.put(ParamCons.MODIFYDATE, modifyDate);

        // 模板ID
        params.put(ParamCons.TEMPLATEID, template == null ? templateID : template.getId());
        // RTable(主要是记录)
        if (rtables != null && !rtables.isEmpty())
        {
            ArrayList<HashMap<String, Object>> rtableJ = new ArrayList<HashMap<String, Object>>();
            for (RTable rtable : rtables)
            {
                rtableJ.add(rtable.getJsonObj());
            }
            params.put(ParamCons.RTABLES, rtableJ);
        }
        // 是否是导入记录模式
        params.put(ParamCons.ISIMPORTDATA, isImportData);

        return params;
    }
}