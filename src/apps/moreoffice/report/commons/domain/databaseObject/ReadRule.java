package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.DomainTools;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;

/**
 * 表间规则：提数
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
public class ReadRule extends TableRule
{
    // 序列化ID
    private static final long serialVersionUID = -1559911415295951995L;
    // 数据源
    private Set<RTable> rtables;
    // 关联条件
    private Set<JoinCond> joinConds;
    // 筛选条件
    private String filterCond;
    // 填充数据表
    private RTable fillTable;
    // 填充方式
    private Set<FillMode> fillModes;
    // 执行条件
    private String execCond;
    // 提取前N条记录
    private Long displayNumber;
    // 基本属性集
    private Long attrFlag;

    /**
     * 默认构造器
     */
    public ReadRule()
    {
        super();
        attrFlag = (long)1;
        setType(TableRuleCons.READ_RULE);
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
     * @return 返回 joinConds
     */
    public Set<JoinCond> getJoinConds()
    {
        return joinConds;
    }

    /**
     * @param joinConds 设置 joinConds
     */
    public void setJoinConds(Set<JoinCond> joinConds)
    {
        this.joinConds = joinConds;
    }

    /**
     * @return 返回 filterCond
     */
    public String getFilterCond()
    {
        return filterCond;
    }

    /**
     * @param filterCond 设置 filterCond
     */
    public void setFilterCond(String filterCond)
    {
        this.filterCond = filterCond;
    }

    /**
     * @return 返回 fillTable
     */
    public RTable getFillTable()
    {
        return fillTable;
    }

    /**
     * @param fillTable 设置 fillTable
     */
    public void setFillTable(RTable fillTable)
    {
        this.fillTable = fillTable;
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
     * @return 返回 execCond
     */
    public String getExecCond()
    {
        return execCond;
    }

    /**
     * @param execCond 设置 execCond
     */
    public void setExecCond(String execCond)
    {
        this.execCond = execCond;
    }

    /**
     * @return 返回 displayNumber
     */
    public Long getDisplayNumber()
    {
        return displayNumber;
    }

    /**
     * @param displayNumber 设置 displayNumber
     */
    public void setDisplayNumber(Long displayNumber)
    {
        this.displayNumber = displayNumber;
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
     * 得到boolean属性
     * 
     * @param flag 标记位
     *        TableRuleCons.SHOWALLDATA:显示全部查询数据
     *        TableRuleCons.REPEATDATASHOWONE:重复数据只显示一次
     *        TableRuleCons.HIDEFORMULACONTENT:手工应用时隐藏公式内容
     * @return boolean boolean属性
     */
    public boolean getBooleanAttr(int flag)
    {
        return DomainTools.isLongFlag(attrFlag, flag);
    }

    /**
     * 设置boolean属性
     * 
     * @param flag 标记位
     *        TableRuleCons.SHOWALLDATA:显示全部查询数据
     *        TableRuleCons.REPEATDATASHOWONE:重复数据只显示一次
     *        TableRuleCons.HIDEFORMULACONTENT:手工应用时隐藏公式内容
     * @param value boolean属性
     */
    public void setBooleanAttr(int flag, boolean value)
    {
        attrFlag = DomainTools.setLongFlag(attrFlag, flag, value);
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public ReadRule clone(boolean isSimple)
    {
        ReadRule readRule = new ReadRule();
        clone(readRule, isSimple);
        if (rtables != null && !rtables.isEmpty())
        {
            HashSet<RTable> set = new HashSet<RTable>();
            for (RTable rtable : rtables)
            {
                set.add(isClient() ? rtable : rtable.clone(isSimple));
            }
            readRule.setRtables(set);
        }
        if (joinConds != null && !joinConds.isEmpty())
        {
            HashSet<JoinCond> set = new HashSet<JoinCond>();
            for (JoinCond joinCond : joinConds)
            {
                set.add(joinCond.clone(isSimple));
            }
            readRule.setJoinConds(set);
        }
        readRule.setFilterCond(filterCond);
        if (fillTable != null)
        {
            readRule.setFillTable(isClient() ? fillTable : fillTable.clone(isSimple));
        }
        if (fillModes != null && !fillModes.isEmpty())
        {
            HashSet<FillMode> set = new HashSet<FillMode>();
            for (FillMode fillMode : fillModes)
            {
                set.add(fillMode.clone(isSimple));
            }
            readRule.setFillModes(set);
        }
        readRule.setExecCond(execCond);
        readRule.setDisplayNumber(displayNumber);
        readRule.setAttrFlag(attrFlag);

        return readRule;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.putAll(super.getJsonObj());
        // 数据源
        if (rtables != null && !rtables.isEmpty())
        {
            ArrayList<HashMap<String, Object>> rtableJ = new ArrayList<HashMap<String, Object>>();
            for (RTable rtable : rtables)
            {
                rtableJ.add(rtable.getJsonObj());
            }
            params.put(ParamCons.RTABLES, rtableJ);
        }
        // 关联条件
        if (joinConds != null && !joinConds.isEmpty())
        {
            ArrayList<HashMap<String, Object>> joinCondJ = new ArrayList<HashMap<String, Object>>();
            for (JoinCond joinCond : joinConds)
            {
                joinCondJ.add(joinCond.getJsonObj());
            }
            params.put(ParamCons.JOINCONDS, joinCondJ);
        }
        // 筛选条件
        params.put(ParamCons.FILTERCOND, filterCond);
        // 填充数据表
        if (fillTable != null)
        {
            params.put(ParamCons.FILLTABLE, fillTable.getJsonObj());
        }
        // 填充方式
        if (fillModes != null && !fillModes.isEmpty())
        {
            ArrayList<HashMap<String, Object>> fillModeJ = new ArrayList<HashMap<String, Object>>();
            for (FillMode fillMode : fillModes)
            {
                fillModeJ.add(fillMode.getJsonObj());
            }
            params.put(ParamCons.FILLMODES, fillModeJ);
        }
        // 执行条件
        params.put(ParamCons.EXECCOND, execCond);
        // 提取前N条记录
        params.put(ParamCons.DISPLAYNUMBER, displayNumber);
        // 基本属性集
        params.put(ParamCons.ATTRFLAG, attrFlag);

        return params;
    }
}