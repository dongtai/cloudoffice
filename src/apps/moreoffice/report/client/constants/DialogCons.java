package apps.moreoffice.report.client.constants;

import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 对话盒常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-9-4
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface DialogCons
{
    // 名称限长
    public final static int NAME_LIMIT_LEN = 64;
    // 文本限长
    public final static int TEXT_LIMIT_LEN = 4000;
    // 日期类型
    public final static String[] DATE_STRUCT = new String[]{"YYMM", "YYYYMM", "YYMMDD", "YYYYMMDD",
        "YY", "YYYY", "MMDD"};

    public final static String DLG_STYLE_LABEL = "font-size:12px;font-family:宋体";

    // TODO
    // 基本类型列表
    public final static String[] BASICTYPES = new String[]{ReportCommonResource.TEXT,
        ReportCommonResource.NUMBER, ReportCommonResource.DATE};
//    public final static String[] BASICTYPES = new String[]{ReportCommonResource.TEXT,
//        ReportCommonResource.NUMBER, ReportCommonResource.DATE, ReportCommonResource.PICTURE,
//        ReportCommonResource.FILE};
}