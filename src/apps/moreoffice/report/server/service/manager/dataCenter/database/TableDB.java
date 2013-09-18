package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.RField;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.TableDAO;

/**
 * 数据库操作实现类：表和字段
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TableDB extends DataBase implements ITableDB
{
    // dao对象
    private TableDAO dao;
    // 用户表操作接口
    private ISQLDB sqlDB;

    /**
     * 存盘
     * 
     * @param template 模板对象
     */
    @ SuppressWarnings("rawtypes")
    public void saveTemplate(Template template)
    {
        // 创建用户表
        // 得到RTable集合
        Set<RTable> rtables = template.getRtables();
        if (rtables != null && rtables.size() > 0)
        {
            short relation;
            String tableName;
            short createState;
            Set<RField> rfields;
            List oldrfields = null;
            for (RTable rtable : rtables)
            {
                relation = rtable.getRelation();
                tableName = rtable.getRealName();
                createState = rtable.getDtable().getCreateState();
                // 如果不需要创建,则继续
                if (createState == TableCons.NOCREATE)
                {
                    continue;
                }
                else if (createState == TableCons.HASCREATED)
                {
                    // 如果当前rtable已经创建，则得到数据库中的该RTable对应的字段，如果不在保存范围内，就需要删除
                    oldrfields = dao.getRFieldByRTableID(rtable.getId());
                }

                // 如果是对应到已有数据表，则继续
                if (relation == TableCons.MAPTOTABLE)
                {
                    continue;
                }

                // 如果没有字段，则继续
                rfields = rtable.getRfields();
                if (rfields == null || rfields.size() < 1)
                {
                    continue;
                }

                // 得到所有字段列表
                Object[] fieldObj;
                ArrayList<Object[]> fields = new ArrayList<Object[]>();
                for (RField field : rfields)
                {
                    fieldObj = new Object[5];
                    // 字段名
                    fieldObj[0] = field.getRealName();
                    // 字段类型
                    fieldObj[1] = field.getDataType();
                    // 是否是无符号数组
                    fieldObj[2] = false;
                    // 是否非空
                    fieldObj[3] = field.getDfield().getNotNull();

                    fields.add(fieldObj);
                }

                if (relation == TableCons.CREATETABLE && createState == TableCons.NEEDCREATE)
                {
                    // 如果是新创建的表
                    sqlDB.createUserTable(null, tableName, fields);
                    // 设置表的创建标记为已经创建
                    rtable.getDtable().setCreateState(TableCons.HASCREATED);
                }
                else if (relation == TableCons.ADDTOTABLE)
                {
                    // 如果是添加到已有数据表
                    sqlDB.insertField(null, tableName, fields);
                }

                // 如果已经创建，并且表中没有记录，则修改字段属性
                if (createState == TableCons.HASCREATED && sqlDB.isNullTable(null, tableName))
                {
                    /**
                     * 对多余字段进行删除(多余的表在优化处理方法里删除了)
                     * 为什么没有放在优化里处理，原因是哪个时候不知道当前字段属于哪个表了
                     * 所以没法从用户表里删除对应的字段
                     */
                    if (oldrfields != null)
                    {
                        boolean exist;
                        RField oldrfield;
                        for (int i = 0, size = oldrfields.size(); i < size; i++)
                        {
                            exist = false;
                            oldrfield = (RField)oldrfields.get(i);
                            for (RField field : rfields)
                            {
                                // 如果没有删除，则继续
                                if (field.getId() == oldrfield.getId())
                                {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist)
                            {
                                // 如果在新的字段中没有找到对应的字段，则删除
                                deleteField(oldrfield.getRtable().getDtable(), oldrfield);
                            }
                        }
                    }

                    sqlDB.modifyField(null, tableName, fields);
                }
            }
        }
    }

    /**
     * 删除实体对象
     * (删除模板时，需要调用此方法，删除对应的无用数据表)
     * 
     * @param template 模板对象
     */
    public void delete(DataBaseObject entity)
    {
        if (entity == null || !(entity instanceof Template))
        {
            return;
        }

        Set<RTable> rtables = ((Template)entity).getRtables();
        if (rtables == null || rtables.size() < 1)
        {
            return;
        }

        // 如果对应的表中没有任何记录，则删除
        for (RTable rtable : rtables)
        {
            delete(rtable);
        }
    }

    /*
     * 删除RTable对象
     */
    private void delete(RTable rtable)
    {
        String tableName = rtable.getRealName();
        if (sqlDB.isNullTable(rtable.getDtable().getDataSource(), tableName))
        {
            sqlDB.deleteUserTable(null, tableName);
        }
    }

    /**
     * 得到所有的已创建的用户数据表
     * 
     * @return ArrayList 已创建的用户数据表
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<DataTable> getDataTables()
    {
        return (ArrayList<DataTable>)dao.getDataTables();
    }

    /**
     * 得到外部数据源对应的RTable列表
     * 
     * @return ArrayList<RTable> 外部数据源对应的RTable列表
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<RTable> getOtherRTables()
    {
        return (ArrayList<RTable>)dao.getOtherRTables();
    }

    /**
     * 检查表名是否合法
     * 
     * @param tableName 表名
     * @return Result 结果
     */
    public boolean checkTableName(String tableName)
    {
        Result result = new Result();
        result.setData(true);
        // 先检查DataTable
        boolean valid = dao.checkTableName(tableName);
        if (!valid)
        {
            return false;
        }
        return true;
    }

    /**
     * 根据RTableID得到RTable对象
     * 
     * @param rtableID rtableID
     * @return RTable RTable对象
     */
    public RTable getRTableByID(long rtableID)
    {
        return dao.getRTableByID(rtableID);
    }

    /**
     * 优化
     */
    public void optimization()
    {
        dao.optimization();
    }

    /*
     * 删除无用的RField
     */
    private void deleteField(DataTable dtable, RField rfield)
    {
        super.delete(rfield);
        if (sqlDB.isNullTable(dtable.getDataSource(), dtable.getRealName()))
        {
            sqlDB.deleteField(dtable.getDataSource(), dtable.getRealName(), rfield.getRealName());
        }
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(TableDAO dao)
    {
        this.dao = dao;
        setBaseDao(dao);
    }

    /**
     * @param sqlDB 设置 sqlDB
     */
    public void setSqlDB(ISQLDB sqlDB)
    {
        this.sqlDB = sqlDB;
    }
}