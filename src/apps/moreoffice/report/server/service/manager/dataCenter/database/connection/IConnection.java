package apps.moreoffice.report.server.service.manager.dataCenter.database.connection;

import java.util.Vector;

/**
 * 数据库连接接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-12-12
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IConnection
{
    /**
     * 执行创建、更新、删除语句
     * 
     * @param sql sql语句
     */
    void executeUpdate(String sql);

    /**
     * 执行查询语句
     * 
     * @param sql sql语句 
     * @return ResultSet 结果集
     */
    Vector<Vector<Object>> executeQuery(String sql);
}