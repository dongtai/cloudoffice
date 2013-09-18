package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 回写
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-8-7
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class WriteMode extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = -5595048782353628511L;
    // id
    private Long id;
    // 填充到的报表
    private RTable rtable;
    // 回写项
    private Set<WriteModeItem> writeModeItems;

    // 引用WriteMode的NewFormRule
    private transient NewFormRule newFormRule;

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
     * @return 返回 newFormRule
     */
    public NewFormRule getNewFormRule()
    {
        return newFormRule;
    }

    /**
     * @param newFormRule 设置 newFormRule
     */
    public void setNewFormRule(NewFormRule newFormRule)
    {
        this.newFormRule = newFormRule;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public WriteMode clone(boolean isSimple)
    {
        WriteMode writeMode = new WriteMode();
        writeMode.setId(id);
        writeMode.setRtable(isClient() ? rtable : rtable.clone(true));
        if (writeModeItems != null && !writeModeItems.isEmpty())
        {
            HashSet<WriteModeItem> set = new HashSet<WriteModeItem>();
            for (WriteModeItem writeModeItem : writeModeItems)
            {
                set.add(writeModeItem.clone(isSimple));
            }
            writeMode.setWriteModeItems(set);
        }

        return writeMode;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof WriteMode)
        {
            if (id != null && id.equals(((WriteMode)obj).getId()))
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
        // 填充到的报表
        if (rtable != null)
        {
            params.put(ParamCons.RTABLE, rtable.getJsonObj());
        }
        // 回写项
        if (writeModeItems != null && !writeModeItems.isEmpty())
        {
            ArrayList<HashMap<String, Object>> writeModeItemJ = new ArrayList<HashMap<String, Object>>();
            for (WriteModeItem writeModeItem : writeModeItems)
            {
                writeModeItemJ.add(writeModeItem.getJsonObj());
            }
            params.put(ParamCons.WRITEMODEITEMS, writeModeItemJ);
        }

        return params;
    }
}