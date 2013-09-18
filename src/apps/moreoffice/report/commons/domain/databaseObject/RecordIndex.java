package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 记录索引
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-9
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class RecordIndex extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 2745944516954147324L;
    // id
    private Long id;
    /**
     * 对应的逻辑表
     * 如果对应物理表，则在保存记录时，无法拿到对应的RTable中的信息
     * 而且理论上貌似记录索引是与模板绑定的，所以应该引用RTable
     */
    private RTable rtable;
    // 数据记录id
    private Long dataID;

    // 记录索引所在的记录
    private transient Record record;

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
     * @return 返回 dataID
     */
    public Long getDataID()
    {
        return dataID;
    }

    /**
     * @param dataID 设置 dataID
     */
    public void setDataID(Long dataID)
    {
        this.dataID = dataID;
    }

    /**
     * @return 返回 record
     */
    public Record getRecord()
    {
        return record;
    }

    /**
     * @param record 设置 record
     */
    public void setRecord(Record record)
    {
        this.record = record;
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
        // 对应的逻辑表
        if (rtable != null)
        {
            params.put(ParamCons.RTABLE, rtable.getJsonObj());
        }
        // 数据记录id
        params.put(ParamCons.DATAID, dataID);

        return params;
    }
}