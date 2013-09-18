package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.List;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.formula.RFormula;

/**
 * 数据库操作接口：数据规范
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
public interface IDataRuleDB extends IDataBase
{
    /**
     * 得到数据规范列表
     * 
     * @param type 类型
     * @return List DataRule列表
     */
    @ SuppressWarnings("rawtypes")
    List getDataRuleList(short type);

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param dataRuleID 数据规范ID
     * @return DataRule 数据规范
     */
    DataRule getDataRuleByID(long dataRuleID);

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
    Vector<Vector<Object>> execDownListRule(DataTable dtable, DataField dfield,
        DataField sortfield, RFormula formula, boolean sort);
}