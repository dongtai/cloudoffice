package apps.moreoffice.report.client.container;

/**
 * 报表容器接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-9-3
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IReportContainer
{
    /**
     * 更新容器数据
     * 
     * @param type 类型
     * @param start 开始位置
     * @param number 数量 
     */
    void updateGridData(int start, int number);
}