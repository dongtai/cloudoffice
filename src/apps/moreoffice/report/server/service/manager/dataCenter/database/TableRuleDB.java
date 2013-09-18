package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.constants.TableRuleCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataSource;
import apps.moreoffice.report.commons.domain.databaseObject.JoinCond;
import apps.moreoffice.report.commons.domain.databaseObject.TableRule;
import apps.moreoffice.report.commons.formula.RFormula;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableRuleDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.TableRuleDAO;

/**
 * 数据库操作实现类：表间规则
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
public class TableRuleDB extends DataBase implements ITableRuleDB
{
    // dao对象
    private TableRuleDAO dao;
    // 用户表操作接口
    private ISQLDB sqlDB;

    /**
     * 根据表间规则ID得到表间规则
     * 
     * @param id 表间规则ID
     * @return TableRule 表间规则
     */
    public TableRule getTableRuleByID(long id)
    {
        return dao.getTableRuleByID(id);
    }

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
    public Vector<Vector<Object>> execReadRule(DataSource dataSource, Vector<RFormula> express,
        Set<JoinCond> joinConds, RFormula filterCond, Vector<Short> sortTypes)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");

        // 选择范围
        int size = express.size();
        String[] alias = new String[size];
        String condition;
        for (int i = 0; i < size; i++)
        {
            condition = FormulaToSQL.revertConditionToSQL(express.get(i), i == 0);
            sb.append(condition);
            sb.append(" as ");
            alias[i] = "field_alias_" + i;
            sb.append(alias[i]);
            if (i != size - 1)
            {
                sb.append(", ");
            }
        }

        // 筛选条件
        if (filterCond == null)
        {
            condition = "";
        }
        else
        {
            condition = FormulaToSQL.revertConditionToSQL(filterCond, false);
        }

        if (joinConds != null && joinConds.size() > 0)
        {
            StringBuffer condSB = new StringBuffer();
            String leftTableName, leftFieldName, conn, rightTableName, rightFieldName;
            for (JoinCond joinCond : joinConds)
            {
                leftTableName = joinCond.getLeftTable().getDtable()
                    .getRealName();
                leftFieldName = joinCond.getLeftField().getDfield()
                    .getRealName();
                conn = joinCond.getRelation();
                rightTableName = joinCond.getRightTable()
                    .getRealName();
                rightFieldName = joinCond.getRightField()
                    .getRealName();
                condSB.append(leftTableName);
                condSB.append(".");
                condSB.append(leftFieldName);
                if (conn.equals(TableRuleCons.EQUAL))
                {
                    condSB.append(conn);
                }
                else
                {
                    //TODO:若是*=或者=*，这里要大幅修改，要使用左连接和右连接，而不该放在where子句里。
                    condSB.append(TableRuleCons.EQUAL);
                }
                condSB.append(rightTableName);
                condSB.append(".");
                condSB.append(rightFieldName);
                condSB.append(" AND ");
                FormulaToSQL.addToTableNamesAndFields(leftTableName, leftFieldName);
                FormulaToSQL.addToTableNamesAndFields(rightTableName, rightFieldName);
            }
            condSB.delete(condSB.length() - 5, condSB.length());
            if (condition == null || condition.equals(""))
            {
                condition = condSB.toString();
            }
            else
            {
                condition += " AND " + condSB.toString();
            }
        }

        ArrayList<String> tableNames = FormulaToSQL.getTableNames();
        if (!(tableNames == null || tableNames.size() == 0))
        {
            sb.append(" FROM ");
            addTableNameString(sb, tableNames);
            if (condition != null && condition.length() > 0)
            {
                sb.append(" WHERE ");
                sb.append(condition);
            }
            if (sortTypes != null)
            {
                size = sortTypes.size();
                boolean needSort = false;
                for (int i = 0; i < size; i++)
                {
                    if (sortTypes.get(i) != TableCons.DEFAULT)
                    {
                        needSort = true;
                        break;
                    }
                }
                if (needSort)
                {
                    sb.append(" order by ");
                    for (int i = 0; i < size; i++)
                    {
                        short sort = sortTypes.get(i);
                        if (sort == TableCons.DEFAULT)
                        {
                            continue;
                        }
                        sb.append(alias[i]);
                        if (sort == TableCons.DESC)
                        {
                            sb.append(" DESC");
                        }
                        else
                        {
                            sb.append(" ASC");
                        }
                        if (i != size - 1)
                        {
                            sb.append(", ");
                        }
                    }
                }
            }
        }
        sb.append(";");
        return sqlDB.executeQuery(dataSource, sb.toString());
    }

    /**
     * 修改
     * 
     * @param tableName 表名
     * @param fieldNames 字段名列表
     * @param express 字段值
     * @param filterCond 筛选条件
     */
    public void execModifyRule(String tableName, String[] fieldNames, RFormula[] express,
        RFormula filterCond)
    {
        // 筛选条件
        String condition = FormulaToSQL.revertConditionToSQL(filterCond, true);
        if (condition == null)
        {
            return;
        }

        // 修改表达式
        int expressLen = express.length;
        String[] values = new String[expressLen];
        String temp;
        for (int i = 0; i < expressLen; i++)
        {
            temp = FormulaToSQL.revertConditionToSQL(express[i], false);
            if (temp == null)
            {
                return;
            }
            values[i] = "(" + temp + ")";
            FormulaToSQL.addToTableNamesAndFields(tableName, fieldNames[i]);
        }

        // 表名列表
        ArrayList<String> tableNames = FormulaToSQL.getTableNames();
        if (tableNames == null || tableNames.size() == 0)
        {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("update ");
        addTableNameString(sb, tableNames);
        sb.append(" set ");
        for (int i = 0; i < expressLen; i++)
        {
            sb.append(tableName);
            sb.append(".");
            sb.append(fieldNames[i]);
            sb.append(" = ");
            sb.append(values[i]);
            if (i != expressLen - 1)
            {
                sb.append(", ");
            }
        }
        if (condition != null && condition.length() > 0)
        {
            sb.append(" WHERE ");
            sb.append(condition);
        }
        sb.append(";");
        sqlDB.executeUpdate(null, sb.toString());
    }

    /*
     * 
     */
    private void addTableNameString(StringBuffer sb, ArrayList<String> tableNames)
    {
        int len = tableNames.size();
        for (int i = 0; i < len; i++)
        {
            sb.append(tableNames.get(i));
            if (i != len - 1)
            {
                sb.append(", ");
            }
        }
    }

    /**
     * 插入记录
     * 
     * @param tableName 表名
     * @param fields 字段列表
     * @return values 值列表
     * @param long 记录ID
     */
    public long insertRecord(String tableName, String[] fields, Object[] values)
    {
        return sqlDB.insertRecord(null, tableName, fields, values);
    }

    /**
     * 公式计算
     * 
     * @param formula 公式对象
     * @return Object 结果
     */
    public Object calculatorFormula(RFormula formula)
    {
        String sql = FormulaToSQL.revertConditionToSQL(formula, true);
        return sqlDB.executeQuery(null, FormulaToSQL.dealWithCondition(sql));
    }

    /**
     * 通过公式查询符合条件的指定数据表中的ID
     * 
     * @param tableNames 表名数组
     * @param formula 公式对象
     * @return Vector<Vector<Object>> 结果
     */
    public Vector<Vector<Object>> queryDataIDByFormula(String[] tableNames, RFormula formula)
    {
        String filterCond = FormulaToSQL.revertConditionToSQL(formula, true);
        // 暂时可能不需要，理论上formula会解析到相关的表，如果没有解析到，说明不存在条件，即全部符合。
        //        for (String tableName : tableNames)
        //        {
        //            FormulaToSQL.addToTableNamesAndFields(tableName, "id");
        //        }
        ArrayList<String> tableNameList = FormulaToSQL.getTableNames();

        StringBuffer sb = new StringBuffer();
        int tnLen = tableNameList.size();
        sb.append("SELECT ");
        for (int i = 0; i < tnLen; i++)
        {
            sb.append(tableNameList.get(i));
            sb.append(".");
            sb.append("id");
            if (i != tnLen - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(" FROM ");
        addTableNameString(sb, tableNameList);
        if (filterCond != null)
        {
            sb.append(" WHERE ");
            sb.append(filterCond);
        }
        sb.append(";");
        Vector<Vector<Object>> ids = sqlDB.executeQuery(null, sb.toString());
        Vector<Object> temp = new Vector<Object>(tnLen);
        for (int i = 0; i < tnLen; i++)
        {
            temp.add(tableNameList.get(i));
        }
        ids.add(0, temp);

        return ids;
    }

    /**
     * 优化
     */
    public void optimization()
    {
        dao.optimization();
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(TableRuleDAO dao)
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