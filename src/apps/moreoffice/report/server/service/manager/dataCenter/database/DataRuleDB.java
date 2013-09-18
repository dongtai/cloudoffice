package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.List;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.ListSelectRule;
import apps.moreoffice.report.commons.domain.databaseObject.TreeSelectRule;
import apps.moreoffice.report.commons.formula.RFormula;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataRuleDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.DataRuleDAO;

/**
 * 数据库操作实现类：数据规范
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
public class DataRuleDB extends DataBase implements IDataRuleDB
{
    // dao对象
    private DataRuleDAO dao;
    // 用户表操作接口
    private ISQLDB sqlDB;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        entity = super.save(entity);

        // 优化
        if (entity instanceof ListSelectRule)
        {
            dao.optimizationL();
        }
        else if (entity instanceof TreeSelectRule)
        {
            dao.optimizationT();
        }

        return entity;
    }

    /**
     * 得到数据规范列表
     * 
     * @param type 类型
     * @return List DataRule列表
     */
    @ SuppressWarnings("rawtypes")
    public List getDataRuleList(short type)
    {
        if (type == DataRuleCons.ALLTYPE)
        {
            return dao.getAllDataRule();
        }
        else
        {
            return dao.getDataRuleByType(type);
        }
    }

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param dataRuleID 数据规范ID
     * @return DataRule 数据规范
     */
    public DataRule getDataRuleByID(long dataRuleID)
    {
        return dao.getDataRuleByID(dataRuleID);
    }

    /**
     * 执行下拉列表数据规范
     * 
     * @param dtable 数据表
     * @param dfield 值字段
     * @param sortfield 排序字段
     * @param formula 筛选条件
     * @param sort 是否升序
     * @return Result 执行结果
     */
    public Vector<Vector<Object>> execDownListRule(DataTable dtable, DataField dfield,
        DataField sortfield, RFormula formula, boolean sort)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct ");
        sb.append("model.");
        sb.append(dfield.getRealName());
        sb.append(" ");
        sb.append(" FROM ");
        sb.append(dtable.getRealName());
        sb.append(" as model");
        if (formula != null)
        {
            sb.append(" WHERE ");
            sb.append(FormulaToSQL.revertConditionToSQL(formula, true));
        }
        if (sortfield != null)
        {
            sb.append(" order by model.");
            sb.append(sortfield.getRealName());
            sb.append(" ");
            if (sort)
            {
                sb.append("ASC");
            }
            else
            {
                sb.append("DESC");
            }
        }
        return sqlDB.executeQuery(dtable.getDataSource(), sb.toString());
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(DataRuleDAO dao)
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