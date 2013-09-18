package apps.moreoffice.report.client.resource;

import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 对话盒资源
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
public interface DialogResource
{
    // 公共
    String OK = "确定";
    String CANCEL = "取消";
    String DELETE = "删除";
    String ADD = "添加";
    String UP = "上移";
    String DOWN = "下移";
    String ALLOWOTHERSMODIFY = "允许其他设计者修改删除";
    String CANEDIT = "填报时允许手工输入";
    String MODIFY = "修改";

    // 分类对话盒
    String CREATE_FOLDER = "创建分类";
    String RENAME_FOLDER = "重命名分类";
    String FOLDER_NAME = "分类名称";

    // 数据类型对话盒
    String DATATYPE_TITLE = "自定义数据类型";
    String DATATYPE_NAME = "类型名称";
    String BASETYPE = "基础类型";
    String NUMBERLENGTH = "位数";
    String LIMITLENGTH = "限定长度";
    String DECIMALDIGIT = "小数位数";
    String MAXLENGTH = "最大限定长度为";
    String DESCRIPTION = "说明";

    // 数据规范对话盒
    String DATARULE_TYPE = "规范方式";

    // 自动编号对话盒
    String AUTONUM_TITLE = "定义自动编号";
    String AUTONUM_NAME = "编号名称";
    String AUTONUM_MEM = "编号组成";
    String AUTONUM_FORMAT = "编号格式";
    String AUTONUM_SAMPLE = "样例";
    String AUTONUM_CREATEATSAVE = "保存报表时才产生";
    String[] AUTONUM_COLUMN = new String[]{"序号", "组成类别", "选项"};
    String[] AUTONUM_DATA = new String[]{ReportCommonResource.FIXEDWORDS,
        ReportCommonResource.SYSVAR, ReportCommonResource.DATEVAR, ReportCommonResource.NUMBERS};
    String[] AUTONUM_SYSTEMVAR = new String[]{ReportCommonResource.CURRENTUSERNAME,
        ReportCommonResource.CURRENTLOGINNAME, ReportCommonResource.CURRENTORGUSER,
        ReportCommonResource.CURRENTDATE, ReportCommonResource.CURRENTDATETIME,};

    // 下拉列表对话盒
    String DOWNLIST_TITLE = "定义下拉列表规范";
    String DOWNLIST_NAME = "下拉列表名称";
    String DATASOURCE = "数据来源";
    String FIXEDVALUE = "固定取值";
    String FROMTABLE = "来自数据表";
    String NODENAME = "节点名称";
    String DOWNVALUELIST = "下拉取值列表";
    String SORTFIELD = "排序字段";
    String SORTMODE = "排序方式";

    // 树型选择对话盒
    String TREESELECT_TITLE = "定义树型选择规范";
    String TREESELECT_NAME = "树型选择名称";
    String TREESELECT_NODE = "树型节点";
    String TREESELECT_ADDNODE = "增加节点";
    String TREESELECT_ADDCHILD = "细分";
    String TREESELECT_NODEOPERATE_TITLE = "请输入节点内容";
    String TREESELECT_FORMAT = "选择构造方式";
    String TREESELECT_MULTIFIELD = "多字段分级";
    String TREESELECT_FIELDLENGTH = "编码长度分级";
    String TREESELECT_PROPAGATE = "自我繁殖";
    String TREESELECT_LEVELFIELD = "各级字段";
    String[] TREESELECT_COLUMN = new String[]{"级数", "分级字段", "排序"};

    // 列表选择对话盒
    String LISTSELECT_TITLE = "定义列表选择规范";
    String LISTSELECT_NAME = "列表选择名称";
    String LISTSELECT_LIST = "列表";
    String LISTSELECT_ONESELFSORT = "自身分类";
    String LISTSELECT_NOSORT = "无分类";
    String LISTSELECT_RELATIONSORT = "关联分类";
    String[] LISTSELECT_ONESELF_COLUMN = new String[]{"分类", "字段名", "排序", "隐藏", "返回项", "显示标题",
        "统计显示"};
}