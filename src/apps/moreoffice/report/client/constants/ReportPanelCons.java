package apps.moreoffice.report.client.constants;

/**
 * 报表面板常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-10-12
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ReportPanelCons
{
    // 面板显示数据默认起始值
    int MIN_DATA_NUM = 0;
    // 面板显示数据默认显示数
    int MAX_DATA_NUM = Integer.MAX_VALUE;
    // style类型
    String TEXT_STYLE_NAME = "gwt-Label2";
    // 默认每页记录数
    int DEFAULT_RECORD_NUMBER = 20;

    // ----------列头字段----------
    String NAME = "name";
    String CREATOR = "creator";
    String modifier = "modifier";
    String LASTMODIFYDATE = "lastModifyDate";
    String ID = "hide_id";
    String PATH = "hide_path";
    // 模板面板列头字段
    String STATUS = "status";
    String[] TEMPLATE_COLUMN_FIELDS = new String[]{NAME, "sort", STATUS, CREATOR, modifier,
        LASTMODIFYDATE, ID, PATH};
    // 记录面板列头字段
    String LOCKSTATUS = "lockStatus";
    // 数据类型面板列头字段
    String[] DATATYPE_COLUMN_FIELDS = new String[]{NAME, "basicType", "limitLength",
        "decimalDigit", CREATOR, modifier, LASTMODIFYDATE, ID};
    // 数据规范面板列头字段
    String RULETYPENAME = "ruleTypeName";
    String RULETYPE = "hide_ruleType";
    String[] DATARULE_COLUMN_FIELDS = new String[]{NAME, RULETYPENAME, "source", "systemDefined",
        CREATOR, modifier, LASTMODIFYDATE, ID, RULETYPE};
    // 系统变量面板列头字段
    String[] SYSTEMVAR_COLUMN_FIELDS = new String[]{NAME, RULETYPENAME, "systemDefined", CREATOR,
        ID};
    // 自动编号面板列头字段
    String[] AUTONUM_COLUMN_FIELDS = new String[]{NAME, "format", CREATOR, modifier,
        LASTMODIFYDATE, ID};
    // 其他规则面板列头字段
    String[] OTHERRULE_COLUMN_FIELDS = new String[]{NAME, RULETYPENAME, "source", CREATOR,
        modifier, LASTMODIFYDATE, ID};
}