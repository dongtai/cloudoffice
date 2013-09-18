package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.commons.domain.databaseObject.RField;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.ReadRule;
import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.RecordIndex;
import apps.moreoffice.report.commons.domain.databaseObject.TableRule;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.server.service.manager.dataCenter.IRecordDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 记录管理器
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
public class RecordManager extends BaseManager
{
    // 数据库操作接口
    private IRecordDB db;
    // 用户表操作接口
    private ISQLDB sqlDB;
    // 模板管理器
    private TemplateManager templateM;
    // 表和字段管理器
    private TableManager tableM;
    // 权限管理器
    private PermissionManager permissionM;
    // 表间规则管理器
    private TableRuleManager tableRuleM;

    // 记录锁定标记位
    // 手动锁定
    private final int manualLock = 0;

    /**
     * 保存记录
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        Record record = (Record)entity;

        // 设置创建或修改时间
        if (record.getCreateDate() == null)
        {
            record.setCreateDate(new Date());
        }
        else
        {
            record.setModifyDate(new Date());
        }

        // 与模板对象关联，客户端传过来的只是模板ID
        record.setTemplate(templateM.getTemplateByID(record.getTemplateID()));

        // 导入模式
        if (record.isImportData())
        {
            importData(record);
            return null;
        }

        /**
         * 在保存前，先获得以前的对象，不能放在后面，如果放在后面，就会拿到最新的
         * 而且还不能直接得到Record，然后再从Record中得到ReportIndex
         * 因为数据库会锁定此对象，当再次存储id相同的对象时，会认为错误 
         */
        List<RecordIndex> oldRecordIndexs = null;
        if (record.getId() != null)
        {
            oldRecordIndexs = db.getRecordIndex(record.getId());
        }

        int index;
        long recordID;
        long[] dataIDs;
        Object value;
        Object[] values;
        long recordCount;
        String[] fieldNames;
        String tableName;
        Set<RField> rfields;
        RecordIndex newRecordIndex;
        Set<RecordIndex> recordIndexs = new HashSet<RecordIndex>();
        Set<RTable> rtables = record.getRtables();
        for (RTable rtable : rtables)
        {
            // 得到字段列表
            fieldNames = rtable.getFieldNames();
            if (fieldNames == null || fieldNames.length == 0)
            {
                continue;
            }

            // 得到当前表里的记录个数
            recordCount = 0;
            recordCount = rtable.getRecordCount();
            if (recordCount < 1)
            {
                continue;
            }

            /**
             * 遍历每条记录进行存储
             * field1  field2  field3
             *  1_f1    1_f2    1_f3
             *  2_f1    2_f2    2_f3
             *  3_f1    3_f2    3_f3
             *  先按照纵向遍历，再按照横向遍历，可组合成多条记录值
             */
            index = 0;
            dataIDs = rtable.getDataIDs();
            rfields = rtable.getRfields();
            values = new Object[rfields.size()];
            tableName = rtable.getRealName();
            for (int j = 0; j < recordCount; j++)
            {
                index = 0;
                for (RField rfield : rfields)
                {
                    value = rfield.getValues();
                    if (value instanceof Object[])
                    {
                        values[index++] = ((Object[])value)[j];
                    }
                    else
                    {
                        values[index++] = value;
                    }
                }

                if (dataIDs != null && dataIDs.length > j)
                {
                    sqlDB.modifyRecord(null, tableName, dataIDs[j], fieldNames, values);
                    newRecordIndex = findAndDelete(oldRecordIndexs, rtable.getId(), dataIDs[j]);
                    if (newRecordIndex == null)
                    {
                        newRecordIndex = new RecordIndex();
                        newRecordIndex.setDataID(dataIDs[j]);
                    }
                }
                else
                {
                    recordID = sqlDB.insertRecord(null, tableName, fieldNames, values);
                    if (recordID == -1)
                    {
                        continue;
                    }
                    newRecordIndex = new RecordIndex();
                    newRecordIndex.setDataID(recordID);
                }
                newRecordIndex.setRtable(rtable);
                recordIndexs.add(newRecordIndex);
            }
        }

        record.setRecordIndexs(recordIndexs);
        // 保存Record对象本身
        record = (Record)super.save(record);

