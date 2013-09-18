package apps.moreoffice.report.server.resource;

import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 模板分类资源
 * 
 * <p>
 * <p>
 * <p>
 */
public class TemplateSortResource
{
    // 报表根节点
    public static final String REPORTROOTNODE = "myReport";
    // 模板路径
    public static final String TEMPLATEPATH = REPORTROOTNODE + "/" + "Document";
    // 数据类型路径
    public static final String DATATYPEPATH = REPORTROOTNODE + "/" + "dataType";
    // 数据规范路径
    public static final String DATARULEPATH = REPORTROOTNODE + "/" + "dataRule";
    // 外部数据源路径
    public static final String EXTERNALPATH = REPORTROOTNODE + "/" + "externalDataSource";
    // 系统变量路径
    public static final String SYSVARPATH = DATARULEPATH + "/" + ReportCommonResource.SYSVAR;
    // 自动编号路径
    public static final String AUTONUMPATH = DATARULEPATH + "/" + ReportCommonResource.AUTONUM;
    // 下拉列表路径
    public static final String DOWNLISTPATH = DATARULEPATH + "/" + ReportCommonResource.DOWNLIST;
    // 树型选择路径
    public static final String TREESELECTPATH = DATARULEPATH + "/"
        + ReportCommonResource.TREESELECT;
    // 列表选择路径
    public static final String LISTSELECTPATH = DATARULEPATH + "/"
        + ReportCommonResource.LISTSELECT;

    // 流程根节点
    public static final String WORKFLOWROOTNODE = "myWorkFlow";
    // 流程路径
    public static final String WORKFLOWPATH = WORKFLOWROOTNODE + "/" + "Document";
    public static final String BASEFLOW = "基础流程";
    public static final String BUSINESSFLOW = "业务流程";

    public static final String TEMPLATE = "数据中心";
    public static final String BASEDATA = "基础数据";
    public static final String BUSINESSDATA = "业务单据";
    public static final String ANALYSISDATA = "查询统计";
    public static final String EXTERNALDATASOURCE = "外部数据源";
}