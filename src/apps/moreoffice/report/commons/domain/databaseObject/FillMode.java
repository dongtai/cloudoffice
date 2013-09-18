package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 填充方式
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
public class FillMode extends DataBaseObject
{
    // 序列化ID
    private static final long serialVersionUID = 2368075079840018310L;
    // id
    private Long id;
    // 填充到的报表
    private RTable rtable;
    // 填充项
    private Set<FillModeItem> fillModeItems;
    /**
     * 基本属性集
     * 0:填充后自动删除多余的行
     * 1:填充前清空目的字段数据区域
     */
    private Long attrFlag = (long)0;

    // 引用FillMode的ReadRule
    private transient ReadRule readRule;

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
     * 得到填充完毕后删除最后一行之下的所有行
     * 
     * @return boolean 是否填充完毕后删除最后一行之下的所有行
     */
    public boolean getRemoveRow()
    {
        return DomainTools.isLongFlag(attrFlag, 0);
    }

    /**
     * 设置填充完毕后删除最后一行之下的所有行
     * 
     * @param removeRow 填充完毕后删除最后一行之下的所有行
     */
    public void setRemoveRow(boolean removeRow)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 0, removeRow);
    }

    /**
     * 得到执行公式前清空数据字段
     * 
     * @return boolean 是否执行公式前清空数据字段
     */
    public boolean getClearData()
    {
        return DomainTools.isLongFlag(attrFlag, 1);
    }

    /**
     * 设置执行公式前清空数据字段
     * 
     * @param clearData 执行公式前清空数据字段
     */
    public void setClearData(boolean clearData)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, 1, clearData);
    }

    /**
     * @return 返回 readRule
     */
    public ReadRule getReadRule()
    {
        return readRule;
    }

    /**
     * @param readRule 设置 readRule
     */
    public void setReadRule(ReadRule readRule)
    {
        this.readRule = readRule;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public FillMode clone(boolean isSimple)
    {
        FillMode fillMode = new FillMode();
        fillMode.setId(id);
        fillMode.setRtable(isClient() ? rtable : rtable.clone(true));
        if (fillModeItems != null && !fillModeItems.isEmpty())
        {
            HashSet<FillModeItem> set = new HashSet<FillModeItem>();
            for (FillModeItem fillModeItem : fillModeItems)
            {
                set.add(fillModeItem.clone(isSimple));
            }
            fillMode.setFillModeItems(set);
        }
        fillMode.setAttrFlag(attrFlag);

        return fillMode;
    }

    /**
     * 判断两个对象是否相等
     * 
     * @param obj 需要判断的对象
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof FillMode)
        {
            if (id != null && id.equals(((FillMode)obj).getId()))
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
        // 填充项
        if (fillModeItems != null && !fillModeItems.isEmpty())
        {
            ArrayList<HashMap<String, Object>> fillModeItemsJ = new ArrayList<HashMap<String, Object>>();
            for (FillModeItem fillModeItem : fillModeItems)
            {
                fillModeItemsJ.add(fillModeItem.getJsonObj());
            }
            params.put(ParamCons.FILLMODEITEMS, fillModeItemsJ);
        }
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }
}