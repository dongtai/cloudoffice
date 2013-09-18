package apps.moreoffice.report.client.constants;

/**
 * 报表树常量
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
public interface ReportTreeCons
{
    // ----------节点类型常量----------
    // 模板节点
    public final static int TEMPLATE_NODE = 0;
    // 记录节点
    public final static int RECORD_NODE = TEMPLATE_NODE + 1;
    // 数据类型节点
    public final static int DATATYPE_NODE = RECORD_NODE + 1;
    // 数据规范节点
    public final static int DATARULE_NODE = DATATYPE_NODE + 1;
    // 系统变量节点
    public final static int SYSTEMVAR_NODE = DATARULE_NODE * 10 + 1;
    // 自动编号节点
    public final static int AUTONUM_NODE = DATARULE_NODE * 10 + 2;
    // 下拉列表节点
    public final static int DOWNLIST_NODE = DATARULE_NODE * 10 + 3;
    // 树型选择节点
    public final static int TREESELECT_NODE = DATARULE_NODE * 10 + 4;
    // 列表选择节点
    public final static int LISTSELECT_NODE = DATARULE_NODE * 10 + 5;
}