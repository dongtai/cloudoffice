package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.Template;

/**
 * 数据库操作接口：表和字段
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
public interface ITableDB extends IDataBase
{
    /**
     * 存盘
     * 
     * @param template 模板对象
     */
    void saveTemplate(Template template);

    /**
     * 得到所有的用户数据表
     * 
     * @return ArrayList 用户数据表
     */
    ArrayList<DataTable> getDataTables();

    /**
     * 得到外部数据源对应的RTable列表
     * 
     * @return ArrayList<RTable> 外部数据源对应的RTable列表
     */
    ArrayList<RTable> getOtherRTables();

    /**
     * 检查表名是否合法
     * 
     * @param tableName 表名
     * @return boolean 是否合法
     */
    boolean checkTableName(String tableName);

    /**
     * 根据RTableID得到RTable对象
     * 
     * @param rtableID rtableID
     * @return RTable RTable对象
     */
    RTable getRTableByID(long rtableID);

    /**
     * 优化
     */
    void optimization();
}