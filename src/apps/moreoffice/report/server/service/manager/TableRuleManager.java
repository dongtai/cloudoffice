package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.AddDetailRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataSource;
import apps.moreoffice.report.commons.domain.databaseObject.DelDetailRule;
import apps.moreoffice.report.commons.domain.databaseObject.DelFormRule;
import apps.moreoffice.report.commons.domain.databaseObject.FillMode;
import apps.moreoffice.report.commons.domain.databaseObject.FillModeItem;
import apps.moreoffice.report.commons.domain.databaseObject.JoinCond;
import apps.moreoffice.report.commons.domain.databaseObject.ModifyRule;
import apps.moreoffice.report.commons.domain.databaseObject.NewFormRule;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.ReadRule;
import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.RecordIndex;
import apps.moreoffice.report.commons.domain.databaseObject.TableRule;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.databaseObject.WriteMode;
import apps.moreoffice.report.commons.domain.databaseObject.WriteModeItem;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.commons.formula.RFormula;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableRuleDB;

/**
 * 表间规则管理器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TableRuleManager extends BaseManager
{
    // 数据库操作接口
    private ITableRuleDB db;
    // 表和字段管理器
    private TableManager tableM;
    // 记录管理器
    private RecordManager recordM;
    // 数据规范管理器
    private DataRuleManager dataRuleM;

    /**
     * 对TableRule进行存盘前处理
     * 
     * @param template 模板对象
     */
    protected void beforeSave(Template template)
    {
        Set<TableRule> tableRules = template.getTableRules();
        if (tableRules == null || tableRules.isEmpty())
        {
            return;
        }

        for (TableRule tableRule : tableRules)
        {
            tableRule.setCurrentTemplate(template);
            if (tableRule instanceof ReadRule)
            {
                ReadRule readRule = (ReadRule)tableRule;
                Set<FillMode> fillModes = readRule.getFillModes();
                if (fillModes != null && !fillModes.isEmpty())
                {
                    for (FillMode fillMode : fillModes)
                    {
                        fillMode.setReadRule(readRule);
                    }
                }
                Set<JoinCond> joinConds = readRule.getJoinConds();
                if (joinConds != null && !joinConds.isEmpty())
                {
                    for (JoinCond joinCond : joinConds)
                    {
                        joinCond.setReadRule(readRule);
                    }
                }
            }
            else if (tableRule instanceof ModifyRule)
            {
                ModifyRule modifyRule = (ModifyRule)tableRule;
                Set<WriteModeItem> writeModeItems = modifyRule.getWriteModeItems();
                if (writeModeItems != null && !writeModeItems.isEmpty())
                {
                    for (WriteModeItem writeModeItem : writeModeItems)
                    {
                        writeModeItem.setModifyRule(modifyRule);
                    }
                }
            }
            else if (tableRule instanceof AddDetailRule)
            {
                AddDetailRule addDetailRule = (AddDetailRule)tableRule;
                Set<WriteModeItem> writeModeItems = addDetailRule.getWriteModeItems();
                if (writeModeItems != null && !writeModeItems.isEmpty())
                {
                    for (WriteModeItem writeModeItem : writeModeItems)
                    {
                        writeModeItem.setAddDetailRule(addDetailRule);
                    }
                }
            }
            else if (tableRule instanceof NewFormRule)
            {
                NewFormRule newFormRule = (NewFormRule)tableRule;
                Set<WriteMode> writeModes = newFormRule.getWriteModes();
                if (writeModes != null && !writeModes.isEmpty())
                {
                    for (WriteMode writeMode : writeModes)
                    {
                        Set<WriteModeItem> writeModeItems = writeMode.getWriteModeItems();
                        if (writeModeItems != null && !writeModeItems.isEmpty())
                        {
                            for (WriteModeItem writeModeItem : writeModeItems)
                            {
                                writeModeItem.setWriteMode(writeMode);
                            }
                        }
                        writeMode.setNewFormRule(newFormRule);
                    }
                }
            }
        }
    }

    /**
     * 执行表间规则
     * 
     * @param userID 用户ID
     * @param tableRuleIDs 表间规则ID列表
     * @param currentData 当前报表中用户填写的数据
     * @return Vector<Vector<Object>> 结果
     */
    @ SuppressWarnings("rawtypes")
    public Vector<Vector<Object>> execTableRules(long userID, ArrayList tableRuleIDs,
        HashMap<String, Object> currentData)
    {
        // 添加系统变量值到currentData中
        currentData.put(ReportCommonResource.SYSVAR, getSysVarValue(userID));

        Vector<Vector<Object>> values = null;
        for (int i = 0, size = tableRuleIDs.size(); i < size; i++)
        {
            values = execTableRules(userID, db.getTableRuleByID((Integer)tableRuleIDs.get(i)),
                currentData);
        }

        // 只有一个表间规则时，才有返回值，如果多个表间规则，则返回最后一个返回值(理论上如果执行多个表间规则，则不会要返回值)
        return values;
    }

    /**
     * 得到系统变量值
     * 
     * @param userID 用户ID
     * @return HashMap<String, Object> 系统变量值
     */
    public HashMap<String, Object> getSysVarValue(long userID)
    {
        HashMap<String, Object> sysVarValue = new HashMap<String, Object>();
        for (String sysVar : ReportCommonResource.SYSVARS)
        {
            sysVarValue.put(sysVar, dataRuleM.getSysVarData(userID, sysVar));
        }
        return sysVarValue;
    }

    /*
     * 执行表间规则
     */
    private Vector<Vector<Object>> execTableRules(long userID, TableRule tableRule,
        HashMap<String, Object> currentData)
    {
        if (tableRule instanceof ReadRule)
        {
            return execReadRule((ReadRule)tableRule, currentData);
        }
        else if (tableRule instanceof ModifyRule)
        {
            execModifyRule((ModifyRule)tableRule, currentData);
        }
        else if (tableRule instanceof AddDetailRule)
        {
            execAddDetailRule(userID, (AddDetailRule)tableRule, currentData);
        }
        else if (tableRule instanceof DelDetailRule)
        {
            execDelDetailRule(userID, (DelDetailRule)tableRule, currentData);
        }
        else if (tableRule instanceof DelFormRule)
        {
            execDelFormRule(userID, (DelFormRule)tableRule, currentData);
        }
        return null;
    }

    /*
     * 执行提数规则
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public Vector<Vector<Object>> execReadRule(ReadRule tableRule,
        HashMap<String, Object> currentData)
    {
        // 得到从哪里提数
        Set<FillMode> fillModes = tableRule.getFillModes();
        if (fillModes == null || fillModes.size() < 1)
        {
            return null;
        }

        RFormula expressFormula;
        // 排序数据
        Vector<Short> sortTypes = new Vector<Short>();
        // 提数的表达式数组
        Vector<RFormula> express = new Vector<RFormula>();
        // 需要填充的数据项数组
        Vector<Object> fillFieldNames = new Vector<Object>();
        Vector resultVec = new Vector();
        for (FillMode fillMode : fillModes)
        {
            Set<FillModeItem> fillModeItems = fillMode.getFillModeItems();
            if (fillModeItems != null && !fillModeItems.isEmpty())
            {
                for (FillModeItem fillModeItem : fillModeItems)
                {
                    expressFormula = RFormula.convertFormula(fillModeItem.getExpression());
                    if (expressFormula != null)
                    {
                        expressFormula.convert(currentData);
                    }
                    express.add(expressFormula);
                    sortTypes.add(fillModeItem.getSortType());
                    fillFieldNames.add(fillModeItem.getRfield().getRealName());
                }
            }

            // 得到筛选条件
            RFormula filterCond = RFormula.convertFormula(tableRule.getFilterCond());
            if (filterCond != null)
            {
                filterCond.convert(currentData);
            }

            // 数据源
            DataSource dataSource = null;
            Set<RTable> rtables = tableRule.getRtables();
            for (RTable rtable : rtables)
            {
                dataSource = rtable.getDtable().getDataSource();
                if (dataSource != null)
                {
                    break;
                }
            }
            // 从数据库中提数
            Vector values = db.execReadRule(dataSource, express, tableRule.getJoinConds(),
                filterCond, sortTypes);
            values.add(0, fillFieldNames);
            values.add(0, fillMode.getRtable().getRealName());
            resultVec.add(values);
        }

        return resultVec;
    }

    /*
     * 执行修改规则
     */
    public void execModifyRule(ModifyRule tableRule, HashMap<String, Object> currentData)
    {
        Set<WriteModeItem> writeModeItems = tableRule.getWriteModeItems();
        if (writeModeItems == null || writeModeItems.size() < 1)
        {
            return;
        }

        // 得到表名
        String tableName = tableRule.getRtable().getRealName();

        // 字段名数组
        String[] fieldNames = new String[writeModeItems.size()];
        // 回写表达式
        RFormula[] express = new RFormula[fieldNames.length];
        int index = 0;
        for (WriteModeItem writeModeItem : writeModeItems)
        {
            express[index] = RFormula.convertFormula(writeModeItem.getExpression());
            if (express[index] != null)
            {
                express[index].convert(currentData);
            }
            fieldNames[index] = writeModeItem.getRfield().getRealName();
            index++;
        }

        // 得到筛选条件
        RFormula filterCond = RFormula.convertFormula(tableRule.getFilterCond());
        if (filterCond != null)
        {
            filterCond.convert(currentData);
        }

        db.execModifyRule(tableName, fieldNames, express, filterCond);
    }

    /*
     * 执行补充明细规则
     */
    @ SuppressWarnings("rawtypes")
    public void execAddDetailRule(long userID, AddDetailRule tableRule,
        HashMap<String, Object> currentData)
    {
        Set<WriteModeItem> writeModeItems = tableRule.getWriteModeItems();
        if (writeModeItems == null || writeModeItems.size() < 1)
        {
            return;
        }

        // 得到表名
        String tableName = tableRule.getRtable().getRealName();

        // 字段名数组
        String[] fieldNames = new String[writeModeItems.size()];
        // 字段名对应的值
        Object[] values = new Object[fieldNames.length];
        int index = 0;
        RFormula formula;
        for (WriteModeItem writeModeItem : writeModeItems)
        {
            formula = RFormula.convertFormula(writeModeItem.getExpression());
            if (formula != null)
            {
                formula.convert(currentData);
            }

            Object formulaValue = db.calculatorFormula(formula);
            if (formulaValue instanceof Vector)
            {
                values[index] = ((Vector)((Vector)formulaValue).get(0)).get(0);
                fieldNames[index] = writeModeItem.getRfield().getRealName();
            }
            index++;
        }

        // 得到筛选条件
        RFormula filterCond = RFormula.convertFormula(tableRule.getFilterCond());
        if (filterCond != null)
        {
            filterCond.convert(currentData);
        }

        // 先在明细表中插入记录
        long recordID = db.insertRecord(tableName, fieldNames, values);

        // 记录的筛选条件
        RecordIndex recordIndex;
        RFormula primaryTableCond = RFormula.convertFormula(tableRule.getPrimaryTableCond());
        if (primaryTableCond == null)
        {
            // 如果是null，则是向每个记录的明细表中添加明细，是否要提示用户？
            Set<Record> records = tableRule.getTemplate().getRecords();
            if (records != null && !records.isEmpty())
            {
                for (Record record : records)
                {
                    recordIndex = new RecordIndex();
                    recordIndex.setRecord(record);
                    recordIndex.setDataID(recordID);
                    recordIndex.setRtable(tableRule.getRtable());
                    db.save(recordIndex);
                }
            }
            return;
        }

        // 当前模板中引用到的所有主表名
        ArrayList<RTable> rtables = tableM.getRTableList(userID, TableCons.TEMPLATE, tableRule
            .getTemplate().getName(), TableCons.SINGLETABLE, PermissionCons.CANWRITE,
            PermissionCons.CANNEW);
        String[] mainTableNames = new String[rtables.size()];
        for (int i = 0, size = mainTableNames.length; i < size; i++)
        {
            mainTableNames[i] = rtables.get(i).getRealName();
        }

        // 从主表里查询符合条件的记录ID
        long id;
        RTable rtable;
        // 需要补充明细的记录数组
        ArrayList<Record> records = new ArrayList<Record>();
        Vector<Vector<Object>> idVec = db.queryDataIDByFormula(mainTableNames, primaryTableCond);
        Vector<Object> tableNameVec = idVec.get(0);
        for (int i = 0; i < tableNameVec.size(); i++)
        {
            rtable = rtables.get(i);
            for (int j = 1, size = idVec.size(); j < size; j++)
            {
                id = ((Number)idVec.get(j).get(i)).longValue();
                recordIndex = recordM.getRecordIndex(rtable.getId(), id);
                if (!records.contains(recordIndex.getRecord()))
                {
                    records.add(recordIndex.getRecord());
                }
            }
        }

        // 保存记录索引对象
        if (!records.isEmpty())
        {
            for (Record record : records)
            {
                recordIndex = new RecordIndex();
                recordIndex.setRecord(record);
                recordIndex.setDataID(recordID);
                recordIndex.setRtable(tableRule.getRtable());
                db.save(recordIndex);
            }
        }
    }

    /*
     * 执行删除明细规则
     */
    private void execDelDetailRule(long userID, DelDetailRule tableRule,
        HashMap<String, Object> currentData)
    {
        // 得到筛选条件
        RFormula filterCond = RFormula.convertFormula(tableRule.getFilterCond());
        if (filterCond != null)
        {
            filterCond.convert(currentData);
        }

        // 当前模板中引用到的所有主表名
        ArrayList<RTable> rtables = tableM.getRTableList(userID, TableCons.TEMPLATE, tableRule
            .getTemplate().getName(), TableCons.SINGLETABLE, PermissionCons.CANWRITE,
            PermissionCons.CANDELETE);
        String[] mainTableNames = new String[rtables.size() + 1];
        // 需要删除明细的重复表
        mainTableNames[0] = tableRule.getRtable().getRealName();
        for (int i = 1, size = mainTableNames.length; i < size; i++)
        {
            mainTableNames[i] = rtables.get(i - 1).getRealName();
        }

        // 得到所有符合条件的表和id
        Vector<Vector<Object>> idsVec = db.queryDataIDByFormula(mainTableNames, filterCond);
        // 如果碰到既有主表条件又有明细表条件的公式怎么执行？
        // 删除符合主表条件的明细
        long dataID;
        RTable rtable;
        String tableName;
        Vector<Object> idVec;
        RecordIndex recordIndex;
        Set<RecordIndex> recordIndexs;
        Vector<Object> tableNameVec = idsVec.get(0);
        if (tableNameVec != null && !tableNameVec.isEmpty())
        {
            for (int i = 0, size = tableNameVec.size(); i < size; i++)
            {
                rtable = null;
                tableName = tableNameVec.get(i).toString();
                for (RTable temp : rtables)
                {
                    if (temp.getRealName().equals(tableName))
                    {
                        rtable = temp;
                        break;
                    }
                }

                if (rtable == null)
                {
                    continue;
                }

                for (int j = 1, count = idsVec.size(); j < count; j++)
                {
                    dataID = ((Number)idsVec.get(j).get(i)).longValue();
                    recordIndex = recordM.getRecordIndex(rtable.getId(), dataID);
                    recordIndexs = recordIndex.getRecord().getRecordIndexs();
                    for (RecordIndex index : recordIndexs)
                    {
                        if (index.getRtable().equals(tableRule.getRtable()))
                        {
                            db.delete(recordIndex);
                        }
                    }
                }
            }
        }
        // 删除符合明细条件的明细
        for (int i = 1, size = idsVec.size(); i < size; i++)
        {
            idVec = idsVec.get(i);
            if (idVec.get(0) instanceof Number)
            {
                recordM.deleteRecordIndex(tableRule.getRtable().getId(),
                    ((Number)idVec.get(0)).longValue());
            }
        }
    }

    /*
     * 执行删除表单规则
     */
    private void execDelFormRule(long userID, DelFormRule tableRule,
        HashMap<String, Object> currentData)
    {
        // 得到筛选条件
        RFormula filterCond = RFormula.convertFormula(tableRule.getFilterCond());
        if (filterCond != null)
        {
            filterCond.convert(currentData);
        }

        // 当前模板中引用到的所有主表名
        ArrayList<RTable> rtables = tableM.getRTableList(userID, TableCons.TEMPLATE, tableRule
            .getTemplate().getName(), TableCons.ALLTABLE, PermissionCons.CANWRITE,
            PermissionCons.CANDELETE);
        String[] mainTableNames = new String[rtables.size()];
        for (int i = 0, size = mainTableNames.length; i < size; i++)
        {
            mainTableNames[i] = rtables.get(i).getRealName();
        }

        // 得到所有符合条件的表和id
        Vector<Vector<Object>> idsVec = db.queryDataIDByFormula(mainTableNames, filterCond);
        long dataID;
        RTable rtable;
        String tableName;
        RecordIndex recordIndex;
        ArrayList<Record> records = new ArrayList<Record>();
        Vector<Object> tableNameVec = idsVec.get(0);
        if (tableNameVec != null && !tableNameVec.isEmpty())
        {
            for (int i = 0, size = tableNameVec.size(); i < size; i++)
            {
                rtable = null;
                tableName = tableNameVec.get(i).toString();
                for (RTable temp : rtables)
                {
                    if (temp.getRealName().equals(tableName))
                    {
                        rtable = temp;
                        break;
                    }
                }

                if (rtable == null)
                {
                    continue;
                }

                for (int j = 1, count = idsVec.size(); j < count; j++)
                {
                    dataID = ((Number)idsVec.get(j).get(i)).longValue();
                    recordIndex = recordM.getRecordIndex(rtable.getId(), dataID);
                    if (recordIndex != null && !records.contains(recordIndex.getRecord()))
                    {
                        records.add(recordIndex.getRecord());
                    }
                }
            }
        }

        if (!records.isEmpty())
        {
            for (Record record : records)
            {
                recordM.deleteRecord(tableRule.getTemplate().getId(), record.getId());
            }
        }
    }

    /**
     * @param db 设置 db
     */
    public void setDb(ITableRuleDB db)
    {
        this.db = db;
        setBasedb(db);
    }

    /**
     * @param tableM 设置 tableM
     */
    public void setTableM(TableManager tableM)
    {
        this.tableM = tableM;
    }

    /**
     * @param recordM 设置 recordM
     */
    public void setRecordM(RecordManager recordM)
    {
        this.recordM = recordM;
    }

    /**
     * @param dataRuleM 设置 dataRuleM
     */
    public void setDataRuleM(DataRuleManager dataRuleM)
    {
        this.dataRuleM = dataRuleM;
    }
}