        // 删除多余记录放在后面，否则数据库中有外键删不掉
        if (oldRecordIndexs != null && oldRecordIndexs.size() > 0)
        {
            RecordIndex oldRecordIndex;
            for (int i = 0, size = oldRecordIndexs.size(); i < size; i++)
            {
                oldRecordIndex = oldRecordIndexs.get(i);
                sqlDB.deleteRecord(null, oldRecordIndex.getRtable().getRealName(),
                    oldRecordIndex.getDataID());
                db.delete(oldRecordIndex);
            }
        }

        return record;
    }

    /*
     * 在保存记录索引时，先从原来的记录索引列表中找到修改的记录，并从原来的记录索引
     * 列表中删除之，剩下的就是需要删除的记录索引，最后统一删除
     */
    private RecordIndex findAndDelete(List<RecordIndex> oldRecordIndexs, long rtableID, long dataID)
    {
        if (oldRecordIndexs != null && oldRecordIndexs.size() > 0)
        {
            RecordIndex oldRecordIndex;
            for (int i = 0, size = oldRecordIndexs.size(); i < size; i++)
            {
                oldRecordIndex = oldRecordIndexs.get(i);
                if (oldRecordIndex.getRtable().getId() == rtableID
                    && oldRecordIndex.getDataID() == dataID)
                {
                    oldRecordIndexs.remove(oldRecordIndex);
                    return oldRecordIndex;
                }
            }
        }
        return null;
    }

    /**
     * 保存json格式的记录值
     * 
     * @param paramsMap 参数
     * @return Result 记录ID
     */
    public Result save(HashMap<String, Object> paramsMap)
    {
        // 得到模板对象
        String templateName = HashMapTools.getString(paramsMap, ParamCons.TEMPLATENAME);
        Template template = templateM.getTemplateByName(templateName);
        if (template == null)
        {
            return null;
        }

        // 创建记录对象
        Record record = new Record();
        record.setTemplateID(template.getId());
        record.setCreatorId(HashMapTools.getLong(paramsMap, ParamCons.USERID));

        // 获取记录值
        Set<RField> rfields;
        Set<RTable> rtables = template.getRtables();
        if (rtables == null || rtables.isEmpty())
        {
            return null;
        }
        for (RTable rtable : rtables)
        {
            rfields = rtable.getRfields();
            if (rfields == null || rfields.isEmpty())
            {
                continue;
            }
            for (RField rfield : rfields)
            {
                rfield.setValues(paramsMap.get(rtable.getRealName() + "." + rfield.getRealName()));
            }
        }
        record.setRtables(rtables);
        save(record);

        // 返回记录ID
        Result result = new Result();
        result.setData(record.getId());
        return result;
    }

    /*
     * 导入数据
     */
    private void importData(Record record)
    {
        // 得到导入的记录数
        long recordCount = 0;
        Set<RTable> rtables = record.getRtables();
        for (RTable rtable : rtables)
        {
            recordCount = recordCount > rtable.getRecordCount() ? recordCount : rtable
                .getRecordCount();
        }

        int index;
        Object value;
        int fieldSize;
        long recordID;
        Object[] rowData;
        Record tempRecord;
        Set<RField> rfields;
        String[] fieldNames;
        RecordIndex recordIndex;
        Set<RecordIndex> recordIndexs;
        for (int i = 0; i < recordCount; i++)
        {
            index = 0;
            // 克隆一个新的记录对象
            tempRecord = record.clone(true);
            tempRecord.setTemplate(record.getTemplate());
            recordIndexs = new HashSet<RecordIndex>();
            // 遍历每张表
            for (RTable rtable : rtables)
            {
                rfields = rtable.getRfields();
                if (rfields == null || rfields.size() == 0)
                {
                    continue;
                }
                // 获得字段名和字段值数组
                fieldSize = rfields.size();
                fieldNames = new String[fieldSize];
                rowData = new Object[fieldSize];
                for (RField rfield : rfields)
                {
                    fieldNames[index] = rfield.getRealName();
                    value = rfield.getValues();
                    if (value instanceof Object[])
                    {
                        rowData[index] = ((Object[])value)[i];
                    }
                    index++;
                }
                // 插入记录
                recordID = sqlDB.insertRecord(null, rtable.getRealName(), fieldNames, rowData);
                recordIndex = new RecordIndex();
                recordIndex.setDataID(recordID);
                recordIndex.setRtable(rtable);
                recordIndexs.add(recordIndex);
            }
            tempRecord.setRecordIndexs(recordIndexs);
            super.save(tempRecord);
        }
    }

    /**
     * 得到单一记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     * {
     *  [字段1名称，字段2名称，字段3名称。。。]
     *  [重复表1名称，重复表2名称，重复表3名称。。。]
     *  [记录id，锁状态，字段1值，字段2值，字段3值。。。]
     *  [记录id，锁状态，字段1值，字段2值，字段3值。。。]
     *  [。。。]
     * }
     */
    @ SuppressWarnings("unchecked")
    public Result getSingleData(long userID, String nodePath, int start, int number)
    {
        // 得到模板对象
        Template template = getTemplateByNodePath(nodePath);
        if (template == null)
        {
            return ErrorManager.getErrorResult("模板：" + nodePath + " 不存在");
        }

        // 得到所有表对象
        Set<RTable> rtables = template.getRtables();
        if (rtables == null || rtables.isEmpty())
        {
            return ErrorManager.getErrorResult("模板内没有定义对应的表");
        }

        // 得到需要显示的字段
        int index;
        Set<RField> rfields;
        String[] fieldNames;
        RField[] tempRFields;
        // 返回给客户端的临时重复表表名数组
        String[] tempRepeatTableNames = new String[rtables.size()];
        // 返回给客户端的字段名列表
        ArrayList<Object> fieldNameList = new ArrayList<Object>();
        // 缓存每张表对应的字段名数组，为了查询数据库用
        HashMap<String, String[]> fieldNameMap = new HashMap<String, String[]>();
        for (RTable rtable : rtables)
        {
            // 如果表隐藏，则继续
            if (rtable.isHide())
            {
                continue;
            }
            // 如果为重复表，则仅仅返回重复表名
            if (rtable.isRepeatTable() && rtable.getPosition() < rtables.size())
            {
                tempRepeatTableNames[rtable.getPosition()] = rtable.getRealName();
                continue;
            }
            // 得到RField集合
            rfields = rtable.getRfields();
            if (rfields == null || rfields.isEmpty())
            {
                continue;
            }

            // 过滤并排序RField
            tempRFields = new RField[rfields.size()];
            for (RField rfield : rfields)
            {
                if (rfield.isHide())
                {
                    continue;
                }

                tempRFields[rfield.getPosition()] = rfield;
            }
            // 得到rfield字段名列表
            for (int i = 0, size = rfields.size(); i < size; i++)
            {
                if (tempRFields[i] != null)
                {
                    fieldNameList.add(tempRFields[i].getRealName());
                }
            }
            fieldNames = new String[fieldNameList.size()];
            fieldNameList.toArray(fieldNames);
            fieldNameMap.put(rtable.getRealName(), fieldNames);
        }
        if (fieldNameMap.isEmpty())
        {
            return null;
        }

        RTable rtable;
        Vector<Object> value;
        ArrayList<Object> rowData;
        Vector<Vector<Object>> values;
        Set<RecordIndex> recordIndexs;
        ArrayList<ArrayList<Object>> rowDatas = new ArrayList<ArrayList<Object>>();
        rowDatas.add(fieldNameList);
        // 返回给客户端的重复表名列表
        ArrayList<Object> repeatNameList = new ArrayList<Object>();
        for (int i = 0, len = tempRepeatTableNames.length; i < len; i++)
        {
            if (tempRepeatTableNames[i] != null)
            {
                repeatNameList.add(tempRepeatTableNames[i]);
            }
        }
        rowDatas.add(repeatNameList);
        Result result = new Result();

        // 权限判断
        boolean canRead = false;
        ArrayList<Permission> permissions = permissionM.getPermission(PermissionCons.TEMPLATE,
            template.getId(), userID);
        if (permissions == null)
        {
            return result;
        }
        else
        {
            for (Permission permission : permissions)
            {
                if (permission.canRead())
                {
                    canRead = true;
                    break;
                }
            }
        }

        // 查询报表
        if (template.isPureQuery())
        {
            Set<TableRule> tableRules = template.getTableRules();
            if (tableRules == null)
            {
                return null;
            }
            Vector<Object> queryFieldNames;

            // 系统变量值
            HashMap<String, Object> currentData = new HashMap<String, Object>();
            currentData.put(ReportCommonResource.SYSVAR, tableRuleM.getSysVarValue(userID));

            for (TableRule tableRule : tableRules)
            {
                if (!(tableRule instanceof ReadRule))
                {
                    return null;
                }

                Vector<Vector<Object>> queryResult = tableRuleM.execReadRule((ReadRule)tableRule,
                    currentData);
                if (queryResult == null || queryResult.isEmpty())
                {
                    return null;
                }
                rowDatas = new ArrayList<ArrayList<Object>>();
                for (Vector<Object> recordValue : queryResult)
                {
                    if (recordValue == null || recordValue.size() < 3)
                    {
                        continue;
                    }
                    // 添加字段名
                    queryFieldNames = (Vector<Object>)recordValue.get(1);
                    fieldNameList = new ArrayList<Object>();
                    fieldNameList.addAll(queryFieldNames);
                    rowDatas.add(fieldNameList);
                    // 重复表名
                    rowDatas.add(null);

                    for (int i = 2, len = recordValue.size(); i < len; i++)
                    {
                        rowData = new ArrayList<Object>();
                        rowData.add(-1);
                        rowData.add(false);
                        for (int j = 0, size = fieldNameList.size(); j < size; j++)
                        {
                            rowData.add(((Vector<Object>)recordValue.get(i)).get(j));
                        }
                        rowDatas.add(rowData);
                    }
                }

                result.setData(rowDatas);
                return result;
            }
            return null;
        }

        // 得到所有记录
        List<Record> records = db.findByExample(template.getId(), start, number);
        if (records == null || records.isEmpty())
        {
            return null;
        }
        for (Record record : records)
        {
            recordIndexs = record.getRecordIndexs();
            if (recordIndexs == null || recordIndexs.isEmpty())
            {
                continue;
            }

            // 如果不是报表设计者，则只能查看自己填报的记录
            if (record.getCreatorId() != userID && !canRead)
            {
                continue;
            }

            // 得到每条记录值
            index = 2;
            rowData = new ArrayList<Object>();
            for (RecordIndex recordIndex : recordIndexs)
            {
                rtable = recordIndex.getRtable();
                if (!rtable.isSingleTable())
                {
                    continue;
                }
                fieldNames = fieldNameMap.get(rtable.getRealName());
                if (fieldNames == null)
                {
                    continue;
                }
                if (recordIndex.getDataID() < 1)
                {
                    continue;
                }

                rowData.add(record.getId());
                rowData.add(record.getLockState() != null && record.getLockState() != 0);
                values = sqlDB.getRecord(null, rtable.getRealName(), fieldNames,
                    new long[]{recordIndex.getDataID().longValue()}, null, TableCons.DEFAULT);
                if (values == null || values.isEmpty())
                {
                    // 是否要删除无效的记录索引？
                    continue;
                }

                value = values.get(0);
                for (int i = 0; i < fieldNames.length; i++)
                {
                    rowData.add(index++, value == null ? null : value.get(i));
                }
            }
            /**
             * 有时候会拿不到记录，这时记录的数据里只有记录id和锁状态，此时就不需要返回给客户端了
             */
            if (rowData.size() > 2)
            {
                rowDatas.add(rowData);
            }
        }

        result.setData(rowDatas);
        return result;
    }

    /**
     * 得到重复记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param recordID 记录ID
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     * {
     *  {
     *    [重复表1表名]
     *    [重复表1字段1名称，重复表1字段2名称，重复表1字段3名称。。。]
     *    [记录id，字段1值，字段2值，字段3值。。。]
     *    [记录id，字段1值，字段2值，字段3值。。。]
     *    [。。。]
     *  }
     *  {
     *    [重复表2表名]
     *    [重复表2字段1名称，重复表2字段2名称，重复表2字段3名称。。。]
     *    [记录id，字段1值，字段2值，字段3值。。。]
     *    [记录id，字段1值，字段2值，字段3值。。。]
     *    [。。。]
     *  }
     *  {
     *   [。。。]
     *  }
     * }
     */
    @ SuppressWarnings({"unchecked", "rawtypes"})
    public Result getRepeatData(long userID, String nodePath, long recordID, int start, int number)
    {
        Template template = getTemplateByNodePath(nodePath);
        if (template == null)
        {
            return ErrorManager.getErrorResult("模板：" + nodePath + " 不存在");
        }
        Record record = getRecord(template.getId(), recordID);
        Set<RTable> rtables = record.getRtables();
        if (rtables == null)
        {
            return null;
        }

        int col;
        Object rfieldValue;
        Set<RField> rfields;
        RField[] tempRFields;
        ArrayList<Object> value;
        ArrayList<Object> rowData;
        ArrayList<Object> fieldNameList;
        ArrayList<ArrayList<Object>> rowDatas = new ArrayList<ArrayList<Object>>();
        for (RTable rtable : rtables)
        {
            if (!rtable.isRepeatTable() || rtable.isHide())
            {
                continue;
            }

            rfields = rtable.getRfields();
            if (rfields == null || rfields.isEmpty())
            {
                continue;
            }

            // 过滤并排序RField
            tempRFields = new RField[rfields.size()];
            for (RField rfield : rfields)
            {
                if (rfield.isHide())
                {
                    continue;
                }

                tempRFields[rfield.getPosition()] = rfield;
            }

            col = 1;
            fieldNameList = new ArrayList<Object>();
            rowData = new ArrayList<Object>();
            for (RField rfield : tempRFields)
            {
                if (rfield == null || rfield.isHide())
                {
                    continue;
                }
                fieldNameList.add(rfield.getRealName());

                rfieldValue = rfield.getValues();
                if (rfieldValue instanceof Object[])
                {
                    for (int row = 0, size = ((Object[])rfieldValue).length; row < size; row++)
                    {
                        if (row >= rowData.size() || rowData.get(row) == null)
                        {
                            value = new ArrayList<Object>();
                            value.add(0);
                            rowData.add(row, value);
                        }
                        else
                        {
                            value = (ArrayList)rowData.get(row);
                        }

                        value.add(col, rfieldValue == null ? null : ((Object[])rfieldValue)[row]);
                    }
                }
                else
                {
                    if (rowData.isEmpty())
                    {
                        value = new ArrayList<Object>();
                        value.add(0);
                        rowData.add(value);
                    }
                    else
                    {
                        value = (ArrayList)rowData.get(0);
                    }

                    value.add(col, rfieldValue);
                }
                col++;
            }

            if (fieldNameList.size() > 0)
            {
                rowData.add(0, rtable.getRealName());
                rowData.add(1, fieldNameList);
                rowDatas.add(rowData);
            }
        }

        Result result = new Result();
        result.setData(rowDatas);
        return result;
    }

    /**
     * 得到记录总数
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @return Result 结果集
     */
    @ SuppressWarnings("unchecked")
    public Result getRecordNumber(long userID, String nodePath)
    {
        // 得到模板对象
        Template template = getTemplateByNodePath(nodePath);
        if (template == null)
        {
            return ErrorManager.getErrorResult("模板：" + nodePath + " 不存在");
        }

        // 得到所有记录
        List<Record> records = db.findByExample(template.getId(), 0, Integer.MAX_VALUE);
        if (records != null && !records.isEmpty())
        {
            Result result = new Result();
            result.setData(records.size());
            return result;
        }
        return null;
    }

    /*
     * 根据节点路径得到模板对象
     */
    private Template getTemplateByNodePath(String nodePath)
    {
        String templateName = "";
        int index = nodePath.lastIndexOf("/");
        if (index > 0)
        {
            templateName = nodePath.substring(index + 1);
        }
        Template template = templateM.getTemplateByName(templateName);
        return template;
    }

    /**
     * 根据记录ID得到模板对应的记录
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Record 记录对象
     */
    public Record getRecord(long templateID, long recordID)
    {
        // 得到当前模板中引用的所有表对象
        Set<RTable> rtables = templateM.getTemplateByID(templateID).getRtables();
        if (rtables == null)
        {
            return null;
        }

        // 得到当前记录对象
        Record record = db.getRecord(templateID, recordID);
        if (record == null)
        {
            return null;
        }
        Set<RecordIndex> recordIndexs = record.getRecordIndexs();

        long[] dataIDs;
        String[] fieldNames;
        Vector<Vector<Object>> values;
        ArrayList<Long> dataIDList;
        for (RTable rtable : rtables)
        {
            dataIDList = new ArrayList<Long>();
            fieldNames = rtable.getFieldNames();
            for (RecordIndex recordIndex : recordIndexs)
            {
                if (recordIndex.getRtable().equals(rtable))
                {
                    dataIDList.add(recordIndex.getDataID());
                }
            }

            if (!dataIDList.isEmpty())
            {
                dataIDs = new long[dataIDList.size()];
                for (int i = 0; i < dataIDs.length; i++)
                {
                    dataIDs[i] = dataIDList.get(i);
                }

                rtable.setDataIDs(dataIDs);
                values = sqlDB.getRecord(null, rtable.getRealName(), fieldNames, dataIDs, null,
                    TableCons.DEFAULT);
                rtable.initRecord(fieldNames, values);
            }
        }

        record.setRtables(rtables);
        return record;
    }

    /**
     * 根据记录ID得到对应的相关记录ID(第一、前、后、最后记录ID)
     * 
     * @param templateID 模板ID
     * @param recordID 当前记录ID
     * @return long[] 结果(长度为4的long数组)
     */
    public long[] getRecordID(long templateID, long recordID)
    {
        // TODO 要根据权限进行过滤
        List<Record> records = db.getRecord(templateID);
        long[] recordIDs = new long[]{-1, -1, -1, -1};
        if (records == null || records.size() < 1)
        {
            return recordIDs;
        }
        // 第一条记录
        if (records.get(0).getId() != recordID)
        {
            recordIDs[0] = records.get(0).getId();
        }

        // 如果是空记录时进行调整
        if (recordID == -1)
        {
            recordIDs[1] = records.get(records.size() - 1).getId();
            recordIDs[3] = records.get(records.size() - 1).getId();
        }

        //上一条记录和下一条记录
        for (int i = 0; i < records.size(); i++)
        {
            if (records.get(i).getId() == recordID)
            {
                // 上一条记录
                if (i != 0)
                {
                    recordIDs[1] = records.get(i - 1).getId();
                }

                // 下一条记录
                if (i != records.size() - 1)
                {
                    recordIDs[2] = records.get(i + 1).getId();
                }
                break;
            }
        }

        // 最后一条记录
        if (recordID > 0 && records.size() > 1)
        {
            recordIDs[3] = records.get(records.size() - 1).getId();
        }
        return recordIDs;
    }

    /**
     * 删除记录
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return boolean 删除是否成功
     */
    public boolean deleteRecord(long templateID, long recordID)
    {
        Record record = db.getRecord(templateID, recordID);
        Set<RecordIndex> recordIndexs = record.getRecordIndexs();
        if (recordIndexs == null || recordIndexs.size() == 0)
        {
            return false;
        }

        for (RecordIndex recordIndex : recordIndexs)
        {
            sqlDB
                .deleteRecord(null, recordIndex.getRtable().getRealName(), recordIndex.getDataID());
        }

        super.delete(record);
        return true;
    }

    /**
     * 上锁/解锁记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @param lockStatus 锁定状态数组
     * @return Result 结果集
     */
    public Result lockRecord(long userID, long templateID, long[] recordIDs, boolean[] lockStatus)
    {
        Record record;
        for (int i = 0, len = recordIDs.length; i < len; i++)
        {
            record = db.getRecord(templateID, recordIDs[i]);
            record.setLock(manualLock, lockStatus[i]);
            db.save(record);
        }

        return new Result();
    }

    /**
     * 根据表id和数据id得到对应的记录索引对象
     * 
     * @param rtableID 表ID
     * @param id 数据id
     * @return RecordIndex 记录索引
     */
    public RecordIndex getRecordIndex(long rtableID, long id)
    {
        return db.getRecordIndex(rtableID, id);
    }

    /**
     * 根据表名和数据id删除对应的记录索引对象
     * 
     * @param rtableID 表ID
     * @param id 数据id
     * @return RecordIndex 记录索引
     */
    public void deleteRecordIndex(long rtableID, long id)
    {
        // 先删除对应的recordIndex记录
        delete(getRecordIndex(rtableID, id));
        RTable rtable = tableM.getRTable(rtableID);
        if (rtable != null)
        {
            // 再删除数据
            sqlDB.deleteRecord(null, rtable.getRealName(), id);
        }
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IRecordDB db)
    {
        this.db = db;
        setBasedb(db);
    }

    /**
     * @param sqlDB 设置 sqlDB
     */
    public void setSqlDB(ISQLDB sqlDB)
    {
        this.sqlDB = sqlDB;
    }

    /**
     * @param templateM 设置 templateM
     */
    public void setTemplateM(TemplateManager templateM)
    {
        this.templateM = templateM;
    }

    /**
     * @param tableM 设置 tableM
     */
    public void setTableM(TableManager tableM)
    {
        this.tableM = tableM;
    }

    /**
     * @param permissionM 设置 permissionM
     */
    public void setPermissionM(PermissionManager permissionM)
    {
        this.permissionM = permissionM;
    }

    /**
     * @param tableRuleM 设置 tableRuleM
     */
    public void setTableRuleM(TableRuleManager tableRuleM)
    {
        this.tableRuleM = tableRuleM;
    }
}