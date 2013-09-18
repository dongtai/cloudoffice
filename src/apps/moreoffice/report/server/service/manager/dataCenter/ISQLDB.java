package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.List;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.databaseObject.DataSource;

/**
 * 用户表操作接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ISQLDB
{
    /**
     * 得到外部数据表的表字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @return Vector<Vector<Object>> 表名列表
     */
    Vector<Vector<Object>> getOtherTableFields(DataSource dataSource, String tableName);

    /**
     * 创建用户表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param tableName 表名
     * @param field 字段列表，Object[]格式如下：
                Object[0]: 字段名(String)
                Object[1]: 字段类型(short)
                Object[2]: 是否是无符号数字(boolean),若是数值类型的字段，且要无符号的，则为true，否则为false
                Object[3]: 是否要非空(boolean), 若需要此字段非空，则为true，否则为false
                Object[4]: 默认值(String), 若有默认值，则为一个字符串，否则则设为null
     */
    void createUserTable(DataSource dataSource, String tableName, List<Object[]> fields);

    /**
     * 判断用户表是否是空表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @return boolean 是否是空表
     */
    boolean isNullTable(DataSource dataSource, String tableName);

    /**
     * 删除用户表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     */
    void deleteUserTable(DataSource dataSource, String tableName);

    /**
     * 向已有表中添加字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表(见创建表字段格式)
     */
    void insertField(DataSource dataSource, String tableName, List<Object[]> fields);

    /**
     * 修改字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表(见创建表字段格式)
     */
    void modifyField(DataSource dataSource, String tableName, List<Object[]> fields);

    /**
     * 删除字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fieldName 字段名
     */
    void deleteField(DataSource dataSource, String tableName, String fieldName);

    /**
     * 插入记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表
     * @param values 值列表
     * @return long 记录ID
     */
    long insertRecord(DataSource dataSource, String tableName, String[] fields, Object[] values);

    /**
     * 修改记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param id 记录ID
     * @param fields 字段列表
     * @param values 值列表
     */
    void modifyRecord(DataSource dataSource, String tableName, long id, String[] fields,
        Object[] values);

    /**
     * 得到记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param ids 记录ID数组
     * @param fieldNames 字段名列表
     * @param sortFieldName 排序字段
     * @param sortType 排序类型
     * @return Vector<Vector<Object>> 记录值
     */
    Vector<Vector<Object>> getRecord(DataSource dataSource, String tableName, String[] fieldNames,
        long[] ids, String sortFieldName, byte sortType);

    /**
     * 删除记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param id 记录id
     */
    void deleteRecord(DataSource dataSource, String tableName, long id);

    /**
     * 执行创建、更新、删除语句
     * 
     * @param dataSource 数据源
     * @param sql sql语句
     */
    void executeUpdate(DataSource dataSource, String sql);

    /**
     * 执行查询语句
     * 
     * @param dataSource 数据源
     * @param sql sql语句 
     * @return ResultSet 结果集
     */
    Vector<Vector<Object>> executeQuery(DataSource dataSource, String sql);
}