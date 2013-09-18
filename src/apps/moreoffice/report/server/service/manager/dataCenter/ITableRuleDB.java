package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.databaseObject.DataSource;
import apps.moreoffice.report.commons.domain.databaseObject.JoinCond;
import apps.moreoffice.report.commons.domain.databaseObject.TableRule;
import apps.moreoffice.report.commons.formula.RFormula;

/**
 * 数据库操作接口：表间规则
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
public interface ITableRuleDB extends IDataBase
{
    /**
     * 根据表间规则ID得到表间规则
     * 
     * @param id 表间规则ID
     * @return TableRule 表间规则
     */
    TableRule getTableRuleByID(long id);

    /**
     * 提数
     * 
     * @param dataSource 数据源
     * @param express 需要提数的表达式
     * @param joinConds 关联条件
     * @param filterCond 筛选条件
     * @param sortTypes 排序数组
     * @return Vector<Vector<Object>> 提数结果
     */
    Vector<Vector<Object>> execReadRule(DataSource dataSource, Vector<RFormula> express,
        Set<JoinCond> joinConds, RFormula filterCond, Vector<Short> sortTypes);

    /**
     * 修改
     * 
     * @param tableName 表名
     * @param fieldNames 字段名列表
     * @param express 字段值
     * @param filterCond 筛选条件
     */
    void execModifyRule(String tableName, String[] fieldNames, RFormula[] express,
        RFormula filterCond);

    /**
     * 插入记录
     * 
     * @param tableName 表名
     * @param fields 字段列表
     * @param values 值列表
     * @return long 记录ID
     */
    long insertRecord(String tableName, String[] fields, Object[] values);

    /**
     * 公式计算
     * 
     * @param formula 公式对象
     * @return Object 结果
     */
    Object calculatorFormula(RFormula formula);

    /**
     * 通过公式查询符合条件的制定数据表中的ID
     * 
     * @param tableNames 表名数组
     * @param formula 公式对象
     * @return Vector<Vector<Object>> 结果
     */
    Vector<Vector<Object>> queryDataIDByFormula(String[] tableNames, RFormula formula);

    /**
     * 优化
     */
    void optimization();
}