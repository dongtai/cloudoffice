package apps.moreoffice.report.commons.domain.constants;

/**
 * 数据规范常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-9
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface DataRuleCons
{
    // 所有
    public static final short ALLTYPE = 0;
    // 系统变量
    public static final short SYSVAR = 1;
    // 自动编号
    public static final short AUTONUM = 2;
    // 下拉列表
    public static final short DOWNLIST = 3;
    // 树型选择
    public static final short TREESELECT = 4;
    // 列表选择
    public static final short LISTSELECT = 5;

    // 数据源类型
    // 固定取值
    public final static short DATASOURCE_FIXED = 0;
    // 来自数据库
    public final static short DATASOURCE_DB = 1;

    // 构造方式
    // 多字段分级
    public final static short FORMAT_MULTIFIELD = 0;
    // 编码长度分级
    public final static short FORMAT_FIELDLENGTH = 1;
    // 自我繁殖
    public final static short FORMAT_PROPAGATE = 2;

    // 数据规范对象的attr属性
    // 是否可编辑
    public static final int CANEDIT = 0;
    // 是否只能选择最底层节点
    public static final int ONLYSELECTBOTTOM = 1;
    // 是否能多选
    public static final int MULTSELECT = 2;
    // 保存时产生
    public static final int CREATEATSAVE = 3;
    // 排序
    public static final int SORT = 4;

    // ListSelectItem的attr属性
    // 分类
    public static final int LISTSELECTITEM_SORT = 0;
    // 隐藏
    public static final int LISTSELECTITEM_HIDE = 1;
    // 返回项
    public static final int LISTSELECTITEM_RETURN = 2;
    // 统计显示
    public static final int LISTSELECTITEM_STATISTICS = 3;
}