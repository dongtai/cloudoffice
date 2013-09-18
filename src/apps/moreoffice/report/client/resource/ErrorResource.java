package apps.moreoffice.report.client.resource;

import apps.moreoffice.report.client.constants.DialogCons;

/**
 * 错误信息资源
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-10-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ErrorResource
{
    // 错误提示
    String ERROR_TITLE = "永中Office";

    String ERROR_1 = "确定要删除[";
    String ERROR_1_1 = "]吗?";
    String ERROR_2 = "名称长度不能超过" + DialogCons.NAME_LIMIT_LEN;
    String ERROR_3 = "未保存成功";
    String ERROR_4 = "根路径初始化失败";
    String ERROR_5 = "弹右键菜单时刷新当前节点失败";
    String ERROR_6 = "确定要删除所选内容吗?";
    String ERROR_7 = "分页值输入值无效，请输入数字";
    String ERROR_8 = "页码值输入值无效，请输入数字";
    String ERROR_9 = "名称不能为空";
    String ERROR_10 = "位数应大于0并且小于20";
    String ERROR_11 = "位数必须是数字";
    
    // 模板
    String TEMPLATE_ERROR_1 = "请选择模板路径";

    // 数据类型
    String DATATYPE_ERROR_1 = "请输入数据类型名称";
    String DATATYPE_ERROR_2 = "限定长度应大于0并且小于";
    String DATATYPE_ERROR_3 = "限定长度必须是数字";

    // 自动编号
    String AUTONUM_ERROR_1 = "请输入自动编号名称";
    String AUTONUM_ERROR_2 = "请添加编号组成字段";
    String AUTONUM_ERROR_5 = "不存在顺序号位数";
    String AUTONUM_ERROR_6 = "存在多于一个顺序号位数";

    // 下拉列表
    String DOWNLIST_ERROR_1 = "请输入下拉列表规范名称";
    String DOWNLIST_ERROR_2 = "请先选择数据表";
    String DOWNLIST_ERROR_3 = "请选择取值字段";
    
    // 树型选择
    String TREESELECT_ERROR_1 = "节点名称不能为空";
}