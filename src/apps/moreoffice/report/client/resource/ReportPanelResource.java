package apps.moreoffice.report.client.resource;

import apps.moreoffice.report.client.constants.ReportPanelCons;

/**
 * 报表面板资源
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
public interface ReportPanelResource
{
    // ----------右键资源----------
    String NEW = "新建";
    String RENAME = "重命名";
    String DELETE = "删除";
    String PROPERTY = "属性";

    // ----------工具条资源----------
    // 模板工具条资源
    String NEW_TEMPLATE = "新建模板";
    String START = "启用";
    String STOP = "停用";
    String MODIFY = "修改";
    String IMPORT = "导入";
    String FILL = "填报";
    // 记录工具条资源
    String VIEW = "查看";
    String LOCK = "上锁";
    String UNLOCK = "解锁";
    String GONG = "共";
    String TIAOJILU = "条记录";
    String FIRSTPAGE = "首页";
    String UPPAGE = "上页";
    String NEXTPAGE = "下页";
    String ENDPAGE = "尾页";
    String YECI = "页次：";
    String TONUM = "到第";
    String PAGE = "页";
    String CUTPAGE = "分页";
    String MEIYE = "每页：";
    String SELECTALL = "全选";

    String LOCKING = "锁定";
    String NOLOCK = "无锁";

    // ----------面板列头资源----------
    String CREATOR = "创建者";
    String MODIFIER = "修改者";
    String LASTMODIFYDATE = "最后修改时间";
    // 模板面板列头名称
    String[] TEMPLATE_COLUMN_NAMES = new String[]{"模板名称", "模板分类", "使用状态", CREATOR, MODIFIER,
        LASTMODIFYDATE, ReportPanelCons.ID, ReportPanelCons.PATH};
    // 记录面板列头名称
    String LOCKSTATUS = "锁状态";
    // 数据类型面板列头名称
    String[] DATATYPE_COLUMN_NAMES = new String[]{"数据类型名称", "基础类型", "长度", "小数位数", CREATOR,
        MODIFIER, LASTMODIFYDATE, ReportPanelCons.ID};
    // 数据规范面板列头名称
    String[] DATARULE_COLUMN_NAMES = new String[]{"数据规范名称", "规范方式", "来自数据表", "备注", CREATOR,
        MODIFIER, LASTMODIFYDATE, ReportPanelCons.ID, ReportPanelCons.RULETYPE};
    // 系统变量面板列头名称
    String[] SYSTEMVAR_COLUMN_NAMES = new String[]{"数据规范名称", "规范方式", "备注", CREATOR,
        ReportPanelCons.ID};
    // 自动编号面板列头名称
    String[] AUTONUM_COLUMN_NAMES = new String[]{"自动编号", "编号格式", CREATOR, MODIFIER, LASTMODIFYDATE,
        ReportPanelCons.ID};
    // 其他面板列头名称
    String[] OTHERRULE_COLUMN_NAMES = new String[]{"数据规范名称", "规范方式", "来自数据表", CREATOR, MODIFIER,
        LASTMODIFYDATE, ReportPanelCons.ID};
}